# LAB-BANCO — Catálogo y mapa de conocimientos de los 51 microservicios

## 1. Catálogo de microservicios

### Módulo 1 — Institución y oferta comercial

**01. Institución**
- **Negocio:** estructura institucional, sucursales, jerarquías, facultades y delegaciones.
- **Aprendizaje técnico:** modelado jerárquico, autorización contextual, relaciones entre entidades, validaciones de dominio, versionado, persistencia histórica y auditoría.
- **Desafío:** impedir delegaciones inválidas y preservar decisiones históricas ante reorganizaciones.

**02. Catálogo de productos**
- **Negocio:** productos bancarios, variantes, elegibilidad, compatibilidad, publicación y retiro.
- **Aprendizaje técnico:** catálogos empresariales, reglas configurables, máquinas de estados, versionado, persistencia, validaciones condicionales y compatibilidad histórica.
- **Desafío:** modificar productos sin alterar contratos existentes.

**03. Condiciones comerciales**
- **Negocio:** tasas, tarifas, comisiones, bonificaciones, límites y vigencias.
- **Aprendizaje técnico:** BigDecimal, precisión y redondeo, motores de reglas, prioridades, consultas temporales, configuraciones versionadas y auditoría de cálculos.
- **Desafío:** resolver condiciones comerciales superpuestas y reproducir cálculos históricos.

**04. Calendarios bancarios**
- **Negocio:** días hábiles, feriados, jurisdicciones, vencimientos y convenciones bancarias.
- **Aprendizaje técnico:** Java Time API, algoritmos temporales, zonas horarias, series recurrentes, versionado inmutable, validación de cobertura y pruebas deterministas.
- **Desafío:** calcular fechas correctamente atravesando feriados y cambios de mes o año.

### Módulo 2 — Personas y relaciones bancarias

**05. Personas**
- **Negocio:** identidad de personas humanas y jurídicas, relaciones societarias y duplicados.
- **Aprendizaje técnico:** entidades complejas, normalización, relaciones, restricciones únicas, detección de duplicados, procedencia de datos, persistencia histórica y protección de información.
- **Desafío:** resolver identidades duplicadas sin perder referencias.

**06. Vinculación**
- **Negocio:** admisión de clientes, verificaciones, documentación, aprobaciones y rechazos.
- **Aprendizaje técnico:** workflows, máquinas de estados, integración entre servicios, asincronía, timeouts, idempotencia, persistencia del progreso y recuperación.
- **Desafío:** recuperar solicitudes interrumpidas sin producir admisiones duplicadas.

**07. Clientes**
- **Negocio:** relación comercial, segmentación, revisiones periódicas y desvinculación.
- **Aprendizaje técnico:** ciclos de vida, transiciones, tareas programadas, versionado, consistencia eventual, coordinación de bajas y validaciones de dominio.
- **Desafío:** impedir desvinculaciones definitivas con obligaciones pendientes.

**08. Representación**
- **Negocio:** poderes, representación legal, autorizaciones conjuntas, revocaciones y vigencias.
- **Aprendizaje técnico:** ABAC, autorización contextual, relaciones complejas, reglas temporales, concurrencia entre revocación y ejecución y auditoría.
- **Desafío:** autorizar correctamente actuaciones conjuntas y rechazar facultades vencidas.

### Módulo 3 — Contratación

**09. Solicitudes de productos**
- **Negocio:** contratación, requisitos, evaluaciones, aprobaciones y subsanaciones.
- **Aprendizaje técnico:** orquestación distribuida, máquinas de estados, integración HTTP y eventos, correlación, idempotencia, persistencia de procesos, reintentos y recuperación.
- **Desafío:** coordinar evaluaciones independientes y recuperar decisiones inciertas.

**10. Contratos**
- **Negocio:** acuerdos, condiciones pactadas, consentimientos, vigencias y modificaciones.
- **Aprendizaje técnico:** versionado inmutable, integridad de contenido, gestión de consentimientos, firmas simuladas, seguridad documental, almacenamiento de evidencias y auditoría.
- **Desafío:** garantizar que una aceptación corresponde a la versión exacta de un contrato.

