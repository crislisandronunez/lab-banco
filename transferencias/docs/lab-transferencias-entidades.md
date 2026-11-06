# Transfer Service — Modelo de dominio

## 1. Objetivo

Este documento define el dominio de `transfer-service`: las clases, los atributos, las reglas de negocio,
las enumeraciones, los objetos de valor y las relaciones necesarias para implementar `domain/`.

El servicio recibe instrucciones de transferencia, conserva el contenido autorizado, coordina su ejecución,
registra hechos comprobados y resuelve cancelaciones, devoluciones y resultados inciertos.

El diseño distingue tres conceptos:

- **Transferencia:** instrucción de negocio cuya identidad y contenido se conservan.
- **Actuación:** petición identificada que modifica o consulta el avance de una operación.
- **Efecto monetario:** débito, crédito o reserva ejecutado por el servicio responsable de los fondos.

Una transferencia puede contener varias actuaciones y relacionarse con varios efectos. Son identidades diferentes.

La distribución de clases presentada aquí es una organización Java del dominio. Las reglas empresariales descritas
son obligatorias; los nombres de métodos son una forma clara de expresarlas y pueden adaptarse sin cambiar la lógica.

## 2. Responsabilidad y límites

`Transfer` es la **raíz del agregado principal**. Su responsabilidad es conservar una instrucción bancaria y
controlar las decisiones que afectan a su ciclo de vida.

### 2.1. Responsabilidades propias

- Identificar transferencia, ordenante, origen, destino, principal, moneda y finalidad.
- Conservar el destino resuelto que fue presentado y aceptado.
- Conservar las autorizaciones vinculadas al contenido exacto de la instrucción.
- Diferenciar y registrar controles, actuaciones, hechos comprobados y resultados inciertos.
- Incorporar los resultados de la ejecución de transferencias internas y externas.
- Conservar las referencias y evidencias de reservas, débitos y créditos, sin administrar saldos.
- Ordenar cancelaciones frente al despacho y conservar sus resultados.
- Controlar devoluciones parciales, totales, duplicadas y simultáneas.
- Mantener identidades estables para idempotencia y recuperación ante fallos.
- Conservar la historia y los datos necesarios para comunicar hechos del servicio.

La coordinación de los casos de uso y las solicitudes a Fondos y Compensación corresponden a la capa de
aplicación. El agregado `Transfer` controla sus invariantes e incorpora los resultados comprobados,
sin invocar directamente a las autoridades externas.

### 2.2. Responsabilidades que no se incorporan

| Información o efecto                                | Autoridad del negocio                      |
|-----------------------------------------------------|--------------------------------------------|
| Titularidad, estado y permisos de una cuenta        | Cuentas                                    |
| Saldos, disponibilidad, reservas y movimientos      | Fondos                                     |
| Resolución de CBU y alias                           | Identificación bancaria                    |
| Facultades de representantes                        | Representación                             |
| Evaluaciones de riesgo y restricciones              | Servicio competente de riesgo/cumplimiento |
| Intercambio externo, mensajes y conciliación de red | Compensación y conciliación de pagos       |
| Liquidación financiera entre participantes          | Tesorería                                  |
| Deuda comercial y aplicación de obligaciones        | Pagos o servicio acreedor                  |
| Asientos contables                                  | Contabilidad                               |

Por lo tanto, no crear `Account`, `Balance`, `Customer`, `Reservation` ni `LedgerEntry` como entidades propias.
Se conservan las referencias y evidencias recibidas de las autoridades correspondientes.

## 3. Estructura de `domain/`

```text
domain/
├── model/
│   ├── Transfer.java
│   ├── TransferExternalOperation.java
│   ├── TransferMonetaryExecution.java
│   ├── TransferOutgoingExecution.java
│   ├── TransferIncomingAcceptance.java
│   ├── TransferCancellation.java
│   ├── TransferReturnControl.java
│   ├── TransferReturn.java
│   ├── TransferRemoteStep.java
│   ├── TransferReturnStep.java
│   └── TransferFact.java
├── enums/
│   ├── Currency.java
│   ├── TransferType.java
│   ├── ProcessingState.java
│   ├── DestinationType.java
│   ├── AuthorizationBasisType.java
│   ├── MonetaryComponentType.java
│   ├── MonetaryExecutionStatus.java
│   ├── OutgoingCoverageStatus.java
│   ├── IncomingAcceptanceStatus.java
│   ├── CancellationOutcome.java
│   ├── ReturnStatus.java
│   ├── RemoteStepStatus.java
│   └── ConfirmedFactType.java
└── valueobject/
    ├── AcceptedDestination.java
    ├── AuthorizationBasis.java
    ├── ExternalOperationReference.java
    ├── ObjectReference.java
    ├── MonetaryConcept.java
    ├── ConfirmedFact.java
    ├── PendingComponent.java
    ├── ReturnDestination.java
    ├── CheckEvidence.java
    └── StructuredContent.java
```

### 3.1. Qué representa cada categoría

- `model/`: la raíz del agregado, sus entidades subordinadas y componentes con identidad o seguimiento propio.
- `enums/`: conjuntos cerrados de valores que restringen modalidades y resultados del negocio.
- `valueobject/`: datos definidos por su contenido, sin identidad empresarial propia y preferentemente inmutables.

El árbol muestra una organización Java de referencia. `ExternalOperationReference.java` es una posible
representación tipada, no una obligación de duplicar la identidad ya conservada por `TransferExternalOperation`.

`Transfer` y `TransferOrder` son dos nombres para **la misma instrucción**, no dos agregados separados.
En este modelo se utiliza `Transfer` como nombre de clase.

Una tabla en PostgreSQL no obliga a crear una entidad de dominio distinta. Tampoco se debe transformar cada clase
incluida en `domain/model/` en una entidad JPA: la persistencia pertenece a la capa de infraestructura.

### 3.2. Relaciones del agregado

| Origen           | Componente                   | Cardinalidad |
|------------------|------------------------------|--------------|
| `Transfer`       | `AcceptedDestination`        | 0..1         |
| `Transfer`       | `TransferExternalOperation`  | 0..1         |
| `Transfer`       | `TransferMonetaryExecution`  | 0..1         |
| `Transfer`       | `TransferOutgoingExecution`  | 0..1         |
| `Transfer`       | `TransferIncomingAcceptance` | 0..1         |
| `Transfer`       | `TransferCancellation`       | 0..N         |
| `Transfer`       | `TransferReturnControl`      | 0..1         |
| `Transfer`       | `TransferReturn`             | 0..N         |
| `Transfer`       | `TransferRemoteStep`         | 0..N         |
| `TransferReturn` | `TransferReturnStep`         | 0..N         |
| `Transfer`       | `TransferFact`               | 0..N         |

Las relaciones expresan pertenencia de información dentro de Transferencias. No representan asociaciones con
entidades JPA pertenecientes a otros microservicios.

## 4. Entidades y componentes del dominio

### 4.1. `Transfer`

**Tipo:** raíz del agregado.

**Responsabilidad:** representar la instrucción y controlar sus invariantes durante las actuaciones de negocio.
No realiza llamadas HTTP, operaciones SQL ni movimientos de saldo.

#### Identidad y origen

| Atributo                 | Tipo Java         | Regla                                        |
|--------------------------|-------------------|----------------------------------------------|
| `transferId`             | `UUID`            | Identidad única, estable e irrepetible.      |
| `transferType`           | `TransferType`    | `INTERNAL`, `OUTGOING` o `INCOMING`.         |
| `originService`          | `String`          | Servicio que originó la instrucción.         |
| `originType`             | `String`          | Tipo de origen; código no vacío.             |
| `originReference`        | `String`          | Referencia original de hasta 120 caracteres. |
| `orderingPartyReference` | `ObjectReference` | Ordenante identificado.                      |
| `receivedAt`             | `Instant`         | Recepción efectiva de la instrucción.        |

`originService`, `originType` y `originReference` forman una identidad de origen. Debe existir unicidad para evitar
que una misma instrucción recibida varias veces se convierta en transferencias distintas.

#### Contenido monetario y destino

| Atributo               | Tipo Java               | Regla                                          |
|------------------------|-------------------------|------------------------------------------------|
| `sourceAccountId`      | `UUID`                  | Origen interno para `INTERNAL` y `OUTGOING`.   |
| `destinationAccountId` | `UUID`                  | Destino interno para `INTERNAL` y `INCOMING`.  |
| `acceptedDestination`  | `AcceptedDestination`   | Destino aceptado para interna o saliente.      |
| `principalAmount`      | `BigDecimal`            | Principal estrictamente positivo.              |
| `currency`             | `Currency`              | `ARS` o `USD`.                                 |
| `purposeCode`          | `String`                | Finalidad codificada; hasta 80 caracteres.     |
| `authorizationBasis`   | `AuthorizationBasis`    | Fundamento de autorización cuando corresponde. |
| `acceptedContentHash`  | `String`                | SHA-256 hexadecimal, 64 caracteres.            |
| `concepts`             | `List<MonetaryConcept>` | Principal, comisiones, tributos y ajustes.     |

El principal no equivale al total de los cargos. Los conceptos monetarios se conservan por separado.
El destino aceptado no debe cambiar cuando otro servicio modifica un alias o un dato posterior.

#### Procesamiento, comprobaciones e historia

