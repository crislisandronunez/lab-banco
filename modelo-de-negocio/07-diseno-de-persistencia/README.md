# Diseño de persistencia del sistema bancario

## 1. Propósito y alcance

Este documento establece las convenciones comunes para almacenar, proteger y recuperar los datos del sistema.

Comprende:

- Propiedad y aislamiento.

- Tipos, nulabilidad y relaciones.

- Identidades y restricciones.

- Versiones e historia.

- Transacciones y concurrencia.

- Actuaciones e idempotencia.

- Preparaciones, intentos y barreras.

- Outbox, inbox y trabajos persistentes.

- Secuencias y cortes.

- Archivos, proyecciones y caché.

- Migraciones, conservación y restauración.

El diseño de persistencia materializa las responsabilidades, operaciones y contratos técnicos acordados.

No modifica el significado del negocio ni traslada autoridad entre servicios.

## 2. Organización documental

El diseño comprende este documento principal y doce apartados de módulo.

| **Apartado**                             | **Contenido**                                                           |
|------------------------------------------|-------------------------------------------------------------------------|
| Principal                                | Convenciones, estructuras técnicas y garantías comunes.                 |
| 1\. Institución y oferta comercial       | Organización, facultades, productos, condiciones y calendarios.         |
| 2\. Personas y relaciones bancarias      | Personas, vinculación, clientes y representación.                       |
| 3\. Contratación                         | Solicitudes, modelos, propuestas, aceptaciones y contratos.             |
| 4\. Cuentas y fondos                     | Cuentas, identificadores, posiciones, reservas, movimientos y efectivo. |
| 5\. Pagos y transferencias               | Instrucciones, pagos, recurrencias, instrumentos y compensación.        |
| 6\. Financiación                         | Evaluaciones, aprobaciones, garantías, deuda y recuperaciones.          |
| 7\. Tarjetas                             | Instrumentos, autorizaciones, consumos, resúmenes y disputas.           |
| 8\. Ahorro e inversiones                 | Depósitos, órdenes, unidades, derechos y custodia.                      |
| 9\. Contabilidad                         | Hechos, asientos, períodos, controles e informes.                       |
| 10\. Tesorería                           | Recursos propios, compromisos, cambio y liquidaciones.                  |
| 11\. Riesgo y cumplimiento               | Exposición, verificaciones, investigaciones, medidas y presentaciones.  |
| 12\. Atención y operación administrativa | Reclamos, tareas, documentos, comunicaciones y extractos.               |

Cada módulo se organiza por servicio propietario.

Las referencias documentales utilizan nombres e identificadores, sin depender de rutas de archivos.

Las tablas comunes se definen aquí. Cada módulo identifica cuáles incorpora y define sus extensiones de negocio.

## 3. Propiedad y aislamiento

### 3.1. Base por servicio

Cada servicio mantiene:

- Base PostgreSQL propia.

- Usuario de ejecución exclusivo.

- Identidad separada para migraciones.

- Tablas de negocio.

- Estructuras técnicas necesarias.

- Controles de acceso y recuperación.

El usuario de ejecución no modifica el esquema ni accede a bases de otros servicios.

No existen claves foráneas, consultas SQL, vistas ni relaciones JPA entre bases de servicios distintos.

### 3.2. Referencias locales

Las referencias a objetos de la misma base utilizan claves foráneas cuando su existencia es obligatoria.

Las claves compuestas comprueban pertenencia cuando una relación debe conservar el mismo padre, versión o ámbito.

No basta con comprobar por separado que dos identificadores existen.

### 3.3. Referencias remotas

Una referencia remota conserva:

| **Atributo**                | **Tipo**        |
|-----------------------------|-----------------|
| Servicio propietario        | varchar(80)     |
| Tipo de objeto              | varchar(80)     |
| Identificador               | uuid            |
| Versión, cuando corresponde | bigint positivo |

La referencia documental conserva identificador y versión del documento.

Los atributos se almacenan en columnas cuando intervienen en unicidad, búsqueda o concurrencia.  
Pueden formar parte de JSONB cuando únicamente integran una instantánea validada.

Una referencia remota no tiene FK hacia otra base.

La ausencia de una copia local no demuestra inexistencia del objeto remoto.

## 4. Convenciones físicas

### 4.1. Nombres

Tablas, columnas, restricciones e índices utilizan snake_case.

Las claves primarias tienen nombres explícitos, como account_id, contract_id o reservation_id.

Los nombres de restricciones e índices identifican tabla y finalidad y respetan el límite de PostgreSQL.

### 4.2. Tipos

| **Dato**                      | **PostgreSQL** | **Regla**                                        |
|-------------------------------|----------------|--------------------------------------------------|
| Identificador                 | uuid           | Estable y no reutilizable.                       |
| Versión de negocio            | bigint         | Mayor que cero; comienza en 1.                   |
| Versión de control            | bigint         | No negativa; comienza en 0.                      |
| Contador transaccional        | bigint         | No negativo; comienza en 0.                      |
| Secuencia asignada a un hecho | bigint         | Mayor que cero.                                  |
| Código                        | varchar(80)    | No vacío y perteneciente a su catálogo.          |
| Referencia externa            | varchar(120)   | Conserva caracteres significativos.              |
| Clave de idempotencia         | varchar(120)   | Entre 1 y 120 caracteres.                        |
| Correlación                   | varchar(120)   | Entre 1 y 120 caracteres.                        |
| Fundamento                    | varchar(4000)  | No vacío cuando es obligatorio.                  |
| Fecha                         | date           | Sin conversión de zona.                          |
| Instante                      | timestamptz    | Intercambio en UTC.                              |
| Indicador                     | boolean        | Sin sustitución por códigos numéricos.           |
| Importe monetario             | numeric(24,2)  | Signo según el campo.                            |
| Importe contractual           | numeric(24,8)  | Solo donde el contrato admite mayor escala.      |
| Tasa                          | numeric(24,12) | Proporción decimal.                              |
| Cantidad o cálculo            | numeric(30,12) | Rango según instrumento o metodología.           |
| Participación                 | numeric(15,12) | Entre cero y uno.                                |
| Hash SHA-256                  | char(64)       | Hexadecimal en minúsculas.                       |
| Tamaño en bytes               | bigint         | No negativo; positivo para archivos confirmados. |
| Contenido estructurado        | jsonb          | Esquema identificado y validado.                 |

