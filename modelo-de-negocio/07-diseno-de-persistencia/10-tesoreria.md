# **Diseño de persistencia — Módulo 10. Tesorería**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Tesorería.

| **Servicio**               | **Responsabilidad**                                                                            |
|----------------------------|------------------------------------------------------------------------------------------------|
| Posiciones y liquidez      | Recursos propios, ubicaciones, efectivo, compromisos, flujos, proyecciones y cierre operativo. |
| Operaciones de tesorería   | Financiación e inversiones propias, rendimientos, derechos y obligaciones.                     |
| Cambio de moneda           | Cotizaciones, propuestas, aceptación, ejecución y recuperación de intercambios.                |
| Liquidaciones de tesorería | Obligaciones, instrucciones, confirmaciones y conciliación entre participantes.                |

Comprende los contratos CI-10-001 a CI-10-039.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases.

Los saldos de clientes pertenecen a Fondos. Los recursos propios pertenecen a Posiciones y liquidez.

Un cambio interno entre cuentas de clientes no demuestra una salida de efectivo ni un movimiento de una cuenta externa
del banco.

Una financiación concertada no representa dinero recibido. Una instrucción de liquidación no demuestra pago.

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
| Contador transaccional  | bigint         | No negativo; empieza en cero.      |
| Secuencia asignada      | bigint         | Positiva.                          |
| Código                  | varchar(80)    | No vacío y registrado.             |
| Referencia externa      | varchar(120)   | No vacía.                          |
| Denominación            | varchar(200)   | No vacía.                          |
| Fundamento              | varchar(4000)  | No vacío cuando está presente.     |
| Moneda                  | varchar(3)     | ARS o USD.                         |
| Importe monetario       | numeric(24,2)  | No negativo.                       |
| Saldo o variación       | numeric(24,2)  | Con signo cuando se indica.        |
| Cantidad                | numeric(30,12) | No negativa.                       |
| Precio                  | numeric(30,12) | Positivo.                          |
| Tasa                    | numeric(24,12) | Conforme a su definición.          |
| Valor de cálculo        | numeric(30,12) | Conforme a su significado.         |
| Hash                    | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Instante                | timestamptz    | Intercambio en UTC.                |
| Fecha                   | date           | Fecha bancaria.                    |
| Contenido               | jsonb          | Esquema registrado y versionado.   |

Los importes y cantidades ejecutables son positivos.

Se valida precisión y escala antes de persistir.

Los cálculos intermedios conservan al menos doce decimales. Los resultados monetarios utilizan HALF_UP.

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

Los cuatro servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Las preparaciones de facultades se referencian desde sus autoridades. No se confunden con compromisos de recursos.

## **3. Esquemas estructurados y coordinaciones**

### **3.1. Esquemas**

Cada JSONB conserva su versión mediante columna explícita o envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                  | **Contenido**                                              |
|------------------------------|------------------------------------------------------------|
| RemoteReference              | Propietario, tipo, identidad y versión cuando corresponde. |
| EvidenceReferences           | Hechos y evidencias identificados.                         |
| PendingComponents            | Componente, responsable, motivo y seguimiento.             |
| SettlementDestination        | Destino, versión, verificación y participante.             |
| TreasuryCommitmentConditions | Hecho exigible, propietario y condiciones de conclusión.   |
| TreasuryOperationTerms       | Capital, plazo, tasa, flujos, garantías y condiciones.     |
| EconomicComponents           | Capital, interés, cargos, tributos y ajustes separados.    |
| LiquidityComposition         | Flujos, obligaciones y compromisos relacionados.           |
| LiquidityScenario            | Hipótesis y parámetros de Riesgos versionados.             |
| ForeignExchangeProposal      | Monedas, importes finales, cargos, destinos y conformidad. |
| SettlementCommand            | Obligación, recursos, destino y autorización.              |
| ReconciliationEvidence       | Fuentes, cortes, comparación y diferencias.                |
| TreasuryPositionSnapshot     | Saldo, recursos, restricciones y tránsito.                 |

No se admiten scripts, SQL, URL arbitrarias ni campos desconocidos.

Los atributos utilizados en unicidad, concurrencia y recuperación se almacenan en columnas explícitas.

### **3.2. Coordinaciones remotas**

Las tablas siguientes comparten estructura:

| **Tabla**                               | **Propietario N**          | **FK local**                    |
|-----------------------------------------|----------------------------|---------------------------------|
| treasury_operation_coordination         | treasury_operation_id uuid | treasury_operation              |
| liquidity_coverage_action               | coverage_plan_id uuid      | liquidity_coverage_plan         |
| foreign_exchange_coordination_component | fx_operation_id uuid       | foreign_exchange_operation      |
| treasury_settlement_local_application   | fulfillment_id uuid        | treasury_settlement_fulfillment |

Columnas adicionales:

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| step_id                             | uuid        | N, PK           |
| component_code, responsible_service | varchar(80) | N               |
| remote_action_id                    | uuid        | N               |
| economic_effect_id                  | uuid        | S               |
| requested_content                   | jsonb       | N               |
| status                              | varchar(30) | N               |
| result_reference, result_payload    | jsonb       | S               |
| evidence_references                 | jsonb       | N               |
| last_checked_at, next_check_at      | timestamptz | S               |
| T, C, S                             | Bloques     |                 |

Cada tabla declara:

- UQ (propietario, component_code).

- UQ (responsible_service, remote_action_id).

- Índice (status, next_check_at, step_id).

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de llamar al propietario. La recuperación conserva contenido, actuación y efecto
originales.

## **4. Ubicaciones de recursos propios**

### **4.1. treasury_resource_location**

| **Columna**                              | **Tipo**     | **Nulabilidad** |
|------------------------------------------|--------------|-----------------|
| location_id                              | uuid         | N, PK           |
| code                                     | varchar(80)  | N               |
| name                                     | varchar(200) | N               |
| location_type                            | varchar(30)  | N               |
| institution_reference, holder_reference  | jsonb        | N               |
| currency                                 | varchar(3)   | N               |
| purpose_code                             | varchar(80)  | N               |
| capability_codes, responsible_references | jsonb        | N               |
| physical_function_code                   | varchar(80)  | S               |
| external_identifier                      | varchar(120) | S               |
| current_destination_version              | bigint       | N               |
| status                                   | varchar(20)  | N               |
| T, C                                     | Bloques      |                 |

UQ sobre código.

Tipos EXTERNAL_ACCOUNT y PHYSICAL_CASH_LOCATION.

La ubicación física exige función registrada. La titularidad debe corresponder al banco.

Estados DRAFT, ENABLED, LIMITED, CLOSING y RETIRED.

Registrar una ubicación no crea saldo.

### **4.2. treasury_location_destination_version**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| location_id            | uuid        | N               |
| destination_version    | bigint      | N               |
| destination_type       | varchar(20) | N               |
| destination_identifier | jsonb       | N               |
| participant_reference  | jsonb       | S               |
| currency               | varchar(3)  | N               |
| verification_reference | jsonb       | N               |
| approval_reference     | jsonb       | S               |
| valid_from             | timestamptz | N               |
| valid_until            | timestamptz | S               |
| T, S                   | Bloques     |                 |

PK sobre ubicación y versión. FK a ubicación.

Tipos EXTERNAL_ACCOUNT y CASH_LOCATION.

Intervalos semiabiertos, sin superposición por ubicación.

Los cambios sensibles exigen verificación independiente y aprobación aplicable.

Una instrucción conserva la versión utilizada; no se redirige al cambiar el destino actual.

### **4.3. treasury_location_restriction**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| restriction_id, location_id   | uuid        | N               |
| authority_service, cause_code | varchar(80) | N               |
| source_restriction_id         | uuid        | N               |
| source_version                | bigint      | N               |
| scope_payload                 | jsonb       | N               |
| valid_from                    | timestamptz | N               |
| valid_until                   | timestamptz | S               |
| status                        | varchar(20) | N               |
| T, C                          | Bloques     |                 |

PK restriction_id, FK a ubicación.

UQ (authority_service, source_restriction_id, source_version, location_id).

Estados ACTIVE y LIFTED.

### **4.4. treasury_location_retirement**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| retirement_id, location_id       | uuid        | N               |
| requested_at                     | timestamptz | N               |
| inventory_cut, inventory_content | jsonb       | N               |
| treatment_references             | jsonb       | N               |
| status                           | varchar(30) | N               |
| T, C, S                          | Bloques     |                 |

PK retirement_id, FK a ubicación.

