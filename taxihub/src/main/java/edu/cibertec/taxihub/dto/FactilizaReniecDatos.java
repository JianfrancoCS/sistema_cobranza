package edu.cibertec.taxihub.dto;

public record FactilizaReniecDatos(
        String numero,
        String nombres,
        String apellido_paterno,
        String apellido_materno,
        String nombre_completo,
        String fecha_nacimiento,
        String sexo
) {
}
