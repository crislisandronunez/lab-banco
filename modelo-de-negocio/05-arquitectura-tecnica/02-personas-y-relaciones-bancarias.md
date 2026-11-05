# Diseño técnico — Módulo 2. Personas y relaciones bancarias

## 1. Propósito y alcance

Este documento define el diseño técnico de:

- Personas.

- Vinculación.

- Clientes.

- Representación.

Comprende datos propios, estados, persistencia, interfaces HTTP, eventos, transacciones, concurrencia, seguridad y
recuperación.

Mantiene las operaciones OF-02-01 a OF-02-13 y los contratos CI-02-001 a CI-02-033.

Registrar una persona, verificar su identidad, admitirla, constituir la relación comercial y reconocer una
representación son actuaciones diferentes.

Este módulo no abre cuentas, concede crédito ni modifica dinero, deuda o tenencias.

## 2. Distribución técnica

| Servicio | Aplicación | Base PostgreSQL | Autoridad |
| --- | --- | --- | --- |
| Personas | party-service | party_db | Datos declarados, procedencia, relaciones y referencias de identidad |
| Vinculación | onboarding-service | onboarding_db | Solicitudes y decisiones de admisión |
| Clientes | customer-service | customer_db | Identificación comercial, períodos, revisiones y desvinculación |
| Representación | representation-service | representation_db | Fundamentos y facultades para actuar por otra persona |

Cada base utiliza un usuario exclusivo.

Las referencias externas no crean claves foráneas ni permiten consultas SQL entre servicios.

Conocimiento del cliente mantiene las verificaciones especializadas. Personas conserva las declaraciones y sus
antecedentes.

## 3. Convenciones de datos e interfaces

### 3.1. Tipos

| Dato | Java | PostgreSQL | JSON |
| --- | --- | --- | --- |
| Identificador interno | UUID | uuid | Cadena UUID |
| Versión | long | bigint | Entero positivo |
| Fecha | LocalDate | date | YYYY-MM-DD |
| Instante | Instant | timestamptz | ISO 8601 UTC |
| Texto limitado | String | varchar(n) | Cadena |
| Decimal exacto | BigDecimal | numeric(p,s) | Cadena decimal |
| Booleano | boolean | boolean | Booleano |
| Enumeración | Enumeración | varchar(40) | Código permitido |

Las fechas de nacimiento y constitución no contienen hora.

Los identificadores documentales, tributarios y contactos conservan su representación textual.

Los plazos bancarios utilizan America/Argentina/Buenos_Aires.

### 3.2. Límites

| Dato | Límite |
| --- | --- |
| Nombres y apellidos | De 1 a 150 caracteres por campo |
| Denominación jurídica | De 1 a 250 caracteres |
| Nombre comercial | Hasta 250 caracteres |
| Código técnico | De 1 a 80 caracteres |
| Identificador documental o tributario | De 1 a 120 caracteres |
| Emisor | Hasta 120 caracteres |
| Correo electrónico | Hasta 254 caracteres |
| Teléfono normalizado | Hasta 20 caracteres |
| Línea de domicilio | Hasta 250 caracteres |
| Localidad o región | Hasta 120 caracteres |
| Código postal | Hasta 20 caracteres |
| Fundamento | De 1 a 4.000 caracteres |
| Referencia externa | De 1 a 120 caracteres |
| Huella de contenido | SHA-256 hexadecimal de 64 caracteres |

Los valores que exceden los límites se rechazan sin recorte silencioso.

### 3.3. Ausencia y calidad

Los atributos que pueden estar incompletos se acompañan de uno de estos estados:

| Estado | Significado |
| --- | --- |
| PROVIDED | Valor aportado |
| UNKNOWN | Desconocido |
| NOT_PROVIDED | Todavía no aportado |
| NOT_APPLICABLE | No corresponde |

PROVIDED exige un valor. Los demás estados no admiten valores ficticios.

La procedencia y la verificación se mantienen separadas del valor declarado.

Una contradicción se registra sin destruir automáticamente la declaración anterior.

### 3.4. Contexto de actuación

Los comandos persistentes incluyen:

| Campo | Tipo | Obligatoriedad |
| --- | --- | --- |
| actionId | UUID | Obligatorio |
| operationId | UUID | Obligatorio |
| causationId | UUID | Opcional |
| purpose | Código | Obligatorio |
| reason | Fundamento | Obligatorio |
| expectedVersion | Versión | Para modificar |
| evidenceReferences | Referencias documentales | Colección; vacía solo si no se exige evidencia |
| originalReceivedAt | Instante | Cuando afecta al trámite |

El servicio y actor se obtienen de autenticación o delegación verificadas.

Se requieren Idempotency-Key y X-Correlation-Id.

La finalidad y el motivo no conceden permisos por sí solos.

### 3.5. Resultados

Las actuaciones devuelven:

- Identidad de actuación.

- Resultado.

- Objeto y versión.

- Recepción y confirmación.

- Referencias relacionadas.

- Componentes aplicados.

- Pendientes y motivos.

- Recurso de seguimiento.

Los resultados comunes son CONFIRMED, PARTIALLY_CONFIRMED, PENDING, REJECTED y CONFLICT.

No sustituyen los estados particulares del objeto.

