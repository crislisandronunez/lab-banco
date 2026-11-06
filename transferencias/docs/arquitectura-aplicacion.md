# Transfer Service — Arquitectura de la capa de aplicación

## 1. Propósito y alcance

Este documento define la organización y el comportamiento de `application/` para el laboratorio independiente
Transferencias, mediante arquitectura hexagonal y Java/Spring Boot.

Su finalidad es explicar cómo la capa de aplicación recibe instrucciones, comprueba identidades, coordina
las autoridades, utiliza el dominio, conserva decisiones y recupera actuaciones sin repetir efectos económicos.

Transfer Service coordina transferencias internas, salientes y entrantes, cancelaciones, devoluciones y
consultas. Conserva instrucciones, autorizaciones, referencias, hechos y resultados; no administra saldos ni
sustituye a las autoridades de cuentas, fondos o compensación.

El laboratorio reproduce estas responsabilidades con dependencias simuladas. Los simuladores implementan
contratos con entradas, salidas, errores, resultados inciertos e identidades recuperables. La lógica de
aplicación depende de puertos, de modo que una futura integración pueda sustituir un simulador sin cambiar
las reglas del negocio ni trasladar a Transferencias las decisiones de otra autoridad.

La capa de aplicación coordina las operaciones descritas en este documento mediante el dominio, los puertos
de entrada y salida, la persistencia local y los mecanismos de recuperación.

Las decisiones sobre carpetas, nombres de interfaces y clases Java son técnicas; no añaden nuevas operaciones
bancarias. Cuando un contrato empresarial necesita una estructura de datos, su semántica es obligatoria, pero
no necesariamente la existencia de una clase Java con el mismo nombre literal.

El laboratorio no requiere ejecutar el resto de la plataforma bancaria. Puede utilizar proveedores simulados,
siempre que conserven los contratos, decisiones, resultados y escenarios de falla de las autoridades reales.

No se implementan saldos, titulares, compensación externa ni asientos contables dentro de Transferencias.
Tampoco se reduce el negocio a un registro CRUD ni se sustituyen fallas remotas por respuestas exitosas constantes.

## 2. Arquitectura hexagonal

### 2.1. Sentido de las dependencias

```text
            HTTP / mensajes / continuaciones persistentes
                              |
                              v
                     Adaptadores de entrada
                              |
                              v
                    application/port/in/
                              |
                              v
                    application/service/
                       /            \
                      v              v
                  domain/      application/port/out/
                                     |
                                     v
                            Adaptadores de salida
                        /             |             \
                       v              v              v
                  PostgreSQL     Autoridades      Mensajería
                                 simuladas       y seguimiento
```

Los servicios de aplicación dependen de los contratos de los puertos y del modelo de dominio, nunca de
controladores, entidades JPA, clientes HTTP, bibliotecas de mensajería ni clases concretas de simuladores.

| Elemento | Responsabilidad |
| --- | --- |
| `domain/` | Invariantes, decisiones locales, entidades, objetos de valor y hechos propios. |
| `application/port/in/` | Capacidades invocables y contratos de entrada de los casos de uso. |
| `application/service/` | Coordinación, autorización aplicable, transacciones y pasos remotos. |
| `application/port/out/` | Dependencias de persistencia, autoridades y confirmaciones locales. |
| `application/command/` | Datos y contexto empresarial de comandos persistentes. |
| `application/query/` | Criterios tipados de consultas sin efectos económicos. |
| `application/result/` | Resultados y vistas autorizadas de los casos de uso. |
| `application/dto/` | Entradas y salidas tipadas de los puertos de salida. |
| `application/exception/` | Fallas de coordinación, acceso, identidad y recuperación. |
| Adaptadores de entrada | Autenticación técnica, traducción y procedencia verificable. |
| Adaptadores de salida | Protocolo, persistencia, transacción, publicación o simulación. |

Las decisiones de negocio no se infieren de una respuesta HTTP satisfactoria, del retorno normal de un método,
de un acuse de comunicación ni de la ausencia de un registro remoto.

### 2.2. Responsabilidad de los servicios de aplicación

Un servicio de aplicación debe:

1. Identificar la actuación, el solicitante confiable, la instrucción de origen y el contenido recibido.
2. Resolver idempotencia y conflictos antes de ordenar cualquier nuevo efecto.
3. Verificar el acceso del solicitante al acto y al objeto que pretende modificar o consultar.
4. Consultar a las autoridades propietarias de cuentas, destinos, facultades, controles y condiciones.
5. Recuperar `Transfer`, ordenar comportamientos de dominio e incorporar evidencias verificables.
6. Registrar de forma durable la intención e identidad de cada actuación remota antes de invocarla.
7. Realizar llamadas externas fuera de las transacciones SQL propias.
8. Incorporar confirmaciones, rechazos definitivos o incertidumbres sin reinterpretar su significado.
9. Conservar cambios, historial, actuación, idempotencia y comunicación pendiente de forma coherente.
10. Reanudar trabajos y recuperar resultados originales después de fallas o interrupciones.
11. Devolver un resultado que distinga hechos comprobados de componentes todavía pendientes.

### 2.3. Límites de la coordinación

La aplicación no calcula saldos, no decide disponibilidad por su cuenta, no consume reservas mediante
actualizaciones locales y no efectúa el débito o crédito bancario de otra autoridad.

Tampoco resuelve CBU o alias con una tabla propia, decide facultades de representación de otro servicio,
suplanta controles de riesgo ni administra mensajes o conciliaciones del mecanismo externo.

La capa de aplicación puede verificar que existe una decisión de una autoridad, comprobar su procedencia y
su alcance, y decidir si el proceso de Transferencias puede continuar con esa evidencia.

## 3. Correspondencia entre aplicación y dominio

### 3.1. Componentes del agregado

`Transfer` es la raíz del agregado principal. La aplicación reconstruye y modifica el agregado por medio de
operaciones de dominio; no asigna directamente campos para saltar sus invariantes.

| Componente | Qué protege o conserva | Qué coordina aplicación |
| --- | --- | --- |
| `Transfer` | Instrucción, destino, autorización y evolución. | Admisión y decisiones con evidencia. |
| `AcceptedDestination` | Destino histórico presentado y aceptado. | Resolución externa y aceptación verificable. |
| `AuthorizationBasis` | Fundamento, alcance y huella de autorización. | Consulta de facultades y conformidad. |
| `TransferExternalOperation` | Identidad externa y contenido consistente. | Obtener y buscar la referencia externa. |
| `TransferMonetaryExecution` | Intención e identidad de efecto de Fondos. | Petición, recuperación e incorporación. |
| `TransferOutgoingExecution` | Cobertura, despacho, aceptación y crédito. | Reserva, intercambio y consumo posterior. |
| `TransferIncomingAcceptance` | Verificación, aceptación y crédito único. | Comprobación y acreditación por Fondos. |
| `TransferCancellation` | Solicitud, resultado y evidencia. | Ordenar cancelación y despacho. |
| `TransferReturnControl` | Principal devuelto, comprometido y disponible. | Compromiso o liberación transaccional. |
| `TransferReturn` | Devolución, fundamento, destino y efecto único. | Ejecución y seguimiento. |
| `TransferRemoteStep` | Intención y resultado remoto de la transferencia. | Envío, consulta y reanudación. |
| `TransferReturnStep` | Intención y resultado remoto de una devolución. | Recuperación del retorno original. |
| `TransferFact` | Hecho confirmado, origen y evidencia inmutable. | Verificación, deduplicación y publicación. |

`ConfirmedFact`, `PendingComponent`, `CheckEvidence`, `StructuredContent`, `ReturnDestination`,
`MonetaryConcept` y `ObjectReference` representan información tipada; no son clientes de infraestructura.

### 3.2. Identidad externa: requisito y representación

El contrato define `ExternalOperationReference` con estos datos:

| Dato | Significado |
| --- | --- |
| `participantId` | Participante al que pertenece la referencia. |
| `mechanismCode` | Mecanismo que emitió o reconoce la operación. |
| `mechanismVersion` | Versión del esquema que permitió interpretar el contenido. |
| `externalOperationId` | Referencia de la operación en ese mecanismo. |

La clave empresarial es `(participantId, mechanismCode, externalOperationId)`.
`mechanismVersion` se conserva, pero no distingue otra operación económica.

`TransferExternalOperation` ya representa esos datos en el dominio. El contrato no obliga a duplicarlos
mediante una segunda clase de dominio denominada `ExternalOperationReference`.

Un comando, resultado o búsqueda puede transportar una referencia tipada equivalente; lo indispensable es
conservar los cuatro datos y utilizar la clave empresarial correcta. Si se utiliza un `record` que contiene
`mechanismVersion`, su `equals()` completo no reemplaza la comparación de identidad empresarial.

Una misma clave con contenido idéntico recupera la operación anterior. La misma clave con contenido
incompatible origina un conflicto y no autoriza otra acreditación.

### 3.3. Huellas de contenido con alcances diferentes

La capa de aplicación no compara por igualdad dos hashes construidos sobre contenidos distintos.

| Huella | Contenido que representa | Uso |
| --- | --- | --- |
| `AcceptedDestination.acceptedContentHash` | Destino y datos aceptados. | Conservar la resolución. |
| `Transfer.acceptedContentHash` | Instrucción y contenido autorizado. | Detectar cambios o repetición incompatible. |
| `AuthorizationBasis.contentHash` | Contenido exacto cubierto por el fundamento. | Verificar alcance de autorización. |
| `TransferExternalOperation.externalContentHash` | Mensaje u operación externa. | Detectar repeticiones distintas. |
| `StructuredContent.contentHash` | Contenido canónico remoto. | Recuperar el paso y esquema originales. |

Cada huella se calcula o verifica usando su contenido canónico y su versión de canonicalización o esquema
correspondiente. La autorización debe cubrir la instrucción aceptada, sin exigir que el hash del destino
sea idéntico al de toda la instrucción.

No se vuelve a resolver un alias para sustituir el destino de una operación que ya fue aceptada.
Los cambios materiales exigen nuevas comprobaciones y conformidad según el proceso documentado.

### 3.4. Invariantes que no se resuelven con orquestación superficial

- El destino aceptado queda fijado y no se reemplaza por un nuevo snapshot incompatible.
- La transferencia interna exige cuentas diferentes y ejecución conjunta confirmada por Fondos.
- La salida externa exige cobertura protegida y ausencia de barrera de cancelación antes del despacho.
- Un acuse o un despacho no equivalen a acreditación del beneficiario.
- La aceptación entrante irrevocable mantiene obligación de acreditar o resolver procedentemente.
- Una cancelación no borra un efecto acreditado ni libera cobertura sin evidencia suficiente.
- Una devolución compromete remanente antes de iniciar el efecto y conserva ese compromiso si es incierto.
- Una actuación remota conserva identidades, contenido e intención durante la recuperación.
- Un hecho confirmado se incorpora una sola vez con identidad, versión, autoridad y evidencia.
- La restauración desde persistencia debe rechazar datos que violen invariantes, no corregirlos en silencio.

La aplicación protege el orden de las decisiones y persiste cambios; el dominio decide si una transición es válida.
Ambos son necesarios: validar en memoria no garantiza atomicidad bajo dos transacciones concurrentes.

## 4. Organización de `application/`

### 4.1. Estructura de paquetes

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
│   │   ├── IncorporateExternalResultUseCase.java
│   │   └── ResumeTransferWorkUseCase.java
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
│   ├── ExternalOperationReference.java
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
│   ├── TransferCancellationView.java
│   └── TransferReturnView.java
├── dto/
│   ├── account/
│   ├── destination/
│   ├── representation/
│   ├── controls/
│   ├── commercial/
│   ├── identity/
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

La estructura mantiene el diseño hexagonal acordado. Los nombres de clases y la partición de servicios son
una organización Java para cumplir los contratos, no requisitos empresariales nuevos.

No hace falta crear una clase adicional cuando una implementación existente satisface exactamente el contrato.
Tampoco corresponde conservar un DTO demasiado genérico si pierde una identidad o una evidencia obligatoria.

Las consultas de entrada se ubican en `query/` y son objetos inmutables sin efectos económicos.

### 4.2. Separación de modelos

| Familia | Contenido | No debe incluir |
| --- | --- | --- |
| `command/` | Actuación solicitada y datos empresariales. | DTO HTTP, credenciales o JPA. |
| `query/` | Criterios informativos y referencia de acceso. | Modificación monetaria. |
| `result/` | Hechos y resultados autorizados. | Entidades JPA o inferencias de acreditación. |
| `dto/<autoridad>/` | Solicitud y respuesta de un puerto saliente. | Clientes HTTP, JSON específico o simuladores. |
| `dto/identity/` | Identidades empresariales compartidas por comandos y puertos. | Datos de autenticación no verificados. |
| `dto/persistence/` | Registros locales de soporte de aplicación. | Reglas del agregado o tablas ajenas. |
| `exception/` | Interrupciones y conflictos de coordinación. | Estados financieros inventados. |

## 5. Puertos de entrada y casos de uso

### 5.1. Operaciones públicas

