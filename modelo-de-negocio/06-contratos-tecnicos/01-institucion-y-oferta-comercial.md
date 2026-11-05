# **Contratos técnicos — Módulo 1. Institución y oferta comercial**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**            | **Responsabilidad**                                                           |
|-------------------------|-------------------------------------------------------------------------------|
| Institución             | Identidad institucional, organización, funciones, adscripciones y facultades. |
| Catálogo de productos   | Definición, compatibilidad, requisitos y habilitación de productos.           |
| Condiciones comerciales | Condiciones, ofertas y determinaciones comerciales.                           |
| Calendarios bancarios   | Calendarios, convenciones y cálculos de fechas.                               |

Comprende los contratos **CI-01-001 a CI-01-025** y las operaciones de preparación necesarias para decisiones
coordinadas.

Cada servicio prepara y confirma únicamente las condiciones de su competencia.

Una consulta no crea reservas, compromisos ni autorizaciones permanentes. La aprobación de una definición no demuestra
que el producto esté habilitado operativamente.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las consultas informativas mediante POST no requieren clave de idempotencia ni producen preparaciones.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del servicio proveedor.

La consulta permite recuperar resultados utilizando la identidad conocida antes del envío, aunque se haya perdido la
respuesta con identificadores generados.

### **2.2. Tipos**

| **Tipo**          | **Representación y validación**                                       |
|-------------------|-----------------------------------------------------------------------|
| UUID              | Cadena UUID.                                                          |
| Version           | Entero entre 1 y 9.223.372.036.854.775.807.                           |
| Code              | Cadena de 1 a 80 caracteres: mayúsculas, números y guion bajo.        |
| ServiceCode       | Identificador técnico de servicio registrado, de hasta 80 caracteres. |
| Name              | Cadena de 1 a 200 caracteres.                                         |
| Description       | Cadena de hasta 4000 caracteres.                                      |
| Reason            | Cadena de 1 a 4000 caracteres.                                        |
| ExternalReference | Cadena de 1 a 120 caracteres.                                         |
| Instant           | Instante UTC.                                                         |
| Date              | Fecha YYYY-MM-DD.                                                     |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                 |
| Currency          | ARS o USD.                                                            |
| Amount            | Cadena decimal no negativa, precisión 24 y escala máxima 8.           |
| Ratio             | Cadena decimal con signo, precisión 24 y escala máxima 12.            |
| ZoneId            | Identificador IANA válido de hasta 80 caracteres.                     |

Los importes ejecutables respetan la escala del contrato monetario correspondiente. La precisión de una condición
comercial no autoriza a ejecutar fracciones inferiores al centavo.

El tipo Code no se aplica a nombres de eventos, rutas, identificadores IANA ni identificadores técnicos de servicio.

### **2.3. Obligatoriedad**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige expresamente contenido.

Los campos opcionales se omiten. No se admite null salvo definición expresa.

Las variantes excluyen campos pertenecientes a otras modalidades. Las propiedades desconocidas se rechazan.

## **3. Estructuras compartidas**

### **3.1. Referencias y contexto**

Se utilizan ObjectReference, VersionedReference, DocumentReference y ActionContext del documento principal.

En los comandos administrativos de este módulo, reason es obligatorio.

expectedVersion identifica la versión del recurso modificado. Las actuaciones sobre propuestas identifican además la
versión de propuesta correspondiente.

El actor y el servicio proceden de la identidad autenticada o de una delegación verificable.

### **3.2. Validity**

| **Campo**     | **Tipo** | **Condición**                  |
|---------------|----------|--------------------------------|
| effectiveFrom | Instant  | Obligatorio.                   |
| effectiveTo   | Instant  | Opcional; posterior al inicio. |

El inicio se incluye y el final se excluye.

### **3.3. Address**

Contiene:

- countryCode: dos letras mayúsculas.

- region: texto de 1 a 120 caracteres.

- locality: texto de 1 a 120 caracteres.

- addressLine: texto de 1 a 250 caracteres.

- postalCode: texto de 1 a 20 caracteres, opcional.

### **3.4. MoneyLimit**

Contiene currency y maximumAmount.

El importe admite cero. No se repite una moneda dentro del mismo conjunto.

### **3.5. Observation**

Contiene:

- code: Code.

- detail: texto de 1 a 1000 caracteres.

- field: texto de hasta 200 caracteres, cuando corresponde.

El detalle no revela información reservada innecesaria.

### **3.6. PendingDependency**

Contiene:

- dependencyCode: Code.

- responsibleService: ServiceCode.

- status: PENDING, UNCERTAIN o REQUIRES_REVIEW.

- reference: ObjectReference, cuando existe.

- lastCheckedAt: Instant, cuando fue comprobada.

- nextCheckAt: Instant, cuando existe revisión programada.

Los resultados comunes conservan pendingComponents. Cada dependencia pendiente se representa también mediante su código,
responsable y referencia en esa colección.

### **3.7. Resultados y metadatos**

Los comandos utilizan el resultado común. Sus resultados administrativos son:

APPLIED, REJECTED, WITHDRAWN, PENDING o RECORDED.

Se agregan changeId, effectiveFrom, pendingDependencies y observations cuando corresponden.

Las consultas contienen QueryMetadata:

| **Campo**     | **Tipo**                        |
|---------------|---------------------------------|
| evaluatedAt   | Instant                         |
| referenceTime | Instant                         |
| completeness  | COMPLETE, PARTIAL o UNAVAILABLE |
| missingData   | Code\[\]                        |

