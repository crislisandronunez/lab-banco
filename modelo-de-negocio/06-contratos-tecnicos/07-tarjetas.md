# **Contratos técnicos — Módulo 7. Tarjetas**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**              | **Responsabilidad**                                                              |
|---------------------------|----------------------------------------------------------------------------------|
| Emisión de tarjetas       | Instrumentos, portadores, entrega, activación, credenciales y habilitaciones.    |
| Autorizaciones            | Decisiones de autorización, controles operativos y coordinación de cobertura.    |
| Procesamiento de consumos | Presentaciones, devoluciones, intercambios y obligaciones de red.                |
| Cuenta de tarjeta         | Capacidad crediticia, compromisos, consumos, planes, deuda, resúmenes y pagos.   |
| Disputas de tarjetas      | Cuestionamientos, investigación, protección, resolución y reparación coordinada. |

Comprende los contratos **CI-07-001 a CI-07-048**.

El instrumento, la cuenta bancaria y la cuenta de tarjeta de crédito conservan identidades diferentes.

Fondos mantiene la posición monetaria de las cuentas bancarias. Cuenta de tarjeta mantiene la deuda crediticia.
Liquidaciones de tesorería ejecuta las obligaciones entre participantes.

Los participantes externos operan dentro de la simulación.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las comprobaciones informativas mediante POST no requieren clave de idempotencia ni crean preparaciones.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

La consulta permite recuperar los identificadores generados aunque se pierda la respuesta inicial.

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

Los importes de compra, pago y devolución son positivos. Acumulados y remanentes admiten cero.

Los cálculos intermedios conservan al menos doce decimales. Los resultados monetarios utilizan HALF_UP, salvo el mínimo,
que se redondea hacia arriba a centavos sin superar el total.

Los cálculos de fechas utilizan America/Argentina/Buenos_Aires y el calendario aplicable.

### **2.3. Obligatoriedad y estructuras comunes**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. CardOperationReference**

Contiene:

- participantId: UUID.

- mechanismCode: Code.

- externalOperationId: ExternalReference.

- merchantReference: ExternalReference, para compra.

- deviceReference: ExternalReference, para operación en dispositivo.

- originalOperationReference: referencia de operación, para actuación vinculada.

La identidad se interpreta dentro del participante y mecanismo.

Los mensajes de transporte no sustituyen la identidad de la operación.

### **3.2. EconomicComponent**

Contiene:

- conceptCode: Code.

- componentType: CAPITAL, INTEREST, FEE, TAX o ADJUSTMENT.

- amount: MoneyAmount.

- currency: Currency.

- calculationReference: ObjectReference, cuando corresponde.

Los intereses futuros no se confunden con capital comprometido.

Al invocar Fondos, el capital utiliza la categoría monetaria PRINCIPAL, conservando concepto y referencia de origen.

### **3.3. LimitDefinition**

| **Campo**         | **Tipo**                  | **Condición**                               |
|-------------------|---------------------------|---------------------------------------------|
| limitId y version | UUID y Version            | Generados por su propietario.               |
| limitType         | Enumeración               | Obligatorio.                                |
| scopeReference    | ObjectReference           | Cuenta, instrumento, adicional o modalidad. |
| operationCodes    | Code\[\]                  | No vacío.                                   |
| currency          | Currency                  | Obligatorio.                                |
| maximumAmount     | MoneyAmount               | Obligatorio.                                |
| validity          | Inicio y final opcional   | Obligatorio.                                |
| basisReference    | ObjectReference           | Obligatorio.                                |
| ownerService      | Identificador de servicio | Determinado por competencia.                |
| periodDefinition  | PeriodDefinition          | Para acumulación temporal.                  |

Los tipos son:

- CREDIT_GENERAL.

- CREDIT_SUBLIMIT.

- OPERATION_MAXIMUM.

- PERIOD_USAGE.

PeriodDefinition contiene frecuencia DAY, WEEK o MONTH, zona y fecha de anclaje. Las semanas y meses se interpretan
desde ese anclaje.

Cada contador conserva periodStart y periodEnd, con final excluido.

No se habilita un límite periódico sin período interpretable.

### **3.4. Autoridad sobre límites**

| **Control**                                   | **Propietario**      |
|-----------------------------------------------|----------------------|
| Habilitación del instrumento                  | Emisión de tarjetas. |
| Máximo por operación                          | Autorizaciones.      |
| Uso acumulado por período                     | Autorizaciones.      |
| Límite crediticio general                     | Cuenta de tarjeta.   |
| Sublímite crediticio de adicional o modalidad | Cuenta de tarjeta.   |
| Disponibilidad monetaria de débito            | Fondos.              |

Un control operativo no sustituye capacidad crediticia ni disponibilidad monetaria.

### **3.5. Resultados**

Los comandos utilizan el resultado común y agregan:

- committedResources.

- Referencias de cobertura.

- Referencias de deuda o movimientos.

- Resultados por componente.

- Pendientes comunes.

Se distinguen decisión de autorización, cobertura, presentación, deuda, pago y liquidación externa.

## **4. Servicio Emisión de tarjetas**

### **4.1. CardInstrument**

Contiene:

- cardId y version, generados.

- issuanceReference: ObjectReference.

- cardType: DEBIT o CREDIT.

- holderPartyId: UUID.

- contractualResponsiblePartyId: UUID.

- holderType: PRIMARY o ADDITIONAL.

- accountReference: cuenta bancaria o cuenta de tarjeta.

- contractReference: VersionedReference.

- maskedIdentifier: texto de hasta 40 caracteres, generado.

- credentialReference: referencia protegida.

- validFrom y validUntil: Instant.

- configurationVersion: Version.

- Estados por dimensión.

- predecessorCardId y successorCardId, cuando existe reemplazo.

| **Dimensión**    | **Valores**                                     |
|------------------|-------------------------------------------------|
| emissionStatus   | PREPARING, ISSUED, FAILED.                      |
| deliveryStatus   | PENDING, DELIVERED, FAILED, UNCERTAIN.          |
| activationStatus | INACTIVE, ACTIVE.                               |
| usageStatus      | ENABLED, BLOCKED, EXPIRED, REPLACED, CANCELLED. |

Las credenciales sintéticas completas no aparecen en eventos, consultas ordinarias ni comprobantes.

### **4.2. Interfaces**

