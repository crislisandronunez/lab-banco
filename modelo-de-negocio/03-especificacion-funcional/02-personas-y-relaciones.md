# Operaciones funcionales — Módulo 2. Personas y relaciones bancarias

## 1. Propósito y alcance

Este documento define las operaciones para registrar personas, mantener sus relaciones, tramitar su vinculación al banco
y administrar la relación comercial y las facultades de representación.

Comprende los microservicios Personas, Vinculación, Clientes y Representación.

Registrar una persona, verificar su identidad, admitirla como cliente y autorizarla para actuar son hechos diferentes.
Ninguno sustituye los requisitos de los demás.

Las decisiones operativas establecidas corresponden al banco simulado. Los plazos de trámite aquí fijados no representan
plazos regulatorios.

## 2. Catálogo de operaciones

| Identificador | Operación                                                  | Responsable    |
|---------------|------------------------------------------------------------|----------------|
| OF-02-01      | Registrar una persona                                      | Personas       |
| OF-02-02      | Actualizar datos de una persona                            | Personas       |
| OF-02-03      | Administrar relaciones y composición societaria            | Personas       |
| OF-02-04      | Resolver registros duplicados                              | Personas       |
| OF-02-05      | Registrar circunstancias personales y jurídicas especiales | Personas       |
| OF-02-06      | Solicitar la vinculación al banco                          | Vinculación    |
| OF-02-07      | Evaluar y resolver la admisión                             | Vinculación    |
| OF-02-08      | Constituir la relación de cliente                          | Clientes       |
| OF-02-09      | Mantener y revisar la relación comercial                   | Clientes       |
| OF-02-10      | Desvincular y readmitir un cliente                         | Clientes       |
| OF-02-11      | Registrar y verificar una representación                   | Representación |
| OF-02-12      | Modificar, renovar y terminar una representación           | Representación |
| OF-02-13      | Comprobar facultades de representación                     | Representación |

## 3. Reglas comunes del módulo

### 3.1. Identidad y relación comercial

Cada persona recibe una referencia interna estable.

Los documentos e identificadores tributarios conservan tipo, jurisdicción, emisor y vigencia cuando corresponda. No
reemplazan la referencia interna ni demuestran por sí solos identidad, capacidad o representación.

Cada persona mantiene una única identificación de cliente del banco, aunque tenga varios productos o distintos períodos
de relación comercial.

### 3.2. Datos declarados y verificados

Personas conserva datos, procedencia e historia. Conocimiento del cliente conserva verificaciones y conclusiones.

Se distinguen:

- Información declarada.

- Evidencia recibida.

- Información verificada.

- Información observada o contradictoria.

- Información cuya actualización está pendiente.

La existencia de un dato verificado no permite borrar declaraciones o evidencias anteriores.

### 3.3. Facultades y decisiones

Se comprueban por separado las facultades para aportar datos, consultar información, modificar registros y actuar por
otra persona.

La admisión, el rechazo, la resolución de duplicados y el reconocimiento de representaciones requieren preparación y
aprobación por personas diferentes.

Las actualizaciones ordinarias solicitadas por el propio participante pueden completarse sin intervención humana
adicional cuando las comprobaciones definidas resulten satisfactorias.

Gestión administrativa coordina las tareas humanas. Los servicios de este módulo conservan sus decisiones.

### 3.4. Plazos de solicitudes y subsanación

El banco adopta las siguientes reglas:

- La solicitud inicial dispone de 30 días corridos para completar la información exigida al solicitante.

- El plazo comienza al día siguiente de poner a disposición el requerimiento inicial por el canal elegido y habilitado
  para el trámite.

- Vence al finalizar el trigésimo día. Si ese día es inhábil bancario, se traslada al final del siguiente hábil.

- Se informa el vencimiento al emitir el requerimiento y se genera un recordatorio cinco días corridos antes.

