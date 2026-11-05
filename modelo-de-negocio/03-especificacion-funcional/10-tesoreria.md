# Operaciones funcionales — Módulo 10. Tesorería

## 1. Propósito y alcance

Este documento define las operaciones para administrar las disponibilidades propias del banco, sus posiciones
financieras, la liquidez, las operaciones cambiarias y las liquidaciones externas.

Comprende los microservicios Posiciones y liquidez, Operaciones de tesorería, Cambio de moneda y Liquidaciones de
tesorería.

La disponibilidad de una cuenta de cliente no equivale a la liquidez del banco. Una instrucción operativa confirmada y
su liquidación entre participantes pueden ocurrir en momentos distintos.

Los mercados, entidades depositarias y contrapartes externas funcionan dentro de la simulación.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-10-01 | Registrar cuentas y ubicaciones de disponibilidades | Posiciones y liquidez |
| OF-10-02 | Registrar movimientos de recursos propios | Posiciones y liquidez |
| OF-10-03 | Trasladar y verificar efectivo | Posiciones y liquidez |
| OF-10-04 | Comprometer y liberar disponibilidades | Posiciones y liquidez |
| OF-10-05 | Proyectar flujos y necesidades de liquidez | Posiciones y liquidez |
| OF-10-06 | Resolver faltantes y excedentes de liquidez | Posiciones y liquidez |
| OF-10-07 | Concertar financiación propia | Operaciones de tesorería |
| OF-10-08 | Concertar inversiones propias | Operaciones de tesorería |
| OF-10-09 | Administrar rendimientos y vencimientos propios | Operaciones de tesorería |
| OF-10-10 | Modificar y cancelar operaciones propias | Operaciones de tesorería |
| OF-10-11 | Administrar cotizaciones | Cambio de moneda |
| OF-10-12 | Cotizar una operación cambiaria | Cambio de moneda |
| OF-10-13 | Ejecutar cambio de moneda | Cambio de moneda |
| OF-10-14 | Resolver incidencias cambiarias | Cambio de moneda |
| OF-10-15 | Recibir y validar obligaciones de liquidación | Liquidaciones de tesorería |
| OF-10-16 | Preparar e instruir liquidaciones | Liquidaciones de tesorería |
| OF-10-17 | Confirmar cobros y entregas externos | Liquidaciones de tesorería |
| OF-10-18 | Gestionar liquidaciones parciales o incumplidas | Liquidaciones de tesorería |
| OF-10-19 | Conciliar disponibilidades y liquidaciones | Liquidaciones de tesorería |
| OF-10-20 | Valuar posiciones y elaborar el cierre de tesorería | Posiciones y liquidez y Operaciones de tesorería |

## 3. Reglas comunes del módulo

### 3.1. Autoridad sobre las posiciones

Posiciones y liquidez administra los recursos monetarios propios disponibles y comprometidos.

Operaciones de tesorería administra la financiación e inversiones propias, sus obligaciones y rendimientos.

Cambio de moneda administra cotizaciones y operaciones cambiarias.

Liquidaciones de tesorería administra instrucciones y resultados de entrega o recepción externa.

Contabilidad representa sus consecuencias patrimoniales y de resultados.

### 3.2. Separación respecto de los clientes

Fondos mantiene las cuentas bancarias de clientes.

Tenencias y valuación mantiene las inversiones de clientes.

Los recursos recibidos de clientes generan las obligaciones correspondientes del banco. Su utilización financiera no
elimina esas obligaciones.

Los activos en custodia pertenecientes a clientes no se consideran recursos propios disponibles para invertir o
liquidar.

### 3.3. Disponibilidad utilizable

Cada posición identifica moneda, ubicación, titularidad y momento de referencia.

La capacidad utilizable se determina como:

Disponibilidad confirmada menos compromisos e inmovilizaciones aplicables.

Los cobros esperados se incluyen en proyecciones, pero no se utilizan como dinero confirmado.

Las líneas de financiación aprobadas se muestran como capacidad contingente. Solo incrementan disponibilidades cuando se
confirma su disposición.

Si la posición resulta insuficiente:

- La capacidad para nuevos compromisos se informa en cero.

- El faltante se muestra separadamente.

- Los compromisos anteriores conservan seguimiento.

- No se incrementa ficticiamente el saldo.

### 3.4. Aprobación y separación de funciones

