# Diseño técnico — Módulo 10. Tesorería

## 1. Propósito y alcance

El módulo administra los recursos propios del banco, sus compromisos de liquidez, las operaciones financieras propias,
el cambio de moneda y las liquidaciones externas.

Conserva la relación entre:

- Disponibilidades confirmadas.

- Compromisos.

- Flujos previstos.

- Operaciones concertadas.

- Entregas y recepciones.

- Cambios de exposición.

- Obligaciones pendientes.

- Conciliaciones y valuaciones.

La disponibilidad de una cuenta de cliente no equivale a la liquidez del banco.

Una anotación entre cuentas de clientes no demuestra un movimiento de efectivo ni una transferencia entre cuentas
externas del banco.

## 2. Servicios y responsabilidades

| Servicio | Identificador técnico | Base de datos | Responsabilidad |
| --- | --- | --- | --- |
| Posiciones y liquidez | liquidity-service | liquidity_db | Disponibilidades, efectivo, compromisos, proyecciones y cierre operativo. |
| Operaciones de tesorería | treasury-operation-service | treasury_operation_db | Financiación e inversiones propias, rendimientos y obligaciones. |
| Cambio de moneda | foreign-exchange-service | foreign_exchange_db | Cotizaciones, propuestas e intercambios. |
| Liquidaciones de tesorería | treasury-settlement-service | treasury_settlement_db | Obligaciones externas, instrucciones, cumplimiento y conciliación. |

Cada servicio administra PostgreSQL, usuario y migraciones propios.

No existen consultas SQL, relaciones JPA ni claves foráneas entre sus bases.

### 2.1. Distribución de autoridad

| Información o decisión | Responsable |
| --- | --- |
| Dinero de cuentas de clientes | Fondos. |
| Tenencias de clientes | Tenencias y valuación. |
| Obligaciones de compensación de pagos | Compensación y conciliación de pagos. |
| Obligaciones de red de tarjetas | Procesamiento de consumos. |
| Condiciones contractuales | Contratos. |
| Límites y exposición compartida | Riesgos. |
| Medidas restrictivas | Restricciones. |
| Facultades institucionales | Institución. |
| Registración contable | Registración contable. |

Los activos de clientes en custodia no se utilizan como recursos propios.

La falta de liquidez no extingue obligaciones ni permite borrar acreditaciones confirmadas.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato | Java | PostgreSQL | HTTP |
| --- | --- | --- | --- |
| Identificador | UUID | uuid | Cadena UUID. |
| Versión o secuencia | long | bigint | Entero positivo. |
| Instante | Instant | timestamptz | ISO 8601 en UTC. |
| Fecha económica | LocalDate | date | YYYY-MM-DD. |
| Importe | BigDecimal | numeric(24,8) | Cadena decimal. |
| Cantidad de instrumentos | BigDecimal | numeric(30,12) | Cadena decimal. |
| Cotización o precio | BigDecimal | numeric(30,12) | Cadena decimal. |
| Tasa o coeficiente | BigDecimal | numeric(24,12) | Cadena decimal. |
| Moneda | Enumeración | varchar(3) | ARS o USD. |
| Código | String | varchar(80) | Cadena normalizada. |
| Referencia externa | String | varchar(120) | Cadena. |
| Denominación | String | varchar(200) | Cadena. |
| Fundamento | String | varchar(4000) | Cadena. |
| Resumen | String | char(64) | SHA-256 hexadecimal. |

Los importes ejecutables respetan centavos.

Los cálculos conservan al menos doce decimales intermedios. Los importes monetarios finales utilizan HALF_UP.

Las cantidades respetan la precisión del instrumento.

La zona institucional es America/Argentina/Buenos_Aires.

### 3.2. Actuaciones

Los comandos identifican actuación, operación, objeto, finalidad, causa, versión esperada y evidencias.

Las actuaciones de liquidación agregan obligación, contraparte, mecanismo, destino, importe o cantidad y fecha.

El actor y el servicio emisor proceden de la autenticación.

Se distinguen operación, obligación, instrucción, mensaje, cumplimiento, movimiento y cambio de exposición.

### 3.3. Resultados

Los resultados técnicos son:

- PENDING.

- CONFIRMED.