| Atributo                   | Tipo Java                | Regla                                               |
|----------------------------|--------------------------|-----------------------------------------------------|
| `processingState`          | `ProcessingState`        | Situación general del procesamiento.                |
| `dispatchBarrier`          | `boolean`                | Impide despacho tardío tras cancelación confirmada. |
| `pendingComponents`        | `List<PendingComponent>` | Componentes cuyo resultado debe resolverse.         |
| `confirmedFacts`           | `List<ConfirmedFact>`    | Hechos comprobados con evidencia.                   |
| `monetaryEffectReferences` | `List<ObjectReference>`  | Referencias monetarias de Fondos.                   |
| `version`                  | `long`                   | Versión de negocio positiva.                        |
| `lockVersion`              | `long`                   | Versión de concurrencia no negativa.                |
| `createdActionId`          | `UUID`                   | Actuación que creó la instrucción.                  |
| `createdAt`                | `Instant`                | Creación del registro.                              |
| `updatedAt`                | `Instant`                | Última modificación.                                |

Las colecciones pueden componerse desde las entidades vinculadas, sin duplicar copias modificables en memoria.
Una lista vacía de pendientes no demuestra por sí sola que todos los efectos monetarios hayan sido confirmados.

#### Reglas obligatorias

1. `principalAmount` debe ser mayor que cero y tener como máximo dos decimales.
2. `INTERNAL` requiere cuenta de origen y destino, ambas presentes y diferentes.
3. `OUTGOING` requiere cuenta de origen y destino externo aceptado.
4. `INCOMING` requiere cuenta de destino y evidencia identificable de recepción externa.
5. La misma identidad con idéntico contenido debe recuperar la operación original.
6. La misma identidad con distinto contenido debe generar conflicto, nunca otro efecto económico.
7. La autorización debe corresponder al contenido exacto aceptado.
8. No se convierte moneda implícitamente: una conversión exige una operación cambiaria identificada.
9. No se considera acreditada una transferencia por el solo hecho de recibir un acuse técnico.
10. Una respuesta perdida no autoriza a rechazar ni volver a ejecutar con otra identidad económica.
11. Una cancelación previa confirmada debe impedir todo despacho posterior.
12. Una acreditación ya confirmada no se elimina ni se convierte en cancelación ficticia.
13. Las devoluciones conservan la transferencia original, su identidad y el remanente económico.
14. Las modificaciones concurrentes deben respetar versión y decisión previamente confirmada.

#### Comportamientos de dominio

Se recomienda expresar los cambios mediante métodos con significado empresarial, no setters públicos generales:

- `acceptInstruction(...)`: admite la instrucción sin perder su identidad y contenido.
- `fixAcceptedDestination(...)`: fija el destino que fue aceptado.
- `recordAuthorization(...)`: incorpora el fundamento asociado a la huella autorizada.
- `recordMonetaryOutcome(...)`: refleja un resultado de Fondos con su evidencia.
- `recordConfirmedFact(...)`: incorpora un hecho comprobado sin repetirlo.
- `recordPendingComponent(...)`: identifica una incertidumbre concreta y su responsable.
- `protectDispatch(...)`: comprueba la barrera y la cobertura requerida para iniciar despacho.
- `applyCancellation(...)`: incorpora una actuación de cancelación de forma consistente.
- `registerReturn(...)`: vincula una devolución sin exceder el principal disponible.

Las firmas son orientativas. Un método del agregado recibe resultados y evidencias, no clientes HTTP ni repositorios.

### 4.2. `TransferExternalOperation`

**Tipo:** componente subordinado con identidad externa contextualizada.

**Responsabilidad:** conservar cómo identifica el participante externo a la transferencia y relacionarla con el
intercambio administrado por Compensación.

| Atributo              | Tipo Java         | Regla                                     |
|-----------------------|-------------------|-------------------------------------------|
| `transferId`          | `UUID`            | Transferencia propietaria.                |
| `participantId`       | `UUID`            | Participante externo.                     |
| `mechanismCode`       | `String`          | Mecanismo, hasta 80 caracteres.           |
| `mechanismVersion`    | `long`            | Versión positiva del mecanismo utilizado. |
| `externalOperationId` | `String`          | Referencia externa, hasta 120 caracteres. |
| `externalContentHash` | `String`          | Hash del contenido recibido/enviado.      |
| `exchangeReference`   | `ObjectReference` | Intercambio relacionado, cuando existe.   |
| `schemaVersion`       | `long`            | Versión positiva del esquema.             |

**Clave externa:** `(participantId, mechanismCode, externalOperationId)`.

- `mechanismVersion` no forma parte de esa clave.
- Una repetición con igual clave y contenido recupera la misma operación.
- Una repetición con igual clave y otro contenido es una inconsistencia, no otra transferencia.
- El cambio de versión del mecanismo no debe producir una segunda acreditación.
- El componente referencia intercambios, pero no implementa la lógica de compensación.

**Comportamientos:** `matchesExternalIdentity(...)`, `verifyContentConsistency(...)` y
`attachExchangeReference(...)`.

### 4.3. `TransferMonetaryExecution`

**Tipo:** componente subordinado de ejecución monetaria.

**Responsabilidad:** representar una actuación solicitada a Fondos y su resultado verificable, sin mantener
saldos ni efectuar débitos/créditos directamente.

| Atributo                    | Tipo Java                 | Regla                                     |
|-----------------------------|---------------------------|-------------------------------------------|
| `transferId`                | `UUID`                    | Transferencia propietaria.                |
| `fundsService`              | `String`                  | Autoridad monetaria.                      |
| `remoteActionId`            | `UUID`                    | Actuación remota estable.                 |
| `movementGroupId`           | `UUID`                    | Grupo indivisible, cuando corresponde.    |
| `debitEffectId`             | `UUID`                    | Identidad del débito, si corresponde.     |
| `creditEffectId`            | `UUID`                    | Identidad del crédito, si corresponde.    |
| `reservationReference`      | `ObjectReference`         | Reserva administrada por Fondos.          |
| `fxReference`               | `ObjectReference`         | Cambio de moneda identificado, si existe. |
| `requestedContent`          | `StructuredContent`       | Petición original enviada a Fondos.       |
| `status`                    | `MonetaryExecutionStatus` | Resultado monetario específico.           |
| `movementReferences`        | `List<ObjectReference>`   | Movimientos informados por Fondos.        |
| `executionAttemptReference` | `ObjectReference`         | Intento recuperable, cuando existe.       |

La petición original debe conservarse con sus versiones y referencias para que la recuperación sea exacta.

**Invariantes:**

- Una transferencia interna requiere confirmación conjunta de débito y crédito por Fondos.
- Una reserva previa consumida no origina un segundo débito.
- `remoteActionId` y los identificadores de efecto no cambian durante un reintento o recuperación.
- `CONFIRMED` requiere referencias y evidencia suficiente de la autoridad monetaria.
- `UNCERTAIN` no significa que el efecto haya fallado; obliga a consultar la actuación original.
- Si existe conversión expresa, debe conservarse su referencia, las monedas y los importes aceptados.

**Comportamientos:** `registerRequest(...)`, `confirmWithEvidence(...)`, `rejectWithEvidence(...)` y
`markUncertain(...)`.

### 4.4. `TransferOutgoingExecution`

**Tipo:** componente subordinado de salida externa.

**Responsabilidad:** conservar cobertura, despacho, aceptación irrevocable, acreditación y consumo de la reserva.

| Atributo                  | Tipo Java                | Regla                                         |
|---------------------------|--------------------------|-----------------------------------------------|
| `transferId`              | `UUID`                   | Transferencia propietaria.                    |
| `mechanismReference`      | `ObjectReference`        | Mecanismo y versión utilizados.               |
| `reservationReference`    | `ObjectReference`        | Reserva obligatoria de Fondos.                |
| `coverageStatus`          | `OutgoingCoverageStatus` | Situación de cobertura monetaria.             |
| `exchangeReference`       | `ObjectReference`        | Intercambio administrado externamente.        |
| `dispatchAuthorizedAt`    | `Instant`                | Autorización de despacho, si existe.          |
| `irrevocablyAcceptedAt`   | `Instant`                | Aceptación firme comprobada.                  |
| `beneficiaryCreditedAt`   | `Instant`                | Acreditación comprobada.                      |
| `creditEvidenceReference` | `ObjectReference`        | Evidencia asociada a esa acreditación.        |
| `consumptionStepId`       | `UUID`                   | Paso remoto de consumo de reserva.            |
| `pendingComponents`       | `List<PendingComponent>` | Resultados que siguen requiriendo resolución. |
| `updatedAt`               | `Instant`                | Momento de modificación.                      |
| `lockVersion`             | `long`                   | Control de concurrencia.                      |

**Invariantes:**

1. Una saliente no se despacha sin reserva suficiente y cobertura protegida.
2. La intención de despacho debe quedar persistida antes de realizar la llamada externa.
3. La barrera de cancelación se comprueba al crear o habilitar un intercambio.
4. `DISPATCHED` demuestra salida al mecanismo; no acredita el destino.
5. La aceptación irrevocable mantiene un compromiso firme de cobertura.
6. Solo la acreditación comprobada permite consumir la reserva conforme al contrato.
7. `beneficiaryCreditedAt` y `creditEvidenceReference` deben incorporarse conjuntamente.
8. La incertidumbre posterior al despacho no permite liberar cobertura por transcurso de tiempo.
9. Un rechazo definitivo exige demostrar ausencia de crédito y de ejecución futura pendiente.
10. Una falla posterior de liquidación externa no revierte una acreditación confirmada.
11. Si ya hubo crédito externo pero el débito local es incierto, se recupera el efecto local original.

**Comportamientos:** `registerCoverage(...)`, `protectCoverage(...)`, `authorizeDispatch(...)`,
`recordIrrevocableAcceptance(...)`, `recordBeneficiaryCredit(...)` y `linkReservationConsumption(...)`.

### 4.5. `TransferIncomingAcceptance`

