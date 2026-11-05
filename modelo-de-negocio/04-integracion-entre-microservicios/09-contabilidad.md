# Contratos de integración — Módulo 9. Contabilidad

## 1. Propósito y alcance

Este documento define los contratos de integración proporcionados por los servicios:

- Registración contable.

- Cierres contables.

- Información contable.

Comprende recepción e integración de hechos económicos, asientos, correcciones, representación multimoneda,
conciliación, cierres e informes financieros.

Contabilidad representa activos, pasivos, patrimonio, ingresos, gastos y posiciones de orden. No sustituye los saldos
operativos, las deudas contractuales, las tenencias ni las disponibilidades administradas por los otros módulos.

Información regulatoria conserva la elaboración y presentación de declaraciones regulatorias.

## 2. Catálogo de contratos

Todos los contratos corresponden a la versión 1.

| Identificador | Contrato | Proveedor | Clase |
| --- | --- | --- | --- |
| CI-09-001 | Administrar el plan de cuentas | Registración contable | Solicitud de actuación |
| CI-09-002 | Consultar el plan de cuentas | Registración contable | Consulta |
| CI-09-003 | Definir y aprobar reglas de contabilización | Registración contable | Solicitud de actuación |
| CI-09-004 | Consultar una regla de contabilización | Registración contable | Consulta |
| CI-09-005 | Incorporar un hecho económico o antecedente | Registración contable | Solicitud de actuación |
| CI-09-006 | Consultar el tratamiento de un hecho económico | Registración contable | Consulta |
| CI-09-007 | Completar la integración de una operación compartida | Registración contable | Solicitud de actuación |
| CI-09-008 | Registrar un asiento automático | Registración contable | Solicitud de actuación |
| CI-09-009 | Preparar y confirmar un ajuste manual | Registración contable | Solicitud de actuación |
| CI-09-010 | Revertir o reclasificar una registración | Registración contable | Solicitud de actuación |
| CI-09-011 | Reexpresar posiciones contables en moneda extranjera | Registración contable | Solicitud de actuación |
| CI-09-012 | Consultar un asiento o una actuación contable | Registración contable | Consulta |
| CI-09-013 | Abrir períodos y administrar cortes | Cierres contables | Solicitud de actuación |
| CI-09-014 | Consultar un período y comprobar admisión contable | Cierres contables | Consulta |
| CI-09-015 | Incorporar una confirmación de integridad del emisor | Cierres contables | Solicitud de actuación |
| CI-09-016 | Incorporar un auxiliar al corte | Cierres contables | Solicitud de actuación |
| CI-09-017 | Conciliar auxiliares con contabilidad | Cierres contables | Solicitud de actuación |
| CI-09-018 | Resolver una diferencia contable | Cierres contables | Solicitud de actuación |
| CI-09-019 | Ejecutar un cierre diario | Cierres contables | Solicitud de actuación |
| CI-09-020 | Ejecutar un cierre mensual o de ejercicio | Cierres contables | Solicitud de actuación |
| CI-09-021 | Resolver el tratamiento de un hecho tardío o una reapertura | Cierres contables | Solicitud de actuación |
| CI-09-022 | Consultar un cierre y sus controles | Cierres contables | Consulta |
| CI-09-023 | Administrar estructuras de información contable | Información contable | Solicitud de actuación |
| CI-09-024 | Consultar una estructura de información contable | Información contable | Consulta |
| CI-09-025 | Elaborar y emitir un informe contable | Información contable | Solicitud de actuación |
| CI-09-026 | Consultar saldos, movimientos y su justificación | Información contable | Consulta |
| CI-09-027 | Consultar un informe emitido | Información contable | Consulta |
| CI-09-028 | Rectificar un informe emitido | Información contable | Solicitud de actuación |
| CI-09-029 | Comunicar un cambio de plan o regla contable | Registración contable | Evento |
| CI-09-030 | Comunicar el tratamiento de un hecho o la confirmación de un asiento | Registración contable | Evento |
| CI-09-031 | Comunicar un cambio de período, cierre o reapertura | Cierres contables | Evento |
| CI-09-032 | Comunicar una diferencia de conciliación y su tratamiento | Cierres contables | Evento |
| CI-09-033 | Comunicar la emisión o rectificación de un informe | Información contable | Evento |

## 3. Reglas comunes de integración

### 3.1. Identificación y procedencia

Cada hecho económico identifica:

- Operación y ocurrencia económica.

- Servicio responsable del hecho.

- Tipo y versión del hecho.

- Conceptos, importes y monedas.

- Participantes o posiciones pertinentes.

- Fecha económica y fecha de recepción.

