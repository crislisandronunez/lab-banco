# Diseño técnico — Módulo 4. Cuentas y fondos

## 1. Propósito y alcance

El módulo administra las cuentas bancarias, sus condiciones operativas, sus identificadores y sus posiciones monetarias.

Comprende:

- Apertura y habilitación.

- Titularidades y autorizaciones.

- Capacidades y restricciones.

- Identificadores bancarios.

- Saldos, movimientos y disponibilidad.

- Reservas y afectaciones.

- Utilización inmediata de descubierto.

- Liquidaciones propias de cuenta.

- Depósitos y extracciones de efectivo.

- Correcciones.

- Cierre y administración residual.

Cada efecto conserva su causa, autorización, cobertura, ejecución y correcciones.

La apertura no acredita dinero. Una reserva no es un débito. Una autorización no demuestra entrega de efectivo.

## 2. Servicios y responsabilidades

| Servicio                | Identificador técnico       | Base de datos          | Responsabilidad                                                    |
|-------------------------|-----------------------------|------------------------|--------------------------------------------------------------------|
| Cuentas                 | account-service             | account_db             | Ciclo de vida, participantes, capacidades, liquidaciones y cierre. |
| Identificación bancaria | bank-identification-service | bank_identification_db | CBU, alias y correspondencias históricas.                          |
| Fondos                  | funds-service               | funds_db               | Posiciones, movimientos, reservas y afectaciones monetarias.       |
| Operaciones de efectivo | cash-operation-service      | cash_operation_db      | Recepción y entrega física simulada, coordinación y diferencias.   |

Cada servicio administra su base PostgreSQL, usuario y migraciones.

Fondos mantiene las posiciones de las cuentas dentro de su base. Los conjuntos internos que deben ser indivisibles se
confirman mediante una transacción local.

No existen consultas SQL ni relaciones JPA entre bases de servicios.

### 2.1. Autoridades relacionadas

| Información o decisión                  | Responsable              |
|-----------------------------------------|--------------------------|
| Acuerdo y participantes contractuales   | Contratos.               |
| Titularidad y autorizaciones operativas | Cuentas.                 |
| Representación general                  | Representación.          |
| Facultades institucionales              | Institución.             |
| Concesión y deuda de descubierto        | Préstamos y líneas.      |
| Utilización monetaria inmediata         | Fondos.                  |
| Fundamento y prioridad de medidas       | Restricciones.           |
| Existencias físicas del banco           | Posiciones y liquidez.   |
| Recepción o entrega por operación       | Operaciones de efectivo. |
| Contabilidad general                    | Registración contable.   |

Fondos no concede financiación ni determina la procedencia comercial de una operación.

## 3. Convenciones técnicas

### 3.1. Tipos

| Dato                  | Java        | PostgreSQL     | HTTP                 |
|-----------------------|-------------|----------------|----------------------|
| Identificador interno | UUID        | uuid           | Cadena UUID.         |
| Versión o secuencia   | long        | bigint         | Entero positivo.     |
| Instante              | Instant     | timestamptz    | ISO 8601 en UTC.     |
| Fecha                 | LocalDate   | date           | YYYY-MM-DD.          |
| Importe               | BigDecimal  | numeric(24,8)  | Cadena decimal.      |
| Tasa o coeficiente    | BigDecimal  | numeric(24,12) | Cadena decimal.      |
| Moneda                | Enumeración | varchar(3)     | ARS o USD.           |
| Código                | String      | varchar(80)    | Cadena normalizada.  |
| Referencia externa    | String      | varchar(120)   | Cadena.              |
| Fundamento            | String      | varchar(4000)  | Cadena.              |
| Resumen de contenido  | String      | char(64)       | SHA-256 hexadecimal. |

Los importes ejecutables respetan centavos. Fondos rechaza una escala incompatible; no redondea una instrucción
silenciosamente.

Los cálculos conservan al menos doce decimales intermedios. Las liquidaciones monetarias utilizan HALF_UP.

La zona institucional es America/Argentina/Buenos_Aires.

### 3.2. Identidades

Se distinguen:

- Operación de negocio.

- Actuación.

- Efecto económico.

- Cuenta.

- Intento de ejecución.

- Reserva.

- Movimiento.

- Conjunto monetario.

- Medida.

- Operación de efectivo.

- Corrección.

La misma consecuencia conserva effectId aunque llegue mediante otro servicio o interfaz.

