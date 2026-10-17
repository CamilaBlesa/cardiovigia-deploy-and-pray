package ar.edu.unsam.cardiovigia.bdd;

import ar.edu.unsam.cardiovigia.CardioVigia;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CardioVigiaStepDefinitions {

    private final CardioVigia cardioVigia = new CardioVigia();

    private int frecuenciaCardiaca;
    private boolean medicionAprobada;

    private int edadPaciente;
    private boolean perfilCompleto;
    private boolean pacienteElegible;

    private double probabilidadEstimada;
    private String nivelRiesgoActual;
    private String notificacionActual;

    private String nivelAlertaParaEscalar;
    private String destinatarioEscalado;

    @Given("una frecuencia cardiaca de {int} lpm")
    public void unaFrecuenciaCardiacaDeLpm(int fc) {
        this.frecuenciaCardiaca = fc;
    }

    @When("el monitor valida la frecuencia cardiaca")
    public void elMonitorValidaLaFrecuenciaCardiaca() {
        this.medicionAprobada = cardioVigia.validarFrecuenciaCardiaca(frecuenciaCardiaca);
    }

    @Then("la medicion es aceptada")
    public void laMedicionEsAceptada() {
        assertTrue(medicionAprobada);
    }

    @Then("la medicion es rechazada")
    public void laMedicionEsRechazada() {
        assertFalse(medicionAprobada);
    }

    @Given("un paciente de {int} anos con perfil completo")
    public void unPacienteDeAnosConPerfilCompleto(int edad) {
        this.edadPaciente = edad;
        this.perfilCompleto = true;
    }

    @When("se evalua la elegibilidad para IA")
    public void seEvaluaLaElegibilidadParaIA() {
        this.pacienteElegible = cardioVigia.esPacienteElegible(edadPaciente, perfilCompleto);
    }

    @Then("el paciente es elegible para el modelo")
    public void elPacienteEsElegibleParaElModelo() {
        assertTrue(pacienteElegible);
    }

    @Then("el paciente es excluido de la estimacion de IA")
    public void elPacienteEsExcluidoDeLaEstimacionDeIA() {
        assertFalse(pacienteElegible);
    }

    @Given("una estimacion de riesgo de complicacion del {double} por ciento")
    public void unaEstimacionDeRiesgo(double prob) {
        this.probabilidadEstimada = prob;
    }

    @When("se clasifica el nivel de riesgo")
    public void seClasificaElNivelDeRiesgo() {
        this.nivelRiesgoActual = cardioVigia.clasificarNivelRiesgo(probabilidadEstimada);
        this.notificacionActual = cardioVigia.determinarNotificacion(nivelRiesgoActual);
    }

    @Then("el nivel asignado es {string}")
    public void elNivelAsignadoEs(String nivelEsperado) {
        assertEquals(nivelEsperado, nivelRiesgoActual);
    }

    @And("la notificacion emitida es {string}")
    public void laNotificacionEmitidaEs(String notifEsperada) {
        assertEquals(notifEsperada, notificacionActual);
    }

    @Given("una alerta de nivel {string} no reconocida")
    public void unaAlertaDeNivelNoReconocida(String nivel) {
        this.nivelAlertaParaEscalar = nivel;
    }

    @When("transcurren {int} segundos sin respuesta")
    public void transcurrenSegundosSinRespuesta(int segundos) {
        this.destinatarioEscalado = cardioVigia.evaluarEscalamiento(nivelAlertaParaEscalar, segundos);
    }

    @Then("la alerta escala al {string}")
    public void laAlertaEscalaAl(String destinatarioEsperado) {
        assertEquals(destinatarioEsperado, destinatarioEscalado);
    }
}
