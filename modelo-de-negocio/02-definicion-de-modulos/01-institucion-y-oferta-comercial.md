## Módulo 1. Institución y oferta comercial

Este módulo administra la identidad y organización del banco, los productos que ofrece y las condiciones bajo las cuales
los comercializa.

Se divide en cuatro microservicios.

### 1. Institución

Descripción: representa al banco como institución y mantiene su organización y sus facultades de actuación.

Responsabilidades:

- Administrar la identidad jurídica y comercial del banco.

- Mantener el alcance institucional de las actividades habilitadas.

- Administrar unidades organizativas, sucursales y sus funciones.

- Definir las facultades de decisión, sus límites y las delegaciones entre funciones institucionales.

- Conservar la vigencia e historia de los cambios institucionales.

Datos propios: identificación institucional, denominaciones, domicilios institucionales, actividades habilitadas,
estructura organizativa, sucursales, funciones y delegaciones de facultades.

Límites:

- Las facultades institucionales determinan quién puede aprobar una decisión de negocio; no administran credenciales ni
  permisos técnicos de acceso.

- No administra personas como clientes, representantes de clientes ni contratos.

- No define políticas especializadas de riesgo ni decide operaciones particulares.

- Mantiene el alcance autorizado del banco; Riesgo y cumplimiento determina las restricciones regulatorias aplicables.

Relaciones:

- Catálogo de productos comprueba que sus productos correspondan a actividades habilitadas.

- Los módulos que requieren aprobaciones consultan las facultades institucionales.

- Riesgo y cumplimiento comunica restricciones que afectan el alcance de actuación.

Reglas y excepciones:

- Una delegación debe identificar función otorgante, función receptora, alcance y vigencia.

- No puede conceder facultades superiores a las que posee quien delega.

- Una reorganización conserva las referencias históricas de las decisiones anteriores.

- La baja de una sucursal no elimina sus operaciones ni extingue sus obligaciones pendientes.

### 2. Catálogo de productos

Descripción: administra la definición de los productos y servicios que el banco puede ofrecer.

Responsabilidades:

- Identificar y clasificar productos y variantes.

- Definir características, monedas admitidas y operaciones que permite cada producto.

- Mantener requisitos de elegibilidad y compatibilidad con otros productos.

- Establecer qué condiciones comerciales y contractuales necesita cada producto.

- Administrar versiones y el ciclo de aprobación, habilitación y retiro del catálogo.

Datos propios: identificadores de productos, familias, variantes, características, capacidades operativas, requisitos de
elegibilidad, compatibilidades y versiones de definición.

Límites:

- Define el producto ofrecido; no representa una cuenta, préstamo o tarjeta de un cliente.

- Mantiene los requisitos de elegibilidad; la evaluación de una persona concreta corresponde a la contratación y al
  módulo especializado.

- No mantiene los valores de tasas, comisiones o bonificaciones.

- No administra contratos ni ejecuta operaciones.

Relaciones:

- Institución proporciona el alcance de actividades habilitadas.

- Condiciones comerciales vincula ofertas a versiones concretas de productos.

- Contratación utiliza las definiciones para tramitar solicitudes.

- Los módulos operativos utilizan las características del producto contratado.

- Riesgo y cumplimiento establece requisitos y restricciones que debe respetar el catálogo.

Reglas y excepciones:

- Cada versión debe definir de manera inequívoca sus características y operaciones admitidas.

- No se habilita un producto incompatible con el alcance institucional.

- Retirar un producto impide nuevas contrataciones según la fecha definida; no cancela productos ya contratados.

- Un cambio de características conserva la versión anterior y no se aplica automáticamente a contratos existentes.

### 3. Condiciones comerciales

Descripción: administra las condiciones económicas y comerciales de las ofertas del banco.

Responsabilidades:

- Definir tasas ofrecidas y sus modalidades de aplicación.

- Mantener tarifas, comisiones, bonificaciones y sus requisitos.

- Administrar límites comerciales y condiciones diferenciadas por producto, canal o segmento.

