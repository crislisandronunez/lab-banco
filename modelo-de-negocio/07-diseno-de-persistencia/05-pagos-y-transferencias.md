# **Diseño de persistencia — Módulo 5. Pagos y transferencias**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Pagos y transferencias.

| **Servicio**                         | **Responsabilidad**                                                                                |
|--------------------------------------|----------------------------------------------------------------------------------------------------|
| Transferencias                       | Instrucciones internas, salientes y entrantes, cancelaciones y devoluciones.                       |
| Pagos                                | Convenios, referencias de obligaciones, ejecución, aplicación y restituciones.                     |
| Mandatos y recurrencias              | Autorizaciones, programaciones, ocurrencias y solicitudes de reversión.                            |
| Cheques y ECHEQ                      | Instrumentos, circulación, descuento, presentaciones y resultados de cobro.                        |
| Compensación y conciliación de pagos | Participantes, mecanismos, intercambios, mensajes, diferencias y obligaciones entre participantes. |

Comprende los contratos CI-05-001 a CI-05-044.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases de servicios.

Fondos conserva la autoridad monetaria. Los acreedores mantienen sus obligaciones e imputaciones. Liquidaciones de
tesorería ejecuta la liquidación entre participantes.

La ejecución del instrumento, la aplicación a una obligación y la liquidación entre participantes conservan identidades
y resultados diferentes.

## **2. Convenciones físicas**

### **2.1. Notación**

- **N:** columna obligatoria.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en orden. Las PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                | **PostgreSQL** | **Restricción**                    |
|-------------------------|----------------|------------------------------------|
| Identificador           | uuid           | Estable y no reutilizable.         |
| Versión                 | bigint         | Positiva.                          |
| Control de concurrencia | bigint         | No negativo.                       |
| Índice de ocurrencia    | bigint         | Positivo.                          |
| Código                  | varchar(80)    | No vacío y registrado.             |
| Referencia externa      | varchar(120)   | No vacía.                          |
| Fundamento              | varchar(4000)  | No vacío cuando está presente.     |
| Moneda                  | varchar(3)     | ARS o USD.                         |
| Importe                 | numeric(24,2)  | No negativo.                       |
| Valor de cálculo        | numeric(30,12) | Conforme al concepto.              |
| Tasa                    | numeric(24,12) | Conforme a su definición.          |
| Hash                    | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Instante                | timestamptz    | Intercambio en UTC.                |
| Fecha                   | date           | Fecha bancaria.                    |
| Hora local              | time(0)        | Precisión de segundos.             |
| Zona                    | varchar(80)    | Identificador IANA válido.         |
| Contenido estructurado  | jsonb          | Esquema registrado y versionado.   |

Los importes ejecutables son positivos. Acumulados, remanentes y resultados de neteo admiten cero.

La precisión y escala se validan antes de persistir. No se aceptan valores inválidos mediante redondeo implícito.

La zona institucional es America/Argentina/Buenos_Aires.

### **2.3. Bloques de columnas**

Los bloques representan columnas físicas incorporadas a cada tabla que los menciona.

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

**Bloque O — Identidad de origen**

| **Columna**      | **Tipo**     | **Nulabilidad** |
|------------------|--------------|-----------------|
| origin_service   | varchar(80)  | N               |
| origin_type      | varchar(80)  | N               |
| origin_reference | varchar(120) | N               |

La combinación del bloque es única dentro de cada tabla receptora que lo incorpora.

### **2.4. Tablas comunes**

Los cinco servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message.

- persistent_job.

Las preparaciones de autoridades y los intentos monetarios se referencian desde sus propietarios. Este módulo no duplica
la decisión de Fondos.

## **3. Esquemas estructurados**

Cada JSONB utiliza una columna de versión explícita o un envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                   | **Contenido**                                                 |
|-------------------------------|---------------------------------------------------------------|
| RemoteReference               | Propietario, tipo, identidad y versión cuando corresponde.    |
| AuthorizationBasis            | Modalidad, antecedente, actores, huella y evidencias.         |
| EvidenceReferences            | Hechos y evidencias identificados.                            |
| PendingComponents             | Componente, responsable, motivo y seguimiento.                |
| AcceptedDestination           | Destino aceptado y datos presentados.                         |
| MonetaryConcepts              | Concepto, categoría, importe, moneda y cálculo de origen.     |
| TransferCommand               | Instrucción, principal, destino, condiciones y cargos.        |
| CollectionAgreementDefinition | Modalidades, instrumentos, monedas, cargos y capacidades.     |
| ReferenceFieldDefinitions     | Campos tipados, obligatoriedad y restricciones.               |
| PaymentInstruction            | Obligación, importe, instrumento y autorización.              |
| CreditorPreparation           | Alcance, obligación, importe y referencias de preparación.    |
| MandateDefinition             | Alcance aceptado, frecuencia e importe.                       |
| ScheduledInstruction          | Variante completa de transferencia o pago.                    |
| CalendarCalculation           | Calendario, entradas, regla y resultado.                      |
| ChequeContent                 | Contenido del instrumento y antecedentes de integridad.       |
| ChequeCirculation             | Transmisión, cesión o aval con sus evidencias.                |
| MechanismDefinition           | Operaciones, esquemas, adaptadores y semántica de resultados. |
| NetworkMessage                | Mensaje tipado por mecanismo, versión y tipo.                 |
| ReconciliationResult          | Fuentes, cortes, comparación y diferencias.                   |
| ChargeCalculation             | Condiciones, bases, bonificación y determinación tributaria.  |

No se admiten scripts, SQL, URL arbitrarias ni campos desconocidos.

Los atributos utilizados en identidad, unicidad, bloqueo o recuperación se conservan también en columnas explícitas.

## **4. Pasos remotos recuperables**

### **4.1. Estructura física**

Las tablas de pasos remotos utilizan las siguientes columnas:

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| step_id             | uuid        | N, PK           |
| component_code      | varchar(80) | N               |
| responsible_service | varchar(80) | N               |
| remote_action_id    | uuid        | N               |
| effect_id           | uuid        | S               |
| requested_content   | jsonb       | N               |
| status              | varchar(30) | N               |
| result_reference    | jsonb       | S               |
| result_payload      | jsonb       | S               |
| evidence_references | jsonb       | N               |
| last_checked_at     | timestamptz | S               |
| next_check_at       | timestamptz | S               |
| T                   | Bloque      |                 |
| C                   | Bloque      |                 |
| S                   | Bloque      |                 |

Cada tabla agrega la columna de propietario indicada:

| **Tabla**                 | **Columna N**              | **FK local**                  |
|---------------------------|----------------------------|-------------------------------|
| transfer_remote_step      | transfer_id uuid           | transfer_order                |
| transfer_return_step      | return_id uuid             | transfer_return               |
| payment_remote_step       | payment_id uuid            | payment_order                 |
| payment_adjustment_step   | adjustment_id uuid         | payment_adjustment            |
| restitution_remote_step   | restitution_action_id uuid | payment_restitution_action    |
| mandate_prior_instruction | mandate_action_id uuid     | mandate_status_action         |
| cheque_presentation_step  | presentation_id uuid       | cheque_presentation           |
| payment_difference_step   | resolution_id uuid         | payment_difference_resolution |

Cada tabla declara:

- UQ (propietario, component_code).

- UQ (responsible_service, remote_action_id).

- Índice (status, next_check_at, step_id).

- Índice (effect_id), donde exista efecto.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de la llamada. El resultado, historia, actuación, trabajo siguiente y outbox se
incorporan conjuntamente.

La actuación remota y el efecto no cambian durante la recuperación.

## **5. Hechos de negocio**

Las tablas de hechos utilizan:

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| fact_id             | uuid        | N, PK           |
| fact_type           | varchar(40) | N               |
| source_service      | varchar(80) | N               |
| source_fact_id      | uuid        | N               |
| source_fact_version | bigint      | N               |
| occurred_at         | timestamptz | N               |
| source_reference    | jsonb       | N               |
| evidence_references | jsonb       | N               |
| T                   | Bloque      |                 |
| S                   | Bloque      |                 |

Se crean:

| **Tabla**     | **Propietario N** | **FK**         |
|---------------|-------------------|----------------|
| transfer_fact | transfer_id uuid  | transfer_order |
| payment_fact  | payment_id uuid   | payment_order  |

UQ por (propietario, source_service, source_fact_id, source_fact_version).

Índice (propietario, occurred_at, fact_id).

Tipos:

RECEIVED, ACCEPTED_FOR_EXECUTION, DISPATCHED, IRREVOCABLY_ACCEPTED, INSTRUMENT_EXECUTED, BENEFICIARY_CREDITED,
CREDITOR_APPLIED, RETURNED y EXTERNALLY_SETTLED.

Los hechos son inmutables. Una recepción técnica no acredita ejecución, crédito al beneficiario ni aplicación del
acreedor.

## **6. Transferencias: instrucciones y destinos**

### **6.1. transfer_order**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| transfer_id              | uuid          | N, PK           |
| transfer_type            | varchar(20)   | N               |
| O                        | Bloque        |                 |
| ordering_party_reference | jsonb         | N               |
| source_account_id        | uuid          | S               |
| destination_account_id   | uuid          | S               |
| principal_amount         | numeric(24,2) | N               |
| currency                 | varchar(3)    | N               |
| purpose_code             | varchar(80)   | N               |
| authorization_basis      | jsonb         | S               |
| accepted_content_hash    | char(64)      | N               |
| processing_state         | varchar(30)   | N               |
| dispatch_barrier         | boolean       | N               |
| pending_components       | jsonb         | N               |
| received_at              | timestamptz   | N               |
| T                        | Bloque        |                 |
| C                        | Bloque        |                 |

Tipos INTERNAL, OUTGOING e INCOMING.

Estados: RECEIVED, VALIDATING, PREPARING, EXECUTING, COMPLETED, CANCELLED, REJECTED y REQUIRES_RESOLUTION.

Restricciones:

- Principal positivo.

- INTERNAL exige origen y destino distintos.

- OUTGOING exige origen.

- INCOMING exige destino y evidencia de recepción.

- Las instrucciones internas y salientes requieren autorización comprobada.

- La moneda coincide con el principal autorizado.

Índices:

- (processing_state, updated_at, transfer_id).

- (source_account_id, received_at, transfer_id), donde exista origen.

- (destination_account_id, received_at, transfer_id), donde exista destino.

### **6.2. transfer_accepted_destination**

| **Columna**              | **Tipo**     | **Nulabilidad** |
|--------------------------|--------------|-----------------|
| transfer_id              | uuid         | N, PK           |
| destination_type         | varchar(10)  | N               |
| account_reference        | jsonb        | S               |
| participant_reference    | jsonb        | S               |
| bank_identifier_type     | varchar(80)  | S               |
| bank_identifier_value    | varchar(120) | S               |
| resolution_reference     | jsonb        | S               |
| beneficiary_reference    | jsonb        | S               |
| beneficiary_display_name | varchar(250) | S               |
| currency                 | varchar(3)   | N               |
| accepted_at              | timestamptz  | N               |
| accepted_content_hash    | char(64)     | N               |
| schema_version           | bigint       | N               |

FK a transferencia.

INTERNAL exige cuenta y no participante externo. EXTERNAL exige participante y no cuenta interna.

Tipo y valor del identificador aparecen conjuntamente.

El contenido aceptado es inmutable. Un cambio posterior de alias no modifica el destino.

### **6.3. transfer_external_operation**

| **Columna**           | **Tipo**     | **Nulabilidad** |
|-----------------------|--------------|-----------------|
| transfer_id           | uuid         | N, PK           |
| participant_id        | uuid         | N               |
| mechanism_code        | varchar(80)  | N               |
| mechanism_version     | bigint       | N               |
| external_operation_id | varchar(120) | N               |
| external_content_hash | char(64)     | N               |
| exchange_reference    | jsonb        | S               |
| schema_version        | bigint       | N               |

