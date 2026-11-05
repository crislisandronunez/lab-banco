# Contratos de integración — Módulo 12. Atención y operación administrativa

## 1. Propósito y alcance

Este documento define los contratos de integración proporcionados por:

- Reclamos.

- Gestión administrativa.

- Documentos y comprobantes.

- Comunicaciones.

- Extractos.

Comprende atención, reclamos, expedientes, tareas, aprobaciones, evidencias, conservación documental, comunicaciones y
extractos.

La recepción de una petición, su resolución, la ejecución de sus efectos y la comunicación del resultado son hechos
diferentes.

Este módulo coordina actuaciones y presenta información. Los módulos responsables conservan autoridad sobre contratos,
productos, dinero, deuda, tenencias y registros contables.

## 2. Catálogo de contratos

Todos los contratos corresponden a la versión 1.

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-12-001 | Recibir y tramitar una consulta | Gestión administrativa | Solicitud de actuación |
| CI-12-002 | Registrar y clasificar un reclamo | Reclamos | Solicitud de actuación |
| CI-12-003 | Incorporar antecedentes y coordinar una investigación | Reclamos | Solicitud de actuación |
| CI-12-004 | Resolver un reclamo y solicitar reparación | Reclamos | Solicitud de actuación |
| CI-12-005 | Incorporar el resultado de una reparación | Reclamos | Solicitud de actuación |
| CI-12-006 | Comunicar, cerrar o reabrir un reclamo | Reclamos | Solicitud de actuación |
| CI-12-007 | Consultar un reclamo y sus actuaciones | Reclamos | Consulta |
| CI-12-008 | Analizar causas y recurrencia de reclamos | Reclamos | Solicitud de actuación |
| CI-12-009 | Administrar procedimientos administrativos | Gestión administrativa | Solicitud de actuación |
| CI-12-010 | Abrir o actualizar un expediente | Gestión administrativa | Solicitud de actuación |
| CI-12-011 | Crear, asignar o reasignar una tarea | Gestión administrativa | Solicitud de actuación |
| CI-12-012 | Registrar una revisión o aprobación | Gestión administrativa | Solicitud de actuación |
| CI-12-013 | Comprobar una aprobación para su aplicación | Gestión administrativa | Consulta |
| CI-12-014 | Incorporar ejecución, escalar o concluir una tarea | Gestión administrativa | Solicitud de actuación |
| CI-12-015 | Coordinar una actuación externa simulada | Gestión administrativa | Solicitud de actuación |
| CI-12-016 | Consultar un procedimiento, expediente o tarea | Gestión administrativa | Consulta |
| CI-12-017 | Recibir y clasificar un documento | Documentos y comprobantes | Solicitud de actuación |
| CI-12-018 | Emitir un comprobante o certificado | Documentos y comprobantes | Solicitud de actuación |
| CI-12-019 | Consultar documentos y comprobar evidencias | Documentos y comprobantes | Consulta |
| CI-12-020 | Administrar políticas de conservación | Documentos y comprobantes | Solicitud de actuación |
| CI-12-021 | Aplicar o levantar una preservación documental | Documentos y comprobantes | Solicitud de actuación |
| CI-12-022 | Preparar y ejecutar una disposición documental | Documentos y comprobantes | Solicitud de actuación |
| CI-12-023 | Recuperar o corregir documentación | Documentos y comprobantes | Solicitud de actuación |
| CI-12-024 | Consultar una actuación documental | Documentos y comprobantes | Consulta |
| CI-12-025 | Administrar plantillas de comunicación | Comunicaciones | Solicitud de actuación |
| CI-12-026 | Administrar preferencias y consentimientos | Comunicaciones | Solicitud de actuación |
| CI-12-027 | Consultar preferencias y canales admitidos | Comunicaciones | Consulta |
| CI-12-028 | Preparar y enviar una comunicación | Comunicaciones | Solicitud de actuación |
| CI-12-029 | Incorporar una constancia de comunicación | Comunicaciones | Solicitud de actuación |
| CI-12-030 | Recuperar o sustituir un envío fallido | Comunicaciones | Solicitud de actuación |
| CI-12-031 | Consultar una comunicación y sus entregas | Comunicaciones | Consulta |
| CI-12-032 | Administrar modalidades de extracto | Extractos | Solicitud de actuación |
| CI-12-033 | Componer y verificar un extracto | Extractos | Solicitud de actuación |
| CI-12-034 | Emitir y entregar un extracto | Extractos | Solicitud de actuación |
| CI-12-035 | Rectificar un extracto emitido | Extractos | Solicitud de actuación |
| CI-12-036 | Consultar un extracto y sus versiones | Extractos | Consulta |
| CI-12-037 | Comunicar un cambio de reclamo | Reclamos | Evento |
| CI-12-038 | Comunicar una tarea, aprobación o actuación administrativa | Gestión administrativa | Evento |
| CI-12-039 | Comunicar una emisión o actuación documental | Documentos y comprobantes | Evento |
| CI-12-040 | Comunicar un resultado de comunicación | Comunicaciones | Evento |
| CI-12-041 | Comunicar una emisión o rectificación de extracto | Extractos | Evento |

## 3. Reglas comunes de integración

### 3.1. Recepción, identidad y acceso

Las consultas, reclamos y denuncias se reciben aunque falte comprobar identidad o representación.

