# **Diseño de persistencia — Módulo 6. Financiación**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Financiación.

| **Servicio**               | **Responsabilidad**                                                                                   |
|----------------------------|-------------------------------------------------------------------------------------------------------|
| Evaluación crediticia      | Antecedentes, análisis, decisiones, aprobaciones y aplicación de su alcance.                          |
| Préstamos y líneas         | Constitución, cálculos, desembolsos, capital, límites, deuda e imputaciones.                          |
| Garantías                  | Admisibilidad, constitución, cobertura, asignación, continuidad, liberación y ejecución de respaldos. |
| Cobranzas y recuperaciones | Gestión de obligaciones vencidas, acuerdos y coordinación de recuperaciones.                          |

Comprende los contratos CI-06-001 a CI-06-043.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases de servicios.

Aprobación, contrato, constitución, entrega de dinero y reconocimiento de capital conservan identidades y resultados
diferentes.

Fondos administra los movimientos de cuentas y la utilización inmediata del descubierto.

La deuda de tarjetas pertenece a Cuenta de tarjeta. Evaluarla o gestionarla en cobranza no crea otra deuda en este
módulo.

## **2. Convenciones físicas**

### **2.1. Notación**

- **N:** columna obligatoria.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Cuando una fila agrupa varios nombres de columnas, cada columna tiene individualmente el tipo y la nulabilidad
indicados.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en orden. Las PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                | **PostgreSQL** | **Restricción**                    |
|-------------------------|----------------|------------------------------------|
| Identificador           | uuid           | Estable y no reutilizable.         |
| Versión                 | bigint         | Positiva.                          |
| Control de concurrencia | bigint         | No negativo.                       |
| Secuencia asignada      | bigint         | Positiva.                          |
| Código                  | varchar(80)    | No vacío y registrado.             |
| Referencia visible      | varchar(120)   | No vacía.                          |
| Fundamento              | varchar(4000)  | No vacío cuando está presente.     |
| Moneda                  | varchar(3)     | ARS o USD.                         |
| Importe monetario       | numeric(24,2)  | No negativo.                       |
| Variación monetaria     | numeric(24,2)  | Admite signo cuando se indica.     |
| Valor de cálculo        | numeric(30,12) | Según su significado.              |
| Tasa                    | numeric(24,12) | Según la definición contractual.   |
| Hash                    | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Instante                | timestamptz    | Intercambio en UTC.                |
| Fecha                   | date           | Fecha bancaria.                    |
| Contenido estructurado  | jsonb          | Esquema registrado y versionado.   |

Los importes ejecutables son positivos. Límites, acumulados y remanentes admiten cero.

La precisión y escala se comprueban antes de persistir.

Los cálculos intermedios utilizan precisión decimal suficiente y conservan al menos doce decimales. Los resultados
monetarios utilizan HALF_UP a centavos.

Los calendarios se interpretan en America/Argentina/Buenos_Aires.

### **2.3. Bloques de columnas**

Los bloques se incorporan físicamente a las tablas que los mencionan.

**Bloque T — Creación**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| created_action_id | uuid        | N               |
| created_at        | timestamptz | N               |

La actuación referencia service_action(action_id).

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

Las versiones son positivas.

### **2.4. Tablas comunes**

Los cuatro servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Las decisiones de ejecución monetaria pertenecen a Fondos y se conservan mediante referencias. No se duplican como
decisiones locales de este módulo.

## **3. Contenido estructurado y coordinación**

### **3.1. Esquemas**

Cada JSONB utiliza una columna explícita de versión o un envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                | **Contenido**                                                         |
|----------------------------|-----------------------------------------------------------------------|
| RemoteReference            | Propietario, tipo, identidad y versión cuando corresponde.            |
| EvidenceReferences         | Hechos y evidencias identificados.                                    |
| PendingComponents          | Componente, responsable, motivo y seguimiento.                        |
| CreditAntecedent           | Datos declarados, comprobados o estimados y su procedencia.           |
| CreditPolicyInputs         | Política, metodología, parámetros y fuentes.                          |
| CreditEvaluatedScope       | Producto, personas, importe, plazo y modalidades evaluados.           |
| CreditFinancialTerms       | Condiciones financieras aprobadas y sus versiones.                    |
| FinancingTerms             | Condiciones contractuales aceptadas.                                  |
| FinancingCalculationInputs | Tasa, amortización, calendario, entregas, cargos, seguros y tributos. |
| RateDefinition             | Tipo, período, base, capitalización y modalidad fija o variable.      |
| FinancingCondition         | Requisito, etapa, propietario y posibilidad de dispensa.              |
| PaymentAllocationRules     | Orden de imputación y tratamientos admitidos.                         |
| CalendarCalculation        | Calendario, entradas, regla y resultado.                              |
| CollateralScope            | Alcance de respaldo y determinación de su impacto.                    |
| CollectionAgreementTerms   | Conceptos, calendario, quitas y condiciones.                          |
| FinancingPosition          | Deuda, compromisos, pagos, mora, garantías y exposición.              |

No se admiten scripts, SQL, campos desconocidos ni estructuras libres.

### **3.2. Pasos remotos**

Las siguientes tablas utilizan una estructura física común:

| **Tabla**                        | **Propietario N**          | **FK local**               |
|----------------------------------|----------------------------|----------------------------|
| credit_exposure_coordination     | approval_id uuid           | credit_approval            |
| financing_constitution_component | financing_id uuid          | financing                  |
| financing_remote_step            | financing_id uuid          | financing                  |
| cheque_discount_coordination     | discount_operation_id uuid | cheque_discount_operation  |
| collateral_release_step          | release_process_id uuid    | collateral_release_process |
| collection_recovery_step         | recovery_id uuid           | collection_recovery        |

Columnas adicionales de cada tabla:

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| step_id                          | uuid        | N, PK           |
| component_code                   | varchar(80) | N               |
| responsible_service              | varchar(80) | N               |
| remote_action_id                 | uuid        | N               |
| economic_effect_id               | uuid        | S               |
| previous_commitment_reference    | jsonb       | S               |
| requested_content                | jsonb       | N               |
| status                           | varchar(30) | N               |
| result_reference, result_payload | jsonb       | S               |
| evidence_references              | jsonb       | N               |
| last_checked_at, next_check_at   | timestamptz | S               |
| T, C, S                          | Bloques     |                 |

Cada tabla declara:

- UQ (propietario, component_code).

- UQ (responsible_service, remote_action_id).

- Índice (status, next_check_at, step_id).

- Índice (economic_effect_id), donde exista efecto.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de la llamada. El resultado se incorpora junto con historia, actuación, inbox, trabajos y
outbox aplicables.

## **4. Evaluación crediticia**

### **4.1. credit_assessment**

| **Columna**                   | **Tipo**      | **Nulabilidad** |
|-------------------------------|---------------|-----------------|
| assessment_id                 | uuid          | N, PK           |
| origin_service                | varchar(80)   | N               |
| origin_reference              | varchar(120)  | N               |
| application_reference         | jsonb         | N               |
| product_service               | varchar(80)   | N               |
| product_id                    | uuid          | N               |
| product_version               | bigint        | N               |
| financing_purpose             | varchar(80)   | N               |
| requested_amount              | numeric(24,2) | N               |
| currency                      | varchar(3)    | N               |
| term_quantity                 | integer       | N               |
| term_unit                     | varchar(10)   | N               |
| proposed_conditions           | jsonb         | N               |
| offered_collateral_references | jsonb         | N               |
| status                        | varchar(30)   | N               |
| received_at                   | timestamptz   | N               |
| T, C                          | Bloques       |                 |

UQ (origin_service, origin_reference).

Importe y cantidad positivos. Unidad DAYS, MONTHS o YEARS.

Estados: PREPARING, AWAITING_INFORMATION, READY_FOR_ANALYSIS, ANALYSED, DECISION_PENDING, DECIDED y REQUIRES_REVIEW.

Índices:

- (status, updated_at, assessment_id).

- (product_service, product_id, received_at, assessment_id).

### **4.2. credit_assessment_party**

| **Columna**                                          | **Tipo**    | **Nulabilidad** |
|------------------------------------------------------|-------------|-----------------|
| assessment_id, party_id                              | uuid        | N               |
| role                                                 | varchar(20) | N               |
| obligation_basis_reference, representation_reference | jsonb       | S               |

PK (assessment_id, party_id, role) y FK a evaluación.

Roles: BORROWER, CO_BORROWER, GUARANTOR y PAYER.

Índice inverso (party_id, assessment_id).

La evaluación exige al menos un prestatario. Una relación declarada no acredita solidaridad, garantía o representación.

### **4.3. credit_antecedent**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| antecedent_id, assessment_id       | uuid        | N               |
| antecedent_type, source_type       | varchar(80) | N               |
| source_service                     | varchar(80) | S               |
| source_object_id                   | uuid        | S               |
| source_version                     | bigint      | S               |
| previous_antecedent_id             | uuid        | S               |
| observed_on                        | date        | N               |
| valid_until                        | timestamptz | S               |
| status                             | varchar(20) | N               |
| evidence_references, typed_content | jsonb       | N               |
| T, S                               | Bloques     |                 |

PK antecedent_id.

FK a evaluación y antecedente anterior.

Estados: DECLARED, VERIFIED, OBSERVED y UNAVAILABLE.

La fuente identificada aparece con propietario e identidad conjuntos. La versión solo existe cuando la fuente la
utiliza.

Índices:

- (assessment_id, antecedent_type, observed_on, antecedent_id).

- (source_service, source_object_id, source_version), donde exista identidad de fuente.

Las correcciones agregan antecedentes vinculados. No sobrescriben lo utilizado por análisis anteriores.

Una fuente sin respuesta no demuestra ausencia de deuda.

## **5. Análisis crediticio**

### **5.1. credit_analysis**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| analysis_id, assessment_id                   | uuid        | N               |
| evaluated_assessment_version                 | bigint      | N               |
| input_hash                                   | char(64)    | N               |
| risk_policy_reference, methodology_reference | jsonb       | N               |
| proposed_conditions, source_references       | jsonb       | N               |
| conclusion                                   | varchar(20) | N               |
| analysed_at                                  | timestamptz | N               |
| T, S                                         | Bloques     |                 |

PK analysis_id y FK a evaluación.

Conclusiones FAVOURABLE, UNFAVOURABLE e INCOMPLETE.

UQ (assessment_id, analysis_id).

Índice (assessment_id, analysed_at, analysis_id).

### **5.2. credit_analysis_indicator**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| analysis_id                              | uuid           | N               |
| indicator_code, unit_code                | varchar(80)    | N               |
| value                                    | numeric(30,12) | S               |
| completeness                             | varchar(20)    | N               |
| methodology_reference, source_references | jsonb          | N               |
| missing_data_codes                       | jsonb          | N               |

PK (analysis_id, indicator_code) y FK a análisis.

Completitud COMPLETE o INCOMPLETE.

