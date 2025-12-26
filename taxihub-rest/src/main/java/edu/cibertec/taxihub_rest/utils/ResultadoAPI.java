package edu.cibertec.taxihub_rest.utils;

import java.time.Instant;
import java.util.Map;

public record ResultadoAPI<T>(
        boolean exito,
        String mensaje,
        T datos,
        Map<String, String> errores,
        Instant marcaTiempo
) {
    public static <T> ResultadoAPI<T> exito(T datos) {
        return new ResultadoAPI<>(true, "Operación completada satisfactoriamente", datos, null, Instant.now());
    }

    public static <T> ResultadoAPI<T> exito(T datos, String mensaje) {
        return new ResultadoAPI<>(true, mensaje, datos, null, Instant.now());
    }

    public static <T> ResultadoAPI<T> fallo(String mensaje, Map<String, String> errores) {
        return new ResultadoAPI<>(false, mensaje, null, errores, Instant.now());
    }

    public static <T> ResultadoAPI<T> fallo(String mensaje) {
        return fallo(mensaje, null);
    }
}