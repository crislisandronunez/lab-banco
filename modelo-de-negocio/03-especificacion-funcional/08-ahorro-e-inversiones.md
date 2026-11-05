# Operaciones funcionales — Módulo 8. Ahorro e inversiones

## 1. Propósito y alcance

Este documento define las operaciones para constituir y administrar depósitos a plazo, ejecutar inversiones de clientes
y mantener sus tenencias, rendimientos y movimientos de custodia.

Comprende los microservicios Depósitos a plazo, Instrumentos de inversión, Órdenes de inversión, y Tenencias y
valuación.

Se distinguen dos relaciones económicas:

- Un depósito a plazo genera una obligación de devolución del banco.

- Una inversión genera una tenencia o derecho sobre un instrumento cuyo valor puede variar.

Las inversiones propias del banco pertenecen a Tesorería.

Los administradores, mercados, intermediarios y custodios externos funcionan como participantes simulados.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-08-01 | Preparar una constitución de depósito a plazo | Depósitos a plazo |
| OF-08-02 | Constituir el depósito | Depósitos a plazo |
| OF-08-03 | Calcular intereses y actualizaciones | Depósitos a plazo |
| OF-08-04 | Administrar instrucciones de vencimiento | Depósitos a plazo |
| OF-08-05 | Pagar un depósito al vencimiento | Depósitos a plazo |
| OF-08-06 | Renovar un depósito | Depósitos a plazo |
| OF-08-07 | Precancelar un depósito admitido | Depósitos a plazo |
| OF-08-08 | Corregir y consultar depósitos | Depósitos a plazo |
| OF-08-09 | Definir y habilitar instrumentos de inversión | Instrumentos de inversión |
| OF-08-10 | Modificar o suspender instrumentos | Instrumentos de inversión |
| OF-08-11 | Constituir y mantener una cuenta de inversión | Tenencias y valuación |
| OF-08-12 | Recibir y validar una orden | Órdenes de inversión |
| OF-08-13 | Comprometer fondos o instrumentos | Órdenes de inversión |
| OF-08-14 | Ejecutar suscripciones de fondos comunes | Órdenes de inversión |
| OF-08-15 | Ejecutar rescates de fondos comunes | Órdenes de inversión |
| OF-08-16 | Ejecutar compras y ventas de títulos | Órdenes de inversión |
| OF-08-17 | Cancelar o vencer órdenes | Órdenes de inversión |
| OF-08-18 | Coordinar la liquidación de inversiones | Órdenes de inversión |
| OF-08-19 | Registrar movimientos y restricciones de tenencias | Tenencias y valuación |
| OF-08-20 | Valuar posiciones | Tenencias y valuación |
| OF-08-21 | Procesar eventos y rendimientos | Instrumentos de inversión y Tenencias y valuación |
| OF-08-22 | Conciliar órdenes y custodia | Órdenes de inversión y Tenencias y valuación |
| OF-08-23 | Corregir operaciones y posiciones | Servicio responsable del efecto |
| OF-08-24 | Cerrar una cuenta de inversión | Tenencias y valuación |
| OF-08-25 | Trasladar instrumentos entre custodias | Tenencias y valuación |

## 3. Reglas comunes del módulo

### 3.1. Participantes y contratación

Las operaciones requieren participantes identificados, facultades suficientes y acuerdos válidos.

La cotitularidad y la actuación conjunta se aplican conforme al contrato.

La cuenta monetaria utilizada no determina por sí sola la titularidad del depósito o de la inversión.

Las verificaciones de adecuación, riesgo y restricciones pertenecen a los servicios especializados del módulo 11.

Aceptar una advertencia no permite omitir un control obligatorio ni ejecutar una inversión incompatible con el perfil
admitido.

### 3.2. Fondos y tenencias

Fondos administra el dinero de cuentas bancarias.

Tenencias y valuación distingue:

| Concepto | Significado |
| --- | --- |
| Cantidad registrada | Unidades incorporadas a la posición conforme a hechos confirmados. |
| Disponible | Unidades que pueden comprometerse para nuevas actuaciones. |
| Comprometida | Unidades reservadas para venta, rescate o traslado. |
| Restringida | Unidades cuya utilización está limitada por una medida. |
| Pendiente de liquidación | Derechos o entregas de operaciones ejecutadas todavía no liquidadas. |
| En tránsito | Unidades cuya salida fue confirmada y cuya recepción final sigue pendiente. |

Una misma unidad no se considera disponible en dos posiciones ni respalda dos operaciones incompatibles.

No se suman afectaciones superpuestas como si fueran cantidades independientes.

La simulación no admite:

- Ventas en descubierto.

- Inversión financiada con descubierto.

- Utilización anticipada de cobros pendientes.

- Utilización de activos de clientes para obligaciones propias del banco.

### 3.3. Modalidades de depósito

Se ofrecen:

| Modalidad | Moneda | Plazo mínimo | Salida anticipada |
| --- | --- | --- | --- |
| Tradicional a tasa fija | Pesos o dólares | Treinta días corridos | No |
| Precancelable nominal | Pesos | Ciento ochenta días corridos | Desde el día treinta |
| Indexado UVA | Pesos expresados en unidades | Noventa días corridos | No |
| UVA precancelable para personas humanas | Pesos expresados en unidades | Noventa días corridos | Desde el día treinta |
| UVA precancelable para personas jurídicas | Pesos expresados en unidades | Ciento ochenta días corridos | Desde el día noventa |

