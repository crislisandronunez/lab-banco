# Arquitectura técnica del sistema bancario

## 1. Propósito y alcance

Este documento establece las decisiones técnicas comunes para implementar los servicios del sistema bancario.

La arquitectura representa el modelo de negocio, las responsabilidades de los módulos, las operaciones funcionales y los
contratos de integración definidos.

Comprende:

- Organización de servicios.

- Propiedad y persistencia de datos.

- Interfaces HTTP y eventos.

- Transacciones y coordinación de procesos.

- Preparación y confirmación de actuaciones.

- Idempotencia, concurrencia y recuperación.

- Identidad y permisos.

- Documentos y almacenamiento.

- Caché.

- Tareas programadas.

- Observabilidad y pruebas.

- Compatibilidad de contratos técnicos.

La documentación técnica se organiza en un documento principal y doce documentos de módulo.

Las referencias documentales utilizan nombres e identificadores funcionales, sin depender de rutas de archivos ni de un
repositorio particular.

## 2. Organización de servicios

El sistema comprende 51 servicios distribuidos en doce módulos.

| Módulo                                  | Servicios                                                                                                                                    |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| 1. Institución y oferta comercial       | Institución; Catálogo de productos; Condiciones comerciales; Calendarios bancarios                                                           |
| 2. Personas y relaciones bancarias      | Personas; Vinculación; Clientes; Representación                                                                                              |
| 3. Contratación                         | Solicitudes de productos; Contratos                                                                                                          |
| 4. Cuentas y fondos                     | Cuentas; Identificación bancaria; Fondos; Operaciones de efectivo                                                                            |
| 5. Pagos y transferencias               | Transferencias; Pagos; Mandatos y recurrencias; Cheques y ECHEQ; Compensación y conciliación de pagos                                        |
| 6. Financiación                         | Evaluación crediticia; Préstamos y líneas; Garantías; Cobranzas y recuperaciones                                                             |
| 7. Tarjetas                             | Emisión de tarjetas; Autorizaciones; Procesamiento de consumos; Cuenta de tarjeta; Disputas de tarjetas                                      |
| 8. Ahorro e inversiones                 | Depósitos a plazo; Instrumentos de inversión; Órdenes de inversión; Tenencias y valuación                                                    |
| 9. Contabilidad                         | Registración contable; Cierres contables; Información contable                                                                               |
| 10. Tesorería                           | Posiciones y liquidez; Operaciones de tesorería; Cambio de moneda; Liquidaciones de tesorería                                                |
| 11. Riesgo y cumplimiento               | Riesgos; Conocimiento del cliente; Prevención de lavado; Prevención de fraude; Restricciones; Criterios tributarios; Información regulatoria |
| 12. Atención y operación administrativa | Reclamos; Gestión administrativa; Documentos y comprobantes; Comunicaciones; Extractos                                                       |

Cada servicio constituye una aplicación independiente, con sus interfaces, configuración y ciclo de ejecución.

Los módulos agrupan responsabilidades. No crean bases compartidas ni permiten omitir contratos entre sus servicios.

La implementación avanza por recorridos funcionales completos. El entorno local permite ejecutar los servicios
necesarios para cada recorrido sin mantener los 51 procesos activos simultáneamente.

## 3. Tecnologías y responsabilidades

| Tecnología o capacidad | Responsabilidad                                   |
|------------------------|---------------------------------------------------|
| Java y Spring Boot     | Implementación de servicios                       |
| Maven                  | Construcción, dependencias y verificaciones       |
| PostgreSQL             | Persistencia autoritativa y transacciones locales |
| Flyway                 | Migraciones de esquemas                           |
| HTTP con JSON          | Consultas y comandos directos                     |
| Kafka                  | Distribución de eventos                           |
| Keycloak               | Autenticación y emisión de tokens                 |
| MinIO                  | Almacenamiento privado de documentos y evidencias |
| Redis                  | Caché opcional de información recuperable         |
| OpenAPI                | Especificación de interfaces HTTP                 |
| AsyncAPI y JSON Schema | Especificación de canales y mensajes              |
| Docker Compose         | Dependencias y ejecución local                    |
| OpenTelemetry          | Instrumentación y trazas                          |
| Prometheus y Grafana   | Métricas y visualización                          |
| Loki y Tempo           | Registros y consulta de trazas                    |

