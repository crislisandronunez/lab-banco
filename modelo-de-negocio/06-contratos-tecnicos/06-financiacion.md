# **Contratos técnicos — Módulo 6. Financiación**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**               | **Responsabilidad**                                                                        |
|----------------------------|--------------------------------------------------------------------------------------------|
| Evaluación crediticia      | Antecedentes, análisis, decisiones, aprobaciones y aplicación de su alcance.               |
| Préstamos y líneas         | Constitución, cálculos, capital, límites, desembolsos, deuda e imputaciones.               |
| Garantías                  | Admisibilidad, constitución, asignación, continuidad, liberación y ejecución de respaldos. |
| Cobranzas y recuperaciones | Gestión de obligaciones vencidas, acuerdos y coordinación de recuperaciones.               |

Comprende los contratos **CI-06-001 a CI-06-043**.

Aprobación, contrato, constitución del producto, entrega de dinero y reconocimiento de capital son hechos diferentes.

La deuda de tarjetas pertenece a Cuenta de tarjeta. Su gestión de cobranza no crea otra deuda en este módulo.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

Esta consulta recupera los identificadores generados aunque se pierda la respuesta inicial. Las consultas particulares
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
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                  |
| Currency          | ARS o USD.                                                             |
| MoneyAmount       | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| SignedMoneyAmount | Cadena decimal con signo, precisión máxima 24 y hasta dos decimales.   |
| CalculationValue  | Cadena decimal con signo, precisión 30 y escala máxima 12.             |
| Rate              | Cadena decimal, precisión 24 y escala máxima 12.                       |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |

Los importes de ejecución son positivos. Acumulados, límites y remanentes admiten cero.

Los cálculos intermedios conservan al menos doce decimales. Los resultados monetarios utilizan HALF_UP a centavos.

La última amortización absorbe el residuo de capital sin aumentar el capital originalmente financiado.

### **2.3. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. FinancingParty**

Contiene:

- partyId: UUID.

- role: BORROWER, CO_BORROWER, GUARANTOR o PAYER.

- obligationBasisReference: cuando existe obligación formalizada.

- representationReference: cuando interviene representación.

Una relación declarada no constituye solidaridad, garantía ni representación.

### **3.2. FinancingCondition**

Contiene:

- conditionId: UUID.

- conditionCode: Code.

- stage: etapa.

- responsibleService: identificador del propietario.

- status: estado.

- waivable: Booleano.

- evidenceReferences: ObjectReference\[\].

- sourceReference: cuando existe antecedente.

- waiverApprovalReference: exclusivamente para dispensa.

Las etapas son BEFORE_CONTRACT, BEFORE_CONSTITUTION, BEFORE_DISBURSEMENT y BEFORE_UTILIZATION.

Los estados son PENDING, SATISFIED, FAILED y WAIVED.

Una dispensa exige condición dispensable y aprobación competente.

### **3.3. EconomicComponent**

Contiene:

- componentId: UUID.

- conceptCode: Code.

- componentType: CAPITAL, INTEREST, FEE, TAX, INSURANCE o AUTHORIZED_EXPENSE.

- amount: MoneyAmount.

- currency: Currency.

- sourceReference: ObjectReference.

- status: ACCRUED, DUE, PARTIALLY_PAID, PAID, CORRECTED o WAIVED.

- dueDate: cuando es exigible.

Los pagos y ajustes se conservan mediante referencias y acumulados, sin sobrescribir el importe original.

No se incorporan automáticamente intereses, cargos o tributos al capital.

Al invocar Fondos, el capital utiliza la categoría monetaria PRINCIPAL; conserva su concepto y referencia financiera.

### **3.4. AllocationItem**

Contiene:

- obligationReference: ObjectReference.

- componentId: UUID.

- componentType: categoría financiera.

- amount: MoneyAmount positivo.

- currency: Currency.

La suma de asignaciones y excedente coincide con el importe tratado.

### **3.5. Resultados**

Los comandos utilizan el resultado común y agregan:

- monetaryEffectReferences: ObjectReference\[\].

- exposureReferences: ObjectReference\[\].

- collateralReferences: ObjectReference\[\].

- Resultados específicos por dimensión.

Una regularización pendiente no niega un desembolso ya confirmado.

Las consultas identifican corte, versión, completitud, fuentes y datos faltantes.

## **4. Servicio Evaluación crediticia**

### **4.1. CreditAssessment**

Contiene:

- assessmentId y version, generados.

- applicationReference: ObjectReference.

- productReference: VersionedReference.

- financingPurpose: Code.

- parties: FinancingParty\[\], no vacío.

- requestedAmount: MoneyAmount positivo.

- currency: Currency.

- requestedTerm: cantidad positiva y unidad DAYS, MONTHS o YEARS.

- proposedConditions: FinancingCondition\[\].

- offeredCollateralReferences: ObjectReference\[\].

- antecedents: CreditAntecedent\[\].

- status y receivedAt, generados.

Los estados son PREPARING, AWAITING_INFORMATION, READY_FOR_ANALYSIS, ANALYSED, DECISION_PENDING, DECIDED y
REQUIRES_REVIEW.

Cada antecedente contiene tipo, fuente, fecha de observación, referencias de evidencia y condición DECLARED, VERIFIED,
OBSERVED o UNAVAILABLE.

Agrega versión y vigencia cuando la fuente las utiliza.

### **4.2. Interfaces**

