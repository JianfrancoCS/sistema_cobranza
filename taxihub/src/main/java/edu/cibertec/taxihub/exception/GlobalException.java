package edu.cibertec.taxihub.exception;

public class GlobalException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public GlobalException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = null;
    }

    public GlobalException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}