**Tipo:** componente subordinado de recepción externa.

**Responsabilidad:** conservar comprobaciones de destino y moneda, controles, aceptación firme e identidad del crédito.

| Atributo                | Tipo Java                  | Regla                                    |
|-------------------------|----------------------------|------------------------------------------|
| `transferId`            | `UUID`                     | Transferencia propietaria.               |
| `destinationCheck`      | `CheckEvidence`            | Comprobación de destino.                 |
| `currencyCheck`         | `CheckEvidence`            | Comprobación de moneda.                  |
| `controlEvidence`       | `CheckEvidence`            | Restricciones y controles pertinentes.   |
| `messageReference`      | `ObjectReference`          | Mensaje recibido y verificable.          |
| `acceptanceStatus`      | `IncomingAcceptanceStatus` | Pendiente, aceptada o rechazada.         |
| `irrevocablyAcceptedAt` | `Instant`                  | Fecha obligatoria al aceptar firmemente. |
| `creditEffectId`        | `UUID`                     | Efecto de crédito único.                 |

`destinationCheck`, `currencyCheck` y `controlEvidence` conservan resultados y referencias de autoridad.
Un `CheckEvidence` no autoriza por sí mismo a saltar preparaciones o verificaciones vigentes de otros servicios.

**Invariantes:**

- El participante y la identidad externa deben estar reconocidos antes de procesar la recepción.
- Se valida destino, moneda y restricciones antes de aceptar irrevocablemente.
- Una cuenta inexistente o un destino inválido se rechaza antes de asumir compromiso firme.
- Una restricción exclusiva de débitos no se interpreta como una prohibición automática de créditos.
- `ACCEPTED` exige `irrevocablyAcceptedAt` y obligación de acreditar o resolver la devolución correspondiente.
- `creditEffectId` debe ser único; una respuesta perdida no habilita una segunda acreditación.
- Los mensajes duplicados con contenido incompatible se investigan sin alterar la operación original.

**Comportamientos:** `recordChecks(...)`, `acceptIrrevocably(...)`, `rejectBeforeAcceptance(...)` y
`associateUniqueCreditEffect(...)`.

### 4.6. `TransferCancellation`

**Tipo:** entidad subordinada identificada.

**Responsabilidad:** conservar cada solicitud de cancelación, la autorización, la coordinación con el despacho y
el resultado confirmado o incierto.

| Atributo             | Tipo Java               | Regla                                          |
|----------------------|-------------------------|------------------------------------------------|
| `cancellationId`     | `UUID`                  | Identidad de la actuación.                     |
| `transferId`         | `UUID`                  | Transferencia original.                        |
| `receivedAt`         | `Instant`               | Recepción efectiva de la solicitud.            |
| `reason`             | `String`                | Fundamento obligatorio, hasta 4000 caracteres. |
| `authorizationBasis` | `AuthorizationBasis`    | Actor y facultades comprobables.               |
| `exchangeReference`  | `ObjectReference`       | Intercambio externo relacionado.               |
| `cancellationStepId` | `UUID`                  | Paso remoto, cuando corresponde.               |
| `outcome`            | `CancellationOutcome`   | Resultado de la cancelación.                   |
| `finalEvidence`      | `List<ObjectReference>` | Evidencias de resolución.                      |
| `version`            | `long`                  | Versión de la actuación.                       |
| `lockVersion`        | `long`                  | Control de concurrencia.                       |

**Invariantes:**

- Cancelación y despacho compiten sobre la misma instrucción; deben ordenarse mediante la transferencia.
- Si se confirma la cancelación antes de crear el intercambio, `dispatchBarrier` impide un despacho posterior.
- Si el intercambio ya existe, primero debe resolverse si todavía puede despacharse.
- Una petición de cancelación no equivale a la confirmación de que la operación se detuvo.
- Si hubo envío, se solicita y verifica cancelación mediante el mecanismo externo.
- Una respuesta `NOT_RECEIVED` aislada no prueba que una instrucción no pueda ejecutarse después.
- No se libera cobertura sin evidencia de ausencia de efectos o compromisos pendientes.
- Si ya hubo acreditación, corresponde gestionar una devolución, no borrar el hecho confirmado.

**Comportamientos:** `registerCancellationRequest(...)`, `recordExternalResponse(...)`,
`confirmCancellation(...)` y `recordUncertainOutcome(...)`.

### 4.7. `TransferReturnControl`

**Tipo:** componente subordinado con control económico concurrente.

**Responsabilidad:** impedir que varias devoluciones consuman el mismo remanente del principal original.

| Atributo            | Tipo Java    | Regla                                           |
|---------------------|--------------|-------------------------------------------------|
| `transferId`        | `UUID`       | Transferencia original.                         |
| `originalPrincipal` | `BigDecimal` | Principal que puede controlarse.                |
| `returnedAmount`    | `BigDecimal` | Total ya devuelto y confirmado.                 |
| `pendingAmount`     | `BigDecimal` | Total comprometido por devoluciones pendientes. |
| `remainingAmount`   | `BigDecimal` | Remanente disponible.                           |
| `lockVersion`       | `long`       | Protección de concurrencia.                     |
| `updatedAt`         | `Instant`    | Modificación del control.                       |

**Invariante aritmética:**

```text
originalPrincipal = returnedAmount + pendingAmount + remainingAmount
returnedAmount >= 0
pendingAmount >= 0
remainingAmount >= 0
```

Ejemplo: principal 100, devoluciones confirmadas 40 y devoluciones comprometidas 20.
El importe disponible para nuevas devoluciones es **40**, no 60.

**Comportamientos:**

- `commitReturnAmount(amount)`: resta del remanente y agrega a los importes comprometidos.
- `confirmReturnAmount(amount)`: traslada un importe comprometido a devuelto confirmado.
- `releaseRejectedReturn(amount)`: devuelve al remanente un importe cuyo rechazo fue comprobado.
- `availableForReturn()`: consulta el remanente sin modificar saldos ni crear una devolución.

Un resultado incierto conserva el importe comprometido. Las operaciones concurrentes requieren una decisión atómica.

### 4.8. `TransferReturn`

**Tipo:** entidad subordinada con identidad económica propia.

**Responsabilidad:** representar una devolución vinculada a una transferencia acreditada, sin modificar ni borrar
la transferencia original.

| Atributo             | Tipo Java               | Regla                                   |
|----------------------|-------------------------|-----------------------------------------|
| `returnId`           | `UUID`                  | Identidad de la devolución.             |
| `transferId`         | `UUID`                  | Transferencia original.                 |
| `returnEffectId`     | `UUID`                  | Identidad económica única y estable.    |
| `amount`             | `BigDecimal`            | Positivo; dentro del remanente.         |
| `currency`           | `Currency`              | Misma moneda del principal devuelto.    |
| `reasonCode`         | `String`                | Motivo codificado, hasta 80 caracteres. |
| `returnDestination`  | `ReturnDestination`     | Destino de retorno tipado.              |
| `authorizationBasis` | `AuthorizationBasis`    | Fundamento suficiente de la devolución. |
| `status`             | `ReturnStatus`          | Resultado de esta devolución.           |
| `resultReferences`   | `List<ObjectReference>` | Efectos y evidencias vinculados.        |
| `createdActionId`    | `UUID`                  | Actuación original de devolución.       |
| `createdAt`          | `Instant`               | Instante de creación.                   |
| `version`            | `long`                  | Versión de negocio positiva.            |
| `lockVersion`        | `long`                  | Concurrencia no negativa.               |

**Invariantes:**

1. Cada devolución tiene `returnId` y `returnEffectId` propios.
2. La devolución no modifica el principal ni los hechos de la transferencia original.
3. Antes de iniciar el efecto, debe comprometerse su importe en `TransferReturnControl`.
4. Una repetición con el mismo efecto económico recupera el resultado original.
5. Las devoluciones confirmadas y comprometidas no pueden exceder el principal de origen.
6. `UNCERTAIN` conserva el compromiso hasta demostrar el resultado.
7. La petición del ordenante no le concede poder para debitar la cuenta del beneficiario.
8. Los cargos y sus correcciones se manejan por separado del principal.

**Comportamientos:** `registerReturn(...)`, `confirmWithEvidence(...)`, `rejectWithEvidence(...)` y
`recordUncertainty(...)`.

### 4.9. `TransferRemoteStep`

**Tipo:** componente subordinado de recuperación.

**Responsabilidad:** conservar la intención de una actuación remota que pertenece a la transferencia.

| Atributo             | Tipo Java               | Regla                                      |
|----------------------|-------------------------|--------------------------------------------|
| `stepId`             | `UUID`                  | Identidad del paso local.                  |
| `transferId`         | `UUID`                  | Transferencia propietaria.                 |
| `componentCode`      | `String`                | Código de componente, hasta 80 caracteres. |
| `responsibleService` | `String`                | Dependencia que debe resolverlo.           |
| `remoteActionId`     | `UUID`                  | Actuación remota estable.                  |
| `effectId`           | `UUID`                  | Efecto económico, cuando corresponde.      |
| `requestedContent`   | `StructuredContent`     | Contenido exacto solicitado.               |
| `status`             | `RemoteStepStatus`      | Situación del paso remoto.                 |
| `resultReference`    | `ObjectReference`       | Referencia al resultado, cuando existe.    |
| `resultPayload`      | `StructuredContent`     | Resultado devuelto o recuperado.           |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias acumuladas.                     |
| `lastCheckedAt`      | `Instant`               | Última consulta, cuando existe.            |
| `nextCheckAt`        | `Instant`               | Próxima consulta, cuando existe.           |

**Invariantes:**

