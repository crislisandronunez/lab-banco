# **Diseño de persistencia — Módulo 1. Institución y oferta comercial**

## **1. Propósito y alcance**

Este documento define la persistencia de los siguientes servicios:

| **Servicio**            | **Datos propios**                                                             |
|-------------------------|-------------------------------------------------------------------------------|
| Institución             | Identidad institucional, organización, funciones, adscripciones y facultades. |
| Catálogo de productos   | Definiciones, requisitos, dependencias, compatibilidad y habilitación.        |
| Condiciones comerciales | Condiciones, ofertas, publicaciones y determinaciones.                        |
| Calendarios bancarios   | Calendarios, excepciones, convenciones y cálculos de fechas.                  |

Comprende los contratos **CI-01-001 a CI-01-025** y las preparaciones de autoridades del módulo.

Cada servicio mantiene su base PostgreSQL, usuario, migraciones y transacciones.

Las propuestas, aprobaciones, aplicaciones y habilitaciones son hechos diferentes.

Las consultas informativas no crean reservas ni preparaciones.

No existen claves foráneas ni consultas SQL entre bases de servicios distintos.

## **2. Convenciones de lectura**

### **2.1. Tipos y nulabilidad**

Se aplican los tipos del documento principal y estas precisiones:

| **Dato**                   | **Tipo**       |
|----------------------------|----------------|
| Nombre                     | varchar(200)   |
| Identidad de autenticación | varchar(255)   |
| País                       | varchar(2)     |
| Región o localidad         | varchar(120)   |
| Línea de domicilio         | varchar(250)   |
| Código postal              | varchar(20)    |
| Detalle de observación     | varchar(1000)  |
| Campo de observación       | varchar(200)   |
| Zona IANA                  | varchar(80)    |
| Importe contractual        | numeric(24,8)  |
| Proporción                 | numeric(24,12) |
| Entero de requisito        | bigint         |
| Decimal de requisito       | numeric(30,12) |

En las tablas:

- **N** significa que la columna es obligatoria.

- **S** significa que admite NULL con el significado indicado.

- Todos los UUID identifican objetos estables.

- Versiones de negocio son positivas.

- Versiones de control son no negativas.

- Los hashes tienen 64 caracteres hexadecimales en minúsculas.

Las columnas mencionadas conjuntamente en una fila tienen el mismo tipo y nulabilidad.

### **2.2. Relaciones e índices**

Todas las FK de este documento son locales y utilizan eliminación restringida.

Las PK y restricciones únicas generan sus índices correspondientes.

Los índices adicionales se indican con columnas ordenadas de izquierda a derecha. El orden es ascendente salvo
indicación expresa.

Las referencias remotas no se declaran como FK.

### **2.3. Bloques de columnas**

Los bloques siguientes representan columnas físicas repetidas. No constituyen tablas ni herencia de entidades.

Una tabla que incorpora un bloque incluye exactamente sus columnas.

## **3. Columnas repetidas**

### **3.1. Bloque R: identidad versionada**

Se incorpora en las raíces expresamente indicadas.

| **Columna**       | **Tipo**    | **Nulo** | **Regla**                                       |
|-------------------|-------------|----------|-------------------------------------------------|
| next_version      | bigint      | N        | Próxima versión de definición; comienza en 1.   |
| event_version     | bigint      | N        | Versión del agregado de eventos; comienza en 1. |
| lock_version      | bigint      | N        | Control local; comienza en 0.                   |
| created_action_id | uuid        | N        | FK a service_action.action_id.                  |
| created_at        | timestamptz | N        | Creación.                                       |

El contador se incrementa junto con la inserción de la versión.

La versión de eventos se incrementa únicamente cuando cambia el agregado correspondiente.

### **3.2. Bloque V: definición versionada**

| **Columna**             | **Tipo**    | **Nulo** | **Regla**                                            |
|-------------------------|-------------|----------|------------------------------------------------------|
| version                 | bigint      | N        | Versión positiva.                                    |
| effective_from          | timestamptz | N        | Inicio declarado.                                    |
| effective_to            | timestamptz | S        | Final declarado, posterior al inicio.                |
| schema_code             | varchar(80) | N        | Esquema de definición.                               |
| schema_version          | bigint      | N        | Versión positiva del esquema.                        |
| content_hash            | char(64)    | N        | Huella del contenido.                                |
| source_change_id        | uuid        | N        | Propuesta de origen.                                 |
| source_proposal_version | bigint      | N        | Revisión de origen.                                  |
| approval_evidence_id    | uuid        | S        | Aprobación comprobada; obligatoria antes de aplicar. |
| created_action_id       | uuid        | N        | FK a actuación.                                      |
| created_at              | timestamptz | N        | Registro de versión.                                 |

Relaciones del bloque:

- FK (source_change_id, source_proposal_version) a change_revision.

- FK de created_action_id a service_action.

- FK compuesta de aprobación y revisión a change_approval_evidence, cuando existe aprobación.

La definición del esquema comprende sus contenidos JSONB y colecciones relacionadas.

La huella excluye los estados técnicos de procesamiento y utiliza el contenido vinculante del contrato.

### **3.3. Bloque A: domicilio**

| **Columna**  | **Tipo**     | **Nulo** |
|--------------|--------------|----------|
| country_code | varchar(2)   | N        |
| region       | varchar(120) | N        |
| locality     | varchar(120) | N        |
| address_line | varchar(250) | N        |
| postal_code  | varchar(20)  | S        |

El país utiliza dos letras mayúsculas. Los demás valores presentes no son vacíos.

## **4. Estructuras comunes y propuestas**

Los cuatro servicios incorporan las siguientes tablas del principal:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message, cuando consumen eventos.

- persistent_job.

Institución, Catálogo y Condiciones comerciales incorporan authority_preparation.

Las tablas de propuestas de esta sección existen separadamente en cada base.

### **4.1. change_proposal**

Representa la identidad de una propuesta.

| **Columna**              | **Tipo**    | **Nulo** |
|--------------------------|-------------|----------|
| change_id                | uuid        | N        |
| capability_code          | varchar(80) | N        |
| change_type              | varchar(80) | N        |
| target_type              | varchar(80) | N        |
| target_id                | uuid        | S        |
| expected_target_version  | bigint      | S        |
| current_proposal_version | bigint      | N        |
| status                   | varchar(40) | N        |
| created_action_id        | uuid        | N        |
| created_at, updated_at   | timestamptz | N        |
| lock_version             | bigint      | N        |

PK: change_id.

FK: created_action_id → service_action.

Restricciones:

- Destinatario y versión esperada aparecen conjuntamente para objetos existentes.

- Una creación puede mantener ambos ausentes.

- current_proposal_version \> 0.

- lock_version \>= 0.

- El destinatario es una referencia local discriminada; se comprueba al aplicar.

Estados:

DRAFT, PENDING_APPROVAL, APPROVED, SCHEDULED, PENDING_DEPENDENCIES, APPLIED, REQUIRES_REVIEW, REJECTED y WITHDRAWN.

Índices:

- ix_change_proposal_target: target_type, target_id, created_at, change_id.

