# Diseño técnico — Módulo 7. Tarjetas

## 1. Propósito y alcance

El módulo administra tarjetas de débito y crédito, autorizaciones, presentaciones, deuda, pagos y disputas.

Comprende:

- Emisión, entrega y activación.

- Habilitaciones, bloqueos y reemplazos.

- Límites de uso y capacidad crediticia.

- Autorizaciones y compromisos.

- Presentaciones y devoluciones.

- Cuotas y planes financiados.

- Resúmenes, pagos y financiación rotativa.

- Disputas y contracargos.

- Conciliación y obligaciones de red.

- Cancelación y administración residual.

La tarjeta, la cuenta bancaria, la cuenta de tarjeta y el contrato son objetos diferentes.

Una autorización no demuestra presentación, deuda facturada ni entrega de efectivo.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Emisión de tarjetas | card-issuance-service | card_issuance_db | Instrumentos, portadores, credenciales y ciclo de vida. |
| Autorizaciones | card-authorization-service | card_authorization_db | Decisiones de uso, límites operativos y coordinación de coberturas. |
| Procesamiento de consumos | card-processing-service | card_processing_db | Presentaciones, devoluciones, intercambio, conciliación y compensación. |
| Cuenta de tarjeta | card-account-service | card_account_db | Capacidad crediticia, consumos, planes, deuda, resúmenes y pagos. |
| Disputas de tarjetas | card-dispute-service | card_dispute_db | Investigación, protección, resolución y contracargos. |

Cada servicio administra PostgreSQL, usuario y migraciones propios.

No existen consultas SQL ni relaciones JPA entre bases.

### 2.1. Autoridades relacionadas

| Información o decisión | Responsable |
| --- | --- |
| Dinero y reservas de débito | Fondos. |
| Facultades sobre la cuenta bancaria | Cuentas. |
| Representación general | Representación. |
| Entrega física de efectivo | Operaciones de efectivo. |
| Aprobación crediticia | Evaluación crediticia. |
| Exposición compartida | Riesgos. |
| Mandato de débito automático | Mandatos y recurrencias. |
| Ejecución y reversión del pago bancario | Pagos y Fondos, según el efecto. |
| Conversión monetaria | Cambio de moneda. |
| Liquidación externa | Liquidaciones de tesorería. |
| Gestión de deuda vencida | Cobranzas y recuperaciones. |

La deuda de tarjeta no se duplica como préstamo.

El intercambio de redes de tarjetas pertenece a Procesamiento de consumos.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador | UUID | uuid | Cadena UUID. |
| Versión o secuencia | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha contractual | LocalDate | date | YYYY-MM-DD. |
| Importe | BigDecimal | numeric(24,8) | Cadena decimal. |
| Tasa o coeficiente | BigDecimal | numeric(24,12) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Descripción | String | varchar(200) | Cadena. |
| Fundamento | String | varchar(4000) | Cadena. |
| Resumen | String | char(64) | SHA-256 hexadecimal. |

Los importes ejecutables respetan centavos.

Los cálculos conservan al menos doce decimales intermedios y utilizan HALF_UP.

El mínimo se redondea hacia arriba sin superar el total.

La zona institucional es America/Argentina/Buenos_Aires.

### 3.2. Actuaciones

Los comandos identifican actuación, operación, objeto, versión esperada, finalidad, causa y evidencias.

El actor y el servicio emisor proceden de la autenticación.

Se distinguen instrumento, autorización, intento, reserva, compromiso, presentación, consumo, plan, resumen, pago y
disputa.

La identidad del efecto económico se conserva entre interfaces y servicios.

### 3.3. Resultados

Las respuestas distinguen PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

Identifican efectos, recursos comprometidos y pendientes.

Una falta de respuesta no demuestra rechazo ni ausencia de ejecución.

## 4. Emisión de tarjetas

### 4.1. Instrumento

El instrumento conserva:

- Identidad y referencia única de emisión.

- Constitución y contrato.

- Modalidad de débito o crédito.