- Un requerimiento nuevo necesario por una omisión del banco no perjudica al solicitante: establece un nuevo plazo de 30
  días para los puntos añadidos.

- Una presentación recibida dentro del plazo impide declarar vencimiento mientras se comprueba su suficiencia.

- Si la presentación es insuficiente, se emite un requerimiento final con 15 días corridos, calculados del mismo modo.

- Las demoras de verificaciones internas o de participantes simulados no consumen un plazo de respuesta del solicitante.

- Una indisponibilidad del canal que impida responder durante el vencimiento extiende el plazo hasta finalizar el
  segundo día hábil posterior a la recuperación.

El vencimiento concluye el trámite pendiente de respuesta, no constituye rechazo comercial ni elimina la persona.

La reapertura requiere petición del solicitante. Conserva el expediente anterior y vuelve a comprobar datos, reglas y
verificaciones vigentes; no recupera automáticamente aprobaciones anteriores.

### 3.5. Revisiones de clientes

El banco establece un máximo ordinario de 12 meses calendario entre revisiones de la relación comercial.

Conocimiento del cliente o Riesgos pueden establecer una próxima revisión anterior mediante sus criterios aprobados. Una
exigencia obligatoria más estricta prevalece.

La fecha se calcula desde la finalización de la revisión anterior, conservando su anclaje y utilizando el siguiente día
hábil cuando corresponda.

Los cambios relevantes de identidad, actividad, estructura, representación o restricciones anticipan la revisión sin
esperar al vencimiento periódico.

### 3.6. Versiones, concurrencia y recuperación

Toda modificación identifica su versión de origen.

Si cambian antecedentes relevantes antes de resolver, se revisan las comprobaciones afectadas. No se sobrescriben
cambios posteriores.

Cada actuación tiene una referencia identificable. Su repetición devuelve el resultado existente sin crear personas,
clientes o representaciones duplicados.

La falta de respuesta exige consultar antes de repetir o dar por fallida una actuación.

### 3.7. Restricciones y protección de información

Las medidas se administran en Restricciones y se aplican mediante los servicios afectados.

Una revisión pendiente no bloquea automáticamente productos ni fondos. Las consecuencias restrictivas requieren
fundamento, alcance y aplicación confirmada.

La documentación se conserva mediante Documentos y comprobantes. Las comunicaciones respetan destinatarios, facultades y
confidencialidad.

---

## 4. OF-02-01. Registrar una persona

### Propósito y responsable

Crear o recuperar la identidad de una persona humana o jurídica que participa del negocio.

Responsable: Personas.

### Información y condiciones previas

Se requiere tipo de persona, nombre o denominación, identificadores disponibles, datos de nacimiento o constitución,
procedencia y finalidad.

Los datos desconocidos se distinguen de los no aplicables.

### Desarrollo

- Recibir datos y antecedentes.

- Buscar coincidencias.

- Comprobar estructura y coherencia de identificadores.

- Determinar si existe una persona correspondiente.

- Crear la referencia interna cuando sea necesario.

- Conservar los datos y sus fuentes.

- Solicitar verificaciones cuando el proceso lo requiera.

### Reglas y decisiones

- Una coincidencia relevante de identificadores impide crear automáticamente otra persona.

- El nombre por sí solo no demuestra duplicación.

- Un registro incompleto puede conservarse como antecedente, pero no satisface requisitos pendientes.

- Las personas jurídicas identifican forma, jurisdicción y antecedentes constitutivos.

- Los representantes, garantes y otros participantes pueden registrarse sin ser clientes.

- El registro no concede capacidad ni facultades.

### Resultados y excepciones

Se obtiene una persona registrada, una referencia existente o una coincidencia pendiente de investigación.

### Criterios de aceptación

