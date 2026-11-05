## Módulo 8. Ahorro e inversiones

Este módulo administra los depósitos a plazo y las operaciones de inversión de los clientes, incluyendo constitución,
ejecución, rendimientos, tenencias, vencimientos y rescates.

Se divide en cuatro microservicios: Depósitos a plazo, Instrumentos de inversión, Órdenes de inversión y Tenencias y
valuación.

### 1. Depósitos a plazo

Descripción: administra los depósitos mediante los cuales el cliente entrega fondos al banco durante un plazo y bajo
condiciones de devolución y remuneración determinadas.

Responsabilidades:

- Constituir depósitos a partir de una contratación válida y fondos confirmados.

- Mantener titulares, capital, moneda, plazo, tasa y modalidad de remuneración.

- Administrar depósitos tradicionales, precancelables e indexados contemplados en el catálogo.

- Calcular intereses, actualizaciones, cargos y tributos aplicables.

- Mantener los importes devengados y las obligaciones de pago.

- Administrar vencimientos, instrucciones de renovación y modalidades de devolución.

- Procesar precancelaciones cuando el producto las permita.

- Coordinar el pago del capital y los rendimientos.

- Conservar la relación entre depósitos vencidos y renovaciones.

Datos propios: depósitos constituidos, referencias contractuales, titulares operativos, capital, condiciones económicas
aplicables, devengamientos, vencimientos, instrucciones de renovación y obligaciones de devolución.

Límites:

- Catálogo de productos define las modalidades ofrecidas; Contratos conserva las condiciones aceptadas.

- Fondos administra el dinero disponible antes de la constitución y después de la acreditación del pago.

- El capital constituido se mantiene como obligación en Depósitos a plazo; no permanece simultáneamente disponible en la
  cuenta de origen.

- No administra instrumentos de mercado ni inversiones propias del banco.

- Tesorería administra la liquidez necesaria para atender los vencimientos.

Relaciones:

- Contratación proporciona el acuerdo y las condiciones particulares.

- Cuentas y fondos verifica las cuentas y ejecuta las afectaciones monetarias.

- Calendarios bancarios proporciona fechas y convenciones.

- Riesgo y cumplimiento aporta controles y criterios tributarios.

- Tesorería recibe las proyecciones de vencimiento.

- Contabilidad recibe constituciones, devengamientos, pagos y ajustes.

- Atención y operación administrativa conserva certificados y comunica resultados.

Reglas y excepciones:

- Una solicitud aceptada no equivale a un depósito constituido: la afectación de fondos debe estar confirmada.

- Un resultado incierto de constitución se comprueba antes de repetir la afectación.

- Un depósito no precancelable no admite retirar anticipadamente su capital como si fuera saldo disponible.

- La precancelación aplica las fechas, condiciones y remuneración correspondientes.

- Las modalidades indexadas conservan el índice, sus valores y la fórmula utilizada.

- Una renovación requiere una instrucción válida y condiciones identificadas para el nuevo período.

- Cada renovación genera un depósito identificable vinculado al anterior.

- Si la cuenta de destino no admite la acreditación, la obligación de devolución permanece pendiente y recibe
  tratamiento expreso.

- Las correcciones conservan los cálculos y movimientos originales y sus ajustes.

### 2. Instrumentos de inversión

Descripción: administra la definición operativa de los instrumentos financieros en los que pueden invertir los clientes.

Responsabilidades:

- Identificar instrumentos, clases, emisores, administradores y participantes relevantes.

- Mantener moneda de denominación, unidades de negociación y reglas de precisión.

- Definir modalidades admitidas de suscripción, compra, venta y rescate.

- Mantener plazos de liquidación, horarios de corte, calendarios e importes mínimos.

- Registrar riesgos, restricciones y documentación informativa del instrumento.

- Mantener eventos que afecten sus derechos: intereses, amortizaciones, distribuciones y otras modificaciones.

- Administrar su habilitación o suspensión operativa y conservar versiones.

Datos propios: instrumentos, clases, identificadores, participantes referenciados, características financieras,
condiciones operativas, eventos y documentación informativa referenciada.

