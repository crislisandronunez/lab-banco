# Diseño técnico — Módulo 11. Riesgo y cumplimiento

## 1. Propósito y alcance

El módulo administra políticas, verificaciones, exposición y medidas que condicionan las relaciones y operaciones del
banco.

Comprende:

- Riesgos y límites compartidos.

- Conocimiento del cliente.

- Prevención de lavado.

- Prevención de fraude.

- Restricciones.

- Criterios tributarios.

- Información regulatoria simulada.

Una verificación favorable no admite automáticamente al cliente, concede financiación ni ejecuta una operación.

Una alerta no demuestra incumplimiento. Registrar una medida no demuestra su aplicación.

## 2. Servicios y responsabilidades

| Servicio                 | Identificador técnico          | Base de datos             | Responsabilidad                                                |
|--------------------------|--------------------------------|---------------------------|----------------------------------------------------------------|
| Riesgos                  | risk-service                   | risk_db                   | Políticas, exposición, límites, escenarios e incidentes.       |
| Conocimiento del cliente | customer-due-diligence-service | customer_due_diligence_db | Verificaciones, beneficiarios finales, listas y perfiles.      |
| Prevención de lavado     | aml-service                    | aml_db                    | Monitoreo, alertas, investigaciones y reportes especializados. |
| Prevención de fraude     | fraud-service                  | fraud_db                  | Controles de operaciones e investigaciones.                    |
| Restricciones            | restriction-service            | restriction_db            | Medidas, alcance, asignaciones y aplicación comprobada.        |
| Criterios tributarios    | tax-policy-service             | tax_policy_db             | Reglas, determinaciones y acumuladores compartidos.            |
| Información regulatoria  | regulatory-reporting-service   | regulatory_reporting_db   | Requerimientos, obligaciones y presentaciones simuladas.       |

Cada servicio administra PostgreSQL, usuario y migraciones propios.

No existen consultas SQL, relaciones JPA ni claves foráneas entre bases.

### 2.1. Autoridades relacionadas

| Información o decisión               | Responsable                          |
|--------------------------------------|--------------------------------------|
| Datos y relaciones declarados        | Personas.                            |
| Representación                       | Representación.                      |
| Admisión comercial                   | Vinculación.                         |
| Relación comercial                   | Clientes.                            |
| Decisión crediticia individual       | Evaluación crediticia.               |
| Deuda                                | Producto correspondiente.            |
| Disponibilidad monetaria             | Fondos.                              |
| Capacidad crediticia de tarjeta      | Cuenta de tarjeta.                   |
| Cálculo tributario de cada operación | Servicio que origina el hecho.       |
| Ejecución de restricciones           | Propietario del producto o posición. |
| Reparación económica                 | Responsable del efecto.              |
| Registración contable                | Registración contable.               |

Riesgos no mantiene otra deuda de los productos.

Restricciones no inmoviliza directamente fondos ni modifica por sí sola capacidades de productos.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato                 | Java        | PostgreSQL     | HTTP                 |
|----------------------|-------------|----------------|----------------------|
| Identificador        | UUID        | uuid           | Cadena UUID.         |
| Versión o secuencia  | long        | bigint         | Entero positivo.     |
| Instante             | Instant     | timestamptz    | ISO 8601 en UTC.     |
| Fecha                | LocalDate   | date           | YYYY-MM-DD.          |
| Importe              | BigDecimal  | numeric(24,8)  | Cadena decimal.      |
| Cantidad             | BigDecimal  | numeric(30,12) | Cadena decimal.      |
| Tasa o coeficiente   | BigDecimal  | numeric(24,12) | Cadena decimal.      |
| Moneda               | Enumeración | varchar(3)     | ARS o USD.           |
| Código               | String      | varchar(80)    | Cadena normalizada.  |
| Referencia externa   | String      | varchar(120)   | Cadena.              |
| Denominación         | String      | varchar(200)   | Cadena.              |
| Fundamento           | String      | varchar(4000)  | Cadena.              |
| Resumen de contenido | String      | char(64)       | SHA-256 hexadecimal. |

Los porcentajes se representan como proporciones.

Los cálculos conservan al menos doce decimales intermedios.

Los importes finales respetan la unidad mínima de la moneda. Las reglas tributarias identifican su redondeo.

La zona institucional es America/Argentina/Buenos_Aires.

### 3.2. Actuaciones y evidencias

Los comandos incluyen actuación, operación, objeto, finalidad, causa, versión esperada y evidencias.

Las evidencias identifican propietario, referencia, versión, momento, alcance y vencimiento cuando corresponda.

Los archivos permanecen en Documentos y comprobantes.

El actor y el servicio emisor proceden de la autenticación.

