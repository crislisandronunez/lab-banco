# Diseño técnico — Módulo 8. Ahorro e inversiones

## 1. Propósito y alcance

El módulo administra depósitos a plazo e inversiones de clientes, desde su contratación hasta el pago, rescate, venta o
traslado de los activos.

Comprende:

- Constitución, remuneración, vencimiento y renovación de depósitos.

- Precancelación de modalidades admitidas.

- Definición de instrumentos de inversión.

- Órdenes, recursos comprometidos y ejecuciones.

- Liquidación de dinero e instrumentos.

- Cuentas de inversión, posiciones y custodia.

- Precios de referencia y valuación.

- Derechos económicos.

- Traslados entre custodias.

- Conciliaciones, correcciones y cierre.

Los depósitos generan una obligación de devolución del banco. Las inversiones generan posiciones y derechos sobre
instrumentos.

Una orden aceptada no demuestra ejecución. Una ejecución no demuestra liquidación ni disponibilidad.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Depósitos a plazo | term-deposit-service | term_deposit_db | Depósitos, cálculos, renovaciones y obligaciones de devolución. |
| Instrumentos de inversión | investment-instrument-service | investment_instrument_db | Definiciones, habilitaciones, referencias externas y eventos de instrumentos. |
| Órdenes de inversión | investment-order-service | investment_order_db | Instrucciones, compromisos, ejecuciones y coordinación de liquidación. |
| Tenencias y valuación | investment-position-service | investment_position_db | Cuentas, cantidades, compromisos, valuaciones, derechos y traslados. |

Cada servicio administra su base PostgreSQL, usuario y migraciones.

No existen consultas SQL, relaciones JPA ni claves foráneas entre bases de servicios.

### 2.1. Autoridades relacionadas

| Información o decisión | Responsable |
| --- | --- |
| Oferta comercial | Condiciones comerciales. |
| Acuerdo y titularidad contractual | Contratos. |
| Identidad y representación | Personas y Representación. |
| Dinero, reservas y movimientos de cuentas | Fondos. |
| Capacidades de cuentas bancarias | Cuentas. |
| Adecuación y restricciones | Servicios competentes de Riesgo y cumplimiento. |
| Criterios tributarios | Criterios tributarios. |
| Conversión monetaria | Cambio de moneda. |
| Liquidación externa | Liquidaciones de tesorería. |
| Liquidez e inversiones propias del banco | Tesorería. |
| Registración contable | Contabilidad. |

Los activos de clientes no se registran como inversiones propias del banco.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador interno | UUID | uuid | Cadena UUID. |
| Versión o secuencia | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha económica | LocalDate | date | YYYY-MM-DD. |
| Importe monetario | BigDecimal | numeric(24,8) | Cadena decimal. |
| Cantidad de instrumentos | BigDecimal | numeric(30,12) | Cadena decimal. |
| Precio o valor de unidad | BigDecimal | numeric(30,12) | Cadena decimal. |
| Tasa o coeficiente | BigDecimal | numeric(24,12) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Descripción | String | varchar(200) | Cadena. |
| Fundamento | String | varchar(4000) | Cadena. |
| Resumen de contenido | String | char(64) | SHA-256 hexadecimal. |

Los importes ejecutables respetan centavos.

Las cantidades deben respetar la precisión y el múltiplo de negociación del instrumento. Una precisión superior a la
soportada impide habilitarlo.

Las unidades de un depósito UVA son datos de cálculo, no cantidades negociables. Se conservan con al menos doce
decimales y no se truncan como una orden de inversión.

Los cálculos utilizan aritmética decimal. Los importes finales se redondean mediante HALF_UP.

### 3.2. Contexto de actuación

Los comandos incluyen:

- Actuación y operación de negocio.

- Objeto y versión esperada.

- Finalidad y causa.

- Evidencias.

- Contrato y condiciones aceptadas.

- Fecha efectiva cuando corresponda.

- Recepción acreditada cuando afecte un plazo.

El actor y el servicio emisor proceden de la autenticación.

Las evidencias documentales identifican documento, versión y resumen. Los archivos permanecen en Documentos.

### 3.3. Identidades y resultados

Se distinguen depósito, constitución, liquidación, renovación, instrumento, orden, ejecución, compromiso, movimiento,
derecho y traslado.

Los resultados técnicos son PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

Cada respuesta identifica lo confirmado, lo comprometido y lo pendiente.

La falta de respuesta no demuestra ausencia de ejecución.

## 4. Calendarios y modalidades

La zona institucional es America/Argentina/Buenos_Aires.

| Actuación | Convención |
| --- | --- |
| Constitución de depósitos | Hábiles, de 09:00 a 17:00. |
| Recepción de órdenes | Todos los días. |
| Corte de fondos comunes | 15:00 de días hábiles. |
| Negociación de títulos | Días de mercado, de 11:00 a 17:00. |
| Liquidación ordinaria | Siguiente hábil del calendario correspondiente. |
| Recepción de traslados | Todos los días. |
| Gestión ordinaria de traslados | Hábiles, de 09:00 a 17:00. |

