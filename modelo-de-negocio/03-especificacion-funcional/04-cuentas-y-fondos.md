# Operaciones funcionales — Módulo 4. Cuentas y fondos

## 1. Propósito y alcance

Este documento define las operaciones para abrir y administrar cuentas bancarias, mantener sus identificadores y
facultades, comprometer fondos y registrar movimientos monetarios.

Comprende los microservicios Cuentas, Identificación bancaria, Fondos y Operaciones de efectivo.

La apertura, la habilitación, la aceptación de una instrucción, la reserva, el movimiento definitivo y la liquidación de
conceptos son hechos distintos.

Cuentas administra la relación operativa. Fondos mantiene la posición monetaria. Identificación bancaria administra CBU
y alias simulados. Operaciones de efectivo coordina la recepción y entrega física simulada.

Los acuerdos crediticios, la deuda y los intereses de descubierto pertenecen a Préstamos y líneas. La finalidad de
pagos, transferencias e inversiones permanece en sus módulos responsables.

## 2. Catálogo de operaciones

| Identificador | Operación | Responsable |
| --- | --- | --- |
| OF-04-01 | Abrir y habilitar una cuenta | Cuentas |
| OF-04-02 | Administrar titulares y autorizaciones operativas | Cuentas |
| OF-04-03 | Modificar condiciones y capacidades operativas | Cuentas |
| OF-04-04 | Aplicar y levantar restricciones | Cuentas y Fondos |
| OF-04-05 | Asignar y mantener identificadores bancarios | Identificación bancaria |
| OF-04-06 | Administrar alias | Identificación bancaria |
| OF-04-07 | Resolver un identificador bancario | Identificación bancaria |
| OF-04-08 | Consultar saldos y movimientos | Fondos |
| OF-04-09 | Constituir una reserva de fondos | Fondos |
| OF-04-10 | Consumir, liberar y revisar reservas | Fondos |
| OF-04-11 | Registrar movimientos monetarios | Fondos |
| OF-04-12 | Ejecutar movimientos entre cuentas internas | Fondos |
| OF-04-13 | Administrar la utilización de descubierto | Fondos |
| OF-04-14 | Liquidar conceptos propios de la cuenta | Cuentas |
| OF-04-15 | Recibir un depósito de efectivo | Operaciones de efectivo |
| OF-04-16 | Realizar una extracción de efectivo | Operaciones de efectivo |
| OF-04-17 | Resolver diferencias de efectivo | Operaciones de efectivo |
| OF-04-18 | Corregir movimientos monetarios | Fondos |
| OF-04-19 | Cerrar una cuenta | Cuentas |

## 3. Reglas comunes del módulo

### 3.1. Identidad, producto y moneda

Cada cuenta conserva:

- Identificación interna estable.

- Producto y versión de origen.

- Contrato.

- Moneda.

- Titulares y modalidad de actuación.

- Condiciones operativas y económicas aplicables.

Los CBU y alias permiten localizar una cuenta. No acreditan titularidad ni facultades.

La moneda no se cambia sobre una cuenta con historia. Operar en otra moneda requiere una cuenta compatible y, cuando
corresponda, una operación de Cambio de moneda.

La caja de ahorro y la cuenta sueldo no admiten descubierto. La cuenta corriente solo lo admite cuando existe una
financiación expresamente contratada y habilitada.

Los controles de gratuidad y cargos permitidos se aplican según el producto. Una tarifa comercial no habilita un cargo
incompatible con sus condiciones.

### 3.2. Posición monetaria

Fondos distingue:

| Concepto | Significado |
| --- | --- |
| Saldo registrado | Suma de movimientos monetarios confirmados de la cuenta. |
| Fondos propios utilizables | Parte positiva del saldo que puede respaldar nuevas operaciones después de considerar afectaciones. |
| Crédito autorizado | Capacidad de descubierto concedida y operativamente vigente. |
| Reservas | Importes comprometidos para operaciones todavía no convertidas íntegramente en movimientos. |
| Inmovilizaciones | Importes afectados por medidas que impiden su utilización. |
| Créditos no disponibles | Acreditaciones confirmadas cuya disponibilidad está sujeta a una condición identificada. |
| Capital de descubierto utilizado | Parte negativa de la posición monetaria atribuible a utilizaciones de financiación confirmadas. |
| Exceso | Utilización o compromiso que supera las condiciones crediticias vigentes. |
| Capacidad para nuevos compromisos | Recursos que pueden afectarse a nuevas instrucciones. |

El saldo registrado de una cuenta es un registro operativo. No sustituye la contabilidad general del banco.

Cada afectación se considera una sola vez. No se descuentan nuevamente importes ya comprendidos en otra afectación sobre
el mismo recurso.

Las inmovilizaciones no utilizan descubierto para completar su importe.

### 3.3. Comprobación y ejecución

Fondos comprueba disponibilidad y registra su afectación como una única decisión.

Las consultas anteriores no garantizan disponibilidad posterior.

El servicio de negocio determina la finalidad y procedencia de la operación. Fondos determina si puede ejecutar su
efecto monetario.

Las operaciones concurrentes no pueden consumir los mismos fondos ni la misma capacidad crediticia.