FK a transferencia.

UQ (participant_id, mechanism_code, external_operation_id).

La versión del mecanismo no forma parte de la identidad.

## **7. Ejecución de transferencias**

### **7.1. transfer_monetary_execution**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| transfer_id                 | uuid        | N, PK           |
| funds_service               | varchar(80) | N               |
| remote_action_id            | uuid        | N               |
| movement_group_id           | uuid        | S               |
| debit_effect_id             | uuid        | S               |
| credit_effect_id            | uuid        | S               |
| reservation_reference       | jsonb       | S               |
| fx_reference                | jsonb       | S               |
| requested_content           | jsonb       | N               |
| status                      | varchar(30) | N               |
| movement_references         | jsonb       | N               |
| execution_attempt_reference | jsonb       | S               |
| T                           | Bloque      |                 |
| C                           | Bloque      |                 |
| S                           | Bloque      |                 |

FK a transferencia.

UQ (funds_service, remote_action_id).

Estados: NOT_STARTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

La transferencia interna exige conjunto de Fondos y efectos diferenciados de débito y crédito.

La conversión conserva intercambio, monedas e importes aceptados. No existe conversión implícita.

### **7.2. transfer_outgoing_execution**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| transfer_id               | uuid        | N, PK           |
| mechanism_reference       | jsonb       | N               |
| reservation_reference     | jsonb       | N               |
| coverage_status           | varchar(30) | N               |
| exchange_reference        | jsonb       | S               |
| dispatch_authorized_at    | timestamptz | S               |
| irrevocably_accepted_at   | timestamptz | S               |
| beneficiary_credited_at   | timestamptz | S               |
| credit_evidence_reference | jsonb       | S               |
| consumption_step_id       | uuid        | S               |
| pending_components        | jsonb       | N               |
| updated_at                | timestamptz | N               |
| lock_version              | bigint      | N               |

FK a transferencia y paso remoto de consumo.

Cobertura: PREPARING, PROTECTED, RESOLUTION_PENDING y RESOLVED.

Despachar exige cobertura protegida frente a incertidumbre.

La acreditación y su evidencia aparecen conjuntamente. Solo esa comprobación habilita el consumo correspondiente.

Un acuse técnico no demuestra acreditación.

### **7.3. transfer_incoming_acceptance**

| **Columna**             | **Tipo**    | **Nulabilidad** |
|-------------------------|-------------|-----------------|
| transfer_id             | uuid        | N, PK           |
| destination_check       | jsonb       | N               |
| currency_check          | jsonb       | N               |
| control_evidence        | jsonb       | N               |
| message_reference       | jsonb       | N               |
| acceptance_status       | varchar(20) | N               |
| irrevocably_accepted_at | timestamptz | S               |
| credit_effect_id        | uuid        | N               |
| T                       | Bloque      |                 |
| S                       | Bloque      |                 |

FK a transferencia. UQ sobre credit_effect_id.

Estados: PENDING, ACCEPTED y REJECTED.

ACCEPTED exige fecha de aceptación irrevocable.

Después de aceptar, se conserva la obligación de acreditar o tramitar el retorno. Una respuesta perdida no habilita otro
crédito.

## **8. Cancelaciones y devoluciones**

### **8.1. transfer_cancellation**

| **Columna**          | **Tipo**      | **Nulabilidad** |
|----------------------|---------------|-----------------|
| cancellation_id      | uuid          | N, PK           |
| transfer_id          | uuid          | N               |
| received_at          | timestamptz   | N               |
| reason               | varchar(4000) | N               |
| authorization_basis  | jsonb         | N               |
| exchange_reference   | jsonb         | S               |
| cancellation_step_id | uuid          | S               |
| outcome              | varchar(40)   | N               |
| final_evidence       | jsonb         | S               |
| T                    | Bloque        |                 |
| C                    | Bloque        |                 |

FK a transferencia y paso remoto.

Resultados: CANCELLED, PENDING_EXTERNAL_CONFIRMATION, ALREADY_CREDITED y REQUIRES_RESOLUTION.

Índice (transfer_id, received_at, cancellation_id).

La cancelación y la creación del intercambio bloquean la transferencia.

Si aún no existe intercambio, confirmar cancelación activa dispatch_barrier. Una creación tardía comprueba esa barrera.

Si existe intercambio, la cobertura no se libera hasta resolver su capacidad de despacho.

### **8.2. transfer_return_control**

| **Columna**        | **Tipo**      | **Nulabilidad** |
|--------------------|---------------|-----------------|
| transfer_id        | uuid          | N, PK           |
| original_principal | numeric(24,2) | N               |
| returned_amount    | numeric(24,2) | N               |
| pending_amount     | numeric(24,2) | N               |
| remaining_amount   | numeric(24,2) | N               |
| lock_version       | bigint        | N               |
| updated_at         | timestamptz   | N               |

FK a transferencia.

Original = devuelto + comprometido pendiente + remanente.

### **8.3. transfer_return**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| return_id           | uuid          | N, PK           |
| transfer_id         | uuid          | N               |
| return_effect_id    | uuid          | N               |
| amount              | numeric(24,2) | N               |
| currency            | varchar(3)    | N               |
| reason_code         | varchar(80)   | N               |
| return_destination  | jsonb         | N               |
| authorization_basis | jsonb         | N               |
| status              | varchar(30)   | N               |
| result_references   | jsonb         | N               |
| T                   | Bloque        |                 |
| C                   | Bloque        |                 |
| S                   | Bloque        |                 |

FK al control. UQ sobre return_effect_id.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (transfer_id, status, return_id).

El importe se compromete antes de ejecutar. La incertidumbre mantiene el compromiso.

La petición del ordenante no autoriza debitar al beneficiario.

## **9. Pagos: convenios**

### **9.1. collection_agreement**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| agreement_id          | uuid        | N, PK           |
| code                  | varchar(80) | N               |
| participant_reference | jsonb       | N               |
| contract_reference    | jsonb       | N               |
| current_version       | bigint      | N               |
| T                     | Bloque      |                 |
| lock_version          | bigint      | N               |

UQ sobre code.

### **9.2. collection_agreement_version**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| agreement_id              | uuid        | N               |
| agreement_version         | bigint      | N               |
| service_code              | varchar(80) | N               |
| currencies                | jsonb       | N               |
| instrument_types          | jsonb       | N               |
| allowed_payment_modes     | jsonb       | N               |
| charge_rules              | jsonb       | N               |
| multiple_payments_allowed | boolean     | N               |
| valid_from                | timestamptz | N               |
| valid_until               | timestamptz | S               |
| status                    | varchar(30) | N               |
| definition_revision       | bigint      | N               |
| approved_review_id        | uuid        | S               |
| T                         | Bloque      |                 |
| S                         | Bloque      |                 |
| lock_version              | bigint      | N               |
| updated_at                | timestamptz | N               |

PK (agreement_id, agreement_version) y FK al convenio.

Estados: DRAFT, PENDING_APPROVAL, ENABLED, SUSPENDED y WITHDRAWN.

Monedas e instrumentos no vacíos. Modalidades TOTAL, PARTIAL, ADVANCE y OVERDUE según habilitación expresa.

ENABLED exige revisión aprobatoria del contenido exacto.

La raíz referencia su versión actual mediante FK diferible.

Índice (status, valid_from, agreement_id, agreement_version).

### **9.3. collection_reference_format**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| agreement_id      | uuid        | N               |
| agreement_version | bigint      | N               |
| format_code       | varchar(80) | N               |
| format_version    | bigint      | N               |
| field_definitions | jsonb       | N               |

PK (agreement_id, agreement_version, format_code) y FK a versión.

Los campos identifican tipo, obligatoriedad, longitud, rango y normalización aplicables.

### **9.4. collection_creditor_capability**

| **Columna**        | **Tipo**    | **Nulabilidad** |
|--------------------|-------------|-----------------|
| agreement_id       | uuid        | N               |
| agreement_version  | bigint      | N               |
| operation_code     | varchar(80) | N               |
| schema_version     | bigint      | N               |
| authorized_service | varchar(80) | N               |
| adapter_code       | varchar(80) | S               |
| usage_conditions   | jsonb       | N               |

PK (agreement_id, agreement_version, operation_code) y FK a versión.

Las operaciones registradas comprenden consulta, aceptación preliminar, preparación, confirmación, recuperación,
cancelación y regularización.

No se habilita un convenio sin recuperación de ejecuciones pendientes de aplicar.

### **9.5. collection_agreement_review**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| review_id             | uuid          | N, PK           |
| agreement_id          | uuid          | N               |
| agreement_version     | bigint        | N               |
| definition_revision   | bigint        | N               |
| reviewed_content_hash | char(64)      | N               |
| preparer_person_id    | uuid          | N               |
| approver_person_id    | uuid          | S               |
| decision              | varchar(20)   | N               |
| approval_reference    | jsonb         | S               |
| submitted_at          | timestamptz   | N               |
| decided_at            | timestamptz   | S               |
| reason                | varchar(4000) | S               |
| T                     | Bloque        |                 |

FK a versión. UQ (review_id, agreement_id, agreement_version).

Decisiones PENDING, APPROVED y REJECTED.

Una aprobación exige fecha, aprobación y personas diferentes. La versión referencia su revisión mediante FK compuesta.

El contenido aprobado es inmutable.

## **10. Referencias de obligaciones**

### **10.1. payment_obligation_identity**

| **Columna**                   | **Tipo**     | **Nulabilidad** |
|-------------------------------|--------------|-----------------|
| obligation_id                 | uuid         | N, PK           |
| creditor_service              | varchar(80)  | N               |
| creditor_id                   | uuid         | N               |
| agreement_id                  | uuid         | N               |
| external_obligation_reference | varchar(120) | N               |
| created_at                    | timestamptz  | N               |

FK a convenio.

UQ (creditor_service, creditor_id, agreement_id, external_obligation_reference).

### **10.2. payment_obligation_observation**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| observation_id        | uuid          | N, PK           |
| obligation_id         | uuid          | N               |
| agreement_version     | bigint        | N               |
| source_version        | bigint        | S               |
| debtor_reference      | jsonb         | S               |
| amount                | numeric(24,2) | S               |
| currency              | varchar(3)    | S               |
| due_date              | date          | S               |
| allowed_payment_modes | jsonb         | N               |
| checked_at            | timestamptz   | N               |
| valid_until           | timestamptz   | S               |
| verification_status   | varchar(20)   | N               |
| source_reference      | jsonb         | N               |
| evidence_references   | jsonb         | N               |
| schema_version        | bigint        | N               |

FK a obligación. Se comprueba la versión del convenio de esa obligación.

Importe y moneda aparecen conjuntamente.

Estados: CONFIRMED, MANUAL, AMBIGUOUS y UNVERIFIED.

UQ (obligation_id, observation_id).

Índice (obligation_id, checked_at, observation_id).

Las observaciones son inmutables. No representan una segunda deuda ni sustituyen al acreedor.

## **11. Órdenes e instrumentos de pago**

### **11.1. payment_obligation_control**

| **Columna**   | **Tipo**    | **Nulabilidad** |
|---------------|-------------|-----------------|
| obligation_id | uuid        | N, PK           |
| lock_version  | bigint      | N               |
| updated_at    | timestamptz | N               |

FK a obligación. Existe antes de admitir pagos.

### **11.2. payment_order**

| **Columna**             | **Tipo**      | **Nulabilidad** |
|-------------------------|---------------|-----------------|
| payment_id              | uuid          | N, PK           |
| O                       | Bloque        |                 |
| payer_reference         | jsonb         | N               |
| obligation_id           | uuid          | N               |
| accepted_observation_id | uuid          | N               |
| agreement_id            | uuid          | N               |
| agreement_version       | bigint        | N               |
| payment_mode            | varchar(20)   | N               |
| accepted_amount         | numeric(24,2) | N               |
| currency                | varchar(3)    | N               |
| instrument_type         | varchar(20)   | N               |
| authorization_basis     | jsonb         | N               |
| occurrence_reference    | jsonb         | S               |
| execution_status        | varchar(20)   | N               |
| application_status      | varchar(30)   | N               |
| pending_components      | jsonb         | N               |
| T                       | Bloque        |                 |
| C                       | Bloque        |                 |
| S                       | Bloque        |                 |

