# Operaciones funcionales — Módulo 5. Pagos y transferencias

## 1. Propósito y alcance

Este documento define las operaciones para trasladar fondos, cumplir obligaciones de pago, administrar instrucciones
futuras y procesar cheques y ECHEQ.

Comprende los microservicios Transferencias, Pagos, Mandatos y recurrencias, Cheques y ECHEQ, y Compensación y
conciliación de pagos.

Los participantes y mecanismos externos funcionan dentro de la simulación. Sus confirmaciones representan hechos
diferentes: recepción, aceptación, acreditación, aplicación y liquidación.

Fondos administra las reservas y los movimientos de las cuentas. Tesorería cumple las obligaciones externas de
liquidación. Los módulos acreedores conservan la deuda y la imputación de sus propios productos.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-05-01 | Ejecutar una transferencia interna | Transferencias |
| OF-05-02 | Enviar una transferencia externa | Transferencias |
| OF-05-03 | Recibir una transferencia externa | Transferencias |
| OF-05-04 | Cancelar o devolver una transferencia | Transferencias |
| OF-05-05 | Administrar convenios de recaudación | Pagos |
| OF-05-06 | Identificar una obligación de pago | Pagos |
| OF-05-07 | Ejecutar y aplicar un pago | Pagos |
| OF-05-08 | Anular, devolver o corregir un pago | Pagos |
| OF-05-09 | Constituir y modificar un mandato | Mandatos y recurrencias |
| OF-05-10 | Suspender, revocar o vencer un mandato | Mandatos y recurrencias |
| OF-05-11 | Programar y ejecutar ocurrencias | Mandatos y recurrencias |
| OF-05-12 | Tramitar la reversión de un débito | Mandatos y recurrencias y Pagos |
| OF-05-13 | Habilitar chequeras y emitir instrumentos | Cheques y ECHEQ |
| OF-05-14 | Administrar aceptación y circulación | Cheques y ECHEQ |
| OF-05-15 | Depositar y presentar al cobro | Cheques y ECHEQ |
| OF-05-16 | Resolver pago o rechazo | Cheques y ECHEQ |
| OF-05-17 | Administrar medidas y devoluciones de instrumentos | Cheques y ECHEQ |
| OF-05-18 | Administrar participantes y mecanismos | Compensación y conciliación de pagos |
| OF-05-19 | Intercambiar instrucciones y resultados | Compensación y conciliación de pagos |
| OF-05-20 | Conciliar y resolver diferencias | Compensación y conciliación de pagos |
| OF-05-21 | Determinar obligaciones de compensación | Compensación y conciliación de pagos |

## 3. Reglas comunes del módulo

### 3.1. Identidad y autorización

Cada instrucción conserva:

- Referencia única.

- Ordenante y beneficiario.

- Cuentas y participantes.

- Importe y moneda.

- Finalidad.

- Condiciones aceptadas.

- Fechas.

- Controles y facultades utilizados.

La misma referencia con contenido idéntico recupera el resultado anterior. Con contenido diferente genera una
inconsistencia.

Dos operaciones similares con referencias distintas no se consideran automáticamente duplicadas. Los controles pueden
exigir comprobaciones adicionales.

La autorización corresponde al contenido aceptado. Cambiar importe, moneda, destino o finalidad material exige revisar
las comprobaciones y obtener una nueva conformidad cuando corresponda.

### 3.2. Recursos y conceptos monetarios

Fondos determina disponibilidad, constituye reservas y ejecuta movimientos.

El principal se distingue de comisiones y tributos.

Las comisiones por ejecución se generan al confirmar la ejecución correspondiente. Un rechazo anterior no genera esa
comisión.

Los cargos de participantes distintos identifican su causa. Utilizar una transferencia para ejecutar un pago no permite
cobrar dos veces el mismo servicio.

Una devolución del principal no revierte automáticamente todos los conceptos. Si deriva de un error del banco, se
regularizan también sus consecuencias indebidas.

Las operaciones que admiten descubierto deben indicarlo expresamente. Las demás utilizan únicamente fondos propios.

### 3.3. Resultados diferenciados

| Resultado | Significado |
| --- | --- |
| Recibida | El banco conserva la instrucción. |
| Aceptada para ejecución | Se cumplieron los requisitos de esa etapa. |
| Enviada | Existe constancia de salida al mecanismo externo. |
| Acreditada | Se confirmó el movimiento en destino. |
| Aplicada | El acreedor confirmó la imputación a la obligación. |
| Rechazada | Se comprobó que el efecto solicitado no se ejecutará. |
| Incierta | Falta comprobar un resultado relevante. |
| Devuelta | Se ejecutó una operación posterior de retorno. |
| Liquidada | Se cumplió la obligación financiera entre participantes. |

Un acuse de recepción no demuestra acreditación, aplicación ni liquidación.

