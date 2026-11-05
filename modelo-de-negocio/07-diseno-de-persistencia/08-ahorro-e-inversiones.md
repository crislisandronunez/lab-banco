# **Diseño de persistencia — Módulo 8. Ahorro e inversiones**

## **1. Propósito y alcance**

Este documento define la persistencia de los servicios del módulo Ahorro e inversiones.

| **Servicio**              | **Responsabilidad**                                                                     |
|---------------------------|-----------------------------------------------------------------------------------------|
| Depósitos a plazo         | Preparación, constitución, rendimiento, vencimiento, renovación y precancelación.       |
| Instrumentos de inversión | Definiciones, clases, habilitaciones, valores de referencia y eventos económicos.       |
| Órdenes de inversión      | Instrucciones, recursos, ejecución, cancelación y coordinación de liquidaciones.        |
| Tenencias y valuación     | Cuentas de inversión, unidades, compromisos, custodia, derechos, valuación y traslados. |

Comprende los contratos CI-08-001 a CI-08-047 y las preparaciones de facultades de la cuenta de inversión.

Cada servicio mantiene su base PostgreSQL, usuario y migraciones. No existen FK, consultas SQL ni transacciones entre
bases.

Un depósito representa una obligación del banco. Una inversión representa unidades o derechos cuyo valor puede variar.

Fondos conserva la autoridad sobre el dinero de clientes. Las inversiones propias del banco pertenecen a Tesorería.

Orden aceptada, ejecución, liquidación y disponibilidad son hechos diferentes.

## **2. Convenciones físicas**

### **2.1. Notación**

- **N:** columna obligatoria.

- **S:** columna que admite NULL.

- **PK:** clave primaria.

- **UQ:** restricción única.

- **FK:** clave foránea local.

Cuando una fila agrupa nombres de columnas, cada columna tiene individualmente el tipo y nulabilidad indicados.

Las columnas condicionales se declaran S y se acompañan de restricciones de presencia.

Las FK utilizan eliminación restringida. Las referencias remotas no generan FK.

Los índices se expresan con sus columnas en orden. PK, UQ y exclusiones generan sus índices correspondientes.

### **2.2. Tipos**

| **Dato**                | **PostgreSQL** | **Restricción**                             |
|-------------------------|----------------|---------------------------------------------|
| Identificador           | uuid           | Estable y no reutilizable.                  |
| Versión                 | bigint         | Positiva.                                   |
| Control de concurrencia | bigint         | No negativo.                                |
| Contador transaccional  | bigint         | No negativo; empieza en cero.               |
| Secuencia asignada      | bigint         | Positiva.                                   |
| Código                  | varchar(80)    | No vacío y registrado.                      |
| Referencia externa      | varchar(120)   | No vacía.                                   |
| Nombre                  | varchar(200)   | No vacío.                                   |
| Fundamento              | varchar(4000)  | No vacío cuando está presente.              |
| Moneda                  | varchar(3)     | ARS o USD.                                  |
| Importe monetario       | numeric(24,2)  | No negativo.                                |
| Variación monetaria     | numeric(24,2)  | Admite signo cuando se indica.              |
| Cantidad                | numeric(30,12) | No negativa.                                |
| Precio o índice         | numeric(30,12) | Positivo, salvo conceptos que admiten cero. |
| Valor de cálculo        | numeric(30,12) | Conforme a su significado.                  |
| Tasa                    | numeric(24,12) | Conforme a su definición.                   |
| Hash                    | char(64)       | SHA-256 hexadecimal en minúsculas.          |
| Instante                | timestamptz    | Intercambio en UTC.                         |
| Fecha                   | date           | Fecha bancaria.                             |
| Contenido               | jsonb          | Esquema registrado y versionado.            |

Importes y cantidades ejecutables son positivos.

La precisión y escala se validan antes de persistir. Los resultados monetarios utilizan HALF_UP.

Las cantidades negociables respetan precisión, mínimo e incremento del instrumento. Los movimientos derivados de eventos
utilizan las reglas específicas del evento.

Cuando se determina cantidad a partir de un máximo monetario, se trunca al incremento permitido para no superar lo
autorizado.

Las unidades UVA son datos de cálculo del depósito. No se truncan como cantidades negociables.

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

Tenencias y valuación incorpora authority_preparation.

Los intentos monetarios y las preparaciones de otras autoridades se conservan mediante referencias a sus propietarios.

## **3. Esquemas estructurados**

Cada JSONB conserva su versión mediante columna explícita o envoltorio con schemaCode, schemaVersion y data.

| **Esquema**                | **Contenido**                                              |
|----------------------------|------------------------------------------------------------|
| RemoteReference            | Propietario, tipo, identidad y versión cuando corresponde. |
| EvidenceReferences         | Hechos y evidencias identificados.                         |
| PendingComponents          | Componente, responsable, motivo y seguimiento.             |
| Ownership                  | Titulares, modalidad, operadores y fundamento.             |
| MonetaryDestination        | Destino interno o externo, moneda y autorización.          |
| RateDefinition             | Valor, clase, período y base temporal.                     |
| DepositIndexDefinition     | Serie, fuente y reglas de fechas.                          |
| DepositCalculationInputs   | Condiciones, período, índices y fuentes.                   |
| InstrumentDefinition       | Operaciones, riesgo, calendarios, liquidación y traslado.  |
| InvestmentOrderInstruction | Variante completa de orden y condiciones aceptadas.        |
| SettlementCondition        | Condiciones comprobables de liquidación y disponibilidad.  |
| CalendarCalculation        | Calendario, entradas, regla y resultado.                   |
| PositionSnapshot           | Cantidades, recursos, restricciones y tránsito.            |
| EntitlementRule            | Regla de derecho, beneficiario, tributos y residuos.       |
| CustodyTransferOwnership   | Titularidad completa comprobada en origen y destino.       |

No se admiten scripts, SQL, URL arbitrarias ni campos desconocidos.

Los atributos que intervienen en identidad, unicidad, bloqueo o recuperación se almacenan en columnas explícitas.

## **4. Coordinaciones remotas**

Las siguientes tablas utilizan una estructura física común:

| **Tabla**                            | **Propietario N**   | **FK local**               |
|--------------------------------------|---------------------|----------------------------|
| term_deposit_constitution_component  | deposit_id uuid     | term_deposit               |
| term_deposit_payment_step            | payment_id uuid     | term_deposit_payment       |
| investment_order_resource            | order_id uuid       | investment_order           |
| investment_settlement_step           | settlement_id uuid  | investment_settlement      |
| investment_entitlement_step          | entitlement_id uuid | investment_entitlement     |
| custody_transfer_step                | transfer_id uuid    | custody_transfer           |
| investment_account_closure_component | closure_id uuid     | investment_account_closure |

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

- Índice parcial sobre efecto cuando exista.

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

La intención se confirma antes de llamar al propietario. La recuperación conserva actuación, efecto y contenido
originales.

## **5. Titularidad y horarios**

### **5.1. Titularidad**

Los titulares se identifican sin duplicados.

La modalidad es INDIVIDUAL o JOINT. JOINT exige regla de actuación conjunta.

Los operadores autorizados no adquieren propiedad.

La titularidad de una inversión no se deduce de la cuenta monetaria utilizada.

La modificación de titulares conserva formalización y tratamiento explícito de posiciones existentes.

### **5.2. Horarios**

| **Actuación**             | **Ventana**                                                |
|---------------------------|------------------------------------------------------------|
| Constitución de depósitos | Hábiles, desde las 09:00 hasta antes de las 17:00.         |
| Recepción de órdenes      | Todos los días.                                            |
| Corte de fondos comunes   | 15:00 de días hábiles.                                     |
| Negociación de títulos    | Días de mercado, desde las 11:00 hasta antes de las 17:00. |
| Liquidación ordinaria     | Siguiente hábil aplicable.                                 |
| Recepción de traslados    | Todos los días.                                            |
| Gestión de traslados      | Hábiles, desde las 09:00 hasta antes de las 17:00.         |

La zona institucional es America/Argentina/Buenos_Aires.

Fuera de horario se conserva recepción y fecha de procesamiento. No se informa ejecución futura como realizada ni se
reutilizan condiciones vencidas.

## **6. Preparaciones de depósitos**

### **6.1. term_deposit_preparation**

| **Columna**                                          | **Tipo**      | **Nulabilidad** |
|------------------------------------------------------|---------------|-----------------|
| preparation_id                                       | uuid          | N, PK           |
| product_service                                      | varchar(80)   | N               |
| product_id                                           | uuid          | N               |
| product_version                                      | bigint        | N               |
| modality                                             | varchar(30)   | N               |
| holder_category                                      | varchar(20)   | N               |
| ownership_mode                                       | varchar(20)   | N               |
| joint_rule                                           | jsonb         | S               |
| capital_amount                                       | numeric(24,2) | N               |
| currency                                             | varchar(3)    | N               |
| term_days                                            | integer       | N               |
| funding_account_reference, maturity_destination      | jsonb         | N               |
| planned_constitution_date, contractual_maturity_date | date          | N               |
| scheduled_payment_date                               | date          | N               |
| rate_definition                                      | jsonb         | N               |
| early_exit_rate_definition, index_definition         | jsonb         | S               |
| initial_maturity_instruction                         | varchar(40)   | N               |
| valid_until                                          | timestamptz   | N               |
| acceptance_evidence                                  | jsonb         | S               |
| T, C, S                                              | Bloques       |                 |

Capital y plazo positivos.

Categoría NATURAL_PERSON o LEGAL_ENTITY, acreditada mediante las fuentes de titularidad.

Las condiciones aceptadas son inmutables. Un cambio material genera otra preparación.

### **6.2. term_deposit_preparation_holder**

| **Columna**              | **Tipo**    | **Nulabilidad** |
|--------------------------|-------------|-----------------|
| preparation_id, party_id | uuid        | N               |
| party_type               | varchar(20) | N               |
| ownership_basis          | jsonb       | N               |

PK sobre preparación y persona. FK a preparación.

Se exige al menos un titular.

### **6.3. Modalidades**