Los intervalos de atención excluyen su hora final. Las solicitudes posteriores quedan para la siguiente ventana
aplicable.

Fuera de horario no se informa ejecución ni se conserva una oferta vencida.

### 4.1. Depósitos admitidos

| Modalidad | Moneda | Plazo mínimo | Precancela desde |
| --- | --- | --- | --- |
| Tradicional fijo | ARS o USD | 30 días corridos | No admite. |
| Precancelable nominal | ARS | 180 días corridos | Día 30. |
| UVA | ARS expresados en unidades | 90 días corridos | No admite. |
| UVA precancelable, persona humana | ARS expresados en unidades | 90 días corridos | Día 30. |
| UVA precancelable, persona jurídica | ARS expresados en unidades | 180 días corridos | Día 90. |

Las constancias son nominativas e intransferibles. No se admite fraccionar el depósito.

### 4.2. Inversiones admitidas

Se admiten fondos comunes abiertos con valor diario y títulos de renta fija sin apalancamiento.

No se admiten:

- Ventas en descubierto.

- Inversión financiada con descubierto.

- Utilización anticipada de cobros pendientes.

- Derivados o garantías de margen.

- Utilización de activos de clientes para obligaciones propias.

## 5. Depósitos a plazo

### 5.1. Datos

| Campo | Regla |
| --- | --- |
| depositId | Identidad estable. |
| constitutionId | Coordinación de origen, única. |
| contractId y contractVersion | Condiciones aceptadas. |
| productId y productVersion | Modalidad de catálogo. |
| holders | Titulares y régimen de actuación. |
| sourceAccountId | Origen autorizado. |
| destinationAccountId | Destino de devolución. |
| principalAmount | Capital original. |
| currency | Moneda. |
| modality | Modalidad admitida. |
| constitutedOn | Fecha efectiva de constitución. |
| contractualMaturityOn | Vencimiento contractual. |
| scheduledPaymentOn | Fecha programada ajustada. |
| annualNominalRate | Tasa contractual. |
| earlyExitRate | Obligatoria cuando admite precancelación. |
| economicDeterminationId | Cálculo aceptado. |
| predecessorDepositId | Depósito renovado. |
| version | Concurrencia. |

Las series y valores UVA se conservan con fecha, fuente, versión y evidencia.

### 5.2. Estados separados

| Dimensión | Estados |
| --- | --- |
| Constitución | Preparada, recursos pendientes, débito pendiente, constituida o reparación requerida. |
| Ciclo económico | Vigente, vencida, precancelación programada o cancelada. |
| Obligación de pago | No exigible, pendiente, parcialmente cumplida o cumplida. |
| Renovación | No prevista, instruida, en preparación, confirmada o impedida. |

Un débito confirmado con constitución pendiente conserva una obligación de completar o restituir.

### 5.3. Preparación y constitución

La preparación determina fechas, tasas, modalidad, actualización, salida anticipada e instrucción de vencimiento.

Una propuesta vencida se actualiza antes de afectar dinero.

La constitución sigue este recorrido:

- Conservar intención y referencias.

- Comprobar contrato, participantes y condiciones.

- Reservar capital propio en Fondos.

- Consumir la reserva con efecto estable.

- Comprobar el débito.

- Reconocer el depósito.

- Solicitar constancia.

- Comunicar a Contabilidad y Tesorería.

El capital no permanece disponible en la cuenta de origen.

La coordinación entre Fondos y Depósitos es persistente. No se presume una transacción distribuida.

### 5.4. Remuneración nominal

Se aplica interés simple:

$I = P \times j \times \frac{d}{365}$

El intervalo incluye constitución y excluye la fecha hasta la cual se calcula.

Se cuentan días reales, con divisor 365 incluso en años bisiestos.

No se capitalizan intereses dentro del mismo depósito.

### 5.5. Actualización UVA

Se conservan:

$Q = \frac{P}{U_{0}}$

$CapitalActualizado = Q \times U_{v}$

$Interés = CapitalActualizado \times j \times \frac{d}{365}$

Actualización e interés son conceptos separados.

La ausencia del valor necesario mantiene pendiente la liquidación definitiva. No se sustituye por un valor supuesto.

Los cálculos conservan entradas y algoritmo versionado para reproducir el resultado.

## 6. Vencimiento, renovación y precancelación

### 6.1. Instrucción de vencimiento

Las modalidades son:

- Pago total.

- Renovación del capital y pago del rendimiento neto.

- Renovación del capital más rendimiento neto.

En indexados, renovar capital significa renovar capital actualizado.

Sin renovación aceptada se prepara pago total.

