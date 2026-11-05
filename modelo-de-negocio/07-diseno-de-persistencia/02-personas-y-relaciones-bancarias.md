# **Diseño de persistencia — Módulo 2. Personas y relaciones bancarias**

## **1. Propósito y alcance**

Este documento define la persistencia de los siguientes servicios:

| **Servicio**   | **Datos propios**                                                                                  |
|----------------|----------------------------------------------------------------------------------------------------|
| Personas       | Datos personales, identificadores, contactos, domicilios, relaciones, duplicados y circunstancias. |
| Vinculación    | Solicitudes, requisitos, antecedentes, decisiones de admisión y usos confirmados.                  |
| Clientes       | Identidad comercial, constituciones, períodos, revisiones y desvinculación.                        |
| Representación | Solicitudes, fundamentos, facultades, continuidad, modificaciones y preparaciones.                 |

Comprende los contratos **CI-02-001 a CI-02-033** y las preparaciones de representación.

Cada servicio mantiene su base PostgreSQL, usuario, migraciones y transacciones.

Registrar una persona, verificar identidad, admitir, constituir una relación comercial y reconocer representación son
hechos diferentes.

Ninguna tabla de este módulo constituye una cuenta bancaria, concede financiación o modifica dinero.

No existen claves foráneas ni consultas SQL entre bases de servicios distintos.

## **2. Convenciones físicas**

### **2.1. Tipos**

| **Dato**               | **PostgreSQL** | **Restricción**                    |
|------------------------|----------------|------------------------------------|
| Identificador          | uuid           | Estable y no reutilizable.         |
| Versión de negocio     | bigint         | Positiva.                          |
| Versión de control     | bigint         | No negativa.                       |
| Código                 | varchar(80)    | Catálogo correspondiente.          |
| Nombres o apellidos    | varchar(150)   | No vacíos cuando están informados. |
| Denominación jurídica  | varchar(250)   | No vacía.                          |
| Nombre comercial       | varchar(250)   | No vacío cuando existe.            |
| Identificador oficial  | varchar(120)   | Texto; conserva ceros iniciales.   |
| Emisor documental      | varchar(120)   | Según tipo de identificación.      |
| Correo                 | varchar(254)   | Formato validado.                  |
| Teléfono normalizado   | varchar(20)    | Formato internacional.             |
| Línea de domicilio     | varchar(250)   | No vacía.                          |
| País                   | varchar(2)     | Dos letras mayúsculas.             |
| Región o localidad     | varchar(120)   | Según jurisdicción.                |
| Código postal          | varchar(20)    | Según jurisdicción.                |
| Referencia externa     | varchar(120)   | No vacía.                          |
| Fundamento             | varchar(4000)  | No vacío cuando es obligatorio.    |
| Fecha                  | date           | Sin conversión de zona.            |
| Instante               | timestamptz    | Intercambio en UTC.                |
| Importe de límite      | numeric(24,8)  | No negativo.                       |
| Participación          | numeric(15,12) | Entre cero y uno.                  |
| Cantidad               | numeric(30,12) | No negativa.                       |
| Hash                   | char(64)       | SHA-256 hexadecimal en minúsculas. |
| Contenido estructurado | jsonb          | Esquema identificado y validado.   |

Los límites representativos no autorizan una escala incompatible con el efecto monetario posterior.

### **2.2. Nulabilidad y relaciones**

En las tablas:

- **N** significa columna obligatoria.

- **S** significa que admite NULL con el significado indicado.

- Las columnas agrupadas en una fila tienen el mismo tipo y nulabilidad.

Las FK son locales y utilizan eliminación restringida.

Las referencias remotas conservan propietario, tipo, identidad y versión cuando corresponde.

Las parejas de identificador y versión aparecen conjuntamente.

Las PK y restricciones únicas generan sus índices. No se duplican.

Los índices adicionales son ascendentes salvo indicación expresa.

## **3. Columnas y estructuras comunes**

### **3.1. Bloque T: origen de registro**

Las tablas que incorporan T incluyen:

| **Columna**       | **Tipo**    | **Nulo** | **Regla**                      |
|-------------------|-------------|----------|--------------------------------|
| created_action_id | uuid        | N        | FK a service_action.action_id. |
| created_at        | timestamptz | N        | Registro local.                |

### **3.2. Bloque C: control de agregado**

| **Columna**  | **Tipo**    | **Nulo** | **Regla**                   |
|--------------|-------------|----------|-----------------------------|
| version      | bigint      | N        | Positiva; comienza en 1.    |
| lock_version | bigint      | N        | No negativa; comienza en 0. |
| updated_at   | timestamptz | N        | Última modificación.        |

### **3.3. Bloque S: instantánea estructurada**

| **Columna**    | **Tipo** | **Nulo** | **Regla**             |
|----------------|----------|----------|-----------------------|
| schema_version | bigint   | N        | Positiva.             |
| content_hash   | char(64) | N        | Huella del contenido. |

El esquema corresponde al concepto de la tabla y a su contrato.

### **3.4. Estructuras del principal**

Los cuatro servicios incorporan:

- service_action.

- idempotency_record.

- business_change_history.

- outbox_event.

- inbox_message, cuando consumen eventos.

- persistent_job.

Representación incorpora authority_preparation.

Los bloques de columnas no son tablas ni herencia de entidades.

## **4. Servicio Personas: identidad e historia**

### **4.1. party**

| **Columna**                  | **Tipo**      | **Nulo**      |
|------------------------------|---------------|---------------|
| party_id                     | uuid          | N             |
| party_type                   | varchar(20)   | N             |
| record_status                | varchar(30)   | N             |
| principal_party_id           | uuid          | S             |
| search_name                  | text          | N             |
| search_normalization_version | bigint        | N             |
| Columnas T y C               | Según bloques | Según bloques |

PK: party_id.

FK: principal_party_id → party.party_id.

Tipos: NATURAL_PERSON y LEGAL_ENTITY.

Estados: INCOMPLETE, REGISTERED, DUPLICATE_REVIEW y RESOLVED_ALIAS.

Restricciones:

- La referencia principal no apunta a la misma persona.

- RESOLVED_ALIAS exige referencia principal.

- Una persona no asociada conserva referencia principal ausente.

- La referencia principal no forma ciclos.

- El tipo de persona y sus datos específicos son coherentes.

search_name es un valor derivado para búsqueda; no sustituye los nombres originales.

Índices:

- ix_party_principal: principal_party_id, party_id, para referencia presente.

- ix_party_search_name: search_name, party_id, con soporte de búsqueda por prefijo.

- ix_party_status: record_status, party_id.

Las búsquedas por atributos complementarios se validan contra los datos tipados de la persona.

### **4.2. party_version**

| **Columna**        | **Tipo**      | **Nulo**      |
|--------------------|---------------|---------------|
| party_id           | uuid          | N             |
| party_version      | bigint        | N             |
| party_type         | varchar(20)   | N             |
| record_status      | varchar(30)   | N             |
| principal_party_id | uuid          | S             |
| snapshot_payload   | jsonb         | N             |
| Columnas T y S     | Según bloques | Según bloques |

PK: (party_id, party_version).

FK:

- party_id → party.

- principal_party_id → party, cuando existe.

La versión conserva la instantánea completa de Party: datos específicos, identificadores, contactos, domicilios y
procedencias.

Es inmutable.

La versión de la raíz y su instantánea se actualizan conjuntamente.

La consulta histórica no reconstruye valores anteriores utilizando filas actuales.

