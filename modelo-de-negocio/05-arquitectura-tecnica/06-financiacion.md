# Diseño técnico — Módulo 6. Financiación

## 1. Propósito y alcance

El módulo administra la evaluación, constitución, utilización y seguimiento de financiaciones, sus garantías y las
actuaciones de recuperación.

Comprende:

- Evaluación y aprobación crediticia.

- Afectación de exposición mediante Riesgos.

- Constitución de préstamos y líneas.

- Cálculos económicos, cronogramas y costo financiero total.

- Desembolsos y utilizaciones.

- Devengamientos, obligaciones e imputaciones.

- Descubierto de cuenta corriente.

- Precancelaciones y refinanciaciones.

- Garantías y recuperaciones.

- Descuento de cheques.

- Mora, previsiones y cierre.

La aprobación, la celebración contractual, la constitución del producto y el desembolso son hechos diferentes.

La deuda de capital surge de desembolsos o utilizaciones comprobados. Constituir una línea no genera deuda por todo su
límite.

## 2. Servicios y propiedad de la información

| Servicio                   | Identificador técnico     | Base de datos        | Responsabilidad                                                            |
|----------------------------|---------------------------|----------------------|----------------------------------------------------------------------------|
| Evaluación crediticia      | credit-assessment-service | credit_assessment_db | Antecedentes, análisis, decisiones, vigencia y aplicación de aprobaciones. |
| Préstamos y líneas         | lending-service           | lending_db           | Financiaciones, condiciones económicas, utilizaciones, deuda e imputación. |
| Garantías                  | collateral-service        | collateral_db        | Respaldos, valuaciones, cobertura, afectaciones, constitución y ejecución. |
| Cobranzas y recuperaciones | collection-service        | collection_db        | Casos, gestiones, propuestas, acuerdos y coordinación de recuperos.        |

Cada servicio administra su propia base PostgreSQL, usuario y migraciones.

No existen consultas SQL, relaciones JPA ni claves foráneas entre bases de servicios.

### 2.1. Distribución de autoridad

| Información o decisión                         | Responsable            |
|------------------------------------------------|------------------------|
| Decisión crediticia individual                 | Evaluación crediticia. |
| Políticas, clasificación y límites compartidos | Riesgos.               |
| Compromiso de exposición                       | Riesgos.               |
| Contrato y modificaciones                      | Contratos.             |
| Deuda de préstamos y líneas                    | Préstamos y líneas.    |
| Utilización inmediata del descubierto          | Fondos.                |
| Deuda de tarjeta                               | Cuenta de tarjeta.     |
| Cobertura de garantías                         | Garantías.             |
| Legitimación y cobro del cheque                | Cheques y ECHEQ.       |
| Movimientos de cuentas                         | Fondos.                |
| Liquidaciones externas                         | Tesorería.             |
| Registración contable                          | Contabilidad.          |

Cobranzas conserva referencias de deuda. No mantiene una segunda deuda ni decide su imputación.

## 3. Convenciones de datos y actuaciones

### 3.1. Tipos

| Dato                 | Java        | PostgreSQL     | HTTP                 |
|----------------------|-------------|----------------|----------------------|
| Identificador        | UUID        | uuid           | Cadena UUID.         |
| Versión o secuencia  | long        | bigint         | Entero positivo.     |
| Instante             | Instant     | timestamptz    | ISO 8601 en UTC.     |
| Fecha económica      | LocalDate   | date           | YYYY-MM-DD.          |
| Importe              | BigDecimal  | numeric(24,8)  | Cadena decimal.      |
| Tasa o coeficiente   | BigDecimal  | numeric(24,12) | Cadena decimal.      |
| Moneda               | Enumeración | varchar(3)     | ARS o USD.           |
| Código               | String      | varchar(80)    | Cadena normalizada.  |
| Referencia externa   | String      | varchar(120)   | Cadena.              |
| Descripción          | String      | varchar(200)   | Cadena.              |
| Fundamento           | String      | varchar(4000)  | Cadena.              |
| Resumen de contenido | String      | char(64)       | SHA-256 hexadecimal. |

Las tasas se conservan como proporciones: 36,5 % se representa internamente como 0.365.

Los cálculos no utilizan punto flotante binario. Los algoritmos de potencias y búsqueda de tasas emplean aritmética
decimal con precisión superior a la persistida.

Los importes exigibles respetan centavos. Los cálculos intermedios conservan al menos doce decimales y se redondean
mediante HALF_UP al determinar el importe monetario.

Un valor ausente no equivale a cero.

### 3.2. Contexto común

Cada comando identifica:

- Actuación.

- Operación de negocio.

- Objeto y versión esperada.

- Finalidad.

- Causa y fundamento.

- Evidencias.

- Contrato y aprobación cuando correspondan.

- Fecha efectiva y recepción acreditada cuando afecten derechos.

El actor y el servicio emisor proceden de la autenticación.

Las referencias documentales identifican documento, versión y resumen de contenido. Los archivos permanecen bajo
autoridad de Documentos.

### 3.3. Identidades económicas

Se mantienen identificadores diferentes para:

- Evaluación y decisión.

- Aplicación de aprobación.

- Compromiso de exposición.

- Financiación.

- Utilización.

- Desembolso.

- Determinación económica.

- Obligación y concepto.

- Pago y preparación.

- Imputación.

- Garantía y afectación.

- Recuperación.

- Corrección.

Un mismo ingreso conserva su referencia aunque intervengan Pagos, Fondos y Préstamos y líneas.