| **Modalidad**      | **Moneda y titular**  | **Plazo mínimo**  | **Salida anticipada** |
|--------------------|-----------------------|-------------------|-----------------------|
| TRADITIONAL        | ARS o USD             | 30 días corridos  | No.                   |
| NOMINAL_EARLY_EXIT | ARS                   | 180 días corridos | Desde el día 30.      |
| UVA                | ARS                   | 90 días corridos  | No.                   |
| UVA_EARLY_EXIT     | Persona humana, ARS   | 90 días corridos  | Desde el día 30.      |
| UVA_EARLY_EXIT     | Persona jurídica, ARS | 180 días corridos | Desde el día 90.      |

La salida anticipada es total y exige cinco días hábiles de preaviso.

Los depósitos son nominativos, intransferibles y no se fraccionan.

Esta preparación comercial no reserva dinero ni constituye preparación de autoridad.

## **7. Constitución del depósito**

### **7.1. term_deposit**

| **Columna**                                       | **Tipo**       | **Nulabilidad** |
|---------------------------------------------------|----------------|-----------------|
| deposit_id, constitution_id                       | uuid           | N               |
| contract_reference                                | jsonb          | N               |
| preparation_id                                    | uuid           | N               |
| accepted_preparation_version                      | bigint         | N               |
| accepted_content_hash                             | char(64)       | N               |
| accepted_ownership                                | jsonb          | N               |
| original_capital                                  | numeric(24,2)  | N               |
| currency                                          | varchar(3)     | N               |
| modality                                          | varchar(30)    | N               |
| funding_account_reference, payment_destination    | jsonb          | N               |
| constituted_at                                    | timestamptz    | S               |
| contractual_maturity_date, scheduled_payment_date | date           | N               |
| contractual_rate, early_exit_rate                 | jsonb          | N/S             |
| original_index_observation_id                     | uuid           | S               |
| original_index_units                              | numeric(30,12) | S               |
| previous_deposit_id                               | uuid           | S               |
| status                                            | varchar(30)    | N               |
| T, C                                              | Bloques        |                 |

PK deposit_id.

contractual_rate es N; early_exit_rate es S.

FK a preparación, índice inicial y depósito anterior.

UQ sobre constitution_id y sobre preparation_id.

Estados CONSTITUTING, ACTIVE, MATURITY_PROCESSING, EARLY_EXIT_PROCESSING, PAYMENT_PENDING, RENEWED, PAID y
REQUIRES_RESOLUTION.

UVA exige índice inicial y unidades originales conjuntamente. Las modalidades nominales no los contienen.

Índices:

- (status, scheduled_payment_date, deposit_id).

- (previous_deposit_id, deposit_id), donde exista anterior.

### **7.2. Reconocimiento**

La constitución utiliza exclusivamente fondos propios.

La intención monetaria se confirma antes del débito.

Al acreditar el débito se registran conjuntamente:

- Evidencia monetaria.

- Constitución efectiva.

- Capital y obligación.

- Condiciones definitivas.

- Historia.

- Resultado de actuación.

- Outbox.

Un débito confirmado con constitución pendiente exige completar o restituir con las identidades originales.

La falta de constancia documental no elimina la obligación.

## **8. Cálculos e índices de depósitos**

### **8.1. term_deposit_index_observation**

| **Columna**                 | **Tipo**       | **Nulabilidad** |
|-----------------------------|----------------|-----------------|
| observation_id              | uuid           | N, PK           |
| series_code, source_service | varchar(80)    | N               |
| source_observation_id       | uuid           | N               |
| source_version              | bigint         | N               |
| applicable_date             | date           | N               |
| value                       | numeric(30,12) | N               |
| replaced_observation_id     | uuid           | S               |
| evidence_references         | jsonb          | N               |
| T, S                        | Bloques        |                 |

Valor positivo.

FK a observación sustituida.

UQ (source_service, source_observation_id, source_version).

Índice (series_code, applicable_date, observation_id).

Las observaciones utilizadas no se sobrescriben.

### **8.2. term_deposit_calculation**

| **Columna**                                              | **Tipo**       | **Nulabilidad** |
|----------------------------------------------------------|----------------|-----------------|
| calculation_id                                           | uuid           | N               |
| calculation_version                                      | bigint         | N               |
| preparation_id, deposit_id                               | uuid           | S               |
| purpose_code                                             | varchar(80)    | N               |
| calculated_until                                         | date           | N               |
| days_counted                                             | integer        | N               |
| original_capital, index_adjustment                       | numeric(24,2)  | N               |
| interest_amount, tax_amount                              | numeric(24,2)  | N               |
| compensation_amount, net_amount                          | numeric(24,2)  | N               |
| initial_index_observation_id, final_index_observation_id | uuid           | S               |
| original_units                                           | numeric(30,12) | S               |
| calculation_inputs, source_references                    | jsonb          | N               |
| algorithm_code                                           | varchar(80)    | N               |
| algorithm_version                                        | bigint         | N               |
| result                                                   | varchar(20)    | N               |
| T, S                                                     | Bloques        |                 |

PK (calculation_id, calculation_version).

Exactamente uno de preparación y depósito está presente, con FK local.

FK a observaciones de índice utilizadas.

Resultados DETERMINED, PROVISIONAL e INCOMPLETE_INPUT.

Neto = capital original + actualización + interés + compensación − tributos.

La actualización admite signo cuando la definición del índice lo requiere; el neto ejecutable no puede ser negativo.

El nominal utiliza interés simple Actual/365, incluyendo constitución y excluyendo fecha final.

UVA conserva unidades originales, valores inicial y final, capital actualizado e interés separados.

Sin índice requerido no existe liquidación definitiva.

## **9. Obligación, instrucciones y vencimiento**

### **9.1. term_deposit_obligation**

| **Columna**                                         | **Tipo**      | **Nulabilidad** |
|-----------------------------------------------------|---------------|-----------------|
| obligation_id, deposit_id                           | uuid          | N               |
| concept_code                                        | varchar(80)   | N               |
| determined_amount, applied_amount, remaining_amount | numeric(24,2) | N               |
| currency                                            | varchar(3)    | N               |
| calculation_id                                      | uuid          | N               |
| calculation_version                                 | bigint        | N               |
| status                                              | varchar(20)   | N               |
| T                                                   | Bloque        |                 |
| lock_version                                        | bigint        | N               |

PK obligation_id.

FK a depósito y cálculo.

UQ (deposit_id, concept_code).

Conceptos CAPITAL, INDEX_ADJUSTMENT, INTEREST, TAX y COMPENSATION.

Las variaciones de actualización se conservan con su signo. Los importes pagaderos y aplicaciones se determinan sobre el
resultado neto.

No se registra NET_PAYABLE como otra deuda sumable a sus componentes; se obtiene del cálculo y sus aplicaciones.

### **9.2. term_deposit_maturity_instruction**

| **Columna**         | **Tipo**    | **Nulabilidad** |
|---------------------|-------------|-----------------|
| deposit_id          | uuid        | N               |
| instruction_version | bigint      | N               |
| mode                | varchar(40) | N               |
| acceptance_evidence | jsonb       | N               |
| destination         | jsonb       | N               |
| effective_at        | timestamptz | N               |
| T, S                | Bloques     |                 |

PK (deposit_id, instruction_version) y FK a depósito.

Modalidades PAY_TOTAL, RENEW_CAPITAL_PAY_NET_RETURN y RENEW_CAPITAL_AND_NET_RETURN.

Sin renovación aceptada corresponde pago total.

### **9.3. term_deposit_maturity_occurrence**

| **Columna**               | **Tipo**    | **Nulabilidad** |
|---------------------------|-------------|-----------------|
| occurrence_id, deposit_id | uuid        | N               |
| maturity_date             | date        | N               |
| status                    | varchar(20) | N               |
| T, C                      | Bloques     |                 |

PK occurrence_id.

FK a depósito. UQ (deposit_id, maturity_date).

Estados PENDING, PROCESSING, COMPLETED y REQUIRES_RESOLUTION.

### **9.4. term_deposit_exit_control**

| **Columna**                                        | **Tipo**      | **Nulabilidad** |
|----------------------------------------------------|---------------|-----------------|
| deposit_id                                         | uuid          | N, PK           |
| selected_treatment                                 | varchar(20)   | S               |
| occurrence_id                                      | uuid          | S               |
| calculation_id                                     | uuid          | S               |
| calculation_version                                | bigint        | S               |
| renewable_amount, monetary_payable_amount          | numeric(24,2) | N               |
| committed_payment_amount, confirmed_payment_amount | numeric(24,2) | N               |
| renewed_amount                                     | numeric(24,2) | N               |
| lock_version                                       | bigint        | N               |
| updated_at                                         | timestamptz   | N               |

FK a depósito, ocurrencia y cálculo cuando existan.

Tratamientos MATURITY_PAYMENT, RENEWAL y EARLY_EXIT.

Los destinos de aplicación se determinan sobre el mismo neto. Ninguna porción puede pagarse y renovarse a la vez.

## **10. Pago y renovación**

### **10.1. term_deposit_payment**

| **Columna**            | **Tipo**      | **Nulabilidad** |
|------------------------|---------------|-----------------|
| payment_id, deposit_id | uuid          | N               |
| maturity_occurrence_id | uuid          | S               |
| early_exit_request_id  | uuid          | S               |
| payment_component_code | varchar(80)   | N               |
| calculation_id         | uuid          | N               |
| calculation_version    | bigint        | N               |
| destination            | jsonb         | N               |
| amount                 | numeric(24,2) | N               |
| currency               | varchar(3)    | N               |
| payment_effect_id      | uuid          | N               |
| status                 | varchar(30)   | N               |
| result_reference       | jsonb         | S               |
| T, C, S                | Bloques       |                 |

PK payment_id.

FK a depósito, cálculo y origen de salida.

Exactamente una de ocurrencia y solicitud anticipada está presente.

UQ sobre efecto.

UQ parciales por ocurrencia o salida y payment_component_code.

Estados PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Una cuenta inválida no extingue la obligación.

La remuneración llega hasta la fecha hábil programada. La demora adicional del banco genera compensación separada, sin
duplicar períodos.

### **10.2. term_deposit_renewal**

