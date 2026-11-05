# Contratos de integración — Módulo 6. Financiación

## 1. Propósito y alcance

Este documento define los contratos de integración proporcionados por los servicios:

- Evaluación crediticia.

- Préstamos y líneas.

- Garantías.

- Cobranzas y recuperaciones.

Comprende evaluación, aprobación, constitución, garantías, desembolsos, utilizaciones, cálculo financiero, pagos, mora,
modificaciones, recuperación y cancelación.

Incluye préstamos amortizables, préstamos con capital al vencimiento, líneas renovables y no renovables, descubierto y
descuento de cheques con responsabilidad del cliente.

La aprobación, la contratación, la constitución del producto, la constitución de garantías, el desembolso y el
reconocimiento de deuda son hechos distintos.

La deuda de tarjetas permanece en Cuenta de tarjeta. Cobranzas y recuperaciones puede gestionar su recuperación sin
mantener otra deuda por los mismos consumos.

## 2. Catálogo de contratos

Todos los contratos corresponden a la versión 1.

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-06-001 | Preparar y completar una evaluación crediticia | Evaluación crediticia | Solicitud de actuación |
| CI-06-002 | Analizar capacidad de pago y exposición | Evaluación crediticia | Solicitud de actuación |
| CI-06-003 | Resolver una solicitud de financiación | Evaluación crediticia | Solicitud de actuación |
| CI-06-004 | Revisar, vencer o retirar una aprobación | Evaluación crediticia | Solicitud de actuación |
| CI-06-005 | Consultar una evaluación o aprobación | Evaluación crediticia | Consulta |
| CI-06-006 | Confirmar la aplicación de una aprobación | Evaluación crediticia | Solicitud de actuación |
| CI-06-007 | Registrar y evaluar una garantía | Garantías | Solicitud de actuación |
| CI-06-008 | Constituir y afectar una garantía | Garantías | Solicitud de actuación |
| CI-06-009 | Revisar, sustituir o liberar una garantía | Garantías | Solicitud de actuación |
| CI-06-010 | Consultar una garantía y su cobertura | Garantías | Consulta |
| CI-06-011 | Constituir un préstamo o una línea | Préstamos y líneas | Solicitud de actuación |
| CI-06-012 | Calcular condiciones, cronograma y CFT | Préstamos y líneas | Solicitud de actuación |
| CI-06-013 | Desembolsar una financiación | Préstamos y líneas | Solicitud de actuación |
| CI-06-014 | Administrar el límite de una línea | Préstamos y líneas | Solicitud de actuación |
| CI-06-015 | Solicitar una utilización de línea | Préstamos y líneas | Solicitud de actuación |
| CI-06-016 | Incorporar y conciliar utilización de descubierto | Préstamos y líneas | Solicitud de actuación |
| CI-06-017 | Devengar y liquidar conceptos económicos | Préstamos y líneas | Solicitud de actuación |
| CI-06-018 | Consultar deuda y posición de financiación | Préstamos y líneas | Consulta |
| CI-06-019 | Preparar la aplicación de un pago | Préstamos y líneas | Solicitud de actuación |
| CI-06-020 | Confirmar o cancelar la preparación de un pago | Préstamos y líneas | Solicitud de actuación |
| CI-06-021 | Cobrar intereses y cargos de descubierto | Préstamos y líneas | Solicitud de actuación |
| CI-06-022 | Preparar una liquidación de precancelación | Préstamos y líneas | Solicitud de actuación |
| CI-06-023 | Ejecutar una precancelación | Préstamos y líneas | Solicitud de actuación |
| CI-06-024 | Determinar mora y obligaciones vencidas | Préstamos y líneas | Solicitud de actuación |
| CI-06-025 | Modificar, reestructurar o refinanciar | Préstamos y líneas | Solicitud de actuación |
| CI-06-026 | Determinar previsiones y tratamientos de cartera | Préstamos y líneas | Solicitud de actuación |
| CI-06-027 | Corregir obligaciones e imputaciones | Préstamos y líneas | Solicitud de actuación |
| CI-06-028 | Cancelar financiación o cerrar una línea | Préstamos y líneas | Solicitud de actuación |
| CI-06-029 | Preparar una operación de descuento | Préstamos y líneas | Solicitud de actuación |
| CI-06-030 | Formalizar y anticipar un descuento | Préstamos y líneas | Solicitud de actuación |
| CI-06-031 | Incorporar el resultado de un cheque descontado | Préstamos y líneas | Solicitud de actuación |
| CI-06-032 | Consultar una actuación de financiación | Préstamos y líneas | Consulta |
| CI-06-033 | Abrir o actualizar un caso de cobranza | Cobranzas y recuperaciones | Solicitud de actuación |
| CI-06-034 | Gestionar propuestas y acuerdos de cobranza | Cobranzas y recuperaciones | Solicitud de actuación |
| CI-06-035 | Iniciar y administrar la ejecución de una garantía | Garantías | Solicitud de actuación |
| CI-06-036 | Registrar y coordinar la aplicación de una recuperación | Cobranzas y recuperaciones | Solicitud de actuación |
| CI-06-037 | Consultar o cerrar un caso de cobranza | Cobranzas y recuperaciones | Consulta / Solicitud de actuación |
| CI-06-038 | Comunicar una decisión crediticia | Evaluación crediticia | Evento |
| CI-06-039 | Comunicar un cambio de garantía | Garantías | Evento |
| CI-06-040 | Comunicar constitución, desembolso o cambio de capacidad | Préstamos y líneas | Evento |
| CI-06-041 | Comunicar un cambio de deuda o aplicación | Préstamos y líneas | Evento |
| CI-06-042 | Comunicar mora o tratamiento de cartera | Préstamos y líneas | Evento |
| CI-06-043 | Comunicar un cambio de cobranza o recuperación | Cobranzas y recuperaciones | Evento |

## 3. Reglas comunes de integración

### 3.1. Identificación y recuperación

Toda actuación identifica operación, solicitud, objeto, servicio solicitante, causa y antecedentes.

Cuando corresponda, agrega:

- Actor y facultades.

- Versión esperada del objeto.

- Contrato y condiciones aceptadas.

- Importe, moneda y fechas.

- Aprobación crediticia.

