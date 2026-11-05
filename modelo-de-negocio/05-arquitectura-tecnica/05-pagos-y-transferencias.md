# Diseño técnico — Módulo 5. Pagos y transferencias

## 1. Propósito y alcance

El módulo administra instrucciones para trasladar fondos, cumplir obligaciones, ejecutar autorizaciones recurrentes y
procesar cheques y ECHEQ.

Conserva la relación entre:

- Instrucción y contenido autorizado.

- Recursos comprometidos.

- Ejecución monetaria.

- Resultado externo.

- Aplicación a una obligación.

- Devoluciones y correcciones.

- Compensación y liquidación entre participantes.

Recibir una instrucción, enviar un mensaje, acreditar una cuenta, aplicar un pago y liquidar entre entidades son hechos
diferentes.

Los participantes y mecanismos externos operan dentro de la simulación.

## 2. Servicios y responsabilidades

| Servicio                             | Identificador técnico    | Base de datos       | Responsabilidad                                                                      |
|--------------------------------------|--------------------------|---------------------|--------------------------------------------------------------------------------------|
| Transferencias                       | transfer-service         | transfer_db         | Instrucciones internas, externas, cancelaciones y devoluciones.                      |
| Pagos                                | payment-service          | payment_db          | Convenios, obligaciones de referencia, ejecución, aplicación y reversión económica.  |
| Mandatos y recurrencias              | mandate-service          | mandate_db          | Autorizaciones, programaciones, ocurrencias y solicitudes de reversión.              |
| Cheques y ECHEQ                      | cheque-service           | cheque_db           | Identidad, circulación, compromisos, presentaciones, pago y rechazo de instrumentos. |
| Compensación y conciliación de pagos | payment-clearing-service | payment_clearing_db | Intercambios externos, recuperación, conciliación y obligaciones de compensación.    |

Cada servicio mantiene su base PostgreSQL, usuario y migraciones.

No existen relaciones JPA, consultas SQL ni claves foráneas entre bases de servicios.

### 2.1. Autoridades externas al módulo

| Información o decisión            | Responsable                                     |
|-----------------------------------|-------------------------------------------------|
| Permisos y capacidades de cuenta  | Cuentas.                                        |
| Resolución de CBU y alias         | Identificación bancaria.                        |
| Reservas y movimientos monetarios | Fondos.                                         |
| Representación general            | Representación.                                 |
| Deuda e imputación de préstamos   | Préstamos y líneas.                             |
| Deuda e imputación de tarjetas    | Cuenta de tarjeta.                              |
| Autorización y cargos con tarjeta | Servicios correspondientes de Tarjetas.         |
| Liquidación financiera externa    | Liquidaciones de tesorería.                     |
| Restricciones y controles         | Servicios competentes de Riesgo y cumplimiento. |
| Registración contable             | Contabilidad.                                   |

Compensación no modifica saldos. Pagos no mantiene una segunda deuda de préstamos o tarjetas.

## 3. Convenciones técnicas

### 3.1. Tipos y validaciones

| Dato                  | Java        | PostgreSQL     | HTTP                 |
|-----------------------|-------------|----------------|----------------------|
| Identificador interno | UUID        | uuid           | Cadena UUID.         |
| Versión               | long        | bigint         | Entero positivo.     |
| Instante              | Instant     | timestamptz    | ISO 8601 en UTC.     |
| Fecha                 | LocalDate   | date           | YYYY-MM-DD.          |
| Hora programada       | LocalTime   | time           | HH:mm:ss.            |
| Importe               | BigDecimal  | numeric(24,8)  | Cadena decimal.      |
| Tasa o coeficiente    | BigDecimal  | numeric(24,12) | Cadena decimal.      |
| Moneda                | Enumeración | varchar(3)     | ARS o USD.           |
| Código                | String      | varchar(80)    | Cadena normalizada.  |
| Referencia externa    | String      | varchar(120)   | Cadena.              |
| Descripción           | String      | varchar(200)   | Cadena.              |
| Fundamento            | String      | varchar(4000)  | Cadena.              |
| Resumen de contenido  | String      | char(64)       | SHA-256 hexadecimal. |

Los importes de ejecución son positivos y respetan los centavos de la moneda. No se redondean silenciosamente al
ejecutar.

Los cálculos intermedios conservan al menos doce decimales. Los conceptos finales se redondean una sola vez mediante
HALF_UP.

La zona institucional es America/Argentina/Buenos_Aires.

### 3.2. Identidades

Se distinguen:

- Operación de negocio.

- Actuación.

- Efecto monetario.

- Instrucción de transferencia o pago.

- Mandato y versión.

- Ocurrencia.

- Instrumento y presentación.

- Intercambio y mensaje.

- Devolución.

- Hecho compensable.

- Ciclo y obligación de liquidación.

Un mensaje identifica una comunicación. No sustituye la identidad del efecto económico.

Una devolución es una operación vinculada al original, con identidad propia.

### 3.3. Contexto de actuación

Los comandos incluyen:

| Campo              | Uso                                         |
|--------------------|---------------------------------------------|
| actionId           | Identidad de la petición.                   |
| operationId        | Correlación del recorrido de negocio.       |
| causationId        | Actuación o hecho de origen, cuando exista. |
| purpose            | Finalidad.                                  |
| reason             | Fundamento cuando corresponda.              |
| expectedVersion    | Versión esperada en modificaciones.         |
| evidenceReferences | Autorizaciones y evidencias verificables.   |
| originalReceivedAt | Recepción acreditada cuando afecta plazos.  |

El servicio y el actor autenticados se obtienen del contexto de seguridad.

Las evidencias identifican propietario, referencia, versión cuando corresponda, momento, alcance y vencimiento.

### 3.4. Contenido autorizado

El resumen de autorización comprende:

- Ordenante y representado.

- Cuenta de origen.

- Destino resuelto.

- Beneficiario.

- Principal.

- Moneda.

- Cargos aceptados.

- Finalidad.

- Obligación, mandato u ocurrencia relacionados.