Las versiones concretas se fijan conjuntamente antes de implementar y se comprueba su compatibilidad.

MinIO se utiliza para documentos. Redis se incorpora cuando una consulta justifique caché por frecuencia o costo.

MongoDB no integra la arquitectura inicial. Su incorporación requiere una necesidad concreta que no quede adecuadamente
cubierta por las tecnologías adoptadas.

## 4. Estructura interna de los servicios

Cada servicio separa las siguientes responsabilidades:

| Componente   | Responsabilidad                                                      |
|--------------|----------------------------------------------------------------------|
| Dominio      | Reglas, estados e invariantes propios                                |
| Aplicación   | Casos de uso y coordinación                                          |
| Entrada      | Interfaces HTTP, consumidores y recepción de tareas                  |
| Salida       | Persistencia, clientes remotos, publicación y almacenamiento externo |

El dominio no depende de controladores HTTP, entidades de otros servicios ni detalles del transporte.

Los controladores y consumidores validan la entrada y delegan el caso de uso. Las reglas financieras permanecen en el
dominio y la aplicación.

Las entidades persistentes no se exponen directamente como respuestas HTTP ni como eventos.

Las bibliotecas comunes contienen capacidades técnicas. No contienen entidades de negocio compartidas que obliguen a
varios servicios a modificar conjuntamente su modelo.

## 5. Propiedad y persistencia de datos

### 5.1. Base por servicio

Cada servicio que necesita persistencia dispone de una base PostgreSQL y un usuario propios.

Varias bases pueden residir en una misma instancia local. Esto no concede acceso directo entre servicios.

Se prohíben:

- Consultas SQL a bases ajenas.

- Tablas de dominio compartidas.

- Claves foráneas entre bases de servicios.

- Repositorios de persistencia compartidos entre servicios.

- Modificaciones directas de datos ajenos para completar procesos.

Las referencias externas se conservan como identificadores, acompañadas por las versiones o evidencias necesarias.

### 5.2. Datos autoritativos y proyecciones

Cada dato tiene un responsable.

Los consumidores pueden mantener proyecciones para consulta, historia o evidencia. Estas identifican fuente, versión y
momento de actualización.

Una proyección no concede autoridad para modificar el original ni sustituye una comprobación exigible al confirmar una
actuación.

Cuando intervienen varias fuentes, se conserva el corte o versión de cada una. Los datos faltantes y las
discontinuidades se informan expresamente.

### 5.3. Migraciones

Flyway administra los cambios de esquema.

Una migración aplicada es inmutable. Las correcciones utilizan migraciones posteriores.

Los cambios consideran datos existentes, lectores, escritores y mensajes pendientes. La eliminación de campos exige
resolver previamente sus usos y compatibilidad.

Los datos de prueba son sintéticos y se mantienen separados de otras configuraciones.

## 6. Identificadores, versiones y representación de datos

### 6.1. Identificadores

Los objetos técnicos nuevos utilizan UUID como identificador interno.

Los identificadores bancarios, documentales, tributarios y externos conservan su formato específico, incluidos ceros
iniciales y caracteres significativos.

Se distinguen:

- Identificador del objeto.

- Operación de negocio.

- Actuación solicitada.

- Intento de confirmación.

- Preparación de una autoridad.

- Efecto económico.

- Evento.

- Correlación.

- Causa.

Una correlación puede reunir varios efectos legítimos. La identidad del efecto económico permite reconocer duplicados
aunque lleguen por distintos canales.

### 6.2. Versiones

Se distinguen las versiones de:

- Contrato técnico.

- Esquema de evento.

- Objeto.

- Política o condiciones.

- Documento.

- Contenido canónico utilizado para calcular una huella.

Las modificaciones sensibles reciben la versión esperada. Un conflicto no sobrescribe cambios posteriores.

### 6.3. Importes y cantidades

