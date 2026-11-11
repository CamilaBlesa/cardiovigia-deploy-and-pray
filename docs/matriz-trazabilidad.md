# Matriz de trazabilidad de pruebas

## Convenciones

- **RF**: requisito funcional.
- **RF-xx Caso de prueba: xx**: identificación del caso dentro del requisito.
- **Implementada**: existe un artefacto ejecutable en el repositorio.
- **Pendiente**: no hay evidencia de ejecución o el criterio todavía no fue medido.
- **DEF**: defecto funcional registrado.
- **MUT**: mutante sobreviviente informado por PIT.

La matriz distingue entre la existencia de una prueba y la evidencia de su
ejecución. No se asigna un resultado de aprobación a pruebas no ejecutadas.

## Requisitos funcionales

| Requisito | Caso de prueba | Propósito | Artefacto | Ejecución/evidencia | Defecto o hallazgo |
|---|---|---|---|---|---|
| RF-01 | RF-01 Caso de prueba: 01-03 | Validar frecuencia cardíaca y presión arterial media, incluidos límites | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber | Implementada; suite JUnit/MockMvc y 97 escenarios Cucumber | Sin defecto funcional registrado |
| RF-02 | RF-02 Caso de prueba: 01-02 | Validar temperatura y SpO2 | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber | Implementada; suite JUnit/MockMvc y 97 escenarios Cucumber | Sin defecto funcional registrado |
| RF-03 | RF-03 Caso de prueba: 01 | Validar calidad y recuperación del canal | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Selenium | Implementada; pruebas automatizadas ejecutadas | Sin defecto funcional registrado |
| RF-04 | RF-04 Caso de prueba: 01-04 | Registrar/consultar paciente y validar roles/fases | `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; MockMvc, Cucumber y colección Postman | Sin defecto funcional registrado |
| RF-05 | RF-05 Caso de prueba: 01-02 | Evaluar elegibilidad y exclusión | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; pruebas automatizadas y API | Sin defecto funcional registrado |
| RF-06 | RF-06 Caso de prueba: 01-03 | Clasificar niveles de riesgo y rechazar probabilidades inválidas | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Selenium, Postman | Implementada; pruebas automatizadas y API | MUT-001: límite condicional en `clasificarNivelRiesgo` |
| RF-07 | RF-07 Caso de prueba: 01-02 | Determinar notificaciones y supresión de alarmas | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Selenium, Postman | Implementada; pruebas automatizadas y API | Sin defecto funcional registrado |
| RF-08 | RF-08 Caso de prueba: 01 | Evaluar escalamiento crítico y alto | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; pruebas automatizadas y API | Sin defecto funcional registrado |
| RF-09 | RF-09 Caso de prueba: 01 | Activar modo degradado por señal o falla de IA | `CardioVigiaApplicationTests`, `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; pruebas automatizadas y API | Sin defecto funcional registrado |
| RF-10 | RF-10 Caso de prueba: 01 | Enviar resumen y consultar estado HCE | `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; MockMvc, Cucumber y API | Sin defecto funcional registrado |
| RF-11 | RF-11 Caso de prueba: 01-03 | Aceptar, rechazar o bloquear umbrales | `CardioVigiaControllerTest`, Cucumber, Postman | Implementada; MockMvc, Cucumber y API | MUT-002 a MUT-004: límites condicionales en `actualizarUmbrales` |

## Requisitos no funcionales y criterios de salida

| Requisito | Caso de prueba | Criterio | Artefacto previsto | Estado de evidencia | Defecto/hallazgo |
|---|---|---|---|---|---|
| Eficiencia | RNF-EF Caso de prueba: 01 | P95 < 2 s y máximo < 5 s | `resultados/pacientesSimultaneos.csv` | Cumplida en la corrida registrada: P95 3 ms y máximo 27 ms | Medición puntual; no demuestra disponibilidad mensual |
| Carga | RNF-CG Caso de prueba: 01 | 24 pacientes simultáneos | `resultados/pacientesSimultaneos.csv` | Cumplida en la corrida registrada: 24 usuarios, 4 iteraciones, 96 requests y 0% de errores | No es una prueba de resistencia prolongada |
| Recuperación | RNF-RE Caso de prueba: 01 | Recuperación < 30 s | Terminal: reinicio, health check y respuesta HTTP | Cumplida: 9 s hasta el primer `200 OK` | Medición realizada desde la terminal del proyecto |
| Pairwise | RNF-PW Caso de prueba: 01-10 | Ejecutar los 10 casos generados | `docs/pairwise/` y resultados de ejecución | Casos diseñados | |

## Cobertura y mutación

| Métrica | Resultado disponible | Objetivo | Observación |
|---|---:|---:|---|
| JaCoCo líneas | 98,3% | ≥ 80% | Cumplido según el reporte generado |
| JaCoCo ramas/decisiones | 89,4% | ≥ 75% | Cumplido según el reporte generado |
| Complejidad total | 88,8% | Referencia para caminos | JaCoCo no mide caminos directamente; se usa como aproximación |
| Complejidad del controller | 74,3% | Referencia para caminos | Debe informarse como aproximación, no como cobertura de caminos |
| PIT mutation score | 96% | ≥ 90% | 4 mutantes sobrevivientes documentados |

## Registro de defectos y mutantes

| ID | Requisito | Ubicación | Descripción | Estado |
|---|---|---|---|---|
| MUT-001 | RF-06 | `CardioVigia.java`, `clasificarNivelRiesgo` | Mutante de límite condicional | Sobreviviente; conservar para demostración |
| MUT-002 | RF-11 | `CardioVigiaController.java`, `actualizarUmbrales` | Mutante de límite inferior de `umbralAlto` | Sobreviviente; conservar para demostración |
| MUT-003 | RF-11 | `CardioVigiaController.java`, `actualizarUmbrales` | Mutante de límite superior de `umbralAlto` | Sobreviviente; conservar para demostración |
| MUT-004 | RF-11 | `CardioVigiaController.java`, `actualizarUmbrales` | Mutante de límite inferior de `umbralCritico` | Sobreviviente; conservar para demostración |

No se registran defectos funcionales nuevos sin una evidencia reproducible.
