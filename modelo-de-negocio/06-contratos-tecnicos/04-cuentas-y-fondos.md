# **Contratos técnicos — Módulo 4. Cuentas y fondos**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**            | **Responsabilidad**                                                                       |
|-------------------------|-------------------------------------------------------------------------------------------|
| Cuentas                 | Constitución, participantes, capacidades, condiciones, liquidaciones y cierre de cuentas. |
| Identificación bancaria | Asignaciones de identificación bancaria, alias y resolución de destinos.                  |
| Fondos                  | Posiciones monetarias, reservas, movimientos, descubierto y afectaciones.                 |
| Operaciones de efectivo | Recepción y entrega física de efectivo, diferencias y coordinación monetaria.             |

Comprende los contratos **CI-04-001 a CI-04-041**.

Fondos conserva la autoridad sobre el dinero registrado en cuentas. Cuentas conserva condiciones y permisos específicos.
Operaciones de efectivo conserva el hecho físico.

Las existencias propias del banco pertenecen a Posiciones y liquidez, dentro de Tesorería.

Una reserva no es un débito. Una inmovilización no es un débito. Una entrega física no se presume a partir de una
instrucción monetaria.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las comprobaciones informativas mediante POST no requieren clave de idempotencia ni crean preparaciones.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del servicio.

La consulta recupera el resultado por una identidad conocida antes del envío. Las consultas particulares conservan el
detalle de cada capacidad.

### **2.2. Tipos**

| **Tipo**          | **Representación**                                                     |
|-------------------|------------------------------------------------------------------------|
| UUID              | Identificador interno.                                                 |
| Version           | Entero positivo dentro del rango de long.                              |
| Sequence          | Entero positivo dentro del rango de long.                              |
| Code              | Cadena de 1 a 80 caracteres.                                           |
| Instant           | Instante UTC.                                                          |
| Date              | Fecha YYYY-MM-DD.                                                      |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                  |
| Currency          | ARS o USD.                                                             |
| MoneyAmount       | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| SignedMoneyAmount | Cadena decimal con signo, precisión máxima 24 y hasta dos decimales.   |
| CalculationValue  | Cadena decimal con signo, precisión 30 y escala máxima 12.             |
| Rate              | Cadena decimal, precisión 24 y escala máxima 12.                       |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |

El sentido de un movimiento se informa separadamente del importe.

Los importes ejecutables deben ser positivos, salvo acumulados, límites y remanentes que admiten cero.

Los valores fuera de escala se rechazan sin redondeo silencioso.

### **2.3. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige expresamente contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. MonetaryConcept**

Contiene:

- conceptCode: Code.

- componentType: PRINCIPAL, INTEREST, FEE, TAX o ADJUSTMENT.

- amount: MoneyAmount.

- currency: Currency.

- calculationReference: ObjectReference, cuando deriva de cálculo especializado.

La suma de componentes coincide con el importe que descomponen.

El consumidor traduce su clasificación de origen a estas categorías sin perder el concepto ni la referencia
especializada.

### **3.2. BusinessBasis**

Contiene:

- basisType: Code registrado para el circuito.

- ownerService: identificador del propietario de negocio.

- sourceReference: ObjectReference.

- evidenceReferences: ObjectReference\[\].

- contentHash: Hash, cuando vincula contenido exacto.

El proveedor comprueba que el fundamento habilite ese efecto y consumidor.

Un código libre o una referencia aportada sin comprobación no sustituyen la autoridad requerida.

### **3.3. PendingComponent**

Contiene componentCode, responsibleService, reasonCode y referencia cuando existe.

### **3.4. Resultado y consultas**

Los comandos utilizan el resultado común y agregan, cuando corresponden:

- executionAttemptId.

- Referencias de movimientos.

- Recursos comprometidos.

- Secuencias de posición.

- Resultados por componente.

Las consultas identifican momento, corte, completitud y datos faltantes.

Los movimientos paginados utilizan cutId y cursor. No mezclan cortes diferentes.

## **4. Servicio Cuentas**

### **4.1. Account**

| **Campo**                  | **Tipo**                   | **Condición**                        |
|----------------------------|----------------------------|--------------------------------------|
| accountId y version        | UUID y Version             | Generados.                           |
| constitutionId             | UUID                       | Obligatorio.                         |
| productReference           | VersionedReference         | Obligatorio.                         |
| contractReference          | VersionedReference         | Obligatorio.                         |
| accountType                | SAVINGS, PAYROLL o CURRENT | Obligatorio.                         |
| currency                   | Currency                   | Obligatorio e inmutable.             |
| participants               | AccountParticipant\[\]     | Al menos un titular.                 |
| capabilityCodes            | Code\[\]                   | Obligatorio.                         |
| economicTermsReference     | ObjectReference            | Obligatorio.                         |
| overdraftFacilityReference | ObjectReference            | Cuando existe financiación aplicada. |
| status                     | Estado                     | Generado.                            |
| openedAt                   | Instant                    | Después de constitución.             |
| operationalFrom            | Instant                    | Después de habilitación.             |
| closureReference           | ObjectReference            | Cuando existe cierre.                |

Los estados son CONSTITUTING, CONSTITUTED, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

SAVINGS y PAYROLL no admiten descubierto. CURRENT requiere financiación contractual aplicada para utilizarlo.