- La intención y su identidad se conservan antes de invocar al otro servicio.
- Un paso conserva el mismo `remoteActionId`, `effectId` y contenido durante la recuperación.
- Solo puede existir un paso por `(transferId, componentCode)`.
- `(responsibleService, remoteActionId)` es una identidad remota única por tabla.
- Una respuesta perdida se consulta por su referencia original y no causa otra ejecución.
- Una respuesta tardía se incorpora al paso original sin borrar hechos previos.
- La consulta se agenda al detectar incertidumbre, a los 5 minutos y a los 30 minutos.
- Si continúa sin resolución, se investiga y se revisa en cada jornada operativa.
- Ninguno de esos plazos autoriza liberar fondos ni declarar rechazo automáticamente.

**Comportamientos:** `registerIntent(...)`, `recordResult(...)`, `markUncertain(...)` y
`scheduleNextCheck(...)`.

### 4.10. `TransferReturnStep`

**Tipo:** componente subordinado de recuperación de devoluciones.

**Responsabilidad:** conservar la intención de una actuación remota relacionada con una devolución,
permitiendo recuperar sus resultados sin duplicar efectos económicos.

| Atributo             | Tipo Java               | Regla                                      |
|----------------------|-------------------------|--------------------------------------------|
| `stepId`             | `UUID`                  | Identidad única del paso local.            |
| `returnId`           | `UUID`                  | Devolución propietaria.                    |
| `componentCode`      | `String`                | Código de componente, hasta 80 caracteres. |
| `responsibleService` | `String`                | Dependencia responsable de resolverlo.     |
| `remoteActionId`     | `UUID`                  | Actuación remota estable.                  |
| `effectId`           | `UUID`                  | Efecto económico, cuando corresponde.      |
| `requestedContent`   | `StructuredContent`     | Contenido exacto de la solicitud original. |
| `status`             | `RemoteStepStatus`      | Situación del paso remoto.                 |
| `resultReference`    | `ObjectReference`       | Referencia al resultado, cuando existe.    |
| `resultPayload`      | `StructuredContent`     | Resultado devuelto o recuperado.           |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias acumuladas.                     |
| `lastCheckedAt`      | `Instant`               | Última consulta, cuando existe.            |
| `nextCheckAt`        | `Instant`               | Próxima consulta, cuando existe.           |

**Invariantes:**

- La intención y su identidad se conservan antes de invocar al servicio responsable.
- Un paso conserva el mismo `remoteActionId`, `effectId` y contenido durante la recuperación.
- Solo puede existir un paso por `(returnId, componentCode)`.
- `(responsibleService, remoteActionId)` es una identidad remota única por tabla.
- El efecto económico de retorno permanece identificado por `returnEffectId` en `TransferReturn`.
- Una respuesta perdida se consulta mediante la referencia original y no genera otra devolución.
- Una respuesta tardía se incorpora al paso original sin borrar hechos anteriores.
- La consulta se agenda al detectar incertidumbre, a los 5 minutos y a los 30 minutos.
- Si continúa sin resolución, se investiga y se revisa en cada jornada operativa.
- Ninguno de esos plazos autoriza repetir el efecto económico ni liberar importes comprometidos.

**Comportamientos:** `registerIntent(...)`, `recordResult(...)`, `markUncertain(...)` y
`scheduleNextCheck(...)`.

### 4.11. `TransferFact`

**Tipo:** entidad subordinada de historial inmutable.

**Responsabilidad:** conservar hechos confirmados con identidad, procedencia, momento y evidencias.

| Atributo             | Tipo Java               | Regla                                 |
|----------------------|-------------------------|---------------------------------------|
| `factId`             | `UUID`                  | Identidad local del registro.         |
| `transferId`         | `UUID`                  | Transferencia propietaria.            |
| `factType`           | `ConfirmedFactType`     | Hecho específico comprobado.          |
| `sourceService`      | `String`                | Autoridad que informó el hecho.       |
| `sourceFactId`       | `UUID`                  | Identidad del hecho en origen.        |
| `sourceFactVersion`  | `long`                  | Versión positiva del hecho de origen. |
| `occurredAt`         | `Instant`               | Momento efectivo del hecho.           |
| `sourceReference`    | `ObjectReference`       | Objeto responsable del hecho.         |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias verificables.              |

**Clave de deduplicación:**

```text
transferId + sourceService + sourceFactId + sourceFactVersion
```

**Invariantes:**

- Un hecho confirmado se registra una sola vez para una misma identidad y versión de origen.
- Los hechos confirmados son inmutables; las correcciones se expresan mediante nuevas actuaciones vinculadas.
- `RECEIVED`, `DISPATCHED` y `BENEFICIARY_CREDITED` son hechos diferentes.
- El registro de un hecho no vuelve a efectuar el débito, crédito o reserva que describe.

**Comportamientos:** crear un hecho validado, comprobar repetición de identidad y conservar evidencia de origen.

## 5. Enumeraciones de `domain/enums/`

Cada enumeración representa un concepto específico. Aunque dos enums compartan valores, no deben mezclarse:
un resultado de Fondos no es un resultado de cancelación y una incertidumbre no equivale a un rechazo.

### 5.1. `Currency`

```text
ARS
USD
```

- `ARS`: pesos argentinos.
- `USD`: dólares estadounidenses.
- No agregar otras monedas sin extender expresamente el negocio y sus contratos.
- El principal y cada cargo deben identificar la moneda que les corresponde.

### 5.2. `TransferType`

```text
INTERNAL
OUTGOING
INCOMING
```

- `INTERNAL`: transferencia entre cuentas internas; Fondos confirma débito y crédito conjuntamente.
- `OUTGOING`: transferencia enviada desde el banco hacia un participante externo.
- `INCOMING`: transferencia recibida desde un participante externo hacia una cuenta del banco.

La modalidad determina qué atributos son obligatorios y qué componentes de ejecución corresponden.

### 5.3. `ProcessingState`

```text
RECEIVED
VALIDATING
PREPARING
EXECUTING
COMPLETED
CANCELLED
REJECTED
REQUIRES_RESOLUTION
```

- `RECEIVED`: la instrucción quedó identificada y conservada.
- `VALIDATING`: se comprueban destino, autorización y condiciones aplicables.
- `PREPARING`: se coordinan las preparaciones y la cobertura exigida.
- `EXECUTING`: la actuación de ejecución se encuentra en curso.
- `COMPLETED`: se alcanzó la conclusión correspondiente a la modalidad con hechos comprobados.
- `CANCELLED`: la cancelación quedó confirmada sin ejecución posterior posible.
- `REJECTED`: existe rechazo definitivo con evidencia suficiente.
- `REQUIRES_RESOLUTION`: hace falta resolver una situación que no puede cerrarse como éxito o rechazo.

Es el estado general de la instrucción. **No sustituye** los hechos `DISPATCHED`,
`IRREVOCABLY_ACCEPTED` o `BENEFICIARY_CREDITED`.

No asumir una transición automática entre todos los valores. Cada modificación debe respetar el avance real,
las actuaciones incompatibles, la evidencia disponible y las obligaciones que continúan vigentes.

### 5.4. `DestinationType`

```text
INTERNAL
EXTERNAL
```

- `INTERNAL`: identifica una cuenta interna.
- `EXTERNAL`: identifica un participante y un destino bancario externo.

Las dos variantes exigen campos diferentes y mutuamente excluyentes.

### 5.5. `AuthorizationBasisType`

```text
DIRECT_INSTRUCTION
MANDATE
OCCURRENCE
CONTRACTUAL_OBLIGATION
AUTHORIZED_RESOLUTION
```

- `DIRECT_INSTRUCTION`: orden impartida directamente por un actor autorizado.
- `MANDATE`: instrucción fundada en un mandato vigente y aplicable.
- `OCCURRENCE`: actuación originada por una ocurrencia identificada de un mandato o programación.
- `CONTRACTUAL_OBLIGATION`: actuación fundada en una obligación contractual reconocida.
- `AUTHORIZED_RESOLUTION`: actuación fundada en una decisión o resolución autorizada.

El enum identifica el tipo de fundamento, pero no reemplaza la comprobación de facultades y evidencias.

### 5.6. `MonetaryComponentType`

```text
PRINCIPAL
FEE
TAX
ADJUSTMENT
```

- `PRINCIPAL`: importe principal trasladado por la transferencia.
- `FEE`: comisión vinculada a una condición o ejecución que la origina.
- `TAX`: tributo correspondiente a un concepto identificado.
- `ADJUSTMENT`: regularización o corrección monetaria identificada.

No sumar todos los componentes indiscriminadamente: cada uno conserva finalidad y tratamiento propio.

### 5.7. `MonetaryExecutionStatus`

```text
NOT_STARTED
PENDING
CONFIRMED
REJECTED
UNCERTAIN
```

- `NOT_STARTED`: no existe una actuación monetaria iniciada.
- `PENDING`: actuación identificada cuya resolución todavía no se confirmó.
- `CONFIRMED`: Fondos confirmó el resultado y proporcionó evidencias suficientes.
- `REJECTED`: la autoridad confirmó que el efecto no se ejecutó.
- `UNCERTAIN`: se desconoce el resultado de la actuación original.

`UNCERTAIN` obliga a consultar esa misma actuación. No autoriza un débito nuevo.

### 5.8. `OutgoingCoverageStatus`

```text
PREPARING
PROTECTED
RESOLUTION_PENDING
RESOLVED
```

- `PREPARING`: se está constituyendo o verificando la cobertura exigida.
- `PROTECTED`: la cobertura se encuentra protegida frente al despacho y su posible incertidumbre.
- `RESOLUTION_PENDING`: la cobertura requiere resolver un resultado externo o monetario incierto.
- `RESOLVED`: el tratamiento de la cobertura quedó comprobado.