FK a obligación, observación de esa obligación y convenio versionado.

Se comprueba que el convenio coincide con el de la obligación.

Ejecución: NOT_STARTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

Aplicación: NOT_PREPARED, PREPARED, PENDING, APPLIED, CANCELLED y REQUIRES_RESOLUTION.

Índices:

- (obligation_id, created_at, payment_id).

- (execution_status, application_status, updated_at, payment_id).

### **11.3. payment_instrument**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| payment_id                      | uuid        | N, PK           |
| instrument_type                 | varchar(20) | N               |
| account_id                      | uuid        | S               |
| reservation_reference           | jsonb       | S               |
| authorized_transfer_instruction | jsonb       | S               |
| transfer_reference              | jsonb       | S               |
| card_token_reference            | jsonb       | S               |
| card_authorization_reference    | jsonb       | S               |
| card_presentment_reference      | jsonb       | S               |
| schema_version                  | bigint      | N               |

FK a pago.

Variantes:

- ACCOUNT_DEBIT: cuenta y reserva opcional; no contiene datos de transferencia o tarjeta.

- TRANSFER: instrucción nueva o transferencia existente, exclusivamente una.

- CARD: referencia tokenizada; autorización y presentación cuando existan.

No se almacenan credenciales sensibles. Pagos no crea un débito paralelo al instrumento utilizado.

### **11.4. payment_obligation_commitment**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| payment_id            | uuid          | N, PK           |
| obligation_id         | uuid          | N               |
| payment_mode          | varchar(20)   | N               |
| amount                | numeric(24,2) | N               |
| preparation_reference | jsonb         | S               |
| status                | varchar(20)   | N               |
| created_at            | timestamptz   | N               |
| updated_at            | timestamptz   | N               |

FK a pago y control de obligación.

Estados: PENDING, PREPARED, EXECUTED, RELEASED y UNCERTAIN.

Índice (obligation_id, status, payment_id).

La admisión bloquea primero el control. Si no se admiten pagos múltiples, impide compromisos activos incompatibles.

Si se admiten, la preparación del acreedor determina la asignación. Consultar deuda no sustituye preparar su aplicación.

## **12. Preparación, ejecución y aplicación**

### **12.1. payment_creditor_preparation**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| payment_id                        | uuid          | N, PK           |
| creditor_service                  | varchar(80)   | N               |
| preliminary_acceptance_reference  | jsonb         | S               |
| application_preparation_reference | jsonb         | S               |
| obligation_source_version         | bigint        | S               |
| amount                            | numeric(24,2) | N               |
| currency                          | varchar(3)    | N               |
| valid_until                       | timestamptz   | S               |
| state                             | varchar(30)   | N               |
| prepared_content                  | jsonb         | N               |
| evidence_references               | jsonb         | N               |
| T                                 | Bloque        |                 |
| C                                 | Bloque        |                 |
| S                                 | Bloque        |                 |

FK a pago.

Estados: PRELIMINARY_PENDING, PRELIMINARY_ACCEPTED, PREPARATION_PENDING, PREPARED, REJECTED, CANCELLED y UNCERTAIN.

PREPARED exige ambas referencias.

### **12.2. payment_execution**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| payment_id                 | uuid        | N, PK           |
| remote_step_id             | uuid        | N               |
| instrument_reference       | jsonb       | N               |
| economic_effect_references | jsonb       | N               |
| execution_result_reference | jsonb       | S               |
| confirmed_at               | timestamptz | S               |
| status                     | varchar(20) | N               |
| evidence_references        | jsonb       | N               |

FK a pago y paso remoto.

Estados de ejecución del pago. CONFIRMED exige resultado, fecha y evidencias.

La fila conserva una única coordinación económica del pago. Una repetición recupera su resultado.

### **12.3. payment_creditor_application**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| payment_id                | uuid          | N, PK           |
| preparation_reference     | jsonb         | N               |
| confirmation_step_id      | uuid          | N               |
| applied_amount            | numeric(24,2) | S               |
| resulting_balance         | numeric(24,2) | S               |
| creditor_result_reference | jsonb         | S               |
| applied_at                | timestamptz   | S               |
| status                    | varchar(30)   | N               |
| evidence_references       | jsonb         | N               |

FK a pago y paso remoto.

Estados: PENDING, APPLIED, CANCELLED y REQUIRES_RESOLUTION.

APPLIED exige importe, referencia y fecha. El saldo solo se conserva si lo informa el acreedor.

Un instrumento ejecutado con aplicación desconocida permanece pendiente de aplicación. No vuelve a ejecutarse.

## **13. Ajustes de pagos**

### **13.1. payment_adjustment**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| adjustment_id             | uuid          | N, PK           |
| payment_id                | uuid          | N               |
| adjustment_type           | varchar(30)   | N               |
| previous_reference        | jsonb         | S               |
| resulting_reference       | jsonb         | S               |
| creditor_conformity       | jsonb         | S               |
| amount                    | numeric(24,2) | S               |
| currency                  | varchar(3)    | S               |
| original_effect_reference | jsonb         | S               |
| cancellation_evidence     | jsonb         | S               |
| authorization_basis       | jsonb         | N               |
| approval_reference        | jsonb         | S               |
| status                    | varchar(30)   | N               |
| result_references         | jsonb         | N               |
| T                         | Bloque        |                 |
| C                         | Bloque        |                 |
| S                         | Bloque        |                 |

FK a pago.

Modalidades:

- CANCEL_BEFORE_EXECUTION exige evidencia o barrera de cancelación.

- RECTIFY_REFERENCE exige referencia anterior, nueva y conformidad del acreedor.

- REFUND exige importe y moneda.

- CORRECT_EFFECT exige efecto original, alcance y aprobación aplicable.

Estados: PENDING, APPLIED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (payment_id, created_at, adjustment_id).

Una rectificación de referencia no ejecuta dinero. Un pago aplicado no se elimina.

## **14. Control económico de restituciones**

### **14.1. payment_restitution_control**

| **Columna**                     | **Tipo**      | **Nulabilidad** |
|---------------------------------|---------------|-----------------|
| restitution_control_id          | uuid          | N, PK           |
| debit_owner_service             | varchar(80)   | N               |
| original_debit_id               | uuid          | N               |
| payment_id                      | uuid          | N               |
| original_amount                 | numeric(24,2) | N               |
| currency                        | varchar(3)    | N               |
| claimed_amount                  | numeric(24,2) | N               |
| pending_amount                  | numeric(24,2) | N               |
| provisional_outstanding_amount  | numeric(24,2) | N               |
| definitive_amount               | numeric(24,2) | N               |
| remaining_amount                | numeric(24,2) | N               |
| cumulative_provisional_credited | numeric(24,2) | N               |
| cumulative_regularized          | numeric(24,2) | N               |
| cumulative_recovered            | numeric(24,2) | N               |
| lock_version                    | bigint        | N               |
| updated_at                      | timestamptz   | N               |

FK a pago.

UQ (debit_owner_service, original_debit_id).

Ecuación de cobertura actual:

Original = pendiente + provisional vigente + definitivo + remanente.

Los acumulados históricos no se suman nuevamente a esa ecuación.

El importe reclamado conserva alcance de solicitudes, sin multiplicar por duplicaciones del mismo reclamo ni superar el
débito.

### **14.2. payment_restitution_allocation**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| allocation_id                | uuid          | N, PK           |
| restitution_control_id       | uuid          | N               |
| amount                       | numeric(24,2) | N               |
| state                        | varchar(30)   | N               |
| source_adjustment_id         | uuid          | S               |
| source_reversal_case_id      | uuid          | S               |
| customer_credit_effect_id    | uuid          | S               |
| provisional_credit_reference | jsonb         | S               |
| definitive_result_reference  | jsonb         | S               |
| recovery_effect_id           | uuid          | S               |
| updated_at                   | timestamptz   | N               |
| lock_version                 | bigint        | N               |

FK al control, ajuste y caso de reversión.

Exactamente uno de ajuste y caso identifica el origen.

Estados: PENDING_CREDIT, PROVISIONAL, DEFINITIVE, RECOVERY_PENDING, RECOVERED y CANCELLED.

Importe positivo.

UQ parciales sobre los efectos de acreditación y recupero cuando existen.

Índice (restitution_control_id, state, allocation_id).

Una porción se divide antes de recibir tratamientos distintos. La suma de porciones activas coincide con los acumulados
del control.

### **14.3. payment_restitution_action**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| restitution_action_id | uuid          | N, PK           |
| allocation_id         | uuid          | N               |
| action_type           | varchar(40)   | N               |
| economic_destination  | varchar(40)   | N               |
| amount                | numeric(24,2) | N               |
| original_receipt_at   | timestamptz   | S               |
| resolution_reference  | jsonb         | S               |
| approval_reference    | jsonb         | S               |
| status                | varchar(30)   | N               |
| evidence_references   | jsonb         | N               |
| T                     | Bloque        |                 |
| C                     | Bloque        |                 |
| S                     | Bloque        |                 |

FK a porción.

Acciones: PREPARE_PROVISIONAL, CONFIRM_DEFINITIVE, REGULARIZE_PROVISIONAL y RECOVER_IMPROPER_PROVISIONAL.

Destinos: CREDIT_CUSTOMER, REGULARIZE_EXISTING_PROVISIONAL y AUTHORIZED_OTHER_APPLICATION.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índice (allocation_id, created_at, restitution_action_id).

### **14.4. Conservación económica**

Antes de solicitar un retorno al instrumento se fija su destino económico.

Si existe provisional por la porción, el definitivo transforma su situación sin crear otro crédito al cliente.

Una recuperación exige resolución. No utiliza descubierto automáticamente.

Una objeción del acreedor no retira por sí sola el provisional.

Las acciones inciertas mantienen el importe comprometido. Una devolución solo reabre deuda mediante decisión del
acreedor.

## **15. Casos económicos de reversión**

### **15.1. payment_reversal_case**

| **Columna**                      | **Tipo**      | **Nulabilidad** |
|----------------------------------|---------------|-----------------|
| reversal_case_id                 | uuid          | N, PK           |
| requesting_service               | varchar(80)   | N               |
| source_request_id                | uuid          | N               |
| restitution_control_id           | uuid          | N               |
| requested_amount                 | numeric(24,2) | N               |
| original_received_at             | timestamptz   | N               |
| identity_verified                | boolean       | N               |
| authority_verified               | boolean       | N               |
| correspondence_verified          | boolean       | N               |
| provisional_due_at               | timestamptz   | S               |
| deadline_calculation             | jsonb         | S               |
| status                           | varchar(30)   | N               |
| creditor_notification_references | jsonb         | N               |
| T                                | Bloque        |                 |
| C                                | Bloque        |                 |

FK al control de restitución.

UQ (requesting_service, source_request_id).

Estados: RECEIVED, VERIFYING, PROVISIONAL_PENDING, UNDER_REVIEW, DEFINITIVE, RECOVERY_PENDING y RESOLVED.

Comprobadas identidad, facultades y correspondencia, se conserva el vencimiento al finalizar el tercer día hábil
posterior a la recepción original.

Índice (provisional_due_at, reversal_case_id), donde status = 'PROVISIONAL_PENDING'.

## **16. Mandatos**