La recepción conserva fecha, canal, contenido y referencia. No concede acceso a información protegida ni autoriza una
reparación económica.

Cuando falten comprobaciones, se identifican los requisitos pendientes y se limita la información comunicada.

Las medidas urgentes se derivan inmediatamente. Una denuncia de pérdida de tarjeta no queda detenida por exigir el mismo
mecanismo de acceso que podría estar comprometido.

### 3.2. Identificación y repetición

Cada consulta, reclamo, expediente, tarea, aprobación, documento, envío y extracto conserva identidad propia.

Las actuaciones identifican servicio solicitante, causa, objeto, contenido, referencias de origen, actor y facultades
cuando corresponda.

Una repetición con contenido idéntico recupera el resultado. La misma referencia con contenido diferente genera
conflicto.

Los casos relacionados pueden vincularse sin eliminar sus fechas, solicitantes ni evidencias.

### 3.3. Autoridad sobre los efectos

| Información o actuación | Responsable |
| --- | --- |
| Resolución de atención | Reclamos |
| Investigación especializada | Servicio competente |
| Coordinación de tareas y aprobaciones | Gestión administrativa |
| Decisión económica y ejecución de reparación | Módulo responsable |
| Custodia y conservación documental | Documentos y comprobantes |
| Preferencias, envíos y constancias | Comunicaciones |
| Composición y emisión de extractos | Extractos |
| Datos originales de saldos, deuda y tenencias | Servicio que administra la posición |

Una tarea aprobada no demuestra ejecución. Un documento recibido no demuestra autenticidad económica. Una comunicación
enviada no demuestra entrega.

### 3.4. Plazos y seguimiento

Se aplican las convenciones adoptadas en las operaciones funcionales:

- Constancia inmediata en canales digitales y atención directa.

- Clasificación y asignación hasta finalizar el siguiente día hábil.

- Respuesta definitiva ordinaria dentro de diez días hábiles.

- Aviso interno dos días hábiles antes del vencimiento.

- Escalación inmediata al vencer sin resolución.

Los días hábiles se cuentan desde el día siguiente a la recepción.

Los procedimientos especializados conservan sus plazos particulares. Una respuesta provisional no se presenta como
definitiva.

Reasignar, reclasificar o ampliar el mismo problema no reinicia el plazo original. Las demoras internas no constituyen
una prórroga automática.

### 3.5. Aprobación y aplicación

La aprobación identifica:

- Propuesta y versión.

- Contenido exacto.

- Preparador y aprobador.

- Facultades utilizadas.

- Alcance y condiciones.

- Fecha y vigencia cuando corresponda.

Una modificación material exige nueva aprobación.

Gestión administrativa conserva la evidencia y coordinación de la aprobación. El servicio responsable comprueba su
correspondencia y las condiciones de ejecución.

Una aprobación administrativa no sustituye una decisión especializada de crédito, contratación, fraude o cumplimiento.

### 3.6. Documentos y comunicaciones

Los originales, representaciones de lectura, copias y rectificaciones se distinguen.

Los documentos históricos no cambian por actualizar una plantilla o contacto.

Las comunicaciones identifican destinatario, canal, contenido, versión, finalidad y fecha. La entrega al proveedor
simulado no demuestra recepción del destinatario.

Los datos reservados no se incorporan indiscriminadamente a respuestas, documentos, extractos o notificaciones.

### 3.7. Recuperación

Ante incertidumbre se consulta la actuación original antes de repetir una reparación, emisión, disposición o envío.

La falta de respuesta no demuestra ausencia de ejecución.

Las correcciones conservan antecedentes y referencias. Una rectificación documental o informativa no crea movimientos
económicos.

### 3.8. Eventos

Todo evento identifica hecho, objeto, versión o secuencia, causa, momento, resultado y pendientes.

Su repetición no duplica reparaciones, documentos, comunicaciones ni extractos.

Los eventos de resolución, ejecución y entrega mantienen significados distintos.

## 4. Contratos de Reclamos

### CI-12-002. Registrar y clasificar un reclamo

Proveedor: Reclamos. Consumidores: Gestión administrativa y servicios que reciben una disconformidad.

Entrada: presentante, motivo, hechos, recepción original, canal, productos, importe si se conoce, evidencias y medio de
respuesta.

Actuación: registra, entrega constancia, identifica urgencia, vincula operaciones y casos y asigna tratamiento.

Salida: reclamo, constancia, responsables, vencimientos y comprobaciones pendientes.

Reglas y aceptación: no exige conocer el identificador técnico de la operación. Una queja resuelta en el acto también se
registra. Una denuncia urgente se deriva sin esperar la clasificación ordinaria.

### CI-12-003. Incorporar antecedentes y coordinar una investigación

Proveedor: Reclamos. Consumidores: servicios responsables de operaciones, Reclamos especializados y Gestión
administrativa.

Entrada: caso, actuación solicitada o respuesta, hechos, evidencias, referencias y pendientes.

Actuación: solicita antecedentes, integra respuestas y resuelve contradicciones con sus responsables.

Salida: investigación actualizada, tareas, conclusiones parciales e impedimentos.

Reglas y aceptación: no vuelve a pedir al cliente documentos que el banco conserva y puede recuperar. Una respuesta
interna sin evidencia no basta para cerrar.

