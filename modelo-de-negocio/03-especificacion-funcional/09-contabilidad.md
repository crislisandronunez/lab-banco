# Operaciones funcionales — Módulo 9. Contabilidad

## 1. Propósito y alcance

Este documento define las operaciones para representar contablemente los hechos económicos del banco, controlar su
integridad y elaborar información financiera.

Comprende los microservicios Registración contable, Cierres contables e Información contable.

La contabilidad mantiene activos, pasivos, patrimonio, ingresos, gastos y registros de orden. No sustituye los saldos
operativos, las deudas contractuales, las tenencias de clientes ni las disponibilidades administradas por sus
respectivos módulos.

Las estructuras y decisiones contables aquí definidas corresponden al banco simulado. La elaboración de declaraciones
regulatorias pertenece a Información regulatoria.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-09-01 | Administrar el plan de cuentas | Registración contable |
| OF-09-02 | Definir y aprobar reglas de contabilización | Registración contable |
| OF-09-03 | Recibir y validar hechos económicos | Registración contable |
| OF-09-04 | Integrar antecedentes de una operación compartida | Registración contable |
| OF-09-05 | Registrar asientos automáticos | Registración contable |
| OF-09-06 | Registrar ajustes manuales | Registración contable |
| OF-09-07 | Revertir y reclasificar registraciones | Registración contable |
| OF-09-08 | Administrar contabilización en distintas monedas | Registración contable |
| OF-09-09 | Registrar devengamientos y previsiones | Registración contable |
| OF-09-10 | Registrar posiciones de orden y custodia | Registración contable |
| OF-09-11 | Abrir períodos y administrar cortes | Cierres contables |
| OF-09-12 | Conciliar auxiliares con contabilidad | Cierres contables |
| OF-09-13 | Resolver diferencias contables | Cierres contables |
| OF-09-14 | Ejecutar el cierre diario | Cierres contables |
| OF-09-15 | Ejecutar cierres mensuales y de ejercicio | Cierres contables |
| OF-09-16 | Tratar hechos tardíos y reaperturas | Cierres contables |
| OF-09-17 | Administrar estructuras de información contable | Información contable |
| OF-09-18 | Elaborar y emitir informes | Información contable |
| OF-09-19 | Consultar saldos y reconstruir registraciones | Información contable |
| OF-09-20 | Rectificar informes emitidos | Información contable |

## 3. Reglas comunes del módulo

### 3.1. Partida doble y naturaleza económica

Todo asiento confirmado mantiene igualdad entre débitos y créditos.

La registración distingue la naturaleza del hecho:

- Un depósito de cliente genera un pasivo del banco.

- Un préstamo desembolsado genera un activo por cobrar.

- Los intereses ganados y pagados tienen tratamientos diferentes.

- El movimiento entre cuentas de clientes no constituye por sí mismo un ingreso o gasto.

- Los activos pertenecientes a clientes no se incorporan como inversiones propias del banco.

La cuenta utilizada debe corresponder al significado económico del hecho.

### 3.2. Identificación y fuentes

Cada hecho contabilizable identifica:

- Operación y ocurrencia económica.

- Servicio emisor responsable.

- Tipo de hecho y versión.

- Participantes o posiciones pertinentes.

- Importes, monedas y conceptos.

- Fecha económica y fecha de recepción.

- Evidencias y referencias operativas.

- Correcciones anteriores relacionadas.

La identificación del hecho es independiente de la cantidad de veces que se comunique.

### 3.3. Operaciones compartidas

Una operación puede producir antecedentes de varios servicios.

La regla contable determina:

- Qué hecho origina cada consecuencia.

- Qué servicio aporta cada antecedente.

- Qué información debe coincidir.

- Qué asientos deben producirse.

- Qué elementos deben estar completos antes de confirmar.

Una comunicación de coordinación y otra de ejecución no representan necesariamente dos hechos contables.