Cambiar materialmente estos datos exige otra comprobación y la conformidad correspondiente.

## 4. Resultados y estados

Los servicios conservan dimensiones separadas.

| Dimensión                  | Estados principales                                                           |
|----------------------------|-------------------------------------------------------------------------------|
| Instrucción                | Recibida, en comprobación, aceptada para ejecución, cancelada o rechazada.    |
| Ejecución monetaria        | No iniciada, reservada, confirmada, incierta o regularización pendiente.      |
| Intercambio                | Preparado, en despacho, enviado, recibido o incierto.                         |
| Resultado del participante | Pendiente, aceptado irrevocablemente, acreditado o rechazado definitivamente. |
| Aplicación de pago         | No preparada, preparada, aplicada o pendiente de resolución.                  |
| Devolución                 | No solicitada, pendiente, parcial o completada.                               |
| Liquidación                | No determinada, pendiente, parcial, liquidada o fallida.                      |

No todos los instrumentos recorren todos esos estados.

Cada respuesta informa hechos comprobados, pendientes, referencias e importes remanentes.

Un rechazo definitivo requiere evidencia de que no se ejecutó el efecto y no queda una ejecución pendiente.

## 5. Transferencias

### 5.1. Datos propios

| Campo                       | Regla                                         |
|-----------------------------|-----------------------------------------------|
| transferId                  | UUID estable.                                 |
| transferType                | Interna, externa saliente o externa entrante. |
| originInstructionReference  | Referencia única del originante.              |
| orderingPartyId             | Ordenante.                                    |
| sourceAccountId             | Cuenta propia debitada, cuando corresponda.   |
| destination                 | Destino interno o externo tipado.             |
| resolvedDestinationSnapshot | Destino presentado y aceptado.                |
| principalAmount             | Importe del principal.                        |
| currency                    | Moneda del traslado.                          |
| acceptedContentHash         | Resumen del contenido autorizado.             |
| chargeDeterminationId       | Cargos y tributos determinados.               |
| reservationId               | Cobertura en Fondos.                          |
| monetaryEffectReferences    | Efectos de principal y cargos.                |
| exchangeId                  | Intercambio externo.                          |
| mechanismVersion            | Mecanismo utilizado.                          |
| version                     | Concurrencia.                                 |

El destino interno contiene cuenta y resolución de origen.

El destino externo contiene participante, identificador bancario, referencia de resolución e información de beneficiario
necesaria para la operación.

No se permite una transferencia entre monedas diferentes mediante una conversión implícita.

### 5.2. Transferencia interna

El proceso:

- Conserva la instrucción.

- Resuelve y fija el destino.

- Comprueba facultades, restricciones y condiciones.

- Determina los conceptos aceptados.

- Conserva la intención monetaria.

- Solicita a Fondos el conjunto interno.

- Recupera el resultado.

- Confirma la transferencia y publica el hecho.

Fondos confirma débito y crédito conjuntamente.

Una respuesta perdida se recupera mediante la actuación monetaria original. No se envía otro conjunto con efectos
diferentes.

La misma cuenta no puede ocupar ambos extremos.

### 5.3. Transferencia externa saliente

El recorrido ordinario es:

- Comprobar instrucción y destino.

- Reservar principal y cargos autorizados.

- Preparar el intercambio.

- Proteger la cobertura ante el inicio del despacho.

- Enviar mediante Compensación.

- Comprobar aceptación y acreditación.

- Consumir la reserva al acreditar el destino.

- Incorporar el hecho a compensación.

Antes de habilitar el despacho se conserva el compromiso de envío y se solicita a Fondos el tratamiento incierto de la
reserva. Si se confirma que no hubo envío ni ejecución posible, se regulariza su firmeza o se libera según corresponda.

La aceptación irrevocable conserva cobertura firme.

El acuse técnico no habilita el consumo como si demostrara acreditación.

Si el destino se acreditó y falta confirmar el débito de origen, se mantiene la obligación de completar ese efecto. No
se informa rechazo.

Una falla posterior de liquidación no revierte la acreditación.

### 5.4. Transferencia externa entrante

La clave externa comprende:

- Participante.

- Mecanismo.

- Referencia de operación.

Se verifica la coherencia del contenido ante repeticiones.

Antes de aceptar irrevocablemente se comprueban destino, moneda y controles.

Después de aceptar se conserva la obligación de acreditar o resolver mediante el procedimiento correspondiente.

El crédito utiliza un efecto monetario estable. Un mensaje repetido no origina otra acreditación.

Una restricción exclusiva sobre débitos no se interpreta como prohibición de créditos.

### 5.5. Cancelación

La cancelación se ordena frente a la autorización de despacho.

- Si se cancela una instrucción todavía no entregada al intercambio, se impide crear o habilitar su despacho.

- Si ya existe un intercambio, se solicita su cancelación y se espera una confirmación que descarte un envío posterior.

- Si el despacho está en curso, la cancelación queda pendiente.

- Si hubo envío, se consulta y tramita la cancelación externa.

- Si hubo acreditación, corresponde una devolución.

Compensación conserva una marca de cancelación para impedir que una preparación tardía de la misma referencia se envíe
después de confirmar su cancelación.

No se libera cobertura por la mera recepción de la petición.

### 5.6. Devoluciones

Cada devolución conserva original, importe, moneda, causa, destino de retorno, fundamento y efectos.

Se controla conjuntamente:

- Principal ya devuelto.

- Devoluciones pendientes que comprometen remanente.

- Nueva devolución solicitada.

Sobre un principal de 100 con 40 devueltos, quedan como máximo 60, descontando también solicitudes ya comprometidas.

La petición del ordenante no autoriza a debitar al beneficiario.

Los cargos reciben tratamiento separado. Un error del banco exige regularizar también sus consecuencias indebidas.

## 6. Pagos

### 6.1. Convenio de recaudación

Cada versión contiene:

- Participante y contrato.

- Servicio recaudado.

- Formatos de referencia admitidos.

- Monedas.

- Instrumentos.

- Pagos parciales, anticipados y vencidos permitidos.