| Situación                                                                  | Resultado esperado                             |
|----------------------------------------------------------------------------|------------------------------------------------|
| Llega otra petición con el mismo identificador y antecedentes coincidentes | Se recupera la persona existente.              |
| Dos personas comparten nombre, pero tienen identidades diferentes          | Se conservan registros separados.              |
| Falta verificar documentación                                              | La persona no se presenta como verificada.     |
| Se registra un garante                                                     | No se constituye automáticamente como cliente. |

## 5. OF-02-02. Actualizar datos de una persona

### Propósito y responsable

Mantener datos actuales conservando su historia.

Responsable: Personas.

### Información y condiciones previas

Se requiere persona, campos, valores propuestos, motivo, actor, evidencia y versión de origen.

### Desarrollo

- Comprobar identidad y facultades del solicitante.

- Clasificar los datos afectados.

- Solicitar evidencia y verificaciones.

- Revisar contradicciones.

- Aplicar los cambios admitidos.

- Conservar valores y fuentes anteriores.

- Comunicar cambios relevantes.

### Reglas y decisiones

- Nombre, identificadores, constitución y capacidad requieren evidencia y revisión especializada.

- Los contactos nuevos deben verificarse antes de utilizarse para actuaciones sensibles.

- Cambiar un contacto no se autoriza exclusivamente mediante el mismo contacto nuevo.

- Si se perdieron los medios anteriores, se utiliza recuperación de identidad con verificación adicional.

- El dato anterior conserva su uso histórico y puede continuar como contacto hasta confirmar el nuevo, salvo que exista
  una medida que lo impida.

- Una modificación parcial identifica qué campos se aplicaron.

- Los cambios relevantes de actividad o identidad generan revisión en Conocimiento del cliente.

### Resultados y excepciones

La actualización queda aplicada, parcialmente aplicada, pendiente o rechazada con fundamento.

### Criterios de aceptación

| Situación                                            | Resultado esperado                                                |
|------------------------------------------------------|-------------------------------------------------------------------|
| Se modifica el domicilio                             | El anterior permanece en la historia.                             |
| El nuevo documento contradice la identidad existente | Se requiere resolución antes de reemplazarlo.                     |
| Se declara otro teléfono                             | No se utiliza para autorizar cambios sensibles hasta verificarlo. |
| La versión cambió durante el trámite                 | Se revisan las diferencias antes de aplicar.                      |

## 6. OF-02-03. Administrar relaciones y composición societaria

### Propósito y responsable

Registrar vínculos relevantes y estructuras de propiedad declaradas.

Responsable: Personas.

### Información y condiciones previas

Se identifican participantes, naturaleza, fechas, fuentes y documentación.

Las participaciones indican clase, porcentaje o cantidad, derechos económicos, derechos de voto y base de medición
cuando corresponda.

### Desarrollo

- Comprobar participantes.

- Registrar la relación propuesta.

- Revisar coherencia temporal y cuantitativa.

- Incorporar participaciones directas y cadenas declaradas.

- Identificar información faltante.

- Solicitar verificaciones especializadas.

- Comunicar cambios relevantes.

### Reglas y decisiones

- Los vínculos familiares, societarios o laborales no conceden representación.

- Los porcentajes se comparan sobre una misma base y fecha.

- Los derechos económicos y de voto se mantienen diferenciados.

- Las participaciones circulares se conservan y se derivan a análisis.

- Una estructura incompleta no se completa con supuestos.

- Conocimiento del cliente determina beneficiarios finales; Personas conserva las declaraciones.

- Riesgos utiliza los vínculos para su consolidación conforme a sus propios criterios.

### Criterios de aceptación

| Situación                                       | Resultado esperado                              |
|-------------------------------------------------|-------------------------------------------------|
| Se registra un socio con participación del 40 % | Se conserva la base y fecha de ese porcentaje.  |
| Tiene 40 % económico y 60 % de voto             | Ambos derechos permanecen separados.            |
| La estructura declarada está incompleta         | Se identifica el faltante.                      |
| Un socio solicita actuar por la sociedad        | Debe comprobarse representación adicionalmente. |