Por ejemplo, el desembolso informado por Préstamos y líneas y la acreditación informada por Fondos pueden integrar una
misma registración: no originan dos préstamos ni dos depósitos.

### 3.4. Monedas y precisión

El peso argentino es la moneda de presentación contable de la simulación.

Cada partida conserva su moneda e importe originales y su equivalente en pesos.

Los importes originales provienen del responsable económico. Contabilidad no modifica cuotas, intereses o cargos para
hacer coincidir el asiento.

Las conversiones utilizan una cotización identificada por finalidad, fecha, fuente y versión.

Los importes contables se redondean a centavos, al más cercano, elevando empates. Las diferencias exclusivamente
producidas por conversión se registran en cuentas específicas de redondeo; no modifican obligaciones de clientes.

### 3.5. Fechas y períodos

Se conservan:

- Fecha económica del hecho.

- Fecha de recepción.

- Fecha de contabilización.

- Período de imputación.

El día económico institucional transcurre de 00:00 a 24:00 en America/Argentina/Buenos_Aires.

El corte de compensación de un mecanismo no reemplaza el corte contable: una operación posterior a las 18:00 puede
pertenecer al mismo día económico y a otro ciclo de compensación.

### 3.6. Confirmación e inmutabilidad

Un asiento se confirma completo. Sus partidas no quedan parcialmente disponibles como una registración terminada.

Los asientos confirmados no se borran ni sobrescriben.

Las correcciones se realizan mediante reversión, ajuste o reclasificación, conservando sus referencias.

### 3.7. Intervenciones humanas

El plan de cuentas, las reglas, los asientos manuales, las resoluciones de diferencias y las reaperturas requieren
preparación y aprobación por personas diferentes.

La aplicación automática de una regla aprobada no requiere aprobar individualmente cada asiento.

Las facultades corresponden a Institución. Gestión administrativa coordina las tareas humanas.

### 3.8. Integridad y pendientes

Un hecho incompleto queda pendiente con causa y responsable.

No se utiliza una cuenta genérica para ocultar información faltante o equilibrar una operación desconocida.

Las cuentas transitorias solo se utilizan cuando una regla aprobada representa una situación económica real, como fondos
recibidos pendientes de aplicación. Deben tener origen, contrapartida, responsable y procedimiento de resolución.

---

## 4. OF-09-01. Administrar el plan de cuentas

### Propósito y responsable

Definir dónde se registran los hechos económicos.

Responsable: Registración contable.

### Información y condiciones previas

Cada cuenta identifica código estable, denominación, naturaleza, clasificación, monedas, dimensiones y vigencia.

### Desarrollo

- Registrar propuesta.

- Comprobar unicidad y jerarquía.

- Determinar reglas de uso.

- Revisar relaciones con informes y productos.

- Obtener aprobación.

- Habilitar la versión.

- Conservar antecedentes.

### Reglas y decisiones

- Se distinguen cuentas de agrupación y cuentas que admiten partidas.

- Las cuentas de agrupación no reciben movimientos directos.

- Una cuenta con historia no cambia de naturaleza para reutilizar su código.

- La baja impide nuevos usos ordinarios, pero conserva consultas y correcciones autorizadas.

- Una cuenta con saldo no se retira sin tratamiento expreso.

- Las dimensiones permiten identificar producto, moneda y origen sin crear una cuenta distinta por cada cliente.

- Las cuentas de orden se distinguen de las patrimoniales.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se intenta registrar en una cuenta de agrupación | Se impide. |
| Una cuenta de activo pasa a representar un pasivo | Se requiere otra cuenta y tratamiento de transición. |
| Se da de baja una cuenta con saldo | La baja no concluye sin resolverlo. |
| Se consulta una cuenta retirada | Su historia permanece disponible. |

## 5. OF-09-02. Definir y aprobar reglas de contabilización

### Propósito y responsable

Establecer la correspondencia entre hechos y asientos.

Responsable: Registración contable.

### Información y condiciones previas