Estados CHECKING, TREATMENT_PENDING y RETIRED.

El inventario identifica saldo, compromisos, tránsitos e incertidumbres. La ausencia de respuesta no equivale a ausencia
de recursos.

## **5. Posiciones propias**

### **5.1. treasury_position**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| position_id, location_id                 | uuid          | N               |
| currency                                 | varchar(3)    | N               |
| position_version, last_movement_sequence | bigint        | N               |
| confirmed_balance                        | numeric(24,2) | N               |
| new_capacity, shortage                   | numeric(24,2) | N               |
| created_at, updated_at                   | timestamptz   | N               |
| lock_version                             | bigint        | N               |

PK position_id, FK a ubicación.

UQ (location_id, currency).

El saldo admite signo. Capacidad y faltante son no negativos.

La moneda coincide con la ubicación.

La posición inicial tiene saldo y secuencia cero. Un saldo inicial distinto requiere movimiento con hecho y evidencia.

Los cobros esperados no aumentan saldo confirmado.

### **5.2. treasury_position_history**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| position_id                        | uuid          | N               |
| position_version                   | bigint        | N               |
| confirmed_balance                  | numeric(24,2) | N               |
| new_capacity, shortage             | numeric(24,2) | N               |
| last_movement_sequence             | bigint        | N               |
| resource_snapshot, cause_reference | jsonb         | N               |
| recorded_at                        | timestamptz   | N               |
| T, S                               | Bloques       |                 |

PK sobre posición y versión. FK a posición.

El saldo admite signo. La instantánea es inmutable.

### **5.3. treasury_resource_movement**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| movement_id, position_id, effect_id | uuid          | N               |
| movement_sequence, position_version | bigint        | N               |
| source_service, source_type         | varchar(80)   | N               |
| source_id                           | uuid          | N               |
| direction                           | varchar(3)    | N               |
| amount                              | numeric(24,2) | N               |
| currency                            | varchar(3)    | N               |
| effective_date                      | date          | N               |
| recorded_at                         | timestamptz   | N               |
| commitment_id, original_movement_id | uuid          | S               |
| evidence_references                 | jsonb         | N               |
| T                                   | Bloque        |                 |

PK movement_id.

FK a posición, historia, compromiso y movimiento original.

UQ sobre efecto y (position_id, movement_sequence).

Sentido IN u OUT. Importe y secuencia positivos.

La secuencia se incrementa en la propia posición dentro de la transacción.

Una salida requiere hecho comprobado. La instrucción no modifica saldo.

## **6. Compromisos de recursos**

### **6.1. treasury_resource_catalog_control**

Columnas N:

- control_id smallint, PK con valor 1.

- catalog_version bigint.

- lock_version bigint.

- updated_at timestamptz.

Serializa cambios del conjunto de posiciones asignadas a compromisos y permite determinar sus bloqueos sin omitir una
reasignación concurrente.

### **6.2. treasury_resource_commitment**

| **Columna**                                        | **Tipo**      | **Nulabilidad** |
|----------------------------------------------------|---------------|-----------------|
| commitment_id                                      | uuid          | N, PK           |
| commitment_class                                   | varchar(30)   | N               |
| owner_service, origin_component_code               | varchar(80)   | N               |
| origin_operation_id                                | uuid          | N               |
| obligation_reference                               | jsonb         | S               |
| currency                                           | varchar(3)    | N               |
| current_amount, consumed_amount                    | numeric(24,2) | N               |
| released_amount, resolved_amount, remaining_amount | numeric(24,2) | N               |
| firmness                                           | varchar(20)   | N               |
| required_date                                      | date          | N               |
| conclusion_conditions                              | jsonb         | N               |
| status                                             | varchar(30)   | N               |
| T, C, S                                            | Bloques       |                 |

UQ (owner_service, origin_operation_id, origin_component_code, commitment_class).

Clases:

- EXTERNAL_PAYMENT.

- PHYSICAL_CASH.

- OWN_INVESTMENT.

- INTERNAL_FX_PREPARATION.

Vigente = consumido + liberado + resuelto + remanente.

Firmezas CANCELLABLE, FIRM y UNCERTAIN.

Estados ACTIVE, PARTIALLY_CONSUMED, CONSUMED, RELEASED, RESOLVED_INTERNAL_FX y RESOLUTION_PENDING.

Solo INTERNAL_FX_PREPARATION utiliza resolución interna sin consumo externo.

La clase es inmutable.

### **6.3. treasury_commitment_assignment**

| **Columna**                | **Tipo**      | **Nulabilidad** |
|----------------------------|---------------|-----------------|
| commitment_id, position_id | uuid          | N               |
| remaining_assigned_amount  | numeric(24,2) | N               |
| updated_at                 | timestamptz   | N               |

PK sobre compromiso y posición. FK a ambos.

Índice inverso (position_id, commitment_id).

La suma asignada coincide con el remanente del compromiso.

La asignación comprueba moneda, restricciones y capacidad en la misma transacción.

### **6.4. treasury_commitment_action**

| **Columna**                                 | **Tipo**      | **Nulabilidad** |
|---------------------------------------------|---------------|-----------------|
| commitment_action_id, commitment_id         | uuid          | N               |
| action                                      | varchar(30)   | N               |
| previous_amount, resulting_amount           | numeric(24,2) | N               |
| previous_assignments, resulting_assignments | jsonb         | N               |
| reason, evidence_references                 | jsonb         | N               |
| T, S                                        | Bloques       |                 |

PK commitment_action_id, FK a compromiso.

Acciones MODIFY, REALLOCATE, RELEASE_CANCELLED y RESOLVE_INTERNAL_FX.

Modificar no reduce el total por debajo de consumido, liberado y resuelto.

La reasignación aplica el cambio completo, sin duplicar cobertura.

Liberar no genera ingreso. La incertidumbre impide liberar por vencimiento administrativo.

### **6.5. treasury_commitment_consumption**

Columnas N: consumption_id uuid PK, commitment_id uuid FK, movement_id uuid FK y UQ, amount numeric(24,2) y bloque T.

La salida y el consumo se confirman conjuntamente.

## **7. Afectaciones y disponibilidad**

### **7.1. treasury_resource_allocation**

| **Columna**                | **Tipo**      | **Nulabilidad** |
|----------------------------|---------------|-----------------|
| allocation_id, position_id | uuid          | N               |
| amount                     | numeric(24,2) | N               |
| status                     | varchar(20)   | N               |
| origin_position_version    | bigint        | N               |
| ended_position_version     | bigint        | S               |

PK allocation_id, FK a posición e historia.

Estados HELD, CONSUMED y RELEASED.

Importe positivo.

### **7.2. treasury_resource_condition**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| condition_id, position_id          | uuid          | N               |
| owner_service, condition_code      | varchar(80)   | N               |
| source_condition_id                | uuid          | N               |
| source_version                     | bigint        | N               |
| scope_payload, evidence_references | jsonb         | N               |
| amount                             | numeric(24,2) | N               |
| status                             | varchar(20)   | N               |
| T, C                               | Bloques       |                 |

PK condition_id, FK a posición.

UQ (owner_service, source_condition_id, source_version, position_id).

Representa inmovilizaciones o condiciones de disponibilidad. Estados ACTIVE y LIFTED.

### **7.3. treasury_resource_allocation_cause**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| allocation_id, cause_id     | uuid        | N               |
| cause_type                  | varchar(20) | N               |
| commitment_id, condition_id | uuid        | S               |

PK sobre asignación y causa.

FK a asignación y causa local correspondiente.

COMMITMENT exige compromiso; CONDITION exige condición. Son excluyentes.

Una porción puede tener varias causas superpuestas, pero no respaldar dos obligaciones de gasto independientes.

Capacidad nueva = máximo entre cero y saldo confirmado − asignaciones HELD distintas.

Faltante = máximo entre cero y asignaciones HELD distintas − saldo confirmado.

El saldo y las asignaciones pueden producir faltante sin borrar compromisos previos.

### **7.4. treasury_resource_history**

Columnas N: history_id uuid PK, position_id uuid, position_version bigint, resource_type varchar(80), resource_id uuid,
change_code varchar(80), resulting_snapshot jsonb y bloques T y S.

Columna S: previous_snapshot jsonb.

FK a historia de posición.

Las comprobaciones entre asignaciones y causas se ejecutan bajo el control de posición.

## **8. Efectivo propio y tránsito**

### **8.1. cash_resource_transfer**

