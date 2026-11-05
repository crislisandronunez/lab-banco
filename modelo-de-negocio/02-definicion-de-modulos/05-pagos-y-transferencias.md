## Módulo 5. Pagos y transferencias

Este módulo administra las instrucciones para trasladar fondos o cumplir obligaciones de pago, desde su recepción hasta
un resultado comprobable, incluyendo rechazos, devoluciones y conciliaciones.

Se divide en cinco microservicios: Transferencias, Pagos, Mandatos y recurrencias, Cheques y ECHEQ, y Compensación y
conciliación de pagos.

### 1. Transferencias

Descripción: administra instrucciones de movimiento de fondos entre cuentas propias, entre clientes del banco y con
cuentas de otras entidades simuladas.

Responsabilidades:

- Recibir instrucciones e identificar ordenante, cuentas, importe, moneda y destino.

- Resolver y conservar los datos del destino utilizados para la operación.

- Comprobar autorización, condiciones operativas, límites y controles aplicables.

- Coordinar reservas, débitos y créditos con Fondos.

- Ejecutar transferencias internas y gestionar el envío y la recepción de transferencias externas.

- Mantener el resultado de cada instrucción y su relación con los movimientos monetarios.

- Gestionar solicitudes de devolución, devoluciones recibidas y recuperación de operaciones inciertas.

- Calcular los cargos y tributos propios de la transferencia cuando correspondan.

Datos propios: instrucciones de transferencia, participantes, destinos resueltos, referencias de autorización,
resultados de controles, referencias monetarias, envíos, recepciones y devoluciones.

Límites:

- Fondos determina la disponibilidad y ejecuta los movimientos de cuentas.

- Identificación bancaria resuelve CBU y alias.

- No administra la obligación comercial que puede justificar una transferencia.

- Compensación y conciliación de pagos administra el intercambio externo y comprueba sus resultados.

- Tesorería cumple las obligaciones de liquidación entre entidades.

Relaciones:

- Cuentas y Representación proporcionan condiciones y facultades.

- Riesgo y cumplimiento aporta controles y restricciones.

- Fondos ejecuta los efectos monetarios.

- Compensación y conciliación de pagos comunica resultados externos.

- Pagos puede utilizar una transferencia como medio de ejecución.

- Atención y operación administrativa recibe información para comprobantes y avisos.

Reglas y excepciones:

- La repetición de una instrucción identificada no genera una nueva transferencia.

- Una transferencia interna solo se confirma cuando sus movimientos correspondientes quedan realizados.

- Una transferencia externa distingue recepción de la instrucción, envío, aceptación, acreditación y liquidación.

- La pérdida de una respuesta exige consultar y conciliar; no permite presumir rechazo.

- Una transferencia recibida que no puede acreditarse requiere el tratamiento de rechazo, retención o devolución que
  corresponda.

- Una operación confirmada no se cancela borrándola: cualquier devolución constituye una operación vinculada.

- Un cambio posterior de alias no modifica el destino registrado para una instrucción ya aceptada.

### 2. Pagos

Descripción: administra órdenes destinadas a cumplir obligaciones identificadas frente a prestadores, organismos,
comercios u otros beneficiarios.

Responsabilidades:

- Identificar al beneficiario y la obligación que se pretende pagar.

- Mantener convenios operativos con recaudadores y referencias de servicios habilitados.

- Consultar o recibir deudas y referencias de cobro de participantes simulados.

- Recibir órdenes de pago y comprobar importe, vencimiento y modalidades admitidas.

- Coordinar su ejecución mediante el instrumento correspondiente.

- Registrar la aceptación o aplicación informada por el beneficiario.

- Gestionar pagos parciales, anticipados o fuera de término cuando el convenio los permita.

- Administrar anulaciones permitidas, devoluciones y diferencias de imputación.

- Calcular cargos y tributos propios del servicio de pago.

Datos propios: convenios operativos de recaudación, referencias de obligaciones externas, órdenes de pago,
beneficiarios, importes, vencimientos, aplicaciones y devoluciones.

Límites:

- No mantiene la deuda original de un préstamo o una tarjeta del banco: pertenece al módulo acreedor.

- Una referencia de deuda externa refleja información recibida; no convierte a Pagos en su acreedor.

- No administra saldos ni autoriza consumos con tarjeta.

- Las condiciones comerciales generales pertenecen al módulo 1; los acuerdos contractuales, a Contratación.