Se requiere tipo de hecho, responsable, antecedentes, cuentas, dimensiones, importes, fechas y tratamiento de
correcciones.

### Desarrollo

- Identificar el hecho económico.

- Asignar fuentes de información.

- Definir débitos y créditos.

- Comprobar equilibrio.

- Definir excepciones.

- Verificar ejemplos y correcciones.

- Obtener aprobación.

- Establecer vigencia.

### Reglas y decisiones

- Cada consecuencia tiene un emisor o integración de fuentes inequívocos.

- No se contabiliza dos veces por recibir información de varios participantes.

- Una versión nueva no reinterpreta automáticamente hechos históricos.

- Los cambios incluyen su tratamiento de transición.

- Las reglas deben distinguir principal, rendimiento, comisión, tributo y diferencia.

- Las cuentas transitorias requieren un significado económico específico.

- Una regla no autoriza operaciones de negocio.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Desembolso en cuenta de cliente por 500 | Débito a préstamos por cobrar y crédito a depósitos por 500, sin duplicación. |
| Transferencia interna de 30 | Reclasificación entre pasivos de clientes, sin ingreso ficticio. |
| Falta una contrapartida | No se aprueba la regla. |
| Se modifica una regla | Se conserva cuál se aplicó a cada hecho. |

## 6. OF-09-03. Recibir y validar hechos económicos

### Propósito y responsable

Aceptar antecedentes completos y detectar repeticiones o inconsistencias.

Responsable: Registración contable.

### Desarrollo

- Identificar emisor y referencia.

- Comprobar repetición.

- Validar contenido obligatorio.

- Localizar regla aplicable.

- Comprobar cuentas y período.

- Admitir el procesamiento o registrar impedimentos.

### Reglas y decisiones

- La misma referencia y contenido recuperan el resultado existente.

- La misma referencia con otro contenido genera inconsistencia.

- Un tipo de hecho desconocido no se contabiliza mediante una regla parecida.

- Los importes originales deben coincidir con la evidencia operativa.

- El rechazo contable no revierte automáticamente el negocio.

- Los pendientes se comunican al responsable del hecho.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se recibe tres veces el mismo hecho | Una sola contabilización. |
| Cambia el importe con la misma referencia | Investigación, sin segundo asiento automático. |
| Falta moneda | Hecho pendiente. |
| No existe regla | No se utiliza una cuenta arbitraria. |

## 7. OF-09-04. Integrar antecedentes de una operación compartida

### Propósito y responsable

Reunir fuentes que justifican una registración conjunta.

Responsable: Registración contable.

### Desarrollo

- Identificar operación y ocurrencia.

- Obtener antecedentes exigidos por la regla.

- Comparar referencias, fechas e importes.

- Distinguir confirmaciones de hechos nuevos.

- Completar la representación.

- Registrar o mantener pendiente.

### Reglas y decisiones

- La integración no convierte comunicaciones repetidas en nuevos hechos.

- Cada fuente conserva su autoridad.

- Una diferencia entre desembolso y acreditación no se compensa inventando un cargo.

- Los hechos independientes de una operación pueden registrarse por separado si la regla lo contempla.

- Una operación pendiente de integración conserva seguimiento hasta resolverse.

- La contabilidad no declara fallida una operación únicamente por falta de una comunicación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Préstamos informa entrega 500 y Fondos acredita 500 | Se integran conforme a la regla. |
| Una fuente informa 500 y otra 490 sin cargos identificados | No se presume que los 10 son comisión. |
| Llega tarde la confirmación faltante | Se completa el mismo hecho, sin duplicarlo. |

## 8. OF-09-05. Registrar asientos automáticos

### Propósito y responsable

Confirmar partidas equilibradas mediante reglas aprobadas.

Responsable: Registración contable.

### Desarrollo

- Obtener hecho validado.

- Aplicar versión de regla.

- Determinar partidas y dimensiones.

- Comprobar equilibrio y período.

- Confirmar el asiento completo.

