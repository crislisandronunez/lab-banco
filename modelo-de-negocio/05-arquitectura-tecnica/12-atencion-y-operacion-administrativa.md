# Diseño técnico — Módulo 12. Atención y operación administrativa

## 1. Propósito y alcance

El módulo administra consultas, reclamos, actuaciones humanas, documentos, comunicaciones y extractos.

Conserva la relación entre:

- Presentación y recepción.

- Investigación y decisión.

- Aprobación y ejecución.

- Documento y evidencia.

- Envío y entrega.

- Información de origen y contenido emitido.

- Conservación, preservación y disposición documental.

Una resolución favorable no demuestra reparación ejecutada. Una aprobación no ejecuta una operación económica. Un envío
no demuestra recepción.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Reclamos | complaint-service | complaint_db | Casos de atención, investigaciones coordinadas, resoluciones y reparaciones. |
| Gestión administrativa | administrative-case-service | administrative_case_db | Consultas, procedimientos, expedientes, tareas y aprobaciones. |
| Documentos y comprobantes | document-service | document_db | Archivos, versiones, comprobantes, conservación y disposición. |
| Comunicaciones | communication-service | communication_db | Plantillas, preferencias, envíos y evidencias de entrega. |
| Extractos | statement-service | statement_db | Composición, controles, emisión y rectificación de extractos. |

Cada servicio administra PostgreSQL, usuario y migraciones propios.

Documentos y comprobantes administra exclusivamente el acceso al almacenamiento privado en MinIO. Los demás servicios
utilizan sus interfaces y referencias documentales.

No existen consultas SQL ni relaciones JPA entre bases.

### 2.1. Límites

| Información o decisión | Responsable |
| --- | --- |
| Identidad y contactos | Personas. |
| Representación | Representación. |
| Facultades institucionales | Institución. |
| Investigación especializada | Servicio competente. |
| Reparación económica | Propietario de la operación. |
| Contenido contractual y aceptación | Contratos. |
| Movimientos y saldos | Fondos. |
| Deuda y posiciones de productos | Producto correspondiente. |
| Resumen de tarjeta | Cuenta de tarjeta. |
| Información financiera del banco | Información contable. |

El módulo no modifica directamente contratos, saldos, deuda, tenencias ni asientos ajenos.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador interno | UUID | uuid | Cadena UUID. |
| Versión o secuencia | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha | LocalDate | date | YYYY-MM-DD. |
| Importe informado | BigDecimal | numeric(24,8) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Título | String | varchar(200) | Cadena. |
| Descripción o fundamento | String | varchar(4000) | Cadena. |
| Tamaño de archivo | long | bigint | Entero de bytes. |
| Resumen de contenido | String | char(64) | SHA-256 hexadecimal. |

Los importes son información recibida del responsable económico. No se utilizan como una segunda posición financiera.

La zona institucional es America/Argentina/Buenos_Aires.

Los campos extensos que excedan la descripción se incorporan como documentos o antecedentes identificados.

### 3.2. Actuaciones

Los comandos incluyen actuación, operación, finalidad, causa, objeto, versión esperada y evidencias.

La recepción original se conserva cuando afecta plazos.

El actor y el servicio emisor proceden de la autenticación.

Las respuestas distinguen PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

Cada resultado identifica su dimensión: recepción, decisión, ejecución, conservación, emisión o entrega.

### 3.3. Recepción y acceso

Se reciben consultas, reclamos y denuncias aunque falte verificar identidad o representación.

La recepción:

- Conserva fecha, canal y contenido.

- Entrega una referencia.

- Identifica comprobaciones pendientes.

- No concede acceso a información protegida.

- No autoriza reparaciones económicas.

Las denuncias urgentes se derivan sin esperar la finalización del trámite ordinario.

## 4. Consultas y reclamos

### 4.1. Consulta

Gestión administrativa conserva:

- Presentante.

- Canal.

- Recepción.

- Motivo.

- Información solicitada.

- Fuentes consultadas.

- Respuesta.

- Comprobaciones de acceso.

- Expediente o reclamo derivado.