- Compromiso de exposición.

- Garantías y afectaciones.

- Referencias monetarias.

- Evidencias y aprobaciones separadas.

Una misma actuación con contenido idéntico recupera su resultado. Un contenido diferente genera conflicto.

El identificador de un desembolso, pago, utilización o recuperación impide duplicar su efecto aunque se comunique por
mensajes distintos.

Una respuesta perdida exige consultar la actuación original. No autoriza a repetir la entrega ni a liberar compromisos.

### 3.2. Autoridad sobre la información

| Información | Autoridad |
| --- | --- |
| Decisión crediticia individual | Evaluación crediticia |
| Políticas, clasificación, exposición y límites compartidos | Riesgos |
| Acuerdo y aceptación | Contratos |
| Deuda de préstamos, líneas y descuentos | Préstamos y líneas |
| Garantías y distribución de cobertura | Garantías |
| Gestión de recuperación | Cobranzas y recuperaciones |
| Movimientos de cuentas y utilización inmediata de descubierto | Fondos |
| Instrumentos descontados | Cheques y ECHEQ |
| Deuda de tarjetas | Cuenta de tarjeta |
| Liquidaciones y recursos externos | Tesorería |
| Representación contable | Contabilidad |

Una consulta no transfiere la autoridad sobre los datos consultados.

### 3.3. Aprobación, exposición y constitución

La aprobación favorable no queda utilizable sin la afectación de exposición que corresponda.

Consultar exposición no compromete capacidad.

La aprobación, su aplicación al producto y la transformación del compromiso conservan referencias relacionadas. No se
libera primero un compromiso para intentar constituirlo nuevamente sin protección frente a operaciones concurrentes.

El vencimiento de la aprobación original no elimina la exposición de un producto ya constituido.

Una constitución incierta mantiene el compromiso hasta comprobar su resultado.

### 3.4. Capital y dinero

El límite concedido no es capital adeudado.

El capital surge de desembolsos o utilizaciones comprobados. En descuentos se reconoce el nominal conforme a su
tratamiento específico.

Cada desembolso distingue:

- Capital bruto.

- Retenciones.

- Entrega neta.

- Destinatario.

- Aplicación de los conceptos retenidos.

Una retención no representa otra entrega al cliente. Solo cancela su concepto cuando la aplicación queda confirmada.

### 3.5. Cálculo y vigencia

Los cálculos conservan entradas, fuentes, condiciones, fechas, versiones y resultado.

Los importes intermedios mantienen al menos doce decimales. Los exigibles se redondean a la unidad mínima de la moneda,
al más cercano, elevando los empates positivos.

La última amortización absorbe el residuo de capital. No se crea capital adicional por redondeo.

Los cronogramas conservan su anclaje original. El ajuste a hábil siguiente no desplaza las cuotas posteriores ni genera
por sí solo interés adicional o mora.

Cambiar la fecha de desembolso exige recalcular y obtener aceptación de las condiciones afectadas antes de ejecutar.

### 3.6. Intereses, pagos y correcciones

No se capitalizan automáticamente intereses ni se generan intereses sobre intereses, comisiones o tributos.

No se agrega tasa punitoria ni penalidad fija por atraso en las modalidades ordinarias.

Un pago comprobado se aplica una sola vez. Una corrección de imputación reutiliza ese ingreso; no crea otro cobro.

Las reversiones utilizan los importes registrados y conservan el vínculo con el hecho original.

### 3.7. Eventos

Todo evento identifica hecho, objeto, versión o secuencia, causa, momento, referencias y pendientes.

Los eventos de financiación referencian los movimientos ejecutados por Fondos o Tesorería. No representan otra entrega o
cobro.

Los consumidores distinguen deuda, exposición, dinero y representación contable para evitar sumar dos veces el mismo
hecho.

## 4. Contratos de Evaluación crediticia

### CI-06-001. Preparar y completar una evaluación crediticia

Proveedor: Evaluación crediticia. Consumidores: Solicitudes de productos, Préstamos y líneas, Emisión de tarjetas y
Gestión administrativa.

Entrada: solicitud, producto, solicitante, obligados, importe, moneda, plazo, finalidad y garantías ofrecidas;
antecedentes y documentos con fuente y fecha.

Actuación: reúne información, consulta obligaciones en sus módulos acreedores y registra suficiencia, vigencia y
observaciones.

Salida: evaluación identificada, antecedentes utilizados, requisitos faltantes y estado de preparación.

Reglas y aceptación: distingue ingresos declarados de comprobados. Una fuente sin respuesta no demuestra ausencia de
deuda. Una garantía ofrecida no se informa como constituida. Repetir la solicitud recupera la misma evaluación.

### CI-06-002. Analizar capacidad de pago y exposición

Proveedor: Evaluación crediticia. Consumidores: Solicitudes de productos, Préstamos y líneas, Emisión de tarjetas y
Gestión administrativa.

Entrada: evaluación preparada, condiciones propuestas, antecedentes y versión de política aplicable.

Actuación: determina capacidad, servicio de deuda, escenarios, exposición y cobertura. Consulta a Riesgos los criterios
y la situación consolidada.

Salida: análisis, cálculos, fuentes, escenarios, impedimentos y conclusión.

Reglas y aceptación: una garantía suficiente no compensa automáticamente un flujo insuficiente. Una tasa variable
requiere el escenario adverso configurado. Los descuentos consideran cliente, instrumento y librador sin duplicar la
misma exposición.

### CI-06-003. Resolver una solicitud de financiación

Proveedor: Evaluación crediticia. Consumidores: Solicitudes de productos y Gestión administrativa; Préstamos y líneas y
Emisión de tarjetas para decisiones relacionadas con sus productos.

Entrada: evaluación y análisis, condiciones, decisión propuesta, facultades y aprobación separada cuando sea humana;
política aprobada cuando sea automática.

Actuación: aprueba, condiciona o rechaza. Para una decisión favorable coordina la afectación de exposición con Riesgos.

Salida: decisión, alcance, condiciones por etapa, vigencia y referencia de exposición; o decisión todavía no utilizable
por afectación pendiente.

Reglas y aceptación: la aprobación puede utilizarse durante un máximo de treinta días corridos, limitado por sus
antecedentes. Dos solicitudes de 80 que compiten por capacidad total de 100 no pueden quedar ambas confirmadas por una
consulta previa del saldo.

