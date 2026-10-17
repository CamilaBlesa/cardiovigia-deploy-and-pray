package ar.edu.unsam.cardiovigia.controller;

import ar.edu.unsam.cardiovigia.model.Alerta;
import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import ar.edu.unsam.cardiovigia.model.Paciente;
import ar.edu.unsam.cardiovigia.model.SignoVital;
import ar.edu.unsam.cardiovigia.service.AlertaService;
import ar.edu.unsam.cardiovigia.service.RiesgoService;
import ar.edu.unsam.cardiovigia.service.SignoVitalValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CardioVigiaController {

    private final SignoVitalValidator validator;
    private final RiesgoService riesgoService;
    private final AlertaService alertaService;

    public CardioVigiaController(SignoVitalValidator validator, RiesgoService riesgoService, AlertaService alertaService) {
        this.validator = validator;
        this.riesgoService = riesgoService;
        this.alertaService = alertaService;
    }

    @GetMapping("/salud")
    public ResponseEntity<Map<String, Object>> estadoSalud() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("sistema", "CardioVigia");
        resp.put("version", "2.4.0");
        resp.put("estado", "OPERATIVO");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/signos-vitales/validar")
    public ResponseEntity<Map<String, Object>> validarSignosVitales(@RequestBody SignoVital signos) {
        boolean valido = validator.validarSignosVitales(signos);
        boolean canalConfiable = validator.esCanalConfiable(signos.getSegundosSinSenal());

        Map<String, Object> resp = new HashMap<>();
        resp.put("valido", valido);
        resp.put("canalConfiable", canalConfiable);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/pacientes/elegibilidad")
    public ResponseEntity<Map<String, Object>> evaluarElegibilidad(@RequestBody Paciente paciente) {
        boolean elegible = riesgoService.esElegibleParaEstimacionIA(paciente);
        Map<String, Object> resp = new HashMap<>();
        resp.put("pacienteId", paciente.getId());
        resp.put("elegibleIA", elegible);
        resp.put("modo", elegible ? "ESTIMACION_IA" : "MONITOREO_BASICO");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/alertas/evaluar")
    public ResponseEntity<Map<String, Object>> evaluarAlerta(
            @RequestParam String pacienteId,
            @RequestParam double probabilidad,
            @RequestParam(defaultValue = "0") long timestampSegundos) {

        long ts = timestampSegundos > 0 ? timestampSegundos : (System.currentTimeMillis() / 1000);
        Alerta alerta = alertaService.procesarAlerta(pacienteId, probabilidad, ts);

        Map<String, Object> resp = new HashMap<>();
        if (alerta != null) {
            resp.put("alertaEmitida", true);
            resp.put("nivel", alerta.getNivel().name());
            resp.put("notificacion", alerta.getNotificacion());
            resp.put("destinatario", alerta.getDestinatarioActual());
            resp.put("factores", alerta.getFactoresPrincipales());
        } else {
            resp.put("alertaEmitida", false);
            resp.put("motivo", "SILENCIADA_POR_SUPRESION_2_MIN_O_BAJO_RIESGO");
        }
        return ResponseEntity.ok(resp);
    }
}
