# **Diseño de persistencia — Módulo 9. Contabilidad**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Contabilidad.

| **Servicio**          | **Responsabilidad**                                                                           |
|-----------------------|-----------------------------------------------------------------------------------------------|
| Registración contable | Plan, reglas, integración de hechos, asientos, saldos y controles locales de registración.    |
| Cierres contables     | Períodos, integridad de fuentes, auxiliares, conciliaciones, barreras, cierres y reaperturas. |
| Información contable  | Estructuras de presentación, cifras, procedencia, emisión y rectificación de informes.        |

Comprende los contratos CI-09-001 a CI-09-033, las aplicaciones de control de períodos  
y las decisiones de emisión definitiva.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones.  
No existen FK, consultas SQL ni transacciones entre bases.

Contabilidad representa hechos económicos.  
No modifica dinero, deuda, tenencias o recursos operativos para conseguir un asiento equilibrado.

Un impedimento contable no revierte automáticamente la operación bancaria de origen.

Información regulatoria conserva la elaboración y presentación de sus declaraciones.

## **2. Convenciones físicas**

### **2.1. Notación**

- N: columna obligatoria.

- S: columna que admite NULL.

- PK: clave primaria.

- UQ: restricción única.

- FK: clave foránea local.

Cuando una fila agrupa nombres de columnas, cada columna tiene individualmente el tipo  
y nulabilidad indicados.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Las FK utilizan eliminación restringida.  
Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en orden.  
PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                 | **PostgreSQL** | **Restricción**                    |
|--------------------------|----------------|------------------------------------|
| Identificador            | uuid           | Estable y no reutilizable.         |
| Versión                  | bigint         | Positiva.                          |
| Control de concurrencia  | bigint         | No negativo.                       |
| Contador de registración | bigint         | No negativo; empieza en cero.      |
| Secuencia confirmada     | bigint         | Positiva.                          |
| Código                   | varchar(80)    | No vacío y registrado.             |
| Denominación             | varchar(200)   | No vacía.                          |
| Descripción de partida   | varchar(500)   | Opcional.                          |
| Fundamento               | varchar(4000)  | No vacío cuando está presente.     |
| Moneda                   | varchar(3)     | ARS o USD.                         |
| Importe de partida       | numeric(24,2)  | No negativo.                       |
| Importe con signo        | numeric(24,2)  | Conforme al concepto.              |
| Acumulado interno        | numeric(38,2)  | Según el saldo o total.            |
| Cantidad                 | numeric(30,12) | No negativa.                       |
| Acumulado de unidades    | numeric(38,12) | Según el saldo.                    |
| Cotización               | numeric(24,12) | Positiva.                          |
| Hash                     | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Instante                 | timestamptz    | Intercambio en UTC.                |
| Fecha                    | date           | Fecha bancaria.                    |
| Contenido                | jsonb          | Esquema registrado y versionado.   |

La precisión y escala se validan antes de persistir.

Los acumulados internos utilizan mayor capacidad que las partidas.  
Antes de exponer resultados se valida su representación en los tipos del contrato.

La moneda de presentación es ARS.

Las conversiones utilizan HALF_UP a centavos.  
Un residuo exclusivo de conversión debe estar identificado;  
no puede encubrir una diferencia económica.

### **2.3. Fechas**

Se conservan separadamente:

- Momento original del hecho.

- Fecha económica.

- Recepción efectiva.

- Confirmación contable.

- Período de registración.

- Corte de información.

El día económico utiliza America/Argentina/Buenos_Aires,  
desde las 00:00 hasta el inicio del día siguiente.

El corte de compensación de las 18:00 no sustituye al corte contable diario.

### **2.4. Bloques físicos**

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

### **2.5. Tablas comunes**

Los tres servicios incorporan actuaciones, idempotencia, historia, outbox, inbox  
y trabajos persistentes.

Información contable incorpora:

- execution_identity_control.

- execution_attempt.

Cierres contables incorpora authority_preparation.

Los intentos de emisión utilizan attempt_type = ACCOUNTING_REPORT_EMISSION.

Una identidad desconocida no demuestra aborto definitivo.

## **3. Esquemas estructurados**

Cada JSONB conserva su versión mediante columna explícita  
o envoltorio con schemaCode, schemaVersion y data.

| **Esquema**               | **Contenido**                                              |
|---------------------------|------------------------------------------------------------|
| RemoteReference           | Propietario, tipo, identidad y versión cuando corresponda. |
| EvidenceReferences        | Hechos y evidencias identificados.                         |
| PendingComponents         | Componente, responsable, motivo y seguimiento.             |
| LedgerAccountDefinition   | Clase, naturaleza, libro, monedas, dimensiones y vigencia. |
| AccountingRuleDefinition  | Fuentes, correlación, fechas, partidas y correcciones.     |
| RegisteredExpression      | Operaciones tipadas y versionadas admitidas.               |
| AccountingFactPayload     | Componentes monetarios o de unidades.                      |
| AccountingNormalization   | Representación normalizada sin alterar el original.        |
| PeriodControlCommand      | Período, jerarquía, modalidad y regularizaciones.          |
| SourceIntegrityScope      | Ámbito, fecha económica, corte y cobertura.                |
| AuxiliaryDimensions       | Dimensiones registradas y referencias tipadas.             |
| ReconciliationMapping     | Correspondencias entre conceptos comparables.              |
| CloseControlEvidence      | Resultado, fuentes, comprobaciones y observaciones.        |
| ReportStructureDefinition | Grupos, correspondencias, fórmulas y validaciones.         |
| ReportProvenance          | Corte, cuentas, partidas, hechos y reglas.                 |

No se admiten SQL, scripts ni atributos arbitrarios.

Las operaciones configurables identifican código, versión y parámetros tipados  
de un mecanismo registrado.

## **4. Propuestas y aprobación**

### **4.1. Propuestas de plan y reglas**

Las tablas siguientes comparten estructura:

| **Tabla**                | **Identidad de destino S** | **FK**          |
|--------------------------|----------------------------|-----------------|
| ledger_account_proposal  | ledger_account_id uuid     | ledger_account  |
| accounting_rule_proposal | rule_id uuid               | accounting_rule |

Columnas adicionales:

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| proposal_id                  | uuid        | N, PK           |
| proposal_version             | bigint      | N               |
| proposal_type                | varchar(20) | N               |
| proposed_definition          | jsonb       | N               |
| validation_results, examples | jsonb       | N               |
| preparer_person_id           | uuid        | N               |
| status                       | varchar(30) | N               |
| approved_review_id           | uuid        | S               |
| T, S                         | Bloques     |                 |
| lock_version                 | bigint      | N               |
| updated_at                   | timestamptz | N               |

Modalidades CREATE, NEW_VERSION, ENABLE y RETIRE.

CREATE no tiene destino existente.  
Las demás lo requieren.

Estados DRAFT, SUBMITTED, APPROVED, APPLIED, REJECTED y WITHDRAWN.

Índice (status, updated_at, proposal_id).

### **4.2. Revisiones**

Se crean:

- ledger_account_proposal_review.

- accounting_rule_review.

Cada tabla incorpora:

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

PK review_id, FK a su propuesta local.

UQ (review_id, proposal_id, proposal_version).

Decisiones PENDING, APPROVED y REJECTED.

APPROVED exige aprobación, fecha y personas distintas.  
REJECTED exige fundamento.

Cada aplicación comprueba versión y huella exactas.  
Modificar contenido exige nueva revisión.

Las propuestas no sustituyen la conservación de las versiones aplicadas.

## **5. Plan de cuentas**

### **5.1. accounting_plan_control**

| **Columna**  | **Tipo**    | **Nulabilidad** |
|--------------|-------------|-----------------|
| control_id   | smallint    | N, PK           |
| plan_version | bigint      | N               |
| lock_version | bigint      | N               |
| updated_at   | timestamptz | N               |

Fila única con control_id = 1, creada por migración.

Serializa cambios de jerarquía y validaciones del plan.

### **5.2. ledger_account**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| ledger_account_id | uuid        | N, PK           |
| code              | varchar(80) | N               |
| current_version   | bigint      | N               |
| T                 | Bloque      |                 |
| lock_version      | bigint      | N               |

UQ sobre código.

Un código con historia no se reutiliza con otra naturaleza.

### **5.3. ledger_account_version**

| **Columna**                          | **Tipo**     | **Nulabilidad** |
|--------------------------------------|--------------|-----------------|
| ledger_account_id                    | uuid         | N               |
| account_version                      | bigint       | N               |
| name                                 | varchar(200) | N               |
| parent_account_id                    | uuid         | S               |
| account_class                        | varchar(20)  | N               |
| normal_balance                       | varchar(10)  | N               |
| account_role                         | varchar(10)  | N               |
| ledger_code                          | varchar(80)  | N               |
| monetary_classification              | varchar(30)  | N               |
| valid_from                           | date         | N               |
| valid_until                          | date         | S               |
| status                               | varchar(20)  | N               |
| source_proposal_id, source_review_id | uuid         | N               |
| balance_retirement_treatment         | jsonb        | S               |
| T, S                                 | Bloques      |                 |

PK (ledger_account_id, account_version).

FK a cuenta, superior, propuesta y revisión.

Clases ASSET, LIABILITY, EQUITY, INCOME, EXPENSE y MEMORANDUM.

Saldo normal DEBIT o CREDIT.  
Rol GROUP o POSTING.

Estados DRAFT, APPROVED, ENABLED y RETIRED.

Monetariedad MONETARY, NON_MONETARY o NOT_APPLICABLE.

La raíz referencia su versión actual mediante FK diferible.

Bajo control de plan se comprueban ausencia de ciclos,  
compatibilidad del superior, libro y vigencia.

GROUP no recibe partidas.  
Retirar una cuenta con saldo exige tratamiento explícito.

### **5.4. ledger_account_currency**

Columnas N:

- ledger_account_id uuid.