### 3.4. Respuestas

Las respuestas distinguen petición recibida, decisión favorable, aprobación utilizable, constitución, entrega
confirmada, deuda reconocida y aplicación de pago.

Los estados técnicos son PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

Cada respuesta informa efectos confirmados, referencias, remanentes y pendientes.

## 4. Evaluación crediticia

### 4.1. Expediente

| Campo                       | Contenido                          |
|-----------------------------|------------------------------------|
| assessmentId                | Identidad estable.                 |
| originRequestId             | Solicitud o petición de producto.  |
| applicantPartyId            | Solicitante principal.             |
| obligors                    | Obligados y roles.                 |
| productReference            | Producto y versión.                |
| requestedAmount             | Importe propuesto.                 |
| currency                    | Moneda.                            |
| requestedTerm               | Plazo y unidad.                    |
| purpose                     | Finalidad crediticia.              |
| proposedConditionsReference | Determinación económica utilizada. |
| policyReference             | Política y versión.                |
| inputHash                   | Resumen del conjunto evaluado.     |
| version                     | Control de concurrencia.           |

Los antecedentes se conservan por tipo, fuente, fecha, vigencia y estado de comprobación.

Ingresos declarados, ingresos comprobados y estimaciones de flujo se distinguen explícitamente.

La falta de respuesta de una fuente no demuestra ausencia de obligaciones.

### 4.2. Análisis

El análisis registra:

- Ingresos o flujos admitidos.

- Compromisos existentes.

- Servicio de deuda propuesto.

- Indicadores calculados.

- Escenarios.

- Exposición consultada.

- Cobertura informada por Garantías.

- Impedimentos.

- Excepciones solicitadas.

- Conclusión.

Cada indicador identifica fórmula versionada, entradas y resultado.

Las metodologías, umbrales y escenarios obligatorios proceden de Riesgos. No se incorporan umbrales ocultos en el
servicio.

Una tasa variable exige el escenario adverso definido por la política.

Una garantía suficiente no elimina un impedimento de capacidad de pago.

### 4.3. Decisión

La decisión contiene:

- Importe máximo.

- Moneda.

- Plazo.

- Modalidad.

- Condiciones autorizadas.

- Garantías requeridas.

- Requisitos por etapa.

- Vigencia.

- Autoridad o política automática.

- Excepciones aprobadas.

- Compromiso de exposición.

Los requisitos distinguen celebración, constitución y desembolso, conservando cualquier etapa más específica establecida
por Contratos.

### 4.4. Estados

| Dimensión   | Estados                                                                                       |
|-------------|-----------------------------------------------------------------------------------------------|
| Expediente  | En preparación, pendiente de antecedentes, analizado o cerrado.                               |
| Decisión    | Pendiente, aprobada, condicionada o rechazada.                                                |
| Utilización | No utilizable, utilizable, aplicación comprometida, aplicada, vencida, retirada o sustituida. |
| Exposición  | No requerida, pendiente, confirmada, transformación pendiente o regularización pendiente.     |

Una aprobación favorable con exposición pendiente no se informa como utilizable.

La vigencia máxima es de treinta días corridos, limitada por el vencimiento anterior de sus antecedentes.

La fecha límite concreta se conserva en la decisión. No se recalcula al consultarla.

### 4.5. Exposición compartida

Evaluación conserva la intención antes de solicitar afectación a Riesgos.

La consulta previa de exposición no reserva capacidad.

La aprobación se habilita después de confirmar la afectación exigible.

Una sustitución referencia el compromiso anterior y coordina su modificación. No crea dos compromisos independientes por
el mismo importe.

Vencimiento o retiro no liberan exposición mientras exista contratación o constitución incierta.

### 4.6. Aplicación de una aprobación

La aplicación vincula aprobación, versión, producto y actuación de constitución.

Su confirmación se ordena frente a retiro y vencimiento bajo bloqueo de la aprobación.

Si la aplicación se confirmó válidamente, el vencimiento posterior no la elimina. Si después falla la constitución, se
regulariza mediante una actuación vinculada.

Repetir la aplicación para la misma constitución recupera el resultado.

## 5. Préstamos y líneas

### 5.1. Financiación

| Campo                        | Regla                                                                |
|------------------------------|----------------------------------------------------------------------|
| financingId                  | UUID estable.                                                        |
| financingReference           | Referencia visible única.                                            |
| constitutionId               | Constitución de origen, única.                                       |
| contractId y contractVersion | Acuerdo aplicado.                                                    |
| approvalId y approvalVersion | Decisión habilitante.                                                |
| approvalApplicationId        | Aplicación confirmada.                                               |
| exposureReference            | Compromiso o exposición vinculada.                                   |
| modality                     | Amortizable, capital al vencimiento, línea, descubierto o descuento. |
| currency                     | Moneda inmutable.                                                    |
| obligors                     | Personas y roles.                                                    |
| economicDeterminationId      | Condiciones aceptadas.                                               |
| linkedAccountId              | Obligatoria para descubierto.                                        |
| version                      | Concurrencia.                                                        |

### 5.2. Dimensiones de estado

| Dimensión           | Valores                                                                     |
|---------------------|-----------------------------------------------------------------------------|
| Constitución        | En preparación, constituida o inconsistente.                                |
| Capacidad           | Pendiente, habilitada, suspendida, vencida o cerrada.                       |
| Situación económica | Sin utilización, con deuda, cancelada o residual.                           |
| Cumplimiento        | Al día, en mora, controvertida o en recuperación.                           |
| Coordinación        | Sin pendientes, pendiente de exposición, garantías, dinero o documentación. |

