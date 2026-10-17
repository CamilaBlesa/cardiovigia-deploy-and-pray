package ar.edu.unsam.cardiovigia.unit;

import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import ar.edu.unsam.cardiovigia.model.Paciente;
import ar.edu.unsam.cardiovigia.service.RiesgoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - RiesgoService (RF-05 y RF-06)")
class RiesgoServiceTest {

    private RiesgoService riesgoService;

    @BeforeEach
    void setUp() {
        riesgoService = new RiesgoService();
    }

    @Test
    @DisplayName("RF-05: Elegibilidad de edad en valores límite [18, 90] y perfil completo")
    void testElegibilidadPaciente() {
        assertFalse(riesgoService.esElegibleParaEstimacionIA(null));

        Paciente pIncompleto = new Paciente("P1", 45, false, "Induccion");
        assertFalse(riesgoService.esElegibleParaEstimacionIA(pIncompleto), "Perfil incompleto no debe ser elegible");

        Paciente pMenor = new Paciente("P2", 17, true, "Induccion");
        assertFalse(riesgoService.esElegibleParaEstimacionIA(pMenor), "Menor de 18 no debe ser elegible");

        Paciente pLimiteInf = new Paciente("P3", 18, true, "Induccion");
        assertTrue(riesgoService.esElegibleParaEstimacionIA(pLimiteInf), "Edad 18 debe ser elegible");

        Paciente pLimiteSup = new Paciente("P4", 90, true, "Induccion");
        assertTrue(riesgoService.esElegibleParaEstimacionIA(pLimiteSup), "Edad 90 debe ser elegible");

        Paciente pMayor = new Paciente("P5", 91, true, "Induccion");
        assertFalse(riesgoService.esElegibleParaEstimacionIA(pMayor), "Mayor de 90 no debe ser elegible");
    }

    @Test
    @DisplayName("RF-06: Clasificación de Niveles de Riesgo según particiones y límites")
    void testClasificacionRiesgo() {
        assertEquals(NivelRiesgo.BAJO, riesgoService.clasificarNivelRiesgo(0.0));
        assertEquals(NivelRiesgo.BAJO, riesgoService.clasificarNivelRiesgo(19.9));

        assertEquals(NivelRiesgo.MODERADO, riesgoService.clasificarNivelRiesgo(20.0));
        assertEquals(NivelRiesgo.MODERADO, riesgoService.clasificarNivelRiesgo(49.9));

        assertEquals(NivelRiesgo.ALTO, riesgoService.clasificarNivelRiesgo(50.0));
        assertEquals(NivelRiesgo.ALTO, riesgoService.clasificarNivelRiesgo(79.9));

        assertEquals(NivelRiesgo.CRITICO, riesgoService.clasificarNivelRiesgo(80.0));
        assertEquals(NivelRiesgo.CRITICO, riesgoService.clasificarNivelRiesgo(100.0));
    }

    @Test
    @DisplayName("RF-06: Lanzamiento de excepción ante probabilidades fuera de [0, 100]")
    void testProbabilidadFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> riesgoService.clasificarNivelRiesgo(-0.1));
        assertThrows(IllegalArgumentException.class, () -> riesgoService.clasificarNivelRiesgo(100.1));
    }

    @Test
    @DisplayName("RF-06: Factores determinantes presentes desde nivel Moderado")
    void testFactoresDeterminantes() {
        assertTrue(riesgoService.obtenerFactoresDeterminantes(NivelRiesgo.BAJO).isEmpty());

        List<String> factoresModerado = riesgoService.obtenerFactoresDeterminantes(NivelRiesgo.MODERADO);
        assertEquals(3, factoresModerado.size());

        List<String> factoresCritico = riesgoService.obtenerFactoresDeterminantes(NivelRiesgo.CRITICO);
        assertEquals(3, factoresCritico.size());
    }
}
