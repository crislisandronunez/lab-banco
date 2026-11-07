# **Diseño de persistencia — Módulo 4. Cuentas y fondos**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Cuentas y fondos.

| **Servicio**            | **Responsabilidad**                                                                                 |
|-------------------------|-----------------------------------------------------------------------------------------------------|
| Cuentas                 | Constitución, participantes, permisos, capacidades, condiciones, liquidaciones y cierre de cuentas. |
| Identificación bancaria | CBU, alias, correspondencias históricas y resolución de destinos.                                   |
| Fondos                  | Posiciones monetarias, reservas, movimientos, descubierto y afectaciones monetarias.                |
| Operaciones de efectivo | Recepción y entrega física, coordinación monetaria y diferencias.                                   |

Comprende los contratos CI-04-001 a CI-04-041, las preparaciones de Cuentas y los intentos de ejecución monetaria.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases de servicios.

Fondos es propietario del dinero registrado en cuentas. Cuentas administra las condiciones operativas. Operaciones de
efectivo conserva los hechos físicos.

Las existencias propias del banco pertenecen a Posiciones y liquidez, dentro de Tesorería.

## **2. Convenciones físicas**

### **2.1. Notación**

- **N:** columna obligatoria.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Las columnas condicionales se declaran S y su presencia se controla mediante las restricciones indicadas.

Las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en orden. Las PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                           | **PostgreSQL** | **Restricción**                                |
|------------------------------------|----------------|------------------------------------------------|
| Identificador                      | uuid           | Estable y no reutilizable.                     |
| Versión de negocio                 | bigint         | Positiva.                                      |
| Control de concurrencia            | bigint         | No negativo.                                   |
| Contador de movimientos            | bigint         | No negativo; empieza en cero.                  |
| Secuencia de movimiento confirmado | bigint         | Positiva.                                      |
| Código                             | varchar(80)    | No vacío y registrado.                         |
| Referencia visible                 | varchar(120)   | No vacía.                                      |
| Fundamento                         | varchar(4000)  | No vacío cuando está presente.                 |
| Moneda                             | varchar(3)     | ARS o USD.                                     |
| Importe monetario                  | numeric(24,2)  | No negativo, salvo saldo expresamente firmado. |
| Valor de cálculo                   | numeric(30,12) | Signo según su significado.                    |
| Tasa                               | numeric(24,12) | Conforme a su definición.                      |
| Hash                               | char(64)       | SHA-256 hexadecimal en minúsculas.             |
| Instante                           | timestamptz    | Intercambio en UTC.                            |
| Fecha                              | date           | Fecha bancaria.                                |
| Contenido estructurado             | jsonb          | Esquema registrado y versionado.               |

Los movimientos y las instrucciones de ejecución utilizan importes positivos.

La precisión y escala se validan antes de persistir. No se aceptan importes inválidos mediante redondeo implícito.

### **2.3. Bloques de columnas**

Los bloques representan columnas físicas incorporadas a las tablas que los mencionan.

**Bloque T — Creación**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| created_action_id | uuid        | N               |
| created_at        | timestamptz | N               |

created_action_id referencia service_action(action_id).

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

Cuentas incorpora authority_preparation.

Fondos incorpora execution_identity_control y execution_attempt.

Los intentos monetarios utilizan attempt_type = FUND_EXECUTION.

El contrato de aborto de Fondos no confirma abortos de identidades desconocidas. No utiliza barreras de aborto
anticipado para ese caso.

## **3. Esquemas estructurados**

Los JSONB utilizan esquemas identificados. Cada columna de contenido incorpora su versión mediante una columna explícita
o un envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                  | **Contenido**                                                               |
|------------------------------|-----------------------------------------------------------------------------|
| RemoteReference              | Propietario, tipo, identidad y versión cuando corresponde.                  |
| EvidenceReferences           | Referencias de evidencias y hechos acreditados.                             |
| PendingComponents            | Componente, propietario, motivo y seguimiento.                              |
| AccountSnapshot              | Identidad, condiciones, participantes, capacidades y estados de la cuenta.  |
| AccountOperationalDefinition | Capacidades, condiciones económicas, financiación y fundamento contractual. |
| AccountParticipantDefinition | Actos, modalidad, fundamento y vigencia.                                    |
| JointActionRule              | Grupos, integrantes, mínimo de actores distintos y actos alcanzados.        |
| AccountChangeCommand         | Cambio solicitado, versión de origen y componentes.                         |
| AccountOperationCheckInput    | Actores, representado, acto, destino, importe, moneda, finalidad, huella, momento y evidencia conjunta. |
| AccountPreparationBasis      | Actores, acto, destino, importes y comprobaciones.                          |
| BusinessBasis                | Tipo de fundamento, propietario, antecedente y evidencias.                  |
| FundExecutionCommand         | Cuentas, efectos, conceptos, recursos y autoridades exigibles.              |
| ResourceSnapshot             | Asignaciones, causas, cobertura y estado.                                   |
| SettlementInputs             | Condiciones, bases, fuentes y reglas de cálculo.                            |
| CalendarCalculation          | Calendario versionado, entradas, regla y resultado.                         |
| CashPhysicalInstruction      | Operación, ubicación, importe, moneda y preparaciones.                      |
| CashPhysicalEvidence         | Resultado físico, identidad de origen y comprobaciones.                     |

No se admiten scripts, SQL, campos desconocidos ni estructuras libres.

Los atributos utilizados en unicidad, bloqueos y recuperación se conservan en columnas explícitas.

## **4. Servicio Cuentas: identidad y versiones**

### **4.1. account**

| **Columna**         | **Tipo**     | **Nulabilidad** |
|---------------------|--------------|-----------------|
| account_id          | uuid         | N, PK           |
| account_reference   | varchar(120) | N               |
| constitution_id     | uuid         | N               |
| application_item_id | uuid         | N               |
| contract_service    | varchar(80)  | N               |
| contract_id         | uuid         | N               |
| contract_version    | bigint       | N               |
| product_service     | varchar(80)  | N               |
| product_id          | uuid         | N               |
| product_version     | bigint       | N               |
| account_type        | varchar(20)  | N               |
| currency            | varchar(3)   | N               |
| status              | varchar(30)  | N               |
| operational_version | bigint       | N               |
| opened_at           | timestamptz  | S               |
| operational_from    | timestamptz  | S               |
| service_ended_on    | date         | S               |
| closed_at           | timestamptz  | S               |
| T                   | Bloque       |                 |
| C                   | Bloque       |                 |

UQ sobre account_reference y constitution_id.

Tipos: SAVINGS, PAYROLL y CURRENT.

Estados: CONSTITUTING, CONSTITUTED, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

Restricciones:

- Moneda y tipo no se reinterpretan mediante modificaciones posteriores.

- CONSTITUTED y estados posteriores requieren fecha de constitución.

- ACTIVE y LIMITED requieren fecha de habilitación.

- CLOSED requiere fecha de cierre.

- Las fechas confirmadas conservan orden temporal compatible.

- SAVINGS y PAYROLL no admiten descubierto.

- CURRENT requiere financiación contractual aplicada para utilizarlo.

Índices:

- (status, updated_at, account_id).

- (contract_service, contract_id, account_id).

- (product_service, product_id, account_id).

### **4.2. account_version**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| account_id      | uuid        | N               |
| account_version | bigint      | N               |
| snapshot        | jsonb       | N               |
| effective_at    | timestamptz | N               |
| T               | Bloque      |                 |
| S               | Bloque      |                 |

PK (account_id, account_version) y FK a cuenta.

Cada cambio de versión de la raíz genera su instantánea en la misma transacción.

### **4.3. account_operational_version**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| account_id                   | uuid        | N               |
| operational_version          | bigint      | N               |
| definition                   | jsonb       | N               |
| contract_reference           | jsonb       | N               |
| economic_terms_reference     | jsonb       | N               |
| overdraft_facility_reference | jsonb       | S               |
| effective_from               | timestamptz | N               |
| effective_until              | timestamptz | S               |
| T                            | Bloque      |                 |
| S                            | Bloque      |                 |

PK (account_id, operational_version) y FK a cuenta.

Intervalos semiabiertos, con final posterior al inicio. Exclusión de solapamiento por cuenta.

Índice (account_id, effective_from, operational_version).

La raíz referencia su versión operativa mediante FK compuesta diferible.

### **4.4. account_constitution_component**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| account_id          | uuid        | N               |
| component_code      | varchar(80) | N               |
| responsible_service | varchar(80) | N               |
| remote_action_id    | uuid        | N               |
| requested_content   | jsonb       | N               |
| status              | varchar(30) | N               |
| result_reference    | jsonb       | S               |
| evidence_references | jsonb       | N               |
| last_checked_at     | timestamptz | S               |
| updated_at          | timestamptz | N               |
| lock_version        | bigint      | N               |
| S                   | Bloque      |                 |

PK (account_id, component_code) y FK a cuenta.

UQ (responsible_service, remote_action_id).

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (responsible_service, status, updated_at, account_id, component_code).

La cuenta y sus intenciones se confirman antes de llamar a los propietarios.

La inicialización de Fondos crea una posición cero sin movimiento monetario.

### **4.5. account_operation_check**

| **Columna**                   | **Tipo**     | **Nulabilidad** |
|-------------------------------|--------------|-----------------|
| check_id                      | uuid         | N, PK           |
| account_id                    | uuid         | N               |
| requester_service             | varchar(80)  | N               |
| original_input                | jsonb        | N               |
| original_account_version      | bigint       | N               |
| created_at                    | timestamptz  | N               |
| result                        | varchar(20)  | N               |
| evaluated_account_version     | bigint       | S               |
| evaluated_operational_version | bigint       | S               |
| evaluated_at                  | timestamptz  | S               |
| reason_code                   | varchar(80)  | S               |
| evaluated_joint_evidence      | jsonb        | S               |

original_input utiliza el esquema AccountOperationCheckInput versionado. La identidad, la cuenta, el servicio
solicitante, la entrada y la versión original son inmutables. La recuperación utiliza check_id y exige que coincidan
la cuenta indicada y el alcance autorizado del solicitante.

FK a cuenta y FK compuestas desde la cuenta y las versiones originales y evaluadas a sus respectivos historiales.
original_account_version y evaluated_account_version referencian account_version.
evaluated_operational_version referencia account_operational_version.

