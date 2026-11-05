# **Contratos técnicos — Módulo 10. Tesorería**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**               | **Responsabilidad**                                                               |
|----------------------------|-----------------------------------------------------------------------------------|
| Posiciones y liquidez      | Recursos propios, ubicaciones, efectivo, compromisos, flujos y proyecciones.      |
| Operaciones de tesorería   | Financiación e inversiones propias del banco, rendimientos y obligaciones.        |
| Cambio de moneda           | Cotizaciones, propuestas, aceptación, ejecución y recuperación de intercambios.   |
| Liquidaciones de tesorería | Instrucciones, confirmaciones y conciliación de obligaciones entre participantes. |

Comprende los contratos **CI-10-001 a CI-10-039**.

Los saldos de clientes pertenecen a Fondos. Las posiciones propias del banco pertenecen a Posiciones y liquidez.

Un cambio interno entre cuentas de clientes no demuestra una salida de efectivo ni un movimiento de una cuenta externa.

Una financiación concertada no representa dinero recibido. Una instrucción de liquidación no demuestra pago.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

Esta consulta recupera resultados e identificadores generados aunque se pierda la respuesta inicial.

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
| Rate              | Cadena decimal, precisión 24 y escala máxima 12.                       |
| Price             | Cadena decimal positiva, precisión 30 y escala máxima 12.              |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |

Los importes y cantidades ejecutados son positivos. Acumulados y remanentes admiten cero.

Los cálculos intermedios conservan al menos doce decimales. Los resultados monetarios utilizan HALF_UP a centavos.

### **2.3. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. EconomicComponent**

Contiene:

- conceptCode: Code.

- componentType: CAPITAL, INTEREST, ACCRUED_INTEREST, FEE, TAX, PRICE_DIFFERENCE o AUTHORIZED_ADJUSTMENT.

- amount: MoneyAmount.

- currency: Currency.

- sourceReference: ObjectReference.

Las retenciones, entregas y conceptos devengados permanecen separados.

Al invocar Fondos se utiliza su clasificación monetaria, conservando concepto y referencia de origen.

### **3.2. ExternalExecutionReference**

Contiene:

- participantReference: ObjectReference.

- mechanismCode: Code.

- mechanismVersion: Version.

- externalExecutionId: ExternalReference.

La identidad externa se interpreta dentro del participante y mecanismo. Cambiar la versión técnica no permite duplicar
una ejecución anterior.

### **3.3. SettlementDestination**

| **Campo**                  | **Tipo**                                  | **Condición**              |
|----------------------------|-------------------------------------------|----------------------------|
| destinationType            | EXTERNAL_ACCOUNT, CASH_LOCATION o CUSTODY | Obligatorio.               |
| accountOrLocationReference | ObjectReference                           | Obligatorio.               |
| destinationVersion         | Version                                   | Obligatorio.               |
| verificationReference      | ObjectReference                           | Obligatorio.               |
| participantReference       | ObjectReference                           | Para participante externo. |
| currency                   | Currency                                  | Para dinero.               |
| instrumentReference        | ObjectReference                           | Para instrumentos.         |

El destino queda vinculado a una versión verificada.

Modificar datos sensibles exige verificación independiente y aprobación cuando corresponde. Una instrucción ya
autorizada no se redirige silenciosamente.

### **3.4. Resultados**

Los comandos utilizan el resultado común y agregan:

- resourceCommitmentReferences.

- monetaryEffectReferences.

- externalExecutionReferences.

- Resultados por componente.

- Pendientes comunes.

Se distinguen ejecución externa, actualización de posiciones, aplicación al origen y regularizaciones.

## **4. Servicio Posiciones y liquidez**

### **4.1. TreasuryResourceLocation**

Contiene:

- locationId y version, generados.

- locationType: EXTERNAL_ACCOUNT o PHYSICAL_CASH_LOCATION.

- institutionReference.

- holderReference.

- currency.

- purposeCode.

- capabilityCodes.

- responsibleReferences.

- restrictionReferences.

- settlementDestination.

- externalIdentifier, cuando existe.

- status, generado.

Los estados son DRAFT, ENABLED, LIMITED, CLOSING y RETIRED.

Registrar una ubicación no crea saldo.

### **4.2. TreasuryPosition**

Contiene:

- positionId: UUID.

- locationId: UUID.

- currency.

- positionVersion: Version.

- movementSequence: Sequence, cuando existen movimientos.

- confirmedBalance: SignedMoneyAmount.

- Compromisos por clase.

- Inmovilizaciones.

- Efectivo en tránsito.

- newCapacity: MoneyAmount.

- shortage: MoneyAmount.

- cutAt: Instant.

Los componentes identifican sus relaciones para no descontar dos veces recursos superpuestos.

Un cobro esperado no integra saldo confirmado.

