## Módulo 9. Contabilidad

Este módulo administra la representación contable del patrimonio, las obligaciones y los resultados del banco,
conservando la correspondencia entre los hechos económicos, sus asientos y la información financiera resultante.

Se divide en tres microservicios: Registración contable, Cierres contables e Información contable.

### 1. Registración contable

Descripción: transforma los hechos económicos del banco en registros contables equilibrados, identificables y
permanentes.

Responsabilidades:

- Administrar el plan de cuentas, sus clasificaciones y versiones.

- Mantener las reglas que relacionan hechos económicos con cuentas y conceptos contables.

- Recibir hechos de los módulos operativos y comprobar su integridad.

- Registrar asientos con sus débitos, créditos, monedas y fechas.

- Contabilizar capital, intereses, comisiones, tributos, previsiones, diferencias de cambio y otros conceptos.

- Mantener registros patrimoniales y cuentas de orden según la naturaleza del hecho.

- Conservar la relación entre operación, hecho económico y asiento.

- Registrar reversiones, reclasificaciones y ajustes autorizados.

- Proporcionar los registros necesarios para conciliaciones, cierres y reportes.

Datos propios: plan de cuentas, reglas de contabilización, hechos recibidos, asientos, partidas, referencias de origen,
versiones de reglas aplicadas y antecedentes de corrección.

Límites:

- Los módulos de productos determinan sus operaciones, obligaciones y cálculos económicos.

- Registración contable determina cómo representar esos hechos en la contabilidad.

- No administra saldos disponibles de clientes, deuda operativa, tenencias ni liquidez.

- Cierres contables administra los períodos y las condiciones para admitir registraciones.

- Información contable elabora las presentaciones financieras.

Relaciones:

- Los módulos operativos proporcionan hechos económicos y sus importes desglosados.

- Tesorería proporciona operaciones propias y referencias de cotización para conversiones contables.

- Cierres contables informa períodos habilitados y solicita ajustes.

- Información contable consulta los registros confirmados.

- Atención y operación administrativa conserva documentación justificativa y aprobaciones humanas.

Reglas y excepciones:

- Todo asiento debe cumplir el equilibrio entre débitos y créditos conforme al esquema contable aplicable.

- Una operación en varias monedas conserva sus importes originales y la conversión utilizada para su representación
  contable.

- Una misma ocurrencia económica no puede contabilizarse dos veces por repetición del mensaje.

- Una operación puede originar varios hechos contables distintos; cada uno debe identificarse por separado.

- Un hecho incompleto, contradictorio o sin regla aplicable queda pendiente de resolución y no se registra mediante una
  cuenta arbitraria.

- Un rechazo contable no revierte automáticamente la operación de negocio que lo originó.

- Los asientos confirmados no se borran ni sobrescriben; se corrigen mediante registros vinculados.

- La fecha económica del hecho, la fecha de recepción y la fecha de contabilización se conservan cuando difieren.

- Las registraciones manuales requieren fundamento y aprobación según las facultades institucionales.

### 2. Cierres contables

Descripción: administra los períodos contables y los controles necesarios para cerrar la actividad de una fecha o
período.

Responsabilidades:

- Abrir períodos y establecer sus fechas y condiciones de registración.

- Coordinar cierres diarios, mensuales y de ejercicio.

- Comprobar que los hechos económicos esperados hayan sido recibidos y contabilizados.

- Conciliar registros auxiliares de productos, fondos, custodia y tesorería con la contabilidad general.

- Identificar diferencias, omisiones y registraciones pendientes.

- Solicitar a los módulos responsables los devengamientos, previsiones, valuaciones y regularizaciones necesarios.

- Determinar ajustes específicamente contables, incluidas reclasificaciones y resultados de cierre.

- Coordinar la aprobación de diferencias y ajustes dentro de las facultades aplicables.

- Confirmar el cierre y administrar reaperturas excepcionales.

- Conservar los controles, resultados y aprobaciones que fundamentan cada cierre.

Datos propios: períodos, fechas de corte, ejecuciones de cierre, controles, conciliaciones contables, diferencias,
ajustes solicitados, aprobaciones y antecedentes de reapertura.

Límites:

- No calcula nuevamente los intereses, cuotas o cargos que administra cada producto.

- Riesgo y cumplimiento establece los criterios de previsión; los módulos responsables determinan los importes de sus
  carteras.

- Registración contable realiza los asientos, incluidos los ajustes de cierre.

- La conciliación contable no reemplaza las conciliaciones de pagos, redes, custodia o disponibilidades externas.

