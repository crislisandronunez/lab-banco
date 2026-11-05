## Módulo 4. Cuentas y fondos

Este módulo administra las cuentas bancarias, las facultades para operarlas y sus posiciones monetarias. Es responsable
de que cada movimiento respete las condiciones de la cuenta y de que los mismos fondos no se utilicen dos veces.

Se divide en cuatro microservicios: Cuentas, Identificación bancaria, Fondos y Operaciones de efectivo.

### 1. Cuentas

Descripción: representa cada cuenta bancaria y administra su ciclo de vida y sus condiciones operativas.

Responsabilidades:

- Abrir cuentas a partir de una contratación válida.

- Mantener el tipo de cuenta, moneda, producto y contrato de origen.

- Administrar titulares, cotitulares, autorizados y modalidades de actuación individual o conjunta.

- Determinar las operaciones admitidas según el producto y las condiciones contratadas.

- Administrar las habilitaciones operativas y aplicar medidas recibidas de Restricciones.

- Mantener el alcance, la vigencia y la versión de las condiciones operativas aplicables.

- Mantener la vinculación con la financiación de descubierto autorizada.

- Calcular y liquidar remuneraciones, comisiones de servicio y tributos propios de la cuenta.

- Administrar cambios de condiciones operativas.

- Coordinar el cierre con Fondos y los módulos que mantienen instrumentos, operaciones u obligaciones vinculadas.

Datos propios: cuentas, referencias contractuales, titularidades operativas, autorizaciones específicas, modalidades de
actuación, capacidades operativas, restricciones aplicadas, referencias de descubierto, liquidaciones de conceptos
propios de la cuenta y antecedentes del procedimiento de cierre.

Límites:

- Contratos conserva el acuerdo y sus participantes; Cuentas mantiene su aplicación operativa.

- Representación determina las facultades generales para actuar por otra persona; Cuentas administra las autorizaciones
  específicas sobre la cuenta.

- Fondos mantiene movimientos, reservas, saldos y afectaciones monetarias.

- Préstamos y líneas concede y administra la financiación de descubierto; Cuentas conserva su vinculación operativa.

- Los intereses y cargos derivados del crédito utilizado corresponden a Préstamos y líneas. Cuentas no los calcula
  nuevamente.

- No administra la deuda de préstamos, tarjetas ni depósitos a plazo.

- Restricciones conserva el fundamento y alcance de las medidas; Cuentas administra su aplicación sobre las operaciones
  admitidas.

Relaciones:

- Solicitudes de productos y Contratos proporcionan los antecedentes de apertura.

- Personas, Clientes y Representación permiten comprobar participantes y facultades.

- Identificación bancaria asigna los identificadores de la cuenta.

- Fondos aplica las condiciones operativas y confirma las afectaciones monetarias y limitaciones de cierre.

- Préstamos y líneas informa la financiación de descubierto vinculada.

- Restricciones comunica medidas, modificaciones y levantamientos y recibe confirmaciones de aplicación.

- Los módulos de productos informan vínculos y obligaciones que afectan al cierre.

- Atención y operación administrativa recibe información para extractos, comprobantes y comunicaciones.

Reglas y excepciones:

- La apertura no habilita operaciones mientras falten requisitos obligatorios.

- Una cuenta tiene moneda definida; una conversión requiere una operación cambiaria.

- Una autorización específica no puede ampliar las facultades legales del participante.

- Las restricciones identifican acciones alcanzadas, momento de efecto y condiciones de aplicación.

- Una comprobación previa no habilita a ignorar una restricción que resulte aplicable antes de ejecutar la operación.

- Una restricción operativa no inmoviliza dinero por sí sola. Cuando exige afectar un importe, su aplicación se coordina
  con Fondos y se confirma a Restricciones.

- Una medida no se informa como aplicada mientras falten las confirmaciones necesarias.

- El inicio del cierre impide aceptar nuevos compromisos incompatibles con su finalización y exige confirmación de esa
  limitación por los servicios afectados.

- Las operaciones aceptadas previamente se identifican y reciben tratamiento expreso; no se rechazan ni se eliminan
  automáticamente.