La información pública puede responderse sin identificar una cuenta.

Si la consulta expresa disconformidad, se deriva a Reclamos conservando la recepción original.

Una fuente indisponible no se sustituye por una respuesta supuesta.

### 4.2. Reclamo

| Campo | Contenido |
| --- | --- |
| complaintId | Identidad estable. |
| complaintReference | Referencia visible única. |
| presenterReference | Presentante declarado o identificado. |
| representedPartyId | Persona por cuya cuenta actúa, cuando corresponde. |
| identityVerificationStatus | Situación de identificación. |
| receivedAt | Recepción original. |
| channelCode | Canal. |
| reasonCode | Clasificación. |
| description | Disconformidad. |
| affectedReferences | Productos y operaciones. |
| claimedAmount / currency | Importe cuando corresponda. |
| specializedProcedureReference | Procedimiento particular aplicable. |
| dueAt | Vencimiento. |
| version | Concurrencia. |

Las presentaciones relacionadas conservan identidades y fechas. Vincularlas no elimina su evidencia.

No se exige al presentante conocer el identificador técnico de una operación.

### 4.3. Estados separados

| Dimensión | Estados |
| --- | --- |
| Atención | Recibido, clasificado, en investigación, resuelto o reabierto. |
| Resolución | Pendiente, favorable, parcialmente favorable o desfavorable. |
| Reparación | No requerida, pendiente, parcial o confirmada. |
| Comunicación | Pendiente, entregada, fallida o incierta. |
| Finalización | Abierto o cerrado integralmente. |

Resolver no equivale a cerrar integralmente.

### 4.4. Plazos

| Actuación | Plazo |
| --- | --- |
| Constancia | En el acto para canales digitales y atención directa. |
| Clasificación y asignación | Hasta finalizar el siguiente hábil. |
| Respuesta ordinaria definitiva | Diez días hábiles. |
| Aviso de proximidad | Dos días hábiles antes. |
| Escalación | Inmediata al vencer sin resolución. |

Se cuenta desde el día siguiente a la recepción utilizando Calendarios bancarios.

Los procedimientos especializados conservan sus plazos particulares.

La reasignación, reclasificación o ampliación del mismo problema no reinicia el plazo.

Las tareas internas vencen, como máximo, el hábil anterior al de resolución, salvo urgencia que exija actuar antes.

### 4.5. Investigación y reparación

Reclamos solicita antecedentes a sus propietarios y conserva contradicciones y pendientes.

No exige al cliente documentación que el banco ya conserva y puede recuperar.

La resolución identifica fundamentos y acciones por concepto.

Las resoluciones desfavorables y reparaciones discrecionales requieren preparación y aprobación separadas.

Cada reparación tiene:

- Referencia estable.

- Propietario ejecutor.

- Operación original.

- Conceptos e importes.

- Fundamento.

- Resultado esperado.

- Confirmaciones.

- Remanente.

Si otro procedimiento ya restituyó el mismo efecto, se referencia esa ejecución. No se ordena otra devolución.

Una respuesta interna sin evidencia suficiente no permite afirmar cumplimiento.

### 4.6. Cierre y recurrencia

El cierre comprueba resolución, tratamiento de reparaciones y comunicación.

La reapertura conserva resolución y fechas anteriores.

El análisis de recurrencia es mensual, con consolidación trimestral.

Las fallas generales originan actuaciones sobre otros afectados potenciales, sin limitarse a quienes reclamaron.

El cierre no levanta automáticamente preservaciones documentales.

## 5. Gestión administrativa

### 5.1. Procedimiento y expediente

El procedimiento define finalidad, entradas, tareas, responsables, plazos, escalaciones y finalización.

Sus versiones distinguen recopilar, revisar, aprobar y ejecutar.

El expediente conserva:

- Procedimiento y versión.

- Origen.

- Objeto de negocio.

- Participantes.

- Tareas.

- Documentos.

- Decisiones.

- Actuaciones externas.

- Pendientes.

- Causa de finalización.

No se habilita un procedimiento sin responsable de conclusión.

Los procesos especializados permanecen en sus servicios. Gestión administrativa coordina únicamente sus intervenciones
humanas.

