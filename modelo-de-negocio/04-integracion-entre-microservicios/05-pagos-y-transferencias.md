# Contratos de integración — Módulo 5. Pagos y transferencias

## 1. Propósito y alcance

Este documento define los contratos de integración proporcionados por los servicios:

- Transferencias.

- Pagos.

- Mandatos y recurrencias.

- Cheques y ECHEQ.

- Compensación y conciliación de pagos.

Comprende instrucciones, autorizaciones recurrentes, circulación de instrumentos, ejecución, aplicación, devoluciones,
intercambio externo y determinación de obligaciones entre participantes.

Fondos conserva la autoridad sobre reservas y movimientos de cuentas. Los módulos acreedores conservan la deuda y su
imputación. Liquidaciones de tesorería conserva la ejecución de las liquidaciones externas.

Los contratos aplican a los participantes y mecanismos externos simulados definidos por el modelo funcional.

## 2. Catálogo de contratos

Todos los contratos de este documento corresponden a la versión 1.

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-05-001 | Ejecutar una transferencia interna | Transferencias | Solicitud de actuación |
| CI-05-002 | Enviar una transferencia externa | Transferencias | Solicitud de actuación |
| CI-05-003 | Recibir una transferencia externa | Transferencias | Solicitud de actuación |
| CI-05-004 | Cancelar una instrucción de transferencia | Transferencias | Solicitud de actuación |
| CI-05-005 | Tramitar una devolución de transferencia | Transferencias | Solicitud de actuación |
| CI-05-006 | Consultar una transferencia y sus actuaciones | Transferencias | Consulta |
| CI-05-007 | Administrar un convenio de recaudación | Pagos | Solicitud de actuación |
| CI-05-008 | Consultar un convenio de recaudación | Pagos | Consulta |
| CI-05-009 | Identificar una obligación de pago | Pagos | Consulta |
| CI-05-010 | Ejecutar y aplicar un pago | Pagos | Solicitud de actuación |
| CI-05-011 | Anular, devolver o corregir un pago | Pagos | Solicitud de actuación |
| CI-05-012 | Consultar un pago y sus actuaciones | Pagos | Consulta |
| CI-05-013 | Constituir o modificar un mandato | Mandatos y recurrencias | Solicitud de actuación |
| CI-05-014 | Suspender, reanudar o revocar un mandato | Mandatos y recurrencias | Solicitud de actuación |
| CI-05-015 | Consultar y comprobar un mandato | Mandatos y recurrencias | Consulta |
| CI-05-016 | Administrar una programación | Mandatos y recurrencias | Solicitud de actuación |
| CI-05-017 | Originar una ocurrencia | Mandatos y recurrencias | Solicitud de actuación |
| CI-05-018 | Consultar una programación o una ocurrencia | Mandatos y recurrencias | Consulta |
| CI-05-019 | Solicitar la reversión de un débito autorizado | Mandatos y recurrencias | Solicitud de actuación |
| CI-05-020 | Tramitar la restitución y resolución de una reversión | Pagos | Solicitud de actuación |
| CI-05-021 | Habilitar y entregar una chequera | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-022 | Registrar la emisión de un cheque o ECHEQ | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-023 | Administrar aceptación, transmisión y aval | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-024 | Comprometer un instrumento para descuento | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-025 | Confirmar o liberar el compromiso para descuento | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-026 | Depositar y presentar un instrumento al cobro | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-027 | Resolver el pago o rechazo de una presentación | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-028 | Administrar medidas y devoluciones de instrumentos | Cheques y ECHEQ | Solicitud de actuación |
| CI-05-029 | Consultar un instrumento y sus actuaciones | Cheques y ECHEQ | Consulta |
| CI-05-030 | Administrar participantes y mecanismos de intercambio | Compensación y conciliación de pagos | Solicitud de actuación |
| CI-05-031 | Consultar participantes y mecanismos | Compensación y conciliación de pagos | Consulta |
| CI-05-032 | Intercambiar una instrucción o una consulta externa | Compensación y conciliación de pagos | Solicitud de actuación |
| CI-05-033 | Incorporar un mensaje o resultado externo | Compensación y conciliación de pagos | Solicitud de actuación |
| CI-05-034 | Consultar un intercambio y sus evidencias | Compensación y conciliación de pagos | Consulta |
| CI-05-035 | Conciliar registros y administrar diferencias | Compensación y conciliación de pagos | Solicitud de actuación |
| CI-05-036 | Determinar y consultar obligaciones de compensación | Compensación y conciliación de pagos | Solicitud de actuación / Consulta |
| CI-05-037 | Incorporar el resultado de una liquidación externa | Compensación y conciliación de pagos | Solicitud de actuación |
| CI-05-038 | Comunicar un cambio de transferencia | Transferencias | Evento |
| CI-05-039 | Comunicar un cambio de pago o reversión | Pagos | Evento |
| CI-05-040 | Comunicar un cambio de mandato o programación | Mandatos y recurrencias | Evento |
| CI-05-041 | Comunicar el resultado de una ocurrencia | Mandatos y recurrencias | Evento |
| CI-05-042 | Comunicar un cambio de instrumento | Cheques y ECHEQ | Evento |
| CI-05-043 | Comunicar un resultado de intercambio o conciliación | Compensación y conciliación de pagos | Evento |
| CI-05-044 | Comunicar una obligación de compensación y su seguimiento | Compensación y conciliación de pagos | Evento |

## 3. Reglas comunes de integración

### 3.1. Identificación y contenido

Toda solicitud de actuación identifica:

- Operación de negocio y actuación solicitada.

- Servicio solicitante.

- Actor y facultades, cuando intervenga una persona.

- Objeto afectado y versión esperada, cuando exista.

- Causa y antecedentes.

- Fecha y hora de la instrucción.

- Referencias de autorización, controles y documentos aplicables.

Las actuaciones monetarias agregan importe, moneda, conceptos separados, cuentas o participantes y referencias de
efectos monetarios.

Una misma actuación con contenido idéntico recupera su resultado. Si cambia su contenido, se informa conflicto.

Una referencia de mensaje identifica una comunicación. No sustituye la referencia de la operación ni la del efecto
económico.

### 3.2. Autorización y destino

La autorización queda vinculada al contenido aceptado.

Cambiar importe, moneda, destino o finalidad material exige las comprobaciones y la conformidad correspondientes.

La resolución de un alias se conserva con el destino presentado y aceptado. Un cambio posterior del alias no modifica la
instrucción existente.

La identidad de una persona, su representación y sus facultades sobre una cuenta son comprobaciones distintas.

### 3.3. Autoridad monetaria

Fondos constituye, modifica, consume y libera reservas, y registra movimientos de cuentas.

