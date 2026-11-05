# **Diseño de persistencia — Módulo 3. Contratación**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Contratación.

| **Servicio**             | **Responsabilidad**                                                                                      |
|--------------------------|----------------------------------------------------------------------------------------------------------|
| Solicitudes de productos | Solicitudes, componentes, requisitos, antecedentes, viabilidad y coordinación de constitución operativa. |
| Contratos                | Modelos, propuestas, manifiestos, entregas, aceptaciones, celebración y ciclo contractual.               |

Comprende los contratos CI-03-001 a CI-03-033.

Cada servicio mantiene su propia base PostgreSQL, usuario y migraciones. No existen claves foráneas, consultas SQL ni
transacciones entre ambas bases.

Solicitud, viabilidad, aceptación, celebración, entrada en efecto y constitución operativa conservan identidades y
resultados diferentes.

Contratos no administra saldos, concede crédito ni recalcula las determinaciones económicas de los propietarios de
productos.

## **2. Convenciones físicas**

### **2.1. Notación**

En las tablas de este documento:

- **N:** columna obligatoria, NOT NULL.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Una columna condicional se declara S y su presencia se controla mediante la restricción indicada.

Todas las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en el orden de búsqueda. Las PK, UQ y restricciones de exclusión crean sus
índices correspondientes.

### **2.2. Tipos y restricciones generales**

| **Dato**               | **Tipo PostgreSQL** | **Restricción**                        |
|------------------------|---------------------|----------------------------------------|
| Identificador          | uuid                | Estable y no reutilizable.             |
| Versión de negocio     | bigint              | Mayor que cero.                        |
| Versión de control     | bigint              | Mayor o igual que cero.                |
| Código                 | varchar(80)         | No vacío; valor registrado.            |
| Referencia visible     | varchar(120)        | No vacía.                              |
| Nombre                 | varchar(200)        | No vacío.                              |
| Fundamento             | varchar(4000)       | No vacío cuando está presente.         |
| Instante               | timestamptz         | Intercambio en UTC.                    |
| Fecha                  | date                | Sin conversión de zona.                |
| Importe contractual    | numeric(24,8)       | No negativo.                           |
| Tasa                   | numeric(24,12)      | Según la definición contractual.       |
| Moneda                 | varchar(3)          | ARS o USD.                             |
| Hash                   | char(64)            | SHA-256 hexadecimal en minúsculas.     |
| Contenido estructurado | jsonb               | Validado contra un esquema registrado. |

La precisión y escala se comprueban antes de persistir. El redondeo implícito de PostgreSQL no constituye validación de
entrada.

Los plazos bancarios utilizan la zona America/Argentina/Buenos_Aires y calendarios versionados.

### **2.3. Bloques de columnas**

Los siguientes bloques representan columnas físicas que se incorporan a cada tabla que los menciona. No implican
herencia de tablas.

**Bloque T — Creación**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| created_action_id | uuid        | N               |
| created_at        | timestamptz | N               |

created_action_id referencia service_action(action_id).

**Bloque C — Control mutable**

| **Columna**  | **Tipo**    | **Nulabilidad** |
|--------------|-------------|-----------------|
| version      | bigint      | N               |
| lock_version | bigint      | N               |
| updated_at   | timestamptz | N               |

version \> 0 y lock_version \>= 0.

**Bloque S — Contenido versionado**

| **Columna**              | **Tipo** | **Nulabilidad** |
|--------------------------|----------|-----------------|
| schema_version           | bigint   | N               |
| canonicalization_version | bigint   | N               |
| content_hash             | char(64) | N               |

Las dos versiones son positivas.

### **2.4. Tablas comunes**

Ambos servicios incorporan las tablas del documento principal:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Contratos incorpora además:

- execution_identity_control.

- execution_attempt.

El intento de celebración utiliza attempt_type = CONTRACT_CELEBRATION.

Este contrato no permite confirmar un aborto para una identidad desconocida. Por ello, el aborto de celebración no crea
una barrera de aborto anticipado.

## **3. Contenido estructurado**

### **3.1. Registro de esquemas**

Los JSONB de este módulo utilizan los siguientes esquemas. Su versión se conserva en la columna indicada o en el
envoltorio de cada estructura.

| **Esquema**                  | **Contenido**                                                                 |
|------------------------------|-------------------------------------------------------------------------------|
| ParticipantRights            | Códigos de derechos y obligaciones, sin duplicados.                           |
| EvidenceReferences           | Referencias con propietario, tipo, identidad y versión cuando corresponde.    |
| PendingComponents            | Componente, propietario, objeto, estado y seguimiento.                        |
| SpecialistDecision           | Emisor, decisión, versión, alcance, vigencia y evidencias.                    |
| ContractModelRules           | Reglas de participantes, aceptación, efectos y ciclo contractual.             |
| FieldRestrictions            | Restricciones compatibles con el tipo del campo.                              |
| ContractParticularValues     | Valores particulares discriminados por tipo.                                  |
| ContractEconomicTerms        | Moneda, conceptos, tasas y determinaciones especializadas.                    |
| ContractConditionDefinitions | Definiciones contractuales y sus condiciones de exigibilidad.                 |
| ContractStructuredContent    | Contenido íntegro vinculante de una propuesta o versión contractual.          |
| ConstitutionCommand          | Contrato, producto, participantes, capacidades y condiciones de constitución. |
| ConstitutionOutcome          | Resultado operativo y sus evidencias.                                         |
| ContractAmendmentChanges     | Cambios tipados o definición contractual completa.                            |
| ContractExitInstructions     | Prestaciones impedidas, restituciones e instrucciones por propietario.        |
| ContractCertificateFacts     | Hechos acreditados, fuentes y corte de referencia.                            |
| CalendarCalculation          | Calendario, regla, entradas, resultado y evidencia del cálculo.               |

No se admiten campos desconocidos, scripts, expresiones SQL ni mapas libres.

Las referencias que intervienen en unicidad, pertenencia, concurrencia o recuperación se almacenan también en columnas
explícitas.

### **3.2. Valores particulares**

Cada valor contiene fieldCode, valueType y exclusivamente el valor correspondiente:

| **Tipo**  | **Valor**                          |
|-----------|------------------------------------|
| TEXT      | Texto.                             |
| DATE      | Fecha.                             |
| INSTANT   | Instante.                          |
| AMOUNT    | Importe y moneda.                  |
| RATE      | Tasa.                              |
| INTEGER   | Entero dentro del rango de bigint. |
| BOOLEAN   | Booleano.                          |
| CODE      | Código admitido.                   |
| REFERENCE | Referencia estructurada.           |

Los valores se contrastan con los campos de la versión exacta del modelo. No se aceptan campos adicionales ni
sustituciones prohibidas.

### **3.3. Condiciones económicas**

ContractEconomicTerms conserva:

- Moneda contractual.

- Conceptos monetarios y sus propietarios.

- Modalidad excluyente FIXED_AMOUNT o CALCULATED.

- Importes fijos o referencias de cálculo.

- Definiciones de tasas.

- Referencias versionadas de cronogramas y cálculos especializados.

Una tasa fija exige valor. Una variable exige fuente, margen, regla de revisión y tratamiento de indisponibilidad.

Contratos conserva estas determinaciones y su procedencia; no vuelve a calcularlas.

## **4. Solicitudes de productos**

### **4.1. product_application**

| **Columna**               | **Tipo**     | **Nulabilidad** |
|---------------------------|--------------|-----------------|
| application_id            | uuid         | N, PK           |
| application_reference     | varchar(120) | N               |
| applicant_party_id        | uuid         | N               |
| customer_id               | uuid         | S               |
| channel_code              | varchar(80)  | N               |
| purpose_code              | varchar(80)  | N               |
| previous_application_id   | uuid         | S               |
| status                    | varchar(40)  | N               |
| received_at               | timestamptz  | N               |
| original_received_at      | timestamptz  | S               |
| original_receipt_evidence | jsonb        | S               |
| T                         | Bloque       |                 |
| C                         | Bloque       |                 |

Restricciones:

- UQ sobre application_reference.

- FK de previous_application_id a la misma tabla.

- La solicitud anterior no puede ser la propia solicitud.

- original_received_at y su evidencia están presentes conjuntamente.

- La recepción original no puede ser posterior a la recepción efectiva.

Estados:

RECEIVED, AWAITING_INFORMATION, UNDER_REVIEW, VIABLE, REJECTED, WITHDRAWN, EXPIRED, CONTRACTED, CONSTITUTION_PENDING,
COMPLETED y REPAIR_REQUIRED.

Índices:

- (applicant_party_id, received_at, application_id).

- (status, updated_at, application_id).

- (customer_id, received_at, application_id), donde customer_id IS NOT NULL.

- (previous_application_id, application_id), donde exista solicitud anterior.

Una reapertura crea otra solicitud. No reactiva precios, aprobaciones o aceptaciones vencidos.

### **4.2. application_participant**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| participant_id             | uuid        | N, PK           |
| application_id             | uuid        | N               |
| party_id                   | uuid        | N               |
| role_code                  | varchar(80) | N               |
| acceptance_required        | boolean     | N               |
| rights_codes               | jsonb       | N               |
| obligation_codes           | jsonb       | N               |
| joint_group_id             | uuid        | S               |
| representation_reference   | jsonb       | S               |
| participant_schema_version | bigint      | N               |

FK a la solicitud.

UQ:

- (application_id, party_id, role_code).

- (application_id, participant_id).

Índice inverso: (party_id, application_id, role_code).

Los códigos de derechos y obligaciones son arreglos sin duplicados. La representación se conserva como antecedente
remoto; no se acredita por esta tabla.

### **4.3. application_item**

| **Columna**          | **Tipo**      | **Nulabilidad** |
|----------------------|---------------|-----------------|
| item_id              | uuid          | N, PK           |
| application_id       | uuid          | N               |
| product_service      | varchar(80)   | N               |
| product_id           | uuid          | N               |
| product_version      | bigint        | N               |
| modality_code        | varchar(80)   | N               |
| currency             | varchar(3)    | S               |
| amount               | numeric(24,8) | S               |
| term_quantity        | integer       | S               |
| term_unit            | varchar(10)   | S               |
| status               | varchar(40)   | N               |
| condition_references | jsonb         | N               |
| proposal_references  | jsonb         | N               |
| contract_references  | jsonb         | N               |
| T                    | Bloque        |                 |
| C                    | Bloque        |                 |