## 4. Servicio Personas

### 4.1. Persona

| Campo | Tipo | Obligatoriedad |
| --- | --- | --- |
| partyId | UUID | Obligatorio |
| version | Versión | Obligatorio |
| partyType | NATURAL_PERSON o LEGAL_ENTITY | Obligatorio |
| recordStatus | Estado del registro | Obligatorio |
| principalPartyId | UUID local | Para referencia asociada |
| createdAt y updatedAt | Instantes | Obligatorios |
| originReference | Procedencia | Obligatorio |

Los estados del registro son INCOMPLETE, REGISTERED, DUPLICATE_REVIEW y RESOLVED_ALIAS.

Para persona humana se conservan nombre, apellido y fecha de nacimiento, con ausencia explícita cuando se desconozcan.

Para persona jurídica se conservan denominación, forma jurídica, jurisdicción, fecha de constitución y antecedentes
constitutivos.

El nombre o denominación de referencia es obligatorio para registrar. Los restantes faltantes mantienen incompleto el
registro y no satisfacen requisitos posteriores.

Las fechas aportadas no pueden ser futuras respecto del hecho declarado. Una corrección documental contradictoria se
deriva a revisión.

### 4.2. Identificadores

Cada identificador conserva:

- identifierId y partyId.

- Tipo.

- Valor original y normalizado.

- Jurisdicción.

- Emisor cuando sea exigible.

- Fechas de vigencia.

- Procedencia.

- Referencia de verificación, cuando exista.

- Estado de observación.

Los estados de observación son NONE, PENDING, CONTRADICTORY y EXPIRED.

La normalización es específica del tipo y conserva el original.

### 4.3. Contactos y domicilios

Los contactos contienen identidad, tipo, valor original y normalizado, vigencia, procedencia y estado.

Los estados son:

- DECLARED.

- PENDING_CONFIRMATION.

- CONFIRMED.

- REJECTED.

- RETIRED.

Confirmar un contacto acredita control de ese medio dentro del procedimiento. No equivale a verificar toda la identidad.

Los desafíos no se almacenan en texto claro. Tienen vencimiento, límite de intentos y vinculación al contacto concreto.

Un contacto nuevo no autoriza por sí solo su sustitución. La pérdida de medios anteriores exige recuperación de
identidad y evidencia adicional.

Los domicilios conservan tipo, país, región, localidad, líneas, código postal, vigencia y fuente.

### 4.4. Relaciones declaradas

| Campo | Regla |
| --- | --- |
| relationshipId y version | Identidad y control de modificación |
| sourcePartyId y targetPartyId | Personas registradas |
| relationshipType | Familiar, laboral, societario u otro tipo definido |
| validFrom y validTo | Intervalo coherente |
| measurementDate | Obligatoria para composición cuantificada |
| economicShare | numeric(15,12), entre cero y uno |
| votingShare | numeric(15,12), entre cero y uno |
| quantity | numeric(30,12), opcional con unidad |
| measurementBasis | Base y referencia obligatorias si se cuantifica |
| evidenceReferences | Procedencia documental |
| completeness | COMPLETE, INCOMPLETE u OBSERVED |

Las participaciones económicas y de voto se mantienen separadas.

Solo se comparan magnitudes de igual base y fecha.

Una estructura circular se conserva y deriva a análisis. Registrar una relación societaria no concede representación.

### 4.5. Circunstancias especiales

Cada circunstancia contiene persona, tipo, fecha del hecho, conocimiento, fuente, evidencia y condición de verificación.

Los tipos iniciales son:

- CAPACITY_CHANGE.

- DEATH_REPORTED.

- SUCCESSION.

- DISSOLUTION.

- LIQUIDATION.

- OTHER_DOCUMENTED_CIRCUMSTANCE.

La edad se calcula con nacimiento y fecha de referencia. No se conserva una edad fija como autoridad.

Un fallecimiento comunicado no se presenta como verificado sin su conclusión correspondiente.

### 4.6. Interfaces

| Contrato | Interfaz | Finalidad |
| --- | --- | --- |
| CI-02-001 | POST /v1/parties | Registrar o recuperar persona |
| CI-02-002 | GET /v1/parties/{partyId} | Consultar atributos autorizados |
| CI-02-002 | POST /v1/party-searches | Buscar con criterios protegidos |
| CI-02-003 | POST /v1/parties/{partyId}/changes | Actualizar datos |
| CI-02-003 | POST /v1/contact-confirmations | Iniciar confirmación |
| CI-02-003 | POST /v1/contact-confirmations/{confirmationId}/responses | Responder desafío |
| CI-02-004 | POST /v1/party-relationships | Registrar relación |
| CI-02-004 | POST /v1/party-relationships/{relationshipId}/changes | Modificar relación |
| CI-02-005 | GET /v1/parties/{partyId}/relationships | Consultar relaciones |
| CI-02-006 | POST /v1/party-duplicate-cases | Abrir resolución de duplicados |
| CI-02-007 | POST /v1/parties/{partyId}/circumstances | Registrar circunstancia |
| CI-02-022 | GET /v1/party-actions/{actionId} | Recuperar actuación |

Las búsquedas sensibles utilizan cuerpo y controles de acceso, evitando identificadores personales en rutas o parámetros
de búsqueda.

