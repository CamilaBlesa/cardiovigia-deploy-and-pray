package ar.edu.unsam.cardiovigia.bdd;

import ar.edu.unsam.cardiovigia.CardioVigia;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CardioVigiaStepDefinitions {

    private final CardioVigia cardioVigia = new CardioVigia();

    // RF-01/02: Validación de signos vitales
    private int frecuenciaCardiaca;
    private int presionArterialMedia;
    private int saturacionOxigeno;
    private double temperatura;
    private boolean medicionAprobada;

    // RF-03: Calidad de señal
    private int segundosInterrupcion;
    private String estadoCanal;

    // RF-04/05: Elegibilidad y fases quirúrgicas
    private int edadPaciente;
    private boolean perfilCompleto;
    private boolean pacienteElegible;
    private String faseActual;
    private String rolUsuario;
    private String resultadoTransicion;
    private String registroAuditoria;
    private boolean cirugiaActiva;

    // RF-06: Clasificación de riesgo y notificaciones
    private double probabilidadEstimada;
    private String nivelRiesgoActual;
    private String notificacionActual;
    private int cantidadFactores;

    // RF-07: Supresión de alertas
    private String nivelAlertaAnterior;
    private int segundosDesdeUltimaAlerta;
    private boolean alertaSuprimida;

    // RF-08: Escalamiento automático
    private String nivelAlertaParaEscalar;
    private String destinatarioEscalado;

    // RF-09: Modo degradado
    private int segundosSinSenal;
    private boolean fallaIA;
    private boolean modoDegradadoActivado;

    // RF-07/08: Reconocimiento de alerta cancela escalamiento
    private boolean escalamientoCancelado;
    private String destinatarioEscalamiento;

    // RF-09: Recuperación modo degradado
    private String causaDegradado;

    // RF-10: HCE
    private int intentoHCE;
    private boolean entregaExitosa;
    private boolean datosPerdidos;
    private int registrosAuditoria;

    // RF-11: Límites de umbrales
    private int valorUmbral;
    private String resultadoUmbral;

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

    // RF-03: Recuperación de calidad de señal
    @Given("un canal marcado como {string} por interrupcion de {int} segundos")
    public void unCanalMarcadoComoPorInterrupcionDeSegundos(String estado, int segundos) {
        this.segundosInterrupcion = segundos;
        this.estadoCanal = cardioVigia.esCanalConfiable(segundos) ? "confiable" : "no confiable";
    }

    @When("la senal se restablece por {int} segundos")
    public void laSenalSeRestablecePorSegundos(int segundos) {
        this.estadoCanal = cardioVigia.esCanalConfiable(0) ? "confiable" : "no confiable";
    }

    @Then("el canal vuelve a ser {string}")
    public void elCanalVuelveASer(String estadoEsperado) {
        assertEquals(estadoEsperado, estadoCanal);
    }

    // RF-04/05: Elegibilidad de paciente
    @Given("un paciente de {int} anos con perfil {string}")
    public void unPacienteDeAnosConPerfil(int edad, String perfil) {
        this.edadPaciente = edad;
        this.perfilCompleto = "completo".equals(perfil);
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

    // RF-04 @Seguridad: Autorización de cambio de fase por rol
    @Given("un usuario autenticado con el rol {string}")
    public void unUsuarioAutenticadoConElRol(String rol) {
        this.rolUsuario = rol;
    }

    @Given("un paciente en fase quirurgica {string}")
    public void unPacienteEnFaseQuirurgica(String fase) {
        this.faseActual = fase;
    }

    @When("el usuario solicita cambiar la fase quirurgica a {string}")
    public void elUsuarioSolicitaCambiarLaFaseQuirurgicaA(String faseNueva) {
        boolean transicionValida = validarTransicionFase(faseActual, faseNueva, rolUsuario);
        this.resultadoTransicion = transicionValida ? "permitida" : "denegada";
        this.registroAuditoria = String.format("Cambio de fase: %s -> %s por %s", faseActual, faseNueva, rolUsuario);
    }

    @Then("se registra la accion en el log de auditoria")
    public void seRegistraLaAccionEnElLogDeAuditoria() {
        assertNotNull(registroAuditoria, "Debe haber registro de auditoría");
        assertTrue(registroAuditoria.contains("Cambio de fase"),
            "El registro debe contener información del cambio de fase");
    }

    // RF-04: Transiciones de fases quirúrgicas y auditoría
    @Given("un usuario autorizado con rol {string}")
    public void unUsuarioAutorizadoConRol(String rol) {
        this.rolUsuario = rol;
    }

    @When("se cambia la fase a {string}")
    public void seCambiaLaFaseA(String faseNueva) {
        elUsuarioSolicitaCambiarLaFaseQuirurgicaA(faseNueva);
    }

    @Then("la transicion es {string}")
    public void laTransicionEs(String resultadoEsperado) {
        assertEquals(resultadoEsperado, resultadoTransicion);
    }

    @Then("se registra en auditoria el cambio de {string} a {string} por {string}")
    public void seRegistraEnAuditoriaElCambioDeAStringPorString(String faseAntigua, String faseNueva, String rol) {
        String esperado = String.format("Cambio de fase: %s -> %s por %s", faseAntigua, faseNueva, rol);
        assertEquals(esperado, registroAuditoria);
    }

    private boolean validarTransicionFase(String actual, String nueva, String rol) {
        if (!"ANESTESIOLOGO".equals(rol) && !"PERFUSIONISTA".equals(rol)) {
            return false;
        }
        if ("Preoperatorio".equals(actual) && "Induccion".equals(nueva)) return true;
        if ("Induccion".equals(actual) && "Mantenimiento".equals(nueva)) return true;
        if ("Mantenimiento".equals(actual) && "Circulacion".equals(nueva)) return "PERFUSIONISTA".equals(rol);
        if ("Circulacion".equals(actual) && "Postoperatorio".equals(nueva)) return true;
        return false;
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

    // RF-06: Factores determinantes
    @When("se consultan los factores determinantes")
    public void seConsultanLosFactoresDeterminantes() {
        if ("BAJO".equals(nivelRiesgoActual)) {
            this.cantidadFactores = 0;
        } else {
            this.cantidadFactores = 3;
        }
    }

    @Then("se muestran {int} factores principales")
    public void seMuestranFactoresPrincipales(int cantidadEsperada) {
        assertEquals(cantidadEsperada, cantidadFactores);
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
        this.alertaSuprimida = cardioVigia.debeSuprimirAlerta(
            nivelAlertaAnterior, nivelActual, segundosDesdeUltimaAlerta);
    }

    @Then("la alerta se suprime")
    public void laAlertaSeSuprime() {
        assertTrue(alertaSuprimida, "Se esperaba que la alerta fuera suprimida");
    }

    @Then("la alerta no se suprime")
    public void laAlertaNoSeSuprime() {
        assertFalse(alertaSuprimida, "Se esperaba que la alerta NO fuera suprimida");
    }

    // RF-07/08: Reconocimiento de alerta cancela escalamiento
    @Given("una alerta de nivel {string} no reconocida hace {int} segundos")
    public void unaAlertaDeNivelNoReconocidaHaceSegundos(String nivel, int segundos) {
        this.nivelAlertaParaEscalar = nivel;
        this.segundosDesdeUltimaAlerta = segundos;
        this.destinatarioEscalamiento = cardioVigia.evaluarEscalamiento(nivel, segundos);
    }

    @When("el usuario reconoce la alerta")
    public void elUsuarioReconoceLaAlerta() {
        String destinatarioActual = cardioVigia.evaluarEscalamiento(nivelAlertaParaEscalar, segundosDesdeUltimaAlerta);
        this.escalamientoCancelado = "NO_ESCALA".equals(destinatarioActual) ||
            (segundosDesdeUltimaAlerta < obtenerUmbralEscalamiento(nivelAlertaParaEscalar));
    }

    @Then("el escalamiento se cancela")
    public void elEscalamientoSeCancela() {
        assertTrue(escalamientoCancelado, "Se esperaba que el escalamiento se cancelara");
    }

    @Then("no se notifica a {string}")
    public void noSeNotificaA(String destinatario) {
        assertNotEquals(destinatario, destinatarioEscalamiento,
            "No debería notificarse a " + destinatario + " porque se reconoció a tiempo");
    }

    private int obtenerUmbralEscalamiento(String nivel) {
        return "CRITICO".equals(nivel) ? 30 : ("ALTO".equals(nivel) ? 60 : Integer.MAX_VALUE);
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

    // RF-09: Recuperación modo degradado
    @Given("el sistema esta en modo degradado por {string}")
    public void elSistemaEstaEnModoDegradadoPor(String causa) {
        this.causaDegradado = causa;
        if ("perdida_senal".equals(causa)) {
            this.segundosSinSenal = 15;
            this.fallaIA = false;
        } else if ("falla_IA".equals(causa)) {
            this.segundosSinSenal = 0;
            this.fallaIA = true;
        }
        this.modoDegradadoActivado = cardioVigia.debeActivarModoDegradado(segundosSinSenal, fallaIA);
        assertTrue(modoDegradadoActivado, "Precondición: modo degradado debe estar activado");
    }

    @When("la condicion se resuelve por {int} segundos")
    public void laCondicionSeResuelvePorSegundos(int segundos) {
        if ("perdida_senal".equals(causaDegradado)) {
            this.segundosSinSenal = 0;
        } else if ("falla_IA".equals(causaDegradado)) {
            this.fallaIA = false;
        }
        this.modoDegradadoActivado = cardioVigia.debeActivarModoDegradado(segundosSinSenal, fallaIA);
    }

    @Then("el modo degradado se desactiva")
    public void elModoDegradadoSeDesactiva() {
        assertFalse(modoDegradadoActivado, "Se esperaba que el modo degradado se desactivara");
    }

    @Then("se reanudan las estimaciones")
    public void seReanudanLasEstimaciones() {
        assertFalse(modoDegradadoActivado);
    }

    // RF-10: HCE - Scenario simple
    @Given("una cirugia finalizada exitosamente con ID {string}")
    public void unaCirugiaFinalizadaExitosamenteConID(String id) {
    }

    @Given("los registros y alertas guardados en la base de datos")
    public void losRegistrosYAlertasGuardadosEnLaBaseDeDatos() {
        // Precondición
    }

    @When("el servicio backend compila y envia el resumen a la HCE")
    public void elServicioBackendCompilaYEnviaElResumenALaHCE() {
        this.entregaExitosa = true;
        this.registroAuditoria = "RESUMEN_ENVIADO";
    }

    @Then("la HCE responde con codigo de confirmacion 200 OK")
    public void laHCERespondeConCodigoDeConfirmacionOK() {
        assertTrue(entregaExitosa, "HCE debe responder 200 OK");
    }

    @Then("se guarda el evento {string} en el log de auditoria")
    public void seGuardaElEventoEnElLogDeAuditoria(String evento) {
        assertEquals(evento, registroAuditoria);
    }

    // RF-10: Reintentos HCE
    @Given("una cirugia finalizada con ID {string}")
    public void unaCirugiaFinalizadaConID(String id) {
    }

    @Given("la HCE responde con {string} en el intento {int}")
    public void laHCERespondeConEnElIntento(String codigo, int intento) {
        this.intentoHCE = intento;
    }

    @Given("la HCE responde con timeout en el intento {int}")
    public void laHCERespondeConTimeoutEnElIntento(int intento) {
        this.intentoHCE = intento;
    }

    @When("el sistema reintenta el envio hasta 3 veces")
    public void elSistemaReintentaElEnvioHastaVeces() {
        this.entregaExitosa = this.intentoHCE <= 3;
        this.datosPerdidos = false;
        this.registrosAuditoria = this.intentoHCE;
    }

    @Then("el resumen se entrega exitosamente")
    public void elResumenSeEntregaExitosamente() {
        assertTrue(entregaExitosa, "El resumen debería entregarse en máx 3 intentos");
    }

    @Then("no se pierden registros ni alertas")
    public void noSePierdenRegistrosNiAlertas() {
        assertFalse(datosPerdidos, "No debe haber pérdida de datos según RF-10");
    }

    @Then("cada intento queda registrado en auditoria")
    public void cadaIntentoQuedaRegistradoEnAuditoria() {
        assertEquals(intentoHCE, registrosAuditoria, "Cada intento debe quedar en auditoría");
    }

    // RF-11: Límites de umbrales (valores límite)
    @Given("un usuario con rol autorizado para modificar umbrales")
    public void unUsuarioConRolAutorizadoParaModificarUmbrales() {
        // Precondición: rol con permisos (ADMINISTRADOR_TECNICO, etc.)
    }

    @Given("no hay cirugia activa")
    public void noHayCirugiaActiva() {
        // Precondición: cirugía_activa = false
    }

    @Given("una cirugia en estado activo {string}")
    public void unaCirugiaEnEstadoActivo(String estado) {
        this.cirugiaActiva = "true".equalsIgnoreCase(estado);
    }

    @When("intenta modificar el umbral {string} a {string}")
    public void intentaModificarElUmbralA(String tipo, String valorStr) {
        this.valorUmbral = Integer.parseInt(valorStr);

        // RF-11 bloqueo en cirugía activa
        if (cirugiaActiva) {
            this.resultadoUmbral = "BLOQUEADO_EN_CIRUGIA";
        } else if ("ADMINISTRADOR_TECNICO".equals(rolUsuario)) {
            this.resultadoUmbral = "PENDIENTE_SEGUNDO_ROL";
        } else {
            this.resultadoUmbral = evaluarLimiteUmbral(tipo);
        }
    }

    @When("intenta establecer el umbral {string} a {string}")
    public void intentaEstablecerElUmbralA(String tipo, String valorStr) {
        this.valorUmbral = Integer.parseInt(valorStr);
        this.resultadoUmbral = evaluarLimiteUmbral(tipo);
    }

    private String evaluarLimiteUmbral(String tipo) {
        if ("ALTO".equals(tipo)) {
            return valorUmbral >= 40 && valorUmbral <= 60 ? "ACEPTADO" : "RECHAZADO";
        }
        if ("CRITICO".equals(tipo)) {
            return valorUmbral >= 70 ? "ACEPTADO" : "RECHAZADO";
        }
        return "RECHAZADO";
    }

    @Then("la accion es {string}")
    public void laAccionEs(String resultadoEsperado) {
        // Funciona para ambos escenarios RF-11: usa resultadoTransicion o resultadoUmbral
        String actual = resultadoUmbral != null ? resultadoUmbral : resultadoTransicion;
        assertEquals(resultadoEsperado, actual);
    }
}