No se utiliza punto flotante binario para cálculos financieros.

Los identificadores bancarios, documentales y tributarios se almacenan como texto, preservando ceros iniciales.

### 4.3. Validación numérica

La aplicación valida precisión y escala antes de convertir el dato al tipo SQL.

El redondeo implícito de numeric no se utiliza para aceptar instrucciones inválidas.

Los cálculos utilizan precisión suficiente antes de obtener el resultado persistido.

### 4.4. Nulabilidad

En las tablas de este documento:

- **Nulo: No** significa NOT NULL.

- **Nulo: Sí** permite ausencia con el significado indicado.

Las condiciones de presencia se protegen mediante restricciones y validación transaccional cuando corresponda.

Un campo desconocido no utiliza cero, cadena vacía o fecha ficticia.

## 5. Ausencia, colecciones y contenido estructurado

### 5.1. Ausencia explícita

Los atributos que distinguen calidad de información conservan estado y valor.

| **Estado**     | **Valor** |
|----------------|-----------|
| PROVIDED       | Presente. |
| UNKNOWN        | Ausente.  |
| NOT_PROVIDED   | Ausente.  |
| NOT_APPLICABLE | Ausente.  |

La combinación incompatible se rechaza.

### 5.2. Colecciones

Participantes, componentes, asignaciones y elementos con identidad o controles propios utilizan tablas relacionadas.

El orden significativo utiliza una posición positiva y única dentro del padre.

Una colección vacía se diferencia de una fuente incompleta.

### 5.3. JSONB

Se utiliza para:

- Solicitudes y resultados versionados.

- Evidencias estructuradas.

- Contenido de eventos.

- Configuraciones tipadas.

- Instantáneas inmutables.

Cada contenido identifica esquema y versión mediante columnas o mediante la definición inequívoca de su tabla.

No sustituye columnas necesarias para claves, relaciones, importes controlados o consultas habituales.

La forma textual devuelta por PostgreSQL no se utiliza para recalcular huellas contractuales.

## 6. Claves, restricciones e índices

### 6.1. Identidad

Cada tabla identifica explícitamente su PK.

La generación de UUID no sustituye la unicidad de negocio.

Las restricciones únicas especifican todas sus columnas y, cuando son parciales, su condición.

### 6.2. Relaciones

Cada FK identifica:

- Columnas de origen.

- Tabla y columnas de destino.

- Nulabilidad.

- Tratamiento de eliminación.

El tratamiento ordinario es impedir la eliminación del padre referenciado.

No se utiliza cascada para borrar movimientos, contratos, decisiones, evidencias o historia aplicada.

### 6.3. Restricciones

Las condiciones sobre una fila utilizan CHECK cuando son expresables de forma estable.

Las reglas entre filas utilizan restricciones únicas, exclusiones temporales o transacciones sobre un control
compartido.

Una consulta previa no es la única protección de una invariante concurrente.

### 6.4. Índices

Cada índice declara:

- Nombre.

- Columnas y orden.

- Unicidad.

- Condición, si existe.

- Consulta o control que lo utiliza.

Los índices implícitos de PK y restricciones únicas no se duplican.

Las tablas de módulos añaden los índices de sus consultas y relaciones.

## 7. Versiones, vigencias e historia

Las versiones de negocio comienzan en 1 y se incrementan junto con el cambio confirmado.

lock_version comienza en 0 y cambia en cada modificación protegida de la fila.

Los contadores pueden estar en cero antes del primer hecho. No representan una versión de negocio cero.

Las definiciones aprobadas o utilizadas son inmutables. Los cambios generan nuevas versiones.

Los intervalos temporales incluyen inicio y excluyen final, salvo que un contrato defina expresamente  
otra convención, como cobertura de fechas de un calendario.

El final ausente representa continuidad cuando esa modalidad está admitida.

Las correcciones agregan registros vinculados al original.

## 8. Tabla común service_action

Conserva el comando aceptado y su resultado recuperable.

