package ar.edu.unsam.cardiovigia.service;

import ar.edu.unsam.cardiovigia.model.Alerta;
import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertaService {

    private final RiesgoService riesgoService;

    // Almacena la última alerta emitida por paciente para la regla de supresión de 2 minutos (120s)
    private final Map<String, Alerta> ultimasAlertasPorPaciente = new HashMap<>();

    public AlertaService(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    /**
     * RF-07: Generación de alertas según nivel de riesgo y control de supresión de 2 min.
     */
    public Alerta procesarAlerta(String pacienteId, double probabilidad, long timestampActualSegundos) {
        NivelRiesgo nivel = riesgoService.clasificarNivelRiesgo(probabilidad);
        List<String> factores = riesgoService.obtenerFactoresDeterminantes(nivel);

        // Regla de no repetir la misma alerta por 2 minutos (120 s) salvo que empeore
        if (ultimasAlertasPorPaciente.containsKey(pacienteId)) {
            Alerta ultima = ultimasAlertasPorPaciente.get(pacienteId);
            long tiempoTranscurrido = timestampActualSegundos - ultima.getTimestampCreacion();

            boolean esMismoNivel = ultima.getNivel() == nivel;
            boolean esNivelMenor = nivel.ordinal() <= ultima.getNivel().ordinal();

            if (tiempoTranscurrido < 120 && (esMismoNivel || esNivelMenor)) {
                return null; // Silenciada temporalmente
            }
        }

        String notificacion;
        String destinatario;

        switch (nivel) {
            case MODERADO:
                notificacion = "INDICACION_VISUAL";
                destinatario = "PANEL_LOCAL";
                break;
            case ALTO:
                notificacion = "ALERTA_ANESTESIOLOGO";
                destinatario = "ANESTESIOLOGO";
                break;
            case CRITICO:
                notificacion = "ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO";
                destinatario = "ANESTESIOLOGO_Y_CIRUJANO";
                break;
            case BAJO:
            default:
                notificacion = "NINGUNA";
                destinatario = "NINGUNO";
                break;
        }

        Alerta nuevaAlerta = new Alerta(
                "ALT-" + System.currentTimeMillis(),
                pacienteId,
                nivel,
                probabilidad,
                factores,
                notificacion,
                destinatario,
                timestampActualSegundos
        );

        ultimasAlertasPorPaciente.put(pacienteId, nuevaAlerta);
        return nuevaAlerta;
    }

    /**
     * RF-08: Escalamiento automático si no fue reconocida.
     * Crítica escala al Coordinador a los 30 s.
     * Alta escala al Cirujano a los 60 s.
     */
    public boolean evaluarEscalamiento(Alerta alerta, long segundosTranscurridos) {
        if (alerta == null || alerta.isReconocida() || alerta.isEscalada()) {
            return false;
        }

        if (alerta.getNivel() == NivelRiesgo.CRITICO && segundosTranscurridos >= 30) {
            alerta.setEscalada(true);
            alerta.setDestinatarioActual("COORDINADOR_UCI");
            return true;
        }

        if (alerta.getNivel() == NivelRiesgo.ALTO && segundosTranscurridos >= 60) {
            alerta.setEscalada(true);
            alerta.setDestinatarioActual("CIRUJANO");
            return true;
        }

        return false;
    }

    /**
     * RF-09: Modo Degradado.
     * Si la pérdida de señal supera los 10 segundos o falla la IA.
     */
    public boolean debeActivarModoDegradado(int segundosSinSenal, boolean fallaIA) {
        return fallaIA || segundosSinSenal > 10;
    }
}