| **Contrato** | **operationId**     | **Método y ruta**                          |
|--------------|---------------------|--------------------------------------------|
| CI-07-002    | issueCard           | POST /v1/cards                             |
| CI-07-003    | actOnCardLifecycle  | POST /v1/cards/{cardId}/lifecycle-actions  |
| CI-07-004    | getCard             | GET /v1/cards/{cardId}                     |
| CI-07-004    | checkCardUsage      | POST /v1/cards/{cardId}/usage-checks       |
| CI-07-005    | changeCardUsage     | POST /v1/cards/{cardId}/usage-changes      |
| CI-07-006    | actOnCardInstrument | POST /v1/cards/{cardId}/instrument-actions |

### **4.3. Emisión y ciclo**

Emitir recibe contexto, constitución de emisión, contrato, modalidad, portador, responsable contractual, vínculo,
capacidades y vigencia.

Una adicional exige autorización del responsable contractual.

Emitir no activa ni crea deuda.

Las actuaciones de ciclo son:

| **Acción**         | **Datos específicos**                                  |
|--------------------|--------------------------------------------------------|
| RECORD_DELIVERY    | Destinatario, fecha y evidencia.                       |
| ACTIVATE           | Evidencia de autenticación y conformidad del portador. |
| REPLACE_CREDENTIAL | Fundamento y verificación.                             |
| RECOVER_ACCESS     | Evidencia del procedimiento de recuperación.           |

Una credencial sustituida no habilita nuevos usos.

### **4.4. Comprobación y configuración**

La comprobación recibe operación, acto, canal, modalidad, importe, moneda, momento y finalidad.

Devuelve checkId, versión de instrumento, versión de configuración, capacidades y resultado ENABLED, PREVENTED o
PENDING.

No prepara habilitación ni afecta contadores.

Cambiar configuración recibe contexto, capacidades, límites, fecha efectiva y autorización.

Cada cambio de límite utiliza modalidad CREATE, MODIFY o REMOVE, con definición o referencia según corresponda.

Emisión coordina los componentes con sus propietarios. Cada componente aplicado informa propietario, referencia, versión
y fecha.

No declara aplicación completa mientras falten confirmaciones obligatorias.

### **4.5. Bloqueo, renovación y cancelación**

Las acciones son BLOCK, UNBLOCK, RENEW, REPLACE y CANCEL.

Reciben contexto, motivo, alcance y evidencias.

Desbloquear no reactiva un instrumento reemplazado o cancelado.

Renovar o reemplazar conserva referencia del antecesor y sucesor.

Cancelar una principal identifica el tratamiento de adicionales.

La respuesta distingue efecto local, comunicación de red e intentos pendientes.

### **4.6. Preparación de uso**

| **operationId**         | **Método y ruta**                                                       |
|-------------------------|-------------------------------------------------------------------------|
| prepareCardUsage        | POST /v1/card-usage-preparations                                        |
| getCardUsagePreparation | GET /v1/card-usage-preparations/{preparationId}                         |
| notifyCardUsageDecision | POST /v1/card-usage-preparations/{preparationId}/decision-notifications |

Utiliza la preparación común y agrega instrumento, versión, acto, canal y modalidad.

Protege únicamente habilitación propia. No concede fondos ni crédito.

Un bloqueo incompatible posterior impide nuevas preparaciones y solicita resolver los intentos existentes.

Las preparaciones no se liberan por timeout.

## **5. Cuenta de tarjeta: constitución y capacidad**

### **5.1. CardAccount**

Contiene:

- cardAccountId y version, generados.

- constitutionId: UUID.

- contractReference: VersionedReference.

- responsibleParties: referencias de responsables contractuales, no vacías.

- approvalReference: VersionedReference.

- approvalApplicationReference: cuando está confirmada.

- exposureReferences: ObjectReference\[\].

- currencyPositions: posiciones por moneda.

- economicTermsReference: VersionedReference.

- closingDay: entero entre 1 y 28.

- status: CONSTITUTING, ACTIVE, LIMITED, CLOSING, RESIDUAL o CLOSED.

Cada posición conserva:

- Moneda.

- Límite y sublímites.

- Compromisos.

- Capital facturado, no facturado y futuro.

- Conceptos adicionales.

- Capital protegido por disputa.

- Saldo a favor.

- Cobertura protegida.

- Capacidad nueva.

- Exceso.

No se suman dos veces capital o compromisos que representan el mismo consumo.

### **5.2. Interfaces**

| **Contrato** | **operationId**           | **Método y ruta**                                       |
|--------------|---------------------------|---------------------------------------------------------|
| CI-07-001    | constituteCardAccount     | POST /v1/card-accounts                                  |
| CI-07-007    | changeCardCreditLimits    | POST /v1/card-accounts/{cardAccountId}/limit-changes    |
| CI-07-010    | commitCardCredit          | POST /v1/card-credit-commitments                        |
| CI-07-012    | actOnCardCreditCommitment | POST /v1/card-credit-commitments/{commitmentId}/actions |

Constituir recibe contexto, constitución, contrato, responsables, aprobación, límites, condiciones y día de cierre.

Sin consumos, el capital es cero.

No se habilita una moneda sin aprobación correspondiente.

Cambiar límites exige fundamento contractual y aprobación o exposición cuando corresponde. Una reducción conserva
compromisos firmes o inciertos y capital ya reconocido.

### **5.3. CardCreditCommitment**

Contiene:

- commitmentId y version.

- cardAccountId.

- cardId.

- authorizationReference.

- capitalAmount: MoneyAmount.

- currency.

- modalityCode.

- affectedLimits: afectaciones por límite.

- remainingAmount.

- firmness: CANCELLABLE, FIRM o UNCERTAIN.

- status: ACTIVE, PARTIALLY_TRANSFORMED, TRANSFORMED, RELEASED o RESOLUTION_PENDING.

- planReference, cuando existe.

Cada afectación contiene límite, versión e importe.

Comprometer recibe contexto, intento, autorización, cuenta, instrumento, capital, moneda, modalidad y configuración.
Agrega plan aceptado cuando corresponde.

Cuenta de tarjeta determina todos los límites exigibles y los afecta conjuntamente.

Las acciones son INCREASE, REDUCE y RELEASE, con importe objetivo y evidencia según modalidad.

No se libera capital ya transformado en deuda.

## **6. Servicio Autorizaciones**

### **6.1. Configuración operativa**

