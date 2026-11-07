# Contratos de integración — Módulo 4. Cuentas y fondos

## 1. Propósito y alcance

Este documento define los intercambios ofrecidos por:

- Cuentas.

- Identificación bancaria.

- Fondos.

- Operaciones de efectivo.

Comprende apertura, habilitación y cierre; titularidades y autorizaciones; identificación bancaria; posiciones y
movimientos; reservas; descubierto; afectaciones monetarias; liquidaciones propias y efectivo.

Fondos es la autoridad sobre la posición monetaria de las cuentas.

Los solicitantes conservan la finalidad y el seguimiento de sus operaciones.

Préstamos y líneas administra el acuerdo crediticio, la deuda y los intereses de descubierto.

Posiciones y liquidez administra las existencias físicas simuladas y las disponibilidades propias del banco.

## 2. Catálogo de contratos

| Identificador | Contrato                                     | Proveedor               | Clase     |
|---------------|----------------------------------------------|-------------------------|-----------|
| CI-04-001     | Abrir y habilitar una cuenta                 | Cuentas                 | Solicitud |
| CI-04-002     | Consultar una cuenta y sus condiciones       | Cuentas                 | Consulta  |
| CI-04-003     | Modificar titulares y autorizaciones         | Cuentas                 | Solicitud |
| CI-04-004     | Modificar condiciones y capacidades          | Cuentas                 | Solicitud |
| CI-04-005     | Aplicar o levantar restricción operativa     | Cuentas                 | Solicitud |
| CI-04-006     | Consultar actuación de cuenta                | Cuentas                 | Consulta  |
| CI-04-007     | Solicitar cierre                             | Cuentas                 | Solicitud |
| CI-04-008     | Asignar o retirar identificadores            | Identificación bancaria | Solicitud |
| CI-04-009     | Administrar alias                            | Identificación bancaria | Solicitud |
| CI-04-010     | Resolver identificador                       | Identificación bancaria | Consulta  |
| CI-04-011     | Consultar actuación de identificación        | Identificación bancaria | Consulta  |
| CI-04-012     | Consultar posición y movimientos             | Fondos                  | Consulta  |
| CI-04-013     | Constituir reserva                           | Fondos                  | Solicitud |
| CI-04-014     | Consumir reserva                             | Fondos                  | Solicitud |
| CI-04-015     | Liberar reserva                              | Fondos                  | Solicitud |
| CI-04-016     | Modificar importe o firmeza de reserva       | Fondos                  | Solicitud |
| CI-04-017     | Registrar movimiento monetario               | Fondos                  | Solicitud |
| CI-04-018     | Ejecutar movimientos internos relacionados   | Fondos                  | Solicitud |
| CI-04-019     | Aplicar límite operativo de descubierto      | Fondos                  | Solicitud |
| CI-04-020     | Consultar utilización de descubierto         | Fondos                  | Consulta  |
| CI-04-021     | Corregir movimiento monetario                | Fondos                  | Solicitud |
| CI-04-022     | Consultar actuación monetaria                | Fondos                  | Consulta  |
| CI-04-023     | Consultar reserva                            | Fondos                  | Consulta  |
| CI-04-024     | Aplicar o levantar afectación monetaria      | Fondos                  | Solicitud |
| CI-04-025     | Liquidar conceptos propios de cuenta         | Cuentas                 | Solicitud |
| CI-04-026     | Registrar depósito de efectivo               | Operaciones de efectivo | Solicitud |
| CI-04-027     | Solicitar extracción de efectivo             | Operaciones de efectivo | Solicitud |
| CI-04-028     | Registrar resultado físico                   | Operaciones de efectivo | Solicitud |
| CI-04-029     | Resolver diferencia de efectivo              | Operaciones de efectivo | Solicitud |
| CI-04-030     | Consultar operación de efectivo              | Operaciones de efectivo | Consulta  |
| CI-04-031     | Comunicar cambio de cuenta                   | Cuentas                 | Evento    |
| CI-04-032     | Comunicar cambio de identificación           | Identificación bancaria | Evento    |
| CI-04-033     | Comunicar cambio de reserva                  | Fondos                  | Evento    |
| CI-04-034     | Comunicar movimiento confirmado              | Fondos                  | Evento    |
| CI-04-035     | Comunicar variación de descubierto           | Fondos                  | Evento    |
| CI-04-036     | Comunicar liquidación de cuenta              | Cuentas                 | Evento    |
| CI-04-037     | Comunicar resultado de efectivo              | Operaciones de efectivo | Evento    |
| CI-04-038     | Comunicar cierre o administración residual   | Cuentas                 | Evento    |
| CI-04-039     | Comunicar afectación monetaria aplicada      | Fondos                  | Evento    |
| CI-04-040     | Comprobar facultades y admisión de operación | Cuentas                 | Consulta  |
| CI-04-041     | Revalidar comprobación de operación          | Cuentas                 | Solicitud |

Las preparaciones y operaciones de seguimiento desarrollan el protocolo común sin modificar el carácter informativo de
las consultas.

## 3. Convenciones comunes

### 3.1. Identidades

Se distinguen operación, actuación, intento de ejecución, preparación, efecto, cuenta, reserva, movimiento, afectación y
corrección.

El efecto conserva su identidad aunque cambie el canal de comunicación o el servicio que ejecuta una etapa autorizada.

No se ejecuta el mismo efecto mediante consumo de reserva y débito independiente.

### 3.2. Información de entrada

Las solicitudes incluyen:

- Actuación y operación.

- Servicio solicitante.

- Finalidad.

- Objetos afectados.

- Fundamento de negocio.

- Autorizaciones y controles.

- Actor y representado cuando correspondan.

- Recepción original cuando tenga consecuencias.

- Versión esperada para modificaciones.

