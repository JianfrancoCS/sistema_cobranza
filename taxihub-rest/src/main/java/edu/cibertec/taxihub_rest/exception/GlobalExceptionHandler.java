package edu.cibertec.taxihub_rest.exception;

import edu.cibertec.taxihub_rest.utils.ResultadoAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MarcacionException.class)
    public ResponseEntity<ResultadoAPI<Object>> handleMarcacionException(MarcacionException ex) {
        String mensaje = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), Locale.getDefault());
        ResultadoAPI<Object> resultado = ResultadoAPI.fallo(mensaje);
        return ResponseEntity.ok(resultado);
    }
}