La concertación de operaciones propias, las excepciones, los ajustes manuales y los cambios de datos de liquidación
requieren preparación y aprobación separadas.

La liberación de una instrucción de pago corresponde a una función distinta de quien concertó la operación.

Las liquidaciones automáticas pueden utilizar procedimientos previamente aprobados, siempre que identifiquen origen,
límites y controles.

Institución administra las facultades. Riesgos administra límites y exposición.

### 3.5. Monedas y cálculos

Se mantienen posiciones separadas en pesos y dólares.

Las conversiones identifican:

- Par de monedas.

- Sentido de compra o venta.

- Importe de referencia.

- Cotización.

- Fecha y vigencia.

- Cargos y tributos.

Los cálculos intermedios conservan al menos doce decimales. Los importes finales utilizan la unidad mínima de la moneda
y redondeo al más cercano, elevando empates positivos.

Las operaciones propias a tasa fija utilizan interés simple Actual/365, salvo inversiones con flujos contractuales
específicos.

No existe capitalización automática de intereses.

### 3.6. Calendarios y cortes

La referencia temporal es America/Argentina/Buenos_Aires.

Se utilizan:

- Gestión ordinaria de tesorería en días hábiles de 09:00 a 18:00.

- Recepción de obligaciones durante todo el día.

- Cortes de compensación de los mecanismos definidos por sus módulos de origen.

- Liquidación externa conforme al calendario y fecha de cada obligación.

- Cierre de posición al finalizar cada día económico.

Una obligación recibida fuera del horario ordinario conserva su fecha y urgencia. No se transforma en obligación del día
siguiente por una decisión administrativa.

### 3.7. Recuperación y correcciones

Cada operación e instrucción tiene referencia única.

La repetición no duplica compromisos, pagos ni posiciones.

La falta de respuesta requiere consulta antes de reenviar, liberar recursos o registrar un incumplimiento definitivo.

Las correcciones conservan los hechos originales y se realizan por sus responsables.

---

## 4. OF-10-01. Registrar cuentas y ubicaciones de disponibilidades

### Propósito y responsable

Identificar dónde se mantienen los recursos propios del banco.

Responsable: Posiciones y liquidez.

### Información y condiciones previas

Se requiere entidad o ubicación, moneda, identificación, finalidad, responsables, capacidades y restricciones.

Comprende tesoro, cajas, dispositivos y cuentas propias en participantes simulados.

### Desarrollo

- Registrar la propuesta.

- Comprobar titularidad y participante.

- Definir actuaciones admitidas.

- Verificar datos de liquidación.

- Obtener aprobación.

- Habilitar.

- Incorporar saldo inicial respaldado.

### Reglas y decisiones

- Registrar una ubicación no crea dinero.

- El saldo inicial requiere un hecho de apertura identificado y su correspondencia contable.

- Las cuentas de clientes no se registran como cuentas propias externas.

- Los cambios de datos sensibles se verifican por una vía independiente de la solicitud que los modifica.

- Una ubicación con saldo o compromisos no se elimina.

- Su baja requiere traslado o tratamiento de posiciones.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se registra una caja nueva sin efectivo | Posición inicial cero. |
| Se incorpora capital inicial de la simulación | Existe hecho institucional y contable que lo respalda. |
| Se cambia el destino de pagos de una contraparte | Se verifica y aprueba antes de utilizarlo. |
| Se solicita baja con recursos | Permanece pendiente hasta tratarlos. |

## 5. OF-10-02. Registrar movimientos de recursos propios

### Propósito y responsable

Actualizar disponibilidades a partir de hechos comprobados.

Responsable: Posiciones y liquidez.

### Desarrollo

- Recibir el hecho y su referencia.

- Comprobar origen.

- Identificar moneda y ubicación.

- Registrar entrada, salida o traslado.

- Actualizar compromisos relacionados.

- Comunicar a conciliación y contabilidad.

### Reglas y decisiones

- Una orden de pago no equivale a salida confirmada.

- Una expectativa de cobro no equivale a entrada.

- Los movimientos físicos proceden de Operaciones de efectivo o del traslado interno correspondiente.

- Los externos proceden de Liquidaciones de tesorería.

- Un traslado entre posiciones propias no genera ingreso o gasto por su principal.