| **Columna**                                              | **Tipo**      | **Nulabilidad** |
|----------------------------------------------------------|---------------|-----------------|
| transfer_id, source_location_id, destination_location_id | uuid          | N               |
| currency                                                 | varchar(3)    | N               |
| requested_amount                                         | numeric(24,2) | N               |
| planned_date                                             | date          | N               |
| responsible_references, approval_reference               | jsonb         | N               |
| commitment_id                                            | uuid          | N               |
| status                                                   | varchar(30)   | N               |
| T, C, S                                                  | Bloques       |                 |

PK transfer_id.

FK a ubicaciones y compromiso.

Origen y destino distintos y físicos. Moneda coincidente.

Estados PREPARING, READY, IN_TRANSIT, PARTIALLY_RECEIVED, COMPLETED y REQUIRES_RESOLUTION.

### **8.2. cash_resource_transfer_component**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| component_id, transfer_id           | uuid          | N               |
| requested_amount, dispatched_amount | numeric(24,2) | N               |
| received_amount, returned_amount    | numeric(24,2) | N               |
| cancelled_amount                    | numeric(24,2) | N               |
| status                              | varchar(30)   | N               |
| lock_version                        | bigint        | N               |

PK component_id, FK a traslado.

Despachado + cancelado no supera solicitado. Recibido + retornado no supera despachado.

### **8.3. cash_resource_physical_fact**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| fact_id, transfer_id, component_id | uuid          | N               |
| source_service                     | varchar(80)   | N               |
| source_fact_id                     | uuid          | N               |
| fact_type                          | varchar(10)   | N               |
| amount                             | numeric(24,2) | N               |
| currency                           | varchar(3)    | N               |
| occurred_at                        | timestamptz   | N               |
| evidence_references                | jsonb         | N               |
| T, S                               | Bloques       |                 |

PK fact_id.

FK a traslado y componente propio.

UQ (source_service, source_fact_id).

Tipos DISPATCHED, RECEIVED y RETURNED.

### **8.4. cash_resource_transit**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| transit_id, component_id, dispatch_fact_id          | uuid          | N               |
| dispatched_amount, received_amount, returned_amount | numeric(24,2) | N               |
| remaining_transit_amount                            | numeric(24,2) | N               |
| lock_version                                        | bigint        | N               |
| updated_at                                          | timestamptz   | N               |

PK transit_id.

FK a componente y hecho de despacho. UQ sobre despacho.

Tránsito = despachado − recibido − retornado.

La salida reduce origen, consume compromiso y crea tránsito.

La recepción reduce ese tránsito e incrementa destino.

Si salen 100 y llegan 90, permanecen 10 identificados. No se crea efectivo para completar.

### **8.5. cash_resource_transit_application**

Columnas N: application_id uuid PK, transit_id uuid FK, physical_fact_id uuid FK y UQ, movement_id uuid FK y UQ, amount
numeric(24,2) y bloque T.

Relaciona cada recepción o retorno con el tránsito y movimiento correspondientes.

## **9. Arqueos**

### **9.1. cash_resource_count**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| count_id, location_id                   | uuid          | N               |
| currency                                | varchar(3)    | N               |
| cut_reference                           | jsonb         | N               |
| counted_at                              | timestamptz   | N               |
| recorded_amount, counted_amount         | numeric(24,2) | N               |
| difference_amount                       | numeric(24,2) | N               |
| custodian_person_id, verifier_person_id | uuid          | N               |
| evidence_references                     | jsonb         | N               |
| T, S                                    | Bloques       |                 |

PK count_id, FK a ubicación.

Diferencia con signo = contado − registrado.

Custodio y verificador distintos.

### **9.2. cash_resource_count_resolution**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| resolution_id, count_id                    | uuid        | N               |
| investigation_result, authorized_treatment | jsonb       | N               |
| preparer_person_id, approver_person_id     | uuid        | N               |
| approval_reference                         | jsonb       | N               |
| resulting_movement_id                      | uuid        | S               |
| status                                     | varchar(20) | N               |
| T, C, S                                    | Bloques     |                 |

PK resolution_id, FK a arqueo y movimiento.

Personas distintas.

El arqueo no modifica saldo automáticamente ni demuestra una entrega al cliente.

## **10. Flujos de liquidez**

### **10.1. liquidity_flow**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| flow_id         | uuid        | N, PK           |
| owner_service   | varchar(80) | N               |
| source_flow_id  | uuid        | N               |
| current_version | bigint      | N               |
| T               | Bloque      |                 |
| lock_version    | bigint      | N               |

UQ (owner_service, source_flow_id).

### **10.2. liquidity_flow_version**

| **Columna**                           | **Tipo**      | **Nulabilidad** |
|---------------------------------------|---------------|-----------------|
| flow_id                               | uuid          | N               |
| flow_version, source_version          | bigint        | N               |
| action                                | varchar(10)   | N               |
| operation_reference                   | jsonb         | N               |
| obligation_reference                  | jsonb         | S               |
| currency                              | varchar(3)    | S               |
| amount                                | numeric(24,2) | S               |
| direction                             | varchar(3)    | S               |
| expected_date                         | date          | S               |
| certainty                             | varchar(20)   | S               |
| source_reference, evidence_references | jsonb         | N               |
| withdrawal_reason                     | varchar(4000) | S               |
| T, S                                  | Bloques       |                 |

PK sobre flujo y versión. FK a flujo.

UQ (flow_id, source_version).

UPSERT exige datos económicos. WITHDRAW exige fundamento y no requiere repetirlos.

Sentido IN u OUT.

Certezas CONTRACTUAL, FIRM, ESTIMATED y UNCERTAIN.

Una versión antigua no sustituye una posterior.

### **10.3. liquidity_composition_control**

Columnas N: control_id smallint PK con valor 1, composition_version bigint, lock_version bigint y updated_at
timestamptz.

### **10.4. liquidity_flow_composition**

Columnas N: aggregate_flow_id uuid, component_flow_id uuid y effective_from timestamptz.

Columna S: effective_until timestamptz.

PK sobre agregado, componente e inicio. FK a flujos.

Se prohíben autorreferencia y ciclos bajo control de composición.

### **10.5. liquidity_obligation_representation**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| representation_id                         | uuid        | N, PK           |
| owner_service                             | varchar(80) | N               |
| source_obligation_id                      | uuid        | N               |
| currency                                  | varchar(3)  | N               |
| selected_flow_ids, related_commitment_ids | jsonb       | N               |
| representation_rule                       | varchar(80) | N               |
| effective_from                            | timestamptz | N               |
| effective_until                           | timestamptz | S               |
| T, S                                      | Bloques     |                 |

UQ parcial por propietario, obligación y moneda mientras esté vigente.

La proyección selecciona agregado o componentes. No suma ambos ni descuenta dos veces flujo y compromiso de la misma
obligación.

## **11. Proyecciones y cobertura**

### **11.1. liquidity_projection**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| projection_id                         | uuid        | N, PK           |
| cut_reference, scope_definition       | jsonb       | N               |
| scenario_reference                    | jsonb       | N               |
| source_versions, missing_source_codes | jsonb       | N               |
| completeness                          | varchar(20) | N               |
| T, S                                  | Bloques     |                 |

Completitud COMPLETE o INCOMPLETE.

### **11.2. liquidity_projection_horizon**

| **Columna**                           | **Tipo**      | **Nulabilidad** |
|---------------------------------------|---------------|-----------------|
| projection_id                         | uuid          | N               |
| currency                              | varchar(3)    | N               |
| horizon                               | varchar(30)   | N               |
| base_balance                          | numeric(24,2) | N               |
| expected_inflows, expected_outflows   | numeric(24,2) | N               |
| commitments, shortage, surplus        | numeric(24,2) | N               |
| assumptions, selected_representations | jsonb         | N               |
| missing_source_codes                  | jsonb         | N               |

PK sobre proyección, moneda y horizonte. FK a proyección.

Saldo base con signo.

Horizontes INTRADAY, NEXT_BUSINESS_DAY, SEVEN_DAYS y THIRTY_DAYS.

### **11.3. liquidity_need**

Columnas N: need_id uuid PK, currency varchar(3), required_amount numeric(24,2), required_date date,
obligation_references jsonb, cause_code varchar(80), confirmed_coverage numeric(24,2), remaining_shortage numeric(24,2)
y bloques T y C.

Necesidad = cobertura comprobada + faltante.

### **11.4. liquidity_coverage_plan**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| coverage_plan_id, need_id | uuid        | N               |
| alternatives              | jsonb       | N               |
| status                    | varchar(20) | N               |
| T, C, S                   | Bloques     |                 |