FK a la solicitud y UQ (application_id, item_id).

Restricciones:

- Producto versionado con versión positiva.

- amount \>= 0 y, si existe, exige moneda.

- Cantidad y unidad de plazo presentes conjuntamente.

- Cantidad positiva; unidad DAYS, MONTHS o YEARS.

- La modalidad determina si moneda, importe y plazo son obligatorios, opcionales o inadmisibles.

Los estados del componente utilizan el catálogo del trámite: RECEIVED, AWAITING_INFORMATION, UNDER_REVIEW, VIABLE,
REJECTED, WITHDRAWN, EXPIRED, CONTRACTED, CONSTITUTION_PENDING, COMPLETED y REPAIR_REQUIRED.

Índices:

- (application_id, status, item_id).

- (product_service, product_id, application_id, item_id).

### **4.4. application_item_participant**

Columnas obligatorias:

| **Columna**    | **Tipo** |
|----------------|----------|
| application_id | uuid     |
| item_id        | uuid     |
| participant_id | uuid     |

PK (item_id, participant_id).

FK compuestas:

- (application_id, item_id) a application_item.

- (application_id, participant_id) a application_participant.

Índice inverso (participant_id, item_id).

### **4.5. application_item_dependency**

Columnas obligatorias:

| **Columna**       | **Tipo** |
|-------------------|----------|
| application_id    | uuid     |
| dependent_item_id | uuid     |
| required_item_id  | uuid     |

PK (dependent_item_id, required_item_id).

Ambos componentes referencian la misma solicitud mediante FK compuestas. Se prohíbe la autorreferencia.

Índice inverso (required_item_id, dependent_item_id).

La ausencia de ciclos se comprueba bajo bloqueo de product_application.

### **4.6. Retiro**

**application_withdrawal**

| **Columna**        | **Tipo**      | **Nulabilidad** |
|--------------------|---------------|-----------------|
| withdrawal_id      | uuid          | N, PK           |
| application_id     | uuid          | N               |
| reason             | varchar(4000) | N               |
| received_at        | timestamptz   | N               |
| outcome            | varchar(40)   | N               |
| pending_components | jsonb         | N               |
| T                  | Bloque        |                 |

FK a solicitud. UQ sobre created_action_id.

outcome: APPLIED, PARTIALLY_APPLIED, PENDING o SUBSEQUENT_TREATMENT_REQUIRED.

Índice (application_id, received_at, withdrawal_id).

**application_withdrawal_item**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| withdrawal_id       | uuid        | N               |
| application_id      | uuid        | N               |
| item_id             | uuid        | N               |
| outcome             | varchar(40) | N               |
| decision_references | jsonb       | N               |
| pending_components  | jsonb       | N               |
| updated_at          | timestamptz | N               |
| lock_version        | bigint      | N               |

PK (withdrawal_id, item_id).

Se declara UQ (withdrawal_id, application_id) en la cabecera y FK compuesta desde el detalle. El componente referencia
(application_id, item_id).

Una celebración incierta impide declarar que el retiro quedó aplicado sin consecuencias contractuales.

## **5. Requerimientos y presentaciones**

### **5.1. application_requirement**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| requirement_id        | uuid        | N, PK           |
| application_id        | uuid        | N               |
| application_item_id   | uuid        | S               |
| cause                 | varchar(30) | N               |
| channel_code          | varchar(80) | N               |
| made_available_at     | timestamptz | N               |
| availability_evidence | jsonb       | N               |
| calendar_service      | varchar(80) | N               |
| calendar_id           | uuid        | N               |
| calendar_version      | bigint      | N               |
| original_due_at       | timestamptz | N               |
| effective_due_at      | timestamptz | N               |
| reminder_at           | timestamptz | N               |
| status                | varchar(30) | N               |
| T                     | Bloque      |                 |
| C                     | Bloque      |                 |

FK a solicitud y FK opcional (application_id, application_item_id) al componente.

UQ (application_id, requirement_id).

Causas: INITIAL, FINAL_CORRECTION y BANK_OMISSION.

Estados: OPEN, UNDER_REVIEW, SATISFIED, EXPIRED y CANCELLED.

Índices:

- (application_id, requirement_id).

- (effective_due_at, requirement_id), donde status IN ('OPEN','UNDER_REVIEW').

- (reminder_at, requirement_id), donde status = 'OPEN'.

### **5.2. application_requirement_item**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| requirement_item_id | uuid        | N, PK           |
| application_id      | uuid        | N               |
| requirement_id      | uuid        | N               |
| requirement_code    | varchar(80) | N               |
| responsible_service | varchar(80) | N               |
| status              | varchar(30) | N               |
| last_submission_id  | uuid        | S               |
| observations        | jsonb       | N               |
| updated_at          | timestamptz | N               |
| lock_version        | bigint      | N               |

FK compuesta a requerimiento.

UQ:

- (application_id, requirement_item_id).

- (requirement_id, requirement_code, responsible_service).

last_submission_id, cuando existe, referencia una presentación de la misma solicitud mediante FK compuesta.

Estados: PENDING, SUBMITTED, UNDER_REVIEW, SATISFIED y OBSERVED.

Índice (requirement_id, status, requirement_item_id).

### **5.3. application_submission**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| submission_id                | uuid        | N, PK           |
| application_id               | uuid        | N               |
| received_at                  | timestamptz | N               |
| original_received_at         | timestamptz | S               |
| original_receipt_evidence    | jsonb       | S               |
| document_references          | jsonb       | N               |
| specialist_result_references | jsonb       | N               |
| review_status                | varchar(30) | N               |
| T                            | Bloque      |                 |
| S                            | Bloque      |                 |

FK a solicitud.

UQ sobre created_action_id y (application_id, submission_id).

Recepción original y evidencia presentes conjuntamente.

Estados: RECEIVED, UNDER_REVIEW, REVIEWED y OBSERVED.

Índice (application_id, received_at, submission_id).

### **5.4. application_submission_item**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| submission_id       | uuid        | N               |
| requirement_item_id | uuid        | N               |
| application_id      | uuid        | N               |
| sufficiency         | varchar(20) | N               |
| reasons             | jsonb       | N               |
| evidence_references | jsonb       | N               |
| reviewed_at         | timestamptz | S               |
| reviewed_action_id  | uuid        | S               |

PK (submission_id, requirement_item_id).

FK compuestas a presentación y punto de la misma solicitud. La actuación de revisión referencia service_action.

Suficiencia: PENDING, SUFFICIENT o INSUFFICIENT.

PENDING no tiene revisión final; los otros valores requieren fecha y actuación.

Índice inverso (requirement_item_id, submission_id).

### **5.5. application_channel_incident**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| incident_id         | uuid        | N, PK           |
| application_id      | uuid        | N               |
| channel_code        | varchar(80) | N               |
| interrupted_at      | timestamptz | N               |
| recovered_at        | timestamptz | S               |
| evidence_references | jsonb       | N               |
| T                   | Bloque      |                 |

FK a solicitud. Recuperación posterior a interrupción.

Índice (application_id, channel_code, interrupted_at, incident_id).

### **5.6. application_deadline_history**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| calculation_id                | uuid        | N, PK           |
| requirement_id                | uuid        | N               |
| calculation_version           | bigint      | N               |
| previous_due_at               | timestamptz | S               |
| resulting_due_at              | timestamptz | N               |
| cause_code                    | varchar(80) | N               |
| affected_requirement_item_ids | jsonb       | N               |
| calendar_calculation          | jsonb       | N               |
| evidence_references           | jsonb       | N               |
| calculated_at                 | timestamptz | N               |
| T                             | Bloque      |                 |

FK al requerimiento. UQ (requirement_id, calculation_version).

Los puntos afectados deben pertenecer al requerimiento; se comprueba bajo bloqueo de solicitud. El historial es
inmutable.

### **5.7. Reglas temporales**

| **Situación**                | **Tratamiento**                                               |
|------------------------------|---------------------------------------------------------------|
| Requerimiento inicial        | Treinta días corridos.                                        |
| Corrección final             | Quince días corridos.                                         |
| Omisión interna              | Treinta días para los puntos nuevos.                          |
| Inicio                       | Día siguiente a la puesta a disposición acreditada.           |
| Final inhábil                | Siguiente hábil.                                              |
| Recordatorio                 | Cinco días corridos antes.                                    |
| Canal indisponible al vencer | Hasta finalizar el segundo hábil posterior a su recuperación. |

Una presentación oportuna pendiente de revisión impide declarar vencidos los puntos que atiende.

Recepción, suficiencia, extensión y vencimiento se ordenan sobre el control de la solicitud.

## **6. Viabilidad y condiciones de solicitud**

### **6.1. application_viability_assessment**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| assessment_id     | uuid        | N, PK           |
| application_id    | uuid        | N               |
| evaluated_version | bigint      | N               |
| input_hash        | char(64)    | N               |
| assessed_at       | timestamptz | N               |
| valid_until       | timestamptz | S               |
| source_references | jsonb       | N               |
| processing_status | varchar(30) | N               |
| T                 | Bloque      |                 |
| schema_version    | bigint      | N               |

FK a solicitud. UQ (application_id, assessment_id).

Estados: PENDING, COMPLETED y REQUIRES_REVIEW.

La vigencia, cuando existe, es posterior a la evaluación.

Índice (application_id, assessed_at, assessment_id).

### **6.2. application_viability_item**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| assessment_id        | uuid        | N               |
| application_id       | uuid        | N               |
| item_id              | uuid        | N               |
| outcome              | varchar(30) | N               |
| condition_references | jsonb       | N               |
| observations         | jsonb       | N               |

PK (assessment_id, item_id).

FK compuestas a evaluación y componente de la misma solicitud.

Conclusiones: VIABLE, CONDITIONALLY_VIABLE, NOT_VIABLE y PENDING.

Índice (item_id, assessment_id).