La versión de posición comprende cambios sin movimiento; la secuencia identifica únicamente movimientos.

### **4.3. Interfaces**

| **Contrato** | **operationId**                | **Método y ruta**                                         |
|--------------|--------------------------------|-----------------------------------------------------------|
| CI-10-001    | createTreasuryResourceLocation | POST /v1/treasury-resource-locations                      |
| CI-10-001    | actOnTreasuryResourceLocation  | POST /v1/treasury-resource-locations/{locationId}/actions |
| CI-10-002    | getTreasuryPositions           | GET /v1/treasury-positions                                |
| CI-10-003    | recordTreasuryResourceMovement | POST /v1/treasury-resource-movements                      |

Las acciones de ubicación son MODIFY, ENABLE, LIMIT y RETIRE.

Los cambios identifican versión y aprobación exigible.

No se retira una ubicación con saldo o compromisos sin tratamiento identificado.

Consultar posiciones recibe finalidad, moneda, ubicación o ámbito, corte y paginación. Devuelve fuentes y antigüedad.

### **4.4. Movimiento propio**

Recibe contexto, effectId, hecho de origen, ubicación, sentido IN o OUT, importe, moneda, fecha y evidencia.

Agrega compromiso cuando respalda el movimiento.

La posición y el consumo del compromiso se confirman conjuntamente cuando pertenecen al mismo servicio.

Una salida requiere hecho comprobado. La orden no modifica saldo por sí misma.

La resolución de una preparación cambiaria interna no utiliza esta interfaz para inventar un movimiento externo.

## **5. Efectivo y compromisos**

### **5.1. Interfaces**

| **Contrato** | **operationId**                  | **Método y ruta**                                              |
|--------------|----------------------------------|----------------------------------------------------------------|
| CI-10-004    | createCashResourceTransfer       | POST /v1/cash-resource-transfers                               |
| CI-10-005    | recordCashResourceTransferResult | POST /v1/cash-resource-transfers/{transferId}/physical-results |
| CI-10-005    | recordCashResourceCount          | POST /v1/cash-resource-counts                                  |
| CI-10-006    | commitTreasuryResources          | POST /v1/treasury-resource-commitments                         |
| CI-10-007    | actOnTreasuryResourceCommitment  | POST /v1/treasury-resource-commitments/{commitmentId}/actions  |

### **5.2. Traslado y arqueo**

Crear traslado recibe contexto, origen, destino, importe, moneda, fecha prevista, responsables y aprobación.

Devuelve traslado, compromiso y condiciones de salida y recepción.

Los resultados físicos identifican hecho único, tipo DISPATCHED, RECEIVED o RETURNED, importe, moneda, fecha y
evidencia.

Cada parcialidad conserva identidad propia. Un importe recibido no se presume igual al enviado.

El arqueo recibe ubicación, moneda, importe contado, fecha, custodio, verificador y evidencia.

Custodio y verificador son personas distintas.

La respuesta distingue existencia, tránsito, diferencia y ajuste pendiente.

El arqueo no crea saldo automáticamente para forzar coincidencia. Un ajuste requiere aprobación y movimiento
identificado.

### **5.3. TreasuryResourceCommitment**

Contiene:

- commitmentId y version.

- commitmentClass.

- ownerService.

- operationReference.

- currency y amount.

- resourceAssignments.

- firmness: CANCELLABLE, FIRM o UNCERTAIN.

- consumedAmount.

- releasedAmount.

- resolvedAmount.

- remainingAmount.

- conclusionConditions.

- status.

- obligationReference, cuando existe.

Las clases son:

- EXTERNAL_PAYMENT.

- PHYSICAL_CASH.

- OWN_INVESTMENT.

- INTERNAL_FX_PREPARATION.

Cada asignación identifica ubicación e importe.

Se cumple:

**Importe comprometido vigente = consumido + liberado + resuelto + remanente.**

resolvedAmount corresponde a conclusión interna que no representa consumo externo.

Los estados son ACTIVE, PARTIALLY_CONSUMED, CONSUMED, RELEASED, RESOLVED_INTERNAL_FX y RESOLUTION_PENDING.

### **5.4. Constitución y resolución**

Comprometer recibe contexto, clase, operación, obligación cuando existe, importe, moneda, fecha requerida, recursos
elegibles y firmeza.

Las condiciones de conclusión identifican hecho exigido, propietario y referencias verificables.

Para cambio interno se vinculan intercambio y contenido aceptado.

La comprobación y afectación de recursos se realizan conjuntamente.

Las acciones son:

| **Modalidad**       | **Datos específicos**                                                  |
|---------------------|------------------------------------------------------------------------|
| MODIFY              | Importe objetivo y condiciones propuestas.                             |
| REALLOCATE          | Nuevas asignaciones y fundamento.                                      |
| RELEASE_CANCELLED   | Evidencia que descarta ejecución pendiente.                            |
| RESOLVE_INTERNAL_FX | Intercambio, resultado de Fondos y transformación de Riesgos exigible. |

