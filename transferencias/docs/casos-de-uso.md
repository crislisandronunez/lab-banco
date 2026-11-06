# Transfer Service — Casos de uso de Transferencias

## 1. Propósito

Este documento explica qué operaciones empresariales debe resolver `transfer-service`, por qué existen,
qué información necesitan, qué decisiones deben tomar y qué garantías debe cumplir el software.

Está organizado para estudiar cada caso de uso por separado y después comprender su implementación en código.

El objetivo no es implementar una transferencia como un simple cambio de estado. Una instrucción bancaria puede
involucrar autorización, reservas, movimientos, intercambio externo, compromisos firmes, cancelaciones,
devoluciones y resultados que necesitan recuperación.

**Principio central:** Transferencias es responsable de la instrucción y de coordinar su ejecución.
No es dueño de los saldos ni ejecuta por sí mismo los movimientos monetarios.

## 2. Qué necesita resolver el negocio

### 2.1. Las tres modalidades de transferencia

| Modalidad | Nombre | Problema empresarial |
|---|---|---|
| Interna | `INTERNAL` | Trasladar dinero entre cuentas con resultado indivisible. |
| Saliente | `OUTGOING` | Enviar dinero a otro participante. |
| Entrante | `INCOMING` | Recibir una instrucción externa y acreditar una cuenta una vez. |

Una operación puede estar recibida sin haberse ejecutado. Un mensaje puede haberse enviado sin que el
beneficiario esté acreditado. Una devolución es una operación nueva y vinculada, no la desaparición de la original.

### 2.2. Catálogo de casos de uso

| Caso de uso | Propósito | Contrato |
|---|---|---|
| `ExecuteInternalTransfer` | Coordinar débito y crédito conjuntos. | CI-05-001 |
| `SendOutgoingTransfer` | Proteger cobertura y coordinar envío. | CI-05-002 |
| `ReceiveIncomingTransfer` | Coordinar aceptación y crédito único. | CI-05-003 |
| `CancelTransfer` | Impedir ejecución cuando pueda confirmarse. | CI-05-004 |
| `ReturnTransfer` | Restituir principal en una operación vinculada. | CI-05-005 |
| `GetTransfer` | Consultar hechos, importes y pendientes. | CI-05-006 |
| `GetTransferAction` | Recuperar el resultado de una petición. | CI-05-006 |
| `SearchTransfers` | Buscar por identidad externa completa. | CI-05-006 |

Estos ocho nombres permiten reconocer las operaciones públicas. Además existen continuaciones internas:
`RecoverRemoteStep`, `IncorporateExternalResult` y `ResumeTransferWork`. No representan nuevas
operaciones bancarias ofrecidas al cliente: permiten completar y recuperar las anteriores.

### 2.3. Diferencia entre instrucción, actuación y efecto

| Concepto              | Nombre de referencia  | Qué identifica                                                  |
|-----------------------|-----------------------|-----------------------------------------------------------------|
| Transferencia         | `transferId`          | Una instrucción bancaria específica.                            |
| Operación empresarial | `operationId`         | El recorrido de negocio que reúne actuaciones relacionadas.     |
| Actuación             | `actionId`            | Una petición persistente y recuperable.                         |
| Actuación remota      | `remoteActionId`      | Lo solicitado a otra autoridad.                                 |
| Efecto económico      | `effectId`            | Débito, crédito u otro efecto que no debe duplicarse.           |
| Efecto de devolución  | `returnEffectId`      | Efecto propio de una devolución vinculada.                      |
| Intercambio           | `exchangeReference`   | Operación de red administrada por Compensación.                 |
| Referencia externa    | `externalOperationId` | Identificador del mecanismo dentro de participante y mecanismo. |

**Ejemplo:** una transferencia puede tener un `transferId`, una solicitud de débito con su `remoteActionId`
y una devolución posterior con otro `returnEffectId`. No son tres transferencias independientes.

### 2.4. Hechos que nunca se confunden

| Hecho | Qué afirma | Qué no afirma |
|---|---|---|
| `RECEIVED` | La instrucción se recibió y registró. | Que el dinero se movió. |
| `ACCEPTED_FOR_EXECUTION` | Se admitió para ejecutar. | Que se acreditó al beneficiario. |
| `DISPATCHED` | La instrucción salió al mecanismo. | Que el receptor la aceptó firmemente. |
| `IRREVOCABLY_ACCEPTED` | Existe aceptación firme. | Que ya ocurrió el crédito. |
| `BENEFICIARY_CREDITED` | Se comprobó la acreditación. | Que ya se liquidó entre entidades. |
| `EXTERNALLY_SETTLED` | La autoridad confirmó la liquidación. | Que liquidar sea otro crédito al cliente. |
| `RETURNED` | Existe devolución posterior confirmada. | Que se borró la transferencia original. |

Un timeout no es un hecho financiero. La ausencia de respuesta tampoco demuestra rechazo ni cancelación.

## 3. Reglas comunes antes de estudiar cada caso

### 3.1. Identidad y repeticiones

- La misma instrucción, con la misma identidad y contenido, recupera el resultado original.
- La misma identidad con otro contenido representa un conflicto; no autoriza otra ejecución.
- Dos instrucciones parecidas con identidades diferentes no son automáticamente duplicadas.
- La clave técnica `Idempotency-Key` no reemplaza la identidad del efecto monetario.
- La repetición de un evento o mensaje tampoco debe repetir débitos, créditos o devoluciones.

La identidad de origen de una instrucción se conserva mediante `(originService, originType, originReference)`.
La obtiene o valida el caso de uso a partir de su procedencia; no se sustituye por `actionId`,
`Idempotency-Key` ni por la referencia de un mensaje de transporte.

### 3.2. Contenido autorizado

La conformidad comprende ordenante, representado cuando exista, cuenta origen, destino resuelto, beneficiario,
principal, moneda, cargos aceptados, finalidad y las obligaciones, mandatos u ocurrencias vinculados.

Si cambia materialmente ese contenido, se necesitan las comprobaciones y la aceptación que correspondan.
El destino aceptado se conserva incluso si posteriormente cambia el alias que se utilizó para resolverlo.

El `acceptedContentHash` de `AcceptedDestination` corresponde al destino presentado y aceptado.
El `acceptedContentHash` de `Transfer` corresponde al contenido vinculante de la instrucción completa.
Cada huella se comprueba contra su contenido canónico y versión de canonicalización; no se exige igualdad
entre ambas huellas. La huella recibida de una operación externa tiene su propio contenido y esquema.

### 3.3. Datos y exactitud monetaria

- `principalAmount` es positivo. Para ARS y USD admite hasta dos decimales; no se redondea en silencio.
- Los acumulados y remanentes pueden ser cero; los importes que se ejecutan deben ser positivos.
- Se utiliza `BigDecimal`, nunca `float` o `double` para decidir cantidades monetarias.
- Principal, comisiones y tributos son conceptos diferentes y conservan su causa.
- No se convierte de moneda implícitamente; una conversión requiere operación de cambio identificada.
- Una comisión por ejecución requiere que se confirme el hecho que la origina.

### 3.4. Contexto de actuación

| Dato                 | Tipo de referencia    | Para qué se necesita                            |
|----------------------|-----------------------|-------------------------------------------------|
| `actionId`           | `UUID`                | Recuperar la petición exacta.                   |
| `operationId`        | `UUID`                | Relacionar las actuaciones del mismo recorrido. |
| `causationId`        | `UUID` condicional    | Identificar la actuación que causó otra.        |
| `purpose`            | `String`              | Conocer la finalidad de la actuación.           |
| `reason`             | `String` condicional  | Fundamentar una actuación que lo requiera.      |
| `expectedVersion`    | `Long` condicional    | Impedir modificar información desactualizada.   |
| `originalReceivedAt` | `Instant` condicional | Conservar recepción original relevante.         |
| `evidenceReferences` | Referencias tipadas   | Respaldar decisiones y resultados.              |

La identidad del actor y del servicio procede de autenticación o delegación verificadas.
Un campo enviado por el solicitante no demuestra, por sí mismo, que tenga facultades sobre una cuenta.
El caso de uso debe recibir ese contexto verificado, sin exigir una clase Java llamada `RequestContext`.

### 3.5. Quién decide cada parte

| Pregunta | Autoridad | Qué conserva Transferencias |
|---|---|---|
| ¿La cuenta existe y admite la operación? | Cuentas | Comprobación y referencia. |
| ¿Qué cuenta corresponde a un alias? | Identificación bancaria | Destino resuelto y aceptado. |
| ¿El actor representa al ordenante? | Representación | Alcance, autorización y evidencia. |
| ¿Hay impedimentos o restricciones? | Riesgo y cumplimiento | Resultado verificable. |
| ¿Qué cargos corresponden? | Transferencias determina los conceptos propios; las autoridades comerciales y tributarias aportan condiciones y criterios. | Determinación, concepto, hecho generador y evidencia. |
| ¿Hay capacidad monetaria y se ejecutó? | Fondos | Reserva, actuación y movimientos. |
| ¿Se envió, aceptó o acreditó afuera? | Compensación y mecanismo | Intercambio, hechos y evidencias. |
| ¿Se liquidó entre participantes? | Tesorería | Referencias y hechos informados. |
| ¿Se aplicó una deuda de cliente? | Pagos o acreedor | Relación con la instrucción, sin aplicar deuda. |

Las comunicaciones con estas autoridades se expresan mediante contratos y puertos.
El caso de uso decide **qué pedir y qué hacer con la respuesta**, no cómo funciona la autoridad.
La capa de aplicación coordina las integraciones; `Transfer` y sus componentes protegen las invariantes
e incorporan resultados comprobados sin invocar directamente a Fondos ni a Compensación.

### 3.6. Qué significa una operación persistente

Un caso de uso con efectos necesita conservar la identidad y el contenido de la petición, la decisión tomada,
los efectos comprobados y los componentes que todavía requieren resolución.

Hay tres fronteras que no deben confundirse:

1. **Decisión local:** guardar la instrucción, actuación, intención remota y deduplicación en una transacción.
2. **Actuación remota:** solicitar el efecto a su autoridad fuera de esa transacción local.
3. **Resultado local:** incorporar la respuesta o incertidumbre con historia, seguimiento y evento de salida.

No existe una transacción SQL compartida con Fondos ni con Compensación.
Para evitar duplicados, hay que conservar el identificador remoto **antes** de enviar la solicitud.

### 3.7. Observabilidad y recuperación

Las métricas, registros y trazas permiten entender qué ocurre sin alterar las decisiones financieras.
Son especialmente importantes en: actuaciones remotas inciertas, reservas sin resolución, cancelaciones pendientes,
devoluciones comprometidas, trabajos de recuperación y eventos que aún deben entregarse.

- **Logs:** operación, actuación, componente, autoridad, resultado y causa, sin datos sensibles innecesarios.
- **Trazas:** recorrido de `operationId`, `actionId`, intercambio y actuación remota.
- **Métricas:** cantidades, tiempos y componentes pendientes; sin usar clientes ni cuentas como etiquetas.
- **Recuperación:** conservar intención, referencias y siguiente consulta; no reemplazar evidencia por un timeout.

La consulta ante incertidumbre se realiza al detectarla, a los cinco minutos y a los treinta minutos. Si persiste,
se abre una investigación y se revisa durante cada jornada operativa. Ningún plazo autoriza repetir un efecto,
liberar fondos ni declarar un rechazo.

## 4. `ExecuteInternalTransfer` — Ejecutar transferencia interna

**Tipo:** comando persistente de negocio.

**Problema empresarial:** trasladar dinero entre dos cuentas del banco, garantizando que el débito del origen y
el crédito del destino ocurran como un solo conjunto. No puede confirmarse una transferencia a medias.

**Responsabilidad:** identificar y autorizar la instrucción, conservar el destino aceptado, coordinar el conjunto
monetario con Fondos y devolver el resultado comprobado o su seguimiento.

**Contrato:** CI-05-001, `POST /v1/internal-transfers`.

### 4.1. Información que recibe

El nombre orientativo del comando es `ExecuteInternalTransferCommand`.