Las tasas comerciales y de salida anticipada quedan identificadas al constituir.

Los depósitos se instrumentan mediante constancias nominativas intransferibles. No se ofrece circulación de certificados
ni fraccionamiento del depósito.

### 3.4. Instrumentos de inversión

Se admiten:

- Fondos comunes abiertos con valor diario.

- Bonos y otros títulos de renta fija sin apalancamiento.

- Derechos económicos y eventos de esos instrumentos.

No se habilitan derivados, ventas en corto ni operaciones que requieran garantías de margen.

Cada instrumento identifica moneda, precisión, unidad de negociación, calendario, precio, flujos y mecanismo de
liquidación.

### 3.5. Precisión

Los cálculos intermedios conservan al menos doce decimales.

Los importes monetarios finales se redondean a la unidad mínima de su moneda, al más cercano, elevando los empates
positivos.

Las cantidades respetan la precisión del instrumento.

Cuando una compra por importe no utiliza todo el dinero:

- Se determina la cantidad sin exceder el máximo autorizado.

- Se conserva el residuo.

- Se devuelve o libera.

- No se reconoce como ingreso del banco.

Las reversiones utilizan importes y cantidades registrados.

### 3.6. Calendarios y horarios

La referencia temporal es America/Argentina/Buenos_Aires.

| Actuación | Convención |
| --- | --- |
| Constitución de depósitos | Días hábiles, de 09:00 a 17:00 |
| Recepción de órdenes de inversión | Todos los días |
| Corte de fondos comunes | 15:00 de días hábiles |
| Negociación de títulos | Días de mercado, de 11:00 a 17:00 |
| Liquidación ordinaria de inversiones | Siguiente día hábil del calendario correspondiente |
| Recepción de solicitudes de traslado | Todos los días |
| Gestión ordinaria de traslados | Días hábiles, de 09:00 a 17:00 |

Las solicitudes fuera de horario permanecen pendientes. No se presentan como ejecutadas ni conservan una cotización
vencida.

### 3.7. Recuperación y evidencia

Cada actuación conserva referencia única y contenido identificable.

Una orden recibida no demuestra ejecución. Una ejecución no demuestra liquidación.

La falta de respuesta exige consultar antes de repetir o liberar recursos.

Ante incertidumbre externa se consulta al detectarla, a los cinco minutos y a los treinta minutos. Si persiste, se abre
investigación y revisión por jornada operativa.

Los ajustes manuales requieren preparación y aprobación separadas.

Los participantes reciben información que distingue capital, rendimiento, cargos, tributos y neto.

## 4. OF-08-01. Preparar una constitución de depósito a plazo

Propósito: determinar condiciones para aceptación.

Responsable: Depósitos a plazo.

### Información y desarrollo

Producto, capital, moneda, plazo, titulares, cuenta de origen y destino.

- Comprobar elegibilidad y facultades.

- Obtener oferta.

- Determinar constitución y vencimiento.

- Calcular rendimiento o actualización.

- Presentar condiciones y renovación.

- Obtener aceptación contractual.

### Reglas

- El plazo comienza en la constitución efectiva.

- La actualización futura de un indexado no se presenta como conocida.

- Se informa la modalidad de precancelación.

- Una propuesta vencida se actualiza antes de afectar fondos.

- Un cambio de fecha que altere tasa, plazo o importe requiere revisar la aceptación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Tradicional solicitado por veinte días | No corresponde a la modalidad ofrecida. |
| Depósito UVA | Se muestran pesos, unidades y regla de actualización. |
| Constitución trasladada al siguiente hábil | Se revisan fechas y condiciones afectadas. |

## 5. OF-08-02. Constituir el depósito

Propósito: transformar fondos disponibles en una obligación a plazo.

Responsable: Depósitos a plazo.

### Desarrollo

- Comprobar contrato y condiciones.

- Reservar capital en Fondos.

- Crear referencia de constitución.

- Confirmar débito y reconocimiento del depósito.

- Registrar fechas, tasa y unidades.

- Emitir constancia.

- Informar a Contabilidad y Tesorería.

### Reglas

- El capital no permanece disponible en la cuenta de origen.

- La constitución requiere afectación monetaria comprobada.

- Un débito confirmado con constitución pendiente conserva obligación de completar o restituir.

- Se utilizan fondos propios; no descubierto.

- La repetición no crea otro depósito.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo 1.000 y constitución 600 | Disponibilidad restante 400 y depósito 600. |
| Débito incierto | Consulta antes de repetir. |
| Reserva rechazada | No se informa constitución. |

## 6. OF-08-03. Calcular intereses y actualizaciones

Propósito: determinar rendimiento y obligación del banco.

Responsable: Depósitos a plazo.

### Depósitos nominales

Se utiliza interés simple Actual/365:

$I = P \times j \times \frac{d}{365}$

Donde:

- $P$: capital.

- $j$: tasa nominal anual.

- $d$: días reales.

El intervalo incluye la constitución y excluye la fecha hasta la cual se calcula.

Los años bisiestos mantienen divisor 365 y cuentan sus días reales.

### Depósitos indexados

Se conservan:

- Capital original en pesos $P$.

- Valor inicial de unidad $U_{0}$.