La instrucción es versionada y su modificación se ordena frente a la aceptación de la nueva constitución.

### 6.2. Pago

El vencimiento inhábil se paga el siguiente hábil, manteniendo remuneración hasta esa fecha programada.

En indexados se utiliza el valor correspondiente a esa fecha.

El pago conserva capital, actualización, interés, tributos, neto y efectos monetarios.

Una cuenta de destino inválida no extingue la obligación.

Una demora adicional del banco genera compensación separada:

- Nominal: interés simple sobre capital a la tasa contratada.

- Indexado: diferencia del total extendido hasta la puesta a disposición, descontando lo reconocido.

No se paga dos veces el mismo período.

### 6.3. Renovación

Cada renovación crea otro depósito vinculado.

Antes de confirmar se comprueban oferta vigente, elegibilidad, medidas y consentimiento.

El importe renovable permanece comprometido durante la transición.

Cuando ambos depósitos pertenecen al mismo servicio, una transacción local registra:

- Liquidación del anterior.

- Aplicación del importe a la nueva constitución.

- Obligación del nuevo depósito.

- Remanente pagadero.

- Historial y outbox.

No se acredita como dinero libre y se reutiliza simultáneamente.

La fecha inicial nueva corresponde a la constitución efectiva. Si no puede renovarse, se prepara pago total salvo
impedimento.

### 6.4. Precancelación

La salida es total y requiere cinco días hábiles de preaviso.

La fecha es la más tardía entre permanencia mínima y preaviso, ajustada al siguiente hábil.

Si alcanza el vencimiento, se aplica el pago ordinario.

La tasa de salida queda fijada al constituir.

Se utiliza capital original e interés simple hasta el pago. En UVA precancelable no se suma actualización de vencimiento
al rendimiento de salida.

No se agrega otra penalidad.

La solicitud, aceptación y ejecución tienen referencias diferenciadas para impedir cancelaciones duplicadas.

## 7. Instrumentos de inversión

### 7.1. Definición

Cada instrumento contiene:

- Identidad estable y clase.

- Emisor o administrador.

- Identificadores externos.

- Moneda.

- Precisión de cantidad.

- Unidad o múltiplo de negociación.

- Convención de precio.

- Operaciones admitidas.

- Mínimos.

- Calendarios y horarios.

- Liquidación.

- Riesgos y documentación.

- Restricciones.

- Eventos y flujos.

- Versiones y fechas de efecto.

Los bonos cotizan por cien nominales.

Los fondos identifican clase y valor diario de cuotaparte.

Un instrumento incompleto no se habilita.

### 7.2. Versiones y suspensión

Los estados son preparación, pendiente de aprobación, habilitado, suspendido y retirado.

La habilitación requiere preparación y aprobación separadas.

La suspensión identifica si afecta compras, ventas, rescates, traslados o liquidaciones. No bloquea automáticamente
actuaciones fuera del alcance.

Las posiciones y derechos existentes se conservan.

### 7.3. Referencias de precios

CI-08-013 recibe precios y cuotapartes en Instrumentos de inversión.

Este servicio valida procedencia, identidad, fecha y formato y conserva la referencia externa inmutable.

Tenencias y valuación mantiene las referencias utilizadas para valorar posiciones, su vigencia, selección y resultados.
Conserva el identificador y la versión del dato recibido, sin generar otra cotización independiente.

Las correcciones de fuente producen versiones vinculadas.

Se distinguen precio de negociación, valor de cuotaparte e interés corrido.

## 8. Cuentas y posiciones de inversión

### 8.1. Cuenta

La cuenta contiene:

- Identidad y constitución.

- Contrato.

- Titulares.

- Actuación individual o conjunta.

- Autorizados.

- Cuentas monetarias vinculadas.

- Custodio y referencia externa.

- Estado.

- Versión.

Constituir no incorpora instrumentos.

Cambiar un autorizado no cambia propiedad. Modificar titulares exige formalización y tratamiento explícito de
posiciones.

### 8.2. Posición

La identidad de posición comprende cuenta, instrumento, clase y custodia.

Se conservan:

- Cantidad registrada.

- Cantidad comprometida.

- Cantidad restringida.

- Cantidad disponible.

- Entradas y salidas pendientes.

- Unidades en tránsito.

- Secuencia y corte.

- Derechos relacionados.

Las categorías se informan con sus relaciones. No se suman cantidades superpuestas como activos distintos.

### 8.3. Compromisos

El compromiso identifica operación, finalidad, cantidad, consumos, liberaciones, remanente y firmeza.

Se constituye bajo bloqueo de la posición.

Con cien unidades, dos ventas de ochenta no pueden obtener ambas cobertura.

Comprometer no reduce cantidad registrada ni transfiere propiedad.

Consumir registra la salida y reduce el compromiso en una sola transacción.

Las restricciones conservan causas y prioridades. Levantar una no libera unidades alcanzadas por otra.

