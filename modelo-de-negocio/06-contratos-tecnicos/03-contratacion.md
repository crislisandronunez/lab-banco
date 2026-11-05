# **Contratos técnicos — Módulo 3. Contratación**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**             | **Responsabilidad**                                                           |
|--------------------------|-------------------------------------------------------------------------------|
| Solicitudes de productos | Solicitudes, requisitos, viabilidad y coordinación de constitución operativa. |
| Contratos                | Modelos, propuestas, entrega, aceptación, celebración y ciclo contractual.    |

Comprende los contratos **CI-03-001 a CI-03-033**.

Solicitud, consentimiento, celebración, entrada en efecto y habilitación operativa conservan identidades y resultados
diferentes.

Contratos no concede crédito ni administra saldos. Solicitudes de productos coordina la constitución sin sustituir al
propietario operativo.

## **2. Convenciones**

### **2.1. Transporte y contexto**

Las rutas utilizan /v1.

Todas las interfaces requieren autenticación y X-Correlation-Id. Los comandos persistentes requieren Idempotency-Key y
ActionContext.

Se utilizan las estructuras comunes:

- ObjectReference.

- VersionedReference.

- DocumentReference.

- Resultado de actuación.

- Problem Details.

- Sobre de eventos.

- Consulta común GET /v1/actions/{actionId}.

El actor y el servicio proceden de la identidad autenticada o de una delegación verificable.

Las consultas mediante POST no crean preparaciones.

### **2.2. Tipos**

| **Tipo** | **Representación**                                          |
|----------|-------------------------------------------------------------|
| UUID     | Identificador interno.                                      |
| Version  | Entero positivo dentro del rango de long.                   |
| Code     | Cadena de 1 a 80 caracteres.                                |
| Name     | Cadena de 1 a 200 caracteres.                               |
| Reason   | Cadena de 1 a 4000 caracteres.                              |
| Instant  | Instante UTC.                                               |
| Date     | Fecha YYYY-MM-DD.                                           |
| Hash     | SHA-256 hexadecimal de 64 caracteres.                       |
| Currency | ARS o USD.                                                  |
| Amount   | Cadena decimal no negativa, precisión 24 y escala máxima 8. |
| Rate     | Cadena decimal, precisión 24 y escala máxima 12.            |

Los importes destinados a ejecución monetaria respetan centavos. Los valores contractuales de cálculo pueden conservar
mayor escala.

Los plazos utilizan el calendario identificado y la zona America/Argentina/Buenos_Aires.

### **2.3. Obligatoriedad**

En las estructuras de este módulo, los campos enumerados son obligatorios salvo que se indiquen como opcionales,
condicionales o generados.

Los campos generados no se admiten como instrucciones del consumidor.

Las colecciones obligatorias pueden estar vacías salvo que se exija expresamente al menos un elemento.

Los campos desconocidos se rechazan. Las estructuras configurables utilizan definiciones registradas y versionadas.

## **3. Estructuras compartidas**

### **3.1. Participant**

| **Campo**               | **Tipo**        | **Condición**                            |
|-------------------------|-----------------|------------------------------------------|
| partyId                 | UUID            | Obligatorio.                             |
| roleCode                | Code            | Rol contractual.                         |
| acceptanceRequired      | Booleano        | Exigencia de conformidad.                |
| rightsCodes             | Code\[\]        | Derechos asignados.                      |
| obligationCodes         | Code\[\]        | Obligaciones asignadas.                  |
| jointGroupId            | UUID            | Para actuación conjunta.                 |
| representationReference | ObjectReference | Cuando se actúa mediante representación. |

El actor que interviene se identifica en la evidencia de cada actuación. No se deduce exclusivamente del participante
contractual.

### **3.2. ContractualCondition**

| **Campo**               | **Tipo**            | **Condición**                |
|-------------------------|---------------------|------------------------------|
| conditionId             | UUID                | Obligatorio.                 |
| code                    | Code                | Identifica la condición.     |
| requiredFactCode        | Code                | Hecho exigido.               |
| stage                   | Enumeración         | Etapa de exigibilidad.       |
| responsibleService      | Code                | Propietario del hecho.       |
| sourceReference         | ObjectReference     | Cuando existe antecedente.   |
| deadline                | Instant             | Cuando tiene vencimiento.    |
| failureConsequenceCode  | Code                | Consecuencia prevista.       |
| waivable                | Booleano            | Posibilidad de dispensa.     |
| status                  | Enumeración         | Situación comprobada.        |
| evidenceReferences      | ObjectReference\[\] | Evidencias relacionadas.     |
| waiverApprovalReference | ObjectReference     | Exclusivamente con dispensa. |

Las etapas son:

- BEFORE_CELEBRATION.

- BEFORE_EFFECT.

- BEFORE_CONSTITUTION_OR_ENABLEMENT.

- BEFORE_DISBURSEMENT_OR_USE.

Los estados son PENDING, SATISFIED, FAILED y WAIVED.

WAIVED exige que la condición sea dispensable y que exista aprobación competente. No se traslada un requisito
obligatorio a una etapa posterior.

### **3.3. EconomicTerms**

Contiene:

| **Campo**             | **Tipo**                       |
|-----------------------|--------------------------------|
| currency              | Currency                       |
| monetaryConcepts      | ContractualMonetaryConcept\[\] |
| rateDefinitions       | ContractualRate\[\]            |
| calculationReferences | VersionedReference\[\]         |
| scheduleReferences    | VersionedReference\[\]         |

Cada concepto contiene conceptCode, ownerService y una modalidad excluyente:

- FIXED_AMOUNT: amount.

- CALCULATED: calculationReference.