- Referencias operativas y evidencias.

- Correcciones relacionadas.

Las solicitudes administrativas agregan actuación, actor, facultades, versión esperada y aprobación cuando corresponda.

La identidad del hecho es independiente de las comunicaciones que lo transportan.

Una misma referencia con igual contenido recupera su tratamiento. La misma referencia con contenido distinto genera
inconsistencia.

### 3.2. Operaciones compartidas

La regla contable establece qué consecuencias reconoce, qué antecedentes necesita y qué servicio es autoridad sobre cada
dato.

Una operación puede producir varios hechos independientes y varios antecedentes del mismo hecho. La correlación no se
realiza solamente por el identificador general de la operación: también distingue ocurrencia y consecuencia económica.

Una comunicación del coordinador y otra del ejecutor no generan necesariamente dos asientos.

El desembolso informado por Préstamos y líneas y su acreditación informada por Fondos pueden justificar una misma
registración. No crean dos préstamos ni dos depósitos.

### 3.3. Partida doble y confirmación

Todo asiento confirmado mantiene igualdad entre débitos y créditos.

Sus partidas, confirmación y actualización de saldos forman una registración completa. Una interrupción no permite
presentar una parte como asiento terminado.

Las partidas monetarias utilizan importes positivos y expresan su sentido mediante débito o crédito.

Los registros de orden se equilibran dentro de su esquema y se presentan separados de los patrimoniales.

### 3.4. Autoridad operativa

Contabilidad utiliza los importes determinados por el responsable económico.

No modifica cuotas, capital, intereses, cargos ni unidades para equilibrar un asiento.

Un rechazo contable no revierte automáticamente una operación bancaria. Un asiento manual tampoco ejecuta un débito,
cancela una deuda o modifica custodia.

Los errores operativos se corrigen en el servicio de origen y se relacionan con su tratamiento contable.

### 3.5. Fechas, períodos y concurrencia

Se conservan fecha económica, recepción, contabilización y período de imputación.

El día económico transcurre de 00:00 a 24:00 en America/Argentina/Buenos_Aires.

El corte de compensación no sustituye el corte contable. Una operación posterior a las 18:00 puede pertenecer al mismo
día económico y al siguiente ciclo de compensación.

La admisión de un asiento queda ordenada frente al cierre del período. Consultar que un período está abierto no concede
permiso indefinido para registrar después de su cierre.

### 3.6. Monedas y precisión

El peso argentino es la moneda de presentación contable.

Cada partida conserva moneda e importe originales y su equivalente en pesos cuando corresponda.

La cotización identifica finalidad contable, fecha, fuente y versión. No se confunde con una oferta comercial de cambio.

Los importes contables se redondean a centavos, al más cercano, elevando empates. Las diferencias exclusivas de
conversión utilizan cuentas específicas de redondeo, sin modificar obligaciones de clientes.

### 3.7. Pendientes y cuentas transitorias

Un antecedente incompleto conserva causa, responsable y seguimiento.

No se utiliza una cuenta genérica para ocultar datos faltantes o equilibrar una operación desconocida.

Una cuenta transitoria exige regla aprobada y una situación económica real, con origen, contrapartida, responsable y
procedimiento de resolución.

No existe un importe mínimo que permita ignorar diferencias.

### 3.8. Aprobaciones, correcciones y eventos

El plan, las reglas, los ajustes manuales, las resoluciones de diferencias y las reaperturas requieren preparación y
aprobación por personas diferentes.

Una modificación material de la propuesta invalida la aprobación anterior.

Los asientos confirmados no se borran ni sobrescriben. Las correcciones conservan original, fundamento y efecto
relacionado.

Los eventos identifican hecho, objeto, versión o secuencia, causa, fecha y referencias. Consumir un evento contable no
vuelve a ejecutar el negocio ni genera otra contabilización del mismo hecho.

## 4. Contratos de Registración contable

### CI-09-001. Administrar el plan de cuentas

Proveedor: Registración contable. Consumidor: Gestión administrativa.

Entrada: propuesta, cuenta, código, denominación, naturaleza, clasificación, jerarquía, monedas, dimensiones, vigencia y
aprobación separada.

Actuación: crea, modifica, habilita o retira una versión del plan.

Salida: cuenta y versión, vigencia, capacidades de uso, transición y requisitos pendientes.

Reglas y aceptación: las cuentas de agrupación no reciben partidas. Un código con historia no cambia de naturaleza para
reutilizarlo. Una cuenta con saldo no se retira sin tratamiento expreso. La baja conserva historia y correcciones
autorizadas.

### CI-09-002. Consultar el plan de cuentas