## 7. OF-02-04. Resolver registros duplicados

### Propósito y responsable

Regularizar varias representaciones de una misma persona sin perder historia.

Responsable: Personas.

### Información y condiciones previas

Se requieren registros candidatos, evidencias, contradicciones y relaciones afectadas.

La resolución exige preparación y aprobación separadas.

### Desarrollo

- Abrir el caso.

- Comparar identidades y fuentes.

- Solicitar verificación adicional.

- Determinar si corresponden a una misma persona.

- Elegir referencia principal.

- Vincular las referencias anteriores.

- Comunicar a los servicios afectados.

- Seguir regularizaciones pendientes.

### Reglas y decisiones

- Se conserva como principal la referencia con identidad más sólidamente acreditada.

- Si ambas tienen igual acreditación, se conserva la más antigua.

- Una medida o investigación relevante sobre cualquiera de los registros debe considerarse antes de habilitar
  actuaciones.

- Las referencias anteriores siguen siendo resolubles.

- La resolución no suma fondos, deudas o tenencias.

- Los contratos y operaciones mantienen sus referencias históricas.

- Una asociación errónea se rectifica sin borrar la resolución anterior.

### Criterios de aceptación

| Situación                                     | Resultado esperado                                         |
|-----------------------------------------------|------------------------------------------------------------|
| Dos registros corresponden a la misma persona | Quedan vinculados a una referencia principal.              |
| Ambos tienen igual acreditación               | Se conserva la referencia más antigua.                     |
| Cada registro tiene una cuenta                | Las cuentas permanecen separadas; no se trasladan saldos.  |
| La coincidencia resulta falsa                 | Las personas continúan independientes.                     |

## 8. OF-02-05. Registrar circunstancias personales y jurídicas especiales

### Propósito y responsable

Registrar hechos que puedan afectar capacidad, continuidad o facultades.

Responsable: Personas.

### Información y condiciones previas

Se requiere persona, circunstancia, fecha del hecho, fecha de conocimiento, fuente y evidencia.

Comprende minoría de edad, cambios de capacidad, fallecimiento, sucesión, disolución y otras circunstancias
documentadas.

### Desarrollo

- Recibir la información.

- Registrar su procedencia y condición de verificación.

- Solicitar comprobaciones.

- Identificar relaciones potencialmente afectadas.

- Comunicar a Clientes, Representación y responsables pertinentes.

- Solicitar medidas preventivas cuando exista fundamento.

- Seguir confirmaciones.

### Reglas y decisiones

- Una denuncia no comprobada no se presenta como hecho definitivamente verificado.

- Los hechos urgentes se derivan sin esperar a completar la actualización ordinaria.

- La minoría de edad no implica rechazo general: se aplican los productos y actuaciones admitidos.

- El fallecimiento no elimina cuentas, contratos ni deuda.

- Un vínculo familiar no concede facultades sucesorias.

- Las representaciones se revisan según su fundamento, sin presumir que todas continúan o terminan del mismo modo.

- La disolución no extingue automáticamente obligaciones.

- La adquisición de capacidad exige revisar facultades anteriores y condiciones afectadas.

### Criterios de aceptación

| Situación                                          | Resultado esperado                                                        |
|----------------------------------------------------|---------------------------------------------------------------------------|
| Se comunica un fallecimiento todavía no verificado | Se conserva como comunicación pendiente y se evalúan medidas preventivas. |
| Un familiar solicita retirar fondos                | Debe acreditar facultades suficientes.                                    |
| Se verifica una disolución                         | Se revisan relaciones y representación, conservando obligaciones.         |
| Se rectifica el hecho comunicado                   | Se revisan sus consecuencias y medidas, sin levantamientos implícitos.    |

## 9. OF-02-06. Solicitar la vinculación al banco

### Propósito y responsable

Iniciar el trámite para establecer una relación comercial.

Responsable: Vinculación.