El servicio que coordina una operación conserva las referencias de esos efectos. No registra un segundo débito por
recibir una confirmación o por completar una etapa posterior.

Cuando Pagos utiliza Transferencias o Tarjetas, reconoce la ejecución del instrumento utilizado. No reproduce su efecto
monetario.

El principal, las comisiones y los tributos permanecen separados. Una comisión por ejecución solo se genera al
confirmarse la ejecución que la origina.

El uso de descubierto debe estar expresamente permitido por el producto, la cuenta y el concepto. Su ausencia no se
interpreta como autorización.

### 3.4. Estados y evidencias

Se distinguen:

| Estado o hecho | Alcance |
| --- | --- |
| Recibida | Se conserva la instrucción. |
| Aceptada para ejecución | Cumple los requisitos de esa etapa. |
| Enviada | Existe constancia de salida al mecanismo. |
| Aceptada irrevocablemente | Existe un compromiso firme de ejecución. |
| Acreditada | Se confirmó el crédito en destino. |
| Aplicada | El acreedor confirmó la imputación. |
| Rechazada definitivamente | Existe evidencia de que no se ejecutó ni queda ejecución pendiente. |
| Incierta | Falta comprobar un resultado relevante. |
| Devuelta | Se confirmó una operación posterior de retorno. |
| Liquidada | Se cumplió la obligación financiera entre participantes. |

Estos hechos no forman una única secuencia obligatoria para todos los instrumentos. Cada operación conserva los que le
corresponden.

Un acuse técnico no demuestra acreditación, aplicación ni liquidación.

### 3.5. Incertidumbre y recuperación

Ante un resultado incierto:

- Se conserva la operación original.

- Se consulta su resultado.

- Se mantiene la cobertura necesaria.

- Se contrastan evidencias.

- Se completa o regulariza el efecto original.

Las consultas externas se realizan al detectar la incertidumbre, a los cinco minutos y a los treinta minutos. Si
persiste, se abre investigación y se revisa en cada jornada operativa.

Los plazos de consulta no autorizan liberaciones ni rechazos automáticos.

La ausencia de un registro en una consulta aislada tampoco demuestra que una instrucción enviada no pueda ejecutarse
después.

### 3.6. Concurrencia y cancelación

Una cancelación debe quedar ordenada frente al envío o la ejecución.

Si la cancelación se confirma antes del envío, la instrucción no puede enviarse posteriormente.

Si el envío ya ocurrió, se tramita la cancelación externa y se conserva la cobertura hasta comprobar su resultado.

La recepción de una petición de cancelación no equivale a su ejecución.

### 3.7. Eventos

Todo evento identifica:

- Hecho y objeto de negocio.

- Versión o secuencia del objeto.

- Operación y actuación causantes.

- Momento del hecho y de su registro.

- Referencias de efectos y evidencias.

- Estado confirmado y pendientes relacionados.

Los eventos repetidos no producen nuevos efectos.

Un evento de transferencia, pago o cheque referencia los movimientos de Fondos; no representa otro movimiento.

Un evento antiguo no reemplaza un estado posterior. Una discontinuidad o contradicción requiere consultar al proveedor.

## 4. Contratos de Transferencias

### CI-05-001. Ejecutar una transferencia interna

Proveedor: Transferencias. Consumidores: Gestión administrativa, Mandatos y recurrencias y Pagos.

Entrada: ordenante, cuenta de origen, destino aceptado, importe positivo, moneda, finalidad, autorización, condiciones
comerciales y desglose de cargos.

Condiciones: cuentas distintas; compatibilidad monetaria; facultades, restricciones y controles vigentes. Una conversión
requiere una operación de Cambio de moneda identificada.

Actuación: solicita a Fondos los movimientos internos relacionados mediante CI-04-018. Si existe una reserva vinculada,
su consumo forma parte de esa ejecución y no origina otro débito.

Salida: transferencia confirmada con referencias de débito, crédito y cargos; rechazo sin traslado parcial; o actuación
pendiente de comprobación.

Recuperación y aceptación: una respuesta perdida se resuelve consultando la ejecución original. Para una transferencia
de 30, sin cargos, desde una cuenta con 100 hacia otra con 20, los saldos resultantes son 70 y 50.

### CI-05-002. Enviar una transferencia externa

Proveedor: Transferencias. Consumidores: Gestión administrativa, Mandatos y recurrencias y Pagos.

Entrada: ordenante, cuenta de origen, beneficiario y destino aceptados, participante receptor, mecanismo, importe,
moneda, finalidad, autorización y cargos.

Condiciones: mecanismo habilitado y reserva suficiente antes del envío.

Actuación: constituye o utiliza una reserva identificada, envía la instrucción y sigue sus resultados. La reserva pasa a
incierta después del envío y a firme cuando existe aceptación irrevocable. Se consume al comprobar la acreditación.

Salida: referencias interna y externa, reserva, estado de envío, aceptación y acreditación; efectos confirmados y
pendientes.

Recuperación y aceptación: un acuse no habilita a informar acreditación. Un rechazo libera cobertura únicamente con
evidencia de ausencia de crédito y de ejecución pendiente. Una falla posterior de liquidación no revierte la
transferencia acreditada.

### CI-05-003. Recibir una transferencia externa

Proveedor: Transferencias. Consumidor: Compensación y conciliación de pagos, para instrucciones recibidas del mecanismo
simulado.

Entrada: participante y mecanismo de origen, referencia externa, ordenante, beneficiario, destino, importe, moneda,
finalidad y evidencia del mensaje.

Condiciones: origen reconocido; referencia coherente; destino y controles admisibles.

Actuación: acepta o rechaza antes de asumir el compromiso. Una vez aceptada irrevocablemente, conserva la obligación de
acreditar o resolver su devolución. Solicita a Fondos un crédito con referencia económica estable.

Salida: recepción, aceptación o rechazo; acreditación y referencia del movimiento; o compromiso pendiente de resolución.

Recuperación y aceptación: la misma referencia del participante y mecanismo no genera otro crédito. Una restricción
exclusiva de débitos no se interpreta automáticamente como prohibición de créditos.

### CI-05-004. Cancelar una instrucción de transferencia

Proveedor: Transferencias. Consumidores: Gestión administrativa, Mandatos y recurrencias y Pagos.

Entrada: transferencia, motivo, solicitante autorizado y actuación de cancelación.

Actuación: comprueba el avance y coordina la cancelación con el envío. Si ya fue enviada, solicita confirmación al
mecanismo externo.

Salida: cancelada sin ejecución; cancelación pendiente; cancelación no procedente por acreditación; o resultado
incierto.

