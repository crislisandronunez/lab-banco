# Operaciones funcionales — Módulo 6. Financiación

## 1. Propósito y alcance

Este documento define las operaciones para evaluar, conceder y administrar préstamos, adelantos, líneas de crédito y
descuentos de cheques, incluyendo garantías, desembolsos, intereses, pagos, mora y recuperación.

Comprende los microservicios Evaluación crediticia, Préstamos y líneas, Garantías, y Cobranzas y recuperaciones.

La aprobación, la contratación, la constitución de garantías, el desembolso y el reconocimiento de deuda son hechos
diferentes.

La deuda de tarjetas pertenece a Cuenta de tarjeta. Este módulo participa en su evaluación y cobranza sin mantener otra
deuda por los mismos consumos.

Fondos administra los movimientos de cuentas y la utilización inmediata del descubierto. Cheques y ECHEQ administra los
instrumentos utilizados en descuentos. Riesgos controla políticas, exposición y límites compartidos.

## 2. Catálogo de operaciones

| Identificador | Operación                                                  | Responsable                            |
|---------------|------------------------------------------------------------|----------------------------------------|
| OF-06-01      | Preparar una evaluación crediticia                         | Evaluación crediticia                  |
| OF-06-02      | Evaluar capacidad de pago y exposición                     | Evaluación crediticia                  |
| OF-06-03      | Aprobar, condicionar o rechazar financiación               | Evaluación crediticia                  |
| OF-06-04      | Revisar, vencer o retirar una aprobación                   | Evaluación crediticia                  |
| OF-06-05      | Registrar y evaluar garantías                              | Garantías                              |
| OF-06-06      | Constituir y afectar garantías                             | Garantías                              |
| OF-06-07      | Revisar, sustituir y liberar garantías                     | Garantías                              |
| OF-06-08      | Constituir un préstamo o una línea                         | Préstamos y líneas                     |
| OF-06-09      | Calcular condiciones, cronogramas y costo financiero total | Préstamos y líneas                     |
| OF-06-10      | Desembolsar financiación                                   | Préstamos y líneas                     |
| OF-06-11      | Administrar límites y utilizaciones                        | Préstamos y líneas                     |
| OF-06-12      | Devengar y liquidar conceptos económicos                   | Préstamos y líneas                     |
| OF-06-13      | Recibir e imputar pagos                                    | Préstamos y líneas                     |
| OF-06-14      | Procesar precancelaciones                                  | Préstamos y líneas                     |
| OF-06-15      | Determinar mora y obligaciones vencidas                    | Préstamos y líneas                     |
| OF-06-16      | Modificar, reestructurar y refinanciar                     | Préstamos y líneas                     |
| OF-06-17      | Determinar previsiones y tratamientos de cartera           | Préstamos y líneas                     |
| OF-06-18      | Gestionar cobranza y acuerdos                              | Cobranzas y recuperaciones             |
| OF-06-19      | Ejecutar garantías y registrar recuperaciones              | Cobranzas y recuperaciones y Garantías |
| OF-06-20      | Corregir obligaciones e imputaciones                       | Préstamos y líneas                     |
| OF-06-21      | Cancelar financiación y cerrar líneas                      | Préstamos y líneas                     |
| OF-06-22      | Descontar cheques y aplicar su cobro                       | Préstamos y líneas                     |

## 3. Reglas comunes del módulo

### 3.1. Autoridad sobre las decisiones

| Información o decisión                                    | Responsable                |
|-----------------------------------------------------------|----------------------------|
| Decisión crediticia individual                            | Evaluación crediticia      |
| Políticas, límites compartidos y exposición consolidada   | Riesgos                    |
| Acuerdo y condiciones aceptadas                           | Contratos                  |
| Capital, intereses, cargos y pagos de financiación        | Préstamos y líneas         |
| Respaldos y cobertura                                     | Garantías                  |
| Gestión de recupero                                       | Cobranzas y recuperaciones |
| Dinero de cuentas y utilización inmediata del descubierto | Fondos                     |
| Legitimación, circulación y cobro de cheques              | Cheques y ECHEQ            |
| Recursos propios y liquidaciones externas                 | Tesorería                  |
| Representación contable                                   | Contabilidad               |

Una decisión favorable no sustituye las demás condiciones necesarias.

### 3.2. Capital y compromisos

El capital adeudado surge de desembolsos o utilizaciones comprobados.

Aprobar o constituir una línea no genera deuda por todo su límite.

Se distinguen:

- Límite concedido.

- Capital utilizado.

- Compromisos de utilización.

- Capacidad disponible.

- Intereses devengados y exigibles.

- Cargos y tributos.

- Importes pagados.

- Excesos.

Un desembolso puede incluir importes aplicados por cuenta del cliente a conceptos expresamente aceptados. Se informan
capital bruto, conceptos retenidos y entrega neta.

Las retenciones no se presentan como dinero recibido libremente por el cliente ni se contabilizan nuevamente como otro
desembolso.

### 3.3. Modalidades comprendidas

La simulación admite:

- Préstamos amortizables por sistema francés o alemán.

- Préstamos con capital al vencimiento.

- Líneas renovables y no renovables.

- Descubierto de cuenta corriente.

- Descuento de cheques con responsabilidad del cliente financiado.

Las tasas, límites, importes, plazos y cargos son configurables dentro de los comportamientos definidos.

Las garantías permiten diferenciar préstamos personales, comerciales, prendarios e hipotecarios sin alterar la autoridad
sobre la deuda.

### 3.4. Configuración necesaria

Antes de emitir una propuesta deben conocerse:

- Moneda y capital.

- Modalidad de financiación.

- Tasa y mecanismo de revisión.

- Sistema de amortización.

- Fechas y calendario.

- Desembolsos.

- Cargos, tributos y bonificaciones.

- Garantías.

- Gracia, si existe.

- Imputación.

- Precancelación.

- Mora.

- Renovación y terminación.

Un valor ausente no se interpreta como cero.

### 3.5. Precisión y redondeo