### Información y condiciones previas

Se requiere solicitante, propósito, actividad, contactos y representantes cuando intervengan.

La persona debe estar registrada o registrarse durante el trámite.

### Desarrollo

- Identificar solicitante y solicitudes anteriores.

- Registrar la petición.

- Determinar requisitos.

- Entregar el requerimiento inicial con su vencimiento.

- Solicitar verificaciones y antecedentes.

- Registrar presentaciones.

- Gestionar subsanaciones.

### Reglas y decisiones

- Una persona ya cliente se deriva al trámite correspondiente.

- Una readmisión conserva antecedentes de la relación anterior.

- Presentar la solicitud no habilita productos.

- Los plazos se calculan conforme a las reglas comunes del módulo.

- Las presentaciones completas detienen el vencimiento por falta de respuesta.

- El retiro requiere facultades y conserva el expediente.

- Los vencimientos y retiros se comunican sin eliminar datos sujetos a conservación.

### Resultados y excepciones

La solicitud queda en trámite, pendiente de respuesta, retirada o vencida.

Las fallas internas no se convierten en incumplimiento del solicitante.

### Criterios de aceptación

| Situación                                            | Resultado esperado                               |
|------------------------------------------------------|--------------------------------------------------|
| El requerimiento se pone a disposición el 1 de junio | El primer día de cómputo es el 2 de junio.       |
| Se recibe documentación el último día permitido      | No se declara vencimiento mientras se revisa.    |
| Falta una verificación interna                       | No vence por falta de respuesta del solicitante. |
| Se repite la misma petición                          | Se recupera el expediente existente.             |

## 10. OF-02-07. Evaluar y resolver la admisión

### Propósito y responsable

Decidir si corresponde establecer la relación comercial.

Responsable: Vinculación.

### Información y condiciones previas

Se requieren identidad y verificaciones, actividad, propósito, representación y resultados de cumplimiento.

Los requisitos de admisión deben corresponder a capacidades y controles definidos.

### Desarrollo

- Comprobar integridad del expediente.

- Revisar vigencias y observaciones.

- Resolver inconsistencias.

- Preparar una conclusión.

- Obtener decisión de una función facultada.

- Registrar aceptación o rechazo.

- Solicitar constitución a Clientes cuando se acepte.

- Comunicar el resultado permitido.

### Reglas y decisiones

- No se acepta con requisitos obligatorios pendientes.

- Una alerta no constituye rechazo automático.

- Una prohibición efectiva impide la admisión alcanzada.

- Las excepciones solo afectan requisitos internos expresamente exceptuables.

- La aceptación puede utilizarse durante 30 días corridos desde su aprobación, sin superar la vigencia de sus
  antecedentes.

- Los cambios relevantes dentro de ese período exigen revisión.

- La aceptación no se informa como relación constituida hasta confirmar Clientes.

- Los fundamentos internos reservados se distinguen de los motivos comunicables.

### Resultados y excepciones

Se obtiene aceptación, rechazo o revisión pendiente.

Si la constitución es incierta, se consulta antes de repetirla o liberar actuaciones vinculadas.

### Criterios de aceptación

| Situación                                   | Resultado esperado                                            |
|---------------------------------------------|---------------------------------------------------------------|
| Falta una verificación obligatoria          | No se acepta.                                                 |
| Existe una alerta sin conclusión            | Se investiga sin presumir rechazo.                            |
| La aceptación tiene 31 días y no se utilizó | Requiere revisión antes de constituir la relación.            |
| Un antecedente vence antes de los 30 días   | La aceptación no puede utilizarse después de ese vencimiento. |

## 11. OF-02-08. Constituir la relación de cliente

### Propósito y responsable

Establecer la relación comercial a partir de una admisión aceptada.

Responsable: Clientes.

### Información y condiciones previas

Se requiere persona, decisión vigente, alcance y clasificación comercial inicial.

