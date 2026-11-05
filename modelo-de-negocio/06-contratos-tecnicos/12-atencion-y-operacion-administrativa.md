# **Contratos técnicos — Módulo 12. Atención y operación administrativa**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**              | **Identificador técnico**   |
|---------------------------|-----------------------------|
| Reclamos                  | complaint-service           |
| Gestión administrativa    | administrative-case-service |
| Documentos y comprobantes | document-service            |
| Comunicaciones            | communication-service       |
| Extractos                 | statement-service           |

Comprende los contratos **CI-12-001 a CI-12-041**.

El módulo conserva la relación entre recepción, investigación, decisión, aprobación, ejecución, evidencia y entrega.

Los propietarios del negocio mantienen la autoridad sobre contratos, productos, dinero, deuda, posiciones y registros
contables.

Una aprobación no ejecuta una operación económica. Un documento no sustituye la confirmación de su fuente. La aceptación
técnica de un envío no acredita entrega al destinatario.

## **2. Convenciones comunes**

### **2.1. Transporte, contexto y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext. Las consultas mediante POST no crean compromisos ni
requieren clave de idempotencia.

Cada servicio ofrece la consulta común:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro de cada servicio.

Esta consulta recupera resultados utilizando la identidad conocida antes del envío, incluidos los identificadores
generados que pudieron perderse con la respuesta inicial.

Las interfaces particulares de consulta complementan ese seguimiento.

### **2.2. Tipos**

| **Tipo**          | **Representación**                                                     |
|-------------------|------------------------------------------------------------------------|
| UUID              | Identificador interno.                                                 |
| Version           | Entero positivo dentro del rango de long.                              |
| Code              | Cadena de 1 a 80 caracteres.                                           |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |
| Title             | Cadena de 1 a 200 caracteres.                                          |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| Instant           | Instante UTC.                                                          |
| Date              | Fecha YYYY-MM-DD.                                                      |
| Currency          | ARS o USD.                                                             |
| MoneyAmount       | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                  |
| ByteLength        | Entero no negativo de bytes.                                           |

Los saldos informativos que admiten signo lo declaran expresamente. Las cantidades y cotizaciones conservan la precisión
de su fuente.

Los cálculos de períodos utilizan America/Argentina/Buenos_Aires y Calendarios bancarios.

### **2.3. Estructuras comunes**

Se utilizan las referencias y resultados definidos en el documento principal.

PendingComponent contiene:

- componentCode: Code.

- responsibleService: Code.

- reasonCode: Code.

- reference: ObjectReference, cuando existe.

VerifiedEvidence contiene:

- sourceReference: ObjectReference.

- documentReferences: DocumentReference\[\].

- factOccurredAt: Instant, cuando se conoce.

- verificationStatus: DECLARED, PENDING, VERIFIED u OBSERVED.

Las estructuras enumeradas en este documento tienen campos obligatorios salvo indicación de campo generado, opcional o
condicional.

Las colecciones pueden estar vacías excepto cuando se exige expresamente contenido.

### **2.4. Resultados**

Los comandos devuelven el resultado común con:

- actionId y operationId.

- processingStatus.

- outcome.

- receivedAt.

- confirmedAt, cuando corresponde.

- objectReferences.

- confirmedEffects.

- pendingComponents.

- statusResource.

Se agrega resultDimension, con valores:

RECEPTION, DECISION, EXECUTION, CONSERVATION, ISSUANCE o DELIVERY.

processingStatus admite PENDING, COMPLETED y REQUIRES_REVIEW.

Los resultados particulares definen qué dimensión se confirmó. Completar una recepción no implica completar reparación,
emisión o entrega.

## **3. Recepción y acceso**

Las consultas, reclamos y denuncias se reciben aunque falte verificar identidad o representación.

El canal público o asistido invoca una interfaz interna con autenticación del canal. El presentante puede permanecer sin
identificar.

Presenter contiene:

| **Campo**                | **Tipo**              | **Condición**                                 |
|--------------------------|-----------------------|-----------------------------------------------|
| identificationMode       | DECLARED o IDENTIFIED | Obligatorio.                                  |
| displayName              | Title                 | Opcional si no fue informado.                 |
| partyId                  | UUID                  | Obligatorio con IDENTIFIED.                   |
| representedPartyId       | UUID                  | Si declara actuar por otra persona.           |
| responseContactReference | ObjectReference       | Si existe contacto registrado.                |
| declaredResponseContact  | Contacto declarado    | Alternativa para presentante no identificado. |

Un contacto declarado contiene channelCode y value, de hasta 254 caracteres. No se considera verificado.

La recepción limitada devuelve referencia visible, fecha, constancia y comprobaciones pendientes.

No revela información privada ni concede facultades de reparación.

Conocer una referencia de presentación no permite consultar el expediente protegido.

Las denuncias urgentes se derivan inmediatamente al servicio competente, sin esperar la clasificación ordinaria.

## **4. Reclamos**

### **4.1. Complaint**

| **Campo**                        | **Tipo**                                    |
|----------------------------------|---------------------------------------------|
| complaintId                      | UUID generado.                              |
| version                          | Version generada.                           |
| complaintReference               | ExternalReference generada.                 |
| presenter                        | Presenter.                                  |
| identityVerificationStatus       | UNVERIFIED, PENDING, VERIFIED o FAILED.     |
| representationVerificationStatus | NOT_REQUIRED, PENDING, VERIFIED o FAILED.   |
| receivedAt                       | Instant.                                    |
| channelCode                      | Code.                                       |
| description                      | Reason.                                     |
| affectedReferences               | ObjectReference\[\].                        |
| evidenceReferences               | DocumentReference\[\].                      |
| reasonCode                       | Code, cuando está clasificado.              |
| claimedAmount y currency         | Se informan juntos si se conoce el importe. |
| specializedProcedureReference    | ObjectReference, cuando corresponde.        |
| classificationDueAt              | Instant generado.                           |
| resolutionDueAt                  | Instant generado.                           |
| attentionStatus                  | Estado de atención.                         |
| resolutionStatus                 | Estado de resolución.                       |
| repairStatus                     | Estado de reparación.                       |
| communicationStatus              | Estado de comunicación.                     |
| closureStatus                    | Estado de finalización.                     |