Los cálculos intermedios conservan al menos doce decimales.

Los importes exigibles se redondean a la unidad mínima de la moneda, al más cercano, elevando los empates positivos.

No se redondea diariamente el devengamiento antes de acumularlo.

En cronogramas:

- Se redondean los conceptos exigibles de cada cuota.

- La última amortización absorbe el residuo de capital.

- La suma de amortizaciones coincide con el capital financiado.

- No se crea capital adicional para resolver redondeos.

Las reversiones utilizan los importes registrados.

### 3.6. Fechas y vencimientos

La referencia temporal es America/Argentina/Buenos_Aires.

Los cronogramas mensuales conservan un anclaje original. El ajuste de una cuota no desplaza las siguientes.

El pago de cuotas utiliza hábil siguiente.

El desplazamiento por calendario no genera por sí solo mora ni interés adicional. Los períodos económicos previstos
utilizan las fechas contractuales.

Un desembolso en otra fecha exige recalcular y obtener aceptación de las condiciones afectadas antes de ejecutarse.

### 3.7. Capitalización y mora

En los productos ordinarios:

- No se capitalizan automáticamente intereses.

- La gracia no convierte intereses en capital.

- No se generan intereses sobre intereses, comisiones o tributos.

- No se cobra una penalidad fija por el solo atraso.

- No se agrega una tasa punitoria.

- El capital impago continúa generando remuneración ordinaria según su modalidad.

- Una refinanciación identifica separadamente capital, intereses, cargos y condonaciones.

### 3.8. Recuperación

Cada aprobación, desembolso, utilización, cálculo, pago y ajuste conserva referencia única.

La repetición no duplica deuda, cobros ni exposición.

Una entrega o cobro incierto se consulta antes de repetirlo o liberar su cobertura.

Los hechos confirmados se corrigen mediante actuaciones vinculadas.

## 4. OF-06-01. Preparar una evaluación crediticia

Propósito: reunir antecedentes para analizar financiación.

Responsable: Evaluación crediticia.

### Información y desarrollo

Solicitante, producto, importe, moneda, plazo, finalidad, obligados y garantías ofrecidas.

- Identificar petición.

- Recuperar evaluaciones relacionadas.

- Determinar requisitos.

- Obtener ingresos, actividad y obligaciones.

- Consultar fuentes simuladas.

- Comprobar vigencia y suficiencia.

- Registrar observaciones.

### Reglas

- Se distinguen ingresos declarados y comprobados.

- Los antecedentes conservan fuente y fecha.

- Las obligaciones se consultan en sus módulos acreedores.

- Los plazos de subsanación corresponden al trámite de contratación.

- Una garantía ofrecida no se considera constituida.

- Una fuente sin respuesta no demuestra ausencia de deuda.

### Criterios de aceptación

| Situación                              | Resultado esperado                      |
|----------------------------------------|-----------------------------------------|
| Ingreso declarado sin respaldo exigido | Pendiente de comprobación.              |
| Consulta externa sin respuesta         | No se presume ausencia de obligaciones. |
| Petición repetida                      | Misma evaluación.                       |
| Existe otro obligado                   | Se incorpora al análisis.               |

## 5. OF-06-02. Evaluar capacidad de pago y exposición

Propósito: determinar compatibilidad con la devolución y los límites.

Responsable: Evaluación crediticia.

### Desarrollo

- Determinar ingresos o flujos disponibles.

- Identificar obligaciones.

- Calcular servicio de deuda propuesto.

- Aplicar escenarios exigidos.

- Consultar exposición.

- Analizar cobertura.

- Elaborar conclusión.

### Reglas

- Riesgos administra metodologías y umbrales.

- Las garantías complementan la capacidad de pago.

- Las tasas variables se evalúan también bajo un escenario adverso configurado.

- Consultar exposición no compromete capacidad.

- Los parámetros obligatorios deben estar completos.

- En descuentos se consideran el cliente financiado y el riesgo del instrumento y su librador, sin duplicar la misma
  exposición.

### Criterios de aceptación

| Situación                                         | Resultado esperado                               |
|---------------------------------------------------|--------------------------------------------------|
| Ingreso disponible 1.000 y servicio propuesto 400 | Se compara con la política vigente.              |
| Garantía suficiente y flujo insuficiente          | Se conserva el impedimento de capacidad de pago. |
| Otro crédito aprobado pendiente                   | Se considera su compromiso.                      |
| Falta escenario requerido                         | No se completa el análisis.                      |

## 6. OF-06-03. Aprobar, condicionar o rechazar financiación

Propósito: adoptar una decisión utilizable.

Responsable: Evaluación crediticia.

### Desarrollo

- Preparar conclusión.

- Comprobar facultades.

- Determinar condiciones.

- Solicitar afectación a Riesgos.

- Obtener confirmación.

- Registrar decisión y vigencia.

- Comunicar.

### Reglas

- Las decisiones humanas requieren preparación y aprobación separadas.

- Las automáticas aplican una política aprobada.

- La aprobación puede utilizarse durante un máximo de treinta días corridos, limitada por sus antecedentes.

- Las condiciones distinguen requisitos anteriores a contratar, constituir y desembolsar.

- Una aprobación no queda utilizable sin afectación de exposición cuando corresponda.

- Las excepciones no levantan prohibiciones.

### Criterios de aceptación

| Situación                                                           | Resultado esperado              |
|---------------------------------------------------------------------|---------------------------------|
| Dos aprobaciones compiten por capacidad 100 y solicitan 80 cada una | Riesgos impide confirmar ambas. |
| Falta garantía previa al desembolso                                 | No se entrega dinero.           |
| Aprobación vencida sin utilizar                                     | Requiere revisión.              |
| Comunicación repetida                                               | No duplica exposición.          |

## 7. OF-06-04. Revisar, vencer o retirar una aprobación

Propósito: mantener decisiones y compromisos consistentes.

Responsable: Evaluación crediticia.

### Desarrollo

- Identificar causa.

- Comprobar contratación y utilización.

