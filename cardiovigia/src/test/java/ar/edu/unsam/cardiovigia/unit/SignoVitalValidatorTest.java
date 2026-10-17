package ar.edu.unsam.cardiovigia.unit;

import ar.edu.unsam.cardiovigia.model.SignoVital;
import ar.edu.unsam.cardiovigia.service.SignoVitalValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - SignoVitalValidator (Caja Negra y Caja Blanca)")
class SignoVitalValidatorTest {

    private SignoVitalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SignoVitalValidator();
    }

    @Test
    @DisplayName("CP-FC-01 y 03: Valores límite válidos de Frecuencia Cardíaca [0, 300]")
    void testFrecuenciaCardiacaLimitesValidos() {
        assertTrue(validator.validarFrecuenciaCardiaca(0), "Límite inferior 0 debe ser válido");
        assertTrue(validator.validarFrecuenciaCardiaca(75), "Valor medio 75 debe ser válido");
        assertTrue(validator.validarFrecuenciaCardiaca(300), "Límite superior 300 debe ser válido");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50, 301, 450})
    @DisplayName("CP-FC-04 y 05: Valores límite inválidos de Frecuencia Cardíaca")
    void testFrecuenciaCardiacaLimitesInvalidos(int fcInvalida) {
        assertFalse(validator.validarFrecuenciaCardiaca(fcInvalida), "FC fuera de rango debe ser inválida: " + fcInvalida);
    }

    @Test
    @DisplayName("CP-PAM: Valores límite de Presión Arterial Media [20, 200]")
    void testPresionArterialMediaLimites() {
        assertFalse(validator.validarPresionArterialMedia(19), "PAM 19 debe ser inválida");
        assertTrue(validator.validarPresionArterialMedia(20), "PAM 20 debe ser válida");
        assertTrue(validator.validarPresionArterialMedia(100), "PAM 100 debe ser válida");
        assertTrue(validator.validarPresionArterialMedia(200), "PAM 200 debe ser válida");
        assertFalse(validator.validarPresionArterialMedia(201), "PAM 201 debe ser inválida");
    }

    @Test
    @DisplayName("CP-TEMP: Valores límite de Temperatura [15.0, 43.0]")
    void testTemperaturaLimites() {
        assertFalse(validator.validarTemperatura(14.9), "Temp 14.9 debe ser rechazada");
        assertTrue(validator.validarTemperatura(15.0), "Temp 15.0 (hipotermia inducida) debe ser válida");
        assertTrue(validator.validarTemperatura(36.5), "Temp 36.5 debe ser válida");
        assertTrue(validator.validarTemperatura(43.0), "Temp 43.0 debe ser válida");
        assertFalse(validator.validarTemperatura(43.1), "Temp 43.1 debe ser rechazada");
    }

    @Test
    @DisplayName("CP-SAT: Valores límite de Saturación [0, 100]")
    void testSaturacionLimites() {
        assertFalse(validator.validarSaturacionOxigeno(-1));
        assertTrue(validator.validarSaturacionOxigeno(0));
        assertTrue(validator.validarSaturacionOxigeno(98));
        assertTrue(validator.validarSaturacionOxigeno(100));
        assertFalse(validator.validarSaturacionOxigeno(101));
    }

    @Test
    @DisplayName("RF-03: Canal no confiable si señal ausente > 15 segundos")
    void testCanalConfiable() {
        assertTrue(validator.esCanalConfiable(0));
        assertTrue(validator.esCanalConfiable(15));
        assertFalse(validator.esCanalConfiable(16));
    }

    @Test
    @DisplayName("Validación integral de objeto SignoVital con datos completos")
    void testValidacionIntegral() {
        assertFalse(validator.validarSignosVitales(null));

        SignoVital valido = new SignoVital(80, 90, 99, 36.6, 2);
        assertTrue(validator.validarSignosVitales(valido));

        SignoVital canalCaido = new SignoVital(80, 90, 99, 36.6, 16);
        assertFalse(validator.validarSignosVitales(canalCaido));

        SignoVital datoInvalido = new SignoVital(350, 90, 99, 36.6, 2);
        assertFalse(validator.validarSignosVitales(datoInvalido));
    }
}