### **4.2. AccountParticipant**

Contiene:

- partyId: UUID.

- role: HOLDER o AUTHORIZED_OPERATOR.

- permittedActs: Code\[\].

- amountLimits: límites por moneda.

- actingMode: INDIVIDUAL o JOINT.

- validity: inicio y final opcional.

- basisReference: ObjectReference.

- jointRule: obligatorio para actuación conjunta.

Cada límite contiene moneda e importe máximo, sin repetir moneda.

La regla conjunta identifica participantes o grupos admitidos, cantidad mínima de actores distintos y actos alcanzados.

Un autorizado no adquiere titularidad ni amplía su representación general.

### **4.3. Interfaces**

| **Contrato** | **operationId**           | **Método y ruta**                                      |
|--------------|---------------------------|--------------------------------------------------------|
| CI-04-001    | constituteAccount         | POST /v1/accounts                                      |
| CI-04-002    | getAccount                | GET /v1/accounts/{accountId}                           |
| CI-04-003    | changeAccountParticipants | POST /v1/accounts/{accountId}/participant-changes      |
| CI-04-004    | changeAccountOperations   | POST /v1/accounts/{accountId}/operational-changes      |
| CI-04-005    | applyAccountRestriction   | POST /v1/accounts/{accountId}/restriction-applications |
| CI-04-006    | getAccountAction          | GET /v1/account-actions/{actionId}                     |
| CI-04-007    | requestAccountClosure     | POST /v1/accounts/{accountId}/closures                 |
| CI-04-040    | checkAccountOperation     | POST /v1/accounts/{accountId}/operation-checks         |
| CI-04-041    | revalidateAccountOperation | POST /v1/accounts/{accountId}/operation-checks/{checkId}/revalidations |

### **4.4. Constitución y consulta**

Constituir recibe contexto y los campos de Account no generados.

Devuelve cuenta, capacidades confirmadas, posición, identificadores y pendientes.

La posición inicial es cero. La creación no demuestra habilitación completa.

Se utiliza 201 para recurso creado, 200 para resultado recuperado y 202 para actuación durable pendiente.

Consultar recibe finalidad y versión o momento histórico, de forma excluyente.

Devuelve cuenta, permisos específicos y situación de cierre. No presenta disponibilidad como dato propio de Cuentas.

### **4.5. Participantes y capacidades**

Cambiar participantes recibe contexto, fundamento contractual y cambios de modalidad:

- ADD: definición del participante.

- MODIFY: participante existente y nueva definición.

- REVOKE: participante, actos alcanzados y momento de efecto.

- REMOVE: participante y fundamento de finalización.

Cambiar titularidad exige contrato y conformidades aplicables.

Cambiar capacidades recibe contexto, contrato, cambios de capacidad y fecha efectiva. Agrega referencia de condiciones
económicas si cambian.

No permite modificar moneda. El descubierto se aplica mediante su contrato específico.

### **4.6. Restricciones operativas**

Consumidor: Restricciones.

Recibe contexto, medida, componente de aplicación, acción, actos afectados y momento de efecto.

Las acciones son APPLY, MODIFY y LIFT.

Devuelve aplicación, versión, alcance e intentos pendientes.

La restricción operativa no inmoviliza dinero por sí sola.

### **4.7. Cierre**

Recibe contexto, causa y fundamento contractual. Agrega destino del saldo cuando existe.

El destino identifica modalidad, referencia y autorización verificable.

Devuelve:

- closureId: UUID.

- outcome: INITIATED, RESIDUAL o CLOSED.

- Actos nuevos impedidos.

- Inventarios.

- Liquidaciones relacionadas.

- Obligaciones residuales.

- Pendientes.

La falta de respuesta de un propietario no equivale a inventario vacío.

No confirma cierre definitivo con recursos o vínculos sin resolver.

### **4.8. Comprobación informativa**

Recibe actores, acto, objeto destinatario, huella, momento y finalidad.

Agrega representado, importe, moneda y evidencia conjunta cuando corresponden.

Devuelve:

- checkId: UUID.

- result: ADMITTED, PREVENTED o PENDING.

- accountVersion: Version.

- capabilityCodes: Code\[\].

- permissionReferences: ObjectReference\[\].

- missingParticipants: UUID\[\].

- Observaciones autorizadas.

No reserva ni prepara condiciones.

### **4.9. Revalidación de una comprobación**

La ruta recibe accountId y checkId, ambos UUID. La solicitud utiliza autenticación y X-Correlation-Id. El cuerpo es
opcional y admite únicamente jointEvidenceReferences (ObjectReference[]), con referencias a conformidades posteriores
para la instrucción original. La decisión definitiva se recupera por checkId. Esta operación no requiere
Idempotency-Key ni ActionContext.

Devuelve HTTP 200 con:

- checkId: UUID.

- result: ADMITTED o PREVENTED.

- accountVersion: Version de la cuenta evaluada. La versión operativa se utiliza en la confirmación interna.

- evaluatedAt: Instant de la evaluación que produjo la decisión definitiva.

- reasonCode: Code, obligatorio para PREVENTED.

Una solicitud repetida devuelve la decisión conservada con la misma versión, momento y motivo, cuando corresponda.