| **Columna**                                    | **Tipo**      | **Nulabilidad** |
|------------------------------------------------|---------------|-----------------|
| renewal_id, previous_deposit_id, occurrence_id | uuid          | N               |
| instruction_version                            | bigint        | N               |
| accepted_new_preparation_id                    | uuid          | N               |
| renewed_capital, payable_net_return            | numeric(24,2) | N               |
| successor_deposit_id                           | uuid          | S               |
| status                                         | varchar(30)   | N               |
| T, C, S                                        | Bloques       |                 |

PK renewal_id.

FK a depósito anterior, ocurrencia, instrucción, preparación y sucesor.

UQ sobre ocurrencia y sobre sucesor cuando exista.

Estados PREPARING, CONFIRMED, PAYMENT_FALLBACK y REQUIRES_RESOLUTION.

La confirmación local registra conjuntamente:

- Liquidación del anterior.

- Aplicación del importe renovado.

- Sucesor y su obligación.

- Remanente pagadero.

- Historia y outbox.

El importe renovado no se acredita como dinero libre.

En UVA, renovar capital significa capital actualizado.

## **11. Precancelación y correcciones de depósitos**

### **11.1. term_deposit_early_exit_request**

| **Columna**                                                | **Tipo**    | **Nulabilidad** |
|------------------------------------------------------------|-------------|-----------------|
| early_exit_request_id, deposit_id                          | uuid        | N               |
| received_at                                                | timestamptz | N               |
| minimum_stay_date, notice_completion_date, admissible_date | date        | N               |
| calendar_calculation                                       | jsonb       | N               |
| status                                                     | varchar(30) | N               |
| T, C                                                       | Bloques     |                 |

PK early_exit_request_id, FK a depósito.

Estados RECEIVED, QUOTE_PENDING, AWAITING_ACCEPTANCE, READY, EXECUTING, COMPLETED y MATURITY_TREATMENT.

La fecha admisible es la posterior entre permanencia mínima y preaviso, ajustada al siguiente hábil.

Si alcanza el vencimiento corresponde tratamiento ordinario.

### **11.2. term_deposit_early_exit_quote**

| **Columna**                                     | **Tipo**    | **Nulabilidad** |
|-------------------------------------------------|-------------|-----------------|
| quote_id, early_exit_request_id, calculation_id | uuid        | N               |
| calculation_version                             | bigint      | N               |
| exit_rate_definition                            | jsonb       | N               |
| valid_until                                     | timestamptz | N               |
| T, S                                            | Bloques     |                 |

PK quote_id.

FK a solicitud y cálculo.

### **11.3. term_deposit_early_exit_acceptance**

Columnas N: acceptance_id uuid PK, quote_id uuid FK, actor_reference jsonb, accepted_content_hash char(64), accepted_at
timestamptz, evidence_references jsonb y bloque T.

UQ sobre quote_id.

### **11.4. term_deposit_early_exit_execution**

Columnas N: execution_id uuid PK, early_exit_request_id uuid FK, acceptance_id uuid FK, payment_id uuid FK, status
varchar(30) y bloques T y C.

UQ sobre solicitud.

La salida utiliza capital original y tasa fijada al constituir.

UVA precancelable no agrega actualización de vencimiento ni otra penalidad.

### **11.5. term_deposit_correction**

| **Columna**                               | **Tipo**    | **Nulabilidad** |
|-------------------------------------------|-------------|-----------------|
| correction_id, deposit_id                 | uuid        | N               |
| original_reference                        | jsonb       | N               |
| correction_type                           | varchar(30) | N               |
| typed_changes, evidence_references        | jsonb       | N               |
| approval_reference                        | jsonb       | N               |
| difference_calculation, result_references | jsonb       | N               |
| status                                    | varchar(20) | N               |
| T, C, S                                   | Bloques     |                 |

PK correction_id, FK a depósito.

Tipos METADATA, CALCULATION, MATURITY_INSTRUCTION y ECONOMIC_EFFECT.

No modifica condiciones aceptadas históricas ni repite pagos.

## **12. Instrumentos y clases**

### **12.1. investment_instrument**

| **Columna**                       | **Tipo**     | **Nulabilidad** |
|-----------------------------------|--------------|-----------------|
| instrument_id                     | uuid         | N, PK           |
| code                              | varchar(80)  | N               |
| name                              | varchar(200) | N               |
| issuer_or_administrator_reference | jsonb        | N               |
| T, C                              | Bloques      |                 |

UQ sobre código.

### **12.2. investment_instrument_class**

| **Columna**     | **Tipo**    | **Nulabilidad** |
|-----------------|-------------|-----------------|
| instrument_id   | uuid        | N               |
| class_code      | varchar(80) | N               |
| current_version | bigint      | N               |
| T               | Bloque      |                 |
| lock_version    | bigint      | N               |

PK (instrument_id, class_code) y FK a instrumento.

### **12.3. investment_instrument_version**

| **Columna**                                           | **Tipo**       | **Nulabilidad** |
|-------------------------------------------------------|----------------|-----------------|
| instrument_id                                         | uuid           | N               |
| class_code                                            | varchar(80)    | N               |
| instrument_version                                    | bigint         | N               |
| instrument_type                                       | varchar(30)    | N               |
| currency                                              | varchar(3)     | N               |
| quantity_scale                                        | integer        | N               |
| minimum_quantity, quantity_step                       | numeric(30,12) | N               |
| quotation_basis                                       | varchar(20)    | N               |
| allowed_operations, risk_classification               | jsonb          | N               |
| calendar_references, settlement_rules, transfer_rules | jsonb          | N               |
| specific_definition, document_references              | jsonb          | N               |
| valid_from                                            | timestamptz    | N               |
| valid_until                                           | timestamptz    | S               |
| status                                                | varchar(30)    | N               |
| definition_revision                                   | bigint         | N               |
| approved_review_id                                    | uuid           | S               |
| T, S                                                  | Bloques        |                 |
| lock_version                                          | bigint         | N               |

PK sobre instrumento, clase y versión.

FK a clase.

Tipos OPEN_END_FUND y FIXED_INCOME_SECURITY.

Precisión entre cero y doce; mínimo e incremento positivos y representables con esa precisión.

Bases PER_UNIT y PER_100_NOMINAL.

Estados DRAFT, PENDING_APPROVAL, APPROVED, ENABLED, SUSPENDED y WITHDRAWN.

Fondos conservan administrador y regla de cuotaparte. Títulos conservan nominal, cupón, amortización y vencimiento.

No se admiten derivados, apalancamiento, ventas en corto ni margen.

## **13. Revisión y habilitación**

### **13.1. investment_instrument_review**

| **Columna**                             | **Tipo**      | **Nulabilidad** |
|-----------------------------------------|---------------|-----------------|
| review_id, instrument_id                | uuid          | N               |
| class_code                              | varchar(80)   | N               |
| instrument_version, definition_revision | bigint        | N               |
| reviewed_content_hash                   | char(64)      | N               |
| preparer_person_id                      | uuid          | N               |
| approver_person_id                      | uuid          | S               |
| decision                                | varchar(20)   | N               |
| approval_reference                      | jsonb         | S               |
| submitted_at                            | timestamptz   | N               |
| decided_at                              | timestamptz   | S               |
| reason                                  | varchar(4000) | S               |
| T                                       | Bloque        |                 |

PK review_id, FK a versión.

UQ sobre revisión, instrumento, clase y versión para FK compuesta desde la definición.

APPROVED exige aprobación, fecha y personas distintas.

El contenido aprobado es inmutable.

### **13.2. investment_instrument_operation_status**

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| operation_status_id, instrument_id | uuid        | N               |
| class_code, operation_code         | varchar(80) | N               |
| instrument_version                 | bigint      | N               |
| status                             | varchar(20) | N               |
| effective_from                     | timestamptz | N               |
| effective_until                    | timestamptz | S               |
| scope_payload, basis_reference     | jsonb       | N               |
| T                                  | Bloque      |                 |

PK operation_status_id, FK a versión.

Estados ENABLED, SUSPENDED y WITHDRAWN.

Índice (instrument_id, class_code, operation_code, effective_from, operation_status_id).

Los alcances incompatibles no se superponen bajo el control de clase.

Suspender compras no suspende automáticamente rescates, liquidaciones o derechos.

## **14. Valores de referencia y eventos**

### **14.1. investment_reference_value**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| reference_value_id, instrument_id | uuid        | N               |
| class_code, value_type            | varchar(80) | N               |
| applicable_date                   | date        | N               |
| source_service                    | varchar(80) | N               |
| source_observation_id             | uuid        | N               |
| current_version                   | bigint      | N               |
| T                                 | Bloque      |                 |
| lock_version                      | bigint      | N               |

PK reference_value_id, FK a clase.

UQ (source_service, source_observation_id).

Tipos UNIT_VALUE, MARKET_PRICE y ACCRUED_INTEREST.

### **14.2. investment_reference_value_version**

| **Columna**            | **Tipo**       | **Nulabilidad** |
|------------------------|----------------|-----------------|
| reference_value_id     | uuid           | N               |
| value_version          | bigint         | N               |
| source_version         | bigint         | N               |
| value                  | numeric(30,12) | N               |
| currency               | varchar(3)     | N               |
| instrument_version     | bigint         | N               |
| verification_status    | varchar(30)    | N               |
| previous_value_version | bigint         | S               |
| correction_reason      | varchar(4000)  | S               |
| evidence_references    | jsonb          | N               |
| T, S                   | Bloques        |                 |

PK (reference_value_id, value_version).

FK a identidad y versión anterior cuando exista.

Precios y cuotapartes positivos. Interés corrido no negativo.

Estados VALIDATED, PENDING_VERIFICATION y OBSERVED.

UQ (reference_value_id, source_version).

Las correcciones no sobrescriben observaciones utilizadas.

### **14.3. investment_economic_event**

| **Columna**                | **Tipo**    | **Nulabilidad** |
|----------------------------|-------------|-----------------|
| event_id, instrument_id    | uuid        | N               |
| class_code, source_service | varchar(80) | N               |
| source_event_id            | uuid        | N               |
| current_version            | bigint      | N               |
| T                          | Bloque      |                 |
| lock_version               | bigint      | N               |

