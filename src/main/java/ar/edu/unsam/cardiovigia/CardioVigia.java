package ar.edu.unsam.cardiovigia;

public class CardioVigia {

    public boolean validarFrecuenciaCardiaca(int fc) {
        return fc >= 0 && fc <= 300;
    }

    public boolean validarPresionArterialMedia(int pam) {
        return pam >= 20 && pam <= 200;
    }

    public boolean validarSaturacionOxigeno(int sat) {
        return sat >= 0 && sat <= 100;
    }

    public boolean validarTemperatura(double temp) {
        return temp >= 15.0 && temp <= 43.0;
    }

    public boolean esCanalConfiable(int segundosSinSenal) {
        return segundosSinSenal <= 15;
    }

    public boolean esPacienteElegible(int edad, boolean perfilCompleto) {
        if (!perfilCompleto) {
            return false;
        }
        return edad >= 18 && edad <= 90;
    }

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

    public String evaluarEscalamiento(String nivel, int segundosSinReconocer) {
        if ("CRITICO".equals(nivel) && segundosSinReconocer >= 30) {
            return "COORDINADOR_UCI";
        }
        if ("ALTO".equals(nivel) && segundosSinReconocer >= 60) {
            return "CIRUJANO";
        }
        return "NO_ESCALA";
    }

    public boolean debeActivarModoDegradado(int segundosSinSenal, boolean fallaIA) {
        return fallaIA || segundosSinSenal > 10;
    }
}