- ix_change_proposal_status: status, updated_at, change_id.

- ix_change_proposal_action: created_action_id.

### **4.2. change_revision**

Conserva el contenido completo de cada revisión.

| **Columna**              | **Tipo**      | **Nulo** |
|--------------------------|---------------|----------|
| change_id                | uuid          | N        |
| proposal_version         | bigint        | N        |
| schema_code              | varchar(80)   | N        |
| schema_version           | bigint        | N        |
| payload                  | jsonb         | N        |
| effective_from           | timestamptz   | N        |
| effective_to             | timestamptz   | S        |
| canonicalization_version | bigint        | N        |
| content_hash             | char(64)      | N        |
| prepared_by_reference    | jsonb         | N        |
| reason                   | varchar(4000) | N        |
| created_action_id        | uuid          | N        |
| created_at               | timestamptz   | N        |

PK: (change_id, proposal_version).

FK:

- change_id → change_proposal.

- created_action_id → service_action.

Restricciones:

- Versiones positivas.

- Final posterior al inicio.

- Preparador identificado mediante referencia verificada.

- Payload conforme a colección, modalidad y esquema.

- Revisión inmutable después de su registro.

Índices:

- ix_change_revision_schedule: effective_from, change_id, proposal_version.

- ix_change_revision_action: created_action_id.

- ix_change_revision_hash: content_hash, change_id, proposal_version.

La revisión vigente se comprueba bajo bloqueo de la propuesta.

### **4.3. change_approval_evidence**

Conserva la comprobación de una aprobación remota.

| **Columna**                            | **Tipo**    | **Nulo** |
|----------------------------------------|-------------|----------|
| approval_evidence_id                   | uuid        | N        |
| change_id                              | uuid        | N        |
| proposal_version                       | bigint      | N        |
| approval_reference                     | jsonb       | N        |
| approved_content_hash                  | char(64)    | N        |
| preparer_reference, approver_reference | jsonb       | N        |
| checked_at                             | timestamptz | N        |
| valid_until                            | timestamptz | S        |
| verification_result                    | varchar(20) | N        |
| evidence_schema_version                | bigint      | N        |
| evidence_payload                       | jsonb       | N        |

PK: approval_evidence_id.

Única: (approval_evidence_id, change_id, proposal_version).

FK: (change_id, proposal_version) → change_revision.

Resultados: VALID, INVALID y PENDING.

La validación comprueba:

- Propuesta y revisión.

- Huella aprobada.

- Identidades del preparador y aprobador.

- Separación exigida.

- Alcance y vigencia.

- Condiciones de utilización.

Índices:

- ix_change_approval_revision: change_id, proposal_version, checked_at.

- ix_change_approval_expiry: valid_until, approval_evidence_id, para verificaciones VALID con vencimiento.

### **4.4. change_application**

Conserva una aplicación recuperable de la revisión.

| **Columna**            | **Tipo**    | **Nulo** |
|------------------------|-------------|----------|
| application_id         | uuid        | N        |
| change_id              | uuid        | N        |
| proposal_version       | bigint      | N        |
| action_id              | uuid        | N        |
| result                 | varchar(30) | N        |
| result_schema_version  | bigint      | N        |
| result_references      | jsonb       | N        |
| applied_at             | timestamptz | S        |
| created_at, updated_at | timestamptz | N        |
| lock_version           | bigint      | N        |

PK: application_id.

Única: (change_id, proposal_version).

FK:

- Revisión a change_revision.

- Actuación a service_action.

Resultados: PENDING, APPLIED, REJECTED y REQUIRES_REVIEW.

APPLIED exige applied_at; los demás resultados mantienen ese campo ausente.

Un índice único parcial sobre change_id, para resultado APPLIED, impide aplicar dos revisiones de la misma propuesta.

Índices adicionales:

- ix_change_application_action: action_id.

- ix_change_application_pending: updated_at, application_id, para PENDING y REQUIRES_REVIEW.

### **4.5. change_dependency**

Conserva cada dependencia de aplicación.

| **Columna**                    | **Tipo**     | **Nulo** |
|--------------------------------|--------------|----------|
| dependency_id                  | uuid         | N        |
| application_id                 | uuid         | N        |
| dependency_code                | varchar(80)  | N        |
| component_key                  | varchar(120) | N        |
| responsible_service            | varchar(80)  | N        |
| remote_reference               | jsonb        | S        |
| remote_action_id               | uuid         | S        |
| status                         | varchar(30)  | N        |
| last_checked_at, next_check_at | timestamptz  | S        |
| result_schema_version          | bigint       | N        |
| result_payload                 | jsonb        | N        |
| lock_version                   | bigint       | N        |

PK: dependency_id.

Única: (application_id, dependency_code, component_key).

FK: application_id → change_application.

Estados internos: PENDING, CONFIRMED, REJECTED, UNCERTAIN y REQUIRES_REVIEW.

Índices:

- ix_change_dependency_due: next_check_at, dependency_id, para estados pendientes.

- ix_change_dependency_owner: responsible_service, status, dependency_id.

Una dependencia confirmada conserva su evidencia. Una dependencia incierta no se sustituye por otra identidad para
evitar recuperarla.

## **5. Servicio Institución: controles e identidad**

### **5.1. institution_initialization_control**

Serializa la constitución inicial.

| **Columna**   | **Tipo** | **Nulo** |
|---------------|----------|----------|
| singleton_key | smallint | N        |
| lock_version  | bigint   | N        |

PK: singleton_key.

Restricciones: singleton_key = 1, lock_version \>= 0.

La fila se incorpora mediante migración.

### **5.2. institution**

| **Columna**           | **Tipo**     | **Nulo**     |
|-----------------------|--------------|--------------|
| institution_id        | uuid         | N            |
| singleton_key         | smallint     | N            |
| Columnas del bloque R | Según bloque | Según bloque |

PK: institution_id.

Única: singleton_key.

Restricción: singleton_key = 1.

La base admite una sola identidad institucional.

### **5.3. institution_graph_control**

| **Columna**    | **Tipo**    | **Nulo** |
|----------------|-------------|----------|
| institution_id | uuid        | N        |
| lock_version   | bigint      | N        |
| updated_at     | timestamptz | N        |

PK y FK: institution_id → institution.

Ordena organización, funciones, adscripciones, facultades y preparaciones.

### **5.4. institution_version**

| **Columna**                 | **Tipo**     | **Nulo**     |
|-----------------------------|--------------|--------------|
| institution_id              | uuid         | N            |
| legal_name                  | varchar(200) | N            |
| trade_name                  | varchar(200) | S            |
| activity_codes              | jsonb        | N            |
| foundation_document_id      | uuid         | N            |
| foundation_document_version | bigint       | N            |
| Columnas del bloque V       | Según bloque | Según bloque |

PK: (institution_id, version).

FK: institution_id → institution, además de las del bloque V.

activity_codes es una colección de códigos sin duplicados.

El documento constitutivo es remoto.

### **5.5. institution_identifier**