Las tareas internas vencen como máximo el hábil anterior al plazo de resolución, salvo urgencia. Una asignación cercana
al vencimiento se escala inmediatamente.

### CI-12-004. Resolver un reclamo y solicitar reparación

Proveedor: Reclamos. Consumidor: Gestión administrativa.

Entrada: caso, conclusión, fundamento, partes favorables y desfavorables, reparaciones, conceptos y aprobación
aplicable.

Actuación: registra la resolución y solicita efectos al módulo competente.

Salida: resolución, actuaciones económicas o administrativas solicitadas y pendientes.

Reglas y aceptación: distingue restitución por error de compensación comercial. Cargo indebido 100 e interés asociado
indebido 5 requieren tratar ambos conceptos.

La resolución no ejecuta el reintegro. Las partes desfavorables y reparaciones discrecionales conservan aprobación
separada.

### CI-12-005. Incorporar el resultado de una reparación

Proveedor: Reclamos. Consumidores: servicios responsables de ejecutar la reparación.

Entrada: caso, actuación de reparación, resultado, conceptos, importe o alcance, fecha y referencias de efectos.

Actuación: comprueba correspondencia entre resolución y ejecución y mantiene lo pendiente.

Salida: reparación confirmada, parcial, rechazada o incierta; diferencias y seguimiento.

Reglas y aceptación: un reintegro ya ejecutado por Pagos o Disputas no se repite por el reclamo general. Si solo existe
aprobación, se informa ejecución pendiente.

### CI-12-006. Comunicar, cerrar o reabrir un reclamo

Proveedor: Reclamos. Consumidor: Gestión administrativa; el mismo comportamiento rige el seguimiento del caso.

Entrada: caso, actuación, respuesta fundada, resultados de reparación y comunicación o antecedentes nuevos.

Actuación: solicita entrega, comprueba dimensiones pendientes y cierra o reabre conservando historia.

Salida: resolución emitida, ejecución cumplida, entrega comprobada, cierre integral o pendientes.

Reglas y aceptación: el cierre integral requiere tratamiento de resolución, acciones y entrega. El silencio del cliente
no demuestra reparación.

La reapertura conserva fecha y decisión anteriores. Los hechos nuevos pueden tener actuaciones propias sin reiniciar
artificialmente el plazo del problema original.

### CI-12-007. Consultar un reclamo y sus actuaciones

Proveedor: Reclamos. Consumidores: servicios involucrados y Gestión administrativa dentro de su alcance.

Entrada: caso o referencia de presentación y facultades de consulta.

Salida: recepción, clasificación, plazos, antecedentes, resolución, reparaciones, comunicaciones, reaperturas y
pendientes.

Reglas: el acceso al caso general no revela automáticamente expedientes reservados de fraude, lavado o terceros.

### CI-12-008. Analizar causas y recurrencia de reclamos

Proveedor: Reclamos. Consumidores: Riesgos, servicios responsables de productos y Gestión administrativa.

Entrada: período, ámbito, clasificación, casos y resultados.

Actuación: analiza mensualmente y consolida trimestralmente causas, frecuencia, impacto y acciones generales.

Salida: análisis, problema común, población potencialmente afectada, responsables y seguimiento.

Reglas y aceptación: cien reclamos por el mismo cargo erróneo requieren revisar otros afectados, no solo reclamantes.
Reintegros, compensaciones y pérdidas se distinguen. La mejora no se considera cumplida por su mera propuesta.

## 5. Contratos de Gestión administrativa

### CI-12-001. Recibir y tramitar una consulta

Proveedor: Gestión administrativa. Consumidores: servicios que reciben consultas y canales de atención.

Entrada: pregunta, recepción, canal, solicitante, finalidad y productos pertinentes.

Actuación: responde con fuentes autorizadas o abre expediente. Si detecta disconformidad, deriva a Reclamos conservando
fecha.

Salida: respuesta, fuentes, expediente o reclamo relacionado y pendientes.

Reglas y aceptación: información pública no requiere relación de cliente. Un saldo de tercero exige facultades. Una
fuente faltante no se sustituye por una respuesta inventada.

### CI-12-009. Administrar procedimientos administrativos

Proveedor: Gestión administrativa. Consumidor: función administrativa facultada.

Entrada: finalidad, entradas, tareas, decisiones, responsables, plazos, escalaciones, módulos ejecutores y aprobación.

Actuación: define y habilita una versión del procedimiento.

Salida: procedimiento, versión, vigencia y condiciones.

Reglas y aceptación: no habilita un procedimiento sin responsable de finalización. Recopilar, revisar, aprobar y
ejecutar son tareas diferenciadas. Aprobar crédito sigue correspondiendo a Evaluación crediticia.

### CI-12-010. Abrir o actualizar un expediente

Proveedor: Gestión administrativa. Consumidores: servicios que requieren actuaciones humanas o coordinación
administrativa.

Entrada: procedimiento, operación o necesidad, solicitante, antecedentes, urgencia, documentos y vencimiento.

Actuación: registra el expediente, conserva la versión del procedimiento y organiza actuaciones.

Salida: expediente, responsables, tareas y pendientes.

Reglas: actualizar no borra antecedentes. Un cambio de procedimiento identifica expresamente cómo afecta a expedientes
iniciados.

### CI-12-011. Crear, asignar o reasignar una tarea