### **6.3. application_specialist_result**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| specialist_result_id | uuid        | N, PK           |
| application_id       | uuid        | N               |
| item_id              | uuid        | N               |
| owner_service        | varchar(80) | N               |
| source_object_type   | varchar(80) | N               |
| source_object_id     | uuid        | N               |
| source_version       | bigint      | N               |
| decision_code        | varchar(80) | N               |
| scope_payload        | jsonb       | N               |
| valid_from           | timestamptz | N               |
| valid_until          | timestamptz | S               |
| evidence_references  | jsonb       | N               |
| checked_at           | timestamptz | N               |
| T                    | Bloque      |                 |
| S                    | Bloque      |                 |

FK al componente de la solicitud.

UQ (application_id, item_id, owner_service, source_object_type, source_object_id, source_version).

Índice (item_id, owner_service, valid_until, specialist_result_id).

Los resultados son inmutables. Una versión nueva del propietario genera otro registro.

### **6.4. application_condition**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| condition_id             | uuid        | N, PK           |
| application_id           | uuid        | N               |
| item_id                  | uuid        | N               |
| code                     | varchar(80) | N               |
| required_fact_code       | varchar(80) | N               |
| stage                    | varchar(50) | N               |
| responsible_service      | varchar(80) | N               |
| source_reference         | jsonb       | S               |
| deadline                 | timestamptz | S               |
| failure_consequence_code | varchar(80) | N               |
| waivable                 | boolean     | N               |
| current_status           | varchar(20) | N               |
| T                        | Bloque      |                 |
| C                        | Bloque      |                 |

FK al componente de la solicitud.

Etapas:

BEFORE_CELEBRATION, BEFORE_EFFECT, BEFORE_CONSTITUTION_OR_ENABLEMENT y BEFORE_DISBURSEMENT_OR_USE.

Estados: PENDING, SATISFIED, FAILED y WAIVED.

Índice (responsible_service, deadline, condition_id), donde current_status = 'PENDING'.

### **6.5. application_condition_result**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| result_id                 | uuid          | N, PK           |
| condition_id              | uuid          | N               |
| owner_service             | varchar(80)   | N               |
| fact_type                 | varchar(80)   | N               |
| fact_id                   | uuid          | N               |
| fact_version              | bigint        | N               |
| status                    | varchar(20)   | N               |
| occurred_at               | timestamptz   | N               |
| evidence_references       | jsonb         | N               |
| waiver_approval_reference | jsonb         | S               |
| reason                    | varchar(4000) | S               |
| T                         | Bloque        |                 |
| S                         | Bloque        |                 |

FK a condición.

UQ (condition_id, owner_service, fact_type, fact_id, fact_version).

Los resultados admiten SATISFIED, FAILED y WAIVED. WAIVED exige condición dispensable, aprobación competente y
fundamento. Los demás resultados no contienen aprobación de dispensa.

Índice (condition_id, occurred_at, result_id).

El propietario y la definición de la condición se comprueban antes de incorporar el resultado.

## **7. Constitución operativa**

### **7.1. product_constitution**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| constitution_id               | uuid        | N, PK           |
| application_id                | uuid        | N               |
| application_item_id           | uuid        | N               |
| contract_source_service       | varchar(80) | N               |
| contract_id                   | uuid        | N               |
| contract_version              | bigint      | N               |
| product_service               | varchar(80) | N               |
| product_id                    | uuid        | N               |
| product_version               | bigint      | N               |
| operational_owner_service     | varchar(80) | N               |
| requested_content             | jsonb       | N               |
| command_schema_version        | bigint      | N               |
| canonicalization_version      | bigint      | N               |
| content_hash                  | char(64)    | N               |
| remote_action_id              | uuid        | N               |
| status                        | varchar(40) | N               |
| operational_product_reference | jsonb       | S               |
| pending_components            | jsonb       | N               |
| T                             | Bloque      |                 |
| C                             | Bloque      |                 |

FK compuesta al componente.

UQ:

- (application_id, application_item_id, contract_source_service, contract_id).

- (operational_owner_service, remote_action_id).

Estados:

WAITING_CONDITIONS, PREPARING, AWAITING_RESULT, CONSTITUTED, PARTIALLY_ENABLED, ENABLED y REPAIR_REQUIRED.

Los tres estados de constitución comprobada exigen referencia del producto operativo.

Índices:

- (application_id, application_item_id, constitution_id).

- (operational_owner_service, updated_at, constitution_id), donde status IN
  ('PREPARING','AWAITING_RESULT','REPAIR_REQUIRED').

La versión contractual integra el contenido original, pero no permite crear otra constitución equivalente del mismo
componente y contrato.

### **7.2. product_constitution_capability**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| constitution_id     | uuid        | N               |
| capability_code     | varchar(80) | N               |
| responsible_service | varchar(80) | N               |
| required            | boolean     | N               |
| status              | varchar(20) | N               |
| evidence_references | jsonb       | N               |
| updated_at          | timestamptz | N               |

PK (constitution_id, capability_code) y FK a constitución.

Estados: PENDING, CONFIRMED, REJECTED y UNCERTAIN.

Índice (responsible_service, status, constitution_id, capability_code).

### **7.3. product_constitution_result**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| result_id                     | uuid        | N, PK           |
| constitution_id               | uuid        | N               |
| owner_service                 | varchar(80) | N               |
| source_result_id              | uuid        | N               |
| source_result_version         | bigint      | N               |
| remote_action_id              | uuid        | N               |
| outcome                       | varchar(30) | N               |
| operational_product_reference | jsonb       | S               |
| confirmed_capabilities        | jsonb       | N               |
| pending_conditions            | jsonb       | N               |
| evidence_references           | jsonb       | N               |
| received_at                   | timestamptz | N               |
| T                             | Bloque      |                 |
| S                             | Bloque      |                 |

FK a constitución.

UQ (owner_service, source_result_id, source_result_version).

Resultados: CONSTITUTED, PARTIALLY_ENABLED, ENABLED, REJECTED y UNCERTAIN.

Los resultados confirmados exigen producto operativo. La identidad del resultado es la misma por HTTP y por evento.

Índice (constitution_id, received_at, result_id).

### **7.4. Atomicidad y recuperación**

Antes de invocar al propietario se confirman conjuntamente:

- Constitución y contenido vinculante.

- Actuación remota estable.

- Trabajo persistente.

- Resultado recuperable de la actuación iniciadora.

La llamada ocurre fuera de la transacción.

La incorporación del resultado bloquea solicitud y constitución, en ese orden. Actualiza resultado, capacidades,
coordinación, solicitud, historia, inbox y outbox en una misma transacción.

Solo el propietario competente acredita la constitución. Una contradicción de producto, moneda, modalidad o
participantes impide informar correspondencia.

## **8. Modelos contractuales**

### **8.1. contract_model**

| **Columna**   | **Tipo**    | **Nulabilidad** |
|---------------|-------------|-----------------|
| model_id      | uuid        | N, PK           |
| code          | varchar(80) | N               |
| next_version  | bigint      | N               |
| event_version | bigint      | N               |
| lock_version  | bigint      | N               |
| T             | Bloque      |                 |

UQ sobre code. Contadores positivos; control no negativo.

### **8.2. contract_model_version**

| **Columna**                     | **Tipo**     | **Nulabilidad** |
|---------------------------------|--------------|-----------------|
| model_id                        | uuid         | N               |
| model_version                   | bigint       | N               |
| name                            | varchar(200) | N               |
| contractual_regime              | varchar(30)  | N               |
| compatible_products             | jsonb        | N               |
| model_rules                     | jsonb        | N               |
| general_terms_documents         | jsonb        | N               |
| permitted_variations            | jsonb        | N               |
| condition_definitions           | jsonb        | N               |
| automatic_formalization_allowed | boolean      | N               |
| valid_from                      | timestamptz  | S               |
| valid_until                     | timestamptz  | S               |
| status                          | varchar(30)  | N               |
| definition_revision             | bigint       | N               |
| approved_review_id              | uuid         | S               |
| T                               | Bloque       |                 |
| S                               | Bloque       |                 |
| lock_version                    | bigint       | N               |
| updated_at                      | timestamptz  | N               |

PK (model_id, model_version) y FK al modelo.

Regímenes: CONSUMER_BANKING, COMMERCIAL_FINANCING e INSTITUTIONAL.

Estados: DRAFT, PENDING_APPROVAL, APPROVED, ENABLED, REJECTED y WITHDRAWN.

Restricciones:

- Productos compatibles no vacíos.

- Final posterior al inicio, cuando ambos existen.

- APPROVED y ENABLED requieren revisión aprobatoria.

- ENABLED requiere comienzo de vigencia.

- Cada edición de borrador incrementa definition_revision.

- La huella incluye campos particulares y todos los componentes de la definición.

- La definición aprobada es inmutable, aunque cambie su estado administrativo.

Índices:

- (status, valid_from, model_id, model_version).

- (approved_review_id), donde esté presente.

### **8.3. contract_model_field**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| model_id                    | uuid        | N               |
| model_version               | bigint      | N               |
| field_code                  | varchar(80) | N               |
| value_type                  | varchar(20) | N               |
| required                    | boolean     | N               |
| source_service              | varchar(80) | N               |
| validation_code             | varchar(80) | N               |
| substitution_allowed        | boolean     | N               |
| restrictions                | jsonb       | N               |
| restrictions_schema_version | bigint      | N               |

PK (model_id, model_version, field_code).

FK a versión del modelo.

value_type utiliza los tipos del apartado 3.2. Las restricciones especifican longitud, rango, precisión, escala o
valores admitidos conforme al tipo.

### **8.4. contract_model_review**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| review_id             | uuid          | N, PK           |
| model_id              | uuid          | N               |
| model_version         | bigint        | N               |
| definition_revision   | bigint        | N               |
| reviewed_content_hash | char(64)      | N               |
| preparer_reference    | jsonb         | N               |
| preparer_person_id    | uuid          | N               |
| approver_reference    | jsonb         | S               |
| approver_person_id    | uuid          | S               |
| decision              | varchar(20)   | N               |
| approval_reference    | jsonb         | S               |
| reason                | varchar(4000) | S               |
| submitted_at          | timestamptz   | N               |
| decided_at            | timestamptz   | S               |
| T                     | Bloque        |                 |

FK a versión. UQ (review_id, model_id, model_version).

Decisiones: PENDING, APPROVED y REJECTED.

Las decisiones finales requieren aprobador y fecha. APPROVED exige referencia de aprobación y personas preparadora y
aprobadora distintas. REJECTED exige fundamento.

