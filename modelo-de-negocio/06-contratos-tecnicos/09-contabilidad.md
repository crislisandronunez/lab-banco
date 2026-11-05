# **Contratos técnicos — Módulo 9. Contabilidad**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**          | **Responsabilidad**                                                                           |
|-----------------------|-----------------------------------------------------------------------------------------------|
| Registración contable | Plan, reglas, integración de hechos, asientos, saldos y controles locales de registración.    |
| Cierres contables     | Períodos, integridad de fuentes, conciliaciones, barreras, cierres y reaperturas autorizadas. |
| Información contable  | Estructuras de presentación, cifras, procedencia, emisión y rectificación de informes.        |

Comprende los contratos **CI-09-001 a CI-09-033**.

Contabilidad representa hechos económicos. No modifica saldos operativos, deudas, tenencias ni recursos de otros
servicios para conseguir un asiento equilibrado.

Información regulatoria conserva la elaboración y presentación de sus declaraciones.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las consultas mediante POST no crean compromisos ni requieren clave de idempotencia.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

Permite recuperar resultados e identificadores generados aunque se pierda la respuesta inicial.

### **2.2. Tipos**

| **Tipo**         | **Representación**                                                     |
|------------------|------------------------------------------------------------------------|
| UUID             | Identificador interno.                                                 |
| Version          | Entero positivo dentro del rango de long.                              |
| Sequence         | Entero positivo dentro del rango de long.                              |
| Code             | Cadena de 1 a 80 caracteres.                                           |
| Instant          | Instante UTC.                                                          |
| Date             | Fecha YYYY-MM-DD.                                                      |
| Hash             | SHA-256 hexadecimal de 64 caracteres.                                  |
| Currency         | ARS o USD.                                                             |
| AccountingAmount | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| SignedAmount     | Cadena decimal con signo, precisión máxima 24 y hasta dos decimales.   |
| Quantity         | Cadena decimal no negativa, precisión 30 y escala máxima 12.           |
| ExchangeRate     | Cadena decimal positiva, precisión 24 y escala máxima 12.              |
| Reason           | Cadena de 1 a 4000 caracteres.                                         |

Las partidas utilizan sentido DEBIT o CREDIT, separado del importe.

La moneda de presentación es ARS.

La conversión utiliza HALF_UP a centavos. Los residuos exclusivos de conversión se identifican mediante reglas de
redondeo; no se utilizan para ocultar diferencias de origen.

### **2.3. Fechas y obligatoriedad**

Se distinguen:

- economicDate: fecha del hecho.

- receivedAt: recepción efectiva.

- postedAt: confirmación contable.

- accountingPeriodId: período de registración.

- cutAt: corte de información.

El día económico utiliza America/Argentina/Buenos_Aires.

El corte de compensación no sustituye el contable.

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado. Las colecciones
pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

## **3. Estructuras contables compartidas**

### **3.1. AccountingDimensions**

Es una colección de:

- dimensionCode: Code.

- valueReference: ObjectReference.

Las dimensiones permitidas se definen por cuenta y regla. Una dimensión no se repite en la misma partida.

No se admiten atributos arbitrarios ni valores incompatibles con el esquema aprobado.

### **3.2. EconomicIdentity**

Contiene:

- operationId: UUID.

- occurrenceId: UUID.

- consequenceCode: Code.

- ownerService: propietario de la consecuencia.

- sourceFactId: UUID del hecho autoritativo.

La unicidad de una consecuencia se controla por propietario, operación, ocurrencia y código de consecuencia.

sourceFactId identifica su antecedente autoritativo. Cambiarlo no permite registrar otra vez la misma consecuencia.

Otros servicios pueden aportar antecedentes vinculados a esa identidad, sin declarar una segunda consecuencia
equivalente.

### **3.3. AccountingCut**

Contiene:

- cutId: UUID.

- cutAt: Instant.

- ledgerSequence: Sequence, cuando existen asientos.

- periodReference: VersionedReference.

- contentHash: Hash.

- sourceCuts: cortes de fuentes, cuando integra varias.

Cada corte de fuente contiene servicio, ámbito, instante y modalidad de cobertura.

Las modalidades son:

- EXPLICIT_SET: referencias de hechos.

- SEQUENCE_RANGES: flujo y rangos inclusivos.

- EMPTY: ausencia declarada para ese ámbito y corte.

Una secuencia de una fuente no se interpreta como secuencia de todos sus objetos.

### **3.4. AccountingLine**