| **operationId**                   | **Método y ruta**                                         |
|-----------------------------------|-----------------------------------------------------------|
| applyCardOperationalConfiguration | POST /v1/card-operational-configurations                  |
| getCardOperationalConfiguration   | GET /v1/card-operational-configurations/{configurationId} |

Aplicar recibe contexto, instrumento, versión de configuración de origen, límites operativos y fecha efectiva.

Una versión atrasada no reemplaza una posterior.

Cambiar configuración no borra afectaciones pendientes de períodos anteriores.

### **6.2. OperationalUsageAllocation**

Contiene:

- allocationId: UUID.

- authorizationId: UUID.

- limitId y limitVersion.

- currency.

- committedAmount.

- executedAmount.

- releasedAmount.

- sequence.

- periodStart y periodEnd, para límite periódico.

La unicidad corresponde a autorización y límite.

La presentación transforma compromiso pendiente en uso ejecutado. No vuelve a afectar el mismo importe.

Las devoluciones no restablecen automáticamente uso histórico.

### **6.3. Interfaces de autorización**

| **Contrato** | **operationId**              | **Método y ruta**                                      |
|--------------|------------------------------|--------------------------------------------------------|
| CI-07-008    | authorizeDebitCardOperation  | POST /v1/debit-card-authorizations                     |
| CI-07-009    | authorizeCreditCardOperation | POST /v1/credit-card-authorizations                    |
| CI-07-011    | actOnCardAuthorization       | POST /v1/card-authorizations/{authorizationId}/actions |
| CI-07-013    | getCardAuthorization         | GET /v1/card-authorizations/{authorizationId}          |

La solicitud contiene contexto, operación, instrumento, importe, moneda, acto, canal, modalidad, autenticación y huella.

Agrega comercio para compra, dispositivo para extracción, ubicación de efectivo cuando corresponde, plan aceptado para
cuotas y cambio aceptado para conversión.

### **6.4. Decisión y cobertura**

La respuesta contiene:

- authorizationId.

- attemptId.

- decision: APPROVED, DECLINED o PENDING.

- usageAllocationReferences.

- pendingComponents.

- approvedContentHash, para aprobación.

- economicCoverageReference, para aprobación.

- validUntil, cuando aprueba.

La aprobación exige reserva de Fondos o compromiso crediticio confirmado y controles operativos satisfechos.

Un fallo técnico se distingue de un rechazo de negocio. Una cobertura incierta mantiene resultado pendiente.

La autorización no demuestra presentación ni entrega física.

### **6.5. Intento y aborto**

CardAuthorizationAttempt conserva:

- Identidad de intento, actuación y operación.

- Operación externa.

- Huella.

- Instrumento y configuraciones.

- Preparaciones.

- Afectaciones operativas.

- Cobertura económica, cuando existe.

- Límite de decisión.

- Decisión durable.

- Estado de procesamiento.

- Pendientes.

La decisión durable es PENDING, COMMITTED o ABORTED.

Una decisión COMMITTED conserva resultado de negocio APPROVED o DECLINED.

| **operationId**               | **Método y ruta**                                               |
|-------------------------------|-----------------------------------------------------------------|
| getCardAuthorizationAttempt   | GET /v1/card-authorization-attempts/{attemptId}                 |
| requestCardAuthorizationAbort | POST /v1/card-authorization-attempts/{attemptId}/abort-requests |

El aborto devuelve ABORTED, ALREADY_COMMITTED o PENDING.

Un aborto local no demuestra liberación remota. Los recursos inciertos conservan seguimiento.

### **6.6. Ajustes y vencimiento**

Las acciones son:

- INCREASE_AMOUNT: importe objetivo y autorización.

- REDUCE_AMOUNT: importe objetivo y evidencia.

- CANCEL: fundamento y comprobación de ausencia de ejecución pendiente.

- CHECK_EXPIRATION: revisión de vigencia y presentaciones.

- RECORD_PRESENTATION_RESULT: presentación, importe ejecutado, moneda, condición final y efectos.

Las autorizaciones de compra tienen siete días corridos de vigencia. Su vencimiento inicia consulta y resolución; no
libera automáticamente recursos.

Una presentación final de 60 sobre autorización de 100 permite concluir los 40 restantes únicamente después de comprobar
el cierre en los propietarios de cobertura y contadores.

## **7. Procesamiento de presentaciones**

### **7.1. CardPresentation**

Contiene:

- participantReference: ObjectReference.

- externalPresentationId: ExternalReference.

- operationReference: CardOperationReference.

- cardReference: ObjectReference.

- amount: MoneyAmount positivo.

- currency.

- modalityCode.

- merchantOrDeviceReference: ExternalReference.

- occurredAt: Instant.

- isFinal: Booleano.

- evidenceReferences: ObjectReference\[\].

- authorizationReference: cuando existe.

- presentationId, version y receivedAt, generados.

La identidad de presentación incluye participante y referencia externa.

### **7.2. Interfaces**

| **Contrato** | **operationId**               | **Método y ruta**                                   |
|--------------|-------------------------------|-----------------------------------------------------|
| CI-07-014    | processDebitCardPresentation  | POST /v1/debit-card-presentations                   |
| CI-07-015    | processCreditCardPresentation | POST /v1/credit-card-presentations                  |
| CI-07-016    | recordCardConsumption         | POST /v1/card-accounts/{cardAccountId}/consumptions |
| CI-07-021    | getCardNetworkAction          | GET /v1/card-network-actions/{actionId}             |

Presentar recibe contexto y contenido de presentación.

Devuelve validación, movimiento o consumo, obligación externa y actualización de autorización.

Los resultados son CONFIRMED, PENDING_REVIEW, REJECTED y UNCERTAIN.

Una presentación sin autorización, tardía o excesiva requiere resolución fundada.

En extracción, se reconoce el movimiento confirmado por Operaciones de efectivo sin consumir nuevamente la misma
reserva.

### **7.3. Consumo crediticio**

Consumidor: Procesamiento de consumos.

Recibe contexto, identidad de efecto de consumo, presentación, autorización y compromiso cuando existen, capital,
moneda, fecha confirmada y condiciones aceptadas.

Un consumo excepcional sin autorización exige referencia de resolución que habilite su tratamiento.

Cuenta de tarjeta transforma compromiso en capital mediante una transacción local.

Devuelve consumo, capital, remanente del compromiso, ciclo y capacidad.

Un bloqueo actual no invalida por sí solo una presentación anterior válida.

## **8. Cuotas, planes y devoluciones**

