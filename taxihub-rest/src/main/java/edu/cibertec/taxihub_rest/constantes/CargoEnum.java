package edu.cibertec.taxihub_rest.constantes;

public enum CargoEnum {
    SUPERVISOR("SUPERVISOR"),
    CONDUCTOR("CONDUCTOR"),
    ADMINISTRADOR("ADMINISTRADOR");

    private final String nombre;

    CargoEnum(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }

    public String getValue() {
        return nombre;
    }
}