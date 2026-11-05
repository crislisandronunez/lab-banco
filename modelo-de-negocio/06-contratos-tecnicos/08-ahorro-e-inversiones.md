# **Contratos técnicos — Módulo 8. Ahorro e inversiones**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**              | **Responsabilidad**                                                                     |
|---------------------------|-----------------------------------------------------------------------------------------|
| Depósitos a plazo         | Preparación, constitución, rendimiento, vencimiento, renovación y precancelación.       |
| Instrumentos de inversión | Definiciones, clases, habilitaciones, valores y eventos económicos.                     |
| Órdenes de inversión      | Instrucciones, recursos, ejecución, cancelación y coordinación de liquidaciones.        |
| Tenencias y valuación     | Cuentas de inversión, unidades, compromisos, custodia, derechos, valuación y traslados. |

Comprende los contratos **CI-08-001 a CI-08-047**.

Un depósito representa una obligación del banco. Una inversión representa unidades o derechos cuyo valor puede variar.

Fondos conserva la autoridad sobre el dinero de clientes. Las inversiones propias del banco pertenecen a Tesorería.

Los administradores, mercados y custodios externos operan dentro de la simulación.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las comprobaciones informativas mediante POST no crean preparaciones ni compromisos.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

La consulta recupera resultados e identificadores generados aunque se pierda la respuesta inicial.

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
| Quantity          | Cadena decimal no negativa, precisión 30 y escala máxima 12.           |
| ReferenceValue    | Cadena decimal positiva, precisión 30 y escala máxima 12.              |
| CalculationValue  | Cadena decimal con signo, precisión 30 y escala máxima 12.             |
| Rate              | Cadena decimal, precisión 24 y escala máxima 12.                       |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |

Los importes y cantidades ejecutados son positivos. Acumulados y remanentes admiten cero.

Las cantidades respetan precisión, mínimo e incremento del instrumento.

Los cálculos intermedios conservan al menos doce decimales. Los importes monetarios finales utilizan HALF_UP.

Una asignación de unidades por importe máximo trunca cuando corresponde para no superar el importe autorizado.

### **2.3. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Las referencias externas identifican participante o custodio, mecanismo cuando existe y referencia dentro de ese ámbito.

## **3. Estructuras compartidas**

### **3.1. Ownership**

Contiene:

- holderPartyIds: UUID\[\], no vacío y sin duplicados.

- actingMode: INDIVIDUAL o JOINT.

- authorizedOperators: referencias de operadores autorizados.

- contractualBasisReference: ObjectReference.

- jointRule: obligatoria para actuación conjunta.

La regla conjunta identifica actores o grupos admitidos, cantidad mínima de actores distintos y actos alcanzados.

La titularidad de la inversión no se deduce de la cuenta monetaria utilizada. Un autorizado no adquiere propiedad.

### **3.2. InstrumentReference**

Contiene:

- instrumentId: UUID.

- classCode: Code.

- sourceService: identificador del propietario.

- instrumentVersion: obligatoria cuando la definición interviene en la decisión.

La clase forma parte de la identidad de una posición.

### **3.3. EconomicComponent**

Contiene:

- conceptCode: Code.

- componentType: CAPITAL, INDEX_ADJUSTMENT, INTEREST, PRICE_PRINCIPAL, ACCRUED_INTEREST, FEE, TAX o COMPENSATION.

- amount: MoneyAmount.

- currency: Currency.

- calculationReference: cuando existe.

Los conceptos permanecen separados.

Al invocar Fondos se utiliza su clasificación monetaria, conservando concepto y referencia de cálculo. Capital y precio
principal se corresponden con PRINCIPAL; intereses con INTEREST.

### **3.4. MonetaryDestination**

Contiene:

- destinationType: INTERNAL_ACCOUNT o EXTERNAL_DESTINATION.

- accountReference: para cuenta interna.

- acceptedDestinationReference: para destino externo verificado.

- currency.

- authorizationReference.

Una instrucción de pago no convierte un destino no comprobado en válido.

### **3.5. Resultado**

Los comandos utilizan el resultado común y agregan:

- monetaryEffectReferences.

- custodyEffectReferences.

- committedResources.

- Estados particulares.

- Pendientes comunes.

Las respuestas distinguen recepción, ejecución, liquidación y disponibilidad.

## **4. Servicio Depósitos a plazo**

### **4.1. Modalidades**

| **Código**         | **Moneda y titular**  | **Plazo mínimo**  | **Salida anticipada** |
|--------------------|-----------------------|-------------------|-----------------------|
| TRADITIONAL        | ARS o USD             | 30 días corridos  | No.                   |
| NOMINAL_EARLY_EXIT | ARS                   | 180 días corridos | Desde el día 30.      |
| UVA                | ARS                   | 90 días corridos  | No.                   |
| UVA_EARLY_EXIT     | ARS, persona humana   | 90 días corridos  | Desde el día 30.      |
| UVA_EARLY_EXIT     | ARS, persona jurídica | 180 días corridos | Desde el día 90.      |

La precancelación es total y exige cinco días hábiles de preaviso.

Las unidades UVA son unidades de cálculo del depósito. No son cuotapartes negociables.

### **4.2. TermDepositPreparation**

Contiene:

- preparationId y version, generados.

- Producto versionado.

- Modalidad.

- Ownership.

- Capital y moneda.

- Plazo en días corridos.

- Cuenta de financiación.

- Destino de vencimiento.

- Fecha prevista de constitución.

- Vencimiento contractual.