Un efecto ya ejecutado mediante consumo de reserva no se repite mediante un débito independiente.

### 3.3. Contexto y resultados

Cada comando identifica actuación, operación, finalidad, objeto, versión esperada y evidencias.

El actor y el servicio emisor proceden de la autenticación.

Las respuestas distinguen PENDING, CONFIRMED, PARTIALLY_CONFIRMED, REJECTED, UNCERTAIN y CONFLICT.

Cada respuesta informa movimientos, recursos comprometidos, remanentes y pendientes.

La falta de respuesta no demuestra rechazo ni ausencia de ejecución.

## 4. Cuentas

### 4.1. Datos

| Campo                        | Regla                                             |
|------------------------------|---------------------------------------------------|
| accountId                    | Identidad estable.                                |
| accountReference             | Referencia visible única.                         |
| constitutionId               | Constitución de origen, única.                    |
| applicationItemId            | Componente solicitado.                            |
| contractId y contractVersion | Acuerdo aplicado.                                 |
| productId y productVersion   | Producto.                                         |
| accountType                  | Caja de ahorro, cuenta sueldo o cuenta corriente. |
| currency                     | Moneda inmutable.                                 |
| lifecycleStatus              | Ciclo de vida.                                    |
| operationalVersion           | Versión de condiciones.                           |
| openedAt                     | Constitución.                                     |
| serviceEndedOn               | Terminación efectiva del servicio.                |
| closedAt                     | Cierre definitivo.                                |

Los participantes, autorizaciones, capacidades y condiciones económicas se conservan en estructuras relacionadas y
versionadas.

### 4.2. Estados

- CONSTITUTING.

- CONSTITUTED.

- ACTIVE.

- CLOSING.

- RESIDUAL_ADMINISTRATION.

- CLOSED.

Las restricciones se conservan por acción y alcance. No se reducen a un único estado de bloqueo.

Una cuenta puede admitir acreditaciones y tener débitos impedidos.

### 4.3. Participantes y capacidades

Cada autorización identifica persona, rol, actos, límites, moneda, actuación individual o conjunta, vigencia y
fundamento.

Un autorizado no adquiere propiedad.

La actuación conjunta corresponde a idéntico contenido: cuenta, acto, importe, moneda, destino y finalidad.

Cambiar titulares requiere formalización contractual.

Cada capacidad conserva condición prevista, dependencias y aplicación confirmada.

Caja de ahorro y cuenta sueldo no admiten descubierto. La cuenta corriente requiere financiación expresamente contratada
y aplicada.

### 4.4. Apertura

Cuentas conserva el proceso y coordina:

- Verificación del contrato.

- Registro de cuenta y participantes.

- Inicialización de posición cero en Fondos.

- Asignación de identificadores.

- Confirmación de capacidades.

- Comunicación a Solicitudes de productos.

Inicializar la posición no genera movimiento ni concede crédito.

Las actuaciones utilizan referencias estables. Una dependencia pendiente impide informar habilitación completa.

### 4.5. Resolución de comprobaciones pendientes

Cuentas recupera la comprobación por su identificador, verifica que corresponda a la cuenta y comprueba que el
solicitante tenga acceso. Si existe una decisión definitiva, devuelve el resultado conservado.

Para resolver una comprobación pendiente, Cuentas usa el contenido original y evalúa sus condiciones operativas
vigentes. La decisión queda vinculada a la versión de cuenta evaluada. Si falta información indispensable o falla
una dependencia necesaria para decidir, la comprobación sigue pendiente.

La confirmación de la decisión exige que la comprobación siga pendiente y que la versión operativa evaluada continúe
vigente. Si otra solicitud ya confirmó una decisión, se devuelve ese resultado. Si cambió la versión antes de confirmar,
se vuelve a evaluar con las condiciones vigentes.

## 5. Condiciones operativas y restricciones

Cuentas conserva versiones de capacidades, participantes, permisos, medidas y limitaciones de cierre.

Fondos mantiene la aplicación local necesaria para ejecutar:

- Acciones admitidas.

- Versión operativa.

- Restricciones monetarias.

- Medidas pendientes que impiden decidir.

- Barreras de cierre.

La copia local no sustituye la autoridad de origen.

### 5.1. Aplicación de cambios

Cada cambio identifica:

- Autoridad.

- Objeto.

- Versión.

- Momento de efecto.

- Alcance.

- Tratamiento de operaciones anteriores.

- Servicios afectados.

