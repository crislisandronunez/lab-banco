# Contratos de integración — Módulo 8. Ahorro e inversiones

## 1. Propósito y alcance

Este documento define los contratos de integración proporcionados por los servicios:

- Depósitos a plazo.

- Instrumentos de inversión.

- Órdenes de inversión.

- Tenencias y valuación.

Comprende depósitos nominales e indexados, inversiones de clientes en fondos comunes y títulos de renta fija, órdenes,
liquidaciones, posiciones, rendimientos y traslados de custodia.

Un depósito a plazo representa una obligación de devolución del banco. Una inversión representa una tenencia o derecho
sobre un instrumento cuyo valor puede variar.

Las inversiones propias del banco pertenecen a Tesorería.

Los administradores, mercados, intermediarios y custodios externos funcionan como participantes simulados.

## 2. Catálogo de contratos

Todos los contratos corresponden a la versión 1.

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-08-001 | Preparar la constitución de un depósito | Depósitos a plazo | Solicitud de actuación |
| CI-08-002 | Constituir un depósito a plazo | Depósitos a plazo | Solicitud de actuación |
| CI-08-003 | Calcular intereses y actualizaciones de un depósito | Depósitos a plazo | Solicitud de actuación |
| CI-08-004 | Administrar instrucciones de vencimiento | Depósitos a plazo | Solicitud de actuación |
| CI-08-005 | Pagar un depósito al vencimiento | Depósitos a plazo | Solicitud de actuación |
| CI-08-006 | Renovar un depósito | Depósitos a plazo | Solicitud de actuación |
| CI-08-007 | Solicitar y ejecutar una precancelación | Depósitos a plazo | Solicitud de actuación |
| CI-08-008 | Corregir un depósito o sus liquidaciones | Depósitos a plazo | Solicitud de actuación |
| CI-08-009 | Consultar un depósito y sus actuaciones | Depósitos a plazo | Consulta |
| CI-08-010 | Definir y habilitar un instrumento de inversión | Instrumentos de inversión | Solicitud de actuación |
| CI-08-011 | Modificar o suspender un instrumento | Instrumentos de inversión | Solicitud de actuación |
| CI-08-012 | Consultar un instrumento y sus condiciones | Instrumentos de inversión | Consulta |
| CI-08-013 | Incorporar precios, cuotapartes y referencias de valuación | Instrumentos de inversión | Solicitud de actuación |
| CI-08-014 | Definir o corregir un evento de instrumento | Instrumentos de inversión | Solicitud de actuación |
| CI-08-015 | Constituir o mantener una cuenta de inversión | Tenencias y valuación | Solicitud de actuación |
| CI-08-016 | Consultar una cuenta de inversión y sus facultades | Tenencias y valuación | Consulta |
| CI-08-017 | Recibir y validar una orden de inversión | Órdenes de inversión | Solicitud de actuación |
| CI-08-018 | Comprometer recursos para una orden | Órdenes de inversión | Solicitud de actuación |
| CI-08-019 | Comprometer, modificar o liberar unidades | Tenencias y valuación | Solicitud de actuación |
| CI-08-020 | Ejecutar una suscripción de fondo común | Órdenes de inversión | Solicitud de actuación |
| CI-08-021 | Ejecutar un rescate de fondo común | Órdenes de inversión | Solicitud de actuación |
| CI-08-022 | Ejecutar una compra o venta de títulos | Órdenes de inversión | Solicitud de actuación |
| CI-08-023 | Incorporar una ejecución o resultado externo | Órdenes de inversión | Solicitud de actuación |
| CI-08-024 | Cancelar o vencer una orden | Órdenes de inversión | Solicitud de actuación |
| CI-08-025 | Coordinar la liquidación de una inversión | Órdenes de inversión | Solicitud de actuación |
| CI-08-026 | Incorporar confirmaciones de liquidación | Órdenes de inversión | Solicitud de actuación |
| CI-08-027 | Consultar una orden y sus actuaciones | Órdenes de inversión | Consulta |
| CI-08-028 | Registrar movimientos de tenencias | Tenencias y valuación | Solicitud de actuación |
| CI-08-029 | Aplicar o levantar restricciones sobre tenencias | Tenencias y valuación | Solicitud de actuación |
| CI-08-030 | Consultar posiciones y movimientos de custodia | Tenencias y valuación | Consulta |
| CI-08-031 | Valuar posiciones | Tenencias y valuación | Solicitud de actuación |
| CI-08-032 | Determinar derechos y efectos de un evento | Tenencias y valuación | Solicitud de actuación |
| CI-08-033 | Aplicar el cobro o entrega de un derecho económico | Tenencias y valuación | Solicitud de actuación |
| CI-08-034 | Conciliar órdenes y liquidaciones | Órdenes de inversión | Solicitud de actuación |
| CI-08-035 | Conciliar custodia, traslados y derechos | Tenencias y valuación | Solicitud de actuación |
| CI-08-036 | Corregir una orden o su liquidación | Órdenes de inversión | Solicitud de actuación |
| CI-08-037 | Corregir posiciones, valuaciones o derechos | Tenencias y valuación | Solicitud de actuación |
| CI-08-038 | Solicitar un traslado de custodia | Tenencias y valuación | Solicitud de actuación |
| CI-08-039 | Incorporar resultados de un traslado | Tenencias y valuación | Solicitud de actuación |
| CI-08-040 | Cancelar o regularizar un traslado | Tenencias y valuación | Solicitud de actuación |
| CI-08-041 | Consultar un traslado y sus actuaciones | Tenencias y valuación | Consulta |
| CI-08-042 | Cerrar una cuenta de inversión | Tenencias y valuación | Solicitud de actuación |
| CI-08-043 | Comunicar un cambio de depósito | Depósitos a plazo | Evento |
| CI-08-044 | Comunicar un cambio de instrumento o evento económico | Instrumentos de inversión | Evento |
| CI-08-045 | Comunicar un cambio de orden o liquidación | Órdenes de inversión | Evento |
| CI-08-046 | Comunicar un cambio de posición, derecho o valuación | Tenencias y valuación | Evento |
| CI-08-047 | Comunicar un cambio de traslado o cuenta de inversión | Tenencias y valuación | Evento |