Las restricciones, autorizaciones y condiciones operativas se comprueban en el momento exigible. Una reserva anterior no
permite ignorar una medida posterior aplicable.

### 3.4. Reservas y firmeza

Toda reserva identifica un tratamiento:

| Tratamiento | Significado |
| --- | --- |
| Cancelable | Puede dejarse sin efecto sin incumplir una actuación ya aceptada de forma firme. |
| Firme | Respalda una actuación aceptada cuyo cumplimiento corresponde atender. |
| Incierta | No se conoce si existe una ejecución pendiente o realizada que deba atenderse. |

El responsable de negocio determina y justifica el tratamiento. Fondos conserva la reserva y sus referencias.

La ausencia de respuesta no convierte una reserva incierta en cancelable.

### 3.5. Descubierto y reducción de límite

Préstamos y líneas concede y administra la financiación. Fondos controla exclusivamente su utilización inmediata sobre
la cuenta.

Una reducción o vencimiento:

- Impide nuevos compromisos incompatibles.

- Conserva el capital ya utilizado.

- Identifica las reservas anteriores.

- Solicita cancelar las reservas cancelables que no puedan mantenerse.

- Conserva las firmes e inciertas hasta esclarecer y resolver sus obligaciones.

Los compromisos firmes aceptados bajo condiciones anteriores conservan una cobertura de crédito comprometida por el
importe necesario para atenderlos.

Esa cobertura:

- Se vincula a los compromisos originales.

- No habilita operaciones nuevas.

- No constituye un aumento del límite comercial.

- Se comunica a Préstamos y líneas y a Riesgos.

- Se extingue por la parte consumida o liberada.

Una prohibición efectiva puede impedir cumplir un compromiso anterior y exige tratamiento con Restricciones y el
responsable de negocio.

### 3.6. Acreditaciones y pagos de descubierto

Se distinguen dos actuaciones.

Acreditación ordinaria en cuenta

Un depósito, una transferencia recibida o un crédito válido aumenta el saldo registrado. Si el saldo es negativo, reduce
directamente el capital de descubierto utilizado por el importe que corresponda.

No se desvía parte de esa acreditación hacia intereses, comisiones o tributos mediante una imputación implícita.

Pago destinado a obligaciones crediticias

Una instrucción expresamente destinada a pagar deuda de financiación se administra en Préstamos y líneas. Ese servicio
determina los conceptos cancelados y solicita los movimientos correspondientes.

Los intereses y cargos de descubierto:

- Permanecen separados del capital utilizado.

- No se consideran cobrados por el solo ingreso de dinero en la cuenta.

- Requieren una actuación de cobro identificada.

- No se incorporan automáticamente al capital.

- No se financian mediante nuevo descubierto en los productos ordinarios de esta simulación.

Si un cobro utiliza fondos que primero fueron acreditados en la cuenta, se conserva la relación entre ambos hechos. La
acreditación y el cobro no representan dos ingresos independientes.

### 3.7. Fechas, precisión e historia

La referencia temporal es America/Argentina/Buenos_Aires.

Cada movimiento conserva:

- Fecha y hora de registro.

- Fecha operativa.

- Fecha de valor.

- Importe y moneda.

- Concepto.

- Operación y efecto de origen.

La fecha de valor no modifica retrospectivamente la disponibilidad que existió.

Los cálculos intermedios conservan al menos doce decimales. Los importes monetarios finales se redondean a la unidad
mínima de la moneda, al más cercano, elevando los empates positivos.

Una reversión utiliza el importe registrado originalmente.

Los movimientos confirmados no se borran ni sobrescriben.

### 3.8. Recuperación y aprobaciones

Cada instrucción tiene referencia única y contenido identificable.

La repetición recupera su resultado. La misma referencia con contenido diferente genera una inconsistencia.

Ante una respuesta perdida se consulta el resultado antes de repetir, devolver dinero o liberar recursos.

Los ajustes manuales y las resoluciones de diferencias requieren preparación y aprobación separadas. Gestión
administrativa coordina las tareas; Institución determina las facultades.

## 4. OF-04-01. Abrir y habilitar una cuenta

Propósito: constituir una cuenta conforme a un contrato válido.

Responsable: Cuentas.

### Información y condiciones previas

Contrato, producto, versión, moneda, titulares, modalidad de actuación, condiciones económicas y capacidades
solicitadas.

Las identidades, representaciones y decisiones exigibles deben estar comprobadas.

### Desarrollo

- Comprobar contrato y participantes.

- Buscar una constitución anterior de la misma solicitud.

- Asignar identificación interna.

- Registrar titularidades y condiciones.

- Constituir la posición monetaria inicial.

- Solicitar identificadores bancarios.

- Comprobar dependencias operativas.

- Confirmar apertura y capacidades efectivamente habilitadas.

### Reglas y resultados

- La posición inicial es cero.

- Abrir una cuenta no acredita dinero ni concede crédito.

- El depósito inicial se ejecuta mediante una operación propia.

- Los requisitos de apertura se distinguen de los necesarios para cada uso.

- Una dependencia pendiente impide informar habilitación completa.