COMPLETE exige valor. INCOMPLETE conserva los datos faltantes sin sustituirlos por cero.

### **5.3. credit_analysis_scenario**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| analysis_id                              | uuid        | N               |
| scenario_code                            | varchar(80) | N               |
| versioned_assumptions, indicator_results | jsonb       | N               |
| conclusion                               | varchar(20) | N               |

PK (analysis_id, scenario_code) y FK a análisis.

Los análisis utilizados por decisiones son inmutables. Las metodologías y umbrales proceden de Riesgos.

## **6. Decisiones y alcance aprobado**

### **6.1. credit_decision**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| decision_id, assessment_id     | uuid        | N               |
| current_version, event_version | bigint      | N               |
| lock_version                   | bigint      | N               |
| updated_at                     | timestamptz | N               |
| T                              | Bloque      |                 |

PK decision_id, FK a evaluación y UQ sobre assessment_id.

La raíz referencia su versión actual mediante FK compuesta diferible.

### **6.2. credit_decision_version**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| decision_id                            | uuid        | N               |
| decision_version                       | bigint      | N               |
| analysis_id                            | uuid        | N               |
| conclusion                             | varchar(30) | N               |
| evaluated_scope, conditions, reasons   | jsonb       | N               |
| authorization_type                     | varchar(30) | N               |
| approval_reference                     | jsonb       | S               |
| preparer_person_id, approver_person_id | uuid        | S               |
| automated_policy_reference             | jsonb       | S               |
| decided_at                             | timestamptz | N               |
| T, S                                   | Bloques     |                 |

PK (decision_id, decision_version).

FK a decisión y análisis.

Conclusiones APPROVED, CONDITIONALLY_APPROVED y REJECTED.

Autorizaciones:

- HUMAN_APPROVAL exige aprobación e intervinientes distintos.

- AUTOMATED_POLICY exige política versionada habilitada y no contiene aprobación humana.

Se comprueba que el análisis pertenece a la evaluación de la decisión.

### **6.3. credit_approved_scope**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| decision_id                                     | uuid          | N               |
| decision_version                                | bigint        | N               |
| product_service                                 | varchar(80)   | N               |
| product_id                                      | uuid          | N               |
| product_version                                 | bigint        | N               |
| admitted_parties                                | jsonb         | N               |
| currency                                        | varchar(3)    | N               |
| maximum_amount                                  | numeric(24,2) | N               |
| maximum_term_quantity                           | integer       | N               |
| maximum_term_unit                               | varchar(10)   | N               |
| admitted_modalities, financial_terms_references | jsonb         | N               |
| required_collateral                             | jsonb         | N               |

PK y FK (decision_id, decision_version) a decisión versionada.

Solo existe para decisiones favorables. Importe y plazo positivos.

Las personas, modalidades y condiciones se conservan con el alcance aprobado.

### **6.4. credit_decision_condition**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| decision_id                         | uuid        | N               |
| decision_version                    | bigint      | N               |
| condition_id                        | uuid        | N               |
| condition_code, responsible_service | varchar(80) | N               |
| stage                               | varchar(30) | N               |
| waivable                            | boolean     | N               |
| source_reference                    | jsonb       | S               |
| requirement_definition              | jsonb       | N               |

PK (decision_id, decision_version, condition_id) y FK a versión.

Etapas: BEFORE_CONTRACT, BEFORE_CONSTITUTION, BEFORE_DISBURSEMENT y BEFORE_UTILIZATION.

## **7. Aprobaciones y aplicación**

### **7.1. credit_approval**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| approval_id               | uuid        | N, PK           |
| decision_version          | bigint      | N               |
| approved_at, usable_until | timestamptz | N               |
| status                    | varchar(30) | N               |
| exposure_references       | jsonb       | N               |
| lock_version              | bigint      | N               |
| updated_at                | timestamptz | N               |

approval_id es la misma identidad que decision_id.

FK compuesta a decisión favorable.

Estados: EXPOSURE_PENDING, AVAILABLE, APPLIED, WITHDRAWN, EXPIRED y REQUIRES_REVIEW.

La vigencia se fija al aprobar y no supera treinta días corridos ni los antecedentes indispensables.

Índice (status, usable_until, approval_id).

Una exposición pendiente impide AVAILABLE.

### **7.2. credit_approval_application**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| application_id, approval_id, constitution_id    | uuid          | N               |
| approval_version                                | bigint        | N               |
| target_service                                  | varchar(80)   | N               |
| target_product_id                               | uuid          | N               |
| target_product_version                          | bigint        | N               |
| amount                                          | numeric(24,2) | N               |
| currency                                        | varchar(3)    | N               |
| satisfied_conditions, risk_commitment_reference | jsonb         | N               |
| status                                          | varchar(30)   | N               |
| result_reference                                | jsonb         | S               |
| T, C, S                                         | Bloques       |                 |

PK application_id.

FK a aprobación y decisión versionada correspondiente.

UQ (approval_id, constitution_id).

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (approval_id, status, application_id).

Aplicación, retiro, revisión y vencimiento bloquean primero la aprobación.

Una constitución incompatible no puede utilizar el mismo alcance. La aplicación no inventa un producto constituido ni
dinero entregado.

### **7.3. credit_approval_review**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| review_id, approval_id                           | uuid        | N               |
| previous_decision_version                        | bigint      | N               |
| resulting_decision_version                       | bigint      | S               |
| action                                           | varchar(20) | N               |
| verification_references, authorization_reference | jsonb       | N               |
| exposure_treatment, pending_components           | jsonb       | N               |
| T, S                                             | Bloques     |                 |

PK review_id, FK a aprobación y versiones de decisión.

Acciones REVIEW, REPLACE, WITHDRAW y EXPIRE.

REPLACE exige versión resultante.

La exposición anterior se conserva identificada durante sustituciones. La incertidumbre contractual o de constitución
impide liberar por presunción.

## **8. Préstamos y líneas: financiación**

### **8.1. financing**

| **Columna**                    | **Tipo**     | **Nulabilidad** |
|--------------------------------|--------------|-----------------|
| financing_id                   | uuid         | N, PK           |
| financing_reference            | varchar(120) | N               |
| constitution_id                | uuid         | N               |
| product_service                | varchar(80)  | N               |
| product_id                     | uuid         | N               |
| product_version                | bigint       | N               |
| contract_service               | varchar(80)  | N               |
| contract_id                    | uuid         | N               |
| contract_version               | bigint       | N               |
| approval_service               | varchar(80)  | N               |
| approval_id                    | uuid         | N               |
| approval_version               | bigint       | N               |
| approval_application_reference | jsonb        | S               |
| financing_type                 | varchar(30)  | N               |
| currency                       | varchar(3)   | N               |
| terms_reference                | jsonb        | N               |
| calculation_id                 | uuid         | S               |
| calculation_version            | bigint       | S               |
| overdraft_account_id           | uuid         | S               |
| status                         | varchar(30)  | N               |
| debt_version                   | bigint       | N               |
| uses_ceased                    | boolean      | N               |
| T, C                           | Bloques      |                 |

UQ sobre referencia visible y constitución.

Modalidades:

AMORTIZING_LOAN, BULLET_LOAN, REVOLVING_LINE, NON_REVOLVING_LINE, OVERDRAFT y CHEQUE_DISCOUNT.

Estados: CONSTITUTING, CONSTITUTED, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

Cálculo y versión aparecen conjuntamente, con FK al cálculo local.

OVERDRAFT exige cuenta. Las demás modalidades no la utilizan como vínculo de descubierto.

El capital inicial es cero. La mora no se representa mediante el estado del producto.

Índices:

- (status, updated_at, financing_id).

- (approval_service, approval_id, financing_id).

- (overdraft_account_id), donde exista cuenta.

### **8.2. financing_party**

| **Columna**                                          | **Tipo**    | **Nulabilidad** |
|------------------------------------------------------|-------------|-----------------|
| financing_id, party_id                               | uuid        | N               |
| role                                                 | varchar(20) | N               |
| obligation_basis_reference, representation_reference | jsonb       | S               |

PK (financing_id, party_id, role) y FK a financiación.

Roles BORROWER, CO_BORROWER, GUARANTOR y PAYER.

Índice inverso (party_id, financing_id).

### **8.3. financing_condition**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| condition_id, financing_id          | uuid        | N               |
| condition_code, responsible_service | varchar(80) | N               |
| stage                               | varchar(30) | N               |
| waivable                            | boolean     | N               |
| source_reference                    | jsonb       | S               |
| status                              | varchar(20) | N               |
| current_result_id                   | uuid        | S               |
| T, C                                | Bloques     |                 |

PK condition_id, FK a financiación.

Estados PENDING, SATISFIED, FAILED y WAIVED. Etapas del apartado 6.4.

### **8.4. financing_condition_result**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| result_id, condition_id   | uuid        | N               |
| owner_service, fact_type  | varchar(80) | N               |
| fact_id                   | uuid        | N               |
| fact_version              | bigint      | N               |
| status                    | varchar(20) | N               |
| occurred_at               | timestamptz | N               |
| evidence_references       | jsonb       | N               |
| waiver_approval_reference | jsonb       | S               |
| T, S                      | Bloques     |                 |

PK result_id, FK a condición.

UQ (condition_id, owner_service, fact_type, fact_id, fact_version).

WAIVED exige condición dispensable y aprobación competente. La condición referencia un resultado propio mediante FK
compuesta diferible.

Índice de condiciones (financing_id, stage, status, condition_id).

## **9. Cálculos financieros**

### **9.1. financing_calculation**

| **Columna**     | **Tipo** | **Nulabilidad** |
|-----------------|----------|-----------------|
| calculation_id  | uuid     | N, PK           |
| current_version | bigint   | N               |
| T               | Bloque   |                 |
| lock_version    | bigint   | N               |

### **9.2. financing_calculation_version**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| calculation_id                    | uuid          | N               |
| calculation_version               | bigint        | N               |
| financing_type                    | varchar(30)   | N               |
| principal_amount                  | numeric(24,2) | N               |
| currency                          | varchar(3)    | N               |
| amortization_system               | varchar(10)   | S               |
| complete_inputs                   | jsonb         | N               |
| calendar_reference                | jsonb         | S               |
| methodology_reference             | jsonb         | N               |
| algorithm_code                    | varchar(80)   | N               |
| algorithm_version                 | bigint        | N               |
| assumptions, rounding_adjustments | jsonb         | N               |
| valid_until                       | timestamptz   | S               |
| T, S                              | Bloques       |                 |

PK (calculation_id, calculation_version) y FK a raíz.

Sistemas FRENCH, GERMAN y BULLET según producto.

La raíz referencia su versión actual mediante FK diferible.

Una versión utilizada por propuesta, contrato o ejecución es inmutable.

### **9.3. financing_calculation_rate**

