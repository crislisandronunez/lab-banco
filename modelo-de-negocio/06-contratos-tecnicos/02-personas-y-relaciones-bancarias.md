# **Contratos técnicos — Módulo 2. Personas y relaciones bancarias**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**   | **Responsabilidad**                                                                    |
|----------------|----------------------------------------------------------------------------------------|
| Personas       | Datos personales, identificadores, contactos, relaciones, duplicados y circunstancias. |
| Vinculación    | Solicitudes de ingreso, requisitos, decisión de admisión y confirmación de su uso.     |
| Clientes       | Constitución, períodos, revisión y continuidad de la relación comercial.               |
| Representación | Reconocimiento, alcance, continuidad y terminación de facultades representativas.      |

Comprende los contratos **CI-02-001 a CI-02-033** y las preparaciones de representación.

Registrar una persona, verificar identidad, admitir, constituir una relación comercial y reconocer representación son
hechos diferentes.

Ninguna interfaz de este módulo abre cuentas, concede crédito ni modifica saldos.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las búsquedas y comprobaciones informativas mediante POST no requieren clave de idempotencia ni crean preparaciones.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

Esta consulta recupera los identificadores generados aunque se pierda la respuesta inicial.

### **2.2. Tipos**

| **Tipo**          | **Representación**                                                |
|-------------------|-------------------------------------------------------------------|
| UUID              | Identificador interno.                                            |
| Version           | Entero positivo dentro del rango de long.                         |
| Code              | Cadena de 1 a 80 caracteres.                                      |
| ServiceCode       | Identificador técnico de servicio registrado.                     |
| Date              | Fecha YYYY-MM-DD.                                                 |
| Instant           | Instante UTC.                                                     |
| Reason            | Cadena de 1 a 4000 caracteres.                                    |
| ExternalReference | Cadena de 1 a 120 caracteres.                                     |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                             |
| Currency          | ARS o USD.                                                        |
| Amount            | Cadena decimal no negativa, precisión 24 y escala máxima 8.       |
| Share             | Cadena decimal entre cero y uno, precisión 15 y escala máxima 12. |
| Quantity          | Cadena decimal no negativa, precisión 30 y escala máxima 12.      |

Los importes de ejecución respetan centavos. Los límites representativos no autorizan una escala incompatible con la
operación monetaria.

### **2.3. Límites de texto**

| **Dato**                              | **Límite**                          |
|---------------------------------------|-------------------------------------|
| Nombres y apellidos                   | De 1 a 150 caracteres por atributo. |
| Denominación jurídica                 | De 1 a 250 caracteres.              |
| Nombre comercial                      | De 1 a 250 caracteres.              |
| Identificador documental o tributario | De 1 a 120 caracteres.              |
| Emisor documental                     | De 1 a 120 caracteres.              |
| Correo                                | Hasta 254 caracteres.               |
| Teléfono normalizado                  | Hasta 20 caracteres.                |
| Línea de domicilio                    | De 1 a 250 caracteres.              |
| Región o localidad                    | De 1 a 120 caracteres.              |
| Código postal                         | De 1 a 20 caracteres.               |

No se recortan valores silenciosamente. Los identificadores oficiales se conservan como texto.

### **2.4. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige expresamente contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

Las consultas históricas admiten version o referenceTime, de forma excluyente. La autorización se comprueba también para
historia y recuperación idempotente.

## **3. Estructuras compartidas**

### **3.1. AttributeValue**

Los atributos que admiten información incompleta contienen:

| **Campo** | **Tipo**                                         | **Condición**                |
|-----------|--------------------------------------------------|------------------------------|
| state     | PROVIDED, UNKNOWN, NOT_PROVIDED o NOT_APPLICABLE | Obligatorio.                 |
| value     | Tipo del atributo                                | Exclusivamente con PROVIDED. |

No se utilizan valores ficticios para completar información desconocida.

Las observaciones y contradicciones se conservan separadamente.

### **3.2. SourceReference**

Contiene:

- sourceType: DECLARATION, DOCUMENT, SERVICE o ADMINISTRATIVE_RESOLUTION.

- sourceReference: ExternalReference.

- observedAt: Instant.

- sourceService: obligatorio para fuente de servicio.

- documentReference: cuando existe evidencia documental.

La procedencia declarada no se presenta como verificación independiente.

### **3.3. Address**

Contiene:

- addressType: Code.

- countryCode: dos letras mayúsculas.

- addressLines: entre una y tres líneas.

- validFrom: Date.

- source: SourceReference.

- region, locality y postalCode: cuando corresponden a la jurisdicción.

- validTo: Date, opcional y posterior al inicio.

- addressId: UUID generado.

Los campos no aplicables se omiten sin inventar valores.

### **3.4. QueryMetadata**

Contiene evaluatedAt, referenceTime, completeness y missingData.

completeness admite COMPLETE, PARTIAL y UNAVAILABLE.

La completitud se refiere al alcance solicitado y autorizado.

### **3.5. AdmissionScope**

Contiene:

- purposeCodes: Code\[\], no vacío.