Los importes utilizan BigDecimal en Java y tipos decimales exactos en PostgreSQL.

En JSON se representan como cadenas decimales acompañadas por su moneda.

No se utilizan tipos de punto flotante para dinero, tasas ni cantidades que requieran exactitud.

Cada contrato define precisión, escala, unidad y redondeo. Los cálculos conservan al menos doce decimales intermedios
cuando lo exige la especificación funcional.

Las cantidades de instrumentos identifican especie, clase y unidad.

Cero y ausencia de información tienen significados diferentes.

### 6.4. Fechas y horas

Los instantes se intercambian en UTC mediante ISO 8601.

Las fechas sin hora se representan separadamente.

Los calendarios institucionales utilizan America/Argentina/Buenos_Aires. La zona del equipo no determina las reglas del
negocio.

Se distinguen fecha económica, recepción, valor, vencimiento, decisión, ejecución y confirmación.

### 6.5. Definición de campos

Los contratos técnicos concretan, para cada campo:

- Significado y propietario.

- Tipo.

- Obligatoriedad.

- Longitud, precisión o escala.

- Valores admitidos.

- Validaciones.

- Tratamiento de ausencia.

- Sensibilidad.

- Uso histórico.

La representación interna puede diferir del intercambio si conserva significado y exactitud.

## 7. Comunicación HTTP

### 7.1. Consultas

Las consultas recuperan información y no crean preparaciones, reservas ni compromisos.

Sus respuestas identifican versión, corte, integridad y datos faltantes cuando corresponda.

Una comprobación favorable informa la situación evaluada. Cuando la confirmación necesita protección frente a cambios
concurrentes, se utiliza el protocolo de preparación definido en este documento.

Las consultas extensas utilizan paginación y orden estable. Una consulta histórica mantiene el mismo corte entre
páginas.

### 7.2. Comandos

Los comandos con efectos persistentes reciben una referencia estable y permiten consultar su resultado.

Se distinguen recepción, aceptación durable y ejecución.

HTTP 202 significa aceptación durable para procesamiento y proporciona seguimiento. No representa ejecución final.

Las respuestas indican qué quedó confirmado y qué permanece pendiente.

Las preparaciones y solicitudes de aborto son comandos explícitos. No se ejecutan como efectos ocultos de consultas.

### 7.3. Errores

Los errores utilizan Problem Details, código estable y correlación.

Distinguen:

- Entrada inválida.

- Falta de autenticación.

- Permisos insuficientes.

- Objeto no localizado.

- Conflicto de contenido o versión.

- Impedimento de negocio.

- Dependencia indisponible.

- Resultado pendiente o incierto.

No contienen credenciales, SQL, trazas internas ni datos sensibles innecesarios.

El código HTTP no sustituye el estado de la actuación.

### 7.4. Tiempos y reintentos

Las llamadas ordinarias utilizan inicialmente dos segundos de conexión y cinco segundos de respuesta. Un contrato
especializado puede establecer otros valores.

Las consultas seguras admiten como máximo dos intentos totales ante fallas transitorias, respetando el presupuesto
completo de tiempo.

Los comandos no se reintentan automáticamente ante una falla de comunicación. Primero se recupera su resultado con la
referencia original.

Una reentrega conserva identidad y contenido, y requiere deduplicación durable en el receptor.

Un timeout, HTTP 503 o una consulta sin resultado no demuestran ausencia definitiva de ejecución.

## 8. Eventos y Kafka

### 8.1. Finalidad

Los eventos describen hechos confirmados.

Permiten distribuir cambios, actualizar proyecciones y continuar procesos.

No se presupone entrega exactamente una vez ni orden global.

### 8.2. Sobre común

Cada evento contiene:

- Identificador estable.

- Tipo y versión de esquema.

- Servicio emisor.

- Identificador del objeto.

- Momento del hecho.

- Operación y correlación.

- Causa, cuando corresponda.

- Versión o secuencia del objeto.

- Contenido mínimo necesario.

La versión del esquema y la del objeto son independientes.

Los eventos no distribuyen expedientes completos cuando basta una referencia y un resultado autorizado.