Proveedor: Gestión administrativa. Consumidores: servicios responsables de procesos y funciones administrativas
facultadas.

Entrada: expediente, objeto, tipo de actuación, contenido, función requerida, vencimiento, urgencia y separación de
funciones.

Actuación: crea y asigna a una función habilitada o conserva falta de asignación.

Salida: tarea, responsable, facultades requeridas, plazo y escalación.

Reglas y aceptación: las urgentes se asignan inmediatamente. Las ordinarias sin asignación al finalizar el siguiente
hábil se escalan. Reasignar conserva responsables y vencimientos anteriores.

### CI-12-012. Registrar una revisión o aprobación

Proveedor: Gestión administrativa. Consumidor: función humana facultada para la tarea.

Entrada: tarea, propuesta y versión, contenido exacto, decisión, fundamento y actor.

Actuación: comprueba facultades y separación, registra revisión, aprobación o rechazo y conserva evidencia.

Salida: decisión, referencia de aprobación, alcance y condiciones.

Reglas y aceptación: el preparador no aprueba cuando se exige separación. Una facultad retirada antes de decidir impide
aprobar con ella. Cambiar el importe exige nueva revisión.

La decisión especializada se registra o confirma en su servicio competente; la tarea administrativa no la sustituye.

### CI-12-013. Comprobar una aprobación para su aplicación

Proveedor: Gestión administrativa. Consumidores: servicios que ejecutan actuaciones sujetas a aprobación.

Entrada: aprobación, propuesta, versión, contenido, operación y actuación de ejecución.

Salida: correspondencia comprobada, alcance, condiciones o impedimentos.

Reglas: no devuelve un permiso genérico. La aprobación debe corresponder al contenido exacto.

El ejecutor comprueba además las facultades y condiciones vigentes que su operación requiera y conserva su propia
referencia de ejecución. Reutilizar la consulta no habilita repetir el efecto.

### CI-12-014. Incorporar ejecución, escalar o concluir una tarea

Proveedor: Gestión administrativa. Consumidores: servicios ejecutores y funciones administrativas facultadas.

Entrada: tarea, actuación, resultado, evidencia, referencia de ejecución o motivo de escalación.

Actuación: distingue aprobación, ejecución y comprobación; escala o concluye lo satisfecho.

Salida: tarea actualizada, efectos relacionados, atraso y pendientes.

Reglas y aceptación: una tarea aprobada sin ejecución permanece pendiente de ejecutar. No cambia la fecha original para
ocultar atraso. Concluir la tarea no extingue obligaciones administradas por otro servicio.

### CI-12-015. Coordinar una actuación externa simulada

Proveedor: Gestión administrativa. Consumidores: funciones facultadas y servicios que reciben requerimientos
institucionales.

Entrada: comunicación, origen, destinatario, autenticidad simulada, objeto, recepción, vencimiento y documentos.

Actuación: verifica, deriva, reúne respuesta, obtiene aprobación y coordina entrega al participante simulado.

Salida: expediente, verificación, derivaciones, respuesta y constancia.

Reglas y aceptación: medidas sobre productos se derivan a Restricciones; reportes a Información regulatoria; actuaciones
de recuperación a Cobranzas y Garantías. Una autoridad no comprobada no recibe información protegida.

### CI-12-016. Consultar un procedimiento, expediente o tarea

Proveedor: Gestión administrativa. Consumidores: servicios relacionados y actores facultados.

Entrada: referencia y finalidad.

Salida: versiones, contenido, responsables, plazos, decisiones, aprobaciones, ejecuciones, documentos y pendientes.

Reglas: permite reconstruir la coordinación sin atribuirle autoridad sobre datos económicos de origen.

## 6. Contratos de Documentos y comprobantes

### CI-12-017. Recibir y clasificar un documento

Proveedor: Documentos y comprobantes. Consumidores: servicios que reciben o generan evidencias y Gestión administrativa.

Entrada: contenido, origen, aportante, finalidad, proceso, tipo, fecha, sensibilidad y antecedentes de versión.

Actuación: comprueba integridad y admisión, identifica, clasifica y vincula.

Salida: documento, versión, procedencia, observaciones, acceso y conservación.

Reglas y aceptación: recibir no verifica autenticidad jurídica o económica. Conserva el original sin alteración. Una
copia idéntica aportada en otro trámite mantiene la nueva procedencia. Si falta categoría, bloquea eliminación y
divulgación hasta clasificar.

### CI-12-018. Emitir un comprobante o certificado

Proveedor: Documentos y comprobantes. Consumidores: servicios responsables del hecho certificado.

Entrada: emisión, tipo documental, hecho y resultado confirmado, datos, fuente, plantilla aprobada y firma o aprobación
aplicable.

Actuación: comprueba correspondencia, genera y conserva contenido y referencia.

Salida: documento, versión, fecha, hecho certificado y condición.

Reglas y aceptación: una transferencia enviada sin acreditación no se certifica como acreditada. La constancia de deuda
o cancelación procede del acreedor. Intereses pendientes impiden libre deuda total.

Solicitar otra copia conserva la misma emisión. Una rectificación vincula el documento anterior sin borrarlo.

### CI-12-019. Consultar documentos y comprobar evidencias

Proveedor: Documentos y comprobantes. Consumidores: servicios y actores con necesidad y facultades suficientes.