- Confirmaciones.

La recepción, el efecto y la aplicación se conservan separadamente.

Un mensaje antiguo no sustituye una versión posterior.

Una discontinuidad impide utilizar una supuesta versión vigente hasta recuperar la información necesaria.

### 5.2. Operaciones realizadas

Una medida posterior no convierte una operación realizada en inexistente.

Las medidas con efecto anterior a su recepción requieren revisar las actuaciones alcanzadas y determinar su tratamiento.

Una reserva anterior no garantiza que pueda consumirse después de una medida aplicable.

## 6. Protocolo de confirmación monetaria

### 6.1. Propósito

La ejecución debe ordenar disponibilidad, autorizaciones, restricciones y cancelaciones.

Una consulta favorable no constituye una autorización permanente.

Para los efectos que requieren comprobar condiciones administradas por otros servicios se utiliza preparación
persistente y decisión durable, conforme al protocolo común de confirmación.

### 6.2. Intento

Fondos conserva:

| Campo                 | Contenido                           |
|-----------------------|-------------------------------------|
| executionAttemptId    | Identidad estable.                  |
| actionId              | Petición original.                  |
| operationId           | Operación de negocio.               |
| effectReferences      | Efectos individuales.               |
| accountReferences     | Cuentas afectadas.                  |
| contentHash           | Contenido exacto.                   |
| requiredAuthorities   | Autoridades necesarias.             |
| preparationReferences | Preparaciones obtenidas.            |
| resourceCommitments   | Coberturas locales.                 |
| decisionDeadline      | Límite de confirmación.             |
| decision              | Pendiente, confirmar o abortar.     |
| resultReferences      | Reservas o movimientos resultantes. |
| version               | Concurrencia.                       |

Los estados son preparación, listo para decidir, confirmado, abortado y recuperación requerida.

### 6.3. Autoridades participantes

Participan únicamente las autoridades exigibles para el efecto:

- Cuentas, por capacidades y permisos específicos.

- Representación, cuando el acto lo requiera.

- Restricciones, por medidas aplicables.

- Institución, para decisiones internas que lo exijan.

- Responsable del negocio, por la actuación habilitante.

Un resultado de fraude u otro control se vincula al contenido y se comprueba dentro de su alcance.

No se exige una nueva intervención del cliente para regularizar un hecho ya realizado cuando existe otro fundamento
válido, como una entrega de efectivo comprobada.

### 6.4. Preparación

La autoridad recibe intento, contenido, actores, acto y objetos.

Comprueba sus condiciones y registra una preparación específica.

Si ya existe una revocación o restricción incompatible, no prepara favorablemente.

La preparación:

- No reserva dinero por sí sola.

- No amplía facultades.

- No levanta medidas.

- No puede reutilizarse para otro efecto.

- Mantiene seguimiento hasta conocer la decisión.

### 6.5. Cambios concurrentes

La autoridad ordena preparaciones y cambios incompatibles sobre su estado.

Un cambio que alcance un intento preparado:

- Impide nuevas preparaciones incompatibles.

- Conserva recepción y momento de efecto.

- Consulta el intento.

- Solicita aborto si todavía no existe decisión definitiva.

- Comprueba el resultado.

- Aplica el tratamiento correspondiente.

Fondos ordena las peticiones de aborto y su confirmación local bajo el mismo control del intento.

Una modificación no invalida silenciosamente una preparación mientras otro participante presume que sigue siendo
utilizable.

Una medida urgente se recibe y tramita inmediatamente. Su resultado distingue intentos abortados, efectos ya confirmados
y actuaciones pendientes.

### 6.6. Recursos locales

Las coberturas necesarias se conservan antes de ejecutar.

Si se crea un compromiso técnico para un intento, este se identifica como parte de la misma operación y se incluye una
sola vez en disponibilidad.

Cuando ya existe una reserva, no se crea otra por el mismo importe.

Los incrementos requieren recursos adicionales.

### 6.7. Decisión y ejecución

Fondos bloquea intento, posiciones, reservas y controles locales en orden estable.

Dentro de la transacción comprueba:

- Identidad del efecto.

- Contenido.

- Preparaciones completas.

- Vigencias.

- Ausencia de aborto confirmado.

- Condiciones locales.

- Disponibilidad o cobertura.

- Remanentes.

La misma transacción registra:

- Decisión confirmada.

- Reserva o movimientos correspondientes.

- Consumos.