## 9. Órdenes de inversión

### 9.1. Contenido

| Campo | Contenido |
| --- | --- |
| orderId | Identidad estable. |
| originInstructionReference | Referencia única del originante. |
| investmentAccountId | Cuenta. |
| instrumentId y instrumentVersion | Definición utilizada. |
| instrumentClass | Clase. |
| orderType | Suscripción, rescate, compra o venta. |
| amount | Importe de suscripción. |
| quantity | Cantidad de rescate o títulos. |
| redeemAll | Rescate de totalidad aceptado. |
| limitPrice | Obligatorio para títulos. |
| currency | Moneda. |
| processingDate | Fecha aplicable. |
| tradingSessionReference | Rueda de títulos. |
| acceptedContentHash | Contenido autorizado. |
| resourceReferences | Reservas y compromisos. |
| version | Concurrencia. |

Las entradas usan modalidades excluyentes.

Un rescate solicitado por importe se convierte en cantidad estimada y requiere confirmación.

### 9.2. Estados

Se separan:

- Recepción y validación.

- Recursos.

- Envío.

- Aceptación externa.

- Ejecución.

- Cancelación.

- Liquidación.

Una orden puede estar parcialmente ejecutada, con remanente cancelado y liquidación pendiente de sus ejecuciones.

### 9.3. Recursos

La compra reserva principal máximo y cargos autorizados mediante Fondos, usando exclusivamente fondos propios.

Venta y rescate comprometen unidades en Tenencias.

Los cargos monetarios adicionales se cubren según su modalidad aceptada.

Si la preparación queda incompleta, el proceso conserva lo obtenido y regulariza únicamente recursos cuya falta de
ejecución esté comprobada.

No se invierten cobros todavía pendientes.

### 9.4. Cancelación y envío

La autorización de despacho y la cancelación se ordenan sobre la misma orden.

Si el despacho ya fue reclamado por un trabajador, no se confirma cancelación sin resolver ese envío.

Los adaptadores conservan referencia estable y consultas de resultado.

Una cancelación externa confirmada debe impedir la ejecución posterior de esa referencia. La ausencia aislada en una
consulta no basta si existe envío en tránsito.

## 10. Ejecución de inversiones

### 10.1. Fondos comunes

Las suscripciones recibidas antes de las 15:00 de un hábil utilizan esa fecha. Desde las 15:00 utilizan el siguiente
hábil.

Se aplica el valor de cierre correspondiente.

La cantidad se obtiene del importe neto y se trunca a la precisión del instrumento.

El administrador confirma cantidad y dinero aplicado. El residuo se libera o restituye.

Las unidades no quedan disponibles hasta liquidación confirmada.

Los rescates se ejecutan por cantidad o totalidad aceptadas, sin parcialidad unilateral.

Una suspensión de valuación conserva la orden y la incidencia.

### 10.2. Títulos

Las órdenes utilizan cantidad y precio límite y vencen al finalizar la rueda elegida.

Cada ejecución conserva referencia, cantidad, precio y momento.

Para bonos:

$PrecioPrincipal = Nominal \times \frac{PrecioCotizado}{100}$

La liquidación distingue precio principal, interés corrido, cargos y tributos.

Mil nominales a 95 representan 950 de precio principal.

La compra no supera el máximo autorizado y la venta no se ejecuta por debajo del límite.

Las ejecuciones parciales tienen identidades y liquidaciones propias.

### 10.3. Resultados externos

La clave comprende participante, mecanismo y referencia de ejecución.

Una confirmación repetida no crea otra ejecución.

Una referencia desconocida o un contenido incompatible se conserva en investigación.

Un resultado tardío se vincula a la orden original. No origina otra.

## 11. Liquidación

Órdenes administra una coordinación persistente por ejecución.

Cada coordinación contiene:

- Obligación monetaria.

- Obligación de instrumentos.

- Participantes.

- Fecha prevista.

- Reservas y compromisos.

- Instrucción de Tesorería.

- Componentes confirmados.

- Movimientos.

- Diferencias y pendientes.

Los títulos utilizan entrega contra pago.

Las suscripciones y rescates relacionan asignación o retiro de unidades con su contraprestación.

No existe una transacción SQL única entre Fondos, Tesorería y Tenencias.

La coordinación conserva estados intermedios y no habilita recursos cuya contraprestación necesaria siga incierta.

Instrumentos recibidos sin pago confirmado no demuestran liquidación completa.

Una compra ejecutada no se transforma en rechazo original porque Tesorería tenga un faltante.

El consumo de reserva monetaria y el registro de unidades utilizan efectos estables. Su recuperación no repite dinero ni
instrumentos.

## 12. Movimientos, valuación y derechos

### 12.1. Movimientos de custodia

Cada movimiento conserva:

- Cuenta y custodia.

- Instrumento y clase.

- Cantidad.