- account_version bigint.

- currency varchar(3).

PK sobre las tres columnas y FK a versión.

### **5.5. accounting_dimension_definition**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| dimension_code                       | varchar(80) | N               |
| definition_version                   | bigint      | N               |
| reference_type_code, validation_code | varchar(80) | N               |
| validation_parameters                | jsonb       | N               |
| T, S                                 | Bloques     |                 |

PK sobre código y versión.

### **5.6. ledger_account_dimension**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| ledger_account_id            | uuid        | N               |
| account_version              | bigint      | N               |
| dimension_code               | varchar(80) | N               |
| dimension_definition_version | bigint      | N               |
| required                     | boolean     | N               |

PK sobre cuenta, versión y dimensión.

FK a cuenta versionada y definición de dimensión.

## **6. Reglas de contabilización**

### **6.1. accounting_rule**

Columnas N:

- rule_id uuid, PK.

- code varchar(80), UQ.

- current_version bigint.

- Bloque T.

- lock_version bigint.

### **6.2. accounting_rule_version**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| rule_id                                 | uuid        | N               |
| rule_version                            | bigint      | N               |
| fact_type                               | varchar(80) | N               |
| fact_schema_version                     | bigint      | N               |
| consequence_code, consequence_owner     | varchar(80) | N               |
| correlation_definition, matching_fields | jsonb       | N               |
| date_policy, multicurrency_policy       | jsonb       | N               |
| correction_policy, transient_treatment  | jsonb       | N               |
| valid_from                              | date        | N               |
| valid_until                             | date        | S               |
| status                                  | varchar(20) | N               |
| source_proposal_id, source_review_id    | uuid        | N               |
| T, S                                    | Bloques     |                 |

PK (rule_id, rule_version).

FK a regla, propuesta y revisión.

Estados APPROVED, ENABLED y WITHDRAWN.

La habilitación impide reglas ambiguas para el mismo tipo de hecho, esquema,  
consecuencia, propietario y vigencia.

El catálogo de selección utiliza un control único.

**accounting_rule_selection_control**

Columnas N:

- control_id smallint, PK con valor 1.

- catalog_version bigint.

- lock_version bigint.

- updated_at timestamptz.

Altas y habilitaciones se ordenan sobre esa fila.

### **6.3. accounting_rule_source**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| rule_id                                     | uuid        | N               |
| rule_version                                | bigint      | N               |
| source_code, owner_service, antecedent_type | varchar(80) | N               |
| required_fields, integrity_condition        | jsonb       | N               |
| required                                    | boolean     | N               |

PK (rule_id, rule_version, source_code) y FK a versión.

### **6.4. accounting_rule_line**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| rule_id                              | uuid        | N               |
| rule_version                         | bigint      | N               |
| line_number                          | integer     | N               |
| target_type                          | varchar(20) | N               |
| ledger_account_id                    | uuid        | S               |
| selector_code                        | varchar(80) | S               |
| selector_version                     | bigint      | S               |
| side                                 | varchar(10) | N               |
| amount_expression, dimension_mapping | jsonb       | N               |
| effect_mode                          | varchar(30) | N               |

PK sobre regla, versión y línea.  
FK a regla y cuenta cuando exista.

ACCOUNT exige cuenta.  
REGISTERED_SELECTOR exige selector y versión.

Las expresiones son tipadas.  
No ejecutan texto recibido como código.

Una cuenta transitoria exige causa económica real, contrapartida,  
responsable y procedimiento de resolución.

## **7. Identidad económica y hechos**

### **7.1. accounting_consequence**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| consequence_id               | uuid        | N, PK           |
| owner_service                | varchar(80) | N               |
| operation_id, occurrence_id  | uuid        | N               |
| consequence_code             | varchar(80) | N               |
| authoritative_source_fact_id | uuid        | S               |
| status                       | varchar(30) | N               |
| T, C                         | Bloques     |                 |

UQ (owner_service, operation_id, occurrence_id, consequence_code).

Estados RECEIVED, INTEGRATION_PENDING, READY_TO_POST, POSTED,  
INCONSISTENT y REQUIRES_REVIEW.

Cambiar el identificador del hecho o la actuación no crea otra consecuencia.

### **7.2. accounting_fact**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| fact_id, consequence_id                     | uuid        | N               |
| owner_service, fact_type                    | varchar(80) | N               |
| source_fact_id                              | uuid        | N               |
| fact_schema_version                         | bigint      | N               |
| occurred_at, received_at                    | timestamptz | N               |
| economic_date                               | date        | N               |
| original_payload, normalized_payload        | jsonb       | N               |
| operational_references, evidence_references | jsonb       | N               |
| corrected_fact_id                           | uuid        | S               |
| T, S                                        | Bloques     |                 |

PK fact_id.

FK a consecuencia y hecho corregido.

UQ (owner_service, source_fact_id).

Índices:

- (consequence_id, received_at, fact_id).

- (economic_date, owner_service, fact_id).

La identidad autoritativa se valida contra el propietario de la consecuencia.

### **7.3. accounting_fact_component**

| **Columna**             | **Tipo**       | **Nulabilidad** |
|-------------------------|----------------|-----------------|
| fact_id                 | uuid           | N               |
| position                | integer        | N               |
| concept_code, role_code | varchar(80)    | N               |
| source_reference        | jsonb          | N               |
| mode                    | varchar(10)    | N               |
| amount                  | numeric(24,2)  | S               |
| currency                | varchar(3)     | S               |
| quantity                | numeric(30,12) | S               |
| instrument_reference    | jsonb          | S               |
| unit_code               | varchar(80)    | S               |

PK (fact_id, position) y FK a hecho.

MONETARY exige importe y moneda.  
UNITS exige cantidad, instrumento y unidad.

Las variantes son excluyentes.  
Las unidades no se representan como dinero.

### **7.4. accounting_supporting_antecedent**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| antecedent_id, consequence_id      | uuid        | N               |
| source_service, antecedent_type    | varchar(80) | N               |
| source_object_id                   | uuid        | N               |
| source_version                     | bigint      | N               |
| typed_content, evidence_references | jsonb       | N               |
| T, S                               | Bloques     |                 |

PK antecedent_id, FK a consecuencia.

UQ (consequence_id, source_service, source_object_id, source_version).

### **7.5. accounting_fact_conflict**

Columnas N:

- conflict_id uuid, PK.

- consequence_id uuid, FK.

- source_reference jsonb.

- conflicting_hash char(64).

- conflicting_payload jsonb.

- evidence_references jsonb.

- Bloque T.

UQ (consequence_id, conflicting_hash).

El original no se sobrescribe.

## **8. Integración**

### **8.1. accounting_integration**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| integration_id, consequence_id       | uuid        | N               |
| rule_id                              | uuid        | S               |
| rule_version                         | bigint      | S               |
| status                               | varchar(30) | N               |
| missing_sources, consistency_results | jsonb       | N               |
| T, C, S                              | Bloques     |                 |

PK integration_id, FK a consecuencia y regla versionada cuando se seleccione.

UQ sobre consecuencia.

Estados RECEIVED, INTEGRATION_PENDING, READY_TO_POST, POSTED,  
INCONSISTENT y REQUIRES_REVIEW.

### **8.2. accounting_integration_source**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| integration_id              | uuid        | N               |
| source_code                 | varchar(80) | N               |
| required                    | boolean     | N               |
| fact_id, antecedent_id      | uuid        | S               |
| status                      | varchar(20) | N               |
| checks, evidence_references | jsonb       | N               |

PK (integration_id, source_code).

FK a integración y fuente local correspondiente.

Estados MISSING, VERIFIED y CONTRADICTORY.

VERIFIED exige exactamente un hecho o antecedente.

Un bruto 500 y neto 490 sin retenciones acreditadas por 10 permanece inconsistente.  
No se inventa comisión.

## **9. Controles locales de períodos**

### **9.1. ledger_period_control**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| period_id                      | uuid        | N, PK           |
| period_type                    | varchar(10) | N               |
| parent_period_id               | uuid        | S               |
| start_date, end_date_exclusive | date        | N               |
| applied_control_version        | bigint      | N               |
| mode                           | varchar(30) | N               |
| current_application_id         | uuid        | N               |
| applied_at                     | timestamptz | N               |
| confirmed_cut_sequence         | bigint      | S               |
| lock_version                   | bigint      | N               |

FK local al período superior.

Tipos DAY, MONTH y YEAR.

Modos ORDINARY, IDENTIFIED_REGULARIZATIONS y SEALED.

La jerarquía exige día dentro de mes y mes dentro de ejercicio.

Se excluyen intervalos superpuestos del mismo tipo.

### **9.2. ledger_period_control_application**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| application_id, period_id | uuid        | N               |
| source_service            | varchar(80) | N               |
| source_control_version    | bigint      | N               |
| requested_mode            | varchar(30) | N               |
| requested_content         | jsonb       | N               |
| applied_at                | timestamptz | S               |
| result                    | varchar(20) | N               |
| resulting_cut_reference   | jsonb       | S               |
| T, S                      | Bloques     |                 |

PK application_id, FK al control.

UQ (period_id, source_control_version).

Resultados APPLIED, REJECTED y REQUIRES_REVIEW.

Solo Cierres contables puede originar estas aplicaciones.

### **9.3. ledger_period_regularization**

| **Columna**                       | **Tipo** | **Nulabilidad** |
|-----------------------------------|----------|-----------------|
| period_id                         | uuid     | N               |
| control_version                   | bigint   | N               |
| regularization_id, consequence_id | uuid     | N               |
| permitted_entry_types             | jsonb    | N               |
| approval_reference                | jsonb    | N               |
| maximum_scope                     | jsonb    | N               |

PK (period_id, control_version, regularization_id).

FK a período y consecuencia.

IDENTIFIED_REGULARIZATIONS admite únicamente los tratamientos registrados  
y compatibles con todos los controles superiores.

