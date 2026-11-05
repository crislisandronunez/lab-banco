# **Contratos técnicos — Módulo 5. Pagos y transferencias**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**                         | **Responsabilidad**                                                                    |
|--------------------------------------|----------------------------------------------------------------------------------------|
| Transferencias                       | Instrucciones internas, salientes y entrantes, cancelaciones y devoluciones.           |
| Pagos                                | Convenios, identificación de obligaciones, ejecución, aplicación y restituciones.      |
| Mandatos y recurrencias              | Autorizaciones recurrentes, programaciones, ocurrencias y solicitudes de reversión.    |
| Cheques y ECHEQ                      | Instrumentos, circulación, presentación, descuento y resultados de cobro.              |
| Compensación y conciliación de pagos | Participantes, intercambios, mensajes, diferencias y obligaciones entre participantes. |

Comprende los contratos **CI-05-001 a CI-05-044**.

Fondos conserva la autoridad monetaria. Los acreedores mantienen sus obligaciones e imputaciones. Liquidaciones de
tesorería ejecuta la liquidación entre participantes.

Ejecución del instrumento, aplicación a una obligación y liquidación entre participantes son hechos diferentes.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las consultas informativas mediante POST no requieren clave de idempotencia ni crean compromisos.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

Permite recuperar los identificadores generados aunque se pierda la respuesta inicial. Las consultas particulares
conservan el detalle de cada capacidad.

### **2.2. Tipos**

| **Tipo**          | **Representación**                                                     |
|-------------------|------------------------------------------------------------------------|
| UUID              | Identificador interno.                                                 |
| Version           | Entero positivo dentro del rango de long.                              |
| Sequence          | Entero positivo dentro del rango de long.                              |
| Code              | Cadena de 1 a 80 caracteres.                                           |
| Instant           | Instante UTC.                                                          |
| Date              | Fecha YYYY-MM-DD.                                                      |
| LocalTime         | Hora HH:mm:ss.                                                         |
| ZoneId            | Identificador IANA válido.                                             |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                  |
| Currency          | ARS o USD.                                                             |
| MoneyAmount       | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |

Los importes de ejecución son positivos. Los acumulados y remanentes admiten cero.

Los valores fuera de escala se rechazan sin redondearlos silenciosamente.

La zona institucional es America/Argentina/Buenos_Aires.

### **2.3. Obligatoriedad y estructuras comunes**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. MonetaryConcept**

Contiene:

- conceptCode: Code.

- componentType: PRINCIPAL, FEE, TAX o ADJUSTMENT.

- amount: MoneyAmount.

- currency: Currency.

- calculationReference: ObjectReference, cuando corresponde.

Los conceptos de cargo se relacionan con su hecho generador. No se duplican porque intervengan varios coordinadores.

Los conceptos del instrumento de pago se distinguen de los conceptos de deuda que imputa el acreedor.

### **3.2. AcceptedDestination**

| **Campo**              | **Tipo**                      | **Condición**                 |
|------------------------|-------------------------------|-------------------------------|
| destinationType        | INTERNAL o EXTERNAL           | Obligatorio.                  |
| accountReference       | ObjectReference               | Para destino interno.         |
| participantReference   | ObjectReference               | Para destino externo.         |
| bankIdentifierType     | Code                          | Si se utilizó identificador.  |
| bankIdentifierValue    | ExternalReference             | Con tipo de identificador.    |
| resolutionReference    | ObjectReference               | Cuando hubo resolución.       |
| beneficiaryReference   | ObjectReference               | Cuando lo exige el mecanismo. |
| beneficiaryDisplayName | Texto de hasta 250 caracteres | Si se presentó al ordenante.  |
| currency               | Currency                      | Obligatorio.                  |
| acceptedContentHash    | Hash                          | Obligatorio.                  |
| acceptedAt             | Instant                       | Obligatorio.                  |

La huella vincula destino, beneficiario, moneda y datos presentados que formen parte de la conformidad.

Cambiar posteriormente un alias no redirige la instrucción.

### **3.3. AuthorizationBasis**

Contiene:

- basisType: DIRECT_INSTRUCTION, MANDATE, OCCURRENCE, CONTRACTUAL_OBLIGATION o AUTHORIZED_RESOLUTION.

- sourceReference: ObjectReference.

- contentHash: Hash.

- actorReferences: ObjectReference\[\].

- evidenceReferences: ObjectReference\[\].

El fundamento debe habilitar el contenido y alcance solicitados.

### **3.4. ExternalOperationReference**

Contiene:

- participantId: UUID.

- mechanismCode: Code.

- mechanismVersion: Version.

- externalOperationId: ExternalReference.

La identidad externa se interpreta dentro de participante y mecanismo. Su versión define el esquema utilizado; una
actualización del mecanismo no convierte una operación antigua en otra operación.

### **3.5. ConfirmedFact**

Contiene:

- factType: tipo de hecho.

- occurredAt: Instant.

- sourceReference: ObjectReference.

- evidenceReferences: ObjectReference\[\].

Los tipos son:

RECEIVED, ACCEPTED_FOR_EXECUTION, DISPATCHED, IRREVOCABLY_ACCEPTED, INSTRUMENT_EXECUTED, BENEFICIARY_CREDITED,
CREDITOR_APPLIED, RETURNED y EXTERNALLY_SETTLED.

La falta de un hecho no se sustituye por una conclusión favorable.

### **3.6. Resultados**

Los comandos utilizan el resultado común y agregan:

- monetaryEffectReferences: ObjectReference\[\].

- externalReferences: ExternalOperationReference\[\].

- confirmedFacts: ConfirmedFact\[\].

- Estados particulares de ejecución y aplicación.

Cada pendiente identifica componente, responsable, motivo y referencia cuando existe.