- Dirección.

- Hecho económico.

- Compromiso consumido.

- Fecha efectiva.

- Secuencia.

- Evidencia.

Los movimientos confirmados no se sobrescriben.

Las consultas paginadas conservan el mismo corte.

### 12.2. Valuación

La valuación identifica cantidades al corte, precio, fuente, fecha, moneda y antigüedad.

Para fondos:

$Valor = Cantidad \times ValorCuotaparte$

Para bonos:

$ValorPrincipal = Nominal \times \frac{PrecioCierre}{100}$

El interés corrido se informa separado.

Sin precio actual puede mostrarse la última referencia como histórica. La ausencia no equivale a cero.

Una conversión informativa no ejecuta cambio.

El tránsito se incluye con identificación propia sin sumarlo otra vez en origen y destino.

### 12.3. Eventos

Instrumentos define el evento. Tenencias determina posiciones y titulares alcanzados.

Cada evento contiene tipo, fecha de derecho, pago, tasa o proporción, moneda, regla y fuente.

La aplicación conserva evento, versión, posición al corte, cantidad elegible y resultado.

La amortización reduce nominal y genera el derecho correspondiente en una misma transacción local.

Los fondos acumulativos no generan un dividendo adicional ficticio.

### 12.4. Cobro

El derecho distingue reconocimiento, exigibilidad, recepción externa y pago al cliente.

El cobro exige evidencia y utiliza un efecto monetario estable.

Un rendimiento recibido después de un traslado conserva al titular beneficiario y una obligación residual de entrega.

Las correcciones de eventos generan diferencias vinculadas, sin volver a aplicar el evento completo.

## 13. Traslados de custodia

### 13.1. Modalidades

Se admiten:

- Interno entre cuentas con idéntica titularidad.

- Salida externa.

- Entrada externa.

La coincidencia incluye cotitulares. Un cambio de propiedad no se procesa como traslado.

Los fondos comunes requieren que ambos registros admitan la misma clase y transferencia de cuotapartes.

### 13.2. Datos y estados

El traslado conserva cuentas, custodios, titularidad, instrumento, clase, cantidad, fecha, cargos, restricciones y
referencias.

Los estados distinguen:

- Validación.

- Aceptación preliminar.

- Compromiso.

- Envío.

- Salida parcial o total.

- Tránsito.

- Recepción parcial o total.

- Retorno.

- Cancelación.

- Reparación.

La cantidad solicitada, enviada, recibida, retornada y pendiente se conserva separadamente.

### 13.3. Salida externa

- Comprobar destino y titularidad.

- Obtener aceptación preliminar.

- Comprometer unidades y cargos.

- Conservar instrucción.

- Enviar.

- Confirmar salida.

- Reducir custodia de origen y compromiso.

- Registrar tránsito.

- Confirmar recepción.

- Conciliar y resolver cargos.

El tránsito no queda disponible para otra operación.

Si salen veinte de treinta, no se reenvían treinta. Quedan veinte identificadas y diez pendientes.

### 13.4. Entrada externa

Un aviso de envío no incorpora unidades disponibles.

Se comprueban recepción efectiva, clase, cantidad, titularidad y restricciones.

La incorporación es única por hecho externo.

Una recepción no identificada conserva incidencia y responsable. No se asigna arbitrariamente.

### 13.5. Traslado interno

Ambas posiciones pertenecen a Tenencias y valuación.

Se bloquean en orden estable y se confirma salida, entrada y conservación de restricciones en una transacción local.

No se reconoce compraventa ni resultado económico por el traslado.

### 13.6. Restricciones y retorno

Una medida no desaparece al trasladar.

Cuando deba mantenerse en destino, se exige capacidad y confirmación de aplicación antes de liberar el control de
origen.

Un rechazo posterior a la salida mantiene tránsito.

Las unidades solo vuelven a estar disponibles en origen después de confirmar retorno.

Una pérdida comprobada genera incidencia y reparación. No se oculta creando unidades ficticias.

## 14. Conciliación, correcciones y cierre

### 14.1. Conciliación

Órdenes concilia instrucciones, ejecuciones y liquidaciones.

Tenencias concilia custodia, tránsito, restricciones y derechos.

Se ejecuta diariamente y ante incertidumbre, utilizando cortes comparables.

Se distinguen desfases, faltantes, duplicaciones y contradicciones.

Conciliar no autoriza un movimiento por sí solo.

Los ajustes manuales requieren preparación y aprobación separadas.

### 14.2. Correcciones

Cada corrección conserva original, causa, evidencia, autorización y consecuencias.

- Corregir valuación no ejecuta compraventa.

- Corregir una duplicación interna no genera venta ficticia.

- Corregir un traslado exige tratamiento con custodios.

- Recuperar dinero requiere fundamento.

- Los errores de depósitos conservan cálculo original y diferencia.

### 14.3. Cierre

