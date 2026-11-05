# Contratos técnicos del sistema bancario

## 1. Propósito y alcance

Este documento establece las convenciones comunes para las interfaces HTTP y los eventos del sistema bancario.

Los contratos técnicos concretan las capacidades definidas en la integración entre servicios y respetan las
responsabilidades y los mecanismos establecidos en la arquitectura técnica.

Definen:

- Operaciones, proveedores y consumidores.

- Rutas HTTP y canales de eventos.

- Campos, tipos y obligatoriedad.

- Validaciones y resultados.

- Autenticación y autorización.

- Idempotencia y concurrencia.

- Preparaciones, decisiones y abortos.

- Consulta y recuperación.

- Integridad del contenido.

- Compatibilidad y versionado.

Cada interfaz conserva la autoridad y el significado funcional de la capacidad que representa.

## 2. Organización documental

La especificación comprende un apartado principal y doce apartados de módulo.

| **Apartado**                             | **Contenido**                                              |
|------------------------------------------|------------------------------------------------------------|
| Principal                                | Convenciones y estructuras comunes.                        |
| 1\. Institución y oferta comercial       | Institución, productos, condiciones y calendarios.         |
| 2\. Personas y relaciones bancarias      | Personas, vinculación, clientes y representación.          |
| 3\. Contratación                         | Solicitudes, propuestas y acuerdos.                        |
| 4\. Cuentas y fondos                     | Cuentas, identificadores, fondos y efectivo.               |
| 5\. Pagos y transferencias               | Transferencias, pagos, mandatos, cheques y compensación.   |
| 6\. Financiación                         | Evaluación, préstamos, garantías y cobranzas.              |
| 7\. Tarjetas                             | Instrumentos, autorizaciones, consumos, deuda y disputas.  |
| 8\. Ahorro e inversiones                 | Depósitos, instrumentos, órdenes y tenencias.              |
| 9\. Contabilidad                         | Registración, cierres e información contable.              |
| 10\. Tesorería                           | Liquidez, operaciones propias, cambio y liquidaciones.     |
| 11\. Riesgo y cumplimiento               | Riesgos, controles, restricciones, tributos e información. |
| 12\. Atención y operación administrativa | Reclamos, gestión, documentos, comunicaciones y extractos. |

Las interfaces se agrupan por servicio proveedor.

Una estructura compartida se define una sola vez. Los contratos particulares indican su utilización y agregan sus
requisitos específicos.

Las referencias documentales utilizan nombres e identificadores sin depender de rutas de archivos.

## 3. Unidad de especificación

Cada operación técnica define:

| **Elemento**              | **Contenido obligatorio**                                |
|---------------------------|----------------------------------------------------------|
| Identificador técnico     | operationId estable y único dentro del servicio.         |
| Correspondencia funcional | Contrato CI que desarrolla.                              |
| Proveedor                 | Servicio responsable.                                    |
| Consumidores              | Servicios o funciones admitidos y finalidad.             |
| Transporte                | Método y ruta HTTP, o canal y tipo de evento.            |
| Entrada                   | Parámetros, encabezados y cuerpo.                        |
| Salida                    | Estructura y resultados admitidos.                       |
| Seguridad                 | Identidad, permiso y alcance por objeto.                 |
| Efectos                   | Cambios que confirma y pendientes que puede conservar.   |
| Concurrencia              | Versiones, unicidad y actuaciones incompatibles.         |
| Recuperación              | Consulta disponible e identificación original necesaria. |

Un contrato funcional puede desarrollarse mediante varias operaciones técnicas.

Las variantes de entrada y salida tienen un discriminador y campos propios. No se interpretan mediante combinaciones
ambiguas de campos opcionales.

Las especificaciones HTTP se representan mediante OpenAPI. Los eventos se representan mediante AsyncAPI y JSON Schema.

## 4. Identificación y versiones

Se distinguen:

- Versión de API.