- productScope: alcance de productos.

- channelScope: alcance de canales.

Cada alcance utiliza una modalidad excluyente:

- ALL_WITHIN_POLICY: referencia versionada de la política que lo permite.

- EXPLICIT_SET: referencias de productos o códigos de canal, según dimensión, en una colección no vacía.

Un ámbito omitido no significa autorización universal.

El uso solicitado debe estar contenido en el alcance admitido.

## **4. Servicio Personas**

### **4.1. Party**

| **Campo**             | **Tipo**                      | **Condición**              |
|-----------------------|-------------------------------|----------------------------|
| partyId               | UUID                          | Generado.                  |
| version               | Version                       | Generada.                  |
| partyType             | NATURAL_PERSON o LEGAL_ENTITY | Obligatorio.               |
| recordStatus          | Estado                        | Generado.                  |
| principalPartyId      | UUID                          | Si es referencia asociada. |
| naturalPerson         | NaturalPersonData             | Para persona humana.       |
| legalEntity           | LegalEntityData               | Para persona jurídica.     |
| identifiers           | PartyIdentifier\[\]           | Obligatorio.               |
| contacts              | PartyContact\[\]              | Obligatorio.               |
| addresses             | Address\[\]                   | Obligatorio.               |
| origin                | SourceReference               | Obligatorio.               |
| createdAt y updatedAt | Instant                       | Generados.                 |

Las variantes humana y jurídica son excluyentes.

Los estados son INCOMPLETE, REGISTERED, DUPLICATE_REVIEW y RESOLVED_ALIAS.

### **4.2. Datos personales**

NaturalPersonData contiene:

- givenNames: AttributeValue de texto.

- familyNames: AttributeValue de texto.

- birthDate: AttributeValue de Date.

Para registrar se exige al menos uno de los atributos de nombre informado. Los datos insuficientes mantienen el registro
incompleto.

Una fecha de nacimiento informada no puede ser futura.

LegalEntityData contiene:

- legalName: texto obligatorio.

- tradeName: texto opcional.

- legalForm: AttributeValue de Code.

- jurisdictionCode: Code.

- incorporationDate: AttributeValue de Date.

- constitutionalDocuments: DocumentReference\[\].

La fecha de constitución informada no puede ser futura.

### **4.3. Identificadores y contactos**

PartyIdentifier contiene:

- identifierType: Code.

- value: texto original.

- jurisdictionCode: Code.

- source: SourceReference.

- issuer: cuando lo exige el tipo.

- validFrom y validTo: Date, opcionales.

- verificationReference: cuando existe.

- identifierId, normalizedValue y observationStatus: generados.

observationStatus admite NONE, PENDING, CONTRADICTORY y EXPIRED.

La normalización depende del tipo registrado. Conserva el original y no convierte una coincidencia en identidad
confirmada.

PartyContact contiene:

- contactId y version, generados.

- contactType: EMAIL o PHONE.

- originalValue.

- normalizedValue, generado.

- validity: inicio y final opcional.

- source.

- status, generado.

Los estados son DECLARED, PENDING_CONFIRMATION, CONFIRMED, REJECTED y RETIRED.

Un teléfono normalizado utiliza formato internacional con prefijo de país. Un correo conserva su original; la
normalización no modifica arbitrariamente su parte local.

### **4.4. Interfaces de persona**

| **Contrato** | **operationId**              | **Método y ruta**                                         |
|--------------|------------------------------|-----------------------------------------------------------|
| CI-02-001    | registerOrRecoverParty       | POST /v1/parties                                          |
| CI-02-002    | getParty                     | GET /v1/parties/{partyId}                                 |
| CI-02-002    | searchParties                | POST /v1/party-searches                                   |
| CI-02-003    | changePartyData              | POST /v1/parties/{partyId}/changes                        |
| CI-02-003    | createContactConfirmation    | POST /v1/contact-confirmations                            |
| CI-02-003    | respondToContactConfirmation | POST /v1/contact-confirmations/{confirmationId}/responses |
| CI-02-022    | getPartyAction               | GET /v1/party-actions/{actionId}                          |

### **4.5. Registro y búsqueda**

Registrar recibe contexto y datos no generados de Party.

Devuelve resultado común, referencia de persona, referencia principal cuando existe y atributos faltantes.

outcome admite CREATED, EXISTING y MATCH_REVIEW_REQUIRED.

Se utiliza 201 para creación, 200 para recuperación y 202 para revisión durable pendiente.

Una coincidencia de nombre no fusiona registros.

La búsqueda recibe finalidad, atributos solicitados, paginación y criterio excluyente:

- OFFICIAL_IDENTIFIER: tipo, valor y jurisdicción.

- NAME_AND_ATTRIBUTES: nombre y atributos complementarios tipados.

- SOURCE_REFERENCE: fuente y referencia externa.

La respuesta contiene candidatos, razones de coincidencia y metadatos. No afirma identidad por similitud.

### **4.6. Actualización**

Cada cambio contiene:

- fieldCode: atributo registrado.

- operation: SET o RETIRE.

