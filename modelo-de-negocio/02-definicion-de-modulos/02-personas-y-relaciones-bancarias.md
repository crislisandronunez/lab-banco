## Módulo 2. Personas y relaciones bancarias

Este módulo administra quiénes son los participantes, cómo se relacionan entre sí y qué relación mantienen con el banco.

Se divide en cuatro microservicios: Personas, Vinculación, Clientes y Representación.

### 1. Personas

Descripción: representa a las personas humanas y jurídicas que participan del negocio bancario, independientemente de
que sean clientes.

Responsabilidades:

- Registrar e identificar personas humanas y jurídicas.

- Mantener identificadores, denominaciones, domicilios, contactos y actividad declarada.

- Administrar relaciones familiares, societarias y de participación relevantes para el negocio.

- Registrar la composición de personas jurídicas y las declaraciones sobre beneficiarios finales.

- Mantener circunstancias personales o jurídicas relevantes, como minoría de edad, fallecimiento, disolución o sucesión.

- Detectar posibles registros duplicados y administrar su resolución conservando las referencias históricas.

- Conservar el origen y la historia de las modificaciones de datos.

Datos propios: identidad declarada, identificadores, datos personales o constitutivos, domicilios, contactos,
actividades, relaciones entre personas y circunstancias personales o jurídicas.

Límites:

- Registrar una persona no significa verificar su identidad ni admitirla como cliente.

- Conserva la composición societaria y los beneficiarios finales declarados; Riesgo y cumplimiento realiza las
  verificaciones y determinaciones correspondientes.

- Una relación familiar o societaria no concede facultades de representación.

- No administra credenciales de acceso, contratos, cuentas ni titularidades de productos.

Relaciones:

- Vinculación utiliza sus registros para identificar al solicitante.

- Clientes referencia a la persona que mantiene la relación comercial.

- Representación utiliza sus identidades para identificar representados y representantes.

- Riesgo y cumplimiento consulta sus datos y comunica resultados de verificación, sin reemplazar la información
  declarada.

- Atención y operación administrativa conserva los documentos que respaldan los datos.

Reglas y excepciones:

- Una persona conserva su identidad de referencia aunque cambie su nombre, domicilio o documentación.

- Un identificador contradictorio requiere revisión antes de asociar registros.

- La resolución de duplicados conserva el vínculo con los registros anteriores y sus operaciones.

- El fallecimiento o la disolución se comunica a los responsables de las relaciones y productos afectados; no elimina
  sus obligaciones.

### 2. Vinculación

Descripción: administra el proceso mediante el cual una persona solicita establecer una relación comercial con el banco.

Responsabilidades:

- Recibir solicitudes de vinculación e identificar al solicitante.

- Determinar los requisitos de admisión aplicables según el tipo de persona y relación solicitada.

- Coordinar la obtención de datos y documentación.

- Solicitar verificaciones de identidad, actividad, representación y cumplimiento.

- Gestionar observaciones, subsanaciones y revisiones adicionales.

- Resolver la admisión o el rechazo dentro de las facultades institucionales.

- Registrar el fundamento de la decisión y comunicar el resultado.

- Entregar a Clientes una admisión aceptada para constituir la relación comercial.

Datos propios: solicitudes de vinculación, requisitos exigidos, referencias documentales, verificaciones solicitadas,
resultados recibidos, observaciones y decisiones de admisión.

Límites:

- Coordina la admisión; las verificaciones especializadas pertenecen a Riesgo y cumplimiento.

- No modifica por sí mismo la identidad de Personas ni concede representaciones.

- No contrata productos, abre cuentas ni evalúa financiación.

- La aceptación de la vinculación no sustituye las condiciones particulares necesarias para contratar cada producto.

Relaciones:

- Personas proporciona la identidad y los datos del solicitante.

- Representación determina las facultades de quien actúa por otra persona.

- Riesgo y cumplimiento proporciona verificaciones y restricciones.

- Institución proporciona las facultades necesarias para aprobar la admisión.

- Clientes recibe la decisión aceptada.

- Atención y operación administrativa gestiona documentación, tareas humanas y comunicaciones.

Reglas y excepciones:

- No se acepta una vinculación con requisitos obligatorios pendientes o impedimentos aplicables.

- La falta de respuesta de una verificación deja la solicitud pendiente; no equivale a aprobación ni rechazo.

- Un cambio relevante de datos antes de resolver exige revisar las verificaciones afectadas.

- El rechazo conserva su fundamento y antecedentes.

- Repetir la comunicación de una admisión aceptada no puede crear relaciones comerciales duplicadas.

- Una persona que ya es cliente se identifica como tal y continúa por el trámite correspondiente a su solicitud.

### 3. Clientes

Descripción: administra la relación comercial que una persona mantiene con el banco después de su admisión.

Responsabilidades:

- Constituir la relación de cliente a partir de una admisión aceptada.

