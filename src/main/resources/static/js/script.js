function validar() {
    var fcInput = document.getElementById('fc').value.trim();
    var pamInput = document.getElementById('pam').value.trim();
    var fc = Number(fcInput);
    var pam = Number(pamInput);
    var div = document.getElementById('resultadoValidacion');
    var valoresValidos = fcInput !== "" && pamInput !== ""
        && Number.isInteger(fc) && Number.isInteger(pam)
        && fc >= 0 && fc <= 300 && pam >= 20 && pam <= 200;

    if (valoresValidos) {
        div.className = "badge ok";
        div.innerText = "Signos Válidos (Canal Confiable)";
    } else {
        div.className = "badge danger";
        div.innerText = "Signos Inválidos (Descarte por Ruido)";
    }
}

function evaluar() {
    var probabilityInput = document.getElementById('probabilidad').value.trim();
    var p = Number(probabilityInput);
    var div = document.getElementById('resultadoRiesgo');

    if (probabilityInput === "" || !Number.isFinite(p) || p < 0 || p > 100) {
        div.className = "badge danger";
        div.innerText = "Probabilidad Inválida (debe estar entre 0 y 100)";
    } else if (p >= 80) {
        div.className = "badge danger";
        div.innerText = "Nivel de Riesgo: CRITICO - Alerta Sonora Dual Emitida";
    } else if (p >= 50) {
        div.className = "badge danger";
        div.innerText = "Nivel de Riesgo: ALTO - Alerta Anestesiólogo";
    } else if (p >= 20) {
        div.className = "badge ok";
        div.innerText = "Nivel de Riesgo: MODERADO - Indicación Visual";
    } else {
        div.className = "badge ok";
        div.innerText = "Nivel de Riesgo: BAJO - Monitoreo Normal";
    }
}