- Cantidad $Q = P/U_{0}$.

- Valor aplicable $U_{v}$.

$Capital\ actualizado = Q \times U_{v}$

$Interés = Capital\ actualizado \times j \times \frac{d}{365}$

La cantidad de unidades de un depósito es un dato de cálculo, no una tenencia negociable. Se conserva con la precisión
intermedia del módulo y no se trunca como una orden de inversión.

### Reglas

- Actualización e interés se informan separados.

- Los valores de unidad tienen serie, fecha y fuente.

- El devengamiento no equivale a pago.

- No se capitalizan intereses dentro del mismo depósito.

- Sin índice necesario no se liquida definitivamente.

- Las correcciones conservan valores anteriores y diferencias.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capital 1.000, tasa 36,5 % y treinta días | Interés 30. |
| Capital 1.000, unidad inicial 10 y final 11 | Cien unidades y capital actualizado 1.100. |
| Falta valor final | Liquidación pendiente. |
| Devengamiento repetido | Sin rendimiento duplicado. |

## 7. OF-08-04. Administrar instrucciones de vencimiento

Propósito: determinar el destino del capital y rendimiento.

Responsable: Depósitos a plazo.

### Modalidades

- Pago total.

- Renovación del capital y pago del rendimiento neto.

- Renovación de capital más rendimiento neto.

En depósitos indexados, renovar capital significa renovar el capital actualizado.

### Desarrollo

- Registrar selección y destino.

- Comprobar facultades.

- Informar alcance.

- Conservar versión.

- Confirmar cambios anteriores al procesamiento.

### Reglas

- Sin renovación aceptada se prepara pago total.

- La renovación automática requiere consentimiento.

- Puede modificarse la instrucción antes de aceptar la nueva constitución.

- La tasa nueva corresponde a la oferta vigente al renovar.

- Una modificación posterior no deshace una renovación ejecutada.

- Se revisan restricciones y destino antes del vencimiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| No existe renovación aceptada | Pago total. |
| Renovación solo de capital | Rendimiento neto pagado aparte. |
| Cambio válido antes de ejecutar | Se utiliza la última instrucción. |

## 8. OF-08-05. Pagar un depósito al vencimiento

Propósito: cumplir la devolución.

Responsable: Depósitos a plazo.

### Desarrollo

- Determinar vencimiento y pago programado.

- Calcular capital, actualización e intereses.

- Aplicar tributos y medidas.

- Solicitar acreditación.

- Comprobar resultado.

- Cancelar conceptos pagados.

- Emitir constancia.

### Reglas

- El vencimiento inhábil se paga el siguiente hábil.

- Se mantiene remuneración hasta esa fecha programada.

- En indexados se utiliza el valor correspondiente a esa fecha.

- Una demora adicional del banco genera compensación separada hasta la puesta a disposición.

- La compensación nominal utiliza capital e interés simple a la tasa contratada.

- En indexados, la compensación reproduce la diferencia del total que habría resultado al extender el cálculo hasta la
  puesta a disposición, descontando lo ya reconocido.

- No se paga dos veces el mismo período.

- Un destino inválido no extingue la obligación.

- Las medidas no convierten el dinero en ingreso del banco.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capital 1.000 e interés 30 | Acreditación 1.030 antes de tributos. |
| Cuenta destino cerrada | Obligación pendiente y destino por resolver. |
| Crédito confirmado con respuesta perdida | No se paga nuevamente. |
| Demora adicional atribuible al banco | Compensación identificada, sin alterar el original. |

## 9. OF-08-06. Renovar un depósito

Propósito: constituir otra colocación con fondos del vencimiento.

Responsable: Depósitos a plazo.

### Desarrollo

- Comprobar instrucción.

- Liquidar el depósito anterior.

- Determinar importe renovable.

- Obtener oferta vigente.

- Revisar elegibilidad y medidas.

- Constituir el nuevo depósito.

- Pagar remanentes.

- Vincular ambas operaciones.

### Reglas

- La renovación tiene identidad propia.

- No conserva automáticamente la tasa anterior.

- Los fondos permanecen comprometidos durante la transición.

- No se acreditan como libres y se reutilizan simultáneamente.

- Si no puede renovarse, se prepara pago total, salvo impedimento.

- La nueva fecha inicial es la constitución efectiva.

- No se duplica capital en Contabilidad.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capital 1.000 y rendimiento neto 30, renovación total | Nuevo capital 1.030. |
| Renovación nominal solo de capital | Nuevo depósito 1.000 y pago 30. |
| Producto retirado sin continuidad | Se paga, sin renovación ficticia. |

## 10. OF-08-07. Precancelar un depósito admitido

Propósito: ejercer la salida anticipada.

Responsable: Depósitos a plazo.

### Desarrollo

- Recibir petición.

- Comprobar modalidad y facultades.

- Determinar primera fecha admisible.

- Presentar liquidación.

- Obtener confirmación.

- Programar y ejecutar pago.

- Registrar cancelación.

### Reglas

- Solo se admite salida en modalidades precancelables.

- La cancelación es total.

- Se exige preaviso de cinco días hábiles.

- La fecha es la más tardía entre el mínimo de permanencia y el preaviso.

- Si resulta inhábil se ajusta al siguiente hábil.

- Si alcanza o supera el vencimiento se aplica vencimiento ordinario.

- La tasa de salida queda fijada al constituir.