La FK compuesta de approved_review_id en la versión impide asociar una revisión de otro modelo o versión. La aplicación
comprueba revisión, huella y revisión de definición exactas.

Índice (model_id, model_version, submitted_at, review_id).

### **8.5. contract_model_enablement**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| enablement_id               | uuid        | N, PK           |
| model_id                    | uuid        | N               |
| model_version               | bigint      | N               |
| effective_from              | timestamptz | N               |
| effective_until             | timestamptz | S               |
| existing_proposal_treatment | varchar(30) | N               |
| affected_proposals_scope    | jsonb       | N               |
| approval_reference          | jsonb       | N               |
| ended_action_id             | uuid        | S               |
| T                           | Bloque      |                 |

FK a versión y a actuación de cierre.

Tratamientos: NOT_AFFECTED, CONTINUE_AUTHORIZED, REVIEW_REQUIRED y WITHDRAW_REQUIRED.

Final posterior al inicio. Final y actuación de cierre presentes conjuntamente.

Se impide solapar intervalos de habilitación de una misma versión mediante exclusión sobre (model_id, model_version,
intervalo).

Índice (model_id, effective_from, enablement_id).

El retiro impide nuevas propuestas. El tratamiento de propuestas existentes queda identificado por alcance y decisión
expresa.

## **9. Propuestas**

### **9.1. contract_proposal**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| proposal_id             | uuid        | N, PK           |
| application_service     | varchar(80) | N               |
| application_id          | uuid        | N               |
| application_item_id     | uuid        | N               |
| purpose                 | varchar(30) | N               |
| source_contract_id      | uuid        | S               |
| source_contract_version | bigint      | S               |
| current_version         | bigint      | N               |
| T                       | Bloque      |                 |
| lock_version            | bigint      | N               |
| updated_at              | timestamptz | N               |

Finalidades: INITIAL_CONTRACT, BILATERAL_AMENDMENT y EXPRESS_RENEWAL.

INITIAL_CONTRACT no contiene contrato de origen. Las otras finalidades requieren identidad y versión de origen, con FK
local a contract_version.

La FK de versión actual a contract_proposal_version es diferible para permitir la creación atómica de raíz y primera
versión.

Índices:

- (application_service, application_id, application_item_id, proposal_id).

- (source_contract_id, proposal_id), donde exista contrato de origen.

### **9.2. contract_proposal_version**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| proposal_id               | uuid        | N               |
| proposal_version          | bigint      | N               |
| model_id                  | uuid        | N               |
| model_version             | bigint      | N               |
| product_service           | varchar(80) | N               |
| product_id                | uuid        | N               |
| product_version           | bigint      | N               |
| particular_values         | jsonb       | N               |
| economic_terms            | jsonb       | N               |
| condition_definitions     | jsonb       | N               |
| structured_content        | jsonb       | N               |
| structured_content_hash   | char(64)    | N               |
| participant_snapshot_hash | char(64)    | N               |
| manifest_id               | uuid        | S               |
| valid_until               | timestamptz | N               |
| status                    | varchar(30) | N               |
| T                         | Bloque      |                 |
| S                         | Bloque      |                 |
| lock_version              | bigint      | N               |
| updated_at                | timestamptz | N               |

PK (proposal_id, proposal_version).

FK a propuesta y versión del modelo.

Estados:

PREPARING, DOCUMENTS_PENDING, AVAILABLE, PARTIALLY_ACCEPTED, FULLY_ACCEPTED, SUPERSEDED, EXPIRED, WITHDRAWN y USED.

AVAILABLE, PARTIALLY_ACCEPTED, FULLY_ACCEPTED y USED requieren manifiesto íntegro.

El contenido estructurado contiene finalidad, modelo, producto, participantes, valores particulares, economía,
condiciones y contrato de origen cuando corresponde.

Índices:

- (model_id, model_version, proposal_id, proposal_version).

- (valid_until, proposal_id, proposal_version), donde status IN ('AVAILABLE','PARTIALLY_ACCEPTED','FULLY_ACCEPTED').

Una vez disponible, el contenido vinculante es inmutable. Un cambio material genera otra versión.

### **9.3. proposal_participant**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| proposal_id                | uuid        | N               |
| proposal_version           | bigint      | N               |
| party_id                   | uuid        | N               |
| role_code                  | varchar(80) | N               |
| acceptance_required        | boolean     | N               |
| rights_codes               | jsonb       | N               |
| obligation_codes           | jsonb       | N               |
| joint_group_id             | uuid        | S               |
| representation_reference   | jsonb       | S               |
| participant_schema_version | bigint      | N               |

PK (proposal_id, proposal_version, party_id, role_code).

FK a versión de propuesta.

Índice inverso (party_id, proposal_id, proposal_version).

El conjunto no puede estar vacío y debe satisfacer las reglas del modelo.

## **10. Manifiestos**

### **10.1. contract_manifest**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| manifest_id              | uuid        | N, PK           |
| proposal_id              | uuid        | N               |
| proposal_version         | bigint      | N               |
| model_id                 | uuid        | N               |
| model_version            | bigint      | N               |
| canonicalization_version | bigint      | N               |
| structured_content_hash  | char(64)    | N               |
| manifest_hash            | char(64)    | N               |
| integrity_status         | varchar(20) | N               |
| verified_at              | timestamptz | S               |
| T                        | Bloque      |                 |

FK a propuesta y modelo versionados.

UQ:

- (proposal_id, proposal_version).

- (manifest_id, proposal_id, proposal_version).

Estados: BUILDING y VERIFIED. VERIFIED exige fecha de verificación.

La FK compuesta desde la versión de propuesta vincula su manifiesto a esa misma propuesta y versión.

### **10.2. contract_manifest_document**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| manifest_id      | uuid        | N               |
| position         | integer     | N               |
| document_service | varchar(80) | N               |
| document_id      | uuid        | N               |
| document_version | bigint      | N               |
| document_role    | varchar(80) | N               |
| content_hash     | char(64)    | N               |

PK (manifest_id, position) y FK al manifiesto.

Posición y versión positivas.

Índice (document_service, document_id, document_version, manifest_id).

Las posiciones forman una secuencia consecutiva desde uno. Su integridad se comprueba antes de verificar el manifiesto.

El orden documental integra la huella. Los documentos permanecen en Documentos y comprobantes.

No se calcula la huella sobre la representación textual producida por JSONB, sino mediante la canonicalización
registrada para CONTRACT_STRUCTURED_CONTENT y CONTRACT_MANIFEST.

## **11. Entregas**

### **11.1. contract_delivery**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| delivery_id      | uuid        | N, PK           |
| proposal_id      | uuid        | N               |
| proposal_version | bigint      | N               |
| manifest_id      | uuid        | N               |
| delivery_purpose | varchar(30) | N               |
| status           | varchar(30) | N               |
| requested_at     | timestamptz | N               |
| T                | Bloque      |                 |
| C                | Bloque      |                 |

FK compuesta a manifiesto y su propuesta.

UQ (delivery_id, proposal_id, proposal_version).

Finalidades: PRE_ACCEPTANCE, FINAL_COPY, AMENDMENT_NOTICE y OTHER_AUTHORIZED_NOTICE.

Estados: REQUESTED, AVAILABLE, DELIVERED, PARTIALLY_DELIVERED, FAILED y UNCERTAIN.

Índice (proposal_id, proposal_version, requested_at, delivery_id).

### **11.2. contract_delivery_recipient**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| recipient_id            | uuid        | N, PK           |
| delivery_id             | uuid        | N               |
| recipient_reference     | jsonb       | N               |
| recipient_identity_hash | char(64)    | N               |
| channel_codes           | jsonb       | N               |
| status                  | varchar(30) | N               |
| updated_at              | timestamptz | N               |
| lock_version            | bigint      | N               |

FK a entrega.

UQ (delivery_id, recipient_identity_hash) y (delivery_id, recipient_id).

Canales no vacíos. Los estados son los de entrega.

Índice (status, updated_at, recipient_id).

### **11.3. contract_delivery_communication**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| delivery_communication_id | uuid        | N, PK           |
| delivery_id               | uuid        | N               |
| recipient_id              | uuid        | N               |
| channel_code              | varchar(80) | N               |
| communication_service     | varchar(80) | N               |
| remote_action_id          | uuid        | N               |
| communication_id          | uuid        | S               |
| status                    | varchar(30) | N               |
| updated_at                | timestamptz | N               |
| lock_version              | bigint      | N               |

FK compuesta a destinatario de la entrega.

UQ (recipient_id, channel_code) y (communication_service, remote_action_id).

Estados: PENDING, ACCEPTED, UNCERTAIN y FAILED.

Índice (communication_service, communication_id), donde exista comunicación.

El servicio Comunicaciones administra sus propios intentos. Contratos conserva una identidad estable para recuperar la
solicitud de comunicación.

### **11.4. contract_delivery_evidence**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| evidence_id               | uuid        | N, PK           |
| delivery_communication_id | uuid        | N               |
| owner_service             | varchar(80) | N               |
| source_evidence_id        | uuid        | N               |
| source_evidence_version   | bigint      | N               |
| communication_id          | uuid        | N               |
| communication_attempt_id  | uuid        | S               |
| fact_code                 | varchar(80) | N               |
| document_service          | varchar(80) | N               |
| document_id               | uuid        | N               |
| document_version          | bigint      | N               |
| occurred_at               | timestamptz | N               |
| evidence_payload          | jsonb       | N               |
| T                         | Bloque      |                 |
| S                         | Bloque      |                 |

FK a comunicación de entrega.

UQ (owner_service, source_evidence_id, source_evidence_version).

Índice (delivery_communication_id, occurred_at, evidence_id).

Se comprueban comunicación, destinatario y documento exactos. Una aceptación técnica del proveedor no equivale a
recepción contractual.

## **12. Aceptaciones y retiros**

### **12.1. contract_acceptance**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| acceptance_id                     | uuid        | N, PK           |
| proposal_id                       | uuid        | N               |
| proposal_version                  | bigint      | N               |
| manifest_id                       | uuid        | N               |
| manifest_hash                     | char(64)    | N               |
| participant_party_id              | uuid        | N               |
| role_code                         | varchar(80) | N               |
| actor_reference                   | jsonb       | N               |
| representation_reference          | jsonb       | S               |
| authentication_evidence_reference | jsonb       | N               |
| delivery_id                       | uuid        | N               |
| affirmative_consent               | boolean     | N               |
| accepted_at                       | timestamptz | N               |
| received_at                       | timestamptz | N               |
| status                            | varchar(30) | N               |
| verification_evidence             | jsonb       | N               |
| T                                 | Bloque      |                 |
| C                                 | Bloque      |                 |