### 3.4. Firmeza de los compromisos

Antes de enviar una instrucción externa, su reserva es cancelable mientras no exista otra actuación firme.

Después del envío, permanece incierta hasta comprobar aceptación o rechazo.

Cuando el participante acepta irrevocablemente ejecutar, el compromiso es firme.

Una reducción posterior de descubierto no libera compromisos firmes anteriores. Se aplica la cobertura protegida
administrada por Fondos.

Las restricciones posteriores se evalúan según su alcance y momento de efecto.

### 3.5. Recuperación

Ante incertidumbre:

- Se conserva la referencia original.

- Se consulta el resultado.

- Se mantiene la cobertura necesaria.

- Se contrastan registros.

- Se completa, cancela o corrige únicamente con evidencia suficiente.

Los mecanismos externos simulados utilizan consultas:

- Al detectar la incertidumbre.

- A los cinco minutos.

- A los treinta minutos.

Si persiste, se abre investigación y se revisa en cada jornada operativa.

El transcurso de esos tiempos no autoriza a liberar recursos ni a declarar rechazada una operación.

## 4. Mecanismos operativos

### 4.1. Transferencia interna

Funciona todos los días.

Fondos confirma conjuntamente débito y crédito. No existe liquidación externa.

Una respuesta satisfactoria demuestra ambos movimientos.

### 4.2. Transferencia externa inmediata

Funciona todos los días.

El emisor reserva antes de enviar. El receptor:

- Identifica participante y referencia.

- Comprueba destino y controles.

- Confirma aceptación.

- Acredita una sola vez.

- Comunica el resultado.

El emisor consume la reserva al comprobar la acreditación.

Si el receptor aceptó pero todavía no acreditó, el compromiso permanece firme. Si se desconoce el resultado, permanece
incierto.

Un rechazo definitivo debe comprobar que no existe una acreditación realizada ni una ejecución pendiente.

La liquidación posterior entre entidades no condiciona la disponibilidad de una acreditación confirmada.

### 4.3. Pago a recaudador

Se utiliza preparación y confirmación:

- Pagos obtiene una aceptación preliminar de la obligación.

- Fondos reserva el total autorizado.

- El recaudador registra una aplicación preparada.

- El banco confirma el débito.

- El recaudador confirma la aplicación definitiva.

Una aplicación preparada no cancela todavía la deuda.

Si existe débito confirmado sin aplicación, Pagos conserva la obligación de obtener aplicación o devolución, sin cobrar
nuevamente.

El convenio no se habilita si el recaudador simulado no permite consultar y resolver esas situaciones.

### 4.4. Débito autorizado

Cada cobro identifica mandato y ocurrencia.

El débito se realiza una sola vez. Su aplicación a una obligación se confirma con el acreedor o módulo correspondiente.

Aceptar un archivo no demuestra que todas sus ocurrencias fueron cobradas.

### 4.5. Compensación

El corte diario es a las 18:00, en America/Argentina/Buenos_Aires.

- Las operaciones posteriores integran el siguiente ciclo.

- Los instrumentos sujetos a días hábiles utilizan su calendario.

- Las monedas se mantienen separadas.

- El neteo requiere acuerdo expreso.

- Tesorería ejecuta la liquidación.

- Un fallo de liquidación no borra operaciones confirmadas de clientes.

## 5. Reglas de cheques y ECHEQ

### 5.1. Modalidades comprendidas

La simulación admite:

| Característica | Modalidades |
| --- | --- |
| Soporte | Papel simulado y ECHEQ. |
| Clase | Común y de pago diferido. |
| Beneficiario | Nominativo e identificado. |
| Circulación | A la orden o no a la orden. |
| Moneda | Pesos o dólares, sin conversión dentro del instrumento. |
| Pago | Total. |

La cuenta libradora debe ser una cuenta corriente habilitada para cheques en la moneda correspondiente.

No se ofrecen cheques al portador, endosos en blanco, certificación de fondos ni pagos parciales.

Las reglas temporales y de circulación siguientes son las convenciones adoptadas para estos instrumentos simulados.

### 5.2. Fechas y presentación

Cheque común

- La fecha de emisión no puede ser futura.

- Puede presentarse desde su fecha de emisión.

- El plazo de presentación finaliza treinta días corridos después de esa fecha.

Cheque de pago diferido

- Identifica fecha de emisión y fecha de pago.

- La fecha de pago debe ser posterior a la emisión.

- La separación máxima es de 365 días corridos.

- Puede presentarse desde su fecha de pago.

- El plazo finaliza treinta días corridos después de la fecha de pago.

Para ambas clases:

- El día de referencia se excluye del cómputo de los treinta días.

- La fecha inicial y la fecha final de presentación son admisibles.

- Si el último día es inhábil de compensación, se extiende hasta el siguiente hábil.