Una modificación no reduce el importe por debajo de componentes ya concluidos.

La reasignación no mantiene simultáneamente capacidad libre sobre recursos que siguen comprometidos.

### **5.5. Conclusión de cambio interno**

RESOLVE_INTERNAL_FX exige:

- Referencia del intercambio.

- Conjunto monetario de Fondos.

- Resultado confirmado de sus dos piernas.

- Transformación de Riesgos cuando corresponde.

- Correspondencia con monedas, importes y contenido original.

La conclusión extingue la cobertura temporal.

No registra una salida externa ni libera la exposición efectiva resultante del cambio.

Si falta una regularización, el compromiso conserva el pendiente correspondiente sin repetir el intercambio.

## **6. Flujos, proyecciones y cierre operativo**

### **6.1. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                            |
|--------------|------------------------------|----------------------------------------------|
| CI-10-008    | updateLiquidityFlow          | POST /v1/liquidity-flow-updates              |
| CI-10-009    | createLiquidityProjection    | POST /v1/liquidity-projections               |
| CI-10-009    | getLiquidityProjection       | GET /v1/liquidity-projections/{projectionId} |
| CI-10-010    | createLiquidityCoveragePlan  | POST /v1/liquidity-coverage-plans            |
| CI-10-031    | createTreasuryOperatingClose | POST /v1/treasury-operating-closes           |
| CI-10-031    | getTreasuryOperatingClose    | GET /v1/treasury-operating-closes/{closeId}  |
| CI-10-032    | getLiquidityAction           | GET /v1/liquidity-actions/{actionId}         |

### **6.2. Flujos**

Actualizar utiliza UPSERT o WITHDRAW.

UPSERT recibe flujo, versión de fuente, operación, moneda, importe, sentido IN o OUT, fecha esperada y certeza.

Agrega obligación y referencias de composición cuando existen.

La certeza es CONTRACTUAL, FIRM, ESTIMATED o UNCERTAIN.

WITHDRAW identifica flujo, versión de fuente y fundamento. No exige reenviar importes.

La fuente y la identidad de obligación permiten reconocer sustituciones entre flujos agregados y componentes.

### **6.3. Proyecciones y planes**

Proyectar recibe corte, monedas, ámbito, horizontes y escenarios de Riesgos.

Los horizontes son INTRADAY, NEXT_BUSINESS_DAY, SEVEN_DAYS y THIRTY_DAYS.

Devuelve saldos base, flujos, compromisos, faltantes, excedentes, supuestos y fuentes ausentes.

No suma ni descuenta dos veces un flujo y compromiso de la misma obligación.

El plan de cobertura identifica necesidad, moneda, fecha, obligaciones y alternativas.

Cada alternativa contiene modalidad, responsable, fecha y actuación prevista.

Devuelve cobertura comprobada y faltante restante.

Financiación solicitada o desinversión pendiente no equivale a disponibilidad.

### **6.4. Cierre operativo**

Recibe corte, posiciones, compromisos, conciliaciones y valuaciones.

Devuelve cierre versionado, fuentes, saldos, tránsito, compromisos por clase, liquidaciones inciertas y limitaciones.

Su condición es COMPLETE o INCOMPLETE.

No cierra períodos contables ni convierte información faltante en saldo cero.

## **7. Servicio Operaciones de tesorería**

### **7.1. TreasuryOperation**

Contiene:

- treasuryOperationId y version.

- operationType.

- Contraparte.

- Contrato.

- Moneda.

- Condiciones económicas.

- Calendarios.

- Garantías propias.

- Referencias de Riesgos.

- Aprobación.

- Ejecuciones.

- Liquidaciones.

- Estado.

Las modalidades son:

- OWN_BORROWING.

- OWN_CREDIT_LINE.

- OWN_DEPOSIT.

- INTERBANK_LOAN.

- FIXED_INCOME_PURCHASE.

- FIXED_INCOME_SALE.

Las operaciones monetarias identifican capital, plazo y tasa. Las de títulos identifican instrumento, clase, cantidad,
base de cotización y precio.

Los estados son PREPARING, APPROVED, CONTRACTED, EXECUTION_PENDING, ACTIVE, FULFILLMENT_PENDING, CLOSED y
REQUIRES_RESOLUTION.

### **7.2. Interfaces**