### 3.3. Resultados

Los resultados técnicos distinguen:

- PENDING.

- CONFIRMED.

- PARTIALLY_CONFIRMED.

- REJECTED.

- UNCERTAIN.

- CONFLICT.

El resultado funcional se informa separadamente.

La ausencia de información no equivale a exposición cero, exención, capacidad ilimitada o aprobación.

## 4. Políticas y reglas

Cada definición conserva:

- Finalidad.

- Fundamento.

- Alcance.

- Responsable.

- Datos obligatorios.

- Criterios.

- Parámetros.

- Consecuencias.

- Excepciones permitidas.

- Vigencia.

- Versión.

- Aprobación.

- Casos de validación.

Los estados son preparación, pendiente de aprobación, habilitada, suspendida y retirada.

Preparador y aprobador son personas distintas.

Los controles automáticos ejecutan reglas aprobadas y conservan sus entradas.

Las reglas utilizan operadores tipados. No ejecutan SQL o scripts recibidos como configuración.

Una modificación no altera silenciosamente resultados históricos.

Las revisiones sobre relaciones existentes generan actuaciones identificadas.

## 5. Riesgos: límites y exposición

### 5.1. Límite

| Campo                  | Contenido                                                     |
|------------------------|---------------------------------------------------------------|
| limitId                | Identidad estable.                                            |
| riskType               | Crédito, liquidez, mercado u operativo.                       |
| scopeType              | Persona, grupo, contraparte, producto u otro ámbito definido. |
| scopeReference         | Objeto alcanzado.                                             |
| measurementUnit        | Moneda, proporción o cantidad.                                |
| currency               | Obligatoria para límites monetarios.                          |
| horizon                | Horizonte.                                                    |
| authorizedValue        | Valor aprobado.                                               |
| consumptionMethodology | Criterio de afectación.                                       |
| conversionReference    | Conversión cuando se utiliza una moneda común.                |
| validFrom / validUntil | Vigencia.                                                     |
| version                | Versión de control.                                           |

La revisión general se realiza al menos anualmente y ante cambios relevantes.

La ausencia de un límite exigible impide emitir una aceptación definitiva.

### 5.2. Fuentes de exposición

Cada posición recibida identifica:

- Servicio propietario.

- Producto u operación.

- Persona y grupo.

- Moneda.

- Capital.

- Compromisos.

- Exposición contingente.

- Aprobación relacionada.

- Cobertura protegida.

- Fecha efectiva.

- Secuencia.

- Corte.

- Versión.

La exposición bruta y los efectos de garantías se presentan separadamente.

Los datos incompletos o desactualizados se identifican como limitación.

Un evento antiguo no reemplaza una posición posterior.

### 5.3. Grupos vinculados

La pertenencia requiere vínculos y criterios documentados.

Compartir domicilio o apellido no determina agrupación.

Cada pertenencia conserva fuente, fundamento y vigencia.

Los cambios identifican los límites y exposiciones afectados.

## 6. Compromisos y transformación de exposición

### 6.1. Afectación conjunta

El compromiso identifica:

- Propietario.

- Solicitud de origen.

- Operación o aprobación.

- Importe y moneda.

- Límites alcanzados.

- Consumo por límite.

- Metodología.

- Vigencia.

- Estado.

- Producto o efecto resultante.

Riesgos comprueba y afecta todos los límites necesarios en una transacción local.

Los controles se bloquean en orden estable.

Una solicitud que requiere límite de persona y de grupo no queda confirmada solo en uno.

### 6.2. Estados

- PREPARED.

- COMMITTED.

- TRANSFORMATION_PENDING.

- TRANSFORMED.

- RELEASE_PENDING.

- RELEASED.

- RECONCILIATION_REQUIRED.

La reserva de capacidad no demuestra que exista deuda ni dinero entregado.

### 6.3. Transformación

La transformación conserva la identidad de origen.

Se reemplaza el componente pendiente por la exposición efectiva correspondiente, sin mantener ambos como operaciones
independientes.

La variación por cada límite se calcula como:

$Variación = ConsumoResultante - ConsumoSustituido$

No se suma nuevamente el importe completo del producto.

Una utilización de línea modifica composición según metodología.

Un pago no libera necesariamente la exposición asociada a una línea vigente.

### 6.4. Cambio interno de moneda

El cambio interno identifica ambas monedas, importes y efectos de Fondos.

Riesgos conserva:

- Afectación previa.

- Intercambio.

- Resultado monetario.

- Variación de exposición cambiaria.

- Consumo resultante de límites.

- Fuentes y versiones utilizadas.

La transformación no presupone una salida de una cuenta externa del banco.