| Dato                   | Tipo Java               | Para qué se necesita                      |
|------------------------|-------------------------|-------------------------------------------|
| `context`              | `ActionContext`         | Identidad, finalidad, causa y evidencias. |
| `orderingParty`        | `ObjectReference`       | Identificar al ordenante.                 |
| `sourceAccountId`      | `UUID`                  | Cuenta de la que se solicita debitar.     |
| `acceptedDestination`  | `AcceptedDestination`   | Destino interno y contenido aceptado.     |
| `principalAmount`      | `BigDecimal`            | Principal que se trasladará.              |
| `currency`             | `Currency`              | Moneda del principal.                     |
| `purposeCode`          | `String`                | Finalidad de la operación.                |
| `authorizationBasis`   | `AuthorizationBasis`    | Fundamento para ejecutar ese contenido.   |
| `concepts`             | `List<MonetaryConcept>` | Desglose del principal y los cargos.      |
| `commercialReferences` | `List<ObjectReference>` | Condiciones comerciales pertinentes.      |
| `fxReference`          | `ObjectReference`       | Conversión identificada, cuando exista.   |

**Por qué importa cada dato:** las cuentas definen los extremos; el importe y la moneda definen el efecto;
la autorización vincula al actor con el contenido exacto; los conceptos evitan confundir principal con cargos.

La identidad de origen y el momento efectivo de recepción se obtienen y conservan al admitir la instrucción.
Se comprueba la huella completa de `Transfer` contra su contenido, sin equipararla a la huella del destino.

### 4.2. Condiciones empresariales previas

- Origen y destino son cuentas existentes, diferentes y habilitadas para la operación solicitada.
- El destino es interno y queda fijado como fue aceptado; no se vuelve a dirigir por un cambio de alias.
- El ordenante posee las facultades necesarias, incluso si se trata de dos cuentas propias.
- Las restricciones, límites, condiciones de producto y otros controles exigibles permiten la actuación.
- El principal es positivo, de escala correcta y coherente con la moneda.
- Una operación entre monedas distintas necesita conversión expresamente identificada.
- Una consulta informativa de saldo no sustituye la decisión definitiva que toma Fondos al ejecutar.
- El descubierto no se presume: solo puede formar parte de la cobertura si es compatible con producto, cuenta y concepto y está expresamente habilitado por las condiciones aplicables; una opción del solicitante no basta.

### 4.3. Recorrido empresarial

1. Reconocer solicitante autenticado, `actionId`, `operationId` e identidad de origen.
2. Buscar la misma instrucción o actuación y contrastar la huella de su contenido.
3. Recuperar el resultado anterior si es repetición coherente; informar conflicto si cambió el contenido.
4. Verificar que las cuentas sean distintas y que el destino aceptado sea interno.
5. Comprobar cuenta, capacidades, representación, autorización, restricciones y condiciones aplicables.
6. Determinar los conceptos propios de Transferencias con las condiciones comerciales y tributarias aplicables, conservando condición, hecho generador, base, bonificación, determinación tributaria e importe final; asociar el cargo a su efecto y evitar cobrar dos veces un mismo servicio si Pagos originó la operación.
7. Incorporar la referencia de conversión expresa únicamente cuando corresponda.
8. Conservar la instrucción bancaria con identidad estable, destino y autorización aceptados.
9. Identificar el conjunto monetario solicitado, la actuación remota y sus efectos económicos.
10. Confirmar localmente instrucción, actuación, idempotencia e intención recuperable.
11. Pedir a Fondos la ejecución **conjunta** de débito y crédito, sin mantener abierta la transacción local.
12. Incorporar la confirmación con ambos movimientos, o el rechazo definitivo, o una incertidumbre identificada.
13. Confirmar localmente resultado, evidencias, historia y comunicación pendiente del cambio.
14. Entregar al solicitante el resultado y las referencias necesarias para consultar el seguimiento.

`Transfer` protege el contenido y la evolución de la instrucción. `TransferMonetaryExecution` conserva
la intención y el resultado de Fondos; la capa de aplicación coordina la petición monetaria indivisible.

### 4.4. Qué debe devolver

| Resultado              | Información que debe conservar y comunicar                          |
|------------------------|---------------------------------------------------------------------|
| Conjunto confirmado    | `transferId`, actuación, débito, crédito, hechos y evidencias.      |
| Rechazo definitivo     | Motivo comprobado y ausencia de traslado parcial.                   |
| Resultado incierto     | Identidad original de Fondos y componente pendiente de seguimiento. |
| Petición repetida      | Resultado original, sin otro movimiento.                            |
| Contenido incompatible | Conflicto, sin crear otro conjunto monetario.                       |

### 4.5. Garantías técnicas exigidas por el negocio

| Capacidad                | ¿Dónde se necesita y por qué?                                             |
|--------------------------|---------------------------------------------------------------------------|
| Idempotencia             | Antes de admitir la instrucción y al pedir el conjunto a Fondos.          |
| Transacción local        | Al registrar la intención y al incorporar su resultado comprobado.        |
| Consistencia distribuida | Entre la instrucción propia y la ejecución indivisible que decide Fondos. |
| Recuperación             | Si Fondos ejecutó, pero su respuesta se perdió.                           |
| Concurrencia             | En versiones, identidad original y decisión monetaria de Fondos.          |
| Observabilidad           | Tiempo hasta confirmación y transferencias inciertas por autoridad.       |
| Eventos                  | Comunicar hechos confirmados sin producir otro efecto.                    |

Las garantías monetarias corresponden a Fondos. Transferencias conserva la petición, las referencias y la evidencia.

### 4.6. Situaciones que tenés que saber resolver

| Situación                                     | Conducta correcta                                      |
|-----------------------------------------------|--------------------------------------------------------|
| Misma cuenta en ambos extremos                | Rechazar antes de pedir ejecución.                     |
| Cuenta no habilitada                          | No iniciar el movimiento.                              |
| Fondos confirma fondos insuficientes          | Rechazar sin traslado parcial.                         |
| Fondos confirma el conjunto                   | Registrar ambos movimientos vinculados.                |
| Fondos ejecutó, pero respondió timeout        | Consultar la actuación original; no crear otro débito. |
| Cambia el alias después de aceptar            | Mantener la cuenta que se había aceptado.              |
| Llegan dos peticiones de la misma instrucción | Recuperar una única transferencia.                     |

**Ejemplo:** origen con 100, destino con 20 y principal de 30, sin cargos. Fondos debe confirmar el
conjunto que deja 70 en origen y 50 en destino. Transferencias conserva el resultado, no los saldos.

### 4.7. Qué observar y qué probar

- Registrar la identidad de la actuación y el resultado de Fondos, sin exponer cuentas en métricas.
- Medir solicitudes, confirmaciones, rechazos y actuaciones sin respuesta comprobable.
- Probar un resultado confirmado, una ejecución rechazada y una respuesta perdida después del efecto.
- Probar que la petición repetida no origina dos conjuntos monetarios.
- Comprobar que el mismo origen y destino nunca llega como ejecución válida a Fondos.
- Probar que el contenido autorizado y el destino aceptado se validan con sus propias huellas.

**Aprendizaje central:** orquestar un efecto bancario indivisible sin administrar el recurso monetario.

## 5. `SendOutgoingTransfer` — Enviar transferencia externa saliente

**Tipo:** comando persistente de negocio.

**Problema empresarial:** enviar dinero hacia otra entidad aunque el banco no controle el tiempo ni el resultado
final del mecanismo externo. Durante esa espera debe conservar fondos suficientes y evitar duplicar el envío.

**Responsabilidad:** coordinar autorización, cobertura monetaria, despacho, aceptación firme, acreditación y
consumo de reserva, distinguiendo cada hecho por sus evidencias.

**Contrato:** CI-05-002, `POST /v1/outgoing-transfers`.

### 5.1. Información que recibe

El nombre orientativo del comando es `SendOutgoingTransferCommand`.

| Dato                   | Tipo Java               | Para qué se necesita                          |
|------------------------|-------------------------|-----------------------------------------------|
| `context`              | `ActionContext`         | Identificar petición, finalidad y evidencias. |
| `orderingParty`        | `ObjectReference`       | Identificar al ordenante.                     |
| `sourceAccountId`      | `UUID`                  | Cuenta que deberá soportar el débito.         |
| `acceptedDestination`  | `AcceptedDestination`   | Destino externo, beneficiario y aceptación.   |
| `mechanismReference`   | `ObjectReference`       | Mecanismo habilitado y versión utilizada.     |
| `principalAmount`      | `BigDecimal`            | Principal que se envía.                       |
| `currency`             | `Currency`              | Moneda del envío.                             |
| `purposeCode`          | `String`                | Finalidad empresarial.                        |
| `authorizationBasis`   | `AuthorizationBasis`    | Autorización del contenido exacto.            |
| `concepts`             | `List<MonetaryConcept>` | Principal, cargos y tributos diferenciados.   |
| `commercialReferences` | `List<ObjectReference>` | Condiciones comerciales utilizadas.           |
| `fxReference`          | `ObjectReference`       | Conversión expresa, cuando corresponde.       |

La identidad externa que entrega el mecanismo puede conocerse después de aceptar o enviar la solicitud.
No debe inventarse una referencia definitiva antes de que su autoridad la informe.
La identidad de origen de la instrucción y su huella completa se conservan independientemente del
destino aceptado. La huella del destino no se compara por igualdad con la huella de toda la instrucción.

### 5.2. Condiciones empresariales previas

- El ordenante posee facultades para debitar la cuenta de origen.
- El destino externo y el beneficiario fueron presentados y aceptados conforme a su mecanismo.
- El participante, mecanismo, moneda y condiciones aplicables están habilitados.
- Se cumplieron las comprobaciones de cuenta, riesgo, restricciones, representación y cargos pertinentes.
- Fondos constituyó o reconoció cobertura suficiente antes de habilitar el despacho.
- El descubierto no se presume: solo se admite si las condiciones aplicables lo habilitan expresamente y es compatible con producto, cuenta y concepto; una opción del solicitante no basta.
- La cancelación y el despacho deben ordenarse: no puede enviarse una transferencia ya cancelada.

### 5.3. Recorrido empresarial

1. Reconocer la identidad y contenido de la instrucción y resolver duplicaciones o conflictos.
2. Comprobar facultades, cuenta de origen, destino aceptado, mecanismo, restricciones y condiciones.
3. Determinar los cargos y tributos propios de Transferencias con los criterios comerciales y tributarios aplicables, registrando su hecho generador, base, determinación e identidad de efecto; si Pagos originó la operación, evitar cobrar dos veces el mismo servicio.
4. Conservar `Transfer` y una intención recuperable de constituir reserva.
5. Pedir a Fondos la reserva del principal y cargos autorizados, usando una identidad remota estable.
6. Registrar la reserva confirmada o conservar el resultado incierto sin asumir que los fondos están libres.
7. Preparar el intercambio por el mecanismo elegido y proteger la cobertura frente al despacho incierto.
8. Ordenar localmente habilitación de despacho frente a una posible cancelación de esa instrucción.
9. Comprobar que no existe barrera de despacho y registrar la intención externa antes de invocar al mecanismo.
10. Solicitar el envío a Compensación, sin mantener abierta una transacción local de Transferencias.
11. Registrar el hecho `DISPATCHED` solo cuando la autoridad confirme el envío.
12. Registrar `IRREVOCABLY_ACCEPTED` solo con la aceptación firme del participante.
13. Registrar `BENEFICIARY_CREDITED` únicamente con acreditación comprobada.
14. Tras comprobar la acreditación, pedir a Fondos consumir la reserva con la identidad del efecto original.
15. Informar a Compensación el hecho de transferencia acreditada y los datos requeridos para su incorporación, con identidad estable e idempotente para no duplicar la incorporación.
16. Si una llamada o resultado es incierto, conservar cobertura, evidencia y seguimiento de la actuación original.
17. Registrar los hechos y resultados locales junto a historial y comunicación recuperable del cambio.
18. Entregar resultado y referencias sin traducir un acuse técnico como acreditación.

`TransferOutgoingExecution` conserva cobertura y avance externo; `TransferExternalOperation` conserva
la identidad externa cuando la autoridad la informa. `TransferRemoteStep` protege las actuaciones
recuperables. Ninguno de estos componentes envía dinero o mensajes por sí mismo.

Este circuito puede durar más que la primera petición. El solicitante necesita una referencia consultable.

### 5.4. Qué debe devolver

| Resultado o dato | Significado                                                   |
|------------------|---------------------------------------------------------------|
| `transferId`     | Instrucción de negocio conservada.                            |
| Reserva          | Cobertura confirmada o pendiente de Fondos, con su identidad. |
| Intercambio      | Referencia del mecanismo y operación externa, cuando existan. |
| Despacho         | Evidencia de envío, distinta de aceptación y acreditación.    |
| Aceptación firme | Compromiso confirmado por el participante.                    |
| Crédito externo  | Acreditación comprobada con evidencia.                        |
| Pendientes       | Componente, autoridad, motivo y referencia para recuperación. |