Recuperación y aceptación: solo se libera la cobertura cuando se comprueba que no existe ejecución ni compromiso
pendiente. Una transferencia acreditada continúa por CI-05-005.

### CI-05-005. Tramitar una devolución de transferencia

Proveedor: Transferencias. Consumidores: Gestión administrativa, Pagos y el mecanismo externo mediante Compensación y
conciliación de pagos.

Entrada: transferencia original, importe a devolver, moneda, motivo, destino de retorno, autorización o fundamento
suficiente y devoluciones anteriores relacionadas.

Condiciones: principal pendiente de devolución suficiente. La petición del ordenante original no concede facultad para
debitar al beneficiario.

Actuación: registra una devolución vinculada y coordina los efectos internos o externos necesarios. Los cargos y sus
correcciones reciben tratamiento separado.

Salida: devolución confirmada, parcial, pendiente, rechazada o incierta; importe acumulado y principal restante.

Recuperación y aceptación: una devolución repetida no vuelve a pagar. Sobre un principal de 100 con 40 ya devueltos, el
máximo restante es 60.

### CI-05-006. Consultar una transferencia y sus actuaciones

Proveedor: Transferencias. Consumidores: servicios que la originaron, Compensación y conciliación de pagos, Reclamos,
Documentos y comprobantes y Extractos.

Entrada: referencia interna o referencia externa completa; alcance autorizado de consulta.

Salida: contenido aceptado, destino fijado, autorizaciones, reservas, movimientos, intercambios, estados y evidencias;
cancelaciones y devoluciones con sus remanentes.

Reglas: diferencia hechos confirmados de pendientes. La consulta no envía, reintenta, devuelve ni libera fondos.

## 5. Contratos de Pagos

### CI-05-007. Administrar un convenio de recaudación

Proveedor: Pagos. Consumidor: Gestión administrativa.

Entrada: participante, contrato, servicio recaudado, referencias admitidas, monedas, modalidades, vigencia, reglas de
cargos, procedimiento de preparación y aplicación, consultas, recuperación y aprobación separada.

Actuación: crea, modifica, habilita o retira una versión del convenio.

Salida: convenio y versión; modalidades habilitadas; vigencia y condiciones incumplidas.

Reglas y aceptación: el pago parcial, anticipado o posterior al vencimiento requiere habilitación expresa. No se
habilita un convenio sin capacidad de resolver débitos confirmados con aplicación pendiente. La baja impide nuevos pagos
y conserva la resolución de los existentes.

### CI-05-008. Consultar un convenio de recaudación

Proveedor: Pagos. Consumidores: Gestión administrativa, Mandatos y recurrencias y Compensación y conciliación de pagos.

Entrada: convenio o participante y servicio; versión o momento solicitado.

Salida: estado, contrato, monedas, referencias, modalidades, cargos, mecanismos de ejecución, preparación, aplicación y
recuperación.

Reglas: una modalidad ausente se informa como no habilitada. Las operaciones anteriores conservan la versión utilizada.

### CI-05-009. Identificar una obligación de pago

Proveedor: Pagos. Consumidores: Gestión administrativa y Mandatos y recurrencias.

Entrada: convenio, acreedor, referencia de obligación y datos mínimos de identificación. Si se actúa por otro obligado,
se informa esa relación sin conceder acceso general a sus datos.

Salida: obligación identificada; fuente y momento de consulta; obligado, importe, moneda, vencimiento, modalidades y
cargos; o referencia inexistente, ambigua o no comprobada.

Reglas: una referencia manual se distingue de una confirmada por el acreedor. La consulta no reserva fondos ni cancela
deuda. Los módulos de préstamos y tarjetas siguen siendo autoridad sobre sus obligaciones.

Aceptación: si el importe pasa de 100 a 110 antes de autorizar, debe presentarse el nuevo importe para su aceptación.

### CI-05-010. Ejecutar y aplicar un pago

Proveedor: Pagos. Consumidores: Gestión administrativa y Mandatos y recurrencias.

Entrada: obligación y versión o confirmación vigente, convenio, importe aceptado, moneda, instrumento, pagador,
autorización, ocurrencia cuando exista y desglose de conceptos.

Condiciones: modalidad permitida y aceptación preliminar del acreedor.

Actuación:

- Obtiene la aceptación preliminar.

- Coordina la reserva cuando el instrumento utiliza fondos de cuenta.

- Obtiene una aplicación preparada.

- Ejecuta el instrumento una sola vez.

- Solicita la aplicación definitiva.

- Conserva referencias de ejecución e imputación.

Si el instrumento es una transferencia, reutiliza su ejecución y cobertura. Si es tarjeta, la autorización y el cargo
pertenecen a Tarjetas.

Salida: ejecución y aplicación diferenciadas; referencias del instrumento, reserva, movimiento e imputación; saldo
informado por el acreedor y pendientes.

Recuperación y aceptación: un débito confirmado sin aplicación exige completar la aplicación o devolver, sin cobrar
nuevamente. Una preparación cancelada antes de ejecutar no genera débito y permite liberar la cobertura comprobada.

### CI-05-011. Anular, devolver o corregir un pago

Proveedor: Pagos. Consumidores: Gestión administrativa y servicios acreedores responsables de la obligación.

Entrada: pago original, actuación solicitada, motivo, importe cuando corresponda, evidencia, facultades y conformidad o
decisión del acreedor.

Actuación: distingue cancelación previa, rectificación sin dinero, devolución y corrección monetaria. Coordina sus
consecuencias con el acreedor y el instrumento utilizado.

Salida: actuación aplicada o pendiente; efectos monetarios, rectificación de referencia, importe devuelto y
consecuencias sobre la obligación.

Reglas: no elimina el pago aplicado. Una rectificación de referencia no cobra otra vez. Una devolución confirmada
conserva seguimiento hasta acreditarse. La reapertura de deuda pertenece al acreedor.

### CI-05-012. Consultar un pago y sus actuaciones

Proveedor: Pagos. Consumidores: servicios originantes, módulos acreedores, Compensación y conciliación de pagos,
Reclamos, Documentos y comprobantes y Extractos.

Entrada: referencia de pago, actuación o reversión y alcance autorizado.

Salida: obligación, convenio y condiciones utilizadas; preparación, instrumento, ejecución, aplicación, devoluciones,
restituciones provisionales y resolución; referencias y pendientes.

Reglas: diferencia deuda informada por el acreedor de dinero ejecutado. Un pago debitado con aplicación desconocida no
se informa como aplicado ni rechazado.

## 6. Contratos de Mandatos y recurrencias

### CI-05-013. Constituir o modificar un mandato

Proveedor: Mandatos y recurrencias. Consumidor: Gestión administrativa.

