# Contratos de integración entre servicios

## 1. Propósito

Este documento establece cómo se especifican los intercambios entre los servicios del banco.

El modelo de negocio define las actividades. La definición de módulos distribuye responsabilidades. Las operaciones
funcionales describen el comportamiento. Los contratos de integración determinan qué información intercambian los
servicios y qué compromisos asume cada participante.

Cada contrato permite conocer:

- Quién ofrece una capacidad o comunica un hecho.

- Quién lo utiliza y para qué.

- Qué información necesita.

- Qué comprobaciones realiza.

- Qué resultado garantiza.

- Cómo se tratan repeticiones, concurrencia y resultados inciertos.

- Qué efectos pertenecen a cada servicio.

- Cómo se recupera una actuación interrumpida.

Los contratos respetan las autoridades y reglas funcionales existentes.

## 2. Organización documental

La documentación se organiza en un documento principal y doce documentos de contratos.

| Módulo | Servicios comprendidos |
| --- | --- |
| 1. Institución y oferta comercial | Institución; Catálogo de productos; Condiciones comerciales; Calendarios bancarios |
| 2. Personas y relaciones bancarias | Personas; Vinculación; Clientes; Representación |
| 3. Contratación | Solicitudes de productos; Contratos |
| 4. Cuentas y fondos | Cuentas; Identificación bancaria; Fondos; Operaciones de efectivo |
| 5. Pagos y transferencias | Transferencias; Pagos; Mandatos y recurrencias; Cheques y ECHEQ; Compensación y conciliación de pagos |
| 6. Financiación | Evaluación crediticia; Préstamos y líneas; Garantías; Cobranzas y recuperaciones |
| 7. Tarjetas | Emisión de tarjetas; Autorizaciones; Procesamiento de consumos; Cuenta de tarjeta; Disputas de tarjetas |
| 8. Ahorro e inversiones | Depósitos a plazo; Instrumentos de inversión; Órdenes de inversión; Tenencias y valuación |
| 9. Contabilidad | Registración contable; Cierres contables; Información contable |
| 10. Tesorería | Posiciones y liquidez; Operaciones de tesorería; Cambio de moneda; Liquidaciones de tesorería |
| 11. Riesgo y cumplimiento | Riesgos; Conocimiento del cliente; Prevención de lavado; Prevención de fraude; Restricciones; Criterios tributarios; Información regulatoria |
| 12. Atención y operación administrativa | Reclamos; Gestión administrativa; Documentos y comprobantes; Comunicaciones; Extractos |

Cada documento contiene alcance, catálogo, desarrollo de contratos, correspondencia funcional y dependencias
compartidas.

Los contratos ofrecidos se documentan en el módulo del responsable. Los consumidores los referencian sin reproducir su
definición.

Las referencias utilizan nombres e identificadores, sin depender de rutas de archivos.

## 3. Unidad de contrato

Un contrato representa una capacidad de intercambio con propósito y resultado propios.

No se crea necesariamente un contrato por operación funcional ni por consumidor.

Se separan capacidades cuando cambia:

- Propósito.

- Autoridad.

- Información necesaria.

- Resultado garantizado.

- Efecto.

- Autorización.

- Recuperación.

Consultar una reserva, constituirla y consumirla son capacidades diferentes.

Una respuesta ordinaria forma parte del contrato que la origina.

Los intercambios entre servicios del mismo módulo también se documentan.

Una capacidad funcional puede requerir varias operaciones técnicas, como iniciar, consultar y resolver. Cada operación
debe quedar identificada, aunque pertenezca al mismo contrato funcional.

Preparar una condición es una actuación explícita. No se incorpora silenciosamente como efecto de una consulta.

## 4. Clases de intercambio

| Clase | Finalidad |
| --- | --- |
| Consulta | Obtener información o una evaluación informativa |
| Solicitud de actuación | Pedir una decisión o efecto persistente al responsable |
| Evento | Comunicar un hecho confirmado |

### 4.1. Consultas

Una consulta identifica objeto, alcance y momento de referencia.

Distingue información completa, parcial y no disponible.

