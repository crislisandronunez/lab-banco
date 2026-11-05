# Reglas comunes de los módulos y microservicios

Estas reglas se aplican a los doce módulos del banco y a sus microservicios. Establecen criterios comunes de
trazabilidad contable, recuperación de operaciones y autorización.

Cada servicio conserva la autoridad sobre la información y las decisiones asignadas en su definición. La participación
en un proceso compartido no transfiere esa responsabilidad a otro servicio.

## 1. Trazabilidad contable

### Propósito

Garantizar que cada consecuencia económica tenga un origen identificable, una representación contable completa y un
tratamiento que impida registraciones duplicadas.

### Responsabilidades

El servicio responsable de la operación:

- Identifica la operación y su finalidad.

- Conserva participantes, condiciones aplicadas, importes, monedas y fechas.

- Determina los hechos económicos que corresponden a su ámbito.

- Relaciona las actuaciones de los servicios participantes.

- Mantiene el vínculo con sus correcciones, devoluciones y cancelaciones.

Los servicios que ejecutan efectos económicos:

- Identifican cada movimiento, modificación de deuda, afectación de tenencias o liquidación.

- Conservan la referencia a la operación y al hecho que justifican su actuación.

- Informan el resultado comprobado y los conceptos afectados.

- Distinguen sus efectos de los ejecutados por otros participantes.

Registración contable:

- Determina la representación contable de los hechos recibidos.

- Aplica las reglas y versiones correspondientes.

- Conserva la relación entre hechos, asientos y partidas.

- Impide registrar nuevamente un hecho ya contabilizado.

- Mantiene pendientes los hechos que no puedan contabilizarse y comunica su causa.

Cierres contables:

- Comprueba la correspondencia entre los registros operativos y contables.

- Identifica hechos faltantes, duplicaciones y diferencias.

- Coordina su resolución con los responsables.

### Identificación de hechos y efectos

Una operación puede originar varios hechos económicos y varios asientos. Deben distinguirse:

- La operación de negocio.

- Cada hecho económico ocurrido dentro de ella.

- Los efectos ejecutados por los servicios participantes.

- Los asientos que representan esos efectos.

Cada hecho contabilizable tiene un servicio emisor responsable. Los demás participantes conservan referencias al hecho y
aportan información complementaria sin emitirlo nuevamente como si fuera otro.

Cuando un asiento requiere información de varios servicios, la regla de contabilización determina qué antecedentes deben
reunirse antes de confirmarlo.

### Reglas

- La repetición de una comunicación no genera un nuevo hecho económico.

- Una misma consecuencia no se contabiliza dos veces por ser informada por el coordinador y por el ejecutor.

- Los hechos distintos de una operación conservan identificaciones propias.

- Los importes mantienen moneda original, fechas y desglose por concepto.

- Las conversiones conservan cotización, fuente, fecha y criterio de redondeo.

- La fecha económica, la fecha de recepción y la fecha de contabilización se distinguen cuando difieren.

- Los compromisos, reservas, obligaciones reconocidas y pagos efectivos reciben el tratamiento contable que corresponda
  a su naturaleza.

- Una reserva no se presenta como un débito definitivo.

- Los asientos confirmados se corrigen mediante ajustes o reversiones vinculados, conservando los originales.

- Un error de contabilización no elimina automáticamente la operación económica.

- Una operación ejecutada con contabilización pendiente permanece identificada y bajo seguimiento.

- La confirmación operativa y la confirmación contable se distinguen cuando no ocurren al mismo tiempo.

- Las diferencias se resuelven mediante evidencia y decisiones autorizadas, sin alterar registros para ocultarlas.

### Ejemplo de aplicación

En el desembolso de un préstamo:

- Préstamos y líneas identifica el desembolso y reconoce la obligación crediticia correspondiente.

- Fondos registra la acreditación en la cuenta.

- Ambos efectos conservan la referencia al mismo desembolso.

- Registración contable representa el derecho de cobro del banco y la obligación de depósito resultante sin contabilizar
  dos veces el capital.

## 2. Recuperación de operaciones

### Propósito

Resolver operaciones interrumpidas, parciales o inciertas sin duplicar efectos, liberar recursos indebidamente ni perder
obligaciones pendientes.