- Portador.

- Responsable contractual.

- Carácter principal o adicional.

- Cuenta bancaria o cuenta de tarjeta.

- Identificación protegida.

- Identificador enmascarado.

- Vigencia.

- Predecesor y sucesor.

- Versión.

Una adicional no crea otra capacidad crediticia ni concede titularidad de una cuenta bancaria.

### 4.2. Estados

Se separan emisión, entrega, activación, habilitación y vigencia.

Una tarjeta puede estar emitida, entregada e inactiva.

El bloqueo o reemplazo no elimina consumos ni compromisos anteriores.

### 4.3. Credenciales

La simulación utiliza credenciales sintéticas.

Se conservan referencias protegidas y verificadores cuando basta comprobar un secreto.

Las credenciales no aparecen en eventos, comprobantes o logs.

La activación requiere comprobación del portador. Los datos impresos no bastan.

Un reemplazo no reactiva el instrumento anterior ni reutiliza credenciales comprometidas.

## 5. Límites: propiedad y aplicación

### 5.1. Clases

| Clase | Definición | Autoridad de consumo |
| --- | --- | --- |
| Límite crediticio general | Financiación aprobada por cuenta y moneda. | Cuenta de tarjeta. |
| Sublímite crediticio | Parte del límite general asignada a una adicional o modalidad. | Cuenta de tarjeta. |
| Límite operativo por transacción | Máximo admitido para un uso concreto. | Autorizaciones. |
| Límite operativo acumulado | Máximo de uso dentro de un período definido. | Autorizaciones. |
| Habilitación | Canales, operaciones y ámbitos permitidos. | Emisión, con aplicación en Autorizaciones. |

Emisión administra la petición y las características del instrumento.

No mantiene saldos consumibles de crédito.

Un sublímite crediticio reduce el alcance de una capacidad común. No crea financiación adicional.

### 5.2. Definición tipada

Cada límite identifica:

- Tipo.

- Objeto alcanzado.

- Operaciones.

- Moneda.

- Importe.

- Unidad y período cuando corresponda.

- Vigencia.

- Fundamento.

- Versión.

- Autoridad de consumo.

No se habilita un límite acumulado sin período definido.

No se utiliza un mismo contador como límite diario y sublímite crediticio.

### 5.3. Cambios

Los cambios administrados desde Emisión se derivan a su autoridad de aplicación.

Emisión no informa aplicado un sublímite crediticio hasta recibir confirmación de Cuenta de tarjeta.

Autorizaciones no decide sobre un contador con una versión de configuración pendiente o discontinua.

Los aumentos de crédito requieren Evaluación crediticia y Riesgos.

Las reducciones conservan consumos y compromisos anteriores. Los excesos no se ocultan aumentando límites.

## 6. Cuenta de tarjeta y capacidad

### 6.1. Constitución

La cuenta conserva contrato, responsables, aprobación, aplicación crediticia, exposición, límites por moneda,
condiciones económicas y día de cierre.

El cierre contractual está entre los días 1 y 28.

La constitución confirma la aplicación de aprobación y transforma exposición mediante Riesgos.

Constituir no genera deuda.

### 6.2. Posición

Por moneda se mantienen:

- Límite general.

- Sublímites crediticios.

- Compromisos.

- Capital presentado.

- Capital facturado.

- Capital de cuotas futuras.

- Capital rotativo.

- Intereses.

- Comisiones y tributos.

- Saldos a favor.

- Conceptos protegidos.

- Cobertura protegida.

- Exceso.

- Capacidad nueva.

Las clasificaciones de capital no se suman dos veces.

Reclasificar capital hacia un plan o rotativo no genera otra deuda.

Un saldo a favor no aumenta el límite aprobado.

### 6.3. Compromiso crediticio

El compromiso identifica autorización, instrumento, cuenta, capital, moneda, modalidad, plan, remanente y firmeza.

Cuenta de tarjeta afecta conjuntamente:

- Capacidad general.

- Sublímites crediticios aplicables.

