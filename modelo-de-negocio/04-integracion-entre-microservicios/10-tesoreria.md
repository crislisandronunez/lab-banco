# Contratos de integración — Módulo 10. Tesorería

## 1. Propósito y alcance

Este documento define los intercambios proporcionados por:

- Posiciones y liquidez.

- Operaciones de tesorería.

- Cambio de moneda.

- Liquidaciones de tesorería.

Comprende disponibilidades propias, efectivo, compromisos, proyecciones, financiación e inversiones del banco,
cotizaciones, intercambios y liquidaciones externas.

La disponibilidad de una cuenta de cliente y la liquidez del banco son posiciones diferentes.

Una operación del cliente puede estar confirmada mientras permanecen pendientes sus regularizaciones internas o una
liquidación entre participantes.

Los mercados, depositarios, custodios y contrapartes externos funcionan dentro de la simulación.

## 2. Catálogo de contratos

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-10-001 | Administrar cuentas y ubicaciones propias | Posiciones y liquidez | Solicitud |
| CI-10-002 | Consultar disponibilidades y capacidad | Posiciones y liquidez | Consulta |
| CI-10-003 | Registrar movimiento propio | Posiciones y liquidez | Solicitud |
| CI-10-004 | Solicitar traslado de efectivo | Posiciones y liquidez | Solicitud |
| CI-10-005 | Incorporar salida, recepción o arqueo | Posiciones y liquidez | Solicitud |
| CI-10-006 | Comprometer disponibilidades propias | Posiciones y liquidez | Solicitud |
| CI-10-007 | Modificar, reasignar, liberar o resolver compromiso | Posiciones y liquidez | Solicitud |
| CI-10-008 | Incorporar flujos de liquidez | Posiciones y liquidez | Solicitud |
| CI-10-009 | Elaborar y consultar proyección | Posiciones y liquidez | Solicitud y consulta |
| CI-10-010 | Gestionar faltante o excedente | Posiciones y liquidez | Solicitud |
| CI-10-011 | Concertar financiación propia | Operaciones de tesorería | Solicitud |
| CI-10-012 | Concertar inversión o desinversión propia | Operaciones de tesorería | Solicitud |
| CI-10-013 | Incorporar ejecución propia | Operaciones de tesorería | Solicitud |
| CI-10-014 | Determinar rendimientos y vencimientos | Operaciones de tesorería | Solicitud |
| CI-10-015 | Aplicar cumplimiento de operación propia | Operaciones de tesorería | Solicitud |
| CI-10-016 | Modificar o cancelar operación propia | Operaciones de tesorería | Solicitud |
| CI-10-017 | Consultar operación o posición propia | Operaciones de tesorería | Consulta |
| CI-10-018 | Administrar cotizaciones | Cambio de moneda | Solicitud |
| CI-10-019 | Consultar cotización por finalidad | Cambio de moneda | Consulta |
| CI-10-020 | Cotizar intercambio concreto | Cambio de moneda | Solicitud |
| CI-10-021 | Aceptar y ejecutar intercambio | Cambio de moneda | Solicitud |
| CI-10-022 | Resolver incidencia cambiaria | Cambio de moneda | Solicitud |
| CI-10-023 | Consultar operación cambiaria | Cambio de moneda | Consulta |
| CI-10-024 | Recibir y validar obligación de liquidación | Liquidaciones de tesorería | Solicitud |
| CI-10-025 | Preparar y liberar instrucción | Liquidaciones de tesorería | Solicitud |
| CI-10-026 | Incorporar resultado externo | Liquidaciones de tesorería | Solicitud |
| CI-10-027 | Consultar obligación o liquidación | Liquidaciones de tesorería | Consulta |
| CI-10-028 | Resolver parcialidad, incertidumbre o incumplimiento | Liquidaciones de tesorería | Solicitud |
| CI-10-029 | Conciliar cuentas externas y liquidaciones | Liquidaciones de tesorería | Solicitud |
| CI-10-030 | Valuar operaciones e inversiones propias | Operaciones de tesorería | Solicitud |
| CI-10-031 | Elaborar y consultar cierre operativo | Posiciones y liquidez | Solicitud y consulta |
| CI-10-032 | Consultar actuación de posición o liquidez | Posiciones y liquidez | Consulta |
| CI-10-033 | Comunicar disponibilidad o compromiso | Posiciones y liquidez | Evento |
| CI-10-034 | Comunicar necesidad o proyección | Posiciones y liquidez | Evento |
| CI-10-035 | Comunicar cambio de operación propia | Operaciones de tesorería | Evento |
| CI-10-036 | Comunicar cotización o corrección | Cambio de moneda | Evento |
| CI-10-037 | Comunicar cambio de operación cambiaria | Cambio de moneda | Evento |
| CI-10-038 | Comunicar liquidación o conciliación | Liquidaciones de tesorería | Evento |
| CI-10-039 | Comunicar cierre o valuación | Posiciones y liquidez | Evento |

