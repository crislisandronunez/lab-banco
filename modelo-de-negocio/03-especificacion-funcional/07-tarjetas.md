# Operaciones funcionales — Módulo 7. Tarjetas

## 1. Propósito y alcance

Este documento define las operaciones de tarjetas de débito y crédito: emisión, habilitación, autorizaciones, consumos,
cuotas, resúmenes, pagos, financiación, disputas y cancelación.

Comprende los microservicios Emisión de tarjetas, Autorizaciones, Procesamiento de consumos, Cuenta de tarjeta y
Disputas de tarjetas.

En débito se utilizan recursos de una cuenta bancaria. En crédito se compromete capacidad de financiación y se reconocen
obligaciones posteriores.

Las redes, comercios, administradores y dispositivos externos funcionan como participantes simulados.

La evaluación crediticia pertenece a Evaluación crediticia. La exposición compartida pertenece a Riesgos. Cuenta de
tarjeta conserva la capacidad operativa y la deuda de crédito, sin constituir otro préstamo por los mismos consumos.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-07-01 | Constituir una cuenta de tarjeta de crédito | Cuenta de tarjeta |
| OF-07-02 | Emitir tarjetas principales y adicionales | Emisión de tarjetas |
| OF-07-03 | Entregar, activar y administrar credenciales | Emisión de tarjetas |
| OF-07-04 | Modificar habilitaciones y límites de uso | Emisión de tarjetas y Cuenta de tarjeta |
| OF-07-05 | Bloquear, renovar y reemplazar tarjetas | Emisión de tarjetas |
| OF-07-06 | Autorizar una operación de débito | Autorizaciones |
| OF-07-07 | Autorizar una operación de crédito | Autorizaciones |
| OF-07-08 | Ajustar, anular y vencer autorizaciones | Autorizaciones |
| OF-07-09 | Procesar presentaciones de débito | Procesamiento de consumos |
| OF-07-10 | Procesar presentaciones de crédito | Procesamiento de consumos |
| OF-07-11 | Administrar compras en cuotas y planes financiados | Cuenta de tarjeta |
| OF-07-12 | Procesar anulaciones y devoluciones | Procesamiento de consumos |
| OF-07-13 | Cerrar y emitir un resumen | Cuenta de tarjeta |
| OF-07-14 | Determinar pago total y mínimo | Cuenta de tarjeta |
| OF-07-15 | Recibir e imputar pagos | Cuenta de tarjeta |
| OF-07-16 | Ejecutar el débito automático del resumen | Cuenta de tarjeta y Mandatos y recurrencias |
| OF-07-17 | Financiar saldos y determinar mora | Cuenta de tarjeta |
| OF-07-18 | Administrar operaciones en distintas monedas | Procesamiento de consumos y Cuenta de tarjeta |
| OF-07-19 | Recibir e investigar una disputa | Disputas de tarjetas |
| OF-07-20 | Resolver disputas y contracargos | Disputas de tarjetas |
| OF-07-21 | Conciliar redes y determinar compensación | Procesamiento de consumos |
| OF-07-22 | Corregir consumos, liquidaciones e imputaciones | Servicio responsable del efecto |
| OF-07-23 | Cancelar tarjetas y cerrar la cuenta de tarjeta | Emisión de tarjetas y Cuenta de tarjeta |

## 3. Reglas comunes del módulo

### 3.1. Instrumento, cuenta y contrato

La tarjeta es un instrumento. La cuenta bancaria de débito, la cuenta de tarjeta de crédito y el contrato son relaciones
diferentes.

Cada instrumento identifica portador, modalidad y vínculo operativo.

Las adicionales conservan su relación con el responsable contractual. No crean una capacidad crediticia independiente.

Bloquear, reemplazar o cancelar un instrumento no elimina autorizaciones anteriores, consumos, cuotas, pagos ni
disputas.

### 3.2. Responsabilidades económicas

| Información o actuación | Responsable |
| --- | --- |
| Dinero y reservas de débito | Fondos |
| Capacidad, compromisos y deuda de crédito | Cuenta de tarjeta |
| Decisión de concesión o aumento de crédito | Evaluación crediticia |
| Exposición y límites compartidos | Riesgos |
| Presentaciones y compensación de red | Procesamiento de consumos |
| Liquidaciones externas | Liquidaciones de tesorería |
| Registración contable | Contabilidad |

Autorizaciones coordina los controles y la decisión de uso. No mantiene otra disponibilidad monetaria o crediticia.

### 3.3. Modalidades comprendidas

Se admiten:

- Compras de débito.

- Extracciones de débito mediante Operaciones de efectivo.

- Compras de crédito en un pago.

- Compras en cuotas sin interés para el titular.

- Planes financiados por el banco.

- Financiación del saldo impago del resumen.

No se ofrecen adelantos de efectivo de crédito ni nuevas operaciones sin solicitud de autorización.

Las presentaciones recibidas sin autorización se investigan. Su recepción no autoriza automáticamente un cargo al
cliente.

### 3.4. Monedas y límites

Se mantienen posiciones en pesos y dólares.

Cada límite:

- Tiene moneda identificada.

- Debe estar aprobado.