Proveedor: Registración contable. Consumidores: Cierres contables, Información contable y Gestión administrativa.

Entrada: cuenta, clasificación, versión o fecha de vigencia.

Salida: estructura, naturaleza, jerarquía, monedas, dimensiones, admisión de partidas, vigencia e historia.

Reglas: distingue cuentas de agrupación y de movimiento, patrimoniales y de orden. La consulta no modifica el plan ni
autoriza una registración.

### CI-09-003. Definir y aprobar reglas de contabilización

Proveedor: Registración contable. Consumidor: Gestión administrativa.

Entrada: tipo y versión de hecho, consecuencias, fuentes responsables, antecedentes, correlación, cuentas, dimensiones,
importes, fechas, excepciones, correcciones, ejemplos y aprobación separada.

Actuación: comprueba integridad, equilibrio, ausencia de duplicación y tratamiento de transición y habilita la regla.

Salida: regla y versión, vigencia, fuentes exigidas y condiciones de aplicación.

Reglas y aceptación: distingue principal, rendimiento, comisión, tributo y diferencia. Una versión nueva no reinterpreta
automáticamente hechos históricos. Una regla sin contrapartida no se aprueba y ninguna regla autoriza por sí sola una
operación de negocio.

### CI-09-004. Consultar una regla de contabilización

Proveedor: Registración contable. Consumidores: Cierres contables, Información contable y Gestión administrativa.

Entrada: regla, tipo de hecho y versión, o fecha y consecuencia económica.

Salida: regla aplicable, antecedentes requeridos, fuentes, partidas, dimensiones, excepciones y correcciones.

Reglas: un tipo desconocido no se resuelve seleccionando una regla parecida. La consulta histórica conserva la versión
utilizada.

### CI-09-005. Incorporar un hecho económico o antecedente

Proveedor: Registración contable. Consumidores: servicios responsables de hechos económicos y Gestión administrativa
para incorporaciones respaldadas por evidencia.

Entrada: identidad del hecho, emisor, operación, ocurrencia, tipo, versión, conceptos, importes, monedas, fechas,
referencias y evidencias.

Actuación: comprueba procedencia, repetición, consistencia y regla aplicable. Admite el procesamiento, integra
antecedentes o registra un impedimento.

Salida: recepción identificada, hecho admitido, pendiente, inconsistente o ya tratado; referencias de asientos cuando
existan.

Reglas y aceptación: recibir no equivale a contabilizar. Tres comunicaciones del mismo hecho generan una sola
consecuencia. Si falta moneda o regla, queda pendiente sin cuenta arbitraria. El resultado contable no revierte el
negocio.

Los eventos de los módulos de origen se incorporan con estas mismas reglas, sin crear una identidad económica distinta
por su canal de recepción.

### CI-09-006. Consultar el tratamiento de un hecho económico

Proveedor: Registración contable. Consumidores: servicio emisor, Cierres contables, Información contable y Gestión
administrativa.

Entrada: hecho o referencia de origen completa.

Salida: antecedentes recibidos, regla, integración, validaciones, impedimentos, asientos, correcciones y pendientes.

Reglas: permite recuperar el resultado original sin reenviar un hecho como nuevo. La falta de asiento no demuestra
ausencia de ejecución operativa.

### CI-09-007. Completar la integración de una operación compartida

Proveedor: Registración contable. Consumidores: servicios responsables de los antecedentes y Gestión administrativa.

Entrada: operación, ocurrencia y consecuencia económica, integración existente y referencias de antecedentes faltantes.

Actuación: incorpora información de sus fuentes responsables y comprueba coincidencia de referencias, conceptos,
importes y fechas.

Salida: integración completa y apta para registrar, o diferencias y antecedentes pendientes.

Reglas y aceptación: Préstamos informa 500 y Fondos acredita 500: se integran conforme a la regla. Si uno informa 500 y
otro 490 sin retenciones identificadas, no se inventa una comisión de 10.

Los hechos independientes pueden registrarse separadamente si la regla lo contempla.

### CI-09-008. Registrar un asiento automático

Proveedor: Registración contable. Consumidor: Cierres contables para regularizaciones identificadas. El mismo
comportamiento rige el procesamiento automático de hechos admitidos.

Entrada: hecho o integración validada, regla y versión, consecuencia económica y tratamiento de período autorizado.

Actuación: genera partidas, comprueba cuentas, dimensiones, moneda, equilibrio y admisión y confirma asiento y saldos
conjuntamente.

Salida: asiento confirmado, partidas, referencias y período; o impedimento.

