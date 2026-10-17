package ar.edu.unsam.cardiovigia.unit;

import ar.edu.unsam.cardiovigia.model.Alerta;
import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import ar.edu.unsam.cardiovigia.service.AlertaService;
import ar.edu.unsam.cardiovigia.service.RiesgoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - AlertaService (RF-07, RF-08, RF-09)")
class AlertaServiceTest {

    private AlertaService alertaService;

    @BeforeEach
    void setUp() {
        alertaService = new AlertaService(new RiesgoService());
    }

    @Test
    @DisplayName("RF-07: Tipo de notificación según criticidad")
    void testNotificacionPorNivel() {
        Alerta moderada = alertaService.procesarAlerta("P1", 25.0, 1000);
        assertNotNull(moderada);
        assertEquals("INDICACION_VISUAL", moderada.getNotificacion());
        assertEquals("PANEL_LOCAL", moderada.getDestinatarioActual());

        Alerta alta = alertaService.procesarAlerta("P2", 60.0, 1000);
        assertNotNull(alta);
        assertEquals("ALERTA_ANESTESIOLOGO", alta.getNotificacion());

        Alerta critica = alertaService.procesarAlerta("P3", 85.0, 1000);
        assertNotNull(critica);
        assertEquals("ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO", critica.getNotificacion());
    }

    @Test
    @DisplayName("RF-07: Supresión de misma alerta por 2 minutos (120 s) salvo agravamiento")
    void testSupresionAlerta2Minutos() {
        // Primera emisión en t = 100
        Alerta alerta1 = alertaService.procesarAlerta("P10", 60.0, 100);
        assertNotNull(alerta1);

        // Intento a los 50s con la misma severidad -> debe silenciarse (retorna null)
        Alerta repetida = alertaService.procesarAlerta("P10", 65.0, 150);
        assertNull(repetida, "Alerta en menos de 120s del mismo nivel debe suprimirse");

        // Intento a los 60s pero con nivel empeorado a CRITICO (85%) -> debe dispararse
        Alerta empeorada = alertaService.procesarAlerta("P10", 85.0, 160);
        assertNotNull(empeorada, "Si el riesgo empeora a crítico, debe dispararse de inmediato");

        // Pasados los 120s (t = 160 + 121 = 281), la alerta puede volver a emitirse
        Alerta posterior = alertaService.procesarAlerta("P10", 85.0, 285);
        assertNotNull(posterior, "Pasados los 120s, la alerta puede volver a emitirse");
    }

    @Test
    @DisplayName("RF-08: Escalamiento automático de Alerta Crítica al Coordinador a los 30s")
    void testEscalamientoCritica30Segundos() {
        Alerta critica = new Alerta("A1", "P1", NivelRiesgo.CRITICO, 88.0, null, "SONORA", "ANESTESIOLOGO", 0);

        assertFalse(alertaService.evaluarEscalamiento(critica, 29), "A los 29s no debe escalar");
        assertFalse(critica.isEscalada());

        assertTrue(alertaService.evaluarEscalamiento(critica, 30), "A los 30s debe escalar");
        assertTrue(critica.isEscalada());
        assertEquals("COORDINADOR_UCI", critica.getDestinatarioActual());

        // Si ya fue reconocida, no debe escalar
        Alerta reconocida = new Alerta("A2", "P1", NivelRiesgo.CRITICO, 88.0, null, "SONORA", "ANESTESIOLOGO", 0);
        reconocida.setReconocida(true);
        assertFalse(alertaService.evaluarEscalamiento(reconocida, 45));
    }

    @Test
    @DisplayName("RF-08: Escalamiento automático de Alerta Alta al Cirujano a los 60s")
    void testEscalamientoAlta60Segundos() {
        Alerta alta = new Alerta("A3", "P1", NivelRiesgo.ALTO, 65.0, null, "VISUAL", "ANESTESIOLOGO", 0);

        assertFalse(alertaService.evaluarEscalamiento(alta, 59));
        assertTrue(alertaService.evaluarEscalamiento(alta, 60));
        assertEquals("CIRUJANO", alta.getDestinatarioActual());
    }

    @Test
    @DisplayName("RF-09: Activación de Modo Degradado ante pérdida de señal > 10s o falla IA")
    void testModoDegradado() {
        assertFalse(alertaService.debeActivarModoDegradado(10, false));
        assertTrue(alertaService.debeActivarModoDegradado(11, false), "Pérdida de señal > 10s activa modo degradado");
        assertTrue(alertaService.debeActivarModoDegradado(5, true), "Falla en componente IA activa modo degradado");
    }
}
