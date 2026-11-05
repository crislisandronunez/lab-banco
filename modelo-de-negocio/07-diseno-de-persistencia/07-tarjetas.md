# **Diseño de persistencia — Módulo 7. Tarjetas**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Tarjetas.

| **Servicio**              | **Responsabilidad**                                                              |
|---------------------------|----------------------------------------------------------------------------------|
| Emisión de tarjetas       | Instrumentos, portadores, entrega, activación, credenciales y habilitaciones.    |
| Autorizaciones            | Decisiones de uso, controles operativos y coordinación de cobertura.             |
| Procesamiento de consumos | Presentaciones, devoluciones, intercambio, conciliación y obligaciones de red.   |
| Cuenta de tarjeta         | Capacidad crediticia, compromisos, consumos, planes, deuda, resúmenes y pagos.   |
| Disputas de tarjetas      | Cuestionamientos, investigación, protección, resolución y reparación coordinada. |

Comprende los contratos CI-07-001 a CI-07-048 y las preparaciones e intentos asociados.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones.  
No existen FK, consultas SQL ni transacciones entre bases.

El instrumento, la cuenta bancaria, la cuenta de tarjeta y el contrato tienen identidades diferentes.

Fondos administra el dinero de cuentas bancarias.  
Cuenta de tarjeta conserva capacidad y deuda crediticias.

El intercambio y la compensación de tarjetas pertenecen a Procesamiento de consumos.

## **2. Convenciones físicas**

### **2.1. Notación**

- N: columna obligatoria.

- S: columna que admite NULL.

- PK: clave primaria.

- UQ: restricción única.

- FK: clave foránea local.

Cuando una fila agrupa nombres de columnas, cada columna tiene individualmente el tipo y la nulabilidad indicados.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Todas las FK utilizan eliminación restringida.  
Las referencias remotas no generan FK.

Los índices se expresan con las columnas en orden.  
PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                  | **PostgreSQL** | **Restricción**                     |
|---------------------------|----------------|-------------------------------------|
| Identificador             | uuid           | Estable y no reutilizable.          |
| Versión                   | bigint         | Positiva.                           |
| Control de concurrencia   | bigint         | No negativo.                        |
| Contador transaccional    | bigint         | No negativo; empieza en cero.       |
| Secuencia asignada        | bigint         | Positiva.                           |
| Código                    | varchar(80)    | No vacío y registrado.              |
| Referencia externa        | varchar(120)   | No vacía.                           |
| Identificador enmascarado | varchar(40)    | Sin credencial completa.            |
| Fundamento                | varchar(4000)  | No vacío cuando está presente.      |
| Moneda                    | varchar(3)     | ARS o USD.                          |
| Importe                   | numeric(24,2)  | No negativo.                        |
| Variación                 | numeric(24,2)  | Con signo cuando se indica.         |
| Valor de cálculo          | numeric(30,12) | Según su significado.               |
| Tasa                      | numeric(24,12) | Conforme a la definición aplicable. |
| Hash                      | char(64)       | SHA-256 hexadecimal en minúsculas.  |
| Instante                  | timestamptz    | Intercambio en UTC.                 |
| Fecha                     | date           | Fecha bancaria.                     |
| Contenido                 | jsonb          | Esquema registrado y versionado.    |

Los importes ejecutables son positivos.

Se validan precisión y escala antes de persistir.  
Los cálculos intermedios conservan al menos doce decimales.

Los resultados monetarios utilizan HALF_UP.  
El mínimo se redondea hacia arriba a centavos sin superar el total.

Los calendarios se interpretan en America/Argentina/Buenos_Aires.

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

Los cinco servicios incorporan actuaciones, idempotencia, historia, outbox, inbox y trabajos persistentes.

Emisión incorpora authority_preparation.

Autorizaciones incorpora execution_identity_control y execution_attempt.

Cuenta de tarjeta incorpora las estructuras de intento cuando la incorporación de consumo  
exige preparaciones externas.

La consulta de un intento desconocido no demuestra aborto.  
Estos circuitos no crean barreras de aborto anticipado para identidades desconocidas.

## **3. Esquemas estructurados**

Cada JSONB conserva su versión en una columna explícita  
o mediante envoltorio con schemaCode, schemaVersion y data.

| **Esquema**              | **Contenido**                                                        |
|--------------------------|----------------------------------------------------------------------|
| RemoteReference          | Propietario, tipo, identidad y versión cuando corresponda.           |
| EvidenceReferences       | Hechos y evidencias identificados.                                   |
| PendingComponents        | Componente, responsable, motivo y seguimiento.                       |
| CardConfiguration        | Capacidades, canales, modalidades y controles solicitados.           |
| CardUsageBasis           | Instrumento, acto, canal, modalidad y huella.                        |
| CardOperation            | Participante, mecanismo, referencia externa, comercio o dispositivo. |
| CardAuthorizationCommand | Operación, importe, moneda, autenticación, plan y conversión.        |
| CardPresentation         | Operación, autorización, importe, moneda y condición final.          |
| EconomicComponents       | CAPITAL, INTEREST, FEE, TAX o ADJUSTMENT y sus fuentes.              |
| CardPlanInputs           | Capital, cuotas, tasas, cargos, tributos, calendario y supuestos.    |
| CardStatementComposition | Conceptos, capital, pagos, devoluciones y protecciones.              |
| CalendarCalculation      | Calendario, entradas, regla y resultado.                             |
| CardPaymentDistribution  | Obligaciones y componentes de imputación.                            |
| CardDisputeTreatment     | Resultado, importe y reparación por concepto.                        |
| CardNetworkMessage       | Mensaje según participante, mecanismo, versión y tipo.               |
| CardPosition             | Capacidad, deuda, compromisos, protecciones y saldo a favor.         |

No se admiten scripts, SQL, URL arbitrarias ni campos desconocidos.

Los atributos utilizados en unicidad, concurrencia y recuperación se conservan en columnas explícitas.

## **4. Coordinaciones remotas**

Las siguientes tablas utilizan la misma estructura física de coordinación:

| **Tabla**                           | **Columna propietaria N**    | **FK**                            |
|-------------------------------------|------------------------------|-----------------------------------|
| card_configuration_component        | configuration_id uuid        | card_configuration_version        |
| card_lifecycle_component            | lifecycle_action_id uuid     | card_lifecycle_action             |
| card_account_constitution_component | card_account_id uuid         | card_account                      |
| card_limit_change_component         | limit_change_id uuid         | card_credit_limit_change          |
| card_authorization_resource_step    | authorization_id uuid        | card_authorization                |
| card_presentation_execution         | presentation_id uuid         | card_presentation                 |
| card_return_execution               | adjustment_id uuid           | card_consumption_adjustment       |
| card_automatic_payment_step         | automatic_payment_id uuid    | card_automatic_payment            |
| card_currency_payment_step          | currency_preparation_id uuid | card_currency_payment_preparation |
| card_dispute_coordination           | dispute_id uuid              | card_dispute                      |
| card_account_closure_component      | closure_id uuid              | card_account_closure              |

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

- Índice parcial sobre economic_effect_id cuando exista.

Estados: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de llamar al propietario.  
La recuperación conserva actuación y efecto originales.

## **5. Emisión: instrumentos y credenciales**

### **5.1. card_instrument**

| **Columna**                                       | **Tipo**     | **Nulabilidad** |
|---------------------------------------------------|--------------|-----------------|
| card_id                                           | uuid         | N, PK           |
| issuance_owner_service                            | varchar(80)  | N               |
| issuance_reference                                | varchar(120) | N               |
| issuance_constitution_id                          | uuid         | N               |
| card_type                                         | varchar(10)  | N               |
| holder_party_id, contractual_responsible_party_id | uuid         | N               |
| holder_type                                       | varchar(10)  | N               |
| bank_account_reference, card_account_reference    | jsonb        | S               |
| contract_reference                                | jsonb        | N               |
| masked_identifier                                 | varchar(40)  | S               |
| current_credential_id                             | uuid         | S               |
| valid_from, valid_until                           | timestamptz  | N               |
| configuration_version                             | bigint       | N               |
| predecessor_card_id, successor_card_id            | uuid         | S               |
| emission_status, delivery_status                  | varchar(20)  | N               |
| activation_status, usage_status                   | varchar(20)  | N               |
| T, C                                              | Bloques      |                 |

UQ:

- (issuance_owner_service, issuance_reference).

- issuance_constitution_id.

DEBIT exige cuenta bancaria y no cuenta de tarjeta.  
CREDIT exige cuenta de tarjeta y no cuenta bancaria.

Portador PRIMARY o ADDITIONAL.

| **Dimensión** | **Valores**                                     |
|---------------|-------------------------------------------------|
| Emisión       | PREPARING, ISSUED, FAILED.                      |
| Entrega       | PENDING, DELIVERED, FAILED, UNCERTAIN.          |
| Activación    | INACTIVE, ACTIVE.                               |
| Uso           | ENABLED, BLOCKED, EXPIRED, REPLACED, CANCELLED. |

Vigencia final posterior al inicio.

Antecesor y sucesor utilizan FK locales.  
Se prohíben autorreferencias y cadenas cíclicas.

Índices:

- (holder_party_id, card_id).

- (contractual_responsible_party_id, card_id).

- (usage_status, valid_until, card_id).

Una adicional no crea otra capacidad general ni titularidad bancaria.

### **5.2. card_credential**

| **Columna**                          | **Tipo**     | **Nulabilidad** |
|--------------------------------------|--------------|-----------------|
| credential_id, card_id               | uuid         | N               |
| credential_version                   | bigint       | N               |
| protected_reference                  | varchar(120) | N               |
| protection_mode                      | varchar(20)  | N               |
| encrypted_secret, protected_verifier | bytea        | S               |
| key_reference                        | varchar(120) | N               |
| encryption_metadata                  | jsonb        | S               |
| valid_from, valid_until              | timestamptz  | N               |
| invalidated_at                       | timestamptz  | S               |
| status                               | varchar(20)  | N               |
| T                                    | Bloque       |                 |

PK credential_id, FK a instrumento.

UQ (card_id, credential_version) y (card_id, credential_id).

Modos ENCRYPTED y VERIFIER, con presencia exclusiva del material correspondiente.

Estados PREPARED, ACTIVE, REPLACED y REVOKED.

UQ parcial sobre card_id, donde status = 'ACTIVE'.

La raíz referencia una credencial propia mediante FK compuesta diferible.

Los secretos no aparecen en outbox, historia general, comprobantes ni respuestas ordinarias.

## **6. Entrega, activación y configuración**

### **6.1. card_delivery**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| delivery_id, card_id       | uuid        | N               |
| recipient_reference        | jsonb       | N               |
| channel_code               | varchar(80) | N               |
| delivery_request_reference | jsonb       | N               |
| status                     | varchar(20) | N               |
| delivered_at               | timestamptz | S               |
| evidence_references        | jsonb       | N               |
| T, C                       | Bloques     |                 |

PK delivery_id, FK a instrumento.

Estados PENDING, DELIVERED, FAILED y UNCERTAIN.  
DELIVERED exige fecha y evidencia.

Índice (card_id, created_at, delivery_id).

### **6.2. card_activation**

| **Columna**                                      | **Tipo**    | **Nulabilidad** |
|--------------------------------------------------|-------------|-----------------|
| activation_id, card_id, verified_holder_party_id | uuid        | N               |
| authentication_evidence, affirmative_consent     | jsonb       | N               |
| activated_at                                     | timestamptz | N               |
| T                                                | Bloque      |                 |

PK activation_id, FK a instrumento.

El portador verificado debe coincidir con el instrumento.

Emitir o entregar no activa por sí mismo.

### **6.3. card_configuration_version**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| configuration_id, card_id          | uuid        | N               |
| configuration_version              | bigint      | N               |
| capabilities, channels, modalities | jsonb       | N               |
| authorization_reference            | jsonb       | N               |
| effective_from                     | timestamptz | N               |
| effective_until                    | timestamptz | S               |
| application_status                 | varchar(30) | N               |
| T, S                               | Bloques     |                 |

PK configuration_id.

FK a instrumento y UQ (card_id, configuration_version).

