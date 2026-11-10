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


  @RF-04 @RF-05 @valores-limite
  Scenario Outline: Evaluar elegibilidad de paciente para estimacion con IA
    Given un paciente de <edad> anos con perfil <perfil>
    When se evalua la elegibilidad para IA
    Then el paciente es <resultado> para el modelo
    Examples:
      | edad | perfil       | resultado |
      | 17   | completo     | excluido  |
      | 18   | completo     | elegible  |
      | 45   | completo     | elegible  |
      | 90   | completo     | elegible  |
      | 91   | completo     | excluido  |
      | 45   | incompleto   | excluido  |


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
      | 10       | false   | desactivado  |
      | 11       | false   | activado     |
      | 0        | true    | activado     |
      | 5        | true    | activado     |