- Fecha programada de pago.

- Tasa.

- Condiciones de salida anticipada, cuando se admite.

- Definición de índice, para UVA.

- Vigencia.

- Huella del contenido.

La tasa identifica valor, clase, período y base temporal.

La definición de índice identifica serie, fuente y reglas de fechas. Un valor futuro no se presenta como conocido.

### **4.3. TermDeposit**

Contiene:

- depositId y version, generados.

- constitutionId.

- Contrato.

- Preparación aceptada.

- Ownership.

- Capital original.

- Moneda.

- Constitución confirmada.

- Vencimiento contractual.

- Pago programado.

- Tasa.

- Instrucción de vencimiento.

- Unidades originales y valor inicial del índice, para UVA.

- Estado.

Los estados son CONSTITUTING, ACTIVE, MATURITY_PROCESSING, EARLY_EXIT_PROCESSING, PAYMENT_PENDING, RENEWED, PAID y
REQUIRES_RESOLUTION.

La obligación subsiste aunque falte una constancia documental.

### **4.4. Interfaces**

| **Contrato** | **operationId**                      | **Método y ruta**                                               |
|--------------|--------------------------------------|-----------------------------------------------------------------|
| CI-08-001    | prepareTermDeposit                   | POST /v1/term-deposit-preparations                              |
| CI-08-002    | constituteTermDeposit                | POST /v1/term-deposits                                          |
| CI-08-003    | calculatePreparedTermDeposit         | POST /v1/term-deposit-preparations/{preparationId}/calculations |
| CI-08-003    | calculateTermDeposit                 | POST /v1/term-deposits/{depositId}/calculations                 |
| CI-08-004    | changeTermDepositMaturityInstruction | POST /v1/term-deposits/{depositId}/maturity-instructions        |
| CI-08-005    | payTermDepositAtMaturity             | POST /v1/term-deposits/{depositId}/maturity-payments            |
| CI-08-006    | renewTermDeposit                     | POST /v1/term-deposits/{depositId}/renewals                     |
| CI-08-007    | actOnTermDepositEarlyExit            | POST /v1/term-deposits/{depositId}/early-exit-actions           |
| CI-08-008    | correctTermDeposit                   | POST /v1/term-deposits/{depositId}/corrections                  |
| CI-08-009    | getTermDeposit                       | GET /v1/term-deposits/{depositId}                               |
| CI-08-009    | getTermDepositAction                 | GET /v1/term-deposit-actions/{actionId}                         |

### **4.5. Preparación y constitución**

Preparar recibe contexto, producto, modalidad, titulares, capital, moneda, plazo, cuenta de financiación, destino e
instrucción inicial.

Devuelve condiciones, cálculo, documentos y requisitos de aceptación.

Esta preparación comercial no equivale a una preparación de autoridad ni a una reserva monetaria.

Constituir admite a Solicitudes de productos. Recibe contexto, constitución, contrato, preparación aceptada,
conformidad, titulares, capital, moneda y cuentas relacionadas.

Utiliza fondos propios, sin descubierto.

Un débito confirmado con constitución pendiente exige completar o restituir mediante el circuito correspondiente. No
permite repetir el débito.

Cambiar fecha con consecuencias económicas exige revisar cálculo y aceptación.

### **4.6. Cálculos**

Reciben contexto, finalidad de cálculo, fecha, condiciones y valores de índice cuando corresponden.

Devuelven:

- Identidad y versión de cálculo.

- Huella.

- Días computados.

- Capital original.

- Unidades y valores de índice, para UVA.

- Actualización.

- Interés.

- Tributos.

- Compensación, cuando existe.

- Neto.

- Fuentes.

- Resultado DETERMINED, PROVISIONAL o INCOMPLETE_INPUT.

El nominal utiliza interés simple Actual/365, incluyendo constitución y excluyendo fecha final.

En UVA se conservan unidades originales y se separan actualización e interés.

La falta de un índice necesario mantiene pendiente la liquidación definitiva.

### **4.7. Vencimiento y renovación**

Las instrucciones son:

- PAY_TOTAL.

- RENEW_CAPITAL_PAY_NET_RETURN.

- RENEW_CAPITAL_AND_NET_RETURN.

Cambiar instrucción recibe contexto, aceptación y destino cuando corresponde. Se ordena frente al procesamiento del
vencimiento.

Sin renovación aceptada se prepara pago total.

En UVA, renovar capital significa capital actualizado.

Pagar recibe ocurrencia de vencimiento, cálculo, fecha programada y destino.

Un vencimiento inhábil se paga el siguiente hábil con remuneración hasta esa fecha. Una demora adicional del banco se
identifica sin remunerar dos veces el mismo período.

Renovar recibe la misma ocurrencia, instrucción, cálculo y nuevas condiciones aceptadas.

Devuelve depósito anterior, sucesor, capital renovado, rendimiento pagadero y pendientes.

Los fondos no quedan simultáneamente disponibles para otra operación.

### **4.8. Precancelación y corrección**

Las acciones de salida son:

- REQUEST: recepción y petición.

- ACCEPT_QUOTE: liquidación y aceptación.

- EXECUTE: liquidación vigente y destino.

La fecha admisible es la más tardía entre permanencia mínima y preaviso, ajustada al siguiente hábil.

Si alcanza el vencimiento, se utiliza el tratamiento ordinario.

La salida utiliza capital original y tasa de salida fijada al constituir. En UVA precancelable no suma actualización de
vencimiento ni otra penalidad.

