# **Diseño de persistencia — Módulo 11. Riesgo y cumplimiento**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Riesgo y cumplimiento.

| **Servicio**             | **Responsabilidad**                                                              |
|--------------------------|----------------------------------------------------------------------------------|
| Riesgos                  | Políticas, límites, exposición, compromisos, evaluaciones, excesos e incidentes. |
| Conocimiento del cliente | Verificaciones, beneficiarios finales, listas, perfiles y adecuación inversora.  |
| Prevención de lavado     | Escenarios, monitoreo, alertas e investigaciones reservadas.                     |
| Prevención de fraude     | Controles, evaluaciones e investigaciones de operaciones.                        |
| Restricciones            | Medidas, alcance, distribución, aplicación y preparación de actos.               |
| Criterios tributarios    | Reglas, condiciones fiscales, determinaciones y acumuladores compartidos.        |
| Información regulatoria  | Requerimientos, obligaciones y presentaciones a destinatarios simulados.         |

Comprende los contratos CI-11-001 a CI-11-051 y las preparaciones frente a restricciones.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases.

Los propietarios operativos conservan sus decisiones y efectos.

Un control favorable no constituye admisión, concesión crediticia, celebración contractual ni ejecución monetaria.

Una investigación resuelta no demuestra que las medidas o reparaciones solicitadas estén ejecutadas.

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

| **Dato**                | **PostgreSQL** | **Restricción**                    |
|-------------------------|----------------|------------------------------------|
| Identificador           | uuid           | Estable y no reutilizable.         |
| Versión                 | bigint         | Positiva.                          |
| Control de concurrencia | bigint         | No negativo.                       |
| Secuencia de fuente     | bigint         | Positiva dentro de su flujo.       |
| Código                  | varchar(80)    | No vacío y registrado.             |
| Denominación            | varchar(200)   | No vacía.                          |
| Referencia externa      | varchar(120)   | No vacía.                          |
| Fundamento              | varchar(4000)  | No vacío cuando está presente.     |
| Moneda                  | varchar(3)     | ARS o USD.                         |
| Importe monetario       | numeric(24,2)  | No negativo.                       |
| Medición                | numeric(30,12) | Signo según su significado.        |
| Proporción              | numeric(24,12) | Conforme a la metodología.         |
| Participación           | numeric(15,12) | Entre cero y uno.                  |
| Hash                    | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Instante                | timestamptz    | Intercambio en UTC.                |
| Fecha                   | date           | Fecha bancaria.                    |
| Contenido               | jsonb          | Esquema registrado y versionado.   |

La precisión y escala se validan antes de persistir.

Las mediciones conservan su unidad. Una cantidad no se interpreta como dinero ni como proporción.

La falta de información no equivale a exposición cero, exención, capacidad ilimitada o aprobación.

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

**Bloque M — Medición tipada**

| **Columna**          | **Tipo**       | **Nulabilidad** |
|----------------------|----------------|-----------------|
| measurement_type     | varchar(10)    | N               |
| measurement_presence | varchar(10)    | N               |
| money_value          | numeric(24,2)  | S               |
| ratio_value          | numeric(24,12) | S               |
| quantity_value       | numeric(30,12) | S               |
| currency             | varchar(3)     | S               |
| quantity_unit_code   | varchar(80)    | S               |
| instrument_reference | jsonb          | S               |

Tipos MONEY, RATIO y QUANTITY.

Presencia KNOWN o UNKNOWN.

KNOWN exige exclusivamente el valor correspondiente. MONEY exige moneda; QUANTITY exige unidad. Los instrumentos agregan
su referencia.

UNKNOWN conserva tipo y unidad conocidos, pero no contiene un valor supuesto.

Cuando se indica un prefijo, se incorpora a todos los nombres. Por ejemplo, authorized_money_value.

Los bloques representan columnas físicas, no herencia.

### **2.4. Tablas comunes**

Los siete servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Restricciones incorpora authority_preparation.

Las llamadas externas se realizan fuera de las transacciones PostgreSQL.

## **3. Esquemas estructurados**

Cada JSONB conserva su versión mediante columna explícita o envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                    | **Contenido**                                                                |
|--------------------------------|------------------------------------------------------------------------------|
| ControlScope                   | Sujetos, productos, objetos, actos, combinación y alcance general explícito. |
| EvidenceReferences             | Fuente, observación, hecho, documentos y verificación.                       |
| PendingComponents              | Componente, responsable, motivo y seguimiento.                               |
| RiskPolicyDefinition           | Finalidad, entradas, metodología, parámetros, consecuencias y excepciones.   |
| RiskExposureComposition        | Componentes individuales, agregados y sustituciones.                         |
| DueDiligenceEvidence           | Versiones declaradas, fuentes y verificaciones.                              |
| BeneficialOwnershipCriterion   | Umbral, control por otros medios y tratamiento de estructuras incompletas.   |
| AmlScenarioDefinition          | Claves, ventana, operadores, umbrales y acciones.                            |
| FraudControlDefinition         | Señales, condiciones, resultados y motivos comunicables.                     |
| RestrictionDefinition          | Autoridad, fundamento, sujetos, objetos, actos, prioridad y alcance.         |
| TaxRuleDefinition              | Tributo, jurisdicción, hecho generador, base, tasa y exenciones.             |
| TaxAccumulatorDefinition       | Identidad estable, período, unidad y política de restitución de capacidad.   |
| ReportingRequirementDefinition | Destinatario, formato, fuentes, disparador y vencimiento.                    |
| SubmissionValidation           | Validaciones, resultado, faltantes y aprobaciones especializadas.            |

No se admiten scripts, SQL, expresiones ejecutables libres ni parámetros desconocidos.

Una colección vacía no significa alcance universal. Este requiere modalidad explícita y fundamento autorizado.

## **4. Definiciones, propuestas y aprobación**

### **4.1. Familias físicas**

Cada servicio mantiene las tablas de su competencia:

| **Definición**            | **Raíz**              | **Versiones**                 | **Propuestas**                 | **Revisiones**               |
|---------------------------|-----------------------|-------------------------------|--------------------------------|------------------------------|
| Política de riesgo        | risk_policy           | risk_policy_version           | risk_policy_proposal           | risk_policy_review           |
| Escenario de lavado       | aml_scenario          | aml_scenario_version          | aml_scenario_proposal          | aml_scenario_review          |
| Control de fraude         | fraud_control         | fraud_control_version         | fraud_control_proposal         | fraud_control_review         |
| Regla tributaria          | tax_rule              | tax_rule_version              | tax_rule_proposal              | tax_rule_review              |
| Requerimiento informativo | reporting_requirement | reporting_requirement_version | reporting_requirement_proposal | reporting_requirement_review |

Estas son tablas locales independientes. No constituyen una base compartida.

En todas ellas, la columna física de identidad es definition_id.

### **4.2. Raíz**

Columnas N:

- definition_id uuid, PK.

- code varchar(80), UQ.

- current_version bigint.

- event_version bigint.

- lock_version bigint.

- Bloque T.

### **4.3. Versión**

| **Columna**                                          | **Tipo**     | **Nulabilidad** |
|------------------------------------------------------|--------------|-----------------|
| definition_id                                        | uuid         | N               |
| definition_version                                   | bigint       | N               |
| name                                                 | varchar(200) | N               |
| purpose_code                                         | varchar(80)  | N               |
| basis_reference, scope_definition                    | jsonb        | N               |
| input_definitions, methodology_reference             | jsonb        | N               |
| typed_parameters, consequences, permitted_exceptions | jsonb        | N               |
| specific_definition                                  | jsonb        | N               |
| valid_from                                           | timestamptz  | N               |
| valid_until                                          | timestamptz  | S               |
| next_review_at                                       | timestamptz  | N               |
| status                                               | varchar(20)  | N               |
| source_proposal_id, approved_review_id               | uuid         | N               |
| T, S                                                 | Bloques      |                 |

PK sobre identidad y versión.

FK a raíz, propuesta y revisión de la misma familia.

Estados APPROVED, ENABLED, SUSPENDED y WITHDRAWN.

La raíz referencia su versión actual mediante FK diferible.

El contenido aprobado es inmutable.

### **4.4. Propuesta**

| **Columna**                                       | **Tipo**    | **Nulabilidad** |
|---------------------------------------------------|-------------|-----------------|
| proposal_id                                       | uuid        | N, PK           |
| definition_id                                     | uuid        | S               |
| proposal_version                                  | bigint      | N               |
| proposed_definition, examples, validation_results | jsonb       | N               |
| preparer_person_id                                | uuid        | N               |
| status                                            | varchar(20) | N               |
| T, S                                              | Bloques     |                 |
| lock_version                                      | bigint      | N               |
| updated_at                                        | timestamptz | N               |

FK opcional a raíz.

Estados DRAFT, SUBMITTED, APPROVED, APPLIED, REJECTED y WITHDRAWN.