- Revisar cambios.

- Resolver continuidad, sustitución o retiro.

- Coordinar exposición.

- Comunicar.

### Reglas

- Cambiar capital, moneda, plazo u obligados exige revisión.

- El retiro no elimina contratos celebrados.

- La constitución incierta impide liberar exposición.

- La sustitución evita comprometer dos veces el mismo importe.

- Las entregas posteriores de una línea constituida utilizan su acuerdo vigente.

### Criterios de aceptación

| Situación                                         | Resultado esperado                                             |
|---------------------------------------------------|----------------------------------------------------------------|
| Aprobación vence sin contratación ni constitución | Se regulariza su compromiso.                                   |
| Producto ya constituido                           | No se elimina su exposición por vencer la aprobación original. |
| Aumento de importe                                | Se revisa y afecta la diferencia.                              |
| Constitución sin resultado conocido               | Investigación antes de liberar.                                |

## 8. OF-06-05. Registrar y evaluar garantías

Propósito: determinar características y cobertura de un respaldo.

Responsable: Garantías.

### Desarrollo

- Identificar bien, derecho o garante.

- Comprobar titularidad y facultades.

- Obtener valuación.

- Identificar cargas y prioridades.

- Aplicar criterios de cobertura.

- Informar resultado.

### Reglas

- Valor económico y cobertura admitida son distintos.

- Las valuaciones tienen fuente y vigencia.

- Las cargas desconocidas no se presumen inexistentes.

- Las afectaciones pendientes consumen cobertura.

- Las garantías compartidas conservan distribución y prioridad.

- Riesgos define admisibilidad y porcentajes de cobertura.

### Criterios de aceptación

| Situación                           | Resultado esperado             |
|-------------------------------------|--------------------------------|
| Valor 100 y cobertura admitida 70 % | Cobertura inicial 70.          |
| De esa cobertura hay 40 afectados   | No se ofrecen otros 70 libres. |
| Valuación vencida                   | Actualización requerida.       |
| Titularidad no comprobada           | No se confirma admisibilidad.  |

## 9. OF-06-06. Constituir y afectar garantías

Propósito: confirmar el respaldo formalizado.

Responsable: Garantías.

### Desarrollo

- Comprobar acuerdo.

- Verificar actuaciones constitutivas simuladas.

- Registrar prioridades.

- Afectar cobertura.

- Confirmar al producto.

- Conservar evidencia.

### Reglas

- Contratos conserva el acuerdo.

- Garantías conserva situación y cobertura.

- No se informa constitución sin evidencia.

- Las afectaciones concurrentes no comprometen dos veces la misma cobertura.

- La constitución no desembolsa automáticamente.

### Criterios de aceptación

| Situación                                    | Resultado esperado                           |
|----------------------------------------------|----------------------------------------------|
| Acuerdo sin actuación constitutiva exigida   | No se informa perfeccionamiento.             |
| Dos créditos compiten por la misma cobertura | No se asigna íntegramente a ambos.           |
| Garantía constituida                         | Continúan los demás controles de desembolso. |

## 10. OF-06-07. Revisar, sustituir y liberar garantías

Propósito: mantener cobertura y resolver respaldos.

Responsable: Garantías.

### Desarrollo

- Detectar causa.

- Actualizar antecedentes.

- Consultar obligaciones.

- Determinar refuerzo, sustitución o liberación.

- Obtener aprobación.

- Ejecutar y confirmar.

### Reglas

- Se consultan todas las obligaciones cubiertas.

- El respaldo anterior se mantiene hasta confirmar el sustituto.

- Aceptar menor cobertura requiere decisión crediticia y de riesgo.

- La pérdida de valor no aumenta capital.

- Los seguros exigidos mantienen seguimiento.

- La liberación documental pendiente no se informa completada.

### Criterios de aceptación

| Situación                                     | Resultado esperado                      |
|-----------------------------------------------|-----------------------------------------|
| Garantía cubre dos préstamos y se cancela uno | Se mantiene lo necesario para el otro.  |
| Falla el sustituto                            | No se libera el respaldo anterior.      |
| Disminuye valuación                           | Revisión de cobertura, sin nueva deuda. |

## 11. OF-06-08. Constituir un préstamo o una línea

Propósito: registrar la financiación contratada.

Responsable: Préstamos y líneas.

### Desarrollo

- Comprobar contrato y aprobación.

- Asignar identificación.

- Registrar condiciones.

- Vincular garantías.

- Identificar requisitos pendientes.

- Transformar exposición de aprobación en exposición constituida.

- Confirmar.

### Reglas

- El capital utilizado comienza en cero hasta el desembolso.

- Los cargos se mantienen separados.

- La línea identifica si es renovable.

- El descubierto identifica cuenta y circuito exclusivo de Fondos.

- La repetición no crea otra financiación.

### Criterios de aceptación

| Situación                     | Resultado esperado              |
|-------------------------------|---------------------------------|
| Línea 1.000 sin usos          | Límite 1.000 y capital cero.    |
| Misma instrucción repetida    | Misma financiación.             |
| Moneda diferente del contrato | No se completa la constitución. |
| Transformación del compromiso | No se duplica exposición.       |

## 12. OF-06-09. Calcular condiciones, cronogramas y costo financiero total

Propósito: determinar obligaciones y costo de la financiación.

Responsable: Préstamos y líneas.

### 12.1. Tasas

Se admiten:

- Tasa nominal anual.

- Tasa efectiva anual.

- Tasa periódica efectiva.

- Tasa variable como referencia más margen.

La nominal anual identifica su modalidad: conversión periódica o aplicación simple Actual/365.

Para frecuencia anual $m$:

$i = \frac{j}{m}$

Para tasa efectiva anual $e$:

$i = (1 + e)^{1/m} - 1$

La frecuencia mensual utiliza $m = 12$.

No se divide una tasa efectiva anual como si fuera nominal.

### 12.2. Sistema francés

Con capital $P$, tasa periódica $i$ y cantidad de cuotas $n$:

$C = P\frac{i}{1 - (1 + i)^{- n}}$

Con tasa cero:

$C = \frac{P}{n}$

Cada cuota distingue interés y amortización. La última ajusta el residuo de capital.

### 12.3. Sistema alemán

La amortización teórica es:

$A = \frac{P}{n}$

El interés se calcula sobre el capital pendiente al inicio del período.

La cuota reúne amortización, interés y conceptos adicionales exigibles.

### 12.4. Capital al vencimiento

El principal se devuelve en la fecha final.

Los intereses pueden pagarse periódicamente o al vencimiento.

Se utiliza interés simple Actual/365 con tasa nominal anual. Si la oferta parte de una tasa efectiva, debe convertirla y
mostrar la tasa nominal simple adoptada antes de contratar.

El diferimiento del pago no capitaliza intereses.

### 12.5. Períodos irregulares

En préstamos amortizables:

$i_{irregular} = (1 + i)^{d/D} - 1$

Donde:

- $i$: tasa del período regular.

- $d$: días reales del intervalo.

- $D$: días reales del período regular de referencia.

Un mes contractual completo utiliza la tasa mensual, aunque tenga 28, 30 o 31 días.

La diferencia de interés del primer período irregular se identifica en la primera cuota. No modifica silenciosamente las
siguientes.

### 12.6. Líneas y descubierto

Se aplica interés simple diario:

$I = P_{d}\frac{j_{d}}{365}$

El capital diario es el utilizado al finalizar cada día. Las acreditaciones confirmadas reducen la base desde su fecha
efectiva.

Se cuentan los días reales y se mantiene divisor 365 en años bisiestos.

### 12.7. Gracia

Se admite gracia de capital:

- El capital permanece pendiente.

- Los intereses se calculan sobre ese capital.

- Se pagan en las fechas pactadas.

También puede diferirse el pago de intereses. El contrato debe asignarles vencimientos explícitos.

Al terminar la gracia de capital, el cronograma amortiza el capital pendiente mediante el sistema elegido y la cantidad
de cuotas acordada.

Los intereses diferidos permanecen separados y no generan interés compuesto.

### 12.8. Tasa variable

La referencia, margen, fechas de revisión y fuente deben estar identificados.

Si falta una referencia:

- Se conserva provisionalmente la última tasa válida.

- Se informa la incidencia.

- La continuidad provisional se limita a un período de revisión.

- Antes de liquidar definitivamente otro período sin referencia se requiere resolución contractual.

La indisponibilidad no genera mora por diferencias todavía no determinadas.

### 12.9. Costo financiero total

El CFT expresa el costo anual efectivo de los flujos previstos de la financiación.

Se calcula desde la perspectiva del cliente:

- Los desembolsos netos son importes recibidos.

- Las cuotas y demás pagos obligatorios son importes entregados.

- Los conceptos retenidos al desembolsar reducen la entrega neta.

- Los cargos ya descontados de esa entrega no se agregan otra vez como pagos separados.

Se incluyen intereses, comisiones, seguros obligatorios del producto y tributos directamente asociados que deba afrontar
el cliente.

No se incluyen penalidades hipotéticas, gastos voluntarios ni servicios independientes opcionales.

Para fechas reales se determina $r$ de modo que:

$\frac{D_{k}}{(1 + r)^{a_{k}/365}} = \frac{Q_{l}}{(1 + r)^{b_{l}/365}}$

Donde:

- $D_{k}$: desembolso neto recibido.

- $Q_{l}$: pago obligatorio.

- $a_{k}$ y $b_{l}$: días desde el primer desembolso.

- $r$: tasa anual efectiva del costo.

El resultado se presenta como porcentaje anual con dos decimales, conservando internamente la precisión del cálculo.

### Reglas del CFT

- Se utilizan las fechas efectivas previstas de entrega y pago.

- El cronograma incluye los importes monetarios redondeados que realmente se exigirán.

- La tasa nominal no se presenta como CFT.

- Las tasas variables utilizan la referencia disponible al preparar la propuesta, identificando el supuesto de
  continuidad.

- Un cambio material de cargos, fechas o desembolsos obliga a recalcular.

- Un tributo o cargo obligatorio desconocido impide presentar un CFT definitivo.

- Una línea sin utilizaciones futuras determinadas informa sus tasas y cargos; el CFT se calcula para cada utilización
  concreta.

- Un ejemplo representativo de línea se identifica como ejemplo, sin prometer ese costo para cualquier uso.

- Si los flujos no permiten una tasa única, se informa el impedimento y no se publica un resultado arbitrario.

### Desarrollo

- Comprobar entradas y configuración.

- Convertir tasas.

- Generar fechas.

- Calcular capital e intereses.

- Incorporar cargos y tributos.

- Determinar entregas netas.

- Calcular CFT.

- Comprobar capital, cuotas y totales.

- Conservar versión y entregar a Contratos.

### Criterios de aceptación

| Situación                                                      | Resultado esperado                                               |
|----------------------------------------------------------------|------------------------------------------------------------------|
| Capital 1.200, doce cuotas y tasa cero                         | Amortizaciones de 100.                                           |
| Alemán: capital 1.000, dos cuotas y tasa 10 %                  | Cuotas financieras 600 y 550.                                    |
| Francés: capital 1.000, dos cuotas y tasa 10 %                 | Cuota teórica aproximada 576,190476.                             |
| Tasa efectiva anual 21 % y frecuencia semestral                | Tasa semestral 10 %.                                             |
| Línea 1.000 durante diez días al 36,5 % nominal                | Interés 10.                                                      |
| Desembolso neto 1.000 y pago único 1.100 a 365 días            | CFT 10 %.                                                        |
| Capital bruto 1.000, cargo retenido 20 y pago 1.100 a 365 días | CFT calculado sobre recepción neta 980, aproximadamente 12,24 %. |
| Se omite un cargo obligatorio                                  | No se informa CFT definitivo.                                    |

## 13. OF-06-10. Desembolsar financiación