CI-04-040 registra la comprobación antes de devolver check_id. PENDING exige que las versiones evaluadas,
evaluated_at, reason_code y evaluated_joint_evidence sean nulos. ADMITTED y PREVENTED exigen ambas versiones evaluadas
y evaluated_at.
PREVENTED exige reason_code. ADMITTED conserva reason_code nulo.

CI-04-041 evalúa el contenido original. Las referencias a conformidades posteriores verificadas y utilizadas se
conservan en evaluated_joint_evidence con el esquema EvidenceReferences. Se registran junto con la decisión definitiva,
separadas de original_input. Una falla que impide decidir conserva el registro pendiente.

Para confirmar el resultado se bloquea primero la cuenta y luego la comprobación. Si el resultado ya es definitivo,
se devuelve el resultado persistido. Si cambiaron las versiones evaluadas, se descarta la evaluación y se vuelve a
evaluar fuera de la transacción de confirmación. Con las versiones vigentes, una actualización condicionada a PENDING
registra la decisión, las versiones, el momento y el motivo en la misma transacción. Los resultados definitivos son
inmutables.

## **5. Participantes y actuación conjunta**

### **5.1. account_participant**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| participant_id  | uuid        | N, PK           |
| account_id      | uuid        | N               |
| party_id        | uuid        | N               |
| role            | varchar(30) | N               |
| current_version | bigint      | N               |
| T               | Bloque      |                 |
| lock_version    | bigint      | N               |

FK a cuenta.

UQ (account_id, party_id, role) y (account_id, participant_id).

Roles: HOLDER y AUTHORIZED_OPERATOR.

Índice inverso (party_id, account_id, participant_id).

### **5.2. account_participant_version**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| participant_id      | uuid        | N               |
| participant_version | bigint      | N               |
| permitted_acts      | jsonb       | N               |
| acting_mode         | varchar(20) | N               |
| valid_from          | timestamptz | N               |
| valid_until         | timestamptz | S               |
| basis_reference     | jsonb       | N               |
| joint_rule_id       | uuid        | S               |
| T                   | Bloque      |                 |
| S                   | Bloque      |                 |

PK (participant_id, participant_version) y FK al participante.

Modalidad INDIVIDUAL o JOINT. JOINT exige regla conjunta; INDIVIDUAL no la contiene.

Final posterior al inicio.

La raíz referencia su versión actual mediante FK diferible.

### **5.3. account_participant_limit**

Columnas obligatorias:

| **Columna**         | **Tipo**      |
|---------------------|---------------|
| participant_id      | uuid          |
| participant_version | bigint        |
| currency            | varchar(3)    |
| maximum_amount      | numeric(24,2) |

PK (participant_id, participant_version, currency) y FK a versión del participante.

Importe no negativo. La ausencia de una moneda no se interpreta automáticamente como límite cero ni ilimitado: se aplica
la regla del acto y su definición operativa.

### **5.4. account_joint_rule**

| **Columna**             | **Tipo** | **Nulabilidad** |
|-------------------------|----------|-----------------|
| joint_rule_id           | uuid     | N, PK           |
| account_id              | uuid     | N               |
| minimum_distinct_actors | integer  | N               |
| permitted_acts          | jsonb    | N               |
| group_definition        | jsonb    | N               |
| T                       | Bloque   |                 |
| S                       | Bloque   |                 |

FK a cuenta. Mínimo positivo.

UQ (account_id, joint_rule_id).

**account_joint_rule_member**

| **Columna**    | **Tipo**    | **Nulabilidad** |
|----------------|-------------|-----------------|
| joint_rule_id  | uuid        | N               |
| account_id     | uuid        | N               |
| group_code     | varchar(80) | N               |
| participant_id | uuid        | N               |

PK (joint_rule_id, group_code, participant_id).

FK compuestas a regla y participante de la misma cuenta.

Índice (participant_id, joint_rule_id).

La definición determina los grupos admitidos y su combinación. El mínimo no puede superar los actores elegibles
distintos.

La correspondencia entre regla, participante y cuenta se comprueba bajo bloqueo de cuenta.

Un cambio de titularidad requiere fundamento contractual y conformidades. No redistribuye saldos por sí mismo.

## **6. Capacidades y cambios operativos**

### **6.1. account_capability**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| account_id      | uuid        | N               |
| capability_code | varchar(80) | N               |
| current_version | bigint      | N               |
| status          | varchar(30) | N               |
| updated_at      | timestamptz | N               |
| lock_version    | bigint      | N               |

PK (account_id, capability_code) y FK a cuenta.

Estados: PENDING, ENABLED, LIMITED, DISABLED y REQUIRES_REVIEW.

### **6.2. account_capability_version**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| account_id               | uuid        | N               |
| capability_code          | varchar(80) | N               |
| capability_version       | bigint      | N               |
| operational_version      | bigint      | N               |
| definition               | jsonb       | N               |
| dependency_references    | jsonb       | N               |
| economic_terms_reference | jsonb       | N               |
| effective_at             | timestamptz | N               |
| T                        | Bloque      |                 |
| S                        | Bloque      |                 |

PK (account_id, capability_code, capability_version).

FK a capacidad y versión operativa.

La capacidad referencia su versión actual mediante FK diferible.

### **6.3. account_change_application**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| change_id                     | uuid        | N, PK           |
| account_id                    | uuid        | N               |
| source_operational_version    | bigint      | N               |
| change_type                   | varchar(30) | N               |
| contract_reference            | jsonb       | N               |
| requested_content             | jsonb       | N               |
| requested_effective_at        | timestamptz | N               |
| effective_at                  | timestamptz | S               |
| resulting_operational_version | bigint      | S               |
| status                        | varchar(30) | N               |
| T                             | Bloque      |                 |
| C                             | Bloque      |                 |
| S                             | Bloque      |                 |

FK a cuenta y versiones operativas de origen y resultado.

Tipos: PARTICIPANTS y CAPABILITIES.

Estados: RECEIVED, PREPARING, PARTIALLY_APPLIED, APPLIED, REJECTED y REQUIRES_REVIEW.

APPLIED exige fecha y versión resultante.

Índice (account_id, status, created_at, change_id).

**account_change_component**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| change_id           | uuid        | N               |
| component_code      | varchar(80) | N               |
| responsible_service | varchar(80) | N               |
| remote_action_id    | uuid        | S               |
| status              | varchar(30) | N               |
| result_reference    | jsonb       | S               |
| evidence_references | jsonb       | N               |
| updated_at          | timestamptz | N               |
| lock_version        | bigint      | N               |

PK (change_id, component_code) y FK al cambio.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

UQ parcial (responsible_service, remote_action_id), donde exista actuación remota.

Una aplicación parcial identifica sus componentes efectivos y pendientes.

## **7. Restricciones y preparaciones de Cuentas**

### **7.1. account_restriction_application**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| restriction_application_id | uuid        | N, PK           |
| account_id                 | uuid        | N               |
| measure_owner_service      | varchar(80) | N               |
| measure_id                 | uuid        | N               |
| measure_version            | bigint      | N               |
| component_code             | varchar(80) | N               |
| action                     | varchar(10) | N               |
| affected_acts              | jsonb       | N               |
| received_at                | timestamptz | N               |
| effective_at               | timestamptz | S               |
| status                     | varchar(30) | N               |
| evidence_references        | jsonb       | N               |
| T                          | Bloque      |                 |
| S                          | Bloque      |                 |

FK a cuenta.

UQ (measure_owner_service, measure_id, measure_version, component_code).

Acciones APPLY, MODIFY y LIFT.

Estados: RECEIVED, RESOLUTION_PENDING, APPLIED, REJECTED y REQUIRES_REVIEW.

Índice (account_id, measure_owner_service, measure_id, measure_version).

La versión más reciente aplicada determina el alcance actual. Una versión antigua no sustituye una posterior.

Esta aplicación no inmoviliza dinero.

### **7.2. account_authority_control**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| account_id                   | uuid        | N, PK           |
| operational_version          | bigint      | N               |
| incompatible_changes_pending | boolean     | N               |
| closure_barrier              | boolean     | N               |
| lock_version                 | bigint      | N               |
| updated_at                   | timestamptz | N               |

FK a cuenta y versión operativa.

La fila existe desde la constitución.

### **7.3. account_preparation_basis**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| preparation_id         | uuid          | N, PK           |
| account_id             | uuid          | N               |
| account_version        | bigint        | N               |
| operational_version    | bigint        | N               |
| act_code               | varchar(80)   | N               |
| actor_references       | jsonb         | N               |
| represented_party_id   | uuid          | S               |
| target_reference       | jsonb         | N               |
| amount                 | numeric(24,2) | S               |
| currency               | varchar(3)    | S               |
| joint_evidence         | jsonb         | N               |
| permission_references  | jsonb         | N               |
| restriction_references | jsonb         | N               |
| binding_content_hash   | char(64)      | N               |
| schema_version         | bigint        | N               |

FK a preparación común, versión de cuenta y versión operativa.

Importe y moneda aparecen conjuntamente.

Índice (account_id, preparation_id).

**account_preparation_participant**

Columnas obligatorias:

- preparation_id uuid.

- participant_id uuid.

- participant_version bigint.

PK sobre las tres columnas y FK a preparación y versión del participante.

Índice inverso (participant_id, participant_version, preparation_id).

Los participantes deben pertenecer a la cuenta preparada.

### **7.4. Revocaciones incompatibles**

Preparación y cambio incompatible bloquean cuenta y control de autoridad.

El cambio:

1.  Registra su alcance.

2.  Impide nuevas preparaciones incompatibles.

3.  Identifica las preparaciones existentes.

4.  Persiste trabajos de resolución.

5.  Incorpora la decisión del coordinador antes de concluir el tratamiento.

No invalida silenciosamente una preparación ni la libera por timeout.

CI-04-040 y CI-04-041 utilizan account_operation_check. La preparación monetaria utiliza authority_preparation.

## **8. Cierre de cuentas**

### **8.1. account_closure**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| closure_id                     | uuid        | N, PK           |
| account_id                     | uuid        | N               |
| cause_code                     | varchar(80) | N               |
| contractual_basis              | jsonb       | N               |
| received_at                    | timestamptz | N               |
| authorized_balance_destination | jsonb       | S               |
| status                         | varchar(30) | N               |
| outcome                        | varchar(20) | N               |
| commercial_ceased_at           | timestamptz | S               |
| closed_at                      | timestamptz | S               |
| residual_obligation_references | jsonb       | N               |
| T                              | Bloque      |                 |
| C                              | Bloque      |                 |

