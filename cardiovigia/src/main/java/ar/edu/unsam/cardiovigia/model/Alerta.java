package ar.edu.unsam.cardiovigia.model;

import java.util.List;

public class Alerta {
    private String id;
    private String pacienteId;
    private NivelRiesgo nivel;
    private double probabilidad;
    private List<String> factoresPrincipales;
    private String notificacion;
    private boolean reconocida;
    private boolean escalada;
    private String destinatarioActual;
    private long timestampCreacion;

    public Alerta() {
    }

    public Alerta(String id, String pacienteId, NivelRiesgo nivel, double probabilidad, List<String> factoresPrincipales, String notificacion, String destinatarioActual, long timestampCreacion) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.nivel = nivel;
        this.probabilidad = probabilidad;
        this.factoresPrincipales = factoresPrincipales;
        this.notificacion = notificacion;
        this.destinatarioActual = destinatarioActual;
        this.timestampCreacion = timestampCreacion;
        this.reconocida = false;
        this.escalada = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public NivelRiesgo getNivel() {
        return nivel;
    }

    public void setNivel(NivelRiesgo nivel) {
        this.nivel = nivel;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }

    public List<String> getFactoresPrincipales() {
        return factoresPrincipales;
    }

    public void setFactoresPrincipales(List<String> factoresPrincipales) {
        this.factoresPrincipales = factoresPrincipales;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public boolean isReconocida() {
        return reconocida;
    }

    public void setReconocida(boolean reconocida) {
        this.reconocida = reconocida;
    }

    public boolean isEscalada() {
        return escalada;
    }

    public void setEscalada(boolean escalada) {
        this.escalada = escalada;
    }

    public String getDestinatarioActual() {
        return destinatarioActual;
    }

    public void setDestinatarioActual(String destinatarioActual) {
        this.destinatarioActual = destinatarioActual;
    }

    public long getTimestampCreacion() {
        return timestampCreacion;
    }

    public void setTimestampCreacion(long timestampCreacion) {
        this.timestampCreacion = timestampCreacion;
    }
}