### **4.3. party_identity_graph_control**

| **Columna**   | **Tipo** | **Nulo** |
|---------------|----------|----------|
| singleton_key | smallint | N        |
| lock_version  | bigint   | N        |

PK: singleton_key.

Restricciones: clave igual a 1 y control no negativo.

La fila se crea mediante migración.

Ordena las resoluciones que modifican referencias principales.

## **5. Datos humanos, jurídicos y procedencia**

### **5.1. natural_person_data**

| **Columna**        | **Tipo**     | **Nulo** |
|--------------------|--------------|----------|
| party_id           | uuid         | N        |
| given_names_state  | varchar(20)  | N        |
| given_names        | varchar(150) | S        |
| family_names_state | varchar(20)  | N        |
| family_names       | varchar(150) | S        |
| birth_date_state   | varchar(20)  | N        |
| birth_date         | date         | S        |

PK y FK: party_id → party.

Estados de atributos:

PROVIDED, UNKNOWN, NOT_PROVIDED y NOT_APPLICABLE.

Cada estado PROVIDED exige valor. Los demás exigen valor ausente.

Al menos uno de los atributos de nombre está proporcionado para registrar la persona.

La fecha de nacimiento informada no es futura respecto de la fecha de validación del servicio.

No se utilizan comprobaciones dependientes del reloj dentro de un CHECK.

### **5.2. legal_entity_data**

| **Columna**              | **Tipo**     | **Nulo** |
|--------------------------|--------------|----------|
| party_id                 | uuid         | N        |
| legal_name               | varchar(250) | N        |
| trade_name               | varchar(250) | S        |
| legal_form_state         | varchar(20)  | N        |
| legal_form_code          | varchar(80)  | S        |
| jurisdiction_code        | varchar(80)  | N        |
| incorporation_date_state | varchar(20)  | N        |
| incorporation_date       | date         | S        |
| constitutional_documents | jsonb        | N        |

PK y FK: party_id → party.

Los estados de ausencia utilizan las mismas restricciones de coherencia.

La fecha de constitución informada no es futura.

Los documentos son referencias remotas versionadas.

La transacción de persona impide mantener simultáneamente datos humanos y jurídicos para la misma identidad.

### **5.3. party_data_source**

| **Columna**      | **Tipo**     | **Nulo**     |
|------------------|--------------|--------------|
| source_id        | uuid         | N            |
| party_id         | uuid         | N            |
| source_type      | varchar(30)  | N            |
| source_reference | varchar(120) | N            |
| source_service   | varchar(80)  | S            |
| observed_at      | timestamptz  | N            |
| document_id      | uuid         | S            |
| document_version | bigint       | S            |
| Columnas T       | Según bloque | Según bloque |

PK: source_id.

Única: (party_id, source_id).

FK: party_id → party.

Tipos:

DECLARATION, DOCUMENT, SERVICE y ADMINISTRATIVE_RESOLUTION.

SERVICE exige servicio de origen.

Documento y versión aparecen conjuntamente.

Índices:

- source_type, source_reference, party_id.

- source_service, source_reference, party_id, para servicio presente.

Una misma fuente puede aportar información sobre varias personas. No se impone unicidad global de la referencia.

## **6. Identificadores**

### **6.1. party_identifier**

| **Columna**            | **Tipo**      | **Nulo**      |
|------------------------|---------------|---------------|
| identifier_id          | uuid          | N             |
| party_id               | uuid          | N             |
| identifier_type        | varchar(80)   | N             |
| original_value         | varchar(120)  | N             |
| normalized_value       | varchar(120)  | N             |
| normalization_version  | bigint        | N             |
| jurisdiction_code      | varchar(80)   | N             |
| issuer                 | varchar(120)  | S             |
| valid_from, valid_to   | date          | S             |
| source_id              | uuid          | N             |
| verification_reference | jsonb         | S             |
| observation_status     | varchar(20)   | N             |
| review_reference       | jsonb         | S             |
| retired_at             | timestamptz   | S             |
| Columnas T y C         | Según bloques | Según bloques |

PK: identifier_id.

FK:

- party_id → party.

- (party_id, source_id) → party_data_source.

Estados: NONE, PENDING, CONTRADICTORY y EXPIRED.

Si existen ambas fechas, el final es posterior al inicio.

El emisor es obligatorio cuando lo exige el tipo.

Índices:

- identifier_type, jurisdiction_code, normalized_value, party_id.

- party_id, identifier_type, identifier_id.

- party_id, source_id.

No existe unicidad global del identificador declarado.

Bajo bloqueo de la persona se comprueba que un identificador equivalente no se duplique con vigencia superpuesta sin una
actuación de revisión que lo justifique.

La revisión y contradicción quedan conservadas; no provocan fusión automática.

Los valores anteriores permanecen en las instantáneas de persona.

## **7. Contactos y versiones**

### **7.1. party_contact**

| **Columna**          | **Tipo**     | **Nulo**     |
|----------------------|--------------|--------------|
| contact_id           | uuid         | N            |
| party_id             | uuid         | N            |
| contact_type         | varchar(10)  | N            |
| current_version      | bigint       | N            |
| status               | varchar(30)  | N            |
| valid_from, valid_to | timestamptz  | S            |
| Columnas T           | Según bloque | Según bloque |
| lock_version         | bigint       | N            |
| updated_at           | timestamptz  | N            |

PK: contact_id.

Única: (party_id, contact_id).

FK: party_id → party.

Tipos: EMAIL y PHONE.

Estados:

DECLARED, PENDING_CONFIRMATION, CONFIRMED, REJECTED y RETIRED.

Final posterior al inicio cuando ambos existen.

Índice: party_id, status, contact_id.

### **7.2. party_contact_version**

| **Columna**      | **Tipo**     | **Nulo**     |
|------------------|--------------|--------------|
| contact_id       | uuid         | N            |
| contact_version  | bigint       | N            |
| party_id         | uuid         | N            |
| original_value   | varchar(254) | N            |
| normalized_value | varchar(254) | N            |
| source_id        | uuid         | N            |
| Columnas T       | Según bloque | Según bloque |

PK: (contact_id, contact_version).

Única: (party_id, contact_id, contact_version).

FK:

- (party_id, contact_id) → party_contact.

- (party_id, source_id) → party_data_source.

Una FK diferida desde contacto y versión vigente permite insertar raíz y primera versión en la misma transacción y exige
que la versión exista al confirmar.

Para teléfono, el valor normalizado no supera veinte caracteres y utiliza formato internacional.

Para correo se conserva el original sin modificar arbitrariamente su parte local.

Índice: normalized_value, contact_id, contact_version.

No existe unicidad global del contacto.

Cambiar valor genera versión nueva y deja sin aplicación los desafíos de la anterior.

## **8. Domicilios**

### **8.1. party_address**

| **Columna**     | **Tipo**     | **Nulo**     |
|-----------------|--------------|--------------|
| address_id      | uuid         | N            |
| party_id        | uuid         | N            |
| address_type    | varchar(80)  | N            |
| current_version | bigint       | N            |
| retired_at      | timestamptz  | S            |
| Columnas T      | Según bloque | Según bloque |
| lock_version    | bigint       | N            |

PK: address_id.

Única: (party_id, address_id).

FK a persona.

Índice: party_id, address_type, address_id.

### **8.2. party_address_version**