## 3. Convenciones comunes

### 3.1. Referencias

Las actuaciones identifican operación, actuación, solicitante, objeto y causa.

Según corresponda, incluyen:

- Obligación y propietario.

- Contraparte y mecanismo.

- Moneda e importe.

- Instrumento y cantidad.

- Ubicaciones.

- Fechas económicas y efectivas.

- Compromisos.

- Contrato y aceptación.

- Referencias externas.

- Facultades y aprobaciones.

- Evidencias y versión esperada.

La identidad de una obligación, entrega o recepción se conserva aunque lleguen varias comunicaciones.

Misma actuación y contenido recupera el resultado. Otro contenido produce conflicto.

### 3.2. Autoridades

| Información o actuación | Responsable |
| --- | --- |
| Disponibilidades propias y compromisos | Posiciones y liquidez |
| Financiación e inversiones propias | Operaciones de tesorería |
| Cotizaciones e intercambios | Cambio de moneda |
| Instrucciones y resultados externos | Liquidaciones de tesorería |
| Composición de obligación de negocio | Servicio de origen |
| Saldos de clientes | Fondos |
| Custodia de clientes | Tenencias y valuación |
| Límites y exposición | Riesgos |
| Registración contable | Contabilidad |

La financiación del banco no se administra como un préstamo concedido a un cliente.

Las inversiones propias no se incorporan a cuentas de inversión de clientes.

### 3.3. Capacidad

La capacidad se determina por moneda, ubicación y clase de recurso.

Se consideran disponibilidades confirmadas, compromisos e inmovilizaciones sin duplicar afectaciones superpuestas.

Los cobros esperados pertenecen a proyecciones.

Una línea aprobada es capacidad contingente hasta confirmar su disposición.

La insuficiencia se informa como faltante y capacidad nueva cero. No crea saldo.

### 3.4. Clases de compromiso

| Clase | Finalidad |
| --- | --- |
| EXTERNAL_PAYMENT | Respaldar salida monetaria externa |
| PHYSICAL_CASH | Respaldar entrega o traslado físico |
| OWN_INVESTMENT | Respaldar aplicación de recursos en operación propia |
| INTERNAL_FX_PREPARATION | Controlar capacidad temporal durante la preparación de un cambio interno |

El compromiso conserva clase, propietario, operación, recursos afectados, firmeza y condiciones de conclusión.

La clase no se cambia para eludir su tratamiento.

El compromiso de cambio interno no representa una instrucción externa ni una salida física pendiente por sí mismo.

### 3.5. Efectos relacionados

Un cumplimiento puede producir movimiento propio, aplicación a la obligación de origen, movimiento de cliente y asiento.

Son efectos relacionados de autoridades diferentes.

Las referencias permiten integrarlos sin repetir movimientos.

La ejecución de saldos de clientes no demuestra un cambio en existencias físicas o cuentas externas del banco.

### 3.6. Aprobación

Concertación, excepciones, ajustes manuales y cambios sensibles requieren preparación administrativa y aprobación
separadas.

La función que libera una liquidación es distinta de quien concertó la operación.

Los procedimientos automáticos identifican regla aprobada, alcance y límites.

Los cambios de destino se verifican por una vía independiente. Una aprobación anterior no cubre un destino alterado.

### 3.7. Cálculos y horario

Las posiciones en ARS y USD permanecen separadas.

Los cálculos intermedios conservan al menos doce decimales. Los importes finales respetan la unidad mínima y HALF_UP.

Las operaciones propias a tasa fija utilizan interés simple Actual/365, salvo flujos específicos del instrumento.

No existe capitalización automática.

La zona es America/Argentina/Buenos_Aires.

La gestión ordinaria se realiza en días hábiles de 09:00 a 18:00.

Obligaciones y confirmaciones se reciben durante todo el día. Su recepción no modifica vencimientos ni urgencias.

### 3.8. Incertidumbre

Una instrucción enviada sin resultado no se repite como nueva ni permite liberar cobertura.

Se consulta al detectar la incertidumbre, a los cinco minutos y a los treinta minutos.

Si persiste, se abre investigación con revisión por jornada operativa.

Los compromisos se concluyen conforme a su clase:

- Consumo por salida comprobada.

- Liberación por cancelación comprobada.

- Resolución de preparación de cambio interno.

- Mantenimiento mientras exista incertidumbre relevante.

## 4. Contratos de Posiciones y liquidez

### CI-10-001. Administrar cuentas y ubicaciones propias

Consumidor: Gestión administrativa.

Entrada: ubicación, entidad, titularidad, moneda, identificación, finalidad, capacidades, responsables, restricciones,
datos de liquidación y aprobación.

Salida: ubicación, versión, vigencia y pendientes.

Reglas y aceptación: registrar una caja no crea efectivo. Un saldo inicial exige hecho respaldado y correspondencia
contable. Una ubicación con saldo o compromisos conserva tratamiento antes de retirarse.

