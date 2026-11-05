# Diseño técnico — Módulo 9. Contabilidad

## 1. Propósito y alcance

El módulo representa contablemente el patrimonio, las obligaciones y los resultados del banco.

Conserva la relación entre:

- Hechos económicos y antecedentes.

- Reglas de contabilización.

- Asientos y partidas.

- Saldos y conciliaciones.

- Períodos y cierres.

- Informes y rectificaciones.

Comprende la contabilidad patrimonial y los registros de orden.

No sustituye los saldos disponibles de clientes, la deuda de productos, las tenencias ni la liquidez operativa.

Un impedimento contable no revierte automáticamente la operación bancaria que lo originó.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Registración contable | accounting-entry-service | accounting_entry_db | Plan, reglas, hechos, integraciones, asientos y saldos registrados. |
| Cierres contables | accounting-close-service | accounting_close_db | Períodos, integridad, auxiliares, conciliaciones, cierres y reaperturas. |
| Información contable | accounting-report-service | accounting_report_db | Estructuras, informes, comparativos y justificación de cifras. |

Cada servicio administra PostgreSQL, usuario y migraciones propios.

No existen consultas SQL, relaciones JPA ni claves foráneas entre sus bases.

### 2.1. Autoridades relacionadas

| Información o decisión | Responsable |
| --- | --- |
| Hecho económico e importe operativo | Servicio propietario de la operación. |
| Cálculo de intereses, cargos y deuda | Producto correspondiente. |
| Políticas de previsión | Riesgos. |
| Importe de previsión de cartera | Responsable de la cartera. |
| Cotización de finalidad contable | Cambio de moneda. |
| Facultades de aprobación | Institución. |
| Tareas humanas | Gestión administrativa. |
| Documentos justificativos | Documentos y comprobantes. |
| Declaraciones regulatorias | Información regulatoria. |

Contabilidad determina la representación de un hecho. No modifica sus condiciones económicas para conseguir un asiento
equilibrado.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador interno | UUID | uuid | Cadena UUID. |
| Versión o secuencia | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha económica | LocalDate | date | YYYY-MM-DD. |
| Importe original recibido | BigDecimal | numeric(24,8) | Cadena decimal. |
| Importe contable | BigDecimal | numeric(30,2) | Cadena decimal. |
| Cotización | BigDecimal | numeric(30,12) | Cadena decimal. |
| Cantidad de custodia | BigDecimal | numeric(30,12) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Denominación | String | varchar(200) | Cadena. |
| Fundamento | String | varchar(4000) | Cadena. |
| Resumen de contenido | String | char(64) | SHA-256 hexadecimal. |

Los importes recibidos se conservan con su precisión de origen.

Los cálculos de conversión mantienen al menos doce decimales intermedios. Las partidas contables se expresan en centavos
y utilizan HALF_UP.

No se emplea punto flotante binario.

### 3.2. Fechas

Cada hecho y asiento distingue:

- Fecha económica.

- Recepción real.

- Confirmación del registro.

- Fecha de contabilización.

- Período de imputación.

El día institucional corresponde a America/Argentina/Buenos_Aires, desde las 00:00 hasta el inicio del día siguiente.

El corte de compensación de las 18:00 no reemplaza el corte contable diario.

### 3.3. Actuaciones

Los comandos incluyen actuación, operación, finalidad, causa, objeto, versión esperada y evidencias.

Los ajustes incorporan aprobación sobre el contenido exacto.

El actor y el servicio emisor proceden de la autenticación.

Las respuestas distinguen recepción, integración, propuesta, confirmación y pendientes.

Los estados técnicos son PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

## 4. Plan de cuentas

### 4.1. Cuenta contable

| Campo | Regla |
| --- | --- |
| ledgerAccountId | Identidad estable. |
| accountCode | Código único, no reutilizable con otra naturaleza. |
| name | Denominación. |
| parentAccountId | Cuenta superior, cuando corresponda. |
| accountClass | Activo, pasivo, patrimonio, ingreso, gasto o cuenta de orden. |
| normalBalance | Deudor o acreedor. |
| postingAllowed | Distingue agrupación de movimiento. |
| accountingBook | Patrimonial u orden. |
| monetaryClassification | Monetaria o no monetaria. |
| allowedCurrencies | Monedas admitidas. |
| requiredDimensions | Dimensiones obligatorias. |
| validFrom / validUntil | Vigencia. |
| status | Preparación, pendiente de aprobación, habilitada o retirada. |
| version | Versión de definición. |