PK event_id, FK a clase.

UQ (source_service, source_event_id).

### **14.4. investment_economic_event_version**

| **Columna**                                            | **Tipo**      | **Nulabilidad** |
|--------------------------------------------------------|---------------|-----------------|
| event_id                                               | uuid          | N               |
| event_version                                          | bigint        | N               |
| event_type                                             | varchar(30)   | N               |
| instrument_version                                     | bigint        | N               |
| entitlement_date, payment_date                         | date          | N               |
| currency                                               | varchar(3)    | N               |
| registered_rule, source_reference, evidence_references | jsonb         | N               |
| previous_event_version                                 | bigint        | S               |
| correction_reason                                      | varchar(4000) | S               |
| T, S                                                   | Bloques       |                 |

PK (event_id, event_version) y FK a evento.

Tipos COUPON y PRINCIPAL_AMORTIZATION.

Definir evento no acredita cobro. Un fondo acumulativo no genera dividendo adicional ficticio.

## **15. Cuentas de inversión**

### **15.1. investment_account**

| **Columna**                            | **Tipo**     | **Nulabilidad** |
|----------------------------------------|--------------|-----------------|
| investment_account_id, constitution_id | uuid         | N               |
| contract_reference                     | jsonb        | N               |
| ownership_version                      | bigint       | N               |
| custody_service                        | varchar(80)  | N               |
| external_custody_account               | varchar(120) | N               |
| capabilities                           | jsonb        | N               |
| status                                 | varchar(20)  | N               |
| T, C                                   | Bloques      |                 |

PK investment_account_id, UQ sobre constitución.

Estados CONSTITUTING, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

UQ (custody_service, external_custody_account).

La cuenta no mantiene saldo monetario propio.

### **15.2. investment_account_ownership_version**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| investment_account_id | uuid        | N               |
| ownership_version     | bigint      | N               |
| acting_mode           | varchar(20) | N               |
| joint_rule            | jsonb       | S               |
| contractual_basis     | jsonb       | N               |
| valid_from            | timestamptz | N               |
| valid_until           | timestamptz | S               |
| T, S                  | Bloques     |                 |

PK sobre cuenta y versión.

FK a cuenta. JOINT exige regla.

Exclusión de intervalos superpuestos por cuenta.

### **15.3. investment_account_holder**

Columnas N:

- investment_account_id uuid.

- ownership_version bigint.

- party_id uuid.

- ownership_basis jsonb.

PK sobre cuenta, versión y persona. FK a versión de titularidad.

Índice inverso (party_id, investment_account_id, ownership_version).

### **15.4. investment_account_operator**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| operator_id, investment_account_id, party_id | uuid        | N               |
| permitted_acts, basis_reference              | jsonb       | N               |
| valid_from                                   | timestamptz | N               |
| valid_until                                  | timestamptz | S               |
| T, C                                         | Bloques     |                 |

PK operator_id, FK a cuenta.

Índice (investment_account_id, party_id, valid_from, operator_id).

### **15.5. investment_account_monetary_link**

| **Columna**                    | **Tipo**    | **Nulabilidad** |
|--------------------------------|-------------|-----------------|
| link_id, investment_account_id | uuid        | N               |
| bank_account_reference         | jsonb       | N               |
| currency                       | varchar(3)  | N               |
| purpose_code                   | varchar(80) | N               |
| valid_from                     | timestamptz | N               |
| valid_until                    | timestamptz | S               |
| authorization_reference        | jsonb       | N               |
| T                              | Bloque      |                 |

PK link_id, FK a cuenta.

Índice (investment_account_id, currency, purpose_code, link_id).

## **16. Cambios y preparaciones de cuenta**

### **16.1. investment_account_change**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| change_id, investment_account_id             | uuid        | N               |
| source_account_version                       | bigint      | N               |
| change_type                                  | varchar(30) | N               |
| complete_dimension_content, formalized_basis | jsonb       | N               |
| existing_positions_treatment                 | jsonb       | N               |
| received_at                                  | timestamptz | N               |
| effective_at                                 | timestamptz | S               |
| status                                       | varchar(30) | N               |
| T, C, S                                      | Bloques     |                 |

PK change_id, FK a cuenta.

Tipos OWNERSHIP, AUTHORIZED_OPERATORS, MONETARY_ACCOUNTS y CAPABILITIES.

Estados RECEIVED, RESOLUTION_PENDING, APPLIED y REQUIRES_REVIEW.

### **16.2. investment_account_authority_control**

Columnas N: investment_account_id uuid PK y FK, authority_version bigint, incompatible_change_pending boolean,
closure_barrier boolean, lock_version bigint y updated_at timestamptz.

### **16.3. investment_account_preparation_basis**

| **Columna**                           | **Tipo**       | **Nulabilidad** |
|---------------------------------------|----------------|-----------------|
| preparation_id, investment_account_id | uuid           | N               |
| account_version, ownership_version    | bigint         | N               |
| actor_references                      | jsonb          | N               |
| act_code                              | varchar(80)    | N               |
| instrument_reference                  | jsonb          | N               |
| quantity                              | numeric(30,12) | S               |
| amount                                | numeric(24,2)  | S               |
| currency                              | varchar(3)     | S               |
| checks, joint_evidence                | jsonb          | N               |
| binding_content_hash                  | char(64)       | N               |
| schema_version                        | bigint         | N               |

PK preparation_id.

FK a preparación común, cuenta y titularidad.

Importe exige moneda. La presencia de cantidad o importe depende del acto.

Índice (investment_account_id, preparation_id).

La preparación protege facultades propias. No compromete unidades ni dinero.

## **17. Posiciones y movimientos de unidades**

### **17.1. investment_position**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| position_id, investment_account_id       | uuid           | N               |
| instrument_service                       | varchar(80)    | N               |
| instrument_id                            | uuid           | N               |
| class_code, custody_service              | varchar(80)    | N               |
| custody_account_reference                | varchar(120)   | N               |
| registered_quantity, available_quantity  | numeric(30,12) | N               |
| position_version, last_movement_sequence | bigint         | N               |
| completeness                             | varchar(20)    | N               |
| created_at, updated_at                   | timestamptz    | N               |
| lock_version                             | bigint         | N               |

PK position_id, FK a cuenta.

UQ (investment_account_id, instrument_service, instrument_id, class_code, custody_service, custody_account_reference).

Completitud COMPLETE o INCOMPLETE.

Posición inicial con cantidades y secuencia cero. Versión inicial uno.

Disponible no superior a registrado.

### **17.2. investment_position_movement**

| **Columna**                         | **Tipo**       | **Nulabilidad** |
|-------------------------------------|----------------|-----------------|
| movement_id, position_id, effect_id | uuid           | N               |
| movement_sequence, position_version | bigint         | N               |
| direction                           | varchar(3)     | N               |
| quantity                            | numeric(30,12) | N               |
| source_service, source_type         | varchar(80)    | N               |
| source_id                           | uuid           | N               |
| effective_date                      | date           | N               |
| recorded_at                         | timestamptz    | N               |
| commitment_id                       | uuid           | S               |
| settlement_evidence                 | jsonb          | N               |
| T                                   | Bloque         |                 |

PK movement_id.

FK a posición, historia de posición y compromiso cuando exista.

UQ sobre efecto y (position_id, movement_sequence).

Sentido IN u OUT. Cantidad y secuencia positivas.

Índices:

- (position_id, effective_date, movement_sequence).

- (source_service, source_type, source_id, movement_id).

La secuencia se asigna incrementando la posición en la misma transacción.

### **17.3. investment_position_history**

Columnas N:

- position_id uuid.

- position_version bigint.

- registered_quantity numeric(30,12).

- available_quantity numeric(30,12).

- last_movement_sequence bigint.

- resource_snapshot jsonb.

- cause_reference jsonb.

- recorded_at timestamptz.

- Bloques T y S.

PK sobre posición y versión. FK a posición.

Las instantáneas son inmutables.

## **18. Compromisos de unidades**

### **18.1. investment_unit_commitment**

| **Columna**                                              | **Tipo**       | **Nulabilidad** |
|----------------------------------------------------------|----------------|-----------------|
| commitment_id, position_id                               | uuid           | N               |
| origin_service, origin_type                              | varchar(80)    | N               |
| origin_id, covered_effect_id                             | uuid           | N               |
| purpose_code                                             | varchar(80)    | N               |
| initial_quantity, current_quantity                       | numeric(30,12) | N               |
| consumed_quantity, released_quantity, remaining_quantity | numeric(30,12) | N               |
| firmness                                                 | varchar(20)    | N               |
| status                                                   | varchar(30)    | N               |
| T, C, S                                                  | Bloques        |                 |

PK commitment_id, FK a posición.

UQ (origin_service, origin_type, origin_id, covered_effect_id).

Vigente = consumido + liberado + remanente.

Firmezas CANCELLABLE, FIRM y UNCERTAIN.

Estados ACTIVE, PARTIALLY_CONSUMED, CONSUMED, RELEASED y RESOLUTION_PENDING.

Índice (position_id, status, commitment_id).

### **18.2. investment_unit_commitment_action**

| **Columna**                           | **Tipo**       | **Nulabilidad** |
|---------------------------------------|----------------|-----------------|
| commitment_action_id, commitment_id   | uuid           | N               |
| action                                | varchar(20)    | N               |
| previous_quantity, resulting_quantity | numeric(30,12) | N               |
| released_quantity                     | numeric(30,12) | N               |
| reason, evidence_references           | jsonb          | N               |
| T, S                                  | Bloques        |                 |

PK commitment_action_id, FK a compromiso.

Acciones CHANGE_QUANTITY, CHANGE_FIRMNESS y RELEASE.

Una reducción del total autorizado no se contabiliza además como liberación.

### **18.3. investment_commitment_consumption**

Columnas N: consumption_id uuid PK, commitment_id uuid FK, movement_id uuid FK, effect_id uuid UQ, quantity
numeric(30,12) y bloque T.

El consumo y la salida de unidades se confirman conjuntamente.

## **19. Restricciones y superposiciones**