| **Columna**                                       | **Tipo**       | **Nulabilidad** |
|---------------------------------------------------|----------------|-----------------|
| calculation_id                                    | uuid           | N               |
| calculation_version                               | bigint         | N               |
| rate_code                                         | varchar(80)    | N               |
| mode, rate_class                                  | varchar(20)    | N               |
| period_code, day_count_code                       | varchar(80)    | N               |
| capitalization_code                               | varchar(80)    | S               |
| fixed_value                                       | numeric(24,12) | S               |
| source_reference                                  | jsonb          | S               |
| margin                                            | numeric(24,12) | S               |
| revision_rule_code, unavailability_treatment_code | varchar(80)    | S               |
| conversion_detail                                 | jsonb          | N               |

PK (calculation_id, calculation_version, rate_code) y FK al cálculo.

Clases NOMINAL y EFFECTIVE.

FIXED exige valor y no contiene parámetros variables.

VARIABLE exige fuente, margen, revisión y tratamiento de indisponibilidad; no contiene valor fijo.

## **10. Cronogramas, flujos y CFT**

### **10.1. financing_schedule_version**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| schedule_id         | uuid          | N, PK           |
| financing_id        | uuid          | S               |
| calculation_id      | uuid          | N               |
| calculation_version | bigint        | N               |
| schedule_version    | bigint        | N               |
| principal_amount    | numeric(24,2) | N               |
| basis_reference     | jsonb         | N               |
| application_status  | varchar(20)   | N               |
| T, S                | Bloques       |                 |

FK a cálculo y financiación cuando exista.

Estados PROPOSED, APPLIED y SUPERSEDED.

UQ (calculation_id, calculation_version, schedule_version).

Índice (financing_id, application_status, schedule_id).

### **10.2. financing_installment**

| **Columna**                                      | **Tipo**      | **Nulabilidad** |
|--------------------------------------------------|---------------|-----------------|
| schedule_id                                      | uuid          | N               |
| installment_index                                | bigint        | N               |
| contractual_date, adjusted_due_date              | date          | N               |
| economic_period_from, economic_period_to         | date          | N               |
| opening_principal, amortization, interest        | numeric(24,2) | N               |
| fees, taxes, insurance, total, closing_principal | numeric(24,2) | N               |

PK (schedule_id, installment_index) y FK a cronograma.

Índice positivo y período válido.

Total = amortización + interés + cargos + tributos + seguros.

Capital final = inicial − amortización.

La suma de amortizaciones coincide con el capital del cronograma. La última absorbe el residuo sin aumentar capital.

La secuencia conserva continuidad entre capital final e inicial de cuotas sucesivas.

### **10.3. financing_cash_flow**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| calculation_id      | uuid          | N               |
| calculation_version | bigint        | N               |
| position            | integer       | N               |
| flow_date           | date          | N               |
| direction           | varchar(10)   | N               |
| amount              | numeric(24,2) | N               |
| currency            | varchar(3)    | N               |
| concept_code        | varchar(80)   | N               |
| source_reference    | jsonb         | S               |

PK (calculation_id, calculation_version, position) y FK a cálculo.

Sentido RECEIVED o PAID desde el prestatario. Importe y posición positivos.

### **10.4. financing_cft_result**

| **Columna**                     | **Tipo**       | **Nulabilidad** |
|---------------------------------|----------------|-----------------|
| calculation_id                  | uuid           | N               |
| calculation_version             | bigint         | N               |
| status                          | varchar(60)    | N               |
| method_code                     | varchar(80)    | N               |
| method_version                  | bigint         | N               |
| annual_effective_rate           | numeric(24,12) | S               |
| equation_residual               | numeric(30,12) | S               |
| converged                       | boolean        | N               |
| assumptions, missing_data_codes | jsonb          | N               |

PK y FK del cálculo versionado.

Estados:

DETERMINED, INCOMPLETE_INPUT, NO_UNIQUE_RATE y NOT_APPLICABLE_TO_UNSPECIFIED_UTILIZATIONS.

Solo DETERMINED contiene tasa y exige convergencia.

Las retenciones reducen el flujo neto inicial y no se duplican como otro pago.

No se publica un CFT definitivo con costos obligatorios desconocidos.

## **11. Desembolsos y capital**

### **11.1. financing_disbursement**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| disbursement_id, financing_id             | uuid          | N               |
| utilization_id                            | uuid          | S               |
| gross_amount, retained_amount, net_amount | numeric(24,2) | N               |
| currency                                  | varchar(3)    | N               |
| authorized_destination                    | jsonb         | N               |
| accepted_date                             | date          | N               |
| calculation_id                            | uuid          | N               |
| calculation_version                       | bigint        | N               |
| funds_service                             | varchar(80)   | N               |
| remote_action_id                          | uuid          | N               |
| delivery_status                           | varchar(30)   | N               |
| capital_status                            | varchar(20)   | N               |
| delivery_reference                        | jsonb         | S               |
| T, C, S                                   | Bloques       |                 |

PK disbursement_id.

FK a financiación, utilización y cálculo.

UQ (funds_service, remote_action_id).

Bruto = retenido + neto. Neto positivo para entrega ejecutable.

Entrega: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Capital: NOT_RECOGNIZED o RECOGNIZED.

Índice (financing_id, delivery_status, disbursement_id).

### **11.2. financing_disbursement_retention**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| disbursement_id                        | uuid          | N               |
| position                               | integer       | N               |
| concept_code                           | varchar(80)   | N               |
| amount                                 | numeric(24,2) | N               |
| beneficiary_reference, basis_reference | jsonb         | N               |
| application_reference                  | jsonb         | S               |
| application_status                     | varchar(20)   | N               |

PK (disbursement_id, position) y FK a desembolso.

Estados PENDING, APPLIED, UNCERTAIN y REQUIRES_REVIEW.

La suma coincide con el total retenido. Retener no acredita aplicación del concepto.

### **11.3. financing_capital_recognition**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| recognition_id, financing_id | uuid          | N               |
| source_service, source_type  | varchar(80)   | N               |
| source_id                    | uuid          | N               |
| disbursement_id              | uuid          | S               |
| amount                       | numeric(24,2) | N               |
| effective_at                 | timestamptz   | N               |
| evidence_references          | jsonb         | N               |
| T                            | Bloque        |                 |

PK recognition_id.

FK a financiación y desembolso.

UQ (source_service, source_type, source_id) y UQ parcial sobre disbursement_id.

Una entrega rechazada sin efectos no crea capital.

La entrega incierta conserva seguimiento. La confirmada se reconoce una sola vez junto con deuda, historia y outbox.

## **12. Capacidad, límites y utilizaciones**

### **12.1. financing_capacity**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| financing_id                        | uuid          | N, PK           |
| authorized_limit, applied_limit     | numeric(24,2) | N               |
| principal_used, pending_commitments | numeric(24,2) | N               |
| cumulative_non_reusable_usage       | numeric(24,2) | N               |
| available_capacity, excess          | numeric(24,2) | N               |
| limit_version                       | bigint        | N               |
| lock_version                        | bigint        | N               |
| updated_at                          | timestamptz   | N               |

FK a financiación.

En renovables:

Capacidad = máximo entre cero y límite aplicado − capital utilizado − compromisos.

En no renovables se sustituye capital utilizado por consumo acumulado no reutilizable.

El exceso se conserva separadamente y no se borra reduciendo acumulados.

### **12.2. financing_limit_change**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| limit_change_id, financing_id          | uuid          | N               |
| limit_version                          | bigint        | N               |
| new_limit                              | numeric(24,2) | N               |
| currency                               | varchar(3)    | N               |
| valid_from                             | timestamptz   | N               |
| valid_until                            | timestamptz   | S               |
| approval_reference, exposure_reference | jsonb         | N               |
| contract_amendment_reference           | jsonb         | S               |
| remote_application_reference           | jsonb         | S               |
| status                                 | varchar(30)   | N               |
| T, C, S                                | Bloques       |                 |

PK limit_change_id, FK a financiación.

UQ (financing_id, limit_version).

Estados PENDING, APPLIED, SUPERSEDED, REJECTED y REQUIRES_REVIEW.

Una versión antigua no sustituye una posterior. Un aumento no es utilizable antes de confirmar dependencias.

### **12.3. financing_utilization**

| **Columna**                                 | **Tipo**      | **Nulabilidad** |
|---------------------------------------------|---------------|-----------------|
| utilization_id, financing_id                | uuid          | N               |
| amount                                      | numeric(24,2) | N               |
| currency                                    | varchar(3)    | N               |
| authorized_destination, accepted_conditions | jsonb         | N               |
| requested_date                              | date          | N               |
| commitment_amount                           | numeric(24,2) | N               |
| disbursement_id                             | uuid          | S               |
| status                                      | varchar(30)   | N               |
| T, C, S                                     | Bloques       |                 |

PK utilization_id, FK a financiación y desembolso.

Estados COMMITTED, DELIVERY_PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (financing_id, status, utilization_id).

La capacidad se compromete antes de solicitar dinero. Un compromiso incierto no se libera por temporizador.

OVERDRAFT no utiliza esta interfaz para crear capital.

## **13. Obligaciones y componentes de deuda**

### **13.1. financing_obligation**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| obligation_id, financing_id              | uuid        | N               |
| origin_type                              | varchar(80) | N               |
| origin_id                                | uuid        | N               |
| occurrence_code                          | varchar(80) | N               |
| contractual_due_date, adjusted_due_date  | date        | N               |
| economic_period_from, economic_period_to | date        | S               |
| status                                   | varchar(20) | N               |
| T                                        | Bloque      |                 |

PK obligation_id, FK a financiación.

UQ (financing_id, origin_type, origin_id, occurrence_code).

UQ (financing_id, obligation_id) para pertenencia.

Estados OPEN, SETTLED, REPLACED y CANCELLED.

Índice (financing_id, adjusted_due_date, obligation_id).

### **13.2. financing_debt_component**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| component_id, financing_id, obligation_id | uuid          | N               |
| component_type, concept_code              | varchar(80)   | N               |
| currency                                  | varchar(3)    | N               |
| original_amount, recognized_amount        | numeric(24,2) | N               |
| adjustment_amount                         | numeric(24,2) | N               |
| paid_amount, waived_amount                | numeric(24,2) | N               |
| remaining_amount, due_remaining_amount    | numeric(24,2) | N               |
| prepared_amount                           | numeric(24,2) | N               |
| source_reference                          | jsonb         | N               |
| status                                    | varchar(20)   | N               |
| lock_version                              | bigint        | N               |
| updated_at                                | timestamptz   | N               |

PK component_id.

FK compuesta a obligación de la financiación.

adjustment_amount admite signo. Los demás importes son no negativos.

Ecuaciones:

- Remanente = reconocido + ajustes − pagado − condonado.

- Exigible pendiente ≤ remanente.

- Preparado ≤ remanente.

Categorías: CAPITAL, INTEREST, FEE, TAX, INSURANCE y AUTHORIZED_EXPENSE.

Estados: ACCRUED, DUE, PARTIALLY_PAID, PAID, CORRECTED y WAIVED.