- El interés de salida utiliza capital original y Actual/365 hasta el pago.

- En UVA precancelable no se suma actualización de vencimiento al rendimiento de salida.

- No se agrega otra penalidad.

- Se revisan medidas antes de pagar.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Tradicional no precancelable | No se promete retiro anticipado. |
| Nominal precancelable solicitado antes del día treinta | Pago no anterior al mínimo ni al preaviso. |
| UVA precancelable | Capital original e interés de salida, sin doble rendimiento. |
| Solicitud repetida | Una cancelación. |

## 11. OF-08-08. Corregir y consultar depósitos

Propósito: justificar posiciones y regularizar errores.

Responsable: Depósitos a plazo.

### Desarrollo

- Identificar depósito y corte.

- Reconstruir condiciones.

- Calcular diferencias.

- Obtener aprobación para ajustes manuales.

- Registrar corrección.

- Coordinar movimientos y contabilidad.

- Emitir constancia rectificativa.

### Reglas

- No se cambia la tasa histórica para ocultar un error.

- Se distinguen capital, actualización, devengamiento y pago.

- La pérdida de constancia no elimina el depósito.

- Corregir identidad no transfiere titularidad.

- Los recuperos de pagos excesivos requieren fundamento y no conceden descubierto.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Interés pagado de menos | Ajuste y pago complementario. |
| Índice corregido | Valor anterior y diferencia conservados. |
| Consulta histórica | Condiciones aplicables a ese momento. |

## 12. OF-08-09. Definir y habilitar instrumentos de inversión

Propósito: establecer qué puede operarse y bajo qué condiciones.

Responsable: Instrumentos de inversión.

### Información y desarrollo

Identidad, clase, emisor, moneda, precisión, unidad, riesgos, documentos, calendario, precio, flujos y liquidación.

- Registrar definición.

- Comprobar participantes.

- Revisar contrato y controles.

- Determinar operaciones admitidas.

- Definir condiciones financieras.

- Comprobar mecanismos externos.

- Obtener aprobación separada.

- Habilitar.

### Reglas

- El servicio bancario pertenece al catálogo; el instrumento concreto a este servicio.

- Los bonos cotizan por cien unidades nominales.

- Los fondos identifican clase y valor diario de cuotaparte.

- Cada bono define cupón, base temporal, fechas y amortizaciones.

- Los instrumentos sin precisión o liquidación definida no se habilitan.

- La habilitación no garantiza rentabilidad.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Mil nominales a precio 95 | Precio principal 950. |
| Fondo sin regla de cuotaparte | No se habilita. |
| Bono sin condiciones de cupón | No se habilita. |
| Cambio de denominación | Se conserva identidad. |

## 13. OF-08-10. Modificar o suspender instrumentos

Propósito: aplicar cambios manteniendo posiciones y derechos.

Responsable: Instrumentos de inversión.

### Desarrollo

- Registrar causa y efecto.

- Identificar órdenes y posiciones.

- Obtener revisión.

- Aplicar cambio.

- Comunicar tratamiento de pendientes.

### Reglas

- Se distingue suspensión de compras, rescates, traslados y liquidaciones.

- No se impiden actuaciones fuera del alcance.

- Las órdenes anteriores reciben resultado individual.

- Las tenencias no desaparecen.

- Los eventos continúan administrándose.

- Una falta de precio no se resuelve inventando valuación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se suspenden compras | No se bloquean automáticamente rendimientos. |
| Orden incierta | Se consulta antes de liberar. |
| Retiro comercial | Posiciones y derechos conservados. |

## 14. OF-08-11. Constituir y mantener una cuenta de inversión

Propósito: mantener titularidad y facultades sobre posiciones.

Responsable: Tenencias y valuación.

### Desarrollo

- Comprobar contrato y participantes.

- Registrar titularidad y actuación.

- Asignar referencia.

- Vincular cuentas monetarias.

- Coordinar identificación de custodia.

- Confirmar habilitación.

### Reglas

- Constituir no crea instrumentos.

- La cuenta monetaria no sustituye la titularidad de inversión.

- Cambiar titulares requiere formalización y tratamiento de posiciones.

- Un apoderado no adquiere propiedad.

- La referencia externa se conserva separada.

- La repetición no duplica el mismo acuerdo.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cuenta sin operaciones | Posiciones cero. |
| Constitución repetida | Misma cuenta. |
| Cambio de autorizado | No transfiere tenencias. |

## 15. OF-08-12. Recibir y validar una orden

Propósito: registrar una instrucción ejecutable.

Responsable: Órdenes de inversión.

### Desarrollo

- Identificar cuenta, instrumento y modalidad.

- Comprobar facultades.

- Revisar adecuación y restricciones.

- Presentar riesgos, cargos y condiciones.

- Obtener aceptación.

- Registrar vigencia y fecha de procesamiento.

### Modalidades

- Fondos: suscripción por importe.

- Fondos: rescate por cantidad o totalidad.

- Títulos: compra o venta de cantidad con precio límite.

Una solicitud de rescate por importe se transforma en cantidad estimada, informa esa variabilidad y requiere
confirmación antes del envío.

### Reglas

- Las órdenes de títulos vencen al finalizar la rueda elegida.

- Se admiten ejecuciones parciales de títulos.

- Las solicitudes de fondos se procesan por el importe o cantidad aceptados, sin parcialidad unilateral.