- Las duplicaciones se detectan mediante la referencia de origen.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo 1.000 y pago confirmado 200 | Saldo resultante 800. |
| Cobro esperado de 300 sin confirmar | No incrementa el saldo confirmado. |
| Se repite una confirmación | Un solo movimiento. |
| Se trasladan 100 entre cuentas propias | Cambia la ubicación, no el total de recursos por ese traslado. |

## 6. OF-10-03. Trasladar y verificar efectivo

### Propósito y responsable

Mover efectivo entre ubicaciones propias y comprobar sus existencias.

Responsable: Posiciones y liquidez.

### Desarrollo

- Registrar origen, destino, moneda e importe.

- Comprobar capacidad del origen.

- Aprobar el traslado.

- Registrar salida y efectivo en tránsito.

- Confirmar recepción.

- Resolver diferencias.

- Verificar existencias mediante arqueo.

### Reglas y decisiones

- El dinero en tránsito no permanece disponible en origen ni se considera recibido en destino.

- La salida y recepción conservan una misma referencia de traslado.

- La persona que verifica el arqueo es distinta de quien tiene a cargo la existencia revisada.

- Las diferencias se investigan antes de ajustar.

- Los ajustes manuales requieren aprobación separada.

- Un faltante no se imputa a clientes sin evidencia de una operación que lo justifique.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Origen 500; traslado de 100 aún no recibido | Origen utilizable 400 y tránsito 100. |
| Destino confirma 100 | Se extingue tránsito y aumenta destino en 100. |
| Destino recibe 90 | Diferencia de 10 identificada, sin crear efectivo. |
| Arqueo distinto del registro | Se abre investigación y se conserva evidencia. |

## 7. OF-10-04. Comprometer y liberar disponibilidades

### Propósito y responsable

Reservar recursos propios para obligaciones identificadas.

Responsable: Posiciones y liquidez.

### Información y condiciones previas

Se requiere obligación, moneda, importe, fecha, ubicación o conjunto de recursos admitidos y condición de cancelación.

### Desarrollo

- Comprobar referencia y límites.

- Determinar recursos utilizables.

- Registrar compromiso.

- Confirmar cobertura.

- Consumir al liquidar o liberar con fundamento.

- Mantener remanentes.

### Reglas y decisiones

- Comprobar y comprometer se realiza como una única decisión.

- Las obligaciones concurrentes no utilizan los mismos recursos.

- El compromiso no modifica el saldo confirmado hasta la salida.

- La liberación no constituye un ingreso.

- Un compromiso incierto no se libera por vencimiento administrativo.

- Si se reasigna a otra ubicación, se confirma la nueva cobertura antes de liberar la anterior.

- Una obligación en dólares no se cubre con pesos sin una conversión identificada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Disponibilidad 100 y dos compromisos de 80 | No se confirman ambos íntegramente. |
| Reserva propia 40 sobre saldo 100 | Saldo 100; capacidad nueva 60. |
| Se paga 40 | Saldo 60 y compromiso consumido. |
| Se cancela sin pago | Saldo 100 y compromiso liberado. |

## 8. OF-10-05. Proyectar flujos y necesidades de liquidez

### Propósito y responsable

Anticipar recursos necesarios por moneda y fecha.

Responsable: Posiciones y liquidez.

### Fuentes

Se consideran:

- Vencimientos de depósitos.

- Desembolsos y cobros de préstamos.

- Pagos y transferencias externos.

- Compensación de tarjetas y cheques.

- Órdenes de inversión y eventos.

- Operaciones propias.

- Necesidades de efectivo.

- Restricciones y compromisos existentes.

### Desarrollo

- Fijar fecha de referencia.

- Obtener saldos y compromisos.

- Reunir flujos.

- Clasificar certeza y vencimiento.

- Evitar duplicaciones.

- Calcular escenarios.

- Informar faltantes y excedentes.

### Reglas y decisiones

- Cada flujo identifica operación de origen.

- Una obligación agregada de compensación no se suma otra vez a sus componentes.

- Se distinguen flujos contractuales, compromisos firmes y estimaciones.

- Los flujos inciertos se muestran por separado.

- Se calculan horizontes intradiario, siguiente hábil, siete días y treinta días.

- La proyección no ejecuta financiación ni modifica obligaciones.

