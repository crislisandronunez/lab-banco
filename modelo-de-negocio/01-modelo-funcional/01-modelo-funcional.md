# Modelo funcional de un banco argentino — definición del negocio

Este documento define cómo funciona el banco argentino simulado, con independencia de cómo se construya el software.
Toma como referencia las actividades de bancos comerciales como Banco Credicoop y Banco Macro, junto con el marco
normativo del Banco Central de la República Argentina (BCRA) y la Unidad de Información Financiera (UIF).

El banco distingue las obligaciones establecidas por normas de sus decisiones comerciales propias. Una política
particular de una entidad no se convierte en una obligación general para todos los bancos.

## 1. Qué banco estamos definiendo

El banco es una institución financiera argentina simulada que mantiene relaciones comerciales con personas y empresas;
recibe y administra depósitos; ofrece cuentas, medios de pago, ahorro, inversiones y financiación; registra sus
operaciones; administra su liquidez y sus riesgos; y cumple obligaciones de seguridad, información, control y atención a
sus clientes.

La simulación reproduce las reglas, decisiones y consecuencias económicas del negocio, sin manejar dinero real ni
consultar información financiera privada auténtica. Las relaciones con redes bancarias y participantes externos se
representan mediante mecanismos simulados.

### Qué hace el banco

Su actividad se organiza alrededor de tres operaciones económicas fundamentales:

- Captación: recibe fondos de clientes, creando obligaciones de devolución.

- Colocación: otorga financiación, generando derechos de cobro y riesgo crediticio.

- Prestación de servicios: facilita pagos, transferencias, tarjetas, inversiones, consultas, custodia de información y
  otras operaciones.

A estas actividades se suman la contabilidad, la administración de liquidez, el control de riesgos, la atención
comercial y el cumplimiento de obligaciones legales.

### Para quién trabaja

El banco atiende a personas humanas, empresas y otras organizaciones admitidas por sus políticas y por las reglas
aplicables.

Además, mantiene relaciones operativas con comercios, empleadores, entidades financieras, proveedores, organismos de
control y otros participantes del sistema financiero.

El modelo representa una institución financiera con obligaciones frente a sus clientes y con procesos internos que
sostienen su actividad.

## 2. Las 16 áreas del negocio bancario

Las siguientes áreas describen las actividades y reglas generales adoptadas para la simulación.

Cuando una condición depende del producto, del contrato o de una norma variable, se conserva esa dependencia y se
identifica su fundamento y vigencia.

### 1. Institución financiera

El banco debe tener identidad jurídica y comercial, una oferta de productos autorizados, una estructura de gobierno,
políticas institucionales, responsabilidades internas y reglas para operar.

Su alcance comprende recibir depósitos, otorgar créditos, prestar servicios de pago y administrar sus propias posiciones
financieras dentro de las actividades admitidas.

Debe distinguir entre patrimonio propio, dinero recibido de clientes, créditos otorgados y obligaciones con terceros.

También debe administrar riesgos de solvencia, liquidez, crédito, mercado y operación.

Sus decisiones institucionales abarcan aprobación de productos, condiciones comerciales, límites de exposición,
delegación de facultades, controles y tratamiento de incumplimientos.

No puede asumir obligaciones, prestar servicios o realizar operaciones fuera de su alcance autorizado.

En la simulación, las verificaciones regulatorias se reproducen con reglas y participantes ficticios.

### 2. Personas y clientes

Se distinguen los siguientes conceptos:

- Persona: ser humano u organización identificable.

- Cliente: persona que mantiene una relación comercial con el banco.

- Solicitante: persona que pide una relación o producto, aunque todavía no sea cliente.

- Titular: persona con derechos sobre un producto o contrato.

- Cotitular: persona que comparte esos derechos conforme a las condiciones contratadas.

- Representante o apoderado: persona facultada para actuar por otra, dentro de un alcance.

- Beneficiario final: persona humana cuya identificación corresponde al analizar una estructura jurídica conforme a las
  reglas aplicables.

Un cliente puede tener varios productos y una cuenta puede tener varios titulares.

Un representante no se convierte por ello en titular ni propietario de los fondos.

El ciclo funcional comprende:

Solicitud de vinculación → identificación → verificación de documentación → evaluación de admisión → aceptación o
rechazo → mantenimiento de datos y relaciones → revisiones periódicas → desvinculación cuando corresponda.

El banco contempla personas humanas, personas jurídicas, representantes, cotitulares y situaciones especiales como
menores de edad, fallecimiento, sucesiones o restricciones judiciales, aplicando las reglas propias de cada caso.

La vinculación exige conocer al cliente y su actividad, con controles proporcionales al riesgo.

Un cliente admitido puede necesitar verificaciones adicionales para contratar determinados productos.

### 3. Productos bancarios

Un producto es una oferta financiera con características, condiciones, derechos y obligaciones definidas.

El producto ofrecido y el contrato que un cliente celebra son conceptos diferentes.

El catálogo funcional del banco comprende:

| Familia                   | Productos y servicios contemplados                                                                 |
|---------------------------|----------------------------------------------------------------------------------------------------|
| Depósitos a la vista      | Caja de ahorro, cuenta sueldo, cuenta corriente y otras cuentas admitidas.                         |
| Ahorro a plazo            | Plazo fijo tradicional, precancelable y modalidades indexadas admitidas.                           |
| Financiación              | Préstamos personales, prendarios, hipotecarios y comerciales; adelantos y líneas de crédito.       |
| Tarjetas                  | Débito, crédito y tarjetas adicionales.                                                            |
| Pagos                     | Transferencias, servicios, impuestos, débitos autorizados y órdenes periódicas.                    |
| Instrumentos de pago      | Cheques y ECHEQ, según condiciones de la cuenta.                                                   |
| Inversiones               | Fondos comunes, bonos y otros instrumentos ofrecidos mediante las habilitaciones correspondientes. |
| Operaciones cambiarias    | Compra y venta de moneda extranjera cuando estén permitidas.                                       |
| Servicios complementarios | Extractos, certificaciones, avisos, gestión de reclamos y otros servicios comerciales.             |

