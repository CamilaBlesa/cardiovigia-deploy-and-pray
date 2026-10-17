package ar.edu.unsam.cardiovigia;

public class CardioVigia {

    // RF-01 / RF-02: Rango Frecuencia Cardíaca [0, 300] lpm
    public boolean validarFrecuenciaCardiaca(int fc) {
        return fc >= 0 && fc <= 300;
    }

    // RF-01 / RF-02: Rango Presión Arterial Media [20, 200] mmHg
    public boolean validarPresionArterialMedia(int pam) {
        return pam >= 20 && pam <= 200;
    }

    // RF-01 / RF-02: Rango Saturación de Oxígeno [0, 100] %
    public boolean validarSaturacionOxigeno(int sat) {
        return sat >= 0 && sat <= 100;
    }

    // RF-01 / RF-02: Rango Temperatura Corporal [15.0, 43.0] °C
    public boolean validarTemperatura(double temp) {
        return temp >= 15.0 && temp <= 43.0;
    }

    // RF-03: Canal no confiable si señal ausente por más de 15 segundos
    public boolean esCanalConfiable(int segundosSinSenal) {
        return segundosSinSenal <= 15;
    }

    // RF-05: Paciente elegible para IA: adulto de 18 a 90 años con perfil completo
    public boolean esPacienteElegible(int edad, boolean perfilCompleto) {
        if (!perfilCompleto) {
            return false;
        }
        return edad >= 18 && edad <= 90;
    }

    // RF-06: Clasificación de Niveles de Riesgo según probabilidad estimada
    public String clasificarNivelRiesgo(double probabilidad) {
        if (probabilidad < 0.0 || probabilidad > 100.0) {
            throw new IllegalArgumentException("La probabilidad debe estar entre 0.0 y 100.0 %");
        }
        if (probabilidad < 20.0) {
            return "BAJO";
        } else if (probabilidad < 50.0) {
            return "MODERADO";
        } else if (probabilidad < 80.0) {
            return "ALTO";
        } else {
            return "CRITICO";
        }
    }

    // RF-07: Notificación según criticidad
    public String determinarNotificacion(String nivel) {
        switch (nivel) {
            case "MODERADO":
                return "INDICACION_VISUAL";
            case "ALTO":
                return "ALERTA_ANESTESIOLOGO";
            case "CRITICO":
                return "ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO";
            default:
                return "NINGUNA";
        }
    }

    // RF-08: Escalamiento automático ante alerta no reconocida
    public String evaluarEscalamiento(String nivel, int segundosSinReconocer) {
        if ("CRITICO".equals(nivel) && segundosSinReconocer >= 30) {
            return "COORDINADOR_UCI";
        }
        if ("ALTO".equals(nivel) && segundosSinReconocer >= 60) {
            return "CIRUJANO";
        }
        return "NO_ESCALA";
    }

    // RF-09: Modo degradado si se pierde señal > 10s o falla la IA
    public boolean debeActivarModoDegradado(int segundosSinSenal, boolean fallaIA) {
        return fallaIA || segundosSinSenal > 10;
    }
}