### **19.1. investment_position_restriction**

| **Columna**                 | **Tipo**       | **Nulabilidad** |
|-----------------------------|----------------|-----------------|
| restriction_id, position_id | uuid           | N               |
| measure_owner_service       | varchar(80)    | N               |
| measure_id                  | uuid           | N               |
| component_code              | varchar(80)    | N               |
| current_measure_version     | bigint         | N               |
| mode                        | varchar(20)    | N               |
| quantity                    | numeric(30,12) | S               |
| defined_scope               | jsonb          | S               |
| effective_at                | timestamptz    | N               |
| status                      | varchar(20)    | N               |
| T, C                        | Bloques        |                 |

PK restriction_id, FK a posición.

UQ (position_id, measure_owner_service, measure_id, component_code).

QUANTITY exige cantidad. DEFINED_SCOPE exige actos o alcance determinado.

Estados ACTIVE, LIFTED y REQUIRES_REVIEW.

### **19.2. investment_restriction_change**

Columnas N: change_id uuid PK, restriction_id uuid FK, measure_version bigint, action varchar(10), requested_content
jsonb, evidence_references jsonb y bloques T y S.

UQ (restriction_id, measure_version).

Acciones APPLY, MODIFY y LIFT.

### **19.3. investment_unit_allocation**

| **Columna**                | **Tipo**       | **Nulabilidad** |
|----------------------------|----------------|-----------------|
| allocation_id, position_id | uuid           | N               |
| quantity                   | numeric(30,12) | N               |
| status                     | varchar(20)    | N               |
| origin_position_version    | bigint         | N               |
| ended_position_version     | bigint         | S               |

PK allocation_id, FK a posición e historia.

Cantidad positiva.

Estados HELD, CONSUMED y RELEASED.

### **19.4. investment_unit_allocation_cause**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| allocation_id                 | uuid        | N               |
| cause_id                      | uuid        | N               |
| cause_type                    | varchar(30) | N               |
| commitment_id, restriction_id | uuid        | S               |
| settlement_hold_id            | uuid        | S               |

PK (allocation_id, cause_id).

FK a asignación y causa local correspondiente.

Exactamente una referencia de causa está presente.

Una asignación puede tener varias causas superpuestas, pero no respalda dos obligaciones de disposición independientes.

Disponible = registrado − suma de asignaciones HELD distintas.

Las porciones se dividen cuando una causa afecta solo parte. Las sumas y pertenencia a la posición se comprueban bajo
bloqueo.

### **19.5. investment_position_resource_history**

Columnas N: history_id uuid PK, position_id uuid, position_version bigint, resource_type varchar(80), resource_id uuid,
change_code varchar(80), resulting_snapshot jsonb y bloques T y S.

Columna S: previous_snapshot jsonb.

FK a historia de posición.

## **20. Órdenes de inversión**

### **20.1. investment_order**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| order_id, investment_account_id          | uuid           | N               |
| origin_service                           | varchar(80)    | N               |
| origin_reference                         | varchar(120)   | N               |
| instrument_service                       | varchar(80)    | N               |
| instrument_id                            | uuid           | N               |
| class_code                               | varchar(80)    | N               |
| instrument_version                       | bigint         | N               |
| order_type                               | varchar(30)    | N               |
| currency                                 | varchar(3)     | N               |
| maximum_amount, maximum_charges          | numeric(24,2)  | S               |
| requested_quantity                       | numeric(30,12) | S               |
| redeem_all                               | boolean        | N               |
| fixed_total_quantity                     | numeric(30,12) | S               |
| quantity_determination_evidence          | jsonb          | S               |
| limit_price                              | numeric(30,12) | S               |
| trading_session_reference                | jsonb          | S               |
| requested_date, processing_date          | date           | N               |
| received_at                              | timestamptz    | N               |
| accepted_conditions, authorization_basis | jsonb          | N               |
| investor_suitability_reference           | jsonb          | N               |
| status                                   | varchar(30)    | N               |
| T, C, S                                  | Bloques        |                 |

PK order_id.

Las referencias de cuenta e instrumento son remotas.

UQ (origin_service, origin_reference).

Tipos FUND_SUBSCRIPTION, FUND_REDEMPTION, SECURITY_BUY y SECURITY_SELL.

Restricciones:

- Suscripción exige importe máximo.

- Rescate exige cantidad o totalidad, de forma excluyente.

- Totalidad fija cantidad al preparar recursos y conserva evidencia.

- Compra y venta de títulos exigen cantidad, precio límite y rueda.

- No se combinan campos incompatibles.

Estados RECEIVED, VALIDATING, RESOURCES_PENDING, READY, DISPATCHED, PARTIALLY_EXECUTED, EXECUTED, CANCELLATION_PENDING,
CANCELLED, EXPIRED, SETTLEMENT_PENDING, COMPLETED, REJECTED y REQUIRES_RESOLUTION.

Índices:

- (investment_account_id, received_at, order_id).

- (status, processing_date, order_id).

## **21. Despacho y cancelación de órdenes**

### **21.1. investment_order_dispatch**

| **Columna**                               | **Tipo**     | **Nulabilidad** |
|-------------------------------------------|--------------|-----------------|
| dispatch_id, order_id, participant_id     | uuid         | N               |
| mechanism_code                            | varchar(80)  | N               |
| mechanism_version                         | bigint       | N               |
| external_instruction_id                   | varchar(120) | N               |
| requested_content                         | jsonb        | N               |
| dispatch_authorized, cancellation_barrier | boolean      | N               |
| claim_owner                               | uuid         | S               |
| claim_generation                          | bigint       | N               |
| claim_until                               | timestamptz  | S               |
| status                                    | varchar(20)  | N               |
| result_reference                          | jsonb        | S               |
| T, C, S                                   | Bloques      |                 |

PK dispatch_id, FK a orden y UQ sobre order_id.

UQ (participant_id, mechanism_code, external_instruction_id).

Estados PREPARED, CLAIMED, SENT, CONFIRMED, CANCELLED y UNCERTAIN.

CLAIMED exige propietario y vencimiento de concesión.

### **21.2. investment_order_cancellation**

| **Columna**                             | **Tipo**       | **Nulabilidad** |
|-----------------------------------------|----------------|-----------------|
| cancellation_id, order_id               | uuid           | N               |
| action                                  | varchar(20)    | N               |
| requested_remaining_quantity            | numeric(30,12) | S               |
| requested_remaining_amount              | numeric(24,2)  | S               |
| external_checks, final_barrier_evidence | jsonb          | N               |
| result                                  | varchar(30)    | N               |
| T, C, S                                 | Bloques        |                 |

PK cancellation_id, FK a orden.

Acciones REQUEST_CANCELLATION y CHECK_EXPIRATION.

Resultados PENDING, CANCELLED, PARTIALLY_CANCELLED y REQUIRES_RESOLUTION.

La cancelación y la reclamación de despacho bloquean primero la orden.

Si el despacho ya fue reclamado, se resuelve su ejecución antes de liberar recursos.

La ausencia aislada de una referencia externa no prueba cancelación definitiva.

## **22. Ejecuciones y resultados externos**

### **22.1. investment_execution**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| execution_id, order_id, participant_id   | uuid           | N               |
| mechanism_code                           | varchar(80)    | N               |
| external_execution_id                    | varchar(120)   | N               |
| quantity, execution_price                | numeric(30,12) | N               |
| reference_value                          | jsonb          | N               |
| principal_amount, accrued_interest       | numeric(24,2)  | N               |
| charges_amount, tax_amount, total_amount | numeric(24,2)  | N               |
| currency                                 | varchar(3)     | N               |
| occurred_at                              | timestamptz    | N               |
| evidence_references                      | jsonb          | N               |
| settlement_status                        | varchar(30)    | N               |
| T, C, S                                  | Bloques        |                 |

PK execution_id, FK a orden.

UQ (participant_id, mechanism_code, external_execution_id).

Estados PENDING, PARTIALLY_SETTLED, SETTLED y REQUIRES_RESOLUTION.

Índice (order_id, occurred_at, execution_id).

El total conserva la composición aplicable a compra o venta; cargos y tributos no se confunden con principal.

### **22.2. investment_external_result**

| **Columna**                         | **Tipo**     | **Nulabilidad** |
|-------------------------------------|--------------|-----------------|
| result_id, order_id, participant_id | uuid         | N               |
| mechanism_code                      | varchar(80)  | N               |
| external_result_id                  | varchar(120) | N               |
| external_execution_id               | varchar(120) | S               |
| result                              | varchar(20)  | N               |
| typed_payload, evidence_references  | jsonb        | N               |
| occurred_at, received_at            | timestamptz  | N               |
| T, S                                | Bloques      |                 |

PK result_id, FK a orden.

UQ (participant_id, mechanism_code, external_result_id).

ACKNOWLEDGED, ACCEPTED, EXECUTED, REJECTED y UNCERTAIN.

EXECUTED exige identidad de ejecución y cantidades e importes comprobados.

### **22.3. investment_external_result_conflict**

Columnas N: conflict_id uuid PK, result_id uuid FK, conflicting_hash char(64), conflicting_payload jsonb,
evidence_references jsonb y bloque T.

UQ (result_id, conflicting_hash).

El original no se sobrescribe.

### **22.4. Reglas de ejecución**

La suscripción anterior a las 15:00 de un hábil utiliza esa fecha; desde las 15:00, el siguiente hábil.

Se conservan importe autorizado, valor aplicable, cantidad, dinero aplicado y residuo.

Los títulos por cien nominales utilizan principal = cantidad × precio / 100, con redondeo final. El interés corrido y
cargos se conservan separados.

Las ejecuciones respetan límites y acumulados autorizados.

Cada parcial tiene identidad propia. El rescate no admite parcialidad unilateral.

## **23. Liquidaciones y disponibilidad**

### **23.1. investment_settlement**

| **Columna**                                 | **Tipo**    | **Nulabilidad** |
|---------------------------------------------|-------------|-----------------|
| settlement_id, execution_id                 | uuid        | N               |
| contractual_settlement_code                 | varchar(80) | N               |
| participant_references, mechanism_reference | jsonb       | N               |
| scheduled_date                              | date        | N               |
| resource_references                         | jsonb       | N               |
| status                                      | varchar(30) | N               |
| T, C, S                                     | Bloques     |                 |