Los importes identifican moneda y concepto y respetan su unidad mínima.

Fondos no redondea silenciosamente instrucciones monetarias.

### 3.3. Autoridad

El solicitante justifica la operación. Fondos decide la admisión de su efecto monetario.

Las condiciones exigibles se comprueban mediante controles locales y preparaciones de autoridades cuando corresponde.

No se habilita descubierto por una indicación del solicitante si producto, cuenta o concepto no lo permiten.

### 3.4. Recursos

Fondos considera saldo, reservas, inmovilizaciones, créditos no disponibles y financiación autorizada sin descontar dos
veces el mismo recurso.

Las reservas utilizan primero fondos propios y después descubierto únicamente si está admitido.

Una reserva no equivale a un débito.

Los compromisos técnicos de un intento se identifican y cuentan una sola vez. Si ya existe una reserva suficiente, no se
crea otra por el mismo efecto.

### 3.5. Circuito de reservas

Cada reserva identifica:

- Responsable del negocio.

- Solicitante de constitución.

- Operación y efectos respaldados.

- Consumidores autorizados.

- Condiciones de consumo, modificación y liberación.

La autorización de consumidores procede del circuito admitido por Fondos.

En tarjetas de débito:

- Autorizaciones constituye la reserva.

- Procesamiento de consumos puede consumirla por una presentación válida.

- Operaciones de efectivo puede consumirla por la extracción vinculada.

- Una presentación posterior a una extracción debitada reconoce el efecto existente.

Conocer una reserva no concede permiso para utilizarla.

### 3.6. Descubierto

Una acreditación ordinaria reduce directamente el capital utilizado cuando el saldo es negativo.

Los intereses y cargos pendientes permanecen en Préstamos y líneas.

Su cobro requiere actuación separada y fondos propios disponibles. No genera nuevo descubierto.

### 3.7. Resultados y recuperación

Los resultados distinguen confirmación, rechazo sin ejecución, pendiente, incertidumbre y parcialidad admitida.

Cada respuesta identifica efectos, recursos y remanentes.

Misma actuación y contenido recupera el resultado. Otro contenido genera conflicto.

Una referencia desconocida no demuestra que una solicitud en tránsito nunca pueda ejecutarse.

## 4. Protocolo de ejecución monetaria

### 4.1. Coordinador

Fondos mantiene el intento que decide sus reservas o movimientos.

El servicio de negocio conserva su operación y el seguimiento de resultados externos.

La decisión monetaria no confirma automáticamente entrega física, aplicación de deuda ni liquidación externa.

### 4.2. Intento

El intento contiene:

| Dato                   | Significado                    |
|------------------------|--------------------------------|
| executionAttemptId     | Identidad estable              |
| actionId y operationId | Actuación y operación          |
| effectReferences       | Efectos individuales           |
| accountReferences      | Cuentas afectadas              |
| contentHash            | Contenido exacto               |
| requiredAuthorities    | Autoridades necesarias         |
| preparationReferences  | Preparaciones                  |
| resourceCommitments    | Coberturas                     |
| decisionDeadline       | Límite de decisión             |
| decision               | Pendiente, confirmar o abortar |
| resultReferences       | Reservas o movimientos         |
| version                | Concurrencia                   |

Sus estados distinguen preparación, listo para decidir, confirmado, abortado y recuperación requerida.

La misma actuación recupera su intento original.

### 4.3. Autoridades participantes

Intervienen únicamente las exigibles:

| Autoridad               | Condición                          |
|-------------------------|------------------------------------|
| Cuentas                 | Capacidades y permisos específicos |
| Representación          | Actuación por otra persona         |
| Restricciones           | Medidas aplicables                 |
| Institución             | Facultades internas                |
| Responsable del negocio | Fundamento habilitante             |

Los controles de fraude se vinculan al contenido y se utilizan dentro de su alcance y vigencia.

Las autoridades no se sustituyen entre sí.

### 4.4. Preparación

Fondos registra el intento antes de solicitar preparaciones.

Cada autoridad recibe acto, actores, contenido, objetos y límite temporal.

Su resultado identifica preparación, alcance, condiciones y consulta.

Una preparación no reserva dinero por sí sola.

Los cambios incompatibles conocidos antes de preparar impiden un resultado favorable.

Los posteriores se ordenan frente a la decisión mediante consulta y petición de aborto.

### 4.5. Decisión y efectos

Fondos controla intento, posiciones, reservas y afectaciones en orden estable.

Comprueba:

- Identidad de efectos.

- Contenido.

- Preparaciones.

- Vigencias.

- Ausencia de aborto confirmado.

- Condiciones locales.

- Disponibilidad o cobertura.

- Remanentes.

La decisión, las reservas o movimientos, las posiciones, la historia, la idempotencia y la outbox se confirman
conjuntamente.

No existe decisión monetaria confirmada sin sus efectos locales.

### 4.6. Aborto

El aborto durable impide ejecutar el mismo intento.

Se regularizan sus coberturas técnicas cancelables y se comunica la decisión a las autoridades.

Abortar un intento de consumo no libera automáticamente la reserva que respalda una obligación firme o incierta.

Una petición posterior a confirmar devuelve la decisión y los efectos existentes.

### 4.7. Interfaces de seguimiento

| Interfaz                                          | Finalidad                    |
|---------------------------------------------------|------------------------------|
| GET /fund-execution-attempts/{id}                 | Consultar intento y decisión |
| POST /fund-execution-attempts/{id}/abort-requests | Solicitar aborto             |

La petición identifica actuación, intento, causa, solicitante y preparación afectada cuando corresponda.

El resultado distingue ABORTED, ALREADY_COMMITTED y PENDING.

Una referencia desconocida no se informa como aborto confirmado. Se requiere resolver la actuación original e impedir su
recepción ejecutable posterior.