### 8.3. Outbox

El cambio local y su evento pendiente se guardan en la misma transacción PostgreSQL.

Un publicador entrega el evento a Kafka y registra su resultado.

Si se pierde la confirmación de entrega, puede reenviarse el mismo evento. Su identificador permanece estable.

### 8.4. Inbox

El consumidor registra el identificador recibido y su efecto local en una misma transacción.

El avance de lectura se confirma después del commit.

Cuando el procesamiento requiere un comando remoto, conserva una intención durable y lo ejecuta fuera de la transacción
local.

La inbox no garantiza por sí sola una ejecución remota única.

### 8.5. Orden y recuperación

Cuando se requiere orden por objeto, la partición utiliza su identificador.

Un evento antiguo no reemplaza un estado posterior. Los huecos se recuperan mediante consulta o espera conforme al
contrato.

Los mensajes inválidos conservan causa y seguimiento. Apartarlos del procesamiento no equivale a aplicarlos.

Las recuperaciones mantienen identidades. Una corrección de contenido genera un evento nuevo vinculado al anterior.

## 9. Transacciones y concurrencia

### 9.1. Transacciones locales

Cada servicio confirma sus invariantes dentro de su propia base.

La transacción comprende los cambios, la idempotencia y la outbox del efecto local.

No permanece abierta durante:

- Llamadas HTTP.

- Publicación a Kafka.

- Almacenamiento en MinIO.

- Envío de comunicaciones.

- Intervención humana.

No existe una transacción SQL compartida entre servicios.

### 9.2. Recursos concurrentes

El responsable comprueba y afecta el recurso como una única decisión local.

Esto comprende fondos, capacidad crediticia, límites, garantías, unidades de inversión, disponibilidades propias y topes
tributarios.

La implementación utiliza restricciones de unicidad, bloqueos y control de versiones.

Los objetos múltiples se bloquean en orden estable.

Una consulta previa no sustituye los controles necesarios para impedir consumos concurrentes incompatibles.

### 9.3. Movimientos relacionados

Los movimientos que pertenecen a una misma autoridad y requieren confirmación conjunta se ejecutan en su transacción
local.

Fondos confirma conjuntamente las piernas de una transferencia interna y de un intercambio interno de monedas conforme a
CI-04-018.

Esa atomicidad no se extiende a las bases de Cambio de moneda, Tesorería o Contabilidad. Sus registros se completan
mediante coordinación recuperable.

### 9.4. Conflictos

Los conflictos de versión tienen una respuesta definida.

Los reintentos por bloqueos o deadlocks son acotados y conservan la identidad de la actuación.

Cancelación y ejecución se ordenan en el estado autoritativo. No se confirma una cancelación incompatible con un efecto
ya realizado.

## 10. Coordinación persistente

El servicio que tiene asignada la responsabilidad funcional coordina el proceso compartido.

Conserva:

- Etapa actual.

- Actuaciones solicitadas.

- Referencias remotas.

- Preparaciones y recursos comprometidos.

- Resultados comprobados.

- Próxima acción.

- Incidencias.

- Necesidad de intervención.

No existe un coordinador universal que sustituya a los responsables funcionales.

Antes de enviar un comando se registra su intención.

Si el proceso se interrumpe después de un posible efecto remoto, recupera el resultado original antes de continuar.

Las compensaciones son actuaciones de negocio vinculadas. Conservan la historia y no equivalen a un rollback
distribuido.

Los trabajadores utilizan propiedad temporal y una versión de ejecución que impide a un trabajador atrasado confirmar un
paso sustituido.

La expiración de la propiedad de un trabajador no cancela la operación ni libera sus preparaciones o recursos.

## 11. Preparación y decisión de actuaciones

### 11.1. Aplicación

Este protocolo se utiliza cuando la confirmación depende de condiciones administradas por otra autoridad y es necesario
ordenar la decisión frente a cambios concurrentes.

Participan únicamente las autoridades exigibles para la actuación.

Entre ellas pueden encontrarse:

| Autoridad | Alcance |
| --- | --- |
| Institución | Facultades internas y decisiones institucionales |
| Representación | Actuación por cuenta de otra persona |
| Cuentas | Capacidades y permisos específicos de cuentas |
| Restricciones | Medidas aplicables |
| Emisión de tarjetas | Habilitación del instrumento |
| Responsable funcional correspondiente | Condición habilitante de su competencia |

El protocolo no convierte toda consulta en una preparación ni exige que todos los servicios intervengan en cada
operación.

### 11.2. Intento de confirmación

El coordinador conserva un intento con:

- Identificador estable.

- Operación y actuación de origen.

- Objetos y versiones.

- Contenido canónico y huella.

- Actores y alcances.

- Autoridades necesarias.

- Preparaciones recibidas.

- Recursos comprometidos.

- Límite temporal de decisión.

- Decisión durable.

- Referencias de resultado.

- Versión de concurrencia.

La decisión distingue pendiente, confirmada y abortada.

El seguimiento puede identificar preparación, disponibilidad para decidir y recuperación requerida. Estos estados no
sustituyen la decisión durable.

### 11.3. Preparación de una autoridad

La autoridad comprueba sus condiciones y registra una preparación vinculada al intento y al contenido exacto.

Conserva:

- Identificador de preparación.

- Coordinador e intento.

- Referencia para consultar la decisión.

- Actores, objetos y acción.

- Importe y moneda cuando correspondan.

- Huella de contenido.

- Versiones utilizadas.

- Alcance y condiciones temporales.

- Estado y resultado conocido.

Una preparación no concede facultades nuevas, no levanta medidas y no puede reutilizarse para otra actuación.

Tampoco reserva dinero o crédito salvo que el contrato incluya expresamente ese compromiso. Las preparaciones de
autoridad y los compromisos de recursos se identifican por separado.

### 11.4. Orden frente a cambios incompatibles

La autoridad serializa localmente las preparaciones y los cambios incompatibles sobre sus controles.

Si un impedimento efectivo y conocido se confirmó antes de preparar, rechaza la preparación incompatible.

Cuando un cambio alcanza preparaciones pendientes:

- Registra recepción, fundamento y momento de efecto.

- Impide nuevas preparaciones incompatibles.

- Identifica los intentos afectados.

- Consulta sus decisiones.

- Solicita aborto de los que todavía puedan abortarse.

- Recupera el resultado y completa su aplicación.

La autoridad no invalida silenciosamente una preparación mientras el coordinador todavía puede utilizarla para decidir.

Las medidas urgentes se registran y tramitan inmediatamente. Su seguimiento distingue operaciones abortadas, efectos ya
confirmados y resultados pendientes.

Los efectos anteriores al conocimiento de una medida se tratan expresamente. El protocolo no elimina consecuencias que
deban evaluarse posteriormente.

### 11.5. Decisión final

El coordinador ordena confirmación y aborto sobre el mismo intento.

Antes de confirmar comprueba:

- Identidad y contenido.

- Versiones y vigencias.

- Preparaciones exigibles completas.

- Ausencia de aborto confirmado.

- Condiciones y recursos locales.

- Requisitos funcionales de la actuación.

La misma transacción local registra la decisión y el efecto de su competencia, junto con historia, idempotencia y
outbox.

En Contratos, la decisión de celebración y la creación del contrato son un único hecho local.

En Fondos, la decisión monetaria y sus reservas o movimientos correspondientes se confirman conjuntamente.

La conclusión de preparaciones remotas se comunica después y conserva recuperación hasta comprobar su recepción.

### 11.6. Aborto

El aborto se registra durablemente sobre el intento.

Después de abortar, ese intento no puede confirmar.

El coordinador notifica a las autoridades y regulariza los compromisos cancelables.

Abortar un intento no libera recursos que respalden otra obligación firme o incierta.

Una solicitud de aborto recibida después de confirmar devuelve la decisión existente y las referencias del efecto. No
borra la operación.

### 11.7. Vencimiento y recuperación

El vencimiento de una espera no demuestra aborto.

Cuando se alcanza el límite sin decisión conocida, se solicita resolver el intento. El coordinador confirma el aborto si
todavía no había decidido.