PK settlement_id, FK a ejecución.

UQ (execution_id, contractual_settlement_code).

Estados PENDING, PARTIALLY_CONFIRMED, CONFIRMED, FAILED y REQUIRES_RESOLUTION.

### **23.2. investment_settlement_component**

| **Columna**                 | **Tipo**       | **Nulabilidad** |
|-----------------------------|----------------|-----------------|
| component_id, settlement_id | uuid           | N               |
| owner_service               | varchar(80)    | N               |
| component_type              | varchar(20)    | N               |
| direction                   | varchar(10)    | N               |
| amount                      | numeric(24,2)  | S               |
| currency                    | varchar(3)     | S               |
| quantity                    | numeric(30,12) | S               |
| instrument_reference        | jsonb          | S               |
| fulfillment_condition       | jsonb          | N               |
| remote_action_id, effect_id | uuid           | N               |
| confirmed_amount            | numeric(24,2)  | S               |
| confirmed_quantity          | numeric(30,12) | S               |
| status                      | varchar(30)    | N               |

PK component_id, FK a liquidación.

MONEY exige importe y moneda, sin cantidad.

INSTRUMENTS exige cantidad e instrumento, sin importe.

UQ (owner_service, remote_action_id) y sobre efecto.

Los acumulados confirmados no superan el total del componente.

### **23.3. investment_settlement_confirmation**

| **Columna**                   | **Tipo**       | **Nulabilidad** |
|-------------------------------|----------------|-----------------|
| confirmation_id, component_id | uuid           | N               |
| source_service                | varchar(80)    | N               |
| source_fact_id                | uuid           | N               |
| source_fact_version           | bigint         | N               |
| result                        | varchar(10)    | N               |
| confirmed_amount_delta        | numeric(24,2)  | S               |
| confirmed_quantity_delta      | numeric(30,12) | S               |
| occurred_at                   | timestamptz    | N               |
| evidence_references           | jsonb          | N               |
| T, S                          | Bloques        |                 |

PK confirmation_id, FK a componente.

UQ (component_id, source_service, source_fact_id, source_fact_version).

Resultados CONFIRMED, PARTIAL, FAILED y UNCERTAIN.

La confirmación conserva únicamente el incremento nuevo. Las revisiones no suman nuevamente lo ya confirmado.

### **23.4. investment_settlement_hold**

En Tenencias:

| **Columna**                            | **Tipo**       | **Nulabilidad** |
|----------------------------------------|----------------|-----------------|
| settlement_hold_id, position_id        | uuid           | N               |
| settlement_service                     | varchar(80)    | N               |
| settlement_id, component_id            | uuid           | N               |
| quantity                               | numeric(30,12) | N               |
| release_condition, evidence_references | jsonb          | N               |
| status                                 | varchar(20)    | N               |
| T, C                                   | Bloques        |                 |

PK settlement_hold_id, FK a posición.

UQ (settlement_service, settlement_id, component_id, position_id).

Estados HELD, RELEASED y REQUIRES_REVIEW.

Los títulos utilizan entrega contra pago.

Las unidades recibidas con contraprestación pendiente permanecen registradas pero no disponibles.

## **24. Cortes y valuación**

### **24.1. investment_position_cut**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| cut_id, investment_account_id | uuid        | N               |
| cut_at                        | timestamptz | N               |
| filters, source_references    | jsonb       | N               |
| filters_hash                  | char(64)    | N               |
| completeness                  | varchar(20) | N               |
| missing_data_codes            | jsonb       | N               |
| T                             | Bloque      |                 |

PK cut_id, FK a cuenta.

### **24.2. investment_position_cut_item**

Columnas N:

- cut_id uuid.

- position_id uuid.

- position_version bigint.

- maximum_movement_sequence bigint.

- registered_quantity numeric(30,12).

- available_quantity numeric(30,12).

- resource_snapshot jsonb.

PK sobre corte y posición.

FK a corte e historia de posición.

Las páginas de movimientos conservan la secuencia máxima fijada.

### **24.3. investment_valuation**

| **Columna**           | **Tipo**    | **Nulabilidad** |
|-----------------------|-------------|-----------------|
| valuation_id, cut_id  | uuid        | N               |
| presentation_currency | varchar(3)  | N               |
| methodology_reference | jsonb       | N               |
| valued_at             | timestamptz | N               |
| completeness          | varchar(20) | N               |
| T, S                  | Bloques     |                 |

PK valuation_id, FK a corte.

### **24.4. investment_valuation_item**

| **Columna**                                 | **Tipo**       | **Nulabilidad** |
|---------------------------------------------|----------------|-----------------|
| valuation_id, position_id                   | uuid           | N               |
| quantity                                    | numeric(30,12) | N               |
| price_reference, accrued_interest_reference | jsonb          | S               |
| price, accrued_interest_value               | numeric(30,12) | S               |
| principal_value, accrued_interest_amount    | numeric(24,2)  | S               |
| currency                                    | varchar(3)     | N               |
| presentation_amount                         | numeric(24,2)  | S               |
| fx_reference                                | jsonb          | S               |
| source_age_days                             | integer        | S               |
| historical_value                            | boolean        | N               |
| missing_data_codes                          | jsonb          | N               |

PK sobre valuación y posición. FK a ambos.

La ausencia de precio permanece NULL con motivo. No se convierte en cero.

La conversión es informativa y no ejecuta cambio.

El tránsito se identifica sin sumarlo como activo duplicado.

## **25. Derechos económicos**

### **25.1. investment_entitlement**

| **Columna**                   | **Tipo**    | **Nulabilidad** |
|-------------------------------|-------------|-----------------|
| entitlement_id                | uuid        | N, PK           |
| event_service                 | varchar(80) | N               |
| economic_event_id             | uuid        | N               |
| investment_account_id         | uuid        | N               |
| beneficiary_identity_hash     | char(64)    | N               |
| beneficiary_reference         | jsonb       | N               |
| instrument_reference          | jsonb       | N               |
| current_determination_version | bigint      | N               |
| mode                          | varchar(20) | N               |
| due_date                      | date        | N               |
| status                        | varchar(30) | N               |
| T, C                          | Bloques     |                 |

FK a cuenta.

UQ (event_service, economic_event_id, beneficiary_identity_hash, investment_account_id).

Modalidad MONEY o INSTRUMENTS.

Estados DETERMINED, PARTIALLY_FULFILLED, FULFILLED y REQUIRES_RESOLUTION.

Una corrección de evento modifica el mismo derecho mediante nueva determinación.

### **25.2. investment_entitlement_determination**

| **Columna**                                            | **Tipo**       | **Nulabilidad** |
|--------------------------------------------------------|----------------|-----------------|
| entitlement_id                                         | uuid           | N               |
| determination_version, event_version                   | bigint         | N               |
| position_cut_id                                        | uuid           | N               |
| eligible_quantity                                      | numeric(30,12) | N               |
| gross_amount, tax_amount, net_amount                   | numeric(24,2)  | S               |
| currency                                               | varchar(3)     | S               |
| instrument_quantity                                    | numeric(30,12) | S               |
| residual_quantity                                      | numeric(30,12) | N               |
| rules_reference, source_positions, transfer_references | jsonb          | N               |
| previous_determination_version                         | bigint         | S               |
| monetary_difference                                    | numeric(24,2)  | S               |
| quantity_difference                                    | numeric(30,12) | S               |
| T, S                                                   | Bloques        |                 |

PK sobre derecho y versión.

FK a derecho y corte.

Las diferencias admiten signo. Las variantes monetaria y de unidades son excluyentes.

### **25.3. investment_event_position_effect**

| **Columna**              | **Tipo**       | **Nulabilidad** |
|--------------------------|----------------|-----------------|
| effect_id, position_id   | uuid           | N               |
| event_service            | varchar(80)    | N               |
| economic_event_id        | uuid           | N               |
| effect_code              | varchar(80)    | N               |
| event_version            | bigint         | N               |
| original_movement_id     | uuid           | S               |
| current_applied_quantity | numeric(30,12) | N               |
| T, C                     | Bloques        |                 |

PK effect_id, FK a posición y movimiento.

UQ (event_service, economic_event_id, position_id, effect_code).

La amortización de nominal y reconocimiento del derecho confirman conjuntamente. Las correcciones aplican diferencias.

### **25.4. investment_entitlement_receipt**

| **Columna**                | **Tipo**       | **Nulabilidad** |
|----------------------------|----------------|-----------------|
| receipt_id, entitlement_id | uuid           | N               |
| source_service             | varchar(80)    | N               |
| source_receipt_id          | uuid           | N               |
| mode                       | varchar(20)    | N               |
| amount                     | numeric(24,2)  | S               |
| currency                   | varchar(3)     | S               |
| quantity                   | numeric(30,12) | S               |
| instrument_reference       | jsonb          | S               |
| received_at                | timestamptz    | N               |
| evidence_references        | jsonb          | N               |
| T, S                       | Bloques        |                 |

PK receipt_id, FK a derecho.

UQ (source_service, source_receipt_id).

MONEY_RECEIPT e INSTRUMENT_DELIVERY son variantes excluyentes.

### **25.5. investment_entitlement_application**

| **Columna**                                | **Tipo**       | **Nulabilidad** |
|--------------------------------------------|----------------|-----------------|
| application_id, entitlement_id, receipt_id | uuid           | N               |
| beneficiary_reference                      | jsonb          | N               |
| amount                                     | numeric(24,2)  | S               |
| quantity                                   | numeric(30,12) | S               |
| effect_id                                  | uuid           | N               |
| remote_step_id                             | uuid           | S               |
| status                                     | varchar(30)    | N               |
| T, C, S                                    | Bloques        |                 |

PK application_id.

FK a derecho, recepción propia y paso remoto.

UQ sobre efecto.

Estados PENDING, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

Las aplicaciones confirmadas y comprometidas no superan lo recibido. Lo recibido no se confunde con lo entregado al
beneficiario.

## **26. Traslados de custodia**