FK a cuenta.

Estados: REQUESTED, INVENTORY_PENDING, PROCESSING, RESIDUAL, CLOSED y CANCELLED.

Resultados: INITIATED, RESIDUAL y CLOSED.

UQ parcial sobre account_id, donde status NOT IN ('CLOSED','CANCELLED').

CLOSED exige fecha de cierre y comprobación de componentes.

Índice (status, updated_at, closure_id).

### **8.2. account_closure_inventory**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| inventory_id        | uuid        | N, PK           |
| closure_id          | uuid        | N               |
| component_code      | varchar(80) | N               |
| owner_service       | varchar(80) | N               |
| inventory_version   | bigint      | N               |
| status              | varchar(20) | N               |
| cut_at              | timestamptz | S               |
| source_version      | bigint      | S               |
| evidence_references | jsonb       | N               |
| checked_at          | timestamptz | S               |
| T                   | Bloque      |                 |

FK a cierre.

UQ (closure_id, component_code, owner_service, inventory_version).

Estados: PENDING, RECEIVED, EMPTY, INCOMPLETE y FAILED.

EMPTY exige corte, comprobación y evidencia expresa de ausencia de elementos.

**account_closure_inventory_item**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| inventory_id        | uuid        | N               |
| item_id             | uuid        | N               |
| object_reference    | jsonb       | N               |
| nature_code         | varchar(80) | N               |
| status_code         | varchar(80) | N               |
| evidence_references | jsonb       | N               |

PK (inventory_id, item_id) y FK a inventario.

### **8.3. account_closure_component**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| closure_id          | uuid        | N               |
| component_code      | varchar(80) | N               |
| owner_service       | varchar(80) | N               |
| remote_action_id    | uuid        | N               |
| requested_content   | jsonb       | N               |
| status              | varchar(30) | N               |
| result_reference    | jsonb       | S               |
| evidence_references | jsonb       | N               |
| updated_at          | timestamptz | N               |
| lock_version        | bigint      | N               |
| S                   | Bloque      |                 |

PK (closure_id, component_code) y FK a cierre.

UQ (owner_service, remote_action_id).

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (owner_service, status, updated_at, closure_id, component_code).

Fondos aplica su barrera bajo el control de posición. Enviar la instrucción no acredita aplicación.

El cierre definitivo requiere resultados autoritativos y ausencia de recursos incompatibles pendientes.

## **9. Identificación bancaria: CBU**

### **9.1. account_identification**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| account_id                  | uuid        | N, PK           |
| currency                    | varchar(3)  | N               |
| current_cbu_assignment_id   | uuid        | S               |
| current_alias_assignment_id | uuid        | S               |
| retirement_status           | varchar(30) | N               |
| T                           | Bloque      |                 |
| C                           | Bloque      |                 |

Estados de retiro: NONE, PENDING y RETIRED.

Las asignaciones actuales utilizan FK locales y se comprueba que pertenecen a la misma cuenta.

### **9.2. bank_identifier_assignment**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| assignment_id         | uuid        | N, PK           |
| account_id            | uuid        | N               |
| constitution_id       | uuid        | N               |
| institution_reference | jsonb       | N               |
| identifier_type       | varchar(10) | N               |
| identifier_value      | varchar(22) | N               |
| currency              | varchar(3)  | N               |
| status                | varchar(30) | N               |
| valid_from            | timestamptz | N               |
| valid_to              | timestamptz | S               |
| retirement_basis      | jsonb       | S               |
| T                     | Bloque      |                 |
| C                     | Bloque      |                 |

FK a identificación de cuenta.

Tipo exclusivamente CBU.

UQ:

- identifier_value, incluyendo retirados.

- (account_id, constitution_id).

- Parcial sobre account_id, donde status \<\> 'RETIRED'.

Estados: ASSIGNED, RETIREMENT_PENDING y RETIRED.

El CBU contiene exactamente 22 dígitos y debe superar sus verificadores estructurales.

RETIRED exige final y fundamento de retiro. Los demás estados no tienen final efectivo.

El CBU no se reutiliza.

## **10. Alias y resolución**

### **10.1. alias_name_control**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| normalized_alias  | varchar(20) | N, PK           |
| pending_change_id | uuid        | S               |
| lock_version      | bigint      | N               |
| created_at        | timestamptz | N               |
| updated_at        | timestamptz | N               |

Alias de 6 a 20 caracteres: letras ASCII minúsculas, números, punto y guion.

pending_change_id referencia un cambio local. Reserva la identidad mientras su resultado no está resuelto.

### **10.2. alias_assignment**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| assignment_id       | uuid        | N, PK           |
| account_id          | uuid        | N               |
| normalized_alias    | varchar(20) | N               |
| status              | varchar(20) | N               |
| valid_from          | timestamptz | N               |
| valid_to            | timestamptz | S               |
| directory_reference | jsonb       | N               |
| T                   | Bloque      |                 |
| C                   | Bloque      |                 |

FK a identificación y control de nombre.

Estados: ASSIGNED y RETIRED.

Exclusiones temporales:

- Por normalized_alias, para impedir asignación simultánea a distintas cuentas.

- Por account_id, para impedir alias simultáneos de la misma cuenta.

Intervalos semiabiertos. RETIRED exige final; ASSIGNED no lo tiene.

Índice (account_id, valid_from, assignment_id).

### **10.3. alias_change**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| change_id                  | uuid        | N, PK           |
| account_id                 | uuid        | N               |
| action                     | varchar(10) | N               |
| previous_assignment_id     | uuid        | S               |
| requested_alias            | varchar(20) | S               |
| resulting_assignment_id    | uuid        | S               |
| directory_service          | varchar(80) | N               |
| remote_action_id           | uuid        | N               |
| requested_content          | jsonb       | N               |
| outcome                    | varchar(20) | N               |
| directory_result_reference | jsonb       | S               |
| T                          | Bloque      |                 |
| C                          | Bloque      |                 |
| S                          | Bloque      |                 |

FK a cuenta, asignaciones y nombre solicitado.

Acciones ASSIGN, REPLACE y RETIRE.

- ASSIGN exige nombre y no tiene asignación anterior.

- REPLACE exige nombre y asignación anterior.

- RETIRE exige asignación anterior y no tiene nombre nuevo.

Resultados: PENDING, CONFIRMED, REJECTED y UNCERTAIN.

UQ (directory_service, remote_action_id).

UQ parcial sobre account_id, donde outcome IN ('PENDING','UNCERTAIN').

Índice (outcome, updated_at, change_id).

Se bloquean primero los nombres afectados, ordenados, y después la identificación de cuenta.

Una sustitución confirmada cierra el intervalo anterior y abre el nuevo en una misma transacción. Mientras sea incierta,
conserva el anterior y la reserva del nombre solicitado.

### **10.4. bank_identifier_resolution**

| **Columna**           | **Tipo**     | **Nulabilidad** |
|-----------------------|--------------|-----------------|
| resolution_id         | uuid         | N, PK           |
| identifier_type       | varchar(10)  | N               |
| normalized_value      | varchar(22)  | N               |
| purpose               | varchar(80)  | N               |
| check_scope           | jsonb        | N               |
| result                | varchar(20)  | N               |
| resolved_at           | timestamptz  | N               |
| source_reference      | jsonb        | N               |
| destination_type      | varchar(10)  | S               |
| account_reference     | jsonb        | S               |
| participant_reference | jsonb        | S               |
| currency              | varchar(3)   | S               |
| display_name          | varchar(250) | S               |
| masked_identifier     | varchar(120) | S               |
| schema_version        | bigint       | N               |

Resultados: RESOLVED, NOT_FOUND y UNAVAILABLE.

RESOLVED exige destino INTERNAL con cuenta o EXTERNAL con participante, de forma excluyente.

Los otros resultados no contienen destino resuelto.

Índice (identifier_type, normalized_value, resolved_at, resolution_id).

Resolver no autoriza movimientos. Las instrucciones aceptadas conservan el destino original, sin resolver nuevamente el
alias durante recuperación.

## **11. Fondos: posición monetaria**

### **11.1. fund_position**

| **Columna**                    | **Tipo**      | **Nulabilidad** |
|--------------------------------|---------------|-----------------|
| account_id                     | uuid          | N, PK           |
| constitution_id                | uuid          | N               |
| account_type                   | varchar(20)   | N               |
| currency                       | varchar(3)    | N               |
| position_version               | bigint        | N               |
| last_movement_sequence         | bigint        | N               |
| booked_balance                 | numeric(24,2) | N               |
| own_funds_available            | numeric(24,2) | N               |
| overdraft_limit                | numeric(24,2) | N               |
| overdraft_principal_used       | numeric(24,2) | N               |
| protected_coverage             | numeric(24,2) | N               |
| new_capacity                   | numeric(24,2) | N               |
| excess                         | numeric(24,2) | N               |
| operational_source_version     | bigint        | N               |
| operational_application_status | varchar(30)   | N               |
| closure_barrier                | boolean       | N               |
| created_at                     | timestamptz   | N               |
| updated_at                     | timestamptz   | N               |
| lock_version                   | bigint        | N               |

UQ sobre constitution_id.

Solo el saldo admite signo. Los demás importes son no negativos.

Estados de aplicación operativa: COMPLETE, INCOMPLETE y REQUIRES_REVIEW.

La inicialización establece saldo, acumulados y contador de movimientos en cero; position_version comienza en uno.

La moneda y el tipo no se modifican para reinterpretar movimientos.

### **11.2. Ecuaciones de posición**

Bajo bloqueo de posición se mantienen:

- Capital utilizado de descubierto igual a la parte negativa del saldo registrado.

- Fondos propios disponibles iguales al saldo positivo menos las asignaciones propias retenidas, sin resultado negativo.

- Cobertura protegida igual a la suma de protecciones remanentes.

- Capacidad crediticia nueva igual al límite menos capital utilizado y cobertura crediticia comprometida, con mínimo
  cero.

- Exceso igual al capital utilizado por encima del límite, con mínimo cero.

La protección forma parte de la cobertura crediticia comprometida. No se suma como una segunda cobertura.

Las asignaciones superpuestas se cuentan una sola vez.

SAVINGS y PAYROLL mantienen límite, utilización y cobertura crediticia en cero.