### **16.1. mandate**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| mandate_id                | uuid        | N, PK           |
| grantor_party_id          | uuid        | N               |
| beneficiary_reference     | jsonb       | N               |
| beneficiary_identity_hash | char(64)    | N               |
| account_id                | uuid        | N               |
| current_version           | bigint      | N               |
| status                    | varchar(20) | N               |
| T                         | Bloque      |                 |
| lock_version              | bigint      | N               |
| updated_at                | timestamptz | N               |
| event_version             | bigint      | N               |

Estados: PENDING, ACTIVE, SUSPENDED, REVOKED y EXPIRED.

Índices:

- (grantor_party_id, status, mandate_id).

- (account_id, status, mandate_id).

Cambiar beneficiario exige otro mandato. REVOKED no vuelve a ACTIVE.

### **16.2. mandate_version**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| mandate_id             | uuid          | N               |
| mandate_version        | bigint        | N               |
| purpose_code           | varchar(80)   | N               |
| amount_mode            | varchar(30)   | N               |
| fixed_amount           | numeric(24,2) | S               |
| maximum_per_occurrence | numeric(24,2) | S               |
| currency               | varchar(3)    | N               |
| frequency_unit         | varchar(10)   | N               |
| frequency_interval     | integer       | N               |
| anchor_date            | date          | N               |
| maximum_occurrences    | bigint        | S               |
| valid_from             | timestamptz   | N               |
| valid_until            | timestamptz   | S               |
| acceptance_evidence    | jsonb         | N               |
| T                      | Bloque        |                 |
| S                      | Bloque        |                 |

PK (mandate_id, mandate_version) y FK a mandato.

FIXED exige únicamente importe fijo. VARIABLE_WITH_MAXIMUM exige únicamente máximo. Importes positivos.

Unidades DAYS, WEEKS y MONTHS; intervalo y máximo de ocurrencias positivos.

Final posterior al inicio.

La raíz referencia su versión actual mediante FK diferible.

Ampliar importe, frecuencia o alcance exige nueva aceptación.

### **16.3. mandate_status_action**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| mandate_action_id   | uuid        | N, PK           |
| mandate_id          | uuid        | N               |
| action              | varchar(20) | N               |
| received_at         | timestamptz | N               |
| effective_at        | timestamptz | N               |
| actor_reference     | jsonb       | N               |
| evidence_references | jsonb       | N               |
| previous_status     | varchar(20) | N               |
| resulting_status    | varchar(20) | N               |
| T                   | Bloque      |                 |

FK a mandato.

Acciones SUSPEND, RESUME, REVOKE y EXPIRE.

Índice (mandate_id, received_at, mandate_action_id).

Las instrucciones anteriores se siguen mediante mandate_prior_instruction, cuyo contenido identifica ocurrencia,
instrucción y avance conocido.

Revocar no devuelve pagos ni extingue deuda.

## **17. Programaciones**

### **17.1. payment_schedule**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| schedule_id                      | uuid        | N, PK           |
| instruction_type                 | varchar(10) | N               |
| authorized_instruction_reference | jsonb       | N               |
| mandate_id                       | uuid        | S               |
| current_version                  | bigint      | N               |
| next_occurrence_index            | bigint      | N               |
| status                           | varchar(20) | N               |
| T                                | Bloque      |                 |
| lock_version                     | bigint      | N               |
| updated_at                       | timestamptz | N               |
| event_version                    | bigint      | N               |

FK opcional a mandato.

Tipos TRANSFER y PAYMENT.

Estados: ACTIVE, SUSPENDED y TERMINATED.

Índice (status, schedule_id) y (mandate_id, schedule_id), donde exista mandato.

### **17.2. payment_schedule_version**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| schedule_id           | uuid        | N               |
| schedule_version      | bigint      | N               |
| applies_from_index    | bigint      | N               |
| anchor_date           | date        | N               |
| frequency_unit        | varchar(10) | N               |
| frequency_interval    | integer     | N               |
| calendar_reference    | jsonb       | N               |
| adjustment            | varchar(10) | N               |
| local_time            | time(0)     | N               |
| zone_id               | varchar(80) | N               |
| start_date            | date        | N               |
| end_type              | varchar(20) | N               |
| end_date              | date        | S               |
| maximum_occurrences   | bigint      | S               |
| authorized_definition | jsonb       | N               |
| acceptance_evidence   | jsonb       | N               |
| effective_at          | timestamptz | N               |
| T                     | Bloque      |                 |
| S                     | Bloque      |                 |

PK (schedule_id, schedule_version) y FK a programación.

Unidades DAYS, WEEKS y MONTHS. Ajustes NONE, FOLLOWING y PRECEDING.

Finalización:

- ON_DATE exige fecha y no máximo.

- AFTER_OCCURRENCES exige máximo y no fecha.

- OPEN no contiene ninguno y requiere habilitación de la instrucción.

Índice (schedule_id, applies_from_index, schedule_version).

Las modificaciones no afectan índices ya admitidos. Si varias versiones se crean antes del próximo índice, se utiliza la
última versión efectiva aplicable.

La raíz referencia su versión actual mediante FK diferible.

### **17.3. payment_schedule_action**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| schedule_action_id  | uuid        | N, PK           |
| schedule_id         | uuid        | N               |
| action              | varchar(20) | N               |
| previous_version    | bigint      | N               |
| resulting_version   | bigint      | S               |
| applies_from_index  | bigint      | N               |
| actor_reference     | jsonb       | N               |
| acceptance_evidence | jsonb       | S               |
| effective_at        | timestamptz | N               |
| T                   | Bloque      |                 |

FK a programación y versiones.

Acciones MODIFY, SUSPEND, RESUME y TERMINATE.

MODIFY exige versión resultante y aceptación.

Índice (schedule_id, effective_at, schedule_action_id).

### **17.4. Anclaje**

El día inexistente se ajusta al último día del mes sin modificar el anclaje de meses posteriores.

El horario ordinario es 09:00:00. Una transferencia única puede conservar otro horario autorizado.

Las cuotas omitidas no se acumulan. Las versiones no reinician la numeración.

## **18. Ocurrencias**

### **18.1. payment_occurrence**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| occurrence_id            | uuid          | N, PK           |
| origin_type              | varchar(30)   | N               |
| schedule_id              | uuid          | S               |
| schedule_version         | bigint        | S               |
| occurrence_index         | bigint        | S               |
| mandate_id               | uuid          | S               |
| admitted_mandate_version | bigint        | S               |
| creditor_service         | varchar(80)   | S               |
| creditor_id              | uuid          | S               |
| collection_reference     | varchar(120)  | S               |
| contractual_date         | date          | S               |
| adjusted_date            | date          | S               |
| scheduled_at             | timestamptz   | S               |
| amount                   | numeric(24,2) | N               |
| currency                 | varchar(3)    | N               |
| instruction_payload      | jsonb         | N               |
| admitted_at              | timestamptz   | S               |
| outcome                  | varchar(20)   | N               |
| pending_components       | jsonb         | N               |
| T                        | Bloque        |                 |
| C                        | Bloque        |                 |
| S                        | Bloque        |                 |

FK a programación versionada y mandato versionado cuando existan.

Variantes:

- SCHEDULED exige programación, versión, índice y fechas.

- CREDITOR_PRESENTATION exige mandato, acreedor y referencia de cobro; no contiene programación.

Resultados: ORIGINATED, OMITTED, FAILED, PENDING y UNCERTAIN.

UQ parciales:

- (schedule_id, occurrence_index), para programadas.

- (mandate_id, creditor_service, creditor_id, collection_reference), para presentaciones.

La versión y la fecha ajustada no forman parte de esas identidades.

Índice (outcome, scheduled_at, occurrence_id).

### **18.2. occurrence_execution_attempt**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| occurrence_attempt_id         | uuid        | N, PK           |
| occurrence_id                 | uuid        | N               |
| previous_attempt_id           | uuid        | S               |
| responsible_service           | varchar(80) | N               |
| remote_action_id              | uuid        | N               |
| requested_content             | jsonb       | N               |
| late_confirmation_id          | uuid        | S               |
| outcome                       | varchar(20) | N               |
| absence_of_execution_evidence | jsonb       | S               |
| result_reference              | jsonb       | S               |
| T                             | Bloque      |                 |
| C                             | Bloque      |                 |
| S                             | Bloque      |                 |

FK a ocurrencia, intento anterior y confirmación tardía.

UQ (responsible_service, remote_action_id).

Resultados PENDING, ORIGINATED, FAILED y UNCERTAIN.

UQ parcial sobre occurrence_id, donde outcome IN ('PENDING','ORIGINATED','UNCERTAIN').

Un nuevo intento exige autorización expresa y ausencia comprobada de ejecución pendiente del anterior.

### **18.3. occurrence_originated_instruction**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| occurrence_id         | uuid        | N, PK           |
| occurrence_attempt_id | uuid        | N               |
| instruction_service   | varchar(80) | N               |
| instruction_type      | varchar(10) | N               |
| instruction_id        | uuid        | N               |
| remote_action_id      | uuid        | N               |
| confirmed_at          | timestamptz | N               |

FK a ocurrencia e intento.

UQ (instruction_service, instruction_id).

Una ocurrencia no mantiene dos instrucciones efectivas incompatibles.

### **18.4. occurrence_late_confirmation**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| confirmation_id        | uuid        | N, PK           |
| occurrence_id          | uuid        | N               |
| actor_reference        | jsonb       | N               |
| confirmed_content_hash | char(64)    | N               |
| confirmed_at           | timestamptz | N               |
| authorized_until       | timestamptz | S               |
| scope_payload          | jsonb       | N               |
| acceptance_evidence    | jsonb       | N               |
| T                      | Bloque      |                 |

FK a ocurrencia.

Índice (occurrence_id, confirmed_at, confirmation_id).

La recuperación tardía no ejecuta silenciosamente instrucciones vencidas.

La insuficiencia produce fallo sin reintentos automáticos.

## **19. Solicitudes de reversión de débitos**

### **19.1. debit_reversal_request**

| **Columna**               | **Tipo**      | **Nulabilidad** |
|---------------------------|---------------|-----------------|
| reversal_request_id       | uuid          | N, PK           |
| applicant_reference       | jsonb         | N               |
| authority_evidence        | jsonb         | N               |
| debit_owner_service       | varchar(80)   | N               |
| debit_id                  | uuid          | N               |
| payment_reference         | jsonb         | N               |
| mandate_id                | uuid          | S               |
| occurrence_id             | uuid          | S               |
| requested_amount          | numeric(24,2) | N               |
| currency                  | varchar(3)    | N               |
| reason                    | varchar(4000) | N               |
| received_at               | timestamptz   | N               |
| original_received_at      | timestamptz   | S               |
| original_receipt_evidence | jsonb         | S               |
| eligibility               | varchar(30)   | N               |
| economic_case_reference   | jsonb         | S               |
| remote_action_id          | uuid          | N               |
| complaint_reference       | jsonb         | S               |
| status                    | varchar(30)   | N               |
| T                         | Bloque        |                 |
| C                         | Bloque        |                 |

FK opcionales a mandato y ocurrencia.

Elegibilidad IN_PERIOD, OUT_OF_PERIOD y PENDING_VERIFICATION.

Estados: RECEIVED, VERIFYING, ECONOMIC_CASE_PENDING, FOLLOW_UP, REFERRED_TO_COMPLAINT y COMPLETED.

Recepción original y evidencia aparecen conjuntamente.

Índice (debit_owner_service, debit_id, received_at, reversal_request_id).

### **19.2. debit_reversal_deadline**

| **Columna**          | **Tipo**    | **Nulabilidad** |
|----------------------|-------------|-----------------|
| reversal_request_id  | uuid        | N               |
| calculation_version  | bigint      | N               |
| debit_date           | date        | N               |
| effective_receipt_at | timestamptz | N               |
| original_deadline    | date        | N               |
| adjusted_deadline    | date        | N               |
| calendar_calculation | jsonb       | N               |
| calculated_at        | timestamptz | N               |
| T                    | Bloque      |                 |

