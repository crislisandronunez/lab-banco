# Contratos de integración — Módulo 7. Tarjetas

## 1. Propósito y alcance

Este documento define los intercambios proporcionados por:

- Emisión de tarjetas.

- Autorizaciones.

- Procesamiento de consumos.

- Cuenta de tarjeta.

- Disputas de tarjetas.

Comprende instrumentos de débito y crédito, principales y adicionales; autorizaciones; presentaciones; cuotas y planes;
resúmenes; pagos; financiación rotativa; disputas y obligaciones de red.

Redes, comercios, administradores y dispositivos externos son participantes simulados.

La cuenta bancaria de débito, la cuenta de tarjeta de crédito y el instrumento son objetos diferentes.

## 2. Catálogo de contratos

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-07-001 | Constituir cuenta de tarjeta de crédito | Cuenta de tarjeta | Solicitud |
| CI-07-002 | Emitir tarjeta principal o adicional | Emisión de tarjetas | Solicitud |
| CI-07-003 | Registrar entrega, activar y administrar credenciales | Emisión de tarjetas | Solicitud |
| CI-07-004 | Consultar instrumento y comprobar habilitación | Emisión de tarjetas | Consulta |
| CI-07-005 | Modificar habilitaciones y sublímites de uso | Emisión de tarjetas | Solicitud |
| CI-07-006 | Bloquear, renovar, reemplazar o cancelar | Emisión de tarjetas | Solicitud |
| CI-07-007 | Modificar límites de crédito | Cuenta de tarjeta | Solicitud |
| CI-07-008 | Autorizar operación de débito | Autorizaciones | Solicitud |
| CI-07-009 | Autorizar operación de crédito | Autorizaciones | Solicitud |
| CI-07-010 | Comprometer capacidad de crédito | Cuenta de tarjeta | Solicitud |
| CI-07-011 | Ajustar, anular o comprobar vencimiento | Autorizaciones | Solicitud |
| CI-07-012 | Modificar o liberar compromiso de crédito | Cuenta de tarjeta | Solicitud |
| CI-07-013 | Consultar autorización | Autorizaciones | Consulta |
| CI-07-014 | Procesar presentación de débito | Procesamiento de consumos | Solicitud |
| CI-07-015 | Procesar presentación de crédito | Procesamiento de consumos | Solicitud |
| CI-07-016 | Incorporar consumo en cuenta de tarjeta | Cuenta de tarjeta | Solicitud |
| CI-07-017 | Calcular cuotas o plan financiado | Cuenta de tarjeta | Solicitud |
| CI-07-018 | Constituir o modificar distribución de plan | Cuenta de tarjeta | Solicitud |
| CI-07-019 | Procesar anulación o devolución | Procesamiento de consumos | Solicitud |
| CI-07-020 | Aplicar devolución en cuenta de tarjeta | Cuenta de tarjeta | Solicitud |
| CI-07-021 | Consultar presentación o actuación de red | Procesamiento de consumos | Consulta |
| CI-07-022 | Cerrar y emitir resumen | Cuenta de tarjeta | Solicitud |
| CI-07-023 | Incorporar evidencia de entrega de resumen | Cuenta de tarjeta | Solicitud |
| CI-07-024 | Consultar posición, resumen, total y mínimo | Cuenta de tarjeta | Consulta |
| CI-07-025 | Preparar aplicación de pago | Cuenta de tarjeta | Solicitud |
| CI-07-026 | Confirmar o cancelar preparación de pago | Cuenta de tarjeta | Solicitud |
| CI-07-027 | Coordinar débito automático | Cuenta de tarjeta | Solicitud |
| CI-07-028 | Devengar rotativo y determinar mora | Cuenta de tarjeta | Solicitud |
| CI-07-029 | Preparar pago con conversión | Cuenta de tarjeta | Solicitud |
| CI-07-030 | Recibir e investigar disputa | Disputas de tarjetas | Solicitud |
| CI-07-031 | Aplicar protección de disputa | Cuenta de tarjeta | Solicitud |
| CI-07-032 | Resolver disputa y reconsideración | Disputas de tarjetas | Solicitud |
| CI-07-033 | Ejecutar efectos económicos de disputa | Procesamiento de consumos | Solicitud |
| CI-07-034 | Consultar disputa | Disputas de tarjetas | Consulta |
| CI-07-035 | Intercambiar y conciliar registros de red | Procesamiento de consumos | Solicitud |
| CI-07-036 | Determinar y consultar obligaciones de red | Procesamiento de consumos | Solicitud y consulta |
| CI-07-037 | Incorporar liquidación de red | Procesamiento de consumos | Solicitud |
| CI-07-038 | Corregir presentación o actuación de red | Procesamiento de consumos | Solicitud |
| CI-07-039 | Corregir deuda, liquidaciones o imputaciones | Cuenta de tarjeta | Solicitud |
| CI-07-040 | Cerrar cuenta de tarjeta | Cuenta de tarjeta | Solicitud |
| CI-07-041 | Consultar actuación de cuenta de tarjeta | Cuenta de tarjeta | Consulta |
| CI-07-042 | Comunicar cambio de instrumento | Emisión de tarjetas | Evento |
| CI-07-043 | Comunicar cambio de autorización | Autorizaciones | Evento |
| CI-07-044 | Comunicar presentación, devolución o efecto de red | Procesamiento de consumos | Evento |
| CI-07-045 | Comunicar cambio de capacidad o deuda | Cuenta de tarjeta | Evento |
| CI-07-046 | Comunicar resumen, vencimiento o pago | Cuenta de tarjeta | Evento |
| CI-07-047 | Comunicar cambio de disputa | Disputas de tarjetas | Evento |
| CI-07-048 | Comunicar obligación o liquidación de red | Procesamiento de consumos | Evento |