| **Columna**              | **Tipo**     | **Nulo** | **Regla**                            |
|--------------------------|--------------|----------|--------------------------------------|
| action_id                | uuid         | No       | PK.                                  |
| operation_id             | uuid         | No       | Operación de negocio.                |
| technical_operation_id   | varchar(80)  | No       | Operación técnica invocada.          |
| producer_service         | varchar(80)  | No       | Productor autenticado.               |
| actor_reference          | jsonb        | Sí       | Actor o delegación verificable.      |
| purpose                  | varchar(80)  | No       | Finalidad.                           |
| request_schema_code      | varchar(80)  | No       | Esquema de entrada.                  |
| request_schema_version   | bigint       | No       | Positiva.                            |
| canonicalization_version | bigint       | No       | Positiva.                            |
| request_hash             | char(64)     | No       | Huella vinculante.                   |
| correlation_id           | varchar(120) | No       | Correlación inicial.                 |
| processing_status        | varchar(30)  | No       | Estado común.                        |
| outcome_code             | varchar(80)  | Sí       | Resultado conocido.                  |
| received_at              | timestamptz  | No       | Recepción efectiva.                  |
| original_received_at     | timestamptz  | Sí       | Recepción original acreditada.       |
| confirmed_at             | timestamptz  | Sí       | Confirmación local.                  |
| result_schema_code       | varchar(80)  | No       | Esquema del resultado.               |
| result_schema_version    | bigint       | No       | Positiva.                            |
| result_payload           | jsonb        | No       | Resultado actual, incluso pendiente. |
| version                  | bigint       | No       | Versión de negocio positiva.         |
| lock_version             | bigint       | No       | Control no negativo.                 |
| updated_at               | timestamptz  | No       | Última actualización.                |

### 8.1. Restricciones

processing_status admite:

- PENDING.

- COMPLETED.

- REQUIRES_REVIEW.

Un resultado COMPLETED exige outcome_code y confirmed_at.

Mientras permanece PENDING, confirmed_at está ausente.

REQUIRES_REVIEW no equivale a ausencia de efectos. El resultado conserva lo confirmado y lo pendiente.

confirmed_at, cuando existe, no es anterior a received_at.

original_received_at no permite antedatar una decisión.

### 8.2. Índices

| **Índice**                      | **Columnas**                             | **Condición y finalidad**                             |
|---------------------------------|------------------------------------------|-------------------------------------------------------|
| ix_service_action_operation     | operation_id, received_at, action_id     | Seguimiento de una operación.                         |
| ix_service_action_producer_time | producer_service, received_at, action_id | Consulta autorizada por productor y recepción.        |
| ix_service_action_pending       | updated_at, action_id                    | Parcial para PENDING y REQUIRES_REVIEW; recuperación. |

### 8.3. Recuperación

El productor y contenido se comprueban al reutilizar action_id.

La recuperación vuelve a evaluar autorización antes de devolver el resultado.

Los secretos no se incorporan al resultado conservado.

El historial de resultados relevantes se conserva mediante business_change_history.

## 9. Tabla común idempotency_record

Relaciona una clave del productor con la actuación original.

| **Columna**            | **Tipo**     | **Nulo** | **Regla**                      |
|------------------------|--------------|----------|--------------------------------|
| producer_service       | varchar(80)  | No       | Parte de PK.                   |
| idempotency_key        | varchar(120) | No       | Parte de PK; no vacía.         |
| action_id              | uuid         | No       | FK a service_action.action_id. |
| technical_operation_id | varchar(80)  | No       | Operación vinculada.           |
| request_hash           | char(64)     | No       | Huella original.               |
| created_at             | timestamptz  | No       | Registro.                      |

La PK es (producer_service, idempotency_key).

Existe índice ix_idempotency_record_action sobre action_id.

La FK impide eliminar una actuación todavía referenciada.

La inserción comprueba que productor, operación técnica y huella coincidan con service_action.

### 9.1. Admisión concurrente

Actuación y clave se vinculan en una transacción.

Una competencia sobre cualquiera de sus identidades se resuelve recuperando el registro existente  
y comparando contenido.

| **Situación**                                               | **Resultado**                           |
|-------------------------------------------------------------|-----------------------------------------|
| Misma clave, productor y contenido                          | Recupera la actuación original.         |
| Misma clave con contenido incompatible                      | Conflicto.                              |
| Misma actuación con otra clave y contenido coherente        | Vincula o recupera sin repetir efectos. |
| Misma actuación con otro productor o contenido incompatible | Conflicto.                              |

Archivar detalle no elimina la identidad mínima necesaria para reconocer repeticiones.

## 10. Identidad de efectos de negocio

La idempotencia técnica no sustituye la identidad económica u operativa.

Cada módulo define sus restricciones de efecto, por ejemplo:

- Constitución.

- Movimiento.

- Desembolso.

- Ingreso.

- Imputación.

- Restitución.

- Presentación.

- Ocurrencia.

- Emisión.

- Cumplimiento.

La definición identifica:

- Propietario.

- Columnas de identidad.

- Contenido vinculante.

- Estado pendiente o confirmado.

- Resultado.

- Tratamiento de intentos sucesores.

No se incorpora una segunda tabla común que duplique la autoridad de las tablas económicas de cada servicio.

Los efectos compartidos entre circuitos utilizan la misma identidad de negocio y el control de su propietario.

## 11. Control común de identidad de intentos

### 11.1. execution_identity_control

Permite ordenar la petición original y, cuando el contrato lo admite, una barrera anterior a esa petición.

| **Columna**  | **Tipo**    | **Nulo** | **Regla**             |
|--------------|-------------|----------|-----------------------|
| attempt_id   | uuid        | No       | PK.                   |
| attempt_type | varchar(80) | No       | Tipo registrado.      |
| created_at   | timestamptz | No       | Creación del control. |
| lock_version | bigint      | No       | No negativa.          |

Existe restricción única sobre (attempt_id, attempt_type), utilizada por las referencias compuestas.

La fila se crea bajo la PK antes de comprobar una invariante sobre un intento todavía inexistente.

Los procesos competidores bloquean esa misma fila.

### 11.2. Ámbito

Esta tabla es local al coordinador.

No es un registro central de intentos ni concede a otro servicio autoridad para decidirlos.