Entrada: documento o ámbito, solicitante, finalidad, versión y alcance.

Salida: contenido o copia autorizada, procedencia, integridad, versiones y referencias.

Reglas y aceptación: ser cliente no concede acceso a terceros. Las copias parciales o disociadas se identifican. Una
alteración impide presentar el contenido como íntegro. Verificar firma no sustituye comprobar facultades contractuales.

### CI-12-020. Administrar políticas de conservación

Proveedor: Documentos y comprobantes. Consumidor: Gestión administrativa.

Entrada: categoría, fundamentos, plazo, hecho inicial, accesos, obligaciones concurrentes y aprobación separada.

Actuación: valida con las funciones competentes y habilita una versión.

Salida: política, vigencia, categorías y reglas de cálculo de conservación.

Política base:

| Categoría | Plazo base | Inicio |
| --- | --- | --- |
| Contratos y aceptaciones | Diez años | Último entre terminación y resolución de obligaciones |
| Comprobantes y evidencia operativa | Diez años | Confirmación o resolución definitiva |
| Identificación y representación | Diez años | Última finalización de relación o actuación aplicable |
| Reclamos y reparaciones | Diez años | Cierre definitivo del caso y actuaciones |
| Expedientes y aprobaciones | Diez años | Finalización y resolución de consecuencias |
| Comunicaciones obligatorias y entregas | Diez años | Entrega o conclusión de incidencia, considerando el proceso asociado |
| Extractos y rectificaciones | Diez años | Emisión, conservando relacionadas sus versiones |
| Publicidad sin efecto contractual | Dos años | Finalización de campaña o último envío |

Reglas: prevalece la fecha permitida más tardía entre fundamentos aplicables. Una preservación vigente impide disponer.
Falta de clasificación abre resolución y bloquea eliminación; no justifica conservación indefinida sin tratamiento.

### CI-12-021. Aplicar o levantar una preservación documental

Proveedor: Documentos y comprobantes. Consumidores: Reclamos, Prevención de fraude, Prevención de lavado, Restricciones,
Información regulatoria y otros responsables de casos facultados.

Entrada: causa, autoridad, caso, alcance documental, momento y condición de finalización.

Actuación: aplica o levanta la preservación identificada y determina documentos alcanzados.

Salida: preservación, cobertura, documentos y pendientes.

Reglas y aceptación: el vencimiento de conservación no elimina un documento preservado. Levantar una preservación no
levanta otras ni autoriza por sí solo la disposición.

La preservación no divulga la investigación que la origina.

### CI-12-022. Preparar y ejecutar una disposición documental

Proveedor: Documentos y comprobantes. Consumidor: Gestión administrativa.

Entrada: documentos candidatos, políticas, fundamentos, verificación de impedimentos y aprobación separada.

Actuación: comprueba elegibilidad, revisa nuevamente preservaciones antes de ejecutar y registra el resultado.

Salida: disposición, alcance ejecutado, copias o respaldos pendientes y constancia.

Reglas y aceptación: aprobar no equivale a eliminar. Una preservación concurrente debe resolverse antes de ejecutar la
disposición afectada.

No informa eliminación física integral hasta completar el tratamiento de copias y respaldos. La constancia conserva lo
necesario sin reproducir el contenido eliminado. Disponer un documento no borra contratos ni movimientos de otros
servicios.

### CI-12-023. Recuperar o corregir documentación

Proveedor: Documentos y comprobantes. Consumidores: servicios responsables del proceso y Gestión administrativa.

Entrada: documento, incidencia, evidencia, copia recuperable o corrección de metadatos.

Actuación: impide uso no confiable, recupera, verifica o registra rectificación.

Salida: documento recuperado, metadatos corregidos, nueva versión o pérdida e incidencia.

Reglas y aceptación: no fabrica originales. Una reconstrucción desde registros se identifica como reconstruida. Una
restauración reaplica disposiciones y preservaciones vigentes. La falta de documento no cambia por sí sola el hecho
económico.

### CI-12-024. Consultar una actuación documental

Proveedor: Documentos y comprobantes. Consumidores: servicios relacionados y Gestión administrativa.

Entrada: recepción, emisión, preservación, disposición o recuperación.

Salida: contenido de actuación, decisiones, alcance, evidencia, resultado y pendientes.

Reglas: permite recuperar resultados sin emitir otra vez, repetir una eliminación o divulgar documentos fuera del
alcance autorizado.

## 7. Contratos de Comunicaciones

### CI-12-025. Administrar plantillas de comunicación

Proveedor: Comunicaciones. Consumidores: Gestión administrativa y módulo responsable del significado de negocio.

Entrada: finalidad, destinatarios, contenido, campos, fuentes, canales, accesibilidad, sensibilidad, vigencia y
aprobaciones.

Actuación: valida y habilita una versión por canal.

Salida: plantilla, versión, requisitos y vigencia.

Reglas y aceptación: Contratos valida contenido contractual. Una operación pendiente no se describe como exitosa. Campos
esenciales faltantes impiden un envío que altere su significado. Las versiones anteriores permanecen vinculadas a sus
envíos.

### CI-12-026. Administrar preferencias y consentimientos

Proveedor: Comunicaciones. Consumidor: Gestión administrativa.

Entrada: participante, finalidad, canales, selección o revocación, contactos referenciados y evidencia.

