Feature: Monitoreo y Alertas Quirurgicas de CardioVigia

  Como equipo quirurgico en cirugia cardiovascular
  Quiero validar signos vitales y emitir alertas segun el riesgo
  Para prevenir complicaciones hemodinamicas durante la operacion


  @RF-01 @RF-02 @valores-limite
  Scenario Outline: Validar limites de frecuencia cardiaca
    Given una frecuencia cardiaca de <fc> lpm
    When el monitor valida la frecuencia cardiaca
    Then la medicion es <resultado>
    Examples:
      | fc   | resultado  |
      | 0    | aceptada   |
      | 75   | aceptada   |
      | 300  | aceptada   |
      | -1   | rechazada  |
      | 301  | rechazada  |
      | 320  | rechazada  |


  @RF-01 @RF-02 @valores-limite
  Scenario Outline: Validar limites de presion arterial media
    Given una presion arterial media de <pam> mmHg
    When el monitor valida la presion arterial media
    Then la medicion es <resultado>
    Examples:
      | pam  | resultado  |
      | 20   | aceptada   |
      | 100  | aceptada   |
      | 200  | aceptada   |
      | 19   | rechazada  |
      | 201  | rechazada  |


  @RF-01 @RF-02 @valores-limite
  Scenario Outline: Validar limites de saturacion de oxigeno
    Given una saturacion de oxigeno de <spo2> porciento
    When el monitor valida la saturacion de oxigeno
    Then la medicion es <resultado>
    Examples:
      | spo2 | resultado  |
      | 0    | aceptada   |
      | 99   | aceptada   |
      | 100  | aceptada   |
      | -1   | rechazada  |
      | 101  | rechazada  |


  @RF-01 @RF-02 @valores-limite
  Scenario Outline: Validar limites de temperatura corporal
    Given una temperatura corporal de <temp> grados Celsius
    When el monitor valida la temperatura
    Then la medicion es <resultado>
    Examples:
      | temp  | resultado  |
      | 15.0  | aceptada   |
      | 36.5  | aceptada   |
      | 43.0  | aceptada   |
      | 14.9  | rechazada  |
      | 43.1  | rechazada  |


  @RF-03 @valores-limite @transicion-estados
  Scenario Outline: Evaluar calidad de senal segun tiempo sin senal
    Given una interrupcion de senal de <segundos> segundos
    When se evalua la calidad del canal
    Then el canal es <estado>
    Examples:
      | segundos | estado        |
      | 0        | confiable     |
      | 15       | confiable     |
      | 16       | no confiable  |

  @RF-03 @transicion-estados
  Scenario Outline: Recuperar calidad de senal tras interrupcion temporal
    Given un canal marcado como "no confiable" por interrupcion de <segundos_iniciales> segundos
    When la senal se restablece por <segundos_recuperacion> segundos
    Then el canal vuelve a ser "confiable"
    Examples:
      | segundos_iniciales | segundos_recuperacion |
      | 16                 | 1                     |
      | 30                 | 1                     |
      | 60                 | 1                     |


  @RF-05 @valores-limite
  Scenario Outline: Evaluar elegibilidad de paciente para estimacion con IA
    Given un paciente de <edad> anos con perfil "<perfil>"
    When se evalua la elegibilidad para IA
    Then el paciente es "<resultado>" para el modelo
    Examples:
      | edad | perfil       | resultado |
      | 17   | completo     | excluido  |
      | 18   | completo     | elegible  |
      | 45   | completo     | elegible  |
      | 90   | completo     | elegible  |
      | 91   | completo     | excluido  |
      | 45   | incompleto   | excluido  |

    @RF-04 @Seguridad
    Scenario Outline: Validar autorizacion de cambio de fase quirurgica segun rol de usuario
      Given un usuario autenticado con el rol "<rol>"
      And un paciente en fase quirurgica "<fase_actual>"
      When el usuario solicita cambiar la fase quirurgica a "<fase_nueva>"
      Then la accion es "<resultado>"
      And se registra la accion en el log de auditoria
      Examples:
        | rol           | fase_actual   | fase_nueva  | resultado  |
        | ANESTESIOLOGO | Preoperatorio | Induccion   | permitida  |
        | PERFUSIONISTA | Mantenimiento | Circulacion | permitida  |
        | CIRUJANO      | Preoperatorio | Induccion   | denegada   |
        | ENFERMERO     | Mantenimiento | Circulacion | denegada   |

  @RF-04 @transicion-estados @auditoria
  Scenario Outline: Validar transiciones validas de fases quirurgicas y registro obligatorio
    Given un paciente en fase quirurgica "<fase_actual>"
    And un usuario autorizado con rol "<rol>"
    When se cambia la fase a "<fase_nueva>"
    Then la transicion es "<resultado>"
    And se registra en auditoria el cambio de "<fase_actual>" a "<fase_nueva>" por "<rol>"
    Examples:
      | fase_actual   | fase_nueva      | rol           | resultado  |
      | Preoperatorio | Induccion       | ANESTESIOLOGO | permitida  |
      | Induccion     | Mantenimiento   | ANESTESIOLOGO | permitida  |
      | Mantenimiento | Circulacion     | PERFUSIONISTA | permitida  |
      | Circulacion   | Postoperatorio  | ANESTESIOLOGO | permitida  |
      | Preoperatorio | Mantenimiento   | ANESTESIOLOGO | denegada   |
      | Induccion     | Circulacion     | ANESTESIOLOGO | denegada   |
      | Mantenimiento | Preoperatorio   | ANESTESIOLOGO | denegada   |
      | Postoperatorio| Induccion       | ANESTESIOLOGO | denegada   |


  @RF-06 @tabla-decisiones @valores-limite
  Scenario Outline: Clasificar nivel de riesgo segun probabilidad estimada
    Given una estimacion de riesgo de complicacion del <probabilidad> porciento
    When se clasifica el nivel de riesgo
    Then el nivel asignado es "<nivel>"
    Examples:
      | probabilidad | nivel     |
      | 15.0         | BAJO      |
      | 20.0         | MODERADO  |
      | 49.9         | MODERADO  |
      | 50.0         | ALTO      |
      | 79.9         | ALTO      |
      | 80.0         | CRITICO   |
      | 100.0        | CRITICO   |


  @RF-06 @RF-07 @tabla-decisiones
  Scenario Outline: Determinar notificacion segun nivel de riesgo
    Given un nivel de riesgo "<nivel>"
    When se determina la notificacion correspondiente
    Then la notificacion emitida es "<notificacion>"
    Examples:
      | nivel      | notificacion                              |
      | BAJO       | NINGUNA                                   |
      | MODERADO   | INDICACION_VISUAL                         |
      | ALTO       | ALERTA_ANESTESIOLOGO                      |
      | CRITICO    | ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO    |

  @RF-06 @tabla-decisiones
  Scenario Outline: Verificar factores determinantes mostrados desde nivel MODERADO
    Given un nivel de riesgo "<nivel>"
    When se consultan los factores determinantes
    Then se muestran <cantidad_factores> factores principales
    Examples:
      | nivel      | cantidad_factores |
      | BAJO       | 0                 |
      | MODERADO   | 3                 |
      | ALTO       | 3                 |
      | CRITICO    | 3                 |


  @RF-07 @tabla-decisiones @valores-limite
  Scenario Outline: Suprimir alertas repetidas para evitar fatiga de alarmas
    Given una alerta previa de nivel "<nivelAnterior>" hace <segundos> segundos
    When se evalua una nueva alerta de nivel "<nivelActual>"
    Then la alerta <suprimida>
    Examples:
      | nivelAnterior | nivelActual | segundos | suprimida       |
      | ALTO          | ALTO        | 60       | se suprime      |
      | ALTO          | ALTO        | 119      | se suprime      |
      | ALTO          | ALTO        | 120      | no se suprime   |
      | ALTO          | CRITICO     | 30       | no se suprime   |
      | MODERADO      | MODERADO    | 119      | se suprime      |
      | CRITICO       | CRITICO     | 119      | se suprime      |

  @RF-07 @RF-08 @transicion-estados
  Scenario Outline: Reconocer alerta cancela escalamiento automatico
    Given una alerta de nivel "<nivel>" no reconocida hace <segundos_previos> segundos
    When el usuario reconoce la alerta
    Then el escalamiento se cancela
    And no se notifica a "<destinatario_escalamiento>"
    Examples:
      | nivel    | segundos_previos | destinatario_escalamiento |
      | CRITICO  | 25               | COORDINADOR_UCI         |
      | CRITICO  | 29               | COORDINADOR_UCI         |
      | ALTO     | 55               | CIRUJANO                |
      | ALTO     | 59               | CIRUJANO                |


  @RF-08 @valores-limite @temporizador
  Scenario Outline: Escalamiento automatico de alertas no reconocidas
    Given una alerta de nivel "<nivel>" no reconocida
    When transcurren <segundos> segundos sin respuesta
    Then la alerta escala a "<destinatario>"
    Examples:
      | nivel    | segundos | destinatario           |
      | CRITICO  | 29       | NO_ESCALA              |
      | CRITICO  | 30       | COORDINADOR_UCI        |
      | CRITICO  | 31       | COORDINADOR_UCI        |
      | ALTO     | 59       | NO_ESCALA              |
      | ALTO     | 60       | CIRUJANO               |
      | ALTO     | 61       | CIRUJANO               |
      | MODERADO | 120      | NO_ESCALA              |
      | BAJO     | 300      | NO_ESCALA              |


  @RF-09 @valores-limite @transicion-estados
  Scenario Outline: Activar modo degradado por perdida de senal o falla IA
    Given una perdida de senal de <segundos> segundos y falla IA <fallaIA>
    When se evalua el modo de operacion
    Then el modo degradado <activado>
    Examples:
      | segundos | fallaIA | activado     |
      | 10       | "false"   | desactivado  |
      | 11       | "false"   | activado     |
      | 0        | "true"    | activado     |
      | 5        | "true"    | activado     |

  @RF-09 @transicion-estados
  Scenario Outline: Recuperar modo normal tras restauracion de senal o IA
    Given el sistema esta en modo degradado por "<causa>"
    When la condicion se resuelve por <segundos_recuperacion> segundos
    Then el modo degradado se desactiva
    And se reanudan las estimaciones
    Examples:
      | causa              | segundos_recuperacion |
      | perdida_senal      | 1                     |
      | falla_IA           | 1                     |


  @RF-10 @persistencia @reintentos @auditoria
  Scenario Outline: Reintentos de envio a HCE sin perdida de datos
    Given una cirugia finalizada con ID "<id_cirugia>"
    And la HCE responde con "<codigo_respuesta>" en el intento <intento>
    When el sistema reintenta el envio hasta 3 veces
    Then el resumen se entrega exitosamente
    And no se pierden registros ni alertas
    And cada intento queda registrado en auditoria
    Examples:
      | id_cirugia | codigo_respuesta | intento |
      | CIR-001    | 503              | 1       |
      | CIR-002    | 504              | 2       |
      | CIR-003    | timeout          | 3       |


  @RF-11 @bloqueo-cirugia
  Scenario Outline: Validar gobierno de umbrales y bloqueo de seguridad en cirugias activas
    Given un usuario autenticado con el rol "<rol>"
    And una cirugia en estado activo "<cirugia_activa>"
    When intenta modificar el umbral "<tipo_umbral>" a "<valor>"
    Then la accion es "<resultado>"
    Examples:
      | rol                  | cirugia_activa | tipo_umbral | valor | resultado             |
      | ANESTESIOLOGO        | true           | ALTO        | 50    | BLOQUEADO_EN_CIRUGIA  |
      | ADMINISTRADOR_TECNICO| true           | CRITICO     | 70    | BLOQUEADO_EN_CIRUGIA  |
      | ADMINISTRADOR_TECNICO| false          | ALTO        | 50    | PENDIENTE_SEGUNDO_ROL |

  @RF-11 @valores-limite
  Scenario Outline: Validar limites de umbral ALTO (40-60%) y CRITICO (>=70%)
    Given un usuario con rol autorizado para modificar umbrales
    And no hay cirugia activa
    When intenta establecer el umbral "<tipo_umbral>" a "<valor>"
    Then la accion es "<resultado>"
    Examples:
      | tipo_umbral | valor | resultado |
      | ALTO        | 39    | RECHAZADO |
      | ALTO        | 40    | ACEPTADO  |
      | ALTO        | 60    | ACEPTADO  |
      | ALTO        | 61    | RECHAZADO |
      | CRITICO     | 69    | RECHAZADO |
      | CRITICO     | 70    | ACEPTADO  |
      | CRITICO     | 71    | ACEPTADO  |