Este enum describe **cobertura**, no despacho, acreditación ni liquidación externa.

### 5.9. `IncomingAcceptanceStatus`

```text
PENDING
ACCEPTED
REJECTED
```

- `PENDING`: la aceptación todavía no se resolvió.
- `ACCEPTED`: el banco asumió el compromiso de acreditar o resolver de forma procedente.
- `REJECTED`: se rechazó antes de aceptar irrevocablemente.

Una aceptación no demuestra por sí sola que la cuenta ya recibió el crédito.

### 5.10. `CancellationOutcome`

```text
CANCELLED
PENDING_EXTERNAL_CONFIRMATION
ALREADY_CREDITED
REQUIRES_RESOLUTION
```

- `CANCELLED`: se comprobó que no se ejecutó ni puede ejecutarse la instrucción.
- `PENDING_EXTERNAL_CONFIRMATION`: falta confirmación suficiente del mecanismo externo.
- `ALREADY_CREDITED`: el destino ya fue acreditado; corresponde analizar una devolución.
- `REQUIRES_RESOLUTION`: existe una contradicción o incertidumbre que requiere investigación.

`PENDING_EXTERNAL_CONFIRMATION` no permite liberar la reserva ni cerrar el caso como cancelado.

### 5.11. `ReturnStatus`

```text
PENDING
CONFIRMED
REJECTED
UNCERTAIN
REQUIRES_REVIEW
```

- `PENDING`: devolución registrada con importe comprometido y ejecución por resolver.
- `CONFIRMED`: devolución comprobada mediante sus efectos y evidencias.
- `REJECTED`: devolución rechazada con evidencia definitiva.
- `UNCERTAIN`: no se conoce el resultado; se conserva su importe comprometido.
- `REQUIRES_REVIEW`: se requiere revisión por evidencia contradictoria o situación no resoluble automáticamente.

La incertidumbre de una devolución no restituye disponibilidad para solicitar otra devolución.

### 5.12. `RemoteStepStatus`

```text
PENDING
CONFIRMED
REJECTED
UNCERTAIN
REQUIRES_REVIEW
```

- `PENDING`: paso remoto identificado sin resolución comprobada.
- `CONFIRMED`: se recuperó confirmación válida del servicio responsable.
- `REJECTED`: se recibió rechazo definitivo comprobable.
- `UNCERTAIN`: la llamada pudo producir efectos, pero su respuesta no es conocida.
- `REQUIRES_REVIEW`: existen evidencias incompletas o contradictorias que requieren revisión.

Aunque los valores coincidan con `ReturnStatus`, son dos tipos distintos por pertenecer a conceptos diferentes.

### 5.13. `ConfirmedFactType`

```text
RECEIVED
ACCEPTED_FOR_EXECUTION
DISPATCHED
IRREVOCABLY_ACCEPTED
INSTRUMENT_EXECUTED
BENEFICIARY_CREDITED
CREDITOR_APPLIED
RETURNED
EXTERNALLY_SETTLED
```

- `RECEIVED`: recepción de una instrucción.
- `ACCEPTED_FOR_EXECUTION`: admisión de una instrucción para ejecutar.
- `DISPATCHED`: envío al mecanismo externo comprobado.
- `IRREVOCABLY_ACCEPTED`: aceptación firme del participante responsable.
- `INSTRUMENT_EXECUTED`: ejecución comprobada del instrumento utilizado.
- `BENEFICIARY_CREDITED`: crédito en destino confirmado.
- `CREDITOR_APPLIED`: obligación aplicada por el acreedor competente.
- `RETURNED`: operación posterior de devolución confirmada.
- `EXTERNALLY_SETTLED`: liquidación entre participantes confirmada por su autoridad.

Transferencias conserva y comunica los hechos pertinentes con su procedencia. No realiza por sí misma una
liquidación de Tesorería ni una aplicación de deuda que pertenezca a Pagos.

### 5.14. Conceptos que no se convierten en enums cerrados

Los siguientes datos son códigos o referencias cuyos catálogos no deben inventarse dentro de este dominio:

- `purposeCode`: finalidad de la instrucción.
- `reasonCode`: motivo de devolución o actuación.
- `mechanismCode`: código del mecanismo externo.
- `bankIdentifierType`: tipo de identificador bancario aceptado.
- `originType`: clase de originante de una instrucción.
- `componentCode`: componente de integración o recuperación.
- `responsibleService`: autoridad responsable de una actuación.

Tampoco crear `AccountStatus`, `BalanceStatus` o `TransactionStatus` para representar datos ajenos.

Los tipos de eventos publicados y los estados de respuestas HTTP pueden definirse en los contratos de aplicación,
sin introducirlos como reglas adicionales del agregado.

## 6. Objetos de valor de `domain/valueobject/`

Los objetos de valor se comparan por su contenido. Deben construirse con datos coherentes, sin setters que permitan
alterarlos después de una aceptación o confirmación.

### 6.1. `AcceptedDestination`

**Finalidad:** conservar exactamente el destino presentado y aceptado. Es un snapshot histórico, no una consulta
que deba volver a resolverse cada vez que se utiliza.

| Atributo                 | Tipo Java         | Regla                                    |
|--------------------------|-------------------|------------------------------------------|
| `destinationType`        | `DestinationType` | `INTERNAL` o `EXTERNAL`.                 |
| `accountReference`       | `ObjectReference` | Exigida si es interno.                   |
| `participantReference`   | `ObjectReference` | Exigida si es externo.                   |
| `bankIdentifierType`     | `String`          | Tipo del identificador, cuando exista.   |
| `bankIdentifierValue`    | `String`          | Valor sin perder ceros iniciales.        |
| `resolutionReference`    | `ObjectReference` | Identidad de la resolución de CBU/alias. |
| `beneficiaryReference`   | `ObjectReference` | Beneficiario cuando corresponda.         |
| `beneficiaryDisplayName` | `String`          | Nombre presentado; hasta 250 caracteres. |
| `currency`               | `Currency`        | Moneda del destino aceptado.             |
| `acceptedContentHash`    | `String`          | Hash del contenido vinculante.           |
| `acceptedAt`             | `Instant`         | Momento de aceptación.                   |

**Validaciones:**

1. `INTERNAL`: cuenta obligatoria y participante externo ausente.
2. `EXTERNAL`: participante obligatorio y cuenta interna ausente.
3. Si existe `bankIdentifierType`, también existe `bankIdentifierValue`, y viceversa.
4. El identificador bancario se conserva como texto exacto, incluidos ceros a la izquierda.
5. La moneda y la huella son obligatorias y deben coincidir con lo autorizado.
6. El beneficiario se identifica según las exigencias del mecanismo utilizado.
7. No se altera el snapshot cuando cambia el alias o la información que lo originó.

El `acceptedContentHash` de `AcceptedDestination` identifica los datos del destino presentados y aceptados.
El `acceptedContentHash` de `Transfer` identifica el contenido autorizado de toda la instrucción.
Son huellas con alcances distintos: no se exige que sus valores sean iguales entre sí.
Cada una debe verificarse contra el contenido canónico que representa, sin permitir cambios materiales.

Ejemplo de invariancia: un alias se resolvió a la cuenta A y se aceptó. Si luego apunta a B,
la transferencia original continúa dirigida a A.

### 6.2. `AuthorizationBasis`

**Finalidad:** representar el fundamento que autoriza una actuación concreta, con sus actores y evidencias.

| Atributo             | Tipo Java                | Regla                                    |
|----------------------|--------------------------|------------------------------------------|
| `basisType`          | `AuthorizationBasisType` | Tipo de fundamento.                      |
| `sourceReference`    | `ObjectReference`        | Mandato, acto o antecedente habilitante. |
| `contentHash`        | `String`                 | Huella del contenido autorizado.         |
| `actorReferences`    | `List<ObjectReference>`  | Actores comprobados.                     |
| `evidenceReferences` | `List<ObjectReference>`  | Evidencias de facultades y conformidad.  |

**Validaciones:**

- El fundamento debe corresponder al actor, la finalidad y el alcance de la actuación.
- El hash debe representar el contenido sobre el que se concedió autorización.
- Una modificación material de importe, moneda, destino o finalidad exige nuevas comprobaciones.
- No sustituir el fundamento por `authorized = true`.
- Un actor identificado no implica automáticamente que tenga facultades sobre una cuenta ajena.

### 6.3. `ExternalOperationReference`

**Finalidad:** identificar una operación externa con el contexto necesario para evitar confundir referencias
idénticas emitidas por participantes distintos.

| Atributo              | Tipo Java | Regla                         |
|-----------------------|-----------|-------------------------------|
| `participantId`       | `UUID`    | Identidad del participante.   |
| `mechanismCode`       | `String`  | Código de mecanismo no vacío. |
| `mechanismVersion`    | `long`    | Versión positiva utilizada.   |
| `externalOperationId` | `String`  | Referencia externa no vacía.  |

**Dos reglas distintas:**

- **Contenido completo:** participante, mecanismo, versión y referencia.
- **Identidad de negocio:** participante, mecanismo y referencia, **sin la versión**.

```text
businessKey = participantId + mechanismCode + externalOperationId
```

Estos datos y esta clave son requisitos del contrato, independientemente de su representación Java.
Pueden conservarse en `TransferExternalOperation` sin crear una clase adicional, siempre que se mantengan
los cuatro datos, la clave empresarial sin versión y la comprobación de contenido ante repeticiones.
Si se utiliza `ExternalOperationReference` como objeto de valor, debe respetar las mismas reglas.