- La presentación se considera oportuna por su recepción validada en el banco depositario.

- Una demora posterior del banco no convierte una presentación oportuna en vencida.

- Un instrumento presentado oportunamente conserva su seguimiento hasta resolver el cobro.

- Fuera de plazo no se inicia un nuevo cobro ordinario.

Recibir anticipadamente un cheque diferido permite conservarlo para presentación futura. No equivale a presentarlo ni
pagarlo antes de tiempo.

### 5.3. Recepción y cortes

Los instrumentos físicos se reciben en días hábiles hasta las 17:00.

Los recibidos fuera del horario quedan registrados para validación en el siguiente hábil. Esa recepción fuera de horario
no se presenta como depósito validado dentro del plazo.

Los ECHEQ pueden recibirse durante todo el día. Una recepción validada conserva su fecha y hora, aunque el intercambio
se procese en el siguiente ciclo hábil.

Un instrumento físico validado antes de las 17:00 puede incorporarse al ciclo de ese día. Los ECHEQ validados antes de
las 18:00 pueden incorporarse al ciclo hábil de ese día; los posteriores pasan al siguiente.

### 5.4. Legitimación y circulación

Cada instrumento conserva un único tenedor legitimado.

En instrumentos a la orden:

- El tenedor puede transmitir mediante endoso nominativo.

- El receptor debe estar identificado.

- No se admiten transmisiones parciales.

- No se establece un máximo comercial de endosos.

- Cada transmisión conserva origen, destino, facultades y momento.

En instrumentos no a la orden:

- No se admite transmisión mediante endoso.

- Una cesión requiere acuerdo documentado, aceptación del adquirente y reconocimiento por Cheques y ECHEQ.

- La cesión no queda efectiva antes de completar esas comprobaciones.

Mientras una transmisión esté pendiente de aceptación, el instrumento queda comprometido y el transmitente no puede
depositarlo ni transmitirlo por otra vía.

El rechazo o cancelación comprobada de la transmisión restituye su disponibilidad al tenedor anterior.

Una vez depositado, comprometido para descuento o presentado al cobro, no admite circulación incompatible.

### 5.5. Aceptación, aval y rechazo

La aceptación del beneficiario acredita recepción del instrumento; no confirma fondos ni pago.

Un aval identifica:

- Avalista.

- Obligado garantizado.

- Instrumento.

- Alcance total de cobertura.

- Evidencia de aceptación.

El aval no reserva dinero ni produce pago. No se ofrecen avales parciales ni el otorgamiento de avales propios del banco
mediante este circuito.

Las obligaciones derivadas de un rechazo se conservan como antecedentes. Su recuperación no genera un débito automático
sobre cuentas de libradores, endosantes o avalistas.

Después de un rechazo:

- No se repite automáticamente la presentación.

- Puede recibirse otra instrucción de presentación dentro del plazo si la causa fue insuficiencia de fondos y no existe
  otro impedimento.

- Una medida vigente, defecto formal no resuelto o resultado incierto impide esa nueva presentación.

- Cada intento conserva referencia propia y vínculo con el instrumento.

- Un pago confirmado impide cualquier nuevo cobro del mismo cheque.

### 5.6. Relación con el descuento

El descuento es una financiación administrada por Préstamos y líneas.

Cheques y ECHEQ:

- Comprueba instrumento y legitimación.

- Registra el compromiso para descuento.

- Impide su depósito o transmisión por otra vía.

- Confirma la cesión al banco cuando se formaliza la financiación.

- Gestiona la presentación al cobro.

- Informa pago o rechazo a Préstamos y líneas.

El anticipo se acredita mediante la operación de financiación. El cobro posterior del cheque se aplica a esa
financiación y no vuelve a acreditarse como otro ingreso libre del cliente.

Si el cheque es rechazado, Préstamos y líneas conserva y trata la obligación de financiación. Cheques y ECHEQ conserva
el rechazo y las gestiones del instrumento.

## 6. OF-05-01. Ejecutar una transferencia interna

Responsable: Transferencias.

### Información y condiciones previas

Ordenante, origen, destino, importe positivo, moneda, autorización y controles.

### Desarrollo

- Identificar instrucción.

- Resolver destino y presentarlo al ordenante.

- Comprobar facultades, restricciones y límites.

- Solicitar ejecución a Fondos.

- Confirmar débito y crédito.

- Emitir resultado.

### Reglas

- Las cuentas propias también requieren autorización.

- El destino queda fijado al aceptar.

- No se admite la misma cuenta como origen y destino.

- Las monedas distintas requieren una operación cambiaria coordinada.

- Principal, comisión y tributos se identifican por separado.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Origen 100, destino 20 y transferencia 30 | Origen 70 y destino 50, sin cargos adicionales. |
| Capacidad insuficiente | Rechazo sin traslado parcial. |
| Respuesta perdida | Recuperación del resultado original. |
| Alias modificado después de aceptar | Se conserva el destino aceptado. |