## 3. Reglas comunes de integración

### 3.1. Identificación y contenido

Toda actuación identifica operación, actuación, servicio solicitante, objeto afectado, causa y antecedentes.

Según corresponda, incluye:

- Titulares, solicitante y facultades.

- Contrato y condiciones aceptadas.

- Cuenta monetaria y cuenta de inversión.

- Instrumento, clase y versión.

- Capital, cantidad, importe y moneda.

- Fechas previstas y efectivas.

- Reserva monetaria o compromiso de unidades.

- Participantes y referencias externas.

- Ejecución, liquidación, traslado o derecho económico.

- Evidencias y aprobación separada.

La misma actuación con contenido idéntico recupera el resultado existente. Si cambia su contenido, se informa conflicto.

Cada ejecución, movimiento, cobro y entrega conserva una referencia económica estable, independiente del número de
mensajes recibidos.

### 3.2. Titularidad y facultades

La titularidad del depósito o de la inversión no se deduce únicamente de la cuenta monetaria utilizada.

La actuación conjunta y las facultades de representantes se comprueban para la operación concreta.

Un autorizado o apoderado no adquiere propiedad sobre las posiciones.

Un traslado de custodia conserva idéntica titularidad, incluidos los cotitulares. No se utiliza para transferir
propiedad.

### 3.3. Recursos y posiciones

Fondos administra el dinero de cuentas bancarias. Tenencias y valuación administra unidades y derechos sobre
instrumentos.

Se distinguen cantidad registrada, disponible, comprometida, restringida, pendiente de liquidación y en tránsito.

Una misma unidad no puede respaldar dos actuaciones incompatibles. Las afectaciones superpuestas no se suman como si
alcanzaran unidades diferentes.

No se admite:

- Inversión financiada con descubierto.

- Venta de unidades inexistentes.

- Uso anticipado de cobros pendientes.

- Uso de activos de clientes para obligaciones propias del banco.

### 3.4. Recepción, ejecución y liquidación

Recibir una orden no demuestra ejecución. Ejecutar no demuestra liquidación.

Los títulos utilizan entrega contra pago. Los fondos comunes relacionan asignación o rescate con su contraprestación.

Las confirmaciones parciales se conservan como hechos parciales. No se informa cumplimiento completo si falta dinero,
instrumentos o evidencia necesaria.

### 3.5. Precisión y residuos

Los cálculos intermedios conservan al menos doce decimales.

Los importes monetarios finales se redondean a la unidad mínima de la moneda, al más cercano, elevando los empates
positivos.

Las cantidades respetan la precisión del instrumento. Una compra por importe no supera el máximo autorizado; el residuo
se libera o restituye.

Las unidades de cálculo de un depósito UVA no son cuotapartes negociables y no se truncan como una orden de inversión.

Las reversiones utilizan importes y cantidades registrados.

### 3.6. Calendarios

La zona temporal es America/Argentina/Buenos_Aires.

| Actuación | Convención |
| --- | --- |
| Constitución de depósitos | Hábiles, de 09:00 a 17:00 |
| Recepción de órdenes | Todos los días |
| Corte de fondos comunes | 15:00 de días hábiles |
| Negociación de títulos | Días de mercado, de 11:00 a 17:00 |
| Liquidación ordinaria | Siguiente hábil del calendario aplicable |
| Recepción de traslados | Todos los días |
| Gestión ordinaria de traslados | Hábiles, de 09:00 a 17:00 |

Las solicitudes fuera de horario identifican su fecha posterior de procesamiento. No se presentan como ejecutadas ni
conservan una cotización vencida.

### 3.7. Incertidumbre y recuperación

Ante incertidumbre externa se consulta al detectarla, a los cinco minutos y a los treinta minutos. Si persiste, se abre
investigación y revisión por jornada operativa.

No se repite una ejecución ni se liberan recursos por el mero transcurso del tiempo.

Los ajustes manuales requieren preparación y aprobación separadas.

### 3.8. Eventos

Los eventos identifican hecho, objeto, versión o secuencia, causa, momento, efectos y pendientes.

Un evento de orden referencia los movimientos monetarios y de custodia correspondientes; no representa otro movimiento.

La valuación, el reconocimiento de un derecho y su cobro son hechos diferentes.

## 4. Contratos de Depósitos a plazo

### CI-08-001. Preparar la constitución de un depósito

Proveedor: Depósitos a plazo. Consumidores: Solicitudes de productos, Contratos y Gestión administrativa.

Entrada: producto, titulares, capital, moneda, plazo, cuentas de origen y destino, modalidad e instrucción de
vencimiento.

Actuación: comprueba elegibilidad, oferta y facultades; determina fechas, rendimiento o regla de actualización y
condiciones de salida.

Salida: preparación identificada, condiciones, vigencia, capital, fechas, tasa, actualización prevista como regla y
requisitos de aceptación.

Reglas: aplica las modalidades definidas:

| Modalidad | Plazo mínimo | Salida anticipada |
| --- | --- | --- |
| Tradicional en pesos o dólares | 30 días corridos | No |
| Precancelable nominal en pesos | 180 días corridos | Desde el día 30 |
| UVA en pesos | 90 días corridos | No |
| UVA precancelable, persona humana | 90 días corridos | Desde el día 30 |
| UVA precancelable, persona jurídica | 180 días corridos | Desde el día 90 |

Aceptación: una actualización futura no se presenta como conocida. Si cambia la fecha y afecta condiciones, se revisa la
aceptación antes de constituir.

### CI-08-002. Constituir un depósito a plazo

Proveedor: Depósitos a plazo. Consumidor: Solicitudes de productos.

Entrada: actuación de constitución, contrato, preparación vigente y aceptada, titulares, capital, moneda, origen y
destino.

Actuación: reserva fondos propios, confirma el débito y reconoce la obligación a plazo. Conserva fecha efectiva, tasa y
unidades cuando corresponda.

Salida: depósito constituido, movimiento, condiciones y constancia; rechazo sin constitución o actuación pendiente.