### **4.5. Revisión**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| review_id, proposal_id | uuid          | N               |
| proposal_version       | bigint        | N               |
| reviewed_content_hash  | char(64)      | N               |
| preparer_person_id     | uuid          | N               |
| approver_person_id     | uuid          | S               |
| decision               | varchar(20)   | N               |
| approval_reference     | jsonb         | S               |
| decided_at             | timestamptz   | S               |
| reason                 | varchar(4000) | S               |
| T                      | Bloque        |                 |

PK review_id, FK a propuesta.

Decisiones PENDING, APPROVED y REJECTED.

APPROVED exige aprobación, fecha y personas distintas.

La aplicación comprueba versión y huella exactas. Un cambio material exige otra revisión.

## **5. Coordinaciones y evidencias**

### **5.1. Pasos remotos**

Las tablas siguientes utilizan la misma estructura:

| **Tabla**                    | **Propietario N**             | **FK**                            |
|------------------------------|-------------------------------|-----------------------------------|
| risk_commitment_remote_step  | commitment_id uuid            | risk_capacity_commitment          |
| aml_report_request_step      | report_request_id uuid        | aml_specialized_report_request    |
| fraud_required_action        | investigation_id uuid         | fraud_investigation               |
| restriction_application_step | application_component_id uuid | restriction_application_component |
| reporting_remote_step        | obligation_id uuid            | reporting_obligation              |

Columnas adicionales:

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| step_id                             | uuid        | N, PK           |
| component_code, responsible_service | varchar(80) | N               |
| remote_action_id                    | uuid        | N               |
| requested_content                   | jsonb       | N               |
| status                              | varchar(30) | N               |
| result_reference, result_payload    | jsonb       | S               |
| evidence_references                 | jsonb       | N               |
| last_checked_at, next_check_at      | timestamptz | S               |
| T, C, S                             | Bloques     |                 |

UQ por propietario y componente, y por servicio y actuación remota.

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

### **5.2. Evidencias**

Cada servicio mantiene control_evidence en su propia base:

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| evidence_id         | uuid        | N, PK           |
| source_reference    | jsonb       | N               |
| source_version      | bigint      | S               |
| observed_at         | timestamptz | N               |
| fact_occurred_at    | timestamptz | S               |
| document_references | jsonb       | N               |
| verification_status | varchar(20) | N               |
| evidence_payload    | jsonb       | N               |
| T, S                | Bloques     |                 |

Estados VERIFIED, DECLARED, OBSERVED y UNAVAILABLE.

Las decisiones conservan las referencias exactas de evidencia utilizadas.

## **6. Riesgos: límites y selección de ámbitos**

### **6.1. risk_scope_catalog_control**

Columnas N:

- control_id smallint, PK con valor 1.

- catalog_version bigint.

- lock_version bigint.

- updated_at timestamptz.

Serializa cambios de pertenencia a grupos y de aplicabilidad de límites.

### **6.2. risk_limit**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| limit_id            | uuid        | N, PK           |
| risk_type           | varchar(20) | N               |
| scope_type          | varchar(80) | N               |
| scope_reference     | jsonb       | N               |
| scope_identity_hash | char(64)    | N               |
| horizon_code        | varchar(80) | N               |
| current_version     | bigint      | N               |
| T                   | Bloque      |                 |
| lock_version        | bigint      | N               |

Riesgos CREDIT, LIQUIDITY, MARKET y OPERATIONAL.

Ámbitos PERSON, GROUP, COUNTERPARTY, PRODUCT u otro registrado.

UQ (risk_type, scope_type, scope_identity_hash, horizon_code).

### **6.3. risk_limit_version**

| **Columna**                                   | **Tipo**                        | **Nulabilidad** |
|-----------------------------------------------|---------------------------------|-----------------|
| limit_id                                      | uuid                            | N               |
| limit_version                                 | bigint                          | N               |
| authorized_M                                  | Bloque M con prefijo authorized |                 |
| consumption_methodology, conversion_reference | jsonb                           | N               |
| valid_from                                    | timestamptz                     | N               |
| valid_until                                   | timestamptz                     | S               |
| status                                        | varchar(20)                     | N               |
| policy_reference, approval_reference          | jsonb                           | N               |
| T, S                                          | Bloques                         |                 |

PK sobre límite y versión. FK a límite.

Estados DRAFT, APPROVED, ACTIVE, SUSPENDED y WITHDRAWN.

ACTIVE exige medición autorizada conocida.

### **6.4. risk_limit_consumption**

| **Columna**             | **Tipo**                      | **Nulabilidad** |
|-------------------------|-------------------------------|-----------------|
| limit_id                | uuid                          | N, PK           |
| current_M               | Bloque M con prefijo current  |                 |
| capacity_M              | Bloque M con prefijo capacity |                 |
| excess_M                | Bloque M con prefijo excess   |                 |
| consumption_composition | jsonb                         | N               |
| completeness            | varchar(20)                   | N               |
| control_version         | bigint                        | N               |
| lock_version            | bigint                        | N               |
| updated_at              | timestamptz                   | N               |

FK a límite.

La metodología determina consumo y capacidad. Las mediciones no aditivas no se calculan mediante resta genérica.

Una reducción conserva exposición y compromisos, registra exceso e impide nuevas afectaciones incompatibles.

## **7. Grupos vinculados**

### **7.1. risk_group**

Columnas N: group_id uuid PK, current_version bigint, bloque T y lock_version bigint.

### **7.2. risk_group_determination**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| determination_id, group_id                 | uuid        | N               |
| group_version                              | bigint      | N               |
| criterion_reference, relationship_evidence | jsonb       | N               |
| completeness                               | varchar(20) | N               |
| valid_from                                 | timestamptz | N               |
| valid_until                                | timestamptz | S               |
| approval_reference                         | jsonb       | N               |
| T, S                                       | Bloques     |                 |

PK determination_id, FK a grupo.

UQ (group_id, group_version).

### **7.3. risk_group_membership**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| membership_id, group_id       | uuid        | N               |
| group_version                 | bigint      | N               |
| subject_service, subject_type | varchar(80) | N               |
| subject_id                    | uuid        | N               |
| basis_reference               | jsonb       | N               |
| effective_from                | timestamptz | N               |
| effective_until               | timestamptz | S               |

PK membership_id, FK a determinación versionada.

UQ (group_id, group_version, subject_service, subject_type, subject_id).

Índice inverso (subject_service, subject_type, subject_id, effective_from, group_id).

Compartir apellido o domicilio no determina agrupación.

Una pertenencia incompleta conserva su limitación y no se interpreta como ausencia de grupo.

## **8. Exposición y fuentes**

### **8.1. risk_exposure_source_position**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| source_position_id                | uuid        | N, PK           |
| owner_service, source_object_type | varchar(80) | N               |
| source_object_id                  | uuid        | N               |
| subject_references                | jsonb       | N               |
| cut_at                            | timestamptz | N               |
| current_source_version            | bigint      | S               |
| current_stream_sequence           | bigint      | S               |
| source_stream_code                | varchar(80) | S               |
| completeness                      | varchar(20) | N               |
| last_received_at                  | timestamptz | N               |
| lock_version                      | bigint      | N               |

UQ (owner_service, source_object_type, source_object_id).

La fuente debe ofrecer versión o secuencia interpretable. Una secuencia exige flujo.

### **8.2. risk_exposure_source_update**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| update_id, source_position_id         | uuid        | N               |
| source_update_id                      | uuid        | N               |
| source_version, stream_sequence       | bigint      | S               |
| source_cut_at, received_at            | timestamptz | N               |
| original_payload, evidence_references | jsonb       | N               |
| incorporation_status                  | varchar(20) | N               |
| T, S                                  | Bloques     |                 |

PK update_id, FK a posición fuente.

UQ (source_position_id, source_update_id).

Estados APPLIED, HISTORICAL, GAP_PENDING y CONTRADICTORY.

### **8.3. risk_exposure_component**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| exposure_component_id                        | uuid        | N, PK           |
| owner_service, economic_origin_type          | varchar(80) | N               |
| economic_origin_id                           | uuid        | N               |
| economic_component_code, exposure_type       | varchar(80) | N               |
| source_position_id                           | uuid        | S               |
| source_version                               | bigint      | N               |
| subject_references, product_reference        | jsonb       | N               |
| M                                            | Bloque      |                 |
| related_commitment_id, replaced_component_id | uuid        | S               |
| coverage_references                          | jsonb       | N               |
| valid_from                                   | timestamptz | N               |
| valid_until                                  | timestamptz | S               |
| status                                       | varchar(20) | N               |
| T, C, S                                      | Bloques     |                 |

PK exposure_component_id.

FK a fuente, compromiso y componente sustituido.

UQ (owner_service, economic_origin_type, economic_origin_id, economic_component_code).