- Los supuestos de estrés pertenecen a Riesgos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo 100, pagos firmes 80 y cobros firmes 30 | Posición proyectada 50, con sus fechas. |
| Los 80 ya integran una compensación neta informada | No se suman dos veces. |
| Cobro probable de 40 | Se muestra como estimación, no como dinero confirmado. |
| Falta información de un módulo | Se identifica la limitación de la proyección. |

## 9. OF-10-06. Resolver faltantes y excedentes de liquidez

### Propósito y responsable

Coordinar acciones para atender obligaciones o colocar recursos sin comprometer su cumplimiento.

Responsable: Posiciones y liquidez.

### Desarrollo

- Identificar necesidad por moneda y fecha.

- Distinguir obligaciones firmes de decisiones todavía cancelables.

- Evaluar recursos libres y movimientos internos.

- Proponer financiación, venta, cobro o cambio.

- Obtener decisiones autorizadas.

- Seguir ejecución y actualizar proyección.

### Reglas y decisiones

- Se priorizan obligaciones según vencimiento, fundamento y condiciones de liquidación.

- Se suspenden nuevas colocaciones discrecionales incompatibles con obligaciones firmes.

- No se utiliza patrimonio de clientes en custodia como recurso propio.

- No se supone obtenida una financiación todavía no liquidada.

- Una venta de inversión considera precio y plazo de liquidación.

- Las excepciones a límites requieren decisión de Riesgos y autoridad suficiente.

- Un faltante no autoriza a borrar una acreditación de cliente ya confirmada.

- Si no puede cumplirse una obligación, se informa al responsable y se activa su tratamiento de incumplimiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Faltan 20 para una liquidación firme | Se gestionan recursos y se conserva la obligación. |
| Existe inversión que liquida mañana y el pago vence hoy | No se considera disponible hoy. |
| Hay excedente, pero vencimiento firme próximo | La colocación respeta esa necesidad. |
| Financiación solicitada sin respuesta | No aumenta disponibilidad. |

## 10. OF-10-07. Concertar financiación propia

### Propósito y responsable

Obtener recursos para el banco mediante acuerdos con contrapartes simuladas.

Responsable: Operaciones de tesorería.

### Información y condiciones previas

Se requiere contraparte, capital, moneda, plazo, tasa, vencimientos, garantías y destino.

### Desarrollo

- Comprobar contraparte y límites.

- Obtener condiciones.

- Calcular flujos y costo.

- Obtener aprobación.

- Formalizar con Contratos.

- Registrar operación.

- Solicitar liquidación.

- Reconocer recursos recibidos y deuda.

### Reglas y decisiones

- La aprobación no acredita dinero.

- La financiación recibida es una obligación del banco.

- Se admiten financiación a tasa fija y líneas interbancarias simuladas.

- Cada utilización de una línea tiene identidad propia.

- El interés se calcula sobre capital efectivamente recibido.

- No se capitalizan intereses automáticamente.

- Las garantías propias se identifican sin utilizar instrumentos de clientes.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se reciben 1.000 | Aumenta disponibilidad y se reconoce deuda por 1.000. |
| Acuerdo firmado sin entrega | No se informa dinero disponible. |
| Capital 1.000, tasa 36,5 %, diez días | Interés 10 antes de otros conceptos. |
| Se repite la liquidación | Una recepción y una deuda correspondientes. |

## 11. OF-10-08. Concertar inversiones propias

### Propósito y responsable

Colocar recursos del banco dentro de sus límites.

Responsable: Operaciones de tesorería.

### Modalidades admitidas

- Colocaciones a plazo en contrapartes simuladas.

- Préstamos interbancarios simples.

- Compra y venta de títulos de renta fija sin apalancamiento.

### Desarrollo

- Identificar objetivo y recursos.

- Comprobar límites de liquidez y contraparte.

- Determinar condiciones.

- Obtener aprobación.

- Formalizar o registrar ejecución.

- Solicitar liquidación.

- Mantener posición y flujos.

### Reglas y decisiones

- Las posiciones propias se separan de inversiones de clientes.

- Se comprometen recursos antes de instruir la entrega.

- Las órdenes de títulos utilizan cantidad y precio límite.

- No se admiten ventas en corto ni derivados en estas modalidades.

- Los títulos se cotizan por cien unidades nominales.

- El interés corrido se distingue del precio.