## 7. OF-05-02. Enviar una transferencia externa

Responsable: Transferencias.

### Desarrollo

- Comprobar destino, autorización y controles.

- Reservar principal y cargos autorizados.

- Enviar por el mecanismo simulado.

- Clasificar aceptación, acreditación o rechazo.

- Consumir la reserva al comprobar acreditación.

- Informar la operación a compensación.

- Comunicar y seguir pendientes.

### Reglas

- Un acuse no demuestra acreditación.

- La aceptación firme conserva cobertura.

- La cancelación posterior al envío requiere confirmación externa.

- Las devoluciones son operaciones posteriores.

- Una obligación externa se incorpora una sola vez a compensación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Acreditación confirmada por 70 | Débito único por 70 y resultado acreditado. |
| Rechazo comprobado sin efectos | Liberación de reserva. |
| Falta de respuesta | Reserva incierta y recuperación. |
| Falla de liquidación posterior | La acreditación permanece confirmada. |

## 8. OF-05-03. Recibir una transferencia externa

Responsable: Transferencias.

### Desarrollo

- Identificar participante y referencia.

- Comprobar repetición.

- Validar destino, moneda y controles.

- Aceptar o rechazar.

- Acreditar mediante Fondos.

- Confirmar resultado.

- Informar a compensación.

### Reglas

- La referencia es única dentro del participante y mecanismo.

- No se sustituye un destino inválido.

- Una restricción se interpreta según permita o impida créditos.

- Después de aceptar de forma firme, una imposibilidad exige resolución o devolución.

- Los importes sin destino acreditable conservan obligación y responsable.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Misma transferencia recibida dos veces | Una acreditación. |
| Cuenta inexistente | Rechazo antes de aceptar. |
| Cuenta restringida solo para débitos | Se evalúa el crédito conforme al alcance real. |
| Crédito confirmado con respuesta perdida | Se recupera la confirmación existente. |

## 9. OF-05-04. Cancelar o devolver una transferencia

Responsable: Transferencias.

### Desarrollo

- Registrar petición y motivo.

- Comprobar avance.

- Distinguir cancelación de devolución.

- Obtener confirmaciones y facultades.

- Coordinar movimientos.

- Informar resultado y remanentes.

### Reglas

- Antes del envío puede cancelarse una instrucción no ejecutada.

- Después del envío debe comprobarse la cancelación externa.

- Una transferencia acreditada retorna mediante devolución autorizada o fundamento suficiente.

- La petición del ordenante no autoriza a debitar al beneficiario.

- Se admiten devoluciones parciales.

- No se devuelve más que el principal pendiente de devolución.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cancelación antes del envío | No se envía y se libera cobertura. |
| Petición después de acreditar | Se tramita devolución. |
| Principal 100 y devolución anterior 40 | Quedan como máximo 60 por devolver. |
| Devolución repetida | Sin segundo efecto. |

## 10. OF-05-05. Administrar convenios de recaudación

Responsable: Pagos.

### Desarrollo

- Identificar participante y contrato.

- Registrar servicios y referencias.

- Definir validación de obligaciones.

- Seleccionar modalidades admitidas.

- Comprobar preparación, confirmación y recuperación.

- Obtener aprobación separada.

- Habilitar.

### Modalidades

El convenio selecciona expresamente:

- Pago total.

- Pago parcial, si el acreedor admite conservar saldo.

- Pago posterior al vencimiento, con importe actualizado.

- Pago anterior al vencimiento.

La ausencia de selección impide ofrecer la modalidad.

### Reglas

- No se habilita sin tratamiento de pagos incompletos.

- Los cambios conservan versión.

- La baja impide nuevos pagos y mantiene resolución de pendientes.

- El importe confirmado por el acreedor se presenta antes de autorizar.

- Un cambio de importe requiere nueva aceptación.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| No se definió si admite parcialidad | No se ofrece pago parcial. |
| Falta recuperación de débitos inciertos | No se habilita el convenio. |
| Convenio retirado con pagos pendientes | Continúa su resolución. |

## 11. OF-05-06. Identificar una obligación de pago

Responsable: Pagos.

### Desarrollo

- Comprobar convenio.

- Consultar o recibir obligación.

- Identificar acreedor, obligado, referencia, importe y vencimiento.

- Validar información.

- Presentar modalidades y total.

### Reglas

- Consultar no reserva ni cancela deuda.

- Las referencias manuales se distinguen de datos confirmados.

- Pagar por un tercero no concede acceso general.

- Las obligaciones externas conservan fuente y momento.