Estados PENDING, PARTIALLY_APPLIED, APPLIED y REQUIRES_REVIEW.

La raíz referencia su configuración por identidad y versión.

Las configuraciones efectivas no se superponen para el mismo instrumento.  
El contenido aplicado es inmutable.

La tabla de componentes conserva propietario, referencia solicitada, versión y aplicación  
mediante su comando y resultado tipados.

### **6.4. card_lifecycle_action**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| lifecycle_action_id, card_id       | uuid          | N               |
| action                             | varchar(30)   | N               |
| reason                             | varchar(4000) | N               |
| scope_payload, evidence_references | jsonb         | N               |
| successor_card_id                  | uuid          | S               |
| additional_cards_treatment         | jsonb         | N               |
| local_effect_at                    | timestamptz   | S               |
| status                             | varchar(30)   | N               |
| T, C, S                            | Bloques       |                 |

PK lifecycle_action_id.

FK a instrumento y sucesor.

Acciones REPLACE_CREDENTIAL, RECOVER_ACCESS, BLOCK, UNBLOCK, RENEW, REPLACE y CANCEL.

Estados PENDING, LOCALLY_APPLIED, COMPLETED y REQUIRES_REVIEW.

Reemplazos bloquean instrumentos por identificador.

UNBLOCK no reactiva REPLACED ni CANCELLED.

## **7. Preparaciones de uso**

### **7.1. card_usage_control**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| card_id                     | uuid        | N, PK           |
| usage_version               | bigint      | N               |
| incompatible_change_pending | boolean     | N               |
| lock_version                | bigint      | N               |
| updated_at                  | timestamptz | N               |

FK al instrumento.

### **7.2. card_usage_preparation_basis**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| preparation_id, card_id               | uuid        | N               |
| card_version, configuration_version   | bigint      | N               |
| configuration_id                      | uuid        | N               |
| act_code, channel_code, modality_code | varchar(80) | N               |
| requested_content                     | jsonb       | N               |
| binding_content_hash                  | char(64)    | N               |
| schema_version                        | bigint      | N               |

PK preparation_id.

FK a preparación común, instrumento y configuración propia.

Índice (card_id, preparation_id).

Preparaciones y bloqueos incompatibles utilizan instrumento y control de uso.

Una preparación protege habilitación propia, no fondos ni crédito.

El bloqueo impide nuevas preparaciones y solicita resolver las existentes.  
No invalida silenciosamente ni libera por timeout.

## **8. Cuenta de tarjeta y posiciones**

### **8.1. card_account**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| card_account_id, constitution_id              | uuid        | N               |
| contract_reference, credit_approval_reference | jsonb       | N               |
| approval_application_reference                | jsonb       | S               |
| exposure_references, economic_terms_reference | jsonb       | N               |
| closing_day                                   | integer     | N               |
| status                                        | varchar(20) | N               |
| debt_version, last_fact_sequence              | bigint      | N               |
| new_uses_prevented                            | boolean     | N               |
| T, C                                          | Bloques     |                 |

PK card_account_id, UQ constitution_id.

Día de cierre entre 1 y 28.

Estados CONSTITUTING, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

El contador de hechos empieza en cero.  
Constituir no genera deuda.

### **8.2. card_account_responsible_party**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| card_account_id, party_id | uuid        | N               |
| role_code                 | varchar(80) | N               |
| obligation_basis          | jsonb       | N               |

PK (card_account_id, party_id, role_code) y FK a cuenta.

Índice inverso (party_id, card_account_id).

Se exige al menos un responsable contractual.

### **8.3. card_account_currency_position**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| card_account_id                                 | uuid          | N               |
| currency                                        | varchar(3)    | N               |
| general_limit, pending_commitments              | numeric(24,2) | N               |
| outstanding_principal, interest_amount          | numeric(24,2) | N               |
| fee_amount, tax_amount, credit_balance          | numeric(24,2) | N               |
| dispute_protected_principal, protected_coverage | numeric(24,2) | N               |
| new_capacity, excess                            | numeric(24,2) | N               |
| position_version                                | bigint        | N               |
| completeness                                    | varchar(20)   | N               |
| lock_version                                    | bigint        | N               |
| updated_at                                      | timestamptz   | N               |

PK (card_account_id, currency) y FK a cuenta.

Completitud COMPLETE o INCOMPLETE.

Capital protegido es parte del capital pendiente.  
No se suma nuevamente.

Capacidad nueva = máximo entre cero y límite general − capital pendiente − compromisos.

Exceso = máximo entre cero y capital pendiente − límite general.

El saldo a favor no amplía el límite aprobado.

### **8.4. card_position_history**

| **Columna**                        | **Tipo**   | **Nulabilidad** |
|------------------------------------|------------|-----------------|
| card_account_id                    | uuid       | N               |
| currency                           | varchar(3) | N               |
| position_version                   | bigint     | N               |
| position_snapshot, cause_reference | jsonb      | N               |
| fact_sequence                      | bigint     | N               |
| T, S                               | Bloques    |                 |

PK (card_account_id, currency, position_version).

FK a posición.  
Instantáneas inmutables confirmadas con cada cambio.

## **9. Límites crediticios**

### **9.1. card_credit_limit**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| limit_id, card_account_id | uuid        | N               |
| currency                  | varchar(3)  | N               |
| limit_type                | varchar(20) | N               |
| scope_reference           | jsonb       | N               |
| scope_identity_hash       | char(64)    | N               |
| current_version           | bigint      | N               |
| T                         | Bloque      |                 |
| lock_version              | bigint      | N               |

PK limit_id, FK a posición monetaria.

Tipos CREDIT_GENERAL y CREDIT_SUBLIMIT.

UQ (card_account_id, currency, limit_type, scope_identity_hash).

UQ parcial (card_account_id, currency), donde limit_type = 'CREDIT_GENERAL'.

### **9.2. card_credit_limit_version**

| **Columna**                            | **Tipo**      | **Nulabilidad** |
|----------------------------------------|---------------|-----------------|
| limit_id                               | uuid          | N               |
| limit_version                          | bigint        | N               |
| maximum_amount                         | numeric(24,2) | N               |
| operation_codes                        | jsonb         | N               |
| valid_from                             | timestamptz   | N               |
| valid_until                            | timestamptz   | S               |
| basis_reference                        | jsonb         | N               |
| approval_reference, exposure_reference | jsonb         | S               |
| T, S                                   | Bloques       |                 |

PK (limit_id, limit_version) y FK a límite.

Operaciones no vacías y final posterior al inicio.

### **9.3. card_credit_limit_usage**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| limit_id                                 | uuid          | N, PK           |
| committed_amount, outstanding_principal  | numeric(24,2) | N               |
| protected_coverage, new_capacity, excess | numeric(24,2) | N               |
| lock_version                             | bigint        | N               |
| updated_at                               | timestamptz   | N               |

FK a límite.

Cada límite comprueba su propia capacidad.  
Un sublímite no crea capital adicional.

### **9.4. card_credit_limit_change**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| limit_change_id, card_account_id | uuid        | N               |
| source_account_version           | bigint      | N               |
| requested_changes                | jsonb       | N               |
| contractual_basis                | jsonb       | N               |
| status                           | varchar(30) | N               |
| T, C, S                          | Bloques     |                 |

PK limit_change_id, FK a cuenta.

Estados PENDING, PARTIALLY_APPLIED, APPLIED, REJECTED y REQUIRES_REVIEW.

Una reducción conserva capital y compromisos firmes o inciertos.

## **10. Compromisos crediticios**

### **10.1. card_credit_commitment**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| commitment_id, card_account_id, card_id | uuid          | N               |
| authorization_owner_service             | varchar(80)   | N               |
| authorization_id, origin_attempt_id     | uuid          | N               |
| current_amount, transformed_amount      | numeric(24,2) | N               |
| released_amount, remaining_amount       | numeric(24,2) | N               |
| currency                                | varchar(3)    | N               |
| modality_code                           | varchar(80)   | N               |
| accepted_plan_reference                 | jsonb         | S               |
| firmness                                | varchar(20)   | N               |
| status                                  | varchar(30)   | N               |
| T, C, S                                 | Bloques       |                 |

PK commitment_id, FK a posición de cuenta.

UQ (authorization_owner_service, authorization_id).

Vigente = transformado + liberado + remanente.

Firmezas CANCELLABLE, FIRM y UNCERTAIN.

Estados ACTIVE, PARTIALLY_TRANSFORMED, TRANSFORMED, RELEASED y RESOLUTION_PENDING.

Índice (card_account_id, currency, status, commitment_id).

La compra en cuotas compromete todo el capital.

### **10.2. card_commitment_limit_allocation**

| **Columna**                        | **Tipo**      | **Nulabilidad** |
|------------------------------------|---------------|-----------------|
| commitment_id, limit_id            | uuid          | N               |
| limit_version                      | bigint        | N               |
| initial_amount, transformed_amount | numeric(24,2) | N               |
| released_amount, remaining_amount  | numeric(24,2) | N               |

PK (commitment_id, limit_id).

FK a compromiso y límite versionado.

Inicial = transformado + liberado + remanente.

Índice inverso (limit_id, commitment_id).

General y sublímites se afectan conjuntamente.  
Sus importes no se suman como deudas distintas.

### **10.3. card_credit_protected_coverage**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| commitment_id, limit_change_id, limit_id | uuid          | N               |
| initial_amount, transformed_amount       | numeric(24,2) | N               |
| released_amount, remaining_amount        | numeric(24,2) | N               |

PK sobre las tres identidades y FK a sus tablas.

Inicial = transformado + liberado + remanente.

La protección pertenece a compromisos concretos y no habilita nuevas operaciones.

### **10.4. card_credit_commitment_action**

| **Columna**              | **Tipo**      | **Nulabilidad** |
|--------------------------|---------------|-----------------|
| action_id, commitment_id | uuid          | N               |
| action                   | varchar(10)   | N               |
| target_amount            | numeric(24,2) | S               |
| reason                   | varchar(4000) | N               |
| evidence_references      | jsonb         | N               |
| T, S                     | Bloques       |                 |

PK action_id, FK a compromiso.

INCREASE y REDUCE exigen objetivo.  
RELEASE conserva alcance y evidencia de cancelabilidad.

No se libera capital transformado en deuda.

## **11. Autorizaciones: configuración operativa**

### **11.1. card_operational_configuration**

| **Columna**                  | **Tipo**    | **Nulabilidad** |
|------------------------------|-------------|-----------------|
| configuration_id, card_id    | uuid        | N               |
| source_configuration_version | bigint      | N               |
| effective_at                 | timestamptz | N               |
| status                       | varchar(20) | N               |
| applied_definition           | jsonb       | N               |
| T, C, S                      | Bloques     |                 |

PK configuration_id.

UQ (card_id, source_configuration_version).

Estados APPLIED, GAP_PENDING y REQUIRES_REVIEW.

Una configuración discontinua no se utiliza como completa.

### **11.2. card_operational_scope_control**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| scope_identity_hash   | char(64)    | N, PK           |
| scope_reference       | jsonb       | N               |
| configuration_version | bigint      | N               |
| lock_version          | bigint      | N               |
| updated_at            | timestamptz | N               |

Serializa el catálogo de límites aplicables a un objeto,  
incluidas altas y modificaciones concurrentes.

### **11.3. card_operational_limit**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| limit_id            | uuid        | N, PK           |
| scope_identity_hash | char(64)    | N               |
| limit_type          | varchar(20) | N               |
| currency            | varchar(3)  | N               |
| operation_codes     | jsonb       | N               |
| current_version     | bigint      | N               |
| T                   | Bloque      |                 |
| lock_version        | bigint      | N               |

FK a control de alcance.

Tipos OPERATION_MAXIMUM y PERIOD_USAGE.

Índice (scope_identity_hash, currency, limit_type, limit_id).

### **11.4. card_operational_limit_version**

| **Columna**      | **Tipo**      | **Nulabilidad** |
|------------------|---------------|-----------------|
| limit_id         | uuid          | N               |
| limit_version    | bigint        | N               |
| maximum_amount   | numeric(24,2) | N               |
| valid_from       | timestamptz   | N               |
| valid_until      | timestamptz   | S               |
| basis_reference  | jsonb         | N               |
| period_frequency | varchar(10)   | S               |
| period_zone      | varchar(80)   | S               |
| period_anchor    | date          | S               |
| T, S             | Bloques       |                 |