### **11.3. fund_account_control_application**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| application_id      | uuid        | N, PK           |
| account_id          | uuid        | N               |
| authority_service   | varchar(80) | N               |
| component_code      | varchar(80) | N               |
| source_version      | bigint      | N               |
| requested_content   | jsonb       | N               |
| received_at         | timestamptz | N               |
| effective_at        | timestamptz | S               |
| status              | varchar(30) | N               |
| evidence_references | jsonb       | N               |
| T                   | Bloque      |                 |
| S                   | Bloque      |                 |

FK a posición.

UQ (account_id, authority_service, component_code, source_version).

Estados: RECEIVED, APPLIED, GAP_PENDING y REQUIRES_REVIEW.

Índice (account_id, authority_service, component_code, source_version).

Una discontinuidad impide presentar la copia como completa. La copia no sustituye preparaciones obligatorias.

### **11.4. fund_position_history**

Contiene todas las columnas de estado de fund_position, excepto sus fechas de creación y control de bloqueo, más:

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| cause_reference | jsonb       | N               |
| recorded_at     | timestamptz | N               |
| action_id       | uuid        | N               |

PK (account_id, position_version).

FK a posición y actuación. Es inmutable.

Cada cambio de posición, con o sin movimiento, genera una fila en la misma transacción.

## **12. Recursos y superposiciones**

### **12.1. fund_resource_commitment**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| commitment_id             | uuid          | N, PK           |
| account_id                | uuid          | N               |
| resource_class            | varchar(30)   | N               |
| reservation_id            | uuid          | S               |
| attempt_id                | uuid          | S               |
| measure_application_id    | uuid          | S               |
| conditional_credit_id     | uuid          | S               |
| operation_id              | uuid          | N               |
| amount                    | numeric(24,2) | N               |
| own_coverage              | numeric(24,2) | N               |
| credit_coverage           | numeric(24,2) | N               |
| protected_coverage        | numeric(24,2) | N               |
| status                    | varchar(20)   | N               |
| origin_position_version   | bigint        | N               |
| resolved_position_version | bigint        | S               |
| T                         | Bloque        |                 |

FK a posición, historia de posición y propietarios locales identificados.

Clases: RESERVATION, TECHNICAL, MEASURE y CONDITIONAL_CREDIT.

Cada clase exige exclusivamente su propietario correspondiente. Una reserva consumida por un intento sigue siendo
propietaria de su cobertura; el intento la vincula sin crear otro compromiso equivalente.

Estados: ACTIVE, RESOLUTION_PENDING y RESOLVED.

Restricciones:

- amount = own_coverage + credit_coverage.

- protected_coverage \<= credit_coverage.

- Medidas y créditos condicionados no utilizan cobertura crediticia.

- RESOLVED exige versión de resolución y coberturas remanentes cero.

Índice (account_id, status, commitment_id).

### **12.2. fund_resource_allocation**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| allocation_id            | uuid          | N, PK           |
| account_id               | uuid          | N               |
| coverage_type            | varchar(10)   | N               |
| amount                   | numeric(24,2) | N               |
| protected_amount         | numeric(24,2) | N               |
| status                   | varchar(20)   | N               |
| created_position_version | bigint        | N               |
| ended_position_version   | bigint        | S               |

FK a posición e historia.

Tipos OWN y CREDIT.

Estados: HELD, CONSUMED y RELEASED.

Importe positivo. Protección entre cero e importe; OWN exige protección cero.

HELD no tiene versión final. Los demás estados la requieren.

UQ (account_id, allocation_id).

Índice (account_id, coverage_type, allocation_id), donde status = 'HELD'.

### **12.3. fund_resource_allocation_cause**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| account_id        | uuid        | N               |
| allocation_id     | uuid        | N               |
| commitment_id     | uuid        | N               |
| relationship_type | varchar(20) | N               |

PK (allocation_id, commitment_id).

FK a asignación y compromiso de la misma cuenta; el compromiso declara UQ (account_id, commitment_id).

Tipos EXCLUSIVE y OVERLAY.

Un máximo de una causa EXCLUSIVE por asignación, mediante UQ parcial sobre allocation_id.

Índice inverso (commitment_id, allocation_id).

Las causas superpuestas comparten la misma asignación. Si afectan importes distintos, se divide la asignación en
porciones homogéneas bajo bloqueo de posición.

No se comparten como una misma cobertura dos obligaciones de gasto independientes.

Consumir una asignación exige comprobar todas sus causas vigentes. Resolver una causa no elimina las restantes.

### **12.4. fund_resource_history**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| resource_history_id | uuid        | N, PK           |
| account_id          | uuid        | N               |
| position_version    | bigint      | N               |
| commitment_id       | uuid        | N               |
| change_code         | varchar(80) | N               |
| previous_snapshot   | jsonb       | S               |
| resulting_snapshot  | jsonb       | N               |
| action_id           | uuid        | N               |
| recorded_at         | timestamptz | N               |
| schema_version      | bigint      | N               |

FK a historia de posición, compromiso y actuación.

Índice (commitment_id, position_version, resource_history_id).

Los acumulados por compromiso se contrastan con las asignaciones activas. Las sumas entre filas se comprueban dentro de
la transacción de posición.

## **13. Reservas**

### **13.1. fund_reservation**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| reservation_id         | uuid          | N, PK           |
| account_id             | uuid          | N               |
| currency               | varchar(3)    | N               |
| operation_id           | uuid          | N               |
| business_owner_service | varchar(80)   | N               |
| requesting_service     | varchar(80)   | N               |
| execution_circuit_code | varchar(80)   | N               |
| initial_amount         | numeric(24,2) | N               |
| current_amount         | numeric(24,2) | N               |
| consumed_amount        | numeric(24,2) | N               |
| released_amount        | numeric(24,2) | N               |
| remaining_amount       | numeric(24,2) | N               |
| own_funds_coverage     | numeric(24,2) | N               |
| credit_coverage        | numeric(24,2) | N               |
| protected_coverage     | numeric(24,2) | N               |
| firmness               | varchar(20)   | N               |
| status                 | varchar(30)   | N               |
| review_at              | timestamptz   | S               |
| T                      | Bloque        |                 |
| C                      | Bloque        |                 |

FK a posición.

Ecuaciones:

- Vigente = consumido + liberado + remanente.

- Remanente = cobertura propia + crediticia.

- Protección ≤ cobertura crediticia.

- Todos los acumulados son no negativos.

Firmezas: CANCELLABLE, FIRM y UNCERTAIN.

Estados: ACTIVE, PARTIALLY_CONSUMED, CONSUMED, RELEASED y RESOLUTION_PENDING.

Índices:

- (account_id, status, reservation_id).

- (business_owner_service, operation_id, reservation_id).

- (review_at, reservation_id), donde exista remanente y revisión prevista.

El vencimiento inicia revisión; no libera automáticamente.

### **13.2. fund_reservation_effect**

| **Columna**    | **Tipo**    | **Nulabilidad** |
|----------------|-------------|-----------------|
| reservation_id | uuid        | N               |
| effect_id      | uuid        | N               |
| coverage_state | varchar(20) | N               |

PK (reservation_id, effect_id) y FK a reserva y efecto económico.

Estados: ACTIVE, CONSUMED y RELEASED.

UQ parcial sobre effect_id, donde coverage_state IN ('ACTIVE','CONSUMED').

Una cobertura consumida no habilita otra reserva del mismo efecto.

### **13.3. fund_reservation_consumer**

Columnas obligatorias reservation_id uuid y consumer_service varchar(80).

PK sobre ambas y FK a reserva.

Los consumidores proceden del circuito registrado. No los amplía libremente el solicitante.

### **13.4. fund_reservation_consumption**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| consumption_id              | uuid          | N, PK           |
| reservation_id              | uuid          | N               |
| effect_id                   | uuid          | N               |
| amount                      | numeric(24,2) | N               |
| movement_id                 | uuid          | N               |
| attempt_id                  | uuid          | N               |
| consumed_own_coverage       | numeric(24,2) | N               |
| consumed_credit_coverage    | numeric(24,2) | N               |
| consumed_protected_coverage | numeric(24,2) | N               |
| T                           | Bloque        |                 |

FK a reserva, efecto cubierto, movimiento e intento.

UQ sobre effect_id y movement_id.

Importe positivo, igual a cobertura propia más crediticia. Protección no superior a la crediticia.

El consumo y el débito se confirman en la misma transacción.

### **13.5. fund_reservation_release**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| release_id                  | uuid          | N, PK           |
| reservation_id              | uuid          | N               |
| amount                      | numeric(24,2) | N               |
| reason_code                 | varchar(80)   | N               |
| evidence_references         | jsonb         | N               |
| released_own_coverage       | numeric(24,2) | N               |
| released_credit_coverage    | numeric(24,2) | N               |
| released_protected_coverage | numeric(24,2) | N               |
| T                           | Bloque        |                 |

FK a reserva. UQ sobre created_action_id.

Importe positivo e igual a las coberturas propia y crediticia liberadas.

Índice (reservation_id, created_at, release_id).

La liberación exige evidencia que descarte ejecución u obligación pendiente. No genera crédito.

### **13.6. fund_reservation_change**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| change_id           | uuid          | N, PK           |
| reservation_id      | uuid          | N               |
| change_mode         | varchar(20)   | N               |
| previous_amount     | numeric(24,2) | N               |
| resulting_amount    | numeric(24,2) | N               |
| previous_firmness   | varchar(20)   | N               |
| resulting_firmness  | varchar(20)   | N               |
| reason              | varchar(4000) | N               |
| evidence_references | jsonb         | N               |
| T                   | Bloque        |                 |

FK a reserva.

Modalidades CHANGE_AMOUNT y CHANGE_FIRMNESS. La modalidad no modificada conserva su valor anterior.

Índice (reservation_id, created_at, change_id).

Reducir el total no incrementa además el acumulado liberado. Liberar mantiene el total y aumenta dicho acumulado.

El total nunca queda por debajo de consumos y liberaciones confirmados.

## **14. Efectos y movimientos**

### **14.1. fund_economic_effect**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| effect_id              | uuid          | N, PK           |
| account_id             | uuid          | N               |
| business_owner_service | varchar(80)   | N               |
| business_basis         | jsonb         | N               |
| operation_id           | uuid          | N               |
| direction              | varchar(10)   | N               |
| amount                 | numeric(24,2) | N               |
| currency               | varchar(3)    | N               |
| bound_command          | jsonb         | N               |
| current_attempt_id     | uuid          | S               |
| status                 | varchar(20)   | N               |
| resulting_movement_id  | uuid          | S               |
| T                      | Bloque        |                 |
| S                      | Bloque        |                 |
| lock_version           | bigint        | N               |