### CI-06-004. Revisar, vencer o retirar una aprobación

Proveedor: Evaluación crediticia. Consumidores: Solicitudes de productos, Préstamos y líneas, Emisión de tarjetas y
Gestión administrativa.

Entrada: aprobación, versión, causa, cambios solicitados y evidencia de contratación, constitución o utilización.

Actuación: revisa continuidad, sustitución, vencimiento o retiro y coordina sus consecuencias con Riesgos y el producto.

Salida: decisión actualizada, compromiso mantenido, transformado o liberado, y pendientes.

Reglas y aceptación: cambiar capital, moneda, plazo u obligados exige revisión. El retiro no elimina contratos ni
productos existentes. Una sustitución no compromete dos veces el mismo importe. Una constitución incierta impide liberar
exposición.

### CI-06-005. Consultar una evaluación o aprobación

Proveedor: Evaluación crediticia. Consumidores: servicios solicitantes, Contratos, Riesgos, Garantías y Gestión
administrativa.

Entrada: evaluación o aprobación, etapa y momento de consulta.

Salida: antecedentes, análisis, decisión, alcance, condiciones, vigencia, exposición, aplicación al producto y
actuaciones pendientes.

Reglas: una aprobación condicionada no se informa como habilitación general. La consulta no reserva exposición ni
confirma su aplicación.

### CI-06-006. Confirmar la aplicación de una aprobación

Proveedor: Evaluación crediticia. Consumidores: Préstamos y líneas y el servicio responsable de constituir el producto
de tarjeta.

Entrada: aprobación y versión, producto a constituir, actuación de constitución, condiciones comprobadas, importe,
moneda y referencia del compromiso de exposición.

Actuación: comprueba vigencia y correspondencia y registra la aplicación a esa constitución. Coordina con Riesgos la
continuidad o transformación del compromiso.

Salida: aplicación confirmada para el producto y actuación identificados; impedimento; o transformación pendiente.

Reglas y aceptación: el uso queda ordenado frente a retiro y vencimiento. Repetirlo para la misma constitución no
consume otro compromiso. La confirmación no crea por sí sola el producto ni el desembolso. Si la constitución posterior
falla, se resuelve el compromiso mediante una actuación vinculada.

## 5. Contratos de Garantías

### CI-06-007. Registrar y evaluar una garantía

Proveedor: Garantías. Consumidores: Evaluación crediticia, Préstamos y líneas y Gestión administrativa.

Entrada: bien, derecho o garante; titularidad, facultades, valuación, moneda, fuente, vigencia, cargas, prioridades y
documentos.

Actuación: registra el respaldo y determina admisibilidad y cobertura conforme a los criterios de Riesgos.

Salida: garantía, valor económico, cobertura admitida, cobertura comprometida y disponible, incertidumbres y requisitos.

Reglas y aceptación: cargas desconocidas no equivalen a inexistentes. Con valor 100 y cobertura del 70 %, la cobertura
inicial es 70; si ya hay 40 afectados, no quedan otros 70 disponibles.

### CI-06-008. Constituir y afectar una garantía

Proveedor: Garantías. Consumidores: Préstamos y líneas y Gestión administrativa.

Entrada: garantía, contrato, obligaciones cubiertas, distribución, prioridad, importe o alcance de afectación y
evidencias constitutivas.

Actuación: comprueba formalización y afecta cobertura de manera compatible con compromisos existentes.

Salida: constitución y afectación confirmadas, cobertura asignada o requisitos pendientes.

Reglas y aceptación: un acuerdo sin la actuación constitutiva exigida no demuestra perfeccionamiento. Dos afectaciones
concurrentes no utilizan íntegramente la misma cobertura. Constituir la garantía no desembolsa automáticamente.

### CI-06-009. Revisar, sustituir o liberar una garantía

Proveedor: Garantías. Consumidores: Préstamos y líneas, Cobranzas y recuperaciones y Gestión administrativa.

Entrada: garantía y versión, causa, valuación o seguro actualizado, obligaciones cubiertas, actuación propuesta y
decisiones aprobadas.

Actuación: revisa cobertura, solicita refuerzo, sustituye o libera total o parcialmente.

Salida: cobertura actualizada, sustitución confirmada, liberación económica o documental y pendientes.

Reglas y aceptación: consulta todas las obligaciones cubiertas. El respaldo anterior permanece hasta confirmar el
sustituto. Menor cobertura requiere las decisiones crediticia y de riesgo correspondientes. La pérdida de valor no
incrementa capital.

### CI-06-010. Consultar una garantía y su cobertura

Proveedor: Garantías. Consumidores: Evaluación crediticia, Préstamos y líneas, Cobranzas y recuperaciones, Riesgos,
Registración contable y Gestión administrativa.

Entrada: garantía, obligación, titular o ámbito de cobertura; momento de consulta.

Salida: naturaleza, titularidad, valuación, vigencia, cargas, prioridades, constitución, afectaciones, distribución,
seguros, ejecuciones y liberaciones.

Reglas: distingue valor, cobertura admitida y cobertura disponible. Consultar no compromete capacidad para una futura
afectación.

## 6. Contratos de Préstamos y líneas

### CI-06-011. Constituir un préstamo o una línea

Proveedor: Préstamos y líneas. Consumidor: Solicitudes de productos.

Entrada: actuación de constitución, contrato vigente, aprobación, condiciones aceptadas, obligados, moneda, modalidad,
garantías y requisitos por etapa.

Actuación: confirma aplicación de la aprobación, identifica el producto, registra condiciones y coordina transformación
de exposición. Comunica el resultado a Solicitudes de productos.

Salida: financiación identificada, límite, capital utilizado inicialmente cero, condiciones y requisitos pendientes.

Reglas y aceptación: una línea de 1.000 sin utilizaciones tiene capital cero. El descubierto identifica su cuenta y
circuito exclusivo en Fondos. La repetición no crea otro producto ni duplica exposición.

### CI-06-012. Calcular condiciones, cronograma y CFT

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos, Contratos, Evaluación crediticia y Gestión
administrativa.