| **Columna**      | **Tipo**     | **Nulo**     |
|------------------|--------------|--------------|
| address_id       | uuid         | N            |
| address_version  | bigint       | N            |
| party_id         | uuid         | N            |
| country_code     | varchar(2)   | N            |
| region, locality | varchar(120) | S            |
| postal_code      | varchar(20)  | S            |
| valid_from       | date         | N            |
| valid_to         | date         | S            |
| source_id        | uuid         | N            |
| Columnas T       | Según bloque | Según bloque |

PK: dirección y versión.

FK:

- Persona y domicilio a su raíz.

- Persona y fuente a party_data_source.

La versión vigente utiliza FK diferida desde la raíz.

La presencia de región, localidad y código postal depende de la jurisdicción.

### **8.3. party_address_line**

| **Columna**     | **Tipo**     | **Nulo** |
|-----------------|--------------|----------|
| address_id      | uuid         | N        |
| address_version | bigint       | N        |
| position        | smallint     | N        |
| line_value      | varchar(250) | N        |

PK: domicilio, versión y posición.

FK a versión de domicilio.

La posición está entre 1 y 3.

La transacción exige entre una y tres líneas consecutivas y no vacías.

## **9. Confirmación de contactos**

### **9.1. contact_confirmation**

| **Columna**             | **Tipo**     | **Nulo**     |
|-------------------------|--------------|--------------|
| confirmation_id         | uuid         | N            |
| party_id                | uuid         | N            |
| contact_id              | uuid         | N            |
| contact_version         | bigint       | N            |
| purpose_code            | varchar(80)  | N            |
| proof_verifier          | bytea        | N            |
| verifier_key_id         | varchar(120) | N            |
| status                  | varchar(20)  | N            |
| failed_attempts         | smallint     | N            |
| expires_at              | timestamptz  | N            |
| confirmed_at            | timestamptz  | S            |
| communication_reference | jsonb        | S            |
| Columnas T              | Según bloque | Según bloque |
| lock_version            | bigint       | N            |

PK: confirmation_id.

FK compuesta de persona, contacto y versión a party_contact_version.

Estados:

PENDING, CONFIRMED, FAILED, EXPIRED y SUPERSEDED.

Restricciones:

- Fallos entre cero y cinco.

- Vencimiento a diez minutos de creación.

- CONFIRMED exige fecha de confirmación.

- Los demás estados no conservan confirmación.

- El código recibido tiene seis dígitos.

- No se admite una confirmación posterior al vencimiento.

Existe índice único parcial:

**contact_id, purpose_code para estado PENDING.**

Índices adicionales:

- expires_at, confirmation_id, para estado PENDING.

- party_id, contact_id, contact_version.

El verificador utiliza una función criptográfica autenticada con clave externa. No se conserva un hash simple del código
como única protección.

### **9.2. contact_confirmation_attempt**

| **Columna**               | **Tipo**    | **Nulo** |
|---------------------------|-------------|----------|
| attempt_id                | uuid        | N        |
| confirmation_id           | uuid        | N        |
| action_id                 | uuid        | N        |
| attempted_at              | timestamptz | N        |
| result_code               | varchar(80) | N        |
| resulting_failed_attempts | smallint    | N        |

PK: attempt_id.

Única: action_id.

FK a confirmación y actuación.

Fallos resultantes entre cero y cinco.

Índice: confirmation_id, attempted_at, attempt_id.

La prueba no se conserva en claro.

### **9.3. contact_confirmation_dispatch**

| **Columna**             | **Tipo**     | **Nulo** |
|-------------------------|--------------|----------|
| confirmation_id         | uuid         | N        |
| communication_action_id | uuid         | N        |
| encrypted_secret        | bytea        | S        |
| encryption_key_id       | varchar(120) | S        |
| encryption_metadata     | jsonb        | S        |
| dispatch_status         | varchar(30)  | N        |
| secret_erased_at        | timestamptz  | S        |
| updated_at              | timestamptz  | N        |
| lock_version            | bigint       | N        |

PK y FK a confirmación.

La actuación de comunicación es remota y estable.

Estados: PENDING, ACCEPTED, UNCERTAIN, FAILED y STOPPED.

El material cifrado y sus metadatos aparecen conjuntamente. Después de eliminarlo, quedan ausentes y se registra
secret_erased_at.

Los trabajos contienen identidad de confirmación, no el código.

La creación, sustitución y respuesta bloquean persona, contacto y desafío en ese orden.

Una emisión nueva sustituye el desafío pendiente anterior en la misma transacción.

## **10. Relaciones entre personas**

### **10.1. party_relationship**

| **Columna**                      | **Tipo**     | **Nulo**     |
|----------------------------------|--------------|--------------|
| relationship_id                  | uuid         | N            |
| source_party_id, target_party_id | uuid         | N            |
| relationship_type                | varchar(80)  | N            |
| current_version                  | bigint       | N            |
| Columnas T                       | Según bloque | Según bloque |
| lock_version                     | bigint       | N            |

PK: relationship_id.

FK a ambas personas.

Las autorrelaciones se admiten únicamente si el tipo registrado las contempla; no se aceptan por omisión de validación.

Índices:

- source_party_id, relationship_type, relationship_id.

- target_party_id, relationship_type, relationship_id.

### **10.2. party_relationship_version**

| **Columna**                  | **Tipo**       | **Nulo**      |
|------------------------------|----------------|---------------|
| relationship_id              | uuid           | N             |
| relationship_version         | bigint         | N             |
| valid_from                   | date           | N             |
| valid_to                     | date           | S             |
| measurement_date             | date           | S             |
| economic_share, voting_share | numeric(15,12) | S             |
| quantity                     | numeric(30,12) | S             |
| unit_code                    | varchar(80)    | S             |
| measurement_basis            | jsonb          | S             |
| evidence_references          | jsonb          | N             |
| completeness                 | varchar(20)    | N             |
| Columnas T y S               | Según bloques  | Según bloques |

PK: relación y versión.

FK a raíz.

Restricciones:

- Final posterior al inicio.

- Participaciones entre cero y uno.

- Cantidad no negativa y unidad conjunta.

- Composición cuantificada exige fecha y fundamento.

- Completitud COMPLETE, INCOMPLETE u OBSERVED.

La versión vigente utiliza FK diferida desde la raíz.

Una relación societaria no concede representación.

## **11. Duplicados y circunstancias**

### **11.1. party_duplicate_case**

| **Columna**                 | **Tipo**      | **Nulo**      |
|-----------------------------|---------------|---------------|
| case_id                     | uuid          | N             |
| proposed_principal_party_id | uuid          | S             |
| status                      | varchar(30)   | N             |
| Columnas T y C              | Según bloques | Según bloques |

PK: case_id.

FK de principal propuesta a persona.

Estados internos: OPEN, UNDER_REVIEW, RESOLVED y REQUIRES_REVIEW.

Índice: status, created_at, case_id.

### **11.2. party_duplicate_candidate**

| **Columna**             | **Tipo** | **Nulo** |
|-------------------------|----------|----------|
| case_id                 | uuid     | N        |
| party_id                | uuid     | N        |
| evaluated_party_version | bigint   | N        |
| evidence_references     | jsonb    | N        |

PK: caso y persona.

FK a caso y versión de persona.

Índice inverso: party_id, case_id.

### **11.3. party_duplicate_resolution**

| **Columna**             | **Tipo**      | **Nulo**      |
|-------------------------|---------------|---------------|
| resolution_id           | uuid          | N             |
| case_id                 | uuid          | N             |
| conclusion              | varchar(30)   | N             |
| principal_party_id      | uuid          | S             |
| associated_party_ids    | jsonb         | N             |
| approval_reference      | jsonb         | N             |
| evidence_references     | jsonb         | N             |
| pending_regularizations | jsonb         | N             |
| Columnas T y S          | Según bloques | Según bloques |

