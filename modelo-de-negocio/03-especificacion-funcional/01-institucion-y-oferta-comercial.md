# Operaciones funcionales — Módulo 1. Institución y oferta comercial

## 1. Propósito y alcance

Este documento define las operaciones mediante las cuales el banco administra:

- Su identidad, organización y facultades internas.

- Los productos y servicios que ofrece.

- Las condiciones comerciales de sus propuestas.

- Los calendarios y las convenciones temporales utilizados por el negocio.

Comprende los microservicios Institución, Catálogo de productos, Condiciones comerciales y Calendarios bancarios.

Las reglas aquí establecidas corresponden al funcionamiento adoptado para el banco simulado. Los parámetros comerciales
pueden configurarse dentro de esas reglas, sin cambiar su significado ni habilitar capacidades inexistentes.

Las operaciones del módulo no constituyen productos de clientes, no modifican acuerdos individuales ni producen
movimientos monetarios por sí mismas.

## 2. Catálogo de operaciones

| Identificador | Operación                                            | Responsable             |
|---------------|------------------------------------------------------|-------------------------|
| OF-01-01      | Registrar y modificar la definición institucional    | Institución             |
| OF-01-02      | Administrar unidades organizativas y sucursales      | Institución             |
| OF-01-03      | Otorgar, modificar y revocar facultades internas     | Institución             |
| OF-01-04      | Comprobar una facultad institucional                 | Institución             |
| OF-01-05      | Definir y versionar productos                        | Catálogo de productos   |
| OF-01-06      | Habilitar, suspender y retirar productos             | Catálogo de productos   |
| OF-01-07      | Comprobar elegibilidad y compatibilidad de productos | Catálogo de productos   |
| OF-01-08      | Definir y aprobar condiciones comerciales            | Condiciones comerciales |
| OF-01-09      | Publicar, sustituir y retirar ofertas comerciales    | Condiciones comerciales |
| OF-01-10      | Determinar las condiciones comerciales aplicables    | Condiciones comerciales |
| OF-01-11      | Administrar calendarios y convenciones temporales    | Calendarios bancarios   |
| OF-01-12      | Determinar fechas bancarias y series de vencimientos | Calendarios bancarios   |

## 3. Reglas comunes del módulo

### 3.1. Preparación y aprobación

Los cambios de identidad, estructura, facultades, productos, condiciones comerciales y calendarios requieren preparación
y aprobación por personas diferentes.

Institución determina las facultades necesarias. Gestión administrativa coordina las intervenciones humanas. El
microservicio responsable conserva el contenido y aplica la decisión.

La aprobación identifica:

- Objeto y versión exactos.

- Fundamento.

- Preparador y aprobador.

- Fecha de aprobación.

- Momento de efecto.

- Dependencias y condiciones previas.

Modificar el contenido aprobado exige una nueva aprobación.

Las consultas y determinaciones que aplican reglas vigentes no requieren otra aprobación humana, salvo que detecten una
excepción.

### 3.2. Constitución inicial

El banco comienza con un acto de constitución de la simulación que identifica institución, funciones iniciales, personas
designadas, facultades y fecha de efecto.

Deben existir al menos dos personas distintas para preparar y aprobar los cambios posteriores.

El acto inicial se conserva como fundamento de autoridad. No permite omitir después los procedimientos ordinarios.

### 3.3. Versiones y vigencias

Cada objeto mantiene una identificación estable y versiones identificables.

Se distinguen preparación, aprobación, publicación, entrada en vigencia y finalización.

Los períodos incluyen su instante inicial y excluyen el final. Dos versiones incompatibles del mismo objeto no pueden
estar efectivas para un mismo alcance y momento.

Las ofertas comerciales diferentes pueden coexistir cuando sus requisitos y prioridades permiten resolver cuál
corresponde.

La referencia temporal institucional es America/Argentina/Buenos_Aires. Los mecanismos que utilicen otra zona deben
identificarla expresamente.

### 3.4. Cambios simultáneos

Toda modificación identifica la versión de origen.

Si esa versión cambia antes de aplicar la modificación:

- Se impide reemplazarla automáticamente.

- Se comparan las diferencias.