No crea reservas, preparaciones ni compromisos por mostrar disponibilidad o suficiencia.

Una evaluación favorable no constituye un permiso permanente.

Registrar evidencia de lo consultado no convierte la consulta en una preparación protegida frente a cambios
concurrentes.

### 4.2. Solicitudes de actuación

Una solicitud tiene identidad estable, contenido definido y resultado recuperable.

Distingue recepción, aceptación durable y ejecución.

Las preparaciones, solicitudes de aborto y notificaciones de decisión pertenecen a esta clase cuando producen registros
persistentes.

El solicitante no impone un resultado favorable al proveedor.

### 4.3. Eventos

Un evento identifica hecho, emisor, objeto, versión y momento.

Se relaciona con la operación y actuación de origen.

Un consumidor puede iniciar actuaciones a partir del evento, conservando responsabilidad sobre sus comprobaciones y
efectos.

La recepción del evento no demuestra la conclusión de todos los procesos derivados.

## 5. Identificación y catálogo

Los contratos funcionales utilizan identificadores estables con la forma CI-MM-NNN.

| Campo | Contenido |
| --- | --- |
| Identificador | Referencia estable |
| Nombre | Capacidad o hecho concreto |
| Clase | Consulta, actuación o evento |
| Responsable | Proveedor o emisor |
| Consumidores | Servicios y finalidades autorizadas |
| Operaciones relacionadas | Correspondencia funcional |
| Versión | Definición aplicable |
| Operaciones técnicas | Intercambios necesarios para cumplirlo |

Un contrato retirado conserva su identidad.

Agregar consumidores no aumenta la cantidad de contratos si utilizan la misma capacidad sin cambiar su significado.

Las operaciones técnicas del protocolo común se identifican en el contrato correspondiente y en su especificación
técnica. Su existencia no se oculta dentro de una consulta ni obliga a renumerar los contratos funcionales existentes.

El catálogo distingue la cantidad de contratos funcionales de la cantidad de operaciones técnicas.

## 6. Contenido de cada contrato

### 6.1. Identificación y propósito

Cada contrato define identificador, nombre, clase, versión, responsable, necesidad, resultado y operaciones
relacionadas.

Solicitar una acreditación y comunicar una acreditación confirmada son intercambios diferentes.

### 6.2. Participantes y autoridad

Determina:

- Quién inicia o consume.

- Con qué finalidad.

- Quién decide.

- Quién ejecuta cada efecto.

- Quién coordina.

- Qué intervenciones pertenecen a otros contratos.

El proveedor conserva autoridad sobre sus datos. El consumidor conserva responsabilidad sobre el proceso que utiliza esa
capacidad.

### 6.3. Entrada

Cada dato identifica nombre, significado, obligatoriedad, condición, origen y validación.

Cuando basta una referencia, no se exige copiar todo el objeto.

Los datos necesarios no se describen mediante expresiones generales como «datos del cliente» sin precisar atributos y
finalidad.

### 6.4. Condiciones

El contrato especifica:

- Requisitos previos.

- Facultades.

- Restricciones.

- Versiones y vigencias.

- Recursos necesarios.

- Condiciones informativas.

- Condiciones que requieren preparación.

- Momento de comprobación.

- Tratamiento de cambios concurrentes.

Una referencia de autorización no demuestra por sí sola que corresponda al contenido solicitado.

### 6.5. Resultado satisfactorio

Define qué quedó confirmado, qué efectos ocurrieron, qué recursos permanecen comprometidos y qué sigue pendiente.

Una preparación favorable no demuestra ejecución.

Una reserva no demuestra envío.

Una instrucción externa aceptada no demuestra liquidación.

### 6.6. Alternativas y errores

Cada resultado identifica causa, efectos realizados, efectos impedidos, responsable, información comunicable y
condiciones de recuperación.

Incluye el tratamiento de recursos, preparaciones y obligaciones.

### 6.7. Repetición y recuperación

Define identidad, unicidad, contenido vinculante, consultas de resultado, concurrencia, cancelaciones y respuestas
tardías.

### 6.8. Aceptación