Si el intercambio ya modificó las obligaciones monetarias por moneda, su efecto se incorpora una sola vez, aunque
también llegue dentro de una posición agregada posterior.

La conciliación relaciona el intercambio con esa posición para evitar doble cómputo.

Una transformación confirmada puede extinguir la afectación preparatoria, pero no elimina el consumo de riesgo que
corresponda a la exposición efectiva.

### 6.5. Liberación y reducciones

La liberación exige evidencia de ausencia de utilización o de una causa válida de reducción.

Una constitución o ejecución incierta conserva la cobertura necesaria.

La reducción de un límite:

- No borra exposición.

- Registra exceso.

- Impide nuevas afectaciones incompatibles.

- Conserva compromisos anteriores.

- Genera seguimiento.

Las diferencias no liberan capacidad sin fundamento.

## 7. Evaluaciones, excepciones e incidentes

Cada evaluación conserva corte, posiciones, metodología, escenarios, indicadores y conclusiones.

Los escenarios no modifican contratos ni convierten estimaciones en pérdidas realizadas.

La clasificación de Riesgos no sustituye la determinación de mora del producto.

Las excepciones identifican límite, importe, fundamento, aprobador y vencimiento.

No existen excepciones indefinidas.

Una prohibición no se levanta mediante una excepción comercial.

Los incidentes conservan referencias comunes entre Fraude, Tesorería y Contabilidad.

Las recuperaciones se reconocen mediante resultados económicos comprobados.

## 8. Conocimiento del cliente

### 8.1. Expediente

El expediente conserva:

- Persona.

- Finalidad.

- Versiones de datos declarados.

- Fuentes.

- Documentos.

- Verificaciones.

- Observaciones.

- Determinaciones.

- Vigencia y revisión.

Los datos verificados no sobrescriben declaraciones de Personas.

### 8.2. Identidad

Los resultados son satisfactorio, insuficiente, contradictorio o pendiente.

La identidad jurídica y la representación se comprueban separadamente.

Verificar a un representante no concede un poder.

Una fuente indisponible mantiene pendiente el requisito afectado.

### 8.3. Beneficiarios finales

La determinación conserva estructura, cadenas, participaciones económicas, votos, control y evidencias.

Se utiliza el criterio funcional registrado, incluido el umbral del 10 % y el control por otros medios, con fundamento y
versión.

Las cadenas independientes se agregan sin repetir el mismo derecho.

Las estructuras circulares requieren resolución especializada.

La falta de datos no permite concluir que no existe beneficiario final.

### 8.4. Listas

Los resultados son:

- Sin coincidencia relevante.

- Coincidencia pendiente.

- Falso positivo comprobado.

- Coincidencia confirmada.

Se conservan lista, versión, fecha e identidad consultada.

La coincidencia de nombre no demuestra identidad.

La exposición política requiere diligencia reforzada, sin rechazo automático.

Una lista comercial no equivale a una orden de inmovilización.

### 8.5. Perfiles

| Perfil          | Valores y revisión                           |
|-----------------|----------------------------------------------|
| Cumplimiento    | Bajo, medio o alto.                          |
| Bajo y medio    | Revisión máxima de doce meses.               |
| Alto            | Revisión máxima de seis meses.               |
| Inversor        | Conservador, moderado o de mayor tolerancia. |
| Perfil inversor | Vigencia máxima de doce meses.               |

Los hechos relevantes anticipan revisión.

La adecuación identifica instrumento, versión, objetivos, horizonte, experiencia, liquidez y tolerancia a pérdidas.

No existe excepción comercial para una inversión incompatible.

Un resultado favorable de identidad no satisface automáticamente los demás controles.

## 9. Prevención de lavado

### 9.1. Escenarios

Cada escenario identifica patrón, entradas, ventana temporal, agrupación, parámetros y acciones.

Se contemplan fraccionamiento, desvíos de perfil y circulación sin finalidad aparente.

Una configuración incompleta no se activa.

### 9.2. Monitoreo

Cada hecho se incorpora una sola vez por su identidad económica.

Las ventanas utilizan fecha del hecho.

Las llegadas tardías y correcciones reevalúan las contribuciones afectadas y conservan el vínculo con alertas
anteriores.

Una entrada faltante genera incidencia de cobertura, no una conclusión de ausencia de riesgo.

### 9.3. Investigación

La investigación conserva alertas, operaciones, perfil, documentos, diligencias, responsable y próxima actuación.

Una operación inusual no se presenta automáticamente como sospechosa.

La solicitud de información al cliente no revela reportes reservados.

Las medidas preventivas se solicitan a Restricciones con fundamento y facultad.

### 9.4. Resolución y reporte

La conclusión requiere preparación y aprobación separadas.