UQ (financing_id, component_id).

Índice (financing_id, status, obligation_id, component_id).

El importe original es inmutable. Las correcciones se registran como variaciones.

El cronograma distribuye capital reconocido; no crea un segundo capital. El interés previsto no constituye devengamiento
por sí mismo.

### **13.3. financing_debt_entry**

| **Columna**                          | **Tipo**      | **Nulabilidad** |
|--------------------------------------|---------------|-----------------|
| entry_id, financing_id, component_id | uuid          | N               |
| debt_version                         | bigint        | N               |
| entry_position                       | integer       | N               |
| entry_type                           | varchar(30)   | N               |
| amount                               | numeric(24,2) | N               |
| effective_date                       | date          | N               |
| source_service, source_type          | varchar(80)   | N               |
| source_id                            | uuid          | N               |
| source_item_code                     | varchar(80)   | N               |
| T                                    | Bloque        |                 |

PK entry_id.

FK a componente de esa financiación.

El importe admite signo conforme al tipo.

Tipos RECOGNITION, ACCRUAL, PAYMENT, CORRECTION, WAIVER, REPLACEMENT y PAYMENT_REVERSAL.

UQ:

- (financing_id, debt_version, entry_position).

- (source_service, source_type, source_id, source_item_code).

Índice (component_id, debt_version, entry_position).

Las entradas son inmutables y se confirman junto con los acumulados.

## **14. Devengamiento y tasas variables**

### **14.1. financing_accrual**

| **Columna**                                 | **Tipo**       | **Nulabilidad** |
|---------------------------------------------|----------------|-----------------|
| accrual_id, financing_id                    | uuid           | N               |
| concept_code, scope_code                    | varchar(80)    | N               |
| period_from, period_to                      | date           | N               |
| methodology_reference, conditions_reference | jsonb          | N               |
| source_references                           | jsonb          | N               |
| original_accrual_id                         | uuid           | S               |
| unrounded_amount                            | numeric(30,12) | N               |
| rounded_amount                              | numeric(24,2)  | N               |
| status                                      | varchar(20)    | N               |
| T, S                                        | Bloques        |                 |

PK accrual_id.

FK a financiación y determinación original.

Estados DETERMINED, APPLIED, SUPERSEDED e INCOMPLETE.

Se prohíbe solapar intervalos activos para la misma financiación, concepto y alcance.

Una rectificación identifica el original y aplica diferencias. El historial sustituido permanece disponible.

### **14.2. financing_accrual_segment**

| **Columna**               | **Tipo**       | **Nulabilidad** |
|---------------------------|----------------|-----------------|
| accrual_id                | uuid           | N               |
| position                  | integer        | N               |
| period_from, period_to    | date           | N               |
| principal_basis           | numeric(24,2)  | N               |
| rate                      | numeric(24,12) | N               |
| rate_reference            | jsonb          | N               |
| day_count_code            | varchar(80)    | N               |
| days                      | integer        | N               |
| unrounded_amount          | numeric(30,12) | N               |
| position_source_reference | jsonb          | N               |

PK (accrual_id, position) y FK a devengamiento.

Los segmentos cubren el intervalo sin superponerse y se separan ante cambios de capital o tasa.

### **14.3. financing_rate_observation**

| **Columna**               | **Tipo**       | **Nulabilidad** |
|---------------------------|----------------|-----------------|
| observation_id            | uuid           | N, PK           |
| source_service, rate_code | varchar(80)    | N               |
| source_observation_id     | uuid           | N               |
| source_version            | bigint         | N               |
| value                     | numeric(24,12) | N               |
| observation_date          | date           | N               |
| valid_from                | timestamptz    | N               |
| valid_until               | timestamptz    | S               |
| evidence_references       | jsonb          | N               |
| T                         | Bloque         |                 |

UQ (source_service, source_observation_id, source_version).

Índice (source_service, rate_code, observation_date, observation_id).

### **14.4. financing_rate_application**

| **Columna**                                  | **Tipo**       | **Nulabilidad** |
|----------------------------------------------|----------------|-----------------|
| application_id, financing_id, observation_id | uuid           | N               |
| review_index                                 | bigint         | N               |
| margin, applied_rate                         | numeric(24,12) | N               |
| valid_from, valid_until                      | timestamptz    | N               |
| provisional                                  | boolean        | N               |
| provisional_basis, incident_reference        | jsonb          | S               |
| T                                            | Bloque         |                 |

PK application_id, FK a financiación y observación.

UQ (financing_id, review_index).

La provisionalidad exige fundamento contractual e incidencia.

La última referencia válida solo se utiliza durante un período de revisión si el acuerdo lo permite.

No se generan intereses sobre intereses, cargos o tributos. No se agrega tasa punitoria ni penalidad fija ordinaria.

Líneas y descubierto utilizan Actual/365. La remuneración de capital vencido amortizable utiliza la tasa diaria
equivalente contractual, sin duplicar períodos.

## **15. Preparación de pagos**

### **15.1. financing_payment_preparation**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| preparation_id, financing_id      | uuid          | N               |
| payment_owner_service             | varchar(80)   | N               |
| payment_id                        | uuid          | N               |
| payer_reference, debtor_reference | jsonb         | N               |
| amount                            | numeric(24,2) | N               |
| currency                          | varchar(3)    | N               |
| expected_effective_date           | date          | N               |
| prepared_debt_version             | bigint        | N               |
| stage                             | varchar(30)   | N               |
| status                            | varchar(30)   | N               |
| expected_surplus                  | numeric(24,2) | N               |
| conditions                        | jsonb         | N               |
| T, C, S                           | Bloques       |                 |

PK preparation_id, FK a financiación.

UQ (financing_id, payment_owner_service, payment_id).

Etapas PRELIMINARY_ACCEPTANCE y PREPARE_APPLICATION.

Estados PRELIMINARILY_ACCEPTED, PREPARED, APPLIED, CANCELLED y REQUIRES_RESOLUTION.

La promoción a preparación definitiva conserva la misma identidad de pago.

Índice (financing_id, status, preparation_id).

### **15.2. financing_prepared_allocation**

| **Columna**                                               | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------------|---------------|-----------------|
| preparation_id, financing_id, obligation_id, component_id | uuid          | N               |
| amount                                                    | numeric(24,2) | N               |
| status                                                    | varchar(20)   | N               |

PK (preparation_id, component_id).

FK a preparación y componente de la financiación. Se comprueba la obligación del componente.

Estados HELD, APPLIED y RELEASED.

Índice (component_id, status, preparation_id).

Las asignaciones HELD integran prepared_amount. Una aceptación preliminar no crea asignaciones comprometidas.

Preparar no acredita ingreso ni cancela deuda.

## **16. Ingresos e imputaciones**

### **16.1. financing_payment_receipt**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| receipt_id                             | uuid          | N, PK           |
| owner_service                          | varchar(80)   | N               |
| source_receipt_id                      | uuid          | N               |
| amount                                 | numeric(24,2) | N               |
| currency                               | varchar(3)    | N               |
| effective_date                         | date          | N               |
| receipt_reference, evidence_references | jsonb         | N               |
| T, S                                   | Bloques       |                 |

UQ (owner_service, source_receipt_id).

La identidad es la del ingreso comprobado, no la de cada mensaje que lo comunica.

### **16.2. financing_receipt_control**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| receipt_id                          | uuid          | N, PK           |
| received_amount, applied_amount     | numeric(24,2) | N               |
| committed_amount, unassigned_amount | numeric(24,2) | N               |
| returned_amount                     | numeric(24,2) | N               |
| lock_version                        | bigint        | N               |
| updated_at                          | timestamptz   | N               |

FK a ingreso.

Recibido = aplicado + comprometido + no asignado + devuelto.

Un ingreso que alcanza varias financiaciones comparte este control.

### **16.3. financing_payment_application**

| **Columna**                                              | **Tipo**      | **Nulabilidad** |
|----------------------------------------------------------|---------------|-----------------|
| application_id, receipt_id, preparation_id, financing_id | uuid          | N               |
| applied_amount, surplus_amount                           | numeric(24,2) | N               |
| status                                                   | varchar(30)   | N               |
| result_references                                        | jsonb         | N               |
| T, C                                                     | Bloques       |                 |

PK application_id.

FK a ingreso, control, preparación y financiación.

UQ (receipt_id, preparation_id).

Estados PENDING, APPLIED y REQUIRES_REVIEW.

### **16.4. financing_payment_allocation**

| **Columna**                                 | **Tipo**      | **Nulabilidad** |
|---------------------------------------------|---------------|-----------------|
| allocation_id, application_id, financing_id | uuid          | N               |
| obligation_id, component_id                 | uuid          | N               |
| amount                                      | numeric(24,2) | N               |
| effective_date                              | date          | N               |
| T                                           | Bloque        |                 |

PK allocation_id.

FK a aplicación y componente de esa financiación.

Índice (component_id, effective_date, allocation_id).

### **16.5. financing_receipt_disposition**

| **Columna**                                | **Tipo**      | **Nulabilidad** |
|--------------------------------------------|---------------|-----------------|
| disposition_id, receipt_id                 | uuid          | N               |
| amount                                     | numeric(24,2) | N               |
| disposition_type                           | varchar(30)   | N               |
| beneficiary_reference, authorization_basis | jsonb         | N               |
| financing_id                               | uuid          | S               |
| remote_action_id                           | uuid          | S               |
| result_reference                           | jsonb         | S               |
| status                                     | varchar(20)   | N               |
| T, C                                       | Bloques       |                 |

PK disposition_id.

FK a ingreso y financiación cuando corresponda.

Tipos RETURN, AUTHORIZED_PREPAYMENT y PENDING_INSTRUCTION.

Estados PENDING, CONFIRMED, UNCERTAIN y CANCELLED.

Índice (receipt_id, status, disposition_id).

El excedente no precancela capital futuro sin autorización.

### **16.6. Orden de imputación**

La imputación ordinaria atiende:

1.  Obligaciones vencidas, desde la más antigua.

2.  Obligaciones exigibles del día.

3.  Interés ordinario y capital dentro de cada obligación.

4.  Tributos asociados.

5.  Comisiones y gastos posteriores.

Si cambia la deuda, se conserva el ingreso y se aplica únicamente lo procedente.

Corregir una imputación reutiliza el ingreso original; no crea otro cobro.

## **17. Descubierto**

### **17.1. financing_overdraft_link**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| financing_id, account_id    | uuid        | N               |
| funds_service               | varchar(80) | N               |
| limit_application_reference | jsonb       | N               |
| T                           | Bloque      |                 |

PK financing_id, FK a financiación y UQ sobre account_id.

Solo existe para OVERDRAFT.