| **Contrato** | **operationId**                   | **Método y ruta**                                                |
|--------------|-----------------------------------|------------------------------------------------------------------|
| CI-10-011    | createTreasuryFinancingOperation  | POST /v1/treasury-financing-operations                           |
| CI-10-012    | createTreasuryInvestmentOperation | POST /v1/treasury-investment-operations                          |
| CI-10-013    | recordTreasuryOperationExecution  | POST /v1/treasury-operations/{treasuryOperationId}/executions    |
| CI-10-014    | accrueTreasuryOperation           | POST /v1/treasury-operations/{treasuryOperationId}/accruals      |
| CI-10-015    | fulfillTreasuryOperation          | POST /v1/treasury-operations/{treasuryOperationId}/fulfillments  |
| CI-10-016    | modifyTreasuryOperation           | POST /v1/treasury-operations/{treasuryOperationId}/modifications |
| CI-10-017    | getTreasuryOperation              | GET /v1/treasury-operations/{treasuryOperationId}                |
| CI-10-030    | valueTreasuryOperations           | POST /v1/treasury-operation-valuations                           |

El identificador de ruta representa la operación de Tesorería, no el operationId técnico de OpenAPI.

### **7.3. Concertación y ejecución**

Crear financiación recibe modalidad, contraparte, capital, moneda, plazo, tasa, flujos, garantías propias, destino,
contrato, Riesgos y aprobación.

Cada disposición de una línea conserva identidad propia.

Crear inversión recibe contraparte o instrumento según modalidad, capital o cantidad, condiciones, cargos, recursos y
aprobación.

No admite ventas en corto ni apalancamiento.

Registrar ejecución recibe referencia externa, resultado, fecha y evidencia.

Los resultados son ACCEPTED, EXECUTED, REJECTED y UNCERTAIN.

Una ejecución monetaria exige importe y moneda. Una ejecución de títulos exige cantidad, instrumento y precio.

Cada parcialidad conserva identidad y remanente.

Firmar un acuerdo no acredita dinero.

### **7.4. Rendimientos y cumplimiento**

Devengar recibe período, posiciones efectivas y condiciones versionadas.

El período incluye inicio y excluye final.

Devuelve cálculo, capital, interés, cargos, derechos, obligaciones y vencimientos.

No capitaliza automáticamente ni vuelve a devengar el mismo tramo confirmado.

Cumplir admite a Liquidaciones de tesorería y recibe obligación, efecto único, importe o cantidad, fecha y evidencia.

Cobrar un interés devengado cancela el derecho sin reconocer otro ingreso.

### **7.5. Modificación y valuación**

Modificar utiliza CHANGE_TERMS, EARLY_TERMINATION o CANCEL_UNEXECUTED.

Identifica acuerdo efectivo, condiciones o cálculo de salida, Riesgos y aprobación según modalidad.

Una propuesta no cambia el acuerdo. Una cancelación sin ejecución exige descartar pendientes incompatibles.

Valuar recibe corte, posiciones, política, precios y cotizaciones.

Devuelve costo, valor, rendimiento, resultado realizado, variación de valuación y faltantes.

La ausencia de precio no se sustituye por cero. La valuación no crea recursos líquidos.

## **8. Servicio Cambio de moneda**

### **8.1. ExchangeRateReference**

Contiene:

- rateReferenceId y version.

- rateClass.

- baseCurrency y quoteCurrency.

- side: BANK_BUY o BANK_SELL.

- quotationUnit: cantidad positiva de moneda base.

- value: importe de moneda cotizada por esa unidad.

- Fuente.

- Momento aplicable.

- Vigencia.

- Referencia sustituida, cuando corrige.

Las clases son REFERENCE, ACCOUNTING_CLOSE, EXECUTABLE_COMMERCIAL e INFORMATIVE.

El sentido se expresa desde el banco respecto de la moneda base:

- BANK_BUY: el banco compra moneda base.

- BANK_SELL: el banco vende moneda base.

El par contiene monedas diferentes.

### **8.2. Interfaces**

| **Contrato** | **operationId**                 | **Método y ruta**                                                |
|--------------|---------------------------------|------------------------------------------------------------------|
| CI-10-018    | recordExchangeRateReference     | POST /v1/exchange-rate-references                                |
| CI-10-019    | queryExchangeRateReferences     | GET /v1/exchange-rate-references                                 |
| CI-10-020    | createForeignExchangeProposal   | POST /v1/foreign-exchange-proposals                              |
| CI-10-021    | executeForeignExchangeProposal  | POST /v1/foreign-exchange-proposals/{proposalId}/executions      |
| CI-10-022    | resolveForeignExchangeOperation | POST /v1/foreign-exchange-operations/{fxOperationId}/resolutions |
| CI-10-023    | getForeignExchangeOperation     | GET /v1/foreign-exchange-operations/{fxOperationId}              |
| CI-10-023    | getForeignExchangeProposal      | GET /v1/foreign-exchange-proposals/{proposalId}                  |

La carga manual de cotización exige aprobación.

Devuelve VALIDATED, PENDING_VERIFICATION u OBSERVED.