El registro devuelve CREATED, EXISTING o MATCH_REVIEW_REQUIRED.

Una coincidencia de nombre no fusiona personas.

Las actualizaciones reciben cambios tipados, versión, procedencia y evidencia. No admiten rutas arbitrarias ni cambios
del identificador interno.

Nombre, identidad, constitución y capacidad requieren la revisión especializada correspondiente.

### 4.7. Duplicados y persistencia

La resolución de duplicados conserva candidatos, evidencia, propuesta, versiones y aprobación separada.

Se elige la identidad mejor acreditada y, en igualdad, la más antigua.

Las candidatas se bloquean en orden estable. Se impiden asociaciones circulares y resoluciones contradictorias.

Las referencias anteriores continúan resolubles. La resolución no suma cuentas, deudas ni activos.

Los identificadores normalizados utilizan una correspondencia por tipo, jurisdicción y emisor.

Una reclamación concurrente incompatible abre revisión. No asigna automáticamente el mismo identificador a dos personas
nuevas.

La búsqueda difusa propone candidatos y no decide unicidad.

## 5. Servicio Vinculación

### 5.1. Solicitud

La solicitud conserva:

| Campo | Contenido |
| --- | --- |
| applicationId y version | Identidad y versión |
| partyId | Persona |
| applicationType | INITIAL o READMISSION |
| purposeCode | Propósito |
| declaredActivity | Actividad declarada |
| contactReferences | Contactos |
| representationReferences | Cuando interviene un representante |
| previousApplicationId | Trámite anterior relacionado |
| previousCustomerId | Cliente de la readmisión |
| status | Estado |
| receivedAt | Recepción |

Los estados son RECEIVED, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_DECISION, ACCEPTED, REJECTED, WITHDRAWN,
EXPIRED y REQUIRES_REVIEW.

La utilización de la admisión se mantiene separada del estado del expediente.

### 5.2. Requerimientos

Cada requerimiento conserva:

- Identidad y versión.

- Puntos solicitados.

- Causa.

- Canal.

- Puesta a disposición.

- Plazo y calendario.

- Vencimiento original y ajustado.

- Recordatorio.

- Presentaciones.

- Incidencias.

Cada punto tiene estado propio.

Una presentación oportuna impide declarar vencido el punto mientras se revisa.

Los nuevos puntos derivados de una omisión del banco reciben treinta días sin reducir derechos anteriores.

### 5.3. Decisión de admisión

La decisión identifica:

- Decisión y versión.

- Solicitud y versión evaluadas.

- Persona y versión relevante.

- Alcance.

- Resultado.

- Verificaciones utilizadas.

- Aprobación.

- Fundamentos internos y motivos comunicables.

- Momento de aprobación.

- Límite de utilización.

- Condición de retiro o revisión.

La vigencia máxima termina treinta días corridos después de aprobar, a la misma hora local, sin superar la vigencia de
los antecedentes necesarios.

La falta de un requisito obligatorio mantiene la decisión pendiente de revisión.

Preparador y aprobador son personas distintas.

Una alerta sin conclusión no se transforma automáticamente en rechazo.

### 5.4. Uso de admisión

El uso contiene decisión, constitución destinataria, persona, alcance, instante confirmado y seguimiento.

Vinculación ordena el uso frente a retiro, vencimiento y revisión sobre su estado autoritativo.

La misma constitución recupera la confirmación. Otra incompatible genera conflicto.

Una fecha enviada por el consumidor no permite retrotraer un uso vencido.

El vencimiento posterior no anula un uso válido.

### 5.5. Interfaces

| Contrato | Interfaz | Finalidad |
| --- | --- | --- |
| CI-02-008 | POST /v1/onboarding-applications | Iniciar |
| CI-02-009 | POST /v1/onboarding-applications/{applicationId}/submissions | Aportar antecedentes |
| CI-02-010 | POST /v1/onboarding-applications/{applicationId}/lifecycle-actions | Retirar o reabrir |
| CI-02-011 | POST /v1/onboarding-applications/{applicationId}/decisions | Resolver |
| CI-02-012 | GET /v1/onboarding-applications/{applicationId} | Consultar trámite |
| CI-02-012 | GET /v1/admission-decisions/{decisionId} | Consultar decisión y uso |
| CI-02-012 | GET /v1/onboarding-actions/{actionId} | Consultar actuación |
| CI-02-013 | POST /v1/admission-decisions/{decisionId}/uses | Confirmar uso |

Si la persona ya tiene una relación comercial vigente, se informa y deriva sin crear otra innecesariamente.

Las presentaciones no actualizan Personas por sí solas. Las modificaciones personales se solicitan mediante CI-02-003.

Reabrir genera un trámite vinculado y no restaura aprobaciones vencidas.

Si el uso está confirmado o incierto, el retiro se coordina con Clientes antes de informar su conclusión.

## 6. Servicio Clientes

### 6.1. Cliente y períodos

| Campo | Contenido |
| --- | --- |
| customerId y version | Identidad y versión |
| partyId | Persona |
| commercialSegmentCode | Segmento |
| newContractingAllowed | Posibilidad de nuevas contrataciones |
| continuityStatus | Continuidad |
| nextReviewDate | Próxima revisión mientras corresponda |
| createdAt | Creación |