### **17.2. financing_overdraft_source_fact**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| source_fact_id, financing_id, account_id | uuid          | N               |
| funds_service                            | varchar(80)   | N               |
| original_fact_id                         | uuid          | N               |
| source_position_version                  | bigint        | N               |
| source_movement_sequence                 | bigint        | S               |
| principal_used, committed_coverage       | numeric(24,2) | N               |
| protected_coverage, excess               | numeric(24,2) | N               |
| effective_at, cut_at                     | timestamptz   | N               |
| evidence_references                      | jsonb         | N               |
| T, S                                     | Bloques       |                 |

PK source_fact_id, FK a vínculo.

UQ (funds_service, original_fact_id).

Índice (financing_id, source_position_version, source_fact_id).

### **17.3. financing_overdraft_source_control**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| financing_id                | uuid        | N, PK           |
| last_position_version       | bigint      | N               |
| last_movement_sequence      | bigint      | N               |
| completeness                | varchar(20) | N               |
| pending_ranges              | jsonb       | N               |
| authoritative_cut_reference | jsonb       | N               |
| lock_version                | bigint      | N               |
| updated_at                  | timestamptz | N               |

FK a vínculo.

Contadores no negativos.

Completitud COMPLETE, GAP_PENDING o REQUIRES_REVIEW.

La versión de posición ordena cambios globales. La secuencia de movimientos no sustituye cambios de reservas o límites
sin movimiento.

Un hecho atrasado completa historia, pero no reemplaza la posición actual.

La reducción de capital confirmada por Fondos es su única cancelación monetaria. No se vuelve a aplicar como pago de
capital local.

### **17.4. overdraft_charge_collection**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| collection_id, financing_id | uuid          | N               |
| mode                        | varchar(20)   | N               |
| component_allocations       | jsonb         | N               |
| amount                      | numeric(24,2) | N               |
| authorization_reference     | jsonb         | N               |
| monetary_step_id            | uuid          | N               |
| status                      | varchar(20)   | N               |
| T, C, S                     | Bloques       |                 |

PK collection_id, FK a financiación y paso monetario.

Modos TOTAL y EXPLICIT_PARTIAL.

Estados PENDING, CONFIRMED, INSUFFICIENT y UNCERTAIN.

Utiliza exclusivamente fondos propios. No genera descubierto para cobrar sus intereses.

## **18. Precancelación**

### **18.1. financing_prepayment_quote**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| quote_id, financing_id | uuid          | N               |
| debt_version           | bigint        | N               |
| effective_date         | date          | N               |
| scope                  | varchar(10)   | N               |
| target_principal       | numeric(24,2) | S               |
| partial_treatment      | varchar(30)   | S               |
| component_allocations  | jsonb         | N               |
| total_amount           | numeric(24,2) | N               |
| resulting_schedule_id  | uuid          | S               |
| valid_until            | timestamptz   | N               |
| T, S                   | Bloques       |                 |

PK quote_id, FK a financiación y cronograma resultante.

TOTAL no contiene capital objetivo ni tratamiento parcial.

PARTIAL exige capital positivo y REDUCE_TERM o REDUCE_INSTALLMENT.

El final de vigencia es el inicio del día siguiente a la fecha efectiva, como final excluido.

Índice (financing_id, valid_until, quote_id).

No incluye intereses futuros ni comisión ordinaria de precancelación.

### **18.2. financing_prepayment**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| prepayment_id, financing_id, quote_id | uuid        | N               |
| acceptance_evidence                   | jsonb       | N               |
| funding_mode                          | varchar(30) | N               |
| receipt_id                            | uuid        | S               |
| authorized_collection                 | jsonb       | S               |
| monetary_step_id                      | uuid        | S               |
| status                                | varchar(30) | N               |
| result_references, pending_components | jsonb       | N               |
| T, C, S                               | Bloques     |                 |

PK prepayment_id.

FK a financiación, cotización, ingreso y paso cuando existan.

UQ sobre quote_id.

CONFIRMED_PAYMENT exige ingreso y no instrucción de cobro.

AUTHORIZED_COLLECTION exige instrucción y paso monetario. El ingreso resultante se incorpora sin volver a cobrar.

Estados PENDING, APPLIED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Un cambio material de deuda exige otra cotización aceptada.

La aplicación de capital y conceptos y el cronograma resultante se confirman conjuntamente.

## **19. Mora**

### **19.1. financing_delinquency_assessment**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| delinquency_assessment_id, financing_id     | uuid        | N               |
| cut_at                                      | timestamptz | N               |
| debt_version                                | bigint      | N               |
| overdue_obligations, known_pending_payments | jsonb       | N               |
| delinquency_start_date                      | date        | S               |
| days_past_due                               | integer     | N               |
| completeness                                | varchar(20) | N               |
| pending_checks                              | jsonb       | N               |
| T, S                                        | Bloques     |                 |

PK delinquency_assessment_id, FK a financiación.

Completitud COMPLETE o INCOMPLETE. Días no negativos.

Índice (financing_id, cut_at, delinquency_assessment_id).

La mora comienza al día siguiente del vencimiento ajustado.

Los ingresos oportunos pendientes de imputar se comprueban antes de atribuir incumplimiento.

### **19.2. financing_delinquency_correction**

| **Columna**                           | **Tipo** | **Nulabilidad** |
|---------------------------------------|----------|-----------------|
| correction_id, original_assessment_id | uuid     | N               |
| resulting_assessment_id               | uuid     | N               |
| basis_reference, consequences         | jsonb    | N               |
| T                                     | Bloque   |                 |

PK correction_id, FK a evaluaciones.

Las evaluaciones anteriores permanecen disponibles. Riesgos mantiene la clasificación especializada.

## **20. Reestructuraciones y correcciones**

### **20.1. financing_restructuring**

| **Columna**                                       | **Tipo**    | **Nulabilidad** |
|---------------------------------------------------|-------------|-----------------|
| restructuring_id, financing_id                    | uuid        | N               |
| effective_agreement_reference, approval_reference | jsonb       | N               |
| assessment_reference                              | jsonb       | S               |
| exposure_reference, collateral_references         | jsonb       | N               |
| source_debt_version                               | bigint      | N               |
| debt_cut_reference                                | jsonb       | N               |
| effective_date                                    | date        | N               |
| status                                            | varchar(30) | N               |
| T, C, S                                           | Bloques     |                 |

PK restructuring_id, FK a financiación.

Estados PENDING, APPLIED, REJECTED y REQUIRES_REVIEW.

### **20.2. financing_restructuring_item**

| **Columna**                                           | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------------|---------------|-----------------|
| restructuring_id, original_component_id               | uuid          | N               |
| treatment                                             | varchar(20)   | N               |
| amount                                                | numeric(24,2) | N               |
| resulting_obligation_id, resulting_component_id       | uuid          | S               |
| waiver_approval_reference, waiver_condition_reference | jsonb         | S               |
| waiver_status                                         | varchar(20)   | S               |

PK (restructuring_id, original_component_id).

FK a reestructuración, componente original y resultado local.

Tratamientos KEEP, RESCHEDULE, REPLACE y AUTHORIZED_WAIVER.

REPLACE exige componente resultante. La quita exige aprobación y, si es condicional, conserva PENDING o SATISFIED.

La sustitución de obligaciones antiguas y nuevas se confirma en una transacción. No mantiene el mismo capital exigible
en ambas.

### **20.3. financing_correction**

| **Columna**                                         | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------------|-------------|-----------------|
| correction_id, financing_id                         | uuid        | N               |
| original_fact_reference                             | jsonb       | N               |
| correction_type                                     | varchar(30) | N               |
| effective_date                                      | date        | N               |
| affected_components, treatment, evidence_references | jsonb       | N               |
| manual                                              | boolean     | N               |
| preparer_person_id, approver_person_id              | uuid        | S               |
| approval_reference                                  | jsonb       | S               |
| monetary_step_id                                    | uuid        | S               |
| status                                              | varchar(30) | N               |
| result_references                                   | jsonb       | N               |
| T, C, S                                             | Bloques     |                 |

PK correction_id.

FK a financiación y paso monetario cuando corresponda.

Tipos DEBT_COMPONENT, PAYMENT_ALLOCATION, MONETARY_EFFECT y DELINQUENCY.

Una corrección manual exige aprobación y personas distintas.

Estados PENDING, APPLIED, UNCERTAIN y REQUIRES_REVIEW.

Redistribuir un ingreso no crea otro movimiento monetario.

## **21. Descuento de cheques**

### **21.1. cheque_discount_operation**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| discount_operation_id, customer_id      | uuid          | N               |
| instrument_service                      | varchar(80)   | N               |
| instrument_id                           | uuid          | N               |
| instrument_commitment_reference         | jsonb         | S               |
| advance_date, expected_collection_date  | date          | N               |
| nominal_amount, retained_interest       | numeric(24,2) | N               |
| fees, taxes, net_amount                 | numeric(24,2) | N               |
| currency                                | varchar(3)    | N               |
| calculation_id                          | uuid          | N               |
| calculation_version                     | bigint        | N               |
| contract_reference, acceptance_evidence | jsonb         | S               |
| approval_reference, exposure_reference  | jsonb         | S               |
| financing_id                            | uuid          | S               |
| status                                  | varchar(30)   | N               |
| T, C, S                                 | Bloques       |                 |

PK discount_operation_id.

FK a cálculo y financiación individual.

UQ parcial (instrument_service, instrument_id), donde status NOT IN ('REJECTED','CANCELLED').

UQ parcial sobre financing_id, donde exista.

Estados PREPARING, READY, ASSIGNMENT_PENDING, ADVANCE_PENDING, ACTIVE, SETTLED, REJECTED, CANCELLED y REQUIRES_REVIEW.

Nominal = neto + interés retenido + comisiones + tributos.

Neto positivo y pago del cheque posterior al anticipo.

Solo se admite descuento con responsabilidad del cliente.

### **21.2. discount_unearned_interest**

| **Columna**                                   | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------|---------------|-----------------|
| discount_operation_id                         | uuid          | N, PK           |
| original_retained_amount, accrued_amount      | numeric(24,2) | N               |
| regularized_amount, remaining_unearned_amount | numeric(24,2) | N               |
| lock_version                                  | bigint        | N               |
| updated_at                                    | timestamptz   | N               |

FK a operación.

Retenido = devengado + regularizado + remanente no devengado.

El interés anticipado no se reconoce íntegramente como ingreso inicial.

### **21.3. discounted_cheque_result**

| **Columna**                      | **Tipo**      | **Nulabilidad** |
|----------------------------------|---------------|-----------------|
| result_id, discount_operation_id | uuid          | N               |
| instrument_id, presentation_id   | uuid          | N               |
| source_service                   | varchar(80)   | N               |
| source_result_id                 | uuid          | N               |
| result                           | varchar(10)   | N               |
| amount                           | numeric(24,2) | N               |
| currency                         | varchar(3)    | N               |
| effective_date                   | date          | N               |
| monetary_reference               | jsonb         | S               |
| receipt_id                       | uuid          | S               |
| evidence_references              | jsonb         | N               |
| T, S                             | Bloques       |                 |

PK result_id.

FK a operación e ingreso cuando exista.