| Puerto de entrada | Caso de uso | Contrato |
| --- | --- | --- |
| `ExecuteInternalTransferUseCase` | `ExecuteInternalTransfer` | CI-05-001. |
| `SendOutgoingTransferUseCase` | `SendOutgoingTransfer` | CI-05-002. |
| `ReceiveIncomingTransferUseCase` | `ReceiveIncomingTransfer` | CI-05-003. |
| `CancelTransferUseCase` | `CancelTransfer` | CI-05-004. |
| `ReturnTransferUseCase` | `ReturnTransfer` | CI-05-005. |
| `GetTransferUseCase` | `GetTransfer` | CI-05-006. |
| `GetTransferActionUseCase` | `GetTransferAction` | CI-05-006. |
| `SearchTransfersUseCase` | `SearchTransfers` | CI-05-006. |

Las cinco primeras operaciones son comandos durables. Las últimas tres son consultas y no crean una nueva
actuación monetaria al ejecutarse.

Los nombres de operación se conservan en inglés; su significado bancario está descrito en
Los puertos reciben el comando o consulta y un `RequestContext` confiable,
conforme a la decisión de 6.2. No se añaden casos públicos para métodos técnicos de persistencia.

### 5.2. Continuaciones internas

| Continuación | Disparador | Efecto permitido |
| --- | --- | --- |
| `RecoverRemoteStep` | Resultado remoto incierto o consulta programada. | Recuperar resultado original. |
| `IncorporateExternalResult` | Hecho verificado por evento o mensaje. | Incorporar evidencia deduplicada. |
| `ResumeTransferWork` | Reinicio o trabajo durable pendiente. | Continuar el paso autorizado. |
| `RecordTransferChange` | Cambio local confirmado. | Guardar evento en Outbox. |

`ResumeTransferWork` y `RecordTransferChange` pueden ser operaciones internas de servicios de aplicación;
no requieren por sí mismas un endpoint público ni una jerarquía adicional de puertos.

`RecoverRemoteStepUseCase` y `IncorporateExternalResultUseCase` son fronteras útiles si trabajos o adaptadores
de mensajería deben invocarlos sin acceder a implementaciones concretas.

### 5.3. Servicios de aplicación

| Servicio | Coordinación principal |
| --- | --- |
| `InternalTransferService` | Instrucción interna y resultado indivisible de Fondos. |
| `OutgoingTransferService` | Cobertura, intercambio y hechos de salida. |
| `IncomingTransferService` | Identidad externa, aceptación y crédito único. |
| `TransferCancellationService` | Actuaciones de cancelación frente al despacho. |
| `TransferReturnService` | Control del remanente, devolución y resultado remoto. |
| `TransferQueryService` | Consultas consistentes y autorizadas. |
| `TransferRecoveryService` | Recuperación, resultados tardíos y reanudación durable. |

Un servicio puede implementar varios puertos si sus operaciones comparten una responsabilidad cohesionada.
Un método del servicio no adquiere automáticamente una nueva responsabilidad de negocio por recibir un DTO.

## 6. Contextos, identidades y procedencia

### 6.1. `ActionContext`: identidad de la actuación

| Dato | Uso |
| --- | --- |
| `actionId` | Actuación concreta que debe poder recuperarse. |
| `operationId` | Recorrido empresarial al que pertenece. |
| `causationId` | Actuación que origina otra, si corresponde. |
| `purpose` | Finalidad de la actuación. |
| `reason` | Fundamento adicional cuando es exigible. |
| `expectedVersion` | Versión esperada de la información modificable. |
| `originalReceivedAt` | Momento de recepción original cuando sea pertinente. |
| `evidenceReferences` | Evidencias vinculadas a la actuación. |

`actionId`, `operationId`, `remoteActionId`, `effectId` y la identidad de transferencia no son intercambiables.
La identidad de mensaje o el encabezado de correlación tampoco reemplazan ninguna de esas identidades.

### 6.2. `RequestContext`: procedencia confiable

El modelo empresarial exige actor y servicio autenticados, alcance autorizado y trazabilidad. No prescribe
una clase Java obligatoria denominada `RequestContext`.

El laboratorio dispone de un modelo con ese nombre. Puede utilizarse como representación técnica del
contexto verificado, **siempre que llegue efectivamente al caso de uso por una frontera confiable**.
La existencia del archivo Java, sin un recorrido de datos hasta el servicio, no satisface esta garantía.

| Dato | Origen confiable | Condición |
| --- | --- | --- |
| `actorId` y `actorType` | Identidad autenticada o delegación validada. | No aceptar autodeclaración. |
| `callerService` | Credencial del servicio llamador. | Contrastar con autoridad de origen. |
| `originIdentity` | Procedencia empresarial verificada de la instrucción. | Requerida al admitir una instrucción nueva. |
| `scope` | Permiso efectivo para operación y objeto. | No tomarlo de un campo libre del cliente. |
| `idempotencyKey` | Identidad técnica de un comando, cuando aplica. | No sustituye identidad empresarial. |
| `correlationId` | Trazabilidad de la petición. | No autoriza ni identifica efectos. |

**Decisión técnica del laboratorio:** el adaptador de entrada autentica la procedencia y construye un
`RequestContext` verificado. Los puertos de entrada reciben explícitamente ese contexto como segundo argumento:
`execute(Command, RequestContext)` para comandos y `get(Query, RequestContext)` o
`search(Query, RequestContext)` para consultas. El servicio recibe ambos datos por el puerto de entrada.

Las interfaces existentes que solo aceptan `execute(Command)` o `get(Query)` deben ajustarse para transportar
el contexto verificado. No se añade `RequestContext` como campo controlado por el cliente en el comando.
Esta decisión resuelve el recorrido desde el adaptador hasta el caso de uso sin depender de contexto oculto.

`idempotencyKey` es obligatorio cuando el contrato del comando persistente lo exige; en consultas informativas
no se requiere. Por lo tanto, el modelo `RequestContext` debe admitir esa diferencia al validar su creación,
en lugar de exigir una clave de idempotencia ficticia para leer información.

`originIdentity` conserva `(originService, originType, originReference)`. El adaptador la obtiene de la
procedencia empresarial verificada; no se deriva de `actionId`, `operationId` ni de un campo autodeclarado.
Las consultas no requieren identidad de origen. El servicio de aplicación debe exigirla al crear una
transferencia y persistirla junto con la instrucción.

Las continuaciones internas utilizan un `RequestContext` construido a partir de la identidad verificada del
servicio o trabajo persistente y la procedencia original. No se fabrica un usuario humano para recuperarlas.
Los metadatos técnicos de reintento no reemplazan `actionId`, `remoteActionId` ni las claves económicas.

### 6.3. Identidad de origen empresarial

La instrucción conserva `(originService, originType, originReference)` con unicidad propia.
Esta identidad se obtiene o valida desde la procedencia de la instrucción antes de crear el agregado.

| Identidad | Qué controla |
| --- | --- |
| Origen empresarial | Duplicación de la instrucción bancaria. |
| `actionId` | Repetición de una actuación persistente. |
| `operationId` | Relación causal entre actuaciones. |
| `Idempotency-Key` y ámbito | Repetición técnica de una petición de entrada. |
| Identidad externa compuesta | Duplicación de la operación recibida de un participante. |
| `remoteActionId` | Recuperación de la actuación solicitada al proveedor. |
| `effectId` | Unicidad del efecto económico. |
| `returnEffectId` | Unicidad del efecto de devolución. |
| `sourceFactId` y versión | Deduplicación de un hecho informado. |

Dos instrucciones similares con identidades empresariales distintas no se consideran duplicadas por el importe.
Dos peticiones con la misma identidad y contenido incompatible no producen un segundo efecto.

### 6.4. Validación en cada frontera

- Entrada: estructura, credencial, emisor, formato y correlación.
- Aplicación: procedencia, acceso al objeto, idempotencia, coordinación y condiciones previas.
- Dominio: invariantes, autorizaciones vinculadas, estados y cambios compatibles.
- Autoridades: titularidad, saldos, restricciones, intercambio y demás decisiones propias.
- Persistencia: unicidad, concurrencia, durabilidad, integridad y atomicidad.

La validación HTTP no reemplaza una restricción única SQL ni un cambio atómico del remanente de devoluciones.

### 6.5. Tipos compartidos y precisión

| Tipo o dato | Regla aplicable |
| --- | --- |
| `UUID` | Identidad interna estable sin reemplazo durante reintentos. |
| `BigDecimal` | Principal positivo; escala máxima de dos decimales. |
| Acumulados monetarios | No negativos; cero cuando el concepto lo permite. |
| `Currency` | `ARS` y `USD` conforme al contrato del dominio. |
| `Instant` | Instantes de intercambio en UTC. |
| Zona bancaria | `America/Argentina/Buenos_Aires` para reglas locales. |
| Código | Cadena entre 1 y 80 caracteres cuando el contrato utiliza `Code`. |
| Referencia externa | Cadena entre 1 y 120 caracteres, sin convertirla forzosamente a UUID. |
| Motivo | Hasta 4000 caracteres, con obligatoriedad según la actuación. |
| Hash vinculante | SHA-256 hexadecimal de 64 caracteres. |
| Versión de negocio | Entero positivo. |
| `lockVersion` | Entero no negativo. |

Los importes finales respetan la escala y precisión del contrato. Los cálculos intermedios conservan
precisión suficiente y aplican `HALF_UP` solo donde la regla documentada lo exige.
No se redondea un principal inválido para admitir silenciosamente una instrucción.

## 7. Comandos y consultas de aplicación

Los comandos describen la petición empresarial. La autenticación verificada se transporta por la frontera
indicada en 6.2; no se considera válida porque el cuerpo HTTP incluya el nombre de un actor o un permiso.

### 7.1. `ExecuteInternalTransferCommand`

| Dato | Tipo de dominio o aplicación | Propósito |
| --- | --- | --- |
| `context` | `ActionContext` | Actuación y causa de la transferencia. |
| `orderingParty` | `ObjectReference` | Ordenante autorizado. |
| `sourceAccountId` | `UUID` | Cuenta propia que se debitará. |
| `acceptedDestination` | `AcceptedDestination` | Cuenta de destino interno aceptada. |
| `principalAmount` | `BigDecimal` | Principal positivo y exacto. |
| `currency` | `Currency` | Moneda del movimiento. |
| `purposeCode` | `String` | Finalidad bancaria aceptada. |
| `authorizationBasis` | `AuthorizationBasis` | Fundamento vinculado al contenido. |
| `concepts` | `List<MonetaryConcept>` | Principal y cargos separados. |
| `commercialReferences` | Referencias tipadas | Condiciones comerciales aplicadas. |
| `fxReference` | `ObjectReference`, condicional | Cambio expreso identificado. |

El caso de uso obtiene o valida la identidad de origen y el instante de recepción mediante la procedencia
verificada. No deduce ambos datos de un `actionId` generado por conveniencia.

Origen y destino interno son distintos. La instrucción y el destino se verifican con sus propias huellas.
La conversión de moneda no se calcula localmente ni se admite por la simple existencia de `fxReference`;
la autoridad competente debe confirmar su operación y sus importes.

### 7.2. `SendOutgoingTransferCommand`

| Dato | Tipo de dominio o aplicación | Propósito |
| --- | --- | --- |
| `context` | `ActionContext` | Actuación de salida externa. |
| `orderingParty` | `ObjectReference` | Ordenante identificado. |
| `sourceAccountId` | `UUID` | Cuenta origen del débito. |
| `acceptedDestination` | `AcceptedDestination` | Destino y beneficiario externos aceptados. |
| `mechanismReference` | `ObjectReference` | Mecanismo habilitado y versión. |
| `principalAmount` | `BigDecimal` | Importe principal. |
| `currency` | `Currency` | Moneda del traslado. |
| `purposeCode` | `String` | Finalidad aceptada. |
| `authorizationBasis` | `AuthorizationBasis` | Autorización vinculante. |
| `concepts` | `List<MonetaryConcept>` | Principal, comisiones y tributos diferenciados. |
| `commercialReferences` | Referencias tipadas | Condiciones aceptadas. |
| `fxReference` | `ObjectReference`, condicional | Conversión expresamente autorizada. |

El comando no inventa un `externalOperationId` definitivo. `TransferExternalOperation` se completa con
los datos confirmados por la autoridad del mecanismo cuando estén disponibles.

Una preparación de intercambio no equivale a despacho, y una autorización de despacho no sustituye la
comprobación de cobertura ni la decisión ordenada frente a cancelación.

### 7.3. `ReceiveIncomingTransferCommand`

| Dato | Tipo o representación | Propósito |
| --- | --- | --- |
| `context` | `ActionContext` | Actuación de recepción. |
| `externalOperation` | Identidad externa tipada completa | Participante, mecanismo, versión y referencia. |
| `orderingParty` | `ObjectReference` | Ordenante informado desde el origen. |
| `beneficiaryReference` | `ObjectReference`, condicional | Beneficiario exigido por el mecanismo. |
| `destinationAccountId` | `UUID` | Cuenta que recibiría el crédito. |
| `principalAmount` | `BigDecimal` | Principal recibido. |
| `currency` | `Currency` | Moneda del crédito. |
| `purposeCode` | `String` | Finalidad informada. |
| `messageReference` | `ObjectReference` | Mensaje recibido y procedencia. |
| `evidenceReferences` | `List<ObjectReference>` | Evidencia del origen externo. |
| `receivedContentHash` | `String` | Huella del contenido externo original. |