- Cargos.

- Preparación y aplicación.

- Consulta y cancelación.

- Recuperación.

- Vigencia.

- Preparador y aprobación separada.

Los estados son preparación, pendiente de aprobación, habilitado, suspendido y retirado.

No se habilita un convenio que no permita resolver una ejecución monetaria confirmada con aplicación pendiente.

La suspensión impide nuevos pagos y conserva el tratamiento de los anteriores.

### 6.2. Obligación de referencia

| Campo                       | Contenido                                    |
|-----------------------------|----------------------------------------------|
| obligationReferenceId       | Identidad local de la referencia.            |
| creditorReference           | Acreedor competente.                         |
| externalObligationReference | Identidad de la obligación en origen.        |
| debtorReference             | Obligado identificado.                       |
| sourceVersion               | Versión o confirmación del acreedor.         |
| amount                      | Importe informado.                           |
| currency                    | Moneda.                                      |
| dueDate                     | Vencimiento.                                 |
| allowedPaymentModes         | Modalidades admitidas.                       |
| checkedAt                   | Momento de consulta.                         |
| validUntil                  | Vigencia, si existe.                         |
| verificationStatus          | Confirmada, manual, ambigua o no comprobada. |

Una referencia manual no se presenta como deuda confirmada.

Un cambio de importe antes de autorizar exige presentar el nuevo importe.

### 6.3. Orden de pago

La orden conserva:

- Pagador y obligación.

- Convenio y versión.

- Importe aceptado.

- Instrumento.

- Autorización.

- Mandato y ocurrencia cuando existan.

- Aceptación preliminar.

- Aplicación preparada.

- Ejecución monetaria.

- Aplicación definitiva.

- Devoluciones y reversión.

### 6.4. Preparación y aplicación

El proceso es:

- Obtener aceptación preliminar del acreedor.

- Coordinar cobertura.

- Obtener una aplicación preparada.

- Ejecutar el instrumento una sola vez.

- Confirmar la aplicación definitiva.

- Conservar resultado y saldo informado por el acreedor.

La preparación tiene referencia estable y contenido exacto. No extingue la deuda.

Si el instrumento es una transferencia, Pagos utiliza su reserva y ejecución. Si es tarjeta, utiliza la autorización y
el cargo de Tarjetas.

Pagos no crea otra reserva o débito paralelo.

Un débito confirmado sin aplicación inicia recuperación para obtener aplicación o devolución.

La confirmación al acreedor se repite con la misma referencia. No se vuelve a ejecutar el instrumento.

### 6.5. Exclusión de pagos incompatibles

Cuando el convenio no admite pagos múltiples o parciales, Pagos impide órdenes activas incompatibles sobre la misma
obligación.

La clave comprende acreedor, convenio y referencia de obligación.

La validación definitiva de la deuda sigue perteneciendo al acreedor. El control local no sustituye la preparación
remota.

### 6.6. Anulación y rectificación

Se distinguen:

- Cancelación previa a la ejecución.

- Rectificación de referencia sin dinero.

- Devolución monetaria.

- Corrección de un efecto erróneo.

Un pago aplicado no se elimina.

La devolución puede reabrir deuda únicamente mediante la determinación del acreedor.

## 7. Cargos y tributos

Transferencias, Pagos y Cheques determinan los conceptos propios de su servicio.

Cada determinación conserva:

- Condición comercial o contractual.

- Concepto.

- Servicio beneficiario.

- Hecho generador.

- Base.

- Importe o porcentaje.

- Bonificación.

- Determinación tributaria.

- Importe final.

- Efecto monetario.

El principal permanece separado.

Una comisión por ejecución se genera al confirmar la ejecución que la origina. Puede estar cubierta previamente por una
reserva, sin considerarse cobrada.

Cuando Pagos utiliza Transferencias se identifican las causas de cada cargo. No se cobra dos veces el mismo servicio por
intervenir dos coordinadores.

El uso de descubierto exige compatibilidad de producto, cuenta y concepto. Una opción del solicitante no basta.

## 8. Mandatos y recurrencias

### 8.1. Mandato

| Campo                  | Regla                                              |
|------------------------|----------------------------------------------------|
| mandateId              | UUID estable.                                      |
| grantorPartyId         | Otorgante.                                         |
| beneficiaryReference   | Beneficiario autorizado.                           |
| accountId              | Cuenta alcanzada.                                  |
| purpose                | Finalidad.                                         |
| amountMode             | Fijo o variable con máximo.                        |
| fixedAmount            | Obligatorio para modalidad fija.                   |
| maximumPerOccurrence   | Obligatorio para modalidad variable.               |
| currency               | Moneda.                                            |
| frequency              | Frecuencia autorizada.                             |
| validFrom / validUntil | Vigencia.                                          |
| acceptanceEvidence     | Conformidad del otorgante.                         |
| version                | Versión utilizada.                                 |
| status                 | Pendiente, activo, suspendido, revocado o vencido. |

El mandato no reserva fondos para toda su vigencia.

Cambiar beneficiario exige otro mandato. Ampliar importe, frecuencia o alcance requiere aceptación.

No se fracciona un cobro para eludir el máximo.

### 8.2. Programación

La programación conserva:

- Instrucción autorizada.

- Mandato cuando corresponde.

- Fecha original de anclaje.

- Frecuencia.

- Día del mes o intervalo.

- Calendario y versión.

- Regla de ajuste.

- Hora.

- Zona.

- Inicio.

- Fecha final o cantidad máxima.

- Condiciones de importe y destino.

Las reglas de ajuste admitidas son día original, siguiente hábil o anterior hábil, conforme a la instrucción aceptada.

Cuando el día de anclaje no existe en un mes, se utiliza su último día para esa ocurrencia, sin cambiar el anclaje de
meses posteriores.

El horario ordinario es 09:00. Una transferencia única puede establecer otro.

### 8.3. Ocurrencia

Cada ocurrencia contiene:

- Identidad.

- Programación y versión o presentación del acreedor.

- Fecha original y ajustada.

- Hora prevista.

- Mandato y versión.

