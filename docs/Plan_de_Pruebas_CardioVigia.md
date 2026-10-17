# Plan de Pruebas y Estrategia de Testing (IEEE 829 / ISO 29119)
## Sistema: CardioVigía 2.4.0 — Monitoreo de Cirugías Cardíacas con IA
**Equipo Evaluador:** Deploy & Pray  
**Integrantes:** Camila Blesa, Ian Gucevich, Samuel Trejo  
**Cátedra:** Verificación y Validación de Software — Licenciatura en Desarrollo de Software (ECyT - UNSAM)  
**Docentes:** Ing. Di Ludovico Maximiliano / Ing. Millan Alejandro  
**Versión del Documento:** 1.0.0 — Octubre 2026  

---

### 1. Introducción y Objetivos
El presente Plan de Pruebas define la estrategia formal, los niveles de prueba, las técnicas de diseño, los criterios de aceptación y los entornos de ejecución para evaluar la versión **2.4.0** del software médico **CardioVigía**.  
El objetivo del equipo de QA es proporcionar evidencia objetiva, repetible y trazable que permita al Comité Directivo del Instituto Cardiovascular del Plata determinar si el sistema se encuentra en condiciones de ser liberado en sus cuatro quirófanos cardiovasculares y en la Unidad de Cuidados Intensivos Cardiovasculares (UCI-CV).

### 2. Alcance (Scope)
#### 2.1 Dentro del Alcance (In-Scope)
* **Validación de Señales Vitales (RF-01, RF-02):** Filtrado de datos fisiológicos de FC (0–300 lpm), PAM (20–200 mmHg), SatO2 (0–100%) y Temperatura (15.0–43.0 °C).
* **Calidad de Señal y Tolerancia (RF-03):** Detección de canales insuficientes (> 15 segundos) y suspensión de estimaciones no confiables.
* **Elegibilidad y Fases Quirúrgicas (RF-04, RF-05):** Restricción de estimación con IA a adultos de 18 a 90 años con perfil completo; modo básico para pacientes fuera de rango.
* **Cálculo de Riesgo y Factores (RF-06):** Segmentación en 4 niveles (Bajo, Moderado, Alto, Crítico) y extracción de los 3 factores determinantes desde nivel moderado.
* **Alertas y Escalamiento (RF-07, RF-08):** Tipos de notificación (visual vs sonora), regla de supresión de 2 minutos (120 s) ante alertas del mismo nivel, y escalamiento automático (Crítica a los 30 s al Coordinador UCI; Alta a los 60 s al Cirujano).
* **Modo Degradado (RF-09):** Conmutación segura ante pérdida de fuentes (> 10 s) o fallas de inferencia.
* **Auditoría e Integración HCE (RF-10, RF-11):** Integridad del log de eventos y configuración protegida con doble rol.
* **Pruebas de API REST:** Endpoints de ingesta de señales y consulta de estados.

#### 2.2 Fuera del Alcance (Out-of-Scope)
* Ensayos clínicos invasivos sobre pacientes reales (prohibido por Ley 25.326 y 26.529).
* Hardware electromédico físico (sensores de pulso, bombas CEC físicas).
* Reentrenamiento o ajuste de hiperparámetros de la red neuronal en tiempo real.

---

### 3. Análisis de Riesgos del Producto y Riesgos Clínicos
Se aplica una matriz de riesgo basada en Severidad Clínica y Probabilidad de Ocurrencia:

| ID | Riesgo Identificado | Severidad | Probabilidad | Mitigación en el Plan de Pruebas |
| :--- | :--- | :--- | :--- | :--- |
| **R-01** | Falso Negativo ante sangrado masivo o shock hipovolémico. | **Catastrófica** | Baja | Pruebas de caja negra con valores extremos y cobertura de caminos del 100% en la lógica de disparo crítico. |
| **R-02** | Fatiga de alarmas por falsos positivos durante la transición a CEC. | **Crítica** | Alta | Pruebas de supresión de alertas idénticas por 120 s y diseño de escenarios BDD para fases quirúrgicas. |
| **R-03** | Falla de escalamiento: la alerta crítica no escala al Coordinador a los 30s si el anestesiólogo no responde. | **Catastrófica** | Media | Pruebas unitarias de temporización estricta y pruebas de integración con aserciones a 29s, 30s y 31s. |
| **R-04** | Corrupción o lectura de señales fisiológicas espurias por ruido de electrobisturí. | **Mayor** | Alta | Pruebas de partición de equivalencia y valores límite (valores > 300 lpm o < 0 lpm descartados). |
| **R-05** | Activación indebida de estimación IA en población no validada (menores de 18 o mayores de 90 años). | **Mayor** | Media | Pruebas exhaustivas de frontera en edades: 17, 18, 90 y 91 años. |

---

### 4. Estrategia y Niveles de Prueba
1. **Pruebas de Caja Negra:**
   * *Partición de Equivalencias y Análisis de Valores Límite:* Sobre todos los rangos numéricos de señales vitales, edades y temporizadores.
   * *Tablas de Decisión:* Para la matriz completa de niveles de riesgo (Bajo, Moderado, Alto, Crítico) vs acciones y destinatarios.
   * *Transición de Estados:* Para el ciclo de vida de las alertas (Emitida ➔ Silenciada ➔ Escalada ➔ Reconocida) y el Modo Degradado.
2. **Pruebas de Caja Blanca y Cobertura (JaCoCo):**
   * Cobertura de sentencias (Line Coverage) &ge; 80%.
   * Cobertura de decisiones y ramas (Branch Coverage) &ge; 85% en servicios críticos.
3. **Pruebas BDD (Behavior-Driven Development con Cucumber):**
   * Escenarios redactados en lenguaje Gherkin clínico para facilitar la auditoría de anestesiólogos y cirujanos.
4. **Testing de Mutación (PITest):**
   * Inyección de fallas en condiciones lógicas (`<` por `<=`, cambios de retorno) para validar que la suite mate mutantes. Mutation Score objetivo &ge; 70%.
5. **Pruebas de API (Postman):**
   * Pruebas funcionales de endpoints con aserciones automatizadas de status code, payloads JSON y contratos.

---

### 5. Criterios de Entrada, Suspensión y Salida (Definition of Done)
* **Criterios de Entrada:**
  * Build de Maven compila sin errores en Java 21.
  * Suite de pruebas unitarias inicial operativa.
* **Criterios de Suspensión:**
  * Bloqueo en la compilación o fallo crítico del entorno que impida ejecutar los tests.
* **Criterios de Salida / DoD (Definition of Done):**
  * 100% de los casos de prueba de la matriz ejecutados.
  * 0 defectos abiertos con severidad Crítica o Bloqueante.
  * Cobertura de ramas en servicios de negocio superior al 85% medida con JaCoCo.
  * Reportes generados y exportados (JaCoCo, PIT, Postman, Cucumber).

---

### 6. Entornos de Prueba
* **Entorno de Desarrollo (Dev):** Ejecución local en Maven con JUnit 5 y perfiles de test aislados.
* **Entorno de QA / Integración:** Simulación de flujos mediante colección de Postman y datasets sintéticos desidentificados.

---

### 7. Indicadores y Métricas de Calidad (KPIs)
* **KPI-1:** Cobertura de Ramas (JaCoCo Branch Coverage) en `ar.edu.unsam.cardiovigia.service`: Meta &ge; 85%.
* **KPI-2:** Tasa de Efectividad de Pruebas (Mutation Score con PIT): Meta &ge; 70%.
* **KPI-3:** Tasa de Defectos Críticos Resueltos vs Encontrados: Meta = 100%.
* **KPI-4:** Tiempo Medio de Respuesta de API en validación: Meta &le; 100 ms.
