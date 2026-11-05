# **Contratos técnicos — Módulo 11. Riesgo y cumplimiento**

## **1. Propósito y alcance**

Este documento especifica las interfaces HTTP, estructuras de intercambio, eventos y condiciones de recuperación de:

| **Servicio**             | **Responsabilidad**                                                              |
|--------------------------|----------------------------------------------------------------------------------|
| Riesgos                  | Políticas, límites, exposición, compromisos, evaluaciones, excesos e incidentes. |
| Conocimiento del cliente | Verificaciones, beneficiarios finales, listas, perfiles y adecuación inversora.  |
| Prevención de lavado     | Escenarios, monitoreo, alertas e investigaciones reservadas.                     |
| Prevención de fraude     | Controles, evaluaciones e investigaciones de operaciones.                        |
| Restricciones            | Medidas, alcance, distribución, aplicación y preparación de actos.               |
| Criterios tributarios    | Reglas, condiciones fiscales y acumuladores compartidos.                         |
| Información regulatoria  | Requerimientos, obligaciones y presentaciones a destinatarios simulados.         |

Comprende los contratos **CI-11-001 a CI-11-051**.

Los servicios operativos conservan sus decisiones y efectos.

Un control favorable no constituye por sí mismo admisión, concesión crediticia, aceptación contractual ni ejecución
monetaria.

Una resolución de investigación no demuestra que las medidas o reparaciones solicitadas estén ejecutadas.

## **2. Convenciones comunes**

### **2.1. Transporte y seguimiento**

Las interfaces utilizan /v1, autenticación y X-Correlation-Id.

Los comandos persistentes requieren Idempotency-Key y ActionContext.

Las comprobaciones informativas mediante POST no crean preparaciones ni compromisos.

Cada servicio ofrece:

**GET /v1/actions/{actionId}**

Su identificador técnico es getActionStatus, único dentro del proveedor.

La consulta recupera resultados e identificadores generados aunque se pierda la respuesta inicial.

### **2.2. Tipos**

| **Tipo**          | **Representación**                                                     |
|-------------------|------------------------------------------------------------------------|
| UUID              | Identificador interno.                                                 |
| Version           | Entero positivo dentro del rango de long.                              |
| Sequence          | Entero positivo dentro del rango de long.                              |
| Code              | Cadena de 1 a 80 caracteres.                                           |
| Instant           | Instante UTC.                                                          |
| Date              | Fecha YYYY-MM-DD.                                                      |
| Hash              | SHA-256 hexadecimal de 64 caracteres.                                  |
| Currency          | ARS o USD.                                                             |
| MoneyAmount       | Cadena decimal no negativa, precisión máxima 24 y hasta dos decimales. |
| Measurement       | Cadena decimal con signo, precisión 30 y escala máxima 12.             |
| Ratio             | Cadena decimal, precisión 24 y escala máxima 12.                       |
| Reason            | Cadena de 1 a 4000 caracteres.                                         |
| ExternalReference | Cadena de 1 a 120 caracteres.                                          |

Las participaciones utilizan proporciones entre cero y uno.

Los importes ejecutables respetan centavos. Los cálculos conservan al menos doce decimales intermedios.

Las unidades, signos y redondeos se declaran por campo y metodología.

### **2.3. Obligatoriedad y referencias**

Los campos enumerados son obligatorios salvo indicación de campo opcional, condicional o generado.

Las colecciones pueden estar vacías excepto cuando se exige contenido.

Se utilizan las referencias, resultados, errores y canonicalización del documento principal.

Actor y servicio proceden de autenticación o delegación verificadas.

## **3. Estructuras compartidas**

### **3.1. ControlScope**

Contiene:

- subjectReferences: personas o participantes alcanzados.

- productReferences: productos alcanzados.

- objectReferences: objetos concretos.

- actionCodes: actos.

- combinationMode: ALL_CONDITIONS o ANY_CONDITION.

- currency: cuando corresponde.

- horizon: cuando lo exige la regla.

Una colección vacía no significa alcance universal. El esquema de cada control identifica las dimensiones exigidas.

Los alcances generales requieren una modalidad explícita y una política o fundamento que los autorice.

### **3.2. Evidence**

Contiene:

- sourceReference: ObjectReference.

- observedAt: Instant.

- documentReferences: DocumentReference\[\].

- verificationStatus: VERIFIED, DECLARED, OBSERVED o UNAVAILABLE.

- sourceVersion: cuando existe.

- factOccurredAt: cuando se conoce.

La procedencia declarada no se presenta como verificación independiente.

### **3.3. TypedMeasurement**

Utiliza modalidades excluyentes:

| **unit** | **Campos**                                         |
|----------|----------------------------------------------------|
| MONEY    | value: MoneyAmount y currency.                     |
| RATIO    | value: Ratio.                                      |
| QUANTITY | value: Measurement no negativa y quantityUnitCode. |

Las variaciones utilizan una estructura equivalente con valor firmado.

Una cantidad de instrumentos agrega su referencia y clase.

### **3.4. PolicyDefinition**

Contiene:

- Código, nombre y finalidad.

- Fundamento.

- Alcance.

- Definiciones de entradas.

- Metodología versionada.

- Parámetros tipados.

- Consecuencias.

- Excepciones permitidas.

- Vigencia.

- Regla de revisión.

Cada parámetro identifica nombre, tipo, unidad y valor.

Los valores son variantes de texto, entero, decimal, fecha, booleano, código o referencia. Cada mecanismo define los
nombres y tipos admitidos.

No se ejecutan scripts ni SQL recibidos como configuración.

### **3.5. Propuestas y resultados**