### Desarrollo

- Comprobar admisión.

- Buscar relación existente.

- Asignar o recuperar identificación de cliente.

- Registrar el período de relación.

- Vincular antecedentes.

- Establecer próxima revisión.

- Confirmar a Vinculación.

### Reglas y decisiones

- La constitución es la única actuación que establece la relación de cliente.

- Una persona mantiene una identificación estable.

- Una nueva admisión posterior inicia otro período, sin borrar el anterior.

- Las solicitudes concurrentes no crean relaciones equivalentes duplicadas.

- El segmento comercial no constituye perfil de riesgo.

- Ser cliente no abre cuentas ni concede crédito.

- Una admisión retirada o vencida antes de utilizarse no habilita la constitución.

### Criterios de aceptación

| Situación                           | Resultado esperado                                               |
|-------------------------------------|------------------------------------------------------------------|
| Se repite una admisión ya utilizada | Se devuelve la relación constituida.                             |
| La persona ya es cliente            | Se informa la relación existente.                                |
| Se readmite a un antiguo cliente    | Se conserva su identificación y se abre otro período.            |
| Se constituye la relación           | Se registra la próxima revisión sin exceder el máximo ordinario. |

## 12. OF-02-09. Mantener y revisar la relación comercial

### Propósito y responsable

Mantener actualizada la relación y determinar su continuidad.

Responsable: Clientes.

### Información y condiciones previas

Se requiere cliente, motivo, fecha de revisión, verificaciones y productos relacionados.

### Desarrollo

- Abrir la revisión al alcanzar la fecha o recibir un hecho relevante.

- Solicitar datos y verificaciones.

- Informar al cliente las actualizaciones que deba aportar.

- Evaluar continuidad y clasificación comercial.

- Registrar conclusión.

- Coordinar medidas o trámites derivados.

- Fijar la próxima revisión.

### Reglas y decisiones

- Se informa la revisión periódica 30 días corridos antes de su vencimiento cuando se requiere participación del
  cliente.

- El cliente dispone del plazo de respuesta definido en las reglas comunes.

- Se genera recordatorio cinco días antes del vencimiento de su respuesta.

- La revisión no se completa hasta recibir verificaciones obligatorias.

- Al vencer sin resolución, se registra incumplimiento del plazo y se escala a una función supervisora.

- La supervisión recibe una tarea con vencimiento de cinco días hábiles para decidir acciones o justificar continuidad
  de la investigación.

- La escalación no autoriza a declarar satisfactoria una revisión incompleta.

- Las restricciones requieren decisión y aplicación mediante Restricciones.

- Un nuevo plazo periódico comienza desde la conclusión real, sin ocultar el atraso anterior.

- Los cambios de segmento no alteran contratos automáticamente.

### Criterios de aceptación

| Situación                                     | Resultado esperado                                                                |
|-----------------------------------------------|-----------------------------------------------------------------------------------|
| La última revisión concluyó el 15 de abril    | La próxima no supera el 15 de abril del año siguiente, ajustado según calendario. |
| Riesgo exige una revisión anterior            | Se utiliza esa fecha anterior.                                                    |
| Se vence el plazo sin respuesta               | Se registra y escala; no se bloquean fondos automáticamente.                      |
| El cliente respondió y falta análisis interno | La demora queda atribuida al proceso interno.                                     |

## 13. OF-02-10. Desvincular y readmitir un cliente

### Propósito y responsable

Finalizar una relación comercial o establecer un período posterior.

Responsable: Clientes.

### Información y condiciones previas

La desvinculación identifica causa, actor, facultades, productos, contratos, posiciones y restricciones.

La readmisión exige una nueva resolución de Vinculación.

### Desarrollo de desvinculación

- Registrar la causa.

- Comprobar procedencia y comunicaciones.

- Impedir nuevas contrataciones incompatibles.

- Solicitar inventario a los módulos responsables.