- Mantiene su afectación en Riesgos.

- Considera capital y compromisos.

- Se distingue de los sublímites de uso.

Los límites de monedas distintas no se suman ni intercambian automáticamente.

Los sublímites de adicionales, canales o modalidades no crean crédito adicional.

Una operación en otra moneda requiere una conversión identificada antes de comprometer su equivalente.

### 3.5. Autorización y deuda

La autorización compromete capacidad. La presentación válida reconoce el consumo.

En crédito se distinguen:

- Compromisos de autorizaciones.

- Capital presentado.

- Capital facturado.

- Capital de cuotas futuras.

- Capital financiado después del vencimiento.

- Intereses devengados.

- Intereses futuros previstos.

- Comisiones y tributos.

- Saldos a favor.

- Conceptos cuestionados.

Convertir una autorización en deuda no consume dos veces el límite.

La clasificación de un capital entre consumo, plan y financiación rotativa no crea capital nuevo.

### 3.6. Calendario

La referencia temporal es America/Argentina/Buenos_Aires.

Cada cuenta tiene un día de cierre contractual entre el 1 y el 28 de cada mes.

El cierre comprende las presentaciones confirmadas hasta finalizar ese día. Las posteriores pasan al ciclo siguiente.

El vencimiento ordinario se fija veinte días corridos después del cierre, ajustado al siguiente día hábil.

El banco entrega el resumen al menos cinco días corridos antes del vencimiento.

Si no puede acreditar entrega oportuna:

- Conserva el cierre original.

- Registra la incidencia.

- Establece un vencimiento que preserve esa anticipación.

- Actualiza las programaciones de cobro.

- No genera mora ni financiación adicional atribuible a la demora del banco.

### 3.7. Precisión

Los cálculos intermedios conservan al menos doce decimales.

Los importes finales se redondean a la unidad mínima de la moneda, al más cercano, elevando los empates positivos.

El pago mínimo se redondea hacia arriba, sin superar el total exigible.

Las reversiones utilizan importes efectivamente registrados.

### 3.8. Recuperación y aprobaciones

Cada actuación tiene referencia única y contenido identificable.

Una repetición recupera el resultado original. La misma referencia con contenido distinto genera una inconsistencia.

La falta de respuesta exige consultar antes de repetir, liberar compromisos o devolver fondos.

Los ajustes manuales y las resoluciones de disputas requieren preparación y aprobación separadas.

## 4. OF-07-01. Constituir una cuenta de tarjeta de crédito

Propósito: registrar la relación que administrará capacidad y deuda.

Responsable: Cuenta de tarjeta.

### Desarrollo

- Comprobar contrato y aprobación crediticia.

- Identificar responsables.

- Registrar límites por moneda.

- Establecer cierre y condiciones económicas.

- Transformar el compromiso de aprobación en exposición de producto.

- Confirmar constitución.

### Reglas

- La constitución no genera deuda.

- Una cuenta puede reunir tarjetas principales y adicionales.

- Las tasas, cargos y modalidades deben estar definidos antes de habilitar.

- El porcentaje de mínimo se aplica conforme a OF-07-14.

- La repetición no constituye otra cuenta.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Límite 1.000 sin consumos | Capacidad 1.000 y deuda cero. |
| Tres adicionales | Comparten la capacidad aprobada. |
| Falta aprobación en dólares | No se habilita capacidad en esa moneda. |
| Constitución repetida | Misma cuenta de tarjeta. |

## 5. OF-07-02. Emitir tarjetas principales y adicionales

Propósito: crear instrumentos vinculados a relaciones válidas.

Responsable: Emisión de tarjetas.

### Desarrollo

- Comprobar contratación.

- Identificar portador.

- Comprobar vínculo y facultades.

- Asignar identificación protegida.

- Registrar modalidad, capacidades y vencimiento.

- Preparar entrega.

### Reglas

- Débito requiere una cuenta bancaria compatible.

- Una adicional requiere autorización del responsable contractual.

- El portador no adquiere titularidad de la cuenta bancaria por recibir una tarjeta.

- Una adicional no crea otra deuda por los mismos consumos.

- Las credenciales sensibles no se exponen en comprobantes.

- Emitir no activa automáticamente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Emisión de adicional | Portador y responsable identificados. |
| Cuenta bancaria incompatible | No se habilita débito. |
| Solicitud repetida | Misma emisión. |
| Instrumento emitido | Permanece sin uso hasta activación. |

## 6. OF-07-03. Entregar, activar y administrar credenciales

Propósito: habilitar el instrumento para su portador legítimo.

Responsable: Emisión de tarjetas.

### Desarrollo

- Identificar destinatario.

- Registrar entrega.

- Recibir activación.

- Comprobar identidad y facultades.

- Habilitar credenciales.

- Conservar evidencia.

### Reglas

- La entrega y la activación son distintas.

- Los datos impresos no bastan para autorizar activación.

- La recuperación requiere verificación adicional.

- Una credencial sustituida deja de habilitar nuevos usos.

- Los intentos fallidos generan controles sin modificar deuda.

- Un reemplazo no reactiva el instrumento anterior.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Tarjeta no activada | No admite nuevas compras. |
| Activación repetida | No crea otro instrumento. |
| Credencial sustituida | No habilita nuevos usos. |