## **4. Servicio Transferencias**

### **4.1. Transfer**

Contiene:

- transferId y version, generados.

- transferType: INTERNAL, OUTGOING o INCOMING.

- orderingPartyReference: ObjectReference.

- principalAmount: MoneyAmount positivo.

- currency: Currency.

- concepts: MonetaryConcept\[\].

- processingState.

- confirmedFacts: ConfirmedFact\[\].

- monetaryEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Campos condicionales:

- Cuenta de origen para débito interno.

- Destino aceptado para instrucciones internas o salientes.

- Cuenta de destino y evidencia de recepción para entrantes.

- Fundamento de autorización.

- Reserva.

- Referencia externa.

- Intercambio.

- Conversión identificada.

Los estados son RECEIVED, VALIDATING, PREPARING, EXECUTING, COMPLETED, CANCELLED, REJECTED y REQUIRES_RESOLUTION.

La incertidumbre se identifica en el componente afectado.

### **4.2. Interfaces**

| **Contrato** | **operationId**         | **Método y ruta**                             |
|--------------|-------------------------|-----------------------------------------------|
| CI-05-001    | executeInternalTransfer | POST /v1/internal-transfers                   |
| CI-05-002    | sendOutgoingTransfer    | POST /v1/outgoing-transfers                   |
| CI-05-003    | receiveIncomingTransfer | POST /v1/incoming-transfers                   |
| CI-05-004    | cancelTransfer          | POST /v1/transfers/{transferId}/cancellations |
| CI-05-005    | returnTransfer          | POST /v1/transfers/{transferId}/returns       |
| CI-05-006    | getTransfer             | GET /v1/transfers/{transferId}                |
| CI-05-006    | getTransferAction       | GET /v1/transfer-actions/{actionId}           |

### **4.3. Transferencia interna**

Recibe contexto, ordenante, cuenta de origen, destino interno aceptado, principal, moneda, finalidad, autorización,
condiciones comerciales y cargos.

Agrega reserva y referencia de cambio cuando existen.

Las cuentas son distintas. El traslado en la misma moneda conserva principal.

La conversión exige un intercambio aceptado. Sus importes y monedas proceden de ese intercambio, sin conversión
implícita.

Fondos confirma el conjunto indivisible y sus consumos de reserva. Transferencias devuelve la referencia del conjunto y
sus movimientos.

### **4.4. Transferencia saliente**

Recibe contexto, ordenante, origen, destino externo aceptado, mecanismo versionado, principal, moneda, conceptos y
autorización.

Antes del despacho protege la cobertura frente a incertidumbre.

La reserva se consume al comprobar la acreditación, con la misma identidad económica.

Un acuse técnico no demuestra crédito en destino. Si el resultado es incierto se mantiene cobertura y seguimiento.

### **4.5. Transferencia entrante**

Consumidor: Compensación y conciliación de pagos.

Recibe contexto, referencia externa completa, ordenante, beneficiario, cuenta de destino, importe, moneda, finalidad y
evidencia del mensaje.

La comprobación del destino precede a la aceptación irrevocable.

Después de esa aceptación se conserva la obligación de acreditar o tramitar el retorno procedente.

Una repetición externa coherente recupera el crédito original.

### **4.6. Cancelación y devolución**

Cancelar recibe contexto, motivo y fundamento.

Devuelve cancellationId y resultado:

CANCELLED, PENDING_EXTERNAL_CONFIRMATION, ALREADY_CREDITED o REQUIRES_RESOLUTION.

La cancelación confirmada antes del despacho impide un envío posterior. Si existe intercambio, debe resolverse su
capacidad de despacho antes de liberar recursos.

Devolver recibe contexto, returnEffectId, importe, moneda, motivo, destino de retorno, autorización y devoluciones
relacionadas.

Devuelve returnId, acumulado devuelto, comprometido y remanente.

El recuperable considera devoluciones confirmadas y comprometidas. Una petición del ordenante no autoriza debitar al
beneficiario.

### **4.7. Consultas**

Devuelven contenido aceptado, reservas, movimientos, intercambios, hechos, cancelaciones y devoluciones.

La consulta por identidad externa utiliza una búsqueda autenticada:

**POST /v1/transfer-searches**, operationId: searchTransfers.

Recibe finalidad y ExternalOperationReference completa. No admite búsqueda por una referencia externa aislada.

Consultar no reenvía ni libera.

## **5. Servicio Pagos**

### **5.1. CollectionAgreement**

Contiene:

- agreementId y version, generados.

- participantReference.

- contractReference.

- serviceCode.

- acceptedReferenceFormats.

- currencies: Currency\[\], no vacío.

- instrumentTypes.

- allowedPaymentModes.

- chargeRules.

- creditorCapabilities.

- validity.

- status, generado.

Los estados son DRAFT, PENDING_APPROVAL, ENABLED, SUSPENDED y WITHDRAWN.

Las modalidades de pago son TOTAL, PARTIAL, ADVANCE y OVERDUE, según habilitación expresa.

Cada formato contiene código, versión y campos tipados de identificación.

Cada capacidad del acreedor identifica una operación registrada, versión de esquema y servicio o adaptador autorizado.
No admite URL arbitraria.

Las capacidades comprenden consulta, aceptación preliminar, preparación, confirmación, recuperación, cancelación y
regularización.

### **5.2. Interfaces**