- PARTIALLY_CONFIRMED.

- REJECTED.

- UNCERTAIN.

- CONFLICT.

Las respuestas identifican efectos confirmados, recursos comprometidos y remanentes.

Una aceptación técnica no demuestra pago.

### 3.4. Calendarios

La gestión ordinaria funciona en días hábiles de 09:00 a 18:00.

Las obligaciones y resultados se reciben durante todo el día.

Fuera de horario se conservan vencimiento, recepción y urgencia.

El cierre operativo comprende el día económico completo y no se confunde con un ciclo de compensación.

## 4. Ubicaciones y disponibilidades

### 4.1. Ubicación

Cada ubicación conserva:

- Identidad.

- Tipo: tesoro, sucursal, caja, dispositivo o cuenta propia externa.

- Titularidad del banco.

- Entidad depositaria.

- Moneda.

- Finalidad.

- Capacidades.

- Datos de liquidación y versión.

- Responsables.

- Restricciones.

- Estado.

- Versión.

Registrar una ubicación no crea dinero.

El saldo inicial requiere un hecho identificado y respaldo contable.

Los cambios sensibles se verifican por una vía independiente de la solicitud.

Una ubicación con saldo, compromisos o incertidumbres no se retira sin tratamiento.

### 4.2. Posición

Se conservan:

- Saldo confirmado.

- Compromisos monetarios.

- Inmovilizaciones.

- Capacidad utilizable.

- Faltante.

- Tránsito.

- Secuencia.

- Corte.

- Antigüedad.

- Condiciones aplicadas.

Los cobros previstos no aumentan saldo.

Las afectaciones superpuestas identifican el recurso para evitar descuentos duplicados.

Las monedas permanecen separadas.

### 4.3. Movimientos

Cada movimiento identifica efecto, origen, ubicación, moneda, dirección, importe, fecha efectiva, compromiso y
evidencia.

La salida y el consumo de su compromiso se confirman conjuntamente.

Los movimientos confirmados son inmutables.

Las correcciones generan nuevos movimientos relacionados.

## 5. Compromisos propios

### 5.1. Clases

| Clase | Finalidad | Forma de resolución |
| --- | --- | --- |
| EXTERNAL_PAYMENT | Cubrir una entrega monetaria externa. | Consumo contra salida confirmada o liberación comprobada. |
| PHYSICAL_CASH | Cubrir una entrega o traslado físico. | Consumo contra hecho físico o liberación comprobada. |
| OWN_INVESTMENT | Cubrir una operación propia pendiente de entrega. | Consumo al liquidar o liberación del remanente cancelado. |
| INTERNAL_FX_PREPARATION | Proteger la preparación de un cambio interno frente a concurrencia. | Resolución contra el conjunto de Fondos y actualización de exposición, sin salida externa ficticia. |

Una clase no puede cambiarse para reutilizar el compromiso en otra finalidad.

### 5.2. Datos

Cada compromiso identifica:

- Propietario.

- Operación u obligación.

- Clase.

- Moneda.

- Importe.

- Ubicaciones admitidas.

- Asignaciones.

- Consumido.

- Liberado o resuelto.

- Remanente.

- Firmeza.

- Condiciones.

- Versión.

Los compromisos de preparación se muestran separados de los pagos externos firmes.

Mientras estén pendientes afectan la capacidad de preparación correspondiente, sin modificar el saldo confirmado.

### 5.3. Concurrencia

Comprobar y comprometer se realiza en una transacción local.

Las posiciones se bloquean en orden estable.

Con capacidad 100, dos solicitudes incompatibles de 80 no se confirman ambas.

Una consulta no reserva recursos.

### 5.4. Reasignación y liberación

La reasignación confirma cobertura nueva antes de retirar la anterior.

Cuando pertenece a la misma base, se realiza de forma atómica.

No quedan dos compromisos independientes por el mismo importe.

La liberación no genera ingreso.

Un resultado incierto impide liberar por vencimiento administrativo.

## 6. Efectivo y traslados

La existencia física se administra por ubicación y moneda.

Una extracción utiliza:

- Reserva monetaria del cliente en Fondos.

- Compromiso físico en Posiciones y liquidez.

