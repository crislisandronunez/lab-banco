# Diseño técnico — Módulo 3. Contratación

## 1. Propósito y alcance

El módulo administra el recorrido entre la solicitud de un producto, la formación del acuerdo contractual y la
coordinación de su constitución operativa.

Comprende:

- Solicitudes de productos y antecedentes.

- Evaluación de viabilidad del trámite.

- Modelos contractuales.

- Propuestas y contenido íntegro.

- Entrega y aceptación.

- Celebración y efectos contractuales.

- Coordinación de constitución y habilitación.

- Modificación, renovación, arrepentimiento y terminación.

- Consultas históricas y constancias.

Solicitud, propuesta, contrato y producto operativo tienen identidades y estados diferentes.

La celebración no demuestra constitución, habilitación ni desembolso.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Solicitudes de productos | product-application-service | product_application_db | Trámite, antecedentes, requisitos, viabilidad y coordinación operativa. |
| Contratos | contract-service | contract_db | Modelos, propuestas, aceptaciones, celebración y efectos del acuerdo. |

Cada servicio administra su base PostgreSQL, usuario y migraciones.

No existen consultas SQL, relaciones JPA ni claves foráneas entre sus bases.

### 2.1. Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Identidad y datos personales | Personas. |
| Admisión y relación comercial | Vinculación y Clientes. |
| Representación general | Representación. |
| Facultades institucionales | Institución. |
| Condiciones comerciales | Condiciones comerciales. |
| Decisión crediticia | Evaluación crediticia. |
| Cálculos económicos | Servicio del producto. |
| Requisitos y seguimiento del trámite | Solicitudes de productos. |
| Contenido aceptado y efectos contractuales | Contratos. |
| Restricciones | Restricciones. |
| Constitución y capacidades del producto | Servicio operativo correspondiente. |
| Versiones documentales | Documentos y comprobantes. |
| Entrega y evidencias | Comunicaciones. |

Contratos conserva las condiciones económicas recibidas y su procedencia. No recalcula cuotas, deuda, intereses ni
liquidaciones de productos.

## 3. Convenciones de datos y actuaciones

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador interno | UUID | uuid | Cadena UUID. |
| Versión | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha | LocalDate | date | YYYY-MM-DD. |
| Importe | BigDecimal | numeric(24,8) | Cadena decimal. |
| Tasa o coeficiente | BigDecimal | numeric(24,12) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Nombre o título | String | varchar(200) | Cadena. |
| Fundamento | String | varchar(4000) | Cadena. |
| Resumen de contenido | String | char(64) | SHA-256 hexadecimal. |

Los importes no utilizan punto flotante.

Los calendarios se interpretan en America/Argentina/Buenos_Aires.

Un valor ausente no se representa mediante cero.

### 3.2. Contexto común

Cada comando incorpora:

| Campo | Obligación |
| --- | --- |
| actionId | Identidad obligatoria de la petición. |
| operationId | Operación de negocio obligatoria. |
| causationId | Cuando exista una actuación de origen. |
| purpose | Finalidad obligatoria. |
| reason | Para rechazos, excepciones, retiros y cambios que requieran fundamento. |
| expectedVersion | En modificaciones. |
| evidenceReferences | Según los requisitos de la operación. |
| originalReceivedAt | Cuando la recepción acreditada afecte un plazo. |

El actor y el servicio emisor proceden de la autenticación.

Una fecha declarada por el consumidor no permite antedatar una aceptación, una recepción ni una decisión.

### 3.3. Resultados

Los resultados técnicos son:

- PENDING.

- CONFIRMED.

- PARTIALLY_CONFIRMED.

- REJECTED.

- UNCERTAIN.

- CONFLICT.

La respuesta identifica actuación, objeto, versión, fechas, referencias y pendientes.

Aceptar una petición para procesamiento no equivale a aprobar una contratación.

## 4. Objetos compartidos

### 4.1. Evidencia

Una evidencia contiene identidad, tipo, propietario, referencia de origen, versión, momento, vigencia y resumen
documental cuando corresponda.

