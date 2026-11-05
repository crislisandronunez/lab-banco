# Diseño técnico — Módulo 1. Institución y oferta comercial

## 1. Propósito y alcance

Este documento define la implementación técnica de:

- Institución.

- Catálogo de productos.

- Condiciones comerciales.

- Calendarios bancarios.

Comprende datos propios, tipos, persistencia, estados, interfaces HTTP, eventos, transacciones, permisos, tareas y
recuperación.

Las definiciones mantienen las responsabilidades del modelo funcional y los contratos CI-01-001 a CI-01-025.

Este módulo no constituye productos de clientes, calcula deuda individual ni ejecuta movimientos monetarios.

Las comprobaciones informativas y las preparaciones para una actuación concreta son operaciones diferentes. Las primeras
describen la situación evaluada; las segundas mantienen una coordinación persistente con el responsable de la decisión.

## 2. Distribución técnica

| Servicio | Aplicación | Base de datos |
| --- | --- | --- |
| Institución | institution-service | institution_db |
| Catálogo de productos | product-catalog-service | product_catalog_db |
| Condiciones comerciales | commercial-terms-service | commercial_terms_db |
| Calendarios bancarios | banking-calendar-service | banking_calendar_db |

Cada aplicación utiliza Java, Spring Boot, PostgreSQL, Flyway y un usuario de base exclusivo.

Las referencias externas son identificadores. No existen entidades JPA, tablas, claves foráneas ni consultas SQL
compartidas entre servicios.

## 3. Convenciones de datos

### 3.1. Tipos y obligatoriedad

Un campo omitido no equivale a cero, cadena vacía o colección vacía.

| Tipo | Java | PostgreSQL | JSON |
| --- | --- | --- | --- |
| Identificador | UUID | uuid | Cadena UUID |
| Versión | long | bigint | Entero positivo |
| Entero | int | integer | Entero |
| Texto limitado | String | varchar(n) | Cadena |
| Instante | Instant | timestamptz | ISO 8601 UTC |
| Fecha | LocalDate | date | YYYY-MM-DD |
| Decimal | BigDecimal | numeric(p,s) | Cadena decimal |
| Booleano | boolean | boolean | Booleano |
| Enumeración | Enumeración | varchar(40) | Código permitido |
| Colección | Lista o conjunto | Tabla dependiente | Arreglo |

Las colecciones que representan conjuntos rechazan duplicados.

### 3.2. Límites comunes

| Dato | Regla |
| --- | --- |
| Código técnico | De 1 a 80 caracteres; mayúsculas, números y guion bajo |
| Nombre | De 1 a 200 caracteres |
| Descripción | Hasta 4.000 caracteres |
| Fundamento | De 1 a 4.000 caracteres |
| Identificador externo | De 1 a 120 caracteres |
| Clave de idempotencia | De 1 a 120 caracteres |
| Correlación | De 1 a 120 caracteres |
| Huella de contenido | SHA-256 hexadecimal de 64 caracteres |
| Zona horaria | Identificador IANA válido, hasta 80 caracteres |
| Moneda | ARS o USD |
| Versión | Entero mayor o igual a 1 |

Los textos admiten Unicode y rechazan caracteres de control no permitidos.

Los identificadores oficiales conservan su representación textual y sus ceros iniciales.

### 3.3. Dinero y tasas

| Dato | Persistencia | Regla |
| --- | --- | --- |
| Importe configurado | numeric(24,8) | Moneda y unidad obligatorias |
| Tasa o porcentaje | numeric(24,12) | Proporción decimal |
| Prioridad | integer | Mayor o igual a cero |
| Escala final | smallint | Entre cero y ocho |

El 1,5 % se representa como 0.015.

La precisión de configuración no modifica la unidad mínima exigida por una operación monetaria.

Los valores fuera de precisión o escala se rechazan sin truncamiento silencioso.

### 3.4. Referencias y vigencia

Una referencia versionada contiene objectId, version y sourceService.

Una referencia documental contiene documentId y documentVersion.

Los intervalos de vigencia incluyen el inicio y excluyen el final. Si existe final, debe ser posterior al inicio.

La zona institucional es America/Argentina/Buenos_Aires.

Vigencia, publicación y cobertura de fechas son conceptos diferentes.

## 4. Contrato común de actuaciones

### 4.1. Identidad y contexto

Los comandos requieren autenticación, Idempotency-Key y X-Correlation-Id.

El contexto contiene:

| Campo | Tipo | Obligatoriedad |
| --- | --- | --- |
| actionId | UUID | Obligatorio |
| operationId | UUID | Obligatorio |
| causationId | UUID | Opcional |
| reason | Fundamento | Obligatorio |
| expectedVersion | Versión | Obligatorio para modificar |
| receivedAt | Instante | Cuando la recepción original afecta al negocio |

El receptor agrega servicio productor, actor verificado, recepción efectiva y huella canónica.

La identidad del actor procede de credenciales verificadas o de una delegación comprobada. Un identificador enviado en
el cuerpo no acredita por sí solo su intervención.

La actuación, su clave y el contenido deben corresponder. Reutilizar una clave con otro contenido genera conflicto.

### 4.2. Propuestas y aprobación

Los cambios se conservan como propuestas antes de aplicarse.