| **Contrato** | **operationId**           | **Método y ruta**                                    |
|--------------|---------------------------|------------------------------------------------------|
| CI-05-007    | createCollectionAgreement | POST /v1/collection-agreements                       |
| CI-05-007    | actOnCollectionAgreement  | POST /v1/collection-agreements/{agreementId}/actions |
| CI-05-008    | getCollectionAgreement    | GET /v1/collection-agreements/{agreementId}          |
| CI-05-009    | lookupPaymentObligation   | POST /v1/payment-obligation-lookups                  |
| CI-05-010    | executePayment            | POST /v1/payments                                    |
| CI-05-011    | adjustPayment             | POST /v1/payments/{paymentId}/adjustments            |
| CI-05-012    | getPayment                | GET /v1/payments/{paymentId}                         |
| CI-05-012    | getPaymentAction          | GET /v1/payment-actions/{actionId}                   |

Las acciones de convenio son NEW_VERSION, SUBMIT, APPROVE, ENABLE, SUSPEND y WITHDRAW.

Aprobar y habilitar requieren aprobación del contenido exacto. No se habilita un convenio sin recuperación de
ejecuciones pendientes de aplicar.

### **5.3. Identificación de obligación**

Recibe convenio versionado, acreedor, identificador de obligación, finalidad y datos tipados conforme al formato del
convenio.

Devuelve:

- obligationReferenceId: UUID.

- creditorReference.

- externalObligationReference: ExternalReference.

- amount y currency, cuando se conocen.

- debtorReference, cuando está identificado.

- sourceVersion, cuando la fuente utiliza versión.

- dueDate, cuando corresponde.

- allowedPaymentModes.

- checkedAt.

- validUntil, cuando existe vigencia.

- verificationStatus: CONFIRMED, MANUAL, AMBIGUOUS o UNVERIFIED.

Una referencia ambigua o no verificada no se presenta como deuda confirmada.

Consultar no reserva ni cancela deuda.

### **5.4. PaymentOrder**

Contiene:

- payerReference.

- obligationReference.

- agreementReference.

- paymentMode.

- acceptedAmount: MoneyAmount positivo.

- currency.

- instrument.

- authorizationBasis.

- concepts: cargos del instrumento.

- occurrenceReference, cuando procede de recurrencia.

El instrumento es una variante excluyente:

| **Tipo**      | **Contenido**                                                                       |
|---------------|-------------------------------------------------------------------------------------|
| ACCOUNT_DEBIT | Cuenta y reserva cuando existe.                                                     |
| TRANSFER      | Instrucción autorizada o transferencia vinculada, de forma excluyente.              |
| CARD          | Referencia tokenizada del instrumento y autorización o presentación cuando existen. |

No recibe credenciales sensibles de tarjeta en registros ordinarios.

### **5.5. Ejecución y aplicación**

Ejecutar recibe contexto y PaymentOrder.

Devuelve paymentId, versión, referencias y estados:

| **Dimensión**     | **Valores**                                                               |
|-------------------|---------------------------------------------------------------------------|
| executionStatus   | NOT_STARTED, PENDING, CONFIRMED, REJECTED, UNCERTAIN.                     |
| applicationStatus | NOT_PREPARED, PREPARED, PENDING, APPLIED, CANCELLED, REQUIRES_RESOLUTION. |

Conserva separadamente aceptación preliminar, preparación de aplicación, ejecución del instrumento, aplicación del
acreedor y restituciones.

El recorrido comprende:

1.  Aceptación preliminar.

2.  Cobertura según instrumento.

3.  Aplicación preparada.

4.  Ejecución única.

5.  Aplicación definitiva.

6.  Recuperación o devolución, cuando corresponde.

Una ejecución confirmada con imputación desconocida permanece pendiente de aplicación. No vuelve a ejecutar el
instrumento.

### **5.6. Ajustes**

| **adjustmentType**      | **Datos específicos**                                  |
|-------------------------|--------------------------------------------------------|
| CANCEL_BEFORE_EXECUTION | Evidencia o barrera de cancelación.                    |
| RECTIFY_REFERENCE       | Referencia anterior, nueva y conformidad del acreedor. |
| REFUND                  | Importe, moneda y fundamento.                          |
| CORRECT_EFFECT          | Efecto original, alcance y aprobación exigible.        |

La respuesta distingue dinero, referencia e imputación.

Una rectificación informativa no ejecuta otro pago.

## **6. Mandatos y recurrencias**

### **6.1. Mandate**

Contiene:

- mandateId y version, generados.

- grantorPartyId.

- beneficiaryReference.

- accountId.

- purposeCode.

- amountMode: FIXED o VARIABLE_WITH_MAXIMUM.

- fixedAmount: para importe fijo.

- maximumPerOccurrence: para variable.

- currency.

- frequencyRule.

- validFrom.

- validUntil, cuando existe.

- acceptanceEvidence.

- status, generado.

Los estados son PENDING, ACTIVE, SUSPENDED, REVOKED y EXPIRED.

La frecuencia identifica unidad, intervalo positivo, anclaje y máximo de ocurrencias cuando se pacta.

Las unidades son DAYS, WEEKS y MONTHS.

No se admite frecuencia, importe o alcance más amplio que el aceptado.

### **6.2. Interfaces**

| **Contrato** | **operationId**      | **Método y ruta**                            |
|--------------|----------------------|----------------------------------------------|
| CI-05-013    | createMandate        | POST /v1/mandates                            |
| CI-05-013    | createMandateVersion | POST /v1/mandates/{mandateId}/versions       |
| CI-05-014    | changeMandateStatus  | POST /v1/mandates/{mandateId}/status-actions |
| CI-05-015    | getMandate           | GET /v1/mandates/{mandateId}                 |
| CI-05-015    | checkMandate         | POST /v1/mandates/{mandateId}/checks         |
| CI-05-016    | createSchedule       | POST /v1/schedules                           |
| CI-05-016    | actOnSchedule        | POST /v1/schedules/{scheduleId}/actions      |
| CI-05-017    | originateOccurrence  | POST /v1/occurrences                         |
| CI-05-018    | getSchedule          | GET /v1/schedules/{scheduleId}               |
| CI-05-018    | getOccurrence        | GET /v1/occurrences/{occurrenceId}           |