Entrada: otorgante, beneficiario, cuenta, finalidad, moneda, importe fijo o máximo variable por ocurrencia, frecuencia,
vigencia, alcance y aceptación.

Condiciones: facultades suficientes y cuenta compatible.

Actuación: registra el mandato o una nueva versión. Comprueba aceptación de las ampliaciones de importe, frecuencia o
alcance.

Salida: mandato, versión, vigencia y estado habilitado o pendiente.

Reglas y aceptación: un cobro variable sin máximo no queda habilitado. Un cambio de beneficiario requiere un nuevo
mandato. No se reservan fondos por toda la vigencia.

### CI-05-014. Suspender, reanudar o revocar un mandato

Proveedor: Mandatos y recurrencias. Consumidor: Gestión administrativa.

Entrada: mandato, versión, actuación, motivo, actor y momento de la instrucción.

Actuación: impide nuevas ocurrencias alcanzadas e identifica instrucciones anteriores que requieren cancelación.
Registra también el vencimiento conforme a la vigencia establecida.

Salida: nuevo estado y momento efectivo; ocurrencias impedidas y actuaciones anteriores pendientes.

Reglas: reanudar requiere instrucción del otorgante y vigencia. Un mandato revocado no se reactiva: los nuevos cobros
requieren otro mandato. La revocación no devuelve pagos ni extingue deudas.

### CI-05-015. Consultar y comprobar un mandato

Proveedor: Mandatos y recurrencias. Consumidores: Pagos, Transferencias y Gestión administrativa.

Entrada: mandato; para comprobar una actuación, beneficiario, cuenta, finalidad, importe, moneda, ocurrencia y momento
previstos.

Salida: versión y estado; alcance; resultado de correspondencia de la actuación con la autorización; motivos de
incumplimiento.

Reglas: la comprobación no reserva ni ejecuta. La decisión se vincula a una ocurrencia y a su contenido. Un máximo de
100 impide un cobro de 120 y también su fraccionamiento para eludirlo.

La admisión de la ocurrencia y los cambios del mandato quedan ordenados en el servicio proveedor.

### CI-05-016. Administrar una programación

Proveedor: Mandatos y recurrencias. Consumidor: Gestión administrativa.

Entrada: instrucción autorizada, mandato si corresponde, anclaje, frecuencia, calendario, ajuste, horario, fecha de
inicio y finalización, condiciones del importe y destino.

Actuación: crea, modifica, suspende o termina una programación, preservando sus versiones y ocurrencias anteriores.

Salida: programación, versión, próximas fechas y estado.

Reglas y aceptación: el horario ordinario es 09:00 de la zona institucional; una transferencia única puede señalar otro.
El ajuste de febrero no desplaza el anclaje de marzo. No se agregan importes omitidos a futuras ocurrencias sin
autorización.

### CI-05-017. Originar una ocurrencia

Proveedor: Mandatos y recurrencias. Consumidores: Gestión administrativa y Compensación y conciliación de pagos para
presentaciones de acreedores. El mismo comportamiento rige la ejecución temporal de las programaciones del proveedor.

Entrada: programación y fecha prevista, o presentación del acreedor con mandato y referencia de cobro; importe, moneda y
datos de ejecución.

Condiciones: ocurrencia identificada, mandato y programación admisibles, importe autorizado y ausencia de una ejecución
previa de esa ocurrencia.

Actuación: registra la ocurrencia y origina una única instrucción en Pagos o Transferencias.

Salida: ocurrencia, instrucción originada y resultado confirmado, pendiente, omitido o fallido.

Recuperación y aceptación:

- Dentro de la fecha prevista, puede completar la ejecución una vez si conserva requisitos.

- En una fecha posterior, un pago o transferencia programado requiere confirmación del ordenante.

- Una presentación omitida de acreedor requiere otra ocurrencia válida presentada por este.

- La insuficiencia de fondos no genera reintentos automáticos.

- Consultar una ejecución incierta no crea otra ocurrencia.

Un intento posterior autorizado conserva referencia propia y vínculo con el anterior, sin eludir máximos ni duplicar un
cobro ya ejecutado.

### CI-05-018. Consultar una programación o una ocurrencia

Proveedor: Mandatos y recurrencias. Consumidores: Gestión administrativa, Pagos, Transferencias, Reclamos y
Comunicaciones.

Entrada: programación, ocurrencia o presentación del acreedor.

Salida: calendario, anclaje, fechas originales y ajustadas; mandato y versión; importe, instrucciones originadas y
resultados; omisiones, confirmaciones e intentos posteriores.

Reglas: una ocurrencia omitida no se presenta como ejecutada. Una presentación de archivo no demuestra el cobro de todas
sus ocurrencias.

### CI-05-019. Solicitar la reversión de un débito autorizado

Proveedor: Mandatos y recurrencias. Consumidores: Gestión administrativa y Reclamos.

Entrada: solicitante, facultades, débito original, motivo y fecha de recepción.

Actuación: identifica mandato y ocurrencia, comprueba correspondencia y plazo, consulta devoluciones anteriores y
solicita a Pagos el tratamiento económico mediante CI-05-020.

Salida: expediente de reversión, débito vinculado, comprobaciones, plazo aplicable y referencia de tratamiento
económico; o derivación a reclamo fuera del plazo ordinario.

Reglas: se reciben solicitudes dentro de treinta días corridos desde el débito, excluyendo el día inicial y trasladando
el vencimiento inhábil al siguiente hábil. La solicitud no revoca el mandato.

### CI-05-020. Tramitar la restitución y resolución de una reversión

Proveedor: Pagos. Consumidores: Mandatos y recurrencias y Gestión administrativa para actuaciones de resolución
autorizadas.

Entrada: expediente, débito y pago originales, identidad y facultades comprobadas, recepción, importe reclamado,
devoluciones previas y evidencias de resolución cuando existan.

Actuación: administra restitución provisional, devolución definitiva, regularización y eventual recupero, con
referencias que impidan acreditar dos veces el mismo importe.

Salida: importe provisional, definitivo y pendiente; movimientos relacionados; resolución y consecuencias comunicadas al
acreedor.

Reglas y aceptación:

- Comprobados identidad, facultades y correspondencia, la restitución provisional se realiza como máximo al finalizar el
  tercer día hábil posterior a la recepción.

- Una devolución total anterior impide otra acreditación por ese importe.

- La devolución posterior del acreedor regulariza el provisional.

- Una objeción del acreedor no retira por sí sola el crédito provisional.

- El recupero de un provisional improcedente requiere resolución y no concede descubierto.

- Préstamos o Cuenta de tarjeta determinan la deuda resultante de sus propios productos.