PK (reversal_request_id, calculation_version) y FK a solicitud.

Treinta días corridos desde el débito, excluyendo el día inicial y ajustando el final inhábil al siguiente hábil.

Fuera de plazo se conserva la recepción y la derivación a Reclamos.

## **20. Chequeras**

### **20.1. cheque_account_control**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| drawer_account_id | uuid        | N, PK           |
| lock_version      | bigint      | N               |
| updated_at        | timestamptz | N               |

Serializa rangos y altas de instrumentos de papel de la cuenta.

### **20.2. cheque_book**

| **Columna**             | **Tipo**       | **Nulabilidad** |
|-------------------------|----------------|-----------------|
| cheque_book_id          | uuid           | N, PK           |
| drawer_account_id       | uuid           | N               |
| currency                | varchar(3)     | N               |
| first_number            | varchar(120)   | N               |
| last_number             | varchar(120)   | N               |
| first_number_value      | numeric(120,0) | N               |
| last_number_value       | numeric(120,0) | N               |
| status                  | varchar(30)    | N               |
| authorization_reference | jsonb          | N               |
| T                       | Bloque         |                 |
| C                       | Bloque         |                 |

FK al control de cuenta.

Números compuestos exclusivamente por dígitos; auxiliares iguales a su interpretación numérica.

Inicial no superior al final.

Estados: PREPARED, DELIVERY_PENDING, DELIVERED y RETIRED.

Se prohíbe solapar rangos de la misma cuenta mediante exclusión del intervalo numérico cerrado.

### **20.3. cheque_book_delivery**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| delivery_id         | uuid        | N, PK           |
| cheque_book_id      | uuid        | N               |
| recipient_reference | jsonb       | N               |
| delivered_at        | timestamptz | N               |
| evidence_references | jsonb       | N               |
| T                   | Bloque      |                 |

FK a chequera.

Índice (cheque_book_id, delivered_at, delivery_id).

Entregar una chequera no emite instrumentos ni reserva fondos.

## **21. Instrumentos**

### **21.1. cheque_instrument**

| **Columna**                   | **Tipo**      | **Nulabilidad** |
|-------------------------------|---------------|-----------------|
| instrument_id                 | uuid          | N, PK           |
| support                       | varchar(10)   | N               |
| cheque_class                  | varchar(10)   | N               |
| drawer_account_reference      | jsonb         | S               |
| drawer_account_id             | uuid          | S               |
| drawer_party_reference        | jsonb         | S               |
| initial_beneficiary_reference | jsonb         | S               |
| current_holder_reference      | jsonb         | S               |
| amount                        | numeric(24,2) | S               |
| currency                      | varchar(3)    | S               |
| issue_date                    | date          | S               |
| payment_date                  | date          | S               |
| circulation_mode              | varchar(20)   | S               |
| cheque_book_id                | uuid          | S               |
| paper_number                  | varchar(120)  | S               |
| registry_participant_id       | uuid          | S               |
| registry_code                 | varchar(80)   | S               |
| external_registry_id          | varchar(120)  | S               |
| registry_sequence             | bigint        | S               |
| integrity_status              | varchar(20)   | N               |
| missing_field_codes           | jsonb         | N               |
| instrument_status             | varchar(30)   | N               |
| T                             | Bloque        |                 |
| C                             | Bloque        |                 |
| S                             | Bloque        |                 |

FK opcional a chequera.

Soporte PAPER o ECHEQ. Clase COMMON o DEFERRED.

Integridad COMPLETE o INCOMPLETE. ECHEQ exige COMPLETE desde su registro.

COMPLETE requiere cuenta, librador, beneficiario, tenedor comprobado, importe, moneda, emisión y circulación.

Circulación TO_ORDER o NOT_TO_ORDER.

Estados del instrumento: REGISTERED, IN_CIRCULATION, DISCOUNT_ASSIGNED, PAID y CANCELLED.

UQ parciales:

- (drawer_account_id, paper_number), para papel con numeración identificada.

- (registry_participant_id, registry_code, external_registry_id), para identidad electrónica.

Los datos de identidad electrónica aparecen conjuntamente. Su versión de esquema no altera la unicidad.

Índices:

- (drawer_account_id, instrument_id).

- (instrument_status, updated_at, instrument_id).

### **21.2. cheque_content_version**

| **Columna**         | **Tipo** | **Nulabilidad** |
|---------------------|----------|-----------------|
| instrument_id       | uuid     | N               |
| instrument_version  | bigint   | N               |
| content_snapshot    | jsonb    | N               |
| missing_field_codes | jsonb    | N               |
| T                   | Bloque   |                 |
| S                   | Bloque   |                 |

PK (instrument_id, instrument_version) y FK al instrumento.

Las instantáneas conservan antecedentes incompletos del papel y sus verificaciones posteriores.

Los datos esenciales emitidos de ECHEQ no se sobrescriben.

### **21.3. cheque_presentation_window**

| **Columna**          | **Tipo** | **Nulabilidad** |
|----------------------|----------|-----------------|
| instrument_id        | uuid     | N               |
| calculation_version  | bigint   | N               |
| base_date            | date     | N               |
| original_deadline    | date     | N               |
| adjusted_deadline    | date     | N               |
| calendar_calculation | jsonb    | N               |
| evidence_references  | jsonb    | N               |
| T                    | Bloque   |                 |

PK (instrument_id, calculation_version) y FK al instrumento.

El común no admite emisión futura.

El diferido exige pago posterior a emisión y separación máxima de 365 días corridos.

El plazo de presentación termina treinta días corridos después de emisión o pago, según clase, con ajuste final al
siguiente hábil.

## **22. Circulación y exclusión de usos**

### **22.1. cheque_circulation_action**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| circulation_action_id     | uuid        | N, PK           |
| instrument_id             | uuid        | N               |
| source_instrument_version | bigint      | N               |
| action_type               | varchar(30) | N               |
| related_action_id         | uuid        | S               |
| transferor_reference      | jsonb       | S               |
| recipient_reference       | jsonb       | S               |
| action_content            | jsonb       | N               |
| authority_evidence        | jsonb       | N               |
| evidence_references       | jsonb       | N               |
| occurred_at               | timestamptz | N               |
| outcome                   | varchar(20) | N               |
| T                         | Bloque      |                 |
| S                         | Bloque      |                 |

FK al instrumento, versión histórica y actuación relacionada.

Acciones: ACCEPT_BENEFICIARY, PROPOSE_TRANSFER, ACCEPT_TRANSFER, REJECT_TRANSFER, CANCEL_PENDING_TRANSFER,
REGISTER_ASSIGNMENT y REGISTER_GUARANTEE.

Resultados PENDING, CONFIRMED y REJECTED.

Índice (instrument_id, occurred_at, circulation_action_id).

El esquema exige los actores, conformidades y documentos correspondientes a cada acción.

### **22.2. cheque_holder_history**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| holder_history_id     | uuid        | N, PK           |
| instrument_id         | uuid        | N               |
| holder_reference      | jsonb       | N               |
| holder_identity_hash  | char(64)    | N               |
| circulation_action_id | uuid        | N               |
| valid_from            | timestamptz | N               |
| valid_until           | timestamptz | S               |

FK a instrumento y actuación confirmada.

Exclusión temporal por instrumento. No existen tenencias confirmadas superpuestas.

Índice (holder_identity_hash, instrument_id, valid_from).

### **22.3. cheque_active_commitment**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| commitment_id          | uuid        | N, PK           |
| instrument_id          | uuid        | N               |
| commitment_type        | varchar(20) | N               |
| circulation_action_id  | uuid        | S               |
| discount_commitment_id | uuid        | S               |
| presentation_id        | uuid        | S               |
| owner_service          | varchar(80) | N               |
| status                 | varchar(20) | N               |
| T                      | Bloque      |                 |
| lock_version           | bigint      | N               |

FK al instrumento y objeto de uso correspondiente.

Tipos TRANSFER, DISCOUNT y PRESENTATION. Cada tipo exige exclusivamente su referencia.

Estados ACTIVE, RESOLUTION_PENDING y RESOLVED.

UQ parcial sobre instrument_id, donde status IN ('ACTIVE','RESOLUTION_PENDING').

Una transmisión pendiente bloquea descuento y presentación.

La cesión por descuento resuelve el compromiso de formalización y deja la legitimación vinculada a la financiación.

## **23. Descuento, avales y cesiones**

### **23.1. cheque_discount_commitment**

| **Columna**                     | **Tipo**    | **Nulabilidad** |
|---------------------------------|-------------|-----------------|
| discount_commitment_id          | uuid        | N, PK           |
| instrument_id                   | uuid        | N               |
| financing_application_reference | jsonb       | N               |
| holder_reference                | jsonb       | N               |
| legitimacy_evidence             | jsonb       | N               |
| authorization_reference         | jsonb       | N               |
| financing_reference             | jsonb       | S               |
| collection_destination          | jsonb       | S               |
| status                          | varchar(30) | N               |
| evidence_references             | jsonb       | N               |
| T                               | Bloque      |                 |
| C                               | Bloque      |                 |

FK al instrumento.

Estados: COMMITTED, ASSIGNMENT_CONFIRMED, RELEASED y UNCERTAIN.

ASSIGNMENT_CONFIRMED exige financiación y destino de cobro.

Índice (instrument_id, status, discount_commitment_id).

### **23.2. cheque_discount_resolution**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| resolution_id          | uuid        | N, PK           |
| discount_commitment_id | uuid        | N               |
| action                 | varchar(30) | N               |
| financing_reference    | jsonb       | S               |
| evidence_references    | jsonb       | N               |
| T                      | Bloque      |                 |

FK al compromiso.

Acciones CONFIRM_ASSIGNMENT y RELEASE_NOT_EXECUTED.

Confirmar exige financiación y evidencia de cesión. Liberar exige descartar ejecución o pendiente incompatible.

No se registra anticipo ni concesión de crédito.

### **23.3. cheque_guarantee**

| **Columna**                     | **Tipo** | **Nulabilidad** |
|---------------------------------|----------|-----------------|
| guarantee_id                    | uuid     | N, PK           |
| instrument_id                   | uuid     | N               |
| circulation_action_id           | uuid     | N               |
| guarantor_reference             | jsonb    | N               |
| guaranteed_obligation_reference | jsonb    | N               |
| evidence_references             | jsonb    | N               |
| T                               | Bloque   |                 |

FK a instrumento y actuación.

El aval es total. No modifica tenedor ni reserva fondos.

### **23.4. cheque_assignment**

| **Columna**           | **Tipo** | **Nulabilidad** |
|-----------------------|----------|-----------------|
| assignment_id         | uuid     | N, PK           |
| instrument_id         | uuid     | N               |
| circulation_action_id | uuid     | N               |
| assignor_reference    | jsonb    | N               |
| assignee_reference    | jsonb    | N               |
| document_references   | jsonb    | N               |
| acceptance_evidence   | jsonb    | N               |
| recognition_evidence  | jsonb    | N               |
| T                     | Bloque   |                 |

FK a instrumento y actuación.

No se admite transmisión parcial, endoso en blanco ni beneficiario al portador.

## **24. Presentaciones y pago de cheques**

### **24.1. cheque_presentation**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| presentation_id          | uuid        | N, PK           |
| instrument_id            | uuid        | N               |
| origin_instruction_id    | uuid        | N               |
| holder_reference         | jsonb       | S               |
| financing_reference      | jsonb       | S               |
| collection_destination   | jsonb       | N               |
| received_at              | timestamptz | N               |
| receipt_evidence         | jsonb       | N               |
| validated_at             | timestamptz | S               |
| timeliness_date          | date        | S               |
| calendar_calculation     | jsonb       | S               |
| cycle_reference          | jsonb       | S               |
| previous_presentation_id | uuid        | S               |
| status                   | varchar(30) | N               |
| T                        | Bloque      |                 |
| C                        | Bloque      |                 |