Tenencias registra petición, limita nuevos compromisos y obtiene confirmaciones de Órdenes y los responsables afectados.

Las confirmaciones se vinculan a la versión de cierre y se obtienen después de aplicar las limitaciones
correspondientes.

Cerrar no autoriza a vender a cualquier precio.

Ventas, rescates y traslados requieren instrucciones identificadas.

El cierre distingue cuenta finalizada y administración residual de derechos o incidencias.

Un depósito a plazo vigente continúa bajo su propio acuerdo.

## 15. Interfaces HTTP

Las interfaces utilizan JSON y OpenAPI. Los comandos incluyen el contexto común.

### 15.1. Depósitos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-08-001 | POST /term-deposit-preparations | Producto, capital, titulares y fechas. Devuelve condiciones para aceptar. |
| CI-08-002 | POST /term-deposits | Constitución, contrato y preparación. Devuelve depósito o coordinación pendiente. |
| CI-08-003 | POST /term-deposits/{id}/calculations | Corte, tasas e índices. Devuelve capital, actualización e interés. |
| CI-08-004 | POST /term-deposits/{id}/maturity-instructions | Pago o renovación y destino. Devuelve versión vigente. |
| CI-08-005 | POST /term-deposits/{id}/maturity-payments | Liquidación y destino. Devuelve pago y remanentes. |
| CI-08-006 | POST /term-deposits/{id}/renewals | Instrucción y oferta vigente. Devuelve sucesor y remanente pagadero. |
| CI-08-007 | POST /term-deposits/{id}/early-exit-actions | Solicitud, aceptación o ejecución tipada. Devuelve fecha y liquidación. |
| CI-08-008 | POST /term-deposits/{id}/corrections | Original, causa y aprobación. Devuelve diferencias. |
| CI-08-009 | GET /term-deposits/{id} | Devuelve posición, condiciones y obligaciones. |
| CI-08-009 | GET /term-deposit-actions/{actionId} | Recupera resultado original. |

### 15.2. Instrumentos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-08-010 | POST /investment-instruments | Definición completa. Devuelve instrumento en preparación. |
| CI-08-010 | POST /investment-instruments/{id}/enablement-actions | Revisión y aprobación. Devuelve habilitación. |
| CI-08-011 | POST /investment-instruments/{id}/changes | Versión, alcance y efecto. Devuelve modificación o suspensión. |
| CI-08-012 | GET /investment-instruments/{id} | Devuelve condiciones y versión. |
| CI-08-013 | POST /investment-reference-values | Instrumento, valor, fecha y fuente. Devuelve referencia validada. |
| CI-08-014 | POST /investment-instruments/{id}/events | Evento, fechas y reglas. Devuelve definición o corrección vinculada. |

### 15.3. Órdenes

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-08-017 | POST /investment-orders | Cuenta, modalidad, importe o cantidad y aceptación. Devuelve orden y fecha aplicable. |
| CI-08-018 | POST /investment-orders/{id}/resource-actions | Máximos y cargos. Devuelve reservas y compromisos. |
| CI-08-020 | POST /investment-orders/{id}/fund-subscriptions | Orden y cobertura. Devuelve asignación y residuo. |
| CI-08-021 | POST /investment-orders/{id}/fund-redemptions | Cantidad y compromiso. Devuelve rescate y neto. |
| CI-08-022 | POST /investment-orders/{id}/security-executions | Rueda, cantidad y límite. Devuelve ejecuciones y remanente. |
| CI-08-023 | POST /investment-orders/{id}/external-results | Referencia y evidencia externa. Devuelve incorporación o discrepancia. |
| CI-08-024 | POST /investment-orders/{id}/cancellation-actions | Motivo o vencimiento. Devuelve remanente cancelado o pendiente. |
| CI-08-025 | POST /investment-executions/{id}/settlements | Obligaciones y mecanismo. Devuelve coordinación. |
| CI-08-026 | POST /investment-settlements/{id}/confirmations | Componente y evidencia. Devuelve cumplimiento y pendientes. |
| CI-08-027 | GET /investment-orders/{id} | Devuelve recursos, ejecuciones y liquidaciones. |
| CI-08-034 | POST /investment-order-reconciliations | Ámbito y cortes. Devuelve diferencias. |
| CI-08-036 | POST /investment-orders/{id}/corrections | Error y aprobación. Devuelve ajustes y efectos. |