### **8.1. Interfaces**

| **Contrato** | **operationId**            | **Método y ruta**                                          |
|--------------|----------------------------|------------------------------------------------------------|
| CI-07-017    | calculateCardPlan          | POST /v1/card-plan-calculations                            |
| CI-07-018    | actOnCardPlan              | POST /v1/card-accounts/{cardAccountId}/plan-actions        |
| CI-07-019    | adjustCardConsumption      | POST /v1/card-consumption-adjustments                      |
| CI-07-020    | applyCardConsumptionReturn | POST /v1/card-accounts/{cardAccountId}/consumption-returns |

### **8.2. Cálculo y constitución**

Calcular recibe contexto, capital, moneda, modalidad, cantidad de cuotas, tasa, cargos, tributos, fecha de constitución,
primer vencimiento y calendario.

Las modalidades son INTEREST_FREE_INSTALLMENTS y BANK_FINANCED_PLAN.

La primera utiliza interés cero. Los planes financiados por el banco utilizan sistema francés.

Devuelve cálculo versionado, huella, cronograma, flujos, CFT y supuestos.

Cada cuota identifica índice, fecha contractual, vencimiento ajustado, capital, interés, cargos, tributos y total.

El CFT utiliza flujos y fechas reales. Su estado es DETERMINED, INCOMPLETE_INPUT o NO_UNIQUE_RATE. Solo el primero
incluye tasa definitiva.

Constituir o redistribuir recibe contexto, cálculo, aceptación y referencias del capital seleccionado.

No crea otra compra ni vuelve a comprometer capital existente.

No capitaliza conceptos adicionales automáticamente.

### **8.3. Anulación y devolución**

Procesamiento de consumos recibe contexto, operación original, importe, moneda, motivo y evidencias.

Las acciones son CANCEL_UNPRESENTED y RETURN_CONSUMPTION.

El recuperable considera devoluciones y restituciones de disputa confirmadas o comprometidas.

Aplicar devolución en Cuenta de tarjeta recibe efecto único, consumo, devolución confirmada, importe, moneda y
regularizaciones relacionadas.

La devolución reduce:

1.  Capital futuro del mismo consumo, desde las últimas cuotas.

2.  Capital facturado pendiente.

3.  Capital ya pagado, mediante saldo a favor.

Se recalculan intereses futuros y consecuencias indebidas del consumo corregido.

No acredita dos veces una restitución provisional y su devolución definitiva.

## **9. Resúmenes, total y mínimo**

### **9.1. CardStatement**

Contiene:

- statementId y version.

- cardAccountId.

- cycleReference.

- cutAt.

- currencySections.

- plannedDueDate.

- effectiveDueDate.

- deliveryReferences.

- status: COMPOSING, VALIDATED, ISSUED, RECTIFIED o REQUIRES_REVIEW.

- documentReference, cuando está generado.

Cada sección monetaria conserva capital facturado, intereses, comisiones, tributos, protecciones, pagos, devoluciones,
total y mínimo.

Autorizaciones pendientes y cuotas futuras se muestran separadamente de lo exigible.

### **9.2. Interfaces y cierre**

| **Contrato** | **operationId**             | **Método y ruta**                                        |
|--------------|-----------------------------|----------------------------------------------------------|
| CI-07-022    | issueCardStatement          | POST /v1/card-accounts/{cardAccountId}/statements        |
| CI-07-023    | recordCardStatementDelivery | POST /v1/card-statements/{statementId}/delivery-evidence |
| CI-07-024    | getCardAccountPosition      | GET /v1/card-accounts/{cardAccountId}/position           |
| CI-07-024    | getCardStatement            | GET /v1/card-statements/{statementId}                    |

Emitir recibe contexto, ciclo y corte.

El cierre comprende presentaciones confirmadas hasta finalizar el día contractual. Cuenta de tarjeta lo ordena frente a
la incorporación de consumos mediante corte temporal y secuencia local.

Las presentaciones posteriores integran el ciclo siguiente.

La misma cuenta y ciclo recuperan la emisión original. Una rectificación exige fundamento y referencias de corrección, y
conserva la versión anterior.

La consulta del resumen admite versión concreta. La consulta de posición admite moneda, finalidad y corte.

### **9.3. Entrega y vencimiento**

El vencimiento ordinario se calcula sumando veinte días corridos a la fecha de cierre y ajustando el resultado al
siguiente día hábil cuando corresponde.

El cálculo conserva:

- closingDate: Date.

- calendarReference: VersionedReference.

- plannedDueDate: Date.

- effectiveDueDate: Date.

- dueDateAdjustmentReasonCode: Code, cuando existe ajuste adicional.

Registrar entrega recibe contexto, versión de resumen, comunicación, destinatario, fecha y evidencia comprobable.

Devuelve entrega, cálculo de vencimiento y actualizaciones de cobro.

Debe acreditarse entrega al menos cinco días corridos antes del vencimiento efectivo.

Si no se cumple:

- Se conserva la fecha de cierre.

- Se registra la incidencia.

- Se establece un vencimiento con al menos cinco días corridos desde la entrega acreditada, ajustado al siguiente hábil.

- El ajuste no anticipa el vencimiento ordinario.

- Se actualizan los cobros pendientes.

- Se corrigen financiación y mora atribuibles a la demora del banco.

Si todavía no existe evidencia de entrega, no se presume cumplida la anticipación ni se atribuyen consecuencias al
cliente basadas en esa presunción.

La modificación actualiza la misma obligación y sus ocurrencias relacionadas. No crea otra ocurrencia ejecutable por el
solo cambio de vencimiento.

La aceptación técnica de un correo no acredita entrega del resumen.

### **9.4. Total y mínimo**

Para conceptos no protegidos:

- P: capital facturado pendiente.

- K: parte de ese capital correspondiente a mínimos anteriores vencidos.

- C: conceptos adicionales exigibles.

Se exige 0 ≤ K ≤ P.

**Total = P + C.**

**Mínimo = menor entre el total y C + K + 10 % de (P − K).**

El mínimo se redondea hacia arriba a centavos, sin superar el total.

K es parte de P; no se agrega nuevamente como otra deuda.

Las protecciones se aplican antes de determinar total y mínimo exigibles.

## **10. Pagos y financiación rotativa**

### **10.1. Interfaces**