| **Dimensión** | **Valores**                                                    |
|---------------|----------------------------------------------------------------|
| Atención      | RECEIVED, CLASSIFIED, UNDER_INVESTIGATION, RESOLVED, REOPENED. |
| Resolución    | PENDING, FAVORABLE, PARTIALLY_FAVORABLE, UNFAVORABLE.          |
| Reparación    | NOT_REQUIRED, PENDING, PARTIAL, CONFIRMED.                     |
| Comunicación  | PENDING, DELIVERED, FAILED, UNCERTAIN.                         |
| Finalización  | OPEN, CLOSED.                                                  |

### **4.2. Interfaces**

| **Contrato** | **operationId técnico**     | **Método y ruta**                              |
|--------------|-----------------------------|------------------------------------------------|
| CI-12-002    | registerComplaint           | POST /v1/complaints                            |
| CI-12-003    | actOnComplaintInvestigation | POST /v1/complaints/{id}/investigation-actions |
| CI-12-004    | resolveComplaint            | POST /v1/complaints/{id}/resolutions           |
| CI-12-005    | recordComplaintRepairResult | POST /v1/complaint-repairs/{id}/results        |
| CI-12-006    | actOnComplaintLifecycle     | POST /v1/complaints/{id}/lifecycle-actions     |
| CI-12-007    | getComplaint                | GET /v1/complaints/{id}                        |
| CI-12-008    | analyseComplaintCauses      | POST /v1/complaint-cause-analyses              |

### **4.3. Registrar y clasificar**

Recibe contexto, presentante, canal, recepción original, descripción, referencias afectadas, evidencias y clasificación
inicial si se conoce.

La urgencia se informa mediante urgent, Booleano, y urgencyReason, obligatorio cuando es verdadero.

No se exige conocer el identificador técnico de la operación.

Devuelve reclamo, constancia, plazos y verificaciones pendientes.

Las clasificaciones posteriores se registran como actuación de investigación CLASSIFY, con clasificación, responsable y
procedimiento especializado cuando corresponda.

### **4.4. Investigación**

actionType selecciona una modalidad excluyente:

| **Modalidad**         | **Datos**                                                                        |
|-----------------------|----------------------------------------------------------------------------------|
| CLASSIFY              | reasonCode, responsibleService y procedimiento especializado cuando corresponde. |
| REQUEST_INFORMATION   | Responsable, información requerida y vencimiento.                                |
| ADD_INFORMATION       | Solicitud atendida cuando existe, hechos, fuente y evidencias.                   |
| RECORD_FINDING        | Conclusión parcial y evidencias.                                                 |
| RECORD_CONTRADICTION  | Referencias contradictorias y responsable de tratamiento.                        |
| RESOLVE_CONTRADICTION | Contradicción, conclusión y evidencias.                                          |

Devuelve actuación, tareas relacionadas, hallazgos y pendientes.

No se vuelve a exigir al cliente documentación que el banco conserva y puede recuperar.

### **4.5. Resolución y reparación**

La resolución recibe conclusión, fundamento, evidencia, partes favorables y desfavorables, reparaciones y aprobaciones
aplicables.

Cada reparación contiene:

- repairId: UUID.

- repairType: ERROR_RESTITUTION, COMMERCIAL_COMPENSATION o ADMINISTRATIVE_CORRECTION.

- executorService: Code.

- concepts: conceptos reparados.

- expectedOutcomeCode: Code.

- originalOperationReference: cuando existe.

- approvalReference: cuando se exige.

- existingEffectReference: cuando el mismo efecto ya fue ejecutado.

Cada concepto identifica conceptCode y alcance. Para alcance monetario exige amount y currency; para alcance
administrativo exige administrativeScopeCode.

Las partes desfavorables y reparaciones discrecionales requieren preparación y aprobación separadas.

Devuelve resolución y reparaciones solicitadas. Reclamos no ejecuta directamente el reintegro.

El resultado de reparación incorpora:

- Referencia de actuación del ejecutor.

- Resultado: PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED o UNCERTAIN.

- Conceptos atendidos.

- Referencias de efectos.

- Evidencias.

- Fecha efectiva, cuando existe.

- Remanente.

Solo el ejecutor competente acredita sus efectos.

Una reparación ya realizada por Disputas, Pagos u otro procedimiento se vincula sin ordenar otra devolución.

### **4.6. Ciclo y plazos**

Las acciones son:

- REQUEST_COMMUNICATION: resolución y destinatarios.

- RECORD_COMMUNICATION: comunicación y evidencia comprobable.

- CLOSE: resolución, tratamiento de reparaciones y entrega.

- REOPEN: fundamento y nuevos antecedentes.

El cierre integral exige las tres dimensiones satisfechas. La reapertura conserva recepción e historia.

| **Actuación**                  | **Plazo**                           |
|--------------------------------|-------------------------------------|
| Constancia digital o directa   | En el acto.                         |
| Clasificación y asignación     | Hasta finalizar el siguiente hábil. |
| Respuesta definitiva ordinaria | Diez días hábiles.                  |
| Aviso de proximidad            | Dos días hábiles antes.             |
| Escalación por vencimiento     | Inmediata.                          |

El cómputo comienza el día siguiente a la recepción. Los procedimientos especializados conservan sus plazos.