| **Campo**                  | **Tipo**                      | **Condición**             |
|----------------------------|-------------------------------|---------------------------|
| lineId                     | UUID                          | Generado al confirmar.    |
| lineNumber                 | Entero positivo               | Único dentro del asiento. |
| ledgerAccountReference     | VersionedReference            | Obligatorio.              |
| side                       | DEBIT o CREDIT                | Obligatorio.              |
| originalAmount             | AccountingAmount positivo     | Obligatorio.              |
| originalCurrency           | Currency                      | Obligatorio.              |
| presentationAmount         | AccountingAmount              | Obligatorio.              |
| presentationCurrency       | ARS                           | Obligatorio.              |
| dimensions                 | AccountingDimensions          | Obligatorio.              |
| exchangeRateReference      | ObjectReference               | Para conversión.          |
| economicComponentReference | ObjectReference               | Cuando corresponde.       |
| description                | Texto de hasta 500 caracteres | Opcional.                 |

Para ARS coinciden importe original y presentación.

Un importe de presentación redondeado a cero conserva el importe original y la regla aplicada; no se inventa un centavo.

Los registros de orden se equilibran y presentan separados de los patrimoniales.

### **3.5. AccountingEntry**

Contiene:

- entryId y version.

- entryType.

- economicIdentity.

- factReferences.

- ruleReference.

- economicDate.

- postedAt.

- periodReference.

- periodControlVersion.

- ledgerSequence.

- lines: AccountingLine\[\], no vacío.

- totals: débitos y créditos de presentación.

- correctionReferences.

- approvalReference, cuando corresponde.

Los tipos son AUTOMATIC, MANUAL, REVERSAL, RECLASSIFICATION, REVALUATION y YEAR_END_RESULT_TRANSFER.

Solo un asiento confirmado expone secuencia definitiva.

La confirmación exige equilibrio y cumplimiento de la regla multimoneda aplicable.

## **4. Plan y reglas contables**

### **4.1. LedgerAccount**

Contiene:

- Identidad y versión.

- Código y nombre.

- accountClass.

- normalBalance.

- accountRole.

- Cuenta superior, salvo raíz.

- Monedas permitidas.

- Dimensiones requeridas.

- Vigencia.

- Estado.

Las clases son ASSET, LIABILITY, EQUITY, INCOME, EXPENSE y MEMORANDUM.

El saldo normal es DEBIT o CREDIT.

El rol es GROUP o POSTING.

Los estados son DRAFT, APPROVED, ENABLED y RETIRED.

Una cuenta agrupadora no recibe partidas. Un código con historia no se reutiliza cambiando su naturaleza.

### **4.2. AccountingRule**

Contiene:

- Identidad y versión.

- Tipo y versión de esquema del hecho.

- Código de consecuencia.

- Fuentes requeridas.

- Definición de correlación.

- Definiciones de partidas.

- Política de fechas.

- Política de corrección.

- Tratamiento transitorio.

- Vigencia.

- Aprobación.

Cada fuente identifica propietario, tipo de antecedente, campos exigidos y condición de integridad.

Cada partida define cuenta o selector registrado, sentido, origen del importe y dimensiones.

Los selectores y operaciones son tipados y versionados. No ejecutan SQL ni scripts recibidos como configuración.

### **4.3. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                                       |
|--------------|------------------------------|---------------------------------------------------------|
| CI-09-001    | createLedgerAccountProposal  | POST /v1/ledger-account-proposals                       |
| CI-09-001    | actOnLedgerAccountProposal   | POST /v1/ledger-account-proposals/{proposalId}/actions  |
| CI-09-002    | listLedgerAccounts           | GET /v1/ledger-accounts                                 |
| CI-09-003    | createAccountingRuleProposal | POST /v1/accounting-rule-proposals                      |
| CI-09-003    | actOnAccountingRuleProposal  | POST /v1/accounting-rule-proposals/{proposalId}/actions |
| CI-09-004    | listAccountingRules          | GET /v1/accounting-rules                                |

Crear propuesta de cuenta recibe contexto, modalidad CREATE, NEW_VERSION, ENABLE o RETIRE, destinatario cuando existe y
definición pertinente.

Las acciones son SUBMIT, APPROVE, APPLY y WITHDRAW.

Las acciones de regla son SUBMIT, APPROVE, ENABLE y WITHDRAW.

Las aprobaciones vinculan versión y huella del contenido.

La validación de reglas incluye equilibrio, fuentes, identidad económica, fechas, correcciones y ejemplos.

Una versión nueva no reinterpreta hechos históricos automáticamente.

Retirar una cuenta con saldo exige tratamiento explícito.

Las consultas admiten finalidad, filtros, versión o momento, y paginación estable. No seleccionan reglas por semejanza.

## **5. Hechos e integración**

### **5.1. AccountingFact**

Contiene:

- factId: UUID.

- economicIdentity: EconomicIdentity.

- factType: Code.

- factSchemaVersion: entero positivo.

- ownerService.

- occurredAt.

- economicDate.