| **Contrato** | **operationId**                | **Método y ruta**                                      |
|--------------|--------------------------------|--------------------------------------------------------|
| CI-06-001    | createCreditAssessment         | POST /v1/credit-assessments                            |
| CI-06-001    | addCreditAssessmentAntecedents | POST /v1/credit-assessments/{assessmentId}/antecedents |
| CI-06-002    | analyseCreditAssessment        | POST /v1/credit-assessments/{assessmentId}/analyses    |
| CI-06-003    | decideCreditAssessment         | POST /v1/credit-assessments/{assessmentId}/decisions   |
| CI-06-004    | reviewCreditApproval           | POST /v1/credit-approvals/{approvalId}/reviews         |
| CI-06-005    | getCreditAssessment            | GET /v1/credit-assessments/{assessmentId}              |
| CI-06-005    | getCreditApproval              | GET /v1/credit-approvals/{approvalId}                  |
| CI-06-006    | applyCreditApproval            | POST /v1/credit-approvals/{approvalId}/applications    |

### **4.3. Preparación y análisis**

Crear recibe contexto y datos no generados de la evaluación.

Agregar antecedentes recibe contexto y antecedentes nuevos o versiones corregidas.

La respuesta identifica suficiencia, faltantes y fuentes pendientes. Una consulta de deuda sin respuesta no se
interpreta como deuda cero.

Analizar recibe contexto, condiciones propuestas y política de Riesgos versionadas.

Devuelve:

- analysisId: UUID.

- inputHash: Hash.

- sourceReferences: ObjectReference\[\].

- indicators: indicadores tipados.

- scenarioResults: resultados de escenarios.

- conclusion: FAVOURABLE, UNFAVOURABLE o INCOMPLETE.

Cada indicador contiene código, valor decimal, unidad, fuente y metodología versionada.

Cada escenario identifica código, supuestos versionados, indicadores y conclusión.

Una garantía suficiente no sustituye capacidad de pago.

### **4.4. Decisión y aprobación**

La decisión recibe contexto, análisis, conclusión, alcance, condiciones y autorización.

Las conclusiones son APPROVED, CONDITIONALLY_APPROVED y REJECTED.

El alcance favorable contiene:

- Producto y versión compatibles.

- Personas y roles admitidos.

- Moneda.

- Importe máximo.

- Plazo máximo.

- Modalidades admitidas.

- Condiciones financieras aprobadas por referencia versionada.

La autorización es excluyente:

- HUMAN_APPROVAL: aprobación e intervinientes verificados.

- AUTOMATED_POLICY: política y versión habilitadas.

Una decisión rechazada conserva el alcance evaluado, sin habilitar su aplicación.

CreditApproval identifica evaluación, análisis, decisión, alcance, condiciones, referencias de exposición y
aplicaciones.

Una aprobación favorable agrega fecha de aprobación y límite de utilización.

Sus estados son EXPOSURE_PENDING, AVAILABLE, APPLIED, WITHDRAWN, EXPIRED y REQUIRES_REVIEW.

Una decisión rechazada no genera una aprobación utilizable.

La vigencia no supera treinta días corridos ni los antecedentes indispensables. Una aprobación con exposición pendiente
no está disponible para constituir.

### **4.5. Revisión y aplicación**

Las revisiones son REVIEW, REPLACE, WITHDRAW y EXPIRE.

Reemplazar exige alcance propuesto, verificaciones y autorización correspondientes. Conserva continuidad de exposición.

Retirar no elimina contratos ni productos constituidos. Una constitución incierta impide liberar el compromiso por
presunción.

Aplicar admite a Préstamos y líneas y a Cuenta de tarjeta para su constitución.

Recibe contexto, versión de aprobación, constitución, producto destinatario, importe, moneda, condiciones satisfechas y
compromiso de Riesgos.

Devuelve aplicación, constitución vinculada, resultado y exposición.

La misma constitución recupera el resultado. Otra incompatible produce conflicto.

Aplicar una aprobación no crea por sí mismo producto ni dinero entregado.

## **5. Servicio Garantías**

### **5.1. Collateral**

Contiene:

- collateralId y version, generados.

- collateralType: Code.

- assetOrGuarantorReference: ObjectReference.

- ownerReferences: ObjectReference\[\], no vacío.

- documentReferences: DocumentReference\[\].

- valuations: CollateralValuation\[\].

- charges: cargas conocidas.

- chargesCompleteness: COMPLETE, INCOMPLETE u OBSERVED.

- priorities: prioridades fundadas.

- insuranceReferences: ObjectReference\[\].

- currency: Currency.

- admittedCoverage, committedCoverage y availableCoverage: MoneyAmount.

- contractReference: cuando está constituida.

- status, generado.

Los estados son REGISTERED, UNDER_REVIEW, ADMITTED, CONSTITUTION_PENDING, CONSTITUTED, ALLOCATED, RELEASE_PENDING,
RELEASED y REQUIRES_RESOLUTION.

Cada valuación contiene identidad, importe, moneda, fecha, vencimiento, fuente y metodología de Riesgos.

Cada carga identifica causa, titular de la carga, prioridad y evidencia; agrega importe cuando está determinado.

Las prioridades identifican obligación o beneficiario, rango positivo y fundamento.

### **5.2. Interfaces**

| **Contrato** | **operationId**            | **Método y ruta**                                       |
|--------------|----------------------------|---------------------------------------------------------|
| CI-06-007    | registerCollateral         | POST /v1/collaterals                                    |
| CI-06-008    | constituteCollateral       | POST /v1/collaterals/{collateralId}/constitutions       |
| CI-06-008    | allocateCollateral         | POST /v1/collaterals/{collateralId}/allocations         |
| CI-06-009    | reviewCollateral           | POST /v1/collaterals/{collateralId}/reviews             |
| CI-06-010    | getCollateral              | GET /v1/collaterals/{collateralId}                      |
| CI-06-035    | actOnCollateralEnforcement | POST /v1/collaterals/{collateralId}/enforcement-actions |