### 4.8. Vencimiento e incertidumbre

Al alcanzar el límite sin decisión conocida se solicita resolver el intento.

Fondos aborta si todavía no había confirmado.

Las autoridades no liberan preparaciones por timeout.

Si Fondos está indisponible, mantienen resolución pendiente.

La expiración de un trabajador no cancela el intento.

### 4.9. Efectivo

La entrega física tiene su punto de ejecución en Operaciones de efectivo.

Preparar la entrega y preparar su registro monetario son actuaciones relacionadas y diferentes.

Una revocación puede impedir una entrega todavía no realizada.

Después de una entrega comprobada, el débito se trata según el fundamento de regularización del hecho. No se exige
inventar una nueva instrucción del cliente ni se presenta la entrega como fallida.

## 5. Contratos de Cuentas

### CI-04-001. Abrir y habilitar una cuenta

Clase: solicitud. Proveedor: Cuentas. Consumidor: Solicitudes de productos.

Entrada: contrato y versión, producto, moneda, titulares, modalidad, condiciones, capacidades y constitución.

Actuación: comprueba el acuerdo y coordina posición e identificación bancaria.

Salida: cuenta, versión, apertura, capacidades, identificadores y pendientes.

Reglas y aceptación:

- Posición inicial cero.

- Abrir no acredita dinero.

- Misma constitución no crea otra cuenta.

- Dependencias incompletas impiden informar habilitación total.

- La moneda permanece inmutable.

### CI-04-002. Consultar una cuenta y sus condiciones

Clase: consulta. Proveedor: Cuentas. Consumidores: Servicios operativos, Contratos, controles y Gestión administrativa.

Entrada: cuenta, finalidad, alcance y fecha o versión.

Salida: producto, contrato, moneda, participantes, autorizaciones, modalidad, capacidades, restricciones operativas,
descubierto vinculado y cierre.

Reglas y aceptación: no informa disponibilidad como autoridad monetaria. Una consulta histórica no habilita actos
actuales.

### CI-04-003. Modificar titulares y autorizaciones

Clase: solicitud. Proveedor: Cuentas. Consumidores: Solicitudes de productos y Gestión administrativa.

Entrada: cuenta, versión, participantes, roles, actos, límites, modalidad, fundamento y conformidades.

Salida: versión, cambios, efecto y coordinación pendiente.

Reglas y aceptación:

- Titularidad exige formalización contractual.

- Un autorizado no adquiere propiedad.

- Una autorización específica no amplía representación general.

- Las revocaciones impiden nuevas preparaciones incompatibles.

- Los intentos preparados se resuelven con su coordinador.

- No se eliminan compromisos firmes anteriores.

### CI-04-004. Modificar condiciones y capacidades

Clase: solicitud. Proveedor: Cuentas. Consumidores: Contratos, Solicitudes de productos y Gestión administrativa.

Entrada: cuenta y versión, cambios, fundamento, efecto y dependencias.

Salida: condiciones, capacidades y pendientes.

Reglas y aceptación: una oferta no modifica la cuenta por sí sola. Retirar capacidades exige resolver intentos afectados
y conservar obligaciones anteriores.

La moneda no se cambia sobre la misma cuenta. El límite de descubierto utiliza CI-04-019.

### CI-04-005. Aplicar o levantar restricción operativa

Clase: solicitud. Proveedor: Cuentas. Consumidor: Restricciones.

Entrada: medida y versión, cuenta, acciones, fundamento, prioridad y efecto.

Salida: aplicación, acciones alcanzadas, fechas y pendientes.

Reglas y aceptación:

- Prohibir débitos no prohíbe automáticamente créditos.

- La restricción operativa no inmoviliza importes por sí sola.

- Los intentos preparados se resuelven explícitamente.

- Una afectación monetaria se aplica mediante Fondos.

- Levantar una causa no elimina otras.

### CI-04-006. Consultar actuación de cuenta

Clase: consulta. Proveedor: Cuentas. Consumidores: Solicitante autorizado y Gestión administrativa.

Entrada: actuación y finalidad.

Salida: cuenta, versiones, efectos, dependencias y pendientes.

Comprende apertura, cambios, restricciones, liquidaciones y cierre.

Aceptación: recupera la cuenta original; cálculo con cobro pendiente no se informa cobrado.

### CI-04-007. Solicitar cierre

Clase: solicitud. Proveedor: Cuentas. Consumidores: Contratos, Clientes y Gestión administrativa.

Entrada: cuenta, causa, actor, facultades, recepción, fundamento y destino autorizado de saldos.

Salida: cierre iniciado, limitaciones, liquidaciones, obligaciones, responsables y cierre o administración residual.

Reglas y aceptación:

- Recibe la solicitud aunque existan pendientes.

- Impide nuevos compromisos incompatibles.

- Resuelve intentos preparados frente a confirmación o aborto.

- No elimina reservas firmes o inciertas.

- No presume inventarios vacíos por falta de respuesta.

- No prolonga cargos por demoras internas.

- Los saldos a favor requieren destino y ejecución comprobados.

- Las devoluciones posteriores utilizan administración residual.

## 6. Contratos de Identificación bancaria

### CI-04-008. Asignar o retirar identificadores

Clase: solicitud. Proveedor: Identificación bancaria. Consumidor: Cuentas.

Entrada: cuenta, modalidad, moneda, referencias institucionales y fundamento.

Salida: identificador, situación para nuevos usos, historia y pendientes.

Reglas y aceptación: CBU simulado válido y único, sin reutilización para otra cuenta. Retirar no borra historia. La
repetición recupera la misma asignación.

### CI-04-009. Administrar alias

Clase: solicitud. Proveedor: Identificación bancaria. Consumidores: Cuentas y Gestión administrativa.

Entrada: cuenta, alias, modalidad, actor, facultades y asignación anterior.