La jerarquía no admite ciclos.

Las cuentas de agrupación no reciben partidas.

Una cuenta con historia no cambia de naturaleza para reutilizar su código.

El retiro impide nuevos usos ordinarios. Las consultas y correcciones autorizadas conservan acceso histórico.

### 4.2. Dimensiones

Las dimensiones permiten identificar producto, sucursal, moneda, contraparte y origen cuando la regla lo requiera.

Se definen mediante códigos y tipos aprobados. No se admiten claves arbitrarias que alteren el significado contable.

Las referencias a clientes u operaciones no obligan a crear una cuenta contable distinta por persona.

### 4.3. Aprobación

La preparación y la aprobación corresponden a personas diferentes.

La aprobación identifica la versión y el resumen del contenido.

Un cambio posterior exige otra aprobación.

Una cuenta con saldo no se retira sin tratamiento de transición.

## 5. Reglas de contabilización

### 5.1. Definición

Cada versión contiene:

- Tipo y versión del hecho.

- Consecuencia económica.

- Fuente responsable.

- Antecedentes requeridos.

- Claves de correlación.

- Campos que deben coincidir.

- Cuentas y dimensiones.

- Partidas.

- Fórmulas admitidas.

- Fechas.

- Monedas y conversión.

- Excepciones.

- Correcciones.

- Ejemplos de validación.

- Vigencia y aprobación.

### 5.2. Expresiones

Las reglas utilizan operaciones tipadas: selección de concepto, suma, resta, multiplicación por cotización y redondeo
definido.

No ejecutan SQL, scripts ni código recibido en la configuración.

Las referencias a datos se validan contra el esquema del hecho.

Un campo ausente no se convierte en cero.

Una regla desconocida no se sustituye por otra de nombre parecido.

### 5.3. Versiones

La regla aplicada queda fijada en el tratamiento del hecho.

Una versión nueva no recontabiliza automáticamente el pasado.

Toda transición define qué hechos utiliza y cómo trata pendientes anteriores.

Las reglas separan principal, rendimiento, comisión, tributo, diferencia de cambio y redondeo.

Una cuenta transitoria requiere una situación económica real, contrapartida, responsable y procedimiento de resolución.
No se utiliza para ocultar información faltante.

## 6. Hechos e integración de antecedentes

### 6.1. Hecho recibido

| Campo | Contenido |
| --- | --- |
| economicFactId | Identidad económica del hecho. |
| sourceService | Propietario del hecho. |
| sourceReference | Referencia consultable en origen. |
| operationId | Operación relacionada. |
| occurrenceReference | Ocurrencia concreta. |
| factType / schemaVersion | Esquema. |
| economicDate | Fecha del hecho. |
| occurredAt | Momento original. |
| receivedAt | Recepción real. |
| concepts | Importes tipados por concepto y moneda. |
| relatedEffects | Movimientos y consecuencias vinculados. |
| evidenceReferences | Antecedentes verificables. |
| contentHash | Resumen canónico. |
| correctionOf | Original corregido, cuando corresponda. |

Se conserva el contenido original y su normalización.

La recepción por HTTP y Kafka utiliza la misma identidad económica.

### 6.2. Estados

- RECEIVED.

- VALIDATION_PENDING.

- AWAITING_ANTECEDENTS.

- READY_TO_POST.

- POSTED.

- PERIOD_TREATMENT_PENDING.

- INCONSISTENT.

- RESOLUTION_REQUIRED.

Un hecho recibido no se presenta como contabilizado.

### 6.3. Operaciones compartidas

La integración tiene una clave formada por operación, ocurrencia y consecuencia económica.

La regla establece qué servicio aporta cada antecedente.

Por ejemplo:

- Préstamos informa capital bruto y retenciones.

- Fondos informa el crédito neto.

- La integración comprueba su correspondencia.

- Se produce la consecuencia contable definida una sola vez.

Si se informa capital 500 y crédito 490, deben existir retenciones identificadas que expliquen 10. Contabilidad no
inventa una comisión.

### 6.4. Repetición y conflictos

Mismo hecho y contenido recuperan el tratamiento original.

