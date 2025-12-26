package edu.cibertec.taxihub.dto;

public record FactilizaReponse<T>(
        int status,
        boolean success,
        String message,
        T data
) {
}