Incluye situaciones comprobables de éxito, insuficiencia, impedimentos, duplicados, conflictos, concurrencia, pérdida de
respuesta y corrección.

Cuando participa en el protocolo común, incluye competencia entre confirmación y aborto.

## 7. Referencias e información común

Los intercambios distinguen:

| Referencia | Finalidad |
| --- | --- |
| Operación | Proceso de negocio |
| Actuación | Decisión o efecto solicitado |
| Intento | Instancia concreta de confirmación |
| Preparación | Condición mantenida por una autoridad |
| Mensaje | Comunicación |
| Hecho | Ocurrencia confirmada |
| Efecto económico | Consecuencia que no debe duplicarse |
| Objeto | Persona, cuenta, contrato, instrumento o posición |
| Causa | Antecedente que origina la actuación |
| Versión | Contenido o condiciones utilizadas |

Cambiar el mensaje de transporte no cambia la identidad económica.

Una correlación puede reunir varios efectos legítimos.

### 7.1. Actor y fundamento

Las actuaciones sensibles identifican servicio, actor, persona por cuya cuenta actúa, finalidad y autorizaciones.

La identidad procede de autenticación o delegación verificable.

Una tarea automática identifica su causa habilitante: contrato, mandato, vencimiento u otro fundamento admitido.

### 7.2. Fechas

Se distinguen solicitud, recepción, hecho, fecha económica, valor, vencimiento, decisión, ejecución y confirmación.

La recepción tardía no modifica la fecha del hecho informado.

Una fecha declarada por el consumidor no permite antedatar una decisión.

## 8. Importes, cantidades y propiedad de datos

Todo importe identifica moneda.

Las cantidades de instrumentos identifican especie, clase y unidad.

Los contratos precisan escala, signo, sentido y redondeo, y separan principal, intereses, comisiones, tributos, total y
neto.

Cero y ausencia tienen significados diferentes.

Las conversiones conservan ambas monedas, importes, cotización, sentido y fecha.

Cada intercambio determina si necesita una referencia, información actual, una copia histórica o una evidencia.

Una copia no transfiere autoridad.

Los faltantes se informan. El consumidor no completa silenciosamente datos con supuestos.

## 9. Idempotencia

Las actuaciones con efectos tienen reconocimiento durable de repeticiones.

| Situación | Resultado |
| --- | --- |
| Misma actuación y contenido | Recupera el resultado existente |
| Misma actuación con otro contenido | Conflicto |
| Actuación pendiente | Devuelve seguimiento existente |
| Rechazo definitivo repetido | Conserva el rechazo |
| Condiciones diferentes | Nueva actuación vinculada |
| Corrección | Identidad propia y referencia al original |

La idempotencia del comando se complementa con unicidad del efecto económico.

Una devolución no se duplica por llegar desde varios circuitos.

El archivo de información conserva referencias suficientes para impedir ejecuciones repetidas.

Recuperar resultados exige permisos. Conocer una referencia no concede acceso.

## 10. Concurrencia sobre recursos

Cada recurso tiene una autoridad única.

La comprobación y afectación se realizan como una decisión local del responsable.

Esto comprende fondos, crédito de tarjetas, límites compartidos, unidades de inversión, garantías, disponibilidades
propias y topes tributarios.

Una consulta no reserva capacidad.

Una modificación identifica versión esperada. Si cambió, informa conflicto sin sobrescribir.

Cancelación y ejecución se ordenan sobre el mismo estado autoritativo.

Los recursos distintos de una operación se identifican separadamente. Una reserva del cliente y un compromiso físico de
efectivo no son duplicados: protegen recursos diferentes.

## 11. Protocolo común de preparación y decisión

### 11.1. Finalidad

El protocolo ordena una decisión frente a cambios concurrentes de condiciones administradas por otros servicios.

Se utiliza cuando una consulta favorable no basta para mantener la condición hasta confirmar.

Participan únicamente las autoridades exigibles para el acto.

No se exige preparar a todos los servicios ni todos los datos consultados.

### 11.2. Roles