### CI-10-002. Consultar disponibilidades y capacidad

Consumidores: Operaciones de tesorería, Cambio de moneda, Liquidaciones de tesorería, Operaciones de efectivo, Riesgos y
Gestión administrativa.

Entrada: moneda, ámbito, ubicaciones, corte y finalidad.

Salida: saldo, compromisos por clase, inmovilizaciones, tránsito, capacidad, faltante y antigüedad.

Reglas: consultar no reserva. El tránsito no está disponible simultáneamente en origen y destino.

Los compromisos de preparación cambiaria se muestran separados de obligaciones de pago externo.

### CI-10-003. Registrar movimiento propio

Consumidores: Liquidaciones de tesorería, Operaciones de efectivo, Cambio de moneda para efectos comprobados y Gestión
administrativa autorizada.

Entrada: hecho, efecto, ubicación, moneda, importe, dirección, fecha, compromiso y evidencia.

Salida: movimiento, saldo y remanente.

Reglas:

- Requiere entrada o salida comprobada.

- Consume el compromiso aplicable sin repetirlo.

- Una orden no demuestra salida.

- Resolver preparación de cambio interno no utiliza este contrato para crear un movimiento ficticio.

- Los traslados físicos utilizan su circuito de tránsito.

Aceptación: saldo 1.000 y pago confirmado 200 dejan 800; una confirmación repetida no descuenta otros 200.

### CI-10-004. Solicitar traslado de efectivo

Consumidor: Gestión administrativa.

Entrada: origen, destino, moneda, importe, fecha, responsables y aprobación.

Salida: traslado, compromiso y condiciones.

Reglas: solicitar no demuestra salida. La cancelación exige comprobar que el traslado no comenzó.

El compromiso no respalda otra entrega.

### CI-10-005. Incorporar salida, recepción o arqueo

Consumidor: Gestión administrativa mediante actores autorizados.

Entrada: traslado o ubicación, hecho físico, importe contado, moneda, momento, responsable y evidencia.

Salida: existencia, tránsito, recepción, diferencia y seguimiento.

Reglas:

- El arqueador es distinto del custodio.

- Los ajustes requieren aprobación separada.

- Las diferencias no crean efectivo.

- Un faltante no se imputa a clientes sin evidencia.

Aceptación: salida 100 de origen 500 deja 400 y tránsito 100. Recepción 90 conserva diferencia 10.

### CI-10-006. Comprometer disponibilidades propias

Consumidores: Liquidaciones de tesorería, Operaciones de tesorería, Cambio de moneda y Operaciones de efectivo.

Entrada:

- Actuación y operación.

- Clase de compromiso.

- Obligación o intercambio.

- Moneda e importe.

- Fecha.

- Recursos admitidos.

- Propietario.

- Firmeza.

- Condiciones de consumo, cancelación o resolución.

- Referencias de coordinación.

Actuación: comprueba y afecta capacidad en una decisión local.

Salida: compromiso, clase, recursos, importe, remanente y condiciones.

Reglas:

- No modifica saldo confirmado.

- No duplica una afectación por repetición.

- Los recursos afectados no respaldan preparaciones incompatibles concurrentes.

- El compromiso físico se distingue de la reserva del cliente.

- INTERNAL_FX_PREPARATION se vincula al intercambio y a sus referencias de Fondos y Riesgos.

Aceptación: capacidad 100 no confirma dos compromisos incompatibles de 80. Comprometer 40 conserva saldo 100 y capacidad
60.

### CI-10-007. Modificar, reasignar, liberar o resolver compromiso

Consumidores: Propietarios autorizados y Gestión administrativa en su competencia.

Modalidades:

| Modalidad | Resultado |
| --- | --- |
| MODIFY | Ajusta importe o condiciones |
| REALLOCATE | Cambia cobertura preservando continuidad |
| RELEASE_CANCELLED | Libera por cancelación comprobada |
| RESOLVE_INTERNAL_FX | Concluye preparación cambiaria ejecutada internamente |

Entrada común: compromiso, versión, actuación, modalidad, causa y evidencia.

RESOLVE_INTERNAL_FX exige:

- Clase INTERNAL_FX_PREPARATION.

- Intercambio original.

- Resultado confirmado del conjunto de Fondos.

- Ambas monedas e importes.

- Referencia de transformación confirmada en Riesgos.

- Correspondencia entre las referencias y el compromiso.

Salida: cobertura, remanente, conclusión, referencias y pendientes.

Reglas:

- Reasignar confirma cobertura nueva antes de retirar la anterior.

- No mantiene dos compromisos independientes por el mismo importe.

- Liberar no genera ingreso.

- La incertidumbre no permite liberar por vencimiento.

- RESOLVE_INTERNAL_FX registra RESOLVED_INTERNAL_FX y extingue la afectación temporal.

- Esa resolución no registra salida física o externa ni libera la exposición efectiva de Riesgos.