### **26.1. custody_transfer**

| **Columna**                                                 | **Tipo**       | **Nulabilidad** |
|-------------------------------------------------------------|----------------|-----------------|
| transfer_id                                                 | uuid           | N, PK           |
| transfer_type                                               | varchar(10)    | N               |
| source_account_id, destination_account_id                   | uuid           | S               |
| source_custodian_reference, destination_custodian_reference | jsonb          | N               |
| verified_ownership                                          | jsonb          | N               |
| ownership_hash                                              | char(64)       | N               |
| instrument_reference                                        | jsonb          | N               |
| requested_quantity, committed_quantity                      | numeric(30,12) | N               |
| sent_quantity, received_quantity, returned_quantity         | numeric(30,12) | N               |
| cancelled_quantity                                          | numeric(30,12) | N               |
| requested_date                                              | date           | N               |
| authorization_reference, charges, applicable_measures       | jsonb          | N               |
| status                                                      | varchar(30)    | N               |
| T, C, S                                                     | Bloques        |                 |

PK transfer_id.

Tipos INTERNAL, OUTGOING e INCOMING.

FK a las cuentas que pertenezcan a Tenencias:

- INTERNAL exige ambas, distintas.

- OUTGOING exige origen.

- INCOMING exige destino.

Estados RECEIVED, PREPARING, COMMITTED, IN_TRANSIT, PARTIALLY_RECEIVED, COMPLETED, CANCELLED y REQUIRES_RESOLUTION.

La titularidad completa coincide, incluidos cotitulares. El traslado no cambia propiedad.

### **26.2. custody_transfer_component**

| **Columna**                                              | **Tipo**       | **Nulabilidad** |
|----------------------------------------------------------|----------------|-----------------|
| component_id, transfer_id                                | uuid           | N               |
| source_position_id, destination_position_id              | uuid           | S               |
| commitment_id                                            | uuid           | S               |
| requested_quantity, sent_quantity                        | numeric(30,12) | N               |
| received_quantity, returned_quantity, cancelled_quantity | numeric(30,12) | N               |
| restriction_treatment                                    | jsonb          | N               |
| status                                                   | varchar(30)    | N               |
| lock_version                                             | bigint         | N               |

PK component_id.

FK a traslado, posiciones y compromiso locales.

Se exige:

- Enviado no superior a solicitado.

- Recibido + retornado no superior a enviado.

- Cancelado no superior a la parte no enviada.

- Enviado + cancelado no superior a solicitado.

### **26.3. custody_transit_position**

| **Columna**                                         | **Tipo**       | **Nulabilidad** |
|-----------------------------------------------------|----------------|-----------------|
| component_id                                        | uuid           | N, PK           |
| sent_quantity, received_quantity, returned_quantity | numeric(30,12) | N               |
| pending_transit_quantity                            | numeric(30,12) | N               |
| evidence_references                                 | jsonb          | N               |
| lock_version                                        | bigint         | N               |
| updated_at                                          | timestamptz    | N               |

FK a componente.

Tránsito pendiente = enviado − recibido − retornado.

No constituye otra posición disponible.

### **26.4. custody_transfer_external_fact**

| **Columna**                              | **Tipo**       | **Nulabilidad** |
|------------------------------------------|----------------|-----------------|
| fact_id, transfer_id, component_id       | uuid           | N               |
| source_service                           | varchar(80)    | N               |
| external_fact_id                         | varchar(120)   | N               |
| fact_type                                | varchar(10)    | N               |
| quantity                                 | numeric(30,12) | N               |
| instrument_reference, ownership_evidence | jsonb          | N               |
| occurred_at                              | timestamptz    | N               |
| evidence_references                      | jsonb          | N               |
| T, S                                     | Bloques        |                 |

PK fact_id.

FK a traslado y componente propio.

UQ (source_service, external_fact_id).

Tipos ACCEPTED, SENT, RECEIVED, REJECTED y RETURNED.

Un rechazo posterior al envío no restituye disponibilidad sin retorno comprobado.

### **26.5. custody_transfer_resolution**

| **Columna**                                  | **Tipo**    | **Nulabilidad** |
|----------------------------------------------|-------------|-----------------|
| resolution_id, transfer_id                   | uuid        | N               |
| action                                       | varchar(30) | N               |
| component_scope                              | jsonb       | N               |
| new_destination                              | jsonb       | S               |
| authorization_reference, evidence_references | jsonb       | N               |
| result_payload                               | jsonb       | N               |
| T, C, S                                      | Bloques     |                 |

PK resolution_id, FK a traslado.

Acciones CANCEL_UNSENT, QUERY_PENDING, CORRECT_DESTINATION y ARRANGE_RETURN.

Corregir destino exige comprobación y autorización nuevas. No redirige unilateralmente unidades enviadas.

## **27. Conservación de titularidad, restricciones y derechos**

El traslado interno confirma conjuntamente salida, entrada, consumo de compromiso, continuidad de restricciones,
historia y outbox.

La salida externa reduce custodia de origen y registra tránsito en la misma transacción.

La recepción entrante comprueba titularidad, clase, cantidad y condiciones antes de crear disponibilidad.

Las restricciones no desaparecen por traslado. Cuando deban mantenerse en destino, se conserva la confirmación
requerida.

Los derechos se determinan con cortes y reglas de custodia que identifican las unidades trasladadas.

Un custodio anterior que recibe rendimiento después del traslado conserva la obligación residual de entregarlo al
beneficiario.

No se reconoce el mismo derecho dos veces por aparecer las unidades en origen y en tránsito o destino.

## **28. Conciliaciones y correcciones**

### **28.1. Conciliaciones**

Órdenes mantiene investment_order_reconciliation.

Tenencias mantiene investment_custody_reconciliation.

Ambas tablas utilizan:

| **Columna**                        | **Tipo**    | **Nulabilidad** |
|------------------------------------|-------------|-----------------|
| reconciliation_id                  | uuid        | N, PK           |
| scope_code                         | varchar(80) | N               |
| comparable_cuts, source_references | jsonb       | N               |
| status                             | varchar(20) | N               |
| started_at                         | timestamptz | N               |
| completed_at                       | timestamptz | S               |
| T, C                               | Bloques     |                 |

Estados RUNNING, MATCHED, DIFFERENCES y FAILED.

Cada servicio mantiene su tabla local de diferencias:

- investment_order_difference.

- investment_custody_difference.

Columnas N: difference_id uuid PK, reconciliation_id uuid FK, classification varchar(30), affected_references jsonb,
comparison_detail jsonb, responsible_service varchar(80), status varchar(20) y bloques T y C.

Columnas S: quantity_difference numeric(30,12) e amount_difference numeric(24,2), ambas con signo.

Clasificaciones MISSING_RECORD, DUPLICATE, QUANTITY_MISMATCH, AMOUNT_MISMATCH, SETTLEMENT_PENDING, TRANSIT_MISMATCH y
ENTITLEMENT_MISMATCH.

Estados OPEN, UNDER_REVIEW, RESOLUTION_PENDING y RESOLVED.

### **28.2. investment_order_correction**

| **Columna**                                                 | **Tipo**    | **Nulabilidad** |
|-------------------------------------------------------------|-------------|-----------------|
| correction_id, order_id                                     | uuid        | N               |
| original_reference                                          | jsonb       | N               |
| monetary_treatment, custody_treatment, settlement_treatment | jsonb       | N               |
| evidence_references                                         | jsonb       | N               |
| approval_reference                                          | jsonb       | S               |
| status                                                      | varchar(30) | N               |
| T, C, S                                                     | Bloques     |                 |

PK correction_id, FK a orden.

### **28.3. investment_position_correction**

| **Columna**                                        | **Tipo**    | **Nulabilidad** |
|----------------------------------------------------|-------------|-----------------|
| correction_id, investment_account_id               | uuid        | N               |
| target_type                                        | varchar(20) | N               |
| position_id, valuation_id, entitlement_id          | uuid        | S               |
| original_reference, treatment, evidence_references | jsonb       | N               |
| manual                                             | boolean     | N               |
| preparer_person_id, approver_person_id             | uuid        | S               |
| approval_reference                                 | jsonb       | S               |
| result_references                                  | jsonb       | N               |
| T, C, S                                            | Bloques     |                 |

PK correction_id.

FK a cuenta y objetivo correspondiente.

POSITION, VALUATION y ENTITLEMENT exigen exclusivamente su referencia.

Un ajuste manual exige aprobación y personas distintas.

Una ejecución omitida se recupera sin otra orden. Una diferencia de custodia no autoriza crear unidades.

Corregir valuación no ejecuta compraventa.

## **29. Cierre de cuentas de inversión**

### **29.1. investment_account_closure**

| **Columna**                       | **Tipo**    | **Nulabilidad** |
|-----------------------------------|-------------|-----------------|
| closure_id, investment_account_id | uuid        | N               |
| closure_version                   | bigint      | N               |
| contractual_termination_reference | jsonb       | N               |
| received_at                       | timestamptz | N               |
| authorized_instructions           | jsonb       | N               |
| residual_entitlement_destination  | jsonb       | N               |
| status                            | varchar(20) | N               |
| closed_at                         | timestamptz | S               |
| T, C                              | Bloques     |                 |

PK closure_id, FK a cuenta.

Estados REQUESTED, PROCESSING, RESIDUAL y CLOSED.

UQ parcial sobre cuenta mientras no sea CLOSED.

Los componentes cubren órdenes, compromisos, posiciones, liquidaciones, traslados,  
derechos, incidencias y documentos.

### **29.2. investment_closure_inventory**

| **Columna**                            | **Tipo**    | **Nulabilidad** |
|----------------------------------------|-------------|-----------------|
| inventory_id, closure_id               | uuid        | N               |
| component_code, owner_service          | varchar(80) | N               |
| inventory_version                      | bigint      | N               |
| status                                 | varchar(30) | N               |
| cut_at                                 | timestamptz | S               |
| source_version                         | bigint      | S               |
| object_references, evidence_references | jsonb       | N               |
| T                                      | Bloque      |                 |

PK inventory_id, FK a cierre.

UQ (closure_id, component_code, owner_service, inventory_version).