| Participante | Responsabilidad |
| --- | --- |
| Coordinador | Mantiene el intento y decide confirmar o abortar |
| Autoridad | Comprueba y prepara una condición de su competencia |
| Responsable del recurso | Compromete o transforma su recurso |
| Ejecutor | Confirma el efecto que le corresponde |

Un servicio puede cumplir varios roles sin asumir datos ajenos.

Una preparación de autoridad no reserva dinero o crédito salvo que el intercambio lo establezca expresamente.

### 11.3. Datos del intento

El coordinador conserva:

- Identificador de intento.

- Operación y actuación.

- Contenido y huella.

- Actores y objetos.

- Versiones.

- Autoridades requeridas.

- Preparaciones.

- Compromisos de recursos.

- Límite de decisión.

- Decisión durable.

- Resultados y pendientes.

La decisión distingue pendiente, confirmada y abortada.

Los estados de procesamiento no sustituyen esa decisión.

### 11.4. Solicitud de preparación

El solicitante identifica:

- Coordinador e intento.

- Referencia autorizada de consulta de decisión.

- Acto y finalidad.

- Actores.

- Objetos y versiones.

- Importe y moneda cuando correspondan.

- Contenido vinculante.

- Límite de decisión.

- Evidencias exigibles.

La autoridad devuelve:

- Identificador de preparación.

- Resultado.

- Alcance preparado.

- Fundamentos y versiones.

- Condiciones temporales.

- Motivos autorizados.

- Consulta de seguimiento.

Una preparación favorable es un registro durable vinculado al intento.

No amplía facultades, levanta restricciones ni permite otro contenido.

### 11.5. Cambios incompatibles

La autoridad ordena localmente preparación y cambios incompatibles.

Si conoce un impedimento efectivo antes de preparar, no prepara favorablemente.

Si el cambio alcanza una preparación existente:

- Registra recepción y momento de efecto.

- Impide nuevas preparaciones incompatibles.

- Identifica los intentos afectados.

- Consulta sus decisiones.

- Solicita los abortos procedentes.

- Recupera resultados y completa la aplicación.

No invalida silenciosamente una preparación mientras el coordinador todavía puede utilizarla.

La recepción de una medida urgente no espera a concluir todas las consultas. Su seguimiento distingue efectos
confirmados, intentos abortados y resultados pendientes.

### 11.6. Decisión del coordinador

Confirmación y aborto compiten sobre el mismo intento.

El coordinador verifica contenido, condiciones, preparaciones, vigencias y recursos.

La decisión y el efecto local correspondiente se confirman conjuntamente.

En Contratos, celebrar y crear el contrato constituyen un único hecho local.

En Fondos, la decisión monetaria se registra junto con sus reservas o movimientos.

Los efectos de otros servicios permanecen sujetos a su coordinación y recuperación. No se presupone una transacción
global.

### 11.7. Aborto

El aborto es durable e impide confirmar ese intento.

Se comunican sus consecuencias a las autoridades y responsables de recursos.

Solo se liberan compromisos cancelables que correspondan a ese intento.

Una obligación firme o incierta conserva su cobertura.

Si ya existe confirmación, la petición de aborto devuelve esa decisión. Cualquier corrección posterior utiliza su
actuación propia.

### 11.8. Operaciones técnicas necesarias

Cada participación identifica expresamente:

| Operación | Resultado |
| --- | --- |
| Preparar | Condición persistida o resultado desfavorable |
| Consultar preparación | Estado y decisión conocida |
| Consultar intento | Decisión durable y efectos |
| Solicitar aborto | Aborto confirmado, confirmación existente o resolución pendiente |
| Notificar decisión | Decisión incorporada o pendiente de verificación |

Las notificaciones se autentican y vinculan a preparación, intento y contenido.

Las referencias remotas utilizan destinos configurados y autorizados.

Las consultas informativas existentes conservan su significado. No crean preparaciones como efecto oculto.

### 11.9. Vencimiento e incertidumbre

El vencimiento de una espera no demuestra aborto.

Cuando se alcanza el límite sin decisión conocida, se solicita resolver el intento.

El coordinador aborta si todavía no había decidido.

Si está indisponible, se mantienen preparación y seguimiento. No se presume confirmación ni aborto.