Una versión antigua no reabre el período.

## **10. Secuencia y asientos**

### **10.1. ledger_posting_control**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| control_id            | smallint    | N, PK           |
| last_posting_sequence | bigint      | N               |
| lock_version          | bigint      | N               |
| updated_at            | timestamptz | N               |

Fila única con valor 1, creada por migración.

El contador se incrementa dentro de la transacción de confirmación.  
No utiliza una secuencia PostgreSQL independiente.

### **10.2. accounting_posting_batch**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| batch_id, consequence_id      | uuid        | N               |
| integration_id                | uuid        | S               |
| posted_at                     | timestamptz | N               |
| first_sequence, last_sequence | bigint      | N               |
| entries_hash                  | char(64)    | N               |
| T                             | Bloque      |                 |

PK batch_id.

FK a consecuencia e integración.

UQ sobre consecuencia.

El conjunto completo de asientos de una consecuencia confirma atómicamente.

### **10.3. accounting_entry**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| entry_id, batch_id, consequence_id                  | uuid          | N               |
| entry_version                                       | bigint        | N               |
| entry_position                                      | integer       | N               |
| ledger_code                                         | varchar(80)   | N               |
| entry_type                                          | varchar(30)   | N               |
| fact_id, integration_id                             | uuid          | S               |
| rule_id                                             | uuid          | S               |
| rule_version                                        | bigint        | S               |
| economic_date, posting_date                         | date          | N               |
| source_received_at, posted_at                       | timestamptz   | N               |
| accounting_period_id                                | uuid          | N               |
| period_control_version                              | bigint        | N               |
| ledger_sequence                                     | bigint        | N               |
| presentation_debit_total, presentation_credit_total | numeric(38,2) | N               |
| approval_reference                                  | jsonb         | S               |
| correction_references                               | jsonb         | N               |
| T                                                   | Bloque        |                 |

PK entry_id.

FK a lote, consecuencia, fuentes, regla y período locales.

UQ sobre secuencia y (batch_id, entry_position).

Tipos AUTOMATIC, MANUAL, REVERSAL, RECLASSIFICATION, REVALUATION  
y YEAR_END_RESULT_TRANSFER.

Solo existen en esta tabla asientos confirmados.  
Los borradores se conservan en propuestas.

## **11. Partidas y dimensiones**

### **11.1. accounting_entry_line**

| **Columna**                          | **Tipo**       | **Nulabilidad** |
|--------------------------------------|----------------|-----------------|
| line_id, entry_id, ledger_account_id | uuid           | N               |
| line_number                          | integer        | N               |
| account_version                      | bigint         | N               |
| side                                 | varchar(10)    | N               |
| original_amount                      | numeric(24,2)  | N               |
| original_effect_amount               | numeric(24,2)  | N               |
| original_currency                    | varchar(3)     | N               |
| presentation_amount                  | numeric(24,2)  | N               |
| presentation_currency                | varchar(3)     | N               |
| exchange_rate_reference              | jsonb          | S               |
| exchange_rate                        | numeric(24,12) | S               |
| economic_component_reference         | jsonb          | S               |
| description                          | varchar(500)   | S               |
| effect_mode                          | varchar(30)    | N               |
| dimension_set_id                     | uuid           | N               |

PK line_id.

FK a asiento, cuenta versionada y combinación de dimensiones.

UQ (entry_id, line_number).

Sentido DEBIT o CREDIT.  
Moneda de presentación exclusivamente ARS.

Modalidades:

- ORIGINAL_AND_PRESENTATION: el efecto original coincide con el importe original.

- PRESENTATION_ONLY: el importe original conserva la base de referencia  
  > y su efecto sobre moneda original es cero.

La segunda modalidad se utiliza exclusivamente para reexpresión autorizada.

Para ARS ordinario coinciden importe original y presentación.

Una conversión válida puede dar presentación cero.  
Se conserva el importe original sin inventar un centavo.

### **11.2. accounting_entry_line_unit**

| **Columna**                          | **Tipo**       | **Nulabilidad** |
|--------------------------------------|----------------|-----------------|
| line_id, entry_id, ledger_account_id | uuid           | N               |
| line_number                          | integer        | N               |
| account_version                      | bigint         | N               |
| side                                 | varchar(10)    | N               |
| quantity                             | numeric(30,12) | N               |
| instrument_reference                 | jsonb          | N               |
| instrument_identity_hash             | char(64)       | N               |
| unit_code                            | varchar(80)    | N               |
| dimension_set_id                     | uuid           | N               |

PK line_id.

FK a asiento, cuenta versionada y dimensiones.

UQ (entry_id, line_number).

Solo se admite en libro de orden y con cuentas compatibles.

Las líneas de unidades no se suman a totales monetarios.  
Se equilibran por instrumento y unidad.

La numeración es única entre todas las líneas del mismo asiento,  
comprobada antes de confirmar el conjunto.

### **11.3. accounting_dimension_set**

Columnas N:

- dimension_set_id uuid, PK.

- canonical_dimensions jsonb.

- dimensions_hash char(64), UQ.

- schema_version bigint.

La normalización ordena códigos y referencias.  
Una coincidencia de hash exige comprobar también el contenido.

Existe una combinación vacía estable para partidas sin dimensiones.

### **11.4. accounting_dimension_set_item**

| **Columna**        | **Tipo**    | **Nulabilidad** |
|--------------------|-------------|-----------------|
| dimension_set_id   | uuid        | N               |
| dimension_code     | varchar(80) | N               |
| definition_version | bigint      | N               |
| value_reference    | jsonb       | N               |

PK sobre combinación y código.

FK a combinación y definición de dimensión.

### **11.5. accounting_entry_line_dimension**

Columnas N:

- line_id uuid.

- dimension_code varchar(80).

- definition_version bigint.

- value_reference jsonb.

PK sobre línea y código.

FK a línea monetaria y definición.  
Las líneas de unidades conservan su combinación mediante dimension_set_id.

La combinación y las dimensiones materializadas deben coincidir.

## **12. Saldos y equilibrio**

### **12.1. ledger_balance**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| balance_id                                | uuid          | N, PK           |
| ledger_code                               | varchar(80)   | N               |
| ledger_account_id, dimension_set_id       | uuid          | N               |
| original_currency                         | varchar(3)    | N               |
| original_debits, original_credits         | numeric(38,2) | N               |
| original_balance                          | numeric(38,2) | N               |
| presentation_debits, presentation_credits | numeric(38,2) | N               |
| presentation_balance                      | numeric(38,2) | N               |
| last_posting_sequence                     | bigint        | N               |
| lock_version                              | bigint        | N               |

FK a cuenta y dimensiones.

UQ (ledger_code, ledger_account_id, original_currency, dimension_set_id).

Los saldos utilizan convención firmada débito menos crédito.  
El saldo normal define su interpretación.

### **12.2. ledger_unit_balance**

| **Columna**                                       | **Tipo**       | **Nulabilidad** |
|---------------------------------------------------|----------------|-----------------|
| balance_id                                        | uuid           | N, PK           |
| ledger_code                                       | varchar(80)    | N               |
| ledger_account_id, dimension_set_id               | uuid           | N               |
| instrument_identity_hash                          | char(64)       | N               |
| instrument_reference                              | jsonb          | N               |
| unit_code                                         | varchar(80)    | N               |
| debit_quantity, credit_quantity, balance_quantity | numeric(38,12) | N               |
| last_posting_sequence                             | bigint         | N               |
| lock_version                                      | bigint         | N               |

UQ por libro, cuenta, dimensiones, instrumento y unidad.

### **12.3. Confirmación**

Se comprueban:

- Cuentas de movimiento habilitadas.

- Versiones y vigencias.

- Monedas y dimensiones.

- Integridad de fuentes.

- Equilibrio en ARS por libro.

- Política multimoneda.

- Equilibrio de registros de orden por instrumento y unidad.

- Admisión del período.

Un libro de orden no compensa un desequilibrio patrimonial.

Consecuencia, lote, asientos, partidas, saldos, secuencias, historia y outbox  
confirman en una sola transacción.

## **13. Ajustes manuales y correcciones**

### **13.1. manual_accounting_entry_proposal**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| proposal_id                              | uuid          | N, PK           |
| proposal_version                         | bigint        | N               |
| economic_date, posting_date              | date          | N               |
| accounting_period_id                     | uuid          | N               |
| proposed_lines                           | jsonb         | N               |
| reason                                   | varchar(4000) | N               |
| document_references, original_references | jsonb         | N               |
| validation_results                       | jsonb         | N               |
| preparer_person_id                       | uuid          | N               |
| status                                   | varchar(20)   | N               |
| T, S                                     | Bloques       |                 |
| lock_version                             | bigint        | N               |

FK a control local de período.

Estados DRAFT, SUBMITTED, APPROVED, POSTED y REJECTED.

### **13.2. manual_accounting_entry_approval**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| approval_id, proposal_id               | uuid        | N               |
| proposal_version                       | bigint      | N               |
| approved_content_hash                  | char(64)    | N               |
| preparer_person_id, approver_person_id | uuid        | N               |
| approval_reference                     | jsonb       | N               |
| approved_at                            | timestamptz | N               |
| T                                      | Bloque      |                 |

PK approval_id, FK a propuesta.

Personas distintas.  
Cambiar partidas exige nueva aprobación.

### **13.3. Controles de alcance corregible**

**accounting_line_correction_control**

Conserva el alcance monetario corregible de una partida original.

| **Columna**                     | **Tipo**      | **Nulabilidad** |
|---------------------------------|---------------|-----------------|
| original_line_id                | uuid          | N, PK           |
| correctable_original_amount     | numeric(24,2) | N               |
| corrected_original_amount       | numeric(24,2) | N               |
| pending_original_amount         | numeric(24,2) | N               |
| remaining_original_amount       | numeric(24,2) | N               |
| correctable_presentation_amount | numeric(24,2) | N               |
| corrected_presentation_amount   | numeric(24,2) | N               |
| pending_presentation_amount     | numeric(24,2) | N               |
| remaining_presentation_amount   | numeric(24,2) | N               |
| lock_version                    | bigint        | N               |