Las propuestas de riesgo, lavado, fraude y tributos utilizan creación y acciones versionadas.

Las acciones son SUBMIT, APPROVE, ENABLE, SUSPEND y WITHDRAW, según capacidad.

La aprobación vincula versión y contentHash. Un cambio material exige nueva propuesta o revisión aprobada.

Los comandos utilizan el resultado común. Agregan vigencia y observaciones autorizadas cuando corresponde.

Una respuesta pendiente no se utiliza como control favorable.

## **4. Servicio Riesgos**

### **4.1. RiskLimit**

Contiene:

- limitId y version.

- riskType: CREDIT, LIQUIDITY, MARKET u OPERATIONAL.

- scopeType: PERSON, GROUP, COUNTERPARTY, PRODUCT o ámbito registrado.

- scopeReference.

- authorizedMeasurement: TypedMeasurement.

- horizon: código de horizonte registrado.

- Metodología de consumo.

- Conversión versionada, cuando se consolida moneda.

- Vigencia.

- Estado.

Los estados son DRAFT, APPROVED, ACTIVE, SUSPENDED y WITHDRAWN.

### **4.2. Interfaces**

| **Contrato** | **operationId**             | **Método y ruta**                                         |
|--------------|-----------------------------|-----------------------------------------------------------|
| CI-11-001    | createRiskPolicyProposal    | POST /v1/risk-policy-proposals                            |
| CI-11-001    | actOnRiskPolicyProposal     | POST /v1/risk-policy-proposals/{proposalId}/actions       |
| CI-11-002    | queryRiskPolicies           | GET /v1/risk-policies                                     |
| CI-11-003    | updateRiskExposure          | POST /v1/risk-exposure-updates                            |
| CI-11-004    | determineRiskGroup          | POST /v1/risk-group-determinations                        |
| CI-11-005    | queryRiskExposures          | GET /v1/risk-exposures                                    |
| CI-11-006    | commitRiskCapacity          | POST /v1/risk-capacity-commitments                        |
| CI-11-007    | actOnRiskCapacityCommitment | POST /v1/risk-capacity-commitments/{commitmentId}/actions |
| CI-11-008    | assessRisk                  | POST /v1/risk-assessments                                 |
| CI-11-009    | createRiskManagementCase    | POST /v1/risk-management-cases                            |
| CI-11-009    | actOnRiskManagementCase     | POST /v1/risk-management-cases/{caseId}/actions           |
| CI-11-010    | getRiskAction               | GET /v1/risk-actions/{actionId}                           |

La revisión general de políticas es al menos anual y ante cambios relevantes.

### **4.3. Exposición y grupos**

Actualizar exposición recibe contexto, fuente, objeto de origen, sujetos, corte y versión o secuencia autoritativa.

Cada componente identifica:

- componentId.

- exposureType.

- Medición.

- Referencia económica de origen.

- Componente sustituido, cuando corresponde.

Agrega coberturas y transformaciones relacionadas.

La respuesta identifica posición de exposición, versión, composición, diferencias y limitaciones.

Un agregado posterior reconoce componentes ya incorporados. No suma nuevamente una transformación registrada.

Determinar grupo recibe integrantes, relaciones, evidencia de control, criterio versionado, vigencia y aprobación.

Compartir apellido o domicilio no basta para agrupar.

La consulta distingue exposición efectiva, compromisos, contingencias, coberturas, consumo, capacidad, excesos y
faltantes.

Consultar no reserva capacidad.

### **4.4. RiskCapacityCommitment**

Contiene:

- Identidad y versión.

- Propietario.

- Operación.

- Ámbitos afectados.

- Afectaciones por límite.

- Metodología.

- Vigencia.

- Estado.

- Referencias de efectos resultantes.

- Aprobación relacionada, cuando existe.

Cada afectación identifica límite, versión y TypedMeasurement de consumo.

Los estados son PREPARED, COMMITTED, TRANSFORMATION_PENDING, TRANSFORMED, RELEASE_PENDING, RELEASED y
RECONCILIATION_REQUIRED.

Estos estados describen el compromiso de riesgo; no sustituyen los estados del protocolo común de preparación de
autoridades.

Comprometer recibe sujetos, producto, mediciones, horizonte, etapa y condiciones de conclusión.

Riesgos determina todos los límites aplicables y afecta conjuntamente persona, grupo y demás ámbitos exigibles.

### **4.5. Transformación y liberación**

Las acciones son:

- TRANSFORM: componente sustituido, resultado efectivo y evidencias.

- ADJUST: nueva composición, variación y fundamento.

- RELEASE_CANCELLED: cancelación comprobada sin ejecución pendiente.

La variación de consumo es el resultado menos el componente sustituido.

Una aprobación de 500 transformada en financiación de 500 mantiene consumo 500, sin sumar 1000 ni abrir capacidad libre
entre etapas.

Para cambio interno se exigen intercambio, conjunto de Fondos, resultado confirmado, ambas monedas e importes y
metodología de exposición.

No se presume una salida externa ni se libera capacidad como si el cambio no hubiera ocurrido.

### **4.6. Evaluaciones y casos**

Evaluar recibe cortes, antecedentes, metodología y escenarios.

Devuelve indicadores, clasificación, concentración, vulnerabilidad, criterios de previsión y acciones.

Cada indicador identifica código, unidad, valor y fuentes. Cada clasificación conserva metodología y versión.

Una simulación adversa no reconoce una pérdida realizada.

Los casos son EXCESS, EXCEPTION o INCIDENT.

Las excepciones exigen alcance, máximo, vencimiento y aprobación. No se admiten excepciones indefinidas ni sobre
prohibiciones no exceptuables.