Salida: asignación, rechazo o seguimiento incierto.

Reglas y aceptación:

- Máximo un alias vigente por cuenta.

- Un alias resuelve una cuenta en cada momento.

- La sustitución conserva la asignación anterior hasta confirmar.

- Cambiar alias no redirige una transferencia ya aceptada.

### CI-04-010. Resolver identificador

Clase: consulta. Proveedor: Identificación bancaria. Consumidores: Transferencias, Pagos y servicios autorizados.

Entrada: identificador, finalidad y alcance.

Salida: destino, participante, condición interna o externa, moneda, datos autorizados y momento.

Reglas y aceptación: resolver no autoriza el movimiento. No sustituye un destino inexistente. La información externa
conserva procedencia.

### CI-04-011. Consultar actuación de identificación

Clase: consulta. Proveedor: Identificación bancaria. Consumidores: Cuentas, Gestión administrativa y solicitante
autorizado.

Entrada: actuación y finalidad.

Salida: cuenta, identificador, modalidad, resultado y pendientes.

Aceptación: respuesta perdida de un cambio recupera la asignación existente sin repetirlo.

## 7. Contratos de Fondos

### CI-04-012. Consultar posición y movimientos

Clase: consulta. Proveedor: Fondos. Consumidores: Cuentas, servicios operativos, Extractos, Contabilidad y controles.

Entrada: cuenta, finalidad, corte, período y continuación.

Salida:

- Moneda y corte.

- Saldo registrado.

- Fondos propios utilizables.

- Reservas e inmovilizaciones.

- Créditos no disponibles.

- Límite y utilización de descubierto.

- Cobertura protegida.

- Capacidad nueva y exceso.

- Compromisos técnicos pendientes, sin duplicar reservas.

- Movimientos y correcciones.

La continuación conserva el corte.

Aceptación: saldo 100 con reserva 30 mantiene saldo 100 y capacidad propia 70. Saldo −60 y límite 40 presenta capacidad
nueva cero y exceso 20.

### CI-04-013. Constituir reserva

Clase: solicitud. Proveedor: Fondos. Consumidores: Transferencias, Pagos, Autorizaciones, Depósitos a plazo, Órdenes de
inversión, Cambio de moneda y Operaciones de efectivo.

Entrada: cuenta, importe positivo, moneda, conceptos, operación, efectos, firmeza, recursos admitidos y condiciones de
consumo y revisión.

Identifica responsable y circuito. Fondos determina ejecutores admitidos.

Salida: reserva, cobertura propia y crediticia, firmeza, remanente, ejecutores e intento.

Reglas y aceptación:

- Preparaciones exigibles antes de confirmar.

- Comprobación y afectación local conjunta.

- Capacidad 100 no permite dos reservas de 80.

- Fondos propios 30 y reserva 50 utiliza crédito 20 solo si está permitido.

- Una cobertura técnica previa del mismo intento no se cuenta otra vez.

### CI-04-014. Consumir reserva

Clase: solicitud. Proveedor: Fondos. Consumidores: Responsable o ejecutor autorizado del circuito.

Entrada: reserva, operación, presentación o hecho habilitante, importe, moneda, conceptos, efecto, evidencia y fechas.

Salida: movimientos, consumo, remanente, descubierto e intento.

Reglas:

- Comprueba consumidor, fundamento, alcance y cobertura.

- No cambia cuenta, moneda o finalidad.

- No supera remanente sin cobertura adicional válida.

- Ordena condiciones exigibles mediante preparación.

- Confirma débito y reducción de reserva conjuntamente.

- No debita otra vez una extracción o presentación existente.

- Abortar el consumo no libera la obligación respaldada.

Aceptación:

| Situación                                    | Resultado                        |
|----------------------------------------------|----------------------------------|
| Saldo 100, reserva 30 y consumo 30           | Saldo 70, reserva cero           |
| Reserva 50 y consumo 20                      | Remanente 30                     |
| Presentación válida del ejecutor autorizado  | Consumo dentro del circuito      |
| Servicio ajeno                               | Rechazo                          |
| Extracción debitada y presentación posterior | Referencia al efecto existente   |
| Consumo y liberación concurrentes            | Remanente utilizado una sola vez |

### CI-04-015. Liberar reserva

Clase: solicitud. Proveedor: Fondos. Consumidor: Responsable del negocio.

Entrada: reserva, importe, causa y evidencia de ausencia de obligación o ejecución pendiente.

Salida: liberación, remanente y cobertura extinguida.

Reglas y aceptación: liberar no acredita saldo. Una reserva firme o incierta requiere resolución comprobada. El
vencimiento no prueba que no hubo ejecución.

### CI-04-016. Modificar importe o firmeza

Clase: solicitud. Proveedor: Fondos. Consumidor: Responsable del negocio conforme al circuito.

Entrada: reserva, versión, importe o firmeza propuestos, causa y evidencia.

Salida: reserva actualizada, coberturas, remanente e impedimentos.

Reglas y aceptación:

- Aumentar exige capacidad adicional y controles aplicables.

- Reducir exige comprobar la parte cancelable.

- No se reduce por debajo de lo consumido.

- No se convierte incertidumbre en cancelación mediante una etiqueta.

- Una modificación no duplica la cobertura existente.

### CI-04-017. Registrar movimiento monetario

Clase: solicitud. Proveedor: Fondos. Consumidores: Servicios propietarios de operaciones autorizadas.

Entrada: cuenta, efecto, sentido, importe, moneda, concepto, fundamento, controles y fechas.

Salida: movimiento, posición, descubierto e intento.

Reglas y aceptación:

- Una reserva aplicable se consume por su circuito; no se elude con otro débito.

- El crédito condicionado identifica causa y responsable.

- Los débitos respetan recursos y financiación admitidos.