- source: SourceReference.

- evidenceReferences: DocumentReference\[\].

- value: variante tipada, exclusivamente con SET.

- elementId: para modificar o retirar un elemento de colección.

Los valores utilizan el esquema del atributo: datos humanos, datos jurídicos, identificador, contacto o domicilio.

No se admiten rutas arbitrarias de modificación.

La respuesta identifica componentes aplicados, pendientes y rechazados con sus motivos.

Cambiar un contacto genera otra versión y deja sin aplicación los desafíos del valor anterior.

### **4.7. Confirmación de contactos**

Crear recibe contexto, persona, contacto y versión.

Devuelve:

- confirmationId: UUID.

- maskedDestination: texto de hasta 254 caracteres.

- expiresAt: Instant.

- remainingAttempts: entero entre cero y cinco.

- status: PENDING, CONFIRMED, FAILED, EXPIRED o SUPERSEDED.

El desafío utiliza un código de seis dígitos enviado al contacto mediante Comunicaciones.

Vence a los diez minutos, admite cinco respuestas fallidas y se utiliza una sola vez.

Responder recibe contexto y proof, cadena de seis dígitos.

El secreto no aparece en respuestas, eventos ni registros. Se conserva mediante un verificador protegido.

Una nueva emisión para el mismo contacto y finalidad sustituye el desafío pendiente anterior. Un envío anterior tardío
no vuelve a habilitarlo.

Confirmar el contacto no verifica toda la identidad ni autoriza por sí solo una sustitución sensible.

## **5. Relaciones, duplicados y circunstancias**

### **5.1. Interfaces**

| **Contrato** | **operationId**           | **Método y ruta**                                     |
|--------------|---------------------------|-------------------------------------------------------|
| CI-02-004    | createPartyRelationship   | POST /v1/party-relationships                          |
| CI-02-004    | changePartyRelationship   | POST /v1/party-relationships/{relationshipId}/changes |
| CI-02-005    | listPartyRelationships    | GET /v1/parties/{partyId}/relationships               |
| CI-02-006    | createPartyDuplicateCase  | POST /v1/party-duplicate-cases                        |
| CI-02-006    | resolvePartyDuplicateCase | POST /v1/party-duplicate-cases/{caseId}/resolutions   |
| CI-02-007    | recordPartyCircumstance   | POST /v1/parties/{partyId}/circumstances              |

### **5.2. PartyRelationship**

Contiene:

- relationshipId y version, generados.

- sourcePartyId y targetPartyId.

- relationshipType: Code.

- validFrom: Date.

- validTo: Date, opcional.

- evidenceReferences: DocumentReference\[\].

- completeness: COMPLETE, INCOMPLETE u OBSERVED.

Para composición cuantificada agrega:

- measurementDate: Date.

- economicShare y votingShare: cuando corresponden.

- quantity y unitCode: juntos cuando corresponde.

- measurementBasis: ObjectReference.

Crear recibe contexto y definición. Cambiar recibe contexto y modalidad REPLACE_DEFINITION o END.

La consulta admite tipo, fecha y paginación.

Una relación societaria no concede representación.

### **5.3. Duplicados**

Crear caso recibe candidatos versionados, evidencias y persona principal propuesta cuando existe.

Resolver recibe contexto, conclusión y aprobación separada.

Las conclusiones son:

- SAME_PERSON: exige principalPartyId.

- DIFFERENT_PERSONS: no admite principal común.

- INCONCLUSIVE: conserva revisión pendiente.

La respuesta contiene caseId, conclusión, referencia principal cuando corresponde, referencias asociadas y
regularizaciones pendientes.

Las versiones de candidatos se comprueban bajo control local.

La resolución no fusiona productos, cuentas, deuda ni titularidades.

### **5.4. Circunstancias**

Recibe contexto, tipo, fecha del hecho, fecha de conocimiento, fuente y evidencias.

Los tipos son:

CAPACITY_CHANGE, DEATH_REPORTED, SUCCESSION, DISSOLUTION, LIQUIDATION y OTHER_DOCUMENTED_CIRCUMSTANCE.

Se agrega verificación especializada cuando existe.

Devuelve circumstanceId, condición de verificación, referencias de derivación y pendientes.

Comunicar un fallecimiento no demuestra que todas las medidas operativas hayan sido aplicadas.

## **6. Servicio Vinculación**

### **6.1. OnboardingApplication**

Contiene:

- applicationId y version, generados.

- partyId: UUID.

- applicationType: INITIAL o READMISSION.

- purposeCode: Code.

- declaredActivity: actividad y procedencia declaradas.

- contactReferences: referencias versionadas.

- representationReferences: cuando interviene representante.

- previousApplicationId: cuando existe antecedente.

- previousCustomerId: para readmisión.

- status y receivedAt, generados.

declaredActivity contiene activityCode, fundsOriginCode, source y descripción complementaria opcional.

Los estados son:

RECEIVED, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_DECISION, ACCEPTED, REJECTED, WITHDRAWN, EXPIRED y
REQUIRES_REVIEW.