## 3. Convenciones comunes

### 3.1. Referencias

Las actuaciones identifican operación, actuación, solicitante, causa y objeto.

Según corresponda, incluyen instrumento, portador, responsable contractual, cuenta, comercio, referencia de red,
autorización, presentación, consumo, plan, moneda, reserva, compromiso, pago, devolución y disputa.

Se distinguen:

- Mensaje de transporte.

- Operación económica.

- Intento de autorización.

- Decisión de autorización.

- Afectación de contador.

- Reserva monetaria.

- Compromiso crediticio.

- Presentación.

- Efecto ejecutado.

Cambiar una comunicación no crea otra compra.

Misma actuación y contenido recupera el resultado. Otro contenido produce conflicto.

### 3.2. Autoridades

| Información o recurso | Responsable |
| --- | --- |
| Instrumento, portador y credenciales | Emisión de tarjetas |
| Coordinación de configuración del instrumento | Emisión de tarjetas |
| Decisión de autorización | Autorizaciones |
| Contadores operativos | Autorizaciones |
| Reservas y movimientos de débito | Fondos |
| Límite general y sublímites crediticios | Cuenta de tarjeta |
| Compromisos y deuda de crédito | Cuenta de tarjeta |
| Presentaciones y obligaciones de red | Procesamiento de consumos |
| Decisión crediticia | Evaluación crediticia |
| Exposición y límites compartidos | Riesgos |
| Investigación y resolución de disputas | Disputas de tarjetas |
| Conversión | Cambio de moneda |
| Liquidación externa | Liquidaciones de tesorería |

Autorizaciones no mantiene una segunda disponibilidad monetaria o crediticia.

Procesamiento de consumos no conserva otra deuda por consumos registrados en Cuenta de tarjeta.

### 3.3. Límites y contadores

Cada límite identifica:

- Identidad y versión.

- Clase.

- Propietario.

- Alcance.

- Actos admitidos.

- Moneda.

- Importe.

- Período cuando corresponda.

- Vigencia.

- Fundamento.

Se distinguen:

| Clase | Control |
| --- | --- |
| Crediticio general | Capacidad de la cuenta por moneda |
| Sublímite crediticio | Parte de la capacidad común admitida para un adicional o modalidad |
| Operativo por operación | Máximo para un uso concreto |
| Operativo por período | Acumulación de usos dentro de un intervalo definido |

Cuenta de tarjeta afecta conjuntamente el límite general y los sublímites crediticios exigibles.

Autorizaciones afecta conjuntamente los contadores operativos exigibles.

Un sublímite no amplía el límite común.

Los períodos de acumulación tienen inicio, final exclusivo y zona definidos. No se admiten límites periódicos sin
intervalo interpretable.

### 3.4. Monedas y capital

Las posiciones se mantienen separadas en ARS y USD.

Las cuotas comprometen el capital completo. Los intereses futuros se identifican separadamente.

Transformar compromiso en consumo no utiliza dos veces la capacidad.

Reducir límites no elimina deuda ni compromisos firmes o inciertos. La cobertura protegida no habilita nuevas compras.

### 3.5. Contadores operativos

Cada autorización conserva las afectaciones de sus contadores y el período original de admisión.

La afectación distingue pendiente, ejecutado y liberado.

Una presentación transforma la parte pendiente en ejecutada una sola vez.

Cambiar de día o mes no elimina las afectaciones pendientes del período anterior.

Una devolución no restituye automáticamente consumo histórico de un contador operativo. La restitución de capacidad
crediticia pertenece a Cuenta de tarjeta.

### 3.6. Recuperación

Las autorizaciones de compra tienen siete días corridos de vigencia.

Su vencimiento inicia consulta y resolución. No libera automáticamente coberturas ni contadores pendientes.

La incertidumbre se consulta con referencias originales.

La extracción conserva el resultado de Operaciones de efectivo. Una autorización o presentación no demuestra entrega
física.

### 3.7. Cálculos y calendario

La zona es America/Argentina/Buenos_Aires.

Los cálculos intermedios conservan al menos doce decimales. Los importes finales utilizan la unidad mínima y HALF_UP,
salvo el mínimo, que se redondea hacia arriba sin superar el total.

El cierre contractual se establece entre los días 1 y 28.

El vencimiento ordinario es veinte días corridos después del cierre, ajustado al siguiente hábil.

La entrega debe anticipar al menos cinco días corridos al vencimiento efectivo.

### 3.8. Correcciones

Las correcciones conservan original, causa, evidencia y efectos relacionados.

Los ajustes manuales y resoluciones de disputas requieren preparación administrativa y aprobación separadas.

Los recorridos de devolución y disputa comparten referencias de regularización para impedir restituciones duplicadas.

## 4. Preparación y decisión de autorización

### 4.1. Intento

Autorizaciones conserva un intento con:

- Identificador estable.

- Actuación y operación.

- Contenido exacto.

- Instrumento y versiones.

- Actores y objetos.

- Preparaciones exigibles.

- Afectaciones de contadores.

- Reserva o compromiso crediticio.

- Límite de decisión.

- Decisión y resultado.

- Pendientes de regularización.

El intento distingue preparación, cobertura pendiente, listo para decidir, confirmado, abortado y recuperación
requerida.

### 4.2. Secuencia

Autorizaciones:

- Registra el intento.

- Verifica contenido y controles.

- Obtiene las preparaciones exigibles.

- Afecta sus contadores operativos.

- Solicita reserva a Fondos o compromiso a Cuenta de tarjeta.

- Recupera el resultado si se pierde la respuesta.

- Confirma localmente la autorización con sus referencias.

- Comunica la decisión y conserva seguimiento.

La respuesta favorable exige cobertura financiera confirmada.

La preparación de una autoridad y el compromiso de recursos son registros diferentes.