`externalOperation` cumple el contrato de `ExternalOperationReference` como estructura de información.
No se exige una clase de dominio nueva con ese nombre si los cuatro datos se conservan en
`TransferExternalOperation` con su clave empresarial correcta.

La aplicación compara la huella externa con el contenido externo original conservado. No la equipara
al `acceptedContentHash` de `Transfer` ni al del destino de una transferencia saliente.

El origen del mensaje no equivale a autorización del participante. La identidad del servicio que informa
la recepción debe estar verificada antes de incorporar la instrucción.

### 7.4. `CancelTransferCommand`

| Dato | Tipo | Propósito |
| --- | --- | --- |
| `context` | `ActionContext` | Actuación, causa y versión esperada. |
| `transferId` | `UUID` | Transferencia original. |
| `reason` | `String` | Motivo de cancelación. |
| `authorizationBasis` | `AuthorizationBasis` | Facultad para solicitarla. |

`cancellationId` identifica la nueva actuación de cancelación. No sustituye la identidad de transferencia,
ni la acción persistente, ni el intercambio remoto que debe cancelarse.

La aplicación no confunde recibir la solicitud con confirmar que la operación dejó de poder ejecutarse.

### 7.5. `ReturnTransferCommand`

| Dato | Tipo | Propósito |
| --- | --- | --- |
| `context` | `ActionContext` | Actuación original de devolución. |
| `transferId` | `UUID` | Transferencia cuyo principal se devuelve. |
| `returnEffectId` | `UUID` | Identidad económica única del retorno. |
| `amount` | `BigDecimal` | Principal positivo solicitado. |
| `currency` | `Currency` | Moneda del principal. |
| `reasonCode` | `String` | Motivo de devolución. |
| `returnDestination` | `ReturnDestination` | Destino de retorno tipado. |
| `authorizationBasis` | `AuthorizationBasis` | Fundamento suficiente del efecto. |
| `relatedReturns` | Referencias tipadas, condicional | Antecedentes de la transferencia. |

La aplicación asigna o recupera `returnId` sin confundirlo con `returnEffectId`.
Las devoluciones anteriores se verifican contra el control económico autoritativo de la transferencia,
no mediante un importe acumulado informado libremente por el solicitante.

### 7.6. `IncorporateExternalResultCommand`

| Dato | Tipo | Propósito |
| --- | --- | --- |
| `transferId` | `UUID` | Instrucción afectada. |
| `sourceReference` | `ObjectReference` | Autoridad y objeto de procedencia. |
| `sourceFactId` | `UUID` | Identidad del hecho en origen. |
| `sourceFactVersion` | `long` | Versión del hecho. |
| `fact` | `ConfirmedFact` | Resultado comprobado y evidencia. |
| `relatedActionId` | `UUID`, condicional | Actuación original relacionada. |

El adaptador aporta el identificador de mensaje necesario para Inbox y la identidad autenticada del emisor.
El mensaje, el hecho fuente y el efecto económico poseen identidades distintas.

### 7.7. Consultas

| Consulta | Criterio de búsqueda | Control obligatorio |
| --- | --- | --- |
| `GetTransferQuery` | `transferId`. | Acceso al objeto y consistencia de lectura. |
| `GetTransferActionQuery` | `actionId`. | Acceso a actuación y objetos relacionados. |
| `SearchTransfersQuery` | Finalidad e identidad externa completa. | Participante, mecanismo y alcance. |

`SearchTransfersQuery` transporta los cuatro datos del contrato externo. La búsqueda utiliza la clave
empresarial sin versión y conserva `mechanismVersion` para interpretar el esquema original.

Una consulta no necesita un `Idempotency-Key` de comando persistente. No crea actuaciones de Fondos,
reservas, despachos, cancelaciones, devoluciones ni nuevas intenciones remotas.

## 8. Resultados y vistas

### 8.1. Resultado de una actuación persistente

`TransferActionResult` debe permitir reconocer lo que se conoce de la actuación original y consultar su avance.
La forma Java exacta puede ser un `record` o una clase inmutable; el significado de los datos no es opcional.

| Información | Propósito |
| --- | --- |
| `actionId` | Recuperar la actuación identificada. |
| `operationId` | Relacionar el recorrido empresarial. |
| Referencia de transferencia | Identificar la instrucción afectada, si existe. |
| Referencia de cancelación o devolución | Identificar la actuación subordinada, si existe. |
| Avance de la actuación | Distinguir pendiente, resolución y conclusión. |
| Resultado empresarial | Confirmación, rechazo definitivo o incertidumbre. |
| `receivedAt` | Momento de recepción reconocido. |
| `confirmedAt` | Confirmación cuando fue comprobada. |
| Referencias económicas | Débito, crédito, reserva o retorno realmente identificados. |
| Hechos confirmados | Tipo, momento, fuente y evidencia. |
| Componentes pendientes | Autoridad, causa, referencia y seguimiento. |
| Recurso de consulta | Recuperar el resultado sin repetir el comando. |

El estado general `ProcessingState` de `Transfer` no sustituye la situación de una actuación individual.
`REQUIRES_RESOLUTION` significa que existe una incertidumbre pendiente de resolver con su referencia original;
no demuestra fracaso, cancelación ni permiso para repetir un efecto económico.
El resultado no declara `BENEFICIARY_CREDITED` a partir de `DISPATCHED`, un acuse o un HTTP 200.

Los campos del `TransferActionResult` Java existente se contrastan con la información exigida por el contrato.
Si su estructura es insuficiente, se completa sin duplicar el resultado empresarial dentro del controlador.

### 8.2. `TransferView`

Debe representar la instrucción y el destino histórico aceptado, las autorizaciones visibles según permiso,
las referencias de Fondos, los hechos externos, las cancelaciones, las devoluciones y los pendientes.

La vista se construye desde información persistida coherente. La ausencia de pendientes visibles no
prueba por sí sola que todos los efectos monetarios quedaron confirmados.

### 8.3. `TransferActionView`

Debe permitir consultar la actuación original por `actionId`, su tipo, identidad de negocio, recepción,
resultado, referencias de efectos, evidencias, pendientes y fechas comprobadas.

La consulta no ejecuta la recuperación remota. Si la actuación todavía requiere investigación, informa
esa condición sin cambiar su identidad ni repetir la instrucción.

La búsqueda devuelve cero o una transferencia, porque la identidad empresarial externa compuesta es única.
Si la persistencia encuentra más de una coincidencia, se trata como una inconsistencia y no se elige una
arbitrariamente. La respuesta contiene únicamente datos autorizados.

Una búsqueda no debe exponer información de otro participante por conocer un identificador externo aislado.

## 9. Puertos hacia autoridades de negocio

### 9.1. Capacidades y autoridad propietaria

| Puerto de salida | Capacidad solicitada | Decisión que no pertenece a Transferencias |
| --- | --- | --- |
| `AccountAuthorityPort` | Comprobar cuenta, moneda y capacidad. | Titularidad y habilitación de Cuentas. |
| `DestinationResolutionPort` | Resolver CBU o alias y documentar resolución. | Identificación bancaria. |
| `RepresentationAuthorityPort` | Comprobar facultades y alcance. | Representación. |
| `BusinessControlsPort` | Obtener controles y restricciones aplicables. | Riesgo y cumplimiento. |
| `CommercialConditionsPort` | Obtener condiciones y criterios vigentes. | Autoridad comercial y tributaria. |
| `FundsAuthorityPort` | Reservar, ejecutar, proteger y recuperar dinero. | Fondos. |
| `ExchangeAuthorityPort` | Preparar, despachar y recuperar intercambio externo. | Compensación. |
| `CurrencyExchangePort` | Verificar la conversión expresa autorizada. | Autoridad de cambio de moneda. |

Cada puerto expresa una capacidad requerida; el método Java y su DTO no son necesariamente el nombre del
endpoint del servicio proveedor. La implementación del adaptador respeta el contrato de la autoridad.

### 9.2. Información mínima de comprobaciones

Una solicitud debe identificar el acto concreto, su contenido relevante, las cuentas o participantes
involucrados, el actor que puede ejecutarlo y las versiones que condicionan la decisión.

Un resultado debe permitir identificar:

- Qué autoridad lo emitió y a qué actuación o objeto corresponde.
- Qué decisión comunicó y si es definitiva, condicional o pendiente.
- Qué versión, vigencia o momento respalda la comprobación.
- Qué evidencias o referencias permiten verificarla.
- Qué condiciones deben mantenerse antes de ejecutar el efecto asociado.

No se interpreta un `boolean` favorable como permiso universal para actuaciones diferentes.
La autorización del ordenante tampoco se deduce de que una cuenta exista.

### 9.3. Resolución y fijación del destino

`DestinationResolutionPort` entrega la resolución de un identificador bancario y su evidencia.
La aplicación presenta y conserva el destino correspondiente a la instrucción aceptada.

El destino interno exige cuenta y ausencia de participante externo; el externo exige participante y
ausencia de cuenta interna. El identificador bancario conserva ceros a la izquierda y su tipo.

Una vez aceptado, el agregado no puede recibir otra resolución que cambie materialmente el destino.
Si un alias cambia después, el recorrido original conserva el snapshot previo.

### 9.4. Representación, controles y condiciones

La aplicación debe comprobar la capacidad del actor para el acto y el contenido concreto.
Cuando existe un mandato, obligación o autorización representativa, debe conservar su identidad,
versión, alcance y evidencia.

Los controles se interpretan según su alcance. Una prohibición de débitos no implica automáticamente
una prohibición de créditos entrantes.

Transferencias determina los cargos y tributos propios de su operación cuando correspondan, usando las
condiciones, tasas, bases y criterios recibidos de las autoridades competentes. La aplicación obtiene esos
insumos y coordina la determinación; las reglas empresariales propias se aplican en el dominio.

Las condiciones y los tributos originan conceptos separados del principal. No se cobran dos veces por
intervención de más de un coordinador. Una comisión causada por ejecución no se trata como cobrada
antes de comprobar el hecho que la origina. El simulador comercial no sustituye esa responsabilidad.

### 9.5. Conversión monetaria

Si las monedas de origen y destino difieren, la aplicación exige una operación de cambio explícita,
identificada, autorizada y respaldada por sus importes y monedas.

No calcula una cotización propia ni modifica `Transfer.currency` para simular una conversión.
Si no existe una operación cambiaria válida, no se permite convertir implícitamente.

## 10. `FundsAuthorityPort`: efectos monetarios y resultados

Fondos conserva la autoridad sobre saldos, disponibilidad, reservas y movimientos.
Transferencias identifica la actuación, solicita una capacidad y conserva la respuesta verificable.

### 10.1. Capacidades requeridas

| Capacidad del puerto | Resultado empresarial necesario |
| --- | --- |
| `executeInternalMovements(...)` | Débito y crédito internos confirmados conjuntamente. |
| `createReservation(...)` | Cobertura identificada para principal y cargos admitidos. |
| `protectReservation(...)` | Cobertura protegida frente a despacho e incertidumbre. |
| `consumeReservation(...)` | Consumo sin segundo débito de la reserva original. |
| `releaseReservation(...)` | Liberación justificada por ausencia de compromiso pendiente. |
| `creditIncoming(...)` | Crédito entrante identificado por efecto económico único. |
| `executeReturnEffect(...)` | Efecto de devolución autorizado y deduplicable. |
| `recoverMonetaryAction(...)` | Resultado de la actuación remota original. |
| `getReservation(...)` | Situación comprobada de una cobertura identificada. |

Los nombres de métodos son la organización Java del puerto. Los adaptadores locales o simulados deben
proporcionar el significado de cada operación, no responder siempre éxito para simplificar el laboratorio.

### 10.2. Solicitud de ejecución interna

La solicitud identifica la transferencia, `remoteActionId`, `effectId`, cuentas de origen y destino,
principal, moneda, conceptos, fundamento y reserva previa cuando corresponda.

Para una interna, Fondos debe decidir conjuntamente débito y crédito. La aplicación solo registra
la confirmación cuando ambos efectos quedan comprobados como un conjunto indivisible.

Un rechazo de Fondos por insuficiencia de fondos no se transforma en un débito parcial.
Si se pierde la respuesta después del efecto, la aplicación consulta la identidad remota original.

### 10.3. Reserva, protección, consumo y liberación

La reserva debe cubrir el principal y los cargos autorizados aplicables. Para salientes, antes de habilitar
un despacho se exige cobertura suficiente y protegida frente a resultados inciertos.

Proteger la reserva no equivale a consumirla. Consumirla no origina un segundo débito.
La acreditación externa comprobada habilita el tratamiento de consumo definido por Fondos.

La liberación solo procede con evidencia suficiente de ausencia de ejecución posible o de compromiso
pendiente. Ni un timeout, ni un acuse, ni una consulta remota vacía permiten liberar automáticamente.

### 10.4. Crédito entrante y devolución

Un crédito entrante utiliza `creditEffectId` estable, reconocido antes de invocar Fondos.
Una respuesta tardía se asocia con el efecto original.