- Posiciones.

- Historial.

- Idempotencia.

- Outbox.

No existe decisión monetaria confirmada sin sus efectos locales.

### 6.8. Aborto y recuperación

Un aborto confirmado impide ejecutar ese intento.

Se regularizan las coberturas técnicas cancelables y se notifica a las autoridades.

Una reserva que respalde otra obligación firme o incierta conserva su tratamiento. Abortar un intento de consumo no
autoriza a liberar toda la reserva.

Ante una respuesta perdida se consulta la decisión original.

Las preparaciones no se liberan por timeout. Al vencer el límite se solicita resolver el intento.

Si Fondos no está disponible, se mantiene pendiente y se escala. No se presume ejecución ni aborto.

### 6.9. Alcance físico

La entrega de efectivo tiene su propio punto de ejecución en Operaciones de efectivo.

La preparación para entregar y la preparación para registrar el débito son actuaciones relacionadas, pero diferentes.

Una revocación anterior a una entrega todavía no ejecutada puede impedirla.

Después de una entrega comprobada, su débito se trata como cumplimiento o regularización de ese hecho. No se registra
una entrega ficticiamente fallida para evitar reconocerlo.

## 7. Identificación bancaria

Cada identificador conserva tipo, valor normalizado, cuenta, vigencia, situación y actuación de origen.

El CBU simulado es una cadena de 22 dígitos con validación estructural y verificadores.

No se reutiliza para otra cuenta.

Los alias se normalizan a minúsculas, tienen entre 6 y 20 caracteres y admiten letras ASCII, números, punto y guion.

Se exige:

- Una asignación vigente por alias.

- Como máximo un alias vigente por cuenta.

- Historia sin superposición.

- Sustitución confirmada antes de retirar la asignación anterior.

El directorio simulado utiliza referencias idempotentes y consulta de resultados.

Una resolución devuelve destino, fuente y momento. No autoriza a operar.

Las instrucciones aceptadas conservan su destino original aunque cambie el alias.

El cierre retira nuevos usos y conserva la correspondencia histórica.

## 8. Posición y disponibilidad

Fondos conserva por cuenta:

- Saldo registrado.

- Reservas.

- Inmovilizaciones.

- Créditos no disponibles.

- Límite de descubierto.

- Capital utilizado.

- Cobertura protegida.

- Capacidad nueva.

- Exceso.

- Secuencia.

- Condiciones aplicadas.

El saldo registrado es la suma de movimientos confirmados.

### 8.1. Recursos

Cada afectación identifica causa, importe y recursos alcanzados.

Las superposiciones se representan expresamente para no descontar dos veces el mismo recurso.

La capacidad considera fondos propios utilizables y crédito admitido, descontando compromisos y afectaciones conforme a
su alcance.

La cobertura protegida no integra capacidad para operaciones nuevas.

Las inmovilizaciones no utilizan descubierto.

Las reservas utilizan primero fondos propios y después crédito cuando esté admitido.

### 8.2. Consultas

La respuesta presenta todos los componentes, moneda y corte.

Los movimientos utilizan paginación con cursor y secuencia de corte estable.

La fecha de valor no modifica retrospectivamente la disponibilidad que existió.

Los saldos materializados se actualizan junto con los movimientos y pueden reconstruirse desde su historia.

## 9. Reservas

### 9.1. Datos

Cada reserva contiene:

- Identidad.

- Cuenta y moneda.

- Importe original y vigente.

- Consumido.

- Liberado.

- Remanente.

- Firmeza.

- Responsable de negocio.

- Solicitante.

- Circuito.

- Ejecutores admitidos.

- Efectos cubiertos.

- Cobertura propia y crediticia.

- Cobertura protegida.

- Revisión.

- Versión.

La firmeza distingue cancelable, firme e incierta.

El vencimiento inicia revisión. No libera automáticamente.

### 9.2. Consumo

El consumo registra conjuntamente débito y reducción del remanente.

No descuenta nuevamente recursos ya comprometidos.

El consumo parcial conserva el resto.

Los cambios de condiciones se comprueban mediante el protocolo de confirmación correspondiente.

### 9.3. Ejecutores

El permiso deriva del circuito autorizado.

En tarjeta de débito:

- Autorizaciones constituye la reserva.

- Procesamiento de consumos puede consumirla para una presentación válida.

- Operaciones de efectivo puede consumirla para una extracción vinculada.

Conocer la reserva no concede permiso.