- Una colocación no se considera realizada por una mera propuesta aceptada internamente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compra de 1.000 nominales a precio 95 | Precio principal 950, con interés corrido y cargos separados. |
| La colocación dejaría sin cobertura una obligación firme | No se autoriza sin resolver la necesidad. |
| Orden parcialmente ejecutada | Se conservan cantidad realizada y remanente. |
| Posición del cliente en el mismo título | No se suma como inversión propia. |

## 12. OF-10-09. Administrar rendimientos y vencimientos propios

### Propósito y responsable

Mantener obligaciones y derechos de operaciones propias.

Responsable: Operaciones de tesorería.

### Desarrollo

- Generar calendario contractual.

- Calcular devengamientos.

- Registrar derechos y obligaciones.

- Preparar cobros y pagos.

- Solicitar liquidación.

- Confirmar resultados.

- Actualizar posición.

### Reglas y decisiones

- Los calendarios conservan anclaje.

- El devengamiento no equivale a cobro.

- La amortización reduce capital; el interés reconoce rendimiento o costo.

- Los títulos conservan su método contractual de cupón.

- Los pagos recibidos se aplican a su operación, sin duplicar ingresos ya devengados.

- Los incumplimientos se informan a Riesgos.

- Las renovaciones requieren una nueva decisión o mecanismo expresamente pactado.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Interés devengado 10 y cobro posterior 10 | Un rendimiento reconocido y su cancelación, sin duplicarlo. |
| Vence una colocación y no paga la contraparte | Derecho pendiente e incidencia de riesgo. |
| Se cobra amortización parcial | Disminuye el capital correspondiente. |

## 13. OF-10-10. Modificar y cancelar operaciones propias

### Propósito y responsable

Aplicar cambios y finalizar acuerdos sin perder obligaciones.

Responsable: Operaciones de tesorería.

### Desarrollo

- Registrar causa.

- Comprobar contrato y avance.

- Determinar saldo y compensaciones.

- Evaluar efecto en liquidez y riesgo.

- Obtener aprobación y acuerdo cuando corresponda.

- Coordinar liquidación.

- Registrar resultado.

### Reglas y decisiones

- Cambiar condiciones requiere fundamento contractual.

- La cancelación anticipada aplica los conceptos expresamente pactados.

- Una propuesta no modifica la operación vigente.

- Una instrucción externa incierta se consulta antes de cancelar recursos.

- Las pérdidas o ganancias de salida se identifican.

- No se cierra una operación con entregas o cobros sin tratamiento.

- Las garantías se liberan después de comprobar su procedencia.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se negocia otra tasa, pero no se acepta | Continúa la anterior. |
| Cancelación con costo pactado | Se muestra principal, interés y costo por separado. |
| Hay liquidación incierta | No se informa cierre definitivo. |

## 14. OF-10-11. Administrar cotizaciones

### Propósito y responsable

Mantener referencias de cambio para finalidades diferenciadas.

Responsable: Cambio de moneda.

### Clases

- Cotización de referencia.

- Cotización contable de cierre.

- Cotización comercial ejecutable.

- Cotización informativa.

### Desarrollo

- Recibir o preparar el valor.

- Identificar fuente, par, sentido y momento.

- Comprobar coherencia.

- Obtener aprobación cuando sea carga manual.

- Publicar vigencia.

- Conservar historia y correcciones.

### Reglas y decisiones

- Una cotización informativa no puede ejecutarse como oferta.

- Compra y venta se expresan desde la perspectiva del banco.

- El precio de compra no supera el de venta para el mismo alcance y momento.

- Las cotizaciones manuales requieren preparación y aprobación separadas.

- Una referencia faltante no se reemplaza silenciosamente.

- La cotización contable de cierre se identifica por fecha y no modifica la moneda original de las posiciones.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compra 1.000 y venta 1.020 pesos por dólar | Sentidos y unidad quedan explícitos. |
| Compra 1.030 y venta 1.020 para igual alcance | Se rechaza por inconsistencia. |
| Falta cotización del día | No se inventa un valor definitivo. |
| Se corrige una fuente | Se conserva el valor anterior y el fundamento. |

## 15. OF-10-12. Cotizar una operación cambiaria

### Propósito y responsable

Proponer un intercambio concreto.

Responsable: Cambio de moneda.

### Información y condiciones previas

Se requiere participante, monedas, sentido, importe, cuentas, finalidad y fecha de liquidación.

### Desarrollo

- Comprobar producto, facultades y controles.