### 4.3. Participantes

Intervienen únicamente las autoridades necesarias:

- Emisión de tarjetas, por habilitación.

- Cuentas, por capacidades de débito.

- Representación, cuando corresponda.

- Restricciones.

- Institución, para actuaciones internas exigibles.

- Otros responsables del fundamento de negocio.

Cada participante conserva su autoridad.

Cuenta de tarjeta y Fondos aplican su propio protocolo cuando sus decisiones requieren condiciones externas preparadas.

### 4.4. Aborto y regularización

El aborto impide confirmar la autorización con ese intento.

Si una reserva o compromiso ya fue constituido, el intento conserva la actuación necesaria para regularizarlo.

No se informa liberación completa hasta comprobar el resultado remoto.

Los contadores no se liberan mientras exista una cobertura remota incierta o una actuación que todavía pueda ejecutarse.

Una confirmación remota recuperada después del aborto no reactiva la autorización: se regulariza su cobertura con la
referencia original.

### 4.5. Cambios concurrentes

Un bloqueo, revocación o reducción incompatible impide nuevas preparaciones y resuelve las existentes frente a la
decisión.

Una autorización confirmada no se elimina retroactivamente. Su anulación y sus compromisos siguen CI-07-011.

Los cambios posteriores no convierten automáticamente una presentación anterior válida en una compra nueva.

### 4.6. Interfaces técnicas

| Interfaz | Finalidad |
| --- | --- |
| GET /card-authorization-attempts/{id} | Consultar decisión y pendientes |
| POST /card-authorization-attempts/{id}/abort-requests | Solicitar aborto |

La petición identifica actuación, intento, causa y preparación afectada cuando corresponda.

El resultado distingue aborto confirmado, autorización ya confirmada y resolución pendiente.

Una referencia desconocida no demuestra aborto ni ausencia definitiva de ejecución.

### 4.7. Vencimiento

El límite impide nuevas confirmaciones tardías conforme al control del coordinador.

Las autoridades no liberan preparaciones por timeout. Solicitan resolver el intento.

La indisponibilidad mantiene seguimiento y escalación.

La expiración de un trabajador no cancela la operación.

## 5. Contratos de Emisión de tarjetas

### CI-07-002. Emitir tarjeta principal o adicional

Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: contrato, modalidad, portador, responsable, cuenta, capacidades, vencimiento y autorización.

Salida: instrumento, vínculos, estado y entrega pendiente.

Reglas: emitir no activa. La adicional comparte capacidad y no crea deuda independiente. Una tarjeta de débito no
concede titularidad bancaria.

Aceptación: la repetición recupera el instrumento; una adicional no genera otro límite general.

### CI-07-003. Registrar entrega, activar y administrar credenciales

Consumidor: Gestión administrativa.

Entrada: instrumento, actuación, destinatario, identidad, facultades y evidencia de entrega o recuperación.

Salida: entrega, activación y resultado de credenciales.

Reglas: entrega y activación son distintas. Los datos impresos no bastan para activar. Una credencial sustituida deja de
habilitar nuevos usos.

Las respuestas y comprobantes no exponen secretos.

### CI-07-004. Consultar instrumento y comprobar habilitación

Consumidores: Autorizaciones, Procesamiento de consumos, Cuenta de tarjeta, Disputas y Gestión administrativa.

Entrada: instrumento; para evaluar uso, operación, canal, modalidad, importe, moneda y momento.

Salida: portador, vínculo, estado, vigencia, capacidades, configuración aplicada y resultado informativo.

Reglas: no reserva recursos ni crea preparación.

Una presentación anterior se analiza con sus antecedentes, sin reactivar el instrumento.

Aceptación: habilitación favorable no demuestra disponibilidad financiera.

### CI-07-005. Modificar habilitaciones y sublímites

Consumidor: Gestión administrativa.

Entrada: instrumento, versión, cambios, clase de límite, alcance, moneda, importe, período si corresponde, vigencia y
autorización.

Actuación:

- Emisión modifica sus habilitaciones.

- Solicita a Autorizaciones la aplicación de límites operativos.

- Solicita a Cuenta de tarjeta la aplicación de sublímites crediticios.

- Conserva versiones y resultados por componente.

Salida: configuración propia, componentes aplicados, versiones efectivas y pendientes.

Reglas:

- No informa una configuración como aplicada sin confirmación del propietario.

- Una aplicación parcial se identifica expresamente.

- No permite elegir condiciones pendientes más favorables que las efectivas.

- Una reducción no elimina afectaciones existentes.

- Un aumento de sublímite no concede más crédito.

- Los cambios incompatibles resuelven intentos preparados.

Las operaciones técnicas de aplicación y consulta de configuración en Autorizaciones reciben la actuación original,
versión de configuración y contenido tipado.

Su recuperación no reinicia contadores.

Aceptación: si Cuenta de tarjeta no confirmó el sublímite, Emisión lo informa pendiente.

### CI-07-006. Bloquear, renovar, reemplazar o cancelar

Consumidores: Gestión administrativa y Prevención de fraude en su competencia.

Entrada: instrumento, actuación, causa, momento, solicitante y alcance.

Salida: bloqueo o cancelación, coordinación pendiente y sucesor.

Reglas:

- Recibe denuncias permanentemente.

- Impide nuevas preparaciones incompatibles.

- Resuelve intentos en curso.

- El sucesor no reutiliza credenciales comprometidas.

- No elimina consumos, cuotas, disputas ni compromisos anteriores.

- La cancelación de una principal identifica el tratamiento de adicionales.

### 5.1. Preparación explícita del instrumento

Emisión ofrece operaciones técnicas para preparar el uso, consultar la preparación e incorporar la decisión verificable
del coordinador.

La solicitud identifica intento, instrumento, acto, contenido, actores y límite.