### 5.5. Garantías técnicas exigidas por el negocio

| Capacidad                | ¿Dónde se necesita y por qué?                                                |
|--------------------------|------------------------------------------------------------------------------|
| Reserva identificada     | Antes de enviar; el compromiso debe estar cubierto.                          |
| Ordenamiento concurrente | En la carrera entre cancelación y habilitación del despacho.                 |
| Transacciones locales    | Para persistir intención de reserva, intercambio y resultados.               |
| Idempotencia             | Para no generar otra reserva, otro envío ni otro consumo.                    |
| Recuperación             | Tras pérdida de respuesta o resultado externo incierto.                      |
| Outbox/Inbox             | Para conservar comunicación y tratar resultados repetidos.                   |
| Observabilidad           | Para detectar cobertura sin resultado, envíos inciertos y consumo pendiente. |

### 5.6. Situaciones que tenés que saber resolver

| Situación                                        | Conducta correcta                                           |
|--------------------------------------------------|-------------------------------------------------------------|
| Fondos rechaza constituir reserva                | No despachar.                                               |
| Reserva confirmada, despacho sin respuesta       | Proteger cobertura y consultar el intercambio original.     |
| Solo existe acuse técnico                        | No informar que el beneficiario recibió dinero.             |
| Participante aceptó irrevocablemente             | Conservar compromiso firme hasta el tratamiento procedente. |
| Acreditación comprobada                          | Consumir la reserva original, sin débito adicional.         |
| Rechazo externo definitivo sin ejecución posible | Tratar la cobertura con evidencia suficiente.               |
| Falla la liquidación externa después del crédito | No revertir una acreditación ya confirmada.                 |
| Se cancela antes de habilitar despacho           | Impedir el envío tardío.                                    |

**Ejemplo:** se reservan 70 para un destino externo. El mecanismo acusa recepción, pero no confirma crédito.
La transferencia todavía no está acreditada y la cobertura no puede liberarse por falta de respuesta.

### 5.7. Qué observar y qué probar

- Medir reservas protegidas sin resultado externo, envíos inciertos y créditos acreditados sin consumo resuelto.
- Correlacionar la transferencia, reserva, intercambio y actuación remota sin registrar datos sensibles.
- Probar aceptación irrevocable sin crédito, resultado externo tardío y fallo de liquidación posterior.
- Probar que una cancelación confirmada antes del envío impide cualquier despacho posterior.
- Probar que el reintento de una consulta no crea otro intercambio ni otro consumo económico.

**Aprendizaje central:** consistencia distribuida, cobertura protegida y separación de hechos externos.

## 6. `ReceiveIncomingTransfer` — Recibir transferencia externa entrante

**Tipo:** comando persistente de negocio recibido desde Compensación.

**Problema empresarial:** una entidad externa comunica que debe acreditarse dinero en una cuenta del banco.
El banco debe reconocer la operación, verificar el destino y evitar dos créditos por mensajes duplicados.

**Responsabilidad:** comprobar identidad externa y condiciones de recepción, aceptar o rechazar antes del
compromiso irrevocable y, si se acepta, coordinar un único crédito mediante Fondos.

**Contrato:** CI-05-003, `POST /v1/incoming-transfers`.

### 6.1. Información que recibe

El nombre orientativo del comando es `ReceiveIncomingTransferCommand`.

| Dato                   | Tipo o representación       | Para qué se necesita                          |
|------------------------|------------------------------|-----------------------------------------------|
| `context`              | `ActionContext`              | Identidad y causa de la actuación.            |
| `externalOperation`    | Referencia externa tipada   | Participante, mecanismo y referencia externa. |
| `orderingParty`        | `ObjectReference`            | Ordenante informado por el origen.            |
| `beneficiaryReference` | `ObjectReference`            | Beneficiario cuando el mecanismo lo exige.    |
| `destinationAccountId` | `UUID`                       | Cuenta que recibiría el crédito.              |
| `principalAmount`      | `BigDecimal`                 | Importe del crédito solicitado.               |
| `currency`             | `Currency`                   | Moneda que debe aceptar la cuenta.            |
| `purposeCode`          | `String`                     | Finalidad informada.                          |
| `messageReference`     | `ObjectReference`            | Identidad del mensaje y su procedencia.       |
| `evidenceReferences`   | `List<ObjectReference>`      | Evidencias de recepción.                      |
| `receivedContentHash`  | `String`                     | Huella que permite comprobar repeticiones.    |

El contrato denomina `ExternalOperationReference` a la estructura con participante, mecanismo, versión y
referencia externa. Esa estructura es obligatoria, pero no exige otra clase Java de dominio si
`TransferExternalOperation` ya conserva sus cuatro datos y la comprobación de repeticiones.
La identidad externa de negocio utiliza `(participantId, mechanismCode, externalOperationId)`.
`mechanismVersion` identifica el esquema, pero **no** crea una operación nueva si cambió la versión.
El `receivedContentHash` se contrasta con el contenido externo original conservado en
`TransferExternalOperation`; no se equipara automáticamente al `acceptedContentHash` de `Transfer`.

### 6.2. Condiciones empresariales previas

- Solo un emisor autorizado para el participante y mecanismo puede presentar la instrucción.
- El destino es una cuenta interna existente, capaz de recibir la moneda correspondiente.
- Las restricciones se interpretan según su alcance real para créditos, no según su nombre genérico.
- Se verifican los controles exigibles antes de comunicar aceptación irrevocable.
- Una identidad externa recibida anteriormente exige comparar el contenido original.
- Una referencia de mensaje, por sí sola, no identifica un nuevo crédito económico.

### 6.3. Recorrido empresarial

1. Verificar la identidad del servicio que entrega la instrucción y la habilitación del mecanismo.
2. Extraer la identidad externa completa y localizar una recepción anterior con esa clave.
3. Si ya existe con igual contenido, recuperar la transferencia y el resultado originales.
4. Si ya existe con contenido incompatible, conservar la inconsistencia sin acreditar nuevamente.
5. Registrar procedencia, mensaje, ordenante, beneficiario, destino, importe y moneda informados.
6. Preguntar a Cuentas si la cuenta de destino existe, admite la moneda y puede recibir el crédito.
7. Obtener los controles necesarios y distinguir restricciones sobre créditos de las que solo afectan débitos.
8. Si el destino o los controles impiden admitir la instrucción, rechazar antes de aceptar irrevocablemente.
9. Si corresponde aceptar, conservar la instrucción y las comprobaciones que respaldan la decisión.
10. Registrar la aceptación firme y la obligación de acreditar o resolver la devolución procedente.
11. Identificar `creditEffectId` y `remoteActionId` estables antes de solicitar el movimiento a Fondos.
12. Confirmar localmente la aceptación, la intención de crédito, la historia y el seguimiento.
13. Pedir a Fondos el crédito identificado, fuera de la transacción SQL local.
14. Incorporar la confirmación del crédito; ante incertidumbre consultar la actuación original; ante imposibilidad definitiva de acreditar, conservar la obligación y tramitar la resolución procedente (rechazo, retención o devolución conforme a la causa y al estado de aceptación), sin tratarla como un simple timeout ni afirmar un crédito inexistente.
15. Informar a Compensación los hechos comprobados y los componentes cuya resolución continúa.

La identidad de origen de `Transfer` se obtiene del emisor y de la instrucción reconocida.
`TransferIncomingAcceptance` conserva verificaciones y aceptación firme; `TransferExternalOperation`
conserva la identidad del participante. `TransferMonetaryExecution` registra la actuación de crédito
de Fondos. La aplicación coordina la acreditación sin ejecutarla dentro del agregado.

### 6.4. Qué debe devolver

| Resultado                        | Información que debe conservar y comunicar                        |
|----------------------------------|-------------------------------------------------------------------|
| Recepción repetida coherente     | Identidad y resultado originales, sin otro crédito.               |
| Rechazo anterior a aceptación    | Motivo y evidencia de que no se asumió compromiso firme.          |
| Aceptación irrevocable           | Identidad y obligación de acreditar o resolver el retorno.        |
| Crédito confirmado               | Efecto único, movimiento de Fondos y evidencia.                   |
| Crédito incierto                 | Misma actuación monetaria y componente pendiente de recuperación. |
| Contenido externo contradictorio | Conflicto, sin modificar la recepción anterior.                   |

Aceptar irrevocablemente y acreditar son dos hechos distintos. El resultado debe permitir reconocer esa diferencia.

### 6.5. Garantías técnicas exigidas por el negocio

| Capacidad            | ¿Dónde se necesita y por qué?                                               |
|----------------------|-----------------------------------------------------------------------------|
| Clave externa única  | Al reconocer participante, mecanismo y referencia.                          |
| Idempotencia         | Al recibir mensajes repetidos y al pedir el crédito a Fondos.               |
| Transacción local    | Para aceptación firme, intención económica, identidad y seguimiento.        |
| Control de autoridad | Para impedir que cualquier emisor informe operaciones de otro participante. |
| Recuperación         | Cuando Fondos acreditó, pero no llegó su respuesta.                         |
| Inbox                | Para reconocer mensajes repetidos o fuera de orden.                         |
| Observabilidad       | Para detectar aceptaciones firmes que siguen sin crédito comprobado.        |

### 6.6. Situaciones que tenés que saber resolver

| Situación                                             | Conducta correcta                                  |
|-------------------------------------------------------|----------------------------------------------------|
| Mismo participante, mecanismo, referencia y contenido | Recuperar la operación original.                   |
| Misma identidad externa con otro importe              | Informar conflicto; no acreditar otra vez.         |
| Cuenta inexistente                                    | Rechazar antes de aceptación firme.                |
| Restricción exclusiva de débitos                      | Comprobar si el crédito sigue permitido.           |
| Mensaje duplicado con otra versión de mecanismo       | Mantener la misma identidad externa de negocio.    |
| Aceptación firme seguida de timeout de Fondos         | Conservar obligación y consultar `creditEffectId`. |
| Fondos confirma crédito con respuesta tardía          | Incorporar evidencia al efecto original.           |

**Ejemplo:** el participante P informa dos veces una transferencia de 80 con la misma referencia y contenido.
El banco conserva una sola recepción y pide un único crédito, aunque lleguen mensajes distintos.

### 6.7. Qué observar y qué probar

- Medir recepciones aceptadas, rechazadas, duplicadas y aceptadas firmemente sin crédito comprobado.
- Correlacionar identidad del mensaje, identidad externa, transferencia y efecto de crédito.
- Probar recepción duplicada, contenido contradictorio y cambio de versión del mecanismo.
- Probar cuenta inexistente, restricción solo de débito y respuesta perdida de Fondos.
- Confirmar que una recepción aceptada firmemente mantiene seguimiento hasta resolver el efecto.
- Contrastar el contenido externo original sin confundir su huella con la de la instrucción local.

**Aprendizaje central:** deduplicación empresarial, seguridad entre servicios y compromiso posterior a la aceptación.

## 7. `CancelTransfer` — Cancelar una transferencia

**Tipo:** comando persistente sobre una instrucción existente.

**Problema empresarial:** el solicitante quiere detener una transferencia, pero el banco debe demostrar que aún
puede impedir su ejecución. Recibir la solicitud no significa que el mecanismo externo la haya detenido.

**Responsabilidad:** autorizar la petición y coordinarla con el despacho o la ejecución, sin declarar cancelación
ni liberar cobertura mientras exista posibilidad real de ejecución.

**Contrato:** CI-05-004, `POST /v1/transfers/{transferId}/cancellations`.

### 7.1. Información que recibe

El nombre orientativo del comando es `CancelTransferCommand`.

| Dato                 | Tipo Java            | Para qué se necesita                           |
|----------------------|----------------------|------------------------------------------------|
| `context`            | `ActionContext`      | Identidad, finalidad y versión esperada.       |
| `transferId`         | `UUID`               | Instrucción original a cancelar.               |
| `reason`             | `String`             | Motivo de la solicitud, hasta 4000 caracteres. |
| `authorizationBasis` | `AuthorizationBasis` | Facultades para pedir esa cancelación.         |

La actuación de cancelación obtiene identidad propia `cancellationId`.
No se identifica una cancelación únicamente con `transferId`: una instrucción puede recibir varias actuaciones.

### 7.2. Condiciones empresariales previas