Entrada: capital, moneda, modalidad, tasa y convención, sistema, fechas, desembolsos, cargos, tributos, seguros
obligatorios, gracia, calendario y demás condiciones económicas.

Actuación: genera una determinación económica identificada y reproducible.

Salida: tasas utilizadas, cronograma, amortizaciones, intereses, conceptos adicionales, entregas netas, flujos, CFT,
supuestos y vigencia.

Reglas:

- Admite francés, alemán y capital al vencimiento según el producto.

- Distingue tasa nominal, efectiva y periódica.

- Aplica las convenciones de períodos irregulares definidas en las operaciones.

- En líneas y descubierto utiliza interés simple diario Actual/365 sobre capital utilizado al cierre diario.

- El CFT utiliza flujos netos y fechas reales con base de 365 días.

- Un cargo retenido reduce la entrega neta y no se agrega otra vez como pago.

- Una línea sin utilizaciones determinadas no recibe un CFT único general.

- Un concepto obligatorio desconocido o flujos sin tasa única impiden publicar un CFT definitivo.

Aceptación: capital bruto 1.000, cargo retenido 20 y pago 1.100 a 365 días determina el CFT sobre 980 recibidos:
aproximadamente 12,24 %.

### CI-06-013. Desembolsar una financiación

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: financiación, entrega identificada, importe bruto, retenciones, neto, moneda, destino autorizado, fecha y
determinación económica aceptada.

Condiciones: contrato, requisitos, garantías y exposición comprobados; capacidad suficiente.

Actuación: coordina la entrega con Fondos o Tesorería, comprueba el resultado y reconoce el capital correspondiente.

Salida: entrega confirmada, rechazada o incierta; capital reconocido, retenciones aplicadas, neto entregado y
referencias.

Recuperación y aceptación: una entrega incierta no se repite. Un rechazo sin entrega no genera capital utilizado. Con
capital bruto 500 y cargo retenido 10, se identifican capital 500, cargo 10 y neto 490.

### CI-06-014. Administrar el límite de una línea

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: línea, versión, nuevo límite, vigencia, aprobación y referencia de exposición; modificación contractual cuando
corresponda.

Actuación: aplica aumentos, reducciones o vencimientos. Para descubierto comunica la capacidad a Fondos y verifica su
aplicación.

Salida: límite autorizado y aplicado, utilizados, compromisos, exceso, capacidad y pendientes.

Reglas y aceptación: reducir el límite no elimina deuda. Los compromisos firmes conservan cobertura protegida y los
inciertos continúan su seguimiento. Una aplicación pendiente en Fondos no se informa como completada.

### CI-06-015. Solicitar una utilización de línea

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: línea, utilización, importe, moneda, destino, fecha, condiciones y autorización.

Actuación: comprueba capacidad y compromete el uso antes de coordinar su desembolso mediante CI-06-013.

Salida: utilización y compromiso, entrega vinculada, capital confirmado y capacidad restante.

Reglas y aceptación: las utilizaciones concurrentes no exceden la capacidad disponible. La línea renovable recupera
capacidad por capital devuelto; la no renovable no habilita nuevas entregas por pagos. El descubierto no utiliza este
circuito: su utilización inmediata pertenece a Fondos.

### CI-06-016. Incorporar y conciliar utilización de descubierto

Proveedor: Préstamos y líneas. Consumidores: Fondos y Gestión administrativa para conciliaciones autorizadas.

Entrada: línea y cuenta, utilización o variación confirmada por Fondos, movimiento o corte, secuencia, fecha efectiva,
capital utilizado, compromisos y exceso.

Actuación: incorpora las variaciones y contrasta su posición con la autoridad monetaria de Fondos.

Salida: posición conciliada, variación reconocida o diferencia pendiente.

Reglas y aceptación: una acreditación ordinaria reduce directamente el capital según Fondos. No se redistribuye primero
a intereses ni se registra otra cancelación del mismo capital. Un evento antiguo no reemplaza una posición posterior.

Con capital utilizado 100, interés pendiente 10 y acreditación de 50, el resultado es capital 50 e interés pendiente 10.

### CI-06-017. Devengar y liquidar conceptos económicos

Proveedor: Préstamos y líneas. Consumidor: Gestión administrativa; el mismo contrato funcional rige las actuaciones
periódicas del proveedor.

Entrada: financiación, período o fecha de corte, condiciones y versiones, posiciones efectivas, referencias de tasas y
determinaciones tributarias.

Actuación: calcula devengamientos, determina conceptos exigibles y conserva diferencias respecto de lo previamente
registrado.

Salida: cálculo, base, períodos, tasas, devengado, exigible, ajustes y referencias.

Reglas: no redondea diariamente antes de acumular ni capitaliza intereses. Si falta una referencia variable, utiliza
provisionalmente la última válida durante un máximo de un período de revisión; otra liquidación definitiva sin
referencia requiere resolución contractual.

Aceptación: interés devengado 10 se registra como deuda de interés, sin simular un cobro. Repetir la liquidación no crea
otro concepto.

### CI-06-018. Consultar deuda y posición de financiación

Proveedor: Préstamos y líneas. Consumidores: Pagos, Contratos, Evaluación crediticia, Garantías, Cobranzas y
recuperaciones, Riesgos, Registración contable, Extractos y Gestión administrativa.

Entrada: financiación, obligado, fecha de corte y alcance autorizado.

Salida: contrato y condiciones; límite, capital, compromisos, cronograma, intereses devengados y exigibles, cargos,
tributos, pagos, mora, garantías, exposición y saldos a favor.

Reglas: diferencia deuda exigible de futura y capital de conceptos adicionales. En descubierto identifica el corte de
Fondos utilizado. Una consulta histórica conserva el corte solicitado.

### CI-06-019. Preparar la aplicación de un pago

Proveedor: Préstamos y líneas. Consumidores: Pagos, Cobranzas y recuperaciones y Gestión administrativa.

Entrada: financiación, pagador, deudor, importe, moneda, fecha efectiva prevista, referencia del pago y destino
solicitado.

Actuación: comprueba la obligación, determina conceptos y registra una preparación vinculada al pago. Las preparaciones
concurrentes se consideran para evitar aplicaciones incompatibles.

Salida: aceptación preliminar o preparación confirmada, según la etapa solicitada; distribución propuesta, versión de
deuda, excedente y condiciones de confirmación.