- components: FactComponent\[\].

- operationalReferences: ObjectReference\[\].

- evidenceReferences: ObjectReference\[\].

- inputHash: Hash.

- correctedFactReference: para corrección.

Cada componente contiene concepto, rol, fuente y modalidad excluyente:

- MONETARY: importe y moneda.

- UNITS: cantidad, instrumento y unidad.

Los roles incluyen CAPITAL, INTEREST, FEE, TAX, PROVISION, CUSTODY y COMMITMENT, según el esquema registrado.

Las unidades de custodia no se presentan como dinero.

### **5.2. Interfaces**

| **Contrato** | **operationId**                     | **Método y ruta**                                            |
|--------------|-------------------------------------|--------------------------------------------------------------|
| CI-09-005    | incorporateAccountingFact           | POST /v1/accounting-facts                                    |
| CI-09-006    | getAccountingFactTreatment          | GET /v1/accounting-facts/{factId}/treatment                  |
| CI-09-007    | addAccountingIntegrationAntecedents | POST /v1/accounting-integrations/{integrationId}/antecedents |

Incorporar recibe contexto, recordRole y contenido.

Las modalidades son:

- ECONOMIC_FACT: hecho del propietario de la consecuencia.

- SUPPORTING_ANTECEDENT: evidencia vinculada a la consecuencia y su propietario.

Devuelve recepción, hecho, integración, asientos y tratamiento:

RECEIVED, INTEGRATION_PENDING, READY_TO_POST, POSTED, INCONSISTENT o REQUIRES_REVIEW.

Los eventos de origen utilizan las mismas identidades y validaciones. Su reentrega no crea otro hecho económico.

Completar integración recibe contexto, identidad económica y antecedentes.

Devuelve coincidencias, diferencias, faltantes y aptitud de registración.

Un desembolso bruto de 500 y crédito neto de 490 sin retención acreditada generan inconsistencia. No se inventa una
comisión.

La falta de asiento no demuestra que la operación bancaria no ocurrió.

## **6. Asientos, correcciones y consultas**

### **6.1. Interfaces**

| **Contrato** | **operationId**            | **Método y ruta**                                          |
|--------------|----------------------------|------------------------------------------------------------|
| CI-09-008    | postAccountingFact         | POST /v1/accounting-posting-actions                        |
| CI-09-009    | createManualEntryProposal  | POST /v1/manual-entry-proposals                            |
| CI-09-009    | confirmManualEntryProposal | POST /v1/manual-entry-proposals/{proposalId}/confirmations |
| CI-09-010    | correctAccountingEntry     | POST /v1/accounting-entries/{entryId}/corrections          |
| CI-09-011    | revalueAccountingPositions | POST /v1/accounting-revaluations                           |
| CI-09-012    | getAccountingEntry         | GET /v1/accounting-entries/{entryId}                       |
| CI-09-012    | getAccountingAction        | GET /v1/accounting-actions/{actionId}                      |

### **6.2. Registración**

Registrar recibe contexto, hecho o integración de forma excluyente, regla y tratamiento de período cuando corresponde.

La confirmación incluye asiento completo, saldos, secuencia, idempotencia y outbox en una transacción local.

La misma consecuencia no se contabiliza otra vez por cambiar actuación o vía de recepción.

Un importe cero no origina partidas ficticias.

### **6.3. Ajustes manuales**

Crear propuesta recibe fecha económica, período, partidas, fundamento y referencias originales.

Devuelve propuesta, versión, huella, validaciones e impedimentos.

Confirmar recibe contexto, versión de propuesta, huella y aprobación.

Preparador y aprobador son distintos. Cambiar partidas exige nueva revisión y aprobación.

### **6.4. Reversión y reclasificación**

Corregir recibe contexto, tipo, alcance, partidas originales, importes, motivo y evidencia. Agrega aprobación cuando es
manual.

Los tipos son REVERSE y RECLASSIFY.

El alcance es TOTAL o PARTIAL. La parcial identifica importes por partida y conserva equilibrio.

Devuelve nuevos asientos, alcance corregido y remanente.

No borra el original ni corrige más que el alcance pendiente, considerando correcciones comprometidas.

Una reclasificación no ejecuta movimientos bancarios.

### **6.5. Reexpresión**

Recibe corte, posiciones, clasificación monetaria, política y cotizaciones.

Devuelve valores originales, presentación anterior y resultante, diferencias, asientos y faltantes.

Un pasivo USD 100 conserva USD 100 aunque cambie su equivalente ARS.

No ejecuta compra o venta de moneda.

### **6.6. Lectura autoritativa**

Registración ofrece:

| **operationId**     | **Método y ruta**               |
|---------------------|---------------------------------|
| queryLedgerBalances | POST /v1/ledger-balance-queries |
| queryLedgerLines    | POST /v1/ledger-line-queries    |

