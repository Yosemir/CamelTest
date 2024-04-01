package com.telefonica.camel.exception;

public class IODirectoryException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    public IODirectoryException(String message, Throwable cause){
	super(message, cause);
    }

}