Las colecciones contienen items, metadatos y nextCursor cuando existe continuación.

## **4. Propuestas de cambio**

### **4.1. Colecciones e identificadores**

Cada colección ofrece seis operaciones.

| **Contrato** | **Colección**                    | **Prefijo de identificadores técnicos** |
|--------------|----------------------------------|-----------------------------------------|
| CI-01-002    | /v1/institution-changes          | InstitutionChange                       |
| CI-01-003    | /v1/authority-changes            | AuthorityChange                         |
| CI-01-006    | /v1/product-definition-changes   | ProductDefinitionChange                 |
| CI-01-007    | /v1/product-enablement-changes   | ProductEnablementChange                 |
| CI-01-009    | /v1/commercial-condition-changes | CommercialConditionChange               |
| CI-01-010    | /v1/commercial-offer-changes     | CommercialOfferChange                   |
| CI-01-013    | /v1/calendar-changes             | CalendarChange                          |

Los operationId se forman de manera exacta con la acción y el prefijo indicado:

| **Operación** | **Método y recurso relativo** | **Identificador**        |
|---------------|-------------------------------|--------------------------|
| Crear         | POST a la colección           | create + prefijo         |
| Consultar     | GET /{changeId}               | get + prefijo            |
| Editar        | PUT /{changeId}/draft         | update + prefijo + Draft |
| Presentar     | POST /{changeId}/submission   | submit + prefijo         |
| Aplicar       | POST /{changeId}/application  | apply + prefijo          |
| Retirar       | POST /{changeId}/withdrawal   | withdraw + prefijo       |

Por ejemplo, la aplicación de una habilitación utiliza applyProductEnablementChange.

### **4.2. ChangeRequest**

| **Campo**             | **Tipo**                    | **Condición**          |
|-----------------------|-----------------------------|------------------------|
| context               | ActionContext               | Obligatorio.           |
| changeType            | Enumeración de la colección | Obligatorio.           |
| targetId              | UUID                        | Para objeto existente. |
| expectedTargetVersion | Version                     | Para objeto existente. |
| validity              | Validity                    | Vigencia propuesta.    |
| payload               | Variante tipada             | Según changeType.      |

No se aceptan identificadores o versiones generados como sustitutos de la identidad de una propuesta nueva.

### **4.3. Change**

La consulta devuelve:

- changeId: UUID.

- proposalVersion: Version.

- changeType: modalidad.

- validity: Validity.

- payload: contenido tipado.

- contentHash: Hash.

- status: estado.

- createdAt y updatedAt: Instant.

- actionResult: resultado común.

- targetId y expectedTargetVersion, para objeto existente.

- approvalReference, cuando existe aprobación.

Los estados son:

DRAFT, PENDING_APPROVAL, APPROVED, SCHEDULED, PENDING_DEPENDENCIES, APPLIED, REQUIRES_REVIEW, REJECTED y WITHDRAWN.

### **4.4. Edición, presentación y aplicación**

Editar recibe contexto, expectedProposalVersion y contenido completo de la revisión.

Presentar recibe contexto, expectedProposalVersion y contentHash.

Aplicar recibe contexto, expectedProposalVersion, contentHash y approvalReference.

Retirar recibe contexto y expectedProposalVersion.

El contenido vinculante incluye colección, modalidad, objeto destinatario, versión esperada, vigencia y payload. Utiliza
la canonicalización común.

La aprobación corresponde a ese contenido exacto. La evidencia se obtiene mediante Gestión administrativa.

Una edición material deja sin aplicación la aprobación anterior. Una propuesta aplicada no se modifica; exige otro
cambio.

La aplicación y la retirada compiten sobre el mismo control local.

Crear una propuesta responde 201. El seguimiento durable pendiente responde 202. Una aplicación concluida o recuperación
responde 200.

## **5. Servicio Institución**

### **5.1. InstitutionVersion**

Contiene:

- institutionId: UUID generado.

- version: Version generada.

- legalName: Name.

- tradeName: Name, opcional.

- identifiers: InstitutionIdentifier\[\], no vacío.

- addresses: Address\[\], no vacío.

- activityCodes: Code\[\].

- validity: Validity.

- foundationDocument: DocumentReference.

- changeId: UUID generado.

Cada identificador contiene type, value y jurisdictionCode. El valor es texto y conserva ceros iniciales.

### **5.2. Organización, funciones y adscripciones**

OrganizationalUnit contiene:

- unitId y version, generados.

- code: Code.

- name: Name.

- unitType: ORGANIZATIONAL_UNIT o BRANCH.

- parentUnitId: UUID, salvo unidad raíz.

- address: Address, obligatorio para sucursal.

- functionCodes: Code\[\].

- status: ACTIVE, CLOSING o INACTIVE.

- validity: Validity.

- continuityAssignments: ContinuityAssignment\[\].

Cada continuidad contiene taskReference, responsibleUnitId y treatmentCode.

InstitutionalFunction contiene functionId, version, code, name, unitId y validity.

ActorAssignment contiene:

- assignmentId y version, generados.

- partyId: UUID.

- identityIssuer e identitySubject: textos de 1 a 255 caracteres.

- functionId y unitId: UUID.

- validity: Validity.

- basisDocument: DocumentReference.

Los datos de identidad no son credenciales.

### **5.3. AuthorityGrant**