Actuación: registra preferencias y consentimiento cuando sea necesario.

Salida: preferencias, vigencia, evidencia y canales pendientes de verificación.

Reglas y aceptación: Personas mantiene contactos; Comunicaciones, preferencias. Publicidad requiere autorización
diferenciada. Revocarla impide nuevos envíos opcionales, sin impedir comunicaciones obligatorias por medios válidos.

Un contacto nuevo no recibe información sensible hasta verificarse.

### CI-12-027. Consultar preferencias y canales admitidos

Proveedor: Comunicaciones. Consumidores: servicios que necesitan entregar información.

Entrada: participante, finalidad, sensibilidad y momento.

Salida: canales admitidos, preferencias, consentimientos, referencias de contactos verificados y limitaciones.

Reglas: preferencia no demuestra entrega ni obliga a usar un canal incapaz de proteger el contenido. Los contactos se
obtienen de su fuente responsable.

### CI-12-028. Preparar y enviar una comunicación

Proveedor: Comunicaciones. Consumidores: servicios responsables de comunicar decisiones, hechos o documentos.

Entrada: solicitud, operación de origen, finalidad, destinatarios, contenido o plantilla y datos, documentos,
sensibilidad, plazo y canales admitidos.

Actuación: comprueba destinatarios, contacto, preferencias, vigencia y confidencialidad y envía.

Salida: comunicación, contenido conservado, destinos utilizados, canal, fecha, intentos y estado.

Reglas y aceptación: una solicitud repetida recupera el seguimiento. Actualizar contacto no cambia el destino histórico.
No adjunta documentos reservados a canales no habilitados.

Una comunicación no modifica por sí sola contrato, producto o vencimiento.

### CI-12-029. Incorporar una constancia de comunicación

Proveedor: Comunicaciones. Consumidores: proveedores y canales simulados; Gestión administrativa para constancias de
atención asistida autorizadas.

Entrada: comunicación, intento, destinatario, canal, resultado, momento y evidencia.

Actuación: clasifica aceptación técnica, entrega comprobada, rechazo o incertidumbre.

Salida: constancia incorporada, alcance por destinatario y pendientes.

Reglas y aceptación: la recepción por el proveedor no prueba entrega. Una constancia tardía se incorpora al envío
original. No informa comunicación completa si falta un destinatario obligatorio.

### CI-12-030. Recuperar o sustituir un envío fallido

Proveedor: Comunicaciones. Consumidores: servicio originante y Gestión administrativa; el mismo comportamiento rige la
recuperación automática aprobada.

Entrada: comunicación, fallo o incertidumbre, resultado de consulta, vigencia de finalidad y alternativa autorizada.

Actuación: consulta antes de duplicar; reintenta fallas transitorias comprobadas o utiliza una alternativa verificada.

Salida: intento relacionado, canal alternativo, entrega o tarea asistida pendiente.

Reglas y aceptación: reintentos a cinco y treinta minutos ante falla transitoria comprobada. Si persiste y no hay
alternativa suficiente, abre tarea asistida para el siguiente hábil o antes si el vencimiento exige urgencia.

Un rechazo definitivo no origina reintentos indefinidos. Los reintentos cesan si el mensaje perdió finalidad o fue
sustituido. El origen decide consecuencias contractuales de la entrega fallida.

### CI-12-031. Consultar una comunicación y sus entregas

Proveedor: Comunicaciones. Consumidores: servicio originante, Reclamos, Documentos y comprobantes y Gestión
administrativa.

Entrada: comunicación o solicitud de origen.

Salida: contenido, versiones, destinos históricos, intentos, constancias, entrega por destinatario, fallas y pendientes.

Reglas: consultar no reenvía. Permite comprobar qué fue entregado y cuándo, sin inferir lectura o aceptación cuando la
evidencia no las acredita.

## 8. Contratos de Extractos

### CI-12-032. Administrar modalidades de extracto

Proveedor: Extractos. Consumidor: Gestión administrativa.

Entrada: modalidad, productos, fuentes, campos, corte, monedas, periodicidad, controles, destinatarios y aprobación.

Actuación: habilita una modalidad y conserva versiones.

Salida: modalidad, fuentes, calendario y requisitos.

Modalidades: extracto mensual de cuentas, informe mensual de inversiones, consolidado mensual solicitado y constancia a
fecha.

Reglas y aceptación: período mensual calendario y emisión ordinaria dentro de los dos primeros hábiles siguientes. El
resumen de tarjeta lo elabora Cuenta de tarjeta; el consolidado lo referencia sin recalcular total ni vencimiento.

### CI-12-033. Componer y verificar un extracto

Proveedor: Extractos. Consumidor: Gestión administrativa; el mismo comportamiento rige la composición periódica.

Entrada: modalidad, titular o cuenta, período, corte y fuentes requeridas.

Actuación: obtiene datos autorizados, comprueba continuidad y totales y prepara composición reproducible.

Salida: composición, fuentes y versiones, controles, pendientes y condición completa o parcial.

Reglas y aceptación: inicial 100, créditos 50 y débitos 30 producen final 120. Una reserva 20 se muestra separada sin
transformar ese saldo en 100.

Separa monedas, capital, rendimiento, deuda, cantidades y valuaciones. Una conversión informativa identifica cotización.
No altera el origen para cuadrar.