| **Contrato** | **operationId**               | **Método y ruta**                                                    |
|--------------|-------------------------------|----------------------------------------------------------------------|
| CI-07-025    | prepareCardPayment            | POST /v1/card-accounts/{cardAccountId}/payment-preparations          |
| CI-07-025    | getCardPaymentPreparation     | GET /v1/card-payment-preparations/{preparationId}                    |
| CI-07-026    | resolveCardPaymentPreparation | POST /v1/card-payment-preparations/{preparationId}/resolutions       |
| CI-07-027    | actOnCardAutomaticPayment     | POST /v1/card-statements/{statementId}/automatic-payment-actions     |
| CI-07-028    | assessCardFinancing           | POST /v1/card-accounts/{cardAccountId}/financing-assessments         |
| CI-07-029    | prepareCardCurrencyPayment    | POST /v1/card-accounts/{cardAccountId}/currency-payment-preparations |

### **10.2. Preparación y aplicación**

Preparar recibe contexto, pago, pagador, importe, moneda, fecha efectiva prevista y modalidad TOTAL, MINIMUM o
SPECIFIED_AMOUNT.

La etapa es PRELIMINARY_ACCEPTANCE o PREPARE_APPLICATION.

Devuelve preparación, versión de deuda, distribución, mínimo, excedente y estado:

PRELIMINARILY_ACCEPTED, PREPARED, APPLIED, CANCELLED o REQUIRES_RESOLUTION.

La distribución identifica obligaciones, conceptos, importes y moneda. Las preparaciones concurrentes evitan
asignaciones incompatibles.

Resolver admite:

- CONFIRM_RECEIPT: importe, moneda, fecha efectiva y evidencia de ingreso.

- CANCEL_NOT_EXECUTED: evidencia que descarta ejecución pendiente.

La imputación atiende obligaciones vencidas más antiguas y luego las del ciclo; dentro de ellas, intereses exigibles y
capital, después comisiones y tributos asociados.

Devuelve capital cancelado, conceptos aplicados, deuda, mínimo, capacidad y saldo a favor.

Pagar el total no precancela cuotas futuras. Preparar no demuestra ingreso.

### **10.3. Débito automático**

Recibe contexto, mandato, modalidad total o mínimo, fecha de cobro y ocurrencia cuando existe.

La ejecución ordinaria es a las 09:00 del vencimiento efectivo.

Se calcula el remanente real antes de ejecutar y se respeta el máximo autorizado.

La insuficiencia rechaza el importe completo. No produce cobros parciales ni reintentos ocultos.

Un cambio de vencimiento actualiza la misma obligación y trata las ocurrencias existentes según su estado real.

La reversión de un débito se refleja mediante corrección vinculada, sin repetir la restitución monetaria.

### **10.4. Rotativo y mora**

Recibe contexto, moneda, período, vencimiento efectivo, cortes de posición y tasa versionada.

Devuelve evaluación, bases diarias, interés, mínimo impago, inicio de mora y ajustes.

El interés simple Actual/365 comienza al día siguiente del vencimiento.

No se calcula sobre intereses, cargos, tributos ni cuotas futuras.

No se solapan interés de plan y rotativo para el mismo capital y período.

Cumplir el mínimo evita mora del mínimo, aunque continúe financiación.

Los importes protegidos por disputa se excluyen de las bases durante el período protegido.

### **10.5. Pago con conversión**

Recibe contexto, moneda de deuda, importe objetivo, moneda de pago, pagador e instrucción de conversión.

Devuelve preparación, propuesta de cambio, vigencia, importe de origen, cargos y obligación destinataria.

Preparar no ejecuta cambio ni aplica pago.

La conversión utiliza un intercambio aceptado y conserva sus referencias. Una ejecución incierta se recupera antes de
originar otra.

El pago reconoce el resultado convertido sin crear otro débito del mismo origen.

## **11. Disputas**

### **11.1. CardDispute**

Contiene:

- disputeId y version, generados.

- requesterReference.

- operationReference.

- consumptionReference, cuando existe.

- disputedComponents: referencias de conceptos y alcance.

- amount y currency.

- reasonCode.

- originalReceivedAt.

- evidenceReferences.

- status.

- Referencias de protección, resoluciones, reconsideraciones y efectos económicos.

Los estados de investigación son RECEIVED, UNDER_INVESTIGATION, AWAITING_EXTERNAL_INFORMATION, RESOLVED y
UNDER_RECONSIDERATION.

La reparación conserva estado separado: NOT_REQUIRED, PENDING, PARTIAL, CONFIRMED o UNCERTAIN.

### **11.2. Interfaces**

| **Contrato** | **operationId**             | **Método y ruta**                                          |
|--------------|-----------------------------|------------------------------------------------------------|
| CI-07-030    | createCardDispute           | POST /v1/card-disputes                                     |
| CI-07-031    | applyCardDisputeProtection  | POST /v1/card-accounts/{cardAccountId}/dispute-protections |
| CI-07-032    | resolveCardDispute          | POST /v1/card-disputes/{disputeId}/resolutions             |
| CI-07-032    | reconsiderCardDispute       | POST /v1/card-disputes/{disputeId}/reconsiderations        |
| CI-07-033    | executeCardDisputeEconomics | POST /v1/card-dispute-economic-actions                     |
| CI-07-034    | getCardDispute              | GET /v1/card-disputes/{disputeId}                          |

### **11.3. Recepción, oportunidad y protección**

Recibir conserva fecha original, conceptos, importe, evidencias y requisitos pendientes.

El acuse se produce el mismo día.

Cuando el cuestionamiento corresponde a un consumo incluido en un resumen recibido, se comprueba el plazo de treinta
días corridos desde esa recepción acreditada, excluyendo el día inicial.

Los cuestionamientos posteriores a ese plazo se reciben y tramitan como reclamos, conservando fecha, contenido,
evidencias y referencias de la operación.

La respuesta identifica la vía de tratamiento y el reclamo relacionado. No presenta una derivación como rechazo de la
recepción.

Si falta evidencia de recepción del resumen, no se presume vencido el plazo.

Las denuncias urgentes se derivan inmediatamente al servicio competente, cualquiera sea la vía de tratamiento.

La respuesta fundada utiliza quince días corridos. Una investigación exterior adicional puede alcanzar sesenta días
corridos con motivo comunicado.

En crédito, la protección suspende exigibilidad e intereses de los conceptos alcanzados y recalcula total y mínimo.

Aplicar protección recibe contexto, disputa, componentes, importe, moneda, acción y fundamento.

