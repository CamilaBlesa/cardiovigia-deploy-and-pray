package ar.edu.unsam.cardiovigia.model;

public class SignoVital {
    private int frecuenciaCardiaca;
    private int presionArterialMedia;
    private int saturacionOxigeno;
    private double temperatura;
    private int segundosSinSenal;

    public SignoVital() {
    }

    public SignoVital(int frecuenciaCardiaca, int presionArterialMedia, int saturacionOxigeno, double temperatura, int segundosSinSenal) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.presionArterialMedia = presionArterialMedia;
        this.saturacionOxigeno = saturacionOxigeno;
        this.temperatura = temperatura;
        this.segundosSinSenal = segundosSinSenal;
    }

    public int getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public int getPresionArterialMedia() {
        return presionArterialMedia;
    }

    public void setPresionArterialMedia(int presionArterialMedia) {
        this.presionArterialMedia = presionArterialMedia;
    }

    public int getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    public void setSaturacionOxigeno(int saturacionOxigeno) {
        this.saturacionOxigeno = saturacionOxigeno;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public int getSegundosSinSenal() {
        return segundosSinSenal;
    }

    public void setSegundosSinSenal(int segundosSinSenal) {
        this.segundosSinSenal = segundosSinSenal;
    }
}