### **6.2. Requerimientos**

Cada requerimiento contiene identidad, versión, causa, puntos, canal, puesta a disposición, calendario, vencimiento
original, vencimiento efectivo, recordatorio y referencias de presentaciones e incidencias.

Las causas son INITIAL, FINAL_CORRECTION y BANK_OMISSION.

Cada punto contiene itemId, requirementCode, responsibleService y estado:

PENDING, SUBMITTED, UNDER_REVIEW, SATISFIED u OBSERVED.

Se aplican treinta días corridos para el requerimiento inicial y quince para subsanación final. Los puntos nuevos
derivados de una omisión del banco reciben treinta días.

El cómputo comienza el día siguiente a la puesta a disposición. El vencimiento inhábil se traslada al siguiente hábil.

El recordatorio se genera cinco días corridos antes. Si el canal está indisponible al vencer, se extiende hasta
finalizar el segundo hábil posterior a su recuperación.

Una presentación oportuna impide declarar vencido el punto mientras se revisa.

### **6.3. Interfaces**

| **Contrato** | **operationId**             | **Método y ruta**                                                  |
|--------------|-----------------------------|--------------------------------------------------------------------|
| CI-02-008    | createOnboardingApplication | POST /v1/onboarding-applications                                   |
| CI-02-009    | submitOnboardingEvidence    | POST /v1/onboarding-applications/{applicationId}/submissions       |
| CI-02-010    | changeOnboardingLifecycle   | POST /v1/onboarding-applications/{applicationId}/lifecycle-actions |
| CI-02-011    | decideAdmission             | POST /v1/onboarding-applications/{applicationId}/decisions         |
| CI-02-012    | getOnboardingApplication    | GET /v1/onboarding-applications/{applicationId}                    |
| CI-02-012    | getAdmissionDecision        | GET /v1/admission-decisions/{decisionId}                           |
| CI-02-012    | getOnboardingAction         | GET /v1/onboarding-actions/{actionId}                              |
| CI-02-013    | confirmAdmissionUse         | POST /v1/admission-decisions/{decisionId}/uses                     |

### **6.4. Inicio, antecedentes y ciclo**

Iniciar recibe contexto y datos no generados de la solicitud.

Si existe relación comercial vigente compatible, la informa sin crear otra innecesariamente.

Aportar antecedentes recibe contexto, puntos atendidos, referencias de datos y documentos. Devuelve presentación,
recepción, suficiencia por punto y plazos.

La presentación no actualiza Personas automáticamente.

El ciclo utiliza:

- WITHDRAW: motivo y alcance.

- REOPEN: antecedentes actuales; crea nuevo trámite vinculado.

Reabrir no reactiva aprobaciones vencidas. Si existe uso confirmado o incierto, el retiro se coordina con Clientes.

### **6.5. Decisión de admisión**

Recibe:

- Contexto y versión evaluada.

- conclusion: ACCEPT, REJECT o REQUIRE_REVIEW.

- scope: AdmissionScope.

- Verificaciones versionadas.

- Aprobación separada para decisión definitiva.

- Motivos internos y comunicables, en colecciones diferentes.

Devuelve:

- decisionId: UUID.

- version: Version.

- result: ACCEPTED, REJECTED o REQUIRES_REVIEW.

- scope: AdmissionScope.

- decidedAt: Instant.

- approvedAt y usableUntil: para admisión aceptada.

- Observaciones autorizadas.

La vigencia no supera treinta días corridos desde la aprobación, a la misma hora local, ni los antecedentes
indispensables.

Una alerta sin conclusión no se transforma automáticamente en rechazo.

### **6.6. Confirmación de uso**

Consumidor: Clientes.

Recibe contexto, versión de decisión, persona, alcance solicitado y customerConstitutionId.

Devuelve admissionUseId, decisión versionada, constitución destinataria, resultado y fecha de confirmación cuando
existe.

Los resultados son CONFIRMED, REJECTED y PENDING.

Vinculación ordena uso, retiro, revisión y vencimiento sobre su estado autoritativo.

La misma constitución recupera el uso. Otra incompatible produce conflicto.

El vencimiento posterior no anula un uso confirmado válidamente.

## **7. Servicio Clientes**

### **7.1. Customer y períodos**

Customer contiene:

- customerId y version.

- partyId.

- commercialSegmentCode.

- newContractingAllowed: Booleano.

- continuityStatus: ACTIVE, OFFBOARDING, RESIDUAL o ENDED.

- createdAt.

- nextReviewDate, cuando corresponde.

Cada período contiene:

- periodId: UUID.

- admissionReference: VersionedReference.

- admissionUseId: UUID.

- startedAt: Instant.

- reasonCode: Code.

- commercialEndedAt y fullyEndedAt: cuando se confirman.

Existe una correspondencia comercial única entre persona principal y cliente. No se crean períodos activos equivalentes
concurrentes.

### **7.2. Interfaces**

