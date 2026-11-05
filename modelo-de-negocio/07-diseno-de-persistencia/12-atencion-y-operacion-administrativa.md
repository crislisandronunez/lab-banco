# **Diseño de persistencia — Módulo 12. Atención y operación administrativa**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Atención y operación administrativa.

| **Servicio**              | **Responsabilidad**                                                                              |
|---------------------------|--------------------------------------------------------------------------------------------------|
| Reclamos                  | Recepción, investigación, resolución, coordinación de reparaciones y seguimiento.                |
| Gestión administrativa    | Consultas, procedimientos, expedientes, tareas, decisiones y actuaciones externas simuladas.     |
| Documentos y comprobantes | Ingreso, conservación, emisión, integridad, preservación, disposición y recuperación documental. |
| Comunicaciones            | Plantillas, preferencias, consentimiento, envíos, constancias y recuperación.                    |
| Extractos                 | Composición, validación, emisión y rectificación de información de productos.                    |

Comprende los contratos CI-12-001 a CI-12-041.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases.

Los propietarios del negocio conservan autoridad sobre contratos, productos, dinero, deuda, tenencias y registros
contables.

Una aprobación no ejecuta una operación económica. Un documento no sustituye la confirmación de su fuente. La aceptación
técnica de un envío no acredita entrega al destinatario.

## **2. Convenciones físicas**

### **2.1. Notación**

- **N:** columna obligatoria.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Cuando una fila agrupa columnas, cada una tiene individualmente el tipo y nulabilidad indicados.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con las columnas en orden. PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                     | **PostgreSQL** | **Restricción**                     |
|------------------------------|----------------|-------------------------------------|
| Identificador                | uuid           | Estable y no reutilizable.          |
| Versión                      | bigint         | Positiva.                           |
| Control de concurrencia      | bigint         | No negativo.                        |
| Código                       | varchar(80)    | No vacío y registrado.              |
| Referencia visible o externa | varchar(120)   | No vacía.                           |
| Título                       | varchar(200)   | No vacío.                           |
| Fundamento o descripción     | varchar(4000)  | No vacío cuando está presente.      |
| Contacto declarado           | varchar(254)   | Validado según canal.               |
| Tipo de contenido            | varchar(120)   | Admitido por el tipo documental.    |
| Moneda                       | varchar(3)     | ARS o USD.                          |
| Importe                      | numeric(24,2)  | No negativo.                        |
| Saldo informativo            | numeric(24,2)  | Admite signo.                       |
| Cantidad informativa         | numeric(30,12) | Conserva precisión de su fuente.    |
| Fecha                        | date           | Fecha bancaria.                     |
| Instante                     | timestamptz    | Intercambio en UTC.                 |
| Tamaño en bytes              | bigint         | Positivo para contenido confirmado. |
| Hash                         | char(64)       | SHA-256 hexadecimal en minúsculas.  |
| Contenido estructurado       | jsonb          | Esquema registrado y versionado.    |

La precisión y escala se validan antes de persistir.

Los períodos y plazos utilizan America/Argentina/Buenos_Aires y calendarios versionados.

### **2.3. Bloques físicos**

**Bloque T — Creación**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| created_action_id | uuid        | N               |
| created_at        | timestamptz | N               |

La actuación referencia service_action.

**Bloque C — Control**

| **Columna**  | **Tipo**    | **Nulabilidad** |
|--------------|-------------|-----------------|
| version      | bigint      | N               |
| lock_version | bigint      | N               |
| updated_at   | timestamptz | N               |

Versión positiva y control no negativo.

**Bloque S — Contenido vinculante**

| **Columna**              | **Tipo** | **Nulabilidad** |
|--------------------------|----------|-----------------|
| schema_version           | bigint   | N               |
| canonicalization_version | bigint   | N               |
| content_hash             | char(64) | N               |

Versiones positivas.

### **2.4. Tablas comunes**

Los cinco servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Las llamadas remotas y transferencias de archivos se realizan fuera de las transacciones PostgreSQL.

## **3. Esquemas estructurados**

Cada JSONB conserva su versión mediante columna explícita o envoltorio con schemaCode, schemaVersion y data.

| **Esquema**               | **Contenido**                                                       |
|---------------------------|---------------------------------------------------------------------|
| RemoteReference           | Propietario, tipo, identidad y versión cuando corresponde.          |
| EvidenceReferences        | Fuente, documentos, hecho y estado de comprobación.                 |
| PendingComponents         | Componente, responsable, motivo y seguimiento.                      |
| PresenterSnapshot         | Identificación, representación declarada, contactos y verificación. |
| ComplaintResolution       | Conclusión y tratamiento por parte del reclamo.                     |
| RepairScope               | Conceptos, importes o alcance administrativo.                       |
| AdministrativeProcedure   | Entradas, pasos, funciones, plazos y transiciones.                  |
| ApprovalScope             | Propuesta, contenido, actuación prevista, vigencia y condiciones.   |
| DocumentMetadata          | Origen, finalidad, clasificación, categoría y procedencia.          |
| DocumentTypeDefinition    | Datos tipados y formatos admitidos.                                 |
| DocumentPreservationScope | Documentos, sujetos, casos y condiciones de cobertura.              |
| CommunicationTemplate     | Contenido, campos tipados y restricciones por canal.                |
| CommunicationRequest      | Destinatarios, contenido y requisitos de entrega.                   |
| StatementComposition      | Fuentes, secciones, controles y limitaciones.                       |
| CalendarCalculation       | Calendario, entradas, regla y resultado.                            |

No se admiten scripts, SQL, campos desconocidos ni mapas libres.

## **4. Presentantes y coordinación remota**

### **4.1. Presentantes**

Reclamos y Gestión administrativa mantienen, cada uno en su base, presenter_snapshot.

| **Columna**                    | **Tipo**     | **Nulabilidad** |
|--------------------------------|--------------|-----------------|
| presenter_id                   | uuid         | N, PK           |
| identification_mode            | varchar(20)  | N               |
| display_name                   | varchar(200) | S               |
| party_id, represented_party_id | uuid         | S               |
| response_contact_reference     | jsonb        | S               |
| declared_contact_channel       | varchar(80)  | S               |
| declared_contact_value         | varchar(254) | S               |
| verification_status            | varchar(20)  | N               |
| evidence_references            | jsonb        | N               |
| T, S                           | Bloques      |                 |

DECLARED no exige persona identificada. IDENTIFIED exige party_id.

Contacto registrado y declarado son alternativas. Canal y valor declarados aparecen conjuntamente.

Un contacto declarado no se considera verificado.

La recepción no concede acceso a expedientes privados ni acredita representación.

### **4.2. Pasos remotos**

Las tablas siguientes comparten estructura:

| **Tabla**                     | **Propietario N**        | **FK**                         |
|-------------------------------|--------------------------|--------------------------------|
| complaint_repair_execution    | repair_id uuid           | complaint_repair               |
| administrative_task_execution | task_id uuid             | administrative_task            |
| administrative_external_step  | external_action_id uuid  | administrative_external_action |
| document_issuance_step        | issuance_id uuid         | document_issuance              |
| statement_issuance_step       | issuance_process_id uuid | statement_issuance_process     |

Columnas adicionales:

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| step_id                             | uuid        | N, PK           |
| component_code, responsible_service | varchar(80) | N               |
| remote_action_id                    | uuid        | N               |
| economic_effect_reference           | jsonb       | S               |
| requested_content                   | jsonb       | N               |
| status                              | varchar(30) | N               |
| result_reference, result_payload    | jsonb       | S               |
| evidence_references                 | jsonb       | N               |
| last_checked_at, next_check_at      | timestamptz | S               |
| T, C, S                             | Bloques     |                 |

UQ por propietario y componente, y por servicio y actuación remota.

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de llamar al propietario.

## **5. Reclamos: recepción y estados**

### **5.1. complaint**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| complaint_id                                        | uuid          | N, PK           |
| complaint_reference                                 | varchar(120)  | N               |
| presenter_id                                        | uuid          | N               |
| identity_verification_status                        | varchar(20)   | N               |
| representation_verification_status                  | varchar(20)   | N               |
| received_at                                         | timestamptz   | N               |
| original_received_at                                | timestamptz   | S               |
| original_receipt_evidence                           | jsonb         | S               |
| channel_code                                        | varchar(80)   | N               |
| description                                         | varchar(4000) | N               |
| reason_code, responsible_service                    | varchar(80)   | S               |
| claimed_amount                                      | numeric(24,2) | S               |
| currency                                            | varchar(3)    | S               |
| urgent                                              | boolean       | N               |
| urgency_reason                                      | varchar(4000) | S               |
| specialized_procedure_reference                     | jsonb         | S               |
| attention_status, resolution_status                 | varchar(30)   | N               |
| repair_status, communication_status, closure_status | varchar(20)   | N               |
| T, C                                                | Bloques       |                 |

FK a presentante. UQ sobre referencia visible.

Importe y moneda aparecen conjuntamente. Urgencia exige motivo.

Recepción original y evidencia aparecen conjuntamente. No puede ser posterior a la recepción efectiva.