Una financiación puede tener capacidad cerrada y deuda pendiente.

Una baja contable no determina cancelación económica.

### 5.3. Constitución

El proceso:

- Conservar la intención.

- Verificar contrato, condiciones y participantes.

- Confirmar aplicación de la aprobación.

- Registrar la financiación.

- Vincular garantías.

- Transformar el compromiso de exposición mediante Riesgos.

- Informar constitución y pendientes a Solicitudes de productos.

El capital comienza en cero.

La financiación puede quedar identificada con dependencias pendientes, pero no se habilitan utilizaciones impedidas.

La transformación de exposición conserva el vínculo de origen. No se cuentan aprobación y producto como exposiciones
independientes.

## 6. Determinaciones económicas

### 6.1. Entrada completa

Cada cálculo recibe y conserva:

- Capital.

- Moneda.

- Modalidad.

- Tipo y valor de tasa.

- Convención.

- Sistema de amortización.

- Cantidad de cuotas.

- Anclaje y fechas.

- Calendario.

- Desembolsos previstos.

- Gracia.

- Cargos.

- Seguros obligatorios.

- Tributos.

- Bonificaciones.

- Condiciones de revisión.

- Reglas de precancelación.

La determinación tiene identidad, versión del algoritmo, resumen de entradas, resultados, supuestos y vigencia.

Una determinación utilizada por una propuesta es inmutable.

### 6.2. Tasas

Para nominal anual $j$ convertible con frecuencia anual $m$:

$i = \frac{j}{m}$

Para efectiva anual $e$:

$i = (1 + e)^{1/m} - 1$

La frecuencia mensual utiliza $m = 12$.

La modalidad simple Actual/365 se identifica expresamente. No se confunde con conversión periódica.

### 6.3. Francés

Con capital $P$, tasa periódica $i$ y $n$ cuotas:

$C = P\frac{i}{1 - (1 + i)^{- n}}$

Con tasa cero:

$C = \frac{P}{n}$

Cada cuota conserva interés y amortización.

La última amortización absorbe el residuo de capital sin crear capital adicional.

### 6.4. Alemán

La amortización teórica es:

$A = \frac{P}{n}$

El interés se calcula sobre capital pendiente al inicio del período.

La última amortización ajusta el residuo.

### 6.5. Capital al vencimiento

El capital vence en la fecha final. Los intereses tienen vencimientos periódicos o final según contrato.

Se aplica tasa nominal simple Actual/365.

Si la oferta parte de una efectiva, la conversión y la tasa nominal adoptada quedan determinadas y aceptadas antes de
contratar.

Diferir intereses no los capitaliza.

### 6.6. Fechas y períodos irregulares

Los cronogramas conservan:

- Fecha contractual.

- Fecha ajustada de pago.

- Período económico.

- Fecha original de anclaje.

Los vencimientos se ajustan al hábil siguiente. Ese ajuste no genera por sí solo interés adicional ni mora.

En períodos irregulares:

$i_{irregular} = (1 + i)^{d/D} - 1$

Se conserva el período regular de referencia que determina $D$.

La diferencia del primer período se identifica en la primera cuota. No cambia silenciosamente las siguientes.

### 6.7. Líneas y descubierto

Se aplica:

$I = P_{d}\frac{j_{d}}{365}$

La base es el capital utilizado al cierre diario. Se consideran fechas efectivas de pagos y variaciones.

No se redondea diariamente.

### 6.8. Gracia y tasa variable

La gracia de capital no extingue intereses ni los transforma en capital.

Los intereses diferidos tienen vencimientos explícitos.

La tasa variable identifica fuente, referencia, margen y revisiones.

Si falta una referencia:

- Se conserva provisionalmente la última válida.

- Se registra la incidencia.

- Se limita la continuidad a un período de revisión.

- Otra liquidación definitiva sin referencia exige resolución contractual.

Las diferencias no determinadas no generan mora por sí mismas.

### 6.9. Costo financiero total

El CFT utiliza flujos netos del cliente y fechas reales:

$\frac{D_{k}}{(1 + r)^{a_{k}/365}} = \frac{Q_{l}}{(1 + r)^{b_{l}/365}}$

Incluye conceptos obligatorios directamente asociados.

Una retención reduce la entrega neta. No se agrega otra vez como pago separado.

El cálculo conserva:

- Flujos monetarios redondeados.

- Fechas.

- Supuestos.

- Método numérico y versión.

- Tasa obtenida.

- Residuo de la ecuación.

- Resultado de convergencia.

No se publica un valor arbitrario si los flujos no permiten una tasa única o faltan conceptos obligatorios.

El resultado se presenta como porcentaje anual con dos decimales.

Una línea sin usos futuros definidos informa tasas y cargos; cada utilización concreta recibe su cálculo
correspondiente.

## 7. Desembolsos y utilizaciones

### 7.1. Desembolso

Cada entrega contiene:

- disbursementId.

- Financiación.

- Utilización relacionada.

- Importe bruto.

- Retenciones individualizadas.

- Neto.

- Moneda.

- Destino autorizado.

- Fecha aceptada.

- Determinación económica.

- Efectos monetarios.

- Resultado.

Se exige:

$Bruto = Neto + Retenciones$

Cada retención identifica concepto y aplicación. Retener no demuestra que el concepto quedó cancelado.