### Responsable del seguimiento

El servicio que administra la operación conserva la responsabilidad de seguirla hasta obtener un resultado comprobable.

Los servicios participantes mantienen la responsabilidad sobre sus propios efectos y deben permitir comprobar qué
ejecutaron.

| Operación | Responsable del seguimiento |
| --- | --- |
| Vinculación al banco | Vinculación |
| Solicitud de contratación | Solicitudes de productos |
| Celebración o modificación contractual | Contratos |
| Cierre de cuenta | Cuentas |
| Depósito o extracción de efectivo | Operaciones de efectivo |
| Transferencia | Transferencias |
| Pago de una obligación externa | Pagos |
| Ocurrencia programada o débito recurrente | Mandatos y recurrencias, hasta comprobar la instrucción generada |
| Operación con cheque o ECHEQ | Cheques y ECHEQ |
| Desembolso o pago de préstamo | Préstamos y líneas |
| Autorización de tarjeta | Autorizaciones |
| Presentación o devolución de tarjeta | Procesamiento de consumos |
| Imputación de pago de tarjeta | Cuenta de tarjeta |
| Depósito a plazo | Depósitos a plazo |
| Orden de inversión | Órdenes de inversión |
| Evento económico sobre una tenencia | Tenencias y valuación |
| Operación propia del banco | Operaciones de tesorería |
| Operación cambiaria | Cambio de moneda |
| Liquidación externa | Liquidaciones de tesorería |

Cuando una operación utiliza otra, cada responsable conserva su seguimiento. Por ejemplo, Pagos comprueba la aplicación
a la obligación y Transferencias comprueba el traslado de fondos utilizado para ejecutarla.

### Información que debe conservarse

- Identificación de la operación y de sus actuaciones.

- Instrucción original y condiciones aceptadas.

- Servicios y participantes externos involucrados.

- Compromisos, reservas y afectaciones constituidos.

- Efectos confirmados y efectos pendientes.

- Solicitudes enviadas y resultados recibidos.

- Consultas, reintentos y decisiones de recuperación.

- Correcciones y obligaciones residuales.

### Reglas de actuación

- La ausencia de respuesta no demuestra rechazo ni falta de ejecución.

- Antes de repetir una actuación se consulta su resultado utilizando la referencia original.

- Reintentar la misma actuación no puede producir un segundo efecto económico.

- Si una petición repetida contiene condiciones diferentes, se trata como una inconsistencia; no se ejecuta
  silenciosamente como la original.

- Las ejecuciones parciales identifican lo realizado y lo pendiente.

- Los recursos comprometidos se liberan cuando se comprueba que ya no respaldan una ejecución posible o una obligación
  vigente.

- Un vencimiento de espera no autoriza por sí solo a devolver dinero o liberar una reserva.

- La cancelación de una instrucción se distingue de la devolución de una operación ejecutada.

- Las correcciones posteriores se registran como actuaciones vinculadas; no borran los hechos anteriores.

- Un efecto irreversible se trata mediante las acciones contractuales, económicas o administrativas correspondientes.

- La investigación manual identifica responsable, fundamento y resultado.

- Un servicio de apoyo puede investigar o conciliar; el responsable de la operación conserva el seguimiento de su
  resultado.

### Condiciones de resolución

Una operación se considera resuelta cuando:

- Su resultado está comprobado.

- Los efectos realizados concuerdan con ese resultado.

- Las reservas y compromisos tienen tratamiento definido.

- Las correcciones necesarias fueron ejecutadas o dieron origen a obligaciones residuales identificadas.

- Los participantes pertinentes recibieron el resultado.

- Las incidencias contables o de comunicación pendientes tienen un responsable y seguimiento propios.

Trasladar una tarea a otro servicio no equivale a resolver la operación.

### Ejemplo de aplicación

Si una transferencia externa pierde su respuesta:

- Transferencias conserva la instrucción original.

- Compensación y conciliación de pagos consulta y contrasta el resultado externo.

- Fondos mantiene el tratamiento monetario correspondiente mientras exista incertidumbre.

- Transferencias confirma, rechaza o coordina la devolución según la evidencia obtenida.

- No se crea una segunda transferencia para sustituir la investigación de la primera.

## 3. Autorización de actuaciones