- Actualizar saldos contables.

- Comunicar el resultado.

### Reglas y decisiones

- Los importes son positivos y su sentido se expresa mediante débito o crédito.

- Un importe cero no genera partidas sin efecto.

- El asiento conserva hecho, regla y documentos.

- La confirmación y la actualización de saldos no se presentan como actuaciones independientes concluidas.

- Una falla de respuesta se resuelve consultando la referencia original.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Depósito de efectivo confirmado por 100 | Débito a efectivo y crédito a depósitos por 100. |
| Débitos 100 y créditos 90 | No se confirma. |
| Respuesta perdida después de confirmar | Se recupera el mismo asiento. |
| Hecho sin importe económico | No se genera movimiento ficticio. |

## 9. OF-09-06. Registrar ajustes manuales

### Propósito y responsable

Registrar actuaciones justificadas que no correspondan a la aplicación automática ordinaria.

Responsable: Registración contable.

### Información y desarrollo

Se requiere motivo, partidas, fecha, fundamento, evidencia y referencias.

- Preparar ajuste.

- Comprobar cuentas y período.

- Revisar su relación con operaciones.

- Obtener aprobación separada.

- Confirmar.

- Informar a los responsables afectados.

### Reglas y decisiones

- Un ajuste manual no modifica saldos operativos.

- Si el error está en el producto, su responsable corrige el origen.

- No se crean ingresos o gastos para cerrar una diferencia sin explicación.

- El aprobador debe tener facultades sobre el alcance.

- Una propuesta modificada requiere nueva aprobación.

- Las correcciones de ejercicios anteriores siguen el procedimiento de períodos y reaperturas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| El preparador intenta aprobar su ajuste | Se impide. |
| Se corrige una clasificación contable | No se debita al cliente. |
| No existe evidencia suficiente | Permanece pendiente. |
| Cambian las partidas aprobadas | Se requiere nueva revisión. |

## 10. OF-09-07. Revertir y reclasificar registraciones

### Propósito y responsable

Corregir asientos conservando su historia.

Responsable: Registración contable.

### Desarrollo

- Identificar asiento original.

- Revisar correcciones anteriores.

- Determinar reversión total, parcial o reclasificación.

- Comprobar fundamento y período.

- Obtener aprobación cuando sea manual.

- Registrar partidas vinculadas.

- Conciliar el resultado.

### Reglas y decisiones

- La reversión invierte las partidas correspondientes.

- Una reversión parcial no excede el importe todavía reversible.

- La reclasificación traslada entre cuentas sin inventar un movimiento económico.

- Las correcciones no modifican documentos originales.

- Un error contable y un error monetario reciben actuaciones distintas y relacionadas.

- Las diferencias de cotización posteriores no se confunden con errores originales.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Asiento erróneo de 100 totalmente revertido | Efecto neto cero, con ambos registros conservados. |
| Ya se revirtieron 40 de 100 | Como máximo quedan 60 por revertir de ese efecto. |
| Reclasificación entre dos activos | No aparece un ingreso por la transferencia. |

## 11. OF-09-08. Administrar contabilización en distintas monedas

### Propósito y responsable

Representar hechos multimoneda y sus diferencias.

Responsable: Registración contable.

### Desarrollo

- Conservar importes originales.

- Obtener cotización de finalidad contable.

- Calcular equivalente en pesos.

- Registrar contrapartidas.

- Separar diferencias de cambio y redondeo.

- Conservar referencias.

### Reglas y decisiones

- La conversión contable no compra ni vende moneda.

- La cotización contable se distingue de la comercial ofrecida al cliente.

- Una operación cambiaria identifica ambas entregas.

- Los elementos monetarios en moneda extranjera se reexpresan al cierre con la cotización de cierre.

- La reexpresión no cambia la cantidad de moneda adeudada al cliente.

- Las partidas no monetarias utilizan el criterio de valuación asignado a su clase.