- Un cierre contable no cancela contratos ni cierra cuentas de clientes.

Relaciones:

- Registración contable proporciona asientos y pendientes de registración.

- Los módulos operativos aportan posiciones y totales de control correspondientes a la misma fecha de corte.

- Tesorería aporta disponibilidades, posiciones y conciliaciones propias.

- Riesgo y cumplimiento proporciona criterios y recibe incidencias relevantes.

- Institución determina las facultades para aprobar cierres, excepciones y reaperturas.

- Información contable utiliza los períodos y cortes confirmados.

Reglas y excepciones:

- Cada cierre identifica qué fecha, operaciones y registros comprende.

- Las comparaciones utilizan cortes equivalentes; una diferencia temporal no se trata automáticamente como pérdida.

- El cierre exige completar sus controles obligatorios y resolver las diferencias que impidan aprobarlo.

- Las diferencias admitidas deben conservar fundamento, responsable, tratamiento y plazo de regularización.

- Una ejecución fallida puede retomarse sin repetir ajustes ya contabilizados.

- Los hechos recibidos después del corte se tratan mediante ajuste posterior o reapertura autorizada, conservando su
  fecha económica.

- Un período cerrado impide nuevas registraciones ordinarias sobre él.

- Una reapertura conserva el cierre anterior y requiere motivo, alcance y aprobación.

- Los resultados de ejercicio se determinan sin duplicar ingresos o gastos ya reconocidos.

### 3. Información contable

Descripción: elabora información financiera a partir de los registros contables y de los cortes definidos para su
presentación.

Responsabilidades:

- Mantener estructuras y criterios de presentación de informes contables.

- Elaborar mayores, balances de sumas y saldos y estados de situación patrimonial y resultados.

- Presentar movimientos y saldos por cuenta, período, moneda y otras dimensiones contables.

- Distinguir información de períodos abiertos de información correspondiente a cierres confirmados.

- Preparar comparaciones entre períodos con criterios identificables.

- Conservar informes emitidos, sus versiones y el corte de datos utilizado.

- Proporcionar información contable para gestión institucional y elaboración de reportes regulatorios.

- Permitir justificar los importes presentados mediante sus cuentas y asientos de origen.

Datos propios: estructuras de presentación, correspondencias con cuentas contables, definiciones de informes,
ejecuciones, cortes utilizados e informes emitidos.

Límites:

- No crea ni corrige asientos.

- No modifica los períodos ni aprueba cierres.

- Sus cálculos de presentación no sustituyen los saldos registrados.

- Riesgo y cumplimiento prepara las declaraciones regulatorias, incorporando la información contable necesaria.

- Los extractos de clientes y resúmenes de tarjetas pertenecen a sus respectivos responsables.

Relaciones:

- Registración contable proporciona partidas y saldos.

- Cierres contables identifica los períodos y cortes confirmados.

- Institución y Tesorería utilizan la información para gestión.

- Riesgo y cumplimiento utiliza los datos en reportes y controles.

- Atención y operación administrativa conserva y entrega los documentos emitidos cuando corresponde.

Reglas y excepciones:

- Todo informe identifica período, moneda de presentación, fecha de corte y criterios utilizados.

- Los importes deben poder reconstruirse desde los registros que los originaron.

- Las agregaciones monetarias requieren una moneda común y una conversión identificada.

- Una corrección de registros que afecte un informe emitido exige una nueva versión cuando corresponda; la anterior se
  conserva.

- La falta de datos o una conciliación pendiente se informa expresamente.

- Un informe basado en un período abierto no se presenta como resultado de un cierre confirmado.

- Los cambios de presentación conservan las correspondencias necesarias para interpretar comparaciones históricas.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Hecho económico e importe operativo | Módulo que origina la operación |
| Plan de cuentas y reglas de contabilización | Registración contable |
| Asientos, partidas y ajustes registrados | Registración contable |
| Períodos y condiciones de registración | Cierres contables |
| Conciliación entre auxiliares y contabilidad general | Cierres contables |
| Aprobación del cierre y gestión de reaperturas | Cierres contables, según facultades institucionales |
| Estructuras de presentación e informes contables | Información contable |
| Criterios de riesgo y previsión | Riesgo y cumplimiento |
| Declaraciones regulatorias | Riesgo y cumplimiento |

El circuito queda establecido: los módulos operativos generan los hechos económicos; Registración contable los
contabiliza; Cierres contables comprueba su integridad y correspondencia; Información contable presenta los resultados.