FK a posición, intento y movimiento resultante.

Estados: DEFINED, PENDING, COMMITTED y ABORTED.

COMMITTED exige movimiento. Los demás no tienen movimiento confirmado.

Índice (account_id, status, effect_id).

La identidad es común a todos los circuitos. No puede repetirse un débito mediante otro punto de entrada.

### **14.2. fund_movement**

| **Columna**          | **Tipo**      | **Nulabilidad** |
|----------------------|---------------|-----------------|
| movement_id          | uuid          | N, PK           |
| account_id           | uuid          | N               |
| account_sequence     | bigint        | N               |
| position_version     | bigint        | N               |
| effect_id            | uuid          | N               |
| operation_id         | uuid          | N               |
| action_id            | uuid          | N               |
| execution_attempt_id | uuid          | N               |
| direction            | varchar(10)   | N               |
| amount               | numeric(24,2) | N               |
| currency             | varchar(3)    | N               |
| recorded_at          | timestamptz   | N               |
| occurred_at          | timestamptz   | N               |
| economic_date        | date          | N               |
| value_date           | date          | S               |
| reservation_id       | uuid          | S               |
| movement_group_id    | uuid          | S               |
| original_movement_id | uuid          | S               |

FK a posición, historia de posición, efecto, actuación, intento y referencias locales opcionales.

UQ sobre effect_id y (account_id, account_sequence).

Sentido DEBIT o CREDIT. Importe y secuencia positivos.

Índices:

- (account_id, economic_date, account_sequence).

- (execution_attempt_id, movement_id).

- (movement_group_id, movement_id), donde exista.

- (original_movement_id, movement_id), donde exista.

El contador de secuencia se incrementa en la propia posición dentro de la transacción. No utiliza una secuencia
PostgreSQL independiente.

Los movimientos confirmados son inmutables.

### **14.3. fund_movement_concept**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| movement_id           | uuid          | N               |
| position              | integer       | N               |
| concept_code          | varchar(80)   | N               |
| component_type        | varchar(20)   | N               |
| amount                | numeric(24,2) | N               |
| currency              | varchar(3)    | N               |
| calculation_reference | jsonb         | S               |

PK (movement_id, position) y FK a movimiento.

Categorías: PRINCIPAL, INTEREST, FEE, TAX y ADJUSTMENT.

Posición positiva; conceptos no vacíos. Su suma coincide con el movimiento y utilizan la misma moneda.

## **15. Correcciones monetarias**

### **15.1. fund_correction_control**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| original_movement_id         | uuid          | N, PK           |
| correctable_amount           | numeric(24,2) | N               |
| corrected_amount             | numeric(24,2) | N               |
| pending_committed_amount     | numeric(24,2) | N               |
| remaining_correctable_amount | numeric(24,2) | N               |
| updated_at                   | timestamptz   | N               |
| lock_version                 | bigint        | N               |

FK al movimiento original.

Ecuación:

Correctible = corregido + comprometido pendiente + remanente corregible.

Todos los importes son no negativos y el alcance correctible no supera el movimiento original.

### **15.2. fund_movement_correction**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| correction_id         | uuid          | N, PK           |
| original_movement_id  | uuid          | N               |
| correction_effect_id  | uuid          | N               |
| attempt_id            | uuid          | N               |
| amount                | numeric(24,2) | N               |
| scope_code            | varchar(80)   | N               |
| reason                | varchar(4000) | N               |
| business_basis        | jsonb         | N               |
| approval_reference    | jsonb         | S               |
| status                | varchar(20)   | N               |
| resulting_movement_id | uuid          | S               |
| T                     | Bloque        |                 |
| S                     | Bloque        |                 |

FK a control, efecto, intento y movimiento resultante.

UQ sobre correction_effect_id.

Estados: PENDING, COMMITTED y ABORTED.

COMMITTED exige movimiento; PENDING conserva importe comprometido.

Índice (original_movement_id, status, correction_id).

Una corrección genera movimientos nuevos y no concede financiación por sí misma.

## **16. Conjuntos internos**

### **16.1. internal_movement_group**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| movement_group_id | uuid        | N, PK           |
| group_type        | varchar(30) | N               |
| operation_id      | uuid        | N               |
| business_basis    | jsonb       | N               |
| requested_content | jsonb       | N               |
| attempt_id        | uuid        | S               |
| economic_date     | date        | N               |
| status            | varchar(20) | N               |
| result_references | jsonb       | N               |
| T                 | Bloque      |                 |
| S                 | Bloque      |                 |

FK al intento.

Tipos SAME_CURRENCY_TRANSFER e INTERNAL_FX.

Estados: PENDING, COMMITTED y ABORTED.

### **16.2. internal_movement_leg**

| **Columna**       | **Tipo**      | **Nulabilidad** |
|-------------------|---------------|-----------------|
| movement_group_id | uuid          | N               |
| position          | integer       | N               |
| effect_id         | uuid          | N               |
| account_id        | uuid          | N               |
| direction         | varchar(10)   | N               |
| amount            | numeric(24,2) | N               |
| currency          | varchar(3)    | N               |
| concepts          | jsonb         | N               |

PK (movement_group_id, position).

FK al grupo, efecto y posición de cuenta. UQ sobre effect_id.

Importe y posición positivos.

El esquema de conceptos conserva código, categoría, importe, moneda y referencia de cálculo.

### **16.3. internal_group_reservation_consumption**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| movement_group_id   | uuid          | N               |
| reservation_id      | uuid          | N               |
| reservation_version | bigint        | N               |
| effect_id           | uuid          | N               |
| amount              | numeric(24,2) | N               |

PK (movement_group_id, reservation_id, effect_id).

FK a grupo, reserva y efecto cubierto.

Índice (reservation_id, movement_group_id).

### **16.4. internal_fx_basis**

| **Columna**                   | **Tipo**      | **Nulabilidad** |
|-------------------------------|---------------|-----------------|
| movement_group_id             | uuid          | N, PK           |
| exchange_reference            | jsonb         | N               |
| accepted_proposal_reference   | jsonb         | N               |
| quotation_reference           | jsonb         | N               |
| treasury_commitment_reference | jsonb         | N               |
| risk_affectation_reference    | jsonb         | N               |
| source_currency               | varchar(3)    | N               |
| source_amount                 | numeric(24,2) | N               |
| target_currency               | varchar(3)    | N               |
| target_amount                 | numeric(24,2) | N               |
| accepted_content_hash         | char(64)      | N               |

FK al grupo. Exclusivo de INTERNAL_FX.

Monedas diferentes e importes positivos.

Fondos no calcula la cotización ni modifica los importes aceptados.

### **16.5. Atomicidad**

En traslado de la misma moneda, el principal debitado coincide con el acreditado y las cuentas son distintas. Los cargos
se identifican separadamente.

Todas las piernas, consumos, recursos, posiciones y decisión confirman en una sola transacción.

Una pierna inválida impide confirmar el conjunto completo.

El cambio interno no genera una salida externa ficticia de Tesorería.

## **17. Intentos monetarios**

### **17.1. fund_execution_attempt_detail**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| attempt_id           | uuid        | N, PK           |
| attempt_type         | varchar(80) | N               |
| execution_kind       | varchar(30) | N               |
| requested_content    | jsonb       | N               |
| binding_content_hash | char(64)    | N               |
| pending_components   | jsonb       | N               |
| schema_version       | bigint      | N               |

FK compuesta al intento común. attempt_type = FUND_EXECUTION.

Clases: MOVEMENT, RESERVATION_CREATE, RESERVATION_CONSUMPTION, INTERNAL_GROUP y CORRECTION.

### **17.2. Relaciones del intento**

**fund_attempt_account**

Columnas obligatorias attempt_id uuid y account_id uuid.

PK sobre ambas y FK a intento y posición.

Índice inverso (account_id, attempt_id).

**fund_attempt_effect**

Columnas obligatorias attempt_id uuid y effect_id uuid.

PK sobre ambas y FK a intento y efecto.

Índice inverso (effect_id, attempt_id).

**fund_attempt_resource**

| **Columna**       | **Tipo**      | **Nulabilidad** |
|-------------------|---------------|-----------------|
| attempt_id        | uuid          | N               |
| commitment_id     | uuid          | N               |
| effect_id         | uuid          | N               |
| amount            | numeric(24,2) | N               |
| relationship_type | varchar(20)   | N               |

PK (attempt_id, commitment_id, effect_id).

FK a intento, compromiso y efecto.

Tipos OWNED y BORROWED_RESERVATION.

Una reserva existente se vincula como cobertura prestada al intento; no se vuelve a descontar.

Índice (commitment_id, attempt_id).

### **17.3. fund_attempt_authority**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| attempt_id           | uuid        | N               |
| component_code       | varchar(80) | N               |
| owner_service        | varchar(80) | N               |
| required             | boolean     | N               |
| remote_action_id     | uuid        | N               |
| preparation_id       | uuid        | S               |
| binding_content_hash | char(64)    | N               |
| scope_payload        | jsonb       | N               |
| state                | varchar(30) | N               |
| notification_state   | varchar(20) | N               |
| last_checked_at      | timestamptz | S               |
| evidence_references  | jsonb       | N               |
| updated_at           | timestamptz | N               |
| lock_version         | bigint      | N               |

PK (attempt_id, component_code) y FK al intento.

UQ (owner_service, remote_action_id).

Preparación: REQUEST_PENDING, PREPARED, REJECTED, UNCERTAIN y CONCLUDED.

Notificación: NOT_REQUIRED, PENDING, CONFIRMED y UNCERTAIN.

PREPARED y CONCLUDED requieren preparación remota.

Índices:

- (owner_service, preparation_id), donde exista.

- (state, updated_at, attempt_id, component_code).

- (notification_state, updated_at, attempt_id, component_code), donde notification_state IN ('PENDING','UNCERTAIN').

### **17.4. Confirmación y aborto**

La intención se registra antes de solicitar preparaciones.

El orden es:

1.  Identidad de ejecución.

2.  Intento.

3.  Posiciones ordenadas por cuenta.

4.  Reservas ordenadas por identidad.

5.  Controles de corrección y afectación.

6.  Efectos y detalles.

La confirmación registra conjuntamente decisión, movimientos o reservas, recursos, posiciones, historia, actuación,
outbox y notificaciones pendientes.