- No se garantiza el precio antes de ejecutar.

- No se vende una cantidad inexistente.

- Las órdenes fuera de horario identifican la próxima rueda o fecha efectiva.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compra limitada a 100 | No se ejecuta a 101. |
| Solicitud después del corte | Procesamiento en el siguiente hábil. |
| Rescate estimado por importe | No se promete neto exacto. |
| Unidades comprometidas para traslado | No están disponibles para vender. |

## 16. OF-08-13. Comprometer fondos o instrumentos

Propósito: asegurar recursos sin duplicarlos.

Responsable: Órdenes de inversión coordina; Fondos y Tenencias y valuación ejecutan las afectaciones.

### Desarrollo

- Calcular recursos máximos.

- Solicitar reserva monetaria o de unidades.

- Obtener confirmación.

- Vincular a la orden.

- Mantener hasta ejecución o cancelación comprobada.

### Reglas

- La compra reserva principal máximo y cargos autorizados.

- La venta y el rescate comprometen unidades.

- No se utiliza descubierto.

- Una ampliación exige recursos adicionales.

- El compromiso no transfiere titularidad.

- La incertidumbre impide liberar por mero vencimiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cien unidades y dos ventas de ochenta | No se aceptan ambas íntegramente. |
| Compra máxima 1.000 y cargos 10 | Reserva 1.010. |
| Fondos propios insuficientes | Sin financiación implícita. |

## 17. OF-08-14. Ejecutar suscripciones de fondos comunes

Propósito: adquirir cuotapartes.

Responsable: Órdenes de inversión.

### Desarrollo

- Comprobar orden y reserva.

- Enviar al administrador.

- Obtener aceptación.

- Recibir valor y asignación.

- Verificar cálculo.

- Coordinar liquidación.

- Confirmar tenencia.

### Reglas

- Antes de las 15:00 de un hábil se utiliza esa fecha.

- Desde las 15:00 se utiliza el siguiente hábil.

- El valor aplicable es el de cierre.

- La cantidad se determina sobre importe neto invertido.

- Se trunca a la precisión del instrumento.

- El administrador confirma cantidad y dinero efectivamente aplicado.

- El residuo se libera o restituye.

- Las unidades quedan disponibles después de liquidación confirmada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Neto 1.000 y cuotaparte 10 | Cien cuotapartes. |
| Valor final distinto de estimación | Se utiliza el de la fecha aplicable. |
| Falta confirmación | Orden pendiente, sin otra suscripción. |

## 18. OF-08-15. Ejecutar rescates de fondos comunes

Propósito: obtener el valor de cuotapartes.

Responsable: Órdenes de inversión.

### Desarrollo

- Comprobar disponibilidad.

- Comprometer unidades.

- Enviar.

- Obtener aceptación y valuación.

- Determinar neto.

- Coordinar salida y cobro.

- Acreditar lo confirmado.

### Reglas

- Se utiliza cantidad aceptada y valor de cierre de la fecha efectiva.

- El corte se aplica igual que en suscripciones.

- La liquidación ordinaria es el siguiente hábil.

- Una suspensión conserva solicitud y obligaciones.

- No se ofrecen rescates en especie.

- Una contingencia extraordinaria no se documenta como pago monetario inexistente.

- El envío por sí solo no acredita dinero.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cien cuotas a valor 12 | Bruto 1.200. |
| Rescate sin liquidación | Sin dinero disponible. |
| Valuación suspendida | Solicitud conservada e incidencia informada. |

## 19. OF-08-16. Ejecutar compras y ventas de títulos

Propósito: negociar instrumentos con precio límite.

Responsable: Órdenes de inversión.

### Desarrollo

- Validar orden y recursos.

- Enviar al mercado.

- Registrar ejecuciones.

- Comprobar precio y cantidad.

- Calcular importes y cargos.

- Coordinar liquidación.

- Tratar remanente.

### Reglas

- Cada ejecución identifica cantidad, precio y momento.

- El precio se distingue del interés corrido.

- La liquidación incorpora ambos cuando corresponda.

- El interés corrido utiliza las condiciones financieras del instrumento.

- La confirmación externa se contrasta con ese cálculo.

- La compra no supera el máximo autorizado.

- La venta no se realiza por debajo del límite.

- El remanente vence al finalizar la rueda.

- Una ejecución incierta no se reenvía como orden nueva.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Mil nominales a precio 95 | Precio 950. |
| Interés corrido 8 y cargos 2 | Liquidación de compra 960. |
| Orden mil y ejecución cuatrocientos | Ejecutado cuatrocientos y remanente seiscientos. |

## 20. OF-08-17. Cancelar o vencer órdenes

Propósito: detener remanentes cancelables.

Responsable: Órdenes de inversión.

### Desarrollo

- Registrar petición o vencimiento.

- Consultar ejecución.

- Solicitar cancelación.

- Obtener confirmación.

- Liberar recursos no utilizados.

- Comunicar.

### Reglas

- La petición no demuestra cancelación.

- Las ejecuciones anteriores permanecen.

- Una suscripción o rescate aceptado requiere confirmación del administrador para cancelarse.

- Los resultados tardíos se concilian.

- El vencimiento no libera ejecuciones inciertas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Ejecución parcial | Solo se cancela el remanente. |
| Cancelación incierta | Recursos comprometidos. |
| Ausencia de ejecuciones confirmada | Liberación correspondiente. |