Una presentación posterior de una extracción ya debitada recupera su efecto.

### 9.4. Liberación y cambios

La liberación exige evidencia de que el importe ya no respalda ejecución u obligación.

No genera crédito.

Consumo y liberación se ordenan sobre la misma reserva.

Aumentar requiere capacidad adicional. Reducir requiere justificar el remanente liberable.

Una reserva incierta no se convierte administrativamente en cancelable.

## 10. Movimientos y conjuntos internos

Cada movimiento conserva cuenta, moneda, dirección, importe, concepto, efecto, operación, fechas, secuencia y
correcciones.

Los movimientos confirmados son inmutables.

### 10.1. Créditos condicionados

El crédito identifica condición, responsable, evidencia, importe pendiente y parcialidad admitida.

Cumplir la condición elimina únicamente esa indisponibilidad.

No acredita nuevamente ni levanta otras medidas.

El responsable se fija al registrar la condición. No puede sustituirse mediante la solicitud de liberación.

### 10.2. Transferencia interna

Se exige origen diferente de destino, misma moneda e igual principal en débito y crédito.

Fondos confirma el conjunto en una transacción, incluyendo consumos de reserva.

Si el destino no admite crédito, no confirma solo el débito.

Los cargos se identifican separadamente.

### 10.3. Intercambio de monedas

Se requiere operación de Cambio de moneda, propuesta aceptada, cotización, ambas monedas, importes y requisitos de
recursos propios.

Fondos no calcula ni sustituye la cotización.

El conjunto confirma conjuntamente los movimientos que le pertenecen.

No se exige igualdad numérica entre monedas diferentes.

La atomicidad local no sustituye los efectos propios de Tesorería ni las entregas externas.

## 11. Descubierto

Préstamos y líneas concede financiación y mantiene deuda e intereses.

Fondos controla utilización monetaria inmediata.

Cada límite conserva autorización, financiación, versión, moneda, vigencia y momento de efecto.

El aumento no está disponible antes de confirmarse.

### 11.1. Acreditaciones y cargos

Una acreditación ordinaria sobre saldo negativo reduce directamente capital.

Los intereses pendientes no se cobran implícitamente.

Su cobro exige una actuación separada y utiliza exclusivamente fondos propios disponibles.

Si Préstamos solicita un pago destinado a capital de descubierto, la acreditación y la reducción informada por Fondos
constituyen una sola cancelación de capital.

### 11.2. Reducciones

Una reducción o vencimiento:

- Impide nuevos usos incompatibles.

- Conserva capital.

- Identifica reservas anteriores.

- Revisa cancelables desde las más recientes.

- Conserva firmes e inciertas.

- Mantiene cobertura protegida.

- Comunica exceso y compromisos.

Con capital 20, reserva firme 70 y nuevo límite 40, la cobertura adicional protegida es 50.

Consumida esa reserva, quedan capital 90 y exceso 50.

La cobertura protegida no es un aumento comercial.

### 11.3. Conciliación

Cada variación tiene identidad y secuencia.

Préstamos la reconoce una sola vez.

Las discontinuidades se recuperan antes de cerrar cálculos afectados.

Una diferencia no autoriza a borrar deuda ni ampliar capacidad.

## 12. Liquidaciones propias de cuenta

Cuentas administra remuneración, comisiones de servicio y tributos propios.

Los intereses y cargos crediticios pertenecen a Préstamos y líneas.

### 12.1. Remuneración

Se utiliza interés simple diario Actual/365:

$I = \max_{}(S_{d},0)\frac{j_{d}}{365}$

La base es saldo registrado al cierre diario.

Reservas e inmovilizaciones no la reducen.

Los créditos confirmados no disponibles integran saldo. Los pendientes no.

No se redondea diariamente.

El período ordinario es mensual. Se acredita el primer hábil siguiente y se liquida el período parcial al terminar el
servicio.

### 12.2. Comisiones

Se admiten:

- Importe fijo por hecho.

- Porcentaje por hecho.

- Importe fijo mensual proporcional a días de servicio.

Para el mensual:

$C = \frac{F_{d}}{D_{m}}$

Se incluye inicio y se excluye terminación efectiva.

Las bonificaciones se aplican antes de tributos.

La liquidación identifica cuenta, concepto y período u ocurrencia.

Calculado no significa cobrado. Sin fondos propios queda pendiente, sin descubierto automático.

Las correcciones generan diferencias vinculadas.