Las acciones de caso son ADD_EVIDENCE, RECORD_TREATMENT, ESCALATE y CLOSE.

Cerrar exige evidencia del tratamiento; no elimina la exposición original.

## **5. Servicio Conocimiento del cliente**

### **5.1. Interfaces**

| **Contrato** | **operationId**                    | **Método y ruta**                                     |
|--------------|------------------------------------|-------------------------------------------------------|
| CI-11-011    | verifyIdentity                     | POST /v1/identity-verifications                       |
| CI-11-012    | determineBeneficialOwnership       | POST /v1/beneficial-ownership-determinations          |
| CI-11-013    | checkComplianceLists               | POST /v1/compliance-list-checks                       |
| CI-11-013    | resolveComplianceListMatch         | POST /v1/compliance-list-checks/{checkId}/resolutions |
| CI-11-014    | establishCustomerComplianceProfile | POST /v1/customer-compliance-profiles                 |
| CI-11-015    | assessInvestmentSuitability        | POST /v1/investment-suitability-assessments           |
| CI-11-016    | queryCustomerDueDiligenceResults   | GET /v1/customer-due-diligence-results                |

### **5.2. Identidad**

Recibe persona versionada, finalidad, datos declarados, documentos y fuentes requeridas.

Devuelve:

- verificationId.

- result: SATISFACTORY, INSUFFICIENT, CONTRADICTORY o PENDING.

- Atributos comprobados.

- Fuentes.

- Discrepancias.

- Requisitos de revisión.

- Fecha de finalización, cuando existe.

- Vigencia, cuando corresponde.

Cada atributo identifica código, resultado y evidencia. No distribuye valores personales innecesarios.

La verificación no modifica silenciosamente Personas ni concede representación.

### **5.3. Beneficiarios finales y control**

Recibe entidad jurídica, estructuras de propiedad y voto, evidencia de control y criterio versionado.

Devuelve determinación, personas identificadas, cadenas, hallazgos y faltantes.

Cada persona determinada conserva participación económica o de voto cuando puede calcularse, modalidad de control, fecha
y evidencia.

Se aplica el umbral del 10 % adoptado por el modelo y el control por otros medios.

Las cadenas circulares o incompletas permanecen bajo resolución. No se inventa un beneficiario para completar el
resultado.

### **5.4. Listas**

La comprobación recibe personas, relaciones, finalidad, momento y listas versionadas.

Los resultados son NO_RELEVANT_MATCH, PENDING_MATCH, FALSE_POSITIVE y CONFIRMED_MATCH.

Resolver coincidencia recibe contexto, versión, coincidencia, conclusión y evidencia.

Una coincidencia de nombre no confirma identidad ni ejecuta una restricción.

### **5.5. Perfiles y adecuación**

El perfil de cumplimiento recibe identidad verificada, actividad, finalidad, productos, canales, ámbitos geográficos y
antecedentes.

Devuelve perfil versionado, riesgo LOW, MEDIUM o HIGH, actividad esperada, diligencias, fundamentos y próxima revisión.

La actividad esperada utiliza indicadores tipados de volumen, frecuencia, productos y canales. Su metodología define
unidades y períodos.

La revisión máxima es de doce meses para riesgo bajo y medio y seis para alto.

La evaluación inversora utiliza:

- PROFILE: objetivos, horizonte, experiencia, conocimientos, liquidez y tolerancia a pérdidas.

- ORDER_SUITABILITY: perfil vigente, instrumento y características de la operación.

Los perfiles son CONSERVATIVE, MODERATE y HIGHER_TOLERANCE.

La adecuación es SUITABLE, UNSUITABLE o PENDING. La vigencia máxima del perfil es doce meses.

Un instrumento incompatible no se habilita por excepción comercial.

### **5.6. Consulta**

Recibe persona, tipo de control, finalidad y momento.

Devuelve resultado, alcance, vigencia, limitaciones y referencias autorizadas.

Identidad, listas, beneficiarios finales, cumplimiento y adecuación son controles diferentes.

## **6. Servicio Prevención de lavado**

### **6.1. Escenario**

Contiene:

- Código y versión.

- Finalidad.

- Entradas tipadas.

- Claves de agregación.

- Ventana temporal.

- Umbrales.

- Condiciones.

- Acciones resultantes.

- Ejemplos.

- Vigencia.

La ventana define tamaño, unidad, anclaje y tratamiento de hechos tardíos.

Los mecanismos y operadores están registrados. No admiten expresiones ejecutables libres.

### **6.2. Interfaces**

| **Contrato** | **operationId**                | **Método y ruta**                                         |
|--------------|--------------------------------|-----------------------------------------------------------|
| CI-11-017    | createAmlScenarioProposal      | POST /v1/aml-scenario-proposals                           |
| CI-11-017    | actOnAmlScenarioProposal       | POST /v1/aml-scenario-proposals/{proposalId}/actions      |
| CI-11-018    | incorporateAmlOperationFact    | POST /v1/aml-operation-facts                              |
| CI-11-019    | createAmlInvestigation         | POST /v1/aml-investigations                               |
| CI-11-019    | addAmlInvestigationAntecedents | POST /v1/aml-investigations/{investigationId}/antecedents |
| CI-11-020    | resolveAmlInvestigation        | POST /v1/aml-investigations/{investigationId}/resolutions |
| CI-11-021    | getAmlInvestigation            | GET /v1/aml-investigations/{investigationId}              |

### **6.3. Monitoreo**

Incorporar recibe contexto, hecho, fuente, operación, participantes, importe, moneda, fecha del hecho, fecha económica,
canal y finalidad.