- Préstamos y tarjetas mantienen su deuda en sus módulos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Referencia inexistente | No se sustituye. |
| Importe cambia de 100 a 110 antes de autorizar | Se presenta 110 para aceptación. |
| Parcialidad prohibida | No se ofrece. |

## 12. OF-05-07. Ejecutar y aplicar un pago

Responsable: Pagos.

### Desarrollo

- Confirmar obligación y autorización.

- Obtener aceptación preliminar.

- Reservar recursos.

- Confirmar aplicación preparada.

- Ejecutar el instrumento de pago.

- Obtener aplicación definitiva.

- Conciliar y emitir comprobante.

### Reglas

- Principal, comisión y tributos se desglosan antes de autorizar.

- Un pago parcial exige modalidad admitida.

- Si utiliza Transferencias, no duplica su movimiento.

- Si utiliza tarjeta, autorización y cargo pertenecen a Tarjetas.

- La imputación sobre deuda propia corresponde al módulo acreedor.

- Un débito sin aplicación exige resolver, sin cobrar nuevamente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Deuda 100 y pago parcial admitido por 40 | Aplicación 40 y saldo restante identificado. |
| Débito confirmado sin respuesta del acreedor | Pendiente de aplicación, sin otro débito. |
| Preparación cancelada antes de ejecutar | No se cobra y se libera cobertura. |
| Pago repetido | Se recupera su resultado. |

## 13. OF-05-08. Anular, devolver o corregir un pago

Responsable: Pagos.

### Desarrollo

- Comprobar ejecución y aplicación.

- Determinar actuación posible.

- Obtener intervención del acreedor.

- Coordinar devolución o rectificación.

- Confirmar efectos.

- Comunicar.

### Reglas

- Corregir una referencia no exige mover dinero si basta una rectificación del acreedor.

- Un pago aplicado no se elimina.

- La devolución puede restablecer deuda bajo la autoridad del acreedor.

- Los conceptos adicionales reciben tratamiento propio.

- Una devolución confirmada conserva seguimiento hasta acreditarse.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Referencia errónea corregible sin devolución | Se rectifica sin segundo cobro. |
| Devolución externa confirmada | Se acredita una sola vez. |
| Pago parcialmente devuelto | Se conserva remanente y consecuencia sobre la deuda. |

## 14. OF-05-09. Constituir y modificar un mandato

Responsable: Mandatos y recurrencias.

### Información y desarrollo

Otorgante, beneficiario, cuenta, finalidad, modalidad, moneda, importe o máximo, frecuencia y vigencia.

- Comprobar facultades.

- Presentar alcance.

- Obtener aceptación.

- Registrar mandato.

- Confirmar habilitación.

- Comunicar.

### Reglas

- El importe puede ser fijo o variable con máximo por ocurrencia.

- Un máximo no definido impide habilitar cobros variables.

- No se fracciona un mismo cobro para eludir el máximo.

- Ampliar importe, frecuencia o alcance exige aceptación.

- Cambiar beneficiario requiere una autorización nueva.

- El mandato no reserva fondos para toda su duración.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Máximo 100 y cobro 120 | No se ejecuta por ese mandato. |
| Aumento del máximo | Nueva aceptación. |
| Cambio de beneficiario | Nuevo mandato. |
| Activación incierta | No se informa habilitado. |

## 15. OF-05-10. Suspender, revocar o vencer un mandato

Responsable: Mandatos y recurrencias.

### Desarrollo

- Registrar solicitud o vencimiento.

- Comprobar actor y momento.

- Impedir nuevas instrucciones alcanzadas.

- Identificar instrucciones anteriores.

- Solicitar cancelaciones cuando proceda.

- Confirmar resultado.

### Reglas

- Suspender impide nuevas ocurrencias.

- Reanudar requiere instrucción del otorgante y vigencia.

- Después de revocar se requiere otro mandato para nuevos cobros.

- Los pagos anteriores no se revierten automáticamente.

- La deuda subyacente permanece en el acreedor.

- Las instrucciones enviadas siguen su procedimiento de cancelación o devolución.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Revocación anterior a generar ocurrencia | No se origina el cobro. |
| Revocación posterior al envío | Se solicita cancelación y se comprueba resultado. |
| Mandato suspendido | No genera nuevas instrucciones. |

## 16. OF-05-11. Programar y ejecutar ocurrencias

Responsable: Mandatos y recurrencias.

### Desarrollo

- Registrar anclaje, frecuencia, calendario, ajuste y finalización.

- Generar ocurrencias identificadas.

- Comprobar autorización.

- Confirmar importe.

- Originar una instrucción.

- Seguir el resultado.

### Reglas

- Horario ordinario: 09:00 de la zona institucional.

- Una transferencia única puede señalar otra hora.

- El ajuste de una fecha no desplaza el anclaje de las siguientes.

- No se acumulan cuotas omitidas sin autorización.

- No hay nuevos intentos automáticos por insuficiencia de fondos.