### **6.3. Constitución y estado**

Crear o modificar recibe contexto, definición y aceptación.

Cambiar beneficiario exige otro mandato. Ampliar importe, frecuencia o alcance exige nueva aceptación.

El mandato no reserva fondos para toda su vigencia.

Las acciones de estado son SUSPEND, RESUME y REVOKE.

Un mandato revocado no vuelve a activo. Revocar no devuelve pagos ni elimina deuda.

La respuesta distingue el impedimento de nuevas ocurrencias de la cancelación de instrucciones previamente admitidas.

### **6.4. Comprobación**

Recibe beneficiario, cuenta, finalidad, importe, moneda, ocurrencia, huella y fecha prevista.

Devuelve checkId, versión, resultado MATCHES, DOES_NOT_MATCH o PENDING, y observaciones.

No origina ocurrencia ni reserva fondos.

### **6.5. Schedule**

Contiene:

- scheduleId y version, generados.

- instructionType: TRANSFER o PAYMENT.

- authorizedInstructionReference.

- mandateReference, cuando corresponde.

- anchorDate.

- frequency: unidad e intervalo.

- calendarReference.

- adjustment: NONE, FOLLOWING o PRECEDING.

- localTime.

- zoneId.

- startDate.

- endCondition.

La finalización es una variante:

- ON_DATE: endDate.

- AFTER_OCCURRENCES: maximumOccurrences.

- OPEN: únicamente si la instrucción lo permite.

El horario ordinario es 09:00:00. Una transferencia única puede establecer otro.

Las acciones son MODIFY, SUSPEND, RESUME y TERMINATE.

Modificar exige contenido completo y aceptación de cambios. No acumula importes omitidos sin autorización.

### **6.6. Occurrence**

La creación utiliza:

- SCHEDULED: programación, versión e índice.

- CREDITOR_PRESENTATION: mandato, acreedor y referencia de cobro.

Ambas incluyen contexto, importe, moneda, instrucción tipada y huella.

La instrucción se ajusta a la variante de transferencia o pago definida en este módulo.

La ocurrencia programada se identifica por programación e índice, aunque dos fechas terminen ajustadas al mismo día.

Devuelve:

- occurrenceId.

- Versión de mandato admitida, si corresponde.

- Fecha programada y ajustada.

- Referencia de instrucción, cuando se originó.

- outcome: ORIGINATED, OMITTED, FAILED, PENDING o UNCERTAIN.

- Pendientes.

La admisión se ordena frente a revocación sobre el mandato.

La insuficiencia no genera reintentos automáticos.

Recuperar después de la fecha prevista requiere confirmación del ordenante, vinculada a contenido y ocurrencia original.
No permite duplicar una ejecución incierta.

## **7. Reversión de débitos**

### **7.1. Interfaces**

| **Contrato** | **Proveedor**           | **operationId**             | **Método y ruta**                                               |
|--------------|-------------------------|-----------------------------|-----------------------------------------------------------------|
| CI-05-019    | Mandatos y recurrencias | requestDebitReversal        | POST /v1/debit-reversal-requests                                |
| CI-05-020    | Pagos                   | actOnDebitReversalEconomics | POST /v1/debit-reversal-cases/{reversalCaseId}/economic-actions |

### **7.2. Solicitud**

Recibe contexto, solicitante, débito, pago, importe solicitado, moneda y motivo.

Recupera mandato, ocurrencia y restituciones previas.

Devuelve caso, cálculo de plazo y elegibilidad:

IN_PERIOD, OUT_OF_PERIOD o PENDING_VERIFICATION.

El plazo es de treinta días corridos desde el débito, excluyendo el día inicial y trasladando el final inhábil al
siguiente hábil.

Fuera de plazo conserva recepción y deriva a Reclamos.

### **7.3. Tratamiento económico**

Las acciones son:

- PREPARE_PROVISIONAL.

- CONFIRM_DEFINITIVE.

- REGULARIZE_PROVISIONAL.

- RECOVER_IMPROPER_PROVISIONAL.

Reciben contexto, pago, débito, recepción original, importe, moneda y verificaciones. Agregan resolución y aprobación
cuando el tratamiento las exige.

Devuelven:

- Importe reclamado.

- Restitución comprometida.

- Provisional acreditado.

- Devolución definitiva.

- Importe regularizado.

- Remanente restituible.

- Movimientos.

- Notificaciones al acreedor.

- Pendientes.

La identidad, facultades y correspondencia comprobadas habilitan la restitución provisional dentro del tercer día hábil
posterior a la recepción.

El definitivo regulariza el provisional sin acreditar nuevamente el mismo importe.

El recupero exige resolución y no concede descubierto.

## **8. Cheques y ECHEQ**

### **8.1. Estructuras**

ChequeBook contiene identidad, versión, cuenta, moneda, numeración inicial y final, estado y referencia de entrega
cuando existe.

Los números son cadenas de dígitos. Los rangos de una misma cuenta no se superponen.

Los estados son PREPARED, DELIVERY_PENDING, DELIVERED y RETIRED.

Cheque contiene:

- instrumentId y version, generados.

- support: PAPER o ECHEQ.

- chequeClass: COMMON o DEFERRED.

- drawerAccountReference.

- drawerPartyReference.

- initialBeneficiaryReference.

- currentHolderReference, comprobada por el proveedor.

- amount y currency.

- issueDate.

- paymentDate, obligatoria para diferido.

- circulationMode: TO_ORDER o NOT_TO_ORDER.

- contentHash.

- externalRegistryReference y registrySequence, cuando existe registro electrónico.

No admite portador, endoso en blanco, certificación ni pago parcial.