### Propósito

Garantizar que cada actuación sea realizada por un participante identificado, con facultades suficientes y bajo las
condiciones contractuales y operativas aplicables.

### Comprobaciones diferenciadas

| Comprobación | Qué determina | Responsable funcional |
| --- | --- | --- |
| Identidad | Quién es el participante | Personas |
| Verificación de identidad | Qué identidad fue comprobada y con qué alcance | Conocimiento del cliente |
| Autenticación del canal | Quién está utilizando el canal y cómo lo acredita | Seguridad de acceso, definida en la arquitectura técnica |
| Relación comercial | Qué vínculo mantiene la persona con el banco | Clientes |
| Representación | Si puede actuar por otra persona | Representación |
| Participación contractual | Qué derechos y obligaciones aceptó | Contratos |
| Facultad sobre un producto | Qué actuaciones puede realizar sobre ese producto | Servicio responsable del producto |
| Facultad interna del banco | Qué decisiones puede aprobar un agente del banco | Institución |
| Restricciones | Qué medidas limitan la actuación | Restricciones y servicios que las aplican |
| Disponibilidad de recursos | Si puede comprometer fondos, crédito o instrumentos | Fondos, Cuenta de tarjeta o servicio responsable de la posición |
| Control especializado | Si cumple verificaciones de fraude, riesgo u otras exigibles | Servicio especializado correspondiente |

### Responsabilidad de la decisión

El servicio que acepta una actuación debe reunir las comprobaciones exigibles y determinar si permiten realizarla.

Los resultados aportados por otros servicios conservan su alcance y vigencia. No se interpretan como autorizaciones
generales para cualquier operación.

### Reglas

- La autenticación de una persona no demuestra que tenga facultades sobre un producto.

- Ser cliente no concede derechos sobre cuentas, contratos o fondos ajenos.

- Un representante solo puede actuar dentro del alcance vigente de su representación.

- Una autorización operativa específica no amplía facultades legales o contractuales.

- La titularidad, la representación y la condición de portador de una tarjeta son relaciones distintas.

- La actuación conjunta requiere las participaciones exigidas.

- La aceptación contractual identifica el contenido exacto y la versión aceptada.

- Una modificación material de participantes, importe, destino o condiciones exige revisar las comprobaciones afectadas.

- Las aprobaciones internas respetan facultades, límites y separación de funciones.

- Una aprobación comercial no levanta una restricción judicial o regulatoria.

- Los controles de fondos, crédito o tenencias se ejecutan bajo la autoridad que administra esos recursos.

- Las comprobaciones deben ser válidas en el momento exigido por la actuación; no basta con haberlas obtenido al iniciar
  el trámite.

- Una revocación o restricción posterior se aplica conforme a su momento de efecto y al avance de la operación.

- Una operación ya ejecutada conserva su historia, aunque posteriormente cambien las facultades del participante.

- Si una comprobación obligatoria falta, es contradictoria o está vencida, la actuación no se autoriza hasta resolverla.

- Las contingencias solo pueden utilizar alternativas expresamente admitidas; la falta de respuesta no se interpreta
  como permiso.

- Las operaciones automáticas deben tener una causa habilitante identificable, como un mandato, contrato, vencimiento o
  instrucción institucional válida.

### Evidencia de autorización

Cada actuación sensible conserva:

- La identidad del actor y de la persona por cuya cuenta actúa.

- El producto, contrato u operación alcanzados.

- La acción solicitada y sus condiciones.

- Las facultades comprobadas.

- Las aceptaciones o aprobaciones requeridas.

- Los controles utilizados, sus referencias y fechas.

- La decisión y su fundamento.

- El resultado de ejecución.

La evidencia permite justificar la actuación sin exponer credenciales ni información sensible innecesaria.

### Ejemplo de aplicación

Para que un apoderado ordene una transferencia:

- Personas identifica al apoderado y al representado.

- El canal autentica a quien presenta la instrucción.

- Representación comprueba el poder y su alcance.

- Cuentas comprueba las facultades operativas y la modalidad de actuación.

- Los servicios correspondientes comprueban restricciones y controles.

- Fondos determina y compromete la disponibilidad.

- Transferencias acepta la instrucción y conserva las referencias que justifican su ejecución.