- El traslado de dinero y la aplicación a la obligación son resultados distintos.

Relaciones:

- Transferencias ejecuta los traslados que utilicen ese instrumento.

- Fondos realiza los efectos monetarios que correspondan directamente.

- Mandatos y recurrencias origina órdenes previamente autorizadas.

- Compensación y conciliación de pagos intercambia información con recaudadores simulados.

- Los módulos acreedores determinan la imputación de pagos sobre sus propias deudas.

Reglas y excepciones:

- Un pago conserva la referencia exacta de la obligación y del beneficiario.

- No se cobra dos veces la misma orden por repetición accidental.

- Una deuda puede recibir varios pagos únicamente cuando su modalidad lo permita.

- Debitar fondos no basta para afirmar que la obligación fue cancelada.

- Si el beneficiario recibió el dinero pero no confirmó la aplicación, se investiga y concilia antes de repetir el pago.

- La devolución de fondos no implica por sí sola que la obligación original deje de existir.

- Los cargos de cada participante se identifican por su causa para evitar duplicaciones al utilizar una transferencia.

### 3. Mandatos y recurrencias

Descripción: administra autorizaciones para débitos y programaciones que originan instrucciones futuras de pago o
transferencia.

Responsabilidades:

- Registrar adhesiones y mandatos, identificando otorgante, beneficiario, cuenta y alcance.

- Mantener importes fijos o variables, límites, periodicidad y vigencia.

- Registrar órdenes únicas programadas y series recurrentes.

- Determinar cada ocurrencia según calendario y condiciones acordadas.

- Comprobar la vigencia de la autorización antes de originar una instrucción.

- Administrar modificaciones, suspensiones, revocaciones y vencimientos.

- Registrar el resultado de cada ocurrencia y aplicar las reglas autorizadas de reintento.

- Tramitar solicitudes de reversión de débitos hacia el responsable de la operación ejecutada.

Datos propios: mandatos, adhesiones, programaciones, condiciones de recurrencia, ocurrencias, revocaciones y vínculos
con instrucciones generadas.

Límites:

- Una programación establece cuándo originar una operación; no garantiza su ejecución.

- No reserva fondos por toda la duración de una serie.

- Pagos y Transferencias ejecutan las instrucciones y mantienen sus resultados.

- No calcula la deuda de productos ni determina su cancelación.

- La reversión de un débito ejecutado no se confunde con revocar futuros débitos.

Relaciones:

- Cuentas y Representación permiten comprobar las facultades del otorgante.

- Calendarios bancarios permite determinar fechas.

- Pagos y Transferencias reciben las instrucciones generadas.

- Contratos proporciona las condiciones de autorización aplicables.

- Atención y operación administrativa comunica adhesiones, cambios y resultados.

Reglas y excepciones:

- Cada ocurrencia tiene identidad propia y solo puede originar una instrucción efectiva.

- Una autorización revocada impide nuevas instrucciones desde el momento aplicable.

- Si una instrucción ya fue aceptada para ejecución, su cancelación se solicita al responsable y depende de su avance.

- Los reintentos respetan la autorización, los plazos y las reglas del instrumento; no generan cobros adicionales por
  una ejecución anterior incierta.

- Una modificación de la serie conserva las condiciones utilizadas por ocurrencias anteriores.

- La falta de fondos se registra como resultado de la ocurrencia y se trata según las condiciones acordadas.

### 4. Cheques y ECHEQ

Descripción: administra el ciclo de los cheques físicos simulados y electrónicos en los que el banco interviene como
entidad girada o depositaria.

Responsabilidades:

- Administrar chequeras, numeración y habilitación de emisión.

- Registrar cheques emitidos, recibidos y depositados.

- Mantener librador, beneficiarios, importe, moneda y fechas.

- Administrar aceptación, endosos, avales y cesiones cuando la modalidad los admita.

- Comprobar legitimación, condiciones formales y facultades de los participantes.

- Tramitar presentaciones al cobro y coordinar su pago.

- Registrar órdenes de no pagar, denuncias y restricciones con su fundamento.

- Administrar rechazos, devoluciones y constancias.

- Calcular cargos y tributos propios del instrumento.

Datos propios: chequeras, instrumentos, numeración, participantes, cadena de transmisión, presentaciones, medidas sobre
instrumentos, pagos y rechazos.

Límites:

- Cuentas determina si la cuenta admite operar con cheques.

