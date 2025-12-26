package edu.cibertec.taxihub.constantes;

public enum TipoDocuementoEnum {
    DNI("DNI"),
    CARNET_EXTRANJERIA("CE");

    private final String codigo;

    TipoDocuementoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