Para igual par, unidad, alcance y momento, compra no supera venta.

Una referencia contable o informativa no se ejecuta como oferta comercial.

### **8.3. ForeignExchangeProposal**

Contiene:

- proposalId y version.

- Operación de origen.

- Participante.

- executionMode: INTERNAL_CUSTOMER_ACCOUNTS o EXTERNAL_DELIVERY.

- bankSide.

- Monedas base y cotizada.

- fixedAmountSide: BASE o QUOTE.

- Importe fijo.

- Contraprestación calculada.

- Importes base y cotizado.

- Cotización versionada.

- Cargos y tributos.

- Orígenes y destinos.

- Fecha prevista de liquidación.

- issuedAt.

- validUntil.

- contentHash.

La contraprestación se obtiene de importe, valor y unidad de cotización, con redondeo monetario explícito.

Los cargos y tributos identifican moneda y tratamiento. La propuesta conserva los importes finales que se debitarán y
acreditarán.

La vigencia es de sesenta segundos, con final excluido.

Cotizar no reserva recursos.

### **8.4. Aceptación y ejecución**

Aceptar recibe contexto, versión, huella aceptada, evidencia de conformidad y autorizaciones.

El proveedor registra su recepción efectiva y exige que sea anterior a validUntil.

La respuesta contiene:

- fxOperationId.

- acceptanceStatus: ACCEPTED, REJECTED o PENDING.

- monetaryExecutionStatus.

- riskTransformationStatus.

- liquidityCommitmentStatus.

- externalDeliveryStatus, para modalidad externa.

- Pendientes.

Los estados de ejecución son NOT_STARTED, PENDING, CONFIRMED, REJECTED y UNCERTAIN.

Aceptar dentro de vigencia no permite alterar después el contenido económico. Si no puede completarse, conserva la
aceptación y su tratamiento comprobable.

### **8.5. Cambio interno**

La coordinación conserva:

- Reserva monetaria cuando corresponde.

- Compromiso INTERNAL_FX_PREPARATION.

- Afectación de Riesgos.

- Preparaciones de autoridades.

- Identidad del conjunto interno.

- Resultado de Fondos.

- Transformación de exposición.

- Resolución del compromiso temporal.

Fondos confirma ambas piernas mediante el conjunto interno indivisible.

Después se transforma exposición y se concluye la preparación temporal.

Si Fondos confirmó, el resultado monetario permanece CONFIRMED aunque falte una regularización.

No se repite el cambio ni se crea una salida externa ficticia para completar el seguimiento.

### **8.6. Entrega externa e incidencias**

La modalidad externa identifica obligaciones por componente y utiliza Liquidaciones de tesorería.

Una entrada esperada no modifica saldo confirmado. La parcialidad depende del mecanismo.

Las resoluciones son:

- RECOVER_EXECUTION.

- COMPLETE_RISK_TRANSFORMATION.

- COMPLETE_PREPARATION_RESOLUTION.

- CORRECT_ECONOMIC_EFFECT.

- RESTITUTE_CONFIRMED_AMOUNT.

Cada una recibe referencias comprobadas, tratamiento y aprobación cuando corresponde.

Corregir un error no simula una nueva compra voluntaria a cotización actual.

## **9. Servicio Liquidaciones de tesorería**

### **9.1. TreasurySettlementObligation**

Contiene:

- obligationId y version.

- ownerService.

- Obligación y operación de origen.

- Referencias de composición.

- Contraparte.

- direction.

- Fecha de vencimiento.

- Mecanismo versionado.

- Destino verificado.

- Firmeza.

- partialSettlementAllowed.

- Recursos y resultados relacionados.

Las variantes son:

| **Sentido**                               | **Datos económicos**    |
|-------------------------------------------|-------------------------|
| PAY o RECEIVE                             | Importe y moneda.       |
| DELIVER_INSTRUMENTS o RECEIVE_INSTRUMENTS | Cantidad e instrumento. |

Los componentes de entrega contra pago quedan vinculados mediante una referencia de conjunto.

### **9.2. Interfaces**

| **Contrato** | **operationId**                      | **Método y ruta**                                                     |
|--------------|--------------------------------------|-----------------------------------------------------------------------|
| CI-10-024    | registerTreasurySettlementObligation | POST /v1/treasury-settlement-obligations                              |
| CI-10-025    | instructTreasurySettlement           | POST /v1/treasury-settlement-obligations/{obligationId}/instructions  |
| CI-10-026    | recordTreasurySettlementResult       | POST /v1/treasury-settlement-instructions/{instructionId}/results     |
| CI-10-027    | getTreasurySettlementObligation      | GET /v1/treasury-settlement-obligations/{obligationId}                |
| CI-10-027    | getTreasurySettlementInstruction     | GET /v1/treasury-settlement-instructions/{instructionId}              |
| CI-10-028    | resolveTreasurySettlement            | POST /v1/treasury-settlement-instructions/{instructionId}/resolutions |
| CI-10-029    | reconcileTreasurySettlements         | POST /v1/treasury-settlement-reconciliations                          |

