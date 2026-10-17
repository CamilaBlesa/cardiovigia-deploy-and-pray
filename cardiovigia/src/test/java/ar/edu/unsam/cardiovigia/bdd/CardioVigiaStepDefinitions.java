package ar.edu.unsam.cardiovigia.bdd;

import ar.edu.unsam.cardiovigia.model.Alerta;
import ar.edu.unsam.cardiovigia.model.NivelRiesgo;
import ar.edu.unsam.cardiovigia.model.SignoVital;
import ar.edu.unsam.cardiovigia.service.AlertaService;
import ar.edu.unsam.cardiovigia.service.RiesgoService;
import ar.edu.unsam.cardiovigia.service.SignoVitalValidator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CardioVigiaStepDefinitions {

    @Autowired
    private SignoVitalValidator validator;

    @Autowired
    private RiesgoService riesgoService;

    @Autowired
    private AlertaService alertaService;

    private SignoVital signos;
    private boolean validacionExitosa;
    private boolean canalConfiable;

    private String pacienteActualId;
    private Alerta alertaActual;

    @Given("una lectura con frecuencia cardíaca de {int} lpm y presión arterial media de {int} mmHg")
    public void unaLecturaConFcYPam(int fc, int pam) {
        signos = new SignoVital();
        signos.setFrecuenciaCardiaca(fc);
        signos.setPresionArterialMedia(pam);
    }

    @And("saturación de oxígeno del {int} por ciento y temperatura de {double} grados")
    public void saturacionYTemperatura(int sat, double temp) {
        signos.setSaturacionOxigeno(sat);
        signos.setTemperatura(temp);
    }

    @And("la señal no ha sufrido pérdidas de paquetes")
    public void senalSinPerdidas() {
        signos.setSegundosSinSenal(0);
    }

    @And("el canal ha estado sin señal durante {int} segundos")
    public void canalSinSenal(int seg) {
        signos.setSegundosSinSenal(seg);
    }

    @When("el validador de signos vitales procesa los datos")
    public void procesarSignosVitales() {
        validacionExitosa = validator.validarSignosVitales(signos);
        canalConfiable = validator.esCanalConfiable(signos.getSegundosSinSenal());
    }

    @Then("la lectura es aceptada como válida")
    public void lecturaAceptada() {
        assertTrue(validacionExitosa, "La lectura de signos vitales debería haber sido aceptada");
    }

    @Then("la lectura es rechazada por estar fuera de rango")
    public void lecturaRechazada() {
        assertFalse(validacionExitosa, "La lectura de signos vitales debería haber sido rechazada");
    }

    @And("el canal es calificado como confiable")
    public void canalEsConfiable() {
        assertTrue(canalConfiable);
    }

    @And("el canal es calificado como no confiable")
    public void canalNoEsConfiable() {
        assertFalse(canalConfiable);
    }

    @Given("un paciente {string} en monitoreo intraoperatorio")
    public void unPacienteEnMonitoreo(String pacienteId) {
        this.pacienteActualId = pacienteId;
    }

    @When("el motor de riesgo procesa una probabilidad de complicación del {double} por ciento")
    public void procesarRiesgo(double prob) {
        alertaActual = alertaService.procesarAlerta(pacienteActualId, prob, System.currentTimeMillis() / 1000);
    }

    @Then("el nivel de riesgo asignado es {string}")
    public void verificarNivelRiesgo(String nivelEsperado) {
        assertNotNull(alertaActual);
        assertEquals(NivelRiesgo.valueOf(nivelEsperado), alertaActual.getNivel());
    }

    @And("se emite una notificación de tipo {string}")
    public void verificarNotificacion(String notifEsperada) {
        assertEquals(notifEsperada, alertaActual.getNotificacion());
    }

    @And("el destinatario inicial es {string}")
    public void verificarDestinatario(String destEsperado) {
        assertEquals(destEsperado, alertaActual.getDestinatarioActual());
    }

    @And("se incluyen los tres factores determinantes de riesgo")
    public void verificarTresFactores() {
        assertNotNull(alertaActual.getFactoresPrincipales());
        assertEquals(3, alertaActual.getFactoresPrincipales().size());
    }

    @Given("que se generó una alerta de nivel {string} para el paciente {string}")
    public void alertaGenerada(String nivel, String pacienteId) {
        this.pacienteActualId = pacienteId;
        this.alertaActual = new Alerta("ALT-BDD", pacienteId, NivelRiesgo.valueOf(nivel), 85.0, null, "SONORA", "ANESTESIOLOGO", 0);
    }

    @And("la alerta no ha sido reconocida por el personal")
    public void alertaNoReconocida() {
        alertaActual.setReconocida(false);
    }

    @When("transcurren {int} segundos sin confirmación")
    public void transcurrenSegundos(int segundos) {
        alertaService.evaluarEscalamiento(alertaActual, segundos);
    }

    @Then("la alerta escala automáticamente")
    public void alertaEscala() {
        assertTrue(alertaActual.isEscalada(), "La alerta debería haberse escalado");
    }

    @And("el nuevo destinatario asignado es {string}")
    public void nuevoDestinatario(String dest) {
        assertEquals(dest, alertaActual.getDestinatarioActual());
    }
}