PK: resolution_id.

FK a caso y persona principal presente.

Conclusiones:

SAME_PERSON, DIFFERENT_PERSONS e INCONCLUSIVE.

SAME_PERSON exige principal perteneciente a los candidatos evaluados.

DIFFERENT_PERSONS excluye principal común.

La resolución adquiere primero el control del grafo, después el caso y las personas en orden estable.

Comprueba versiones y ausencia de ciclos.

No fusiona contratos, cuentas, deuda ni patrimonio.

### **11.4. Circunstancias**

party_circumstance contiene:

| **Columna**            | **Tipo**      | **Nulo**      |
|------------------------|---------------|---------------|
| circumstance_id        | uuid          | N             |
| party_id               | uuid          | N             |
| circumstance_type      | varchar(40)   | N             |
| fact_date              | date          | N             |
| known_at               | timestamptz   | N             |
| source_id              | uuid          | N             |
| verification_status    | varchar(20)   | N             |
| verification_reference | jsonb         | S             |
| evidence_references    | jsonb         | N             |
| Columnas T y C         | Según bloques | Según bloques |

PK: circunstancia.

FK a persona y a fuente de esa persona.

Tipos:

CAPACITY_CHANGE, DEATH_REPORTED, SUCCESSION, DISSOLUTION, LIQUIDATION y OTHER_DOCUMENTED_CIRCUMSTANCE.

Verificación: DECLARED, PENDING, VERIFIED u OBSERVED.

Índice: party_id, fact_date, circumstance_id.

party_circumstance_application conserva application_id uuid N como PK, circumstance_id uuid N como FK, component_code
varchar(80) N, responsible_service varchar(80) N, remote_action_id uuid N, result_reference jsonb S, status varchar(30)
N y updated_at timestamptz N.

Única: circunstancia, componente y responsable.

Estados: PENDING, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

No se informa aplicación integral por la mera recepción de la circunstancia.

## **12. Servicio Vinculación: solicitudes**

### **12.1. onboarding_application**

| **Columna**               | **Tipo**      | **Nulo**      |
|---------------------------|---------------|---------------|
| application_id            | uuid          | N             |
| party_id                  | uuid          | N             |
| application_type          | varchar(20)   | N             |
| purpose_code              | varchar(80)   | N             |
| declared_activity         | jsonb         | N             |
| contact_references        | jsonb         | N             |
| representation_references | jsonb         | N             |
| previous_application_id   | uuid          | S             |
| previous_customer_id      | uuid          | S             |
| status                    | varchar(30)   | N             |
| received_at               | timestamptz   | N             |
| Columnas T, C y S         | Según bloques | Según bloques |

PK: solicitud.

FK local a solicitud anterior.

Tipos: INITIAL y READMISSION.

La readmisión exige cliente anterior.

La solicitud anterior no puede ser la misma.

Estados:

RECEIVED, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_DECISION, ACCEPTED, REJECTED, WITHDRAWN, EXPIRED y
REQUIRES_REVIEW.

La actividad conserva código, procedencia de fondos, fuente y descripción opcional.

Índices:

- party_id, status, received_at, application_id.

- previous_application_id, para antecedente presente.

- status, updated_at, application_id.

## **13. Requerimientos, presentaciones y plazos**

### **13.1. onboarding_requirement**

| **Columna**                                    | **Tipo**     | **Nulo**     |
|------------------------------------------------|--------------|--------------|
| requirement_id                                 | uuid         | N            |
| application_id                                 | uuid         | N            |
| requirement_version                            | bigint       | N            |
| cause                                          | varchar(30)  | N            |
| channel_code                                   | varchar(80)  | N            |
| made_available_at                              | timestamptz  | N            |
| availability_evidence                          | jsonb        | N            |
| calendar_reference                             | jsonb        | N            |
| original_due_at, effective_due_at, reminder_at | timestamptz  | N            |
| status                                         | varchar(20)  | N            |
| Columnas T                                     | Según bloque | Según bloque |
| lock_version                                   | bigint       | N            |

PK: requerimiento.

Única: (application_id, requirement_id).

FK a solicitud.

Causas: INITIAL, FINAL_CORRECTION y BANK_OMISSION.

Estados internos: OPEN, UNDER_REVIEW, SATISFIED, EXPIRED y CANCELLED.

Índice: status, effective_due_at, requirement_id.

### **13.2. onboarding_requirement_item**

| **Columna**         | **Tipo**    | **Nulo** |
|---------------------|-------------|----------|
| item_id             | uuid        | N        |
| application_id      | uuid        | N        |
| requirement_id      | uuid        | N        |
| requirement_code    | varchar(80) | N        |
| responsible_service | varchar(80) | N        |
| status              | varchar(20) | N        |
| last_submission_id  | uuid        | S        |
| observations        | jsonb       | N        |

PK: punto.

Única: (application_id, item_id).

FK compuesta a requerimiento de la misma solicitud.

Estados: PENDING, SUBMITTED, UNDER_REVIEW, SATISFIED y OBSERVED.

La presentación referenciada debe pertenecer a la misma solicitud.

Índice: requirement_id, status, item_id.

### **13.3. Presentaciones**

onboarding_submission:

| **Columna**                          | **Tipo**      | **Nulo**      |
|--------------------------------------|---------------|---------------|
| submission_id                        | uuid          | N             |
| application_id                       | uuid          | N             |
| received_at                          | timestamptz   | N             |
| original_received_at                 | timestamptz   | S             |
| data_references, document_references | jsonb         | N             |
| review_status                        | varchar(20)   | N             |
| Columnas T y S                       | Según bloques | Según bloques |

PK: presentación.

Únicas:

- Actuación de origen.

- (application_id, submission_id).

FK a solicitud.

Estados de revisión: RECEIVED, UNDER_REVIEW, REVIEWED y OBSERVED.

Índice: application_id, received_at, submission_id.

onboarding_submission_item contiene:

- application_id uuid N.

- submission_id uuid N.

- item_id uuid N.

- sufficiency_status varchar(20) N.

- evidence_references jsonb N.

- observations jsonb N.

- reviewed_at timestamptz S.

PK: presentación y punto.

FK compuestas a presentación y punto de la misma solicitud.

Suficiencia: PENDING, SUFFICIENT e INSUFFICIENT.

Índice inverso: item_id, submission_id.

### **13.4. Incidencias e historia de plazos**

onboarding_channel_incident contiene:

- incident_id uuid N, PK.

- application_id uuid N, FK.

- channel_code varchar(80) N.

- started_at timestamptz N.

- recovered_at timestamptz S.

- evidence_references jsonb N.

- Bloque T.

Recuperación posterior al inicio.

Índice: application_id, channel_code, started_at.

onboarding_deadline_history contiene:

- deadline_change_id uuid N, PK.

- requirement_id uuid N, FK.

- previous_due_at timestamptz N.

- resulting_due_at timestamptz N.

- cause_code varchar(80) N.

- calendar_reference jsonb N.

- calculation_reference jsonb N.

- evidence_references jsonb N.

- Bloque T.

Índice: requirement_id, created_at, deadline_change_id.

Las presentaciones oportunas y la declaración de vencimiento se ordenan bajo la solicitud.

Los plazos conservan treinta días iniciales, quince de subsanación y treinta para puntos nuevos por omisión interna.