PK (limit_id, limit_version) y FK a límite.

PERIOD_USAGE exige frecuencia DAY, WEEK o MONTH, zona y anclaje.  
OPERATION_MAXIMUM no los contiene.

## **12. Períodos y afectaciones operativas**

### **12.1. card_usage_period**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| period_id, limit_id               | uuid          | N               |
| period_start, period_end          | timestamptz   | N               |
| frequency                         | varchar(10)   | N               |
| zone_id                           | varchar(80)   | N               |
| anchor_date                       | date          | N               |
| committed_amount, executed_amount | numeric(24,2) | N               |
| adjustment_amount                 | numeric(24,2) | N               |
| sequence                          | bigint        | N               |
| lock_version                      | bigint        | N               |
| updated_at                        | timestamptz   | N               |

PK period_id, FK a límite.

adjustment_amount admite signo.

UQ (limit_id, period_start, period_end).

Se excluyen intervalos superpuestos del mismo límite.

El uso efectivo del período es comprometido + ejecutado + ajustes, no negativo.

Modificar el importe del límite no reinicia el contador ni cambia la identidad del período.

### **12.2. card_operational_usage_allocation**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| allocation_id, authorization_id, limit_id | uuid          | N               |
| limit_version                             | bigint        | N               |
| period_id                                 | uuid          | S               |
| currency                                  | varchar(3)    | N               |
| authorized_amount, committed_amount       | numeric(24,2) | N               |
| executed_amount, released_amount          | numeric(24,2) | N               |
| sequence                                  | bigint        | N               |
| status                                    | varchar(20)   | N               |
| lock_version                              | bigint        | N               |

PK allocation_id.

FK a autorización, límite versionado y período.

UQ (authorization_id, limit_id).

Autorizado = comprometido + ejecutado + liberado.

Estados ACTIVE, RESOLUTION_PENDING y RESOLVED.

La modalidad periódica exige período; máximo por operación no lo contiene.

Índice (period_id, status, allocation_id).

### **12.3. card_operational_usage_change**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| change_id, allocation_id                        | uuid          | N               |
| change_type                                     | varchar(30)   | N               |
| presentation_owner_service                      | varchar(80)   | S               |
| presentation_id                                 | uuid          | S               |
| committed_delta, executed_delta, released_delta | numeric(24,2) | N               |
| reason_reference                                | jsonb         | N               |
| approval_reference                              | jsonb         | S               |
| T                                               | Bloque        |                 |

PK change_id, FK a afectación.

Las variaciones admiten signo.

UQ parcial (allocation_id, presentation_owner_service, presentation_id),  
donde exista presentación.

Una presentación al día siguiente transforma el período original.  
Las devoluciones no restauran automáticamente uso histórico.

## **13. Autorizaciones e intentos**

### **13.1. card_authorization**

| **Columna**                                    | **Tipo**      | **Nulabilidad** |
|------------------------------------------------|---------------|-----------------|
| authorization_id, participant_id, card_id      | uuid          | N               |
| mechanism_code                                 | varchar(80)   | N               |
| external_operation_id                          | varchar(120)  | N               |
| merchant_reference, device_reference           | varchar(120)  | S               |
| act_code, channel_code, modality_code          | varchar(80)   | N               |
| amount                                         | numeric(24,2) | N               |
| currency                                       | varchar(3)    | N               |
| authentication_evidence                        | jsonb         | N               |
| accepted_plan_reference, accepted_fx_reference | jsonb         | S               |
| decision                                       | varchar(10)   | N               |
| approved_content_hash                          | char(64)      | S               |
| valid_until                                    | timestamptz   | S               |
| current_attempt_id                             | uuid          | S               |
| T, C, S                                        | Bloques       |                 |

PK authorization_id.

UQ (participant_id, mechanism_code, external_operation_id).

Decisiones APPROVED, DECLINED y PENDING.

APPROVED exige huella, vigencia y cobertura confirmada.  
PENDING no se transforma en rechazo por error técnico.

Índice (card_id, created_at, authorization_id).

### **13.2. card_authorization_attempt_detail**

| **Columna**                                                       | **Tipo**    | **Nulabilidad** |
|-------------------------------------------------------------------|-------------|-----------------|
| attempt_id, authorization_id                                      | uuid        | N               |
| attempt_type                                                      | varchar(80) | N               |
| card_configuration_reference, operational_configuration_reference | jsonb       | N               |
| coverage_type                                                     | varchar(10) | S               |
| funds_reservation_reference, credit_commitment_reference          | jsonb       | S               |
| coverage_status                                                   | varchar(20) | N               |
| business_result                                                   | varchar(10) | S               |
| pending_decision                                                  | boolean     | N               |
| pending_components                                                | jsonb       | N               |

PK attempt_id, FK a intento común y autorización.

Tipo de intento CARD_AUTHORIZATION.

Cobertura DEBIT exige reserva; CREDIT exige compromiso.  
Son excluyentes.

Estados de cobertura NOT_REQUESTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

UQ parcial sobre authorization_id, donde pending_decision = true.

La coherencia entre indicador y decisión común se controla de forma diferida en la transacción.

COMMITTED conserva APPROVED o DECLINED.  
APPROVED exige cobertura CONFIRMED.

### **13.3. card_authorization_authority**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| attempt_id                    | uuid        | N               |
| component_code, owner_service | varchar(80) | N               |
| remote_action_id              | uuid        | N               |
| preparation_id                | uuid        | S               |
| binding_content_hash          | char(64)    | N               |
| state, notification_state     | varchar(30) | N               |
| evidence_references           | jsonb       | N               |
| updated_at                    | timestamptz | N               |
| lock_version                  | bigint      | N               |

PK (attempt_id, component_code) y FK a intento.

UQ (owner_service, remote_action_id).

Preparación REQUEST_PENDING, PREPARED, REJECTED, UNCERTAIN o CONCLUDED.

Notificación NOT_REQUIRED, PENDING, CONFIRMED o UNCERTAIN.

Índice (notification_state, updated_at, attempt_id, component_code).

### **13.4. Decisión**

Confirmación y aborto utilizan:

1.  Autorización.

2.  Identidad de ejecución.

3.  Intento.

4.  Controles de alcance ordenados.

5.  Límites y períodos ordenados.

6.  Afectaciones.

Se confirman conjuntamente decisión, afectaciones, cobertura, resultado, historia y outbox.

Un aborto local no demuestra liberación remota.  
Los recursos inciertos conservan seguimiento.

## **14. Ajustes y presentaciones de autorización**

### **14.1. card_authorization_action**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| authorization_action_id, authorization_id | uuid          | N               |
| action                                    | varchar(30)   | N               |
| target_amount                             | numeric(24,2) | S               |
| authorization_basis, evidence_references  | jsonb         | N               |
| result_payload                            | jsonb         | N               |
| T, C, S                                   | Bloques       |                 |

PK authorization_action_id, FK a autorización.

Acciones INCREASE_AMOUNT, REDUCE_AMOUNT, CANCEL, CHECK_EXPIRATION y RECORD_PRESENTATION_RESULT.

Aumentar o reducir exige importe objetivo.  
Aumentar exige cobertura y controles adicionales.

### **14.2. card_authorization_presentation_result**

| **Columna**                                    | **Tipo**      | **Nulabilidad** |
|------------------------------------------------|---------------|-----------------|
| result_id, authorization_id                    | uuid          | N               |
| presentation_service                           | varchar(80)   | N               |
| presentation_id                                | uuid          | N               |
| executed_amount                                | numeric(24,2) | N               |
| currency                                       | varchar(3)    | N               |
| is_final                                       | boolean       | N               |
| effect_references, resource_resolution_results | jsonb         | N               |
| T, S                                           | Bloques       |                 |

PK result_id, FK a autorización.

UQ (presentation_service, presentation_id).

Las autorizaciones de compra tienen siete días corridos de vigencia.

Vencer inicia resolución, sin liberar automáticamente dinero, crédito o contadores.

Una presentación final de 60 sobre autorización de 100 conserva ejecución 60  
y resolución comprobada del remanente 40.

## **15. Procesamiento de presentaciones**

### **15.1. card_presentation**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| presentation_id, participant_id, card_id        | uuid          | N               |
| external_presentation_id, external_operation_id | varchar(120)  | N               |
| mechanism_code                                  | varchar(80)   | N               |
| authorization_reference                         | jsonb         | S               |
| merchant_or_device_reference                    | varchar(120)  | N               |
| original_amount, presented_amount               | numeric(24,2) | N               |
| original_currency, presented_currency           | varchar(3)    | N               |
| conversion_reference, accepted_plan_reference   | jsonb         | S               |
| modality_code                                   | varchar(80)   | N               |
| occurred_at, received_at                        | timestamptz   | N               |
| is_final                                        | boolean       | N               |
| evidence_references                             | jsonb         | N               |
| result                                          | varchar(20)   | N               |
| T, C, S                                         | Bloques       |                 |

PK presentation_id.

UQ (participant_id, external_presentation_id).

Resultados CONFIRMED, PENDING_REVIEW, REJECTED y UNCERTAIN.

Conversión obligatoria cuando difieren las monedas.

Índices:

- (participant_id, mechanism_code, external_operation_id, presentation_id).

- (result, received_at, presentation_id).

### **15.2. card_presentation_review**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| review_id, presentation_id | uuid        | N               |
| cause_code                 | varchar(80) | N               |
| checks, resolution_payload | jsonb       | N               |
| preparer_person_id         | uuid        | N               |
| approver_person_id         | uuid        | S               |
| approval_reference         | jsonb       | S               |
| status                     | varchar(20) | N               |
| T, S                       | Bloques     |                 |

PK review_id, FK a presentación.

Estados PENDING, APPROVED y REJECTED.

La resolución excepcional que habilita un cargo exige aprobación y separación de intervinientes.

Una obligación externa no habilita por sí misma cargar al cliente.

### **15.3. Ejecución**

La coordinación distingue:

- Consumo de reserva de Fondos.

- Incorporación de consumo en Cuenta de tarjeta.

- Reconocimiento de extracción ya ejecutada por Operaciones de efectivo.

En una extracción ya debitada se conserva el movimiento original.  
No se crea otra reserva ni otro débito.

La recepción de un archivo no valida automáticamente cada presentación.

## **16. Consumos crediticios**

### **16.1. card_consumption**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| consumption_id, card_account_id, card_id | uuid          | N               |
| consumption_effect_id                    | uuid          | N               |
| presentation_service                     | varchar(80)   | N               |
| presentation_id                          | uuid          | N               |
| authorization_reference                  | jsonb         | S               |
| commitment_id                            | uuid          | S               |
| principal_amount                         | numeric(24,2) | N               |
| currency                                 | varchar(3)    | N               |
| confirmed_at                             | timestamptz   | N               |
| accepted_terms                           | jsonb         | N               |
| exceptional_resolution_reference         | jsonb         | S               |
| fact_sequence                            | bigint        | N               |
| T, C, S                                  | Bloques       |                 |

PK consumption_id.

FK a posición monetaria y compromiso cuando exista.

UQ:

- consumption_effect_id.

- (presentation_service, presentation_id).

- (card_account_id, fact_sequence).

Sin autorización, se exige resolución excepcional.

La transformación reduce compromiso y aumenta capital conjuntamente.  
No existe una fase confirmada con ambos importes sumados como exposición independiente.

### **16.2. Decisión con autoridades externas**

Cuando corresponda, Cuenta de tarjeta utiliza execution_attempt con tipo CARD_CONSUMPTION.

**card_consumption_attempt**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| attempt_id, consumption_effect_id, card_account_id | uuid        | N               |
| attempt_type                                       | varchar(80) | N               |
| requested_content                                  | jsonb       | N               |
| required_authorities, preparation_references       | jsonb       | N               |
| pending_decision                                   | boolean     | N               |
| resulting_consumption_id                           | uuid        | S               |
| pending_components                                 | jsonb       | N               |