### 5.2. Tareas

Cada tarea identifica:

- Expediente.

- Tipo.

- Objeto.

- Resultado requerido.

- Función habilitada.

- Responsable asignado.

- Vencimiento.

- Estado.

- Historial.

- Versión.

Los estados son pendiente, asignada, en tratamiento, completada, cancelada o vencida.

Vencer no completa la tarea.

La reasignación conserva responsables anteriores.

### 5.3. Aprobación

La aprobación contiene:

- Identidad.

- Objeto y versión.

- Tipo de decisión.

- Resumen exacto del contenido.

- Preparador.

- Aprobador.

- Facultad utilizada.

- Alcance.

- Fecha.

- Vigencia.

- Condiciones.

- Estado.

El preparador no aprueba cuando se exige separación.

Cambiar el contenido requiere nueva aprobación.

El servicio de negocio consulta su correspondencia antes de utilizarla y registra la ejecución con su referencia
original.

Una aprobación permite ejecutar únicamente dentro del alcance y condiciones verificadas. No sustituye las comprobaciones
operativas posteriores.

### 5.4. Actuaciones externas

Se conservan participante simulado, finalidad, documentos, referencia de envío y resultado.

El envío no demuestra cumplimiento.

Las actuaciones judiciales o administrativas simuladas mantienen su fundamento y se derivan al servicio competente
cuando generan medidas o efectos económicos.

## 6. Documentos y comprobantes

### 6.1. Documento y versión

| Campo | Contenido |
| --- | --- |
| documentId | Identidad lógica. |
| documentVersionId | Identidad inmutable de versión. |
| documentType | Categoría. |
| ownerService | Responsable del significado de negocio. |
| sourceReference | Proceso o hecho de origen. |
| informationSubjects | Titulares de la información. |
| purpose | Finalidad. |
| classification | Nivel de acceso. |
| mediaType | Tipo de contenido validado. |
| sizeBytes | Tamaño. |
| contentHash | SHA-256. |
| storageObjectReference | Referencia interna, no expuesta al consumidor. |
| createdAt | Creación. |
| integrityStatus | Pendiente, comprobada o incidente. |
| retentionApplications | Fundamentos de conservación. |

Una nueva versión no sobrescribe otra.

Las referencias de firma y autenticidad se conservan separadamente del resultado de comprobación.

Verificar una firma no concede facultad contractual.

### 6.2. Ingreso y almacenamiento

El ingreso sigue un proceso persistente:

- Registrar intención y metadatos.

- Recibir el archivo en área temporal privada.

- Comprobar tamaño, tipo permitido e integridad.

- Aplicar las comprobaciones de contenido configuradas.

- Confirmar el objeto y su versión.

- Confirmar metadatos y disponibilidad.

- Publicar el resultado.

Los archivos temporales o no comprobados no se presentan como evidencia disponible.

PostgreSQL y MinIO no comparten transacción. Los estados intermedios permiten recuperar cargas incompletas y objetos sin
confirmación.

El acceso a MinIO pertenece exclusivamente a Documentos.

### 6.3. Emisión

El comprobante o certificado recibe datos confirmados por el propietario y conserva:

- Tipo y plantilla.

- Versión.

- Fuente.

- Corte o fecha.

- Hecho acreditado.

- Condición recibida, pendiente o confirmada.

- Documento generado.

- Resumen.

- Actuación de emisión.

La generación no transforma una petición en operación ejecutada.

Contratos determina contenido contractual; Extractos compone extractos; Cuenta de tarjeta compone sus resúmenes.

Documentos conserva y representa esos contenidos sin recalcularlos.

### 6.4. Consulta

Se comprueban identidad, representación, finalidad y clasificación.

El servicio puede producir una copia parcial o disociada, identificada como derivada y vinculada al original.

La autorización no se deriva del conocimiento de un UUID.

Las descargas utilizan acceso autenticado mediante Documentos. Los enlaces temporales, cuando se utilicen, tienen
alcance y vigencia limitados y no sustituyen la referencia documental.

## 7. Conservación, preservación y disposición

