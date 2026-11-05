## Módulo 3. Contratación

Este módulo administra las solicitudes de productos y los acuerdos que establecen derechos y obligaciones entre el banco
y sus clientes.

Se divide en dos microservicios: Solicitudes de productos y Contratos.

### 1. Solicitudes de productos

Descripción: administra el trámite mediante el cual un cliente solicita contratar, modificar, renovar o terminar un
producto o servicio.

Responsabilidades:

- Registrar la petición, sus participantes, el producto solicitado y el canal de origen.

- Identificar la versión del producto y la oferta comercial considerada.

- Determinar los requisitos del trámite y reunir sus antecedentes.

- Comprobar la relación de cliente y las facultades de quienes intervienen.

- Solicitar evaluaciones y decisiones a los módulos especializados.

- Gestionar documentación pendiente, observaciones y subsanaciones.

- Consolidar el resultado de las evaluaciones y resolver la viabilidad de la contratación dentro de las facultades
  correspondientes.

- Coordinar la preparación del acuerdo en Contratos.

- Registrar el resultado contractual y la habilitación posterior informada por el módulo del producto.

- Administrar el retiro de la solicitud, su vencimiento y su rechazo.

Datos propios: solicitudes, finalidad del trámite, participantes solicitantes, referencias al producto y oferta,
requisitos, antecedentes recibidos, evaluaciones solicitadas, resultados, observaciones, decisiones y referencias a
contratos y productos resultantes.

Límites:

- Vinculación administra el ingreso al banco; este servicio administra solicitudes de productos.

- No decide el riesgo crediticio, verifica identidades ni determina el valor de garantías: solicita esas decisiones a
  sus responsables.

- No conserva el acuerdo contractual definitivo ni registra su aceptación.

- No abre cuentas, emite tarjetas, constituye depósitos ni desembolsa préstamos.

- La solicitud es el trámite de contratación; las solicitudes operativas posteriores pertenecen al módulo del producto.

Relaciones:

- Catálogo de productos y Condiciones comerciales proporcionan las definiciones de la oferta.

- Clientes informa la relación comercial.

- Representación determina las facultades de quienes actúan por terceros.

- Riesgo y cumplimiento proporciona verificaciones y restricciones.

- Los módulos de productos realizan sus evaluaciones especializadas y ejecutan la habilitación operativa.

- Contratos administra el acuerdo resultante.

- Atención y operación administrativa aporta documentos, tareas humanas y comunicaciones.

- Institución proporciona las facultades de aprobación.

Reglas y excepciones:

- Cada solicitud identifica qué producto y qué actuación se requieren.

- La aprobación especializada conserva su responsable, fundamento, condiciones y vigencia.

- No se presenta como viable una contratación con requisitos obligatorios pendientes o decisiones incompatibles.

- Un cambio de participantes, producto o condiciones exige revisar las evaluaciones afectadas.

- Una evaluación vencida debe renovarse antes de utilizarse cuando su vigencia sea exigible.

- La falta de respuesta mantiene pendiente el trámite; no equivale a aprobación.

- Repetir una petición identificada como la misma solicitud no crea contratos o productos duplicados.

- Retirar una solicitud después de celebrado el contrato requiere aplicar el procedimiento contractual correspondiente.

- Una falla de habilitación posterior a la contratación se deriva al módulo del producto y conserva el vínculo con el
  contrato; no se transforma automáticamente en rechazo de la solicitud.

### 2. Contratos

Descripción: administra el contenido y la vigencia de los acuerdos celebrados entre el banco y sus contrapartes para
contratar productos y servicios.

Responsabilidades:

- Mantener modelos contractuales y sus versiones aprobadas.

- Preparar acuerdos con las condiciones particulares autorizadas para cada contratación.

- Identificar participantes, roles contractuales y facultades exigidas.

- Establecer derechos, obligaciones, moneda, duración y condiciones de entrada en vigencia.

- Incorporar tasas, cargos, límites y mecanismos de actualización acordados.