Se preservan inicio al día siguiente, ajuste final al hábil siguiente, recordatorio y extensión por indisponibilidad
hasta el segundo hábil posterior a recuperación.

## **14. Decisiones de admisión**

### **14.1. admission_decision**

| **Columna**         | **Tipo**     | **Nulo**     |
|---------------------|--------------|--------------|
| decision_id         | uuid         | N            |
| application_id      | uuid         | N            |
| current_version     | bigint       | N            |
| availability_status | varchar(30)  | N            |
| lock_version        | bigint       | N            |
| updated_at          | timestamptz  | N            |
| Columnas T          | Según bloque | Según bloque |

PK: decisión.

FK a solicitud.

Disponibilidad interna:

NOT_USABLE, AVAILABLE, USED, WITHDRAWN, EXPIRED y REQUIRES_REVIEW.

Índice: application_id, decision_id.

### **14.2. admission_decision_version**

| **Columna**                                      | **Tipo**      | **Nulo**      |
|--------------------------------------------------|---------------|---------------|
| decision_id                                      | uuid          | N             |
| decision_version                                 | bigint        | N             |
| evaluated_application_version                    | bigint        | N             |
| party_id                                         | uuid          | N             |
| party_version                                    | bigint        | N             |
| result                                           | varchar(30)   | N             |
| scope_payload                                    | jsonb         | N             |
| verification_references                          | jsonb         | N             |
| approval_reference                               | jsonb         | S             |
| internal_reason_codes, communicable_reason_codes | jsonb         | N             |
| decided_at                                       | timestamptz   | N             |
| approved_at, usable_until                        | timestamptz   | S             |
| Columnas T y S                                   | Según bloques | Según bloques |

PK: decisión y versión.

FK a raíz.

La versión vigente utiliza FK diferida desde la raíz.

Resultados: ACCEPTED, REJECTED y REQUIRES_REVIEW.

La decisión definitiva exige aprobación separada.

La aceptación exige aprobación, fecha de aprobación y límite de utilización.

El límite no supera treinta días corridos desde la aprobación a la misma hora local ni los antecedentes indispensables.

Los motivos internos y comunicables permanecen separados.

El alcance utiliza AdmissionScope, sin interpretar ámbitos omitidos como universales.

Índice: usable_until, decision_id, decision_version, para resultado aceptado.

## **15. Uso, retiro y reapertura de admisión**

### **15.1. admission_use**

| **Columna**              | **Tipo**      | **Nulo**      |
|--------------------------|---------------|---------------|
| admission_use_id         | uuid          | N             |
| decision_id              | uuid          | N             |
| decision_version         | bigint        | N             |
| customer_constitution_id | uuid          | N             |
| party_id                 | uuid          | N             |
| requested_scope          | jsonb         | N             |
| result                   | varchar(20)   | N             |
| confirmed_at             | timestamptz   | S             |
| follow_up_references     | jsonb         | N             |
| Columnas T y S           | Según bloques | Según bloques |

PK: uso.

Única: (decision_id, customer_constitution_id).

FK a decisión versionada.

Resultados: CONFIRMED, REJECTED y PENDING.

CONFIRMED exige fecha.

Un índice único parcial sobre decision_id, para resultado CONFIRMED, impide confirmar otra constitución con la misma
decisión.

La constitución repetida recupera el uso original.

Índice: customer_constitution_id, admission_use_id.

Uso, retiro, revisión y vencimiento bloquean la misma decisión.

La hora válida procede del servicio.

El vencimiento posterior no invalida un uso confirmado válidamente.

### **15.2. onboarding_lifecycle_action**

| **Columna**              | **Tipo**      | **Nulo**     |
|--------------------------|---------------|--------------|
| lifecycle_action_id      | uuid          | N            |
| application_id           | uuid          | N            |
| action_type              | varchar(20)   | N            |
| reason                   | varchar(4000) | N            |
| decision_id              | uuid          | S            |
| successor_application_id | uuid          | S            |
| known_use_references     | jsonb         | N            |
| pending_components       | jsonb         | N            |
| result_code              | varchar(80)   | N            |
| Columnas T               | Según bloque  | Según bloque |

PK: actuación de ciclo.

FK a solicitud, decisión y solicitud sucesora presentes.

Tipos: WITHDRAW y REOPEN.

La reapertura crea otro trámite y conserva el anterior.

El retiro con uso confirmado o incierto mantiene coordinación con Clientes.

## **16. Servicio Clientes: identidad y constitución**

### **16.1. customer_party_control**

| **Columna**  | **Tipo**    | **Nulo** |
|--------------|-------------|----------|
| party_id     | uuid        | N        |
| lock_version | bigint      | N        |
| created_at   | timestamptz | N        |

PK: persona principal remota.

Se crea bajo unicidad antes de constituir o readmitir.

### **16.2. customer**

| **Columna**             | **Tipo**      | **Nulo**      |
|-------------------------|---------------|---------------|
| customer_id             | uuid          | N             |
| party_id                | uuid          | N             |
| commercial_segment_code | varchar(80)   | N             |
| new_contracting_allowed | boolean       | N             |
| continuity_status       | varchar(20)   | N             |
| next_review_date        | date          | S             |
| Columnas T y C          | Según bloques | Según bloques |

PK: cliente.

Única: party_id.

FK local a customer_party_control.

Estados: ACTIVE, OFFBOARDING, RESIDUAL y ENDED.

Índices:

- next_review_date, customer_id, para revisión presente.

- continuity_status, customer_id.

La regularización de referencias principales no fusiona clientes automáticamente.

### **16.3. customer_constitution**

| **Columna**               | **Tipo**      | **Nulo**      |
|---------------------------|---------------|---------------|
| constitution_id           | uuid          | N             |
| party_id                  | uuid          | N             |
| party_resolution_evidence | jsonb         | N             |
| admission_reference       | jsonb         | N             |
| requested_scope           | jsonb         | N             |
| initial_segment_code      | varchar(80)   | N             |
| status                    | varchar(40)   | N             |
| admission_use_id          | uuid          | S             |
| admission_use_action_id   | uuid          | N             |
| customer_id, period_id    | uuid          | S             |
| Columnas T, C y S         | Según bloques | Según bloques |

PK: constitución estable.

FK a control de persona, cliente y período presentes.

La relación con período se comprueba al completar la constitución.

Estados:

INTENT_RECORDED, ADMISSION_USE_PENDING, ADMISSION_USE_CONFIRMED, CONSTITUTED y FAILED_REQUIRING_RESOLUTION.

La actuación de uso es remota y estable.

Índice: party_id, status, constitution_id.

La intención se confirma antes de solicitar uso.

Si ya existe relación activa compatible, se recupera sin consumir otra admisión innecesariamente.

## **17. Períodos comerciales e historia**

### **17.1. customer_period**

| **Columna**                         | **Tipo**      | **Nulo**      |
|-------------------------------------|---------------|---------------|
| period_id                           | uuid          | N             |
| customer_id                         | uuid          | N             |
| constitution_id                     | uuid          | N             |
| admission_reference                 | jsonb         | N             |
| admission_use_id                    | uuid          | N             |
| started_at                          | timestamptz   | N             |
| commercial_ended_at, fully_ended_at | timestamptz   | S             |
| reason_code                         | varchar(80)   | N             |
| Columnas T y C                      | Según bloques | Según bloques |

PK: período.

Únicas: constitución y uso de admisión.

FK a cliente y constitución.