Si el coordinador no está disponible, la preparación permanece pendiente y se escala.

Las autoridades no liberan preparaciones por timeout ni por una consulta aislada sin resultado.

Los intentos y decisiones conservan referencias suficientes para impedir que una petición atrasada reactive una
actuación abortada.

### 11.8. Interfaces del protocolo

Los contratos distinguen operaciones explícitas para:

- Preparar.

- Consultar la preparación.

- Consultar la decisión del intento.

- Solicitar aborto.

- Notificar una decisión.

Las notificaciones se autentican y su correspondencia con el intento se verifica.

Las consultas funcionales existentes mantienen su significado informativo. Las preparaciones son actuaciones técnicas
adicionales, identificadas y documentadas.

### 11.9. Garantía y disponibilidad

El protocolo proporciona una decisión durable recuperable y un orden explícito frente a cambios conocidos por las
autoridades participantes.

No proporciona una transacción SQL global.

Ante una partición o indisponibilidad puede mantener preparaciones y recursos pendientes. Se conserva ese bloqueo antes
que presumir una decisión desconocida.

## 12. Recursos, límites y transformaciones

Cada recurso tiene una autoridad y una identidad de afectación.

Una operación puede requerir recursos distintos. Su vinculación no los convierte en un mismo saldo.

### 12.1. Tarjetas

La distribución es:

| Responsable | Recurso |
| --- | --- |
| Emisión de tarjetas | Instrumento y coordinación de configuración |
| Autorizaciones | Contadores operativos por operación o período |
| Cuenta de tarjeta | Límite crediticio general y sublímites crediticios |
| Fondos | Reservas y movimientos de débito |

Cuenta de tarjeta comprueba y afecta conjuntamente el límite general y los sublímites crediticios correspondientes.

Los contadores operativos conservan su período original y distinguen pendiente y ejecutado.

Las autorizaciones inciertas mantienen sus afectaciones hasta obtener resultado o cancelación comprobada.

### 12.2. Cambio interno de moneda

El cambio interno de saldos de clientes:

- Ejecuta sus movimientos monetarios en Fondos.

- Mantiene el compromiso temporal de preparación en Posiciones y liquidez.

- Compromete y transforma la capacidad exigible en Riesgos.

- Actualiza flujos y necesidades por moneda.

La confirmación de Fondos no demuestra una salida de efectivo ni de una cuenta externa del banco.

El compromiso temporal se resuelve con referencias al resultado monetario y a la transformación de riesgo, sin crear un
movimiento externo ficticio.

Si Fondos confirmó y falta una regularización interna, la operación conserva ese resultado y el seguimiento pendiente.
No se repite el intercambio.

### 12.3. Transformaciones

Una transformación reemplaza la afectación anterior por la resultante conservando continuidad e identidad.

No mantiene ambos componentes como consumos independientes ni abre un intervalo ficticio de capacidad libre.

Los agregados posteriores reconocen los componentes ya incorporados para evitar doble cómputo.

## 13. Idempotencia y recuperación

### 13.1. Solicitudes

El receptor conserva identidad del solicitante, referencia de actuación, contenido canónico y resultado.

La misma referencia y contenido recupera el estado existente.

La misma referencia con contenido diferente produce conflicto.

Un rechazo definitivo conserva su resultado. Un intento bajo condiciones diferentes utiliza otra referencia vinculada.

### 13.2. Efectos económicos

La idempotencia del comando se complementa con unicidad del efecto.

Un débito no puede ejecutarse por consumo de reserva y por movimiento independiente.

Una devolución no se repite por llegar desde reclamo, disputa y red.

Los identificadores necesarios para detectar duplicados se conservan aunque se archive el detalle.

### 13.3. Incertidumbre

Un resultado incierto conserva recursos y seguimiento.

No se libera por timeout, cierre de sesión o agotamiento de reintentos técnicos.

Abandonar una solicitud en tránsito requiere una cancelación o barrera confirmada que impida su ejecución posterior.

Los hechos físicos comprobados conservan su tratamiento. Una entrega de efectivo realizada requiere regularizar sus
efectos, aunque ya no corresponda aceptar una nueva instrucción del cliente.