Corregir identifica referencia original, tipo, evidencia, valores tipados y aprobación.

Los tipos son METADATA, CALCULATION, MATURITY_INSTRUCTION y ECONOMIC_EFFECT.

No sustituye silenciosamente condiciones históricas ni repite un pago confirmado.

## **5. Servicio Instrumentos de inversión**

### **5.1. InvestmentInstrument**

Contiene:

- instrumentId y version, generados.

- Código y nombre.

- instrumentType: OPEN_END_FUND o FIXED_INCOME_SECURITY.

- classCode.

- Emisor o administrador.

- Moneda.

- quantityScale: entero entre cero y doce.

- minimumQuantity y quantityStep: Quantity positivas.

- quotationBasis: PER_UNIT o PER_100_NOMINAL.

- Operaciones permitidas.

- Clasificación de riesgo.

- Calendarios.

- Reglas de liquidación y traslado.

- Documentos.

- Vigencia.

- Estado.

Los estados son DRAFT, PENDING_APPROVAL, APPROVED, ENABLED, SUSPENDED y WITHDRAWN.

Los fondos definen administrador, clase y regla de cuotaparte.

Los títulos definen nominal, cupón, amortización y vencimiento.

No se habilitan derivados, apalancamiento, ventas en corto ni garantías de margen.

### **5.2. Interfaces**

| **Contrato** | **operationId**                     | **Método y ruta**                                                 |
|--------------|-------------------------------------|-------------------------------------------------------------------|
| CI-08-010    | createInvestmentInstrument          | POST /v1/investment-instruments                                   |
| CI-08-010    | actOnInvestmentInstrumentEnablement | POST /v1/investment-instruments/{instrumentId}/enablement-actions |
| CI-08-011    | changeInvestmentInstrument          | POST /v1/investment-instruments/{instrumentId}/changes            |
| CI-08-012    | getInvestmentInstrument             | GET /v1/investment-instruments/{instrumentId}                     |
| CI-08-013    | recordInvestmentReferenceValue      | POST /v1/investment-reference-values                              |
| CI-08-014    | defineInvestmentInstrumentEvent     | POST /v1/investment-instruments/{instrumentId}/events             |

Las acciones de habilitación son SUBMIT, APPROVE, ENABLE y WITHDRAW.

Los cambios son NEW_VERSION, SUSPEND y RESUME.

Suspender compras no bloquea automáticamente rescates ni derechos.

### **5.3. Valores y eventos económicos**

Un valor de referencia contiene instrumento versionado, tipo, valor, moneda, fecha aplicable, fuente y evidencia.

Los tipos son:

- UNIT_VALUE.

- MARKET_PRICE.

- ACCRUED_INTEREST.

La cuotaparte y el precio son positivos. El interés corrido admite cero.

Devuelve identidad, versión y estado VALIDATED, PENDING_VERIFICATION u OBSERVED.

Una corrección identifica el valor sustituido y fundamento.

Definir evento económico recibe contexto, instrumento, tipo, fecha de derecho, pago, moneda, fuente, evidencia y regla
de cálculo registrada.

Los tipos son COUPON y PRINCIPAL_AMORTIZATION. Otros cambios del instrumento se tramitan mediante nueva versión.

Una corrección referencia el evento anterior.

Definir un derecho no demuestra cobro. Un fondo acumulativo no genera un dividendo adicional ficticio.

## **6. Cuenta de inversión y órdenes**

### **6.1. InvestmentAccount**

Contiene:

- investmentAccountId y version.

- constitutionId.

- Contrato.

- Ownership.

- Cuentas monetarias vinculadas.

- Custodia.

- Capacidades.

- Estado.

Los estados son CONSTITUTING, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

La cuenta no mantiene dinero como autoridad monetaria.

### **6.2. Interfaces de cuenta**

| **Contrato** | **operationId**                 | **Método y ruta**                                         |
|--------------|---------------------------------|-----------------------------------------------------------|
| CI-08-015    | constituteInvestmentAccount     | POST /v1/investment-accounts                              |
| CI-08-015    | changeInvestmentAccount         | POST /v1/investment-accounts/{accountId}/changes          |
| CI-08-016    | getInvestmentAccount            | GET /v1/investment-accounts/{accountId}                   |
| CI-08-016    | checkInvestmentAccountOperation | POST /v1/investment-accounts/{accountId}/operation-checks |

Constituir recibe contexto y datos contractuales no generados.

Modificar recibe contexto, modalidad OWNERSHIP, AUTHORIZED_OPERATORS, MONETARY_ACCOUNTS o CAPABILITIES, contenido
completo de la dimensión y fundamento formalizado.

La comprobación recibe actores, acto, instrumento, cantidad o importe, huella, momento y finalidad.

Devuelve ADMITTED, PREVENTED o PENDING, versión y fundamentos autorizados.

No compromete unidades ni sustituye representación.

### **6.3. InvestmentOrder**

Contiene:

- orderId y version, generados.

- Cuenta de inversión.

- Instrumento y clase.

- orderType.

- Moneda.

- Fecha solicitada.

- Condiciones aceptadas.

- Autorización.

- Adecuación inversora.

- Estado.

- Recursos vinculados cuando están confirmados.

- Datos específicos de modalidad.

| **Modalidad**     | **Datos específicos**                         |
|-------------------|-----------------------------------------------|
| FUND_SUBSCRIPTION | Importe máximo autorizado.                    |
| FUND_REDEMPTION   | Cantidad aceptada o instrucción de totalidad. |
| SECURITY_BUY      | Cantidad, precio límite y rueda.              |
| SECURITY_SELL     | Cantidad, precio límite y rueda.              |

