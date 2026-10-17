package ar.edu.unsam.cardiovigia.service;

import ar.edu.unsam.cardiovigia.model.SignoVital;
import org.springframework.stereotype.Service;

@Service
public class SignoVitalValidator {

    /**
     * RF-01 / RF-02: Valida rango de Frecuencia Cardíaca (0 - 300 lpm)
     */
    public boolean validarFrecuenciaCardiaca(int fc) {
        return fc >= 0 && fc <= 300;
    }

    /**
     * RF-01 / RF-02: Valida rango de Presión Arterial Media (20 - 200 mmHg)
     */
    public boolean validarPresionArterialMedia(int pam) {
        return pam >= 20 && pam <= 200;
    }

    /**
     * RF-01 / RF-02: Valida rango de Saturación de Oxígeno (0 - 100 %)
     */
    public boolean validarSaturacionOxigeno(int sat) {
        return sat >= 0 && sat <= 100;
    }

    /**
     * RF-01 / RF-02: Valida rango de Temperatura (15.0 - 43.0 °C)
     */
    public boolean validarTemperatura(double temp) {
        return temp >= 15.0 && temp <= 43.0;
    }

    /**
     * RF-03: Si un canal es insuficiente por más de 15 segundos, se marca NO confiable.
     */
    public boolean esCanalConfiable(int segundosSinSenal) {
        return segundosSinSenal <= 15;
    }

    /**
     * Validación integral de los signos vitales.
     */
    public boolean validarSignosVitales(SignoVital signos) {
        if (signos == null) {
            return false;
        }

        if (!esCanalConfiable(signos.getSegundosSinSenal())) {
            return false;
        }

        return validarFrecuenciaCardiaca(signos.getFrecuenciaCardiaca())
                && validarPresionArterialMedia(signos.getPresionArterialMedia())
                && validarSaturacionOxigeno(signos.getSaturacionOxigeno())
                && validarTemperatura(signos.getTemperatura());
    }
}
