Feature: Gestión, Emisión y Escalamiento Automático de Alertas (RF-06, RF-07, RF-08)

  Como equipo quirúrgico del Instituto Cardiovascular del Plata
  Queremos recibir alertas priorizadas según el riesgo y que escalen oportunamente
  Para actuar de forma inmediata ante descompensaciones hemodinámicas.

  Scenario: Emitir alerta crítica sonora simultánea ante riesgo mayor al 80%
    Given un paciente "PAC-01" en monitoreo intraoperatorio
    When el motor de riesgo procesa una probabilidad de complicación del 85 por ciento
    Then el nivel de riesgo asignado es "CRITICO"
    And se emite una notificación de tipo "ALERTA_SONORA_ANESTESIOLOGO_Y_CIRUJANO"
    And el destinatario inicial es "ANESTESIOLOGO_Y_CIRUJANO"
    And se incluyen los tres factores determinantes de riesgo

  Scenario: Escalamiento automático de alerta crítica no reconocida a los 30 segundos
    Given que se generó una alerta de nivel "CRITICO" para el paciente "PAC-01"
    And la alerta no ha sido reconocida por el personal
    When transcurren 30 segundos sin confirmación
    Then la alerta escala automáticamente
    And el nuevo destinatario asignado es "COORDINADOR_UCI"