Una devolución utiliza `returnEffectId` y su fundamento particular. La intención remota identifica
importe, moneda y destino autorizados; no habilita un débito arbitrario de la cuenta del beneficiario.

El control local del remanente de devoluciones no es una reserva monetaria de Fondos.
Ambos conceptos deben mantenerse separados.

### 10.5. Significado de los resultados

| Resultado | Qué permite afirmar | Qué no permite afirmar |
| --- | --- | --- |
| Confirmación con referencias | El efecto informado fue comprobado. | Que otros efectos también ocurrieron. |
| Rechazo definitivo con evidencia | La autoridad no ejecutó ese efecto. | Que otros pasos ajenos quedaron resueltos. |
| Pendiente o incertidumbre | No existe resultado suficiente para concluir. | Que el efecto falló o puede repetirse. |
| Timeout de transporte | La respuesta no llegó. | Que el proveedor no ejecutó. |
| Consulta inconcluyente | Investigar la identidad original. | Que puede crearse otro efecto. |

Las respuestas deben conservar `remoteActionId`, `effectId`, referencias de movimientos, hechos,
evidencias y momentos de confirmación según la capacidad invocada.

## 11. `ExchangeAuthorityPort`: intercambio externo

### 11.1. Capacidades requeridas

| Capacidad | Resultado que debe poder conservar Transferencias |
| --- | --- |
| `validateMechanism(...)` | Participante, mecanismo, versión y operación admitida. |
| `prepareExchange(...)` | Intercambio preparado e identidad de actuación. |
| `dispatchExchange(...)` | Evidencia de despacho, distinta de acreditación. |
| `requestCancellation(...)` | Resultado de la solicitud externa de cancelación. |
| `requestReturn(...)` | Resultado o seguimiento del retorno externo autorizado. |
| `recoverExchange(...)` | Estado comprobado y hechos del intercambio original. |

Compensación administra la mensajería, el mecanismo y la conciliación. Transferencias no altera el
resultado del mecanismo desde una entidad local.

### 11.2. Preparación y despacho

La preparación identifica transferencia, participante, versión de mecanismo, destino aceptado,
principal, moneda, cobertura, actuación y contenido original.

El despacho solo se solicita tras comprobar cobertura protegida y ausencia de barrera de cancelación.
La intención de despacho se conserva antes de enviar al proveedor; la aplicación no abre una transacción
SQL propia durante esa llamada.

Si la preparación o el envío se vuelven inciertos, la aplicación investiga el intercambio original.
No crea otro intercambio para obtener una respuesta más rápida.

### 11.3. Hechos externos independientes

| Hecho | Evidencia necesaria |
| --- | --- |
| `RECEIVED` | Recepción de la instrucción identificada. |
| `DISPATCHED` | Envío efectivamente informado por el mecanismo. |
| `IRREVOCABLY_ACCEPTED` | Compromiso firme comprobado. |
| `BENEFICIARY_CREDITED` | Acreditación del destinatario confirmada. |
| `EXTERNALLY_SETTLED` | Liquidación entre participantes informada por su autoridad. |
| `RETURNED` | Devolución posterior comprobada y vinculada. |

Estos hechos son distintos incluso cuando el mecanismo los comunica en una sola respuesta.
Un acuse técnico no sustituye ninguno de los hechos que aún no ha confirmado.

### 11.4. Cancelación externa y retorno

Si el intercambio ya existe, la aplicación consulta a su autoridad para conocer si todavía puede
realizarse un despacho o un efecto. Una respuesta aislada `NOT_RECEIVED` no garantiza que no exista
instrucción que pueda ejecutarse posteriormente.

La cancelación confirmada antes de habilitar un envío activa la barrera local correspondiente.
Si se confirmó una acreditación, corresponde gestionar devolución, no eliminar el hecho.

El retorno externo conserva su fundamento, nueva identidad económica y resultado propio.
No se confunde con reversión contable ni con cancelación ficticia de la transferencia original.

## 12. Puertos de persistencia y coordinación local

### 12.1. Repositorios requeridos

| Puerto | Información que administra |
| --- | --- |
| `TransferRepository` | Agregado completo, identidades, versiones y componentes subordinados. |
| `TransferActionRepository` | Actuaciones y resultados recuperables. |
| `IdempotencyRepository` | Ámbito, clave, contenido y actuación de una petición. |
| `TransferHistoryRepository` | Cambios y antecedentes de decisiones, sin alterar hechos. |
| `TransferOutboxRepository` | Eventos durables pendientes de publicación. |
| `TransferInboxRepository` | Mensajes recibidos, identidad y aplicación deduplicada. |
| `RecoveryJobRepository` | Trabajos de recuperación, propietario y próxima consulta. |
| `LocalUnitOfWork` | Frontera de confirmación transaccional propia. |

Un puerto de repositorio expresa necesidades de la aplicación, no operaciones JPA expuestas al negocio.
El dominio no conoce `EntityManager`, transacciones de Spring, anotaciones JPA ni SQL.

### 12.2. `TransferRepository`

Capacidades conceptuales:

- Guardar `Transfer` con sus componentes y la versión correspondiente.
- Recuperar un agregado completo por `transferId`.
- Localizar por identidad `(originService, originType, originReference)`.
- Localizar por identidad externa de participante, mecanismo y referencia.
- Adquirir la protección necesaria para modificar una instrucción concurrentemente.

La consulta de identidad externa conserva `mechanismVersion` cuando sea necesaria para leer el esquema,
pero la unicidad se aplica sin esa versión.

No se exige `ExternalOperationReference` como argumento Java del repositorio si existe una representación
tipada equivalente. No se utiliza un `ObjectReference` genérico sin comprobar que contiene la clave correcta.

`save(Transfer)` guarda el agregado; no se crean repositorios de dominio separados para cada componente
subordinado solamente porque tenga una tabla física distinta.

### 12.3. Unicidad respaldada por persistencia

Las restricciones de la base deben impedir duplicación incluso cuando dos solicitudes llegan al mismo tiempo:

| Identidad | Restricción o verificación |
| --- | --- |
| Identidad de origen | Unicidad de servicio, tipo y referencia originales. |
| Identidad externa | Unicidad de participante, mecanismo y referencia, sin versión. |
| `actionId` | Una sola actuación original. |
| Ámbito y clave técnica de idempotencia | Una sola decisión por petición identificada. |
| Identidad monetaria | Un solo efecto por clave económica de Fondos. |
| `returnEffectId` | Una sola devolución económica. |
| Paso remoto | Unicidad por propietario y componente. |
| Actuación de autoridad | Unicidad de servicio responsable y `remoteActionId`. |
| Hecho confirmado | Unicidad de transferencia, fuente, ID y versión. |
| Mensaje de Inbox | Una sola aplicación local por identidad de mensaje. |

La lectura previa `find...` es necesaria para responder al solicitante, pero no reemplaza una restricción
única. La implementación debe gestionar las carreras cuando ambos intentan insertar la misma identidad.

### 12.4. Actuaciones e idempotencia

`TransferActionRepository` conserva la actuación, `operationId`, tipo, objeto afectado, recepción,
resultado, hechos y pendientes necesarios para atender `GetTransferAction`.

`IdempotencyRepository` asocia ámbito, clave técnica, hash del comando y `actionId`.
El ámbito forma parte de la identidad de idempotencia; no alcanza con guardar una clave sin distinguir
su alcance si la búsqueda opera por `(scope, requestKey)`.

Una entrada de idempotencia no debe registrar otra actuación para resolver una petición repetida.
Si el contenido difiere, devuelve conflicto sin llamar al proveedor.

### 12.5. Historia, Inbox y Outbox

`TransferHistoryRepository` conserva las decisiones con su causa, actuación, instante y referencias.
La historia no sustituye los hechos confirmados de `TransferFact` ni su clave de deduplicación.

`TransferInboxRepository` conserva identidad de mensaje, emisor, referencia de acto y situación de
aplicación. La deduplicación del mensaje y la del hecho son controles complementarios.

`TransferOutboxRepository` registra el hecho que debe comunicarse dentro de la transacción local del cambio.
La entrega ocurre después de la confirmación; una publicación repetida no crea un nuevo movimiento.

### 12.6. Trabajos persistentes

`RecoveryJobRepository` vincula trabajo, transferencia o devolución, paso remoto, motivo y siguiente control.
El reinicio del proceso no debe perder el trabajo ni generar una identidad remota diferente.

Los trabajos se reclaman mediante control de concurrencia para evitar que dos ejecutores adopten
simultáneamente decisiones incompatibles sobre el mismo agregado.

### 12.7. `LocalUnitOfWork`

Una unidad local confirma o revierte los cambios de las tablas propias de Transferencias.
No coordina una transacción distribuida con Fondos, Compensación ni con los simuladores.

Su contrato permite ejecutar un conjunto de operaciones dentro de una transacción, retornar su resultado y
propagar errores de forma que la confirmación o reversión sea determinable.

La implementación puede usar transacciones declarativas o programáticas de Spring en la infraestructura.
No se debe comprometer una transacción parcial si falló una inserción de identidad o el registro del Outbox.

No se realiza llamada externa mientras el `LocalUnitOfWork` mantiene una transacción SQL abierta.

## 13. Fronteras transaccionales

### 13.1. Secuencia de una actuación con efecto remoto

```text
Solicitud autenticada
    |
    v
Comprobar identidad, contenido, acceso y versión
    |
    v
Transacción local A
    - Identidad empresarial y actuación
    - Idempotencia y agregado inicial
    - Intención remota, contenido e identificadores
    - Historia y trabajo recuperable
    |
    v
Commit local A
    |
    v
Llamada a la autoridad, fuera de transacción SQL local
    |
    +--> Confirmación con evidencia
    |       |
    |       v
    |   Transacción local B: aplicar dominio, resultado, historia y Outbox
    |
    +--> Rechazo definitivo comprobado
    |       |
    |       v
    |   Transacción local B: conservar rechazo y tratamiento permitido
    |
    +--> Timeout, respuesta incompleta o contradicción
            |
            v
        Transacción local B: conservar incertidumbre y seguimiento
            |
            v
        Recuperar la actuación remota original
```

La aplicación no declara éxito por el hecho de haber confirmado la transacción local A.
Si el proceso se interrumpe después del commit A, el trabajo durable debe recuperar la intención original.

### 13.2. Decisiones que deben ser atómicas localmente

| Decisión | Datos que deben conservarse juntos |
| --- | --- |
| Admitir instrucción | Identidad, contenido aceptado, acción e idempotencia. |
| Preparar efecto | Actuación remota, `effectId`, contenido y paso recuperable. |
| Incorporar confirmación | Agregado, hechos, acción, historia y Outbox. |
| Ordenar cancelación | Barrera y decisión frente a habilitación de despacho. |
| Comprometer devolución | `TransferReturn`, remanente, acción y paso económico. |
| Confirmar devolución | Resultado y traslado de comprometido a devuelto. |
| Recibir hecho | Inbox, deduplicación, agregado, historia y Outbox. |
| Reanudar trabajo | Referencia del paso, control de versión y próximo seguimiento. |

Una excepción o un conflicto de concurrencia no puede dejar confirmada solo una parte de esa decisión.
Las llamadas a proveedores quedan fuera de esas unidades locales.

### 13.3. Regla de reintento

Un error de conexión antes de recibir una respuesta no permite asumir que no ocurrió el efecto.
La aplicación reintenta la consulta o recuperación por `remoteActionId` y `effectId` originales.

No se reutiliza una clave de idempotencia para enmascarar un nuevo contenido, ni se genera otro efecto
para superar una respuesta incierta.

## 14. Ejecución de los ocho casos de uso

### 14.1. `ExecuteInternalTransfer`

**Entrada:** comando de transferencia interna y contexto de invocación verificado.

**Precondiciones:** cuentas diferentes, destino interno aceptado, actor autorizado, controles aplicables,
principal positivo, moneda válida y ausencia de una conversión implícita.

**Coordinación:**

1. Reconocer actor, servicio, instrucción de origen, `actionId`, `operationId` y claves técnicas.
2. Buscar la instrucción o actuación original y comparar el contenido canónico completo.
3. Recuperar la misma respuesta si la repetición es coherente; informar conflicto si difiere.
4. Comprobar cuentas, destino aceptado, representación, restricciones y condiciones comerciales.
5. Determinar los cargos y tributos propios aplicables con sus bases y reglas vigentes.
6. Conservar las evidencias de las autoridades y la autorización asociada al contenido vinculante.
7. Crear o recuperar `Transfer` sin modificar un destino ya fijado ni reinterpretar sus hashes.
8. Preparar `TransferMonetaryExecution` y `TransferRemoteStep` con identidades y contenido estables.
9. Confirmar en PostgreSQL instrucción, actuación, idempotencia e intención monetaria.
10. Solicitar a Fondos el conjunto indivisible de débito y crédito fuera de la transacción local.
11. Interpretar la confirmación conjunta, el rechazo definitivo o el resultado incierto.
12. Incorporar el resultado al dominio y confirmar historia, acción, pendientes y Outbox.
13. Devolver hechos confirmados y referencias; si quedó incierto, devolver seguimiento recuperable.
**No permitido:** confirmar solo el débito, alterar el destino por un alias nuevo, convertir moneda en
`InternalTransferService` o repetir el movimiento con un `effectId` diferente tras timeout.