El receptor comprueba emisor, alcance y correspondencia.

Una referencia aportada por un consumidor no demuestra por sí sola que el requisito esté satisfecho.

### 4.2. Participante

Cada participante contractual identifica:

- Persona.

- Rol.

- Derechos y obligaciones.

- Necesidad de aceptación.

- Grupo de actuación conjunta.

- Actor interviniente.

- Representación utilizada.

Los datos incluidos en el documento contractual se conservan históricamente. Su copia no sustituye los datos actuales de
Personas.

### 4.3. Condición

Cada condición contiene:

- Identidad y código.

- Hecho exigido.

- Etapa.

- Responsable de acreditarlo.

- Referencia de origen.

- Fecha límite cuando corresponda.

- Consecuencia de incumplimiento.

- Estado.

- Evidencias.

Las etapas son:

- BEFORE_CELEBRATION.

- BEFORE_EFFECT.

- BEFORE_CONSTITUTION_OR_ENABLEMENT.

- BEFORE_DISBURSEMENT_OR_USE.

No se traslada un requisito obligatorio a una etapa posterior.

Una dispensa requiere que la condición sea dispensable y que exista aprobación competente.

## 5. Solicitudes de productos

### 5.1. Datos propios

La solicitud conserva:

- Identidad y referencia visible.

- Solicitante.

- Cliente cuando corresponda.

- Canal.

- Recepción.

- Finalidad.

- Participantes.

- Componentes solicitados.

- Antecedentes.

- Requisitos.

- Evaluaciones.

- Actuaciones contractuales.

- Coordinaciones operativas.

- Solicitud anterior relacionada.

- Versión.

Puede recibirse con vinculación pendiente. La celebración exige la relación comercial cuando sea necesaria.

### 5.2. Componentes

Cada componente identifica producto, versión, modalidad, moneda, importe cuando corresponda y participantes.

Conserva decisiones especializadas, condiciones por etapa, dependencias, propuesta, contrato y constitución.

Las dependencias forman un grafo sin ciclos.

Los componentes independientes mantienen resultados propios. Una falla no revierte productos ya constituidos de otros
componentes.

### 5.3. Estados

| Estado | Significado |
| --- | --- |
| RECEIVED | Petición registrada. |
| AWAITING_INFORMATION | Antecedentes del solicitante pendientes. |
| UNDER_REVIEW | Evaluación interna. |
| VIABLE | Puede avanzar dentro de sus condiciones. |
| REJECTED | Decisión desfavorable. |
| WITHDRAWN | Retiro anterior a contratación. |
| EXPIRED | Vencimiento procedente. |
| CONTRACTED | Contrato celebrado. |
| CONSTITUTION_PENDING | Constitución o habilitación pendiente. |
| COMPLETED | Resultado operativo previsto comprobado. |
| REPAIR_REQUIRED | Inconsistencia o imposibilidad que requiere tratamiento. |

El resultado global distingue parcialidades.

La reapertura crea un trámite vinculado. No reactiva aprobaciones, precios o aceptaciones vencidas.

### 5.4. Requerimientos

Cada requerimiento conserva puntos, responsables, puesta a disposición, calendario, vencimiento y presentaciones.

| Situación | Plazo o tratamiento |
| --- | --- |
| Requerimiento inicial | Treinta días corridos. |
| Presentación observada | Quince días corridos. |
| Inicio | Día siguiente a la puesta a disposición. |
| Final inhábil | Siguiente hábil. |
| Recordatorio | Cinco días corridos antes. |
| Canal indisponible al vencer | Hasta finalizar el segundo hábil posterior a recuperación. |

Una presentación oportuna impide declarar vencimiento mientras se revisa.

Los nuevos puntos derivados de una omisión interna reciben treinta días. No se imputan al solicitante como
incumplimiento anterior.

### 5.5. Viabilidad

Cada evaluación conserva versión y resumen de entradas, decisiones utilizadas, resultado, condiciones, vigencia y
fundamento.

Los resultados son favorable, favorable condicionada, desfavorable o pendiente.