PK coverage_plan_id, FK a necesidad.

Estados PROPOSED, IN_PROGRESS, COVERED y REQUIRES_REVIEW.

Las alternativas identifican modalidad, responsable, fecha y actuación prevista.

Una financiación solicitada o una inversión pendiente no equivale a disponibilidad comprobada.

## **12. Operaciones propias de tesorería**

### **12.1. treasury_operation**

| **Columna**                                    | **Tipo**     | **Nulabilidad** |
|------------------------------------------------|--------------|-----------------|
| treasury_operation_id                          | uuid         | N, PK           |
| origin_service                                 | varchar(80)  | N               |
| origin_reference                               | varchar(120) | N               |
| operation_type                                 | varchar(30)  | N               |
| counterparty_reference, contract_reference     | jsonb        | N               |
| currency                                       | varchar(3)   | N               |
| current_terms_version                          | bigint       | N               |
| calendar_references, own_collateral_references | jsonb        | N               |
| risk_references                                | jsonb        | N               |
| approval_reference                             | jsonb        | S               |
| status                                         | varchar(30)  | N               |
| T, C                                           | Bloques      |                 |

UQ (origin_service, origin_reference).

Modalidades OWN_BORROWING, OWN_CREDIT_LINE, OWN_DEPOSIT, INTERBANK_LOAN, FIXED_INCOME_PURCHASE y FIXED_INCOME_SALE.

Estados PREPARING, APPROVED, CONTRACTED, EXECUTION_PENDING, ACTIVE, FULFILLMENT_PENDING, CLOSED y REQUIRES_RESOLUTION.

### **12.2. treasury_operation_terms_version**

| **Columna**                                    | **Tipo**       | **Nulabilidad** |
|------------------------------------------------|----------------|-----------------|
| treasury_operation_id                          | uuid           | N               |
| terms_version                                  | bigint         | N               |
| capital_amount                                 | numeric(24,2)  | S               |
| term_quantity                                  | integer        | S               |
| term_unit                                      | varchar(10)    | S               |
| rate_definition                                | jsonb          | S               |
| instrument_reference                           | jsonb          | S               |
| quantity, price                                | numeric(30,12) | S               |
| quotation_basis                                | varchar(20)    | S               |
| charges, contractual_flows, modification_rules | jsonb          | N               |
| effective_from                                 | timestamptz    | N               |
| effective_until                                | timestamptz    | S               |
| preparer_person_id                             | uuid           | N               |
| approver_person_id                             | uuid           | S               |
| approval_reference                             | jsonb          | S               |
| T, S                                           | Bloques        |                 |

PK sobre operación y versión. FK a operación.

La modalidad determina campos exigibles. Las operaciones de títulos requieren instrumento, cantidad, precio y base.

Aplicar condiciones exige aprobación correspondiente y personas distintas cuando hay aprobación humana.

Concertar no acredita dinero.

## **13. Ejecuciones y líneas propias**

### **13.1. treasury_operation_execution**

| **Columna**                                         | **Tipo**       | **Nulabilidad** |
|-----------------------------------------------------|----------------|-----------------|
| execution_id, treasury_operation_id, participant_id | uuid           | N               |
| mechanism_code                                      | varchar(80)    | N               |
| mechanism_version                                   | bigint         | N               |
| external_execution_id                               | varchar(120)   | N               |
| result                                              | varchar(10)    | N               |
| effective_date                                      | date           | N               |
| amount                                              | numeric(24,2)  | S               |
| currency                                            | varchar(3)     | S               |
| quantity, price                                     | numeric(30,12) | S               |
| instrument_reference                                | jsonb          | S               |
| evidence_references                                 | jsonb          | N               |
| T, S                                                | Bloques        |                 |

PK execution_id, FK a operación.

UQ (participant_id, mechanism_code, external_execution_id).

Resultados ACCEPTED, EXECUTED, REJECTED y UNCERTAIN.

Las variantes monetaria y de títulos exigen sus campos. Cada parcial conserva identidad.

### **13.2. treasury_credit_line_capacity**

Columnas N: treasury_operation_id uuid PK y FK, limit_amount numeric(24,2), used_amount numeric(24,2), pending_amount
numeric(24,2), available_amount numeric(24,2), lock_version bigint y updated_at timestamptz.

Solo existe para OWN_CREDIT_LINE.

Disponible = máximo entre cero y límite − utilizado − pendiente.

### **13.3. treasury_credit_line_drawdown**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| drawdown_id, treasury_operation_id | uuid          | N               |
| amount                             | numeric(24,2) | N               |
| execution_id                       | uuid          | S               |
| destination_reference              | jsonb         | N               |
| status                             | varchar(30)   | N               |
| T, C, S                            | Bloques       |                 |

PK drawdown_id.

FK a línea y ejecución.

Estados COMMITTED, EXECUTION_PENDING, CONFIRMED, REJECTED y UNCERTAIN.

Una línea contratada no integra saldo confirmado por todo su límite.

## **14. Instrumentos propios del banco**

### **14.1. treasury_own_instrument_position**

| **Columna**                                                 | **Tipo**       | **Nulabilidad** |
|-------------------------------------------------------------|----------------|-----------------|
| position_id                                                 | uuid           | N, PK           |
| bank_holder_reference                                       | jsonb          | N               |
| instrument_service                                          | varchar(80)    | N               |
| instrument_id                                               | uuid           | N               |
| class_code, custody_service                                 | varchar(80)    | N               |
| custody_account_reference                                   | varchar(120)   | N               |
| registered_quantity, committed_quantity, available_quantity | numeric(30,12) | N               |
| position_version, last_movement_sequence                    | bigint         | N               |
| lock_version                                                | bigint         | N               |
| updated_at                                                  | timestamptz    | N               |

UQ por instrumento, clase, custodio y cuenta de custodia.

Disponible = registrado − comprometido.

### **14.2. treasury_own_instrument_commitment**

Columnas N: commitment_id uuid PK, position_id uuid FK, treasury_operation_id uuid FK, current_quantity numeric(30,12),
consumed_quantity numeric(30,12), released_quantity numeric(30,12), remaining_quantity numeric(30,12), status
varchar(30) y bloques T y C.

UQ (position_id, treasury_operation_id).

Vigente = consumido + liberado + remanente.

### **14.3. treasury_own_instrument_movement**

| **Columna**                           | **Tipo**       | **Nulabilidad** |
|---------------------------------------|----------------|-----------------|
| movement_id, position_id, effect_id   | uuid           | N               |
| movement_sequence                     | bigint         | N               |
| direction                             | varchar(3)     | N               |
| quantity                              | numeric(30,12) | N               |
| source_reference, settlement_evidence | jsonb          | N               |
| commitment_id                         | uuid           | S               |
| effective_date                        | date           | N               |
| T                                     | Bloque         |                 |

PK movement_id.

FK a posición y compromiso.

UQ sobre efecto y (position_id, movement_sequence).

Salida y consumo confirman conjuntamente.

No se venden unidades inexistentes ni se utilizan activos de clientes.

## **15. Devengamientos y obligaciones propias**

### **15.1. treasury_operation_accrual**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| accrual_id, treasury_operation_id        | uuid           | N               |
| concept_code                             | varchar(80)    | N               |
| period_from, period_to                   | date           | N               |
| terms_version                            | bigint         | N               |
| methodology_reference, source_references | jsonb          | N               |
| unrounded_amount                         | numeric(30,12) | N               |
| rounded_amount                           | numeric(24,2)  | N               |
| original_accrual_id                      | uuid           | S               |
| status                                   | varchar(20)    | N               |
| T, S                                     | Bloques        |                 |

PK accrual_id.

FK a operación, condiciones y determinación original.

Estados DETERMINED, APPLIED, SUPERSEDED e INCOMPLETE.

Se excluyen intervalos activos superpuestos por operación y concepto.

### **15.2. treasury_operation_accrual_segment**

Columnas N: accrual_id uuid FK, position integer, period_from date, period_to date, principal_basis numeric(24,2), rate
numeric(24,12), days integer, unrounded_amount numeric(30,12) y source_reference jsonb.

PK sobre devengamiento y posición.

Tasa fija con interés simple Actual/365, salvo flujos específicos del instrumento. No se capitaliza automáticamente.

### **15.3. treasury_operation_obligation**