### 7.1. Política

La política contiene categoría, fundamento, plazo, hecho inicial, protección, actuaciones permitidas, alcance histórico
y aprobación.

Se aplican estas bases:

| Categoría | Plazo base | Inicio |
| --- | --- | --- |
| Contratos y aceptaciones | Diez años | Último entre terminación y resolución de obligaciones. |
| Comprobantes y evidencia de operaciones | Diez años | Confirmación o resolución definitiva. |
| Identificación y representación | Diez años | Última finalización aplicable de relación o actuación. |
| Reclamos y reparaciones | Diez años | Cierre definitivo y conclusión de actuaciones. |
| Expedientes y aprobaciones | Diez años | Finalización y resolución de consecuencias. |
| Comunicaciones obligatorias | Diez años | Entrega o conclusión de incidencia, sin reducir conservación asociada. |
| Extractos y rectificaciones | Diez años | Emisión, conservando versiones relacionadas. |
| Publicidad opcional sin efecto contractual | Dos años | Finalización de campaña o último envío. |

Una obligación más extensa o preservación vigente prevalece.

Cada aplicación conserva política, versión y hecho inicial. Si ese hecho no ocurrió, el plazo no se considera vencido.

La fecha permitida de disposición es la más tardía de los fundamentos aplicables.

### 7.2. Preservación

Cada preservación identifica causa, autoridad, documentos o ámbito, vigencia y condición de levantamiento.

Las solicitudes por ámbito generan una regla de bloqueo y una identificación de documentos alcanzados.

La aplicación no se informa completa hasta comprobar cobertura y disposiciones en curso.

Levantar una preservación no elimina las demás.

Una categoría desconocida impide disponer y abre tarea de clasificación.

### 7.3. Disposición

Los estados distinguen:

- Candidato.

- Revisión pendiente.

- Autorizado.

- Bloqueado.

- Ejecución preparada.

- Ejecución en curso.

- Eliminación del almacenamiento principal comprobada.

- Copias pendientes.

- Completado.

- Incidencia.

Antes de autorizar y antes de ejecutar se revisan políticas, requisitos, preservaciones y aprobación.

La preparación de preservaciones y disposiciones se ordena sobre el mismo control documental.

Si una eliminación ya está en curso, una nueva solicitud de preservación queda pendiente de comprobación: no se afirma
que se preservó contenido que pudiera haber sido eliminado. Se detienen las partes todavía cancelables y se registra
cualquier incompatibilidad para su tratamiento.

No se mantiene una transacción SQL abierta durante la operación de MinIO.

### 7.4. Copias y restauración

La disposición identifica objetos, versiones y copias gestionadas.

El ciclo de respaldo debe conservar una fecha comprobable de expiración o purga de las copias alcanzadas. Mientras esa
fecha o su cumplimiento no estén acreditados, la disposición sigue pendiente de copias.

La restauración aplica el registro de disposiciones antes de habilitar documentos recuperados.

La constancia conserva solo identidad, alcance, fundamento, autorización y resultado necesarios, sin reproducir el
contenido eliminado.

La disposición documental no elimina registros económicos de otros servicios.

### 7.5. Incidentes

Un archivo corrupto se retira de uso como evidencia íntegra.

La recuperación compara el contenido con su resumen esperado.

Si solo puede reconstruirse desde datos operativos, la nueva evidencia se identifica como reconstruida. No se presenta
como original.

La pérdida se comunica al proceso responsable y a Riesgos.

## 8. Comunicaciones

### 8.1. Plantilla y contenido

La plantilla define finalidad, destinatarios, canal, campos tipados, fuentes, clasificación y versión.

El propietario funcional aprueba el significado de negocio.

Las plantillas contractuales utilizan contenido validado por Contratos.

Un campo obligatorio faltante impide enviar un mensaje cuyo significado quedaría alterado.

No se incluyen credenciales completas ni datos sensibles innecesarios.

### 8.2. Preferencias

Las preferencias identifican participante, finalidad, canal, idioma, accesibilidad, consentimiento y vigencia.

Personas conserva contactos. Comunicaciones conserva la selección y la evidencia utilizada.