### 15.4. Tenencias y valuación

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-08-015 | POST /investment-accounts | Contrato, titulares y custodia. Devuelve cuenta. |
| CI-08-015 | POST /investment-accounts/{id}/changes | Modificación formalizada. Devuelve aplicación. |
| CI-08-016 | GET /investment-accounts/{id} | Devuelve titularidad y vínculos. |
| CI-08-016 | POST /investment-accounts/{id}/operation-checks | Actor y acto. Devuelve facultades y alcance. |
| CI-08-019 | POST /investment-unit-commitments | Posición, cantidad y operación. Devuelve compromiso. |
| CI-08-019 | POST /investment-unit-commitments/{id}/actions | Ajuste o liberación acreditados. Devuelve remanente. |
| CI-08-028 | POST /investment-position-movements | Hecho, cantidad y liquidación. Devuelve posición. |
| CI-08-029 | POST /investment-position-restrictions | Medida, prioridad y alcance. Devuelve aplicación. |
| CI-08-030 | GET /investment-positions | Cuenta, instrumento y corte. Devuelve cantidades y derechos. |
| CI-08-030 | GET /investment-position-movements | Posición y cursor. Devuelve historial al corte. |
| CI-08-031 | POST /investment-valuations | Posiciones y referencias. Devuelve valores y limitaciones. |
| CI-08-032 | POST /investment-event-entitlements | Evento y posiciones. Devuelve derechos y efectos. |
| CI-08-033 | POST /investment-entitlements/{id}/fulfillments | Cobro o entrega comprobados. Devuelve pago y remanente. |
| CI-08-035 | POST /investment-custody-reconciliations | Registros y cortes. Devuelve diferencias. |
| CI-08-037 | POST /investment-position-corrections | Original y evidencia. Devuelve ajuste. |
| CI-08-038 | POST /custody-transfers | Cuentas, custodios, cantidad y autorización. Devuelve traslado. |
| CI-08-039 | POST /custody-transfers/{id}/results | Salida, recepción, rechazo o retorno. Devuelve cantidades y tránsito. |
| CI-08-040 | POST /custody-transfers/{id}/resolution-actions | Cancelación o regularización. Devuelve resultado comprobado. |
| CI-08-041 | GET /custody-transfers/{id} | Devuelve componentes y pendientes. |
| CI-08-042 | POST /investment-accounts/{id}/closures | Petición y tratamiento de posiciones. Devuelve cierre o régimen residual. |

Las modalidades tienen esquemas excluyentes. Los resultados utilizan 200, 201 o 202.

Los errores usan Problem Details con códigos 400, 401, 403, 404, 409, 422 y 503, según estructura, permisos, conflicto,
impedimento o disponibilidad.

## 16. Eventos

El sobre contiene evento, esquema, emisor, agregado, versión, operación, actuación, causa, correlación y fechas.

| Contrato | Evento | Contenido |
| --- | --- | --- |
| CI-08-043 | TermDepositChanged | Constitución, cálculo, obligación, pago, renovación o corrección. |
| CI-08-044 | InvestmentInstrumentOrEventChanged | Definición, habilitación, referencia incorporada o evento versionado. |
| CI-08-045 | InvestmentOrderOrSettlementChanged | Orden, ejecución, recursos, liquidación y pendientes. |
| CI-08-046 | InvestmentPositionOrEntitlementChanged | Cantidades, valuación, derecho, cobro o ajuste. |
| CI-08-047 | CustodyTransferOrInvestmentAccountChanged | Cuenta, limitación, traslado, tránsito, cierre y pendientes. |

Los temas son:

- banking.term-deposit.events.

- banking.investment-instrument.events.

- banking.investment-order.events.

- banking.investment-position.events.

La clave es el agregado.

Un evento de valuación no representa dinero. Un derecho no acredita cobro. Una confirmación de liquidación referencia
efectos originales sin ordenar repetirlos.

## 17. Persistencia, consistencia y recuperación

### 17.1. Estructuras

| Servicio | Persistencia principal |
| --- | --- |
| Depósitos | Preparaciones, depósitos, condiciones, índices, cálculos, instrucciones, pagos y renovaciones. |
| Instrumentos | Versiones, participantes, modalidades, referencias de valores y eventos. |
| Órdenes | Instrucciones, controles, recursos, envíos, ejecuciones y liquidaciones. |
| Tenencias | Cuentas, posiciones, movimientos, compromisos, medidas, valuaciones, derechos y traslados. |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

### 17.2. Restricciones de integridad

Se exige:

- Constitución única por referencia.

- Renovación única por depósito e instrucción utilizada.

- Pago único por efecto.

- Orden única por referencia del originante.

- Ejecución externa única en su ámbito.

- Movimiento de unidades único por hecho.

- Compromisos dentro de disponibilidad.

- Derecho único por evento y posición elegible.

- Correcciones versionadas.

- Cantidades de traslado conciliables.

- Incorporación única de recepción externa.

Los traslados internos bloquean posiciones en orden estable.

Las operaciones sobre una posición serializan compromiso, movimiento, restricción y cierre.

### 17.3. Transacciones

Cada transacción confirma agregado, historial, resultado, idempotencia y outbox.

No incluye llamadas HTTP ni publicación directa a Kafka.

Los procesos distribuidos conservan intención antes de invocar y consultan por referencias estables.