## 14. Identidad, seguridad y permisos

Keycloak administra autenticación y emisión de tokens mediante OIDC y OAuth2.

Se distinguen:

- Identidad del servicio.

- Identidad del usuario.

- Representación.

- Derechos contractuales.

- Facultades sobre el producto.

- Facultades internas.

- Restricciones.

- Disponibilidad de recursos.

Los servicios validan emisor, destinatario, vigencia y permisos.

La delegación de usuario debe ser verificable por el receptor. Un identificador de actor enviado en JSON no acredita por
sí solo su intervención.

Keycloak no sustituye a las autoridades de negocio.

Los permisos también se comprueban al recuperar resultados idempotentes y documentos históricos.

Los secretos se proporcionan mediante configuración protegida y no se incorporan a código, eventos o registros.

## 15. Documentos y MinIO

MinIO almacena contenido binario privado.

Documentos y comprobantes administra:

- Identidad y versión.

- Procedencia.

- Tipo y tamaño.

- Huella de integridad.

- Clasificación.

- Referencia de almacenamiento.

- Permisos.

- Conservación y preservaciones.

- Estado de recepción, recuperación y disposición.

Los servicios de negocio acceden mediante Documentos y comprobantes. No administran directamente objetos en MinIO.

### 15.1. Recepción

PostgreSQL y MinIO no comparten transacción.

La recepción registra intención, almacena contenido, verifica integridad y confirma metadatos mediante un proceso
recuperable.

Hasta completar la verificación, el documento no se informa como íntegro y disponible.

Los objetos sin proceso válido y las intenciones incompletas se identifican para recuperación o limpieza controlada.

### 15.2. Acceso e historia

Los objetos son privados.

Las direcciones temporales, cuando se utilizan, tienen alcance y vencimiento limitados.

Un identificador o huella no constituye permiso.

Las versiones originales se conservan conforme a su política y no se sobrescriben silenciosamente.

### 15.3. Disposición

La eliminación comprueba conservación y preservaciones.

El seguimiento incluye versiones, copias gestionadas y respaldos. No se informa eliminación física integral mientras
existan componentes pendientes.

Una restauración reaplica las disposiciones vigentes.

## 16. Redis y caché

Redis contiene resultados recuperables.

Los primeros usos admitidos son consultas frecuentes de catálogo, referencias comerciales y calendarios cuando el
contrato permita una copia con antigüedad conocida.

Cada uso define fuente, clave, versión, validez, invalidación, sensibilidad y comportamiento ante indisponibilidad.

Redis no es autoridad de:

- Saldos y reservas.

- Límites y contadores durables.

- Restricciones.

- Preparaciones y decisiones.

- Topes tributarios.

- Idempotencia persistente.

Una copia favorable no sustituye una preparación exigible.

Ante una falla de Redis se consulta la fuente cuando sea posible. La falla no se transforma en saldo cero ni en
autorización.

## 17. Tareas programadas y tiempo

Los vencimientos, cierres, cobros y revisiones se representan mediante tareas persistentes en el servicio responsable.

Cada ocurrencia conserva identidad, fecha prevista, calendario, estado, intentos y próxima actuación.

Las ejecuciones concurrentes no producen dos ocurrencias del mismo vencimiento.

La recuperación respeta la fecha y el calendario originales y las reglas para instrucciones vencidas. No ejecuta
automáticamente actuaciones que requieren una nueva confirmación.

Los procesos utilizan propiedad temporal y control de versión.

La expiración de una tarea o de su trabajador no libera preparaciones, reservas ni compromisos de negocio.

El reloj utilizado en decisiones concurrentes se consulta dentro del control transaccional correspondiente.

## 18. Participantes externos y comunicaciones

Los participantes externos se representan mediante adaptadores y simuladores con referencias y resultados recuperables.

Distinguen aceptación, ejecución, rechazo, incertidumbre y respuesta tardía.

Recibir una instrucción no equivale a ejecutarla.

Las comunicaciones locales se capturan en Mailpit. Su aceptación técnica no demuestra entrega al destinatario.