| **Campo**             | **Tipo**                  | **Condición**                  |
|-----------------------|---------------------------|--------------------------------|
| authorityId y version | UUID y Version            | Generados.                     |
| parentAuthorityId     | UUID                      | Para delegación.               |
| grantorAssignmentId   | UUID                      | Salvo fundamento constitutivo. |
| recipientFunctionId   | UUID                      | Obligatorio.                   |
| decisionCode          | Code                      | Obligatorio.                   |
| scope                 | AuthorityScope            | Obligatorio.                   |
| amountLimits          | MoneyLimit\[\]            | Obligatorio.                   |
| subdelegationAllowed  | Booleano                  | Obligatorio.                   |
| jointApprovalRule     | JointApprovalRule         | Para decisión conjunta.        |
| validity              | Validity                  | Obligatorio.                   |
| status                | ACTIVE, REVOKED o EXPIRED | Generado.                      |

AuthorityScope identifica institución y dimensiones de alcance.

Cada dimensión de unidades, productos u objetos declara:

- mode: ALL o LIST.

- Lista de identificadores o referencias, obligatoria y no vacía con LIST.

ALL debe estar permitido por la facultad de origen. Omitir una dimensión no significa alcance universal.

JointApprovalRule contiene functionIds, minimumDistinctActors e incompatibleFunctionPairs.

La cantidad mínima es positiva y no supera el conjunto de intervinientes admitidos.

### **5.4. Interfaces de consulta**

| **Contrato** | **operationId**             | **Método y ruta**                        |
|--------------|-----------------------------|------------------------------------------|
| CI-01-001    | getInstitution              | GET /v1/institution                      |
| CI-01-001    | listOrganizationalUnits     | GET /v1/organizational-units             |
| CI-01-001    | getOrganizationalUnit       | GET /v1/organizational-units/{unitId}    |
| CI-01-001    | listInstitutionalFunctions  | GET /v1/institutional-functions          |
| CI-01-001    | getActorAssignment          | GET /v1/actor-assignments/{assignmentId} |
| CI-01-004    | checkInstitutionalAuthority | POST /v1/authority-checks                |
| CI-01-016    | getInstitutionAction        | GET /v1/institution-actions/{actionId}   |

Las consultas reciben finalidad y, cuando admiten historia, version o referenceTime de forma excluyente.

Las colecciones utilizan paginación común.

### **5.5. CI-01-002 — Cambios institucionales**

| **changeType**        | **Payload**                                                |
|-----------------------|------------------------------------------------------------|
| ESTABLISH_INSTITUTION | Definición inicial, funciones, adscripciones y facultades. |
| UPDATE_INSTITUTION    | Nueva definición institucional completa.                   |
| CREATE_UNIT           | Definición de unidad.                                      |
| UPDATE_UNIT           | Nueva definición de unidad existente.                      |
| REORGANIZE_UNIT       | Unidad, nueva unidad superior y continuidades.             |
| CLOSE_UNIT            | Unidad y continuidades; fecha mediante validity.           |
| ADMINISTER_FUNCTION   | Modalidad CREATE, UPDATE o END y función.                  |
| ADMINISTER_ASSIGNMENT | Modalidad CREATE, UPDATE o END y adscripción.              |

Las modalidades END identifican objeto y fundamento; no exigen reenviar una definición completa.

La constitución inicial utiliza fundamento documental único y una identidad de inicialización previamente habilitada. La
aprobación separada se comprueba antes de aplicar.

No se admiten ciclos organizativos ni cierre sin tratamiento de responsabilidades pendientes.

### **5.6. CI-01-003 — Facultades**

Las modalidades son:

- GRANT: definición de facultad.

- MODIFY: facultad destinataria y definición propuesta.

- REVOKE: fundamento y alcance del retiro; objeto y momento mediante los campos comunes.

Revocar no exige reenviar límites.

La delegación no amplía actos, ámbitos, importes, vigencias ni posibilidad de subdelegación del fundamento.

Una modificación comprueba también facultades dependientes.

### **5.7. CI-01-004 — Comprobar facultad**

Entrada:

- assignmentId: UUID.

- decisionCode: Code.

- target: ObjectReference.

- decisionContentHash: Hash.

- referenceTime: Instant.

- purpose: Code.

- productId: cuando corresponde.

- amount y currency: juntos para acto monetario.

- actingParticipants: UUID\[\], para actuación conjunta.

Salida:

- checkId: UUID.

- result: SUFFICIENT, INSUFFICIENT o PENDING.

- authorityReferences: VersionedReference\[\].

- missingParticipants: UUID\[\].

- evaluatedContentHash: Hash.

- metadata: QueryMetadata.

- observations: Observation\[\].

La conclusión negativa responde 200. No crea preparación.

## **6. Servicio Catálogo de productos**

### **6.1. ProductDefinition**

Contiene:

- productId y definitionVersion, generados.

- code, name, familyCode y variantCode.

- currencies: Currency\[\], no vacío.

- participantTypes: Code\[\], no vacío.

- operationCodes: Code\[\].

- capabilities: CapabilityDefinition\[\].

- eligibilityRequirements: RequirementDefinition\[\].

- dependencies: ProductDependency\[\].

- compatibilityRules: CompatibilityRule\[\].

- commercialConcepts: Code\[\].

- contractingRequirements: RequirementDefinition\[\].

- terminationRules: TerminationRule\[\].

- validity: Validity.

Una capacidad contiene code, functionalVersion y responsibleService.

Una dependencia contiene identidad, etapa, responsable y modalidad excluyente:

- PRODUCT: requiredProductId.

- CAPABILITY: capabilityCode.

Una compatibilidad contiene ruleId, relatedProductId, relationType y requiredStage.

