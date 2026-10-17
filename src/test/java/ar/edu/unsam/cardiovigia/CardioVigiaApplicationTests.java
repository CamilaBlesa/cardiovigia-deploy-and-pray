package ar.edu.unsam.cardiovigia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - CardioVigia (JUnit 5)")
class CardioVigiaApplicationTests {

    private CardioVigia cardioVigia;

    @BeforeEach
    void setUp() {
        cardioVigia = new CardioVigia();
    }

    @Test
    @DisplayName("RF-01: Valores límite válidos de Frecuencia Cardíaca [0, 300]")
    void testFrecuenciaCardiacaLimitesValidos() {
        assertTrue(cardioVigia.validarFrecuenciaCardiaca(0));
        assertTrue(cardioVigia.validarFrecuenciaCardiaca(75));
        assertTrue(cardioVigia.validarFrecuenciaCardiaca(300));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 301, 450})
    @DisplayName("RF-01: Valores límite inválidos de Frecuencia Cardíaca")
    void testFrecuenciaCardiacaLimitesInvalidos(int fc) {
        assertFalse(cardioVigia.validarFrecuenciaCardiaca(fc));
    }

    @Test
    @DisplayName("RF-01: Valores límite de Presión Arterial Media [20, 200]")
    void testPresionArterialMediaLimites() {
        assertFalse(cardioVigia.validarPresionArterialMedia(19));
        assertTrue(cardioVigia.validarPresionArterialMedia(20));
        assertTrue(cardioVigia.validarPresionArterialMedia(100));
        assertTrue(cardioVigia.validarPresionArterialMedia(200));
        assertFalse(cardioVigia.validarPresionArterialMedia(201));
    }

    @Test
    @DisplayName("RF-01: Valores límite de Temperatura [15.0, 43.0]")
    void testTemperaturaLimites() {
        assertFalse(cardioVigia.validarTemperatura(14.9));
        assertTrue(cardioVigia.validarTemperatura(15.0));
        assertTrue(cardioVigia.validarTemperatura(36.5));
        assertTrue(cardioVigia.validarTemperatura(43.0));
        assertFalse(cardioVigia.validarTemperatura(43.1));
    }

    @Test
    @DisplayName("RF-01: Valores límite de Saturación [0, 100]")
    void testSaturacionLimites() {
        assertFalse(cardioVigia.validarSaturacionOxigeno(-1));
        assertTrue(cardioVigia.validarSaturacionOxigeno(0));
        assertTrue(cardioVigia.validarSaturacionOxigeno(99));
        assertTrue(cardioVigia.validarSaturacionOxigeno(100));
        assertFalse(cardioVigia.validarSaturacionOxigeno(101));
    }

    @Test
    @DisplayName("RF-03: Calidad de señal (confiable hasta 15s sin señal)")
    void testCanalConfiable() {
        assertTrue(cardioVigia.esCanalConfiable(0));
        assertTrue(cardioVigia.esCanalConfiable(15));
        assertFalse(cardioVigia.esCanalConfiable(16));
    }

    @Test
    @DisplayName("RF-05: Elegibilidad de paciente (edad 18 a 90 con perfil completo)")
    void testElegibilidadPaciente() {
        assertFalse(cardioVigia.esPacienteElegible(45, false), "Perfil incompleto debe ser rechazado");
        assertFalse(cardioVigia.esPacienteElegible(17, true), "Menor de 18 no es elegible");
        assertTrue(cardioVigia.esPacienteElegible(18, true), "Edad 18 es elegible");
        assertTrue(cardioVigia.esPacienteElegible(90, true), "Edad 90 es elegible");
        assertFalse(cardioVigia.esPacienteElegible(91, true), "Mayor de 90 no es elegible");
    }

    @Test
    @DisplayName("RF-06: Clasificación de Niveles de Riesgo según probabilidad")
    void testClasificacionRiesgo() {
        assertEquals("BAJO", cardioVigia.clasificarNivelRiesgo(15.0));
        assertEquals("MODERADO", cardioVigia.clasificarNivelRiesgo(20.0));
        assertEquals("ALTO", cardioVigia.clasificarNivelRiesgo(50.0));
        assertEquals("CRITICO", cardioVigia.clasificarNivelRiesgo(80.0));
        assertThrows(IllegalArgumentException.class, () -> cardioVigia.clasificarNivelRiesgo(-1.0));
    }

    @Test
    @DisplayName("RF-07: Notificación según nivel de riesgo")
    void testNotificaciones() {
        assertEquals("INDICACION_VISUAL", cardioVigia.determinarNotificacion("MODERADO"));
        assertEquals("ALERTA_ANESTESIOLOGO", cardioVigia.determinarNotificacion("ALTO"));
        assertEquals("ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO", cardioVigia.determinarNotificacion("CRITICO"));
        assertEquals("NINGUNA", cardioVigia.determinarNotificacion("BAJO"));
    }

    @Test
    @DisplayName("RF-08: Escalamiento automático a los 30s (Crítico) y 60s (Alto)")
    void testEscalamiento() {
        assertEquals("NO_ESCALA", cardioVigia.evaluarEscalamiento("CRITICO", 29));
        assertEquals("COORDINADOR_UCI", cardioVigia.evaluarEscalamiento("CRITICO", 30));

        assertEquals("NO_ESCALA", cardioVigia.evaluarEscalamiento("ALTO", 59));
        assertEquals("CIRUJANO", cardioVigia.evaluarEscalamiento("ALTO", 60));
    }

    @Test
    @DisplayName("RF-09: Modo degradado si señal perdida > 10s o falla IA")
    void testModoDegradado() {
        assertFalse(cardioVigia.debeActivarModoDegradado(10, false));
        assertTrue(cardioVigia.debeActivarModoDegradado(11, false));
        assertTrue(cardioVigia.debeActivarModoDegradado(0, true));
    }
}