| **Columna**         | **Tipo**     | **Nulo** |
|---------------------|--------------|----------|
| institution_id      | uuid         | N        |
| institution_version | bigint       | N        |
| identifier_type     | varchar(80)  | N        |
| identifier_value    | varchar(120) | N        |
| jurisdiction_code   | varchar(80)  | N        |

PK: las cinco columnas.

FK: (institution_id, institution_version) → institution_version.

Índice de búsqueda:

identifier_type, jurisdiction_code, identifier_value, institution_id.

La versión institucional exige al menos un identificador.

### **5.6. institution_address**

| **Columna**           | **Tipo**     | **Nulo**     |
|-----------------------|--------------|--------------|
| address_id            | uuid         | N            |
| institution_id        | uuid         | N            |
| institution_version   | bigint       | N            |
| position              | integer      | N            |
| Columnas del bloque A | Según bloque | Según bloque |

PK: address_id.

Única: (institution_id, institution_version, position).

FK: versión institucional.

Restricción: position \> 0.

La versión exige al menos un domicilio.

## **6. Organización, funciones y adscripciones**

### **6.1. Raíces organizativas**

| **Tabla**              | **PK**             | **Columnas adicionales al bloque R**      | **Unicidad**          |
|------------------------|--------------------|-------------------------------------------|-----------------------|
| organizational_unit    | unit_id uuid       | institution_id uuid N, code varchar(80) N | Institución y código. |
| institutional_function | function_id uuid   | institution_id uuid N, code varchar(80) N | Institución y código. |
| actor_assignment       | assignment_id uuid | institution_id uuid N                     | PK.                   |

Las tres utilizan FK a institution.

Los códigos son estables y no se reutilizan para otra identidad.

### **6.2. organizational_unit_version**

| **Columna**           | **Tipo**     | **Nulo**     |
|-----------------------|--------------|--------------|
| unit_id               | uuid         | N            |
| name                  | varchar(200) | N            |
| unit_type             | varchar(30)  | N            |
| parent_unit_id        | uuid         | S            |
| address_payload       | jsonb        | S            |
| function_codes        | jsonb        | N            |
| status                | varchar(20)  | N            |
| Columnas del bloque V | Según bloque | Según bloque |

PK: (unit_id, version).

FK:

- unit_id → organizational_unit.

- parent_unit_id → organizational_unit.

- Las del bloque V.

Tipos: ORGANIZATIONAL_UNIT y BRANCH.

Estados: ACTIVE, CLOSING e INACTIVE.

Restricciones:

- Superior distinto de la propia unidad.

- Sucursal exige domicilio completo conforme a Address.

- Colección de funciones sin duplicados.

- La raíz organizativa es la única modalidad que admite superior ausente.

La comprobación de raíz, institución común y ausencia de ciclos se realiza bajo institution_graph_control.

Índices:

- parent_unit_id, effective_from, unit_id, version.

- status, effective_from, unit_id, version.

### **6.3. unit_continuity_assignment**

| **Columna**         | **Tipo**    | **Nulo** |
|---------------------|-------------|----------|
| continuity_id       | uuid        | N        |
| unit_id             | uuid        | N        |
| unit_version        | bigint      | N        |
| task_source_service | varchar(80) | N        |
| task_type           | varchar(80) | N        |
| task_id             | uuid        | N        |
| responsible_unit_id | uuid        | N        |
| treatment_code      | varchar(80) | N        |
| verification_status | varchar(20) | N        |
| evidence_payload    | jsonb       | N        |

PK: continuity_id.

Única: unidad, versión, servicio, tipo e identidad de tarea.

FK:

- Versión de unidad.

- responsible_unit_id → organizational_unit.

Estados de comprobación: PENDING, VERIFIED y OBSERVED.

Índice: responsible_unit_id, verification_status, continuity_id.

No se cierra una unidad con responsabilidades pendientes sin tratamiento comprobado.

### **6.4. institutional_function_version**

| **Columna**           | **Tipo**     | **Nulo**     |
|-----------------------|--------------|--------------|
| function_id           | uuid         | N            |
| name                  | varchar(200) | N            |
| unit_id               | uuid         | N            |
| Columnas del bloque V | Según bloque | Según bloque |

PK: (function_id, version).

FK a función, unidad y las del bloque V.

Índice: unit_id, effective_from, function_id, version.

### **6.5. actor_assignment_version**

| **Columna**            | **Tipo**     | **Nulo**     |
|------------------------|--------------|--------------|
| assignment_id          | uuid         | N            |
| party_id               | uuid         | N            |
| identity_issuer        | varchar(255) | N            |
| identity_subject       | varchar(255) | N            |
| function_id, unit_id   | uuid         | N            |
| basis_document_id      | uuid         | N            |
| basis_document_version | bigint       | N            |
| Columnas del bloque V  | Según bloque | Según bloque |

PK: (assignment_id, version).

FK a adscripción, función, unidad y las del bloque V.

Persona y documento son referencias remotas.

Índices:

- identity_issuer, identity_subject, effective_from, assignment_id, version.

- party_id, effective_from, assignment_id, version.

- function_id, unit_id, effective_from, assignment_id, version.

La coherencia entre función y unidad se comprueba bajo el control institucional.

No se prohíben funciones simultáneas válidas; las incompatibilidades se comprueban para el acto.

## **7. Facultades y vigencias efectivas**

### **7.1. institutional_authority**

PK: authority_id uuid.

Incluye institution_id uuid N, FK a institución, y bloque R.

### **7.2. institutional_authority_version**

| **Columna**                | **Tipo**     | **Nulo**     |
|----------------------------|--------------|--------------|
| authority_id               | uuid         | N            |
| parent_authority_id        | uuid         | S            |
| parent_authority_version   | bigint       | S            |
| grantor_assignment_id      | uuid         | S            |
| grantor_assignment_version | bigint       | S            |
| recipient_function_id      | uuid         | N            |
| decision_code              | varchar(80)  | N            |
| scope_payload              | jsonb        | N            |
| subdelegation_allowed      | boolean      | N            |
| joint_rule_payload         | jsonb        | S            |
| status                     | varchar(20)  | N            |
| Columnas del bloque V      | Según bloque | Según bloque |

PK: (authority_id, version).

FK:

- Autoridad raíz.

- Facultad delegante y versión, cuando existen.

- Adscripción otorgante y versión, cuando existen.

- Función destinataria.

- Las del bloque V.

Restricciones:

- Cada pareja de identificador y versión aparece conjuntamente.

- No existe autodelegación.

- Estados ACTIVE, REVOKED y EXPIRED.

- Ausencia de otorgante solo para fundamento constitutivo comprobado.

- Regla conjunta presente cuando el acto la exige.

scope_payload utiliza AuthorityScope: institución y dimensiones explícitas ALL o LIST.

joint_rule_payload utiliza funciones admitidas, mínimo de actores distintos y pares incompatibles.

La delegación no amplía actos, ámbitos, importes, vigencia o subdelegación.

Índices:

- parent_authority_id, parent_authority_version.

- grantor_assignment_id, grantor_assignment_version.

- recipient_function_id, decision_code, effective_from, authority_id, version.