Reclasificar o reasignar no reinicia el plazo del mismo problema.

### **4.7. Consulta y recurrencia**

La consulta devuelve únicamente secciones autorizadas. Las actuaciones extensas se paginan.

El análisis recibe período, ámbito y clasificaciones. Devuelve analysisId, corte, población examinada, causas, impacto,
responsables y acciones.

Cada acción contiene referencia, responsable, vencimiento y estado PROPOSED, IN_PROGRESS o VERIFIED.

Se distinguen reintegros, compensaciones y pérdidas. Una mejora no se considera cumplida por estar propuesta.

## **5. Gestión administrativa**

### **5.1. Estructuras**

AdministrativeCase contiene:

- caseId: UUID generado.

- version: Version generada.

- procedureReference: VersionedReference.

- objectReference: ObjectReference.

- requesterReference: ObjectReference.

- backgroundReferences: ObjectReference\[\].

- documentReferences: DocumentReference\[\].

- urgencyCode: Code.

- dueAt: Instant.

- responsibleReferences: ObjectReference\[\].

- status: OPEN, IN_PROGRESS, AWAITING_EXECUTION, COMPLETED o CANCELLED.

- pendingComponents: PendingComponent\[\].

AdministrativeTask contiene:

- taskId, caseId y version.

- taskType: COLLECT, REVIEW, APPROVE, EXECUTE o VERIFY.

- objectReference.

- requiredFunctionCode.

- dueAt.

- urgencyCode.

- separationRequirements: Code\[\].

- assigneeReference, cuando está asignada.

- status.

Los estados de tarea son:

UNASSIGNED, ASSIGNED, IN_PROGRESS, AWAITING_DECISION, AWAITING_EXECUTION, AWAITING_VERIFICATION, COMPLETED y CANCELLED.

La escalación se registra separadamente y no borra el estado ni el vencimiento.

### **5.2. Interfaces**

| **Contrato** | **operationId técnico**           | **Método y ruta**                                    |
|--------------|-----------------------------------|------------------------------------------------------|
| CI-12-001    | registerAdministrativeEnquiry     | POST /v1/administrative-enquiries                    |
| CI-12-009    | createAdministrativeProcedure     | POST /v1/administrative-procedures                   |
| CI-12-009    | actOnAdministrativeProcedure      | POST /v1/administrative-procedures/{id}/actions      |
| CI-12-010    | createAdministrativeCase          | POST /v1/administrative-cases                        |
| CI-12-010    | updateAdministrativeCase          | POST /v1/administrative-cases/{id}/updates           |
| CI-12-011    | createAdministrativeTask          | POST /v1/administrative-tasks                        |
| CI-12-011    | assignAdministrativeTask          | POST /v1/administrative-tasks/{id}/assignments       |
| CI-12-012    | recordAdministrativeDecision      | POST /v1/administrative-tasks/{id}/decisions         |
| CI-12-013    | checkAdministrativeApproval       | POST /v1/administrative-approval-checks              |
| CI-12-014    | actOnAdministrativeTaskExecution  | POST /v1/administrative-tasks/{id}/execution-actions |
| CI-12-015    | coordinateSimulatedExternalAction | POST /v1/administrative-external-actions             |
| CI-12-016    | getAdministrativeCase             | GET /v1/administrative-cases/{id}                    |
| CI-12-016    | getAdministrativeTask             | GET /v1/administrative-tasks/{id}                    |
| CI-12-016    | getAdministrativeProcedure        | GET /v1/administrative-procedures/{id}               |

### **5.3. Consultas y procedimientos**

La consulta administrativa recibe pregunta, presentante, canal, recepción, finalidad y objetos pertinentes.

Devuelve enquiryId, respuesta cuando está disponible, fuentes utilizadas, expediente o reclamo derivado y pendientes.

La derivación conserva la recepción original.

Un procedimiento contiene código, título, finalidad, entradas tipadas, pasos, responsables, transiciones, plazos,
escalaciones, servicios ejecutores y responsable de finalización.

Cada paso identifica stepCode, taskType, requiredFunctionCode, nextStepCodes y reglas de vencimiento y separación.

Las acciones son NEW_VERSION, SUBMIT_FOR_REVIEW, APPROVE, ENABLE y RETIRE.

La aprobación vincula versión y huella. Habilitar otra versión no migra silenciosamente expedientes iniciados.

### **5.4. Expedientes y tareas**

Crear expediente recibe contexto y los datos no generados de AdministrativeCase.

Actualizar agrega antecedentes, responsables o urgencia con fundamento y versión esperada. No sobrescribe historia.

Crear tarea recibe contexto y los datos no generados de AdministrativeTask.

Asignar o reasignar recibe responsable, función y fundamento. Conserva asignaciones anteriores y vencimiento.

Las urgentes se asignan inmediatamente. Las ordinarias sin asignación al finalizar el siguiente hábil se escalan.

### **5.5. Decisiones y aprobaciones**

La decisión recibe:

- Contexto.

- Propuesta y versión.

- Referencia del contenido exacto.

- contentHash.

- Decisión: REVIEWED, APPROVED o REJECTED.

- Fundamento.

- Alcance y condiciones.

- Vigencia, cuando corresponde.

- Evidencias.

Devuelve decisionId, actor verificado, facultades utilizadas y aprobación cuando existe.

La aprobación conserva preparador, aprobador, propuesta, versión y contenido.

Cuando se exige separación, el preparador no aprueba. Una facultad retirada antes de decidir impide utilizarla.

La comprobación de aprobación es una consulta. Recibe aprobación, propuesta, versión, huella, operación y actuación
previstas.

Devuelve:

- matches: Booleano.

- usable: Booleano.

- checkedAt: Instant.

- scopeReferences: ObjectReference\[\].