- La transferencia existe y el solicitante tiene facultades para intervenir sobre ella.
- El motivo y la autorización son pertinentes y verificables.
- Se conoce el avance real: intercambio, envío, crédito, pasos remotos y hechos comprobados.
- Se controla la versión cuando la decisión compite con otros cambios.
- Si hubo intercambio o envío, la autoridad externa debe confirmar si aún puede producirse ejecución.

### 7.3. Recorrido empresarial

1. Reconocer actor, actuación original y posibles repeticiones de la solicitud.
2. Recuperar la transferencia y su avance real, no solo un estado general.
3. Verificar autorización, motivo y versión de la instrucción que se pretende cancelar.
4. Ordenar la decisión local frente a la creación o habilitación de un despacho concurrente.
5. Crear una actuación de cancelación con identidad, causa y referencia a la transferencia.
6. Si se confirma que no existe envío ni capacidad posterior de ejecutarlo, impedir el despacho definitivamente.
7. Conservar la barrera de despacho `dispatchBarrier` junto con la decisión local.
8. Si hay intercambio o envío, identificar una consulta o petición de cancelación al mecanismo responsable.
9. Registrar esa intención con referencia remota estable antes de solicitar la actuación externa.
10. Consultar o solicitar cancelación al mecanismo sin mantener abierta una transacción SQL local.
11. Incorporar su evidencia y distinguir cancelación confirmada de resultado todavía incierto.
12. Solo al demostrar ausencia de ejecución pendiente, coordinar el tratamiento de cobertura con Fondos.
13. Si ya hubo acreditación, conservar el hecho y comunicar que corresponde un retorno, no una cancelación ficticia.
14. Registrar resultado, pendientes, historia y comunicación recuperable del cambio.

`TransferCancellation` conserva la actuación, la decisión y su evidencia. `Transfer` ordena la
barrera `dispatchBarrier` frente al despacho; la petición externa la coordina la capa de aplicación.

### 7.4. Qué debe devolver

| `CancellationOutcome`           | Significado empresarial                                                   |
|---------------------------------|---------------------------------------------------------------------------|
| `CANCELLED`                     | Se comprobó ausencia de ejecución y se impidió cualquier envío posterior. |
| `PENDING_EXTERNAL_CONFIRMATION` | El mecanismo aún debe demostrar el resultado de la cancelación.           |
| `ALREADY_CREDITED`              | El beneficiario ya recibió el crédito; corresponde evaluar devolución.    |
| `REQUIRES_RESOLUTION`           | Existen incertidumbres o evidencias que necesitan investigación.          |

El resultado incluye `cancellationId`, transferencia original, actuación, evidencias y pendientes aplicables.

### 7.5. Garantías técnicas exigidas por el negocio

| Capacidad          | ¿Dónde se necesita y por qué?                                         |
|--------------------|-----------------------------------------------------------------------|
| Concurrencia       | Al competir cancelación con creación o habilitación del despacho.     |
| Barrera durable    | Para impedir que una tarea atrasada envíe después de cancelar.        |
| Idempotencia       | Para no duplicar pedidos externos de cancelación.                     |
| Recuperación       | Si el mecanismo no responde o responde después.                       |
| Consistencia local | Al confirmar barrera, actuación, historia y seguimiento.              |
| Evidencia externa  | Para afirmar que ya no es posible ejecutar la instrucción.            |
| Observabilidad     | Para detectar cancelaciones con cobertura o intercambio sin resolver. |

### 7.6. Situaciones que tenés que saber resolver

| Situación                                       | Conducta correcta                                        |
|-------------------------------------------------|----------------------------------------------------------|
| Se cancela antes de crear intercambio           | Registrar barrera; no permitir despacho tardío.          |
| Cancelación y despacho llegan al mismo tiempo   | Ordenar ambas decisiones sobre la misma instrucción.     |
| Ya se envió la transferencia                    | Consultar y solicitar cancelación mediante el mecanismo. |
| El mecanismo responde únicamente `NOT_RECEIVED` | No presumir que el envío futuro es imposible.            |
| Hay timeout de cancelación                      | Mantener cobertura y seguimiento.                        |
| Se confirmó acreditación                        | Informar `ALREADY_CREDITED`; considerar devolución.      |
| Se prueba que no habrá ejecución                | Confirmar cancelación y tratar cobertura con Fondos.     |

**Ejemplo:** una tarea programada intenta despachar una instrucción al mismo tiempo que llega una cancelación.
Una decisión debe prevalecer con protección transaccional; no puede quedar cancelada y enviarse después.

### 7.7. Qué observar y qué probar

- Medir cancelaciones anteriores al envío, pendientes externas y denegadas por acreditación.
- Registrar correlación de transferencia, cancelación, intercambio y consulta externa.
- Probar carreras reales entre habilitación de despacho y confirmación de cancelación.
- Probar timeout, respuesta tardía y `NOT_RECEIVED` sin evidencia concluyente.
- Verificar que ninguna cancelación libere reservas solo por haberse solicitado.

**Aprendizaje central:** decisiones concurrentes incompatibles y necesidad de evidencia para cerrar una operación.

## 8. `ReturnTransfer` — Tramitar una devolución de transferencia

**Tipo:** comando persistente que origina una operación económica vinculada.

**Problema empresarial:** el dinero ya fue acreditado y corresponde devolverlo total o parcialmente.
El banco debe impedir que distintas solicitudes devuelvan más que el principal disponible.

**Responsabilidad:** comprobar fundamento, importe y destino de retorno; comprometer atómicamente el importe
solicitado sin exceder el remanente; coordinar un efecto único de devolución y vincularlo a la transferencia.

**Contrato:** CI-05-005, `POST /v1/transfers/{transferId}/returns`.

### 8.1. Información que recibe

El nombre orientativo del comando es `ReturnTransferCommand`.

Este comando describe una devolución iniciada localmente. Una devolución recibida desde el exterior es una entrada
distinta: se reconoce por participante, mecanismo y referencia externa completa, se vincula a la transferencia original
con evidencia de Compensación y se deduplica por esa identidad y contenido. Se registra su resultado o componente
pendiente sin volver a solicitar el mismo efecto a Fondos ni duplicar la regularización. Si no puede vincularse con
certeza, se conserva para investigación; no se aplica por similitud ni se acredita dos veces.

| Dato                 | Tipo Java            | Para qué se necesita                            |
|----------------------|----------------------|-------------------------------------------------|
| `context`            | `ActionContext`      | Identidad y fundamento de la actuación.         |
| `transferId`         | `UUID`               | Transferencia cuyo principal se devuelve.       |
| `returnEffectId`     | `UUID`               | Efecto económico que no debe repetirse.         |
| `amount`             | `BigDecimal`         | Importe positivo que se solicita devolver.      |
| `currency`           | `Currency`           | Moneda del principal de retorno.                |
| `reasonCode`         | `String`             | Motivo codificado de la devolución.             |
| `returnDestination`  | `ReturnDestination`  | Destino al que debe dirigirse el retorno.       |
| `authorizationBasis` | `AuthorizationBasis` | Facultad o fundamento suficiente para devolver. |
| `relatedReturns`     | Referencias tipadas  | Antecedentes pertinentes de devoluciones.       |

Cada devolución tiene además una identidad propia `returnId`, separada de `transferId` y `returnEffectId`.

### 8.2. Condiciones empresariales previas

- La transferencia original existe y su resultado permite tramitar la devolución correspondiente.
- Existe autorización o fundamento suficiente para crear el efecto económico de retorno.
- La solicitud del ordenante original no concede por sí sola permiso para debitar al beneficiario.
- El importe solicitado es positivo, de escala válida y en la moneda del principal original.
- El destino de retorno es coherente con la operación y con su fundamento.
- Se conocen importes confirmados, comprometidos y todavía disponibles para devolver.

### 8.3. Regla económica fundamental

La transferencia original conserva un principal fijo. Se distinguen tres partes:

- `returnedAmount`: principal ya devuelto y confirmado.
- `pendingAmount`: principal comprometido por devoluciones cuya resolución está pendiente.
- `remainingAmount`: principal todavía disponible para nuevas devoluciones.

```text
originalPrincipal = returnedAmount + pendingAmount + remainingAmount
```

Los tres importes son no negativos. Una devolución incierta **sigue ocupando su importe comprometido**.

**Ejemplo:** principal 100, confirmado devuelto 40 y comprometido 20. El disponible es 40.
Una devolución adicional de 50 no se admite, aunque una consulta superficial indique que solo se devolvieron 40.

### 8.4. Recorrido empresarial

1. Identificar petición, transferencia original y `returnEffectId` y resolver repeticiones.
2. Verificar el avance de la transferencia y la legitimidad de tramitar una devolución.
3. Comprobar fundamento, facultades, moneda, motivo, importe y destino de retorno.
4. Obtener el control económico del principal y proteger su modificación frente a solicitudes simultáneas.
5. Confirmar que `amount` no supera `remainingAmount`.
6. Crear una devolución identificada y vinculada a la transferencia, sin cambiar los hechos originales.
7. Comprometer atómicamente el importe: disminuir disponible y aumentar pendiente.
8. Registrar la intención de actuación interna o externa con `returnEffectId` y `remoteActionId` estables.
9. Confirmar localmente devolución, compromiso, acción, historia e intención recuperable.
10. Coordinar el efecto mediante Fondos o el mecanismo que corresponda, según la modalidad de retorno.
11. Si el efecto se confirma, trasladar el importe comprometido a devuelto confirmado.
12. Si el rechazo definitivo prueba que no se ejecutará, liberar el compromiso de manera consistente.
13. Si el resultado es incierto, conservar el importe pendiente y recuperar la actuación original.
14. Incorporar evidencias, resultado, remanentes e historia, sin borrar ni alterar la transferencia original.

`TransferReturnControl` protege la ecuación económica y `TransferReturn` identifica cada devolución.
Ambos cambios se confirman localmente de forma atómica. `TransferReturnStep` conserva la intención
remota del retorno para recuperarla sin duplicar `returnEffectId`. El control del remanente no crea
una reserva de Fondos ni modifica el saldo de ninguna cuenta.

### 8.5. Qué debe devolver

| Dato o resultado     | Significado                                                       |
|----------------------|-------------------------------------------------------------------|
| `returnId`           | Identidad de esta devolución vinculada.                           |
| `returnEffectId`     | Identidad económica del efecto que no se repite.                  |
| Importe confirmado   | Principal devuelto con evidencia.                                 |
| Importe comprometido | Principal que no puede asignarse a otra devolución todavía.       |
| `remainingAmount`    | Principal disponible para nuevas devoluciones.                    |
| Devolución incierta  | Misma identidad económica y seguimiento de su actuación original. |
| Rechazo definitivo   | Resultado comprobado y tratamiento del compromiso.                |

### 8.6. Garantías técnicas exigidas por el negocio

| Capacidad              | ¿Dónde se necesita y por qué?                                           |
|------------------------|-------------------------------------------------------------------------|
| Invariante monetaria   | Al comprometer, confirmar o liberar importes de devolución.             |
| Concurrencia real      | Cuando dos solicitudes compiten por el mismo `remainingAmount`.         |
| Transacción local      | Para registrar devolución y compromiso como una decisión indivisible.   |
| Idempotencia económica | Para no ejecutar dos retornos con el mismo `returnEffectId`.            |
| Recuperación           | Para conservar comprometido un retorno que quizá se ejecutó.            |
| Evidencia              | Para confirmar devolución o liberar su importe tras rechazo definitivo. |
| Observabilidad         | Para detectar importes comprometidos sin resolución comprobable.        |

### 8.7. Situaciones que tenés que saber resolver

| Situación                                            | Conducta correcta                                    |
|------------------------------------------------------|------------------------------------------------------|
| Principal 100, devuelto 40, pendiente 20             | Solo quedan 40 disponibles.                          |
| Dos solicitudes simultáneas de 30 con disponible 40  | No comprometer ambas: excederían el principal.       |
| Misma devolución llega dos veces                     | Recuperar `returnEffectId` original.                 |
| Devolución remota responde timeout                   | Mantener comprometido el importe.                    |
| Rechazo definitivo sin ejecución                     | Liberar compromiso con evidencia suficiente.         |
| Ordenante exige debitar al beneficiario sin facultad | No ejecutar ese débito.                              |
| Se devuelve principal                                | No revertir automáticamente comisiones ni tributos.  |
| Existe error del banco con cargos indebidos          | Coordinar la regularización procedente por separado. |

### 8.8. Qué observar y qué probar