FK compuestas:

- Participante de la propuesta y versión.

- Manifiesto de esa propuesta y versión.

- Entrega de esa propuesta y versión.

affirmative_consent debe ser verdadero.

Estados: PENDING_VERIFICATION, EFFECTIVE, WITHDRAWN y REJECTED.

UQ parcial (proposal_id, proposal_version, participant_party_id, role_code), donde status = 'EFFECTIVE'.

Índices:

- (proposal_id, proposal_version, status, acceptance_id).

- (participant_party_id, received_at, acceptance_id).

- (delivery_id, acceptance_id).

Antes de pasar a EFFECTIVE se comprueban identidad, autenticación, intervención representativa y acceso previo al
contenido íntegro.

### **12.2. contract_acceptance_withdrawal**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| withdrawal_id                | uuid          | N, PK           |
| acceptance_id                | uuid          | N               |
| received_at                  | timestamptz   | N               |
| reason                       | varchar(4000) | S               |
| outcome                      | varchar(30)   | N               |
| celebration_attempt_id       | uuid          | S               |
| resulting_contract_reference | jsonb         | S               |
| T                            | Bloque        |                 |

FK a aceptación y, cuando exista, al intento local.

Resultados: WITHDRAWN, ALREADY_COMMITTED y PENDING.

ALREADY_COMMITTED exige referencia contractual resultante.

Índice (acceptance_id, received_at, withdrawal_id).

El retiro no elimina la manifestación original ni una celebración confirmada.

## **13. Intentos de celebración**

### **13.1. contract_celebration_attempt**

Extensión uno a uno de execution_attempt.

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| attempt_id                 | uuid        | N, PK           |
| attempt_type               | varchar(80) | N               |
| proposal_id                | uuid        | N               |
| proposal_version           | bigint      | N               |
| manifest_id                | uuid        | N               |
| manifest_hash              | char(64)    | N               |
| participant_snapshot_hash  | char(64)    | N               |
| purpose                    | varchar(30) | N               |
| source_contract_id         | uuid        | S               |
| source_contract_version    | bigint      | S               |
| bank_conformity_type       | varchar(30) | N               |
| bank_approval_reference    | jsonb       | S               |
| automatic_rule_reference   | jsonb       | S               |
| pending_decision           | boolean     | N               |
| resulting_contract_id      | uuid        | S               |
| resulting_contract_version | bigint      | S               |

FK compuesta al intento común mediante (attempt_id, attempt_type); el principal incorpora la UQ necesaria en
execution_attempt.

attempt_type es exclusivamente CONTRACT_CELEBRATION.

FK compuestas al manifiesto, contrato de origen y versión resultante cuando corresponda.

Conformidad del banco:

- INDIVIDUAL_APPROVAL exige únicamente aprobación.

- AUTOMATIC_FORMALIZATION exige únicamente regla habilitante.

pending_decision es verdadero exclusivamente mientras la decisión común es PENDING. Su coherencia se exige mediante
comprobación diferida en la transacción que modifica cualquiera de las dos tablas.

UQ parcial (proposal_id, proposal_version), donde pending_decision = true.

Resultado contractual e identidad de versión están presentes conjuntamente y solo cuando la decisión es COMMITTED.

### **13.2. contract_attempt_acceptance**

| **Columna**   | **Tipo** | **Nulabilidad** |
|---------------|----------|-----------------|
| attempt_id    | uuid     | N               |
| acceptance_id | uuid     | N               |

PK (attempt_id, acceptance_id).

FK a intento y aceptación. Índice inverso (acceptance_id, attempt_id).

La aceptación debe pertenecer a la propuesta y versión del intento. Se comprueba bajo el mismo control de propuesta.

### **13.3. contract_attempt_authority**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| attempt_id                  | uuid        | N               |
| component_code              | varchar(80) | N               |
| owner_service               | varchar(80) | N               |
| required                    | boolean     | N               |
| remote_action_id            | uuid        | N               |
| preparation_id              | uuid        | S               |
| binding_content_hash        | char(64)    | N               |
| scope_payload               | jsonb       | N               |
| state                       | varchar(30) | N               |
| decision_notification_state | varchar(30) | N               |
| last_checked_at             | timestamptz | S               |
| evidence_references         | jsonb       | N               |
| updated_at                  | timestamptz | N               |
| lock_version                | bigint      | N               |

PK (attempt_id, component_code) y FK al intento.

UQ (owner_service, remote_action_id).

Estados de preparación: REQUEST_PENDING, PREPARED, REJECTED, UNCERTAIN y CONCLUDED.

Estados de notificación: NOT_REQUIRED, PENDING, CONFIRMED y UNCERTAIN.

PREPARED y CONCLUDED requieren preparation_id.

Índices:

- (owner_service, preparation_id), donde exista.

- (state, updated_at, attempt_id, component_code).

- (decision_notification_state, updated_at, attempt_id, component_code), donde decision_notification_state IN
  ('PENDING','UNCERTAIN').

Una consulta informativa no se registra como preparación favorable.

### **13.4. contract_proposal_use**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| proposal_id      | uuid        | N, PK           |
| proposal_version | bigint      | N               |
| attempt_id       | uuid        | N               |
| contract_id      | uuid        | N               |
| contract_version | bigint      | N               |
| used_at          | timestamptz | N               |

FK a propuesta versionada, intento y versión contractual.

UQ sobre attempt_id.

La PK sobre la identidad de propuesta impide utilizar otra versión de una propuesta ya consumida. Una modificación
posterior utiliza una nueva propuesta.

### **13.5. Decisión atómica**

La confirmación utiliza este orden:

1.  Raíz de propuesta.

2.  Identidad de ejecución.

3.  Intento común y extensión.

4.  Aceptaciones, ordenadas por identificador.

5.  Contrato existente, cuando corresponda.

6.  Período contractual, si corresponde a renovación.

Comprueba contenido, vigencia, aceptaciones, retiros, conformidad bancaria, condiciones previas, preparaciones y versión
contractual de origen.

En una misma transacción registra:

- COMMITTED.

- Contrato o versión contractual.

- Uso de propuesta.

- Estado USED.

- Resultado de actuación.

- Historia.

- Outbox.

- Trabajos de notificación y aplicación pendientes.

El aborto utiliza el mismo orden. Si la confirmación ocurrió primero, devuelve ALREADY_COMMITTED.

Una identidad desconocida no produce ABORTED. Una preparación vencida o un trabajador expirado tampoco demuestran
aborto.

## **14. Contratos y versiones**

### **14.1. contract**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| contract_id                 | uuid        | N, PK           |
| current_version             | bigint      | N               |
| initial_proposal_id         | uuid        | N               |
| initial_proposal_version    | bigint      | N               |
| initial_attempt_id          | uuid        | N               |
| celebrated_at               | timestamptz | N               |
| lifecycle_status            | varchar(30) | N               |
| effect_status               | varchar(30) | N               |
| fulfillment_status          | varchar(30) | N               |
| residual_obligations_status | varchar(20) | N               |
| T                           | Bloque      |                 |
| lock_version                | bigint      | N               |
| event_version               | bigint      | N               |
| updated_at                  | timestamptz | N               |

FK a propuesta inicial e intento.

UQ sobre initial_proposal_id y sobre initial_attempt_id.

FK diferible (contract_id, current_version) a versión contractual.

| **Dimensión** | **Estados**                                  |
|---------------|----------------------------------------------|
| Ciclo         | CELEBRATED, TERMINATION_PENDING, TERMINATED. |
| Efecto        | SUSPENDED_BY_CONDITION, EFFECTIVE, CEASED.   |
| Cumplimiento  | PENDING, FULFILLED, BREACHED, UNDER_REVIEW.  |
| Residuales    | NONE, PENDING, RESOLVED.                     |

Índice (lifecycle_status, updated_at, contract_id).

La versión contractual identifica contenido. event_version ordena cambios del agregado, incluidos cambios de estado que
no alteran ese contenido.

### **14.2. contract_version**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| contract_id            | uuid        | N               |
| contract_version       | bigint      | N               |
| model_id               | uuid        | N               |
| model_version          | bigint      | N               |
| product_service        | varchar(80) | N               |
| product_id             | uuid        | N               |
| product_version        | bigint      | N               |
| proposal_id            | uuid        | S               |
| proposal_version       | bigint      | S               |
| celebration_attempt_id | uuid        | S               |
| manifest_id            | uuid        | N               |
| particular_values      | jsonb       | N               |
| economic_terms         | jsonb       | N               |
| structured_content     | jsonb       | N               |
| change_type            | varchar(30) | N               |
| change_reference       | jsonb       | N               |
| T                      | Bloque      |                 |
| S                      | Bloque      |                 |

PK (contract_id, contract_version).

FK al contrato, modelo, manifiesto y, cuando existan, propuesta e intento.

Propuesta y versión aparecen conjuntamente.

change_type: INITIAL, AMENDMENT o RENEWAL.

INITIAL y las modificaciones o renovaciones con aceptación expresa exigen propuesta e intento. Los cambios habilitados
sin nueva aceptación conservan su fundamento y manifiesto íntegro.

El contenido es inmutable.

Índices:

- (product_service, product_id, contract_id, contract_version).

- (proposal_id, proposal_version), donde exista propuesta.

- (celebration_attempt_id), donde exista intento.

### **14.3. contract_participant**

Mismas columnas de proposal_participant, sustituyendo propuesta por contract_id y contract_version.

PK (contract_id, contract_version, party_id, role_code).

FK a versión contractual.

Índice inverso (party_id, contract_id, contract_version, role_code).

Los participantes se copian desde el contenido confirmado; no se reconstruyen con datos actuales.

### **14.4. contract_version_effectivity**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| effectivity_id   | uuid        | N, PK           |
| contract_id      | uuid        | N               |
| contract_version | bigint      | N               |
| effective_from   | timestamptz | N               |
| effective_until  | timestamptz | S               |
| cause_reference  | jsonb       | N               |
| ended_action_id  | uuid        | S               |
| T                | Bloque      |                 |