Si se utiliza `record`, su igualdad estructural incluye `mechanismVersion`. No usar ese `equals()`
como único criterio para reconocer una operación externa ya procesada.

### 6.4. `ObjectReference`

**Finalidad:** conservar la identidad de un objeto administrado por este u otro servicio, sin apropiarse de sus datos.

| Atributo        | Tipo Java            | Regla                                      |
|-----------------|----------------------|--------------------------------------------|
| `objectType`    | `String`             | Tipo de objeto referenciado.               |
| `objectId`      | Identificador tipado | Identidad según su autoridad.              |
| `sourceService` | `String`             | Servicio propietario de la información.    |
| `version`       | `Long`               | Presente cuando corresponde fijar versión. |

La referencia puede identificar una cuenta, reserva, movimiento, autorización, intercambio o documento.
El identificador de un recurso interno puede ser `UUID`; una referencia bancaria externa puede requerir `String`.

**Validaciones:**

- No admitir tipo, identificador ni servicio de origen vacíos.
- Exigir versión positiva cuando se proporciona.
- Mantener el identificador en su formato real: no forzar a UUID un identificador externo.
- No interpretar una referencia como permiso para consultar o modificar su objeto.
- Si un contrato exige versión, debe estar presente; una versión opcional no vuelve opcional ese requisito.

### 6.5. `MonetaryConcept`

**Finalidad:** representar cada componente económico separadamente.

| Atributo               | Tipo Java               | Regla                                    |
|------------------------|-------------------------|------------------------------------------|
| `conceptCode`          | `String`                | Código de concepto, hasta 80 caracteres. |
| `componentType`        | `MonetaryComponentType` | Clase de componente monetario.           |
| `amount`               | `BigDecimal`            | Importe exacto, no negativo.             |
| `currency`             | `Currency`              | Moneda del concepto.                     |
| `calculationReference` | `ObjectReference`       | Cálculo de origen cuando exista.         |

**Validaciones:**

- No mezclar el principal con comisiones o tributos.
- Los importes ejecutables deben ser positivos y respetar dos decimales como máximo.
- Los totales o acumulados pueden ser cero cuando su significado lo admita.
- Rechazar escalas inválidas sin redondeo silencioso.
- Las comisiones por ejecución se reconocen cuando se confirma el hecho que las origina.
- Las correcciones de cargos no se confunden con una devolución del principal.

### 6.6. `ConfirmedFact`

**Finalidad:** expresar un hecho comprobado en la vista del negocio y en los contratos del servicio.

| Atributo             | Tipo Java               | Regla                       |
|----------------------|-------------------------|-----------------------------|
| `factType`           | `ConfirmedFactType`     | Tipo exacto de hecho.       |
| `occurredAt`         | `Instant`               | Momento efectivo del hecho. |
| `sourceReference`    | `ObjectReference`       | Fuente que lo confirma.     |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias verificables.    |

**Validaciones:**

- Los hechos no se crean por presunción ni por interpretación de un timeout.
- `RECEIVED` y `DISPATCHED` no permiten inferir `BENEFICIARY_CREDITED`.
- Una aceptación irrevocable no demuestra, por sí sola, que ya se acreditó.
- Un hecho externo debe conservar la autoridad que lo informó.
- El registro inmutable `TransferFact` conserva además la identidad y versión de origen para deduplicar.

### 6.7. `PendingComponent`

**Finalidad:** identificar con precisión qué parte de una transferencia requiere resolución.

| Atributo             | Tipo Java         | Regla                                   |
|----------------------|-------------------|-----------------------------------------|
| `componentCode`      | `String`          | Componente afectado.                    |
| `responsibleService` | `String`          | Servicio responsable.                   |
| `reasonCode`         | `String`          | Motivo de seguimiento.                  |
| `reference`          | `ObjectReference` | Identidad del componente cuando exista. |

**Validaciones:**

- La incertidumbre se asocia a un componente y no se oculta dentro de un estado general ambiguo.
- No asignar a Transferencias una obligación que pertenece a Fondos o Compensación.
- Conservar la referencia remota que permite consultar y resolver el resultado.
- La falta de respuesta no se convierte automáticamente en un rechazo definitivo.

Ejemplo: Fondos confirmó el débito, pero todavía no hay evidencia de acreditación externa.
La transferencia debe conservar esa diferencia y no informar acreditación confirmada.

### 6.8. `ReturnDestination`

**Finalidad:** expresar el destino al que se dirige una devolución, separándolo del destino original aceptado.

Es una **decisión de representación Java** para conservar tipado el destino de retorno. No implica que el
ordenante tenga automáticamente facultades para debitar al beneficiario.

| Atributo               | Tipo Java         | Regla                                       |
|------------------------|-------------------|---------------------------------------------|
| `destinationType`      | `DestinationType` | Retorno interno o externo.                  |
| `accountReference`     | `ObjectReference` | Cuenta cuando el retorno es interno.        |
| `participantReference` | `ObjectReference` | Participante si el retorno es externo.      |
| `bankIdentifierType`   | `String`          | Tipo de identificador, cuando existe.       |
| `bankIdentifierValue`  | `String`          | Identificador bancario, cuando existe.      |
| `resolutionReference`  | `ObjectReference` | Resolución del destino, cuando corresponde. |
| `currency`             | `Currency`        | Moneda del principal a devolver.            |

**Validaciones:**

- Los campos de destino interno y externo son excluyentes.
- Tipo y valor del identificador bancario aparecen juntos.
- La moneda coincide con la devolución solicitada.
- El destino debe ser coherente con la autorización o el fundamento suficiente de la devolución.
- El retorno no se ejecuta mediante un débito arbitrario sobre la cuenta del beneficiario.

`ReturnDestination` no reutiliza una aceptación de transferencia como si fuera automáticamente una autorización
para efectuar un nuevo efecto económico. Ese efecto requiere su propio fundamento y control de importe.

### 6.9. `CheckEvidence`

**Finalidad:** representar el resultado y la procedencia de una comprobación efectuada por una autoridad.
Su estructura interna es una decisión técnica de modelado; no sustituye los contratos de esa autoridad.

| Atributo             | Tipo Java               | Regla                                         |
|----------------------|-------------------------|-----------------------------------------------|
| `checkCode`          | `String`                | Tipo de comprobación, sin catálogo inventado. |
| `decisionCode`       | `String`                | Resultado informado por la autoridad.         |
| `sourceReference`    | `ObjectReference`       | Autoridad y actuación comprobada.             |
| `checkedAt`          | `Instant`               | Instante de evaluación.                       |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias entregadas.                        |

La interpretación de `decisionCode` depende del contrato de la autoridad consultada.
La existencia de una evidencia no transforma un rechazo o una comprobación caducada en autorización.

### 6.10. `StructuredContent`

**Finalidad:** conservar una petición o respuesta remota con un esquema identificado y contenido inmutable.
Es una representación técnica; los datos concretos dependen de la operación de Fondos o Compensación.

| Atributo        | Tipo Java              | Regla                                            |
|-----------------|------------------------|--------------------------------------------------|
| `schemaCode`    | `String`               | Identifica el esquema admitido por la operación. |
| `schemaVersion` | `long`                 | Versión positiva del esquema.                    |
| `contentHash`   | `String`               | Hash del contenido canónico.                     |
| `data`          | Valor inmutable tipado | Contenido ajustado al esquema indicado.          |

`data` no significa un conjunto arbitrario de campos: cada integración conserva su esquema concreto.
Una recuperación reutiliza el mismo contenido, versión y hash; no construye otra petición equivalente.
El adaptador convierte esa representación a JSON y almacena la copia durable exigida por el proceso.

## 7. Tipos y validaciones compartidas

### 7.1. Importes, precisión y fechas

| Dato                | Tipo Java    | Regla                                   |
|---------------------|--------------|-----------------------------------------|
| Identidad interna   | `UUID`       | Estable e irrepetible.                  |
| Principal           | `BigDecimal` | Positivo; escala máxima de 2 decimales. |
| Acumulado monetario | `BigDecimal` | No negativo; puede ser cero.            |
| Moneda              | `Currency`   | `ARS` o `USD`.                          |
| Versión de negocio  | `long`       | Positiva.                               |
| Versión de bloqueo  | `long`       | No negativa.                            |
| Instante            | `Instant`    | Intercambio en UTC.                     |
| Zona bancaria       | `ZoneId`     | `America/Argentina/Buenos_Aires`.       |
| Código              | `String`     | Entre 1 y 80 caracteres.                |
| Referencia externa  | `String`     | Entre 1 y 120 caracteres.               |
| Fundamento          | `String`     | Entre 1 y 4000 caracteres.              |
| Huella de contenido | `String`     | SHA-256 hexadecimal de 64 caracteres.   |

- Usar `BigDecimal` para dinero. No utilizar `float` ni `double`.
- Rechazar valores recibidos fuera de escala en lugar de corregirlos silenciosamente.
- Los importes monetarios del servicio se almacenan con precisión y escala acordes con `numeric(24,2)`.
- Los cálculos intermedios conservan precisión suficiente; los conceptos finales se redondean una sola vez
  mediante `HALF_UP` cuando corresponda a un cálculo documentado.
- Los instantes se expresan en UTC; las reglas bancarias locales usan la zona institucional.

### 7.2. Identidades que no se intercambian