- Consultar disponibilidad y posición propia.

- Determinar cotización.

- Calcular contraprestación.

- Desglosar cargos y tributos.

- Emitir propuesta con vencimiento.

### Reglas y decisiones

- La propuesta comercial ejecutable tiene una vigencia de sesenta segundos.

- La aceptación debe llegar antes de su vencimiento.

- La aceptación fija las condiciones, pero la ejecución exige confirmar ambos recursos.

- Si no puede ejecutarse antes de asumir el compromiso, se informa el rechazo sin cambiar la tasa aceptada por otra.

- Una nueva tasa requiere nueva aceptación.

- Las cotizaciones para operaciones con clientes respetan el tratamiento del producto de origen, incluidos pagos de
  tarjeta.

- No se agrega un cargo de cambio que el producto no admita.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Banco vende USD 100 a 1.020 | Principal en pesos 102.000, con conceptos adicionales separados. |
| Aceptación después de sesenta segundos | Se requiere otra propuesta. |
| Cambia la tasa después de aceptar y comprometer válidamente | No se sustituye unilateralmente la pactada. |
| Falta disponibilidad en una moneda | No se promete ejecución completa. |

## 16. OF-10-13. Ejecutar cambio de moneda

### Propósito y responsable

Cumplir ambas partes del intercambio.

Responsable: Cambio de moneda.

### Desarrollo

- Comprobar propuesta y aceptación.

- Revisar restricciones efectivas.

- Comprometer recursos del cliente mediante Fondos.

- Comprometer recursos propios mediante Posiciones y liquidez.

- Ejecutar movimientos relacionados.

- Confirmar resultado completo.

- Registrar cambios de posición y hechos contables.

### Reglas y decisiones

- Una operación interna no se informa completa con una sola moneda entregada.

- Las reservas no se consumen dos veces.

- Los importes originales y resultantes permanecen identificados.

- Si existe entrega externa, Liquidaciones de tesorería administra su cumplimiento.

- Las operaciones propias también requieren límites y facultades.

- No se utilizan activos de clientes para completar faltantes propios.

- Los resultados inciertos conservan cobertura.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cliente compra USD 100 por 102.000 pesos | Débito en pesos y crédito en dólares correspondientes, sin duplicación. |
| Una parte falla antes de ejecutar | No se informa intercambio completo. |
| Se pierde la respuesta | Se consulta la misma operación. |
| Se repite la instrucción | Un solo intercambio. |

## 17. OF-10-14. Resolver incidencias cambiarias

### Propósito y responsable

Tratar ejecuciones parciales, inciertas o incorrectas.

Responsable: Cambio de moneda.

### Desarrollo

- Identificar operación y efectos.

- Consultar participantes.

- Comprobar qué se ejecutó.

- Determinar cumplimiento, restitución o corrección.

- Obtener aprobación cuando sea manual.

- Ejecutar y conciliar.

- Comunicar resultado.

### Reglas y decisiones

- La restitución de una operación errónea utiliza importes y fundamento del ajuste, sin simular una compra nueva del
  cliente.

- Una operación nueva voluntaria utiliza cotización actual y aceptación.

- No se traslada automáticamente al cliente una pérdida causada por error del banco.

- Los cargos y tributos se revisan junto con el principal.

- Una cobertura incierta no se libera por vencimiento de la cotización.

- La corrección no borra las entregas anteriores.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se debitó en pesos y no se acreditó moneda, resultado comprobado | Se completa o restituye mediante actuación vinculada. |
| El cliente decide vender después lo comprado correctamente | Nueva operación con nuevas condiciones. |
| Se repite una restitución | No se devuelve dos veces. |

## 18. OF-10-15. Recibir y validar obligaciones de liquidación

### Propósito y responsable

Admitir obligaciones externas con causa y composición verificables.

Responsable: Liquidaciones de tesorería.

### Información necesaria

- Operación o ciclo de origen.

- Participante.

- Importe o cantidad.

- Moneda o instrumento.

- Sentido de entrega o recepción.

- Fecha.

- Mecanismo.

- Datos de destino.

- Condiciones de firmeza y cancelación.

### Desarrollo

- Identificar origen.

- Comprobar referencia.

- Validar composición.

- Revisar datos de liquidación.

- Aceptar o informar inconsistencias.

- Incorporar a la planificación.