- Medir devoluciones confirmadas, inciertas y remanentes comprometidos a la espera de evidencia.
- Correlacionar transferencia original, `returnId`, `returnEffectId` y actuación remota.
- Probar devolución parcial, repetición económica y retorno confirmado con respuesta perdida.
- Probar simultaneidad con una base transaccional real; una prueba con objetos en memoria no basta.
- Verificar que ninguna modificación rompa la ecuación del principal original.

**Aprendizaje central:** control concurrente de remanentes e idempotencia de un nuevo efecto económico vinculado.

## 9. `GetTransfer` — Consultar una transferencia

**Tipo:** consulta informativa, sin efectos económicos.

**Problema empresarial:** el cliente o servicio autorizado necesita saber qué pasó realmente con una transferencia,
no recibir una respuesta simplificada que confunda instrucción recibida, envío, acreditación y devolución.

**Responsabilidad:** recuperar el contenido aceptado y los hechos comprobados de la instrucción, con sus referencias,
incertidumbres y devoluciones, respetando el alcance de consulta autorizado.

**Contrato:** CI-05-006, `GET /v1/transfers/{transferId}`.

### 9.1. Información que recibe

| Dato                 | Tipo                | Para qué se necesita                              |
|----------------------|---------------------|---------------------------------------------------|
| `transferId`         | `UUID`              | Identificar la instrucción consultada.            |
| Contexto autenticado | Identidad y alcance | Proteger información de clientes y participantes. |

La consulta no requiere una nueva actuación económica ni una clave de idempotencia de comando persistente.

### 9.2. Información que debe devolver

| Información         | Qué debe poder comprender el consultante                       |
|---------------------|----------------------------------------------------------------|
| Instrucción         | Identidad, modalidad, principal, moneda y finalidad.           |
| Destino             | Snapshot aceptado, no resolución actual de alias.              |
| Autorización        | Fundamento y evidencias permitidas por el alcance de lectura.  |
| Ejecución monetaria | Reserva, efectos y movimientos informados por Fondos.          |
| Intercambio externo | Participante, mecanismo, intercambio y resultados comprobados. |
| Hechos              | Qué ocurrió, cuándo, quién lo informó y con qué evidencia.     |
| Pendientes          | Qué falta resolver, quién es responsable y cómo se sigue.      |
| Cancelaciones       | Actuaciones y resultados vinculados a la instrucción.          |
| Devoluciones        | Importe devuelto, comprometido y remanente disponible.         |

### 9.3. Recorrido empresarial

1. Reconocer la identidad del consultante y su alcance sobre la transferencia.
2. Recuperar la instrucción y sus componentes vinculados con información coherente.
3. Mostrar el destino que se aceptó, aunque otro servicio haya cambiado después su alias.
4. Diferenciar las evidencias de Fondos, Compensación y las demás autoridades.
5. Presentar cancelaciones y devoluciones sin alterar la historia de la transferencia original.
6. Informar las incertidumbres concretas; no inferir un éxito por ausencia de pendientes visibles.
7. Exponer únicamente la información a la que el consultante está autorizado.

### 9.4. Garantías técnicas exigidas por el negocio

| Capacidad               | ¿Dónde se necesita y por qué?                               |
|-------------------------|-------------------------------------------------------------|
| Lectura consistente     | Para no mezclar componentes incompatibles del mismo avance. |
| Seguridad por objeto    | Para impedir consultas sobre instrucciones ajenas.          |
| Trazabilidad            | Para mostrar procedencia y momento de los hechos.           |
| Observabilidad          | Para medir consultas fallidas y tiempo de respuesta.        |
| Sin efectos colaterales | Para que consultar nunca envíe, debite ni libere.           |

### 9.5. Situaciones que tenés que saber resolver

| Situación                                     | Conducta correcta                                |
|-----------------------------------------------|--------------------------------------------------|
| Solo consta un acuse técnico                  | No declarar acreditación.                        |
| Existe reserva sin crédito comprobado         | Informar esa diferencia.                         |
| Hay una devolución parcial                    | Mostrar principal original y remanente.          |
| El alias original cambió                      | Mostrar el destino histórico aceptado.           |
| Un actor no tiene permisos sobre la operación | No exponer información protegida.                |
| Existe resultado monetario incierto           | Mostrar componente y referencia de recuperación. |

**Aprendizaje central:** consultas autorizadas y fieles a los hechos, sin inventar conclusiones de negocio.

## 10. `GetTransferAction` — Consultar una actuación

**Tipo:** consulta informativa de una petición persistida.

**Problema empresarial:** un solicitante envió una orden, se cortó la comunicación y no sabe si la petición quedó
registrada o qué resultado obtuvo. Necesita recuperar **la misma actuación**, no ejecutarla otra vez.

**Responsabilidad:** recuperar por `actionId` el resultado, los objetos afectados, las evidencias y el seguimiento.

**Contrato:** CI-05-006, `GET /v1/transfer-actions/{actionId}`.
También existe la consulta común `GET /v1/actions/{actionId}` para actuaciones del servicio.

### 10.1. Información que recibe

| Dato                 | Tipo                | Para qué se necesita                             |
|----------------------|---------------------|--------------------------------------------------|
| `actionId`           | `UUID`              | Identidad exacta de la petición inicial.         |
| Contexto autenticado | Identidad y alcance | Comprobar autorización sobre actuación y objeto. |

### 10.2. Qué debe devolver

| Dato                           | Significado                                          |
|--------------------------------|------------------------------------------------------|
| `actionId`                     | Petición original, sin crear otra.                   |
| `operationId`                  | Recorrido empresarial al que pertenece.              |
| Tipo y referencia de actuación | Qué se solicitó y sobre qué transferencia.           |
| Resultado registrado           | Qué se confirmó, rechazó o sigue por resolver.       |
| Hechos y efectos               | Evidencias y referencias económicas comprobadas.     |
| Pendientes                     | Componentes, autoridades y consultas de seguimiento. |
| Fechas                         | Recepción efectiva y confirmación cuando existe.     |

### 10.3. Recorrido empresarial

1. Autenticar al consultante y verificar su permiso para la actuación y los objetos relacionados.
2. Buscar la actuación original por identidad estable, aunque su respuesta inicial se haya perdido.
3. Recuperar el resultado persistido y el detalle permitido de sus efectos y pendientes.
4. Mostrar si existe una actuación remota que necesita recuperación, sin invocarla por la mera consulta.
5. Devolver el resultado original o su avance comprobado sin registrar un nuevo efecto económico.

### 10.4. Garantías técnicas exigidas por el negocio

| Capacidad         | ¿Dónde se necesita y por qué?                                     |
|-------------------|-------------------------------------------------------------------|
| Identidad estable | Para reconocer una petición tras perder su respuesta.             |
| Durabilidad       | Para conservar actuaciones incluso si se reinicia la aplicación.  |
| Seguridad         | Para que conocer un UUID no otorgue acceso automático.            |
| Idempotencia      | Para no recrear la actuación al recuperarla.                      |
| Observabilidad    | Para correlacionar la petición inicial con consultas posteriores. |

### 10.5. Situaciones que tenés que saber resolver

| Situación                                   | Conducta correcta                              |
|---------------------------------------------|------------------------------------------------|
| Se pierde la respuesta de creación          | Recuperar por `actionId` sin reenviar efectos. |
| La actuación sigue sin resultado definitivo | Mostrar componentes pendientes.                |
| Otra persona conoce el identificador        | Aplicar autorización antes de revelar datos.   |
| La actuación ya tuvo rechazo definitivo     | Devolver ese resultado, sin recrearla.         |

**Aprendizaje central:** recuperación de peticiones durables y separación entre consulta y ejecución.

## 11. `SearchTransfers` — Buscar por identidad externa

**Tipo:** consulta protegida que recibe criterios mediante un cuerpo estructurado.

**Problema empresarial:** dos participantes pueden utilizar el mismo número de referencia externa.
Buscar solo por ese número podría devolver la transferencia equivocada o exponer información de otro participante.

**Responsabilidad:** localizar una instrucción por identidad externa completa y finalidad autorizada.

**Contrato:** CI-05-006, `POST /v1/transfer-searches`.

### 11.1. Información que recibe

El nombre orientativo de la consulta es `SearchTransfersQuery`.

| Dato                 | Tipo o representación       | Para qué se necesita               |
|----------------------|------------------------------|------------------------------------|
| `purpose`            | `String`                     | Finalidad autorizada de búsqueda.  |
| `externalOperation`  | Referencia externa tipada   | Identidad externa contextualizada. |
| Contexto autenticado | Identidad y alcance          | Restringir la información visible. |

El contrato utiliza la estructura `ExternalOperationReference` completa. El caso de uso puede recibir una
representación tipada equivalente sin exigir una clase de dominio adicional; la búsqueda conserva los
cuatro datos y utiliza la clave de negocio sin `mechanismVersion`.

### 11.2. Recorrido empresarial

1. Comprobar quién solicita la búsqueda y con qué finalidad.
2. Exigir participante, mecanismo y referencia externa; rechazar criterios incompletos.
3. Buscar por `(participantId, mechanismCode, externalOperationId)`.
4. No tratar `mechanismVersion` como parte de una nueva identidad de negocio.
5. Verificar el alcance sobre los objetos encontrados antes de devolver información.
6. Si aparece una contradicción de unicidad o contenido, informar inconsistencia sin elegir arbitrariamente.
7. Entregar referencias o datos autorizados, sin alterar ni reenviar la transferencia.

### 11.3. Garantías técnicas exigidas por el negocio

| Capacidad               | ¿Dónde se necesita y por qué?                                         |
|-------------------------|-----------------------------------------------------------------------|
| Identidad compuesta     | Para evitar colisiones entre participantes o mecanismos.              |
| Integridad persistente  | Para impedir registrar dos operaciones con la misma clave de negocio. |
| Autorización por objeto | Para evitar exposición por coincidencia de referencias.               |
| Consulta sin efectos    | Para que buscar no cree reservas ni envíos.                           |
| Observabilidad          | Para detectar consultas fallidas o incompatibilidades de identidad.   |

### 11.4. Situaciones que tenés que saber resolver

| Situación                                     | Conducta correcta                                  |
|-----------------------------------------------|----------------------------------------------------|
| Falta participante o mecanismo                | No realizar búsqueda ambigua.                      |
| Dos participantes usan el mismo identificador | Distinguir las operaciones por su contexto.        |
| Cambia `mechanismVersion`                     | Reconocer la misma identidad externa.              |
| Solicitante sin autorización                  | No entregar información protegida.                 |
| Se detecta contenido contradictorio           | Informar inconsistencia sin inventar un resultado. |

**Aprendizaje central:** identidades empresariales compuestas y consultas seguras sin efectos secundarios.

## 12. Continuaciones internas que permiten completar los casos de uso

No son productos bancarios ni operaciones públicas adicionales. Son trabajos necesarios para que los casos de uso
cumplan sus compromisos después de un timeout, reinicio, entrega duplicada o respuesta tardía.

### 12.1. `RecoverRemoteStep` — Recuperar una actuación remota

**Tipo:** continuación interna recuperable.

**Problema empresarial:** Fondos o Compensación pudo ejecutar una petición aunque Transferencias no haya recibido
la respuesta. Repetirla con una identidad nueva podría duplicar dinero, intercambios o devoluciones.

**Responsabilidad:** recuperar por su identidad original la actuación remota y adoptar únicamente decisiones
respaldadas por el resultado comprobado.

| Dato que necesita              | Tipo de referencia            | Para qué sirve                              |
|--------------------------------|-------------------------------|---------------------------------------------|
| `stepId`                       | `UUID`                        | Localizar el paso de recuperación.          |
| `transferId` o `returnId`      | `UUID`                        | Identificar operación propietaria.          |
| `remoteActionId`               | `UUID`                        | Consultar la actuación remota original.     |
| `effectId`                     | `UUID` condicional            | Identificar el efecto económico protegido.  |
| `requestedContent`             | `StructuredContent`           | Conservar contenido idéntico al solicitado. |
| `responsibleService`           | `String`                      | Saber qué autoridad debe responder.         |
| `resultReference`              | `ObjectReference` condicional | Vincular el resultado ya conocido.          |
| `lastCheckedAt`, `nextCheckAt` | `Instant`                     | Ordenar seguimiento recuperable.            |

Una actuación de transferencia se conserva en `TransferRemoteStep`; la de una devolución, en
`TransferReturnStep`. Cada paso mantiene contenido e identidades originales y es único por propietario
y componente. Ninguno puede sustituir una actuación remota incierta por otra identidad económica.