| Identidad             | Qué identifica                                      |
|-----------------------|-----------------------------------------------------|
| `transferId`          | La instrucción completa.                            |
| `operationId`         | El recorrido de negocio asociado.                   |
| `actionId`            | Una actuación concreta y recuperable.               |
| `remoteActionId`      | Una actuación identificada en otro servicio.        |
| `effectId`            | El efecto económico cuya repetición debe impedirse. |
| `exchangeReference`   | Intercambio administrado por Compensación.          |
| `externalOperationId` | Operación identificada por participante externo.    |
| `cancellationId`      | Una solicitud de cancelación.                       |
| `returnId`            | Una devolución vinculada.                           |
| `returnEffectId`      | Un efecto económico único de devolución.            |
| `factId`              | Registro local de hecho comprobado.                 |
| `sourceFactId`        | Identidad del hecho en su fuente.                   |

No utilizar `X-Correlation-Id`, identidad de mensaje o clave HTTP de idempotencia como sustituto de
`effectId` o de la referencia original de negocio.

### 7.3. Contenido autorizado

La huella de contenido vinculante debe considerar lo que se aceptó para ejecutar:

- Ordenante y persona representada cuando corresponda.
- Cuenta de origen.
- Destino resuelto y beneficiario.
- Principal y moneda.
- Comisiones y tributos aceptados.
- Finalidad de la operación.
- Obligación, mandato u ocurrencia asociados, si existen.

Si cambia alguno de estos datos de manera material, el servicio debe volver a comprobar las condiciones y
obtener conformidad cuando corresponda. La versión de canonicalización utilizada para calcular el hash también
se conserva para poder reproducir la comparación histórica.

### 7.4. Reglas de idempotencia

- Misma actuación, misma identidad y mismo contenido: recuperar el resultado original.
- Misma identidad con contenido diferente: rechazar por conflicto.
- Dos instrucciones similares con identidades distintas: no tratarlas automáticamente como una duplicación.
- Misma operación externa recibida repetidamente: un único crédito económico.
- Misma devolución recibida repetidamente: un único efecto de retorno.
- Misma actuación remota con respuesta perdida: recuperar con el `remoteActionId` original.
- Mismo hecho fuente y versión: un solo registro de hecho comprobado.

La idempotencia de comandos debe complementarse con unicidad de los efectos económicos.

### 7.5. Concurrencia y bloqueos

El orden de adquisición de bloqueos locales es:

1. Transferencia.
2. Control de devoluciones.
3. Cancelación o devolución afectada.
4. Ejecución y pasos remotos afectados.

Este orden protege especialmente dos situaciones:

- Una cancelación compite con la creación o habilitación del despacho externo.
- Dos devoluciones compiten por el mismo importe todavía disponible.

Una validación previa en memoria no reemplaza una restricción y una decisión atómica en PostgreSQL.
El agregado expresa la regla; la persistencia debe mantenerla aun con solicitudes concurrentes.

### 7.6. Recuperación de efectos remotos

Una actuación con posible efecto económico se registra de forma durable **antes** de llamar al proveedor.
Si se pierde una respuesta, se consulta el mismo identificador de actuación y efecto.

La consulta de incertidumbre sigue este recorrido:

1. Al detectarla.
2. A los 5 minutos.
3. A los 30 minutos.
4. En cada jornada operativa mientras continúa la investigación.

El tiempo transcurrido no demuestra rechazo, cancelación ni autorización para liberar una reserva.
Una respuesta tardía debe incorporarse a la actuación original y conservar la historia previa.

## 8. Comportamiento del agregado por operación

Las operaciones de esta sección describen decisiones y efectos exigidos por el negocio.
La coordinación con servicios externos ocurre en aplicación; el dominio valida el contenido,
conserva identidades y evita cambios incompatibles.

### 8.1. Transferencia interna

**Datos necesarios:** ordenante, cuenta de origen, destino interno aceptado, principal positivo, moneda,
finalidad, autorización y conceptos monetarios.

**Recorrido:**

1. Reconocer la identidad de la instrucción y comprobar si ya existe.
2. Comprobar que origen y destino son cuentas distintas.
3. Conservar el destino resuelto, la autorización y el contenido aceptado.
4. Comprobar facultades, límites, restricciones y condiciones exigibles.
5. Registrar la intención monetaria con una actuación y efecto económico estables.
6. Solicitar a Fondos un conjunto indivisible de débito y crédito.
7. Incorporar el resultado confirmado o conservar la incertidumbre de la actuación original.
8. Registrar los hechos, movimientos referenciados y pendientes correspondientes.

**Ejemplo:** origen 100, destino 20, principal 30 y sin cargos.

```text
Saldo origen antes:     100
Saldo destino antes:     20
Principal:               30
Saldo origen después:    70
Saldo destino después:   50
```

Estos saldos pertenecen a Fondos o a su simulador contractual. Nunca son campos de `Transfer`.

**Casos límite:**

- Origen y destino idénticos: no admitir la instrucción.
- Saldo insuficiente confirmado por Fondos: rechazo sin traslado parcial.
- Confirmación de Fondos perdida: consultar la actuación original; no repetir con efectos nuevos.
- Cambio posterior de alias: respetar el destino fijado cuando se aceptó la instrucción.
- Moneda distinta sin conversión identificada: no ejecutar una conversión implícita.

### 8.2. Transferencia externa saliente

**Datos necesarios:** ordenante, cuenta de origen, destino externo aceptado, participante, mecanismo,
principal, moneda, autorización, finalidad y cargos.

**Recorrido:**

1. Comprobar el destino externo, la autorización y las condiciones operativas.
2. Registrar la reserva solicitada a Fondos, incluida la cobertura de cargos autorizados.
3. Proteger la cobertura frente a una ejecución o un resultado de despacho inciertos.
4. Registrar la intención de intercambio y comprobar `dispatchBarrier`.
5. Solicitar despacho mediante la autoridad de Compensación.
6. Registrar por separado envío, aceptación irrevocable y acreditación, según sus evidencias.
7. Cuando se acredita el beneficiario, coordinar el consumo de reserva con identidad estable.
8. Conservar pendientes y recuperar respuestas inciertas sin duplicar efectos.

**Diferencia entre hechos:**

| Evidencia               | Qué permite afirmar                                |
|-------------------------|----------------------------------------------------|
| Acuse técnico           | El mecanismo recibió o reconoció una comunicación. |
| Envío comprobado        | La instrucción fue despachada.                     |
| Aceptación irrevocable  | Existe compromiso firme de ejecución.              |
| Acreditación comprobada | El beneficiario recibió el crédito.                |
| Liquidación comprobada  | Se cumplió la obligación entre participantes.      |

No utilizar un acuse para establecer `beneficiaryCreditedAt`.

**Casos límite:**

- Respuesta perdida después del envío: cobertura protegida, consulta y recuperación.
- Rechazo definitivo sin ejecución posible: regularizar o liberar cobertura con evidencia suficiente.
- Aceptación firme sin crédito: conservar obligación y protección de recursos.
- Crédito externo comprobado con débito local incierto: recuperar el débito original.
- Liquidación externa fallida después del crédito: mantener la acreditación confirmada.

### 8.3. Transferencia externa entrante

**Datos necesarios:** participante, mecanismo, referencia externa, ordenante, beneficiario,
cuenta interna de destino, importe, moneda, finalidad y mensaje con evidencia.

**Recorrido:**

1. Construir la identidad externa completa.
2. Verificar si ya se recibió esa identidad y contrastar el contenido original.
3. Comprobar destino, moneda, restricciones y procedencia del participante.
4. Aceptar irrevocablemente solo después de las comprobaciones exigibles.
5. Conservar el `creditEffectId` único antes de pedir el crédito a Fondos.
6. Consultar el crédito original ante timeout, interrupción o respuesta perdida.
7. Registrar el resultado y comunicar la información necesaria para el circuito externo.

**Casos límite:**

- Mismo participante, mecanismo, referencia y contenido: recuperar la misma recepción.
- Misma identidad externa con contenido diferente: conflicto, sin crédito adicional.
- Destino inexistente: rechazo antes de aceptar irrevocablemente.
- Restricción solo de débitos: no tratarla automáticamente como prohibición de crédito.
- Aceptación firme y crédito que no se pudo comprobar: conservar obligación y recuperación.
- Crédito confirmado con respuesta perdida: recuperar evidencia, sin volver a acreditar.

### 8.4. Cancelación de transferencia

**Datos necesarios:** transferencia original, actuación, solicitante autorizado, motivo y evidencias.

**Recorrido:**

1. Registrar la solicitud con `cancellationId` propio.
2. Bloquear o versionar la transferencia para ordenar cancelación frente a despacho.
3. Comprobar si existe intercambio y si puede ejecutarse una salida tardía.
4. Si no hubo envío posible y se confirma la cancelación, activar `dispatchBarrier`.
5. Si existe intercambio, solicitar cancelación al mecanismo responsable.
6. Conservar la cobertura mientras no se demuestre ausencia de ejecución pendiente.
7. Confirmar cancelación solo con evidencia definitiva.
8. Si ya ocurrió la acreditación, tramitar devolución en lugar de borrar el hecho.

**Casos límite:**

- Cancelación confirmada antes del envío: no puede existir despacho posterior.
- Cancelación concurrente con envío: la decisión debe quedar ordenada, no depender del azar de hilos.
- Petición recibida sin respuesta externa: la operación no puede considerarse cancelada.
- Consulta externa sin registro: no prueba por sí sola que el envío sea imposible.
- Transferencia acreditada: no es cancelable como si nunca hubiera ocurrido.

### 8.5. Devolución de transferencia

**Datos necesarios:** transferencia original, principal a devolver, moneda, fundamento,
destino de retorno, identidad del nuevo efecto y devoluciones anteriores.

**Recorrido:**