## 7. OF-07-04. Modificar habilitaciones y límites de uso

Propósito: administrar capacidades sin conceder financiación indebidamente.

Responsables: Emisión de tarjetas y Cuenta de tarjeta.

### Desarrollo

- Registrar petición.

- Distinguir límite de uso y límite de crédito.

- Comprobar facultades.

- Obtener evaluación cuando cambie financiación.

- Aplicar y confirmar.

- Comunicar.

### Reglas

- El cliente puede reducir sus límites de uso.

- Un aumento de crédito requiere Evaluación crediticia y Riesgos.

- Los sublímites no superan la capacidad común.

- Una reducción no elimina deuda ni compromisos firmes.

- Los compromisos anteriores conservan cobertura exclusiva cuando sea necesaria.

- La cobertura protegida no habilita compras nuevas.

- Las instrucciones pendientes de resultado no se liberan por reducir el límite.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Límite general 1.000 y sublímite adicional 200 | La adicional no compromete 300. |
| Se aumenta un sublímite | No aumenta el crédito general. |
| Límite menor que deuda y compromisos | Capacidad nueva cero y exceso identificado. |

## 8. OF-07-05. Bloquear, renovar y reemplazar tarjetas

Propósito: impedir nuevos usos y mantener continuidad legítima.

Responsable: Emisión de tarjetas.

### Desarrollo

- Recibir denuncia, solicitud o vencimiento.

- Registrar fecha, hora y referencia.

- Aplicar bloqueo.

- Comunicar a Autorizaciones y red.

- Determinar reemplazo o renovación.

- Emitir y activar el sucesor por su procedimiento.

### Reglas

- Las denuncias se reciben permanentemente.

- El bloqueo impide nuevas autorizaciones alcanzadas.

- Las presentaciones de operaciones anteriores se revisan según sus antecedentes.

- El reemplazo no reutiliza credenciales comprometidas.

- Renovar el instrumento no renueva automáticamente el contrato.

- Las cuotas y obligaciones permanecen vinculadas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Bloqueo anterior a nueva compra | Autorización rechazada. |
| Presentación posterior de compra anterior válida | Se procesa sin reactivar la tarjeta. |
| Reemplazo | No duplica deuda ni pierde cuotas. |

## 9. OF-07-06. Autorizar una operación de débito

Propósito: comprobar el uso y comprometer recursos bancarios.

Responsable: Autorizaciones.

### Desarrollo

- Identificar instrumento y operación.

- Comprobar vigencia y autenticación.

- Revisar capacidades, límites y restricciones.

- Solicitar controles especializados.

- Obtener reserva de Fondos.

- Confirmar autorización.

### Reglas

- Fraude favorable no sustituye disponibilidad.

- Moneda e importe deben corresponder a la cuenta o conversión aceptada.

- El uso de descubierto requiere que la operación y la cuenta lo admitan.

- La extracción se vincula a Operaciones de efectivo.

- No se crean dos reservas para la misma extracción.

- Una reserva incierta se consulta antes de responder favorablemente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capacidad 100 y compra 70 | Reserva 70, sin débito definitivo. |
| Dos compras de 80 con capacidad 100 | No se aprueban ambas. |
| Extracción autorizada sin entrega | No se informa efectivo entregado. |

## 10. OF-07-07. Autorizar una operación de crédito

Propósito: comprometer capacidad de financiación.

Responsable: Autorizaciones.

### Desarrollo

- Comprobar instrumento y contenido.

- Aplicar controles.

- Determinar capital a comprometer.

- Solicitar afectación a Cuenta de tarjeta.

- Confirmar autorización y vigencia.

### Reglas

- Cuenta de tarjeta es la autoridad de capacidad.

- Una compra en cuotas compromete el capital total.

- Los intereses futuros se presentan separados.

- Los adicionales consumen capacidad común y su sublímite.

- Autorizar no demuestra presentación.

- Una financiación que exceda condiciones aprobadas requiere evaluación previa.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Límite 1.000 y compra 600 en cuotas | Capacidad restante 400. |
| Autorización repetida | Un compromiso. |
| Capacidad insuficiente | No se aprueba por ingresos futuros esperados. |

## 11. OF-07-08. Ajustar, anular y vencer autorizaciones

Propósito: administrar compromisos anteriores a su presentación.

Responsable: Autorizaciones.

### Desarrollo

- Identificar autorización.

- Obtener ajuste, anulación o vencimiento.

- Comprobar presentaciones.

- Determinar remanente.

- Solicitar consumo, liberación o mantenimiento.

- Confirmar.

### Reglas

- Las autorizaciones de compra tienen siete días corridos de vigencia.

- El vencimiento inicia comprobación con la red.

- Solo se libera cuando no existe ejecución pendiente.

- Una incertidumbre mantiene cobertura.

- Los aumentos requieren capacidad adicional.

- Una presentación parcial libera el resto únicamente si cierra la operación.