Reciben finalidad, cuentas, dimensiones, monedas y corte. Las partidas admiten paginación.

Devuelven saldos o partidas, secuencia, totales y metadatos de completitud.

Son consultas. No conceden acceso SQL a la base de Registración.

## **7. Servicio Cierres contables**

### **7.1. AccountingPeriod**

Contiene:

- periodId y version.

- periodType: DAY, MONTH o YEAR.

- parentPeriodId, para período dependiente.

- startDate.

- endDateExclusive.

- status: OPEN, CLOSING, CLOSED o REOPENING.

- controlVersion.

- controlApplicationReference, cuando existe aplicación.

Los períodos del mismo nivel no se superponen.

El ejercicio comprende desde el 1 de enero hasta el 1 de enero siguiente, con final excluido.

### **7.2. Interfaces**

| **Contrato** | **operationId**        | **Método y ruta**                                      |
|--------------|------------------------|--------------------------------------------------------|
| CI-09-013    | createAccountingPeriod | POST /v1/accounting-periods                            |
| CI-09-013    | actOnAccountingPeriod  | POST /v1/accounting-periods/{periodId}/control-actions |
| CI-09-014    | checkAccountingPeriod  | POST /v1/accounting-period-checks                      |
| CI-09-022    | getAccountingPeriod    | GET /v1/accounting-periods/{periodId}                  |
| CI-09-022    | getAccountingClose     | GET /v1/accounting-closes/{closeId}                    |

Crear recibe contexto, tipo, fechas y período superior cuando corresponde.

Las acciones de control son OPEN, START_CLOSING y APPLY_AUTHORIZED_TREATMENT.

Una reapertura exige tratamiento autorizado y alcance explícito.

### **7.3. Barrera en Registración**

| **operationId**                   | **Método y ruta**                                          |
|-----------------------------------|------------------------------------------------------------|
| applyLedgerPeriodControl          | POST /v1/ledger-period-control-applications                |
| getLedgerPeriodControlApplication | GET /v1/ledger-period-control-applications/{applicationId} |

Proveedor: Registración contable.  
Consumidor: Cierres contables.

Recibe contexto, aplicación, período, versión de control, modalidad y regularizaciones autorizadas.

Las modalidades son:

- ORDINARY.

- IDENTIFIED_REGULARIZATIONS.

- SEALED.

Registración aplica el control bajo el mismo mecanismo local que confirma asientos. Comprueba también los controles
superiores de mes y ejercicio afectados.

Devuelve versión aplicada, modalidad, fecha y corte.

Una versión atrasada no reabre el período.

Cierres no informa una transición completada sin comprobar su aplicación.

### **7.4. Comprobación informativa**

Recibe fecha económica, período, acto, finalidad y regularización cuando corresponde.

Devuelve ADMITTED, PREVENTED o PENDING, estado, versión de control y tratamiento requerido.

La consulta no sustituye la barrera local al confirmar un asiento.

## **8. Integridad y conciliación**

### **8.1. Interfaces**

| **Contrato** | **operationId**                       | **Método y ruta**                                          |
|--------------|---------------------------------------|------------------------------------------------------------|
| CI-09-015    | recordAccountingIntegrityConfirmation | POST /v1/accounting-integrity-confirmations                |
| CI-09-016    | recordAccountingAuxiliarySnapshot     | POST /v1/accounting-auxiliary-snapshots                    |
| CI-09-017    | reconcileAccountingAuxiliaries        | POST /v1/accounting-reconciliations                        |
| CI-09-018    | resolveAccountingDifference           | POST /v1/accounting-differences/{differenceId}/resolutions |

### **8.2. Integridad de fuentes**

La confirmación recibe identidad, emisor, ámbito, fecha económica, corte, cobertura, cantidad de hechos, huella y
pendientes conocidos.

La cobertura utiliza conjunto explícito, rangos de secuencia o declaración EMPTY.

La ausencia declarada no se confunde con una fuente que no respondió.

Devuelve declaración recibida, cobertura comprobada, faltantes y estado:

RECEIVED, VERIFIED, INCOMPLETE o CONTRADICTORY.

Los hechos declarados todavía no recibidos mantienen el control pendiente.

### **8.3. Auxiliares**

El auxiliar recibe identidad, fuente, tipo, ámbito, corte, versión, monedas, registros, detalle y pendientes.

Cada registro contiene concepto, dimensiones y modalidad:

- MONETARY: importe con signo y moneda.

- UNITS: cantidad, instrumento y unidad.

Las correspondencias determinan qué cifras monetarias o registros de orden son comparables.

Las revisiones crean otra versión y preservan la anterior.

