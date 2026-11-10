package ar.edu.unsam.cardiovigia.bdd;

import ar.edu.unsam.cardiovigia.CardioVigia;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CardioVigiaStepDefinitions {

    private final CardioVigia cardioVigia = new CardioVigia();

    private int frecuenciaCardiaca;
    private int presionArterialMedia;
    private int saturacionOxigeno;
    private double temperatura;
    private boolean medicionAprobada;

    private int edadPaciente;
    private boolean perfilCompleto;
    private boolean pacienteElegible;

    private double probabilidadEstimada;
    private String nivelRiesgoActual;
    private String notificacionActual;

    private String nivelAlertaParaEscalar;
    private String destinatarioEscalado;

    private String nivelAlertaAnterior;
    private String nivelAlertaActual;
    private int segundosDesdeUltimaAlerta;
    private boolean alertaSuprimida;

    private int segundosSinSenal;
    private boolean fallaIA;
    private boolean modoDegradadoActivado;

    private int segundosInterrupcion;
    private String estadoCanal;
    
    // RF-01/02: Validación de signos vitales (Frecuencia Cardíaca)
    @Given("una frecuencia cardiaca de {int} lpm")
    public void unaFrecuenciaCardiacaDeLpm(int fc) {
        this.frecuenciaCardiaca = fc;
    }

    @When("el monitor valida la frecuencia cardiaca")
    public void elMonitorValidaLaFrecuenciaCardiaca() {
        this.medicionAprobada = cardioVigia.validarFrecuenciaCardiaca(frecuenciaCardiaca);
    }

    // RF-01/02: Validación de signos vitales (Presión Arterial Media)
    @Given("una presion arterial media de {int} mmHg")
    public void unaPresionArterialMediaDeMmHg(int pam) {
        this.presionArterialMedia = pam;
    }

    @When("el monitor valida la presion arterial media")
    public void elMonitorValidaLaPresionArterialMedia() {
        this.medicionAprobada = cardioVigia.validarPresionArterialMedia(presionArterialMedia);
    }

    // RF-01/02: Validación de signos vitales (Saturación Oxígeno)
    @Given("una saturacion de oxigeno de {int} porciento")
    public void unaSaturacionDeOxigenoDePorCiento(int spo2) {
        this.saturacionOxigeno = spo2;
    }

    @When("el monitor valida la saturacion de oxigeno")
    public void elMonitorValidaLaSaturacionDeOxigeno() {
        this.medicionAprobada = cardioVigia.validarSaturacionOxigeno(saturacionOxigeno);
    }

    // RF-01/02: Validación de signos vitales (Temperatura)
    @Given("una temperatura corporal de {double} grados Celsius")
    public void unaTemperaturaCorporalDeGradosCelsius(double temp) {
        this.temperatura = temp;
    }

    @When("el monitor valida la temperatura")
    public void elMonitorValidaLaTemperatura() {
        this.medicionAprobada = cardioVigia.validarTemperatura(temperatura);
    }

    // RF-01/02: Resultado común para validaciones
    @Then("la medicion es aceptada")
    public void laMedicionEsAceptada() {
        assertTrue(medicionAprobada, "Se esperaba que la medición fuera aceptada");
    }

    @Then("la medicion es rechazada")
    public void laMedicionEsRechazada() {
        assertFalse(medicionAprobada, "Se esperaba que la medición fuera rechazada");
    }

    // RF-03: Calidad de señal
    @Given("una interrupcion de senal de {int} segundos")
    public void unaInterrupcionDeSenalDeSegundos(int segundos) {
        this.segundosInterrupcion = segundos;
    }

    @When("se evalua la calidad del canal")
    public void seEvaluaLaCalidadDelCanal() {
        this.estadoCanal = cardioVigia.esCanalConfiable(segundosInterrupcion) ? "confiable" : "no confiable";
    }

    @Then("el canal es confiable")
    public void elCanalEsConfiable() {
        assertEquals("confiable", estadoCanal);
    }

    @Then("el canal es no confiable")
    public void elCanalEsNoConfiable() {
        assertEquals("no confiable", estadoCanal);
    }

    // RF-04/05: Elegibilidad de paciente
    @Given("un paciente de {int} anos con perfil {string}")
    public void unPacienteDeAnosConPerfil(int edad, String perfil) {
        this.edadPaciente = edad;
        this.perfilCompleto = "completo".equals(perfil);
    }

    @Given("un paciente de {int} anos con perfil completo")
    public void unPacienteDeAnosConPerfilCompleto(int edad) {
        this.edadPaciente = edad;
        this.perfilCompleto = true;
    }

    @Given("un paciente de {int} anos con perfil incompleto")
    public void unPacienteDeAnosConPerfilIncompleto(int edad) {
        this.edadPaciente = edad;
        this.perfilCompleto = false;
    }

    @When("se evalua la elegibilidad para IA")
    public void seEvaluaLaElegibilidadParaIA() {
        this.pacienteElegible = cardioVigia.esPacienteElegible(edadPaciente, perfilCompleto);
    }

    @Then("el paciente es {string} para el modelo")
    public void elPacienteEsParaElModelo(String resultadoEsperado) {
        boolean esperado = "elegible".equals(resultadoEsperado);
        assertEquals(esperado, pacienteElegible);
    }

    @Then("el paciente es elegible para el modelo")
    public void elPacienteEsElegibleParaElModelo() {
        assertTrue(pacienteElegible);
    }

    @Then("el paciente es excluido para el modelo")
    public void elPacienteEsExcluidoParaElModelo() {
        assertFalse(pacienteElegible);
    }

    // RF-06: Clasificación de nivel de riesgo
    @Given("una estimacion de riesgo de complicacion del {double} porciento")
    public void unaEstimacionDeRiesgoDeComplicacionDelPorciento(double prob) {
        this.probabilidadEstimada = prob;
    }

    @When("se clasifica el nivel de riesgo")
    public void seClasificaElNivelDeRiesgo() {
        this.nivelRiesgoActual = cardioVigia.clasificarNivelRiesgo(probabilidadEstimada);
    }

    @Then("el nivel asignado es {string}")
    public void elNivelAsignadoEs(String nivelEsperado) {
        assertEquals(nivelEsperado, nivelRiesgoActual);
    }
    // RF-07: Determinar notificación según nivel de riesgo
    @Given("un nivel de riesgo {string}")
    public void unNivelDeRiesgo(String nivel) {
        this.nivelRiesgoActual = nivel;
    }

    @When("se determina la notificacion correspondiente")
    public void seDeterminaLaNotificacionCorrespondiente() {
        this.notificacionActual = cardioVigia.determinarNotificacion(nivelRiesgoActual);
    }

    @Then("la notificacion emitida es {string}")
    public void laNotificacionEmitidaEs(String notificacionEsperada) {
        assertEquals(notificacionEsperada, notificacionActual);
    }

    // RF-07: Supresión de alertas (fatiga de alarmas)
    @Given("una alerta previa de nivel {string} hace {int} segundos")
    public void unaAlertaPreviaDeNivelHaceSegundos(String nivelAnterior, int segundos) {
        this.nivelAlertaAnterior = nivelAnterior;
        this.segundosDesdeUltimaAlerta = segundos;
    }

    @When("se evalua una nueva alerta de nivel {string}")
    public void seEvaluaUnaNuevaAlertaDeNivel(String nivelActual) {
        this.nivelAlertaActual = nivelActual;
        this.alertaSuprimida = cardioVigia.debeSuprimirAlerta(nivelAlertaAnterior, nivelAlertaActual, segundosDesdeUltimaAlerta);
    }

    @Then("la alerta se suprime")
    public void laAlertaSeSuprime() {
        assertTrue(alertaSuprimida, "Se esperaba que la alerta fuera suprimida");
    }

    @Then("la alerta no se suprime")
    public void laAlertaNoSeSuprime() {
        assertFalse(alertaSuprimida, "Se esperaba que la alerta NO fuera suprimida");
    }

    // RF-08: Escalamiento automático
    @Given("una alerta de nivel {string} no reconocida")
    public void unaAlertaDeNivelNoReconocida(String nivel) {
        this.nivelAlertaParaEscalar = nivel;
    }

    @When("transcurren {int} segundos sin respuesta")
    public void transcurrenSegundosSinRespuesta(int segundos) {
        this.destinatarioEscalado = cardioVigia.evaluarEscalamiento(nivelAlertaParaEscalar, segundos);
    }

    @Then("la alerta escala a {string}")
    public void laAlertaEscalaA(String destinatarioEsperado) {
        assertEquals(destinatarioEsperado, destinatarioEscalado);
    }
    // RF-09: Modo degradado
    @Given("una perdida de senal de {int} segundos y falla IA {string}")
    public void unaPerdidaDeSenalDeSegundosYFallaIA(int segundos, String fallaIA) {
        this.segundosSinSenal = segundos;
        this.fallaIA = "true".equals(fallaIA);
    }

    @Given("una perdida de senal de {int} segundos y falla IA true")
    public void unaPerdidaDeSenalDeSegundosYFallaIATrue(int segundos) {
        this.segundosSinSenal = segundos;
        this.fallaIA = true;
    }

    @Given("una perdida de senal de {int} segundos y falla IA false")
    public void unaPerdidaDeSenalDeSegundosYFallaIAFalse(int segundos) {
        this.segundosSinSenal = segundos;
        this.fallaIA = false;
    }

    @When("se evalua el modo de operacion")
    public void seEvaluaElModoDeOperacion() {
        this.modoDegradadoActivado = cardioVigia.debeActivarModoDegradado(segundosSinSenal, fallaIA);
    }

    @Then("el modo degradado activado")
    public void elModoDegradadoActivado() {
        assertTrue(modoDegradadoActivado, "Se esperaba que el modo degradado estuviera activado");
    }

    @Then("el modo degradado desactivado")
    public void elModoDegradadoDesactivado() {
        assertFalse(modoDegradadoActivado, "Se esperaba que el modo degradado NO estuviera activado");
    }
}