### **7.3. authority_money_limit**

| **Columna**       | **Tipo**      | **Nulo** |
|-------------------|---------------|----------|
| authority_id      | uuid          | N        |
| authority_version | bigint        | N        |
| currency          | varchar(3)    | N        |
| maximum_amount    | numeric(24,8) | N        |

PK: autoridad, versión y moneda.

FK a versión de facultad.

Moneda ARS o USD. Importe no negativo.

### **7.4. authority_dependency**

| **Columna**         | **Tipo**    | **Nulo** |
|---------------------|-------------|----------|
| dependency_id       | uuid        | N        |
| authority_id        | uuid        | N        |
| authority_version   | bigint      | N        |
| dependency_type     | varchar(20) | N        |
| parent_authority_id | uuid        | S        |
| assignment_id       | uuid        | S        |
| function_id         | uuid        | S        |
| unit_id             | uuid        | S        |

PK: dependency_id.

FK a facultad propietaria y a cada destino local presente.

El discriminador admite AUTHORITY, ASSIGNMENT, FUNCTION y UNIT.

Exactamente una columna de destino está presente.

Cada modalidad tiene unicidad parcial sobre facultad, versión y destino.

Los índices inversos utilizan cada destino seguido de authority_id, authority_version, para destinos presentes.

### **7.5. institutional_effectivity**

| **Columna**             | **Tipo**    | **Nulo** |
|-------------------------|-------------|----------|
| effectivity_id          | uuid        | N        |
| object_type             | varchar(30) | N        |
| object_id               | uuid        | N        |
| object_version          | bigint      | N        |
| effective_from          | timestamptz | N        |
| effective_to            | timestamptz | S        |
| application_id          | uuid        | N        |
| ended_by_application_id | uuid        | S        |

PK: effectivity_id.

FK de aplicaciones a change_application.

Tipos: INSTITUTION, UNIT, FUNCTION, ASSIGNMENT y AUTHORITY.

La referencia de objeto es local discriminada; se comprueba bajo el control institucional.

Una exclusión por tipo, objeto e intervalo impide aplicaciones efectivas superpuestas.

El final, cuando existe, es posterior al inicio.

La definición histórica no se modifica para cerrar el intervalo efectivo.

## **8. Servicio Catálogo de productos**

### **8.1. product**

| **Columna**             | **Tipo**     | **Nulo**     |
|-------------------------|--------------|--------------|
| product_id              | uuid         | N            |
| code                    | varchar(80)  | N            |
| next_enablement_version | bigint       | N            |
| Columnas del bloque R   | Según bloque | Según bloque |

PK: product_id.

Única: code.

next_enablement_version \> 0.

### **8.2. product_definition**

| **Columna**               | **Tipo**     | **Nulo**     |
|---------------------------|--------------|--------------|
| product_id                | uuid         | N            |
| name                      | varchar(200) | N            |
| family_code, variant_code | varchar(80)  | N            |
| currencies                | jsonb        | N            |
| participant_types         | jsonb        | N            |
| operation_codes           | jsonb        | N            |
| commercial_concepts       | jsonb        | N            |
| Columnas del bloque V     | Según bloque | Según bloque |

PK: (product_id, version).

FK a producto y las del bloque V.

Monedas y tipos de participante son colecciones no vacías, sin duplicados.

Operaciones y conceptos son colecciones tipadas, sin duplicados.

### **8.3. product_capability**

| **Columna**         | **Tipo**    | **Nulo** |
|---------------------|-------------|----------|
| product_id          | uuid        | N        |
| definition_version  | bigint      | N        |
| capability_code     | varchar(80) | N        |
| functional_version  | bigint      | N        |
| responsible_service | varchar(80) | N        |

PK: producto, definición y capacidad.

FK a product_definition.

Índice inverso: capability_code, product_id, definition_version.

### **8.4. product_requirement**

| **Columna**        | **Tipo**    | **Nulo** |
|--------------------|-------------|----------|
| product_id         | uuid        | N        |
| definition_version | bigint      | N        |
| requirement_id     | uuid        | N        |
| requirement_class  | varchar(20) | N        |
| attribute_code     | varchar(80) | N        |
| source_service     | varchar(80) | N        |
| value_type         | varchar(20) | N        |
| operator_code      | varchar(30) | N        |
| expected_values    | jsonb       | N        |
| required_stage     | varchar(20) | N        |
| missing_result     | varchar(20) | N        |

PK: producto, definición y requisito.

FK a product_definition.

Clases: ELIGIBILITY y CONTRACTING.

Tipos: TEXT, INTEGER, DECIMAL, DATE, BOOLEAN y CODE.

Etapas: PROPOSAL, CELEBRATION, EFFECT, CONSTITUTION y USE.

Ausencia: PENDING o INELIGIBLE.

expected_values es una colección de valores discriminados del tipo declarado.

Restricciones:

- PRESENT: colección vacía.

- EQUALS: un valor.

- IN: colección no vacía y sin duplicados.

- Operadores de orden: un valor ordenable.

- BETWEEN: dos extremos ordenados e inclusivos.

- No se utilizan operadores de orden con texto o booleano.

Índice: product_id, definition_version, required_stage, requirement_class.

### **8.5. Dependencias, compatibilidad y terminación**

| **Tabla**                | **Columnas específicas, todas obligatorias salvo indicación**                                                                                                                                                       | **PK y relaciones**                                                                   |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| product_dependency       | dependency_id uuid, product_id uuid, definition_version bigint, dependency_type varchar(20), required_product_id uuid S, capability_code varchar(80) S, required_stage varchar(20), responsible_service varchar(80) | PK producto, definición y dependencia. FK a definición y producto requerido presente. |
| product_compatibility    | rule_id uuid, product_id uuid, definition_version bigint, related_product_id uuid, relation_type varchar(20), required_stage varchar(20)                                                                            | PK producto, definición y regla. FK a definición y producto relacionado.              |
| product_termination_rule | rule_id uuid, product_id uuid, definition_version bigint, trigger_code varchar(80), treatment_code varchar(80), responsible_service varchar(80)                                                                     | PK producto, definición y regla. FK a definición.                                     |

En dependencias:

- PRODUCT exige producto y excluye capacidad.

- CAPABILITY exige capacidad y excluye producto.

- La misma dependencia equivalente no se repite en una definición.

Compatibilidad admite REQUIRED, ALLOWED e INCOMPATIBLE.

Existe unicidad por producto, definición, producto relacionado y etapa.

Los tratamientos de terminación son códigos registrados.

Índices:

- Dependencia por required_product_id, required_stage, para destino presente.

- Dependencia por capability_code, required_stage, para capacidad presente.

- Compatibilidad por related_product_id, required_stage, product_id.

- Terminación por product_id, definition_version, trigger_code.

## **9. Habilitación y preparación de productos**

### **9.1. product_enablement**