### Reglas y decisiones

- La misma obligación no se recibe como nueva por repetición.

- Una obligación neta no se suma a sus operaciones componentes.

- Tesorería no cambia el resultado de negocio para ajustar una obligación.

- La corrección procede del módulo de origen.

- Una instrucción con destino modificado requiere verificación.

- Una obligación legítima sin fondos se admite como pendiente de cobertura, no se elimina.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Ciclo de compensación neta por 30 | Una obligación por 30 con detalle. |
| Se reciben también sus componentes | No se duplican. |
| Falta moneda | No se instruye el pago. |
| Hay obligación válida sin recursos | Se registra faltante y se gestiona cobertura. |

## 19. OF-10-16. Preparar e instruir liquidaciones

### Propósito y responsable

Preparar entregas y cobros dentro de sus condiciones.

Responsable: Liquidaciones de tesorería.

### Desarrollo

- Comprobar obligación y vencimiento.

- Seleccionar ubicación de recursos.

- Obtener compromiso de disponibilidad.

- Verificar contraparte y destino.

- Liberar la instrucción mediante control correspondiente.

- Enviar.

- Registrar constancia y seguimiento.

### Reglas y decisiones

- El acuse de envío no demuestra pago.

- La persona que concertó una operación propia no libera por sí sola su liquidación.

- Las instrucciones automáticas conservan la regla aprobada.

- La neteabilidad se determina por el acuerdo del mecanismo, no por conveniencia ocasional.

- No se compensa entre monedas.

- La entrega contra pago mantiene relacionadas las obligaciones de fondos e instrumentos.

- Después del envío no se libera cobertura sin comprobar el resultado.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Disponibilidad insuficiente | Se mantiene pendiente de cobertura, sin envío ficticio. |
| Instrucción repetida | Se recupera su referencia externa. |
| Datos de destino no verificados | No se libera. |
| Compra de título con entrega contra pago | Se relacionan dinero e instrumento. |

## 20. OF-10-17. Confirmar cobros y entregas externos

### Propósito y responsable

Reconocer lo efectivamente recibido o entregado.

Responsable: Liquidaciones de tesorería.

### Desarrollo

- Recibir confirmación.

- Validar participante y referencia.

- Comprobar importe, moneda y fecha.

- Registrar cumplimiento.

- Actualizar posición propia.

- Informar al módulo de origen.

- Comunicar hechos contables.

### Reglas y decisiones

- Se distingue aceptación de instrucción de liquidación efectiva.

- Los cobros esperados no se convierten en saldo por una estimación.

- Las confirmaciones parciales mantienen remanentes.

- El mensaje duplicado no produce otro movimiento.

- El resultado externo se conserva aunque llegue fuera del horario ordinario.

- Los instrumentos recibidos se confirman con el servicio que administra la posición correspondiente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cobro confirmado 100 | Aumenta disponibilidad por 100 una vez. |
| Confirmación solo de recepción de instrucción | No se declara cobro. |
| Pago parcial 60 de obligación 100 | Cumplido 60 y pendiente 40. |
| Confirmación tardía | Se integra conservando sus fechas reales. |

## 21. OF-10-18. Gestionar liquidaciones parciales o incumplidas

### Propósito y responsable

Resolver obligaciones no cumplidas completamente.

Responsable: Liquidaciones de tesorería.

### Desarrollo

- Detectar falta o incertidumbre.

- Consultar resultado.

- Conservar compromisos.

- Informar a Posiciones y liquidez y al origen.

- Determinar cumplimiento posterior o corrección.

- Registrar costos y consecuencias comprobados.

- Confirmar resolución.

### Reglas y decisiones

- La parcialidad solo se instruye cuando el mecanismo la admite.

- No se paga una obligación completa nuevamente por haber recibido una confirmación parcial.

- Ante incertidumbre se consulta al detectarla, a los cinco minutos y a los treinta minutos.

- Si persiste, se abre investigación y revisión por jornada operativa.

- Estos tiempos no autorizan liberar recursos ni declarar extinción.

- Un incumplimiento de contraparte se comunica a Riesgos.

- Una compensación o costo requiere fundamento; no se calcula arbitrariamente.