| Campo | Contenido |
| --- | --- |
| changeId | Identificador de propuesta |
| targetId | Objeto existente, cuando corresponda |
| proposalVersion | Versión de propuesta |
| expectedTargetVersion | Versión esperada del objeto existente |
| changeType | Modalidad tipada |
| contentHash | Huella del contenido |
| preparedBy | Actor verificado |
| approvalId | Aprobación exigible para aplicar |
| validity | Vigencia propuesta |
| status | Estado |
| evidenceReferences | Evidencias; colección vacía solo si no son exigibles |

Los estados son:

| Estado | Significado |
| --- | --- |
| DRAFT | Contenido en preparación |
| PENDING_APPROVAL | Enviado a aprobación |
| APPROVED | Aprobación comprobada |
| SCHEDULED | Aplicación futura |
| PENDING_DEPENDENCIES | Requisitos pendientes |
| APPLIED | Cambio propio aplicado |
| REQUIRES_REVIEW | Cambió el contenido o una condición relevante |
| REJECTED | Rechazo definitivo |
| WITHDRAWN | Retirado antes de aplicar |

La aprobación corresponde a una versión y una huella exactas.

Editar contenido aprobado genera una revisión que requiere nueva aprobación.

La evidencia de una aprobación histórica no constituye una autorización general para cualquier aplicación posterior. Su
utilización respeta el alcance y las condiciones que le correspondan.

### 4.3. Operaciones de propuestas

Cada colección de cambios admite:

| Operación | Método y recurso relativo a la colección |
| --- | --- |
| Crear | POST a la colección |
| Consultar | GET /{changeId} |
| Editar borrador | PUT /{changeId}/draft |
| Enviar a aprobación | POST /{changeId}/submission |
| Aplicar | POST /{changeId}/application |
| Retirar | POST /{changeId}/withdrawal |

Aplicar recibe actuación, versión y aprobación. Retirar recibe actuación, versión y fundamento.

Retirada y aplicación se ordenan sobre la misma propuesta. Una propuesta aplicada requiere una actuación posterior para
modificar sus efectos.

### 4.4. Resultado

El resultado identifica:

- Actuación y propuesta.

- Estado.

- Objeto y versión.

- Confirmación, cuando exista.

- Momento de efecto.

- Dependencias pendientes.

- Observaciones autorizadas.

Cada dependencia pendiente identifica responsable, referencia, estado, última comprobación y próxima revisión.

La aplicación del cambio propio y la conclusión de su coordinación con otros servicios se informan separadamente cuando
no terminan juntas.

## 5. Servicio Institución

### 5.1. Modelo institucional

La versión institucional contiene:

| Campo | Tipo | Obligatoriedad |
| --- | --- | --- |
| institutionId | UUID | Obligatorio |
| version | Versión | Obligatorio |
| legalName | Nombre | Obligatorio |
| tradeName | Nombre | Opcional |
| identifiers | Identificadores | Al menos uno |
| addresses | Domicilios | Al menos uno |
| activityCodes | Conjunto de códigos | Obligatorio |
| validity | Vigencia | Obligatorio |
| foundationDocument | Referencia documental | Obligatorio |
| changeId | UUID | Obligatorio |

Cada identificador contiene tipo, valor y jurisdicción.

Cada domicilio contiene país de dos caracteres, provincia o región, localidad, línea principal de hasta 250 caracteres y
código postal de hasta 20.

El cambio de nombre o domicilio mantiene institutionId.

### 5.2. Organización y adscripciones

La unidad organizativa contiene:

| Campo | Contenido |
| --- | --- |
| unitId y version | Identidad y versión |
| code | Código único |
| name | Denominación |
| unitType | ORGANIZATIONAL_UNIT o BRANCH |
| parentUnitId | Obligatorio salvo unidad raíz |
| address | Obligatorio para sucursal |
| functionCodes | Funciones asignadas |
| status | ACTIVE, CLOSING o INACTIVE |
| validity | Vigencia |
| continuityAssignments | Obligaciones y responsables de continuidad cuando corresponda |

La función institucional identifica código, nombre, unidad y vigencia.

La adscripción vincula persona, emisor y sujeto de identidad autenticada, función, unidad, vigencia y fundamento.

Los cargos de un token no conceden por sí solos facultades institucionales.

### 5.3. Facultades

La facultad contiene:

| Campo | Contenido |
| --- | --- |
| authorityId y version | Identidad y versión |
| parentAuthorityId | Fundamento de delegación, cuando exista |
| grantorAssignmentId | Otorgante; exceptuado únicamente el fundamento constitutivo |
| recipientFunctionId | Función destinataria |
| decisionCode | Decisión permitida |
| scope | Institución, unidades, productos u objetos admitidos |
| amountLimits | Límites por moneda cuando corresponda |
| subdelegationAllowed | Permiso explícito de subdelegación |
| jointApprovalRule | Regla conjunta, cuando corresponda |
| validity | Vigencia |
| status | ACTIVE, REVOKED o EXPIRED |

Cada límite monetario es no negativo e identifica moneda.

Una equivalencia entre monedas exige regla y cotización identificadas.

La regla conjunta identifica funciones, cantidad mínima de personas distintas e incompatibilidades.

La delegación no puede ampliar alcance, importe, vigencia ni capacidad de subdelegar respecto de su fundamento.

### 5.4. Consultas y cambios