Estados PENDING, EFFECTIVE, REPLACED y CEASED.

### **8.4. risk_exposure_representation**

Columnas N: representation_id uuid PK, source_position_id uuid FK, source_version bigint, representation_type
varchar(20), included_components jsonb, methodology_reference jsonb y bloques T y S.

Representaciones COMPONENTS o AGGREGATE.

La incorporación de agregados reconoce componentes ya existentes, sin sumarlos otra vez.

### **8.5. risk_exposure_source_control**

Columnas N: source_position_id uuid PK y FK, contiguous_source_version bigint, pending_ranges jsonb, missing_sources
jsonb, completeness varchar(20), lock_version bigint y updated_at timestamptz.

Contador no negativo.

Una fuente incompleta impide presentar capacidad definitiva como completa.

## **9. Compromisos y transformaciones de capacidad**

### **9.1. risk_capacity_commitment**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| commitment_id                         | uuid        | N, PK           |
| owner_service, origin_component_code  | varchar(80) | N               |
| origin_operation_id                   | uuid        | N               |
| subject_references, product_reference | jsonb       | N               |
| stage_code, horizon_code              | varchar(80) | N               |
| methodology_reference                 | jsonb       | N               |
| approval_reference                    | jsonb       | S               |
| valid_from                            | timestamptz | N               |
| valid_until                           | timestamptz | S               |
| conclusion_conditions                 | jsonb       | N               |
| status                                | varchar(30) | N               |
| T, C, S                               | Bloques     |                 |

UQ (owner_service, origin_operation_id, origin_component_code).

Estados PREPARED, COMMITTED, TRANSFORMATION_PENDING, TRANSFORMED, RELEASE_PENDING, RELEASED y RECONCILIATION_REQUIRED.

Estos estados no sustituyen el protocolo común de preparación de autoridades.

### **9.2. risk_commitment_limit_allocation**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| commitment_id, limit_id | uuid        | N               |
| limit_version           | bigint      | N               |
| M                       | Bloque      |                 |
| allocation_state        | varchar(20) | N               |
| updated_at              | timestamptz | N               |

PK sobre compromiso y límite.

FK a compromiso y límite versionado.

Estados PENDING, EFFECTIVE, TRANSFORMED y RELEASED.

Índice inverso (limit_id, allocation_state, commitment_id).

Todos los límites obligatorios se afectan conjuntamente.

### **9.3. risk_capacity_transformation**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| transformation_id, commitment_id           | uuid        | N               |
| economic_result_owner                      | varchar(80) | N               |
| economic_result_id                         | uuid        | N               |
| replaced_components, resulting_components  | jsonb       | N               |
| methodology_reference, evidence_references | jsonb       | N               |
| T, S                                       | Bloques     |                 |

PK transformation_id, FK a compromiso.

UQ (commitment_id, economic_result_owner, economic_result_id).

**risk_transformation_limit_change**

Columnas N: transformation_id uuid, limit_id uuid, previous_measurement jsonb, resulting_measurement jsonb,
signed_variation jsonb y schema_version bigint.

PK sobre transformación y límite. FK a ambos.

Las tres mediciones utilizan el esquema tipado y las mismas unidades.

La transformación retira la representación pendiente e incorpora la efectiva en una transacción.

Una aprobación 500 transformada en financiación 500 conserva consumo 500, sin consumo 1000 ni capacidad libre
intermedia.

### **9.4. risk_fx_transformation**

| **Columna**                                   | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------|---------------|-----------------|
| transformation_id, commitment_id              | uuid          | N               |
| fx_service                                    | varchar(80)   | N               |
| fx_operation_id, funds_group_id               | uuid          | N               |
| base_currency, quote_currency                 | varchar(3)    | N               |
| base_amount, quote_amount                     | numeric(24,2) | N               |
| confirmed_funds_result, methodology_reference | jsonb         | N               |
| T, S                                          | Bloques       |                 |

PK transformation_id.

FK a transformación y compromiso.

UQ (fx_service, fx_operation_id).

Exige ambas piernas confirmadas. No presume salida de cuenta externa ni elimina exposición efectiva.

### **9.5. risk_commitment_action**

Columnas N: action_id uuid PK, commitment_id uuid FK, action varchar(20), requested_composition jsonb,
evidence_references jsonb, result_payload jsonb y bloques T y S.

Acciones TRANSFORM, ADJUST y RELEASE_CANCELLED.

La liberación exige cancelación comprobada y ausencia de ejecución pendiente.

## **10. Evaluaciones y casos de Riesgos**

### **10.1. risk_assessment**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| assessment_id                              | uuid        | N, PK           |
| scope_definition, source_cuts              | jsonb       | N               |
| antecedent_references, position_references | jsonb       | N               |
| methodology_reference, scenario_results    | jsonb       | N               |
| conclusion_code                            | varchar(80) | N               |
| completeness                               | varchar(20) | N               |
| T, S                                       | Bloques     |                 |

### **10.2. risk_assessment_indicator**

Columnas N: assessment_id uuid, indicator_code varchar(80), bloque M y source_references jsonb.

PK sobre evaluación y código. FK a evaluación.

### **10.3. risk_assessment_classification**

Columnas N: assessment_id uuid, classification_code varchar(80), scope_reference jsonb y methodology_reference jsonb.

PK sobre evaluación y clasificación. FK a evaluación.

### **10.4. risk_management_case**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| case_id                             | uuid        | N, PK           |
| case_type                           | varchar(20) | N               |
| scope_definition, source_references | jsonb       | N               |
| responsible_reference               | jsonb       | N               |
| exception_maximum                   | jsonb       | S               |
| exception_expires_at                | timestamptz | S               |
| approval_reference                  | jsonb       | S               |
| status                              | varchar(20) | N               |
| T, C                                | Bloques     |                 |

Tipos EXCESS, EXCEPTION e INCIDENT.

EXCEPTION exige máximo, vencimiento y aprobación.

**risk_case_action**

Columnas N: case_action_id uuid PK, case_id uuid FK, action varchar(20), content jsonb, evidence_references jsonb y
bloques T y S.

Acciones ADD_EVIDENCE, RECORD_TREATMENT, ESCALATE y CLOSE.

Cerrar exige comprobar tratamiento. No elimina exposición.

## **11. Conocimiento del cliente: identidad**

### **11.1. customer_due_diligence_case**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| case_id, party_id                                | uuid        | N               |
| party_version                                    | bigint      | N               |
| purpose_code                                     | varchar(80) | N               |
| declared_version_references, document_references | jsonb       | N               |
| related_control_references                       | jsonb       | N               |
| T, C                                             | Bloques     |                 |

PK case_id.

Índice (party_id, purpose_code, created_at, case_id).

### **11.2. identity_verification**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| verification_id, case_id, party_id        | uuid        | N               |
| party_version                             | bigint      | N               |
| purpose_code                              | varchar(80) | N               |
| declared_data_reference, required_sources | jsonb       | N               |
| result                                    | varchar(20) | N               |
| completed_at, valid_until                 | timestamptz | S               |
| T, C, S                                   | Bloques     |                 |

PK verification_id, FK a expediente.

Resultados SATISFACTORY, INSUFFICIENT, CONTRADICTORY y PENDING.

SATISFACTORY exige finalización y fuentes suficientes.

### **11.3. identity_attribute_verification**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| verification_id                    | uuid        | N               |
| attribute_code                     | varchar(80) | N               |
| result                             | varchar(20) | N               |
| evidence_references, discrepancies | jsonb       | N               |

PK sobre verificación y atributo. FK a verificación.

Una fuente ausente mantiene pendiente el atributo. No concede representación ni modifica silenciosamente Personas.

## **12. Beneficiarios finales**

### **12.1. beneficial_ownership_determination**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| determination_id, entity_party_id | uuid        | N               |
| entity_party_version              | bigint      | N               |
| criterion_reference               | jsonb       | N               |
| cut_at                            | timestamptz | N               |
| result_status                     | varchar(30) | N               |
| findings, missing_data_codes      | jsonb       | N               |
| T, C, S                           | Bloques     |                 |

PK determination_id.

Estados DETERMINED, INCOMPLETE y REQUIRES_RESOLUTION.

### **12.2. beneficial_ownership_node**

Columnas N: node_id uuid PK, determination_id uuid FK, party_id uuid, party_type varchar(20) y source_reference jsonb.

UQ (determination_id, party_id).

### **12.3. beneficial_ownership_relationship**

| **Columna**                       | **Tipo**       | **Nulabilidad** |
|-----------------------------------|----------------|-----------------|
| relationship_id, determination_id | uuid           | N               |
| from_node_id, to_node_id          | uuid           | N               |
| right_owner_service               | varchar(80)    | N               |
| source_right_id                   | uuid           | N               |
| source_right_version              | bigint         | N               |
| economic_share, voting_share      | numeric(15,12) | S               |
| control_mode                      | varchar(80)    | N               |
| valid_from                        | date           | N               |
| valid_until                       | date           | S               |
| evidence_references               | jsonb          | N               |