Cada tasa identifica:

- rateCode.

- rateTypeCode.

- periodCode.

- dayCountCode.

- capitalizationCode, cuando corresponde.

- Modalidad FIXED o VARIABLE.

FIXED exige value.

VARIABLE exige sourceReference, margin, revisionRuleCode y unavailabilityTreatmentCode.

Los cronogramas y CFT conservan la determinación versionada de su propietario. Contratos no recalcula resultados
especializados.

### **3.4. Manifest**

| **Campo**               | **Tipo**             |
|-------------------------|----------------------|
| manifestId              | UUID                 |
| canonicalizationVersion | Version              |
| modelReference          | VersionedReference   |
| documentEntries         | ManifestDocument\[\] |
| structuredContentHash   | Hash                 |
| manifestHash            | Hash                 |
| createdAt               | Instant              |

Cada ManifestDocument contiene:

- position: entero positivo único.

- documentReference: DocumentReference.

- documentRole: Code.

- contentHash: Hash.

El orden de los documentos es significativo.

structuredContentHash vincula finalidad, modelo, producto, participantes, condiciones particulares, condiciones
económicas, condiciones contractuales y contrato anterior cuando corresponde.

manifestHash vincula el contenido estructurado y las entradas documentales ordenadas. Ambos se calculan mediante el
procedimiento común, con tipos vinculantes CONTRACT_STRUCTURED_CONTENT y CONTRACT_MANIFEST.

Los documentos permanecen en Documentos y comprobantes. Una URL mutable no identifica contenido aceptado.

### **3.5. Datos particulares tipados**

Cada valor particular contiene fieldCode, valueType y un único campo de valor:

| **valueType** | **Campo**              |
|---------------|------------------------|
| TEXT          | textValue              |
| DATE          | dateValue              |
| INSTANT       | instantValue           |
| AMOUNT        | amountValue y currency |
| RATE          | rateValue              |
| INTEGER       | integerValue           |
| BOOLEAN       | booleanValue           |
| CODE          | codeValue              |
| REFERENCE     | referenceValue         |

Su definición establece longitud, rango, precisión, escala o valores admitidos según el tipo.

No se aceptan valores para campos no definidos por la versión del modelo.

## **4. Solicitudes de productos**

### **4.1. ProductApplication**

| **Campo**             | **Tipo**                       | **Condición**                     |
|-----------------------|--------------------------------|-----------------------------------|
| applicationId         | UUID                           | Generado.                         |
| version               | Version                        | Generado.                         |
| applicationReference  | Cadena de hasta 120 caracteres | Referencia visible generada.      |
| applicantPartyId      | UUID                           | Solicitante.                      |
| customerId            | UUID                           | Cuando existe relación comercial. |
| channelCode           | Code                           | Canal.                            |
| purposeCode           | Code                           | Finalidad.                        |
| participants          | Participant\[\]                | Al menos uno.                     |
| items                 | ApplicationItem\[\]            | Al menos uno.                     |
| previousApplicationId | UUID                           | En reapertura.                    |
| receivedAt            | Instant                        | Generado.                         |
| status                | Enumeración                    | Generado.                         |

Los estados son:

RECEIVED, AWAITING_INFORMATION, UNDER_REVIEW, VIABLE, REJECTED, WITHDRAWN, EXPIRED, CONTRACTED, CONSTITUTION_PENDING,
COMPLETED y REPAIR_REQUIRED.

Un componente fallido no revierte automáticamente otros productos ya constituidos.

### **4.2. ApplicationItem**

Contiene:

- itemId: UUID.

- productReference: VersionedReference.

- modalityCode: Code.

- participants: Participant\[\].

- dependsOnItemIds: UUID\[\].

- conditionReferences: ObjectReference\[\].

- currency: cuando la modalidad la exige.

- amount: cuando la modalidad admite importe.

- term: cuando la modalidad exige plazo.

- status: generado.

term contiene quantity, entero positivo, y unit: DAYS, MONTHS o YEARS.

Las dependencias no forman ciclos.

Cada componente conserva sus evaluaciones, propuestas, contratos y constituciones mediante referencias.

### **4.3. Requerimientos**

ApplicationRequirement contiene:

- requirementId: UUID.

- version: Version.

- applicationItemId: UUID, cuando es específico.

- cause: INITIAL, FINAL_CORRECTION o BANK_OMISSION.

- items: RequirementItem\[\], no vacío.

- channelCode: Code.

- madeAvailableAt: Instant.

- calendarReference: VersionedReference.

- originalDueAt: Instant.

- effectiveDueAt: Instant.

- reminderAt: Instant.

- submissionReferences: ObjectReference\[\].

- incidentReferences: ObjectReference\[\].

Cada punto identifica itemId, requirementCode, responsibleService y status.

Los estados de punto son PENDING, SUBMITTED, UNDER_REVIEW, SATISFIED y OBSERVED.

Se aplican:

| **Situación**                        | **Tratamiento**                                               |
|--------------------------------------|---------------------------------------------------------------|
| Requerimiento inicial                | Treinta días corridos.                                        |
| Corrección de presentación observada | Quince días corridos.                                         |
| Omisión interna del banco            | Treinta días para los puntos nuevos.                          |
| Inicio                               | Día siguiente a la puesta a disposición.                      |
| Final inhábil                        | Siguiente hábil.                                              |
| Recordatorio                         | Cinco días corridos antes.                                    |
| Canal indisponible al vencer         | Hasta finalizar el segundo hábil posterior a su recuperación. |

Una presentación oportuna impide declarar vencimiento mientras está en revisión.

### **4.4. Catálogo HTTP**

