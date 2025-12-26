package edu.cibertec.taxihub.constantes;

public enum ModalidadPagoEnum {
    BILLETERA_VIRTUAL("BILLETERA VIRTUAL"),
    EFECTIVO("EFECTIVO");

    private final String descripcion;

    ModalidadPagoEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}