Propósito: poner fondos a disposición según el acuerdo.

Responsable: Préstamos y líneas.

### Desarrollo

- Comprobar condiciones y límites.

- Verificar garantías.

- Identificar entrega.

- Confirmar destino y conceptos retenidos.

- Coordinar con Fondos o Tesorería.

- Comprobar resultado.

- Reconocer capital y actualizar exposición.

### Reglas

- El destino puede ser el prestatario o un tercero autorizado.

- Los desembolsos parciales tienen referencias propias.

- Capital bruto, retenciones y neto se muestran separados.

- El cambio de fecha que afecte condiciones exige aceptación previa.

- Una entrega incierta no se repite.

- Un rechazo sin entrega no genera capital utilizado.

- Una retención solo cancela un concepto cuando su aplicación queda confirmada.

### Criterios de aceptación

| Situación                             | Resultado esperado                |
|---------------------------------------|-----------------------------------|
| Acreditación comprobada 500           | Capital utilizado 500.            |
| Entrega rechazada antes de ejecutar   | Sin capital por esa entrega.      |
| Respuesta perdida                     | Consulta de la entrega original.  |
| Capital bruto 500 y cargo retenido 10 | Capital 500, cargo 10 y neto 490. |

## 14. OF-06-11. Administrar límites y utilizaciones

Propósito: controlar capacidad crediticia y cambios.

Responsable: Préstamos y líneas.

### Desarrollo

- Registrar límite.

- Comprobar aprobación y exposición.

- Aceptar usos o comunicar capacidad a Fondos.

- Registrar reducciones de capital.

- Actualizar capacidad.

- Conciliar y tratar vencimientos.

### Reglas

- La línea renovable restituye capacidad por capital devuelto.

- La no renovable no habilita nuevas entregas por pagos.

- Reducir el límite no elimina deuda.

- En descubierto se utiliza exclusivamente el control de Fondos.

- Las reservas firmes conservan cobertura protegida.

- Las inciertas conservan seguimiento.

- Los pagos de intereses no se presentan como reducción de capital.

### Criterios de aceptación

| Situación                                                     | Resultado esperado                                  |
|---------------------------------------------------------------|-----------------------------------------------------|
| Línea renovable 100, uso 60 y devolución de capital 20        | Utilizado 40 y capacidad 60, sin otros compromisos. |
| Mismo caso en no renovable                                    | El pago no habilita otra entrega.                   |
| Descubierto usado 20, reserva firme 70 y límite reducido a 40 | Cobertura adicional protegida 50.                   |
| Consumo de esa reserva                                        | Capital 90 y exceso 50.                             |

## 15. OF-06-12. Devengar y liquidar conceptos económicos

Propósito: reconocer y hacer exigibles conceptos.

Responsable: Préstamos y líneas.

### Desarrollo

- Obtener posiciones.

- Separar tramos de capital y tasa.

- Calcular devengamiento.

- Determinar liquidación.

- Aplicar beneficios y tributos.

- Registrar conceptos.

- Comunicar a Contabilidad.

### Reglas

- Se calcula sobre capital efectivamente pendiente.

- Los pagos reducen la base desde su fecha efectiva.

- El interés previsto y el correspondiente al atraso no cubren dos veces los mismos días.

- En cuotas amortizables, el capital vencido utiliza remuneración simple con tasa diaria equivalente a la efectiva anual
  contractual.

- Líneas y descubierto mantienen Actual/365.

- Los intereses impagos no aumentan el capital.

- Las comisiones requieren hechos identificados.

- Los datos provisionales impiden liquidación definitiva.

- En descuentos, el interés retenido se devenga durante el plazo conforme a OF-06-22.

### Criterios de aceptación

| Situación                                      | Resultado esperado                    |
|------------------------------------------------|---------------------------------------|
| Capital baja de 1.000 a 600 durante el período | Se distinguen los tramos.             |
| Interés devengado 10 sin pagar                 | Deuda de interés, no cobro ficticio.  |
| Liquidación repetida                           | Un concepto.                          |
| Interés pendiente                              | No se incorpora a la base de capital. |

## 16. OF-06-13. Recibir e imputar pagos

Propósito: aplicar fondos comprobados a obligaciones.

Responsable: Préstamos y líneas.

### Desarrollo

- Identificar pago y financiación.

- Comprobar ingreso o cobro.

- Determinar obligaciones.

- Aplicar imputación.

- Actualizar deuda y mora.

- Tratar excedentes.

- Comunicar.

### Imputación ordinaria

Para préstamos y utilizaciones distintas de la reducción automática del descubierto:

- Obligaciones vencidas, desde la más antigua.

- Obligaciones exigibles del día.

- Dentro de cada obligación, intereses ordinarios y después capital.

- Tributos asociados según su concepto.

- Comisiones y gastos después de esas obligaciones.

El pagador puede identificar la financiación. Si no puede determinarse su destino, el importe permanece pendiente sin
aplicarse arbitrariamente.

### Descubierto

Una acreditación ordinaria reduce directamente el capital utilizado según el saldo resultante informado por Fondos.

Esa reducción no se redistribuye mediante la imputación ordinaria anterior.

Los intereses y cargos se cobran por una actuación separada:

- Préstamos y líneas identifica conceptos e importe.

- Fondos utiliza fondos propios disponibles.

- No se genera nuevo descubierto para cobrarlos.

- Un cobro parcial requiere instrucción concreta.

- La confirmación cancela únicamente los conceptos informados.

Si un pago recibido debe cancelar descubierto, su componente de capital se acredita en la cuenta deudora. La reducción
confirmada por Fondos es la única cancelación de ese capital; no se registra otra adicional.

### Otras reglas

- La demora interna no genera mora adicional.

- Un pago parcial cancela solo conceptos imputados.

- Los excedentes requieren instrucción de precancelación o devolución.

- Los pagos de terceros identifican pagador y deudor.

- Un mismo ingreso no cancela dos veces.

### Criterios de aceptación