Recuperación y aceptación: un débito confirmado con constitución pendiente obliga a completar o restituir. No se debita
nuevamente. Sobre saldo 1.000 y depósito 600, quedan 400 disponibles y una obligación a plazo de 600.

### CI-08-003. Calcular intereses y actualizaciones de un depósito

Proveedor: Depósitos a plazo. Consumidores: Contratos y Gestión administrativa; el mismo comportamiento rige los
cálculos periódicos del proveedor.

Entrada: depósito o preparación, fecha de cálculo, condiciones y valores de unidad con serie, fecha y fuente.

Actuación: calcula capital, actualización e interés separados.

Salida: determinación reproducible, días, tasa, valores utilizados, devengamiento, importes y datos pendientes.

Reglas: en nominales aplica interés simple Actual/365, incluyendo constitución y excluyendo la fecha final. En indexados
utiliza las unidades originales, el valor aplicable y el interés sobre capital actualizado.

Aceptación: capital 1.000 al 36,5 % por treinta días produce interés 30. Sin índice necesario no hay liquidación
definitiva. Devengar no demuestra pago.

### CI-08-004. Administrar instrucciones de vencimiento

Proveedor: Depósitos a plazo. Consumidor: Gestión administrativa.

Entrada: depósito, versión, titulares y facultades, modalidad, destino y consentimiento.

Actuación: registra pago total, renovación de capital con pago del rendimiento neto o renovación de capital más
rendimiento neto.

Salida: instrucción vigente, versión, alcance y momento de aceptación.

Reglas y aceptación: sin renovación aceptada se prepara pago total. En indexados, renovar capital significa renovar
capital actualizado. Los cambios se ordenan frente a la nueva constitución; una modificación posterior no deshace una
renovación ejecutada.

### CI-08-005. Pagar un depósito al vencimiento

Proveedor: Depósitos a plazo. Consumidor: Gestión administrativa; el mismo comportamiento rige el procesamiento de
vencimientos del proveedor.

Entrada: depósito, actuación de pago, fecha programada, liquidación, destino, tributos y medidas aplicables.

Actuación: determina la obligación y solicita acreditación. Cancela únicamente conceptos cuyo pago queda comprobado.

Salida: capital, actualización, interés, tributos, neto, movimiento y saldo pendiente.

Reglas y aceptación: el vencimiento inhábil se paga el siguiente hábil, manteniendo remuneración hasta esa fecha
programada. Una demora adicional del banco genera compensación separada, sin pagar dos veces el mismo período.

Un destino inválido conserva la obligación. Un crédito confirmado con respuesta perdida se recupera sin nuevo pago.

### CI-08-006. Renovar un depósito

Proveedor: Depósitos a plazo. Consumidor: Gestión administrativa; el mismo comportamiento rige las renovaciones
programadas.

Entrada: depósito anterior, instrucción vigente, liquidación de vencimiento, oferta nueva y condiciones de renovación
aceptadas.

Actuación: liquida el anterior y constituye otro depósito, manteniendo comprometidos los fondos durante la transición.

Salida: depósito anterior liquidado, nuevo depósito, importe renovado, rendimiento pagado y referencias relacionadas.

Reglas y aceptación: no acredita fondos como libres para reutilizarlos simultáneamente. La tasa nueva corresponde a la
oferta vigente. Si no puede renovar, prepara pago total salvo impedimento.

Capital 1.000 más rendimiento neto 30, con renovación total, constituye capital nuevo 1.030.

### CI-08-007. Solicitar y ejecutar una precancelación

Proveedor: Depósitos a plazo. Consumidor: Gestión administrativa.

Entrada: depósito, petición, facultades, fecha de recepción y confirmación de la liquidación presentada.

Actuación: comprueba modalidad, determina fecha admisible, presenta liquidación y, confirmada, programa y ejecuta el
pago.

Salida: solicitud, liquidación, fecha, aceptación, pago y cancelación o impedimentos.

Reglas: precancelación total; preaviso de cinco días hábiles; fecha no anterior al mínimo de permanencia ni al preaviso,
ajustada a hábil siguiente. Si alcanza el vencimiento, se aplica el tratamiento ordinario.

Aceptación: utiliza capital original y tasa de salida fijada al constituir. En UVA precancelable no suma actualización
de vencimiento al rendimiento de salida ni agrega otra penalidad.

### CI-08-008. Corregir un depósito o sus liquidaciones

Proveedor: Depósitos a plazo. Consumidor: Gestión administrativa.

Entrada: original, error, evidencia, valores correctos, fecha pertinente y aprobación separada.

Actuación: reconstruye condiciones y registra diferencias; coordina pagos complementarios o recuperos fundados.

Salida: ajuste, conceptos, movimientos relacionados y constancia rectificativa.

Reglas y aceptación: conserva tasa e índices anteriores. No cambia condiciones históricas para ocultar un error. Un
recupero de exceso no concede descubierto. Corregir identidad no transfiere titularidad.

### CI-08-009. Consultar un depósito y sus actuaciones

Proveedor: Depósitos a plazo. Consumidores: Contratos, Solicitudes de productos, Riesgos, Posiciones y liquidez,
Registración contable, Reclamos, Documentos y comprobantes y Gestión administrativa.

Entrada: depósito o actuación, fecha de corte y alcance autorizado.

Salida: titulares, condiciones, capital, unidades, actualización, devengamiento, instrucciones, pagos, renovaciones,
precancelaciones, correcciones y pendientes.

Reglas: distingue obligación de devolución, devengamiento y pago. La pérdida de una constancia no elimina el depósito.

## 5. Contratos de Instrumentos de inversión

### CI-08-010. Definir y habilitar un instrumento de inversión

Proveedor: Instrumentos de inversión. Consumidor: Gestión administrativa.

Entrada: identidad, clase, emisor, moneda, precisión, unidad de negociación, riesgos, documentos, calendario, precios,
flujos, participantes y liquidación; aprobación separada.

Actuación: registra y habilita el instrumento y sus operaciones admitidas.

Salida: instrumento, versión, capacidades, vigencia y requisitos incumplidos.

Reglas y aceptación: admite fondos comunes abiertos y títulos de renta fija sin apalancamiento. No habilita derivados,
ventas en corto ni garantías de margen. Un bono sin reglas de cupón o un fondo sin regla de cuotaparte no queda
habilitado.