Aceptación: un intercambio confirmado no se cancela como si nunca hubiera ocurrido; se resuelve con sus efectos y
exposición identificados.

### CI-10-008. Incorporar flujos

Consumidores: Responsables de depósitos, financiación, pagos, tarjetas, inversiones, efectivo, cambios y operaciones
propias.

Entrada: flujo, origen, versión, moneda, importe, dirección, fecha, certeza y composición.

Salida: registro y relaciones.

Reglas: agregado y componentes no se suman dos veces. La expectativa no modifica disponibilidad.

El cambio interno actualiza las necesidades por moneda sin inventar un movimiento externo.

### CI-10-009. Elaborar y consultar proyección

Consumidores: Operaciones de tesorería, Liquidaciones, Riesgos y Gestión administrativa.

Entrada: corte, monedas, ámbitos, horizontes y escenarios; para consulta, proyección.

Salida: horizontes intradiario, siguiente hábil, siete y treinta días; faltantes, excedentes, fuentes y supuestos.

Reglas: compromisos y flujos relacionados no se descuentan dos veces.

Aceptación: saldo 100, pagos 80 y cobros 30 producen proyección 50 conforme a sus fechas, sin convertir cobros previstos
en dinero actual.

### CI-10-010. Gestionar faltante o excedente

Consumidores: Liquidaciones, Operaciones de tesorería y Gestión administrativa.

Entrada: necesidad, moneda, fecha, obligaciones, alternativas y autorizaciones.

Salida: plan, actuaciones, cobertura y remanente.

Reglas: inversión que liquida mañana no cubre hoy. Financiación solicitada no acredita. No utiliza custodia de clientes
ni borra sus acreditaciones.

### CI-10-032. Consultar actuación de posición o liquidez

Consumidores: Originantes, Riesgos, Cierres contables y Gestión administrativa.

Entrada: actuación, movimiento, compromiso, traslado, proyección o cierre.

Salida: contenido, clase, versiones, decisiones, recursos, efectos y pendientes.

Permite recuperar RESOLVED_INTERNAL_FX sin crear un movimiento ni otra resolución.

## 5. Contratos de Operaciones de tesorería

### CI-10-011. Concertar financiación propia

Consumidor: Gestión administrativa.

Entrada: contraparte, modalidad, capital, moneda, plazo, tasa, flujos, garantías, contrato, límites y aprobación.

Salida: operación, condiciones, obligaciones y liquidaciones.

Reglas: firmar no acredita. Cada disposición de línea tiene identidad. El interés corresponde al capital efectivamente
recibido.

Aceptación: capital 1.000, tasa 36,5 % y diez días producen interés 10 antes de otros conceptos.

### CI-10-012. Concertar inversión o desinversión propia

Consumidor: Gestión administrativa.

Entrada: contraparte o instrumento, importe o cantidad, moneda, plazo, tasa o precio límite, cargos, recursos y
aprobación.

Salida: operación, recursos, ejecución y liquidación previstas.

Reglas: admite colocaciones, préstamos interbancarios simples y renta fija sin apalancamiento. No admite ventas en corto
ni activos de clientes.

Aceptación: mil nominales a precio 95 representan principal 950, con interés corrido y cargos separados.

### CI-10-013. Incorporar ejecución propia

Consumidores: Mercados y contrapartes simuladas; Gestión administrativa autorizada.

Entrada: operación, referencia externa, cantidad o capital, precio, fecha, resultado y evidencia.

Salida: ejecución, remanente, flujos y liquidaciones.

Reglas: acuse no demuestra ejecución. Las parcialidades tienen identidad y conservan recursos del remanente.

### CI-10-014. Determinar rendimientos y vencimientos

Consumidores: Posiciones y liquidez, Cierres contables y Gestión administrativa.

Entrada: operación, período, condiciones, capital y reglas de cupón o amortización.

Salida: calendario, devengamientos, derechos, obligaciones y cálculos.

Reglas: devengar no cobra. Amortización e interés mantienen naturaleza. No duplica períodos ni capitaliza
automáticamente.

### CI-10-015. Aplicar cumplimiento

Consumidor: Liquidaciones de tesorería.

Entrada: operación, obligación, cumplimiento, importe o cantidad, fecha y evidencia.

Salida: conceptos cumplidos, posición, saldo e incidencias.

Reglas: cobrar interés devengado cancela el derecho sin reconocer otro ingreso. El movimiento propio se registra una
vez.

El incumplimiento conserva el derecho u obligación y se comunica a Riesgos.

### CI-10-016. Modificar o cancelar operación propia

Consumidor: Gestión administrativa.

Entrada: operación, causa, propuesta, acuerdo, liquidación de salida, consecuencias y aprobación.

Salida: condiciones, principal, interés, costo o resultado y pendientes.

Reglas: una propuesta no cambia el acuerdo. La liquidación incierta impide cierre definitivo. Liberar recursos o
garantías exige fundamento.

### CI-10-017. Consultar operación o posición propia