Los circuitos simulados no envían operaciones financieras, reportes regulatorios ni comunicaciones institucionales
reales.

## 19. Observabilidad

OpenTelemetry propaga trazas entre solicitudes, eventos y procesos.

Prometheus recopila métricas; Grafana presenta paneles; Loki conserva registros y Tempo permite consultar trazas.

Se observan:

- Latencias y errores.

- Procesos pendientes y antigüedad.

- Outbox e inbox.

- Duplicados y reintentos.

- Conflictos de concurrencia.

- Preparaciones sin decisión conocida.

- Abortos y confirmaciones pendientes de comunicación.

- Compromisos pendientes de resolución.

- Liquidaciones inciertas.

- Diferencias de conciliación.

- Tareas vencidas.

- Fallas de almacenamiento y entrega.

Los identificadores de personas y operaciones no se utilizan como etiquetas de métricas de alta cardinalidad.

Los registros excluyen credenciales y datos sensibles innecesarios.

Una traza ayuda a investigar. La evidencia del negocio permanece en registros durables.

## 20. Estrategia de pruebas

| Nivel | Propósito |
| --- | --- |
| Unitarias | Reglas, cálculos y transiciones |
| Persistencia | Migraciones, unicidad, bloqueo y versiones |
| Contratos | Solicitudes, respuestas, eventos y compatibilidad |
| Integración | Dependencias técnicas y seguridad |
| Recorridos completos | Comportamiento conjunto de las operaciones |
| Recuperación | Caídas, duplicados, respuestas perdidas y resultados tardíos |

Las pruebas financieras verifican importes, conceptos, fechas y cantidad de efectos.

La concurrencia y el commit se comprueban con PostgreSQL real aislado.

Los recorridos de preparación incluyen:

- Impedimento confirmado antes de preparar.

- Cambio incompatible después de preparar.

- Competencia entre aborto y confirmación.

- Vencimiento sin decisión conocida.

- Caída del coordinador.

- Notificación perdida.

- Petición atrasada después de abortar.

- Confirmación local con regularización remota pendiente.

También se comprueban afectaciones conjuntas de límites, mantenimiento de recursos inciertos y transformaciones sin
doble cómputo.

## 21. Contratos técnicos y compatibilidad

Cada interfaz identifica la responsabilidad y el contrato funcional que representa.

Una capacidad funcional puede requerir varias operaciones técnicas, como iniciar, preparar, consultar y resolver.

Estas operaciones conservan la identidad de la actuación y no duplican sus efectos económicos.

OpenAPI concreta campos, tipos, permisos, respuestas y errores.

AsyncAPI y JSON Schema concretan canales, productores, consumidores y mensajes.

Los contratos de preparación especifican además:

- Coordinador y autoridades.

- Alcance preparado.

- Contenido vinculante.

- Condiciones temporales.

- Decisiones admitidas.

- Consulta y recuperación.

- Tratamiento de cambios incompatibles.

- Condiciones para concluir la preparación.

Los cambios incompatibles utilizan otra versión y contemplan actuaciones anteriores y mensajes pendientes.

Una modificación de tasa o parámetro comercial no equivale por sí sola a cambiar el contrato técnico.

Las consultas conservan su carácter informativo. Ningún cambio de contrato introduce reservas o preparaciones como
efectos ocultos.

## 22. Contenido de los documentos técnicos por módulo

Cada documento desarrolla:

- Responsabilidades y fronteras.

- Datos propios y validaciones.

- Objetos, estados e invariantes.

- Persistencia e índices.

- Límites transaccionales.

- Interfaces HTTP.

- Eventos.

- Procesos compartidos.

- Participación como coordinador o autoridad.

- Idempotencia, concurrencia y recuperación.

- Permisos y protección de información.

- Tareas temporales.

- Uso de almacenamiento y caché.

- Observabilidad.

- Pruebas y criterios de aceptación.

Las decisiones comunes se aplican desde este documento.

Cada módulo concreta su participación sin cambiar la propiedad de los datos, las responsabilidades funcionales ni el
significado de las operaciones bancarias.
