Feature: Monitoreo y Alertas Quirurgicas de CardioVigia

  Como equipo quirurgico en cirugia cardiovascular
  Quiero validar signos vitales y emitir alertas segun el riesgo
  Para prevenir complicaciones hemodinamicas durante la operacion


  Scenario: Aceptar signos vitales dentro del rango fisiologico

    Given una frecuencia cardiaca de 75 lpm
    When el monitor valida la frecuencia cardiaca
    Then la medicion es aceptada


  Scenario: Rechazar frecuencia cardiaca fuera de escala por ruido de electrobisturi

    Given una frecuencia cardiaca de 320 lpm
    When el monitor valida la frecuencia cardiaca
    Then la medicion es rechazada


  Scenario: Evaluar elegibilidad de paciente adulto para estimacion con IA

    Given un paciente de 45 anos con perfil completo
    When se evalua la elegibilidad para IA
    Then el paciente es elegible para el modelo


  Scenario: Excluir paciente menor de edad del modelo de IA

    Given un paciente de 17 anos con perfil completo
    When se evalua la elegibilidad para IA
    Then el paciente es excluido de la estimacion de IA


  Scenario: Emitir alerta sonora simultanea ante riesgo critico

    Given una estimacion de riesgo de complicacion del 85 por ciento
    When se clasifica el nivel de riesgo
    Then el nivel asignado es "CRITICO"
    And la notificacion emitida es "ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO"


  Scenario: Escalamiento automatico al coordinador a los 30 segundos

    Given una alerta de nivel "CRITICO" no reconocida
    When transcurren 30 segundos sin respuesta
    Then la alerta escala al "COORDINADOR_UCI"