Cambios de importe, moneda, participantes o modalidad requieren revisar las evaluaciones afectadas.

Solicitudes reúne decisiones. No concede crédito ni levanta restricciones.

## 6. Modelos contractuales

Cada modelo conserva versiones con:

- Productos compatibles.

- Régimen contractual.

- Roles.

- Conformidades.

- Condiciones generales.

- Campos particulares.

- Variaciones permitidas.

- Documentos y anexos.

- Requisitos.

- Reglas de efectos.

- Modificación.

- Renovación.

- Arrepentimiento.

- Terminación.

- Conformidad del banco.

- Formalización automática admitida.

Los regímenes son CONSUMER_BANKING, COMMERCIAL_FINANCING e INSTITUTIONAL.

### 6.1. Campos particulares

Cada campo identifica código, tipo, obligatoriedad, fuente, validación, valores admitidos y permiso de sustitución.

Los tipos son texto, fecha, instante, importe, tasa, entero, booleano, código o referencia.

Las condiciones particulares sustituyen una general únicamente cuando el modelo lo permite expresamente.

### 6.2. Versiones y aprobación

Los estados son:

DRAFT, PENDING_APPROVAL, APPROVED, ENABLED, REJECTED y WITHDRAWN.

Preparador y aprobador son personas distintas.

Una versión aprobada es inmutable.

El retiro impide nuevas propuestas. La continuidad de propuestas existentes requiere una decisión expresa sobre su
alcance.

## 7. Propuestas y contenido íntegro

La propuesta conserva solicitud, componente, finalidad, modelo, participantes, régimen, condiciones y vigencia.

La finalidad distingue contratación inicial, modificación bilateral y renovación expresa.

Las condiciones económicas incluyen valores y referencias del responsable del cálculo.

Una fórmula variable conserva fuente, margen, revisión y tratamiento de indisponibilidad.

### 7.1. Manifiesto

El manifiesto inmutable identifica:

- Modelo y versión.

- Condiciones generales y particulares.

- Participantes.

- Condiciones económicas.

- Cronogramas.

- Anexos.

- Versiones documentales.

- Orden de incorporación.

- Resúmenes individuales.

- Resumen del conjunto.

Se utiliza serialización canónica versionada.

Los archivos permanecen en Documentos. Una URL mutable no acredita contenido aceptado.

### 7.2. Vigencia y estados

La vigencia no supera los antecedentes indispensables.

La conservación de oferta se aplica únicamente cuando esté autorizada comercialmente. En los demás casos se comprueba
aplicabilidad antes de celebrar.

Los estados son preparación, documentos pendientes, disponible, parcialmente aceptada, totalmente aceptada, sustituida,
vencida, retirada y utilizada.

Un cambio material genera otra versión y exige las conformidades correspondientes.

## 8. Entrega y aceptación

### 8.1. Entrega

La actuación identifica propuesta, versión, manifiesto, destinatario, canal y documentos.

Conserva la referencia de Comunicaciones y la evidencia exacta obtenida.

Solicitado, disponible y entregado son resultados diferentes.

La aceptación exige que el contenido íntegro haya estado accesible previamente.

### 8.2. Aceptación

Cada aceptación contiene:

- Identidad.

- Propuesta y versión.

- Resumen del contenido.

- Participante y rol.

- Actor.

- Representación.

- Evidencia de autenticación.

- Evidencia de entrega.

- Conformidad afirmativa.

- Fecha.

- Estado.

Una aceptación pendiente de verificación no sirve para celebrar.

La actuación conjunta exige idéntico contenido.

El silencio no constituye aceptación.

### 8.3. Retiro

El retiro y la decisión de celebración se ordenan sobre la misma propuesta en Contratos.

Si el retiro se confirma primero, esa aceptación no se utiliza.

Si la celebración ya quedó confirmada, el retiro no borra la aceptación histórica. Se informa el tratamiento de
arrepentimiento o terminación correspondiente.

## 9. Protocolo de confirmación contractual

### 9.1. Finalidad