## 7. Contratos de Cheques y ECHEQ

### CI-05-021. Habilitar y entregar una chequera

Proveedor: Cheques y ECHEQ. Consumidor: Gestión administrativa.

Entrada: cuenta corriente, moneda, solicitante y facultades, numeración solicitada y evidencia de entrega cuando
corresponda.

Condiciones: cuenta habilitada para cheques y numeración no asignada de forma incompatible.

Salida: chequera, rango asignado, estado de habilitación y entrega.

Reglas y aceptación: entregar una chequera no emite todos sus instrumentos, no reserva fondos y no genera débitos.

### CI-05-022. Registrar la emisión de un cheque o ECHEQ

Proveedor: Cheques y ECHEQ. Consumidores: Gestión administrativa y Compensación y conciliación de pagos para registros
externos del mecanismo.

Entrada: identidad del instrumento, soporte, cuenta libradora, librador, beneficiario identificado, importe, moneda,
clase, fechas, circulación y evidencia de emisión.

En papel, los datos se incorporan cuando son conocidos; la presentación requiere comprobar el contenido completo.

Condiciones: cuenta corriente habilitada y misma moneda. Se admiten instrumentos nominativos, comunes o diferidos, a la
orden o no a la orden, de pago total.

Salida: instrumento y contenido registrado; estado y comprobaciones pendientes, si corresponde.

Reglas y aceptación: un común no tiene emisión futura. En un diferido, el pago es posterior a la emisión y no excede 365
días corridos de separación. No se ofrecen portador, endoso en blanco, certificación ni pago parcial. Emitir no reserva
fondos ni otorga descubierto.

### CI-05-023. Administrar aceptación, transmisión y aval

Proveedor: Cheques y ECHEQ. Consumidores: Gestión administrativa y Compensación y conciliación de pagos.

Entrada: instrumento y versión, actuación, intervinientes, facultades, evidencia y aceptación del receptor cuando
corresponda.

Actuación: registra aceptación del beneficiario; propone, acepta, rechaza o cancela una transmisión; reconoce una cesión
documentada; o registra un aval total de tercero.

Salida: actuación, tenedor legitimado, cadena de circulación, compromiso pendiente y avales registrados.

Reglas y aceptación:

- La transmisión es nominativa y total.

- Un instrumento no a la orden no se transmite por endoso.

- La cesión requiere documentación, aceptación y reconocimiento.

- Una transmisión pendiente impide depósito, descuento u otra transmisión incompatible.

- Solo una de dos transmisiones concurrentes puede confirmarse.

- El aval no cambia al tenedor, no reserva dinero y no representa pago.

- No se otorgan avales propios del banco por este contrato.

### CI-05-024. Comprometer un instrumento para descuento

Proveedor: Cheques y ECHEQ. Consumidor: Préstamos y líneas.

Entrada: instrumento, versión, tenedor, solicitud de financiación, facultades y referencia de compromiso.

Condiciones: legitimación comprobada e instrumento disponible, sin depósito, transmisión, presentación o compromiso
incompatible.

Actuación: compromete el instrumento para la financiación identificada.

Salida: compromiso confirmado o rechazado; estado del instrumento y evidencia de legitimación.

Recuperación y aceptación: el compromiso impide depósito y transmisión por otra vía. No acredita un anticipo ni confirma
por sí solo la financiación. La incertidumbre sobre su formalización no permite liberarlo por mero vencimiento temporal.

### CI-05-025. Confirmar o liberar el compromiso para descuento

Proveedor: Cheques y ECHEQ. Consumidor: Préstamos y líneas.

Entrada: compromiso original, financiación, actuación y evidencia de formalización o de no ejecución.

Actuación: confirma la cesión al banco y el vínculo con la financiación, o libera el compromiso si se comprueba que no
subsiste una financiación ni una actuación incompatible.

Salida: instrumento cedido y destinado al cobro de la financiación; compromiso liberado; o resolución pendiente.

Reglas y aceptación: el cobro posterior se destina a Préstamos y líneas. No produce otro ingreso libre al cliente. Una
cesión confirmada no se deshace mediante una liberación simple.

### CI-05-026. Depositar y presentar un instrumento al cobro

Proveedor: Cheques y ECHEQ. Consumidores: Gestión administrativa, Préstamos y líneas y Compensación y conciliación de
pagos.

Entrada: instrumento, tenedor o financiación beneficiaria, destino del cobro, evidencia de recepción, fecha y hora, y
presentación anterior cuando se solicita un nuevo intento admisible.

Condiciones: legitimación, integridad, oportunidad y ausencia de una presentación incierta o un pago confirmado.

Actuación: valida la recepción, compromete el instrumento y origina la presentación en el ciclo correspondiente.

Salida: recepción y validación diferenciadas, fecha de oportunidad, presentación, ciclo, destino y estado.

Reglas y aceptación:

- El común admite presentación desde la emisión hasta treinta días corridos después.

- El diferido admite presentación desde la fecha de pago hasta treinta días corridos después.

- El día de referencia se excluye del cómputo; ambos extremos de presentación son admisibles y el final inhábil se
  extiende al siguiente hábil.

- Recibir anticipadamente un diferido permite conservarlo, sin presentarlo antes de la fecha de pago.

- El papel se valida en días hábiles hasta las 17:00; fuera de horario queda para validación posterior.

- El ECHEQ admite recepción continua; los validados antes de las 18:00 integran el ciclo hábil correspondiente.

- La demora interna no convierte en vencida una recepción validada oportunamente.

- El depósito no concede disponibilidad ni constituye un anticipo.

### CI-05-027. Resolver el pago o rechazo de una presentación

Proveedor: Cheques y ECHEQ. Consumidores: Compensación y conciliación de pagos y Gestión administrativa para
resoluciones respaldadas por evidencia.

Entrada: instrumento, presentación, resultado o instrucción de resolución, importe íntegro, moneda, participante y
evidencias.

Actuación: comprueba legitimación, medidas y resultado monetario. Coordina con Fondos o con el mecanismo externo y
comunica el resultado al tenedor o a Préstamos y líneas.

Salida: pago confirmado con referencias económicas; rechazo con causal y fecha; o presentación incierta.

Reglas y aceptación:

- Un cheque de 100 con capacidad de 80 no se paga parcialmente.

- La incertidumbre monetaria no se transforma en rechazo.

- Un pago confirmado cierra la posibilidad de otro cobro.

- Un rechazo por insuficiencia puede admitir otra presentación dentro del plazo, con nueva instrucción y una vez
  resuelto el intento anterior.

- No hay nueva presentación automática.

- En instrumentos descontados, pago y rechazo se informan a Préstamos y líneas; no se duplica el crédito ni se extingue
  la financiación por un rechazo.