- Se prepara una versión compatible.

- Se obtiene una nueva aprobación.

No se considera válida una aprobación sobre un contenido diferente del revisado.

### 3.5. Aplicación, retiro y recuperación

Antes de aplicar una decisión se comprueba que conserve su aprobación, fundamento y requisitos.

Una aprobación retirada antes de su aplicación impide ejecutarla. Si el cambio ya fue aplicado, su reversión requiere
otra actuación identificada.

La repetición de una solicitud conserva el resultado original. Una misma referencia con distinto contenido se trata como
inconsistencia.

Una aplicación parcial identifica lo realizado y lo pendiente. Los productos y ofertas no admiten nuevas contrataciones
mientras falte una dependencia obligatoria.

Las comunicaciones pendientes mantienen seguimiento. Los consumidores comprueban la definición vigente antes de
utilizarla; recibir una comunicación anterior no garantiza que continúe vigente.

### 3.6. Efectos sobre contratos existentes

Las modificaciones del catálogo y de la oferta no cambian automáticamente contratos celebrados.

Si un cambio también debe afectar operaciones existentes, se coordina con Contratos, Restricciones y los módulos
responsables.

Los hechos anteriores conservan las definiciones utilizadas. Las correcciones no reescriben su historia.

---

## 4. OF-01-01. Registrar y modificar la definición institucional

### Propósito y responsable

Establecer la identidad del banco y el alcance de sus actividades.

Responsable: Institución.

### Información y condiciones previas

La propuesta identifica:

- Denominaciones e identificadores institucionales.

- Domicilios.

- Actividades comprendidas.

- Fundamento.

- Fecha de efecto.

- Diferencias respecto de la versión vigente.

- Productos y procesos afectados.

La constitución requiere el acto inicial. Las modificaciones requieren facultades vigentes y revisión de cumplimiento.

### Desarrollo

- Registrar la propuesta y sus antecedentes.

- Comprobar integridad y compatibilidad con la institución.

- Revisar las actividades solicitadas y sus restricciones.

- Identificar consecuencias sobre productos y procesos.

- Determinar las actuaciones de continuidad o cese.

- Obtener aprobación.

- Aplicar la versión.

- Comunicarla y seguir sus actuaciones dependientes.

### Reglas y configuración

- Cambiar nombre o domicilio no crea otra institución.

- Ampliar el alcance no habilita automáticamente productos.

- Reducir el alcance impide nuevas actividades comprendidas desde su efecto.

- Las obligaciones anteriores continúan administrándose.

- El cese urgente se hace efectivo mediante medidas de Restricciones y actuaciones de los responsables operativos.

- Una fecha futura puede programarse; no permite uso anticipado.

- No se admiten modificaciones retroactivas para justificar actividades que carecían de habilitación.

### Resultados y excepciones

La propuesta queda aplicada, programada, pendiente o rechazada.

Si una reducción requiere actuaciones en varios módulos, Institución conserva su seguimiento sin informar que todas
terminaron antes de recibir confirmaciones.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se modifica la denominación | Se conserva la identidad y la historia institucional. |
| Se aprueba una actividad nueva | Deben habilitarse por separado sus productos. |
| Se retira una actividad con contratos vigentes | Se impiden nuevas actividades alcanzadas y se conservan las obligaciones anteriores. |
| Se repite la misma solicitud | Se recupera su resultado sin crear otra versión involuntaria. |

## 5. OF-01-02. Administrar unidades organizativas y sucursales

### Propósito y responsable

Crear, modificar, reorganizar y dar de baja unidades y sucursales.

Responsable: Institución.

### Información y condiciones previas

Se requiere identificación, tipo, ubicación, funciones, dependencia, vigencia y responsables de continuidad.

La unidad superior debe existir. La estructura no puede contener ciclos.

### Desarrollo

- Registrar el cambio.

- Comprobar estructura y funciones.

- Identificar tareas, facultades y recursos afectados.

- Determinar las reasignaciones necesarias.

- Obtener aprobación.

- Aplicar el cambio.

- Obtener confirmaciones de continuidad.

### Reglas y configuración