Reglas y aceptación: no requiere aprobación humana individual si aplica una regla aprobada. Débitos 100 y créditos 90
impiden confirmar. Una respuesta perdida recupera el mismo asiento. Un importe cero no genera partidas ficticias.

### CI-09-009. Preparar y confirmar un ajuste manual

Proveedor: Registración contable. Consumidor: Gestión administrativa.

Entrada: motivo, partidas, fecha, período, fundamento, evidencia, originales relacionados y aprobación separada sobre el
contenido exacto.

Actuación: prepara, valida y, aprobada la propuesta, confirma el ajuste.

Salida: propuesta, aprobación, asiento o requisitos pendientes.

Reglas y aceptación: el preparador no aprueba su ajuste. Cambiar partidas exige nueva aprobación. Corregir clasificación
contable no debita a un cliente. Un error del producto se deriva a su responsable y no se oculta mediante un asiento.

### CI-09-010. Revertir o reclasificar una registración

Proveedor: Registración contable. Consumidores: Cierres contables y Gestión administrativa.

Entrada: asiento original, alcance, causa, tratamiento total o parcial, evidencia, período y aprobación cuando sea
manual.

Actuación: comprueba correcciones previas y registra partidas vinculadas.

Salida: reversión, ajuste o reclasificación, importe todavía reversible y efecto resultante.

Reglas y aceptación: una reversión de 40 sobre 100 deja como máximo 60 reversibles de ese efecto. La reclasificación no
inventa ingreso ni dinero. Una diferencia de cotización posterior no se trata como error original.

### CI-09-011. Reexpresar posiciones contables en moneda extranjera

Proveedor: Registración contable. Consumidor: Cierres contables.

Entrada: corte, posiciones, clase monetaria o no monetaria, criterio aplicable, cotización de cierre y versiones.

Actuación: determina equivalentes y diferencias respecto de los valores reconocidos y registra los ajustes pertinentes.

Salida: cálculo, cotización, diferencias de cambio o redondeo, asientos y faltantes.

Reglas y aceptación: un pasivo USD 100 con cotización que pasa de 1.000 a 1.100 cambia su equivalente de ARS 100.000 a
ARS 110.000, sin modificar los USD 100 adeudados.

No ejecuta compra o venta de moneda. Sin cotización necesaria no concluye la valuación definitiva.

### CI-09-012. Consultar un asiento o una actuación contable

Proveedor: Registración contable. Consumidores: Cierres contables, Información contable, servicio emisor del hecho y
Gestión administrativa.

Entrada: asiento, actuación o referencia económica y alcance autorizado.

Salida: partidas, moneda original, equivalente, fechas, período, hecho, regla, aprobaciones, documentos y correcciones.

Reglas: distingue propuesta de asiento confirmado. Conserva original y reversión y no modifica registros.

## 5. Tratamiento de devengamientos y registros de orden

Los hechos de intereses, previsiones, garantías, compromisos y custodia ingresan mediante CI-09-005, se integran cuando
corresponde mediante CI-09-007 y se registran mediante CI-09-008.

Su naturaleza no requiere otro contrato de recepción con las mismas funciones.

### 5.1. Devengamientos y previsiones

El hecho identifica período, base, cálculo de origen y si informa una variación o un acumulado.

Registración contable conserva lo ya reconocido para determinar la diferencia aplicable. No registra como nueva
variación un acumulado ya incorporado.

Cobrar un interés previamente devengado cancela el derecho a cobrar; no vuelve a reconocer el mismo ingreso.

Pagar intereses de depósitos cancela la obligación; no duplica el gasto.

Una previsión requerida que pasa de 20 a 25 produce aumento 5, no otro gasto de 25.

### 5.2. Descuentos de cheques

La regla representa el nominal de financiación y el interés retenido pendiente de devengamiento según los hechos de
Préstamos y líneas.

El anticipo neto, las retenciones y el nominal no se suman como entregas independientes.

El cobro posterior del cheque cancela la financiación correspondiente, sin reconocer otro depósito libre del cliente.

### 5.3. Compromisos y custodia

Las líneas no utilizadas no se registran como capital prestado por todo su límite.

Las garantías no constituyen efectivo disponible del banco.

Las inversiones de clientes se representan en su esquema de orden o custodia, sin incorporarlas como inversiones
propias.

Una ejecución de garantía o adjudicación puede originar un hecho patrimonial distinto, respaldado por su evidencia y
regla.

## 6. Contratos de Cierres contables

### CI-09-013. Abrir períodos y administrar cortes

Proveedor: Cierres contables. Consumidor: Gestión administrativa.

Entrada: ejercicio, períodos mensuales, fechas diarias, cortes y actuación autorizada.

Actuación: crea y habilita períodos y administra su estado.