| Situación                                                                  | Resultado esperado                           |
|----------------------------------------------------------------------------|----------------------------------------------|
| Interés 10, capital exigible 90 y pago 40                                  | Interés cancelado 10 y capital cancelado 30. |
| Pago 120 sobre obligación 100                                              | Aplicación 100 y excedente 20.               |
| Descubierto: capital 100, interés pendiente 10 y acreditación ordinaria 50 | Capital 50 e interés pendiente 10.           |
| Saldo propio disponible 20 y cobro de interés autorizado 10                | Débito 10 y cancelación de interés 10.       |
| Saldo cero e interés pendiente 10                                          | Sin nuevo descubierto para cobrarlo.         |
| Pago oportuno imputado después                                             | Sin mora por demora interna.                 |

## 17. OF-06-14. Procesar precancelaciones

Propósito: aplicar devolución anticipada de capital.

Responsable: Préstamos y líneas.

### Desarrollo

- Recibir instrucción.

- Determinar fecha.

- Calcular capital y conceptos devengados.

- Presentar liquidación.

- Obtener aceptación.

- Confirmar fondos.

- Aplicar y recalcular.

### Reglas

- No se cobra comisión de precancelación en las modalidades ordinarias.

- La liquidación vale hasta finalizar la fecha indicada.

- El pago posterior exige actualizar utilización efectiva.

- La precancelación parcial permite reducir plazo o cuota.

- Sin selección no se modifica definitivamente el cronograma.

- El capital cancelado deja de generar interés.

- No se cobran intereses futuros no devengados.

- Los descuentos aplican además su tratamiento de interés retenido.

- El arrepentimiento utiliza las reglas de Contratos.

### Criterios de aceptación

| Situación                                    | Resultado esperado              |
|----------------------------------------------|---------------------------------|
| Capital 1.000 y anticipo neto de capital 300 | Capital restante 700.           |
| No se eligió reducir plazo o cuota           | Se solicita elección.           |
| Pago posterior a la liquidación              | Se actualiza el importe.        |
| Cancelación total                            | No se cobran intereses futuros. |

## 18. OF-06-15. Determinar mora y obligaciones vencidas

Propósito: identificar incumplimientos.

Responsable: Préstamos y líneas.

### Desarrollo

- Obtener vencimientos efectivos.

- Comprobar pagos.

- Resolver ingresos pendientes conocidos.

- Determinar impago.

- Calcular antigüedad.

- Informar a Riesgos y Cobranzas.

### Reglas

- La mora comienza el día siguiente al vencimiento ajustado.

- No existe gracia de pago implícita.

- La gracia de capital es diferente.

- Un pago oportuno no produce mora por procesamiento posterior.

- Los reclamos mantienen su tratamiento sin borrar deuda.

- Riesgos determina la clasificación.

- La regularización conserva historia.

### Criterios de aceptación

| Situación                               | Resultado esperado                             |
|-----------------------------------------|------------------------------------------------|
| Vencimiento lunes e impago al finalizar | Mora desde el martes.                          |
| Domingo ajustado a lunes                | No hay mora el lunes por el ajuste.            |
| Cuota 100 y pago oportuno 40            | Impago 60.                                     |
| Ingreso oportuno sin imputar            | Se comprueba antes de atribuir incumplimiento. |

## 19. OF-06-16. Modificar, reestructurar y refinanciar

Propósito: cambiar o sustituir obligaciones por acuerdo.

Responsable: Préstamos y líneas.

### Desarrollo

- Determinar deuda.

- Evaluar capacidad.

- Obtener aprobación y exposición.

- Definir tratamiento de cada concepto.

- Formalizar con Contratos.

- Aplicar modificación o sustitución.

- Conciliar obligaciones y garantías.

### Reglas

- Una propuesta no modifica deuda.

- Capital, intereses, cargos y quitas se distinguen.

- No se capitalizan intereses automáticamente.

- La obligación sustituida deja de exigirse conforme al acuerdo.

- Las quitas condicionadas se aplican al cumplirse sus condiciones.

- Una condonación definitiva no revive por perder otro beneficio.

- El historial permanece.

- Se recalculan cronograma y CFT de la nueva financiación cuando corresponda.

### Criterios de aceptación

| Situación                       | Resultado esperado                                     |
|---------------------------------|--------------------------------------------------------|
| Capital 1.000 e intereses 100   | Tratamientos separados.                                |
| Nuevo acuerdo sustituye capital | No quedan dos capitales exigibles por el mismo origen. |
| Propuesta rechazada             | Continúa el acuerdo anterior.                          |
| Sustitución de garantía         | Se confirma cobertura antes de liberar.                |

## 20. OF-06-17. Determinar previsiones y tratamientos de cartera

Propósito: calcular coberturas contables de riesgo de la cartera administrada.

Responsable: Préstamos y líneas.

### Desarrollo

- Obtener posiciones al corte.

- Recibir clasificación y metodología.

- Comprobar saldos y garantías.

- Calcular importes requeridos.

- Determinar diferencias respecto de lo registrado.

- Informar a Contabilidad.

### Reglas

- Riesgos establece criterios.

- Se conservan versión y entradas.

- Previsionar no paga ni condona deuda.

- La baja contable no extingue el derecho.

- Se comunica la variación, evitando duplicaciones.

- Los descuentos se consideran dentro de su cartera.

- No se calcula aquí una segunda previsión sobre deuda de tarjetas.

### Criterios de aceptación

| Situación                          | Resultado esperado                        |
|------------------------------------|-------------------------------------------|
| Base 100 y previsión 20 %          | Previsión requerida 20.                   |
| Requerimiento pasa de 20 a 25      | Variación 5.                              |
| Crédito dado de baja contablemente | Mantiene recupero si subsiste el derecho. |
| Cálculo repetido                   | Sin gasto duplicado.                      |

## 21. OF-06-18. Gestionar cobranza y acuerdos

Propósito: recuperar obligaciones vencidas.

Responsable: Cobranzas y recuperaciones.

### Desarrollo

- Recibir deuda del acreedor.