La preparación protege únicamente la habilitación de su competencia.

No sustituye CI-07-004 ni concede fondos o crédito.

## 6. Constitución y capacidad crediticia

### CI-07-001. Constituir cuenta de tarjeta

Proveedor: Cuenta de tarjeta. Consumidor: Solicitudes de productos.

Entrada: contrato, responsables, aprobación, límites por moneda, condiciones, modalidades y cierre.

Actuación: confirma aplicación crediticia mediante CI-06-006 y coordina exposición con Riesgos.

Salida: cuenta, límites, condiciones, cierre y pendientes.

Aceptación: límite 1.000 sin consumos mantiene deuda cero. Sin aprobación USD no habilita esa moneda. La repetición
recupera la misma cuenta.

### CI-07-007. Modificar límites crediticios

Proveedor: Cuenta de tarjeta. Consumidores: Solicitudes de productos, Gestión administrativa y Emisión para
configuración autorizada de sublímites.

Entrada: cuenta, versión, clase, alcance, moneda, importe, vigencia y decisiones aplicables.

Salida: límite general, sublímites, deuda, compromisos, protección, exceso y capacidad nueva.

Reglas:

- Los aumentos de crédito requieren Evaluación crediticia y Riesgos.

- Los sublímites se mantienen dentro de su fundamento.

- La reducción no borra deuda ni compromisos firmes o inciertos.

- El propietario confirma aplicación y versión.

- Los cambios se ordenan con compromisos concurrentes.

Aceptación: reducir por debajo de lo comprometido deja capacidad nueva cero sin liberar obligaciones.

### CI-07-010. Comprometer capacidad de crédito

Proveedor: Cuenta de tarjeta. Consumidor: Autorizaciones.

Entrada: cuenta, instrumento, intento, autorización, capital, moneda, modalidad, plan y configuración crediticia
aplicable.

Actuación: comprueba y afecta conjuntamente límite general y todos los sublímites crediticios exigibles.

Salida: compromiso, afectaciones por límite, capital, moneda, condiciones y capacidad restante.

Reglas:

- La decisión es indivisible dentro de Cuenta de tarjeta.

- Los límites no se aceptan como valores libres impuestos por el solicitante.

- Cuotas comprometen capital total.

- Los adicionales consumen capacidad común.

- La identidad impide duplicar compromisos por reenvío.

Aceptación: compra 600 en cuotas compromete 600. Si falla un sublímite exigible, no confirma solo el límite general.

### CI-07-012. Modificar o liberar compromiso

Proveedor: Cuenta de tarjeta. Consumidor: Autorizaciones.

Entrada: compromiso, versión, modalidad, importe, causa y evidencia de presentación o cancelación.

Salida: afectaciones actualizadas, liberación o mantenimiento por incertidumbre.

Reglas: actualiza conjuntamente los límites afectados. Aumentar 100 a 120 exige capacidad adicional 20.

No libera capital transformado en deuda ni cobertura de ejecución desconocida.

## 7. Contratos de Autorizaciones

### CI-07-008. Autorizar débito

Consumidores: Participantes simulados y Pagos cuando utiliza el instrumento.

Entrada: instrumento, operación, comercio o dispositivo, importe, moneda, modalidad, autenticación y conversión
identificada.

Actuación: aplica el protocolo, afecta contadores operativos y obtiene reserva de Fondos.

Salida: autorización con reserva confirmada, rechazo o seguimiento pendiente.

Reglas:

- Fraude favorable no sustituye fondos.

- Descubierto requiere admisión.

- La extracción utiliza la misma reserva.

- No responde favorablemente con reserva incierta.

- Los contadores conservan su período y referencia.

### CI-07-009. Autorizar crédito

Consumidores: Participantes simulados y Pagos.

Entrada: instrumento, operación, comercio, capital, moneda, modalidad, autenticación y plan.

Actuación: prepara condiciones, afecta contadores y solicita CI-07-010.

Salida: autorización, compromiso, vigencia y condiciones; rechazo o incertidumbre.

Reglas: no reconoce deuda presentada. Una financiación fuera del alcance aprobado exige evaluación.

La respuesta favorable requiere compromiso confirmado.

### CI-07-011. Ajustar, anular o comprobar vencimiento

Consumidores: Participantes simulados, Procesamiento de consumos y Gestión administrativa.

Entrada: autorización, actuación, importe, presentaciones y evidencia de cierre o ausencia de ejecución.

Salida: autorización, presentado, remanente, coberturas, contadores y pendientes.

Reglas:

- Coordina recursos financieros y contadores operativos.

- Una presentación parcial transforma solo la parte ejecutada.

- La parte no final permanece pendiente.

- Una presentación final permite regularizar el remanente no ejecutable.

- El vencimiento de siete días inicia comprobación.

- La incertidumbre mantiene cobertura.

- Una actualización tardía no repite el uso del contador.

Aceptación: autorización 100 y presentación final 60 deja uso ejecutado 60 y libera el pendiente 40 solo con cierre
comprobado.

### CI-07-013. Consultar autorización

Consumidores: Procesamiento, Disputas, Pagos, Operaciones de efectivo y Gestión administrativa.

Entrada: autorización o referencia completa.

Salida: intento, decisión, contenido, controles, contadores, período, reserva o compromiso, presentaciones, ajustes y
pendientes.

Reglas: no reintenta la compra ni demuestra entrega física o liquidación.

## 8. Presentaciones, consumos y planes

### CI-07-014. Procesar presentación de débito

Proveedor: Procesamiento de consumos. Consumidores: Red simulada y Gestión administrativa autorizada.

Entrada: presentación, operación, autorización, importe, moneda, comercio o dispositivo, fechas y evidencia.

Actuación: valida correspondencia y consume reserva mediante Fondos.