### CI-05-028. Administrar medidas y devoluciones de instrumentos

Proveedor: Cheques y ECHEQ. Consumidores: Restricciones y Gestión administrativa.

Entrada: instrumento, medida o actuación, causa, autoridad, alcance, vigencia, evidencia y conformidades exigibles.

Actuación: registra y aplica medidas; tramita cancelación por acuerdo o devolución del soporte; identifica
presentaciones y cobros afectados.

Salida: medida efectiva o pendiente; cancelación confirmada o impedida; devolución con destinatario; actuaciones
monetarias relacionadas.

Reglas y aceptación: una denuncia no demuestra por sí sola invalidez. La cancelación por acuerdo requiere librador y
tenedor actual y no procede con presentación incierta. Devolver el soporte no significa pagar ni devolver dinero. Un
pago confirmado conserva su historia y cualquier retorno monetario requiere una actuación vinculada.

### CI-05-029. Consultar un instrumento y sus actuaciones

Proveedor: Cheques y ECHEQ. Consumidores: Gestión administrativa, Préstamos y líneas, Garantías, Restricciones,
Compensación y conciliación de pagos, Reclamos y Documentos y comprobantes.

Entrada: instrumento o actuación, momento de consulta y alcance autorizado.

Salida: contenido, soporte, clase, fechas, plazo, tenedor, cadena, avales, medidas, compromisos, descuento, depósitos,
presentaciones, pago, rechazos y devoluciones.

Reglas: distingue estado del instrumento, resultado monetario y financiación relacionada. Consultar no compromete el
cheque para una operación posterior.

## 8. Contratos de Compensación y conciliación de pagos

### CI-05-030. Administrar participantes y mecanismos de intercambio

Proveedor: Compensación y conciliación de pagos. Consumidor: Gestión administrativa.

Entrada: participante, identidad de Personas, acuerdo de Contratos, mecanismos, monedas, referencias, calendarios,
capacidades, consultas, recuperación, reglas de compensación y liquidación, vigencia y aprobación separada.

Actuación: registra, modifica, habilita o suspende una versión.

Salida: participante y mecanismo, versión, capacidades y estado.

Reglas y aceptación: no se habilita un mecanismo que impida consultar y resolver ejecuciones inciertas. Suspender nuevos
envíos conserva consultas y resolución de pendientes. Un convenio de recaudación permanece bajo la autoridad de Pagos.

### CI-05-031. Consultar participantes y mecanismos

Proveedor: Compensación y conciliación de pagos. Consumidores: Transferencias, Pagos, Mandatos y recurrencias, Cheques y
ECHEQ y Liquidaciones de tesorería.

Entrada: participante, mecanismo, operación, moneda y momento; versión histórica cuando corresponda.

Salida: habilitación, capacidades, calendario, cortes, identificación, semántica de confirmaciones, recuperación y
liquidación.

Reglas: una operación anterior conserva la versión con la que se originó. Una capacidad no definida no se presume
disponible.

### CI-05-032. Intercambiar una instrucción o una consulta externa

Proveedor: Compensación y conciliación de pagos. Consumidores: Transferencias, Pagos, Mandatos y recurrencias y Cheques
y ECHEQ.

Entrada: operación y actuación de negocio, participante, mecanismo y versión, tipo de intercambio, contenido, referencia
externa si existe y evidencias o compromisos exigidos para el envío.

Actuación: asigna o recupera la referencia externa, registra la intención y constancias de envío, y comunica los
resultados recibidos.

Salida: referencia de intercambio, referencia externa, constancia de envío o de no envío, acuses y resultados
diferenciados.

Reglas y aceptación: el proveedor no mueve dinero ni decide la aplicación de una deuda. Consultar una operación utiliza
su referencia original. Un reenvío técnico conserva la identidad económica y solo procede conforme a las garantías del
mecanismo.

### CI-05-033. Incorporar un mensaje o resultado externo

Proveedor: Compensación y conciliación de pagos. Consumidores: mecanismos externos simulados y Gestión administrativa
para evidencias incorporadas mediante una actuación autorizada.

Entrada: participante, mecanismo, mensaje, referencias, tipo de hecho, contenido, momento y evidencia de origen.

Actuación: verifica procedencia, duplicación y correspondencia; conserva el mensaje y lo entrega al servicio de negocio
responsable.

Salida: mensaje incorporado, duplicado reconocido, referencia no vinculada o contradicción en investigación;
destinatario y estado de procesamiento.

Reglas y aceptación: una confirmación tardía se integra a la operación original. Un mensaje sin referencia conocida no
se aplica a otra operación. Los resultados incompatibles se conservan y abren investigación.

### CI-05-034. Consultar un intercambio y sus evidencias

Proveedor: Compensación y conciliación de pagos. Consumidores: servicios de negocio originantes, Liquidaciones de
tesorería, Reclamos y Gestión administrativa.

Entrada: intercambio o referencia completa de participante y mecanismo.

Salida: contenido enviado y recibido, versión del mecanismo, constancias, acuses, hechos comunicados, consultas,
contradicciones y pendientes.

Reglas: diferencia ausencia de constancia de envío de evidencia concluyente de no ejecución. La consulta no vuelve a
enviar una instrucción.

### CI-05-035. Conciliar registros y administrar diferencias

Proveedor: Compensación y conciliación de pagos. Consumidores: Transferencias, Pagos, Cheques y ECHEQ, Liquidaciones de
tesorería y Gestión administrativa.

Entrada: ámbito, ciclo y cortes comparables; referencias de registros internos y externos; para resolver una diferencia,
evidencia, decisión y aprobación separada cuando haya ajuste manual.

Actuación: compara referencias, importes, monedas y resultados; clasifica desfases, faltantes, duplicaciones y
contradicciones; solicita regularizaciones a los responsables.

Salida: conciliación, diferencias, evidencia, responsable, actuaciones de resolución y consecuencias pendientes.

Reglas y aceptación: conciliar no autoriza un débito. Fondos ejecuta una corrección monetaria solicitada válidamente por
el responsable de negocio. No se alteran registros para forzar coincidencia. Si el externo acreditó y el interno sigue
pendiente, se completa el efecto original sin reenviar la transferencia.

### CI-05-036. Determinar y consultar obligaciones de compensación

Proveedor: Compensación y conciliación de pagos. Consumidores: Liquidaciones de tesorería, Posiciones y liquidez,
Registración contable y Gestión administrativa.

Entrada para determinar: ciclo, mecanismo, participante, moneda, versión del acuerdo y referencias de hechos confirmados
y ajustes.

Entrada para consultar: obligación, ciclo o composición solicitada.