| **Contrato** | **operationId**                | **Método y ruta**                                   |
|--------------|--------------------------------|-----------------------------------------------------|
| CI-02-014    | constituteCustomerRelationship | POST /v1/customer-constitutions                     |
| CI-02-015    | getCustomer                    | GET /v1/customers/{customerId}                      |
| CI-02-015    | getCustomerRelationByParty     | GET /v1/customer-relations/by-party/{partyId}       |
| CI-02-016    | actOnCustomerReview            | POST /v1/customers/{customerId}/review-actions      |
| CI-02-017    | actOnCustomerOffboarding       | POST /v1/customers/{customerId}/offboarding-actions |
| CI-02-023    | getCustomerAction              | GET /v1/customer-actions/{actionId}                 |

### **7.3. Constitución**

Consumidor: Vinculación.

Recibe contexto, constitución estable, persona, decisión de admisión, alcance y segmento inicial.

Los estados técnicos son:

INTENT_RECORDED, ADMISSION_USE_PENDING, ADMISSION_USE_CONFIRMED, CONSTITUTED y FAILED_REQUIRING_RESOLUTION.

Clientes registra intención antes de solicitar el uso de admisión.

Después constituye cliente o período mediante una transacción local. Una caída intermedia recupera el mismo uso y
continúa la misma constitución.

Devuelve cliente, período, uso confirmado y pendientes.

Si ya existe relación activa compatible, recupera la existente sin consumir otra admisión innecesariamente.

### **7.4. Revisiones**

Las modalidades son:

| **Acción**         | **Datos específicos**                            |
|--------------------|--------------------------------------------------|
| START              | Motivo, fecha prevista y alcance.                |
| ADD_EVIDENCE       | Revisión y antecedentes.                         |
| REQUEST_CONCLUSION | Revisión, verificaciones y conclusión propuesta. |
| CHANGE_SEGMENT     | Segmento, fundamento y aprobación exigible.      |

Las conclusiones son CONTINUE, CONTINUE_WITH_FOLLOW_UP y INITIATE_OFFBOARDING.

Los estados son OPEN, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_CONCLUSION, COMPLETED, OVERDUE y ESCALATED.

La próxima revisión utiliza la fecha más próxima entre doce meses desde la conclusión real y las exigencias anteriores
de controles.

Cuando exige participación, se avisa treinta días antes. Al vencer se escala; la tarea supervisora tiene cinco días
hábiles.

Una revisión pendiente no equivale automáticamente a restricción.

Cambiar segmento no modifica contratos existentes.

### **7.5. Desvinculación**

Las modalidades son:

- REQUEST: causa, recepción y fundamento.

- ADD_INVENTORY: inventario de un propietario.

- CONFIRM_COMPONENT: resultado y evidencia del responsable.

- WITHDRAW_REQUEST: fundamento y tratamiento de actuaciones iniciadas.

- FINALIZE: comprobación de requisitos de finalización.

Después de crear la solicitud, las actuaciones identifican offboardingId.

Un inventario contiene servicio, corte, objetos relacionados, pendientes y resultado:

CONFIRMED_EMPTY, CONFIRMED_WITH_RELATIONS o PENDING.

La falta de respuesta no equivale a inventario vacío.

Se distinguen cese comercial, actuaciones operativas, régimen residual y finalización integral. No se borran
obligaciones de productos ajenos.

## **8. Servicio Representación**

### **8.1. Representation**

Contiene:

- representationId y version, generados.

- representedPartyId: UUID.

- representativePartyIds: UUID\[\], no vacío.

- basisType: Code.

- basisDocuments: DocumentReference\[\].

- parentRepresentationId: para fundamento derivado.

- permittedActs: Code\[\], no vacío.

- productScope y objectScope: RepresentationScope.

- amountLimits: límites por moneda.

- actingMode: INDIVIDUAL o JOINT.

- jointRule: para actuación conjunta.

- validFrom: Instant.

- validTo: cuando el fundamento tiene vencimiento.

- continuityReviewedAt: después de revisión.

- nextContinuityReviewDate: sin vencimiento documental.

- status, generado.

Los límites contienen moneda e importe máximo, sin repetir moneda.

RepresentationScope admite:

- ALL_WITHIN_BASIS: fundamento verificado que permite ese alcance.

- EXPLICIT_SET: referencias concretas, no vacías.

JointRule contiene representantes admitidos, cantidad mínima de actores distintos y combinaciones permitidas por el
fundamento.

Los estados son:

SUBMITTED, UNDER_VERIFICATION, PENDING_APPROVAL, ACTIVE, CONTINUITY_REVIEW_REQUIRED, REVOKED, EXPIRED, ENDED y REJECTED.

### **8.2. Interfaces**

| **Contrato** | **operationId**                 | **Método y ruta**                                                |
|--------------|---------------------------------|------------------------------------------------------------------|
| CI-02-018    | createRepresentationApplication | POST /v1/representation-applications                             |
| CI-02-018    | submitRepresentationEvidence    | POST /v1/representation-applications/{applicationId}/submissions |
| CI-02-018    | decideRepresentationApplication | POST /v1/representation-applications/{applicationId}/decisions   |
| CI-02-019    | changeRepresentation            | POST /v1/representations/{representationId}/changes              |
| CI-02-020    | getRepresentation               | GET /v1/representations/{representationId}                       |
| CI-02-020    | listPartyRepresentations        | GET /v1/parties/{partyId}/representations                        |
| CI-02-021    | checkRepresentation             | POST /v1/representation-checks                                   |
| CI-02-024    | getRepresentationAction         | GET /v1/representation-actions/{actionId}                        |