- La acreditación ordinaria reduce capital de descubierto sin cobrar implícitamente intereses.

- Una respuesta perdida recupera el movimiento original.

### CI-04-018. Ejecutar movimientos internos relacionados

Clase: solicitud. Proveedor: Fondos. Consumidores: Transferencias, Cambio de moneda y servicios autorizados.

Modalidades:

- Traslado entre cuentas en la misma moneda.

- Intercambio interno coordinado por Cambio de moneda.

Entrada común: operación, modalidad, actuación, efectos, cuentas, sentidos, importes, monedas, conceptos, controles,
reservas y fechas.

Entrada de traslado: origen, destino y principal común.

Entrada de intercambio:

- Intercambio y propuesta aceptada.

- Cotización y sentido.

- Ambas monedas e importes.

- Cargos y tributos separados.

- Referencias verificables de compromiso de preparación de liquidez y capacidad de riesgo exigibles.

Salida: decisión conjunta, movimientos, reservas consumidas, remanentes e intento.

Reglas:

- El traslado conserva importe y moneda del principal.

- Origen y destino no son la misma cuenta en un traslado.

- Cada cuenta conserva su moneda.

- Fondos no calcula ni sustituye cotización.

- No exige igualdad numérica entre monedas diferentes.

- No deduce una conversión implícita.

- Reservas, débitos y créditos del conjunto confirman localmente juntos.

- Una condición impeditiva anterior al commit impide todo el conjunto.

- No confirma existencias físicas ni saldos externos del banco.

- Las regularizaciones posteriores de Tesorería y Riesgos mantienen seguimiento sin repetir el intercambio.

Aceptación:

| Situación                                      | Resultado                                    |
|------------------------------------------------|----------------------------------------------|
| Origen 100, destino 20 y traslado 30           | Origen 70 y destino 50                       |
| Destino no admite crédito                      | No confirma solo el débito                   |
| Compra de USD 100 por ARS 102.000              | Débito y crédito conjuntos, cargos separados |
| Falta cambio aceptado                          | No convierte                                 |
| Mismo conjunto repetido                        | Recupera todos sus movimientos               |
| Fondos confirmó y falta regularización interna | Conserva confirmación y pendientes           |

### CI-04-019. Aplicar límite operativo de descubierto

Clase: solicitud. Proveedor: Fondos. Consumidor: Préstamos y líneas.

Entrada: cuenta, financiación, autorización, versión, límite, moneda, vigencia y actuación sustituida.

Salida: límite, utilización, compromisos, cobertura protegida, exceso y pendientes.

Reglas y aceptación:

- Fondos no concede crédito.

- El aumento se utiliza después de confirmar.

- La reducción conserva deuda.

- Revisa reservas cancelables incompatibles desde las más recientes.

- Protege las firmes e inciertas.

- Capital 60 y límite 40 produce exceso 20.

- Capital 20, reserva firme 70 y límite 40 requiere cobertura adicional protegida 50, sin capacidad nueva.

### CI-04-020. Consultar utilización de descubierto

Clase: consulta. Proveedor: Fondos. Consumidores: Préstamos y líneas, Riesgos y Cuentas.

Entrada: cuenta o financiación, corte y finalidad.

Salida: límite, versión, capital, reservas, cobertura protegida, capacidad, exceso y variaciones.

Aceptación: capital 100 e interés pendiente 10 informa utilización 100. Una acreditación ordinaria 50 reduce capital a

50.

### CI-04-021. Corregir movimiento monetario

Clase: solicitud. Proveedor: Fondos. Consumidor: Responsable original, con aprobación exigible.

Entrada: original, correcciones previas, causa, importe, conceptos, relaciones, fechas y fundamento.

Salida: movimientos correctivos, regularizado, remanente y consecuencias.

Reglas y aceptación:

- Conserva el original.

- No supera el efecto pendiente.

- No autoriza débitos ajenos sin fundamento.

- No crea financiación.

- Los errores exclusivamente contables pertenecen a Contabilidad.

- Los intereses se recalculan por su responsable.

- De 20 con 12 corregidos quedan como máximo 8.

### CI-04-022. Consultar actuación monetaria

Clase: consulta. Proveedor: Fondos. Consumidores: Solicitante, coordinador y conciliadores autorizados.

Entrada: actuación o efecto y finalidad.

Salida: intento, decisión, movimientos, reservas, afectaciones, importes, monedas, fechas y pendientes.

Comprende límites, correcciones y conjuntos internos.

Aceptación: un intercambio recupera ambas piernas sin iniciar otro cambio. Pendiente e inexistencia comprobada son
resultados diferentes.

### CI-04-023. Consultar reserva

Clase: consulta. Proveedor: Fondos. Consumidores: Responsable, ejecutores, Cuentas y controles autorizados.

Entrada: reserva o referencia original, corte y finalidad.

Salida: importes inicial, vigente, consumido y liberado; firmeza, cobertura, circuito, movimientos y pendientes.

Reglas y aceptación: no libera ni concede permisos. Reserva 50 y consumo 20 informa remanente 30. Vencimiento no
demuestra ausencia de ejecución.

### CI-04-024. Aplicar o levantar afectación monetaria

Clase: solicitud. Proveedor: Fondos.

| Modalidad                            | Consumidor                             |
|--------------------------------------|----------------------------------------|
| Inmovilización por medida            | Restricciones                          |
| Habilitación de crédito condicionado | Responsable registrado de la condición |

Los permisos son independientes.

Entrada para medida: medida y versión, cuenta, asignación, moneda, prioridad, efecto y revisión.

Entrada para crédito condicionado: crédito y movimiento originales, condición, responsable, hecho, evidencia, importe
habilitable y fecha.

Salida: afectación o habilitación, insuficiencia, remanente y otras causas subsistentes.

Reglas:

- Inmovilizar no debita; liberar no acredita.

- No utiliza descubierto para completar medidas.

- Respeta asignaciones de Restricciones.

- No habilita más que la parte pendiente de la condición.

- El solicitante no cambia al responsable registrado.

- Cumplir una condición no levanta otras medidas.

- Aplicación y efectos monetarios locales se ordenan con los intentos afectados.

Aceptación:

| Situación                               | Resultado                               |
|-----------------------------------------|-----------------------------------------|
| Medida 100 y fondos afectables 60       | Afecta 60 y conserva insuficiencia 40   |
| Crédito 100 condicionado y cumplimiento | Extingue esa condición sin otro crédito |
| Responsable no autorizado               | No habilita                             |
| Subsiste otra medida                    | Mantiene su afectación                  |
| Cumplimiento parcial admitido de 40     | Conserva 60 pendientes                  |
| Repetición                              | Sin doble liberación                    |

## 8. Liquidaciones propias

### CI-04-025. Liquidar conceptos propios de cuenta

Clase: solicitud. Proveedor: Cuentas. Consumidores: Gestión administrativa autorizada y proceso de cierre.

Entrada: cuenta, concepto, período u ocurrencia, causa y condiciones contractuales.

Salida: bases, condiciones, bruto, bonificaciones, tributos, neto, movimientos y pendientes.

Reglas:

- Remuneración sobre saldo positivo registrado al cierre diario.

- Interés simple Actual/365.

- Reservas e inmovilizaciones no reducen esa base.

- Acumulación sin redondeo diario.

- Liquidación ordinaria mensual y acreditación el primer hábil siguiente.

- Al terminar se liquida el período parcial.

- Cargo mensual proporcional a días de servicio: incluye inicio y excluye terminación efectiva.

- Bonificaciones anteriores a tributos.

- Intereses de descubierto pertenecen a Préstamos y líneas.

- Cobro sin fondos propios queda pendiente, sin crear descubierto ni otra liquidación.

Aceptación: saldo 1.000 por diez días al 36,5 % produce bruto 10. Mensualidad 30 en mes de treinta días con quince de
servicio produce 15.

## 9. Contratos de Operaciones de efectivo

### CI-04-026. Registrar depósito

Clase: solicitud. Proveedor: Operaciones de efectivo. Consumidor: Gestión administrativa mediante ejecutor simulado
autorizado.

Entrada: depositante, cuenta, moneda, importe declarado, ubicación y actuación.

Salida: operación, requisitos y verificación física o resultado.

Reglas y aceptación: declarar 100 no acredita. La recepción comprobada genera obligación de acreditar o restituir.
Tesorería registra la existencia física correspondiente.

### CI-04-027. Solicitar extracción

Clase: solicitud. Proveedor: Operaciones de efectivo. Consumidores: Gestión administrativa y Autorizaciones.

Entrada: cuenta, importe, moneda, actor, ubicación, controles y autorización o reserva existente.

Salida: operación, cobertura monetaria, cobertura física, instrucción o impedimento.

Reglas:

- Reutiliza una reserva válida del circuito.

- Prepara la entrega con las condiciones exigibles.

- Una cancelación confirmada antes de entregar impide su ejecución posterior.

- Si falla una cobertura, regulariza la otra solo cuando es comprobablemente cancelable.

- Efectivo insuficiente no se convierte en entrega ficticia.

- Autorización no demuestra entrega.

- Repetir no crea otra instrucción.

### CI-04-028. Registrar resultado físico

Clase: solicitud. Proveedor: Operaciones de efectivo. Consumidor: Gestión administrativa, transmitiendo evidencia del
ejecutor autorizado.

Entrada: operación, ubicación, ejecutor, importe comprobado, moneda, momento, resultado y evidencia.

Salida: hecho físico, movimientos, remanentes y diferencias.

Reglas:

- Prevalece el importe comprobado.

- La parcialidad requiere modalidad admitida.

- La incertidumbre no se informa como fallo sin entrega.

- Entrega comprobada exige regularizar débito y salida física.

- Recepción comprobada exige acreditar o restituir.

- La regularización utiliza el fundamento del hecho realizado.

- La repetición no duplica variación física ni monetaria.

### CI-04-029. Resolver diferencia

Clase: solicitud. Proveedor: Operaciones de efectivo. Consumidor: Gestión administrativa.

Entrada: operación, diferencia, registros, evidencia, conclusión y aprobación separada.

Salida: resolución, regularizaciones provisionales o definitivas y pendientes.

Reglas y aceptación: faltante físico sin prueba de entrega no se debita al cliente. Tesorería corrige existencias y
Fondos movimientos. Resolver no equivale a reparar hasta comprobar efectos.

### CI-04-030. Consultar efectivo

Clase: consulta. Proveedor: Operaciones de efectivo. Consumidores: Gestión administrativa, Autorizaciones, Procesamiento
de consumos, Cuentas, Tesorería y controles.

Entrada: operación o actuación, finalidad y corte.

Salida: declarado, comprobado, coberturas, entrega o recepción, movimientos y diferencias.

Aceptación: una extracción incierta no habilita reentrega ni segundo débito.

## 10. Eventos

Todos incluyen hecho, emisor, objeto, versión o secuencia, actuación, operación, causa y fechas.

### CI-04-031. Comunicar cambio de cuenta

Emisor: Cuentas. Consumidores: Fondos, Identificación bancaria, Contratos, Solicitudes de productos y vinculados.

Contenido: cuenta, versión, cambio, capacidades, participantes, efecto y pendientes.

Distingue registro del cambio y aplicación a intentos afectados. No informa saldo ni crédito por inferencia.

### CI-04-032. Comunicar cambio de identificación

Emisor: Identificación bancaria. Consumidores: Cuentas, Transferencias y autorizados.