Son recursos distintos, vinculados a la misma operación.

La entrega comprobada produce una sola variación física.

### 6.1. Traslado

El traslado conserva origen, destino, importe, responsables, aprobación y referencias.

La salida reduce existencia de origen y genera tránsito.

La recepción extingue tránsito e incrementa destino por el importe comprobado.

Si salen 100 y se reciben 90, la diferencia 10 permanece identificada.

No se crea efectivo para completar el traslado.

### 6.2. Arqueo

Se conservan importe registrado, contado, momento, responsable y evidencia.

La verificación corresponde a una persona distinta del custodio de la existencia.

Las diferencias se investigan antes de ajustar.

Los ajustes requieren preparación y aprobación separadas.

## 7. Flujos y proyecciones

Cada flujo identifica propietario, operación, versión, moneda, importe, dirección, fecha y certeza.

La certeza distingue contractual, firme, estimada e incierta.

Las relaciones de composición permiten sustituir componentes por una obligación agregada sin sumarlos nuevamente.

Los horizontes son:

- Intradiario.

- Siguiente hábil.

- Siete días.

- Treinta días.

Cada proyección conserva fuentes, cortes, escenarios y limitaciones.

Los compromisos y sus flujos no se descuentan dos veces.

Una financiación aprobada no utilizada se informa como capacidad contingente.

### 7.1. Cobertura de faltantes

El plan coordina traslados, financiación, desinversión o cambio mediante sus responsables.

Una inversión que liquida mañana no cubre un pago de hoy.

Se impiden colocaciones discrecionales incompatibles con obligaciones firmes.

Las obligaciones incumplidas permanecen identificadas y se comunican.

## 8. Operaciones propias

### 8.1. Modalidades

Se administran financiación propia, colocaciones, préstamos interbancarios simples e inversiones de renta fija sin
apalancamiento.

No se admiten ventas en corto ni utilización de activos de clientes.

### 8.2. Operación

La operación conserva:

- Contraparte.

- Modalidad.

- Contrato y versión.

- Moneda.

- Capital o cantidad.

- Tasa o precio.

- Calendario.

- Cargos y tributos.

- Garantías propias.

- Límites.

- Aprobación.

- Ejecuciones.

- Liquidaciones.

- Derechos y obligaciones.

- Versión.

Concertar no demuestra recibir ni entregar dinero.

Las ejecuciones parciales tienen referencias propias.

### 8.3. Rendimientos

Las operaciones a tasa fija utilizan interés simple Actual/365, salvo flujos específicos del instrumento.

No se capitalizan intereses automáticamente.

Los títulos mantienen cupón, amortizaciones e interés corrido.

El cobro de un rendimiento devengado cancela el derecho sin reconocer otro ingreso.

### 8.4. Modificación

Una propuesta no cambia condiciones vigentes.

La salida anticipada conserva principal, interés, costos y resultado.

Una liquidación incierta impide informar cierre definitivo.

## 9. Cotizaciones

### 9.1. Datos

Cada cotización identifica:

- Clase.

- Par.

- Moneda base.

- Moneda cotizada.

- Unidad.

- Sentido desde el banco.

- Valor.

- Fuente.

- Momento.

- Vigencia.

- Versión.

- Corrección.

- Aprobación manual cuando corresponda.

Las clases son referencia, cierre contable, comercial ejecutable e informativa.

La compra no supera la venta para igual alcance y momento.

### 9.2. Finalidad

Una referencia contable o informativa no se ejecuta como oferta comercial.

La falta de referencia se informa.

La corrección conserva el valor anterior.

Una cotización histórica no se presenta como vigente.

## 10. Propuesta y aceptación cambiaria

La propuesta concreta conserva:

- Participante.

- Operación de origen.

- Finalidad.

- Modalidad de ejecución.

- Sentido.

- Ambas monedas.

- Importe fijo.

- Contraprestación.

- Cotización.

- Cargos y tributos.

- Cuentas o posiciones.

- Fecha de liquidación.

- Contenido.

- Emisión y vencimiento.

La vigencia comercial es de sesenta segundos.

Aceptar exige recepción anterior al vencimiento, comprobada por el servicio.

La aceptación fija condiciones. No asegura ejecución sin recursos.