Misma identidad con contenido diferente produce inconsistencia.

La identidad de la consecuencia contable impide contabilizarla otra vez aunque llegue en varios eventos.

Una operación puede producir varias consecuencias legítimas: constitución, devengamiento, cobro y corrección. Cada una
conserva su identidad.

## 7. Asientos y saldos

### 7.1. Asiento

El asiento contiene:

- Identidad y referencia.

- Libro.

- Consecuencia económica.

- Hecho o integración.

- Regla y versión.

- Fechas.

- Período.

- Autorización de admisión.

- Secuencia de confirmación.

- Partidas.

- Aprobación cuando es manual.

- Original y correcciones relacionadas.

### 7.2. Partida

| Campo | Regla |
| --- | --- |
| entryLineId | Identidad. |
| lineNumber | Orden dentro del asiento. |
| ledgerAccountId | Cuenta de movimiento. |
| accountDefinitionVersion | Versión utilizada. |
| direction | Débito o crédito. |
| originalCurrency | Moneda de origen. |
| originalAmount | Importe original. |
| functionalAmount | Equivalente contable en ARS. |
| exchangeRateReference | Cotización cuando corresponde. |
| dimensions | Valores tipados exigidos. |
| sourceConceptReference | Concepto que fundamenta la partida. |

Las partidas de reexpresión identifican que ajustan exclusivamente el equivalente en ARS: no modifican las unidades de
moneda original.

No se generan partidas sin efecto contable.

### 7.3. Equilibrio

El libro patrimonial debe mantener igualdad entre débitos y créditos en moneda de presentación.

Los registros de orden se equilibran dentro de su propio libro. No compensan un desequilibrio patrimonial.

Las reglas multimoneda definen contrapartidas y posiciones de cambio. No exigen igualdad numérica entre importes de
monedas distintas.

Las diferencias legítimas de conversión se identifican y registran mediante su regla. Un faltante económico no se
clasifica como redondeo.

### 7.4. Confirmación atómica

Registración confirma en una transacción:

- Identidad de la consecuencia.

- Regla y cuentas.

- Admisión del período.

- Equilibrio.

- Asiento completo.

- Partidas.

- Saldos.

- Historial.

- Resultado e idempotencia.

- Outbox.

No existen asientos confirmados a medias.

### 7.5. Saldos y secuencias

Los saldos se mantienen por cuenta, moneda original y dimensiones.

La actualización utiliza bloqueos en orden estable para reducir interbloqueos.

La secuencia de confirmación se asigna mediante un control transaccional del libro, de modo que un corte no incluya una
secuencia posterior y omita otra todavía sin confirmar.

Los saldos pueden reconstruirse desde las partidas.

## 8. Ajustes, reversiones y multimoneda

### 8.1. Ajuste manual

La propuesta conserva partidas, fundamento, documentos y resumen de contenido.

El preparador no aprueba.

Al confirmar se comprueban nuevamente período, cuentas y alcance de la aprobación.

Un cambio de partidas invalida la aprobación anterior.

Un asiento manual no autoriza a debitar una cuenta bancaria ni a modificar deuda operativa.

### 8.2. Reversión y reclasificación

Las correcciones agregan registros vinculados.

Se controla el alcance todavía reversible bajo bloqueo del original.

Sobre un efecto de 100 con 40 revertidos quedan como máximo 60 por revertir en ese alcance.

Una reclasificación no genera dinero ni un ingreso ficticio.

La reversión utiliza importes registrados y su tratamiento de período autorizado.

### 8.3. Conversión contable

ARS es la moneda de presentación.

Cada partida conserva moneda e importe originales y el equivalente utilizado.

La cotización identifica finalidad contable, fecha, fuente y versión. No se sustituye por una oferta comercial.

La conversión contable no ejecuta cambio de moneda.

### 8.4. Reexpresión

Los elementos monetarios en moneda extranjera se reexpresan al cierre.

Se conserva valor anterior, cotización nueva, equivalente requerido y diferencia.

Un pasivo USD 100 puede pasar de ARS 100.000 a ARS 110.000 sin cambiar los USD 100 adeudados.

Las partidas no monetarias utilizan el criterio asignado a su clase.

La falta de cotización impide concluir la valuación definitiva.

## 9. Devengamientos, previsiones y custodia