| **Dimensión** | **Estados**                                                    |
|---------------|----------------------------------------------------------------|
| Atención      | RECEIVED, CLASSIFIED, UNDER_INVESTIGATION, RESOLVED, REOPENED. |
| Resolución    | PENDING, FAVORABLE, PARTIALLY_FAVORABLE, UNFAVORABLE.          |
| Reparación    | NOT_REQUIRED, PENDING, PARTIAL, CONFIRMED.                     |
| Comunicación  | PENDING, DELIVERED, FAILED, UNCERTAIN.                         |
| Finalización  | OPEN, CLOSED.                                                  |

Identidad UNVERIFIED, PENDING, VERIFIED o FAILED.

Representación NOT_REQUIRED, PENDING, VERIFIED o FAILED.

### **5.2. complaint_affected_object**

| **Columna**                      | **Tipo**      | **Nulabilidad** |
|----------------------------------|---------------|-----------------|
| affected_object_id, complaint_id | uuid          | N               |
| object_reference                 | jsonb         | S               |
| declared_reference               | varchar(120)  | S               |
| description                      | varchar(4000) | S               |

PK affected_object_id, FK a reclamo.

Se exige referencia o descripción suficiente para conservar lo informado. No se exige identificador técnico conocido.

### **5.3. complaint_evidence**

Columnas N: evidence_id uuid PK, complaint_id uuid FK, source_reference jsonb, document_references jsonb,
verification_status varchar(20) y bloques T y S.

## **6. Plazos e investigación de reclamos**

### **6.1. complaint_deadline**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| deadline_id, complaint_id             | uuid        | N               |
| purpose_code                          | varchar(80) | N               |
| calculation_version                   | bigint      | N               |
| effective_receipt_at, due_at          | timestamptz | N               |
| reminder_at                           | timestamptz | S               |
| calendar_calculation, basis_reference | jsonb       | N               |
| superseded_deadline_id                | uuid        | S               |
| T                                     | Bloque      |                 |

PK deadline_id, FK a reclamo y cálculo sustituido.

UQ (complaint_id, purpose_code, calculation_version).

Plazos ordinarios:

- Constancia en el acto.

- Clasificación y asignación hasta finalizar el siguiente hábil.

- Respuesta definitiva en diez días hábiles.

- Aviso dos días hábiles antes.

- Escalación inmediata al vencer.

El cómputo comienza el día siguiente a la recepción.

Reclasificar, reasignar o agregar antecedentes no reinicia el plazo del mismo problema.

### **6.2. complaint_investigation_action**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| investigation_action_id, complaint_id   | uuid        | N               |
| action_type                             | varchar(30) | N               |
| responsible_reference, source_reference | jsonb       | N               |
| action_content, evidence_references     | jsonb       | N               |
| related_action_id                       | uuid        | S               |
| T, S                                    | Bloques     |                 |

PK investigation_action_id, FK a reclamo y actuación relacionada.

Acciones CLASSIFY, REQUEST_INFORMATION, ADD_INFORMATION, RECORD_FINDING, RECORD_CONTRADICTION y RESOLVE_CONTRADICTION.

### **6.3. complaint_information_request**

Columnas N: request_id uuid PK, complaint_id uuid FK, investigation_action_id uuid FK, requested_information jsonb,
responsible_reference jsonb, due_at timestamptz y status varchar(20).

Columna S: response_action_id uuid, FK a investigación.

Estados PENDING, ANSWERED y OVERDUE.

No se vuelve a exigir documentación que el banco conserva y puede recuperar.

## **7. Resoluciones y reparaciones**

### **7.1. complaint_resolution**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| resolution_id, complaint_id                 | uuid        | N               |
| resolution_version                          | bigint      | N               |
| conclusion                                  | varchar(30) | N               |
| grounds, favorable_parts, unfavorable_parts | jsonb       | N               |
| evidence_references                         | jsonb       | N               |
| preparer_person_id                          | uuid        | N               |
| approver_person_id                          | uuid        | S               |
| approval_reference                          | jsonb       | S               |
| approved_content_hash                       | char(64)    | S               |
| T, S                                        | Bloques     |                 |

PK resolution_id.

FK a reclamo. UQ (complaint_id, resolution_version).

Las partes desfavorables y las reparaciones discrecionales exigen aprobación y personas distintas.

### **7.2. complaint_repair**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| repair_id, complaint_id, resolution_id        | uuid        | N               |
| repair_type                                   | varchar(30) | N               |
| executor_service                              | varchar(80) | N               |
| original_operation_reference                  | jsonb       | S               |
| expected_outcome_code                         | varchar(80) | N               |
| approval_reference, existing_effect_reference | jsonb       | S               |
| status                                        | varchar(30) | N               |
| remaining_scope                               | jsonb       | N               |
| T, C, S                                       | Bloques     |                 |

PK repair_id.

FK a reclamo y resolución propia.

Tipos ERROR_RESTITUTION, COMMERCIAL_COMPENSATION y ADMINISTRATIVE_CORRECTION.

Estados PENDING, PARTIALLY_CONFIRMED, CONFIRMED, REJECTED y UNCERTAIN.

### **7.3. complaint_repair_concept**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| repair_id                 | uuid          | N               |
| concept_code              | varchar(80)   | N               |
| scope_type                | varchar(20)   | N               |
| amount                    | numeric(24,2) | S               |
| currency                  | varchar(3)    | S               |
| administrative_scope_code | varchar(80)   | S               |

PK sobre reparación y concepto. FK a reparación.

MONETARY exige importe y moneda. ADMINISTRATIVE exige alcance administrativo.

### **7.4. complaint_repair_result**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| result_id, repair_id                 | uuid        | N               |
| source_service                       | varchar(80) | N               |
| source_result_id                     | uuid        | N               |
| source_result_version                | bigint      | N               |
| executor_action_reference            | jsonb       | N               |
| result                               | varchar(30) | N               |
| attended_concepts, effect_references | jsonb       | N               |
| evidence_references, remaining_scope | jsonb       | N               |
| effective_at                         | timestamptz | S               |
| T, S                                 | Bloques     |                 |

PK result_id, FK a reparación.

UQ (repair_id, source_service, source_result_id, source_result_version).

Reclamos no ejecuta reintegros. Si otro procedimiento ya reparó el efecto, vincula esa ejecución sin ordenar otra
devolución.

## **8. Comunicación, cierre y análisis de reclamos**

### **8.1. complaint_communication**

Columnas N: communication_link_id uuid PK, complaint_id uuid FK, resolution_id uuid FK, recipient_references jsonb,
remote_action_id uuid UQ, status varchar(20) y bloques T y C.

Columnas S: communication_reference jsonb y delivery_evidence jsonb.

Estados PENDING, DELIVERED, FAILED y UNCERTAIN.

### **8.2. complaint_lifecycle_action**

Columnas N: lifecycle_action_id uuid PK, complaint_id uuid FK, action varchar(30), grounds jsonb, evidence_references
jsonb y bloques T y S.

Acciones REQUEST_COMMUNICATION, RECORD_COMMUNICATION, CLOSE y REOPEN.

Cerrar exige resolución, tratamiento de reparaciones y comunicación requerida.

Reabrir conserva recepción, resoluciones e historia.

### **8.3. complaint_cause_analysis**

Columnas N: analysis_id uuid PK, period_from date, period_to date, cut_at timestamptz, scope_definition jsonb,
examined_population jsonb, causes jsonb, impact jsonb, methodology_reference jsonb y bloques T y S.

### **8.4. complaint_improvement_action**

Columnas N: improvement_id uuid PK, analysis_id uuid FK, responsible_reference jsonb, due_at timestamptz, status
varchar(20) y bloques T y C.

Columna S: verification_evidence jsonb.

Estados PROPOSED, IN_PROGRESS y VERIFIED. VERIFIED exige evidencia.

El análisis es mensual, con consolidación trimestral.

## **9. Gestión administrativa: consultas**

### **9.1. administrative_enquiry**

| **Columna**                | **Tipo**      | **Nulabilidad** |
|----------------------------|---------------|-----------------|
| enquiry_id, presenter_id   | uuid          | N               |
| question                   | varchar(4000) | N               |
| channel_code, purpose_code | varchar(80)   | N               |
| received_at                | timestamptz   | N               |
| original_received_at       | timestamptz   | S               |
| original_receipt_evidence  | jsonb         | S               |
| object_references          | jsonb         | N               |
| response_content           | jsonb         | S               |
| status                     | varchar(20)   | N               |
| T, C                       | Bloques       |                 |

PK enquiry_id, FK a presentante local.

Estados RECEIVED, CHECKING, ANSWERED y DERIVATION_PENDING.

### **9.2. administrative_enquiry_source**

Columnas N: source_id uuid PK, enquiry_id uuid FK, source_reference jsonb, query_content jsonb, status varchar(20) y
bloque S.

Columna S: result_payload jsonb.

Estados VERIFIED, UNAVAILABLE y PENDING.

### **9.3. administrative_enquiry_derivation**

