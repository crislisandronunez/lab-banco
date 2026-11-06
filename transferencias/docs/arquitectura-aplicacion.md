# Transfer Service — Arquitectura de la capa de aplicación

## 1. Propósito

Este documento explica cómo organizar e implementar `application/` en `transfer-service` mediante arquitectura
hexagonal, conservando las reglas empresariales definidas para los casos de uso de Transferencias.

El objetivo es que puedas identificar qué archivo crear, qué responsabilidad asignarle, qué datos debe transportar,
qué puerto debe invocar y dónde ubicar cada garantía técnica exigida por el banco.

Este documento responde **cómo organizar la implementación**. La explicación profunda de **qué exige el negocio**
corresponde a `casos-de-uso.md`. Ambos deben conservar las mismas operaciones, identidades y garantías.

Las clases y los nombres de puertos propuestos son una organización técnica Java. No constituyen contratos nuevos.
Los comportamientos empresariales, las autoridades, las identidades y las garantías sí deben respetarse.

## 2. Arquitectura hexagonal y distribución de responsabilidades

### 2.1. Dependencias permitidas

```text
                         HTTP / Kafka / trabajos locales
                                      |
                                      v
                            Adaptadores de entrada
                                      |
                                      v
                             application/port/in/
                                      |
                                      v
                            application/service/
                              |             |
                              v             v
                           domain/   application/port/out/
                                            |
                                            v
                                  Adaptadores de salida
                                            |
                         +------------------+------------------+
                         |                  |                  |
                      PostgreSQL        Autoridades        Outbox / Inbox
                                         simuladas
```

La aplicación conoce los **contratos de sus puertos**, no los protocolos ni las implementaciones de sus adaptadores.

| Componente              | Responsabilidad                                                        |
|-------------------------|------------------------------------------------------------------------|
| `domain/`               | Proteger invariantes, identidades y comportamientos de Transferencias. |
| `application/port/in/`  | Definir las capacidades que puede solicitar un consumidor.             |
| `application/service/`  | Coordinar casos de uso, decisiones locales y actuaciones remotas.      |
| `application/port/out/` | Expresar las capacidades requeridas a persistencia y autoridades.      |
| `application/dto/`      | Definir las solicitudes y respuestas tipadas que utilizan los puertos. |
| Adaptador de entrada    | Traducir HTTP, evento o trabajo persistente a la invocación del caso.  |
| Adaptador de salida     | Implementar un puerto con SQL, HTTP, mensajería u otro mecanismo.      |

### 2.2. Qué hace un servicio de aplicación

- Recibe un comando o una consulta tipados, junto con el contexto confiable de invocación.
- Identifica la actuación y reconoce repeticiones, conflictos y versiones incompatibles.
- Solicita comprobaciones, preparaciones y efectos a las autoridades correspondientes.
- Recupera agregados y les ordena comportamientos mediante sus métodos de negocio.
- Conserva intenciones, resultados, evidencias, historia y seguimiento de forma durable.
- Distingue actuaciones locales de llamadas remotas, sin abrir una transacción distribuida.
- Devuelve hechos comprobados y componentes por resolver, sin presumir éxito por un acuse técnico.

### 2.3. Qué no hace

- No implementa reglas monetarias pertenecientes a Fondos.
- No consulta directamente las tablas de otros microservicios.
- No interpreta un JWT ni decide autenticación en lugar del adaptador de entrada.
- No calcula saldos ni decide que una reserva fue consumida sin confirmación de Fondos.
- No resuelve alias por su cuenta ni modifica el destino aceptado para una instrucción anterior.
- No transforma el intercambio externo en lógica interna de Transferencias.
- No permite que las vistas o DTO de transporte modifiquen directamente entidades.

## 3. Casos de uso y responsabilidades de implementación

### 3.1. Operaciones públicas

| Caso de uso               | Referencia en español                  | Contrato  | Naturaleza       |
|---------------------------|----------------------------------------|-----------|------------------|
| `ExecuteInternalTransfer` | Ejecutar transferencia interna         | CI-05-001 | Comando durable. |
| `SendOutgoingTransfer`    | Enviar transferencia externa saliente  | CI-05-002 | Comando durable. |
| `ReceiveIncomingTransfer` | Recibir transferencia externa entrante | CI-05-003 | Comando durable. |
| `CancelTransfer`          | Cancelar una transferencia             | CI-05-004 | Comando durable. |
| `ReturnTransfer`          | Tramitar una devolución                | CI-05-005 | Comando durable. |
| `GetTransfer`             | Consultar una transferencia            | CI-05-006 | Consulta.        |
| `GetTransferAction`       | Consultar una actuación                | CI-05-006 | Consulta.        |
| `SearchTransfers`         | Buscar por identidad externa           | CI-05-006 | Consulta.        |

### 3.2. Continuaciones internas

| Operación                   | Referencia en español             | Función técnica                                         |
|-----------------------------|-----------------------------------|---------------------------------------------------------|
| `RecoverRemoteStep`         | Recuperar paso remoto             | Consultar un efecto sin repetirlo.                      |
| `IncorporateExternalResult` | Incorporar resultado externo      | Aplicar evidencia tardía o recibida por otro canal.     |
| `ResumeTransferWork`        | Reanudar trabajo de transferencia | Continuar tareas durables tras una interrupción.        |
| `RecordTransferChange`      | Registrar cambio de transferencia | Generar el hecho para Outbox junto con el cambio local. |

Las continuaciones internas no necesitan endpoints públicos propios. Pueden implementarse como métodos de servicios
cohesivos, siempre que se conserven identidad, recuperación y autorización del origen del resultado.

### 3.3. Relación con las entidades existentes

| Caso         | Componentes de dominio que necesita coordinar                                         |
|--------------|---------------------------------------------------------------------------------------|
| Interna      | `Transfer`, `TransferMonetaryExecution`, `TransferRemoteStep`, `TransferFact`.        |
| Saliente     | `Transfer`, `TransferOutgoingExecution`, `TransferExternalOperation`, pasos y hechos. |
| Entrante     | `Transfer`, `TransferIncomingAcceptance`, operación externa, ejecución y hechos.      |
| Cancelación  | `Transfer`, `TransferCancellation`, ejecución y pasos relacionados.                   |
| Devolución   | `Transfer`, `TransferReturnControl`, `TransferReturn`, `TransferReturnStep`.          |
| Consultas    | `Transfer`, acciones persistidas y componentes vinculados según permiso.              |
| Recuperación | `TransferRemoteStep` o `TransferReturnStep` y el agregado propietario.                |

La aplicación utiliza los métodos de negocio ya existentes en esas clases. No debe reemplazarlos con
modificaciones directas de atributos ni duplicar sus reglas con estados paralelos.

## 4. Estructura propuesta de `application/`

```text
application/
├── port/
│   ├── in/
│   │   ├── ExecuteInternalTransferUseCase.java
│   │   ├── SendOutgoingTransferUseCase.java
│   │   ├── ReceiveIncomingTransferUseCase.java
│   │   ├── CancelTransferUseCase.java
│   │   ├── ReturnTransferUseCase.java
│   │   ├── GetTransferUseCase.java
│   │   ├── GetTransferActionUseCase.java
│   │   ├── SearchTransfersUseCase.java
│   │   ├── RecoverRemoteStepUseCase.java
│   │   └── IncorporateExternalResultUseCase.java
│   └── out/
│       ├── TransferRepository.java
│       ├── TransferActionRepository.java
│       ├── IdempotencyRepository.java
│       ├── TransferHistoryRepository.java
│       ├── TransferOutboxRepository.java
│       ├── TransferInboxRepository.java
│       ├── RecoveryJobRepository.java
│       ├── LocalUnitOfWork.java
│       ├── AccountAuthorityPort.java
│       ├── DestinationResolutionPort.java
│       ├── RepresentationAuthorityPort.java
│       ├── BusinessControlsPort.java
│       ├── CommercialConditionsPort.java
│       ├── FundsAuthorityPort.java
│       ├── ExchangeAuthorityPort.java
│       └── CurrencyExchangePort.java
├── command/
│   ├── ActionContext.java
│   ├── RequestContext.java
│   ├── ExecuteInternalTransferCommand.java
│   ├── SendOutgoingTransferCommand.java
│   ├── ReceiveIncomingTransferCommand.java
│   ├── CancelTransferCommand.java
│   ├── ReturnTransferCommand.java
│   └── IncorporateExternalResultCommand.java
├── query/
│   ├── GetTransferQuery.java
│   ├── GetTransferActionQuery.java
│   └── SearchTransfersQuery.java
├── result/
│   ├── TransferActionResult.java
│   ├── TransferView.java
│   ├── TransferActionView.java
│   └── TransferSearchResult.java
├── dto/
│   ├── account/
│   ├── destination/
│   ├── representation/
│   ├── controls/
│   ├── commercial/
│   ├── funds/
│   ├── exchange/
│   ├── currency/
│   └── persistence/
├── exception/
└── service/
    ├── InternalTransferService.java
    ├── OutgoingTransferService.java
    ├── IncomingTransferService.java
    ├── TransferCancellationService.java
    ├── TransferReturnService.java
    ├── TransferQueryService.java
    └── TransferRecoveryService.java
```

- Un servicio puede implementar varios puertos de entrada si sus responsabilidades permanecen cohesionadas.
- Los modelos específicos de puertos de salida se organizan en `application/dto/` por autoridad o responsabilidad.
- `LocalUnitOfWork` expresa una frontera transaccional; no crea una segunda base ni reemplaza PostgreSQL.
- Los errores de negocio permanecen en las excepciones del dominio cuando corresponden a sus invariantes.
- La política técnica de errores de aplicación se separa de códigos HTTP y clases de Spring.
- Los adaptadores y los simuladores quedan fuera de `application/`.

### 4.1. Cómo leer los nombres

| Sufijo o paquete | Significado                           | Ejemplo                   |
|------------------|---------------------------------------|---------------------------|
| `UseCase`        | Contrato de entrada.                  | `CancelTransferUseCase`   |
| `Command`        | Datos de una actuación persistente.   | `ReturnTransferCommand`   |
| `Query`          | Datos de una consulta sin efectos.    | `GetTransferQuery`        |
| `Result`         | Resultado de una actuación.           | `TransferActionResult`    |
| `View`           | Proyección de lectura autorizada.     | `TransferView`            |
| `Port`           | Capacidad que exige la aplicación.    | `FundsAuthorityPort`      |
| `Repository`     | Acceso abstracto a datos propios.     | `TransferRepository`      |
| `Service`        | Orquestación de un caso de uso.       | `IncomingTransferService` |
| `Request`        | Solicitud tipada hacia una autoridad. | `FundsExecutionRequest`   |
| `DTO Result`     | Respuesta tipada de una autoridad.    | `FundsExecutionResult`    |

## 5. Puertos de entrada: `application/port/in/`

Un puerto de entrada define **qué puede pedir un consumidor**, no cómo llega el HTTP o el mensaje.

| Interfaz                           | Operación sugerida | Entrada                          | Resultado                  |
|------------------------------------|--------------------|----------------------------------|----------------------------|
| `ExecuteInternalTransferUseCase`   | `execute(...)`     | `ExecuteInternalTransferCommand` | `TransferActionResult`     |
| `SendOutgoingTransferUseCase`      | `execute(...)`     | `SendOutgoingTransferCommand`    | `TransferActionResult`     |
| `ReceiveIncomingTransferUseCase`   | `execute(...)`     | `ReceiveIncomingTransferCommand` | `TransferActionResult`     |
| `CancelTransferUseCase`            | `execute(...)`     | `CancelTransferCommand`          | `TransferActionResult`     |
| `ReturnTransferUseCase`            | `execute(...)`     | `ReturnTransferCommand`          | `TransferActionResult`     |
| `GetTransferUseCase`               | `get(...)`         | `GetTransferQuery`               | `TransferView`             |
| `GetTransferActionUseCase`         | `get(...)`         | `GetTransferActionQuery`         | `TransferActionView`       |
| `SearchTransfersUseCase`           | `search(...)`      | `SearchTransfersQuery`           | `TransferSearchResult`     |
| `RecoverRemoteStepUseCase`         | `recover(...)`     | Identidad del paso original      | Resultado de recuperación  |
| `IncorporateExternalResultUseCase` | `incorporate(...)` | Resultado con procedencia        | Resultado de incorporación |

El nombre `execute(...)` puede repetirse en interfaces diferentes: la interfaz aporta el significado del caso.

Las consultas no deben recibir `Idempotency-Key` para crear efectos; los comandos persistentes sí deben conservar
una identidad de actuación y un contenido recuperable.

## 6. Comandos, consultas y contexto confiable

### 6.1. `ActionContext` — Contexto de actuación