### CI-08-011. Modificar o suspender un instrumento

Proveedor: Instrumentos de inversión. Consumidor: Gestión administrativa.

Entrada: instrumento, versión, causa, cambios, alcance, vigencia y aprobación.

Actuación: aplica cambios e identifica órdenes, posiciones y derechos afectados.

Salida: versión efectiva, operaciones suspendidas y tratamiento de pendientes.

Reglas y aceptación: distingue compras, rescates, traslados y liquidaciones. Suspender compras no bloquea
automáticamente rendimientos. Las posiciones y derechos permanecen; las órdenes anteriores reciben tratamiento
individual.

### CI-08-012. Consultar un instrumento y sus condiciones

Proveedor: Instrumentos de inversión. Consumidores: Órdenes de inversión, Tenencias y valuación, Riesgos, Liquidaciones
de tesorería y Gestión administrativa.

Entrada: instrumento, clase, operación y momento o versión.

Salida: definición, habilitación, precisión, cotización, reglas financieras, calendario, liquidación, traslados
admitidos y referencias de precios y eventos.

Reglas: el catálogo bancario define el servicio; este contrato define el instrumento concreto. La habilitación no
garantiza rentabilidad.

### CI-08-013. Incorporar precios, cuotapartes y referencias de valuación

Proveedor: Instrumentos de inversión. Consumidores: fuentes y administradores simulados; Gestión administrativa para
incorporaciones autorizadas.

Entrada: instrumento, clase, valor, moneda, fecha aplicable, fuente, referencia y evidencia; corrección vinculada cuando
corresponda.

Actuación: valida y conserva la referencia sin sustituir silenciosamente valores anteriores.

Salida: valor incorporado, duplicado reconocido, inconsistencia o referencia pendiente de comprobación.

Reglas y aceptación: diferencia precio, cuotaparte e interés corrido. La ausencia de precio no significa cero. Una
corrección identifica el valor anterior y permite revisar sus consecuencias.

### CI-08-014. Definir o corregir un evento de instrumento

Proveedor: Instrumentos de inversión. Consumidores: fuentes simuladas y Gestión administrativa.

Entrada: instrumento, evento, tipo, fecha de derecho, fecha de pago, tasa o proporción, reglas, moneda, fuente y
evidencia.

Actuación: registra cupón, amortización u otro derecho o cambio admitido y sus versiones.

Salida: evento, condiciones, fechas y corrección relacionada.

Reglas y aceptación: definir un cupón no demuestra su cobro. Los fondos acumulativos no generan un dividendo ficticio
adicional. Una corrección mantiene el evento original y sus diferencias.

## 6. Contratos de cuentas, órdenes y recursos

### CI-08-015. Constituir o mantener una cuenta de inversión

Proveedor: Tenencias y valuación. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: contrato, titulares, régimen de actuación, autorizados, cuentas monetarias y referencia de custodia.

Actuación: constituye la cuenta o aplica modificaciones formalizadas.

Salida: cuenta, titularidad, facultades, vínculos monetarios, identificación externa y habilitación.

Reglas y aceptación: constituir no crea instrumentos. Cambiar un autorizado no transfiere tenencias. Un cambio de
titulares requiere formalización y tratamiento expreso de posiciones. Repetir la constitución no duplica el acuerdo.

### CI-08-016. Consultar una cuenta de inversión y sus facultades

Proveedor: Tenencias y valuación. Consumidores: Órdenes de inversión, Contratos, Riesgos y Gestión administrativa.

Entrada: cuenta; para comprobar una actuación, actor, operación, instrumento, cantidad o importe y momento.

Salida: titularidad, régimen de actuación, estado, vínculos, restricciones y facultades para el acto.

Reglas: no reemplaza la comprobación de representación ni los controles especializados. La cuenta monetaria vinculada no
define por sí sola la propiedad de la inversión.

### CI-08-017. Recibir y validar una orden de inversión

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa.

Entrada: cuenta, instrumento y clase, modalidad, importe o cantidad, moneda, precio límite cuando corresponda, rueda o
fecha, facultades y aceptación de riesgos, cargos y condiciones.

Actuación: valida contrato, adecuación, restricciones y contenido y registra la instrucción.

Salida: orden, condiciones aceptadas, vigencia, fecha de procesamiento y requisitos.

Reglas y aceptación: fondos: suscripción por importe y rescate por cantidad o totalidad. Un rescate solicitado por
importe se transforma en cantidad estimada y requiere confirmación.

Títulos: cantidad y precio límite, con vencimiento al finalizar la rueda elegida. Fuera de horario se identifica la
próxima fecha. Una advertencia aceptada no permite omitir un control obligatorio.

### CI-08-018. Comprometer recursos para una orden

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige la preparación de
ejecución del proveedor.

Entrada: orden, versión, máximo monetario o cantidad y cargos autorizados.

Actuación: reserva fondos propios en Fondos o compromete unidades mediante CI-08-019.

Salida: recursos confirmados, reserva o compromiso, importe o cantidad y pendientes.

Reglas y aceptación: una compra máxima 1.000 con cargos 10 requiere cobertura 1.010. No utiliza descubierto ni cobros
pendientes. Una falla parcial de preparación conserva seguimiento y libera lo cancelable cuando se comprueba que no hubo
envío ni compromiso firme.

### CI-08-019. Comprometer, modificar o liberar unidades

Proveedor: Tenencias y valuación. Consumidor: Órdenes de inversión. Para traslados, Tenencias y valuación aplica
internamente las mismas reglas de exclusión.

Entrada: cuenta, instrumento y clase, operación destinataria, cantidad, actuación, versión y evidencia de liberación
cuando corresponda.

Actuación: comprueba disponibilidad y afecta unidades frente a operaciones concurrentes.

Salida: compromiso, cantidad, disponibilidad y remanente o rechazo.

Reglas y aceptación: con cien unidades, dos ventas de ochenta no obtienen ambas cobertura íntegra. Comprometer no reduce
cantidad registrada ni transfiere titularidad. Liberar exige ausencia comprobada de ejecución pendiente.