### **9.3. Recepción e instrucción**

Registrar recibe contexto y obligación de origen.

La identidad del propietario y obligación original impide duplicarla por otra vía.

Un agregado no se incorpora además de sus componentes como otra obligación independiente.

Un cambio interno sin entrega externa no genera obligación ficticia.

Instruir recibe contexto, recursos, mecanismo, destino verificado y autorización de liberación.

La autorización utiliza:

- INDIVIDUAL_APPROVAL: aprobación vinculante.

- AUTOMATIC_RULE: regla y versión habilitadas.

Concertación y liberación conservan separación de funciones.

La respuesta identifica instrucción, referencia externa, despacho y cobertura.

### **9.4. Resultados**

El adaptador autorizado incorpora referencia externa, resultado, importe o cantidad, fecha y evidencia.

Los resultados son ACKNOWLEDGED, CONFIRMED, PARTIAL, REJECTED y UNCERTAIN.

Un acuse no demuestra pago. Un rechazo no se utiliza cuando persiste ejecución desconocida.

La respuesta separa:

- Cumplimiento externo.

- Actualización de posición.

- Aplicación o comunicación al origen.

- Remanente.

- Pendientes.

Si el externo confirmó y falta actualizar la posición, se recupera esa actualización sin otro pago.

### **9.5. Cancelación y remanentes**

Las resoluciones son:

- QUERY_RESULT.

- CANCEL_UNSENT.

- COMPLETE_REMAINDER.

- INVESTIGATE_FAILURE.

Una cancelación definitiva impide despacho posterior de la misma referencia.

Si el despacho ya fue reclamado, se mantiene pendiente hasta conocer su resultado.

Completar remanente exige descartar duplicación con instrucciones anteriores. La nueva instrucción tiene identidad
propia y vínculo con la original.

No supera lo pendiente ni admite parcialidad no habilitada.

### **9.6. Conciliación**

Recibe cuentas, obligaciones, instrucciones, cortes comparables, extractos externos y evidencias.

Devuelve coincidencias y diferencias:

TIMING, MISSING_RECORD, DUPLICATE, AMOUNT_MISMATCH, QUANTITY_MISMATCH o STATE_CONTRADICTION.

Cada diferencia identifica responsable y actuación de resolución.

Los ajustes manuales requieren aprobación y ejecución por el propietario competente.

Un débito externo desconocido no se carga automáticamente a un cliente.

## **10. Coordinación y recuperación**

Las actuaciones utilizan preparaciones de Institución, Representación, Restricciones y propietarios operativos cuando el
circuito las exige.

La preparación de facultades es distinta del compromiso de recursos.

La incertidumbre externa se consulta al detectarla, a los cinco minutos y a los treinta minutos. Si persiste, continúa
investigación por jornada operativa.

Los tiempos no autorizan liberaciones automáticas.

Se conservan identidades de obligación, instrucción, compromiso y efecto.

La gestión ordinaria se realiza en días hábiles desde las 09:00 hasta antes de las 18:00. Las obligaciones y
confirmaciones se reciben durante todo el día.

La zona es America/Argentina/Buenos_Aires.

## **11. Eventos**

### **11.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                                  | **Tema**                           | **Agregado**                            |
|--------------|-------------------------------------------|------------------------------------|-----------------------------------------|
| CI-10-033    | TreasuryResourceOrCommitmentChanged       | banking.liquidity.events           | Posición o compromiso.                  |
| CI-10-034    | LiquidityProjectionOrNeedChanged          | banking.liquidity.events           | Proyección o necesidad.                 |
| CI-10-035    | TreasuryOperationChanged                  | banking.treasury-operation.events  | Operación propia.                       |
| CI-10-036    | ExchangeRateReferenceChanged              | banking.foreign-exchange.events    | Cotización.                             |
| CI-10-037    | ForeignExchangeOperationChanged           | banking.foreign-exchange.events    | Intercambio.                            |
| CI-10-038    | TreasurySettlementOrReconciliationChanged | banking.treasury-settlement.events | Obligación, instrucción o conciliación. |
| CI-10-039    | TreasuryCloseOrValuationChanged           | banking.liquidity.events           | Cierre o valuación.                     |

La versión inicial de esquema es 1. La clave es el identificador del agregado.

### **11.2. CI-10-033 — TreasuryResourceOrCommitmentChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: MOVEMENT_RECORDED, POSITION_CHANGED, COMMITMENT_CREATED, COMMITMENT_CHANGED o COMMITMENT_CONCLUDED.