### Módulo 4 — Cuentas y fondos

**11. Cuentas**
- **Negocio:** apertura, titularidades, autorizados, condiciones, restricciones y cierre.
- **Aprendizaje técnico:** agregados de dominio, máquinas de estados, autorización por titularidad, integración, coordinación de cierres, concurrencia y persistencia histórica.
- **Desafío:** cerrar cuentas sin perder el tratamiento de operaciones y obligaciones pendientes.

**12. Identificación bancaria**
- **Negocio:** CBU simulados, alias, asignaciones, cambios y consultas.
- **Aprendizaje técnico:** identificadores únicos, índices PostgreSQL, restricciones, transacciones, concurrencia, integración externa, consultas y caché justificada.
- **Desafío:** impedir asignaciones simultáneas del mismo alias.

**13. Fondos**
- **Negocio:** saldos, débitos, créditos, reservas, inmovilizaciones y descubiertos.
- **Aprendizaje técnico:** transacciones ACID, aislamiento, bloqueos, concurrencia, atomicidad, BigDecimal, idempotencia, restricciones SQL, Outbox, integridad monetaria y recuperación.
- **Desafío:** impedir doble utilización de fondos y ejecutar movimientos internos atómicos.

**14. Operaciones de efectivo**
- **Negocio:** depósitos, extracciones, entregas físicas simuladas y conciliación.
- **Aprendizaje técnico:** coordinación distribuida, transacciones, reservas, máquinas de estados, efectos parciales, idempotencia, timeouts y recuperación.
- **Desafío:** recuperar una entrega de efectivo cuyo resultado se desconoce.

### Módulo 5 — Pagos y transferencias

**15. Transferencias**
- **Negocio:** transferencias internas y externas, controles, destinos, devoluciones y seguimiento.
- **Aprendizaje técnico:** orquestación, integración HTTP, contratos, idempotencia, consistencia distribuida, Outbox, Inbox, reintentos, resultados inciertos, trazabilidad y recuperación.
- **Desafío:** coordinar transferencias sin administrar directamente los saldos.

**16. Pagos**
- **Negocio:** obligaciones, beneficiarios, recaudadores, pagos parciales y devoluciones.
- **Aprendizaje técnico:** integración externa, transacciones, idempotencia, conciliación, máquinas de estados, reintentos y recuperación.
- **Desafío:** distinguir un débito confirmado de una obligación efectivamente cancelada.

**17. Mandatos y recurrencias**
- **Negocio:** débitos autorizados, pagos programados, periodicidad y revocaciones.
- **Aprendizaje técnico:** Spring Scheduling, tareas persistentes, concurrencia entre workers, calendarios, recurrencias, idempotencia y recuperación de jobs.
- **Desafío:** impedir ejecuciones duplicadas o posteriores a una revocación.

**18. Cheques y ECHEQ**
- **Negocio:** emisión, endosos, avales, presentaciones, rechazos y devoluciones.
- **Aprendizaje técnico:** máquinas de estados, cadenas de legitimación, autorizaciones, reglas temporales, integración, idempotencia y trazabilidad documental.
- **Desafío:** impedir doble pago y validar correctamente las transmisiones de un cheque.

**19. Compensación y conciliación de pagos**
- **Negocio:** intercambio interbancario, conciliación, compensación y obligaciones de liquidación.
- **Aprendizaje técnico:** Kafka, eventos, Outbox, Inbox, deduplicación, consistencia eventual, procesamiento por lotes, correlación y recuperación.
- **Desafío:** procesar mensajes duplicados, faltantes o fuera de orden y resolver diferencias.

### Módulo 6 — Financiación

**20. Evaluación crediticia**
- **Negocio:** capacidad de pago, endeudamiento, garantías, políticas y aprobaciones.
- **Aprendizaje técnico:** motores de reglas, cálculos financieros, integración de fuentes, procesamiento paralelo cuando corresponda, versionado de decisiones y control concurrente de límites.
- **Desafío:** evitar que aprobaciones simultáneas superen límites de exposición.