### **8.2. Interfaces**

| **Contrato** | **operationId**                 | **Método y ruta**                                          |
|--------------|---------------------------------|------------------------------------------------------------|
| CI-05-021    | createChequeBook                | POST /v1/cheque-books                                      |
| CI-05-021    | recordChequeBookDelivery        | POST /v1/cheque-books/{chequeBookId}/delivery-actions      |
| CI-05-022    | registerCheque                  | POST /v1/cheques                                           |
| CI-05-023    | actOnChequeCirculation          | POST /v1/cheques/{instrumentId}/circulation-actions        |
| CI-05-024    | commitChequeForDiscount         | POST /v1/cheques/{instrumentId}/discount-commitments       |
| CI-05-025    | resolveChequeDiscountCommitment | POST /v1/discount-commitments/{commitmentId}/resolutions   |
| CI-05-026    | presentCheque                   | POST /v1/cheques/{instrumentId}/presentations              |
| CI-05-027    | resolveChequePresentation       | POST /v1/cheque-presentations/{presentationId}/resolutions |
| CI-05-028    | actOnChequeInstrument           | POST /v1/cheques/{instrumentId}/instrument-actions         |
| CI-05-029    | getCheque                       | GET /v1/cheques/{instrumentId}                             |

### **8.3. Emisión y entrega**

Crear chequera recibe contexto, cuenta, moneda, rango solicitado y autorización.

Registrar entrega recibe contexto, destinatario, momento y evidencia.

Emitir chequera no emite sus instrumentos ni reserva dinero.

Registrar cheque recibe contexto y contenido. El ECHEQ exige contenido íntegro.

El papel puede conservar antecedentes pendientes explícitos, pero no presentarse sin contenido completo comprobado.

El común no tiene emisión futura. El diferido exige pago posterior a emisión y separación máxima de 365 días corridos.

### **8.4. Circulación**

| **Acción**              | **Datos específicos**                             |
|-------------------------|---------------------------------------------------|
| ACCEPT_BENEFICIARY      | Beneficiario y evidencia de aceptación.           |
| PROPOSE_TRANSFER        | Tenedor, destinatario y contenido de transmisión. |
| ACCEPT_TRANSFER         | Transmisión y aceptación del destinatario.        |
| REJECT_TRANSFER         | Transmisión y fundamento.                         |
| CANCEL_PENDING_TRANSFER | Transmisión y autorización.                       |
| REGISTER_ASSIGNMENT     | Cedente, cesionario, documentos y reconocimiento. |
| REGISTER_GUARANTEE      | Avalista, obligación avalada y evidencia.         |

Cada actuación incluye contexto, versión y huella.

Una transmisión pendiente bloquea depósito, descuento u otra transmisión incompatible.

El aval es total. La modalidad de circulación determina si corresponde transmisión a la orden o cesión documentada.

### **8.5. Descuento**

Comprometer recibe contexto, tenedor, solicitud de financiación y autorización.

Devuelve compromiso, instrumento, legitimación y estado. No acredita anticipo ni confirma financiación.

Resolver admite:

- CONFIRM_ASSIGNMENT: financiación y evidencia de cesión.

- RELEASE_NOT_EXECUTED: evidencia que descarta ejecución o pendiente incompatible.

La cesión confirmada vincula el cobro a la financiación.

### **8.6. Presentación y resultado**

Presentar recibe contexto, tenedor o financiación, destino de cobro, evidencia de recepción y presentación anterior
cuando corresponde.

Devuelve recepción, validación, fecha utilizada para oportunidad, presentación, ciclo y resultado:

RECEIVED, VALIDATION_PENDING, WAITING_PAYMENT_DATE, PRESENTED o REQUIRES_RESOLUTION.

El común admite presentación desde emisión hasta treinta días después. El diferido, desde la fecha de pago hasta treinta
días después.

El final inhábil se traslada al siguiente hábil.

El papel se valida en días hábiles hasta las 17:00. ECHEQ tiene recepción continua y corte de ciclo a las 18:00.

Resolver recibe contexto, instrumento, resultado, importe íntegro, moneda, participante y evidencia.

Los resultados son PAID, REJECTED y UNCERTAIN.

PAID exige evidencia monetaria íntegra. REJECTED exige causa y ausencia de ejecución desconocida.

Con importe 100 y capacidad 80 no se pagan 80.

Una nueva presentación exige instrucción nueva, plazo válido e intento anterior resuelto.

### **8.7. Medidas y soporte**

Las acciones son APPLY_MEASURE, MODIFY_MEASURE, LIFT_MEASURE, CANCEL_BY_AGREEMENT y RETURN_PAPER_SUPPORT.

Las medidas identifican autoridad, alcance, vigencia y fundamento.

La cancelación por acuerdo exige librador, tenedor actual y ausencia de presentación incierta.

Devolver papel identifica destinatario y evidencia física. No devuelve dinero.

## **9. Compensación y conciliación de pagos**

### **9.1. ParticipantMechanism**

Contiene:

- Participante, persona y contrato.

- Mecanismo y versión.

- Operaciones y monedas admitidas.

- Calendario y reglas de corte.

- Semántica de confirmaciones.

- Capacidades de recuperación.

- Reglas de compensación y liquidación.

- Vigencia y estado.

Los estados son DRAFT, PENDING_APPROVAL, ENABLED, SUSPENDED y RETIRED.

Las capacidades identifican adaptadores y operaciones registrados. No admiten destinos arbitrarios.

No se habilita un mecanismo sin consulta y resolución de incertidumbre.

### **9.2. Interfaces**