### **5.3. Registro, constitución y asignación**

Registrar recibe contexto, respaldo, titulares, documentos, valuaciones, cargas y prioridades.

Las cargas desconocidas no se presentan como inexistentes.

Constituir recibe contexto, contrato y evidencia constitutiva.

Asignar recibe contexto y asignaciones. Cada una contiene identidad, obligación, prioridad y modalidad:

- MONETARY: importe y moneda.

- DEFINED_SCOPE: código de alcance y referencia de determinación de cobertura.

Las asignaciones pendientes consumen cobertura. No se concede dos veces el mismo respaldo disponible.

La comprobación y afectación de cobertura se realizan conjuntamente.

### **5.4. Revisión y liberación**

| **Acción**            | **Datos específicos**                                         |
|-----------------------|---------------------------------------------------------------|
| REVALUE               | Nueva valuación y metodología.                                |
| UPDATE_INSURANCE      | Seguro y evidencia.                                           |
| REQUEST_REINFORCEMENT | Obligaciones, faltante y vencimiento.                         |
| SUBSTITUTE            | Respaldo sustituto, asignaciones y confirmación de cobertura. |
| RELEASE_PARTIAL       | Asignaciones e importe o alcance liberable.                   |
| RELEASE_TOTAL         | Obligaciones resueltas y evidencia.                           |

La respuesta distingue liberación económica y documental.

No libera cobertura con ejecución, obligación o sustitución incierta.

### **5.5. Ejecución**

Consumidor: Cobranzas y recuperaciones.

Recibe contexto, caso, obligaciones, decisión autorizada y acción:

START, RECORD_PROCEEDING, RECORD_SALE, RECORD_GUARANTOR_PAYMENT, RECORD_ADJUDICATION o CONCLUDE.

Venta y pago de garante incorporan evidencia del ingreso. Adjudicación incorpora bien, valuación admitida y aceptación
correspondiente.

Devuelve ejecución, etapa, prioridades y aplicación pendiente.

Una adjudicación no se informa como efectivo recibido.

## **6. Servicio Préstamos y líneas**

### **6.1. Financing**

Contiene:

- financingId y version, generados.

- constitutionId: UUID.

- productReference: VersionedReference.

- contractReference: VersionedReference.

- approvalReference: VersionedReference.

- approvalApplicationReference: cuando se confirma su aplicación.

- financingType.

- parties: FinancingParty\[\], no vacío.

- currency.

- termsReference: condiciones aceptadas.

- calculationReference: cuando corresponde.

- collateralAllocationReferences.

- exposureReferences.

- limitAmount: para líneas y descubierto.

- overdraftAccountId: para descubierto.

- status, generado.

Las modalidades son AMORTIZING_LOAN, BULLET_LOAN, REVOLVING_LINE, NON_REVOLVING_LINE, OVERDRAFT y CHEQUE_DISCOUNT.

Los estados son CONSTITUTING, CONSTITUTED, ACTIVE, LIMITED, CLOSING, RESIDUAL y CLOSED.

La mora se representa separadamente del ciclo del producto.

### **6.2. Interfaces de constitución y utilización**

| **Contrato** | **operationId**            | **Método y ruta**                                   |
|--------------|----------------------------|-----------------------------------------------------|
| CI-06-011    | constituteFinancing        | POST /v1/financings                                 |
| CI-06-012    | calculateFinancingTerms    | POST /v1/financing-calculations                     |
| CI-06-013    | disburseFinancing          | POST /v1/financings/{financingId}/disbursements     |
| CI-06-014    | changeFinancingLimit       | POST /v1/financings/{financingId}/limit-changes     |
| CI-06-015    | utilizeFinancingLine       | POST /v1/financings/{financingId}/utilizations      |
| CI-06-016    | incorporateOverdraftUpdate | POST /v1/financings/{financingId}/overdraft-updates |
| CI-06-017    | accrueFinancingConcepts    | POST /v1/financings/{financingId}/accruals          |
| CI-06-018    | getFinancingPosition       | GET /v1/financings/{financingId}/position           |
| CI-06-032    | getFinancingAction         | GET /v1/financing-actions/{actionId}                |

Constituir recibe contexto y datos no generados de Financing.

Devuelve producto, aplicación de aprobación, exposición, condiciones y pendientes.

Una línea sin entregas tiene capital cero.

### **6.3. Cálculo financiero**

FinancingCalculationRequest contiene:

- Capital y moneda.

- Modalidad.

- Definición de tasa.

- Cargos y tributos.

- Sistema de amortización, para préstamo con cronograma.

- Plan de entregas, cuando existen desembolsos previstos.

- Primera fecha contractual, cantidad y frecuencia de cuotas, para amortización periódica.

- Fecha final, para pago único.

- Calendario y ajuste, cuando se utilizan días hábiles.

- Gracia, cuando está admitida.

- Seguro obligatorio, cuando corresponde.

- Condiciones aceptadas, cuando se reproduce una determinación contractual.

Los sistemas son FRENCH, GERMAN y BULLET, según producto.

La frecuencia contiene unidad DAYS o MONTHS, intervalo positivo y anclaje.