La correspondencia entre persona principal y cliente es única.

Cada período conserva admisión, uso confirmado, inicio, cese comercial, finalización integral y causa.

No existen dos períodos comerciales activos equivalentes para el mismo cliente.

Los estados de continuidad son ACTIVE, OFFBOARDING, RESIDUAL y ENDED.

Una revisión pendiente no equivale automáticamente a una restricción.

### 6.2. Constitución recuperable

La constitución distingue:

- INTENT_RECORDED.

- ADMISSION_USE_PENDING.

- ADMISSION_USE_CONFIRMED.

- CONSTITUTED.

- FAILED_REQUIRING_RESOLUTION.

Clientes registra intención antes de solicitar el uso de admisión.

Tras confirmar el uso, constituye el cliente o período mediante una transacción local con idempotencia y outbox.

Una interrupción entre ambas etapas recupera la misma confirmación y continúa la misma constitución.

Si existe una relación activa compatible, devuelve la existente sin consumir otra admisión innecesariamente.

La unicidad local y el control de identidad comercial impiden constituciones equivalentes concurrentes.

### 6.3. Revisión

La revisión conserva identidad, cliente, motivo, fecha prevista, inicio, requerimientos, verificaciones, conclusión,
finalización y próxima fecha.

Los estados son OPEN, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_CONCLUSION, COMPLETED, OVERDUE y ESCALATED.

La próxima revisión utiliza la fecha más próxima entre el máximo ordinario de doce meses y las exigencias anteriores de
controles.

La conclusión real inicia el nuevo plazo. No se retrodata para ocultar atrasos.

Cambiar segmento requiere fundamento y no modifica contratos existentes.

### 6.4. Desvinculación

La desvinculación conserva:

- Causa y solicitud.

- Cese de nuevas contrataciones.

- Inventarios por módulo.

- Confirmaciones y cortes.

- Cuentas, contratos, deudas, activos y medidas.

- Actuaciones de cierre.

- Responsables residuales.

- Comunicaciones.

- Cese comercial y finalización integral.

Los inventarios distinguen CONFIRMED_EMPTY, CONFIRMED_WITH_RELATIONS y PENDING.

La falta de respuesta no equivale a ausencia de productos.

Retirar una solicitud no reabre productos cerrados.

La readmisión conserva customerId y abre un nuevo período mediante nueva admisión.

### 6.5. Interfaces

| Contrato | Interfaz | Finalidad |
| --- | --- | --- |
| CI-02-014 | POST /v1/customer-constitutions | Constituir relación |
| CI-02-015 | GET /v1/customers/{customerId} | Consultar cliente |
| CI-02-015 | GET /v1/customer-relations/by-party/{partyId} | Consultar relación por persona |
| CI-02-016 | POST /v1/customers/{customerId}/review-actions | Revisar o cambiar segmento |
| CI-02-017 | POST /v1/customers/{customerId}/offboarding-actions | Desvincular y seguir componentes |
| CI-02-023 | GET /v1/customer-actions/{actionId} | Recuperar actuación |

CI-02-014 recibe persona, decisión y versión, alcance, segmento y customerConstitutionId.

CI-02-016 admite START, ADD_EVIDENCE, REQUEST_CONCLUSION y CHANGE_SEGMENT.

CI-02-017 admite REQUEST, ADD_INVENTORY, CONFIRM_COMPONENT, WITHDRAW_REQUEST y FINALIZE.

Los inventarios se aceptan del responsable o mediante evidencia administrativa autorizada.

Clientes no replica el legajo personal ni el perfil de cumplimiento como datos propios.

### 6.6. Duplicados de persona

Una resolución de duplicados no fusiona automáticamente clientes ni productos.

Clientes registra referencias asociadas y comprueba identificaciones comerciales existentes.

Los conflictos requieren regularización explícita bajo aprobación, preservando períodos, historia y medidas.

No se trasladan fondos ni se cambian titularidades.

## 7. Servicio Representación

### 7.1. Modelo

| Campo | Contenido |
| --- | --- |
| representationId y version | Identidad y versión |
| representedPartyId | Persona representada |
| representativePartyIds | Representantes |
| basisType | Fundamento |
| basisDocuments | Referencias documentales |
| parentRepresentationId | Fundamento derivado, cuando exista |
| permittedActs | Actos permitidos |
| productScope | Productos alcanzados |
| objectScope | Objetos alcanzados |
| amountLimits | Límites por moneda |
| actingMode | INDIVIDUAL o JOINT |
| jointRule | Regla conjunta |
| validFrom y validTo | Vigencia |
| continuityReviewedAt | Última revisión |
| nextContinuityReviewDate | Próxima revisión cuando no hay vencimiento documental |
| status | Estado |

Los importes utilizan numeric(24,8) y moneda explícita.

El alcance utiliza ALL_WITHIN_BASIS o EXPLICIT_SET.

ALL_WITHIN_BASIS solo procede cuando el fundamento verificado lo permite.

La representación derivada no puede superar su fundamento en actos, objetos, importes, vigencia ni capacidad de delegar.

### 7.2. Estados

Los estados son:

- SUBMITTED.

- UNDER_VERIFICATION.