relationType admite REQUIRED, ALLOWED e INCOMPATIBLE.

Una regla de terminación contiene ruleId, triggerCode, treatmentCode y responsibleService.

Los tratamientos corresponden a comportamientos registrados. No contienen scripts.

### **6.2. RequirementDefinition**

| **Campo**      | **Tipo**                                     |
|----------------|----------------------------------------------|
| requirementId  | UUID                                         |
| attributeCode  | Code                                         |
| sourceService  | ServiceCode                                  |
| valueType      | TEXT, INTEGER, DECIMAL, DATE, BOOLEAN o CODE |
| operator       | Operador admitido                            |
| requiredStage  | Etapa                                        |
| missingResult  | PENDING o INELIGIBLE                         |
| expectedValues | Valores tipados, según operador              |

Los operadores son:

- EQUALS: un valor.

- IN: conjunto no vacío.

- GREATER_OR_EQUAL: un valor ordenable.

- LESS_OR_EQUAL: un valor ordenable.

- BETWEEN: dos extremos ordenados e inclusivos.

- PRESENT: sin valores esperados.

Las etapas son PROPOSAL, CELEBRATION, EFFECT, CONSTITUTION y USE.

Los operadores de orden admiten enteros, decimales y fechas. No se aplican a textos o booleanos.

### **6.3. ProductEnablement**

Contiene:

- productId.

- definitionVersion.

- enablementVersion, generada.

- scope: alcance de canales y segmentos.

- validity.

- status.

- previousProposalTreatment.

- dependencyConfirmations.

Cada dimensión de alcance declara ALL o LIST. Una lista es no vacía y ALL no admite valores adicionales.

Los estados son NOT_ENABLED, SCHEDULED, ENABLED, SUSPENDED y WITHDRAWN.

Cada confirmación contiene capacidad, responsable, referencia, fecha y resultado CONFIRMED, PENDING o REJECTED; agrega
vigencia cuando corresponde.

### **6.4. Interfaces**

| **Contrato** | **operationId**               | **Método y ruta**                               |
|--------------|-------------------------------|-------------------------------------------------|
| CI-01-005    | getProduct                    | GET /v1/products/{productId}                    |
| CI-01-005    | getProductVersion             | GET /v1/products/{productId}/versions/{version} |
| CI-01-006    | createProductDefinitionChange | POST /v1/product-definition-changes             |
| CI-01-007    | createProductEnablementChange | POST /v1/product-enablement-changes             |
| CI-01-008    | checkProductEligibility       | POST /v1/product-eligibility-checks             |
| CI-01-017    | getProductAction              | GET /v1/product-actions/{actionId}              |

Las colecciones de cambios incluyen las operaciones del apartado 4.

La definición admite CREATE y NEW_VERSION.

La habilitación admite ENABLE, SUSPEND, RESUME y WITHDRAW.

El tratamiento de propuestas anteriores es:

BLOCK_PENDING, ALLOW_WITHIN_EXISTING_VALIDITY o REVIEW_REQUIRED.

No extiende vigencias ni levanta impedimentos ajenos.

### **6.5. CI-01-008 — Elegibilidad**

Entrada:

- Producto y versión.

- Participantes con persona y rol.

- verifiedFacts: antecedentes tipados.

- relatedProducts: referencias y estados comprobados.

- Momento de evaluación.

- Finalidad.

Cada antecedente contiene atributo, tipo, valor, fuente, versión de fuente y fecha de observación. Agrega evidencia
cuando corresponde.

El valor utiliza un único campo según su tipo: texto, entero, decimal, fecha, booleano o código.

Salida:

- checkId: UUID.

- result: ELIGIBLE, INELIGIBLE o PENDING.

- requirementResults: requisito, resultado y fundamento.

- dependencyResults: dependencia, resultado y referencias.

- sourceReferences: ObjectReference\[\].

- metadata: QueryMetadata.

Los resultados individuales son SATISFIED, NOT_SATISFIED y PENDING.

La elegibilidad no concede crédito ni prepara habilitación.

## **7. Servicio Condiciones comerciales**

### **7.1. CommercialCondition**

Contiene:

- conditionId y version, generados.

- productId.

- compatibleProductVersions: Version\[\], no vacío.

- conceptCode.

- triggerCode.

- valueType.

- roundingMode: HALF_UP, UP o DOWN.

- finalScale: entero entre 0 y 8.

- requirements: RequirementDefinition\[\].

- priority: entero no negativo.

- validity.

- baseCode y periodicityCode, cuando el concepto los exige.

- Campos específicos de la modalidad.

| **valueType**      | **Datos exigidos**                                                                       |
|--------------------|------------------------------------------------------------------------------------------|
| FIXED_AMOUNT       | value: Amount y currency.                                                                |
| RATE               | value: Ratio, clase de tasa, período, base temporal y capitalización cuando corresponde. |
| PERCENTAGE         | value: Ratio y base de aplicación.                                                       |
| FORMULA_REFERENCE  | CalculationDefinition.                                                                   |
| EXTERNAL_REFERENCE | CalculationDefinition con fuente externa identificada.                                   |

Un importe destinado directamente a dinero final utiliza escala máxima 2. Una escala mayor solo corresponde a cálculos
intermedios o unidades que lo admiten.

### **7.2. CalculationDefinition**

Contiene mechanismCode, mechanismVersion y operands.

Cada operando identifica name, valueType y modalidad:

- CONSTANT: valor tipado.

- SOURCE_ATTRIBUTE: referencia de fuente y código de atributo.