- La repetición no crea otra cuenta.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Apertura sin depósito | Saldo cero. |
| Falta un identificador necesario | Cuenta constituida con habilitación pendiente. |
| Se repite la solicitud | Se recupera la misma cuenta. |
| Moneda distinta de la contratada | No se completa la habilitación. |

## 5. OF-04-02. Administrar titulares y autorizaciones operativas

Propósito: mantener quién puede actuar y con qué alcance.

Responsable: Cuentas.

### Desarrollo

- Recibir modificación y fundamento.

- Comprobar contrato, participantes y representación.

- Determinar actos, límites y modalidad.

- Obtener las aceptaciones necesarias.

- Aplicar la modificación.

- Comunicar sus consecuencias.

### Reglas y resultados

- Cambiar titulares requiere formalización contractual.

- Un autorizado no adquiere propiedad sobre los fondos.

- Una autorización de cuenta no amplía la representación legal.

- La actuación conjunta exige conformidad sobre el mismo contenido.

- Las instrucciones incompletas requieren facultades vigentes antes de aceptarse.

- Una revocación posterior no cancela automáticamente una instrucción firme anterior.

- Las restricciones posteriores se aplican según su alcance y momento de efecto.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Autorización de consulta | No permite extracciones. |
| Se exige actuación conjunta | Una sola intervención no completa la autorización. |
| Revocación anterior a completar la instrucción | No se utiliza la facultad revocada. |
| Se incorpora otro titular | No se redistribuyen saldos por el mero registro. |

## 6. OF-04-03. Modificar condiciones y capacidades operativas

Propósito: aplicar cambios válidos sobre el funcionamiento de una cuenta.

Responsable: Cuentas.

### Desarrollo

- Comprobar fundamento y versión de origen.

- Identificar diferencias y fecha de efecto.

- Revisar instrumentos y compromisos.

- Coordinar con Fondos y los responsables afectados.

- Confirmar las capacidades aplicadas.

- Conservar versiones y comunicar.

### Reglas y resultados

- Una oferta nueva no modifica contratos existentes.

- Las capacidades nuevas requieren dependencias disponibles.

- Retirar una capacidad impide nuevas instrucciones alcanzadas.

- Los compromisos anteriores reciben tratamiento individual.

- Una aplicación parcial identifica qué quedó efectivo.

- El descubierto utiliza su procedimiento específico.

- Los cambios económicos dentro de un período se aplican desde su fecha válida, sin retroactividad implícita.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Medio de pago sin soporte operativo | No se habilita. |
| Se retiran nuevas extracciones | Las entregas anteriores inciertas siguen investigándose. |
| Cambia una tasa de remuneración válidamente | El cálculo distingue los días anteriores y posteriores. |
| Se pretende cambiar la moneda | Se requiere otra cuenta y la operación correspondiente. |

## 7. OF-04-04. Aplicar y levantar restricciones

Propósito: aplicar medidas sobre capacidades e importes.

Responsables: Cuentas administra impedimentos operativos; Fondos ejecuta afectaciones monetarias.

### Información y desarrollo

Restricciones proporciona medida, versión, alcance, prioridad y momento de efecto.

- Identificar cuentas y actuaciones alcanzadas.

- Aplicar impedimentos.

- Afectar importes cuando corresponda.

- Revisar reservas y operaciones en curso.

- Confirmar aplicación, insuficiencias y pendientes.

- Mantener seguimiento.

### Reglas y resultados

- Prohibir débitos no implica prohibir créditos.

- Inmovilizar no debita ni transfiere dinero.

- No se utiliza descubierto para completar una medida.

- Restricciones determina prioridades ante conflictos.

- Una reserva anterior no prevalece automáticamente.

- Levantar una medida no libera afectaciones justificadas por otras.

- Las operaciones realizadas conservan sus efectos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Medida por 100 y fondos afectables por 60 | Se afectan 60 y se informa diferencia de 40. |
| Hay descubierto disponible por 50 | No se utiliza para completar la medida. |
| Termina una de dos medidas | Continúa la afectación requerida por la restante. |
| La medida alcanza una reserva previa | Se determina su prioridad antes de consumir o liberar. |

## 8. OF-04-05. Asignar y mantener identificadores bancarios

Propósito: asignar identificadores únicos a cuentas.

Responsable: Identificación bancaria.

### Desarrollo

- Comprobar la cuenta.

- Buscar una asignación anterior.

- Generar el identificador.

- Comprobar estructura y unicidad.

- Vincularlo y confirmar.

### Reglas y resultados

- El CBU simulado conserva estructura y validación.

- Un CBU no se reutiliza para otra cuenta.

- Una reorganización de sucursales no modifica automáticamente identificadores.

- La baja conserva la correspondencia histórica.

- Resolver un identificador no demuestra que la cuenta pueda operar.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Dos solicitudes compiten por un identificador | Solo una asignación se confirma. |
| Se repite la asignación | Se recupera el identificador existente. |
| La cuenta cierra | Su correspondencia histórica permanece. |

## 9. OF-04-06. Administrar alias

Propósito: asignar, cambiar y retirar alias.

Responsable: Identificación bancaria.

### Desarrollo

- Comprobar facultades y cuenta.

- Validar formato y disponibilidad.

- Solicitar el cambio al directorio simulado.

- Consultar resultados inciertos.