**21. Préstamos y líneas**
- **Negocio:** créditos, amortización, intereses, cuotas, mora, desembolsos y refinanciaciones.
- **Aprendizaje técnico:** BigDecimal, matemática financiera, cronogramas, transacciones, tareas programadas, imputación de pagos, idempotencia y recuperación.
- **Desafío:** calcular y mantener correctamente la deuda ante pagos parciales y modificaciones.

**22. Garantías**
- **Negocio:** avales, bienes, coberturas, prioridades, valuaciones y liberaciones.
- **Aprendizaje técnico:** relaciones complejas, validaciones de cobertura, concurrencia, transacciones, versionado, persistencia de afectaciones y auditoría.
- **Desafío:** impedir comprometer una garantía por encima de su cobertura disponible.

**23. Cobranzas y recuperaciones**
- **Negocio:** deudas vencidas, acuerdos, compromisos, refinanciaciones y recuperaciones.
- **Aprendizaje técnico:** workflows, estados, tareas programadas, prioridades, integración, autorización y recuperación de procesos.
- **Desafío:** distinguir compromisos de pago de recuperaciones efectivamente confirmadas.

### Módulo 7 — Tarjetas

**24. Emisión de tarjetas**
- **Negocio:** emisión, activación, renovación, bloqueo, reemplazo y tarjetas adicionales.
- **Aprendizaje técnico:** máquinas de estados, protección de datos sensibles, autorización por canal, relaciones, versionado, procesamiento diferido y trazabilidad.
- **Desafío:** reemplazar instrumentos sin perder operaciones y obligaciones anteriores.

**25. Autorizaciones**
- **Negocio:** autorización de consumos, límites, reservas y compromisos crediticios.
- **Aprendizaje técnico:** baja latencia, concurrencia, idempotencia, coordinación de reservas, timeouts, consistencia de decisiones, pruebas de carga y recuperación.
- **Desafío:** impedir autorizaciones simultáneas por encima de la capacidad disponible.

**26. Procesamiento de consumos**
- **Negocio:** presentaciones, compras, consumos parciales, devoluciones y conciliación.
- **Aprendizaje técnico:** mensajería, procesamiento asíncrono, deduplicación, eventos tardíos, correlación, consistencia eventual, conciliación y recuperación.
- **Desafío:** procesar presentaciones parciales sin generar cargos duplicados.

**27. Cuenta de tarjeta**
- **Negocio:** límites, consumos, cuotas, resúmenes, intereses, pago mínimo y financiación.
- **Aprendizaje técnico:** BigDecimal, transacciones, concurrencia, cronogramas, tareas de cierre, imputación de pagos, cálculos financieros y persistencia histórica.
- **Desafío:** generar resúmenes e intereses sin duplicar deuda ni compromisos.

**28. Disputas de tarjetas**
- **Negocio:** desconocimientos, contracargos, investigaciones y créditos provisionales.
- **Aprendizaje técnico:** workflows, máquinas de estados, gestión de evidencias, integración, plazos, compensaciones de negocio, idempotencia y auditoría.
- **Desafío:** resolver disputas y créditos provisionales sin duplicar devoluciones.

### Módulo 8 — Ahorro e inversiones

**29. Depósitos a plazo**
- **Negocio:** plazos fijos, intereses, vencimientos, renovaciones y precancelaciones.
- **Aprendizaje técnico:** BigDecimal, matemática financiera, calendarios, tareas programadas, transacciones, coordinación de fondos, versionado y recuperación.
- **Desafío:** ejecutar renovaciones y vencimientos sin duplicar depósitos ni pagos.

**30. Instrumentos de inversión**
- **Negocio:** instrumentos financieros, emisores, negociación, eventos y condiciones operativas.
- **Aprendizaje técnico:** catálogos, modelado financiero, versionado, reglas configurables, precisión, consultas temporales e integración.
- **Desafío:** modificar instrumentos sin alterar derechos y tenencias existentes.

**31. Órdenes de inversión**
- **Negocio:** compras, ventas, suscripciones, rescates, ejecuciones parciales y liquidaciones.
- **Aprendizaje técnico:** orquestación, eventos, máquinas de estados, idempotencia, control concurrente de recursos, reintentos, conciliación y recuperación.
- **Desafío:** procesar ejecuciones parciales y cancelaciones concurrentes.