- Cobertura protegida cuando corresponda.

La comprobación y afectación se realizan bajo los mismos controles locales.

Dos adicionales no pueden utilizar simultáneamente la misma capacidad común.

La compra en cuotas compromete el capital total.

### 6.4. Conversión y liberación

La incorporación de consumo reduce compromiso y aumenta capital en una transacción.

Una liberación solo alcanza el remanente no convertido en deuda y comprobablemente cancelable.

La reducción de límites conserva compromisos firmes e inciertos.

La cobertura protegida es exclusiva de esos compromisos. No habilita compras nuevas.

## 7. Límites operativos de uso

### 7.1. Contadores

Autorizaciones mantiene contadores por definición, objeto, moneda y período.

Cada contador conserva:

- Configuración y versión.

- Inicio y fin.

- Importe comprometido.

- Importe ejecutado computable.

- Ajustes.

- Capacidad restante.

- Secuencia.

Las afectaciones tienen identidad por autorización y límite.

La misma autorización puede afectar varios límites operativos. Su aceptación es conjunta dentro de Autorizaciones.

### 7.2. Períodos

La autorización se vincula al período aplicable al admitirla.

Un cambio de día o mes no borra compromisos anteriores.

Cada compromiso continúa en su período original hasta resolverse. Las obligaciones monetarias y crediticias mantienen,
además, sus propios controles.

Una presentación no crea otro consumo operativo por el importe ya cubierto por la autorización.

### 7.3. Consumo

El consumo convierte la parte comprometida en uso ejecutado, sin descontarla nuevamente.

La liberación restituye únicamente el compromiso no ejecutado.

Una devolución posterior de una compra no elimina automáticamente el uso histórico del período.

Los errores de autorización o duplicación se corrigen mediante ajustes identificados.

## 8. Protocolo de autorización

### 8.1. Solicitud e intento

La solicitud conserva participante, referencia de operación, instrumento, comercio o dispositivo, importe, moneda,
modalidad, autenticación y condiciones aceptadas.

El intento identifica:

- Autorización.

- Contenido exacto.

- Versiones de habilitación.

- Preparaciones de autoridades.

- Compromisos operativos.

- Reserva de débito o compromiso de crédito.

- Límite temporal.

- Decisión.

- Resultado.

Los estados distinguen preparación, listo para decidir, aprobado, rechazado, abortado y recuperación.

### 8.2. Preparación

Autorizaciones:

- Verifica identidad del instrumento y contenido.

- Obtiene las preparaciones exigibles.

- Compromete límites operativos locales.

- Solicita cobertura económica.

- Comprueba resultados.

- Confirma la decisión.

En débito, Fondos constituye la reserva.

En crédito, Cuenta de tarjeta constituye el compromiso general y sus sublímites crediticios.

No se responde favorablemente con cobertura pendiente o incierta.

### 8.3. Coordinación frente a cambios

Las habilitaciones, facultades y restricciones exigibles se preparan para el intento conforme al protocolo común.

La autoridad ordena su preparación frente a cambios incompatibles y consulta la decisión antes de concluir el
tratamiento de un acto preparado.

Una petición de aborto y la aprobación se ordenan dentro de Autorizaciones.

Si la aprobación ya fue confirmada, el cambio posterior conserva el tratamiento de compromisos anteriores. Aprobar no
garantiza que una presentación futura pueda ejecutarse ignorando nuevas medidas.

### 8.4. Fallos de cobertura

Si la cobertura económica se rechaza definitivamente:

- No se aprueba la autorización.

- Se resuelven las preparaciones.

- Se liberan los compromisos operativos que no respalden ejecución.

- Se conserva el resultado.

Si se desconoce la cobertura, se consulta su referencia original.

No se libera el contador local suponiendo que la reserva remota no existe.

### 8.5. Aprobación

La transacción local confirma decisión, cobertura referenciada, compromisos de uso, idempotencia y outbox.

La respuesta incluye autorización, vigencia y referencia económica.

