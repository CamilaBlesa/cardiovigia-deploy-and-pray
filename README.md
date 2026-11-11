# CardioVigia

Aplicación Spring Boot para el monitoreo de cirugías cardíacas. El proyecto
incluye pruebas unitarias, de integración del controller, BDD con Cucumber,
pruebas E2E con Selenium, pruebas de API con Postman y análisis de cobertura
con JaCoCo y PIT.

## Requisitos

- Java 21.
- Chrome instalado para las pruebas Selenium.
- Maven Wrapper (`./mvnw`), incluido en el repositorio.
- Postman, si se ejecutan las pruebas de API manualmente.

Verificar Java:

```bash
java -version
```

En macOS o Linux, si el wrapper no tiene permisos:

```bash
chmod +x mvnw
```

## Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en:

- Interfaz web: http://localhost:8080/
- API: http://localhost:8080/api
- Health check: http://localhost:8080/api/salud

Para detenerla, presionar `Ctrl+C`.

## Ejecutar las pruebas

### Suite completa

Ejecuta las pruebas unitarias, MockMvc, Cucumber y Selenium, y genera el
reporte JaCoCo:

```bash
./mvnw clean test
```

### Pruebas unitarias de `CardioVigia`

```bash
./mvnw -Dtest=CardioVigiaApplicationTests test
```

### Pruebas MockMvc del controller

Cubren las rutas REST, respuestas HTTP, roles, límites y errores:

```bash
./mvnw -Dtest=CardioVigiaControllerTest test
```

### Pruebas BDD con Cucumber

```bash
./mvnw -Dtest=CucumberTest test
```

El feature utilizado está en
[`src/test/resources/features/cardiovigia.feature`](src/test/resources/features/cardiovigia.feature).

### Pruebas con Selenium

```bash
./mvnw -Dtest=CardioVigiaSeleniumTest test
```

Selenium inicia Chrome en modo `headless`, por lo que normalmente no se abre
una ventana. Para observar el navegador, quitar temporalmente
`--headless=new` de
[`CardioVigiaSeleniumTest.java`](src/test/java/ar/edu/unsam/cardiovigia/selenium/CardioVigiaSeleniumTest.java).

## JaCoCo

El reporte se genera al ejecutar:

```bash
./mvnw clean test
```

Abrirlo en macOS:

```bash
open target/site/jacoco/index.html
```

Objetivos definidos en el plan:

- Cobertura de líneas: ≥ 80%.
- Cobertura de decisiones: ≥ 75%.
- Cobertura de caminos en código crítico: ≥ 70%.

## PIT (mutation testing)

Ejecutar PIT con las pruebas habilitadas:

```bash
./mvnw -DwithHistory=false org.pitest:pitest-maven:mutationCoverage
```

Abrir el reporte:

```bash
open target/pit-reports/index.html
```

El objetivo del plan es un mutation score de al menos 90%. Los mutantes
sobrevivientes deben revisarse en el reporte HTML, indicando método, línea,
mutador, prueba relacionada y motivo de supervivencia.

## Pruebas de API con Postman

Archivos:

- Colección:
  [`postman/CardioVigia_Postman_Collection.json`](postman/CardioVigia_Postman_Collection.json)
- Entorno:
  [`postman/CardioVigia_Environment.json`](postman/CardioVigia_Environment.json)

1. Iniciar la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

2. Importar la colección y el entorno en Postman.
3. Seleccionar el entorno `CardioVigia - Local`.
4. Verificar que `baseUrl` sea `http://localhost:8080/api`.
5. Ejecutar la colección `CardioVigia API Tests`.

La colección contiene 26 requests distribuidas entre los requisitos RF-01 a
RF-11. Las pruebas Postman validan respuestas HTTP y contratos de la API, pero
no reemplazan la cobertura JaCoCo.

## Pruebas Pairwise

El modelo y los casos generados están documentados en:

- [`docs/pairwise/modelo_cardiovigia.txt`](docs/pairwise/modelo_cardiovigia.txt)
- [`docs/pairwise/casos_generados_pict.md`](docs/pairwise/casos_generados_pict.md)

La ejecución o regeneración de PICT debe realizarse manualmente con Microsoft
PICT o con [`docs/pairwise/pict.exe`](docs/pairwise/pict.exe) en Windows.

## Estado de las pruebas requeridas por el plan

| Tipo de prueba | Estado | Ejecución o evidencia |
|---|---|---|
| Unitarias | Implementada | `./mvnw -Dtest=CardioVigiaApplicationTests test` |
| Controller / MockMvc | Implementada | `./mvnw -Dtest=CardioVigiaControllerTest test` |
| BDD / Cucumber | Implementada | `./mvnw -Dtest=CucumberTest test` |
| E2E / Selenium | Implementada | `./mvnw -Dtest=CardioVigiaSeleniumTest test` |
| API / Postman | Implementada manualmente | Colección en `postman/` |
| Cobertura / JaCoCo | Implementada | `./mvnw clean test` |
| Mutación / PIT | Implementada | Comando de PIT anterior |
| Pairwise / PICT | Documentada, ejecución manual | `docs/pairwise/` |

## Trazabilidad de pruebas

Los artefactos de prueba utilizan identificadores con el formato
`RF-xx Caso de prueba: xx`. `RF-xx` identifica el requisito funcional y el
segundo `xx` es el número correlativo del caso dentro de ese requisito. Los
escenarios Cucumber mantienen además las etiquetas
`@RF-xx`, y las carpetas de Postman identifican el requisito cubierto.

| Requisito | Casos automatizados principales | Artefacto |
|---|---|---|
| RF-01 | `RF-01 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber, Selenium |
| RF-02 | `RF-02 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber |
| RF-03 | `RF-03 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber, Selenium |
| RF-04 | `RF-04 Caso de prueba: xx` | MockMvc, Cucumber |
| RF-05 | `RF-05 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber |
| RF-06 | `RF-06 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber, Selenium |
| RF-07 | `RF-07 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber, Selenium |
| RF-08 | `RF-08 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber |
| RF-09 | `RF-09 Caso de prueba: xx` | Unitarias, MockMvc, Cucumber |
| RF-10 | `RF-10 Caso de prueba: xx` | MockMvc, Cucumber |
| RF-11 | `RF-11 Caso de prueba: xx` | MockMvc, Cucumber |


## Reportes generados

- JaCoCo: `target/site/jacoco/index.html`
- PIT: `target/pit-reports/index.html`