| **Contrato** | **operationId**                  | **Método y ruta**                                               |
|--------------|----------------------------------|-----------------------------------------------------------------|
| CI-05-030    | createPaymentNetworkParticipant  | POST /v1/payment-network-participants                           |
| CI-05-030    | actOnPaymentNetworkParticipant   | POST /v1/payment-network-participants/{participantId}/actions   |
| CI-05-031    | getPaymentNetworkParticipant     | GET /v1/payment-network-participants/{participantId}            |
| CI-05-032    | createPaymentExchange            | POST /v1/payment-exchanges                                      |
| CI-05-033    | incorporatePaymentNetworkMessage | POST /v1/payment-network-messages                               |
| CI-05-034    | getPaymentExchange               | GET /v1/payment-exchanges/{exchangeId}                          |
| CI-05-035    | createPaymentReconciliation      | POST /v1/payment-reconciliations                                |
| CI-05-035    | resolvePaymentDifference         | POST /v1/payment-differences/{differenceId}/resolutions         |
| CI-05-036    | determineClearingObligations     | POST /v1/clearing-cycles/{cycleId}/determinations               |
| CI-05-036    | getClearingObligation            | GET /v1/clearing-obligations/{obligationId}                     |
| CI-05-037    | recordClearingSettlementResult   | POST /v1/clearing-obligations/{obligationId}/settlement-results |

Las acciones de participante son NEW_VERSION, SUBMIT, APPROVE, ENABLE, SUSPEND y RETIRE.

Suspender nuevos envíos conserva recuperación de pendientes.

### **9.3. Intercambios y mensajes**

Un intercambio recibe contexto, operación de negocio, participante, mecanismo y modalidad:

- INSTRUCTION: contenido tipado del instrumento.

- QUERY: referencia de operación consultada.

- CANCELLATION: referencia, causa y autorización.

El esquema se identifica por mecanismo, versión y tipo de mensaje. Sus campos se validan antes de despachar; no se
admite contenido libre.

Devuelve intercambio, referencia externa, estado de despacho, acuses, resultados de negocio y pendientes.

La incorporación de mensajes requiere contexto, identidad externa de mensaje, operación externa, tipo, fecha, payload
tipado y evidencia.

El emisor se autentica como adaptador autorizado.

Los resultados son INCORPORATED, DUPLICATE, UNMATCHED y CONTRADICTORY.

Un mensaje no vinculado no se aplica por semejanza a otra operación.

### **9.4. Despacho y cancelación**

El servicio conserva intención, autorización de despacho y reclamación del trabajador.

Una cancelación confirmada antes del despacho impide un envío posterior de la misma referencia.

Si un trabajador ya reclamó el despacho, la cancelación permanece pendiente hasta resolverlo.

El adaptador distingue:

NOT_RECEIVED, RECEIVED, ACCEPTED, EXECUTED, CANCELLED_FINAL y UNCERTAIN.

NOT_RECEIVED aislado no demuestra que una petición atrasada no pueda llegar.

CANCELLED_FINAL exige una barrera que impida ejecutar esa referencia.

### **9.5. Conciliación**

Recibe contexto, ámbito, cortes comparables, fuentes y ciclo cuando corresponde.

Las diferencias son:

TEMPORAL_MISMATCH, MISSING_RECORD, DUPLICATE, AMOUNT_MISMATCH, CURRENCY_MISMATCH, STATE_CONTRADICTION y
SETTLEMENT_PENDING.

Resolver recibe contexto, conclusión, tratamiento, evidencia y aprobación para ajustes manuales.

Las conclusiones son EXPLAINED, CORRECTION_REQUIRED y UNRESOLVED.

La conciliación no autoriza débitos ni modifica por sí sola efectos de clientes.

### **9.6. Obligaciones y liquidación**

Determinar recibe contexto, versión de ciclo, mecanismo, participante, moneda, acuerdo y hechos confirmados.

Devuelve:

- obligationId y version.

- cycleReference.

- participantReference.

- mode: GROSS o NET.

- direction: PAY o RECEIVE.

- amount y currency.

- dueAt.

- composition.

- Ajustes relacionados.

Cada componente identifica hecho único, sentido e importe.

El corte es 18:00 de la zona institucional. Un hecho exactamente en el corte integra el siguiente intervalo.

Solo se netea igual contraparte y moneda con acuerdo expreso.

Un ciclo cerrado no se reescribe. Los ajustes integran otro ciclo relacionado.

Liquidaciones de tesorería incorpora resultados CONFIRMED, PARTIAL, FAILED o UNCERTAIN, con importe, moneda, fecha y
evidencia.

La parcialidad requiere habilitación del mecanismo.

Una liquidación fallida no rechaza retroactivamente operaciones de clientes.

## **10. Preparaciones y recuperación**

Las operaciones utilizan las preparaciones de Cuentas, Representación, Institución y Restricciones cuando el circuito
las exige.

La consulta informativa no sustituye una preparación.

Los efectos monetarios utilizan los intentos de Fondos. La admisión de ocurrencias se ordena localmente sobre el
mandato. El despacho se ordena localmente frente a cancelación.

Cada frontera conserva decisión y referencias. No existe una transacción SQL compartida entre servicios.

Las operaciones inciertas se consultan al detectarlas, a los cinco minutos, a los treinta minutos y por jornada
operativa mientras continúa la investigación.

Esas consultas no autorizan reejecución ni liberación automática.

Las respuestas tardías se incorporan al proceso original.

Los conflictos de evidencia se investigan sin sobrescribir hechos confirmados.

## **11. Eventos**