| **Columna**                 | **Tipo**    | **Nulo** |
|-----------------------------|-------------|----------|
| product_id                  | uuid        | N        |
| enablement_version          | bigint      | N        |
| definition_version          | bigint      | N        |
| channel_scope               | jsonb       | N        |
| segment_scope               | jsonb       | N        |
| status                      | varchar(20) | N        |
| previous_proposal_treatment | varchar(40) | N        |
| effective_from              | timestamptz | N        |
| effective_to                | timestamptz | S        |
| content_hash                | char(64)    | N        |
| source_change_id            | uuid        | N        |
| source_proposal_version     | bigint      | N        |
| application_id              | uuid        | N        |

PK: producto y habilitación.

FK a definición, revisión y aplicación locales.

Estados:

NOT_ENABLED, SCHEDULED, ENABLED, SUSPENDED y WITHDRAWN.

Tratamientos:

BLOCK_PENDING, ALLOW_WITHIN_EXISTING_VALIDITY y REVIEW_REQUIRED.

Los alcances utilizan ALL o LIST. LIST exige valores no vacíos y sin duplicados; ALL no contiene valores.

Índice: product_id, status, effective_from, enablement_version.

### **9.2. product_enablement_dependency**

| **Columna**            | **Tipo**     | **Nulo** |
|------------------------|--------------|----------|
| product_id             | uuid         | N        |
| enablement_version     | bigint       | N        |
| component_key          | varchar(120) | N        |
| capability_code        | varchar(80)  | N        |
| responsible_service    | varchar(80)  | N        |
| confirmation_reference | jsonb        | S        |
| result                 | varchar(20)  | N        |
| checked_at             | timestamptz  | S        |
| valid_until            | timestamptz  | S        |

PK: producto, habilitación y componente.

FK a habilitación.

Resultados: CONFIRMED, PENDING y REJECTED.

Confirmación exige referencia y fecha.

Índice: responsible_service, result, product_id, enablement_version.

### **9.3. product_enablement_effectivity**

| **Columna**             | **Tipo**    | **Nulo** |
|-------------------------|-------------|----------|
| effectivity_id          | uuid        | N        |
| product_id              | uuid        | N        |
| enablement_version      | bigint      | N        |
| effective_from          | timestamptz | N        |
| effective_to            | timestamptz | S        |
| application_id          | uuid        | N        |
| ended_by_application_id | uuid        | S        |

PK: effectivity_id.

FK a habilitación y aplicaciones.

Índice: product_id, effective_from, effectivity_id.

Las intersecciones de canales, segmentos y vigencias se comprueban bajo bloqueo de product.

No basta con comparar igualdad textual del JSONB.

### **9.4. product_preparation_control**

| **Columna**        | **Tipo**    | **Nulo** |
|--------------------|-------------|----------|
| preparation_id     | uuid        | N        |
| product_id         | uuid        | N        |
| definition_version | bigint      | N        |
| enablement_version | bigint      | N        |
| control_role       | varchar(80) | N        |
| scope_hash         | char(64)    | N        |

PK: preparación, producto y papel de control.

FK a preparación, definición y habilitación.

Índice: product_id, preparation_id.

Preparación y cambio incompatible bloquean la misma raíz de producto.

## **10. Servicio Condiciones comerciales**

### **10.1. commercial_product_control**

| **Columna**            | **Tipo**    | **Nulo** |
|------------------------|-------------|----------|
| product_id             | uuid        | N        |
| lock_version           | bigint      | N        |
| created_at, updated_at | timestamptz | N        |

PK: product_id.

El producto es remoto. No existe FK al Catálogo.

La fila se crea bajo unicidad antes de comprobar condiciones concurrentes.

### **10.2. Raíces comerciales**

| **Tabla**            | **PK**            | **Columnas adicionales al bloque R** |
|----------------------|-------------------|--------------------------------------|
| commercial_condition | condition_id uuid | product_id uuid N                    |
| commercial_offer     | offer_id uuid     | product_id uuid N                    |

Ambas utilizan FK local de product_id a commercial_product_control.

Índices:

- Condición: product_id, condition_id.

- Oferta: product_id, offer_id.

### **10.3. commercial_condition_version**

| **Columna**                                       | **Tipo**       | **Nulo**     |
|---------------------------------------------------|----------------|--------------|
| condition_id                                      | uuid           | N            |
| compatible_product_versions                       | jsonb          | N            |
| concept_code, trigger_code                        | varchar(80)    | N            |
| value_type                                        | varchar(30)    | N            |
| amount_value                                      | numeric(24,8)  | S            |
| ratio_value                                       | numeric(24,12) | S            |
| currency                                          | varchar(3)     | S            |
| base_code, periodicity_code                       | varchar(80)    | S            |
| rate_class_code, rate_period_code, day_count_code | varchar(80)    | S            |
| capitalization_code                               | varchar(80)    | S            |
| calculation_definition                            | jsonb          | S            |
| rounding_mode                                     | varchar(10)    | N            |
| final_scale                                       | smallint       | N            |
| priority                                          | integer        | N            |
| requirements                                      | jsonb          | N            |
| Columnas del bloque V                             | Según bloque   | Según bloque |

PK: condición y versión.

FK a raíz y las del bloque V.

Restricciones:

- Versiones compatibles: colección positiva no vacía y sin duplicados.

- priority \>= 0.

- Escala entre 0 y 8.

- Redondeo HALF_UP, UP o DOWN.

Variantes:

| **Modalidad**      | **Campos exigidos**                        | **Campos excluidos**  |
|--------------------|--------------------------------------------|-----------------------|
| FIXED_AMOUNT       | Importe y moneda                           | Proporción y cálculo. |
| RATE               | Proporción, clase, período y base temporal | Importe y cálculo.    |
| PERCENTAGE         | Proporción y base                          | Importe y cálculo.    |
| FORMULA_REFERENCE  | Cálculo registrado                         | Importe y proporción. |
| EXTERNAL_REFERENCE | Cálculo con fuente externa                 | Importe y proporción. |

Los requisitos utilizan RequirementDefinition.

El cálculo conserva mecanismo, versión y operandos tipados CONSTANT o SOURCE_ATTRIBUTE.

Una condición directamente ejecutable como dinero no tiene escala final superior a dos.

Índices:

- concept_code, trigger_code, effective_from, condition_id, version.

- source_change_id, source_proposal_version.

### **10.4. commercial_offer_version**

| **Columna**                  | **Tipo**     | **Nulo**     |
|------------------------------|--------------|--------------|
| offer_id                     | uuid         | N            |
| compatible_product_versions  | jsonb        | N            |
| channel_scope, segment_scope | jsonb        | N            |
| currencies                   | jsonb        | N            |
| priority                     | integer      | N            |
| preservation_allowed         | boolean      | N            |
| preservation_days            | integer      | S            |
| status                       | varchar(20)  | N            |
| previous_proposal_treatment  | varchar(40)  | S            |
| previous_offer_id            | uuid         | S            |
| Columnas del bloque V        | Según bloque | Según bloque |

PK: oferta y versión.

FK a raíz, oferta anterior presente y las del bloque V.

Estados: DRAFT, APPROVED, SCHEDULED, PUBLISHED y WITHDRAWN.

Se exige:

- Prioridad no negativa.