FK al instrumento y presentación anterior.

UQ sobre origin_instruction_id.

Debe identificarse el tenedor legitimado o la financiación cesionaria, conforme al instrumento.

Estados: RECEIVED, VALIDATION_PENDING, WAITING_PAYMENT_DATE, PRESENTED y REQUIRES_RESOLUTION.

Índice (instrument_id, received_at, presentation_id).

El papel se valida en días hábiles hasta las 17:00. ECHEQ tiene recepción continua y corte de ciclo a las 18:00.

La recepción validada oportuna no pierde oportunidad por demora interna posterior.

### **24.2. cheque_presentation_result**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| result_id                    | uuid          | N, PK           |
| presentation_id              | uuid          | N               |
| source_service               | varchar(80)   | N               |
| source_result_id             | uuid          | N               |
| source_result_version        | bigint        | N               |
| result                       | varchar(20)   | N               |
| amount                       | numeric(24,2) | N               |
| currency                     | varchar(3)    | N               |
| participant_reference        | jsonb         | N               |
| reason_code                  | varchar(80)   | S               |
| monetary_evidence            | jsonb         | S               |
| absence_of_pending_execution | jsonb         | S               |
| evidence_references          | jsonb         | N               |
| T                            | Bloque        |                 |
| S                            | Bloque        |                 |

FK a presentación.

UQ (source_service, source_result_id, source_result_version).

Resultados PAID, REJECTED y UNCERTAIN.

PAID exige importe íntegro y evidencia monetaria. REJECTED exige causa y ausencia comprobada de ejecución pendiente.

### **24.3. cheque_payment_confirmation**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| instrument_id            | uuid        | N, PK           |
| presentation_id          | uuid        | N               |
| result_id                | uuid        | N               |
| payment_effect_reference | jsonb       | N               |
| paid_at                  | timestamptz | N               |

FK a instrumento, presentación y resultado.

Se comprueba que todos corresponden al mismo instrumento.

La PK impide un segundo pago. No se admite pago parcial.

Una nueva presentación requiere instrucción nueva, plazo válido y resultado anterior resuelto.

## **25. Medidas, cancelación y soporte**

### **25.1. cheque_measure_application**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| application_id        | uuid        | N, PK           |
| instrument_id         | uuid        | N               |
| measure_owner_service | varchar(80) | N               |
| measure_id            | uuid        | N               |
| measure_version       | bigint      | N               |
| component_code        | varchar(80) | N               |
| action                | varchar(20) | N               |
| scope_payload         | jsonb       | N               |
| valid_from            | timestamptz | N               |
| valid_until           | timestamptz | S               |
| basis_reference       | jsonb       | N               |
| status                | varchar(20) | N               |
| T                     | Bloque      |                 |

FK a instrumento.

UQ (measure_owner_service, measure_id, measure_version, component_code).

Acciones APPLY_MEASURE, MODIFY_MEASURE y LIFT_MEASURE.

Estados RECEIVED, APPLIED y REQUIRES_REVIEW.

Índice (instrument_id, measure_id, measure_version).

### **25.2. cheque_agreed_cancellation**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| cancellation_id              | uuid        | N, PK           |
| instrument_id                | uuid        | N               |
| drawer_conformity            | jsonb       | N               |
| current_holder_conformity    | jsonb       | N               |
| absence_of_pending_execution | jsonb       | N               |
| confirmed_at                 | timestamptz | N               |
| T                            | Bloque      |                 |

FK al instrumento. UQ sobre instrument_id.

No se confirma con presentación incierta.

### **25.3. cheque_support_return**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| support_return_id   | uuid        | N, PK           |
| instrument_id       | uuid        | N               |
| recipient_reference | jsonb       | N               |
| returned_at         | timestamptz | N               |
| physical_evidence   | jsonb       | N               |
| T                   | Bloque      |                 |

FK al instrumento.

Exclusivo de PAPER. Devolver el soporte no devuelve dinero.

## **26. Participantes y mecanismos de compensación**

### **26.1. payment_network_participant**

| **Columna**        | **Tipo**    | **Nulabilidad** |
|--------------------|-------------|-----------------|
| participant_id     | uuid        | N, PK           |
| participant_code   | varchar(80) | N               |
| party_reference    | jsonb       | N               |
| contract_reference | jsonb       | N               |
| T                  | Bloque      |                 |
| C                  | Bloque      |                 |

UQ sobre participant_code.

### **26.2. participant_mechanism**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| participant_id  | uuid        | N               |
| mechanism_code  | varchar(80) | N               |
| current_version | bigint      | N               |
| lock_version    | bigint      | N               |
| updated_at      | timestamptz | N               |

PK (participant_id, mechanism_code) y FK a participante.

### **26.3. participant_mechanism_version**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| participant_id             | uuid        | N               |
| mechanism_code             | varchar(80) | N               |
| mechanism_version          | bigint      | N               |
| operations                 | jsonb       | N               |
| currencies                 | jsonb       | N               |
| calendar_reference         | jsonb       | N               |
| cutoff_rules               | jsonb       | N               |
| message_schemas            | jsonb       | N               |
| confirmation_semantics     | jsonb       | N               |
| adapter_capabilities       | jsonb       | N               |
| recovery_rules             | jsonb       | N               |
| clearing_rules             | jsonb       | N               |
| partial_settlement_allowed | boolean     | N               |
| valid_from                 | timestamptz | N               |
| valid_until                | timestamptz | S               |
| status                     | varchar(30) | N               |
| definition_revision        | bigint      | N               |
| approved_review_id         | uuid        | S               |
| T                          | Bloque      |                 |
| S                          | Bloque      |                 |
| lock_version               | bigint      | N               |
| updated_at                 | timestamptz | N               |

PK (participant_id, mechanism_code, mechanism_version) y FK al mecanismo.

Estados: DRAFT, PENDING_APPROVAL, ENABLED, SUSPENDED y RETIRED.

La raíz referencia su versión actual mediante FK diferible.

No se habilita sin consulta y resolución de incertidumbre.

### **26.4. participant_mechanism_review**

| **Columna**           | **Tipo**      | **Nulabilidad** |
|-----------------------|---------------|-----------------|
| review_id             | uuid          | N, PK           |
| participant_id        | uuid          | N               |
| mechanism_code        | varchar(80)   | N               |
| mechanism_version     | bigint        | N               |
| definition_revision   | bigint        | N               |
| reviewed_content_hash | char(64)      | N               |
| preparer_person_id    | uuid          | N               |
| approver_person_id    | uuid          | S               |
| decision              | varchar(20)   | N               |
| approval_reference    | jsonb         | S               |
| submitted_at          | timestamptz   | N               |
| decided_at            | timestamptz   | S               |
| reason                | varchar(4000) | S               |
| T                     | Bloque        |                 |

FK a versión.

UQ (review_id, participant_id, mechanism_code, mechanism_version).

PENDING, APPROVED y REJECTED. La aprobación exige personas distintas, fecha y referencia competente.

La versión utiliza FK compuesta a revisión. El contenido aprobado es inmutable.

Suspender nuevos envíos conserva recuperación de pendientes y sus versiones originales.

## **27. Identidad externa e intercambios**

### **27.1. external_operation_control**

| **Columna**                   | **Tipo**     | **Nulabilidad** |
|-------------------------------|--------------|-----------------|
| external_operation_control_id | uuid         | N, PK           |
| participant_id                | uuid         | N               |
| mechanism_code                | varchar(80)  | N               |
| external_operation_id         | varchar(120) | N               |
| business_operation_reference  | jsonb        | S               |
| bound_content_hash            | char(64)     | S               |
| cancellation_barrier          | boolean      | N               |
| cancellation_evidence         | jsonb        | S               |
| known_state                   | varchar(30)  | N               |
| lock_version                  | bigint       | N               |
| created_at                    | timestamptz  | N               |
| updated_at                    | timestamptz  | N               |

FK al mecanismo.

UQ (participant_id, mechanism_code, external_operation_id).

Estados: UNKNOWN, NOT_RECEIVED, RECEIVED, ACCEPTED, EXECUTED, CANCELLED_FINAL y UNCERTAIN.

Puede existir una barrera antes de recibir una preparación tardía. En ese caso, operación y huella pueden estar ausentes
hasta vincular el antecedente.

CANCELLED_FINAL exige barrera y evidencia.

### **27.2. payment_exchange**

| **Columna**                   | **Tipo**     | **Nulabilidad** |
|-------------------------------|--------------|-----------------|
| exchange_id                   | uuid         | N, PK           |
| external_operation_control_id | uuid         | N               |
| participant_id                | uuid         | N               |
| mechanism_code                | varchar(80)  | N               |
| mechanism_version             | bigint       | N               |
| exchange_mode                 | varchar(20)  | N               |
| business_operation_reference  | jsonb        | N               |
| request_reference             | varchar(120) | N               |
| payload                       | jsonb        | N               |
| dispatch_authorized           | boolean      | N               |
| dispatch_state                | varchar(30)  | N               |
| business_result               | jsonb        | S               |
| pending_components            | jsonb        | N               |
| T                             | Bloque       |                 |
| C                             | Bloque       |                 |
| S                             | Bloque       |                 |

FK al control y versión del mecanismo.

Modos INSTRUCTION, QUERY y CANCELLATION.

Estados de despacho: NOT_AUTHORIZED, PENDING, CLAIMED, SENT, CONFIRMED, CANCELLED y UNCERTAIN.

UQ (participant_id, mechanism_code, request_reference).

UQ parcial sobre external_operation_control_id, donde exchange_mode = 'INSTRUCTION'.

Índice (dispatch_state, updated_at, exchange_id).

Las consultas y cancelaciones tienen actuaciones propias y conservan la operación externa consultada.

### **27.3. payment_dispatch_attempt**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| dispatch_attempt_id      | uuid        | N, PK           |
| exchange_id              | uuid        | N               |
| generation               | bigint      | N               |
| claim_owner              | uuid        | N               |
| claim_until              | timestamptz | N               |
| authorized_at            | timestamptz | N               |
| claimed_at               | timestamptz | N               |
| sent_at                  | timestamptz | S               |
| concluded_at             | timestamptz | S               |
| state                    | varchar(20) | N               |
| adapter_result_reference | jsonb       | S               |
| evidence_references      | jsonb       | N               |

FK a intercambio.

UQ (exchange_id, generation).

Estados CLAIMED, SENT, CONFIRMED, FAILED y UNCERTAIN.

UQ parcial sobre exchange_id, donde state IN ('CLAIMED','SENT','UNCERTAIN').

Índice (claim_until, dispatch_attempt_id), donde state = 'CLAIMED'.

El vencimiento del trabajador habilita investigar y recuperar. No habilita otro envío de una instrucción incierta.

## **28. Despacho y cancelación**

Despacho y cancelación bloquean:

1.  Control de operación externa.

2.  Intercambio.

3.  Intento de despacho.

El trabajador confirma su reclamación antes de llamar al adaptador.

Si la cancelación definitiva ocurrió primero, no se reclama ni despacha.

Si el despacho ya estaba reclamado, la cancelación permanece pendiente hasta resolverlo.

NOT_RECEIVED aislado no demuestra que una solicitud atrasada no pueda llegar.

CANCELLED_FINAL exige una barrera persistente y reconocida por el adaptador que impida ejecutar posteriormente la misma
referencia.

Una generación de trabajador antigua no puede sobrescribir el resultado de una generación posterior. Esta protección
local no sustituye la identidad idempotente y la barrera del adaptador.

## **29. Mensajes de red**

### **29.1. payment_network_message**