### **11.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                               | **Tema**                        | **Agregado**                |
|--------------|----------------------------------------|---------------------------------|-----------------------------|
| CI-05-038    | TransferChanged                        | banking.transfer.events         | Transferencia.              |
| CI-05-039    | PaymentOrReversalChanged               | banking.payment.events          | Pago.                       |
| CI-05-040    | MandateOrScheduleChanged               | banking.mandate.events          | Mandato o programación.     |
| CI-05-041    | OccurrenceOutcomeRecorded              | banking.mandate.events          | Ocurrencia.                 |
| CI-05-042    | ChequeChanged                          | banking.cheque.events           | Instrumento.                |
| CI-05-043    | PaymentExchangeOrReconciliationChanged | banking.payment-clearing.events | Intercambio o conciliación. |
| CI-05-044    | ClearingObligationChanged              | banking.payment-clearing.events | Obligación.                 |

La versión inicial de esquema es 1. La clave es el identificador del agregado.

### **11.2. CI-05-038 — TransferChanged**

Payload obligatorio:

- transferId: UUID.

- transferVersion: Version.

- transferType: tipo de transferencia.

- changeType: RECEIVED, EXECUTION_UPDATED, CANCELLED o RETURN_UPDATED.

- processingState: estado.

- principalAmount: MoneyAmount.

- currency: Currency.

- confirmedFacts: ConfirmedFact\[\].

- monetaryEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Campos condicionales:

- acceptedDestinationReference: para destino aceptado.

- exchangeReference: si existe intercambio.

- returnReference: al actualizar devolución.

aggregateType es TRANSFER.

### **11.3. CI-05-039 — PaymentOrReversalChanged**

Payload obligatorio:

- paymentId: UUID.

- paymentVersion: Version.

- changeType: EXECUTION_UPDATED, APPLICATION_UPDATED, ADJUSTMENT_UPDATED o REVERSAL_UPDATED.

- obligationReference: ObjectReference.

- instrumentType: ACCOUNT_DEBIT, TRANSFER o CARD.

- executionStatus.

- applicationStatus.

- amount: MoneyAmount.

- currency: Currency.

- monetaryEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

REVERSAL_UPDATED agrega caso, provisional acreditado, definitivo, regularizado y remanente restituible.

aggregateType es PAYMENT.

Una regularización de provisional no representa otra acreditación.

### **11.4. CI-05-040 — MandateOrScheduleChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: CREATED, VERSION_CHANGED, SUSPENDED, RESUMED, REVOKED o TERMINATED.

- status: estado del objeto.

- affectedOccurrenceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Con aggregateType igual a MANDATE agrega otorgante, beneficiario, cuenta, finalidad, moneda, modalidad e importe fijo o
máximo.

Con aggregateType igual a SCHEDULE agrega instrucción autorizada, anclaje, frecuencia, calendario y condición de
finalización.

Las dos variantes son excluyentes.

### **11.5. CI-05-041 — OccurrenceOutcomeRecorded**

Payload obligatorio:

- occurrenceId: UUID.

- occurrenceVersion: Version.

- originType: SCHEDULED o CREDITOR_PRESENTATION.

- amount: MoneyAmount.

- currency: Currency.

- contentHash: Hash.

- outcome: resultado de ocurrencia.

- pendingComponents: pendientes comunes.

La programada agrega programación, versión, índice, fecha contractual y ajustada.

La presentación agrega mandato, acreedor y referencia de cobro.

Incluye instrucción originada cuando existe.

aggregateType es OCCURRENCE.

### **11.6. CI-05-042 — ChequeChanged**

Payload obligatorio:

- instrumentId: UUID.

- instrumentVersion: Version.

- changeType: REGISTERED, CIRCULATION_CHANGED, DISCOUNT_COMMITTED, DISCOUNT_RESOLVED, PRESENTATION_UPDATED o
  MEASURE_CHANGED.

- support: soporte.

- amount: MoneyAmount.

- currency: Currency.

- currentHolderReference: ObjectReference.

- pendingComponents: pendientes comunes.

Agrega:

- registrySequence, para registro electrónico con secuencia.

- discountCommitmentReference, para descuento.

- presentationReference y resultado, para presentación.

- measureReference, para medida.

- transmissionReference, para transmisión.

aggregateType es CHEQUE.

### **11.7. CI-05-043 — PaymentExchangeOrReconciliationChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: EXCHANGE_UPDATED, MESSAGE_INCORPORATED, RECONCILIATION_UPDATED o DIFFERENCE_RESOLVED.

- evidenceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

La variante PAYMENT_EXCHANGE agrega operación de negocio, participante, mecanismo y estado de despacho. Incluye
referencia externa cuando existe.

La variante PAYMENT_RECONCILIATION agrega ámbito, cortes y diferencias relacionadas.

Una diferencia resuelta agrega referencia, clasificación, conclusión y tratamiento.

El aggregateType selecciona una de esas dos variantes.

### **11.8. CI-05-044 — ClearingObligationChanged**

Payload obligatorio:

- obligationId: UUID.

- obligationVersion: Version.

- cycleReference: VersionedReference.

- participantReference: ObjectReference.

- changeType: DETERMINED, ADJUSTMENT_LINKED o SETTLEMENT_UPDATED.

- mode: GROSS o NET.

- direction: PAY o RECEIVE.

- amount: MoneyAmount.

- currency: Currency.

- compositionReference: ObjectReference.

- fulfilledAmount y remainingAmount: MoneyAmount.

- pendingComponents: pendientes comunes.

Una actualización de liquidación agrega referencia y resultado. Un ajuste agrega obligación o ciclo original
relacionado.

aggregateType es CLEARING_OBLIGATION.

### **11.9. Consumidores y orden**