Actuación: selecciona los hechos que generan obligación, elimina duplicaciones y determina importes brutos o netos según
el acuerdo.

Salida: obligación identificada, pagar o cobrar, contraparte, moneda, importe, vencimiento, composición y ajustes
relacionados.

Reglas y aceptación:

- El corte diario es 18:00 en America/Argentina/Buenos_Aires.

- Solo se netea con acuerdo expreso, misma contraparte y misma moneda.

- Las reservas no se incluyen como pagos.

- Las transferencias aportan acreditaciones y devoluciones confirmadas.

- Los pagos y débitos aportan ejecuciones que generan obligación entre participantes.

- Los cheques aportan pagos confirmados por la entidad girada.

- Los hechos subyacentes se incluyen una vez, aunque aparezcan en varias comunicaciones.

- Los ajustes posteriores integran otro ciclo y referencian el original.

Pagar 100 y cobrar 70 a la misma contraparte y moneda, con neteo acordado, determina un pago neto de 30. Repetir la
determinación recupera la misma obligación.

### CI-05-037. Incorporar el resultado de una liquidación externa

Proveedor: Compensación y conciliación de pagos. Consumidor: Liquidaciones de tesorería.

Entrada: obligación, actuación de liquidación, resultado, importe, moneda, fechas y evidencia de ejecución o
incertidumbre.

Actuación: relaciona la liquidación con su obligación y actualiza el seguimiento. Si el mecanismo admite cumplimiento
parcial, conserva el importe efectivamente cumplido y el remanente.

Salida: obligación liquidada, parcialmente cumplida, pendiente o incierta; referencias y saldo pendiente.

Reglas y aceptación: el mismo cumplimiento no reduce dos veces la obligación. Una falla de liquidación no borra
acreditaciones ni pagos confirmados de clientes. El proveedor registra el resultado; no vuelve a ejecutar el pago de
tesorería.

## 9. Contratos de eventos

### CI-05-038. Comunicar un cambio de transferencia

Proveedor: Transferencias. Consumidores: Pagos, Mandatos y recurrencias, Compensación y conciliación de pagos,
Registración contable, Documentos y comprobantes, Comunicaciones y Extractos.

Contenido: transferencia, actuación, versión, hecho confirmado, importe y moneda, participantes, referencias de reservas
y movimientos, devoluciones, momento y pendientes.

Reglas: distingue envío, aceptación, acreditación, cancelación y devolución. Solo los hechos definidos por el mecanismo
originan obligaciones de compensación.

Aceptación: recibir nuevamente el evento de acreditación no vuelve a debitar, acreditar ni compensar.

### CI-05-039. Comunicar un cambio de pago o reversión

Proveedor: Pagos. Consumidores: Mandatos y recurrencias, módulos acreedores, Compensación y conciliación de pagos,
Registración contable, Reclamos, Documentos y comprobantes, Comunicaciones y Extractos.

Contenido: pago, obligación, actuación, instrumento, ejecución, aplicación, importe, moneda, devoluciones, provisional y
resolución; referencias económicas y de imputación.

Reglas: ejecución y aplicación se comunican como hechos distintos. El acreedor decide las consecuencias sobre su deuda.

Aceptación: una devolución definitiva posterior a un provisional no se interpreta como otra restitución íntegra al
cliente.

### CI-05-040. Comunicar un cambio de mandato o programación

Proveedor: Mandatos y recurrencias. Consumidores: Pagos, Transferencias, Gestión administrativa, Documentos y
comprobantes y Comunicaciones.

Contenido: mandato o programación, versión, estado, alcance, vigencia, momento efectivo y actuaciones afectadas.

Reglas: no contiene una autorización abierta para cualquier cobro. Los consumidores comprueban la ocurrencia concreta.
Una revocación no informa devolución de pagos anteriores.

Aceptación: un evento antiguo de habilitación no reactiva un mandato posteriormente revocado.

### CI-05-041. Comunicar el resultado de una ocurrencia

Proveedor: Mandatos y recurrencias. Consumidores: Gestión administrativa, Pagos, Transferencias, Reclamos y
Comunicaciones.

Contenido: ocurrencia, mandato, programación o presentación del acreedor, fecha prevista, importe, instrucción
originada, resultado y causa; vínculos con intentos posteriores.

Reglas: diferencia ejecución, fallo, omisión y resultado incierto. No representa por sí mismo un movimiento monetario
adicional.

Aceptación: una ocurrencia fallida por insuficiencia no origina un nuevo cobro al consumirse el evento.

### CI-05-042. Comunicar un cambio de instrumento

Proveedor: Cheques y ECHEQ. Consumidores: Préstamos y líneas, Garantías, Restricciones, Compensación y conciliación de
pagos, Registración contable, Documentos y comprobantes y Comunicaciones.

Contenido: instrumento, versión, actuación, tenedor y compromiso cuando sean pertinentes; emisión, transmisión, aval,
descuento, presentación, pago, rechazo, medida o devolución; referencias y evidencias.

Reglas: un pago de instrumento descontado identifica la financiación destinataria. Un aval o transmisión no se presenta
como pago.

Aceptación: el rechazo de un cheque descontado no crea por sí solo otra deuda de principal ni elimina la existente.

### CI-05-043. Comunicar un resultado de intercambio o conciliación

Proveedor: Compensación y conciliación de pagos. Consumidores: servicio de negocio responsable, Liquidaciones de
tesorería, Gestión administrativa y Reclamos.

Contenido: intercambio o diferencia, operación vinculada, participante, mecanismo, resultado, evidencia, clasificación y
actuaciones pendientes.

Reglas: un acuse sigue siendo recepción. Una diferencia no constituye autorización monetaria. El cierre de una
investigación identifica el tratamiento de sus consecuencias.

Aceptación: un resultado contradictorio genera investigación y no reemplaza silenciosamente un pago confirmado.

### CI-05-044. Comunicar una obligación de compensación y su seguimiento

Proveedor: Compensación y conciliación de pagos. Consumidores: Liquidaciones de tesorería, Posiciones y liquidez,
Registración contable y Gestión administrativa.

Contenido: obligación, ciclo, versión, contraparte, moneda, importe, dirección, vencimiento, composición referenciada,
ajustes y cumplimiento registrado.

Reglas: distingue nacimiento de obligación, ajuste y liquidación. La repetición del evento no crea otra orden de
liquidación.

Aceptación: un ajuste de 5 posterior a un ciclo cerrado conserva su propia referencia y el vínculo con la obligación
original.

## 10. Integraciones consumidas

### 10.1. Institución y oferta comercial