- Los faltantes de cotización impiden una valuación definitiva.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Pasivo de USD 100; cotización pasa de 1.000 a 1.100 | Pasivo equivalente pasa de 100.000 a 110.000 pesos; los USD 100 no cambian. |
| Se realiza esa reexpresión | Se reconoce la diferencia correspondiente, sin movimiento en la cuenta del cliente. |
| Falta cotización de cierre | No se inventa un valor ni se declara completo el control. |

## 12. OF-09-09. Registrar devengamientos y previsiones

### Propósito y responsable

Contabilizar importes calculados por sus responsables económicos.

Responsable: Registración contable.

### Desarrollo

- Recibir cálculo y período.

- Comprobar versión y referencias.

- Identificar importe nuevo o diferencia.

- Aplicar regla.

- Confirmar asiento.

- Conciliar con el auxiliar.

### Reglas y decisiones

- Los productos calculan sus intereses y cargos.

- Riesgos establece criterios de previsión y el producto calcula sus importes.

- Un acumulado no se registra otra vez íntegramente cuando corresponde contabilizar su variación.

- La cobranza de un interés ya devengado cancela el derecho; no reconoce otra vez el mismo ingreso.

- El pago de interés de depósito cancela la obligación; no duplica el gasto.

- La previsión no reduce la deuda contractual del cliente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Interés a cobrar devengado 10 | Se reconoce derecho e ingreso por 10. |
| Luego se cobran esos 10 | Se cancela el derecho, sin otro ingreso de 10. |
| Previsión requerida pasa de 20 a 25 | Se registra aumento 5, no otros 25 adicionales. |
| Se repite el período | No se duplica el efecto. |

## 13. OF-09-10. Registrar posiciones de orden y custodia

### Propósito y responsable

Representar compromisos, garantías y activos administrados por cuenta de terceros.

Responsable: Registración contable.

### Desarrollo

- Identificar naturaleza.

- Obtener cantidades e importes del auxiliar.

- Aplicar cuentas de orden.

- Registrar altas, cambios y bajas.

- Conciliar.

### Reglas y decisiones

- Los valores de clientes no se incluyen como inversiones propias.

- Los compromisos no desembolsados se distinguen de préstamos por cobrar.

- La cobertura de garantía no constituye efectivo del banco.

- Una ejecución o adjudicación puede generar un hecho patrimonial nuevo, con su fundamento.

- Los registros de orden se equilibran dentro de su esquema y se presentan separados del patrimonio.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cliente posee bonos valuados en 1.000 | No se incrementan inversiones propias del banco por 1.000. |
| Línea concedida sin uso | No se registra capital prestado por todo el límite. |
| Se cancela una garantía | Se actualiza su registro sin crear un pago ficticio. |

## 14. OF-09-11. Abrir períodos y administrar cortes

### Propósito y responsable

Determinar dónde pueden registrarse hechos.

Responsable: Cierres contables.

### Desarrollo

- Crear ejercicio y períodos mensuales.

- Abrir fechas diarias.

- Establecer cortes.

- Comunicar habilitación.

- Controlar cambios y cierres.

### Reglas y decisiones

- Existe un ejercicio calendario del 1 de enero al 31 de diciembre.

- Cada fecha pertenece a un único período mensual.

- Un período abierto admite registraciones ordinarias.

- Un período en cierre admite únicamente actuaciones identificadas de regularización.

- Un período cerrado impide registraciones ordinarias.

- Las operaciones bancarias pueden continuar mientras se cierra una fecha anterior.

- Las fechas futuras no reciben hechos reales anticipados por conveniencia administrativa.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Operación del día posterior durante cierre anterior | Se imputa a su fecha, no se incorpora al cierre previo. |
| Se intenta registrar ordinariamente en mes cerrado | Se deriva al procedimiento de hecho tardío. |
| Períodos superpuestos | No se habilitan. |

## 15. OF-09-12. Conciliar auxiliares con contabilidad

### Propósito y responsable