- Registrar la información entregada antes de contratar y las aceptaciones requeridas.

- Comprobar que la aceptación corresponda al contenido exacto del acuerdo.

- Determinar cuándo el contrato está celebrado y cuándo produce efectos.

- Administrar modificaciones, renovaciones, desistimientos y terminaciones.

- Conservar la historia contractual y las obligaciones que sobrevivan a su terminación.

Datos propios: modelos contractuales, acuerdos particulares, participantes y roles contractuales, condiciones pactadas,
versiones, consentimientos, referencias de firma, fechas de celebración y efecto, modificaciones y antecedentes de
terminación.

Límites:

- Condiciones comerciales administra la oferta; Contratos conserva lo efectivamente acordado.

- La evidencia documental se conserva en Atención y operación administrativa; Contratos determina a qué acuerdo y
  versión corresponde.

- Representación administra las facultades de representación; Contratos conserva la referencia utilizada para justificar
  la intervención.

- No mantiene saldos, consumos, cuotas, tenencias ni movimientos de productos.

- No determina que una deuda fue pagada: recibe esa información del módulo responsable.

- La celebración del contrato no sustituye los requisitos operativos para habilitar el producto.

Relaciones:

- Solicitudes de productos proporciona el trámite, sus participantes y las decisiones que habilitan el acuerdo.

- Catálogo de productos identifica la definición contratada.

- Condiciones comerciales proporciona las condiciones ofrecidas que se incorporan al contrato.

- Personas, Clientes y Representación permiten identificar a los participantes y sus facultades.

- Riesgo y cumplimiento aporta requisitos y restricciones aplicables.

- Los módulos de productos reciben las condiciones pactadas e informan las obligaciones relevantes para modificar o
  terminar el acuerdo.

- Atención y operación administrativa conserva evidencias y entrega comunicaciones.

Reglas y excepciones:

- Toda aceptación debe quedar vinculada a una versión precisa del acuerdo.

- Cambiar el contenido antes de completar las aceptaciones exige obtener las conformidades necesarias sobre la nueva
  versión.

- Un contrato no se considera celebrado mientras falten aceptaciones obligatorias.

- La fecha de celebración y la fecha de entrada en vigencia se distinguen cuando corresponda.

- Una modificación de la oferta no cambia por sí sola un contrato existente.

- Las condiciones variables deben conservar la referencia y el mecanismo de actualización acordados.

- Las modificaciones requieren el fundamento, consentimiento o comunicación que corresponda a su naturaleza.

- Una falla al entregar la confirmación no implica que el contrato no se haya celebrado; debe comprobarse el resultado y
  completarse la comunicación.

- La terminación contractual no elimina deudas, reclamos, garantías u otras obligaciones que deban subsistir.

- Una corrección conserva el acuerdo anterior y documenta su alcance y fecha de efecto.

### Distribución de autoridad

| Información o decisión                                | Responsable                                                               |
|-------------------------------------------------------|---------------------------------------------------------------------------|
| Trámite de solicitud de un producto                   | Solicitudes de productos                                                  |
| Requisitos y seguimiento de la solicitud              | Solicitudes de productos                                                  |
| Evaluación especializada y decisión sobre el producto | Módulo responsable del producto                                           |
| Modelos y contenido contractual                       | Contratos                                                                 |
| Condiciones aceptadas y evidencia de su aceptación    | Contratos, con respaldo documental en Atención y operación administrativa |
| Facultades de representación                          | Representación                                                            |
| Condiciones de la oferta comercial                    | Condiciones comerciales                                                   |
| Habilitación y funcionamiento del producto            | Módulo responsable del producto                                           |
| Saldo, deuda u obligación económica pendiente         | Módulo que administra esa posición                                        |

El recorrido queda establecido: Solicitudes de productos coordina los requisitos y decisiones; Contratos formaliza el
acuerdo; el módulo responsable habilita y administra el producto bajo las condiciones pactadas.