Esta clasificación toma como referencia productos bancarios habituales, sin incorporar promociones particulares ni
asumir que todas las variantes deban contratarse.

Cada producto define elegibilidad, moneda, condiciones de contratación, vigencia, tasas, cargos, límites, restricciones,
modificación, cancelación y consecuencias sobre obligaciones pendientes.

Una modificación de la oferta comercial no cambia automáticamente las condiciones de contratos vigentes cuando no
corresponde.

### 4. Cuentas bancarias

Una cuenta representa una relación bancaria específica, con identificación, titulares, moneda, condiciones y operaciones
admitidas.

Se distinguen cuatro productos importantes:

| Cuenta                    | Comportamiento                                                                                        |
|---------------------------|-------------------------------------------------------------------------------------------------------|
| Caja de ahorro            | Permite mantener fondos disponibles y realizar operaciones admitidas. No tiene descubierto ordinario. |
| Cuenta sueldo             | Recibe remuneraciones y tiene condiciones específicas de gratuidad y operación.                       |
| Cuenta corriente          | Permite operar con transferencias y cheques, y utilizar un descubierto cuando está contratado.        |
| Cuenta Gratuita Universal | Modalidad de caja de ahorro con condiciones particulares de acceso y gratuidad.                       |

Las condiciones de gratuidad y elegibilidad se aplican según el producto y su régimen correspondiente.

La Cuenta Gratuita Universal conserva sus requisitos específicos.

El ciclo de una cuenta comprende:

Solicitud → verificación de elegibilidad y titulares → aceptación contractual → apertura → habilitación de operaciones →
funcionamiento → eventuales restricciones → cierre.

Las cuentas tienen identificación bancaria, como CBU y alias cuando corresponde.

La titularidad, los permisos de operación y la identificación de la cuenta son conceptos diferentes.

Reglas esenciales:

- No se puede retirar o comprometer más dinero del disponible, salvo financiación expresamente autorizada.

- El bloqueo de una operación no equivale necesariamente al cierre de la cuenta.

- Una cuenta puede mantener obligaciones pendientes aunque ya no admita nuevas operaciones.

- El cierre exige resolver o tratar expresamente saldos, operaciones pendientes, medios de pago y restricciones.

- Una cuenta no pierde su historia cuando deja de operar.

### 5. Administración del dinero

Este ámbito establece qué significa que el dinero ingrese, salga, se reserve, quede disponible o esté comprometido.

| Concepto              | Significado                                                                |
|-----------------------|----------------------------------------------------------------------------|
| Saldo contable        | Posición resultante de movimientos monetarios confirmados.                 |
| Saldo disponible      | Importe que puede utilizarse considerando restricciones y compromisos.     |
| Reserva               | Fondos temporalmente comprometidos para una operación determinada.         |
| Movimiento            | Efecto monetario registrado como resultado de un hecho bancario.           |
| Obligación pendiente  | Importe que debe pagarse o liquidarse conforme a una relación contractual. |
| Ajuste o compensación | Operación que corrige o contrarresta un efecto anterior sin ocultarlo.     |

Una operación no puede consumir dos veces los mismos fondos.

Una reserva no equivale todavía al pago definitivo.

Al convertir una reserva en cargo, el dinero no se descuenta dos veces.

Los depósitos, retiros, transferencias, pagos, intereses, comisiones y desembolsos producen consecuencias monetarias
identificables y verificables.

Las operaciones en distintas monedas conservan su denominación.

La conversión cambiaria exige una operación específica y una cotización aplicable. No se suman pesos y dólares como si
fueran la misma unidad.

Cuando una operación no tiene resultado conocido, el banco averigua qué sucedió antes de repetirla, devolver fondos o
darla por rechazada.

### 6. Contabilidad bancaria

Un depósito del cliente no es un ingreso del banco. Es una obligación del banco frente al depositante.

Un préstamo otorgado no es un gasto del banco por el importe del capital. Genera un derecho de cobro frente al
prestatario y una salida o puesta a disposición de fondos.

La contabilidad representa:

- Activos: efectivo, disponibilidades, préstamos por cobrar, inversiones y otros derechos.

- Pasivos: depósitos, deudas financieras y otras obligaciones.

- Patrimonio: capital, reservas y resultados acumulados.

- Ingresos: intereses ganados, comisiones y otros resultados.

- Gastos: intereses pagados, costos, previsiones y otros egresos.

Toda operación contable mantiene el equilibrio entre débitos y créditos.

Los asientos confirmados no se borran para corregir una operación. Se registran los ajustes correspondientes.

La actividad contable comprende cierres diarios y periódicos, devengamientos, conciliación, registración de diferencias,
previsiones por riesgo crediticio y elaboración de información financiera.

La contabilidad general del banco y el extracto que recibe cada cliente tienen finalidades diferentes.

### 7. Transferencias y pagos

Una transferencia representa una instrucción para mover fondos entre cuentas.

Un pago es una operación destinada a cumplir una obligación o entregar fondos a un beneficiario.

Un pago puede utilizar una transferencia, una tarjeta, un débito autorizado u otro instrumento.

Operaciones comprendidas:

- Transferencias entre cuentas propias.

- Transferencias entre clientes del mismo banco.