**Recorrido empresarial:**

1. Recuperar la intención original y su propietario, sin reconstruir un efecto nuevo.
2. Comprobar si ya se incorporó un resultado definitivo; no repetir actuaciones resueltas.
3. Consultar a la autoridad correspondiente usando `remoteActionId` y el efecto original.
4. Distinguir confirmación, rechazo definitivo, incertidumbre y evidencia contradictoria.
5. Incorporar hechos comprobados sin modificar la identidad de la petición ni borrar la historia.
6. Conservar reserva o importe comprometido mientras falte evidencia concluyente.
7. Confirmar localmente resultado, historia, seguimiento y evento cuando corresponda.
8. Si continúa incierta, conservar el siguiente control y su motivo.

**Invariantes:**

- `remoteActionId`, `effectId` y el contenido solicitado permanecen estables.
- Consultar el resultado no constituye permiso para ejecutar otra vez.
- Una respuesta tardía corresponde a la actuación original.
- El tiempo sin respuesta no demuestra que no existió el efecto.
- Una evidencia contradictoria se investiga; no se sobrescribe una confirmación anterior.

**Dónde aplicar capacidades técnicas:**

| Capacidad                 | Necesidad empresarial                                            |
|---------------------------|------------------------------------------------------------------|
| Persistencia durable      | Recuperar después de reiniciar el proceso.                       |
| Identidad remota estable  | Evitar duplicación de efectos monetarios.                        |
| Transacción local         | Confirmar la incorporación de resultado y siguiente seguimiento. |
| Programación de controles | Continuar consultas sin depender de la primera petición HTTP.    |
| Observabilidad            | Detectar pasos inciertos repetidos y su antigüedad.              |

**Prueba esencial:** Fondos acredita 50 y se pierde la respuesta. La recuperación obtiene ese crédito original.
Nunca se solicita otro crédito de 50 con identidad diferente.

### 12.2. `IncorporateExternalResult` — Incorporar un resultado externo

**Tipo:** continuación interna originada por información de una autoridad.

**Problema empresarial:** un resultado puede llegar por evento o mensaje después de la solicitud original,
e incluso repetirse o llegar fuera de orden. No puede crear otro movimiento ni contradecir hechos confirmados.

**Responsabilidad:** reconocer la procedencia, deduplicar el hecho e incorporarlo a la instrucción correcta.

| Dato que necesita   | Tipo Java          | Para qué sirve                            |
|---------------------|--------------------|-------------------------------------------|
| `transferId`        | `UUID`             | Identificar la instrucción afectada.      |
| `sourceReference`   | `ObjectReference`  | Identificar autoridad y objeto de origen. |
| `sourceFactId`      | `UUID`             | Identidad del hecho en la fuente.         |
| `sourceFactVersion` | `long`             | Distinguir versiones de un hecho.         |
| `fact`              | `ConfirmedFact`    | Contenido comprobado y evidencias.        |
| `relatedActionId`   | `UUID` condicional | Vincularlo con la actuación original.     |

`ConfirmedFact` expresa el hecho comunicado; `TransferFact` conserva su identidad local y la
clave de deduplicación `(transferId, sourceService, sourceFactId, sourceFactVersion)`.

**Recorrido empresarial:**

1. Validar que la fuente está autorizada a informar ese hecho sobre ese objeto y mecanismo.
2. Identificar el mensaje y el hecho de origen sin confundir ambas identidades.
3. Reconocer entregas duplicadas mediante mensaje y clave del hecho.
4. Comprobar que la versión recibida no contradice ni reemplaza hechos posteriores.
5. Si hay discontinuidad relevante, recuperar el resultado en la autoridad antes de decidir.
6. Incorporar el hecho al procesamiento original y actualizar el componente correspondiente.
7. Confirmar conjuntamente recepción deduplicada, cambio local, historia y comunicación de salida.
8. Confirmar la recepción técnica solo después de conservar el resultado de forma durable.

**Invariantes:**

- Mismo hecho y versión no producen dos registros ni dos efectos económicos.
- Un evento no constituye por sí mismo otra transferencia o devolución.
- Un mensaje antiguo no reemplaza una versión más reciente.
- Una contradicción conserva la evidencia recibida y requiere revisión.

**Dónde aplicar capacidades técnicas:**

| Capacidad          | Necesidad empresarial                                           |
|--------------------|-----------------------------------------------------------------|
| Inbox              | Deduplicar mensajes recibidos y reconocer su aplicación local.  |
| Versionado         | Detectar información antigua o discontinuidades.                |
| Transacción local  | Guardar la recepción y el efecto local como una sola decisión.  |
| Identidad de hecho | Deduplicar aunque el hecho llegue por canales diferentes.       |
| Observabilidad     | Medir duplicados, contradicciones y retraso de hechos externos. |

**Prueba esencial:** llega dos veces el hecho `BENEFICIARY_CREDITED` con la misma identidad y versión.
La transferencia conserva una sola confirmación y no solicita otro efecto a Fondos.

### 12.3. `ResumeTransferWork` — Reanudar trabajo de transferencia

**Tipo:** continuación interna de trabajo persistente.

**Problema empresarial:** un proceso puede detenerse después de registrar una intención o una confirmación parcial.
El banco necesita continuar exactamente desde el componente que falta, sin reiniciar toda la transferencia.

**Responsabilidad:** recuperar trabajos e intenciones durables y continuar el paso que corresponda según la
evidencia disponible.

**Recorrido empresarial:**

1. Identificar trabajos recuperables y recuperar su transferencia o devolución propietaria.
2. Comprobar la identidad, el contenido original, el avance y las actuaciones ya confirmadas.
3. Identificar el componente cuyo resultado falta y su autoridad responsable.
4. Si el efecto remoto es incierto, consultar su actuación original antes de decidir la continuación.
5. Si existe un resultado firme, continuar solo el componente empresarial dependiente de ese hecho.
6. Conservar versiones y evitar que dos trabajadores ejecuten incompatiblemente el mismo paso.
7. Guardar de forma coherente avance, seguimiento, historia y comunicación pendiente.

**Ejemplo:** el beneficiario externo ya recibió el crédito, pero el consumo de reserva en Fondos está incierto.
Se recupera **el consumo original**; no se vuelve a enviar la transferencia externa.

**Dónde aplicar capacidades técnicas:** trabajos persistentes, control concurrente de trabajadores,
consultas de recuperación, métricas de antigüedad y trazas correlacionadas con la actuación original.

### 12.4. `TransferChanged` — Comunicar cambios confirmados

**Tipo:** comunicación de hechos del servicio, asociada al contrato CI-05-038.

**Problema empresarial:** otros servicios necesitan conocer cambios de una transferencia sin acceder a su base.
Publicar un evento no debe producir otro movimiento monetario ni perder un hecho por una caída posterior al cambio.

El evento comunica identidad, versión, modalidad, tipo de cambio, situación general, principal, moneda,
hechos confirmados, referencias económicas y componentes pendientes. Agrega referencias de destino,
intercambio o devolución cuando corresponden.

| Capacidad            | Necesidad empresarial                                                 |
|----------------------|-----------------------------------------------------------------------|
| Outbox               | Conservar evento pendiente en la misma confirmación del cambio local. |
| Clave de agregado    | Permitir ordenar versiones de una misma transferencia.                |
| Evento repetible     | Reenviar después de fallo de entrega sin crear otro efecto bancario.  |
| Inbox del consumidor | Reconocer duplicados, versiones y discontinuidades.                   |
| Observabilidad       | Detectar atraso de publicación y mensajes sin entrega comprobada.     |

No existe obligación de que Kafka entregue globalmente en orden todos los mensajes de todos los agregados.
Los consumidores deben comprobar las versiones de la transferencia y recuperar información faltante cuando afecte
una decisión posterior.

## 13. Dónde ocurren las transacciones y por qué

La expresión **transacción local** significa que Transferencias confirma sus propios datos juntos.
No significa que Fondos, Compensación y Transferencias compartan una única transacción de base de datos.

| Momento del negocio        | Qué debe confirmarse de forma coherente                           |
|----------------------------|-------------------------------------------------------------------|
| Recibir comando nuevo      | Identidad, contenido, actuación e idempotencia.                   |
| Preparar efecto remoto     | Intención, referencia remota, efecto y seguimiento.               |
| Incorporar resultado       | Cambio de transferencia, evidencia, actuación, historia y Outbox. |
| Cancelar antes de despacho | Barrera y decisión ordenadas frente a la habilitación del envío.  |
| Comprometer devolución     | Devolución y cambio atómico del remanente económico.              |
| Confirmar devolución       | Resultado y traslado de pendiente a devuelto.                     |
| Recibir evento externo     | Inbox, hecho deduplicado, cambio local e historia.                |
| Reanudar trabajo           | Avance recuperado y siguiente seguimiento persistente.            |

**Regla de frontera:** no mantener una transacción SQL local abierta mientras se espera una llamada remota.
Primero se registra la intención, luego se comunica con la autoridad y finalmente se registra el resultado.

Si una llamada falla después de que la autoridad ejecutó, la intención persistida permite investigar el
resultado original. Sin esa intención, el sistema podría iniciar por error una actuación económica diferente.

### 13.1. Orden de protección concurrente

Cuando una operación necesita varias decisiones locales compatibles, el orden de protección es:

1. Transferencia original.
2. Control de devoluciones, cuando se modifica el remanente.
3. Cancelación o devolución involucrada.
4. Ejecución y pasos remotos afectados.

El propósito es evitar decisiones que se contradigan por carreras, especialmente:

- Cancelar mientras otra tarea habilita el despacho.
- Comprometer dos devoluciones sobre el mismo principal disponible.
- Recuperar una actuación mientras llega su confirmación tardía.

**Advertencia importante:** validar un importe o un booleano en memoria no reemplaza bloqueos,
versiones y restricciones de unicidad en una base transaccional real.

## 14. Integraciones que necesita comprender cada caso de uso

No es necesario ejecutar toda una plataforma bancaria para estudiar estas operaciones.
Cada autoridad que intervenga en el recorrido debe estar accesible mediante un contrato equivalente,
real o simulado, sin que Transferencias adopte su lógica empresarial.

| Autoridad | Solicitud | Casos de uso |
|---|---|---|
| Cuentas | Existencia, condiciones, capacidades y facultades. | Interna, saliente, entrante. |
| Identificación bancaria | Resolver identificadores para aceptar el destino. | Interna, saliente. |
| Representación | Comprobar facultades del actor y contenido. | Interna, saliente, cancelación. |
| Riesgo y cumplimiento | Comprobar controles y restricciones. | Interna, saliente, entrante. |
| Condiciones comerciales | Determinar cargos y condiciones. | Interna, saliente. |
| Fondos | Reservar, ejecutar, consumir, liberar y recuperar. | Interna, saliente, entrante, retorno. |
| Compensación | Enviar, consultar, cancelar e informar resultados. | Saliente, entrante, cancelación, retorno. |
| Cambio de moneda | Proveer conversión autorizada. | Transferencias entre monedas. |
| Otras autoridades | Aportar permisos, evidencia o decisión requerida. | Según finalidad y modalidad. |

### 14.1. Qué debe poder hacer un simulador contractual

Un simulador de Fondos debe reconocer actuaciones y efectos por su identidad, conservar resultados y permitir
consultarlos aun cuando la primera respuesta se haya perdido. Debe ser dueño de sus reservas y movimientos.

Un simulador externo debe distinguir recepción de mensaje, envío, aceptación irrevocable, acreditación y
resultado definitivo. No puede responder `BENEFICIARY_CREDITED` solo porque registró un acuse de comunicación.

Un simulador de Cuentas debe expresar las capacidades y restricciones relevantes para el acto concreto.
Consultar una cuenta no puede convertirse silenciosamente en una reserva monetaria.

Un simulador de Identificación bancaria debe permitir obtener un destino concreto que después quede fijado
como contenido aceptado de la transferencia; no debe redirigir una instrucción antigua al cambiar un alias.

**Garantía común:** una repetición de una actuación remota con la misma identidad y contenido debe recuperar
su resultado original. Una repetición incompatible debe producir conflicto, no otro efecto económico.

### 14.2. Qué no se traslada a Transferencias