- currency.

- pendingComponents: pendientes comunes.

Con aggregateType igual a TREASURY_POSITION, agrega ubicación, saldo, capacidad, faltante y movimiento con secuencia
cuando existe.

Con aggregateType igual a TREASURY_COMMITMENT, agrega clase, operación, importe, consumido, liberado, resuelto,
remanente y estado.

RESOLVED_INTERNAL_FX no comunica entrada ni salida monetaria.

### **11.3. CI-10-034 — LiquidityProjectionOrNeedChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- cutAt: Instant.

- currency.

- sourceReferences: ObjectReference\[\].

- missingSourceCodes: Code\[\].

- pendingComponents: pendientes comunes.

La variante LIQUIDITY_PROJECTION agrega horizonte, saldo base, entradas, salidas, compromisos, faltante, excedente y
supuestos versionados.

La variante LIQUIDITY_NEED agrega fecha requerida, necesidad, cobertura comprobada y plan relacionado cuando existe.

El aggregateType selecciona la variante.

### **11.4. CI-10-035 — TreasuryOperationChanged**

aggregateType es TREASURY_OPERATION.

Payload obligatorio:

- treasuryOperationId: UUID.

- treasuryOperationVersion: Version.

- changeType: CONTRACTED, EXECUTION_UPDATED, ACCRUED, FULFILLMENT_UPDATED, MODIFIED o CLOSED.

- operationType.

- counterpartyReference.

- currency.

- obligationReferences: ObjectReference\[\].

- settlementReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Una ejecución agrega referencia externa, resultado y datos económicos.

Un devengamiento agrega período, cálculo y conceptos.

Un cumplimiento agrega efecto, importe o cantidad y remanente.

### **11.5. CI-10-036 — ExchangeRateReferenceChanged**

aggregateType es EXCHANGE_RATE_REFERENCE.

Payload obligatorio:

- rateReferenceId: UUID.

- rateVersion: Version.

- rateClass.

- baseCurrency y quoteCurrency.

- side.

- quotationUnit.

- value.

- sourceReference.

- applicableAt.

- validity.

- verificationStatus: VALIDATED, PENDING_VERIFICATION u OBSERVED.

Una corrección agrega supersededReference y motivo.

El evento no constituye una propuesta ejecutable aceptada.

### **11.6. CI-10-037 — ForeignExchangeOperationChanged**

aggregateType es FOREIGN_EXCHANGE_OPERATION.

Payload obligatorio:

- fxOperationId: UUID.

- fxOperationVersion: Version.

- proposalReference: VersionedReference.

- acceptedContentHash: Hash.

- executionMode.

- baseCurrency y quoteCurrency.

- baseAmount y quoteAmount: MoneyAmount.

- Estado monetario.

- Estado de transformación de Riesgos.

- Estado del compromiso de liquidez.

- pendingComponents: pendientes comunes.

Agrega conjunto de Fondos para ejecución interna y obligaciones externas para entrega externa.

Un cambio monetario confirmado no vuelve a pendiente por una regularización posterior incompleta.

### **11.7. CI-10-038 — TreasurySettlementOrReconciliationChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: OBLIGATION_REGISTERED, INSTRUCTION_UPDATED, RESULT_RECORDED o RECONCILIATION_UPDATED.

- originReferences: ObjectReference\[\].

- evidenceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Variantes:

| **aggregateType**                  | **Campos específicos**                                                        |
|------------------------------------|-------------------------------------------------------------------------------|
| TREASURY_SETTLEMENT_OBLIGATION     | Propietario, contraparte, sentido, importe o cantidad, cumplido y remanente.  |
| TREASURY_SETTLEMENT_INSTRUCTION    | Obligación, destino verificado, referencia externa cuando existe y resultado. |
| TREASURY_SETTLEMENT_RECONCILIATION | Cortes, fuentes y diferencias relacionadas.                                   |

Una aceptación técnica no se presenta como cumplimiento confirmado.

### **11.8. CI-10-039 — TreasuryCloseOrValuationChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- cutAt: Instant.

- sourceReferences: ObjectReference\[\].

- limitations: Code\[\].

- pendingComponents: pendientes comunes.

La variante TREASURY_OPERATING_CLOSE agrega condición completa o incompleta, posiciones, compromisos y conciliaciones.

La variante TREASURY_VALUATION agrega política, valores por operación, monedas, fuentes y faltantes.

Los resultados con signo utilizan SignedMoneyAmount.

Un cierre operativo no comunica cierre de período contable.

### **11.9. Consumidores y orden**

Los eventos se entregan a originantes y propietarios de Posiciones, Operaciones, Cambio, Liquidaciones, Riesgos y
Contabilidad según competencia.

Documentos, Comunicaciones y Gestión administrativa reciben únicamente información necesaria para su finalidad.