- Transferencias hacia otras entidades.

- Recepción de transferencias externas.

- Pagos de servicios e impuestos.

- Pagos a comercios.

- Débitos automáticos y cobros recurrentes.

- Órdenes de pago programadas.

- Pagos con cheques y ECHEQ.

Para las transferencias se comprueban el ordenante, su autorización, la cuenta de origen, el destino, la moneda, los
fondos disponibles, los límites y las restricciones aplicables.

Las condiciones normativas y los límites variables conservan su vigencia y fundamento.

La instrucción recibida, la operación ejecutada, su acreditación y su liquidación se distinguen cuando corresponden a
hechos diferentes.

Las excepciones comprenden fondos insuficientes, cuenta bloqueada, destino inválido, rechazo del receptor, duplicación,
demora, operación incierta, devolución y conciliación pendiente.

Los débitos autorizados incluyen reglas de adhesión, revocación y reversión, según las condiciones aplicables.

### 8. Préstamos y créditos

El crédito es una obligación de devolución asumida por el prestatario frente al banco.

Solicitar, evaluar, aprobar, contratar y desembolsar son hechos diferentes. Ninguno sustituye automáticamente al
siguiente.

El ciclo funcional completo comprende:

Solicitud → identificación del solicitante → evaluación de ingresos, antecedentes, capacidad de pago y riesgo → decisión
→ aceptación de condiciones → constitución de garantías cuando corresponda → desembolso → administración de cuotas →
pagos → cancelación.

Un préstamo define capital, moneda, tasa, plazo, sistema de amortización, vencimientos, cargos, garantías, condiciones
de precancelación y consecuencias de la mora.

El costo de un préstamo no se limita a su tasa nominal. El Costo Financiero Total incorpora los demás componentes
aplicables de la financiación.

Reglas principales:

- Aprobar un préstamo no significa haber desembolsado dinero.

- Pagar una cuota distingue capital, intereses y demás conceptos.

- Un pago parcial no necesariamente cancela una cuota completa.

- La mora aparece cuando una obligación exigible permanece impaga según las condiciones aplicables.

- Refinanciar cambia condiciones mediante una nueva decisión contractual; no borra la historia anterior.

- Un préstamo cancelado no conserva una deuda exigible por el capital ya pagado.

- Los reclamos y ajustes mantienen trazabilidad de lo ocurrido.

La simulación contempla préstamos normales, pagos anticipados, mora, refinanciación y recuperaciones.

La habilitación comercial de una línea respeta sus condiciones particulares.

### 9. Tarjetas

La tarjeta de débito permite disponer de fondos de una cuenta vinculada, sujeto a saldos, permisos y límites.

La tarjeta de crédito permite realizar consumos dentro de una financiación concedida, generando obligaciones de pago
posteriores.

Una tarjeta de crédito no exige tener depositado previamente el importe de cada consumo.

El banco establece un límite y el titular paga su resumen conforme al contrato.

El proceso comprende:

Emisión → entrega → activación → uso → autorización de consumo → presentación del comercio → confirmación del cargo →
resumen → pago → eventual financiación o mora.

Se distinguen:

- Límite de compra y límite disponible.

- Autorización de un consumo y cargo presentado.

- Consumo en un pago y en cuotas.

- Fecha de cierre del resumen y fecha de vencimiento.

- Pago total, pago parcial y pago mínimo.

- Intereses por financiación y cargos por incumplimientos, según contrato.

- Bloqueo preventivo, robo, extravío y reemplazo.

- Desconocimiento de consumo, disputa y resolución.

Una autorización no equivale necesariamente a una deuda definitivamente liquidada.

Una operación anulada, rechazada o no presentada tiene su tratamiento propio.

Las tarjetas adicionales, las responsabilidades del titular, los reclamos y los consumos cuestionados conservan sus
condiciones particulares.

### 10. Ahorro e inversiones

Se distinguen:

- Dinero disponible en una cuenta.

- Dinero colocado en un depósito a plazo.

- Dinero invertido en un instrumento financiero.

En un plazo fijo, el cliente entrega fondos al banco bajo condiciones de monto, plazo, moneda, remuneración y
vencimiento.

Al vencimiento corresponde la devolución del capital y los intereses según el producto.

El plazo fijo tradicional, los precancelables y las modalidades indexadas tienen tratamientos específicos de
disponibilidad, remuneración y actualización.

El ciclo comprende:

Solicitud → verificación de fondos → constitución → vigencia → devengamiento → vencimiento → pago o renovación pactada.

En instrumentos como fondos comunes de inversión, el cliente adquiere una participación cuyo valor puede variar.

No corresponde prometer una rentabilidad fija ni tratar esa inversión como un depósito bancario ordinario.

Las operaciones de inversión conservan las reglas de suscripción, valuación, ejecución, liquidación y rescate
correspondientes al instrumento.

El banco distingue sus obligaciones como receptor de depósitos de su actuación como intermediario o comercializador de
inversiones.

### 11. Comisiones, intereses e impuestos

Estos conceptos tienen causas diferentes:

- Comisión: cargo por un producto o servicio conforme a las condiciones aplicables.

- Interés: remuneración del dinero en función del capital, la tasa, el tiempo y las condiciones pactadas.

- Impuesto o percepción: obligación tributaria aplicable a una operación o sujeto.

Para cada concepto se define su hecho generador, base de cálculo, vigencia, condiciones, exenciones, vencimiento, forma
de cobro y tratamiento de errores.

Ejemplos:

- Intereses que el banco paga por un depósito.

- Intereses que cobra por un préstamo.

- Intereses por financiación de tarjeta.

- Comisiones por determinados servicios.

- Tributos aplicables a operaciones financieras.