### 7.2. Ejecución

Antes de solicitar dinero se conservan intención, capacidad comprometida y efectos estables.

Se comprueban contrato, requisitos, garantía y exposición.

Fondos o Tesorería confirma la entrega conforme al circuito.

Solo después se reconoce el capital correspondiente, conservando como pendiente cualquier confirmación todavía
desconocida.

Una entrega rechazada sin efectos no genera capital.

Una respuesta perdida no permite repetirla.

Si cambian fecha o condiciones materiales, se recalcula y obtiene aceptación antes de ejecutar.

### 7.3. Capacidad de líneas

Para líneas renovables, la capacidad considera límite, capital utilizado y compromisos pendientes.

Para no renovables, se considera también el capital históricamente entregado que no vuelve a quedar utilizable por
amortización.

Los usos concurrentes se comprometen bajo bloqueo de la línea.

Los compromisos inciertos no se liberan por temporizador.

El descubierto no utiliza este circuito de solicitudes: Fondos acepta sus compromisos monetarios.

## 8. Deuda, devengamiento y mora

### 8.1. Obligaciones por concepto

Cada obligación identifica:

- Financiación.

- Concepto.

- Período u ocurrencia.

- Capital o base.

- Tasa y versión.

- Importe devengado.

- Importe exigible.

- Vencimiento contractual y ajustado.

- Importe pagado.

- Ajustes.

- Remanente.

Se separan capital, interés, comisión, seguro, tributo y gasto admitido.

El cronograma previsto no se vuelve a registrar como deuda adicional al liquidar.

### 8.2. Devengamiento

Los tramos se separan ante cambios de capital, tasa o condiciones.

El interés previsto y el interés por atraso no cubren dos veces los mismos días.

En cuotas amortizables, el capital vencido utiliza remuneración simple con tasa diaria equivalente a la efectiva anual
contractual.

Líneas y descubierto mantienen Actual/365.

No se generan intereses sobre intereses, comisiones o tributos.

No se incorpora tasa punitoria ni penalidad fija ordinaria por atraso.

### 8.3. Mora

La mora comienza al día siguiente del vencimiento ajustado.

Antes de determinar incumplimiento se comprueban ingresos oportunos conocidos y preparaciones con ejecución pendiente de
aplicación.

Una demora interna no produce mora adicional.

Riesgos clasifica. Préstamos y líneas informa impago, antigüedad y antecedentes.

Las rectificaciones conservan la historia y corrigen las consecuencias afectadas.

## 9. Preparación e imputación de pagos

### 9.1. Preparación

La preparación contiene:

- Pago de origen.

- Pagador y deudor.

- Financiación.

- Importe.

- Fecha efectiva prevista.

- Versión de deuda.

- Distribución por obligación y concepto.

- Excedente.

- Condiciones de confirmación.

La preparación no cancela deuda.

Las preparaciones concurrentes consideran las asignaciones anteriores para impedir distribuciones incompatibles.

### 9.2. Imputación ordinaria

Para préstamos y utilizaciones distintas de la reducción automática de descubierto:

- Obligaciones vencidas, desde la más antigua.

- Obligaciones exigibles del día.

- Dentro de cada obligación, interés ordinario y capital.

- Tributos asociados.

- Comisiones y gastos posteriores.

El pagador puede identificar financiación. Un ingreso sin destino determinable queda pendiente.

### 9.3. Confirmación

La aplicación exige evidencia del ingreso o cobro confirmado.

Dentro de la transacción se:

- Comprueba la identidad del ingreso.

- Bloquean financiación y obligaciones.

- Revisa la preparación.

- Determina la aplicación admisible.

- Registra imputaciones.

- Actualiza deuda.

- Conserva excedente.

- Publica el hecho mediante outbox.

Si la deuda cambió, se conserva el ingreso y se aplica únicamente lo procedente.

Un ingreso de 120 sobre deuda aplicable de 100 deja excedente 20. No precancela capital futuro sin instrucción.

### 9.4. Correcciones

Una corrección de imputación reutiliza el ingreso original.

Se registran contrapartidas y nuevas asignaciones vinculadas. No se vuelve a cobrar.

Las devoluciones requieren un efecto monetario identificado.

## 10. Descubierto

### 10.1. Autoridad y límites

Préstamos y líneas administra autorización, deuda e intereses.

Fondos administra utilización inmediata, reservas y capacidad monetaria.

Cada cambio de límite identifica versión, vigencia, autorización y actuación sustituida.

Un aumento no se informa como utilizable antes de confirmarse en Fondos.

### 10.2. Incorporación de variaciones

Se conservan:

- Cuenta y financiación.

- Movimiento o hecho.

- Secuencia de Fondos.

- Fecha efectiva.

- Utilización anterior y resultante.

- Compromisos.

- Cobertura protegida.

- Exceso.

Un evento antiguo no reemplaza una posición posterior.

Una discontinuidad exige recuperación antes de cerrar cálculos afectados. Un corte aislado no sustituye las variaciones
históricas necesarias para devengar correctamente.

### 10.3. Acreditaciones y pagos

Una acreditación ordinaria reduce directamente capital según Fondos.

No se redistribuye primero a intereses.

Los intereses y cargos se cobran mediante un débito separado, autorizado y limitado a fondos propios disponibles.

Si un pago debe cancelar capital de descubierto, ese componente se acredita en la cuenta. La reducción informada por
Fondos es su única cancelación de capital.

### 10.4. Reducción o vencimiento