Cuando el contrato no permite abortar una identidad desconocida, la ausencia devuelve el tratamiento  
pendiente o desconocido definido por ese contrato.

## 12. Tabla común execution_attempt

| **Columna**              | **Tipo**    | **Nulo** | **Regla**                                                                |
|--------------------------|-------------|----------|--------------------------------------------------------------------------|
| attempt_id               | uuid        | No       | PK.                                                                      |
| attempt_type             | varchar(80) | No       | Tipo de intento.                                                         |
| action_id                | uuid        | No       | FK a service_action.action_id.                                           |
| operation_id             | uuid        | No       | Operación de negocio.                                                    |
| binding_schema_code      | varchar(80) | No       | Esquema vinculante.                                                      |
| binding_schema_version   | bigint      | No       | Positiva.                                                                |
| canonicalization_version | bigint      | No       | Positiva.                                                                |
| binding_content          | jsonb       | No       | Contenido inmutable preparado.                                           |
| content_hash             | char(64)    | No       | Huella vinculante.                                                       |
| target_references        | jsonb       | No       | Referencias tipadas; puede ser colección vacía si el contrato lo admite. |
| decision_deadline        | timestamptz | No       | Límite de decisión.                                                      |
| processing_state         | varchar(40) | No       | Estado técnico interno.                                                  |
| decision                 | varchar(20) | No       | Decisión durable.                                                        |
| decided_at               | timestamptz | Sí       | Exigida con decisión final.                                              |
| result_references        | jsonb       | No       | Resultados conocidos.                                                    |
| created_at               | timestamptz | No       | Registro.                                                                |
| updated_at               | timestamptz | No       | Última actualización.                                                    |
| version                  | bigint      | No       | Positiva.                                                                |
| lock_version             | bigint      | No       | No negativa.                                                             |

La PK es (attempt_id).

La restricción uq_execution_attempt_id_type establece unicidad sobre (attempt_id, attempt_type)  
en esta tabla. Permite que las extensiones locales referencien conjuntamente la identidad y el tipo del intento.

La FK local desde execution_attempt(attempt_id, attempt_type) referencia  
execution_identity_control(attempt_id, attempt_type) e impide eliminar el control referenciado.

La FK local desde execution_attempt(action_id) referencia service_action(action_id)  
e impide eliminar la actuación referenciada.

Las extensiones que utilizan FK compuesta hacia execution_attempt referencian  
(attempt_id, attempt_type). La restricción única de execution_identity_control no sustituye  
la requerida en execution_attempt.

### 12.1. Restricciones

decision admite PENDING, COMMITTED y ABORTED.

processing_state admite:

- PREPARING.

- READY_TO_DECIDE.

- COMMITTED.

- ABORTED.

- RECOVERY_REQUIRED.

Se exige:

- COMMITTED de procesamiento corresponde a decisión COMMITTED.

- ABORTED de procesamiento corresponde a decisión ABORTED.

- Los demás estados mantienen decisión PENDING.

- decided_at está presente exclusivamente con decisión final.

- decision_deadline no es anterior a la creación.

- Contenido y huella no cambian después de registrar el intento.

Una decisión final es inmutable.

Los pendientes posteriores a la decisión se conservan en actuaciones, componentes y trabajos;  
no vuelven a pendiente la decisión.

### 12.2. Índices

| **Índice**                     | **Columnas**                         | **Finalidad**                              |
|--------------------------------|--------------------------------------|--------------------------------------------|
| ix_execution_attempt_action    | action_id, attempt_id                | Recuperar intentos por actuación.          |
| ix_execution_attempt_operation | operation_id, created_at, attempt_id | Seguimiento de negocio.                    |
| ix_execution_attempt_pending   | decision_deadline, attempt_id        | Parcial para decisión PENDING; resolución. |

La PK y uq_execution_attempt_id_type generan sus índices correspondientes.  
No se crean índices adicionales que dupliquen esas mismas claves.

Las exclusiones de intentos pendientes por propuesta, autorización u objeto se definen  
en el módulo correspondiente.

### 12.3. Decisión y efecto

La misma transacción confirma:

- Decisión.

- Efecto local indivisible.

- Recursos consumidos o transformados.

- Resultado.

- Historia.

- Outbox.

- Trabajos posteriores.

No se registra decisión confirmada separada del efecto local que determina.

## 13. Barreras de aborto

### 13.1. execution_abort_barrier

Se utiliza únicamente cuando el contrato permite impedir una petición original que todavía no fue recibida.

| **Columna**         | **Tipo**    | **Nulo** | **Regla**                |
|---------------------|-------------|----------|--------------------------|
| attempt_id          | uuid        | No       | PK.                      |
| attempt_type        | varchar(80) | No       | Tipo registrado.         |
| abort_action_id     | uuid        | No       | FK a service_action.     |
| requesting_service  | varchar(80) | No       | Solicitante autenticado. |
| reason_code         | varchar(80) | No       | Motivo.                  |
| evidence_references | jsonb       | No       | Evidencias tipadas.      |
| created_at          | timestamptz | No       | Confirmación de barrera. |

La FK compuesta (attempt_id, attempt_type) apunta a execution_identity_control.

Existe índice sobre abort_action_id.

### 13.2. Competencia con la petición original

Ambas operaciones bloquean execution_identity_control.

Si ya existe intento confirmado, no se registra una barrera que contradiga su decisión.

Si existe intento pendiente, el aborto se resuelve sobre ese intento.

Si se admite barrera previa y no existe intento, se confirma la barrera.

Una petición original posterior encuentra la barrera y no ejecuta.