- Bonificaciones comerciales sujetas a requisitos.

Reglas fundamentales:

- No cobrar comisiones prohibidas para el producto.

- No aplicar una tasa distinta de la contractualmente válida sin fundamento.

- No cobrar dos veces por el mismo hecho.

- Distinguir intereses devengados, liquidados, cobrados y pendientes.

- Registrar devoluciones y ajustes sin ocultar el cargo original.

- Aplicar los tributos según el régimen efectivamente correspondiente.

- Informar las condiciones y costos de los productos.

El modelo general no establece una tasa de interés, alícuota impositiva o comisión única para todos los productos.

Cada condición conserva su alcance, fundamento y vigencia.

### 12. Tesorería y administración financiera

El banco administra su propia capacidad para cumplir las obligaciones asumidas.

Esto incluye efectivo, disponibilidades, necesidades de financiación, posiciones por moneda, obligaciones de
liquidación, inversiones propias y relaciones con otras entidades.

Se distinguen:

- Liquidez: capacidad para cumplir pagos y obligaciones cuando vencen.

- Solvencia: capacidad patrimonial para absorber riesgos y pérdidas.

- Rentabilidad: resultado económico de las actividades.

- Disponibilidad del cliente: fondos utilizables por un cliente determinado.

Son conceptos relacionados, pero no equivalentes.

Un banco puede registrar correctamente una transferencia y enfrentar una obligación de liquidación interbancaria que
debe atender con sus propias disponibilidades.

La simulación contempla movimientos internos, posiciones por moneda, conciliación con entidades externas, previsión de
necesidades de fondos y controles de exposición.

Las condiciones prudenciales de liquidez y capital se representan mediante las reglas y parámetros aplicables al modelo.

### 13. Riesgo y cumplimiento

El banco evalúa riesgos antes, durante y después de sus relaciones comerciales y operaciones.

Los riesgos y controles comprendidos son:

- Crédito: posibilidad de que un deudor no pague.

- Liquidez: incapacidad de atender obligaciones al vencimiento.

- Mercado: pérdidas por movimientos de tasas, tipos de cambio u otros precios.

- Operacional: errores, fallos, fraudes y procesos inadecuados.

- Prevención de lavado y financiación del terrorismo: exposición a operaciones o relaciones sospechosas.

- Cumplimiento: incumplimiento de obligaciones legales o regulatorias.

El proceso funcional comprende:

Identificar riesgo → evaluar → decidir controles → monitorear → detectar desvíos → investigar → resolver o elevar el
caso → conservar evidencia.

En prevención de lavado existe conocimiento del cliente, evaluación basada en riesgo, monitoreo continuo y tratamiento
de operaciones inusuales o sospechosas.

Una operación inusual no es automáticamente delictiva. Una alerta no implica por sí sola que deba bloquearse una cuenta.

El banco contempla detección de fraude, bloqueos preventivos debidamente autorizados, revisión de decisiones, gestión de
alertas y confidencialidad de investigaciones.

### 14. Operaciones administrativas

El personal autorizado administra relaciones, productos y situaciones excepcionales dentro de sus facultades.

Funciones comprendidas:

- Atender solicitudes y reclamos.

- Verificar documentación.

- Aprobar o rechazar actuaciones según facultades.

- Corregir errores administrativos.

- Tramitar bloqueos, desbloqueos y cancelaciones.

- Gestionar excepciones comerciales autorizadas.

- Investigar diferencias y operaciones controvertidas.

- Realizar conciliaciones y cierres.

- Preparar información de control.

- Gestionar recuperaciones de crédito.

Una intervención administrativa no habilita a alterar dinero, contratos o registros históricos arbitrariamente.

Toda acción sensible está respaldada por una causa, una facultad válida y evidencia suficiente.

Las operaciones que requieren aprobación diferenciada no pueden completarse únicamente por decisión de alguien sin esa
atribución.

Se contemplan sustituciones, delegaciones, revocaciones de facultades y tratamiento de actuaciones realizadas por error.

### 15. Documentación e información

El banco administra contratos, identificaciones, comprobantes, autorizaciones, documentación crediticia, extractos,
comunicaciones y evidencias.

Cada documento o información tiene una finalidad funcional identificable y reglas sobre obtención, validación, vigencia,
acceso, conservación y eventual rectificación o eliminación cuando corresponde.

Se distinguen:

- Documento presentado por el cliente.

- Documento emitido por el banco.

- Contrato aceptado.

- Comprobante de operación.

- Evidencia de una verificación.

- Registro de una decisión.

- Comunicación enviada o recibida.

- Historial de una relación financiera.

El cliente accede a información sobre sus productos y operaciones conforme a los derechos aplicables.

La protección de datos personales y el deber de confidencialidad se mantienen durante toda la relación.

Los derechos de acceso y rectificación se ejercen bajo las condiciones legales correspondientes.

La finalización de la relación comercial no permite borrar inmediatamente toda la información: pueden subsistir
obligaciones de conservación.

### 16. Procesos bancarios completos

Esta área define cómo se relacionan todas las anteriores.

Un proceso bancario completo no termina cuando alguien solicita una operación, sino cuando existe un resultado de
negocio comprobable y se resolvieron sus consecuencias relevantes.

Todo proceso define:

- Quién puede iniciarlo y por qué.

- Qué situación debe existir antes de comenzar.

- Qué información y autorizaciones necesita.

- Qué validaciones y decisiones se realizan.

- Qué cambios contractuales, monetarios o administrativos produce.

- Cuándo se considera efectivamente concluido.

- Qué se informa a los interesados.

- Cómo se tratan rechazos, cancelaciones, demoras y resultados inciertos.