Estados PENDING, CONFIRMED_EMPTY, CONFIRMED_WITH_OBJECTS e INCOMPLETE.

Una respuesta ausente no equivale a inventario vacío.

Cerrar no autoriza vender a cualquier precio.  
Se conservan instrucciones de venta, rescate o traslado  
y vía de entrega de derechos posteriores.

Los depósitos a plazo mantienen sus acuerdos independientes.

## **30. Índices complementarios**

| **Tabla**                          | **Índice**                                                                   |
|------------------------------------|------------------------------------------------------------------------------|
| term_deposit_preparation_holder    | (party_id, preparation_id)                                                   |
| term_deposit_obligation            | (deposit_id, status, obligation_id)                                          |
| term_deposit_payment               | (status, updated_at, payment_id)                                             |
| term_deposit_early_exit_request    | (status, admissible_date, early_exit_request_id)                             |
| investment_instrument_version      | (status, valid_from, instrument_id, class_code, instrument_version)          |
| investment_reference_value         | (instrument_id, class_code, value_type, applicable_date, reference_value_id) |
| investment_economic_event_version  | (entitlement_date, event_id, event_version)                                  |
| investment_position                | (investment_account_id, position_id)                                         |
| investment_unit_allocation         | (position_id, status, allocation_id)                                         |
| investment_unit_allocation_cause   | (commitment_id, allocation_id), donde exista compromiso                      |
| investment_unit_allocation_cause   | (restriction_id, allocation_id), donde exista restricción                    |
| investment_unit_allocation_cause   | (settlement_hold_id, allocation_id), donde exista condición de liquidación   |
| investment_execution               | (settlement_status, occurred_at, execution_id)                               |
| investment_settlement              | (status, scheduled_date, settlement_id)                                      |
| investment_entitlement             | (status, due_date, entitlement_id)                                           |
| investment_entitlement_application | (entitlement_id, status, application_id)                                     |
| custody_transfer                   | (status, requested_date, transfer_id)                                        |
| custody_transfer_external_fact     | (transfer_id, component_id, occurred_at, fact_id)                            |
| investment_account_closure         | (status, updated_at, closure_id)                                             |

Las referencias compuestas declaran las UQ necesarias para comprobar pertenencia al mismo agregado.

Las FK disponen de índices adecuados cuando no quedan cubiertas por claves e índices existentes.

## **31. Orden de bloqueo y atomicidad**

### **31.1. Depósitos**

Depósito → control de salida → ocurrencia o solicitud anticipada → obligaciones y aplicaciones.

La renovación crea el sucesor dentro de la transacción del anterior. El sucesor no puede existir previamente como
depósito activo independiente.

### **31.2. Instrumentos**

Instrumento → clase → versión o evento → revisión o habilitación.

Las observaciones utilizadas se conservan inmutables.

### **31.3. Órdenes**

Orden → despacho → ejecuciones ordenadas → liquidaciones y componentes.

Cancelación y autorización de despacho utilizan la misma raíz.

### **31.4. Tenencias**

Cuentas ordenadas → traslado, cuando corresponda → posiciones ordenadas → compromisos → restricciones y asignaciones.

Los procesos que solo afectan una posición comienzan por su cuenta.

Ningún proceso adquiere una cuenta después de bloquear una de sus posiciones.

Las operaciones de derechos y aplicaciones determinan previamente todas las cuentas y posiciones que requieren bloqueo.

### **31.5. Confirmación local**

Se confirman conjuntamente:

- Movimiento y consumo de compromiso.

- Posición, disponibilidad e historia.

- Amortización de nominal y derecho.

- Salida de custodia y tránsito.

- Traslado interno de ambas posiciones.

- Aplicación de obligación de depósito y renovación.

- Resultado de actuación y outbox.

No se mantienen transacciones PostgreSQL durante llamadas remotas.

## **32. Eventos y recuperación**

| **Contrato** | **Tipo**                                  | **Tema**                             |
|--------------|-------------------------------------------|--------------------------------------|
| CI-08-043    | TermDepositChanged                        | banking.term-deposit.events          |
| CI-08-044    | InvestmentInstrumentOrEventChanged        | banking.investment-instrument.events |
| CI-08-045    | InvestmentOrderOrSettlementChanged        | banking.investment-order.events      |
| CI-08-046    | InvestmentPositionOrEntitlementChanged    | banking.investment-position.events   |
| CI-08-047    | CustodyTransferOrInvestmentAccountChanged | banking.investment-position.events   |

Versión inicial de esquema 1. La clave de partición es la identidad del agregado definido por cada variante.

Una valuación no comunica dinero disponible. Un derecho determinado no acredita cobro.

La liquidación referencia efectos originales y no ordena repetirlos.

Los trabajos conservan identidades de preparación, depósito, ocurrencia, pago, renovación, orden, ejecución,
liquidación, movimiento, derecho y traslado.

Ante incertidumbre se consulta al detectarla, a los cinco minutos, a los treinta minutos y por jornada operativa
mientras continúe la investigación.

No se liberan dinero ni unidades por timeout.

## **33. Seguridad, migraciones y conservación**

Las migraciones utilizan Flyway por servicio.

Las FK circulares de raíz y versión se implementan como diferibles. Las exclusiones temporales utilizan el soporte
PostgreSQL correspondiente.

Los cambios de precisión, clase o instrumento no reinterpretan cantidades históricas.

Se conservan cálculos aceptados, movimientos, ejecuciones, evidencias de custodia y aplicaciones.

No se eliminan mediante cascadas depósitos, obligaciones, liquidaciones, movimientos, derechos o traslados utilizados.

Redis no conserva la única copia de obligaciones, unidades, compromisos, tránsito o decisiones.

Los activos de clientes no respaldan obligaciones propias del banco.

La restauración pausa instrucciones y reconcilia dinero y custodia externos antes de reanudar.

Las consultas históricas y recuperaciones mantienen autorización, finalidad y titularidad comprobadas.

## **34. Correspondencia con contratos técnicos**

| **Contratos**         | **Persistencia**                                          |
|-----------------------|-----------------------------------------------------------|
| CI-08-001 a CI-08-003 | Preparaciones, depósitos, cálculos e índices.             |
| CI-08-004 a CI-08-006 | Instrucciones, ocurrencias, pagos y renovaciones.         |
| CI-08-007 a CI-08-009 | Precancelación, correcciones y seguimiento.               |
| CI-08-010 a CI-08-012 | Instrumentos, clases, versiones, revisión y habilitación. |
| CI-08-013 y CI-08-014 | Valores de referencia y eventos económicos.               |
| CI-08-015 y CI-08-016 | Cuentas, titularidad, operadores y facultades.            |
| CI-08-017 y CI-08-018 | Órdenes y recursos.                                       |
| CI-08-019             | Compromisos de unidades.                                  |
| CI-08-020 a CI-08-023 | Ejecuciones y resultados externos.                        |
| CI-08-024             | Cancelaciones y resolución de remanentes.                 |
| CI-08-025 y CI-08-026 | Liquidaciones y confirmaciones.                           |
| CI-08-027             | Consulta de orden y seguimiento.                          |
| CI-08-028 y CI-08-029 | Movimientos y restricciones.                              |
| CI-08-030 y CI-08-031 | Cortes, posiciones y valuación.                           |
| CI-08-032 y CI-08-033 | Derechos, recepciones y aplicaciones.                     |
| CI-08-034 a CI-08-037 | Conciliación y correcciones.                              |
| CI-08-038 a CI-08-041 | Traslados, hechos y resolución.                           |
| CI-08-042             | Cierre e inventarios.                                     |
| CI-08-043 a CI-08-047 | Outbox e historia de agregados.                           |

## **35. Escenarios de verificación de implementación**

| **Escenario**                                    | **Resultado exigido**                                  |
|--------------------------------------------------|--------------------------------------------------------|
| Depósito 1000 al 36,5 % durante treinta días     | Interés nominal 30 antes de tributos.                  |
| Débito confirmado y constitución pendiente       | Completa o restituye sin otro débito.                  |
| Falta índice UVA                                 | No presenta liquidación supuesta como definitiva.      |
| Renovación de capital 1000 y rendimiento neto 30 | Sucesor 1030 sin disponibilidad simultánea.            |
| Salida anticipada                                | Respeta modalidad, permanencia y preaviso.             |
| Pago y renovación concurrentes                   | No aplican dos veces el mismo importe.                 |
| Compra de 1000 nominales a 95                    | Principal 950, con interés corrido y cargos separados. |
| Cargos superiores al máximo                      | No amplía silenciosamente cobertura.                   |
| Posición 100 y compromiso 30                     | Registrado 100 y disponible 70 sin otras causas.       |
| Salida de esas 30 unidades                       | Registrado 70 y compromiso consumido una vez.          |
| Causas superpuestas                              | Descuenta una sola vez la misma porción.               |
| Liquidación pendiente                            | No informa disponibilidad completa.                    |
| Cancelación incierta                             | Mantiene recursos y seguimiento.                       |
| Ejecución parcial                                | Conserva identidad y liquidación de cada parcial.      |
| Movimiento posterior con fecha anterior          | No altera páginas de un corte fijado.                  |
| Precio ausente                                   | No lo sustituye por cero.                              |
| Evento económico corregido                       | Ajusta el mismo derecho mediante diferencias.          |
| Amortización de nominal duplicada                | Un único efecto sobre posición.                        |
| Traslado 30 con salida 20                        | Conserva 20 en tránsito y 10 sin enviar.               |
| Rechazo posterior al envío                       | No restituye disponibilidad sin retorno.               |
| Derecho cobrado después del traslado             | Conserva beneficiario y obligación residual.           |
| Traslado interno                                 | Confirma salida y entrada conjuntamente.               |
| Corrección de valuación                          | No crea compraventa ficticia.                          |
| Cierre con derechos pendientes                   | Conserva régimen residual y vía de entrega.            |
| Respuesta perdida o evento duplicado             | Recupera efectos sin repetir dinero ni unidades.       |

Las restricciones, exclusiones, conservación de cantidades, bloqueos y transacciones se verifican durante la
implementación con PostgreSQL real y escenarios concurrentes.