Una consulta aislada sin resultado no permite liberar.

La expiración de la propiedad temporal de un trabajador no modifica la decisión del negocio.

### 11.10. Alcance de la garantía

El protocolo proporciona una decisión durable recuperable y un orden explícito frente a cambios conocidos por las
autoridades participantes.

No proporciona una transacción SQL compartida.

La indisponibilidad puede mantener recursos y condiciones pendientes de resolución.

Los hechos conocidos después, incluidas medidas con efecto anterior a su recepción, conservan su tratamiento específico
y no se presentan como impedidos retroactivamente.

## 12. Autoridades y condiciones preparadas

Los documentos de módulo concretan las participaciones necesarias.

| Autoridad | Condición |
| --- | --- |
| Institución | Facultades internas |
| Catálogo de productos | Habilitación y compatibilidad exigibles |
| Condiciones comerciales | Aplicabilidad o conservación autorizada |
| Representación | Actuación por cuenta de otra persona |
| Cuentas | Capacidades y permisos específicos |
| Emisión de tarjetas | Habilitación del instrumento |
| Restricciones | Medidas aplicables |
| Responsable funcional | Fundamento habilitante de su competencia |

Una versión inmutable utilizada como antecedente no exige preparación por ese solo motivo.

Los contratos identifican cuáles condiciones deben mantenerse hasta decidir y cuáles corresponden a una evidencia
histórica válida.

## 13. Resultados y fallas

| Situación | Tratamiento |
| --- | --- |
| Entrada inválida | Informa qué impide interpretar o aceptar |
| Impedimento de negocio | Identifica la condición |
| Permiso insuficiente | No ejecuta ni revela datos protegidos |
| Conflicto de versión | Exige revisar contenido |
| Dependencia indisponible | Identifica comprobación o efecto pendiente |
| Actuación pendiente | Conserva identidad y seguimiento |
| Ejecución parcial | Detalla efectos y remanentes |
| Resultado incierto | Investiga antes de repetir o liberar |

Un fallo de comunicación no equivale a rechazo de negocio.

«No encontrado» distingue una referencia desconocida de una actuación comprobablemente descartada.

Los contratos no convierten falta de respuesta en autorización, saldo cero, inventario vacío ni ausencia de
restricciones.

## 14. Consulta y recuperación

Las actuaciones persistentes permiten recuperar:

- Recepción y aceptación.

- Preparaciones.

- Decisión.

- Efectos ejecutados.

- Recursos comprometidos.

- Parcialidades.

- Pendientes.

- Resultado definitivo.

La recuperación utiliza identidades originales.

Una consulta no reinicia la ejecución.

Las respuestas tardías se incorporan al seguimiento existente.

Las contradicciones abren investigación.

La intervención manual conserva fundamento, evidencia y aprobación.

Cada participante mantiene sus efectos. El coordinador mantiene el resultado conjunto.

## 15. Eventos y orden

Cada evento identifica hecho, tipo, versión, emisor, objeto, operación, causa y momento.

Cuando corresponda, identifica corrección y secuencia.

Los contratos garantizan que:

- Un duplicado no repite efectos.

- Un evento antiguo no reemplaza uno posterior.

- Los huecos relevantes reciben tratamiento.

- Un mensaje insuficiente no se descarta silenciosamente.

- El emisor conserva evidencia del hecho aunque falle su publicación.

- El consumidor vincula el evento con sus actuaciones.

No existe orden global entre servicios.

Una notificación de revocación no demuestra por sí sola que todos los intentos afectados estén abortados.

## 16. Procesos compartidos y efectos económicos

Cada proceso distingue las etapas que necesita: preparación, compromiso, ejecución, confirmación, liquidación y
corrección.

Identifica quién decide avanzar y qué evidencia necesita.

### 16.1. Trazabilidad contable

Operación, hecho económico, efecto operativo y asiento son referencias diferentes.

Una comunicación del coordinador y otra del ejecutor no representan necesariamente dos hechos contabilizables.

Contabilidad identifica fuentes y condición de integridad antes de completar la registración.

### 16.2. Cancelaciones y devoluciones