- Qué obligaciones o procesos posteriores genera.

Una operación puede resultar rechazada, confirmada, cancelada o pendiente de resolución, según su naturaleza.

No todos los procesos comparten los mismos estados, aunque respetan significados compatibles.

## 3. Los diez fundamentos del funcionamiento bancario

Las áreas anteriores comparten los siguientes fundamentos:

| Fundamento                       | Definición funcional                                                                                                                                                                        |
|----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1. Propósito                     | Recibir fondos, ofrecer productos financieros, facilitar operaciones, otorgar financiación y administrar las obligaciones y riesgos derivados.                                              |
| 2. Actores                       | Clientes, solicitantes, titulares, cotitulares, representantes, beneficiarios, empleados, responsables de control, comercios, empleadores, entidades financieras y organismos competentes.  |
| 3. Conceptos fundamentales       | Persona, cliente, producto, contrato, cuenta, titularidad, operación, obligación, deuda, saldo, reserva, asiento, tasa, vencimiento, autorización, liquidación y conciliación.              |
| 4. Relaciones básicas            | Una persona puede contratar varios productos; un contrato puede involucrar varios participantes; una operación puede afectar más de una obligación o cuenta.                                |
| 5. Principios contables          | Todo efecto económico se registra según su naturaleza; se respetan partida doble, integridad, trazabilidad, devengamiento y correcciones identificables.                                    |
| 6. Principios monetarios         | Los fondos no se duplican; las reservas y los cargos se diferencian; el dinero disponible depende de compromisos y restricciones válidas.                                                   |
| 7. Procesos generales            | Alta, contratación, operación, modificación, suspensión, recuperación, reclamación, cancelación y cierre.                                                                                   |
| 8. Controles generales           | Identificación, autorización, verificación de condiciones, límites, evaluación de riesgos, cumplimiento, auditoría y conciliación.                                                          |
| 9. Principios de operación       | Toda operación tiene una causa, participantes, reglas, un resultado identificable y consecuencias; los fallos no autorizan repetir efectos sin comprobarlos.                                |
| 10. Relaciones entre actividades | Contratar crea obligaciones; operar puede mover dinero; financiar crea deuda; cobrar modifica obligaciones; detectar riesgos puede imponer restricciones; cerrar exige resolver pendientes. |

### Relaciones que deben permanecer siempre claras

Persona, cliente y cuenta. Una persona no es una cuenta. Una cuenta no es un cliente. La titularidad es una relación
entre personas y productos.

Cuenta, saldo y dinero. La cuenta identifica una relación contractual; el saldo describe una posición monetaria. La
existencia de una cuenta no garantiza disponibilidad de fondos.

Dinero y contabilidad. Un movimiento de dinero produce consecuencias contables, pero un asiento de ajuste o
devengamiento no necesariamente representa una transferencia de dinero entre clientes.

Crédito y depósito. Cuando el banco presta dinero, nace un derecho de cobro. Cuando recibe un depósito, nace una
obligación de devolución.

Tarjeta y deuda. Una autorización de tarjeta puede comprometer un límite. El cargo definitivo y la deuda exigible
responden a hechos posteriores.

Riesgo y operación. Detectar una alerta no demuestra fraude. Su tratamiento depende de la evidencia, las reglas y las
facultades aplicables.

Reclamo y corrección. Que una persona cuestione una operación no la elimina automáticamente. El reclamo puede producir
investigación, devolución, ajuste o rechazo fundamentado.

Estas distinciones se mantienen en todas las actividades del banco.

## 4. Cómo comienza, se desarrolla y termina cada proceso

La siguiente tabla establece el recorrido de negocio y las consecuencias indispensables de las principales operaciones.

Cada variante de producto conserva sus condiciones particulares.

| Proceso               | Inicio y condiciones                                       | Resultado esperado                                                 | Fallos y consecuencias                                                                         |
|-----------------------|------------------------------------------------------------|--------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| Alta de cliente       | Solicitud, identidad y documentación.                      | Relación comercial aceptada.                                       | Rechazo, documentación pendiente o revisión adicional.                                         |
| Apertura de cuenta    | Cliente elegible, producto y contrato aceptados.           | Cuenta habilitada bajo condiciones definidas.                      | Solicitud pendiente, rechazo o apertura sin habilitación operativa hasta completar requisitos. |
| Depósito              | Cuenta receptora admitida y origen de fondos evaluable.    | Fondos acreditados y obligación del banco actualizada.             | Rechazo, diferencia de importe o acreditación pendiente.                                       |
| Extracción            | Titular autorizado, fondos y límites suficientes.          | Entrega o transferencia de fondos y disminución de disponibilidad. | Rechazo, entrega fallida o diferencia a conciliar.                                             |
| Transferencia interna | Instrucción autorizada y fondos disponibles.               | Débito y crédito correspondientes confirmados.                     | Rechazo sin movimiento o resultado incierto que requiere comprobación.                         |
| Transferencia externa | Instrucción válida y acceso al mecanismo de transferencia. | Envío, acreditación o resultado final verificable.                 | Rechazo externo, demora, devolución o conciliación.                                            |
| Pago de servicio      | Obligación identificada, autorización y fondos.            | Pago aplicado y referencia de cumplimiento.                        | Referencia incorrecta, rechazo o pago efectuado pero no informado al beneficiario.             |
| Débito recurrente     | Autorización vigente, vencimiento y condiciones.           | Cobro aplicado a la obligación correspondiente.                    | Rechazo, revocación, reversión o deuda pendiente.                                              |
| Préstamo              | Solicitud, evaluación y aceptación contractual.            | Financiación desembolsada y deuda constituida.                     | Rechazo crediticio o aprobación sin desembolso.                                                |
| Pago de cuota         | Obligación exigible y fondos aplicables.                   | Disminución de los conceptos adeudados.                            | Pago parcial, mora, imputación incorrecta o reclamo.                                           |
| Compra con tarjeta    | Tarjeta válida, autorización y límite disponible.          | Consumo presentado y cargo reconocido.                             | Autorización rechazada, reversión, disputa o presentación no recibida.                         |
| Pago de tarjeta       | Resumen y obligación identificados.                        | Deuda total o parcialmente cancelada.                              | Saldo financiado, intereses aplicables, mora o pago sin imputar.                               |
| Plazo fijo            | Fondos, condiciones y aceptación.                          | Depósito constituido con vencimiento definido.                     | Rechazo de constitución, precancelación admitida o renovación.                                 |
| Inversión financiera  | Instrumento habilitado, fondos y aceptación del riesgo.    | Participación o tenencia registrada conforme a la operación.       | Orden rechazada, liquidación pendiente o rescate sujeto a condiciones.                         |
| Cambio de moneda      | Operación permitida, fondos y cotización.                  | Compra y venta de las monedas involucradas.                        | Restricción normativa, rechazo o diferencia de liquidación.                                    |
| Reclamo               | Disconformidad identificada y evidencia disponible.        | Resolución fundada y acciones correspondientes.                    | Investigación pendiente, devolución, corrección o rechazo.                                     |
| Cierre de cuenta      | Solicitud o causal válida, con obligaciones revisadas.     | Relación cerrada y obligaciones residuales tratadas.               | Cierre impedido o pendiente por deudas, operaciones o restricciones.                           |