## 7. Contratos de ejecución y liquidación

### CI-08-020. Ejecutar una suscripción de fondo común

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige las órdenes
habilitadas para procesamiento.

Entrada: orden aceptada, reserva y fecha aplicable.

Actuación: envía al administrador, obtiene aceptación, valor de cierre y asignación y prepara liquidación.

Salida: suscripción, importe aplicado, cuotapartes, valor, residuo y estado.

Reglas y aceptación: antes de las 15:00 de un hábil utiliza esa fecha; desde las 15:00, el siguiente hábil. Trunca
cantidad a la precisión del instrumento sin exceder el importe. Las unidades se habilitan después de liquidación
confirmada.

Neto invertido 1.000 con cuotaparte 10 determina cien unidades.

### CI-08-021. Ejecutar un rescate de fondo común

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige las órdenes
habilitadas para procesamiento.

Entrada: orden de rescate, cantidad confirmada, compromiso, administrador y destino monetario.

Actuación: envía, obtiene resultado del administrador y determina bruto, cargos, tributos y neto para liquidación.

Salida: rescate, cantidad, valor aplicable, importes y pendientes.

Reglas y aceptación: no admite parcialidad unilateral del importe o cantidad aceptados. No habilita dinero por el mero
envío. Cien cuotas a valor 12 producen bruto 1.200. Una valuación suspendida conserva la solicitud y la incidencia.

### CI-08-022. Ejecutar una compra o venta de títulos

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige las órdenes
habilitadas para procesamiento.

Entrada: orden, rueda, cantidad, precio límite, recursos y condiciones aceptadas.

Actuación: envía al mercado y registra ejecuciones identificadas.

Salida: ejecuciones, cantidades, precios, momentos, interés corrido, cargos y remanente.

Reglas y aceptación: el bono cotiza por cien nominales. Mil nominales a 95 representan precio principal 950; con interés
corrido 8 y cargos 2, compra total 960.

No compra sobre el máximo ni vende bajo el límite. Las ejecuciones parciales conservan identidad y el remanente vence al
finalizar la rueda, sujeto a comprobación de pendientes.

### CI-08-023. Incorporar una ejecución o resultado externo

Proveedor: Órdenes de inversión. Consumidores: administradores, mercados e intermediarios simulados; Gestión
administrativa para evidencias autorizadas.

Entrada: participante, referencia externa, orden, resultado, cantidad, importe, precio o cuotaparte, fecha y evidencia.

Actuación: comprueba correspondencia, duplicación y condiciones y registra aceptación, ejecución, rechazo o
incertidumbre.

Salida: resultado incorporado, ejecución relacionada, discrepancia o investigación.

Reglas y aceptación: un acuse no equivale a ejecución. Una ejecución tardía se vincula a la orden original. Una
referencia desconocida no se asigna arbitrariamente ni origina una nueva orden.

### CI-08-024. Cancelar o vencer una orden

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige los vencimientos del
proveedor.

Entrada: orden, actuación, motivo, versión y evidencia del estado externo.

Actuación: consulta ejecuciones, solicita cancelación y libera únicamente recursos no utilizados cuya falta de ejecución
queda comprobada.

Salida: remanente cancelado, ejecuciones conservadas, recursos liberados y pendientes.

Reglas y aceptación: la petición no confirma cancelación. Una suscripción o rescate aceptado necesita confirmación del
administrador. Un vencimiento no libera ejecuciones inciertas.

### CI-08-025. Coordinar la liquidación de una inversión

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa; el mismo comportamiento rige las ejecuciones
confirmadas del proveedor.

Entrada: ejecución, obligaciones monetarias y de instrumentos, participantes, fecha, reservas, compromisos y mecanismo.

Actuación: entrega instrucciones a Liquidaciones de tesorería y coordina fondos y custodia.

Salida: liquidación identificada, instrucciones vinculadas, vencimiento y condiciones de cumplimiento.

Reglas: títulos con entrega contra pago; fondos con asignación o rescate relacionado con contraprestación. Cada
ejecución parcial conserva liquidación propia. La falta de recursos de Tesorería no transforma una ejecución confirmada
en rechazo original.

### CI-08-026. Incorporar confirmaciones de liquidación

Proveedor: Órdenes de inversión. Consumidores: Liquidaciones de tesorería y Tenencias y valuación.

Entrada: liquidación, componente confirmado, importe o cantidad, fecha efectiva, referencias y evidencia.

Actuación: comprueba correspondencia y coordina consumo de recursos, movimientos y registro de posiciones.

Salida: componentes cumplidos, liquidación completa o parcial, efectos y pendientes.

Reglas y aceptación: instrumentos recibidos sin pago confirmado no demuestran liquidación completa. Los componentes
inciertos permanecen identificados y no disponibles para nuevos usos. La confirmación repetida no duplica dinero ni
unidades.

### CI-08-027. Consultar una orden y sus actuaciones

Proveedor: Órdenes de inversión. Consumidores: Tenencias y valuación, Liquidaciones de tesorería, Riesgos, Reclamos y
Gestión administrativa.

Entrada: orden, ejecución, liquidación o actuación.

Salida: contenido aceptado, recursos, envíos, resultados, ejecuciones parciales, cancelaciones, liquidaciones,
diferencias y pendientes.

Reglas: permite recuperar el resultado original sin reenviar la inversión. Distingue orden recibida, ejecutada y
liquidada.

## 8. Contratos de posiciones, valuación y derechos

### CI-08-028. Registrar movimientos de tenencias

Proveedor: Tenencias y valuación. Consumidor: Órdenes de inversión. Los eventos de instrumentos y traslados utilizan sus
contratos específicos.

Entrada: cuenta, instrumento y clase, hecho de origen, cantidad, dirección, compromiso cuando exista, fecha y evidencia
de liquidación.

Actuación: registra ingreso o salida y consume el compromiso relacionado cuando corresponde.

Salida: movimiento, posición resultante, compromiso remanente y disponibilidad.

Reglas y aceptación: cien unidades con treinta comprometidas siguen registrando cien. Al entregar esas treinta, quedan
setenta registradas y compromiso cero. Una confirmación repetida no incorpora otra cantidad.

