# Operaciones funcionales — Módulo 3. Contratación

## 1. Propósito y alcance

Este documento define las operaciones para solicitar productos, preparar propuestas, formalizar acuerdos y administrar
su modificación, renovación y terminación.

Comprende los microservicios Solicitudes de productos y Contratos.

La solicitud, la aprobación especializada, la celebración del contrato y la habilitación operativa son hechos
diferentes. La existencia de uno no demuestra que los demás hayan ocurrido.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-03-01 | Administrar modelos contractuales | Contratos |
| OF-03-02 | Registrar una solicitud de producto | Solicitudes de productos |
| OF-03-03 | Reunir requisitos y resolver la viabilidad | Solicitudes de productos |
| OF-03-04 | Retirar, vencer o rechazar una solicitud | Solicitudes de productos |
| OF-03-05 | Preparar una propuesta contractual | Contratos |
| OF-03-06 | Entregar condiciones y registrar aceptaciones | Contratos |
| OF-03-07 | Celebrar el contrato y determinar sus efectos | Contratos |
| OF-03-08 | Coordinar la habilitación del producto | Solicitudes de productos |
| OF-03-09 | Modificar un contrato | Contratos |
| OF-03-10 | Renovar o prorrogar un contrato | Contratos |
| OF-03-11 | Tramitar el arrepentimiento de una contratación | Contratos |
| OF-03-12 | Terminar un contrato | Contratos |
| OF-03-13 | Consultar y acreditar condiciones contractuales | Contratos |

## 3. Reglas comunes del módulo

### 3.1. Responsabilidades

Solicitudes de productos administra el trámite, reúne decisiones y sigue la constitución del producto.

Los módulos especializados deciden sobre crédito, garantías, admisión operativa y demás condiciones de su competencia.

Contratos conserva el acuerdo, sus participantes, aceptaciones y efectos. No calcula nuevamente cuotas, determina saldos
ni ejecuta movimientos monetarios.

### 3.2. Participantes y facultades

El acuerdo identifica:

- Partes y roles contractuales.

- Obligaciones y derechos de cada parte.

- Personas que intervienen.

- Representaciones utilizadas.

- Aceptaciones individuales o conjuntas.

- Conformidad del banco y su fundamento.

Se comprueban por separado identidad, autenticación, representación y facultades sobre el producto.

Una firma o aceptación no concede facultades que su autor no tenía.

### 3.3. Contenido íntegro

Se conserva el contenido completo aceptado: condiciones generales, particulares, anexos y documentos incorporados.

Las condiciones particulares solo pueden sustituir una condición general cuando el modelo lo admita expresamente. Las
restantes contradicciones impiden celebrar.

Las referencias variables identifican fórmula, fuente, frecuencia y tratamiento de indisponibilidad. Una dirección web
que cambia no sustituye la conservación del contenido contractual.

### 3.4. Preparación y aprobación

Los modelos y sus modificaciones requieren preparación y aprobación separadas.

Los acuerdos individuales pueden formalizarse automáticamente cuando:

- Utilizan un modelo habilitado.

- Respetan las variaciones permitidas.

- Tienen decisiones especializadas vigentes.

- Cumplen todas las aceptaciones exigidas.

- No contienen excepciones pendientes.

Las excepciones requieren aprobación individual y no pueden levantar prohibiciones.

### 3.5. Plazos de las solicitudes

El banco adopta para las solicitudes de productos:

- 30 días corridos para responder el requerimiento inicial.

- 15 días corridos para completar una presentación observada.

- Inicio del cómputo al día siguiente de poner a disposición el requerimiento.

- Traslado al siguiente día hábil si el vencimiento cae en día inhábil.

- Recordatorio cinco días corridos antes del vencimiento.

- Suspensión del vencimiento por falta de respuesta mientras se revisa una presentación recibida a tiempo.