- Registrar la asignación confirmada.

- Comunicar.

### Reglas y resultados

- Una cuenta mantiene como máximo un alias vigente.

- Un alias resuelve una única cuenta en cada momento.

- El cambio conserva la asignación anterior hasta confirmar su sustitución.

- Una asignación incierta no se informa como efectiva.

- Las instrucciones aceptadas mantienen el destino resuelto originalmente.

- La reasignación requiere confirmar la baja anterior, sin superposición.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Alias ocupado | No se asigna. |
| Cambio sin confirmación | Se consulta; no se informa éxito. |
| Sustitución confirmada | Se conserva historia de ambas asignaciones. |
| Transferencia anterior al cambio | Mantiene el destino aceptado. |

## 10. OF-04-07. Resolver un identificador bancario

Propósito: localizar una cuenta y proporcionar datos de comprobación.

Responsable: Identificación bancaria.

### Desarrollo

- Validar identificador y finalidad.

- Comprobar las facultades de consulta.

- Consultar el registro o directorio.

- Identificar cuenta, participante y vigencia.

- Entregar la información necesaria.

### Reglas y resultados

- Las cuentas externas se distinguen de las propias.

- Resolver no autoriza una operación.

- No se sustituye un destino inexistente.

- La indisponibilidad no produce un resultado supuesto.

- Se expone únicamente la información necesaria para la finalidad.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Identificador inexistente | Se informa ausencia. |
| Directorio indisponible | Resultado pendiente. |
| Cuenta localizada | El consumidor comprueba después su admisión operativa. |

## 11. OF-04-08. Consultar saldos y movimientos

Propósito: informar la posición y sus componentes.

Responsable: Fondos.

### Desarrollo

- Comprobar acceso.

- Determinar corte.

- Reunir movimientos y afectaciones.

- Separar fondos propios, crédito y compromisos.

- Presentar posición y pendientes.

### Reglas y resultados

- Toda consulta identifica moneda y momento.

- Las reservas no se muestran como débitos definitivos.

- La capacidad para nuevos compromisos se informa como cero cuando no existen recursos utilizables.

- Los excesos se muestran separadamente.

- La cobertura protegida no se presenta como crédito para nuevas operaciones.

- La consulta no reserva recursos.

- Los extractos emitidos pertenecen a Extractos.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo 100 y reserva 30, sin otras afectaciones | Saldo registrado 100 y capacidad propia restante 70. |
| Saldo −20 y descubierto vigente 50, sin reservas | Capacidad restante 30. |
| Saldo −60 y límite 40 | Capacidad nueva cero y exceso 20. |
| Consulta repetida | No modifica posiciones. |

## 12. OF-04-09. Constituir una reserva de fondos

Propósito: comprometer un importe para una operación.

Responsable: Fondos.

### Información y condiciones previas

Cuenta, importe positivo, moneda, origen, firmeza, recursos admitidos y condiciones de consumo, liberación y revisión.

El servicio solicitante debe indicar si la operación admite descubierto. Cuando no lo admite, la reserva utiliza
exclusivamente fondos propios.

### Desarrollo

- Comprobar referencia.

- Validar capacidades, restricciones y recursos admitidos.

- Determinar disponibilidad.

- Registrar reserva y cobertura.

- Confirmar referencia y condiciones.

### Reglas y resultados

- Se utilizan primero fondos propios y después crédito, si está admitido.

- La reserva identifica su cobertura crediticia.

- Un aumento requiere comprobar recursos adicionales.

- La firmeza cambia por una actuación del responsable de negocio.

- Una cuenta en cierre rechaza compromisos incompatibles.

- Los importes cero o negativos no constituyen reservas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capacidad 100 y dos reservas concurrentes de 80 | No se confirman ambas íntegramente. |
| Fondos propios 30 y reserva 50 con crédito admitido | Se identifican 20 de cobertura crediticia. |
| Misma situación con crédito prohibido para esa operación | La reserva se rechaza. |
| Referencia repetida | Una sola reserva. |

## 13. OF-04-10. Consumir, liberar y revisar reservas

Propósito: convertir compromisos en movimientos o restituir disponibilidad.

Responsable: Fondos.

### Desarrollo

- Comprobar reserva y remanente.

- Obtener el resultado del negocio.

- Revisar restricciones efectivas.

- Consumir o liberar.

- Registrar remanentes y cobertura.

- Informar.

### Reglas y resultados

- Consumir debita y reduce la reserva en una misma actuación monetaria.

- No se consume más de lo reservado sin comprobar la diferencia.

- Liberar no aumenta el saldo registrado.

- Las reservas firmes requieren cumplimiento o cancelación confirmada.

- El vencimiento de una reserva incierta inicia recuperación.

- El remanente se libera cuando ya no respalda una ejecución posible.

- La cobertura protegida se extingue por la parte consumida o liberada.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo 100, reserva 30 y consumo 30 | Saldo 70, reserva cero y capacidad restante 70. |
| Reserva 50 y consumo parcial 20 | Remanente reservado 30. |
| Liberación repetida | No aumenta dos veces la disponibilidad. |
| Reserva vencida con entrega incierta | Continúa comprometida bajo investigación. |