- Crear una unidad no concede facultades automáticamente.

- Una reorganización no cambia referencias históricas.

- La baja impide nuevas asignaciones desde su efecto.

- Las tareas pendientes mantienen un responsable.

- Institución no traslada efectivo, cuentas ni contratos: coordina con sus responsables.

- Si una unidad pierde su dependencia superior, debe recibir otra válida antes de completar la reorganización.

### Resultados y excepciones

La modificación organizativa puede quedar aplicada con actuaciones dependientes pendientes, que deben identificarse.

La baja no se informa concluida mientras existan responsabilidades sin tratamiento.

### Criterios de aceptación

| Situación                                  | Resultado esperado                                           |
|--------------------------------------------|--------------------------------------------------------------|
| Una unidad pretende depender de sí misma   | Se rechaza la modificación.                                  |
| Se cierra una sucursal con tareas abiertas | Se reasignan antes de concluir la baja.                      |
| Se traslada efectivo                       | Tesorería confirma el movimiento; Institución no lo presume. |
| Cambia la dependencia de una unidad        | Las operaciones anteriores conservan su referencia original. |

## 6. OF-01-03. Otorgar, modificar y revocar facultades internas

### Propósito y responsable

Determinar qué decisiones puede adoptar una función institucional.

Responsable: Institución.

### Información y condiciones previas

Se identifican otorgante, función receptora, decisiones, ámbito, productos, límites, monedas, vigencia y modalidad
individual o conjunta.

El otorgante debe poseer facultades suficientes y la función receptora debe estar habilitada.

### Desarrollo

- Registrar la propuesta.

- Comprobar autoridad del otorgante.

- Revisar alcance y límites.

- Comprobar incompatibilidades y cadena de delegación.

- Obtener aprobación.

- Aplicar la actuación.

- Comunicar consecuencias sobre decisiones y delegaciones dependientes.

### Reglas y configuración

- No se delegan facultades superiores a las propias.

- La subdelegación requiere autorización expresa.

- No se admiten cadenas circulares.

- El vencimiento o revocación del fundamento impide utilizar delegaciones dependientes.

- Las decisiones conjuntas identifican cantidad y funciones requeridas.

- Los límites monetarios se establecen por moneda.

- Una decisión en otra moneda requiere una regla de equivalencia y cotización expresamente identificadas.

- En ausencia de equivalencia aprobada, la facultad no cubre esa moneda.

- Las facultades institucionales no sustituyen permisos técnicos ni derechos sobre productos de clientes.

### Resultados y excepciones

Se confirma otorgamiento, modificación, revocación o impedimento.

Una revocación no invalida automáticamente decisiones anteriores válidas. Las decisiones pendientes comprueban las
facultades al momento de aprobar.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Un otorgante con límite de 100 unidades intenta delegar 150 | Se rechaza. |
| Una delegación vence antes de aprobar una operación | No puede utilizarse. |
| Se exige aprobación de dos funciones | Una sola intervención no resulta suficiente. |
| Se revoca el fundamento de una subdelegación | La subdelegación deja de habilitar nuevas decisiones. |

## 7. OF-01-04. Comprobar una facultad institucional

### Propósito y responsable

Determinar si un actor puede adoptar una decisión concreta.

Responsable: Institución.

### Información y condiciones previas

Se requiere actor autenticado, función y adscripción acreditadas, decisión, objeto, importe, moneda y momento de
actuación.

### Desarrollo

- Comprobar función y adscripción.

- Localizar facultades aplicables.

- Evaluar vigencia y cadena de delegación.

- Comprobar límites e incompatibilidades.

- Determinar intervenciones conjuntas.

- Emitir el resultado y sus fundamentos.

### Reglas

- El nombre de un cargo no demuestra autoridad.

- El resultado se limita al objeto y condiciones evaluados.

- Cambiar importe, moneda, producto o participantes exige una nueva comprobación.

- Las facultades deben continuar vigentes al adoptar la decisión.

- La falta de datos o respuesta produce una comprobación pendiente.

### Resultados y criterios de aceptación