- Calcular y modificar saldos de cuentas.
- Inventar la disponibilidad que corresponde determinar a Fondos.
- Resolver alias mediante lógica interna que sustituya Identificación bancaria.
- Inventar facultades de representación o restricciones de otras autoridades.
- Simular una acreditación externa mediante un booleano local sin evidencia.
- Ejecutar liquidaciones entre participantes o asientos contables.
- Aplicar o cancelar deudas comerciales que pertenecen a Pagos o al acreedor.

## 15. Observabilidad: qué problema bancario permite detectar

La observabilidad no sustituye una regla de negocio. Permite saber si el circuito está cumpliendo sus compromisos
y dónde se concentra un trabajo que aún necesita atención o recuperación.

| Señal observada                       | Riesgo empresarial que ayuda a descubrir                               |
|---------------------------------------|------------------------------------------------------------------------|
| Transferencias enviadas sin resultado | Instrucciones externas que podrían ejecutarse sin constancia recibida. |
| Reservas protegidas sin acreditación  | Recursos comprometidos cuyo resultado externo falta.                   |
| Débitos o consumos sin confirmación   | Efectos de Fondos que requieren recuperación.                          |
| Entrantes firmes sin crédito          | Obligaciones aceptadas que todavía no se acreditaron.                  |
| Cancelaciones pendientes              | Instrucciones cuyo despacho o ausencia de ejecución no se comprobó.    |
| Devoluciones inciertas                | Principal comprometido cuyo efecto aún se desconoce.                   |
| Trabajos de recuperación atrasados    | Riesgo de dejar actuaciones sin investigación.                         |
| Eventos pendientes de Outbox          | Hechos locales confirmados que otros servicios aún no recibieron.      |
| Mensajes duplicados o contradictorios | Riesgo de aplicar dos veces un hecho o aceptar evidencia incompatible. |

### 15.1. Logs

Registrar el resultado de cada decisión importante: admisión, reserva, despacho, aceptación, acreditación,
cancelación, devolución, recuperación y rechazo definitivo.

Usar referencias técnicas de actuación y operación para correlacionar los mensajes.
No registrar credenciales, datos sensibles, cuentas completas ni contenido bancario innecesario.

### 15.2. Métricas

Medir cantidad y duración de actuaciones, incertidumbres por componente, tiempo desde despacho sin resultado,
trabajos de recuperación y cambios pendientes de entrega.

No usar `transferId`, `accountId`, nombres de personas ni referencias externas como etiquetas de métricas.
Sus valores tienen cardinalidad elevada y pueden exponer información sensible.

### 15.3. Trazas distribuidas

Correlacionar el recorrido completo cuando una solicitud pasa por Transferencias, Cuentas, Fondos y Compensación.
Una traza permite localizar dónde se perdió una respuesta; **no** constituye evidencia de que se movió dinero.
El resultado de negocio debe proceder del servicio responsable y de sus referencias recuperables.

## 16. Idempotencia, fallos y recuperación: preguntas que siempre hay que contestar

| Pregunta                                         | Respuesta exigida                                             |
|--------------------------------------------------|---------------------------------------------------------------|
| ¿Qué pasa si recibo dos veces el mismo comando?  | Recuperar la actuación original si el contenido coincide.     |
| ¿Y si llega la misma identidad con otro importe? | Informar conflicto sin ejecutar otro efecto.                  |
| ¿Qué pasa si el proveedor no responde?           | Conservar incertidumbre y consultar la actuación original.    |
| ¿Se puede reintentar con otro `effectId`?        | No para recuperar el mismo efecto económico.                  |
| ¿Un acuse demuestra que el beneficiario cobró?   | No. Hace falta evidencia de acreditación.                     |
| ¿Cuándo libero una reserva?                      | Cuando la autoridad comprueba que no queda ejecución posible. |
| ¿Qué ocurre si una devolución queda incierta?    | Su importe continúa comprometido.                             |
| ¿Qué pasa si la aplicación se reinicia?          | Reanudar desde la intención y el componente persistidos.      |
| ¿Qué ocurre con un mensaje repetido?             | Reconocerlo sin duplicar hechos ni efectos.                   |
| ¿Un evento antiguo reemplaza otro posterior?     | No; conservar versiones y recuperar información faltante.     |
| ¿Qué ocurre si falla la publicación del evento?  | Reintentar la entrega del evento persistido en Outbox.        |
| ¿Un error de liquidación revierte un crédito?    | No; la liquidación externa y el crédito son hechos distintos. |

### 16.1. Rechazo definitivo frente a incertidumbre

**Rechazo definitivo:** existe evidencia suficiente de que el efecto no se ejecutó ni puede ejecutarse después.
Puede permitir cerrar una actuación o tratar compromisos pendientes conforme a la autoridad responsable.

**Incertidumbre:** todavía puede existir un efecto realizado o pendiente. No permite afirmar éxito, rechazo,
cancelación ni disponibilidad monetaria por simple deducción.

Esta diferencia cambia qué datos se conservan, qué acciones pueden ejecutarse y qué pruebas son necesarias.

### 16.2. Respuestas a solicitudes persistentes

| Respuesta técnica | Significado de uso                                                         |
|-------------------|----------------------------------------------------------------------------|
| `200`             | Resultado consultado, actualizado o recuperado de una actuación existente. |
| `201`             | Se creó un recurso y el resultado indicado corresponde a esa creación.     |
| `202`             | La actuación quedó aceptada duraderamente y requiere seguimiento.          |
| `409`             | Identidad o versión incompatible, cuando el contrato así lo identifica.    |
| `422`             | Impedimento de negocio sin ejecución admitida.                             |
| `503`             | Dependencia necesaria indisponible antes de aceptar el trabajo.            |

Los códigos HTTP no demuestran por sí mismos los efectos monetarios.
La respuesta debe incluir identidad, resultados y componentes pendientes de acuerdo con el contrato.

## 17. Escenarios empresariales para comprobar los casos de uso

Los escenarios describen resultados observables que la implementación debe demostrar.
Pasar una compilación no demuestra por sí mismo ninguna de estas garantías empresariales.

### 17.1. `ExecuteInternalTransfer`

| Caso   | Situación                                   | Resultado esperado                        |
|--------|---------------------------------------------|-------------------------------------------|
| INT-01 | Principal igual a cero.                     | No se admite la instrucción.              |
| INT-02 | Principal negativo o fuera de escala.       | No se redondea ni se ejecuta.             |
| INT-03 | Cuenta origen igual a destino.              | No se solicita el conjunto a Fondos.      |
| INT-04 | Ordenante sin facultades.                   | No se inicia efecto monetario.            |
| INT-05 | Saldo insuficiente confirmado.              | Rechazo sin traslado parcial.             |
| INT-06 | Débito y crédito confirmados conjuntamente. | Un conjunto y dos movimientos vinculados. |
| INT-07 | Fondos ejecuta y se pierde la respuesta.    | Recuperar el conjunto original.           |
| INT-08 | Se repite la identidad y el contenido.      | Recuperar resultado; sin segundo efecto.  |
| INT-09 | Misma identidad con distinto importe.       | Conflicto; sin segundo efecto.            |
| INT-10 | El alias cambia después de aceptar.         | Conservar destino originalmente aceptado. |
| INT-11 | Monedas incompatibles sin conversión.       | No convertir implícitamente.              |
| INT-12 | Reserva vinculada al conjunto.              | Consumir según contrato, sin otro débito. |
| INT-13 | Se solicita descubierto sin habilitación expresa compatible. | No solicitar ejecución con descubierto. |
| INT-14 | Cargo por ejecución antes de confirmar el hecho generador. | No registrar el cargo como cobrado; no duplicar cargos originados por Pagos. |

### 17.2. `SendOutgoingTransfer`

| Caso   | Situación                               | Resultado esperado                       |
|--------|-----------------------------------------|------------------------------------------|
| OUT-01 | Mecanismo no habilitado.                | No despachar.                            |
| OUT-02 | Fondos rechaza constituir reserva.      | No despachar.                            |
| OUT-03 | La reserva queda incierta.              | Recuperar antes de continuar el envío.   |
| OUT-04 | Reserva confirmada, no hay intercambio. | No informar despacho.                    |
| OUT-05 | Cancelación confirmada antes de envío.  | Prohibir despacho tardío.                |
| OUT-06 | Se registra un acuse técnico.           | No informar acreditación.                |
| OUT-07 | Se confirma aceptación irrevocable.     | Mantener cobertura firme.                |
| OUT-08 | Se acredita el beneficiario.            | Coordinar consumo de reserva original.   |
| OUT-09 | Se pierde la confirmación del consumo.  | Recuperar consumo, no debitar otra vez.  |
| OUT-10 | Envío incierto sin respuesta externa.   | Conservar cobertura y seguimiento.       |
| OUT-11 | Rechazo externo definitivo sin efecto.  | Tratar reserva con evidencia suficiente. |
| OUT-12 | Falla liquidación después del crédito.  | Conservar acreditación confirmada.       |
| OUT-13 | No existe habilitación expresa compatible para descubierto. | No usarlo para constituir cobertura ni despachar. |
| OUT-14 | Pagos originó una transferencia con cargo de servicio. | Determinar la causa una vez y evitar doble cobro entre coordinadores. |
| OUT-15 | Acreditación confirmada todavía no incorporada a Compensación. | Informar el hecho con identidad idempotente; una repetición no duplica la incorporación. |

### 17.3. `ReceiveIncomingTransfer`

| Caso  | Situación                                           | Resultado esperado                       |
|-------|-----------------------------------------------------|------------------------------------------|
| IN-01 | Emisor no autorizado para el mecanismo.             | No admitir la instrucción.               |
| IN-02 | Cuenta de destino inexistente.                      | Rechazo previo a aceptación irrevocable. |
| IN-03 | Restricción solo sobre débitos.                     | Evaluar crédito según alcance real.      |
| IN-04 | Recepción repetida con igual identidad y contenido. | No acreditar dos veces.                  |
| IN-05 | Identidad externa igual con contenido distinto.     | Conflicto, sin otro crédito.             |
| IN-06 | Mismo identificador en otro participante.           | Distinguir por la clave compuesta.       |
| IN-07 | Cambia versión del mecanismo.                       | No crear otra identidad externa.         |
| IN-08 | Se acepta irrevocablemente y Fondos rechaza definitivamente el crédito. | Conservar obligación y tramitar resolución o devolución procedente; no tratarlo como timeout. |
| IN-09 | Fondos acredita, pero se pierde su respuesta.       | Recuperar `creditEffectId` original.     |
| IN-10 | Mensaje externo duplicado.                          | Un único registro económico.             |
| IN-11 | Tras aceptación firme se comprueba imposibilidad definitiva de acreditar. | Resolver mediante el procedimiento aplicable y conservar vínculo y evidencia; no dejarlo como mera incertidumbre. |

### 17.4. `CancelTransfer`

| Caso   | Situación                                     | Resultado esperado                        |
|--------|-----------------------------------------------|-------------------------------------------|
| CAN-01 | Cancelación confirmada antes del intercambio. | Impedir toda habilitación posterior.      |
| CAN-02 | Cancelación concurrente con despacho.         | Decisiones ordenadas y compatibles.       |
| CAN-03 | Intercambio ya enviado.                       | Pedir comprobación al mecanismo.          |
| CAN-04 | El mecanismo no responde.                     | No declarar cancelación definitiva.       |
| CAN-05 | Solo existe `NOT_RECEIVED`.                   | No presumir que no habrá envío futuro.    |
| CAN-06 | Existe crédito confirmado.                    | Informar `ALREADY_CREDITED`.              |
| CAN-07 | Se comprueba ausencia de ejecución pendiente. | Confirmar cancelación y tratar cobertura. |
| CAN-08 | Se repite petición ya resuelta.               | Recuperar el resultado original.          |

### 17.5. `ReturnTransfer`

| Caso   | Situación                                      | Resultado esperado                  |
|--------|------------------------------------------------|-------------------------------------|
| RET-01 | Importe cero, negativo o fuera de escala.      | No admitir nueva devolución.        |
| RET-02 | Principal 100, devuelto 40, pendiente 20.      | Remanente disponible 40.            |
| RET-03 | Se solicitan 50 con remanente 40.              | Rechazar exceso de importe.         |
| RET-04 | Dos devoluciones de 30 con remanente 40.       | No comprometer ambas.               |
| RET-05 | Se repite el mismo `returnEffectId`.           | No crear otro retorno.              |
| RET-06 | Efecto de devolución incierto.                 | Mantener el importe comprometido.   |
| RET-07 | Se confirma la devolución.                     | Pasar comprometido a devuelto.      |
| RET-08 | Rechazo definitivo sin ejecución.              | Liberar compromiso acreditado.      |
| RET-09 | El ordenante no puede debitar al beneficiario. | No ejecutar ese débito.             |
| RET-10 | Se devuelve principal.                         | No revertir cargos automáticamente. |
| RET-11 | Compensación informa una devolución externa ya recibida. | Vincularla por identidad externa completa; deduplicar y no solicitar ni aplicar dos veces el efecto. |