Salida: período, fechas comprendidas, condición de admisión y vigencia.

Reglas y aceptación: ejercicio calendario del 1 de enero al 31 de diciembre. No admite superposición. Un período abierto
admite registraciones ordinarias; en cierre, regularizaciones identificadas; cerrado, tratamiento de hechos tardíos.

Las operaciones del día posterior pueden continuar mientras se cierra el anterior.

### CI-09-014. Consultar un período y comprobar admisión contable

Proveedor: Cierres contables. Consumidores: Registración contable, Información contable y Gestión administrativa.

Entrada: fecha económica, período, tipo de actuación y autorización de regularización o reapertura cuando exista.

Salida: estado, versión de control, admisión o impedimento y procedimiento requerido.

Reglas: la confirmación del asiento debe comprobar que la admisión continúa vigente. Una consulta anterior al cierre no
habilita registrar después de este.

### CI-09-015. Incorporar una confirmación de integridad del emisor

Proveedor: Cierres contables. Consumidores: servicios responsables de hechos económicos.

Entrada: emisor, ámbito, fecha y corte, referencia de confirmación, conjunto o secuencia de hechos comprendidos,
faltantes conocidos y pendientes.

Actuación: relaciona la declaración del emisor con los hechos efectivamente recibidos y sus tratamientos.

Salida: confirmación incorporada, cobertura comprobada, diferencias y control pendiente o satisfecho.

Reglas y aceptación: también se identifica expresamente un ámbito sin hechos. La ausencia de mensajes no demuestra
completitud. Una confirmación que declara hechos todavía no recibidos mantiene el control pendiente.

Una rectificación conserva la confirmación anterior y su causa.

### CI-09-016. Incorporar un auxiliar al corte

Proveedor: Cierres contables. Consumidores: servicios responsables de posiciones operativas.

Entrada: emisor, auxiliar, corte, alcance, monedas, conceptos, saldos, movimientos o referencias de detalle, versión y
pendientes conocidos.

Actuación: valida coherencia temporal y conceptual y conserva el auxiliar para conciliación.

Salida: auxiliar identificado, controles de recepción, faltantes o incompatibilidades.

Reglas y aceptación: distingue saldo contable de cuenta, disponible, reservas y afectaciones. No compara disponibilidad
70 con pasivo 100 como si fueran el mismo concepto.

La revisión del auxiliar conserva la versión anterior.

### CI-09-017. Conciliar auxiliares con contabilidad

Proveedor: Cierres contables. Consumidor: Gestión administrativa; el mismo comportamiento rige los controles de cierre.

Entrada: corte, ámbito, auxiliares y saldos contables comparables, monedas, dimensiones y reglas de correspondencia.

Actuación: compara conceptos equivalentes y determina diferencias.

Salida: conciliación, alcance, posiciones comparadas, coincidencias, diferencias y responsables.

Reglas y aceptación: incluye depósitos, financiación, tarjetas, disponibilidades, liquidaciones, cambio, custodia,
tributos y transitorias.

Capital operativo 500 y activo contable 490 sin explicación genera diferencia 10. Cortes distintos deben homogeneizarse
antes de concluir un faltante.

### CI-09-018. Resolver una diferencia contable

Proveedor: Cierres contables. Consumidor: Gestión administrativa.

Entrada: diferencia, diagnóstico, evidencia, responsable, actuación propuesta, fecha objetivo y aprobación separada.

Actuación: determina origen operativo, contable o temporal, solicita corrección al responsable y vuelve a comprobar las
consecuencias.

Salida: resolución, actuaciones vinculadas, comprobación posterior y pendientes.

Reglas y aceptación: una duplicación confirmada se corrige. Un desfase requiere referencias comprobables y seguimiento.
Aprobar seguimiento no convierte una causa desconocida en gasto válido ni permite forzar coincidencia.

### CI-09-019. Ejecutar un cierre diario

Proveedor: Cierres contables. Consumidor: Gestión administrativa.

Entrada: fecha económica, actuación de cierre y aprobación sobre sus controles.

Actuación: fija corte, comprueba integridad de emisores, hechos, equilibrio, auxiliares y transitorias; coordina
regularizaciones y confirma el cierre.

Salida: cierre, versión, controles, saldos, pendientes representados y evidencias.

Reglas y aceptación: se inicia el siguiente día operativo. No concluye con emisores sin confirmar integridad, asientos
desequilibrados, duplicaciones confirmadas o hechos conocidos sin tratamiento.

Una obligación externa incierta puede continuar si está correctamente representada y conciliada como pendiente. Retomar
un cierre interrumpido no repite ajustes.