### **8.3. Reconocimiento y modificación**

Crear recibe contexto y definición propuesta.

Aportar evidencia recibe contexto, documentos y verificaciones relacionadas.

Decidir recibe contexto, verificaciones, conclusión y aprobación separada.

Las conclusiones son RECOGNIZE, REJECT y REQUIRE_REVIEW.

Recibir documentos no activa facultades.

Las modificaciones son:

- AMEND: definición propuesta, verificaciones y aprobación.

- RENEW: vigencia, fundamento, verificaciones y aprobación.

- REVOKE: fundamento y momento de efecto.

- END: causa y momento de efecto.

- CONFIRM_CONTINUITY: verificaciones y próxima revisión.

Las representaciones sin vencimiento documental se revisan como máximo cada doce meses y ante hechos relevantes.

Las ampliaciones y renovaciones requieren aprobación equivalente al reconocimiento.

### **8.4. Consulta y comprobación**

La consulta por persona utiliza rol REPRESENTED o REPRESENTATIVE, finalidad, referencia temporal y paginación.

No devuelve documentos reservados por defecto.

La comprobación recibe:

- Persona representada.

- Actores, en colección no vacía.

- Acto.

- Objeto.

- Producto, cuando corresponde.

- Importe y moneda juntos, para alcance monetario.

- Huella del contenido.

- Momento y finalidad.

Devuelve checkId, resultado, representaciones utilizadas, huella evaluada, actores faltantes, condiciones y metadatos.

El resultado es SUFFICIENT, INSUFFICIENT o PENDING.

No combina poderes incompatibles ni crea una preparación.

## **9. Preparaciones de representación**

### **9.1. Interfaces**

| **operationId**              | **Método y ruta**                                                                  |
|------------------------------|------------------------------------------------------------------------------------|
| prepareRepresentationAction  | POST /v1/representation-action-preparations                                        |
| getRepresentationPreparation | GET /v1/representation-action-preparations/{preparationId}                         |
| notifyRepresentationDecision | POST /v1/representation-action-preparations/{preparationId}/decision-notifications |

### **9.2. Entrada y resultado**

La entrada utiliza la solicitud común de preparación y agrega:

- representedPartyId.

- actingPartyIds, no vacío.

- actCode.

- objectReference.

- productReference, según acto.

- representationReferences, cuando se identifican fundamentos concretos.

- jointEvidenceReferences, cuando se exigen conformidades conjuntas.

Los importes y monedas se incluyen cuando forman parte del alcance.

La huella vincula representado, actores, acto, objetos, importes y contenido exacto.

La respuesta contiene preparación, intento, huella, alcance, controles versionados, referencias de representación,
estado y consulta.

Los estados son PREPARED, RESOLUTION_PENDING, CONCLUDED_COMMITTED, CONCLUDED_ABORTED y REJECTED.

Un resultado favorable persistido responde 201. Un procesamiento pendiente responde 202 y no habilita confirmación.

### **9.3. Cambios y recuperación**

Preparación, renovación y revocación utilizan controles locales compatibles.

Una revocación anterior impide preparar. Una posterior bloquea nuevas preparaciones y solicita resolver los intentos
existentes.

La notificación verifica coordinador, intento, huella y decisión.

El vencimiento no libera una preparación con decisión desconocida.

La preparación no sustituye permisos de cuenta, restricciones, capacidad crediticia ni recursos monetarios.

## **10. Eventos**

### **10.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                             | **Tema**                 | **Agregado**              |
|--------------|--------------------------------------|--------------------------|---------------------------|
| CI-02-025    | party.updated.v1                     | party.events.v1          | Persona.                  |
| CI-02-026    | party.relationship-changed.v1        | party.events.v1          | Relación.                 |
| CI-02-027    | party.duplicates-resolved.v1         | party.events.v1          | Caso de duplicados.       |
| CI-02-028    | party.circumstance-recorded.v1       | party.events.v1          | Persona.                  |
| CI-02-029    | onboarding.admission-decided.v1      | onboarding.events.v1     | Solicitud de vinculación. |
| CI-02-030    | customer.relationship-established.v1 | customer.events.v1       | Cliente.                  |
| CI-02-031    | customer.review-completed.v1         | customer.events.v1       | Cliente.                  |
| CI-02-032    | customer.continuity-changed.v1       | customer.events.v1       | Cliente.                  |
| CI-02-033    | representation.changed.v1            | representation.events.v1 | Representación.           |

La versión inicial de esquema es 1. La clave es aggregateId.

Los tipos de agregado son PARTY, PARTY_RELATIONSHIP, PARTY_DUPLICATE_CASE, ONBOARDING_APPLICATION, CUSTOMER y
REPRESENTATION.