Una instrucción de rescate total fija la cantidad bajo control de posición al preparar recursos y conserva la evidencia
de esa determinación.

Un rescate solicitado por importe exige estimación de cantidad y confirmación antes de ejecutar.

Los estados son RECEIVED, VALIDATING, RESOURCES_PENDING, READY, DISPATCHED, PARTIALLY_EXECUTED, EXECUTED,
CANCELLATION_PENDING, CANCELLED, EXPIRED, SETTLEMENT_PENDING, COMPLETED, REJECTED y REQUIRES_RESOLUTION.

Los hechos de ejecución y liquidación se conservan por componente; el estado global no oculta parcialidades.

### **6.4. Interfaces de orden y recursos**

| **Contrato** | **operationId**               | **Método y ruta**                                           |
|--------------|-------------------------------|-------------------------------------------------------------|
| CI-08-017    | createInvestmentOrder         | POST /v1/investment-orders                                  |
| CI-08-018    | actOnInvestmentOrderResources | POST /v1/investment-orders/{orderId}/resource-actions       |
| CI-08-019    | commitInvestmentUnits         | POST /v1/investment-unit-commitments                        |
| CI-08-019    | actOnInvestmentUnitCommitment | POST /v1/investment-unit-commitments/{commitmentId}/actions |
| CI-08-027    | getInvestmentOrder            | GET /v1/investment-orders/{orderId}                         |

Crear recibe contexto y contenido no generado.

Los recursos admiten PREPARE, ADJUST y RELEASE_CANCELLED.

Una compra máxima de 1000 con cargos máximos autorizados de 10 exige cobertura 1010. Nunca se amplía el importe aceptado
para completar cargos desconocidos.

Solo se utilizan fondos propios.

### **6.5. Compromiso de unidades**

Proveedor: Tenencias y valuación.

Crear recibe contexto, cuenta, instrumento, cantidad y operación destinataria. Agrega versión esperada cuando forma
parte de la instrucción.

Devuelve compromiso, versión, cantidad inicial, consumida, liberada, remanente y disponibilidad.

Las acciones son INCREASE, REDUCE y RELEASE, con cantidad objetivo y evidencia pertinente.

La cantidad comprometida no respalda otra venta, rescate o traslado.

Los compromisos inciertos no se liberan por vencimiento.

## **7. Ejecución y liquidación de órdenes**

### **7.1. Interfaces**

| **Contrato** | **operationId**                        | **Método y ruta**                                            |
|--------------|----------------------------------------|--------------------------------------------------------------|
| CI-08-020    | executeFundSubscription                | POST /v1/investment-orders/{orderId}/fund-subscriptions      |
| CI-08-021    | executeFundRedemption                  | POST /v1/investment-orders/{orderId}/fund-redemptions        |
| CI-08-022    | executeSecurityOrder                   | POST /v1/investment-orders/{orderId}/security-executions     |
| CI-08-023    | recordInvestmentExternalResult         | POST /v1/investment-orders/{orderId}/external-results        |
| CI-08-024    | cancelInvestmentOrder                  | POST /v1/investment-orders/{orderId}/cancellation-actions    |
| CI-08-025    | coordinateInvestmentSettlement         | POST /v1/investment-executions/{executionId}/settlements     |
| CI-08-026    | recordInvestmentSettlementConfirmation | POST /v1/investment-settlements/{settlementId}/confirmations |

### **7.2. Fondos y títulos**

Suscribir recibe contexto, reserva y fecha aplicable.

Devuelve aceptación del administrador, valor de cuotaparte, unidades, importe aplicado, residuo y liquidación.

Antes de las 15:00 de un hábil utiliza esa fecha; desde las 15:00, el siguiente hábil.

Rescatar recibe cantidad confirmada, compromiso y destino monetario.

Devuelve valor aplicable, bruto, cargos, tributos, neto y pendientes.

Ejecutar títulos recibe rueda, cantidad, precio límite y recursos.

El precio se expresa por cien nominales. No compra por encima del límite ni vende por debajo.

Cada ejecución parcial conserva identidad, cantidad, precio, interés corrido, cargos y remanente.

El remanente vence al finalizar la rueda, sujeto a resolución de ejecuciones pendientes.

### **7.3. Resultados externos**

Recibe contexto, participante, referencia externa, resultado, momento y evidencia.

Los resultados son ACKNOWLEDGED, ACCEPTED, EXECUTED, REJECTED y UNCERTAIN.

EXECUTED exige cantidad, importe, moneda y referencia de precio o cuotaparte según el instrumento.

Un resultado rechazado no incorpora importes como ejecutados.

Devuelve incorporación, duplicado, discrepancia o seguimiento.

La identidad externa incluye participante y referencia de ejecución.

### **7.4. Cancelación**

Las acciones son REQUEST_CANCELLATION y CHECK_EXPIRATION.

Devuelve ejecutado, remanente cancelado, recursos liberados y pendientes.

Una petición no prueba cancelación externa.

La cancelación definitiva debe impedir ejecuciones tardías de la referencia cancelada antes de liberar su cobertura.

### **7.5. Liquidación**

Coordinar recibe ejecución, obligaciones monetarias y de instrumentos, participantes, fecha, mecanismo y recursos.

Cada componente tiene componentId, propietario, sentido, importe o cantidad y condición de cumplimiento.

Los títulos utilizan entrega contra pago.