| **Columna**                   | **Tipo**     | **Nulabilidad** |
|-------------------------------|--------------|-----------------|
| message_id                    | uuid         | N, PK           |
| participant_id                | uuid         | N               |
| mechanism_code                | varchar(80)  | N               |
| external_message_id           | varchar(120) | N               |
| external_operation_id         | varchar(120) | N               |
| mechanism_version             | bigint       | N               |
| message_type                  | varchar(80)  | N               |
| message_schema_version        | bigint       | N               |
| occurred_at                   | timestamptz  | N               |
| received_at                   | timestamptz  | N               |
| original_payload              | jsonb        | N               |
| message_hash                  | char(64)     | N               |
| evidence_references           | jsonb        | N               |
| external_operation_control_id | uuid         | S               |
| incorporation_status          | varchar(20)  | N               |
| T                             | Bloque       |                 |

FK a mecanismo versionado y control cuando se vincula.

UQ (participant_id, mechanism_code, external_message_id).

Estados INCORPORATED, UNMATCHED y CONTRADICTORY.

DUPLICATE es el resultado de una recepción repetida; no reemplaza el estado del mensaje original.

Índices:

- (participant_id, mechanism_code, external_operation_id, received_at, message_id).

- (incorporation_status, received_at, message_id).

### **29.2. payment_message_conflict**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| conflict_id         | uuid        | N, PK           |
| original_message_id | uuid        | N               |
| conflicting_hash    | char(64)    | N               |
| conflicting_payload | jsonb       | N               |
| evidence_references | jsonb       | N               |
| received_at         | timestamptz | N               |
| T                   | Bloque      |                 |

FK a mensaje.

UQ (original_message_id, conflicting_hash).

El contenido original no se sobrescribe. Un mensaje no vinculado no se aplica por semejanza.

## **30. Conciliación y diferencias**

### **30.1. payment_reconciliation**

| **Columna**       | **Tipo**    | **Nulabilidad** |
|-------------------|-------------|-----------------|
| reconciliation_id | uuid        | N, PK           |
| scope_code        | varchar(80) | N               |
| source_references | jsonb       | N               |
| comparable_cuts   | jsonb       | N               |
| cycle_id          | uuid        | S               |
| status            | varchar(20) | N               |
| started_at        | timestamptz | N               |
| completed_at      | timestamptz | S               |
| T                 | Bloque      |                 |
| C                 | Bloque      |                 |

FK opcional a ciclo.

Estados RUNNING, MATCHED, DIFFERENCES y FAILED.

Índice (status, started_at, reconciliation_id).

### **30.2. payment_reconciliation_item**

| **Columna**            | **Tipo**    | **Nulabilidad** |
|------------------------|-------------|-----------------|
| reconciliation_item_id | uuid        | N, PK           |
| reconciliation_id      | uuid        | N               |
| position               | integer     | N               |
| left_reference         | jsonb       | S               |
| right_reference        | jsonb       | S               |
| comparison_result      | varchar(80) | N               |
| comparison_detail      | jsonb       | N               |

FK a conciliación.

UQ (reconciliation_id, position).

Al menos una referencia presente y posición positiva.

### **30.3. payment_difference**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| difference_id          | uuid          | N, PK           |
| reconciliation_item_id | uuid          | N               |
| classification         | varchar(30)   | N               |
| amount                 | numeric(24,2) | S               |
| currency               | varchar(3)    | S               |
| status                 | varchar(20)   | N               |
| T                      | Bloque        |                 |
| C                      | Bloque        |                 |

FK al elemento.

Clasificaciones: TEMPORAL_MISMATCH, MISSING_RECORD, DUPLICATE, AMOUNT_MISMATCH, CURRENCY_MISMATCH, STATE_CONTRADICTION y
SETTLEMENT_PENDING.

Estados OPEN, UNDER_REVIEW, RESOLUTION_PENDING y RESOLVED.

Índice (classification, status, updated_at, difference_id).

### **30.4. payment_difference_resolution**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| resolution_id       | uuid        | N, PK           |
| difference_id       | uuid        | N               |
| conclusion          | varchar(30) | N               |
| treatment_code      | varchar(80) | N               |
| evidence_references | jsonb       | N               |
| preparer_person_id  | uuid        | N               |
| approver_person_id  | uuid        | S               |
| approval_reference  | jsonb       | S               |
| manual_adjustment   | boolean     | N               |
| status              | varchar(20) | N               |
| T                   | Bloque      |                 |
| S                   | Bloque      |                 |

FK a diferencia.

Conclusiones EXPLAINED, CORRECTION_REQUIRED y UNRESOLVED.

Estados RECORDED, APPLICATION_PENDING y APPLIED.

Un ajuste manual exige aprobación y personas distintas.

La diferencia no se cierra mientras sus consecuencias permanezcan pendientes.

Conciliar no autoriza débitos ni altera efectos de clientes.

## **31. Hechos compensables**

### **31.1. clearing_fact**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| clearing_fact_id             | uuid          | N, PK           |
| source_service               | varchar(80)   | N               |
| economic_fact_id             | uuid          | N               |
| economic_fact_type           | varchar(80)   | N               |
| business_operation_reference | jsonb         | N               |
| participant_id               | uuid          | N               |
| mechanism_code               | varchar(80)   | N               |
| mechanism_version            | bigint        | N               |
| currency                     | varchar(3)    | N               |
| direction                    | varchar(10)   | N               |
| amount                       | numeric(24,2) | N               |
| occurred_at                  | timestamptz   | N               |
| original_fact_id             | uuid          | S               |
| evidence_references          | jsonb         | N               |
| eligibility_status           | varchar(20)   | N               |
| T                            | Bloque        |                 |
| S                            | Bloque        |                 |

FK a mecanismo versionado y hecho original cuando es ajuste.

UQ (source_service, economic_fact_type, economic_fact_id).

Sentido PAY o RECEIVE. Importe positivo.

Estados PENDING_EVIDENCE, ELIGIBLE y EXCLUDED.

Índice (participant_id, mechanism_code, currency, occurred_at, clearing_fact_id).

Una versión de mensaje o una entrega por otro canal no crea otro hecho económico.

Son compensables las acreditaciones y devoluciones confirmadas, ejecuciones que generan obligación entre participantes y
pagos de cheques confirmados.

Las reservas no son pagos.

## **32. Ciclos y composición**

### **32.1. clearing_cycle**

| **Columna**        | **Tipo**    | **Nulabilidad** |
|--------------------|-------------|-----------------|
| cycle_id           | uuid        | N, PK           |
| participant_id     | uuid        | N               |
| mechanism_code     | varchar(80) | N               |
| mechanism_version  | bigint      | N               |
| currency           | varchar(3)  | N               |
| interval_from      | timestamptz | N               |
| interval_to        | timestamptz | N               |
| calendar_reference | jsonb       | N               |
| rules_reference    | jsonb       | N               |
| status             | varchar(20) | N               |
| closed_at          | timestamptz | S               |
| T                  | Bloque      |                 |
| C                  | Bloque      |                 |

FK a mecanismo versionado.

Estados OPEN, DETERMINING y CLOSED.

Intervalo con inicio incluido y final excluido.

UQ (participant_id, mechanism_code, currency, interval_from, interval_to).

Exclusión de solapamiento por participante, mecanismo y moneda.

El corte ordinario es 18:00 de la zona institucional. Un hecho exactamente en el corte integra el siguiente intervalo.

### **32.2. clearing_cycle_fact**

Columnas obligatorias:

- cycle_id uuid.

- clearing_fact_id uuid.

PK sobre ambas y FK a ciclo y hecho.

UQ sobre clearing_fact_id.

La inclusión comprueba participante, mecanismo, moneda, momento y elegibilidad.

Un hecho solo integra un ciclo. Los hechos tardíos de un ciclo cerrado generan tratamiento de ajuste vinculado; no
reescriben la composición cerrada.

### **32.3. clearing_determination**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| determination_id            | uuid        | N, PK           |
| cycle_id                    | uuid        | N               |
| rules_version               | bigint      | N               |
| mode                        | varchar(10) | N               |
| netting_agreement_reference | jsonb       | S               |
| composition_hash            | char(64)    | N               |
| outcome                     | varchar(20) | N               |
| zero_net                    | boolean     | N               |
| T                           | Bloque      |                 |
| S                           | Bloque      |                 |

FK al ciclo. UQ sobre cycle_id.

Modo GROSS o NET. NET exige acuerdo expreso.

Resultados DETERMINED y REQUIRES_REVIEW.

La determinación y cierre se confirman junto con su composición y obligaciones.

Un ciclo cerrado es inmutable.

## **33. Obligaciones entre participantes**

### **33.1. clearing_obligation**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| obligation_id               | uuid          | N, PK           |
| determination_id            | uuid          | N               |
| cycle_id                    | uuid          | N               |
| participant_id              | uuid          | N               |
| mode                        | varchar(10)   | N               |
| direction                   | varchar(10)   | N               |
| amount                      | numeric(24,2) | N               |
| currency                    | varchar(3)    | N               |
| due_at                      | timestamptz   | N               |
| netting_agreement_reference | jsonb         | S               |
| gross_fact_id               | uuid          | S               |
| fulfilled_amount            | numeric(24,2) | N               |
| remaining_amount            | numeric(24,2) | N               |
| status                      | varchar(20)   | N               |
| treasury_action_id          | uuid          | N               |
| T                           | Bloque        |                 |
| C                           | Bloque        |                 |

FK a determinación, ciclo, participante y hecho bruto cuando corresponda.

Importe positivo. Importe = cumplido + remanente.

Estados DETERMINED, SETTLEMENT_PENDING, PARTIALLY_SETTLED, SETTLED y REQUIRES_REVIEW.

UQ:

- Parcial (determination_id), para NET.

- Parcial (determination_id, gross_fact_id), para GROSS.

- Sobre treasury_action_id.

NET no contiene hecho bruto individual y exige acuerdo. GROSS exige hecho.

Índice (status, due_at, obligation_id).

Un neto cero se conserva en la determinación sin crear instrucción monetaria de importe cero.

### **33.2. clearing_obligation_component**

| **Columna**      | **Tipo**      | **Nulabilidad** |
|------------------|---------------|-----------------|
| obligation_id    | uuid          | N               |
| clearing_fact_id | uuid          | N               |
| direction        | varchar(10)   | N               |
| amount           | numeric(24,2) | N               |

PK (obligation_id, clearing_fact_id).

FK a obligación y hecho.

UQ sobre clearing_fact_id.

En GROSS, el componente coincide con la obligación. En NET, la diferencia firmada de componentes determina importe y
sentido.

Solo se netea la misma contraparte y moneda.

### **33.3. clearing_settlement_result**

| **Columna**                | **Tipo**      | **Nulabilidad** |
|----------------------------|---------------|-----------------|
| settlement_result_id       | uuid          | N, PK           |
| obligation_id              | uuid          | N               |
| treasury_service           | varchar(80)   | N               |
| treasury_result_id         | uuid          | N               |
| treasury_result_version    | bigint        | N               |
| settlement_execution_id    | uuid          | N               |
| result                     | varchar(20)   | N               |
| confirmed_increment_amount | numeric(24,2) | N               |
| currency                   | varchar(3)    | N               |
| occurred_at                | timestamptz   | N               |
| evidence_references        | jsonb         | N               |
| T                          | Bloque        |                 |
| S                          | Bloque        |                 |

FK a obligación.

UQ (treasury_service, treasury_result_id, treasury_result_version).

Resultados CONFIRMED, PARTIAL, FAILED y UNCERTAIN.

El importe representa exclusivamente el incremento confirmado por ese resultado. FAILED y UNCERTAIN aportan incremento
cero.

La suma confirmada no supera la obligación. PARTIAL exige habilitación del mecanismo.

La recepción de revisiones de una misma ejecución se contrasta con su historia para no sumar nuevamente importes ya
acreditados.

La falta de liquidación externa no rechaza retroactivamente operaciones de clientes.

## **34. Cargos propios de instrumentos**

Transferencias, Pagos y Cheques mantienen sus cargos en tablas locales independientes:

- transfer_charge, con transfer_id.

- payment_charge, con payment_id.

- cheque_charge, con instrument_id.