Cotizar no reserva.

Una nueva tasa requiere nueva aceptación.

## 11. Cambio interno entre cuentas de clientes

### 11.1. Naturaleza

Esta modalidad modifica saldos de clientes en dos monedas dentro de Fondos.

No demuestra que hayan cambiado las existencias físicas ni los saldos de cuentas externas del banco.

Puede modificar la exposición cambiaria del banco y sus necesidades de liquidez.

Por eso se separan tres controles:

| Control | Responsable |
| --- | --- |
| Recursos del cliente | Fondos. |
| Cobertura temporal de preparación | Posiciones y liquidez. |
| Capacidad y exposición cambiaria | Riesgos. |

El compromiso temporal no sustituye el control de exposición.

### 11.2. Preparación

Cambio conserva una coordinación con:

- Intercambio.

- Propuesta y aceptación.

- Efectos en Fondos.

- Reserva del cliente.

- Compromiso propio de preparación.

- Afectación de Riesgos.

- Preparaciones de autorización.

- Resultado.

- Actuaciones pendientes.

El recorrido:

- Registrar intención.

- Comprobar condiciones y controles.

- Reservar recursos del cliente.

- Obtener compromiso INTERNAL_FX_PREPARATION.

- Afectar la capacidad de riesgo exigible.

- Confirmar que las coberturas corresponden al mismo contenido.

- Solicitar el conjunto interno a Fondos.

Si el cambio está integrado en otro producto, se reutilizan coberturas autorizadas y efectos relacionados. No se crean
reservas independientes por el mismo principal.

### 11.3. Ejecución

Fondos confirma conjuntamente débito y crédito mediante el conjunto interno.

Para la compra de USD 100 por ARS 102.000, ambos movimientos pertenecen al mismo intercambio.

La decisión monetaria y su recuperación siguen el protocolo de Fondos.

Cambio no informa ejecución completa con una sola moneda.

### 11.4. Resolución del compromiso propio

Después de comprobar el conjunto confirmado:

- Cambio comunica a Riesgos el resultado económico.

- Riesgos transforma la afectación pendiente en exposición efectiva.

- Se actualizan los flujos y necesidades de liquidez relacionados.

- Cambio solicita resolver el compromiso temporal en Posiciones.

- Posiciones conserva el vínculo con las confirmaciones y extingue el remanente de preparación.

La resolución se registra como RESOLVED_INTERNAL_FX, no como salida monetaria externa.

El saldo de una caja o cuenta propia externa no cambia por esta resolución.

El compromiso no se libera como si el cambio nunca hubiera ocurrido: queda resuelto contra un resultado confirmado y una
exposición identificada.

La exposición efectiva continúa computándose en Riesgos. Su transformación no devuelve automáticamente capacidad de
riesgo consumida.

### 11.5. Ejemplo

Para un cambio interno confirmado por USD 100:

| Elemento | Resultado |
| --- | --- |
| Cuenta del cliente en ARS | Débito del importe aceptado. |
| Cuenta del cliente en USD | Crédito de USD 100. |
| Cuenta propia externa en USD | Sin movimiento si no existió entrega externa. |
| Compromiso temporal propio | Resuelto contra el intercambio confirmado. |
| Exposición cambiaria | Actualizada conforme a Riesgos. |
| Proyección de liquidez | Actualizada sin registrar un flujo externo ya realizado inexistente. |

### 11.6. Fallos

Si Fondos rechaza definitivamente sin ejecutar, se aborta la preparación y se regularizan las coberturas.

Si el resultado de Fondos es incierto, se mantienen y consultan las referencias originales.

Si Fondos confirmó y falta actualizar Riesgos o resolver Posiciones:

- El cambio del cliente permanece confirmado.

- Se conserva regularización interna pendiente.

- No se repite el intercambio.

- No se crea un movimiento externo.

- No se libera capacidad de riesgo suponiendo inexistencia del cambio.

## 12. Cambio con entregas externas o propias

Cuando existen entregas efectivas entre ubicaciones propias o participantes externos se utiliza Liquidaciones de
tesorería.

Cada componente conserva moneda, importe, sentido, ubicación, obligación y cumplimiento.

El compromiso monetario se consume contra la salida comprobada.