Una omisión del banco que origine nuevos requisitos concede 30 días para esos puntos. Las demoras internas no se imputan
al solicitante.

Si el canal impide responder durante el vencimiento, se extiende hasta el final del segundo día hábil posterior a su
recuperación.

### 3.6. Propuestas y aceptaciones

La propuesta tiene una versión y vencimiento.

La mera solicitud no conserva una oferta. La conservación individual solo existe cuando Condiciones comerciales la
permite y Contratos la registra dentro del plazo autorizado.

Las aceptaciones incompletas vencen con la propuesta. No se trasladan a otra versión.

### 3.7. Fechas y recuperación

Se distinguen entrega, aceptación, celebración, entrada en vigencia, habilitación y terminación.

Los plazos por días hábiles excluyen la fecha inicial y vencen al finalizar el último día hábil. Los plazos por meses
conservan anclaje conforme a Calendarios bancarios.

Una actuación repetida no crea otro contrato. La falta de respuesta exige consulta del resultado original.

### 3.8. Protección contractual adoptada

El banco utiliza un régimen contractual que distingue:

- Relaciones de consumo bancario.

- Financiación y servicios comerciales de empresas.

- Operaciones con contrapartes institucionales.

El encuadre depende de la finalidad y características de la relación, no únicamente de que la persona sea humana o
jurídica.

Para las relaciones alcanzadas, la referencia normativa de protección contempla arrepentimiento dentro de diez días
hábiles desde el último entre recepción del contrato y disponibilidad efectiva, y preaviso de sesenta días corridos para
modificaciones comprendidas en ese régimen. bcra.gob.ar

Como decisión del banco simulado:

- Los contratos bancarios de servicio de cuentas, tarjetas y canales admiten el procedimiento de arrepentimiento
  descrito aquí.

- Los préstamos admiten restitución anticipada por arrepentimiento conforme a la operación OF-03-11.

- Los depósitos a plazo e inversiones no reciben una promesa general de deshacer operaciones ejecutadas: se distingue el
  contrato de servicio de la operación financiera concreta.

- Los acuerdos institucionales se terminan o modifican mediante sus mecanismos expresamente pactados.

- No se permiten aumentos unilaterales de una tasa fija de financiación.

- Los nuevos productos no pueden utilizar un encuadre contractual indeterminado.

---

## 4. OF-03-01. Administrar modelos contractuales

### Propósito y responsable

Definir los acuerdos y mecanismos admitidos para cada producto.

Responsable: Contratos.

### Información y condiciones previas

Se requiere producto, versiones compatibles, participantes, derechos, obligaciones, condiciones económicas, aceptación,
vigencia y terminación.

Cada modelo identifica su encuadre y las operaciones admitidas.

### Desarrollo

- Registrar el modelo.

- Revisar correspondencia con el producto.

- Comprobar contenido funcional y requisitos de protección.

- Definir campos y opciones permitidos.

- Obtener aprobación.

- Establecer vigencia.

- Habilitar y conservar versiones.

### Reglas y decisiones

- Cada campo obligatorio debe tener significado y fuente.

- Las opciones no pueden alterar el objeto del producto.

- Los mecanismos de modificación, renovación y arrepentimiento se incorporan según este documento.

- Los cálculos especializados proceden del módulo del producto.

- Retirar un modelo impide nuevas propuestas.

- Las propuestas existentes solo continúan si el retiro permite respetarlas.

- Las cláusulas que requieren otros documentos incluyen su entrega y conservación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Falta el tratamiento de una tasa variable | El modelo no se habilita. |
| Se aprueba otra versión | Los contratos anteriores permanecen intactos. |
| Una opción excede las capacidades del producto | No se incorpora. |
| Se retira un modelo | No se generan nuevas propuestas con él. |

## 5. OF-03-02. Registrar una solicitud de producto

### Propósito y responsable

Iniciar una contratación o actuación contractual identificable.

Responsable: Solicitudes de productos.