- El cierre comprueba saldos, reservas, descubierto utilizado, instrumentos y operaciones pendientes mediante
  confirmaciones de sus responsables.

- Durante el cierre solo se admiten las actuaciones autorizadas para resolver obligaciones y posiciones pendientes.

- La cuenta se cierra cuando se cumplen las condiciones contractuales y operativas y se confirma el tratamiento de los
  vínculos afectados.

- Una cuenta cerrada conserva su historia. Las correcciones posteriores siguen un procedimiento identificado y
  autorizado.

### 2. Identificación bancaria

Descripción: administra los identificadores que permiten reconocer y localizar cuentas dentro de la simulación bancaria.

Responsabilidades:

- Asignar identificadores bancarios y CBU simulados.

- Administrar altas, cambios y bajas de alias.

- Mantener la correspondencia entre identificadores y cuentas.

- Resolver consultas sobre cuentas internas.

- Coordinar consultas y cambios de alias con el directorio interbancario simulado.

- Conservar la historia de asignaciones y sus períodos de vigencia.

- Aplicar las condiciones de utilización de identificadores durante el cierre de una cuenta.

Datos propios: identificadores bancarios, CBU simulados, alias, asignaciones a cuentas, vigencias y resultados de
intercambios con el directorio simulado.

Límites:

- No abre cuentas ni administra titulares, saldos o permisos.

- Resolver un identificador no autoriza una operación.

- La información sobre cuentas externas procede del participante o directorio simulado correspondiente; no convierte
  esas cuentas en cuentas propias del banco.

- No administra beneficiarios guardados ni instrucciones de transferencia.

- Cuentas determina las condiciones de operación y cierre; este servicio aplica sus consecuencias sobre los
  identificadores.

Relaciones:

- Cuentas solicita asignaciones y comunica cambios de habilitación o cierre.

- Pagos y transferencias consulta identificadores para determinar destinos.

- Atención y operación administrativa utiliza los identificadores para informar al cliente.

Reglas y excepciones:

- Cada identificador debe resolver una única cuenta durante su vigencia.

- Un alias no puede tener dos asignaciones vigentes simultáneas.

- Un cambio de alias conserva la trazabilidad de su asignación anterior.

- Los CBU asignados no se reutilizan para otra cuenta dentro de la simulación.

- Una falla del directorio externo deja pendiente la confirmación del cambio; no permite informar que fue realizado.

- Durante el cierre se distingue la resolución del identificador de la admisión de nuevas operaciones.

- El cierre retira la posibilidad de utilizar los identificadores para nuevas operaciones y conserva sus referencias
  históricas.

- Las operaciones anteriores y sus devoluciones conservan la referencia al identificador utilizado originalmente.

### 3. Fondos

Descripción: administra los movimientos monetarios, las reservas y los saldos de las cuentas. Es la autoridad que
determina si una cuenta puede comprometer un importe, incluyendo el uso de descubierto autorizado.

Responsabilidades:

- Mantener el registro monetario de cada cuenta y moneda.

- Registrar débitos, créditos, devoluciones y ajustes.

- Determinar saldo contable y saldo disponible.

- Crear, consumir, liberar y vencer reservas.

- Administrar inmovilizaciones de importes y afectaciones parciales conforme a medidas válidas.

- Mantener el límite de descubierto operativo autorizado para cada cuenta, su vigencia, utilización y compromisos
  pendientes.

- Controlar exclusivamente la capacidad de descubierto inmediatamente utilizable sobre la cuenta.

- Comprobar disponibilidad y registrar su afectación como una única decisión.

- Aplicar las habilitaciones y restricciones efectivas que correspondan al movimiento.

- Ejecutar movimientos entre cuentas internas conservando la correspondencia entre débito y crédito.

- Informar a Préstamos y líneas las utilizaciones y reducciones del capital de descubierto.

- Conciliar esas utilizaciones con la deuda administrada por Préstamos y líneas.

- Impedir efectos duplicados por repetición de instrucciones.

- Conservar referencias al hecho de negocio que origina cada movimiento.

- Aplicar las limitaciones monetarias del procedimiento de cierre y confirmar sus posiciones pendientes.