- Las presentaciones tardías conservan investigación; no generan cargos automáticos sobre una autorización liberada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Reserva 100 y presentación final 60 | Consumo 60 y liberación 40. |
| Aumento de 100 a 120 | Comprobación de 20 adicionales. |
| Vencimiento incierto | No se libera. |
| Anulación repetida | No aumenta dos veces capacidad. |

## 12. OF-07-09. Procesar presentaciones de débito

Propósito: convertir presentaciones válidas en movimientos.

Responsable: Procesamiento de consumos.

### Desarrollo

- Validar participante y referencia.

- Detectar repetición.

- Relacionar autorización.

- Comprobar importe, moneda y resultado.

- Solicitar consumo de reserva.

- Registrar cargo y obligación externa.

### Reglas

- Un exceso exige autorización adicional o resolución fundada.

- Una presentación sin autorización no concede descubierto.

- Una obligación externa que el banco deba asumir sin derecho a cargarla al cliente se trata separadamente.

- Incluir una operación en un archivo de red no demuestra su procedencia.

- Las extracciones utilizan la entrega comprobada de Operaciones de efectivo.

- No se vuelve a debitar una extracción ya regularizada por su circuito.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Autorización y presentación 80 | Débito único 80. |
| Presentación repetida | Sin segundo cargo. |
| Presentación 100 y autorización 80 | Los 20 adicionales requieren tratamiento válido. |
| Extracción ya debitada | La presentación confirma el mismo efecto. |

## 13. OF-07-10. Procesar presentaciones de crédito

Propósito: reconocer consumos y transformar compromisos en deuda.

Responsable: Procesamiento de consumos.

### Desarrollo

- Validar presentación.

- Relacionar autorización.

- Comprobar condiciones.

- Comunicar a Cuenta de tarjeta.

- Confirmar transformación.

- Incorporar compensación.

### Reglas

- Compromiso y deuda no consumen dos veces el límite.

- La presentación confirmada determina el ciclo.

- Los cargos tardíos o sin autorización requieren revisión.

- Una falla de liquidación no elimina deuda válida.

- Una diferencia de condiciones del plan impide aceptar silenciosamente otra financiación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compromiso 100 convertido en consumo | Capital 100 y compromiso cero. |
| Presentación después del cierre | Ciclo siguiente. |
| Presentación repetida | Un consumo. |
| Cuotas distintas de las aceptadas | Pendiente de resolución. |

## 14. OF-07-11. Administrar compras en cuotas y planes financiados

Propósito: distribuir capital y financiación en vencimientos identificados.

Responsable: Cuenta de tarjeta.

### 14.1. Modalidades

Cuotas sin interés

El capital se divide entre la cantidad aceptada. La última amortización absorbe el residuo de redondeo.

Plan financiado por el banco

Utiliza sistema francés con tasa periódica, plazo, cargos y costo financiero total aceptados.

Puede aplicarse:

- A una compra identificada.

- A capital de consumos existentes seleccionado por el cliente.

No se incorporan automáticamente intereses, comisiones ni tributos pendientes al capital del plan.

Transformar deuda existente en un plan no genera otra compra, desembolso ni afectación adicional del capital ya
comprometido.

### 14.2. Cálculo financiero

Con capital $P$, tasa mensual efectiva $i$ y cantidad de cuotas $n$:

$C = P\frac{i}{1 - (1 + i)^{- n}}$

Con tasa cero:

$C = \frac{P}{n}$

Las cuotas distinguen capital, interés y conceptos adicionales.

La tasa nominal anual con conversión mensual utiliza $i = j/12$. Una tasa efectiva anual utiliza:

$i = (1 + e)^{1/12} - 1$

La primera cuota integra el primer cierre que corresponda a la presentación o constitución confirmada del plan.

Las siguientes corresponden a cierres mensuales sucesivos. Los vencimientos utilizan el calendario de la cuenta.

Para un primer período distinto de un mes:

$i_{inicial} = (1 + i)^{d/D} - 1$

Donde:

- $d$: días desde la constitución del plan hasta el primer vencimiento contractual.

- $D$: días del mes de referencia comprendido entre ese vencimiento y la misma fecha del mes anterior, ajustada al
  último día si fuera necesario.

La diferencia entre interés inicial e interés mensual ordinario se identifica en la primera cuota. Las restantes
conservan su cronograma.

El ajuste hábil del vencimiento no agrega intereses por sí solo.

### 14.3. Costo financiero total

El CFT expresa el costo anual efectivo del plan.

El beneficio financiado inicial es:

- El precio de contado financiado, en una compra.

- El capital existente incorporado al plan, en una reorganización de deuda.

No se utiliza como beneficio el total de cuotas con intereses.

Los cargos retenidos o pagados al constituir reducen el beneficio neto. Los cargos exigibles después se incorporan a los
pagos de sus fechas.

Se incluyen intereses, comisiones, seguros obligatorios y tributos directamente asociados al plan.

Los cargos generales de la tarjeta no se atribuyen nuevamente al plan cuando existirían con independencia de
contratarlo. Un cargo exigido específicamente para acceder al plan sí se incluye.

Con beneficio neto inicial $B$ y pagos obligatorios $Q_{k}$:

$B = \frac{Q_{k}}{(1 + r)^{d_{k}/365}}$

Donde:

- $r$: CFT anual efectivo.

- $d_{k}$: días desde la constitución hasta cada pago.

Se utilizan fechas contractuales ajustadas y los importes finales del cronograma.

El resultado se muestra como porcentaje anual con dos decimales, conservando precisión interna.

Un costo obligatorio desconocido impide presentar un CFT definitivo. Los conceptos pagados al inicio no se cuentan otra
vez en las cuotas.

### 14.4. Desarrollo

- Identificar compra o capital a financiar.

- Comprobar elegibilidad, capacidad y restricciones.

- Obtener condiciones.

- Calcular fechas, cuotas y conceptos.

- Calcular CFT.

- Presentar costo, composición y total.

- Obtener aceptación.

- Constituir el plan.

- Transformar la clasificación del capital sin duplicarlo.

- Generar sus obligaciones.

### 14.5. Reglas

- El capital total ocupa capacidad.

- Los intereses futuros previstos no se reconocen anticipadamente como devengados.

- Pagar capital restituye capacidad dentro del límite vigente.

- Se admite precancelar sin comisión.

- La precancelación elimina intereses futuros no devengados.

- Si una cuota queda impaga, su capital pasa al tratamiento rotativo desde el día siguiente al vencimiento.

- El interés del plan no continúa sobre ese capital durante el mismo período de financiación rotativa.

- El capital de cuotas futuras conserva el tratamiento del plan.

- Una corrección del plan recalcula los documentos afectados.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compra 100 en tres cuotas sin interés | Capital 33,33; 33,33; 33,34. |
| Compra 600 en seis cuotas | Capacidad afectada por 600. |
| Capital existente 500 convertido en plan | Continúan 500 de capital, sin nueva compra de 500. |
| Beneficio neto 1.000 y pago único 1.100 a 365 días | CFT 10 %. |
| Cargo inicial 20 y pago 1.100 a 365 días sobre capital 1.000 | Beneficio neto 980 y CFT aproximado 12,24 %. |
| Precancela capital futuro | No se cobran intereses futuros no devengados. |
| Cuota impaga | Su capital no devenga simultáneamente interés de plan y rotativo. |

## 15. OF-07-12. Procesar anulaciones y devoluciones

Propósito: registrar actuaciones que contrarrestan consumos.

Responsable: Procesamiento de consumos.

### Desarrollo

- Identificar consumo o autorización.

- Comprobar importe pendiente de devolución.

- Distinguir liberación de compromiso y crédito.

- Coordinar con Fondos o Cuenta de tarjeta.

- Actualizar compensación.

- Comunicar.

### Reglas

- Anular una autorización no acredita un pago inexistente.

- Devolver un consumo genera un crédito vinculado.

- En cuotas se reduce primero capital futuro del mismo consumo y después capital facturado pendiente.

- El capital futuro se reduce desde las últimas cuotas hacia las primeras.

- Se recalculan los intereses futuros asociados al capital eliminado.

- Si el capital ya fue pagado, se genera saldo a favor.

- Se regularizan intereses indebidamente cobrados.

- No se devuelve más que el importe recuperable.

- Una devolución no se aplica silenciosamente a otra moneda.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Consumo 100 y devolución 40, sin pagos previos | Capital restante 60. |
| Consumo totalmente pagado y devolución 100 | Saldo a favor 100. |
| Autorización no presentada anulada | Liberación sin acreditación monetaria. |
| Plan con capital futuro reducido | Cronograma e intereses futuros actualizados. |

## 16. OF-07-13. Cerrar y emitir un resumen

Propósito: determinar y comunicar las obligaciones del ciclo.

Responsable: Cuenta de tarjeta.

### Desarrollo

- Fijar corte.

- Incorporar presentaciones y cuotas.

- Conciliar pagos y devoluciones.

- Liquidar intereses, cargos y tributos.

- Separar conceptos cuestionados.

- Determinar total y mínimo.

- Conservar documento.

- Entregar mediante Comunicaciones.

### Reglas

- Las autorizaciones pendientes son información, no deuda presentada.

- Las cuotas futuras se muestran separadas.

- Las monedas no se suman.

- Los planes identifican capital, interés, cuotas restantes y CFT aceptado.

- La financiación rotativa informa tasa, base y período calculado.

- Las rectificaciones conservan el resumen original.

- La falla de entrega aplica el tratamiento temporal común.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Autorización sin presentación | No integra el total exigible. |
| Primera cuota de seis | Se exige la primera, no todo el plan. |
| Resumen rectificado | Se conservan ambas versiones. |
| Obligaciones en pesos y dólares | Totales separados. |

## 17. OF-07-14. Determinar pago total y mínimo

Propósito: determinar lo exigible y la parte financiable.

Responsable: Cuenta de tarjeta.

### Cálculo por moneda

Sobre conceptos no cuestionados:

- $P$: capital facturado pendiente, sin cuotas futuras.

- $K$: parte de ese capital correspondiente a mínimos anteriores vencidos.

- $C$: intereses, comisiones y tributos exigibles pendientes.

$T = P + C$

$M = \min_{}\left( T,\ C + K + 0,10(P - K) \right)$

El mínimo se redondea hacia arriba a la unidad mínima sin superar el total.