La tasa identifica modalidad FIXED o VARIABLE, clase NOMINAL o EFFECTIVE, período, base temporal y capitalización cuando
corresponde.

La fija exige valor. La variable exige fuente, margen, revisión y tratamiento de indisponibilidad.

La gracia identifica duración, unidad y tratamiento contractual de capital e interés. No habilita capitalización
implícita.

### **6.4. Resultado de cálculo**

Devuelve:

- calculationId y version.

- inputHash.

- methodologyVersion.

- Tasas aplicadas.

- Cronograma.

- Flujos.

- Resultado de CFT.

- Ajustes de redondeo.

- Supuestos.

- Vigencia, cuando existe.

Cada cuota identifica índice, fecha contractual, vencimiento ajustado, capital inicial, amortización, interés, cargos,
tributos, seguros, total y capital final.

Cada flujo identifica fecha, dirección desde el prestatario, importe, moneda y concepto.

cftResult.status admite:

- DETERMINED.

- INCOMPLETE_INPUT.

- NO_UNIQUE_RATE.

- NOT_APPLICABLE_TO_UNSPECIFIED_UTILIZATIONS.

Solo DETERMINED incluye annualEffectiveRate.

El CFT utiliza flujos netos y fechas reales. Una retención reduce el desembolso inicial y no vuelve a sumarse como pago.

No se publica tasa definitiva con costos obligatorios desconocidos.

### **6.5. Desembolso**

Recibe contexto, identidad de desembolso, bruto, retenciones, neto, moneda, destino, fecha, cálculo aceptado y
condiciones satisfechas.

Se cumple:

**Neto = bruto − retenciones.**

Cada retención identifica concepto, destinatario y referencia de aplicación.

La respuesta distingue:

- Entrega.

- Capital reconocido.

- Retenciones aplicadas.

- Exposición.

- Pendientes.

Una entrega rechazada sin efectos no crea capital. Una entrega incierta no se repite ni se presenta como ausencia de
deuda comprobada.

Cambiar fecha con consecuencias económicas exige cálculo y aceptación antes de ejecutar.

### **6.6. Límites y utilizaciones**

Cambiar límite recibe contexto, nuevo límite, moneda, vigencia, aprobación, exposición y modificación contractual cuando
corresponde.

Devuelve límite autorizado, aplicado, utilizado, comprometido, exceso y pendientes.

En descubierto identifica la aplicación confirmada por Fondos.

Utilizar línea recibe contexto, identidad de utilización, importe, moneda, destino, fecha y condiciones aceptadas.

Los usos concurrentes comprometen capacidad localmente.

La línea renovable recupera capacidad por capital devuelto. La no renovable conserva el consumo acumulado según el
acuerdo.

El descubierto no crea capital mediante esta interfaz. Su utilización inmediata pertenece a Fondos.

### **6.7. Actualizaciones de descubierto**

Recibe contexto, cuenta, corte de Fondos, referencia del hecho monetario, capital utilizado, cobertura comprometida,
protección y exceso.

Incluye:

- sourcePositionVersion: versión autoritativa de la posición.

- sourceSequence: cuando la actualización está asociada a un movimiento.

El orden general se controla mediante la versión de posición. La secuencia de movimientos no sustituye la versión de
cambios sin movimiento.

Fondos acredita sus hechos. Una actuación administrativa puede solicitar comprobación, pero no declarar capital distinto
de la fuente.

Una acreditación ordinaria reduce capital según Fondos, sin otra imputación que lo cancele dos veces.

### **6.8. Devengamiento y posición**

Devengar recibe período, condiciones, cortes de posición, tasas y determinaciones tributarias.

El período incluye inicio y excluye final.

Se separan tramos ante cambios de capital o tasa. No se redondea diariamente antes de acumular.

No se generan intereses sobre intereses, comisiones o tributos.

En cuotas amortizables, el capital vencido utiliza remuneración simple con tasa diaria equivalente a la efectiva anual
contractual. Líneas y descubierto mantienen Actual/365.

No se agrega tasa punitoria ni penalidad fija ordinaria por atraso.

Una referencia variable indisponible puede utilizar la última válida durante un período de revisión, si lo permite el
acuerdo. Se identifica provisionalidad.

La posición devuelve capital, conceptos, cronograma, compromisos, pagos, preparaciones, mora, garantías, exposición y
saldos a favor. El descubierto conserva el corte autoritativo de Fondos.

## **7. Pagos y cobro de descubierto**

### **7.1. Interfaces**

| **Contrato** | **operationId**                    | **Método y ruta**                                                   |
|--------------|------------------------------------|---------------------------------------------------------------------|
| CI-06-019    | prepareFinancingPayment            | POST /v1/financings/{financingId}/payment-preparations              |
| CI-06-019    | getFinancingPaymentPreparation     | GET /v1/financing-payment-preparations/{preparationId}              |
| CI-06-020    | resolveFinancingPaymentPreparation | POST /v1/financing-payment-preparations/{preparationId}/resolutions |
| CI-06-021    | collectOverdraftCharges            | POST /v1/financings/{financingId}/overdraft-charge-collections      |

### **7.2. Preparación de aplicación**

Recibe contexto, pago, pagador, deudor, importe, moneda, fecha efectiva prevista y etapa:

- PRELIMINARY_ACCEPTANCE.

- PREPARE_APPLICATION.

Puede agregar distribución solicitada, sujeta al acuerdo y a las reglas admitidas.

Devuelve preparación, versión de deuda, AllocationItem\[\], excedente previsto, condiciones y estado:

PRELIMINARILY_ACCEPTED, PREPARED, APPLIED, CANCELLED o REQUIRES_RESOLUTION.

La aceptación preliminar no compromete distribución definitiva.

Las preparaciones concurrentes consideran asignaciones existentes. Preparar no demuestra ingreso ni cancela deuda.

### **7.3. Confirmación y cancelación**

Las modalidades son:

- CONFIRM_RECEIPT: importe, moneda, fecha efectiva y evidencia del ingreso.

- CANCEL_NOT_EXECUTED: evidencia que descarta ejecución pendiente.

La referencia de pago original se conserva en todas las etapas.

La confirmación devuelve asignaciones aplicadas, capital cancelado, deuda, excedente y pendientes.

La imputación ordinaria atiende:

1.  Obligaciones vencidas, desde la más antigua.

2.  Obligaciones exigibles del día.

3.  Dentro de cada obligación, interés ordinario y capital.

4.  Tributos asociados.

5.  Comisiones y gastos posteriores.

El excedente no se precancela silenciosamente: requiere autorización o devolución.

En descubierto, la reducción de capital confirmada por Fondos constituye su única cancelación monetaria.

### **7.4. Cobro de conceptos de descubierto**

Recibe contexto, cuenta, componentes, importe, moneda, modalidad y autorización.

Las modalidades son TOTAL y EXPLICIT_PARTIAL.

Utiliza exclusivamente fondos propios disponibles. No genera descubierto para cobrar intereses del propio descubierto.

Devuelve cobro, insuficiencia o incertidumbre y referencias originales.

## **8. Precancelación, mora y modificaciones**

### **8.1. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                                   |
|--------------|------------------------------|-----------------------------------------------------|
| CI-06-022    | quoteFinancingPrepayment     | POST /v1/financings/{financingId}/prepayment-quotes |
| CI-06-023    | executeFinancingPrepayment   | POST /v1/financings/{financingId}/prepayments       |
| CI-06-024    | assessFinancingDelinquency   | POST /v1/delinquency-assessments                    |
| CI-06-025    | restructureFinancing         | POST /v1/financings/{financingId}/restructurings    |
| CI-06-026    | calculatePortfolioProvisions | POST /v1/portfolio-provision-calculations           |
| CI-06-027    | correctFinancing             | POST /v1/financings/{financingId}/corrections       |
| CI-06-028    | closeFinancing               | POST /v1/financings/{financingId}/closures          |

### **8.2. Precancelación**

Cotizar recibe contexto, fecha efectiva y alcance TOTAL o PARTIAL.

La parcial exige capital objetivo y tratamiento REDUCE_TERM o REDUCE_INSTALLMENT.

Devuelve identidad, versión de deuda, componentes, total, nuevo cronograma y vigencia.

El límite de vigencia se representa mediante el inicio del día siguiente a la fecha efectiva en la zona institucional,
como final excluido.

No cobra intereses futuros ni comisión ordinaria de precancelación.

Ejecutar recibe liquidación vigente, aceptación y financiación del pago mediante una variante:

- CONFIRMED_PAYMENT: evidencia del ingreso.

- AUTHORIZED_COLLECTION: instrucción de cobro autorizada.

Devuelve cancelación de conceptos, cronograma, exposición, garantías y excedentes.

No vuelve a cobrar un ingreso ya confirmado.

### **8.3. Mora**

Recibe contexto, financiaciones o cartera de forma excluyente, corte y pagos conocidos pendientes.

Devuelve obligaciones vencidas, conceptos, vencimiento ajustado, inicio de mora, días de atraso y comprobaciones
pendientes.

La mora comienza al día siguiente del vencimiento ajustado.

Un ingreso oportuno pendiente de imputar se comprueba antes de atribuir incumplimiento. La demora interna no crea mora
adicional.

Riesgos conserva la clasificación especializada.

### **8.4. Reestructuración**

Recibe acuerdo efectivo, evaluación cuando corresponde, aprobación, exposición, corte de deuda, tratamientos y
garantías.

Cada tratamiento identifica componente original y modalidad:

- KEEP.

- RESCHEDULE.

- REPLACE.

- AUTHORIZED_WAIVER.

Una sustitución identifica la nueva obligación. Una quita identifica aprobación y condición cuando existe.

No mantiene el mismo capital exigible en ambas obligaciones.

Una quita condicionada no se aplica definitivamente antes de cumplir su condición.

### **8.5. Previsiones**

Recibe cartera, corte, posiciones, clasificaciones, metodología, garantías e importes previamente reconocidos.

Devuelve cálculo, base, requerido, reconocido y variación con signo.

No calcula otra previsión sobre deuda de tarjetas administrada por Cuenta de tarjeta.

Previsionar o dar de baja contablemente no paga ni condona deuda.

### **8.6. Corrección y cierre**

Corregir recibe hecho original, tipo, fecha, componentes, tratamiento, evidencia y aprobación cuando es manual.

Los tipos son DEBT_COMPONENT, PAYMENT_ALLOCATION, MONETARY_EFFECT y DELINQUENCY.

Devuelve referencias de corrección y consecuencias sobre deuda, dinero, mora, exposición y documentos.

Redistribuir un ingreso no crea otro cobro.

Cerrar recibe causa y evidencias de extinción.

La respuesta separa cese de usos, cancelación económica, régimen residual, garantías, exposición, instrumentos, saldos a
favor y documentos.

Capital cero con interés pendiente no permite informar cancelación integral.

## **9. Descuento de cheques**