- Versión de esquema.

- Versión de objeto.

- Versión de configuración o condiciones.

- Versión documental.

- Versión del procedimiento de canonicalización.

Una versión de objeto no sustituye una versión de esquema.

Las rutas HTTP utilizan /v1. Las rutas de la arquitectura que omiten ese prefijo se interpretan como relativas a esa
versión.

Los contratos técnicos conservan las rutas y los temas establecidos en la arquitectura. Cualquier sustitución requiere
una modificación explícita y compatible de ambas especificaciones.

Los identificadores internos de recursos utilizan UUID.

Los identificadores sensibles se incluyen en cuerpos de búsqueda cuando su presencia en una URL pueda exponerlos en
registros.

## 5. Tipos de datos

| **Tipo**              | **Representación**                                         |
|-----------------------|------------------------------------------------------------|
| Identificador interno | Cadena UUID en minúsculas con guiones.                     |
| Versión o secuencia   | Entero positivo dentro del rango de long.                  |
| Fecha                 | Cadena YYYY-MM-DD.                                         |
| Instante              | Cadena ISO 8601 en UTC.                                    |
| Importe monetario     | Cadena decimal y moneda identificada.                      |
| Tasa o proporción     | Cadena decimal.                                            |
| Cantidad              | Cadena decimal con unidad identificada.                    |
| Booleano              | true o false.                                              |
| Enumeración           | Cadena con valores definidos.                              |
| Código                | Cadena de 1 a 80 caracteres, salvo restricción particular. |
| Referencia externa    | Cadena de 1 a 120 caracteres.                              |
| Fundamento            | Cadena de 1 a 4000 caracteres.                             |
| Resumen SHA-256       | Cadena hexadecimal de 64 caracteres en minúsculas.         |
| Colección             | Arreglo con elementos tipados.                             |

### 5.1. Decimales

Las cadenas decimales utilizan punto como separador, sin separadores de miles, espacios, signo positivo ni notación
exponencial.

Cada campo establece:

- Precisión.

- Escala máxima o fija.

- Signo admitido.

- Unidad o moneda.

- Regla de redondeo, cuando existe cálculo.

Los importes monetarios ejecutables en ARS y USD admiten hasta dos decimales. Un valor fuera de escala se rechaza sin
redondearlo silenciosamente.

Los cálculos, cantidades, cotizaciones y valuaciones utilizan la precisión particular de su contrato.

Las tasas se intercambian como proporciones: el 1,5 % se representa como 0.015.

### 5.2. Fechas y vigencias

Los instantes se intercambian en UTC. Los cálculos bancarios identifican calendario y zona.

La zona institucional es America/Argentina/Buenos_Aires.

Los intervalos incluyen inicio y excluyen final, salvo rangos de cobertura declarados expresamente como inclusivos.

Recepción, aceptación, decisión, ejecución, fecha económica y fecha valor son datos diferentes.

### 5.3. Texto

Las cadenas admiten Unicode válido. Se rechazan caracteres de control, salvo tabulación y saltos de línea en campos que
expresamente los permitan.

No se recortan ni normalizan silenciosamente nombres, descripciones o evidencias.

Los identificadores oficiales se conservan como texto, incluidos sus ceros iniciales.

## 6. Obligatoriedad y ausencia

Cada campo se identifica como obligatorio, opcional o condicional. Los campos condicionales establecen la condición
exacta de presencia.

| **Representación** | **Significado**                                    |
|--------------------|----------------------------------------------------|
| Campo omitido      | No informado o no aplicable según el esquema.      |
| Colección vacía    | Conjunto conocido sin elementos.                   |
| Cero               | Valor conocido igual a cero.                       |
| Cadena vacía       | Solo admitida si el campo lo permite expresamente. |
| null               | Solo admitido si el esquema define su significado. |

Los campos que admiten información desconocida pueden utilizar una estructura con:

- state: PROVIDED, UNKNOWN, NOT_PROVIDED o NOT_APPLICABLE.

- value: presente únicamente con PROVIDED.

No se utilizan valores ficticios para completar datos faltantes.

Los objetos de negocio no admiten propiedades adicionales sin definir. Las extensiones configurables utilizan atributos
registrados, tipados y versionados.

## 7. Encabezados HTTP

| **Encabezado**   | **Uso**                               |
|------------------|---------------------------------------|
| Authorization    | Credencial verificable.               |
| Content-Type     | Tipo del contenido enviado.           |
| Accept           | Tipo de respuesta admitido.           |
| X-Correlation-Id | Correlación de la solicitud.          |
| traceparent      | Propagación de traza cuando existe.   |
| Idempotency-Key  | Obligatorio en comandos persistentes. |

Las solicitudes ordinarias utilizan JSON. Las interfaces binarias declaran su tipo de contenido particular.

Los errores utilizan application/problem+json.

La correlación tiene entre 1 y 120 caracteres y no contiene datos personales. No se utiliza como identidad de negocio ni
como clave de idempotencia.

## 8. Contexto de actuación

Los comandos persistentes contienen ActionContext.

| **Campo**          | **Tipo**            | **Obligatoriedad**                               |
|--------------------|---------------------|--------------------------------------------------|
| actionId           | UUID                | Obligatorio y estable.                           |
| operationId        | UUID                | Obligatorio; identifica la operación de negocio. |
| causationId        | UUID                | Cuando existe actuación causante.                |
| purpose            | Código              | Obligatorio.                                     |
| reason             | Fundamento          | Cuando la actuación exige justificación.         |
| expectedVersion    | Versión             | Para modificaciones sujetas a versión.           |
| originalReceivedAt | Instante            | Cuando la recepción original afecta al negocio.  |
| evidenceReferences | Referencias tipadas | Según requisitos del acto.                       |

El operationId del contexto identifica una operación de negocio. El operationId de OpenAPI identifica una operación
técnica; son conceptos distintos.

El proveedor obtiene actor y servicio de la autenticación o de una delegación verificable.

originalReceivedAt no permite antedatar decisiones ni sustituye la recepción efectiva del proveedor.

### 8.1. Referencias comunes

ObjectReference contiene:

- objectType.

- objectId.

- sourceService.

- version, cuando debe fijarse una versión.

VersionedReference contiene obligatoriamente objectId, version y sourceService.

DocumentReference contiene documentId y documentVersion.

Las referencias no conceden autorización ni transfieren la propiedad de los datos.

## 9. Consultas

Una consulta no crea reservas, preparaciones ni compromisos.

Puede utilizar POST cuando requiere un cuerpo complejo o protegido.

Cada respuesta identifica:

- Objeto y versión, cuando existen.

- Momento de evaluación o corte.

- Alcance autorizado.

- Completitud.

- Datos faltantes.

- Procedencia, cuando corresponde.

Una consulta histórica no autoriza una actuación actual.

### 9.1. Colecciones

Las colecciones utilizan paginación por cursor y orden estable.

El tamaño predeterminado es 50 elementos y el máximo 200.

El cursor es opaco y queda vinculado a filtros, orden y corte. Una continuación incompatible o vencida exige reiniciar
la consulta.

No se mezclan silenciosamente páginas de cortes diferentes.

## 10. Comandos y respuestas

| **HTTP** | **Significado**                                                       |
|----------|-----------------------------------------------------------------------|
| 200      | Operación completada, actualización o resultado existente recuperado. |
| 201      | Recurso creado.                                                       |
| 202      | Procesamiento aceptado durablemente y pendiente.                      |

Una aceptación durable requiere conservar lo necesario para continuar o recuperar.

El resultado común contiene:

| **Campo**         | **Obligatoriedad**                     |
|-------------------|----------------------------------------|
| actionId          | Obligatorio.                           |
| operationId       | Obligatorio.                           |
| processingStatus  | Obligatorio.                           |
| outcome           | Obligatorio; enumeración particular.   |
| receivedAt        | Obligatorio.                           |
| confirmedAt       | Cuando existe confirmación definitiva. |
| objectReferences  | Obligatorio; puede estar vacío.        |
| confirmedEffects  | Obligatorio; puede estar vacío.        |
| pendingComponents | Obligatorio; puede estar vacío.        |
| statusResource    | Obligatorio.                           |

processingStatus admite PENDING, COMPLETED y REQUIRES_REVIEW.

Cada pendiente contiene componentCode, responsibleService, reasonCode y una referencia cuando existe.

Las respuestas distinguen el efecto principal de sus regularizaciones y notificaciones posteriores.

COMPLETED no significa automáticamente aprobación, ejecución económica, entrega o cierre integral.

## 11. Errores

Los errores utilizan Problem Details.

| **Campo**     | **Contenido**                          |
|---------------|----------------------------------------|
| type          | Tipo estable del problema.             |
| title         | Descripción breve.                     |
| status        | Código HTTP.                           |
| detail        | Explicación autorizada.                |
| instance      | Referencia de la ocurrencia.           |
| code          | Código estable.                        |
| correlationId | Correlación.                           |
| actionId      | Cuando existe actuación registrada.    |
| violations    | Errores de campos, cuando corresponde. |

Cada violación identifica field, code y message.

| **HTTP** | **Uso**                                             |
|----------|-----------------------------------------------------|
| 400      | Estructura, tipo o formato inválido.                |
| 401      | Credencial ausente o inválida.                      |
| 403      | Permiso insuficiente.                               |
| 404      | Objeto no localizado dentro del alcance autorizado. |
| 409      | Conflicto de contenido, versión o estado.           |
| 422      | Impedimento de negocio.                             |
| 503      | Dependencia necesaria indisponible.                 |

Una evaluación completada puede responder 200 con conclusión negativa.

Una respuesta 503 no demuestra que una actuación remota no se haya ejecutado.

Los errores no exponen secretos, consultas SQL, trazas internas ni información reservada.

## 12. Idempotencia e integridad del contenido

Todo comando persistente utiliza actionId e Idempotency-Key.

La clave tiene entre 1 y 120 caracteres y se interpreta dentro de la identidad del productor.

| **Situación**                        | **Tratamiento**                  |
|--------------------------------------|----------------------------------|
| Misma actuación y contenido          | Recupera el resultado existente. |
| Misma clave con contenido diferente  | Conflicto.                       |
| Misma actuación con otra clave       | No repite el efecto.             |
| Actuación pendiente                  | Devuelve seguimiento.            |
| Rechazo definitivo                   | Conserva el rechazo.             |
| Nuevo intento bajo otras condiciones | Nueva actuación vinculada.       |

La unicidad del efecto económico complementa la idempotencia técnica.

### 12.1. Canonicalización

El procedimiento se identifica mediante canonicalizationVersion, con valor inicial 1.

Se aplica en este orden:

1.  Validar el contenido contra el esquema y rechazar propiedades desconocidas o claves JSON duplicadas.

2.  Construir el objeto vinculante definido por la operación.

3.  Normalizar los valores tipados según las reglas siguientes.

4.  Serializar mediante JSON Canonicalization Scheme, RFC 8785.

5.  Codificar el resultado en UTF-8 sin marca de orden de bytes.

6.  Calcular SHA-256 sobre esos bytes.

7.  Representar el resultado mediante 64 caracteres hexadecimales en minúsculas.

Las reglas de normalización son:

| **Dato**      | **Tratamiento**                                                                                      |
|---------------|------------------------------------------------------------------------------------------------------|
| UUID          | Minúsculas con guiones.                                                                              |
| Instante      | UTC con Z, segundos obligatorios y fracción sin ceros finales; se omite la fracción si es cero.      |
| Fecha         | YYYY-MM-DD.                                                                                          |
| Decimal       | Se eliminan ceros iniciales y ceros fraccionarios finales; se elimina el punto si no queda fracción. |
| Cero decimal  | 0, incluido cualquier cero con signo admitido.                                                       |
| Texto         | Se conserva exactamente; no se recorta ni se aplica normalización Unicode implícita.                 |
| Arreglo       | Se conserva el orden.                                                                                |
| Campo omitido | Permanece omitido.                                                                                   |
| null          | Solo se conserva cuando está permitido por el esquema.                                               |

Un contrato que trate una colección como conjunto debe definir su clave de orden antes de calcular la huella. No se
ordenan automáticamente todos los arreglos.

Los enteros que puedan superar el rango de representación exacta de JCS se incorporan al objeto vinculante como cadenas
decimales. Esta transformación se utiliza únicamente para calcular la huella; no modifica su tipo de intercambio.

### 12.2. Contenido vinculante

Cada operación define un bindingType y un objeto vinculante con:

- bindingType.

- schemaVersion.

- canonicalizationVersion.

- providerService.

- technicalOperationId.

- target, cuando existe un recurso destinatario.

- content, con los campos que determinan la decisión o el efecto.

La identidad del recurso de ruta forma parte de target.

Se incluyen importes, monedas, participantes, destinos, condiciones, versiones y evidencias cuando alteran el
significado de la actuación.

Se excluyen credenciales, trazas, correlación y encabezados de transporte.

La huella de idempotencia vincula además actionId, operationId y los restantes campos del contexto que afectan al
negocio.

Una huella de propuesta o aprobación utiliza el contenido de esa propuesta, sin incorporar la identidad de una
invocación posterior.

### 12.3. Archivos

El contentHash de un archivo se calcula directamente sobre sus bytes originales.

No se aplica canonicalización JSON al archivo ni se modifica su contenido antes de verificar integridad.

El manifiesto documental vincula referencias, versiones, hashes y orden de documentos mediante el procedimiento de
canonicalización común.

## 13. Concurrencia

Las modificaciones sensibles utilizan expectedVersion.

El proveedor comprueba la versión dentro del control local que confirma el cambio.

Cada contrato identifica:

- Recursos afectados.

- Alcance de unicidad.

- Actuaciones incompatibles.

- Resultado de la competencia.

- Cancelación admitida.

- Parcialidad permitida.

Una consulta previa no sustituye el control transaccional.

No se mantienen transacciones SQL abiertas durante llamadas HTTP, Kafka, almacenamiento de archivos o participantes
externos.

## 14. Preparaciones y decisiones

### 14.1. Solicitud

Cada preparación identifica:

- Contexto de actuación.

- coordinatorService.

- attemptId.

- decisionQueryReference.

- Acto, actores y objetos.

- Contenido vinculante y su huella.

- Importes y monedas pertinentes.

- Versiones necesarias.

- decisionDeadline.

- Evidencias.

decisionQueryReference contiene coordinatorService, attemptType y attemptId.

El destino se resuelve mediante configuración autorizada. No se aceptan direcciones arbitrarias.

La huella no sustituye los datos que necesita la autoridad para evaluar.

### 14.2. Respuesta

La respuesta identifica preparación, coordinador, intento, alcance, fundamentos, versiones, estado y consulta.

Los estados son:

- PREPARED.

- RESOLUTION_PENDING.

- CONCLUDED_COMMITTED.

- CONCLUDED_ABORTED.

- REJECTED.

Una preparación favorable debe estar persistida antes de informarse como utilizable.

### 14.3. Decisión y aborto

La consulta del intento devuelve PENDING, COMMITTED o ABORTED, junto con contenido, efectos, recursos y pendientes.

La solicitud de aborto devuelve:

- ABORTED.

- ALREADY_COMMITTED.

- PENDING.

Una referencia desconocida no constituye aborto confirmado.