Comprobar correspondencia entre posiciones operativas y contables.

Responsable: Cierres contables.

### Desarrollo

- Fijar corte.

- Obtener auxiliares.

- Obtener saldos y movimientos contables.

- Homogeneizar alcance y moneda de comparación.

- Comparar.

- Identificar diferencias y responsables.

- Obtener resolución.

### Reglas y decisiones

Se concilian, al menos:

- Depósitos a la vista y a plazo.

- Capital, intereses y previsiones de financiación.

- Deuda de tarjeta.

- Disponibilidades propias y liquidaciones.

- Operaciones de cambio.

- Tenencias y registros de custodia.

- Tributos y cuentas transitorias.

Los saldos se comparan con conceptos equivalentes. El saldo disponible del cliente no se compara como si fuera el pasivo
contable total.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo contable de cuenta 100 y reserva 30 | El pasivo no se reduce a 70 por la reserva. |
| Capital operativo 500 y activo contable 490 sin explicación | Diferencia de 10 bajo investigación. |
| Los cortes son distintos | Se ajusta el alcance antes de concluir faltante. |

## 16. OF-09-13. Resolver diferencias contables

### Propósito y responsable

Determinar causa y tratamiento de discrepancias.

Responsable: Cierres contables.

### Desarrollo

- Clasificar diferencia.

- Solicitar evidencia.

- Determinar origen operativo, contable o temporal.

- Definir actuación.

- Obtener aprobación si corresponde.

- Ejecutar mediante el responsable.

- Repetir la comprobación afectada.

### Reglas y decisiones

- Las diferencias aritméticas y duplicaciones confirmadas deben corregirse.

- Una diferencia temporal requiere referencias y expectativa comprobable de resolución.

- El banco no establece un importe mínimo para ignorar diferencias.

- Los redondeos legítimos se registran bajo su regla.

- Las partidas pendientes tienen responsable y fecha objetivo.

- La aprobación de seguimiento no transforma una diferencia desconocida en un gasto válido.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Falta un hecho confirmado | Se recupera el hecho original. |
| Se duplicó un asiento | Se revierte el duplicado identificado. |
| Solo difiere el momento de confirmación externa | Se documenta el desfase y continúa seguimiento. |
| No se conoce la causa | No se fuerza la conciliación. |

## 17. OF-09-14. Ejecutar el cierre diario

### Propósito y responsable

Confirmar la integridad contable de una fecha.

Responsable: Cierres contables.

### Desarrollo

- Fijar corte del día.

- Solicitar confirmaciones de integridad a los emisores.

- Comprobar hechos recibidos y pendientes.

- Verificar equilibrio.

- Conciliar auxiliares.

- Revisar cuentas transitorias.

- Registrar ajustes necesarios.

- Obtener aprobación.

- Confirmar cierre.

### Reglas y decisiones

- El cierre se inicia el siguiente día operativo, sin cambiar la fecha económica de los hechos.

- Cada emisor confirma que entregó los hechos del corte o identifica faltantes.

- No basta con que no hayan llegado mensajes para declarar completitud.

- No se cierra con asientos desequilibrados, duplicaciones confirmadas o hechos económicos conocidos sin tratamiento.

- Las obligaciones externas inciertas pueden continuar si su situación está correctamente representada y conciliada como
  pendiente.

- Una ejecución interrumpida se retoma sin repetir ajustes.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Un emisor no confirma integridad | El control permanece pendiente. |
| Transferencia incierta correctamente representada | Mantiene seguimiento sin fingir rechazo o acreditación. |
| Se repite el cierre confirmado | Se recupera el mismo resultado. |
| Se interrumpe después de un ajuste | No se vuelve a registrar ese ajuste. |

## 18. OF-09-15. Ejecutar cierres mensuales y de ejercicio

### Propósito y responsable

Determinar saldos y resultados de períodos completos.

Responsable: Cierres contables.

### Desarrollo

- Comprobar cierres diarios.