- El cliente puede ordenar después otro intento identificado.

- Las consultas de una ejecución incierta no son nuevos cobros.

### Recuperación

- Recuperación dentro de la fecha prevista: se procesa una vez si conserva requisitos.

- Recuperación en una fecha posterior: la transferencia o pago programado requiere confirmación del ordenante.

- Débito presentado por acreedor omitido: se informa el resultado; el acreedor debe presentar otra ocurrencia válida.

- La ocurrencia omitida conserva esa condición y no se registra como ejecutada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Interrupción a las 09:00 y recuperación a las 14:00 | Ejecución única si conserva requisitos. |
| Recuperación al día siguiente | No se ejecuta silenciosamente una transferencia atrasada. |
| Serie mensual anclada el día 30 | Febrero no desplaza marzo al día 28. |
| Fondos insuficientes | Resultado fallido, sin reintentos ocultos. |

## 17. OF-05-12. Tramitar la reversión de un débito

Responsables: Mandatos y recurrencias vincula autorización y ocurrencia; Pagos administra la reversión económica.

### Plazo adoptado

Se reciben solicitudes dentro de treinta días corridos desde el débito.

El día inicial se excluye. Un vencimiento inhábil se traslada al siguiente hábil.

Comprobada la identidad del solicitante, sus facultades y la correspondencia del débito, se realiza restitución
provisional como máximo al finalizar el tercer día hábil posterior a la recepción.

### Desarrollo

- Registrar solicitud y fecha.

- Identificar débito.

- Comprobar facultades y plazo.

- Revisar devoluciones anteriores.

- Tramitar con el acreedor o módulo responsable.

- Ejecutar restitución provisional.

- Resolver y comunicar.

### Reglas

- Revertir no revoca el mandato.

- La deuda subyacente no desaparece automáticamente.

- Fuera del plazo se recibe reclamo sin prometer reversión automática.

- Una objeción del acreedor no retira por sí sola el crédito provisional.

- La devolución definitiva regulariza el provisional.

- El recupero de un provisional improcedente requiere resolución y no concede descubierto.

- Si el débito pagó deuda de préstamo o tarjeta, su módulo acreedor determina la reapertura de los conceptos
  correspondientes.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Ya existe devolución total | Sin nueva acreditación. |
| Acreedor devuelve después del provisional | Se regulariza el mismo importe. |
| Se revierte el débito | El mandato continúa salvo instrucción separada. |
| Era pago de tarjeta | Cuenta de tarjeta determina la deuda resultante, sin duplicarla. |

## 18. OF-05-13. Habilitar chequeras y emitir instrumentos

Responsable: Cheques y ECHEQ.

### Desarrollo

- Comprobar cuenta corriente y facultades.

- Asignar numeración.

- Registrar entrega de chequera o emisión electrónica.

- Validar beneficiario, importe, moneda, clase y fechas.

- Determinar circulación admitida.

- Confirmar emisión.

### Reglas

- Entregar una chequera no emite todos sus cheques.

- Los datos físicos se registran cuando son conocidos y se verifican al depositar.

- Emitir no reserva fondos ni concede descubierto.

- Un ECHEQ conserva su contenido emitido; una modificación esencial exige cancelar válidamente y emitir otro.

- No se emite con modalidades fuera del alcance definido.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Chequera entregada sin instrumentos librados | Sin débitos ni reservas. |
| Emisión repetida de ECHEQ | Un instrumento. |
| Diferido con pago a más de 365 días | No se emite. |
| Cuenta sin habilitación | No se emite electrónicamente. |

## 19. OF-05-14. Administrar aceptación y circulación

Responsable: Cheques y ECHEQ.

### Desarrollo

- Identificar instrumento y tenedor.

- Comprobar facultades.

- Validar modalidad de circulación.

- Registrar propuesta y comprometer el instrumento.

- Obtener aceptación del receptor.

- Confirmar transmisión.

- Actualizar cadena y legitimación.

### Reglas

- La aceptación no demuestra cobro.

- Los endosos son nominativos y totales.

- Una cesión exige documentación y reconocimiento.

- No se admiten transmisiones simultáneas incompatibles.

- En papel se conserva evidencia de la cadena presentada.

- En ECHEQ se utiliza la secuencia confirmada por el registro simulado.

- Los avales no cambian el tenedor ni acreditan dinero.

- El rechazo de una transmisión no borra las anteriores.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Instrumento a la orden transmitido válidamente | Nuevo tenedor identificado. |
| Instrumento no a la orden enviado como endoso | Se rechaza esa modalidad. |
| Dos transmisiones concurrentes | Solo una puede confirmarse. |
| Receptor rechaza la transmisión | Se libera el compromiso del tenedor anterior. |
| Instrumento comprometido para descuento | No se transmite por otra vía. |