### 17.6. Consultas y continuaciones internas

| Caso   | Situación                                            | Resultado esperado                               |
|--------|------------------------------------------------------|--------------------------------------------------|
| QRY-01 | Se consulta una transferencia con envío sin crédito. | Mostrar hechos y pendiente correctos.            |
| QRY-02 | Se consulta una devolución parcial.                  | Conservar original, devuelto, pendiente y resto. |
| QRY-03 | Se consulta una actuación tras respuesta perdida.    | Recuperar actuación sin ejecutar otra vez.       |
| QRY-04 | Actor no autorizado conoce un `transferId`.          | No revelar información protegida.                |
| QRY-05 | Búsqueda por referencia externa aislada.             | Rechazar búsqueda ambigua.                       |
| QRY-06 | Consulta sin efectos económicos.                     | No crear reservas ni mensajes de envío.          |
| REC-01 | Reinicio con actuación monetaria incierta.           | Consultar identidad remota original.             |
| REC-02 | Respuesta tardía de efecto confirmado.               | Incorporar al paso original.                     |
| REC-03 | Evento externo repetido.                             | Deduplicar sin repetir efecto.                   |
| REC-04 | Evento antiguo llega después de uno reciente.        | No sobrescribir la versión posterior.            |
| REC-05 | Falta una versión relevante de origen.               | Recuperar información antes de decidir.          |
| REC-06 | Outbox quedó pendiente después de commit.            | Entregar el mismo evento sin otro movimiento.    |
| REC-07 | Crédito externo confirmado, consumo incierto.        | Recuperar solo el consumo original.              |
| REC-08 | Dos trabajadores toman el mismo seguimiento.         | No ejecutar actuaciones incompatibles.           |

### 17.7. Qué demuestra cada tipo de prueba

| Prueba | Qué comprueba | Qué no demuestra por sí sola |
|---|---|---|
| Validación de datos | Formatos, obligatoriedad y condiciones locales. | Integridad concurrente. |
| Caso de uso | Secuencia de decisiones y llamadas. | Durabilidad de PostgreSQL. |
| Integración | Contratos, respuestas y recuperación. | Todos los fallos posibles. |
| Transaccional | Atomicidad y restricciones persistentes. | Ausencia de duplicados remotos. |
| Concurrencia | Carreras entre despacho, cancelación y devoluciones. | Resistencia a reinicios. |
| Recuperación | Continuar desde la intención original. | Permisos de todos los actores. |
| Observabilidad | Visibilidad de hechos y pendientes. | Autoridad de los efectos. |

## 18. Recorridos completos para estudiar la lógica empresarial

### 18.1. Una transferencia interna sin fallos

**Situación:** se solicitan 30 desde una cuenta con 100 hacia otra con 20, sin cargos adicionales.

1. Se verifica quién ordena, qué cuenta desea debitar y cuál es el destino aceptado.
2. Se comprueba que son cuentas diferentes y que existen facultades para ejecutar el acto.
3. Se registra la instrucción y la identidad del conjunto monetario a ejecutar.
4. Fondos determina la ejecución y confirma un débito de 30 y un crédito de 30 como conjunto.
5. Transferencias conserva ambas referencias y el hecho comprobado.
6. El resultado muestra la transferencia y sus efectos. Los saldos siguen siendo autoridad de Fondos.

**Lo que aprendés:** responsabilidad distribuida, contrato monetario indivisible y evidencia de resultado.

### 18.2. Una transferencia saliente con acuse, pero sin acreditación

**Situación:** el banco debe enviar 70 a otro participante. Fondos constituye cobertura suficiente.

1. Transferencias fija el destino externo y conserva la autorización.
2. Se registra la reserva y se protege la cobertura antes de enviar.
3. Compensación confirma que el mecanismo recibió el mensaje.
4. No se obtuvo todavía aceptación irrevocable ni prueba de crédito al beneficiario.
5. Transferencias conserva la referencia de intercambio y el seguimiento del resultado.
6. No se consume la reserva por el simple acuse, ni se informa acreditación ficticia.

**Lo que aprendés:** distinguir transporte de dinero, cobertura firme o incierta y evidencias externas.

### 18.3. Una transferencia entrante duplicada

**Situación:** un participante remite dos veces la misma instrucción de 80 por un problema de red.

1. Se reconoce participante, mecanismo y referencia externa de negocio.
2. La segunda recepción coincide con la identidad y el contenido de la primera.
3. Transferencias recupera la operación original y su resultado.
4. Si Fondos ya acreditó 80, no se solicita otro crédito de 80.
5. Si falta confirmación, se consulta el `creditEffectId` original.

**Lo que aprendés:** deduplicación entre servicios, identidad de negocio y recuperación sin efecto doble.

### 18.4. Una cancelación compite con un despacho

**Situación:** el cliente solicita cancelar mientras una tarea intenta habilitar el envío externo.

1. Las dos actuaciones se refieren a la misma transferencia.
2. Deben ordenarse mediante una decisión local protegida y persistente.
3. Si la cancelación se confirma primero, `dispatchBarrier` impide el despacho tardío.
4. Si el intercambio ya existe o el envío puede producirse, se consulta al mecanismo.
5. La petición sola no autoriza liberar cobertura ni afirmar que no habrá ejecución.

**Lo que aprendés:** concurrencia de decisiones incompatibles y diferencia entre intención y resultado.

### 18.5. Dos devoluciones compiten por el mismo principal

**Situación:** el principal original es 100. Ya se devolvieron 40 y hay 20 comprometidos.

1. El remanente disponible es 40.
2. Llegan dos solicitudes concurrentes de 30 cada una.
3. El banco no puede admitir ambas porque comprometerían 60 sobre un remanente de 40.
4. Una decisión transaccional debe impedir el exceso, incluso con solicitudes simultáneas.
5. Si la devolución admitida queda incierta, conserva el importe comprometido.

**Lo que aprendés:** invariantes aritméticas y concurrencia real sobre recursos económicos.

### 18.6. El servicio se reinicia después de una actuación remota

**Situación:** Transferencias pidió un crédito identificado a Fondos y se detuvo antes de recibir respuesta.

1. La intención y el identificador remoto ya estaban persistidos.
2. Al reiniciar, se identifica el trabajo que requiere recuperación.
3. Se consulta a Fondos por la actuación original; no se origina otro crédito.
4. El resultado recuperado se incorpora al cambio local y su historia.
5. La comunicación del hecho se entrega mediante el evento pendiente, sin ejecutar dinero otra vez.

**Lo que aprendés:** durabilidad, recuperación, Outbox y comportamiento empresarial ante fallos reales.

## 19. Glosario de términos usados en el código

Los nombres ingleses son la referencia principal del negocio y permiten reconocer clases, comandos y operaciones.
La traducción no sustituye el identificador ni crea un segundo nombre técnico.

| Término inglés | Significado en español | Uso en Transferencias                                   |
|----------------|------------------------|---------------------------------------------------------|
| `Transfer`     | Transferencia          | Instrucción bancaria y su recorrido.                    |
| `Execute`      | Ejecutar               | Solicitar el efecto previsto de una operación.          |
| `Internal`     | Interna                | Movimiento entre cuentas del banco.                     |
| `Outgoing`     | Saliente               | Instrucción enviada hacia otro participante.            |
| `Incoming`     | Entrante               | Instrucción recibida desde otro participante.           |
| `Receive`      | Recibir                | Incorporar una instrucción o información entrante.      |
| `Send`         | Enviar                 | Coordinar la salida por el mecanismo externo.           |
| `Cancel`       | Cancelar               | Impedir una ejecución todavía cancelable.               |
| `Return`       | Devolver               | Efecto posterior que retorna principal.                 |
| `Get`          | Consultar u obtener    | Recuperar información sin ejecutarla otra vez.          |
| `Search`       | Buscar                 | Localizar operaciones por identidad y alcance.          |
| `Action`       | Actuación              | Petición identificada y recuperable.                    |
| `Effect`       | Efecto                 | Consecuencia económica que no debe duplicarse.          |
| `Reference`    | Referencia             | Identidad de objeto, actuación o intercambio.           |
| `Destination`  | Destino                | Cuenta o participante al que se dirige el dinero.       |
| `Accepted`     | Aceptado               | Contenido aprobado o compromiso firme, según contexto.  |
| `Dispatch`     | Despacho o envío       | Salida al mecanismo externo.                            |
| `Coverage`     | Cobertura              | Recursos protegidos para cumplir un compromiso.         |
| `Reservation`  | Reserva                | Compromiso monetario administrado por Fondos.           |
| `Debit`        | Débito                 | Movimiento que reduce recursos de una cuenta.           |
| `Credit`       | Crédito                | Movimiento que acredita recursos a una cuenta.          |
| `Principal`    | Principal              | Importe de dinero trasladado, distinto de cargos.       |
| `Fee`          | Comisión               | Cargo por un hecho o condición comercial.               |
| `Tax`          | Tributo                | Importe tributario identificado por su causa.           |
| `Outcome`      | Resultado              | Conclusión de una actuación, según su alcance.          |
| `Confirmed`    | Confirmado             | Hecho respaldado por evidencia suficiente.              |
| `Pending`      | Pendiente              | Componente que necesita una resolución.                 |
| `Uncertain`    | Incierto               | No se conoce el resultado definitivo.                   |
| `Recover`      | Recuperar              | Consultar y completar la actuación original.            |
| `Resume`       | Reanudar               | Continuar trabajo persistido desde el paso necesario.   |
| `Incorporate`  | Incorporar             | Registrar un hecho comunicado por su autoridad.         |
| `Idempotency`  | Idempotencia           | Repetir una petición sin duplicar su efecto.            |
| `Concurrency`  | Concurrencia           | Actuaciones simultáneas sobre información compartida.   |
| `Evidence`     | Evidencia              | Respaldo verificable de una decisión o un hecho.        |
| `Fact`         | Hecho                  | Suceso confirmado, con fuente y momento identificables. |
| `Outbox`       | Salida transaccional   | Conserva eventos a publicar junto al cambio local.      |
| `Inbox`        | Entrada deduplicada    | Conserva mensajes recibidos y su aplicación.            |
| `Port`         | Puerto                 | Contrato por el que se solicita una capacidad externa.  |

## 20. Cómo estudiar y comprobar un caso de uso

Al abordar cualquiera de los casos de uso, la pregunta inicial es **qué necesita garantizar el banco**,
no qué controlador, clase o dependencia conviene escribir primero.

Para comprender un caso completo, deberías poder responder:

1. ¿Qué problema empresarial concreto resuelve y quién puede solicitarlo?
2. ¿Qué datos necesita y qué significan en el negocio?
3. ¿Qué validaciones son propias de Transferencias y qué decisiones pertenecen a otra autoridad?
4. ¿Qué hechos deben comprobarse antes de continuar al siguiente paso?
5. ¿Qué información debe conservarse antes de solicitar un efecto externo?
6. ¿Qué se confirma dentro de una transacción local y qué se ejecuta fuera de ella?
7. ¿Qué identidades evitan crear dos efectos por una misma actuación?
8. ¿Qué puede pasar si llegan dos actuaciones incompatibles al mismo tiempo?
9. ¿Qué se hace si se pierde una respuesta después de que el proveedor pudo haber ejecutado?
10. ¿Qué métricas, logs y trazas permitirían reconocer el problema sin revelar datos sensibles?
11. ¿Qué debe devolver una consulta o actuación para mostrar lo comprobado y lo pendiente?
12. ¿Qué pruebas demostrarían que el resultado cumple las obligaciones del negocio?

El aprendizaje técnico exigido por Transferencias incluye orquestación de operaciones, integración HTTP,
contratos, idempotencia, consistencia distribuida, Outbox, Inbox, recuperación, trazabilidad y resultados inciertos.

Estas capacidades no son adornos tecnológicos: cada una responde a riesgos bancarios concretos descritos aquí.
La implementación de un caso de uso es correcta en términos de negocio cuando conserva sus responsabilidades,
respeta los límites de las autoridades y demuestra sus garantías en los escenarios correspondientes.