La publicidad requiere autorización diferenciada.

Revocarla impide nuevos envíos opcionales. Las comunicaciones obligatorias continúan por canales admitidos.

Un contacto no verificado no recibe automáticamente información sensible.

### 8.3. Envío

Cada solicitud contiene:

- Identidad y origen.

- Tipo.

- Finalidad.

- Destinatarios.

- Plantilla y versión.

- Datos.

- Documentos y versiones.

- Clasificación.

- Plazo de utilidad.

- Canales admitidos.

Cada intento conserva el destino concreto utilizado. Un cambio posterior del contacto no altera el historial.

Los estados separan preparado, enviado, aceptado por proveedor, puesto a disposición, entregado, rechazado e incierto.

La evidencia especifica qué hecho acredita. La aceptación del proveedor no demuestra recepción por el destinatario.

### 8.4. Recuperación

Ante incertidumbre se consulta antes de reenviar.

Una falla transitoria comprobada permite reintentos a los cinco y treinta minutos.

Si persiste, se utiliza otro canal verificado y autorizado. Sin alternativa suficiente, se abre atención asistida para
el siguiente hábil o antes si el vencimiento lo exige.

Los reintentos cesan si el mensaje perdió finalidad o fue sustituido.

La revocación de publicidad se comprueba antes del despacho de nuevos envíos opcionales.

Los resultados se comunican al servicio de origen, que decide sus consecuencias contractuales o económicas.

### 8.5. Entorno de entrega

El correo local utiliza Mailpit.

Los demás canales se representan mediante adaptadores simulados con referencias, estados y evidencias explícitas.

Ningún adaptador puede marcar entrega al destinatario basándose únicamente en haber aceptado el envío.

## 9. Extractos

### 9.1. Modalidades

Se administran:

- Extracto mensual de cuentas.

- Informe mensual de inversión.

- Consolidado mensual solicitado.

- Constancia a fecha autorizada.

El período mensual es calendario.

El corte es el inicio del mes siguiente, excluido del período informado.

La emisión ordinaria se realiza dentro de los dos primeros días hábiles siguientes.

Los resúmenes de tarjeta continúan bajo Cuenta de tarjeta.

### 9.2. Composición

La ejecución conserva:

- Titulares autorizados.

- Productos.

- Período.

- Corte.

- Fuentes y versiones.

- Movimientos.

- Saldos.

- Posiciones.

- Monedas.

- Referencias de valuación.

- Controles.

- Limitaciones.

Las fuentes se consultan mediante sus interfaces y cortes estables.

Una proyección local derivada de eventos puede acelerar la composición, pero debe comprobar completitud frente al corte
y totales de origen.

### 9.3. Controles

Se comprueba:

$SaldoFinal = SaldoInicial + Créditos - Débitos$

Las reservas no se presentan como débitos.

El descubierto conserva límite, utilización y capacidad.

Los depósitos separan capital, actualización y rendimiento.

Las inversiones separan cantidad, valuación, disponibilidad y tránsito.

Las monedas se presentan por separado. La conversión consolidada es informativa y referencia cotización.

No se alteran datos para cuadrar.

### 9.4. Emisión y rectificación

Los estados separan composición, validación, documento, emisión y entrega.

Los faltantes que impiden integridad mantienen pendiente la emisión completa.

Una entrega parcial excepcional exige autorización y limitaciones visibles. No sustituye el extracto completo pendiente.

La emisión conserva contenido, fuentes y documento inmutables.

La rectificación identifica original, motivo, diferencias y corrección de origen.

Los movimientos posteriores no se incorporan indebidamente al corte anterior.

Si cambia un saldo final, se revisa la continuidad con períodos siguientes.

## 10. Interfaces HTTP

Las interfaces utilizan JSON y OpenAPI. Los comandos incluyen el contexto común.