La entrada aumenta posición solo al confirmarse.

Una parte ejecutada y otra pendiente no constituyen intercambio completo.

No se aplica la resolución de preparación interna para encubrir un pago externo.

Las operaciones propias mantienen facultades y límites correspondientes.

## 13. Autorizaciones y cambios concurrentes

Los actos que requieren condiciones externas utilizan preparaciones vinculadas al contenido y una decisión durable en el
ejecutor.

La aceptación de una cotización no sustituye la preparación del efecto.

Los cambios de facultades y restricciones:

- Impiden nuevas preparaciones incompatibles.

- Conservan recepción y efecto.

- Identifican intentos preparados.

- Solicitan aborto cuando procede.

- Recuperan la decisión.

- Tratan expresamente los efectos ya confirmados.

Las preparaciones no se liberan por timeout.

Para el conjunto de cuentas de clientes, Fondos conserva la decisión.

Para una entrega externa, Liquidaciones conserva su autorización de despacho y el resultado del mecanismo.

Una restricción posterior no permite registrar como inexistente una entrega realizada.

## 14. Incidencias cambiarias

Se distinguen:

- Completar una entrega.

- Restituir un error.

- Corregir conceptos.

- Resolver exposición o cobertura pendiente.

- Realizar otro cambio voluntario.

Una restitución no se disfraza de una compra nueva a cotización actual.

La operación voluntaria nueva exige condiciones actuales y aceptación.

Los ajustes conservan principal, cargos y tributos relacionados.

La pérdida causada por un error del banco no se traslada automáticamente al cliente.

El vencimiento de la propuesta no libera una ejecución incierta.

## 15. Obligaciones e instrucciones de liquidación

### 15.1. Obligación

La obligación identifica:

- Propietario y referencia.

- Versión.

- Operación o ciclo.

- Composición.

- Contraparte.

- Dirección.

- Moneda o instrumento.

- Importe o cantidad.

- Vencimiento.

- Mecanismo.

- Destino y versión.

- Firmeza.

- Parcialidad.

- Cumplimientos.

Una obligación neta no se suma a sus componentes.

Las correcciones proceden del origen y no sobrescriben cumplimientos.

Una obligación válida sin recursos permanece pendiente de cobertura.

### 15.2. Preparación y liberación

Liquidaciones obtiene compromiso, verifica destino y conserva aprobación o procedimiento automático.

Quien concertó una operación propia no libera por sí solo su liquidación.

La instrucción tiene referencia externa estable antes del envío.

Despacho y cancelación se ordenan localmente.

Si el despacho ya fue reclamado, la cancelación queda pendiente hasta resolver ese envío.

### 15.3. Cumplimiento

El resultado identifica instrucción, participante, importe o cantidad, fecha y evidencia.

Liquidaciones conserva el hecho, aplica cumplimiento, solicita el movimiento propio e informa al origen.

Si el externo confirmó y falta actualizar Posiciones, se recupera esa actualización. No se vuelve a pagar.

Los instrumentos se confirman con el propietario de la posición.

### 15.4. Parcialidad y recuperación

Pagados 60 de 100, quedan 40.

La parcialidad solo se instruye cuando el mecanismo la admite.

Se consulta al detectar incertidumbre, a los cinco minutos y a los treinta minutos.

Si persiste, se abre investigación y revisión por jornada.

Los tiempos no autorizan liberación ni extinción.

## 16. Conciliación, valuación y cierre

Liquidaciones compara cuentas externas, instrucciones y posiciones con cortes equivalentes.

Las diferencias distinguen tránsito, desfase, faltante, duplicación y contradicción.

No se modifica una posición para forzar coincidencia.

Operaciones de tesorería conserva costo, rendimiento, valor y referencias de inversiones propias.

Un precio faltante no equivale a cero.

Posiciones compone el cierre con disponibilidades, compromisos, tránsitos, exposiciones referenciadas, liquidaciones y
pendientes.

El cierre conserva fuentes y versiones.

Un cierre incompleto no se informa como definitivo.

El cierre operativo no cierra el período contable.

## 17. Interfaces HTTP

