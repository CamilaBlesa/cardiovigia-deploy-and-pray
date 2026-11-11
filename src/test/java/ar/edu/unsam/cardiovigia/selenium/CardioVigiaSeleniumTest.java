package ar.edu.unsam.cardiovigia.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardioVigiaSeleniumTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @Test
    void testValidacionSignosVitalesWeb() {
        driver.get("http://localhost:" + port);
        driver.findElement(By.id("fc")).clear();
        driver.findElement(By.id("fc")).sendKeys("75");
        driver.findElement(By.id("btnValidar")).click();
        assertEquals("Signos Válidos (Canal Confiable)",
                driver.findElement(By.id("resultadoValidacion")).getText());
    }

    @Test
    void testRechazaSignosVitalesFueraDeRangoWeb() {
        driver.get("http://localhost:" + port);
        driver.findElement(By.id("fc")).clear();
        driver.findElement(By.id("fc")).sendKeys("301");
        driver.findElement(By.id("btnValidar")).click();
        assertEquals("Signos Inválidos (Descarte por Ruido)",
                driver.findElement(By.id("resultadoValidacion")).getText());
    }

    @Test
    void testAlertaCriticaWeb() {
        driver.get("http://localhost:" + port);
        driver.findElement(By.id("probabilidad")).clear();
        driver.findElement(By.id("probabilidad")).sendKeys("85");
        driver.findElement(By.id("btnEvaluarRiesgo")).click();
        assertEquals("Nivel de Riesgo: CRITICO - Alerta Sonora Dual Emitida",
                driver.findElement(By.id("resultadoRiesgo")).getText());
    }

    @Test
    void testRechazaProbabilidadFueraDeRangoWeb() {
        driver.get("http://localhost:" + port);
        driver.findElement(By.id("probabilidad")).clear();
        driver.findElement(By.id("probabilidad")).sendKeys("101");
        driver.findElement(By.id("btnEvaluarRiesgo")).click();
        assertEquals("Probabilidad Inválida (debe estar entre 0 y 100)",
                driver.findElement(By.id("resultadoRiesgo")).getText());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