### **8.4. Conciliación y diferencias**

Conciliar recibe corte, ámbito, auxiliares, saldos y regla de correspondencia.

Devuelve identidad, conceptos comparados, coincidencias, diferencias y controles pendientes.

Disponibilidad 70 y saldo registrado 100 no se comparan como el mismo pasivo.

Resolver diferencia recibe diagnóstico, propietario, tratamiento, actuaciones propuestas, fecha objetivo, evidencia y
aprobación exigible.

Los diagnósticos son:

TEMPORAL, MISSING_FACT, DUPLICATE, AMOUNT_MISMATCH, MISCLASSIFICATION, INCOMPLETE_SOURCE y UNDETERMINED.

Los tratamientos son CORRECT_SOURCE, INCORPORATE_FACT, CORRECT_ENTRY, AUTHORIZED_TIMING_DIFFERENCE y
CONTINUE_INVESTIGATION.

La diferencia solo queda resuelta tras comprobar el tratamiento. Aprobar seguimiento no transforma una causa desconocida
en gasto válido.

## **9. Cierres y hechos tardíos**

### **9.1. AccountingClose**

Contiene:

- closeId y version.

- Período.

- Fecha económica, para diario.

- Corte.

- Referencias de integridad, auxiliares y conciliaciones.

- Resultados de controles.

- Pendientes admitidos.

- Asientos de ajuste.

- Huella de controles.

- Estado.

- Aprobación y huella de saldos cuando están confirmadas.

Los estados son STARTED, BARRIER_PENDING, CHECKING, REGULARIZING, APPROVAL_PENDING, SEALING, CLOSED y REQUIRES_REVIEW.

Cada control contiene código, resultado PASSED, FAILED o PENDING, evidencia y observaciones.

Cada pendiente admitido identifica causa, alcance, responsable, tratamiento y autorización.

### **9.2. Interfaces**

| **Contrato**          | **operationId**                      | **Método y ruta**                                  |
|-----------------------|--------------------------------------|----------------------------------------------------|
| CI-09-019             | executeDailyAccountingClose          | POST /v1/daily-accounting-closes                   |
| CI-09-020             | executePeriodAccountingClose         | POST /v1/period-accounting-closes                  |
| CI-09-019 y CI-09-020 | confirmAccountingClose               | POST /v1/accounting-closes/{closeId}/confirmations |
| CI-09-021             | authorizeLateAccountingFactTreatment | POST /v1/late-accounting-fact-treatments           |

### **9.3. Ejecución y confirmación**

El cierre diario recibe fecha económica y período.

El periódico recibe período, cierres diarios y controles requeridos.

El proceso:

1.  Aplica barrera.

2.  Fija corte.

3.  Comprueba integridad.

4.  Completa integración y conciliaciones.

5.  Registra regularizaciones.

6.  Repite controles.

7.  Obtiene aprobación sobre versión y huella.

8.  Sella Registración.

9.  Confirma cierre.

Confirmar recibe contexto, versión de cierre, huella de controles y aprobación.

Un cambio que altera controles aprobados exige nueva aprobación.

No concluye con desequilibrio, duplicaciones confirmadas ni hechos conocidos sin tratamiento.

El ejercicio registra una transferencia de resultados identificada. La apertura siguiente conserva continuidad sin
duplicar patrimonio.

### **9.4. Hechos tardíos**

Recibe hecho, fecha económica, recepción real, períodos afectados, impacto, informes emitidos, tratamiento propuesto y
aprobación.

Las modalidades son:

- REOPEN_DAY: día cerrado con mes abierto.

- REOPEN_PERIOD: mes cerrado con ejercicio todavía no emitido definitivamente, cuando cambian cifras.

- PRIOR_PERIOD_ADJUSTMENT: ejercicio emitido definitivamente.

Devuelve tratamiento autorizado, alcance, períodos y controles a repetir.

No altera la recepción real ni introduce asientos ordinarios en un período sellado.

Una reapertura conserva cierres anteriores e identifica cuál los sustituye.

## **10. Servicio Información contable**

### **10.1. AccountingReportStructure**

Contiene:

- Identidad y versión.

- Tipo de informe.

- Moneda de presentación.

- Grupos.

- Correspondencias de cuentas.

- Reglas de dimensiones.

- Fórmulas.

- Comparativos.

- Validaciones.

- Vigencia y estado.

Cada grupo tiene código, nombre, posición y superior cuando corresponde.

Las fórmulas utilizan operaciones tipadas SUM, SUBTRACT o NEGATE sobre cifras o grupos identificados. No admiten scripts
ni ciclos.

Las correspondencias controlan omisiones y duplicaciones.

Los registros de orden se presentan separadamente.

### **10.2. Interfaces**