### **9.1. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                                                            |
|--------------|------------------------------|------------------------------------------------------------------------------|
| CI-06-029    | prepareChequeDiscount        | POST /v1/cheque-discount-operations                                          |
| CI-06-030    | advanceChequeDiscount        | POST /v1/cheque-discount-operations/{discountOperationId}/advances           |
| CI-06-031    | recordDiscountedChequeResult | POST /v1/cheque-discount-operations/{discountOperationId}/instrument-results |

### **9.2. Preparación y anticipo**

Cada cheque mantiene una financiación individual. Solo se admite descuento con responsabilidad del cliente.

Preparar recibe cliente, instrumento diferido, moneda, fecha de anticipo, condiciones financieras y antecedentes.

Devuelve compromiso del cheque, cálculo, neto, CFT, evaluación y condiciones.

La fecha de pago debe ser futura respecto del anticipo.

Anticipar recibe contrato, aceptación, aprobación, compromiso y cálculo aceptado.

Confirma cesión mediante Cheques y ECHEQ y ejecuta anticipo y retenciones.

Devuelve financiación, nominal, neto, interés retenido no devengado y efectos.

El interés anticipado se reconoce durante el período. No se registra íntegramente como ingreso inicial.

Una cesión confirmada con anticipo fallido requiere regularización comprobada.

### **9.3. Resultado del instrumento**

Recibe instrumento, presentación, resultado PAID o REJECTED, importe, moneda, fecha y evidencias.

PAID exige referencia monetaria confirmada. REJECTED no admite presentar como cobrado un importe incierto.

El cobro cancela la financiación correspondiente sin acreditar otra vez el nominal como saldo libre del cliente.

El rechazo no crea otro capital.

Pago del cliente y cobro del cheque se ordenan sobre la misma deuda. Cualquier excedente conserva su titular y destino.

## **10. Servicio Cobranzas y recuperaciones**

### **10.1. CollectionCase**

Contiene:

- caseId y version, generados.

- creditorService.

- obligationReferences.

- debtorReference.

- debtCutReference.

- delinquencyReference.

- knownPendingPayments.

- disputeReferences.

- collateralReferences.

- strategyCode.

- assignedAgentReference, cuando está asignado.

- status, generado.

Los estados son OPEN, UNDER_MANAGEMENT, AGREEMENT_PENDING, AGREEMENT_ACTIVE, ENFORCEMENT, RESIDUAL y CLOSED.

La deuda es una referencia del acreedor, no una obligación nueva del caso.

### **10.2. Interfaces**

| **Contrato** | **operationId**            | **Método y ruta**                                     |
|--------------|----------------------------|-------------------------------------------------------|
| CI-06-033    | createCollectionCase       | POST /v1/collection-cases                             |
| CI-06-033    | updateCollectionCaseDebt   | POST /v1/collection-cases/{caseId}/debt-updates       |
| CI-06-034    | actOnCollectionCase        | POST /v1/collection-cases/{caseId}/management-actions |
| CI-06-036    | registerCollectionRecovery | POST /v1/collection-cases/{caseId}/recoveries         |
| CI-06-037    | getCollectionCase          | GET /v1/collection-cases/{caseId}                     |
| CI-06-037    | closeCollectionCase        | POST /v1/collection-cases/{caseId}/closures           |

### **10.3. Gestiones y acuerdos**

Crear recibe contexto y datos no generados del caso.

Actualizar deuda recibe posición del acreedor, pagos y disputas. La fuente autoritativa prevalece sobre la copia del
caso.

Las gestiones son:

- RECORD_CONTACT: canal, interlocutor, fecha y resultado.

- RECORD_PROMISE: obligaciones, importes y fechas prometidas.

- SUBMIT_PROPOSAL: conceptos, calendario, quitas y condiciones.

- RECORD_AGREEMENT: aceptación, aprobación y contrato cuando corresponde.

- RECORD_AGREEMENT_RESULT: hechos, cumplimiento e incumplimientos.

Una promesa no reduce deuda.

Los gastos externos exigen evidencia. Los gastos internos ordinarios no se agregan como penalidad.

### **10.4. Recuperaciones**

Recibe identidad de recupero, fuente, evidencia, moneda, gastos justificados, prioridades y obligaciones destinatarias.

Las fuentes son:

- CASH_RECEIPT.

- GUARANTOR_PAYMENT.

- ASSET_SALE.

- ADJUDICATION.

Las tres primeras incorporan importe y evidencia de ingreso. La adjudicación incorpora valor admitido, bien y resolución
correspondiente.

La aplicación utiliza los contratos del acreedor. Un bien adjudicado no se presenta como dinero.

Devuelve recupero, gastos, aplicaciones, excedentes y pendientes.

No informa deuda cancelada mientras la imputación siga pendiente.

Cerrar el caso exige causa y evidencia, pero no extingue por sí mismo la deuda.

## **11. Coordinación y recuperación**

Las operaciones utilizan las autoridades y preparaciones exigidas por Institución, Representación, Restricciones y los
productos involucrados.

Se conservan referencias de aprobación, constitución, exposición, garantía, desembolso, ingreso, instrumento e
imputación.

La incertidumbre se resuelve con identidades originales.

No se libera exposición, garantía o instrumento por timeout.

La aplicación de una aprobación no permite inventar un producto constituido ni una entrega realizada.

No se mantienen transacciones SQL abiertas durante llamadas remotas. Cada propietario confirma sus efectos locales,
resultado y outbox.

## **12. Eventos**