| **Contrato** | **operationId técnico**           | **Método y ruta**                                                   |
|--------------|-----------------------------------|---------------------------------------------------------------------|
| CI-03-003    | createProductApplication          | POST /v1/product-applications                                       |
| CI-03-004    | addProductApplicationAntecedents  | POST /v1/product-applications/{applicationId}/antecedents           |
| CI-03-005    | assessProductApplicationViability | POST /v1/product-applications/{applicationId}/viability-assessments |
| CI-03-006    | withdrawProductApplication        | POST /v1/product-applications/{applicationId}/withdrawals           |
| CI-03-006    | reopenProductApplication          | POST /v1/product-applications/{applicationId}/reopenings            |
| CI-03-007    | getProductApplication             | GET /v1/product-applications/{applicationId}                        |
| CI-03-007    | listProductApplicationActions     | GET /v1/product-applications/{applicationId}/actions                |

**Crear:** recibe contexto y los campos de solicitud no generados. Devuelve la solicitud, requisitos y dependencias.

Puede registrarse con vinculación pendiente. Esto no satisface requisitos posteriores de relación comercial.

**Incorporar antecedentes:** recibe contexto, puntos atendidos, documentos y resultados especializados. Cada resultado
identifica emisor, decisión, versión, alcance y vigencia.

Devuelve submissionId, recepción, puntos aplicados, observados y pendientes. Recibir no significa aprobar suficiencia.

**Evaluar viabilidad:** recibe contexto, componentes y antecedentes versionados. Devuelve:

- assessmentId: UUID.

- evaluatedVersion: Version.

- inputHash: Hash.

- itemResults: resultados por componente.

- validUntil: cuando existe vigencia.

- observations: Code\[\].

Cada resultado identifica componente, conclusión y condiciones. Las conclusiones son VIABLE, CONDITIONALLY_VIABLE,
NOT_VIABLE y PENDING.

La viabilidad no concede crédito.

**Retirar:** recibe contexto, motivo y componentes alcanzados. Devuelve retiro confirmado o componentes pendientes de
resolución. Si existe celebración incierta, primero recupera su decisión.

**Reabrir:** recibe contexto, antecedentes actuales y componentes solicitados. Crea una nueva solicitud vinculada; no
reactiva aprobaciones, precios o aceptaciones vencidos.

**Consultar:** devuelve las estructuras autorizadas y sus referencias. El historial utiliza paginación estable.

## **5. Constitución operativa**

### **5.1. ProductConstitution**

Contiene:

| **Campo**                   | **Tipo**                           |
|-----------------------------|------------------------------------|
| constitutionId              | UUID                               |
| applicationId               | UUID                               |
| applicationItemId           | UUID                               |
| contractReference           | VersionedReference                 |
| productReference            | VersionedReference                 |
| participantReferences       | Participant\[\]                    |
| requiredCapabilities        | Code\[\]                           |
| conditionReferences         | ObjectReference\[\]                |
| currency                    | Currency, según producto           |
| operationalOwnerService     | Code, determinado por el proveedor |
| operationalProductReference | ObjectReference, cuando existe     |
| confirmedCapabilities       | Code\[\]                           |
| status                      | Enumeración                        |
| pendingComponents           | Pendientes comunes                 |

Los estados son WAITING_CONDITIONS, PREPARING, AWAITING_RESULT, CONSTITUTED, PARTIALLY_ENABLED, ENABLED y
REPAIR_REQUIRED.

### **5.2. Interfaces**

| **Contrato** | **operationId técnico**         | **Método y ruta**                                       |
|--------------|---------------------------------|---------------------------------------------------------|
| CI-03-015    | coordinateProductConstitution   | POST /v1/product-constitutions                          |
| CI-03-016    | recordProductConstitutionResult | POST /v1/product-constitutions/{constitutionId}/results |
| CI-03-007    | getProductConstitution          | GET /v1/product-constitutions/{constitutionId}          |

Coordinar recibe contexto y los datos de constitución conocidos antes de la ejecución. Conserva intención antes del
comando remoto.

Incorporar resultado recibe contrato, producto operativo, capacidades, evidencias y resultado:

CONSTITUTED, PARTIALLY_ENABLED, ENABLED, REJECTED o UNCERTAIN.

Solo el propietario operativo competente acredita su resultado. Gestión administrativa puede aportar antecedentes y
coordinar su comprobación.

Una contradicción de moneda, modalidad o participantes impide informar correspondencia.

Una celebración confirmada conserva su existencia aunque la constitución permanezca pendiente.

## **6. Modelos contractuales**

### **6.1. ContractModelVersion**

Contiene:

- modelId y version, generados.

- code: Code.

- name: Name.

- compatibleProductReferences: VersionedReference\[\], no vacío.

- contractualRegime: enumeración.

- participantRules: reglas de roles.

- requiredAcceptances: reglas de conformidad.

- generalTermsDocuments: DocumentReference\[\].

- particularFieldDefinitions: definiciones tipadas.

- permittedVariations: Code\[\].

- conditionDefinitions: ContractualCondition\[\].

- effectRules, amendmentRules, renewalRules, withdrawalRules y terminationRules: reglas registradas.

- bankApprovalRule: regla de conformidad del banco.

- automaticFormalizationAllowed: Booleano.

- validity: inicio y final opcional.

- status: generado.

Los regímenes son CONSUMER_BANKING, COMMERCIAL_FINANCING e INSTITUTIONAL.

Las reglas registradas contienen ruleCode, ruleVersion y parámetros tipados conforme a esa regla. No admiten scripts ni
parámetros desconocidos.

Una definición particular contiene fieldCode, valueType, required, sourceService, validationCode, substitutionAllowed y
las restricciones correspondientes a su tipo.