| Contrato | Interfaz o colección | Finalidad |
| --- | --- | --- |
| CI-01-001 | GET /v1/institution | Definición institucional |
| CI-01-001 | GET /v1/organizational-units | Unidades |
| CI-01-001 | GET /v1/organizational-units/{unitId} | Unidad concreta |
| CI-01-001 | GET /v1/institutional-functions | Funciones |
| CI-01-001 | GET /v1/actor-assignments/{assignmentId} | Adscripción |
| CI-01-002 | /v1/institution-changes | Cambios institucionales y organizativos |
| CI-01-003 | /v1/authority-changes | Otorgamiento, modificación y revocación |
| CI-01-004 | POST /v1/authority-checks | Comprobación informativa |
| CI-01-016 | GET /v1/institution-actions/{actionId} | Seguimiento de actuaciones |

Las consultas aceptan versión o instante histórico y finalidad. Las colecciones utilizan paginación estable.

Las consultas históricas no habilitan decisiones actuales.

### 5.5. Cambios institucionales

CI-01-002 admite:

| Modalidad | Contenido |
| --- | --- |
| ESTABLISH_INSTITUTION | Definición, acto inicial, funciones, adscripciones y facultades iniciales |
| UPDATE_INSTITUTION | Definición completa y consecuencias |
| CREATE_UNIT | Unidad propuesta |
| UPDATE_UNIT | Unidad y versión completa |
| REORGANIZE_UNIT | Dependencia y continuidad |
| CLOSE_UNIT | Cese y tratamiento de responsabilidades |
| ADMINISTER_FUNCTION | Función y vigencia |
| ADMINISTER_ASSIGNMENT | Actor, función, unidad, vigencia y fundamento |

La constitución tiene referencia única permanente y solo procede sobre una institución no constituida.

Su fundamento documental y el procedimiento inicial de autorización quedan identificados. No se utiliza como mecanismo
para reemplazar autoridades constituidas.

Debe establecer al menos dos personas distintas para las actuaciones posteriores que exijan separación de funciones.

### 5.6. Cambios de facultades

CI-01-003 admite GRANT, MODIFY y REVOKE.

La revocación recibe facultad, versión, momento y fundamento. No exige reenviar límites propios de un otorgamiento.

El resultado identifica la facultad y sus delegaciones dependientes.

La revocación se registra y bloquea nuevas preparaciones incompatibles. Los intentos ya preparados se resuelven mediante
el protocolo de este documento.

### 5.7. Comprobación informativa

CI-01-004 recibe:

- Adscripción.

- Código de decisión.

- Objeto.

- Producto, cuando corresponda.

- Importe y moneda para actos monetarios.

- Huella del contenido de decisión.

- Participantes de actuación conjunta.

- Instante de referencia.

- Finalidad.

Devuelve checkId, resultado SUFFICIENT, INSUFFICIENT o PENDING, facultades y versiones, intervenciones faltantes,
instante evaluado y motivos autorizados.

Esta comprobación no crea una preparación ni mantiene facultades disponibles para una decisión futura.

### 5.8. Persistencia e invariantes

Institución conserva definiciones, unidades, funciones, adscripciones, facultades, reglas conjuntas, propuestas,
comprobaciones y preparaciones.

Los códigos estables son únicos.

Los cambios de jerarquía y delegación se serializan mediante un registro de control institucional. La verificación de
ciclos ocurre dentro de esa protección.

Las preparaciones utilizan el mismo orden de control que los cambios que pueden invalidar su fundamento.

Una revocación del fundamento alcanza las delegaciones dependientes, aunque todavía no se haya distribuido su evento.

Las versiones históricas permanecen disponibles.

## 6. Servicio Catálogo de productos

### 6.1. Definición

La definición del producto contiene:

| Grupo | Datos |
| --- | --- |
| Identidad | productId, definitionVersion y código único |
| Presentación | Nombre, familia y variante |
| Alcance | Monedas y tipos de participantes |
| Operación | Operaciones y capacidades |
| Admisión | Requisitos de elegibilidad |
| Relaciones | Dependencias y compatibilidades |
| Comercial | Conceptos comerciales |
| Contratación | Requisitos y etapas |
| Terminación | Reglas aplicables |
| Tiempo | Vigencia |

Cada capacidad identifica código, versión funcional y servicio responsable.

Los conjuntos obligatorios de monedas y participantes no pueden quedar vacíos cuando ello impida interpretar el
producto.

### 6.2. Requisitos tipados

Cada requisito contiene:

- requirementId.

- attributeCode.

- sourceService.

- valueType.

- operator.

- expectedValues cuando el operador los requiere.

- requiredStage.

- missingResult.

Los tipos admitidos son texto, entero, decimal, fecha, booleano y código.

Los operadores son EQUALS, IN, GREATER_OR_EQUAL, LESS_OR_EQUAL, BETWEEN y PRESENT.

La ausencia se resuelve como pendiente o impedimento conforme al requisito. No se presupone cumplimiento.

No se admiten scripts ni SQL configurables.

### 6.3. Habilitación

La habilitación contiene producto, versión de definición, versión de habilitación, alcance, vigencia, estado y
tratamiento de propuestas anteriores.

Los estados son:

- NOT_ENABLED.

- SCHEDULED.

- ENABLED.

- SUSPENDED.

- WITHDRAWN.

Cada confirmación de dependencia identifica capacidad, servicio, referencia, versión, momento, resultado y condiciones
de vigencia.

La disponibilidad técnica de un servicio no demuestra que una capacidad funcional esté habilitada.

### 6.4. Interfaces