El aborto utiliza el mismo orden.

Abortar un consumo no libera una reserva firme ajena al intento. Una identidad desconocida no acredita aborto.

## **18. Descubierto**

### **18.1. overdraft_facility_application**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| application_id            | uuid          | N, PK           |
| account_id                | uuid          | N               |
| financing_service         | varchar(80)   | N               |
| financing_id              | uuid          | N               |
| authorization_reference   | jsonb         | N               |
| limit_version             | bigint        | N               |
| limit_amount              | numeric(24,2) | N               |
| currency                  | varchar(3)    | N               |
| valid_from                | timestamptz   | N               |
| valid_until               | timestamptz   | S               |
| superseded_application_id | uuid          | S               |
| effective_at              | timestamptz   | S               |
| status                    | varchar(20)   | N               |
| evidence_references       | jsonb         | N               |
| T                         | Bloque        |                 |
| S                         | Bloque        |                 |

FK a posición y aplicación sustituida.

UQ (financing_service, financing_id, limit_version).

Estados: RECEIVED, APPLIED, SUPERSEDED y REJECTED.

Solo Préstamos y líneas puede acreditar la aplicación.

Índice (account_id, limit_version, application_id).

Una versión antigua no sustituye una posterior. Solo se aplica a CURRENT y con moneda coincidente.

### **18.2. overdraft_utilization_change**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| utilization_change_id    | uuid          | N, PK           |
| account_id               | uuid          | N               |
| position_version         | bigint        | N               |
| facility_application_id  | uuid          | N               |
| previous_principal_used  | numeric(24,2) | N               |
| resulting_principal_used | numeric(24,2) | N               |
| protected_coverage       | numeric(24,2) | N               |
| new_capacity             | numeric(24,2) | N               |
| excess                   | numeric(24,2) | N               |
| cause_reference          | jsonb         | N               |
| T                        | Bloque        |                 |

FK a historia de posición y aplicación de financiación.

UQ (account_id, position_version).

Una acreditación ordinaria sobre saldo negativo reduce capital utilizado. No cobra intereses, comisiones o tributos
implícitamente.

### **18.3. overdraft_protected_commitment**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| commitment_id            | uuid          | N               |
| facility_application_id  | uuid          | N               |
| protected_initial_amount | numeric(24,2) | N               |
| consumed_amount          | numeric(24,2) | N               |
| released_amount          | numeric(24,2) | N               |
| remaining_amount         | numeric(24,2) | N               |
| updated_at               | timestamptz   | N               |
| lock_version             | bigint        | N               |

PK (commitment_id, facility_application_id).

FK a compromiso y aplicación de límite.

Inicial = consumido + liberado + remanente.

La reducción conserva capital y cobertura firme o incierta. Los cancelables se revisan desde los más recientes.

La protección no financia operaciones nuevas. Su consumo puede generar exceso; su liberación elimina la protección
correspondiente.

## **19. Medidas monetarias**

### **19.1. monetary_measure_application**

| **Columna**             | **Tipo**      | **Nulabilidad** |
|-------------------------|---------------|-----------------|
| affectation_id          | uuid          | N, PK           |
| account_id              | uuid          | N               |
| measure_owner_service   | varchar(80)   | N               |
| measure_id              | uuid          | N               |
| component_code          | varchar(80)   | N               |
| current_measure_version | bigint        | N               |
| priority_code           | varchar(80)   | N               |
| priority_basis          | jsonb         | N               |
| requested_amount        | numeric(24,2) | N               |
| applied_amount          | numeric(24,2) | N               |
| insufficient_amount     | numeric(24,2) | N               |
| status                  | varchar(20)   | N               |
| effective_at            | timestamptz   | N               |
| T                       | Bloque        |                 |
| C                       | Bloque        |                 |

FK a posición.

UQ (account_id, measure_owner_service, measure_id, component_code).

Estados: ACTIVE, PARTIALLY_APPLIED, LIFTED y REQUIRES_REVIEW.

Solicitado = aplicado + insuficiente.

Índice (account_id, status, priority_code, affectation_id).

### **19.2. monetary_affectation_change**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| change_id             | uuid          | N, PK           |
| affectation_id        | uuid          | N               |
| measure_owner_service | varchar(80)   | N               |
| measure_id            | uuid          | N               |
| measure_version       | bigint        | N               |
| component_code        | varchar(80)   | N               |
| mode                  | varchar(20)   | N               |
| requested_amount      | numeric(24,2) | N               |
| applied_amount        | numeric(24,2) | N               |
| insufficient_amount   | numeric(24,2) | N               |
| effective_at          | timestamptz   | N               |
| evidence_references   | jsonb         | N               |
| T                     | Bloque        |                 |
| S                     | Bloque        |                 |

FK a afectación.

UQ (measure_owner_service, measure_id, measure_version, component_code).

Modalidades APPLY_MEASURE, MODIFY_MEASURE y LIFT_MEASURE.

La aplicación valida pertenencia a la cuenta y versión. El levantamiento deja remanentes de esa causa en cero, sin
eliminar otras causas.

No se utiliza descubierto para completar una medida.

Inmovilizar no debita y levantar no acredita.

## **20. Créditos condicionados**

### **20.1. conditional_credit**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| conditional_credit_id       | uuid          | N, PK           |
| original_movement_id        | uuid          | N               |
| account_id                  | uuid          | N               |
| condition_id                | uuid          | N               |
| condition_code              | varchar(80)   | N               |
| responsible_service         | varchar(80)   | N               |
| original_amount             | numeric(24,2) | N               |
| released_amount             | numeric(24,2) | N               |
| remaining_amount            | numeric(24,2) | N               |
| partial_fulfillment_allowed | boolean       | N               |
| evidence_requirement_codes  | jsonb         | N               |
| status                      | varchar(20)   | N               |
| T                           | Bloque        |                 |
| C                           | Bloque        |                 |

FK a movimiento y posición.

UQ (original_movement_id, condition_id).

El movimiento original debe ser CREDIT de esa cuenta.

Original = liberado + remanente.

Estados: ACTIVE, PARTIALLY_RELEASED y RELEASED.

Índice (account_id, status, conditional_credit_id).

### **20.2. conditional_credit_release**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| release_id               | uuid          | N, PK           |
| conditional_credit_id    | uuid          | N               |
| owner_service            | varchar(80)   | N               |
| fulfillment_fact_type    | varchar(80)   | N               |
| fulfillment_fact_id      | uuid          | N               |
| fulfillment_fact_version | bigint        | N               |
| amount                   | numeric(24,2) | N               |
| effective_at             | timestamptz   | N               |
| evidence_references      | jsonb         | N               |
| T                        | Bloque        |                 |
| S                        | Bloque        |                 |

FK a crédito condicionado.

UQ (conditional_credit_id, owner_service, fulfillment_fact_type, fulfillment_fact_id, fulfillment_fact_version).

Importe positivo. La suma liberada no supera el original.

Solo el responsable registrado acredita cumplimiento. Una liberación parcial exige permiso expreso.

La liberación modifica disponibilidad, no crea otro crédito y no levanta otras medidas.

## **21. Cortes y conciliación de Fondos**

### **21.1. fund_query_cut**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| cut_id                    | uuid        | N, PK           |
| account_id                | uuid        | N               |
| position_version          | bigint      | N               |
| maximum_movement_sequence | bigint      | N               |
| cut_at                    | timestamptz | N               |
| filters                   | jsonb       | N               |
| filters_hash              | char(64)    | N               |
| completeness              | varchar(20) | N               |
| missing_data_codes        | jsonb       | N               |
| position_snapshot         | jsonb       | N               |
| schema_version            | bigint      | N               |
| created_at                | timestamptz | N               |

FK a historia de posición.

Completitud COMPLETE o INCOMPLETE.

Índice (account_id, cut_at, cut_id).

El corte se obtiene de una lectura consistente. Las páginas utilizan la secuencia máxima fijada, incluso si después se
registra un movimiento con fecha económica anterior.

El cursor vincula corte, filtros y última secuencia. Un corte no disponible no se sustituye silenciosamente.

### **21.2. fund_reconciliation_run**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| reconciliation_id   | uuid        | N, PK           |
| cut_id              | uuid        | N               |
| scope_codes         | jsonb       | N               |
| started_at          | timestamptz | N               |
| completed_at        | timestamptz | S               |
| status              | varchar(20) | N               |
| check_results       | jsonb       | N               |
| incident_references | jsonb       | N               |
| T                   | Bloque      |                 |

FK al corte.

Estados: RUNNING, MATCHED, DIFFERENCES y FAILED.

Resultados finales requieren fecha de finalización.

Se comprueban saldo, secuencias, recursos, reservas, descubierto y correspondencia entre decisiones y efectos.

La conciliación no altera movimientos confirmados para hacer coincidir acumulados.

## **22. Liquidaciones de cuenta**

### **22.1. account_settlement**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| settlement_id               | uuid        | N, PK           |
| account_id                  | uuid        | N               |
| concept_code                | varchar(80) | N               |
| occurrence_type             | varchar(20) | N               |
| period_from                 | date        | S               |
| period_to                   | date        | S               |
| occurrence_id               | uuid        | S               |
| original_settlement_id      | uuid        | S               |
| rectification_id            | uuid        | S               |
| current_calculation_version | bigint      | N               |
| T                           | Bloque      |                 |
| lock_version                | bigint      | N               |

FK a cuenta y liquidación original.

PERIODIC exige período y no ocurrencia. EVENT exige ocurrencia y no período.

El período incluye inicio y excluye final.

UQ parciales para originales:

- (account_id, concept_code, period_from, period_to), donde sea periódica y no rectificación.

- (account_id, concept_code, occurrence_id), donde sea por hecho y no rectificación.

Las rectificaciones exigen original e identidad de rectificación, con UQ (original_settlement_id, rectification_id).

### **22.2. account_settlement_calculation**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| settlement_id       | uuid          | N               |
| calculation_version | bigint        | N               |
| input_hash          | char(64)      | N               |
| inputs              | jsonb         | N               |
| currency            | varchar(3)    | N               |
| gross_amount        | numeric(24,2) | N               |
| discount_amount     | numeric(24,2) | N               |
| tax_amount          | numeric(24,2) | N               |
| net_amount          | numeric(24,2) | N               |
| execution_direction | varchar(10)   | N               |
| rounding_code       | varchar(20)   | N               |
| calculation_status  | varchar(30)   | N               |
| calculated_at       | timestamptz   | N               |
| T                   | Bloque        |                 |
| schema_version      | bigint        | N               |