### 17.1. Posiciones y liquidez

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-10-001 | POST /treasury-resource-locations | Ubicación. |
| CI-10-001 | POST /treasury-resource-locations/{id}/actions | Cambio y aplicación. |
| CI-10-002 | GET /treasury-positions | Disponibilidades y compromisos por clase. |
| CI-10-003 | POST /treasury-resource-movements | Movimiento efectivo. |
| CI-10-004 | POST /cash-resource-transfers | Traslado y cobertura. |
| CI-10-005 | POST /cash-resource-transfers/{id}/physical-results | Salida, recepción y tránsito. |
| CI-10-005 | POST /cash-resource-counts | Arqueo. |
| CI-10-006 | POST /treasury-resource-commitments | Compromiso tipado. |
| CI-10-007 | POST /treasury-resource-commitments/{id}/actions | Ajuste, reasignación, liberación o resolución interna. |
| CI-10-008 | POST /liquidity-flow-updates | Flujo y composición. |
| CI-10-009 | POST /liquidity-projections | Proyección. |
| CI-10-009 | GET /liquidity-projections/{id} | Resultado conservado. |
| CI-10-010 | POST /liquidity-coverage-plans | Plan y actuaciones. |
| CI-10-031 | POST /treasury-operating-closes | Cierre. |
| CI-10-031 | GET /treasury-operating-closes/{id} | Versión y limitaciones. |
| CI-10-032 | GET /liquidity-actions/{actionId} | Resultado original. |

La resolución interna de CI-10-007 exige intercambio, conjunto de Fondos confirmado y transformación de exposición
cuando corresponda.

No permite declarar un pago externo como resuelto sin salida comprobada.

### 17.2. Operaciones propias

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-10-011 | POST /treasury-financing-operations | Acuerdo y obligaciones. |
| CI-10-012 | POST /treasury-investment-operations | Inversión o desinversión. |
| CI-10-013 | POST /treasury-operations/{id}/executions | Ejecución. |
| CI-10-014 | POST /treasury-operations/{id}/accruals | Rendimientos y vencimientos. |
| CI-10-015 | POST /treasury-operations/{id}/fulfillments | Aplicación. |
| CI-10-016 | POST /treasury-operations/{id}/modifications | Condiciones y salida. |
| CI-10-017 | GET /treasury-operations/{id} | Posición y pendientes. |
| CI-10-030 | POST /treasury-operation-valuations | Valuación. |

### 17.3. Cambio

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-10-018 | POST /exchange-rate-references | Cotización versionada. |
| CI-10-019 | GET /exchange-rate-references | Referencia por finalidad. |
| CI-10-020 | POST /foreign-exchange-proposals | Propuesta concreta. |
| CI-10-021 | POST /foreign-exchange-proposals/{id}/executions | Intercambio y coordinación. |
| CI-10-022 | POST /foreign-exchange-operations/{id}/resolutions | Corrección o regularización. |
| CI-10-023 | GET /foreign-exchange-operations/{id} | Ejecución y recursos. |
| CI-10-023 | GET /foreign-exchange-proposals/{id} | Oferta y aceptación. |

### 17.4. Liquidaciones

| Contrato | Interfaz | Resultado |
| --- | --- | --- |
| CI-10-024 | POST /treasury-settlement-obligations | Obligación validada. |
| CI-10-025 | POST /treasury-settlement-obligations/{id}/instructions | Instrucción y cobertura. |
| CI-10-026 | POST /treasury-settlement-instructions/{id}/results | Cumplimiento. |
| CI-10-027 | GET /treasury-settlement-obligations/{id} | Composición y remanente. |
| CI-10-027 | GET /treasury-settlement-instructions/{id} | Envío y resultado. |
| CI-10-028 | POST /treasury-settlement-instructions/{id}/resolutions | Recuperación o tratamiento. |
| CI-10-029 | POST /treasury-settlement-reconciliations | Diferencias. |

Las interfaces utilizan JSON, OpenAPI y contexto común.

Las modalidades tienen esquemas excluyentes.

Las respuestas usan 200, 201 y 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 18. Eventos