- Monedas y versiones compatibles no vacías, sin duplicados.

- Días positivos exclusivamente cuando se permite conservación.

- Alcances explícitos ALL o LIST.

- Tratamiento de propuestas presente cuando sustituye o retira.

Índice: status, effective_from, offer_id, version.

### **10.5. commercial_offer_condition**

| **Columna**       | **Tipo** | **Nulo** |
|-------------------|----------|----------|
| offer_id          | uuid     | N        |
| offer_version     | bigint   | N        |
| condition_id      | uuid     | N        |
| condition_version | bigint   | N        |

PK: oferta, versión y condición.

FK compuestas a oferta y condición.

Índice inverso: condition_id, condition_version, offer_id, offer_version.

La aplicación comprueba que oferta y condiciones corresponden al mismo producto remoto.

### **10.6. commercial_offer_publication**

| **Columna**             | **Tipo**    | **Nulo** |
|-------------------------|-------------|----------|
| publication_id          | uuid        | N        |
| offer_id                | uuid        | N        |
| offer_version           | bigint      | N        |
| product_id              | uuid        | N        |
| effective_from          | timestamptz | N        |
| effective_to            | timestamptz | S        |
| application_id          | uuid        | N        |
| ended_by_application_id | uuid        | S        |

PK: publication_id.

FK a oferta, control de producto y aplicaciones.

Índice: product_id, effective_from, publication_id.

Las ofertas pueden superponerse cuando su selección sea coherente.

Bajo el control de producto se comprueban alcance, monedas, versiones, prioridades y condiciones.

El menor número de prioridad prevalece.

Un empate incompatible impide aplicar la publicación.

## **11. Determinaciones y conservación comercial**

### **11.1. commercial_determination**

| **Columna**               | **Tipo**      | **Nulo** |
|---------------------------|---------------|----------|
| determination_id          | uuid          | N        |
| product_id                | uuid          | N        |
| product_version           | bigint        | N        |
| reference_time            | timestamptz   | N        |
| evaluated_at              | timestamptz   | N        |
| channel_code              | varchar(80)   | N        |
| segment_code              | varchar(80)   | S        |
| currency                  | varchar(3)    | N        |
| amount                    | numeric(24,8) | S        |
| term_quantity             | bigint        | S        |
| term_unit                 | varchar(20)   | S        |
| purpose                   | varchar(80)   | N        |
| schema_version            | bigint        | N        |
| input_payload             | jsonb         | N        |
| input_hash                | char(64)      | N        |
| result                    | varchar(20)   | N        |
| source_references         | jsonb         | N        |
| missing_requirements      | jsonb         | N        |
| completeness              | varchar(20)   | N        |
| preservation_allowed      | boolean       | N        |
| preservation_maximum_days | integer       | S        |
| preservation_valid_until  | timestamptz   | S        |

PK: determination_id.

El producto y su versión son remotos.

Resultados: RESOLVED, NO_OFFER, PENDING y CONFLICT.

Completitud: COMPLETE, PARTIAL y UNAVAILABLE.

Plazo: cantidad positiva y unidad conjunta CALENDAR_DAYS, BUSINESS_DAYS o MONTHS.

Conservación permitida exige días positivos y límite; si no se permite, ambos están ausentes.

Índices:

- product_id, evaluated_at, determination_id.

- input_hash, evaluated_at, determination_id.

No existe unicidad solo por huella.

La determinación es evidencia informativa inmutable, no reserva de oferta.

### **11.2. Relaciones de determinación**

| **Tabla**                          | **Columnas**                                                                                                                                                 | **PK y FK**                                                             |
|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| commercial_determination_offer     | determination_id uuid N, offer_id uuid N, offer_version bigint N                                                                                             | PK determinación y oferta. FK a determinación y versión de oferta.      |
| commercial_determination_condition | determination_id uuid N, position integer N, condition_id uuid N, condition_version bigint N, resolved_payload jsonb N, future_dependency_references jsonb N | PK determinación y posición. FK a determinación y versión de condición. |

La posición es positiva.

El contenido resuelto utiliza el esquema CommercialCondition y conserva la modalidad exacta del valor.

Índices inversos:

- Oferta, versión y determinación.

- Condición, versión y determinación.

### **11.3. commercial_preservation_evidence**

| **Columna**                       | **Tipo**    | **Nulo** |
|-----------------------------------|-------------|----------|
| preservation_evidence_id          | uuid        | N        |
| proposal_source_service           | varchar(80) | N        |
| proposal_id                       | uuid        | N        |
| proposal_version                  | bigint      | N        |
| determination_id                  | uuid        | N        |
| binding_content_hash              | char(64)    | N        |
| basis_payload                     | jsonb       | N        |
| authorized_from, authorized_until | timestamptz | N        |
| checked_action_id                 | uuid        | N        |
| checked_at                        | timestamptz | N        |

PK: preservation_evidence_id.

FK a determinación y actuación.

Única: propuesta propietaria, identidad, versión y determinación.

Restricción: final posterior al inicio.

Índice: proposal_source_service, proposal_id, proposal_version, authorized_until.

Las ofertas utilizadas se recuperan por la determinación vinculada.

La evidencia no extiende por sí misma una oferta ni levanta impedimentos de otros servicios.

## **12. Servicio Calendarios bancarios**

### **12.1. banking_calendar**

PK: calendar_id uuid.

Columnas adicionales:

- code varchar(80) N, único.

- Bloque R.

### **12.2. banking_calendar_version**

| **Columna**                     | **Tipo**     | **Nulo**     |
|---------------------------------|--------------|--------------|
| calendar_id                     | uuid         | N            |
| name                            | varchar(200) | N            |
| purpose_code, jurisdiction_code | varchar(80)  | N            |
| zone_id                         | varchar(80)  | N            |
| coverage_from, coverage_to      | date         | N            |
| source_reference                | varchar(120) | N            |
| status                          | varchar(20)  | N            |
| Columnas del bloque V           | Según bloque | Según bloque |

PK: calendario y versión.

FK a raíz y las del bloque V.

La cobertura incluye ambos extremos y exige final no anterior al inicio.

Estados: DRAFT, APPROVED, SCHEDULED, PUBLISHED y RETIRED.

La zona debe ser IANA válida.

Índice: status, effective_from, calendar_id, version.

### **12.3. Clasificación semanal**

calendar_ordinary_business_day:

| **Columna**      | **Tipo** | **Nulo** |
|------------------|----------|----------|
| calendar_id      | uuid     | N        |
| calendar_version | bigint   | N        |
| weekday          | smallint | N        |

PK: las tres columnas.

FK a versión de calendario.

Día entre 1 y 7: lunes es 1 y domingo es 7.

### **12.4. Excepciones**

calendar_exception:

| **Columna**      | **Tipo**      | **Nulo** |
|------------------|---------------|----------|
| calendar_id      | uuid          | N        |
| calendar_version | bigint        | N        |
| exception_date   | date          | N        |
| classification   | varchar(20)   | N        |
| reason           | varchar(4000) | N        |
| source_reference | varchar(120)  | N        |