- Mantener su identificación comercial y fecha de vinculación.

- Administrar segmentos y clasificaciones comerciales, conservando su vigencia.

- Coordinar actualizaciones y revisiones periódicas de la relación.

- Registrar medidas que afecten la continuidad o el alcance de la relación comercial.

- Tramitar su desvinculación, comprobando productos y obligaciones pendientes.

- Conservar la historia de la relación después de su terminación.

Datos propios: identificación de cliente, vínculo con la persona, antecedentes de admisión, clasificación comercial,
revisiones y decisiones sobre continuidad o desvinculación.

Límites:

- Personas mantiene la identidad; Clientes mantiene la relación comercial.

- El segmento comercial no sustituye el perfil de riesgo.

- No administra los contratos, saldos ni deudas del cliente.

- Las restricciones de cumplimiento pertenecen a Riesgo y cumplimiento; Clientes registra su repercusión sobre la
  relación.

- Ser cliente no concede autorización para operar cualquier producto.

Relaciones:

- Vinculación proporciona la admisión.

- Personas comunica modificaciones relevantes.

- Contratación comprueba la relación comercial para nuevas contrataciones.

- Los módulos de productos informan relaciones y obligaciones que condicionan la desvinculación.

- Riesgo y cumplimiento solicita revisiones y comunica medidas.

- Atención y operación administrativa recibe trámites y comunica decisiones.

Reglas y excepciones:

- Una persona mantiene una identificación de cliente estable; contratar más productos no crea nuevos clientes.

- La revisión periódica no borra la admisión ni la historia anterior.

- Una actualización pendiente produce únicamente las consecuencias previstas por las reglas aplicables.

- La desvinculación distingue el cese de nuevas contrataciones del cierre de relaciones existentes.

- No se considera terminada la relación sin resolver o dar tratamiento expreso a los productos y obligaciones
  pendientes.

- Una nueva vinculación posterior conserva los antecedentes de la relación anterior.

### 4. Representación

Descripción: administra las facultades mediante las cuales una persona puede actuar en nombre de otra.

Responsabilidades:

- Registrar poderes, representaciones legales y designaciones de autoridades.

- Identificar representados, representantes y fundamento de la representación.

- Determinar actos permitidos, productos alcanzados, límites y condiciones.

- Administrar actuación individual o conjunta.

- Registrar vigencias, vencimientos, revocaciones y sustituciones.

- Evaluar si una representación habilita un acto concreto en una fecha determinada.

- Conservar las versiones y referencias documentales que justifican cada facultad.

Datos propios: relaciones de representación, fundamento, alcance, modalidades de actuación, límites, vigencias,
revocaciones y referencias a sus evidencias.

Límites:

- Una representación no convierte al representante en cliente, titular ni propietario de fondos.

- No administra facultades internas de empleados del banco: corresponden a Institución.

- Las autorizaciones operativas específicas de una cuenta pertenecen a Cuentas y fondos.

- No habilita operaciones por sí sola: también deben cumplirse las condiciones del contrato, producto y controles
  aplicables.

- No administra permisos técnicos de acceso.

Relaciones:

- Personas identifica a quienes intervienen y sus circunstancias.

- Vinculación y Contratación comprueban facultades para presentar solicitudes y aceptar acuerdos.

- Los módulos operativos consultan el alcance de la representación cuando corresponde.

- Riesgo y cumplimiento aporta verificaciones y restricciones.

- Atención y operación administrativa conserva la documentación respaldatoria.

Reglas y excepciones:

- Toda representación debe tener fundamento y alcance identificables.

- Una facultad no documentada o ambigua requiere revisión antes de autorizar el acto.

- La actuación conjunta exige la participación de los representantes necesarios.

- Una representación vencida o revocada no habilita nuevos actos.

- La revocación no invalida automáticamente actos válidamente realizados con anterioridad.

- Las sustituciones o delegaciones solo se admiten cuando el fundamento de la representación las permite.

- El fallecimiento, la disolución u otra circunstancia relevante exige determinar sus consecuencias sobre la
  representación.

### Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Identidad y datos de una persona | Personas |
| Relaciones familiares, societarias y participación declarada | Personas |
| Solicitud y decisión de admisión al banco | Vinculación |
| Relación comercial y clasificación del cliente | Clientes |
| Facultades para actuar por otra persona | Representación |
| Verificación de identidad y determinación de beneficiarios finales | Riesgo y cumplimiento |
| Condiciones y participantes de un contrato | Contratación |
| Titularidad y autorizaciones operativas de una cuenta | Cuentas y fondos |
| Facultades internas de decisión del banco | Institución |

El recorrido de ingreso queda establecido: Personas identifica al solicitante; Representación determina las facultades
cuando interviene un tercero; Vinculación coordina los requisitos y resuelve la admisión; Clientes constituye y mantiene
la relación comercial.