## 14. OF-04-11. Registrar movimientos monetarios

Propósito: ejecutar débitos y créditos válidos.

Responsable: Fondos.

### Información y condiciones previas

Cuenta, importe positivo, moneda, sentido, concepto, origen, fechas, fundamento y recursos admitidos.

### Desarrollo

- Comprobar referencia y contenido.

- Validar cuenta y condiciones.

- Para débitos, comprobar reserva o disponibilidad.

- Registrar movimiento y actualizar posición.

- Informar al responsable de negocio.

- Comunicar el hecho monetario correspondiente.

### Reglas y resultados

- Todo crédito requiere causa identificada.

- Un débito sin reserva comprueba recursos al ejecutarse.

- No se recorta una instrucción silenciosamente.

- Intereses, comisiones y tributos no reciben financiación automática.

- Las obligaciones no cobradas permanecen en su módulo responsable.

- Un crédito no disponible identifica su condición de liberación.

- Las devoluciones conservan vínculo con su origen.

- Los créditos sobre una posición negativa informan la reducción de capital a Préstamos y líneas.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Débito 80 con capacidad 50 | No se ejecuta parcialmente sin autorización. |
| Crédito sin fundamento | Se rechaza o mantiene pendiente. |
| Movimiento repetido | Un solo efecto. |
| Cargo sin fondos propios y sin financiación admitida | No se cobra; la obligación mantiene seguimiento. |

## 15. OF-04-12. Ejecutar movimientos entre cuentas internas

Propósito: realizar efectos monetarios relacionados sobre cuentas del banco.

Responsable: Fondos.

### Desarrollo

- Comprobar origen y destino.

- Validar autorización, restricciones y recursos.

- Determinar principal y conceptos adicionales.

- Ejecutar conjuntamente los movimientos relacionados.

- Confirmar el resultado completo.

### Reglas y resultados

- El principal mantiene correspondencia entre débito y crédito.

- No se informa éxito con una sola parte realizada.

- Una ejecución incompleta no concede disponibilidad como si estuviera terminada.

- No se admite la misma cuenta como origen y destino de una transferencia.

- Las conversiones pertenecen a Cambio de moneda.

- El servicio de origen conserva la finalidad.

- Las comisiones y tributos se identifican separadamente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Origen 100, destino 20 y traslado 30 | Origen 70 y destino 50, sin otros conceptos. |
| Destino no admite acreditación | No se confirma la transferencia completa. |
| Ejecución repetida | Se recupera el mismo resultado. |
| Comisión adicional de 2 | Se distingue del principal transferido. |

## 16. OF-04-13. Administrar la utilización de descubierto

Propósito: aplicar la financiación autorizada y mantener correspondencia con su deuda.

Responsable: Fondos.

### Desarrollo

- Recibir autorización de Préstamos y líneas.

- Comprobar cuenta, versión y vigencia.

- Aplicar el límite.

- Confirmar capacidad utilizable.

- Registrar compromisos y utilizaciones.

- Informar variaciones de capital.

- Conciliar con Préstamos y líneas.

- Tratar reducciones, vencimientos y excesos.

### Reglas de utilización

- Fondos no concede ni amplía crédito.

- No existe un circuito paralelo que utilice el mismo descubierto.

- Los débitos admitidos incrementan utilización cuando consumen crédito.

- Las acreditaciones ordinarias reducen capital utilizado hasta llevarlo a cero.

- El excedente posterior queda como saldo positivo.

- Los intereses y cargos pendientes no se descuentan implícitamente de esas acreditaciones.

### Cobro de intereses y cargos

Préstamos y líneas calcula y conserva las obligaciones.

Cuando solicita su cobro:

- Identifica obligación, concepto e importe.

- Comprueba el fundamento para debitar.

- Solicita a Fondos el importe cobrable.

- Fondos utiliza únicamente fondos propios disponibles.

- El resultado confirmado cancela los conceptos correspondientes.

- El remanente no cobrado permanece pendiente.

Si el cobro parcial está autorizado, Préstamos y líneas determina su imputación y solicita un importe concreto. Fondos
no decide por sí mismo cómo distribuirlo.

La regla general de imputación de pagos de financiación no modifica el efecto de una acreditación ordinaria sobre el
saldo de la cuenta.

### Reducciones y vencimientos

- Las reservas cancelables incompatibles se someten a cancelación desde las más recientes.

- No se liberan hasta confirmar la cancelación.

- Si dejaron de ser cancelables, reciben tratamiento firme o incierto.

- La cobertura protegida no habilita nuevos usos.

- Los excesos conservan deuda y seguimiento, sin aumentar artificialmente el límite.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Capital utilizado 100, intereses pendientes 10 y acreditación ordinaria 50 | Capital utilizado 50; intereses pendientes 10. |
| Sobre el caso anterior ingresan otros 70 | Capital utilizado cero; saldo positivo 20; intereses pendientes 10. |
| Existe autorización de cobro de esos intereses y se debitan 10 | Saldo positivo 10; intereses cancelados. |
| Saldo cero e intereses pendientes 10 | No se genera descubierto para cobrarlos. |
| Límite 100 y capital utilizado 60; límite baja a 40 | Nuevos usos cero, capital 60 y exceso 20. |
| Capital usado 20 y reserva firme 70; límite baja a 40 | Se conserva cobertura adicional de 50 exclusiva para esa reserva. |
| Se consume esa reserva de 70 | Capital utilizado 90; reserva cero; exceso 50. |
| Se repite una reducción de capital informada | Préstamos y líneas no registra otra cancelación. |