FK local:

original_line_id referencia accounting_entry_line(line_id).

La eliminación de la partida referenciada está restringida.

Todos los importes del control son no negativos.  
lock_version es no negativo.

Se cumplen separadamente:

- Correctible original = corregido original + pendiente original + remanente original.

- Correctible de presentación = corregido de presentación + pendiente de presentación + remanente de presentación.

Una corrección identifica el alcance que afecta en cada moneda.  
No convierte automáticamente un importe original en su importe de presentación actual.

La fila se bloquea antes de reservar, confirmar o liberar alcance corregible.

**accounting_unit_correction_control**

Conserva el alcance corregible de una partida de unidades.

| **Columna**          | **Tipo**       | **Nulabilidad** |
|----------------------|----------------|-----------------|
| original_line_id     | uuid           | N, PK           |
| correctable_quantity | numeric(30,12) | N               |
| corrected_quantity   | numeric(30,12) | N               |
| pending_quantity     | numeric(30,12) | N               |
| remaining_quantity   | numeric(30,12) | N               |
| lock_version         | bigint         | N               |

FK local:

original_line_id referencia accounting_entry_line_unit(line_id).

La eliminación de la partida referenciada está restringida.

Todas las cantidades son no negativas.  
lock_version es no negativo.

Se exige:

Correctible = corregido + pendiente + remanente.

La cantidad correctible no supera la cantidad de la partida original.

El instrumento y la unidad son los de la partida original.  
Una corrección no cambia esas identidades para consumir otro alcance.

Las correcciones concurrentes bloquean la misma fila de control  
antes de reservar o aplicar cantidades.

Al preparar una corrección se reduce remanente y aumenta pendiente.  
Al confirmarla se reduce pendiente y aumenta corregido.  
Al rechazarla o cancelarla de forma comprobada se reduce pendiente y se restituye remanente.

El vencimiento de un trabajador no libera una corrección cuyo resultado permanece incierto.

Las PK proporcionan la búsqueda por partida original.  
No se crean índices adicionales que dupliquen esas claves.

La modificación del control, el resultado de la corrección, los asientos resultantes,  
la historia y la outbox se confirman conjuntamente cuando corresponde su aplicación.

### **13.4. accounting_entry_correction**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| correction_id, original_entry_id          | uuid          | N               |
| correction_type                           | varchar(20)   | N               |
| scope                                     | varchar(10)   | N               |
| reason                                    | varchar(4000) | N               |
| original_line_scopes, evidence_references | jsonb         | N               |
| manual                                    | boolean       | N               |
| approval_reference                        | jsonb         | S               |
| resulting_batch_id                        | uuid          | S               |
| status                                    | varchar(20)   | N               |
| T, C, S                                   | Bloques       |                 |

PK correction_id.

FK a original y lote resultante.

Tipos REVERSE y RECLASSIFY.  
Alcance TOTAL o PARTIAL.

Estados PENDING, POSTED, REJECTED y REQUIRES_REVIEW.

La parcial identifica líneas e importes y mantiene equilibrio.  
La corrección manual exige aprobación.

El original permanece inmutable.

## **14. Reexpresión y acumulados**

### **14.1. accounting_revaluation**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| revaluation_id, cut_id                     | uuid        | N               |
| policy_reference, exchange_rate_references | jsonb       | N               |
| status                                     | varchar(20) | N               |
| missing_data_codes                         | jsonb       | N               |
| T, C, S                                    | Bloques     |                 |

PK revaluation_id, FK al corte.

Estados DETERMINED, POSTED, INCOMPLETE y REQUIRES_REVIEW.

### **14.2. accounting_revaluation_item**

| **Columna**                                  | **Tipo**       | **Nulabilidad** |
|----------------------------------------------|----------------|-----------------|
| revaluation_id, balance_id                   | uuid           | N               |
| original_amount                              | numeric(38,2)  | N               |
| previous_presentation, required_presentation | numeric(38,2)  | N               |
| presentation_difference                      | numeric(38,2)  | N               |
| exchange_rate                                | numeric(24,12) | S               |
| exchange_rate_reference                      | jsonb          | S               |
| resulting_entry_id                           | uuid           | S               |
| missing_data_codes                           | jsonb          | N               |

PK sobre reexpresión y saldo.

FK a reexpresión, saldo y asiento.

UQ de negocio por saldo, corte y tratamiento de reexpresión,  
comprobada mediante su determinación.

Una posición USD 100 mantiene USD 100.  
Solo cambia el equivalente ARS.

### **14.3. accounting_recognition_control**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| recognition_control_id      | uuid          | N, PK           |
| owner_service, concept_code | varchar(80)   | N               |
| period_from, period_to      | date          | N               |
| scope_hash                  | char(64)      | N               |
| scope_payload               | jsonb         | N               |
| recognized_amount           | numeric(38,2) | N               |
| last_source_version         | bigint        | N               |
| lock_version                | bigint        | N               |

UQ (owner_service, concept_code, period_from, period_to, scope_hash).

### **14.4. accounting_recognition_application**

| **Columna**                                            | **Tipo**      | **Nulabilidad** |
|--------------------------------------------------------|---------------|-----------------|
| application_id, recognition_control_id, consequence_id | uuid          | N               |
| source_mode                                            | varchar(20)   | N               |
| source_version                                         | bigint        | N               |
| reported_amount, applied_variation                     | numeric(24,2) | N               |
| resulting_batch_id                                     | uuid          | S               |
| T                                                      | Bloque        |                 |

PK application_id.

FK a control, consecuencia y lote.

UQ (recognition_control_id, source_version).

SOURCE_DELTA aplica variación.  
SOURCE_CUMULATIVE determina diferencia contra el acumulado reconocido.

Pasar de 20 a 25 registra 5, no 25 adicionales.

El control se actualiza junto con el asiento.

## **15. Cortes y lectura autoritativa**

### **15.1. accounting_cut**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| cut_id                   | uuid        | N, PK           |
| cut_at                   | timestamptz | N               |
| maximum_posting_sequence | bigint      | N               |
| period_id                | uuid        | S               |
| period_control_version   | bigint      | S               |
| scope_payload            | jsonb       | N               |
| totals, source_cuts      | jsonb       | N               |
| completeness             | varchar(20) | N               |
| content_hash             | char(64)    | N               |
| schema_version           | bigint      | N               |
| T                        | Bloque      |                 |

FK al período local cuando corresponda.

Completitud COMPLETE o INCOMPLETE.

### **15.2. Instantáneas de saldos**

**accounting_balance_snapshot**

Conserva los saldos monetarios correspondientes a un corte.

| **Columna**          | **Tipo**      | **Nulabilidad** |
|----------------------|---------------|-----------------|
| cut_id               | uuid          | N               |
| balance_id           | uuid          | N               |
| ledger_code          | varchar(80)   | N               |
| ledger_account_id    | uuid          | N               |
| dimension_set_id     | uuid          | N               |
| original_currency    | varchar(3)    | N               |
| original_debits      | numeric(38,2) | N               |
| original_credits     | numeric(38,2) | N               |
| original_balance     | numeric(38,2) | N               |
| presentation_debits  | numeric(38,2) | N               |
| presentation_credits | numeric(38,2) | N               |
| presentation_balance | numeric(38,2) | N               |

PK (cut_id, balance_id).

FK locales:

| **Columna**       | **Destino**                                |
|-------------------|--------------------------------------------|
| cut_id            | accounting_cut(cut_id)                     |
| balance_id        | ledger_balance(balance_id)                 |
| ledger_account_id | ledger_account(ledger_account_id)          |
| dimension_set_id  | accounting_dimension_set(dimension_set_id) |

Todas utilizan eliminación restringida.

Libro, cuenta, moneda y dimensiones deben coincidir con la identidad del saldo referenciado.

Los débitos y créditos acumulados son no negativos.  
Los saldos admiten signo y cumplen débito menos crédito.

La instantánea conserva los valores del corte.  
Una modificación posterior de ledger_balance no cambia sus valores históricos.

**accounting_unit_balance_snapshot**

Conserva los saldos de unidades correspondientes a un corte,  
separados de los saldos monetarios.

| **Columna**              | **Tipo**       | **Nulabilidad** |
|--------------------------|----------------|-----------------|
| cut_id                   | uuid           | N               |
| balance_id               | uuid           | N               |
| ledger_code              | varchar(80)    | N               |
| ledger_account_id        | uuid           | N               |
| dimension_set_id         | uuid           | N               |
| instrument_identity_hash | char(64)       | N               |
| instrument_reference     | jsonb          | N               |
| unit_code                | varchar(80)    | N               |
| debit_quantity           | numeric(38,12) | N               |
| credit_quantity          | numeric(38,12) | N               |
| balance_quantity         | numeric(38,12) | N               |
| last_posting_sequence    | bigint         | N               |

PK (cut_id, balance_id).

FK locales:

| **Columna**       | **Destino**                                |
|-------------------|--------------------------------------------|
| cut_id            | accounting_cut(cut_id)                     |
| balance_id        | ledger_unit_balance(balance_id)            |
| ledger_account_id | ledger_account(ledger_account_id)          |
| dimension_set_id  | accounting_dimension_set(dimension_set_id) |

Todas utilizan eliminación restringida.

La identidad del saldo está formada por libro, cuenta, dimensiones, instrumento y unidad.  
Estos atributos coinciden con los del saldo referenciado al materializar el corte.

instrument_reference conserva una referencia tipada y versionada.  
Su identidad coincide con instrument_identity_hash.

debit_quantity y credit_quantity son no negativas.

balance_quantity admite signo y cumple:

Saldo de unidades = cantidad debitada − cantidad acreditada.