PK: calendario, versión y fecha.

FK a versión.

Clasificaciones: BUSINESS_DAY y NON_BUSINESS_DAY.

La fecha pertenece a la cobertura declarada.

La excepción prevalece sobre la clasificación semanal.

### **12.5. calendar_publication**

| **Columna**             | **Tipo**    | **Nulo** |
|-------------------------|-------------|----------|
| publication_id          | uuid        | N        |
| calendar_id             | uuid        | N        |
| calendar_version        | bigint      | N        |
| effective_from          | timestamptz | N        |
| effective_to            | timestamptz | S        |
| application_id          | uuid        | N        |
| ended_by_application_id | uuid        | S        |

PK: publication_id.

FK a versión y aplicaciones.

Una exclusión por calendario e intervalo impide publicaciones efectivas superpuestas.

Publicar o retirar bloquea banking_calendar.

Retirar no modifica cálculos históricos ni reprograma obligaciones ajenas.

### **12.6. calendar_convention**

| **Columna**        | **Tipo**      | **Nulo** |
|--------------------|---------------|----------|
| convention_code    | varchar(80)   | N        |
| name               | varchar(200)  | N        |
| description        | varchar(4000) | N        |
| functional_version | bigint        | N        |
| enabled            | boolean       | N        |

PK: convention_code.

Códigos: NONE, FOLLOWING, PRECEDING y MODIFIED_FOLLOWING.

Identifican comportamientos registrados. No contienen código ejecutable.

## **13. Evidencias de cálculo de fechas**

### **13.1. date_calculation**

| **Columna**      | **Tipo**    | **Nulo** |
|------------------|-------------|----------|
| calculation_id   | uuid        | N        |
| operation_code   | varchar(30) | N        |
| schema_version   | bigint      | N        |
| input_payload    | jsonb       | N        |
| input_hash       | char(64)    | N        |
| calendar_id      | uuid        | S        |
| calendar_version | bigint      | S        |
| convention_code  | varchar(80) | S        |
| zone_id          | varchar(80) | N        |
| result           | varchar(30) | N        |
| occurrences      | jsonb       | N        |
| missing_ranges   | jsonb       | N        |
| evaluated_at     | timestamptz | N        |

PK: calculation_id.

FK:

- Calendario y versión, conjuntamente presentes.

- Convención, cuando existe.

Operaciones: CLASSIFY, SHIFT, ADJUST y GENERATE_SERIES.

Resultados: COMPLETE e INCOMPLETE_COVERAGE.

Los payloads utilizan los esquemas DateCalculationRequest, DateOccurrence y DateRange.

Restricciones:

- Referencia de fecha o instante, de forma excluyente.

- Instante exige zona.

- Calendario exigido para clasificación, días hábiles o ajuste distinto de NONE.

- Series con cantidad de ocurrencias o fecha final, de forma excluyente.

- Índices de ocurrencia no negativos.

- Rangos faltantes inclusivos y ordenados.

- Resultado completo sin rangos faltantes.

Índices:

- calendar_id, calendar_version, evaluated_at, calculation_id.

- input_hash, evaluated_at, calculation_id.

La conservación es informativa e inmutable. No crea una preparación ni un compromiso.

Las series conservan anclaje original y reglas de ajuste.

## **14. Relaciones de preparaciones**

### **14.1. institution_preparation_control**

| **Columna**                                       | **Tipo**    | **Nulo** |
|---------------------------------------------------|-------------|----------|
| preparation_control_id                            | uuid        | N        |
| preparation_id                                    | uuid        | N        |
| control_type                                      | varchar(20) | N        |
| authority_id, assignment_id, function_id, unit_id | uuid        | S        |
| control_version                                   | bigint      | N        |
| control_role                                      | varchar(80) | N        |
| scope_hash                                        | char(64)    | N        |

PK: preparation_control_id.

FK a preparación.

Exactamente un destino está presente, según AUTHORITY, ASSIGNMENT, FUNCTION o UNIT.

Cada destino y control_version utilizan FK a su tabla versionada correspondiente.

Por modalidad existe unicidad sobre preparación, destino, versión y papel.

Cada modalidad tiene índice inverso sobre destino y preparación, para destino presente.

### **14.2. commercial_preparation_control**

| **Columna**                                                      | **Tipo**    | **Nulo** |
|------------------------------------------------------------------|-------------|----------|
| preparation_control_id                                           | uuid        | N        |
| preparation_id                                                   | uuid        | N        |
| product_id                                                       | uuid        | N        |
| determination_id                                                 | uuid        | N        |
| control_type                                                     | varchar(30) | N        |
| offer_id, condition_id, publication_id, preservation_evidence_id | uuid        | S        |
| definition_version                                               | bigint      | S        |
| control_role                                                     | varchar(80) | N        |
| scope_hash                                                       | char(64)    | N        |

PK: preparation_control_id.

FK a preparación, control de producto y determinación.

Tipos:

- OFFER: oferta y versión presentes.

- CONDITION: condición y versión presentes.

- PUBLICATION: publicación presente, sin versión adicional.

- PRESERVATION: evidencia presente, sin versión adicional.

Exactamente un destino está presente.

Las referencias utilizan FK a su tabla local y, para oferta o condición, a su versión.

Índices:

- product_id, preparation_id.

- offer_id, preparation_id, para oferta presente.

- condition_id, preparation_id, para condición presente.

- publication_id, preparation_id, para publicación presente.

- preservation_evidence_id, preparation_id, para evidencia presente.

La repetición del mismo destino, versión y papel dentro de una preparación se impide mediante unicidad parcial por
modalidad.

## **15. JSONB y validación estructural**

Los campos JSONB no admiten mapas libres.

| **Contenido**                    | **Esquema**                                                  |
|----------------------------------|--------------------------------------------------------------|
| Propuesta                        | Variante exacta de la colección y change_type.               |
| Actor y aprobación               | Referencias verificadas y evidencia de aprobación.           |
| Domicilio                        | Address.                                                     |
| Alcance institucional            | AuthorityScope.                                              |
| Regla conjunta                   | JointApprovalRule.                                           |
| Valores esperados                | Colección de valores discriminados de RequirementDefinition. |
| Alcance comercial o habilitación | Modalidad ALL o LIST.                                        |
| Requisitos comerciales           | Colección de RequirementDefinition.                          |
| Cálculo comercial                | CalculationDefinition.                                       |
| Determinación                    | Entrada, condiciones resueltas y metadatos de su contrato.   |
| Cálculo de fechas                | Solicitud, ocurrencias y rangos faltantes.                   |

Se rechazan propiedades desconocidas, tipos incompatibles y duplicados no admitidos.

Los elementos con identidad o controles propios utilizan las tablas relacionadas definidas en este documento.

Las versiones de esquema permiten interpretar las instantáneas históricas.

## **16. Transacciones y orden de bloqueo**

### **16.1. Institución**

Orden para aplicaciones que modifican el grafo:

1.  Control de inicialización, únicamente para constitución inicial.

2.  Control institucional del grafo.

3.  Propuesta y aplicación.