PK (settlement_id, calculation_version) y FK a liquidación.

Estados: DETERMINED, INCOMPLETE_INPUT y OBSERVED.

Bonificado no superior al bruto. Neto = bruto − bonificado + tributario.

Sentido DEBIT o CREDIT. Las diferencias de rectificación conservan su sentido explícito, sin importes ejecutables
negativos.

La raíz referencia su cálculo actual mediante FK diferible.

### **22.3. account_settlement_daily_basis**

| **Columna**            | **Tipo**       | **Nulabilidad** |
|------------------------|----------------|-----------------|
| settlement_id          | uuid           | N               |
| calculation_version    | bigint         | N               |
| basis_date             | date           | N               |
| booked_closing_balance | numeric(24,2)  | N               |
| annual_rate            | numeric(24,12) | N               |
| unrounded_amount       | numeric(30,12) | N               |
| service_days           | integer        | N               |
| month_days             | integer        | N               |
| conditions_reference   | jsonb          | N               |
| source_cut_reference   | jsonb          | N               |

PK (settlement_id, calculation_version, basis_date) y FK al cálculo.

Días de servicio no negativos; días de mes entre 28 y 31.

La remuneración utiliza interés simple diario Actual/365 sobre saldo positivo registrado al cierre.

Reservas e inmovilizaciones no reducen esa base. No se redondea diariamente; el resultado final utiliza HALF_UP.

El inicio del servicio se incluye y su terminación se excluye. Las bonificaciones preceden a los tributos.

### **22.4. account_settlement_execution**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| execution_id        | uuid        | N, PK           |
| settlement_id       | uuid        | N               |
| calculation_version | bigint      | N               |
| effect_id           | uuid        | N               |
| funds_service       | varchar(80) | N               |
| remote_action_id    | uuid        | N               |
| status              | varchar(20) | N               |
| movement_reference  | jsonb       | S               |
| pending_components  | jsonb       | N               |
| T                   | Bloque      |                 |
| C                   | Bloque      |                 |

FK al cálculo.

UQ sobre effect_id, (funds_service, remote_action_id) y (settlement_id, calculation_version).

Estados: NOT_REQUESTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

CONFIRMED exige movimiento.

Una rectificación ejecuta únicamente la diferencia respecto de lo confirmado. Un cálculo nuevo no habilita cobrar
nuevamente el total.

Los cargos sin fondos propios permanecen pendientes, sin descubierto automático.

## **23. Operaciones de efectivo**

### **23.1. cash_operation**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| cash_operation_id      | uuid          | N, PK           |
| operation_type         | varchar(10)   | N               |
| account_id             | uuid          | N               |
| currency               | varchar(3)    | N               |
| declared_amount        | numeric(24,2) | N               |
| verified_amount        | numeric(24,2) | S               |
| actor_reference        | jsonb         | N               |
| location_reference     | jsonb         | N               |
| location_identity_hash | char(64)      | N               |
| business_basis         | jsonb         | N               |
| physical_status        | varchar(30)   | N               |
| monetary_status        | varchar(30)   | N               |
| pending_components     | jsonb         | N               |
| T                      | Bloque        |                 |
| C                      | Bloque        |                 |

Tipos DEPOSIT y WITHDRAWAL.

Estados físicos: NOT_STARTED, PREPARED, CONFIRMED, PARTIALLY_CONFIRMED, FAILED y UNCERTAIN.

Estados monetarios: NOT_REQUESTED, PENDING, CONFIRMED y REQUIRES_RESOLUTION.

Importe declarado positivo. CONFIRMED y PARTIALLY_CONFIRMED físicos exigen importe verificado.

Índices:

- (account_id, created_at, cash_operation_id).

- (location_identity_hash, created_at, cash_operation_id).

- (physical_status, monetary_status, updated_at, cash_operation_id).

El importe declarado de un depósito no acredita saldo.

### **23.2. cash_operation_resource**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| resource_id         | uuid          | N, PK           |
| cash_operation_id   | uuid          | N               |
| resource_type       | varchar(30)   | N               |
| owner_service       | varchar(80)   | N               |
| source_object_id    | uuid          | N               |
| source_version      | bigint        | S               |
| amount              | numeric(24,2) | N               |
| status              | varchar(20)   | N               |
| evidence_references | jsonb         | N               |
| updated_at          | timestamptz   | N               |

FK a operación.

Tipos FUND_RESERVATION, CARD_AUTHORIZATION, PHYSICAL_COMMITMENT y AUTHORITY_PREPARATION.

UQ (cash_operation_id, resource_type, owner_service, source_object_id).

Estados: PENDING, CONFIRMED, UNCERTAIN y RESOLVED.

La cobertura válida de tarjeta o reserva se reutiliza; no se duplica.

### **23.3. cash_physical_instruction**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| instruction_id         | uuid        | N, PK           |
| cash_operation_id      | uuid        | N               |
| executor_service       | varchar(80) | N               |
| remote_action_id       | uuid        | N               |
| requested_content      | jsonb       | N               |
| preparation_references | jsonb       | N               |
| status                 | varchar(20) | N               |
| T                      | Bloque      |                 |
| C                      | Bloque      |                 |
| S                      | Bloque      |                 |

FK a operación.

UQ sobre cash_operation_id y (executor_service, remote_action_id).

Estados: PREPARED, SENT, CONFIRMED, FAILED y UNCERTAIN.

La instrucción se confirma antes de enviarse. La incertidumbre no permite crear otra entrega física.

### **23.4. cash_physical_fact**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| fact_id             | uuid          | N, PK           |
| cash_operation_id   | uuid          | N               |
| instruction_id      | uuid          | N               |
| owner_service       | varchar(80)   | N               |
| source_fact_id      | uuid          | N               |
| source_fact_version | bigint        | N               |
| location_reference  | jsonb         | N               |
| result              | varchar(20)   | N               |
| verified_amount     | numeric(24,2) | S               |
| currency            | varchar(3)    | N               |
| occurred_at         | timestamptz   | N               |
| absence_confirmed   | boolean       | N               |
| evidence_payload    | jsonb         | N               |
| T                   | Bloque        |                 |
| S                   | Bloque        |                 |

FK a operación e instrucción.

UQ (owner_service, source_fact_id, source_fact_version).

Resultados: CONFIRMED, PARTIAL, FAILED y UNCERTAIN.

- CONFIRMED y PARTIAL requieren importe positivo.

- PARTIAL exige modalidad admitida e importe inferior al instruido.

- FAILED exige ausencia comprobada de recepción o entrega.

- UNCERTAIN no se interpreta como importe cero.

Índice (cash_operation_id, occurred_at, fact_id).

Se comprueba que instrucción, cuenta, ubicación y moneda corresponden. Los hechos contradictorios no se sobrescriben.

## **24. Coordinación monetaria y posición propia de efectivo**

### **24.1. cash_monetary_application**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| application_id        | uuid        | N, PK           |
| cash_operation_id     | uuid        | N               |
| physical_fact_id      | uuid        | S               |
| effect_id             | uuid        | N               |
| reservation_reference | jsonb       | S               |
| funds_service         | varchar(80) | N               |
| remote_action_id      | uuid        | N               |
| requested_content     | jsonb       | N               |
| status                | varchar(30) | N               |
| movement_reference    | jsonb       | S               |
| T                     | Bloque      |                 |
| C                     | Bloque      |                 |
| S                     | Bloque      |                 |

FK a operación y hecho físico cuando exista.

UQ sobre effect_id y (funds_service, remote_action_id).

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_RESOLUTION.

La identidad del efecto se mantiene entre autorización, extracción y presentación posterior.

Una entrega confirmada con débito pendiente exige regularización monetaria, no otra entrega.

### **24.2. cash_own_position_application**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| application_id         | uuid        | N, PK           |
| cash_operation_id      | uuid        | N               |
| physical_fact_id       | uuid        | N               |
| treasury_service       | varchar(80) | N               |
| remote_action_id       | uuid        | N               |
| requested_content      | jsonb       | N               |
| status                 | varchar(30) | N               |
| own_movement_reference | jsonb       | S               |
| T                      | Bloque      |                 |
| C                      | Bloque      |                 |
| S                      | Bloque      |                 |

FK a operación y hecho.

UQ (physical_fact_id, treasury_service) y (treasury_service, remote_action_id).

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

El servicio coordina la posición propia con Tesorería. No mantiene un saldo de caja paralelo.

## **25. Diferencias de efectivo**

### **25.1. cash_difference**

| **Columna**             | **Tipo**      | **Nulabilidad** |
|-------------------------|---------------|-----------------|
| difference_id           | uuid          | N, PK           |
| cash_operation_id       | uuid          | N               |
| nature_code             | varchar(80)   | N               |
| amount                  | numeric(24,2) | N               |
| currency                | varchar(3)    | N               |
| related_fact_references | jsonb         | N               |
| investigation_owner     | varchar(80)   | N               |
| status                  | varchar(20)   | N               |
| T                       | Bloque        |                 |
| C                       | Bloque        |                 |

FK a operación.

Estados: OPEN, UNDER_INVESTIGATION, RESOLUTION_PENDING y RESOLVED.

Índice (investigation_owner, status, updated_at, difference_id).

### **25.2. cash_difference_resolution**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| resolution_id               | uuid        | N, PK           |
| difference_id               | uuid        | N               |
| conclusion_code             | varchar(80) | N               |
| verified_facts              | jsonb       | N               |
| treatment                   | varchar(40) | N               |
| preparer_person_id          | uuid        | N               |
| approver_person_id          | uuid        | S               |
| approval_reference          | jsonb       | S               |
| evidence_references         | jsonb       | N               |
| monetary_application_id     | uuid        | S               |
| own_position_application_id | uuid        | S               |
| T                           | Bloque      |                 |
| S                           | Bloque      |                 |

FK a diferencia y aplicaciones relacionadas.

Tratamientos:

- CONFIRM_ORIGINAL.

- REGULARIZE_MONETARY_EFFECT.

- REGULARIZE_OWN_CASH_POSITION.

- CONTINUE_INVESTIGATION.

Las regularizaciones exigen aprobación y personas preparadora y aprobadora distintas.

Índice (difference_id, created_at, resolution_id).

Cerrar la diferencia requiere comprobar las aplicaciones necesarias, no solamente enviar instrucciones.