Columnas N: derivation_id uuid PK, enquiry_id uuid FK, target_service varchar(80), remote_action_id uuid,
original_received_at timestamptz, requested_content jsonb, status varchar(20) y bloques T y C.

Columna S: result_reference jsonb.

UQ (target_service, remote_action_id).

La derivación no reinicia recepción.

## **10. Procedimientos administrativos**

### **10.1. administrative_procedure**

Columnas N: procedure_id uuid PK, code varchar(80) UQ, current_version bigint, bloque T y lock_version bigint.

### **10.2. administrative_procedure_version**

| **Columna**                     | **Tipo**     | **Nulabilidad** |
|---------------------------------|--------------|-----------------|
| procedure_id                    | uuid         | N               |
| procedure_version               | bigint       | N               |
| title                           | varchar(200) | N               |
| purpose_code                    | varchar(80)  | N               |
| typed_inputs, escalation_rules  | jsonb        | N               |
| completion_responsible_function | varchar(80)  | N               |
| status                          | varchar(30)  | N               |
| preparer_person_id              | uuid         | N               |
| approver_person_id              | uuid         | S               |
| approval_reference              | jsonb        | S               |
| T, S                            | Bloques      |                 |

PK sobre procedimiento y versión. FK a raíz.

Estados DRAFT, SUBMITTED_FOR_REVIEW, APPROVED, ENABLED y RETIRED.

Aprobar exige huella exacta y personas distintas.

### **10.3. administrative_procedure_step**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| procedure_id                    | uuid        | N               |
| procedure_version               | bigint      | N               |
| step_code                       | varchar(80) | N               |
| task_type                       | varchar(20) | N               |
| required_function_code          | varchar(80) | N               |
| deadline_rule, separation_rules | jsonb       | N               |
| executor_service                | varchar(80) | S               |
| completion_rule                 | jsonb       | N               |

PK sobre procedimiento, versión y paso.

FK a versión.

### **10.4. administrative_procedure_transition**

Columnas N: procedure_id uuid, procedure_version bigint, from_step_code varchar(80), to_step_code varchar(80) y
condition_definition jsonb.

PK sobre versión y ambos pasos.

FK compuestas a pasos de la misma versión.

Los retornos admitidos conservan condición y seguimiento. No se habilitan transiciones indeterminadas ni procedimientos
sin responsable de conclusión.

Una versión nueva no migra expedientes existentes automáticamente.

## **11. Expedientes y tareas**

### **11.1. administrative_case**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| case_id, procedure_id                      | uuid        | N               |
| procedure_version                          | bigint      | N               |
| object_reference, requester_reference      | jsonb       | N               |
| background_references, document_references | jsonb       | N               |
| urgency_code                               | varchar(80) | N               |
| due_at                                     | timestamptz | N               |
| responsible_references, pending_components | jsonb       | N               |
| status                                     | varchar(30) | N               |
| T, C                                       | Bloques     |                 |

PK case_id, FK a procedimiento versionado.

Estados OPEN, IN_PROGRESS, AWAITING_EXECUTION, COMPLETED y CANCELLED.

### **11.2. administrative_case_update**

Columnas N: update_id uuid PK, case_id uuid FK, previous_version bigint, resulting_version bigint, change_content jsonb,
reason varchar(4000) y bloques T y S.

### **11.3. administrative_task**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| task_id, case_id, procedure_id | uuid        | N               |
| procedure_version              | bigint      | N               |
| step_code                      | varchar(80) | N               |
| task_type                      | varchar(20) | N               |
| object_reference               | jsonb       | N               |
| required_function_code         | varchar(80) | N               |
| due_at, assignment_due_at      | timestamptz | N               |
| urgency_code                   | varchar(80) | N               |
| separation_requirements        | jsonb       | N               |
| assignee_reference             | jsonb       | S               |
| status                         | varchar(30) | N               |
| T, C                           | Bloques     |                 |

PK task_id.

FK al expediente y paso de su procedimiento.

Tipos COLLECT, REVIEW, APPROVE, EXECUTE y VERIFY.

Estados UNASSIGNED, ASSIGNED, IN_PROGRESS, AWAITING_DECISION, AWAITING_EXECUTION, AWAITING_VERIFICATION, COMPLETED y
CANCELLED.

### **11.4. administrative_task_assignment**

Columnas N: assignment_id uuid PK, task_id uuid FK, assignee_reference jsonb, function_code varchar(80), valid_from
timestamptz, basis_reference jsonb y bloque T.

Columna S: valid_until timestamptz.

Exclusión de asignaciones activas superpuestas de la misma tarea.

### **11.5. administrative_task_escalation**

Columnas N: escalation_id uuid PK, task_id uuid FK, cause_code varchar(80), recipient_reference jsonb, escalated_at
timestamptz, result_payload jsonb y bloque T.

Las urgentes se asignan inmediatamente. Las ordinarias sin asignación se escalan al finalizar el siguiente hábil.

Vencer no completa la tarea.

## **12. Decisiones, aprobaciones y ejecución**

### **12.1. administrative_decision**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| decision_id, task_id                    | uuid          | N               |
| proposal_reference                      | jsonb         | N               |
| proposal_version                        | bigint        | N               |
| content_reference                       | jsonb         | N               |
| approved_content_hash                   | char(64)      | N               |
| decision                                | varchar(20)   | N               |
| reason                                  | varchar(4000) | N               |
| scope_definition, conditions            | jsonb         | N               |
| valid_from                              | timestamptz   | N               |
| valid_until                             | timestamptz   | S               |
| actor_person_id                         | uuid          | N               |
| authority_evidence, evidence_references | jsonb         | N               |
| T, S                                    | Bloques       |                 |

PK decision_id, FK a tarea.

Decisiones REVIEWED, APPROVED y REJECTED.

### **12.2. administrative_approval**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| approval_id, decision_id               | uuid        | N               |
| preparer_person_id, approver_person_id | uuid        | N               |
| proposal_reference                     | jsonb       | N               |
| proposal_version                       | bigint      | N               |
| approved_content_hash                  | char(64)    | N               |
| authorized_scope, authority_evidence   | jsonb       | N               |
| valid_from                             | timestamptz | N               |
| valid_until                            | timestamptz | S               |
| separation_required                    | boolean     | N               |
| T                                      | Bloque      |                 |

PK approval_id, FK a decisión y UQ sobre decisión.

Cuando se exige separación, las personas son distintas.

Una facultad retirada antes de decidir impide utilizarla.

Consultar aprobación verifica correspondencia, vigencia y alcance. No la convierte en permiso genérico ni la consume.

### **12.3. administrative_task_verification**

Columnas N: verification_id uuid PK, task_id uuid FK, execution_step_id uuid FK, verifier_person_id uuid, conclusion
varchar(20), evidence_references jsonb y bloque T.

Conclusiones VERIFIED, INCOMPLETE y FAILED.

Aprobar sin ejecutar mantiene ejecución pendiente.

### **12.4. administrative_external_action**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| external_action_id                      | uuid        | N, PK           |
| simulated_participant_reference         | jsonb       | N               |
| origin_reference, recipient_reference   | jsonb       | N               |
| authenticity_status                     | varchar(20) | N               |
| object_reference                        | jsonb       | N               |
| received_at, due_at                     | timestamptz | N               |
| document_references, required_treatment | jsonb       | N               |
| status                                  | varchar(30) | N               |
| T, C, S                                 | Bloques     |                 |

Las medidas se derivan a Restricciones; reportes a Información regulatoria; recuperaciones a sus propietarios.

Una autoridad no comprobada no recibe información protegida.

## **13. Documentos: identidad y versiones**

### **13.1. document**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| document_id                       | uuid        | N, PK           |
| meaning_owner_service             | varchar(80) | N               |
| origin_reference                  | jsonb       | N               |
| purpose_code, classification_code | varchar(80) | N               |
| category_code                     | varchar(80) | S               |
| current_version                   | bigint      | N               |
| T, C                              | Bloques     |                 |

Una categoría ausente impide disponer.

### **13.2. document_information_subject**

Columnas N: document_id uuid, subject_service varchar(80), subject_type varchar(80) y subject_id uuid.

PK sobre las cuatro columnas. FK a documento.

### **13.3. document_version**

| **Columna**                       | **Tipo**     | **Nulabilidad** |
|-----------------------------------|--------------|-----------------|
| document_id                       | uuid         | N               |
| document_version                  | bigint       | N               |
| document_version_id               | uuid         | N               |
| document_type_code                | varchar(80)  | N               |
| origin_reference                  | jsonb        | N               |
| purpose_code, classification_code | varchar(80)  | N               |
| category_code                     | varchar(80)  | S               |
| media_type                        | varchar(120) | N               |
| byte_length                       | bigint       | N               |
| bytes_hash                        | char(64)     | N               |
| provenance_type                   | varchar(20)  | N               |
| previous_version_id               | uuid         | S               |
| storage_object_id                 | uuid         | N               |
| integrity_status                  | varchar(20)  | N               |
| authenticity_references           | jsonb        | N               |
| T                                 | Bloque       |                 |

PK sobre documento y versión.

UQ sobre identidad de versión.

FK a documento, versión anterior y objeto de almacenamiento.