### CI-08-029. Aplicar o levantar restricciones sobre tenencias

Proveedor: Tenencias y valuación. Consumidor: Restricciones.

Entrada: medida, autoridad, fundamento, cuenta e instrumento, cantidad o alcance, prioridad, vigencia y actuación.

Actuación: aplica o levanta la afectación y determina su consecuencia sobre disponibilidad y operaciones pendientes.

Salida: medida aplicada, cantidad alcanzada, disponibilidad y conflictos pendientes.

Reglas y aceptación: levantar una medida no libera unidades todavía alcanzadas por otra. No suma afectaciones
superpuestas como cantidades independientes. Una restricción no desaparece por solicitar traslado.

### CI-08-030. Consultar posiciones y movimientos de custodia

Proveedor: Tenencias y valuación. Consumidores: Órdenes de inversión, Riesgos, Garantías, Contratos, Registración
contable, Extractos y Gestión administrativa.

Entrada: cuenta, instrumento, fecha de corte y alcance.

Salida: registrado, disponible, comprometido, restringido, pendiente de liquidación, tránsito, movimientos y derechos.

Reglas: identifica categorías superpuestas para que no se sumen como activos diferentes. La consulta no compromete
unidades para una futura orden.

### CI-08-031. Valuar posiciones

Proveedor: Tenencias y valuación. Consumidores: Riesgos, Extractos y Gestión administrativa.

Entrada: posiciones al corte, referencias de precio o cuotaparte, moneda y conversión informativa cuando se solicite.

Actuación: calcula valores y conserva fuentes, fechas y antigüedad.

Salida: valuación por instrumento y moneda, interés corrido separado, referencias históricas y faltantes.

Reglas y aceptación: la valuación no es dinero disponible. Bonos: precio por cien nominales. Sin precio actual puede
informar la última referencia como histórica, sin usar cero. El tránsito no se suma nuevamente en origen y destino.

### CI-08-032. Determinar derechos y efectos de un evento

Proveedor: Tenencias y valuación. Consumidores: Instrumentos de inversión y Gestión administrativa.

Entrada: evento y versión, fecha de derecho, posiciones, reglas de liquidación y custodia, tributos y antecedentes de
traslados.

Actuación: determina beneficiarios, importes, unidades, amortizaciones y residuos.

Salida: derechos identificados, cantidades o importes, vencimientos, disminución de nominal cuando corresponda y cobros
pendientes.

Reglas y aceptación: mil nominales con cupón 2 % producen derecho bruto 20. Amortización del 10 % reduce nominal cien y
genera el derecho monetario correspondiente. Reconocer el derecho no acredita un cobro.

Los traslados conservan un único derecho por las mismas unidades.

### CI-08-033. Aplicar el cobro o entrega de un derecho económico

Proveedor: Tenencias y valuación. Consumidores: Liquidaciones de tesorería y Gestión administrativa para evidencias
autorizadas.

Entrada: derecho, cobro o entrega, importe o cantidad, moneda, fecha, fuente y evidencia.

Actuación: comprueba cumplimiento y coordina acreditación monetaria o ingreso de unidades.

Salida: derecho cumplido total o parcialmente, movimientos, neto y remanente.

Reglas y aceptación: no paga dos veces el mismo cupón. Si el custodio anterior recibe un rendimiento después del
traslado, conserva la obligación residual de entregarlo al titular. Una fracción mantiene residuo o compensación
identificada.

## 9. Contratos de conciliación y corrección

### CI-08-034. Conciliar órdenes y liquidaciones

Proveedor: Órdenes de inversión. Consumidores: Liquidaciones de tesorería y Gestión administrativa.

Entrada: ámbito, cortes comparables, órdenes, registros externos y evidencias; decisión aprobada para ajustes manuales.

Actuación: compara instrucciones, ejecuciones, importes y liquidaciones y solicita regularizaciones.

Salida: conciliación, diferencias, responsables, actuaciones y pendientes.

Reglas y aceptación: conciliación diaria y ante incertidumbres. Una ejecución externa omitida internamente se recupera
sin otra orden. Conciliar no autoriza por sí solo dinero ni una operación nueva.

### CI-08-035. Conciliar custodia, traslados y derechos

Proveedor: Tenencias y valuación. Consumidores: custodios simulados, Liquidaciones de tesorería y Gestión
administrativa.

Entrada: cuentas, instrumentos, cortes, registros internos y externos, traslados, derechos y evidencias.

Actuación: compara cantidades, tránsito, afectaciones y derechos; clasifica diferencias y coordina su solución.

Salida: conciliación, diferencias temporales o no justificadas, incidencias y consecuencias pendientes.

Reglas y aceptación: custodio cien e interno noventa requiere investigación antes de agregar diez. Una salida comprobada
sin recepción mantiene tránsito. No se cierra el traslado por conciliar únicamente la salida.

### CI-08-036. Corregir una orden o su liquidación

Proveedor: Órdenes de inversión. Consumidor: Gestión administrativa.

Entrada: original, error, evidencia, tratamiento, fecha pertinente y aprobación separada.

Actuación: registra ajustes y coordina con Fondos, Tesorería y Tenencias y valuación sus consecuencias.

Salida: corrección, ejecución o liquidación relacionada, efectos monetarios y de custodia, documentos y pendientes.

Reglas: no modifica el consentimiento histórico ni borra una ejecución externa. Distingue pérdida del banco de importe
recuperable. Un débito requiere fundamento.

### CI-08-037. Corregir posiciones, valuaciones o derechos

Proveedor: Tenencias y valuación. Consumidores: Órdenes de inversión, Instrumentos de inversión y Gestión
administrativa.

Entrada: original, causa, evidencia, valores o cantidades correctos, fecha y aprobación aplicable.

Actuación: registra diferencias y coordina efectos sobre posiciones, eventos, tributos y cobros.

Salida: ajuste, posición o derecho resultante, movimientos vinculados y pendientes.

Reglas y aceptación: corregir valuación no ejecuta compraventa. Una duplicación exclusivamente interna se corrige con
evidencia, sin venta ficticia. Un traslado erróneo exige recuperación o regularización con custodios; no basta alterar
el registro interno.