**Componentes:** `Transfer`, `AcceptedDestination`, `AuthorizationBasis`,
`TransferMonetaryExecution`, `TransferRemoteStep` y `TransferFact`.

**Pruebas esenciales:** cuentas iguales, saldo insuficiente, confirmación conjunta, resultado perdido,
solicitudes duplicadas, contenido incompatible y alteración material del destino o de los cargos aceptados.

### 14.2. `SendOutgoingTransfer`

**Entrada:** comando de salida externa, destino y mecanismo versionado, más contexto verificado.

**Precondiciones:** autorización del ordenante, destino externo aceptado, mecanismo habilitado,
condiciones aplicables y cobertura obtenida de Fondos antes del despacho.

**Coordinación:**

1. Reconocer identidad de origen, acción y contenido; resolver duplicados o conflicto.
2. Comprobar facultades, cuenta, destino, controles, moneda, cargos y mecanismo.
3. Determinar los cargos y tributos propios conforme a las condiciones vigentes.
4. Conservar `Transfer`, contenido autorizado y la intención recuperable de reserva.
5. Solicitar a Fondos la reserva mediante actuación remota y efecto estables.
6. Registrar la reserva confirmada o su incertidumbre sin suponer fondos liberados.
7. Proteger la cobertura frente a despacho o resultado externo incierto.
8. Preparar el intercambio original y conservar su referencia cuando la autoridad la informe.
9. Bajo decisión local protegida, comprobar cobertura y `dispatchBarrier`.
10. Conservar intención de despacho e identidad antes de llamar a Compensación.
11. Solicitar despacho fuera de la transacción SQL local, sin producir otro intercambio.
12. Registrar `DISPATCHED` solo con evidencia de envío.
13. Registrar aceptación firme y acreditación como hechos independientes y comprobados.
14. Tras acreditar, coordinar consumo de la reserva original con Fondos y recuperar su resultado.
15. Conservar incertidumbres, historia, acción, hechos y evento de cambio sin liberar cobertura prematuramente.

**No permitido:** afirmar acreditación por acuse, consumir reserva sin el hecho exigido, despachar
tras cancelación confirmada o rechazar porque falló la liquidación después de acreditar.

**Componentes:** `Transfer`, `TransferOutgoingExecution`, `TransferExternalOperation`,
`TransferMonetaryExecution`, `TransferRemoteStep` y `TransferFact`.

**Pruebas esenciales:** reserva rechazada, preparación incierta, acuse sin crédito, aceptación irrevocable,
acreditación con consumo incierto, cancelación concurrente y liquidación posterior fallida.

### 14.3. `ReceiveIncomingTransfer`

**Entrada:** comando de recepción externa con identidad y mensaje verificables.

**Precondiciones:** emisor autorizado para participante y mecanismo, clave externa completa,
destino interno verificable, moneda admitida y controles pertinentes.

**Coordinación:**

1. Verificar servicio emisor y procedencia del participante.
2. Construir la identidad externa y buscar su clave empresarial sin `mechanismVersion`.
3. Recuperar la recepción previa si la clave y el contenido original son iguales.
4. Informar conflicto y no acreditar si la misma clave trae contenido incompatible.
5. Registrar procedencia, ordenante, beneficiario, destino, importe, moneda y evidencia del mensaje.
6. Consultar a Cuentas y a las autoridades que determinen controles sobre el crédito.
7. Rechazar antes de aceptación firme cuando destino, moneda o restricciones impidan continuar.
8. Conservar `Transfer`, `TransferExternalOperation` y `TransferIncomingAcceptance`.
9. Confirmar la aceptación irrevocable solamente después de las verificaciones exigibles.
10. Identificar un único `creditEffectId` y la actuación de Fondos antes de invocarla.
11. Confirmar localmente aceptación, intención monetaria, historia y seguimiento.
12. Solicitar el crédito a Fondos fuera de la transacción SQL propia.
13. Incorporar resultado confirmado o incertidumbre del crédito original, sin volver a acreditar.
14. Comunicar a Compensación los hechos verificados y pendientes a resolver.

**No permitido:** utilizar el ID del mensaje como clave económica, rechazar créditos por una restricción
solo aplicable a débitos, aceptar irrevocablemente sin comprobación o convertir un timeout en rechazo.

**Componentes:** `Transfer`, `TransferExternalOperation`, `TransferIncomingAcceptance`,
`TransferMonetaryExecution`, `TransferRemoteStep` y `TransferFact`.

**Pruebas esenciales:** mensajes duplicados, mismo contenido con versión de mecanismo diferente,
clave externa incompatible, destino inexistente, restricción de débito y respuesta perdida de Fondos.

### 14.4. `CancelTransfer`

**Entrada:** solicitud identificada de cancelación, versión y fundamento autorizado.

**Precondiciones:** transferencia existente, solicitante con facultades, motivo válido y evidencia
del avance real, no solamente un estado general.

**Coordinación:**

1. Reconocer actuación y petición repetida, sin crear otra cancelación económica por reintento.
2. Recuperar `Transfer`, intercambios, hechos, pasos e información de cobertura.
3. Comprobar permisos, motivo y versión esperada.
4. Ordenar la decisión local frente a preparación o habilitación de despacho concurrente.
5. Crear `TransferCancellation` con `cancellationId` propio y conservar el resultado inicial.
6. Si está comprobado que ningún despacho puede ocurrir, aplicar la barrera de despacho.
7. Si existe intercambio potencialmente ejecutable, conservar intención de consulta o cancelación externa.
8. Consultar la actuación original en Compensación fuera de la transacción local.
9. Registrar confirmación de ausencia de ejecución o conservar incertidumbre y obligación de seguimiento.
10. Solicitar a Fondos el tratamiento de cobertura solamente con evidencia suficiente.
11. Si existe acreditación, conservar el hecho y remitir al proceso de devolución procedente.
12. Persistir decisión, barrera, actuación, historia y evento sin borrar confirmaciones anteriores.

**No permitido:** transformar una solicitud en cancelación confirmada, liberar reserva tras timeout,
tratar `NOT_RECEIVED` aislado como prueba concluyente o borrar una acreditación confirmada.

**Componentes:** `Transfer`, `TransferCancellation`, `TransferOutgoingExecution`,
`TransferRemoteStep` y `TransferFact`.

**Pruebas esenciales:** barrera previa al envío, carrera real cancelación-despacho, cancelación posterior,
`NOT_RECEIVED`, falta de respuesta y devolución tras acreditación.

### 14.5. `ReturnTransfer`

**Entrada:** comando de devolución con `returnEffectId`, importe, moneda, destino y fundamento.

**Precondiciones:** transferencia susceptible de devolución, autorización suficiente, principal positivo,
moneda coherente y remanente protegido frente a modificaciones concurrentes.

**Coordinación:**

1. Reconocer identidad de petición y efecto original; resolver repetición o conflicto.
2. Recuperar la transferencia, sus devoluciones y el control económico del principal.
3. Comprobar fundamento, alcance del solicitante, moneda, importe y destino del retorno.
4. Proteger en PostgreSQL el agregado y `TransferReturnControl` siguiendo el orden de bloqueo.
5. Validar que `amount` no supera el `remainingAmount` disponible.
6. Crear `TransferReturn` con `returnId` y `returnEffectId` únicos.
7. Comprometer el importe disminuyendo remanente y aumentando `pendingAmount` en la misma transacción.
8. Conservar `TransferReturnStep`, contenido, actuación remota y trabajo recuperable.
9. Confirmar juntos el agregado, el compromiso, la acción, la historia y la intención de retorno.
10. Solicitar el efecto a Fondos o el mecanismo competente fuera de la transacción local.
11. Si confirma, trasladar importe de pendiente a devuelto conservando su evidencia.
12. Si rechaza definitivamente sin posibilidad de efecto, liberar el importe comprometido.
13. Si queda incierto, mantenerlo comprometido y consultar la actuación remota original.
14. Conservar la transferencia original, su principal y sus hechos sin convertir devolución en anulación.

**Invariante económica:**

```text
originalPrincipal = returnedAmount + pendingAmount + remainingAmount
returnedAmount >= 0
pendingAmount >= 0
remainingAmount >= 0
```

**No permitido:** comprometer más que el remanente, liberar importes inciertos, debitar al beneficiario
por petición del ordenante sin facultades o revertir automáticamente comisiones al devolver principal.

**Componentes:** `Transfer`, `TransferReturnControl`, `TransferReturn`, `TransferReturnStep`
y `TransferFact` cuando el resultado confirma un hecho.

**Pruebas esenciales:** parcial, total, duplicada, dos devoluciones simultáneas, resultado incierto,
rechazo definitivo, reinicio y cargo con tratamiento independiente del principal.

### 14.6. `GetTransfer`

1. Autenticar a quien consulta y comprobar permiso sobre la transferencia.
2. Recuperar `Transfer` y sus componentes en una lectura coherente.
3. Exponer destino histórico aceptado, nunca volver a resolver el alias para mostrar otro destinatario.
4. Presentar resultados de Fondos, hechos externos, cancelaciones, devoluciones y pendientes.
5. Respetar la procedencia de las evidencias y el alcance autorizado de los datos.
6. Devolver `TransferView` sin crear actuaciones económicas ni disparar reintentos de proveedores.

La vista no confunde aceptación de la instrucción, despacho, aceptación firme y acreditación.

### 14.7. `GetTransferAction`

1. Verificar identidad del consultante y permisos sobre acción y objeto vinculado.
2. Recuperar por `actionId` el registro de la actuación original.
3. Construir `TransferActionView` con resultado, referencias, hechos, fechas y pendientes.
4. Informar incertidumbre o resultado definitivo sin ejecutar de nuevo el comando.
5. Si no existe actuación, utilizar la excepción de aplicación apropiada sin crear otra.

La mera existencia de un UUID no concede acceso a los datos del banco.

### 14.8. `SearchTransfers`

1. Comprobar solicitante, propósito de búsqueda y alcance de información.
2. Exigir participante, mecanismo y referencia externa; conservar la versión del mecanismo.
3. Buscar por `(participantId, mechanismCode, externalOperationId)`.
4. Impedir que una nueva versión del mecanismo cree una identidad económica diferente.
5. Contrastar unicidad y contenido cuando haya información incompatible.
6. Aplicar autorización antes de mostrar referencias o detalles de la transferencia.
7. Devolver la transferencia encontrada o ausencia, sin reservas, envíos ni créditos.

La representación Java de la referencia externa puede ser un DTO específico del puerto de consulta;
la obligación del negocio es mantener la clave y el contenido, no crear otra entidad de dominio.

## 15. Recuperación, eventos e interrupciones

### 15.1. `RecoverRemoteStep`

La recuperación localiza `TransferRemoteStep` o `TransferReturnStep` por su propietario y su identidad.
Conserva `remoteActionId`, `effectId`, el contenido solicitado, la autoridad y la versión del esquema.

El recorrido es:

1. Recuperar el paso original y su agregado propietario.
2. Comprobar si ya existe un resultado final incorporado.
3. Consultar a la autoridad mediante los identificadores originales.
4. Separar confirmación, rechazo comprobado, incertidumbre y evidencia contradictoria.
5. Incorporar el resultado al dominio sin alterar hechos confirmados ni generar otra actuación.
6. Guardar resultado, historia, pendientes y siguiente control de forma localmente coherente.
7. Publicar hechos confirmados por Outbox después de la transacción, nunca antes.

**Regla:** `registerIntent(...)` debe conservar efectivamente el contenido y la identidad que se
recuperarán. Validar parámetros y marcar un paso como pendiente sin guardar la intención no es suficiente.

### 15.2. Programación de consultas

La consulta por incertidumbre se realiza:

1. Al detectar el resultado incierto.
2. A los cinco minutos.
3. A los treinta minutos.
4. En cada jornada operativa si persiste la investigación.

Estos plazos provienen del modelo de negocio. No son tiempos para rechazar, cancelar, liberar reservas
ni ejecutar un segundo efecto.

El trabajo debe quedar durable, con autoridad, referencia original y próximo control.

### 15.3. `IncorporateExternalResult`

1. Verificar que el emisor puede informar el tipo de hecho recibido.
2. Conservar la identidad del mensaje en Inbox y reconocer su eventual repetición.
3. Distinguir ID de mensaje, ID de hecho fuente y referencia del efecto económico.
4. Buscar por `(transferId, sourceService, sourceFactId, sourceFactVersion)`.
5. Aplicar `ConfirmedFact` y conservar `TransferFact` solo cuando corresponda incorporarlo.
6. Detectar versiones antiguas, discontinuidades y contradicciones sin borrar hechos previos.
7. Guardar Inbox, dominio, historia y Outbox en una sola decisión local.
8. Confirmar recepción técnica únicamente cuando el resultado quedó durable.

Dos mensajes distintos pueden transportar el mismo hecho. Deduplicar mensajes no reemplaza deduplicar hechos.

### 15.4. `ResumeTransferWork`