| **HTTP** | **Condición** |
|----------|---------------|
| 400 | Alguno de los identificadores de la ruta es inválido. |
| 403 | El solicitante autenticado carece de autorización para revalidar la comprobación. |
| 404 | La comprobación no existe para la cuenta indicada. |
| 503 | Una dependencia o información indispensable está indisponible y no se puede concluir la evaluación. |

El error 503 conserva pendiente la comprobación. PREVENTED se devuelve como decisión funcional en una respuesta 200.

## **5. Preparación de Cuentas**

| **operationId**       | **Método y ruta**                                                           |
|-----------------------|-----------------------------------------------------------------------------|
| prepareAccountAction  | POST /v1/account-action-preparations                                        |
| getAccountPreparation | GET /v1/account-action-preparations/{preparationId}                         |
| notifyAccountDecision | POST /v1/account-action-preparations/{preparationId}/decision-notifications |

La entrada utiliza la preparación común y agrega cuenta, acto, actores, objeto destinatario y evidencia conjunta cuando
se exige.

El contenido vincula cuenta, moneda, importe, acto, actores y destino cuando intervienen en la decisión.

La respuesta identifica preparación, versión de cuenta, alcance, controles, estado y consulta.

Los estados son PREPARED, RESOLUTION_PENDING, CONCLUDED_COMMITTED, CONCLUDED_ABORTED y REJECTED.

Cuentas prepara únicamente capacidades y permisos propios.

Los cambios incompatibles y las preparaciones utilizan controles locales comunes.

Una revocación posterior bloquea nuevas preparaciones y solicita resolver los intentos existentes. No invalida
silenciosamente la preparación anterior.

Las preparaciones no se liberan por timeout.

## **6. Servicio Identificación bancaria**

### **6.1. BankIdentifierAssignment**

Contiene:

- assignmentId y version.

- accountId.

- identifierType: CBU.

- identifierValue: cadena de 22 dígitos.

- currency.

- status: ASSIGNED, RETIREMENT_PENDING o RETIRED.

- validFrom: Instant.

- validTo: Instant, cuando existe retiro efectivo.

El identificador cumple sus verificadores estructurales y no se reutiliza para otra cuenta.

### **6.2. Interfaces**

| **Contrato** | **operationId**             | **Método y ruta**                                    |
|--------------|-----------------------------|------------------------------------------------------|
| CI-04-008    | assignBankIdentifier        | POST /v1/bank-identifiers/assignments                |
| CI-04-008    | retireBankIdentifier        | POST /v1/bank-identifiers/{identifierId}/retirements |
| CI-04-009    | changeAccountAlias          | POST /v1/accounts/{accountId}/alias-changes          |
| CI-04-010    | resolveBankIdentifier       | POST /v1/bank-identifier-resolutions                 |
| CI-04-011    | getBankIdentificationAction | GET /v1/bank-identification-actions/{actionId}       |

Asignar recibe contexto, cuenta, moneda, institución y constitución.

Retirar recibe contexto y cierre o fundamento autorizado.

### **6.3. Alias**

Las acciones son ASSIGN, REPLACE y RETIRE.

Reciben contexto y alias solicitado para asignación o sustitución. Los cambios sobre asignación existente identifican su
versión.

El alias normalizado utiliza entre 6 y 20 caracteres: letras ASCII minúsculas, números, punto y guion.

La respuesta contiene asignación, alias, vigencia, referencia del directorio y resultado PENDING, CONFIRMED, REJECTED o
UNCERTAIN.

Una sustitución no elimina la asignación anterior antes de comprobar el resultado correspondiente.

### **6.4. Resolución de destino**

Recibe tipo y valor del identificador, finalidad y alcance de comprobación.

Devuelve:

- resolutionId: UUID.

- result: RESOLVED, NOT_FOUND o UNAVAILABLE.

- resolvedAt: Instant.

- sourceReference: ObjectReference.

- destinationType: INTERNAL o EXTERNAL, cuando resuelve.

- accountReference: para destino interno.

- participantReference: para destino externo.

- currency: cuando está comprobada.

- authorizedDisplayData: datos de presentación expresamente autorizados.

Los datos de presentación contienen únicamente denominación de hasta 250 caracteres e identificador enmascarado de hasta
120 caracteres, cuando pueden divulgarse.

Resolver un identificador no autoriza un movimiento.

## **7. Servicio Fondos: posición y reservas**

### **7.1. FundPosition**

Contiene:

- accountId: UUID.

- currency: Currency.

- positionVersion: Version.

- accountSequence: Sequence, cuando existen movimientos.

- bookedBalance: SignedMoneyAmount.

- ownFundsAvailable: MoneyAmount.

- reservationReferences: ObjectReference\[\].

- immobilizationReferences: ObjectReference\[\].

- conditionalCreditReferences: ObjectReference\[\].

- overdraftLimit: MoneyAmount.

- overdraftPrincipalUsed: MoneyAmount.

- protectedCoverage: MoneyAmount.

- newCapacity: MoneyAmount.

- excess: MoneyAmount.

- pendingTechnicalCommitments: referencias de recursos.

- cutAt: Instant.

Los componentes superpuestos identifican su relación para no descontarse dos veces.