### **10.2. CI-02-025 — Persona actualizada**

Payload obligatorio:

- partyId: UUID.

- partyVersion: Version.

- changedFieldCodes: Code\[\], no vacío.

- reviewRequired: Booleano.

- recordStatus: estado de persona.

No incluye valores completos de contactos ni documentos.

### **10.3. CI-02-026 — Relación modificada**

Payload obligatorio:

- relationshipId: UUID.

- relationshipVersion: Version.

- changeType: CREATED, UPDATED o ENDED.

- sourcePartyId y targetPartyId: UUID.

- relationshipType: Code.

- validFrom: Date.

- completeness: estado de completitud.

Incluye validTo cuando existe final determinado.

Los datos de participación detallada se recuperan mediante consulta autorizada.

### **10.4. CI-02-027 — Duplicados resueltos**

Payload obligatorio:

- caseId: UUID.

- caseVersion: Version.

- conclusion: SAME_PERSON o DIFFERENT_PERSONS.

- candidatePartyIds: UUID\[\], no vacío.

- associatedPartyIds: UUID\[\].

- pendingComponents: pendientes comunes.

SAME_PERSON exige principalPartyId.

Una conclusión inconclusa no se publica como duplicado resuelto.

### **10.5. CI-02-028 — Circunstancia registrada**

Payload obligatorio:

- partyId: UUID.

- partyVersion: Version.

- circumstanceId: UUID.

- circumstanceType: tipo de circunstancia.

- factDate: Date.

- knownAt: Instant.

- verificationStatus: DECLARED, PENDING, VERIFIED u OBSERVED.

- pendingComponents: pendientes comunes.

No acredita aplicación integral de medidas sobre productos.

### **10.6. CI-02-029 — Admisión decidida**

Payload obligatorio:

- applicationId: UUID.

- applicationVersion: Version.

- partyId: UUID.

- decisionId: UUID.

- decisionVersion: Version.

- result: resultado de admisión.

- scope: AdmissionScope.

- decidedAt: Instant.

- communicableReasonCodes: Code\[\].

Una admisión aceptada agrega approvedAt y usableUntil.

Los motivos reservados no se distribuyen en este evento.

### **10.7. CI-02-030 — Relación constituida**

Payload obligatorio:

- customerId: UUID.

- customerVersion: Version.

- periodId: UUID.

- partyId: UUID.

- admissionReference: VersionedReference.

- admissionUseId: UUID.

- customerConstitutionId: UUID.

- startedAt: Instant.

- commercialSegmentCode: Code.

Solo se publica cuando la constitución local está confirmada.

### **10.8. CI-02-031 — Revisión completada**

Payload obligatorio:

- customerId: UUID.

- customerVersion: Version.

- reviewId: UUID.

- conclusion: conclusión de revisión.

- completedAt: Instant.

- pendingComponents: pendientes comunes.

Incluye nextReviewDate cuando la continuidad exige otra revisión.

### **10.9. CI-02-032 — Continuidad modificada**

Payload obligatorio:

- customerId: UUID.

- customerVersion: Version.

- previousStatus: estado de continuidad.

- newStatus: estado de continuidad.

- newContractingAllowed: Booleano.

- effectiveAt: Instant.

- reasonCode: Code.

- pendingComponents: pendientes comunes.

Incluye offboardingId cuando corresponde.

El evento no extingue obligaciones administradas por otros servicios.

### **10.10. CI-02-033 — Representación modificada**

Payload obligatorio:

- representationId: UUID.

- representationVersion: Version.

- representedPartyId: UUID.

- representativePartyIds: UUID\[\], no vacío.

- changeType: RECOGNIZED, AMENDED, RENEWED, REVOKED, ENDED o CONTINUITY_CONFIRMED.

- status: estado de representación.

- permittedActs: Code\[\].

- productScope y objectScope: RepresentationScope.

- actingMode: modalidad de actuación.

- validFrom: Instant.

- pendingComponents: pendientes comunes.

Incluye validTo cuando existe y jointRule para actuación conjunta.

La revocación comunicada no demuestra aborto de todos los intentos preparados.

### **10.11. Consumidores y orden**

| **Información**             | **Consumidores autorizados**                                                  |
|-----------------------------|-------------------------------------------------------------------------------|
| Personas                    | Vinculación, Clientes, Representación y controles competentes.                |
| Relaciones y circunstancias | Conocimiento del cliente, Riesgos y responsables afectados.                   |
| Admisión                    | Clientes, Solicitudes de productos y Gestión administrativa.                  |
| Cliente                     | Contratos, propietarios de productos y controles competentes.                 |
| Representación              | Servicios que admiten actos representados y Restricciones cuando corresponde. |

Las proyecciones utilizan versiones para evitar reemplazos por mensajes antiguos.

Ante una discontinuidad relevante se recupera el estado autoritativo. El evento no sustituye la comprobación actual ni
la preparación exigida para un acto.

## **11. Seguridad y recuperación**