### **6.2. Interfaces**

| **Contrato** | **operationId técnico**    | **Método y ruta**                                             |
|--------------|----------------------------|---------------------------------------------------------------|
| CI-03-001    | createContractModel        | POST /v1/contract-models                                      |
| CI-03-001    | createContractModelVersion | POST /v1/contract-models/{modelId}/versions                   |
| CI-03-001    | actOnContractModelVersion  | POST /v1/contract-models/{modelId}/versions/{version}/actions |
| CI-03-002    | getContractModelVersion    | GET /v1/contract-models/{modelId}/versions/{version}          |

Crear recibe contexto y definición inicial.

Crear versión recibe contexto, versión de origen y contenido completo propuesto.

Las acciones son:

- UPDATE_DRAFT: definición completa; solo sobre borrador.

- SUBMIT: huella del contenido presentado.

- APPROVE: aprobación separada y huella.

- REJECT: fundamento.

- ENABLE: aprobación y vigencia.

- WITHDRAW: fundamento y tratamiento de propuestas existentes.

Los estados son DRAFT, PENDING_APPROVAL, APPROVED, ENABLED, REJECTED y WITHDRAWN.

Una versión aprobada es inmutable. Preparador y aprobador deben ser personas distintas.

El retiro impide nuevas propuestas. La continuidad de las existentes exige una decisión expresa sobre su alcance.

## **7. Propuestas, entregas y aceptaciones**

### **7.1. ContractProposal**

| **Campo**               | **Tipo**                     | **Condición**                   |
|-------------------------|------------------------------|---------------------------------|
| proposalId y version    | UUID y Version               | Generados.                      |
| applicationId           | UUID                         | Obligatorio.                    |
| applicationItemId       | UUID                         | Obligatorio.                    |
| purpose                 | Enumeración                  | Finalidad.                      |
| sourceContractReference | VersionedReference           | Para modificación o renovación. |
| modelReference          | VersionedReference           | Obligatorio.                    |
| productReference        | VersionedReference           | Obligatorio.                    |
| participants            | Participant\[\]              | No vacío.                       |
| particularValues        | Valores particulares tipados | Obligatorio.                    |
| economicTerms           | EconomicTerms                | Obligatorio.                    |
| conditions              | ContractualCondition\[\]     | Obligatorio.                    |
| manifest                | Manifest                     | Cuando está disponible.         |
| validUntil              | Instant                      | Obligatorio.                    |
| status                  | Enumeración                  | Generado.                       |

purpose admite INITIAL_CONTRACT, BILATERAL_AMENDMENT y EXPRESS_RENEWAL.

Los estados son PREPARING, DOCUMENTS_PENDING, AVAILABLE, PARTIALLY_ACCEPTED, FULLY_ACCEPTED, SUPERSEDED, EXPIRED,
WITHDRAWN y USED.

La vigencia no supera los antecedentes indispensables. Un cambio material genera otra versión y exige las conformidades
correspondientes.

### **7.2. Interfaces de propuesta y entrega**

| **Contrato** | **operationId técnico**            | **Método y ruta**                                          |
|--------------|------------------------------------|------------------------------------------------------------|
| CI-03-008    | prepareContractProposal            | POST /v1/contract-proposals                                |
| CI-03-009    | getContractProposalVersion         | GET /v1/contract-proposals/{proposalId}/versions/{version} |
| CI-03-010    | requestContractProposalDelivery    | POST /v1/contract-proposals/{proposalId}/deliveries        |
| CI-03-011    | registerContractProposalAcceptance | POST /v1/contract-proposals/{proposalId}/acceptances       |
| CI-03-012    | withdrawContractAcceptance         | POST /v1/contract-acceptances/{acceptanceId}/withdrawals   |

Preparar propuesta recibe contexto, solicitud, componente, finalidad, modelo, producto, participantes, valores
particulares, condiciones, aprobaciones y anexos.

La respuesta identifica propuesta, versión, documentos pendientes, conformidades y vencimiento.

Solicitar entrega recibe:

- Contexto.

- proposalVersion: Version.

- manifestHash: Hash.

- recipientReferences: ObjectReference\[\], no vacío.

- channelCodes: Code\[\], no vacío.

- deliveryPurpose: PRE_ACCEPTANCE, FINAL_COPY, AMENDMENT_NOTICE u OTHER_AUTHORIZED_NOTICE.

Devuelve deliveryId, documentos, destinatarios, comunicaciones y estado.

Los estados distinguen REQUESTED, AVAILABLE, DELIVERED, PARTIALLY_DELIVERED, FAILED y UNCERTAIN.

La evidencia de Comunicaciones se incorpora por su evento autorizado, comprobando comunicación, destinatario, documento
y versión. Si falta el evento, Contratos consulta la comunicación original.

No se acepta una declaración libre de entrega.

### **7.3. Acceptance**

Contiene:

- acceptanceId: UUID generado.

- proposalReference: VersionedReference.

- manifestHash: Hash.

- participantPartyId: UUID.

- roleCode: Code.

- actorReference: ObjectReference.

- representationReference: cuando corresponde.

- authenticationEvidenceReference: ObjectReference.

- deliveryReference: ObjectReference.

- affirmativeConsent: Booleano, obligatorio y verdadero.

- acceptedAt: instante comprobado de manifestación.

- receivedAt: recepción generada.

- status: generado.

Los estados son PENDING_VERIFICATION, EFFECTIVE, WITHDRAWN y REJECTED.

Registrar aceptación exige correspondencia entre la propuesta de ruta, su versión y manifiesto.

La conformidad requiere acceso previo al contenido íntegro. Una aceptación pendiente de verificación no sirve para
celebrar.

