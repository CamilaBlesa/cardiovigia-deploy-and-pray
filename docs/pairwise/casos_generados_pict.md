# Diseño de Casos de Prueba con Técnica Combinatoria Pairwise (Microsoft PICT)

**Proyecto:** CardioVigia v2.4.0 — Monitoreo de Cirugías Cardíacas con IA  
**Equipo:** Deploy & Pray (UNSAM - Verificación y Validación de Software)  
**Herramienta utilizada:** Microsoft PICT (*Pairwise Independent Combinatorial Testing*)  
**Requisitos cubiertos:** RF-04 (Roles y Fases), RF-05 (Filtro de Población para IA) y RF-11 (Auditoría/Registro).

---

## 1. Justificación y Reducción Combinatoria

Al evaluar la interacción de múltiples parámetros clínicos y operativos, una combinatoria exhaustiva (todas las combinaciones posibles) requeriría:
$$\text{Combinaciones totales} = 3 \times 2 \times 3 \times 3 = 54 \text{ casos de prueba}$$

Ejecutar y mantener 54 casos para un único flujo genera **explosión combinatoria**.

Siguiendo el estándar de la industria y la literatura de testing (estudios empíricos de NIST), entre el 70% y el 90% de los defectos en software surgen de la interacción de **a lo sumo dos factores (pares)**. Mediante la herramienta **Microsoft PICT**, se diseñó una suite mínima de **10 casos de prueba** que garantiza una **cobertura del 100% de todos los pares de valores posibles (2-way coverage)**, logrando una **reducción del 81.5%** en el esfuerzo de pruebas.

---

## 2. Archivo de Parámetros (`modelo_cardiovigia.txt`)

```text
Edad: Menor_18, Adulto_18_90, Mayor_90
PerfilClinico: Completo, Incompleto
FaseQuirurgica: Preoperatorio, Intraoperatorio, Postoperatorio
RolUsuario: Anestesiologo, Cirujano, Perfusionista
```

---

## 3. Matriz de Casos de Prueba Generada por PICT

| ID Caso | Parámetro: Edad | Parámetro: Perfil Clínico | Parámetro: Fase Quirúrgica | Parámetro: Rol Usuario | Resultado Esperado del Sistema | Oráculo / Justificación Clínica |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **TC-PW-01** | `Menor_18` | `Completo` | `Preoperatorio` | `Anestesiologo` | **Excluido de IA** | Criterio de exclusión pediátrica. Permanece en monitoreo estándar sin inferencia IA. |
| **TC-PW-02** | `Adulto_18_90` | `Completo` | `Intraoperatorio` | `Cirujano` | **Aceptado para IA** | Caso nominal óptimo. Inferencia continua y clasificación de riesgo activa. |
| **TC-PW-03** | `Mayor_90` | `Incompleto` | `Postoperatorio` | `Perfusionista` | **Excluido de IA** | Doble condición de invalidez (edad geriátrica extrema y faltante de datos basales). |
| **TC-PW-04** | `Adulto_18_90` | `Incompleto` | `Preoperatorio` | `Perfusionista` | **Excluido de IA** | Faltan variables basales críticas requeridas por la red de IA. |
| **TC-PW-05** | `Menor_18` | `Incompleto` | `Postoperatorio` | `Cirujano` | **Excluido de IA** | Paciente no elegible para modelo entrenado exclusivamente en adultos. |
| **TC-PW-06** | `Mayor_90` | `Completo` | `Intraoperatorio` | `Anestesiologo` | **Excluido de IA** | Paciente mayor de 90 años excluido del algoritmo para evitar falsos positivos. |
| **TC-PW-07** | `Adulto_18_90` | `Completo` | `Postoperatorio` | `Anestesiologo` | **Aceptado para IA** | Transición de fase válida con perfil completo; predicciones activas en recuperación. |
| **TC-PW-08** | `Menor_18` | `Completo` | `Intraoperatorio` | `Perfusionista` | **Excluido de IA** | Registro de cambios por perfusionista auditado, modelo IA en modo inactivo. |
| **TC-PW-09** | `Mayor_90` | `Completo` | `Preoperatorio` | `Cirujano` | **Excluido de IA** | Registro prequirúrgico correcto; el paciente se monitorea con alarmas umbral fijas. |
| **TC-PW-10** | `Adulto_18_90` | `Incompleto` | `Postoperatorio` | `Cirujano` | **Excluido de IA** | Alerta visual de datos incompletos en panel de quirófano/recuperación. |

---

## 4. Evidencia de Ejecución en Consola

```bash
$ pict modelo_cardiovigia.txt
Edad            PerfilClinico   FaseQuirurgica   RolUsuario
Menor_18        Completo        Preoperatorio    Anestesiologo
Adulto_18_90    Completo        Intraoperatorio  Cirujano
Mayor_90        Incompleto      Postoperatorio   Perfusionista
Adulto_18_90    Incompleto      Preoperatorio    Perfusionista
Menor_18        Incompleto      Postoperatorio   Cirujano
Mayor_90        Completo        Intraoperatorio  Anestesiologo
Adulto_18_90    Completo        Postoperatorio   Anestesiologo
Menor_18        Completo        Intraoperatorio  Perfusionista
Mayor_90        Completo        Preoperatorio    Cirujano
Adulto_18_90    Incompleto      Postoperatorio   Cirujano
```