Una corrección identifica el hecho original y su efecto sustitutivo.

Deduplica por propietario y hecho. Conserva contribuciones a ventanas.

Los hechos tardíos y correcciones revisan las ventanas afectadas sin multiplicar volumen.

Una alerta no ejecuta inmovilización ni confirma ilicitud.

### **6.4. Investigación y resolución**

Crear recibe alertas, operaciones, perfiles y evidencia.

Agregar antecedentes conserva historial, responsable y próxima actuación.

Los estados son OPEN, UNDER_INVESTIGATION, AWAITING_INFORMATION, READY_FOR_DECISION y RESOLVED.

Resolver recibe conclusión, hallazgos, evidencia y aprobación.

Las conclusiones son:

- CLOSE_WITHOUT_REPORT.

- PREPARE_SPECIALIZED_REPORT.

La segunda exige requerimiento de presentación y contenido especializado versionado.

Si falta configuración, conserva incidencia crítica y seguimiento. No informa reporte presentado.

La consulta requiere permiso especializado. No revela al participante la existencia de reportes reservados.

## **7. Servicio Prevención de fraude**

### **7.1. Interfaces**

| **Contrato** | **operationId**            | **Método y ruta**                                           |
|--------------|----------------------------|-------------------------------------------------------------|
| CI-11-022    | createFraudControlProposal | POST /v1/fraud-control-proposals                            |
| CI-11-022    | actOnFraudControlProposal  | POST /v1/fraud-control-proposals/{proposalId}/actions       |
| CI-11-023    | assessFraudOperation       | POST /v1/fraud-operation-assessments                        |
| CI-11-024    | createFraudInvestigation   | POST /v1/fraud-investigations                               |
| CI-11-024    | resolveFraudInvestigation  | POST /v1/fraud-investigations/{investigationId}/resolutions |
| CI-11-025    | getFraudAssessment         | GET /v1/fraud-assessments/{assessmentId}                    |
| CI-11-025    | getFraudInvestigation      | GET /v1/fraud-investigations/{investigationId}              |

### **7.2. Controles y evaluación**

El control propuesto contiene señales tipadas, condiciones, acciones, motivos comunicables, antecedentes reservados y
ejemplos.

Los resultados de negocio son ALLOW, DECLINE y REQUIRE_ADDITIONAL_VERIFICATION.

Evaluar recibe operación y versión, huella, participantes, importe y moneda cuando corresponden, destino, canal,
autenticación, señales y finalidad.

Las señales pertenecen al esquema registrado del tipo de operación.

Devuelve evaluación, resultado, huella evaluada, versiones de controles, vigencia, requisitos y motivo comunicable.

Si no puede concluir, devuelve PENDING. La indisponibilidad nunca se convierte en ALLOW.

Los bloqueos persistentes se coordinan con Restricciones.

### **7.3. Investigación**

Crear recibe operaciones, indicios, evidencia y disputas relacionadas.

Resolver recibe conclusión, pérdidas comprobadas, actuaciones requeridas y aprobación.

Las conclusiones son FRAUD_CONFIRMED, FRAUD_NOT_CONFIRMED e INCONCLUSIVE.

Cada actuación identifica propietario, objeto, tipo y evidencia necesaria.

La respuesta separa decisión, medidas, reparaciones y recuperaciones.

Fraude no ejecuta devoluciones directamente. Una reparación se solicita al propietario y conserva la identidad económica
compartida con disputas o reclamos.

## **8. Servicio Restricciones**

### **8.1. Restriction**

Contiene:

- restrictionId y version.

- Autoridad.

- Fundamento.

- Evidencias.

- Sujetos.

- Objetos.

- Actos restringidos.

- Modalidad de alcance.

- Prioridad fundada.

- Recepción.

- Inicio y final opcional.

- Estado temporal.

- Estado de aplicación.

- Ambigüedades pendientes.

Las modalidades son:

- ACTS: actos y objetos.

- MONEY: importe total y moneda.

- UNITS: cantidad e instrumento.

Los estados temporales son FUTURE, EFFECTIVE y CEASED.

Los de aplicación son PENDING, PARTIAL y COMPLETE.

Registro, vigencia y aplicación son dimensiones diferentes.

### **8.2. Interfaces**

| **Contrato** | **operationId**                    | **Método y ruta**                                         |
|--------------|------------------------------------|-----------------------------------------------------------|
| CI-11-026    | registerRestriction                | POST /v1/restrictions                                     |
| CI-11-027    | checkRestrictionApplicability      | POST /v1/restriction-applicability-checks                 |
| CI-11-028    | actOnRestrictionApplication        | POST /v1/restrictions/{restrictionId}/application-actions |
| CI-11-029    | recordRestrictionApplicationResult | POST /v1/restriction-applications/{applicationId}/results |
| CI-11-030    | changeRestriction                  | POST /v1/restrictions/{restrictionId}/changes             |
| CI-11-031    | getRestriction                     | GET /v1/restrictions/{restrictionId}                      |
| CI-11-031    | getRestrictionAction               | GET /v1/restriction-actions/{actionId}                    |

### **8.3. Registro y comprobación**

Registrar recibe contexto y definición.

Las medidas urgentes conservan recepción y tratamiento inmediato dentro de su fundamento.

No amplía arbitrariamente personas, monedas o patrimonio.

La comprobación recibe sujetos, objetos, acto, momento, finalidad e importe y moneda cuando corresponden.

Devuelve medidas aplicables, consecuencias, prioridades y ámbitos sin resolver.

No crea preparación.

### **8.4. Aplicación distribuida**

Las acciones son INITIALIZE, REALLOCATE y RECONCILE.