Una identidad desconocida sin barrera no demuestra aborto definitivo.

La barrera conserva su identidad durante archivo y restauración.

## 14. Tabla común authority_preparation

| **Columna**              | **Tipo**    | **Nulo** | **Regla**                                     |
|--------------------------|-------------|----------|-----------------------------------------------|
| preparation_id           | uuid        | No       | PK.                                           |
| action_id                | uuid        | No       | FK a service_action.                          |
| coordinator_service      | varchar(80) | No       | Coordinador autorizado.                       |
| attempt_type             | varchar(80) | No       | Tipo de intento remoto.                       |
| attempt_id               | uuid        | No       | Intento remoto.                               |
| component_code           | varchar(80) | No       | Condición preparada.                          |
| binding_schema_code      | varchar(80) | No       | Esquema vinculante.                           |
| binding_schema_version   | bigint      | No       | Positiva.                                     |
| canonicalization_version | bigint      | No       | Positiva.                                     |
| prepared_content         | jsonb       | No       | Contenido inmutable.                          |
| content_hash             | char(64)    | No       | Huella.                                       |
| scope_payload            | jsonb       | No       | Alcance tipado.                               |
| state                    | varchar(40) | No       | Estado de preparación.                        |
| prepared_at              | timestamptz | Sí       | Presente cuando la preparación fue favorable. |
| decision_deadline        | timestamptz | No       | Límite vinculado al intento.                  |
| known_decision           | varchar(20) | Sí       | Decisión final comprobada.                    |
| decision_evidence        | jsonb       | Sí       | Evidencia de decisión.                        |
| concluded_at             | timestamptz | Sí       | Conclusión.                                   |
| rejection_reason_code    | varchar(80) | Sí       | Exigido para rechazo.                         |
| created_at               | timestamptz | No       | Recepción local.                              |
| updated_at               | timestamptz | No       | Última actualización.                         |
| version                  | bigint      | No       | Positiva.                                     |
| lock_version             | bigint      | No       | No negativa.                                  |

### 14.1. Identidad y referencias

Existe unicidad sobre:

(coordinator_service, attempt_type, attempt_id, component_code).

El intento remoto no tiene FK hacia otra base.

La consulta de decisión utiliza esos atributos y destinos configurados. No acepta URL arbitraria.

Los módulos incorporan tablas relacionadas que identifican los controles locales y versiones utilizados.

### 14.2. Restricciones

Los estados son:

PREPARED, RESOLUTION_PENDING, CONCLUDED_COMMITTED, CONCLUDED_ABORTED y REJECTED.

Se exige:

- PREPARED y RESOLUTION_PENDING: preparación favorable presente, sin decisión final.

- CONCLUDED_COMMITTED: decisión COMMITTED, evidencia y conclusión.

- CONCLUDED_ABORTED: decisión ABORTED, evidencia y conclusión.

- REJECTED: motivo presente, sin preparación favorable.

Una conclusión no es anterior a la creación.

### 14.3. Índices

| **Índice**                           | **Columnas**                                  | **Finalidad**                               |
|--------------------------------------|-----------------------------------------------|---------------------------------------------|
| ix_authority_preparation_action      | action_id                                     | Seguimiento de actuación.                   |
| ix_authority_preparation_pending     | decision_deadline, preparation_id             | Parcial para PREPARED y RESOLUTION_PENDING. |
| ix_authority_preparation_coordinator | coordinator_service, attempt_type, attempt_id | Resolver componentes de un intento.         |

Los índices por cuenta, producto, representado o ámbito se definen en sus tablas de relación.

### 14.4. Cambios incompatibles

La preparación y el cambio utilizan los mismos controles locales.

El cambio bloquea nuevas preparaciones incompatibles y conserva los intentos pendientes.

No transforma una preparación favorable en abortada sin comprobar la decisión del coordinador.

El vencimiento inicia resolución y escalación. No libera automáticamente.

## 15. Tabla común business_change_history

| **Columna**           | **Tipo**      | **Nulo** | **Regla**                                    |
|-----------------------|---------------|----------|----------------------------------------------|
| history_id            | uuid          | No       | PK.                                          |
| object_type           | varchar(80)   | No       | Tipo local de objeto.                        |
| object_id             | uuid          | No       | Identidad local.                             |
| previous_version      | bigint        | Sí       | Ausente en creación.                         |
| resulting_version     | bigint        | No       | Positiva.                                    |
| action_id             | uuid          | Sí       | FK a actuación cuando la origina un comando. |
| source_event_id       | uuid          | Sí       | Evento de origen cuando corresponde.         |
| actor_reference       | jsonb         | Sí       | Actor verificado.                            |
| producer_service      | varchar(80)   | No       | Servicio responsable del cambio.             |
| change_code           | varchar(80)   | No       | Cambio registrado.                           |
| recorded_at           | timestamptz   | No       | Registro.                                    |
| effective_at          | timestamptz   | Sí       | Efecto cuando está determinado.              |
| reason                | varchar(4000) | Sí       | Fundamento cuando corresponde.               |
| evidence_references   | jsonb         | No       | Referencias tipadas.                         |
| detail_schema_code    | varchar(80)   | No       | Esquema del detalle.                         |
| detail_schema_version | bigint        | No       | Positiva.                                    |
| detail_payload        | jsonb         | No       | Detalle mínimo necesario.                    |

Si existe versión anterior, la resultante debe ser mayor.

(object_type, object_id) es una referencia local discriminada. No se declara como FK polimórfica.

La existencia del objeto se comprueba en la transacción que registra el cambio.