### CI-09-020. Ejecutar un cierre mensual o de ejercicio

Proveedor: Cierres contables. Consumidor: Gestión administrativa.

Entrada: período, cierres diarios, devengamientos, previsiones, valuaciones, conciliaciones y aprobación.

Actuación: verifica acumulados y ajustes y confirma el período. En ejercicio, transfiere resultados y establece
continuidad de saldos iniciales.

Salida: cierre, saldos, resultado, asientos relacionados, apertura siguiente y controles.

Reglas y aceptación: ingresos 100 y gastos 70 producen resultado 30, sujeto a los demás conceptos registrados. Abrir el
siguiente ejercicio no duplica activos, pasivos ni patrimonio.

Transferir resultados no distribuye ganancias ni constituye otro ingreso operativo. Una previsión exigida faltante
impide informar cierre completo.

### CI-09-021. Resolver el tratamiento de un hecho tardío o una reapertura

Proveedor: Cierres contables. Consumidores: Registración contable y Gestión administrativa.

Entrada: hecho o corrección, fecha económica, recepción real, período afectado, impacto, informes emitidos, propuesta y
aprobación separada.

Actuación: determina ajuste en período abierto o reapertura de alcance identificado y coordina registro y repetición de
controles.

Salida: tratamiento autorizado, alcance, período, regularización y cierres o informes afectados.

Reglas:

- Día cerrado y mes abierto: reapertura controlada de la fecha y nuevo cierre.

- Mes cerrado y ejercicio todavía no emitido como definitivo: reapertura del período cuando cambian sus cifras.

- Ejercicio emitido: ajuste identificado de períodos anteriores y determinación de rectificaciones.

Aceptación: sin aprobación no registra en el período cerrado. La reapertura conserva fecha real de recepción y exige
revisar los períodos posteriores afectados.

### CI-09-022. Consultar un cierre y sus controles

Proveedor: Cierres contables. Consumidores: Registración contable, Información contable, Información regulatoria y
Gestión administrativa.

Entrada: período, cierre, conciliación o actuación.

Salida: estado, corte, versión, integridad de fuentes, auxiliares, diferencias, resoluciones, aprobaciones, reaperturas
y resultado.

Reglas: permite reconstruir qué información respaldó el cierre. No presenta un cierre anterior como vigente si fue
reabierto o sustituido por otro resultado.

## 7. Contratos de Información contable

### CI-09-023. Administrar estructuras de información contable

Proveedor: Información contable. Consumidor: Gestión administrativa.

Entrada: estructura, cuentas, dimensiones, agrupaciones, fórmulas, comparativos, vigencia y aprobación.

Actuación: valida cobertura y ausencia de duplicaciones y habilita una versión.

Salida: estructura aprobada, vigencia, correspondencias y requisitos.

Reglas y aceptación: una cuenta nueva sin ubicación impide un informe completo. Las cuentas de orden se presentan
separadas del patrimonio. Cambiar agrupación no modifica asientos.

### CI-09-024. Consultar una estructura de información contable

Proveedor: Información contable. Consumidores: Cierres contables, Información regulatoria y Gestión administrativa.

Entrada: estructura, informe, versión o fecha.

Salida: agrupaciones, fórmulas, cuentas y dimensiones comprendidas, moneda, comparativos y vigencia.

Reglas: conserva el criterio histórico. Una estructura no sustituye las reglas de contabilización.

### CI-09-025. Elaborar y emitir un informe contable

Proveedor: Información contable. Consumidores: Gestión administrativa e Información regulatoria dentro de sus
facultades.

Entrada: tipo de informe, período, corte, estructura y versión, comparativos, condición solicitada y aprobación de
emisión cuando corresponda.

Actuación: reúne registros, calcula agrupaciones, verifica totales y condición del período y conserva la emisión.

Salida: informe, versión, moneda, corte, condición provisional o definitiva, controles y referencia documental.

Reglas y aceptación: un informe definitivo utiliza períodos cerrados. Falta de datos no equivale a cero. Activos 150,
pasivos 100 y patrimonio 50 satisfacen igualdad patrimonial.

La emisión definitiva de ejercicio se comunica a Cierres contables para que los hechos posteriores sigan el tratamiento
correspondiente.

### CI-09-026. Consultar saldos, movimientos y su justificación

Proveedor: Información contable. Consumidores: Cierres contables, Información regulatoria, servicios responsables del
origen y Gestión administrativa.

Entrada: cuenta, dimensiones, moneda, período, corte, informe o cifra y alcance autorizado.

Salida: saldo inicial, débitos, créditos, saldo final y recorrido hasta partidas, asientos, hechos, reglas y evidencias.