Las confirmaciones reciben componente, resultado, fecha y evidencia.

Las variantes son:

- MONEY: importe y moneda.

- INSTRUMENTS: cantidad e instrumento.

Los resultados son CONFIRMED, PARTIAL, FAILED y UNCERTAIN. La parcialidad requiere habilitación del mecanismo.

Una recepción de instrumentos sin contraprestación confirmada no demuestra liquidación completa ni disponibilidad
automática.

Las unidades y el dinero se habilitan únicamente conforme al cumplimiento comprobado de sus condiciones.

## **8. Tenencias y valuación**

### **8.1. InvestmentPosition**

Contiene:

- positionId: UUID generado.

- Cuenta de inversión.

- Instrumento y clase.

- Custodia.

- positionVersion.

- movementSequence, cuando existen movimientos.

- Cantidad registrada.

- Cantidad disponible.

- Compromisos.

- Restricciones.

- Liquidaciones pendientes.

- Cantidades en tránsito.

- Corte.

La identidad conceptual corresponde a cuenta, instrumento, clase y custodia.

Los componentes identifican superposiciones. Tránsito y compromisos no se suman como activos independientes.

La versión de posición comprende cambios sin movimiento; la secuencia identifica movimientos de unidades.

### **8.2. Interfaces**

| **Contrato** | **operationId**                    | **Método y ruta**                                             |
|--------------|------------------------------------|---------------------------------------------------------------|
| CI-08-028    | recordInvestmentPositionMovement   | POST /v1/investment-position-movements                        |
| CI-08-029    | actOnInvestmentPositionRestriction | POST /v1/investment-position-restrictions                     |
| CI-08-030    | listInvestmentPositions            | GET /v1/investment-positions                                  |
| CI-08-030    | listInvestmentPositionMovements    | GET /v1/investment-position-movements                         |
| CI-08-031    | valueInvestmentPositions           | POST /v1/investment-valuations                                |
| CI-08-032    | determineInvestmentEntitlements    | POST /v1/investment-event-entitlements                        |
| CI-08-033    | fulfillInvestmentEntitlement       | POST /v1/investment-entitlements/{entitlementId}/fulfillments |

### **8.3. Movimientos y restricciones**

Registrar movimiento recibe efecto único, cuenta, instrumento, sentido IN o OUT, cantidad, hecho de origen, fecha y
evidencia de liquidación.

Si existe compromiso, lo consume conjuntamente con el movimiento local.

Devuelve movimiento, secuencia, posición y remanente.

Las restricciones admiten a Restricciones y utilizan APPLY, MODIFY o LIFT.

Identifican medida, componente, cuenta, instrumento, fecha y alcance:

- QUANTITY: cantidad.

- DEFINED_SCOPE: actos o alcance expresamente determinados.

Levantar una causa no elimina otras.

### **8.4. Valuación**

Recibe cortes de posición y valores de referencia.

Una conversión informativa agrega moneda de presentación y cotización versionada.

Devuelve valuación por posición, cantidad, precio, interés corrido separado, importe, fuente, antigüedad y faltantes.

Un valor histórico se identifica como tal. La ausencia de precio no se sustituye por cero.

La valuación no representa dinero disponible.

### **8.5. Derechos económicos**

Determinar recibe evento, fecha de derecho, cortes de posición, reglas de custodia, tributos y traslados pertinentes.

Devuelve derechos, beneficiarios, bases, importes o cantidades, vencimientos, efectos sobre nominal y residuos.

El derecho conserva su versión, origen y estado DETERMINED, PARTIALLY_FULFILLED, FULFILLED o REQUIRES_RESOLUTION.

Cumplir utiliza efecto único y variante:

- MONEY_RECEIPT: importe, moneda y evidencia del ingreso.

- INSTRUMENT_DELIVERY: cantidad, instrumento y evidencia.

La respuesta distingue recibido, aplicado al beneficiario, remanente y pendientes.

Reconocer un derecho no acredita cobro.

Un traslado no genera dos derechos por las mismas unidades. Un custodio anterior que recibe rendimiento conserva la
obligación residual de entregarlo al titular.

## **9. Conciliación y correcciones**

| **Contrato** | **operationId**            | **Método y ruta**                                |
|--------------|----------------------------|--------------------------------------------------|
| CI-08-034    | reconcileInvestmentOrders  | POST /v1/investment-order-reconciliations        |
| CI-08-035    | reconcileInvestmentCustody | POST /v1/investment-custody-reconciliations      |
| CI-08-036    | correctInvestmentOrder     | POST /v1/investment-orders/{orderId}/corrections |
| CI-08-037    | correctInvestmentPosition  | POST /v1/investment-position-corrections         |

Las conciliaciones reciben contexto, ámbito, cortes comparables y fuentes.

Las diferencias identifican objeto, clasificación, cantidad o importe discrepante, responsables y seguimiento.

Las clasificaciones son MISSING_RECORD, DUPLICATE, QUANTITY_MISMATCH, AMOUNT_MISMATCH, SETTLEMENT_PENDING,
TRANSIT_MISMATCH y ENTITLEMENT_MISMATCH.

Una ejecución externa omitida se recupera sin originar otra orden.

La diferencia de custodia no autoriza crear unidades.

Corregir orden separa tratamiento monetario, custodia y liquidación. No modifica el consentimiento histórico ni borra
una ejecución externa.

Corregir posición utiliza targetType:

- POSITION: movimiento y ajuste de cantidad comprobado.

- VALUATION: fuente o cálculo corregido.