Un faltante de caja no demuestra entrega al cliente. La resolución no borra hechos ni movimientos originales.

## **26. Orden de bloqueo y atomicidad**

### **26.1. Cuentas**

Orden:

1.  Cuenta.

2.  Control de autoridad.

3.  Cambio o cierre.

4.  Participantes, capacidades y preparaciones afectadas.

5.  Componentes y resultados.

No se consulta al coordinador dentro de la transacción de preparación.

### **26.2. Identificación bancaria**

Para alias:

1.  Controles de nombres, ordenados alfabéticamente.

2.  Identificación de cuenta.

3.  Cambio.

4.  Asignaciones.

Para CBU:

1.  Identificación de cuenta.

2.  Asignación.

Ningún proceso adquiere un control de nombre después de bloquear la identificación de cuenta.

### **26.3. Fondos**

Cuando hay intento:

1.  Identidad de ejecución.

2.  Intento.

3.  Posiciones ordenadas.

4.  Reservas ordenadas.

5.  Controles de corrección y afectación ordenados.

6.  Efectos y detalle.

Sin intento se conserva el mismo orden desde las posiciones.

Ningún comando bloquea una reserva antes de su posición.

Las transacciones confirman conjuntamente los detalles y acumulados que modifican. No existe movimiento confirmado sin
efecto, posición, secuencia e historia correspondientes.

### **26.4. Operaciones de efectivo**

Orden:

1.  Operación.

2.  Instrucción.

3.  Hechos y diferencia.

4.  Aplicaciones monetarias o de Tesorería.

Las llamadas físicas, monetarias y documentales ocurren fuera de las transacciones.

## **27. Eventos**

| **Contrato** | **Tipo**                      | **Tema**                           | **Agregado**           |
|--------------|-------------------------------|------------------------------------|------------------------|
| CI-04-031    | AccountChanged                | banking.account.events             | ACCOUNT                |
| CI-04-032    | BankIdentificationChanged     | banking.bank-identification.events | ACCOUNT_IDENTIFICATION |
| CI-04-033    | FundReservationChanged        | banking.funds.events               | FUND_POSITION          |
| CI-04-034    | MonetaryMovementConfirmed     | banking.funds.events               | FUND_POSITION          |
| CI-04-035    | OverdraftUtilizationChanged   | banking.funds.events               | FUND_POSITION          |
| CI-04-036    | AccountSettlementRecorded     | banking.account.events             | ACCOUNT                |
| CI-04-037    | CashOperationOutcomeRecorded  | banking.cash-operation.events      | CASH_OPERATION         |
| CI-04-038    | AccountClosureOutcomeRecorded | banking.account.events             | ACCOUNT                |
| CI-04-039    | MonetaryAffectationApplied    | banking.funds.events               | FUND_POSITION          |

La versión inicial del esquema es 1.

La clave de partición es accountId, excepto efectivo, que utiliza cashOperationId.

Los eventos se registran junto con sus efectos locales.

La versión de posición ordena cambios globales. La secuencia monetaria identifica exclusivamente movimientos.

Varios eventos del mismo cambio pueden compartir versión de posición, con identidades de evento diferentes.

Una reserva y su consumo no representan dos débitos. Un cambio de descubierto no representa por sí mismo otro desembolso
o pago.

## **28. Trabajos persistentes**

| **Trabajo**                      | **Identidad de deduplicación**          |
|----------------------------------|-----------------------------------------|
| Dependencia de constitución      | Cuenta y componente.                    |
| Aplicación de cambio operativo   | Cambio y componente.                    |
| Resolución de preparación        | Preparación.                            |
| Componente de cierre             | Cierre y componente.                    |
| Seguimiento de alias             | Cambio de alias.                        |
| Ejecución monetaria              | Intento.                                |
| Notificación a autoridad         | Intento y componente.                   |
| Revisión de reserva              | Reserva y versión revisada.             |
| Liquidación                      | Ejecución de liquidación.               |
| Recuperación física              | Instrucción física.                     |
| Aplicación monetaria de efectivo | Aplicación monetaria.                   |
| Aplicación propia de Tesorería   | Aplicación propia.                      |
| Resolución de diferencia         | Resolución.                             |
| Conciliación                     | Identidad de ejecución de conciliación. |

Las claves se construyen dentro del límite de 120 caracteres. Cuando la composición lo requiere se utiliza su hash
canónico.

Recuperar un trabajo conserva la actuación remota original.

La expiración de la concesión del trabajador no demuestra ausencia de ejecución, no libera cobertura ni autoriza otra
entrega.

## **29. Seguridad, migraciones y conservación**

Las migraciones utilizan Flyway por servicio.

Las referencias circulares de raíz y versión se implementan mediante FK diferibles. Las restricciones de exclusión
temporal utilizan el soporte PostgreSQL correspondiente.

Las migraciones preservan identidades económicas, secuencias, hashes y referencias de recuperación.

Se separan permisos para:

- Constituir y modificar cuentas.

- Administrar identificación.

- Consultar posiciones.

- Crear, consumir, modificar y liberar reservas.

- Ejecutar movimientos y correcciones.

- Aplicar descubierto.

- Aplicar medidas y acreditar condiciones.

- Registrar hechos físicos y resolver diferencias.

- Preparar, confirmar y abortar.

Consumir una reserva no concede permiso general para modificarla o liberarla.

No se eliminan movimientos, consumos, decisiones ni hechos físicos confirmados mediante cascadas.

Redis no conserva la única copia de saldos, reservas, identificadores confirmados, decisiones o hechos físicos.

Las consultas históricas y recuperaciones mantienen autorización y finalidad.

Después de restaurar un respaldo se pausan las instrucciones externas y se reconcilian actuaciones y hechos posteriores
al respaldo antes de reanudar.

## **30. Correspondencia con contratos técnicos**

| **Contratos**            | **Persistencia**                                                                |
|--------------------------|---------------------------------------------------------------------------------|
| CI-04-001 a CI-04-007    | Cuenta, versiones, participantes, capacidades, cambios, restricciones y cierre. |
| CI-04-008 a CI-04-011    | Identificación, CBU, alias, directorio y resolución.                            |
| CI-04-012                | Posición, historia, movimientos y cortes.                                       |
| CI-04-013 a CI-04-016    | Reservas, consumidores, coberturas, consumos, liberaciones y cambios.           |
| CI-04-017 y CI-04-018    | Efectos, movimientos, conjuntos e intentos.                                     |
| CI-04-019 y CI-04-020    | Aplicaciones y utilización de descubierto.                                      |
| CI-04-021                | Correcciones y control del alcance corregible.                                  |
| CI-04-022 y CI-04-023    | Actuaciones monetarias y consulta de reservas.                                  |
| CI-04-024                | Medidas, asignaciones y créditos condicionados.                                 |
| CI-04-025                | Liquidaciones, cálculos, bases y ejecución.                                     |
| CI-04-026 a CI-04-030    | Efectivo, hechos físicos, aplicaciones y diferencias.                           |
| CI-04-031 a CI-04-039    | Outbox e historia de agregados.                                                 |
| CI-04-040                | Identidad y contenido de la comprobación en account_operation_check.           |
| CI-04-041                | Decisión definitiva y recuperación por check_id en account_operation_check.     |
| Preparaciones de Cuentas | Preparación común, bases, participantes y control de autoridad.                 |
| Intentos de Fondos       | Identidad, intención, recursos, autoridades, decisión y recuperación.           |

## **31. Escenarios de verificación de implementación**

| **Escenario**                            | **Resultado exigido**                                   |
|------------------------------------------|---------------------------------------------------------|
| Constitución repetida                    | Recupera una sola cuenta.                               |
| Inicialización de posición               | Saldo cero sin movimiento ficticio.                     |
| Cambio de moneda                         | Se rechaza.                                             |
| Revocación concurrente con preparación   | Se ordena mediante cuenta y control de autoridad.       |
| Revalidaciones concurrentes de la misma comprobación | Devuelven la misma decisión definitiva. |
| Cambio de versión antes de confirmar la revalidación | Se vuelve a evaluar con las versiones vigentes. |
| Dependencia indisponible durante la revalidación | La comprobación queda pendiente, sin decisión definitiva. |
| CBU retirado                             | No se reutiliza.                                        |
| Dos cuentas solicitan el mismo alias     | No confirman asignaciones superpuestas.                 |
| Sustitución de alias incierta            | Conserva anterior y nombre solicitado bajo seguimiento. |
| Saldo 100 y reserva 30                   | Saldo 100 y disponibilidad propia 70.                   |
| Consumo por 30                           | Saldo 70 y remanente cero, sin doble descuento.         |
| Consumo y liberación concurrentes        | No utilizan dos veces el remanente.                     |
| Efecto recibido por circuitos distintos  | Un único movimiento confirmado.                         |
| Recursos propios insuficientes           | No confirma coberturas incompatibles.                   |
| Medidas superpuestas                     | Descuenta la unión de porciones, no la suma duplicada.  |
| Levantamiento de una causa               | Conserva las restantes.                                 |
| Transferencia con destino impedido       | No confirma solamente el débito.                        |
| Cambio interno                           | Conserva ambas piernas e importes aceptados.            |
| Confirmación y aborto simultáneos        | Una sola decisión durable.                              |
| Aborto de consumo                        | No libera automáticamente una reserva firme.            |
| Reducción de descubierto                 | Conserva capital y cobertura firme o incierta.          |
| Crédito sobre saldo negativo             | Reduce capital sin cobro implícito de intereses.        |
| Crédito condicionado 100 y liberación 40 | Conserva 60 condicionados sin otro crédito.             |
| Correcciones concurrentes                | No superan el alcance corregible.                       |
| Movimiento posterior con fecha anterior  | No altera las páginas de un corte fijado.               |
| Liquidación calculada sin movimiento     | No se informa cobrada.                                  |
| Entrega física incierta                  | No presume fallo ni vuelve a entregar.                  |
| Entrega confirmada con débito pendiente  | Regulariza dinero sin repetir efectivo.                 |
| Inventario sin respuesta                 | Permanece pendiente.                                    |
| Cierre con obligaciones                  | Conserva administración residual.                       |
| Evento duplicado o restauración          | Recupera identidades y evita repetir efectos.           |

Las restricciones, exclusiones, bloqueos, secuencias y transacciones atómicas se verifican durante la implementación con
PostgreSQL real y escenarios concurrentes.