Límites:

- Catálogo de productos define el servicio de inversión que ofrece el banco; este servicio define los instrumentos
  concretos utilizables mediante ese servicio.

- Personas administra la identidad de emisores y demás participantes.

- Riesgo y cumplimiento determina restricciones y criterios de adecuación.

- No administra órdenes, posiciones de clientes ni precios de valuación.

- No promete rendimientos que el instrumento no garantice contractualmente.

Relaciones:

- Órdenes de inversión consulta las condiciones necesarias para aceptar y tramitar operaciones.

- Tenencias y valuación utiliza características y eventos para mantener posiciones.

- Calendarios bancarios proporciona referencias temporales.

- Riesgo y cumplimiento comunica habilitaciones y restricciones.

- Atención y operación administrativa entrega la información requerida al inversor.

Reglas y excepciones:

- Un instrumento conserva una identidad estable aunque cambie su denominación.

- Las condiciones operativas deben permitir determinar cómo se negocia y liquida.

- Suspender nuevas órdenes no elimina tenencias ni obligaciones existentes.

- Los cambios de características conservan su fecha de efecto.

- Un evento corregido mantiene el vínculo con su versión anterior y permite identificar las posiciones afectadas.

### 3. Órdenes de inversión

Descripción: administra las instrucciones de clientes para suscribir, comprar, vender o rescatar instrumentos
financieros.

Responsabilidades:

- Recibir órdenes e identificar cliente, instrumento, importe o cantidad y modalidad.

- Comprobar contratación, facultades, elegibilidad y controles exigidos.

- Registrar la entrega de información y las aceptaciones requeridas para la operación.

- Solicitar reservas de fondos para compras o suscripciones.

- Solicitar afectaciones de tenencias para ventas o rescates.

- Tramitar órdenes con intermediarios, administradores y mercados simulados.

- Administrar aceptaciones, ejecuciones parciales, rechazos y cancelaciones.

- Coordinar la entrega de fondos y la incorporación o salida de instrumentos durante la liquidación.

- Calcular comisiones y tributos propios de la operación.

- Conciliar órdenes, ejecuciones y liquidaciones con los participantes externos.

Datos propios: órdenes, instrucciones del cliente, controles y aceptaciones referenciados, reservas asociadas, envíos,
ejecuciones, precios de ejecución, cargos y resultados de liquidación.

Límites:

- Fondos administra la disponibilidad monetaria.

- Tenencias y valuación administra cantidades disponibles y comprometidas.

- Instrumentos de inversión mantiene las condiciones del instrumento.

- Tesorería ejecuta las liquidaciones externas del banco.

- Una orden aceptada no equivale a una operación ejecutada ni liquidada.

- No administra depósitos a plazo ni inversiones propias de Tesorería.

Relaciones:

- Contratos proporciona el acuerdo que habilita la operatoria.

- Personas, Clientes y Representación permiten comprobar participantes y facultades.

- Riesgo y cumplimiento verifica restricciones y adecuación cuando corresponda.

- Fondos y Tenencias y valuación confirman los recursos comprometidos.

- Tesorería recibe las obligaciones de liquidación.

- Contabilidad recibe los hechos económicos.

- Atención y operación administrativa comunica resultados y conserva comprobantes.

Reglas y excepciones:

- Repetir la misma instrucción identificada no crea una segunda orden.

- Una orden requiere recursos suficientes según su modalidad.

- Las ejecuciones parciales conservan cantidades, importes y precios individuales.

- Una solicitud de cancelación no se considera efectiva hasta comprobar qué parte seguía pendiente.

- Una ejecución incierta exige consulta y conciliación antes de reenviar o liberar recursos.

- Los recursos no utilizados se liberan cuando se comprueba que ya no respaldan una ejecución pendiente.

- Las diferencias de liquidación permanecen identificadas y no se presentan como posiciones disponibles.

- Una operación en distinta moneda coordina expresamente la conversión correspondiente con Tesorería.

### 4. Tenencias y valuación