Retirar aceptación recibe contexto y referencia de propuesta. Devuelve WITHDRAWN, ALREADY_COMMITTED o PENDING.

El retiro y la celebración se ordenan sobre el mismo control local.

## **8. Celebración**

### **8.1. CI-03-013 — Celebrar contrato**

**Operación:** celebrateContract  
**Interfaz:** POST /v1/contracts

Entrada:

| **Campo**            | **Tipo**            | **Condición**           |
|----------------------|---------------------|-------------------------|
| context              | ActionContext       | Obligatorio.            |
| proposalReference    | VersionedReference  | Obligatorio.            |
| applicationId        | UUID                | Obligatorio.            |
| applicationItemId    | UUID                | Obligatorio.            |
| acceptanceReferences | ObjectReference\[\] | Conformidades exigidas. |
| bankConformity       | Variante tipada     | Obligatorio.            |

bankConformity admite:

- INDIVIDUAL_APPROVAL: approvalReference.

- AUTOMATIC_FORMALIZATION: ruleReference.

La respuesta contiene:

- celebrationAttemptId: UUID.

- decision: PENDING, COMMITTED o ABORTED.

- contractReference: cuando confirma.

- contractualEffects: Code\[\].

- pendingConditions: ContractualCondition\[\].

- pendingComponents: pendientes comunes.

Se utiliza 201 para contrato nuevo confirmado, 200 para resultado recuperado y 202 para intento durable pendiente.

### **8.2. ContractCelebrationAttempt**

Contiene:

- celebrationAttemptId.

- actionId y operationId.

- proposalReference.

- contentHash, correspondiente al manifiesto aceptado.

- participantSnapshotHash.

- purpose.

- sourceContractReference, cuando corresponde.

- requiredAuthorities: Code\[\].

- preparationReferences: ObjectReference\[\].

- decisionDeadline: Instant.

- processingState.

- decision.

- decidedAt, cuando existe decisión.

- contractReference o resultingContractVersion, según finalidad.

- pendingComponents.

processingState admite PREPARING, READY_TO_DECIDE, COMMITTED, ABORTED y RECOVERY_REQUIRED.

Solo puede existir un intento sin resolver por versión de propuesta.

### **8.3. Consulta y aborto**

| **operationId técnico**         | **Método y ruta**                                                 |
|---------------------------------|-------------------------------------------------------------------|
| getContractCelebrationAttempt   | GET /v1/contract-celebration-attempts/{attemptId}                 |
| requestContractCelebrationAbort | POST /v1/contract-celebration-attempts/{attemptId}/abort-requests |

El aborto recibe contexto, reasonCode y preparationReference cuando lo solicita una autoridad participante.

Devuelve ABORTED, ALREADY_COMMITTED o PENDING, con decisión y referencias.

Una referencia desconocida no produce aborto confirmado.

### **8.4. Confirmación**

Contratos comprueba:

- Propuesta y versión.

- Integridad del manifiesto.

- Vigencia.

- Conformidades efectivas.

- Retiros.

- Conformidad del banco.

- Preparaciones obligatorias.

- Condiciones previas.

La decisión y la creación del contrato se confirman conjuntamente.

La notificación pendiente a una autoridad no elimina una celebración confirmada.

## **9. Contrato y condiciones**

### **9.1. Contract**

Contiene:

- contractId: UUID.

- version: Version.

- proposalReference.

- modelReference.

- productReference.

- participants: Participant\[\].

- manifest: Manifest.

- economicTerms: EconomicTerms.

- conditions: ContractualCondition\[\].

- celebrationAttemptId: UUID.

- celebratedAt: Instant.

- Estados por dimensión.

| **Dimensión**             | **Valores**                                  |
|---------------------------|----------------------------------------------|
| lifecycleStatus           | CELEBRATED, TERMINATION_PENDING, TERMINATED. |
| effectStatus              | SUSPENDED_BY_CONDITION, EFFECTIVE, CEASED.   |
| fulfillmentStatus         | PENDING, FULFILLED, BREACHED, UNDER_REVIEW.  |
| residualObligationsStatus | NONE, PENDING, RESOLVED.                     |

La constitución operativa se consulta en Solicitudes de productos.

### **9.2. CI-03-014 — Acreditar condición**

**Operación:** recordContractConditionFulfillment  
**Interfaz:** POST /v1/contracts/{contractId}/condition-fulfillments

Recibe contexto, conditionId y modalidad:

- RECORD_FACT: referencia del hecho, momento, resultado SATISFIED o FAILED y evidencias.

- RECORD_WAIVER: aprobación de dispensa y fundamento.

El proveedor verifica competencia del emisor y posibilidad de dispensa.

Devuelve condición actualizada, consecuencia contractual y pendientes.

Una instrucción enviada no demuestra cumplimiento.

## **10. Modificación y renovación**

| **Contrato** | **operationId técnico**    | **Método y ruta**                                        |
|--------------|----------------------------|----------------------------------------------------------|
| CI-03-017    | requestContractAmendment   | POST /v1/contracts/{contractId}/amendments               |
| CI-03-018    | requestContractRenewal     | POST /v1/contracts/{contractId}/renewals                 |
| CI-03-019    | registerContractNonRenewal | POST /v1/contracts/{contractId}/non-renewal-instructions |

### **10.1. Modificación**

Recibe contexto, amendmentType, cambios tipados, fundamentos, fecha propuesta y participantes afectados.

Las modalidades son:

- BILATERAL.

- AGREED_FORMULA.

- PERMITTED_UNILATERAL.

- UNCONDITIONAL_IMPROVEMENT.