### 9.1. Devengamientos

El producto informa si el importe es variación o acumulado.

Registración conserva lo reconocido por concepto y período.

Si el acumulado pasa de 20 a 25, se registra diferencia 5.

Cobrar un interés devengado cancela el derecho: no reconoce otro ingreso.

Pagar interés de depósito cancela la obligación: no duplica gasto.

### 9.2. Descuento de cheques

La regla conserva:

- Nominal de financiación.

- Anticipo neto.

- Retenciones.

- Interés retenido pendiente de devengar.

- Devengamientos posteriores.

- Aplicación del cobro.

No suma nominal, neto y retenciones como desembolsos independientes.

El cobro del cheque cancela la financiación correspondiente. No origina otro depósito libre para el cliente.

### 9.3. Previsiones

El producto determina importes con la metodología de Riesgos.

Contabilidad registra el requerimiento y su variación respecto de lo reconocido.

Previsionar o dar de baja contablemente no extingue deuda.

### 9.4. Registros de orden

Se distinguen compromisos no utilizados, garantías y activos de clientes en custodia.

Los valores de clientes no se incorporan como inversiones propias.

Una garantía no constituye efectivo disponible.

Una adjudicación puede generar un hecho patrimonial distinto, con evidencia y regla específicas.

## 10. Períodos y admisión de registraciones

### 10.1. Estructura

Se administra:

- Ejercicio calendario.

- Períodos mensuales.

- Fechas diarias.

- Cortes.

- Versiones de control.

- Regularizaciones autorizadas.

- Cierres y reaperturas.

Los intervalos de un mismo nivel no se superponen.

Los estados son:

- OPEN.

- CLOSING.

- CLOSED.

- REOPENING.

Un período en cierre admite únicamente regularizaciones identificadas.

### 10.2. Barrera de registración

Cierres es propietario de la decisión sobre el período.

Registración mantiene su aplicación local versionada, utilizada dentro de la misma transacción que confirma los
asientos.

El cambio sigue este protocolo:

- Cierres conserva la transición y su versión.

- Solicita su aplicación a Registración.

- Registración bloquea el control local del período.

- Aplica la nueva condición.

- Devuelve confirmación y secuencia de corte.

- Cierres consolida la transición.

Los asientos bloquean ese mismo control antes de confirmar.

Así se ordenan la aplicación del cierre y las registraciones concurrentes.

Una consulta anterior que indicaba período abierto no permite ignorar la barrera aplicada después.

### 10.3. Recuperación

Si se pierde la confirmación, Cierres consulta la aplicación original.

Mientras no pueda determinarse la versión aplicada, no informa transición completada.

Una versión antigua no vuelve a abrir un período.

El resto de las fechas puede continuar operando mientras se cierra una fecha anterior.

## 11. Integridad, auxiliares y conciliación

### 11.1. Confirmación del emisor

Cada emisor declara:

- Ámbito y fecha.

- Corte.

- Identidad de confirmación.

- Conjunto de hechos o secuencias cubiertas.

- Cantidad.

- Resumen del conjunto.

- Faltantes y pendientes conocidos.

También declara expresamente un ámbito sin hechos.

Cierres compara la declaración con los hechos recibidos y sus tratamientos.

La ausencia de mensajes no demuestra completitud.

### 11.2. Auxiliar

El auxiliar conserva:

- Servicio propietario.

- Tipo.

- Corte.

- Versión.

- Moneda.

- Conceptos.

- Dimensiones.

- Saldos.

- Movimientos o referencias de detalle.

- Pendientes.

- Resumen de contenido.

Las revisiones conservan las versiones anteriores.

### 11.3. Comparaciones

Se concilian al menos:

- Depósitos a la vista y a plazo.

- Capital e intereses de financiación.

- Previsiones.

- Deuda de tarjetas.

- Disponibilidades.

- Liquidaciones.

- Cambio.

- Custodia.

- Tributos.

- Cuentas transitorias.

Se comparan conceptos equivalentes.

Saldo registrado 100 y reserva 30 no significan pasivo 70.

Los cortes diferentes se homogeneizan antes de concluir un faltante.

### 11.4. Diferencias

Se distinguen:

- Temporal.

- Hecho faltante.

- Duplicación.

- Importe incompatible.

- Clasificación incorrecta.