- Referencia del cobro.

- Importe.

- Contenido autorizado.

- Instrucción originada.

- Resultado y causa.

La identidad de una ocurrencia programada deriva de programación y posición dentro de la serie. No se identifica
únicamente por la fecha ajustada, porque dos fechas pueden coincidir después de un ajuste.

Una ocurrencia solo origina una instrucción efectiva.

### 8.4. Admisión y revocación

La admisión de la ocurrencia y los cambios del mandato se ordenan bajo bloqueo del mismo mandato.

Si la revocación se confirma antes de admitir, no se origina la instrucción.

Si la ocurrencia ya fue admitida, se conserva como actuación anterior y se coordina la cancelación con Pagos o
Transferencias según su avance.

Revocar no devuelve pagos ni extingue deuda.

Un mandato revocado no se reactiva. Se requiere uno nuevo.

### 8.5. Ejecución y recuperación

Los trabajadores temporales conservan trabajos persistentes con arrendamiento y control de vigencia.

- Recuperación dentro de la fecha prevista: ejecución única si conserva requisitos.

- Recuperación en fecha posterior: pago o transferencia programados requieren confirmación del ordenante.

- Presentación omitida de un acreedor: se informa la omisión y se exige otra ocurrencia válida.

- Insuficiencia de fondos: resultado fallido sin reintentos automáticos.

- Resultado incierto: consultas de la misma instrucción.

Un intento posterior autorizado conserva referencia propia y vínculo con el anterior. No elude máximos ni repite un
cobro incierto.

## 9. Reversión de débitos

### 9.1. Recepción

Mandatos y recurrencias recibe la solicitud e identifica:

- Solicitante y facultades.

- Mandato.

- Ocurrencia.

- Débito y pago.

- Importe reclamado.

- Recepción acreditada.

- Devoluciones anteriores.

El plazo es de treinta días corridos desde el débito, excluyendo el día inicial. El vencimiento inhábil se traslada al
siguiente hábil.

Fuera del plazo se deriva a reclamo, conservando la recepción.

Solicitar reversión no revoca el mandato.

### 9.2. Tratamiento económico

Pagos administra:

- Restitución provisional.

- Devolución definitiva.

- Regularización.

- Eventual recupero.

- Comunicación al acreedor.

Comprobadas identidad, facultades y correspondencia, la restitución provisional debe completarse como máximo al
finalizar el tercer día hábil posterior a la recepción.

El cómputo conserva calendario, fecha inicial y vencimiento.

### 9.3. Prevención de doble restitución

Pagos mantiene un registro económico por débito original con:

- Importe reclamado.

- Restituciones comprometidas.

- Provisionales acreditadas.

- Devoluciones definitivas.

- Regularizaciones.

- Remanente restituible.

Las decisiones concurrentes se serializan sobre ese registro.

Las devoluciones recibidas después del provisional se vinculan a su regularización. No se acreditan nuevamente al
cliente por el mismo importe.

El instrumento encargado del retorno recibe la instrucción y el destino económico correspondiente antes de ejecutar sus
efectos.

Una objeción del acreedor no retira por sí sola el provisional.

El recupero exige resolución y no utiliza descubierto automáticamente.

## 10. Cheques y ECHEQ

### 10.1. Modalidades

Se admiten:

- Papel simulado y ECHEQ.

- Común y de pago diferido.

- Beneficiario nominativo.

- A la orden o no a la orden.

- ARS o USD.

- Pago total.

No se admiten portador, endoso en blanco, certificación de fondos, aval parcial ni pago parcial.

### 10.2. Instrumento

| Campo                       | Contenido                                   |
|-----------------------------|---------------------------------------------|
| instrumentId                | Identidad estable.                          |
| externalRegistryReference   | Referencia del registro cuando corresponda. |
| support                     | Papel o ECHEQ.                              |
| chequeClass                 | Común o diferido.                           |
| drawerAccountReference      | Cuenta libradora interna o externa.         |
| drawerPartyReference        | Librador.                                   |
| initialBeneficiaryReference | Beneficiario de emisión.                    |
| currentHolderReference      | Tenedor legitimado.                         |
| amount                      | Importe íntegro.                            |
| currency                    | Moneda.                                     |
| issueDate                   | Fecha de emisión.                           |
| paymentDate                 | Fecha de pago del diferido.                 |
| circulationMode             | A la orden o no a la orden.                 |
| contentHash                 | Contenido emitido.                          |
| registrySequence            | Secuencia del registro electrónico.         |
| version                     | Concurrencia local.                         |

La identidad del instrumento externo se controla dentro de participante y registro.

En papel pueden existir datos pendientes al registrar una chequera. El contenido completo debe verificarse antes de
presentar el instrumento.

### 10.3. Chequeras y emisión

La chequera conserva cuenta, moneda, rango de numeración, habilitación y entrega.

Los rangos de la misma cuenta no se superponen.

Entregar una chequera no emite sus instrumentos, reserva dinero ni debita.

La emisión electrónica verifica cuenta corriente habilitada y misma moneda.

Los cambios esenciales de un ECHEQ emitido requieren cancelación válida y otra emisión.

### 10.4. Fechas

| Clase    | Regla                                                                                                        |
|----------|--------------------------------------------------------------------------------------------------------------|
| Común    | Emisión no futura; presentación desde emisión hasta treinta días corridos después.                           |
| Diferido | Pago posterior a emisión, separación máxima de 365 días; presentación desde pago hasta treinta días después. |

Se excluye el día de referencia del cómputo de treinta días y se admiten ambos extremos de presentación.

El último día inhábil se extiende al siguiente hábil de compensación.

La oportunidad depende de la recepción validada en el banco depositario, no de una demora posterior de procesamiento.

### 10.5. Circulación

Se conservan actuaciones inmutables de:

- Aceptación.

- Propuesta de transmisión.

- Aceptación o rechazo del receptor.

- Endoso.

- Cesión.

- Aval.

- Compromiso para descuento.

Cada actuación identifica origen, destino, facultades, contenido y momento.

