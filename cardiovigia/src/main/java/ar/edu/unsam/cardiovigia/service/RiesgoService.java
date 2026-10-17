package ar.edu.unsam.cardiovigia.service;

import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import ar.edu.unsam.cardiovigia.model.Paciente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiesgoService {

    /**
     * RF-05: Valida elegibilidad de paciente para estimación con IA.
     * Solo aplica a adultos de 18 a 90 años con perfil completo.
     */
    public boolean esElegibleParaEstimacionIA(Paciente paciente) {
        if (paciente == null) {
            return false;
        }
        if (!paciente.isPerfilCompleto()) {
            return false;
        }
        return paciente.getEdad() >= 18 && paciente.getEdad() <= 90;
    }

    /**
     * RF-06: Clasificación de Niveles de Riesgo según probabilidad (0% a 100%).
     */
    public NivelRiesgo clasificarNivelRiesgo(double probabilidad) {
        if (probabilidad < 0.0 || probabilidad > 100.0) {
            throw new IllegalArgumentException("La probabilidad debe estar comprendida entre 0.0 y 100.0 %");
        }

        if (probabilidad < 20.0) {
            return NivelRiesgo.BAJO;
        } else if (probabilidad < 50.0) {
            return NivelRiesgo.MODERADO;
        } else if (probabilidad < 80.0) {
            return NivelRiesgo.ALTO;
        } else {
            return NivelRiesgo.CRITICO;
        }
    }

    /**
     * RF-06: Desde nivel Moderado se deben extraer los 3 factores determinantes principales.
     */
    public List<String> obtenerFactoresDeterminantes(NivelRiesgo nivel) {
        List<String> factores = new ArrayList<>();
        if (nivel != NivelRiesgo.BAJO) {
            factores.add("Variabilidad sostenida de PAM");
            factores.add("Tiempo acumulado en CEC");
            factores.add("Índice de saturación venosa central");
        }
        return factores;
    }
}