La secuencia de movimientos no se utiliza como secuencia de todos los cambios de reservas o afectaciones.
positionVersion identifica la versión global de la posición.

### **7.2. Consultas**

| **Contrato** | **operationId**    | **Método y ruta**                            |
|--------------|--------------------|----------------------------------------------|
| CI-04-012    | getFundPosition    | GET /v1/fund-positions/{accountId}           |
| CI-04-012    | listFundMovements  | GET /v1/fund-positions/{accountId}/movements |
| CI-04-023    | getFundReservation | GET /v1/fund-reservations/{reservationId}    |

La posición recibe finalidad y corte cuando corresponde.

Los movimientos reciben finalidad, intervalo, tamaño y cursor. El inicio del intervalo se incluye y el final se excluye.

La primera página fija cutId. Las siguientes conservan ese corte.

### **7.3. FundReservation**

Contiene:

- reservationId y version.

- accountId y currency.

- operationId.

- businessOwnerService.

- requestingService.

- executionCircuitCode.

- coveredEffectReferences: ObjectReference\[\], no vacío.

- authorizedConsumerServices: identificadores de servicio.

- initialAmount: importe de creación.

- currentAmount: importe total autorizado vigente.

- consumedAmount.

- releasedAmount.

- remainingAmount.

- ownFundsCoverage.

- creditCoverage.

- firmness: CANCELLABLE, FIRM o UNCERTAIN.

- status.

- reviewAt: cuando existe revisión prevista.

Se cumple:

**Importe vigente = consumido + liberado + remanente.**

Las coberturas propias y crediticias descomponen el remanente comprometido.

Los estados son ACTIVE, PARTIALLY_CONSUMED, CONSUMED, RELEASED y RESOLUTION_PENDING.

El proveedor determina consumidores autorizados según el circuito. El solicitante no puede ampliarlos libremente.

### **7.4. Interfaces de reserva**

| **Contrato** | **operationId**        | **Método y ruta**                                       |
|--------------|------------------------|---------------------------------------------------------|
| CI-04-013    | createFundReservation  | POST /v1/fund-reservations                              |
| CI-04-014    | consumeFundReservation | POST /v1/fund-reservations/{reservationId}/consumptions |
| CI-04-015    | releaseFundReservation | POST /v1/fund-reservations/{reservationId}/releases     |
| CI-04-016    | changeFundReservation  | POST /v1/fund-reservations/{reservationId}/changes      |

Crear recibe contexto, cuenta, importe positivo, moneda, conceptos, fundamento, circuito, efectos cubiertos, firmeza y
política de recursos.

La política es OWN_FUNDS_ONLY u OWN_FUNDS_AND_AUTHORIZED_OVERDRAFT. La configuración efectiva puede impedir descubierto
aunque se solicite.

Consumir recibe contexto, versión, effectId, importe positivo, moneda, conceptos, fundamento y fechas económicas.

El consumo y el débito se confirman conjuntamente.

Liberar recibe contexto, versión, importe positivo, motivo y evidencia que descarta ejecución u obligación pendiente.

Modificar utiliza:

- CHANGE_AMOUNT: nuevo importe total autorizado y fundamento.

- CHANGE_FIRMNESS: nueva firmeza y evidencia.

El importe total no puede quedar por debajo de consumos y liberaciones ya confirmados. Aumentar exige cobertura
adicional. Reducir afecta únicamente remanente cancelable.

La respuesta devuelve reserva, consumos o liberaciones, remanente, coberturas y pendientes.

## **8. Movimientos y conjuntos internos**

### **8.1. FundMovement**

Contiene:

- movementId: UUID.

- accountId: UUID.

- accountSequence: Sequence.

- effectId: UUID.

- operationId y actionId: UUID.

- executionAttemptId: UUID.

- direction: DEBIT o CREDIT.

- amount: MoneyAmount positivo.

- currency: Currency.

- concepts: MonetaryConcept\[\], no vacío.

- occurredAt: Instant.

- economicDate: Date.

- valueDate: Date, cuando corresponde.

- reservationId, movementGroupId y originalMovementId: cuando existen.

Los movimientos confirmados no se sobrescriben.

### **8.2. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                                |
|--------------|------------------------------|--------------------------------------------------|
| CI-04-017    | registerFundMovement         | POST /v1/fund-movements                          |
| CI-04-018    | executeInternalMovementGroup | POST /v1/internal-movement-groups                |
| CI-04-021    | correctFundMovement          | POST /v1/fund-movements/{movementId}/corrections |
| CI-04-022    | getFundAction                | GET /v1/fund-actions/{actionId}                  |

### **8.3. Movimiento individual**

Recibe contexto, cuenta, efecto, sentido, importe, moneda, conceptos, fundamento y fechas.

Un crédito condicionado agrega:

- conditionId: UUID.

- conditionCode: Code.

- responsibleService: servicio autorizado para acreditar cumplimiento.

- partialFulfillmentAllowed: Booleano.

- evidenceRequirementCodes: Code\[\].

Un débito respaldado por reserva utiliza el circuito de consumo. No elude sus controles mediante un movimiento
individual.

La respuesta identifica movimiento, posición e intento.

### **8.4. Conjunto interno**

Recibe:

- Contexto.

- movementGroupId: UUID.

- groupType: SAME_CURRENCY_TRANSFER o INTERNAL_FX.