Los consumidores comparan versiones y recuperan estado autoritativo ante discontinuidades relevantes.

Un evento no repite movimientos ni crea otra obligación por el mismo hecho.

## **12. Seguridad y errores**

Se separan permisos para administrar ubicaciones, mover recursos, trasladar y arquear efectivo, comprometer, reasignar,
liberar, concertar, aprobar, cotizar, ejecutar cambios, liberar liquidaciones, incorporar resultados, conciliar, valuar
y cerrar.

La autorización de resolución interna no permite declarar pagada una obligación externa sin evidencia.

Una cotización informativa en caché no sustituye la propuesta persistida aceptada.

Las transacciones locales confirman agregado, historial, idempotencia y outbox sin mantener bloqueos durante llamadas
remotas.

| **Código**                         | **HTTP** | **Significado**                          |
|------------------------------------|----------|------------------------------------------|
| CONTENT_CONFLICT                   | 409      | Identidad con contenido diferente.       |
| VERSION_CONFLICT                   | 409      | Versión incompatible.                    |
| RESOURCE_LOCATION_NOT_ENABLED      | 422      | Ubicación no habilitada.                 |
| TREASURY_CAPACITY_INSUFFICIENT     | 422      | Cobertura insuficiente.                  |
| RESOURCE_COMMITMENT_CLASS_MISMATCH | 422      | Modalidad incompatible.                  |
| COMMITMENT_EXECUTION_UNCERTAIN     | 409      | No puede liberarse el compromiso.        |
| INTERNAL_FX_RESULT_REQUIRED        | 422      | Falta resultado de Fondos.               |
| RISK_TRANSFORMATION_REQUIRED       | 422      | Falta transformación exigible.           |
| CASH_PHYSICAL_RESULT_CONFLICT      | 409      | Hechos físicos contradictorios.          |
| EXCHANGE_RATE_INCONSISTENT         | 422      | Compra y venta incoherentes.             |
| FX_PROPOSAL_EXPIRED                | 422      | Aceptación fuera de vigencia.            |
| FX_CONTENT_MISMATCH                | 409      | Contenido distinto del propuesto.        |
| SETTLEMENT_DESTINATION_UNVERIFIED  | 422      | Destino sin verificación suficiente.     |
| SETTLEMENT_PARTIAL_NOT_ALLOWED     | 422      | Parcialidad no admitida.                 |
| SETTLEMENT_REMAINDER_EXCEEDED      | 422      | Supera el pendiente.                     |
| EXTERNAL_EXECUTION_CONFLICT        | 409      | Resultado externo contradictorio.        |
| VALUATION_REFERENCE_MISSING        | 422      | Falta fuente exigible para la actuación. |
| DEPENDENCY_UNAVAILABLE             | 503      | Dependencia necesaria indisponible.      |

Una repetición coherente recupera el resultado original.

Una actuación durable pendiente responde 202 con seguimiento. Un fallo de comunicación no demuestra ausencia de
ejecución.

## **13. Escenarios de aceptación**

| **Escenario**                                           | **Resultado exigido**                                             |
|---------------------------------------------------------|-------------------------------------------------------------------|
| Saldo propio 100 y compromiso 40                        | Mantiene saldo 100 y capacidad nueva 60.                          |
| Dos compromisos incompatibles de 80 sobre capacidad 100 | No confirma ambos.                                                |
| Reasignación de recursos                                | No duplica cobertura ni libera antes de sustituirla.              |
| Salen 100 de efectivo y llegan 90                       | Conserva diferencia 10 sin crear efectivo.                        |
| Financiación concertada todavía no recibida             | No integra saldo confirmado.                                      |
| Flujo y compromiso de la misma obligación               | No se computan dos veces.                                         |
| Aceptación exactamente en el final de vigencia          | No se considera dentro del intervalo.                             |
| Compra interna de USD 100 por ARS 102.000               | Fondos confirma ambas piernas con el contenido aceptado.          |
| Falta transformar Riesgos después de Fondos             | Conserva resultado monetario y sigue la regularización.           |
| Conclusión de preparación cambiaria                     | No genera salida externa ficticia.                                |
| Participante confirmó pago y falta posición local       | Recupera actualización sin otro pago.                             |
| Resultado externo incierto                              | Conserva recursos y consulta antes de liberar.                    |
| Liquidación parcial                                     | Completa únicamente el remanente autorizado.                      |
| Cambio sensible de destino                              | Exige nueva verificación y autorización aplicable.                |
| Débito externo no identificado                          | No lo carga automáticamente a un cliente.                         |
| Cierre con fuentes ausentes                             | Se presenta como incompleto.                                      |
| Respuesta perdida o evento duplicado                    | Recupera el efecto original sin repetir recursos ni obligaciones. |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