| Atributo             | Tipo Java               | Uso                                          |
|----------------------|-------------------------|----------------------------------------------|
| `actionId`           | `UUID`                  | Identifica la petición persistente original. |
| `operationId`        | `UUID`                  | Relaciona el recorrido empresarial.          |
| `causationId`        | `UUID`                  | Referencia causal, cuando existe.            |
| `purpose`            | `String`                | Finalidad declarada y comprobable.           |
| `reason`             | `String`                | Fundamento cuando la actuación lo exige.     |
| `expectedVersion`    | `Long`                  | Control de modificación concurrente.         |
| `originalReceivedAt` | `Instant`               | Fecha original cuando afecta el negocio.     |
| `evidenceReferences` | `List<ObjectReference>` | Evidencias vinculadas a la actuación.        |

El contexto no concede permisos por sí mismo. Las facultades se comprueban con la identidad autenticada y las
preparaciones o autorizaciones correspondientes.

### 6.2. `RequestContext` — Contexto verificado de invocación

| Dato                          | Propósito                                                                   |
|-------------------------------|-----------------------------------------------------------------------------|
| Identidad del actor           | Reconocer quién solicita la operación, desde autenticación verificable.     |
| Servicio llamador             | Diferenciar una integración de una instrucción de usuario.                  |
| Alcance y permisos            | Resolver qué objetos y capacidades puede usar.                              |
| Clave técnica de idempotencia | Recuperar el comando HTTP original sin confundirlo con el efecto económico. |
| Correlación de solicitud      | Enlazar logs y trazas; no constituye identidad de negocio.                  |

`RequestContext` puede ser un record inmutable construido por el adaptador de entrada tras comprobar autenticación.
No aceptar como verdadero un `actorId` incluido libremente por el consumidor.

### 6.3. Comandos de negocio

| Comando                            | Datos principales                                             | Datos condicionales      |
|------------------------------------|---------------------------------------------------------------|--------------------------|
| `ExecuteInternalTransferCommand`   | Origen, destino aceptado, principal, moneda, autorización.    | Cargos, cambio.          |
| `SendOutgoingTransferCommand`      | Origen, destino externo, mecanismo, importe, autorización.    | Cargos, cambio.          |
| `ReceiveIncomingTransferCommand`   | Identidad externa, destino, principal, mensaje y evidencia.   | Antecedentes.            |
| `CancelTransferCommand`            | `transferId`, motivo, contexto y autorización.                | Intercambio relacionado. |
| `ReturnTransferCommand`            | `transferId`, `returnEffectId`, importe, destino, fundamento. | Antecedentes.            |
| `IncorporateExternalResultCommand` | Actuación, fuente, hecho, versión y evidencias.               | Resultado específico.    |

**Regla de diseño:** cada comando conserva los campos exactos necesarios para su modalidad.
No utilizar un único comando enorme con decenas de atributos opcionales que permitan combinaciones inválidas.

Los comandos pueden implementarse con `record` y validaciones estructurales. Sus invariantes empresariales profundas
siguen correspondiendo al dominio y a las comprobaciones de las autoridades competentes.

### 6.3.1. `ExecuteInternalTransferCommand`

| Atributo               | Tipo Java               | Uso                                          |
|------------------------|-------------------------|----------------------------------------------|
| `context`              | `ActionContext`         | Identidad y fundamento de la actuación.      |
| `orderingParty`        | `ObjectReference`       | Ordenante de la transferencia.               |
| `sourceAccountId`      | `UUID`                  | Cuenta a debitar mediante Fondos.            |
| `acceptedDestination`  | `AcceptedDestination`   | Destino interno que se aceptó.               |
| `principalAmount`      | `BigDecimal`            | Principal exacto y positivo.                 |
| `currency`             | `Currency`              | Moneda del traslado.                         |
| `purposeCode`          | `String`                | Finalidad de negocio.                        |
| `authorizationBasis`   | `AuthorizationBasis`    | Autorización del contenido.                  |
| `concepts`             | `List<MonetaryConcept>` | Principal, comisiones y tributos.            |
| `commercialReferences` | `List<ObjectReference>` | Condiciones comerciales aplicadas.           |
| `fxReference`          | `ObjectReference`       | Conversión identificada, cuando corresponda. |

### 6.3.2. `SendOutgoingTransferCommand`

| Atributo               | Tipo Java               | Uso                                       |
|------------------------|-------------------------|-------------------------------------------|
| `context`              | `ActionContext`         | Identidad de actuación y causa.           |
| `orderingParty`        | `ObjectReference`       | Ordenante que acepta el contenido.        |
| `sourceAccountId`      | `UUID`                  | Cuenta cuyo principal tendrá cobertura.   |
| `acceptedDestination`  | `AcceptedDestination`   | Destino externo y beneficiario aceptados. |
| `mechanismReference`   | `ObjectReference`       | Mecanismo externo y versión aplicable.    |
| `principalAmount`      | `BigDecimal`            | Principal a enviar.                       |
| `currency`             | `Currency`              | Moneda del principal.                     |
| `purposeCode`          | `String`                | Finalidad aceptada.                       |
| `authorizationBasis`   | `AuthorizationBasis`    | Fundamento suficiente.                    |
| `concepts`             | `List<MonetaryConcept>` | Cargos y conceptos separados.             |
| `commercialReferences` | `List<ObjectReference>` | Condiciones comerciales utilizadas.       |
| `fxReference`          | `ObjectReference`       | Cambio expreso, cuando corresponda.       |

### 6.3.3. `ReceiveIncomingTransferCommand`

| Atributo               | Tipo Java                    | Uso                                         |
|------------------------|------------------------------|---------------------------------------------|
| `context`              | `ActionContext`              | Causa y actuación de recepción.             |
| `externalOperation`    | `ExternalOperationReference` | Identidad externa completa.                 |
| `orderingParty`        | `ObjectReference`            | Ordenante informado por el mecanismo.       |
| `beneficiaryReference` | `ObjectReference`            | Beneficiario, cuando lo exige el mecanismo. |
| `destinationAccountId` | `UUID`                       | Cuenta interna que recibiría crédito.       |
| `principalAmount`      | `BigDecimal`                 | Principal recibido.                         |
| `currency`             | `Currency`                   | Moneda informada.                           |
| `purposeCode`          | `String`                     | Finalidad recibida.                         |
| `messageReference`     | `ObjectReference`            | Mensaje recibido y procedencia.             |
| `evidenceReferences`   | `List<ObjectReference>`      | Evidencias originales.                      |
| `receivedContentHash`  | `String`                     | Huella para contrastar repeticiones.        |

### 6.3.4. `CancelTransferCommand`

| Atributo             | Tipo Java            | Uso                                         |
|----------------------|----------------------|---------------------------------------------|
| `context`            | `ActionContext`      | Actuación, motivo, causa y versión.         |
| `transferId`         | `UUID`               | Transferencia cuya cancelación se solicita. |
| `reason`             | `String`             | Fundamento de cancelación.                  |
| `authorizationBasis` | `AuthorizationBasis` | Facultad para pedir la cancelación.         |

`cancellationId` identifica la nueva actuación de cancelación. No equivale a `transferId` ni a `actionId`.

### 6.3.5. `ReturnTransferCommand`

| Atributo             | Tipo Java               | Uso                                   |
|----------------------|-------------------------|---------------------------------------|
| `context`            | `ActionContext`         | Actuación y causa del retorno.        |
| `transferId`         | `UUID`                  | Operación original.                   |
| `returnEffectId`     | `UUID`                  | Efecto económico único de devolución. |
| `amount`             | `BigDecimal`            | Principal positivo a devolver.        |
| `currency`           | `Currency`              | Moneda de la devolución.              |
| `reasonCode`         | `String`                | Motivo codificado.                    |
| `returnDestination`  | `ReturnDestination`     | Destino de retorno tipado.            |
| `authorizationBasis` | `AuthorizationBasis`    | Facultad o fundamento del retorno.    |
| `relatedReturns`     | `List<ObjectReference>` | Devoluciones vinculadas.              |

`returnId` es la identidad de esta devolución. No reemplaza `returnEffectId`.

### 6.3.6. `IncorporateExternalResultCommand`

| Atributo            | Tipo Java         | Uso                                   |
|---------------------|-------------------|---------------------------------------|
| `transferId`        | `UUID`            | Instrucción cuya historia cambia.     |
| `sourceReference`   | `ObjectReference` | Autoridad y objeto de origen.         |
| `sourceFactId`      | `UUID`            | Hecho identificable en la fuente.     |
| `sourceFactVersion` | `long`            | Versión del hecho comprobado.         |
| `fact`              | `ConfirmedFact`   | Qué ocurrió y con qué evidencias.     |
| `relatedActionId`   | `UUID`            | Actuación relacionada, cuando existe. |

El identificador de mensaje para Inbox pertenece al sobre recibido y no sustituye la identidad del hecho.
La fuente autenticada debe coincidir con quien está autorizado a informar el resultado.

### 6.4. Consultas

| Consulta                 | Datos requeridos                                  | Regla                             |
|--------------------------|---------------------------------------------------|-----------------------------------|
| `GetTransferQuery`       | `transferId` y contexto de acceso.                | Lectura por objeto autorizado.    |
| `GetTransferActionQuery` | `actionId` y contexto de acceso.                  | Recuperar actuación original.     |
| `SearchTransfersQuery`   | Finalidad, identidad externa completa y contexto. | No buscar por referencia aislada. |

La identidad externa comprende participante, mecanismo y referencia. La versión del mecanismo no crea una
identidad de negocio diferente.

### 6.5. Validación de entrada frente a validación empresarial

- **Adaptador de entrada:** forma, formato, encabezados, deserialización y autenticación técnica.
- **Aplicación:** identidad de actuación, idempotencia, versión esperada, autorizaciones y coordinación.
- **Dominio:** invariantes de `Transfer`, cancelación, remanente, devolución y objetos de valor.
- **Autoridad externa:** decisiones propias sobre cuentas, fondos, representación, controles e intercambio.

No confiar en que una anotación de validación HTTP protege una regla económica bajo concurrencia.

## 7. Modelos de resultado

### 7.1. `TransferActionResult`

| Dato                | Significado                                                    |
|---------------------|----------------------------------------------------------------|
| `actionId`          | Identidad de la actuación original.                            |
| `operationId`       | Operación de negocio asociada.                                 |
| `processingStatus`  | `PENDING`, `COMPLETED` o `REQUIRES_REVIEW`.                    |
| `outcome`           | Resultado tipado de la operación correspondiente.              |
| `receivedAt`        | Recepción efectiva reconocida por el servicio.                 |
| `confirmedAt`       | Confirmación final, cuando existe.                             |
| `objectReferences`  | Transferencia, cancelación o devolución relacionadas.          |
| `confirmedEffects`  | Efectos comprobados, no efectos supuestos.                     |
| `confirmedFacts`    | Hechos comprobados, con su procedencia.                        |
| `pendingComponents` | Componentes con autoridad, motivo y referencia de seguimiento. |
| `statusResource`    | Dirección lógica de consulta posterior.                        |

El contrato HTTP transporta estos datos mediante sus esquemas. La aplicación no debe devolver entidades JPA ni
establecer un código HTTP para decidir que el dinero fue acreditado.

### 7.2. Vistas de consulta

| Vista                  | Contenido principal                                                              |
|------------------------|----------------------------------------------------------------------------------|
| `TransferView`         | Instrucción, destino histórico, hechos, movimientos, intercambio y devoluciones. |
| `TransferActionView`   | Petición original, avance comprobado, efectos, fechas y seguimiento.             |
| `TransferSearchResult` | Coincidencias autorizadas por identidad externa completa.                        |

Las vistas deben construirse desde información coherente y únicamente con los datos autorizados.
Consultar no debe invocar un comando remoto de débito, reserva, despacho, cancelación o devolución.

## 8. Puertos de salida hacia autoridades del banco

Un puerto describe una **capacidad exigida por Transferencias**. El adaptador decide cómo hablar con su proveedor.

### 8.1. Cuentas, destino, representación y controles

| Puerto                        | Operación conceptual                                   | Autoridad                |
|-------------------------------|--------------------------------------------------------|--------------------------|
| `AccountAuthorityPort`        | Comprobar cuenta, moneda y capacidad de operación.     | Cuentas.                 |
| `DestinationResolutionPort`   | Resolver CBU o alias y devolver destino verificable.   | Identificación bancaria. |
| `RepresentationAuthorityPort` | Comprobar facultades sobre actor, cuenta y acto.       | Representación.          |
| `BusinessControlsPort`        | Solicitar controles exigibles, con alcance y vigencia. | Riesgo y cumplimiento.   |
| `CommercialConditionsPort`    | Recuperar condiciones y determinar cargos pertinentes. | Autoridad comercial.     |
| `CurrencyExchangePort`        | Vincular una conversión autorizada cuando se requiera. | Cambio de moneda.        |