- Recibir devengamientos, previsiones y valuaciones.

- Conciliar acumulados.

- Determinar ajustes contables.

- Verificar cuentas transitorias.

- Obtener aprobación.

- Confirmar período.

- En ejercicio, transferir resultados y abrir saldos iniciales.

### Reglas y decisiones

- Los productos calculan sus obligaciones y rendimientos.

- Contabilidad determina reclasificaciones y ajustes de presentación.

- La apertura del ejercicio siguiente no duplica saldos.

- Los ingresos y gastos del ejercicio se transfieren al resultado acumulado mediante un asiento identificado.

- Los activos, pasivos y patrimonio mantienen continuidad.

- Las distribuciones de resultados requieren una decisión institucional distinta.

- No se considera distribuida una ganancia por cerrar el ejercicio.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Ingresos 100 y gastos 70 | Resultado del ejercicio 30, sujeto a los demás conceptos registrados. |
| Se abre el ejercicio siguiente | Los saldos patrimoniales continúan una sola vez. |
| Falta una previsión exigida | No se informa cierre mensual completo. |
| Se transfiere resultado | No se crea un ingreso operativo adicional. |

## 19. OF-09-16. Tratar hechos tardíos y reaperturas

### Propósito y responsable

Registrar información recibida después de un cierre sin alterar silenciosamente períodos anteriores.

Responsable: Cierres contables.

### Desarrollo

- Identificar fecha económica y período afectado.

- Determinar si existe asiento previo.

- Evaluar tratamiento.

- Solicitar corrección en período abierto o reapertura.

- Obtener aprobación.

- Registrar.

- Reejecutar controles e identificar informes afectados.

### Reglas adoptadas

- Si la fecha diaria cerró, pero su mes sigue abierto, se reabre la fecha afectada con aprobación y se vuelve a cerrar.

- Si el mes cerró, pero el ejercicio no fue emitido como definitivo, se reabre el período afectado cuando el hecho
  modifica sus cifras.

- Si el ejercicio ya fue emitido, se registra un ajuste identificado de períodos anteriores y se determina la
  rectificación de informes.

- Los documentos emitidos no se sustituyen silenciosamente.

- La reapertura identifica alcance y motivo.

- Una registración tardía conserva su fecha de recepción real.

- Los períodos posteriores afectados se revisan.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Hecho del lunes recibido el miércoles, mes abierto | Se trata la fecha económica mediante reapertura controlada. |
| Hecho modifica un ejercicio emitido | Se registra ajuste y se evalúa rectificación, conservando el informe anterior. |
| La reapertura no fue aprobada | No se registra en el período cerrado. |

## 20. OF-09-17. Administrar estructuras de información contable

### Propósito y responsable

Definir cómo se presentan los registros.

Responsable: Información contable.

### Desarrollo

- Registrar estructura.

- Asignar cuentas y dimensiones.

- Definir fórmulas de agregación.

- Comprobar cobertura.

- Revisar comparaciones.

- Obtener aprobación.

- Establecer vigencia.

### Reglas y decisiones

- Una cuenta no se incluye dos veces en el mismo total salvo una presentación explícita que no duplique el resultado.

- Las cuentas sin correspondencia impiden emitir un informe completo.

- Los cambios conservan versiones.

- Las reclasificaciones de presentación no modifican asientos.

- Los registros de orden se separan de los patrimoniales.

- Los criterios comparativos quedan identificados.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Una cuenta nueva no tiene ubicación | Se resuelve antes de emitir el informe completo. |
| Cambia una agrupación | Se conserva el criterio anterior. |
| Una fórmula duplica un saldo | No se aprueba. |

## 21. OF-09-18. Elaborar y emitir informes

### Propósito y responsable

Producir mayores, balances y estados derivados de la contabilidad.

Responsable: Información contable.

### Desarrollo

- Identificar período y corte.

- Seleccionar estructura vigente.

- Reunir registros.

- Calcular agrupaciones.