## 17. OF-04-14. Liquidar conceptos propios de la cuenta

Propósito: calcular y liquidar remuneraciones, comisiones de servicio y tributos propios de la cuenta.

Responsable: Cuentas.

Los intereses y cargos de descubierto pertenecen a Préstamos y líneas.

### 17.1. Configuración admitida

Cada condición identifica:

- Producto y contrato.

- Concepto y moneda.

- Hecho o período generador.

- Valor o tasa.

- Modalidad de cálculo.

- Vigencia.

- Bonificaciones.

- Tratamiento tributario.

- Fecha de liquidación.

Las cuentas no remuneradas tienen esa condición expresamente definida. No se interpreta una tasa ausente como tasa cero.

### 17.2. Remuneración de saldos

La simulación utiliza interés simple diario con tasa nominal anual y base Actual/365.

Para cada día:

$B_{d} = \max_{}(S_{d},0)$

$I = B_{d} \times \frac{j_{d}}{365}$

Donde:

- $S_{d}$: saldo registrado al finalizar el día.

- $B_{d}$: base remunerable del día.

- $j_{d}$: tasa nominal anual contractual vigente ese día.

- $I$: interés acumulado del período.

Se adoptan estas reglas:

- Se consideran movimientos confirmados con fecha operativa comprendida hasta ese cierre diario.

- Las reservas y las inmovilizaciones no reducen la base remunerable: limitan utilización, pero no eliminan el depósito.

- Los créditos registrados aunque todavía no disponibles integran la base.

- Las instrucciones pendientes sin movimiento confirmado no integran el saldo.

- Los saldos negativos no generan remuneración.

- No se utiliza el límite de descubierto como base.

- Los años bisiestos cuentan sus días reales y mantienen divisor 365.

- No se redondea cada devengamiento diario antes de acumularlo.

- Una modificación de tasa se aplica desde su fecha de efecto, al inicio de ese día.

- No se ofrecen escalas, topes remunerados ni promedios mensuales en esta modalidad.

### 17.3. Período y pago de remuneraciones

El período ordinario es el mes calendario.

El interés:

- Se acumula por días.

- Se liquida al finalizar el mes.

- Se redondea una sola vez.

- Recibe el tratamiento tributario correspondiente.

- Se acredita el primer día hábil siguiente.

La acreditación es un movimiento propio. No se incorpora retrospectivamente al capital remunerado del mes anterior.

Desde su acreditación integra el saldo de la cuenta y puede participar en períodos posteriores.

Si una restricción impide disponer o acreditar, se aplica su alcance y se conserva la obligación de pago pendiente.

Al cerrar una cuenta se liquida el período parcial sin esperar al cierre mensual.

### 17.4. Comisiones admitidas

Se admiten tres modalidades:

| Modalidad | Cálculo |
| --- | --- |
| Importe fijo por hecho | Importe contractual por una ocurrencia confirmada. |
| Porcentaje por hecho | Base monetaria identificada multiplicada por el porcentaje contractual. |
| Importe fijo mensual | Cargo por los días de servicio activo del mes. |

Para el cargo mensual:

$C = \frac{F_{d}}{D_{m}}$

Donde:

- $F_{d}$: importe mensual contractual vigente el día considerado.

- $D_{m}$: cantidad de días calendario de ese mes.

Se incluye la fecha de inicio del servicio y se excluye la fecha de su terminación efectiva. Si comienza y termina en la
misma fecha, no se genera cargo mensual.

Los cambios de importe durante el mes dividen el cálculo por días de vigencia.

Las bonificaciones se aplican sobre el cargo antes de calcular los tributos correspondientes. Una bonificación total
produce cargo neto cero.

No se admiten mínimos, máximos o escalas adicionales que carezcan de comportamiento especificado.

### 17.5. Desarrollo

- Identificar hecho o período.

- Recuperar condiciones contractuales.

- Obtener saldos o bases.

- Comprobar integridad.

- Calcular remuneración o cargo.

- Aplicar bonificaciones y criterios tributarios.

- Registrar la liquidación.

- Solicitar pago o cobro a Fondos.

- Conservar resultados confirmados y pendientes.

- Comunicar los hechos contables.

### 17.6. Correcciones y pendientes

- Una liquidación identifica cuenta, concepto y período u ocurrencia.

- Repetirla no genera otro cargo ni otro rendimiento.

- La falta de fondos no financia automáticamente una comisión.

- Un cargo pendiente conserva su identidad; no vuelve a generarse cada mes como si fuera otro.

- Las correcciones de saldos o tasas recalculan únicamente los días afectados.

- Se conserva la liquidación original y se registra la diferencia.

- Una corrección histórica modifica el rendimiento correspondiente sin afirmar que el cliente tuvo disponibilidad
  retrospectiva.

- Los recuperos de importes pagados en exceso requieren fundamento y autorización; no generan descubierto
  automáticamente.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo positivo constante 1.000 durante diez días al 36,5 % | Interés bruto 10. |