| **Contrato** | **operationId**                 | **Método y ruta**                                           |
|--------------|---------------------------------|-------------------------------------------------------------|
| CI-09-023    | createAccountingReportStructure | POST /v1/accounting-report-structures                       |
| CI-09-023    | actOnAccountingReportStructure  | POST /v1/accounting-report-structures/{structureId}/actions |
| CI-09-024    | getAccountingReportStructure    | GET /v1/accounting-report-structures/{structureId}          |
| CI-09-025    | issueAccountingReport           | POST /v1/accounting-reports                                 |
| CI-09-026    | getAccountingBalances           | GET /v1/accounting-balances                                 |
| CI-09-026    | getAccountingFigureProvenance   | GET /v1/accounting-report-figures/{figureId}/provenance     |
| CI-09-027    | getAccountingReport             | GET /v1/accounting-reports/{reportId}                       |
| CI-09-028    | restateAccountingReport         | POST /v1/accounting-reports/{reportId}/rectifications       |

Las acciones de estructura son SUBMIT, APPROVE, ENABLE y WITHDRAW.

Cambiar presentación no modifica asientos.

### **10.3. AccountingReport**

Contiene:

- reportId y version.

- Tipo de informe.

- Período.

- Corte.

- Estructura versionada.

- Versiones del plan utilizadas.

- Moneda de presentación.

- Condición PROVISIONAL o DEFINITIVE.

- Cifras.

- Controles.

- Comparativos.

- Estado de elaboración y emisión.

- Cierre, aprobación y documento, cuando corresponden.

- Informe anterior, si rectifica.

Cada cifra tiene identidad, código, importe con signo, moneda y referencia de procedencia.

Los estados son COMPOSING, VALIDATING, READY_TO_ISSUE, ISSUED y REQUIRES_REVIEW.

### **10.4. Emisión y procedencia**

Emitir recibe contexto, tipo, período, corte, estructura, condición solicitada, comparativos y aprobación cuando
corresponde.

Un período abierto permite únicamente informe provisional.

Una proyección propia debe alcanzar el corte y comprobar totales con Registración.

La respuesta distingue elaboración, emisión y documento pendiente. Un fallo documental posterior no permite emitir
nuevamente las mismas cifras como otra actuación independiente.

La procedencia permite recorrer estructura, cuentas, partidas, asientos, hechos y reglas.

Consultar un informe histórico devuelve sus cifras conservadas. No las recalcula con datos actuales.

## **11. Emisión definitiva y rectificación**

### **11.1. Preparación frente a reapertura**

| **Proveedor** | **operationId**                            | **Método y ruta**                                                            |
|---------------|--------------------------------------------|------------------------------------------------------------------------------|
| Cierres       | prepareDefinitiveReportEmission            | POST /v1/definitive-report-emission-preparations                             |
| Cierres       | getDefinitiveReportEmissionPreparation     | GET /v1/definitive-report-emission-preparations/{preparationId}              |
| Cierres       | resolveDefinitiveReportEmissionPreparation | POST /v1/definitive-report-emission-preparations/{preparationId}/resolutions |
| Información   | getAccountingReportEmissionAttempt         | GET /v1/accounting-report-emission-attempts/{attemptId}                      |
| Información   | requestAccountingReportEmissionAbort       | POST /v1/accounting-report-emission-attempts/{attemptId}/abort-requests      |

Información registra el intento antes de solicitar preparación.

La solicitud vincula intento, consulta de decisión, cierre exacto, corte, estructura, cifras, huella y límite de
decisión.

Cierres ordena preparación y reapertura sobre su control local.

Información confirma decisión y emisión en su transacción local. Después comunica el resultado a Cierres.

La decisión es PENDING, COMMITTED o ABORTED.

El aborto devuelve ABORTED, ALREADY_COMMITTED o PENDING.

Una reapertura incompatible solicita resolver el intento. No elimina silenciosamente su preparación.

Una respuesta perdida se consulta. No se libera por timeout suponiendo que no hubo emisión.

### **11.2. Rectificación**

Recibe contexto, informe original, fundamento, aprobación y modalidad:

- ACCOUNTING_DATA_CORRECTION: asientos corregidos.

- PRESENTATION_CHANGE: cambio aprobado de estructura o correspondencia.

Devuelve nueva emisión, diferencias, referencia anterior y destinatarios.

La versión anterior permanece inmutable.

Información regulatoria aplica su procedimiento independiente sobre las declaraciones afectadas.

## **12. Eventos**