FK a versión y actuación de cierre.

Intervalo con inicio incluido y final excluido. Final posterior al inicio y acompañado de actuación de cierre.

Exclusión de solapamiento sobre (contract_id, intervalo).

Índice (contract_id, effective_from, effectivity_id).

La versión más reciente y la versión efectiva en un instante se distinguen mediante estos intervalos. Una versión futura
no sustituye anticipadamente al contenido efectivo.

## **15. Condiciones contractuales**

### **15.1. contract_condition**

| **Columna**      | **Tipo** | **Nulabilidad** |
|------------------|----------|-----------------|
| condition_id     | uuid     | N, PK           |
| contract_id      | uuid     | N               |
| origin_reference | jsonb    | N               |
| T                | Bloque   |                 |

FK al contrato. UQ (contract_id, condition_id).

### **15.2. contract_condition_definition**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| condition_id             | uuid        | N               |
| contract_id              | uuid        | N               |
| contract_version         | bigint      | N               |
| code                     | varchar(80) | N               |
| required_fact_code       | varchar(80) | N               |
| stage                    | varchar(50) | N               |
| responsible_service      | varchar(80) | N               |
| source_reference         | jsonb       | S               |
| deadline                 | timestamptz | S               |
| failure_consequence_code | varchar(80) | N               |
| waivable                 | boolean     | N               |

PK (condition_id, contract_version).

FK compuestas a condición del contrato y versión contractual.

Las etapas son las del apartado 6.4. La definición es inmutable.

### **15.3. contract_condition_result**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| result_id                 | uuid          | N, PK           |
| condition_id              | uuid          | N               |
| contract_version          | bigint        | N               |
| owner_service             | varchar(80)   | N               |
| fact_type                 | varchar(80)   | N               |
| fact_id                   | uuid          | N               |
| fact_version              | bigint        | N               |
| status                    | varchar(20)   | N               |
| occurred_at               | timestamptz   | N               |
| evidence_references       | jsonb         | N               |
| waiver_approval_reference | jsonb         | S               |
| reason                    | varchar(4000) | S               |
| T                         | Bloque        |                 |
| S                         | Bloque        |                 |

FK a definición.

UQ (condition_id, contract_version, owner_service, fact_type, fact_id, fact_version).

Estados: SATISFIED, FAILED y WAIVED.

WAIVED exige posibilidad de dispensa, aprobación y fundamento. Los demás estados no contienen aprobación de dispensa.

Índice (condition_id, contract_version, occurred_at, result_id).

### **15.4. contract_condition_state**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| condition_id      | uuid        | N               |
| contract_version  | bigint      | N               |
| status            | varchar(20) | N               |
| current_result_id | uuid        | S               |
| updated_at        | timestamptz | N               |
| lock_version      | bigint      | N               |

PK y FK (condition_id, contract_version) a definición.

Se declara UQ (result_id, condition_id, contract_version) en resultados y FK compuesta desde el estado actual.

PENDING exige ausencia de resultado. SATISFIED, FAILED y WAIVED exigen resultado concordante.

Índice (status, condition_id, contract_version), donde status = 'PENDING'.

El resultado y sus consecuencias sobre el contrato se confirman conjuntamente.

## **16. Modificaciones**

### **16.1. contract_amendment**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| amendment_id               | uuid        | N, PK           |
| contract_id                | uuid        | N               |
| source_contract_version    | bigint      | N               |
| amendment_type             | varchar(40) | N               |
| changes_payload            | jsonb       | N               |
| reasons                    | jsonb       | N               |
| affected_participants      | jsonb       | N               |
| proposal_id                | uuid        | S               |
| proposal_version           | bigint      | S               |
| proposed_effective_at      | timestamptz | N               |
| effective_at               | timestamptz | S               |
| resulting_contract_version | bigint      | S               |
| status                     | varchar(30) | N               |
| approval_reference         | jsonb       | S               |
| T                          | Bloque      |                 |
| C                          | Bloque      |                 |
| schema_version             | bigint      | N               |

FK al contrato, versión de origen, propuesta y versión resultante cuando existan.

Modalidades:

BILATERAL, AGREED_FORMULA, PERMITTED_UNILATERAL, UNCONDITIONAL_IMPROVEMENT y MANDATORY_CHANGE.

Estados: REQUESTED, PROPOSAL_PENDING, NOTICE_PENDING, SCHEDULED, APPLIED, REJECTED y REQUIRES_REVIEW.

APPLIED exige versión resultante y fecha efectiva.

Índices:

- (contract_id, created_at, amendment_id).

- (proposed_effective_at, amendment_id), donde status = 'SCHEDULED'.

Los cambios bilaterales utilizan propuesta, aceptación e intento. Los estructurales conservan una definición completa,
no parches libres.

### **16.2. contract_amendment_notice**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| notice_id               | uuid        | N, PK           |
| amendment_id            | uuid        | N               |
| delivery_id             | uuid        | N               |
| recipient_reference     | jsonb       | N               |
| recipient_identity_hash | char(64)    | N               |
| delivery_evidence_id    | uuid        | S               |
| notified_at             | timestamptz | S               |
| earliest_effective_at   | timestamptz | S               |
| calendar_calculation    | jsonb       | S               |
| status                  | varchar(20) | N               |
| T                       | Bloque      |                 |

FK a modificación, entrega y evidencia local.

UQ (amendment_id, recipient_identity_hash).

Estados: PENDING y VERIFIED. VERIFIED exige evidencia, notificación y cálculo.

Índice (amendment_id, status, notice_id).

La modificación desfavorable alcanzada no se aplica antes de cumplir sesenta días corridos desde el día siguiente a la
notificación acreditada a todos los destinatarios exigidos.

## **17. Períodos, renovaciones y no renovación**

### **17.1. contract_period**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| period_id              | uuid        | N, PK           |
| contract_id            | uuid        | N               |
| period_number          | bigint      | N               |
| starts_at              | timestamptz | N               |
| ends_at                | timestamptz | N               |
| basis_reference        | jsonb       | N               |
| non_renewal_registered | boolean     | N               |
| T                      | Bloque      |                 |
| lock_version           | bigint      | N               |

FK al contrato.

UQ (contract_id, period_number) y (contract_id, period_id).

Número positivo y final posterior al inicio.

Exclusión de solapamiento sobre (contract_id, intervalo).

### **17.2. contract_renewal**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| renewal_id                   | uuid        | N, PK           |
| contract_id                  | uuid        | N               |
| source_period_id             | uuid        | N               |
| renewal_type                 | varchar(30) | N               |
| proposed_terms               | jsonb       | N               |
| evaluation_references        | jsonb       | N               |
| enabling_agreement_reference | jsonb       | S               |
| proposal_id                  | uuid        | S               |
| proposal_version             | bigint      | S               |
| resulting_period_id          | uuid        | S               |
| resulting_contract_version   | bigint      | S               |
| effective_at                 | timestamptz | S               |
| status                       | varchar(30) | N               |
| T                            | Bloque      |                 |
| C                            | Bloque      |                 |
| schema_version               | bigint      | N               |

FK compuestas a períodos del contrato, propuesta y versión contractual resultante.

UQ (contract_id, source_period_id).

Modalidades: AUTOMATIC_AGREED, EXPRESS_RENEWAL y EXTENSION.

Estados: REQUESTED, PROPOSAL_PENDING, SCHEDULED, APPLIED, NOT_RENEWED, REJECTED y REQUIRES_REVIEW.

AUTOMATIC_AGREED exige pacto habilitante. EXPRESS_RENEWAL exige propuesta antes de confirmar. APPLIED requiere período
resultante, versión y fecha.

Índice (status, updated_at, renewal_id).

Una renovación rechazada o en revisión se conserva en esta misma coordinación. No se crean coordinaciones paralelas para
el mismo período.

### **17.3. contract_non_renewal_instruction**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| instruction_id  | uuid        | N, PK           |
| contract_id     | uuid        | N               |
| period_id       | uuid        | N               |
| actor_reference | jsonb       | N               |
| received_at     | timestamptz | N               |
| outcome         | varchar(30) | N               |
| renewal_id      | uuid        | S               |
| T               | Bloque      |                 |

FK al período del contrato y renovación, cuando exista.

Resultados: REGISTERED, ALREADY_RENEWED y PENDING.

Índice (contract_id, period_id, received_at, instruction_id).

Renovación y no renovación bloquean contrato y período. Una renovación expresa adquiere antes propuesta e intento
conforme al orden de celebración.

Ningún proceso bloquea una propuesta después de adquirir su contrato.

## **18. Aplicación operativa de cambios**

### **18.1. contract_operational_application**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| application_id      | uuid        | N, PK           |
| contract_id         | uuid        | N               |
| amendment_id        | uuid        | S               |
| renewal_id          | uuid        | S               |
| owner_service       | varchar(80) | N               |
| object_type         | varchar(80) | N               |
| object_id           | uuid        | N               |
| effect_code         | varchar(80) | N               |
| remote_action_id    | uuid        | N               |
| requested_content   | jsonb       | N               |
| status              | varchar(30) | N               |
| result_reference    | jsonb       | S               |
| evidence_references | jsonb       | N               |
| T                   | Bloque      |                 |
| C                   | Bloque      |                 |
| S                   | Bloque      |                 |

FK al contrato y a modificación o renovación.

Exactamente una de amendment_id y renewal_id está presente. Se comprueba que pertenece al mismo contrato.

UQ:

- (owner_service, remote_action_id).

- Parcial (amendment_id, owner_service, object_type, object_id, effect_code), donde exista modificación.

- Parcial (renewal_id, owner_service, object_type, object_id, effect_code), donde exista renovación.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (owner_service, status, updated_at, application_id).

La intención se confirma antes de llamar al propietario. Una aplicación pendiente no elimina la decisión contractual ni
se presenta como ejecución operativa integral.

## **19. Arrepentimiento y terminación**

### **19.1. contract_exit_request**