- businessBasis.

- legs: piernas monetarias, no vacías.

- reservationConsumptions: consumos relacionados.

- economicDate.

- fxReference: obligatoria para cambio interno.

Cada pierna contiene efecto, cuenta, sentido, importe, moneda y conceptos.

Cada consumo identifica reserva, versión, importe y efectos cubiertos.

El traslado en la misma moneda exige cuentas distintas y conservación del principal. Los cargos se identifican
separadamente.

fxReference contiene referencias del intercambio, propuesta aceptada, cotización, compromiso de Tesorería y afectación
de Riesgos.

También identifica monedas e importes aceptados y huella del contenido.

Fondos no calcula la cotización.

Todos los movimientos y consumos confirman en una sola transacción local. No se admite parcialidad del conjunto.

### **8.5. Correcciones**

Corregir recibe contexto, correctionEffectId, importe, conceptos, motivo, fundamento y efectos relacionados.

Devuelve movimientos correctivos, importe regularizado y remanente corregible.

La corrección no supera el alcance pendiente considerando correcciones confirmadas y comprometidas.

No concede financiación ni autoriza un débito sin fundamento.

## **9. Descubierto**

| **Contrato** | **operationId**         | **Método y ruta**                     |
|--------------|-------------------------|---------------------------------------|
| CI-04-019    | applyOverdraftLimit     | POST /v1/overdraft-limit-applications |
| CI-04-020    | getOverdraftUtilization | GET /v1/overdraft-utilizations        |

Aplicar límite admite exclusivamente al propietario de Préstamos y líneas.

Recibe contexto, cuenta, financiación, autorización, versión de límite, importe, moneda y vigencia.

Agrega la aplicación sustituida cuando existe.

Devuelve límite aplicado, capital utilizado, reservas cubiertas, protección, capacidad nueva, exceso y pendientes.

Una versión antigua no sustituye una posterior.

La reducción no elimina deuda ni cobertura firme o incierta. No habilita nueva capacidad mientras los componentes
existentes la consuman.

Consultar recibe cuenta o financiación, finalidad y corte. Si se informan ambos identificadores deben corresponder a la
misma financiación.

Los intereses pendientes no se agregan al capital monetario utilizado.

## **10. Intentos de ejecución monetaria**

### **10.1. FundExecutionAttempt**

Contiene:

- executionAttemptId.

- actionId y operationId.

- effectReferences.

- accountReferences.

- contentHash.

- requiredAuthorities.

- preparationReferences.

- resourceCommitments.

- decisionDeadline.

- processingState.

- decision.

- resultReferences.

- pendingComponents.

- decidedAt, cuando existe decisión.

decision admite PENDING, COMMITTED y ABORTED.

processingState admite PREPARING, READY_TO_DECIDE, COMMITTED, ABORTED y RECOVERY_REQUIRED.

### **10.2. Interfaces**

| **operationId**           | **Método y ruta**                                           |
|---------------------------|-------------------------------------------------------------|
| getFundExecutionAttempt   | GET /v1/fund-execution-attempts/{attemptId}                 |
| requestFundExecutionAbort | POST /v1/fund-execution-attempts/{attemptId}/abort-requests |

El aborto recibe contexto, motivo y preparación afectada cuando procede de una autoridad.

Devuelve ABORTED, ALREADY_COMMITTED o PENDING.

Una referencia desconocida no se presenta como aborto definitivo.

Fondos registra intención antes de solicitar preparaciones. Confirmación y aborto compiten sobre el mismo intento.

La decisión y los movimientos locales confirman conjuntamente.

Abortar un consumo no libera automáticamente una reserva que respalda otra obligación.

## **11. Afectaciones monetarias**

### **11.1. CI-04-024 — Aplicar o levantar afectación**

**Operación:** actOnMonetaryAffectation  
**Interfaz:** POST /v1/monetary-affectation-actions

La entrada utiliza mode y variantes excluyentes.

### **11.2. Medidas**

Consumidor: Restricciones.

Las modalidades son APPLY_MEASURE, MODIFY_MEASURE y LIFT_MEASURE.

Reciben contexto, medida, componente de aplicación, cuenta, moneda y momento de efecto.

Aplicar o modificar agrega importe y prioridad fundada. Modificar o levantar identifica afectación y versión.

### **11.3. Crédito condicionado**

Consumidor: responsable registrado de la condición.

La modalidad es RELEASE_CREDIT_CONDITION.

Recibe contexto, movimiento original, condición, cuenta, importe, moneda, hecho de cumplimiento y momento efectivo.

Una liberación parcial exige que la condición lo permita.

### **11.4. Resultado**

Devuelve:

- affectationId: UUID.

- version: Version.

- mode.

- appliedAmount.

- remainingAmount.

- insufficientAmount.

- otherAffectationReferences.

- Pendientes.

Inmovilizar no debita. Liberar no acredita.

No se utiliza descubierto para completar una medida.

Cumplir una condición no levanta otras causas.

## **12. Liquidaciones de cuenta**

### **12.1. CI-04-025 — Liquidar conceptos**

**Operación:** settleAccountConcepts  
**Interfaz:** POST /v1/accounts/{accountId}/settlements

Recibe contexto, concepto, período, ocurrencia y condiciones contractuales versionadas.