### Información y condiciones previas

Se requiere solicitante, producto, actuación, participantes, canal y antecedentes.

La relación de cliente debe estar constituida antes de celebrar un producto que la requiera. La recepción puede
coordinarse con una vinculación pendiente, sin presentarla como cumplida.

### Desarrollo

- Identificar solicitante y petición.

- Detectar repetición.

- Registrar productos y finalidad.

- Determinar requisitos.

- Entregar requerimiento y vencimiento.

- Iniciar evaluaciones.

### Reglas y decisiones

- Una solicitud puede contener productos relacionados, con seguimiento individual.

- Se identifican dependencias obligatorias y contrataciones independientes.

- La recepción no aprueba financiación ni constituye el producto.

- Los cambios relevantes generan revisión de requisitos.

- Las referencias repetidas con contenido distinto producen inconsistencia.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se solicita cuenta y tarjeta vinculada | Cada producto conserva su resultado y dependencia. |
| Se repite la petición | Se recupera el trámite existente. |
| La vinculación sigue pendiente | No se presume relación de cliente. |
| Se solicita un préstamo | No se genera capital adeudado. |

## 6. OF-03-03. Reunir requisitos y resolver la viabilidad

### Propósito y responsable

Determinar si puede prepararse y celebrarse el acuerdo.

Responsable: Solicitudes de productos.

### Desarrollo

- Reunir datos y documentos.

- Solicitar elegibilidad y verificaciones.

- Obtener decisiones especializadas.

- Resolver observaciones.

- Comprobar compatibilidad y vigencia.

- Clasificar condiciones pendientes.

- Entregar antecedentes a Contratos.

### Reglas y decisiones

Los requisitos se asignan a una etapa:

- Antes de celebrar.

- Antes de la entrada en vigencia.

- Antes de habilitar o desembolsar.

No pueden diferirse requisitos obligatorios de una etapa anterior.

Una aprobación condicionada debe señalar qué actuación impide cada condición.

Las evaluaciones vencidas se renuevan. Los cambios de importe, moneda, participantes o modalidad revisan las decisiones
afectadas.

Los compromisos de exposición conservan su referencia en Riesgos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Falta identidad obligatoria | No se celebra. |
| Existe garantía pendiente exigida antes del desembolso | Puede formalizarse si las demás condiciones lo permiten, sin desembolsar. |
| Cambia el capital solicitado | Se revisan crédito, oferta y exposición. |
| Falta respuesta especializada | El requisito permanece pendiente. |

## 7. OF-03-04. Retirar, vencer o rechazar una solicitud

### Propósito y responsable

Concluir un trámite que no producirá la contratación prevista.

Responsable: Solicitudes de productos.

### Desarrollo

- Registrar causa y fecha.

- Comprobar si existe celebración o ejecución incierta.

- Impedir nuevas actuaciones incompatibles.

- Cancelar tareas innecesarias.

- Solicitar regularización de compromisos.

- Obtener confirmaciones.

- Comunicar y conservar antecedentes.

### Reglas y decisiones

- Antes de celebrar se admite el retiro.

- Después de celebrar corresponde arrepentimiento o terminación.

- El vencimiento por falta de respuesta no es rechazo comercial.

- La reapertura conserva el expediente, pero revisa requisitos y aprobaciones actuales.

- No se libera exposición utilizada o incierta.

- Un rechazo conserva fundamento y motivos comunicables.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se responde dentro del plazo | No vence mientras se revisa la presentación. |
| Existe contrato celebrado | No se cierra como simple retiro. |
| Hay constitución incierta | Se consulta antes de liberar compromisos. |
| Se reabre el trámite | No se recuperan automáticamente aprobaciones vencidas. |

## 8. OF-03-05. Preparar una propuesta contractual

### Propósito y responsable

Producir el contenido concreto que las partes podrán aceptar.

Responsable: Contratos.

### Desarrollo

- Seleccionar modelo.