- Coordinar cierres y obligaciones.

- Obtener confirmaciones.

- Registrar el cese comercial y, cuando corresponda, la finalización integral.

- Conservar relaciones residuales y comunicar.

### Reglas y decisiones

- Se distingue el cese de nuevas contrataciones de la resolución de relaciones anteriores.

- Las deudas, activos y obligaciones no desaparecen.

- Las obligaciones pendientes mantienen un responsable aunque cese la oferta de nuevos productos.

- La falta de respuesta de un módulo no demuestra ausencia de relaciones.

- Los reclamos y deberes documentales pueden continuar después.

- Retirar la solicitud de desvinculación no reabre automáticamente productos cerrados.

- La readmisión revisa causas anteriores y medidas vigentes.

- Los productos anteriores solo se recuperan si su responsable determina que siguen vigentes; los cerrados requieren
  nueva contratación.

### Criterios de aceptación

| Situación                | Resultado esperado                                                                    |
|--------------------------|---------------------------------------------------------------------------------------|
| Existe una cuenta activa | Su cierre o continuidad residual recibe tratamiento antes de finalizar integralmente. |
| Hay deuda pendiente      | Se conserva su administración y cobranza.                                             |
| Un módulo no respondió   | La revisión continúa pendiente.                                                       |
| Se readmite al cliente   | Se conserva historia e identificación; no se reabren productos cerrados.              |

## 14. OF-02-11. Registrar y verificar una representación

### Propósito y responsable

Reconocer facultades para actuar en nombre de otra persona.

Responsable: Representación.

### Información y condiciones previas

Se requieren representado, representantes, fundamento, documentos, actos, productos, límites, modalidad y vigencia.

Los participantes deben estar registrados.

### Desarrollo

- Recibir la presentación.

- Comprobar participantes y documentación.

- Solicitar verificaciones.

- Determinar alcance y condiciones.

- Resolver ambigüedades.

- Obtener aprobación separada de la preparación.

- Registrar la representación utilizable.

- Comunicar su resultado.

### Reglas y decisiones

- La recepción del documento no habilita por sí sola.

- El alcance debe poder relacionarse con actos concretos.

- No se interpreta un poder ambiguo como autorización ilimitada.

- Las representaciones sin vencimiento documental no se consideran indefinidas sin control: se verifica su continuidad
  al menos cada 12 meses y ante hechos relevantes.

- Una falta de confirmación de continuidad impide emitir nuevas comprobaciones favorables.

- La sustitución o subdelegación requiere fundamento expreso.

- El representante no adquiere titularidad ni propiedad.

- Las autorizaciones de cuenta no amplían el alcance de la representación.

### Criterios de aceptación

| Situación                               | Resultado esperado                                 |
|-----------------------------------------|----------------------------------------------------|
| Se presenta un poder para consultar     | No habilita transferencias.                        |
| El instrumento exige actuación conjunta | Se registra esa exigencia.                         |
| El alcance es ambiguo                   | Queda pendiente para los actos no comprobados.     |
| No se confirma la continuidad exigida   | No se autoriza una nueva actuación basada en ella. |

## 15. OF-02-12. Modificar, renovar y terminar una representación

### Propósito y responsable

Mantener sus facultades actualizadas y aplicar su finalización.

Responsable: Representación.

### Información y condiciones previas

Se requiere representación, actuación, solicitante, fundamento, evidencia y momento de efecto.

### Desarrollo

- Registrar la actuación.

- Comprobar autoridad y evidencia.

- Identificar facultades y delegaciones afectadas.

- Aplicar el cese válido o revisar la modificación.

- Comunicar los cambios.

- Seguir operaciones pendientes y confirmaciones.

### Reglas y decisiones

- Las ampliaciones y renovaciones requieren verificación y aprobación equivalentes al reconocimiento.

- La continuidad de uso no renueva un poder.

- El vencimiento impide nuevos actos alcanzados.