La recepción de un reclamo, su investigación, la comunicación de la resolución y la eventual corrección son actividades
propias del banco.

La intervención posterior de otras instancias conserva su procedimiento correspondiente.

### Ejemplo A: apertura de cuenta y primer depósito

Una persona solicita ser cliente. El banco verifica su identidad, evalúa si puede admitirla y le ofrece una cuenta con
condiciones claras.

La persona acepta el contrato, el banco abre la cuenta y la habilita para las operaciones permitidas.

Luego deposita $100.000 en efectivo.

Consecuencias:

- El cliente tiene un derecho de cobro frente al banco por el depósito.

- El banco aumenta sus disponibilidades de efectivo.

- El banco reconoce una obligación frente al cliente.

- El saldo de la cuenta refleja el dinero acreditado.

- La operación conserva un comprobante y antecedentes para consultas posteriores.

El depósito no genera automáticamente una ganancia de $100.000 para el banco.

### Ejemplo B: transferencia entre dos clientes

Un cliente tiene $100.000 disponibles y ordena transferir $30.000 a otro cliente del mismo banco.

El banco comprueba identidad, permisos, restricciones, fondos y límites. Si se cumplen las condiciones, efectúa la
operación.

Consecuencias:

- La disponibilidad del ordenante disminuye $30.000.

- La posición del beneficiario aumenta $30.000.

- El banco conserva el registro económico de ambas consecuencias.

- Se emite una confirmación verificable.

No ingresaron ni salieron $30.000 del banco como conjunto. Se modificó la distribución de sus obligaciones frente a los
clientes.

Si falla la comunicación de la confirmación, no corresponde volver a transferir automáticamente los $30.000.

### Ejemplo C: otorgamiento y cobro de un préstamo

Una persona solicita $500.000 de financiación.

El banco evalúa su capacidad de pago y aprueba el crédito bajo determinadas condiciones.

Después se celebra el contrato y se realiza el desembolso.

Consecuencias:

- El banco adquiere un derecho de cobro por el capital prestado.

- El prestatario adquiere una obligación de devolución.

- El desembolso pone fondos a disposición conforme a lo pactado.

- Se establece un calendario de obligaciones.

- Los intereses se devengan conforme a las condiciones del crédito.

Cuando el cliente paga una cuota, el banco identifica qué importe corresponde al capital, cuál a intereses y cuáles a
otros conceptos aplicables.

Pagar $50.000 no significa necesariamente reducir el capital adeudado en $50.000.

### Ejemplo D: compra con tarjeta de crédito

Un cliente realiza una compra de $80.000 con tarjeta.

Primero se verifica si la operación puede autorizarse.

La autorización puede comprometer capacidad de compra, sin que necesariamente exista todavía un cargo definitivamente
presentado.

Cuando se recibe y procesa la presentación válida, se reconoce el consumo conforme al circuito correspondiente.

Posteriormente, ese consumo participa en la liquidación periódica que determina las obligaciones del titular.

Si el cliente paga parcialmente el resumen, puede permanecer una deuda financiada y generarse intereses según las
condiciones aplicables.

La compra, la autorización, el cargo, el cierre del resumen y su pago son hechos diferentes.

### Ejemplo E: plazo fijo

Un cliente constituye un plazo fijo de $200.000.

Esos fondos pasan a estar sujetos a las condiciones del depósito contratado.

El banco devuelve el importe correspondiente al vencimiento y paga los intereses pactados.

Consecuencias:

- Cambia la naturaleza y disponibilidad de la obligación que el banco mantiene frente al cliente.

- Nace el derecho a la remuneración del depósito.

- Se establece un vencimiento.

- Se producen devengamientos de intereses.

- Al finalizar, corresponde pagar o renovar según las condiciones válidas.

Un plazo fijo tradicional no se trata como dinero disponible para retirarlo en cualquier momento.

### Ejemplo F: transferencia externa con resultado incierto

Un cliente ordena transferir $70.000 a otro banco.

El banco de origen valida la instrucción y la envía. Sin embargo, se pierde la respuesta de la entidad receptora.