La reducción:

- Impide usos nuevos incompatibles.

- Conserva capital.

- Conserva compromisos firmes e inciertos.

- Coordina cancelaciones permitidas.

- Reconoce cobertura protegida.

- Informa exposición y exceso.

No crea otra financiación para justificar el exceso.

La conciliación compara referencias y cortes compatibles. No corrige diferencias borrando deuda o ampliando
disponibilidad.

## 11. Precancelación y refinanciación

### 11.1. Precancelación

La liquidación identifica fecha, capital, conceptos devengados, pagos pendientes y vigencia hasta finalizar ese día.

No incluye intereses futuros ni comisión ordinaria de precancelación.

La parcial exige elegir reducción de plazo o cuota.

La ejecución requiere aceptación y fondos comprobados.

El capital cancelado deja de devengar desde su fecha efectiva.

Una liquidación vencida se actualiza antes de ejecutar.

### 11.2. Modificación y refinanciación

Cada actuación identifica:

- Obligaciones originales.

- Deuda al corte.

- Evaluación y aprobación.

- Acuerdo efectivo.

- Tratamiento de capital, intereses, cargos y tributos.

- Quitas.

- Nuevas obligaciones.

- Garantías.

- Exposición.

Una propuesta no modifica deuda.

La sustitución confirma conjuntamente dentro de Préstamos y líneas el tratamiento de obligaciones antiguas y nuevas. El
mismo capital no queda exigible en ambas.

No se capitalizan intereses automáticamente.

Las quitas condicionadas se aplican al cumplirse su condición. Una condonación definitiva no revive por perder otro
beneficio.

## 12. Descuento de cheques

### 12.1. Operación

Cada cheque tiene una financiación individual.

Se admite instrumento diferido, con fecha futura, legitimación comprobada, cedido al banco y sin compromiso
incompatible.

El cliente conserva responsabilidad por la financiación si el cheque no se cobra.

### 12.2. Cálculo

Con nominal $N$, tasa nominal anual $j$ y días reales $d$:

$I = N \times j \times \frac{d}{365}$

$Neto = N - I - Comisiones - TributosRetenidos$

Los días incluyen anticipo y excluyen cobro previsto.

La fecha prevista de cobro es la fecha de pago ajustada al siguiente hábil de compensación.

El neto debe ser positivo.

### 12.3. Formalización y anticipo

El proceso:

- Comprobar instrumento.

- Comprometerlo mediante Cheques y ECHEQ.

- Evaluar cliente, librador y exposición.

- Determinar liquidación y CFT.

- Obtener contrato y aceptación.

- Confirmar cesión.

- Ejecutar anticipo y retenciones.

- Reconocer financiación.

- Solicitar presentación oportuna.

Al confirmar se reconoce nominal y entrega neta, manteniendo el interés retenido pendiente de devengar.

No se reconoce todo el interés como ganado al inicio.

Una falla de anticipo con cesión confirmada exige comprobar ausencia de entrega y regularizar la cesión.

### 12.4. Cobro y rechazo

El cobro del cheque cancela nominal pendiente. No vuelve a acreditarse como ingreso libre del cliente.

El rechazo no crea otro capital.

Desde el día siguiente al vencimiento efectivo, el capital impago devenga remuneración simple Actual/365 a la tasa
contractual, sin repetir el interés anticipado original.

Las demoras exclusivamente atribuibles al banco no generan interés adicional al cliente.

### 12.5. Cancelación y concurrencia

En cancelación anticipada, el interés retenido no devengado reduce la liquidación.

No se libera un instrumento con presentación firme o incierta sin resolverla.

El pago del cliente y el cobro del cheque se aplican bajo el mismo control de deuda.

La financiación se cancela una sola vez. El excedente conserva titular y destino autorizado.

## 13. Garantías

### 13.1. Datos

La garantía conserva:

- Naturaleza del respaldo.

- Bien, derecho o garante.

- Propietarios.

- Contrato.

- Documentos.

- Valuaciones.

- Moneda.

- Cargas.

- Prioridades.

- Seguros.

- Cobertura admitida.

- Afectaciones.

- Constitución.

- Ejecución y liberación.

La valuación identifica fuente, fecha, vigencia y criterio de admisibilidad de Riesgos.

Valor económico, cobertura admitida y cobertura disponible son cantidades diferentes.

### 13.2. Afectación

Cada afectación identifica obligación, importe o alcance, prioridad y estado.

Las afectaciones pendientes consumen cobertura.

La asignación se confirma bajo bloqueo de la garantía. Dos créditos no reciben íntegramente la misma cobertura libre.

Una consulta no compromete cobertura.

### 13.3. Estados

Se distinguen:

- Ofrecida.

- En evaluación.

- Admitida.

- Constitución pendiente.

- Constituida.

- Revisión requerida.

- Sustitución pendiente.

- En ejecución.

- Liberación pendiente.

- Liberada.

La constitución exige evidencia de las actuaciones requeridas.

### 13.4. Sustitución y liberación

Antes de liberar se consultan todas las obligaciones cubiertas y sus compromisos.

El respaldo anterior permanece hasta confirmar el sustituto.

La liberación se coordina con los acreedores para impedir nuevos usos incompatibles sobre una cobertura que se está
retirando.

Aceptar menor cobertura requiere decisión crediticia y de riesgo.

La liberación económica y la documental se informan separadamente.

## 14. Cobranzas y recuperaciones

### 14.1. Caso

El caso conserva:

- Acreedor.