| Contrato | Interfaz o colección | Finalidad |
| --- | --- | --- |
| CI-01-005 | GET /v1/products/{productId} | Definición y habilitación |
| CI-01-005 | GET /v1/products/{productId}/versions/{version} | Versión histórica |
| CI-01-006 | /v1/product-definition-changes | Creación y nuevas versiones |
| CI-01-007 | /v1/product-enablement-changes | Habilitar, suspender, reanudar o retirar |
| CI-01-008 | POST /v1/product-eligibility-checks | Elegibilidad y compatibilidad |
| CI-01-017 | GET /v1/product-actions/{actionId} | Seguimiento |

Aprobar una definición no equivale a habilitar el producto.

La habilitación y reanudación requieren las confirmaciones aplicables de oferta, contratación, operación, controles,
contabilidad y documentación.

Producto y oferta pueden prepararse conjuntamente. La contratación requiere que ambos resulten utilizables conforme a
sus reglas.

### 6.5. Elegibilidad

La comprobación recibe producto y versión, participantes, antecedentes verificados, productos relacionados, instante y
finalidad.

Cada antecedente identifica atributo, valor tipado, fuente, versión, fecha y evidencia exigible.

Cada producto relacionado identifica contrato, cuenta o producto concreto y estado comprobado.

Devuelve ELIGIBLE, INELIGIBLE o PENDING, con resultado por requisito, dependencias, fuentes y versiones.

No concede crédito ni sustituye la admisión del cliente.

### 6.6. Persistencia e invariantes

Se separan producto estable, versiones, requisitos, capacidades, compatibilidades, habilitaciones y confirmaciones.

Los cambios del mismo producto utilizan control local y versión esperada.

Las vigencias incompatibles se impiden dentro de esa protección.

Las preparaciones que requieran habilitación vigente se vinculan al producto y al alcance comprobados.

Suspender o retirar bloquea nuevas preparaciones incompatibles y resuelve las existentes conforme al tratamiento
explícito de propuestas anteriores.

Retirar una versión no elimina sus contratos ni sus consultas históricas.

## 7. Servicio Condiciones comerciales

### 7.1. Condición comercial

La condición contiene:

| Grupo | Datos |
| --- | --- |
| Identidad | conditionId y version |
| Producto | productId y versiones compatibles |
| Aplicación | conceptCode y triggerCode |
| Valor | valueType, currency y value |
| Cálculo | calculationDefinition, baseCode y periodicityCode |
| Liquidación | roundingMode y finalScale |
| Selección | Requisitos y prioridad |
| Tiempo | Vigencia |

Los tipos de valor son FIXED_AMOUNT, RATE, PERCENTAGE, FORMULA_REFERENCE y EXTERNAL_REFERENCE.

La moneda es obligatoria para conceptos monetarios.

Las tasas identifican tipo, período, base temporal y capitalización cuando corresponda.

Las fórmulas referencian un mecanismo implementado y versionado con operandos tipados. No ejecutan texto arbitrario.

### 7.2. Oferta

La oferta contiene:

- offerId y version.

- Producto y versiones compatibles.

- Referencias exactas de condiciones.

- Segmentos, canales y monedas.

- Prioridad.

- Permiso de conservación.

- Días de conservación cuando se permite.

- Vigencia.

- Estado.

Los estados son DRAFT, APPROVED, SCHEDULED, PUBLISHED y WITHDRAWN.

La sustitución o el retiro incluyen una decisión explícita sobre propuestas individuales anteriores.

Las compatibilidades de beneficios se conservan separadamente. La ausencia de permiso de acumulación impide acumularlos.

### 7.3. Interfaces

| Contrato | Interfaz o colección | Finalidad |
| --- | --- | --- |
| CI-01-009 | /v1/commercial-condition-changes | Definir condiciones |
| CI-01-010 | /v1/commercial-offer-changes | Publicar, sustituir o retirar |
| CI-01-011 | POST /v1/commercial-determinations | Determinar condiciones |
| CI-01-012 | GET /v1/offers/{offerId} | Consultar oferta |
| CI-01-012 | GET /v1/offers/{offerId}/versions/{version} | Consultar versión |
| CI-01-018 | GET /v1/commercial-actions/{actionId} | Seguimiento |

Crear o modificar una condición requiere su definición completa.

El cero es un valor explícito. Una configuración sin tasa, unidad o base obligatoria permanece incompleta.

Las modalidades de oferta son PUBLISH, REPLACE y WITHDRAW.

REPLACE identifica oferta anterior y nueva. WITHDRAW identifica versión retirada y tratamiento de propuestas previas.

### 7.4. Determinación

La entrada contiene producto y versión, instante, canal, segmento cuando corresponda, moneda, importe o plazo exigibles,
antecedentes y finalidad.

La respuesta contiene:

- determinationId.

- RESOLVED, NO_OFFER, PENDING o CONFLICT.

- Contenido evaluado.

- Ofertas y versiones exactas.

- Valores, fórmulas y referencias por concepto.

- Requisitos faltantes.

- Vigencias.

- Conservación autorizada.

- Condiciones firmes y dependencias futuras.

El menor número de prioridad prevalece.

Un empate incompatible no se resuelve por orden de creación.

Contratos determina la vigencia de la propuesta individual. Los responsables del producto calculan cuotas, cronogramas y
CFT.

Una determinación no constituye una reserva de oferta.

### 7.5. Preparación comercial

Cuando una celebración depende de la aplicabilidad comercial actual, Condiciones comerciales prepara esa condición para
el intento concreto.

La preparación identifica determinación, oferta, versiones, contenido y tratamiento de conservación.

Si existe una conservación válida y suficiente, se comprueba ese fundamento. No se exige convertir la oferta histórica
en una oferta actualmente publicada.