El contenido especializado se entrega a Información regulatoria con requerimiento y fecha inicial de cómputo.

Si falta una configuración necesaria, se registra incidencia crítica y escalación.

El cierre de la relación comercial no extingue el caso ni sus obligaciones.

## 10. Prevención de fraude

La evaluación conserva operación, versión, contenido, participantes, importe, moneda, destino, canal, autenticación y
señales.

Las acciones son permitir, rechazar o requerir comprobación adicional.

Un cambio material exige reevaluación.

La falta de respuesta no autoriza nuevas salidas.

Si el mecanismo exige respuesta inmediata, se rechaza por imposibilidad de completar el control, sin afirmar fraude
confirmado.

Los hechos realizados se investigan sin repetirse.

Las investigaciones conservan indicios, evidencias, medidas, resolución y referencias de reparación.

Los bloqueos persistentes se administran mediante Restricciones.

Fraude no devuelve dinero directamente.

Las disputas y reclamos relacionados comparten referencias económicas para evitar restituciones duplicadas.

## 11. Restricciones: datos y estados

### 11.1. Medida

Cada medida contiene:

- Identidad.

- Autoridad.

- Fundamento.

- Evidencia.

- Personas.

- Productos, posiciones u operaciones.

- Acciones alcanzadas.

- Importe o cantidad.

- Moneda o instrumento.

- Recepción.

- Momento de efecto.

- Vencimiento expreso.

- Prioridad.

- Versión.

Una medida ambigua requiere aclaración. No se amplía arbitrariamente.

Prohibir débitos no implica prohibir créditos.

### 11.2. Dimensiones

| Dimensión     | Estados                                                 |
|---------------|---------------------------------------------------------|
| Determinación | En revisión, determinada o inconsistente.               |
| Vigencia      | Futura, efectiva o cesada.                              |
| Aplicación    | Pendiente, parcial, completa o con incidencia.          |
| Modificación  | Sin cambio, cambio pendiente o levantamiento pendiente. |

Una medida puede ser efectiva y tener aplicación parcial.

El estado informa esa diferencia. No retrasa ni oculta su momento de efecto.

### 11.3. Componentes

Cada componente identifica servicio ejecutor, objeto, acción, asignación, instrucción, versión, resultado y evidencia.

Una medida total de 100 no asigna 100 a cada cuenta.

Las asignaciones se controlan bajo bloqueo de la medida.

La insuficiencia se conserva sin utilizar descubierto.

## 12. Preparación de actos y cambios concurrentes

### 12.1. Consulta y preparación

La consulta de restricciones informa medidas y alcance. No produce una preparación.

La preparación es una actuación técnica persistente para un intento concreto, según el protocolo común de confirmación.

Contiene:

- restrictionPreparationId.

- Servicio coordinador.

- Referencia consultable del intento.

- Tipo de acto.

- Objetos.

- Actores cuando corresponda.

- Importe, moneda o cantidad.

- Resumen del contenido.

- Versión de control.

- Medidas consideradas.

- Condiciones temporales.

- Estado.

- Decisión final relacionada.

No se reutiliza para otro acto.

### 12.2. Control de concurrencia

Restricciones mantiene registros de control por ámbito alcanzado.

La preparación y la incorporación de cambios incompatibles se ordenan mediante esos controles.

Cuando el acto alcanza varios ámbitos, se bloquean en orden estable.

Si existe una medida incompatible, una modificación relevante pendiente de determinar o información necesaria no
comprobada, no se prepara favorablemente.

### 12.3. Versiones

Cada preparación conserva la versión del ámbito evaluado.

Los cambios incrementan esa versión y generan componentes identificados.

Una preparación anterior no se interpreta como permiso general frente a cambios posteriores.

El cambio debe resolver su orden respecto del intento asociado.

### 12.4. Cambio durante una preparación

Cuando una medida alcanza un intento preparado:

- Se registra inmediatamente la medida o modificación.

- Se conserva recepción y efecto.

- Se impiden nuevas preparaciones incompatibles.

- Se identifican los intentos abiertos alcanzados.

- Se solicita aborto a sus coordinadores.

- Se consulta la decisión durable.

- Se comunica a los ejecutores el tratamiento resultante.

- Se comprueba su aplicación.

La petición de aborto no equivale a aborto confirmado.

### 12.5. Resultados de coordinación

| Decisión del ejecutor   | Tratamiento                                                                        |
|-------------------------|------------------------------------------------------------------------------------|
| Intento abortado        | Se impide el efecto y se resuelven preparaciones y recursos correspondientes.      |
| Efecto confirmado       | Se conserva el hecho y se determina el tratamiento autorizado.                     |
| Sin decisión disponible | Se mantiene pendiente, se bloquean nuevos actos incompatibles y se escala.         |
| Referencia desconocida  | Se comprueba que no pueda recibirse o ejecutarse posteriormente antes de concluir. |