Consumidores: Posiciones y liquidez, Liquidaciones, Riesgos, Contabilidad y Gestión administrativa.

Entrada: operación, instrumento, contraparte, actuación o corte.

Salida: condiciones, ejecuciones, capital, cantidades, rendimientos, compromisos, obligaciones, valuaciones y
pendientes.

Separa posiciones propias de custodia de clientes.

### CI-10-030. Valuar operaciones e inversiones propias

Consumidores: Posiciones y liquidez, Riesgos y Cierres contables.

Entrada: corte, posiciones, criterios, precios, cotizaciones y versiones.

Salida: valuación, fuentes, antigüedad, diferencias y faltantes.

Reglas: falta de precio no se sustituye por cero. Valuar no vende ni modifica contratos.

Distingue resultado realizado y variación de valuación.

## 6. Contratos de Cambio de moneda

### CI-10-018. Administrar cotizaciones

Consumidores: Fuentes simuladas y Gestión administrativa.

Entrada: clase, par, sentido, unidad, valor, fuente, momento, vigencia, versión y evidencia.

La carga manual requiere aprobación separada.

Salida: cotización, finalidad, procedencia y validación.

Reglas: distingue referencia, cierre contable, comercial ejecutable e informativa.

Compra y venta se expresan desde el banco.

Aceptación: compra 1.030 y venta 1.020 para igual alcance y momento se rechazan por incoherencia.

### CI-10-019. Consultar cotización por finalidad

Consumidores: Contabilidad, Tenencias y valuación, Operaciones de tesorería y Gestión administrativa.

Entrada: par, sentido, finalidad, fecha o versión.

Salida: valor, unidad, clase, fuente, vigencia, antigüedad y correcciones.

Reglas: referencia contable no es oferta ejecutable. La ausencia se informa. Una cotización histórica no se presenta
como actual.

### CI-10-020. Cotizar intercambio concreto

Consumidores: Transferencias, Pagos, Procesamiento de consumos, Cuenta de tarjeta, Órdenes de inversión, Operaciones de
tesorería y Gestión administrativa.

Entrada: participante, operación, modalidad, monedas, sentido, importe fijo, cuentas o posiciones, finalidad y
liquidación.

Salida: propuesta, ambos importes, cotización, cargos, tributos, destinos y vencimiento.

Reglas:

- Vigencia de sesenta segundos.

- Identifica importe fijo y contraprestación.

- Cotizar no reserva recursos.

- No asegura ejecución.

- La modalidad distingue cambio interno y entregas externas.

Aceptación: venta bancaria de USD 100 a ARS 1.020 determina principal ARS 102.000, con cargos separados.

### CI-10-021. Aceptar y ejecutar intercambio

Consumidores: Originantes de la cotización y Gestión administrativa.

Entrada: propuesta, aceptación, actuación, modalidad, autorizaciones y referencias.

La recepción de la aceptación debe ser anterior al vencimiento y la comprueba Cambio de moneda.

Salida: aceptación, resultado monetario, compromisos, exposición, entregas y regularizaciones pendientes.

#### Modalidad interna

Cambio de moneda:

- Registra intención y aceptación exactas.

- Obtiene cobertura del cliente cuando corresponde.

- Obtiene INTERNAL_FX_PREPARATION mediante CI-10-006.

- Compromete capacidad exigible en Riesgos mediante CI-11-006.

- Solicita el conjunto atómico de Fondos mediante CI-04-018.

- Recupera su resultado si es incierto.

- Con Fondos confirmado, solicita transformación en Riesgos mediante CI-11-007.

- Actualiza flujos y necesidades por moneda.

- Resuelve el compromiso temporal mediante CI-10-007.

Reglas:

- No sustituye unilateralmente condiciones aceptadas.

- Fondos confirma ambas piernas conjuntamente.

- El resultado monetario no depende de inventar una entrega externa.

- La confirmación de Fondos se conserva aunque falte transformar riesgo o resolver preparación.

- Una regularización pendiente no permite repetir el cambio.

- No libera riesgo como si no hubiera intercambio.

- Antes de ejecutar, un fallo permite cancelar solo lo comprobablemente cancelable.

- Con Fondos incierto conserva recursos y consulta.

#### Modalidad con entrega externa

Las entregas se coordinan mediante Liquidaciones de tesorería.

Cada salida o recepción propia exige evidencia de su componente.

Los compromisos correspondientes se consumen por salida efectiva. Una entrada esperada no aumenta saldo confirmado.

Las parcialidades se admiten únicamente si el mecanismo lo permite.

Aceptación:

| Situación | Resultado |
| --- | --- |
| Aceptación fuera de vigencia | Requiere otra propuesta |
| Fondos rechaza sin efectos | Regulariza compromisos cancelables |
| Fondos confirmado y Riesgos pendiente | Cambio monetario confirmado con regularización pendiente |
| Fondos incierto | Consulta original y mantiene cobertura |
| Cambio interno completo | Sin salida externa ficticia |
| Entrega externa no confirmada | No informa liquidación completa |