Los resultados de estos puertos deben incluir referencia a autoridad, versión o fecha y evidencias necesarias.
Una consulta favorable no sustituye una preparación que deba proteger condiciones frente a modificaciones concurrentes.

### 8.2. `FundsAuthorityPort` — Autoridad de Fondos

| Capacidad                       | Necesidad empresarial                                           |
|---------------------------------|-----------------------------------------------------------------|
| `executeInternalMovements(...)` | Confirmar conjuntamente débito y crédito de una interna.        |
| `createReservation(...)`        | Proteger principal y cargos antes de salida externa.            |
| `protectReservation(...)`       | Mantener cobertura ante envío o respuesta incierta.             |
| `consumeReservation(...)`       | Convertir cobertura en efecto, sin doble débito.                |
| `releaseReservation(...)`       | Liberar solo cuando no existe ejecución o compromiso pendiente. |
| `creditIncoming(...)`           | Solicitar un crédito entrante con efecto único.                 |
| `executeReturnEffect(...)`      | Solicitar el efecto autorizado de una devolución.               |
| `recoverMonetaryAction(...)`    | Consultar por identidad estable tras perder la respuesta.       |
| `getReservation(...)`           | Comprobar alcance y situación de una cobertura.                 |

Estas operaciones son **nombres orientativos del puerto**, no endpoints que deba ofrecer Fondos exactamente así.

Cada petición con efecto contiene identidad remota y, cuando corresponde, identidad económica estable.
Un timeout no autoriza a crear otro `effectId` ni a llamar a `executeInternalMovements(...)` otra vez sin recuperación.

### 8.3. `ExchangeAuthorityPort` — Intercambio externo

| Capacidad                  | Necesidad empresarial                                              |
|----------------------------|--------------------------------------------------------------------|
| `validateMechanism(...)`   | Comprobar participante, mecanismo, versión y operación habilitada. |
| `prepareExchange(...)`     | Crear intención externa identificada y recuperable.                |
| `dispatchExchange(...)`    | Solicitar el envío respetando cobertura y cancelación.             |
| `requestCancellation(...)` | Coordinar cancelación del intercambio existente.                   |
| `requestReturn(...)`       | Tramitar retorno externo cuando corresponda.                       |
| `recoverExchange(...)`     | Consultar el intercambio original y sus hechos.                    |

Compensación administra intercambios, mensajes, resultados externos y conciliación.
Transferencias conserva referencias y hechos, pero no sustituye al mecanismo con decisiones propias.

Una respuesta de `prepareExchange(...)` no implica `DISPATCHED`. Un acuse no implica crédito del beneficiario.

### 8.4. Capacidades condicionales

Cuando el circuito real lo exige, el caso de uso también utiliza capacidades de:

- Institución y condiciones de producto: habilitación del acto, producto y reglas comerciales.
- Restricciones, fraude y prevención: decisiones sobre el contenido concreto, no listas genéricas asumidas.
- Criterios tributarios: conceptos y determinaciones correspondientes a la operación.
- Cambio de moneda: conversión explícita, sin cambiar moneda dentro de `Transfer` por cálculo propio.

No se requiere ejecutar todas esas aplicaciones en el laboratorio. Sí se deben respetar las condiciones aplicables
y los contratos de las dependencias efectivamente utilizadas, mediante simuladores independientes cuando corresponda.

## 9. Puertos de salida para datos y consistencia local

| Puerto                      | Responsabilidad                                                           |
|-----------------------------|---------------------------------------------------------------------------|
| `TransferRepository`        | Guardar y recuperar `Transfer` y sus componentes con identidad y versión. |
| `TransferActionRepository`  | Conservar actuación original, resultado y referencias de seguimiento.     |
| `IdempotencyRepository`     | Relacionar clave, actuación y huella de contenido para deduplicar.        |
| `TransferHistoryRepository` | Registrar cambios y evidencias sin borrar decisiones previas.             |
| `TransferOutboxRepository`  | Guardar eventos durables que se publicarán después del commit.            |
| `TransferInboxRepository`   | Deduplicar resultados y hechos recibidos por mensajería.                  |
| `RecoveryJobRepository`     | Programar y recuperar trabajos vinculados a pasos remotos.                |
| `LocalUnitOfWork`           | Confirmar atómicamente los registros de una decisión local.               |

### 9.1. Funciones esenciales de `TransferRepository`

- `findById(...)`: reconstruye la transferencia sin ejecutar efectos.
- `findByOriginIdentity(...)`: reconoce la misma instrucción de origen.
- `findByExternalIdentity(...)`: consulta la clave externa completa.
- `save(...)`: conserva cambios de agregado y control de versión.
- `lockForUpdate(...)`: obtiene protección concurrente donde la operación lo necesita.

Una búsqueda de identidad y la escritura posterior deben sostenerse con unicidad en PostgreSQL.
No alcanza con ejecutar `exists(...)` y luego `save(...)` bajo dos peticiones simultáneas.

### 9.2. Funciones esenciales de actuaciones e idempotencia

- Buscar por `actionId` la actuación original y su resultado recuperable.
- Registrar clave técnica de idempotencia y hash canónico del contenido.
- Recuperar una repetición coherente sin reejecutar ningún efecto remoto.
- Informar conflicto ante la misma identidad y distinto contenido.
- Conservar en una sola confirmación local la actuación y el registro de idempotencia.

La misma operación externa también exige una restricción única de negocio independiente de la clave HTTP.
La misma devolución exige unicidad de `returnEffectId`, aunque cambie la clave técnica.

### 9.3. Concurrencia y límites de las transacciones

`LocalUnitOfWork` delimita un conjunto de cambios en **la base propia**. Puede implementarse mediante
transacciones de Spring en infraestructura de ejecución sin acoplar las reglas del dominio a ese mecanismo.

No debe dejar una conexión SQL abierta durante la llamada HTTP al servicio de Fondos, Compensación o un simulador.

## 10. Ejecución técnica de cada caso de uso

Las secuencias siguientes muestran **cómo se organiza la coordinación**.
Las condiciones y ejemplos empresariales completos pertenecen a `casos-de-uso.md`.

### 10.1. `ExecuteInternalTransfer` — Ejecución interna

**Servicio propuesto:** `InternalTransferService`.

**Dependencias principales:** repositorios locales, cuentas, destino, representación, controles, Fondos y
unidad de trabajo.

| Tramo | Operación de aplicación                                  | Garantía                                  |
|-------|----------------------------------------------------------|-------------------------------------------|
| 1     | Recuperar identidad y huella de la instrucción.          | Repetición coherente o conflicto.         |
| 2     | Comprobar cuenta, destino y facultades exigibles.        | Origen y destino válidos.                 |
| 3     | Crear `Transfer` y ejecución monetaria desde el dominio. | Invariantes protegidas.                   |
| 4     | Persistir actuación e intención remota.                  | Solicitud durable antes de llamar.        |
| 5     | Pedir a Fondos movimiento conjunto.                      | No efectuar débito/crédito en aplicación. |
| 6     | Incorporar resultado o incertidumbre.                    | Identidad remota conservada.              |
| 7     | Confirmar historia, seguimiento y Outbox.                | Cambio local atómico.                     |

La aplicación no confirma el caso por recibir HTTP 200 sin evidencia del conjunto de débito y crédito.

### 10.2. `SendOutgoingTransfer` — Salida externa

**Servicio propuesto:** `OutgoingTransferService`.

**Dependencias principales:** controles, Fondos, Compensación, datos propios y recuperación.

| Tramo | Operación de aplicación                                 | Garantía                               |
|-------|---------------------------------------------------------|----------------------------------------|
| 1     | Validar identidad, mecanismo, destino y autorización.   | Contenido vinculante.                  |
| 2     | Registrar intención de reserva en un paso recuperable.  | Identidad monetaria estable.           |
| 3     | Constituir cobertura mediante Fondos.                   | No despachar sin reserva suficiente.   |
| 4     | Preparar intercambio y proteger reserva.                | Incertidumbre no libera cobertura.     |
| 5     | Ordenar despacho frente a cancelación bajo bloqueo.     | `dispatchBarrier` respetada.           |
| 6     | Solicitar despacho mediante Compensación.               | Intención persistida antes de invocar. |
| 7     | Incorporar despacho, aceptación y crédito por separado. | Evidencias distintas.                  |
| 8     | Consumir reserva tras crédito comprobado.               | No crear un segundo débito.            |
| 9     | Guardar resultado, historia, recuperación y Outbox.     | Reanudación posible.                   |

La acreditación puede llegar por un resultado posterior. No forzar todo el recorrido dentro de una sola petición.

### 10.3. `ReceiveIncomingTransfer` — Entrada externa

**Servicio propuesto:** `IncomingTransferService`.

**Dependencias principales:** identidad externa, cuenta de destino, controles, Fondos e Inbox cuando corresponda.

| Tramo | Operación de aplicación                                | Garantía                           |
|-------|--------------------------------------------------------|------------------------------------|
| 1     | Verificar procedencia del servicio y participante.     | Origen autorizado.                 |
| 2     | Buscar participante, mecanismo y referencia externos.  | Unicidad entre recepciones.        |
| 3     | Contrastar la huella recibida con la original.         | Duplicado coherente o conflicto.   |
| 4     | Comprobar destino, moneda y restricciones aplicables.  | Antes de aceptar irrevocablemente. |
| 5     | Registrar aceptación e identidad del crédito.          | Efecto económico único.            |
| 6     | Solicitar crédito a Fondos fuera de transacción local. | Sin acreditar desde aplicación.    |
| 7     | Incorporar confirmación o recuperar incertidumbre.     | Sin segundo crédito.               |
| 8     | Confirmar hechos, seguimiento, historia y evento.      | Evidencia y trazabilidad.          |

Una aceptación firme sin crédito confirmado conserva la obligación de acreditar o resolver el retorno procedente.

### 10.4. `CancelTransfer` — Cancelación

**Servicio propuesto:** `TransferCancellationService`.

**Dependencias principales:** transferencia, actuaciones, Compensación y Fondos cuando haya cobertura.

| Tramo | Operación de aplicación                                   | Garantía                          |
|-------|-----------------------------------------------------------|-----------------------------------|
| 1     | Validar actor, actuación, motivo y versión.               | Petición identificada.            |
| 2     | Bloquear la transferencia y revisar el intercambio.       | Orden frente a despacho.          |
| 3     | Si aún es cancelable, activar barrera según dominio.      | No permitir despacho posterior.   |
| 4     | Si existe intercambio, pedir cancelación externa.         | No asumir que se detuvo.          |
| 5     | Conservar reserva hasta evidencia definitiva.             | No liberar cobertura por timeout. |
| 6     | Registrar resultado, cancelación, historia y seguimiento. | Decisión durable.                 |

Una acreditación confirmada conduce al tratamiento de devolución; no se borra la operación original.

### 10.5. `ReturnTransfer` — Devolución

**Servicio propuesto:** `TransferReturnService`.

**Dependencias principales:** control del remanente, autoridad de efecto, referencia original y recuperación.

| Tramo | Operación de aplicación                                  | Garantía                     |
|-------|----------------------------------------------------------|------------------------------|
| 1     | Comprobar facultad, causa, importe y destino de retorno. | Efecto justificado.          |
| 2     | Bloquear transferencia y control económico.              | Decisión concurrente única.  |
| 3     | Comprometer el importe en `TransferReturnControl`.       | No exceder remanente.        |
| 4     | Crear `TransferReturn` con efecto económico único.       | No duplicar devolución.      |
| 5     | Registrar intención remota y confirmar localmente.       | Recuperación posible.        |
| 6     | Solicitar efecto a la autoridad correspondiente.         | Sin débito arbitrario local. |
| 7     | Confirmar, rechazar o conservar incertidumbre.           | Remanente coherente.         |
| 8     | Registrar historia, actuación, seguimiento y Outbox.     | Resultado auditable.         |

La confirmación traslada importe de comprometido a devuelto. El rechazo definitivamente probado puede liberarlo.

### 10.6. `GetTransfer` — Consulta de transferencia

**Servicio propuesto:** `TransferQueryService`.

1. Comprobar alcance autorizado sobre `transferId`.
2. Recuperar una vista coherente con sus componentes vinculados.
3. Exponer destino histórico, hechos, reservas, movimientos, devoluciones y pendientes autorizados.
4. No invocar ninguna actuación económica para completar datos ausentes.

### 10.7. `GetTransferAction` — Consulta de actuación

**Servicio propuesto:** `TransferQueryService`.