Reglas: preparar no cancela deuda ni demuestra ingreso. La imputación ordinaria atiende obligaciones vencidas desde la
más antigua, luego las exigibles del día; dentro de cada obligación, intereses ordinarios y capital, tributos asociados
y, después, comisiones y gastos.

Aceptación: sobre interés 10 y capital exigible 90, un pago de 40 prepara interés 10 y capital 30.

### CI-06-020. Confirmar o cancelar la preparación de un pago

Proveedor: Préstamos y líneas. Consumidores: Pagos, Cobranzas y recuperaciones y Gestión administrativa.

Entrada: preparación, actuación, evidencia de ingreso o de cancelación sin ejecución, importe y fecha efectiva
confirmados.

Actuación: aplica el ingreso una vez o cancela la preparación cuando se comprueba ausencia de ejecución pendiente. Si
cambió la deuda, determina la aplicación admisible y conserva el excedente sin perder la referencia del ingreso.

Salida: conceptos cancelados, deuda resultante, excedente, fecha efectiva y pendientes.

Reglas y aceptación: un ingreso de 120 para deuda de 100 aplica 100 y conserva excedente 20 para precancelación
autorizada o devolución. Un pago oportuno procesado después no produce mora artificial.

En descubierto, el componente de capital se acredita en la cuenta deudora; la reducción informada por Fondos es la única
cancelación de ese capital.

### CI-06-021. Cobrar intereses y cargos de descubierto

Proveedor: Préstamos y líneas. Consumidores: Pagos y Gestión administrativa.

Entrada: línea, cuenta, conceptos exigibles, importe, autorización y alcance total o parcial del cobro.

Actuación: solicita a Fondos un débito separado que utiliza únicamente fondos propios disponibles. Cancela los conceptos
al confirmar el movimiento.

Salida: cobro confirmado, insuficiencia, resultado incierto o parcial expresamente instruido; conceptos cancelados y
remanentes.

Reglas y aceptación: no utiliza descubierto para cobrar intereses o cargos del propio descubierto. Con saldo propio 20 e
interés autorizado 10, debita 10. Con saldo cero no genera otro descubierto de 10.

### CI-06-022. Preparar una liquidación de precancelación

Proveedor: Préstamos y líneas. Consumidores: Contratos, Pagos y Gestión administrativa.

Entrada: financiación, fecha, alcance total o parcial y, en precancelación parcial, reducción de plazo o cuota.

Actuación: calcula capital, conceptos devengados, pagos pendientes y nuevo cronograma cuando corresponda.

Salida: liquidación identificada, vigencia hasta el final de la fecha indicada, importe, desglose, efectos y condiciones
de ejecución.

Reglas: no cobra intereses futuros ni comisión de precancelación ordinaria. En descuento reduce la liquidación por
interés retenido todavía no devengado. Sin elección de plazo o cuota no confirma el nuevo cronograma.

### CI-06-023. Ejecutar una precancelación

Proveedor: Préstamos y líneas. Consumidores: Pagos, Contratos y Gestión administrativa.

Entrada: liquidación vigente, aceptación, pago comprobado o instrucción autorizada de cobro y elección aplicable.

Actuación: comprueba fondos, aplica la cancelación y actualiza cronograma, exposición y garantías.

Salida: capital cancelado, conceptos aplicados, nuevo cronograma o cancelación total, excedentes y pendientes.

Reglas y aceptación: una liquidación vencida requiere actualización. Capital 1.000 con anticipo neto de capital 300 deja
capital 700. En descuentos no libera un instrumento con presentación firme o incierta.

### CI-06-024. Determinar mora y obligaciones vencidas

Proveedor: Préstamos y líneas. Consumidores: Cobranzas y recuperaciones, Riesgos y Gestión administrativa.

Entrada: financiación o cartera, corte y referencias de pagos e ingresos pendientes conocidos.

Actuación: comprueba vencimientos ajustados, pagos efectivos e imputaciones pendientes y determina impago y antigüedad.

Salida: obligaciones vencidas, importes por concepto, inicio y días de mora, causas y verificaciones pendientes.

Reglas y aceptación: la mora comienza el día siguiente al vencimiento ajustado. Domingo trasladado a lunes no produce
mora el lunes. Un ingreso oportuno sin imputar debe comprobarse antes de atribuir incumplimiento.

### CI-06-025. Modificar, reestructurar o refinanciar

Proveedor: Préstamos y líneas. Consumidores: Contratos, Solicitudes de productos y Cobranzas y recuperaciones.

Entrada: financiación, acuerdo efectivo, evaluación y aprobación cuando correspondan, exposición, deuda al corte,
tratamiento de cada concepto y garantías.

Actuación: modifica condiciones o sustituye obligaciones conforme al acuerdo, coordinando deuda, cronograma, CFT,
exposición y cobertura.

Salida: condiciones efectivas, obligaciones sustituidas y nuevas, capital, intereses, cargos, quitas y pendientes.

Reglas y aceptación: una propuesta no modifica deuda. El mismo capital no queda exigible simultáneamente en la
obligación sustituida y la nueva. Las quitas condicionadas requieren cumplimiento; una condonación definitiva no revive
por perder otro beneficio.

### CI-06-026. Determinar previsiones y tratamientos de cartera

Proveedor: Préstamos y líneas. Consumidores: Riesgos, Registración contable y Gestión administrativa.

Entrada: cartera y corte, posiciones, garantías, clasificación y metodología de Riesgos, versiones y valores previamente
reconocidos.

Actuación: calcula cobertura requerida y variación respecto de la registrada.

Salida: base, clasificación, previsión requerida, diferencia y tratamiento de cartera con trazabilidad.

Reglas y aceptación: pasar de previsión 20 a 25 comunica variación 5. Previsionar o dar de baja contablemente no paga ni
condona la deuda. No calcula otra previsión sobre deuda de tarjetas.

### CI-06-027. Corregir obligaciones e imputaciones

Proveedor: Préstamos y líneas. Consumidores: Pagos, Contratos y Gestión administrativa.

Entrada: hecho original, error, evidencia, tratamiento solicitado, fechas pertinentes y aprobación separada para ajustes
manuales.