### CI-12-034. Emitir y entregar un extracto

Proveedor: Extractos. Consumidor: Gestión administrativa; el mismo comportamiento rige la emisión periódica verificada.

Entrada: composición verificada, período, versión y destinatarios autorizados.

Actuación: identifica la emisión, conserva fuentes, solicita documento y coordina entrega.

Salida: extracto, documento, comunicación, condición y pendientes.

Reglas y aceptación: un faltante que impide integridad mantiene la emisión completa pendiente. Si vence el plazo,
registra incidencia y comunica demora.

La información parcial se identifica expresamente y no sustituye la completa. Entrega fallida no cambia movimientos ni
justifica otro cobro. Una copia recupera la emisión existente.

### CI-12-035. Rectificar un extracto emitido

Proveedor: Extractos. Consumidores: servicios de origen que comunican correcciones y Gestión administrativa.

Entrada: emisión, causa, corrección validada de origen o error de composición, corte y evidencia.

Actuación: reconstruye el período, compara y emite versión rectificativa, conservando la anterior.

Salida: rectificación, diferencias, documento y comunicación.

Reglas y aceptación: omitir un movimiento existente se corrige sin repetirlo. Un error de saldo requiere corrección en
Fondos. Si cambia el saldo final, verifica continuidad con períodos siguientes.

No incorpora movimientos posteriores indebidamente al corte anterior.

### CI-12-036. Consultar un extracto y sus versiones

Proveedor: Extractos. Consumidores: Gestión administrativa, Reclamos y servicios relacionados dentro de sus facultades.

Entrada: emisión, período, cuenta, titular y alcance.

Salida: versión conservada, fuentes, corte, condición, documento, rectificaciones y entrega.

Reglas: consultar una versión histórica no la recalcula con datos actuales. Los originales y rectificaciones permanecen
identificados.

## 9. Contratos de eventos

### CI-12-037. Comunicar un cambio de reclamo

Proveedor: Reclamos. Consumidores: servicios involucrados, Gestión administrativa, Comunicaciones y Riesgos cuando
exista incidente relevante.

Contenido: caso, versión, recepción, plazo, asignación, resolución, reparación, entrega, cierre o reapertura.

Reglas: una resolución favorable no demuestra reintegro. La información especializada reservada no se difunde con el
seguimiento general.

### CI-12-038. Comunicar una tarea, aprobación o actuación administrativa

Proveedor: Gestión administrativa. Consumidores: servicios responsables de la actuación y funciones autorizadas.

Contenido: expediente, tarea, propuesta, versión, responsable, decisión, aprobación, ejecución, vencimiento y
pendientes.

Reglas: el evento de aprobación no ejecuta por sí solo una operación. El consumidor comprueba contenido y condiciones y
conserva su referencia de efecto.

### CI-12-039. Comunicar una emisión o actuación documental

Proveedor: Documentos y comprobantes. Consumidores: servicio solicitante, Gestión administrativa y responsables de casos
afectados.

Contenido: documento, versión, emisión, preservación, disposición, recuperación, integridad y pendientes.

Reglas: disposición aprobada y eliminación completada son hechos distintos. El evento no expone contenido reservado a
destinatarios no autorizados.

### CI-12-040. Comunicar un resultado de comunicación

Proveedor: Comunicaciones. Consumidores: servicio originante, Reclamos, Extractos, Documentos y comprobantes y Gestión
administrativa.

Contenido: comunicación, intento, destinatario, canal, aceptación, entrega, rechazo o incertidumbre, fecha y evidencia.

Reglas: el origen determina consecuencias de entrega o falla. Cuenta de tarjeta ajusta su vencimiento cuando
corresponde; Contratos determina el efecto sobre sus avisos.

Una constancia repetida no genera otro envío.

### CI-12-041. Comunicar una emisión o rectificación de extracto

Proveedor: Extractos. Consumidores: Gestión administrativa, Reclamos, Documentos y comprobantes y Comunicaciones.

Contenido: emisión, versión, período, corte, productos, condición, documento y rectificación relacionada.

Reglas: no representa un movimiento económico. La nueva versión no borra la anterior ni vuelve a ejecutar una corrección
de origen.

## 10. Integraciones consumidas

### 10.1. Institución, personas y contratación

| Contrato | Uso |
| --- | --- |
| CI-01-004 | Comprobar facultades y separación de funciones. |
| CI-01-011 | Consultar condiciones comerciales aplicables a respuestas o actuaciones. |
| CI-01-012 | Recuperar oferta y versión sin presentar condiciones retiradas como vigentes. |
| CI-01-014 | Consultar calendario y convenciones. |
| CI-01-015 | Determinar plazos y avisos. |
| CI-02-002 | Consultar identidad y contactos de su fuente responsable. |
| CI-02-015 | Consultar relación comercial. |
| CI-02-021 | Comprobar representación para la actuación o acceso. |
| CI-03-022 | Consultar contrato, condiciones y situación. |
| CI-03-024 | Consultar actuaciones contractuales vinculadas. |

Las operaciones administrativas no sustituyen la aceptación contractual ni las facultades sobre el producto.

### 10.2. Productos, dinero y posiciones