Descripción: administra las posiciones de inversión de los clientes, sus cantidades disponibles y comprometidas y su
valoración.

Responsabilidades:

- Mantener posiciones por titularidad, instrumento y cuenta de inversión.

- Registrar incorporaciones, salidas y movimientos de instrumentos.

- Administrar cantidades disponibles, comprometidas, pendientes de liquidación y restringidas.

- Impedir que las mismas unidades respalden operaciones incompatibles.

- Mantener precios, valores de cuotaparte y cotizaciones de valuación con su fuente y fecha.

- Calcular valoraciones e identificar su moneda y momento de referencia.

- Aplicar eventos del instrumento sobre las posiciones afectadas.

- Determinar derechos económicos por intereses, amortizaciones y distribuciones.

- Calcular retenciones o cargos asociados a esos eventos cuando correspondan.

- Conciliar tenencias con custodios o administradores simulados.

Datos propios: cuentas de inversión, titularidades operativas, posiciones, movimientos de instrumentos, compromisos,
restricciones aplicadas, precios de referencia, valuaciones y derechos económicos.

Límites:

- Contratos conserva la titularidad acordada; este servicio mantiene su aplicación sobre las posiciones.

- Órdenes de inversión administra las instrucciones y ejecuciones.

- Una valuación es una estimación referida a un momento; no representa dinero disponible ni garantiza un precio de
  venta.

- Fondos registra las acreditaciones monetarias de rendimientos y rescates.

- Instrumentos de inversión define los eventos; este servicio determina su efecto sobre cada tenencia.

- No registra como patrimonio propio del banco los activos pertenecientes a clientes.

Relaciones:

- Órdenes de inversión solicita compromisos y comunica ejecuciones y liquidaciones.

- Instrumentos de inversión proporciona características y eventos.

- Riesgo y cumplimiento comunica restricciones sobre posiciones.

- Tesorería coordina cobros y entregas externos de eventos económicos.

- Fondos acredita importes efectivamente pagaderos al cliente.

- Contabilidad recibe los hechos que correspondan a registros patrimoniales o de custodia.

- Atención y operación administrativa utiliza las posiciones para informar al cliente.

Reglas y excepciones:

- Las unidades disponibles no incluyen cantidades comprometidas, restringidas o todavía no disponibles por liquidación.

- Un compromiso y su consumo no descuentan dos veces las mismas unidades.

- Cada precio conserva fuente, fecha, moneda y condición de vigencia.

- Ante un precio faltante o desactualizado, la posición se informa con esa limitación; no se inventa un valor.

- Un evento se aplica una sola vez a cada posición alcanzada.

- Tener derecho a un rendimiento no equivale a haberlo cobrado.

- Las diferencias de custodia requieren investigación y ajustes fundados.

- Las correcciones conservan los movimientos y valoraciones anteriores.

- El cierre de una cuenta de inversión exige resolver tenencias, compromisos y derechos pendientes.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Depósitos a plazo y obligación de devolución | Depósitos a plazo |
| Intereses y actualizaciones de depósitos | Depósitos a plazo |
| Características operativas de instrumentos | Instrumentos de inversión |
| Instrucciones y ejecuciones de inversión | Órdenes de inversión |
| Conciliación de órdenes y ejecuciones externas | Órdenes de inversión |
| Cantidades y disponibilidad de instrumentos | Tenencias y valuación |
| Precios de referencia y valoración de posiciones | Tenencias y valuación |
| Derechos económicos derivados de tenencias | Tenencias y valuación |
| Disponibilidad y movimientos monetarios | Fondos |
| Liquidaciones externas y posiciones propias del banco | Tesorería |
| Condiciones contractuales | Contratos |
| Controles, restricciones y criterios tributarios | Riesgo y cumplimiento |
| Registración contable | Contabilidad |

Los depósitos a plazo mantienen una obligación de devolución del banco; las inversiones mantienen posiciones y derechos
sobre instrumentos. Cada circuito conserva sus propios compromisos, rendimientos y resultados de liquidación.