Si no existe conservación autorizada, la preparación utiliza las condiciones aplicables y su orden frente a
sustituciones o retiros.

Una nueva oferta no modifica contratos existentes ni sustituye silenciosamente el contenido aceptado.

### 7.6. Persistencia e invariantes

Se conservan condiciones, ofertas, versiones, alcances, compatibilidades, determinaciones, propuestas y preparaciones.

Las publicaciones potencialmente coincidentes se serializan por producto.

La verificación de superposiciones y la publicación ocurren bajo esa protección.

Los retiros y las preparaciones utilizan controles compatibles para ordenar sus resultados.

Las determinaciones conservan las entradas mínimas necesarias, sin copiar legajos completos.

## 8. Servicio Calendarios bancarios

### 8.1. Modelo

La versión del calendario contiene:

| Campo | Contenido |
| --- | --- |
| calendarId y version | Identidad y versión |
| code y name | Código único y denominación |
| purposeCode | Finalidad |
| jurisdictionCode | Jurisdicción |
| zoneId | Zona IANA |
| coverageFrom y coverageTo | Cobertura inclusiva |
| ordinaryBusinessDays | Conjunto de días del uno al siete |
| exceptions | Excepciones por fecha |
| sourceReference | Fuente |
| validity | Vigencia |
| status | Estado |

Los estados son DRAFT, APPROVED, SCHEDULED, PUBLISHED y RETIRED.

Cada excepción identifica fecha, BUSINESS_DAY o NON_BUSINESS_DAY, fundamento y fuente.

Una versión no admite clasificaciones contradictorias para una misma fecha.

### 8.2. Interfaces

| Contrato | Interfaz o colección | Finalidad |
| --- | --- | --- |
| CI-01-013 | /v1/calendar-changes | Creación, nueva versión o retiro |
| CI-01-014 | GET /v1/calendars/{calendarId} | Definición |
| CI-01-014 | GET /v1/calendars/{calendarId}/versions/{version} | Versión concreta |
| CI-01-014 | GET /v1/calendar-conventions | Convenciones |
| CI-01-015 | POST /v1/date-calculations | Clasificación, desplazamiento, ajuste o serie |
| CI-01-019 | GET /v1/calendar-actions/{actionId} | Seguimiento |

Las convenciones son NONE, FOLLOWING, PRECEDING y MODIFIED_FOLLOWING.

Los horarios de corte pertenecen a sus productos o mecanismos.

### 8.3. Cálculos

La entrada identifica:

- Operación CLASSIFY, SHIFT, ADJUST o GENERATE_SERIES.

- Calendario y versión cuando corresponda.

- Fecha o instante de referencia.

- Zona para interpretar un instante.

- Desplazamiento y unidad.

- Convención de ajuste.

- Anclaje y modalidad para series.

- Frecuencia.

- Índice inicial.

- Cantidad de ocurrencias o fecha final.

Las unidades son CALENDAR_DAYS, BUSINESS_DAYS y MONTHS.

Las modalidades de anclaje mensual son DAY_OF_MONTH y END_OF_MONTH.

Se rechazan fecha e instante simultáneos, y cantidad y fecha final simultáneas.

El índice cero corresponde al anclaje. END_OF_MONTH exige un anclaje de fin de mes.

La respuesta identifica cálculo, huella de entradas, calendario, versión, zona, ocurrencias y rangos faltantes.

Sus resultados son COMPLETE, INCOMPLETE_COVERAGE o INVALID_INPUT.

### 8.4. Invariantes

Los días hábiles excluyen la fecha inicial.

El ajuste se aplica después del desplazamiento.

Las series mensuales se calculan desde el anclaje original. El ajuste de febrero no desplaza el anclaje de marzo.

Una cobertura incompleta no se presenta como serie definitiva.

Las versiones publicadas son inmutables. Consultar una versión no utiliza automáticamente la más reciente.

Un cálculo sobre una versión inmutable no requiere una preparación persistente por ese solo motivo.

Una corrección del calendario no sobrescribe vencimientos administrados por otros servicios. El responsable de la
obligación decide y registra su tratamiento.

## 9. Preparaciones de este módulo

### 9.1. Responsables y alcance

| Servicio | Condición que puede preparar |
| --- | --- |
| Institución | Facultades, adscripciones y reglas de actuación conjunta |
| Catálogo de productos | Habilitación y compatibilidad de la versión para el acto |
| Condiciones comerciales | Aplicabilidad o conservación autorizada de condiciones |
| Calendarios bancarios | No participa como autoridad preparada en los cálculos ordinarios sobre versiones inmutables |

Solo se prepara una condición cuando el recorrido funcional lo exige.

Los hechos que pertenecen a Personas, Clientes, Representación, Restricciones u otros responsables se verifican mediante
sus contratos. Este módulo no emite preparaciones en nombre de ellos.

### 9.2. Datos de preparación

Cada preparación contiene:

| Campo | Contenido |
| --- | --- |
| preparationId | UUID |
| actionId y operationId | Actuación y operación |
| coordinatorService | Servicio responsable de decidir |
| attemptId | Intento concreto |
| decisionQueryReference | Referencia de consulta admitida por el contrato |
| contentHash | Huella del contenido vinculante |
| actorReferences | Actores verificados |
| objectReferences | Objetos y versiones |
| actionCode | Acto evaluado |
| amount y currency | Cuando correspondan |
| scope | Alcance exacto |
| controlVersions | Versiones de los controles utilizados |
| preparedAt | Momento de preparación |
| decisionDeadline | Límite de decisión |
| state | Estado de coordinación |
| decision | Resultado durable conocido |
| resultReferences | Evidencias del resultado |