### 10.1. Reclamos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-12-002 | POST /complaints | Presentación, canal y recepción. Devuelve caso y constancia. |
| CI-12-003 | POST /complaints/{id}/investigation-actions | Antecedentes y responsables. Devuelve investigación coordinada. |
| CI-12-004 | POST /complaints/{id}/resolutions | Conclusión, acciones y aprobación. Devuelve decisión y reparaciones solicitadas. |
| CI-12-005 | POST /complaint-repairs/{id}/results | Ejecutor, efecto y evidencia. Devuelve cumplimiento. |
| CI-12-006 | POST /complaints/{id}/lifecycle-actions | Comunicación, cierre o reapertura tipados. Devuelve situación. |
| CI-12-007 | GET /complaints/{id} | Devuelve actuaciones y pendientes permitidos. |
| CI-12-008 | POST /complaint-cause-analyses | Período y clasificación. Devuelve recurrencia y acciones. |

### 10.2. Gestión administrativa

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-12-001 | POST /administrative-enquiries | Consulta y recepción. Devuelve respuesta o expediente. |
| CI-12-009 | POST /administrative-procedures | Definición y responsables. Devuelve procedimiento. |
| CI-12-009 | POST /administrative-procedures/{id}/actions | Revisión y aprobación. Devuelve versión habilitada. |
| CI-12-010 | POST /administrative-cases | Procedimiento y objeto. Devuelve expediente. |
| CI-12-010 | POST /administrative-cases/{id}/updates | Antecedentes y cambios permitidos. Devuelve historial. |
| CI-12-011 | POST /administrative-tasks | Objeto, función y plazo. Devuelve tarea. |
| CI-12-011 | POST /administrative-tasks/{id}/assignments | Responsable y causa. Devuelve asignación. |
| CI-12-012 | POST /administrative-tasks/{id}/decisions | Revisión o aprobación exacta. Devuelve decisión. |
| CI-12-013 | POST /administrative-approval-checks | Aprobación, objeto y contenido. Devuelve correspondencia y vigencia. |
| CI-12-014 | POST /administrative-tasks/{id}/execution-actions | Ejecución, escalación o conclusión. Devuelve resultado. |
| CI-12-015 | POST /administrative-external-actions | Participante y finalidad. Devuelve seguimiento simulado. |
| CI-12-016 | GET /administrative-cases/{id} | Devuelve expediente autorizado. |
| CI-12-016 | GET /administrative-tasks/{id} | Devuelve tarea e historia. |
| CI-12-016 | GET /administrative-procedures/{id} | Devuelve versión y requisitos. |

### 10.3. Documentos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-12-017 | POST /document-ingestions | Metadatos y contenido o mecanismo de carga. Devuelve actuación. |
| CI-12-017 | POST /document-ingestions/{id}/completion | Confirmación de carga e integridad. Devuelve versión conservada. |
| CI-12-018 | POST /document-issuances | Tipo, datos confirmados y fuente. Devuelve comprobante o certificado. |
| CI-12-019 | GET /documents/{id}/versions/{version} | Devuelve metadatos y evidencia. |
| CI-12-019 | GET /document-versions/{id}/content | Acceso autorizado al archivo o copia derivada. |
| CI-12-020 | POST /document-retention-policies | Categorías, plazos y fundamentos. Devuelve propuesta. |
| CI-12-020 | POST /document-retention-policies/{id}/actions | Aprobación y aplicación. Devuelve versión y alcance. |
| CI-12-021 | POST /document-preservations | Causa, ámbito y autoridad. Devuelve bloqueo y pendientes. |
| CI-12-021 | POST /document-preservations/{id}/releases | Fundamento y autoridad. Devuelve levantamiento. |
| CI-12-022 | POST /document-dispositions | Candidatos y fundamento. Devuelve revisión. |
| CI-12-022 | POST /document-dispositions/{id}/execution-actions | Aprobación y alcance. Devuelve eliminación y copias pendientes. |
| CI-12-023 | POST /document-recovery-actions | Incidencia y evidencia. Devuelve recuperación o reconstrucción. |
| CI-12-024 | GET /document-actions/{actionId} | Recupera resultado documental. |