- Fondos determina la disponibilidad y ejecuta los movimientos.

- Emitir un cheque no inmoviliza automáticamente su importe.

- Compensación y conciliación de pagos administra el intercambio interbancario.

- El descuento de un cheque como financiación pertenece a Financiación; este servicio administra el instrumento.

Relaciones:

- Personas y Representación permiten identificar participantes y facultades.

- Cuentas proporciona condiciones operativas.

- Fondos ejecuta reservas o movimientos expresamente requeridos.

- Riesgo y cumplimiento informa restricciones.

- Compensación y conciliación de pagos tramita presentaciones externas.

- Atención y operación administrativa conserva evidencias y constancias.

Reglas y excepciones:

- Cada instrumento conserva una identidad única y su historia de transmisión.

- Un cheque de pago diferido respeta su fecha de pago y las condiciones de presentación.

- La transmisión debe conservar una cadena válida de legitimación.

- Una presentación repetida no produce un segundo pago.

- Los rechazos conservan su causal y las constancias correspondientes.

- Una orden de no pagar requiere fundamento y tratamiento aplicable; no extingue automáticamente la obligación
  subyacente.

- El cierre de la cuenta exige considerar los instrumentos pendientes y sus consecuencias.

### 5. Compensación y conciliación de pagos

Descripción: administra el intercambio operativo con redes y participantes simulados, determina obligaciones de
compensación y comprueba la correspondencia entre operaciones y resultados externos.

Responsabilidades:

- Mantener participantes, redes y convenios operativos de intercambio.

- Enviar y recibir instrucciones, presentaciones, confirmaciones y devoluciones.

- Validar referencias, integridad y correspondencia de los intercambios.

- Detectar mensajes repetidos, faltantes o contradictorios.

- Consultar resultados de operaciones pendientes o inciertas.

- Conciliar instrucciones internas con registros externos.

- Determinar obligaciones de compensación por participante, moneda y período.

- Comunicar obligaciones a Tesorería y recibir su resultado de liquidación.

- Administrar diferencias hasta obtener una resolución fundada.

Datos propios: participantes y acuerdos operativos de red, intercambios, referencias externas, resultados recibidos,
conciliaciones, diferencias y obligaciones de compensación.

Límites:

- Los servicios de origen conservan la instrucción de negocio y deciden su tratamiento.

- No modifica directamente saldos ni deudas.

- Tesorería ejecuta la liquidación y administra las disponibilidades del banco.

- Contabilidad registra las consecuencias contables.

- El procesamiento de redes de tarjetas corresponde al módulo Tarjetas.

Relaciones:

- Transferencias, Pagos y Cheques y ECHEQ solicitan intercambios y reciben resultados.

- Tesorería recibe obligaciones de liquidación.

- Contabilidad recibe los hechos de compensación que requieran registración.

- Atención y operación administrativa coordina investigaciones con intervención humana.

Reglas y excepciones:

- La recepción técnica de una instrucción no equivale a su aceptación económica.

- Las confirmaciones deben identificar qué resultado prueban: recepción, aceptación, acreditación o liquidación.

- Los mensajes duplicados no producen nuevos efectos de negocio.

- Una diferencia no se considera resuelta sin evidencia del resultado o una decisión autorizada.

- Las correcciones generan antecedentes vinculados; no sobrescriben intercambios anteriores.

- Una falla de liquidación se comunica a los responsables sin convertir automáticamente en rechazados los pagos ya
  confirmados.

- Las obligaciones de compensación conservan el detalle que permite justificar su composición.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Instrucción de transferencia y su resultado | Transferencias |
| Orden de pago y seguimiento de su aplicación | Pagos |
| Autorización de débito y programación futura | Mandatos y recurrencias |
| Ciclo y legitimación de cheques y ECHEQ | Cheques y ECHEQ |
| Intercambio externo y conciliación operativa | Compensación y conciliación de pagos |
| Obligaciones de compensación | Compensación y conciliación de pagos |
| Disponibilidad y movimientos de cuentas | Fondos |
| Cumplimiento de liquidaciones externas | Tesorería |
| Imputación sobre préstamos o deuda de tarjeta | Financiación o Tarjetas, respectivamente |
| Registración contable | Contabilidad |

Cada instrucción conserva su relación con la autorización, los movimientos monetarios, el resultado externo y la
liquidación correspondiente. Esto permite resolver una operación pendiente o corregida sin repetir sus efectos ni perder
su historia.