- Una revocación comprobada se aplica sin esperar la aprobación de otras modificaciones.

- Una comunicación urgente todavía no verificada se deriva a Restricciones para evaluar medidas preventivas.

- Las facultades derivadas no superan el alcance ni vigencia de su fundamento.

- Los cambios de capacidad, fallecimiento o disolución requieren revisar el fundamento concreto.

- Los actos válidamente realizados conservan sus antecedentes.

- Las instrucciones pendientes deben comprobar la representación antes del momento en que sus facultades sean exigibles.

### Criterios de aceptación

| Situación                                               | Resultado esperado                                  |
|---------------------------------------------------------|-----------------------------------------------------|
| Vence la representación                                 | Se impiden nuevas actuaciones que dependan de ella. |
| Se revoca el poder principal                            | Se revisan y limitan las facultades derivadas.      |
| Se amplía el alcance                                    | Se exige nueva evidencia y aprobación.              |
| Se comunica una revocación después de un acto ejecutado | Se determina el efecto temporal sin borrar el acto. |

## 16. OF-02-13. Comprobar facultades de representación

### Propósito y responsable

Determinar si un representante puede realizar una actuación concreta.

Responsable: Representación.

### Información y condiciones previas

Se requiere actor, representado, acto, objeto, producto, importe, moneda, fecha y participantes conjuntos.

### Desarrollo

- Localizar fundamentos aplicables.

- Comprobar vigencia y continuidad.

- Evaluar acto, límites y objeto.

- Comprobar modalidad de actuación.

- Emitir resultado y referencias.

- Informar condiciones adicionales exigibles.

### Reglas y decisiones

- La autenticación no acredita representación.

- El resultado se limita a la actuación evaluada.

- Cambiar importe, destino, producto o contenido relevante exige otra comprobación.

- La actuación conjunta debe referirse al mismo contenido.

- No se combinan poderes incompatibles para construir facultades inexistentes.

- Una restricción sobre el representado o producto se comprueba adicionalmente.

- La comprobación favorable no reserva fondos ni aprueba crédito.

- Ante incertidumbre de alcance o vigencia no se emite resultado favorable.

### Criterios de aceptación

| Situación                                           | Resultado esperado                                                                             |
|-----------------------------------------------------|------------------------------------------------------------------------------------------------|
| Poder limitado a 100 unidades; actuación por 120    | Facultad insuficiente.                                                                         |
| Se requieren dos representantes y participa uno     | No se completa la autorización.                                                                |
| La representación se revoca antes de ejecutar       | Un resultado favorable anterior no permite omitir la revocación aplicable.                     |
| La representación es suficiente, pero no hay fondos | El control de fondos impide el efecto monetario; no cambia la conclusión sobre representación. |

## 17. Correspondencia funcional

| Información o decisión                              | Responsable                                                            |
|-----------------------------------------------------|------------------------------------------------------------------------|
| Identidad y datos declarados                        | Personas                                                               |
| Relaciones personales y societarias declaradas      | Personas                                                               |
| Verificaciones y beneficiarios finales determinados | Conocimiento del cliente                                               |
| Solicitud y decisión de admisión                    | Vinculación                                                            |
| Identificación, períodos y continuidad comercial    | Clientes                                                               |
| Representaciones y alcance                          | Representación                                                         |
| Titularidad y autorizaciones específicas de cuentas | Cuentas                                                                |
| Medidas restrictivas                                | Restricciones y servicios afectados                                    |
| Asignaciones internas y tareas humanas              | Gestión administrativa y administración de acceso, según su naturaleza |
| Evidencias y conservación                           | Documentos y comprobantes                                              |
| Notificaciones y constancias de entrega             | Comunicaciones                                                         |

Las operaciones del módulo no generan movimientos monetarios ni deuda. Sus resultados identifican participantes y
habilitan o condicionan actuaciones cuya ejecución permanece en los módulos responsables.