- Identificar la base, periodicidad y convenciones necesarias para interpretar cada condición.

- Establecer vigencias, compatibilidades y prioridades entre condiciones.

- Administrar la aprobación, publicación, modificación y retiro de ofertas.

Datos propios: ofertas comerciales, tasas, tarifas, bonificaciones, límites comerciales, criterios de aplicación,
convenciones de cálculo, aprobaciones y versiones vigentes e históricas.

Límites:

- Define condiciones ofrecidas; Contratación conserva las efectivamente pactadas.

- No calcula intereses devengados ni cargos de operaciones individuales: los calcula el módulo responsable del producto.

- Los límites comerciales no sustituyen los límites de riesgo, las restricciones regulatorias ni la disponibilidad de
  fondos.

- No administra alícuotas ni criterios tributarios, que corresponden a Riesgo y cumplimiento.

- No registra cobros, pagos ni asientos contables.

Relaciones:

- Catálogo de productos identifica el producto y la versión a los que pertenece cada oferta.

- Contratación selecciona y conserva las condiciones aceptadas.

- Los módulos de productos utilizan las condiciones contractuales y las referencias comerciales que correspondan.

- Calendarios bancarios proporciona las referencias temporales.

- Riesgo y cumplimiento aporta restricciones sobre tasas, cargos y límites.

Reglas y excepciones:

- Toda condición publicada debe tener alcance, moneda cuando corresponda y vigencia identificables.

- Una combinación de condiciones debe producir un resultado inequívoco; las superposiciones requieren una prioridad
  expresa.

- Una bonificación debe indicar requisitos, duración y consecuencias de dejar de cumplirlos.

- Las condiciones variables deben identificar el índice o referencia y la regla de actualización.

- Corregir o retirar una oferta conserva su historia. El tratamiento de contratos afectados se realiza mediante
  Contratación y el módulo del producto.

### 4. Calendarios bancarios

Descripción: administra las fechas y convenciones temporales comunes que utiliza el banco.

Responsabilidades:

- Mantener calendarios por jurisdicción y finalidad operativa.

- Identificar días hábiles, inhábiles, feriados y jornadas especiales.

- Administrar zonas horarias y convenciones generales para determinar fechas.

- Mantener reglas de ajuste de fechas, como trasladar al día hábil siguiente o anterior.

- Proporcionar versiones de calendarios para reproducir determinaciones históricas.

Datos propios: calendarios, fechas especiales, jurisdicciones, zonas horarias, convenciones de ajuste y versiones.

Límites:

- Determina cómo interpretar una fecha conforme al calendario solicitado.

- No decide qué convención corresponde a un contrato: esa elección pertenece a las condiciones del producto y al acuerdo
  contractual.

- No administra vencimientos particulares, cierres de tarjeta ni cuotas de préstamos.

- No ejecuta tareas programadas ni realiza cierres operativos o contables.

Relaciones:

- Condiciones comerciales y Contratación referencian calendarios y convenciones.

- Los módulos operativos los utilizan para determinar vencimientos, cortes y fechas de liquidación.

- Contabilidad los consulta para organizar sus períodos y cierres.

Reglas y excepciones:

- Toda consulta debe identificar el calendario y la fecha aplicables.

- La ausencia de un calendario necesario debe informarse; no se presume silenciosamente que el día es hábil.

- Una modificación extraordinaria conserva la versión anterior y permite identificar las operaciones potencialmente
  afectadas.

- Los módulos responsables deciden el tratamiento de obligaciones ya constituidas conforme a sus contratos y reglas.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Identidad, organización y facultades del banco | Institución |
| Definición y capacidades de un producto | Catálogo de productos |
| Condiciones económicas de una oferta | Condiciones comerciales |
| Calendarios y convenciones temporales comunes | Calendarios bancarios |
| Condiciones aceptadas por un cliente | Módulo de Contratación |
| Aplicación de condiciones y cálculo de una operación | Módulo responsable del producto |
| Restricciones regulatorias y políticas de riesgo | Módulo de Riesgo y cumplimiento |