- ENTITLEMENT: derecho y tratamiento corregido.

Los ajustes manuales requieren preparación y aprobación separadas.

Corregir valuación no ejecuta compraventa. Una duplicación interna no se corrige mediante venta ficticia.

Las conciliaciones se ejecutan diariamente y ante incertidumbre.

## **10. Traslados y cierre**

### **10.1. CustodyTransfer**

Contiene:

- transferId y version.

- transferType: INTERNAL, OUTGOING o INCOMING.

- Cuentas y custodios de origen y destino.

- Ownership.

- Instrumento y clase.

- Cantidad solicitada.

- Cantidad comprometida.

- Cantidad enviada.

- Cantidad recibida.

- Cantidad retornada.

- Cantidad cancelada.

- Pendientes.

Las cantidades se relacionan por componentes. No se computa tránsito como tenencia duplicada.

### **10.2. Interfaces**

| **Contrato** | **operationId**             | **Método y ruta**                                          |
|--------------|-----------------------------|------------------------------------------------------------|
| CI-08-038    | createCustodyTransfer       | POST /v1/custody-transfers                                 |
| CI-08-039    | recordCustodyTransferResult | POST /v1/custody-transfers/{transferId}/results            |
| CI-08-040    | resolveCustodyTransfer      | POST /v1/custody-transfers/{transferId}/resolution-actions |
| CI-08-041    | getCustodyTransfer          | GET /v1/custody-transfers/{transferId}                     |
| CI-08-042    | closeInvestmentAccount      | POST /v1/investment-accounts/{accountId}/closures          |

### **10.3. Solicitud y resultados**

Solicitar recibe cuentas, custodios, titularidad, instrumento, cantidad, fecha, autorización, cargos y medidas
aplicables.

La titularidad completa coincide, incluidos cotitulares. El destino admite instrumento y clase.

El traslado no cambia propiedad.

Los resultados son ACCEPTED, SENT, RECEIVED, REJECTED y RETURNED.

Cada hecho identifica referencia externa, cantidad, instrumento, titularidad, fecha y evidencia.

Una salida confirmada reduce origen y genera tránsito. Un rechazo posterior no restituye disponibilidad sin retorno
comprobado.

En traslado interno, salida y entrada se confirman conjuntamente en Tenencias.

### **10.4. Resolución y cierre**

Las acciones son:

- CANCEL_UNSENT.

- QUERY_PENDING.

- CORRECT_DESTINATION.

- ARRANGE_RETURN.

Una corrección de destino exige nueva comprobación y autorización; no redirige unilateralmente unidades enviadas.

Devuelve cancelado, liberado, recibido, retornado y tránsito pendiente.

Cerrar cuenta recibe terminación contractual e instrucciones identificadas de venta, rescate o traslado.

No autoriza vender a cualquier precio.

La respuesta distingue limitación de usos, cierre, régimen residual y vía de entrega de derechos posteriores.

Los depósitos a plazo conservan sus acuerdos independientes.

## **11. Preparaciones, horarios y recuperación**

### **11.1. Facultades de la cuenta de inversión**

Tenencias ofrece:

| **operationId**                 | **Método y ruta**                                                                      |
|---------------------------------|----------------------------------------------------------------------------------------|
| prepareInvestmentAccountAction  | POST /v1/investment-account-action-preparations                                        |
| getInvestmentAccountPreparation | GET /v1/investment-account-action-preparations/{preparationId}                         |
| notifyInvestmentAccountDecision | POST /v1/investment-account-action-preparations/{preparationId}/decision-notifications |

Utilizan la preparación común y vinculan cuenta, actores, acto, instrumento, importe o cantidad y contenido exacto.

Preparan únicamente facultades propias de la cuenta. No comprometen unidades ni dinero.

Los cambios incompatibles se ordenan frente a las preparaciones y solicitan resolver intentos existentes.

También se utilizan las autoridades de Representación, Institución, Cuentas y Restricciones cuando corresponden.

### **11.2. Horarios**

| **Actuación**             | **Convención**                                             |
|---------------------------|------------------------------------------------------------|
| Constitución de depósitos | Hábiles, desde las 09:00 hasta antes de las 17:00.         |
| Recepción de órdenes      | Todos los días.                                            |
| Corte de fondos           | 15:00 de días hábiles.                                     |
| Negociación de títulos    | Días de mercado, desde las 11:00 hasta antes de las 17:00. |
| Liquidación ordinaria     | Siguiente hábil aplicable.                                 |
| Recepción de traslados    | Todos los días.                                            |
| Gestión de traslados      | Hábiles, desde las 09:00 hasta antes de las 17:00.         |

La zona es America/Argentina/Buenos_Aires.

Fuera de horario se identifica fecha de procesamiento. No se informa ejecución ni se conserva una cotización vencida.

### **11.3. Recuperación**

Ante incertidumbre externa se consulta al detectarla, a los cinco minutos y a los treinta minutos. Después continúa
investigación por jornada operativa.

No se liberan coberturas ni unidades por timeout.

Se conserva identidad de orden, ejecución, liquidación, movimiento y derecho.

No se admite descubierto para invertir, venta de unidades inexistentes ni uso de cobros pendientes como disponibilidad
confirmada.

Los activos de clientes no respaldan obligaciones propias del banco.

## **12. Eventos**