En extracción, consulta el resultado físico y reconoce el débito existente.

Salida: presentación, movimiento, obligación de red, actualización de autorización e incidencias.

Reglas: una repetición no debita. El exceso requiere cobertura o resolución fundada. Una obligación de red sin derecho a
cargo se trata separadamente.

La actualización de contadores mantiene seguimiento sin repetir el débito confirmado.

### CI-07-015. Procesar presentación de crédito

Proveedor: Procesamiento de consumos. Consumidores: Red simulada y Gestión administrativa autorizada.

Entrada: presentación, autorización, capital, moneda, fechas y condiciones aceptadas.

Actuación: incorpora consumo mediante CI-07-016 y conserva obligación de red.

Salida: consumo, ciclo, transformación, actualización de autorización y pendientes.

Reglas: no cambia silenciosamente cuotas. Una falla externa no elimina una deuda válida.

### CI-07-016. Incorporar consumo

Proveedor: Cuenta de tarjeta. Consumidor: Procesamiento de consumos.

Entrada: presentación validada, autorización, compromiso, capital, moneda, fecha y condiciones.

Actuación: transforma compromiso en capital presentado y clasifica ciclo o plan.

Salida: consumo, capital, remanente y capacidad.

Reglas:

- Compromiso 100 a consumo 100 no utiliza otros 100 de capacidad.

- La transformación conserva general y sublímites crediticios.

- La repetición recupera el consumo.

- La falta de compromiso exige resolución documentada.

- Un bloqueo posterior del instrumento no rechaza por sí solo una presentación anterior válida.

### CI-07-017. Calcular cuotas o plan

Proveedor: Cuenta de tarjeta. Consumidores: Autorizaciones, Procesamiento, Contratos y Gestión administrativa.

Entrada: capital, moneda, modalidad, cuotas, tasa, cargos, tributos, fechas y calendario.

Salida: determinación, distribución, intereses, cronograma, flujos, CFT y supuestos.

Reglas:

- Cuotas sin interés ajustan residuo en la última.

- Planes del banco utilizan sistema francés.

- Período irregular conforme a la especificación funcional.

- CFT anual efectivo con flujos y fechas reales, base 365.

- Solo cargos atribuibles al plan.

- Un costo obligatorio desconocido impide CFT definitivo.

Calcular no constituye plan ni crea deuda.

### CI-07-018. Constituir o modificar plan

Proveedor: Cuenta de tarjeta. Consumidores: Procesamiento, Contratos y Gestión administrativa.

Entrada: determinación, aceptación, capital seleccionado, moneda y actuación.

Salida: plan, capital, cuotas, interés futuro, CFT y clasificación sustituida.

Reglas: conserva origen sin nueva compra ni segundo compromiso. No incorpora automáticamente intereses, comisiones o
tributos al capital.

No solapa interés de plan y rotativo sobre el mismo capital y período.

### CI-07-019. Procesar anulación o devolución

Proveedor: Procesamiento de consumos. Consumidores: Red simulada, Pagos y Gestión administrativa.

Entrada: original, importe, moneda, causa, evidencia y regularizaciones previas.

Salida: liberación o devolución, recuperable y efectos.

Reglas:

- Anular autorización no presentada libera, no acredita.

- Devolver consumo produce crédito o reducción de deuda.

- No supera recuperable considerando devoluciones y disputas comprometidas.

- No convierte moneda sin conversión válida.

- No restaura automáticamente contadores históricos.

### CI-07-020. Aplicar devolución

Proveedor: Cuenta de tarjeta. Consumidor: Procesamiento de consumos.

Entrada: consumo, devolución confirmada, importe, moneda y tratamiento relacionado.

Salida: capital, cronograma, ajustes, saldo a favor y capacidad.

Reglas: reduce cuotas futuras desde las últimas, después capital facturado y trata capital pagado como saldo a favor.

Recalcula intereses futuros y evita duplicar restituciones provisionales.

Aceptación: consumo 100 sin pagos y devolución 40 deja capital 60.

### CI-07-021. Consultar presentación o red

Proveedor: Procesamiento de consumos. Consumidores: Autorizaciones, Cuenta de tarjeta, Disputas, Pagos, Tesorería y
Gestión administrativa.

Entrada: presentación, operación, devolución o actuación.

Salida: contenido, validación, efectos, obligación, intercambios, diferencias y pendientes.

No vuelve a cargar ni enviar.

## 9. Resúmenes, pagos y financiación

### CI-07-022. Cerrar y emitir resumen

Proveedor: Cuenta de tarjeta. Consumidor: Gestión administrativa; también rige el proceso periódico.

Entrada: cuenta, ciclo y corte.

Actuación: reúne hechos confirmados, liquida conceptos, aplica protecciones y determina exigible.

Salida: resumen, versión, composición, total, mínimo, cierre, vencimiento y entrega.

Reglas: autorizaciones y cuotas futuras quedan separadas del exigible. El cierre se ordena localmente frente a nuevas
presentaciones.

Repetir el ciclo recupera su resultado.

### CI-07-023. Incorporar entrega de resumen

Proveedor: Cuenta de tarjeta. Consumidores: Comunicaciones y Gestión administrativa autorizada.

Entrada: resumen y versión, destinatario, entrega, momento y evidencia.

Salida: entrega, vencimiento efectivo, incidencia y actualizaciones.

Reglas: sin cinco días corridos de anticipación se ajusta el vencimiento. Conserva cierre y corrige consecuencias
atribuibles a la demora.

Un envío no acreditado no se informa como entrega.

### CI-07-024. Consultar posición, total y mínimo

Proveedor: Cuenta de tarjeta. Consumidores: Pagos, Mandatos, Evaluación crediticia, Riesgos, Cobranzas, Contratos y
Gestión administrativa.

Entrada: cuenta, moneda, ciclo o corte y alcance.

