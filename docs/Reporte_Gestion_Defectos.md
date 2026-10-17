# Reporte de Gestión de Defectos y Ciclo de Vida (Jira)
## Proyecto: CardioVigía 2.4.0 — TPI Verificación y Validación de Software (UNSAM)
**Equipo:** Deploy & Pray  
**Integrantes:** Camila Blesa, Ian Gucevich, Samuel Trejo  
**Fecha de Corte:** Octubre 2026

---

### 1. Política de Clasificación de Defectos en Contexto Médico Crítico

* **Severidad Clínica:**
  * **S1 - Crítica (Peligro de Vida):** Defectos que silencian alertas ante descompensación, impiden el escalamiento o emiten datos falseados al cirujano.
  * **S2 - Mayor (Degradación Severa):** Pérdida de fuentes sin pasar a modo degradado, o alertas duplicadas que inducen fatiga clínica.
  * **S3 - Menor (Cosmética / Usabilidad):** Retardo en etiquetas visuales, tipografías poco legibles con luz atenuada.
* **Prioridad de Corrección:**
  * **P1 - Bloqueante:** Debe solucionarse antes de continuar con la suite o liberar a producción.
  * **P2 - Alta:** Debe corregirse en la iteración actual.
  * **P3 - Media:** Puede corregirse en el siguiente release.

---

### 2. Defectos Registrados durante las Pruebas

#### Defecto 1: `BUG-CV-01`
* **Título:** El temporizador de escalamiento de alerta crítica no escala a los 30s si la alerta se dispara en el segundo límite de reloj.
* **Requisito Afectado:** `RF-08: Escalamiento automático de alertas críticas al Coordinador a los 30 segundos`.
* **Severidad:** `S1 - Crítica` | **Prioridad:** `P1 - Bloqueante`
* **Estado:** `CERRADO / RESUELTO`
* **Ciclo de Vida:** `Nuevo ➔ En Análisis ➔ En Corrección ➔ Verificado en QA ➔ Cerrado`
* **Causa Raíz:** Condición de frontera estricta (`> 30` en lugar de `>= 30`) en la evaluación del temporizador de seguridad.
* **Resolución:** Se corrigió en `AlertaService.java` implementando `>= 30` y se añadieron aserciones a 29s, 30s y 31s.

#### Defecto 2: `BUG-CV-02`
* **Título:** Señal de temperatura de 15.0 °C rechazada erróneamente durante inducción de hipotermia en circulación extracorpórea (CEC).
* **Requisito Afectado:** `RF-01 / RF-02: Rango válido de temperatura (15.0 - 43.0 °C)`.
* **Severidad:** `S2 - Mayor` | **Prioridad:** `P1 - Bloqueante`
* **Estado:** `CERRADO / RESUELTO`
* **Causa Raíz:** Se utilizaba operador de desigualdad estricta (`temp > 15.0`) descartando el valor límite inferior exacto.
* **Resolución:** Se ajustó a `temp >= 15.0 && temp <= 43.0`, habilitando la hipotermia terapéutica en CEC.

#### Defecto 3: `BUG-CV-03`
* **Título:** Supresión de alertas silencia eventos de agravamiento súbito (pasaje de riesgo Alto a Crítico dentro de los 2 minutos).
* **Requisito Afectado:** `RF-07: No repetir la misma alerta por 2 min salvo que empeore`.
* **Severidad:** `S1 - Crítica` | **Prioridad:** `P1 - Bloqueante`
* **Estado:** `CERRADO / RESUELTO`
* **Causa Raíz:** La lógica de silenciamiento comparaba únicamente el tiempo transcurrido sin evaluar la severidad del nuevo riesgo.
* **Resolución:** Se incorporó la cláusula `nivel.ordinal() <= ultima.getNivel().ordinal()`. Si el riesgo actual empeora, la supresión se cancela y la alarma suena de inmediato.

#### Defecto 4: `BUG-CV-04`
* **Título:** Inconsistencia de contrato en endpoint `/api/alertas/evaluar` al enviar valores de probabilidad menores a 0.
* **Requisito Afectado:** `RF-06: Probabilidad comprendida entre 0% y 100%`.
* **Severidad:** `S3 - Menor` | **Prioridad:** `P2 - Alta`
* **Estado:** `CERRADO / RESUELTO`
* **Resolución:** Se incorporó validación de entrada en `RiesgoService` arrojando `IllegalArgumentException` con código HTTP 400 controlado.