Si se pierde la respuesta, se recupera esa decisión.

Una anulación posterior requiere comprobar que la autorización no fue utilizada ni puede ejecutarse de forma pendiente.

### 8.6. Vencimiento

Las autorizaciones de compra tienen siete días corridos.

El vencimiento inicia comprobación con la red.

No libera dinero, crédito ni límites operativos por sí solo.

Cada autoridad confirma su liberación. La autorización conserva pendientes hasta reunir los resultados necesarios.

## 9. Presentaciones y ejecuciones

### 9.1. Datos

Cada presentación conserva:

- Participante y mecanismo.

- Referencia externa.

- Operación original.

- Autorización.

- Instrumento.

- Comercio o dispositivo.

- Importe y moneda originales.

- Importe y moneda presentados.

- Conversión.

- Condiciones del plan.

- Carácter parcial o final.

- Efecto económico.

- Evidencia.

- Resultado.

La recepción de un archivo no valida todos sus elementos.

### 9.2. Débito

Procesamiento valida y solicita a Fondos consumir la reserva dentro de su circuito.

Fondos verifica consumidor, correspondencia, cobertura y restricciones aplicables.

La preparación monetaria utiliza el intento de ejecución de Fondos. La autorización anterior no sustituye ese control.

En extracciones se utiliza el resultado de Operaciones de efectivo y el movimiento existente.

No se constituye otra reserva ni se debita nuevamente por la presentación de red.

### 9.3. Crédito

Cuenta de tarjeta incorpora el consumo y transforma compromiso en capital.

Cuando existan controles externos exigibles para esa incorporación, prepara el acto y registra una decisión durable
junto con el efecto local, siguiendo el protocolo común.

El bloqueo de una tarjeta no impide automáticamente reconocer una presentación válida de una operación anterior.

### 9.4. Parcialidades

Cada parcial tiene efecto propio y consume únicamente la parte correspondiente.

Una presentación final de 60 sobre 100 permite consumir 60 y tramitar liberación de 40.

Una presentación parcial no final conserva el resto.

Los límites operativos reciben la confirmación vinculada para transformar su compromiso en uso ejecutado.

La pérdida de esa comunicación mantiene un pendiente recuperable; no genera otro cargo.

### 9.5. Excepciones

Las presentaciones tardías, sin autorización, por exceso o con condiciones incompatibles quedan bajo revisión.

Un exceso requiere capacidad adicional y fundamento.

No se concede financiación por recibir un archivo.

Una obligación externa sin derecho a cargarla al cliente se conserva separadamente.

## 10. Cuotas y planes

El plan identifica consumo o capital seleccionado, moneda, capital, cantidad de cuotas, tasa, cargos, tributos,
aceptación, cronograma y CFT.

No incorpora automáticamente intereses o cargos pendientes al capital.

Convertir deuda existente en plan no crea otra compra ni otra afectación del capital.

### 10.1. Cuotas sin interés

Se distribuye capital entre cuotas.

La última amortización absorbe el residuo.

### 10.2. Plan financiado

Se utiliza sistema francés:

$C = P\frac{i}{1 - (1 + i)^{- n}}$

Con tasa cero:

$C = \frac{P}{n}$

La nominal anual convertible mensualmente utiliza $i = j/12$.

Para efectiva anual:

$i = (1 + e)^{1/12} - 1$

El primer período irregular utiliza:

$i_{inicial} = (1 + i)^{d/D} - 1$

La diferencia se identifica en la primera cuota. El ajuste hábil no agrega intereses por sí solo.

### 10.3. CFT

Con beneficio neto inicial $B$:

$B = \frac{Q_{k}}{(1 + r)^{d_{k}/365}}$

Se conservan flujos, fechas, conceptos, algoritmo y supuestos.

Los cargos iniciales no se cuentan otra vez.

Los cargos generales independientes de contratar el plan no se atribuyen nuevamente.

Un costo obligatorio desconocido impide informar CFT definitivo.

## 11. Anulaciones y devoluciones