Procedencias ORIGINAL, COPY, DERIVED y RECONSTRUCTED.

Integridad PENDING, VERIFIED, CORRUPTED y UNAVAILABLE.

Las versiones no se sobrescriben.

## **14. Ingreso y transferencia de archivos**

### **14.1. document_ingestion**

| **Columna**                             | **Tipo**     | **Nulabilidad** |
|-----------------------------------------|--------------|-----------------|
| ingestion_id                            | uuid         | N, PK           |
| target_document_id                      | uuid         | S               |
| mode                                    | varchar(10)  | N               |
| origin_reference, contributor_reference | jsonb        | N               |
| purpose_code, document_type_code        | varchar(80)  | N               |
| category_code                           | varchar(80)  | S               |
| classification_code                     | varchar(80)  | N               |
| document_date                           | date         | S               |
| expected_media_type                     | varchar(120) | N               |
| expected_byte_length                    | bigint       | N               |
| expected_bytes_hash                     | char(64)     | N               |
| upload_expires_at                       | timestamptz  | S               |
| status                                  | varchar(30)  | N               |
| confirmed_document_version_id           | uuid         | S               |
| T, C                                    | Bloques      |                 |

FK a documento destino y versión confirmada cuando existan.

Modos INLINE y DEFERRED.

DEFERRED exige vencimiento exactamente a las 24 horas de creación.

Tamaño entre 1 y 26.214.400 bytes.

Estados CREATED, UPLOADING, RECEIVED, VERIFYING, VERIFIED, FINALIZING, CONFIRMED, EXPIRED, REJECTED y REQUIRES_REVIEW.

### **14.2. document_upload_attempt**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| upload_attempt_id, ingestion_id | uuid        | N               |
| upload_action_id, operation_id  | uuid        | N               |
| expected_byte_length            | bigint      | N               |
| expected_bytes_hash             | char(64)    | N               |
| actual_byte_length              | bigint      | S               |
| actual_bytes_hash               | char(64)    | S               |
| temporary_storage_object_id     | uuid        | S               |
| started_at                      | timestamptz | N               |
| completed_at                    | timestamptz | S               |
| status                          | varchar(20) | N               |
| lock_version                    | bigint      | N               |

PK upload_attempt_id.

FK a ingreso, actuación y objeto temporal.

UQ sobre actuación.

Estados RECEIVING, COMPLETE, INTERRUPTED y REJECTED.

Se rechazan cargas parciales y Content-Range.

Una interrupción no confirma documento. La repetición completa con igual tamaño y hash recupera el resultado; otro
contenido genera conflicto.

### **14.3. document_content_verification**

| **Columna**                                      | **Tipo**     | **Nulabilidad** |
|--------------------------------------------------|--------------|-----------------|
| verification_id, ingestion_id, upload_attempt_id | uuid         | N               |
| actual_byte_length                               | bigint       | N               |
| actual_bytes_hash                                | char(64)     | N               |
| actual_media_type                                | varchar(120) | N               |
| format_checks, content_checks                    | jsonb        | N               |
| methodology_reference                            | jsonb        | N               |
| result                                           | varchar(20)  | N               |
| verified_at                                      | timestamptz  | N               |
| T, S                                             | Bloques      |                 |

PK verification_id, FK a ingreso e intento.

Resultados VERIFIED y REJECTED.

Se admiten PDF, JPEG, PNG, texto UTF-8 y CSV UTF-8 según tipo documental. No se admiten ejecutables ni comprimidos.

El hash se calcula sobre bytes originales.

## **15. PostgreSQL y MinIO**

### **15.1. document_storage_object**

| **Columna**          | **Tipo**     | **Nulabilidad** |
|----------------------|--------------|-----------------|
| storage_object_id    | uuid         | N, PK           |
| storage_role         | varchar(20)  | N               |
| bucket_code          | varchar(80)  | N               |
| internal_object_key  | text         | N               |
| minio_version_id     | varchar(120) | S               |
| expected_byte_length | bigint       | N               |
| expected_bytes_hash  | char(64)     | N               |
| verified_byte_length | bigint       | S               |
| verified_bytes_hash  | char(64)     | S               |
| storage_state        | varchar(30)  | N               |
| last_verified_at     | timestamptz  | S               |
| T, C                 | Bloques      |                 |

Clave interna no vacía.

Roles TEMPORARY y PRIMARY.

Estados INTENT_RECORDED, STORED, VERIFIED, DELETE_PENDING, DELETED y UNAVAILABLE.

UQ (bucket_code, internal_object_key).

La identidad de versión física se fija al comprobar almacenamiento y se conserva para lectura, verificación y
eliminación exactas.

### **15.2. document_ingestion_finalization**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| finalization_id, ingestion_id              | uuid        | N               |
| verification_id, primary_storage_object_id | uuid        | N               |
| target_document_id                         | uuid        | N               |
| allocated_document_version                 | bigint      | N               |
| allocated_document_version_id              | uuid        | N               |
| state                                      | varchar(30) | N               |
| T, C                                       | Bloques     |                 |

PK finalization_id.

FK a ingreso, verificación, objeto y documento.

UQ sobre ingreso y versión documental asignada.

Estados PREPARING, PRIMARY_OBJECT_PENDING, PRIMARY_VERIFIED y CONFIRMED.

### **15.3. Confirmación**

PostgreSQL y MinIO no comparten transacción.

El proceso conserva intención, objeto temporal, verificación, objeto definitivo y confirmación documental.

La versión, vínculo de almacenamiento, resultado, historia y outbox confirman juntos en PostgreSQL después de comprobar
el objeto definitivo.

Un objeto existente sin confirmación local se recupera mediante la finalización original.

La limpieza temporal comprueba que el objeto no pertenece a una finalización en curso.

MinIO es privado. Solo Documentos administra sus claves y acceso.

## **16. Emisión, acceso y derivados**

### **16.1. document_type_definition**

Columnas N: type_code varchar(80), type_version bigint, typed_data_schema jsonb, allowed_media_types jsonb,
required_source_rules jsonb, classification_rules jsonb y bloques T y S.

PK sobre tipo y versión.

### **16.2. document_issuance**

| **Columna**                                            | **Tipo**     | **Nulabilidad** |
|--------------------------------------------------------|--------------|-----------------|
| issuance_id                                            | uuid         | N, PK           |
| owner_service                                          | varchar(80)  | N               |
| emission_reference                                     | varchar(120) | N               |
| document_type_code                                     | varchar(80)  | N               |
| document_type_version                                  | bigint       | N               |
| responsible_source_reference, certified_fact_reference | jsonb        | N               |
| confirmed_result_reference, typed_data                 | jsonb        | N               |
| template_reference                                     | jsonb        | N               |
| approval_or_signature_reference                        | jsonb        | S               |
| document_version_id                                    | uuid         | S               |
| status                                                 | varchar(30)  | N               |
| T, C, S                                                | Bloques      |                 |

FK a tipo versionado y documento.

UQ (owner_service, emission_reference).

Estados RECEIVED, VALIDATING, GENERATING, CONFIRMED y REQUIRES_REVIEW.

Documentos no recalcula el negocio de la fuente.

### **16.3. document_access_record**

Columnas N: access_id uuid PK, document_version_id uuid FK, actor_reference jsonb, purpose_code varchar(80),
authorization_reference jsonb, accessed_at timestamptz, result_code varchar(80) y correlation_id varchar(120).

Conocer el UUID no concede acceso.

### **16.4. document_derivation**

Columnas N: derivation_id uuid PK, original_document_version_id uuid FK, derived_document_version_id uuid FK,
derivation_scope jsonb, authorization_reference jsonb y bloques T y S.

Una copia parcial o disociada se identifica como DERIVED.

## **17. Políticas de conservación**

### **17.1. document_retention_policy**

Columnas N: policy_id uuid PK, code varchar(80) UQ, current_version bigint, bloque T y lock_version bigint.

### **17.2. document_retention_policy_version**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| policy_id                                | uuid        | N               |
| policy_version                           | bigint      | N               |
| category_code                            | varchar(80) | N               |
| basis_reference                          | jsonb       | N               |
| retention_years                          | integer     | N               |
| initial_fact_rule                        | jsonb       | N               |
| scope_definition, concurrent_obligations | jsonb       | N               |
| historical_scope, access_rules           | jsonb       | N               |
| preparer_person_id                       | uuid        | N               |
| approver_person_id                       | uuid        | S               |
| approval_reference                       | jsonb       | S               |
| status                                   | varchar(20) | N               |
| T, S                                     | Bloques     |                 |

PK sobre política y versión. FK a raíz.

Estados DRAFT, SUBMITTED, APPROVED, ENABLED y RETIRED.

Plazo positivo. Habilitar exige aprobación separada.

### **17.3. document_retention_application**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| application_id, document_id, policy_id        | uuid        | N               |
| policy_version                                | bigint      | N               |
| initial_fact_reference                        | jsonb       | S               |
| initial_fact_date, permitted_disposition_date | date        | S               |
| status                                        | varchar(30) | N               |
| T, C                                          | Bloques     |                 |