Cada asignación identifica:

- applicationComponentId.

- Propietario responsable.

- Objeto.

- Actos.

- Importe o cantidad, según modalidad.

- Prioridad.

Una medida monetaria total de 100 no se transforma en una orden de inmovilizar 100 en cada cuenta.

La suma asignada, su cobertura transitoria y las confirmaciones conservan relación con el total de la medida.

Los resultados de componente son APPLIED, PARTIALLY_APPLIED, NOT_APPLIED y UNCERTAIN.

Cada confirmación incorpora alcance, importe o cantidad, fecha y evidencia del propietario.

La insuficiencia permanece identificada.

### **8.5. Modificación y levantamiento**

Las acciones son MODIFY y LIFT.

Exigen autoridad, fundamento y alcance propuesto o momento de cese.

Las liberaciones se confirman por componente.

Levantar una medida conserva las restantes y no informa liberación integral mientras haya componentes inciertos.

## **9. Preparación frente a restricciones**

| **operationId**           | **Método y ruta**                                                               |
|---------------------------|---------------------------------------------------------------------------------|
| prepareRestrictedAction   | POST /v1/restriction-action-preparations                                        |
| getRestrictionPreparation | GET /v1/restriction-action-preparations/{preparationId}                         |
| notifyRestrictionDecision | POST /v1/restriction-action-preparations/{preparationId}/decision-notifications |

La solicitud utiliza la preparación común y agrega acto, sujetos, objetos, actores pertinentes y mediciones del alcance.

La respuesta conserva preparación, intento, huella, alcance, controles, medidas consideradas, límite de decisión, estado
y consulta.

Los estados son PREPARED, RESOLUTION_PENDING, CONCLUDED_COMMITTED, CONCLUDED_ABORTED y REJECTED.

Una ambigüedad que impide descartar incompatibilidad no produce preparación favorable.

Una medida incompatible posterior bloquea nuevas preparaciones y consulta o solicita resolver los intentos existentes.

No invalida silenciosamente una preparación ni declara aborto por vencimiento.

La indisponibilidad del coordinador mantiene seguimiento.

## **10. Servicio Criterios tributarios**

### **10.1. TaxRule**

Contiene:

- Identidad y versión.

- Tributo y jurisdicción.

- Alcance.

- Hecho generador.

- Regla de fecha determinante.

- Definición de base y tasa.

- Mínimo y máximo cuando corresponden.

- Exenciones.

- Redondeo.

- Acumulador compartido cuando existe.

- Vigencia.

- Fundamento.

Las reglas distinguen parámetros adoptados por el modelo de referencias externas.

Los mecanismos son registrados y versionados. No ejecutan fórmulas arbitrarias.

### **10.2. Interfaces**

| **Contrato** | **operationId**              | **Método y ruta**                                        |
|--------------|------------------------------|----------------------------------------------------------|
| CI-11-032    | createTaxRuleProposal        | POST /v1/tax-rule-proposals                              |
| CI-11-032    | actOnTaxRuleProposal         | POST /v1/tax-rule-proposals/{proposalId}/actions         |
| CI-11-032    | recordVerifiedTaxCondition   | POST /v1/verified-tax-conditions                         |
| CI-11-033    | determineTaxTreatment        | POST /v1/tax-treatment-determinations                    |
| CI-11-034    | getTaxTreatmentDetermination | GET /v1/tax-treatment-determinations/{determinationId}   |
| CI-11-034    | queryTaxRules                | GET /v1/tax-rules                                        |
| CI-11-035    | reserveSharedTaxAffectation  | POST /v1/shared-tax-affectations                         |
| CI-11-036    | actOnSharedTaxAffectation    | POST /v1/shared-tax-affectations/{affectationId}/actions |
| CI-11-037    | querySharedTaxAccumulators   | GET /v1/shared-tax-accumulators                          |

### **10.3. Condiciones y determinación**

Registrar condición fiscal recibe sujeto, condición, alcance, vigencia, verificación y evidencia.

Una exención identifica concepto, vigencia y fundamento.

Determinar recibe contexto, sujeto, operación, conceptos económicos, jurisdicción, fechas determinantes, moneda y
condiciones verificadas.

Devuelve:

- Identidad de determinación.

- Resultado TAXABLE, EXEMPT, OUT_OF_SCOPE o PENDING.

- Reglas versionadas.

- Parámetros de base y tasa.

- Redondeo.

- Exenciones.

- Afectaciones compartidas exigidas.

El producto calcula y conserva el importe de su operación utilizando esa determinación.

La determinación no cobra. La ausencia de regla no equivale a exención.

### **10.4. Acumuladores compartidos**

Reservar recibe sujeto, regla, período, operación, base relevante y afectación solicitada.

Comprueba y afecta el acumulador conjuntamente.

Devuelve afectación, versión, importe admitido, remanente y parámetros.

Las acciones son:

- CONFIRM_EVENT: hecho generador e importes definitivos.

- RELEASE_CANCELLED: cancelación comprobada.

- CORRECT: referencia original, variación y fundamento.

La reserva confirmada se transforma en consumo sin contarse dos veces.

La incertidumbre no permite liberar. Cambiar período no borra afectaciones anteriores.

La consulta devuelve confirmado, reservado, corregido, tope y remanente al corte.

## **11. Servicio Información regulatoria**

### **11.1. ReportingRequirement**

Contiene:

- Identidad y versión.

- Destinatario simulado.

- Fundamento.

- Alcance.

- Formato versionado.

- Fuentes requeridas.

- Disparador.

- Vencimiento.

- Calendario.

- Validaciones.

- Aprobaciones.