UQ (source_service, source_result_id).

PAID exige referencia monetaria confirmada. REJECTED no crea capital.

El cobro cancela la financiación, sin acreditar nuevamente el nominal como saldo libre.

Pago del cliente y cobro del cheque compiten sobre el mismo control de ingreso y financiación. Los excedentes conservan
titular y destino.

## **22. Garantías: identidad y antecedentes**

### **22.1. collateral**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| collateral_id                          | uuid          | N, PK           |
| collateral_type                        | varchar(80)   | N               |
| asset_or_guarantor_reference           | jsonb         | N               |
| currency                               | varchar(3)    | N               |
| status                                 | varchar(30)   | N               |
| contract_reference                     | jsonb         | S               |
| admitted_coverage, committed_coverage  | numeric(24,2) | N               |
| available_coverage, coverage_shortfall | numeric(24,2) | N               |
| charges_completeness                   | varchar(20)   | N               |
| release_barrier                        | boolean       | N               |
| current_coverage_determination_id      | uuid          | S               |
| T, C                                   | Bloques       |                 |

Estados: REGISTERED, UNDER_REVIEW, ADMITTED, CONSTITUTION_PENDING, CONSTITUTED, ALLOCATED, RELEASE_PENDING, RELEASED y
REQUIRES_RESOLUTION.

Cargas COMPLETE, INCOMPLETE u OBSERVED.

Disponible = máximo entre cero y admitida − comprometida.

Insuficiencia = máximo entre cero y comprometida − admitida.

Una reducción de cobertura no elimina asignaciones existentes.

### **22.2. collateral_owner**

| **Columna**                      | **Tipo** | **Nulabilidad** |
|----------------------------------|----------|-----------------|
| collateral_id, owner_id          | uuid     | N               |
| owner_reference, ownership_basis | jsonb    | N               |

PK (collateral_id, owner_id) y FK a garantía.

Se exige al menos un titular.

### **22.3. collateral_document**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| collateral_id    | uuid        | N               |
| document_service | varchar(80) | N               |
| document_id      | uuid        | N               |
| document_version | bigint      | N               |
| document_role    | varchar(80) | N               |

PK sobre garantía, propietario documental, documento, versión y rol.

FK a garantía. Los binarios permanecen en Documentos y comprobantes.

### **22.4. collateral_valuation**

| **Columna**                                  | **Tipo**      | **Nulabilidad** |
|----------------------------------------------|---------------|-----------------|
| valuation_id, collateral_id                  | uuid          | N               |
| amount                                       | numeric(24,2) | N               |
| currency                                     | varchar(3)    | N               |
| valued_on, expires_on                        | date          | N               |
| source_reference, risk_methodology_reference | jsonb         | N               |
| T, S                                         | Bloques       |                 |

PK valuation_id, FK a garantía.

Índice (collateral_id, valued_on, valuation_id).

Las valuaciones son históricas. Una nueva no sobrescribe la utilizada anteriormente.

### **22.5. collateral_charge**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| charge_id, collateral_id                | uuid          | N               |
| cause_code                              | varchar(80)   | N               |
| charge_holder_reference, priority_basis | jsonb         | N               |
| priority_rank                           | integer       | N               |
| amount                                  | numeric(24,2) | S               |
| amount_status                           | varchar(20)   | N               |
| evidence_references                     | jsonb         | N               |
| T, S                                    | Bloques       |                 |

PK charge_id, FK a garantía.

Importe KNOWN o UNKNOWN. KNOWN exige importe; UNKNOWN lo deja ausente.

Rango positivo.

Las cargas desconocidas no se interpretan como cero.

### **22.6. collateral_priority**

| **Columna**                         | **Tipo** | **Nulabilidad** |
|-------------------------------------|----------|-----------------|
| priority_id, collateral_id          | uuid     | N               |
| beneficiary_or_obligation_reference | jsonb    | N               |
| priority_rank                       | integer  | N               |
| basis_reference                     | jsonb    | N               |
| T                                   | Bloque   |                 |

PK priority_id, FK a garantía.

Índice (collateral_id, priority_rank, priority_id).

### **22.7. collateral_insurance**

| **Columna**                              | **Tipo** | **Nulabilidad** |
|------------------------------------------|----------|-----------------|
| insurance_id, collateral_id              | uuid     | N               |
| insurance_reference, coverage_definition | jsonb    | N               |
| valid_from, valid_until                  | date     | N               |
| evidence_references                      | jsonb    | N               |
| T                                        | Bloque   |                 |

PK insurance_id, FK a garantía.

Final no anterior al inicio.

Índice (valid_until, collateral_id, insurance_id).

## **23. Cobertura y asignación de garantías**

### **23.1. collateral_coverage_determination**

| **Columna**                           | **Tipo**      | **Nulabilidad** |
|---------------------------------------|---------------|-----------------|
| determination_id, collateral_id       | uuid          | N               |
| valuation_id                          | uuid          | N               |
| charges_snapshot, priorities_snapshot | jsonb         | N               |
| methodology_reference                 | jsonb         | N               |
| admitted_coverage                     | numeric(24,2) | S               |
| currency                              | varchar(3)    | N               |
| result                                | varchar(20)   | N               |
| missing_data_codes                    | jsonb         | N               |
| T, S                                  | Bloques       |                 |

PK determination_id.

FK a garantía y valuación de esa garantía.

Resultados DETERMINED, INCOMPLETE y OBSERVED.

Solo DETERMINED contiene cobertura utilizable.

La garantía referencia su determinación mediante FK compuesta diferible.

### **23.2. collateral_allocation**

| **Columna**                   | **Tipo**      | **Nulabilidad** |
|-------------------------------|---------------|-----------------|
| allocation_id, collateral_id  | uuid          | N               |
| creditor_service              | varchar(80)   | N               |
| obligation_type               | varchar(80)   | N               |
| obligation_id                 | uuid          | N               |
| priority_id                   | uuid          | N               |
| mode                          | varchar(20)   | N               |
| amount                        | numeric(24,2) | S               |
| currency                      | varchar(3)    | S               |
| scope_code                    | varchar(80)   | S               |
| scope_determination_reference | jsonb         | S               |
| coverage_consumption          | numeric(24,2) | N               |
| released_coverage             | numeric(24,2) | N               |
| status                        | varchar(30)   | N               |
| T, C, S                       | Bloques       |                 |

PK allocation_id.

FK a garantía y prioridad de esa garantía.

MONETARY exige importe y moneda, sin alcance alternativo.

DEFINED_SCOPE exige código y determinación versionada, sin importe monetario directo.

Ambas modalidades exigen impacto de cobertura determinado. No se admite un alcance no cuantificado como consumo cero.

Estados PENDING, ACTIVE, RELEASE_PENDING, RELEASED y UNCERTAIN.

Índices:

- (collateral_id, status, allocation_id).

- (creditor_service, obligation_type, obligation_id, allocation_id).

Las asignaciones pendientes e inciertas consumen cobertura.

El compromiso remanente es consumo menos cobertura liberada, nunca negativo.

### **23.3. collateral_reinforcement_request**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| request_id, collateral_id | uuid          | N               |
| affected_obligations      | jsonb         | N               |
| shortfall_amount          | numeric(24,2) | N               |
| due_at                    | timestamptz   | N               |
| status                    | varchar(20)   | N               |
| evidence_references       | jsonb         | N               |
| T, C                      | Bloques       |                 |

PK request_id, FK a garantía.

Estados OPEN, SATISFIED, OVERDUE y CANCELLED.

Índice (status, due_at, request_id).

## **24. Liberación y sustitución de garantías**

### **24.1. collateral_release_process**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| release_process_id, collateral_id   | uuid          | N               |
| release_type                        | varchar(10)   | N               |
| requested_allocations               | jsonb         | N               |
| requested_amount                    | numeric(24,2) | S               |
| requested_scope                     | jsonb         | S               |
| creditor_inventory                  | jsonb         | N               |
| economic_status, documentary_status | varchar(20)   | N               |
| status                              | varchar(30)   | N               |
| T, C, S                             | Bloques       |                 |

PK release_process_id, FK a garantía.

Tipos PARTIAL y TOTAL.

Estados económicos y documentales PENDING, CONFIRMED y REQUIRES_REVIEW.

Estado global REQUESTED, CHECKING, COORDINATING, COMPLETED y REQUIRES_RESOLUTION.

La falta de respuesta del acreedor permanece pendiente.

### **24.2. collateral_release_allocation**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| release_process_id, allocation_id      | uuid          | N               |
| requested_coverage, confirmed_coverage | numeric(24,2) | N               |
| evidence_references                    | jsonb         | N               |
| status                                 | varchar(20)   | N               |

PK sobre proceso y asignación. FK a ambos.

Confirmado no superior a solicitado ni al remanente de asignación.

### **24.3. collateral_substitution**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| substitution_id                                  | uuid        | N, PK           |
| previous_collateral_id, substitute_collateral_id | uuid        | N               |
| allocation_mapping                               | jsonb       | N               |
| substitute_confirmation_reference                | jsonb       | S               |
| release_process_id                               | uuid        | S               |
| status                                           | varchar(30) | N               |
| T, C, S                                          | Bloques     |                 |

FK a ambas garantías y proceso de liberación.

Garantías distintas.

Estados PREPARING, SUBSTITUTE_PENDING, RELEASE_PENDING, COMPLETED y REQUIRES_RESOLUTION.

El respaldo anterior permanece hasta confirmar sustituto y aplicaciones.

Las garantías se bloquean por identificador. La liberación instala una barrera que impide nuevas asignaciones
incompatibles.

No se libera con obligaciones, ejecuciones o sustituciones inciertas.

## **25. Ejecución de garantías**

### **25.1. collateral_enforcement**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| enforcement_id, collateral_id                    | uuid        | N               |
| collection_case_reference, obligation_references | jsonb       | N               |
| authorized_decision_reference                    | jsonb       | N               |
| stage_code                                       | varchar(80) | N               |
| status                                           | varchar(20) | N               |
| pending_applications                             | jsonb       | N               |
| T, C                                             | Bloques     |                 |

PK enforcement_id, FK a garantía.

Estados OPEN, IN_PROGRESS, APPLICATION_PENDING y CONCLUDED.

### **25.2. collateral_enforcement_action**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| enforcement_action_id, enforcement_id         | uuid        | N               |
| action                                        | varchar(30) | N               |
| occurred_at                                   | timestamptz | N               |
| basis_reference, evidence_references          | jsonb       | N               |
| receipt_reference                             | jsonb       | S               |
| asset_reference, accepted_valuation_reference | jsonb       | S               |
| admission_acceptance                          | jsonb       | S               |
| result_payload                                | jsonb       | N               |
| T, S                                          | Bloques     |                 |

PK enforcement_action_id, FK a ejecución.

Acciones START, RECORD_PROCEEDING, RECORD_SALE, RECORD_GUARANTOR_PAYMENT, RECORD_ADJUDICATION y CONCLUDE.