En ese momento no se conoce si la transferencia fue acreditada.

El banco no comunica que fue rechazada sin evidencia ni crea una segunda transferencia para resolver el problema.

Conserva la instrucción original, consulta su resultado y concilia la operación.

Según el resultado comprobado, confirma, mantiene pendiente o realiza el tratamiento correspondiente.

Este comportamiento evita duplicaciones y pérdidas.

## 5. Las reglas generales que debe cumplir toda actividad bancaria

Las siguientes reglas se aplican transversalmente, sin depender de un producto particular.

| Regla                 | Exigencia funcional                                                                                                                        |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| Identificación        | Toda relación y operación sensible tiene participantes identificados conforme a sus requisitos.                                            |
| Facultad              | Nadie ejerce derechos sobre productos ajenos sin una autorización válida.                                                                  |
| Contratación          | Ningún producto se considera contratado sin las condiciones y aceptación exigibles.                                                        |
| Fondos                | No se comprometen importes superiores a los disponibles, salvo financiación expresamente admitida.                                         |
| Unicidad              | Una misma operación no produce dos veces el mismo efecto económico por repetición accidental.                                              |
| Contabilidad          | Los hechos económicos se registran íntegramente y mantienen el equilibrio contable.                                                        |
| Moneda                | Cada obligación y operación tiene moneda definida; cualquier conversión requiere reglas propias.                                           |
| Temporalidad          | Vencimientos, cortes, liquidaciones, intereses y obligaciones respetan fechas y calendarios aplicables.                                    |
| Transparencia         | El cliente conoce las condiciones, costos y obligaciones correspondientes.                                                                 |
| Riesgo                | El banco evalúa y controla los riesgos relevantes de sus actividades.                                                                      |
| Seguridad             | Las operaciones requieren controles adecuados y las restricciones no se ignoran arbitrariamente.                                           |
| Trazabilidad          | Las decisiones, movimientos y correcciones relevantes pueden justificarse.                                                                 |
| Recuperación          | Una operación incierta se investiga antes de repetir efectos económicos.                                                                   |
| Protección al cliente | Existen mecanismos de consulta, reclamo, rectificación y resolución.                                                                       |
| Conservación          | La evidencia se conserva conforme a las obligaciones aplicables; la historia financiera no desaparece por una modificación administrativa. |
| Cumplimiento          | Ninguna política comercial prevalece sobre una prohibición legal o regulatoria aplicable.                                                  |

### Operaciones permitidas y prohibidas

El banco puede, dentro del alcance autorizado y de las condiciones aplicables, abrir cuentas, recibir depósitos,
efectuar transferencias, tramitar pagos, conceder créditos, emitir instrumentos de pago, recibir inversiones, aplicar
cargos válidos y resolver relaciones comerciales.

El banco no puede:

- Permitir operaciones sin la autorización exigible.

- Debitar fondos dos veces por la misma obligación.

- Otorgar disponibilidad inexistente sin financiación válida.

- Aplicar condiciones contractuales o cargos prohibidos.

- Confundir una operación pendiente con una operación confirmada.

- Borrar movimientos económicos para ocultar errores.

- Exponer información protegida a personas no autorizadas.

- Ejecutar operaciones alcanzadas por restricciones que impidan su realización.

- Dar por resuelta una obligación cuando todavía existe un resultado incierto o una diferencia pendiente.

Estas prohibiciones representan principios generales.

Las restricciones particulares se distinguen según el producto y las reglas correspondientes.

## 6. Cómo se relacionan todas las actividades del banco

Las actividades bancarias producen consecuencias sobre otras relaciones, obligaciones y procesos.

| Actividad inicial            | Qué otras actividades puede afectar                                                                        |
|------------------------------|------------------------------------------------------------------------------------------------------------|
| Alta de cliente              | Identificación, documentación, admisión, contratación y controles de riesgo.                               |
| Apertura de cuenta           | Titularidad, condiciones comerciales, medios de pago y administración de fondos.                           |
| Depósito                     | Disponibilidad, obligaciones del banco, contabilidad, liquidez y controles.                                |
| Transferencia                | Cuentas, disponibilidad, pagos, registros, posibles liquidaciones y conciliaciones.                        |
| Otorgamiento de préstamo     | Riesgo crediticio, contrato, desembolso, contabilidad, liquidez y futuras cobranzas.                       |
| Pago de cuota                | Deuda, intereses, mora, contabilidad y evaluación crediticia.                                              |
| Compra con tarjeta           | Autorización, límites, reservas, deuda, presentación y liquidación.                                        |
| Constitución de plazo fijo   | Fondos disponibles, obligación de depósito, intereses y liquidez del banco.                                |
| Operación de inversión       | Fondos, instrumento adquirido, condiciones de rescate y valuación.                                         |
| Cobro de comisión o impuesto | Obligación que origina el cobro, fondos, contabilidad y comprobantes.                                      |
| Detección de fraude          | Evaluación de riesgo, posibles restricciones, investigación y atención al cliente.                         |
| Reclamo aceptado             | Rectificación, devolución, ajuste contable, comunicaciones y reevaluación de controles cuando corresponda. |
| Cierre de cuenta             | Fondos restantes, obligaciones, instrumentos asociados, autorizaciones y conservación documental.          |

Ninguna actividad se evalúa aisladamente.

Cerrar una cuenta con un débito pendiente, un cheque presentado, un préstamo asociado o una restricción judicial exige
examinar esas circunstancias.

El cierre no se utiliza para hacer desaparecer las obligaciones.

Cancelar una tarjeta tampoco extingue automáticamente las deudas originadas por consumos anteriores.