Los consumidores registran inbox y efecto local antes de confirmar el offset.

### 17.4. Recuperación externa

Se consulta al detectar incertidumbre, a los cinco minutos y a los treinta minutos.

Si persiste, se abre investigación y revisión por jornada operativa.

Esos tiempos no autorizan liberaciones ni rechazos automáticos.

Los trabajadores persistentes utilizan arrendamiento y control de vigencia para impedir ejecución concurrente del mismo
paso.

## 18. Seguridad y observabilidad

Keycloak autentica usuarios, servicios y participantes simulados.

Los permisos separan:

- Constitución y pago de depósitos.

- Instrucciones de renovación.

- Definición y habilitación de instrumentos.

- Incorporación de referencias.

- Originación de órdenes.

- Incorporación de ejecuciones.

- Movimientos de custodia.

- Restricciones.

- Derechos.

- Traslados.

- Preparación y aprobación de correcciones.

La autorización considera titularidad, representación, actuación conjunta y finalidad.

Una cuenta monetaria vinculada no concede propiedad sobre la inversión.

Redis no administra posiciones, reservas, derechos, índices utilizados ni decisiones de ejecución. Puede almacenar
versiones inmutables como optimización.

PostgreSQL conserva la autoridad. Los documentos binarios permanecen en Documentos. MongoDB no resulta necesario para
estas responsabilidades.

Se observan depósitos debitados pendientes de constitución, pagos demorados, índices faltantes, órdenes inciertas,
liquidaciones incompletas, precios antiguos, diferencias de custodia y unidades en tránsito.

Los logs evitan documentación personal y contenido sensible.

## 19. Pruebas y correspondencia funcional

Las pruebas verifican cálculos con resultados independientes, restricciones en PostgreSQL, concurrencia, contratos HTTP
y eventos, y recuperación ante respuestas perdidas.

Se incluyen:

- Reserva de depósito sin descubierto.

- Débito confirmado con caída antes de reconocer depósito.

- Renovación concurrente con cambio de instrucción.

- Falta o corrección de índice UVA.

- Dos ventas o traslados sobre las mismas unidades.

- Ejecución parcial y cancelación del remanente.

- Suscripción con residuo.

- Recepción de instrumentos sin pago confirmado.

- Evento corregido después de cobrar.

- Salida de custodia sin recepción.

- Retorno parcial.

- Derechos posteriores al traslado.

- Cierre con operaciones inciertas.

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-08-01. Preparar depósito | Condiciones y aceptación. | CI-08-001 y 003. |
| OF-08-02. Constituir depósito | Reserva, débito y obligación. | CI-08-002 y 043. |
| OF-08-03. Calcular rendimiento | Tasas, unidades e índices. | CI-08-003. |
| OF-08-04. Instrucción de vencimiento | Selección versionada. | CI-08-004. |
| OF-08-05. Pago | Liquidación y acreditación. | CI-08-005. |
| OF-08-06. Renovación | Sucesor y transición de obligación. | CI-08-006. |
| OF-08-07. Precancelación | Permanencia, preaviso y salida. | CI-08-007. |
| OF-08-08. Corregir y consultar | Ajustes e historia. | CI-08-008 y 009. |
| OF-08-09. Instrumentos | Definición y habilitación. | CI-08-010 y 012. |
| OF-08-10. Suspensión | Alcance y versiones. | CI-08-011 y 044. |
| OF-08-11. Cuenta de inversión | Titularidad y custodia. | CI-08-015 y 016. |
| OF-08-12. Validar orden | Contenido y controles. | CI-08-017. |
| OF-08-13. Recursos | Fondos y compromisos de unidades. | CI-08-018 y 019. |
| OF-08-14. Suscripción | Valor y asignación. | CI-08-020 y 023. |
| OF-08-15. Rescate | Cantidad y contraprestación. | CI-08-021 y 023. |
| OF-08-16. Títulos | Límites y ejecuciones. | CI-08-022 y 023. |
| OF-08-17. Cancelación | Remanente comprobado. | CI-08-024. |
| OF-08-18. Liquidación | Componentes y confirmaciones. | CI-08-025 y 026. |
| OF-08-19. Movimientos | Cantidades y restricciones. | CI-08-028, 029 y 030. |
| OF-08-20. Valuación | Referencias y corte. | CI-08-013 y 031. |
| OF-08-21. Eventos | Definición, derechos y cobro. | CI-08-014, 032 y 033. |
| OF-08-22. Conciliación | Órdenes y custodia. | CI-08-027, 034 y 035. |
| OF-08-23. Correcciones | Ajustes por propietario. | CI-08-036 y 037. |
| OF-08-24. Cierre | Limitaciones y régimen residual. | CI-08-042 y 047. |
| OF-08-25. Traslado | Compromiso, tránsito y recepción. | CI-08-038, 039, 040 y 041. |