last_posting_sequence conserva la última registración incorporada en ese saldo  
y no supera accounting_cut.maximum_posting_sequence.

Las cantidades mantienen su instrumento y unidad.  
No se convierten en importes monetarios ni se suman a los totales de presentación en ARS.

Las filas de ambas tablas son inmutables una vez confirmado el corte.

Índices complementarios:

| **Tabla**                        | **Índice**           | **Finalidad**                               |
|----------------------------------|----------------------|---------------------------------------------|
| accounting_balance_snapshot      | (balance_id, cut_id) | Historia de cortes de un saldo monetario.   |
| accounting_unit_balance_snapshot | (balance_id, cut_id) | Historia de cortes de un saldo de unidades. |

Las PK cubren la lectura por corte y saldo.

La materialización confirma el corte y sus instantáneas en una lectura consistente.  
La recuperación de una respuesta perdida devuelve el mismo corte sin actualizar sus valores históricos.

### **15.3. Consistencia**

El corte fija el contador confirmado y obtiene saldos en una lectura consistente,  
manteniendo estable el control de confirmación durante su materialización.

Las partidas se consultan con secuencia no superior al corte.

Las páginas conservan corte, filtros y última posición.  
Los asientos posteriores no ingresan en páginas siguientes.

Cierres e Información utilizan interfaces autorizadas.  
No consultan la base de Registración.

## **16. Períodos en Cierres contables**

### **16.1. accounting_period_catalog_control**

Columnas N:

- control_id smallint, PK con valor 1.

- lock_version bigint.

- updated_at timestamptz.

Serializa altas y cambios de jerarquía temporal.

### **16.2. accounting_period**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| period_id                             | uuid        | N, PK           |
| period_type                           | varchar(10) | N               |
| parent_period_id                      | uuid        | S               |
| start_date, end_date_exclusive        | date        | N               |
| status                                | varchar(20) | N               |
| control_version                       | bigint      | N               |
| current_control_application_reference | jsonb       | S               |
| incompatible_change_pending           | boolean     | N               |
| definitive_emission_recorded          | boolean     | N               |
| T, C                                  | Bloques     |                 |

FK al superior.

Tipos DAY, MONTH y YEAR.

Estados OPEN, CLOSING, CLOSED y REOPENING.

Se excluyen intervalos superpuestos por nivel.

El ejercicio abarca del 1 de enero al 1 de enero siguiente.  
Meses y días deben estar contenidos en superiores compatibles.

### **16.3. accounting_period_transition**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| transition_id, period_id                         | uuid        | N               |
| source_period_version, requested_control_version | bigint      | N               |
| previous_status, requested_status                | varchar(20) | N               |
| requested_mode                                   | varchar(30) | N               |
| approval_reference                               | jsonb       | S               |
| requested_content                                | jsonb       | N               |
| ledger_application_id, remote_action_id          | uuid        | N               |
| ledger_result_reference                          | jsonb       | S               |
| status                                           | varchar(30) | N               |
| T, C, S                                          | Bloques     |                 |

PK transition_id, FK a período.

UQ (period_id, requested_control_version) y sobre actuación remota.

Estados PENDING, APPLIED y REQUIRES_REVIEW.

Una transición no se informa completada hasta comprobar la barrera en Registración.

## **17. Integridad de fuentes**

### **17.1. accounting_source_integrity_confirmation**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| confirmation_id          | uuid        | N, PK           |
| source_service           | varchar(80) | N               |
| source_confirmation_id   | uuid        | N               |
| scope_code               | varchar(80) | N               |
| economic_date            | date        | N               |
| source_cut               | jsonb       | N               |
| coverage_mode            | varchar(20) | N               |
| declared_fact_count      | bigint      | N               |
| declared_hash            | char(64)    | N               |
| known_pending_components | jsonb       | N               |
| status                   | varchar(20) | N               |
| T, C, S                  | Bloques     |                 |

UQ (source_service, source_confirmation_id).

Modos EXPLICIT_SET, SEQUENCE_RANGES y EMPTY.

Estados RECEIVED, VERIFIED, INCOMPLETE y CONTRADICTORY.

EMPTY exige cantidad cero, sin elementos ni rangos.

### **17.2. accounting_integrity_fact_reference**

Columnas N:

- confirmation_id uuid.

- fact_owner_service varchar(80).

- source_fact_id uuid.

PK sobre las tres columnas.  
FK a confirmación.

### **17.3. accounting_integrity_sequence_range**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| confirmation_id               | uuid        | N               |
| range_position                | integer     | N               |
| stream_code                   | varchar(80) | N               |
| first_sequence, last_sequence | bigint      | N               |

PK sobre confirmación y posición.

FK a confirmación.  
Extremos positivos e inclusivos.

Se excluyen rangos superpuestos del mismo flujo y declaración.

### **17.4. accounting_integrity_verification**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| verification_id, confirmation_id    | uuid        | N               |
| ledger_cut_reference                | jsonb       | N               |
| received_coverage, treated_coverage | jsonb       | N               |
| missing_facts, contradictory_facts  | jsonb       | N               |
| result                              | varchar(20) | N               |
| evidence_references                 | jsonb       | N               |
| T, S                                | Bloques     |                 |

PK verification_id, FK a confirmación.

Resultados COMPLETE, INCOMPLETE y CONTRADICTORY.

Una secuencia solo representa su flujo declarado.  
Una fuente sin respuesta no es una fuente vacía.

## **18. Auxiliares y correspondencias**

### **18.1. accounting_auxiliary_snapshot**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| auxiliary_id               | uuid        | N               |
| auxiliary_version          | bigint      | N               |
| source_service             | varchar(80) | N               |
| source_snapshot_id         | uuid        | N               |
| auxiliary_type, scope_code | varchar(80) | N               |
| cut_at                     | timestamptz | N               |
| source_cut_reference       | jsonb       | N               |
| completeness               | varchar(20) | N               |
| pending_components         | jsonb       | N               |
| T, S                       | Bloques     |                 |

PK sobre auxiliar y versión.

UQ (source_service, source_snapshot_id, auxiliary_version).

### **18.2. accounting_auxiliary_record**

| **Columna**          | **Tipo**       | **Nulabilidad** |
|----------------------|----------------|-----------------|
| auxiliary_id         | uuid           | N               |
| auxiliary_version    | bigint         | N               |
| record_position      | bigint         | N               |
| concept_code         | varchar(80)    | N               |
| dimensions           | jsonb          | N               |
| mode                 | varchar(10)    | N               |
| signed_amount        | numeric(24,2)  | S               |
| currency             | varchar(3)     | S               |
| quantity             | numeric(30,12) | S               |
| instrument_reference | jsonb          | S               |
| unit_code            | varchar(80)    | S               |

PK sobre auxiliar, versión y posición.

FK a instantánea.

MONETARY y UNITS tienen presencia excluyente de sus campos.

### **18.3. accounting_reconciliation_mapping**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| mapping_id                              | uuid        | N               |
| mapping_version                         | bigint      | N               |
| auxiliary_type                          | varchar(80) | N               |
| source_concept_code                     | varchar(80) | N               |
| accounting_selection, dimension_mapping | jsonb       | N               |
| comparison_mode                         | varchar(20) | N               |
| approval_reference                      | jsonb       | N               |
| T, S                                    | Bloques     |                 |

PK sobre identidad y versión.

Modos MONETARY y UNITS.

Solo se comparan conceptos equivalentes.  
Disponibilidad y saldo registrado no representan la misma obligación.

## **19. Conciliaciones y diferencias**

### **19.1. accounting_reconciliation**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| reconciliation_id    | uuid        | N, PK           |
| scope_code           | varchar(80) | N               |
| ledger_cut_reference | jsonb       | N               |
| mapping_id           | uuid        | N               |
| mapping_version      | bigint      | N               |
| auxiliary_references | jsonb       | N               |
| cut_comparability    | jsonb       | N               |
| status               | varchar(20) | N               |
| T, C, S              | Bloques     |                 |

FK a correspondencia.

Estados RUNNING, MATCHED, DIFFERENCES e INCOMPLETE.

### **19.2. accounting_reconciliation_item**

| **Columna**                                              | **Tipo**       | **Nulabilidad** |
|----------------------------------------------------------|----------------|-----------------|
| item_id, reconciliation_id                               | uuid           | N               |
| position                                                 | integer        | N               |
| concept_code                                             | varchar(80)    | N               |
| compared_references                                      | jsonb          | N               |
| ledger_amount, auxiliary_amount, amount_difference       | numeric(38,2)  | S               |
| ledger_quantity, auxiliary_quantity, quantity_difference | numeric(38,12) | S               |
| currency, unit_code                                      | varchar(80)    | S               |
| instrument_reference                                     | jsonb          | S               |
| completeness                                             | varchar(20)    | N               |

PK item_id, FK a conciliación.

UQ (reconciliation_id, position).

Importes y diferencias admiten signo.  
La variante monetaria no contiene cantidades y la de unidades no contiene importes.

### **19.3. accounting_difference**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| difference_id, reconciliation_item_id | uuid        | N               |
| diagnosis                             | varchar(30) | N               |
| source_reference                      | jsonb       | N               |
| treatment_owner_service               | varchar(80) | N               |
| status                                | varchar(20) | N               |
| T, C                                  | Bloques     |                 |

PK difference_id, FK al elemento.

Diagnósticos TEMPORAL, MISSING_FACT, DUPLICATE, AMOUNT_MISMATCH,  
MISCLASSIFICATION, INCOMPLETE_SOURCE y UNDETERMINED.

Estados OPEN, DIAGNOSED, TREATMENT_PENDING y RESOLVED.

### **19.4. accounting_difference_treatment**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| treatment_id, difference_id               | uuid        | N               |
| treatment_type                            | varchar(40) | N               |
| proposed_actions, authorization_reference | jsonb       | N               |
| responsible_service                       | varchar(80) | N               |
| target_date                               | date        | N               |
| evidence_references                       | jsonb       | N               |
| status                                    | varchar(20) | N               |
| T, C, S                                   | Bloques     |                 |