PK relationship_id.

FK a determinación y nodos de esa determinación.

UQ (determination_id, right_owner_service, source_right_id, source_right_version).

Las participaciones presentes están entre cero y uno.

### **12.4. beneficial_ownership_path**

Columnas N: path_id uuid PK, determination_id uuid FK, relationship_sequence jsonb, contribution_payload jsonb,
result_status varchar(20) y bloque S.

Cada secuencia ordenada identifica relaciones de la misma determinación.

### **12.5. beneficial_owner_result**

| **Columna**                  | **Tipo**       | **Nulabilidad** |
|------------------------------|----------------|-----------------|
| determination_id, party_id   | uuid           | N               |
| economic_share, voting_share | numeric(15,12) | S               |
| control_mode                 | varchar(80)    | N               |
| basis, evidence_references   | jsonb          | N               |

PK sobre determinación y persona. FK a determinación.

Se aplica el umbral del 10 % y el control por otros medios.

Los derechos no se duplican al aparecer por varias fuentes. Las estructuras circulares o incompletas permanecen bajo
resolución.

## **13. Listas y perfiles**

### **13.1. compliance_list_check**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| check_id, party_id                        | uuid        | N               |
| party_version                             | bigint      | N               |
| purpose_code                              | varchar(80) | N               |
| checked_at                                | timestamptz | N               |
| related_party_references, versioned_lists | jsonb       | N               |
| result                                    | varchar(30) | N               |
| T, C, S                                   | Bloques     |                 |

PK check_id.

Resultados NO_RELEVANT_MATCH, PENDING_MATCH, FALSE_POSITIVE y CONFIRMED_MATCH.

### **13.2. compliance_list_match**

Columnas N: match_id uuid PK, check_id uuid FK, list_reference jsonb, list_entry_reference jsonb, compared_attributes
jsonb, evidence_references jsonb y bloque S.

### **13.3. compliance_list_match_resolution**

Columnas N: resolution_id uuid PK, match_id uuid FK, match_version bigint, conclusion varchar(30), reason varchar(4000),
intervening_person_id uuid, evidence_references jsonb y bloque T.

Una coincidencia de nombre no demuestra identidad ni ejecuta restricción.

### **13.4. customer_compliance_profile**

Columnas N: profile_id uuid PK, party_id uuid UQ, current_version bigint, bloque T y lock_version bigint.

### **13.5. customer_compliance_profile_version**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| profile_id                                       | uuid        | N               |
| profile_version                                  | bigint      | N               |
| party_version                                    | bigint      | N               |
| activity, purpose, products, channels, geography | jsonb       | N               |
| antecedent_references, methodology_reference     | jsonb       | N               |
| risk_level                                       | varchar(10) | N               |
| expected_activity, required_diligence, reasons   | jsonb       | N               |
| established_at, next_review_at                   | timestamptz | N               |
| T, S                                             | Bloques     |                 |

PK sobre perfil y versión. FK a perfil.

LOW y MEDIUM: revisión máxima a doce meses. HIGH: seis meses.

Los hechos relevantes pueden anticiparla.

### **13.6. investment_investor_profile**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| investor_profile_id, party_id              | uuid        | N               |
| profile_version                            | bigint      | N               |
| objectives, horizon, experience, knowledge | jsonb       | N               |
| liquidity_needs, loss_tolerance            | jsonb       | N               |
| profile_class                              | varchar(30) | N               |
| acceptance_evidence                        | jsonb       | N               |
| established_at, valid_until                | timestamptz | N               |
| T, S                                       | Bloques     |                 |

PK sobre identidad y versión.

Clases CONSERVATIVE, MODERATE y HIGHER_TOLERANCE.

Vigencia máxima doce meses.

### **13.7. investment_order_suitability**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| suitability_id, party_id                | uuid        | N               |
| investor_profile_id                     | uuid        | N               |
| profile_version                         | bigint      | N               |
| instrument_reference, operation_content | jsonb       | N               |
| evaluated_content_hash                  | char(64)    | N               |
| result                                  | varchar(20) | N               |
| valid_until                             | timestamptz | S               |
| reasons                                 | jsonb       | N               |
| T, S                                    | Bloques     |                 |

PK suitability_id, FK a perfil versionado.

Resultados SUITABLE, UNSUITABLE y PENDING.

Una incompatibilidad no se habilita por excepción comercial.

## **14. Prevención de lavado: hechos y ventanas**

### **14.1. Definición del escenario**

aml_scenario_version.specific_definition conserva:

- Entradas tipadas.

- Claves de agregación.

- Tamaño, unidad y anclaje de ventana.

- Umbrales y operadores registrados.

- Tratamiento de correcciones y hechos tardíos.

- Acciones resultantes.

No se habilita una definición incompleta.

### **14.2. aml_operation_fact**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| fact_id                      | uuid        | N, PK           |
| owner_service                | varchar(80) | N               |
| source_fact_id, operation_id | uuid        | N               |
| current_revision             | bigint      | N               |
| T                            | Bloque      |                 |
| lock_version                 | bigint      | N               |

UQ (owner_service, source_fact_id).

### **14.3. aml_operation_fact_revision**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| fact_id                                   | uuid          | N               |
| revision                                  | bigint        | N               |
| participant_references                    | jsonb         | N               |
| amount                                    | numeric(24,2) | N               |
| currency                                  | varchar(3)    | N               |
| occurred_at, received_at                  | timestamptz   | N               |
| economic_date                             | date          | N               |
| channel_code, purpose_code                | varchar(80)   | N               |
| previous_revision                         | bigint        | S               |
| correction_treatment, evidence_references | jsonb         | N               |
| T, S                                      | Bloques       |                 |

PK sobre hecho y revisión. FK a hecho y revisión anterior.

### **14.4. aml_monitoring_window**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| window_id, scenario_id     | uuid        | N               |
| scenario_version           | bigint      | N               |
| aggregation_key_hash       | char(64)    | N               |
| aggregation_key            | jsonb       | N               |
| interval_from, interval_to | timestamptz | N               |
| evaluation_version         | bigint      | N               |
| accumulated_measurements   | jsonb       | N               |
| completeness               | varchar(20) | N               |
| lock_version               | bigint      | N               |
| updated_at                 | timestamptz | N               |

PK window_id.

FK al escenario versionado.

UQ (scenario_id, scenario_version, aggregation_key_hash, interval_from, interval_to).

### **14.5. aml_window_contribution**

| **Columna**               | **Tipo** | **Nulabilidad** |
|---------------------------|----------|-----------------|
| window_id, fact_id        | uuid     | N               |
| fact_revision             | bigint   | N               |
| contribution_measurements | jsonb    | N               |
| active                    | boolean  | N               |
| T, S                      | Bloques  |                 |

PK sobre ventana, hecho y revisión.

FK a ventana y revisión de hecho.

UQ parcial sobre ventana y hecho, donde active = true.

Una corrección retira la contribución anterior y aplica la nueva en las ventanas afectadas. Los hechos tardíos utilizan
fecha de ocurrencia.

### **14.6. aml_alert**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| alert_id, window_id | uuid        | N               |
| condition_code      | varchar(80) | N               |
| current_revision    | bigint      | N               |
| status              | varchar(20) | N               |
| T, C                | Bloques     |                 |

PK alert_id, FK a ventana.

UQ (window_id, condition_code).

**aml_alert_revision**

Columnas N: alert_id uuid, alert_revision bigint, window_evaluation_version bigint, related_facts jsonb,
evaluation_result jsonb, antecedent_references jsonb y bloques T y S.

PK sobre alerta y revisión.

Una alerta no demuestra ilicitud ni inmoviliza recursos.

## **15. Investigaciones reservadas de lavado**

### **15.1. aml_investigation**

| **Columna**                                                | **Tipo**    | **Nulabilidad** |
|------------------------------------------------------------|-------------|-----------------|
| investigation_id                                           | uuid        | N, PK           |
| alert_references, operation_references, profile_references | jsonb       | N               |
| responsible_reference                                      | jsonb       | N               |
| status                                                     | varchar(30) | N               |
| next_action_at                                             | timestamptz | N               |
| T, C                                                       | Bloques     |                 |

Estados OPEN, UNDER_INVESTIGATION, AWAITING_INFORMATION, READY_FOR_DECISION y RESOLVED.

### **15.2. aml_investigation_antecedent**

Columnas N: antecedent_id uuid PK, investigation_id uuid FK, source_reference jsonb, document_references jsonb,
diligence_content jsonb, evidence_references jsonb y bloques T y S.