Actuación: reconstruye condiciones y registra diferencias vinculadas; coordina dinero, mora, exposición y documentos
afectados.

Salida: corrección, conceptos e imputaciones rectificados, movimientos relacionados y posición resultante.

Reglas y aceptación: corregir un pago de 100 aplicado a otra obligación no cobra otros 100. Una devolución requiere
efecto monetario identificado. Las correcciones de descubierto se concilian con Fondos y las de descuento con Cheques y
ECHEQ.

### CI-06-028. Cancelar financiación o cerrar una línea

Proveedor: Préstamos y líneas. Consumidores: Contratos, Solicitudes de productos y Gestión administrativa.

Entrada: financiación, causa, instrucción o acuerdo, evidencias de pagos y causas extintivas.

Actuación: impide nuevos usos, comprueba conceptos y compromisos, coordina exposición, garantías y saldos a favor, y
registra cancelación o administración residual.

Salida: capacidad cerrada, situación económica, obligaciones pendientes, liberaciones y trámites documentales.

Reglas y aceptación: vencer una línea con utilización 50 no elimina ese capital. Capital cero con interés 10 no permite
informar libre deuda total. Las reservas firmes o inciertas conservan tratamiento. Las demoras internas no prolongan
cargos futuros.

### CI-06-029. Preparar una operación de descuento

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: cliente, cheque diferido, moneda, fecha de anticipo, tasa, comisiones, tributos y antecedentes.

Actuación: comprueba y compromete el instrumento mediante Cheques y ECHEQ, coordina evaluación y exposición y prepara
liquidación y CFT.

Salida: operación, compromiso, nominal, interés anticipado, retenciones, neto, fecha prevista de cobro y requisitos.

Reglas: cada cheque mantiene financiación individual. Debe tener fecha de pago futura, legitimación comprobada y
ausencia de compromisos incompatibles. Solo se admite descuento con responsabilidad del cliente.

Aceptación: nominal 1.000, treinta días y tasa nominal anual 36,5 % produce interés anticipado 30; con comisión 5 y sin
tributos, neto 965.

### CI-06-030. Formalizar y anticipar un descuento

Proveedor: Préstamos y líneas. Consumidor: Solicitudes de productos; Gestión administrativa para continuar una actuación
autorizada.

Entrada: operación preparada, contrato y aceptación, aprobación aplicable, compromiso del instrumento, cesión y
liquidación vigente.

Actuación: confirma la cesión al banco, ejecuta anticipo y retenciones, reconoce la financiación y solicita presentación
oportuna.

Salida: financiación, nominal, interés retenido pendiente de devengamiento, neto entregado, referencias y pendientes.

Reglas y aceptación: al confirmar el anticipo se reconoce el nominal, conservando separado el interés retenido no
devengado. No reconoce todo ese interés como ganado el primer día. Si el anticipo falla con cesión confirmada, comprueba
ausencia de entrega y regulariza la cesión.

### CI-06-031. Incorporar el resultado de un cheque descontado

Proveedor: Préstamos y líneas. Consumidores: Cheques y ECHEQ y Gestión administrativa para conciliaciones autorizadas.

Entrada: instrumento, financiación, presentación, pago o rechazo confirmado, importe, fecha, referencia económica y
evidencia.

Actuación: aplica el cobro a la financiación o registra rechazo e incumplimiento. Concilia pagos del cliente,
recuperaciones y cobros del instrumento.

Salida: nominal cancelado o pendiente, intereses, excedente, mora y situación del descuento.

Reglas y aceptación: el cobro del cheque no se acredita nuevamente como ingreso libre al cliente. El rechazo no crea
otro capital. Desde el día siguiente al vencimiento efectivo, el capital impago devenga remuneración simple Actual/365 a
la tasa contractual, sin volver a cobrar el interés anticipado original.

Las demoras exclusivamente atribuibles al banco no generan interés adicional al cliente. Si cliente y cheque pagan la
misma deuda, se cancela una vez y se trata el excedente según su titular.

### CI-06-032. Consultar una actuación de financiación

Proveedor: Préstamos y líneas. Consumidores: servicios originantes, Garantías, Cobranzas y recuperaciones, Riesgos,
Reclamos y Gestión administrativa.

Entrada: actuación de constitución, cálculo, desembolso, utilización, pago, ajuste, descuento o cierre.

Salida: contenido, condiciones, etapas, efectos, referencias monetarias, exposición, evidencias y pendientes.

Reglas: diferencia solicitud recibida de ejecución confirmada. Recupera el resultado original sin repetir dinero, deuda
ni exposición.

## 7. Contratos de Cobranzas y recuperaciones

### CI-06-033. Abrir o actualizar un caso de cobranza

Proveedor: Cobranzas y recuperaciones. Consumidores: Préstamos y líneas, Cuenta de tarjeta y Gestión administrativa.

Entrada: acreedor, obligación, deudor, deuda al corte, mora, controversias, garantías y pagos pendientes conocidos.

Actuación: abre o actualiza el caso, comprueba vigencia de la información y determina estrategia conforme a las
decisiones aplicables.

Salida: caso, obligaciones gestionadas, responsable, estrategia y verificaciones pendientes.

Reglas y aceptación: la información del caso referencia la deuda del acreedor. No crea otra deuda. Un pago pendiente
debe comprobarse antes de reclamar un importe posiblemente cancelado.

### CI-06-034. Gestionar propuestas y acuerdos de cobranza

Proveedor: Cobranzas y recuperaciones. Consumidores: Gestión administrativa y módulos acreedores.

Entrada: caso, contactos, propuesta, promesa, condiciones, facultades y aprobaciones; acuerdo formalizado cuando exista.

Actuación: registra gestiones y promesas, tramita propuestas y coordina acuerdos con el acreedor y Contratos.

Salida: gestión, propuesta o acuerdo; condiciones, vencimientos, aceptación, tratamiento solicitado al acreedor y
seguimiento.

Reglas y aceptación: una promesa no reduce deuda. Las quitas requieren aprobación separada. Los gastos externos
requieren evidencia y fundamento; los gastos internos ordinarios no se agregan como penalidad. La modificación económica
la confirma el acreedor.

### CI-06-035. Iniciar y administrar la ejecución de una garantía

