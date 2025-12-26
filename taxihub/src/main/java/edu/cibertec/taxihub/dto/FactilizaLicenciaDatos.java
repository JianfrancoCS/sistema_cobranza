package edu.cibertec.taxihub.dto;

public record FactilizaLicenciaDatos(
        String numero_documento,
        String nombre_completo,
        LicenciaDetalle licencia
) {
    public record LicenciaDetalle(
            String numero,
            String categoria,
            String fecha_expedicion,
            String fecha_vencimiento,
            String estado,
            String restricciones
    ) {
    }
}