## 13. Operaciones de efectivo

La operación conserva cuenta, actor, moneda, importe declarado, importe comprobado, ubicación, ejecutor, coberturas,
instrucción física y evidencias.

Se separan preparación, cobertura monetaria, cobertura física, resultado físico, movimiento y conciliación.

### 13.1. Depósito

Se registra declaración, se comprueba recepción y se determina el importe aceptado.

Luego se coordinan existencias y acreditación.

Recibir efectivo genera obligación de acreditar o restituir.

Una declaración de 100 con recepción de 90 no permite acreditar 100 sin otro fundamento.

### 13.2. Extracción

El proceso:

- Comprobar actor y cuenta.

- Obtener compromiso físico.

- Constituir o reutilizar reserva monetaria.

- Preparar ejecución bajo condiciones vigentes.

- Conservar instrucción física estable.

- Entregar.

- Comprobar resultado.

- Consumir cobertura por el importe entregado.

- Resolver remanentes.

- Conciliar.

El ejecutor simulado distingue instrucción aceptada, entrega confirmada, fallo comprobado e incertidumbre.

Una entrega incierta no se repite ni libera por timeout.

Una entrega comprobada conserva la obligación de regularizar su débito.

### 13.3. Parcialidad y diferencias

La parcialidad requiere modalidad admitida.

Si se entregan 60 de 100, se debitan 60 y se resuelve la cobertura restante.

Las diferencias conservan evidencia, investigación, aprobación y tratamiento económico.

Un faltante físico no se imputa automáticamente al cliente.

Tesorería corrige existencias. Fondos corrige movimientos.

## 14. Correcciones y cierre

### 14.1. Correcciones

Cada corrección identifica original, alcance, importe, causa, aprobaciones y efectos previos.

Se controla el remanente corregible.

Un cargo de 20 con 12 corregidos deja como máximo 8 por ese alcance.

No se elimina el original, no se concede financiación ni se debita a terceros sin fundamento.

Las consecuencias sobre intereses se derivan al propietario del cálculo.

### 14.2. Cierre

Cuentas registra petición, legitimación, fecha de terminación, barrera, vínculos, posiciones y responsables.

Los servicios afectados confirman la limitación de nuevos compromisos antes del inventario final.

Fondos ordena la barrera frente a sus intentos:

- No admite intentos nuevos incompatibles.

- Identifica los preparados.

- Resuelve los todavía abortables.

- Conserva los efectos confirmados y las obligaciones anteriores.

- Confirma posición y pendientes.

La falta de respuesta no equivale a ausencia de vínculos.

Los cargos futuros cesan conforme a la terminación efectiva.

Las reservas firmes o inciertas no se eliminan para forzar cierre.

Las devoluciones posteriores utilizan actuaciones residuales, sin reabrir silenciosamente la cuenta.

## 15. Interfaces HTTP

### 15.1. Cuentas

| Contrato  | Interfaz                                     | Resultado                           |
|-----------|----------------------------------------------|-------------------------------------|
| CI-04-001 | POST /accounts                               | Cuenta y habilitación.              |
| CI-04-002 | GET /accounts/{id}                           | Condiciones actuales o históricas.  |
| CI-04-003 | POST /accounts/{id}/participant-changes      | Participantes y permisos aplicados. |
| CI-04-004 | POST /accounts/{id}/operational-changes      | Capacidades y pendientes.           |
| CI-04-005 | POST /accounts/{id}/restriction-applications | Aplicación de medida y versión.     |
| CI-04-006 | GET /account-actions/{actionId}              | Resultado original.                 |
| CI-04-007 | POST /accounts/{id}/closures                 | Cierre o administración residual.   |
| CI-04-025 | POST /accounts/{id}/settlements              | Bases, conceptos y ejecución.       |
| CI-04-040 | POST /accounts/{id}/operation-checks         | Facultades, alcance y versión.      |
| CI-04-041 | Solicitud de revalidación por cuenta y comprobación | Decisión de la comprobación original. |

La comprobación informativa y su revalidación se gestionan por separado de la preparación para un intento monetario.

### 15.2. Identificación

| Contrato  | Interfaz                                    | Resultado                 |
|-----------|---------------------------------------------|---------------------------|
| CI-04-008 | POST /bank-identifiers/assignments          | Identificador y vigencia. |
| CI-04-008 | POST /bank-identifiers/{id}/retirements     | Retiro de nuevos usos.    |
| CI-04-009 | POST /accounts/{id}/alias-changes           | Asignación o seguimiento. |
| CI-04-010 | POST /bank-identifier-resolutions           | Destino y fuente.         |
| CI-04-011 | GET /bank-identification-actions/{actionId} | Resultado del directorio. |