**32. Tenencias y valuación**
- **Negocio:** posiciones, cantidades disponibles, precios, valuaciones y rendimientos.
- **Aprendizaje técnico:** concurrencia, reservas de cantidades, transacciones, BigDecimal, cálculos, procesamiento de eventos, consistencia temporal y conciliación.
- **Desafío:** impedir vender dos veces las mismas unidades de inversión.

### Módulo 9 — Contabilidad

**33. Registración contable**
- **Negocio:** partida doble, asientos, débitos, créditos, ajustes y hechos económicos.
- **Aprendizaje técnico:** ACID, integridad financiera, BigDecimal, eventos, Inbox, idempotencia, persistencia inmutable, auditoría y correcciones.
- **Desafío:** garantizar asientos equilibrados y evitar contabilizaciones duplicadas.

**34. Cierres contables**
- **Negocio:** cierres diarios, mensuales y anuales, conciliaciones, ajustes y reaperturas.
- **Aprendizaje técnico:** procesamiento batch, Spring Batch cuando se justifique, scheduling, transacciones, cortes consistentes, coordinación, idempotencia y recuperación.
- **Desafío:** reanudar cierres fallidos sin repetir ajustes.

**35. Información contable**
- **Negocio:** balances, mayores, estados financieros e informes históricos.
- **Aprendizaje técnico:** SQL avanzado, agregaciones, índices, optimización de consultas, proyecciones, paginación, reportes y versionado.
- **Desafío:** generar informes reproducibles y trazables hasta los registros originales.

### Módulo 10 — Tesorería

**36. Posiciones y liquidez**
- **Negocio:** recursos propios, disponibilidad, compromisos, proyecciones y necesidades de liquidez.
- **Aprendizaje técnico:** cálculos financieros, agregaciones, transacciones, control concurrente de recursos, proyecciones temporales, integración y reconciliación.
- **Desafío:** calcular disponibilidad sin duplicar compromisos.

**37. Operaciones de tesorería**
- **Negocio:** inversiones propias, financiación, contrapartes, tasas, vencimientos y rendimientos.
- **Aprendizaje técnico:** matemática financiera, BigDecimal, contratos versionados, estados, integración, tareas programadas y recuperación.
- **Desafío:** distinguir operaciones acordadas, liquidadas y pendientes.

**38. Cambio de moneda**
- **Negocio:** divisas, cotizaciones, conversión, compra y venta de monedas.
- **Aprendizaje técnico:** BigDecimal, redondeos, cotizaciones con vencimiento, transacciones, idempotencia, coordinación de dos efectos monetarios y recuperación distribuida.
- **Desafío:** garantizar consistencia cuando una conversión presenta una ejecución parcial o incierta.

**39. Liquidaciones de tesorería**
- **Negocio:** obligaciones externas, entregas, recepciones, liquidaciones parciales y conciliaciones.
- **Aprendizaje técnico:** orquestación, mensajería, estados, idempotencia, reintentos, correlación, transacciones locales, recuperación y conciliación.
- **Desafío:** recuperar liquidaciones inciertas sin repetir efectos económicos.

### Módulo 11 — Riesgo y cumplimiento

**40. Riesgos**
- **Negocio:** exposición crediticia, límites, concentraciones, riesgo financiero y políticas.
- **Aprendizaje técnico:** motores de reglas, agregaciones, cálculos, concurrencia, transacciones, bloqueos, idempotencia, versionado y simulación de escenarios.
- **Desafío:** evitar superar límites compartidos bajo concurrencia.

**41. Conocimiento del cliente**
- **Negocio:** KYC, identidad, documentos, beneficiarios finales y origen de fondos.
- **Aprendizaje técnico:** integración externa, validaciones documentales, relaciones societarias, workflows, revisiones, seguridad, auditoría y procedencia de datos.
- **Desafío:** distinguir datos declarados, verificados y pendientes de comprobación.