- conditionCodes: Code\[\].

- impedimentCodes: Code\[\].

- validUntil: cuando existe.

No consume la aprobación ni concede permiso genérico. El ejecutor comprueba además sus condiciones y la unicidad del
efecto.

### **5.6. Ejecución y actuaciones externas**

Las acciones de tarea son:

- RECORD_EXECUTION: ejecutor, resultado, efecto y evidencia.

- RECORD_VERIFICATION: ejecución comprobada y conclusión.

- ESCALATE: causa, destinatario y vencimiento.

- CONCLUDE: requisitos satisfechos y evidencia.

Aprobar sin ejecutar mantiene la tarea pendiente de ejecución.

La actuación externa simulada recibe participante, origen, destinatario, autenticidad, objeto, recepción, vencimiento y
documentos.

Devuelve expediente, verificaciones, derivaciones y respuesta.

Las medidas sobre productos se derivan a Restricciones; los reportes, a Información regulatoria; las actuaciones de
recuperación, a Cobranzas o Garantías.

Una autoridad no comprobada no recibe información protegida.

## **6. Documentos y comprobantes**

### **6.1. DocumentVersion**

| **Campo**                | **Tipo**                                    |
|--------------------------|---------------------------------------------|
| documentId               | UUID.                                       |
| documentVersion          | Version.                                    |
| documentVersionId        | UUID.                                       |
| documentTypeCode         | Code.                                       |
| originReference          | ObjectReference.                            |
| purpose                  | Code.                                       |
| classificationCode       | Code.                                       |
| mediaType                | Cadena de hasta 120 caracteres.             |
| byteLength               | ByteLength.                                 |
| contentHash              | Hash.                                       |
| integrityStatus          | PENDING, VERIFIED, CORRUPTED o UNAVAILABLE. |
| provenanceType           | ORIGINAL, COPY, DERIVED o RECONSTRUCTED.    |
| categoryCode             | Code, cuando está clasificado.              |
| previousVersionReference | DocumentReference, cuando existe.           |
| retentionApplications    | Referencias de políticas aplicadas.         |
| preservationReferences   | ObjectReference\[\].                        |

No se exponen claves internas de MinIO.

### **6.2. Interfaces**

| **Contrato** | **operationId técnico**        | **Método y ruta**                                     |
|--------------|--------------------------------|-------------------------------------------------------|
| CI-12-017    | createDocumentIngestion        | POST /v1/document-ingestions                          |
| CI-12-017    | uploadDocumentIngestionContent | PUT /v1/document-ingestions/{id}/content              |
| CI-12-017    | completeDocumentIngestion      | POST /v1/document-ingestions/{id}/completion          |
| CI-12-018    | issueDocument                  | POST /v1/document-issuances                           |
| CI-12-019    | getDocumentVersion             | GET /v1/documents/{id}/versions/{version}             |
| CI-12-019    | getDocumentVersionContent      | GET /v1/document-versions/{id}/content                |
| CI-12-020    | createDocumentRetentionPolicy  | POST /v1/document-retention-policies                  |
| CI-12-020    | actOnDocumentRetentionPolicy   | POST /v1/document-retention-policies/{id}/actions     |
| CI-12-021    | applyDocumentPreservation      | POST /v1/document-preservations                       |
| CI-12-021    | releaseDocumentPreservation    | POST /v1/document-preservations/{id}/releases         |
| CI-12-022    | prepareDocumentDisposition     | POST /v1/document-dispositions                        |
| CI-12-022    | actOnDocumentDisposition       | POST /v1/document-dispositions/{id}/execution-actions |
| CI-12-023    | recoverDocument                | POST /v1/document-recovery-actions                    |
| CI-12-024    | getDocumentAction              | GET /v1/document-actions/{actionId}                   |

### **6.3. Recepción y transferencia del archivo**

La recepción tiene modalidades excluyentes:

- INLINE: multipart con metadatos JSON y un archivo.

- DEFERRED: JSON con metadatos y carga binaria posterior.

Los metadatos incluyen origen, aportante, finalidad, tipo, fecha documental cuando se conoce, sensibilidad, tamaño
esperado, hash esperado y categoría cuando se conoce.

El límite por archivo es **25 MiB**, equivalente a **26.214.400 bytes**. No se admiten archivos vacíos.

Se aceptan PDF, JPEG, PNG, texto UTF-8 y CSV UTF-8, sujetos a la categoría documental. Los ejecutables y archivos
comprimidos no se admiten.

En carga diferida:

1.  La recepción crea ingestionId.

2.  Devuelve uploadExpiresAt, fijado a 24 horas desde la creación.

3.  El archivo se transfiere mediante PUT a la ruta de contenido.

4.  La finalización verifica tamaño, hash y formato real.

5.  Solo entonces confirma la versión documental.

La carga utiliza autenticación, correlación y clave de idempotencia. Su actionId y operationId se transmiten mediante
X-Action-Id y X-Operation-Id, porque el cuerpo contiene exclusivamente bytes.

Los encabezados adicionales son:

- Content-Type.

- Content-Length.

- X-Content-SHA256.

El servicio comprueba el contenido real. Un encabezado declarado no constituye prueba de integridad.

No se admiten cargas parciales ni Content-Range. Si la conexión se interrumpe, el intento incompleto no queda como
archivo confirmado.

Repetir una carga completa con el mismo hash y tamaño recupera el resultado. Otro contenido para el mismo ingreso
produce conflicto.

La finalización recibe contexto y referencia de ingreso. Comprueba también que los valores coincidan con la recepción
original.

Una carga no finalizada al vencer no se presenta como documento. Se conserva el seguimiento de la actuación y se retira
el objeto temporal mediante un trabajo recuperable.