| Situación                                                    | Resultado esperado                                   |
|--------------------------------------------------------------|------------------------------------------------------|
| Importe dentro del límite y alcance                          | Facultad suficiente.                                 |
| Importe superior al límite                                   | Facultad insuficiente; requiere otra autoridad.      |
| Adscripción no comprobada                                    | Resultado pendiente, sin autorización favorable.     |
| Facultad revocada después de una consulta y antes de decidir | La decisión no puede utilizar el resultado anterior. |

## 8. OF-01-05. Definir y versionar productos

### Propósito y responsable

Establecer las características y comportamientos admitidos por cada producto.

Responsable: Catálogo de productos.

### Información y condiciones previas

La definición incluye familia, variante, monedas, participantes, operaciones, elegibilidad, dependencias,
compatibilidades, contratación y terminación.

También identifica capacidades operativas, condiciones comerciales necesarias y responsables.

### Desarrollo

- Registrar la definición.

- Comparar con versiones anteriores.

- Revisar contratación, operación, controles y consecuencias contables.

- Comprobar que cada requisito tenga un responsable.

- Identificar combinaciones válidas.

- Obtener aprobación.

- Conservar la versión para su habilitación.

### Reglas y configuración

- El producto mantiene identidad estable.

- Los cambios funcionales generan una versión.

- Cambiar un valor comercial dentro de capacidades existentes no exige redefinir el producto.

- Cada modalidad admitida debe tener comportamiento funcional especificado.

- Una característica nueva no migra contratos automáticamente.

- La migración de relaciones existentes requiere una actuación contractual y operativa propia.

- No se aprueban requisitos imposibles de comprobar ni capacidades sin responsable.

- La aprobación de una definición no demuestra disponibilidad operativa.

### Resultados y excepciones

Se obtiene definición aprobada, pendiente o rechazada.

Las contradicciones entre producto, contratación y operación se resuelven antes de aprobar.

### Criterios de aceptación

| Situación                                    | Resultado esperado                                     |
|----------------------------------------------|--------------------------------------------------------|
| Cambia una comisión admitida por el producto | Se versiona la condición comercial.                    |
| Se incorpora otro sistema de amortización    | Se define su comportamiento y se versiona el producto. |
| Existe un requisito sin responsable          | No se aprueba la definición.                           |
| Se aprueba una versión nueva                 | Los contratos anteriores conservan sus condiciones.    |

## 9. OF-01-06. Habilitar, suspender y retirar productos

### Propósito y responsable

Determinar qué versiones pueden utilizarse para nuevas contrataciones.

Responsable: Catálogo de productos.

### Información y condiciones previas

La actuación identifica producto, versión, alcance, fundamento y momento de efecto.

La habilitación requiere confirmación de:

- Definición aprobada.

- Condiciones comerciales utilizables.

- Modelos contractuales aprobados.

- Capacidades operativas disponibles.

- Controles y reglas contables necesarios.

- Documentación y comunicaciones preparadas.

### Desarrollo

- Registrar la actuación.

- Comprobar dependencias.

- Identificar ofertas y solicitudes afectadas.

- Obtener aprobación.

- Coordinar la entrada en vigencia.

- Confirmar habilitación, suspensión o retiro.

- Seguir el tratamiento de solicitudes pendientes.

### Reglas y configuración

- Producto y oferta pueden prepararse conjuntamente; contratar exige que ambos estén efectivos.

- La suspensión impide nuevas contrataciones y permite reanudación.

- La reanudación comprueba nuevamente todas las dependencias.

- El retiro finaliza la comercialización de la versión. Su reintroducción requiere nueva aprobación.

- Una propuesta individual previa solo continúa si está vigente y la decisión de suspensión o retiro permite respetarla.

- Una prohibición aplicable impide continuar aunque exista propuesta.

- La operación de contratos existentes solo se limita mediante decisiones específicas.

- Una dependencia incompleta impide informar habilitación efectiva.

### Criterios de aceptación

| Situación                                        | Resultado esperado                                       |
|--------------------------------------------------|----------------------------------------------------------|
| Falta capacidad para ejecutar el producto        | No se habilita.                                          |
| La oferta entra en vigencia después del producto | No se contrata hasta que ambas estén efectivas.          |
| Se retira una versión                            | No se cierran automáticamente productos existentes.      |
| Se reanuda un producto con tarifas vencidas      | Deben publicarse condiciones válidas antes de contratar. |