PK attempt_id.

FK al intento común, cuenta y consumo resultante.

UQ parcial sobre consumption_effect_id mientras la decisión sea pendiente.

La decisión COMMITTED y el consumo se confirman en la misma transacción.

## **17. Capital y componentes de deuda**

### **17.1. card_capital_portion**

| **Columna**                                   | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------|---------------|-----------------|
| portion_id, consumption_id, card_account_id   | uuid          | N               |
| currency                                      | varchar(3)    | N               |
| original_amount, paid_amount, returned_amount | numeric(24,2) | N               |
| adjustment_amount                             | numeric(24,2) | N               |
| transferred_out_amount, remaining_amount      | numeric(24,2) | N               |
| prepared_amount                               | numeric(24,2) | N               |
| parent_portion_id                             | uuid          | S               |
| status                                        | varchar(20)   | N               |
| T                                             | Bloque        |                 |
| lock_version                                  | bigint        | N               |

PK portion_id.

FK a consumo, posición y porción anterior.

Ajustes con signo.

Remanente = original + ajustes − pagado − devuelto − transferido.

Preparado no superior al remanente.

Estados ACTIVE, SETTLED y TRANSFERRED.

Índice (card_account_id, currency, status, portion_id).

Si una porción requiere tratamientos distintos, se divide mediante transferencia de remanente  
a porciones hijas del mismo consumo.  
La salida del padre coincide con las entradas hijas.

La división no reconoce otro capital.  
El capital de la cuenta se obtiene del remanente vigente, no sumando originales históricos.

### **17.2. card_capital_classification**

| **Columna**                              | **Tipo**    | **Nulabilidad** |
|------------------------------------------|-------------|-----------------|
| classification_id, portion_id            | uuid        | N               |
| classification                           | varchar(20) | N               |
| plan_id, cycle_id                        | uuid        | S               |
| installment_index                        | bigint      | S               |
| contractual_due_date, effective_due_date | date        | S               |
| effective_from                           | timestamptz | N               |
| effective_until                          | timestamptz | S               |
| T                                        | Bloque      |                 |

PK classification_id, FK a porción, plan y ciclo cuando existan.

Clases UNBILLED, FUTURE_INSTALLMENT, BILLED y REVOLVING.

Exclusión temporal por porción.

Una reclasificación no crea capital.

### **17.3. card_debt_component**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| component_id, card_account_id                   | uuid          | N               |
| currency                                        | varchar(3)    | N               |
| component_type, concept_code                    | varchar(80)   | N               |
| capital_portion_id                              | uuid          | S               |
| recognized_amount, paid_amount, returned_amount | numeric(24,2) | N               |
| adjustment_amount                               | numeric(24,2) | N               |
| remaining_amount, prepared_amount               | numeric(24,2) | N               |
| source_reference                                | jsonb         | N               |
| status                                          | varchar(20)   | N               |
| T                                               | Bloque        |                 |
| lock_version                                    | bigint        | N               |

PK component_id, FK a posición y porción cuando sea capital.

Categorías CAPITAL, INTEREST, FEE, TAX y ADJUSTMENT.

CAPITAL es una representación del remanente de su porción, no otra deuda acumulable.  
Se mantiene correspondencia uno a uno mediante UQ parcial sobre capital_portion_id.

Los demás componentes utilizan sus propios reconocimientos.

Remanente = reconocido + ajustes − pagado − devuelto.

Estados ACCRUED, DUE, PARTIALLY_PAID, PAID y CORRECTED.

### **17.4. card_debt_entry**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| entry_id, card_account_id, component_id | uuid          | N               |
| debt_version                            | bigint        | N               |
| entry_position                          | integer       | N               |
| entry_type                              | varchar(30)   | N               |
| amount                                  | numeric(24,2) | N               |
| source_service, source_type             | varchar(80)   | N               |
| source_id                               | uuid          | N               |
| source_item_code                        | varchar(80)   | N               |
| effective_date                          | date          | N               |
| T                                       | Bloque        |                 |

PK entry_id.

FK a cuenta y componente propio.

Importe con signo según tipo.

UQ:

- (card_account_id, debt_version, entry_position).

- (source_service, source_type, source_id, source_item_code).

Entradas inmutables de reconocimiento, pago, devolución, reclasificación, transferencia y corrección.

## **18. Planes, cronogramas y CFT**

### **18.1. card_plan_calculation_version**

| **Columna**                       | **Tipo**      | **Nulabilidad** |
|-----------------------------------|---------------|-----------------|
| calculation_id                    | uuid          | N               |
| calculation_version               | bigint        | N               |
| principal_amount                  | numeric(24,2) | N               |
| currency                          | varchar(3)    | N               |
| modality                          | varchar(30)   | N               |
| installment_count                 | integer       | N               |
| rate_definition, charges, taxes   | jsonb         | N               |
| constitution_date, first_due_date | date          | N               |
| calendar_reference, assumptions   | jsonb         | N               |
| algorithm_code                    | varchar(80)   | N               |
| algorithm_version                 | bigint        | N               |
| T, S                              | Bloques       |                 |

PK (calculation_id, calculation_version).

Modalidades INTEREST_FREE_INSTALLMENTS y BANK_FINANCED_PLAN.

La primera exige interés cero.  
La segunda utiliza sistema francés.

### **18.2. card_plan**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| plan_id, card_account_id, calculation_id | uuid          | N               |
| calculation_version                      | bigint        | N               |
| acceptance_evidence                      | jsonb         | N               |
| selected_principal                       | numeric(24,2) | N               |
| status                                   | varchar(20)   | N               |
| T, C                                     | Bloques       |                 |

PK plan_id.

FK a cuenta y cálculo versionado.

Estados PROPOSED, ACTIVE, REPLACED y SETTLED.

### **18.3. card_plan_capital_allocation**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| plan_id, portion_id | uuid          | N               |
| amount              | numeric(24,2) | N               |
| effective_from      | timestamptz   | N               |
| effective_until     | timestamptz   | S               |

PK (plan_id, portion_id, effective_from).

FK a plan y porción.

Las porciones se dividen previamente cuando solo una parte se incorpora.  
No existen asignaciones activas incompatibles sobre la misma porción.

### **18.4. card_plan_installment**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| calculation_id                          | uuid          | N               |
| calculation_version                     | bigint        | N               |
| installment_index                       | bigint        | N               |
| contractual_date, adjusted_due_date     | date          | N               |
| principal, interest, fees, taxes, total | numeric(24,2) | N               |

PK sobre cálculo, versión e índice.  
FK al cálculo.

Total igual a suma de componentes.  
La suma de amortizaciones coincide con el capital; la última absorbe el residuo.

### **18.5. card_plan_cash_flow**

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

PK sobre cálculo, versión y posición.

### **18.6. card_plan_cft_result**

| **Columna**                     | **Tipo**       | **Nulabilidad** |
|---------------------------------|----------------|-----------------|
| calculation_id                  | uuid           | N               |
| calculation_version             | bigint         | N               |
| status                          | varchar(30)    | N               |
| method_code                     | varchar(80)    | N               |
| method_version                  | bigint         | N               |
| annual_effective_rate           | numeric(24,12) | S               |
| equation_residual               | numeric(30,12) | S               |
| converged                       | boolean        | N               |
| assumptions, missing_data_codes | jsonb          | N               |

PK y FK del cálculo versionado.

Estados DETERMINED, INCOMPLETE_INPUT y NO_UNIQUE_RATE.

Solo DETERMINED contiene tasa definitiva y exige convergencia.

Los intereses futuros previstos no integran deuda exigible.

## **19. Restituciones y devoluciones**

### **19.1. card_restitution_control**

En Procesamiento de consumos:

| **Columna**                                    | **Tipo**      | **Nulabilidad** |
|------------------------------------------------|---------------|-----------------|
| restitution_control_id                         | uuid          | N, PK           |
| original_effect_owner                          | varchar(80)   | N               |
| original_effect_id                             | uuid          | N               |
| original_amount                                | numeric(24,2) | N               |
| currency                                       | varchar(3)    | N               |
| pending_amount, provisional_outstanding_amount | numeric(24,2) | N               |
| definitive_amount, remaining_amount            | numeric(24,2) | N               |
| cumulative_regularized, cumulative_recovered   | numeric(24,2) | N               |
| lock_version                                   | bigint        | N               |
| updated_at                                     | timestamptz   | N               |

UQ (original_effect_owner, original_effect_id).

Original = pendiente + provisional vigente + definitivo + remanente.

Los acumulados históricos no se suman nuevamente.

Devoluciones, disputas y reparaciones de reclamos utilizan el mismo control.

### **19.2. card_restitution_allocation**

| **Columna**                                 | **Tipo**      | **Nulabilidad** |
|---------------------------------------------|---------------|-----------------|
| allocation_id, restitution_control_id       | uuid          | N               |
| amount                                      | numeric(24,2) | N               |
| origin_reference                            | jsonb         | N               |
| state                                       | varchar(30)   | N               |
| customer_effect_id, recovery_effect_id      | uuid          | S               |
| provisional_reference, definitive_reference | jsonb         | S               |
| T                                           | Bloque        |                 |
| lock_version                                | bigint        | N               |

PK allocation_id, FK al control.

Estados PENDING, PROVISIONAL, DEFINITIVE, RECOVERY_PENDING, RECOVERED y CANCELLED.

Los efectos presentes son únicos.

Índice (restitution_control_id, state, allocation_id).

### **19.3. card_consumption_adjustment**

| **Columna**                  | **Tipo**      | **Nulabilidad** |
|------------------------------|---------------|-----------------|
| adjustment_id                | uuid          | N, PK           |
| original_operation_reference | jsonb         | N               |
| mode                         | varchar(30)   | N               |
| amount                       | numeric(24,2) | N               |
| currency                     | varchar(3)    | N               |
| reason, evidence_references  | jsonb         | N               |
| restitution_allocation_id    | uuid          | S               |
| status                       | varchar(30)   | N               |
| T, C, S                      | Bloques       |                 |

FK opcional a porción de restitución.

CANCEL_UNPRESENTED tramita liberación y no crédito.

RETURN_CONSUMPTION exige asignación del control económico.

### **19.4. card_consumption_return**

En Cuenta de tarjeta:

| **Columna**                                           | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------------|---------------|-----------------|
| return_id, consumption_id, card_account_id            | uuid          | N               |
| return_effect_id                                      | uuid          | N               |
| amount                                                | numeric(24,2) | N               |
| currency                                              | varchar(3)    | N               |
| confirmed_return_reference, regularization_references | jsonb         | N               |
| status                                                | varchar(20)   | N               |
| T, C, S                                               | Bloques       |                 |

PK return_id.

FK a consumo y cuenta.  
UQ sobre return_effect_id.

Estados PENDING, APPLIED y REQUIRES_REVIEW.

**card_return_allocation**

Columnas N:

- return_id uuid.

- position integer.

- component_id uuid.

- amount numeric(24,2).

- treatment varchar(30).

PK (return_id, position).  
FK a devolución y componente.

Tratamientos FUTURE_PRINCIPAL, BILLED_PRINCIPAL y CREDIT_BALANCE.

La devolución atiende capital futuro desde las últimas cuotas,  
luego facturado pendiente y finalmente capital pagado mediante saldo a favor.

El definitivo regulariza el provisional, sin repetir reparación.

## **20. Ciclos y obligaciones facturables**

### **20.1. card_billing_cycle**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| cycle_id, card_account_id | uuid        | N               |
| period_from, period_to    | timestamptz | N               |
| contractual_closing_date  | date        | N               |
| cut_at                    | timestamptz | N               |
| cut_sequence              | bigint      | S               |
| status                    | varchar(20) | N               |
| T, C                      | Bloques     |                 |

PK cycle_id, FK a cuenta.

Estados OPEN, COMPOSING y CLOSED.

Exclusión de intervalos superpuestos por cuenta.

El corte es el inicio del día siguiente al cierre contractual, como final excluido.

Consumos y cierre se ordenan sobre la cuenta.  
Confirmaciones posteriores al corte integran el siguiente ciclo.

### **20.2. card_charge_occurrence**