Los coordinadores son los propietarios del acto: Contratos para celebración, Fondos para efectos monetarios y
Autorizaciones para su decisión de uso.

Las entregas físicas y externas conservan el tratamiento de sus ejecutores.

### 12.6. Urgencia y efecto anterior

La atención urgente no espera un ciclo ordinario de procesamiento.

Se detienen las actuaciones todavía controlables y se consultan las que están en curso.

No se afirma que una operación realizada fue impedida.

Una medida con efecto anterior a su recepción identifica actuaciones potencialmente alcanzadas y exige resolución
competente.

El sistema conserva hechos, fechas y decisiones; no presume que una confirmación técnica elimina las consecuencias de
una medida retroactiva.

### 12.7. Recuperación

Las preparaciones no vencen liberándose automáticamente.

El límite temporal inicia resolución con el coordinador.

Si no se conoce la decisión, permanecen pendientes y se escalan.

La recuperación utiliza la misma referencia.

La resolución registra decisión, efectos, componentes y pendientes de la medida.

## 13. Aplicación y levantamiento

Los servicios operativos aplican la versión bajo sus propios controles locales.

Una consulta favorable no sustituye la aplicación exigible al ejecutar.

Restricciones informa aplicación completa únicamente después de confirmar:

- Componentes necesarios.

- Asignaciones.

- Tratamiento de intentos abiertos.

- Insuficiencias.

- Incidencias.

### 13.1. Reasignaciones

La reasignación conserva una identidad común y límites por componente.

Una preparación transitoria de destino no se presenta como otra afectación definitiva.

No se concluye hasta comprobar origen y destino.

### 13.2. Levantamiento

El levantamiento requiere autoridad y versión.

El vencimiento produce cese automático solo cuando esté expresamente establecido.

Las liberaciones se confirman por sus ejecutores.

Levantar una medida no elimina otras causas.

Liberar una inmovilización no genera otro crédito ni ingreso.

## 14. Criterios tributarios

### 14.1. Reglas y condiciones

Cada regla identifica tributo, jurisdicción, sujeto, hecho generador, fecha determinante, base, alícuota, mínimos,
topes, exenciones, acumulación, redondeo y vigencia.

Se distinguen reglas referenciadas y casos fiscales ficticios.

La bonificación comercial no constituye exención.

### 14.2. Determinación

Los resultados son gravado, exento, no alcanzado o pendiente.

Se conservan parámetros, versiones, fecha y alcance.

El producto calcula y conserva el importe económico.

Una determinación no cobra dinero.

### 14.3. Topes compartidos

La clave incluye sujeto, regla, período y ámbito.

Se mantienen acumulado confirmado, reservas, correcciones, tope y remanente.

Comprobar y reservar es una decisión local indivisible.

La confirmación transforma reserva en consumo sin duplicación.

Una ejecución incierta impide liberar.

Las reversiones restituyen capacidad solo dentro de lo permitido por la regla.

Los acumulados exclusivos de un producto permanecen en su propietario.

## 15. Información regulatoria

El requerimiento conserva destinatario simulado, fundamento, alcance, formato, fuentes, periodicidad o evento, inicio,
calendario, vencimiento y aprobaciones.

La obligación tiene identidad por requerimiento y período o evento.

La falta de configuración genera incidencia, no cumplimiento supuesto.

### 15.1. Preparación

Cada fuente aporta datos con corte, versión e integridad.

La información contable identifica cierre y emisión.

Los datos faltantes no se sustituyen por cero.

El contenido especializado conserva su aprobación de origen.

La presentación es inmutable después de aprobarse. Un cambio requiere otra versión y aprobación.

### 15.2. Presentación

Se distinguen preparada, aprobada, enviada, recibida técnicamente, aceptada, rechazada e incierta.

Solo se utilizan destinatarios simulados.

Una respuesta perdida se consulta antes de reenviar.

El rechazo no extingue la obligación.

Las rectificaciones conservan original, motivo, fuentes y aprobación.

La presentación tardía mantiene su fecha real.

## 16. Interfaces HTTP

Las interfaces utilizan JSON y OpenAPI. Los comandos incorporan contexto común.

### 16.1. Riesgos