Salida: límites, sublímites crediticios, compromisos, capital, cuotas, intereses, cargos, tributos, protecciones, saldo
a favor, total y mínimo.

Sobre conceptos no protegidos:

$T = P + C$

$M = \min_{}\left( T,C + K + 0,10(P - K) \right)$

Donde:

- $P$: capital facturado pendiente.

- $K$: parte de ese capital correspondiente a mínimos anteriores vencidos.

- $C$: intereses, comisiones y tributos exigibles pendientes.

- $0 \leq K \leq P$.

El mínimo se redondea hacia arriba sin superar el total.

Aceptación: $P = 1.000$, $K = 100$ y $C = 20$ producen total 1.020 y mínimo 210. $K$ no se agrega nuevamente como deuda.

### CI-07-025. Preparar aplicación de pago

Proveedor: Cuenta de tarjeta. Consumidores: Pagos, Cobranzas y Gestión administrativa.

Entrada: cuenta, pagador, moneda, importe, fecha, referencia y modalidad.

Salida: preparación, distribución, versión, mínimo y excedente previsto.

Reglas: considera otras preparaciones. Atiende obligaciones vencidas más antiguas y luego las del ciclo; dentro de
ellas, intereses exigibles y capital, después comisiones y tributos asociados.

No demuestra ingreso ni cancela deuda.

### CI-07-026. Confirmar o cancelar preparación de pago

Proveedor: Cuenta de tarjeta. Consumidores: Pagos, Cobranzas y Gestión administrativa.

Entrada: preparación, evidencia de ingreso o cancelación comprobada, importe, moneda y fecha.

Salida: imputación, deuda, mínimo, capacidad restituida y saldo a favor.

Reglas: aplica una vez. Cancelar exige ausencia de ejecución pendiente. Pagar capital restituye capacidad dentro de
límites vigentes.

Pagar el total no precancela cuotas futuras.

### CI-07-027. Coordinar débito automático

Proveedor: Cuenta de tarjeta. Consumidores: Mandatos y Gestión administrativa.

Entrada: resumen, mandato, modalidad, ocurrencia y fecha.

Salida: importe actualizado, ocurrencia, pago, ejecución y aplicación.

Reglas:

- Mandatos origina la ocurrencia única.

- Pagos ejecuta.

- Cuenta de tarjeta imputa.

- Ejecución ordinaria a las 09:00 del vencimiento.

- Respeta el máximo autorizado.

- Insuficiencia rechaza el importe completo.

- No hay parcialidades ni reintentos ocultos.

- Los excedentes concurrentes se reconocen y regularizan.

- Una reversión conserva referencia al pago.

### CI-07-028. Devengar rotativo y determinar mora

Proveedor: Cuenta de tarjeta. Consumidores: Riesgos, Cobranzas y Gestión administrativa.

Entrada: moneda, período, vencimiento, capital, pagos, protecciones y tasa.

Salida: bases diarias, interés, mínimo incumplido y ajustes.

Reglas: interés simple Actual/365 desde el día siguiente al vencimiento. No calcula sobre intereses, comisiones o
tributos ni sobre cuotas futuras.

Una cuota transferida a rotativo deja de generar interés de plan por el mismo período.

Aceptación: capital 1.000 por diez días al 36,5 % produce 10. Cumplir mínimo evita su mora aunque continúe financiación.

### CI-07-029. Preparar pago con conversión

Proveedor: Cuenta de tarjeta. Consumidores: Pagos y Gestión administrativa.

Entrada: deuda, moneda, importe, moneda de pago, pagador e instrucción.

Salida: conversión identificada, cotización, vigencia, origen, cargos y destino.

Reglas: requiere aceptación válida. Un equivalente informativo no sustituye deuda USD por ARS.

Preparar no ejecuta conversión ni cancela deuda. Una ejecución incierta se recupera antes de pedir otra conversión.

## 10. Disputas

### CI-07-030. Recibir e investigar

Proveedor: Disputas de tarjetas. Consumidores: Gestión administrativa y Reclamos.

Entrada: solicitante, facultades, operación, conceptos, importe, motivo, recepción y evidencia.

Salida: caso, constancia, plazos, protección y requisitos.

Reglas: acuse el mismo día; respuesta fundada en quince días corridos. Investigación exterior adicional puede extenderse
hasta sesenta, con motivo comunicado.

Cuestionamientos posteriores a treinta días del resumen se reciben como reclamos sin rechazo automático.

### CI-07-031. Aplicar protección

Proveedor: Cuenta de tarjeta. Consumidor: Disputas.

Entrada: causa, conceptos, importe, período y etapa.

Salida: protección, posición, total, mínimo y ajustes.

Reglas: suspende exigibilidad e intereses de crédito alcanzados. No elimina capital ni restituye capacidad
automáticamente.

Cada causa se mantiene separada.

La protección continúa durante disconformidad y reconsideración oportunas.

En débito no produce devolución por sí sola.

### CI-07-032. Resolver y reconsiderar

Proveedor: Disputas. Consumidor: Gestión administrativa.

Entrada: evidencia, propuesta, aprobación y disconformidad.

Salida: resolución, motivos, efectos y plazos.

Reglas: siete días corridos para disconformidad y diez hábiles para resolver reconsideración oportuna.

No exige interés retroactivo del período protegido.

Los conceptos que vuelven a exigirse integran un resumen posterior con entrega y vencimiento propios.

### CI-07-033. Ejecutar efectos de disputa

Proveedor: Procesamiento de consumos. Consumidor: Disputas.

Entrada: resolución o provisional aprobado, consumo, importe, moneda y efectos.

Salida: restitución, deuda regularizada, contracargo, recupero y pendientes.

Reglas: el favorable no espera recupero del comercio.