### 15.3. Fondos

| Contrato  | Interfaz                                  | Resultado                           |
|-----------|-------------------------------------------|-------------------------------------|
| CI-04-012 | GET /fund-positions/{accountId}           | Posición descompuesta.              |
| CI-04-012 | GET /fund-positions/{accountId}/movements | Historia con corte estable.         |
| CI-04-013 | POST /fund-reservations                   | Reserva y cobertura.                |
| CI-04-014 | POST /fund-reservations/{id}/consumptions | Débito y remanente.                 |
| CI-04-015 | POST /fund-reservations/{id}/releases     | Liberación comprobada.              |
| CI-04-016 | POST /fund-reservations/{id}/changes      | Importe o firmeza.                  |
| CI-04-017 | POST /fund-movements                      | Movimiento y posición.              |
| CI-04-018 | POST /internal-movement-groups            | Conjunto indivisible.               |
| CI-04-019 | POST /overdraft-limit-applications        | Límite aplicado.                    |
| CI-04-020 | GET /overdraft-utilizations               | Capital, cobertura y exceso.        |
| CI-04-021 | POST /fund-movements/{id}/corrections     | Ajuste y remanente.                 |
| CI-04-022 | GET /fund-actions/{actionId}              | Actuación y efectos.                |
| CI-04-023 | GET /fund-reservations/{id}               | Estado y circuito.                  |
| CI-04-024 | POST /monetary-affectation-actions        | Medida o habilitación condicionada. |

El seguimiento de los comandos monetarios incorpora:

- GET /fund-execution-attempts/{id}.

- POST /fund-execution-attempts/{id}/abort-requests.

Estas operaciones recuperan o resuelven el intento original. No crean otro efecto económico.

CI-04-024 utiliza modalidades excluyentes: medidas solicitadas por Restricciones y habilitación solicitada por el
responsable del crédito condicionado.

### 15.4. Efectivo

| Contrato  | Interfaz                                    | Resultado                             |
|-----------|---------------------------------------------|---------------------------------------|
| CI-04-026 | POST /cash-deposits                         | Recepción declarada y comprobaciones. |
| CI-04-027 | POST /cash-withdrawals                      | Coberturas y preparación.             |
| CI-04-028 | POST /cash-operations/{id}/physical-results | Hecho físico y tratamiento monetario. |
| CI-04-029 | POST /cash-differences/{id}/resolutions     | Regularizaciones.                     |
| CI-04-030 | GET /cash-operations/{id}                   | Resultados y pendientes.              |

Las interfaces utilizan JSON y OpenAPI, con contexto común y campos específicos tipados.

Las respuestas usan 200, 201 y 202. Los errores usan Problem Details con 400, 401, 403, 404, 409, 422 y 503.

## 16. Eventos

| Contrato  | Evento                        | Contenido                                  |
|-----------|-------------------------------|--------------------------------------------|
| CI-04-031 | AccountChanged                | Capacidades, versión y pendientes.         |
| CI-04-032 | BankIdentificationChanged     | Asignaciones y vigencias.                  |
| CI-04-033 | FundReservationChanged        | Importes, firmeza y cobertura.             |
| CI-04-034 | MonetaryMovementConfirmed     | Movimiento, efecto y conjunto.             |
| CI-04-035 | OverdraftUtilizationChanged   | Capital, límite y exceso.                  |
| CI-04-036 | AccountSettlementRecorded     | Cálculo y ejecución.                       |
| CI-04-037 | CashOperationOutcomeRecorded  | Hecho físico y diferencias.                |
| CI-04-038 | AccountClosureOutcomeRecorded | Cese, cierre o régimen residual.           |
| CI-04-039 | MonetaryAffectationApplied    | Alcance, insuficiencia y causas restantes. |

Los temas son:

- banking.account.events.

- banking.bank-identification.events.

- banking.funds.events.

- banking.cash-operation.events.

La clave corresponde al agregado. Los movimientos de Fondos conservan secuencia por cuenta y referencia de conjunto.

Un evento de reserva no representa otro débito. El de liquidación no demuestra cobro. El de efectivo no sustituye el
movimiento.