## 21. OF-08-18. Coordinar la liquidación de inversiones

Propósito: completar dinero e instrumentos.

Responsable: Órdenes de inversión.

### Desarrollo

- Determinar obligaciones.

- Enviar a Liquidaciones de tesorería.

- Coordinar fondos y custodio.

- Recibir confirmaciones.

- Aplicar movimientos y posiciones.

- Conciliar diferencias.

### Reglas

- Los títulos utilizan entrega contra pago.

- Los fondos relacionan asignación o rescate con contraprestación.

- No se permite disponer de activos recibidos de forma incierta.

- Las ejecuciones parciales conservan liquidaciones propias.

- Un faltante de Tesorería no se oculta como rechazo original.

- Una operación ejecutada conserva sus obligaciones.

- Las confirmaciones no duplican dinero ni unidades.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Compra liquidada | Débito y unidades correspondientes. |
| Instrumentos recibidos sin pago confirmado | No se informa liquidación completa. |
| Respuesta perdida | Se consulta antes de repetir. |

## 22. OF-08-19. Registrar movimientos y restricciones de tenencias

Propósito: mantener cantidades y disponibilidad.

Responsable: Tenencias y valuación.

### Desarrollo

- Validar hecho de origen.

- Identificar instrumento, clase y titularidad.

- Registrar ingreso, salida o afectación.

- Actualizar disponibilidad.

- Confirmar.

- Comunicar hechos correspondientes.

### Reglas

- Todo movimiento tiene origen identificado.

- Una reserva no reduce cantidad registrada.

- Su consumo reduce cantidad y compromiso sin duplicación.

- Las restricciones conservan fundamento y prioridad.

- Una corrección administrativa no transfiere propiedad.

- Los traslados utilizan OF-08-25.

- Las cantidades en tránsito se muestran separadas de la custodia disponible.

- Una misma confirmación no incorpora unidades otra vez.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cien unidades y compromiso treinta | Registrado cien y disponible setenta. |
| Entrega de esas treinta | Registrado setenta y compromiso cero. |
| Se levanta una medida y subsiste otra | Continúa la afectación necesaria. |
| Recepción duplicada | Un ingreso. |

## 23. OF-08-20. Valuar posiciones

Propósito: determinar valor informativo.

Responsable: Tenencias y valuación.

### Desarrollo

- Obtener cantidades al corte.

- Seleccionar precio o cuotaparte.

- Comprobar fuente y fecha.

- Calcular valor.

- Identificar moneda y antigüedad.

- Informar.

### Reglas

- La valuación no es dinero disponible.

- Los fondos utilizan valor del administrador.

- Los bonos utilizan precio de cierre por cien nominales.

- El interés corrido se informa separado.

- Sin precio actual puede mostrarse la última referencia como histórica.

- La ausencia de precio no significa cero.

- La conversión informativa no ejecuta cambio.

- Las posiciones en tránsito se identifican para no sumarlas otra vez a origen y destino.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cien cuotas a valor 12 | Valuación 1.200. |
| Falta precio del día | Antigüedad informada. |
| Pesos y dólares | Separación o conversión informativa explícita. |

## 24. OF-08-21. Procesar eventos y rendimientos

Propósito: aplicar derechos económicos y cambios de instrumentos.

Responsables: Instrumentos de inversión define el evento; Tenencias y valuación determina sus efectos.

### Desarrollo

- Registrar evento y versión.

- Determinar fecha de derecho.

- Identificar posiciones alcanzadas.

- Calcular importes o unidades.

- Aplicar tributos.

- Coordinar cobro o entrega.

- Registrar lo confirmado.

### Reglas

- La fecha de derecho se distingue de la fecha de pago.

- Se consideran reglas de liquidación y custodia.

- El cupón se aplica una vez.

- La amortización reduce nominal y genera derecho monetario.

- Los fondos acumulativos no generan un dividendo ficticio adicional.

- Un derecho reconocido no demuestra cobro.

- Las fracciones conservan residuo o compensación identificada.

- Los traslados no duplican derechos sobre las mismas unidades.

- Si un custodio anterior recibe un pago correspondiente al titular, se mantiene una obligación residual de entregarlo.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Mil nominales y cupón 2 % | Derecho bruto 20. |
| Amortización 10 % | Nominal disminuye cien. |
| Evento repetido | Un efecto. |
| Rendimiento recibido después del traslado | Se identifica y paga al titular correspondiente. |

## 25. OF-08-22. Conciliar órdenes y custodia

Propósito: comprobar correspondencia con participantes externos.

Responsables: Órdenes de inversión concilia instrucciones y ejecuciones; Tenencias y valuación concilia cantidades,
traslados y derechos.

### Desarrollo

- Reunir cortes comparables.

- Relacionar referencias.

- Comparar cantidades, importes y resultados.

- Clasificar diferencias.

- Consultar.

- Determinar regularización.

- Comprobar efectos.

### Reglas

- Conciliación diaria y ante incertidumbres.

- Los pendientes no se confunden con faltantes definitivos.

- No se altera una cantidad para forzar coincidencia.

- Los ajustes manuales requieren aprobación separada.

- Las pérdidas mantienen investigación.

- La parte monetaria externa se coordina con Tesorería.