4.  Raíces afectadas, por tipo e identificador.

5.  Versiones, dependencias y efectividad.

Las preparaciones adquieren el control del grafo antes de comprobar las facultades y registrar sus bases.

### **16.2. Catálogo**

Orden:

1.  Productos afectados, por identificador.

2.  Propuesta y aplicación.

3.  Definiciones o habilitaciones.

4.  Dependencias y efectividad.

Si un cambio necesita comprobar varios productos de forma indivisible, bloquea todas las raíces antes de aplicar.

### **16.3. Condiciones comerciales**

Orden:

1.  Controles de producto, por identificador.

2.  Propuesta y aplicación.

3.  Condiciones y ofertas, en orden estable.

4.  Publicaciones y relaciones de preparación.

La preparación comercial utiliza el mismo control de producto que el retiro incompatible.

### **16.4. Calendarios**

Orden:

1.  Calendario.

2.  Propuesta y aplicación.

3.  Versión y publicación.

Los cálculos sobre versiones inmutables no requieren bloqueos de negocio.

### **16.5. Edición y retirada de propuestas**

Una edición o retirada que solo afecta la propuesta bloquea esa propuesta y no adquiere después controles de negocio
dentro de la misma transacción.

Si también necesita modificar objetos aplicados o resolver efectos, sigue el orden de aplicación del servicio.

Así se evita invertir el orden entre propuesta y control del negocio.

## **17. Aplicación, eventos y recuperación**

Una aplicación confirma conjuntamente:

- Definición o efecto local.

- Relaciones y controles.

- Historia.

- Resultado de actuación.

- Estado de aplicación.

- Outbox.

- Trabajos necesarios.

Las comprobaciones remotas se realizan fuera de la transacción.

La fecha programada no permite omitir la revisión de versión, aprobación y condiciones.

Las dependencias pendientes impiden informar aplicación integral.

Los eventos CI-01-020 a CI-01-025 conservan tipos, temas y claves de los contratos técnicos.

La versión de una unidad no se publica como versión global de la institución.

Los consumidores utilizan inbox y no sustituyen versiones posteriores por eventos atrasados.

Una preparación con decisión desconocida conserva seguimiento. No se libera por timeout.

## **18. Trabajos e índices operativos**

Los trabajos utilizan persistent_job del principal.

| **Trabajo**                 | **Identidad de deduplicación**                    |
|-----------------------------|---------------------------------------------------|
| Aplicación programada       | Propuesta, revisión y aplicación.                 |
| Comprobación de dependencia | Aplicación y componente.                          |
| Resolución de preparación   | Preparación y revisión programada de seguimiento. |
| Notificación de conclusión  | Preparación y decisión final.                     |
| Revisión de vigencia        | Objeto, versión y vencimiento.                    |
| Publicación de outbox       | Evento o lote reclamado según el publicador.      |

Las ocurrencias sucesivas de seguimiento no reutilizan la identidad de un trabajo ya completado.

Las tablas versionadas utilizan su PK para consultas por identidad y versión.

Los índices adicionales definidos en cada sección cubren:

- Búsquedas inversas de dependencia.

- Selección temporal.

- Estados pendientes.

- Asignaciones y facultades.

- Publicaciones.

- Recuperación por actuación.

No se crean índices indiscriminados sobre todos los contenidos JSONB.

## **19. Migraciones, conservación y seguridad**

Las migraciones utilizan Flyway por servicio.

Las exclusiones temporales se implementan sobre intervalos de inicio incluido y final excluido.

La incorporación de restricciones verifica previamente los datos existentes.

Las versiones aprobadas, aplicaciones, evidencias y publicaciones históricas permanecen resolubles.

No se elimina una definición utilizada por contratos, cálculos o preparaciones.

Los borradores retirados pueden archivarse conservando identidad y resultado de actuación.

Redis puede acelerar consultas, pero no conserva la única copia de facultades, vigencias, aprobaciones o preparaciones.

Este módulo no administra archivos binarios.

Las consultas históricas e idempotentes mantienen autorización y finalidad.

## **20. Correspondencia con los contratos**

| **Contratos**                 | **Estructuras principales**                                                   |
|-------------------------------|-------------------------------------------------------------------------------|
| CI-01-001 a CI-01-004         | Institución, organización, funciones, adscripciones, facultades y propuestas. |
| CI-01-005 a CI-01-008         | Productos, requisitos, dependencias, compatibilidad y habilitación.           |
| CI-01-009 a CI-01-012         | Condiciones, ofertas, publicaciones y determinaciones.                        |
| CI-01-013 a CI-01-015         | Calendarios, excepciones, convenciones y cálculos.                            |
| CI-01-016 a CI-01-019         | Actuaciones y seguimiento de cada servicio.                                   |
| CI-01-020 a CI-01-025         | Outbox, versiones de agregados e historia.                                    |
| Preparaciones institucionales | Preparación común y controles institucionales versionados.                    |
| Preparaciones de producto     | Preparación común, definición y habilitación.                                 |
| Preparaciones comerciales     | Preparación común, determinación y controles comerciales.                     |

## **21. Escenarios de validación**

| **Escenario**                                    | **Garantía**                                                      |
|--------------------------------------------------|-------------------------------------------------------------------|
| Dos constituciones institucionales               | Una sola institución.                                             |
| Edición material posterior a aprobación          | Otra revisión y nueva comprobación de aprobación.                 |
| Aplicación y retirada concurrentes               | Una decisión coherente sobre la misma propuesta.                  |
| Cambio organizativo que crea un ciclo            | No se aplica.                                                     |
| Sucursal sin domicilio                           | No se confirma la definición.                                     |
| Adscripción incompatible con su función o unidad | No se aplica.                                                     |
| Delegación que amplía el fundamento              | Se rechaza.                                                       |
| Revocación y preparación simultáneas             | Comparten control institucional.                                  |
| Cambio afecta facultades derivadas               | Identifica sus dependencias y preparaciones.                      |
| Producto sin dependencias operativas confirmadas | No informa habilitación completa.                                 |
| Alcance general y lista específica               | Comprueba intersección efectiva.                                  |
| Ofertas incompatibles con igual prioridad        | No aplica una publicación ambigua.                                |
| Determinación repetida en otro momento           | Conserva su propia evidencia, sin unicidad incorrecta por huella. |
| Retiro con conservación autorizada               | No extiende el permiso más allá de su límite.                     |
| Nueva versión de calendario                      | No modifica cálculos históricos.                                  |
| Dos excepciones sobre una fecha                  | La PK impide duplicación.                                         |
| Cobertura insuficiente                           | Devuelve resultado incompleto sin inventar fechas.                |
| Respuesta de aplicación perdida                  | Recupera aplicación y versiones originales.                       |
| Coordinador indisponible                         | Mantiene preparación y seguimiento.                               |
| Evento duplicado o restauración                  | Conserva identidades y no repite aplicaciones.                    |

La revisión documental comprueba las estructuras y su correspondencia con los contratos. Las restricciones, exclusiones,
bloqueos y atomicidad se verifican con PostgreSQL real aislado durante la implementación.