PK treatment_id, FK a diferencia.

Tipos CORRECT_SOURCE, INCORPORATE_FACT, CORRECT_ENTRY,  
AUTHORIZED_TIMING_DIFFERENCE y CONTINUE_INVESTIGATION.

Estados AUTHORIZED, EXECUTION_PENDING y EXECUTED.

### **19.5. accounting_difference_verification**

Columnas N:

- verification_id uuid, PK.

- difference_id uuid, FK.

- treatment_id uuid, FK.

- result varchar(20).

- evidence_references jsonb.

- verified_at timestamptz.

- Bloque T.

Resultados SATISFIED, PENDING y FAILED.

Solo SATISFIED permite marcar RESOLVED.

No existe un importe mínimo para ignorar diferencias.

## **20. Cierres contables**

### **20.1. accounting_close**

| **Columna**                          | **Tipo**    | **Nulabilidad** |
|--------------------------------------|-------------|-----------------|
| close_id, period_id                  | uuid        | N               |
| close_type                           | varchar(10) | N               |
| economic_date                        | date        | S               |
| cut_reference                        | jsonb       | S               |
| status                               | varchar(30) | N               |
| controls_hash, balances_hash         | char(64)    | S               |
| approved_close_version               | bigint      | S               |
| approval_reference                   | jsonb       | S               |
| previous_close_id                    | uuid        | S               |
| sealed_control_application_reference | jsonb       | S               |
| closed_at                            | timestamptz | S               |
| T, C                                 | Bloques     |                 |

PK close_id.

FK a período y cierre sustituido.

Tipos DAILY, MONTHLY y YEARLY.  
DAILY exige fecha económica.

Estados STARTED, BARRIER_PENDING, CHECKING, REGULARIZING,  
APPROVAL_PENDING, SEALING, CLOSED y REQUIRES_REVIEW.

UQ parcial sobre período mientras el cierre no sea CLOSED.

### **20.2. accounting_close_control**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| close_id                          | uuid        | N               |
| close_version                     | bigint      | N               |
| control_code                      | varchar(80) | N               |
| result                            | varchar(10) | N               |
| evidence_references, observations | jsonb       | N               |

PK sobre cierre, versión y código.  
FK a cierre.

Resultados PASSED, FAILED y PENDING.

### **20.3. accounting_close_source**

| **Columna**                                                | **Tipo** | **Nulabilidad** |
|------------------------------------------------------------|----------|-----------------|
| close_id                                                   | uuid     | N               |
| close_version                                              | bigint   | N               |
| source_position                                            | integer  | N               |
| integrity_confirmation_id, auxiliary_id, reconciliation_id | uuid     | S               |
| auxiliary_version                                          | bigint   | S               |

PK sobre cierre, versión y posición.

FK a cierre y fuente local correspondiente.

Exactamente una clase de fuente está presente.

### **20.4. accounting_close_admitted_pending**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| pending_id, close_id                     | uuid        | N               |
| close_version                            | bigint      | N               |
| cause_code                               | varchar(80) | N               |
| scope_payload                            | jsonb       | N               |
| responsible_service                      | varchar(80) | N               |
| authorized_treatment, approval_reference | jsonb       | N               |
| T                                        | Bloque      |                 |

PK pending_id, FK a cierre.

Un pendiente admitido debe estar correctamente representado.  
No permite omitir un hecho conocido.

### **20.5. Confirmación**

El cierre conserva barrera, corte, integridad, conciliaciones, regularizaciones,  
repetición de controles, aprobación y sellado.

La aprobación vincula versión y huella.  
Un cambio material exige repetir comprobaciones y aprobación.

CLOSED requiere sellado comprobado, corte y contenido aprobados.

No concluye con desequilibrio, duplicaciones confirmadas o hechos conocidos sin tratamiento.

## **21. Ejercicio, hechos tardíos y reapertura**

### **21.1. accounting_year_end_transfer**

| **Columna**                                    | **Tipo**    | **Nulabilidad** |
|------------------------------------------------|-------------|-----------------|
| transfer_id, year_period_id, close_id          | uuid        | N               |
| cut_reference, calculation, approval_reference | jsonb       | N               |
| consequence_reference, entry_reference         | jsonb       | S               |
| status                                         | varchar(20) | N               |
| T, C, S                                        | Bloques     |                 |

PK transfer_id, FK a ejercicio y cierre.

UQ sobre ejercicio.

La transferencia de resultados no distribuye ganancias ni crea otro ingreso operativo.

### **21.2. accounting_opening_balance_reference**

Columnas N:

- year_period_id uuid, PK y FK.

- previous_close_id uuid, FK.

- closing_cut_reference jsonb.

- continuity_hash char(64).

- Bloque T.

La apertura conserva continuidad sin duplicar patrimonio.

### **21.3. late_accounting_fact_treatment**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| treatment_id                             | uuid        | N, PK           |
| fact_reference                           | jsonb       | N               |
| economic_date                            | date        | N               |
| actual_received_at                       | timestamptz | N               |
| affected_periods, impact, issued_reports | jsonb       | N               |
| treatment_type                           | varchar(30) | N               |
| approval_reference, controls_to_repeat   | jsonb       | N               |
| T, C, S                                  | Bloques     |                 |

Tipos REOPEN_DAY, REOPEN_PERIOD y PRIOR_PERIOD_ADJUSTMENT.

La recepción real nunca se altera.

### **21.4. accounting_reopening**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| reopening_id, period_id, previous_close_id | uuid        | N               |
| late_treatment_id                          | uuid        | N               |
| reason, scope_payload                      | jsonb       | N               |
| approval_reference                         | jsonb       | N               |
| transition_id                              | uuid        | S               |
| status                                     | varchar(30) | N               |
| T, C, S                                    | Bloques     |                 |

PK reopening_id.

FK a período, cierre, tratamiento y transición.

Una reapertura conserva cierres anteriores y registra el sustituto.

Un ejercicio emitido definitivamente utiliza ajuste identificado de períodos anteriores  
y evaluación de rectificaciones.

## **22. Estructuras de informes**

### **22.1. accounting_report_structure**

Columnas N:

- structure_id uuid, PK.

- code varchar(80), UQ.

- current_version bigint.

- Bloque T.

- lock_version bigint.

### **22.2. accounting_report_structure_version**

| **Columna**                                          | **Tipo**    | **Nulabilidad** |
|------------------------------------------------------|-------------|-----------------|
| structure_id                                         | uuid        | N               |
| structure_version                                    | bigint      | N               |
| report_type                                          | varchar(80) | N               |
| presentation_currency                                | varchar(3)  | N               |
| dimension_rules, comparative_rules, validation_rules | jsonb       | N               |
| valid_from                                           | date        | N               |
| valid_until                                          | date        | S               |
| status                                               | varchar(20) | N               |
| definition_revision                                  | bigint      | N               |
| preparer_person_id                                   | uuid        | N               |
| approver_person_id                                   | uuid        | S               |
| approval_reference                                   | jsonb       | S               |
| T, S                                                 | Bloques     |                 |

PK sobre estructura y versión.

Estados DRAFT, SUBMITTED, APPROVED, ENABLED y WITHDRAWN.

Moneda exclusivamente ARS.

Aprobar exige contenido exacto y personas distintas.  
El contenido aprobado es inmutable.

### **22.3. accounting_report_group**

| **Columna**       | **Tipo**     | **Nulabilidad** |
|-------------------|--------------|-----------------|
| structure_id      | uuid         | N               |
| structure_version | bigint       | N               |
| group_code        | varchar(80)  | N               |
| name              | varchar(200) | N               |
| position          | integer      | N               |
| parent_group_code | varchar(80)  | S               |
| ledger_scope_code | varchar(80)  | N               |

PK sobre estructura, versión y grupo.

FK a estructura y superior de la misma versión.

UQ de posición dentro de la estructura.

Se prohíben ciclos.  
Los registros de orden se presentan separadamente.

### **22.4. accounting_report_account_mapping**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| mapping_id, structure_id | uuid        | N               |
| structure_version        | bigint      | N               |
| group_code               | varchar(80) | N               |
| ledger_account_reference | jsonb       | N               |
| dimension_filter         | jsonb       | N               |
| sign_multiplier          | smallint    | N               |
| overlap_treatment        | varchar(20) | N               |

PK mapping_id, FK al grupo.

Multiplicador 1 o −1.

La habilitación verifica cobertura, omisiones y duplicaciones de correspondencias.

### **22.5. accounting_report_formula**

Columnas N:

- formula_id uuid, PK.

- structure_id uuid.

- structure_version bigint.

- target_group_code varchar(80).

- operation varchar(10).

FK al grupo y UQ por grupo de destino.

Operaciones SUM, SUBTRACT y NEGATE.

### **22.6. accounting_report_formula_operand**

| **Columna**        | **Tipo**    | **Nulabilidad** |
|--------------------|-------------|-----------------|
| formula_id         | uuid        | N               |
| position           | integer     | N               |
| operand_group_code | varchar(80) | N               |

PK sobre fórmula y posición.  
FK a fórmula.

El grupo debe pertenecer a la misma estructura y versión.  
Se comprueba bajo bloqueo de estructura.

NEGATE exige un operando.  
SUBTRACT exige dos.  
SUM exige al menos uno.

Se prohíben ciclos entre fórmulas.

## **23. Informes y cifras**

### **23.1. accounting_report**

| **Columna**      | **Tipo**    | **Nulabilidad** |
|------------------|-------------|-----------------|
| report_id        | uuid        | N, PK           |
| report_type      | varchar(80) | N               |
| period_reference | jsonb       | N               |
| current_version  | bigint      | N               |
| T                | Bloque      |                 |
| lock_version     | bigint      | N               |