1. Comprobar permiso sobre la actuación y el objeto al que pertenece.
2. Recuperar `actionId` original y su resultado persistido.
3. Devolver evidencias, efectos y componentes que requieren seguimiento.
4. No registrar una nueva actuación al consultar la original.

### 10.8. `SearchTransfers` — Consulta por referencia externa

**Servicio propuesto:** `TransferQueryService`.

1. Exigir finalidad autorizada y `ExternalOperationReference` completa.
2. Buscar por participante, mecanismo y referencia, sin incluir versión en la clave de identidad.
3. Comprobar autorización sobre los resultados antes de devolverlos.
4. No ejecutar reservas, despachos ni movimientos como consecuencia de la búsqueda.

## 11. Secuencia transaccional obligatoria

La mayoría de los comandos persistentes tiene **tres momentos**, separados por fronteras técnicas.

```text
Comando identificado
        |
        v
[Transacción local A]
  Identidad de instrucción + actuación + idempotencia
  Decisión del dominio + intención remota + seguimiento
        |
     COMMIT
        |
        v
[Llamada remota SIN transacción SQL abierta]
  Fondos o Compensación con remoteActionId/effectId originales
        |
        v
[Transacción local B]
  Resultado confirmado, rechazo con evidencia o incertidumbre
  Agregado + historia + actuación + trabajo + Outbox
        |
     COMMIT
        |
        v
Respuesta con hechos confirmados y componentes pendientes
```

No todos los comandos requieren una llamada remota. Una consulta usa lectura y no crea efectos.

### 11.1. Qué debe estar en cada transacción

| Conjunto atómico           | Registros vinculados                                                     |
|----------------------------|--------------------------------------------------------------------------|
| Aceptación durable         | Instrucción, actuación, idempotencia, intención y próxima continuación.  |
| Incorporación de resultado | Agregado, evidencia, historia, resultado de actuación, trabajo y Outbox. |
| Consumo de devolución      | Control de remanente, devolución, acción e intención del efecto.         |
| Recepción de evento        | Inbox, efecto local y continuación, dentro de una transacción.           |
| Publicación durable        | Evento Outbox confirmado con el cambio empresarial correspondiente.      |

Una anotación o un método transaccional solo en memoria no garantiza atomicidad de varios repositorios.
La implementación debe compartir una transacción real de PostgreSQL en cada conjunto.

## 12. Idempotencia: cuatro identidades que no deben confundirse

| Identidad                     | Qué reconoce                          | Propiedad                       |
|-------------------------------|---------------------------------------|---------------------------------|
| `actionId`                    | La petición original.                 | Permite consultar su resultado. |
| `Idempotency-Key`             | La repetición técnica del comando.    | No define el efecto económico.  |
| `remoteActionId`              | La actuación solicitada al proveedor. | No cambia en la recuperación.   |
| `effectId` / `returnEffectId` | El movimiento o retorno económico.    | No puede producirse dos veces.  |

### 12.1. Decisión ante repetición

```text
Llega comando identificado
        |
        v
¿Existe actuación o clave original?
     |                    |
    Sí                   No
     |                    |
     v                    v
¿Misma huella?       Registrar identidad e intención
   |       |          con protección de unicidad
  Sí       No
   |       |
   v       v
Resultado  Conflicto
original   sin efecto
```

El cálculo de la huella debe incluir el contenido vinculante en una representación canónica versionada.
Dos solicitudes con el mismo importe pero distinta identidad de negocio no se deduplican arbitrariamente.

La búsqueda previa es útil, pero la protección definitiva necesita restricciones únicas de persistencia.

## 13. Concurrencia: decisiones que deben ordenarse

### 13.1. Orden de bloqueo local

```text
1. Transfer
2. TransferReturnControl
3. TransferCancellation o TransferReturn
4. Ejecución y pasos remotos relacionados
```

Este orden evita inversiones de bloqueo entre operaciones que afectan la misma transferencia.

### 13.2. Cancelación frente a despacho

- El despacho debe leer y proteger la barrera bajo una transacción que impida una decisión incompatible.
- La cancelación debe competir sobre la misma transferencia, no sobre una variable local de Java.
- El adaptador no puede empezar un envío tardío si la decisión autoritativa ya cerró esa posibilidad.
- Si el envío pudo ocurrir, la cancelación requiere evidencia externa de no ejecución futura.

### 13.3. Devoluciones simultáneas

- El caso bloquea la transferencia y su control de devoluciones antes de comprometer importe.
- La operación de reserva del remanente se confirma atómicamente con la devolución identificada.
- Dos solicitudes de 40 sobre un remanente de 60 no pueden quedar ambas comprometidas.
- Una devolución incierta no libera importe hasta recuperar resultado definitivo.

### 13.4. Versiones

- `version` expresa la versión de negocio del agregado.
- `lockVersion` soporta el control concurrente local.
- `expectedVersion` identifica la versión que el solicitante cree estar modificando.
- Un conflicto no debe sobrescribir decisiones previas ni reintentar automáticamente un efecto remoto.

La estrategia de bloqueo concreta pertenece al adaptador de persistencia y debe probarse con PostgreSQL real.

## 14. Recuperación de actuaciones remotas

### 14.1. `RecoverRemoteStep` — Recuperar el mismo paso

Un paso durable identifica componente, proveedor, contenido, `remoteActionId` y `effectId` cuando corresponda.

| Paso de recuperación    | Responsabilidad                                         |
|-------------------------|---------------------------------------------------------|
| Recuperar intención     | Leer la solicitud original y el proveedor responsable.  |
| Consultar por identidad | Preguntar por la actuación original, no crear otra.     |
| Comparar evidencia      | Distinguir confirmación, rechazo e incertidumbre.       |
| Aplicar resultado       | Modificar agregado y paso mediante sus comportamientos. |
| Registrar seguimiento   | Persistir fechas, historia y próxima revisión.          |
| Comunicar cambio        | Generar Outbox con el hecho correspondiente.            |

Las consultas de recuperación ocurren al detectar incertidumbre, a los cinco minutos y a los treinta minutos.
Si persiste, corresponde revisión en cada jornada operativa. El tiempo no prueba rechazo ni permite liberar fondos.

### 14.2. `IncorporateExternalResult` — Resultado por canal diferente

- Verificar autenticación y autoridad del emisor.
- Comprobar identidad de intercambio, actuación, hecho y versión de fuente.
- Usar Inbox y deduplicación del hecho para no aplicar dos veces el mismo resultado.
- Rechazar o investigar contradicciones sin borrar evidencia previa.
- Incorporar un hecho tardío a la operación original aunque la petición inicial ya haya terminado.

### 14.3. `ResumeTransferWork` — Reanudar tras reinicio

- Leer trabajos persistentes y pasos que requieren comprobación.
- Recuperar su propietario y el contexto de actuación guardado.
- Consultar autoridades usando las identidades originales.
- Aplicar resultados compatibles y reagendar los que continúan sin resolución.
- No utilizar el inicio del proceso como autorización para ejecutar otra vez efectos económicos.

### 14.4. Registro y entrega de eventos

`RecordTransferChange` produce el cambio `TransferChanged` con identidad y versión de agregado.
El publicador Outbox envía el evento después del commit local y puede reenviar el mismo evento si pierde el acuse.

No se presume entrega exactamente una vez. El consumidor comprueba identidad, versión y continuidad del hecho.

## 15. Seguridad: dónde se comprueba cada cosa

| Lugar                | Comprobación                                                        |
|----------------------|---------------------------------------------------------------------|
| Adaptador de entrada | Validez de la credencial y autenticidad del servicio llamador.      |
| Caso de uso          | Permiso y alcance sobre la transferencia y la finalidad solicitada. |
| Autoridades          | Facultades de cuenta, representación y restricciones en su ámbito.  |
| Dominio              | Invariantes internas de la instrucción, cancelación o devolución.   |
| Consulta             | Visibilidad del objeto y de cada dato devuelto.                     |

Los permisos de originar, cancelar, devolver, consultar y recuperar son distintos.
Un UUID conocido no concede acceso al expediente. Una solicitud externa no puede atribuirse cualquier participante.

No colocar credenciales ni datos sensibles innecesarios en logs, métricas o eventos.

## 16. Observabilidad incorporada a la aplicación

### 16.1. Qué registrar y por qué

| Señal            | Cuándo                            | Pregunta que permite responder          |
|------------------|-----------------------------------|-----------------------------------------|
| Log estructurado | Comando aceptado o conflicto.     | ¿Qué actuación se reconoció?            |
| Log estructurado | Resultado remoto o incertidumbre. | ¿Qué proveedor debe resolverlo?         |
| Traza            | Paso de caso de uso a un puerto.  | ¿Dónde ocurrió la demora?               |
| Métrica          | Actuaciones por tipo y resultado. | ¿Cuántos comandos se resuelven?         |
| Métrica          | Pasos inciertos por dependencia.  | ¿Qué integraciones requieren atención?  |
| Métrica          | Antigüedad de trabajos y Outbox.  | ¿Hay procesos sin continuar o publicar? |

### 16.2. Campos útiles de correlación

- `operationId`: recorrido empresarial.
- `actionId`: actuación original.
- `remoteActionId`: interacción identificada con un proveedor.
- `componentCode`: componente afectado, sin usar nombres de personas ni números de cuenta.
- `outcome`: resultado tipado sin datos sensibles.

No usar `transferId`, `accountId`, nombres de clientes ni identificadores de instrucciones como etiquetas de métricas.
Se pueden conservar referencias protegidas en trazas y logs cuando la política de datos lo permita.

### 16.3. Qué no significa una métrica

Una métrica de solicitudes HTTP exitosas no demuestra que las transferencias estén acreditadas.
La aplicación diferencia aceptación durable, actuación remota, acreditación y resolución de pendientes.

## 17. Errores: frontera empresarial y frontera técnica

| Situación                                    | Decisión de aplicación                        | Tratamiento en el adaptador |
|----------------------------------------------|-----------------------------------------------|-----------------------------|
| Contenido incompatible                       | No ejecutar otro efecto; informar conflicto.  | Respuesta de conflicto.     |
| Versión incompatible                         | Conservar el original; no sobrescribir.       | Respuesta de conflicto.     |
| Destino distinto del aceptado                | Rechazar modificación material no autorizada. | Error de negocio.           |
| Principal o remanente insuficiente           | No comprometer importe inválido.              | Error de negocio.           |
| Dependencia indisponible antes de aceptar    | No fingir actuación durable.                  | Error de disponibilidad.    |
| Solicitud durable, resultado remoto incierto | Guardar seguimiento y misma identidad.        | Resultado consultable.      |
| Falta de permiso                             | No exponer ni modificar el objeto.            | Error de autorización.      |

Los adaptadores HTTP pueden convertir estas situaciones a Problem Details y códigos 400, 403, 409, 422 o 503,
según el contrato. Las clases de dominio y los puertos no deben depender de respuestas HTTP.

Una actuación durable por resolver puede responder 202; eso no constituye acreditación ni éxito económico.

## 18. Adaptadores de entrada y salida: límites contractuales

### 18.1. Entradas

| Entrada                 | Responsabilidad                                                    |
|-------------------------|--------------------------------------------------------------------|
| HTTP                    | Deserializar, autenticar, validar forma y traducir al caso.        |
| Mensaje de Compensación | Verificar emisor, deduplicar e incorporar resultado externo.       |
| Trabajo persistente     | Cargar paso original e invocar recuperación con identidad estable. |

Un adaptador no puede llamar directamente a `Transfer` para saltarse la coordinación, la persistencia y la seguridad.

### 18.2. Salidas

| Salida                       | Implementación esperada                                             |
|------------------------------|---------------------------------------------------------------------|
| Persistencia                 | Adaptadores PostgreSQL con versiones y restricciones únicas.        |
| Simulador de Fondos          | Puerto que conserva actuaciones y efectos con consulta recuperable. |
| Simulador de Cuentas         | Puerto que informa capacidades y condiciones verificables.          |
| Simulador de identificadores | Puerto que conserva la resolución que originó el destino aceptado.  |
| Simulador de Compensación    | Puerto que distingue preparación, envío, aceptación y crédito.      |
| Outbox                       | Registro local durable y publicador posterior al commit.            |
| Inbox                        | Deduplicación transaccional de hechos y mensajes entrantes.         |

Los simuladores no reemplazan integraciones por lógica de negocio dentro de `TransferService`.
Deben ser independientes, consultables y capaces de devolver efectos ya ejecutados aunque se pierda la respuesta.

## 19. Excepciones de aplicación: `application/exception/`

### 19.1. Propósito y responsabilidad

La carpeta `exception/` contiene las excepciones que representan errores detectados durante la coordinación de los
casos de uso de Transferencias.