La referencia de consulta utiliza destinos configurados y autorizados. No permite suministrar una URL arbitraria para
que el servicio la invoque.

La preparación exige contenido suficiente para comprobar la condición. No acepta una huella sin los datos necesarios
para interpretarla.

### 9.3. Estados

| Estado | Significado |
| --- | --- |
| PREPARED | Preparación favorable persistida |
| RESOLUTION_PENDING | Se requiere conocer o resolver la decisión |
| CONCLUDED_COMMITTED | Decisión confirmada comprobada |
| CONCLUDED_ABORTED | Aborto comprobado |
| REJECTED | Preparación desfavorable definitiva |

Una respuesta incierta se recupera por la actuación original. No se crea otra preparación para evitar consultar la
anterior.

La misma preparación no puede concluir con dos decisiones.

### 9.4. Interfaces

Cada autoridad participante dispone de operaciones explícitas:

| Servicio | Preparar | Consultar | Notificar decisión |
| --- | --- | --- | --- |
| Institución | POST /v1/authority-preparations | GET /v1/authority-preparations/{preparationId} | POST /v1/authority-preparations/{preparationId}/decision-notifications |
| Catálogo de productos | POST /v1/product-action-preparations | GET /v1/product-action-preparations/{preparationId} | POST /v1/product-action-preparations/{preparationId}/decision-notifications |
| Condiciones comerciales | POST /v1/commercial-action-preparations | GET /v1/commercial-action-preparations/{preparationId} | POST /v1/commercial-action-preparations/{preparationId}/decision-notifications |

Estas operaciones desarrollan el protocolo común y se vinculan a las responsabilidades funcionales de comprobación
correspondientes.

No modifican el carácter informativo de CI-01-004, CI-01-008 o CI-01-011.

La consulta de decisión y la petición de aborto se realizan contra el coordinador del intento, mediante su interfaz
autorizada.

### 9.5. Preparación local

La autoridad:

- Verifica emisor, actor, contenido e idempotencia.

- Bloquea sus controles locales en orden estable.

- Comprueba condiciones, vigencias y cambios incompatibles.

- Registra resultado y preparación.

- Confirma la transacción.

- Devuelve referencias de seguimiento.

Las llamadas externas necesarias se realizan fuera de la transacción. Sus resultados se vinculan a versiones y se
comprueban antes de persistir la preparación.

Si la autoridad no puede establecer la suficiencia de su condición, no devuelve una preparación favorable.

### 9.6. Cambios concurrentes

Un cambio incompatible identifica las preparaciones alcanzadas por su alcance, incluidas las que dependen de una
facultad delegante, adscripción o unidad.

Bajo el control local correspondiente:

- Se registra recepción y momento de efecto.

- Se impiden nuevas preparaciones incompatibles.

- Se conserva el conjunto de intentos que requieren resolución.

Después del commit se consultan los coordinadores y se solicitan los abortos procedentes.

La coordinación distingue:

- Intentos abortados.

- Decisiones ya confirmadas.

- Decisiones pendientes de conocer.

- Actuaciones posteriores necesarias.

No se mantiene una transacción SQL durante esas consultas.

El registro de una revocación y su aplicación a todos los intentos existentes son dimensiones diferentes. Una respuesta
no informa conclusión completa mientras existan intentos sin resolver.

Los cambios programados tienen su momento de efecto definido. La preparación no puede fijar un límite de decisión
posterior a un impedimento futuro conocido que deba impedir ese acto.

### 9.7. Decisión y conclusión

La autoridad acepta notificaciones únicamente del coordinador autorizado y verifica intento, preparación, contenido y
resultado.

Cuando sea necesario recuperar o comprobar la decisión, consulta el registro durable del coordinador.

Una decisión confirmada concluye la preparación sin convertirla en un permiso reutilizable.

Un aborto comprobado impide utilizarla para confirmar ese intento.

Los cambios posteriores se aplican a las actuaciones futuras y al tratamiento que corresponda a los hechos existentes.
No eliminan decisiones históricas.

### 9.8. Vencimiento e indisponibilidad

Al alcanzar el límite sin decisión conocida, la preparación pasa a resolución pendiente y se solicita resolver el
intento.

No se presume aborto ni se libera por timeout.

Si el coordinador está indisponible, se conserva el seguimiento y se escala.

La demora afecta al alcance incompatible preparado. No bloquea indiscriminadamente productos, actores u operaciones
ajenos a ese alcance.

## 10. Eventos

### 10.1. Sobre común

Todos los eventos incluyen identificador, tipo, versión de esquema, emisor, agregado, versión del agregado, momento,
operación, correlación, causa cuando corresponda y contenido específico.

La versión del agregado no se confunde con la del esquema.

### 10.2. Eventos funcionales

| Contrato | Tipo | Canal | Contenido |
| --- | --- | --- | --- |
| CI-01-020 | institution.changed.v1 | institution.events.v1 | Institución, cambio, objeto, versiones, efecto y continuidad |
| CI-01-021 | institution.authority-changed.v1 | institution.events.v1 | Facultad, función, cambio, alcance, vigencia y delegación |
| CI-01-022 | product.definition-approved.v1 | product-catalog.events.v1 | Producto, definición, aprobación y capacidades |
| CI-01-023 | product.enablement-changed.v1 | product-catalog.events.v1 | Producto, habilitación, estado, alcance y propuestas anteriores |
| CI-01-024 | commercial-offer.changed.v1 | commercial-terms.events.v1 | Oferta, producto, versiones, alcance, vigencia y conservación |
| CI-01-025 | banking-calendar.changed.v1 | banking-calendar.events.v1 | Calendario, versiones, vigencia, cobertura y fechas afectadas |