Provisional 100 y definitivo 100 no generan crédito 200.

Los efectos del cliente y de la red conservan referencias separadas y vinculadas.

### CI-07-034. Consultar disputa

Proveedor: Disputas. Consumidores: Cuenta de tarjeta, Procesamiento, Fraude, Reclamos y Gestión administrativa.

Entrada: caso u operación y alcance.

Salida: investigación, protección, decisiones, reconsideración, efectos y recuperos.

No levanta protección ni ejecuta créditos.

## 11. Red, correcciones y cierre

### CI-07-035. Intercambiar y conciliar red

Proveedor: Procesamiento de consumos. Consumidores: Red simulada y Gestión administrativa.

Entrada: participante, mecanismo, acuerdo, mensajes o archivos, ciclo, cortes y evidencia.

Salida: intercambios, conciliación, diferencias y pendientes.

Reglas: acuse no demuestra ejecución. Una diferencia no autoriza un débito. Los mensajes contradictorios se investigan.

La conciliación de tarjetas no se duplica en Compensación y conciliación de pagos.

### CI-07-036. Determinar y consultar obligaciones

Proveedor: Procesamiento de consumos. Consumidores: Tesorería, Contabilidad y Gestión administrativa.

Entrada: ciclo, participante, moneda, acuerdo, hechos y ajustes; para consulta, obligación o ciclo.

Salida: dirección, importe, vencimiento, composición y ajustes.

Reglas: corte a las 18:00. Los hechos desde ese instante integran el siguiente intervalo.

El neteo exige acuerdo. No compensa autorizaciones como consumos.

Aceptación: obligaciones 100 y devoluciones 30 producen neto 70 si el acuerdo lo permite.

### CI-07-037. Incorporar liquidación de red

Proveedor: Procesamiento de consumos. Consumidor: Liquidaciones de tesorería.

Entrada: obligación, liquidación, importe, moneda, fecha, resultado y evidencia.

Salida: cumplimiento, parcialidad admitida, remanente o incertidumbre.

Reglas: no reduce dos veces la obligación. Una falla externa no vuelve impago un pago aplicado ni genera otro cargo.

### CI-07-038. Corregir presentación o red

Proveedor: Procesamiento de consumos. Consumidores: Disputas y Gestión administrativa.

Entrada: original, causa, evidencia, corrección y aprobación exigible.

Salida: efectos, diferencias y pendientes.

Reglas: conserva original y utiliza al propietario de dinero, deuda, autorización y obligación.

No crea deuda por una diferencia de red sin fundamento.

### CI-07-039. Corregir deuda o imputación

Proveedor: Cuenta de tarjeta. Consumidores: Pagos, Procesamiento, Disputas y Gestión administrativa.

Entrada: original, error o reversión, evidencia, fecha y autorización.

Salida: posición, capacidad, cronograma, mínimo, interés, mora y documentos corregidos.

Reglas: redistribuir pago 100 no cobra otros 100. La reversión no duplica restitución. Corregir CFT informativo no crea
dinero.

### CI-07-040. Cerrar cuenta de tarjeta

Proveedor: Cuenta de tarjeta. Consumidores: Contratos, Solicitudes de productos y Gestión administrativa.

Entrada: cuenta, causa, terminación, instrumentos y evidencia.

Salida: cierre o régimen residual, capacidad para nuevos usos, obligaciones y pendientes.

Reglas:

- Recibe baja con deuda.

- Impide nuevos compromisos incompatibles.

- Resuelve intentos y configuraciones pendientes.

- No acelera cuotas.

- Conserva presentaciones anteriores válidas y disputas.

- No libera cobertura incierta.

- Restituye saldos a favor por destino autorizado.

- No prolonga cargos por demoras internas.

### CI-07-041. Consultar actuación de cuenta

Proveedor: Cuenta de tarjeta. Consumidores: Originantes, Riesgos, Cobranzas, Reclamos y Gestión administrativa.

Entrada: actuación de constitución, límite, compromiso, consumo, plan, resumen, pago, corrección o cierre.

Salida: contenido, versiones, decisiones, efectos y pendientes.

Permite recuperar sin repetir crédito, deuda o cobro.

## 12. Eventos

Todos identifican hecho, emisor, objeto, versión o secuencia, actuación, causa, fechas y pendientes.

| Contrato | Emisor | Contenido y garantía |
| --- | --- | --- |
| CI-07-042 | Emisión | Instrumento, vínculo, configuración, activación, bloqueo o sucesor |
| CI-07-043 | Autorizaciones | Intento, decisión, contadores, período, cobertura, ajustes y pendientes |
| CI-07-044 | Procesamiento | Presentación, devolución, efectos y obligación externa |
| CI-07-045 | Cuenta de tarjeta | Límites, sublímites, compromisos, deuda, capacidad y exceso |
| CI-07-046 | Cuenta de tarjeta | Resumen, entrega, vencimiento, pago e imputación |
| CI-07-047 | Disputas | Etapa, protección, resolución, plazos y ejecución |
| CI-07-048 | Procesamiento | Obligación, composición, ajustes y liquidación |

### 12.1. Consumidores

- CI-07-042: Autorizaciones, Procesamiento, Cuenta de tarjeta, Fraude, Documentos y Comunicaciones.

- CI-07-043: Procesamiento, Cuenta de tarjeta, Operaciones de efectivo, Pagos y Fraude.

- CI-07-044: Cuenta de tarjeta, Autorizaciones, Disputas, Pagos, Contabilidad y Documentos.

- CI-07-045: Autorizaciones, Riesgos, Evaluación crediticia, Cobranzas y Contabilidad.

- CI-07-046: Mandatos, Pagos, Cobranzas, Documentos, Comunicaciones y Contabilidad.

- CI-07-047: Cuenta de tarjeta, Procesamiento, Fraude, Reclamos, Documentos y Comunicaciones.