La referencia recíproca desde constitución puede diferirse hasta confirmar la transacción.

Un índice único parcial sobre customer_id, para commercial_ended_at ausente, impide dos períodos comerciales activos.

Las fechas de finalización no preceden al inicio.

La finalización integral no precede al cese comercial.

Índice histórico: customer_id, started_at, period_id.

### **17.2. customer_version**

Contiene:

- customer_id uuid N.

- customer_version bigint N.

- snapshot_payload jsonb N.

- Bloques T y S.

PK: cliente y versión.

FK a cliente.

Conserva segmento, continuidad, permiso de contratación, revisión y períodos de esa versión.

Es inmutable.

La constitución local confirma cliente, período, resultado y outbox conjuntamente.

## **18. Revisiones comerciales**

### **18.1. customer_review**

| **Columna**              | **Tipo**      | **Nulo**      |
|--------------------------|---------------|---------------|
| review_id                | uuid          | N             |
| customer_id              | uuid          | N             |
| reason_code              | varchar(80)   | N             |
| scope_payload            | jsonb         | N             |
| planned_date             | date          | N             |
| started_at, completed_at | timestamptz   | S             |
| status                   | varchar(30)   | N             |
| conclusion               | varchar(40)   | S             |
| next_review_date         | date          | S             |
| approval_reference       | jsonb         | S             |
| Columnas T y C           | Según bloques | Según bloques |

PK: revisión.

FK a cliente.

Estados:

OPEN, AWAITING_APPLICANT, UNDER_VERIFICATION, READY_FOR_CONCLUSION, COMPLETED, OVERDUE y ESCALATED.

Conclusiones:

CONTINUE, CONTINUE_WITH_FOLLOW_UP e INITIATE_OFFBOARDING.

COMPLETED exige conclusión y fecha real.

Índices:

- customer_id, planned_date, review_id.

- status, planned_date, review_id.

La próxima revisión utiliza la fecha más próxima entre doce meses desde la conclusión real y exigencias anteriores de
controles.

### **18.2. Evidencias y requerimientos**

customer_review_evidence:

- evidence_id uuid N, PK.

- review_id uuid N, FK.

- source_reference jsonb N.

- verification_reference jsonb S.

- document_references jsonb N.

- observed_at timestamptz N.

- Bloques T y S.

Índice: review_id, observed_at, evidence_id.

customer_review_requirement:

- requirement_id uuid N, PK.

- review_id uuid N, FK.

- requirement_code varchar(80) N.

- responsible_service varchar(80) N.

- due_at timestamptz N.

- status varchar(20) N.

- result_references jsonb N.

- Bloque T.

Estados: PENDING, UNDER_REVIEW, SATISFIED y OBSERVED.

Índice: status, due_at, requirement_id.

La revisión pendiente no impone automáticamente una restricción.

Los avisos, escalaciones y tarea supervisora conservan los plazos del contrato.

## **19. Desvinculación**

### **19.1. customer_offboarding**

| **Columna**                         | **Tipo**      | **Nulo**      |
|-------------------------------------|---------------|---------------|
| offboarding_id                      | uuid          | N             |
| customer_id                         | uuid          | N             |
| reason_code                         | varchar(80)   | N             |
| basis_reference                     | jsonb         | N             |
| received_at                         | timestamptz   | N             |
| new_contracting_ceased_at           | timestamptz   | S             |
| commercial_ended_at, fully_ended_at | timestamptz   | S             |
| coordination_status                 | varchar(30)   | N             |
| Columnas T y C                      | Según bloques | Según bloques |

PK: desvinculación.

FK a cliente.

Estados internos:

REQUESTED, INVENTORY_PENDING, PROCESSING, RESIDUAL, COMPLETED y WITHDRAWN.

Un índice único parcial sobre cliente impide dos procesos no concluidos incompatibles.

Condición del índice: estado distinto de COMPLETED y WITHDRAWN.

### **19.2. Inventarios**

customer_offboarding_inventory conserva:

- inventory_id uuid N, PK.

- offboarding_id uuid N, FK.

- owner_service varchar(80) N.

- cut_at timestamptz S.

- source_version bigint S.

- result varchar(30) N.

- related_objects jsonb N.

- pending_components jsonb N.

- evidence_references jsonb N.

- received_at timestamptz N.

- schema_version bigint N.

Resultados:

CONFIRMED_EMPTY, CONFIRMED_WITH_RELATIONS y PENDING.

Los resultados confirmados exigen corte y evidencia.

Índice: offboarding_id, owner_service, received_at, inventory_id.

Las revisiones se conservan, no sobrescriben el inventario anterior.

### **19.3. Componentes**

customer_offboarding_component conserva:

- component_id uuid N, PK.

- offboarding_id uuid N, FK.

- component_code varchar(80) N.

- owner_service varchar(80) N.

- object_reference jsonb N.

- remote_action_id uuid N.

- status varchar(30) N.

- result_reference jsonb S.

- evidence_references jsonb N.

- updated_at timestamptz N.

- lock_version bigint N.

Única: desvinculación y código de componente.

Estados: PENDING, CONFIRMED, UNCERTAIN y REQUIRES_REVIEW.

Índice: owner_service, status, component_id.

La falta de respuesta no produce inventario vacío.

La finalización comprueba cliente, período y componentes bajo el mismo control local.

No extingue obligaciones de productos ajenos.

## **20. Servicio Representación: controles y solicitudes**

### **20.1. Controles**

representation_graph_control:

- singleton_key smallint N, PK, siempre 1.

- lock_version bigint N, no negativo.

La fila se crea mediante migración.

represented_party_control:

- represented_party_id uuid N, PK remota.

- lock_version bigint N.

- updated_at timestamptz N.

La fila se crea bajo unicidad antes de admitir modificaciones o preparaciones.

### **20.2. representation_application**

| **Columna**          | **Tipo**      | **Nulo**      |
|----------------------|---------------|---------------|
| application_id       | uuid          | N             |
| represented_party_id | uuid          | N             |
| proposed_definition  | jsonb         | N             |
| status               | varchar(30)   | N             |
| received_at          | timestamptz   | N             |
| Columnas T, C y S    | Según bloques | Según bloques |

PK: solicitud.

FK local al control de representado.

Estados internos:

SUBMITTED, UNDER_VERIFICATION, PENDING_APPROVAL, RECOGNIZED, REJECTED y REQUIRES_REVIEW.

Índice: represented_party_id, status, received_at, application_id.

### **20.3. Antecedentes y decisión**

representation_application_submission:

- submission_id uuid N, PK.

- application_id uuid N, FK.

- document_references jsonb N.

- verification_references jsonb N.

- received_at timestamptz N.

- Bloques T y S.

Actuación de origen única.

Índice: application_id, received_at, submission_id.

representation_application_decision:

- decision_id uuid N, PK.

- application_id uuid N, FK.

- evaluated_application_version bigint N.

- evaluated_content_hash char(64) N.

- conclusion varchar(20) N.

- approval_reference jsonb S.

- preparer_reference jsonb N.

- approver_reference jsonb S.

- verification_references jsonb N.

- representation_id uuid S, FK local.

- Bloques T y S.

Conclusiones: RECOGNIZE, REJECT y REQUIRE_REVIEW.

Las decisiones definitivas conservan aprobación separada.

Recibir documentos no activa facultades.

## **21. Representación y definición versionada**

### **21.1. representation**