La decisión y sus efectos locales se confirman conjuntamente. Confirmación y aborto compiten sobre el mismo intento.

### 14.4. Cambios y vencimientos

Un impedimento conocido anterior impide preparar.

Un cambio incompatible posterior bloquea nuevas preparaciones y solicita resolver los intentos existentes.

Las preparaciones no se liberan por timeout. El vencimiento inicia consulta, resolución y escalación.

La expiración del permiso temporal de un trabajador no determina la vigencia de una preparación.

## 15. Eventos

### 15.1. Sobre común

| **Campo**        | **Tipo**                 | **Obligatoriedad**                             |
|------------------|--------------------------|------------------------------------------------|
| eventId          | UUID                     | Obligatorio.                                   |
| type             | Código de tipo de evento | Obligatorio.                                   |
| schemaVersion    | Entero positivo          | Obligatorio.                                   |
| producerService  | Código de servicio       | Obligatorio.                                   |
| aggregateType    | Código                   | Obligatorio.                                   |
| aggregateId      | UUID                     | Obligatorio.                                   |
| aggregateVersion | Versión                  | Cuando el agregado utiliza versión.            |
| sequence         | Secuencia                | Cuando el contrato requiere orden comprobable. |
| occurredAt       | Instante                 | Obligatorio.                                   |
| operationId      | UUID                     | Obligatorio.                                   |
| actionId         | UUID                     | Cuando existe actuación.                       |
| correlationId    | Texto                    | Obligatorio.                                   |
| causationId      | UUID                     | Cuando existe causa.                           |
| payload          | Objeto tipado            | Obligatorio.                                   |

El nombre del discriminador común es type. Los mensajes no utilizan eventType como alternativa para ese campo.

La identidad del esquema es la combinación de type y schemaVersion.

### 15.2. Payload

Cada payload define nombres de campos, tipos, obligatoriedad, condiciones, enumeraciones y límites.

Una descripción conceptual del contenido no sustituye esa definición.

Los cambios diferentes dentro de un mismo evento utilizan un discriminador particular y variantes excluyentes.

Los eventos no transportan objetos abiertos de negocio, archivos ni expedientes completos.

### 15.3. Canales y orden

Cada contrato identifica tema exacto, productor, consumidores, clave, orden y recuperación.

La clave corresponde al agregado definido por el contrato.

No existe orden global entre temas o claves.

Si se requiere detectar huecos, el evento incluye sequence y el consumidor recupera el tramo faltante o un estado
autoritativo al corte antes de continuar los efectos dependientes.

### 15.4. Entrega

Los productores utilizan outbox con eventId estable. Los consumidores con efectos locales utilizan inbox.

Una reentrega conserva la identidad del evento.

Si un consumidor necesita un efecto remoto, registra intención y ejecuta una actuación idempotente fuera de su
transacción local.

Un mensaje inválido conserva causa y seguimiento. Apartarlo del procesamiento no significa aplicarlo.

## 16. Seguridad

Las interfaces protegidas utilizan tokens verificables emitidos por Keycloak.

El proveedor comprueba emisor, destinatario, vigencia, permisos y alcance sobre el objeto.

Cada operación identifica servicios admitidos, finalidad, actor y delegación cuando corresponda.

Un permiso técnico no concede representación, crédito ni facultades sobre productos.

Los controles se aplican también a consultas históricas y recuperación idempotente.

La recepción pública o asistida de consultas, reclamos y denuncias utiliza las excepciones limitadas definidas en
Atención y operación administrativa. Recibir una presentación no concede acceso a información protegida.

## 17. Documentos y contenido binario

Las interfaces ordinarias intercambian referencias documentales.

Documentos y comprobantes administra recepción, carga, integridad y acceso binario.

Cada modalidad de carga define método, ruta o mecanismo autorizado, tipo de contenido, límite de tamaño, vigencia,
identificación y consulta de recuperación.