- Fuente incompleta.

- Causa no determinada.

Cada diferencia conserva responsable, evidencia, tratamiento, fecha objetivo y comprobación posterior.

No existe un importe mínimo para ignorarlas.

Una causa desconocida no se convierte en gasto válido por aprobar su seguimiento.

## 12. Cierres diarios, mensuales y de ejercicio

### 12.1. Cierre diario

Se inicia el siguiente día operativo y conserva la fecha económica original.

El proceso:

- Aplicar la barrera de cierre.

- Fijar corte.

- Comprobar integridad.

- Resolver hechos pendientes.

- Verificar equilibrio.

- Conciliar auxiliares.

- Revisar transitorias.

- Registrar regularizaciones.

- Repetir controles afectados.

- Obtener aprobación.

- Sellar registraciones.

- Confirmar cierre.

El sellado impide nuevas registraciones, incluidas regularizaciones sin otro tratamiento autorizado.

Si un ajuste posterior modifica los controles aprobados, se requiere otra aprobación del contenido resultante.

No se cierra con duplicaciones confirmadas, desequilibrio o hechos conocidos sin tratamiento.

Una obligación externa incierta puede continuar cuando esté correctamente representada y conciliada como pendiente.

### 12.2. Cierre mensual

Comprueba cierres diarios, devengamientos, previsiones, valuaciones y conciliaciones.

No recalcula las obligaciones económicas de los productos.

Las reclasificaciones y ajustes específicamente contables se ejecutan mediante Registración.

### 12.3. Ejercicio

El ejercicio corresponde al 1 de enero hasta el 31 de diciembre.

El cierre transfiere ingresos y gastos al resultado acumulado mediante un asiento identificado.

La continuidad de saldos del siguiente ejercicio no genera una segunda incorporación patrimonial. Los saldos iniciales
referencian el cierre que los fundamenta.

Transferir resultados no distribuye ganancias ni crea otro ingreso operativo.

### 12.4. Resultado conservado

El cierre guarda:

- Versión.

- Corte y secuencia.

- Confirmaciones de fuentes.

- Auxiliares.

- Controles.

- Diferencias admitidas y tratamiento.

- Ajustes.

- Saldos.

- Aprobaciones.

- Resumen del conjunto.

Repetir una ejecución confirmada recupera ese resultado.

## 13. Hechos tardíos y reaperturas

Se aplican estas reglas:

| Situación | Tratamiento |
| --- | --- |
| Día cerrado y mes abierto | Reapertura controlada de la fecha y nuevo cierre. |
| Mes cerrado y ejercicio todavía no emitido como definitivo | Reapertura del período si cambia sus cifras. |
| Ejercicio emitido como definitivo | Ajuste identificado de períodos anteriores y evaluación de rectificaciones. |

La reapertura conserva motivo, alcance, aprobación y cierre anterior.

La fecha real de recepción no se modifica.

Los períodos posteriores afectados se revisan.

Una reapertura comienza aplicando la nueva condición en Registración. No basta con modificar el estado de Cierres.

Los informes asociados al cierre anterior permanecen conservados y se identifican como afectados cuando corresponda.

## 14. Información contable

### 14.1. Estructuras

Cada versión establece:

- Tipo de informe.

- Agrupaciones.

- Cuentas.

- Dimensiones.

- Signos de presentación.

- Fórmulas.

- Comparativos.

- Moneda.

- Vigencia.

- Aprobación.

Las fórmulas utilizan operaciones tipadas sobre partidas y agregaciones identificadas.

La estructura impide ciclos de cálculo y duplicaciones involuntarias.

Las cuentas sin correspondencia impiden emitir como completo un informe que deba incluirlas.

Los registros de orden se presentan separados.

### 14.2. Obtención de datos

Información obtiene partidas, saldos y cortes mediante interfaces de Registración y Cierres.

Puede mantener una proyección propia derivada de eventos, identificada como tal.

Antes de emitir comprueba que alcanzó el corte requerido y valida totales de control con Registración.

Una proyección incompleta no se presenta como fuente completa.

No consulta directamente las bases ajenas.

### 14.3. Informe

Cada ejecución conserva:

- Tipo.

- Período.

- Corte y secuencia.

- Estructura y versión.

- Plan utilizado.

- Moneda.