**42. Prevención de lavado**
- **Negocio:** monitoreo, patrones inusuales, alertas, investigaciones y reportes especializados.
- **Aprendizaje técnico:** procesamiento de eventos, reglas, agregaciones temporales, correlación, consultas históricas, deduplicación, workflows y auditoría.
- **Desafío:** identificar operaciones relacionadas y evitar investigaciones duplicadas.

**43. Prevención de fraude**
- **Negocio:** operaciones sospechosas, indicadores, evaluaciones, decisiones y medidas preventivas.
- **Aprendizaje técnico:** reglas de detección, eventos, procesamiento de baja latencia, análisis temporal, consultas, timeouts, resiliencia, métricas y pruebas de carga.
- **Desafío:** evaluar operaciones concurrentes y resolver decisiones cuando un control no responde.

**44. Restricciones**
- **Negocio:** embargos simulados, inmovilizaciones, prioridades, vigencias, levantamientos y cumplimiento.
- **Aprendizaje técnico:** autorización contextual, consistencia temporal, concurrencia, coordinación distribuida, preparación y confirmación, versionado, idempotencia y recuperación.
- **Desafío:** evitar operaciones prohibidas durante modificaciones concurrentes de restricciones.

**45. Criterios tributarios**
- **Negocio:** tributos, retenciones, percepciones, exenciones, alícuotas y topes.
- **Aprendizaje técnico:** motores de reglas, BigDecimal, redondeos, versionado, cálculos condicionales, acumuladores, transacciones, concurrencia e idempotencia.
- **Desafío:** aplicar topes compartidos sin duplicaciones y regularizar cálculos anteriores.

**46. Información regulatoria**
- **Negocio:** reportes regulatorios simulados, declaraciones, validaciones, aprobaciones y rectificaciones.
- **Aprendizaje técnico:** procesamiento batch, integración de fuentes, generación de archivos, validación de esquemas, workflows, seguridad, versionado y recuperación de envíos.
- **Desafío:** presentar información consistente y recuperar confirmaciones perdidas.

### Módulo 12 — Atención y operación administrativa

**47. Reclamos**
- **Negocio:** reclamos, investigaciones, resoluciones, devoluciones y reaperturas.
- **Aprendizaje técnico:** workflows, máquinas de estados, tareas programadas, SLA, integración, autorización, auditoría y recuperación.
- **Desafío:** impedir cierres de reclamos con reparaciones pendientes.

**48. Gestión administrativa**
- **Negocio:** expedientes, tareas humanas, responsables, aprobaciones y separación de funciones.
- **Aprendizaje técnico:** motores de workflow, autorización por roles y facultades, concurrencia, scheduling, estados persistentes, auditoría y recuperación.
- **Desafío:** implementar aprobaciones con separación de funciones y recuperar tareas interrumpidas.

**49. Documentos y comprobantes**
- **Negocio:** documentos, comprobantes, evidencias, políticas de conservación, integridad y eliminación.
- **Aprendizaje técnico:** MinIO, almacenamiento de objetos, metadatos, hashes, versionado, seguridad, permisos, coordinación de persistencia, recuperación y auditoría.
- **Desafío:** mantener consistencia entre archivos y base de datos e impedir eliminaciones indebidas.

**50. Comunicaciones**
- **Negocio:** notificaciones, avisos, plantillas, preferencias, envíos y constancias de entrega.
- **Aprendizaje técnico:** mensajería asíncrona, colas, Mailpit, reintentos, backoff, idempotencia, procesamiento de trabajos, errores y observabilidad.
- **Desafío:** procesar envíos sin duplicarlos y distinguir recepción técnica de entrega efectiva.

**51. Extractos**
- **Negocio:** movimientos, saldos, períodos, cortes, extractos y rectificaciones.
- **Aprendizaje técnico:** SQL avanzado, agregaciones, procesamiento batch, paginación, consultas consistentes, generación documental, versionado y optimización.
- **Desafío:** generar extractos íntegros y rectificarlos conservando las versiones anteriores.

---

# 2. Mapa de conocimientos