Contenido: cuenta, identificador, asignaciones, vigencias y confirmación del directorio.

Conserva destinos aceptados e historia.

### CI-04-033. Comunicar cambio de reserva

Emisor: Fondos. Consumidores: Responsable, ejecutores, Cuentas y controles.

Contenido: reserva, operación, cambio, importes, firmeza, cobertura, remanente y movimientos.

Consumir referencia el movimiento; no representa otro débito. Liberar no es acreditar.

### CI-04-034. Comunicar movimiento confirmado

Emisor: Fondos. Consumidores: Responsable, Contabilidad, Extractos y controles.

Contenido: movimiento, efecto, cuenta, intento, importe, moneda, sentido, concepto, fechas, reserva y correcciones.

Los conjuntos incluyen referencia común y relaciones entre efectos.

Contabilidad evita doble cómputo. El débito no demuestra aplicación del acreedor ni liquidación externa.

### CI-04-035. Comunicar variación de descubierto

Emisor: Fondos. Consumidores: Préstamos y líneas, Riesgos y Cuentas.

Contenido: financiación, límite, versión, capital anterior y resultante, cobertura protegida, exceso y movimientos.

La variación se refleja una vez y no cobra intereses implícitamente.

### CI-04-036. Comunicar liquidación de cuenta

Emisor: Cuentas. Consumidores: Contabilidad, Fondos para actuaciones autorizadas y atención.

Contenido: liquidación, período, bases, conceptos, bruto, beneficios, tributos, neto y ejecución.

Calculado no significa cobrado. El evento no repite un comando ya ejecutado.

### CI-04-037. Comunicar resultado de efectivo

Emisor: Operaciones de efectivo. Consumidores: Tesorería, Cuentas, Autorizaciones, Procesamiento de consumos,
Contabilidad y atención.

Contenido: operación, ubicación, importes, resultado físico, movimientos y diferencias.

Identifica regularizaciones pendientes. La presentación de red reconoce el débito existente.

### CI-04-038. Comunicar cierre o administración residual

Emisor: Cuentas. Consumidores: Contratos, Clientes, Identificación bancaria, vinculados y atención.

Contenido: cuenta, actuación, cese, régimen, identificadores, obligaciones y responsables.

Distingue limitación de usos, cierre y administración residual. No extingue deuda ni reabre silenciosamente la cuenta.

### CI-04-039. Comunicar afectación aplicada

Emisor: Fondos. Consumidores: Restricciones, responsable de la condición y Cuentas según alcance.

Contenido: modalidad, causa, cuenta, versión, importe, moneda, efecto, insuficiencia y afectaciones subsistentes.

Una medida 100 aplicada por 60 conserva insuficiencia 40. Cumplir una condición no elimina otras causas.

## 11. Comprobación y preparación de Cuentas

### CI-04-040. Comprobar facultades y admisión

Clase: consulta. Proveedor: Cuentas. Consumidores: Transferencias, Pagos, Autorizaciones, Operaciones de efectivo y
servicios autorizados.

Entrada: cuenta, actor, representado, acto, importe, moneda, participantes, contenido y momento.

Salida: admitida, impedida o pendiente; facultades específicas, capacidades, participantes faltantes, restricciones
consideradas, versión y alcance.

Reglas:

- Comprueba permisos propios de cuenta.

- Las referencias de representación conservan autoridad en Representación.

- Las conformidades conjuntas corresponden al mismo contenido.

- No reserva dinero ni crea preparación.

- No sustituye los controles monetarios de Fondos.

Aceptación: permiso de consulta no permite extraer. Cambiar materialmente importe o destino exige otra evaluación.

### CI-04-041. Revalidar comprobación de operación

Clase: solicitud. Proveedor: Cuentas. Consumidores: Transferencias, Pagos, Autorizaciones, Operaciones de efectivo y
servicios autorizados.

Entrada: cuenta e identificador de la comprobación original (checkId).

Actuación: recupera la comprobación original y su contenido, verifica que el solicitante esté autorizado y vuelve a
evaluar las condiciones y facultades administradas por Cuentas.

Salida: identificador de comprobación, resultado ADMITTED o PREVENTED, versión de cuenta, momento de evaluación y motivo
cuando corresponda.

Reglas:

- La revalidación utiliza el contenido original sin admitir modificaciones del solicitante.

- Comprueba capacidades, permisos específicos, participantes, actuación conjunta y restricciones aplicables.

- Considera las condiciones vigentes y los cambios de cuenta confirmados desde la comprobación original.

- Las conformidades conjuntas deben corresponder al mismo contenido y encontrarse vigentes.

- La ausencia de facultades o conformidades exigibles impide admitir la operación.

- Una comprobación inexistente o ajena al alcance autorizado del solicitante no puede revalidarse.

- Una comprobación con decisión definitiva conserva su resultado y no se reabre mediante este contrato.

- Una comprobación pendiente se resuelve como ADMITTED o PREVENTED cuando existen elementos suficientes para decidir.

- El resultado definitivo se conserva junto con la comprobación, su contenido y la versión de cuenta evaluada.

- Las solicitudes repetidas recuperan la decisión conservada sin generar otra evaluación.

- Una indisponibilidad técnica o información indispensable no verificable impide concluir la revalidación y no se
  interpreta como PREVENTED.

- La revalidación no reserva dinero, no crea preparaciones ni sustituye las decisiones de otras autoridades.

- Una decisión favorable corresponde a las condiciones evaluadas y no constituye autorización permanente.

Aceptación: una comprobación pendiente obtiene una decisión definitiva de Cuentas cuando sus condiciones pueden
verificarse. El resultado identifica la versión evaluada y se recupera ante solicitudes repetidas. Los impedimentos
funcionales se distinguen de las fallas técnicas y de las decisiones pertenecientes a otras autoridades.