PK application_id.

FK a documento y política versionada.

UQ (document_id, policy_id, policy_version).

Estados WAITING_INITIAL_FACT, ACTIVE y ELIGIBLE_BY_TIME.

Sin hecho inicial no existe vencimiento supuesto.

### **17.4. Plazos base**

| **Categoría**                      | **Plazo** | **Inicio**                                                            |
|------------------------------------|-----------|-----------------------------------------------------------------------|
| Contratos y aceptaciones           | Diez años | Último entre terminación y resolución de obligaciones.                |
| Comprobantes y evidencia operativa | Diez años | Confirmación o resolución definitiva.                                 |
| Identificación y representación    | Diez años | Última finalización aplicable.                                        |
| Reclamos y reparaciones            | Diez años | Cierre definitivo y conclusión de actuaciones.                        |
| Expedientes y aprobaciones         | Diez años | Finalización y resolución de consecuencias.                           |
| Comunicaciones obligatorias        | Diez años | Entrega o conclusión de incidencia, considerando el proceso asociado. |
| Extractos y rectificaciones        | Diez años | Emisión y relación entre versiones.                                   |
| Publicidad sin efecto contractual  | Dos años  | Fin de campaña o último envío.                                        |

Prevalece la fecha permitida más tardía.

## **18. Preservaciones y control documental**

### **18.1. document_scope_catalog_control**

Columnas N: control_id smallint PK con valor 1, catalog_version bigint, lock_version bigint y updated_at timestamptz.

Serializa clasificación, ámbitos de preservación y admisión de disposiciones.

### **18.2. document_lifecycle_control**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| document_id                              | uuid        | N, PK           |
| lifecycle_version                        | bigint      | N               |
| preservation_pending                     | boolean     | N               |
| active_preservation_count                | integer     | N               |
| active_disposition_id, deletion_claim_id | uuid        | S               |
| storage_status                           | varchar(30) | N               |
| incident_references                      | jsonb       | N               |
| lock_version                             | bigint      | N               |
| updated_at                               | timestamptz | N               |

FK a documento, disposición y ejecución cuando existan.

### **18.3. document_preservation**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| preservation_id                     | uuid        | N, PK           |
| cause_code                          | varchar(80) | N               |
| authority_reference, case_reference | jsonb       | N               |
| scope_definition                    | jsonb       | N               |
| effective_from                      | timestamptz | N               |
| effective_until                     | timestamptz | S               |
| release_condition                   | jsonb       | N               |
| catalog_barrier_version             | bigint      | N               |
| coverage_complete                   | boolean     | N               |
| status                              | varchar(30) | N               |
| T, C, S                             | Bloques     |                 |

Estados REGISTERED, ENUMERATING, ACTIVE, RELEASE_PENDING y RELEASED.

La barrera por ámbito se registra antes de enumerar documentos.

### **18.4. document_preservation_coverage**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| preservation_id, document_id | uuid        | N               |
| status                       | varchar(30) | N               |
| checked_lifecycle_version    | bigint      | N               |
| storage_evidence             | jsonb       | N               |
| checked_at                   | timestamptz | N               |

PK sobre preservación y documento. FK a ambos.

Estados PROTECTED, DELETION_PENDING_VERIFICATION, UNAVAILABLE e INCIDENT.

Una eliminación ya en curso no permite afirmar protección completa sin comprobar su resultado.

### **18.5. document_preservation_release**

Columnas N: release_id uuid PK, preservation_id uuid FK, authority_reference jsonb, basis_reference jsonb,
released_scope jsonb, result_payload jsonb y bloque T.

Levantar una preservación no elimina otras ni invalida otros fundamentos de conservación.

## **19. Disposición documental**

### **19.1. document_disposition**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| disposition_id                     | uuid        | N, PK           |
| candidate_scope, policy_references | jsonb       | N               |
| grounds                            | jsonb       | N               |
| preparer_person_id                 | uuid        | N               |
| approver_person_id                 | uuid        | S               |
| approval_reference                 | jsonb       | S               |
| approved_content_hash              | char(64)    | S               |
| status                             | varchar(40) | N               |
| T, C, S                            | Bloques     |                 |

Estados CANDIDATE, UNDER_REVIEW, AUTHORIZED, BLOCKED, PREPARED, EXECUTING, PRIMARY_DELETION_CONFIRMED, COPIES_PENDING,
COMPLETED e INCIDENT.

La autorización exige personas distintas y contenido exacto.

### **19.2. document_disposition_item**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| item_id, disposition_id, document_id   | uuid        | N               |
| document_version_id, storage_object_id | uuid        | N               |
| expected_bytes_hash                    | char(64)    | N               |
| checked_lifecycle_version              | bigint      | N               |
| status                                 | varchar(40) | N               |
| evidence_references                    | jsonb       | N               |

PK item_id.

FK a disposición, documento, versión y objeto exactos.

UQ (disposition_id, document_version_id).

### **19.3. document_disposition_execution**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| execution_id, disposition_item_id | uuid        | N               |
| generation                        | bigint      | N               |
| claim_owner                       | uuid        | N               |
| claim_until, claimed_at           | timestamptz | N               |
| storage_command_id                | uuid        | N               |
| exact_storage_target              | jsonb       | N               |
| state                             | varchar(30) | N               |
| result_evidence                   | jsonb       | S               |
| concluded_at                      | timestamptz | S               |

PK execution_id, FK a elemento.

UQ (disposition_item_id, generation) y sobre comando de almacenamiento.

Estados CLAIMED, REQUESTED, CONFIRMED, CANCELLED, UNCERTAIN e INCIDENT.

Antes de reclamar se comprueban nuevamente políticas, preservaciones y aprobación.

La eliminación usa el objeto y versión físicos exactos. No elimina por prefijos ni por una clave mutable que pueda
apuntar a otro contenido.

### **19.4. document_copy_inventory**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| copy_id, document_version_id | uuid        | N               |
| copy_type                    | varchar(20) | N               |
| managed_copy_reference       | jsonb       | N               |
| created_at                   | timestamptz | N               |
| expected_expiration_at       | timestamptz | S               |
| treatment_status             | varchar(30) | N               |

PK copy_id, FK a versión.

Tipos REPLICA, BACKUP y MANAGED_EXPORT.

### **19.5. document_copy_disposition_result**

Columnas N: result_id uuid PK, copy_id uuid FK, disposition_item_id uuid FK, treatment_type varchar(30), confirmed_at
timestamptz, evidence_references jsonb y bloque T.

Una fecha esperada de expiración no prueba purga.

Mientras falten copias o respaldos se conserva COPIES_PENDING.

### **19.6. document_disposition_tombstone**

| **Columna**                                    | **Tipo**    | **Nulabilidad** |
|------------------------------------------------|-------------|-----------------|
| tombstone_id, document_id, document_version_id | uuid        | N               |
| disposition_id                                 | uuid        | N               |
| scope_code                                     | varchar(80) | N               |
| authorization_reference, outcome_evidence      | jsonb       | N               |
| primary_deleted_at                             | timestamptz | S               |
| copies_concluded_at                            | timestamptz | S               |
| restore_blocked                                | boolean     | N               |
| T                                              | Bloque      |                 |

PK tombstone_id.

UQ sobre versión documental.

Conserva identidad y fundamento sin reproducir contenido eliminado.

La restauración consulta estas marcas antes de habilitar contenido.

## **20. Recuperación documental**

### **20.1. document_recovery**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| recovery_id, document_id      | uuid        | N               |
| affected_document_version_id  | uuid        | N               |
| incident_reference            | jsonb       | N               |
| recovery_mode                 | varchar(30) | N               |
| source_ingestion_id           | uuid        | S               |
| resulting_document_version_id | uuid        | S               |
| evidence_references           | jsonb       | N               |
| result_payload                | jsonb       | N               |
| status                        | varchar(30) | N               |
| T, C, S                       | Bloques     |                 |

PK recovery_id.

FK a documento, versiones e ingreso cuando existan.

Modalidades VERIFY_INTEGRITY, RECOVER_COPY, CORRECT_METADATA, REGISTER_RECTIFICATION, RECONSTRUCT y RECORD_LOSS.

La recuperación compara bytes con hash esperado.

Una reconstrucción se identifica como RECONSTRUCTED.

La pérdida no elimina el hecho económico. Se comunica al propietario y a Riesgos mediante coordinaciones persistentes.

Preservaciones y disposiciones se reaplican antes de habilitar contenido restaurado.

## **21. Comunicaciones: plantillas**

### **21.1. communication_template**

Columnas N: template_id uuid PK, code varchar(80) UQ, current_version bigint, bloque T y lock_version bigint.

### **21.2. communication_template_version**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| template_id                   | uuid        | N               |
| template_version              | bigint      | N               |
| purpose_code                  | varchar(80) | N               |
| allowed_channels              | jsonb       | N               |
| content_definition            | jsonb       | N               |
| source_definitions            | jsonb       | N               |
| classification_code           | varchar(80) | N               |
| language_code                 | varchar(80) | N               |
| accessibility_definition      | jsonb       | N               |
| preparer_person_id            | uuid        | N               |
| approver_person_id            | uuid        | S               |
| functional_approval_reference | jsonb       | S               |
| status                        | varchar(20) | N               |
| T, S                          | Bloques     |                 |