### **12.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                                  | **Tema**                             | **Agregado**                           |
|--------------|-------------------------------------------|--------------------------------------|----------------------------------------|
| CI-08-043    | TermDepositChanged                        | banking.term-deposit.events          | Depósito.                              |
| CI-08-044    | InvestmentInstrumentOrEventChanged        | banking.investment-instrument.events | Instrumento, valor o evento económico. |
| CI-08-045    | InvestmentOrderOrSettlementChanged        | banking.investment-order.events      | Orden.                                 |
| CI-08-046    | InvestmentPositionOrEntitlementChanged    | banking.investment-position.events   | Posición o derecho.                    |
| CI-08-047    | CustodyTransferOrInvestmentAccountChanged | banking.investment-position.events   | Cuenta o traslado.                     |

La versión inicial de esquema es 1. La clave es el identificador del agregado.

### **12.2. CI-08-043 — TermDepositChanged**

Payload obligatorio:

- depositId: UUID.

- depositVersion: Version.

- changeType: CONSTITUTED, CALCULATION_RECORDED, INSTRUCTION_CHANGED, PAYMENT_UPDATED, RENEWED, EARLY_EXIT_UPDATED o
  CORRECTED.

- modality: modalidad de depósito.

- currency.

- originalCapital: MoneyAmount.

- status: estado del depósito.

- monetaryEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Campos condicionales:

- Unidades e índice inicial para UVA constituido.

- Cálculo para liquidación.

- Instrucción para cambio de vencimiento.

- Pago y remanente para cumplimiento.

- Sucesor y capital renovado para renovación.

- Solicitud, liquidación y aceptación para salida anticipada.

aggregateType es TERM_DEPOSIT.

### **12.3. CI-08-044 — InvestmentInstrumentOrEventChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- instrumentReference: InstrumentReference.

- changeType: DEFINITION_CHANGED, ENABLEMENT_CHANGED, REFERENCE_VALUE_RECORDED o ECONOMIC_EVENT_DEFINED.

- pendingComponents: pendientes comunes.

Variantes excluyentes:

| **aggregateType**          | **Campos específicos**                                     |
|----------------------------|------------------------------------------------------------|
| INVESTMENT_INSTRUMENT      | Estado, operaciones afectadas y vigencia.                  |
| INVESTMENT_REFERENCE_VALUE | Tipo, valor, moneda, fecha, fuente y verificación.         |
| INVESTMENT_ECONOMIC_EVENT  | Tipo económico, fecha de derecho, pago y regla versionada. |

Las correcciones agregan referencia sustituida y motivo.

### **12.4. CI-08-045 — InvestmentOrderOrSettlementChanged**

Payload obligatorio:

- orderId: UUID.

- orderVersion: Version.

- investmentAccountId: UUID.

- instrumentReference: InstrumentReference.

- changeType: RECEIVED, RESOURCES_UPDATED, EXECUTION_UPDATED, CANCELLATION_UPDATED, SETTLEMENT_UPDATED o CORRECTED.

- orderType.

- status: estado de orden.

- resourceReferences: ObjectReference\[\].

- monetaryEffectReferences: ObjectReference\[\].

- custodyEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

La ejecución agrega identidad, cantidad, importe y moneda.

La liquidación agrega referencia y resultados por componente.

La cancelación agrega remanente cancelado y cantidades pendientes de resolución.

aggregateType es INVESTMENT_ORDER.

### **12.5. CI-08-046 — InvestmentPositionOrEntitlementChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- investmentAccountId: UUID.

- instrumentReference: InstrumentReference.

- changeType: MOVEMENT_RECORDED, COMMITMENT_CHANGED, RESTRICTION_CHANGED, VALUATION_RECORDED, ENTITLEMENT_DETERMINED,
  ENTITLEMENT_FULFILLED o CORRECTED.

- pendingComponents: pendientes comunes.

Con aggregateType igual a INVESTMENT_POSITION, agrega:

- Cantidad registrada y disponible.

- Referencias de compromisos, restricciones y tránsito.

- Movimiento y secuencia, cuando existe.

- Valuación, cuando es el hecho comunicado.

Con aggregateType igual a INVESTMENT_ENTITLEMENT, agrega:

- Evento de origen.

- Beneficiario.

- Modalidad monetaria o de instrumentos.

- Importe o cantidad.

- Estado.

- Efectos y remanente de cumplimiento.

Una valuación no representa dinero. Un derecho determinado no demuestra cobro.

### **12.6. CI-08-047 — CustodyTransferOrInvestmentAccountChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: ACCOUNT_CONSTITUTED, ACCOUNT_CHANGED, ACCOUNT_CLOSURE_UPDATED, TRANSFER_CREATED, TRANSFER_UPDATED o
  TRANSFER_RESOLVED.

- ownershipReference: ObjectReference.

- pendingComponents: pendientes comunes.

Con aggregateType igual a INVESTMENT_ACCOUNT, agrega capacidades, estado y obligaciones residuales.

Con aggregateType igual a CUSTODY_TRANSFER, agrega:

- Modalidad de traslado.

- Cuentas y custodios relacionados.

- Instrumento.

- Cantidades solicitada, comprometida, enviada, recibida, retornada y cancelada.

- Referencias de hechos externos.

- Derechos residuales.

La recepción y el retorno se acreditan separadamente.

### **12.7. Consumidores y orden**

