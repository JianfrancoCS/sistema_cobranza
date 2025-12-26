package edu.cibertec.taxihub.dto;

public record PerudevsResponse<T>(
        boolean estado,
        String mensage,
        T resultado
) {
}