Anular una autorización no presentada libera recursos. No acredita dinero.

Devolver un consumo genera un crédito vinculado.

Se controla el importe recuperable considerando devoluciones y reparaciones anteriores o comprometidas.

En cuotas se reduce:

- Capital futuro del mismo consumo, desde las últimas cuotas.

- Capital facturado pendiente.

- Saldo a favor por capital ya pagado.

Se recalculan intereses futuros y consecuencias indebidas.

Las devoluciones conservan moneda.

Los ajustes por disputa y devoluciones ordinarias comparten referencias de regularización para evitar duplicaciones.

## 12. Resúmenes y entrega

El cierre comprende presentaciones confirmadas hasta finalizar el día contractual.

Cuenta de tarjeta ordena el cierre frente a la incorporación de consumos mediante corte temporal y secuencia local.

Los posteriores pasan al ciclo siguiente.

El resumen conserva composición, monedas, total, mínimo, protecciones, vencimiento, fuentes y documento.

Las autorizaciones y cuotas futuras se presentan separadas del exigible.

El vencimiento ordinario es veinte días corridos después del cierre, ajustado al siguiente hábil.

Debe acreditarse entrega al menos cinco días corridos antes.

Si falla esa anticipación:

- Se conserva cierre.

- Se registra incidencia.

- Se establece vencimiento suficiente desde entrega acreditada.

- Se actualizan cobros pendientes.

- Se corrigen financiación o mora atribuibles a demora del banco.

No se crea otra ocurrencia ejecutable por cambiar el vencimiento.

## 13. Total, mínimo y pagos

### 13.1. Mínimo

Por moneda y sobre conceptos no protegidos:

- $P$: capital facturado pendiente.

- $K$: parte de $P$ correspondiente a mínimos anteriores vencidos.

- $C$: conceptos adicionales exigibles.

$T = P + C$

$M = \min_{}\left( T,\ C + K + 0,10(P - K) \right)$

Se exige $0 \leq K \leq P$.

El mínimo se redondea hacia arriba sin superar total.

No se agregan nuevamente los importes de $K$ como deuda.

### 13.2. Preparación y aplicación

La preparación identifica pago, cuenta, moneda, importe, fecha efectiva, versión y distribución.

Se consideran preparaciones concurrentes.

La imputación atiende obligaciones vencidas más antiguas y luego las del ciclo; dentro de ellas, intereses exigibles y
capital, comisiones después y tributos asociados.

La confirmación requiere ingreso comprobado y registra aplicación una sola vez.

El capital pagado restituye capacidad dentro de los límites vigentes.

Pagar el total no precancela cuotas futuras.

Los excedentes conservan identidad y destino.

### 13.3. Débito automático

La adhesión distingue total y mínimo.

Cuenta de tarjeta calcula remanente; Mandatos origina ocurrencia; Pagos ejecuta; Cuenta de tarjeta aplica.

La ejecución ordinaria es a las 09:00 del vencimiento efectivo.

Se respeta el máximo autorizado.

Insuficiencia rechaza el importe completo. No existen parcialidades o reintentos ocultos.

La reversión del débito se refleja mediante corrección vinculada, sin repetir la restitución monetaria.

## 14. Financiación rotativa y monedas

El capital facturado impago genera interés desde el día siguiente al vencimiento:

$I = P_{d}\frac{j_{d}}{365}$

No se calcula sobre intereses, comisiones o tributos.

Las cuotas futuras no generan rotativo.

Una cuota transferida a rotativo deja de generar interés del plan sobre el mismo capital y período.

Cumplir el mínimo evita mora del mínimo, aunque continúe financiación.

Las deudas y límites en ARS y USD permanecen separados.

Pagar en otra moneda exige conversión identificada y aceptada.

Una cotización informativa no cancela deuda ni constituye oferta vigente.

## 15. Disputas

La disputa conserva operación, conceptos, importe, recepción, evidencias, protección, decisiones y efectos.

Los plazos son:

| Actuación | Plazo |
| --- | --- |
| Acuse | Mismo día. |
| Respuesta fundada | Quince días corridos. |
| Investigación exterior adicional | Hasta sesenta días corridos, con motivo comunicado. |
| Disconformidad | Siete días corridos desde comunicación acreditada. |
| Reconsideración oportuna | Diez días hábiles. |

Los cuestionamientos posteriores a treinta días de recibido el resumen se reciben como reclamos.

### 15.1. Protección

En crédito se suspenden exigibilidad e intereses y se recalculan total y mínimo.

La protección no elimina capital ni restituye capacidad automáticamente.

Cada causa se conserva por separado.

En débito no equivale a devolución.

### 15.2. Resolución

La preparación y aprobación son separadas.

La favorable corrige al cliente sin esperar recupero del comercio.

La desfavorable no genera intereses retroactivos del período protegido.

La protección continúa durante disconformidad y reconsideración oportuna.

Los conceptos que vuelven a exigirse integran un resumen posterior con entrega y vencimiento propios.

El provisional se regulariza contra el definitivo. No se acreditan ambos por el mismo importe.

## 16. Red, correcciones y cierre

Procesamiento conserva participantes, mecanismos, mensajes, resultados y conciliaciones.

El corte de compensación es a las 18:00. Los hechos desde ese instante integran el siguiente intervalo.

Cada obligación conserva composición, moneda, contraparte, neteo y cumplimiento.

Las autorizaciones no se compensan como consumos.

Una falla externa no vuelve impago lo pagado por el cliente.

Las correcciones se ejecutan por el propietario del efecto y conservan original y aprobación.

La baja se recibe con deuda, impide nuevos usos y mantiene presentaciones anteriores, cuotas, disputas y devoluciones.

No acelera cuotas futuras.

El cierre identifica administración residual y tratamiento de compromisos, deuda, saldos a favor y exposición.

## 17. Interfaces HTTP

### 17.1. Emisión y autorizaciones

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-07-002 | POST /cards | Instrumento y vínculos. |
| CI-07-003 | POST /cards/{id}/lifecycle-actions | Entrega, activación o credencial. |
| CI-07-004 | GET /cards/{id} | Situación enmascarada. |
| CI-07-004 | POST /cards/{id}/usage-checks | Habilitación o preparación específica. |
| CI-07-005 | POST /cards/{id}/usage-changes | Configuración y confirmaciones de aplicación. |
| CI-07-006 | POST /cards/{id}/instrument-actions | Bloqueo, renovación o cancelación. |
| CI-07-008 | POST /debit-card-authorizations | Decisión y reserva. |
| CI-07-009 | POST /credit-card-authorizations | Decisión y compromiso. |
| CI-07-011 | POST /card-authorizations/{id}/actions | Ajuste, anulación o revisión. |
| CI-07-013 | GET /card-authorizations/{id} | Contenido y coberturas. |

El seguimiento incorpora consulta y petición de aborto del intento original:

- GET /card-authorization-attempts/{id}.

- POST /card-authorization-attempts/{id}/abort-requests.

No crean otra operación.