La celebración debe ordenar su decisión frente a cambios concurrentes de representación, facultades, condiciones
habilitantes y restricciones.

Una consulta favorable seguida de una transacción local no basta para garantizar esa correspondencia.

Se utiliza una coordinación persistente con preparación de las autoridades participantes y una decisión final durable en
Contratos.

No se mantienen transacciones SQL ni conexiones bloqueadas entre servicios durante la coordinación.

### 9.2. Intento de celebración

Cada intento contiene:

| Campo | Contenido |
| --- | --- |
| celebrationAttemptId | Identidad estable. |
| proposalId y proposalVersion | Propuesta exacta. |
| contentHash | Contenido aceptado. |
| applicationItemId | Componente de origen. |
| participantSnapshotHash | Participantes y actuaciones utilizados. |
| requiredAuthorities | Autoridades exigibles. |
| preparationReferences | Preparaciones recibidas. |
| decisionDeadline | Límite para confirmar. |
| decision | Pendiente, celebrar o abortar. |
| contractId | Contrato resultante. |
| version | Concurrencia. |

Los estados son:

- PREPARING.

- READY_TO_DECIDE.

- COMMITTED.

- ABORTED.

- RECOVERY_REQUIRED.

Solo puede existir un intento no resuelto por versión de propuesta.

### 9.3. Preparación de autoridades

Contratos solicita preparación únicamente a las autoridades necesarias para esa contratación.

Cada autoridad recibe intento, propuesta, contenido, actores, objeto y acto.

Comprueba sus condiciones y registra una preparación vinculada a ese contenido.

La respuesta identifica:

- Preparación.

- Autoridad.

- Objeto y versiones.

- Alcance.

- Resultado.

- Condiciones temporales.

- Referencia de consulta y recuperación.

Preparar no concede facultades nuevas ni levanta prohibiciones.

### 9.4. Orden frente a cambios concurrentes

La autoridad ordena la preparación y las modificaciones incompatibles dentro de su propia base.

Si una revocación o restricción efectiva se confirmó antes de preparar, se rechaza la preparación incompatible.

Una preparación confirmada mantiene abierta la coordinación de ese acto. Una modificación posterior que lo alcance debe
resolver su orden frente a la decisión contractual, en lugar de invalidar silenciosamente la evidencia.

Durante esa resolución:

- Se impiden nuevas preparaciones incompatibles.

- Se conserva la recepción y el momento de efecto de la modificación.

- Se consulta el intento contractual.

- Se solicita su aborto cuando todavía no existe una decisión definitiva.

- Se obtiene el resultado antes de cerrar el tratamiento del acto en curso.

Una recepción urgente se registra y atiende inmediatamente. No se presenta como aplicada a una celebración en curso
hasta comprobar si esta fue abortada o ya estaba confirmada.

Las medidas con efecto anterior a su conocimiento se tratan expresamente como tales. El protocolo no convierte un hecho
desconocido en conocido ni elimina efectos jurídicos retroactivos.

### 9.5. Decisión final

La decisión se confirma en una transacción local de Contratos.

Dentro de ella se bloquean propuesta e intento y se comprueba:

- Propuesta y contenido sin cambios.

- Vigencia y límite de decisión.

- Aceptaciones vigentes.

- Ausencia de retiros.

- Preparaciones exigibles completas.

- Ausencia de un impedimento incorporado al intento.

- Requisitos previos.

- Conformidad del banco.

La misma transacción registra:

- Decisión COMMITTED.

- Contrato.

- Versión inicial.

- Aceptaciones utilizadas.

- Condiciones.

- Resultado idempotente.

- Outbox.

La decisión durable y la creación del contrato son un único hecho local.

No existe una decisión de celebrar guardada sin su contrato.

### 9.6. Aborto

El aborto se confirma sobre el mismo intento.

Después de ABORTED no puede celebrarse con ese intento.

Las autoridades reciben la decisión y concluyen sus preparaciones.

Un nuevo intento exige referencias propias y comprobaciones actualizadas.