El instrumento tiene un único compromiso incompatible activo. Ese control impide transmisión, depósito y descuento
simultáneos.

El endoso es nominativo y total.

La cesión de un instrumento no a la orden exige documentación, aceptación y reconocimiento.

El aval total de un tercero no cambia el tenedor ni reserva fondos.

### 10.6. Descuento

Préstamos y líneas solicita un compromiso identificado.

Cheques verifica legitimación y disponibilidad y bloquea usos incompatibles.

La formalización confirma cesión al banco y financiación relacionada.

Una incertidumbre no permite liberar el compromiso por temporizador.

El cobro posterior se destina a la financiación. No genera otro ingreso libre para el cliente.

El rechazo conserva tanto la historia del instrumento como la financiación bajo autoridad de Préstamos y líneas.

### 10.7. Depósito y presentación

Se distinguen recepción, validación y presentación.

- Papel: validación en días hábiles hasta las 17:00.

- Papel recibido fuera de horario: validación posterior, sin antedatar oportunidad.

- ECHEQ: recepción continua.

- ECHEQ validado antes de las 18:00: incorporación al ciclo hábil correspondiente.

- Recepción anticipada de diferido: custodia hasta la fecha habilitada.

El depósito no concede disponibilidad.

Cada presentación tiene identidad propia. Una presentación incierta impide otra.

### 10.8. Pago, rechazo y medidas

Se procesa pago íntegro o rechazo.

Con importe 100 y capacidad 80 no se debitan 80 como pago parcial.

El pago confirmado impide otro cobro del instrumento.

Después de rechazo por insuficiencia puede recibirse una nueva instrucción dentro del plazo, si el intento anterior está
resuelto y no hay otros impedimentos.

No existe nueva presentación automática.

Las medidas conservan causa, autoridad, alcance y vigencia.

Cancelar por acuerdo requiere conformidad del librador y tenedor actual y ausencia de presentación incierta.

Devolver el soporte no representa pago ni devolución monetaria.

## 11. Intercambio externo

### 11.1. Participantes y mecanismos

Cada versión de mecanismo define:

- Participante.

- Identidad y contrato.

- Operaciones y monedas.

- Calendario.

- Horarios.

- Referencias externas.

- Estados y evidencias.

- Consulta.

- Cancelación.

- Recuperación.

- Hecho compensable.

- Neteo.

- Liquidación.

- Aprobación separada.

No se habilita un mecanismo sin consulta de resultados inciertos.

La suspensión detiene nuevos envíos alcanzados y conserva la recuperación de pendientes.

### 11.2. Intercambio y mensajes

Cada intercambio conserva:

- Servicio y operación de origen.

- Referencia externa estable.

- Participante y mecanismo.

- Versión.

- Tipo de actuación.

- Contenido canónico y resumen.

- Estado de despacho.

- Mensajes enviados y recibidos.

- Evidencias.

- Resultado normalizado.

- Consultas y cancelaciones.

Los mensajes originales no se sobrescriben al normalizarlos.

Una referencia desconocida o un contenido contradictorio se pone en investigación. No se aplica por similitud a otra
operación.

### 11.3. Despacho y cancelación

La preparación, la autorización de despacho y la cancelación se ordenan en Compensación.

El trabajador reclama un despacho mediante una transición local persistente.

Si el despacho fue reclamado, una cancelación posterior no se informa como confirmada sin resolver esa ejecución.

Las llamadas al participante se hacen fuera de la transacción SQL.

El adaptador simulado utiliza referencias idempotentes y una consulta que distingue:

- No recibido.

- Recibido.

- Aceptado.

- Ejecutado.

- Cancelado con imposibilidad de ejecución posterior.

- Resultado incierto.

Una consulta aislada de “no recibido” no basta para liberar cobertura si todavía existe un envío en tránsito.

### 11.4. Recuperación

Se consulta:

- Al detectar incertidumbre.

- A los cinco minutos.

- A los treinta minutos.

Si persiste, se abre investigación y se revisa cada jornada operativa.

Los plazos no autorizan rechazo ni liberación automática.

## 12. Conciliación y compensación

### 12.1. Conciliación

Cada ejecución de conciliación conserva fuentes, cortes, referencias, resultados y diferencias.

Se distinguen:

- Desfase temporal.

- Registro faltante.

- Duplicación.

- Importe o moneda incompatibles.

- Estado contradictorio.

- Liquidación pendiente.

La conciliación solicita regularizaciones al dueño del negocio. No debita dinero directamente.

Las resoluciones manuales requieren preparación y aprobación separadas.

### 12.2. Ciclos

El corte diario es a las 18:00 de la zona institucional.

Los intervalos se representan sin superposición: el hecho ocurrido exactamente en el corte integra el siguiente ciclo.

Los mecanismos sujetos a días hábiles utilizan su calendario.

Cada ciclo conserva mecanismo, participante, moneda, inicio, fin y versión de reglas.

### 12.3. Hechos compensables

| Instrumento     | Hecho                                                   |
|-----------------|---------------------------------------------------------|
| Transferencias  | Acreditaciones confirmadas y devoluciones.              |
| Pagos y débitos | Ejecuciones que generan obligación entre participantes. |
| Cheques         | Pago confirmado por la entidad girada.                  |

Las reservas no se compensan como pagos.

Cada hecho tiene una identidad única de inclusión. El mismo hecho comunicado por un evento y una conciliación no se
incorpora dos veces.

### 12.4. Obligaciones

La obligación conserva:

- Participante.

- Moneda.

- Ciclo.

- Modalidad bruta o neta.

- Detalle de hechos.

- Totales por sentido.

- Importe resultante.

- Acuerdo de neteo.

- Referencia de liquidación.

- Resultados y remanente.

El neteo requiere acuerdo y no mezcla monedas.

Un ciclo cerrado no se reescribe. Los ajustes posteriores se incorporan a otro ciclo con referencia al original.

Liquidaciones de tesorería ejecuta y comunica el resultado.

Una liquidación fallida no rechaza retroactivamente operaciones de clientes.