El mecanismo determina nombres, tipos y obligatoriedad de sus operandos. Los desconocidos se rechazan.

No se ejecutan expresiones recibidas como texto.

### **7.3. CommercialOffer**

Contiene:

- offerId y version, generados.

- productId.

- compatibleProductVersions: Version\[\], no vacío.

- conditionReferences: VersionedReference\[\].

- scope: canales y segmentos con modalidad explícita ALL o LIST.

- currencies: Currency\[\], no vacío.

- priority: entero no negativo.

- preservationAllowed: Booleano.

- preservationDays: entero positivo, exclusivamente cuando se permite conservación.

- validity.

- status, generado.

- previousProposalTreatment, cuando sustituye o retira una oferta.

Los estados son DRAFT, APPROVED, SCHEDULED, PUBLISHED y WITHDRAWN.

preservationDays se expresa en días corridos.

### **7.4. Interfaces**

| **Contrato** | **operationId**                 | **Método y ruta**                           |
|--------------|---------------------------------|---------------------------------------------|
| CI-01-009    | createCommercialConditionChange | POST /v1/commercial-condition-changes       |
| CI-01-010    | createCommercialOfferChange     | POST /v1/commercial-offer-changes           |
| CI-01-011    | determineCommercialTerms        | POST /v1/commercial-determinations          |
| CI-01-012    | getCommercialOffer              | GET /v1/offers/{offerId}                    |
| CI-01-012    | getCommercialOfferVersion       | GET /v1/offers/{offerId}/versions/{version} |
| CI-01-018    | getCommercialAction             | GET /v1/commercial-actions/{actionId}       |

Las condiciones admiten CREATE y NEW_VERSION.

Las ofertas admiten PUBLISH, REPLACE y WITHDRAW.

REPLACE identifica oferta anterior y nueva definición. WITHDRAW identifica versión retirada y tratamiento de propuestas
anteriores.

Un empate incompatible impide aplicar la configuración.

### **7.5. CI-01-011 — Determinación comercial**

Entrada:

| **Campo**      | **Tipo**             | **Condición**                   |
|----------------|----------------------|---------------------------------|
| productId      | UUID                 | Obligatorio.                    |
| productVersion | Version              | Obligatorio.                    |
| referenceTime  | Instant              | Obligatorio.                    |
| channelCode    | Code                 | Obligatorio.                    |
| currency       | Currency             | Obligatorio.                    |
| verifiedFacts  | Antecedentes tipados | Obligatorio.                    |
| purpose        | Code                 | Obligatorio.                    |
| segmentCode    | Code                 | Cuando interviene segmentación. |
| amount         | Amount               | Para condiciones por importe.   |
| term           | Term                 | Para condiciones por plazo.     |

Term contiene cantidad positiva y unidad CALENDAR_DAYS, BUSINESS_DAYS o MONTHS.

Salida:

- determinationId: UUID.

- result: RESOLVED, NO_OFFER, PENDING o CONFLICT.

- evaluatedContentHash: Hash.

- productReference: VersionedReference.

- offerReferences: VersionedReference\[\].

- resolvedConditions: condiciones resueltas.

- missingRequirements: UUID\[\].

- preservation: resultado de conservación.

- metadata: QueryMetadata.

Cada condición resuelta conserva referencia versionada, concepto, modalidad de valor y sus campos específicos. Agrega
futureDependencyReferences cuando utiliza valores futuros.

preservation contiene allowed. Si es verdadero, agrega maximumDays y validUntil.

El menor número de prioridad prevalece. Un empate de condiciones incompatibles produce CONFLICT.

La determinación no constituye una reserva de oferta.

## **8. Servicio Calendarios bancarios**

### **8.1. BankingCalendarVersion**

Contiene:

- calendarId y version, generados.

- code, name, purposeCode y jurisdictionCode.

- zoneId.

- coverageFrom y coverageTo: Date.

- ordinaryBusinessDays: enteros del 1 al 7, sin duplicados.

- exceptions: CalendarException\[\].

- sourceReference: ExternalReference.

- validity.

- status, generado.

La numeración semanal utiliza lunes igual a 1 y domingo igual a 7.

La cobertura incluye ambos extremos.

Cada excepción contiene fecha, clasificación, fundamento y fuente.

classification admite BUSINESS_DAY y NON_BUSINESS_DAY. La excepción prevalece sobre la clasificación semanal ordinaria.

Una fecha no admite excepciones contradictorias en la misma versión.

Los estados son DRAFT, APPROVED, SCHEDULED, PUBLISHED y RETIRED.

### **8.2. Interfaces**

| **Contrato** | **operationId**           | **Método y ruta**                                 |
|--------------|---------------------------|---------------------------------------------------|
| CI-01-013    | createCalendarChange      | POST /v1/calendar-changes                         |
| CI-01-014    | getBankingCalendar        | GET /v1/calendars/{calendarId}                    |
| CI-01-014    | getBankingCalendarVersion | GET /v1/calendars/{calendarId}/versions/{version} |
| CI-01-014    | listCalendarConventions   | GET /v1/calendar-conventions                      |
| CI-01-015    | calculateBankingDates     | POST /v1/date-calculations                        |
| CI-01-019    | getCalendarAction         | GET /v1/calendar-actions/{actionId}               |

Los cambios admiten CREATE, NEW_VERSION y RETIRE.

Retirar no borra historia ni modifica obligaciones existentes automáticamente.

### **8.3. CI-01-015 — Cálculo de fechas**

La entrada utiliza operation como discriminador.

