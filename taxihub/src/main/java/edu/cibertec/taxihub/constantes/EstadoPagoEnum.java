package edu.cibertec.taxihub.constantes;

public enum EstadoPagoEnum {
    POR_REVISAR("POR REVISAR"),
    EN_REVISION("EN REVISION"),
    APROBADO("APROBADO"),
    RECHAZADO("RECHAZADO");

    private final String descripcion;

    EstadoPagoEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}