Las acciones son APPLY, MODIFY y LIFT.

La protección no elimina capital ni restituye capacidad automáticamente. Levantar una causa no elimina otras.

En débito, registrar protección o investigación no equivale a ejecutar una devolución.

### **11.4. Resolución y reconsideración**

Resolver recibe contexto, conclusión, tratamientos por componente, evidencias y aprobación.

Las conclusiones son FAVOURABLE, PARTIALLY_FAVOURABLE y UNFAVOURABLE.

Cada tratamiento identifica concepto, resultado, importe y reparación requerida.

La reconsideración identifica resolución, recepción y fundamentos.

Se conceden siete días corridos desde la comunicación acreditada de la resolución para manifestar disconformidad y diez
días hábiles para resolver una reconsideración oportuna.

La protección continúa durante esos períodos aplicables.

Una resolución desfavorable no genera intereses retroactivos correspondientes al período protegido.

Los conceptos que vuelven a exigirse:

- Conservan su identidad y relación con la disputa.

- Se incorporan a un resumen posterior.

- Tienen entrega y vencimiento propios.

- No se presentan como vencidos durante el período en que estuvieron protegidos.

- No recuperan exigibilidad mientras otra causa de protección siga vigente.

El levantamiento identifica componentes, causa, fecha efectiva y resumen de reincorporación cuando corresponde.

Una resolución parcialmente favorable aplica estos tratamientos por concepto, sin extender el resultado de una parte al
resto.

### **11.5. Efectos económicos**

Proveedor: Procesamiento de consumos.

Recibe contexto, disputa, decisión, efectos originales, importe, moneda y restituciones relacionadas.

Las modalidades son:

- PROVISIONAL_RESTITUTION.

- DEFINITIVE_RESTITUTION.

- REGULARIZE_PROVISIONAL.

- AUTHORIZED_RECOVERY.

La respuesta separa reparación al cliente de contracargo o recupero externo.

Una resolución favorable no espera recuperar del comercio.

Los circuitos de devolución, disputa y reclamo reconocen el mismo remanente económico para impedir restituciones
duplicadas.

## **12. Red, correcciones y cierre**

### **12.1. Interfaces**

| **Contrato** | **operationId**                  | **Método y ruta**                                                    |
|--------------|----------------------------------|----------------------------------------------------------------------|
| CI-07-035    | exchangeCardNetworkRecords       | POST /v1/card-network-exchanges                                      |
| CI-07-035    | reconcileCardNetworkRecords      | POST /v1/card-network-reconciliations                                |
| CI-07-036    | determineCardClearingObligations | POST /v1/card-clearing-cycles/{cycleId}/determinations               |
| CI-07-036    | getCardClearingObligation        | GET /v1/card-clearing-obligations/{obligationId}                     |
| CI-07-037    | recordCardClearingSettlement     | POST /v1/card-clearing-obligations/{obligationId}/settlement-results |
| CI-07-038    | correctCardNetworkAction         | POST /v1/card-network-actions/{networkActionId}/corrections          |
| CI-07-039    | correctCardAccount               | POST /v1/card-accounts/{cardAccountId}/corrections                   |
| CI-07-040    | closeCardAccount                 | POST /v1/card-accounts/{cardAccountId}/closures                      |
| CI-07-041    | getCardAccountAction             | GET /v1/card-account-actions/{actionId}                              |

### **12.2. Intercambio y conciliación**

El intercambio recibe contexto, participante, mecanismo versionado, tipo de mensaje, operaciones y contenido conforme al
esquema registrado.

La conciliación recibe fuentes y cortes comparables.

Devuelve acuses, resultados, diferencias y pendientes.

No se duplica la conciliación de tarjetas dentro de Compensación y conciliación de pagos.

Un acuse técnico no equivale a presentación pagada ni a liquidación externa.

### **12.3. Obligaciones de red**

Determinar recibe contexto, versión de ciclo, participante, moneda, acuerdo y hechos confirmados.

Devuelve obligación, versión, sentido PAY o RECEIVE, importe, vencimiento, composición y ajustes.

Cada componente conserva hecho único, importe y sentido.

El corte es a las 18:00. Los hechos desde ese instante integran el siguiente intervalo.

Las autorizaciones no se incluyen como consumos. El neteo exige acuerdo, misma contraparte y moneda.

Un ciclo cerrado no se reescribe; sus ajustes integran otro ciclo relacionado.

Liquidaciones de tesorería incorpora resultado CONFIRMED, PARTIAL, FAILED o UNCERTAIN.

La parcialidad exige habilitación del mecanismo.

Una falla externa no vuelve impago un pago del cliente ya aplicado.

### **12.4. Correcciones y cierre**

Corregir red recibe referencias originales, tratamiento, evidencia y aprobación exigible.

Corregir cuenta identifica hecho original, fecha y tratamientos por componente. Distingue deuda, imputación, capacidad,
interés, mora y documentos.

Redistribuir un pago no lo cobra nuevamente.

Cerrar cuenta recibe terminación contractual, motivo y tratamiento de instrumentos.

Devuelve cese de usos, instrumentos, intentos, deuda, cuotas, disputas, saldos a favor y régimen residual.

No acelera cuotas ni libera cobertura incierta por el solo hecho de solicitar cierre.

## **13. Eventos**

### **13.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                               | **Tema**                          | **Clave**         |
|--------------|----------------------------------------|-----------------------------------|-------------------|
| CI-07-042    | CardInstrumentChanged                  | banking.card-issuance.events      | cardId            |
| CI-07-043    | CardAuthorizationChanged               | banking.card-authorization.events | authorizationId   |
| CI-07-044    | CardPresentationOrNetworkEffectChanged | banking.card-processing.events    | Agregado afectado |
| CI-07-045    | CardCapacityOrDebtChanged              | banking.card-account.events       | cardAccountId     |
| CI-07-046    | CardStatementOrPaymentChanged          | banking.card-account.events       | cardAccountId     |
| CI-07-047    | CardDisputeChanged                     | banking.card-dispute.events       | disputeId         |
| CI-07-048    | CardNetworkObligationChanged           | banking.card-processing.events    | obligationId      |

La versión inicial de esquema es 1.

### **13.2. CI-07-042 — CardInstrumentChanged**

Payload obligatorio:

- cardId: UUID.

- cardVersion: Version.

- configurationVersion: Version.