| Contrato  | Interfaz                                     | Resultado                            |
|-----------|----------------------------------------------|--------------------------------------|
| CI-11-001 | POST /risk-policy-proposals                  | Política y límites propuestos.       |
| CI-11-001 | POST /risk-policy-proposals/{id}/actions     | Aprobación y aplicación.             |
| CI-11-002 | GET /risk-policies                           | Criterios vigentes o históricos.     |
| CI-11-003 | POST /risk-exposure-updates                  | Incorporación y conciliación.        |
| CI-11-004 | POST /risk-group-determinations              | Grupo y fundamento.                  |
| CI-11-005 | GET /risk-exposures                          | Exposición y capacidad.              |
| CI-11-006 | POST /risk-capacity-commitments              | Afectación conjunta.                 |
| CI-11-007 | POST /risk-capacity-commitments/{id}/actions | Transformación, ajuste o liberación. |
| CI-11-008 | POST /risk-assessments                       | Clasificación y escenarios.          |
| CI-11-009 | POST /risk-management-cases                  | Exceso, excepción o incidente.       |
| CI-11-010 | GET /risk-actions/{actionId}                 | Resultado original.                  |

La transformación cambiaria identifica intercambio, conjunto monetario y alcance sustituido.

### 16.2. Conocimiento del cliente

| Contrato  | Interfaz                                      | Resultado                    |
|-----------|-----------------------------------------------|------------------------------|
| CI-11-011 | POST /identity-verifications                  | Verificación y limitaciones. |
| CI-11-012 | POST /beneficial-ownership-determinations     | Personas y cadenas.          |
| CI-11-013 | POST /compliance-list-checks                  | Coincidencias.               |
| CI-11-013 | POST /compliance-list-checks/{id}/resolutions | Correspondencia resuelta.    |
| CI-11-014 | POST /customer-compliance-profiles            | Perfil y revisión.           |
| CI-11-015 | POST /investment-suitability-assessments      | Perfil y adecuación.         |
| CI-11-016 | GET /customer-due-diligence-results           | Resultado autorizado.        |

### 16.3. Lavado

| Contrato  | Interfaz                                  | Resultado                 |
|-----------|-------------------------------------------|---------------------------|
| CI-11-017 | POST /aml-scenario-proposals              | Escenario propuesto.      |
| CI-11-017 | POST /aml-scenario-proposals/{id}/actions | Versión habilitada.       |
| CI-11-018 | POST /aml-operation-facts                 | Cobertura y alertas.      |
| CI-11-019 | POST /aml-investigations                  | Investigación.            |
| CI-11-019 | POST /aml-investigations/{id}/antecedents | Ampliación.               |
| CI-11-020 | POST /aml-investigations/{id}/resolutions | Conclusión y reporte.     |
| CI-11-021 | GET /aml-investigations/{id}              | Expediente según alcance. |

### 16.4. Fraude

| Contrato  | Interfaz                                    | Resultado                       |
|-----------|---------------------------------------------|---------------------------------|
| CI-11-022 | POST /fraud-control-proposals               | Reglas propuestas.              |
| CI-11-022 | POST /fraud-control-proposals/{id}/actions  | Versión habilitada.             |
| CI-11-023 | POST /fraud-operation-assessments           | Control sobre contenido exacto. |
| CI-11-024 | POST /fraud-investigations                  | Caso.                           |
| CI-11-024 | POST /fraud-investigations/{id}/resolutions | Decisión y actuaciones.         |
| CI-11-025 | GET /fraud-assessments/{id}                 | Evaluación.                     |
| CI-11-025 | GET /fraud-investigations/{id}              | Investigación autorizada.       |

### 16.5. Restricciones

| Contrato  | Interfaz                                    | Resultado                       |
|-----------|---------------------------------------------|---------------------------------| 
| CI-11-026 | POST /restrictions                          | Medida y alcance.               |
| CI-11-027 | POST /restriction-applicability-checks      | Consulta de medidas aplicables. |
| CI-11-028 | POST /restrictions/{id}/application-actions | Componentes y asignaciones.     |
| CI-11-029 | POST /restriction-applications/{id}/results | Aplicación comprobada.          |
| CI-11-030 | POST /restrictions/{id}/changes             | Modificación o levantamiento.   |
| CI-11-031 | GET /restrictions/{id}                      | Historia y cumplimiento.        |
| CI-11-031 | GET /restriction-actions/{actionId}         | Actuación original.             |

El protocolo técnico de confirmación incorpora:

| Interfaz                                                          | Finalidad                                            |
|-------------------------------------------------------------------|------------------------------------------------------|
| POST /restriction-action-preparations                             | Preparar un acto concreto.                           |
| GET /restriction-action-preparations/{id}                         | Consultar preparación y coordinación.                |
| POST /restriction-action-preparations/{id}/decision-notifications | Incorporar una decisión verificable del coordinador. |

La consulta CI-11-027 permanece informativa.

La preparación es una actuación técnica adicional del protocolo común; no se oculta como efecto de una consulta.

### 16.6. Tributarios