Los eventos de cambios que requieren coordinación distinguen el cambio registrado de la aplicación comprobada a los
componentes afectados.

Una notificación de cambio no permite al consumidor suponer que un intento incierto ya fue abortado.

### 10.3. Orden y entrega

La partición utiliza institución, facultad, producto, oferta o calendario según el agregado.

No existe orden global entre claves.

Los cuatro servicios utilizan outbox. Los consumidores con efectos persistentes utilizan inbox.

Los eventos antiguos no reemplazan versiones posteriores. Los huecos relevantes requieren recuperación.

Los eventos no contienen documentos completos ni antecedentes personales innecesarios.

## 11. Persistencia técnica y transacciones

Además de sus tablas de dominio, cada servicio mantiene los registros que necesita de:

| Registro | Contenido |
| --- | --- |
| Actuación | Identidad, emisor, contenido y resultado |
| Propuesta | Objeto, versiones, contenido y aprobación |
| Idempotencia | Productor, clave, actuación y huella |
| Outbox | Evento estable y publicación |
| Inbox | Identidad de evento aplicado |
| Trabajo | Actuación, fecha y control de ejecución |
| Dependencia | Responsable, referencia y estado |
| Preparación | Intento, alcance, condiciones y decisión |
| Control | Versión y serialización de cambios incompatibles |

La combinación productor y clave idempotente es única.

Existe unicidad de preparación por coordinador, intento y condición preparada. Las condiciones distintas del mismo
intento se identifican separadamente.

Los cambios autoritativos, la historia, el resultado idempotente y la outbox se confirman conjuntamente.

Las preparaciones y sus conclusiones utilizan transacciones locales breves.

Las llamadas HTTP, Kafka y el almacenamiento documental se realizan fuera de las transacciones de escritura.

## 12. Respuestas y errores

| HTTP | Significado |
| --- | --- |
| 200 | Consulta o actuación completada |
| 201 | Recurso creado |
| 202 | Actuación aceptada durablemente con seguimiento |
| 400 | Entrada inválida |
| 401 | Autenticación ausente o inválida |
| 403 | Permiso técnico insuficiente |
| 404 | Objeto no localizado en el alcance autorizado |
| 409 | Conflicto de contenido, versión o estado |
| 422 | Impedimento funcional |
| 503 | Dependencia necesaria indisponible |

Una comprobación completada con resultado negativo puede responder 200. La insuficiencia de facultades de negocio se
distingue del permiso técnico para consultar.

Problem Details incluye código, correlación, actuación cuando exista y observaciones permitidas.

Los códigos principales son:

- CONTENT_CONFLICT.

- VERSION_CONFLICT.

- APPROVAL_REQUIRED.

- APPROVAL_WITHDRAWN.

- AUTHORITY_INSUFFICIENT.

- DEPENDENCY_PENDING.

- INVALID_EFFECTIVE_INTERVAL.

- INCOMPATIBLE_OVERLAP.

- CALENDAR_COVERAGE_MISSING.

- COMMERCIAL_CONFIGURATION_INCOMPLETE.

- PREPARATION_CONTENT_CONFLICT.

- INCOMPATIBLE_CHANGE_PENDING.

- DECISION_PENDING.

Una respuesta 202 incorpora seguimiento. La indisponibilidad posterior no elimina lo aceptado.

## 13. Seguridad e integraciones

### 13.1. Permisos

Se separan permisos para:

- Lectura institucional.

- Administración institucional.

- Comprobación de facultades.

- Lectura y administración de catálogo.

- Elegibilidad.

- Administración y determinación comercial.

- Lectura, administración y cálculo de calendarios.

- Preparación de condiciones para coordinadores autorizados.

- Notificación de decisiones propias.

- Consulta de actuaciones autorizadas.

La capacidad técnica de solicitar una preparación no concede facultades al actor evaluado.

Las peticiones de aborto se autentican y vinculan a una preparación existente. No permiten cancelar operaciones ajenas
ni borrar decisiones confirmadas.

### 13.2. Dependencias

| Proveedor | Contratos | Uso |
| --- | --- | --- |
| Gestión administrativa | CI-12-010 a CI-12-014 | Expedientes, tareas, aprobación y seguimiento |
| Documentos y comprobantes | CI-12-017 y CI-12-019 | Evidencias |
| Comunicaciones | CI-12-028 y CI-12-031 | Entrega y resultado |
| Personas | CI-02-002 | Identidad mínima |
| Clientes | CI-02-015 | Relación comercial |
| Representación | CI-02-021 | Representación |
| Contratos | CI-03-002 y CI-03-022 | Modelos y acuerdos |
| Riesgos | CI-11-002 | Políticas y límites |
| Conocimiento del cliente | CI-11-016 | Antecedentes pertinentes |
| Restricciones | CI-11-027 | Comprobación informativa de impedimentos |
| Criterios tributarios | CI-11-033 | Tratamiento tributario |
| Registración contable | CI-09-004 | Reglas para habilitación |

Las capacidades operativas se verifican con el responsable identificado en el producto.

Cuando una actuación exige preparación de otra autoridad, se utiliza su protocolo explícito. Una consulta informativa no
sustituye esa participación.