- Obligaciones referenciadas.

- Deudor.

- Corte de deuda.

- Mora.

- Pagos pendientes conocidos.

- Controversias.

- Garantías.

- Responsable.

- Estrategia.

- Restricciones de contacto.

- Gestiones y acuerdos.

Antes de reclamar se actualiza la situación con el acreedor.

Una promesa no reduce deuda.

### 14.2. Acuerdos

Las propuestas identifican conceptos, importes, fechas, quitas, condiciones y consecuencias.

La aprobación no reemplaza la formalización contractual cuando sea necesaria.

El acreedor confirma el cambio económico.

Los gastos externos requieren evidencia y fundamento. Los gastos internos ordinarios no se agregan como penalidad.

### 14.3. Ejecución y recupero

Garantías administra el respaldo y las etapas de ejecución.

Cobranzas coordina distribución y aplicación con el acreedor.

Se distinguen venta, pago del garante y adjudicación. Una adjudicación no se presenta como efectivo.

El recupero utiliza una referencia única y evidencia del ingreso o del valor admitido.

Para deuda 70 y recupero aplicable 80, se cancelan 70 y se conserva excedente 10 a favor de quien corresponda.

El cierre del caso no extingue deuda por sí solo.

## 15. Previsiones y cierre

### 15.1. Previsiones

Préstamos y líneas recibe clasificación y metodología de Riesgos y calcula sobre posiciones al corte.

Conserva entradas, versión, cobertura requerida, importe previamente reconocido y variación.

Pasar de 20 a 25 comunica diferencia 5.

Previsionar o dar de baja contablemente no paga ni condona deuda.

No se calcula otra previsión sobre deuda de tarjetas.

### 15.2. Cancelación

El cierre:

- Impide nuevos usos.

- Resuelve desembolsos y cobros inciertos.

- Comprueba capital y conceptos.

- Coordina exposición.

- Resuelve garantías e instrumentos.

- Trata saldos a favor.

- Distingue cancelación económica y trámites documentales.

Capital cero con interés pendiente no permite informar libre deuda total.

El vencimiento de una línea no borra deuda.

Las demoras internas no prolongan cargos futuros.

## 16. Interfaces HTTP

Las interfaces utilizan JSON y esquemas OpenAPI. Los comandos incorporan el contexto común.

### 16.1. Evaluación crediticia

| Contrato  | Interfaz                                  | Entrada y resultado                                                                 |
|-----------|-------------------------------------------|-------------------------------------------------------------------------------------|
| CI-06-001 | POST /credit-assessments                  | Solicitud, obligados, condiciones y antecedentes. Devuelve expediente y faltantes.  |
| CI-06-001 | POST /credit-assessments/{id}/antecedents | Fuentes y evidencias nuevas. Devuelve suficiencia actualizada.                      |
| CI-06-002 | POST /credit-assessments/{id}/analyses    | Condiciones y política. Devuelve indicadores, escenarios y conclusión.              |
| CI-06-003 | POST /credit-assessments/{id}/decisions   | Decisión, condiciones y aprobación. Devuelve vigencia y exposición.                 |
| CI-06-004 | POST /credit-approvals/{id}/reviews       | Causa, cambios y situación de uso. Devuelve continuidad, retiro o sustitución.      |
| CI-06-005 | GET /credit-assessments/{id}              | Devuelve antecedentes y decisiones.                                                 |
| CI-06-005 | GET /credit-approvals/{id}                | Devuelve alcance, vigencia y aplicación.                                            |
| CI-06-006 | POST /credit-approvals/{id}/applications  | Constitución, producto y condiciones. Devuelve aplicación ordenada frente a retiro. |

### 16.2. Garantías

| Contrato  | Interfaz                                   | Entrada y resultado                                                            |
|-----------|--------------------------------------------|--------------------------------------------------------------------------------|
| CI-06-007 | POST /collaterals                          | Respaldo, titularidad, valuación y cargas. Devuelve admisibilidad y cobertura. |
| CI-06-008 | POST /collaterals/{id}/constitutions       | Contrato y evidencia constitutiva. Devuelve perfeccionamiento.                 |
| CI-06-008 | POST /collaterals/{id}/allocations         | Obligaciones, alcance y prioridad. Devuelve cobertura afectada.                |
| CI-06-009 | POST /collaterals/{id}/reviews             | Actualización, sustitución o liberación. Devuelve cobertura y pendientes.      |
| CI-06-010 | GET /collaterals/{id}                      | Devuelve situación y distribución al corte.                                    |
| CI-06-035 | POST /collaterals/{id}/enforcement-actions | Caso, decisión y evidencia. Devuelve ejecución y resultado.                    |

### 16.3. Préstamos y líneas