## 10. Contratos de traslado y cierre

### CI-08-038. Solicitar un traslado de custodia

Proveedor: Tenencias y valuación. Consumidores: Gestión administrativa y custodios simulados para solicitudes de
entrada.

Entrada: solicitante y facultades, cuentas, custodios, titularidad completa, instrumento y clase, cantidad, fecha,
referencias, cargos y medidas.

Condiciones: titularidad coincidente, instrumento admitido por destino, unidades liquidadas y disponibles y ausencia de
compromiso incompatible.

Actuación: comprueba destino, obtiene aceptación preliminar y compromete unidades y cargos antes del envío.

Salida: traslado, modalidad interna, salida o entrada externa, cantidad, compromisos, referencias y estado.

Reglas y aceptación: las cuotapartes solo se trasladan si ambos registros conservan clase y reconocen la transferencia.
Una medida que deba mantenerse requiere capacidad y confirmación de destino. Un aviso de entrada no incorpora unidades
disponibles.

### CI-08-039. Incorporar resultados de un traslado

Proveedor: Tenencias y valuación. Consumidores: custodios simulados y Liquidaciones de tesorería cuando participa en la
entrega.

Entrada: traslado, salida, recepción, rechazo o retorno; cantidad, clase, titularidad, fecha, referencia y evidencia.

Actuación: registra cada componente una vez. La salida confirmada reduce custodia de origen y su compromiso y mantiene
tránsito hasta recepción o retorno.

Salida: cantidades en origen, tránsito y destino; parcialidades, restricciones, cargos y pendientes.

Reglas y aceptación: de treinta solicitadas, una salida de veinte no habilita reenviar treinta: quedan veinte
identificadas y diez pendientes. El rechazo posterior a salida no restituye disponibilidad en origen sin retorno
confirmado.

En traslado interno del mismo registro, salida y entrada se confirman conjuntamente.

### CI-08-040. Cancelar o regularizar un traslado

Proveedor: Tenencias y valuación. Consumidor: Gestión administrativa.

Entrada: traslado, actuación, motivo, alcance, destino corregido o retorno autorizado y evidencias; aprobación separada
para ajustes manuales.

Actuación: cancela lo no ejecutado, consulta pendientes o coordina destino corregido y retorno.

Salida: cantidad cancelada, liberada, recibida o todavía en tránsito; incidencia y obligaciones de reparación o
recuperación.

Reglas y aceptación: después del envío, la petición no demuestra cancelación. No reincorpora unidades hasta recepción
efectiva. Una pérdida no se oculta creando unidades ficticias.

### CI-08-041. Consultar un traslado y sus actuaciones

Proveedor: Tenencias y valuación. Consumidores: Liquidaciones de tesorería, Restricciones, Reclamos y Gestión
administrativa.

Entrada: traslado o referencia externa completa.

Salida: instrucción, titularidad, compromisos, aceptación, salidas, recepciones, parciales, tránsito, retornos, cargos y
derechos residuales.

Reglas: diferencia lo completado de lo pendiente. La consulta no reenvía unidades ni repite cargos.

### CI-08-042. Cerrar una cuenta de inversión

Proveedor: Tenencias y valuación. Consumidores: Contratos, Solicitudes de productos y Gestión administrativa.

Entrada: cuenta, petición o causa, facultades e instrucciones identificadas de venta, rescate o traslado.

Actuación: impide nuevos compromisos incompatibles y comprueba posiciones, órdenes, liquidaciones, medidas, traslados y
derechos.

Salida: cierre o administración residual, actuaciones pendientes, vía de pago de derechos posteriores y cargos
finalizados.

Reglas y aceptación: cerrar no autoriza vender a cualquier precio. Un traslado incierto conserva seguimiento. Los
derechos posteriores mantienen vía de pago. Un depósito a plazo continúa bajo su acuerdo propio y no se cancela por
cerrar inversión.

## 11. Contratos de eventos

### CI-08-043. Comunicar un cambio de depósito

Proveedor: Depósitos a plazo. Consumidores: Solicitudes de productos, Contratos, Posiciones y liquidez, Registración
contable, Documentos y comprobantes y Comunicaciones.

Contenido: depósito, versión, constitución, devengamiento, actualización, instrucción, pago, renovación, precancelación
o ajuste; conceptos, fechas y referencias.

Reglas: distingue rendimiento de pago. Una renovación relaciona depósito anterior y nuevo sin duplicar capital. El
evento no ordena repetir un débito o crédito.

### CI-08-044. Comunicar un cambio de instrumento o evento económico

Proveedor: Instrumentos de inversión. Consumidores: Órdenes de inversión, Tenencias y valuación, Riesgos y
Comunicaciones.

Contenido: instrumento o evento, versión, habilitación, suspensión, referencia de precio, condiciones, fechas y
correcciones.

Reglas: una suspensión identifica su alcance. Un precio no representa dinero recibido. Una corrección no sustituye
silenciosamente antecedentes.

### CI-08-045. Comunicar un cambio de orden o liquidación

Proveedor: Órdenes de inversión. Consumidores: Tenencias y valuación, Liquidaciones de tesorería, Posiciones y liquidez,
Registración contable, Documentos y comprobantes y Comunicaciones.

Contenido: orden, actuación, ejecución, cantidad, importe, recursos, liquidación, componentes cumplidos y pendientes.

Reglas: diferencia recepción, ejecución y liquidación. Referencia movimientos monetarios y de custodia sin duplicarlos.

### CI-08-046. Comunicar un cambio de posición, derecho o valuación

Proveedor: Tenencias y valuación. Consumidores: Órdenes de inversión, Riesgos, Garantías, Registración contable,
Extractos y Comunicaciones.

Contenido: cuenta, instrumento, hecho, cantidad, categorías afectadas, derecho o valuación, fuentes, fecha y
referencias.

Reglas: un cambio informativo de valor no se interpreta como cobro. Un derecho reconocido y su cumplimiento conservan
relación para evitar duplicaciones.

### CI-08-047. Comunicar un cambio de traslado o cuenta de inversión