- changeType: ISSUED, DELIVERED, ACTIVATED, CONFIGURATION_CHANGED, BLOCKED, UNBLOCKED, RENEWED, REPLACED o CANCELLED.

- cardType.

- accountReference: ObjectReference.

- Estados de emisión, entrega, activación y uso.

- pendingComponents: pendientes comunes.

Incluye sucesor para renovación o reemplazo y componentes aplicados para cambio de configuración.

aggregateType es CARD_INSTRUMENT.

No incluye credenciales.

### **13.3. CI-07-043 — CardAuthorizationChanged**

Payload obligatorio:

- authorizationId: UUID.

- authorizationVersion: Version.

- attemptId: UUID.

- cardReference: ObjectReference.

- operationReference: CardOperationReference.

- changeType: DECISION_RECORDED, AMOUNT_CHANGED, PRESENTATION_RECORDED, CANCELLATION_UPDATED o EXPIRATION_REVIEWED.

- decision: APPROVED, DECLINED o PENDING.

- amount: MoneyAmount.

- currency.

- usageAllocationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

La aprobación agrega huella, cobertura económica y vigencia.

La presentación agrega referencia, importe ejecutado y condición final.

aggregateType es CARD_AUTHORIZATION.

### **13.4. CI-07-044 — CardPresentationOrNetworkEffectChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: PRESENTATION_UPDATED, RETURN_UPDATED, DISPUTE_EFFECT_UPDATED, EXCHANGE_UPDATED o RECONCILIATION_UPDATED.

- operationReferences: referencias de operaciones.

- economicEffectReferences: ObjectReference\[\].

- obligationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Las variantes son:

- CARD_PRESENTATION: importe, moneda y resultado de presentación.

- CARD_NETWORK_ACTION: clase de actuación, resultado y evidencias.

El aggregateType selecciona la variante.

Una devolución o restitución agrega efecto original y remanente recuperable.

### **13.5. CI-07-045 — CardCapacityOrDebtChanged**

Payload obligatorio:

- cardAccountId: UUID.

- cardAccountVersion: Version.

- changeType: CONSTITUTED, LIMIT_CHANGED, COMMITMENT_CHANGED, CONSUMPTION_RECORDED, PLAN_CHANGED, RETURN_APPLIED,
  PROTECTION_CHANGED, CORRECTED o CLOSURE_UPDATED.

- currency: Currency.

- positionReference: ObjectReference.

- affectedLimitReferences: ObjectReference\[\].

- affectedCapitalReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Cada variante agrega su referencia de compromiso, consumo, plan, devolución, protección o cierre.

Los cambios monetarios incluyen capital anterior y resultante, compromisos pendientes, cobertura protegida, capacidad y
exceso.

Para una constitución multimoneda se publica una variante por posición monetaria, con identidad de evento independiente.

aggregateType es CARD_ACCOUNT.

### **13.6. CI-07-046 — CardStatementOrPaymentChanged**

Payload obligatorio:

- cardAccountId: UUID.

- cardAccountVersion: Version.

- changeType: STATEMENT_ISSUED, STATEMENT_RECTIFIED, DELIVERY_RECORDED, PAYMENT_APPLIED o FINANCING_ASSESSED.

- currency: Currency.

- pendingComponents: pendientes comunes.

Las variantes contienen:

| **Cambio**                    | **Campos específicos**                                                                  |
|-------------------------------|-----------------------------------------------------------------------------------------|
| Resumen emitido o rectificado | Resumen versionado, ciclo, corte, total, mínimo, vencimiento y documento cuando existe. |
| Entrega registrada            | Resumen, comunicación, evidencia, vencimiento ordinario y efectivo.                     |
| Pago aplicado                 | Pago, preparación, ingreso original, asignaciones, capital cancelado y saldo a favor.   |
| Financiación evaluada         | Evaluación, período, base, interés y mínimo impago.                                     |

Una rectificación identifica la versión anterior.

Un ajuste de vencimiento agrega el motivo y las referencias de actualización de cobros.

aggregateType es CARD_ACCOUNT.

El pago aplicado no representa otro ingreso.

### **13.7. CI-07-047 — CardDisputeChanged**

Payload obligatorio:

- disputeId: UUID.

- disputeVersion: Version.

- changeType: RECEIVED, INVESTIGATION_UPDATED, PROTECTION_UPDATED, RESOLVED, RECONSIDERATION_RECEIVED o REPAIR_UPDATED.

- operationReference: CardOperationReference.

- amount: MoneyAmount.

- currency.

- status: estado de investigación.

- repairStatus: estado de reparación.

- protectionReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

La resolución agrega referencia, conclusión y tratamientos.

La reconsideración agrega referencia y vencimiento.

La reparación agrega efectos y regularizaciones relacionados.

Un levantamiento de protección identifica fecha efectiva y conceptos afectados. Incluye la referencia de reincorporación
a resumen cuando está determinada.

aggregateType es CARD_DISPUTE.

### **13.8. CI-07-048 — CardNetworkObligationChanged**

Payload obligatorio:

- obligationId: UUID.

- obligationVersion: Version.

- cycleReference: VersionedReference.

- participantReference: ObjectReference.

- changeType: DETERMINED, ADJUSTMENT_LINKED o SETTLEMENT_UPDATED.

- direction: PAY o RECEIVE.

- amount: MoneyAmount.

- currency.

- compositionReference: ObjectReference.

- fulfilledAmount y remainingAmount: MoneyAmount.

- pendingComponents: pendientes comunes.

La liquidación agrega referencia y resultado. El ajuste identifica ciclo u obligación original.

aggregateType es CARD_NETWORK_OBLIGATION.

### **13.9. Consumidores y orden**

| **Información**          | **Consumidores autorizados**                                                  |
|--------------------------|-------------------------------------------------------------------------------|
| Instrumentos             | Autorizaciones, Cuenta de tarjeta, Contratos, Solicitudes y atención.         |
| Autorizaciones           | Procesamiento de consumos, propietarios de cobertura y controles competentes. |
| Presentaciones y efectos | Cuenta de tarjeta, Fondos cuando corresponde, Contabilidad y atención.        |
| Capacidad y deuda        | Autorizaciones, Riesgos, Cobranzas, Contabilidad y Extractos.                 |
| Resúmenes y pagos        | Pagos, Mandatos, Comunicaciones, Documentos y atención.                       |
| Disputas                 | Cuenta de tarjeta, Procesamiento, Reclamos y controles autorizados.           |
| Obligaciones de red      | Liquidaciones de tesorería, Posiciones y liquidez y Contabilidad.             |

