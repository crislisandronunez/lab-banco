## Módulo 7. Tarjetas

Este módulo administra las tarjetas de débito y crédito, las operaciones realizadas con ellas y las obligaciones que
generan, desde su emisión hasta su cancelación.

Se divide en cinco microservicios: Emisión de tarjetas, Autorizaciones, Procesamiento de consumos, Cuenta de tarjeta y
Disputas de tarjetas.

### 1. Emisión de tarjetas

Descripción: administra cada tarjeta como instrumento de pago y su vinculación con cuentas o financiación.

Responsabilidades:

- Emitir tarjetas a partir de una contratación y habilitación válidas.

- Identificar titular contractual, portador y carácter de tarjeta principal o adicional.

- Vincular tarjetas de débito con cuentas bancarias y tarjetas de crédito con su cuenta de tarjeta.

- Administrar personalización, entrega y activación.

- Mantener habilitaciones de uso por canal, tipo de operación y ámbito geográfico.

- Administrar vencimientos, renovaciones, bloqueos, cancelaciones y reemplazos.

- Gestionar referencias protegidas de identificación y credenciales del instrumento.

- Conservar la relación entre una tarjeta reemplazada y su sucesora.

Datos propios: tarjetas, portadores, vínculos con cuentas, características del instrumento, habilitaciones, referencias
de credenciales, entregas, activaciones y antecedentes de reemplazo.

Límites:

- Personas administra la identidad del portador.

- Contratos determina los participantes y sus obligaciones contractuales.

- Cuentas administra la titularidad y los permisos sobre la cuenta bancaria.

- Cuenta de tarjeta administra la línea crediticia y su deuda.

- No mantiene saldos monetarios, autoriza consumos ni procesa presentaciones.

- Las credenciales de la tarjeta no sustituyen las credenciales de acceso a los canales del banco.

Relaciones:

- Contratación proporciona el acuerdo que habilita la emisión.

- Cuentas y Cuenta de tarjeta proporcionan los vínculos operativos.

- Autorizaciones consulta vigencia, bloqueos y capacidades del instrumento.

- Riesgo y cumplimiento comunica medidas de seguridad.

- Atención y operación administrativa coordina entrega, avisos y constancias.

Reglas y excepciones:

- Una tarjeta emitida no se considera entregada ni activada automáticamente.

- Una tarjeta adicional identifica al portador y al responsable contractual de sus consumos.

- Un reemplazo no elimina autorizaciones, consumos ni obligaciones de la tarjeta anterior.

- El bloqueo impide nuevos usos dentro de su alcance; las operaciones anteriores requieren su tratamiento
  correspondiente.

- La cancelación del instrumento no extingue la cuenta bancaria, el contrato ni la deuda.

- Las referencias sensibles se conservan y exponen únicamente según las facultades y necesidades de cada operación.

### 2. Autorizaciones

Descripción: administra la decisión sobre operaciones solicitadas con tarjeta y el compromiso de fondos o capacidad
crediticia que requieren.

Responsabilidades:

- Recibir solicitudes de compra, extracción y otras operaciones admitidas.

- Identificar tarjeta, comercio o dispositivo, importe, moneda y modalidad.

- Comprobar autenticación del instrumento, habilitaciones y límites de uso.

- Solicitar controles de fraude y restricciones aplicables.

- Para débito, solicitar a Fondos la reserva del importe correspondiente.

- Para crédito, solicitar a Cuenta de tarjeta el compromiso de capacidad disponible.

- Aprobar o rechazar la solicitud y conservar el fundamento.

- Administrar anulaciones, ajustes y vencimientos de autorizaciones.

- Relacionar cada autorización con sus presentaciones posteriores.

Datos propios: solicitudes de autorización, resultados de controles, decisiones, referencias de reservas o compromisos,
importes autorizados, vigencias y vínculos con presentaciones.

Límites:

- Fondos es la autoridad sobre el dinero disponible de una cuenta.

- Cuenta de tarjeta es la autoridad sobre la capacidad crediticia disponible.

- Autorizaciones aplica límites de uso, pero no concede financiación.

- Una aprobación no constituye por sí sola un consumo presentado ni una deuda liquidada.

- Procesamiento de consumos determina el tratamiento de las presentaciones recibidas.

Relaciones:

- Emisión de tarjetas informa las condiciones del instrumento.

- Fondos y Cuenta de tarjeta aceptan o rechazan el compromiso económico.

- Riesgo y cumplimiento aporta controles.

- Procesamiento de consumos informa presentaciones y anulaciones.

- Operaciones de efectivo interviene en extracciones con tarjeta sobre cuentas del banco.

Reglas y excepciones:

- Una solicitud repetida conserva su decisión y no duplica reservas o compromisos.