Al reiniciar la aplicación, se recuperan las intenciones y trabajos que no tienen resultado concluyente.
No se reconstruye un nuevo comando con otras identidades para repetir desde el principio.

La aplicación recupera el agregado, comprueba su versión y el trabajo, identifica qué componente falta,
consulta la actuación original si su resultado es incierto y ejecuta únicamente el siguiente paso permitido.

Si el crédito externo ya fue comprobado y el consumo de reserva es incierto, se recupera el consumo
original. No se vuelve a enviar la transferencia externa.

### 15.5. `TransferChanged` y publicación

El contrato de comunicación CI-05-038 informa cambios de Transferencias mediante un evento durable.
La aplicación registra el evento en Outbox junto con el cambio correspondiente; un publicador separado
lo entrega después del commit.

El evento conserva identidad, versión del agregado, modalidad, hechos, referencias económicas y pendientes.
Agrega referencias de cancelación, destino o devolución según el cambio informado.

Los consumidores deben manejar duplicados y versiones; no se presupone orden global absoluto en Kafka.
Un evento repetido comunica el mismo hecho, no autoriza un segundo movimiento monetario.

### 15.6. Recuperación ante contradicción

Cuando una autoridad comunica algo incompatible con un hecho ya confirmado, la aplicación conserva
la evidencia y marca el componente para investigación. No inventa un ajuste o reversión de dinero.

Si se necesita regularización monetaria, corresponde una actuación autorizada y separada conforme al
contrato de Fondos o de la autoridad pertinente.

## 16. Idempotencia empresarial y técnica

### 16.1. Identidades con finalidades diferentes

| Identidad | Repetición coherente | Repetición incompatible |
| --- | --- | --- |
| Origen empresarial | Recuperar `Transfer`. | Conflicto sin otra instrucción. |
| `actionId` | Recuperar actuación persistida. | Conflicto de actuación. |
| Ámbito y `Idempotency-Key` | Recuperar respuesta original. | Conflicto de contenido. |
| Clave de operación externa | Recuperar recepción original. | No acreditar; informar inconsistencia. |
| `remoteActionId` y `effectId` | Recuperar efecto de autoridad. | No crear segundo efecto. |
| `returnEffectId` | Recuperar devolución original. | No devolver de nuevo. |
| Hecho fuente y versión | Reconocer registro original. | Investigar contradicción. |
| Mensaje Inbox | No aplicar otra vez el mismo mensaje. | Investigar identidad o procedencia. |

No se utiliza el número de cuenta, el importe y una ventana temporal como clave de idempotencia económica.

### 16.2. Huellas y contenido aceptado

El hash técnico de idempotencia representa el comando canónico recibido en el ámbito aplicable.
Debe conservarse su versión de canonicalización cuando sea necesaria para verificar históricamente
la equivalencia de contenidos.

La huella completa de `Transfer` representa la instrucción vinculante. La huella de `AcceptedDestination`
representa su snapshot, y `externalContentHash` representa el contenido externo original.

La aplicación compara cada una contra el contenido que le corresponde. No exige coincidencia entre
huellas con alcances diferentes ni reescribe las originales después de una aceptación.

### 16.3. Carreras de idempotencia

Dos hilos pueden intentar crear a la vez la misma identidad. El servicio debe controlar concurrencia
mediante restricciones únicas, transacciones y recuperación de la decisión ganadora.

Si ambos presentan contenido idéntico, devuelven la misma instrucción y actuación correspondiente.
Si presentan contenido incompatible, uno no puede sobrescribir ni duplicar el efecto económico del otro.

El registro de idempotencia y la admisión de la actuación deben ser una decisión local coherente.

## 17. Concurrencia, versiones y orden de decisiones

### 17.1. Orden de protección local

Cuando una operación afecta varias piezas de información del mismo agregado, se adquiere protección en
este orden:

1. Transferencia.
2. Control de devoluciones.
3. Cancelación o devolución afectada.
4. Ejecución y pasos remotos afectados.

Este orden corresponde al diseño del dominio y de persistencia, y evita inversiones innecesarias
de bloqueo al coordinar distintas actuaciones.

### 17.2. Cancelación contra despacho

La decisión de cancelar antes del envío y la decisión de habilitar el despacho compiten sobre la misma
instrucción. Deben resolverse bajo bloqueo o control de versión de `Transfer`.

Si se confirmó cancelación antes de habilitar intercambio, la barrera impide cualquier despacho posterior.
Si ya existe intercambio, debe comprobarse si todavía puede ejecutarse antes de liberar cobertura.

No basta con consultar una variable `boolean` en memoria y después enviar fuera de la transacción:
otra actuación podría haber confirmado la cancelación en el intervalo.

### 17.3. Dos devoluciones simultáneas

Para principal 100, devuelto 40 y comprometido 20, el remanente es 40.
Dos solicitudes simultáneas de 30 no pueden comprometer juntas 60.

La comprobación de remanente, creación de `TransferReturn` y modificación de `TransferReturnControl`
se confirman atómicamente bajo protección real de PostgreSQL.

La invariante aritmética debe verificarse también al restaurar el control desde persistencia.
No se aceptan importes con escala inválida ni versiones inconsistentes corregidas silenciosamente.

### 17.4. Versiones y hechos tardíos

`version` protege la evolución del negocio y `lockVersion` la concurrencia de sus registros.
Una versión recibida no permite reemplazar arbitrariamente una decisión más reciente.

Los resultados externos tardíos se vinculan con la actuación original. Su incorporación comprueba
identidad de fuente, versión del hecho y compatibilidad con los hechos confirmados.

El repositorio debe detectar actualizaciones perdidas y conflictos; una excepción por versión no debe
borrar la intención de una actuación que ya pudo producir un efecto remoto.

## 18. Seguridad y autorización

### 18.1. Frontera de autenticación

La entrada HTTP, de evento o de trabajo local debe proporcionar una identidad verificada antes de invocar
un caso de uso. Un campo `actorId`, `callerService` o `scope` enviado dentro del cuerpo no acredita nada.

En el laboratorio puede utilizarse un proveedor local de identidad, siempre que reproduzca la comprobación
relevante del actor, servicio, alcance y procedencia. No es obligatorio ejecutar toda la plataforma de identidad.

### 18.2. Autorización para el acto y el objeto

| Operación | Control de acceso |
| --- | --- |
| Transferencia interna | Facultades del ordenante sobre origen y actuación. |
| Transferencia saliente | Permiso de débito y mecanismo habilitado. |
| Transferencia entrante | Servicio emisor y participante autorizados; destino válido. |
| Cancelación | Solicitante con facultad sobre la instrucción y avance real. |
| Devolución | Fundamento suficiente del nuevo efecto y destino autorizado. |
| Consultas | Permiso sobre transferencia, actuación y datos solicitados. |
| Incorporación de hechos | Autoridad fuente habilitada para informar ese hecho. |
| Recuperación | Identidad de servicio y trabajo durable vinculado al original. |

La aplicación consulta a Representación y a las autoridades que correspondan. No reemplaza una decisión
de titularidad con un permiso HTTP genérico.

### 18.3. Confidencialidad y protección de datos

Los resultados y registros deben evitar exponer credenciales, tokens, CBU completos, datos personales,
contenido integral de mensajes externos o decisiones de riesgo no autorizadas.

Los identificadores de correlación se usan para seguir una actuación, no para conceder acceso.
La búsqueda externa requiere finalidad y autorización; conocer el número de referencia no basta.

## 19. Excepciones de aplicación

### 19.1. Propósito

Las excepciones de aplicación comunican que una coordinación no puede avanzar con los datos o permisos
disponibles. No sustituyen resultados bancarios válidos, ni borran incertidumbres de operaciones económicas.

Una respuesta de Fondos con rechazo definitivo es un resultado empresarial. Una respuesta perdida después
de un posible efecto debe conservarse como incertidumbre recuperable. Ninguna se transforma arbitrariamente
en excepción para evitar registrar el resultado.

### 19.2. Organización de `application/exception/`

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

El catálogo mantiene la separación del diseño anterior. No se crean jerarquías adicionales sin
necesidad real, ni se incluyen códigos HTTP en las clases de aplicación.

### 19.3. Catálogo de situaciones

| Excepción | Situación concreta | Tratamiento principal |
| --- | --- | --- |
| `ApplicationException` | Base común de errores propios. | Código estable y causa, sin protocolo. |
| `InvalidActionContextException` | Identidad, origen o contexto inválido. | No admitir sin identidad suficiente. |
| `TransferNotFoundException` | No existe la transferencia solicitada. | No crearla en una consulta o cancelación. |
| `TransferActionNotFoundException` | No existe la actuación consultada. | No ejecutar el comando para suplirla. |
| `TransferAccessDeniedException` | Actor o servicio sin facultad. | No revelar datos ni ordenar efectos. |
| `IdempotencyConflictException` | Misma clave técnica con otro contenido. | No crear nueva actuación. |
| `TransferIdentityConflictException` | Misma identidad con contenido diferente. | No crear otro efecto ni crédito. |
| `TransferVersionConflictException` | Actualización obsoleta o concurrente. | No sobrescribir decisiones confirmadas. |
| `ExternalAuthorityUnavailableException` | Dependencia inaccesible sin decisión firme. | Conservar intención y duda. |
| `ExternalResultConflictException` | Evidencias remotas incompatibles. | Investigar sin borrar hechos. |
| `RecoveryContextMissingException` | Falta identidad de recuperación. | Conservar trabajo y escalar investigación. |

### 19.4. Datos de diagnóstico

La excepción base puede transportar `errorCode`, mensaje, causa, `actionId` y `transferId` cuando existan.
Esos datos son referencias diagnósticas, no autorizaciones ni identificadores monetarios sustitutos.

No se decide por el texto del mensaje. El adaptador traduce el error al protocolo de entrada y aplica
la política de exposición de datos, sin introducir HTTP dentro de `application/exception/`.

### 19.5. `InvalidActionContextException`

Se utiliza cuando no puede reconocerse la actuación o falta el contexto confiable que exige la operación.
No permite continuar con un actor inventado ni con un `scope` proporcionado arbitrariamente por el cliente.

Si la petición ya quedó admitida y una autoridad pudo efectuar un movimiento, no se elimina su seguimiento
por descubrir una inconsistencia posterior de contexto; debe conservarse la evidencia e investigarse.

### 19.6. `TransferNotFoundException` y `TransferActionNotFoundException`

Distinguen búsqueda de instrucción y búsqueda de actuación. No se intenta crear una nueva transferencia
o un nuevo efecto para responder una consulta que no encontró información.

El adaptador debe evitar revelar la existencia de objetos ajenos cuando el alcance de seguridad lo impida.

### 19.7. `TransferAccessDeniedException`

Se utiliza si el actor, delegado o servicio no posee la facultad exigida sobre la instrucción o sus datos.
No se obtiene autorización a partir de un UUID conocido, ni de una identidad declarada en el payload.

Una cuenta existente, una representación declarada y una clave de idempotencia válida no reemplazan
la autorización del acto concreto.

### 19.8. `IdempotencyConflictException`

Se produce al reutilizar la misma identidad técnica de solicitud con contenido incompatible dentro de
su ámbito. No se sobrescribe la actuación previa ni se ejecuta un segundo efecto.

La aplicación debe proporcionar una referencia que permita comprender el conflicto sin revelar datos
no autorizados de otra petición.

### 19.9. `TransferIdentityConflictException`

Protege tanto la identidad original de la instrucción como la identidad externa de negocio.
Una clave externa igual con distinto contenido no representa otra transferencia recibida.

El conflicto debe registrarse con su causa y evidencia sin acreditar nuevamente la cuenta destino.

### 19.10. `TransferVersionConflictException`

Una modificación basada en una versión desactualizada no debe sobrescribir un destino aceptado,
una cancelación confirmada ni un remanente comprometido por otra transacción.

El reintento debe recuperar el agregado actual y decidir si la misma actuación ya fue procesada o si
existe un conflicto, no repetir automáticamente efectos remotos.

### 19.11. `ExternalAuthorityUnavailableException`

Distingue la imposibilidad de contactar a una autoridad de su rechazo empresarial definitivo.
Antes de iniciar un efecto, una indisponibilidad puede impedir continuar la preparación.

Después de enviar una actuación con posible efecto, la ausencia de respuesta obliga a conservar
su identidad original y la incertidumbre, aunque el adaptador informe una falla técnica.

La excepción no debe hacer perder una intención persistida ni impedir su recuperación posterior.

### 19.12. `ExternalResultConflictException`

Una confirmación previa no se reemplaza por otra respuesta incompatible sin investigación.
La aplicación conserva fuente, actuación, referencia, momento y evidencia contradictoria.

Si ya hubo acreditación comprobada, la contradicción no autoriza a borrar el crédito ni a ejecutar
un ajuste económico no documentado.

### 19.13. `RecoveryContextMissingException`

Se utiliza cuando existe una operación que debe recuperarse, pero faltan `remoteActionId`, `effectId`,
autoridad, contenido o una referencia indispensable para consultar la actuación original.

La aplicación no fabrica identidades alternativas. Conserva los antecedentes disponibles y señala
el componente para investigación, en lugar de declarar rechazo y liberar recursos.