| **Columna**                          | **Tipo**       | **Nulabilidad** |
|--------------------------------------|----------------|-----------------|
| obligation_id, treasury_operation_id | uuid           | N               |
| origin_type, occurrence_code         | varchar(80)    | N               |
| origin_id                            | uuid           | N               |
| concept_code, direction              | varchar(80)    | N               |
| amount                               | numeric(24,2)  | S               |
| currency                             | varchar(3)     | S               |
| quantity                             | numeric(30,12) | S               |
| instrument_reference                 | jsonb          | S               |
| due_date                             | date           | N               |
| fulfilled_amount                     | numeric(24,2)  | S               |
| fulfilled_quantity                   | numeric(30,12) | S               |
| status                               | varchar(20)    | N               |
| T, C                                 | Bloques        |                 |

PK obligation_id, FK a operación.

UQ (treasury_operation_id, origin_type, origin_id, occurrence_code).

Variantes monetaria y de instrumentos excluyentes.

### **15.4. treasury_operation_fulfillment**

Columnas N: fulfillment_id uuid PK, obligation_id uuid FK, effect_id uuid UQ, effective_date date, evidence_references
jsonb y bloque T.

Columnas S: amount numeric(24,2) y quantity numeric(30,12), según la obligación.

El cumplimiento no supera el remanente.

Cobrar interés devengado cancela el derecho sin reconocer otro ingreso.

## **16. Modificaciones y valuación**

### **16.1. treasury_operation_modification**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| modification_id, treasury_operation_id | uuid        | N               |
| modification_type                      | varchar(30) | N               |
| source_terms_version                   | bigint      | N               |
| resulting_terms_version                | bigint      | S               |
| effective_agreement, proposed_terms    | jsonb       | N               |
| risk_reference, approval_reference     | jsonb       | N               |
| execution_absence_evidence             | jsonb       | S               |
| status                                 | varchar(30) | N               |
| T, C, S                                | Bloques     |                 |

PK modification_id, FK a operación y condiciones.

Tipos CHANGE_TERMS, EARLY_TERMINATION y CANCEL_UNEXECUTED.

Cancelar sin ejecución exige descartar pendientes incompatibles.

### **16.2. treasury_operation_valuation**

Columnas N: valuation_id uuid PK, cut_reference jsonb, policy_reference jsonb, source_references jsonb, completeness
varchar(20), missing_data_codes jsonb y bloques T y S.

### **16.3. treasury_operation_valuation_item**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| valuation_id, treasury_operation_id      | uuid          | N               |
| currency                                 | varchar(3)    | N               |
| cost_amount                              | numeric(24,2) | N               |
| valued_amount, accrued_return            | numeric(24,2) | S               |
| realized_result, valuation_variation     | numeric(24,2) | S               |
| price_reference, exchange_rate_reference | jsonb         | S               |
| missing_data_codes                       | jsonb         | N               |

PK sobre valuación y operación. FK a ambas.

Resultados y variación admiten signo.

La ausencia de precio permanece ausente. La valuación no crea recursos líquidos.

## **17. Cotizaciones**

### **17.1. exchange_rate_scope_control**

| **Columna**                   | **Tipo**       | **Nulabilidad** |
|-------------------------------|----------------|-----------------|
| scope_id                      | uuid           | N, PK           |
| rate_class                    | varchar(30)    | N               |
| base_currency, quote_currency | varchar(3)     | N               |
| quotation_unit                | numeric(30,12) | N               |
| scope_hash                    | char(64)       | N               |
| scope_definition              | jsonb          | N               |
| lock_version                  | bigint         | N               |
| updated_at                    | timestamptz    | N               |

UQ por clase, par, unidad y hash de alcance.

Monedas diferentes y unidad positiva.

### **17.2. exchange_rate_reference**

Columnas N: rate_reference_id uuid PK, scope_id uuid FK, side varchar(10), current_version bigint, bloque T y
lock_version bigint.

Sentidos BANK_BUY y BANK_SELL respecto de moneda base.

### **17.3. exchange_rate_reference_version**

| **Columna**                            | **Tipo**       | **Nulabilidad** |
|----------------------------------------|----------------|-----------------|
| rate_reference_id                      | uuid           | N               |
| rate_version                           | bigint         | N               |
| value                                  | numeric(30,12) | N               |
| source_reference                       | jsonb          | N               |
| applicable_at, valid_from, valid_until | timestamptz    | N               |
| verification_status                    | varchar(30)    | N               |
| superseded_reference                   | jsonb          | S               |
| correction_reason                      | varchar(4000)  | S               |
| manual                                 | boolean        | N               |
| preparer_person_id, approver_person_id | uuid           | S               |
| approval_reference                     | jsonb          | S               |
| T, S                                   | Bloques        |                 |

PK sobre referencia y versión.

FK a referencia.

Valor positivo y final posterior al inicio.

Clases REFERENCE, ACCOUNTING_CLOSE, EXECUTABLE_COMMERCIAL e INFORMATIVE.

Estados VALIDATED, PENDING_VERIFICATION y OBSERVED.

La carga manual exige aprobación y personas distintas.

Bajo control de alcance se comprueba compra no superior a venta para referencias comparables.

Las referencias contables e informativas no se ejecutan como ofertas comerciales.

## **18. Propuestas cambiarias**

### **18.1. foreign_exchange_proposal**

| **Columna**                                       | **Tipo**       | **Nulabilidad** |
|---------------------------------------------------|----------------|-----------------|
| proposal_id                                       | uuid           | N, PK           |
| proposal_version                                  | bigint         | N               |
| origin_operation_reference, participant_reference | jsonb          | N               |
| execution_mode                                    | varchar(30)    | N               |
| bank_side                                         | varchar(10)    | N               |
| base_currency, quote_currency                     | varchar(3)     | N               |
| fixed_amount_side                                 | varchar(10)    | N               |
| fixed_amount, counter_amount                      | numeric(24,2)  | N               |
| base_amount, quote_amount                         | numeric(24,2)  | N               |
| rate_reference_id                                 | uuid           | N               |
| rate_version                                      | bigint         | N               |
| quotation_unit, quoted_value                      | numeric(30,12) | N               |
| charges, taxes, final_monetary_legs               | jsonb          | N               |
| origins, destinations                             | jsonb          | N               |
| planned_settlement_date                           | date           | N               |
| issued_at, valid_until                            | timestamptz    | N               |
| status                                            | varchar(20)    | N               |
| T, S                                              | Bloques        |                 |
| lock_version                                      | bigint         | N               |

FK a cotización versionada.

Modos INTERNAL_CUSTOMER_ACCOUNTS y EXTERNAL_DELIVERY.

Lado fijo BASE o QUOTE.

La vigencia es exactamente sesenta segundos, con final excluido.

El cálculo conserva unidad y redondeo. Si el fijo es BASE, la contraprestación parte de base × valor / unidad; si es
QUOTE, parte de cotizada × unidad / valor.

Las piernas finales incluyen cargos y tributos por moneda.

El contenido económico es inmutable.

### **18.2. foreign_exchange_acceptance**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| acceptance_id, proposal_id                    | uuid        | N               |
| proposal_version                              | bigint      | N               |
| accepted_content_hash                         | char(64)    | N               |
| conformity_evidence, authorization_references | jsonb       | N               |
| received_at                                   | timestamptz | N               |
| status                                        | varchar(20) | N               |
| T                                             | Bloque      |                 |

PK acceptance_id, FK a propuesta.

UQ sobre propuesta.

Estados ACCEPTED, REJECTED y PENDING.

ACCEPTED exige recepción anterior a valid_until.

Aceptar fija condiciones, pero no demuestra ejecución.

## **19. Operación cambiaria**

### **19.1. foreign_exchange_operation**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| fx_operation_id, proposal_id, acceptance_id | uuid        | N               |
| execution_mode                              | varchar(30) | N               |
| accepted_content                            | jsonb       | N               |
| monetary_execution_status                   | varchar(20) | N               |
| risk_transformation_status                  | varchar(20) | N               |
| liquidity_commitment_status                 | varchar(20) | N               |
| external_delivery_status                    | varchar(20) | S               |
| pending_components                          | jsonb       | N               |
| T, C, S                                     | Bloques     |                 |

PK fx_operation_id.

FK a propuesta y aceptación propias.

UQ sobre propuesta y aceptación.

Estados de componentes NOT_STARTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

Entrega externa solo está presente para EXTERNAL_DELIVERY.