### 10.4. Comunicaciones

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-12-025 | POST /communication-templates | Campos, canales y contenido. Devuelve propuesta. |
| CI-12-025 | POST /communication-templates/{id}/actions | Aprobación y habilitación. Devuelve versión. |
| CI-12-026 | POST /communication-preference-actions | Participante, finalidad y consentimiento. Devuelve preferencias. |
| CI-12-027 | GET /communication-preferences | Persona y finalidad. Devuelve canales admitidos. |
| CI-12-028 | POST /communications | Destinatarios, contenido y documentos. Devuelve envío. |
| CI-12-029 | POST /communications/{id}/receipts | Constancia y origen. Devuelve resultado acreditado. |
| CI-12-030 | POST /communications/{id}/recovery-actions | Consulta, reintento o alternativa. Devuelve seguimiento. |
| CI-12-031 | GET /communications/{id} | Devuelve intentos y entregas. |

### 10.5. Extractos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-12-032 | POST /statement-definitions | Modalidad, fuentes y controles. Devuelve definición. |
| CI-12-032 | POST /statement-definitions/{id}/actions | Aprobación y habilitación. Devuelve versión. |
| CI-12-033 | POST /statement-compositions | Titularidad, período y productos. Devuelve composición y controles. |
| CI-12-034 | POST /statement-compositions/{id}/issuances | Composición validada. Devuelve emisión y entrega. |
| CI-12-035 | POST /statements/{id}/rectifications | Causa y fuentes corregidas. Devuelve nueva versión. |
| CI-12-036 | GET /statements/{id} | Devuelve emisión conservada. |
| CI-12-036 | GET /statements/{id}/versions | Devuelve historia rectificativa. |

Las modalidades tienen esquemas excluyentes.

Las respuestas utilizan 200, 201 o 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 11. Eventos

El sobre común contiene identidad, esquema, emisor, agregado, versión, operación, actuación, causa, correlación y
fechas.

| Contrato | Evento | Contenido |
| --- | --- | --- |
| CI-12-037 | ComplaintChanged | Caso, resolución, reparación y comunicación. |
| CI-12-038 | AdministrativeActionChanged | Tarea, aprobación, ejecución y pendientes. |
| CI-12-039 | DocumentActionChanged | Versión, emisión, integridad, preservación o disposición. |
| CI-12-040 | CommunicationOutcomeChanged | Destinatario, intento, evidencia y resultado. |
| CI-12-041 | StatementIssuedOrRectified | Emisión, corte, versión y documento. |

Los temas son:

- banking.complaint.events.

- banking.administrative-case.events.

- banking.document.events.

- banking.communication.events.

- banking.statement.events.

La clave es el agregado.

Los eventos no incluyen archivos ni expedientes completos.

Las evidencias de entrega identifican documento, versión y destinatario para que Contratos, Cuenta de tarjeta u otro
propietario determine su consecuencia.

## 12. Persistencia y recuperación

| Servicio | Persistencia principal |
| --- | --- |
| Reclamos | Casos, presentaciones, investigaciones, resoluciones y reparaciones. |
| Administración | Procedimientos, expedientes, tareas, decisiones y aprobaciones. |
| Documentos | Metadatos, versiones, objetos, políticas, preservaciones y disposiciones. |
| Comunicaciones | Plantillas, preferencias, consentimientos, envíos, intentos y constancias. |
| Extractos | Definiciones, fuentes, composiciones, controles y emisiones. |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Se exige:

- Presentación identificada sin perder recepción.

- Reparación económica reconocida una sola vez.

- Aprobación vinculada al contenido exacto.

- Versión documental inmutable.

- Emisión única por referencia.

- Envío lógico único por solicitud.

- Intentos externos identificados.

- Extracto único por identidad de emisión.

- Rectificaciones vinculadas.

- Disposición comprobable por objeto y copia.

Cada transacción confirma agregado, historial, resultado, idempotencia y outbox.

No mantiene bloqueos SQL durante llamadas HTTP, MinIO o proveedores de comunicación.

Los procesos conservan intención antes de invocar y recuperan mediante referencias originales.

Los consumidores confirman inbox y efecto local antes de avanzar el offset.

## 13. Seguridad y observabilidad

Keycloak autentica actores y servicios.