Un destino temporal no sustituye una referencia documental ni concede acceso general al almacenamiento.

Una carga no se presenta como documento íntegro hasta comprobar contenido, tamaño y hash.

La recepción documental y la confirmación del hecho de negocio se informan por separado.

## 18. Seguimiento y recuperación

### 18.1. Consulta por identidad original

Toda operación persistente debe poder recuperarse utilizando una identidad que el solicitante conozca antes de enviarla.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su operationId técnico es getActionStatus.

La consulta se dirige al servicio proveedor original. No constituye un servicio central de actuaciones.

Devuelve:

- Identidad y tipo de actuación.

- Operación de negocio.

- Estado de procesamiento.

- Resultado conocido.

- Objetos creados.

- Intentos relacionados.

- Efectos confirmados.

- Pendientes.

- Referencias de consultas especializadas.

Las rutas particulares de seguimiento se conservan y complementan esta consulta común.

### 18.2. Actuación no localizada

Un 404 significa que la actuación no está localizada dentro del alcance autorizado en ese momento.

No demuestra que una solicitud en tránsito no pueda llegar después.

Una reentrega autorizada conserva actionId, clave y contenido. Si llega la solicitud original, ambas recuperan la misma
actuación.

Cuando se necesita garantizar que una petición tardía no se ejecute, se utiliza el mecanismo de aborto o cancelación de
su contrato. La ausencia de registro no sustituye esa garantía.

### 18.3. Tiempos

Las llamadas ordinarias utilizan dos segundos de conexión y cinco segundos de respuesta, salvo especificación
particular.

Las consultas seguras admiten como máximo dos intentos totales dentro del presupuesto completo.

Los comandos no se reintentan automáticamente por fallas de comunicación. Primero se consulta la actuación original.

Los plazos de negocio y escalación prevalecen sobre las esperas técnicas.

## 19. Compatibilidad

Requieren revisión de compatibilidad los cambios en:

- Campos obligatorios.

- Significado y resultados.

- Precisión y escala.

- Enumeraciones.

- Autorización.

- Firmeza.

- Idempotencia.

- Canonicalización.

- Recuperación.

- Rutas y temas.

Los cambios incompatibles utilizan una versión diferente y contemplan actuaciones y mensajes pendientes.

Un campo opcional nuevo solo es compatible si ignorarlo no altera la interpretación.

Un resultado desconocido no se interpreta como éxito.

Los cambios de condiciones comerciales no se confunden con versiones técnicas.

## 20. Validación de contratos

La validación comprueba:

- Correspondencia funcional.

- Autoridad del proveedor.

- Datos disponibles en los consumidores.

- Campos, tipos y condiciones de presencia.

- Resultados y errores.

- Idempotencia y unicidad económica.

- Concurrencia.

- Consulta por identidad original.

- Preparaciones y decisiones.

- Autorización.

- Payloads y consumidores.

- Coincidencia de rutas y temas con la arquitectura.

- Compatibilidad entre módulos.

Las pruebas de hashes incluyen contenido equivalente con distinta representación decimal, diferente orden de
propiedades, texto significativo y arreglos cuyo orden debe conservarse.

Los ejemplos deben satisfacer sus propios esquemas.

La validación estructural de OpenAPI, AsyncAPI y JSON Schema complementa la revisión funcional.

## 21. Contenido de los apartados de módulo

Cada apartado incluye:

- Servicios y capacidades.

- Catálogo de operaciones con identificadores técnicos.

- Esquemas completos de entrada y salida.

- Interfaces HTTP.

- Preparaciones y seguimiento.

- Eventos con payloads tipados.

- Seguridad.

- Concurrencia y recuperación.

- Ejemplos y escenarios de aceptación.

Las estructuras compartidas se utilizan con su nombre y significado comunes.

Cada interfaz concreta sus datos y resultados sin redefinir el negocio ni trasladar responsabilidades entre servicios.