| Contrato | Evento |
| --- | --- |
| CI-10-033 | TreasuryResourceOrCommitmentChanged |
| CI-10-034 | LiquidityProjectionOrNeedChanged |
| CI-10-035 | TreasuryOperationChanged |
| CI-10-036 | ExchangeRateReferenceChanged |
| CI-10-037 | ForeignExchangeOperationChanged |
| CI-10-038 | TreasurySettlementOrReconciliationChanged |
| CI-10-039 | TreasuryCloseOrValuationChanged |

Los eventos identifican clase de compromiso, movimiento efectivo y resolución de preparación como hechos diferentes.

El sobre contiene identidad, esquema, emisor, agregado, versión, operación, actuación, causa y fechas.

Los temas son:

- banking.liquidity.events.

- banking.treasury-operation.events.

- banking.foreign-exchange.events.

- banking.treasury-settlement.events.

Una resolución interna no comunica una salida externa ni otro movimiento del cliente.

## 19. Persistencia, seguridad y recuperación

Los servicios mantienen datos propios, actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Se exige unicidad de efecto, compromiso, ejecución externa, aceptación, obligación y cumplimiento.

Cada cambio confirma historial, resultado, idempotencia y outbox en su transacción.

Las llamadas remotas se realizan fuera de ella.

La recuperación conserva referencias originales y no presupone rollback distribuido.

Keycloak autentica actores y servicios. Los permisos separan concertación, aprobación, liberación, resultados, arqueos y
ajustes.

PostgreSQL conserva la autoridad.

Redis puede acelerar cotizaciones informativas. La aceptación utiliza la propuesta persistida.

Los documentos permanecen en Documentos y comprobantes.

## 20. Pruebas y correspondencia funcional

Las pruebas incluyen:

- Dos compromisos sobre la misma disponibilidad.

- Reasignación sin duplicación.

- Efectivo en tránsito.

- Financiación todavía no recibida.

- Proyección sin sumar agregados y componentes.

- Aceptación concurrente con vencimiento.

- Cambio interno sin modificar saldos externos.

- Cambio confirmado con actualización de Riesgos pendiente.

- Resolución repetida del compromiso temporal.

- Cambio rechazado antes de ejecutar.

- Resultado de Fondos incierto.

- Pago externo parcial.

- Confirmación perdida.

- Falla de liquidación sin nuevo cobro al cliente.

- Restricción sobre un intento preparado.

- Cierre con regularizaciones pendientes.

| Operación | Implementación | Contratos |
| --- | --- | --- |
| OF-10-01. Ubicaciones | Titularidad y capacidades. | CI-10-001 y 002. |
| OF-10-02. Movimientos | Recursos efectivos. | CI-10-003 y 033. |
| OF-10-03. Efectivo | Traslado y arqueo. | CI-10-004 y 005. |
| OF-10-04. Compromisos | Clases y resolución. | CI-10-006 y 007. |
| OF-10-05. Proyecciones | Flujos y escenarios. | CI-10-008, 009 y 034. |
| OF-10-06. Faltantes | Plan de cobertura. | CI-10-010. |
| OF-10-07. Financiación propia | Acuerdo y recepción. | CI-10-011 y 013. |
| OF-10-08. Inversión propia | Ejecución y posición. | CI-10-012 y 013. |
| OF-10-09. Rendimientos | Devengamiento y aplicación. | CI-10-014 y 015. |
| OF-10-10. Modificación | Acuerdo y salida. | CI-10-016 y 017. |
| OF-10-11. Cotizaciones | Finalidades y versiones. | CI-10-018, 019 y 036. |
| OF-10-12. Propuesta | Condiciones concretas. | CI-10-020. |
| OF-10-13. Cambio | Recursos, ejecución y exposición. | CI-10-021 y 037. |
| OF-10-14. Incidencias | Corrección vinculada. | CI-10-022 y 023. |
| OF-10-15. Obligaciones | Origen y composición. | CI-10-024. |
| OF-10-16. Instrucciones | Cobertura y liberación. | CI-10-025. |
| OF-10-17. Cumplimiento | Hechos externos. | CI-10-026 y 027. |
| OF-10-18. Recuperación | Parcialidad e incertidumbre. | CI-10-028. |
| OF-10-19. Conciliación | Registros externos. | CI-10-029 y 038. |
| OF-10-20. Cierre | Posiciones y valuación. | CI-10-030, 031, 032 y 039. |