### **15.3. aml_investigation_resolution**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| resolution_id, investigation_id        | uuid        | N               |
| conclusion                             | varchar(40) | N               |
| findings, evidence_references          | jsonb       | N               |
| preparer_person_id, approver_person_id | uuid        | N               |
| approval_reference                     | jsonb       | N               |
| T, S                                   | Bloques     |                 |

PK resolution_id, FK a investigación.

Conclusiones CLOSE_WITHOUT_REPORT y PREPARE_SPECIALIZED_REPORT.

Personas distintas.

### **15.4. aml_specialized_report_request**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| report_request_id, investigation_id, resolution_id | uuid        | N               |
| reporting_requirement_reference                    | jsonb       | S               |
| specialized_content_reference                      | jsonb       | N               |
| origin_approval_reference                          | jsonb       | N               |
| original_clock_started_at                          | timestamptz | N               |
| status                                             | varchar(30) | N               |
| critical_incident_reference                        | jsonb       | S               |
| T, C, S                                            | Bloques     |                 |

PK report_request_id.

FK a investigación y resolución.

Estados CONFIGURATION_PENDING, READY, REQUESTED y FOLLOW_UP.

La falta de configuración conserva incidencia crítica, no presentación supuesta.

Cerrar la relación comercial no extingue la investigación.

## **16. Prevención de fraude**

### **16.1. Controles**

fraud_control_version.specific_definition conserva señales tipadas, condiciones, requisitos adicionales y motivos
comunicables.

Los antecedentes reservados no se incluyen en el motivo comunicable.

### **16.2. fraud_operation_assessment**

| **Columna**                                   | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------|---------------|-----------------|
| assessment_id                                 | uuid          | N, PK           |
| operation_service, operation_type             | varchar(80)   | N               |
| operation_id                                  | uuid          | N               |
| operation_version                             | bigint        | N               |
| evaluated_content_hash                        | char(64)      | N               |
| participant_references, destination_reference | jsonb         | N               |
| amount                                        | numeric(24,2) | S               |
| currency                                      | varchar(3)    | S               |
| channel_code                                  | varchar(80)   | N               |
| authentication_evidence, control_versions     | jsonb         | N               |
| reserved_signals                              | jsonb         | N               |
| result                                        | varchar(40)   | N               |
| additional_requirements, communicable_reasons | jsonb         | N               |
| valid_until                                   | timestamptz   | S               |
| T, S                                          | Bloques       |                 |

Importe y moneda aparecen conjuntamente.

Resultados ALLOW, DECLINE, REQUIRE_ADDITIONAL_VERIFICATION y PENDING.

Índice (operation_service, operation_id, operation_version, assessment_id).

Un cambio material exige otra evaluación. La indisponibilidad no se convierte en ALLOW.

### **16.3. fraud_investigation**

Columnas N: investigation_id uuid PK, operation_references jsonb, indication_references jsonb, evidence_references
jsonb, dispute_references jsonb, status varchar(30) y bloques T y C.

### **16.4. fraud_investigation_resolution**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| resolution_id, investigation_id         | uuid        | N               |
| conclusion                              | varchar(30) | N               |
| verified_losses, required_actions       | jsonb       | N               |
| evidence_references, approval_reference | jsonb       | N               |
| preparer_person_id, approver_person_id  | uuid        | N               |
| T, S                                    | Bloques     |                 |

PK resolution_id, FK a investigación.

Conclusiones FRAUD_CONFIRMED, FRAUD_NOT_CONFIRMED e INCONCLUSIVE.

Las reparaciones identifican efecto original y circuito económico existente. Fraude no devuelve dinero directamente.

## **17. Restricciones: identidad y alcance**

### **17.1. restriction**

Columnas N: restriction_id uuid PK, current_version bigint, event_version bigint, bloque T y lock_version bigint.

### **17.2. restriction_version**

| **Columna**                          | **Tipo**       | **Nulabilidad** |
|--------------------------------------|----------------|-----------------|
| restriction_id                       | uuid           | N               |
| restriction_version                  | bigint         | N               |
| authority_reference, basis_reference | jsonb          | N               |
| measure_type                         | varchar(80)    | N               |
| scope_definition                     | jsonb          | N               |
| mode                                 | varchar(10)    | N               |
| total_amount                         | numeric(24,2)  | S               |
| currency                             | varchar(3)     | S               |
| total_quantity                       | numeric(30,12) | S               |
| instrument_reference                 | jsonb          | S               |
| affected_acts                        | jsonb          | N               |
| priority_code                        | varchar(80)    | N               |
| priority_basis                       | jsonb          | N               |
| received_at, effective_from          | timestamptz    | N               |
| effective_until                      | timestamptz    | S               |
| automatic_cessation_allowed          | boolean        | N               |
| unresolved_scope_codes               | jsonb          | N               |
| T, S                                 | Bloques        |                 |

PK sobre medida y versión. FK a raíz.

Modos ACTS, MONEY y UNITS.

MONEY exige total y moneda. UNITS exige cantidad e instrumento. ACTS no contiene magnitud monetaria ni de unidades.

### **17.3. restriction_scope_member**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| restriction_id             | uuid        | N               |
| restriction_version        | bigint      | N               |
| member_type                | varchar(20) | N               |
| owner_service, object_type | varchar(80) | N               |
| object_id                  | uuid        | N               |

PK sobre medida, versión, tipo y referencia.

FK a versión.

Tipos SUBJECT, PRODUCT y OBJECT.

Índice inverso (owner_service, object_type, object_id, restriction_id).

### **17.4. restriction_status**

Columnas N: restriction_id uuid PK y FK, restriction_version bigint, temporal_status varchar(20), application_status
varchar(20), incidents jsonb, lock_version bigint y updated_at timestamptz.

Temporal FUTURE, EFFECTIVE o CEASED.

Aplicación PENDING, PARTIAL o COMPLETE.

Registrar, estar vigente y estar aplicada son dimensiones distintas.

## **18. Distribución de medidas**

### **18.1. restriction_application_plan**

| **Columna**                        | **Tipo**       | **Nulabilidad** |
|------------------------------------|----------------|-----------------|
| plan_id, restriction_id            | uuid           | N               |
| restriction_version, plan_version  | bigint         | N               |
| total_amount                       | numeric(24,2)  | S               |
| total_quantity                     | numeric(30,12) | S               |
| composition, transitional_coverage | jsonb          | N               |
| status                             | varchar(30)    | N               |
| T, C, S                            | Bloques        |                 |

PK plan_id, FK a medida versionada.

UQ (restriction_id, restriction_version, plan_version).

Estados PREPARING, APPLYING, RECONCILING y COMPLETE.

### **18.2. restriction_application_component**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| application_component_id, restriction_id | uuid        | N               |
| responsible_service, object_type         | varchar(80) | N               |
| object_id                                | uuid        | N               |
| affected_acts                            | jsonb       | N               |
| priority_code                            | varchar(80) | N               |
| current_instruction_version              | bigint      | N               |
| status                                   | varchar(30) | N               |
| T, C                                     | Bloques     |                 |

PK application_component_id, FK a medida.

UQ (restriction_id, responsible_service, object_type, object_id).

### **18.3. restriction_plan_component**

| **Columna**                       | **Tipo**       | **Nulabilidad** |
|-----------------------------------|----------------|-----------------|
| plan_id, application_component_id | uuid           | N               |
| assigned_amount                   | numeric(24,2)  | S               |
| assigned_quantity                 | numeric(30,12) | S               |
| assignment_role                   | varchar(20)    | N               |

PK sobre plan y componente. FK a ambos.

Roles DEFINITIVE y TRANSITIONAL.

La suma definitiva no supera el total de la medida. La cobertura transitoria se identifica y no se publica como
afectación definitiva adicional.

Una medida total 100 no se convierte en asignación definitiva 100 por cuenta.

### **18.4. restriction_application_result**

| **Columna**                             | **Tipo**       | **Nulabilidad** |
|-----------------------------------------|----------------|-----------------|
| result_id, application_component_id     | uuid           | N               |
| instruction_version                     | bigint         | N               |
| source_service                          | varchar(80)    | N               |
| source_result_id                        | uuid           | N               |
| source_result_version                   | bigint         | N               |
| result                                  | varchar(30)    | N               |
| applied_amount, insufficient_amount     | numeric(24,2)  | S               |
| applied_quantity, insufficient_quantity | numeric(30,12) | S               |
| applied_scope, evidence_references      | jsonb          | N               |
| effective_at                            | timestamptz    | N               |
| T, S                                    | Bloques        |                 |

PK result_id, FK a componente.

UQ (application_component_id, source_service, source_result_id, source_result_version).

Resultados APPLIED, PARTIALLY_APPLIED, NOT_APPLIED y UNCERTAIN.

El resultado es una observación de alcance, no un incremento que pueda sumarse indiscriminadamente a observaciones
anteriores.

### **18.5. restriction_reallocation**