- MANDATORY_CHANGE.

Cada cambio identifica un campo permitido por el modelo y su valor tipado. Los cambios estructurales utilizan una nueva
definición contractual completa, sin parches libres.

Devuelve amendmentId, propuesta cuando corresponde, versión resultante si confirmó, fecha efectiva y aplicación
operativa pendiente.

La bilateral utiliza entrega, aceptación e intento de decisión.

El preaviso desfavorable permitido es de sesenta días corridos desde el día siguiente a la notificación acreditada.

### **10.2. Renovación o prórroga**

Recibe contexto, período, modalidad, condiciones propuestas y evaluaciones.

Las modalidades son AUTOMATIC_AGREED, EXPRESS_RENEWAL y EXTENSION.

Devuelve período anterior y resultante, propuesta o intento cuando corresponde, fechas y pendientes.

La automática exige pacto habilitante. La expresa utiliza contenido aceptado y decisión durable.

### **10.3. No renovación**

Recibe contexto y período destinatario.

Devuelve REGISTERED, ALREADY_RENEWED o PENDING.

La instrucción y la renovación compiten sobre el mismo período.

## **11. Arrepentimiento, terminación y constancias**

| **Contrato** | **operationId técnico**        | **Método y ruta**                                         |
|--------------|--------------------------------|-----------------------------------------------------------|
| CI-03-020    | requestContractWithdrawalRight | POST /v1/contracts/{contractId}/withdrawal-right-requests |
| CI-03-021    | requestContractTermination     | POST /v1/contracts/{contractId}/terminations              |
| CI-03-022    | getContract                    | GET /v1/contracts/{contractId}                            |
| CI-03-023    | requestContractCertificate     | POST /v1/contracts/{contractId}/certificates              |
| CI-03-024    | getContractAction              | GET /v1/contract-actions/{actionId}                       |

### **11.1. Arrepentimiento**

Recibe contexto, solicitante, representación cuando corresponde y canal.

Devuelve:

- withdrawalRequestId: UUID.

- eligibility: ELIGIBLE, OUT_OF_PERIOD, NOT_APPLICABLE o PENDING_EVIDENCE.

- Cálculo de plazo.

- Usos impedidos.

- Instrucciones de restitución.

- Pendientes.

Para las contrataciones alcanzadas, el plazo es de diez días hábiles desde el día siguiente al hecho más tardío entre:

- Recepción acreditada del contrato por el participante.

- Disponibilidad efectiva del producto contratado.

La recepción contractual se comprueba mediante la evidencia de entrega. La disponibilidad efectiva se obtiene del
propietario operativo del producto.

La disponibilidad de una copia documental no sustituye la disponibilidad efectiva del producto.

El cálculo conserva:

- contractReceivedAt: Instant, cuando está acreditado.

- productAvailableAt: Instant, cuando está acreditado.

- contractReceiptEvidenceReference: ObjectReference, cuando existe.

- productAvailabilityEvidenceReference: ObjectReference, cuando existe.

- calendarReference: VersionedReference.

- deadlineDate: Date, cuando puede determinarse.

- missingEvidenceCodes: Code\[\].

Si falta evidencia necesaria, no se presume vencido el plazo. Se devuelve PENDING_EVIDENCE y se conserva la solicitud
recibida.

El uso del producto no convierte automáticamente el arrepentimiento en improcedente. Sus consecuencias se determinan
conforme al tratamiento funcional aplicable.

Las instrucciones se comunican en dos días hábiles. En préstamo desembolsado, la restitución utiliza diez días hábiles
desde esa comunicación.

El tratamiento operativo depende del producto y se deriva a su propietario.

### **11.2. Terminación**

Recibe contexto, causa, fecha solicitada cuando procede y fundamentos.

Devuelve terminationRequestId, fecha efectiva cuando está determinada, prestaciones impedidas, obligaciones residuales y
pendientes.

La petición se recibe con deuda. La terminación no certifica saldo cero ni liberación integral.

Dentro de dos días hábiles se informa lo aplicado y lo pendiente.

### **11.3. Consulta contractual**

Admite version o referenceTime, de forma excluyente, además de finalidad y secciones solicitadas.

Las secciones autorizadas comprenden participantes, condiciones, documentos, efectos, modificaciones, renovaciones y
obligaciones residuales.

La deuda actual pertenece al acreedor.

### **11.4. Constancias**

Recibe contexto, versión o momento de referencia, certificateTypeCode y destinatario.

Devuelve solicitud, contenido acreditado, documento y entrega.

Los tipos documentales se validan contra el catálogo autorizado. Una constancia de cancelación económica exige
confirmación del acreedor.

### **11.5. Seguimiento**

La consulta específica y la consulta común por actionId recuperan objeto, contenido, versiones, intento, decisión,
efectos y pendientes.

No dependen de que el consumidor haya recibido el identificador generado del contrato.

## **12. Preparaciones de autoridades**

Contratos solicita las preparaciones exigibles a sus proveedores.

Cada solicitud vincula coordinador, intento, propuesta, versión, manifiesto, actores, acto, objetos, importes
pertinentes y límite de decisión.

Las autoridades preparan únicamente su competencia.

Una consulta informativa no constituye preparación.

Un rechazo obligatorio impide confirmar y exige resolver las preparaciones ya obtenidas.

Un cambio incompatible posterior bloquea nuevas preparaciones y solicita resolver el intento existente. La decisión
confirmada no se borra.

Las preparaciones inciertas no se liberan por vencimiento ni por expiración del trabajador.

## **13. Eventos**