## 10. OF-01-07. Comprobar elegibilidad y compatibilidad de productos

### Propósito y responsable

Determinar si participantes y relaciones cumplen los requisitos del catálogo.

Responsable: Catálogo de productos.

### Información y condiciones previas

Se requiere producto, versión, participantes, antecedentes verificados, productos relacionados y fecha.

### Desarrollo

- Obtener requisitos.

- Comprobar suficiencia de antecedentes.

- Evaluar cada condición.

- Identificar dependencias e incompatibilidades.

- Emitir resultado y fundamento.

### Reglas

- La comprobación aplica requisitos del catálogo; no sustituye admisión, crédito o cumplimiento.

- Un dato faltante no se presume favorable.

- La dependencia identifica la relación concreta que la satisface.

- Los cambios relevantes de datos o requisitos exigen reevaluación.

- No se consultan ni exponen datos personales ajenos a la finalidad.

### Resultados y criterios de aceptación

| Situación                                                        | Resultado esperado                                                         |
|------------------------------------------------------------------|----------------------------------------------------------------------------|
| Cumple requisitos del catálogo, pero falta aprobación crediticia | Elegibilidad favorable, sin autorización de crédito.                       |
| Falta un producto requerido                                      | Incumplimiento o pendiente de constitución, según la dependencia definida. |
| Falta un antecedente obligatorio                                 | Resultado pendiente.                                                       |
| Cambia una condición relevante antes de contratar                | Nueva comprobación.                                                        |

## 11. OF-01-08. Definir y aprobar condiciones comerciales

### Propósito y responsable

Establecer tasas, cargos, bonificaciones y límites de la oferta.

Responsable: Condiciones comerciales.

### Información y condiciones previas

Cada condición identifica:

- Producto y versión compatible.

- Concepto y hecho generador.

- Moneda y unidad.

- Valor, fórmula o referencia.

- Base y periodicidad.

- Precisión y redondeo.

- Requisitos y exclusiones.

- Alcance por segmento y canal.

- Compatibilidades y prioridad.

- Vigencia.

- Posibilidad de conservar condiciones en propuestas individuales.

Las fórmulas deben utilizar comportamientos admitidos por el producto.

### Desarrollo

- Registrar la condición.

- Comprobar integridad y unidades.

- Revisar restricciones de cumplimiento.

- Evaluar combinaciones y superposiciones.

- Comprobar cálculos.

- Revisar expresión contractual.

- Obtener aprobación.

### Reglas y configuración

- Una tasa identifica tipo, período, base temporal y capitalización.

- Un cargo identifica su causa; no puede duplicar otro por el mismo hecho.

- La moneda determina la unidad mínima del importe final.

- Para importes positivos se adopta redondeo al más cercano, elevando los empates.

- Una reversión utiliza el importe efectivamente registrado; no vuelve a redondear el cálculo original.

- Una modalidad que requiera otro redondeo debe identificarlo expresamente y estar admitida por el producto.

- Las bonificaciones identifican requisitos, duración y consecuencias de perderlos.

- Las bonificaciones no se acumulan salvo compatibilidad expresa.

- Las condiciones coincidentes se resuelven por prioridad numérica: el número menor tiene precedencia.

- Condiciones incompatibles con igual prioridad impiden publicar.

- Los tributos se determinan mediante Criterios tributarios.

- Un valor cero es una condición explícita; un valor ausente es información incompleta.

- Los límites comerciales no sustituyen límites de riesgo.

### Resultados y excepciones

Se obtiene una condición aprobada para publicación o una causa de impedimento.

La aprobación de una tasa no habilita una fórmula o modalidad que el producto no admita.

### Criterios de aceptación