### 11.1. Preparación explícita de cuenta

Cuando un coordinador necesita mantener una condición propia de Cuentas, utiliza:

| Interfaz                                                      | Finalidad                               |
|---------------------------------------------------------------|-----------------------------------------|
| POST /account-action-preparations                             | Preparar capacidad o permiso específico |
| GET /account-action-preparations/{id}                         | Consultar preparación                   |
| POST /account-action-preparations/{id}/decision-notifications | Incorporar decisión verificable         |

La solicitud identifica coordinador, intento, cuenta, acto, actores, contenido, importes pertinentes y límite.

Cuentas prepara únicamente su autoridad. No prepara representación o restricciones en nombre de otros servicios.

Los cambios incompatibles utilizan los mismos controles locales, bloquean nuevas preparaciones y solicitan resolver
intentos existentes.

Las preparaciones no se liberan por timeout.

## 12. Integraciones consumidas

| Proveedor                 | Contratos o capacidades           | Uso                               |
|---------------------------|-----------------------------------|-----------------------------------|
| Institución               | CI-01-004 y preparación exigible  | Facultades internas               |
| Catálogo de productos     | CI-01-005                         | Capacidades                       |
| Calendarios bancarios     | CI-01-015                         | Fechas                            |
| Personas                  | CI-02-002                         | Participantes                     |
| Clientes                  | CI-02-015                         | Relación comercial                |
| Representación            | CI-02-021 y preparación explícita | Actuación ajena                   |
| Contratos                 | CI-03-022                         | Acuerdo                           |
| Solicitudes de productos  | CI-03-016                         | Resultado de constitución         |
| Préstamos y líneas        | CI-06-016, CI-06-018 y CI-06-021  | Descubierto y conceptos separados |
| Autorizaciones            | CI-07-013                         | Operación y reserva vinculadas    |
| Procesamiento de consumos | CI-07-021                         | Presentación y efectos existentes |
| Cambio de moneda          | CI-10-023                         | Intercambio aceptado              |
| Posiciones y liquidez     | CI-10-006 y CI-10-007             | Cobertura física                  |
| Posiciones y liquidez     | CI-10-003                         | Existencias comprobadas           |
| Restricciones             | CI-11-027 y preparación explícita | Medidas aplicables                |
| Restricciones             | CI-11-029                         | Resultado de aplicación           |
| Prevención de fraude      | CI-11-023                         | Control sensible                  |
| Criterios tributarios     | CI-11-033                         | Tratamiento                       |
| Gestión administrativa    | CI-12-011 a CI-12-013             | Tareas y aprobaciones             |
| Documentos y comprobantes | CI-12-017 y CI-12-018             | Evidencias                        |
| Comunicaciones            | CI-12-028 y CI-12-031             | Entregas                          |
| Servicios vinculados      | Inventarios y actuaciones propias | Cierre                            |

Las consultas no sustituyen preparaciones exigibles.

### 12.1. Tarjetas de débito

Autorizaciones reserva. Procesamiento consume por presentación válida.

En extracciones, Operaciones de efectivo reutiliza la reserva y regulariza conforme al resultado físico.

La presentación posterior no vuelve a debitar.

### 12.2. Cambio interno

Cambio de moneda conserva propuesta, aceptación, cotización y coordinación.

Fondos confirma ambas piernas mediante CI-04-018.

Posiciones y liquidez resuelve el compromiso temporal de preparación sin inventar una salida externa.

Riesgos transforma el compromiso en la exposición correspondiente.

Una regularización pendiente no autoriza repetir el conjunto monetario confirmado.

### 12.3. Disponibilidad condicionada

El crédito original identifica condición y responsable.

Ese responsable solicita habilitación mediante CI-04-024 con evidencia.

Fondos extingue solo esa causa y conserva las demás afectaciones.

## 13. Correspondencia funcional

| Operación                            | Contratos principales                      |
|--------------------------------------|--------------------------------------------|
| OF-04-01. Apertura                   | CI-04-001, CI-04-008, CI-04-031            |
| OF-04-02. Titulares y autorizaciones | CI-04-003, CI-04-040, CI-04-041            |
| OF-04-03. Condiciones y capacidades  | CI-04-004, CI-04-031                       |
| OF-04-04. Restricciones              | CI-04-005, CI-04-024, CI-04-039            |
| OF-04-05. Identificadores            | CI-04-008, CI-04-032                       |
| OF-04-06. Alias                      | CI-04-009, CI-04-032                       |
| OF-04-07. Resolución                 | CI-04-010                                  |
| OF-04-08. Saldos y movimientos       | CI-04-012                                  |
| OF-04-09. Reservas                   | CI-04-013, CI-04-033                       |
| OF-04-10. Administración de reservas | CI-04-014, CI-04-015, CI-04-016, CI-04-023 |
| OF-04-11. Movimientos                | CI-04-017, CI-04-034                       |
| OF-04-12. Movimientos internos       | CI-04-018, CI-04-034                       |
| OF-04-13. Descubierto                | CI-04-019, CI-04-020, CI-04-035            |
| OF-04-14. Liquidaciones              | CI-04-025, CI-04-036                       |
| OF-04-15. Depósito de efectivo       | CI-04-026, CI-04-028, CI-04-037            |
| OF-04-16. Extracción                 | CI-04-027, CI-04-028, CI-04-037            |
| OF-04-17. Diferencias                | CI-04-029                                  |
| OF-04-18. Correcciones               | CI-04-021, CI-04-034                       |
| OF-04-19. Cierre                     | CI-04-007, CI-04-038                       |

CI-04-006, CI-04-011, CI-04-022 y CI-04-030 recuperan actuaciones de sus responsables.

Las consultas de intentos y preparaciones permiten resolver la coordinación sin repetir efectos ni presumir decisiones
desconocidas.