Venta y pago de garante exigen ingreso comprobado.

Adjudicación exige bien, valuación admitida y aceptación. No genera ingreso de efectivo.

Concluir no demuestra cancelación de obligaciones.

## **26. Cobranzas y recuperaciones**

### **26.1. collection_case**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| case_id                                     | uuid        | N, PK           |
| creditor_service                            | varchar(80) | N               |
| debtor_reference                            | jsonb       | N               |
| debt_cut_reference, delinquency_reference   | jsonb       | N               |
| known_pending_payments, dispute_references  | jsonb       | N               |
| collateral_references, contact_restrictions | jsonb       | N               |
| strategy_code                               | varchar(80) | N               |
| assigned_agent_reference                    | jsonb       | S               |
| status                                      | varchar(30) | N               |
| T, C                                        | Bloques     |                 |

Estados: OPEN, UNDER_MANAGEMENT, AGREEMENT_PENDING, AGREEMENT_ACTIVE, ENFORCEMENT, RESIDUAL y CLOSED.

Índice (creditor_service, status, updated_at, case_id).

### **26.2. collection_case_obligation**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| case_id                           | uuid        | N               |
| creditor_service, obligation_type | varchar(80) | N               |
| obligation_id                     | uuid        | N               |

PK sobre las cuatro columnas y FK al caso.

Índice inverso (creditor_service, obligation_type, obligation_id, case_id).

La obligación continúa perteneciendo al acreedor.

### **26.3. collection_debt_observation**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| observation_id, case_id                            | uuid        | N               |
| source_version                                     | bigint      | N               |
| cut_at                                             | timestamptz | N               |
| completeness                                       | varchar(20) | N               |
| authoritative_position, pending_payments, disputes | jsonb       | N               |
| source_reference                                   | jsonb       | N               |
| T, S                                               | Bloques     |                 |

PK observation_id, FK al caso.

Completitud COMPLETE o INCOMPLETE.

Índice (case_id, cut_at, observation_id).

Las observaciones son inmutables. Una posición antigua no sustituye una posterior.

## **27. Gestiones y acuerdos de cobranza**

### **27.1. collection_management_action**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| management_action_id, case_id       | uuid        | N               |
| action                              | varchar(30) | N               |
| occurred_at                         | timestamptz | N               |
| actor_reference                     | jsonb       | N               |
| action_content, evidence_references | jsonb       | N               |
| T, S                                | Bloques     |                 |

PK management_action_id, FK al caso.

Acciones RECORD_CONTACT, RECORD_PROMISE, SUBMIT_PROPOSAL, RECORD_AGREEMENT y RECORD_AGREEMENT_RESULT.

El esquema exige canal, interlocutor y resultado para contactos; obligaciones, importes y fechas para promesas.

Una promesa no reduce deuda.

### **27.2. collection_agreement**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| agreement_id, case_id                   | uuid        | N               |
| management_action_id                    | uuid        | N               |
| terms                                   | jsonb       | N               |
| acceptance_evidence, approval_reference | jsonb       | N               |
| contract_reference                      | jsonb       | S               |
| creditor_application_reference          | jsonb       | S               |
| status                                  | varchar(30) | N               |
| T, C, S                                 | Bloques     |                 |

PK agreement_id, FK a caso y gestión.

Estados PROPOSED, ACCEPTED, APPLICATION_PENDING, ACTIVE, BREACHED y COMPLETED.

El acuerdo no modifica por sí mismo la deuda. Su aplicación se acredita mediante el acreedor.

Los gastos externos requieren evidencia. Los gastos internos ordinarios no se agregan como penalidad.

## **28. Recuperaciones y distribución**

### **28.1. collection_recovery**

| **Columna**                                      | **Tipo**      | **Nulabilidad** |
|--------------------------------------------------|---------------|-----------------|
| recovery_id, case_id                             | uuid          | N               |
| source_type                                      | varchar(20)   | N               |
| source_service                                   | varchar(80)   | N               |
| source_recovery_id                               | uuid          | N               |
| monetary_amount                                  | numeric(24,2) | S               |
| admitted_asset_value                             | numeric(24,2) | S               |
| currency                                         | varchar(3)    | N               |
| receipt_reference                                | jsonb         | S               |
| asset_reference, adjudication_resolution         | jsonb         | S               |
| evidence_references, priorities                  | jsonb         | N               |
| justified_expenses                               | numeric(24,2) | N               |
| committed_amount, applied_amount, surplus_amount | numeric(24,2) | N               |
| status                                           | varchar(30)   | N               |
| T, C, S                                          | Bloques       |                 |

PK recovery_id, FK al caso.

UQ (source_service, source_recovery_id).

Fuentes CASH_RECEIPT, GUARANTOR_PAYMENT, ASSET_SALE y ADJUDICATION.

Las tres primeras exigen importe e ingreso comprobado, sin valor adjudicado.

ADJUDICATION exige bien, valor admitido y resolución, sin ingreso monetario ficticio.

Estados RECEIVED, DISTRIBUTING, APPLICATION_PENDING, APPLIED y REQUIRES_REVIEW.

Valor tratado = gastos justificados + comprometido + aplicado + excedente.

La contabilización de gastos o aplicaciones de una adjudicación requiere que su resolución admita ese tratamiento.

### **28.2. collection_recovery_allocation**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| allocation_id, recovery_id        | uuid          | N               |
| creditor_service, obligation_type | varchar(80)   | N               |
| obligation_id                     | uuid          | N               |
| amount_or_value                   | numeric(24,2) | N               |
| preparation_reference             | jsonb         | S               |
| remote_action_id                  | uuid          | N               |
| requested_content                 | jsonb         | N               |
| status                            | varchar(30)   | N               |
| result_reference                  | jsonb         | S               |
| T, C, S                           | Bloques       |                 |

PK allocation_id, FK al recupero.

UQ (creditor_service, remote_action_id).

Estados PENDING, PREPARED, APPLIED, REJECTED y UNCERTAIN.

Índice (recovery_id, status, allocation_id).

Las asignaciones pendientes e inciertas mantienen comprometido el importe o valor.

El acreedor confirma la aplicación. No se informa cancelación mientras siga pendiente.

### **28.3. collection_recovery_surplus**

| **Columna**             | **Tipo**      | **Nulabilidad** |
|-------------------------|---------------|-----------------|
| surplus_id, recovery_id | uuid          | N               |
| amount_or_value         | numeric(24,2) | N               |
| holder_reference        | jsonb         | N               |
| authorized_destination  | jsonb         | S               |
| treatment_reference     | jsonb         | S               |
| status                  | varchar(20)   | N               |
| T, C                    | Bloques       |                 |

PK surplus_id, FK al recupero.

Estados PENDING_INSTRUCTION, PROCESSING, RESOLVED y UNCERTAIN.

### **28.4. collection_case_closure**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| closure_id, case_id                      | uuid        | N               |
| cause_code                               | varchar(80) | N               |
| evidence_references, residual_references | jsonb       | N               |
| closed_at                                | timestamptz | N               |
| T                                        | Bloque      |                 |

PK closure_id, FK al caso.

Cerrar el caso no extingue la deuda.

## **29. Previsiones de cartera**

### **29.1. financing_portfolio**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| portfolio_id     | uuid        | N, PK           |
| code             | varchar(80) | N               |
| scope_definition | jsonb       | N               |
| T, C             | Bloques     |                 |

UQ sobre code.

Solo representa cartera de Préstamos y líneas.

### **29.2. portfolio_provision_calculation**

| **Columna**                                  | **Tipo**      | **Nulabilidad** |
|----------------------------------------------|---------------|-----------------|
| calculation_id, portfolio_id                 | uuid          | N               |
| cut_at                                       | timestamptz   | N               |
| positions_reference, risk_classifications    | jsonb         | N               |
| methodology_reference, collateral_references | jsonb         | N               |
| currency                                     | varchar(3)    | N               |
| required_amount, recognized_amount           | numeric(24,2) | N               |
| variation_amount                             | numeric(24,2) | N               |
| completeness                                 | varchar(20)   | N               |
| missing_data_codes                           | jsonb         | N               |
| T, S                                         | Bloques       |                 |

PK calculation_id, FK a cartera.

Variación con signo = requerido − reconocido.

Completitud COMPLETE o INCOMPLETE.

Índice (portfolio_id, cut_at, calculation_id).

### **29.3. portfolio_provision_item**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| calculation_id, financing_id                        | uuid          | N               |
| position_reference, classification_reference        | jsonb         | N               |
| provision_basis, required_amount, recognized_amount | numeric(24,2) | N               |
| variation_amount                                    | numeric(24,2) | N               |

PK sobre cálculo y financiación. FK a ambos.

No incluye otra previsión de deuda administrada por Cuenta de tarjeta.

Previsionar o dar de baja contablemente no paga ni condona deuda.

## **30. Posiciones y cierre de financiación**

### **30.1. financing_position_snapshot**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| snapshot_id, financing_id           | uuid        | N               |
| financing_version, debt_version     | bigint      | N               |
| cut_at                              | timestamptz | N               |
| position_payload, source_references | jsonb       | N               |
| overdraft_source_cut                | jsonb       | S               |
| completeness                        | varchar(20) | N               |
| missing_data_codes                  | jsonb       | N               |
| T, S                                | Bloques     |                 |

PK snapshot_id, FK a financiación.

Índice (financing_id, cut_at, snapshot_id).

Completitud COMPLETE o INCOMPLETE.

La posición identifica capital, conceptos, compromisos, pagos, preparaciones, mora, garantías, exposición y saldos a
favor.

El descubierto conserva el corte autoritativo de Fondos.

### **30.2. financing_closure**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| closure_id, financing_id         | uuid        | N               |
| cause_code                       | varchar(80) | N               |
| received_at                      | timestamptz | N               |
| uses_ceased_at                   | timestamptz | S               |
| economic_status, residual_status | varchar(20) | N               |
| status                           | varchar(30) | N               |
| evidence_references              | jsonb       | N               |
| closed_at                        | timestamptz | S               |
| T, C                             | Bloques     |                 |

PK closure_id, FK a financiación.

Económico PENDING o EXTINGUISHED. Residual NONE, PENDING o RESOLVED.

Estado REQUESTED, PROCESSING, RESIDUAL, CLOSED o REQUIRES_REVIEW.

UQ parcial sobre financiación para cierres no CLOSED.

### **30.3. financing_closure_component**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| closure_id                              | uuid        | N               |
| component_code, responsible_service     | varchar(80) | N               |
| remote_action_id                        | uuid        | S               |
| status                                  | varchar(30) | N               |
| result_reference                        | jsonb       | S               |
| evidence_references, pending_components | jsonb       | N               |
| updated_at                              | timestamptz | N               |
| lock_version                            | bigint      | N               |

PK (closure_id, component_code) y FK a cierre.

Estados PENDING, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

Los componentes cubren capital, otros conceptos, desembolsos y cobros inciertos, garantías, exposición, instrumentos,
saldos a favor y documentos.