## 13. Interfaces HTTP

Las interfaces se describen con OpenAPI. Los comandos incorporan el contexto común y esquemas específicos por modalidad.

### 13.1. Transferencias

| Contrato  | Interfaz                           | Entrada y resultado                                                                               |
|-----------|------------------------------------|---------------------------------------------------------------------------------------------------|
| CI-05-001 | POST /internal-transfers           | Origen, destino aceptado, principal y controles. Devuelve movimientos conjuntos y resultado.      |
| CI-05-002 | POST /outgoing-transfers           | Destino externo, mecanismo, importe y autorización. Devuelve reserva, intercambio y seguimiento.  |
| CI-05-003 | POST /incoming-transfers           | Participante, referencia, destino, importe y evidencia. Devuelve aceptación, crédito o pendiente. |
| CI-05-004 | POST /transfers/{id}/cancellations | Motivo y facultades. Devuelve cancelación comprobada o seguimiento.                               |
| CI-05-005 | POST /transfers/{id}/returns       | Importe, fundamento y destino de retorno. Devuelve devolución y remanente.                        |
| CI-05-006 | GET /transfers/{id}                | Devuelve contenido, estados, movimientos, intercambios y devoluciones.                            |
| CI-05-006 | GET /transfer-actions/{actionId}   | Recupera la actuación original.                                                                   |

### 13.2. Pagos

| Contrato  | Interfaz                                         | Entrada y resultado                                                                                      |
|-----------|--------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| CI-05-007 | POST /collection-agreements                      | Participante, contrato y condiciones. Devuelve convenio en preparación.                                  |
| CI-05-007 | POST /collection-agreements/{id}/actions         | Nueva versión, aprobación, habilitación o retiro. Devuelve situación resultante.                         |
| CI-05-008 | GET /collection-agreements/{id}                  | Devuelve versión y modalidades.                                                                          |
| CI-05-009 | POST /payment-obligation-lookups                 | Convenio, acreedor y referencia. Devuelve obligación y vigencia.                                         |
| CI-05-010 | POST /payments                                   | Obligación, importe aceptado, instrumento y autorización. Devuelve ejecución y aplicación diferenciadas. |
| CI-05-011 | POST /payments/{id}/adjustments                  | Cancelación, rectificación o devolución tipada. Devuelve efectos y pendientes.                           |
| CI-05-012 | GET /payments/{id}                               | Devuelve preparación, ejecución, aplicación y restituciones.                                             |
| CI-05-012 | GET /payment-actions/{actionId}                  | Recupera actuación o reversión.                                                                          |
| CI-05-020 | POST /debit-reversal-cases/{id}/economic-actions | Débito, recepción, importe y evidencias. Devuelve provisional, definitivo y remanente.                   |

### 13.3. Mandatos y recurrencias

| Contrato  | Interfaz                           | Entrada y resultado                                                                              |
|-----------|------------------------------------|--------------------------------------------------------------------------------------------------|
| CI-05-013 | POST /mandates                     | Otorgante, beneficiario, cuenta, límites y aceptación. Devuelve mandato.                         |
| CI-05-013 | POST /mandates/{id}/versions       | Condiciones y aceptación del cambio. Devuelve nueva versión.                                     |
| CI-05-014 | POST /mandates/{id}/status-actions | Suspender, reanudar o revocar. Devuelve efecto y actuaciones anteriores.                         |
| CI-05-015 | GET /mandates/{id}                 | Devuelve versión, alcance y vigencia.                                                            |
| CI-05-015 | POST /mandates/{id}/checks         | Ocurrencia y contenido. Devuelve correspondencia con la autorización.                            |
| CI-05-016 | POST /schedules                    | Anclaje, frecuencia, calendario, importe y destino. Devuelve programación.                       |
| CI-05-016 | POST /schedules/{id}/actions       | Modificación, suspensión o terminación. Devuelve versión y fechas.                               |
| CI-05-017 | POST /occurrences                  | Programación o presentación, importe y referencia. Devuelve instrucción originada o impedimento. |
| CI-05-018 | GET /schedules/{id}                | Devuelve versiones y fechas.                                                                     |
| CI-05-018 | GET /occurrences/{id}              | Devuelve admisión, instrucción y resultado.                                                      |
| CI-05-019 | POST /debit-reversal-requests      | Débito, solicitante, motivo y recepción. Devuelve expediente y tratamiento económico.            |

### 13.4. Cheques y ECHEQ

| Contrato  | Interfaz                                    | Entrada y resultado                                                         |
|-----------|---------------------------------------------|-----------------------------------------------------------------------------|
| CI-05-021 | POST /cheque-books                          | Cuenta, moneda, numeración y facultades. Devuelve habilitación y entrega.   |
| CI-05-022 | POST /cheques                               | Identidad, soporte, importe, participantes y fechas. Devuelve instrumento.  |
| CI-05-023 | POST /cheques/{id}/circulation-actions      | Actuación, participantes y evidencias. Devuelve cadena y compromiso.        |
| CI-05-024 | POST /cheques/{id}/discount-commitments     | Financiación, tenedor y facultades. Devuelve compromiso.                    |
| CI-05-025 | POST /discount-commitments/{id}/resolutions | Formalización o no ejecución acreditada. Devuelve cesión o liberación.      |
| CI-05-026 | POST /cheques/{id}/presentations            | Recepción, tenedor o financiación y destino. Devuelve oportunidad y ciclo.  |
| CI-05-027 | POST /cheque-presentations/{id}/resolutions | Resultado íntegro y evidencia. Devuelve pago, rechazo o incertidumbre.      |
| CI-05-028 | POST /cheques/{id}/instrument-actions       | Medida, cancelación o devolución de soporte. Devuelve alcance y pendientes. |
| CI-05-029 | GET /cheques/{id}                           | Devuelve historia, tenedor, presentaciones y resultado.                     |

### 13.5. Compensación

