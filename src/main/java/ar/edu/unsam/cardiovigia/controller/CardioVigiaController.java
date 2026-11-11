package ar.edu.unsam.cardiovigia.controller;

import ar.edu.unsam.cardiovigia.CardioVigia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CardioVigiaController {

    private final CardioVigia cardioVigia;

    public CardioVigiaController(CardioVigia cardioVigia) {
        this.cardioVigia = cardioVigia;
    }

    @GetMapping("/salud")
    public ResponseEntity<EstadoSalud> estadoSalud() {
        return ResponseEntity.ok(new EstadoSalud("CardioVigia", "2.4.0", "OPERATIVO"));
    }

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

    // DTOs - Records para type-safety y documentación automática
    public record SignosVitalesPeticion(
        String pacienteId,
        int frecuenciaCardiaca,
        int presionArterialMedia,
        int saturacionOxigeno,
        double temperatura,
        int segundosSinSenal
    ) {}

    public record ValidacionSignosRespuesta(
        String pacienteId,
        boolean todoValido,
        boolean canalConfiable,
        boolean frecuenciaCardiacaValida,
        boolean presionMediaValida,
        boolean saturacionValida,
        boolean temperaturaValida
    ) {}

    public record ElegibilidadPeticion(
        String pacienteId,
        int edad,
        boolean perfilCompleto
    ) {}

    public record ElegibilidadRespuesta(
        String pacienteId,
        int edad,
        boolean perfilCompleto,
        String estado,
        boolean elegible
    ) {}

    public record AlertaPeticion(
        double probabilidad
    ) {}

    public record AlertaRespuesta(
        double probabilidad,
        String nivel,
        String notificacion
    ) {}

    public record EscalamientoPeticion(
        String nivel,
        int segundosSinReconocer
    ) {}

    public record EscalamientoRespuesta(
        String nivel,
        int segundosSinReconocer,
        String destinatario
    ) {}

    public record ModoDegradadoPeticion(
        int segundosSinSenal,
        boolean fallaIA
    ) {}

    public record ModoDegradadoRespuesta(
        int segundosSinSenal,
        boolean fallaIA,
        boolean modoDegradadoActivo
    ) {}

    public record EstadoSalud(
        String sistema,
        String version,
        String estado
    ) {}
}