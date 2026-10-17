Feature: Validación de Signos Vitales y Calidad de Señal en Quirófano (RF-01, RF-02, RF-03)

  Como anestesiólogo en quirófano cardiovascular
  Quiero que el sistema descarte mediciones de signos vitales fuera de escala o con señales no confiables
  Para evitar diagnósticos erróneos y falsas alarmas durante la intervención quirúrgica.

  Scenario: Aceptar signos vitales normales y señal estable
    Given una lectura con frecuencia cardíaca de 75 lpm y presión arterial media de 90 mmHg
    And saturación de oxígeno del 99 por ciento y temperatura de 36.5 grados
    And la señal no ha sufrido pérdidas de paquetes
    When el validador de signos vitales procesa los datos
    Then la lectura es aceptada como válida
    And el canal es calificado como confiable

  Scenario: Descartar frecuencia cardíaca fuera de rango fisiológico por electrobisturí
    Given una lectura con frecuencia cardíaca de 320 lpm y presión arterial media de 85 mmHg
    And saturación de oxígeno del 98 por ciento y temperatura de 36.8 grados
    And la señal no ha sufrido pérdidas de paquetes
    When el validador de signos vitales procesa los datos
    Then la lectura es rechazada por estar fuera de rango
    And el canal es calificado como confiable

  Scenario: Marcar canal como no confiable ante pérdida de señal prolongada
    Given una lectura con frecuencia cardíaca de 70 lpm y presión arterial media de 80 mmHg
    And saturación de oxígeno del 98 por ciento y temperatura de 36.5 grados
    And el canal ha estado sin señal durante 18 segundos
    When el validador de signos vitales procesa los datos
    Then la lectura es rechazada por estar fuera de rango
    And el canal es calificado como no confiable