- PENDING_APPROVAL.

- ACTIVE.

- CONTINUITY_REVIEW_REQUIRED.

- REVOKED.

- EXPIRED.

- ENDED.

- REJECTED.

Una ampliación o renovación pendiente se conserva separada de la versión activa y no prolonga facultades.

Las representaciones sin vencimiento documental se revisan como máximo cada doce meses y ante hechos relevantes.

### 7.3. Interfaces funcionales

| Contrato | Interfaz | Finalidad |
| --- | --- | --- |
| CI-02-018 | POST /v1/representation-applications | Reconocer representación |
| CI-02-019 | POST /v1/representations/{representationId}/changes | Modificar, renovar o terminar |
| CI-02-020 | GET /v1/representations/{representationId} | Consultar |
| CI-02-020 | GET /v1/parties/{partyId}/representations | Consultar por persona |
| CI-02-021 | POST /v1/representation-checks | Comprobar facultades informativamente |
| CI-02-024 | GET /v1/representation-actions/{actionId} | Consultar actuación |

Reconocer exige verificación y aprobación separadas de la preparación administrativa.

Un documento recibido sin verificación no habilita actos.

CI-02-019 admite AMEND, RENEW, REVOKE, END y CONFIRM_CONTINUITY.

Las ampliaciones y renovaciones requieren verificaciones equivalentes al reconocimiento.

Una revocación comprobada se registra sin esperar modificaciones ajenas. Los intentos preparados afectados mantienen su
resolución explícita.

Una comunicación urgente no comprobada se deriva a Restricciones para evaluar protección.

### 7.4. Comprobación informativa

CI-02-021 recibe:

| Campo | Obligatoriedad |
| --- | --- |
| representedPartyId | Obligatorio |
| actingPartyIds | Conjunto no vacío |
| actCode | Obligatorio |
| objectReference | Obligatorio |
| productReference | Cuando corresponda |
| amount y currency | Para actos monetarios |
| contentHash | Obligatorio |
| referenceTime | Obligatorio |
| purpose | Obligatorio |

La identidad de los actores y su delegación se comprueban adicionalmente.

La respuesta identifica checkId, SUFFICIENT, INSUFFICIENT o PENDING, representaciones y versiones, límites,
participantes faltantes y condiciones adicionales.

Una actuación conjunta requiere conformidades sobre el mismo contenido.

No se combinan poderes incompatibles para construir una facultad inexistente.

La comprobación no reserva dinero, satisface restricciones del producto ni crea una preparación persistente.

## 8. Preparación de representación

### 8.1. Finalidad

Representación participa como autoridad cuando una decisión concreta requiere proteger la comprobación frente a
revocaciones o cambios concurrentes.

El coordinador puede ser Contratos, Fondos u otro responsable funcional autorizado por el recorrido correspondiente.

La preparación se solicita únicamente cuando el acto requiere representación. La actuación de una persona por derecho
propio no genera una preparación ficticia.

### 8.2. Contenido

Cada preparación conserva:

| Campo | Contenido |
| --- | --- |
| preparationId | UUID |
| actionId y operationId | Actuación y operación |
| coordinatorService | Servicio que decide |
| attemptId | Intento concreto |
| decisionQueryReference | Referencia de consulta autorizada |
| representedPartyId | Representado |
| actingPartyIds | Actores verificados |
| representationReferences | Fundamentos y versiones utilizados |
| actCode | Acto |
| objectReference y productReference | Alcance concreto |
| amount y currency | Cuando correspondan |
| contentHash | Contenido vinculante |
| jointEvidenceReferences | Conformidades conjuntas exigibles |
| controlVersions | Versiones de control |
| preparedAt | Preparación |
| decisionDeadline | Límite para decidir |
| state | Coordinación |
| decision | Decisión durable conocida |
| resultReferences | Evidencias del resultado |

La referencia de consulta se resuelve contra destinos configurados y autorizados. No admite direcciones arbitrarias
suministradas por el consumidor.

La huella se acompaña de los datos necesarios para evaluar el acto.

### 8.3. Interfaces técnicas

| Interfaz | Finalidad |
| --- | --- |
| POST /v1/representation-action-preparations | Preparar un acto concreto |
| GET /v1/representation-action-preparations/{preparationId} | Consultar preparación |
| POST /v1/representation-action-preparations/{preparationId}/decision-notifications | Incorporar decisión verificable |

El contrato técnico identifica su correspondencia con la responsabilidad de CI-02-021.

La consulta informativa conserva su significado. La preparación es un comando separado.

Las consultas de decisión y peticiones de aborto utilizan las interfaces del coordinador del intento.

### 8.4. Estados

| Estado | Significado |
| --- | --- |
| PREPARED | Preparación favorable persistida |
| RESOLUTION_PENDING | Decisión por recuperar o resolver |
| CONCLUDED_COMMITTED | Confirmación comprobada |
| CONCLUDED_ABORTED | Aborto comprobado |
| REJECTED | Preparación desfavorable definitiva |

Una preparación solo puede concluir con una decisión.

No se reutiliza para otro contenido, actor, acto u objeto.

### 8.5. Verificación local

Representación comprueba:

- Identidad de representado y actores.

- Fundamentos y cadena de delegación.