Índices:

- ix_business_history_object: object_type, object_id, resulting_version, history_id.

- ix_business_history_action: action_id, parcial para valores presentes.

- ix_business_history_event: source_event_id, parcial para valores presentes.

La historia es inmutable y no sustituye las tablas operativas.

## 16. Tabla común outbox_event

| **Columna**        | **Tipo**     | **Nulo** | **Regla**                             |
|--------------------|--------------|----------|---------------------------------------|
| event_id           | uuid         | No       | PK y eventId estable.                 |
| event_type         | varchar(80)  | No       | Se publica como type.                 |
| schema_version     | bigint       | No       | Positiva.                             |
| producer_service   | varchar(80)  | No       | Servicio propietario.                 |
| aggregate_type     | varchar(80)  | No       | Tipo del agregado.                    |
| aggregate_id       | uuid         | No       | Identidad del agregado.               |
| aggregate_version  | bigint       | Sí       | Positiva cuando corresponde.          |
| aggregate_sequence | bigint       | Sí       | Positiva cuando el contrato la exige. |
| operation_id       | uuid         | No       | Operación de negocio.                 |
| action_id          | uuid         | Sí       | FK a actuación local.                 |
| correlation_id     | varchar(120) | No       | Correlación.                          |
| causation_id       | uuid         | Sí       | Causa según el contrato.              |
| occurred_at        | timestamptz  | No       | Momento del hecho.                    |
| topic              | text         | No       | Tema configurado y no vacío.          |
| partition_key      | text         | No       | Clave definida por contrato.          |
| payload            | jsonb        | No       | Contenido validado e inmutable.       |
| publication_state  | varchar(30)  | No       | Estado de publicación.                |
| attempt_count      | integer      | No       | No negativo.                          |
| next_attempt_at    | timestamptz  | Sí       | Próxima publicación pendiente.        |
| claim_owner        | uuid         | Sí       | Trabajador que reclama.               |
| claim_until        | timestamptz  | Sí       | Vencimiento de reclamación.           |
| claim_generation   | bigint       | No       | No negativa.                          |
| published_at       | timestamptz  | Sí       | Confirmación local de publicación.    |
| last_error_code    | varchar(80)  | Sí       | Error clasificado.                    |
| created_at         | timestamptz  | No       | Registro local.                       |
| lock_version       | bigint       | No       | No negativa.                          |

### 16.1. Restricciones

Los estados son:

PENDING, CLAIMED, PUBLISHED y REQUIRES_REVIEW.

CLAIMED exige propietario y vencimiento de reclamación.

Los demás estados no conservan reclamación activa.

PUBLISHED exige published_at.

Los estados no publicados mantienen published_at ausente.

PENDING exige next_attempt_at.

Los campos del evento no cambian durante reintentos.

### 16.2. Índices

| **Índice**                | **Columnas**                                              | **Condición**                |
|---------------------------|-----------------------------------------------------------|------------------------------|
| ix_outbox_event_pending   | next_attempt_at, created_at, event_id                     | publication_state = PENDING. |
| ix_outbox_event_claim     | claim_until, event_id                                     | publication_state = CLAIMED. |
| ix_outbox_event_aggregate | aggregate_type, aggregate_id, aggregate_version, event_id | Consulta por agregado.       |
| ix_outbox_event_action    | action_id                                                 | Valores presentes.           |

### 16.3. Publicación

El evento se registra con su efecto local.

El trabajador reclama mediante transacción breve y publica fuera de ella.

La actualización posterior comprueba propietario y generación.

Una publicación con respuesta perdida puede repetirse con el mismo event_id.

No se afirma entrega exactamente una vez.

## 17. Tabla común inbox_message

| **Columna**             | **Tipo**    | **Nulo** | **Regla**                                           |
|-------------------------|-------------|----------|-----------------------------------------------------|
| consumer_code           | varchar(80) | No       | Parte de PK; consumidor lógico estable.             |
| producer_service        | varchar(80) | No       | Parte de PK.                                        |
| event_id                | uuid        | No       | Parte de PK.                                        |
| topic                   | text        | No       | Tema recibido.                                      |
| partition_number        | integer     | No       | No negativo.                                        |
| first_offset            | bigint      | No       | No negativo.                                        |
| event_type              | varchar(80) | No       | Tipo recibido.                                      |
| schema_version          | bigint      | No       | Positiva.                                           |
| message_hash            | char(64)    | No       | Huella del mensaje validado.                        |
| event_payload           | jsonb       | No       | Contenido necesario para procesamiento recuperable. |
| received_at             | timestamptz | No       | Primera recepción.                                  |
| last_received_at        | timestamptz | No       | Última recepción equivalente.                       |
| state                   | varchar(30) | No       | Estado local.                                       |
| applied_at              | timestamptz | Sí       | Aplicación concluida.                               |
| action_id               | uuid        | Sí       | FK a actuación local.                               |
| local_result_references | jsonb       | No       | Efectos o intenciones locales.                      |
| error_code              | varchar(80) | Sí       | Incidencia clasificada.                             |
| lock_version            | bigint      | No       | No negativa.                                        |

La PK es (consumer_code, producer_service, event_id).

### 17.1. Estados

- RECEIVED.

- PENDING_REMOTE.

- APPLIED.

- REQUIRES_REVIEW.

APPLIED exige applied_at.

Los demás estados mantienen ese campo ausente.

La identidad repetida con distinta huella genera incidencia y no sustituye el contenido original.

### 17.2. Índices

- ix_inbox_message_pending: state, received_at, event_id, parcial para estados distintos de APPLIED.