| Contrato  | Interfaz                                           | Entrada y resultado                                                               |
|-----------|----------------------------------------------------|-----------------------------------------------------------------------------------|
| CI-05-030 | POST /payment-network-participants                 | Identidad, contrato y mecanismos. Devuelve configuración.                         |
| CI-05-030 | POST /payment-network-participants/{id}/actions    | Versión, aprobación, habilitación o suspensión. Devuelve capacidades.             |
| CI-05-031 | GET /payment-network-participants/{id}             | Devuelve mecanismos y versiones.                                                  |
| CI-05-032 | POST /payment-exchanges                            | Operación, mecanismo y actuación tipada. Devuelve referencia estable y resultado. |
| CI-05-033 | POST /payment-network-messages                     | Mensaje, origen y evidencia. Devuelve incorporación o inconsistencia.             |
| CI-05-034 | GET /payment-exchanges/{id}                        | Devuelve mensajes, estados y evidencias.                                          |
| CI-05-035 | POST /payment-reconciliations                      | Fuentes y cortes. Devuelve coincidencias y diferencias.                           |
| CI-05-035 | POST /payment-differences/{id}/resolutions         | Conclusión, aprobación y tratamiento. Devuelve efectos comprobados o pendientes.  |
| CI-05-036 | POST /clearing-cycles/{id}/determinations          | Corte y reglas. Devuelve obligaciones y composición.                              |
| CI-05-036 | GET /clearing-obligations/{id}                     | Devuelve detalle y liquidación.                                                   |
| CI-05-037 | POST /clearing-obligations/{id}/settlement-results | Resultado de Tesorería y evidencia. Devuelve importe liquidado y remanente.       |

Las actuaciones con varias modalidades utilizan esquemas excluyentes. No aceptan campos arbitrarios para ejecutar
comportamientos no definidos.

### 13.6. Respuestas HTTP

Se utilizan 200, 201 y 202 para consulta, creación confirmada y trabajo persistido pendiente.

Los errores utilizan Problem Details:

- 400: estructura o tipos inválidos.

- 401 y 403: autenticación o permisos.

- 404: recurso inexistente o no visible.

- 409: versión, identidad o contenido incompatible.

- 422: impedimento de negocio sin ejecución.

- 503: dependencia indisponible antes de aceptar el trabajo.

Un resultado desfavorable de una evaluación completada puede formar parte de una respuesta satisfactoria de consulta.

## 14. Eventos

El sobre incluye evento, esquema, emisor, agregado, versión, actuación, operación, causa, correlación y fechas.

| Contrato  | Evento                                 | Contenido                                                                |
|-----------|----------------------------------------|--------------------------------------------------------------------------|
| CI-05-038 | TransferChanged                        | Instrucción, hechos confirmados, movimientos, devoluciones y pendientes. |
| CI-05-039 | PaymentOrReversalChanged               | Pago, ejecución, aplicación, restitución y resolución.                   |
| CI-05-040 | MandateOrScheduleChanged               | Objeto, versión, alcance, vigencia y efecto.                             |
| CI-05-041 | OccurrenceOutcomeRecorded              | Ocurrencia, instrucción, importe y resultado.                            |
| CI-05-042 | ChequeChanged                          | Instrumento, secuencia, tenedor, compromiso, presentación y resultado.   |
| CI-05-043 | PaymentExchangeOrReconciliationChanged | Intercambio o diferencia, evidencia y resultado normalizado.             |
| CI-05-044 | ClearingObligationChanged              | Ciclo, obligación, composición, ajustes y liquidación.                   |

Los eventos no representan otro movimiento monetario.

Los temas son:

- banking.transfer.events.

- banking.payment.events.

- banking.mandate.events.

- banking.cheque.events.

- banking.payment-clearing.events.

La clave corresponde al agregado. No se presupone orden global entre servicios.

Los consumidores comprueban identidad del hecho, versión y discontinuidades antes de aplicarlo.

## 15. Persistencia y consistencia

### 15.1. Estructuras principales

| Servicio       | Persistencia                                                                                 |
|----------------|----------------------------------------------------------------------------------------------|
| Transferencias | Instrucciones, destinos, controles, cargos, efectos, cancelaciones y devoluciones.           |
| Pagos          | Convenios, obligaciones de referencia, órdenes, preparaciones, aplicaciones y restituciones. |
| Mandatos       | Versiones, aceptaciones, programaciones, ocurrencias y expedientes de reversión.             |
| Cheques        | Chequeras, instrumentos, cadena, avales, compromisos, medidas y presentaciones.              |
| Compensación   | Participantes, mecanismos, intercambios, mensajes, diferencias, ciclos y obligaciones.       |

Todos mantienen actuaciones, idempotencia, outbox, inbox y trabajos persistentes cuando corresponda.

### 15.2. Restricciones de integridad

Se exige:

- Instrucción única por servicio y referencia de origen.

- Referencia externa única por participante y mecanismo.

- Ocurrencia programada única por posición de la serie.

- Presentación de acreedor única por referencia de cobro.

- Una instrucción efectiva por ocurrencia.

- Un compromiso incompatible activo por cheque.

- Una cadena confirmada sin bifurcaciones.

- Pago único por instrumento.

- Hecho compensable incluido una sola vez.

- Obligación única por identidad de determinación.

- Restituciones dentro del importe autorizado pendiente.

Las comprobaciones concurrentes utilizan bloqueos locales, versiones y restricciones únicas.

## 16. Transacciones y recuperación

Cada servicio confirma conjuntamente:

- Cambio de agregado.

- Historial.

- Resultado de actuación.

- Idempotencia.

- Outbox.

No mantiene transacciones SQL abiertas durante llamadas HTTP o Kafka.

Los procesos entre servicios conservan intención antes de invocar, referencia remota, resultado, intentos y próximo
control.

Una respuesta perdida exige consultar la referencia original.

La misma clave con igual contenido recupera el resultado. Con contenido diferente produce conflicto.

Además se verifica la identidad del efecto económico, aunque cambie la clave técnica.

Los consumidores registran inbox y efecto local en una transacción antes de confirmar el offset.

Las correcciones agregan actuaciones vinculadas. No sobrescriben los hechos anteriores.