| **Modalidad**   | **Campos específicos**                                        |
|-----------------|---------------------------------------------------------------|
| CLASSIFY        | Referencia temporal y calendario versionado.                  |
| SHIFT           | Referencia temporal, desplazamiento, unidad y ajuste.         |
| ADJUST          | Referencia temporal, calendario y ajuste.                     |
| GENERATE_SERIES | Anclaje, frecuencia, unidad, índice inicial, límite y ajuste. |

La referencia temporal utiliza referenceDate o referenceInstant, de forma excluyente. Un instante exige zoneId.

displacement es entero con signo.

Las unidades son CALENDAR_DAYS, BUSINESS_DAYS y MONTHS.

Las convenciones son NONE, FOLLOWING, PRECEDING y MODIFIED_FOLLOWING.

Una serie contiene:

- anchorDate: Date.

- frequency: entero positivo.

- unit.

- firstOccurrenceIndex: entero no negativo.

- anchorMode: DAY_OF_MONTH o END_OF_MONTH, para meses.

- occurrenceCount: entero positivo, o endDate, de forma excluyente.

- adjustment.

El índice cero corresponde al anclaje. endDate limita las fechas contractuales de forma inclusiva.

El calendario es obligatorio para clasificación, días hábiles o ajuste distinto de NONE.

Salida:

- calculationId: UUID.

- result: COMPLETE o INCOMPLETE_COVERAGE.

- inputHash: Hash.

- zoneId: ZoneId.

- occurrences: DateOccurrence\[\].

- missingRanges: DateRange\[\].

- calendarReference: cuando se utilizó.

Cada ocurrencia contiene índice y fecha contractual. Agrega fecha ajustada cuando puede determinarse y clasificación
cuando fue comprobada.

Cada rango faltante contiene from y to, inclusivos.

Una entrada estructuralmente inválida responde 400. Una cobertura insuficiente responde 200 con INCOMPLETE_COVERAGE; no
inventa fechas definitivas.

### **8.4. Reglas**

El desplazamiento precede al ajuste.

El conteo de días hábiles excluye la fecha inicial.

Las series mensuales se calculan desde el anclaje original. Si el día no existe, utilizan el último día del mes sin
cambiar el anclaje de los meses siguientes.

END_OF_MONTH exige anclaje de fin de mes.

Los cálculos sobre versiones inmutables no requieren preparación persistente.

## **9. Preparaciones de autoridades**

### **9.1. Interfaces**

| **Servicio**            | **operationId**                      | **Método y ruta**                                                              |
|-------------------------|--------------------------------------|--------------------------------------------------------------------------------|
| Institución             | prepareInstitutionalAuthority        | POST /v1/authority-preparations                                                |
| Institución             | getInstitutionalAuthorityPreparation | GET /v1/authority-preparations/{preparationId}                                 |
| Institución             | notifyInstitutionalAuthorityDecision | POST /v1/authority-preparations/{preparationId}/decision-notifications         |
| Catálogo                | prepareProductAction                 | POST /v1/product-action-preparations                                           |
| Catálogo                | getProductActionPreparation          | GET /v1/product-action-preparations/{preparationId}                            |
| Catálogo                | notifyProductActionDecision          | POST /v1/product-action-preparations/{preparationId}/decision-notifications    |
| Condiciones comerciales | prepareCommercialAction              | POST /v1/commercial-action-preparations                                        |
| Condiciones comerciales | getCommercialActionPreparation       | GET /v1/commercial-action-preparations/{preparationId}                         |
| Condiciones comerciales | notifyCommercialActionDecision       | POST /v1/commercial-action-preparations/{preparationId}/decision-notifications |

### **9.2. Entrada**

Utiliza la solicitud común de preparación:

- Contexto.

- Coordinador e intento.

- Referencia de consulta de decisión.

- Acto, actores y objetos.

- Contenido vinculante y huella.

- Límite de decisión.

- Importes y monedas pertinentes.

- Evidencias.

Se agregan los datos de competencia:

| **Autoridad**           | **Campos específicos**                                                                                              |
|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| Institución             | assignmentId, decisionCode, target, participantes conjuntos y referencias de facultades cuando se conocen.          |
| Catálogo                | productReference, requestedScope, antecedentes de elegibilidad y capacidades exigidas.                              |
| Condiciones comerciales | determinationReference, ofertas versionadas, propuesta relacionada y fundamento de conservación cuando corresponde. |

Una huella sin los datos necesarios para interpretar el acto no basta.

Las referencias de decisión se resuelven mediante destinos configurados y autorizados.

### **9.3. Respuesta y notificación**

La respuesta contiene:

- preparationId.

- actionId.

- coordinatorService.

- attemptId.

- contentHash.

- state.

- result: FAVORABLE, UNFAVORABLE o PENDING.

- scope: alcance preparado.

- controlReferences: VersionedReference\[\].

- decisionDeadline.

- statusResource.

- preparedAt, cuando es favorable.

- observations: Observation\[\].

Los estados son PREPARED, RESOLUTION_PENDING, CONCLUDED_COMMITTED, CONCLUDED_ABORTED y REJECTED.

La notificación recibe contexto, intento, huella, decisión, momento y referencias de resultado.

decision admite COMMITTED y ABORTED.

La autoridad comprueba coordinador, preparación, contenido y decisión durable. Una contradicción produce conflicto y
seguimiento.

### **9.4. Concurrencia y vencimiento**

Preparaciones y cambios incompatibles utilizan controles locales comunes.

Un impedimento anterior rechaza la preparación.