- Incorporar participantes y roles.

- Obtener condiciones comerciales.

- Incorporar decisiones, cronogramas y anexos.

- Resolver contradicciones.

- Determinar vencimiento.

- Conservar la versión completa.

### Reglas y decisiones

- No se exceden límites ni aprobaciones.

- El vencimiento no supera la menor vigencia de sus antecedentes.

- Sin conservación comercial autorizada, las condiciones se comprueban nuevamente antes de celebrar.

- Una propuesta de tasa variable conserva fórmula y referencia.

- Las cantidades indicativas se identifican como tales.

- Las condiciones esenciales no quedan en blanco.

- Un cambio sustancial produce otra versión y exige nuevas aceptaciones.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Crédito aprobado por 100 y propuesta por 120 | Se impide emitirla. |
| La aprobación vence antes que la oferta | La propuesta vence antes. |
| Se cambia el plazo | Se recalculan condiciones y se revisan aprobaciones. |
| Se entrega un cronograma | Corresponde al capital y tasa propuestos. |

## 9. OF-03-06. Entregar condiciones y registrar aceptaciones

### Propósito y responsable

Acreditar qué recibió y aceptó cada participante.

Responsable: Contratos.

### Desarrollo

- Entregar el conjunto documental.

- Comprobar disponibilidad accesible.

- Identificar al participante.

- Comprobar facultades.

- Obtener aceptación sobre la versión exacta.

- Conservar evidencia.

- Informar completitud.

### Reglas y decisiones

- El banco exige entregar el contenido íntegro antes de aceptar.

- La aceptación debe ser una acción afirmativa y verificable.

- El silencio no celebra un nuevo contrato.

- Las aceptaciones conjuntas corresponden al mismo contenido.

- La conformidad puede retirarse antes de celebrar.

- El retiro queda registrado antes de comprobar la celebración.

- Una aceptación perdida se consulta; no se presume inexistente.

- La copia contractual final se entrega al celebrarse. Una falla de entrega genera recuperación y no se presenta como
  recepción exitosa.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Falta un anexo incorporado | No se completa la aceptación informada. |
| Dos participantes aceptan versiones diferentes | No se celebra. |
| Se retira una aceptación antes de celebrar | Se impide utilizarla. |
| Se repite la aceptación | No se genera otro consentimiento. |

## 10. OF-03-07. Celebrar el contrato y determinar sus efectos

### Propósito y responsable

Confirmar que existe acuerdo y establecer cuándo produce efectos.

Responsable: Contratos.

### Desarrollo

- Comprobar propuesta y vigencias.

- Revisar aceptaciones y facultades.

- Comprobar conformidad del banco.

- Aplicar restricciones pertinentes.

- Registrar celebración.

- Determinar efecto inmediato o condicionado.

- Entregar copia y comunicar a los responsables.

### Reglas y decisiones

- La celebración ocurre cuando están reunidas todas las conformidades y requisitos exigibles.

- La decisión se ordena con retiros y revocaciones concurrentes para no utilizar una conformidad que ya había cesado.

- Las condiciones suspensivas identifican hecho, responsable y consecuencia de incumplimiento.

- El contrato distingue efecto jurídico de habilitación operativa.

- Una confirmación repetida conserva una única identidad.

- La aprobación retirada antes de celebrar impide continuar.

- Si ya se celebró, se utiliza el procedimiento contractual correspondiente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Falta aceptación de un cotitular requerido | No se celebra. |
| Están todas las conformidades | Se registra una celebración identificable. |
| Existe condición previa al desembolso | No se informa dinero entregado. |
| Se pierde la respuesta | Se recupera el mismo contrato. |

## 11. OF-03-08. Coordinar la habilitación del producto

### Propósito y responsable

Comprobar que el producto quedó constituido conforme al acuerdo.

Responsable: Solicitudes de productos.

### Desarrollo

- Recibir celebración.