Proveedor: Garantías. Consumidor: Cobranzas y recuperaciones.

Entrada: garantía, caso, obligaciones, incumplimiento comprobado, decisión autorizada, prioridades y formalidades
simuladas.

Actuación: registra y sigue la ejecución, venta, pago del garante o adjudicación, preservando evidencia y cobertura
restante.

Salida: ejecución identificada, etapas, resultado, destinatarios, prioridades y pendientes.

Reglas y aceptación: iniciar ejecución o tasar no cancela deuda. Una adjudicación no se presenta como efectivo. El pago
del garante conserva su origen y derechos derivados. El resultado requiere aplicación por el acreedor.

### CI-06-036. Registrar y coordinar la aplicación de una recuperación

Proveedor: Cobranzas y recuperaciones. Consumidores: Garantías y Gestión administrativa.

Entrada: caso, fuente de recupero, evidencia de ingreso o adjudicación, importe o valor admitido, gastos fundados,
prioridades y obligaciones destinatarias.

Actuación: comprueba el recupero, determina distribución y solicita su aplicación al acreedor. Concilia cobros
concurrentes y excedentes.

Salida: recupero comprobado, aplicaciones confirmadas, saldo pendiente, excedentes y derechos derivados.

Reglas y aceptación: valor de tasación 100 con venta neta 80 representa recupero 80. Para deuda 70 y recupero aplicable
80, cancela 70 y conserva excedente 10 a favor de quien corresponda. El excedente no es ingreso del banco.

### CI-06-037. Consultar o cerrar un caso de cobranza

Proveedor: Cobranzas y recuperaciones. Consumidores: módulos acreedores, Gestión administrativa y Reclamos.

Entrada: caso y alcance de consulta; para cerrar, causa, resultado y evidencia.

Salida: deuda referenciada, gestiones, propuestas, acuerdos, recuperaciones, aplicaciones, controversias y pendientes;
cierre confirmado o impedimentos.

Reglas: cerrar un caso no extingue deuda. La situación económica se confirma con el acreedor. Los contactos y documentos
se exponen conforme a las facultades del consultante.

## 8. Contratos de eventos

### CI-06-038. Comunicar una decisión crediticia

Proveedor: Evaluación crediticia. Consumidores: Solicitudes de productos, Préstamos y líneas, Emisión de tarjetas,
Contratos, Garantías, Riesgos y Comunicaciones.

Contenido: evaluación, decisión, versión, condiciones por etapa, vigencia, exposición y aplicación al producto.

Reglas y aceptación: diferencia decisión favorable de aprobación utilizable. Un evento de retiro no elimina un producto
constituido ni libera por sí solo exposición.

### CI-06-039. Comunicar un cambio de garantía

Proveedor: Garantías. Consumidores: Evaluación crediticia, Préstamos y líneas, Cobranzas y recuperaciones, Riesgos,
Registración contable y Documentos y comprobantes.

Contenido: garantía, versión, valuación, cobertura, afectaciones, constitución, sustitución, liberación o ejecución;
evidencias y pendientes.

Reglas y aceptación: distingue valoración de recupero y liberación económica de documental. Un cambio de valor no crea
capital adeudado.

### CI-06-040. Comunicar constitución, desembolso o cambio de capacidad

Proveedor: Préstamos y líneas. Consumidores: Solicitudes de productos, Contratos, Riesgos, Garantías, Posiciones y
liquidez, Registración contable, Documentos y comprobantes y Comunicaciones.

Contenido: financiación, hecho, versión, contrato, aprobación, límite, utilización, compromiso, entrega bruta y neta,
retenciones y referencias.

Reglas y aceptación: constituir una línea no comunica deuda por todo el límite. Un desembolso referencia el efecto
monetario ya ejecutado. Los cambios de descubierto identifican la confirmación de Fondos.

### CI-06-041. Comunicar un cambio de deuda o aplicación

Proveedor: Préstamos y líneas. Consumidores: Pagos, Cobranzas y recuperaciones, Garantías, Riesgos, Registración
contable, Documentos y comprobantes, Comunicaciones y Extractos.

Contenido: financiación, conceptos, devengamiento, exigibilidad, pago, corrección, precancelación, refinanciación o
cancelación; variaciones, posición resultante y referencias.

Reglas y aceptación: diferencia devengar de cobrar. Un pago recibido por Pagos y aplicado aquí conserva el mismo origen
económico. No se registra otro ingreso por consumir el evento.

### CI-06-042. Comunicar mora o tratamiento de cartera

Proveedor: Préstamos y líneas. Consumidores: Riesgos, Cobranzas y recuperaciones, Registración contable e Información
regulatoria.

Contenido: financiación o cartera, corte, impago, antigüedad, pagos considerados, clasificación recibida, metodología,
previsión requerida y variación.

Reglas y aceptación: una previsión o baja contable no comunica extinción de deuda. Una regularización conserva el
historial y rectifica consecuencias de mora artificial.

### CI-06-043. Comunicar un cambio de cobranza o recuperación

Proveedor: Cobranzas y recuperaciones. Consumidores: acreedor responsable, Garantías, Riesgos, Registración contable,
Gestión administrativa, Documentos y comprobantes y Comunicaciones.

Contenido: caso, gestión, acuerdo, recupero, fuente, aplicaciones, excedentes y resultado.

Reglas y aceptación: una promesa no representa un pago. Un recupero informado como pendiente de aplicación no se
presenta como deuda cancelada. Los efectos confirmados por el acreedor conservan sus referencias originales.

## 9. Integraciones consumidas

### 9.1. Institución y oferta comercial

| Contrato | Uso |
| --- | --- |
| CI-01-004 | Comprobar facultades institucionales. |
| CI-01-005 | Consultar definición y modalidad del producto. |
| CI-01-008 | Comprobar elegibilidad y compatibilidad. |
| CI-01-011 | Determinar condiciones comerciales. |
| CI-01-012 | Recuperar la versión de oferta utilizada. |
| CI-01-014 | Consultar calendarios y convenciones. |
| CI-01-015 | Determinar vencimientos y series. |

Los parámetros configurables deben corresponder a comportamientos definidos. Su ausencia no se interpreta como cero.

### 9.2. Personas y relaciones bancarias