Un cambio posterior:

1.  Registra recepción y momento de efecto.

2.  Bloquea nuevas preparaciones incompatibles.

3.  Identifica intentos existentes.

4.  Consulta o solicita resolver sus decisiones.

5.  Conserva pendientes hasta conocer el resultado.

No se mantiene una transacción SQL durante esas consultas.

El límite de decisión no puede superar un impedimento futuro conocido que deba impedir el acto.

Las preparaciones no se liberan por timeout. La indisponibilidad mantiene resolución pendiente y escalación.

El alcance bloqueado se limita al conflicto existente.

## **10. Eventos**

### **10.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                         | **Tema**                   | **Agregado** |
|--------------|----------------------------------|----------------------------|--------------|
| CI-01-020    | institution.changed.v1           | institution.events.v1      | Institución. |
| CI-01-021    | institution.authority-changed.v1 | institution.events.v1      | Facultad.    |
| CI-01-022    | product.definition-approved.v1   | product-catalog.events.v1  | Producto.    |
| CI-01-023    | product.enablement-changed.v1    | product-catalog.events.v1  | Producto.    |
| CI-01-024    | commercial-offer.changed.v1      | commercial-terms.events.v1 | Oferta.      |
| CI-01-025    | banking-calendar.changed.v1      | banking-calendar.events.v1 | Calendario.  |

La versión inicial de esquema es 1.

La clave de partición es aggregateId.

Los valores de aggregateType son INSTITUTION, INSTITUTIONAL_AUTHORITY, PRODUCT, COMMERCIAL_OFFER y BANKING_CALENDAR.

### **10.2. CI-01-020 — Cambio institucional**

Payload:

| **Campo**           | **Tipo**                          | **Condición**             |
|---------------------|-----------------------------------|---------------------------|
| institutionId       | UUID                              | Obligatorio.              |
| institutionVersion  | Version                           | Obligatorio.              |
| changeId            | UUID                              | Obligatorio.              |
| changeType          | Modalidad de cambio institucional | Obligatorio.              |
| affectedObject      | ObjectReference                   | Obligatorio y versionado. |
| effectiveFrom       | Instant                           | Obligatorio.              |
| pendingDependencies | PendingDependency\[\]             | Obligatorio.              |

El agregado es la institución aunque el objeto afectado sea una unidad, función o adscripción.

La versión del agregado avanza con cada cambio institucional registrado; no sustituye la versión del objeto afectado.

### **10.3. CI-01-021 — Cambio de facultad**

Payload obligatorio:

- authorityId: UUID.

- authorityVersion: Version.

- changeId: UUID.

- changeType: GRANT, MODIFY o REVOKE.

- recipientFunctionId: UUID.

- decisionCode: Code.

- scope: AuthorityScope.

- amountLimits: MoneyLimit\[\].

- validity: Validity.

- status: estado de facultad.

- dependentAuthorityIds: UUID\[\].

- pendingDependencies: PendingDependency\[\].

Incluye parentAuthorityId cuando existe delegación.

Revocar una facultad no afirma que todos los intentos preparados hayan sido abortados.

### **10.4. CI-01-022 — Definición aprobada**

Payload obligatorio:

- productId: UUID.

- definitionVersion: Version.

- changeId: UUID.

- approvalReference: ObjectReference.

- definitionReference: VersionedReference.

- capabilities: CapabilityDefinition\[\].

- validity: Validity.

La aprobación no comunica habilitación operativa.

### **10.5. CI-01-023 — Cambio de habilitación**

Payload obligatorio:

- productId: UUID.

- definitionVersion: Version.

- enablementVersion: Version.

- changeId: UUID.

- status: estado de habilitación.

- scope: alcance explícito.

- effectiveFrom: Instant.

- previousProposalTreatment: tratamiento definido.

- dependencyConfirmations: confirmaciones tipadas.

- pendingDependencies: PendingDependency\[\].

Cada componente distingue confirmación y pendiente. No se informa aplicación integral si faltan dependencias
obligatorias.

### **10.6. CI-01-024 — Cambio de oferta**

Payload obligatorio:

- offerId: UUID.

- offerVersion: Version.

- productId: UUID.

- changeId: UUID.

- changeType: PUBLISH, REPLACE o WITHDRAW.

- status: estado de oferta.

- validity: Validity.

- preservationAllowed: Booleano.

- previousProposalTreatment: tratamiento definido.

- pendingDependencies: PendingDependency\[\].

Campos condicionales:

- preservationDays: si se permite conservación.

- previousOfferReference: para sustitución.

El evento no extiende por sí solo la vigencia de una propuesta.

### **10.7. CI-01-025 — Cambio de calendario**

Payload obligatorio:

- calendarId: UUID.

- calendarVersion: Version.

- changeId: UUID.

- changeType: CREATE, NEW_VERSION o RETIRE.

- status: estado de calendario.

- validity: Validity.

- coverageFrom y coverageTo: Date.

- affectedDateRanges: DateRange\[\].

Incluye previousVersion cuando existe una versión anterior.

Los rangos incluyen ambos extremos. Si solo cambia la situación administrativa, la colección puede estar vacía.

El evento no reprograma obligaciones existentes automáticamente.

### **10.8. Consumidores y recuperación**

| **Evento**          | **Consumidores**                                              |
|---------------------|---------------------------------------------------------------|
| Institución         | Gestión administrativa y servicios afectados.                 |
| Facultades          | Servicios que utilizan decisiones institucionales.            |
| Definición aprobada | Condiciones comerciales, Contratos y propietarios operativos. |
| Habilitación        | Contratos, Solicitudes de productos y servicios afectados.    |
| Oferta              | Catálogo, Contratos y Solicitudes de productos.               |
| Calendario          | Servicios que calculan o administran fechas.                  |