Columnas N: reallocation_id uuid PK, restriction_id uuid FK, source_plan_id uuid FK, target_plan_id uuid FK,
transitional_treatment jsonb, confirmed_source_treatment jsonb, confirmed_target_treatment jsonb, status varchar(30) y
bloques T, C y S.

La distribución final no se informa completa mientras haya resultados inciertos.

## **19. Ámbitos y preparaciones de Restricciones**

### **19.1. restriction_scope_catalog_control**

Columnas N: control_id smallint PK con valor 1, catalog_version bigint, lock_version bigint y updated_at timestamptz.

### **19.2. restriction_scope_control**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| scope_id                    | uuid        | N, PK           |
| scope_hash                  | char(64)    | N               |
| scope_definition            | jsonb       | N               |
| control_version             | bigint      | N               |
| incompatible_change_pending | boolean     | N               |
| lock_version                | bigint      | N               |
| updated_at                  | timestamptz | N               |

UQ sobre hash, comprobando contenido en coincidencias.

Registro, cambio y preparación adquieren primero catálogo y después ámbitos ordenados.

### **19.3. restriction_preparation_basis**

| **Columna**                                             | **Tipo**    | **Nulabilidad** |
|---------------------------------------------------------|-------------|-----------------|
| preparation_id                                          | uuid        | N, PK           |
| act_code                                                | varchar(80) | N               |
| subject_references, object_references, actor_references | jsonb       | N               |
| scope_measurements                                      | jsonb       | N               |
| requested_content                                       | jsonb       | N               |
| binding_content_hash                                    | char(64)    | N               |
| considered_restrictions                                 | jsonb       | N               |
| schema_version                                          | bigint      | N               |

FK a preparación común.

### **19.4. restriction_preparation_scope**

Columnas N: preparation_id uuid, scope_id uuid y control_version bigint.

PK sobre preparación y ámbito. FK a ambos.

Índice inverso (scope_id, preparation_id).

Una ambigüedad relevante no produce preparación favorable.

Una medida incompatible posterior bloquea nuevos actos y solicita resolver intentos existentes. La solicitud de aborto
no equivale a aborto confirmado.

No se liberan preparaciones por timeout ni por indisponibilidad del coordinador.

### **19.5. restriction_change**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| change_id, restriction_id                | uuid        | N               |
| previous_version, resulting_version      | bigint      | N               |
| action                                   | varchar(10) | N               |
| authority_reference, basis_reference     | jsonb       | N               |
| proposed_scope                           | jsonb       | N               |
| effective_at                             | timestamptz | N               |
| affected_preparations, treatment_results | jsonb       | N               |
| T, C, S                                  | Bloques     |                 |

PK change_id, FK a versiones.

Acciones MODIFY y LIFT.

Una medida retroactiva identifica actuaciones alcanzadas, pero no las presenta falsamente como impedidas ni borra sus
efectos.

## **20. Reglas y condiciones tributarias**

### **20.1. Definición de regla**

tax_rule_version.specific_definition conserva:

- Tributo y jurisdicción.

- Hecho generador.

- Fecha determinante.

- Base y tasa.

- Mínimos y máximos.

- Exenciones.

- Regla de redondeo.

- Acumulador compartido, cuando corresponda.

- Referencias externas y parámetros adoptados por el modelo.

La bonificación comercial no constituye exención.

### **20.2. verified_tax_condition**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| condition_id                                | uuid        | N               |
| condition_version                           | bigint      | N               |
| subject_service, subject_type               | varchar(80) | N               |
| subject_id                                  | uuid        | N               |
| condition_code                              | varchar(80) | N               |
| scope_definition                            | jsonb       | N               |
| valid_from                                  | date        | N               |
| valid_until                                 | date        | S               |
| verification_reference, evidence_references | jsonb       | N               |
| T, S                                        | Bloques     |                 |

PK sobre condición y versión.

Índice (subject_service, subject_type, subject_id, condition_code, valid_from).

### **20.3. tax_treatment_determination**

| **Columna**                                     | **Tipo**    | **Nulabilidad** |
|-------------------------------------------------|-------------|-----------------|
| determination_id                                | uuid        | N, PK           |
| subject_reference, operation_reference          | jsonb       | N               |
| economic_concepts                               | jsonb       | N               |
| jurisdiction_code                               | varchar(80) | N               |
| determining_dates                               | jsonb       | N               |
| currency                                        | varchar(3)  | N               |
| verified_condition_references, rule_references  | jsonb       | N               |
| result                                          | varchar(20) | N               |
| base_parameters, rate_parameters, rounding_rule | jsonb       | N               |
| exemptions, required_shared_affectations        | jsonb       | N               |
| T, S                                            | Bloques     |                 |

Resultados TAXABLE, EXEMPT, OUT_OF_SCOPE y PENDING.

El producto calcula y conserva el importe económico de su operación.

La determinación no cobra y la ausencia de regla no equivale a exención.

## **21. Acumuladores tributarios compartidos**

### **21.1. shared_tax_accumulator**

| **Columna**                     | **Tipo**       | **Nulabilidad** |
|---------------------------------|----------------|-----------------|
| accumulator_id                  | uuid           | N, PK           |
| subject_service, subject_type   | varchar(80)    | N               |
| subject_id                      | uuid           | N               |
| accumulator_code                | varchar(80)    | N               |
| period_from, period_to          | date           | N               |
| scope_hash                      | char(64)       | N               |
| scope_definition                | jsonb          | N               |
| unit_code                       | varchar(80)    | N               |
| currency                        | varchar(3)     | S               |
| confirmed_value, reserved_value | numeric(30,12) | N               |
| correction_value                | numeric(30,12) | N               |
| limit_value, remaining_value    | numeric(30,12) | N               |
| current_parameters_reference    | jsonb          | N               |
| lock_version                    | bigint         | N               |
| updated_at                      | timestamptz    | N               |

UQ por sujeto, código de acumulador, período y alcance.

Corrección con signo.

Para acumulación aditiva:

Remanente = máximo entre cero y tope − confirmado − reservado − correcciones.

La versión de regla no integra la identidad.

Las magnitudes monetarias respetan centavos y el rango contractual admitido por este acumulador.

### **21.2. shared_tax_affectation**

| **Columna**                                     | **Tipo**       | **Nulabilidad** |
|-------------------------------------------------|----------------|-----------------|
| affectation_id, accumulator_id                  | uuid           | N               |
| origin_service                                  | varchar(80)    | N               |
| origin_tax_effect_id                            | uuid           | N               |
| operation_reference, rule_reference             | jsonb          | N               |
| relevant_base, requested_value, admitted_value  | numeric(30,12) | N               |
| reserved_value, confirmed_value, released_value | numeric(30,12) | N               |
| correction_value                                | numeric(30,12) | N               |
| status                                          | varchar(30)    | N               |
| T, C, S                                         | Bloques        |                 |

PK affectation_id, FK a acumulador.

UQ (accumulator_id, origin_service, origin_tax_effect_id).

Estados RESERVED, CONFIRMED, RELEASED, RESOLUTION_PENDING y CORRECTED.

Confirmar reduce reservado y aumenta confirmado en la misma transacción.

### **21.3. shared_tax_affectation_action**

| **Columna**                          | **Tipo**       | **Nulabilidad** |
|--------------------------------------|----------------|-----------------|
| action_id, affectation_id            | uuid           | N               |
| action                               | varchar(20)    | N               |
| source_service                       | varchar(80)    | N               |
| source_fact_id                       | uuid           | N               |
| source_fact_version                  | bigint         | N               |
| definitive_value                     | numeric(30,12) | S               |
| signed_variation                     | numeric(30,12) | S               |
| basis_reference, evidence_references | jsonb          | N               |
| T, S                                 | Bloques        |                 |

PK action_id, FK a afectación.

UQ (affectation_id, source_service, source_fact_id, source_fact_version).

Acciones CONFIRM_EVENT, RELEASE_CANCELLED y CORRECT.

La reserva no se cuenta otra vez al confirmar. Las reversiones recuperan capacidad solo si la regla lo permite.

La incertidumbre no habilita liberación.

## **22. Requerimientos regulatorios**

### **22.1. reporting_simulated_destination**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| destination_id                          | uuid        | N, PK           |
| code                                    | varchar(80) | N               |
| adapter_code, registered_operation_code | varchar(80) | N               |
| mechanism_definition                    | jsonb       | N               |
| enabled                                 | boolean     | N               |
| T, C                                    | Bloques     |                 |

UQ sobre código.

Los destinos pertenecen al catálogo simulado. Las solicitudes no incorporan direcciones reales arbitrarias.

### **22.2. Definición de requerimiento**

reporting_requirement_version.specific_definition exige:

- Destino simulado identificado.

- Formato y versión.

- Fuentes requeridas y responsables.

- Disparador PERIODIC o EVENT_DRIVEN.

- Regla de período o hecho.

- Vencimiento fijo o desplazamiento desde hecho.

- Unidad, calendario y ajuste.