Datos propios: posiciones monetarias de cuentas, movimientos, reservas, afectaciones de importes, saldos, límites
operativos de descubierto recibidos, utilización monetaria, compromisos pendientes y resultados de ejecución de
instrucciones.

Límites:

- No decide si un préstamo debe aprobarse, un consumo es legítimo o un pago corresponde a una obligación.

- No concede ni amplía financiación.

- Préstamos y líneas administra el acuerdo crediticio, la deuda y los intereses y cargos de descubierto.

- Fondos controla su utilización monetaria inmediata; no establece una segunda deuda independiente.

- Una misma capacidad de descubierto no puede utilizarse por otro circuito que omita el control de Fondos.

- No administra las tenencias de inversiones ni las obligaciones de depósitos a plazo.

- El registro monetario de cuentas no sustituye la contabilidad general.

- No determina el resultado externo de una transferencia o una entrega de efectivo.

Relaciones:

- Cuentas proporciona condiciones operativas, autorizaciones aplicables y limitaciones de cierre.

- Préstamos y líneas proporciona límites de descubierto autorizados y recibe sus utilizaciones y regularizaciones.

- Restricciones proporciona las medidas monetarias aplicables y recibe confirmaciones de afectación o liberación.

- Pagos, Financiación, Tarjetas, Ahorro e inversiones y Tesorería solicitan efectos monetarios.

- Operaciones de efectivo solicita afectaciones y movimientos vinculados a depósitos y extracciones.

- Contabilidad recibe los hechos monetarios confirmados y sus referencias.

- Cuentas y Atención y operación administrativa consultan posiciones e historial.

Reglas y excepciones:

- La disponibilidad considera saldo, reservas, inmovilizaciones y capacidad de descubierto autorizada, sin contar dos
  veces una misma afectación.

- Una reserva reduce disponibilidad sin generar todavía un débito definitivo.

- Consumir una reserva produce el cargo y elimina el compromiso correspondiente, sin descontar dos veces el importe.

- La liberación de una reserva no genera un crédito contable: restituye disponibilidad.

- Una reserva no se libera automáticamente si existe una ejecución incierta que podría haber consumido los fondos.

- La financiación autorizada puede permitir un saldo contable negativo dentro de su alcance; no permite aceptar
  compromisos superiores a la capacidad disponible resultante.

- Cada cambio de límite de descubierto identifica autorización, versión y momento de efecto.

- Un aumento no está disponible hasta que Fondos confirma su aplicación.

- Una reducción impide nuevos usos que excedan las condiciones resultantes, pero no elimina capital utilizado ni
  compromisos anteriores.

- El vencimiento de la línea impide nuevos compromisos y mantiene identificadas las obligaciones y operaciones
  pendientes.

- Cada utilización o reducción de capital se comunica a Préstamos y líneas con una referencia única; su repetición no
  duplica deuda ni cancelaciones.

- Las diferencias con Préstamos y líneas se investigan y regularizan conservando los antecedentes.

- Antes de afectar fondos se comprueban las condiciones y medidas efectivas aplicables. Su cambio debe quedar coordinado
  con la aceptación y ejecución de movimientos.

- Si no puede determinarse qué condición estaba vigente, la operación permanece pendiente de comprobación.

- Una restricción posterior a una reserva exige determinar si impide su ejecución; reservar fondos no garantiza
  autorización definitiva.

- Una transferencia interna no se confirma con solo uno de sus movimientos realizado.

- La misma instrucción conserva un resultado identificable; repetirla no repite sus efectos.

- Los movimientos confirmados se corrigen mediante nuevos movimientos relacionados con los originales.

- Cada importe conserva su moneda y las fechas operativa y de valor que correspondan.

- Durante el cierre no se aceptan nuevos compromisos incompatibles con la limitación comunicada por Cuentas.

- La confirmación monetaria para cerrar exige resolver saldo, reservas, inmovilizaciones y descubierto utilizado, sin
  dejar operaciones inciertas sin tratamiento.

### 4. Operaciones de efectivo

Descripción: administra los depósitos y las extracciones que implican recepción o entrega física simulada de dinero.