### Reglas

- El porcentaje adoptado es 10 %.

- $K$ forma parte de $P$; no se agrega como otra deuda.

- Los créditos y devoluciones aplicables se consideran antes del cálculo.

- Los conceptos cuestionados se excluyen mientras su exigibilidad esté suspendida.

- Pagar el total no precancela cuotas futuras.

- No se cobra comisión por pagar solo el mínimo.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| $P = 1.000,\ K = 0,\ C = 20$ | Total 1.020 y mínimo 120. |
| $P = 1.000,\ K = 100,\ C = 20$ | Mínimo 210. |
| Total cero | Mínimo cero. |
| Cuotas futuras 500 | No se incluyen por su sola existencia. |

## 18. OF-07-15. Recibir e imputar pagos

Propósito: aplicar fondos confirmados.

Responsable: Cuenta de tarjeta.

### Desarrollo

- Identificar pago, cuenta y moneda.

- Comprobar ingreso.

- Determinar obligaciones.

- Aplicar imputación.

- Actualizar mínimo pendiente.

- Restituir capacidad por capital pagado.

- Tratar excedentes.

- Comunicar.

### Reglas

- Se atienden primero obligaciones vencidas más antiguas y luego las del ciclo.

- Dentro de ellas: intereses exigibles y después capital; comisiones posteriormente.

- Los tributos se distribuyen con sus conceptos.

- El pago reduce el mínimo pendiente hasta el importe recibido.

- Cumplir el mínimo no altera la clasificación de los conceptos todavía impagos.

- La fecha efectiva del ingreso determina el efecto sobre financiación y mora.

- Los excedentes quedan a favor o se devuelven por solicitud válida.

- Un saldo a favor no aumenta el límite.

- No hay conversión implícita entre monedas.

- Un pago repetido no se imputa otra vez.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Total 1.020, mínimo 120 y pago 120 | Mínimo cumplido y deuda restante 900. |
| Se pagan 100 de capital | Se restituye capacidad dentro del límite vigente. |
| Pago mayor que total | Excedente identificado. |
| Pago oportuno procesado después | Sin mora atribuible a demora interna. |

## 19. OF-07-16. Ejecutar el débito automático del resumen

Propósito: cobrar total o mínimo mediante autorización vigente.

Responsables: Cuenta de tarjeta y Mandatos y recurrencias.

### Desarrollo

- Obtener importe pendiente.

- Considerar pagos y créditos.

- Comprobar mandato.

- Generar ocurrencia única.

- Solicitar débito a Fondos.

- Comprobar resultado.

- Imputar y comunicar.

### Reglas

- La adhesión distingue total y mínimo.

- La ejecución ordinaria es a las 09:00 del vencimiento efectivo.

- El importe debe respetar el máximo autorizado por el mandato.

- No se cobra más que la obligación pendiente.

- La insuficiencia rechaza el débito completo.

- No se ejecutan parcialidades ni reintentos ocultos.

- Un pago concurrente exige volver a comprobar el remanente.

- Si una concurrencia produjo excedente, se reconoce y regulariza.

- La reversión de un débito se coordina con Pagos y se refleja en la deuda sin duplicar restituciones.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Total 500 y pago previo 200 | Débito pendiente 300. |
| Mínimo 100 y capacidad 80 | No se cobran 80 como parcialidad no autorizada. |
| Mandato revocado antes de originar | No se genera el cobro. |
| Cobro repetido | Sin segunda imputación. |

## 20. OF-07-17. Financiar saldos y determinar mora

Propósito: administrar capital impago después del vencimiento.

Responsable: Cuenta de tarjeta.

### Interés rotativo

Las compras ordinarias en un pago no generan interés antes del vencimiento.

El capital facturado impago genera interés desde el día siguiente:

$I = P_{d}\frac{j_{d}}{365}$

Donde:

- $P_{d}$: capital financiado pendiente al finalizar cada día.

- $j_{d}$: tasa nominal anual vigente.

- El divisor es 365 incluso en años bisiestos.

### Reglas

- No se capitalizan automáticamente intereses.

- No se calcula interés sobre comisiones, tributos o intereses impagos.

- No se agrega interés punitorio ni penalidad fija.

- Las cuotas futuras no se financian rotativamente antes de vencer.

- Una cuota de plan vencida deja de generar interés del plan sobre el capital transferido a financiación rotativa.

- Los pagos reducen la base desde su fecha efectiva.

- Los períodos se identifican para no devengar dos veces los mismos días.

### Información del costo

Como el costo rotativo depende de pagos futuros todavía desconocidos:

- Se informa tasa nominal y base diaria.

- Se muestra el interés efectivamente generado.

- Una proyección identifica capital, plazo y pagos supuestos.

- El CFT de una proyección se calcula con el mismo criterio de flujos de OF-07-11.

- La proyección no se presenta como costo definitivo de cualquier comportamiento de pago.

### Mora

- Mínimo cumplido en término: sin mora del mínimo.

- Pago menor: diferencia de mínimo vencida.

- La mora comienza el día siguiente al vencimiento efectivo.

- Los conceptos con exigibilidad suspendida no generan mora.