- Condición provisional o definitiva.

- Cierre de referencia.

- Cifras.

- Controles.

- Aprobación.

- Documento y versión.

- Comparativos.

- Justificación.

Un informe de período abierto es provisional.

Una cifra faltante no se reemplaza por cero.

Se comprueban igualdad patrimonial y correspondencia del resultado con ingresos y gastos.

### 14.4. Emisión definitiva y reapertura

La emisión definitiva de ejercicio se coordina con Cierres mediante una actuación persistente.

Cierres registra una reserva de emisión vinculada al cierre exacto. Mientras esa actuación no se resuelva, no confirma
una reapertura incompatible.

Información verifica el corte, confirma la emisión y comunica su resultado.

Si se pierde una respuesta, ambos servicios consultan la referencia original.

Cierres conserva la emisión definitiva para determinar el tratamiento de hechos posteriores. No depende únicamente de
que llegue un evento.

### 14.5. Justificación y rectificación

Cada cifra puede rastrearse hasta agrupaciones, cuentas, partidas, asientos, hechos y reglas.

Consultar un informe emitido recupera sus cifras originales.

Una rectificación crea otra emisión vinculada y conserva diferencias y fundamento.

Se distingue corrección de datos de cambio de presentación.

Información regulatoria recibe el aviso y administra su propio procedimiento de rectificación.

## 15. Interfaces HTTP

Las interfaces utilizan JSON y OpenAPI. Los comandos incorporan el contexto común.

### 15.1. Registración

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-09-001 | POST /ledger-account-proposals | Código, naturaleza, jerarquía y dimensiones. Devuelve propuesta. |
| CI-09-001 | POST /ledger-account-proposals/{id}/actions | Aprobación, habilitación o retiro. Devuelve versión y vigencia. |
| CI-09-002 | GET /ledger-accounts | Cuenta, clasificación o fecha. Devuelve plan autorizado. |
| CI-09-003 | POST /accounting-rule-proposals | Fuentes, integración y partidas. Devuelve regla propuesta. |
| CI-09-003 | POST /accounting-rule-proposals/{id}/actions | Aprobación y habilitación. Devuelve versión aplicable. |
| CI-09-004 | GET /accounting-rules | Tipo, consecuencia y vigencia. Devuelve regla y requisitos. |
| CI-09-005 | POST /accounting-facts | Hecho y antecedentes tipados. Devuelve recepción y tratamiento. |
| CI-09-006 | GET /accounting-facts/{id}/treatment | Devuelve integración, impedimentos y asientos. |
| CI-09-007 | POST /accounting-integrations/{id}/antecedents | Antecedentes de sus propietarios. Devuelve coincidencias y pendientes. |
| CI-09-008 | POST /accounting-posting-actions | Hecho validado y tratamiento de período. Devuelve asiento. |
| CI-09-009 | POST /manual-entry-proposals | Partidas, fundamento y originales. Devuelve propuesta validada. |
| CI-09-009 | POST /manual-entry-proposals/{id}/confirmations | Aprobación del contenido exacto. Devuelve asiento. |
| CI-09-010 | POST /accounting-entries/{id}/corrections | Reversión o reclasificación y alcance. Devuelve registros y remanente. |
| CI-09-011 | POST /accounting-revaluations | Corte, posiciones y cotizaciones. Devuelve diferencias y asientos. |
| CI-09-012 | GET /accounting-entries/{id} | Devuelve partidas y procedencia. |
| CI-09-012 | GET /accounting-actions/{actionId} | Recupera resultado original. |

### 15.2. Cierres

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-09-013 | POST /accounting-periods | Ejercicio, intervalos y condiciones. Devuelve períodos. |
| CI-09-013 | POST /accounting-periods/{id}/control-actions | Transición versionada. Devuelve aplicación y confirmaciones. |
| CI-09-014 | POST /accounting-period-checks | Fecha, actuación y autorización. Devuelve admisión y versión. |
| CI-09-015 | POST /accounting-integrity-confirmations | Emisor, corte y conjunto de hechos. Devuelve cobertura comprobada. |
| CI-09-016 | POST /accounting-auxiliary-snapshots | Auxiliar, conceptos y corte. Devuelve recepción y coherencia. |
| CI-09-017 | POST /accounting-reconciliations | Auxiliares y saldos comparables. Devuelve diferencias. |
| CI-09-018 | POST /accounting-differences/{id}/resolutions | Diagnóstico, evidencia y aprobación. Devuelve tratamiento y comprobación. |
| CI-09-019 | POST /daily-accounting-closes | Fecha y actuación. Devuelve ejecución, controles y cierre. |
| CI-09-020 | POST /period-accounting-closes | Período mensual o ejercicio. Devuelve resultado y continuidad. |
| CI-09-021 | POST /late-accounting-fact-treatments | Hecho, impacto y aprobación. Devuelve tratamiento autorizado. |
| CI-09-022 | GET /accounting-closes/{id} | Devuelve corte, controles y vigencia. |
| CI-09-022 | GET /accounting-periods/{id} | Devuelve estado e historia de cierres. |