| Saldo 1.000 con reserva 300 e inmovilización 200 | Base remunerable 1.000. |
| Cinco días con saldo 1.000 y cinco con saldo 600 al 36,5 % | Interés bruto 8. |
| Saldo negativo durante todo el período | Remuneración cero. |
| Comisión mensual 30 en un mes de treinta días, con quince días de servicio | Cargo bruto 15. |
| Comisión del 1,5 % sobre base 1.000 | Cargo bruto 15. |
| Comisión calculada 10 con bonificación total | Cargo neto cero. |
| Comisión 10 sin fondos propios disponibles | Obligación pendiente, sin nuevo descubierto. |
| Se repite una liquidación | No se duplica su efecto. |

## 18. OF-04-15. Recibir un depósito de efectivo

Propósito: registrar efectivo recibido y coordinar su acreditación.

Responsable: Operaciones de efectivo.

### Desarrollo

- Identificar depositante y cuenta.

- Comprobar admisión y controles.

- Recibir y verificar efectivo simulado.

- Determinar importe aceptado.

- Coordinar la variación de existencias con Tesorería.

- Solicitar acreditación a Fondos.

- Conciliar ambos efectos.

- Emitir comprobante.

### Reglas y resultados

- El importe declarado se distingue del recibido.

- El depositante no adquiere titularidad por depositar.

- El efectivo no comprobado no queda disponible.

- Recibir dinero genera obligación de acreditarlo o restituirlo.

- Rechazar antes de recibir no modifica existencias.

- Una diferencia no se resuelve acreditando el importe declarado sin evidencia.

- Si la cuenta está en descubierto, la acreditación reduce capital conforme a OF-04-13.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Se declaran 100 y se reciben 90 | No se acreditan 100. |
| Se reciben 100 y falla la acreditación | Se conserva la obligación de resolver los 100 recibidos. |
| Se repite la actuación | No se duplica efectivo ni crédito. |
| Se restituye efectivo recibido | Se conserva evidencia de la entrega. |

## 19. OF-04-16. Realizar una extracción de efectivo

Propósito: entregar efectivo contra fondos autorizados.

Responsable: Operaciones de efectivo.

### Desarrollo

- Comprobar actor, cuenta y controles.

- Obtener compromiso de disponibilidad física de Tesorería.

- Reservar fondos.

- Revisar condiciones inmediatamente antes de entregar.

- Ejecutar la entrega.

- Registrar el importe comprobado.

- Consumir la reserva correspondiente.

- Tratar remanentes monetarios y físicos.

- Conciliar y emitir comprobante.

### Reglas y resultados

- La reserva monetaria no demuestra entrega.

- Comprobar existencias sin comprometerlas no garantiza capacidad para entregas concurrentes.

- Si falla una de las reservas antes de entregar, se regulariza la otra.

- La entrega parcial requiere aceptación previa o modalidad admitida.

- La incertidumbre impide repetir la entrega o liberar cobertura.

- Una entrega realizada exige regularizar el débito.

- Las extracciones con tarjeta utilizan la misma operación y cobertura, sin una segunda reserva.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Reserva 100 y entrega comprobada 100 | Débito 100 y reserva consumida. |
| Parcialidad admitida y entrega 60 | Débito 60 y tratamiento de los 40 restantes. |
| Entrega fallida comprobada | Se liberan las coberturas que ya no sean necesarias. |
| Dispositivo sin respuesta | Se investiga antes de reentregar o restituir. |
| Dos extracciones compiten por el mismo efectivo | Tesorería impide comprometer dos veces la existencia. |

## 20. OF-04-17. Resolver diferencias de efectivo

Propósito: resolver discrepancias entre efectivo y movimientos.

Responsable: Operaciones de efectivo.

### Desarrollo

- Abrir investigación.

- Preservar evidencia.

- Comparar registros con Fondos y Tesorería.

- Determinar hechos comprobados.

- Obtener aprobación separada.

- Ejecutar regularizaciones.

- Comprobar efectos y comunicar.

### Reglas y resultados

- Un faltante físico no se imputa automáticamente al cliente.

- El error de cuenta se distingue del error de existencias.

- Tesorería corrige existencias; Fondos corrige movimientos.

- Los créditos provisionales identifican causa y condiciones.

- La diferencia no se cierra mediante una anotación administrativa sin tratamiento económico.

- La regularización conserva una referencia que impide duplicarla.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Faltante sin evidencia de entrega | No se debita arbitrariamente al cliente. |
| Entrega comprobada sin débito | Se regulariza con fundamento y autorización. |
| Regularización repetida | No duplica el efecto. |
| Evidencia insuficiente | Continúa la investigación. |

## 21. OF-04-18. Corregir movimientos monetarios

Propósito: regularizar efectos confirmados incorrectamente.

Responsable: Fondos, a partir de una decisión válida del responsable de negocio.

### Desarrollo

- Identificar original y correcciones anteriores.

- Comprobar fundamento, importe y autorización.

- Determinar efectos relacionados.

- Ejecutar movimientos correctivos.

- Actualizar posición, reservas y descubierto.

- Informar a los responsables económicos.