| Contrato | Uso |
| --- | --- |
| CI-02-002 | Identificar solicitantes, obligados, garantes y pagadores. |
| CI-02-005 | Consultar relaciones declaradas relevantes. |
| CI-02-015 | Consultar la relación comercial. |
| CI-02-020 | Consultar representación. |
| CI-02-021 | Comprobar representación para el acto concreto. |

La relación declarada entre personas no sustituye una garantía, obligación solidaria o representación formalizada.

### 9.3. Contratación

| Contrato | Uso |
| --- | --- |
| CI-03-016 | Informar el resultado de constitución del producto. |
| CI-03-022 | Consultar contrato, condiciones y situación. |

Solicitudes de productos coordina la constitución. Contratos mantiene acuerdos, aceptaciones, modificaciones y
arrepentimiento. Préstamos y líneas proporciona los cálculos y ejecuta las consecuencias económicas de su competencia.

### 9.4. Cuentas y fondos

| Contrato | Uso |
| --- | --- |
| CI-04-002 | Comprobar cuenta y capacidades. |
| CI-04-012 | Consultar posición y movimientos. |
| CI-04-017 | Ejecutar créditos o débitos monetarios correspondientes. |
| CI-04-018 | Ejecutar movimientos internos relacionados cuando la operación lo requiera. |
| CI-04-019 | Aplicar el límite operativo de descubierto. |
| CI-04-020 | Consultar utilización de descubierto. |
| CI-04-021 | Coordinar correcciones monetarias. |
| CI-04-022 | Recuperar resultados monetarios. |
| CI-04-034 | Recibir movimientos confirmados pertinentes. |
| CI-04-035 | Recibir variaciones de descubierto. |
| CI-04-040 | Comprobar facultades y admisión de operaciones de cuenta. |

El evento de movimiento y el de variación de descubierto pueden describir consecuencias relacionadas del mismo hecho. Se
vinculan sin reconocer dos reducciones de capital.

### 9.5. Pagos y transferencias

| Contrato | Uso |
| --- | --- |
| CI-05-010 | Coordinar un cobro mediante Pagos cuando corresponda. |
| CI-05-011 | Coordinar devoluciones o correcciones de pagos. |
| CI-05-012 | Consultar ejecución, aplicación y reversión de un pago. |
| CI-05-024 | Comprometer un cheque para descuento. |
| CI-05-025 | Confirmar la cesión o liberar un compromiso no ejecutado. |
| CI-05-026 | Solicitar presentación del cheque al cobro. |
| CI-05-028 | Resolver medidas y devolución del instrumento. |
| CI-05-029 | Consultar instrumento y actuaciones. |
| CI-05-039 | Recibir cambios de pago o reversión. |
| CI-05-042 | Recibir pago, rechazo y cambios del instrumento descontado. |

Un evento recibido puede originar una actuación interna de incorporación, pero ambas vías conservan el mismo
identificador del hecho para impedir duplicaciones.

### 9.6. Otros módulos

| Proveedor | Integración requerida |
| --- | --- |
| Riesgos | Políticas, capacidad, afectación y transformación de exposición, clasificación y previsiones. |
| Cuenta de tarjeta | Deuda y pagos de tarjetas gestionados en cobranza. |
| Tesorería | Desembolsos y recuperaciones que utilicen sus circuitos. |
| Servicios de Riesgo y cumplimiento | Controles y restricciones de su competencia. |
| Criterios tributarios | Conceptos y determinaciones tributarias. |
| Registración contable | Representación de capital, intereses, retenciones, previsiones y recuperaciones. |
| Gestión administrativa | Actuaciones humanas y aprobaciones separadas. |
| Documentos y comprobantes | Evidencias, liquidaciones y constancias. |
| Comunicaciones | Propuestas, decisiones y resultados. |
| Reclamos | Controversias y errores denunciados. |
| Extractos | Exposición de obligaciones y movimientos. |

Los identificadores de los contratos proporcionados por estos módulos se establecen en sus documentos correspondientes.

## 10. Correspondencia con las operaciones funcionales

| Operación | Contratos principales |
| --- | --- |
| OF-06-01. Preparar una evaluación crediticia | CI-06-001, 005 |
| OF-06-02. Evaluar capacidad de pago y exposición | CI-06-002, 005 |
| OF-06-03. Aprobar, condicionar o rechazar financiación | CI-06-003, 005, 038 |
| OF-06-04. Revisar, vencer o retirar una aprobación | CI-06-004, 005, 006, 038 |
| OF-06-05. Registrar y evaluar garantías | CI-06-007, 010 |
| OF-06-06. Constituir y afectar garantías | CI-06-008, 010, 039 |
| OF-06-07. Revisar, sustituir y liberar garantías | CI-06-009, 010, 039 |
| OF-06-08. Constituir un préstamo o una línea | CI-06-006, 011, 032, 040 |
| OF-06-09. Calcular condiciones, cronogramas y CFT | CI-06-012, 032 |
| OF-06-10. Desembolsar financiación | CI-06-013, 032, 040 |
| OF-06-11. Administrar límites y utilizaciones | CI-06-014, 015, 016, 040 |
| OF-06-12. Devengar y liquidar conceptos económicos | CI-06-017, 018, 041 |
| OF-06-13. Recibir e imputar pagos | CI-06-019, 020, 021, 016, 041 |
| OF-06-14. Procesar precancelaciones | CI-06-022, 023, 041 |
| OF-06-15. Determinar mora y obligaciones vencidas | CI-06-024, 018, 042 |
| OF-06-16. Modificar, reestructurar y refinanciar | CI-06-025, 012, 041 |
| OF-06-17. Determinar previsiones y tratamientos de cartera | CI-06-026, 042 |
| OF-06-18. Gestionar cobranza y acuerdos | CI-06-033, 034, 037, 043 |
| OF-06-19. Ejecutar garantías y registrar recuperaciones | CI-06-035, 036, 039, 043 |
| OF-06-20. Corregir obligaciones e imputaciones | CI-06-027, 032, 041 |
| OF-06-21. Cancelar financiación y cerrar líneas | CI-06-028, 018, 032, 040, 041 |
| OF-06-22. Descontar cheques y aplicar su cobro | CI-06-029, 030, 031, 022, 023, 040, 041 |