### CI-10-022. Resolver incidencia cambiaria

Consumidores: Originantes y Gestión administrativa.

Entrada: intercambio, incidencia, efectos, evidencia, tratamiento y aprobación exigible.

Salida: resolución, movimientos vinculados, exposición, compromisos y pendientes.

Reglas:

- Recupera Fondos antes de decidir sobre efectos desconocidos.

- Completa transformaciones y resoluciones pendientes.

- No simula otra compra a tasa actual para corregir un error.

- Una operación nueva exige condiciones y aceptación nuevas.

- El vencimiento de cotización no libera cobertura incierta.

- El error del banco no traslada automáticamente su pérdida al cliente.

- Las correcciones económicas conservan fundamento y referencia original.

### CI-10-023. Consultar operación cambiaria

Consumidores: Originantes, Fondos, Liquidaciones, Riesgos, Contabilidad, Reclamos y Gestión administrativa.

Entrada: propuesta, intercambio o actuación.

Salida:

- Modalidad.

- Condiciones y aceptación.

- Reserva del cliente.

- Compromiso propio y clase.

- Afectación de Riesgos.

- Intento y conjunto de Fondos.

- Resultado de cada componente.

- Transformación de exposición.

- Resolución del compromiso temporal.

- Entregas externas, cuando existan.

- Correcciones y pendientes.

Consultar no ejecuta otro cambio.

## 7. Contratos de Liquidaciones de tesorería

### CI-10-024. Recibir y validar obligación

Consumidores: Compensación de pagos, Procesamiento de consumos, Órdenes de inversión, Tenencias y valuación, Préstamos y
líneas, Operaciones de tesorería y Cambio de moneda.

Entrada: obligación, origen, composición, contraparte, importe o cantidad, moneda o instrumento, sentido, fecha,
mecanismo, destino y firmeza.

Salida: obligación validada, vínculo, vencimiento y cobertura pendiente.

Reglas: un agregado no se suma a componentes. Insuficiencia no elimina una obligación válida.

La composición se modifica por su propietario con versiones, sin sobrescribir lo liquidado.

Un cambio interno sin entrega externa no genera una obligación ficticia en este contrato.

### CI-10-025. Preparar y liberar instrucción

Consumidor: Gestión administrativa; también rige automatización aprobada.

Entrada: obligación, recursos, mecanismo, destino y aprobación o regla.

Salida: instrucción, referencia externa, envío y cobertura.

Reglas: separación de concertación y liberación. Insuficiencia mantiene pendiente.

La entrega contra pago relaciona componentes.

Después del envío no libera por falta de respuesta. Repetir recupera la instrucción.

### CI-10-026. Incorporar resultado externo

Consumidores: Participantes simulados, custodios responsables y Gestión administrativa autorizada.

Entrada: instrucción, referencia, participante, hecho, importe o cantidad, moneda o instrumento, fecha y evidencia.

Salida: componentes cumplidos, remanente, posición y comunicaciones.

Reglas: acuse no es pago. Pago 60 sobre 100 deja 40.

Los instrumentos se confirman con el propietario de la posición. Tesorería no modifica por su cuenta custodia de
clientes.

### CI-10-027. Consultar obligación o liquidación

Consumidores: Originante, Posiciones y liquidez, Operaciones de tesorería, Cambio de moneda, Riesgos, Cierres y Gestión
administrativa.

Entrada: obligación, instrucción, referencia o actuación.

Salida: composición, controles, destino, recursos, envíos, confirmaciones, remanentes y diferencias.

Distingue recepción, instrucción y cumplimiento. No reenvía ni vuelve a pagar.

### CI-10-028. Resolver parcialidad, incertidumbre o incumplimiento

Consumidores: Originante y Gestión administrativa.

Entrada: obligación o instrucción, incidencia, evidencia, remanente, tratamiento y autorizaciones.

Salida: resolución, cobertura, actuación vinculada, costos y pendientes.

Reglas: pagados 60 de 100, solo procede instruir los 40 restantes cuando el mecanismo lo permite.

Un incumplimiento se informa a Riesgos.

No vuelve a cobrar al cliente que cumplió.

### CI-10-029. Conciliar cuentas externas y liquidaciones

Consumidores: Participantes y custodios simulados; Gestión administrativa.

Entrada: cuentas, obligaciones, liquidaciones, corte, saldos, movimientos y evidencia.

Salida: coincidencias, desfases, diferencias y resoluciones.

Reglas: externo confirma pago 100 e interno pendiente exige recuperar ese pago, no reenviar.

Un débito externo desconocido no se carga automáticamente a un cliente.

Los ajustes manuales requieren aprobación.

## 8. Cierre operativo

### CI-10-031. Elaborar y consultar cierre

Proveedor: Posiciones y liquidez. Consumidores: Riesgos, Contabilidad y Gestión administrativa.