### **23.2. accounting_report_version**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| report_id                           | uuid        | N               |
| report_version                      | bigint      | N               |
| structure_id                        | uuid        | N               |
| structure_version                   | bigint      | N               |
| accounting_cut                      | jsonb       | N               |
| plan_versions                       | jsonb       | N               |
| presentation_currency               | varchar(3)  | N               |
| report_condition                    | varchar(20) | N               |
| status                              | varchar(30) | N               |
| close_reference, approval_reference | jsonb       | S               |
| figures_hash                        | char(64)    | S               |
| issued_at                           | timestamptz | S               |
| previous_report_version             | bigint      | S               |
| T, S                                | Bloques     |                 |

PK sobre informe y versión.

FK a estructura y versión anterior del informe.

Condición PROVISIONAL o DEFINITIVE.

Estados COMPOSING, VALIDATING, READY_TO_ISSUE, ISSUED y REQUIRES_REVIEW.

DEFINITIVE exige cierre y aprobación antes de emitir.

ISSUED exige huella y fecha.  
El contenido emitido es inmutable.

### **23.3. accounting_report_figure**

| **Columna**             | **Tipo**      | **Nulabilidad** |
|-------------------------|---------------|-----------------|
| figure_id, report_id    | uuid          | N               |
| report_version          | bigint        | N               |
| figure_code, group_code | varchar(80)   | N               |
| amount                  | numeric(24,2) | S               |
| currency                | varchar(3)    | N               |
| completeness            | varchar(20)   | N               |
| missing_data_codes      | jsonb         | N               |

PK figure_id, FK a informe versionado.

UQ (report_id, report_version, figure_code).

Importe con signo.  
COMPLETE exige importe.  
Un dato faltante no se convierte en cero.

### **23.4. accounting_report_comparative**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| report_id              | uuid        | N               |
| report_version         | bigint      | N               |
| comparative_code       | varchar(80) | N               |
| source_report_id       | uuid        | S               |
| source_report_version  | bigint      | S               |
| source_cut             | jsonb       | S               |
| presentation_treatment | jsonb       | N               |

PK sobre informe, versión y código.

FK al informe y comparativo local cuando exista.

Exactamente uno de informe comparativo o corte está presente.

## **24. Procedencia y completitud**

### **24.1. accounting_figure_provenance**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| provenance_id, figure_id        | uuid        | N               |
| structure_reference, group_path | jsonb       | N               |
| ledger_cut, query_definition    | jsonb       | N               |
| query_result_hash               | char(64)    | N               |
| source_totals                   | jsonb       | N               |
| completeness                    | varchar(20) | N               |
| T, S                            | Bloques     |                 |

PK provenance_id, FK a cifra.

### **24.2. accounting_figure_source_line**

| **Columna**                      | **Tipo**      | **Nulabilidad** |
|----------------------------------|---------------|-----------------|
| provenance_id                    | uuid          | N               |
| position                         | bigint        | N               |
| ledger_service                   | varchar(80)   | N               |
| entry_id, line_id                | uuid          | N               |
| entry_sequence                   | bigint        | N               |
| source_reference, rule_reference | jsonb         | N               |
| contribution_amount              | numeric(24,2) | N               |

PK sobre procedencia y posición.

FK a procedencia.  
Las referencias contables son remotas.

La contribución admite signo.

### **24.3. accounting_projection_source_control**

| **Columna**                                    | **Tipo**    | **Nulabilidad** |
|------------------------------------------------|-------------|-----------------|
| source_service, stream_code                    | varchar(80) | N               |
| contiguous_sequence, highest_observed_sequence | bigint      | N               |
| pending_ranges                                 | jsonb       | N               |
| completeness                                   | varchar(20) | N               |
| lock_version                                   | bigint      | N               |
| updated_at                                     | timestamptz | N               |

PK sobre fuente y flujo.

Una secuencia alta no acredita continuidad de las anteriores.

### **24.4. accounting_report_source_verification**

| **Columna**                                         | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------------|-------------|-----------------|
| verification_id, report_id                          | uuid        | N               |
| report_version                                      | bigint      | N               |
| required_cut, reached_cut                           | jsonb       | N               |
| authoritative_totals, projected_totals, differences | jsonb       | N               |
| completeness                                        | varchar(20) | N               |
| evidence_references                                 | jsonb       | N               |
| T, S                                                | Bloques     |                 |

PK verification_id, FK al informe versionado.

Antes de emitir se comprueba corte y totales autoritativos.

Consultar un informe histórico devuelve sus cifras conservadas,  
no un recálculo actual.

## **25. Emisión definitiva y reapertura**

### **25.1. accounting_report_emission_attempt**

En Información contable:

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| attempt_id, report_id                 | uuid        | N               |
| attempt_type                          | varchar(80) | N               |
| report_version                        | bigint      | N               |
| exact_close_reference, accounting_cut | jsonb       | N               |
| structure_id                          | uuid        | N               |
| structure_version                     | bigint      | N               |
| figures_hash                          | char(64)    | N               |
| approval_reference                    | jsonb       | N               |
| close_preparation_reference           | jsonb       | S               |
| pending_decision                      | boolean     | N               |
| notification_state                    | varchar(20) | N               |
| result_reference                      | jsonb       | S               |

PK attempt_id.

FK al intento común, informe versionado y estructura.

Tipo ACCOUNTING_REPORT_EMISSION.

UQ parcial (report_id, report_version), donde pending_decision = true.

La coherencia con la decisión común se controla de forma diferida.

### **25.2. definitive_report_preparation_basis**

En Cierres:

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| preparation_id, period_id, close_id | uuid        | N               |
| period_version, close_version       | bigint      | N               |
| coordinator_service                 | varchar(80) | N               |
| emission_attempt_id, report_id      | uuid        | N               |
| report_version                      | bigint      | N               |
| accounting_cut, structure_reference | jsonb       | N               |
| figures_hash                        | char(64)    | N               |
| binding_content_hash                | char(64)    | N               |
| schema_version                      | bigint      | N               |

PK preparation_id.

FK a preparación común, período y cierre.

Índice (period_id, close_id, preparation_id).

La consulta de decisión se determina mediante servicio y operación registrados.  
No se recibe una URL arbitraria.

### **25.3. definitive_report_emission_record**

Columnas N:

- record_id uuid, PK.

- preparation_id uuid, FK y UQ.

- period_id uuid, FK.

- close_id uuid, FK.

- coordinator_service varchar(80).

- attempt_id uuid.

- report_reference jsonb.

- issued_at timestamptz.

- decision_evidence jsonb.

Conserva la emisión comprobada para el tratamiento de hechos posteriores.

### **25.4. Decisión**

Información registra el intento antes de solicitar preparación.

Confirmación y aborto utilizan:

1.  Informe.

2.  Identidad de ejecución.

3.  Intento.

4.  Versión y cifras vinculadas.

COMMITTED y emisión se confirman conjuntamente con resultado, historia,  
outbox y notificación pendiente.

Cierres ordena preparación y reapertura sobre los mismos controles de período,  
respetando ejercicio, mes y día.

Una reapertura incompatible bloquea nuevas preparaciones  
y solicita resolver las existentes.

No se libera preparación por timeout.  
Si la emisión confirmó primero, el aborto devuelve ALREADY_COMMITTED.

## **26. Documentos y rectificaciones**

### **26.1. accounting_report_document_request**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| document_request_id, report_id | uuid        | N               |
| report_version                 | bigint      | N               |
| emitted_content_hash           | char(64)    | N               |
| document_service               | varchar(80) | N               |
| remote_action_id               | uuid        | N               |
| document_reference             | jsonb       | S               |
| status                         | varchar(20) | N               |
| T, C, S                        | Bloques     |                 |

PK document_request_id, FK a informe versionado.

UQ (report_id, report_version) y (document_service, remote_action_id).

Estados PENDING, AVAILABLE, UNCERTAIN y REQUIRES_REVIEW.

Un fallo documental no crea otra emisión.

### **26.2. accounting_report_rectification**

| **Columna**                                       | **Tipo**      | **Nulabilidad** |
|---------------------------------------------------|---------------|-----------------|
| rectification_id, report_id                       | uuid          | N               |
| original_report_version, resulting_report_version | bigint        | N               |
| mode                                              | varchar(30)   | N               |
| reason                                            | varchar(4000) | N               |
| approval_reference                                | jsonb         | N               |
| accounting_correction_references, differences     | jsonb         | N               |
| affected_recipients                               | jsonb         | N               |
| T, S                                              | Bloques       |                 |

PK rectification_id.

FK a ambas versiones.

Modos ACCOUNTING_DATA_CORRECTION y PRESENTATION_CHANGE.

La versión anterior permanece inmutable.

Información regulatoria aplica su propio procedimiento sobre declaraciones afectadas.

## **27. Índices complementarios**

| **Tabla**                                | **Índice**                                                                        |
|------------------------------------------|-----------------------------------------------------------------------------------|
| ledger_account_version                   | (status, valid_from, ledger_account_id, account_version)                          |
| ledger_account_version                   | (parent_account_id, ledger_account_id)                                            |
| accounting_rule_version                  | (fact_type, fact_schema_version, consequence_owner, consequence_code, valid_from) |
| accounting_consequence                   | (status, updated_at, consequence_id)                                              |
| accounting_integration                   | (status, updated_at, integration_id)                                              |
| accounting_entry                         | (accounting_period_id, ledger_sequence)                                           |
| accounting_entry                         | (economic_date, ledger_sequence)                                                  |
| accounting_entry_line                    | (ledger_account_id, original_currency, entry_id, line_number)                     |
| accounting_entry_line                    | (dimension_set_id, entry_id, line_number)                                         |
| accounting_entry_correction              | (original_entry_id, status, correction_id)                                        |
| accounting_cut                           | (cut_at, cut_id)                                                                  |
| accounting_period_transition             | (status, updated_at, transition_id)                                               |
| accounting_source_integrity_confirmation | (source_service, economic_date, scope_code, confirmation_id)                      |
| accounting_auxiliary_snapshot            | (source_service, scope_code, cut_at, auxiliary_id, auxiliary_version)             |
| accounting_difference                    | (treatment_owner_service, status, updated_at, difference_id)                      |
| accounting_close                         | (period_id, created_at, close_id)                                                 |
| accounting_reopening                     | (period_id, status, reopening_id)                                                 |
| accounting_report_version                | (status, created_at, report_id, report_version)                                   |
| accounting_report_figure                 | (report_id, report_version, group_code, figure_id)                                |
| accounting_figure_source_line            | (entry_id, line_id, provenance_id)                                                |
| accounting_report_emission_attempt       | (report_id, report_version, attempt_id)                                           |
| accounting_report_document_request       | (status, updated_at, document_request_id)                                         |