Estas excepciones permiten identificar por qué una actuación no puede continuar, qué condición falló y qué
información necesita conservarse para comunicar o investigar el problema.

La aplicación debe distinguir entre:

- Errores en la identidad, autorización o contexto de una actuación.
- Conflictos de idempotencia y de identidad empresarial.
- Modificaciones concurrentes incompatibles.
- Recursos inexistentes o inaccesibles.
- Dependencias externas indisponibles.
- Resultados externos contradictorios.
- Información insuficiente para recuperar una actuación original.

**Regla principal:** una excepción de aplicación no debe sustituir un resultado empresarial válido.

Por ejemplo, una transferencia rechazada correctamente por Fondos constituye un resultado comprobado.
Una transferencia cuyo resultado monetario se desconoce constituye una actuación pendiente de resolución.

Ninguna de esas situaciones debe convertirse arbitrariamente en una excepción que haga perder su seguimiento.

Las excepciones de aplicación tampoco reemplazan las invariantes protegidas por `domain/`.

### 19.2. Estructura propuesta

```text
application/
└── exception/
    ├── ApplicationException.java
    ├── InvalidActionContextException.java
    ├── TransferNotFoundException.java
    ├── TransferActionNotFoundException.java
    ├── TransferAccessDeniedException.java
    ├── IdempotencyConflictException.java
    ├── TransferIdentityConflictException.java
    ├── TransferVersionConflictException.java
    ├── ExternalAuthorityUnavailableException.java
    ├── ExternalResultConflictException.java
    └── RecoveryContextMissingException.java
```

Esta estructura es una propuesta técnica para representar los errores necesarios en los casos de uso.

No obliga a lanzar todas las excepciones desde todos los servicios ni a crear jerarquías adicionales.

### 19.3. Catálogo de excepciones

| Excepción                               | Significado                      | Responsabilidad                                |
|-----------------------------------------|----------------------------------|------------------------------------------------|
| `ApplicationException`                  | Excepción base                   | Unificar errores propios de aplicación.        |
| `InvalidActionContextException`         | Contexto inválido                | Impedir actuaciones sin contexto válido.       |
| `TransferNotFoundException`             | Transferencia inexistente        | Identificar una transferencia no encontrada.   |
| `TransferActionNotFoundException`       | Actuación inexistente            | Identificar una actuación no encontrada.       |
| `TransferAccessDeniedException`         | Acceso denegado                  | Impedir una actuación sin autorización.        |
| `IdempotencyConflictException`          | Conflicto de idempotencia        | Proteger la identidad de una petición.         |
| `TransferIdentityConflictException`     | Conflicto de identidad           | Proteger la identidad empresarial.             |
| `TransferVersionConflictException`      | Conflicto de versión             | Impedir modificaciones desactualizadas.        |
| `ExternalAuthorityUnavailableException` | Autoridad indisponible           | Identificar una dependencia no accesible.      |
| `ExternalResultConflictException`       | Resultado contradictorio         | Identificar evidencias externas incompatibles. |
| `RecoveryContextMissingException`       | Contexto de recuperación ausente | Impedir recuperar sin identidad original.      |

### 19.4. `ApplicationException` — Excepción base

**Tipo:** clase abstracta que extiende `RuntimeException`.

**Responsabilidad:** proporcionar una representación común para los errores originados en `application/`.

| Atributo     | Tipo Java   | Responsabilidad                             |
|--------------|-------------|---------------------------------------------|
| `errorCode`  | `String`    | Código estable del error de aplicación.     |
| `message`    | `String`    | Descripción del problema.                   |
| `actionId`   | `UUID`      | Actuación relacionada, cuando existe.       |
| `transferId` | `UUID`      | Transferencia relacionada, cuando existe.   |
| `cause`      | `Throwable` | Causa técnica original, cuando corresponde. |

**Reglas:**

- `errorCode` debe permitir distinguir el error sin analizar el texto de `message`.
- `message` no debe utilizarse para tomar decisiones empresariales.
- `actionId` y `transferId` son referencias de diagnóstico, no autorizaciones.
- Los atributos opcionales pueden ser nulos cuando todavía no existe la identidad correspondiente.
- La excepción no contiene códigos HTTP ni depende de Spring.
- No debe incorporar credenciales, datos bancarios sensibles ni mensajes completos de proveedores.

**Comportamiento:**

- Permitir que el caso de uso interrumpa una actuación que no puede continuar válidamente.
- Conservar la causa original cuando el error procede de una dependencia.
- Facilitar que el adaptador de entrada traduzca el error a su contrato de transporte.

### 19.5. `InvalidActionContextException` — Contexto de actuación inválido

**Tipo:** excepción de aplicación.

**Responsabilidad:** impedir que una actuación avance cuando falta información necesaria para identificarla,
relacionarla o comprobar su legitimidad.

**Condiciones de lanzamiento:**

- Falta una identidad de actuación obligatoria.
- No existe el contexto verificado requerido para ejecutar la operación.
- El contexto presenta identidades incompatibles con la actuación solicitada.
- Una continuación intenta utilizar un contexto que no corresponde a la operación original.

**Información relevante:**

| Dato          | Propósito                                        |
|---------------|--------------------------------------------------|
| `actionId`    | Identificar la actuación cuando está disponible. |
| `operationId` | Reconocer la operación empresarial relacionada.  |
| `errorCode`   | Distinguir qué condición del contexto falló.     |

**Casos de uso:** comandos persistentes y continuaciones internas.

**Tratamiento:**

- No ejecutar efectos económicos.
- No generar identidades nuevas para completar silenciosamente un contexto inválido.
- Informar que la actuación no puede procesarse con la información proporcionada.

No sustituye las validaciones de formato que corresponden al adaptador de entrada.

### 19.6. `TransferNotFoundException` — Transferencia no encontrada

**Tipo:** excepción de aplicación.

**Responsabilidad:** representar una búsqueda que no encuentra la transferencia solicitada.

**Condiciones de lanzamiento:**

- No existe una transferencia correspondiente al `transferId`.
- Una cancelación o devolución hace referencia a una transferencia inexistente.
- Una consulta autorizada intenta recuperar un objeto que no existe.

**Información relevante:**

| Dato         | Propósito                                        |
|--------------|--------------------------------------------------|
| `transferId` | Referencia buscada.                              |
| `errorCode`  | Identificar que no se encontró la transferencia. |

**Casos de uso:**

- `GetTransfer`
- `CancelTransfer`
- `ReturnTransfer`

**Tratamiento:**

- Finalizar la actuación sin modificar información.
- No crear una transferencia para satisfacer una referencia inexistente.
- No revelar información sobre objetos para los que el solicitante carece de permisos.

Una transferencia existente pero inaccesible requiere aplicar la política de seguridad correspondiente.

### 19.7. `TransferActionNotFoundException` — Actuación no encontrada

**Tipo:** excepción de aplicación.

**Responsabilidad:** indicar que no existe una actuación persistida con la identidad solicitada.

**Condiciones de lanzamiento:**

- `GetTransferAction` no encuentra el `actionId`.
- Una consulta intenta recuperar una actuación que nunca fue registrada.

**Información relevante:**

| Dato        | Propósito                            |
|-------------|--------------------------------------|
| `actionId`  | Identificar la actuación buscada.    |
| `errorCode` | Distinguir la ausencia de actuación. |

**Caso de uso:** `GetTransferAction`.

**Tratamiento:**

- No crear ni ejecutar una actuación para satisfacer la consulta.
- No deducir que una transferencia fue rechazada porque no se encontró una actuación.
- No exponer actuaciones pertenecientes a operaciones fuera del alcance autorizado.

Esta excepción no debe utilizarse automáticamente durante la recuperación de un paso remoto.
En ese contexto puede existir una inconsistencia que necesita investigación.

### 19.8. `TransferAccessDeniedException` — Acceso denegado

**Tipo:** excepción de aplicación.

**Responsabilidad:** impedir operaciones cuando el actor o servicio solicitante no tiene facultades suficientes.

**Condiciones de lanzamiento:**

- El solicitante no puede consultar la transferencia.
- El actor carece de autorización para cancelar o devolver.
- El servicio llamador intenta ejecutar una capacidad fuera de su alcance.
- Una actuación intenta acceder a información no permitida para su finalidad.

**Información relevante:**

| Dato         | Propósito                            |
|--------------|--------------------------------------|
| `actionId`   | Actuación que requiere autorización. |
| `transferId` | Objeto afectado, cuando corresponde. |
| `errorCode`  | Identificar la denegación de acceso. |

**Casos de uso:** todos, según la capacidad solicitada.

**Tratamiento:**

- Interrumpir la operación antes de producir efectos no autorizados.
- No exponer datos de la transferencia en el mensaje de error.
- Conservar la información necesaria para auditoría sin registrar datos sensibles.

La autenticación técnica del consumidor corresponde al adaptador de entrada.

Las facultades sobre cuentas y representaciones se comprueban mediante las autoridades competentes.
La aplicación coordina esas comprobaciones, pero no reemplaza sus decisiones.

### 19.9. `IdempotencyConflictException` — Conflicto de idempotencia

**Tipo:** excepción de aplicación.

**Responsabilidad:** impedir que una misma identidad técnica de petición se utilice con contenido diferente.

**Condiciones de lanzamiento:**

- Existe una `Idempotency-Key` registrada con otra huella de contenido.
- La petición intenta reutilizar la identidad de una actuación con datos incompatibles.

**Información relevante:**

| Dato        | Propósito                                       |
|-------------|-------------------------------------------------|
| `actionId`  | Actuación original, cuando puede identificarse. |
| `errorCode` | Identificar el conflicto de idempotencia.       |

**Casos de uso:** comandos persistentes.

**Tratamiento:**

- No generar una nueva actuación económica.
- No reemplazar el contenido de la actuación original.
- Conservar el registro de idempotencia existente.
- Informar el conflicto sin volver a invocar autoridades externas.

**Regla fundamental:**

La misma clave con el mismo contenido debe recuperar la actuación original.
La misma clave con contenido diferente debe generar conflicto.

La excepción no representa una transferencia rechazada por Fondos.

### 19.10. `TransferIdentityConflictException` — Conflicto de identidad empresarial

**Tipo:** excepción de aplicación.

**Responsabilidad:** impedir que una identidad empresarial existente represente instrucciones incompatibles.

**Condiciones de lanzamiento:**

- Una identidad de instrucción de origen ya existe con otro contenido.
- Una referencia externa completa identifica una transferencia con datos diferentes.
- Se detecta una colisión de identidad empresarial protegida por persistencia.

**Información relevante:**

| Dato         | Propósito                                   |
|--------------|---------------------------------------------|
| `actionId`   | Actuación que produjo el conflicto.         |
| `transferId` | Transferencia original, cuando corresponde. |
| `errorCode`  | Identificar el conflicto de identidad.      |

**Casos de uso:**

- `ExecuteInternalTransfer`
- `SendOutgoingTransfer`
- `ReceiveIncomingTransfer`

**Tratamiento:**

- No crear una segunda transferencia con la misma identidad empresarial.
- No modificar la transferencia original para acomodarla al nuevo contenido.
- No ejecutar un segundo movimiento monetario.
- Conservar las referencias necesarias para investigar el conflicto.

La identidad externa comprende participante, mecanismo y referencia de operación.
Una referencia externa aislada no es suficiente para reconocer la instrucción.

La protección definitiva contra duplicaciones debe existir también en PostgreSQL.

### 19.11. `TransferVersionConflictException` — Conflicto de versión

**Tipo:** excepción de aplicación.

**Responsabilidad:** impedir que una actuación sobrescriba cambios confirmados por otra operación concurrente.

**Condiciones de lanzamiento:**

- `expectedVersion` no coincide con la versión empresarial aplicable.
- Una actualización detecta que otro proceso modificó el agregado.
- La persistencia rechaza una modificación por conflicto de versión.

**Información relevante:**

| Dato              | Propósito                                   |
|-------------------|---------------------------------------------|
| `transferId`      | Transferencia afectada.                     |
| `actionId`        | Actuación que intentó modificarla.          |
| `expectedVersion` | Versión esperada por el solicitante.        |
| `actualVersion`   | Versión encontrada, cuando está disponible. |

**Casos de uso:**

- `CancelTransfer`
- `ReturnTransfer`
- Continuaciones que modifican una transferencia existente.

**Tratamiento:**

- No sobrescribir modificaciones confirmadas.
- Recuperar el estado vigente antes de decidir cómo continuar.
- No repetir automáticamente una actuación monetaria para resolver el conflicto.

Debe diferenciarse `expectedVersion` de `lockVersion`.
La primera expresa la expectativa del solicitante y la segunda protege concurrencia local.

### 19.12. `ExternalAuthorityUnavailableException` — Autoridad externa indisponible