- Comprobar condiciones de efecto y operación.

- Solicitar actuaciones pendientes.

- Solicitar constitución al módulo responsable.

- Obtener identificación y resultado.

- Comparar con el contrato.

- Confirmar y comunicar.

### Reglas y decisiones

- El responsable del producto ejecuta su constitución.

- Cada instrucción referencia contrato y actuación.

- Un producto no se duplica por repetición.

- Una falla operativa no elimina el contrato.

- Las diferencias impiden informar habilitación completa.

- Si no puede cumplirse el acuerdo, se abre tratamiento contractual y de reparación, conservando sus obligaciones.

- La exposición se transforma mediante Riesgos y el producto.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| La cuenta ya se constituyó | Se recupera su identificación. |
| Falla la emisión de tarjeta | El contrato conserva seguimiento. |
| El producto tiene otra moneda | Se trata como inconsistencia antes de habilitar. |
| Hay garantía pendiente | No se ejecuta la actuación condicionada. |

## 12. OF-03-09. Modificar un contrato

### Propósito y responsable

Cambiar condiciones con fundamento y fecha determinados.

Responsable: Contratos.

### Modalidades adoptadas

| Modalidad | Tratamiento |
| --- | --- |
| Cambio bilateral | Requiere aceptación expresa de las partes afectadas. |
| Actualización por fórmula pactada | Aplica el mecanismo existente y conserva el cálculo y referencia. |
| Cambio comercial unilateral permitido | Exige fundamento contractual, revisión de cumplimiento y preaviso. |
| Mejora económica sin contrapartida | Puede aplicarse desde su confirmación, con comunicación y sin retirar otros derechos. |
| Cambio obligatorio por disposición aplicable | Se registra alcance, fundamento y fecha exigida, con revisión de cumplimiento. |

### Desarrollo

- Registrar propuesta y versión de origen.

- Clasificar modalidad.

- Evaluar posiciones y operaciones afectadas.

- Obtener aprobaciones y aceptaciones.

- Realizar comunicaciones.

- Determinar efecto.

- Coordinar aplicación operativa.

- Confirmar correspondencia.

### Reglas y decisiones

- El banco adopta 60 días corridos de preaviso para cambios unilaterales desfavorables permitidos en sus servicios
  continuados.

- El plazo comienza al día siguiente de la notificación acreditada; el efecto no ocurre antes del inicio del día
  posterior al día 60.

- El aviso informa condiciones anteriores, nuevas, fecha y posibilidad de terminar sin cargo de salida por ese cambio.

- Una notificación no acreditada impide aplicar la modificación al contrato afectado.

- El preaviso no habilita cambios prohibidos ni sustituye aceptación cuando sea necesaria.

- Una tasa fija de préstamo no aumenta unilateralmente.

- No se aplica retroactividad desfavorable.

- Un cambio bilateral pendiente conserva las condiciones anteriores.

- Las disposiciones obligatorias utilizan su fecha exigida, sin fingir que hubo consentimiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se aumenta una comisión sin notificación acreditada | No se aplica al contrato. |
| Se propone aumentar una tasa fija | Requiere un acuerdo válido; no basta el preaviso. |
| Se actualiza un índice pactado | Se conserva fórmula, dato y efecto. |
| El cliente rechaza un cambio unilateral y solicita terminar | Se tramita sin cargo de salida por ese cambio, conservando deuda legítima. |

## 13. OF-03-10. Renovar o prorrogar un contrato

### Propósito y responsable

Extender una relación o establecer un período posterior.

Responsable: Contratos.

### Desarrollo

- Identificar vencimiento o petición.

- Comprobar mecanismo pactado.

- Revisar requisitos y restricciones.

- Determinar condiciones del período nuevo.

- Obtener aceptaciones o avisos.

- Registrar y coordinar con el producto.

### Reglas y decisiones

- La renovación automática requiere pacto expreso.

