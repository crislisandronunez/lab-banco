# Especificación funcional de las operaciones bancarias

## 1. Propósito

Este documento establece cómo se describen las operaciones del banco antes de definir sus contratos de integración y
desarrollar su implementación.

El modelo de negocio explica qué actividades realiza el banco. La definición de módulos y microservicios asigna sus
responsabilidades. Las especificaciones funcionales determinan cómo comienza, se desarrolla y termina cada operación,
qué condiciones debe respetar y qué consecuencias produce.

## 2. Organización documental

Las operaciones se documentan en doce documentos, uno por módulo, bajo esta estructura principal.

| Documento | Operaciones comprendidas |
| --- | --- |
| 1. Institución y oferta comercial | Administración institucional, estructura organizativa, facultades, catálogo de productos, condiciones comerciales y calendarios. |
| 2. Personas y relaciones bancarias | Registro y actualización de personas, vinculación, mantenimiento de clientes, representación y desvinculación. |
| 3. Contratación | Solicitudes de productos, preparación de acuerdos, aceptación, modificación, renovación y terminación contractual. |
| 4. Cuentas y fondos | Apertura y mantenimiento de cuentas, autorizaciones, identificación bancaria, reservas, movimientos, efectivo y cierre. |
| 5. Pagos y transferencias | Transferencias, pagos, mandatos, programaciones, débitos recurrentes, cheques, compensación y conciliación. |
| 6. Financiación | Evaluación crediticia, constitución de financiación, garantías, desembolsos, intereses, pagos, mora y recuperación. |
| 7. Tarjetas | Emisión, activación, autorización, procesamiento de consumos, cuotas, resúmenes, pagos, bloqueos y disputas. |
| 8. Ahorro e inversiones | Depósitos a plazo, instrumentos, órdenes de inversión, liquidación operativa, tenencias, valuaciones y rendimientos. |
| 9. Contabilidad | Administración contable, registración de hechos, ajustes, conciliaciones, cierres y elaboración de información. |
| 10. Tesorería | Posiciones propias, efectivo, liquidez, financiación e inversiones del banco, cambio y liquidaciones externas. |
| 11. Riesgo y cumplimiento | Políticas, exposición, verificaciones, monitoreo, investigaciones, restricciones, criterios tributarios y reportes. |
| 12. Atención y operación administrativa | Reclamos, trámites, aprobaciones humanas, documentos, conservación, comunicaciones y extractos. |

Cada documento comienza con su catálogo de operaciones y desarrolla sus especificaciones bajo títulos independientes.

## 3. Unidad de documentación

Cada especificación describe una operación de negocio con inicio, resultado y responsable identificables.

Una operación puede involucrar varios microservicios y módulos. Se documenta una sola vez, dentro del módulo que
administra su resultado principal, indicando las intervenciones de los demás.

Las actuaciones participantes conservan sus propias responsabilidades. Por ejemplo:

- Transferencias administra el traslado de fondos.

- Fondos administra la reserva y los movimientos de las cuentas.

- Contabilidad administra la registración de los hechos económicos.

- Comunicaciones administra la entrega de la confirmación.

La especificación de una transferencia reúne esas intervenciones sin volver a definir la administración interna de cada
servicio.

Las variantes se incluyen en una misma especificación cuando comparten propósito y recorrido principal. Se separan
cuando cambian sustancialmente sus condiciones, decisiones o consecuencias.

## 4. Contenido de cada especificación

### 4.1. Identificación, propósito y alcance

Establece:

- Nombre e identificador estable de la operación.

- Necesidad que resuelve.

- Resultado que pretende producir.

- Variantes comprendidas.

- Módulo y microservicio responsables.

### 4.2. Participantes

Identifica:

- Quién inicia la operación.

- En nombre de quién actúa.

- Quién decide y aprueba.

- Qué servicios participan.

- Qué terceros simulados intervienen.

- Quién conserva el seguimiento hasta su resolución.

### 4.3. Información necesaria

Define la información que debe conocerse para realizar la operación:

- Participantes y facultades.

- Productos, contratos y posiciones involucrados.

- Importes, cantidades, monedas y fechas.

- Condiciones comerciales y contractuales aplicables.

- Antecedentes, documentos y referencias necesarias.

La información se describe por su significado funcional y su responsable, sin establecer todavía formatos de mensajes o
estructuras técnicas.

### 4.4. Condiciones previas

Determina los requisitos que deben cumplirse antes de aceptar o ejecutar la operación:

- Identificación y verificaciones.

- Representación y autorizaciones.

- Contratación y habilitación del producto.

- Disponibilidad de recursos.

- Límites, restricciones y controles.

- Condiciones temporales y documentales.