- Validaciones.

- Aprobaciones especializadas.

- Rectificaciones y parcialidad admitida.

No se habilita sin plazo interpretable.

### **22.3. reporting_configuration_incident**

Columnas N: incident_id uuid PK, origin_service varchar(80), origin_request_id uuid, required_reporting_purpose
varchar(80), original_clock_started_at timestamptz, missing_configuration jsonb, status varchar(20) y bloques T y C.

UQ (origin_service, origin_request_id).

La configuración ausente mantiene incidencia y seguimiento sin inventar cumplimiento.

## **23. Obligaciones y fuentes**

### **23.1. reporting_obligation**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| obligation_id, requirement_id | uuid        | N               |
| requirement_version           | bigint      | N               |
| trigger_type                  | varchar(20) | N               |
| period_from, period_to        | date        | S               |
| trigger_owner_service         | varchar(80) | S               |
| trigger_fact_id               | uuid        | S               |
| occurred_at                   | timestamptz | N               |
| responsible_reference         | jsonb       | N               |
| due_at                        | timestamptz | N               |
| deadline_calculation          | jsonb       | N               |
| priority_code                 | varchar(80) | N               |
| status                        | varchar(30) | N               |
| T, C, S                       | Bloques     |                 |

PK obligation_id.

FK al requerimiento versionado.

UQ parciales:

- (requirement_id, period_from, period_to), para PERIODIC.

- (requirement_id, trigger_owner_service, trigger_fact_id), para EVENT_DRIVEN.

Las variantes son excluyentes. Cambiar versión del requerimiento no duplica la obligación.

### **23.2. reporting_source_dataset**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| dataset_id                     | uuid        | N               |
| dataset_version                | bigint      | N               |
| source_service                 | varchar(80) | N               |
| source_dataset_id              | uuid        | N               |
| cut_at                         | timestamptz | N               |
| source_cut_reference           | jsonb       | N               |
| dataset_reference              | jsonb       | N               |
| completeness                   | varchar(20) | N               |
| missing_data_codes             | jsonb       | N               |
| specialized_approval_reference | jsonb       | S               |
| T, S                           | Bloques     |                 |

PK sobre conjunto y versión.

UQ (source_service, source_dataset_id, dataset_version).

### **23.3. reporting_obligation_source**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| obligation_id           | uuid        | N               |
| source_code             | varchar(80) | N               |
| required_source_service | varchar(80) | N               |
| dataset_id              | uuid        | S               |
| dataset_version         | bigint      | S               |
| status                  | varchar(20) | N               |
| checks                  | jsonb       | N               |

PK sobre obligación y código.

FK a obligación y conjunto versionado.

Estados MISSING, RECEIVED, VERIFIED e INCOMPLETE.

Datos ausentes no se sustituyen por cero. Se comprueba competencia de la fuente y aprobación de su contenido exacto.

## **24. Presentaciones, aprobación y despacho**

### **24.1. regulatory_submission**

Columnas N: submission_id uuid PK, obligation_id uuid FK, current_version bigint, bloque T y lock_version bigint.

UQ sobre obligación.

### **24.2. regulatory_submission_version**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| submission_id                          | uuid        | N               |
| submission_version                     | bigint      | N               |
| format_code                            | varchar(80) | N               |
| format_version                         | bigint      | N               |
| dataset_references, requested_cut      | jsonb       | N               |
| submission_content_reference           | jsonb       | N               |
| validation_results                     | jsonb       | N               |
| specialized_approval_references        | jsonb       | N               |
| emission_approval_reference            | jsonb       | S               |
| preparer_person_id, approver_person_id | uuid        | S               |
| document_reference                     | jsonb       | S               |
| status                                 | varchar(30) | N               |
| T, S                                   | Bloques     |                 |

PK sobre presentación y versión. FK a raíz.

Estados PREPARING, VALIDATION_FAILED, READY_FOR_APPROVAL, APPROVED, DISPATCH_PENDING, SENT, TECHNICALLY_RECEIVED,
ACCEPTED, REJECTED y UNCERTAIN.

APPROVED exige aprobación de emisión y separación de intervinientes.

El contenido aprobado es inmutable.

### **24.3. regulatory_submission_dispatch**

| **Columna**                                | **Tipo**     | **Nulabilidad** |
|--------------------------------------------|--------------|-----------------|
| dispatch_id, submission_id, destination_id | uuid         | N               |
| submission_version                         | bigint       | N               |
| external_submission_reference              | varchar(120) | N               |
| approved_content_hash                      | char(64)     | N               |
| claim_owner                                | uuid         | S               |
| claim_generation                           | bigint       | N               |
| claim_until                                | timestamptz  | S               |
| sent_at                                    | timestamptz  | S               |
| status                                     | varchar(30)  | N               |
| T, C, S                                    | Bloques      |                 |

PK dispatch_id.

FK a versión y destino simulado.

UQ (submission_id, submission_version) y (destination_id, external_submission_reference).

La reclamación se confirma antes de llamar al adaptador. La consulta utiliza la referencia original.

### **24.4. regulatory_submission_result**

| **Columna**                                             | **Tipo**     | **Nulabilidad** |
|---------------------------------------------------------|--------------|-----------------|
| result_id, dispatch_id                                  | uuid         | N               |
| source_service                                          | varchar(80)  | N               |
| external_result_id                                      | varchar(120) | N               |
| source_result_version                                   | bigint       | N               |
| result                                                  | varchar(30)  | N               |
| occurred_at, received_at                                | timestamptz  | N               |
| evidence_references, accepted_scope, pending_complement | jsonb        | N               |
| T, S                                                    | Bloques      |                 |

PK result_id, FK a despacho.

UQ (source_service, external_result_id, source_result_version).

Resultados SENT, TECHNICALLY_RECEIVED, ACCEPTED, REJECTED y UNCERTAIN.

Recepción técnica no equivale a aceptación. El rechazo no extingue la obligación.

Una parcialidad requiere habilitación y complemento pendiente identificado.

### **24.5. regulatory_submission_rectification**

Columnas N: rectification_id uuid PK, submission_id uuid FK, original_version bigint, resulting_version bigint, cause
varchar(4000), corrected_sources jsonb, differences jsonb, approval_references jsonb y bloques T y S.

FK a ambas versiones.

Las fechas reales no se alteran para ocultar tardanza.

## **25. Reserva de información y acceso**

Cada servicio que conserva expedientes reservados incorpora specialized_access_record.

| **Columna**             | **Tipo**     | **Nulabilidad** |
|-------------------------|--------------|-----------------|
| access_id               | uuid         | N, PK           |
| actor_reference         | jsonb        | N               |
| purpose_code            | varchar(80)  | N               |
| object_type             | varchar(80)  | N               |
| object_id               | uuid         | N               |
| accessed_sections       | jsonb        | N               |
| accessed_at             | timestamptz  | N               |
| authorization_reference | jsonb        | N               |
| correlation_id          | varchar(120) | N               |

Los motivos comunicables se conservan separados de antecedentes reservados.

El seguimiento no concede acceso al expediente completo.

Los eventos reservados tienen consumidores expresamente autorizados y no contienen el expediente íntegro.

Las solicitudes al cliente no revelan la existencia de reportes reservados.

## **26. Índices complementarios**

| **Tabla**                           | **Índice**                                                             |
|-------------------------------------|------------------------------------------------------------------------|
| risk_policy_version                 | (status, next_review_at, definition_id, definition_version)            |
| risk_limit                          | (scope_type, scope_identity_hash, risk_type, limit_id)                 |
| risk_exposure_source_update         | (source_position_id, source_version, update_id)                        |
| risk_exposure_component             | (status, valid_from, exposure_component_id)                            |
| risk_capacity_commitment            | (status, valid_until, commitment_id)                                   |
| risk_management_case                | (case_type, status, updated_at, case_id)                               |
| identity_verification               | (party_id, purpose_code, created_at, verification_id)                  |
| beneficial_ownership_determination  | (entity_party_id, cut_at, determination_id)                            |
| compliance_list_check               | (party_id, checked_at, check_id)                                       |
| customer_compliance_profile_version | (next_review_at, profile_id, profile_version)                          |
| investment_investor_profile         | (party_id, valid_until, investor_profile_id, profile_version)          |
| aml_operation_fact_revision         | (occurred_at, fact_id, revision)                                       |
| aml_window_contribution             | (fact_id, fact_revision, window_id)                                    |
| aml_investigation                   | (status, next_action_at, investigation_id)                             |
| fraud_operation_assessment          | (result, valid_until, assessment_id)                                   |
| restriction_version                 | (effective_from, effective_until, restriction_id, restriction_version) |
| restriction_application_component   | (responsible_service, status, application_component_id)                |
| restriction_application_result      | (application_component_id, instruction_version, created_at, result_id) |
| shared_tax_affectation              | (accumulator_id, status, affectation_id)                               |
| reporting_obligation                | (status, due_at, obligation_id)                                        |
| reporting_source_dataset            | (source_service, cut_at, dataset_id, dataset_version)                  |
| regulatory_submission_dispatch      | (status, updated_at, dispatch_id)                                      |
| specialized_access_record           | (object_type, object_id, accessed_at, access_id)                       |