Abortar no elimina propuesta, aceptaciones ni antecedentes. Su utilización posterior depende de que continúen siendo
válidos.

### 9.7. Recuperación

Las preparaciones no se liberan únicamente porque venza una espera.

Cuando se alcanza el límite sin decisión conocida, se solicita resolver el intento. Contratos confirma el aborto si
todavía no había decidido.

Si Contratos no está disponible, la preparación queda pendiente de recuperación y se escala. No se presume aborto ni
celebración.

Una autoridad que recibe tarde una decisión recupera el resultado original.

Los cambios posteriores a COMMITTED siguen el tratamiento contractual aplicable. No se modifica retroactivamente la
decisión almacenada.

### 9.8. Intercambios técnicos

Las comprobaciones funcionales mantienen su identidad y significado. La preparación agrega la coordinación necesaria
para utilizarlas en una celebración concreta.

Los intercambios técnicos distinguen:

- Comprobación informativa.

- Preparación vinculada a un intento.

- Consulta de preparación.

- Comunicación y consulta de decisión.

Las autoridades implementan únicamente el alcance que les corresponde.

Una referencia de preparación no es un permiso reutilizable para otras operaciones.

## 10. Contratos y efectos

El contrato conserva propuesta, manifiesto, participantes, aceptaciones, preparaciones, conformidad del banco y decisión
de celebración.

Se distinguen:

| Dimensión | Valores |
| --- | --- |
| Ciclo | Celebrado, terminación pendiente o terminado. |
| Efectos | Suspendidos por condición, efectivos o cesados. |
| Cumplimiento | Pendiente, cumplido, incumplido o en revisión. |
| Obligaciones residuales | Inexistentes, pendientes o resueltas. |
| Constitución operativa | Seguimiento mediante Solicitudes de productos. |

Celebración, entrada en efecto y habilitación tienen fechas diferentes.

Solo el responsable del hecho acredita una condición. Contratos determina su consecuencia.

Una garantía constituida no demuestra desembolso.

La copia final se solicita después de confirmar. Una falla de entrega conserva seguimiento y no inventa recepción.

## 11. Constitución y habilitación

Solicitudes de productos mantiene una coordinación con:

- Identidad estable.

- Solicitud y componente.

- Contrato y versión.

- Producto esperado.

- Servicio responsable.

- Moneda.

- Participantes.

- Capacidades.

- Condiciones pendientes.

- Producto informado.

- Resultado.

Los estados distinguen condiciones pendientes, preparación, resultado esperado, producto constituido, habilitación
parcial, habilitación completa y reparación.

Antes de invocar al producto se conserva intención.

Ante respuesta perdida se consulta la constitución original. No se crea otra por cambiar la referencia.

El resultado se contrasta con producto, moneda, modalidad, participantes y capacidades.

Una incompatibilidad impide informar habilitación completa.

La falla no borra el contrato.

La exposición se transforma mediante el producto y Riesgos, sin duplicarla en Solicitudes.

## 12. Modificación y renovación

### 12.1. Tipos de modificación

| Tipo | Tratamiento |
| --- | --- |
| Bilateral | Propuesta, entrega y aceptación de los afectados. |
| Fórmula pactada | Aplicación de la fuente y mecanismo acordados. |
| Unilateral permitida | Fundamento y preaviso exigible. |
| Mejora incondicional | Comunicación sin pérdida de derechos. |
| Obligatoria | Fundamento, alcance y fecha comprobados. |

Los cambios bilaterales y renovaciones expresas que requieren nuevas conformidades utilizan el protocolo de confirmación
para el acto correspondiente.

Las condiciones anteriores siguen aplicables hasta confirmar el cambio y alcanzar su fecha de efecto.

### 12.2. Preaviso

La modificación unilateral adversa permitida exige sesenta días corridos desde el día siguiente a la notificación
acreditada.

El efecto no comienza antes del inicio del día posterior al sexagésimo.

Sin prueba de notificación, permanece pendiente.

No se aumenta unilateralmente una tasa fija de financiación ni se aplica una condición adversa retroactivamente.

### 12.3. Aplicación operativa