El período incluye inicio y excluye final.

Una rectificación agrega referencia de liquidación original y fundamento.

Devuelve:

- settlementId: UUID.

- calculationVersion: Version.

- inputHash: Hash.

- Bases y fuentes.

- Condiciones aplicadas.

- Importes bruto, bonificado, tributario y neto.

- Regla de redondeo.

- Referencias de movimientos.

- Estado de cálculo.

- Estado de ejecución.

- Pendientes.

El cálculo admite DETERMINED, INCOMPLETE_INPUT u OBSERVED.

La ejecución admite NOT_REQUESTED, PENDING, CONFIRMED, REJECTED o UNCERTAIN.

La remuneración conserva bases diarias y tasa. Los cargos proporcionales identifican días de servicio y días del mes.

Una liquidación determinada no se informa cobrada hasta confirmar su efecto monetario.

## **13. Servicio Operaciones de efectivo**

### **13.1. CashOperation**

Contiene:

- cashOperationId y version.

- operationType: DEPOSIT o WITHDRAWAL.

- accountId.

- currency.

- declaredAmount.

- verifiedAmount, cuando se conoce.

- locationReference.

- businessBasis.

- resourceReferences.

- physicalStatus.

- monetaryStatus.

- pendingComponents.

Los estados físicos son NOT_STARTED, PREPARED, CONFIRMED, PARTIALLY_CONFIRMED, FAILED y UNCERTAIN.

Los monetarios son NOT_REQUESTED, PENDING, CONFIRMED y REQUIRES_RESOLUTION.

### **13.2. Interfaces**

| **Contrato** | **operationId**          | **Método y ruta**                                           |
|--------------|--------------------------|-------------------------------------------------------------|
| CI-04-026    | registerCashDeposit      | POST /v1/cash-deposits                                      |
| CI-04-027    | requestCashWithdrawal    | POST /v1/cash-withdrawals                                   |
| CI-04-028    | recordCashPhysicalResult | POST /v1/cash-operations/{cashOperationId}/physical-results |
| CI-04-029    | resolveCashDifference    | POST /v1/cash-differences/{differenceId}/resolutions        |
| CI-04-030    | getCashOperation         | GET /v1/cash-operations/{cashOperationId}                   |

### **13.3. Depósito y extracción**

Registrar depósito recibe contexto, depositante, cuenta, importe, moneda y ubicación.

El importe declarado no acredita saldo.

Solicitar extracción recibe contexto, cuenta, importe, moneda, ubicación y fundamento. Agrega reserva o autorización de
tarjeta cuando existen.

No crea otra reserva si el circuito ya tiene cobertura válida.

Devuelve operación, cobertura monetaria, compromiso físico, preparación y pendientes.

### **13.4. Resultado físico**

Recibe contexto, identidad del hecho físico, ubicación, resultado, moneda, momento y evidencia.

Los resultados son CONFIRMED, PARTIAL, FAILED y UNCERTAIN.

verifiedAmount es obligatorio para confirmación y parcialidad. Un fallo exige ausencia comprobada de recepción o
entrega.

Una parcialidad requiere modalidad admitida.

Devuelve hecho físico, regularización monetaria, movimiento propio y diferencias.

Una entrega confirmada con débito pendiente requiere regularización. No se vuelve a entregar efectivo para resolverla.

### **13.5. Diferencias**

Resolver recibe contexto, conclusión, hechos comprobados, tratamiento y aprobación.

Los tratamientos son CONFIRM_ORIGINAL, REGULARIZE_MONETARY_EFFECT, REGULARIZE_OWN_CASH_POSITION o
CONTINUE_INVESTIGATION.

La conclusión conserva su evidencia. Un faltante de caja no demuestra por sí solo entrega al cliente.

La consulta recupera hechos, recursos, movimientos, diferencias y pendientes sin repetir efectos.

## **14. Eventos**

### **14.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                      | **Tema**                           | **Agregado**           |
|--------------|-------------------------------|------------------------------------|------------------------|
| CI-04-031    | AccountChanged                | banking.account.events             | Cuenta.                |
| CI-04-032    | BankIdentificationChanged     | banking.bank-identification.events | Cuenta.                |
| CI-04-033    | FundReservationChanged        | banking.funds.events               | Posición de cuenta.    |
| CI-04-034    | MonetaryMovementConfirmed     | banking.funds.events               | Posición de cuenta.    |
| CI-04-035    | OverdraftUtilizationChanged   | banking.funds.events               | Posición de cuenta.    |
| CI-04-036    | AccountSettlementRecorded     | banking.account.events             | Cuenta.                |
| CI-04-037    | CashOperationOutcomeRecorded  | banking.cash-operation.events      | Operación de efectivo. |
| CI-04-038    | AccountClosureOutcomeRecorded | banking.account.events             | Cuenta.                |
| CI-04-039    | MonetaryAffectationApplied    | banking.funds.events               | Posición de cuenta.    |

La versión inicial de esquema es 1.

La clave es accountId, excepto para efectivo, que utiliza cashOperationId.

Los tipos de agregado son ACCOUNT, ACCOUNT_IDENTIFICATION, FUND_POSITION y CASH_OPERATION.

### **14.2. CI-04-031 — Cuenta modificada**