Cabecera común de coordinación de salida.

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| exit_request_id                   | uuid        | N, PK           |
| contract_id                       | uuid        | N               |
| exit_type                         | varchar(30) | N               |
| received_at                       | timestamptz | N               |
| original_received_at              | timestamptz | S               |
| original_receipt_evidence         | jsonb       | S               |
| status                            | varchar(30) | N               |
| outcome                           | varchar(30) | N               |
| effective_at                      | timestamptz | S               |
| prevented_service_codes           | jsonb       | N               |
| pending_components                | jsonb       | N               |
| notification_due_at               | timestamptz | N               |
| notification_delivery_id          | uuid        | S               |
| notification_calendar_calculation | jsonb       | N               |
| T                                 | Bloque      |                 |
| C                                 | Bloque      |                 |

FK al contrato y entrega cuando exista.

Tipos: WITHDRAWAL_RIGHT y TERMINATION.

Estados: RECEIVED, EVIDENCE_PENDING, COORDINATING, PARTIALLY_APPLIED, COMPLETED, REJECTED y REQUIRES_REVIEW.

Resultados: PENDING, PARTIALLY_APPLIED, APPLIED y REJECTED.

Recepción original y evidencia aparecen conjuntamente.

Índices:

- (contract_id, received_at, exit_request_id).

- (notification_due_at, exit_request_id), donde notification_delivery_id IS NULL AND status \<\> 'REJECTED'.

- (status, updated_at, exit_request_id).

### **19.2. contract_withdrawal_request**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| exit_request_id             | uuid        | N, PK           |
| applicant_party_id          | uuid        | N               |
| actor_reference             | jsonb       | N               |
| representation_reference    | jsonb       | S               |
| channel_code                | varchar(80) | N               |
| eligibility                 | varchar(30) | N               |
| current_calculation_version | bigint      | S               |

FK a cabecera, cuyo tipo debe ser WITHDRAWAL_RIGHT.

Elegibilidad: ELIGIBLE, OUT_OF_PERIOD, NOT_APPLICABLE y PENDING_EVIDENCE.

FK diferible (exit_request_id, current_calculation_version) al cálculo vigente cuando exista.

### **19.3. contract_withdrawal_deadline**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| exit_request_id                         | uuid        | N               |
| calculation_version                     | bigint      | N               |
| contract_received_at                    | timestamptz | S               |
| contract_receipt_evidence_reference     | jsonb       | S               |
| product_available_at                    | timestamptz | S               |
| product_availability_evidence_reference | jsonb       | S               |
| calendar_service                        | varchar(80) | N               |
| calendar_id                             | uuid        | N               |
| calendar_version                        | bigint      | N               |
| base_date                               | date        | S               |
| deadline_date                           | date        | S               |
| missing_evidence_codes                  | jsonb       | N               |
| calendar_calculation                    | jsonb       | N               |
| calculated_at                           | timestamptz | N               |
| T                                       | Bloque      |                 |

PK (exit_request_id, calculation_version) y FK a arrepentimiento.

Cada hecho y su evidencia aparecen conjuntamente.

Solo se determinan fecha base y límite cuando están acreditados ambos hechos necesarios. La fecha base corresponde al
hecho más tardío.

El plazo es de diez días hábiles desde el día siguiente a:

- La recepción acreditada del contrato.

- La disponibilidad efectiva del producto.

Se utiliza el hecho posterior. La disponibilidad de una copia no sustituye disponibilidad operativa.

Los cálculos son inmutables. La falta de evidencia produce PENDING_EVIDENCE, no vencimiento presunto.

### **19.4. contract_termination_request**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| exit_request_id        | uuid        | N, PK           |
| cause_code             | varchar(80) | N               |
| requested_effective_at | timestamptz | S               |
| grounds                | jsonb       | N               |
| actor_reference        | jsonb       | N               |

FK a cabecera, cuyo tipo debe ser TERMINATION.

La recepción no exige deuda cero. La terminación no certifica cancelación económica ni borra obligaciones residuales.

### **19.5. contract_exit_component**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| component_id                 | uuid        | N, PK           |
| exit_request_id              | uuid        | N               |
| owner_service                | varchar(80) | N               |
| object_type                  | varchar(80) | N               |
| object_id                    | uuid        | N               |
| required_action_code         | varchar(80) | N               |
| remote_action_id             | uuid        | N               |
| requested_content            | jsonb       | N               |
| due_at                       | timestamptz | S               |
| instructions_communicated_at | timestamptz | S               |
| restitution_due_at           | timestamptz | S               |
| calendar_calculation         | jsonb       | S               |
| status                       | varchar(30) | N               |
| result_reference             | jsonb       | S               |
| evidence_references          | jsonb       | N               |
| T                            | Bloque      |                 |
| C                            | Bloque      |                 |
| S                            | Bloque      |                 |

FK a salida.

UQ:

- (exit_request_id, owner_service, object_type, object_id, required_action_code).

- (owner_service, remote_action_id).

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (owner_service, status, updated_at, component_id).

Las instrucciones se comunican dentro de dos días hábiles. En préstamos desembolsados, la restitución conserva diez días
hábiles desde esa comunicación acreditada.

Contratos no ejecuta directamente devoluciones monetarias.

### **19.6. contract_residual_obligation**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| residual_obligation_id      | uuid        | N, PK           |
| contract_id                 | uuid        | N               |
| owner_service               | varchar(80) | N               |
| source_object_type          | varchar(80) | N               |
| source_object_id            | uuid        | N               |
| nature_code                 | varchar(80) | N               |
| current_observation_version | bigint      | N               |
| status                      | varchar(20) | N               |
| T                           | Bloque      |                 |
| lock_version                | bigint      | N               |
| updated_at                  | timestamptz | N               |

FK al contrato.

UQ (contract_id, owner_service, source_object_type, source_object_id, nature_code).

Estados: PENDING, RESOLVED y UNKNOWN.

**contract_residual_observation**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| residual_obligation_id | uuid        | N               |
| observation_version    | bigint      | N               |
| source_version         | bigint      | N               |
| cut_at                 | timestamptz | N               |
| status                 | varchar(20) | N               |
| evidence_references    | jsonb       | N               |
| T                      | Bloque      |                 |

PK (residual_obligation_id, observation_version) y FK a residual.

La raíz utiliza FK diferible a su observación actual.

Índice de raíz (contract_id, status, residual_obligation_id).

Cada observación es inmutable. UNKNOWN no equivale a obligación resuelta.

**contract_exit_residual**

Columnas obligatorias exit_request_id uuid y residual_obligation_id uuid.

PK sobre ambas columnas y FK a sus tablas. Se comprueba que salida y obligación pertenecen al mismo contrato.

## **20. Constancias**

### **20.1. contract_certificate_request**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| certificate_request_id     | uuid        | N, PK           |
| contract_id                | uuid        | N               |
| requested_contract_version | bigint      | S               |
| reference_time             | timestamptz | S               |
| certificate_type_code      | varchar(80) | N               |
| recipient_reference        | jsonb       | N               |
| accredited_facts           | jsonb       | N               |
| source_references          | jsonb       | N               |
| remote_document_action_id  | uuid        | N               |
| document_reference         | jsonb       | S               |
| delivery_id                | uuid        | S               |
| status                     | varchar(30) | N               |
| T                          | Bloque      |                 |
| C                          | Bloque      |                 |
| schema_version             | bigint      | N               |

FK al contrato, versión solicitada y entrega, cuando existan.

Versión y momento de referencia son excluyentes. Si ninguno se solicita, se fija y conserva el corte utilizado al
recibir la petición.

Estados: RECEIVED, EVIDENCE_PENDING, DOCUMENT_PENDING, AVAILABLE, DELIVERY_PENDING, COMPLETED y REQUIRES_REVIEW.

AVAILABLE y los estados posteriores requieren documento.

UQ sobre remote_document_action_id.

Índices:

- (contract_id, created_at, certificate_request_id).

- (status, updated_at, certificate_request_id).

La constancia conserva los hechos acreditados y su corte. Una cancelación económica exige confirmación del acreedor.

Recuperar una emisión existente no certifica hechos nuevos.

## **21. Historia y conservación de contenido**

Ambos servicios utilizan business_change_history.

Se conserva historia de:

- Cambios de solicitud y componentes.

- Requerimientos y suficiencia.

- Viabilidad.

- Constitución operativa.

- Modelos y habilitación.

- Propuestas y sustituciones.

- Entregas y aceptaciones.

- Intentos y decisiones.

- Efectos y condiciones.

- Modificaciones y renovaciones.

- Salidas y obligaciones residuales.

- Constancias.

Las tablas inmutables conservan su contenido original. Las tablas de estado actual se actualizan junto con su historia.

No se reconstruye un contrato anterior con datos personales, modelos, tasas o condiciones comerciales actuales.

Los binarios permanecen en Documentos y comprobantes. Contratación conserva identidades, versiones y hashes.

## **22. Orden de bloqueo y transacciones**

### **22.1. Solicitudes de productos**

Orden:

1.  Solicitud.

2.  Componentes, ordenados por identificador.

3.  Requerimientos o constituciones.

4.  Puntos, capacidades y resultados afectados.

5.  Registro de historia, actuación, inbox, outbox y trabajos.

Una operación sobre un resultado no adquiere después otra solicitud.

Las dependencias se modifican bajo la raíz de solicitud. La misma raíz ordena recepción de antecedentes, vencimiento,
retiro y actualización del resultado global.

### **22.2. Modelos**

Orden:

1.  Modelo.

2.  Versión.

3.  Revisión.

4.  Habilitación y tratamiento de propuestas.

Las comprobaciones externas se realizan antes de la transacción de aplicación. Al aplicar se vuelven a verificar
revisión, huella y versión de control.

### **22.3. Propuestas y celebración**

Orden:

1.  Propuesta.

2.  Identidad de ejecución, cuando intervenga.

3.  Intento.

4.  Aceptaciones ordenadas.

5.  Contrato existente.

6.  Período, cuando corresponda.

Verificación de aceptaciones, retiro, sustitución de propuesta, confirmación y aborto respetan este orden.

Una retirada de modelo registra primero su decisión y trabajo de tratamiento. El trabajo procesa propuestas por
separado; no conserva el bloqueo del modelo mientras adquiere propuestas.

### **22.4. Ciclo contractual sin propuesta**

Orden:

1.  Contrato.

2.  Período, modificación o salida correspondiente.

3.  Condiciones o componentes afectados.

Si una operación necesita propuesta e intento, debe entrar por el orden de celebración. No puede adquirirlos después del
contrato.