Se separan cambio contractual, fecha de efecto y confirmación del producto.

La falla operativa mantiene pendiente identificado. No borra el cambio contractual.

### 12.4. Renovación

La renovación automática requiere cláusula expresa.

La instrucción de no renovar recibida antes del vencimiento se ordena frente a la confirmación de una renovación todavía
no ejecutada.

Se conserva período original, nuevo período, condiciones y evaluaciones.

Las revisiones crediticias y exposición pertenecen a sus autoridades.

## 13. Arrepentimiento y terminación

### 13.1. Arrepentimiento

El plazo es de diez días hábiles desde el día siguiente al hecho más tardío entre recepción del contrato y
disponibilidad efectiva del producto.

La ausencia de prueba no permite presumir vencimiento.

La actuación conserva recepción, cómputo, calendario, producto, liquidación y obligaciones residuales.

Las instrucciones de liquidación se comunican dentro de dos días hábiles.

Para financiación desembolsada se dispone de diez días hábiles desde esa comunicación para restituir conforme al
tratamiento definido, conservando la utilización efectiva hasta el pago.

### 13.2. Tratamiento operativo

| Producto o situación | Tratamiento |
| --- | --- |
| Cuenta o tarjeta | Cese de nuevas prestaciones y tratamiento de obligaciones. |
| Financiación no desembolsada | Cancelación comprobada del desembolso. |
| Financiación desembolsada | Capital y utilización efectiva, sin cargo de salida por arrepentimiento. |
| Depósito no ejecutado | Cancelación de constitución pendiente. |
| Depósito ejecutado | Prec cancelación según modalidad admitida. |
| Servicio de inversión | Terminación sin deshacer ficticiamente operaciones de mercado. |

Una petición fuera del plazo se deriva al procedimiento ordinario aplicable.

### 13.3. Terminación

La petición se recibe con obligaciones pendientes.

Se distinguen legitimación, cese de nuevas prestaciones, coordinación operativa, obligaciones residuales y terminación.

Las demoras internas no prolongan cargos futuros que deban cesar.

La terminación no extingue deuda.

Dentro de dos días hábiles se informa ejecución o pendientes y responsables.

La terminación iniciada por el banco exige fundamento y revisión competente.

## 14. Interfaces HTTP

Las interfaces utilizan JSON y OpenAPI.

### 14.1. Solicitudes

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-03-003 | POST /product-applications | Solicitante y componentes. Devuelve trámite y requisitos. |
| CI-03-004 | POST /product-applications/{id}/antecedents | Puntos y evidencias. Devuelve presentación y revisión. |
| CI-03-005 | POST /product-applications/{id}/viability-assessments | Datos y decisiones. Devuelve resultado y condiciones. |
| CI-03-006 | POST /product-applications/{id}/withdrawals | Motivo y alcance. Devuelve retiro o tratamiento contractual. |
| CI-03-006 | POST /product-applications/{id}/reopenings | Antecedentes actuales. Devuelve nuevo trámite vinculado. |
| CI-03-007 | GET /product-applications/{id} | Devuelve componentes y situación. |
| CI-03-007 | GET /product-applications/{id}/actions | Devuelve historial autorizado. |
| CI-03-015 | POST /product-constitutions | Contrato y resultado esperado. Devuelve coordinación. |
| CI-03-016 | POST /product-constitutions/{id}/results | Resultado del producto. Devuelve correspondencia o inconsistencia. |
| CI-03-007 | GET /product-constitutions/{id} | Devuelve capacidades y pendientes. |

Solo el servicio operativo competente informa el resultado de constitución.