Cada tabla incorpora:

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| charge_id                   | uuid          | N, PK           |
| Objeto local indicado       | uuid          | N               |
| generator_owner_service     | varchar(80)   | N               |
| generator_fact_id           | uuid          | N               |
| concept_code                | varchar(80)   | N               |
| beneficiary_service         | varchar(80)   | N               |
| conditions_reference        | jsonb         | N               |
| calculation_inputs          | jsonb         | N               |
| gross_amount                | numeric(24,2) | N               |
| discount_amount             | numeric(24,2) | N               |
| tax_amount                  | numeric(24,2) | N               |
| final_amount                | numeric(24,2) | N               |
| currency                    | varchar(3)    | N               |
| tax_determination_reference | jsonb         | S               |
| monetary_effect_id          | uuid          | N               |
| execution_reference         | jsonb         | S               |
| status                      | varchar(20)   | N               |
| T                           | Bloque        |                 |
| S                           | Bloque        |                 |

FK al objeto local.

UQ (generator_owner_service, generator_fact_id, concept_code) y sobre monetary_effect_id.

Final = bruto − bonificación + tributo. Bonificación no superior al bruto.

Estados DETERMINED, RESERVED, EXECUTED, REJECTED y UNCERTAIN.

Índice (objeto local, status, charge_id).

Cada concepto tiene un propietario de ejecución. Otro coordinador conserva su referencia sin originar otro cargo.

Reservar no equivale a cobrar. El principal permanece separado.

## **35. Orden de bloqueo y atomicidad**

### **35.1. Transferencias**

1.  Transferencia.

2.  Control de devoluciones.

3.  Cancelación o devolución.

4.  Ejecución y pasos afectados.

La creación del intercambio comprueba la barrera de la transferencia antes de confirmar su intención.

### **35.2. Pagos**

Admisión:

1.  Control de obligación.

2.  Pago.

3.  Compromiso.

4.  Preparación y pasos.

Restituciones:

1.  Control de obligación, cuando se modifica su coordinación.

2.  Control de restitución del débito.

3.  Pago.

4.  Caso, ajuste y porciones.

5.  Acciones y pasos.

Un proceso que solo bloquea el pago no adquiere después el control de obligación o restitución. Si los necesita,
comienza por esos controles.

La correspondencia entre débito y pago se consulta inicialmente y se vuelve a verificar después de adquirir los
bloqueos.

### **35.3. Mandatos y recurrencias**

1.  Mandato.

2.  Programación.

3.  Ocurrencia.

4.  Intento.

La admisión y revocación compiten sobre el mandato.

Si la revocación ocurrió primero, no se origina instrucción. Si la ocurrencia ya fue admitida, se coordina su
cancelación conforme al avance real.

Un proceso que solo modifica programación no adquiere después el mandato.

### **35.4. Cheques**

Para rangos y altas de papel:

1.  Control de cuenta.

2.  Chequera.

3.  Instrumento.

Para circulación, descuento y presentación:

1.  Instrumento.

2.  Compromiso activo.

3.  Transmisión, descuento o presentación.

4.  Resultado.

No se adquiere el control de cuenta después del instrumento.

### **35.5. Compensación**

Despacho: control externo → intercambio → intento de despacho.

Determinación: ciclo → hechos ordenados por identidad → determinación → obligaciones.

Liquidación: obligación → resultados.

La UQ de inclusión complementa los bloqueos de ciclo y hechos.

No se mantienen transacciones abiertas durante llamadas HTTP, publicación ni acceso documental.

## **36. Eventos**

| **Contrato** | **Tipo**                               | **Tema**                        | **Agregado**                              |
|--------------|----------------------------------------|---------------------------------|-------------------------------------------|
| CI-05-038    | TransferChanged                        | banking.transfer.events         | TRANSFER                                  |
| CI-05-039    | PaymentOrReversalChanged               | banking.payment.events          | PAYMENT                                   |
| CI-05-040    | MandateOrScheduleChanged               | banking.mandate.events          | MANDATE o SCHEDULE                        |
| CI-05-041    | OccurrenceOutcomeRecorded              | banking.mandate.events          | OCCURRENCE                                |
| CI-05-042    | ChequeChanged                          | banking.cheque.events           | CHEQUE                                    |
| CI-05-043    | PaymentExchangeOrReconciliationChanged | banking.payment-clearing.events | PAYMENT_EXCHANGE o PAYMENT_RECONCILIATION |
| CI-05-044    | ClearingObligationChanged              | banking.payment-clearing.events | CLEARING_OBLIGATION                       |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado.

El evento se registra junto con su cambio local.

Las versiones de contenido y las de evento se distinguen cuando tienen significado diferente, como en mandatos y
programaciones.

Una regularización de provisional no representa otra acreditación.

La recepción de un evento no repite movimientos, restituciones, presentaciones ni inclusiones compensables.

## **37. Recuperación y trabajos persistentes**

Las operaciones inciertas se consultan:

- Al detectar la incertidumbre.

- A los cinco minutos.

- A los treinta minutos.

- Por jornada operativa mientras continúe la investigación.

Estos plazos no autorizan reejecución ni liberación automática.

| **Trabajo**                        | **Identidad de deduplicación**        |
|------------------------------------|---------------------------------------|
| Paso remoto                        | Tabla de paso y step_id.              |
| Seguimiento de transferencia       | Transferencia y componente.           |
| Devolución                         | Devolución y componente.              |
| Aplicación del acreedor            | Pago y preparación original.          |
| Restitución                        | Acción económica y componente.        |
| Ocurrencia programada              | Programación e índice.                |
| Recuperación de ocurrencia         | Intento original.                     |
| Revocación de instrucción anterior | Acción de mandato y componente.       |
| Presentación de cheque             | Presentación y componente.            |
| Recuperación de despacho           | Intercambio y generación investigada. |
| Vinculación de mensaje             | Mensaje.                              |
| Determinación de ciclo             | Ciclo.                                |
| Liquidación entre participantes    | Obligación y actuación de Tesorería.  |

Las claves físicas respetan el límite de 120 caracteres y pueden utilizar hash de composición canónica.

Las respuestas tardías se incorporan al proceso original. Los conflictos conservan evidencias y abren investigación.

## **38. Seguridad, migraciones y conservación**

Las migraciones utilizan Flyway por servicio.

Las FK circulares se implementan como diferibles. Las restricciones de exclusión utilizan el soporte PostgreSQL
correspondiente.

Las tablas con referencias compuestas incorporan UQ sobre las columnas referenciadas. La pertenencia al mismo agregado
se comprueba mediante FK compuesta cuando sus identidades están materializadas en ambas tablas.

Las restricciones únicas de negocio se mantienen al archivar información.

No se eliminan mediante cascadas instrucciones, pagos, instrumentos, hechos, restituciones ni composiciones utilizadas.

Se separan permisos para instruir, cancelar, devolver, administrar convenios, ejecutar instrumentos, aplicar
obligaciones, administrar mandatos, operar cheques, incorporar mensajes y acreditar liquidaciones.

Un adaptador no obtiene permiso para debitar cuentas. Un pagador distinto del obligado no obtiene acceso general a su
deuda.

Redis no conserva la única copia de instrucciones, ocurrencias, compromisos, restituciones o barreras.

Después de restaurar un respaldo se pausan los envíos y se reconcilian actuaciones remotas antes de reanudar. Se
conservan efectos, referencias externas y actuaciones originales.

## **39. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                                                |
|-----------------------|---------------------------------------------------------------------------------|
| CI-05-001 a CI-05-006 | Transferencias, destinos, ejecuciones, cancelaciones y devoluciones.            |
| CI-05-007 y CI-05-008 | Convenios, versiones, formatos, capacidades y revisiones.                       |
| CI-05-009             | Identidades y observaciones de obligaciones.                                    |
| CI-05-010 a CI-05-012 | Pagos, instrumentos, compromisos, preparación, ejecución, aplicación y ajustes. |
| CI-05-013 a CI-05-015 | Mandatos, versiones y acciones de estado.                                       |
| CI-05-016 a CI-05-018 | Programaciones, ocurrencias, intentos y confirmaciones tardías.                 |
| CI-05-019             | Solicitudes de reversión y cálculo de plazo.                                    |
| CI-05-020             | Casos económicos, controles, porciones y acciones de restitución.               |
| CI-05-021 y CI-05-022 | Chequeras, instrumentos y contenido histórico.                                  |
| CI-05-023 a CI-05-025 | Circulación, legitimación, compromisos y descuento.                             |
| CI-05-026 a CI-05-029 | Presentaciones, resultados, medidas y devolución de soporte.                    |
| CI-05-030 y CI-05-031 | Participantes y mecanismos versionados.                                         |
| CI-05-032 a CI-05-034 | Operaciones externas, intercambios, despacho y mensajes.                        |
| CI-05-035             | Conciliaciones, diferencias y resoluciones.                                     |
| CI-05-036 y CI-05-037 | Hechos, ciclos, obligaciones y liquidación externa.                             |
| CI-05-038 a CI-05-044 | Outbox e historia de agregados.                                                 |

## **40. Escenarios de verificación de implementación**

| **Escenario**                                       | **Resultado exigido**                                  |
|-----------------------------------------------------|--------------------------------------------------------|
| Cambia el alias después de aceptar                  | Conserva el destino autorizado.                        |
| Respuesta monetaria perdida                         | Recupera el conjunto o efecto original.                |
| Misma operación externa con otra versión de esquema | Recupera la misma identidad.                           |
| Acreditación externa incierta                       | Mantiene cobertura y seguimiento.                      |
| Cancelación antes de crear intercambio              | Impide creación o despacho tardíos.                    |
| Adaptador informa solo NOT_RECEIVED                 | No presume cancelación definitiva.                     |
| Instrumento ejecutado e imputación incierta         | Recupera aplicación sin repetir instrumento.           |
| Pagos concurrentes incompatibles                    | Se ordenan mediante el control de obligación.          |
| Revocación anterior a admisión                      | No origina ocurrencia.                                 |
| Ocurrencia admitida antes de revocación             | Coordina cancelación según avance real.                |
| Cambia la versión de programación                   | No duplica índices existentes.                         |
| Dos fechas ajustadas al mismo día                   | Conserva dos identidades de serie distintas.           |
| Insuficiencia en recurrencia                        | No reintenta ni acumula importes automáticamente.      |
| Recuperación tardía                                 | Exige confirmación vinculada al contenido.             |
| Provisional 100 y definitivo 100                    | Regulariza sin acreditar otros 100.                    |
| Devolución y reversión concurrentes                 | Comparten control del débito original.                 |
| Restitución incierta                                | Conserva compromiso.                                   |
| Rango de chequera superpuesto                       | Se rechaza.                                            |
| Papel incompleto                                    | No se presenta como instrumento íntegro.               |
| Transmisión pendiente                               | Impide descuento y presentación incompatibles.         |
| Cheque 100 con capacidad 80                         | No paga parcialmente.                                  |
| Presentación anterior incierta                      | No admite otra por presunción.                         |
| Mensaje externo repetido                            | No repite efecto.                                      |
| Mensaje contradictorio                              | Conserva original y variante conflictiva.              |
| Mismo hecho por evento y conciliación               | Una sola inclusión compensable.                        |
| Hecho exactamente a las 18:00                       | Integra el siguiente intervalo.                        |
| Pago 100 y cobro 70 con acuerdo                     | Determina pago neto 30 en igual contraparte y moneda.  |
| Neto cero                                           | Conserva determinación sin instrucción monetaria cero. |
| Liquidación externa fallida                         | No rechaza retroactivamente operaciones de clientes.   |
| Restauración con efectos remotos posteriores        | Reconcilia antes de repetir instrucciones.             |

Las restricciones, exclusiones, bloqueos, identidades económicas y transacciones se verifican durante la implementación
con PostgreSQL real y escenarios concurrentes.