### 17.2. Cuenta de tarjeta

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-07-001 | POST /card-accounts | Constitución. |
| CI-07-007 | POST /card-accounts/{id}/limit-changes | Límite general y sublímites crediticios aplicados. |
| CI-07-010 | POST /card-credit-commitments | Afectación conjunta de capacidad. |
| CI-07-012 | POST /card-credit-commitments/{id}/actions | Ajuste o liberación. |
| CI-07-016 | POST /card-accounts/{id}/consumptions | Capital y compromiso transformado. |
| CI-07-017 | POST /card-plan-calculations | Cronograma y CFT. |
| CI-07-018 | POST /card-accounts/{id}/plan-actions | Plan y distribución. |
| CI-07-020 | POST /card-accounts/{id}/consumption-returns | Reducción y saldo a favor. |
| CI-07-022 | POST /card-accounts/{id}/statements | Resumen. |
| CI-07-023 | POST /card-statements/{id}/delivery-evidence | Entrega y vencimiento efectivo. |
| CI-07-024 | GET /card-accounts/{id}/position | Capacidad y deuda. |
| CI-07-024 | GET /card-statements/{id} | Total y mínimo. |
| CI-07-025 | POST /card-accounts/{id}/payment-preparations | Distribución propuesta. |
| CI-07-026 | POST /card-payment-preparations/{id}/resolutions | Aplicación. |
| CI-07-027 | POST /card-statements/{id}/automatic-payment-actions | Ocurrencia y cobro. |
| CI-07-028 | POST /card-accounts/{id}/financing-assessments | Interés y mora. |
| CI-07-029 | POST /card-accounts/{id}/currency-payment-preparations | Conversión propuesta. |
| CI-07-031 | POST /card-accounts/{id}/dispute-protections | Protección y exigible. |
| CI-07-039 | POST /card-accounts/{id}/corrections | Ajustes. |
| CI-07-040 | POST /card-accounts/{id}/closures | Cierre o régimen residual. |
| CI-07-041 | GET /card-account-actions/{actionId} | Resultado original. |

### 17.3. Procesamiento

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-07-014 | POST /debit-card-presentations | Movimiento o investigación. |
| CI-07-015 | POST /credit-card-presentations | Consumo y obligación. |
| CI-07-019 | POST /card-consumption-adjustments | Anulación o devolución. |
| CI-07-021 | GET /card-network-actions/{actionId} | Presentación y seguimiento. |
| CI-07-033 | POST /card-dispute-economic-actions | Reparación y recupero separados. |
| CI-07-035 | POST /card-network-exchanges | Intercambio. |
| CI-07-035 | POST /card-network-reconciliations | Diferencias. |
| CI-07-036 | POST /card-clearing-cycles/{id}/determinations | Obligaciones. |
| CI-07-036 | GET /card-clearing-obligations/{id} | Composición. |
| CI-07-037 | POST /card-clearing-obligations/{id}/settlement-results | Cumplimiento. |
| CI-07-038 | POST /card-network-actions/{id}/corrections | Efectos vinculados. |

### 17.4. Disputas

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-07-030 | POST /card-disputes | Expediente y plazos. |
| CI-07-032 | POST /card-disputes/{id}/resolutions | Decisión y efectos solicitados. |
| CI-07-032 | POST /card-disputes/{id}/reconsiderations | Revisión. |
| CI-07-034 | GET /card-disputes/{id} | Protección y ejecución. |

Las interfaces utilizan JSON y OpenAPI, contexto común y modalidades tipadas.

Las respuestas usan 200, 201 y 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 18. Eventos

| Contrato | Evento |
| --- | --- |
| CI-07-042 | CardInstrumentChanged |
| CI-07-043 | CardAuthorizationChanged |
| CI-07-044 | CardPresentationOrNetworkEffectChanged |
| CI-07-045 | CardCapacityOrDebtChanged |
| CI-07-046 | CardStatementOrPaymentChanged |
| CI-07-047 | CardDisputeChanged |
| CI-07-048 | CardNetworkObligationChanged |

El sobre identifica evento, esquema, emisor, agregado, versión, operación, actuación, causa y fechas.

Los temas son:

- banking.card-issuance.events.

- banking.card-authorization.events.

- banking.card-processing.events.

- banking.card-account.events.

- banking.card-dispute.events.

Los eventos de autorización identifican compromisos operativos y económicos.

Los de presentación referencian efectos originales. No ordenan repetir cargos.

Las credenciales y expedientes completos no se publican.

## 19. Persistencia, recuperación y seguridad

Cada servicio mantiene datos propios, actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Autorizaciones conserva contadores operativos, afectaciones, intentos y decisiones.

Cuenta de tarjeta conserva exclusivamente capacidad crediticia, sus sublímites y deuda.

Se exige:

- Una autorización por referencia original.

- Una afectación por autorización y límite.

- Una cobertura económica por efecto.

- Una presentación por referencia externa.