Se separan permisos para registro, actualización, consulta por atributos, búsqueda, duplicados, circunstancias,
admisión, constitución comercial, revisión, desvinculación y representación.

Las búsquedas no permiten enumerar personas fuera de la finalidad y alcance autorizados.

Los motivos comunicables se separan de antecedentes internos.

La confirmación de un contacto no concede acceso automático a información protegida.

Cada servicio conserva actuaciones, idempotencia, outbox, inbox y trabajos recuperables.

Se exige:

- Reconocimiento de actuaciones repetidas.

- Unicidad comercial por persona principal.

- Uso recuperable de admisión.

- Períodos activos no duplicados.

- Referencias históricas conservadas.

- Desafíos ligados a contacto y versión.

- Preparaciones sin conclusión contradictoria.

No se mantienen transacciones SQL abiertas durante llamadas remotas.

## **12. Errores particulares**

| **Código**                        | **HTTP** | **Significado**                                    |
|-----------------------------------|----------|----------------------------------------------------|
| CONTENT_CONFLICT                  | 409      | Identidad reutilizada con otro contenido.          |
| VERSION_CONFLICT                  | 409      | Versión incompatible.                              |
| MATCH_REVIEW_REQUIRED             | 409      | Otra actuación exige resolver identidad pendiente. |
| IDENTIFIER_CONTRADICTION          | 422      | Identificadores incompatibles.                     |
| CONTACT_VERSION_MISMATCH          | 409      | Contacto diferente del confirmado.                 |
| CONTACT_CHALLENGE_EXPIRED         | 422      | Desafío vencido.                                   |
| CONTACT_CHALLENGE_SUPERSEDED      | 409      | Desafío sustituido.                                |
| CONTACT_PROOF_INVALID             | 422      | Prueba incorrecta.                                 |
| CONTACT_ATTEMPTS_EXHAUSTED        | 422      | Intentos agotados.                                 |
| DUPLICATE_RESOLUTION_CONFLICT     | 409      | Resolución concurrente incompatible.               |
| MANDATORY_VERIFICATION_PENDING    | 422      | Falta verificación exigible.                       |
| ADMISSION_EXPIRED                 | 422      | Admisión vencida.                                  |
| ADMISSION_WITHDRAWN               | 422      | Admisión retirada.                                 |
| ADMISSION_USE_CONFLICT            | 409      | Uso incompatible de decisión.                      |
| OFFBOARDING_COMPONENT_PENDING     | 409      | Finalización integral impedida por pendientes.     |
| REPRESENTATION_SCOPE_INSUFFICIENT | 422      | Acto fuera del fundamento.                         |
| REPRESENTATION_CONTINUITY_PENDING | 422      | Continuidad sin resolver.                          |
| JOINT_REPRESENTATIVES_MISSING     | 422      | Faltan intervinientes.                             |
| PREPARATION_CONTENT_CONFLICT      | 409      | Preparación con contenido diferente.               |
| INCOMPATIBLE_CHANGE_PENDING       | 409      | Cambio todavía en resolución.                      |
| DEPENDENCY_UNAVAILABLE            | 503      | Dependencia necesaria indisponible.                |

Un registro aceptado para revisar identidad responde 202 con seguimiento.

Una comprobación informativa negativa responde 200 con INSUFFICIENT.

Un rechazo de prueba de contacto conserva el intento fallido; repetir la misma actuación no consume otro intento.

## **13. Escenarios de aceptación**

| **Escenario**                                          | **Resultado exigido**                                 |
|--------------------------------------------------------|-------------------------------------------------------|
| Persona con nacimiento desconocido                     | Conserva ausencia explícita sin inventar fecha.       |
| Nombre coincidente con otro registro                   | Devuelve candidatos sin fusionar identidades.         |
| Identificador con ceros iniciales                      | Conserva original y normalización por tipo.           |
| Cambia el contacto durante confirmación                | El desafío anterior no confirma el nuevo valor.       |
| Se repite una prueba incorrecta con la misma actuación | No descuenta dos intentos.                            |
| Nueva emisión de desafío                               | Sustituye el anterior pendiente.                      |
| Duplicado confirmado                                   | Conserva referencias sin fusionar patrimonio.         |
| Presentación de vinculación oportuna en revisión       | No declara vencimiento mientras se evalúa.            |
| Se pierde la confirmación de uso de admisión           | Recupera el mismo uso y constitución.                 |
| Admisión vence después de uso válido                   | No anula ese uso.                                     |
| Dos constituciones equivalentes compiten               | No crean períodos activos duplicados.                 |
| Inventario no responde                                 | No se interpreta como vacío.                          |
| Representantes aceptan contenidos distintos            | No satisface actuación conjunta.                      |
| Revocación compite con confirmación                    | Se conserva una decisión comprobable por intento.     |
| Preparación vence sin decisión conocida                | Mantiene resolución pendiente.                        |
| Evento duplicado o antiguo                             | No repite efectos ni sustituye versiones posteriores. |

Las pruebas de unicidad y concurrencia utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban entradas,
respuestas, errores y eventos contra sus esquemas.