| Contrato  | Interfaz                                                 | Entrada y resultado                                                                   |
|-----------|----------------------------------------------------------|---------------------------------------------------------------------------------------|
| CI-06-011 | POST /financings                                         | Constitución, contrato y aprobación. Devuelve producto y pendientes.                  |
| CI-06-012 | POST /financing-calculations                             | Condiciones económicas completas. Devuelve cronograma, flujos y CFT.                  |
| CI-06-013 | POST /financings/{id}/disbursements                      | Entrega, bruto, retenciones, neto y destino. Devuelve ejecución y capital.            |
| CI-06-014 | POST /financings/{id}/limit-changes                      | Límite, vigencia y aprobación. Devuelve autorizado, aplicado y pendientes.            |
| CI-06-015 | POST /financings/{id}/utilizations                       | Uso identificado y condiciones. Devuelve compromiso y entrega.                        |
| CI-06-016 | POST /financings/{id}/overdraft-updates                  | Variación y secuencia de Fondos. Devuelve incorporación o diferencia.                 |
| CI-06-017 | POST /financings/{id}/accruals                           | Período, posiciones y condiciones. Devuelve devengado, exigible y ajustes.            |
| CI-06-018 | GET /financings/{id}/position                            | Corte y alcance. Devuelve deuda descompuesta.                                         |
| CI-06-019 | POST /financings/{id}/payment-preparations               | Pago, importe y fecha. Devuelve distribución y excedente.                             |
| CI-06-020 | POST /financing-payment-preparations/{id}/resolutions    | Confirmación de ingreso o cancelación comprobada. Devuelve aplicación.                |
| CI-06-021 | POST /financings/{id}/overdraft-charge-collections       | Conceptos y autorización. Devuelve cobro con fondos propios.                          |
| CI-06-022 | POST /financings/{id}/prepayment-quotes                  | Fecha, alcance y elección. Devuelve liquidación vigente.                              |
| CI-06-023 | POST /financings/{id}/prepayments                        | Liquidación, aceptación y fondos. Devuelve cancelación y cronograma.                  |
| CI-06-024 | POST /delinquency-assessments                            | Cartera o financiación y corte. Devuelve mora y verificaciones.                       |
| CI-06-025 | POST /financings/{id}/restructurings                     | Acuerdo y tratamiento de conceptos. Devuelve sustitución y deuda.                     |
| CI-06-026 | POST /portfolio-provision-calculations                   | Corte, clasificación y metodología. Devuelve requerido y variación.                   |
| CI-06-027 | POST /financings/{id}/corrections                        | Original, error y aprobación. Devuelve ajustes relacionados.                          |
| CI-06-028 | POST /financings/{id}/closures                           | Causa y evidencias. Devuelve cierre económico y pendientes.                           |
| CI-06-029 | POST /cheque-discount-operations                         | Instrumento y condiciones. Devuelve compromiso, neto y CFT.                           |
| CI-06-030 | POST /cheque-discount-operations/{id}/advances           | Contrato, cesión y liquidación. Devuelve anticipo y nominal.                          |
| CI-06-031 | POST /cheque-discount-operations/{id}/instrument-results | Presentación, resultado y referencia económica. Devuelve aplicación o incumplimiento. |
| CI-06-032 | GET /financing-actions/{actionId}                        | Recupera etapas, efectos y pendientes originales.                                     |

### 16.4. Cobranzas

| Contrato  | Interfaz                                       | Entrada y resultado                                                           |
|-----------|------------------------------------------------|-------------------------------------------------------------------------------|
| CI-06-033 | POST /collection-cases                         | Acreedor, obligaciones y mora. Devuelve caso y estrategia.                    |
| CI-06-033 | POST /collection-cases/{id}/debt-updates       | Posición del acreedor y controversias. Devuelve situación de gestión.         |
| CI-06-034 | POST /collection-cases/{id}/management-actions | Contacto, promesa, propuesta o acuerdo tipado. Devuelve seguimiento.          |
| CI-06-036 | POST /collection-cases/{id}/recoveries         | Fuente, evidencia, prioridades y destino. Devuelve aplicaciones y excedentes. |
| CI-06-037 | GET /collection-cases/{id}                     | Devuelve gestiones y deuda referenciada.                                      |
| CI-06-037 | POST /collection-cases/{id}/closures           | Causa y evidencia. Devuelve cierre del caso.                                  |

Las modalidades tienen esquemas excluyentes. Una petición no puede introducir conceptos o tratamientos arbitrarios.

Las respuestas utilizan 200, 201 o 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 o 503, según
estructura, permisos, conflicto, impedimento o disponibilidad.

## 17. Eventos

El sobre común incluye identidad, esquema, emisor, agregado, versión, actuación, operación, causa, correlación y fechas.

| Contrato  | Evento                                          | Contenido                                                    |
|-----------|-------------------------------------------------|--------------------------------------------------------------|
| CI-06-038 | CreditDecisionChanged                           | Decisión, condiciones, vigencia, exposición y aplicación.    |
| CI-06-039 | CollateralChanged                               | Valuación, cobertura, afectación, constitución o liberación. |
| CI-06-040 | FinancingConstitutionOrCapacityChanged          | Constitución, desembolso, límite y referencias monetarias.   |
| CI-06-041 | FinancingDebtOrApplicationChanged               | Conceptos, variaciones, pagos, ajustes y posición.           |
| CI-06-042 | FinancingDelinquencyOrPortfolioTreatmentChanged | Mora, clasificación, metodología y previsión.                |
| CI-06-043 | CollectionOrRecoveryChanged                     | Caso, acuerdo, recupero y aplicación comprobada.             |

Los eventos distinguen devengar, exigir, cobrar y cancelar.

Un desembolso referencia el dinero ya ejecutado. Una aplicación conserva el ingreso original. Ninguno origina otro
movimiento por su consumo.

Los temas son:

- banking.credit-assessment.events.

- banking.lending.events.

- banking.collateral.events.

- banking.collection.events.

La clave es el agregado. No se presupone orden global entre servicios.

## 18. Persistencia y recuperación

### 18.1. Estructuras principales