| **Columna**                                  | **Tipo**      | **Nulabilidad** |
|----------------------------------------------|---------------|-----------------|
| occurrence_id, card_account_id, component_id | uuid          | N               |
| occurrence_code                              | varchar(80)   | N               |
| capital_classification_id                    | uuid          | S               |
| scheduled_due_date                           | date          | N               |
| amount                                       | numeric(24,2) | N               |
| status                                       | varchar(20)   | N               |
| T                                            | Bloque        |                 |

PK occurrence_id.

FK a cuenta, componente y clasificación cuando corresponda.

UQ (component_id, occurrence_code).

Estados PENDING, INCLUDED, PROTECTED y SETTLED.

Una ocurrencia identifica la exigencia de un componente.  
No crea otra deuda.

### **20.3. card_cycle_component**

Columnas N:

- cycle_id uuid.

- occurrence_id uuid.

- included_amount numeric(24,2).

PK sobre ciclo y ocurrencia.  
FK a ambos.

UQ sobre occurrence_id.

Las cuotas futuras y las autorizaciones no se incluyen como exigible.

### **20.4. card_reinstatement_occurrence**

| **Columna**                                     | **Tipo**      | **Nulabilidad** |
|-------------------------------------------------|---------------|-----------------|
| reinstatement_id, card_account_id, component_id | uuid          | N               |
| source_occurrence_id                            | uuid          | N               |
| protection_reference                            | jsonb         | N               |
| amount                                          | numeric(24,2) | N               |
| target_cycle_id                                 | uuid          | S               |
| status                                          | varchar(30)   | N               |
| T                                               | Bloque        |                 |

PK reinstatement_id, FK a cuenta, componente, ocurrencia original y ciclo posterior.

Estados PENDING, INCLUDED y BLOCKED_BY_OTHER_PROTECTION.

**card_cycle_reinstatement**

Columnas N cycle_id uuid y reinstatement_id uuid.

PK sobre ambas, FK a sus tablas y UQ sobre reincorporación.

La reincorporación cambia exigibilidad sin reconocer otro capital.  
El componente conserva identidad y relación con la disputa.

## **21. Resúmenes y versiones**

### **21.1. card_statement**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| statement_id, card_account_id, cycle_id | uuid        | N               |
| current_version                         | bigint      | N               |
| status                                  | varchar(30) | N               |
| T                                       | Bloque      |                 |
| lock_version                            | bigint      | N               |

PK statement_id.

FK a cuenta y ciclo propio.  
UQ (card_account_id, cycle_id).

Estados COMPOSING, VALIDATED, ISSUED, RECTIFIED y REQUIRES_REVIEW.

### **21.2. card_statement_version**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| statement_id                                       | uuid        | N               |
| statement_version                                  | bigint      | N               |
| cut_at                                             | timestamptz | N               |
| cut_sequence, debt_version                         | bigint      | N               |
| composition, source_references                     | jsonb       | N               |
| closing_date, planned_due_date, effective_due_date | date        | N               |
| calendar_calculation                               | jsonb       | N               |
| document_reference                                 | jsonb       | S               |
| previous_statement_version                         | bigint      | S               |
| rectification_basis                                | jsonb       | S               |
| issued_at                                          | timestamptz | S               |
| T, S                                               | Bloques     |                 |

PK (statement_id, statement_version) y FK a resumen.

La raíz referencia su versión actual mediante FK diferible.

Una rectificación exige versión anterior y fundamento.  
Las versiones emitidas son inmutables.

### **21.3. card_statement_currency_section**

| **Columna**                                       | **Tipo**      | **Nulabilidad** |
|---------------------------------------------------|---------------|-----------------|
| statement_id                                      | uuid          | N               |
| statement_version                                 | bigint        | N               |
| currency                                          | varchar(3)    | N               |
| billed_principal_pending                          | numeric(24,2) | N               |
| previous_overdue_minimum_principal                | numeric(24,2) | N               |
| additional_due_amount                             | numeric(24,2) | N               |
| protected_amount, payments_amount, returns_amount | numeric(24,2) | N               |
| total_amount, minimum_amount                      | numeric(24,2) | N               |
| debt_version                                      | bigint        | N               |

PK (statement_id, statement_version, currency) y FK a versión.

Para conceptos no protegidos:

- P = capital facturado pendiente.

- K = parte de P correspondiente a mínimos anteriores vencidos.

- C = conceptos adicionales exigibles.

Se exige 0 ≤ K ≤ P.

Total = P + C.

Mínimo = menor entre total y C + K + 10 % × (P − K).

El resultado se redondea hacia arriba a centavos y no supera el total.

### **21.4. card_minimum_capital_reference**

Columnas N:

- statement_id uuid.

- statement_version bigint.

- currency varchar(3).

- portion_id uuid.

- amount numeric(24,2).

PK sobre resumen, versión, moneda y porción.

FK a sección y porción.

La suma identifica K dentro de P.  
No reconoce otra deuda.

### **21.5. card_statement_obligation**

| **Columna**                                  | **Tipo**      | **Nulabilidad** |
|----------------------------------------------|---------------|-----------------|
| obligation_id, statement_id, card_account_id | uuid          | N               |
| currency                                     | varchar(3)    | N               |
| current_due_date                             | date          | N               |
| current_total, current_minimum               | numeric(24,2) | N               |
| calculation_history                          | jsonb         | N               |
| lock_version                                 | bigint        | N               |
| updated_at                                   | timestamptz   | N               |

PK obligation_id.

FK a resumen y posición.  
UQ (statement_id, currency).

Pagos, protección y cambio de vencimiento actualizan esta obligación estable.  
El documento anterior permanece inmutable.

## **22. Entrega y vencimiento**

### **22.1. card_statement_delivery**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| delivery_id, statement_id                    | uuid        | N               |
| statement_version                            | bigint      | N               |
| communication_reference, recipient_reference | jsonb       | N               |
| document_reference                           | jsonb       | N               |
| source_service                               | varchar(80) | N               |
| source_evidence_id                           | uuid        | N               |
| source_evidence_version                      | bigint      | N               |
| delivered_at                                 | timestamptz | N               |
| evidence_references                          | jsonb       | N               |
| T                                            | Bloque      |                 |

PK delivery_id, FK a resumen versionado.

UQ (source_service, source_evidence_id, source_evidence_version).

Índice (statement_id, statement_version, delivered_at, delivery_id).

### **22.2. card_statement_due_change**

| **Columna**                           | **Tipo**    | **Nulabilidad** |
|---------------------------------------|-------------|-----------------|
| due_change_id, statement_id           | uuid        | N               |
| delivery_id                           | uuid        | S               |
| previous_due_date, resulting_due_date | date        | N               |
| reason_code                           | varchar(80) | N               |
| calendar_calculation, consequences    | jsonb       | N               |
| T, S                                  | Bloques     |                 |

PK due_change_id, FK a resumen y entrega.

El vencimiento ordinario es cierre más veinte días corridos, ajustado al siguiente hábil.

Debe existir entrega acreditada al menos cinco días corridos antes.

Si es tardía, el vencimiento no precede al ordinario ni a entrega más cinco días, con ajuste hábil.

Sin evidencia no se presume entrega oportuna.

El cambio conserva la obligación, actualiza cobros y corrige financiación y mora atribuibles a la demora.

## **23. Preparación e imputación de pagos**

### **23.1. card_payment_preparation**

| **Columna**                     | **Tipo**      | **Nulabilidad** |
|---------------------------------|---------------|-----------------|
| preparation_id, card_account_id | uuid          | N               |
| payment_owner_service           | varchar(80)   | N               |
| payment_id                      | uuid          | N               |
| payer_reference                 | jsonb         | N               |
| amount                          | numeric(24,2) | N               |
| currency                        | varchar(3)    | N               |
| expected_effective_date         | date          | N               |
| payment_mode                    | varchar(20)   | N               |
| stage                           | varchar(30)   | N               |
| debt_version                    | bigint        | N               |
| expected_surplus                | numeric(24,2) | N               |
| status                          | varchar(30)   | N               |
| T, C, S                         | Bloques       |                 |

PK preparation_id, FK a posición.

UQ (card_account_id, payment_owner_service, payment_id).

Modalidades TOTAL, MINIMUM y SPECIFIED_AMOUNT.

Etapas PRELIMINARY_ACCEPTANCE y PREPARE_APPLICATION.

Estados PRELIMINARILY_ACCEPTED, PREPARED, APPLIED, CANCELLED y REQUIRES_RESOLUTION.

### **23.2. card_prepared_payment_allocation**

Columnas N:

- preparation_id uuid.

- component_id uuid.

- amount numeric(24,2).

- status varchar(20).

PK sobre preparación y componente.  
FK a ambos.

Estados HELD, APPLIED y RELEASED.

Índice (component_id, status, preparation_id).

Las preparaciones concurrentes consideran importes HELD.  
Preparar no acredita ingreso.

### **23.3. card_payment_receipt**

| **Columna**         | **Tipo**      | **Nulabilidad** |
|---------------------|---------------|-----------------|
| receipt_id          | uuid          | N, PK           |
| owner_service       | varchar(80)   | N               |
| source_receipt_id   | uuid          | N               |
| amount              | numeric(24,2) | N               |
| currency            | varchar(3)    | N               |
| effective_date      | date          | N               |
| evidence_references | jsonb         | N               |
| T, S                | Bloques       |                 |

UQ (owner_service, source_receipt_id).

### **23.4. card_payment_receipt_control**

Columnas N:

- receipt_id uuid, PK y FK a ingreso.

- received_amount numeric(24,2).

- committed_amount numeric(24,2).

- applied_amount numeric(24,2).

- credit_balance_amount numeric(24,2).

- pending_treatment_amount numeric(24,2).

- lock_version bigint.

- updated_at timestamptz.

Recibido = comprometido + aplicado + saldo a favor + pendiente de tratamiento.

### **23.5. card_payment_application**

| **Columna**                                | **Tipo**      | **Nulabilidad** |
|--------------------------------------------|---------------|-----------------|
| application_id, receipt_id, preparation_id | uuid          | N               |
| applied_amount, credit_balance_amount      | numeric(24,2) | N               |
| status                                     | varchar(20)   | N               |
| result_payload                             | jsonb         | N               |
| T, C                                       | Bloques       |                 |

PK application_id, FK a ingreso y preparación.

UQ (receipt_id, preparation_id).

Estados PENDING, APPLIED y REQUIRES_REVIEW.

### **23.6. card_payment_allocation**

Columnas N:

- allocation_id uuid.

- application_id uuid.

- component_id uuid.

- amount numeric(24,2).

- effective_date date.

PK allocation_id, FK a aplicación y componente.

Índice (component_id, effective_date, allocation_id).

La imputación atiende vencidos más antiguos y luego ciclo;  
dentro de ellos, intereses exigibles y capital, después comisiones y tributos asociados.

Pagar el total no precancela cuotas futuras.

### **23.7. card_credit_balance_entry**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| entry_id, card_account_id   | uuid          | N               |
| currency                    | varchar(3)    | N               |
| source_service, source_type | varchar(80)   | N               |
| source_id                   | uuid          | N               |
| holder_reference            | jsonb         | N               |
| amount                      | numeric(24,2) | N               |
| destination_reference       | jsonb         | S               |
| T                           | Bloque        |                 |

PK entry_id, FK a posición.

Importe con signo.

UQ (source_service, source_type, source_id).

El saldo a favor conserva titular y origen.  
Su aplicación no representa otro ingreso.

## **24. Débito automático y conversión**

### **24.1. card_automatic_payment**

| **Columna**                                       | **Tipo**      | **Nulabilidad** |
|---------------------------------------------------|---------------|-----------------|
| automatic_payment_id, statement_id, obligation_id | uuid          | N               |
| mandate_reference                                 | jsonb         | N               |
| mode                                              | varchar(10)   | N               |
| planned_collection_date                           | date          | N               |
| authorized_maximum                                | numeric(24,2) | N               |
| occurrence_reference                              | jsonb         | S               |
| status                                            | varchar(30)   | N               |
| T, C, S                                           | Bloques       |                 |

PK automatic_payment_id.

FK a resumen y obligación.  
UQ sobre obligation_id.