Las suscripciones se autorizan por finalidad.

Los consumidores que mantienen proyecciones comparan versiones. Un mensaje repetido no reaplica el cambio. Un mensaje
anterior no sustituye una versión posterior.

Ante una discontinuidad relevante, el consumidor consulta la versión autoritativa antes de utilizar su proyección para
una decisión dependiente.

Los eventos no sustituyen el protocolo durable de preparaciones.

## **11. Seguridad y recuperación**

Se separan permisos para:

- Lectura institucional.

- Administración de organización y adscripciones.

- Administración y comprobación de facultades.

- Administración de catálogo.

- Elegibilidad.

- Administración y determinación comercial.

- Administración y cálculo de calendarios.

- Preparaciones y notificaciones.

- Consulta de actuaciones.

Cada permiso se combina con objeto, finalidad y facultades de negocio.

La autoridad preparadora no representa a Personas, Clientes, Representación o Restricciones.

Los cambios conservan propuesta, revisión, aprobación, aplicación e historial.

Cada servicio utiliza outbox, inbox, idempotencia y trabajos persistentes.

La transacción local confirma objeto, versión, resultado e intención de publicación.

Una respuesta perdida se recupera mediante la actuación original. No se crea otro cambio para evitar consultar el
anterior.

Las autorizaciones también se comprueban en consultas históricas y resultados idempotentes.

## **12. Errores particulares**

| **Código**                          | **HTTP** | **Significado**                             |
|-------------------------------------|----------|---------------------------------------------|
| CONTENT_CONFLICT                    | 409      | Misma identidad con contenido diferente.    |
| VERSION_CONFLICT                    | 409      | Versión incompatible.                       |
| APPROVAL_REQUIRED                   | 422      | Falta aprobación exigida.                   |
| APPROVAL_CONTENT_MISMATCH           | 409      | Aprobación de otro contenido.               |
| APPROVAL_WITHDRAWN                  | 422      | Aprobación no utilizable.                   |
| INSTITUTION_ALREADY_ESTABLISHED     | 409      | Existe constitución institucional previa.   |
| AUTHORITY_INSUFFICIENT              | 422      | Falta facultad para ejecutar una actuación. |
| DELEGATION_SCOPE_EXCEEDED           | 422      | La delegación excede el fundamento.         |
| DELEGATION_CYCLE                    | 422      | Cadena circular de delegaciones.            |
| ORGANIZATIONAL_CYCLE                | 422      | Jerarquía circular.                         |
| CONTINUITY_REQUIRED                 | 422      | Falta tratamiento de responsabilidades.     |
| INCOMPATIBLE_OVERLAP                | 409      | Vigencias incompatibles.                    |
| COMMERCIAL_CONFIGURATION_INCOMPLETE | 422      | Falta definición esencial.                  |
| CALENDAR_COVERAGE_MISSING           | 422      | Una actuación exige cobertura completa.     |
| PREPARATION_CONTENT_CONFLICT        | 409      | Preparación con contenido incompatible.     |
| INCOMPATIBLE_CHANGE_PENDING         | 409      | Cambio incompatible todavía en resolución.  |
| DEPENDENCY_UNAVAILABLE              | 503      | Dependencia necesaria indisponible.         |

Una comprobación negativa completada responde 200 con su resultado.

El error de insuficiencia se utiliza cuando rechaza una actuación que necesita esa facultad.

La cobertura parcial de un cálculo responde 200 con INCOMPLETE_COVERAGE. El error 422 corresponde a una actuación que
exige cobertura completa.

Un procesamiento durable pendiente responde 202 con seguimiento.

## **13. Escenarios de aceptación**

| **Escenario**                                     | **Resultado exigido**                                            |
|---------------------------------------------------|------------------------------------------------------------------|
| Actor excede su límite                            | La consulta informa insuficiencia sin preparar.                  |
| Delegación intenta ampliar alcance                | Se rechaza.                                                      |
| Unidad se cierra con responsabilidades pendientes | Exige continuidad.                                               |
| Cambia el contenido aprobado                      | La aprobación anterior no habilita aplicarlo.                    |
| Se pierde la respuesta de aplicación              | La consulta por actionId recupera el cambio original.            |
| Revocación compite con preparación                | Se ordenan sobre controles locales comunes.                      |
| Revocación posterior a preparación                | Bloquea nuevas preparaciones y resuelve los intentos existentes. |
| Coordinador ya confirmó                           | Se conserva su decisión y el tratamiento posterior.              |
| Coordinador indisponible                          | Mantiene preparación y escalación.                               |
| Definición aprobada sin dependencias operativas   | No se presenta el producto como habilitado.                      |
| Dos ofertas incompatibles empatan en prioridad    | Se informa conflicto.                                            |
| Conservación comercial autorizada                 | Se respeta el límite sin extender otras vigencias.               |
| Serie mensual anclada al día 31                   | Ajusta meses cortos y conserva el anclaje siguiente.             |
| Falta cobertura de calendario                     | No produce fechas definitivas fuera de cobertura.                |
| Evento duplicado o atrasado                       | No repite el efecto ni reemplaza una versión posterior.          |

Las pruebas de unicidad, aplicación y concurrencia utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, respuestas, errores, canonicalización y eventos contra sus esquemas.