Entrada: corte, posiciones, compromisos, conciliaciones y valuaciones; para consulta, cierre y versión.

Salida: saldos por moneda, capacidad, faltantes, tránsito, obligaciones, incertidumbres, valuaciones y limitaciones.

Separa compromisos de preparación cambiaria pendientes, resueltos y exposiciones relacionadas.

Reglas: no cierra períodos contables. Los faltantes necesarios impiden informar integridad completa.

Las rectificaciones conservan corte, versión anterior y causa.

Aceptación: USD 100 a referencia contable 1.100 se presentan como equivalente ARS 110.000 conservando la posición USD.

## 9. Eventos

Todos identifican hecho, emisor, objeto, versión o secuencia, operación, causa, momento y referencias.

### CI-10-033. Comunicar disponibilidad o compromiso

Emisor: Posiciones y liquidez. Consumidores: Liquidaciones, Operaciones de tesorería, Cambio de moneda, Operaciones de
efectivo, Riesgos y Contabilidad.

Contenido: posición, moneda, ubicación, clase de compromiso, movimiento o resolución, saldo, capacidad y origen.

Distingue:

- Compromiso de salida.

- Liberación por cancelación.

- Resolución de preparación interna.

- Salida comprobada.

- Entrada comprobada.

- Tránsito.

RESOLVED_INTERNAL_FX no representa salida ni ingreso.

### CI-10-034. Comunicar necesidad o proyección

Emisor: Posiciones y liquidez. Consumidores: Operaciones de tesorería, Liquidaciones, Riesgos y Gestión administrativa.

Contenido: corte, moneda, horizonte, certeza, faltante, excedente, supuestos y plan.

No concede recursos ni autoriza financiación.

### CI-10-035. Comunicar operación propia

Emisor: Operaciones de tesorería. Consumidores: Posiciones y liquidez, Liquidaciones, Riesgos, Contabilidad, Documentos
y Comunicaciones.

Contenido: concertación, ejecución, devengamiento, obligación, cumplimiento o cambio.

Concertar no comunica dinero recibido. Cobrar interés no duplica lo devengado.

### CI-10-036. Comunicar cotización o corrección

Emisor: Cambio de moneda. Consumidores: Contabilidad, Operaciones de tesorería, Tenencias y servicios usuarios.

Contenido: par, sentido, clase, valor, unidad, fuente, momento, vigencia y corrección.

No modifica silenciosamente operaciones aceptadas ni convierte una referencia en oferta ejecutable.

### CI-10-037. Comunicar operación cambiaria

Emisor: Cambio de moneda. Consumidores: Originante, Posiciones y liquidez, Liquidaciones, Riesgos, Contabilidad,
Documentos y Comunicaciones.

Contenido: intercambio, modalidad, aceptación, monedas, importes, resultado de Fondos, riesgo, compromisos, entregas y
pendientes.

Distingue confirmación monetaria de regularización interna.

No comunica una entrega externa para una modalidad que no la tiene.

### CI-10-038. Comunicar liquidación o conciliación

Emisor: Liquidaciones de tesorería. Consumidores: Originante, Posiciones y liquidez, Operaciones de tesorería, Cambio de
moneda, Riesgos, Contabilidad y Gestión administrativa.

Contenido: obligación, instrucción o diferencia, cumplimiento, importe, remanente y evidencia.

Enviar no equivale a liquidar. Una diferencia no autoriza un ajuste.

### CI-10-039. Comunicar cierre o valuación

Emisor: Posiciones y liquidez. Consumidores: Riesgos, Cierres contables, Información contable y Gestión administrativa.

Contenido: cierre, corte, versión, posiciones, valuaciones, monedas, resultados y limitaciones.

Distingue realizado y valuación. No informa un cierre incompleto como definitivo.

## 10. Integraciones consumidas

### 10.1. Institución, personas y contratación

| Contratos | Uso |
| --- | --- |
| CI-01-004 | Facultades internas |
| CI-01-005 | Capacidades |
| CI-01-011 | Condiciones comerciales |
| CI-01-014 y CI-01-015 | Calendarios |
| CI-02-002 | Participantes y contrapartes |
| CI-02-021 | Representación |
| CI-03-022 | Acuerdos |

Cuando el acto exige mantener condiciones hasta decidir se utilizan preparaciones explícitas.

Identificar una contraparte no verifica sus datos de liquidación.

### 10.2. Cuentas, pagos y financiación

| Contratos | Uso |
| --- | --- |
| CI-04-012 | Posiciones y antecedentes |
| CI-04-013, CI-04-015 y CI-04-016 | Reservas del cliente |
| CI-04-018 | Conjunto interno de cambio |
| CI-04-021 a CI-04-023 | Corrección y recuperación |
| CI-04-030 y CI-04-037 | Resultado físico |
| CI-04-040 | Comprobación informativa de cuenta |
| CI-05-036, CI-05-037 y CI-05-044 | Obligaciones de pagos |
| CI-06-018 y CI-06-032 | Posiciones y actuaciones de financiación |

