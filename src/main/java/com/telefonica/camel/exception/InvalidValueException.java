package com.telefonica.camel.exception;

public class InvalidValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidValueException(String message) {
	super(message);
    }

    public InvalidValueException(Object value, Throwable cause) {
	super("invalid value \"" + value + "\": " + cause.getMessage(), cause);
    }

}