Payload obligatorio:

- accountId: UUID.

- accountVersion: Version.

- changeType: CONSTITUTED, ENABLED, PARTICIPANTS_CHANGED, CAPABILITIES_CHANGED o RESTRICTION_CHANGED.

- status: estado de cuenta.

- capabilityCodes: Code\[\].

- effectiveAt: Instant.

- pendingComponents: pendientes comunes.

Agrega restrictionApplicationReference para cambio de restricción.

### **14.3. CI-04-032 — Identificación modificada**

Payload obligatorio:

- accountId: UUID.

- assignmentReference: VersionedReference.

- identifierType: CBU o ALIAS.

- changeType: ASSIGNED, REPLACED o RETIRED.

- status: estado de asignación.

- validFrom: Instant.

- pendingComponents: pendientes comunes.

Incluye validTo al confirmar retiro, referencia anterior para sustitución y referencia de directorio cuando interviene.

El valor completo del identificador se recupera mediante consulta autorizada; no es necesario distribuirlo a todos los
consumidores del evento.

### **14.4. CI-04-033 — Reserva modificada**

Payload obligatorio:

- accountId: UUID.

- positionVersion: Version.

- reservationId: UUID.

- reservationVersion: Version.

- changeType: CREATED, AMOUNT_CHANGED, FIRMNESS_CHANGED, CONSUMED o RELEASED.

- executionCircuitCode: Code.

- currency: Currency.

- currentAmount, consumedAmount, releasedAmount y remainingAmount: MoneyAmount.

- ownFundsCoverage y creditCoverage: MoneyAmount.

- firmness: firmeza de reserva.

- status: estado de reserva.

- coveredEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Una reserva consumida y su movimiento no representan dos débitos.

### **14.5. CI-04-034 — Movimiento confirmado**

Payload obligatorio:

- movement: FundMovement.

- positionVersion: Version.

- bookedBalanceAfter: SignedMoneyAmount.

Los campos condicionales de FundMovement identifican reserva, conjunto o movimiento corregido.

El aggregateId coincide con la cuenta del movimiento.

Los conjuntos relacionan sus piernas mediante movementGroupId, sin presuponer orden global entre cuentas.

### **14.6. CI-04-035 — Descubierto modificado**

Payload obligatorio:

- accountId: UUID.

- positionVersion: Version.

- financingReference: ObjectReference.

- limitVersion: Version.

- limitAmount: MoneyAmount.

- currency: Currency.

- previousPrincipalUsed y resultingPrincipalUsed: MoneyAmount.

- protectedCoverage, newCapacity y excess: MoneyAmount.

- causeReference: ObjectReference.

- pendingComponents: pendientes comunes.

La variación de utilización no representa por sí misma un segundo desembolso o pago.

### **14.7. CI-04-036 — Liquidación registrada**

Payload obligatorio:

- accountId: UUID.

- settlementId: UUID.

- calculationVersion: Version.

- conceptCode: Code.

- periodFrom y periodTo: Date.

- currency: Currency.

- grossAmount, discountAmount, taxAmount y netAmount: MoneyAmount.

- calculationStatus: estado de cálculo.

- executionStatus: estado de ejecución.

- movementReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Incluye originalSettlementReference si rectifica.

El evento distingue cálculo registrado de cobro confirmado.

### **14.8. CI-04-037 — Resultado de efectivo**

Payload obligatorio:

- cashOperationId: UUID.

- cashOperationVersion: Version.

- operationType: DEPOSIT o WITHDRAWAL.

- accountId: UUID.

- locationReference: ObjectReference.

- currency: Currency.

- declaredAmount: MoneyAmount.

- physicalStatus: estado físico.

- monetaryStatus: estado monetario.

- movementReferences: ObjectReference\[\].

- differenceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Agrega physicalExecutionId, fecha y verifiedAmount cuando existe hecho comprobado.

### **14.9. CI-04-038 — Resultado de cierre**

Payload obligatorio:

- accountId: UUID.

- accountVersion: Version.

- closureId: UUID.

- outcome: INITIATED, RESIDUAL o CLOSED.

- preventedCapabilityCodes: Code\[\].

- residualObligationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Agrega commercialCeasedAt y closedAt cuando esos hechos están confirmados.

Un resultado residual no se presenta como cierre integral.

### **14.10. CI-04-039 — Afectación aplicada**

Payload obligatorio:

- accountId: UUID.

- positionVersion: Version.

- affectationId: UUID.

- affectationVersion: Version.

- mode: modalidad de afectación.

- currency: Currency.

- appliedAmount, remainingAmount e insufficientAmount: MoneyAmount.

- effectiveAt: Instant.

- otherAffectationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Las medidas agregan su referencia y componente de aplicación.

La liberación de crédito condicionado agrega movimiento original, condición y hecho de cumplimiento.

El evento no representa un crédito o débito adicional.

### **14.11. Consumidores y recuperación**

Los consumidores se autorizan por evento y finalidad:

- Cuentas y propietarios de operaciones.

- Contratos y Solicitudes de productos.

- Identificación bancaria.

- Préstamos y líneas.

- Tesorería.

- Riesgos y Restricciones.

- Contabilidad.

- Extractos y atención.

Las proyecciones monetarias comparan versiones y secuencias según su significado.