### **12.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                                        | **Tema**                         | **Agregado**            |
|--------------|-------------------------------------------------|----------------------------------|-------------------------|
| CI-06-038    | CreditDecisionChanged                           | banking.credit-assessment.events | Decisión crediticia.    |
| CI-06-039    | CollateralChanged                               | banking.collateral.events        | Garantía.               |
| CI-06-040    | FinancingConstitutionOrCapacityChanged          | banking.lending.events           | Financiación.           |
| CI-06-041    | FinancingDebtOrApplicationChanged               | banking.lending.events           | Financiación.           |
| CI-06-042    | FinancingDelinquencyOrPortfolioTreatmentChanged | banking.lending.events           | Financiación o cartera. |
| CI-06-043    | CollectionOrRecoveryChanged                     | banking.collection.events        | Caso de cobranza.       |

La versión inicial de esquema es 1. La clave es el identificador del agregado.

### **12.2. CI-06-038 — CreditDecisionChanged**

Payload obligatorio:

- decisionId: UUID.

- decisionVersion: Version.

- assessmentReference: VersionedReference.

- changeType: DECIDED, REVIEWED, REPLACED, WITHDRAWN, EXPIRED o APPLIED.

- decision: conclusión crediticia.

- conditions: FinancingCondition\[\].

- exposureReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Una decisión favorable incluye aprobación, alcance, estado y vigencia.

APPLIED incluye aplicación y constitución destinataria.

El identificador de decisión permanece estable durante su ciclo; para una aprobación favorable corresponde a la
identidad de aprobación consultable.

aggregateType es CREDIT_DECISION.

### **12.3. CI-06-039 — CollateralChanged**

Payload obligatorio:

- collateralId: UUID.

- collateralVersion: Version.

- changeType: REGISTERED, VALUED, CONSTITUTED, ALLOCATED, RELEASED o ENFORCEMENT_UPDATED.

- status: estado de garantía.

- currency: Currency.

- admittedCoverage, committedCoverage y availableCoverage: MoneyAmount.

- allocationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Agrega valuación, liberación o ejecución según el cambio.

aggregateType es COLLATERAL.

### **12.4. CI-06-040 — FinancingConstitutionOrCapacityChanged**

Payload obligatorio:

- financingId: UUID.

- financingVersion: Version.

- constitutionId: UUID.

- changeType: CONSTITUTED, LIMIT_CHANGED, UTILIZATION_RECORDED, DISBURSEMENT_RECORDED o USES_CEASED.

- financingType: modalidad.

- contractReference: VersionedReference.

- approvalReference: VersionedReference.

- currency: Currency.

- monetaryEffectReferences: ObjectReference\[\].

- exposureReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Las líneas agregan límite, capital utilizado y comprometido.

Un desembolso agrega identidad, bruto, retenciones, neto y resultado de entrega.

aggregateType es FINANCING.

El evento referencia dinero ejecutado; no ordena otra entrega.

### **12.5. CI-06-041 — FinancingDebtOrApplicationChanged**

Payload obligatorio:

- financingId: UUID.

- financingVersion: Version.

- changeType: ACCRUED, PAYMENT_APPLIED, PREPAID, RESTRUCTURED, CORRECTED o CLOSED.

- currency: Currency.

- affectedComponentReferences: ObjectReference\[\].

- positionReference: ObjectReference.

- monetaryEffectReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Campos condicionales:

- Pago y asignaciones, para aplicación.

- Liquidación y capital cancelado, para precancelación.

- Acuerdo y obligaciones sustituidas, para reestructuración.

- Hecho original y corrección, para rectificación.

- Situaciones económica y residual, para cierre.

aggregateType es FINANCING.

### **12.6. CI-06-042 — FinancingDelinquencyOrPortfolioTreatmentChanged**

El payload utiliza variantes excluyentes.

Con aggregateType igual a FINANCING contiene:

- Financiación y versión.

- changeType: DELINQUENCY_ASSESSED o DELINQUENCY_CORRECTED.

- Corte.

- Referencia de evaluación.

- Obligaciones vencidas.

- Días de atraso.

- Pendientes.

Con aggregateType igual a FINANCING_PORTFOLIO contiene:

- Cartera y versión.

- changeType: PROVISION_CALCULATED.

- Corte.

- Cálculo y metodología versionados.

- Clasificaciones utilizadas.

- Moneda.

- Importe requerido, reconocido y variación.

- Pendientes.

Una previsión no comunica cancelación de deuda.

### **12.7. CI-06-043 — CollectionOrRecoveryChanged**

Payload obligatorio:

- caseId: UUID.

- caseVersion: Version.

- creditorService: identificador del acreedor.

- changeType: OPENED, MANAGEMENT_RECORDED, AGREEMENT_RECORDED, RECOVERY_RECORDED, APPLICATION_UPDATED o CLOSED.

- obligationReferences: ObjectReference\[\].

- status: estado del caso.

- pendingComponents: pendientes comunes.

Las recuperaciones agregan fuente, importe o valor admitido, moneda, aplicaciones y excedente.

Los acuerdos agregan referencia, aceptación y aprobación.

aggregateType es COLLECTION_CASE.

### **12.8. Consumidores y orden**