## 17. Persistencia, seguridad y recuperación

Cada servicio mantiene datos propios, actuaciones, idempotencia, outbox, inbox y trabajos persistentes.

Fondos conserva además intentos, preparaciones, decisiones y referencias de recursos.

Se exige unicidad de constitución, efecto, movimiento, secuencia de cuenta, asignación vigente y liquidación por
concepto y período.

La confirmación incluye efecto local, posición, historial, idempotencia y outbox.

No se mantienen transacciones durante HTTP, Kafka o ejecución física.

Las decisiones y preparaciones se recuperan por referencia original.

Keycloak autentica actores y servicios. Los permisos distinguen constitución, consumo, liberación, corrección,
aplicación de límites y medidas.

Un ejecutor de reserva no obtiene permiso general para modificarla.

PostgreSQL es autoridad de posiciones y decisiones. Redis no administra saldos, reservas, restricciones, límites ni
idempotencia.

Los documentos permanecen en Documentos. No se incorpora MongoDB para duplicar el registro monetario.

## 18. Observabilidad y pruebas

Se observan:

- Habilitaciones pendientes.

- Intentos preparados sin decisión.

- Cambios de condiciones pendientes de aplicación.

- Reservas firmes e inciertas.

- Cobertura protegida.

- Excesos.

- Entregas físicas inciertas.

- Diferencias de conciliación.

- Cierres pendientes.

- Retraso de outbox.

Las pruebas incluyen:

- Revocación anterior y posterior a preparación.

- Aborto concurrente con ejecución.

- Caída después de preparar y antes de decidir.

- Decisión confirmada con respuesta perdida.

- Dos reservas compitiendo por capacidad.

- Consumo frente a liberación.

- Restricción que alcanza una reserva previa.

- Conjunto interno con destino impedido.

- Cambio de moneda con ambas partidas.

- Extracción ya debitada y presentación posterior.

- Reducción de descubierto con cobertura protegida.

- Liberación de crédito con otra medida vigente.

- Cierre concurrente con intentos preparados.

- Devolución residual después del cierre.

La concurrencia y unicidad se verifican con PostgreSQL real. Los simuladores permiten detener llamadas y comprobar que
las recuperaciones no duplican dinero ni liberan coberturas indebidamente.

## 19. Correspondencia funcional

| Operación                      | Implementación                          | Contratos                  |
|--------------------------------|-----------------------------------------|----------------------------|
| OF-04-01. Apertura             | Constitución y capacidades.             | CI-04-001, 008 y 031.      |
| OF-04-02. Autorizaciones       | Participantes, comprobación y revalidación. | CI-04-003, 040 y 041. |
| OF-04-03. Condiciones          | Versiones y aplicación.                 | CI-04-004 y 031.           |
| OF-04-04. Restricciones        | Coordinación y afectación.              | CI-04-005, 024 y 039.      |
| OF-04-05. Identificadores      | Asignaciones.                           | CI-04-008 y 032.           |
| OF-04-06. Alias                | Sustitución confirmada.                 | CI-04-009 y 011.           |
| OF-04-07. Resolución           | Destino y fuente.                       | CI-04-010.                 |
| OF-04-08. Posición             | Corte e historia.                       | CI-04-012.                 |
| OF-04-09. Reserva              | Cobertura indivisible.                  | CI-04-013 y 033.           |
| OF-04-10. Administrar reservas | Consumo, liberación y revisión.         | CI-04-014, 015, 016 y 023. |
| OF-04-11. Movimiento           | Decisión y efecto local.                | CI-04-017, 022 y 034.      |
| OF-04-12. Conjuntos            | Movimientos relacionados.               | CI-04-018.                 |
| OF-04-13. Descubierto          | Utilización y conciliación.             | CI-04-019, 020 y 035.      |
| OF-04-14. Liquidación          | Cálculo y ejecución separada.           | CI-04-025 y 036.           |
| OF-04-15. Depósito             | Recepción y acreditación.               | CI-04-026 y 028.           |
| OF-04-16. Extracción           | Coberturas y entrega.                   | CI-04-027, 028 y 030.      |
| OF-04-17. Diferencias          | Investigación y regularización.         | CI-04-029 y 037.           |
| OF-04-18. Correcciones         | Efectos vinculados.                     | CI-04-021 y 022.           |
| OF-04-19. Cierre               | Barrera, inventario y régimen residual. | CI-04-002, 006, 007 y 038. |