| Contrato  | Interfaz                                   | Resultado                         |
|-----------|--------------------------------------------|-----------------------------------|
| CI-11-032 | POST /tax-rule-proposals                   | Regla propuesta.                  |
| CI-11-032 | POST /tax-rule-proposals/{id}/actions      | Versión habilitada.               |
| CI-11-032 | POST /verified-tax-conditions              | Condición fiscal.                 |
| CI-11-033 | POST /tax-treatment-determinations         | Tratamiento y parámetros.         |
| CI-11-034 | GET /tax-treatment-determinations/{id}     | Determinación histórica.          |
| CI-11-034 | GET /tax-rules                             | Reglas.                           |
| CI-11-035 | POST /shared-tax-affectations              | Reserva compartida.               |
| CI-11-036 | POST /shared-tax-affectations/{id}/actions | Consumo, liberación o corrección. |
| CI-11-037 | GET /shared-tax-accumulators               | Acumulado y remanente.            |

### 16.7. Regulatoria

| Contrato  | Interfaz                                           | Resultado                   |
|-----------|----------------------------------------------------|-----------------------------|
| CI-11-038 | POST /reporting-requirements                       | Requerimiento.              |
| CI-11-038 | POST /reporting-requirements/{id}/actions          | Versión habilitada.         |
| CI-11-039 | POST /reporting-obligations                        | Obligación y vencimiento.   |
| CI-11-040 | POST /reporting-obligations/{id}/antecedents       | Cobertura de fuentes.       |
| CI-11-041 | POST /reporting-obligations/{id}/preparations      | Presentación validada.      |
| CI-11-041 | POST /regulatory-submissions/{id}/approvals        | Aprobación exacta.          |
| CI-11-042 | POST /regulatory-submissions/{id}/dispatch-actions | Envío o consulta simulada.  |
| CI-11-043 | POST /regulatory-submissions/{id}/rectifications   | Nueva versión vinculada.    |
| CI-11-044 | GET /reporting-requirements/{id}                   | Definición.                 |
| CI-11-044 | GET /reporting-obligations/{id}                    | Cumplimiento.               |
| CI-11-044 | GET /regulatory-submissions/{id}                   | Presentación y constancias. |

Las modalidades tienen esquemas excluyentes.

Las respuestas usan 200, 201 y 202. Los errores utilizan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 17. Eventos

| Contrato  | Evento                                 |
|-----------|----------------------------------------|
| CI-11-045 | RiskPolicyOrExposureChanged            |
| CI-11-046 | CustomerDueDiligenceResultChanged      |
| CI-11-047 | AmlActionRecorded                      |
| CI-11-048 | FraudAssessmentOrResolutionChanged     |
| CI-11-049 | RestrictionOrApplicationChanged        |
| CI-11-050 | TaxTreatmentOrSharedAffectationChanged |
| CI-11-051 | ReportingObligationOrSubmissionChanged |

Los temas son:

- banking.risk.events.

- banking.customer-due-diligence.events.

- banking.aml.events.

- banking.fraud.events.

- banking.restriction.events.

- banking.tax-policy.events.

- banking.regulatory-reporting.events.

El sobre identifica evento, esquema, emisor, agregado, versión, operación, actuación, causa y fechas.

Los eventos reservados tienen permisos específicos.

No contienen expedientes completos ni sustituyen la consulta de decisiones durables.

## 18. Persistencia y recuperación

| Servicio      | Persistencia principal                                                   |
|---------------|--------------------------------------------------------------------------|
| Riesgos       | Políticas, límites, grupos, posiciones, afectaciones y transformaciones. |
| Conocimiento  | Expedientes, verificaciones, listas y perfiles.                          |
| Lavado        | Escenarios, contribuciones de hechos, alertas y casos.                   |
| Fraude        | Controles, evaluaciones, investigaciones y resoluciones.                 |
| Restricciones | Medidas, controles, preparaciones, componentes y confirmaciones.         |
| Tributarios   | Reglas, condiciones, determinaciones y acumuladores.                     |
| Regulatoria   | Requerimientos, obligaciones, fuentes y presentaciones.                  |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Se exige:

- Afectación única por referencia.

- Transformación sin duplicar exposición.

- Preparación única por intento, autoridad y alcance.

- Hecho monitoreado una sola vez.

- Aplicación identificada por medida, versión y objeto.

- Consumo tributario sin repetir reserva.

- Obligación informativa única por período o evento.

- Presentación aprobada inmutable.

Las transacciones confirman cambios, historial, idempotencia y outbox.

Las llamadas remotas se realizan fuera de ellas.

La recuperación utiliza referencias originales.

Un temporizador inicia consulta o escalación. No aprueba, libera ni extingue obligaciones.