Las aplicaciones de barreras en Registración forman parte del protocolo de control del período. No conceden a otros
servicios capacidad para cambiarlo.

### 15.3. Información

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-09-023 | POST /accounting-report-structures | Agrupaciones, fórmulas y correspondencias. Devuelve propuesta. |
| CI-09-023 | POST /accounting-report-structures/{id}/actions | Aprobación y habilitación. Devuelve versión. |
| CI-09-024 | GET /accounting-report-structures/{id} | Devuelve estructura y criterios. |
| CI-09-025 | POST /accounting-reports | Tipo, período, corte y condición. Devuelve ejecución o emisión. |
| CI-09-026 | GET /accounting-balances | Cuenta, dimensiones y corte. Devuelve inicial, movimientos y final. |
| CI-09-026 | GET /accounting-report-figures/{id}/provenance | Devuelve recorrido de justificación. |
| CI-09-027 | GET /accounting-reports/{id} | Devuelve emisión conservada y rectificaciones. |
| CI-09-028 | POST /accounting-reports/{id}/rectifications | Causa, registros o presentación y aprobación. Devuelve nueva emisión. |

Las consultas masivas utilizan cursor y corte estable.

Los estados HTTP son 200, 201 y 202 para consulta, creación y trabajo persistido pendiente.

Los errores utilizan Problem Details con 400, 401, 403, 404, 409, 422 o 503.

## 16. Eventos

El sobre incluye identidad, esquema, emisor, agregado, versión, operación, actuación, causa, correlación y fechas.

| Contrato | Evento | Contenido |
| --- | --- | --- |
| CI-09-029 | AccountingPlanOrRuleChanged | Cuenta o regla, versión, vigencia y transición. |
| CI-09-030 | AccountingFactOrEntryProcessed | Hecho, consecuencia, tratamiento, asiento e impedimentos. |
| CI-09-031 | AccountingPeriodOrCloseChanged | Período, versión, barrera, corte y reapertura. |
| CI-09-032 | AccountingDifferenceChanged | Comparación, causa, tratamiento y comprobación. |
| CI-09-033 | AccountingReportIssuedOrRestated | Informe, versión, corte, condición y emisión anterior. |

Los temas son:

- banking.accounting-entry.events.

- banking.accounting-close.events.

- banking.accounting-report.events.

La clave es el agregado.

Los eventos de tratamiento no ordenan repetir la operación bancaria.

Un evento antiguo de apertura no invalida un cierre posterior.

## 17. Persistencia, idempotencia y recuperación

### 17.1. Estructuras principales

| Servicio | Persistencia |
| --- | --- |
| Registración | Plan, reglas, hechos, integraciones, asientos, partidas, saldos y controles locales de período. |
| Cierres | Períodos, aplicaciones, confirmaciones, auxiliares, conciliaciones, diferencias, cierres y reaperturas. |
| Información | Estructuras, proyecciones, ejecuciones, cifras, procedencia y emisiones. |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

### 17.2. Integridad

Se exige:

- Código contable único.

- Jerarquía sin ciclos.

- Versiones aprobadas inmutables.

- Consecuencia económica contabilizada una sola vez.

- Partidas completas y equilibradas.

- Correcciones dentro del alcance pendiente.

- Períodos sin superposición.

- Aplicaciones de control monotónicas.

- Cierre identificable por versión.

- Informes emitidos inmutables.

### 17.3. Recuperación

Una actuación conserva clave, emisor, resumen de entrada y resultado.

Misma clave y contenido recuperan el resultado. Otro contenido produce conflicto.

