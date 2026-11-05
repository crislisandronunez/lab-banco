## Módulo 10. Tesorería

Este módulo administra los recursos financieros propios del banco, sus posiciones por moneda y su capacidad para cumplir
obligaciones de pago y liquidación.

Se divide en cuatro microservicios: Posiciones y liquidez, Operaciones de tesorería, Cambio de moneda y Liquidaciones de
tesorería.

### 1. Posiciones y liquidez

Descripción: mantiene la posición financiera operativa del banco y determina los recursos disponibles y las necesidades
previstas de fondos.

Responsabilidades:

- Mantener disponibilidades propias por moneda, ubicación y entidad depositaria.

- Administrar existencias de efectivo en tesoro, sucursales, cajas y dispositivos simulados.

- Registrar asignaciones, compromisos y movimientos internos de disponibilidades.

- Consolidar entradas y salidas previstas de depósitos, préstamos, pagos, inversiones y operaciones propias.

- Proyectar necesidades de liquidez por fecha y moneda.

- Determinar recursos utilizables considerando compromisos y restricciones.

- Comprobar disponibilidad para asumir nuevas obligaciones de tesorería.

- Detectar faltantes, concentraciones y desvíos respecto de los límites aplicables.

- Proporcionar posiciones a Riesgo y cumplimiento y a Contabilidad.

Datos propios: posiciones operativas de disponibilidades, existencias de efectivo, asignaciones, compromisos,
movimientos internos, proyecciones y necesidades de liquidez.

Límites:

- Fondos administra la disponibilidad de cuentas de clientes.

- Los módulos de productos mantienen sus obligaciones y vencimientos; este servicio utiliza esa información para
  proyectar flujos.

- Contabilidad mantiene la representación contable de las disponibilidades.

- Riesgo y cumplimiento establece políticas y límites de liquidez.

- No contrata financiación ni ejecuta liquidaciones externas.

Relaciones:

- Operaciones de efectivo informa recepciones y entregas comprobadas.

- Los módulos operativos proporcionan flujos previstos y realizados.

- Operaciones de tesorería consulta necesidades y solicita compromisos.

- Cambio de moneda consulta posiciones y comunica operaciones.

- Liquidaciones de tesorería informa compromisos y movimientos externos.

- Riesgo y cumplimiento recibe exposiciones y alertas.

Reglas y excepciones:

- Cada posición conserva moneda, ubicación y fecha de referencia.

- No se suman distintas monedas sin una conversión identificada.

- Las proyecciones distinguen flujos previstos, comprometidos y realizados.

- Un mismo compromiso no puede reducir dos veces los recursos utilizables.

- La entrega de efectivo se limita por la disponibilidad física de la ubicación correspondiente, además de los controles
  sobre la cuenta del cliente.

- Un traslado interno identifica salida, tránsito y recepción, sin crear efectivo adicional.

- Una diferencia física exige investigación y ajuste autorizado.

- La falta de liquidez no modifica por sí sola los derechos de los depositantes ni elimina obligaciones del banco.

### 2. Operaciones de tesorería

Descripción: administra la financiación y las inversiones que el banco realiza por cuenta propia.

Responsabilidades:

- Registrar propuestas de colocación, inversión y obtención de financiación.

- Comprobar facultades, límites de exposición y condiciones de contraparte.

- Administrar operaciones con entidades y participantes simulados.

- Mantener condiciones acordadas, capital, moneda, tasas, plazos y garantías asociadas.

- Coordinar la formalización contractual y las condiciones previas a la ejecución.

- Administrar posiciones propias, desembolsos, vencimientos y obligaciones.

- Calcular intereses, actualizaciones, valuaciones, resultados, cargos y tributos correspondientes.

- Gestionar renovaciones, cancelaciones anticipadas y modificaciones autorizadas.

- Originar instrucciones de liquidación y hechos contables.

Datos propios: operaciones financieras propias, condiciones económicas, posiciones de inversión y financiación,
calendarios, devengamientos, valuaciones y obligaciones.

Límites:

- Ahorro e inversiones administra inversiones de clientes.

- Financiación administra los créditos ofrecidos comercialmente a clientes.

- Contratos conserva los acuerdos de tesorería y sus modificaciones.

- Posiciones y liquidez mantiene las disponibilidades y los compromisos asociados.

- Liquidaciones de tesorería ejecuta los movimientos externos.

- Las operaciones cambiarias pertenecen a Cambio de moneda.

Relaciones:

- Posiciones y liquidez identifica necesidades y confirma recursos.

- Personas identifica contrapartes.

- Institución proporciona facultades de aprobación.

- Riesgo y cumplimiento aporta límites, restricciones y criterios de exposición.

- Contratos formaliza acuerdos.

- Liquidaciones de tesorería recibe obligaciones de entrega y cobro.

- Contabilidad recibe operaciones, devengamientos y valuaciones.

Reglas y excepciones:

- Las operaciones propias se identifican expresamente y se separan de las posiciones de clientes.

- Una operación acordada se distingue de una operación liquidada.

- Las modificaciones conservan las condiciones anteriores y su fecha de efecto.

- Los rendimientos devengados se distinguen de los cobrados.

- La indisponibilidad de una contraparte o el incumplimiento de un vencimiento genera una obligación pendiente y su
  tratamiento de riesgo.

- Una cancelación anticipada aplica las condiciones y resultados correspondientes.

- Una operación con resultado incierto se verifica antes de repetirse.

- Las valoraciones conservan fuente, fecha y criterio utilizado.

### 3. Cambio de moneda

Descripción: administra cotizaciones y operaciones de compra y venta de monedas, tanto para clientes como para
necesidades propias del banco.

Responsabilidades:

- Mantener cotizaciones de referencia y comerciales con su fuente, finalidad y vigencia.

- Identificar moneda comprada, moneda vendida, importes y tipo de cambio.

- Comprobar elegibilidad, facultades, restricciones y documentación.

- Presentar las condiciones de la operación y registrar su aceptación.

- Solicitar compromisos de fondos del cliente o de disponibilidades propias.

- Coordinar la ejecución de ambas partes del intercambio.

- Calcular diferencias de precio, comisiones y tributos aplicables.

- Administrar operaciones inmediatas o con liquidación diferida cuando estén habilitadas.

- Conservar los resultados y las referencias monetarias de cada operación.

Datos propios: cotizaciones, ofertas cambiarias particulares, aceptaciones, operaciones de cambio, importes por moneda,
cargos y referencias de ejecución.

Límites:

- Condiciones comerciales define márgenes, tarifas y criterios generales de la oferta; este servicio determina la
  cotización concreta aplicable.

- Fondos administra los movimientos de cuentas de clientes.

- Posiciones y liquidez administra las posiciones propias.

- Liquidaciones de tesorería ejecuta las entregas externas.

- Riesgo y cumplimiento determina las restricciones cambiarias aplicables.

- Una cotización de referencia utilizada para valuar no constituye una oferta ejecutable.

Relaciones:

- Cuentas y fondos comprueba condiciones y ejecuta las partes monetarias del cliente.

- Posiciones y liquidez confirma recursos y recibe cambios de posición.

- Los módulos operativos solicitan conversiones cuando sus operaciones lo requieren.

- Riesgo y cumplimiento aporta verificaciones y criterios tributarios.

- Contabilidad recibe importes originales, cotización y resultados.

- Atención y operación administrativa entrega comprobantes.

Reglas y excepciones:

- La cotización aceptada identifica sentido de la operación, monedas, importes y vencimiento.

- Una cotización vencida requiere una nueva aceptación cuando corresponda.

- Cada importe conserva su moneda y regla de redondeo.

- No se informa una conversión completa si falta ejecutar una de sus partes.

- Una ejecución parcial o incierta conserva las obligaciones pendientes y exige recuperación o corrección expresa.

- Repetir la misma instrucción no duplica compras ni ventas.

- Una reversión constituye una actuación vinculada; no borra la operación original ni presume que conserva la misma
  cotización.

### 4. Liquidaciones de tesorería

Descripción: administra el cumplimiento de las obligaciones de entrega y recepción de fondos o activos frente a
participantes externos simulados.

Responsabilidades:

- Recibir obligaciones de liquidación originadas en pagos, tarjetas, inversiones, cambio y operaciones propias.

- Identificar contraparte, importe o cantidad, moneda o instrumento, fecha y mecanismo de liquidación.

- Coordinar los recursos necesarios con Posiciones y liquidez.

- Emitir y recibir instrucciones de liquidación.

- Coordinar entregas de instrumentos con el módulo que administra las posiciones correspondientes.

- Registrar confirmaciones, liquidaciones parciales, rechazos e incumplimientos.

- Conciliar movimientos de cuentas externas y constancias de entrega o recepción.

- Gestionar diferencias y consultar resultados inciertos.

- Comunicar el resultado al módulo que originó la obligación.

Datos propios: obligaciones recibidas, instrucciones de liquidación, referencias externas, entregas y recepciones
confirmadas, conciliaciones y diferencias.

Límites:

- Los módulos de origen determinan la causa y composición de cada obligación.

- Compensación y conciliación de pagos determina obligaciones de compensación de pagos y cheques.

- Procesamiento de consumos determina obligaciones de redes de tarjetas.

- Tenencias y valuación administra los instrumentos de clientes; Operaciones de tesorería administra las inversiones
  propias.

- No modifica directamente cuentas de clientes ni deudas de productos.

- Contabilidad registra los efectos contables.

Relaciones:

- Los módulos operativos proporcionan obligaciones y reciben resultados.

- Posiciones y liquidez confirma recursos y actualiza disponibilidades.

- Los responsables de instrumentos confirman sus entregas o recepciones.

- Riesgo y cumplimiento aporta restricciones y límites.

- Contabilidad recibe movimientos y diferencias que requieren registración.

- Atención y operación administrativa coordina investigaciones y aprobaciones humanas.

Reglas y excepciones:

- Cada obligación mantiene su vínculo con las operaciones que la originaron.

- Una instrucción repetida no produce una segunda liquidación.

- Enviar una instrucción no demuestra que la liquidación se haya realizado.

- Las liquidaciones parciales conservan el importe o cantidad cumplidos y el remanente.

- La entrega de fondos y la de instrumentos se coordinan conforme al mecanismo pactado.

- Una confirmación perdida exige consulta y conciliación antes de reenviar.

- Una diferencia no se cierra sin evidencia o resolución autorizada.

- La falla de liquidación no convierte automáticamente en rechazada una operación de cliente ya confirmada.

- Las correcciones conservan instrucciones y resultados originales.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Disponibilidades y existencias propias | Posiciones y liquidez |
| Compromisos y proyecciones de liquidez | Posiciones y liquidez |
| Financiación e inversiones propias | Operaciones de tesorería |
| Cotizaciones y operaciones cambiarias | Cambio de moneda |
| Ejecución y conciliación de liquidaciones externas | Liquidaciones de tesorería |
| Composición de obligaciones de compensación | Módulo operativo de origen |
| Dinero disponible de clientes | Fondos |
| Tenencias de inversión de clientes | Tenencias y valuación |
| Políticas y límites de riesgo | Riesgo y cumplimiento |
| Acuerdos contractuales | Contratos |
| Registración contable | Contabilidad |