- Sin ese pacto, la falta de respuesta no renueva.

- Una instrucción de no renovar recibida antes del vencimiento impide la renovación todavía no ejecutada.

- El banco recibe esa instrucción sin imponer una ventana comercial que haga imposible ejercerla.

- Las nuevas condiciones desfavorables respetan aceptación o preaviso según su naturaleza.

- Renovar un contrato de servicio no renueva automáticamente cada operación financiera.

- La renovación de financiación requiere la revisión crediticia correspondiente.

- La exposición sustituida no se duplica.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| No existe cláusula de renovación | Se exige nuevo acuerdo. |
| Se pide no renovar antes del vencimiento | No se ejecuta la renovación pendiente. |
| Cambian condiciones que requieren aceptación | No se aplican por silencio. |
| Vence un préstamo impago | La deuda permanece; no se renueva ficticiamente. |

## 14. OF-03-11. Tramitar el arrepentimiento de una contratación

### Propósito y responsable

Dejar sin efecto una contratación dentro del régimen de arrepentimiento admitido.

Responsable: Contratos.

### Alcance y cómputo

Para los servicios bancarios comprendidos se aplica un plazo de diez días hábiles, calculado desde el día siguiente al
último entre recepción del contrato y disponibilidad efectiva del producto. El uso puede originar conceptos
proporcionales previstos, pero no convierte automáticamente el arrepentimiento en improcedente. bcra.gob.ar

El banco adopta estos tratamientos:

| Producto o relación | Consecuencia |
| --- | --- |
| Cuenta o servicio continuado | Se detienen nuevos usos incompatibles y se coordina su terminación. |
| Tarjeta | Se impiden nuevos usos; consumos y obligaciones existentes reciben tratamiento individual. |
| Préstamo no desembolsado | Se cancela la entrega pendiente y se regularizan compromisos. |
| Préstamo desembolsado | Se liquida restitución del capital y remuneración por utilización efectiva, sin comisión de precancelación por arrepentimiento. |
| Depósito a plazo | Antes de constituirse puede retirarse la instrucción; después se aplica su régimen de precancelación, sin prometer disponibilidad inexistente. |
| Servicio de inversión | Puede terminarse el servicio; las órdenes ejecutadas y tenencias se liquidan o trasladan mediante sus operaciones. |

Las operaciones de mercado ejecutadas no se deshacen al precio original por arrepentirse del servicio de inversión.

### Desarrollo

- Registrar fecha, canal y contenido de la petición.

- Comprobar facultades y plazo.

- Identificar contrato y operaciones.

- Detener nuevos usos incompatibles.

- Determinar restituciones y obligaciones.

- Ejecutar regularizaciones con sus responsables.

- Confirmar efectos y comunicar.

### Reglas y decisiones

- Se admite solicitud por el mismo canal de contratación y por canales de atención habilitados.

- Las demoras internas no cambian la fecha de ejercicio.

- Si falta evidencia de entrega o disponibilidad, no se presume vencido el derecho por esa ausencia.

- El banco comunica la liquidación e instrucciones dentro de dos días hábiles desde la solicitud.

- Para préstamos desembolsados concede diez días hábiles desde esa comunicación para restituir los importes liquidados,
  actualizando únicamente la utilización efectiva hasta el pago.

- El incumplimiento de restitución mantiene la obligación y su seguimiento; no genera una comisión de salida inventada.

- Fuera del plazo se ofrece terminación o precancelación ordinaria, sin rechazar automáticamente la petición completa.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Contrato recibido después de habilitar el producto | El cómputo usa la recepción posterior. |
| Solicitud recibida el décimo hábil | Se considera presentada a tiempo. |
| Préstamo todavía no desembolsado | Se impide la entrega pendiente. |
| Existen consumos de tarjeta | Se bloquean nuevos usos y se tratan las obligaciones; no se borran consumos. |
| Existe inversión ya ejecutada | No se promete devolver el precio original ignorando el mercado. |