Los consumidores comparan versiones y recuperan estado autoritativo ante discontinuidades relevantes.

No existe orden global entre instrumentos, autorizaciones, cuentas y obligaciones.

## **14. Seguridad y recuperación**

Se separan permisos de emisión, configuración, autorización, compromiso, presentación, consumo, liberación, pago,
disputa, corrección y liquidación.

Una preparación protege únicamente la condición de su autoridad.

Se exige unicidad por:

- Constitución de cuenta.

- Emisión solicitada.

- Operación externa de autorización.

- Presentación externa.

- Efecto de consumo o devolución.

- Cuenta y ciclo de resumen.

- Ingreso y aplicación de pago.

- Restitución económica.

- Hecho incluido en obligación de red.

Los recursos remotos inciertos se consultan antes de liberar afectaciones locales.

La expiración del trabajador no libera preparaciones ni compromisos.

Las transacciones locales confirman agregado, historial, idempotencia y outbox. No mantienen bloqueos SQL durante
llamadas remotas.

Las respuestas históricas e idempotentes conservan autorización.

## **15. Errores particulares**

| **Código**                    | **HTTP** | **Significado**                                   |
|-------------------------------|----------|---------------------------------------------------|
| CONTENT_CONFLICT              | 409      | Identidad con contenido diferente.                |
| VERSION_CONFLICT              | 409      | Versión incompatible.                             |
| CARD_NOT_ACTIVE               | 422      | Instrumento no activado.                          |
| CARD_USAGE_BLOCKED            | 422      | Uso impedido.                                     |
| CONFIGURATION_NOT_APPLIED     | 409      | Configuración requerida pendiente.                |
| OPERATION_LIMIT_EXCEEDED      | 422      | Máximo por operación excedido.                    |
| PERIOD_LIMIT_EXCEEDED         | 422      | Uso acumulado excedido.                           |
| CREDIT_CAPACITY_INSUFFICIENT  | 422      | Capacidad crediticia insuficiente.                |
| CREDIT_SUBLIMIT_EXCEEDED      | 422      | Sublímite crediticio excedido.                    |
| ECONOMIC_COVERAGE_UNCERTAIN   | 409      | No puede concluirse liberación.                   |
| PRESENTATION_CONTENT_MISMATCH | 409      | Presentación incompatible.                        |
| PRESENTATION_REQUIRES_REVIEW  | 422      | Falta resolución exigible para aplicar el efecto. |
| RETURN_AMOUNT_EXCEEDED        | 422      | Supera recuperable.                               |
| PLAN_CAPITAL_OVERLAP          | 409      | Capital asignado de forma incompatible.           |
| STATEMENT_VERSION_CONFLICT    | 409      | Resumen incompatible con la actuación.            |
| PAYMENT_PREPARATION_CONFLICT  | 409      | Aplicación incompatible.                          |
| DISPUTE_PROTECTION_ACTIVE     | 409      | Actuación incompatible con protección.            |
| RESTITUTION_ALREADY_COVERED   | 409      | Otro circuito ya cubre el efecto.                 |
| NETTING_AGREEMENT_REQUIRED    | 422      | Falta acuerdo de neteo.                           |
| DEPENDENCY_UNAVAILABLE        | 503      | Dependencia necesaria indisponible.               |

Una presentación recibida durablemente para revisión responde 202 con seguimiento.

Una comprobación informativa negativa responde 200 con su conclusión.

La recepción de un cuestionamiento fuera del plazo de disputa conserva constancia y derivación a Reclamos; no se rechaza
como solicitud mal formada.

Una repetición coherente recupera el resultado original.

## **16. Escenarios de aceptación**

| **Escenario**                                                   | **Resultado exigido**                                                             |
|-----------------------------------------------------------------|-----------------------------------------------------------------------------------|
| Capacidad general 1000 y sublímite adicional 200                | Compra de 250 no se aprueba por disponer de capacidad general.                    |
| Compra de 150 dentro de ambos límites                           | Afecta los controles exigibles conjuntamente.                                     |
| Autorización en período diario y presentación al día siguiente  | Transforma la afectación del período original sin duplicarla.                     |
| Fondos rechaza cobertura sin efectos                            | No aprueba la operación y resuelve afectaciones cancelables.                      |
| Cobertura incierta                                              | Mantiene seguimiento antes de liberar.                                            |
| Autorización 100 y presentación final 60                        | Ejecuta 60 y resuelve el remanente 40 sin otro débito.                            |
| Extracción ya debitada por Operaciones de efectivo              | No vuelve a consumir la misma reserva.                                            |
| Plan sobre capital existente                                    | No crea otra compra ni duplica capital.                                           |
| Cierre contractual                                              | Calcula vencimiento ordinario a veinte días corridos y ajusta al siguiente hábil. |
| Entrega tardía del resumen                                      | Conserva cierre, ajusta vencimiento y corrige consecuencias de la demora.         |
| Cambio de vencimiento                                           | Actualiza ocurrencias sin generar otra ejecución independiente.                   |
| Pago total                                                      | No precancela cuotas futuras.                                                     |
| Débito automático insuficiente                                  | No cobra parcialmente ni reintenta de forma oculta.                               |
| Cuestionamiento posterior a treinta días de recibido el resumen | Conserva recepción y lo tramita como reclamo.                                     |
| Falta evidencia de recepción del resumen                        | No presume vencido el plazo de disputa.                                           |
| Disputa protegida                                               | Excluye conceptos alcanzados de exigibilidad e intereses.                         |
| Resolución desfavorable                                         | No genera intereses retroactivos del período protegido.                           |
| Conceptos que vuelven a exigirse                                | Los incorpora a un resumen posterior con entrega y vencimiento propios.           |
| Otra causa de protección sigue vigente                          | No vuelve exigible el concepto por levantar solo una causa.                       |
| Provisional 100 y devolución definitiva 100                     | Regulariza sin acreditar 200.                                                     |
| Liquidación externa fallida                                     | No deshace un pago del cliente ya aplicado.                                       |
| Cierre con cuotas o disputas pendientes                         | Conserva régimen residual.                                                        |
| Respuesta perdida                                               | Recupera actuación, intento y efectos originales.                                 |
| Evento duplicado o antiguo                                      | No repite efectos ni sustituye versiones posteriores.                             |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