### **13.1. Canales y tipos**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                          | **Tema**                           | **Agregado**  |
|--------------|-----------------------------------|------------------------------------|---------------|
| CI-03-025    | ProductApplicationOutcomeRecorded | banking.product-application.events | Solicitud.    |
| CI-03-026    | ContractProposalAvailable         | banking.contract.events            | Propuesta.    |
| CI-03-027    | ContractCelebrated                | banking.contract.events            | Contrato.     |
| CI-03-028    | ContractEffectsChanged            | banking.contract.events            | Contrato.     |
| CI-03-029    | ProductConstitutionVerified       | banking.product-application.events | Constitución. |
| CI-03-030    | ContractAmendmentEffective        | banking.contract.events            | Contrato.     |
| CI-03-031    | ContractRenewalEffective          | banking.contract.events            | Contrato.     |
| CI-03-032    | ContractExitOutcomeRecorded       | banking.contract.events            | Contrato.     |
| CI-03-033    | ContractModelChanged              | banking.contract.events            | Modelo.       |

La versión inicial de los esquemas es 1. La clave de partición es aggregateId.

Los valores de aggregateType son PRODUCT_APPLICATION, CONTRACT_PROPOSAL, CONTRACT, PRODUCT_CONSTITUTION y
CONTRACT_MODEL.

### **13.2. Payloads**

Los campos siguientes son obligatorios salvo condición expresa.

**CI-03-025 — ProductApplicationOutcomeRecorded**

- applicationId: UUID.

- applicationVersion: Version.

- assessmentReference: ObjectReference.

- itemResults: arreglo no vacío con itemId, outcome y conditions.

- requirementReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

- validUntil: Instant, cuando el resultado tiene vigencia.

outcome utiliza los valores de viabilidad.

**CI-03-026 — ContractProposalAvailable**

- proposalId: UUID.

- proposalVersion: Version.

- applicationId: UUID.

- applicationItemId: UUID.

- purpose: finalidad de propuesta.

- modelReference: VersionedReference.

- productReference: VersionedReference.

- manifestReference: ObjectReference.

- manifestHash: Hash.

- requiredParticipantIds: UUID\[\].

- validUntil: Instant.

**CI-03-027 — ContractCelebrated**

- contractReference: VersionedReference.

- proposalReference: VersionedReference.

- celebrationAttemptId: UUID.

- manifestHash: Hash.

- celebratedAt: Instant.

- effectStatus: estado contractual.

- conditions: ContractualCondition\[\].

- pendingComponents: pendientes comunes.

Este evento solo se publica para una celebración confirmada.

**CI-03-028 — ContractEffectsChanged**

- contractReference: VersionedReference.

- previousEffectStatus: estado contractual.

- newEffectStatus: estado contractual.

- effectiveAt: Instant.

- causeReference: ObjectReference.

- affectedConditionIds: UUID\[\].

- pendingComponents: pendientes comunes.

**CI-03-029 — ProductConstitutionVerified**

- constitutionId: UUID.

- applicationId: UUID.

- applicationItemId: UUID.

- contractReference: VersionedReference.

- operationalOwnerService: Code.

- operationalProductReference: ObjectReference.

- verifiedOutcome: CONSTITUTED, PARTIALLY_ENABLED o ENABLED.

- confirmedCapabilities: Code\[\].

- evidenceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Un resultado incierto no se publica como constitución comprobada.

**CI-03-030 — ContractAmendmentEffective**

- contractId: UUID.

- amendmentId: UUID.

- previousContractVersion: Version.

- resultingContractVersion: Version.

- amendmentType: modalidad de modificación.

- changedFieldCodes: Code\[\].

- manifestHash: Hash.

- effectiveAt: Instant.

- pendingOperationalApplications: pendientes comunes.

**CI-03-031 — ContractRenewalEffective**

- contractReference: VersionedReference.

- renewalId: UUID.

- renewalType: modalidad de renovación.

- previousPeriodReference: ObjectReference.

- resultingPeriodReference: ObjectReference.

- termsReference: VersionedReference.

- effectiveAt: Instant.

- pendingComponents: pendientes comunes.

**CI-03-032 — ContractExitOutcomeRecorded**

- contractReference: VersionedReference.

- exitRequestId: UUID.

- exitType: WITHDRAWAL_RIGHT o TERMINATION.

- outcome: APPLIED, PARTIALLY_APPLIED, REJECTED o PENDING.

- receivedAt: Instant.

- preventedServiceCodes: Code\[\].

- residualObligations: ObjectReference\[\].

- pendingComponents: pendientes comunes.

- effectiveAt: Instant, cuando existe efecto.

- reasonCode: Code, si rechaza.

El evento confirma el resultado registrado, sin presentar pendientes como ejecución integral.

**CI-03-033 — ContractModelChanged**

- modelId: UUID.

- modelVersion: Version.

- changeType: CREATED, VERSION_CREATED, APPROVED, ENABLED, REJECTED o WITHDRAWN.

- status: estado de modelo.

- compatibleProductReferences: VersionedReference\[\].

- existingProposalTreatment: NOT_AFFECTED, CONTINUE_AUTHORIZED, REVIEW_REQUIRED o WITHDRAW_REQUIRED.

- approvalReference: ObjectReference, para aprobación o habilitación.

- effectiveFrom: Instant, cuando el cambio tiene vigencia.

### **13.3. Consumidores**

| **Evento**                        | **Consumidores autorizados**                                     |
|-----------------------------------|------------------------------------------------------------------|
| Resultado de solicitud            | Contratos, Gestión administrativa y responsables de componentes. |
| Propuesta disponible              | Solicitudes de productos y Gestión administrativa.               |
| Celebración y cambio de efectos   | Propietarios operativos y controles competentes.                 |
| Constitución comprobada           | Contratos y responsables de componentes relacionados.            |
| Modificación, renovación o salida | Propietarios afectados y Riesgos cuando corresponde.             |
| Cambio de modelo                  | Catálogo, Condiciones comerciales y Solicitudes de productos.    |