## 17. Seguridad y tecnologías complementarias

Keycloak autentica usuarios y servicios.

Los permisos distinguen:

- Originar instrucciones.

- Transmitir autorizaciones.

- Administrar convenios.

- Aprobar habilitaciones.

- Administrar mandatos.

- Incorporar mensajes externos.

- Resolver instrumentos.

- Preparar y aprobar diferencias.

- Determinar compensación.

- Informar liquidaciones.

Se verifica autorización por objeto, finalidad y etapa.

Un adaptador externo no puede informar resultados de cualquier participante. Su identidad técnica queda vinculada a los
mecanismos habilitados.

Redis puede almacenar información inmutable de versiones de convenios o mecanismos. No administra dinero, idempotencia,
mandatos vigentes, exclusiones de instrumentos ni decisiones de ejecución.

PostgreSQL conserva la autoridad transaccional.

Los documentos y evidencias binarias se administran mediante Documentos. No se introduce MongoDB para duplicar el
historial de operaciones.

## 18. Observabilidad y pruebas

### 18.1. Observabilidad

Se observan:

- Transferencias enviadas sin resultado.

- Reservas firmes o inciertas.

- Débitos sin aplicación.

- Cancelaciones pendientes.

- Ocurrencias omitidas.

- Restituciones próximas a vencer.

- Presentaciones inciertas.

- Discontinuidades de registro electrónico.

- Diferencias de conciliación.

- Hechos pendientes de compensación.

- Obligaciones sin liquidar.

- Retraso de outbox y recuperación.

No se utilizan personas, cuentas o instrucciones como etiquetas de métricas.

Las trazas correlacionan operación, actuación, intercambio y efecto sin registrar contenido sensible.

### 18.2. Pruebas de concurrencia

Se verifican con PostgreSQL real:

- Cancelación frente a despacho.

- Revocación frente a admisión de ocurrencia.

- Dos trabajadores sobre una programación.

- Dos pagos incompatibles sobre una obligación.

- Dos devoluciones sobre el mismo remanente.

- Restitución provisional frente a devolución definitiva.

- Transmisión frente a depósito o descuento.

- Dos presentaciones del mismo instrumento.

- Dos determinaciones del mismo ciclo.

### 18.3. Pruebas de recuperación

Se comprueban:

- Transferencia acreditada con respuesta perdida.

- Débito confirmado sin aplicación.

- Caída después de admitir una ocurrencia.

- Mensaje duplicado o contradictorio.

- Resultado externo tardío.

- Cancelación mientras existe envío en tránsito.

- Cheque pagado con confirmación perdida.

- Liquidación fallida después de acreditación.

- Reinicio durante una restitución.

- Evento pendiente después del commit.

### 18.4. Recorridos completos

Los recorridos incluyen:

- Transferencia interna con débito y crédito conjuntos.

- Transferencia externa con acreditación anterior a liquidación.

- Cancelación externa comprobada y liberación.

- Pago preparado, debitado y aplicado.

- Pago debitado con aplicación recuperada.

- Mandato variable con máximo.

- Serie mensual sin desplazamiento de anclaje.

- Reversión provisional seguida de devolución definitiva.

- ECHEQ transmitido y cobrado.

- Cheque descontado cuyo cobro se aplica a financiación.

- Conciliación con regularización autorizada.

- Compensación neta y liquidación parcial.

## 19. Correspondencia funcional

| Operación                                | Implementación                              | Contratos                  |
|------------------------------------------|---------------------------------------------|----------------------------|
| OF-05-01. Transferencia interna          | Instrucción y conjunto en Fondos.           | CI-05-001, 006 y 038.      |
| OF-05-02. Transferencia externa saliente | Reserva, intercambio y acreditación.        | CI-05-002, 006 y 038.      |
| OF-05-03. Transferencia entrante         | Referencia externa y crédito único.         | CI-05-003, 006 y 038.      |
| OF-05-04. Cancelación y devolución       | Cancelación ordenada y retorno vinculado.   | CI-05-004, 005 y 006.      |
| OF-05-05. Convenios                      | Versiones y habilitación.                   | CI-05-007 y 008.           |
| OF-05-06. Obligación de pago             | Consulta y confirmación del acreedor.       | CI-05-009.                 |
| OF-05-07. Ejecución y aplicación         | Preparación y aplicación definitiva.        | CI-05-010, 012 y 039.      |
| OF-05-08. Ajustes de pago                | Rectificación, anulación y devolución.      | CI-05-011, 012 y 039.      |
| OF-05-09. Mandato                        | Alcance, aceptación y versiones.            | CI-05-013 y 015.           |
| OF-05-10. Estado del mandato             | Suspensión, revocación y vencimiento.       | CI-05-014 y 040.           |
| OF-05-11. Ocurrencias                    | Programación y admisión única.              | CI-05-016, 017, 018 y 041. |
| OF-05-12. Reversión                      | Expediente y tratamiento económico.         | CI-05-019, 020 y 039.      |
| OF-05-13. Emisión                        | Chequeras e instrumentos.                   | CI-05-021 y 022.           |
| OF-05-14. Circulación                    | Cadena, transmisión y aval.                 | CI-05-023 y 042.           |
| OF-05-15. Depósito y presentación        | Oportunidad y destino del cobro.            | CI-05-024, 025 y 026.      |
| OF-05-16. Pago o rechazo                 | Resultado íntegro y financiación vinculada. | CI-05-027, 029 y 042.      |
| OF-05-17. Medidas y devoluciones         | Alcance y actuaciones del instrumento.      | CI-05-028, 029 y 042.      |
| OF-05-18. Participantes                  | Mecanismos y versiones.                     | CI-05-030 y 031.           |
| OF-05-19. Intercambio                    | Despacho, mensajes y recuperación.          | CI-05-032, 033 y 034.      |
| OF-05-20. Conciliación                   | Diferencias y resolución comprobada.        | CI-05-035 y 043.           |
| OF-05-21. Compensación                   | Composición y seguimiento de liquidación.   | CI-05-036, 037 y 044.      |