**Tipo:** excepción de aplicación.

**Responsabilidad:** representar la imposibilidad de consultar una autoridad requerida cuando todavía puede
interrumpirse la actuación sin dejar un efecto externo incierto.

**Condiciones de lanzamiento:**

- Una autoridad necesaria para una comprobación previa está indisponible.
- No puede completarse una validación que debe resolverse antes de admitir la actuación.
- Una dependencia indispensable no permite obtener una respuesta requerida para continuar de forma segura.

**Información relevante:**

| Dato            | Propósito                              |
|-----------------|----------------------------------------|
| `actionId`      | Actuación afectada, cuando existe.     |
| `authorityCode` | Identificar la autoridad indisponible. |
| `operationCode` | Identificar la capacidad solicitada.   |
| `cause`         | Conservar el error técnico original.   |

**Casos de uso:** operaciones que requieren comprobaciones de autoridades externas.

**Tratamiento:**

- No inventar un resultado de la autoridad.
- No sustituir la comprobación por reglas locales de Transferencias.
- No declarar una transferencia rechazada por una falla de comunicación.
- Conservar la actuación si ya fue admitida de forma durable.

**Distinción obligatoria:**

Si una actuación remota pudo producir un efecto económico y se perdió la respuesta, corresponde conservar
el resultado incierto y recuperar la actuación original.

No debe utilizarse esta excepción para descartar ese seguimiento.

### 19.13. `ExternalResultConflictException` — Resultado externo contradictorio

**Tipo:** excepción de aplicación.

**Responsabilidad:** identificar información externa incompatible con los hechos ya comprobados.

**Condiciones de lanzamiento:**

- Una autoridad informa resultados contradictorios para la misma actuación.
- Un resultado pretende modificar un hecho confirmado de forma incompatible.
- Un mensaje contiene una identidad de efecto diferente de la esperada.
- Una versión o evidencia externa contradice la información conservada.

**Información relevante:**

| Dato              | Propósito                           |
|-------------------|-------------------------------------|
| `transferId`      | Transferencia afectada.             |
| `actionId`        | Actuación relacionada.              |
| `authorityCode`   | Autoridad que informó el resultado. |
| `sourceReference` | Referencia del hecho recibido.      |
| `errorCode`       | Tipo de contradicción identificada. |

**Casos de uso:**

- `ReceiveIncomingTransfer`
- `RecoverRemoteStep`
- `IncorporateExternalResult`

**Tratamiento:**

- No reemplazar automáticamente hechos confirmados.
- No ejecutar efectos compensatorios sin fundamento.
- Conservar la evidencia contradictoria para investigación.
- Mantener el componente correspondiente pendiente de resolución.

La excepción puede comunicar que no fue posible incorporar el resultado como hecho válido.
No debe provocar la pérdida de la evidencia mediante un rollback accidental.

### 19.14. `RecoveryContextMissingException` — Contexto de recuperación ausente

**Tipo:** excepción de aplicación.

**Responsabilidad:** identificar una actuación que necesita recuperación, pero carece de la información indispensable
para consultar su efecto original.

**Condiciones de lanzamiento:**

- Falta el identificador de la actuación remota original.
- No puede reconstruirse la identidad del proveedor responsable.
- La información persistida no permite consultar el efecto económico original.
- Existe un trabajo de recuperación sin el contexto necesario para continuarlo.

**Información relevante:**

| Dato               | Propósito                                        |
|--------------------|--------------------------------------------------|
| `transferId`       | Transferencia relacionada.                       |
| `actionId`         | Actuación original, cuando se conoce.            |
| `componentCode`    | Componente que necesita recuperación.            |
| `missingReference` | Identificar la referencia indispensable ausente. |

**Casos de uso:**

- `RecoverRemoteStep`
- `ResumeTransferWork`

**Tratamiento:**

- No generar una nueva identidad para repetir el efecto.
- No asumir que el efecto anterior falló.
- Registrar el impedimento para continuar la recuperación.
- Mantener la operación bajo seguimiento e investigación.

Una referencia ausente no autoriza a liberar fondos ni a declarar rechazado un efecto económico.

### 19.15. Relación entre excepciones y casos de uso

| Caso de uso                 | Excepciones de aplicación relevantes                                 |
|-----------------------------|----------------------------------------------------------------------|
| `ExecuteInternalTransfer`   | Contexto, acceso, idempotencia, identidad, autoridad.                |
| `SendOutgoingTransfer`      | Contexto, acceso, idempotencia, identidad, autoridad.                |
| `ReceiveIncomingTransfer`   | Contexto, identidad, idempotencia, autoridad, resultado externo.     |
| `CancelTransfer`            | Transferencia inexistente, acceso, versión, idempotencia, autoridad. |
| `ReturnTransfer`            | Transferencia inexistente, acceso, versión, idempotencia, autoridad. |
| `GetTransfer`               | Transferencia inexistente, acceso.                                   |
| `GetTransferAction`         | Actuación inexistente, acceso.                                       |
| `SearchTransfers`           | Acceso, contexto inválido.                                           |
| `RecoverRemoteStep`         | Contexto de recuperación, versión, resultado externo.                |
| `IncorporateExternalResult` | Contexto, versión, resultado externo.                                |
| `ResumeTransferWork`        | Contexto de recuperación, versión, resultado externo.                |

Esta tabla identifica posibilidades, no obliga a lanzar todas las excepciones en cada recorrido.

Los resultados empresariales esperados deben seguir expresándose mediante los modelos de resultado correspondientes.

### 19.16. Diferencia entre excepciones de dominio y aplicación

| Responsabilidad                    | Ubicación                | Ejemplo                                     |
|------------------------------------|--------------------------|---------------------------------------------|
| Importe inválido                   | `domain/exception/`      | Principal que viola una invariante.         |
| Transición empresarial prohibida   | `domain/exception/`      | Cambio incompatible con el estado.          |
| Devolución superior al remanente   | `domain/exception/`      | Importe que supera el principal disponible. |
| Actuación inexistente              | `application/exception/` | Consulta sin `actionId` registrado.         |
| Conflicto de idempotencia          | `application/exception/` | Misma clave, diferente contenido.           |
| Conflicto de identidad empresarial | `application/exception/` | Misma referencia, distinto contenido.       |
| Modificación concurrente           | `application/exception/` | Versión esperada incompatible.              |
| Dependencia no disponible          | `application/exception/` | Autoridad necesaria inaccesible.            |

**Reglas de separación:**

- El dominio protege sus propias invariantes y no depende de excepciones de aplicación.
- La aplicación no debe duplicar las excepciones que ya representan reglas del dominio.
- Una excepción del dominio puede propagarse hasta el límite de entrada y traducirse allí.
- La aplicación puede interpretar el error cuando necesita conservar una actuación empresarial.
- Un error técnico de un adaptador no debe convertirse arbitrariamente en una regla de negocio.
- Ninguna excepción debe utilizarse para modificar directamente el estado de `Transfer`.

### 19.17. Excepciones frente a resultados inciertos

No todos los problemas requieren lanzar una excepción.

| Situación                                            | Tratamiento                               |
|------------------------------------------------------|-------------------------------------------|
| Misma clave, distinto contenido                      | `IdempotencyConflictException`.           |
| Transferencia inexistente                            | `TransferNotFoundException`.              |
| Actor sin facultades                                 | `TransferAccessDeniedException`.          |
| Versión incompatible                                 | `TransferVersionConflictException`.       |
| Autoridad indisponible antes de la actuación durable | Error de disponibilidad.                  |
| Fondos ejecutó, pero se perdió la respuesta          | Resultado incierto y recuperación.        |
| Compensación recibió, pero no confirmó crédito       | Seguimiento de acreditación pendiente.    |
| Devolución ejecutada con respuesta perdida           | Recuperar `returnEffectId` original.      |
| Mensaje externo contradictorio                       | Conservar evidencia e investigar.         |
| Transferencia rechazada definitivamente              | Registrar rechazo empresarial comprobado. |

**Regla fundamental:** los resultados inciertos no se resuelven lanzando una excepción y descartando la actuación.

Una excepción solo debe interrumpir el flujo cuando la operación no puede continuar válidamente.
Si ya existe un compromiso durable, deben conservarse su identidad, sus evidencias y su recuperación.

### 19.18. Excepciones y límites transaccionales

El tratamiento de excepciones debe respetar las fronteras transaccionales del caso de uso.

**Antes de confirmar una intención durable:**

- Una excepción puede impedir la confirmación de los cambios locales.
- No debe realizarse una actuación económica remota antes de conservar la intención necesaria.
- Los conflictos de identidad deben impedir una segunda ejecución.

**Después de confirmar una intención durable:**

- La operación ya tiene identidad y seguimiento persistentes.
- Una falla remota no permite eliminar la intención original.
- Una respuesta perdida debe conservarse como incertidumbre.
- La recuperación debe utilizar la actuación y el efecto económico originales.

**Durante la incorporación de resultados:**

- El agregado y las evidencias confirmadas deben persistirse atómicamente.
- Una contradicción debe conservarse para investigación.
- No debe lanzarse una excepción que revierta accidentalmente el registro de la contradicción.

Si falla la persistencia local después de que un proveedor pudo ejecutar un efecto, no debe suponerse que el
efecto externo también fue revertido.

### 19.19. Relación con los adaptadores de entrada

Las excepciones de `application/` no conocen HTTP, Kafka ni los mecanismos de transporte.

Los adaptadores son responsables de traducirlas al contrato correspondiente.

| Error de aplicación                     | Interpretación para el consumidor                        |
|-----------------------------------------|----------------------------------------------------------|
| `InvalidActionContextException`         | Contexto requerido ausente o incompatible.               |
| `TransferNotFoundException`             | Transferencia no encontrada.                             |
| `TransferActionNotFoundException`       | Actuación no encontrada.                                 |
| `TransferAccessDeniedException`         | Acceso no autorizado para la actuación.                  |
| `IdempotencyConflictException`          | Petición incompatible con otra registrada.               |
| `TransferIdentityConflictException`     | Identidad empresarial en conflicto.                      |
| `TransferVersionConflictException`      | Modificación incompatible con la versión vigente.        |
| `ExternalAuthorityUnavailableException` | Dependencia requerida no disponible.                     |
| `ExternalResultConflictException`       | Resultado externo incompatible que requiere resolución.  |
| `RecoveryContextMissingException`       | Recuperación impedida por falta de información original. |

El adaptador HTTP puede utilizar códigos y Problem Details según el contrato técnico.

Un consumidor por mensajería puede requerir otro tratamiento.
La excepción no debe cambiar de significado según el transporte.

### 19.20. Observabilidad de excepciones

Cada excepción debe permitir identificar el problema sin exponer información bancaria sensible.

| Señal               | Qué registrar                                           |
|---------------------|---------------------------------------------------------|
| Log estructurado    | Código del error, caso de uso, actuación y correlación. |
| Traza               | Operación afectada y dependencia involucrada.           |
| Métrica             | Cantidad de errores por código y caso de uso.           |
| Seguimiento durable | Actuación y componente que requieren investigación.     |

**Reglas:**

- No registrar contraseñas, tokens, datos bancarios completos ni información sensible innecesaria.
- No utilizar identificadores de clientes o transferencias como etiquetas de métricas.
- No registrar varias veces la misma excepción en cada capa sin aportar información.
- Conservar la causa técnica para diagnóstico cuando corresponda.
- Diferenciar errores de aplicación de resultados empresariales rechazados o inciertos.

Un timeout no debe contabilizarse como transferencia rechazada.

### 19.21. Ejemplo: `ExecuteInternalTransfer`

El caso de uso recibe una instrucción para transferir dinero entre dos cuentas internas.

| Situación                                             | Comportamiento                                          |
|-------------------------------------------------------|---------------------------------------------------------|
| Falta el contexto de actuación obligatorio            | Interrumpir por contexto inválido.                      |
| La clave de idempotencia existe con otro contenido    | Interrumpir por conflicto de idempotencia.              |
| La referencia empresarial existe con otra instrucción | Interrumpir por conflicto de identidad.                 |
| El actor no tiene facultades                          | Interrumpir por acceso denegado.                        |
| Cuentas no admite una comprobación requerida          | No continuar sin una respuesta válida.                  |
| Fondos confirma débito y crédito                      | Registrar el resultado monetario confirmado.            |
| Fondos rechaza definitivamente la ejecución           | Registrar el rechazo comprobado.                        |
| Fondos ejecuta, pero pierde la respuesta              | Conservar incertidumbre y recuperar el efecto original. |

El caso de uso no necesita lanzar una excepción para representar una transferencia completada o rechazada.

Tampoco debe utilizar `ExternalAuthorityUnavailableException` como sustituto de la recuperación monetaria.