- Un traslado no se cierra por comprobar solo la salida.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Custodio cien e interno noventa | Investigación antes de agregar diez. |
| Compra pendiente de liquidación | Diferencia temporal comprobada. |
| Ejecución externa omitida internamente | Recuperación del hecho, sin otra orden. |
| Salida confirmada y recepción pendiente | Tránsito bajo seguimiento. |

## 26. OF-08-23. Corregir operaciones y posiciones

Propósito: regularizar errores conservando antecedentes.

Responsable: servicio que administra el efecto.

### Desarrollo

- Identificar original.

- Determinar causa y alcance.

- Reconstruir valores.

- Obtener aprobación.

- Registrar ajustes.

- Coordinar consecuencias.

- Rectificar información.

### Reglas

- Corregir una valuación no ejecuta una compraventa.

- No se cambia el consentimiento histórico para ocultar un error.

- Las pérdidas del banco se distinguen de importes recuperables.

- No se debita sin fundamento.

- Los ajustes de eventos alcanzan tributos y derechos.

- Un traslado erróneo requiere recuperar o regularizar con los custodios; no basta alterar la posición interna.

- Las reversiones no se repiten.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Error de valuación sin movimientos | Rectificación informativa. |
| Suscripción duplicada solo en el registro interno | Ajuste con evidencia, sin venta ficticia. |
| Interés de depósito insuficiente | Pago complementario. |
| Traslado a destino erróneo | Investigación y recuperación identificadas. |

## 27. OF-08-24. Cerrar una cuenta de inversión

Propósito: finalizar nuevas operaciones y resolver posiciones.

Responsable: Tenencias y valuación.

### Desarrollo

- Registrar petición.

- Comprobar facultades.

- Impedir nuevos compromisos incompatibles.

- Consultar órdenes, liquidaciones, medidas y eventos.

- Obtener instrucciones de venta, rescate o traslado.

- Ejecutarlas mediante sus responsables.

- Resolver importes y derechos.

- Confirmar cierre o administración residual.

### Reglas

- Cerrar no autoriza vender a cualquier precio.

- Las ventas, rescates y traslados requieren instrucciones identificadas.

- Las posiciones restringidas mantienen tratamiento.

- Un traslado incierto impide informar que las unidades desaparecieron sin pendientes.

- La salida de custodia debe conciliarse con su recepción o solución residual.

- Los derechos posteriores conservan vía de pago.

- Los cargos futuros cesan conforme a terminación contractual.

- Los depósitos a plazo continúan bajo sus acuerdos propios.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Bono sin instrucción de venta | No se vende automáticamente. |
| Rescate pendiente | Sigue la operación. |
| Traslado en tránsito | No se declara cierre íntegro sin tratamiento. |
| Rendimiento posterior | Pago residual autorizado. |
| Depósito a plazo vigente | No se cancela por cerrar inversión. |

## 28. OF-08-25. Trasladar instrumentos entre custodias

Propósito: cambiar la ubicación de custodia sin realizar una compraventa ni modificar la titularidad.

Responsable: Tenencias y valuación.

### 28.1. Modalidades

Se admiten:

- Traslado entre cuentas de inversión del mismo banco con idéntica titularidad.

- Salida hacia un custodio externo simulado.

- Entrada desde un custodio externo simulado.

La titularidad debe coincidir, incluyendo cotitulares. Los cambios de propiedad no se ejecutan como traslados de
custodia.

Los fondos comunes solo admiten traslado cuando ambos registros pueden conservar la misma clase y reconocer la
transferencia de cuotapartes. Si no existe esa capacidad, no se ofrece traslado; el rescate sigue su operación propia.

### 28.2. Información necesaria

- Solicitante y facultades.

- Cuenta de origen y destino.

- Custodios.

- Instrumento y clase.

- Cantidad.

- Titularidad.

- Fecha solicitada.

- Referencias externas.

- Cargos autorizados.

- Restricciones y operaciones pendientes.

El destino debe estar comprobado antes de enviar.

### 28.3. Condiciones previas

- Unidades liquidadas y disponibles.

- Ausencia de compromiso incompatible.

- Instrumento admitido por destino.

- Coincidencia de titularidad.

- Restricciones compatibles.

- Recursos para cargos obligatorios informados.

Una medida no se levanta por trasladar. Si exige mantener afectación, el destino debe poder recibirla y confirmarla
antes de liberar el control de origen.

### 28.4. Desarrollo de salida externa

- Registrar solicitud y referencia única.

- Comprobar facultades y destino.

- Obtener aceptación preliminar del custodio receptor.

- Comprometer las unidades.

- Obtener reserva de cargos, cuando corresponda.

- Enviar instrucción de salida.

- Comprobar entrega del custodio de origen.

- Registrar unidades en tránsito.

- Comprobar recepción en destino.

- Conciliar cantidad, clase y titularidad.

- Registrar traslado completo.

- Cobrar cargos por ejecución y comunicar.

La salida confirmada reduce la custodia de origen y extingue su compromiso por la misma cantidad. El tránsito conserva
el derecho pendiente y no queda disponible para otras operaciones.

### 28.5. Desarrollo de entrada externa

- Registrar instrucción o aviso de recepción.

- Comprobar titularidad, cuenta e instrumento.

- Confirmar capacidad de recepción.

- Recibir referencia de salida.

- Comprobar recepción efectiva del custodio del banco.