- ix_inbox_message_action: action_id, parcial para valores presentes.

### 17.3. Aplicación

Cuando el efecto es local, inbox, efecto y outbox resultante se confirman conjuntamente.

Si requiere llamada remota, se registran intención y trabajo persistente y se conserva PENDING_REMOTE.

El avance del transporte ocurre después de confirmar la persistencia necesaria para recuperar el procesamiento.

Registrar recepción no demuestra ejecución remota.

## 18. Tabla común persistent_job

| **Columna**            | **Tipo**     | **Nulo** | **Regla**                             |
|------------------------|--------------|----------|---------------------------------------|
| job_id                 | uuid         | No       | PK.                                   |
| job_type               | varchar(80)  | No       | Tipo registrado.                      |
| deduplication_key      | varchar(120) | No       | Identidad de trabajo dentro del tipo. |
| owner_object_type      | varchar(80)  | Sí       | Tipo de objeto relacionado.           |
| owner_object_id        | uuid         | Sí       | Identidad relacionada.                |
| action_id              | uuid         | Sí       | FK a actuación.                       |
| payload_schema_code    | varchar(80)  | No       | Esquema del trabajo.                  |
| payload_schema_version | bigint       | No       | Positiva.                             |
| payload                | jsonb        | No       | Datos mínimos recuperables.           |
| state                  | varchar(30)  | No       | Estado.                               |
| scheduled_at           | timestamptz  | No       | Momento de ejecución elegible.        |
| attempt_count          | integer      | No       | No negativo.                          |
| claim_owner            | uuid         | Sí       | Trabajador.                           |
| claim_until            | timestamptz  | Sí       | Vencimiento de reclamación.           |
| claim_generation       | bigint       | No       | No negativa.                          |
| completed_at           | timestamptz  | Sí       | Finalización efectiva.                |
| last_error_code        | varchar(80)  | Sí       | Error clasificado.                    |
| created_at             | timestamptz  | No       | Creación.                             |
| updated_at             | timestamptz  | No       | Última actualización.                 |
| lock_version           | bigint       | No       | No negativa.                          |

### 18.1. Restricciones

Existe unicidad por (job_type, deduplication_key).

Tipo e identidad del objeto relacionado aparecen conjuntamente o están ambos ausentes.

Los estados son:

PENDING, CLAIMED, COMPLETED, CANCELLED y REQUIRES_REVIEW.

CLAIMED exige propietario y vencimiento.

Los demás estados no conservan reclamación activa.

COMPLETED exige completed_at.

Los otros estados mantienen ese campo ausente.

La clave de deduplicación distingue la ocurrencia cuando el mismo objeto necesita trabajos periódicos.

No se reutiliza la identidad de un trabajo completado para otra ocurrencia.

### 18.2. Índices

| **Índice**                | **Columnas**                              | **Condición**       |
|---------------------------|-------------------------------------------|---------------------|
| ix_persistent_job_pending | scheduled_at, job_id                      | state = PENDING.    |
| ix_persistent_job_claim   | claim_until, job_id                       | state = CLAIMED.    |
| ix_persistent_job_object  | owner_object_type, owner_object_id, state | Objeto presente.    |
| ix_persistent_job_action  | action_id                                 | Actuación presente. |

### 18.3. Reclamación

Los trabajadores seleccionan filas elegibles mediante bloqueo breve, omitiendo las reclamadas por otros.

Cada reclamación incrementa su generación.

Un trabajador anterior no puede confirmar resultados locales con una generación vencida.

La expiración permite recuperar el trabajo. No demuestra ausencia de ejecución externa.

El contrato determina si corresponde consultar, reintentar o escalar.

## 19. Transacciones y bloqueos

El aislamiento ordinario es READ COMMITTED.

Las capacidades, remanentes y exclusiones se protegen mediante bloqueos explícitos.

Las invariantes de conjunto utilizan una fila compartida de control.  
Si no pueden protegerse de esa forma, utilizan SERIALIZABLE.

Los reintentos de serialización o interbloqueo:

- Repiten únicamente la transacción local.

- Conservan identidad y contenido.

- No repiten llamadas externas dentro del reintento.

- Vuelven a comprobar versiones y decisiones.

Cada módulo define su orden de bloqueo, incluyendo las estructuras comunes que utiliza.

La tabla común no impone un orden que contradiga al agregado: un coordinador puede necesitar bloquear  
primero su propuesta o autorización y después el control del intento.

Confirmación y aborto respetan el mismo orden.

Los bloqueos tienen espera limitada. Un timeout no es cancelación de negocio.

## 20. Recursos y remanentes

Cada tabla de recursos define:

- Importe o cantidad inicial.

- Total autorizado vigente.

- Consumo.

- Liberación.

- Transformación o resolución interna.

- Remanente.

- Firmeza.

- Propietario.

- Operación y efecto cubiertos.

Las ecuaciones se declaran por recurso.  
No se aplica una fórmula genérica que confunda consumo externo con resolución interna.

Las condiciones sobre una fila utilizan CHECK.

Las sumas entre asignaciones utilizan el control del recurso y una transacción.

Los solapamientos identifican la misma porción para no descontarla dos veces.

Los recursos inciertos no se liberan por vencimiento del trabajador.

## 21. Secuencias y cortes

Las secuencias declaran su ámbito:

- Cuenta.

- Posición.

- Libro contable.

- Flujo.

- Otro agregado definido.

El incremento y el hecho se confirman conjuntamente.

La secuencia técnica de PostgreSQL no demuestra por sí sola orden de confirmación ni ausencia de huecos.