- Conciliar y solicitar rectificación documental.

### Reglas y resultados

- No se borra el movimiento original.

- Solicitar devolución no autoriza a debitar a otro participante.

- Una corrección no concede financiación.

- Si no puede recuperarse el importe, se conserva una obligación o incidencia separada.

- Un error exclusivamente contable se corrige en Contabilidad.

- Los intereses afectados se recalculan en Cuentas o Préstamos y líneas, según su naturaleza.

- Una corrección parcial conserva el remanente corregible.

- No se devuelve nuevamente un importe ya regularizado por otra vía.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Cargo erróneo 20 confirmado | Corrección vinculada por el importe autorizado. |
| Beneficiario sin autorización de débito ni otro fundamento | No se recuperan sus fondos automáticamente. |
| Error solo contable | No se crea movimiento de cliente. |
| Corrección cambia la base remunerada de días anteriores | Cuentas calcula y liquida la diferencia. |
| Corrección repetida | No genera otra devolución. |

## 22. OF-04-19. Cerrar una cuenta

Propósito: finalizar su operación y resolver posiciones y vínculos.

Responsable: Cuentas.

### Desarrollo

- Registrar petición o causa.

- Comprobar facultades y procedencia.

- Impedir nuevos compromisos incompatibles.

- Obtener inventario de posiciones, instrumentos y vínculos.

- Fijar la terminación efectiva de servicios futuros.

- Liquidar remuneraciones y cargos del período parcial.

- Resolver saldos, reservas, descubierto y obligaciones.

- Coordinar instrumentos y operaciones anteriores.

- Obtener confirmaciones de los responsables.

- Retirar identificadores para nuevas operaciones.

- Registrar cierre o administración residual.

- Comunicar el resultado.

### Reglas y resultados

- Se recibe la petición aunque existan pendientes.

- Los pendientes identifican responsable y tratamiento.

- Los cargos futuros cesan desde la terminación efectiva del servicio.

- La demora interna no prolonga artificialmente el mantenimiento.

- Los rendimientos pendientes conservan su obligación de pago.

- La remuneración ordinaria del producto cesa en su fecha efectiva de terminación; una reparación por demora se trata
  separadamente.

- Los saldos a favor mantienen un destino autorizado.

- La deuda no desaparece.

- Un préstamo vinculado puede continuar con otro mecanismo de cobro.

- Las reservas firmes e inciertas no se eliminan para forzar el cierre.

- Las medidas vigentes reciben el tratamiento indicado por Restricciones.

- La falta de respuesta de un módulo no demuestra ausencia de vínculos.

- El cierre definitivo exige posición monetaria resuelta y confirmaciones suficientes.

- Los efectos posteriores utilizan una actuación residual identificada, sin reabrir silenciosamente la cuenta.

### Criterios de aceptación

| Situación | Resultado esperado |
| --- | --- |
| Saldo a favor 100 y rendimiento pendiente 5 | Ambos importes reciben tratamiento antes de concluir económicamente. |
| Extracción incierta | Se mantiene investigación y cobertura. |
| Préstamo con débito en cuenta | Se coordina otro mecanismo de cobro, sin exigir cancelación por mera asociación. |
| Cierre a mitad de mes | Se liquida el período parcial según las reglas de cada concepto. |
| Cuenta cerrada que recibe una devolución anterior | Se reconoce el derecho residual y se entrega por actuación autorizada. |
| Un servicio no confirma pendientes | No se informa cierre íntegramente completado. |

## 23. Correspondencia funcional

| Información o decisión | Responsable |
| --- | --- |
| Apertura, capacidades y cierre | Cuentas |
| Titulares y autorizaciones específicas | Cuentas |
| Remuneraciones y comisiones del servicio de cuenta | Cuentas |
| CBU, alias y correspondencias históricas | Identificación bancaria |
| Movimientos, reservas y disponibilidad | Fondos |
| Utilización inmediata de descubierto | Fondos |
| Reducción de capital por acreditaciones ordinarias | Fondos, informada a Préstamos y líneas |
| Concesión, deuda, intereses y cargos de descubierto | Préstamos y líneas |
| Imputación de pagos destinados a financiación | Préstamos y líneas |
| Cobertura de compromisos anteriores | Fondos, coordinado con Préstamos y líneas y Riesgos |
| Recepción y entrega de efectivo | Operaciones de efectivo |
| Existencias y compromisos de efectivo propio | Posiciones y liquidez |
| Fundamento y prioridad de medidas | Restricciones |
| Finalidad de pagos, transferencias e inversiones | Servicio de origen |
| Condiciones aceptadas | Contratos |
| Criterios tributarios | Criterios tributarios |
| Registración contable | Contabilidad |
| Tareas y aprobaciones humanas | Gestión administrativa |
| Comprobantes y evidencias | Documentos y comprobantes |
| Entrega de comunicaciones | Comunicaciones |
| Composición y emisión de extractos | Extractos |

Cada efecto monetario conserva su causa, autorización, cobertura, ejecución y correcciones. Las acreditaciones, los
cobros de obligaciones, los cambios de financiación y el cierre mantienen responsabilidades diferenciadas sin duplicar
dinero, deuda ni compromisos.
