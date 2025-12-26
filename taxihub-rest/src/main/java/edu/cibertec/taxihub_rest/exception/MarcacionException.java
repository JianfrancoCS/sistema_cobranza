package edu.cibertec.taxihub_rest.exception;

public class MarcacionException extends RuntimeException {

    public MarcacionException(String message) {
        super(message);
    }

    public MarcacionException(String message, Object... args) {
        super(String.format(message, args));
    }
}