### 19.22. Pruebas de excepciones de aplicación

| Escenario                                           | Comportamiento esperado                   |
|-----------------------------------------------------|-------------------------------------------|
| Contexto obligatorio ausente                        | No comienza una actuación económica.      |
| Transferencia inexistente                           | Excepción de transferencia no encontrada. |
| Actuación inexistente                               | Excepción de actuación no encontrada.     |
| Acceso no autorizado                                | No expone información ni ejecuta efectos. |
| Misma clave con otro contenido                      | Conflicto sin segunda ejecución.          |
| Identidad externa duplicada con otro contenido      | Conflicto sin segundo crédito.            |
| Versión incompatible                                | No sobrescribe cambios confirmados.       |
| Autoridad indisponible antes de la admisión durable | No inventa un resultado.                  |
| Timeout después de solicitar un efecto              | Conserva identidad y seguimiento.         |
| Resultado externo contradictorio                    | Conserva evidencia para investigación.    |
| Recuperación sin referencia original                | No genera otra actuación económica.       |
| Fallo local después de un posible efecto remoto     | No asume reversión del efecto externo.    |

Las pruebas deben comprobar tanto la excepción producida como la ausencia de efectos indebidos.

**Criterio final:** las excepciones de `application/` protegen la coordinación de los casos de uso.
No deben reemplazar las reglas del dominio, ocultar incertidumbre ni destruir información necesaria para recuperar
una operación bancaria.

## 20. Modelos de los puertos de salida: `application/dto/`

### 20.1. Propósito y responsabilidad

`application/dto/` reúne los objetos tipados que necesitan los puertos de salida para intercambiar información
sin acoplar los casos de uso a PostgreSQL, HTTP, Kafka, JPA ni al formato particular de un simulador.

Estos modelos son **contratos internos de la aplicación**. Su nombre técnico permanece en inglés y su significado
empresarial se explica en español.

La carpeta no reemplaza `application/command/`, `application/query/` ni `application/result/`:

| Elemento                              | Qué representa                                      | Quién lo utiliza                  |
|---------------------------------------|-----------------------------------------------------|-----------------------------------|
| `Command`                             | Solicitud de un caso de uso.                        | Puerto de entrada y servicio.     |
| `Query`                               | Petición de una consulta de aplicación.             | Puerto de entrada y servicio.     |
| `Result` y `View`                     | Respuesta del caso de uso al consumidor.            | Servicio y puerto de entrada.     |
| `dto/<autoridad>/*Request`            | Información enviada a una autoridad.                | Servicio y puerto de salida.      |
| `dto/<autoridad>/*Result`             | Respuesta de una autoridad.                         | Puerto de salida y servicio.      |
| `dto/persistence/*`                   | Datos propios de soporte que requieren un contrato. | Servicios y repositorios locales. |
| Entidad u objeto de valor del dominio | Estado y conceptos empresariales protegidos.        | Dominio y repositorios.           |

**Regla central:** cada puerto recibe y devuelve el tipo que expresa su responsabilidad. No todos necesitan DTO.

### 20.2. Organización de carpetas

```text
application/
└── dto/
    ├── account/
    │   ├── AccountCheckRequest.java
    │   └── AccountCheckResult.java
    ├── destination/
    │   ├── DestinationResolutionRequest.java
    │   └── DestinationResolutionResult.java
    ├── representation/
    │   ├── RepresentationCheckRequest.java
    │   └── RepresentationCheckResult.java
    ├── controls/
    │   ├── BusinessControlsRequest.java
    │   └── BusinessControlsResult.java
    ├── commercial/
    │   ├── CommercialConditionsRequest.java
    │   └── CommercialConditionsResult.java
    ├── funds/
    │   ├── FundsExecutionRequest.java
    │   ├── FundsExecutionResult.java
    │   ├── FundsReservationRequest.java
    │   ├── FundsReservationResult.java
    │   ├── FundsReservationProtectionRequest.java
    │   ├── FundsReservationConsumptionRequest.java
    │   ├── FundsReservationReleaseRequest.java
    │   ├── FundsIncomingCreditRequest.java
    │   ├── FundsReturnEffectRequest.java
    │   ├── FundsMonetaryEffectResult.java
    │   ├── FundsRecoveryRequest.java
    │   ├── FundsRecoveryResult.java
    │   └── FundsReservationQuery.java
    ├── exchange/
    │   ├── ExchangeMechanismRequest.java
    │   ├── ExchangeMechanismResult.java
    │   ├── ExchangePreparationRequest.java
    │   ├── ExchangePreparationResult.java
    │   ├── ExchangeDispatchRequest.java
    │   ├── ExchangeDispatchResult.java
    │   ├── ExchangeCancellationRequest.java
    │   ├── ExchangeCancellationResult.java
    │   ├── ExchangeReturnRequest.java
    │   ├── ExchangeReturnResult.java
    │   ├── ExchangeRecoveryRequest.java
    │   └── ExchangeRecoveryResult.java
    ├── currency/
    │   ├── CurrencyConversionRequest.java
    │   └── CurrencyConversionResult.java
    └── persistence/
        ├── TransferOriginIdentity.java
        ├── TransferActionSnapshot.java
        ├── IdempotencyRecord.java
        ├── TransferHistoryEntry.java
        ├── OutboxEvent.java
        ├── InboxMessage.java
        └── RecoveryJob.java
```

La estructura es una **propuesta técnica** para expresar las operaciones ya descritas en los puertos.
No significa que todos los archivos deban crearse antes de implementar un caso de uso.

Cuando dos operaciones comparten realmente el mismo contrato, pueden reutilizar el mismo tipo.
No se debe forzar un DTO universal con campos opcionales para operaciones empresariales incompatibles.

Si un modelo apropiado ya existe en `domain/` o en `application/dto/`, se reutiliza sin duplicarlo.

### 20.3. Criterios para elegir parámetros y retornos

| Necesidad                         | Entrada                           | Retorno                                 |
|-----------------------------------|-----------------------------------|-----------------------------------------|
| Guardar un agregado               | `Transfer`                        | `Transfer` o `void`, según el contrato. |
| Buscar por identidad simple       | `UUID transferId`                 | `Optional<Transfer>`.                   |
| Buscar por identidad compuesta    | Objeto tipado de identidad.       | `Optional<Transfer>`.                   |
| Recuperar varias coincidencias    | Criterio tipado de búsqueda.      | `List<T>` o resultado tipado.           |
| Comprobar una autoridad           | `*Request` específico.            | `*Result` específico.                   |
| Consultar una autoridad por un ID | Identificador o consulta tipada.  | Resultado con hechos y evidencias.      |
| Ejecutar un efecto externo        | `*Request` con identidad estable. | Resultado tipado y recuperable.         |
| Confirmar una unidad local        | Función transaccional tipada.     | Resultado de la función.                |

**No está prohibido un `UUID` aislado**: es adecuado cuando identifica por sí solo lo que se consulta.
Lo que se evita es descomponer una solicitud empresarial completa en numerosos parámetros sueltos.

Los puertos no deben usar `Object`, mapas genéricos ni `String` sin semántica para ocultar sus contratos.
Los `Command` y `Result` de entrada no sustituyen a las entidades que se guardan en repositorios.

### 20.4. Repositorios y objetos propios de persistencia

`TransferRepository` trabaja con el agregado `Transfer`, que conserva la consistencia de sus componentes.
No se crea un repositorio independiente por cada una de las entidades internas del agregado.

Ejemplo de firmas para la persistencia del agregado:

```java
Transfer save(Transfer transfer);

Optional<Transfer> findById(UUID transferId);

Optional<Transfer> findByOriginIdentity(TransferOriginIdentity identity);

Optional<Transfer> findByExternalIdentity(ExternalOperationReference identity);

Optional<Transfer> lockForUpdate(UUID transferId);
```

`TransferOriginIdentity` agrupa el servicio de origen, el tipo de origen y la referencia de instrucción.
`ExternalOperationReference` ya existe en el dominio y se reutiliza como identidad externa completa.

La unicidad empresarial y el control concurrente deben estar respaldados por PostgreSQL.
Un método `find...` no sustituye una restricción única ni un bloqueo requerido por el negocio.

Otros repositorios guardan **registros propios de coordinación**, que no son necesariamente entidades de `domain/`:

| Puerto                      | Objeto tipado propuesto  | Responsabilidad                        |
|-----------------------------|--------------------------|----------------------------------------|
| `TransferActionRepository`  | `TransferActionSnapshot` | Actuación y resultado recuperable.     |
| `IdempotencyRepository`     | `IdempotencyRecord`      | Clave, huella y actuación original.    |
| `TransferHistoryRepository` | `TransferHistoryEntry`   | Historia y evidencia de cada cambio.   |
| `TransferOutboxRepository`  | `OutboxEvent`            | Evento durable antes de publicar.      |
| `TransferInboxRepository`   | `InboxMessage`           | Mensaje recibido y deduplicación.      |
| `RecoveryJobRepository`     | `RecoveryJob`            | Trabajo recuperable y próximo control. |

Estos tipos son modelos de aplicación, **no entidades JPA** ni copias de las tablas físicas.
Sus campos se ajustan a las identidades, evidencias, versiones y restricciones realmente necesarias.

Para guardar un registro de soporte, el método recibe ese registro como objeto; para buscarlo puede recibir su ID.
Cuando el registro pueda no existir, la búsqueda devuelve `Optional<T>`.

`LocalUnitOfWork` delimita una transacción local y no necesita un DTO de negocio propio.
Su contrato debe permitir ejecutar una operación tipada y confirmar o revertir sus cambios de forma atómica.

### 20.5. Puertos de salida hacia autoridades: tipos por capacidad

Cada autoridad conserva su propia decisión empresarial. El puerto define la solicitud y la respuesta que
Transferencias necesita; el adaptador transforma esos objetos al protocolo concreto del simulador.

| Puerto                        | Método orientativo      | Entrada                        | Retorno                       |
|-------------------------------|-------------------------|--------------------------------|-------------------------------|
| `AccountAuthorityPort`        | `checkAccount(...)`     | `AccountCheckRequest`          | `AccountCheckResult`          |
| `DestinationResolutionPort`   | `resolveDestination`    | `DestinationResolutionRequest` | `DestinationResolutionResult` |
| `RepresentationAuthorityPort` | `checkRepresentation`   | `RepresentationCheckRequest`   | `RepresentationCheckResult`   |
| `BusinessControlsPort`        | `checkControls(...)`    | `BusinessControlsRequest`      | `BusinessControlsResult`      |
| `CommercialConditionsPort`    | `determineConditions`   | `CommercialConditionsRequest`  | `CommercialConditionsResult`  |
| `CurrencyExchangePort`        | `verifyConversion(...)` | `CurrencyConversionRequest`    | `CurrencyConversionResult`    |

Son nombres Java propuestos para capacidades documentadas. No implican que el proveedor tenga esos endpoints.

### 20.6. `FundsAuthorityPort` — Solicitudes y resultados de Fondos

Los DTO de `dto/funds/` representan solicitudes a la autoridad monetaria, no operaciones ejecutadas localmente.

| Método del puerto               | Entrada                              | Retorno                     |
|---------------------------------|--------------------------------------|-----------------------------|
| `executeInternalMovements(...)` | `FundsExecutionRequest`              | `FundsExecutionResult`      |
| `createReservation(...)`        | `FundsReservationRequest`            | `FundsReservationResult`    |
| `protectReservation(...)`       | `FundsReservationProtectionRequest`  | `FundsReservationResult`    |
| `consumeReservation(...)`       | `FundsReservationConsumptionRequest` | `FundsMonetaryEffectResult` |
| `releaseReservation(...)`       | `FundsReservationReleaseRequest`     | `FundsReservationResult`    |
| `creditIncoming(...)`           | `FundsIncomingCreditRequest`         | `FundsMonetaryEffectResult` |
| `executeReturnEffect(...)`      | `FundsReturnEffectRequest`           | `FundsMonetaryEffectResult` |
| `recoverMonetaryAction(...)`    | `FundsRecoveryRequest`               | `FundsRecoveryResult`       |
| `getReservation(...)`           | `FundsReservationQuery`              | `FundsReservationResult`    |

**`FundsExecutionRequest` — Solicitud de movimientos internos relacionados.**

| Dato                     | Significado                                            |
|--------------------------|--------------------------------------------------------|
| `remoteActionId`         | Identidad estable de la actuación solicitada a Fondos. |
| `effectId`               | Identidad estable del conjunto económico.              |
| `sourceAccountId`        | Cuenta de origen del débito.                           |
| `destinationAccountId`   | Cuenta interna de destino del crédito.                 |
| `principalAmount`        | Importe exacto y positivo del principal.               |
| `currency`               | Moneda del movimiento, sin conversión implícita.       |
| `concepts`               | Conceptos monetarios separados cuando correspondan.    |
| `authorizationReference` | Fundamento verificable de la actuación.                |
| `reservationReference`   | Reserva previa, cuando corresponda.                    |