## 15. OF-03-12. Terminar un contrato

### Propósito y responsable

Finalizar el acuerdo y determinar obligaciones subsistentes.

Responsable: Contratos.

### Información y condiciones previas

Se requiere causa, solicitante, facultades, posiciones, instrumentos, compromisos y restricciones.

### Desarrollo

- Registrar la petición sin exigir cancelación previa de todos los pendientes.

- Determinar procedencia y fecha.

- Impedir nuevas prestaciones incompatibles.

- Consultar a los responsables.

- Coordinar cierre y tratamiento residual.

- Confirmar terminación o pendientes.

- Comunicar y conservar.

### Reglas y decisiones

- En servicios continuados, la petición del cliente produce cese de nuevas prestaciones solicitadas desde su recepción
  comprobada, salvo lo necesario para resolver obligaciones.

- Los cargos por servicios futuros cesan según la fecha efectiva; no se prolongan por demoras internas.

- La deuda existente no impide recibir la baja ni desaparece por ella.

- Un préstamo se cancela económicamente mediante pago, acuerdo u otra causa extintiva válida.

- La decisión del banco de terminar una relación requiere fundamento y revisión de cumplimiento; no utiliza como
  sustituto un cierre técnico arbitrario.

- Cuentas coordina el cierre de cuentas.

- Las garantías continúan mientras su fundamento lo requiera.

- Los saldos a favor mantienen un destino y responsable.

- El banco informa dentro de dos días hábiles la ejecución realizada o el detalle de pendientes y sus responsables.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se solicita baja con deuda de tarjeta | Se recibe la baja y se trata la deuda separadamente. |
| Hay saldo a favor | No se elimina ni se convierte en ingreso. |
| Se demora una tarea interna | No se generan servicios futuros ficticios para cobrar mantenimiento. |
| Se cancela una obligación con garantía compartida | Se revisan las otras obligaciones antes de liberar. |

## 16. OF-03-13. Consultar y acreditar condiciones contractuales

### Propósito y responsable

Proporcionar el contenido aplicable y justificar su historia.

Responsable: Contratos.

### Desarrollo

- Comprobar facultades y finalidad.

- Identificar contrato y fecha.

- Reunir versiones y modificaciones.

- Relacionar aceptaciones y entregas.

- Proporcionar consulta o solicitar constancia documental.

### Reglas y decisiones

- Las consultas históricas utilizan condiciones de esa fecha.

- Las futuras se identifican separadamente.

- Una propuesta no celebrada no se informa como contrato.

- Los valores calculados por productos conservan su responsable.

- La copia no crea ni modifica derechos.

- Los terceros acceden únicamente con fundamento suficiente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se consulta una fecha anterior a una modificación | Se obtiene la versión anterior. |
| La propuesta nunca fue aceptada | Se informa como propuesta, no como contrato. |
| Un tercero carece de facultades | Se impide el acceso. |
| Se emite otra copia | Se conserva el mismo acuerdo. |

## 17. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Solicitudes y seguimiento de requisitos | Solicitudes de productos |
| Evaluaciones especializadas | Módulo competente |
| Oferta y conservación comercial | Condiciones comerciales |
| Modelos, propuestas, acuerdos y aceptaciones | Contratos |
| Identidad y relación comercial | Personas y Clientes |
| Representación | Representación |
| Facultades internas | Institución |
| Constitución y ejecución del producto | Módulo responsable |
| Exposición compartida | Riesgos |
| Evidencias documentales | Documentos y comprobantes |
| Entrega y notificación | Comunicaciones |
| Controles sobre requisitos de protección | Riesgo y cumplimiento |

Las referencias normativas se conservan con su versión en los controles de cumplimiento. Las decisiones de servicio
adoptadas por el banco simulado se mantienen diferenciadas de esas exigencias, sin reducir los derechos obligatorios
aplicables.