### 19.14. Excepciones y confirmación local

Un error anterior al commit de la intención puede impedir la admisión completa y revertir sus datos locales.
Un error posterior a una llamada remota no autoriza a revertir el registro durable anterior ni a perder
la evidencia de que un efecto pudo ocurrir.

La traducción HTTP o de mensajería corresponde a los adaptadores. La decisión económica sigue en el dominio
y las autoridades correspondientes.

## 20. Modelos de los puertos de salida: `application/dto/`

### 20.1. Responsabilidad

Los DTO de salida definen contratos internos tipados entre los servicios de aplicación y sus puertos.
No representan automáticamente nuevas entidades de dominio ni deben depender de JPA, HTTP o Kafka.

| Familia | Utilización |
| --- | --- |
| `account/` | Capacidad, restricciones, procedencia y evidencia de cuentas. |
| `destination/` | Destino, beneficiario, versión de resolución y evidencia. |
| `representation/` | Facultades, alcance sobre contenido y evidencia. |
| `controls/` | Contenido evaluado, decisión, autoridad y evidencia. |
| `commercial/` | Cargos tipados, reglas, vigencia y evidencia. |
| `funds/` | Ejecución, reserva, protección, consumo, crédito y recuperación. |
| `exchange/` | Mecanismo, preparación, despacho, cancelación y retorno externo. |
| `currency/` | Conversión identificada y condiciones comprobadas. |
| `persistence/` | Actuaciones, historia, deduplicación, Inbox, Outbox y trabajos. |

Las familias se corresponden con los puertos de salida existentes. La creación de cada clase se justifica
por la operación que deba expresar, no por completar automáticamente un catálogo de nombres.

### 20.2. Regla para elegir un modelo

- Utilizar un `UUID` aislado cuando ese identificador basta para una consulta inequívoca.
- Utilizar una estructura tipada cuando se necesitan datos relacionados y restricciones comunes.
- Reutilizar objetos de valor de dominio si expresan exactamente el mismo concepto.
- Evitar un DTO universal con campos opcionales que representen operaciones incompatibles.
- No sustituir hechos comprobados por cadenas de texto interpretadas por el servicio.
- Conservar esquema, versión, contenido e identidades originales para recuperación.
- Rechazar contenido monetario fuera de escala sin redondearlo en silencio.
- Distinguir datos obligatorios de condicionales según el contrato de cada autoridad.

Un `record` puede ser adecuado para transportar datos inmutables. Su igualdad estructural no debe
utilizarse como regla empresarial si incluye campos ajenos a la clave de negocio.

### 20.3. DTO de cuenta y destino

| Solicitud o resultado | Información imprescindible |
| --- | --- |
| `AccountValidationRequest` | Cuenta, operación, moneda y actor autorizado. |
| `AccountValidationResult` | Capacidad, restricciones, autoridad, momento y evidencia. |
| `DestinationResolutionRequest` | Identificador, tipo, país y solicitante. |
| `DestinationResolutionResult` | Referencias de cuenta, participante y beneficiario; resolución y evidencia. |

La forma exacta de estos DTO depende del contrato del proveedor; no se reemplaza el significado por
campos insuficientes que impidan comprobar una decisión o conservar el destino aceptado.

`DestinationResolutionResult` no sustituye a `AcceptedDestination`: la resolución es información obtenida
de la autoridad; el snapshot aceptado es la decisión conservada por Transferencias.

### 20.4. DTO de representación y controles

| Familia | Información de solicitud | Información de resultado |
| --- | --- | --- |
| `representation/` | Actor, representado, cuenta, acto, alcance y huella. | Facultad, vigencia, autoridad, referencia y evidencia. |
| `controls/` | Contenido, participantes, importe, moneda y finalidad. | Decisión, autoridad, restricciones y evidencia. |

No se acepta una representación únicamente porque un identificador de persona coincida con otro dato.
Las restricciones deben interpretarse para el acto concreto: crédito y débito pueden tener reglas distintas.

### 20.5. DTO comerciales y monetarios de cambio

| Familia | Información de solicitud | Información de resultado |
| --- | --- | --- |
| `commercial/` | Producto, operación, importe, moneda y finalidad. | Conceptos, reglas, vigencia, autoridad y evidencia. |
| `currency/` | Cambio autorizado, importe y monedas distintas. | Importes, tasa, versión, referencia y evidencia. |

La determinación de cargos propios pertenece a Transferencias: las reglas y bases obtenidas se aplican
sin trasladar ese cálculo empresarial al simulador comercial. Cada cargo conserva causa, base,
condición, versión y hecho generador. El principal sigue separado de comisiones, tributos y ajustes.

La conversión requiere autoridad y operación expresa. Transferencias no calcula el tipo de cambio.

### 20.6. DTO de Fondos

Las operaciones de `FundsAuthorityPort` requieren contratos que distingan su semántica.
Pueden representarse por tipos específicos o por tipos compartidos que validen la modalidad exacta.
No deben convertirse en una única petición ambigua con campos opcionales no relacionados.

| Operación | Petición requerida | Resultado requerido |
| --- | --- | --- |
| Ejecución interna | Cuentas, principal, moneda y efecto. | Grupo indivisible, débito, crédito y evidencia. |
| Crear reserva | Cuenta, cobertura, conceptos e identidad remota. | Reserva identificada, alcance y evidencia. |
| Proteger reserva | Reserva y actuación que requieren protección. | Cobertura protegida o incertidumbre. |
| Consumir reserva | Reserva original y hecho habilitante. | Efecto y evidencia, sin segundo débito. |
| Liberar reserva | Reserva y fundamento de ausencia de compromiso. | Liberación confirmada o pendiente. |
| Crédito entrante | Cuenta, `creditEffectId`, importe, moneda y conceptos tipados. | Crédito único, movimiento y evidencia. |
| Efecto de devolución | `returnEffectId`, importe, moneda y conceptos tipados. | Resultado del retorno y referencias. |
| Recuperación monetaria | `remoteActionId` y `effectId` originales. | Resultado original o componente pendiente. |
| Consulta de reserva | Identidad de reserva y contexto de consulta. | Cobertura y situación comprobada. |

`FundsExecutionRequest` y `FundsExecutionResult` pueden utilizarse donde su estructura preserve todos
los datos exigidos por la operación. No se obliga a multiplicar clases si los contratos ya son equivalentes.

Para ejecución interna, el resultado debe distinguir el grupo indivisible y las referencias del débito
y del crédito. Para una devolución, el identificador económico debe corresponder al retorno y su fundamento.

Un resultado que solo contenga `success` o un mensaje de texto no permite resolver las garantías exigidas.
La aplicación necesita resultados que distingan confirmación, rechazo definitivo e incertidumbre.

### 20.7. DTO de Compensación e intercambio

| Operación | Datos de entrada | Datos de salida |
| --- | --- | --- |
| Validar mecanismo | Participante, mecanismo, versión numérica y modalidad. | Habilitación, versión y evidencia tipada. |
| Preparar intercambio | Transferencia, destino aceptado, cobertura y actuación. | Intercambio preparado e identidad original. |
| Despachar | Intercambio, intención y cobertura protegida. | Envío comprobado y referencias de evidencia. |
| Cancelar | Intercambio original y fundamento autorizado. | Resultado, momento y referencias tipadas. |
| Solicitar retorno | Devolución, efecto, destino y fundamento. | Resultado, seguimiento y evidencia. |
| Recuperar intercambio | Referencia y actuación remota originales. | Hechos comprobados y evidencia pendiente. |

Preparación, despacho, cancelación, retorno y recuperación utilizan contratos separados para preservar
los datos y resultados propios de cada paso.

La respuesta puede transportar los cuatro datos del contrato `ExternalOperationReference` cuando el
proveedor los informa. Esto no obliga a crear otra clase de dominio con ese nombre.

El resultado debe distinguir el acuse, el despacho, la aceptación irrevocable, la acreditación y la
liquidación; un texto descriptivo no sustituye tipos y evidencias verificables.

### 20.8. Objetos de soporte de persistencia

Los contratos Java implementados para estas familias incluyen:

| Familia | Tipos de solicitud | Tipos de resultado |
| --- | --- | --- |
| Comercial | `CommercialConditionRequest` | `CommercialConditionResult` |
| Control | `BusinessControlRequest` | `BusinessControlResult` |
| ConversiÃ³n | `CurrencyExchangeRequest` | `CurrencyExchangeResult` |
| RepresentaciÃ³n | `RepresentationCheckRequest` | `RepresentationCheckResult` |
| Fondos | `FundsIncomingCreditRequest`, `FundsRecoveryRequest` | `FundsMonetaryEffectResult`, `FundsRecoveryResult` |
| Fondos | `FundsReservationRequest`, `FundsReservationProtectionRequest` | `FundsReservationResult` |
| Fondos | `FundsReservationConsumptionRequest`, `FundsReservationReleaseRequest` | `FundsMonetaryEffectResult` |
| Fondos | `FundsReservationQuery`, `FundsReturnEffectRequest` | `FundsMonetaryEffectResult` |
| Intercambio | `ExchangeMechanismRequest` | `ExchangeMechanismResult` |
| Intercambio | `ExchangePreparationRequest` | `ExchangePreparationResult` |
| Intercambio | `ExchangeDispatchRequest` | `ExchangeDispatchResult` |
| Intercambio | `ExchangeCancellationRequest` | `ExchangeCancellationResult` |
| Intercambio | `ExchangeReturnRequest`, `ExchangeRecoveryRequest` | `ExchangeReturnResult`, `ExchangeRecoveryResult` |

`TransferExternalIdentity` y `TransferOriginIdentity` conservan identidades empresariales tipadas.
`FundsIncomingCreditRequest` y `ExchangePreparationRequest` transportan
`ExternalOperationReference` completo para conservar participante, mecanismo, versiÃ³n e identificador.

| Modelo de aplicación | Datos conceptuales que debe permitir conservar |
| --- | --- |
| `TransferActionSnapshot` | Actuación, tipo, resultado, evidencias, objetos y pendientes. |
| `IdempotencyRecord` | Ámbito, clave técnica, actuación y huella canónica. |
| `TransferHistoryEntry` | Actuación, cambio, momento, procedencia y referencias. |
| `OutboxEventRecord` | Evento, agregado, versión, contenido y publicación pendiente. |
| `InboxEventRecord` | Mensaje, emisor, hecho vinculado y aplicación deduplicada. |
| `RecoveryJobRecord` | Trabajo, propietario, paso, causa y siguiente consulta. |

Los nombres corresponden a responsabilidades, no a obligación de crear simultáneamente todos los tipos.
La persistencia puede separar un registro de acción resumido de una vista detallada de su resultado,
siempre que no pierda información necesaria para la consulta y recuperación.

El repositorio persiste y restaura el agregado completo para no omitir invariantes mediante una proyección parcial.

Si el puerto busca idempotencia por ámbito y clave, la representación persistida debe conservar ambos.
Si un `RecoveryJobRecord` identifica solamente transferencia, debe ampliarse el vínculo cuando el trabajo
pertenezca específicamente a una devolución o a su paso remoto.

### 20.9. Mappers y ensamblado

```text
Servicio de aplicación
    |
    +--> TransferRepository.save(Transfer)
    |        |
    |        v
    |    Adaptador PostgreSQL --> Mapper --> Entidades JPA
    |
    +--> FundsAuthorityPort.executeInternalMovements(...)
             |
             v
         Adaptador de Fondos --> Mapper --> Contrato del proveedor
                                        |
                                        v
                                   Resultado tipado
```

El mapper de persistencia reconstruye el agregado respetando invariantes y versiones.
No debe utilizar setters generales o reflexión que deje al dominio en una situación inválida.

El mapper de una autoridad traduce protocolo a DTO sin deducir éxito económico de HTTP 200.
El DTO de aplicación no contiene anotaciones JPA ni referencias a clientes del simulador.

### 20.10. Condiciones para un contrato completo

Antes de considerar suficiente un puerto o DTO, debe comprobarse:

1. Que representa una necesidad documentada del caso de uso y su autoridad.
2. Que transporta las identidades empresariales, remotas y económicas que correspondan.
3. Que conserva el contenido, esquema, versión y huella necesarios para la recuperación.
4. Que distingue resultados confirmados, rechazados e inciertos sin depender del texto de respuesta.
5. Que incluye hechos, referencias y evidencias cuando la aplicación debe incorporarlos.
6. Que no exige otra clase Java para un concepto ya correctamente representado.
7. Que no introduce reglas monetarias o de otros servicios en Transferencias.
8. Que el retorno del puerto permite decidir la transición válida del dominio.
9. Que la persistencia puede guardar y restaurar su decisión de forma durable.
10. Que el adaptador simulado puede reproducir duplicados, fallos y respuestas tardías.

La compilación de interfaces y DTO demuestra compatibilidad de tipos, no cumplimiento funcional del contrato.

## 21. Adaptadores y entorno independiente del laboratorio

### 21.1. Adaptadores de entrada

Los adaptadores reciben peticiones HTTP, mensajes de autoridad o trabajos locales.
Su responsabilidad es validar transporte, autenticar procedencia, traducir al comando o consulta
tipados y entregar el contexto confiable al puerto de entrada correspondiente.