La consulta por actionId permite conocer si el archivo fue recibido, verificado o confirmado antes de decidir una
reentrega.

### **6.4. Emisión y consulta**

Emitir recibe referencia única de emisión, tipo, fuente responsable, hecho certificado, resultado confirmado, datos
tipados, plantilla y aprobación o firma aplicable.

Los datos se validan contra la definición versionada del tipo documental. No se admiten mapas libres.

Devuelve issuanceId, documento, versión y hecho certificado.

Otra copia recupera la misma emisión. Una rectificación referencia el documento anterior.

La consulta de versión devuelve metadatos e integridad. La consulta de contenido devuelve bytes autorizados con tipo,
tamaño y hash.

Una copia parcial o disociada se identifica como derivada.

### **6.5. Conservación**

La política contiene categoría, fundamentos, plazo en años, hecho inicial, accesos, obligaciones concurrentes, alcance
histórico y aprobación separada.

Las acciones son SUBMIT, APPROVE, ENABLE y RETIRE.

| **Categoría**                      | **Plazo base** | **Inicio**                                                            |
|------------------------------------|----------------|-----------------------------------------------------------------------|
| Contratos y aceptaciones           | 10 años        | Último entre terminación y resolución de obligaciones.                |
| Comprobantes y evidencia operativa | 10 años        | Confirmación o resolución definitiva.                                 |
| Identificación y representación    | 10 años        | Última finalización aplicable.                                        |
| Reclamos y reparaciones            | 10 años        | Cierre definitivo y conclusión de actuaciones.                        |
| Expedientes y aprobaciones         | 10 años        | Finalización y resolución de consecuencias.                           |
| Comunicaciones obligatorias        | 10 años        | Entrega o conclusión de incidencia, considerando el proceso asociado. |
| Extractos y rectificaciones        | 10 años        | Emisión y relación entre versiones.                                   |
| Publicidad sin efecto contractual  | 2 años         | Finalización de campaña o último envío.                               |

Prevalece la fecha permitida más tardía. Si el hecho inicial no ocurrió, el plazo no se considera vencido.

### **6.6. Preservación y disposición**

Una preservación recibe causa, autoridad, caso, ámbito documental y condición de finalización.

Devuelve preservationId, bloqueo, cobertura comprobada, documentos alcanzados y pendientes.

La preservación por ámbito registra el control que bloquea nuevas disposiciones incompatibles antes de completar la
identificación documental.

El levantamiento requiere fundamento y autoridad. No levanta otras preservaciones.

La disposición recibe candidatos, versiones, políticas, fundamentos y aprobación separada.

Sus estados son:

CANDIDATE, UNDER_REVIEW, AUTHORIZED, BLOCKED, PREPARED, EXECUTING, PRIMARY_DELETION_CONFIRMED, COPIES_PENDING, COMPLETED
e INCIDENT.

Las acciones son REVIEW, AUTHORIZE, EXECUTE y RECORD_COPY_TREATMENT.

Antes de ejecutar se revisan nuevamente preservaciones y aprobación. Preservación y disposición utilizan el mismo
control documental local.

Una eliminación en curso impide afirmar preservación completa sin comprobar el resultado. Se detienen las partes todavía
cancelables.

No se informa eliminación física integral mientras falte acreditar el tratamiento de copias y respaldos.

### **6.7. Recuperación**

Las modalidades son:

- VERIFY_INTEGRITY.

- RECOVER_COPY.

- CORRECT_METADATA.

- REGISTER_RECTIFICATION.

- RECONSTRUCT.

- RECORD_LOSS.

Reciben documento, incidencia y evidencia. La recuperación de contenido utiliza una versión previamente incorporada
mediante el circuito de ingreso.

Una reconstrucción se identifica como tal. La restauración reaplica disposiciones y preservaciones antes de habilitar el
contenido.

La pérdida documental no modifica el hecho económico.

## **7. Comunicaciones**

### **7.1. Interfaces**

| **Contrato** | **operationId técnico**        | **Método y ruta**                             |
|--------------|--------------------------------|-----------------------------------------------|
| CI-12-025    | createCommunicationTemplate    | POST /v1/communication-templates              |
| CI-12-025    | actOnCommunicationTemplate     | POST /v1/communication-templates/{id}/actions |
| CI-12-026    | changeCommunicationPreferences | POST /v1/communication-preference-actions     |
| CI-12-027    | getCommunicationPreferences    | GET /v1/communication-preferences             |
| CI-12-028    | sendCommunication              | POST /v1/communications                       |
| CI-12-029    | recordCommunicationReceipt     | POST /v1/communications/{id}/receipts         |
| CI-12-030    | recoverCommunication           | POST /v1/communications/{id}/recovery-actions |
| CI-12-031    | getCommunication               | GET /v1/communications/{id}                   |

### **7.2. Plantillas y preferencias**

La plantilla contiene código, versión, finalidad, canales, contenido, campos tipados, fuentes, sensibilidad,
accesibilidad y aprobación funcional.

Cada campo identifica código, tipo, obligatoriedad y restricciones. Se utilizan valores discriminados de texto, fecha,
instante, importe, código o referencia.

Las acciones son NEW_VERSION, SUBMIT, APPROVE, ENABLE y RETIRE.

Un campo esencial faltante impide enviar. Contratos valida el contenido contractual.

Las preferencias reciben participante, finalidad, acción, canales, idioma, accesibilidad, contactos referenciados y
evidencia.

Las acciones son SET_PREFERENCE, GRANT_CONSENT y REVOKE_CONSENT.

Personas conserva contactos. Comunicaciones conserva preferencias y consentimiento.

Revocar publicidad impide nuevos envíos opcionales, sin impedir comunicaciones obligatorias por medios válidos.

### **7.3. Solicitud de comunicación**

Contiene:

- requestReference: ExternalReference única.

- originReference: ObjectReference.

- purpose: Code.

- communicationTypeCode: Code.

- recipients: CommunicationRecipient\[\], no vacío.

- contentMode: TEMPLATE o APPROVED_CONTENT.

- documentReferences: DocumentReference\[\].

- classificationCode: Code.

- allowedChannels: Code\[\], no vacío.

- usefulUntil: Instant, cuando existe límite.

TEMPLATE exige plantilla versionada y datos tipados.

APPROVED_CONTENT exige referencia inmutable al contenido aprobado.

Cada destinatario contiene:

- recipientId: UUID.

- partyReference: ObjectReference, cuando está identificado.

- contactReference: referencia versionada, cuando existe.

- mandatory: Booleano.

- deliveryRequirementCode: Code.

El servicio valida destino, finalidad y sensibilidad. Un contacto declarado no recibe información privada sin
verificación suficiente.

### **7.4. Intentos y constancias**

CommunicationAttempt contiene:

- attemptId: UUID.

- recipientId: UUID.

- channelCode: Code.

- adapterCode: Code.

- contactVersionReference: cuando corresponde.

- destinationSnapshotReference: referencia protegida.

- attemptedAt: Instant.

- result: estado conocido.

- externalReference: cuando el adaptador la proporcionó.

- evidenceReferences: ObjectReference\[\].

Los resultados son:

PREPARED, SENT, PROVIDER_ACCEPTED, MADE_AVAILABLE, DELIVERED, REJECTED y UNCERTAIN.

Una constancia contiene referencia única, intento, destinatario, resultado, fecha y evidencia.

Mailpit acredita aceptación técnica cuando ese es el hecho informado. No se transforma en entrega al destinatario.

La recepción de una constancia tardía actualiza el intento original sin borrar antecedentes.

### **7.5. Recuperación**

Las modalidades son:

- QUERY_ATTEMPT.

- RETRY_CONFIRMED_FAILURE.

- USE_ALTERNATIVE_CHANNEL.

- OPEN_ASSISTED_TASK.

- STOP_OBSOLETE_SEND.

Ante incertidumbre se consulta antes de reenviar. Sin prueba de ausencia de envío se mantiene seguimiento y escalación.

Las fallas transitorias comprobadas permiten reintentos a los cinco y treinta minutos.

Si persisten, se utiliza una alternativa verificada y autorizada o se abre atención asistida para el siguiente hábil,
antes si existe urgencia.

Antes de nuevos despachos opcionales se vuelve a comprobar consentimiento.

La consulta por actionId permite recuperar communicationId si se perdió la respuesta inicial. Consultar la comunicación
no reenvía.

## **8. Extractos**

### **8.1. Interfaces**

| **Contrato** | **operationId técnico**   | **Método y ruta**                              |
|--------------|---------------------------|------------------------------------------------|
| CI-12-032    | createStatementDefinition | POST /v1/statement-definitions                 |
| CI-12-032    | actOnStatementDefinition  | POST /v1/statement-definitions/{id}/actions    |
| CI-12-033    | composeStatement          | POST /v1/statement-compositions                |
| CI-12-034    | issueStatement            | POST /v1/statement-compositions/{id}/issuances |
| CI-12-035    | rectifyStatement          | POST /v1/statements/{id}/rectifications        |
| CI-12-036    | getStatement              | GET /v1/statements/{id}                        |
| CI-12-036    | listStatementVersions     | GET /v1/statements/{id}/versions               |

### **8.2. Definiciones y composición**

Las modalidades son:

- MONTHLY_ACCOUNT_STATEMENT.

- MONTHLY_INVESTMENT_REPORT.

- REQUESTED_MONTHLY_CONSOLIDATED.

- AS_OF_CERTIFICATE.

La definición contiene versión, modalidad, productos, fuentes, campos, periodicidad, controles, destinatarios y
aprobación.

Las acciones son NEW_VERSION, SUBMIT, APPROVE, ENABLE y RETIRE.

El período mensual es calendario. El corte es el inicio del mes siguiente, excluido del período.

La emisión ordinaria se realiza dentro de los dos primeros hábiles siguientes.

Componer recibe contexto, definición versionada, titulares autorizados, productos, monedas y variante temporal:

- PERIOD: fromDate, toDateExclusive y cutoffAt.

- AS_OF: asOfDate y cutoffAt.

Devuelve:

- compositionId: UUID.

- version: Version.

- statementDefinitionReference: VersionedReference.

- Período o fecha.

- sourceSnapshots: fuentes comprobadas.

- sections: secciones tipadas.

- controls: resultados de control.

- limitations: Code\[\].

- compositionStatus: COMPOSING, INCOMPLETE, VALIDATED o REJECTED.

- contentHash: cuando existe contenido compuesto.

Cada fuente identifica servicio, objeto, corte, versión o secuencia y completitud COMPLETE, PARTIAL o UNAVAILABLE.

### **8.3. Secciones**

Cada sección contiene sectionType, productReference, moneda cuando corresponde y contenido específico.

| **Tipo**                 | **Contenido**                                                                                         |
|--------------------------|-------------------------------------------------------------------------------------------------------|
| ACCOUNT                  | Saldo inicial, créditos, débitos, saldo final, movimientos, reservas, inmovilizaciones y descubierto. |
| TERM_DEPOSIT             | Capital, actualización, rendimiento, vencimiento y pagos.                                             |
| INVESTMENT               | Instrumento, cantidad, disponibilidad, tránsito, valuación y fuente.                                  |
| CARD_STATEMENT_REFERENCE | Resumen versionado y documento emitidos por Cuenta de tarjeta.                                        |

Los movimientos de cuenta identifican referencia, fecha, sentido, importe y concepto.