Distingue las comprobaciones iniciales de las que deben repetirse o mantenerse vigentes antes de ejecutar un efecto.

### 4.5. Reglas y configuración

Distingue tres elementos:

| Elemento | Qué se documenta |
| --- | --- |
| Reglas obligatorias | Comportamientos que la operación debe respetar siempre dentro de su alcance. |
| Opciones admitidas | Modalidades que la aplicación permite seleccionar y cómo se comporta cada una. |
| Parámetros configurables | Valores que el banco administra, con sus unidades, alcance, vigencia y aprobación. |

Cuando existen cálculos, se definen bases, fórmulas, precisión, redondeo, fechas y tratamiento de diferencias.

Los valores de ejemplo sirven para comprobar el comportamiento; no se convierten en condiciones comerciales permanentes.

### 4.6. Desarrollo de la operación

Describe:

- Hecho que inicia el proceso.

- Comprobaciones y decisiones.

- Aprobaciones necesarias.

- Compromisos asumidos.

- Actuaciones de los participantes.

- Momentos de confirmación.

- Condiciones para avanzar, permanecer pendiente o finalizar.

Cada paso identifica su responsable y su resultado funcional.

### 4.7. Efectos y registros

Determina las consecuencias sobre:

- Personas y relaciones comerciales.

- Contratos y facultades.

- Cuentas, fondos y reservas.

- Límites y exposición.

- Deudas, intereses, cargos y tributos.

- Tenencias y derechos sobre instrumentos.

- Disponibilidades y liquidaciones del banco.

- Hechos contables.

- Documentos y comunicaciones.

Cada efecto identifica al servicio que lo administra y el momento en que corresponde reconocerlo.

### 4.8. Resultados y excepciones

Define el tratamiento de:

- Resultado satisfactorio.

- Rechazo antes de ejecutar efectos.

- Requisitos pendientes.

- Ejecución parcial.

- Resultado incierto.

- Cancelación solicitada.

- Vencimiento.

- Devolución, reversión o corrección.

- Intervención manual.

- Obligaciones que subsisten al finalizar.

Para cada caso se establece qué permanece vigente, qué recursos continúan comprometidos y quién debe resolverlo.

### 4.9. Criterios de aceptación

Incluye situaciones comprobables con:

- Condiciones iniciales.

- Actuación realizada.

- Resultado esperado.

- Efectos permitidos.

- Efectos que no deben producirse.

Contempla el recorrido satisfactorio y las excepciones relevantes, incluyendo repetición accidental, concurrencia y
cambios de condiciones cuando puedan afectar el resultado.

## 5. Relaciones entre operaciones

Los procesos que utilizan otras operaciones identifican esa dependencia y el resultado que necesitan recibir.

Una solicitud, su ejecución y su liquidación se distinguen cuando representan hechos diferentes. La finalización de una
actuación participante no demuestra por sí sola que terminó el proceso completo.

Los documentos mantienen una denominación consistente para participantes, productos, conceptos monetarios y resultados.

Las reglas comunes de trazabilidad contable, recuperación y autorización se aplican a todas las especificaciones. Cada
operación desarrolla únicamente su aplicación particular.

## 6. Configuración y condiciones contratadas

Las especificaciones establecen qué puede configurar el banco y qué comportamiento admite cada opción.

Todo parámetro relevante identifica:

- Significado y unidad.

- Producto u operación alcanzados.

- Valores y combinaciones admitidos.

- Vigencia.

- Facultad necesaria para aprobarlo.

- Tratamiento de cambios.

- Consecuencias sobre operaciones pendientes y contratos existentes.

Publicar una condición nueva no modifica automáticamente acuerdos anteriores. Cada operación determina qué versión
comercial, contractual o normativa debe utilizar.

## 7. Alcance de esta etapa

Esta documentación define comportamientos funcionales suficientemente precisos para diseñar su integración y comprobar
posteriormente su implementación.

Los contratos de integración establecerán después las solicitudes, respuestas y hechos intercambiados entre servicios.

La arquitectura técnica determinará los mecanismos de comunicación, persistencia, seguridad y coordinación necesarios
para cumplir estas especificaciones.

## 8. Criterios de cierre

Cada documento de operaciones se considera completo cuando:

- Cubre las responsabilidades operativas asignadas a su módulo.

- Cada operación tiene un responsable y un resultado definidos.

- Las decisiones, cálculos y opciones configurables tienen comportamiento explícito.

- Los efectos sobre otros módulos están identificados y respetan sus autoridades.

- Los errores, pendientes y correcciones tienen tratamiento.

- Los criterios de aceptación permiten comprobar los resultados.

- No quedan decisiones funcionales indispensables delegadas a suposiciones durante la programación.

- Mantiene coherencia con los otros documentos y con las reglas transversales.