El acceso considera función, objeto, finalidad, representación y clasificación.

Se separan recepción pública o asistida, consulta privada, preparación, aprobación, ejecución y disposición.

Los expedientes reservados no se incorporan a respuestas al cliente por el solo hecho de estar vinculados a un reclamo.

PostgreSQL conserva la autoridad transaccional.

MinIO conserva archivos privados e inmutables por versión. Redis puede acelerar información de consulta, sin decidir
permisos, preservaciones o aprobaciones.

MongoDB no resulta necesario para estas responsabilidades.

Se observan:

- Reclamos próximos a vencer.

- Reparaciones sin confirmar.

- Tareas vencidas.

- Aprobaciones incompatibles.

- Cargas incompletas.

- Fallas de integridad.

- Preservaciones pendientes.

- Disposiciones con copias sin resolver.

- Entregas inciertas.

- Extractos incompletos.

- Retraso de outbox y recuperación.

Los logs no reproducen documentos, destinos completos ni información reservada.

## 14. Pruebas y correspondencia funcional

Se verifican reglas, permisos, concurrencia en PostgreSQL, integración con MinIO, contratos y recuperación.

Los escenarios incluyen:

- Reclamo recibido sin identidad comprobada.

- Derivación sin reiniciar plazo.

- Reparación ya realizada por una disputa.

- Preparador intentando aprobar.

- Contenido modificado después de aprobar.

- Caída entre carga de objeto y confirmación de metadatos.

- Documento corrupto.

- Preservación concurrente con disposición.

- Levantamiento con otra preservación vigente.

- Restauración con disposiciones ejecutadas.

- Proveedor que acepta sin acreditar entrega.

- Respuesta perdida de envío.

- Revocación publicitaria antes del despacho.

- Extracto con fuente incompleta.

- Rectificación sin repetir movimientos.

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-12-01. Consultas | Recepción y respuesta o derivación. | CI-12-001, 010 y 016. |
| OF-12-02. Registrar reclamo | Caso, recepción y clasificación. | CI-12-002. |
| OF-12-03. Investigar | Antecedentes y responsables. | CI-12-003. |
| OF-12-04. Resolver y reparar | Decisión y confirmación económica. | CI-12-004 y 005. |
| OF-12-05. Cerrar o reabrir | Resolución, ejecución y entrega. | CI-12-006 y 007. |
| OF-12-06. Recurrencia | Análisis y acciones generales. | CI-12-008. |
| OF-12-07. Procedimientos | Versiones y expedientes. | CI-12-009 y 010. |
| OF-12-08. Tareas y aprobaciones | Asignación, decisión y ejecución. | CI-12-011, 012, 013 y 014. |
| OF-12-09. Actuación externa | Seguimiento simulado. | CI-12-015 y 016. |
| OF-12-10. Recibir documentos | Ingreso e integridad. | CI-12-017. |
| OF-12-11. Comprobantes | Contenido de fuente y emisión. | CI-12-018. |
| OF-12-12. Consulta documental | Acceso y evidencia. | CI-12-019. |
| OF-12-13. Conservación | Políticas y aplicaciones. | CI-12-020. |
| OF-12-14. Preservar y disponer | Bloqueos y eliminación comprobada. | CI-12-021 y 022. |
| OF-12-15. Recuperar | Incidencias y reconstrucción identificada. | CI-12-023 y 024. |
| OF-12-16. Plantillas | Contenido y versiones. | CI-12-025. |
| OF-12-17. Preferencias | Consentimientos por finalidad. | CI-12-026 y 027. |
| OF-12-18. Enviar | Solicitud e intentos. | CI-12-028. |
| OF-12-19. Entrega | Evidencias y recuperación. | CI-12-029, 030, 031 y 040. |
| OF-12-20. Modalidades de extracto | Fuentes y controles. | CI-12-032. |
| OF-12-21. Componer | Cortes y continuidad. | CI-12-033. |
| OF-12-22. Emitir y entregar | Documento y comunicación. | CI-12-034 y 036. |
| OF-12-23. Rectificar | Nueva emisión vinculada. | CI-12-035 y 041. |
