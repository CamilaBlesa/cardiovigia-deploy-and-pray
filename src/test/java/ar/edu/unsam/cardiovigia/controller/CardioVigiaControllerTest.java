package ar.edu.unsam.cardiovigia.controller;

import ar.edu.unsam.cardiovigia.CardioVigia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de contrato HTTP del controller.
 *
 * Cada método identifica su caso de prueba CT y requisito RF en el comentario
 * asociado. MockMvc permite validar rutas, códigos HTTP y cuerpos JSON sin
 * iniciar un servidor externo; CardioVigia se reemplaza por un mock para
 * aislar el módulo web de la lógica de dominio.
 */
@ExtendWith(MockitoExtension.class)
class CardioVigiaControllerTest {

    @Mock
    private CardioVigia cardioVigia;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CardioVigiaController(cardioVigia))
                .build();
    }

    @Test
    // RF-00 Caso de prueba: 01. Verifica que el health check publique el estado operativo.
    void obtieneEstadoSalud() throws Exception {
        mockMvc.perform(get("/api/salud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sistema").value("CardioVigia"))
                .andExpect(jsonPath("$.estado").value("OPERATIVO"));
    }

    @Test
    // RF-01/RF-02 Caso de prueba: 01. Acepta signos vitales y canal confiable.
    void validaSignosVitalesValidos() throws Exception {
        when(cardioVigia.validarFrecuenciaCardiaca(75)).thenReturn(true);
        when(cardioVigia.validarPresionArterialMedia(85)).thenReturn(true);
        when(cardioVigia.validarSaturacionOxigeno(98)).thenReturn(true);
        when(cardioVigia.validarTemperatura(36.5)).thenReturn(true);
        when(cardioVigia.esCanalConfiable(0)).thenReturn(true);

        mockMvc.perform(post("/api/signos-vitales/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": "P-001",
                                  "frecuenciaCardiaca": 75,
                                  "presionArterialMedia": 85,
                                  "saturacionOxigeno": 98,
                                  "temperatura": 36.5,
                                  "segundosSinSenal": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todoValido").value(true));
    }

    @Test
    // RF-01/RF-02 Caso de prueba: 02. Rechaza signos vitales cuando falla una validación.
    void rechazaSignosVitalesInvalidos() throws Exception {
        when(cardioVigia.validarFrecuenciaCardiaca(301)).thenReturn(false);
        when(cardioVigia.validarPresionArterialMedia(85)).thenReturn(true);
        when(cardioVigia.validarSaturacionOxigeno(98)).thenReturn(true);
        when(cardioVigia.validarTemperatura(36.5)).thenReturn(true);
        when(cardioVigia.esCanalConfiable(0)).thenReturn(true);

        mockMvc.perform(post("/api/signos-vitales/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": "P-001",
                                  "frecuenciaCardiaca": 301,
                                  "presionArterialMedia": 85,
                                  "saturacionOxigeno": 98,
                                  "temperatura": 36.5,
                                  "segundosSinSenal": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.todoValido").value(false))
                .andExpect(jsonPath("$.frecuenciaCardiacaValida").value(false));
    }

    @Test
    // RF-01/RF-02 Caso de prueba: 03. Cubre valores válidos en los extremos definidos.
    void aceptaSignosVitalesEnLimitesInferiorYSuperior() throws Exception {
        when(cardioVigia.validarFrecuenciaCardiaca(0)).thenReturn(true);
        when(cardioVigia.validarPresionArterialMedia(20)).thenReturn(true);
        when(cardioVigia.validarSaturacionOxigeno(100)).thenReturn(true);
        when(cardioVigia.validarTemperatura(43.0)).thenReturn(true);
        when(cardioVigia.esCanalConfiable(15)).thenReturn(true);

        mockMvc.perform(post("/api/signos-vitales/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": "P-001",
                                  "frecuenciaCardiaca": 0,
                                  "presionArterialMedia": 20,
                                  "saturacionOxigeno": 100,
                                  "temperatura": 43.0,
                                  "segundosSinSenal": 15
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todoValido").value(true));
    }

    @Test
    // RF-01/RF-02 Caso de prueba: 04. Cubre valores inmediatamente fuera de los límites.
    void rechazaSignosVitalesApenasFueraDeLosLimites() throws Exception {
        when(cardioVigia.validarFrecuenciaCardiaca(-1)).thenReturn(false);
        when(cardioVigia.validarPresionArterialMedia(201)).thenReturn(false);
        when(cardioVigia.validarSaturacionOxigeno(101)).thenReturn(false);
        when(cardioVigia.validarTemperatura(43.1)).thenReturn(false);
        when(cardioVigia.esCanalConfiable(16)).thenReturn(false);

        mockMvc.perform(post("/api/signos-vitales/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": "P-001",
                                  "frecuenciaCardiaca": -1,
                                  "presionArterialMedia": 201,
                                  "saturacionOxigeno": 101,
                                  "temperatura": 43.1,
                                  "segundosSinSenal": 16
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.todoValido").value(false))
                .andExpect(jsonPath("$.canalConfiable").value(false));
    }

    @Test
    // RF-04 Caso de prueba: 01. Rechaza cambio de fase con rol no autorizado.
    void rechazaCambioDeFasePorRolNoAutorizado() throws Exception {
        mockMvc.perform(put("/api/pacientes/P-001/fase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"faseNueva": "INDUCCION", "rol": "CIRUJANO"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ROL_NO_AUTORIZADO"));
    }

    @Test
    // RF-04 Caso de prueba: 02. Rechaza una transición de fase inexistente.
    void rechazaCambioDeFaseInvalido() throws Exception {
        mockMvc.perform(put("/api/pacientes/P-001/fase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"faseNueva": "FASE_INVALIDA", "rol": "ANESTESIOLOGO"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("TRANSICION_INVALIDA"));
    }

    @Test
    // RF-04 Caso de prueba: 03. Acepta transición válida con rol autorizado.
    void actualizaFaseConRolAutorizado() throws Exception {
        mockMvc.perform(put("/api/pacientes/P-001/fase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"faseNueva": "MANTENIMIENTO", "rol": "PERFUSIONISTA"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.codigo").value("FASE_ACTUALIZADA"));
    }

    @Test
    // RF-03 Caso de prueba: 01. Devuelve el estado del canal solicitado.
    void obtieneCalidadDeSenal() throws Exception {
        mockMvc.perform(get("/api/calidad-senal/CANAL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canalId").value("CANAL-01"))
                .andExpect(jsonPath("$.estado").value("CONFIABLE"));
    }

    @Test
    // RF-04 Caso de prueba: 04. Registra un paciente y permite consultarlo.
    void registraYObtienePaciente() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId": "P-001", "edad": 54, "perfilCompleto": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteId").value("P-001"))
                .andExpect(jsonPath("$.estado").value("REGISTRADO"));

        mockMvc.perform(get("/api/pacientes/P-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacienteId").value("P-001"))
                .andExpect(jsonPath("$.estado").value("ENCONTRADO"));
    }

    @Test
    // RF-05 Caso de prueba: 01. Devuelve elegibilidad para edad y perfil válidos.
    void evaluaElegibilidadDelPaciente() throws Exception {
        when(cardioVigia.esPacienteElegible(18, true)).thenReturn(true);

        mockMvc.perform(post("/api/pacientes/elegibilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId": "P-001", "edad": 18, "perfilCompleto": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ELEGIBLE"))
                .andExpect(jsonPath("$.elegible").value(true));
    }

    @Test
    // RF-05 Caso de prueba: 02. Devuelve exclusión para datos no elegibles.
    void excluyePacienteNoElegible() throws Exception {
        when(cardioVigia.esPacienteElegible(17, false)).thenReturn(false);

        mockMvc.perform(post("/api/pacientes/elegibilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId": "P-002", "edad": 17, "perfilCompleto": false}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value("EXCLUIDO"))
                .andExpect(jsonPath("$.elegible").value(false));
    }

    @Test
    // RF-06/RF-07 Caso de prueba: 01. Transforma una probabilidad inválida en error HTTP.
    void evaluaAlertaInvalida() throws Exception {
        when(cardioVigia.clasificarNivelRiesgo(anyDouble()))
                .thenThrow(new IllegalArgumentException("probabilidad inválida"));

        mockMvc.perform(post("/api/alertas/evaluar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"probabilidad": 101}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nivel").value("ERROR"))
                .andExpect(jsonPath("$.notificacion").value("PROBABILIDAD_INVALIDA"));
    }

    @Test
    // RF-06/RF-07 Caso de prueba: 02. Verifica clasificación y notificación de riesgo.
    void clasificaProbabilidadesEnLimitesDeRiesgo() throws Exception {
        when(cardioVigia.clasificarNivelRiesgo(20.0)).thenReturn("MODERADO");
        when(cardioVigia.determinarNotificacion("MODERADO")).thenReturn("INDICACION_VISUAL");

        mockMvc.perform(post("/api/alertas/evaluar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"probabilidad": 20.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value("MODERADO"))
                .andExpect(jsonPath("$.notificacion").value("INDICACION_VISUAL"));
    }

    @Test
    // RF-06 Caso de prueba: 03. Rechaza probabilidad fuera del rango permitido.
    void rechazaProbabilidadEnLimitesInvalidos() throws Exception {
        when(cardioVigia.clasificarNivelRiesgo(anyDouble()))
                .thenThrow(new IllegalArgumentException("probabilidad inválida"));

        mockMvc.perform(post("/api/alertas/evaluar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"probabilidad": -0.01}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nivel").value("ERROR"));
    }

    @Test
    // RF-08 Caso de prueba: 01. Verifica destinos de escalamiento crítico y alto.
    void validaLimitesDeEscalamiento() throws Exception {
        when(cardioVigia.evaluarEscalamiento("CRITICO", 30)).thenReturn("COORDINADOR_UCI");
        when(cardioVigia.evaluarEscalamiento("ALTO", 60)).thenReturn("CIRUJANO");

        mockMvc.perform(post("/api/alertas/escalamiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivel": "CRITICO", "segundosSinReconocer": 30}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinatario").value("COORDINADOR_UCI"));

        mockMvc.perform(post("/api/alertas/escalamiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivel": "ALTO", "segundosSinReconocer": 60}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinatario").value("CIRUJANO"));
    }

    @Test
    // RF-09 Caso de prueba: 01. Activa modo degradado ante pérdida de señal y falla de IA.
    void evaluaModoDegradado() throws Exception {
        when(cardioVigia.debeActivarModoDegradado(16, true)).thenReturn(true);

        mockMvc.perform(post("/api/modo-degradado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"segundosSinSenal": 16, "fallaIA": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modoDegradadoActivo").value(true))
                .andExpect(jsonPath("$.fallaIA").value(true));
    }

    @Test
    // RF-10 Caso de prueba: 01. Verifica recepción del resumen y estado de auditoría HCE.
    void enviaResumenYConsultaEstadoHCE() throws Exception {
        mockMvc.perform(post("/api/hce/resumen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cirugiaId": "C-001",
                                  "pacienteId": "P-001",
                                  "resumenClinico": {"estado": "estable"}
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cirugiaId").value("C-001"))
                .andExpect(jsonPath("$.estadoEnvio").value("RECIBIDO_OK"))
                .andExpect(jsonPath("$.auditado").value(true));

        mockMvc.perform(get("/api/hce/estado/C-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cirugiaId").value("C-001"))
                .andExpect(jsonPath("$.auditado").value(true))
                .andExpect(jsonPath("$.estadoEnvio").value("RECIBIDO_OK"));
    }

    @Test
    // RF-11 Caso de prueba: 01. Bloquea actualización de umbrales durante cirugía.
    void bloqueaUmbralesDuranteCirugia() throws Exception {
        mockMvc.perform(put("/api/config/umbrales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "umbralAlto": 50,
                                  "umbralCritico": 80,
                                  "cirugiaActiva": true
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultado").value("BLOQUEADO_EN_CIRUGIA"));
    }

    @Test
    // RF-11 Caso de prueba: 02. Acepta umbrales válidos fuera de cirugía.
    void aceptaUmbralesValidos() throws Exception {
        mockMvc.perform(put("/api/config/umbrales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "umbralAlto": 50,
                                  "umbralCritico": 80,
                                  "cirugiaActiva": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualizado").value(true))
                .andExpect(jsonPath("$.resultado").value("ACEPTADO"));
    }

    @Test
    // RF-11 Caso de prueba: 03. Rechaza umbrales apenas fuera de los límites.
    void rechazaUmbralesApenasFueraDeLosLimites() throws Exception {
        mockMvc.perform(put("/api/config/umbrales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "umbralAlto": 39.99,
                                  "umbralCritico": 69.99,
                                  "cirugiaActiva": false
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.actualizado").value(false))
                .andExpect(jsonPath("$.resultado").value("RECHAZADO"));
    }
}