Los índices de instantáneas definidos en el apartado 15.2 complementan esta relación.

Las FK compuestas declaran las UQ necesarias  
y comprueban pertenencia al mismo agregado.

## **28. Orden de bloqueo y atomicidad**

### **28.1. Plan y reglas**

Plan: control de jerarquía → propuesta → cuentas ordenadas → versiones y revisión.

Reglas: control de selección → propuesta → regla → versión.

La confirmación de asientos verifica las definiciones vigentes  
bajo controles compatibles con su habilitación y retiro.

### **28.2. Registración**

1.  Consecuencia económica.

2.  Control de reconocimiento acumulado, cuando corresponda.

3.  Controles de ejercicio, mes y día.

4.  Control global de secuencia.

5.  Saldos afectados, ordenados.

6.  Registros dependientes.

Las barreras adquieren los mismos controles superiores antes de los inferiores  
y después el control de secuencia si fijan corte.

Las correcciones bloquean consecuencia correctiva y original en orden estable  
antes de sus controles de alcance.

Los controles monetarios y de unidades se bloquean por identidad de partida  
en un orden estable cuando una corrección comprende varias líneas.

### **28.3. Cierres**

Ejercicio → mes → día → transición o cierre → controles y fuentes.

Preparación de emisión y reapertura respetan esa misma jerarquía.

Los resultados remotos se comprueban fuera de la transacción  
y se vuelven a contrastar al aplicarlos.

### **28.4. Información**

Informe → identidad de ejecución → intento → versión y cifras.

Las consultas y generación documental no mantienen bloqueos SQL  
durante llamadas remotas.

No existen asientos confirmados parcialmente ni una emisión definitiva local  
separada de su decisión COMMITTED.

## **29. Eventos y recuperación**

| **Contrato** | **Tipo**                         | **Tema**                         |
|--------------|----------------------------------|----------------------------------|
| CI-09-029    | AccountingPlanOrRuleChanged      | banking.accounting-entry.events  |
| CI-09-030    | AccountingFactOrEntryProcessed   | banking.accounting-entry.events  |
| CI-09-031    | AccountingPeriodOrCloseChanged   | banking.accounting-close.events  |
| CI-09-032    | AccountingDifferenceChanged      | banking.accounting-close.events  |
| CI-09-033    | AccountingReportIssuedOrRestated | banking.accounting-report.events |

Versión inicial de esquema 1.

Las claves de partición corresponden a cuenta o regla, hecho o asiento,  
período, diferencia e informe según la variante.

Un evento atrasado de apertura no reemplaza una barrera posterior.

La recepción de eventos no ejecuta operaciones bancarias ni duplica consecuencias.

Los trabajos conservan consecuencia, actuación, versión de control,  
corte, cierre e intento originales.

Se utilizan para completar integración, registrar hechos aptos,  
verificar integridad, conciliar, aplicar controles, completar cierres,  
resolver emisiones y obtener documentos.

La incertidumbre no se resuelve creando otra identidad económica.

## **30. Migraciones, conservación y seguridad**

Las migraciones utilizan Flyway por servicio.

Los asientos, partidas, hechos utilizados, cierres e informes emitidos son inmutables.

No se eliminan mediante cascadas versiones de reglas,  
aprobaciones o procedencias utilizadas.

Los saldos reconstruidos se comparan con las partidas  
antes de sustituir una materialización.

Redis no conserva la única copia de asientos, saldos autoritativos,  
barreras o decisiones.

Se separan permisos para plan, reglas, hechos propios, ajustes,  
períodos, integridad, conciliación, cierre, reapertura y emisión.

Un emisor acredita únicamente hechos y auxiliares de su competencia.

La autorización contable no concede permisos sobre dinero,  
deuda o tenencias operativas.

Después de restaurar se reconcilian controles de períodos,  
continuidad de secuencias y emisiones conocidas antes de reanudar.

## **31. Correspondencia con contratos técnicos**

| **Contratos**                  | **Persistencia**                                                             |
|--------------------------------|------------------------------------------------------------------------------|
| CI-09-001 y CI-09-002          | Plan, propuestas, revisiones, cuentas y dimensiones.                         |
| CI-09-003 y CI-09-004          | Reglas, fuentes, líneas y habilitación.                                      |
| CI-09-005 a CI-09-007          | Consecuencias, hechos, antecedentes e integración.                           |
| CI-09-008                      | Lotes, asientos, partidas, saldos y secuencia.                               |
| CI-09-009                      | Propuestas manuales y aprobación.                                            |
| CI-09-010                      | Reversiones, reclasificaciones y alcance corregible monetario y de unidades. |
| CI-09-011                      | Reexpresión y variaciones de presentación.                                   |
| CI-09-012                      | Consulta de asientos y actuaciones.                                          |
| CI-09-013 y CI-09-014          | Períodos, transiciones y comprobación informativa.                           |
| CI-09-015 y CI-09-016          | Integridad y auxiliares.                                                     |
| CI-09-017 y CI-09-018          | Conciliaciones y diferencias.                                                |
| CI-09-019 y CI-09-020          | Cierres diarios y periódicos.                                                |
| CI-09-021 y CI-09-022          | Hechos tardíos, reaperturas y consultas.                                     |
| CI-09-023 y CI-09-024          | Estructuras de informes.                                                     |
| CI-09-025 a CI-09-028          | Informes, cifras, procedencia y rectificaciones.                             |
| CI-09-029 a CI-09-033          | Outbox e historia.                                                           |
| Aplicación de control          | Barrera local en Registración.                                               |
| Consultas de saldos y partidas | Cortes e instantáneas monetarias y de unidades.                              |
| Emisión definitiva             | Intento, preparación y decisión durable.                                     |

## **32. Escenarios de verificación de implementación**

| **Escenario**                                       | **Resultado exigido**                                           |
|-----------------------------------------------------|-----------------------------------------------------------------|
| Cuenta agrupadora                                   | No recibe partidas.                                             |
| Cambios concurrentes de jerarquía                   | No confirman ciclos.                                            |
| Código con historia                                 | No se reutiliza con otra naturaleza.                            |
| Mismo hecho por HTTP y evento                       | Una consecuencia económica.                                     |
| Cambia la identidad del mensaje fuente              | No contabiliza otra vez.                                        |
| Bruto 500 y neto 490 sin retención acreditada       | Mantiene inconsistencia.                                        |
| Falta antecedente                                   | Conserva integración pendiente.                                 |
| Asiento desequilibrado                              | No confirma partidas ni saldos.                                 |
| Orden equilibrado y patrimonio desequilibrado       | No compensa ambos libros.                                       |
| Unidades en custodia                                | Se registran separadas del dinero.                              |
| Conversión redondeada a cero                        | No inventa un centavo.                                          |
| Reexpresión de USD 100                              | Mantiene USD 100 y ajusta ARS.                                  |
| Acumulado pasa de 20 a 25                           | Registra variación 5.                                           |
| Cobro de interés devengado                          | No reconoce otro ingreso.                                       |
| Propuesta manual cambia tras aprobar                | Exige nueva aprobación.                                         |
| Correcciones parciales concurrentes                 | No superan el remanente.                                        |
| Dos correcciones de unidades sobre la misma partida | Comparten el control y no consumen dos veces su alcance.        |
| Corrección de unidades rechazada                    | Restituye únicamente la cantidad pendiente comprobada.          |
| Corrección con resultado incierto                   | Conserva el alcance pendiente hasta resolverla.                 |
| Corte de saldos de unidades                         | Conserva instrumento, unidad, acumulados y secuencia del corte. |
| Registración posterior a un corte                   | No modifica las instantáneas ya confirmadas.                    |
| Instantánea de unidades con saldo negativo          | Conserva el signo sin convertirlo en importe monetario.         |
| Asiento y barrera concurrentes                      | Se ordenan mediante controles locales.                          |
| Apertura antigua recibida tarde                     | No reabre el período.                                           |
| Fuente sin respuesta                                | No se considera vacía.                                          |
| Integridad declara hechos no recibidos              | Permanece incompleta.                                           |
| Disponibilidad 70 y saldo registrado 100            | No compara conceptos diferentes.                                |
| Cambian controles aprobados                         | Repite controles y aprobación.                                  |
| Hecho tardío                                        | Conserva recepción real y tratamiento.                          |
| Apertura de ejercicio                               | No duplica patrimonio.                                          |
| Proyección con huecos                               | No se presenta como completa.                                   |
| Reapertura y emisión definitiva                     | Resuelven preparación e intento originales.                     |
| Falla documental posterior                          | No vuelve a emitir las mismas cifras.                           |
| Informe rectificado                                 | Conserva anterior y diferencias.                                |
| Ajuste contable                                     | No modifica saldos operativos.                                  |
| Respuesta perdida o restauración                    | Recupera sin duplicar asientos ni emisiones.                    |

Las restricciones, equilibrio, exclusiones, bloqueos, cortes y secuencias  
se verifican durante la implementación con PostgreSQL real y escenarios concurrentes.
