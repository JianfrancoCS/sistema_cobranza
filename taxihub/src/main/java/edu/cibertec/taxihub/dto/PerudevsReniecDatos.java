package edu.cibertec.taxihub.dto;

public record PerudevsReniecDatos(
        String id,
        String nombres,
        String apellido_paterno,
        String apellido_materno,
        String nombre_completo,
        String genero,
        String fecha_nacimiento,
        String codigo_verificacion
) {
}