- Comprobar totales.

- Identificar condición del período.

- Obtener aprobación de emisión cuando corresponda.

- Conservar y entregar.

### Reglas y decisiones

- Los informes identifican moneda, corte, versión y condición de cierre.

- Un informe de período abierto se presenta como información provisional.

- Los estados definitivos utilizan períodos cerrados.

- Se verifica la igualdad entre activos y pasivos más patrimonio.

- Los resultados se corresponden con ingresos y gastos registrados.

- Información regulatoria recibe los datos sin que este servicio presente declaraciones por su cuenta.

- La falta de datos no se oculta mediante ceros.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Activos 150, pasivos 100 y patrimonio 50 | Igualdad patrimonial comprobada. |
| Período abierto | Informe identificado como provisional. |
| Falta una fuente necesaria | No se emite como completo. |
| Informe repetido con mismo corte y versión | Conserva resultados reproducibles. |

## 22. OF-09-19. Consultar saldos y reconstruir registraciones

### Propósito y responsable

Permitir justificar saldos, movimientos y cifras informadas.

Responsable: Información contable.

### Desarrollo

- Comprobar acceso.

- Identificar cuenta, período o informe.

- Obtener saldos iniciales y partidas.

- Relacionar hechos y correcciones.

- Presentar recorrido de justificación.

### Reglas y decisiones

- Una cifra puede rastrearse hasta sus partidas y hechos.

- Las consultas históricas utilizan versiones y cortes correspondientes.

- Los datos de clientes se exponen solo dentro de las facultades.

- La consulta no modifica registros.

- Los saldos se distinguen por moneda y dimensiones.

- Las referencias documentales permanecen accesibles conforme a su conservación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo inicial 100, débitos 30 y créditos 20 en una cuenta de activo | Saldo final 110, conforme a su naturaleza. |
| Existe reversión | Se muestran original y reversión. |
| Se consulta una cifra de balance | Se identifican las cuentas y partidas que la componen. |

## 23. OF-09-20. Rectificar informes emitidos

### Propósito y responsable

Corregir información presentada conservando sus antecedentes.

Responsable: Información contable.

### Desarrollo

- Recibir causa y registros corregidos.

- Identificar informes afectados.

- Recalcular con el corte pertinente.

- Comparar versiones.

- Obtener aprobación.

- Emitir rectificación.

- Comunicar a los destinatarios y a Información regulatoria.

### Reglas y decisiones

- La rectificación identifica el informe anterior.

- No se modifica una cifra sin respaldo contable o cambio de presentación aprobado.

- Se distingue error de datos de cambio metodológico.

- Los destinatarios reciben la condición rectificativa.

- Los informes regulatorios requieren su propio procedimiento de rectificación.

- La emisión anterior permanece conservada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se corrige un asiento que afectaba resultados | Se recalculan los informes alcanzados. |
| Solo cambia una agrupación | Se identifica como cambio de presentación. |
| Se emite otra versión | La anterior no desaparece. |
| Existía declaración regulatoria | Se comunica a su responsable para su tratamiento. |

## 24. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Hecho económico y cálculo operativo | Módulo que administra la operación |
| Plan de cuentas y reglas | Registración contable |
| Asientos y correcciones registradas | Registración contable |
| Cotizaciones de referencia | Cambio de moneda |
| Períodos, conciliaciones y cierres | Cierres contables |
| Políticas de riesgo y previsión | Riesgos |
| Cálculo de previsiones de cartera | Módulo responsable de la cartera |
| Estructuras e informes financieros | Información contable |
| Presentaciones regulatorias | Información regulatoria |
| Facultades de aprobación | Institución |
| Tareas humanas y evidencias | Gestión administrativa y Documentos y comprobantes |

La contabilidad conserva la relación entre hechos, partidas, saldos, cierres e informes. Las diferencias se investigan y
corrigen con fundamento, sin duplicar consecuencias económicas ni utilizar ajustes para ocultar obligaciones.