Los intentos monetarios de Fondos se consultan y resuelven mediante su protocolo. No se presume aborto por timeout.

### 10.3. Tarjetas e inversiones

| Contratos | Uso |
| --- | --- |
| CI-07-036, CI-07-037 y CI-07-048 | Obligaciones y liquidaciones de tarjetas |
| CI-08-009 | Vencimientos de depósitos |
| CI-08-026 y CI-08-027 | Liquidaciones y órdenes |
| CI-08-030 | Posiciones de clientes para entrega |
| CI-08-033 | Derechos económicos |
| CI-08-039 y CI-08-041 | Traslados |
| CI-08-045 | Cambios de órdenes o liquidación |

Dinero, instrumentos y custodia mantienen confirmaciones relacionadas y diferentes.

### 10.4. Riesgos

| Contrato | Uso |
| --- | --- |
| CI-11-005 | Exposición y capacidad informativa |
| CI-11-006 | Comprometer capacidad exigible |
| CI-11-007 | Transformar, ajustar o liberar afectación |
| CI-11-008 | Evaluación y escenarios |
| CI-11-009 | Excesos e incidentes |

Cambio de moneda participa expresamente como consumidor de compromiso y transformación para sus intercambios.

Una transformación de cambio interno identifica ambas monedas, conjunto de Fondos, afectación sustituida y exposición
resultante.

Los agregados posteriores se concilian con los hechos individuales para evitar doble cómputo.

### 10.5. Contabilidad y otros módulos

CI-09-005 a CI-09-007 integran hechos y antecedentes.

CI-09-015 y CI-09-016 permiten confirmar integridad y auxiliares al corte.

Restricciones mantiene medidas; los controles especializados sus decisiones; Criterios tributarios sus reglas.

Gestión administrativa, Documentos, Comunicaciones e Información regulatoria conservan sus responsabilidades.

## 11. Criterios de aceptación compartidos

| Situación | Resultado exigido |
| --- | --- |
| Compromisos concurrentes incompatibles | No exceden capacidad |
| Cambio interno confirmado | Ambas piernas de Fondos relacionadas |
| Fondos incierto | Mantiene compromisos y recuperación |
| Fondos rechaza sin efectos | Libera solo lo cancelable |
| Riesgos pendiente tras ejecución | Conserva obligación de transformar |
| Riesgo transformado | No vuelve a sumar el compromiso sustituido |
| Preparación interna resuelta | Sin movimiento externo ficticio |
| Salida externa comprobada | Consume compromiso y mueve posición una vez |
| Entrada esperada | No aumenta saldo |
| Mensaje duplicado | No repite movimiento ni obligación |
| Reasignación | Sin intervalo descubierto ni doble compromiso |
| Corrección | Conserva original y fundamento |
| Cierre incompleto | Expone faltantes y limitaciones |

## 12. Correspondencia funcional

| Operación | Contratos principales |
| --- | --- |
| OF-10-01. Cuentas y ubicaciones | CI-10-001, CI-10-002 |
| OF-10-02. Movimientos propios | CI-10-003, CI-10-032, CI-10-033 |
| OF-10-03. Efectivo | CI-10-004, CI-10-005, CI-10-032 |
| OF-10-04. Compromisos | CI-10-006, CI-10-007, CI-10-033 |
| OF-10-05. Proyecciones | CI-10-008, CI-10-009, CI-10-034 |
| OF-10-06. Faltantes y excedentes | CI-10-010, CI-10-009, CI-10-034 |
| OF-10-07. Financiación propia | CI-10-011, CI-10-013, CI-10-015, CI-10-035 |
| OF-10-08. Inversiones propias | CI-10-012, CI-10-013, CI-10-017, CI-10-035 |
| OF-10-09. Rendimientos | CI-10-014, CI-10-015, CI-10-017, CI-10-035 |
| OF-10-10. Modificaciones | CI-10-016, CI-10-017, CI-10-035 |
| OF-10-11. Cotizaciones | CI-10-018, CI-10-019, CI-10-036 |
| OF-10-12. Cotizar intercambio | CI-10-020, CI-10-023 |
| OF-10-13. Ejecutar cambio | CI-10-021, CI-10-023, CI-10-037 |
| OF-10-14. Incidencias cambiarias | CI-10-022, CI-10-023, CI-10-037 |
| OF-10-15. Obligaciones | CI-10-024, CI-10-027 |
| OF-10-16. Instrucciones | CI-10-025, CI-10-006, CI-10-027 |
| OF-10-17. Cumplimientos externos | CI-10-026, CI-10-003, CI-10-015, CI-10-038 |
| OF-10-18. Incumplimientos | CI-10-028, CI-10-027, CI-10-038 |
| OF-10-19. Conciliación | CI-10-029, CI-10-027, CI-10-038 |
| OF-10-20. Valuación y cierre | CI-10-030, CI-10-031, CI-10-039 |