- Incorporar unidades una sola vez.

- Aplicar restricciones.

- Confirmar al custodio de origen y al cliente.

Un aviso de envío no incorpora unidades disponibles.

Una recepción sin datos suficientes queda pendiente de identificación. No se asigna arbitrariamente a una cuenta ni se
devuelve sin comprobar origen y fundamento.

### 28.6. Traslado interno

Cuando ambas cuentas pertenecen al mismo registro de posiciones:

- La salida y la entrada se confirman conjuntamente.

- Se conserva la misma cantidad total.

- No se reconoce ingreso, gasto ni resultado de compraventa por el traslado.

- Las restricciones se mantienen.

- Si falla una condición previa, no se confirma una sola parte.

### 28.7. Parcialidad

La solicitud autoriza la cantidad total.

Una entrega parcial externa se registra como hecho comprobado, aunque no complete la instrucción:

- Se identifica lo entregado.

- Se mantiene el remanente comprometido.

- Se conserva tránsito por lo no recibido.

- No se vuelve a enviar la cantidad total.

- El remanente puede cancelarse únicamente con confirmación.

El cliente recibe el detalle de lo completado y lo pendiente.

### 28.8. Cancelación y rechazo

Antes del envío puede cancelarse una instrucción sin efectos firmes.

Después del envío:

- Se consulta ejecución.

- La petición de cancelación no demuestra su resultado.

- Solo se libera la cantidad cuya falta de ejecución quedó comprobada.

Si destino rechaza después de una salida confirmada:

- Se conserva el tránsito.

- Se obtiene motivo.

- Se corrige el destino con autorización o se coordina retorno.

- No se restituyen unidades disponibles en origen hasta confirmar su recepción.

Un retorno mantiene referencia al traslado y no constituye una compra nueva.

### 28.9. Recuperación y cierre

Ante falta de respuesta se aplica el procedimiento común de consultas e investigación.

La operación termina cuando:

- Salida y recepción concuerdan, o existe retorno confirmado.

- Las cantidades parciales están resueltas.

- Las restricciones tienen aplicación comprobada.

- Los cargos y sus pendientes están identificados.

- Los derechos económicos asociados conservan responsable.

- El cliente recibió un resultado que distingue lo realizado de lo pendiente.

Una pérdida confirmada o imposibilidad de completar genera una incidencia y una obligación de reparación o recuperación
identificada. No se oculta creando unidades ficticias.

### 28.10. Cargos y efectos

- No existe principal monetario por compraventa.

- Los cargos se informan antes de autorizar.

- El cargo por ejecución se genera una sola vez por la cantidad trasladada, aplicando la tarifa aceptada.

- El rechazo anterior a la ejecución no genera comisión de ejecución.

- Los rendimientos posteriores se asignan según la fecha de derecho y la custodia efectiva.

- El traslado no duplica eventos ni elimina derechos pendientes.

- Liquidaciones de tesorería coordina entregas externas cuando el mecanismo lo requiere, conservando Tenencias y
  valuación el seguimiento del traslado.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cien unidades y traslado solicitado de treinta | Setenta disponibles y treinta comprometidas. |
| Salida confirmada de treinta sin recepción | Setenta en origen y treinta en tránsito. |
| Destino confirma treinta | Traslado completo y tránsito resuelto. |
| Confirmación repetida | Sin otra salida ni otro ingreso. |
| Se entregan veinte de treinta | Veinte identificadas y diez pendientes, sin reenviar treinta. |
| Destino rechaza después de la salida | Continúa tránsito hasta retorno o solución comprobada. |
| Retorno confirmado | Se reincorporan solo las unidades efectivamente recibidas. |
| Titularidad diferente | No se acepta como traslado de custodia. |
| Recepción anunciada pero no confirmada | Sin unidades disponibles nuevas. |
| Traslado interno de treinta | Origen disminuye treinta y destino aumenta treinta conjuntamente. |
| Rendimiento recibido por custodio anterior | Se conserva obligación residual hacia el titular. |

## 29. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Depósitos y obligaciones de devolución | Depósitos a plazo |
| Intereses y actualizaciones de depósitos | Depósitos a plazo |
| Oferta comercial | Condiciones comerciales |
| Condiciones aceptadas | Contratos |
| Características y eventos de instrumentos | Instrumentos de inversión |
| Órdenes, ejecuciones y seguimiento de liquidación | Órdenes de inversión |
| Cantidades, compromisos y valuaciones | Tenencias y valuación |
| Traslados de custodia y unidades en tránsito | Tenencias y valuación |
| Derechos económicos sobre posiciones | Tenencias y valuación |
| Reservas y movimientos monetarios | Fondos |
| Conversiones | Cambio de moneda |
| Entregas y cobros externos | Liquidaciones de tesorería |
| Recursos e inversiones propios del banco | Tesorería |
| Adecuación, restricciones y criterios tributarios | Servicios correspondientes del módulo 11 |
| Registración contable | Contabilidad |
| Documentos, tareas, reclamos y comunicaciones | Servicios correspondientes del módulo 12 |

Cada operación conserva su relación con contratación, recursos, ejecución, liquidación y posición resultante. Los
depósitos, las inversiones y los traslados mantienen separados capital, rendimiento, custodia y disponibilidad, sin
duplicar dinero, unidades ni derechos económicos.