### **22.5. Llamadas remotas**

No se mantienen transacciones abiertas durante llamadas HTTP, publicación o acceso documental.

Cada comando remoto conserva previamente:

- Identidad estable.

- Propietario.

- Contenido vinculante.

- Hash.

- Actuación remota.

- Seguimiento persistente.

Una respuesta incierta se recupera mediante esas mismas identidades.

## **23. Unicidad y controles de integridad**

| **Garantía**                               | **Mecanismo**                                             |
|--------------------------------------------|-----------------------------------------------------------|
| Participante del componente correcto       | FK compuestas de solicitud.                               |
| Punto y presentación de la misma solicitud | FK compuestas.                                            |
| Dependencias sin ciclos                    | Comprobación bajo bloqueo de solicitud.                   |
| Constitución equivalente única             | UQ por solicitud, componente y contrato.                  |
| Resultado operativo no duplicado           | UQ por propietario, identidad y versión del resultado.    |
| Aprobación del modelo exacto               | FK compuesta, revisión de definición y hash.              |
| Manifiesto de la propuesta correcta        | FK compuesta.                                             |
| Aceptación del participante correcto       | FK compuesta a participante versionado.                   |
| Aceptación efectiva única                  | UQ parcial por propuesta, versión, persona y rol.         |
| Un intento pendiente por versión           | UQ parcial y coherencia diferida con decisión común.      |
| Una utilización por propuesta              | PK de contract_proposal_use.                              |
| Contrato inicial único                     | UQ de propuesta e intento iniciales.                      |
| Versiones efectivas sin solapamiento       | Exclusión temporal por contrato.                          |
| Cumplimiento no duplicado                  | UQ por condición, definición y hecho del propietario.     |
| Renovación única por período               | UQ por contrato y período de origen.                      |
| Aplicación operativa única                 | UQ por cambio, propietario, objeto y efecto.              |
| Restitución recuperable                    | Identidad y actuación remota persistidas antes del envío. |

Las comprobaciones entre tablas que no pueden expresarse mediante CHECK se ejecutan bajo el control local indicado. La
sincronización entre el intento común y su extensión se refuerza mediante restricción diferida.

## **24. Eventos**

Los eventos se registran en outbox junto con el efecto que representan.

| **Contrato** | **Tipo**                          | **Tema**                           | **Agregado**         |
|--------------|-----------------------------------|------------------------------------|----------------------|
| CI-03-025    | ProductApplicationOutcomeRecorded | banking.product-application.events | PRODUCT_APPLICATION  |
| CI-03-026    | ContractProposalAvailable         | banking.contract.events            | CONTRACT_PROPOSAL    |
| CI-03-027    | ContractCelebrated                | banking.contract.events            | CONTRACT             |
| CI-03-028    | ContractEffectsChanged            | banking.contract.events            | CONTRACT             |
| CI-03-029    | ProductConstitutionVerified       | banking.product-application.events | PRODUCT_CONSTITUTION |
| CI-03-030    | ContractAmendmentEffective        | banking.contract.events            | CONTRACT             |
| CI-03-031    | ContractRenewalEffective          | banking.contract.events            | CONTRACT             |
| CI-03-032    | ContractExitOutcomeRecorded       | banking.contract.events            | CONTRACT             |
| CI-03-033    | ContractModelChanged              | banking.contract.events            | CONTRACT_MODEL       |

La versión inicial del esquema es 1. La clave de partición es la identidad del agregado.

La versión de evento se asigna mediante el contador transaccional del agregado correspondiente. No utiliza secuencias
PostgreSQL como prueba de continuidad de negocio.

Reglas:

- ContractProposalAvailable exige manifiesto verificado.

- ContractCelebrated exige decisión COMMITTED.

- ProductConstitutionVerified exige confirmación del propietario operativo.

- Una salida parcial conserva sus pendientes.

- La repetición del evento no repite constitución, modificación, renovación ni restitución.

## **25. Trabajos persistentes**

| **Tipo de trabajo**           | **Identidad de deduplicación**       |
|-------------------------------|--------------------------------------|
| Recordatorio de requerimiento | Requerimiento y versión del cálculo. |
| Vencimiento de requerimiento  | Requerimiento y versión del cálculo. |
| Preparación documental        | Propuesta y versión.                 |
| Solicitud de comunicación     | Destinatario y canal de entrega.     |
| Seguimiento de comunicación   | Comunicación local de entrega.       |
| Resolución de celebración     | Intento.                             |
| Notificación a autoridad      | Intento y componente.                |
| Constitución operativa        | Constitución.                        |
| Seguimiento operativo         | Constitución y actuación remota.     |
| Modificación programada       | Modificación.                        |
| Renovación                    | Contrato y período.                  |
| Componente de salida          | Componente de salida.                |
| Constancia documental         | Solicitud de constancia.             |

Las claves físicas se generan como códigos estables dentro del límite de 120 caracteres, utilizando identidades UUID y,
cuando sea necesario, un hash de la composición canónica.

Las revisiones de fecha generan una nueva ocurrencia de trabajo y cancelan la anterior pendiente cuando corresponda.

La recuperación de un trabajo conserva el mismo comando remoto. La expiración de su concesión no demuestra que el
propietario no ejecutó.

## **26. Migraciones, seguridad y restauración**

Las migraciones se administran con Flyway por servicio.

Las restricciones de exclusión utilizan intervalos semiabiertos y el soporte PostgreSQL necesario para combinar
identidad e intervalo.

Las FK circulares de raíz y primera versión se crean como diferibles. La transacción no puede confirmar una raíz sin su
versión exigida.

Se separan permisos para:

- Administrar y aprobar modelos.

- Registrar solicitudes y antecedentes.

- Evaluar viabilidad.

- Preparar propuestas.

- Entregar contenido.

- Aceptar.

- Celebrar.

- Acreditar condiciones.

- Modificar, renovar y terminar.

- Consultar historia y emitir constancias.

Las consultas históricas y recuperaciones idempotentes vuelven a comprobar autorización y finalidad.

No se eliminan modelos, propuestas, manifiestos, aceptaciones o decisiones utilizados por contratos.

Redis no conserva la única copia de ningún consentimiento, decisión o seguimiento.

Después de restaurar un respaldo:

1.  Se mantienen pausadas las instrucciones externas.

2.  Se recuperan actuaciones, intentos, inbox, outbox y trabajos.

3.  Se reconcilian las identidades remotas potencialmente ejecutadas.

4.  Se incorporan resultados confirmados.

5.  Se reanuda el procesamiento sin cambiar identidades para eludir incertidumbre.

## **27. Correspondencia con los contratos técnicos**

| **Contratos**         | **Persistencia**                                                                           |
|-----------------------|--------------------------------------------------------------------------------------------|
| CI-03-001 y CI-03-002 | Modelos, versiones, campos, revisiones y habilitación.                                     |
| CI-03-003 a CI-03-007 | Solicitudes, participantes, componentes, requisitos, presentaciones, viabilidad y retiros. |
| CI-03-008 y CI-03-009 | Propuestas, versiones, participantes y manifiestos.                                        |
| CI-03-010             | Entregas, destinatarios, comunicaciones y evidencias.                                      |
| CI-03-011 y CI-03-012 | Aceptaciones, verificación y retiros.                                                      |
| CI-03-013             | Intento común, extensión, autoridades, utilización y contrato.                             |
| CI-03-014             | Definiciones, resultados y estado de condiciones contractuales.                            |
| CI-03-015 y CI-03-016 | Constitución, capacidades y resultados del propietario.                                    |
| CI-03-017             | Modificaciones, avisos y aplicaciones operativas.                                          |
| CI-03-018 y CI-03-019 | Períodos, renovaciones y no renovación.                                                    |
| CI-03-020             | Arrepentimiento, cálculo de plazo y componentes de salida.                                 |
| CI-03-021             | Terminación y obligaciones residuales.                                                     |
| CI-03-022             | Versiones, participantes, efectos e historia contractual.                                  |
| CI-03-023             | Constancias y hechos acreditados.                                                          |
| CI-03-024             | Actuación, objetos relacionados, decisiones y pendientes.                                  |
| CI-03-025 a CI-03-033 | Outbox y versiones de agregados.                                                           |

## **28. Escenarios de verificación de implementación**

| **Escenario**                                           | **Resultado exigido**                         |
|---------------------------------------------------------|-----------------------------------------------|
| Participante perteneciente a otra solicitud             | Rechazo por FK compuesta.                     |
| Dependencia circular                                    | No confirma el grafo.                         |
| Presentación oportuna aún en revisión                   | No vence por demora interna.                  |
| Constitución repetida con otra clave idempotente        | Recupera la coordinación existente.           |
| Resultado operativo por HTTP y evento                   | Se incorpora una sola vez.                    |
| Aprobación de otra revisión del modelo                  | No habilita la definición modificada.         |
| Alteración del orden documental                         | Cambia la huella del manifiesto.              |
| Aceptaciones sobre versiones diferentes                 | No satisfacen el mismo contenido.             |
| Aceptación pendiente de verificación                    | No habilita celebración.                      |
| Retiro y celebración concurrentes                       | Una precedencia local determina el resultado. |
| Confirmación y aborto concurrentes                      | Una sola decisión durable.                    |
| Intento desconocido                                     | No confirma aborto.                           |
| Respuesta de celebración perdida                        | Recupera el mismo contrato o versión.         |
| Nueva versión de propuesta ya utilizada                 | No origina otra celebración.                  |
| Autoridad sin notificación final                        | Conserva celebración y seguimiento pendiente. |
| Modificación sobre versión sustituida                   | Exige reevaluación de la base.                |
| Preaviso incompleto                                     | Impide aplicación anticipada.                 |
| Renovación y no renovación concurrentes                 | Compiten sobre contrato y período.            |
| Falta evidencia de disponibilidad del producto          | Conserva PENDING_EVIDENCE.                    |
| Terminación con deuda                                   | Recibe la petición y conserva residuales.     |
| Constancia sin confirmación del acreedor                | No acredita cancelación económica integral.   |
| Restauración con ejecución remota posterior al respaldo | Reconcilia antes de repetir instrucciones.    |

Las restricciones, bloqueos, exclusiones temporales y decisiones atómicas se verifican durante la implementación con
PostgreSQL real y escenarios concurrentes.