### 14.2. Modelos, propuestas y aceptación

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-03-001 | POST /contract-models | Definición inicial. Devuelve modelo en preparación. |
| CI-03-001 | POST /contract-models/{id}/versions | Contenido nuevo. Devuelve versión. |
| CI-03-001 | POST /contract-models/{id}/versions/{version}/actions | Aprobación, habilitación o retiro. Devuelve estado. |
| CI-03-002 | GET /contract-models/{id}/versions/{version} | Devuelve contenido y compatibilidades. |
| CI-03-008 | POST /contract-proposals | Finalidad, modelo y condiciones. Devuelve propuesta. |
| CI-03-009 | GET /contract-proposals/{id}/versions/{version} | Devuelve contenido y vigencia. |
| CI-03-010 | POST /contract-proposals/{id}/deliveries | Versión y destinatario. Devuelve entrega en seguimiento. |
| CI-03-011 | POST /contract-proposals/{id}/acceptances | Contenido y conformidad. Devuelve aceptación. |
| CI-03-012 | POST /contract-acceptances/{id}/withdrawals | Actor y recepción. Devuelve retiro o celebración existente. |

### 14.3. Contratos

| Contrato | Interfaz | Entrada y resultado |
| --- | --- | --- |
| CI-03-013 | POST /contracts | Propuesta y conformidades. Inicia o recupera celebración. |
| CI-03-013 | GET /contract-celebration-attempts/{id} | Devuelve decisión durable y contrato. |
| CI-03-013 | POST /contract-celebration-attempts/{id}/abort-requests | Autoridad o coordinador, causa y evidencia. Devuelve aborto o celebración existente. |
| CI-03-014 | POST /contracts/{id}/condition-fulfillments | Hecho competente y evidencia. Devuelve condición y efectos. |
| CI-03-017 | POST /contracts/{id}/amendments | Tipo, cambios y fundamento. Devuelve modificación o propuesta. |
| CI-03-018 | POST /contracts/{id}/renewals | Período y condiciones. Devuelve renovación. |
| CI-03-019 | POST /contracts/{id}/non-renewal-instructions | Instrucción y recepción. Devuelve alcance. |
| CI-03-020 | POST /contracts/{id}/withdrawal-right-requests | Petición y evidencias. Devuelve tratamiento. |
| CI-03-021 | POST /contracts/{id}/terminations | Causa y recepción. Devuelve terminación y pendientes. |
| CI-03-022 | GET /contracts/{id} | Devuelve situación actual o histórica autorizada. |
| CI-03-023 | POST /contracts/{id}/certificates | Tipo y fecha. Devuelve actuación documental. |
| CI-03-024 | GET /contract-actions/{actionId} | Recupera resultado original. |

Las operaciones técnicas de consulta y aborto forman parte del seguimiento de la celebración. No crean otro contrato ni
otra identidad económica.

Las respuestas usan 200, 201 y 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 15. Eventos

| Contrato | Evento | Contenido |
| --- | --- | --- |
| CI-03-025 | ProductApplicationOutcomeRecorded | Componente, evaluación y requisitos. |
| CI-03-026 | ContractProposalAvailable | Versión, manifiesto y vencimiento. |
| CI-03-027 | ContractCelebrated | Contrato, decisión, celebración y condiciones. |
| CI-03-028 | ContractEffectsChanged | Efecto anterior y nuevo, fecha y causa. |
| CI-03-029 | ProductConstitutionVerified | Producto y capacidades comprobadas. |
| CI-03-030 | ContractAmendmentEffective | Versiones y alcance efectivo. |
| CI-03-031 | ContractRenewalEffective | Períodos y condiciones. |
| CI-03-032 | ContractExitOutcomeRecorded | Salida contractual y obligaciones residuales. |
| CI-03-033 | ContractModelChanged | Versión, estado y tratamiento de propuestas. |

Los temas son banking.product-application.events y banking.contract.events.

La clave es el agregado.

El sobre incluye evento, esquema, emisor, versión, operación, actuación, causa, correlación y fechas.

Las notificaciones del protocolo utilizan referencias persistentes y consulta de decisión. Su recuperación no depende
únicamente de recibir un evento.

## 16. Persistencia y recuperación

Solicitudes conserva trámites, componentes, requisitos, presentaciones, evaluaciones y coordinaciones.

Contratos conserva modelos, propuestas, manifiestos, entregas, aceptaciones, intentos, preparaciones, decisiones,
contratos y actuaciones posteriores.