Modos TOTAL y MINIMUM.

Estados PENDING, ORIGINATED, CONFIRMED, INSUFFICIENT, UNCERTAIN y CANCELLED.

Ejecución ordinaria a las 09:00 del vencimiento efectivo.

Se determina remanente real.  
Insuficiencia rechaza el importe completo, sin parcialidad ni reintento oculto.

Cambiar vencimiento actualiza la coordinación y trata la ocurrencia anterior según su estado.

### **24.2. card_currency_payment_preparation**

| **Columna**                                             | **Tipo**      | **Nulabilidad** |
|---------------------------------------------------------|---------------|-----------------|
| currency_preparation_id, card_account_id, obligation_id | uuid          | N               |
| payment_preparation_id                                  | uuid          | N               |
| debt_currency, payment_currency                         | varchar(3)    | N               |
| target_amount, source_amount, charges_amount            | numeric(24,2) | N               |
| payer_reference, conversion_instruction                 | jsonb         | N               |
| fx_proposal_reference                                   | jsonb         | S               |
| valid_until                                             | timestamptz   | S               |
| converted_receipt_id                                    | uuid          | S               |
| status                                                  | varchar(30)   | N               |
| T, C, S                                                 | Bloques       |                 |

PK currency_preparation_id.

FK a cuenta, obligación, preparación e ingreso convertido.

Monedas diferentes.

Estados PREPARING, READY, EXECUTION_PENDING, APPLIED, UNCERTAIN y REQUIRES_REVIEW.

Preparar no ejecuta cambio.  
La conversión utiliza intercambio aceptado y el ingreso convertido se aplica una sola vez.

## **25. Financiación rotativa y mora**

### **25.1. card_financing_assessment**

| **Columna**                                | **Tipo**      | **Nulabilidad** |
|--------------------------------------------|---------------|-----------------|
| assessment_id, card_account_id             | uuid          | N               |
| currency                                   | varchar(3)    | N               |
| period_from, period_to, effective_due_date | date          | N               |
| position_cuts, rate_reference              | jsonb         | N               |
| interest_amount, unpaid_minimum            | numeric(24,2) | N               |
| delinquency_start_date                     | date          | S               |
| completeness                               | varchar(20)   | N               |
| pending_checks                             | jsonb         | N               |
| original_assessment_id                     | uuid          | S               |
| T, S                                       | Bloques       |                 |

PK assessment_id.

FK a posición y evaluación original cuando sea rectificación.

Índice (card_account_id, currency, period_from, period_to, assessment_id).

### **25.2. card_financing_daily_basis**

| **Columna**                            | **Tipo**       | **Nulabilidad** |
|----------------------------------------|----------------|-----------------|
| assessment_id                          | uuid           | N               |
| basis_date                             | date           | N               |
| eligible_principal, excluded_principal | numeric(24,2)  | N               |
| annual_rate                            | numeric(24,12) | N               |
| unrounded_interest                     | numeric(30,12) | N               |
| source_reference, exclusion_references | jsonb          | N               |

PK (assessment_id, basis_date) y FK a evaluación.

Interés simple Actual/365 desde el día siguiente al vencimiento.

Se excluyen intereses, cargos, tributos, cuotas futuras y capital protegido durante el período alcanzado.

### **25.3. card_capital_interest_assignment**

| **Columna**                      | **Tipo**    | **Nulabilidad** |
|----------------------------------|-------------|-----------------|
| assignment_id, portion_id        | uuid        | N               |
| modality                         | varchar(10) | N               |
| period_from, period_to           | date        | N               |
| plan_id, financing_assessment_id | uuid        | S               |
| status                           | varchar(20) | N               |
| T                                | Bloque      |                 |

PK assignment_id.

FK a porción, plan y evaluación.

PLAN exige plan; REVOLVING exige evaluación.

Se excluyen intervalos activos superpuestos por porción.  
Las rectificaciones sustituyen la asignación anterior sin borrar historia.

Cumplir el mínimo evita mora del mínimo aunque continúe financiación.

## **26. Disputas: casos y plazos**

### **26.1. card_dispute**

| **Columna**                                    | **Tipo**      | **Nulabilidad** |
|------------------------------------------------|---------------|-----------------|
| dispute_id                                     | uuid          | N, PK           |
| requester_reference, operation_reference       | jsonb         | N               |
| consumption_reference                          | jsonb         | S               |
| disputed_components                            | jsonb         | N               |
| amount                                         | numeric(24,2) | N               |
| currency                                       | varchar(3)    | N               |
| reason_code                                    | varchar(80)   | N               |
| received_at, original_received_at              | timestamptz   | N               |
| original_receipt_evidence, evidence_references | jsonb         | N               |
| investigation_status, repair_status            | varchar(30)   | N               |
| treatment_route                                | varchar(20)   | N               |
| complaint_reference                            | jsonb         | S               |
| T, C, S                                        | Bloques       |                 |

Investigación RECEIVED, UNDER_INVESTIGATION, AWAITING_EXTERNAL_INFORMATION,  
RESOLVED y UNDER_RECONSIDERATION.

Reparación NOT_REQUIRED, PENDING, PARTIAL, CONFIRMED y UNCERTAIN.

Vía DISPUTE, COMPLAINT o PENDING_EVIDENCE.

Índice (investigation_status, received_at, dispute_id).

### **26.2. card_dispute_deadline**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| deadline_id, dispute_id                   | uuid        | N               |
| deadline_type                             | varchar(30) | N               |
| calculation_version                       | bigint      | N               |
| base_at                                   | timestamptz | S               |
| due_at                                    | timestamptz | S               |
| calendar_calculation                      | jsonb       | N               |
| base_evidence_reference                   | jsonb       | S               |
| extension_reason, extension_communication | jsonb       | S               |
| missing_evidence_codes                    | jsonb       | N               |
| T                                         | Bloque      |                 |

PK deadline_id, FK a disputa.

UQ (dispute_id, deadline_type, calculation_version).

Plazos:

- Acuse el mismo día.

- Treinta días corridos desde recepción acreditada del resumen para la vía de disputa.

- Respuesta fundada en quince días corridos.

- Investigación exterior hasta sesenta días corridos con motivo comunicado.

- Disconformidad en siete días corridos desde comunicación acreditada.

- Reconsideración oportuna en diez días hábiles.

Sin evidencia de recepción del resumen no se presume vencido el plazo.

Fuera de plazo se recibe y deriva como reclamo.  
Las denuncias urgentes se derivan inmediatamente.

## **27. Protección de conceptos**

### **27.1. card_dispute_protection**

En Cuenta de tarjeta:

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| protection_id, card_account_id | uuid        | N               |
| dispute_service                | varchar(80) | N               |
| dispute_id                     | uuid        | N               |
| cause_code                     | varchar(80) | N               |
| current_version                | bigint      | N               |
| valid_from                     | timestamptz | N               |
| valid_until                    | timestamptz | S               |
| status                         | varchar(20) | N               |
| T                              | Bloque      |                 |
| lock_version                   | bigint      | N               |

PK protection_id, FK a cuenta.

UQ (dispute_service, dispute_id, cause_code).

Estados ACTIVE, LIFT_PENDING y LIFTED.

### **27.2. card_protection_allocation**

| **Columna**                 | **Tipo**      | **Nulabilidad** |
|-----------------------------|---------------|-----------------|
| protection_id, component_id | uuid          | N               |
| amount                      | numeric(24,2) | N               |
| valid_from                  | timestamptz   | N               |
| valid_until                 | timestamptz   | S               |
| status                      | varchar(20)   | N               |

PK (protection_id, component_id, valid_from).

FK a protección y componente de la misma cuenta.

La superposición de causas se evalúa sobre las mismas porciones o componentes,  
sin sumar dos veces el importe protegido.

Si requieren tratamientos parciales distintos, se separan previamente las porciones económicas.

### **27.3. card_protection_action**

| **Columna**                         | **Tipo**      | **Nulabilidad** |
|-------------------------------------|---------------|-----------------|
| protection_action_id, protection_id | uuid          | N               |
| protection_version                  | bigint        | N               |
| action                              | varchar(10)   | N               |
| component_scope                     | jsonb         | N               |
| amount                              | numeric(24,2) | N               |
| effective_at                        | timestamptz   | N               |
| basis_reference                     | jsonb         | N               |
| reinstatement_reference             | jsonb         | S               |
| T, S                                | Bloques       |                 |

PK protection_action_id, FK a protección.

UQ (protection_id, protection_version).

Acciones APPLY, MODIFY y LIFT.

La protección suspende exigibilidad e intereses y recalcula total y mínimo.  
No elimina capital ni restituye capacidad automáticamente.

Levantar una causa no elimina las demás.

## **28. Resolución, reconsideración y reparación**

### **28.1. card_dispute_resolution**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| resolution_id, dispute_id                 | uuid        | N               |
| conclusion                                | varchar(30) | N               |
| component_treatments, evidence_references | jsonb       | N               |
| preparer_person_id, approver_person_id    | uuid        | N               |
| approval_reference                        | jsonb       | N               |
| communicated_at                           | timestamptz | S               |
| communication_evidence                    | jsonb       | S               |
| T, S                                      | Bloques     |                 |

PK resolution_id, FK a disputa.

Conclusiones FAVOURABLE, PARTIALLY_FAVOURABLE y UNFAVOURABLE.

Personas distintas.  
Comunicación y evidencia aparecen conjuntamente.

Los tratamientos identifican resultado e importe por concepto.

### **28.2. card_dispute_reconsideration**

| **Columna**                                   | **Tipo**    | **Nulabilidad** |
|-----------------------------------------------|-------------|-----------------|
| reconsideration_id, dispute_id, resolution_id | uuid        | N               |
| received_at                                   | timestamptz | N               |
| grounds, evidence_references                  | jsonb       | N               |
| timeliness                                    | varchar(20) | N               |
| due_at                                        | timestamptz | S               |
| result_reference                              | jsonb       | S               |
| status                                        | varchar(20) | N               |
| T, C                                          | Bloques     |                 |

PK reconsideration_id, FK a disputa y resolución propia.

Oportunidad IN_PERIOD, OUT_OF_PERIOD o PENDING_EVIDENCE.

Estados RECEIVED, UNDER_REVIEW y RESOLVED.

### **28.3. card_dispute_economic_action**

En Procesamiento de consumos:

| **Columna**                           | **Tipo**      | **Nulabilidad** |
|---------------------------------------|---------------|-----------------|
| economic_action_id                    | uuid          | N, PK           |
| dispute_reference, decision_reference | jsonb         | N               |
| restitution_allocation_id             | uuid          | N               |
| mode                                  | varchar(30)   | N               |
| amount                                | numeric(24,2) | N               |
| currency                              | varchar(3)    | N               |
| responsible_service                   | varchar(80)   | N               |
| remote_action_id                      | uuid          | N               |
| economic_destination                  | varchar(30)   | N               |
| result_reference                      | jsonb         | S               |
| status                                | varchar(30)   | N               |
| T, C, S                               | Bloques       |                 |

FK a porción del control de restitución.

UQ (responsible_service, remote_action_id).

Modalidades PROVISIONAL_RESTITUTION, DEFINITIVE_RESTITUTION,  
REGULARIZE_PROVISIONAL y AUTHORIZED_RECOVERY.

Estados PENDING, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

La reparación al cliente se separa del recupero externo.

Una resolución desfavorable no genera intereses retroactivos del período protegido.

Los conceptos nuevamente exigibles pasan a un resumen posterior,  
con entrega y vencimiento propios, conservando identidad y protección de otras causas.

## **29. Red: participantes, intercambios y mensajes**

### **29.1. card_network_participant**

Columnas N:

- participant_id uuid, PK.

- participant_code varchar(80), UQ.

- party_reference jsonb.

- contract_reference jsonb.

- Bloques T y C.

### **29.2. card_network_mechanism_version**