Este mapa permite buscar microservicios por área de conocimiento, en lugar de buscarlos únicamente por módulo bancario.

Los números corresponden al catálogo anterior.

**Principales:** microservicios en los que el conocimiento representa una parte importante de los problemas que deberás resolver.

**Complementarios:** microservicios donde también podrás practicarlo, aunque no sea necesariamente su desafío central.

La clasificación es una guía de aprendizaje, no una modificación de los requisitos documentados.

## A. Arquitectura y diseño de software

| Conocimiento | Microservicios principales | Complementarios |
|---|---|---|
| Domain-Driven Design y reglas de negocio | 02, 11, 13, 21, 27, 33 | 01, 05, 20, 40 |
| Arquitectura hexagonal y separación de responsabilidades | 09, 13, 15, 21, 31 | 06, 16, 25, 39 |
| Diseño de agregados y entidades | 05, 11, 13, 21, 27 | 10, 22, 32 |
| Máquinas de estados | 06, 09, 11, 18, 28 | 07, 15, 24, 31, 47 |
| Reglas configurables de negocio | 02, 03, 20, 40, 45 | 08, 41, 43 |
| Versionado y evolución de modelos | 02, 03, 04, 10, 30 | 01, 07, 24, 45 |

## B. Bases de datos y persistencia

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Modelado relacional | 05, 11, 13, 21, 27 | 01, 08, 22, 32 |
| Restricciones, claves únicas e índices | 05, 12, 13, 22 | 15, 17, 25 |
| Transacciones ACID | 13, 21, 27, 33 | 12, 25, 32, 38 |
| Aislamiento y bloqueos SQL | 13, 25, 32, 40 | 12, 22, 36, 45 |
| Persistencia histórica e inmutabilidad | 10, 13, 33, 49 | 01, 02, 18, 30 |
| Consultas SQL avanzadas | 35, 36, 42, 51 | 32, 40, 43 |
| Optimización de consultas e índices | 35, 42, 51 | 12, 36, 43 |
| Procesamiento de grandes conjuntos de datos | 19, 34, 35, 46, 51 | 26, 42 |

## C. Concurrencia y paralelismo

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Condiciones de carrera | 13, 25, 32, 40 | 12, 22, 27, 45 |
| Bloqueos pesimistas y optimistas | 13, 25, 40 | 12, 22, 32 |
| Integridad bajo concurrencia | 13, 25, 27, 32, 40 | 17, 36, 44 |
| Ejecución concurrente de trabajos | 17, 34, 48 | 23, 46, 50 |
| Coordinación de recursos compartidos | 13, 22, 32, 40, 44 | 25, 36, 45 |
| Paralelismo de consultas independientes | 20, 36, 42 | 09, 35, 46 |
| Pruebas de concurrencia y carga | 13, 25, 40, 43 | 17, 27, 32, 50 |

## D. Microservicios y sistemas distribuidos

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Comunicación HTTP entre servicios | 09, 15, 16, 20 | 06, 11, 25 |
| Contratos entre servicios | 09, 15, 19, 31 | 06, 16, 39 |
| Orquestación distribuida | 09, 15, 31, 39 | 06, 14, 38 |
| Coordinación de operaciones monetarias | 15, 31, 38, 39 | 14, 16, 25 |
| Consistencia eventual | 19, 26, 31, 39 | 09, 15, 33 |
| Idempotencia distribuida | 15, 19, 26, 39 | 09, 16, 25, 50 |
| Recuperación de operaciones inciertas | 14, 15, 19, 31, 39 | 06, 09, 38 |
| Simulación de servicios externos | 15, 16, 19, 31, 39 | 06, 14, 46 |

## E. Mensajería y asincronía

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Apache Kafka y eventos de negocio | 19, 26, 31, 33 | 15, 42, 50 |
| Patrón Transactional Outbox | 13, 15, 19, 33 | 09, 26, 39 |
| Patrón Inbox | 19, 26, 33, 39 | 15, 31 |
| Mensajes duplicados | 19, 26, 33, 50 | 15, 31, 39 |
| Eventos tardíos o fuera de orden | 19, 26, 31 | 33, 39, 42 |
| Procesamiento asíncrono | 19, 26, 31, 50 | 06, 09, 42 |
| Correlación y trazabilidad de eventos | 15, 19, 26, 39 | 09, 31, 50 |