## 14. Redis y documentos

Redis puede almacenar versiones publicadas e inmutables de productos, ofertas y calendarios, y resultados deterministas
de fechas.

La clave incorpora objeto, versión y parámetros. El TTL inicial es quince minutos.

La pérdida de caché se resuelve consultando la fuente.

La caché no decide vigencia actual de facultades, habilitación, aplicabilidad comercial, preparaciones ni restricciones.

Los cuatro servicios utilizan Documentos y comprobantes para contenido documental y conservan referencias versionadas.
No escriben directamente en MinIO.

## 15. Trabajos y recuperación

Los trabajos persistentes cubren:

- Aplicaciones futuras.

- Dependencias pendientes.

- Publicación de outbox.

- Actuaciones interrumpidas.

- Seguimiento de vigencias.

- Comunicaciones.

- Preparaciones sin decisión conocida.

- Resolución de cambios incompatibles.

Cada trabajo conserva identidad, objeto, versión, actuación, próxima ejecución, intentos y propiedad temporal.

Un trabajador atrasado no confirma una propuesta sustituida.

Las consultas vigentes utilizan los intervalos reales aunque una proyección o tarea de mantenimiento no se haya
actualizado.

La expiración de la propiedad del trabajador no libera una preparación.

Las respuestas perdidas se recuperan mediante la actuación original o la consulta de preparación. No se generan
versiones o intentos nuevos para reemplazar resultados desconocidos.

## 16. Observabilidad

Se observan:

- Latencia y errores.

- Actuaciones por estado.

- Dependencias pendientes y antigüedad.

- Conflictos.

- Trabajos vencidos.

- Outbox pendiente.

- Duplicados.

- Falta de cobertura temporal.

- Ambigüedades comerciales.

- Uso de caché.

- Preparaciones pendientes.

- Abortos solicitados.

- Decisiones sin recuperar.

- Cambios con aplicación parcial.

Las trazas relacionan propuesta, aprobación, preparación, decisión y evento.

Los registros excluyen documentos, credenciales y legajos completos.

Los identificadores personales y de operaciones no se utilizan como etiquetas de métricas.

## 17. Pruebas y aceptación

| Área | Resultado exigido |
| --- | --- |
| Constitución | Una sola aplicación y recuperación idempotente |
| Jerarquía | Cambios concurrentes sin ciclos |
| Delegación | Alcance, importe y vigencia dentro del fundamento |
| Identidad | Actor y adscripción comprobados |
| Consulta | Ninguna preparación creada como efecto oculto |
| Preparación | Vinculación exacta con intento, contenido y condición |
| Revocación previa | Impide preparación incompatible |
| Revocación concurrente | Orden resuelto frente a la decisión del coordinador |
| Delegación dependiente | Cambio del fundamento alcanza sus preparaciones |
| Aborto | Un intento abortado no puede confirmarse |
| Vencimiento | No libera una preparación con decisión desconocida |
| Indisponibilidad | Mantiene seguimiento sin presumir resultado |
| Aprobación | Cambio de contenido exige nueva aprobación |
| Versionado | Conflictos sin sobrescritura |
| Habilitación | Dependencia faltante impide disponibilidad completa |
| Producto y oferta | Preparación conjunta sin contratación anticipada |
| Condiciones | Cero distinto de ausencia |
| Beneficios | Acumulación solo cuando está permitida |
| Prioridad | Empate incompatible impide publicación |
| Conservación comercial | Respeta fundamento, límite y contenido |
| Vigencia | Inicio incluido y final excluido |
| Calendario | Excepción prevalece y contradicción impide publicación |
| Series | Conservan el anclaje original |
| Cobertura | No inventa fechas faltantes |
| Eventos | Duplicados sin repetir efectos |
| Recuperación | Resultado original después de una respuesta perdida |
| Redis | Su falla no modifica autoridad |
| Documentos | Recepción documental distinta de aprobación |

Las pruebas de unicidad, bloqueo, orden y commit utilizan PostgreSQL real aislado.

Los simuladores permiten interrumpir cada intercambio y comprobar recuperación, resultados tardíos y competencia entre
aborto y confirmación.

## 18. Correspondencia funcional

| Operación | Contratos |
| --- | --- |
| OF-01-01. Definición institucional | CI-01-001, CI-01-002, CI-01-016, CI-01-020 |
| OF-01-02. Unidades y sucursales | CI-01-001, CI-01-002, CI-01-016, CI-01-020 |
| OF-01-03. Facultades | CI-01-003, CI-01-016, CI-01-021 |
| OF-01-04. Comprobación de facultad | CI-01-004 |
| OF-01-05. Definición de productos | CI-01-005, CI-01-006, CI-01-017, CI-01-022 |
| OF-01-06. Habilitación | CI-01-007, CI-01-017, CI-01-023 |
| OF-01-07. Elegibilidad | CI-01-008 |
| OF-01-08. Condiciones comerciales | CI-01-009, CI-01-018 |
| OF-01-09. Publicación de ofertas | CI-01-010, CI-01-012, CI-01-018, CI-01-024 |
| OF-01-10. Determinación comercial | CI-01-011, CI-01-012 |
| OF-01-11. Calendarios | CI-01-013, CI-01-014, CI-01-019, CI-01-025 |
| OF-01-12. Fechas y series | CI-01-015 |

Las preparaciones concretan la coordinación técnica de las condiciones utilizadas por estas operaciones. Conservan sus
responsables y no crean una segunda autoridad sobre facultades, productos o condiciones comerciales.