| **Columna**                             | **Tipo**    | **Nulabilidad** |
|-----------------------------------------|-------------|-----------------|
| participant_id                          | uuid        | N               |
| mechanism_code                          | varchar(80) | N               |
| mechanism_version                       | bigint      | N               |
| operations, currencies, message_schemas | jsonb       | N               |
| adapter_capabilities, recovery_rules    | jsonb       | N               |
| clearing_rules                          | jsonb       | N               |
| partial_settlement_allowed              | boolean     | N               |
| valid_from                              | timestamptz | N               |
| valid_until                             | timestamptz | S               |
| status                                  | varchar(20) | N               |
| approval_reference                      | jsonb       | N               |
| T, S                                    | Bloques     |                 |

PK sobre participante, mecanismo y versión.  
FK al participante.

Estados ENABLED, SUSPENDED y RETIRED.

Los adaptadores y operaciones son registrados.  
Suspender no impide recuperar pendientes.

### **29.3. card_network_exchange**

| **Columna**                              | **Tipo**     | **Nulabilidad** |
|------------------------------------------|--------------|-----------------|
| exchange_id, participant_id              | uuid         | N               |
| mechanism_code                           | varchar(80)  | N               |
| mechanism_version                        | bigint       | N               |
| external_operation_id, request_reference | varchar(120) | N               |
| message_type                             | varchar(80)  | N               |
| typed_payload, operation_references      | jsonb        | N               |
| status                                   | varchar(20)  | N               |
| adapter_action_id                        | uuid         | N               |
| response_reference                       | jsonb        | S               |
| T, C, S                                  | Bloques      |                 |

PK exchange_id, FK a mecanismo versionado.

UQ (participant_id, mechanism_code, request_reference).

Estados PENDING, SENT, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

La identidad económica se conserva por participante, mecanismo y operación,  
sin incluir la versión del esquema.

### **29.4. card_network_message**

| **Columna**                                | **Tipo**     | **Nulabilidad** |
|--------------------------------------------|--------------|-----------------|
| message_id, participant_id                 | uuid         | N               |
| mechanism_code                             | varchar(80)  | N               |
| external_message_id, external_operation_id | varchar(120) | N               |
| mechanism_version, message_schema_version  | bigint       | N               |
| message_type                               | varchar(80)  | N               |
| original_payload, evidence_references      | jsonb        | N               |
| message_hash                               | char(64)     | N               |
| occurred_at, received_at                   | timestamptz  | N               |
| incorporation_status                       | varchar(20)  | N               |
| T                                          | Bloque       |                 |

PK message_id.

FK a mecanismo versionado.

UQ (participant_id, mechanism_code, external_message_id).

Estados INCORPORATED, UNMATCHED y CONTRADICTORY.

### **29.5. card_network_message_conflict**

Columnas N:

- conflict_id uuid, PK.

- message_id uuid, FK.

- conflicting_hash char(64).

- conflicting_payload jsonb.

- evidence_references jsonb.

- Bloque T.

UQ (message_id, conflicting_hash).

El original no se sobrescribe.

## **30. Conciliación y correcciones de red**

### **30.1. card_network_reconciliation**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| reconciliation_id                  | uuid        | N, PK           |
| scope_code                         | varchar(80) | N               |
| source_references, comparable_cuts | jsonb       | N               |
| status                             | varchar(20) | N               |
| started_at                         | timestamptz | N               |
| completed_at                       | timestamptz | S               |
| T, C                               | Bloques     |                 |

Estados RUNNING, MATCHED, DIFFERENCES y FAILED.

### **30.2. card_network_difference**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| difference_id, reconciliation_id       | uuid        | N               |
| classification                         | varchar(30) | N               |
| compared_references, comparison_detail | jsonb       | N               |
| status                                 | varchar(20) | N               |
| T, C                                   | Bloques     |                 |

PK difference_id, FK a conciliación.

Clasificaciones MISSING_RECORD, DUPLICATE, AMOUNT_MISMATCH, CURRENCY_MISMATCH,  
STATE_CONTRADICTION, TEMPORAL_MISMATCH y SETTLEMENT_PENDING.

Estados OPEN, UNDER_REVIEW, RESOLUTION_PENDING y RESOLVED.

### **30.3. card_network_difference_resolution**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| resolution_id, difference_id                | uuid        | N               |
| conclusion, treatment_code                  | varchar(80) | N               |
| manual_adjustment                           | boolean     | N               |
| preparer_person_id                          | uuid        | N               |
| approver_person_id                          | uuid        | S               |
| approval_reference                          | jsonb       | S               |
| evidence_references, application_references | jsonb       | N               |
| T, S                                        | Bloques     |                 |

PK resolution_id, FK a diferencia.

Un ajuste manual exige aprobación y personas distintas.

### **30.4. card_network_correction**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| correction_id                     | uuid        | N, PK           |
| original_network_action_reference | jsonb       | N               |
| treatment, evidence_references    | jsonb       | N               |
| approval_reference                | jsonb       | S               |
| economic_effect_references        | jsonb       | N               |
| status                            | varchar(20) | N               |
| T, C, S                           | Bloques     |                 |

Estados PENDING, APPLIED, UNCERTAIN y REQUIRES_REVIEW.

La conciliación no modifica por sí misma dinero ni deuda.

## **31. Obligaciones de red**

### **31.1. card_network_clearing_fact**

| **Columna**                              | **Tipo**      | **Nulabilidad** |
|------------------------------------------|---------------|-----------------|
| fact_id, participant_id                  | uuid          | N               |
| source_service                           | varchar(80)   | N               |
| economic_fact_id                         | uuid          | N               |
| fact_type, mechanism_code                | varchar(80)   | N               |
| mechanism_version                        | bigint        | N               |
| currency                                 | varchar(3)    | N               |
| direction                                | varchar(10)   | N               |
| amount                                   | numeric(24,2) | N               |
| occurred_at                              | timestamptz   | N               |
| operation_reference, evidence_references | jsonb         | N               |
| original_fact_id                         | uuid          | S               |
| T, S                                     | Bloques       |                 |

PK fact_id.

FK a mecanismo y hecho original cuando sea ajuste.

UQ (source_service, fact_type, economic_fact_id).

Sentidos PAY y RECEIVE.

Las autorizaciones no se incluyen como consumos.

### **31.2. card_network_clearing_cycle**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| cycle_id, participant_id   | uuid        | N               |
| mechanism_code             | varchar(80) | N               |
| mechanism_version          | bigint      | N               |
| currency                   | varchar(3)  | N               |
| interval_from, interval_to | timestamptz | N               |
| rules_reference            | jsonb       | N               |
| status                     | varchar(20) | N               |
| closed_at                  | timestamptz | S               |
| T, C                       | Bloques     |                 |

PK cycle_id, FK a mecanismo.

Estados OPEN, DETERMINING y CLOSED.

Exclusión de intervalos superpuestos por participante, mecanismo y moneda.

Corte a las 18:00.  
Inicio incluido y final excluido.

### **31.3. card_network_cycle_fact**

Columnas N cycle_id uuid y fact_id uuid.

PK sobre ambas, FK a ciclo y hecho, y UQ sobre fact_id.

### **31.4. card_network_determination**

| **Columna**                 | **Tipo**    | **Nulabilidad** |
|-----------------------------|-------------|-----------------|
| determination_id, cycle_id  | uuid        | N               |
| mode                        | varchar(10) | N               |
| netting_agreement_reference | jsonb       | S               |
| composition_hash            | char(64)    | N               |
| zero_net                    | boolean     | N               |
| T, S                        | Bloques     |                 |

PK determination_id, FK a ciclo y UQ sobre ciclo.

Modos GROSS y NET.  
NET exige acuerdo expreso, misma contraparte y moneda.

El neto cero se conserva sin instrucción monetaria cero.

### **31.5. card_network_obligation**

| **Columna**                               | **Tipo**      | **Nulabilidad** |
|-------------------------------------------|---------------|-----------------|
| obligation_id, determination_id, cycle_id | uuid          | N               |
| participant_id                            | uuid          | N               |
| direction                                 | varchar(10)   | N               |
| amount                                    | numeric(24,2) | N               |
| currency                                  | varchar(3)    | N               |
| due_at                                    | timestamptz   | N               |
| gross_fact_id                             | uuid          | S               |
| fulfilled_amount, remaining_amount        | numeric(24,2) | N               |
| treasury_action_id                        | uuid          | N               |
| status                                    | varchar(30)   | N               |
| T, C                                      | Bloques       |                 |

PK obligation_id.

FK a determinación, ciclo, participante y hecho bruto cuando corresponda.

UQ sobre treasury_action_id.

Las restricciones únicas parciales son:

| **Nombre**                       | **Columnas**                    | **Condición**             | **Finalidad**                                              |
|----------------------------------|---------------------------------|---------------------------|------------------------------------------------------------|
| uq_card_network_obligation_net   | determination_id                | gross_fact_id IS NULL     | Una obligación neta por determinación.                     |
| uq_card_network_obligation_gross | determination_id, gross_fact_id | gross_fact_id IS NOT NULL | Una obligación bruta por hecho dentro de la determinación. |

La modalidad se obtiene de card_network_determination.mode.

Para NET, gross_fact_id está ausente.  
Para GROSS, gross_fact_id está presente.

La correspondencia entre modalidad y presencia del hecho se comprueba bajo bloqueo de la determinación.  
El mismo control impide incorporar obligaciones de modalidades distintas en una determinación.

Los predicados de los índices utilizan exclusivamente columnas de card_network_obligation.  
La coherencia con la modalidad de la determinación se protege mediante validación transaccional.

La determinación pertenece al ciclo indicado.  
El participante y la moneda coinciden con el ciclo.

En GROSS, el hecho pertenece al mismo ciclo y coincide con participante y moneda.  
El importe y sentido de la obligación corresponden a ese hecho.

En NET, los componentes pertenecen al ciclo de la determinación.  
Su diferencia determina el importe y sentido de la obligación.  
Un neto cero no genera una obligación monetaria de importe cero.

Importe = cumplido + remanente.

El importe de una obligación monetaria es positivo.  
Cumplido y remanente son no negativos.

Estados DETERMINED, SETTLEMENT_PENDING, PARTIALLY_SETTLED, SETTLED y REQUIRES_REVIEW.

Las claves de determinación, ciclo y hecho bruto utilizadas para identificar la obligación  
no cambian durante la liquidación ni sus reintentos.

La determinación, las obligaciones y sus componentes se confirman en una misma transacción.  
La recuperación utiliza las mismas identidades y treasury_action_id.

### **31.6. card_network_obligation_component**

Columnas N:

- obligation_id uuid.

- fact_id uuid.

- direction varchar(10).

- amount numeric(24,2).

PK sobre obligación y hecho.  
FK a ambos y UQ sobre hecho.

La composición bruta o neta determina importe y sentido.

### **31.7. card_network_settlement_result**

| **Columna**                | **Tipo**      | **Nulabilidad** |
|----------------------------|---------------|-----------------|
| result_id, obligation_id   | uuid          | N               |
| treasury_service           | varchar(80)   | N               |
| treasury_result_id         | uuid          | N               |
| treasury_result_version    | bigint        | N               |
| settlement_execution_id    | uuid          | N               |
| result                     | varchar(10)   | N               |
| confirmed_increment_amount | numeric(24,2) | N               |
| currency                   | varchar(3)    | N               |
| occurred_at                | timestamptz   | N               |
| evidence_references        | jsonb         | N               |
| T, S                       | Bloques       |                 |

PK result_id, FK a obligación.

UQ (treasury_service, treasury_result_id, treasury_result_version).

Resultados CONFIRMED, PARTIAL, FAILED y UNCERTAIN.

FAILED y UNCERTAIN aportan incremento confirmado cero.  
Las revisiones de una ejecución no vuelven a sumar lo confirmado.

PARTIAL exige habilitación del mecanismo.

Una falla externa no vuelve impago un pago del cliente ya aplicado.

## **32. Correcciones y cierre de cuenta**

### **32.1. card_account_correction**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| correction_id, card_account_id              | uuid        | N               |
| original_fact_reference                     | jsonb       | N               |
| effective_date                              | date        | N               |
| treatment_by_component, evidence_references | jsonb       | N               |
| approval_reference                          | jsonb       | S               |
| result_references                           | jsonb       | N               |
| status                                      | varchar(20) | N               |
| T, C, S                                     | Bloques     |                 |

PK correction_id, FK a cuenta.