### **19.2. foreign_exchange_internal_execution**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| fx_operation_id                  | uuid        | N, PK           |
| customer_reservation_reference   | jsonb       | S               |
| internal_fx_commitment_reference | jsonb       | N               |
| risk_affectation_reference       | jsonb       | N               |
| authority_preparation_references | jsonb       | N               |
| funds_group_id, funds_action_id  | uuid        | N               |
| funds_result_reference           | jsonb       | S               |
| risk_transformation_reference    | jsonb       | S               |
| preparation_resolution_reference | jsonb       | S               |
| updated_at                       | timestamptz | N               |
| lock_version                     | bigint      | N               |

FK a operación.

UQ sobre conjunto de Fondos y actuación monetaria.

Fondos confirma ambas piernas de forma indivisible.

Si confirmó, el estado monetario permanece CONFIRMED aunque falte Riesgos u otra regularización.

## **20. Resolución del compromiso cambiario interno**

### **20.1. internal_fx_commitment_resolution**

En Posiciones y liquidez:

| **Columna**                   | **Tipo**      | **Nulabilidad** |
|-------------------------------|---------------|-----------------|
| resolution_id, commitment_id  | uuid          | N               |
| fx_service                    | varchar(80)   | N               |
| fx_operation_id, proposal_id  | uuid          | N               |
| proposal_version              | bigint        | N               |
| accepted_content_hash         | char(64)      | N               |
| funds_group_id                | uuid          | N               |
| both_legs_evidence            | jsonb         | N               |
| risk_transformation_required  | boolean       | N               |
| risk_transformation_reference | jsonb         | S               |
| resolved_amount               | numeric(24,2) | N               |
| T, S                          | Bloques       |                 |

PK resolution_id, FK a compromiso.

UQ (commitment_id, fx_service, fx_operation_id).

La clase debe ser INTERNAL_FX_PREPARATION.

Exige ambas piernas confirmadas y transformación de Riesgos cuando corresponda.

La transacción incrementa resuelto, reduce remanente y actualiza asignaciones, historia y outbox.

No genera movimiento monetario externo.

La exposición efectiva resultante permanece en Riesgos.

### **20.2. foreign_exchange_resolution**

En Cambio:

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| resolution_id, fx_operation_id          | uuid        | N               |
| resolution_type                         | varchar(40) | N               |
| original_references, verified_treatment | jsonb       | N               |
| evidence_references                     | jsonb       | N               |
| approval_reference                      | jsonb       | S               |
| result_references                       | jsonb       | N               |
| T, C, S                                 | Bloques     |                 |

PK resolution_id, FK a operación.

Tipos RECOVER_EXECUTION, COMPLETE_RISK_TRANSFORMATION, COMPLETE_PREPARATION_RESOLUTION, CORRECT_ECONOMIC_EFFECT y
RESTITUTE_CONFIRMED_AMOUNT.

Corregir un error no simula otra compra voluntaria a cotización actual.

## **21. Obligaciones de liquidación**

### **21.1. treasury_settlement_origin_control**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| owner_service       | varchar(80) | N               |
| origin_operation_id | uuid        | N               |
| composition_version | bigint      | N               |
| lock_version        | bigint      | N               |
| updated_at          | timestamptz | N               |

PK sobre propietario y operación.

Serializa admisión y cambios de composición para impedir duplicaciones entre agregados y componentes.

### **21.2. treasury_settlement_obligation**

| **Columna**                               | **Tipo**       | **Nulabilidad** |
|-------------------------------------------|----------------|-----------------|
| obligation_id                             | uuid           | N, PK           |
| owner_service                             | varchar(80)    | N               |
| source_obligation_id, origin_operation_id | uuid           | N               |
| source_version                            | bigint         | N               |
| counterparty_reference                    | jsonb          | N               |
| direction                                 | varchar(30)    | N               |
| amount                                    | numeric(24,2)  | S               |
| currency                                  | varchar(3)     | S               |
| quantity                                  | numeric(30,12) | S               |
| instrument_reference                      | jsonb          | S               |
| due_at                                    | timestamptz    | N               |
| mechanism_reference, verified_destination | jsonb          | N               |
| firmness                                  | varchar(20)    | N               |
| partial_settlement_allowed                | boolean        | N               |
| dvp_group_reference                       | jsonb          | S               |
| fulfilled_amount, remaining_amount        | numeric(24,2)  | S               |
| fulfilled_quantity, remaining_quantity    | numeric(30,12) | S               |
| status                                    | varchar(30)    | N               |
| T, C, S                                   | Bloques        |                 |

FK al control de origen.

UQ (owner_service, source_obligation_id).

PAY y RECEIVE exigen variante monetaria.

DELIVER_INSTRUMENTS y RECEIVE_INSTRUMENTS exigen variante de instrumentos.

Estados REGISTERED, RESOURCES_PENDING, INSTRUCTED, PARTIALLY_FULFILLED, FULFILLED y REQUIRES_RESOLUTION.

Total = cumplido + remanente en la magnitud correspondiente.

Una obligación válida sin cobertura permanece pendiente.

### **21.3. treasury_settlement_obligation_component**

| **Columna**                            | **Tipo**       | **Nulabilidad** |
|----------------------------------------|----------------|-----------------|
| obligation_id                          | uuid           | N               |
| source_owner_service, source_fact_type | varchar(80)    | N               |
| source_fact_id                         | uuid           | N               |
| source_component_code                  | varchar(80)    | N               |
| amount                                 | numeric(24,2)  | S               |
| quantity                               | numeric(30,12) | S               |

PK sobre obligación y referencia económica completa.

FK a obligación.

UQ (source_owner_service, source_fact_type, source_fact_id, source_component_code).

El agregado declara sus componentes económicos. Los mismos componentes no pueden incorporarse como otra obligación
independiente.

## **22. Remanentes e instrucciones**

### **22.1. treasury_settlement_remainder_control**

| **Columna**                       | **Tipo**       | **Nulabilidad** |
|-----------------------------------|----------------|-----------------|
| obligation_id                     | uuid           | N, PK           |
| committed_amount, free_amount     | numeric(24,2)  | S               |
| committed_quantity, free_quantity | numeric(30,12) | S               |
| lock_version                      | bigint         | N               |
| updated_at                        | timestamptz    | N               |

FK a obligación.

En la variante aplicable:

Remanente de obligación = comprometido por instrucciones + libre para instruir.

Las instrucciones inciertas conservan compromiso.

### **22.2. treasury_settlement_instruction**

| **Columna**                                             | **Tipo**       | **Nulabilidad** |
|---------------------------------------------------------|----------------|-----------------|
| instruction_id, obligation_id                           | uuid           | N               |
| previous_instruction_id                                 | uuid           | S               |
| amount                                                  | numeric(24,2)  | S               |
| quantity                                                | numeric(30,12) | S               |
| resource_references, mechanism_reference                | jsonb          | N               |
| fixed_verified_destination                              | jsonb          | N               |
| authorization_type                                      | varchar(30)    | N               |
| individual_approval_reference, automatic_rule_reference | jsonb          | S               |
| participant_id                                          | uuid           | N               |
| mechanism_code                                          | varchar(80)    | N               |
| mechanism_version                                       | bigint         | N               |
| external_instruction_id                                 | varchar(120)   | N               |
| status                                                  | varchar(30)    | N               |
| T, C, S                                                 | Bloques        |                 |

PK instruction_id.

FK a obligación e instrucción anterior.

UQ (participant_id, mechanism_code, external_instruction_id).

Autorizaciones INDIVIDUAL_APPROVAL y AUTOMATIC_RULE, con referencias excluyentes.

Estados PREPARED, DISPATCH_PENDING, DISPATCHED, ACKNOWLEDGED, PARTIALLY_CONFIRMED, CONFIRMED, CANCELLED y UNCERTAIN.

La liberación respeta separación de funciones respecto de la concertación.

Completar remanente crea instrucción vinculada y solo compromete lo libre.

## **23. Despacho y cancelación**

### **23.1. treasury_settlement_dispatch**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| dispatch_id, instruction_id | uuid        | N               |
| generation                  | bigint      | N               |
| claim_owner                 | uuid        | N               |
| claim_until, claimed_at     | timestamptz | N               |
| sent_at, concluded_at       | timestamptz | S               |
| cancellation_barrier        | boolean     | N               |
| state                       | varchar(20) | N               |
| adapter_result_reference    | jsonb       | S               |

PK dispatch_id, FK a instrucción.

UQ (instruction_id, generation).

Estados CLAIMED, SENT, CONFIRMED, CANCELLED y UNCERTAIN.

UQ parcial sobre instrucción para estados CLAIMED, SENT y UNCERTAIN.