| Situación                                                                 | Resultado esperado                                        |
|---------------------------------------------------------------------------|-----------------------------------------------------------|
| Comisión del 1,5 % sobre 1.000 unidades                                   | Importe previo a tributos de 15 unidades.                 |
| Resultado de 10,005 en moneda con dos decimales bajo el redondeo adoptado | Importe final de 10,01.                                   |
| Dos bonificaciones incompatibles con igual prioridad                      | No se publica la combinación.                             |
| Cargo configurado en cero                                                 | Se aplica cero; no se interpreta como condición faltante. |
| Reversión de un cargo registrado de 10,01                                 | Se revierte 10,01.                                        |

## 12. OF-01-09. Publicar, sustituir y retirar ofertas comerciales

### Propósito y responsable

Determinar las condiciones disponibles para nuevas propuestas.

Responsable: Condiciones comerciales.

### Información y condiciones previas

Se requiere versión aprobada, producto, alcance, vigencia y actuación.

### Desarrollo

- Comprobar aprobación.

- Revisar dependencias y superposiciones.

- Programar o aplicar publicación.

- Identificar condiciones sustituidas.

- Comunicar el resultado.

- Tratar propuestas individuales afectadas.

### Reglas y configuración

- La oferta se utiliza únicamente dentro de su vigencia.

- El inicio de una solicitud no conserva condiciones.

- La conservación requiere que la oferta la permita y que Contratos emita una propuesta individual.

- La oferta debe definir un plazo máximo positivo de conservación para permitirla.

- Ese plazo se expresa en días corridos desde la emisión, conservando la hora local.

- El vencimiento de la propuesta será el menor entre ese plazo y la vigencia de sus restantes aprobaciones.

- Si la oferta no permite conservación, las condiciones se comprueban nuevamente al celebrar.

- Una oferta retirada solo puede respetarse en propuestas anteriores cuando la decisión lo autoriza expresamente.

- Las prohibiciones y restricciones aplicables prevalecen.

- La sustitución no modifica contratos existentes.

- El retiro de un producto impide usar sus ofertas para nuevas propuestas.

### Resultados y excepciones

La actuación queda aplicada, programada o pendiente.

Una publicación incierta se consulta antes de repetir. Una comunicación pendiente no permite presumir que una versión
anterior sigue vigente.

### Criterios de aceptación

| Situación                                                             | Resultado esperado                                              |
|-----------------------------------------------------------------------|-----------------------------------------------------------------|
| Oferta vigente hasta las 12:00                                        | No se utiliza a las 12:00 ni después.                           |
| Propuesta emitida el día 10 a las 15:00 con conservación de tres días | Vence el día 13 a las 15:00, salvo antecedente que venza antes. |
| Existe solicitud, pero no propuesta individual                        | No se conservan automáticamente condiciones vencidas.           |
| Se publica una tasa nueva                                             | No cambia contratos celebrados.                                 |

## 13. OF-01-10. Determinar las condiciones comerciales aplicables

### Propósito y responsable

Resolver la oferta correspondiente a una propuesta concreta.

Responsable: Condiciones comerciales.

### Información y condiciones previas

Se requiere producto, versión, fecha, canal, segmento, moneda, importes, plazos y antecedentes necesarios.

### Desarrollo

- Identificar ofertas vigentes.

- Filtrar por alcance.

- Comprobar requisitos.

- Aplicar prioridades y compatibilidades.

- Determinar el conjunto resultante.

- Informar vigencia y conservación admitida.

- Conservar las referencias utilizadas.

### Reglas

- El resultado debe ser inequívoco.

- No se sustituye una oferta inexistente por otra de distinto alcance.

- Los beneficios condicionados requieren antecedentes suficientes.

- Los valores indicativos se distinguen de condiciones firmes.

- Las condiciones dependientes de datos futuros identifican fórmula y supuestos.

- El módulo del producto calcula cronogramas, costo financiero total y otras expresiones especializadas.

- La consulta no constituye aceptación contractual.

- Los contratos vigentes utilizan lo pactado y sus mecanismos de actualización.

### Criterios de aceptación

| Situación                                                              | Resultado esperado                                                                 |
|------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| Hay una oferta general y otra específica compatible con el solicitante | Se aplica la prioridad definida.                                                   |
| Falta acreditar el requisito de una bonificación                       | No se confirma ese beneficio.                                                      |
| No existe oferta para la moneda solicitada                             | Se informa ausencia de oferta.                                                     |
| Se consulta un préstamo existente                                      | Se utilizan sus condiciones contractuales, sin reemplazarlas por la oferta actual. |