| **Columna**          | **Tipo**      | **Nulo**      |
|----------------------|---------------|---------------|
| representation_id    | uuid          | N             |
| represented_party_id | uuid          | N             |
| current_version      | bigint        | N             |
| status               | varchar(40)   | N             |
| Columnas T y C       | Según bloques | Según bloques |

PK: representación.

FK local al control de representado.

Estados:

SUBMITTED, UNDER_VERIFICATION, PENDING_APPROVAL, ACTIVE, CONTINUITY_REVIEW_REQUIRED, REVOKED, EXPIRED, ENDED y REJECTED.

Índice: represented_party_id, status, representation_id.

### **21.2. representation_version**

| **Columna**                   | **Tipo**      | **Nulo**      |
|-------------------------------|---------------|---------------|
| representation_id             | uuid          | N             |
| representation_version        | bigint        | N             |
| basis_type                    | varchar(80)   | N             |
| basis_documents               | jsonb         | N             |
| parent_representation_id      | uuid          | S             |
| parent_representation_version | bigint        | S             |
| permitted_acts                | jsonb         | N             |
| product_scope, object_scope   | jsonb         | N             |
| acting_mode                   | varchar(20)   | N             |
| joint_rule                    | jsonb         | S             |
| valid_from                    | timestamptz   | N             |
| valid_to                      | timestamptz   | S             |
| approval_reference            | jsonb         | S             |
| source_application_id         | uuid          | S             |
| source_change_id              | uuid          | S             |
| Columnas T y S                | Según bloques | Según bloques |

PK: representación y versión.

FK a raíz, fundamento versionado y origen local presente.

La versión vigente utiliza FK diferida desde la raíz.

Restricciones:

- Fundamento y versión aparecen conjuntamente.

- No se admite fundamento propio.

- Actos no vacíos y sin duplicados.

- Modalidad INDIVIDUAL o JOINT.

- Regla conjunta exigida exclusivamente para modalidad conjunta.

- Final posterior al inicio.

- Origen de solicitud o cambio identificado.

- Antes de reconocer o aplicar se exige aprobación pertinente.

Una versión reconocida es inmutable.

### **21.3. Representantes y límites**

representation_representative:

- representation_id uuid N.

- representation_version bigint N.

- representative_party_id uuid N.

PK: las tres columnas.

FK a versión.

Índice inverso: representative_party_id, representation_id, representation_version.

La versión exige al menos un representante.

representation_money_limit:

- representation_id uuid N.

- representation_version bigint N.

- currency varchar(3) N.

- maximum_amount numeric(24,8) N.

PK: representación, versión y moneda.

FK a versión.

Importe no negativo y moneda ARS o USD.

Los alcances utilizan ALL_WITHIN_BASIS o EXPLICIT_SET.

La modalidad general exige fundamento verificado. La explícita exige referencias no vacías.

## **22. Dependencias, efectividad y continuidad**

### **22.1. representation_dependency**

| **Columna**                   | **Tipo** | **Nulo** |
|-------------------------------|----------|----------|
| representation_id             | uuid     | N        |
| representation_version        | bigint   | N        |
| parent_representation_id      | uuid     | N        |
| parent_representation_version | bigint   | N        |

PK: las cuatro columnas.

Única: representación, versión y fundamento de representación.

FK a ambas versiones locales.

Índice inverso: parent_representation_id, parent_representation_version, representation_id.

El grafo no admite ciclos.

Una revocación identifica dependencias derivadas y preparaciones afectadas.

### **22.2. representation_effectivity**

| **Columna**            | **Tipo**    | **Nulo** |
|------------------------|-------------|----------|
| effectivity_id         | uuid        | N        |
| representation_id      | uuid        | N        |
| representation_version | bigint      | N        |
| effective_from         | timestamptz | N        |
| effective_to           | timestamptz | S        |
| cause_code             | varchar(80) | N        |
| action_id              | uuid        | N        |

PK: efectividad.

FK a versión y actuación.

Una exclusión por representación e intervalo impide versiones efectivas superpuestas.

Los intervalos incluyen inicio y excluyen final.

### **22.3. representation_continuity_review**

Conserva:

- review_id uuid N, PK.

- representation_id uuid N, FK.

- reviewed_version bigint N, parte de FK a versión.

- planned_date date N.

- reviewed_at timestamptz S.

- status varchar(20) N.

- conclusion_code varchar(80) S.

- verification_references jsonb N.

- approval_reference jsonb S.

- next_review_date date S.

- Bloques T y C.

Estados: PENDING, UNDER_REVIEW, CONFIRMED y REQUIRES_REVIEW.

Confirmación exige fecha, conclusión y próxima revisión cuando continúa sin vencimiento documental.

Índices:

- status, planned_date, review_id.

- representation_id, planned_date, review_id.

La revisión sin vencimiento documental no supera doce meses.

Un control vencido no se transforma automáticamente en continuidad confirmada.

## **23. Cambios y preparaciones de representación**

### **23.1. representation_change**

| **Columna**             | **Tipo**      | **Nulo**      |
|-------------------------|---------------|---------------|
| change_id               | uuid          | N             |
| representation_id       | uuid          | N             |
| change_type             | varchar(30)   | N             |
| previous_version        | bigint        | N             |
| resulting_version       | bigint        | S             |
| received_at             | timestamptz   | N             |
| effective_at            | timestamptz   | N             |
| reason                  | varchar(4000) | N             |
| proposed_definition     | jsonb         | S             |
| approval_reference      | jsonb         | S             |
| verification_references | jsonb         | N             |
| pending_components      | jsonb         | N             |
| status                  | varchar(30)   | N             |
| Columnas T y S          | Según bloques | Según bloques |
| lock_version            | bigint        | N             |

PK: cambio.

FK a raíz y versiones locales presentes.

Tipos:

AMEND, RENEW, REVOKE, END y CONFIRM_CONTINUITY.

Estados internos: PENDING, APPLIED, REQUIRES_REVIEW y REJECTED.

Ampliaciones y renovaciones exigen aprobación equivalente al reconocimiento.

Índice: representation_id, received_at, change_id.

### **23.2. representation_preparation_basis**

| **Columna**            | **Tipo**      | **Nulo** |
|------------------------|---------------|----------|
| preparation_id         | uuid          | N        |
| representation_id      | uuid          | N        |
| representation_version | bigint        | N        |
| represented_party_id   | uuid          | N        |
| basis_role             | varchar(80)   | N        |
| acting_party_ids       | jsonb         | N        |
| act_code               | varchar(80)   | N        |
| object_reference       | jsonb         | N        |
| product_reference      | jsonb         | S        |
| amount                 | numeric(24,8) | S        |
| currency               | varchar(3)    | S        |
| scope_hash             | char(64)      | N        |

PK: preparación, representación y papel del fundamento.

FK a preparación común, versión y control del representado.

Actores no vacíos y sin duplicados.

Importe y moneda aparecen conjuntamente.

Índices:

- representation_id, representation_version, preparation_id.

- represented_party_id, preparation_id.

Cada fundamento derivado utilizado se registra como base, no solo la representación final.

### **23.3. representation_preparation_evidence**

| **Columna**            | **Tipo**    | **Nulo** |
|------------------------|-------------|----------|
| evidence_id            | uuid        | N        |
| preparation_id         | uuid        | N        |
| evidence_type          | varchar(80) | N        |
| actor_party_id         | uuid        | S        |
| evaluated_content_hash | char(64)    | N        |
| source_reference       | jsonb       | N        |
| document_references    | jsonb       | N        |
| verified_at            | timestamptz | N        |