PK sobre plantilla y versión. FK a raíz.

Estados DRAFT, SUBMITTED, APPROVED, ENABLED y RETIRED.

La habilitación exige aprobación del contenido exacto. El contenido habilitado es inmutable.

### **21.3. communication_template_field**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| template_id      | uuid        | N               |
| template_version | bigint      | N               |
| field_code       | varchar(80) | N               |
| value_type       | varchar(20) | N               |
| required         | boolean     | N               |
| restrictions     | jsonb       | N               |

PK sobre plantilla, versión y campo. FK a versión.

Tipos TEXT, DATE, INSTANT, AMOUNT, CODE y REFERENCE.

Un campo esencial faltante impide enviar.

## **22. Preferencias y consentimiento**

### **22.1. communication_preference**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| preference_id, party_id                      | uuid        | N               |
| purpose_code                                 | varchar(80) | N               |
| preferred_channels                           | jsonb       | N               |
| language_code                                | varchar(80) | N               |
| accessibility_definition, contact_references | jsonb       | N               |
| T, C                                         | Bloques     |                 |

PK preference_id.

UQ (party_id, purpose_code).

Los contactos pertenecen a Personas.

### **22.2. communication_consent_control**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| party_id        | uuid        | N               |
| purpose_code    | varchar(80) | N               |
| consent_status  | varchar(20) | N               |
| consent_version | bigint      | N               |
| effective_at    | timestamptz | N               |
| lock_version    | bigint      | N               |
| updated_at      | timestamptz | N               |

PK sobre persona y finalidad.

Estados NOT_GRANTED, GRANTED y REVOKED.

La ausencia de consentimiento no se transforma en autorización.

### **22.3. communication_consent_action**

Columnas N: consent_action_id uuid PK, party_id uuid, purpose_code varchar(80), consent_version bigint, action
varchar(20), effective_at timestamptz, evidence_references jsonb y bloque T.

FK al control. UQ sobre persona, finalidad y versión.

Acciones GRANT_CONSENT y REVOKE_CONSENT.

La revocación impide nuevos despachos opcionales. No impide comunicaciones obligatorias mediante canales válidos.

## **23. Solicitudes y destinatarios**

### **23.1. communication**

| **Columna**                           | **Tipo**     | **Nulabilidad** |
|---------------------------------------|--------------|-----------------|
| communication_id                      | uuid         | N, PK           |
| requesting_service                    | varchar(80)  | N               |
| request_reference                     | varchar(120) | N               |
| origin_reference                      | jsonb        | N               |
| purpose_code, communication_type_code | varchar(80)  | N               |
| content_mode                          | varchar(20)  | N               |
| template_id                           | uuid         | S               |
| template_version                      | bigint       | S               |
| typed_values                          | jsonb        | S               |
| approved_content_reference            | jsonb        | S               |
| classification_code                   | varchar(80)  | N               |
| allowed_channels, document_references | jsonb        | N               |
| useful_until                          | timestamptz  | S               |
| mandatory_recipients_satisfied        | boolean      | N               |
| T, C, S                               | Bloques      |                 |

UQ (requesting_service, request_reference).

TEMPLATE exige plantilla versionada y datos. APPROVED_CONTENT exige referencia inmutable, de forma excluyente.

### **23.2. communication_recipient**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| recipient_id, communication_id             | uuid        | N               |
| party_reference, contact_version_reference | jsonb       | S               |
| declared_contact_reference                 | jsonb       | S               |
| mandatory                                  | boolean     | N               |
| delivery_requirement_code                  | varchar(80) | N               |
| satisfied                                  | boolean     | N               |
| verification_evidence                      | jsonb       | N               |

PK recipient_id, FK a comunicación.

La solicitud exige al menos un destinatario y canales no vacíos.

Un contacto declarado no recibe información privada sin comprobación suficiente.

La satisfacción depende del requisito de entrega, no del mero envío.

## **24. Intentos y despacho de comunicaciones**

### **24.1. communication_attempt**

| **Columna**                 | **Tipo**     | **Nulabilidad** |
|-----------------------------|--------------|-----------------|
| attempt_id, recipient_id    | uuid         | N               |
| attempt_number              | integer      | N               |
| channel_code, adapter_code  | varchar(80)  | N               |
| contact_version_reference   | jsonb        | S               |
| protected_destination       | bytea        | N               |
| destination_key_reference   | varchar(120) | N               |
| immutable_content_reference | jsonb        | N               |
| sent_content_hash           | char(64)     | N               |
| external_reference          | varchar(120) | S               |
| attempted_at                | timestamptz  | N               |
| result                      | varchar(30)  | N               |
| failure_class               | varchar(20)  | S               |
| evidence_references         | jsonb        | N               |
| T, C                        | Bloques      |                 |

PK attempt_id, FK a destinatario.

UQ (recipient_id, attempt_number).

Resultados PREPARED, SENT, PROVIDER_ACCEPTED, MADE_AVAILABLE, DELIVERED, REJECTED y UNCERTAIN.

El destino histórico no cambia al actualizar un contacto.

### **24.2. communication_dispatch_control**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| attempt_id              | uuid        | N, PK           |
| optional_send           | boolean     | N               |
| consent_party_id        | uuid        | S               |
| consent_purpose_code    | varchar(80) | S               |
| checked_consent_version | bigint      | S               |
| dispatch_state          | varchar(20) | N               |
| claim_owner             | uuid        | S               |
| claim_generation        | bigint      | N               |
| claim_until             | timestamptz | S               |
| dispatch_action_id      | uuid        | N               |
| last_result_reference   | jsonb       | S               |
| lock_version            | bigint      | N               |

FK a intento y control de consentimiento cuando sea opcional.

UQ sobre actuación de despacho.

Estados PENDING, CLAIMED, SENT, STOPPED y UNCERTAIN.

CLAIMED exige propietario y vencimiento.

Antes de reclamar un envío opcional se bloquea y comprueba su consentimiento. La revocación utiliza el mismo control.

Los ya reclamados conservan tratamiento según avance real. No se afirma impedimento retroactivo.

## **25. Constancias y recuperación de comunicaciones**

### **25.1. communication_receipt**

| **Columna**                              | **Tipo**     | **Nulabilidad** |
|------------------------------------------|--------------|-----------------|
| receipt_id, attempt_id, recipient_id     | uuid         | N               |
| source_service                           | varchar(80)  | N               |
| source_receipt_reference                 | varchar(120) | N               |
| source_version                           | bigint       | N               |
| result                                   | varchar(30)  | N               |
| result_at                                | timestamptz  | N               |
| evidence_references, document_references | jsonb        | N               |
| T, S                                     | Bloques      |                 |

PK receipt_id.

FK a intento y su destinatario.

UQ (source_service, source_receipt_reference, source_version).

Una constancia tardía actualiza el intento original y conserva antecedentes.

Mailpit acredita aceptación técnica cuando ese sea el hecho informado. No se convierte en entrega.

### **25.2. communication_recovery_action**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| recovery_action_id, communication_id | uuid        | N               |
| attempt_id                           | uuid        | S               |
| recovery_mode                        | varchar(30) | N               |
| evidence_references                  | jsonb       | N               |
| resulting_attempt_id                 | uuid        | S               |
| assisted_task_reference              | jsonb       | S               |
| result_payload                       | jsonb       | N               |
| T, C, S                              | Bloques     |                 |

PK recovery_action_id.

FK a comunicación e intentos locales.

Modalidades QUERY_ATTEMPT, RETRY_CONFIRMED_FAILURE, USE_ALTERNATIVE_CHANNEL, OPEN_ASSISTED_TASK y STOP_OBSOLETE_SEND.

La incertidumbre exige consultar antes de reenviar.

Fallas transitorias comprobadas permiten reintentos a los cinco y treinta minutos. Si persisten, se usa alternativa
verificada o atención asistida.

Un mensaje vencido o sustituido no origina nuevos envíos.

## **26. Extractos: definiciones y composición**

### **26.1. statement_definition**

Columnas N: definition_id uuid PK, code varchar(80) UQ, current_version bigint, bloque T y lock_version bigint.

### **26.2. statement_definition_version**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| definition_id                            | uuid        | N               |
| definition_version                       | bigint      | N               |
| modality                                 | varchar(40) | N               |
| products_definition, required_sources    | jsonb       | N               |
| field_definitions, periodicity, controls | jsonb       | N               |
| recipient_rules                          | jsonb       | N               |
| preparer_person_id                       | uuid        | N               |
| approver_person_id                       | uuid        | S               |
| approval_reference                       | jsonb       | S               |
| status                                   | varchar(20) | N               |
| T, S                                     | Bloques     |                 |

PK sobre definición y versión. FK a raíz.

Modalidades MONTHLY_ACCOUNT_STATEMENT, MONTHLY_INVESTMENT_REPORT, REQUESTED_MONTHLY_CONSOLIDATED y AS_OF_CERTIFICATE.