1. Comprobar que la transferencia original permite tramitar una devolución.
2. Verificar autorización o fundamento suficiente para el efecto que se solicita.
3. Leer el control económico de devoluciones bajo protección concurrente.
4. Comprobar que el importe solicitado no supera `remainingAmount`.
5. Crear `TransferReturn` con `returnId` y `returnEffectId` estables.
6. Comprometer el importe en `TransferReturnControl` antes de iniciar su efecto.
7. Coordinar la actuación interna o externa de retorno y conservar su identidad.
8. Confirmar, rechazar con evidencia o mantener incierta la devolución.
9. Actualizar los importes del control de forma atómica y conservar el vínculo con el original.

**Ejemplo:** transferencia original de 100, devoluciones confirmadas de 40 y comprometidas de 20.

```text
Principal original:          100
Devoluciones confirmadas:     40
Devoluciones comprometidas:   20
Remanente disponible:         40
```

Dos peticiones simultáneas no pueden comprometer juntas más de 40.

**Casos límite:**

- Solicitud repetida con igual `returnEffectId`: se recupera el mismo resultado.
- Solicitudes concurrentes sobre el mismo remanente: una decisión atómica evita exceso.
- Devolución incierta: se mantiene comprometido su importe.
- Rechazo definitivo comprobado: el control puede liberar el importe comprometido.
- Ordenante pide debitar al beneficiario sin facultad: no ejecutar ese débito.
- Devolución de principal: no reversa automáticamente comisiones ni tributos.

### 8.6. Consulta de transferencia

La consulta puede exponer, según el permiso y alcance solicitado:

- Identidad, modalidad, principal, moneda y finalidad.
- Destino exacto que fue aceptado.
- Fundamento y evidencias de autorización pertinentes.
- Referencias de reservas, ejecuciones y movimientos de Fondos.
- Identidad e intercambio externo asociados.
- Hechos comprobados y componentes con incertidumbre.
- Cancelaciones y sus resultados.
- Devoluciones confirmadas, comprometidas y remanente.

La consulta **no** solicita nuevos débitos, créditos, reservas, envíos, cancelaciones o devoluciones.
La búsqueda por referencia externa requiere identidad completa y autorización.

## 9. Correspondencia de las clases con la persistencia

El dominio puede contener componentes subordinados, mientras que la persistencia mantiene registros
normalizados, versiones y restricciones que protegen las invariantes incluso bajo concurrencia.

| Clase o componente de dominio | Tabla propia                    |
|-------------------------------|---------------------------------|
| `Transfer`                    | `transfer_order`                |
| `AcceptedDestination`         | `transfer_accepted_destination` |
| `TransferExternalOperation`   | `transfer_external_operation`   |
| `TransferMonetaryExecution`   | `transfer_monetary_execution`   |
| `TransferOutgoingExecution`   | `transfer_outgoing_execution`   |
| `TransferIncomingAcceptance`  | `transfer_incoming_acceptance`  |
| `TransferCancellation`        | `transfer_cancellation`         |
| `TransferReturnControl`       | `transfer_return_control`       |
| `TransferReturn`              | `transfer_return`               |
| `TransferRemoteStep`          | `transfer_remote_step`          |
| `TransferReturnStep`          | `transfer_return_step`          |
| `TransferFact`                | `transfer_fact`                 |

Otras tablas de soporte mantienen la integridad de las actuaciones, sin necesidad de mezclar sus
modelos de persistencia con las entidades empresariales del agregado:

| Tabla técnica             | Responsabilidad                                                    |
|---------------------------|--------------------------------------------------------------------|
| `service_action`          | Conservar actuaciones y resultados recuperables.                   |
| `idempotency_record`      | Reconocer comandos repetidos y conflictos de contenido.            |
| `business_change_history` | Conservar decisiones y cambios históricos.                         |
| `outbox_event`            | Publicar cambios sin perder consistencia con la transacción local. |
| `inbox_message`           | Evitar procesar dos veces un mismo mensaje entrante.               |
| `persistent_job`          | Recuperar trabajos e investigaciones después de una interrupción.  |

### 9.1. Restricciones de integridad que deben mantenerse

- `transfer_order`: identidad de origen única, principal positivo y campos condicionales por modalidad.
- `transfer_accepted_destination`: contenido aceptado inmutable y variantes interna/externa coherentes.
- `transfer_external_operation`: unicidad de participante, mecanismo y referencia externa.
- `transfer_monetary_execution`: unicidad de servicio de Fondos y `remoteActionId`.
- `transfer_outgoing_execution`: cobertura protegida antes de habilitar el despacho.
- `transfer_incoming_acceptance`: `creditEffectId` único y fecha al aceptar irrevocablemente.
- `transfer_cancellation`: cancelación ordenada frente a intercambio y barrera de despacho.
- `transfer_return_control`: conservación exacta de principal, comprometido, confirmado y remanente.
- `transfer_return`: `returnEffectId` único y compromiso conservado durante incertidumbre.
- `transfer_remote_step`: unicidad de propietario y componente, y de actuación remota.
- `transfer_return_step`: mismas garantías aplicadas al identificador de devolución.
- `transfer_fact`: unicidad de hecho fuente, versión y transferencia propietaria.

### 9.2. Transacciones locales y llamadas remotas

- La identidad y la intención de una actuación se guardan localmente antes de llamar al proveedor.
- No se mantiene una transacción SQL abierta durante una llamada HTTP o publicación Kafka.
- Los resultados confirmados se incorporan junto con la actualización del agregado y su historia.
- El evento de salida se registra en Outbox dentro de la transacción local correspondiente.
- Los mensajes repetidos no vuelven a crear efectos económicos.
- No se establecen claves foráneas ni relaciones JPA directas con bases de otros servicios.

## 10. Separación del dominio respecto de otras capas

### Dentro de `domain/`

- Entidades, objetos de valor, enums e invariantes propias de Transferencias.
- Validaciones de coherencia, importes, identidad, destino y contenido autorizado.
- Reglas de cancelación, devolución, control de remanentes y aceptación de hechos comprobados.
- Decisiones sobre modificaciones incompatibles a partir de información ya comprobada.

### Fuera de `domain/`

- Controladores HTTP, DTO de entrada/salida, respuestas y códigos HTTP.
- Autenticación JWT, obtención del actor y verificación técnica de credenciales.
- Consultas a autoridades y simuladores: Cuentas, Fondos, Compensación y demás dependencias.
- Repositorios JPA, entidades de persistencia, migraciones y transacciones SQL.
- Kafka, Outbox, Inbox, schedulers y clientes de integración.
- Publicación, métricas, registros y trazas instrumentadas.

El dominio **recibe** el resultado de una validación o de una actuación remota a través del caso de uso.
No llama directamente a un simulador para producir o comprobar ese resultado.

## 11. Escenarios de comportamiento para comprobar las reglas

| Caso | Situación                                        | Resultado esperado                       |
|------|--------------------------------------------------|------------------------------------------|
| 01   | Principal igual a cero                           | Instrucción inválida.                    |
| 02   | Principal negativo                               | Instrucción inválida.                    |
| 03   | Principal con escala mayor a 2                   | Instrucción inválida; sin redondeo.      |
| 04   | Interna con misma cuenta en ambos extremos       | Rechazo antes de ejecutar.               |
| 05   | Conversión de moneda sin operación cambiaria     | No ejecutar conversión implícita.        |
| 06   | Misma identidad y contenido                      | Recuperar el resultado original.         |
| 07   | Misma identidad y contenido distinto             | Conflicto; ningún segundo efecto.        |
| 08   | Cambio de alias tras aceptación                  | Mantener destino aceptado.               |
| 09   | Fondos confirma débito y crédito conjuntos       | Interna confirmada sin traslado parcial. |
| 10   | Fondos ejecuta pero se pierde la respuesta       | Recuperar efecto original.               |
| 11   | Saliente con simple acuse                        | No afirmar acreditación.                 |
| 12   | Saliente con aceptación irrevocable              | Conservar cobertura firme.               |
| 13   | Saliente con resultado desconocido               | Mantener protección y seguimiento.       |
| 14   | Crédito externo con liquidación fallida          | No revocar la acreditación.              |
| 15   | Entrante externa repetida con mismo contenido    | Un solo crédito.                         |
| 16   | Entrante repetida con contenido incompatible     | Conflicto; ningún crédito adicional.     |
| 17   | Entrante con cuenta de destino inexistente       | Rechazo antes de compromiso firme.       |
| 18   | Recepción con restricción solo de débitos        | Evaluar el crédito según su alcance.     |
| 19   | Cancelación confirmada previa al despacho        | Impedir envío posterior.                 |
| 20   | Cancelación y despacho simultáneos               | Decisión ordenada y cobertura protegida. |
| 21   | Cancelación solicitada tras acreditación         | Tramitar devolución, no borrar crédito.  |
| 22   | Principal 100, devuelto 40, comprometido 20      | Remanente disponible 40.                 |
| 23   | Dos devoluciones simultáneas                     | Nunca exceder principal disponible.      |
| 24   | Devolución repetida con mismo efecto             | No producir otro retorno.                |
| 25   | Devolución incierta                              | Conservar importe comprometido.          |
| 26   | Ordenante sin poder para debitar al beneficiario | No efectuar débito.                      |
| 27   | Hecho fuente repetido con misma versión          | Registrar una sola vez.                  |
| 28   | Resultado remoto tardío                          | Incorporar al paso original.             |
| 29   | Consulta informativa                             | No generar efectos monetarios.           |
| 30   | Reinicio tras intención remota registrada        | Recuperar sin cambiar identidades.       |

Los escenarios de `domain/` comprueban invariantes y decisiones locales.
Los efectos monetarios reales o simulados, las restricciones SQL y las carreras entre solicitudes requieren
además comprobar la integración con sus adaptadores y la persistencia transaccional correspondiente.