## 19. Seguridad y observabilidad

Keycloak autentica actores y servicios.

El acceso combina función, objeto y finalidad.

Se separan resultado comunicable, motivo reservado, evidencias, investigación y contenido especializado.

Las notificaciones de decisión se aceptan únicamente del coordinador registrado o mediante una recuperación autorizada y
verificada.

Documentos aplica preservación y acceso según el alcance solicitado.

PostgreSQL conserva la autoridad de límites, medidas, perfiles y acumuladores.

Redis no decide admisión, aplicación o liberación.

Los logs excluyen evidencias completas, señales sensibles y contenido reservado.

Se observan especialmente:

- Fuentes incompletas.

- Revisiones vencidas.

- Compromisos inciertos.

- Transformaciones pendientes.

- Preparaciones sin decisión.

- Abortos sin confirmar.

- Medidas parcialmente aplicadas.

- Insuficiencias.

- Acumuladores pendientes.

- Presentaciones próximas a vencer.

## 20. Pruebas y correspondencia funcional

Las pruebas incluyen:

- Dos afectaciones compitiendo por capacidad.

- Transformación de aprobación a producto.

- Cambio interno confirmado y exposición pendiente.

- Evento individual seguido de posición agregada sin duplicación.

- Preparación anterior o posterior a una medida.

- Petición de aborto concurrente con confirmación.

- Coordinador indisponible.

- Medida con efecto anterior a recepción.

- Aplicación parcial entre varias cuentas.

- Levantamiento con otra causa vigente.

- Perfil vencido.

- Estructura societaria circular.

- Hecho tardío en monitoreo.

- Fraude sin respuesta.

- Topes tributarios concurrentes.

- Presentación con respuesta perdida.

- Acceso indebido a información reservada.

La concurrencia se verifica con PostgreSQL real y las interrupciones entre servicios mediante simuladores.

| Operación                          | Implementación                               | Contratos                  |
|------------------------------------|----------------------------------------------|----------------------------|
| OF-11-01. Políticas y límites      | Configuración y aprobación.                  | CI-11-001 y 002.           |
| OF-11-02. Exposición y grupos      | Fuentes y consolidación.                     | CI-11-003, 004 y 005.      |
| OF-11-03. Capacidad compartida     | Afectación y transformación.                 | CI-11-006 y 007.           |
| OF-11-04. Evaluación               | Clasificación y escenarios.                  | CI-11-008.                 |
| OF-11-05. Excesos e incidentes     | Excepciones y seguimiento.                   | CI-11-009, 010 y 045.      |
| OF-11-06. Identidad                | Verificación.                                | CI-11-011.                 |
| OF-11-07. Beneficiarios finales    | Cadenas y control.                           | CI-11-012.                 |
| OF-11-08. Listas                   | Coincidencias.                               | CI-11-013.                 |
| OF-11-09. Cumplimiento             | Perfil y revisión.                           | CI-11-014 y 016.           |
| OF-11-10. Adecuación               | Perfil e instrumento.                        | CI-11-015 y 046.           |
| OF-11-11. Escenarios               | Configuración y cobertura.                   | CI-11-017.                 |
| OF-11-12. Investigación de lavado  | Hechos y alertas.                            | CI-11-018 y 019.           |
| OF-11-13. Reporte especializado    | Conclusión y contenido.                      | CI-11-020, 021 y 047.      |
| OF-11-14. Controles de fraude      | Reglas y señales.                            | CI-11-022.                 |
| OF-11-15. Evaluación de fraude     | Control concreto.                            | CI-11-023.                 |
| OF-11-16. Investigación de fraude  | Resolución y reparación coordinada.          | CI-11-024, 025 y 048.      |
| OF-11-17. Medida                   | Autoridad y alcance.                         | CI-11-026 y 027.           |
| OF-11-18. Aplicación               | Preparaciones, componentes y confirmaciones. | CI-11-028 y 029.           |
| OF-11-19. Levantamiento            | Liberación comprobada.                       | CI-11-030, 031 y 049.      |
| OF-11-20. Reglas tributarias       | Condiciones y versiones.                     | CI-11-032 y 034.           |
| OF-11-21. Determinación tributaria | Tratamiento.                                 | CI-11-033.                 |
| OF-11-22. Topes compartidos        | Reserva y consumo.                           | CI-11-035, 036, 037 y 050. |
| OF-11-23. Requerimientos           | Definición y obligaciones.                   | CI-11-038 y 039.           |
| OF-11-24. Preparación              | Fuentes y aprobación.                        | CI-11-040 y 041.           |
| OF-11-25. Presentación             | Envío, consulta y rectificación.             | CI-11-042, 043, 044 y 051. |
