package com.telefonica.camel.exception;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.Util;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExceptionHandler implements Processor {

    private static final int MAX_STACK_TRACE = 50;

    private static Map<String, String> lastProcessId = new HashMap<>();

    private Logger logger;

    public ExceptionHandler(Logger logger) {
	this.logger = logger;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
	Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
	String uuid = getUUID(exchange);
	String message = getErrorMessage(exception);
	if (exception instanceof NumberFailedLinesException) {
	    handlerAction(uuid, message, exchange);
	} else {
	    handlerAction(uuid, message);
	}
    }

    private String getUUID(Exchange exchange) {
	String uuid = exchange.getProperty(KeyProperty.UUID, String.class);
	return uuid == null ? Util.getUUID() : uuid;
    }

    private String getErrorMessage(Throwable exception) {
	String message;
	if (exception.getMessage() != null) {
	    message = exception.getMessage().replace("\n", " ").trim();
	    return message + " " + getStackTrace(exception);
	}
	if (exception.getCause() != null) {
	    message = exception.getCause().getMessage().replace("\n", " ").trim();
	    return message + " " + getStackTrace(exception);
	}
	return getStackTrace(exception);
    }

    private synchronized void handlerAction(String uuid, String message, Exchange exchange) {
	String processName = getProcessName(exchange);
	if (!uuid.equals(lastProcessId.get(processName))) {
	    lastProcessId.put(processName, uuid);
	    logger.error("{} error: {}", uuid, message);
	}
    }

    private void handlerAction(String uuid, String message) {
	logger.error("{} error: {}", uuid, message);
    }

    private String getProcessName(Exchange exchange) {
	LoadSftpFileToTableProps loadFileProperties = exchange.getProperty(KeyProperty.LOAD_FILE_PROPERTIES, LoadSftpFileToTableProps.class);
	return loadFileProperties.getProcessName();
    }

    private String getStackTrace(Throwable exception) {
	int stackTraceSize = exception.getStackTrace().length;
	return Arrays.asList(Arrays.copyOfRange(exception.getStackTrace(), 0, Math.min(MAX_STACK_TRACE, stackTraceSize))).toString();
    }
    
}