Reglas y aceptación: conserva cortes y versiones históricas. En cuenta de activo, inicial 100, débitos 30 y créditos 20
determinan final 110. Una reversión muestra original y corrección. La consulta no modifica registros.

### CI-09-027. Consultar un informe emitido

Proveedor: Información contable. Consumidores: Información regulatoria, Gestión administrativa y Comunicaciones para
entrega autorizada.

Entrada: informe, versión o referencia de emisión.

Salida: contenido conservado, corte, estructura, condición del período, aprobación, documento y rectificaciones
relacionadas.

Reglas: consultar una emisión recupera sus cifras originales, no un recálculo silencioso con datos actuales. Identifica
si existe rectificación posterior.

### CI-09-028. Rectificar un informe emitido

Proveedor: Información contable. Consumidor: Gestión administrativa.

Entrada: informe original, causa, registros corregidos o cambio de presentación aprobado, alcance y aprobación.

Actuación: identifica informes afectados, recalcula, compara versiones y emite rectificación.

Salida: nueva emisión, diferencias, fundamento, vínculo con la anterior y destinatarios a notificar.

Reglas y aceptación: una cifra exige respaldo contable o cambio de presentación aprobado. La versión anterior permanece.
Se comunica a Información regulatoria, que decide y ejecuta su propio procedimiento de rectificación.

## 8. Contratos de eventos

### CI-09-029. Comunicar un cambio de plan o regla contable

Proveedor: Registración contable. Consumidores: Cierres contables, Información contable y Gestión administrativa.

Contenido: cuenta o regla, versión, vigencia, alcance, transición y aprobación.

Reglas: el evento no recontabiliza hechos históricos. Una baja de cuenta no elimina sus saldos ni registros.

### CI-09-030. Comunicar el tratamiento de un hecho o la confirmación de un asiento

Proveedor: Registración contable. Consumidores: servicio emisor, Cierres contables, Información contable y Gestión
administrativa.

Contenido: hecho, consecuencia, tratamiento, regla, asiento, fechas, período, correcciones e impedimentos.

Reglas: distingue recepción, integración pendiente y asiento confirmado. No genera otra contabilización ni ordena
ejecutar nuevamente la operación bancaria.

### CI-09-031. Comunicar un cambio de período, cierre o reapertura

Proveedor: Cierres contables. Consumidores: Registración contable, Información contable, Información regulatoria y
Gestión administrativa.

Contenido: período, fecha, versión, estado, corte, alcance autorizado, cierre o reapertura y controles afectados.

Reglas: un evento anterior de apertura no habilita registrar después de un cierre posterior. Los consumidores comprueban
la admisión al confirmar su actuación.

### CI-09-032. Comunicar una diferencia de conciliación y su tratamiento

Proveedor: Cierres contables. Consumidores: servicio responsable del auxiliar, Registración contable, Información
contable y Gestión administrativa.

Contenido: diferencia, corte, conceptos comparados, importe, moneda, causa, responsable, actuación y comprobación
posterior.

Reglas: detectar una diferencia no autoriza un ajuste. Un cierre de seguimiento identifica el tratamiento de sus
consecuencias; no convierte lo desconocido en conciliado.

### CI-09-033. Comunicar la emisión o rectificación de un informe

Proveedor: Información contable. Consumidores: Cierres contables, Información regulatoria, Documentos y comprobantes,
Comunicaciones y Gestión administrativa.

Contenido: informe, versión, corte, estructura, condición, aprobación, referencia documental y emisión anterior cuando
exista.

Reglas: distingue emisión inicial y rectificación. No presenta declaraciones regulatorias. Una repetición no crea otra
emisión económica ni reemplaza documentos anteriores.

## 9. Integraciones consumidas

### 9.1. Institución y calendario

| Contrato | Uso |
| --- | --- |
| CI-01-004 | Comprobar facultades institucionales de preparación y aprobación. |
| CI-01-014 | Consultar calendario y convenciones. |
| CI-01-015 | Determinar fechas operativas relacionadas con cierres. |

La fecha económica de los hechos se conserva aunque el procesamiento ocurra en otra jornada.

### 9.2. Fuentes operativas de los módulos anteriores