Las reservas no se presentan como débitos.

Se comprueba:

**Saldo final = saldo inicial + créditos − débitos.**

Las monedas se presentan separadamente. Una conversión informativa conserva cotización y fecha.

Extractos no recalcula total, deuda o vencimiento del resumen de tarjeta.

### **8.4. Emisión y rectificación**

Emitir recibe composición y versión, huella, referencia única de emisión, destinatarios y modalidad de entrega.

La emisión conserva fuentes, contenido y documento inmutables.

Los faltantes esenciales impiden emitir como completo.

Una emisión parcial excepcional exige aprobación, condición PARTIAL y limitaciones visibles. No sustituye el extracto
completo pendiente.

Rectificar recibe versión original, motivo, corrección de origen, diferencias y nueva composición validada.

Devuelve nueva versión, documento, relación rectificativa y comunicación.

Si cambia el saldo final, se revisa continuidad con períodos siguientes. No se incorporan movimientos posteriores al
corte ni se repiten correcciones económicas.

## **9. Eventos**

### **9.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                    | **Tema**                           |
|--------------|-----------------------------|------------------------------------|
| CI-12-037    | ComplaintChanged            | banking.complaint.events           |
| CI-12-038    | AdministrativeActionChanged | banking.administrative-case.events |
| CI-12-039    | DocumentActionChanged       | banking.document.events            |
| CI-12-040    | CommunicationOutcomeChanged | banking.communication.events       |
| CI-12-041    | StatementIssuedOrRectified  | banking.statement.events           |

La versión inicial de esquema es 1. La clave es aggregateId.

### **9.2. CI-12-037 — ComplaintChanged**

aggregateType es COMPLAINT.

Payload obligatorio:

- complaintId: UUID.

- complaintVersion: Version.

- changeType: RECEIVED, CLASSIFIED, INVESTIGATION_UPDATED, RESOLVED, REPAIR_UPDATED, COMMUNICATION_UPDATED, CLOSED o
  REOPENED.

- Los cinco estados del reclamo.

- pendingComponents: PendingComponent\[\].

Campos condicionales:

- resolutionReference: al resolver.

- repairReference y effectReferences: al actualizar reparación.

- communicationReference: al actualizar comunicación.

- resolutionDueAt: cuando existe plazo determinado.

Consumidores: Gestión administrativa y servicios competentes relacionados.

### **9.3. CI-12-038 — AdministrativeActionChanged**

aggregateType admite ADMINISTRATIVE_PROCEDURE, ADMINISTRATIVE_CASE, ADMINISTRATIVE_TASK y
ADMINISTRATIVE_EXTERNAL_ACTION.

Payload obligatorio:

- objectReference: referencia versionada del agregado.

- changeType: CREATED, UPDATED, ASSIGNED, DECIDED, EXECUTION_RECORDED, VERIFIED, ESCALATED o CONCLUDED.

- pendingComponents: PendingComponent\[\].

Campos condicionales:

- caseReference: si pertenece a un expediente diferente del agregado.

- taskReference: si la actuación afecta una tarea vinculada.

- decisionReference: con DECIDED.

- proposalReference y contentHash: si existe aprobación.

- executionReference: con ejecución registrada.

- dueAt: cuando existe plazo.

Consumidores: solicitante, ejecutor competente y responsables de seguimiento.

El evento de aprobación no constituye una orden genérica de ejecución.

### **9.4. CI-12-039 — DocumentActionChanged**

aggregateType admite DOCUMENT, DOCUMENT_INGESTION, DOCUMENT_ISSUANCE, DOCUMENT_RETENTION_POLICY, DOCUMENT_PRESERVATION,
DOCUMENT_DISPOSITION y DOCUMENT_RECOVERY.

Payload obligatorio:

- documentActionId: UUID.

- changeType: INGESTION_UPDATED, VERSION_CONFIRMED, ISSUED, INTEGRITY_CHANGED, RETENTION_CHANGED, PRESERVATION_CHANGED,
  DISPOSITION_CHANGED o RECOVERY_CHANGED.

- documentReferences: DocumentReference\[\].

- pendingComponents: PendingComponent\[\].

Campos condicionales:

- integrityStatus: para cambio de integridad.

- policyReference: para conservación.

- preservationReference y coverageComplete: para preservación.

- dispositionReference y dispositionStatus: para disposición.

- recoveryReference y provenanceType: para recuperación.

- issuanceReference: para emisión.

No transporta archivos ni causas reservadas de preservación a consumidores generales.

### **9.5. CI-12-040 — CommunicationOutcomeChanged**

aggregateType es COMMUNICATION.

Payload obligatorio:

- communicationId: UUID.

- requestReference: ExternalReference.

- originReference: ObjectReference.

- attemptId: UUID.

- recipientId: UUID.

- channelCode: Code.

- result: estado del intento.

- resultAt: Instant.

- evidenceReferences: ObjectReference\[\].

- documentReferences: DocumentReference\[\].

- mandatoryRecipientsSatisfied: Booleano.

- pendingComponents: PendingComponent\[\].

Consumidores: servicio originante y responsables autorizados de atención y documentación.

El origen determina las consecuencias contractuales o económicas. Cuenta de tarjeta incorpora su evidencia de entrega
mediante su contrato específico.

### **9.6. CI-12-041 — StatementIssuedOrRectified**

aggregateType es STATEMENT.

Payload obligatorio:

- statementId: UUID.

- statementVersion: Version.

- changeType: ISSUED o RECTIFIED.

- definitionReference: VersionedReference.

- cutoffAt: Instant.

- productReferences: ObjectReference\[\].

- condition: COMPLETE o PARTIAL.

- documentReference: DocumentReference.

- issuedAt: Instant.

- limitations: Code\[\].

- pendingComponents: PendingComponent\[\].

