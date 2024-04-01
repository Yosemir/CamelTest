package com.telefonica.camel.exception;

import lombok.Getter;

@Getter
public class NumberFailedLinesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NumberFailedLinesException(String filePath) {
	super("The maximum number of failed lines allowed has been reached, review the file " + filePath);
    }

}