## 14. OF-01-11. Administrar calendarios y convenciones temporales

### Propósito y responsable

Mantener calendarios y reglas comunes de interpretación de fechas.

Responsable: Calendarios bancarios.

### Información y condiciones previas

Se requiere jurisdicción, finalidad, zona horaria, días ordinarios, feriados, excepciones, fuente y cobertura.

La definición debe permitir clasificar todas las fechas comprendidas.

### Desarrollo

- Registrar calendario o modificación.

- Comprobar cobertura y contradicciones.

- Identificar diferencias respecto de la versión anterior.

- Obtener aprobación.

- Publicar.

- Comunicar fechas potencialmente afectadas.

- Mantener seguimiento de incidencias.

### Reglas y configuración

El banco admite estas convenciones:

| Convención                 | Comportamiento                                                                                              |
|----------------------------|-------------------------------------------------------------------------------------------------------------|
| Sin ajuste                 | Conserva la fecha contractual.                                                                              |
| Hábil siguiente            | Si es inhábil, avanza al primer hábil posterior.                                                            |
| Hábil anterior             | Si es inhábil, retrocede al primer hábil anterior.                                                          |
| Hábil siguiente modificado | Avanza al siguiente hábil, salvo que cambie el mes; entonces utiliza el hábil anterior a la fecha original. |

- Cada producto o proceso selecciona expresamente su convención.

- Las convenciones pueden coexistir sobre un mismo calendario.

- Una excepción fechada prevalece sobre el patrón ordinario de días.

- Las excepciones contradictorias deben resolverse antes de publicar.

- Los horarios de corte pertenecen al producto o mecanismo operativo.

- Los calendarios publicados deben cubrir al menos las fechas requeridas por las operaciones habilitadas.

- La falta de cobertura impide emitir un cálculo definitivo.

- Un cambio extraordinario no modifica vencimientos automáticamente.

- Cada responsable decide su aplicación a obligaciones existentes conforme al contrato y conserva fecha anterior y
  resultante.

- Las correcciones históricas mantienen disponible la versión utilizada originalmente.

### Criterios de aceptación

| Situación                                                  | Resultado esperado                                                            |
|------------------------------------------------------------|-------------------------------------------------------------------------------|
| Un lunes se declara inhábil como excepción                 | Prevalece sobre el patrón ordinario de lunes hábil.                           |
| Dos excepciones clasifican la misma fecha de forma opuesta | No se publica hasta resolverlas.                                              |
| Se incorpora un feriado extraordinario                     | Se identifican consumidores y fechas afectadas.                               |
| Una cuota ya estaba constituida                            | Su módulo determina el tratamiento; el calendario no la modifica por sí solo. |

## 15. OF-01-12. Determinar fechas bancarias y series de vencimientos

### Propósito y responsable

Clasificar fechas, aplicar desplazamientos y generar series sin introducir desplazamientos acumulativos involuntarios.

Responsable: Calendarios bancarios.

### Información y condiciones previas

La solicitud identifica:

- Fecha o instante de referencia.

- Calendario y versión.

- Zona horaria, si corresponde.

- Operación solicitada.

- Cantidad y unidad de desplazamiento.

- Convención de ajuste.

- Fecha de anclaje para series.

- Modalidad de conservación de día o fin de mes.

- Cantidad de ocurrencias o fecha de finalización.

### Desarrollo

- Comprobar información y cobertura.

- Convertir el instante a fecha local cuando corresponda.

- Determinar la fecha contractual sin ajuste.

- Aplicar la convención bancaria.

- Conservar fecha original y ajustada.

- En series, repetir desde el anclaje correspondiente.

- Devolver resultados y referencias.

### Reglas de desplazamiento

- Los días corridos se suman o restan como fechas calendario.

- Los días hábiles se cuentan excluyendo la fecha inicial.

- Un desplazamiento de cero conserva la fecha antes del ajuste.

- El ajuste hábil se aplica después del desplazamiento.