Incluye período o fecha, de forma excluyente.

RECTIFIED exige previousStatementReference y reasonCode.

No representa un movimiento económico.

## **10. Seguridad, concurrencia y recuperación**

Se separan permisos de recepción, consulta privada, preparación, aprobación, ejecución, preservación y disposición.

Las investigaciones reservadas no se revelan por estar vinculadas a un reclamo general.

Cada servicio conserva autoridad en PostgreSQL. Documentos administra exclusivamente el acceso a MinIO.

Redis puede acelerar consultas sin decidir permisos, aprobaciones, preservaciones o disposiciones.

Las operaciones conservan intención antes de invocar dependencias. La transacción local confirma agregado, historial,
resultado, idempotencia y outbox.

Los consumidores registran inbox y efecto local antes de avanzar el procesamiento.

La misma ejecución recibida por HTTP y evento se reconoce mediante su identidad original.

Ante una respuesta perdida se consulta por actionId. Un 404 no se interpreta como prueba de que una solicitud en
tránsito nunca llegará.

No se mantienen transacciones SQL abiertas durante llamadas remotas o transferencia de archivos.

La autorización se comprueba también al recuperar resultados históricos.

## **11. Errores particulares**

| **HTTP** | **Código**                           | **Significado**                                           |
|----------|--------------------------------------|-----------------------------------------------------------|
| 400      | INVALID_REQUEST                      | Estructura, tipo o modalidad inválida.                    |
| 401      | AUTHENTICATION_REQUIRED              | Falta autenticación exigida.                              |
| 403      | ACCESS_NOT_AUTHORIZED                | Facultades, finalidad o representación insuficientes.     |
| 404      | RESOURCE_NOT_FOUND                   | Recurso inexistente o no visible.                         |
| 409      | IDEMPOTENCY_CONTENT_CONFLICT         | Identidad reutilizada con otro contenido.                 |
| 409      | VERSION_CONFLICT                     | Versión incompatible.                                     |
| 409      | APPROVAL_CONTENT_MISMATCH            | Aprobación de otro contenido.                             |
| 409      | DOCUMENT_UPLOAD_CONTENT_CONFLICT     | Otro archivo para el mismo ingreso.                       |
| 413      | DOCUMENT_TOO_LARGE                   | Archivo superior al límite admitido.                      |
| 415      | DOCUMENT_MEDIA_TYPE_NOT_ALLOWED      | Formato no admitido.                                      |
| 422      | SEPARATION_OF_DUTIES_REQUIRED        | Preparador y aprobador incompatibles.                     |
| 422      | COMPLAINT_CLOSURE_INCOMPLETE         | Faltan dimensiones de cierre.                             |
| 422      | DOCUMENT_UPLOAD_EXPIRED              | Venció la carga diferida.                                 |
| 422      | DOCUMENT_INTEGRITY_FAILED            | Tamaño, hash o contenido incompatible.                    |
| 422      | DOCUMENT_DISPOSITION_BLOCKED         | Conservación, preservación o aprobación impiden disponer. |
| 422      | COMMUNICATION_CHANNEL_NOT_ALLOWED    | Canal o contacto inadecuado.                              |
| 422      | OPTIONAL_COMMUNICATION_NOT_CONSENTED | Falta consentimiento vigente.                             |
| 422      | STATEMENT_SOURCES_INCOMPLETE         | Faltan fuentes esenciales.                                |
| 422      | STATEMENT_RECONCILIATION_FAILED      | Controles no satisfechos.                                 |
| 503      | DEPENDENCY_UNAVAILABLE               | Dependencia necesaria indisponible.                       |

Los errores utilizan Problem Details.

Si una actuación quedó aceptada durablemente y continúa pendiente, se devuelve 202 con seguimiento. No se presenta como
rechazo sin efectos.

## **12. Escenarios de aceptación**

| **Escenario**                                  | **Resultado exigido**                                                 |
|------------------------------------------------|-----------------------------------------------------------------------|
| Reclamo sin identidad comprobada               | Conserva recepción y constancia sin revelar información privada.      |
| Reclasificación o reasignación                 | Mantiene recepción y vencimientos aplicables.                         |
| Reparación ya ejecutada por otro procedimiento | Vincula el efecto original sin otro reintegro.                        |
| Preparador intenta aprobar                     | Rechaza cuando existe separación obligatoria.                         |
| Cambia el contenido aprobado                   | Exige nueva aprobación.                                               |
| Se pierde la respuesta de creación             | Recupera los identificadores mediante actionId.                       |
| Se interrumpe una carga binaria                | No confirma un documento incompleto.                                  |
| Se repite una carga completa idéntica          | Recupera el resultado sin duplicar versión.                           |
| El hash del archivo no coincide                | Impide confirmar integridad.                                          |
| Preservación compite con disposición           | Ordena ambas sobre el mismo control e informa incertidumbre real.     |
| Se levanta una de varias preservaciones        | Las restantes continúan bloqueando.                                   |
| Se restaura un respaldo                        | Reaplica disposiciones y preservaciones antes de habilitar contenido. |
| Mailpit acepta un correo                       | Registra aceptación técnica sin inventar entrega.                     |
| Se pierde la respuesta del proveedor           | Consulta antes de generar otro intento.                               |
| Se revoca publicidad antes del despacho        | Impide el nuevo envío opcional.                                       |
| Falta una fuente del extracto                  | No lo emite como completo.                                            |
| Se rectifica un extracto                       | Conserva versiones y no repite movimientos.                           |

Las pruebas de concurrencia y unicidad utilizan PostgreSQL real aislado. Las pruebas documentales comprueban el circuito
de MinIO y recuperación. Las pruebas de contrato verifican entradas, resultados, errores y eventos contra sus esquemas.