Estados DRAFT, SUBMITTED, APPROVED, ENABLED y RETIRED.

### **26.3. statement_composition**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| composition_id, definition_id                      | uuid        | N               |
| definition_version                                 | bigint      | N               |
| authorized_holders, product_references, currencies | jsonb       | N               |
| temporal_mode                                      | varchar(10) | N               |
| from_date, to_date_exclusive                       | date        | S               |
| as_of_date                                         | date        | S               |
| cutoff_at                                          | timestamptz | N               |
| status                                             | varchar(20) | N               |
| composition_hash                                   | char(64)    | S               |
| limitations                                        | jsonb       | N               |
| T, C, S                                            | Bloques     |                 |

PK composition_id, FK a definición versionada.

PERIOD exige inicio y final; AS_OF exige fecha de referencia. Son excluyentes.

Estados COMPOSING, INCOMPLETE, VALIDATED y REJECTED.

### **26.4. statement_composition_revision**

Columnas N: composition_id uuid, composition_version bigint, snapshot_payload jsonb, composition_hash char(64) y bloques
T y S.

PK sobre composición y versión.

La emisión utiliza una revisión exacta e inmutable.

El período mensual es calendario, con corte al inicio del mes siguiente. La emisión ordinaria se realiza dentro de los
dos primeros hábiles siguientes.

## **27. Fuentes, secciones y controles de extractos**

### **27.1. statement_source_snapshot**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| snapshot_id, composition_id     | uuid        | N               |
| composition_version             | bigint      | N               |
| source_service, object_type     | varchar(80) | N               |
| object_id                       | uuid        | N               |
| source_cut                      | jsonb       | N               |
| source_version, source_sequence | bigint      | S               |
| source_payload                  | jsonb       | N               |
| source_hash                     | char(64)    | N               |
| completeness                    | varchar(20) | N               |
| missing_data_codes              | jsonb       | N               |

PK snapshot_id.

FK a composición versionada.

Estados COMPLETE, PARTIAL y UNAVAILABLE.

Se exige versión o secuencia cuando la fuente la utiliza. Su ausencia no se convierte en cero.

### **27.2. statement_section**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| section_id, composition_id | uuid        | N               |
| composition_version        | bigint      | N               |
| position                   | integer     | N               |
| section_type               | varchar(30) | N               |
| product_reference          | jsonb       | N               |
| currency                   | varchar(3)  | S               |
| typed_content              | jsonb       | N               |
| schema_version             | bigint      | N               |

PK section_id.

FK a composición versionada.

UQ (composition_id, composition_version, position).

Secciones:

| **Tipo**                 | **Contenido**                                                  |
|--------------------------|----------------------------------------------------------------|
| ACCOUNT                  | Saldos, movimientos, reservas, inmovilizaciones y descubierto. |
| TERM_DEPOSIT             | Capital, actualización, rendimiento, vencimiento y pagos.      |
| INVESTMENT               | Instrumento, cantidades, disponibilidad, tránsito y valuación. |
| CARD_STATEMENT_REFERENCE | Resumen versionado y documento de Cuenta de tarjeta.           |

### **27.3. statement_account_movement**

Columnas N: section_id uuid, position bigint, source_movement_reference jsonb, movement_sequence bigint, effective_date
date, direction varchar(10), amount numeric(24,2), currency varchar(3) y concept_code varchar(80).

PK sobre sección y posición. FK a sección ACCOUNT.

Los movimientos conservan identidad de fuente.

### **27.4. statement_composition_control**

| **Columna**                 | **Tipo**       | **Nulabilidad** |
|-----------------------------|----------------|-----------------|
| composition_id              | uuid           | N               |
| composition_version         | bigint         | N               |
| control_code                | varchar(80)    | N               |
| inputs, evidence_references | jsonb          | N               |
| result                      | varchar(10)    | N               |
| monetary_difference         | numeric(24,2)  | S               |
| quantity_difference         | numeric(30,12) | S               |

PK sobre composición, versión y control.

Resultados PASSED, FAILED y PENDING.

Diferencias con signo.

Se verifica saldo final = inicial + créditos − débitos.

Las reservas no son débitos. Las monedas se presentan separadas.

Extractos no recalcula total, mínimo, deuda ni vencimiento de tarjeta.

## **28. Emisión y rectificación de extractos**

### **28.1. statement**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| statement_id                             | uuid        | N, PK           |
| definition_id                            | uuid        | N               |
| ownership_scope_hash, product_scope_hash | char(64)    | N               |
| temporal_mode                            | varchar(10) | N               |
| from_date, to_date_exclusive, as_of_date | date        | S               |
| current_version                          | bigint      | N               |
| T                                        | Bloque      |                 |
| lock_version                             | bigint      | N               |

FK a definición.

UQ parciales para identidad periódica o a fecha, por definición, titularidad, productos y fechas correspondientes.

### **28.2. statement_version**

| **Columna**                    | **Tipo**     | **Nulabilidad** |
|--------------------------------|--------------|-----------------|
| statement_id                   | uuid         | N               |
| statement_version              | bigint       | N               |
| composition_id                 | uuid         | N               |
| composition_version            | bigint       | N               |
| emission_reference             | varchar(120) | N               |
| emitted_content_hash           | char(64)     | N               |
| authorized_holders, recipients | jsonb        | N               |
| condition                      | varchar(10)  | N               |
| limitations                    | jsonb        | N               |
| partial_approval_reference     | jsonb        | S               |
| document_reference             | jsonb        | N               |
| issued_at                      | timestamptz  | N               |
| previous_statement_version     | bigint       | S               |
| T                              | Bloque       |                 |

PK sobre extracto y versión.

FK a extracto, composición exacta y versión anterior.

UQ sobre referencia de emisión.

Condiciones COMPLETE y PARTIAL.

PARTIAL exige aprobación y limitaciones visibles. No sustituye al completo pendiente.

### **28.3. statement_issuance_process**

| **Columna**                                       | **Tipo**     | **Nulabilidad** |
|---------------------------------------------------|--------------|-----------------|
| issuance_process_id, statement_id, composition_id | uuid         | N               |
| target_statement_version, composition_version     | bigint       | N               |
| emission_reference                                | varchar(120) | N               |
| requested_content_hash                            | char(64)     | N               |
| recipients, delivery_mode                         | jsonb        | N               |
| status                                            | varchar(30)  | N               |
| T, C, S                                           | Bloques      |                 |

PK issuance_process_id.

FK a extracto y composición.

UQ sobre emisión y (statement_id, target_statement_version).

Estados PREPARING, DOCUMENT_PENDING, DOCUMENT_CONFIRMED, ISSUED y REQUIRES_REVIEW.

La versión emitida se crea después de comprobar el documento requerido.

### **28.4. statement_rectification**

Columnas N: rectification_id uuid PK, statement_id uuid FK, original_version bigint, resulting_version bigint,
reason_code varchar(80), origin_correction_reference jsonb, differences jsonb, validated_composition_reference jsonb y
bloques T y S.

FK a ambas versiones.

### **28.5. statement_continuity_review**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| review_id, rectification_id, affected_statement_id  | uuid          | N               |
| previous_closing_balance, resulting_closing_balance | numeric(24,2) | N               |
| affected_currency                                   | varchar(3)    | N               |
| required_treatment                                  | jsonb         | N               |
| status                                              | varchar(20)   | N               |
| T, C                                                | Bloques       |                 |

PK review_id, FK a rectificación y extracto afectado.

Saldos con signo.

La revisión no incorpora movimientos posteriores al corte ni repite correcciones económicas.

### **28.6. statement_delivery**

Columnas N: delivery_id uuid PK, statement_id uuid, statement_version bigint, recipient_references jsonb,
document_reference jsonb, remote_action_id uuid UQ, status varchar(20) y bloques T y C.

Columnas S: communication_reference jsonb y delivery_evidence jsonb.

FK a extracto versionado.

Emisión y entrega mantienen estados separados.

## **29. Índices complementarios**

| **Tabla**                      | **Índice**                                                         |
|--------------------------------|--------------------------------------------------------------------|
| complaint                      | (attention_status, received_at, complaint_id)                      |
| complaint                      | (responsible_service, closure_status, complaint_id)                |
| complaint_deadline             | (due_at, complaint_id, deadline_id)                                |
| complaint_repair               | (executor_service, status, repair_id)                              |
| complaint_repair_result        | (repair_id, created_at, result_id)                                 |
| administrative_enquiry         | (status, received_at, enquiry_id)                                  |
| administrative_case            | (status, due_at, case_id)                                          |
| administrative_task            | (status, due_at, task_id)                                          |
| administrative_task            | (case_id, step_code, task_id)                                      |
| administrative_decision        | (task_id, created_at, decision_id)                                 |
| document                       | (meaning_owner_service, category_code, document_id)                |
| document_version               | (bytes_hash, byte_length, document_version_id)                     |
| document_ingestion             | (status, upload_expires_at, ingestion_id)                          |
| document_storage_object        | (storage_state, updated_at, storage_object_id)                     |
| document_retention_application | (status, permitted_disposition_date, application_id)               |
| document_preservation_coverage | (document_id, status, preservation_id)                             |
| document_disposition           | (status, updated_at, disposition_id)                               |
| document_copy_inventory        | (treatment_status, expected_expiration_at, copy_id)                |
| communication                  | (requesting_service, request_reference)                            |
| communication_attempt          | (recipient_id, attempted_at, attempt_id)                           |
| communication_attempt          | (adapter_code, external_reference, attempt_id)                     |
| communication_dispatch_control | (dispatch_state, claim_until, attempt_id)                          |
| statement_composition          | (status, cutoff_at, composition_id)                                |
| statement_source_snapshot      | (composition_id, composition_version, source_service, snapshot_id) |
| statement_issuance_process     | (status, updated_at, issuance_process_id)                          |
| statement_version              | (issued_at, statement_id, statement_version)                       |