## F. Resiliencia y recuperación

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Timeouts y fallas de dependencias | 09, 15, 19, 25, 39 | 06, 14, 43 |
| Reintentos seguros | 15, 19, 39, 50 | 06, 09, 16, 31 |
| Idempotencia y deduplicación | 13, 15, 19, 26, 33 | 16, 17, 25, 50 |
| Recuperación de procesos persistentes | 06, 09, 15, 17, 34 | 23, 31, 39, 48 |
| Resultados inciertos | 14, 15, 19, 38, 39 | 16, 21, 31 |
| Compensaciones de negocio | 15, 28, 31, 38 | 09, 16, 39 |
| Pruebas de fallos e interrupciones | 13, 15, 19, 25, 39 | 06, 09, 34, 49 |

## G. Seguridad

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Autenticación y autorización | 08, 11, 24, 25 | 01, 10, 49 |
| Autorización contextual y ABAC | 01, 08, 11, 44 | 20, 25, 48 |
| Roles y separación de funciones | 01, 48 | 09, 10, 46 |
| Titularidad y representación | 08, 11 | 06, 15, 41 |
| Protección de datos sensibles | 05, 24, 41, 49 | 10, 42, 46 |
| Seguridad documental | 10, 41, 49 | 18, 46 |
| Auditoría de decisiones | 01, 08, 20, 44, 48 | 09, 10, 40 |

## H. Caché y rendimiento

| Conocimiento | Principales oportunidades | Otras oportunidades |
|---|---|---|
| Caché de información consultable | 02, 04, 12, 30 | 35, 43 |
| Invalidación y versionado de caché | 02, 03, 12 | 04, 30 |
| Optimización de consultas | 35, 36, 51 | 12, 40, 42 |
| Procesamiento de baja latencia | 25, 43 | 12, 15 |
| Procesamiento batch | 19, 34, 46, 51 | 26, 35 |
| Pruebas de rendimiento y carga | 13, 25, 43, 50 | 19, 35, 51 |

**Importante:** Redis es una herramienta posible para practicar caché, no un requisito automático de estos servicios. Su uso debe estar justificado por los datos, la frecuencia de consulta y la política de consistencia.

## I. Scheduling y procesamiento temporal

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Spring Scheduling y tareas programadas | 17, 21, 29, 34 | 07, 23, 48 |
| Tareas persistentes y recuperación | 17, 34, 48 | 23, 46, 50 |
| Cálculo de vencimientos | 04, 17, 21, 29 | 18, 27, 37 |
| Concurrencia entre ejecutores | 17, 34, 48 | 21, 50 |
| Calendarios y reglas temporales | 04, 17, 29 | 18, 21, 27 |
| Cierres periódicos | 27, 34, 51 | 21, 29, 46 |

## J. Matemática financiera

| Conocimiento | Principales | Complementarios |
|---|---|---|
| BigDecimal y exactitud monetaria | 03, 13, 21, 27, 33, 38 | 29, 32, 45 |
| Intereses y amortizaciones | 21, 27, 29 | 03, 37 |
| Tasas y convenciones de cálculo | 03, 21, 29, 37 | 27, 38 |
| Monedas y conversiones | 38 | 13, 32, 33, 36 |
| Saldos y reservas | 13, 25, 27 | 14, 32, 36 |
| Valuaciones financieras | 22, 32, 37 | 29, 36 |
| Tributos y redondeos | 45 | 03, 21, 27, 38 |

## K. Procesos empresariales

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Workflows y procesos persistentes | 06, 09, 23, 47, 48 | 28, 41, 46 |
| Máquinas de estados complejas | 09, 15, 18, 28, 31 | 06, 11, 24 |
| Aprobaciones y autorizaciones | 01, 08, 20, 48 | 06, 09, 10 |
| Gestión de expedientes | 47, 48 | 23, 28, 42 |
| Procesos de contratación | 09, 10 | 06, 11 |
| Investigaciones empresariales | 23, 28, 42, 43, 47 | 41, 44 |
| Gestión de vencimientos y SLA | 17, 23, 47, 48 | 06, 09, 46 |