- Actos y objetos.

- Límites y moneda.

- Vigencia y continuidad.

- Actuación conjunta.

- Correspondencia del contenido.

- Ausencia de cambios incompatibles ya efectivos y conocidos.

Los controles de la cadena y del alcance se bloquean en orden estable.

Preparar y registrar cambios incompatibles utilizan esos mismos controles locales.

Las llamadas externas se realizan fuera de la transacción. Sus resultados se vinculan a las referencias y versiones
utilizadas.

Una representación pendiente de reconocimiento, vencida o con continuidad exigible sin resolver no produce una
preparación favorable.

El límite de decisión no puede superar el vencimiento conocido de los fundamentos necesarios.

### 8.6. Revocaciones y cambios concurrentes

Una revocación, terminación, reducción de alcance o cambio relevante de fundamento:

- Registra recepción, evidencia y momento de efecto.

- Impide nuevas preparaciones incompatibles.

- Identifica preparaciones propias y derivadas afectadas.

- Consulta sus intentos.

- Solicita aborto cuando todavía sea posible.

- Recupera la decisión y completa el tratamiento.

La autoridad no invalida silenciosamente una preparación que el coordinador todavía puede utilizar.

El cambio distingue su registro de la aplicación comprobada a todos los intentos afectados.

Si el coordinador abortó, la preparación concluye como abortada.

Si ya confirmó, se conserva la decisión y se aplica el tratamiento correspondiente al hecho existente.

Si no se conoce la decisión, se mantiene resolución pendiente.

La recepción urgente no espera a que termine esta coordinación. Su resultado informa expresamente lo que continúa
pendiente.

### 8.7. Renovación y delegación

Renovación, revocación y preparación compiten sobre el mismo estado autoritativo.

Una renovación atrasada no reactiva una versión revocada.

Los cambios de un fundamento alcanzan las preparaciones de sus delegaciones dependientes.

La comprobación de cadenas impide ciclos y ampliaciones de facultades.

Una renovación no extiende automáticamente el límite de un intento ya preparado. Si necesita otra condición o contenido,
se resuelve el intento y se tramita el que corresponda.

### 8.8. Vencimiento y recuperación

Al vencer el límite sin decisión conocida se solicita resolver el intento.

La preparación no se libera por timeout.

Si el coordinador está indisponible, se conserva el seguimiento y se escala.

Un límite vencido impide una nueva confirmación del intento conforme al control del coordinador, pero no demuestra que
este no haya confirmado antes.

Las notificaciones se autentican y verifican contra intento, preparación y contenido.

Una petición atrasada no reactiva una preparación concluida.

### 8.9. Fronteras

Preparar representación:

- No reserva fondos.

- No concede crédito.

- No levanta restricciones.

- No sustituye permisos específicos del producto.

- No acredita facultades internas del banco.

Cuando el hecho ya ocurrió y su regularización tiene otro fundamento válido, el responsable funcional identifica ese
fundamento. No solicita una nueva instrucción ficticia del representante para justificarlo.

## 9. Plazos, trabajos y recuperación

### 9.1. Requerimientos

El plazo inicial es treinta días corridos desde el día siguiente a la puesta a disposición válida.

El último día termina a las 24:00 locales, representadas como el inicio exclusivo del día siguiente.

Si el último día es inhábil, se traslada al final del siguiente hábil.

La subsanación final utiliza quince días corridos.

El recordatorio se genera cinco días corridos antes del vencimiento efectivo.

Las verificaciones internas no consumen un plazo que no esté pendiente del solicitante.

### 9.2. Incidencias de canal

La incidencia conserva inicio, recuperación, canal y trámites afectados.

Si impide responder al vencer, el nuevo límite no es anterior al final del segundo hábil posterior a la recuperación.

No se sustituye un vencimiento posterior ya concedido por uno más corto.

### 9.3. Revisiones

Se programan aviso treinta días antes cuando corresponde participación, recordatorios, revisiones por hechos relevantes
y escalación al vencer.

La tarea supervisora tiene un plazo de cinco días hábiles.

Vencer no declara favorable la revisión ni bloquea fondos automáticamente.

### 9.4. Admisión y constitución

Vinculación y Clientes conservan referencias cruzadas de solicitud, decisión, uso y constitución.

Una respuesta perdida se recupera mediante CI-02-012 o CI-02-023.

Una falla definitiva de constitución exige evidencia antes de resolver otro uso. No se libera por timeout.

Un retiro posterior al uso confirmado no elimina unilateralmente la relación.

### 9.5. Preparaciones

Los trabajos recuperan decisiones pendientes y cambios con aplicación parcial.

La propiedad temporal del trabajador y la vigencia de la preparación son conceptos distintos.

Perder la propiedad de ejecución no aborta el intento ni permite liberar la preparación.

## 10. Eventos

### 10.1. Sobre común

Los eventos incluyen identificador, tipo, versión de esquema, emisor, objeto, versión del objeto, instante, operación,
correlación, causa y contenido específico.

El cambio y la outbox se confirman conjuntamente.

### 10.2. Catálogo