### **23.2. treasury_settlement_resolution**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| resolution_id, instruction_id       | uuid        | N               |
| action                              | varchar(30) | N               |
| verified_scope, evidence_references | jsonb       | N               |
| final_cancellation_evidence         | jsonb       | S               |
| successor_instruction_id            | uuid        | S               |
| status                              | varchar(30) | N               |
| T, C, S                             | Bloques     |                 |

PK resolution_id.

FK a instrucción y sucesora.

Acciones QUERY_RESULT, CANCEL_UNSENT, COMPLETE_REMAINDER e INVESTIGATE_FAILURE.

Despacho y cancelación bloquean obligación, control de remanente, instrucción y despacho.

La llamada al adaptador ocurre fuera de la transacción.

Si el trabajador ya reclamó el envío, cancelar permanece pendiente hasta resolverlo.

La cancelación definitiva impide ejecutar posteriormente la misma referencia. La expiración del trabajador no demuestra
ausencia de ejecución.

## **24. Resultados y cumplimiento externo**

### **24.1. treasury_settlement_external_result**

| **Columna**                               | **Tipo**       | **Nulabilidad** |
|-------------------------------------------|----------------|-----------------|
| result_id, instruction_id, participant_id | uuid           | N               |
| mechanism_code                            | varchar(80)    | N               |
| source_result_id                          | varchar(120)   | N               |
| source_result_version                     | bigint         | N               |
| external_execution_id                     | varchar(120)   | S               |
| result                                    | varchar(20)    | N               |
| confirmed_amount_increment                | numeric(24,2)  | S               |
| confirmed_quantity_increment              | numeric(30,12) | S               |
| effective_at                              | timestamptz    | N               |
| evidence_references                       | jsonb          | N               |
| T, S                                      | Bloques        |                 |

PK result_id, FK a instrucción.

UQ (participant_id, mechanism_code, source_result_id, source_result_version).

Resultados ACKNOWLEDGED, CONFIRMED, PARTIAL, REJECTED y UNCERTAIN.

ACKNOWLEDGED, REJECTED y UNCERTAIN no aportan cumplimiento confirmado.

Las revisiones se contrastan con la ejecución original y no suman nuevamente importes ya reconocidos.

### **24.2. treasury_settlement_result_conflict**

Columnas N: conflict_id uuid PK, result_id uuid FK, conflicting_hash char(64), conflicting_payload jsonb,
evidence_references jsonb y bloque T.

UQ (result_id, conflicting_hash).

### **24.3. treasury_settlement_fulfillment**

| **Columna**                                              | **Tipo**       | **Nulabilidad** |
|----------------------------------------------------------|----------------|-----------------|
| fulfillment_id, obligation_id, instruction_id, result_id | uuid           | N               |
| external_execution_id                                    | varchar(120)   | N               |
| fulfillment_component_code                               | varchar(80)    | N               |
| amount                                                   | numeric(24,2)  | S               |
| quantity                                                 | numeric(30,12) | S               |
| effective_at                                             | timestamptz    | N               |
| evidence_references                                      | jsonb          | N               |
| T                                                        | Bloque         |                 |

PK fulfillment_id.

FK a obligación, instrucción y resultado propios.

UQ (instruction_id, external_execution_id, fulfillment_component_code).

La suma confirmada no supera instrucción ni obligación. Las parcialidades requieren habilitación.

### **24.4. Aplicaciones locales**

Cada cumplimiento conserva componentes separados para:

- Actualización de posición propia.

- Consumo del compromiso.

- Actualización de instrumentos.

- Aplicación o comunicación al originante.

- Transformación de exposición.

Si el externo confirmó y Posiciones sigue pendiente, se recupera la actualización local sin volver a pagar.

El cumplimiento externo no se borra por una regularización pendiente.

## **25. Conciliación de liquidaciones**

### **25.1. treasury_settlement_reconciliation**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| reconciliation_id                           | uuid        | N, PK           |
| account_references, obligation_references   | jsonb       | N               |
| instruction_references, position_references | jsonb       | N               |
| comparable_cuts, external_statements        | jsonb       | N               |
| evidence_references                         | jsonb       | N               |
| status                                      | varchar(20) | N               |
| T, C, S                                     | Bloques     |                 |

Estados RUNNING, MATCHED, DIFFERENCES e INCOMPLETE.

### **25.2. treasury_settlement_reconciliation_item**

| **Columna**                | **Tipo**       | **Nulabilidad** |
|----------------------------|----------------|-----------------|
| item_id, reconciliation_id | uuid           | N               |
| position                   | integer        | N               |
| compared_references        | jsonb          | N               |
| classification             | varchar(30)    | S               |
| amount_difference          | numeric(24,2)  | S               |
| quantity_difference        | numeric(30,12) | S               |
| responsible_service        | varchar(80)    | S               |
| status                     | varchar(20)    | N               |

PK item_id, FK a conciliación.

UQ (reconciliation_id, position).

Diferencias con signo.

Clasificaciones TIMING, MISSING_RECORD, DUPLICATE, AMOUNT_MISMATCH, QUANTITY_MISMATCH y STATE_CONTRADICTION.

### **25.3. treasury_settlement_difference_resolution**

| **Columna**                                | **Tipo**    | **Nulabilidad** |
|--------------------------------------------|-------------|-----------------|
| resolution_id, reconciliation_item_id      | uuid        | N               |
| diagnosis, treatment                       | jsonb       | N               |
| responsible_service                        | varchar(80) | N               |
| manual_adjustment                          | boolean     | N               |
| preparer_person_id                         | uuid        | N               |
| approver_person_id                         | uuid        | S               |
| approval_reference                         | jsonb       | S               |
| execution_reference, verification_evidence | jsonb       | S               |
| status                                     | varchar(30) | N               |
| T, C, S                                    | Bloques     |                 |

PK resolution_id, FK a elemento.

Ajuste manual exige aprobación y personas distintas.

Resolver requiere ejecución por el propietario y comprobación posterior.

Un débito externo desconocido no se carga automáticamente a un cliente.

## **26. Cortes y cierre operativo**

### **26.1. treasury_position_cut**

| **Columna**                         | **Tipo**    | **Nulabilidad** |
|-------------------------------------|-------------|-----------------|
| cut_id                              | uuid        | N, PK           |
| cut_at                              | timestamptz | N               |
| scope_definition, source_references | jsonb       | N               |
| completeness                        | varchar(20) | N               |
| missing_source_codes                | jsonb       | N               |
| T, S                                | Bloques     |                 |

### **26.2. treasury_position_cut_item**

Columnas N:

- cut_id uuid.

- position_id uuid.

- position_version bigint.

- maximum_movement_sequence bigint.

- position_snapshot jsonb.

PK sobre corte y posición.

FK a corte e historia de posición.

La lectura es consistente. Las páginas mantienen secuencia máxima y filtros del corte.

### **26.3. treasury_operating_close**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| close_id                                         | uuid        | N, PK           |
| economic_date                                    | date        | N               |
| close_version                                    | bigint      | N               |
| position_cut_id                                  | uuid        | N               |
| commitments_by_class, transit_references         | jsonb       | N               |
| uncertain_settlements, reconciliation_references | jsonb       | N               |
| valuation_references, exposure_references        | jsonb       | N               |
| source_references, limitations                   | jsonb       | N               |
| completeness                                     | varchar(20) | N               |
| T, S                                             | Bloques     |                 |

FK a corte.

UQ (economic_date, close_version).

Condición COMPLETE o INCOMPLETE.

El cierre comprende el día económico completo y no cierra períodos contables.

La gestión ordinaria es hábil desde las 09:00 hasta antes de las 18:00. Las obligaciones y confirmaciones se reciben
todo el día.

Una fuente ausente no se convierte en saldo cero.

## **27. Índices complementarios**