Distingue DEBT, PAYMENT_ALLOCATION, CAPACITY, INTEREST, DELINQUENCY y DOCUMENTS  
dentro de los tratamientos.

Estados PENDING, APPLIED, UNCERTAIN y REQUIRES_REVIEW.

Redistribuir un pago no lo cobra nuevamente.

### **32.2. card_account_closure**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| closure_id, card_account_id       | uuid        | N               |
| contractual_termination_reference | jsonb       | N               |
| reason_code                       | varchar(80) | N               |
| received_at                       | timestamptz | N               |
| uses_ceased_at, closed_at         | timestamptz | S               |
| instrument_treatment              | jsonb       | N               |
| status                            | varchar(20) | N               |
| residual_references               | jsonb       | N               |
| T, C                              | Bloques     |                 |

PK closure_id, FK a cuenta.

Estados REQUESTED, PROCESSING, RESIDUAL y CLOSED.

UQ parcial sobre cuenta mientras no sea CLOSED.

Los componentes incluyen instrumentos, intentos, compromisos, presentaciones, deuda,  
cuotas, disputas, devoluciones, saldo a favor, exposición y documentos.

El cierre no acelera cuotas ni libera cobertura incierta.

## **33. Índices complementarios**

Además de las claves e índices indicados:

| **Tabla**                         | **Índice**                                                          |
|-----------------------------------|---------------------------------------------------------------------|
| card_configuration_component      | (configuration_id, status, step_id)                                 |
| card_credit_limit                 | (card_account_id, currency, limit_id)                               |
| card_commitment_limit_allocation  | (limit_id, commitment_id)                                           |
| card_operational_limit            | (scope_identity_hash, limit_id)                                     |
| card_authorization_attempt_detail | (authorization_id, attempt_id)                                      |
| card_consumption                  | (card_account_id, confirmed_at, consumption_id)                     |
| card_debt_component               | (card_account_id, currency, status, component_id)                   |
| card_capital_classification       | (portion_id, effective_from, classification_id)                     |
| card_statement                    | (card_account_id, cycle_id, statement_id)                           |
| card_statement_obligation         | (current_due_date, obligation_id)                                   |
| card_payment_preparation          | (card_account_id, status, preparation_id)                           |
| card_payment_application          | (receipt_id, application_id)                                        |
| card_dispute_deadline             | (due_at, dispute_id, deadline_id)                                   |
| card_protection_allocation        | (component_id, status, protection_id)                               |
| card_network_exchange             | (status, updated_at, exchange_id)                                   |
| card_network_message              | (participant_id, mechanism_code, external_operation_id, message_id) |
| card_network_difference           | (status, updated_at, difference_id)                                 |
| card_network_obligation           | (status, due_at, obligation_id)                                     |
| card_account_closure              | (status, updated_at, closure_id)                                    |

Las FK compuestas incorporan UQ sobre las columnas referenciadas e índices inversos  
cuando la búsqueda no queda cubierta por PK o índices existentes.

## **34. Transacciones y bloqueos**

### **34.1. Emisión**

Instrumento → control de uso → configuración o actuación → componentes y preparaciones.

Cuando intervienen varios instrumentos, se ordenan por identificador.

### **34.2. Autorizaciones**

Autorización → identidad de ejecución → intento → controles de alcance ordenados  
→ límites → períodos → afectaciones.

La modificación del catálogo de límites utiliza los mismos controles de alcance  
para impedir omitir un límite agregado concurrentemente.

### **34.3. Cuenta de tarjeta**

Sin ingreso ni intento:

Cuenta → posiciones por moneda → límites ordenados → compromisos → componentes y porciones.

Con ingreso:

Control de ingreso → cuentas ordenadas → posiciones → preparaciones → componentes y porciones.

Con intento de incorporación:

Identidad de ejecución → intento → cuenta → posiciones → límites → compromiso → capital.

Ningún proceso adquiere un ingreso o intento después de bloquear su cuenta.

Los trabajos de cierre persisten solicitudes de resolución y consultan intentos fuera de la transacción.

### **34.4. Procesamiento**

Para restituciones:

Control del efecto original → asignación → devolución o acción económica → ejecución.

Para compensación:

Ciclo → hechos ordenados → determinación → obligaciones.

La liquidación bloquea obligación y resultados.

### **34.5. Disputas**

Disputa → resolución o reconsideración → plazos → coordinaciones.

La protección efectiva se aplica en Cuenta de tarjeta bajo sus propios controles.

No se mantienen transacciones SQL durante llamadas HTTP, publicación o acceso documental.

## **35. Eventos y recuperación**

| **Contrato** | **Tipo**                               | **Tema**                          |
|--------------|----------------------------------------|-----------------------------------|
| CI-07-042    | CardInstrumentChanged                  | banking.card-issuance.events      |
| CI-07-043    | CardAuthorizationChanged               | banking.card-authorization.events |
| CI-07-044    | CardPresentationOrNetworkEffectChanged | banking.card-processing.events    |
| CI-07-045    | CardCapacityOrDebtChanged              | banking.card-account.events       |
| CI-07-046    | CardStatementOrPaymentChanged          | banking.card-account.events       |
| CI-07-047    | CardDisputeChanged                     | banking.card-dispute.events       |
| CI-07-048    | CardNetworkObligationChanged           | banking.card-processing.events    |

La versión inicial del esquema es 1.  
La clave es la identidad del agregado definida por el contrato.

La constitución multimoneda publica eventos distintos por posición,  
con identidades de evento independientes.

Los cambios se confirman junto con historia, actuación y outbox.

Los trabajos conservan identidades de:

- Configuración y componente.

- Preparación e intento.

- Autorización y revisión de vigencia.

- Presentación y ejecución.

- Control económico y restitución.

- Cuenta y ciclo.

- Resumen y versión.

- Obligación y coordinación de débito.

- Disputa, plazo y reparación.

- Ciclo de red y obligación.

- Cierre y componente.

La expiración de un trabajador no libera recursos ni demuestra ausencia de ejecución.

## **36. Migraciones, seguridad y conservación**

Las migraciones utilizan Flyway por servicio.

Las referencias circulares se implementan como diferibles.  
Las exclusiones temporales utilizan el soporte PostgreSQL correspondiente.

Se preservan identidades económicas, períodos originales, versiones emitidas,  
secuencias y referencias de recuperación.

No se eliminan mediante cascadas autorizaciones, presentaciones, consumos, pagos,  
restituciones, resúmenes, disputas o composiciones utilizadas.

Las credenciales se mantienen fuera de eventos y consultas generales.

Redis no conserva la única copia de capacidad, compromisos, deuda, decisiones, resúmenes o protecciones.

Las consultas históricas y recuperaciones mantienen autorización y finalidad.

Después de restaurar un respaldo se pausan instrucciones externas y se reconcilian  
autorizaciones, cobros, devoluciones y liquidaciones antes de reanudar.

## **37. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                                        |
|-----------------------|-------------------------------------------------------------------------|
| CI-07-001             | Cuenta, responsables, posiciones y constitución.                        |
| CI-07-002 a CI-07-006 | Instrumentos, credenciales, entrega, activación, configuración y ciclo. |
| CI-07-007             | Límites crediticios, cambios y protección.                              |
| CI-07-008 y CI-07-009 | Autorizaciones, intentos, límites operativos y cobertura.               |
| CI-07-010 y CI-07-012 | Compromisos crediticios y afectación de límites.                        |
| CI-07-011 y CI-07-013 | Ajustes, presentación, revisión de vencimiento y consulta.              |
| CI-07-014 y CI-07-015 | Presentaciones y ejecución recuperable.                                 |
| CI-07-016             | Consumos y transformación de compromiso.                                |
| CI-07-017 y CI-07-018 | Cálculos, planes, cronogramas y CFT.                                    |
| CI-07-019 y CI-07-020 | Ajustes, restituciones y devoluciones.                                  |
| CI-07-021             | Actuaciones de red.                                                     |
| CI-07-022 a CI-07-024 | Ciclos, resúmenes, entrega, vencimiento y posición.                     |
| CI-07-025 y CI-07-026 | Preparaciones, ingresos e imputaciones.                                 |
| CI-07-027             | Débito automático y ocurrencia vinculada.                               |
| CI-07-028             | Financiación rotativa y mora.                                           |
| CI-07-029             | Pago con conversión identificada.                                       |
| CI-07-030 a CI-07-034 | Disputas, plazos, protección, resolución y reparación.                  |
| CI-07-035 a CI-07-038 | Red, conciliación, obligaciones y correcciones.                         |
| CI-07-039 a CI-07-041 | Correcciones de cuenta, cierre y actuaciones.                           |
| CI-07-042 a CI-07-048 | Outbox e historia de agregados.                                         |

## **38. Escenarios de verificación de implementación**

| **Escenario**                                              | **Resultado exigido**                                        |
|------------------------------------------------------------|--------------------------------------------------------------|
| Emisión repetida                                           | Recupera el mismo instrumento.                               |
| Entrega sin activación                                     | No habilita el uso.                                          |
| Adicional                                                  | Comparte capacidad general, con sus sublímites.              |
| Credencial sustituida                                      | Impide nuevos usos de la anterior.                           |
| Configuración incompleta                                   | No se utiliza como aplicada íntegramente.                    |
| General 1000 y sublímite 200                               | No aprueba compra de 250.                                    |
| Adicionales concurrentes                                   | No duplican capacidad disponible.                            |
| Compra en cuotas                                           | Compromete todo el capital.                                  |
| Cobertura incierta                                         | Mantiene decisión pendiente.                                 |
| Aprobación y aborto concurrentes                           | Conservan una decisión durable.                              |
| Presentación al día siguiente                              | Transforma el período original.                              |
| Cambio de límite durante el período                        | No reinicia uso acumulado.                                   |
| Autorización vencida                                       | Investiga antes de liberar.                                  |
| Autorización 100 y presentación final 60                   | Ejecuta 60 y resuelve 40.                                    |
| Extracción ya debitada                                     | No vuelve a debitar.                                         |
| Presentación excepcional                                   | Exige resolución fundada.                                    |
| Plan sobre capital existente                               | No crea otra compra ni capital.                              |
| Devolución y disputa                                       | Comparten control recuperable.                               |
| Provisional 100 y definitivo 100                           | Regulariza sin acreditar 200.                                |
| Confirmación posterior al corte                            | Integra ciclo siguiente.                                     |
| Rectificación de resumen                                   | Conserva obligación estable.                                 |
| Entrega tardía                                             | Ajusta vencimiento y consecuencias.                          |
| Mínimo anterior impago                                     | K permanece dentro de P.                                     |
| Pago total                                                 | No precancela cuotas futuras.                                |
| Débito insuficiente                                        | No parcializa ni reintenta ocultamente.                      |
| Plan y rotativo                                            | No devengan sobre el mismo capital y período.                |
| Disputa protegida                                          | Suspende exigibilidad e intereses alcanzados.                |
| Resolución desfavorable                                    | No genera intereses retroactivos protegidos.                 |
| Reincorporación                                            | Utiliza resumen posterior con entrega y vencimiento propios. |
| Otra protección vigente                                    | Mantiene el impedimento correspondiente.                     |
| Hecho de red repetido                                      | Se incluye una sola vez.                                     |
| Dos obligaciones NET de la misma determinación             | La unicidad parcial impide duplicarlas.                      |
| Dos obligaciones GROSS para el mismo hecho y determinación | La unicidad parcial impide duplicarlas.                      |
| Hecho bruto presente en una determinación NET              | Rechaza la incorporación por incoherencia de modalidad.      |
| Hecho bruto ausente en una determinación GROSS             | Rechaza la incorporación por falta de identidad del hecho.   |
| Determinación NET con resultado cero                       | Conserva composición sin instrucción monetaria cero.         |
| Liquidación externa fallida                                | No deshace pagos del cliente.                                |
| Cierre con cuotas y disputas                               | Conserva régimen residual.                                   |
| Restauración o evento duplicado                            | Recupera efectos originales sin repetirlos.                  |

Las restricciones, exclusiones, conservación de capital, contadores y transacciones  
se verifican durante la implementación con PostgreSQL real y escenarios concurrentes.