Los eventos no contienen expedientes completos ni sustituyen la consulta durable de una decisión.

Una reentrega no constituye otro producto, no repite un desembolso ni aplica nuevamente una modificación.

## **14. Seguridad, unicidad y recuperación**

Se separan permisos para administrar modelos, aportar antecedentes, evaluar viabilidad, preparar propuestas, entregar
contenido, aceptar, celebrar, modificar, renovar, terminar y consultar.

Una aceptación procede de una intervención verificable del participante.

Las decisiones especializadas solo se acreditan desde su propietario competente.

Se exige:

- Contrato único por propuesta inicial utilizada.

- Decisión única por intento.

- Un intento no resuelto por versión de propuesta.

- Aceptación efectiva única por participante y contenido.

- Renovación única por contrato y período.

- Cumplimiento único por condición y hecho.

- Constitución recuperable por su identidad original.

Los servicios utilizan outbox, inbox y actuaciones persistentes.

Ante una respuesta perdida se consulta actionId. Si existe intento, se recupera su decisión antes de iniciar una
actuación incompatible.

Los controles de autorización se mantienen en consultas históricas y recuperaciones idempotentes.

## **15. Errores particulares**

| **Código**                     | **HTTP** | **Significado**                           |
|--------------------------------|----------|-------------------------------------------|
| CONTENT_CONFLICT               | 409      | Identidad reutilizada con otro contenido. |
| VERSION_CONFLICT               | 409      | Versión incompatible.                     |
| MODEL_NOT_ENABLED              | 422      | Modelo no utilizable.                     |
| PRODUCT_MODEL_INCOMPATIBLE     | 422      | Producto incompatible con el modelo.      |
| MANDATORY_REQUIREMENT_PENDING  | 422      | Falta requisito previo.                   |
| PROPOSAL_EXPIRED               | 422      | Propuesta vencida.                        |
| PROPOSAL_SUPERSEDED            | 409      | Propuesta sustituida.                     |
| MANIFEST_MISMATCH              | 409      | Contenido diferente.                      |
| DELIVERY_EVIDENCE_REQUIRED     | 422      | Falta acceso previo al contenido exigido. |
| ACCEPTANCE_REQUIRED            | 422      | Falta conformidad.                        |
| ACCEPTANCE_WITHDRAWN           | 422      | Conformidad retirada.                     |
| AUTHORITY_PREPARATION_REJECTED | 422      | Preparación obligatoria rechazada.        |
| ATTEMPT_ALREADY_ABORTED        | 409      | Intento no confirmable.                   |
| ATTEMPT_CONTENT_CONFLICT       | 409      | Intento con contenido diferente.          |
| CONDITION_SOURCE_UNAUTHORIZED  | 403      | Emisor no competente.                     |
| CONSTITUTION_MISMATCH          | 409      | Producto incompatible con el acuerdo.     |
| NOTICE_EVIDENCE_REQUIRED       | 422      | Falta aviso acreditado.                   |
| RENEWAL_NOT_AGREED             | 422      | No existe fundamento de renovación.       |
| DEPENDENCY_UNAVAILABLE         | 503      | Dependencia necesaria indisponible.       |

Una consulta de intento confirmado o abortado responde 200 con su decisión.

Una petición de aborto posterior a la confirmación devuelve ALREADY_COMMITTED. No elimina el contrato.

Si la actuación quedó aceptada durablemente y continúa pendiente, devuelve 202 y seguimiento.

## **16. Escenarios de aceptación**

| **Escenario**                                                  | **Resultado exigido**                                                     |
|----------------------------------------------------------------|---------------------------------------------------------------------------|
| Participantes aceptan versiones diferentes                     | No se considera totalmente aceptada la propuesta.                         |
| Cambia el orden documental                                     | Cambia el manifiesto vinculante.                                          |
| Cambia únicamente la representación decimal equivalente        | La canonicalización conserva la misma representación normalizada.         |
| Se retira una aceptación mientras se celebra                   | Retiro y confirmación se ordenan localmente.                              |
| Se pierde la respuesta de celebración                          | La consulta por actionId recupera el mismo intento y contrato.            |
| Una autoridad solicita aborto                                  | Compite con la confirmación; se conserva una sola decisión.               |
| Vence una preparación sin decisión conocida                    | Permanece pendiente de resolución.                                        |
| El contrato confirma y falla la habilitación                   | Se conserva el contrato y se sigue la constitución pendiente.             |
| Llega una notificación duplicada                               | No repite efectos.                                                        |
| Una modificación requiere preaviso                             | No se aplica antes de cumplirlo.                                          |
| La no renovación compite con la renovación                     | Se resuelve sobre el mismo período.                                       |
| El producto queda disponible después de recibir el contrato    | El plazo de arrepentimiento parte del día siguiente a esa disponibilidad. |
| El contrato se recibe después de quedar disponible el producto | El plazo parte del día siguiente a la recepción contractual.              |
| Falta evidencia de uno de los hechos necesarios                | No se presume vencido el plazo de arrepentimiento.                        |
| Solo está disponible una copia documental                      | No se utiliza como prueba de disponibilidad efectiva del producto.        |
| La terminación mantiene deuda                                  | Se informa la obligación residual sin impedir la recepción.               |
| Se pierde una confirmación operativa                           | Se recupera la constitución original sin crear otro producto.             |

Las pruebas de decisión, bloqueo y unicidad utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