| **Tabla**                               | **Índice**                                                        |
|-----------------------------------------|-------------------------------------------------------------------|
| treasury_resource_location              | (status, location_type, location_id)                              |
| treasury_location_destination_version   | (location_id, valid_from, destination_version)                    |
| treasury_position                       | (location_id, currency, position_id)                              |
| treasury_resource_movement              | (position_id, effective_date, movement_sequence)                  |
| treasury_resource_commitment            | (commitment_class, status, required_date, commitment_id)          |
| treasury_resource_commitment            | (owner_service, origin_operation_id, commitment_id)               |
| treasury_resource_allocation            | (position_id, status, allocation_id)                              |
| cash_resource_transfer                  | (status, planned_date, transfer_id)                               |
| cash_resource_transit                   | (component_id, transit_id)                                        |
| liquidity_flow_version                  | (expected_date, direction, certainty, flow_id, flow_version)      |
| treasury_operation                      | (operation_type, status, treasury_operation_id)                   |
| treasury_operation_execution            | (treasury_operation_id, effective_date, execution_id)             |
| treasury_operation_obligation           | (status, due_date, obligation_id)                                 |
| exchange_rate_reference                 | (scope_id, side, rate_reference_id)                               |
| exchange_rate_reference_version         | (valid_from, valid_until, verification_status, rate_reference_id) |
| foreign_exchange_proposal               | (status, valid_until, proposal_id)                                |
| foreign_exchange_coordination_component | (fx_operation_id, status, step_id)                                |
| treasury_settlement_obligation          | (status, due_at, obligation_id)                                   |
| treasury_settlement_instruction         | (obligation_id, status, instruction_id)                           |
| treasury_settlement_dispatch            | (claim_until, dispatch_id), donde state = 'CLAIMED'               |
| treasury_settlement_local_application   | (status, next_check_at, step_id)                                  |

Las FK compuestas incorporan las UQ necesarias y comprueban pertenencia al mismo agregado.

## **28. Orden de bloqueo y atomicidad**

### **28.1. Posiciones y liquidez**

1.  Ubicaciones, cuando deban comprobarse o modificarse.

2.  Traslado, cuando corresponda.

3.  Control de catálogo de recursos, si cambia la composición de asignaciones.

4.  Posiciones ordenadas.

5.  Compromisos ordenados.

6.  Asignaciones, causas y aplicaciones.

Los procesos que requieren ubicación y posición no adquieren ubicación después de posición.

La reasignación determina y bloquea todas las posiciones anteriores y nuevas.

### **28.2. Operaciones de tesorería**

Operación → capacidad de línea o posiciones propias ordenadas → compromisos → ejecuciones → obligaciones.

### **28.3. Cambio de moneda**

Propuesta → operación cambiaria → aceptación y componentes.

La coherencia de cotizaciones utiliza primero el control de alcance.

### **28.4. Liquidaciones**

Control de origen, cuando cambia composición → obligación → control de remanente → instrucciones ordenadas → despacho y
resultados.

Ningún proceso adquiere un control superior después de su detalle.

### **28.5. Confirmación**

Los movimientos y consumos, posiciones e historia, cumplimiento y remanente, resolución interna y liberación de
asignaciones se confirman conjuntamente con actuación y outbox.

No se mantienen transacciones PostgreSQL durante llamadas externas.

## **29. Eventos y recuperación**

| **Contrato** | **Tipo**                                  | **Tema**                           |
|--------------|-------------------------------------------|------------------------------------|
| CI-10-033    | TreasuryResourceOrCommitmentChanged       | banking.liquidity.events           |
| CI-10-034    | LiquidityProjectionOrNeedChanged          | banking.liquidity.events           |
| CI-10-035    | TreasuryOperationChanged                  | banking.treasury-operation.events  |
| CI-10-036    | ExchangeRateReferenceChanged              | banking.foreign-exchange.events    |
| CI-10-037    | ForeignExchangeOperationChanged           | banking.foreign-exchange.events    |
| CI-10-038    | TreasurySettlementOrReconciliationChanged | banking.treasury-settlement.events |
| CI-10-039    | TreasuryCloseOrValuationChanged           | banking.liquidity.events           |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado definido por cada variante.

RESOLVED_INTERNAL_FX no comunica entrada ni salida monetaria.

Un cierre operativo no comunica cierre contable.

Los trabajos conservan ubicación, compromiso, operación, propuesta, obligación, instrucción y efecto originales.

Ante incertidumbre se consulta al detectarla, a los cinco minutos, a los treinta minutos y por jornada operativa
mientras continúe la investigación.

Estos tiempos no autorizan liberar recursos ni repetir ejecuciones.

## **30. Seguridad, migraciones y conservación**

Las migraciones utilizan Flyway por servicio.

Las referencias circulares son diferibles. Las exclusiones temporales utilizan el soporte PostgreSQL correspondiente.

Se preservan movimientos, resultados externos, cumplimientos, propuestas aceptadas, aprobaciones y referencias
históricas de destinos.

No se eliminan mediante cascadas registros utilizados.

Redis no conserva la única copia de recursos, compromisos, propuestas aceptadas o decisiones de despacho.

Se separan permisos para concertar, aprobar, mover recursos, arquear, cotizar, aceptar, ejecutar, liberar liquidaciones,
incorporar resultados y resolver diferencias.

Los activos de clientes no se utilizan como recursos propios.

Después de restaurar un respaldo se pausan pagos, entregas y liberaciones, y se reconcilian resultados externos antes de
reanudar.

## **31. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                            |
|-----------------------|-------------------------------------------------------------|
| CI-10-001 y CI-10-002 | Ubicaciones, destinos, posiciones y cortes.                 |
| CI-10-003             | Movimientos propios y consumo de compromisos.               |
| CI-10-004 y CI-10-005 | Traslados, hechos físicos, tránsito y arqueos.              |
| CI-10-006 y CI-10-007 | Compromisos, asignaciones y resolución interna.             |
| CI-10-008 a CI-10-010 | Flujos, proyecciones, necesidades y cobertura.              |
| CI-10-011 y CI-10-012 | Financiación e inversiones propias.                         |
| CI-10-013 a CI-10-017 | Ejecuciones, devengamientos, cumplimiento y modificaciones. |
| CI-10-018 y CI-10-019 | Cotizaciones y versiones.                                   |
| CI-10-020 y CI-10-021 | Propuestas, aceptación y ejecución cambiaria.               |
| CI-10-022 y CI-10-023 | Resolución y recuperación de cambio.                        |
| CI-10-024 y CI-10-025 | Obligaciones e instrucciones.                               |
| CI-10-026 a CI-10-029 | Resultados, cumplimiento, remanentes y conciliación.        |
| CI-10-030             | Valuación de operaciones propias.                           |
| CI-10-031 y CI-10-032 | Cierre operativo y actuaciones.                             |
| CI-10-033 a CI-10-039 | Outbox e historia de agregados.                             |

## **32. Escenarios de verificación de implementación**

| **Escenario**                                | **Resultado exigido**                                |
|----------------------------------------------|------------------------------------------------------|
| Alta de ubicación                            | No crea dinero.                                      |
| Saldo 100 y compromiso 40                    | Mantiene saldo 100 y capacidad 60, sin otras causas. |
| Dos compromisos de 80 sobre 100              | No confirma ambos íntegramente.                      |
| Reasignación                                 | No duplica cobertura ni libera anticipadamente.      |
| Compromiso incierto                          | No se libera por temporizador.                       |
| Salida física 100 y recepción 90             | Conserva tránsito o diferencia 10.                   |
| Arqueo con faltante                          | No ajusta saldo automáticamente.                     |
| Financiación concertada no recibida          | No integra saldo confirmado.                         |
| Flujo y compromiso de la misma obligación    | No se computan dos veces.                            |
| Venta sin unidades propias                   | No ejecuta en corto.                                 |
| Cobro de interés devengado                   | No reconoce otro ingreso.                            |
| Cotización contable                          | No se ejecuta como oferta comercial.                 |
| Aceptación exactamente al vencer             | Queda fuera de vigencia.                             |
| Respuesta de aceptación perdida              | Recupera la misma operación cambiaria.               |
| Cambio interno USD 100 por ARS 102.000       | Conserva ambas piernas aceptadas de Fondos.          |
| Fondos confirmó y falta Riesgos              | Mantiene ejecución monetaria confirmada.             |
| Resolución de preparación interna            | No crea salida externa ficticia.                     |
| Agregado y componentes                       | No genera obligaciones duplicadas.                   |
| Instrucciones concurrentes                   | No comprometen el mismo remanente.                   |
| Destino modificado                           | No redirige instrucción autorizada.                  |
| Cancelación y despacho concurrentes          | Se ordenan sobre la misma instrucción.               |
| Acuse técnico                                | No se presenta como pago.                            |
| Pago externo confirmado y posición pendiente | Recupera actualización sin otro pago.                |
| Parcial 60 de 100                            | Conserva 40 pendientes según mecanismo.              |
| Débito externo desconocido                   | No lo carga automáticamente a un cliente.            |
| Cierre con fuente ausente                    | Se presenta como incompleto.                         |
| Evento duplicado o restauración              | Recupera efectos originales sin repetir recursos.    |

Las restricciones, exclusiones, conservación de recursos, bloqueos y transacciones se verifican durante la
implementación con PostgreSQL real y escenarios concurrentes.