### **12.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                         | **Tema**                         | **Clave**        |
|--------------|----------------------------------|----------------------------------|------------------|
| CI-09-029    | AccountingPlanOrRuleChanged      | banking.accounting-entry.events  | Cuenta o regla.  |
| CI-09-030    | AccountingFactOrEntryProcessed   | banking.accounting-entry.events  | Hecho o asiento. |
| CI-09-031    | AccountingPeriodOrCloseChanged   | banking.accounting-close.events  | periodId         |
| CI-09-032    | AccountingDifferenceChanged      | banking.accounting-close.events  | differenceId     |
| CI-09-033    | AccountingReportIssuedOrRestated | banking.accounting-report.events | reportId         |

La versión inicial de esquema es 1.

### **12.2. CI-09-029 — AccountingPlanOrRuleChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: APPROVED, ENABLED, NEW_VERSION o WITHDRAWN.

- validity: vigencia.

- approvalReference: aprobación del cambio.

- pendingComponents: pendientes comunes.

Con aggregateType igual a LEDGER_ACCOUNT, agrega código, clase, rol y estado.

Con aggregateType igual a ACCOUNTING_RULE, agrega tipo de hecho, versión de esquema, consecuencia y tratamiento de
hechos pendientes.

Las variantes son excluyentes.

### **12.3. CI-09-030 — AccountingFactOrEntryProcessed**

Payload obligatorio:

- objectReference: referencia versionada.

- economicIdentity: EconomicIdentity.

- changeType: FACT_RECEIVED, INTEGRATION_UPDATED, ENTRY_POSTED o ENTRY_CORRECTED.

- factReferences: ObjectReference\[\].

- entryReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Con aggregateType igual a ACCOUNTING_FACT, agrega estado de tratamiento e integración cuando existe.

Con aggregateType igual a ACCOUNTING_ENTRY, agrega secuencia, período, fecha económica, control aplicado, tipo de
asiento y totales.

Una corrección agrega referencias originales.

El evento no ejecuta nuevamente el hecho bancario.

### **12.4. CI-09-031 — AccountingPeriodOrCloseChanged**

aggregateType es ACCOUNTING_PERIOD.

Payload obligatorio:

- periodId: UUID.

- periodVersion: Version.

- changeType: OPENED, CLOSING_STARTED, CONTROL_APPLIED, CLOSED o REOPENED.

- status: estado del período.

- controlVersion: Version.

- pendingComponents: pendientes comunes.

Campos condicionales:

- Aplicación de control y modalidad, cuando se comprueba barrera.

- Cierre y corte, cuando se confirma cierre.

- Tratamiento autorizado y cierre sustituido, cuando se reabre.

Un evento atrasado de apertura no reemplaza un control posterior.

### **12.5. CI-09-032 — AccountingDifferenceChanged**

aggregateType es ACCOUNTING_DIFFERENCE.

Payload obligatorio:

- differenceId: UUID.

- differenceVersion: Version.

- reconciliationReference: ObjectReference.

- changeType: DETECTED, DIAGNOSED, TREATMENT_AUTHORIZED o RESOLVED.

- diagnosis: diagnóstico definido.

- ownerService: responsable de tratamiento.

- sourceReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Una diferencia monetaria agrega importe y moneda. Una diferencia de unidades agrega cantidad, instrumento y unidad.

RESOLVED exige evidencia de comprobación posterior. Una autorización sin ejecución no satisface ese resultado.

### **12.6. CI-09-033 — AccountingReportIssuedOrRestated**

aggregateType es ACCOUNTING_REPORT.

Payload obligatorio:

- reportId: UUID.

- reportVersion: Version.

- changeType: ISSUED o RESTATED.

- reportType: Code.

- periodReference: VersionedReference.

- cutReference: AccountingCut.

- structureReference: VersionedReference.

- condition: PROVISIONAL o DEFINITIVE.

- figuresHash: Hash.

- issuedAt: Instant.

- pendingComponents: pendientes comunes.

Incluye documento cuando está generado.

La emisión definitiva agrega cierre y aprobación.

La rectificación agrega informe anterior y fundamento.

### **12.7. Consumidores y recuperación**

| **Información**    | **Consumidores autorizados**                                                           |
|--------------------|----------------------------------------------------------------------------------------|
| Plan y reglas      | Cierres, Información contable y Gestión administrativa.                                |
| Hechos y asientos  | Emisor, Cierres, Información y Gestión administrativa.                                 |
| Períodos y cierres | Registración, Información, Información regulatoria y Gestión administrativa.           |
| Diferencias        | Propietario del auxiliar, Registración e Información.                                  |
| Informes           | Cierres, Información regulatoria, Documentos, Comunicaciones y Gestión administrativa. |

Los consumidores comparan versiones y recuperan estado autoritativo ante discontinuidades relevantes.

El orden del tema no sustituye la barrera local de Registración.

Los eventos no crean nuevos movimientos bancarios ni duplican consecuencias económicas.

## **13. Seguridad y recuperación**