- Rectificaciones.

- Vigencia.

El disparador es:

- PERIODIC: período y calendario.

- EVENT_DRIVEN: hecho y condición de ocurrencia.

El vencimiento utiliza fecha fija o desplazamiento desde un hecho identificado, con unidad y ajuste.

No se habilita un requerimiento sin plazo interpretable.

### **11.2. Interfaces**

| **Contrato** | **operationId**                   | **Método y ruta**                                               |
|--------------|-----------------------------------|-----------------------------------------------------------------|
| CI-11-038    | createReportingRequirement        | POST /v1/reporting-requirements                                 |
| CI-11-038    | actOnReportingRequirement         | POST /v1/reporting-requirements/{requirementId}/actions         |
| CI-11-039    | createReportingObligation         | POST /v1/reporting-obligations                                  |
| CI-11-040    | addReportingAntecedents           | POST /v1/reporting-obligations/{obligationId}/antecedents       |
| CI-11-041    | prepareRegulatorySubmission       | POST /v1/reporting-obligations/{obligationId}/preparations      |
| CI-11-041    | approveRegulatorySubmission       | POST /v1/regulatory-submissions/{submissionId}/approvals        |
| CI-11-042    | actOnRegulatorySubmissionDispatch | POST /v1/regulatory-submissions/{submissionId}/dispatch-actions |
| CI-11-043    | rectifyRegulatorySubmission       | POST /v1/regulatory-submissions/{submissionId}/rectifications   |
| CI-11-044    | getReportingRequirement           | GET /v1/reporting-requirements/{requirementId}                  |
| CI-11-044    | getReportingObligation            | GET /v1/reporting-obligations/{obligationId}                    |
| CI-11-044    | getRegulatorySubmission           | GET /v1/regulatory-submissions/{submissionId}                   |

Las acciones del requerimiento son SUBMIT, APPROVE, ENABLE y WITHDRAW.

### **11.3. Obligaciones y fuentes**

Crear obligación recibe requerimiento versionado, período o hecho disparador de forma excluyente, fecha de ocurrencia y
responsable.

La identidad de ocurrencia impide duplicados.

Devuelve obligación, vencimiento, prioridad, fuentes y pendientes.

Si falta configuración, conserva incidencia sin inventar cumplimiento.

Incorporar fuentes recibe emisor, conjunto de datos, versión, corte, huella, completitud y faltantes.

La aprobación especializada se agrega cuando corresponde.

Se comprueba competencia de la fuente. Los datos ausentes no se sustituyen por cero.

### **11.4. RegulatorySubmission**

Contiene:

- Identidad y versión.

- Obligación.

- Formato.

- Conjuntos de datos.

- Corte.

- Huella.

- Validaciones.

- Aprobaciones especializadas.

- Aprobación de emisión.

- Documento, cuando existe.

- Referencias de despacho.

- Estado.

Los estados son PREPARING, VALIDATION_FAILED, READY_FOR_APPROVAL, APPROVED, DISPATCH_PENDING, SENT,
TECHNICALLY_RECEIVED, ACCEPTED, REJECTED y UNCERTAIN.

Preparar recibe fuentes, formato y corte solicitado.

Aprobar recibe versión, huella y aprobación correspondiente.

El contenido aprobado es inmutable. Cambiarlo exige otra versión.

Una parcialidad solo se admite si el mecanismo la contempla y conserva complemento pendiente.

### **11.5. Despacho y rectificación**

Las acciones de despacho son SEND y QUERY_RESULT.

Enviar utiliza versión aprobada y destinatario simulado configurado. No admite direcciones reales aportadas por la
solicitud.

Consultar resultado utiliza la referencia original.

Se distinguen envío, recepción técnica, aceptación, rechazo e incertidumbre.

Una respuesta perdida exige consulta antes de reenvío. La referencia de presentación se conserva.

Rectificar recibe versión original, causa, fuentes corregidas, formato y aprobaciones.

Devuelve nueva versión, diferencias, despacho y pendientes. La original permanece.

El permiso de seguimiento no concede acceso automático al contenido reservado.

## **12. Eventos**

### **12.1. Catálogo**

Todos utilizan el sobre común con type, schemaVersion y aggregateType.

| **Contrato** | **type**                               | **Tema**                              |
|--------------|----------------------------------------|---------------------------------------|
| CI-11-045    | RiskPolicyOrExposureChanged            | banking.risk.events                   |
| CI-11-046    | CustomerDueDiligenceResultChanged      | banking.customer-due-diligence.events |
| CI-11-047    | AmlActionRecorded                      | banking.aml.events                    |
| CI-11-048    | FraudAssessmentOrResolutionChanged     | banking.fraud.events                  |
| CI-11-049    | RestrictionOrApplicationChanged        | banking.restriction.events            |
| CI-11-050    | TaxTreatmentOrSharedAffectationChanged | banking.tax-policy.events             |
| CI-11-051    | ReportingObligationOrSubmissionChanged | banking.regulatory-reporting.events   |

La versión inicial de esquema es 1. La clave es el identificador del agregado.

### **12.2. CI-11-045 — RiskPolicyOrExposureChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: POLICY_CHANGED, EXPOSURE_UPDATED, CAPACITY_COMMITTED, CAPACITY_TRANSFORMED, CAPACITY_RELEASED o
  CASE_UPDATED.

- scope: ControlScope.

- pendingComponents: pendientes comunes.

Variantes:

| **aggregateType**        | **Campos específicos**                                 |
|--------------------------|--------------------------------------------------------|
| RISK_POLICY              | Política, vigencia, aprobación y límites afectados.    |
| RISK_EXPOSURE            | Corte, fuentes, componentes y metodología.             |
| RISK_CAPACITY_COMMITMENT | Operación, afectaciones, estado y efectos resultantes. |
| RISK_MANAGEMENT_CASE     | Tipo de caso, alcance y tratamiento.                   |

Las transformaciones identifican componente sustituido y resultante.

### **12.3. CI-11-046 — CustomerDueDiligenceResultChanged**

Payload obligatorio:

- resultReference: referencia versionada.

- partyReference: ObjectReference.

- controlType: IDENTITY, BENEFICIAL_OWNERSHIP, LISTS, COMPLIANCE_PROFILE o INVESTMENT_SUITABILITY.

- resultCode: enumeración correspondiente al control.

- scope: ControlScope.

- reviewRequired: Booleano.

- pendingComponents: pendientes comunes.

Incluye vigencia y próxima revisión cuando están determinadas.

El aggregateType identifica IDENTITY_VERIFICATION, BENEFICIAL_OWNERSHIP_DETERMINATION, COMPLIANCE_LIST_CHECK,
COMPLIANCE_PROFILE o INVESTMENT_SUITABILITY_ASSESSMENT.

La variante determina el conjunto válido de resultados. No admite códigos libres.

### **12.4. CI-11-047 — AmlActionRecorded**

Payload obligatorio:

- objectReference: referencia versionada.

- actionType: ALERT_RECORDED, INVESTIGATION_OPENED, ANTECEDENT_ADDED, RESOLVED o SPECIALIZED_REPORT_REQUESTED.

- restrictedCaseReference: ObjectReference.

- pendingComponents: pendientes comunes.

Incluye resolución y requerimiento de presentación únicamente para consumidores especializados autorizados.

Los tipos de agregado son AML_ALERT y AML_INVESTIGATION.

El evento no contiene expediente completo ni se distribuye a suscriptores generales.

### **12.5. CI-11-048 — FraudAssessmentOrResolutionChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: ASSESSMENT_RECORDED, INVESTIGATION_UPDATED o RESOLUTION_RECORDED.

- operationReferences: ObjectReference\[\].

- pendingComponents: pendientes comunes.

Con aggregateType igual a FRAUD_ASSESSMENT, agrega huella evaluada, resultado, versiones de controles y vigencia cuando
corresponde.

Con aggregateType igual a FRAUD_INVESTIGATION, agrega estado, conclusión cuando existe, medidas solicitadas y
referencias de reparación.

El evento de resolución no demuestra devolución ejecutada.

### **12.6. CI-11-049 — RestrictionOrApplicationChanged**

Payload obligatorio:

- restrictionId: UUID.

- restrictionVersion: Version.

- changeType: REGISTERED, MODIFIED, APPLICATION_UPDATED o LIFTED.

- temporalStatus.

- applicationStatus.

- scope: alcance tipado de la medida.

- pendingComponents: pendientes comunes.

La variante RESTRICTION identifica autoridad, fundamento y vigencia.

La variante RESTRICTION_APPLICATION identifica aplicación, componente, propietario, alcance aplicado, insuficiencia y
evidencia.

Una aplicación incierta no se publica como completa.

Un levantamiento no demuestra liberación de todos los componentes.

### **12.7. CI-11-050 — TaxTreatmentOrSharedAffectationChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: RULE_CHANGED, TREATMENT_DETERMINED, AFFECTATION_RESERVED, AFFECTATION_CONFIRMED, AFFECTATION_RELEASED o
  AFFECTATION_CORRECTED.

- pendingComponents: pendientes comunes.

Variantes:

| **aggregateType**      | **Campos específicos**                                                |
|------------------------|-----------------------------------------------------------------------|
| TAX_RULE               | Tributo, jurisdicción, alcance y vigencia.                            |
| TAX_TREATMENT          | Sujeto, operación, resultado y reglas utilizadas.                     |
| SHARED_TAX_AFFECTATION | Sujeto, regla, período, operación, reservado, confirmado y remanente. |

Una corrección identifica afectación y hecho originales.

El evento no cobra tributos ni genera otro movimiento monetario.

### **12.8. CI-11-051 — ReportingObligationOrSubmissionChanged**

Payload obligatorio:

- objectReference: referencia versionada.

- changeType: OBLIGATION_CREATED, PREPARATION_UPDATED, APPROVED, DISPATCH_UPDATED, RESULT_RECORDED o RECTIFIED.

- requirementReference: VersionedReference.

- pendingComponents: pendientes comunes.

Con aggregateType igual a REPORTING_OBLIGATION, agrega ocurrencia, vencimiento, responsable y fuentes pendientes.

Con aggregateType igual a REGULATORY_SUBMISSION, agrega obligación, formato, huella, estado, despacho y resultado
conocido.

Una rectificación identifica presentación y versión anteriores.

Una recepción técnica no se presenta como aceptación del contenido.

### **12.9. Consumidores y orden**

Los consumidores se autorizan por finalidad y competencia:

- Riesgos: propietarios operativos, Evaluación crediticia, Tesorería y Contabilidad.

- Conocimiento: Vinculación, Clientes, Contratos y servicios que requieren el control.

- Lavado: funciones especializadas e Información regulatoria autorizada.

- Fraude: propietarios de operaciones, Disputas, Reclamos y Restricciones según alcance.

- Restricciones: servicios responsables de aplicar componentes.

- Tributos: productos, Pagos, Contabilidad e Información regulatoria según competencia.

- Presentaciones: fuentes, responsables especializados y Gestión administrativa dentro de su alcance.

Los eventos reservados no se distribuyen indiscriminadamente.

