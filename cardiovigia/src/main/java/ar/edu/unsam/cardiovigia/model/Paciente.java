package ar.edu.unsam.cardiovigia.model;

public class Paciente {
    private String id;
    private int edad;
    private boolean perfilCompleto;
    private String faseQuirurgica;

    public Paciente() {
    }

    public Paciente(String id, int edad, boolean perfilCompleto, String faseQuirurgica) {
        this.id = id;
        this.edad = edad;
        this.perfilCompleto = perfilCompleto;
        this.faseQuirurgica = faseQuirurgica;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isPerfilCompleto() {
        return perfilCompleto;
    }

    public void setPerfilCompleto(boolean perfilCompleto) {
        this.perfilCompleto = perfilCompleto;
    }

    public String getFaseQuirurgica() {
        return faseQuirurgica;
    }

    public void setFaseQuirurgica(String faseQuirurgica) {
        this.faseQuirurgica = faseQuirurgica;
    }
}