| Contrato | Tipo | Clave | Contenido |
| --- | --- | --- | --- |
| CI-02-025 | party.updated.v1 | Persona | Campos afectados, versión y revisión |
| CI-02-026 | party.relationship-changed.v1 | Relación | Participantes, tipo, vigencia y versión |
| CI-02-027 | party.duplicates-resolved.v1 | Caso | Referencia principal y asociadas |
| CI-02-028 | party.circumstance-recorded.v1 | Persona | Circunstancia, verificación y fechas |
| CI-02-029 | onboarding.admission-decided.v1 | Solicitud | Decisión, alcance, vigencia y resultado |
| CI-02-030 | customer.relationship-established.v1 | Cliente | Período, admisión y constitución |
| CI-02-031 | customer.review-completed.v1 | Cliente | Conclusión y próxima fecha |
| CI-02-032 | customer.continuity-changed.v1 | Cliente | Contratación, cese y continuidad |
| CI-02-033 | representation.changed.v1 | Representación | Alcance, versión, efecto y continuidad |

Los canales son party.events.v1, onboarding.events.v1, customer.events.v1 y representation.events.v1.

No existe orden global entre objetos.

Los eventos personales no distribuyen documentos, contactos completos ni legajos indiscriminadamente.

El evento de revocación no demuestra por sí solo que todos los intentos afectados estén abortados. La consulta de
actuación informa componentes concluidos y pendientes.

## 11. Persistencia y transacciones

Cada servicio conserva actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Representación conserva además preparaciones, controles de alcance, referencias de decisión y seguimiento de cambios
incompatibles.

Los registros de idempotencia incluyen productor, clave, actuación y huella.

Se utilizan restricciones locales para:

- Correspondencia de identificadores.

- Cliente único por persona principal.

- Período comercial activo no duplicado.

- Uso de admisión vinculado a constitución.

- Preparación única por coordinador, intento y condición.

- Recepción única de eventos.

- Ocurrencias temporales únicas.

Las actualizaciones identifican versiones.

Los cambios, historia, resultado idempotente y outbox se confirman conjuntamente.

No se mantienen transacciones SQL durante llamadas remotas.

Los trabajadores utilizan propiedad temporal y versión para impedir confirmaciones atrasadas.

## 12. Seguridad e integraciones

### 12.1. Permisos

Se distinguen permisos para:

- Registrar y actualizar personas.

- Consultar atributos por finalidad.

- Resolver duplicados.

- Administrar relaciones y circunstancias.

- Tramitar admisión.

- Constituir y revisar clientes.

- Desvincular.

- Reconocer, modificar y revocar representación.

- Comprobar facultades.

- Preparar actos para coordinadores autorizados.

- Comunicar decisiones propias.

- Consultar actuaciones.

Un servicio técnico no obtiene acceso universal.

Los controles también se aplican a búsquedas, resultados idempotentes y referencias históricas.

Las peticiones de aborto se vinculan a una preparación existente y no permiten cancelar operaciones ajenas.

### 12.2. Dependencias

| Proveedor | Contratos | Uso |
| --- | --- | --- |
| Institución | CI-01-004 | Facultades internas |
| Calendarios bancarios | CI-01-014 y CI-01-015 | Plazos y revisiones |
| Conocimiento del cliente | CI-11-011 a CI-11-016 | Identidad, listas, control y perfiles |
| Riesgos | CI-11-002 y CI-11-008 | Criterios y revisión |
| Restricciones | CI-11-026, CI-11-027 y CI-11-031 | Medidas y alcance |
| Gestión administrativa | CI-12-010 a CI-12-014 | Expedientes, tareas y aprobaciones |
| Documentos y comprobantes | CI-12-017, CI-12-019 y CI-12-021 | Evidencias y preservación |
| Comunicaciones | CI-12-028 y CI-12-031 | Puesta a disposición y seguimiento |
| Responsables de productos | Contratos de cada responsable | Inventario y desvinculación |

Cuando una actuación exige preparación de otra autoridad, se utiliza su protocolo explícito.

Las consultas informativas no sustituyen esas preparaciones.

Las conclusiones reservadas de cumplimiento no se incluyen en motivos comunicables.

## 13. MinIO y Redis

Los cuatro servicios utilizan Documentos y comprobantes para conservar evidencias.

Mantienen referencias y versiones. No escriben directamente en MinIO.

Redis no se utiliza inicialmente para decisiones de admisión, continuidad, representación o preparaciones, ni como caché
general de legajos.

Un uso posterior requiere limitar atributos, controlar finalidad y comprobar vigencia en la fuente cuando corresponda.

La indisponibilidad de caché no produce identidad verificada, admisión ni facultad favorable.

## 14. Respuestas y errores

| HTTP | Significado |
| --- | --- |
| 200 | Consulta o actuación completada |
| 201 | Creación confirmada |
| 202 | Aceptación durable pendiente |
| 400 | Entrada inválida |
| 401 | Autenticación inválida o ausente |
| 403 | Permiso técnico insuficiente |
| 404 | Objeto no localizado |
| 409 | Conflicto |
| 422 | Impedimento funcional |
| 503 | Dependencia indisponible |

Una comprobación completada con facultad insuficiente puede responder 200 con resultado negativo.

Los códigos específicos incluyen:

- IDENTITY_MATCH_REQUIRES_REVIEW.

- PARTY_VERSION_CONFLICT.