| Contrato | Uso |
| --- | --- |
| CI-01-004 | Comprobar facultades institucionales de las actuaciones. |
| CI-01-005 | Consultar definición y capacidades del producto. |
| CI-01-011 | Determinar condiciones comerciales aplicables. |
| CI-01-012 | Recuperar la oferta o versión utilizada. |
| CI-01-014 | Consultar calendarios y convenciones. |
| CI-01-015 | Determinar fechas, ajustes y series. |

Las condiciones utilizadas quedan identificadas. Un cambio comercial posterior no altera una instrucción ya aceptada.

### 10.2. Personas y relaciones bancarias

| Contrato | Uso |
| --- | --- |
| CI-02-002 | Consultar identidad de intervinientes. |
| CI-02-015 | Consultar la relación comercial. |
| CI-02-020 | Consultar una representación. |
| CI-02-021 | Comprobar representación para la actuación concreta. |

Pagar por otra persona no concede acceso general a su información.

### 10.3. Contratación

| Contrato | Uso |
| --- | --- |
| CI-03-022 | Consultar contratos, condiciones y situación aplicables a productos, convenios y acuerdos. |

La habilitación operativa de un mecanismo no sustituye la existencia y vigencia del acuerdo correspondiente.

### 10.4. Cuentas y fondos

| Contrato | Uso |
| --- | --- |
| CI-04-002 | Consultar cuenta, condiciones y capacidades. |
| CI-04-010 | Resolver identificadores bancarios. |
| CI-04-012 | Consultar posición y movimientos. |
| CI-04-013 | Constituir reservas. |
| CI-04-014 | Consumir reservas. |
| CI-04-015 | Liberar reservas con evidencia suficiente. |
| CI-04-016 | Modificar importe o firmeza de una reserva. |
| CI-04-017 | Registrar movimientos monetarios individuales. |
| CI-04-018 | Ejecutar movimientos internos relacionados. |
| CI-04-021 | Corregir movimientos mediante actuaciones vinculadas. |
| CI-04-022 | Recuperar el resultado de una actuación monetaria. |
| CI-04-023 | Consultar una reserva. |
| CI-04-040 | Comprobar facultades y admisión de una operación de cuenta. |
| CI-04-041 | Revalidar una comprobación pendiente de Transferencias. |

Si CI-04-040 devuelve PENDING, Transferencias solicita a Cuentas la revalidación de la misma instrucción mediante
CI-04-041. Envía la cuenta y el checkId de la comprobación original y puede adjuntar referencias a conformidades
conjuntas posteriores sobre esa instrucción.

Con ADMITTED, Transferencias continúa con los controles restantes. Con PREVENTED, la instrucción no avanza.
Si Cuentas devuelve HTTP 503, Transferencias puede reintentar con la misma cuenta y el mismo checkId. Esa respuesta
solo indica que Cuentas no pudo devolver una decisión definitiva en esa solicitud.

Cada ejecución conserva una ruta monetaria única. Consumir una reserva y registrar un débito independiente no son dos
formas acumulables de ejecutar el mismo principal.

### 10.5. Otros módulos

| Proveedor | Integración requerida |
| --- | --- |
| Préstamos y líneas | Formalización del descuento, destino del cobro y consecuencias del rechazo; imputación o reapertura de deuda propia. |
| Cuenta de tarjeta | Aplicación y reversión de pagos de tarjeta; determinación de deuda resultante. |
| Servicios de Tarjetas responsables del instrumento | Autorización y cargo cuando un pago se ejecuta con tarjeta. |
| Liquidaciones de tesorería | Ejecución y recuperación de liquidaciones entre participantes. |
| Posiciones y liquidez | Incorporación de obligaciones externas confirmadas. |
| Cambio de moneda | Conversión expresamente coordinada entre monedas. |
| Servicios de Riesgo y cumplimiento | Controles, restricciones y determinación tributaria de su competencia. |
| Registración contable | Registración de hechos económicos con referencia al efecto original. |
| Gestión administrativa | Actuaciones humanas y aprobaciones separadas. |
| Reclamos | Investigaciones y solicitudes que requieren tratamiento de reclamo. |
| Documentos y comprobantes | Evidencias y comprobantes de actuaciones. |
| Comunicaciones | Avisos y resultados. |
| Extractos | Exposición de movimientos y operaciones confirmadas. |

Los identificadores de los contratos proporcionados por esos módulos se establecen en sus respectivos documentos.

## 11. Correspondencia con las operaciones funcionales

| Operación | Contratos principales |
| --- | --- |
| OF-05-01. Ejecutar una transferencia interna | CI-05-001, 006, 038 |
| OF-05-02. Enviar una transferencia externa | CI-05-002, 006, 032, 034, 038 |
| OF-05-03. Recibir una transferencia externa | CI-05-003, 006, 033, 038 |
| OF-05-04. Cancelar o devolver una transferencia | CI-05-004, 005, 006, 038 |
| OF-05-05. Administrar convenios de recaudación | CI-05-007, 008 |
| OF-05-06. Identificar una obligación de pago | CI-05-008, 009 |
| OF-05-07. Ejecutar y aplicar un pago | CI-05-010, 012, 039 |
| OF-05-08. Anular, devolver o corregir un pago | CI-05-011, 012, 039 |
| OF-05-09. Constituir y modificar un mandato | CI-05-013, 015, 040 |
| OF-05-10. Suspender, revocar o vencer un mandato | CI-05-014, 015, 040 |
| OF-05-11. Programar y ejecutar ocurrencias | CI-05-016, 017, 018, 041 |
| OF-05-12. Tramitar la reversión de un débito | CI-05-019, 020, 012, 039 |
| OF-05-13. Habilitar chequeras y emitir instrumentos | CI-05-021, 022, 029, 042 |
| OF-05-14. Administrar aceptación y circulación | CI-05-023, 029, 042 |
| OF-05-15. Depositar y presentar al cobro | CI-05-026, 029, 032, 042 |
| OF-05-16. Resolver pago o rechazo | CI-05-027, 029, 033, 042 |
| OF-05-17. Administrar medidas y devoluciones de instrumentos | CI-05-028, 029, 042 |
| OF-05-18. Administrar participantes y mecanismos | CI-05-030, 031 |
| OF-05-19. Intercambiar instrucciones y resultados | CI-05-032, 033, 034, 043 |
| OF-05-20. Conciliar y resolver diferencias | CI-05-035, 034, 043 |
| OF-05-21. Determinar obligaciones de compensación | CI-05-036, 037, 044 |

CI-05-024 y CI-05-025 proporcionan la integración de instrumentos con la operación de descuento de Préstamos y líneas.
Se complementan con los contratos de presentación, resolución y consulta del instrumento, sin trasladar la
administración de la financiación a Cheques y ECHEQ.