## 20. OF-05-15. Depositar y presentar al cobro

Responsable: Cheques y ECHEQ.

### Desarrollo

- Recibir instrumento y destino.

- Comprobar legitimación y datos.

- Validar fechas y oportunidad.

- Revisar presentaciones anteriores.

- Comprometer el instrumento para cobro.

- Presentar en el ciclo correspondiente.

- Seguir aceptación y resultado.

### Reglas

- La recepción anticipada del diferido conserva la fecha prevista.

- No se presenta antes de su fecha de pago.

- La recepción validada dentro del plazo preserva oportunidad.

- Una presentación incierta impide originar otra.

- No hay disponibilidad por el solo depósito.

- El anticipo exige financiación separada.

- El destino del cobro distingue cuenta del tenedor y financiación por descuento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Común emitido el 1 de junio | Presentable desde el 1 de junio hasta el 1 de julio, con ajuste final si corresponde. |
| Diferido con pago el 10 de junio | No se presenta antes; su plazo ordinario termina el 10 de julio, con ajuste final si corresponde. |
| Depósito oportuno y demora interna posterior | No se rechaza por vencimiento causado por esa demora. |
| Presentación duplicada | No se inicia otro cobro. |
| Instrumento descontado | El cobro se vincula a la financiación. |

## 21. OF-05-16. Resolver pago o rechazo

Responsable: Cheques y ECHEQ.

### Desarrollo

- Comprobar presentación y legitimación.

- Revisar cuenta y medidas.

- Validar importe íntegro.

- Solicitar movimientos o intercambio externo.

- Comprobar pago o rechazo.

- Comunicar al tenedor o a Préstamos y líneas.

- Incorporar compensación cuando corresponda.

### Reglas

- Se procesa pago total o rechazo.

- No se recorta el pago por insuficiencia.

- Un resultado monetario incierto no es rechazo definitivo.

- Un rechazo conserva causal y fecha.

- Un pago confirmado impide otro pago del instrumento.

- En cobros externos, la confirmación de pago del participante girado genera la obligación interbancaria
  correspondiente.

- El cobro de un cheque descontado se aplica mediante Préstamos y líneas.

- El rechazo no elimina la deuda de financiación ni las obligaciones del instrumento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cheque 100 y capacidad 80 | No se debitan 80 como pago parcial. |
| Pago confirmado | No puede cobrarse otra vez. |
| Respuesta perdida | Se consulta antes de rechazar o repetir. |
| Cheque descontado pagado | Se informa a Préstamos y líneas sin otra acreditación libre al cliente. |
| Cheque descontado rechazado | Se conserva financiación y se informa la causal. |

## 22. OF-05-17. Administrar medidas y devoluciones de instrumentos

Responsable: Cheques y ECHEQ.

### Desarrollo

- Registrar solicitud, denuncia o medida.

- Comprobar fundamento y autoridad.

- Coordinar con Restricciones.

- Identificar circulación y cobros afectados.

- Aplicar actuación.

- Obtener confirmaciones.

- Emitir constancias.

### Reglas

- Una denuncia no demuestra por sí sola invalidez.

- Una orden de no pagar requiere causa y autoridad.

- La cancelación por acuerdo exige conformidad del librador y del tenedor actual.

- No se cancela por acuerdo mientras exista presentación incierta.

- El instrumento devuelto al librador no se vuelve pagado por esa devolución.

- La devolución física o electrónica se distingue de una devolución monetaria.

- Después de un pago confirmado, la devolución de dinero utiliza una operación vinculada; no reactiva el cheque.

- El cierre de cuenta no elimina instrumentos pendientes.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Orden válida anterior al pago | Se impide el efecto alcanzado. |
| Denuncia posterior al pago | Se investiga sin borrar el pago. |
| Librador pide cancelar sin conformidad del tenedor | No se cancela por acuerdo. |
| Devolución del soporte | Se conserva constancia y destinatario. |

## 23. OF-05-18. Administrar participantes y mecanismos

Responsable: Compensación y conciliación de pagos.

### Desarrollo

- Registrar participante y acuerdo.

- Seleccionar mecanismo admitido.

- Definir monedas, calendarios y referencias.

- Comprobar consultas y recuperación.

- Establecer compensación y liquidación.

- Obtener aprobación separada.

- Habilitar.

### Reglas

- Personas conserva identidad.

- Contratos conserva acuerdos.

- Pagos conserva convenios de recaudación.

- Este servicio administra el intercambio.

- Una suspensión mantiene consultas y resolución de pendientes.

- Una variante nueva requiere comportamiento definido antes de habilitarse.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| No permite consultar ejecuciones inciertas | No se habilita. |
| Participante suspendido | Se impiden nuevos envíos alcanzados y continúan pendientes. |
| Cambio de mecanismo | Las operaciones anteriores conservan su versión. |