| **Información**         | **Consumidores autorizados**                                            |
|-------------------------|-------------------------------------------------------------------------|
| Depósitos               | Solicitudes, Contratos, Posiciones y liquidez, Contabilidad y atención. |
| Instrumentos            | Órdenes, Tenencias, Riesgos y Comunicaciones.                           |
| Órdenes y liquidaciones | Tenencias, Tesorería, Contabilidad, Documentos y Comunicaciones.        |
| Posiciones y derechos   | Órdenes, Riesgos, Garantías, Contabilidad y Extractos.                  |
| Cuentas y traslados     | Solicitudes, Contratos, Tesorería, Restricciones y atención.            |

Los consumidores comparan versiones y recuperan estado autoritativo ante discontinuidades relevantes.

No existe orden global entre cuenta, orden, instrumento, posición y traslado.

La liquidación comunica efectos originales sin ordenar repetirlos.

## **13. Seguridad y errores**

Se separan permisos para preparar, constituir, pagar, renovar, precancelar, definir instrumentos, registrar valores,
instruir órdenes, comprometer recursos, ejecutar, liquidar, custodiar, valuar, determinar derechos, corregir y
trasladar.

Titulares, representantes y custodios se comprueban separadamente.

Cada servicio confirma agregado, historial, idempotencia y outbox en su transacción local. No mantiene transacciones SQL
abiertas durante llamadas remotas.

| **Código**                     | **HTTP** | **Significado**                              |
|--------------------------------|----------|----------------------------------------------|
| CONTENT_CONFLICT               | 409      | Identidad con contenido diferente.           |
| VERSION_CONFLICT               | 409      | Versión incompatible.                        |
| DEPOSIT_PREPARATION_EXPIRED    | 422      | Condiciones vencidas.                        |
| DEPOSIT_TERM_NOT_ALLOWED       | 422      | Plazo incompatible.                          |
| DEPOSIT_EARLY_EXIT_NOT_ALLOWED | 422      | Modalidad sin salida anticipada.             |
| EARLY_EXIT_NOTICE_PENDING      | 422      | Fecha anterior al preaviso.                  |
| INDEX_REFERENCE_MISSING        | 422      | Falta índice exigible para ejecutar.         |
| INSTRUMENT_OPERATION_SUSPENDED | 422      | Operación no habilitada.                     |
| INVESTOR_SUITABILITY_FAILED    | 422      | Inversión incompatible.                      |
| QUANTITY_PRECISION_INVALID     | 400      | Cantidad fuera de precisión.                 |
| INSUFFICIENT_OWN_FUNDS         | 422      | Cobertura monetaria insuficiente.            |
| INSUFFICIENT_AVAILABLE_UNITS   | 422      | Unidades insuficientes.                      |
| ORDER_PRICE_LIMIT_EXCEEDED     | 422      | Ejecución fuera del límite.                  |
| ORDER_CANCELLATION_NOT_FINAL   | 409      | No puede afirmarse cancelación definitiva.   |
| SETTLEMENT_COMPONENT_CONFLICT  | 409      | Confirmación incompatible.                   |
| ENTITLEMENT_ALREADY_FULFILLED  | 409      | Otra actuación intenta repetir cumplimiento. |
| CUSTODY_OWNERSHIP_MISMATCH     | 422      | Titularidad diferente.                       |
| CUSTODY_TRANSFER_UNCERTAIN     | 409      | Traslado pendiente de resolución.            |
| DEPENDENCY_UNAVAILABLE         | 503      | Dependencia necesaria indisponible.          |

Una repetición coherente recupera el resultado original.

Un cálculo puede responder 200 con INCOMPLETE_INPUT. El error corresponde a la actuación que necesita ese dato para
ejecutar.

Una actuación durable pendiente devuelve 202 con seguimiento.

## **14. Escenarios de aceptación**

| **Escenario**                                          | **Resultado exigido**                                             |
|--------------------------------------------------------|-------------------------------------------------------------------|
| Depósito 1000 al 36,5 % anual durante treinta días     | Calcula interés nominal 30 antes de tributos.                     |
| Débito confirmado y constitución pendiente             | Recupera y completa o restituye sin otro débito.                  |
| Depósito UVA                                           | Conserva unidades originales y fuentes de actualización.          |
| Falta índice de liquidación                            | No presenta un valor supuesto como definitivo.                    |
| Renovación total de capital 1000 y rendimiento neto 30 | Constituye sucesor 1030 sin disponibilidad simultánea.            |
| Salida anticipada                                      | Respeta modalidad, permanencia y preaviso.                        |
| Compra de 1000 nominales a precio 95                   | Principal 950; interés corrido y cargos se agregan separadamente. |
| Cargos superan el máximo autorizado                    | No amplía silenciosamente la cobertura aceptada.                  |
| Posición 100 y compromiso 30                           | Mantiene registrado 100 y disponible 70 si no hay otras causas.   |
| Entrega de esas 30 unidades                            | Reduce registrado a 70 y consume el compromiso una sola vez.      |
| Ejecución confirmada con liquidación pendiente         | No informa disponibilidad completa.                               |
| Cancelación externa incierta                           | Conserva recursos y seguimiento.                                  |
| Traslado solicitado por 30 y salida de 20              | Conserva 20 en tránsito y 10 pendientes, sin reenviar 30.         |
| Rechazo posterior a salida                             | No restituye disponibilidad sin retorno comprobado.               |
| Derecho recibido después del traslado                  | Conserva beneficiario y obligación residual de entrega.           |
| Corrección de valuación                                | No crea compraventa ficticia.                                     |
| Cierre con derechos pendientes                         | Mantiene régimen residual y vía de pago.                          |
| Respuesta perdida o evento duplicado                   | Recupera el efecto original sin repetir dinero ni unidades.       |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