- Sumar meses conserva el día del anclaje; si no existe en el destino, utiliza el último día calendario de ese mes.

- La modalidad de fin de mes requiere que el anclaje sea el último día de su mes.

- En esa modalidad, las ocurrencias se ubican en el último día calendario de cada mes de destino antes del ajuste hábil.

### Reglas de series

- Las series por meses se calculan desde el anclaje original.

- La ocurrencia número n utiliza el anclaje más n veces la frecuencia mensual.

- Una fecha acortada por febrero no cambia el día de anclaje de marzo.

- El ajuste hábil de una ocurrencia no modifica las siguientes.

- Las series por días hábiles utilizan la secuencia definida de días hábiles y conservan su origen y frecuencia.

- Cada ocurrencia mantiene identidad, fecha contractual y fecha ajustada.

- Si dos ocurrencias terminan en la misma fecha ajustada, se conservan ambas. El módulo responsable decide su
  presentación y ejecución sin perder obligaciones.

- Una reprogramación contractual establece un nuevo anclaje únicamente para las ocurrencias futuras alcanzadas.

- Los intervalos y bases de devengamiento pertenecen al producto; mover una fecha no determina por sí solo cuánto
  interés corresponde.

### Resultados y excepciones

Se devuelven fechas calculadas o un impedimento explícito.

No se sustituye un calendario ni una convención faltantes por valores supuestos.

Si un cambio de calendario afecta una serie existente, se identifican las ocurrencias potencialmente afectadas; no se
sobrescriben sus fechas sin una decisión del responsable.

### Criterios de aceptación

Los ejemplos siguientes utilizan calendarios de prueba con la clasificación de días indicada.

| Entrada                                                                                                | Resultado esperado                                                    |
|--------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| Serie mensual anclada el 30 de enero de 2027, conservación de día y sin ajuste                         | 28 de febrero y 30 de marzo de 2027.                                  |
| Serie mensual anclada el 31 de enero de 2027, modalidad fin de mes y sin ajuste                        | 28 de febrero y 31 de marzo de 2027.                                  |
| La ocurrencia de febrero se desplaza al siguiente hábil                                                | La fecha contractual de marzo sigue derivándose del anclaje original. |
| Fecha original inhábil; el siguiente hábil pertenece al mes siguiente; convención siguiente modificado | Se utiliza el hábil anterior a la fecha original.                     |
| Lunes hábil más un día hábil; martes también hábil                                                     | Resultado: martes.                                                    |
| Falta cobertura para una ocurrencia                                                                    | No se emite esa fecha como definitiva ni se presume hábil.            |
| Se repite el cálculo con iguales entradas y versiones                                                  | Se obtienen los mismos resultados.                                    |

## 16. Correspondencia funcional

| Definición administrada                         | Responsable                                        | Aplicación                             |
|-------------------------------------------------|----------------------------------------------------|----------------------------------------|
| Identidad y alcance institucional               | Institución                                        | Actividades admitidas                  |
| Organización y facultades                       | Institución                                        | Asignaciones y decisiones internas     |
| Características y versiones de productos        | Catálogo de productos                              | Elegibilidad, contratación y operación |
| Habilitación comercial                          | Catálogo de productos                              | Admisión de nuevas contrataciones      |
| Tasas, tarifas y beneficios ofrecidos           | Condiciones comerciales                            | Preparación de propuestas              |
| Propuestas individuales y condiciones aceptadas | Contratos                                          | Conservación y aplicación contractual  |
| Calendarios y convenciones temporales           | Calendarios bancarios                              | Determinación de fechas                |
| Vencimientos y consecuencias económicas         | Módulo responsable del producto                    | Administración de obligaciones         |
| Medidas restrictivas                            | Restricciones y servicios afectados                | Limitación efectiva de actuaciones     |
| Aprobaciones humanas y evidencias               | Gestión administrativa y Documentos y comprobantes | Justificación y conservación           |

Cada consumidor conserva las referencias que utilizó y comprueba su vigencia en el momento exigible. La configuración
institucional y comercial determina qué puede ofrecer el banco; los acuerdos y operaciones concretos permanecen bajo la
autoridad de sus módulos responsables.