| Fuente | Información utilizada |
| --- | --- |
| Cuentas y Fondos | Saldos, movimientos, conceptos de cuentas, efectivo y correcciones. |
| Transferencias y Pagos | Ejecución, acreditación, aplicación, devoluciones y obligaciones externas. |
| Mandatos y recurrencias | Antecedentes de autorización y ocurrencia cuando la regla los requiera. |
| Cheques y ECHEQ | Pago, rechazo, cesión y destino del cobro de instrumentos. |
| Préstamos y líneas | Capital, desembolsos, intereses, pagos, descuentos, previsiones y ajustes. |
| Garantías y Cobranzas | Afectaciones, ejecuciones y recuperaciones comprobadas. |
| Cuenta de tarjeta y Procesamiento de consumos | Deuda, pagos, financiación, devoluciones y obligaciones de red. |
| Depósitos a plazo | Capital, actualización, intereses, pagos y renovaciones. |
| Órdenes de inversión y Tenencias y valuación | Liquidaciones, custodia, derechos y movimientos de posiciones. |

Se utilizan, entre otros, los contratos ya definidos:

| Contratos | Uso |
| --- | --- |
| CI-04-012, 022, 030, 034 | Auxiliar monetario, recuperación de actuaciones, efectivo y movimientos confirmados. |
| CI-05-006, 012, 029, 036, 038, 039, 042, 044 | Operaciones de pagos, instrumentos y compensación. |
| CI-06-010, 018, 032, 039, 040, 041, 042, 043 | Garantías, deuda, actuaciones, previsiones y recuperaciones. |
| CI-07-021, 024, 034, 036, 041, 044, 045, 046, 048 | Presentaciones, deuda, disputas y obligaciones de red. |
| CI-08-009, 027, 030, 041, 043, 045, 046, 047 | Depósitos, inversiones, posiciones, derechos y traslados. |

Los contratos de consulta justifican datos y permiten recuperar antecedentes. Los eventos aportan hechos confirmados.
Ninguna de esas vías autoriza duplicar una consecuencia económica.

Las confirmaciones de integridad y los auxiliares se reciben mediante CI-09-015 y CI-09-016, manteniendo cada emisor la
autoridad sobre su información.

### 9.3. Otros módulos

| Proveedor | Integración requerida |
| --- | --- |
| Cambio de moneda | Cotizaciones identificadas para finalidad contable y cierre. |
| Tesorería | Disponibilidades propias, operaciones, obligaciones y liquidaciones. |
| Riesgos | Políticas y clasificación utilizadas por los responsables de previsiones. |
| Criterios tributarios | Determinaciones tributarias y posiciones relacionadas. |
| Información regulatoria | Consumo de información contable y tratamiento de declaraciones. |
| Gestión administrativa | Tareas humanas y aprobaciones separadas. |
| Documentos y comprobantes | Evidencias, informes y conservación de versiones. |
| Comunicaciones | Entrega de informes y rectificaciones. |

Los identificadores de sus contratos se establecen en los documentos correspondientes.

## 10. Correspondencia con las operaciones funcionales

| Operación | Contratos principales |
| --- | --- |
| OF-09-01. Administrar el plan de cuentas | CI-09-001, 002, 029 |
| OF-09-02. Definir y aprobar reglas de contabilización | CI-09-003, 004, 029 |
| OF-09-03. Recibir y validar hechos económicos | CI-09-005, 006, 030 |
| OF-09-04. Integrar antecedentes de una operación compartida | CI-09-005, 007, 006 |
| OF-09-05. Registrar asientos automáticos | CI-09-008, 012, 030 |
| OF-09-06. Registrar ajustes manuales | CI-09-009, 012, 030 |
| OF-09-07. Revertir y reclasificar registraciones | CI-09-010, 012, 030 |
| OF-09-08. Administrar contabilización en distintas monedas | CI-09-005, 008, 011, 012 |
| OF-09-09. Registrar devengamientos y previsiones | CI-09-005, 007, 008, 030 |
| OF-09-10. Registrar posiciones de orden y custodia | CI-09-005, 008, 017 |
| OF-09-11. Abrir períodos y administrar cortes | CI-09-013, 014, 031 |
| OF-09-12. Conciliar auxiliares con contabilidad | CI-09-015, 016, 017 |
| OF-09-13. Resolver diferencias contables | CI-09-018, 022, 032 |
| OF-09-14. Ejecutar el cierre diario | CI-09-015, 017, 019, 022, 031 |
| OF-09-15. Ejecutar cierres mensuales y de ejercicio | CI-09-020, 022, 031 |
| OF-09-16. Tratar hechos tardíos y reaperturas | CI-09-021, 014, 031 |
| OF-09-17. Administrar estructuras de información contable | CI-09-023, 024 |
| OF-09-18. Elaborar y emitir informes | CI-09-025, 027, 033 |
| OF-09-19. Consultar saldos y reconstruir registraciones | CI-09-026, 012, 027 |
| OF-09-20. Rectificar informes emitidos | CI-09-028, 027, 033 |