## L. Información, reportes y documentos

| Conocimiento | Principales | Complementarios |
|---|---|---|
| Generación de reportes | 35, 46, 51 | 36, 40 |
| Agregación de información | 35, 36, 40, 51 | 42, 46 |
| Consistencia de cortes de datos | 34, 35, 46, 51 | 36 |
| Generación documental | 49, 51 | 10, 46 |
| Almacenamiento de objetos | 49 | 10, 41 |
| Versionado de documentos | 10, 49, 51 | 18, 46 |
| Integridad y evidencias | 10, 33, 49 | 28, 41, 46 |
| Conciliación de información | 19, 26, 34, 39 | 32, 35, 51 |

---

# 3. Mapa de conocimientos empresariales

El siguiente mapa permite elegir laboratorios según los conceptos del negocio bancario que se desea aprender.

| Área empresarial | Microservicios |
|---|---|
| Organización y facultades bancarias | 01, 08, 48 |
| Productos y condiciones comerciales | 02, 03, 09, 10 |
| Personas, clientes y relaciones jurídicas | 05, 06, 07, 08, 41 |
| Contratación bancaria | 09, 10, 11, 20 |
| Cuentas y disponibilidad monetaria | 11, 12, 13, 14 |
| Transferencias y pagos | 15, 16, 17, 18, 19 |
| Financiamiento y deuda | 20, 21, 22, 23 |
| Operaciones con tarjetas | 24, 25, 26, 27, 28 |
| Ahorro e inversiones | 29, 30, 31, 32 |
| Contabilidad bancaria | 33, 34, 35 |
| Tesorería y liquidez | 36, 37, 38, 39 |
| Gestión de riesgos | 20, 40, 43, 44 |
| Cumplimiento y prevención | 41, 42, 43, 44, 46 |
| Tributación bancaria | 03, 21, 27, 45 |
| Atención y administración | 23, 47, 48, 50 |
| Documentos e información bancaria | 10, 35, 46, 49, 51 |

---

# 4. Mapa de selección según debilidades técnicas

Esta tabla permite identificar rápidamente dónde concentrar el estudio.

| Quiero fortalecer... | Laboratorios especialmente relevantes |
|---|---|
| Modelado de dominio y reglas empresariales | 02, 11, 13, 21 |
| SQL y persistencia | 05, 12, 13, 35 |
| Transacciones ACID | 13, 21, 27, 33 |
| Concurrencia y bloqueos | 13, 25, 32, 40 |
| Paralelismo y consultas independientes | 20, 36, 42 |
| Comunicación entre microservicios | 09, 15, 19, 39 |
| Sistemas distribuidos | 15, 19, 31, 39 |
| Kafka y procesamiento de eventos | 19, 26, 33 |
| Outbox e Inbox | 13, 19, 26, 33 |
| Idempotencia | 13, 15, 19, 25 |
| Resiliencia y recuperación | 09, 15, 19, 39 |
| Seguridad y autorización | 08, 11, 25, 44 |
| Roles y permisos empresariales | 01, 08, 48 |
| Redis y estrategias de caché | 02, 04, 12, 30 |
| Scheduling y tareas persistentes | 17, 29, 34, 48 |
| Workflows y máquinas de estados | 06, 09, 28, 48 |
| Cálculos financieros | 21, 27, 29, 38 |
| Procesamiento batch | 19, 34, 46, 51 |
| SQL avanzado y rendimiento | 35, 36, 42, 51 |
| Procesamiento de baja latencia | 25, 43 |
| Documentos, archivos y MinIO | 10, 49 |
| Integraciones externas | 15, 16, 19, 39 |
| Conciliación de información | 19, 26, 34, 39 |
| Observabilidad distribuida | 15, 19, 25, 39 |
| Pruebas de carga y concurrencia | 13, 25, 40, 43 |
| Trazabilidad y auditoría | 10, 33, 44, 49 |

---