PK: evidencia.

FK a preparación.

Índice: preparation_id, evidence_type, evidence_id.

Las conformidades conjuntas deben referirse al mismo contenido.

## **24. Transacciones y orden de bloqueo**

### **24.1. Personas**

Cambios de datos:

1.  Persona.

2.  Contactos o domicilios afectados, por identificador.

3.  Versiones y desafíos.

4.  Instantánea de persona.

Las relaciones bloquean las personas afectadas en orden estable antes de modificar la relación.

Las resoluciones de duplicados adquieren primero el control del grafo.

Una transacción que necesita ese control no lo adquiere después de bloquear personas.

### **24.2. Vinculación**

Orden:

1.  Solicitud.

2.  Decisiones, en orden estable.

3.  Usos.

4.  Requerimientos y puntos afectados.

Las operaciones de uso que no modifican solicitud pueden comenzar por la decisión, pero no adquieren después el bloqueo
de solicitud.

### **24.3. Clientes**

Orden:

1.  Control por persona principal.

2.  Cliente.

3.  Constitución o período.

4.  Revisión o desvinculación.

5.  Componentes.

La readmisión y el cese utilizan los mismos controles de identidad comercial.

### **24.4. Representación**

Orden:

1.  Control global del grafo.

2.  Controles de representados afectados, por identificador.

3.  Solicitudes y representaciones afectadas, en orden estable.

4.  Versiones, dependencias y preparaciones.

Se determina la cadena de fundamentos bajo el control del grafo.

Una revocación no deja utilizables fundamentos derivados incompatibles por omisión.

Las consultas a coordinadores se realizan después del commit.

## **25. Contenido estructurado, historia y eventos**

Los JSONB utilizan los esquemas de sus contratos:

| **Contenido**             | **Esquema**                                             |
|---------------------------|---------------------------------------------------------|
| Persona histórica         | Party.                                                  |
| Actividad declarada       | Actividad, origen de fondos y fuente.                   |
| Alcance de admisión       | AdmissionScope.                                         |
| Verificaciones            | Referencias versionadas y evidencias.                   |
| Inventarios               | Objetos, corte, pendientes y resultado del propietario. |
| Definición representativa | Representation.                                         |
| Alcance representativo    | RepresentationScope.                                    |
| Actuación conjunta        | JointRule.                                              |
| Resultados y pendientes   | Estructuras comunes.                                    |

Se rechazan propiedades desconocidas y variantes incompatibles.

Los cambios de elementos de una persona incrementan su versión y registran instantánea, historia y outbox conjuntamente.

Los eventos CI-02-025 a CI-02-033 mantienen sus tipos, temas y agregados acordados.

Los eventos de cliente utilizan la versión del cliente.

Los motivos internos de admisión, secretos de contacto y documentos reservados no se distribuyen en eventos generales.

## **26. Trabajos, migraciones y conservación**

Los trabajos persistentes comprenden:

- Envío y vencimiento de desafíos.

- Recordatorios y plazos.

- Recuperación de usos de admisión.

- Continuación de constituciones.

- Revisiones comerciales.

- Inventarios de desvinculación.

- Continuidad de representaciones.

- Resolución de preparaciones.

- Derivaciones de circunstancias.

- Publicación de outbox.

Las identidades de trabajo incluyen objeto, finalidad y ocurrencia cuando corresponde.

El vencimiento del trabajador no acredita ausencia de efecto remoto.

Las migraciones utilizan Flyway.

Las restricciones se incorporan después de comprobar datos existentes.

No se elimina una persona porque termine su relación comercial.

Las referencias asociadas por duplicados permanecen resolubles.

Las decisiones, usos, períodos y representaciones utilizadas mantienen historia.

Redis puede acelerar consultas, pero no conserva la única copia de decisiones, desafíos, usos o preparaciones.

La restauración reconcilia actuaciones remotas antes de reanudar constituciones, envíos o resoluciones.

## **27. Correspondencia con los contratos**

| **Contratos**                   | **Estructuras principales**                                                            |
|---------------------------------|----------------------------------------------------------------------------------------|
| CI-02-001 a CI-02-003           | Persona, versiones, datos, fuentes, identificadores, contactos, domicilios y desafíos. |
| CI-02-004 y CI-02-005           | Relaciones y mediciones históricas.                                                    |
| CI-02-006                       | Casos, candidatos y resoluciones de duplicados.                                        |
| CI-02-007                       | Circunstancias, verificaciones y aplicaciones derivadas.                               |
| CI-02-008 a CI-02-010           | Vinculación, requerimientos, presentaciones y ciclo.                                   |
| CI-02-011 a CI-02-013           | Decisiones versionadas y uso de admisión.                                              |
| CI-02-014 y CI-02-015           | Constituciones, clientes y períodos.                                                   |
| CI-02-016 y CI-02-017           | Revisiones y desvinculación.                                                           |
| CI-02-018 y CI-02-019           | Solicitudes, versiones y cambios de representación.                                    |
| CI-02-020 y CI-02-021           | Consulta histórica y comprobación representativa.                                      |
| CI-02-022 a CI-02-024           | Actuaciones y seguimiento.                                                             |
| CI-02-025 a CI-02-033           | Outbox, historia y versiones de agregados.                                             |
| Preparaciones de representación | Preparación común, fundamentos, evidencias y controles locales.                        |

## **28. Escenarios de validación**

| **Escenario**                               | **Garantía**                                      |
|---------------------------------------------|---------------------------------------------------|
| Nacimiento desconocido                      | Estado explícito sin fecha ficticia.              |
| Persona humana con datos jurídicos          | No confirma combinación incompatible.             |
| Identificador declarado por dos personas    | Conserva contradicción sin fusión automática.     |
| Contacto compartido                         | No impone unicidad global incorrecta.             |
| Contacto cambia durante confirmación        | El desafío no confirma la versión nueva.          |
| Prueba fallida repetida                     | Una actuación consume un solo intento.            |
| Dos respuestas simultáneas                  | Se ordenan sobre persona, contacto y desafío.     |
| Desafío sustituido recibido tarde           | No vuelve a quedar utilizable.                    |
| Domicilio de otra persona                   | La FK compuesta impide vinculación incorrecta.    |
| Resoluciones de duplicados concurrentes     | No crean ciclos de referencia principal.          |
| Presentación oportuna pendiente de revisión | No vence por demora interna.                      |
| Uso y retiro de admisión concurrentes       | Conserva una decisión local coherente.            |
| Uso confirmado y caída de Clientes          | Continúa la misma constitución.                   |
| Dos constituciones equivalentes             | No crean clientes ni períodos activos duplicados. |
| Admisión vencida después de uso válido      | No invalida el uso confirmado.                    |
| Inventario sin respuesta                    | Permanece pendiente.                              |
| Readmisión                                  | Conserva períodos anteriores.                     |
| Revisión comercial atrasada                 | No retrodata la conclusión.                       |
| Representación recibida sin aprobación      | No activa facultades.                             |
| Revocación de fundamento                    | Identifica dependencias y preparaciones.          |
| Actores conforman contenidos distintos      | No satisface actuación conjunta.                  |
| Preparación con decisión desconocida        | No se libera por timeout.                         |
| Evento duplicado o restauración             | No repite constituciones, usos ni aplicaciones.   |

La revisión documental comprueba las estructuras y su correspondencia con los contratos. Las claves, restricciones,
bloqueos y atomicidad se verifican con PostgreSQL real aislado durante la implementación.