| **Información**          | **Consumidores autorizados**                                                                     |
|--------------------------|--------------------------------------------------------------------------------------------------|
| Decisión crediticia      | Solicitudes, Contratos, Préstamos y líneas, Cuenta de tarjeta, Riesgos y Gestión administrativa. |
| Garantías                | Acreedores, Evaluación crediticia, Riesgos, Cobranzas y Contabilidad.                            |
| Constitución y capacidad | Solicitudes, Contratos, Riesgos, Garantías, Tesorería y Contabilidad.                            |
| Deuda e imputación       | Pagos, Cobranzas, Garantías, Riesgos, Contabilidad y Extractos.                                  |
| Mora y cartera           | Riesgos, Cobranzas, Contabilidad e Información regulatoria.                                      |
| Recuperaciones           | Acreedor, Garantías, Riesgos, Contabilidad y Gestión administrativa.                             |

Documentos y Comunicaciones reciben únicamente datos necesarios para su función.

Los consumidores comparan versiones. Ante una discontinuidad relevante recuperan el estado autoritativo.

Un evento de aplicación no representa otro ingreso. Un evento de garantía no sustituye la aplicación del acreedor.

## **13. Seguridad y errores**

Se separan permisos para preparar, analizar, aprobar, aplicar, constituir, calcular, desembolsar, administrar límites,
afectar garantías, imputar, precancelar, reestructurar, corregir y gestionar recuperaciones.

Preparador y aprobador son distintos cuando existe separación exigida.

Un permiso de cobranza no habilita modificar unilateralmente deuda.

| **Código**                           | **HTTP** | **Significado**                              |
|--------------------------------------|----------|----------------------------------------------|
| CONTENT_CONFLICT                     | 409      | Identidad con contenido diferente.           |
| VERSION_CONFLICT                     | 409      | Versión incompatible.                        |
| CREDIT_INFORMATION_INCOMPLETE        | 422      | Faltan antecedentes exigibles.               |
| CREDIT_APPROVAL_EXPIRED              | 422      | Aprobación vencida.                          |
| CREDIT_APPROVAL_WITHDRAWN            | 422      | Aprobación retirada.                         |
| CREDIT_APPROVAL_SCOPE_MISMATCH       | 422      | Constitución fuera del alcance.              |
| CREDIT_APPROVAL_APPLICATION_CONFLICT | 409      | Uso incompatible.                            |
| EXPOSURE_COMMITMENT_REQUIRED         | 422      | Falta afectación exigible.                   |
| COLLATERAL_COVERAGE_INSUFFICIENT     | 422      | Cobertura insuficiente.                      |
| COLLATERAL_CONSTITUTION_PENDING      | 422      | Falta constitución exigible.                 |
| COLLATERAL_RELEASE_CONFLICT          | 409      | Obligación o compromiso sin resolver.        |
| CALCULATION_CONTENT_MISMATCH         | 409      | Cálculo distinto del aceptado.               |
| DISBURSEMENT_ALREADY_REGISTERED      | 409      | Otra actuación intenta repetir la entrega.   |
| LINE_CAPACITY_INSUFFICIENT           | 422      | Capacidad insuficiente.                      |
| OVERDRAFT_UTILIZATION_ROUTE_INVALID  | 422      | Uso de circuito ajeno a Fondos.              |
| SOURCE_SEQUENCE_GAP                  | 409      | Falta un antecedente necesario de la fuente. |
| PAYMENT_PREPARATION_CONFLICT         | 409      | Aplicación incompatible.                     |
| PREPAYMENT_QUOTE_EXPIRED             | 422      | Liquidación vencida.                         |
| DISCOUNT_INSTRUMENT_UNAVAILABLE      | 422      | Cheque con uso incompatible.                 |
| RECOVERY_APPLICATION_PENDING         | 409      | No puede concluirse sin imputación.          |
| DEPENDENCY_UNAVAILABLE               | 503      | Dependencia necesaria indisponible.          |

Una repetición coherente recupera el resultado original.

Una actuación durable pendiente responde 202 y seguimiento.

## **14. Escenarios de aceptación**

| **Escenario**                                         | **Resultado exigido**                                                |
|-------------------------------------------------------|----------------------------------------------------------------------|
| Dos aprobaciones de 80 compiten por capacidad 100     | La afectación de Riesgos impide comprometer 160.                     |
| Aprobación aplicada sin producto confirmado           | Mantiene constitución pendiente, sin inventar entrega.               |
| Bruto 500 y retención 10                              | Entrega neta 490 con conceptos separados.                            |
| Se pierde respuesta de desembolso                     | Recupera el efecto original sin otra entrega.                        |
| Garantía comprometida en dos operaciones concurrentes | No asigna dos veces la misma cobertura.                              |
| Línea no renovable amortizada                         | No recupera capacidad que el contrato no permite reutilizar.         |
| Descubierto 100, interés pendiente 10 y crédito 50    | Fondos informa capital 50; interés permanece separado.               |
| Pago 40 con interés exigible 10 y capital 90          | Aplica interés 10 y capital 30 después de confirmar ingreso.         |
| Pago oportuno pendiente de imputación                 | No crea mora por demora interna.                                     |
| Precancelación                                        | Excluye intereses futuros y conserva aceptación vigente.             |
| Reestructuración                                      | No deja el mismo capital exigible en obligaciones antiguas y nuevas. |
| Descuento de cheque                                   | Separa nominal, neto e interés retenido no devengado.                |
| Cobro del cheque y pago del cliente compiten          | Cancelan una sola deuda e identifican excedente.                     |
| Recupero 80 con deuda aplicable 70                    | Aplica 70 y conserva excedente 10.                                   |
| Capital cero con interés pendiente                    | No informa cancelación integral.                                     |
| Cierre de cobranza                                    | No extingue deuda por sí mismo.                                      |
| Evento duplicado o antiguo                            | No repite dinero ni reemplaza una posición posterior.                |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
