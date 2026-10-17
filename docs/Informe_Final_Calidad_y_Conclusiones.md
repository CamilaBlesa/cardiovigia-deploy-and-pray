# Informe Final de Calidad y Dictamen de Liberación
## Software: CardioVigía 2.4.0 — Instituto Cardiovascular del Plata
**Equipo:** Deploy & Pray  
**Integrantes:** Camila Blesa, Ian Gucevich, Samuel Trejo  
**Materia:** Verificación y Validación de Software — Licenciatura en Desarrollo de Software (ECyT - UNSAM)  
**Fecha:** Octubre / Noviembre 2026  

---

### 1. Resumen Ejecutivo
El equipo de QA ha completado la evaluación integral de verificación y validación sobre la versión **2.4.0** del sistema **CardioVigía**. Se ejecutaron **35 casos de prueba de caja negra**, **24 pruebas automatizadas con JUnit 5**, **5 escenarios BDD con Cucumber** y una suite de pruebas de API mediante **Postman**.  
La lógica crítica de negocio alcanzó una cobertura de ramas del **91%** y una cobertura de instrucciones del **98%** en los servicios centrales de decisión médica (`ar.edu.unsam.cardiovigia.service`). Todos los defectos críticos identificados durante el proceso de pruebas fueron resueltos y verificados satisfactoriamente.

---

### 2. Resumen de Métricas y KPIs de Calidad Alcanzados

| Métrica / Indicador (KPI) | Meta Definida | Resultado Obtenido | Estado |
| :--- | :--- | :--- | :--- |
| **Tasa de Éxito de Pruebas Automatizadas** | 100% | **100% (24 de 24 tests aprobados)** | ✅ Cumplido |
| **Cobertura de Ramas (JaCoCo Branch Coverage)** | &ge; 85% | **91% en servicios de negocio** | ✅ Cumplido |
| **Cobertura de Instrucciones (JaCoCo Line/Inst)** | &ge; 80% | **98% en servicios de negocio** | ✅ Cumplido |
| **Escenarios BDD Aprobados (Cucumber)** | 100% | **5 de 5 escenarios aprobados** | ✅ Cumplido |
| **Defectos Críticos Abiertos (Bloqueantes)** | 0 | **0 defectos pendientes (100% resueltos)** | ✅ Cumplido |
| **Tiempo de Respuesta Promedio de API (Postman)** | &le; 100 ms | **18 ms promedio** | ✅ Cumplido |

---

### 3. Evaluación de las Preocupaciones de los Actores Clínicos

1. **Anestesiología (Falsas alarmas al entrar y salir de CEC):**
   * *Hallazgo:* La supresión de alertas idénticas por 120 s (RF-07) reduce drásticamente la fatiga de alarmas, mientras que la regla de agravamiento garantiza que si el paciente se descompensa a nivel Crítico, la alarma suena de inmediato.
2. **Cirugía y Perfusión (Ruido de electrobisturí y desincronización):**
   * *Hallazgo:* El validador descarta automáticamente mediciones espurias (frecuencias mayores a 300 lpm o presiones fuera de escala fisiológica), evitando que la IA infiera sobre datos corruptos.
3. **Comité de Ética y Calidad (Sesgo demográfico del modelo):**
   * *Hallazgo:* Se verificó que el sistema bloquea la estimación con IA en menores de 18 y mayores de 90 años manteniendo el monitoreo básico (RF-05). Sin embargo, el dataset de entrenamiento original presenta una subrepresentación de mujeres y pacientes con marcapasos.

---

### 4. Dictamen Final del Equipo de QA

Tras evaluar los resultados de las pruebas funcionales, no funcionales y los riesgos residuales, este equipo de QA emite de manera unánime el siguiente veredicto:

### ⚠️ RECOMENDACIÓN: **LIBERAR CON CONDICIONES**

El núcleo de validación, clasificación de riesgo, escalamiento y modo degradado es **robusto, confiable y verificable**. No obstante, para autorizar el despliegue pleno en los 4 quirófanos y en la UCI-CV del Instituto Cardiovascular del Plata, deben cumplirse las siguientes **tres condiciones operativas de seguridad**:

1. **Condición 1 (Calibración del Modelo):** Ampliar la validación clínica del modelo de IA con una muestra representativa de pacientes mujeres y pacientes con marcapasos antes de habilitar la estimación de sangrado en cirugías abiertas de alto riesgo.
2. **Condición 2 (Infraestructura de Red):** Garantizar sincronización horaria mediante servidor NTP centralizado en todos los monitores del quirófano para prevenir lecturas fuera de secuencia temporal.
3. **Condición 3 (Capacitación y Simulacro):** Capacitar al personal de enfermería de UCI-CV en el reconocimiento en pantalla táctil con una sola mano y realizar un simulacro de escalamiento a los 30 segundos con el Coordinador de UCI.