- Una autorización solo se aprueba cuando el compromiso requerido queda confirmado.

- La pérdida de una respuesta exige comprobar el resultado antes de repetir la afectación.

- Las presentaciones parciales consumen únicamente la parte correspondiente del compromiso.

- Los importes pendientes se liberan conforme a las reglas del instrumento.

- Una ejecución incierta impide liberar fondos automáticamente sin comprobar sus consecuencias.

- La moneda y el criterio de conversión utilizados deben quedar identificados.

### 3. Procesamiento de consumos

Descripción: administra las presentaciones y demás hechos económicos recibidos de redes y participantes de tarjetas
simulados.

Responsabilidades:

- Mantener participantes y acuerdos operativos de intercambio de tarjetas.

- Recibir presentaciones de compras, extracciones, devoluciones y ajustes.

- Validar identidad, integridad, importes y correspondencia de las operaciones.

- Relacionar presentaciones con autorizaciones cuando existan.

- Tratar presentaciones parciales, tardías o sin autorización previa.

- Coordinar cargos de débito con Fondos y reconocimiento de consumos de crédito con Cuenta de tarjeta.

- Administrar anulaciones y devoluciones.

- Conciliar registros internos con los de la red simulada.

- Determinar obligaciones de compensación de tarjetas y comunicarlas a Tesorería.

Datos propios: presentaciones, intercambios de red, consumos procesados, devoluciones, ajustes, conciliaciones,
diferencias y obligaciones de compensación de tarjetas.

Límites:

- No mantiene la deuda consolidada ni emite resúmenes.

- No vuelve a autorizar una operación por el solo hecho de recibir su presentación.

- Fondos y Cuenta de tarjeta ejecutan los efectos sobre sus posiciones.

- Tesorería realiza las liquidaciones externas.

- Disputas de tarjetas determina el procedimiento de controversias.

- El intercambio de redes de tarjetas pertenece a este servicio; el de transferencias, pagos y cheques pertenece al
  módulo 5.

Relaciones:

- Autorizaciones proporciona decisiones y compromisos previos.

- Fondos aplica cargos y devoluciones sobre cuentas.

- Cuenta de tarjeta reconoce consumos y créditos.

- Disputas de tarjetas solicita ajustes derivados de controversias.

- Tesorería recibe obligaciones de liquidación.

- Contabilidad recibe los hechos económicos correspondientes.

Reglas y excepciones:

- Una presentación repetida no genera un segundo cargo.

- La presentación puede diferir de la autorización; la diferencia requiere tratamiento explícito.

- Una presentación sin autorización no se acepta ni se rechaza automáticamente: se aplican las reglas del instrumento y
  del convenio.

- Al confirmar un cargo se consume la reserva correspondiente sin duplicar la afectación.

- Una devolución conserva la referencia al consumo cuando sea identificable.

- La falta de coincidencia con registros externos abre una diferencia de conciliación.

- Una falla de liquidación no elimina un consumo ya reconocido.

- Los consumos en otra moneda conservan importe y moneda originales, conversión aplicada e importe resultante.

### 4. Cuenta de tarjeta

Descripción: administra la relación económica de la tarjeta de crédito: capacidad de financiación, consumos, cuotas,
intereses, resúmenes y pagos.

Responsabilidades:

- Constituir la cuenta de tarjeta a partir del contrato y la aprobación crediticia.

- Mantener responsables contractuales y tarjetas vinculadas.

- Administrar límites aprobados, sublímites y capacidad disponible.

- Registrar compromisos de autorizaciones y convertirlos en deuda cuando corresponda.

- Mantener consumos en un pago y planes de cuotas.

- Calcular intereses, comisiones, tributos y demás conceptos aplicables.

- Administrar cierres, vencimientos y composición de resúmenes.

- Determinar pago total, pago mínimo y obligaciones exigibles.

- Imputar pagos, devoluciones, ajustes y créditos.

- Determinar financiación de saldos y mora.

- Administrar regularizaciones, modificaciones y cierre de la cuenta de tarjeta.

Datos propios: cuentas de tarjeta, límites, compromisos crediticios, consumos reconocidos, planes de cuotas, deuda por
concepto y moneda, devengamientos, resúmenes, pagos imputados y mora.

Límites:

- Evaluación crediticia aprueba la concesión y modificación de la financiación.

- Contratos conserva las condiciones acordadas.

- Emisión de tarjetas administra los instrumentos físicos o virtuales.

- Fondos mantiene el dinero de las cuentas bancarias utilizadas para pagar.

- Cobranzas y recuperaciones gestiona la recuperación de deuda vencida.

- Cuenta de tarjeta conserva la autoridad sobre su deuda y su imputación.

Relaciones:

- Autorizaciones solicita compromisos y liberaciones de capacidad.

- Procesamiento de consumos comunica cargos y devoluciones.

- Pagos y Fondos informan cobros realizados.

- Mandatos y recurrencias administra adhesiones al débito automático del resumen.

- Cobranzas y recuperaciones recibe obligaciones vencidas.

- Atención y operación administrativa entrega los resúmenes elaborados por este servicio.

- Contabilidad recibe deuda, devengamientos, pagos y ajustes.

Reglas y excepciones:

- Las tarjetas adicionales comparten la relación crediticia correspondiente; sus sublímites no crean financiación
  adicional.

- Un compromiso reduce capacidad disponible sin equivaler todavía a deuda presentada.

- La conversión de compromiso en deuda no consume dos veces el límite.

- Las compras en cuotas afectan la capacidad según las condiciones pactadas, distinguiendo deuda total y cuotas
  exigibles.

- El cierre del resumen y su vencimiento son hechos diferentes.

- Un pago parcial se imputa por concepto y puede dejar saldo financiado.

- El pago mínimo no equivale a cancelar la deuda total.

- Las devoluciones y pagos que generan saldo a favor reciben un tratamiento identificado; no desaparecen al cerrar la
  cuenta.

- Una reducción de límite no elimina deuda existente.

- Un resumen emitido conserva su contenido; las correcciones quedan documentadas mediante versiones o ajustes
  identificables.

- La cancelación exige tratar consumos pendientes, cuotas futuras, disputas, deudas y saldos a favor.

### 5. Disputas de tarjetas

Descripción: administra las controversias sobre operaciones con tarjeta y los procedimientos de contracargo frente a
participantes simulados.

Responsabilidades:

- Recibir desconocimientos y cuestionamientos de consumos.

- Identificar operación, motivo, participantes y plazos aplicables.

- Reunir antecedentes de autorización, presentación y autenticación.

- Solicitar documentación al cliente y a los participantes involucrados.

- Determinar la procedencia de medidas provisionales.

- Tramitar contracargos, respuestas y etapas posteriores de controversia.

- Resolver el caso dentro de las facultades correspondientes.

- Coordinar ajustes provisionales o definitivos y comunicar su resultado.

Datos propios: expedientes de disputa, motivos, antecedentes, evidencias referenciadas, plazos, intercambios de
contracargo, decisiones y referencias de ajustes.

Límites:

- Atención y operación administrativa administra el reclamo general y su comunicación con el cliente.

- Disputas administra el procedimiento especializado de tarjeta.

- Riesgo y cumplimiento investiga posibles fraudes y determina medidas de su competencia.

- No modifica directamente saldos ni deuda.

- Procesamiento de consumos coordina los ajustes de red; Fondos o Cuenta de tarjeta aplican sus efectos económicos.

Relaciones:

- Emisión de tarjetas proporciona antecedentes del instrumento.

- Autorizaciones y Procesamiento de consumos aportan información de la operación.

- Cuenta de tarjeta o Fondos informan los efectos económicos.

- Riesgo y cumplimiento recibe indicios de fraude.

- Atención y operación administrativa conserva documentos y comunica resoluciones.

Reglas y excepciones:

- Un desconocimiento no demuestra por sí solo que hubo fraude.

- Una controversia no elimina automáticamente el consumo.

- Los créditos provisionales identifican su carácter, condiciones y posible reversión.

- La resolución distingue el resultado frente al cliente del recupero frente a terceros.

- Los vencimientos y etapas del procedimiento conservan fechas y constancias.

- Una decisión favorable requiere comprobar que los ajustes correspondientes se aplicaron.

- La resolución definitiva trata cualquier crédito provisional para evitar devoluciones duplicadas.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Instrumento, portador, entrega, activación y bloqueo | Emisión de tarjetas |
| Decisión sobre una solicitud de uso | Autorizaciones |
| Reservas y disponibilidad monetaria de débito | Fondos |
| Compromisos y capacidad disponible de crédito | Cuenta de tarjeta |
| Presentaciones e intercambio con redes de tarjetas | Procesamiento de consumos |
| Deuda, cuotas, intereses, resúmenes e imputación | Cuenta de tarjeta |
| Procedimiento de disputa y contracargo | Disputas de tarjetas |
| Aprobación de financiación | Evaluación crediticia |
| Gestión de cobranza de deuda vencida | Cobranzas y recuperaciones |
| Liquidación externa | Tesorería |
| Registración contable | Contabilidad |

En débito, el consumo afecta fondos de una cuenta bancaria. En crédito, afecta capacidad de financiación y
posteriormente genera deuda en la cuenta de tarjeta. Ambos circuitos conservan la relación entre autorización,
presentación, ajuste y liquidación.
