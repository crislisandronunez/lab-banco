# Entidades del microservicio Transferencias

**Microservicio técnico:** `transfer-service`  
**Laboratorio:** `lab-transferencias`  
**Módulo:** 5 — Pagos y transferencias  
**Base de datos:** `transfer_db`  
**Fuentes:** Guía de implementación (`guia_transferencias.docx`), modelo de negocio (definición de módulos, arquitectura técnica y contratos técnicos de Pagos y transferencias).

---

## 1. Agregado raíz y grupos de dominio

El núcleo del dominio gira en torno al **agregado de transferencia** (`Transfer` / `TransferOrder`), identificado de forma estable por `transferId`. Conserva modalidad, referencia del originante, ordenante, cuenta de origen (si aplica), principal, moneda, finalidad, destino aceptado, resumen de autorización, conceptos, estado, pendientes y control de versiones/concurrencia.

Los resultados monetarios y externos se mantienen en estructuras vinculadas; no se reduce el resultado a un simple booleano “transferida”.

| Grupo de dominio | Descripción / regla clave |
|---|---|
| **Transfer / TransferOrder** | Identidad estable (`transferId`), tipo `INTERNAL` / `OUTGOING` / `INCOMING`, estado y control de concurrencia (versión). |
| **AcceptedDestination** | Snapshot inmutable del destino aceptado: cuenta interna o participante externo, moneda y huella (`acceptedContentHash`) del contenido autorizado. |
| **ExternalOperationReference** | Unicidad por participante + mecanismo + referencia externa, independiente de la versión del mecanismo. |
| **Monetary execution** | Referencias de actuación de Fondos, efecto débito/crédito, grupo monetario, reserva y resultado. |
| **Outgoing execution** | Cobertura, despacho, aceptación irrevocable, acreditación y evidencia de consumo. |
| **Incoming acceptance** | Comprobación de destino/moneda/controles, aceptación firme y efecto de crédito único. |
| **Cancellation** | Identidad, petición, razón, estado y evidencia de cancelación o incertidumbre. |
| **Return control / Return** | Control del principal comprometido, devuelto y remanente; efecto propio por cada devolución. |

---

## 2. Entidades / tablas propias de `transfer_db`

### 2.1 Identidad y destinos

| Tabla / entidad | Propósito principal |
|---|---|
| `transfer_order` | Instrucción de transferencia (agregado raíz). Contiene `transferId`, tipo, ordenante, origen, principal, moneda, finalidad, estado, versión, etc. |
| `transfer_accepted_destination` | Snapshot inmutable del destino aceptado (interno o externo) y su huella de contenido. |
| `transfer_external_operation` | Referencia externa única por `(participant_id, mechanism_code, external_operation_id)`. |

### 2.2 Ejecución y resultados

| Tabla / entidad | Propósito principal |
|---|---|
| `transfer_monetary_execution` | Actuación monetaria con Fondos (única por `funds_service` + `remote_action_id`). |
| `transfer_outgoing_execution` | Estado y evidencias de la ejecución saliente (cobertura, despacho, aceptación, acreditación). |
| `transfer_incoming_acceptance` | Aceptación firme de transferencia entrante y `credit_effect_id` único. |

### 2.3 Cancelación y retorno

| Tabla / entidad | Propósito principal |
|---|---|
| `transfer_cancellation` | Solicitud y resultado de cancelación (con barrera frente a despacho). |
| `transfer_return_control` | Control agregado del principal original, devuelto, pendiente y remanente. |
| `transfer_return` | Cada devolución individual (`return_effect_id` único). |

### 2.4 Recuperación remota

| Tabla / entidad | Propósito principal |
|---|---|
| `transfer_remote_step` | Intención persistida antes de invocar un sistema remoto (mismo `remote_action_id` / `effect_id` al recuperar). |
| `transfer_return_step` | Pasos de recuperación específicos de devoluciones. |

### 2.5 Hechos comprobados

| Tabla / entidad | Propósito principal |
|---|---|
| `transfer_fact` | Hechos confirmados (`ConfirmedFact`): RECEIVED, ACCEPTED_FOR_EXECUTION, DISPATCHED, IRREVOCABLY_ACCEPTED, INSTRUMENT_EXECUTED, BENEFICIARY_CREDITED, RETURNED, EXTERNALLY_SETTLED, etc. |

### 2.6 Infraestructura transversal (común a microservicios)

| Tabla / entidad | Propósito principal |
|---|---|
| `service_action` | Actuaciones / acciones del servicio (trazabilidad de comandos). |
| `idempotency_record` | Claves de idempotencia para comandos persistentes. |
| `business_change_history` | Historial de cambios de negocio. |
| `outbox_event` | Eventos a publicar (patrón Outbox). |
| `inbox_message` | Mensajes recibidos (patrón Inbox / deduplicación). |
| `persistent_job` | Trabajos de recuperación / reintentos persistentes. |