Capital cero con interés pendiente no permite cancelación integral.

## **31. Orden de bloqueo y atomicidad**

### **31.1. Evaluación crediticia**

Para análisis y decisión:

1.  Evaluación.

2.  Análisis.

3.  Decisión y versión.

Para revisión y aplicación:

1.  Evaluación, si también debe modificarse.

2.  Decisión.

3.  Aprobación.

4.  Aplicaciones y coordinación de exposición.

Ningún proceso adquiere una evaluación después de bloquear su aprobación.

### **31.2. Préstamos y líneas**

Cuando interviene un ingreso:

1.  Control del ingreso.

2.  Financiaciones ordenadas por identificador.

3.  Preparaciones.

4.  Obligaciones y componentes ordenados.

5.  Aplicaciones y entradas.

Un proceso no adquiere el control de ingreso después de bloquear una financiación.

Desembolsos, utilizaciones y límites bloquean primero la financiación y después sus controles y detalles.

Las operaciones entre varias financiaciones determinan todos sus identificadores antes de bloquearlas.

### **31.3. Garantías**

1.  Garantías ordenadas por identificador.

2.  Controles y determinaciones.

3.  Asignaciones.

4.  Sustitución, liberación o ejecución.

5.  Pasos y resultados.

Las llamadas a acreedores ocurren fuera de la transacción.

### **31.4. Cobranzas**

1.  Caso.

2.  Recupero.

3.  Asignaciones ordenadas.

4.  Excedentes y pasos.

Una recuperación incierta conserva sus compromisos.

### **31.5. Confirmación local**

Cada resultado confirma conjuntamente sus registros, acumulados, historia, actuación, inbox, trabajos y outbox
aplicables.

No existe entrega reconocida dos veces, asignación sin control de cobertura ni imputación sin referencia de ingreso.

## **32. Restricciones de pertenencia e inmutabilidad**

Las referencias a componentes de un mismo agregado utilizan FK compuestas.

Se declaran las UQ necesarias sobre:

- Identidad y propietario de cada componente.

- Condición y resultado.

- Financiación y obligación.

- Financiación y componente.

- Garantía y valuación.

- Garantía y prioridad.

- Preparación y financiación.

- Desembolso y financiación.

- Aplicación y preparación.

Las referencias circulares de raíz y versión son diferibles.

Las comprobaciones de sumas, alcance y compatibilidad entre filas se ejecutan bajo el control local indicado.

Son inmutables los análisis utilizados, decisiones versionadas, cálculos utilizados, entradas de deuda, ingresos
acreditados, resultados de fuente y observaciones históricas.

Las correcciones agregan hechos vinculados y no eliminan el original.

## **33. Eventos**

| **Contrato** | **Tipo**                                        | **Tema**                         | **Agregado**                    |
|--------------|-------------------------------------------------|----------------------------------|---------------------------------|
| CI-06-038    | CreditDecisionChanged                           | banking.credit-assessment.events | CREDIT_DECISION                 |
| CI-06-039    | CollateralChanged                               | banking.collateral.events        | COLLATERAL                      |
| CI-06-040    | FinancingConstitutionOrCapacityChanged          | banking.lending.events           | FINANCING                       |
| CI-06-041    | FinancingDebtOrApplicationChanged               | banking.lending.events           | FINANCING                       |
| CI-06-042    | FinancingDelinquencyOrPortfolioTreatmentChanged | banking.lending.events           | FINANCING o FINANCING_PORTFOLIO |
| CI-06-043    | CollectionOrRecoveryChanged                     | banking.collection.events        | COLLECTION_CASE                 |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado.

Las versiones de contenido y de evento se distinguen cuando tienen significados diferentes.

Un evento de desembolso referencia la entrega existente. No ordena otra.

Un evento de aplicación no representa otro ingreso.

Una previsión no comunica cancelación de deuda. Una garantía no sustituye la imputación del acreedor.

## **34. Trabajos y recuperación**

| **Trabajo**                 | **Identidad de deduplicación**               |
|-----------------------------|----------------------------------------------|
| Antecedente pendiente       | Evaluación y fuente.                         |
| Coordinación remota         | Tabla de paso y step_id.                     |
| Revisión de aprobación      | Aprobación y versión.                        |
| Constitución                | Financiación y componente.                   |
| Desembolso                  | Desembolso.                                  |
| Utilización                 | Utilización.                                 |
| Recuperación de descubierto | Financiación y tramo faltante.               |
| Devengamiento               | Financiación, concepto, alcance e intervalo. |
| Aplicación de pago          | Preparación e ingreso.                       |
| Precancelación              | Precancelación.                              |
| Revisión de seguro          | Seguro y vencimiento.                        |
| Sustitución o liberación    | Proceso y componente.                        |
| Recuperación de cobranza    | Recupero y asignación.                       |
| Cierre                      | Cierre y componente.                         |

Las claves respetan el límite de 120 caracteres y pueden utilizar hash de composición canónica.

La recuperación conserva actuaciones, efectos e identidades originales.

No se libera exposición, garantía, capacidad o instrumento por timeout.

Las respuestas tardías se incorporan al proceso original. Una contradicción conserva evidencias y genera tratamiento
explícito.

## **35. Seguridad, migraciones y conservación**

Las migraciones utilizan Flyway por servicio.

Las exclusiones de períodos se implementan con el soporte PostgreSQL correspondiente.

Se separan permisos para preparar, analizar, aprobar, aplicar, constituir, calcular, desembolsar, administrar límites,
asignar garantías, imputar, precancelar, reestructurar, corregir y gestionar recuperaciones.

Un permiso de cobranza no autoriza modificar unilateralmente deuda.

Los archivos permanecen en Documentos y comprobantes.

Redis no conserva la única copia de deuda, ingresos, compromisos, cálculos aceptados, asignaciones o decisiones.

Las migraciones preservan identidades, versiones, relaciones históricas y referencias de recuperación.

Después de restaurar un respaldo se pausan instrucciones externas y se reconcilian desembolsos, ingresos, cesiones y
aplicaciones posteriores al respaldo antes de reanudar.

## **36. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                                                |
|-----------------------|---------------------------------------------------------------------------------|
| CI-06-001 a CI-06-003 | Evaluaciones, participantes, antecedentes, análisis y decisiones.               |
| CI-06-004 a CI-06-006 | Revisiones, aprobaciones, exposición y aplicaciones.                            |
| CI-06-007 a CI-06-010 | Garantías, antecedentes, cobertura, asignaciones, sustituciones y liberaciones. |
| CI-06-011 y CI-06-012 | Constitución, condiciones, cálculos, tasas, cronogramas y CFT.                  |
| CI-06-013 a CI-06-015 | Desembolsos, retenciones, capital, límites y utilizaciones.                     |
| CI-06-016             | Correspondencia y hechos autoritativos de descubierto.                          |
| CI-06-017 y CI-06-018 | Devengamientos, deuda y posiciones.                                             |
| CI-06-019 y CI-06-020 | Preparaciones, ingresos y asignaciones de pagos.                                |
| CI-06-021             | Cobro de conceptos de descubierto con fondos propios.                           |
| CI-06-022 y CI-06-023 | Cotizaciones y ejecución de precancelación.                                     |
| CI-06-024 y CI-06-025 | Mora y reestructuración.                                                        |
| CI-06-026             | Cartera y previsiones.                                                          |
| CI-06-027 y CI-06-028 | Correcciones y cierre.                                                          |
| CI-06-029 a CI-06-031 | Descuento, interés retenido y resultado del cheque.                             |
| CI-06-032             | Actuaciones y recuperación de financiación.                                     |
| CI-06-033 y CI-06-034 | Casos, observaciones, gestiones y acuerdos.                                     |
| CI-06-035             | Ejecución de garantías.                                                         |
| CI-06-036 y CI-06-037 | Recuperos, distribución, excedentes y cierre de cobranza.                       |
| CI-06-038 a CI-06-043 | Outbox e historia de agregados.                                                 |

## **37. Escenarios de verificación de implementación**

| **Escenario**                             | **Resultado exigido**                                  |
|-------------------------------------------|--------------------------------------------------------|
| Fuente crediticia sin respuesta           | No se interpreta como deuda cero.                      |
| Decisión rechazada                        | No genera aprobación utilizable.                       |
| Exposición pendiente                      | Impide disponibilidad de aprobación.                   |
| Aplicación y retiro concurrentes          | Se ordenan sobre la aprobación.                        |
| Constitución sin desembolso               | Capital cero.                                          |
| Bruto 500 y retención 10                  | Neto 490, con retenciones separadas.                   |
| Respuesta de desembolso perdida           | Recupera la entrega original.                          |
| Línea no renovable amortizada             | No recupera consumo histórico no reutilizable.         |
| Cronograma aplicado                       | No duplica el capital reconocido.                      |
| Tasa variable sin referencia              | Conserva provisionalidad o dato faltante.              |
| Devengamientos superpuestos               | No duplica el mismo período y alcance.                 |
| Dos preparaciones concurrentes            | No comprometen el mismo remanente.                     |
| Ingreso que alcanza varias financiaciones | Comparte control y conserva el total.                  |
| Pago 40 con interés 10 y capital 90       | Aplica interés 10 y capital 30 tras confirmar ingreso. |
| Excedente de pago                         | No precancela automáticamente.                         |
| Descubierto 100, interés 10 y crédito 50  | Capital 50 según Fondos; interés separado.             |
| Cobro de intereses de descubierto         | No utiliza nuevo descubierto.                          |
| Pago oportuno pendiente                   | No crea mora por demora interna.                       |
| Precancelación                            | Excluye intereses futuros y exige aceptación vigente.  |
| Reestructuración                          | No deja el mismo capital exigible en origen y destino. |
| Quita condicionada                        | No se confirma antes del cumplimiento.                 |
| Descuento                                 | Separa nominal, neto e interés no devengado.           |
| Pago del cliente y cobro del cheque       | Cancelan una sola deuda y conservan excedente.         |
| Carga de garantía desconocida             | No se interpreta como cero.                            |
| Asignaciones concurrentes de garantía     | No duplican cobertura disponible.                      |
| Alcance no monetario                      | Exige determinación de su impacto.                     |
| Sustitución de respaldo                   | Mantiene anterior hasta confirmar sustituto.           |
| Liberación con acreedor sin respuesta     | Permanece pendiente.                                   |
| Adjudicación                              | No se registra como efectivo.                          |
| Recupero 80 con deuda aplicable 70        | Aplica 70 y conserva excedente 10.                     |
| Capital cero con interés pendiente        | No informa cancelación integral.                       |
| Cierre de cobranza                        | No extingue deuda por sí mismo.                        |
| Evento duplicado o restauración           | No repite dinero, capital ni aplicaciones.             |

Las restricciones, exclusiones, cálculos, bloqueos y transacciones atómicas se verifican durante la implementación con
PostgreSQL real y escenarios concurrentes.