Se separan permisos para plan, reglas, hechos propios, registración, ajustes manuales, correcciones, períodos,
integridad, auxiliares, conciliación, cierre, reapertura y emisión.

Solo Cierres aplica controles de período mediante la interfaz autorizada.

Un emisor acredita únicamente hechos y auxiliares de su competencia.

La recuperación conserva actuación, identidad económica, versión de control y corte.

Una confirmación incierta no se sustituye por otra con diferente identidad económica.

Las consultas históricas y recuperaciones idempotentes mantienen autorización.

Los servicios utilizan outbox e inbox. Los efectos locales se confirman antes de avanzar el procesamiento de eventos.

## **14. Errores particulares**

| **Código**                        | **HTTP** | **Significado**                       |
|-----------------------------------|----------|---------------------------------------|
| CONTENT_CONFLICT                  | 409      | Identidad con contenido diferente.    |
| VERSION_CONFLICT                  | 409      | Versión incompatible.                 |
| ACCOUNT_NOT_POSTABLE              | 422      | Cuenta agrupadora o no habilitada.    |
| ACCOUNT_DIMENSION_REQUIRED        | 422      | Falta dimensión.                      |
| ACCOUNT_CURRENCY_NOT_ALLOWED      | 422      | Moneda incompatible.                  |
| ACCOUNTING_RULE_NOT_FOUND         | 422      | Falta regla aplicable.                |
| ECONOMIC_FACT_INCONSISTENT        | 409      | Antecedentes contradictorios.         |
| ACCOUNTING_INTEGRATION_INCOMPLETE | 422      | Falta fuente exigible para registrar. |
| ENTRY_UNBALANCED                  | 422      | Débitos y créditos no equilibrados.   |
| ACCOUNTING_PERIOD_NOT_ADMITTED    | 409      | Barrera impide registrar.             |
| PERIOD_CONTROL_VERSION_CONFLICT   | 409      | Control incompatible.                 |
| REVERSAL_SCOPE_EXCEEDED           | 422      | Supera remanente corregible.          |
| ACCOUNTING_RATE_MISSING           | 422      | Falta cotización necesaria.           |
| SOURCE_INTEGRITY_PENDING          | 409      | Integridad no comprobada.             |
| AUXILIARY_CUT_MISMATCH            | 422      | Cortes no comparables.                |
| CLOSE_CONTROL_FAILED              | 422      | Control obligatorio incumplido.       |
| REPORT_MAPPING_INCOMPLETE         | 422      | Correspondencia incompleta.           |
| DEFINITIVE_REPORT_REQUIRES_CLOSE  | 422      | Falta cierre requerido.               |
| REPORT_EMISSION_CONFLICT          | 409      | Emisión o reapertura incompatible.    |
| DEPENDENCY_UNAVAILABLE            | 503      | Dependencia necesaria indisponible.   |

La incorporación durable de un hecho puede responder 202 con integración pendiente.

Eso no equivale a asiento confirmado ni a rechazo de la operación bancaria.

Una repetición coherente recupera el resultado original.

## **15. Escenarios de aceptación**

| **Escenario**                                            | **Resultado exigido**                                 |
|----------------------------------------------------------|-------------------------------------------------------|
| Préstamos informa capital 500 y Fondos acredita 500      | Integra antecedentes sin duplicar la consecuencia.    |
| Desembolso bruto 500 y neto 490 sin retención acreditada | Mantiene inconsistencia; no inventa una comisión.     |
| Interés devengado 10 y posterior cobro                   | Cancela el derecho sin reconocer otros 10 de ingreso. |
| Previsión requerida pasa de 20 a 25                      | Registra variación 5.                                 |
| Cambio material de ajuste aprobado                       | Exige nueva aprobación.                               |
| Asiento y barrera compiten                               | El control local determina admisión y corte.          |
| Declaración de integridad con hechos no recibidos        | No informa cobertura completa.                        |
| Fuente sin respuesta                                     | No la considera vacía.                                |
| Auxiliar disponible 70 y saldo registrado 100            | No los compara como igual concepto.                   |
| Hecho tardío                                             | Conserva recepción real y tratamiento autorizado.     |
| Reapertura compite con emisión definitiva                | Resuelve el intento sin liberar por timeout.          |
| Consulta de informe anterior a rectificación             | Devuelve cifras originales y referencia posterior.    |
| Ajuste contable                                          | No modifica dinero, deuda ni tenencias operativas.    |
| Respuesta perdida                                        | Recupera el mismo asiento, cierre o informe.          |
| Evento duplicado o antiguo                               | No duplica consecuencias ni reabre un período.        |

Las pruebas de unicidad, bloqueo, equilibrio y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato
comprueban entradas, resultados, errores y eventos contra sus esquemas.