| Servicio   | Persistencia                                                                                                  |
|------------|---------------------------------------------------------------------------------------------------------------|
| Evaluación | Expedientes, antecedentes, análisis, decisiones, condiciones, exposición y aplicaciones.                      |
| Préstamos  | Financiaciones, condiciones, cronogramas, entregas, compromisos, obligaciones, pagos, imputaciones y ajustes. |
| Garantías  | Respaldos, valuaciones, cargas, afectaciones, constituciones y ejecuciones.                                   |
| Cobranzas  | Casos, asignaciones, contactos, propuestas, acuerdos y recuperaciones.                                        |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

### 18.2. Integridad

Se exige:

- Constitución única por referencia de origen.

- Aplicación de aprobación única por constitución.

- Desembolso único por entrega.

- Efecto económico único.

- Obligación única por concepto y ocurrencia.

- Ingreso reconocido una sola vez.

- Imputaciones dentro del importe comprobado.

- Variación de descubierto única por hecho de Fondos.

- Afectaciones de garantía dentro de cobertura.

- Recuperación sin doble aplicación.

- Previsión diferenciada respecto de lo registrado.

### 18.3. Transacciones

Se confirman conjuntamente agregado, historial, idempotencia, resultado y outbox.

Las llamadas remotas se realizan fuera de la transacción.

Los procesos conservan intención antes de invocar y recuperan por la referencia original.

Una respuesta perdida no demuestra ausencia de desembolso o cobro.

Los consumidores registran inbox y efecto local antes de confirmar el offset.

## 19. Seguridad y observabilidad

Keycloak autentica actores y servicios.

Los permisos separan preparación, aprobación, desembolso, imputación, corrección, afectación de garantías, quita y
recuperación.

Las decisiones humanas y ajustes manuales requieren preparación y aprobación separadas.

Los permisos técnicos se complementan con facultad institucional, representación y alcance sobre el objeto.

Redis no administra deuda, capacidad, exposición, garantías ni idempotencia. PostgreSQL conserva la autoridad
transaccional.

Las evidencias binarias permanecen en Documentos. MongoDB no es necesario para estas responsabilidades.

Se observan especialmente:

- Aprobaciones con exposición pendiente.

- Aplicaciones sin constitución resuelta.

- Desembolsos inciertos.

- Ingresos sin imputar.

- Discontinuidades de Fondos.

- Tasas provisionales.

- Garantías vencidas.

- Diferencias de deuda.

- Recuperaciones sin aplicación.

- Procesos de cierre pendientes.

Los logs no incluyen documentación personal ni contenido financiero completo.

## 20. Pruebas y correspondencia funcional

Se prueban cálculos contra resultados independientes, invariantes de capital, concurrencia real en PostgreSQL, contratos
HTTP y eventos, y recuperación ante respuestas perdidas.

Los casos incluyen tasa cero, períodos irregulares, años bisiestos, residuos de cuotas, cargos retenidos, pagos
parciales, fechas efectivas, doble cobro y cobros concurrentes del cliente y del cheque.

| Operación                         | Implementación                          | Contratos             |
|-----------------------------------|-----------------------------------------|-----------------------|
| OF-06-01. Preparar evaluación     | Expediente y antecedentes.              | CI-06-001 y 005.      |
| OF-06-02. Capacidad y exposición  | Análisis y escenarios.                  | CI-06-002.            |
| OF-06-03. Decisión                | Aprobación y exposición confirmada.     | CI-06-003 y 038.      |
| OF-06-04. Revisar aprobación      | Vigencia, retiro y aplicación.          | CI-06-004, 005 y 006. |
| OF-06-05. Evaluar garantías       | Valuación y admisibilidad.              | CI-06-007 y 010.      |
| OF-06-06. Constituir garantías    | Perfeccionamiento y afectación.         | CI-06-008 y 039.      |
| OF-06-07. Sustituir o liberar     | Cobertura y coordinación.               | CI-06-009 y 010.      |
| OF-06-08. Constituir financiación | Producto y exposición vinculada.        | CI-06-006, 011 y 040. |
| OF-06-09. Cálculos y CFT          | Determinación reproducible.             | CI-06-012.            |
| OF-06-10. Desembolso              | Entrega y capital comprobado.           | CI-06-013 y 040.      |
| OF-06-11. Límites y usos          | Compromisos y descubierto.              | CI-06-014, 015 y 016. |
| OF-06-12. Devengamiento           | Conceptos y exigibilidad.               | CI-06-017 y 041.      |
| OF-06-13. Pagos                   | Preparación e imputación.               | CI-06-019, 020 y 021. |
| OF-06-14. Precancelación          | Liquidación y ejecución.                | CI-06-022 y 023.      |
| OF-06-15. Mora                    | Vencimientos y pagos efectivos.         | CI-06-024 y 042.      |
| OF-06-16. Refinanciación          | Sustitución y tratamiento de conceptos. | CI-06-025 y 041.      |
| OF-06-17. Previsiones             | Requerimiento y variación.              | CI-06-026 y 042.      |
| OF-06-18. Cobranza                | Casos y acuerdos.                       | CI-06-033, 034 y 037. |
| OF-06-19. Recuperaciones          | Ejecución y aplicación.                 | CI-06-035, 036 y 043. |
| OF-06-20. Correcciones            | Ajustes y conciliación.                 | CI-06-027 y 032.      |
| OF-06-21. Cierre                  | Capacidad, deuda y pendientes.          | CI-06-018, 028 y 032. |
| OF-06-22. Descuento               | Cesión, anticipo y cobro.               | CI-06-029, 030 y 031. |