Un adaptador no modifica directamente `Transfer`, no decide remanentes ni hace movimientos de Fondos.
La seguridad técnica se comprueba antes de la coordinación del caso de uso.

### 21.2. Adaptador de persistencia

PostgreSQL conserva las identidades, versiones, intenciones, historia, Inbox, Outbox y trabajos necesarios.
Los mappers traducen entre agregado y tablas propias, sin convertir entidades JPA en reglas empresariales.

Las restricciones de unicidad y las transacciones deben ejercitarse en un motor real para las pruebas
concurrentes. Un repositorio en memoria no demuestra la misma protección de las invariantes.

### 21.3. Simuladores contractuales

| Dependencia | Comportamiento mínimo del simulador |
| --- | --- |
| Cuentas | Cuenta existente o ausente; capacidad, moneda y restricciones concretas. |
| Identificación bancaria | Resolución estable de alias o CBU, incluida modificación posterior. |
| Representación | Facultad válida, ausente, caducada o insuficiente para el acto. |
| Riesgo y cumplimiento | Aceptación o impedimento según el contenido y tipo de operación. |
| Condiciones comerciales | Cargos correctos, causa y referencias de determinación. |
| Fondos | Reserva, protección, ejecución indivisible, créditos, retornos y recuperación. |
| Compensación | Preparación, despacho, aceptación, acreditación, cancelación y recuperación. |
| Cambio de moneda | Conversión explícita identificada o ausencia de autorización. |

Cada simulador conserva la decisión que le pertenece y permite consultar actuaciones previas.
Puede devolver confirmación, rechazo, timeout, indisponibilidad, resultado tardío o incompatibilidad.

El simulador de Fondos no debe acreditar dos veces el mismo `effectId`, incluso cuando la primera
respuesta se pierda. El de Compensación no declara acreditación a partir de un acuse.

No se ejecuta otro laboratorio como dependencia funcional obligatoria. La simulación puede estar
implementada en un proceso local o adaptador independiente si respeta su frontera contractual.

### 21.4. Mensajería, publicación y trabajos

Cuando se utilice mensajería local, deben conservarse Outbox, Inbox, deduplicación y la separación
entre confirmación de la base y entrega del mensaje.

La infraestructura local puede ser menor que la de la plataforma completa, pero no elimina esas
responsabilidades ni asume entrega exactamente una vez por disponer de un broker.

Los trabajos de recuperación deben sobrevivir a reinicios y prevenir dos procesadores incompatibles.

### 21.5. Tecnologías que no se incorporan por costumbre

Redis no es obligatorio si no existe una necesidad documentada de caché para Transferencias.
La autenticación puede utilizar infraestructura local adaptada si comprueba las identidades y los permisos.

El uso de Spring, PostgreSQL, mensajería y observabilidad debe justificarse por las capacidades requeridas,
no por reproducir todas las herramientas de la plataforma bancaria completa.

## 22. Observabilidad de la aplicación

### 22.1. Registros de decisiones

Se registran, de forma correlacionable y sin información sensible innecesaria:

- `operationId`, `actionId`, transferencia y componente afectado.
- Identidad técnica de la autoridad y tipo de actuación remota.
- Momento de recepción, preparación, consulta y confirmación, si existen.
- Diferencia entre confirmación, rechazo definitivo y resultado incierto.
- Identidad del efecto consultado en recuperación, protegida según el alcance de registro.
- Motivo de conflicto de idempotencia, versión, identidad o evidencia.
- Decisión sobre barrera de despacho o remanente de devolución, sin exponer datos de terceros.

No deben registrarse tokens, credenciales, mensajes completos de proveedores ni cuentas como etiquetas
de métricas. La correlación no reemplaza las restricciones de acceso.

### 22.2. Métricas orientadas al problema bancario

| Indicador | Qué permite investigar |
| --- | --- |
| Instrucciones recibidas por modalidad | Volumen y distribución de las operaciones. |
| Comandos repetidos y conflictos | Idempotencia y contradicciones de contenido. |
| Efectos monetarios inciertos | Tiempo y frecuencia de recuperación de Fondos. |
| Reservas protegidas sin resultado | Riesgo operativo de cobertura sin resolución. |
| Despachos sin crédito comprobado | Diferencia entre envío y acreditación. |
| Cancelaciones pendientes | Carreras y respuestas externas incompletas. |
| Principal comprometido por devoluciones | Importes no disponibles hasta resolver el retorno. |
| Trabajos recuperables atrasados | Investigaciones que no avanzan. |
| Mensajes duplicados | Eficacia de Inbox y deduplicación de hechos. |
| Eventos Outbox pendientes | Retraso en comunicar cambios confirmados. |

Un gráfico o contador no constituye evidencia bancaria. La confirmación económica requiere hechos y
referencias originales del servicio competente.

### 22.3. Trazas distribuidas

Las trazas relacionan la actuación original, las llamadas de comprobación, la preparación, los efectos
monetarios y el seguimiento. Deben distinguir el tiempo de espera por una autoridad de la duración
de una transacción local.

Un reintento o recuperación conserva su vinculación causal con `operationId` y con la actuación remota
original, aunque se ejecute después del reinicio de la aplicación.

## 23. Pruebas para demostrar las garantías

### 23.1. Pruebas unitarias de aplicación

Los servicios de aplicación pueden probarse con puertos simulados controlados para comprobar decisiones:

- Orden correcto de comprobaciones antes de admitir una instrucción.
- Ausencia de efectos ante una identidad empresarial incompatible.
- Uso del mismo `remoteActionId` y `effectId` en recuperación.
- Separación entre resultado técnico y hecho económico confirmado.
- Invocación de los métodos de dominio apropiados, sin modificar estados directamente.
- Solicitud de tratamiento de cobertura solo con evidencia suficiente.
- Consulta de transferencia y actuación sin efectos laterales.

Estas pruebas no demuestran por sí solas unicidad real bajo dos transacciones SQL concurrentes.

### 23.2. Pruebas de dominio

Deben comprobar las invariantes definidas para el dominio de Transferencias, incluidas:

- Principal positivo y escala admitida, tanto al crear como al restaurar.
- Destino aceptado inmutable frente a una segunda resolución incompatible.
- Diferencia de alcance entre hashes y vínculo correcto de autorización.
- Cobertura y barrera exigidas antes de habilitar despacho.
- Conjunción de referencias al confirmar efectos monetarios y acreditaciones.
- Conservación de `remoteActionId`, `effectId` y contenido al registrar intención.
- Cálculo exacto del remanente y rechazo de compromisos excesivos.
- Persistencia de hechos confirmados con su identidad y versión de origen.

### 23.3. Pruebas con PostgreSQL real

| Escenario | Evidencia esperada |
| --- | --- |
| Dos instrucciones con mismo origen | Una sola identidad de transferencia. |
| Dos recepciones con misma clave externa | Un solo registro económico reconocido. |
| Mismo ID externo con contenido distinto | Conflicto sin segundo crédito. |
| Dos devoluciones sobre el mismo remanente | Ningún compromiso superior al principal. |
| Cancelación concurrente al despacho | Nunca despacho posterior a cancelación confirmada. |
| Conflicto de `lockVersion` | No se pierde una actualización confirmada. |
| Fallo durante la decisión local | No quedan solo parte de acción, agregado o Outbox. |
| Repetición de hecho por otro mensaje | Un solo `TransferFact` por clave fuente y versión. |
| Reinicio tras intención persistida | Se recuperan identidades y contenido originales. |
| Dos trabajadores reclaman el mismo paso | Ninguna continuación incompatible duplicada. |

Las pruebas deben inspeccionar la persistencia real, no solo los objetos Java devueltos.

### 23.4. Pruebas de contratos y simuladores

| Autoridad | Escenarios obligatorios para el recorrido relevante |
| --- | --- |
| Cuentas | Admitida, inexistente, moneda incompatible y restricción específica. |
| Identificación bancaria | Alias que cambia después de aceptar una instrucción. |
| Representación | Actor autorizado, representado sin facultad y mandato insuficiente. |
| Fondos | Conjunto confirmado, rechazo, timeout posterior al efecto y recuperación. |
| Compensación | Acuse, despacho, aceptación firme, crédito, retraso y contradicción. |
| Compensación | Cancelación confirmada, `NOT_RECEIVED` aislado y respuesta tardía. |
| Devolución | Confirmada, rechazada definitivamente e incierta con compromiso. |
| Mensajería | Duplicado, desorden, pérdida de respuesta y reentrega después de reinicio. |

Un simulador que siempre confirma no alcanza para validar el caso de uso bancario.

### 23.5. Matriz de los ocho casos de uso

| Caso | Escenarios mínimos a demostrar |
| --- | --- |
| `ExecuteInternalTransfer` | Interna válida, fondos insuficientes, timeout y repetición. |
| `SendOutgoingTransfer` | Cobertura, acuse, aceptación, crédito, cancelación y consumo. |
| `ReceiveIncomingTransfer` | Duplicado, conflicto externo, controles y crédito único. |
| `CancelTransfer` | Antes del envío, durante carrera, después de crédito e incertidumbre. |
| `ReturnTransfer` | Parcial, simultánea, duplicada, incierta y rechazo con evidencia. |
| `GetTransfer` | Vista autorizada, destino aceptado y pendientes fieles. |
| `GetTransferAction` | Respuesta perdida, consulta original y acceso autorizado. |
| `SearchTransfers` | Clave completa, versión de mecanismo y confidencialidad. |

Las continuaciones internas se prueban con eventos repetidos, resultados tardíos, cambios de versión,
reinicio y conservación del mismo efecto económico.

### 23.6. Qué demuestra cada verificación

| Verificación | Qué demuestra | Qué no demuestra |
| --- | --- | --- |
| Compilación | Consistencia sintáctica y tipos compatibles. | Invariantes empresariales. |
| Prueba unitaria | Decisiones locales bajo entradas controladas. | Atomicidad SQL real. |
| Prueba de integración | Interacción con adaptadores y persistencia. | Todos los fallos distribuidos posibles. |
| Prueba concurrente | Protección de identidades y remanentes en carrera. | Recuperación tras reinicio por sí sola. |
| Prueba de contrato | Semántica de una autoridad o simulador. | Comportamiento no ejercitado. |
| Prueba de recuperación | Continuidad con identidades originales. | Ausencia de duplicados si no se prueba. |

No se declara un microservicio validado únicamente porque compile o arranque.
Cada garantía se demuestra mediante escenarios que la ejerciten y evidencias observables.

## 24. Criterios de implementación de `application/`

La implementación respeta este documento cuando:

1. Mantiene la dirección de dependencias hexagonal y las responsabilidades de cada capa.
2. Implementa los ocho casos de uso públicos sin inventar otras operaciones bancarias.
3. Conserva las continuaciones internas necesarias para resultados inciertos y eventos tardíos.
4. Transmite la procedencia autenticada al caso de uso por una frontera comprobable.
5. Reconoce identidad de origen, actuación, efecto, operación y mensaje sin confundirlas.
6. Utiliza el dominio para proteger destino, autorización, cobertura, cancelación y devoluciones.
7. Trata `ExternalOperationReference` como contrato de datos, sin exigir una segunda entidad redundante.
8. Calcula y valida cada hash contra su propio contenido canónico, sin igualdad artificial entre huellas.
9. Determina cargos y tributos propios conforme a las condiciones y reglas aplicables.
10. Solicita los efectos a sus autoridades mediante puertos tipados, sin apropiarse de otras reglas.
12. Conserva intenciones y referencias originales antes de llamar a proveedores.
12. Mantiene llamadas externas fuera de las transacciones SQL locales.
13. Aplica unicidad y concurrencia mediante PostgreSQL y decisiones atómicas.
14. Distingue hechos confirmados de rechazos comprobados y de incertidumbres recuperables.
15. Conserva Inbox, Outbox, trabajos, historia y resultados consultables con sus identidades.
16. Permite ejecutar el laboratorio con simuladores contractuales sin ejecutar toda la plataforma.
17. Instrumenta los problemas operativos relevantes sin usar métricas como evidencia económica.
18. Supera pruebas de dominio, aplicación, persistencia, integración, concurrencia y recuperación.

Los nombres Java y la distribución interna pueden ajustarse cuando representen exactamente el mismo
contrato sin alterar responsabilidades ni garantías. Toda diferencia entre un contrato escrito y una
clase concreta debe resolverse por su significado empresarial, no por preferencia de nombres.

## 25. Alcance del laboratorio

El laboratorio ejecuta Transferencias de forma independiente, con persistencia y observabilidad locales.
No requiere desplegar los demás microservicios ni la infraestructura transversal de producción.

Las dependencias de negocio se sustituyen por simuladores contractuales. Cada simulador conserva la
semántica de su autoridad: valida los datos recibidos, reconoce identidades, devuelve resultados
consistentes y permite recuperar actuaciones. Los fallos y resultados inciertos forman parte de la
simulación para estudiar la lógica real de coordinación.

Prometheus, Grafana, registros y trazas locales permiten observar las decisiones y los resultados del
servicio. Esta infraestructura pertenece al laboratorio y no cambia las responsabilidades de Transferencias.