## 24. OF-05-19. Intercambiar instrucciones y resultados

Responsable: Compensación y conciliación de pagos.

### Desarrollo

- Comprobar origen y contenido.

- Asignar o recuperar referencia externa.

- Enviar o recibir.

- Registrar constancias.

- Clasificar el resultado.

- Informar al responsable.

- Consultar pendientes.

### Reglas

- El transporte no demuestra ejecución.

- Las referencias identifican participante, mecanismo y actuación.

- Los mensajes repetidos no generan efectos nuevos.

- Los resultados contradictorios abren investigación.

- Este servicio no modifica directamente dinero.

- Las correcciones conservan mensajes anteriores.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Acuse técnico | Se informa recepción, no acreditación. |
| Confirmación duplicada | Se conserva un mismo hecho. |
| Respuesta sin referencia conocida | No se aplica a otra operación. |
| Confirmación tardía | Se integra al seguimiento original. |

## 25. OF-05-20. Conciliar y resolver diferencias

Responsable: Compensación y conciliación de pagos.

### Desarrollo

- Reunir registros con cortes comparables.

- Relacionar referencias.

- Comparar importes, monedas y resultados.

- Clasificar diferencias.

- Consultar participantes.

- Determinar evidencia.

- Solicitar regularizaciones.

- Confirmar consecuencias.

### Reglas

- Se concilia al terminar cada ciclo y ante incertidumbres.

- Se distinguen desfases, faltantes, duplicaciones y contradicciones.

- Conciliar no autoriza por sí solo un débito.

- Los ajustes manuales requieren aprobación separada.

- No se alteran registros para forzar coincidencia.

- La diferencia no se cierra mientras sus consecuencias carezcan de tratamiento.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Externo acreditó e interno sigue pendiente | Se completa el efecto original, sin reenviar. |
| Duplicación externa | Se investiga antes de regularizar. |
| Diferencia de cortes | Se comprueba el desfase. |
| Corrección monetaria necesaria | La ejecuta Fondos por instrucción válida del responsable. |

## 26. OF-05-21. Determinar obligaciones de compensación

Responsable: Compensación y conciliación de pagos.

### Desarrollo

- Fijar ciclo.

- Seleccionar hechos comprendidos.

- Comprobar ajustes y duplicaciones.

- Determinar obligaciones por participante y moneda.

- Conservar composición.

- Entregar a Liquidaciones de tesorería.

- Registrar resultado y pendientes.

### Reglas

- Se utiliza el hecho económico previsto por el mecanismo.

- Transferencias: acreditaciones confirmadas y devoluciones.

- Pagos y débitos: ejecuciones que generan obligación entre participantes.

- Cheques: pagos confirmados por la entidad girada.

- No se compensa una reserva como si fuera pago.

- La incertidumbre de inclusión se identifica y se resuelve.

- El neteo requiere acuerdo; sin él se conservan obligaciones brutas.

- Los ajustes posteriores integran otro ciclo referenciado.

- La repetición no crea otra obligación.

- La falta de liquidación no revierte operaciones de clientes.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Obligación de pagar 100 y cobrar 70, misma contraparte y moneda, con neteo | Pago neto 30. |
| Obligaciones en monedas distintas | No se netean. |
| Repetición del cierre de ciclo | Se recupera la misma obligación. |
| Ajuste posterior 5 | Se incorpora al ciclo correspondiente con vínculo al original. |

## 27. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Instrucciones y resultados de transferencias | Transferencias |
| Referencias de obligaciones externas y aplicación de pagos | Pagos |
| Mandatos, programaciones y ocurrencias | Mandatos y recurrencias |
| Identidad, legitimación y circulación de cheques | Cheques y ECHEQ |
| Presentación, pago y rechazo de instrumentos | Cheques y ECHEQ |
| Intercambio y conciliación externa | Compensación y conciliación de pagos |
| Composición de compensación | Compensación y conciliación de pagos |
| Reservas y movimientos de cuentas | Fondos |
| Capacidades de cuentas e identificación bancaria | Cuentas e Identificación bancaria |
| Financiación por descuento e imputación de su cobro | Préstamos y líneas |
| Deuda e imputación de productos propios | Módulo acreedor |
| Autorizaciones y cargos con tarjeta | Tarjetas |
| Liquidación financiera externa | Liquidaciones de tesorería |
| Restricciones y controles especializados | Servicios correspondientes del módulo 11 |
| Registración contable | Contabilidad |
| Reclamos, documentos y comunicaciones | Servicios correspondientes del módulo 12 |

Cada operación conserva la relación entre autorización, compromiso, ejecución, aplicación y liquidación. La circulación
de instrumentos, las devoluciones, los cobros de financiación y las correcciones mantienen referencias suficientes para
evitar duplicar dinero, derechos u obligaciones.