Cancelar una actuación pendiente y devolver una ejecutada tienen consecuencias diferentes.

Las compensaciones tienen identidad, fundamento y relación con el original.

Se ejecutan mediante el propietario del efecto y conservan historia.

### 16.3. Tarjetas

Los intercambios respetan esta distribución:

| Responsable | Recurso o dato |
| --- | --- |
| Emisión de tarjetas | Instrumento y configuración |
| Autorizaciones | Contadores operativos |
| Cuenta de tarjeta | Capacidad crediticia general y sublímites crediticios |
| Fondos | Reservas y movimientos de débito |

La configuración no se informa aplicada antes de la confirmación del propietario correspondiente.

El límite general y los sublímites crediticios se afectan conjuntamente.

Los contadores operativos conservan su período de origen y no duplican el uso al recibir la presentación.

### 16.4. Cambio interno de moneda

Los contratos distinguen:

- Compromiso temporal de preparación en Posiciones y liquidez.

- Capacidad y exposición en Riesgos.

- Movimientos de clientes en Fondos.

- Flujos y regularizaciones posteriores.

Resolver el compromiso temporal no crea un movimiento externo.

Transformar capacidad en exposición no libera riesgo como si el intercambio no hubiera ocurrido.

Una confirmación monetaria con regularización interna pendiente conserva su identidad y no se repite.

## 17. Seguridad y confidencialidad

Cada contrato identifica consumidores y finalidades autorizadas.

Se distinguen identidad técnica, autenticación, representación, derechos contractuales, permisos del producto,
facultades internas, restricciones y recursos.

Ninguno sustituye automáticamente los demás.

Los intercambios contienen solo información necesaria.

Credenciales y expedientes reservados no se incorporan a mensajes ordinarios.

Conocer un objeto, preparación o intento no concede acceso.

Las consultas de decisiones y solicitudes de aborto exigen correspondencia con la participación autorizada.

## 18. Versiones y compatibilidad

Cada contrato tiene versión identificada.

Requieren revisión:

- Nuevos datos obligatorios.

- Cambios de significado.

- Nuevos resultados.

- Precisión o moneda.

- Condiciones de confirmación.

- Autorización.

- Idempotencia.

- Recuperación.

- Participación en preparaciones.

Agregar un dato opcional solo es compatible si puede ignorarse sin interpretar mal el intercambio.

Los cambios incompatibles utilizan otra versión y contemplan actuaciones pendientes.

No se agregan preparaciones a intentos anteriores mediante una migración implícita. Su transición identifica qué reglas
les corresponden y cómo se concluyen.

Los cambios comerciales no se confunden con versiones del contrato técnico.

## 19. Correspondencia técnica

La especificación técnica concreta:

- Medio de comunicación.

- Operaciones y direcciones.

- Campos y tipos.

- Canales.

- Autenticación.

- Respuestas y errores.

- Tiempos de espera.

- Entrega y reintentos.

- Persistencia de identidades.

- Consultas de recuperación.

OpenAPI describe interfaces HTTP.

AsyncAPI y JSON Schema describen eventos y mensajes.

Las decisiones técnicas respetan el significado funcional: una recepción no se convierte en ejecución ni un timeout en
rechazo definitivo.

## 20. Criterios de cierre

Un contrato está suficientemente definido cuando:

- Tiene propósito y responsable.

- Identifica consumidores y finalidades.

- Define datos, origen y validaciones.

- Expresa condiciones y resultados comprobables.

- Respeta autoridades.

- Distingue etapas relevantes.

- Define idempotencia y conflictos.

- Trata concurrencia.

- Permite recuperación.

- Identifica parcialidades y correcciones.

- Mantiene trazabilidad económica.

- Incluye aceptación.

Cuando participa en el protocolo común, también define preparación, decisión, aborto, cambios incompatibles y
vencimiento sin resultado conocido.

El conjunto se comprueba relacionando operaciones funcionales, capacidades ofrecidas y dependencias consumidas.

Toda dependencia entre servicios necesaria para completar una operación tiene un intercambio definido. Una operación
interna que no cruza una frontera no necesita un contrato adicional.