Las FK compuestas incorporan UQ de pertenencia cuando una relación debe conservar el mismo agregado, versión o sujeto.

## **27. Orden de bloqueo y atomicidad**

### **27.1. Riesgos**

1.  Catálogo de ámbitos.

2.  Controles de límites ordenados.

3.  Compromisos ordenados.

4.  Componentes de exposición y representaciones.

5.  Transformaciones y resultados.

La selección de límites y la afectación son una sola transacción. No se admite afectación parcial de controles
obligatorios.

### **27.2. Conocimiento del cliente**

Expediente o identidad de perfil → control específico → versión y evidencias.

Los resultados anteriores no se sobrescriben.

### **27.3. Prevención de lavado**

Hecho → ventanas afectadas ordenadas → contribuciones → alerta y revisión.

Una corrección bloquea todas las ventanas que debe retirar o modificar.

### **27.4. Prevención de fraude**

Operación evaluada → investigación → resolución → actuaciones requeridas.

La resolución no presenta reparaciones pendientes como ejecutadas.

### **27.5. Restricciones**

Catálogo de alcances → controles de ámbito ordenados → medidas ordenadas → planes, componentes y preparaciones.

Registro, modificación, levantamiento y preparación mantienen este orden.

### **27.6. Tributos**

Acumuladores ordenados → afectaciones → acciones.

Comprobar y reservar, transformar reserva en consumo y aplicar corrección son transacciones locales indivisibles.

### **27.7. Información regulatoria**

Obligación → presentación → versión → despacho → resultados.

El envío comprueba aprobación y huella antes de reclamar trabajo.

Ninguna llamada externa mantiene bloqueos PostgreSQL.

## **28. Eventos y recuperación**

| **Contrato** | **Tipo**                               | **Tema**                              |
|--------------|----------------------------------------|---------------------------------------|
| CI-11-045    | RiskPolicyOrExposureChanged            | banking.risk.events                   |
| CI-11-046    | CustomerDueDiligenceResultChanged      | banking.customer-due-diligence.events |
| CI-11-047    | AmlActionRecorded                      | banking.aml.events                    |
| CI-11-048    | FraudAssessmentOrResolutionChanged     | banking.fraud.events                  |
| CI-11-049    | RestrictionOrApplicationChanged        | banking.restriction.events            |
| CI-11-050    | TaxTreatmentOrSharedAffectationChanged | banking.tax-policy.events             |
| CI-11-051    | ReportingObligationOrSubmissionChanged | banking.regulatory-reporting.events   |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado definido por cada variante.

Los eventos se registran junto con los efectos locales.

Una transformación no duplica exposición. Una resolución no demuestra reparación. Un levantamiento no demuestra
liberación integral.

Los trabajos conservan identidades de compromiso, transformación, control, hecho, ventana, medida, preparación,
afectación, obligación y despacho.

No se liberan compromisos, preparaciones ni afectaciones inciertos por timeout.

## **29. Migraciones, conservación y seguridad**

Las migraciones utilizan Flyway por servicio.

Las referencias circulares de raíz y versión se implementan como diferibles.

Las restricciones temporales utilizan el soporte PostgreSQL correspondiente.

Las versiones utilizadas por decisiones, investigaciones, cálculos o presentaciones son inmutables.

No se eliminan mediante cascadas evidencias utilizadas, compromisos, contribuciones, resoluciones o resultados externos.

Redis no conserva la única copia de capacidad, medidas, acumuladores o decisiones.

Se separan permisos para configurar, verificar, analizar, decidir, comprometer, investigar, imponer medidas, aplicar
componentes y preparar, aprobar o enviar información.

Después de restaurar se pausan liberaciones y despachos y se reconcilian los resultados operativos y externos antes de
reanudar.

## **30. Correspondencia con contratos técnicos**

| **Contratos**                  | **Persistencia**                                            |
|--------------------------------|-------------------------------------------------------------|
| CI-11-001 y CI-11-002          | Políticas, propuestas, revisiones y límites.                |
| CI-11-003 a CI-11-005          | Fuentes, exposición, grupos y representaciones.             |
| CI-11-006 y CI-11-007          | Compromisos, afectaciones, transformaciones y liberaciones. |
| CI-11-008 a CI-11-010          | Evaluaciones, indicadores, casos y actuaciones de Riesgos.  |
| CI-11-011                      | Verificaciones de identidad y atributos.                    |
| CI-11-012                      | Beneficiarios finales, relaciones y cadenas.                |
| CI-11-013                      | Listas, coincidencias y resoluciones.                       |
| CI-11-014 a CI-11-016          | Perfiles, adecuación y consulta autorizada.                 |
| CI-11-017 y CI-11-018          | Escenarios, hechos, ventanas y contribuciones.              |
| CI-11-019 a CI-11-021          | Investigaciones y reportes especializados de lavado.        |
| CI-11-022 y CI-11-023          | Controles y evaluaciones de fraude.                         |
| CI-11-024 y CI-11-025          | Investigaciones, resoluciones y actuaciones requeridas.     |
| CI-11-026 y CI-11-027          | Medidas, alcance y comprobación informativa.                |
| CI-11-028 y CI-11-029          | Planes, distribución y resultados de aplicación.            |
| CI-11-030 y CI-11-031          | Cambios, levantamientos y seguimiento.                      |
| CI-11-032 a CI-11-034          | Reglas tributarias, condiciones y determinaciones.          |
| CI-11-035 a CI-11-037          | Acumuladores, afectaciones y acciones.                      |
| CI-11-038 y CI-11-039          | Requerimientos y obligaciones informativas.                 |
| CI-11-040 y CI-11-041          | Fuentes, preparación y aprobación.                          |
| CI-11-042 a CI-11-044          | Despacho, resultados, rectificación y consulta.             |
| CI-11-045 a CI-11-051          | Outbox e historia de agregados.                             |
| Preparaciones de Restricciones | Catálogo, ámbitos, bases y resolución de intentos.          |

## **31. Escenarios de verificación de implementación**

| **Escenario**                                   | **Resultado exigido**                                |
|-------------------------------------------------|------------------------------------------------------|
| Aprobación 500 transformada en financiación 500 | Mantiene consumo 500 sin duplicación.                |
| Límites de persona y grupo                      | Los afecta conjuntamente.                            |
| Cambio concurrente de pertenencia               | No omite límites exigibles.                          |
| Agregado recibido después                       | Reconoce componentes ya incorporados.                |
| Límite reducido                                 | Conserva exposición y registra exceso.               |
| Cambio interno confirmado                       | Transforma exposición sin salida externa ficticia.   |
| Fuente ausente                                  | No interpreta exposición como cero.                  |
| Identidad incompleta                            | No informa verificación favorable.                   |
| Cadena circular                                 | Conserva resolución pendiente.                       |
| Coincidencia de nombre                          | No confirma identidad automáticamente.               |
| Inversión incompatible                          | No se habilita por excepción comercial.              |
| Hecho de lavado duplicado                       | Una contribución activa por ventana.                 |
| Corrección o llegada tardía                     | Reevalúa las ventanas afectadas.                     |
| Alerta                                          | No inmoviliza ni confirma ilicitud.                  |
| Fraude pendiente                                | No devuelve ALLOW.                                   |
| Investigación resuelta                          | Distingue decisión y reparación.                     |
| Medida total 100 en dos cuentas                 | Distribuye 100, no 200.                              |
| Solo existen 60 afectables                      | Conserva insuficiencia 40.                           |
| Medida crea alcance concurrente                 | La preparación considera el cambio.                  |
| Intento ya preparado                            | Solicita resolverlo sin aborto ficticio.             |
| Levantamiento de una causa                      | Mantiene las restantes.                              |
| Dos productos usan beneficio compartido         | No consumen el mismo remanente.                      |
| Confirmación tributaria                         | Transforma reserva sin duplicar consumo.             |
| Cambia versión de regla                         | No reinicia el acumulador.                           |
| Falta regla tributaria                          | No presume exención.                                 |
| Requerimiento sin plazo interpretable           | No se habilita.                                      |
| Contenido aprobado cambia                       | Exige otra versión y aprobación.                     |
| Recepción técnica                               | No se presenta como aceptación.                      |
| Seguimiento general                             | No revela expediente reservado.                      |
| Respuesta perdida o restauración                | Recupera identidades originales sin repetir efectos. |

Las restricciones, transformaciones, acumuladores, bloqueos y transacciones se verifican durante la implementación con
PostgreSQL real y escenarios concurrentes.