Un hueco en movimientos requiere recuperar el tramo o un corte autoritativo. Un evento antiguo no sustituye una posición
posterior.

Los eventos de coordinación no vuelven a ejecutar los efectos originales.

## **15. Seguridad y recuperación**

Se separan permisos para:

- Constitución y modificación de cuentas.

- Identificación bancaria.

- Consulta monetaria.

- Creación, consumo, modificación y liberación de reservas.

- Movimientos y correcciones.

- Aplicación de descubierto.

- Medidas y liberación de condiciones.

- Operaciones físicas y diferencias.

- Preparación, decisión y aborto.

Un consumidor habilitado para consumir una reserva no obtiene permiso general para modificarla o liberarla.

La unicidad de effectId complementa la idempotencia de la actuación.

Cada transacción local confirma posición, movimientos, recursos, historial, resultado y outbox aplicables.

Las llamadas remotas se realizan fuera de la transacción local.

La recuperación utiliza referencias originales. La incertidumbre no se convierte en rechazo ni autoriza liberar
cobertura.

Las consultas históricas y recuperaciones mantienen autorización.

## **16. Errores particulares**

| **Código**                         | **HTTP** | **Significado**                                        |
|------------------------------------|----------|--------------------------------------------------------|
| CONTENT_CONFLICT                   | 409      | Identidad con contenido diferente.                     |
| VERSION_CONFLICT                   | 409      | Versión incompatible.                                  |
| ACCOUNT_NOT_ENABLED                | 422      | Capacidad no habilitada.                               |
| ACCOUNT_CURRENCY_MISMATCH          | 422      | Moneda incompatible.                                   |
| ACCOUNT_CLOSURE_PENDING            | 409      | Actuación incompatible con cierre.                     |
| INSUFFICIENT_OWN_FUNDS             | 422      | Fondos propios insuficientes.                          |
| INSUFFICIENT_AVAILABLE_CAPACITY    | 422      | Cobertura admitida insuficiente.                       |
| OVERDRAFT_NOT_ALLOWED              | 422      | Concepto o producto sin descubierto.                   |
| RESERVATION_CONSUMER_NOT_ALLOWED   | 403      | Ejecutor ajeno al circuito.                            |
| RESERVATION_REMAINDER_EXCEEDED     | 422      | Supera remanente.                                      |
| ECONOMIC_EFFECT_ALREADY_REGISTERED | 409      | Otra actuación intenta repetir un efecto incompatible. |
| INTERNAL_GROUP_INVALID             | 422      | Conjunto inconsistente.                                |
| FX_TERMS_MISMATCH                  | 409      | No corresponde al intercambio aceptado.                |
| CONDITION_OWNER_NOT_ALLOWED        | 403      | Responsable incorrecto.                                |
| MEASURE_SCOPE_MISMATCH             | 422      | Aplicación fuera de medida.                            |
| ALIAS_ALREADY_ASSIGNED             | 409      | Alias ocupado.                                         |
| PHYSICAL_RESULT_CONFLICT           | 409      | Hechos físicos contradictorios.                        |
| ATTEMPT_ALREADY_ABORTED            | 409      | Intento no confirmable.                                |
| DEPENDENCY_UNAVAILABLE             | 503      | Dependencia necesaria indisponible.                    |

Una repetición válida recupera el resultado original. No utiliza el error de duplicación económica como sustituto de la
recuperación.

Una actuación durable pendiente devuelve 202 con consulta.

## **17. Escenarios de aceptación**

| **Escenario**                                             | **Resultado exigido**                                             |
|-----------------------------------------------------------|-------------------------------------------------------------------|
| Saldo 100 y reserva 30                                    | Mantiene saldo 100 y disponibilidad propia 70.                    |
| Consumo de esa reserva por 30                             | Confirma saldo 70 sin segundo descuento de cobertura.             |
| Consumo y liberación concurrentes                         | No afectan el mismo remanente dos veces.                          |
| Ejecutor ajeno al circuito                                | No consume la reserva.                                            |
| Transferencia interna de 30 entre saldos 100 y 20         | Confirma saldos 70 y 50 conjuntamente.                            |
| Destino no admite crédito                                 | No confirma solo el débito del conjunto.                          |
| Cambio interno confirmado                                 | Conserva ambas monedas e importes aceptados.                      |
| Falla una regularización posterior del cambio             | No repite el intercambio ni genera salida externa ficticia.       |
| Reducción de descubierto                                  | Conserva capital y cobertura firme o incierta.                    |
| Crédito condicionado por 100 y cumplimiento parcial de 40 | Libera condición por 40 sin crear otro crédito.                   |
| Otra medida sigue vigente                                 | La liberación de una causa no la levanta.                         |
| Entrega física incierta                                   | Mantiene seguimiento sin liberar ni reentregar por presunción.    |
| Se pierde una respuesta monetaria                         | Recupera intento y efectos por la actuación original.             |
| Aborto compite con confirmación                           | Conserva una sola decisión durable.                               |
| Cierre con obligaciones                                   | Mantiene régimen residual y pendientes explícitos.                |
| Evento duplicado                                          | No repite movimientos ni contabiliza dos veces reserva y consumo. |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, respuestas, errores y eventos contra sus esquemas.