Proveedor: Tenencias y valuación. Consumidores: Solicitudes de productos, Contratos, Liquidaciones de tesorería,
Restricciones, Documentos y comprobantes y Comunicaciones.

Contenido: cuenta o traslado, versión, titularidad referenciada, compromisos, salida, tránsito, recepción, retorno,
cierre o régimen residual.

Reglas: salida no equivale a recepción. El traslado no comunica compraventa ni cambio de propietario. Un cierre residual
no afirma que desaparecieron todas las obligaciones.

## 12. Integraciones consumidas

### 12.1. Institución, personas y contratación

| Contrato | Uso |
| --- | --- |
| CI-01-004 | Comprobar facultades institucionales. |
| CI-01-005 | Consultar producto bancario y capacidades. |
| CI-01-008 | Comprobar elegibilidad y compatibilidad. |
| CI-01-011 | Determinar condiciones comerciales. |
| CI-01-012 | Recuperar oferta y versión utilizadas. |
| CI-01-014 | Consultar calendarios y convenciones. |
| CI-01-015 | Determinar fechas y ajustes. |
| CI-02-002 | Identificar titulares, representantes y participantes. |
| CI-02-015 | Consultar relación comercial. |
| CI-02-021 | Comprobar representación para el acto concreto. |
| CI-03-016 | Informar resultado de constitución del producto. |
| CI-03-022 | Consultar contrato, condiciones y situación. |

La renovación automática requiere consentimiento contractual suficiente. La propuesta inicial no habilita cualquier tasa
o producto futuro.

### 12.2. Cuentas y fondos

| Contrato | Uso |
| --- | --- |
| CI-04-002 | Consultar cuenta monetaria y capacidades. |
| CI-04-012 | Consultar posición y movimientos. |
| CI-04-013 | Constituir reservas con fondos propios. |
| CI-04-014 | Consumir reservas. |
| CI-04-015 | Liberar reservas con evidencia suficiente. |
| CI-04-016 | Ajustar importe o firmeza de reservas. |
| CI-04-017 | Registrar créditos y otros movimientos procedentes. |
| CI-04-021 | Corregir movimientos vinculados. |
| CI-04-022 | Recuperar resultado de actuación monetaria. |
| CI-04-023 | Consultar reservas. |
| CI-04-040 | Comprobar facultades y admisión de operaciones de cuenta. |

Una confirmación de Tesorería y otra de Fondos pueden describir componentes relacionados de una liquidación. Se vinculan
por sus referencias sin registrar otro ingreso o egreso.

### 12.3. Otros módulos

| Proveedor | Integración requerida |
| --- | --- |
| Riesgos | Adecuación de inversiones, perfil admitido y controles de riesgo. |
| Restricciones | Medidas sobre personas, cuentas, dinero o instrumentos. |
| Prevención de fraude y Prevención de lavado | Controles de su competencia. |
| Criterios tributarios | Determinación de tributos y retenciones. |
| Cambio de moneda | Conversiones identificadas y aceptadas. |
| Liquidaciones de tesorería | Entregas y cobros externos, entrega contra pago y participación en traslados. |
| Posiciones y liquidez | Obligaciones y flujos confirmados de depósitos e inversiones. |
| Registración contable | Representación de depósitos, rendimientos, operaciones y obligaciones. |
| Gestión administrativa | Actuaciones humanas y aprobaciones separadas. |
| Documentos y comprobantes | Constancias, acuerdos, liquidaciones y rectificaciones. |
| Comunicaciones | Instrucciones, incidencias y resultados. |
| Reclamos | Investigaciones y errores denunciados. |
| Extractos | Posiciones, movimientos y derechos. |

Los identificadores de esos contratos se establecen en sus documentos respectivos.

## 13. Correspondencia con las operaciones funcionales

| Operación | Contratos principales |
| --- | --- |
| OF-08-01. Preparar una constitución de depósito a plazo | CI-08-001, 003 |
| OF-08-02. Constituir el depósito | CI-08-002, 009, 043 |
| OF-08-03. Calcular intereses y actualizaciones | CI-08-003, 009, 043 |
| OF-08-04. Administrar instrucciones de vencimiento | CI-08-004, 009, 043 |
| OF-08-05. Pagar un depósito al vencimiento | CI-08-005, 003, 043 |
| OF-08-06. Renovar un depósito | CI-08-006, 004, 043 |
| OF-08-07. Precancelar un depósito admitido | CI-08-007, 009, 043 |
| OF-08-08. Corregir y consultar depósitos | CI-08-008, 009 |
| OF-08-09. Definir y habilitar instrumentos de inversión | CI-08-010, 012, 044 |
| OF-08-10. Modificar o suspender instrumentos | CI-08-011, 012, 044 |
| OF-08-11. Constituir y mantener una cuenta de inversión | CI-08-015, 016, 047 |
| OF-08-12. Recibir y validar una orden | CI-08-017, 012, 016, 027 |
| OF-08-13. Comprometer fondos o instrumentos | CI-08-018, 019 |
| OF-08-14. Ejecutar suscripciones de fondos comunes | CI-08-020, 023, 025, 026 |
| OF-08-15. Ejecutar rescates de fondos comunes | CI-08-021, 023, 025, 026 |
| OF-08-16. Ejecutar compras y ventas de títulos | CI-08-022, 023, 025, 026 |
| OF-08-17. Cancelar o vencer órdenes | CI-08-024, 027 |
| OF-08-18. Coordinar la liquidación de inversiones | CI-08-025, 026, 028, 045 |
| OF-08-19. Registrar movimientos y restricciones de tenencias | CI-08-019, 028, 029, 030 |
| OF-08-20. Valuar posiciones | CI-08-013, 030, 031, 046 |
| OF-08-21. Procesar eventos y rendimientos | CI-08-014, 032, 033, 046 |
| OF-08-22. Conciliar órdenes y custodia | CI-08-034, 035 |
| OF-08-23. Corregir operaciones y posiciones | CI-08-008, 036, 037 |
| OF-08-24. Cerrar una cuenta de inversión | CI-08-042, 030, 047 |
| OF-08-25. Trasladar instrumentos entre custodias | CI-08-038, 039, 040, 041, 047 |