- CONTACT_CONFIRMATION_REQUIRED.

- DUPLICATE_RESOLUTION_CONFLICT.

- MANDATORY_VERIFICATION_PENDING.

- ADMISSION_EXPIRED.

- ADMISSION_WITHDRAWN.

- ADMISSION_USE_CONFLICT.

- CUSTOMER_ALREADY_ACTIVE.

- OFFBOARDING_COMPONENT_PENDING.

- REPRESENTATION_SCOPE_INSUFFICIENT.

- REPRESENTATION_CONTINUITY_PENDING.

- JOINT_REPRESENTATIVES_MISSING.

- PREPARATION_CONTENT_CONFLICT.

- INCOMPATIBLE_CHANGE_PENDING.

- DECISION_PENDING.

Los errores no revelan personas, expedientes o atributos fuera del alcance autorizado.

## 15. Observabilidad

Se observan:

- Coincidencias de identidad pendientes.

- Cambios personales parcialmente aplicados.

- Vinculaciones y requerimientos.

- Antigüedad de verificaciones.

- Admisiones próximas a vencer.

- Constituciones pendientes después del uso.

- Revisiones atrasadas.

- Inventarios sin respuesta.

- Representaciones pendientes de continuidad.

- Preparaciones pendientes y antigüedad.

- Revocaciones con componentes sin resolver.

- Peticiones de aborto.

- Decisiones recuperadas.

- Duplicados, conflictos, outbox y trabajos.

Los identificadores personales no se utilizan como etiquetas de métricas.

Las trazas vinculan el proceso. La evidencia durable permanece en los registros del negocio.

## 16. Pruebas y aceptación

| Situación | Resultado exigido |
| --- | --- |
| Identificador y antecedentes compatibles | Recupera persona sin duplicarla |
| Mismo nombre e identidades diferentes | Mantiene personas separadas |
| Fecha de nacimiento faltante | Ausencia explícita |
| Nuevo contacto | No autoriza por sí solo su sustitución |
| Actualizaciones concurrentes | Sin sobrescritura de versiones posteriores |
| Duplicados con productos diferentes | No fusiona saldos ni titularidades |
| Presentación oportuna | No vence mientras se revisa |
| Demora interna | No se atribuye al solicitante |
| Canal indisponible al vencer | Extensión comprobable |
| Admisión retirada antes del uso | No confirma uso |
| Uso confirmado y respuesta perdida | Recupera la misma constitución |
| Admisión vence después del uso válido | No anula retroactivamente el uso |
| Constituciones concurrentes | Un cliente y período equivalente |
| Readmisión | Conserva cliente y abre otro período |
| Revisión incompleta | Escala sin aprobación ni bloqueo automáticos |
| Inventario sin respuesta | No presume ausencia de productos |
| Poder de consulta | No permite transferir |
| Actuación conjunta incompleta | No prepara favorablemente |
| Consulta informativa | No crea una preparación |
| Revocación previa a preparar | Impide preparación incompatible |
| Revocación concurrente | Resuelve orden frente a la decisión |
| Cambio del fundamento delegante | Alcanza preparaciones derivadas |
| Aborto confirmado | Impide confirmar el mismo intento |
| Límite vencido y decisión desconocida | Mantiene recuperación |
| Coordinador indisponible | No presume aborto ni confirmación |
| Notificación repetida | No duplica conclusión |
| Renovación atrasada | No reactiva representación revocada |
| Evento repetido | No duplica período ni actuación |

Las pruebas de concurrencia, unicidad y commit utilizan PostgreSQL real aislado.

Los simuladores permiten comprobar interrupciones, respuestas tardías y competencia entre confirmación y aborto.

## 17. Correspondencia funcional

| Operación | Contratos principales |
| --- | --- |
| OF-02-01. Registrar persona | CI-02-001, CI-02-002 |
| OF-02-02. Actualizar datos | CI-02-003, CI-02-022, CI-02-025 |
| OF-02-03. Relaciones y composición | CI-02-004, CI-02-005, CI-02-026 |
| OF-02-04. Resolver duplicados | CI-02-006, CI-02-022, CI-02-027 |
| OF-02-05. Circunstancias especiales | CI-02-007, CI-02-028 |
| OF-02-06. Solicitar vinculación | CI-02-008, CI-02-009, CI-02-010, CI-02-012 |
| OF-02-07. Resolver admisión | CI-02-011, CI-02-012, CI-02-013, CI-02-029 |
| OF-02-08. Constituir cliente | CI-02-013, CI-02-014, CI-02-023, CI-02-030 |
| OF-02-09. Revisar relación comercial | CI-02-015, CI-02-016, CI-02-031 |
| OF-02-10. Desvincular y readmitir | CI-02-017, CI-02-008, CI-02-014, CI-02-032 |
| OF-02-11. Reconocer representación | CI-02-018, CI-02-020, CI-02-024, CI-02-033 |
| OF-02-12. Modificar o terminar representación | CI-02-019, CI-02-024, CI-02-033 |
| OF-02-13. Comprobar facultades | CI-02-021 |

La preparación de representación concreta su participación en decisiones coordinadas. Mantiene la propiedad de las
facultades en Representación y la decisión final en el responsable de cada actuación.