- Comprobar pagos pendientes.

- Abrir caso.

- Determinar estrategia.

- Registrar contactos y propuestas.

- Obtener aprobaciones.

- Formalizar acuerdos.

- Seguir cumplimiento.

### Reglas

- Una promesa no reduce deuda.

- El acreedor conserva imputación.

- Los contactos respetan confidencialidad.

- Las quitas requieren aprobación separada.

- No se cobran gastos internos ordinarios como penalidad.

- Los gastos externos requieren evidencia y fundamento.

- Las controversias se consideran antes de acciones incompatibles.

- El cierre del caso no extingue deuda por sí solo.

### Criterios de aceptación

| Situación             | Resultado esperado                      |
|-----------------------|-----------------------------------------|
| Pago aún no imputado  | Se comprueba antes de reclamar.         |
| Promesa de pago       | Deuda sin cancelación.                  |
| Quita condicionada    | Se conserva condición y efecto.         |
| Tarjetas informa pago | Se actualiza la gestión sin otra deuda. |

## 22. OF-06-19. Ejecutar garantías y registrar recuperaciones

Propósito: obtener recupero mediante respaldos.

Responsables: Cobranzas y recuperaciones coordina; Garantías administra el respaldo.

### Desarrollo

- Comprobar incumplimiento.

- Obtener decisiones y formalidades.

- Coordinar actuaciones simuladas.

- Registrar venta, pago o adjudicación.

- Determinar recupero.

- Aplicar mediante el acreedor.

- Tratar excedentes y cobertura restante.

### Reglas

- Tasar no equivale a recuperar.

- Iniciar ejecución no cancela deuda.

- El pago del garante conserva origen y derechos derivados.

- La adjudicación de un bien no se confunde con efectivo.

- Se respetan prioridades.

- El excedente no es ingreso del banco.

- Los gastos descontados tienen fundamento.

- Los cobros del instrumento y del cliente se consideran conjuntamente para impedir recuperación duplicada.

### Criterios de aceptación

| Situación                        | Resultado esperado             |
|----------------------------------|--------------------------------|
| Tasación 100 y venta neta 80     | Recupero 80.                   |
| Deuda 70 y recupero aplicable 80 | Cancelación 70 y excedente 10. |
| Garante paga 30                  | Aplicación única.              |
| Ejecución pendiente              | No se informa deuda cancelada. |

## 23. OF-06-20. Corregir obligaciones e imputaciones

Propósito: regularizar errores conservando historia.

Responsable: Préstamos y líneas.

### Desarrollo

- Identificar error.

- Reconstruir condiciones.

- Calcular diferencias.

- Obtener autorización.

- Registrar ajustes.

- Coordinar movimientos.

- Actualizar mora, exposición y reportes.

### Reglas

- La corrección de imputación no duplica el ingreso.

- Se utilizan fechas pertinentes para eliminar mora artificial.

- Una diferencia contable no crea un pago.

- Los ajustes manuales requieren preparación y aprobación separadas.

- Las correcciones de descubierto se concilian con Fondos.

- Los descuentos se concilian con Cheques y ECHEQ.

- Los documentos afectados se rectifican.

### Criterios de aceptación

| Situación                                   | Resultado esperado                       |
|---------------------------------------------|------------------------------------------|
| Pago 100 aplicado a otra obligación         | Se corrige sin cobrar otros 100.         |
| Error produjo interés 5                     | Se regulariza concepto y eventual cobro. |
| Corrección repetida                         | Sin devolución duplicada.                |
| Reducción de descubierto recibida dos veces | Una sola cancelación de capital.         |

## 24. OF-06-21. Cancelar financiación y cerrar líneas

Propósito: concluir capacidad y obligaciones.

Responsable: Préstamos y líneas.

### Desarrollo

- Registrar causa.

- Impedir nuevos usos.

- Revisar capital y conceptos.

- Confirmar pagos o causas extintivas.

- Resolver compromisos.

- Coordinar exposición y garantías.

- Obtener confirmaciones necesarias.

- Registrar cancelación o administración residual.

### Reglas

- Vencer una línea no cancela deuda.

- La cancelación económica se distingue de trámites documentales.

- Los saldos a favor se restituyen.

- Las reservas firmes o inciertas conservan seguimiento.

- Las garantías compartidas se liberan solo en la parte procedente.

- Los cargos futuros no continúan por demoras internas.

- No se informa libre deuda con conceptos pendientes.

- Un cheque asociado no se libera sin resolver la financiación y su cobro.

### Criterios de aceptación

| Situación                                      | Resultado esperado                            |
|------------------------------------------------|-----------------------------------------------|
| Capital cero e interés 10                      | No se emite libre deuda total.                |
| Deuda pagada y liberación documental pendiente | Se distingue cancelación económica y trámite. |
| Línea vencida con uso 50                       | Capital 50 y nuevos usos impedidos.           |
| Descubierto con reserva incierta               | Continúa cobertura y seguimiento.             |

## 25. OF-06-22. Descontar cheques y aplicar su cobro

Propósito: anticipar fondos mediante un cheque cedido al banco y administrar la financiación hasta su cancelación.

Responsable: Préstamos y líneas.

### 25.1. Modalidad admitida

Se admite descuento de cheques de pago diferido físicos simulados y ECHEQ:

- Nominativos.

- Con legitimación comprobada.

- No pagados.

- Sin presentación incierta.

- Sin compromiso incompatible.

- Con fecha de pago futura.

- Cedidos al banco mediante la modalidad admitida por el instrumento.

El cliente financiado mantiene responsabilidad de cancelar la financiación si el cheque no se cobra.

No se ofrece descuento sin responsabilidad del cliente.

Cada cheque mantiene una financiación identificada. Un trámite con varios instrumentos conserva resultados individuales.

### 25.2. Condiciones económicas

Se identifican:

- Nominal $N$.

- Fecha de anticipo.

- Fecha prevista de cobro.

- Tasa nominal anual $j$.

- Días reales $d$.

- Interés anticipado.