---

## 3. Atributos clave del agregado Transfer (resumen)

| Campo | Regla / descripción |
|---|---|
| `transferId` | UUID estable (identidad del agregado). |
| `transferType` | `INTERNAL`, `OUTGOING` o `INCOMING`. |
| `originInstructionReference` | Referencia única del originante. |
| `orderingPartyId` / `orderingPartyReference` | Ordenante. |
| `sourceAccountId` | Cuenta de origen (obligatoria en INTERNAL y OUTGOING). |
| `destination` / `resolvedDestinationSnapshot` | Destino tipado (interno o externo) + snapshot aceptado. |
| `principalAmount` | Importe del principal (positivo). |
| `currency` | Moneda del traslado (ARS / USD). |
| `acceptedContentHash` | Resumen SHA-256 del contenido autorizado. |
| `chargeDeterminationId` | Determinación de cargos y tributos. |
| `reservationId` | Referencia de cobertura en Fondos. |
| `monetaryEffectReferences` | Efectos monetarios de principal y cargos. |
| `exchangeId` | Intercambio externo (cuando aplica). |
| `mechanismVersion` | Versión del mecanismo externo. |
| `version` | Control de concurrencia optimista. |
| `processingState` | Estados: RECEIVED, VALIDATING, PREPARING, EXECUTING, COMPLETED, CANCELLED, REJECTED, REQUIRES_RESOLUTION. |

### Destino aceptado (`AcceptedDestination`)

- `destinationType`: `INTERNAL` o `EXTERNAL`.
- Para interno: referencia de cuenta.
- Para externo: participante, identificador bancario, resolución, beneficiario, etc.
- `currency`, `acceptedContentHash`, `acceptedAt`.

### Referencia de operación externa (`ExternalOperationReference`)

- `participantId`
- `mechanismCode`
- `mechanismVersion`
- `externalOperationId`

---

## 4. Restricciones de consistencia relevantes

- `transfer_order`: principal > 0; INTERNAL exige origen ≠ destino; OUTGOING exige origen; INCOMING exige destino + evidencia de recepción.
- `transfer_accepted_destination`: contenido autorizado inmutable; campos INTERNAL/EXTERNAL mutuamente consistentes.
- `transfer_external_operation`: única por (participante, mecanismo, referencia externa).
- `transfer_monetary_execution`: única por (servicio de fondos, remote_action_id).
- `transfer_incoming_acceptance`: `credit_effect_id` único; fecha obligatoria en aceptación firme.
- `transfer_cancellation`: protección con `dispatch_barrier` frente a creación/despacho.
- `transfer_return_control`: `original_principal = returned_amount + pending_amount + remaining_amount` (bajo concurrencia).
- `transfer_return`: `return_effect_id` único; la incertidumbre mantiene reservado el importe.
- `transfer_remote_step`: intención escrita antes de la llamada remota; misma identidad al recuperar.

Todas las tablas relevantes incorporan bloques comunes de columnas de auditoría/creación (T), versión/bloqueo (C), esquema/huella (S) y origen (O) según el diseño de persistencia.

---

## 5. Dimensiones de estado (no se colapsan en un único estado)

| Dimensión | Estados principales |
|---|---|
| Instrucción | Recibida, en comprobación, aceptada para ejecución, cancelada o rechazada. |
| Ejecución monetaria | No iniciada, reservada, confirmada, incierta o regularización pendiente. |
| Intercambio | Preparado, en despacho, enviado, recibido o incierto. |
| Resultado del participante | Pendiente, aceptado irrevocablemente, acreditado o rechazado definitivamente. |
| Devolución | No solicitada, pendiente, parcial o completada. |

---

## 6. Relaciones con otros microservicios (límites de autoridad)

- **Fondos**: determina disponibilidad y ejecuta movimientos/reservas.
- **Identificación bancaria**: resuelve CBU/alias.
- **Compensación y conciliación de pagos**: intercambio externo y resultados.
- **Cuentas / Representación**: condiciones y facultades.
- **Riesgo y cumplimiento**: controles y restricciones.
- **Tesorería**: liquidaciones entre entidades.
- **Pagos**: puede utilizar una transferencia como instrumento de ejecución.
- **Atención y operación administrativa**: comprobantes y avisos.

Transferencias **no** administra la obligación comercial subyacente ni modifica saldos directamente.

---

*Documento generado a partir del análisis de la documentación vigente del repositorio `lab-banco` (modelo-de-negocio y guía de transferencias).*