Ambos mantienen idempotencia, outbox, inbox y trabajos persistentes.

Se exige:

- Contrato único por propuesta inicial utilizada.

- Decisión única por intento.

- Un intento no resuelto por versión de propuesta.

- Aceptación efectiva única por participante y contenido.

- Renovación única por contrato y período.

- Cumplimiento único por condición y hecho.

- Contenido utilizado inmutable.

El mismo comando y contenido recuperan resultado. Otro contenido con igual identidad produce conflicto.

Las llamadas remotas se ejecutan fuera de la transacción.

Una celebración confirmada con respuesta perdida recupera el contrato original.

Una preparación pendiente nunca se libera suponiendo que el coordinador abortó.

## 17. Seguridad y tecnologías complementarias

Keycloak autentica usuarios y servicios.

Los permisos separan preparación, aprobación, aceptación, celebración, acreditación de condiciones y actuaciones
posteriores.

Las peticiones de aborto de autoridades requieren identidad, alcance y referencia de su preparación. No permiten
cancelar contratos ya confirmados.

Gestión administrativa transmite actuaciones. No reemplaza la conformidad del participante.

PostgreSQL conserva decisiones y coordinación.

Redis puede almacenar versiones inmutables de modelos, pero no decide vigencia, celebración o admisión de una
preparación.

Los documentos permanecen bajo Documentos y su almacenamiento privado. MongoDB no es necesario para estas
responsabilidades.

## 18. Observabilidad y pruebas

Se observan:

- Solicitudes detenidas.

- Propuestas próximas a vencer.

- Entregas fallidas.

- Intentos en preparación.

- Autoridades pendientes.

- Decisiones sin notificación completada.

- Abortos en recuperación.

- Constituciones incompatibles.

- Modificaciones pendientes de aplicación.

- Salidas con obligaciones residuales.

Las pruebas incluyen:

- Retiro frente a celebración.

- Revocación anterior a preparación.

- Revocación durante un intento.

- Restricción con efecto anterior a recepción.

- Vencimiento durante preparación.

- Caída después de preparar autoridades.

- Caída después de confirmar contrato y antes de notificar.

- Petición de aborto después de celebración.

- Recuperación sin decisión conocida.

- Constitución con respuesta perdida.

- Modificación material con aceptaciones anteriores.

- Renovación frente a instrucción de no renovar.

- Arrepentimiento con evidencia incompleta.

- Terminación con deuda residual.

Las pruebas de decisión, unicidad y concurrencia utilizan PostgreSQL real. Los simuladores permiten interrumpir cada
intercambio y comprobar que no se contradicen contrato, decisión y preparaciones.

## 19. Correspondencia funcional

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-03-01. Modelos | Versiones y aprobación. | CI-03-001, 002 y 033. |
| OF-03-02. Solicitudes | Trámite y componentes. | CI-03-003 y 007. |
| OF-03-03. Viabilidad | Requisitos y decisiones. | CI-03-004, 005 y 025. |
| OF-03-04. Retiro y vencimiento | Ciclo del trámite. | CI-03-006, 007 y 025. |
| OF-03-05. Propuesta | Contenido y vigencia. | CI-03-008, 009 y 026. |
| OF-03-06. Consentimiento | Entrega, aceptación y retiro. | CI-03-010, 011 y 012. |
| OF-03-07. Celebración | Preparaciones, decisión y efectos. | CI-03-013, 014, 027 y 028. |
| OF-03-08. Constitución | Coordinación y comprobación operativa. | CI-03-015, 016 y 029. |
| OF-03-09. Modificación | Contenido, confirmación y aplicación. | CI-03-017 y 030. |
| OF-03-10. Renovación | Períodos y no renovación. | CI-03-018, 019 y 031. |
| OF-03-11. Arrepentimiento | Plazo y tratamiento. | CI-03-020 y 032. |
| OF-03-12. Terminación | Cese y obligaciones residuales. | CI-03-021 y 032. |
| OF-03-13. Consultas | Historia y constancias. | CI-03-022, 023 y 024. |