| Contratos | Uso |
| --- | --- |
| CI-04-002, 012, 022, 030 | Consultar cuentas, movimientos, actuaciones monetarias y efectivo. |
| CI-05-006, 012, 018, 029, 034 | Consultar transferencias, pagos, ocurrencias, instrumentos e intercambios. |
| CI-06-005, 010, 018, 032, 037 | Consultar evaluación, garantías, deuda, actuaciones y cobranza. |
| CI-07-004, 013, 021, 024, 034, 041 | Consultar instrumentos, autorizaciones, presentaciones, deuda y disputas. |
| CI-08-009, 027, 030, 041 | Consultar depósitos, órdenes, posiciones y traslados. |
| CI-10-017, 023, 027, 032 | Consultar operaciones propias, cambio, liquidaciones y actuaciones. |

Las reparaciones se solicitan mediante el contrato de corrección, devolución o ajuste del servicio competente. No se
utiliza un asiento administrativo o documento para sustituir esa ejecución.

Los resultados pueden incorporarse por respuesta o evento, manteniendo la referencia del mismo efecto para evitar
duplicaciones.

### 10.3. Contabilidad, riesgo y cumplimiento

| Contrato | Uso |
| --- | --- |
| CI-09-012 | Consultar registraciones vinculadas cuando sea necesario. |
| CI-09-026 | Reconstruir cifras contables dentro de las facultades. |
| CI-09-027 | Recuperar informes emitidos. |
| CI-11-016 | Comprobar resultados de conocimiento del cliente. |
| CI-11-024, 025 | Coordinar casos de fraude y sus resultados autorizados. |
| CI-11-026, 031 | Derivar medidas y consultar su seguimiento. |
| CI-11-044 | Consultar requerimientos y presentaciones dentro del acceso permitido. |

Los saldos de extractos de clientes proceden de los servicios operativos. No se sustituyen por saldos contables de otra
naturaleza.

Los expedientes reservados de cumplimiento no se incorporan a respuestas generales ni extractos.

### 10.4. Correspondencia con los contratos anteriores

| Necesidad de los módulos 1 a 11 | Contrato del módulo 12 |
| --- | --- |
| Coordinar una actuación humana | CI-12-010, 011 |
| Preparar y comprobar aprobación separada | CI-12-012, 013 |
| Registrar ejecución o escalar una tarea | CI-12-014 |
| Recibir evidencia | CI-12-017 |
| Emitir constancia o certificado | CI-12-018 |
| Consultar evidencia y comprobar integridad | CI-12-019 |
| Preservar documentación por un caso | CI-12-021 |
| Resolver indisponibilidad o pérdida documental | CI-12-023 |
| Entregar propuesta, resolución o documento | CI-12-028 |
| Comprobar entrega y recuperar un fallo | CI-12-029, 030, 031 |
| Exponer información periódica de clientes | CI-12-033, 034 |
| Rectificar una presentación periódica | CI-12-035 |

Para la entrega de resúmenes de tarjeta, Comunicaciones informa mediante CI-12-040. Cuenta de tarjeta incorpora la
evidencia mediante CI-07-023 y aplica sus reglas de vencimiento.

Para una propuesta contractual, Comunicaciones y Documentos conservan contenido y entrega; Contratos determina su
validez, aceptación y consecuencias.

## 11. Correspondencia con las operaciones funcionales

| Operación | Contratos principales |
| --- | --- |
| OF-12-01. Recibir y responder una consulta | CI-12-001, 010, 016 |
| OF-12-02. Registrar y clasificar un reclamo | CI-12-002, 007 |
| OF-12-03. Investigar y coordinar la resolución | CI-12-003, 011, 019 |
| OF-12-04. Resolver y ejecutar medidas de reparación | CI-12-004, 005, 012, 013 |
| OF-12-05. Comunicar, cerrar y reabrir reclamos | CI-12-006, 007, 028, 031 |
| OF-12-06. Analizar causas y recurrencia de reclamos | CI-12-008 |
| OF-12-07. Administrar procedimientos y expedientes | CI-12-009, 010, 016 |
| OF-12-08. Asignar, aprobar y escalar tareas | CI-12-011, 012, 013, 014 |
| OF-12-09. Coordinar actuaciones externas simuladas | CI-12-015, 016 |
| OF-12-10. Recibir y clasificar documentos | CI-12-017, 024 |
| OF-12-11. Emitir comprobantes y certificados | CI-12-018, 019, 039 |
| OF-12-12. Consultar documentos y comprobar evidencias | CI-12-019 |
| OF-12-13. Administrar políticas de conservación | CI-12-020 |
| OF-12-14. Preservar y disponer documentación | CI-12-021, 022, 024 |
| OF-12-15. Recuperar y corregir documentación | CI-12-023, 024 |
| OF-12-16. Administrar plantillas de comunicación | CI-12-025 |
| OF-12-17. Administrar preferencias y consentimientos | CI-12-026, 027 |
| OF-12-18. Preparar y enviar comunicaciones | CI-12-028, 031 |
| OF-12-19. Comprobar entregas y recuperar envíos fallidos | CI-12-029, 030, 031, 040 |
| OF-12-20. Administrar modalidades de extracto | CI-12-032 |
| OF-12-21. Componer y verificar extractos | CI-12-033 |
| OF-12-22. Emitir y entregar extractos | CI-12-034, 036, 041 |
| OF-12-23. Rectificar extractos emitidos | CI-12-035, 036, 041 |