Los consumidores comparan versiones y recuperan estado autoritativo ante discontinuidades relevantes.

Los eventos no sustituyen consultas durables de decisiones ni ejecutan nuevamente efectos.

## **13. Seguridad y recuperación**

Se separan permisos para configurar, verificar, analizar, decidir, afectar capacidad, investigar, imponer medidas,
aplicar componentes, administrar acumuladores y preparar, aprobar o enviar presentaciones.

Los motivos comunicables se separan de antecedentes reservados.

Los compromisos, preparaciones y afectaciones compartidas inciertos no se liberan por timeout.

Las transacciones locales confirman objeto, historial, idempotencia y outbox. No mantienen bloqueos SQL durante llamadas
remotas.

Los consumidores registran inbox y efecto local antes de avanzar el procesamiento.

La recuperación utiliza identidades originales y conserva autorización en consultas históricas e idempotentes.

## **14. Errores particulares**

| **Código**                             | **HTTP** | **Significado**                                                   |
|----------------------------------------|----------|-------------------------------------------------------------------|
| CONTENT_CONFLICT                       | 409      | Identidad con contenido diferente.                                |
| VERSION_CONFLICT                       | 409      | Versión incompatible.                                             |
| RISK_POLICY_INCOMPLETE                 | 422      | Falta configuración necesaria.                                    |
| RISK_CAPACITY_INSUFFICIENT             | 422      | Capacidad insuficiente.                                           |
| RISK_TRANSFORMATION_MISMATCH           | 409      | Sustitución incompatible.                                         |
| EXPOSURE_SOURCE_GAP                    | 409      | Falta antecedente necesario.                                      |
| MANDATORY_VERIFICATION_PENDING         | 422      | Falta control exigible para actuar.                               |
| BENEFICIAL_OWNERSHIP_INCOMPLETE        | 422      | No puede completarse una actuación que exige control determinado. |
| INVESTMENT_SUITABILITY_FAILED          | 422      | Instrumento incompatible.                                         |
| FRAUD_ADDITIONAL_VERIFICATION_REQUIRED | 422      | Falta comprobación adicional.                                     |
| RESTRICTION_SCOPE_AMBIGUOUS            | 422      | Alcance sin resolver.                                             |
| RESTRICTION_COMPONENT_CONFLICT         | 409      | Aplicación contradictoria.                                        |
| PREPARATION_CONTENT_CONFLICT           | 409      | Intento o contenido incompatible.                                 |
| INCOMPATIBLE_CHANGE_PENDING            | 409      | Cambio en resolución.                                             |
| TAX_CONFIGURATION_MISSING              | 422      | Falta regla necesaria.                                            |
| SHARED_TAX_CAPACITY_INSUFFICIENT       | 422      | Beneficio o tope insuficiente.                                    |
| REPORTING_CONFIGURATION_MISSING        | 422      | Requerimiento incompleto.                                         |
| SUBMISSION_CONTENT_CHANGED             | 409      | Contenido distinto del aprobado.                                  |
| SUBMISSION_VALIDATION_FAILED           | 422      | Validación obligatoria incumplida.                                |
| DEPENDENCY_UNAVAILABLE                 | 503      | Dependencia necesaria indisponible.                               |

Una evaluación terminada puede responder 200 con resultado negativo o PENDING. El error corresponde a una actuación que
necesita un requisito favorable para continuar.

Una actuación durable todavía en procesamiento responde 202 y seguimiento.

Una repetición coherente recupera el resultado original.

## **15. Escenarios de aceptación**

| **Escenario**                                       | **Resultado exigido**                                                  |
|-----------------------------------------------------|------------------------------------------------------------------------|
| Aprobación 500 transformada en financiación 500     | Mantiene consumo 500 sin duplicación ni capacidad libre intermedia.    |
| Límite de persona y grupo aplicables                | Los comprueba y afecta conjuntamente.                                  |
| Fuente agregada llega después de una transformación | Reconoce los componentes ya incorporados.                              |
| Cambio interno confirmado por Fondos                | Transforma exposición sin simular salida externa.                      |
| Falta identidad o beneficiario final determinado    | Conserva faltantes sin inventar verificación favorable.                |
| Coincidencia de nombre en lista                     | Mantiene análisis, sin confirmar identidad automáticamente.            |
| Instrumento incompatible con perfil                 | No se habilita por excepción comercial.                                |
| Hecho de monitoreo repetido o corregido             | No duplica contribución a ventanas.                                    |
| Fraude no puede evaluarse                           | No devuelve ALLOW.                                                     |
| Resolución favorable de fraude                      | Solicita reparación al propietario sin declarar ejecución inexistente. |
| Medida total 100 sobre dos cuentas                  | Distribuye el total sin inmovilizar 100 en cada una.                   |
| Solo existen 60 afectables                          | Conserva insuficiencia 40.                                             |
| Medida nueva alcanza intento preparado              | Bloquea nuevos actos y resuelve el intento sin aborto ficticio.        |
| Se levanta una de varias medidas                    | Las restantes continúan vigentes.                                      |
| Dos operaciones compiten por beneficio compartido   | No consumen dos veces el mismo remanente.                              |
| Presentación aprobada cambia de contenido           | Exige nueva versión y aprobación.                                      |
| Destinatario devuelve recepción técnica             | Mantiene seguimiento hasta aceptación o resolución.                    |
| Respuesta perdida o evento duplicado                | Recupera decisión y efectos originales sin repetición.                 |

Las pruebas de unicidad, bloqueo y confirmación utilizan PostgreSQL real aislado. Las pruebas de contrato comprueban
entradas, resultados, errores y eventos contra sus esquemas.