Los cortes conservan instante, versión, secuencia o conjunto explícito de hechos.

La paginación mantiene el mismo corte y filtros.

Un hecho incorporado después con fecha económica anterior no aparece silenciosamente en páginas sucesivas.

Una continuación que no puede reconstruir el corte no mezcla datos actuales:  
informa que esa continuación no es válida.

## 22. Archivos, caché y otros almacenamientos

### 22.1. MinIO

Documentos y comprobantes administra exclusivamente los archivos privados.

PostgreSQL conserva metadatos, versiones, tamaño, hash, integridad y referencia de almacenamiento.

La carga, verificación y confirmación son etapas recuperables.

Los demás servicios conservan referencias documentales.

### 22.2. Redis

Redis puede acelerar consultas.

No conserva la única copia de:

- Idempotencia.

- Decisiones.

- Posiciones.

- Recursos.

- Aprobaciones.

- Preparaciones.

- Preservaciones.

- Acumuladores.

La pérdida de caché no modifica el resultado de negocio.

### 22.3. Otros motores

No se incorpora otro almacenamiento autoritativo sin responsabilidad definida en la arquitectura.

Las proyecciones identifican fuente, reconstrucción y completitud.

## 23. Migraciones

Flyway administra el esquema de cada servicio.

Una migración aplicada es inmutable.

Las correcciones utilizan migraciones posteriores.

Los cambios incompatibles contemplan:

- Incorporación compatible.

- Adaptación y migración.

- Comprobación de datos.

- Cambio de uso.

- Retiro posterior.

No se elimina una columna mientras la necesiten versiones desplegadas o trabajos pendientes.

La incorporación de una restricción comprueba previamente los datos existentes.

JPA no modifica automáticamente el esquema de ejecución.

Los datos de ejemplo se separan de las migraciones estructurales.

## 24. Conservación y seguridad

El archivo conserva identidades, relaciones resolubles y reconocimiento de actuaciones antiguas.

La terminación comercial no borra contratos, movimientos, deuda o evidencias.

La disposición documental sigue las políticas de Documentos y comprobantes.

Los secretos no se almacenan en claro en tablas, eventos, resultados o logs.

Cuando un proceso requiere material secreto recuperable, utiliza almacenamiento protegido, clave externa,  
acceso limitado y eliminación cuando deja de ser necesario.

Ese material no integra las estructuras generales de historia ni eventos.

Los usuarios de ejecución no tienen permiso general para alterar historia o esquema.

Las consultas históricas y recuperaciones vuelven a comprobar autorización.

## 25. Respaldo y restauración

La restauración comienza con consumidores, trabajadores y despachos externos detenidos.

Antes de reanudar:

1.  Se comprueba integridad.

2.  Se verifica el esquema.

3.  Se reaplican controles de acceso.

4.  Se aplican disposiciones y preservaciones documentales.

5.  Se identifican actuaciones, intentos y recursos pendientes.

6.  Se reconcilian efectos externos posteriores al respaldo.

7.  Se recuperan outbox, inbox y trabajos con sus identidades originales.

8.  Se habilita la ejecución de forma controlada.

Una base restaurada no demuestra que un pago externo posterior nunca ocurrió.

Una outbox restaurada puede volver a publicar un evento ya entregado. Conserva el mismo event_id.

Las barreras y las identidades archivadas forman parte de la recuperación.

## 26. Contenido de los módulos

Cada tabla de negocio incluye:

| **Elemento**  | **Definición exigida**                         |
|---------------|------------------------------------------------|
| Propiedad     | Servicio y finalidad.                          |
| Columnas      | Nombre, tipo y significado.                    |
| Nulabilidad   | Presencia obligatoria, opcional o condicional. |
| PK            | Columnas exactas.                              |
| FK            | Columnas de origen y destino locales.          |
| Unicidad      | Columnas y condición cuando es parcial.        |
| Restricciones | Estados, rangos, variantes y ecuaciones.       |
| Índices       | Columnas, orden, condición y finalidad.        |
| Concurrencia  | Control y orden de bloqueo.                    |
| Transacción   | Cambios indivisibles.                          |
| Historia      | Versiones y correcciones.                      |
| Recuperación  | Identidades y resultados pendientes.           |
| Contratos     | Operaciones que utilizan la estructura.        |

Las extensiones de intentos y preparaciones utilizan FK a sus tablas comunes.

Las referencias polimórficas se identifican como tales y conservan su validación transaccional.  
No se presentan como FK inexistentes.

No quedan tablas definidas únicamente mediante “conserva antecedentes”, “resultados” o “componentes”  
sin su estructura física.

## 27. Validación documental e implementación

La revisión documental comprueba:

- Correspondencia con contratos y propietarios.

- Tipos y rangos.

- Nulabilidad.

- Claves y relaciones.

- Identidades económicas.

- Restricciones concurrentes.

- Transacciones indivisibles.

- Orden de bloqueo.

- Secuencias y cortes.

- Recuperación de incertidumbre.

- Índices de consultas.

- Conservación y restauración.

Durante la implementación se ejecutan pruebas con PostgreSQL real aislado para comprobar:

- Claves y restricciones.

- Comandos simultáneos.

- Confirmación y aborto.

- Aplicación y revocación.

- Consumo y liberación.

- Secuencias y cortes.

- Eventos duplicados.

- Trabajadores vencidos.

- Respuestas perdidas.

- Confirmaciones tardías.

- Restauración y recuperación.

La revisión documental no se presenta como ejecución de esas pruebas.