- Una restricción por incumplimiento requiere fundamento.

- Reclamar no constituye por sí mismo incumplimiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capital 1.000 durante diez días al 36,5 % | Interés 10. |
| Total pagado en término | Sin interés rotativo por esas compras. |
| Mínimo cumplido y saldo restante | Financiación sin mora del mínimo. |
| Interés pendiente 20 | No se incorpora al capital base. |
| Cuota futura aún no vencida | No genera financiación rotativa. |

## 21. OF-07-18. Administrar operaciones en distintas monedas

Propósito: conservar importes y conversiones verificables.

Responsables: Procesamiento de consumos y Cuenta de tarjeta; Cambio de moneda ejecuta las conversiones.

### Desarrollo

- Conservar importe y moneda originales.

- Determinar moneda de presentación.

- Obtener conversión identificada cuando sea necesaria.

- Comprobar capacidad.

- Registrar diferencias.

- Informar deuda separada.

- Convertir al pagar por instrucción válida.

### Reglas

- No se convierte toda deuda en dólares automáticamente al cierre.

- Se admite pagar deuda en dólares mediante dólares o pesos con conversión aceptada.

- El equivalente informativo no es deuda definitiva en pesos.

- La diferencia de presentación requiere capacidad adicional o resolución.

- Las devoluciones conservan la moneda de la obligación.

- Una cotización histórica no se reutiliza como oferta vigente.

- El CFT de un plan se expresa en su moneda contractual; no incorpora variaciones cambiarias futuras supuestas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Deuda USD 100 | Permanece USD 100 hasta pago o conversión válida. |
| Cotización aceptada ARS 1.000 por USD | Principal equivalente ARS 100.000. |
| Devolución USD 20 | Reduce la obligación correspondiente en dólares. |
| Pago en otra moneda sin instrucción | No se convierte implícitamente. |

## 22. OF-07-19. Recibir e investigar una disputa

Propósito: examinar operaciones cuestionadas.

Responsable: Disputas de tarjetas.

### Desarrollo

- Registrar presentación y fecha.

- Identificar operación y conceptos.

- Obtener antecedentes.

- Emitir constancia.

- Aplicar protección correspondiente.

- Investigar con participantes.

- Preparar explicación o corrección.

### Plazos adoptados

- Acuse el mismo día.

- Respuesta fundada dentro de quince días corridos.

- Operaciones exteriores que requieran investigación adicional: hasta sesenta días corridos, comunicando el motivo.

- Los cuestionamientos posteriores a treinta días desde la recepción del resumen se reciben como reclamos, sin rechazo
  automático.

### Reglas

- En crédito se suspende exigibilidad e intereses de los conceptos cuestionados durante la investigación.

- Se recalculan total exigible y mínimo.

- La suspensión no elimina automáticamente el capital ni restituye capacidad crediticia.

- Un crédito provisional o una resolución favorable puede modificar esa posición.

- En débito, suspender exigibilidad no equivale a devolver dinero ya debitado.

- Una restitución provisional de débito requiere decisión fundada y movimiento identificado.

- No se bloquea al cliente por el solo hecho de reclamar.

- La investigación de fraude conserva su procedimiento especializado.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se cuestionan 100 de total 500 | Exigibilidad calculada sobre los restantes conceptos. |
| Pago de lo no cuestionado | No implica aceptar lo disputado. |
| Débito cuestionado sin resolución | No se informa devolución inexistente. |
| Falta evidencia del comercio | Continúa investigación y protección aplicable. |

## 23. OF-07-20. Resolver disputas y contracargos

Propósito: determinar resultado frente al cliente y recupero de red.

Responsable: Disputas de tarjetas.

### Desarrollo

- Valorar evidencia.

- Preparar resolución.

- Obtener aprobación separada.

- Coordinar ajustes.

- Comunicar.

- Atender disconformidad.

- Seguir recuperos.

### Reglas

- El resultado frente al cliente no depende de recuperar primero del comercio.

- Una resolución favorable cancela el concepto y sus consecuencias indebidas.

- Una desfavorable no cobra intereses retroactivos por el período protegido.

- Se conceden siete días corridos para disconformidad.

- La reconsideración se resuelve en diez días hábiles.

- La protección se mantiene durante ese plazo de disconformidad y durante una reconsideración presentada a tiempo.

- El concepto que vuelva a ser exigible integra un resumen posterior con entrega y vencimiento propios.

- El provisional se regulariza contra el definitivo.

- El contracargo no modifica deuda sin actuación vinculada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Resolución favorable sin recupero externo | Se regulariza al cliente sin esperar al comercio. |
| Provisional 100 y devolución definitiva 100 | No se acreditan otros 100. |
| Resolución desfavorable después de un mes protegido | Sin interés retroactivo de ese período. |
| Reconsideración oportuna | Continúa protección hasta resolver. |

## 24. OF-07-21. Conciliar redes y determinar compensación

Propósito: comprobar operaciones y obligaciones externas.

Responsable: Procesamiento de consumos.

### Desarrollo

- Recibir registros de red.

- Comparar autorizaciones, cargos y devoluciones.

- Identificar diferencias.

- Consultar resultados.

