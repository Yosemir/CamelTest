package com.telefonica.camel.exception;

public class NumberColumnsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NumberColumnsException(int validValue, int wrongValue) {
	super(wrongValue + " columns obtained, expected value was " + validValue);
    }

}