La identidad económica impide duplicaciones aunque cambie la clave técnica.

Los procesos conservan intención antes de invocar dependencias.

No se mantienen transacciones SQL durante llamadas HTTP o publicación Kafka.

Outbox publica hechos confirmados. Inbox y efecto local se confirman antes de avanzar el offset.

Una ejecución interrumpida de cierre recupera ajustes y controles existentes; no los vuelve a crear.

## 18. Seguridad y observabilidad

Keycloak autentica usuarios y servicios.

Los permisos distinguen preparación y aprobación de plan, reglas, ajustes, diferencias, cierres, reaperturas e informes.

El permiso técnico se complementa con facultad institucional y alcance sobre el objeto.

Las consultas restringen datos de clientes y evidencias según finalidad.

PostgreSQL es autoridad de asientos, saldos, períodos y emisiones.

Redis puede acelerar consultas de definiciones inmutables. No decide admisión ni confirma saldos.

Los documentos se conservan mediante Documentos. MongoDB no es necesario para las responsabilidades definidas.

Se observan:

- Hechos sin regla.

- Integraciones incompletas.

- Conflictos de identidad.

- Retraso entre hecho y asiento.

- Diferencias por auxiliar.

- Transitorias pendientes.

- Emisores sin confirmación.

- Barreras sin aplicación confirmada.

- Cierres interrumpidos.

- Proyecciones de informes incompletas.

- Emisiones afectadas por correcciones.

Los identificadores particulares no se utilizan como etiquetas de métricas.

## 19. Pruebas y correspondencia funcional

Se prueban equilibrio, reconstrucción de saldos, idempotencia y trazabilidad con PostgreSQL real.

Los escenarios incluyen:

- Mismo hecho por HTTP y Kafka.

- Antecedentes recibidos en distinto orden.

- Desembolso y acreditación integrados sin duplicación.

- Acumulado tratado como diferencia.

- Asiento concurrente con cierre.

- Regularización concurrente con sellado.

- Reversión parcial repetida.

- Reexpresión sin cambiar moneda original.

- Confirmación de integridad con hechos faltantes.

- Reapertura con controles posteriores afectados.

- Emisión definitiva concurrente con reapertura.

- Proyección de informes atrasada.

- Rectificación sin perder emisión anterior.

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-09-01. Plan de cuentas | Jerarquía y versiones. | CI-09-001, 002 y 029. |
| OF-09-02. Reglas | Fuentes, partidas y aprobación. | CI-09-003 y 004. |
| OF-09-03. Hechos | Recepción y tratamiento. | CI-09-005 y 006. |
| OF-09-04. Integración | Antecedentes y consecuencia única. | CI-09-007. |
| OF-09-05. Asientos automáticos | Confirmación y saldos atómicos. | CI-09-008 y 030. |
| OF-09-06. Ajustes manuales | Propuesta y aprobación exacta. | CI-09-009. |
| OF-09-07. Reversiones | Correcciones vinculadas. | CI-09-010 y 012. |
| OF-09-08. Multimoneda | Conversión y reexpresión. | CI-09-011. |
| OF-09-09. Devengamientos | Variaciones y conceptos. | CI-09-005, 007 y 008. |
| OF-09-10. Orden y custodia | Libro separado. | CI-09-005 y 008. |
| OF-09-11. Períodos | Control y barrera aplicada. | CI-09-013 y 014. |
| OF-09-12. Conciliación | Integridad y auxiliares. | CI-09-015, 016 y 017. |
| OF-09-13. Diferencias | Tratamiento comprobado. | CI-09-018 y 032. |
| OF-09-14. Cierre diario | Controles y sellado. | CI-09-019 y 022. |
| OF-09-15. Cierres de período | Mensual, ejercicio y continuidad. | CI-09-020. |
| OF-09-16. Hechos tardíos | Ajuste o reapertura. | CI-09-021 y 031. |
| OF-09-17. Estructuras | Presentación versionada. | CI-09-023 y 024. |
| OF-09-18. Informes | Corte, controles y emisión. | CI-09-025 y 033. |
| OF-09-19. Justificación | Partidas y procedencia. | CI-09-026 y 027. |
| OF-09-20. Rectificación | Nueva emisión vinculada. | CI-09-028 y 033. |