Responsabilidades:

- Registrar solicitudes de depósito y extracción, participantes, cuenta, moneda e importe.

- Comprobar las condiciones de la cuenta, las facultades y los controles aplicables.

- Coordinar la recepción o entrega de efectivo con la caja, sucursal o dispositivo simulado.

- Solicitar reservas para extracciones cuando corresponda.

- Solicitar la acreditación o el débito conforme al resultado comprobado.

- Gestionar entregas parciales, efectivo rechazado y diferencias entre lo declarado y lo recibido.

- Conciliar el resultado físico simulado con los movimientos de cuenta.

- Informar las operaciones pendientes que afecten al cierre.

- Proporcionar los datos del comprobante.

Datos propios: operaciones de depósito y extracción, importes declarados y comprobados, referencias de cajas o
dispositivos, constancias de recepción o entrega, diferencias y resultados de conciliación.

Límites:

- Fondos administra el saldo y ejecuta los movimientos.

- Tesorería administra las existencias y disponibilidades de efectivo del banco, incluidas las asignadas a cajas o
  dispositivos.

- No determina por sí mismo la legitimidad del origen del dinero; solicita los controles correspondientes.

- No administra transferencias ni pagos sin intercambio de efectivo.

- No puede utilizar una autorización anterior para omitir restricciones posteriores aplicables a la entrega.

Relaciones:

- Cuentas y Representación permiten comprobar las facultades de operación.

- Fondos reserva y registra los efectos monetarios.

- Tesorería informa disponibilidad física y registra sus variaciones.

- Riesgo y cumplimiento proporciona controles y restricciones.

- Cuentas comunica limitaciones de cierre y recibe información sobre operaciones pendientes.

- Atención y operación administrativa conserva evidencias y entrega comprobantes.

Reglas y excepciones:

- Un depósito declarado no se considera efectivo recibido.

- Un depósito recibido puede requerir verificación antes de quedar disponible, según su modalidad.

- Una extracción autorizada no equivale a dinero entregado.

- Antes de una entrega se comprueba que continúen cumplidas las condiciones exigibles para ejecutarla.

- Una restricción recibida cuando la entrega ya ocurrió exige tratar el hecho realizado; no permite registrarlo como
  inexistente.

- Una entrega fallida comprobada permite liberar la reserva o corregir el débito correspondiente.

- Una entrega incierta exige investigación antes de devolver fondos o repetir la entrega.

- Las entregas parciales registran el importe efectivamente entregado y tratan por separado la diferencia.

- Toda diferencia conserva su evidencia, responsable de investigación y resolución económica.

- El procedimiento de cierre impide nuevas operaciones incompatibles y conserva el seguimiento de las aceptadas
  previamente.

- No se confirma la ausencia de pendientes mientras exista una recepción o entrega incierta sin tratamiento.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Ciclo de vida y habilitación de una cuenta | Cuentas |
| Titulares y autorizaciones operativas | Cuentas |
| Coordinación del cierre de cuenta | Cuentas |
| Remuneración y comisiones del servicio de cuenta | Cuentas |
| CBU, alias y correspondencia con cuentas | Identificación bancaria |
| Movimientos, reservas y saldos | Fondos |
| Aceptación de compromisos monetarios | Fondos |
| Capacidad de descubierto inmediatamente utilizable | Fondos |
| Concesión, deuda, intereses y cargos de descubierto | Préstamos y líneas |
| Fundamento, alcance y vigencia de medidas restrictivas | Restricciones |
| Aplicación de restricciones operativas sobre la cuenta | Cuentas |
| Ejecución de inmovilizaciones monetarias | Fondos |
| Recepción y entrega de efectivo por operación | Operaciones de efectivo |
| Existencias de efectivo del banco | Tesorería |
| Condiciones contractuales | Contratos |
| Asientos y equilibrio contable del banco | Contabilidad |

Para operar una cuenta, Cuentas determina qué está permitido; Fondos comprueba y ejecuta la afectación monetaria;
Identificación bancaria permite localizarla; Operaciones de efectivo interviene cuando existe recepción o entrega física
simulada de dinero.