- Comisiones y tributos.

- Neto a acreditar.

La fecha prevista de cobro es la fecha de pago del cheque ajustada al siguiente hábil de compensación.

$I = N \times j \times \frac{d}{365}$

$Neto = N - I - comisiones - tributosretenidos$

Los días incluyen la fecha de anticipo y excluyen la fecha prevista de cobro.

El neto debe ser positivo. Las retenciones deben quedar aceptadas y aplicadas de forma comprobable.

El CFT utiliza el neto recibido y la cancelación prevista por el nominal en la fecha de cobro.

### 25.3. Reconocimiento y devengamiento

Al confirmar el anticipo:

- Se reconoce la obligación nominal de financiación.

- Se identifica la entrega neta.

- El interés anticipado se conserva separado como interés retenido pendiente de devengamiento.

- No se reconoce todo ese interés como resultado ganado el primer día.

- Se devenga durante el plazo según la misma base diaria.

- No se suma nuevamente al nominal al vencimiento.

Contabilidad representa el nominal y el interés pendiente de devengar sin duplicar el activo ni el ingreso.

### 25.4. Desarrollo

- Recibir solicitud e instrumento.

- Solicitar comprobación a Cheques y ECHEQ.

- Comprometer el instrumento para descuento.

- Evaluar cliente, librador y exposición.

- Preparar liquidación y CFT.

- Obtener aceptación contractual.

- Confirmar cesión al banco.

- Ejecutar anticipo y retenciones.

- Reconocer financiación.

- Solicitar presentación oportuna.

- Recibir pago, rechazo o incertidumbre.

- Aplicar cobro o gestionar incumplimiento.

- Resolver instrumento, exposición y saldos.

### 25.5. Cobro y rechazo

Si el cheque se paga:

- El cobro se vincula a la financiación.

- Cancela el nominal pendiente.

- No se acredita otra vez al cliente como depósito libre.

- Se regularizan diferencias y saldos a favor.

Si se rechaza:

- No se crea otro capital por el rechazo.

- Continúa el nominal pendiente.

- Se conserva la causal.

- Se gestiona cobro al cliente y recuperación del instrumento.

- Desde el día siguiente al vencimiento efectivo, el capital impago genera remuneración simple Actual/365 a la tasa
  contractual de descuento.

- No se vuelve a cobrar el interés anticipado del plazo original.

Una demora de presentación o procesamiento atribuible al banco no genera interés adicional al cliente por los días
causados exclusivamente por esa demora.

### 25.6. Cancelación anticipada y cobros concurrentes

Si el cliente cancela antes del vencimiento:

- Se calcula el interés devengado hasta la fecha efectiva.

- El interés retenido todavía no devengado se aplica a reducir la liquidación de cancelación.

- No se cobra comisión de precancelación.

- Las comisiones por servicios ya efectivamente prestados mantienen su tratamiento.

- Se resuelven presentación, cesión y devolución del instrumento.

Si la presentación ya es firme o incierta, el instrumento no se libera hasta comprobar su tratamiento.

Un pago del cliente y un cobro del cheque sobre la misma obligación se concilian. La deuda se cancela una sola vez y
cualquier excedente queda a favor del participante con derecho a recibirlo.

### Criterios de aceptación

| Situación                                                        | Resultado esperado                                                                            |
|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| Nominal 1.000, treinta días y tasa 36,5 %                        | Interés anticipado 30.                                                                        |
| Mismo caso con comisión 5 y sin tributos                         | Neto recibido 965.                                                                            |
| Se confirma el anticipo                                          | Nominal 1.000 e interés retenido 30 identificado separadamente.                               |
| El cheque paga 1.000                                             | Se cancela la financiación sin otra acreditación libre de 1.000 al cliente.                   |
| El cheque es rechazado                                           | Continúa el nominal pendiente; no se genera otro capital de 1.000.                            |
| El cliente cancela al día quince, sin otros conceptos pendientes | Se devengan 15 de interés y los otros 15 retenidos reducen la liquidación del nominal.        |
| Cliente paga y después llega el cobro del cheque                 | Se identifica el excedente y se evita doble cancelación o apropiación del cobro.              |
| Anticipo rechazado con cesión confirmada                         | Se comprueba ausencia de desembolso y se regulariza la cesión; no se abandona el instrumento. |

## 26. Correspondencia funcional

| Información o decisión                            | Responsable                              |
|---------------------------------------------------|------------------------------------------|
| Evaluación y aprobación individual                | Evaluación crediticia                    |
| Políticas, clasificación y exposición compartida  | Riesgos                                  |
| Contrato y modificaciones                         | Contratos                                |
| Capital, intereses, cronogramas y CFT             | Préstamos y líneas                       |
| Deuda e imputación de préstamos y líneas          | Préstamos y líneas                       |
| Utilización inmediata de descubierto              | Fondos                                   |
| Reducción de descubierto por acreditaciones       | Fondos, informada a Préstamos y líneas   |
| Cobro separado de intereses de descubierto        | Préstamos y líneas, ejecutado por Fondos |
| Garantías y cobertura                             | Garantías                                |
| Gestión de recupero                               | Cobranzas y recuperaciones               |
| Legitimación, cesión y cobro de cheques           | Cheques y ECHEQ                          |
| Financiación por descuento y aplicación del cobro | Préstamos y líneas                       |
| Deuda e imputación de tarjetas                    | Cuenta de tarjeta                        |
| Movimientos monetarios                            | Fondos o Tesorería, según el circuito    |
| Criterios tributarios                             | Criterios tributarios                    |
| Asientos y cierres                                | Contabilidad                             |
| Documentos, tareas, reclamos y comunicaciones     | Servicios correspondientes del módulo 12 |

Cada financiación conserva la relación entre aprobación, contrato, respaldo, utilización, devengamiento, pago y
cancelación. Las acreditaciones de cuenta, los pagos de intereses y los cobros de instrumentos se distinguen para
impedir duplicar deuda, ingresos, exposición o recuperaciones.