**`FundsExecutionResult` — Resultado del conjunto solicitado.**

| Dato                          | Significado                                                 |
|-------------------------------|-------------------------------------------------------------|
| `remoteActionId` y `effectId` | Vinculan el resultado con la solicitud original.            |
| `outcome`                     | Confirmación, rechazo definitivo o resultado no comprobado. |
| `debitReference`              | Efecto de débito confirmado, si existe.                     |
| `creditReference`             | Efecto de crédito confirmado, si existe.                    |
| `chargeEffectReferences`      | Cargos ejecutados, cuando corresponda.                      |
| `evidenceReferences`          | Evidencias y procedencia de las confirmaciones.             |
| `confirmedAt`                 | Momento del hecho confirmado, si corresponde.               |

No basta con que el método retorne normalmente: el servicio debe interpretar los hechos informados.
Un acuse o un timeout no justifican declarar acreditación ni repetir un conjunto monetario.

**Reservas:** `FundsReservationRequest` identifica cuenta, importe, moneda, conceptos y actuación original.
El resultado informa reserva identificada, alcance, firmeza, evidencias y componentes pendientes.
Los DTO para proteger, consumir o liberar incluyen la reserva original, la actuación y su fundamento comprobable.

**Créditos y devoluciones:** `FundsIncomingCreditRequest` y `FundsReturnEffectRequest` conservan la identidad del
movimiento único, importe, moneda, destino y autorización o procedencia correspondiente.
`FundsMonetaryEffectResult` distingue efecto confirmado, rechazo comprobado e incertidumbre, con evidencias.

**Recuperación:** `FundsRecoveryRequest` identifica la actuación y el efecto originales.
`FundsRecoveryResult` devuelve el resultado comprobado o el pendiente, sin crear una nueva identidad económica.
Una consulta `getReservation(...)` sin datos concluyentes no demuestra que un efecto no pueda ocurrir.

### 20.7. `ExchangeAuthorityPort` — Solicitudes y resultados de intercambio

Los DTO de `dto/exchange/` conservan participante, mecanismo, versión, operación y evidencias relevantes.

| Método del puerto          | Entrada                       | Retorno                      |
|----------------------------|-------------------------------|------------------------------|
| `validateMechanism(...)`   | `ExchangeMechanismRequest`    | `ExchangeMechanismResult`    |
| `prepareExchange(...)`     | `ExchangePreparationRequest`  | `ExchangePreparationResult`  |
| `dispatchExchange(...)`    | `ExchangeDispatchRequest`     | `ExchangeDispatchResult`     |
| `requestCancellation(...)` | `ExchangeCancellationRequest` | `ExchangeCancellationResult` |
| `requestReturn(...)`       | `ExchangeReturnRequest`       | `ExchangeReturnResult`       |
| `recoverExchange(...)`     | `ExchangeRecoveryRequest`     | `ExchangeRecoveryResult`     |

**Comprobación de mecanismo:** consulta participante, mecanismo, versión y operación permitida.
Su resultado identifica habilitación, condiciones verificadas y autoridad responsable.

**Preparación:** identifica transferencia, destino aceptado, principal, moneda, cobertura y actuación original.
Su resultado identifica el intercambio preparado; no demuestra que haya sido enviado.

**Despacho:** refiere al intercambio preparado y a la decisión de despacho válida frente a una cancelación.
Su resultado distingue preparación, envío efectivo, aceptación y acreditación mediante hechos independientes.

**Cancelación:** refiere al intercambio original y conserva el resultado de la solicitud de cancelación.
Una petición aceptada técnicamente no significa que el envío haya sido detenido.

**Devolución:** identifica la devolución, el importe y el fundamento autorizado del retorno externo.
Su resultado no permite debitar arbitrariamente al beneficiario.

**Recuperación:** consulta la actuación externa original con su referencia estable.
La ausencia de respuesta no autoriza a liberar cobertura ni a declarar rechazo.

### 20.8. Otras autoridades y contenido mínimo de los DTO

| Familia           | Entrada necesaria                        | Resultado necesario                      |
|-------------------|------------------------------------------|------------------------------------------|
| `account/`        | Cuenta, operación, moneda y contexto.    | Capacidades, restricciones y evidencia.  |
| `destination/`    | Identificador bancario, tipo y contexto. | Destino resuelto, referencia y momento.  |
| `representation/` | Actor, representado, cuenta y facultad.  | Decisión, alcance y evidencia.           |
| `controls/`       | Contenido, actores, importe y finalidad. | Admisión, impedimentos y evidencias.     |
| `commercial/`     | Operación y condiciones comerciales.     | Conceptos, determinación y referencias.  |
| `currency/`       | Conversión identificada y condiciones.   | Montos, monedas y referencia autorizada. |

`DestinationResolutionResult` es una resolución verificable que se presenta para aceptación.
Una vez aceptado el destino, una nueva resolución de alias no modifica la instrucción original.

`CurrencyConversionResult` informa una operación de Cambio de moneda identificada.
Transferencias no calcula una conversión implícita ni decide un tipo de cambio por sí mismo.

Los resultados de comprobaciones deben conservar la procedencia, vigencia y evidencias necesarias para decidir.
Las reglas de cada autoridad se mantienen en su simulador o integración, no se copian en `application/`.

### 20.9. Tipado, validaciones y significado de los resultados

- Preferir `record` inmutables para solicitudes y respuestas sin comportamiento empresarial propio.
- Validar estructura, obligatoriedad y consistencia elemental al construir cada modelo.
- Utilizar `BigDecimal` para importes monetarios; no utilizar `double` ni `float`.
- Reutilizar `Currency`, `ObjectReference`, `MonetaryConcept` y otros tipos existentes cuando correspondan.
- Usar tipos explícitos para resultados y hechos; evitar decidir por el texto de `message` o un HTTP 200.
- Conservar `remoteActionId`, `effectId`, identidad externa y evidencias según la operación.
- No confundir `actionId`, `operationId`, identidad de mensaje e identidad del efecto económico.
- No deducir `REJECTED` de un timeout, un `Optional.empty()` remoto o un acuse sin información suficiente.
- Distinguir aceptación, despacho, acreditación, devolución y liquidación cuando sean hechos diferentes.
- Un DTO no modifica entidades, no conserva saldos propios y no ejecuta reglas de otra autoridad.

Los códigos concretos de estado y los campos condicionales se ajustan a los contratos empresariales aplicables.
No introducir un enum o una jerarquía adicional cuando ya existe un tipo válido que representa el mismo concepto.

### 20.10. Mappers y límites de infraestructura

```text
InternalTransferService
    |
    +--> TransferRepository.save(Transfer)
    |        |
    |        v
    |    Adaptador PostgreSQL --> Mapper --> Modelo de persistencia
    |
    +--> FundsAuthorityPort.executeInternalMovements(FundsExecutionRequest)
             |
             v
         Adaptador de Fondos --> Mapper --> Contrato del simulador
                                         |
                                         v
                              FundsExecutionResult
```

El repositorio recibe el agregado `Transfer`; no recibe una lista de sus atributos ni un comando de entrada.
La reconstrucción desde PostgreSQL utiliza las capacidades válidas del dominio para restaurar el agregado.

El adaptador de autoridad transforma el DTO de aplicación a HTTP, mensaje u otro protocolo y viceversa.
El DTO de `application/` no lleva anotaciones JPA ni estructuras de cliente HTTP.

El dominio conserva su encapsulamiento: no agregar setters o getters arbitrarios para satisfacer un mapper.
Si falta una forma legítima de exponer o reconstruir un dato necesario, se debe señalar antes de modificar el dominio.

### 20.11. Criterios para declarar los contratos de salida

1. Revisar la capacidad que exige cada uno de los 16 puertos existentes de `application/port/out/`.
2. Para `TransferRepository`, utilizar `Transfer` y las identidades propias de búsqueda.
3. Para registros de soporte, definir objetos tipados sin convertirlos en entidades JPA.
4. Para cada autoridad, declarar la operación y sus tipos `Request` y `Result` adecuados.
5. Crear los modelos exclusivamente en la carpeta `application/dto/<familia>/` correspondiente.
6. Reutilizar entidades y objetos de valor existentes cuando representen exactamente el dato necesario.
7. No crear DTO redundantes si el método necesita únicamente un identificador o un objeto existente.
8. Mantener los puertos y DTO independientes de protocolos, frameworks y simuladores concretos.
9. No implementar adaptadores, mappers ni lógica de casos de uso como parte de la declaración de contratos.
10. Comprobar compilación y coherencia de firmas; eso no demuestra ejecución ni pruebas de integración.

**Criterio final:** los DTO permiten expresar contratos de salida precisos sin debilitar el dominio ni trasladar
la lógica de otras autoridades al servicio de Transferencias.

## 21. Verificación de la arquitectura y del comportamiento

### 21.1. Pruebas de servicios de aplicación

| Escenario                          | Qué debe comprobar                                             |
|------------------------------------|----------------------------------------------------------------|
| Repetición con igual contenido     | Recupera la actuación original; no vuelve a invocar el efecto. |
| Misma identidad con otro contenido | Conflicto antes de producir un segundo efecto.                 |
| Fondos confirma ambos movimientos  | La interna registra conjunto y evidencias.                     |
| Timeout de Fondos                  | Persiste incertidumbre con identidad remota original.          |
| Acuse externo sin crédito          | No consume reserva ni afirma acreditación.                     |
| Entrante externa duplicada         | Una sola instrucción y un único crédito económico.             |
| Cancelación durante despacho       | Decisiones ordenadas por la misma transferencia.               |
| Dos devoluciones simultáneas       | La suma comprometida nunca supera el remanente.                |
| Mensaje duplicado y tardío         | Inbox y hecho registrado una sola vez.                         |
| Consulta autorizada                | Datos visibles coherentes, sin efectos monetarios.             |

### 21.2. Qué necesita PostgreSQL real

- Restricciones únicas sobre identidad de origen, operación externa, actuación y efecto económico.
- Control de versión y bloqueos de transferencia frente a cancelación y despacho.
- Bloqueo del remanente frente a devoluciones concurrentes.
- Atomicidad de agregado, actuación, idempotencia, historia y Outbox.
- Recuperación de pasos remotos después de reiniciar la aplicación.

Una prueba con mocks puede comprobar coordinación y llamadas, pero no demuestra bloqueos ni aislamiento de base.

### 21.3. Qué prueban los simuladores contractuales

- Que Fondos puede ejecutar un efecto y perder su respuesta sin permitir duplicados.
- Que Compensación diferencia acuse, despacho, aceptación, acreditación y rechazo definitivo.
- Que un mecanismo externo puede entregar eventos repetidos, contradictorios o fuera de orden.
- Que una consulta de recuperación obtiene el resultado original usando la misma identidad.
- Que el laboratorio ejecuta el caso sin requerir levantar toda la plataforma bancaria.

### 21.4. Qué significa construir correctamente

- **Compilar:** las clases y dependencias Java son consistentes para esa compilación.
- **Probar coordinación:** las pruebas comprueban recorridos y decisiones del caso de uso.
- **Probar integración:** PostgreSQL y los simuladores conservan las garantías entre componentes.
- **Validar el negocio:** los escenarios exigidos muestran los efectos correctos y la ausencia de duplicaciones.

No confundir estos niveles ni afirmar validación integral solamente por un build exitoso.

## 22. Criterios para implementar `application/` sin perder el enfoque

1. Elegir un caso de uso por su problema empresarial, no por lo sencillo de su endpoint.
2. Identificar su comando o consulta y el puerto de entrada correspondiente.
3. Marcar qué decisiones son del dominio y qué comprobaciones pertenecen a otra autoridad.
4. Definir qué puerto de salida expresa cada capacidad externa necesaria.
5. Determinar qué intención debe quedar persistida antes de llamar al proveedor.
6. Definir la frontera de cada transacción y el control concurrente que exige.
7. Identificar la respuesta comprobada, la respuesta incierta y la recuperación de ambas.
8. Registrar historia y Outbox dentro de la transacción local que confirma el cambio.
9. Diseñar observabilidad que permita identificar actuaciones sin exponer información sensible.
10. Verificar el caso con pruebas de aplicación, persistencia y simuladores según la garantía.

El objetivo es construir casos de uso que representen decisiones bancarias, no simples secuencias de llamadas.
La arquitectura hexagonal facilita reemplazar los simuladores sin trasladar su lógica a Transferencias.

````