- Calcular obligaciones.

- Entregar a Tesorería.

- Registrar liquidaciones y pendientes.

### Reglas

- La red de tarjetas se concilia en este módulo.

- Corte diario a las 18:00.

- Las operaciones posteriores integran el ciclo siguiente.

- Las monedas se mantienen separadas.

- El neteo requiere acuerdo.

- Los ajustes y contracargos conservan referencias.

- Una falla de liquidación no vuelve impago lo cancelado por el cliente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Obligaciones 100 y devoluciones 30 con neteo | Pago neto 70. |
| Presentación externa duplicada | Investigación sin duplicar cargo. |
| Falla de Tesorería | Obligación externa pendiente, sin otro cobro al cliente. |

## 25. OF-07-22. Corregir consumos, liquidaciones e imputaciones

Propósito: regularizar errores sin borrar antecedentes.

Responsables según efecto:

- Presentación: Procesamiento de consumos.

- Compromiso: Autorizaciones y autoridad de capacidad.

- Deuda o pago: Cuenta de tarjeta.

- Dinero bancario: Fondos.

- Asiento: Contabilidad.

### Desarrollo

- Identificar original.

- Determinar causa y efectos.

- Obtener autorización.

- Registrar ajustes.

- Recalcular capital, capacidad, mínimo, intereses y mora.

- Rectificar cronogramas, CFT o resúmenes afectados.

- Comunicar.

### Reglas

- Los ajustes manuales tienen preparación y aprobación separadas.

- Corregir imputación no duplica ingresos.

- Se eliminan consecuencias indebidas desde la fecha pertinente.

- Una rectificación conserva el documento anterior.

- Los excesos de cobro mantienen obligación de restitución.

- Una corrección de cálculo no modifica silenciosamente condiciones aceptadas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Pago 100 mal imputado | Redistribución sin otro cobro. |
| Error produjo interés 5 | Regularización del concepto y eventual cobro. |
| Corrección afecta mínimo anterior | Se elimina incumplimiento artificial. |
| CFT mal presentado sin error en cuotas | Se rectifica información, sin crear movimientos ficticios. |

## 26. OF-07-23. Cancelar tarjetas y cerrar la cuenta de tarjeta

Propósito: finalizar nuevos usos y resolver posiciones.

Responsables: Emisión de tarjetas y Cuenta de tarjeta.

### Desarrollo

- Registrar petición o causa.

- Impedir nuevas autorizaciones alcanzadas.

- Informar a la red.

- Revisar compromisos, consumos, planes y disputas.

- Mantener cobros y devoluciones procedentes.

- Resolver deuda y saldos a favor.

- Regularizar exposición.

- Confirmar cierre o administración residual.

### Reglas

- La baja se recibe con deuda.

- Las cuotas futuras no se aceleran por solicitar baja.

- Los cargos de servicios futuros cesan según terminación.

- Las demoras internas no prolongan esos cargos.

- Las presentaciones válidas anteriores mantienen tratamiento.

- Las adicionales pueden cancelarse individualmente.

- Cancelar la principal debe indicar expresamente qué ocurre con las adicionales.

- El régimen residual no admite compras nuevas.

- Las disputas, devoluciones y obligaciones continúan.

- La finalización económica requiere tratar compromisos firmes e inciertos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Baja con seis cuotas futuras | Continúan según cronograma, salvo precancelación aceptada. |
| Presentación anterior válida | Se procesa sin reactivar instrumento. |
| Saldo a favor 50 | Se restituye por destino autorizado. |
| Baja de una adicional | Las otras no se cancelan automáticamente. |
| Disputa pendiente | Continúa investigación y reparación. |

## 27. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Instrumentos, portadores y credenciales | Emisión de tarjetas |
| Solicitudes y decisiones de autorización | Autorizaciones |
| Reservas y dinero de débito | Fondos |
| Límites y compromisos de crédito | Cuenta de tarjeta |
| Presentaciones, devoluciones y red | Procesamiento de consumos |
| Capital, cuotas, intereses y CFT de planes | Cuenta de tarjeta |
| Resúmenes, mínimo e imputación | Cuenta de tarjeta |
| Evaluación crediticia | Evaluación crediticia |
| Exposición compartida | Riesgos |
| Mandatos de débito automático | Mandatos y recurrencias |
| Reversión del débito bancario | Pagos, coordinado con Cuenta de tarjeta |
| Disputas y contracargos | Disputas de tarjetas |
| Investigación de fraude | Prevención de fraude |
| Gestión de deuda vencida | Cobranzas y recuperaciones |
| Conversiones monetarias | Cambio de moneda |
| Liquidaciones externas | Liquidaciones de tesorería |
| Registración contable | Contabilidad |
| Reclamos generales y tareas humanas | Reclamos y Gestión administrativa |
| Evidencias y comprobantes | Documentos y comprobantes |
| Entrega de resúmenes y notificaciones | Comunicaciones |

Cada operación conserva la relación entre instrumento, autorización, compromiso, presentación, deuda, pago y
liquidación. Los planes, las devoluciones y las disputas mantienen su trazabilidad sin duplicar capital, intereses,
capacidad ni recuperaciones.