- CI-07-048: Liquidaciones de tesorería, Posiciones y liquidez y Contabilidad.

### 12.2. Reglas

Los eventos:

- Excluyen credenciales.

- No reactivan instrumentos mediante versiones antiguas.

- No repiten reservas o compromisos.

- No convierten autorización en entrega física.

- No duplican exposición al transformar compromiso.

- No representan un segundo ingreso por aplicar un pago.

- No informan reparación ejecutada cuando solo existe resolución.

- No crean otra ocurrencia por cambiar vencimiento.

- Distinguen configuración aplicada de configuración pendiente.

## 13. Integraciones consumidas

### 13.1. Institución, personas y contratación

| Contratos | Uso |
| --- | --- |
| CI-01-004 | Facultades internas |
| CI-01-005 y CI-01-008 | Producto y elegibilidad |
| CI-01-011 y CI-01-012 | Condiciones comerciales |
| CI-01-014 y CI-01-015 | Calendarios |
| CI-02-002 | Participantes |
| CI-02-015 | Relación comercial |
| CI-02-021 | Representación |
| CI-03-016 | Resultado de constitución |
| CI-03-022 | Acuerdo |

Cuando corresponde mantener condiciones hasta decidir, se utilizan las preparaciones explícitas del proveedor.

### 13.2. Cuentas y fondos

| Contratos | Uso |
| --- | --- |
| CI-04-002 y CI-04-040 | Cuenta y permisos informativos |
| CI-04-012 | Posición |
| CI-04-013 a CI-04-016 | Reservas |
| CI-04-017 | Créditos y movimientos procedentes |
| CI-04-021 a CI-04-023 | Corrección y recuperación |
| CI-04-027 y CI-04-030 | Extracción y resultado físico |

La presentación de una extracción reconoce el débito existente.

### 13.3. Pagos y financiación

| Contratos | Uso |
| --- | --- |
| CI-05-010 a CI-05-012 | Pago, devolución y recuperación |
| CI-05-013 | Mandato |
| CI-05-015 a CI-05-018 | Comprobación, programación y ocurrencia |
| CI-05-019, CI-05-020 y CI-05-039 | Reversión y seguimiento |
| CI-06-001 a CI-06-003 | Evaluación y concesión |
| CI-06-005 y CI-06-006 | Aprobación y aplicación |
| CI-06-033, CI-06-034 y CI-06-037 | Cobranza |

Cuenta de tarjeta mantiene planes, CFT, deuda e imputación. No crea un préstamo adicional por los mismos consumos.

### 13.4. Otros responsables

Riesgos administra exposición compartida; Fraude sus evaluaciones; Restricciones sus medidas; Criterios tributarios su
tratamiento.

Cambio de moneda administra conversiones.

Tesorería liquida obligaciones externas.

Contabilidad registra hechos sin duplicarlos.

Gestión administrativa, Reclamos, Documentos y Comunicaciones conservan sus respectivas actuaciones.

## 14. Correspondencia funcional

| Operación | Contratos principales |
| --- | --- |
| OF-07-01. Cuenta de tarjeta | CI-07-001, CI-07-041, CI-07-045 |
| OF-07-02. Emisión | CI-07-002, CI-07-004, CI-07-042 |
| OF-07-03. Entrega y credenciales | CI-07-003, CI-07-004, CI-07-042 |
| OF-07-04. Habilitaciones y límites | CI-07-005, CI-07-007, CI-07-010, CI-07-012, CI-07-045 |
| OF-07-05. Bloqueo y reemplazo | CI-07-006, CI-07-004, CI-07-042 |
| OF-07-06. Autorizar débito | CI-07-008, CI-07-013, CI-07-043 |
| OF-07-07. Autorizar crédito | CI-07-009, CI-07-010, CI-07-013, CI-07-043 |
| OF-07-08. Ajustar autorizaciones | CI-07-011, CI-07-012, CI-07-013, CI-07-043 |
| OF-07-09. Presentación de débito | CI-07-014, CI-07-021, CI-07-044 |
| OF-07-10. Presentación de crédito | CI-07-015, CI-07-016, CI-07-021, CI-07-044 |
| OF-07-11. Cuotas y planes | CI-07-017, CI-07-018, CI-07-024, CI-07-045 |
| OF-07-12. Devoluciones | CI-07-019, CI-07-020, CI-07-044 |
| OF-07-13. Resumen | CI-07-022, CI-07-023, CI-07-024, CI-07-046 |
| OF-07-14. Total y mínimo | CI-07-024, CI-07-022 |
| OF-07-15. Pagos | CI-07-025, CI-07-026, CI-07-046 |
| OF-07-16. Débito automático | CI-07-027, CI-07-025, CI-07-026, CI-07-046 |
| OF-07-17. Rotativo y mora | CI-07-028, CI-07-024, CI-07-045 |
| OF-07-18. Monedas | CI-07-008, CI-07-009, CI-07-014, CI-07-015, CI-07-024, CI-07-029 |
| OF-07-19. Investigar disputa | CI-07-030, CI-07-031, CI-07-034, CI-07-047 |
| OF-07-20. Resolver disputa | CI-07-032, CI-07-033, CI-07-034, CI-07-047 |
| OF-07-21. Redes | CI-07-035, CI-07-036, CI-07-037, CI-07-048 |
| OF-07-22. Correcciones | CI-07-011, CI-07-012, CI-07-038, CI-07-039, CI-07-041 |
| OF-07-23. Cancelación y cierre | CI-07-006, CI-07-040, CI-07-041, CI-07-042, CI-07-045 |

Las consultas de intentos y las actuaciones de configuración permiten recuperar decisiones y aplicaciones pendientes sin
duplicar contadores, capacidad, deuda ni efectos monetarios.