- Un consumo por efecto.

- Una aplicación por ingreso.

- Restituciones dentro del alcance pendiente.

- Resumen identificable por cuenta y ciclo.

Cada transacción confirma cambio, historial, resultado e intención de evento.

Las llamadas remotas se realizan fuera de la transacción.

Una afectación remota incierta se consulta antes de liberar su contrapartida local.

Keycloak autentica actores y servicios. Los permisos distinguen configuración, aprobación, autorización, presentación,
corrección y liquidación.

PostgreSQL es autoridad. Redis no decide capacidad, bloqueos o liberaciones.

Los documentos permanecen en Documentos y comprobantes.

## 20. Pruebas y criterios de aceptación

Se verifican:

- Dos adicionales compitiendo por capacidad común.

- Sublímite crediticio y límite diario sin confundir sus contadores.

- Compromiso operativo seguido de rechazo de Fondos.

- Reserva confirmada con respuesta perdida.

- Compromiso crediticio confirmado con caída de Autorizaciones.

- Anulación concurrente con presentación.

- Vencimiento sin prueba de ausencia de ejecución.

- Presentación parcial y final.

- Extracción debitada y presentación posterior.

- Cambio de límite mientras existen compromisos.

- Bloqueo frente a intento preparado.

- Cierre frente a consumo.

- Pago concurrente con débito automático.

- Protección y mínimo.

- Provisional seguido de devolución definitiva.

- Cuota transferida a rotativo sin doble interés.

Las pruebas comprueban posiciones monetarias, crédito, contadores operativos y cantidad de efectos.

La concurrencia utiliza PostgreSQL real. Los fallos entre servicios se reproducen mediante simuladores.

## 21. Correspondencia funcional

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-07-01. Cuenta de tarjeta | Constitución y exposición. | CI-07-001, 041 y 045. |
| OF-07-02. Emisión | Instrumentos y vínculos. | CI-07-002 y 042. |
| OF-07-03. Activación | Entrega y credenciales. | CI-07-003 y 004. |
| OF-07-04. Habilitaciones y límites | Autoridades y confirmación. | CI-07-005 y 007. |
| OF-07-05. Bloqueo y reemplazo | Ciclo del instrumento. | CI-07-006. |
| OF-07-06. Débito | Límites operativos y reserva. | CI-07-008 y 013. |
| OF-07-07. Crédito | Límites operativos y capacidad crediticia. | CI-07-009 y 010. |
| OF-07-08. Ajustar autorización | Remanentes y liberaciones. | CI-07-011, 012 y 043. |
| OF-07-09. Presentación de débito | Consumo o efecto existente. | CI-07-014 y 021. |
| OF-07-10. Presentación de crédito | Capital reconocido. | CI-07-015 y 016. |
| OF-07-11. Planes | Cálculo y distribución. | CI-07-017 y 018. |
| OF-07-12. Devoluciones | Crédito y regularización. | CI-07-019 y 020. |
| OF-07-13. Resumen | Corte y entrega. | CI-07-022 y 023. |
| OF-07-14. Total y mínimo | Exigible por moneda. | CI-07-024. |
| OF-07-15. Pagos | Preparación e imputación. | CI-07-025 y 026. |
| OF-07-16. Débito automático | Ocurrencia y pago. | CI-07-027 y 046. |
| OF-07-17. Rotativo y mora | Bases y vencimientos. | CI-07-028. |
| OF-07-18. Monedas | Conversión aceptada. | CI-07-029. |
| OF-07-19. Investigación | Caso y protección. | CI-07-030, 031 y 034. |
| OF-07-20. Resolución | Decisión y reparación. | CI-07-032, 033 y 047. |
| OF-07-21. Conciliación | Red y obligaciones. | CI-07-035, 036, 037 y 048. |
| OF-07-22. Correcciones | Ajustes por propietario. | CI-07-038 y 039. |
| OF-07-23. Cierre | Instrumentos y administración residual. | CI-07-006, 040 y 041. |