## 7. Las ocho preguntas que debe responder cualquier operación

Toda operación proporciona respuestas a estas preguntas:

| Pregunta                                 | Respuesta que debe proporcionar el modelo                                                                |
|------------------------------------------|----------------------------------------------------------------------------------------------------------|
| ¿Qué hace el banco?                      | Identifica la actividad económica, contractual o administrativa que realiza.                             |
| ¿Para quién?                             | Identifica los participantes y sus derechos, facultades y obligaciones.                                  |
| ¿Bajo qué condiciones?                   | Determina requisitos previos, elegibilidad, autorizaciones, fondos, límites y condiciones contractuales. |
| ¿Qué reglas debe respetar?               | Establece obligaciones legales, principios financieros y condiciones comerciales aplicables.             |
| ¿Qué está permitido y prohibido?         | Define las acciones admitidas, restricciones y causales de rechazo.                                      |
| ¿Cómo comienza, se desarrolla y termina? | Describe solicitud, controles, decisiones, efectos y resultado comprobable.                              |
| ¿Qué sucede ante errores y excepciones?  | Define rechazo, reintento seguro, investigación, corrección, devolución o recuperación.                  |
| ¿Cómo afecta a otras actividades?        | Identifica consecuencias sobre relaciones, contratos, dinero, deuda, contabilidad, riesgo e información. |

La descripción comprende el recorrido satisfactorio, las excepciones y las consecuencias que permanecen pendientes.

## 8. Alcance funcional del banco

El banco adopta el siguiente alcance funcional para su actividad simulada:

| Aspecto             | Alcance                                                                                            |
|---------------------|----------------------------------------------------------------------------------------------------|
| Tipo de institución | Banco comercial argentino ficticio.                                                                |
| Clientes            | Personas humanas y jurídicas, con sus relaciones y representantes.                                 |
| Monedas             | Pesos y dólares, bajo las condiciones específicas de cada operación.                               |
| Cuentas             | Caja de ahorro, cuenta sueldo, cuenta corriente y cuentas especiales admitidas.                    |
| Movimientos         | Depósitos, extracciones, transferencias, pagos, devoluciones y ajustes.                            |
| Financiación        | Préstamos personales, prendarios, hipotecarios y comerciales, con sus respectivos contratos.       |
| Tarjetas            | Débito y crédito, incluyendo autorización, consumos, resúmenes, pagos y reclamos.                  |
| Ahorro              | Depósitos a plazo y sus distintas condiciones de remuneración y vencimiento.                       |
| Inversiones         | Operaciones con instrumentos financieros admitidos y participantes simulados.                      |
| Medios de pago      | Transferencias, pagos recurrentes, instrumentos de tarjeta y cheques.                              |
| Actividad interna   | Contabilidad, tesorería, liquidez, riesgo, cumplimiento, conciliación y operación administrativa.  |
| Canales             | Atención digital y asistida, con operaciones y participantes simulados.                            |
| Integraciones       | Otras entidades y organismos representados sin operaciones reales.                                 |

La inclusión de una familia de productos en este alcance no habilita automáticamente todas sus variantes.

Cada producto y operación requiere las condiciones comerciales, contractuales y operativas correspondientes.

Las tasas, los montos comerciales, las alícuotas, los límites y otras condiciones variables conservan su fundamento,
alcance y vigencia.

Una modificación no altera automáticamente las condiciones de acuerdos anteriores.

Las condiciones particulares de inversiones, operaciones cambiarias y servicios regulados respetan el régimen aplicable
y las reglas adoptadas para la simulación.

## 9. Desarrollo documental del modelo

Este documento establece la identidad del banco, sus actividades, conceptos fundamentales, principios económicos y
relaciones generales.

El desarrollo del modelo se organiza de la siguiente manera:

| Documento                        | Contenido                                                                              |
|----------------------------------|----------------------------------------------------------------------------------------|
| Modelo funcional                 | Propósito, alcance, conceptos, principios y relaciones del negocio bancario.           |
| Definición de módulos            | Distribución de responsabilidades, información propia y límites entre servicios.       |
| Especificación funcional         | Requisitos, reglas, cálculos, recorridos, resultados y excepciones de las operaciones. |
| Integración entre microservicios | Capacidades ofrecidas, participantes, información intercambiada y garantías.           |
| Arquitectura técnica             | Mecanismos de comunicación, seguridad, coordinación, ejecución y recuperación.         |
| Contratos técnicos               | Interfaces, campos, validaciones, respuestas y eventos.                                |
| Diseño de persistencia           | Estructuras de datos, identidades, restricciones, transacciones e historia.            |

Las condiciones específicas de productos y procesos se desarrollan en las especificaciones correspondientes, manteniendo
los principios establecidos en este modelo.

Las decisiones comerciales y operativas se expresan en la documentación y configuración de su responsable.

No se deducen de ejemplos ni se introducen como supuestos durante la programación.

La distribución técnica respeta la propiedad de las decisiones y de los efectos del negocio.

Una integración, una estructura de datos o un mecanismo de recuperación no modifica por sí mismo las obligaciones
contractuales ni el significado económico de una operación.

## Fuentes principales de la investigación

Las referencias utilizadas para construir el modelo general son:

- Ley de Entidades Financieras 21.526.

- Normativa UIF para entidades financieras.

- Guías del BCRA sobre operaciones y protección de usuarios.

- Información de productos de Banco Credicoop.

- Información de productos de Banco Macro.

- Fondos comunes de inversión — CNV.

Estas referencias aportan el marco general y ejemplos de actividad bancaria.

Las condiciones particulares de la simulación se establecen en el modelo y en las especificaciones de cada producto y
proceso.
