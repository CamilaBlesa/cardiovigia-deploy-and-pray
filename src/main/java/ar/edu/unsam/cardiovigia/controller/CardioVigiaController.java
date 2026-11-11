package ar.edu.unsam.cardiovigia.controller;

import ar.edu.unsam.cardiovigia.CardioVigia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CardioVigiaController {

    private final CardioVigia cardioVigia;

    public CardioVigiaController(CardioVigia cardioVigia) {
        this.cardioVigia = cardioVigia;
    }

    // HealthCheck
    @GetMapping("/salud")
    public ResponseEntity<EstadoSalud> estadoSalud() {
        return ResponseEntity.ok(new EstadoSalud("CardioVigia", "2.4.0", "OPERATIVO"));
    }

    // SignosVitales (RF-01, RF-02)
    @PostMapping("/signos-vitales/validar")
    public ResponseEntity<ValidacionSignosRespuesta> validarSignosVitales(@RequestBody SignosVitalesPeticion peticion) {
        boolean fcOk = cardioVigia.validarFrecuenciaCardiaca(peticion.frecuenciaCardiaca());
        boolean pamOk = cardioVigia.validarPresionArterialMedia(peticion.presionArterialMedia());
        boolean satOk = cardioVigia.validarSaturacionOxigeno(peticion.saturacionOxigeno());
        boolean tempOk = cardioVigia.validarTemperatura(peticion.temperatura());
        boolean canalOk = cardioVigia.esCanalConfiable(peticion.segundosSinSenal());

        boolean globalValido = fcOk && pamOk && satOk && tempOk && canalOk;

        ValidacionSignosRespuesta respuesta = new ValidacionSignosRespuesta(
                peticion.pacienteId(),
                globalValido,
                canalOk,
                fcOk,
                pamOk,
                satOk,
                tempOk
        );
        return globalValido ? ResponseEntity.ok(respuesta) : ResponseEntity.badRequest().body(respuesta);
    }

    // CalidadSenal (RF-03)
    @GetMapping("/calidad-senal/{canalId}")
    public ResponseEntity<CalidadSenalRespuesta> obtenerCalidadSenal(@PathVariable String canalId) {
        return ResponseEntity.ok(new CalidadSenalRespuesta(canalId, "CONFIABLE"));
    }

    // Pacientes (RF-04, RF-05)
    @PostMapping("/pacientes")
    public ResponseEntity<PacienteRespuesta> registrarPaciente(@RequestBody PacientePeticion peticion) {
        // En implementación real, persistir paciente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PacienteRespuesta(peticion.pacienteId(), "REGISTRADO"));
    }

    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<PacienteRespuesta> obtenerPaciente(@PathVariable String pacienteId) {
        // En implementación real, consultar BD
        return ResponseEntity.ok(new PacienteRespuesta(pacienteId, "ENCONTRADO"));
    }

    // Fases Quirúrgicas (RF-04)
    @PutMapping("/pacientes/{pacienteId}/fase")
    public ResponseEntity<CambioFaseRespuesta> cambiarFase(
            @PathVariable String pacienteId,
            @RequestBody CambioFasePeticion peticion) {

        // Validar rol autorizado (RF-04: solo anestesiólogo o perfusionista)
        boolean rolAutorizado = "ANESTESIOLOGO".equals(peticion.rol()) ||
                                "PERFUSIONISTA".equals(peticion.rol());
        if (!rolAutorizado) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CambioFaseRespuesta(false, "ROL_NO_AUTORIZADO", "Solo anestesiólogo o perfusionista pueden cambiar fase"));
        }

        // Validar transición de fase válida
        boolean transicionValida = validarTransicionFase(peticion.faseNueva());
        if (!transicionValida) {
            return ResponseEntity.badRequest()
                    .body(new CambioFaseRespuesta(false, "TRANSICION_INVALIDA", "Transición de fase no permitida"));
        }

        // En implementación real: persistir cambio de fase + auditoría
        return ResponseEntity.ok(new CambioFaseRespuesta(true, "FASE_ACTUALIZADA", "Fase cambiada a " + peticion.faseNueva()));
    }

    private boolean validarTransicionFase(String faseNueva) {
        // Fases válidas: PREOPERATORIO, INDUCCION, MANTENIMIENTO, CIRCULACION, POSTOPERATORIO
        return "INDUCCION".equals(faseNueva) || "MANTENIMIENTO".equals(faseNueva) ||
               "CIRCULACION".equals(faseNueva) || "POSTOPERATORIO".equals(faseNueva);
    }

    // Pacientes (RF-04, RF-05)
    @PostMapping("/pacientes/elegibilidad")
    public ResponseEntity<ElegibilidadRespuesta> evaluarElegibilidad(@RequestBody ElegibilidadPeticion peticion) {
        boolean elegible = cardioVigia.esPacienteElegible(peticion.edad(), peticion.perfilCompleto());

        ElegibilidadRespuesta respuesta = new ElegibilidadRespuesta(
                peticion.pacienteId(),
                peticion.edad(),
                peticion.perfilCompleto(),
                elegible ? "ELEGIBLE" : "EXCLUIDO",
                elegible
        );
        return elegible ? ResponseEntity.ok(respuesta) : ResponseEntity.badRequest().body(respuesta);
    }

    // AlertasYEscalamiento (RF-06, RF-07, RF-08)
    @PostMapping("/alertas/evaluar")
    public ResponseEntity<AlertaRespuesta> evaluarAlerta(@RequestBody AlertaPeticion peticion) {
        try {
            String nivel = cardioVigia.clasificarNivelRiesgo(peticion.probabilidad());
            String notificacion = cardioVigia.determinarNotificacion(nivel);

            AlertaRespuesta respuesta = new AlertaRespuesta(
                    peticion.probabilidad(),
                    nivel,
                    notificacion
            );
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AlertaRespuesta(peticion.probabilidad(), "ERROR", "PROBABILIDAD_INVALIDA")
            );
        }
    }

    @PostMapping("/alertas/escalamiento")
    public ResponseEntity<EscalamientoRespuesta> evaluarEscalamiento(@RequestBody EscalamientoPeticion peticion) {
        String destino = cardioVigia.evaluarEscalamiento(peticion.nivel(), peticion.segundosSinReconocer());

        EscalamientoRespuesta respuesta = new EscalamientoRespuesta(
                peticion.nivel(),
                peticion.segundosSinReconocer(),
                destino
        );
        return ResponseEntity.ok(respuesta);
    }

    // ModoDegradado (RF-09)
    @PostMapping("/modo-degradado")
    public ResponseEntity<ModoDegradadoRespuesta> evaluarModoDegradado(@RequestBody ModoDegradadoPeticion peticion) {
        boolean degradado = cardioVigia.debeActivarModoDegradado(peticion.segundosSinSenal(), peticion.fallaIA());

        ModoDegradadoRespuesta respuesta = new ModoDegradadoRespuesta(
                peticion.segundosSinSenal(),
                peticion.fallaIA(),
                degradado
        );
        return ResponseEntity.ok(respuesta);
    }

    // HCE (RF-10)
    @PostMapping("/hce/resumen")
    public ResponseEntity<HceResumenRespuesta> enviarResumenHCE(@RequestBody HceResumenPeticion peticion) {
        return ResponseEntity.ok(new HceResumenRespuesta(peticion.cirugiaId(), "RECIBIDO_OK", true));
    }

    @GetMapping("/hce/estado/{cirugiaId}")
    public ResponseEntity<HceEstadoRespuesta> obtenerEstadoAuditoriaHCE(@PathVariable String cirugiaId) {
        return ResponseEntity.ok(new HceEstadoRespuesta(cirugiaId, true, "RECIBIDO_OK"));
    }

    // ConfiguracionUmbrales (RF-11)
    @PutMapping("/config/umbrales")
    public ResponseEntity<UmbralesRespuesta> actualizarUmbrales(@RequestBody UmbralesPeticion peticion) {
        if (peticion.cirugiaActiva()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new UmbralesRespuesta(false, "BLOQUEADO_EN_CIRUGIA"));
        }
        if (peticion.umbralAlto() < 40.0 || peticion.umbralAlto() > 60.0 || peticion.umbralCritico() < 70.0) {
            return ResponseEntity.badRequest()
                    .body(new UmbralesRespuesta(false, "RECHAZADO"));
        }
        return ResponseEntity.ok(new UmbralesRespuesta(true, "ACEPTADO"));
    }

    // DTOs
    public record EstadoSalud(String sistema, String version, String estado) {}
    public record SignosVitalesPeticion(String pacienteId, int frecuenciaCardiaca, int presionArterialMedia, int saturacionOxigeno, double temperatura, int segundosSinSenal) {}
    public record ValidacionSignosRespuesta(String pacienteId, boolean todoValido, boolean canalConfiable, boolean frecuenciaCardiacaValida, boolean presionMediaValida, boolean saturacionValida, boolean temperaturaValida) {}
    public record CalidadSenalRespuesta(String canalId, String estado) {}
    public record ElegibilidadPeticion(String pacienteId, int edad, boolean perfilCompleto) {}
    public record ElegibilidadRespuesta(String pacienteId, int edad, boolean perfilCompleto, String estado, boolean elegible) {}
    public record AlertaPeticion(double probabilidad) {}
    public record AlertaRespuesta(double probabilidad, String nivel, String notificacion) {}
    public record EscalamientoPeticion(String nivel, int segundosSinReconocer) {}
    public record EscalamientoRespuesta(String nivel, int segundosSinReconocer, String destinatario) {}
    public record ModoDegradadoPeticion(int segundosSinSenal, boolean fallaIA) {}
    public record ModoDegradadoRespuesta(int segundosSinSenal, boolean fallaIA, boolean modoDegradadoActivo) {}
    public record HceResumenPeticion(String cirugiaId, String pacienteId, Object resumenClinico) {}
    public record HceResumenRespuesta(String cirugiaId, String estadoEnvio, boolean auditado) {}
    public record HceEstadoRespuesta(String cirugiaId, boolean auditado, String estadoEnvio) {}
    public record UmbralesPeticion(double umbralAlto, double umbralCritico, boolean cirugiaActiva) {}
    public record UmbralesRespuesta(boolean actualizado, String resultado) {}

    // DTOs para Pacientes y Fases (RF-04, RF-05)
    public record PacientePeticion(String pacienteId, int edad, boolean perfilCompleto) {}
    public record PacienteRespuesta(String pacienteId, String estado) {}
    public record CambioFasePeticion(String faseNueva, String rol) {}
    public record CambioFaseRespuesta(boolean exitoso, String codigo, String mensaje) {}
}