Las FK compuestas incorporan UQ de pertenencia cuando deben conservar el mismo reclamo, expediente, documento,
destinatario o versión.

## **30. Orden de bloqueo y atomicidad**

### **30.1. Reclamos**

Reclamo → resolución → reparaciones ordenadas → resultados y comunicación.

El cierre comprueba todas sus dimensiones bajo el control del reclamo.

### **30.2. Gestión administrativa**

Expediente → tareas ordenadas → propuestas y decisiones → ejecución y verificación.

Un ejecutor conserva la aprobación utilizada y comprueba sus propias condiciones.

### **30.3. Documentos**

Control de ámbitos → documentos ordenados → controles de ciclo de vida → ingreso, preservación o disposición →
ejecución.

Los procesos que solo reciben bytes no mantienen una transacción abierta durante la transferencia.

La reclamación de eliminación se confirma antes de llamar a MinIO.

Si llega una preservación durante una eliminación en curso, se detienen las partes todavía cancelables y se comprueba el
resultado. No se afirma protección de contenido posiblemente eliminado.

### **30.4. Comunicaciones**

Controles de consentimiento ordenados, cuando correspondan → comunicación → destinatarios → intentos → despacho.

La recuperación y los reintentos mantienen el mismo orden.

### **30.5. Extractos**

Identidad del extracto → composición → revisión exacta → proceso de emisión → versión emitida.

La generación documental ocurre fuera de la transacción.

### **30.6. Confirmación**

Cada servicio confirma cambios, historia, resultado, inbox aplicable y outbox conjuntamente.

La misma ejecución recibida por HTTP y evento se reconoce mediante su identidad original.

Un 404 no demuestra que una solicitud en tránsito nunca llegará.

## **31. Eventos y trabajos persistentes**

| **Contrato** | **Tipo**                    | **Tema**                           |
|--------------|-----------------------------|------------------------------------|
| CI-12-037    | ComplaintChanged            | banking.complaint.events           |
| CI-12-038    | AdministrativeActionChanged | banking.administrative-case.events |
| CI-12-039    | DocumentActionChanged       | banking.document.events            |
| CI-12-040    | CommunicationOutcomeChanged | banking.communication.events       |
| CI-12-041    | StatementIssuedOrRectified  | banking.statement.events           |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado definido por cada variante.

Los eventos documentales no transportan archivos ni causas reservadas de preservación.

Los eventos de aprobación no son órdenes genéricas de ejecución.

Las comunicaciones publican el hecho realmente acreditado. Los extractos no representan movimientos económicos.

Los trabajos conservan identidad de:

- Reclamo y plazo.

- Reparación y componente.

- Tarea y escalación.

- Ingreso, carga y finalización.

- Objeto temporal.

- Preservación y enumeración.

- Disposición, objeto y generación.

- Copia y tratamiento.

- Comunicación e intento.

- Composición y emisión.

- Rectificación y revisión de continuidad.

Una respuesta perdida no se resuelve creando una actuación equivalente con otra identidad.

## **32. Migraciones, conservación y seguridad**

Las migraciones utilizan Flyway por servicio.

Las referencias circulares de raíz y versión se implementan como diferibles.

Las versiones utilizadas de procedimientos, aprobaciones, documentos, plantillas y extractos permanecen inmutables.

No se eliminan mediante cascadas decisiones, reparaciones, constancias ni evidencias utilizadas.

La disposición documental utiliza su procedimiento específico, no borrados generales de expedientes.

Redis no decide permisos, aprobaciones, preservaciones, disposiciones ni entrega.

La restauración reaplica marcas de disposición y preservaciones antes de habilitar contenido.

Las actuaciones remotas posteriores al respaldo se reconcilian antes de reanudar instrucciones.

La autorización se comprueba también en consultas históricas y recuperación idempotente.

## **33. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                                  |
|-----------------------|-------------------------------------------------------------------|
| CI-12-001             | Consultas administrativas, fuentes y derivaciones.                |
| CI-12-002 y CI-12-003 | Reclamos, recepción, plazos e investigación.                      |
| CI-12-004 y CI-12-005 | Resoluciones, reparaciones y resultados del ejecutor.             |
| CI-12-006 a CI-12-008 | Comunicación, cierre, reapertura y análisis de causas.            |
| CI-12-009             | Procedimientos, pasos, transiciones y aprobación.                 |
| CI-12-010 y CI-12-011 | Expedientes, tareas y asignaciones.                               |
| CI-12-012 y CI-12-013 | Decisiones y comprobación de aprobaciones.                        |
| CI-12-014 a CI-12-016 | Ejecución, verificación, actuaciones externas y consulta.         |
| CI-12-017             | Ingresos, cargas, verificación y confirmación documental.         |
| CI-12-018 y CI-12-019 | Emisión, versiones y acceso documental.                           |
| CI-12-020             | Políticas y aplicaciones de conservación.                         |
| CI-12-021 y CI-12-022 | Preservación, disposición, ejecución y copias.                    |
| CI-12-023 y CI-12-024 | Recuperación e incidencias documentales.                          |
| CI-12-025 a CI-12-027 | Plantillas, preferencias y consentimiento.                        |
| CI-12-028 a CI-12-031 | Solicitudes, destinatarios, intentos, constancias y recuperación. |
| CI-12-032 y CI-12-033 | Definiciones, fuentes, secciones y composición.                   |
| CI-12-034 a CI-12-036 | Emisión, rectificación, continuidad y versiones.                  |
| CI-12-037 a CI-12-041 | Outbox e historia de agregados.                                   |

## **34. Escenarios de verificación de implementación**

| **Escenario**                                     | **Resultado exigido**                                        |
|---------------------------------------------------|--------------------------------------------------------------|
| Reclamo sin identidad comprobada                  | Conserva recepción sin acceso privado.                       |
| Presentante desconoce operación técnica           | No impide recibir el problema.                               |
| Derivación a reclamo                              | Conserva recepción original.                                 |
| Reclasificación o reasignación                    | No reinicia el plazo.                                        |
| Resolución aprobada sin reparación                | Mantiene ejecución pendiente.                                |
| Reparación ya ejecutada por otro procedimiento    | Vincula el efecto sin otro reintegro.                        |
| Resultado por HTTP y evento                       | Se incorpora una sola vez.                                   |
| Nueva versión de procedimiento                    | No migra expedientes silenciosamente.                        |
| Preparador intenta aprobar con separación exigida | No confirma aprobación.                                      |
| Contenido aprobado cambia                         | Exige otra aprobación.                                       |
| Tarea vencida                                     | No se considera completada.                                  |
| Carga interrumpida                                | No confirma documento.                                       |
| Archivo vacío o superior a 25 MiB                 | Rechaza contenido.                                           |
| Mismo ingreso con otro archivo                    | Genera conflicto.                                            |
| Objeto en MinIO sin confirmación local            | Recupera finalización sin otra versión.                      |
| Categoría desconocida                             | Impide disponer.                                             |
| Preservación por ámbito                           | Bloquea nuevas disposiciones antes de completar enumeración. |
| Preservación durante eliminación                  | Comprueba resultado sin afirmar protección inexistente.      |
| Primario eliminado y respaldo pendiente           | Mantiene COPIES_PENDING.                                     |
| Restauración de versión dispuesta                 | Bloquea disponibilidad mediante su marca persistente.        |
| Reconstrucción documental                         | Se identifica como reconstruida.                             |
| Revocación publicitaria anterior al despacho      | Impide el nuevo envío opcional.                              |
| Mailpit acepta el mensaje                         | No inventa entrega.                                          |
| Envío incierto                                    | Consulta antes de reenviar.                                  |
| Cambio de contacto posterior                      | Conserva destino histórico.                                  |
| Extracto con fuente incompleta                    | No se emite como completo.                                   |
| Reserva de cuenta                                 | No se presenta como débito.                                  |
| Resumen de tarjeta incluido                       | Conserva versión y documento de Cuenta de tarjeta.           |
| Rectificación de extracto                         | Conserva anterior y revisa continuidad.                      |
| Respuesta perdida o restauración                  | Recupera identidades originales sin repetir efectos.         |

Las restricciones, concurrencia, integración con MinIO, consentimiento, cortes y recuperación se verifican durante la
implementación con PostgreSQL y almacenamiento aislados.