- El cliente que ya cumplió su obligación no vuelve a pagar porque el banco falló frente a un tercero.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Pagados 60 de 100 | Solo quedan 40 pendientes. |
| Respuesta perdida después de enviar | Se consulta antes de reenviar. |
| Contraparte no paga al vencimiento | Derecho pendiente e incidencia. |
| Falla liquidación de un pago de cliente confirmado | Se resuelve entre participantes, sin duplicar el cobro al cliente. |

## 22. OF-10-19. Conciliar disponibilidades y liquidaciones

### Propósito y responsable

Comprobar registros propios frente a cuentas, custodios y participantes externos.

Responsable: Liquidaciones de tesorería.

### Desarrollo

- Fijar corte comparable.

- Obtener movimientos y saldos externos.

- Relacionar referencias.

- Comparar importes, monedas y fechas.

- Clasificar diferencias.

- Consultar y resolver.

- Confirmar posiciones.

### Reglas y decisiones

- Se realiza conciliación diaria y ante resultados inciertos.

- Los tránsitos se distinguen de faltantes definitivos.

- No se modifica una posición para forzar coincidencia.

- Los ajustes manuales requieren aprobación separada.

- Las diferencias de origen se derivan al responsable.

- La conciliación externa no sustituye la conciliación contable de Cierres contables.

- Una diferencia sin causa permanece identificada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Externo registra pago 100 e interno sigue pendiente | Se recupera y confirma el pago original. |
| Los cortes son distintos | Se homogeneizan antes de concluir faltante. |
| Se detecta débito externo desconocido | Investigación, sin imputación automática al cliente. |
| Se regulariza una diferencia | Se conserva evidencia y aprobación. |

## 23. OF-10-20. Valuar posiciones y elaborar el cierre de tesorería

### Propósito y responsables

Determinar posiciones, resultados y pendientes al corte.

Posiciones y liquidez consolida disponibilidades y compromisos. Operaciones de tesorería valúa operaciones e inversiones
propias.

### Desarrollo

- Fijar corte.

- Comprobar movimientos y conciliaciones.

- Obtener capital, intereses y posiciones propias.

- Aplicar cotizaciones y precios.

- Separar resultados realizados y variaciones de valuación.

- Identificar pendientes y excesos.

- Entregar información a Riesgos y Contabilidad.

- Conservar el cierre operativo.

### Reglas y decisiones

- El cierre de tesorería no cierra el período contable.

- Las posiciones monetarias conservan moneda original.

- Las conversiones de presentación utilizan la referencia contable de cierre.

- Las inversiones propias distinguen costo, rendimiento y valor.

- Los precios faltantes se identifican; no se reemplazan por cero.

- Un cierre con información incompleta se informa como pendiente de completar, no como posición definitiva.

- Las diferencias de valuación no modifican por sí solas contratos.

- Las versiones posteriores conservan el corte y las causas de rectificación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| USD 100 y cotización contable 1.100 | Equivalente informativo de 110.000 pesos, manteniendo USD 100. |
| Título sin precio del día | Se informa referencia anterior y su antigüedad, sin valoración definitiva inventada. |
| Hay liquidación incierta | Se muestra como pendiente y con su compromiso. |
| Se repite el cierre con mismos datos y versiones | Resultado reproducible. |

## 24. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Disponibilidades propias y efectivo | Posiciones y liquidez |
| Compromisos y proyecciones | Posiciones y liquidez |
| Financiación e inversiones propias | Operaciones de tesorería |
| Cotizaciones y operaciones de cambio | Cambio de moneda |
| Instrucciones y resultados externos | Liquidaciones de tesorería |
| Conciliación de cuentas externas | Liquidaciones de tesorería |
| Composición de obligaciones de pagos y tarjetas | Módulo operativo de origen |
| Dinero disponible de clientes | Fondos |
| Tenencias de clientes | Tenencias y valuación |
| Políticas, límites y exposición | Riesgos |
| Restricciones | Restricciones y servicios afectados |
| Acuerdos contractuales | Contratos |
| Asientos, conciliación y cierre contable | Contabilidad |
| Facultades y aprobaciones humanas | Institución y Gestión administrativa |
| Evidencias y comunicaciones | Documentos y comprobantes y Comunicaciones |

Cada posición conserva la relación entre origen, compromiso, entrega, recepción y conciliación. La liquidez se
administra mediante recursos comprobados y decisiones identificadas, sin convertir proyecciones en dinero ni trasladar
incumplimientos internos a operaciones de clientes ya cumplidas.