| **Información**               | **Consumidores autorizados**                                               |
|-------------------------------|----------------------------------------------------------------------------|
| Transferencias                | Pagos, Mandatos, Compensación, Contabilidad y atención.                    |
| Pagos y reversiones           | Mandatos, acreedores, Compensación, Contabilidad y atención.               |
| Mandatos y programaciones     | Pagos, Transferencias y Gestión administrativa.                            |
| Ocurrencias                   | Originantes, Pagos, Transferencias y atención.                             |
| Cheques                       | Préstamos y líneas, Garantías, Restricciones, Compensación y Contabilidad. |
| Intercambios y conciliaciones | Propietario de negocio, Tesorería y Gestión administrativa.                |
| Obligaciones                  | Liquidaciones de tesorería, Posiciones y liquidez y Contabilidad.          |

Documentos, Comunicaciones y Extractos reciben únicamente información necesaria para su función.

Las proyecciones comparan versiones. Un mensaje antiguo no reemplaza una versión posterior.

Si falta información relevante, el consumidor recupera el estado autoritativo antes de aplicar una decisión dependiente.

Los eventos no originan otro movimiento económico por el solo hecho de ser recibidos.

## **12. Seguridad y errores**

Se separan permisos para instruir, cancelar, devolver, administrar convenios, ejecutar pagos, coordinar imputaciones,
administrar mandatos, originar ocurrencias, resolver reversiones, operar instrumentos, administrar mecanismos,
incorporar mensajes y acreditar liquidaciones.

Un pagador distinto del obligado no obtiene acceso general a su información.

Un adaptador no adquiere facultades para debitar cuentas directamente.

Un permiso de consulta no autoriza devolución ni liberación.

| **Código**                           | **HTTP** | **Significado**                                   |
|--------------------------------------|----------|---------------------------------------------------|
| CONTENT_CONFLICT                     | 409      | Identidad con contenido diferente.                |
| VERSION_CONFLICT                     | 409      | Versión incompatible.                             |
| DESTINATION_MISMATCH                 | 409      | Destino distinto del aceptado.                    |
| CURRENCY_CONVERSION_REQUIRED         | 422      | Falta conversión identificada.                    |
| MECHANISM_NOT_ENABLED                | 422      | Mecanismo no habilitado para la operación.        |
| CANCELLATION_NOT_FINAL               | 409      | No puede afirmarse cancelación definitiva.        |
| RETURN_AMOUNT_EXCEEDED               | 422      | Supera el remanente recuperable.                  |
| PAYMENT_MODE_NOT_ALLOWED             | 422      | Modalidad no habilitada.                          |
| OBLIGATION_CHANGED                   | 409      | Cambió la obligación aceptada.                    |
| CREDITOR_PREPARATION_REQUIRED        | 422      | Falta preparación del acreedor.                   |
| MANDATE_SCOPE_MISMATCH               | 422      | Cobro fuera del mandato.                          |
| MANDATE_REVOKED                      | 422      | Mandato no utilizable.                            |
| OCCURRENCE_ALREADY_ORIGINATED        | 409      | Actuación incompatible sobre la misma ocurrencia. |
| LATE_EXECUTION_CONFIRMATION_REQUIRED | 422      | Falta autorización para recuperación tardía.      |
| RESTITUTION_AMOUNT_EXCEEDED          | 422      | Supera remanente restituible.                     |
| CHEQUE_CONTENT_INCOMPLETE            | 422      | Instrumento sin datos exigibles.                  |
| CHEQUE_PRESENTATION_OUT_OF_PERIOD    | 422      | Presentación fuera de plazo.                      |
| CHEQUE_INCOMPATIBLE_COMMITMENT       | 409      | Uso incompatible del instrumento.                 |
| CHEQUE_PARTIAL_PAYMENT_NOT_ALLOWED   | 422      | Pago parcial no admitido.                         |
| EXTERNAL_REFERENCE_CONFLICT          | 409      | Referencia externa con contenido incompatible.    |
| NETTING_AGREEMENT_REQUIRED           | 422      | Falta acuerdo de neteo.                           |
| DEPENDENCY_UNAVAILABLE               | 503      | Dependencia necesaria indisponible.               |

Una repetición coherente recupera el resultado original.

Una actuación durable pendiente devuelve 202 y seguimiento. Un error de comunicación no demuestra ausencia de ejecución.

## **13. Escenarios de aceptación**

| **Escenario**                                | **Resultado exigido**                                          |
|----------------------------------------------|----------------------------------------------------------------|
| Cambia el alias después de aceptar           | Conserva el destino aceptado.                                  |
| Transferencia interna con destino impedido   | No confirma únicamente el débito.                              |
| Saliente con acreditación incierta           | Mantiene cobertura y consulta.                                 |
| Cancelación confirmada antes de despacho     | Impide un envío tardío de la misma referencia.                 |
| Instrumento ejecutado e imputación incierta  | Recupera aplicación sin volver a ejecutar.                     |
| Revocación anterior a admisión de ocurrencia | No origina instrucción.                                        |
| Ocurrencia admitida antes de revocación      | Coordina cancelación según avance real.                        |
| Insuficiencia de fondos en programación      | No reintenta automáticamente ni acumula importes.              |
| Recuperación tardía                          | Exige confirmación vinculada a la ocurrencia.                  |
| Provisional de 100 y definitivo de 100       | Regulariza sin acreditar otros 100.                            |
| Cheque de 100 con capacidad 80               | No paga parcialmente.                                          |
| Transmisión pendiente                        | Impide descuento o depósito incompatible.                      |
| Mensaje externo repetido                     | Recupera sin repetir efecto.                                   |
| Mensaje externo contradictorio               | Conserva evidencia y abre resolución.                          |
| Pago 100 y cobro 70 con acuerdo de neteo     | Determina pago neto 30 en igual contraparte y moneda.          |
| Liquidación externa fallida                  | No revierte ficticiamente operaciones de clientes.             |
| Respuesta inicial perdida                    | Recupera por actionId e identidad económica original.          |
| Evento duplicado                             | No repite movimientos, restituciones ni inclusión compensable. |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
