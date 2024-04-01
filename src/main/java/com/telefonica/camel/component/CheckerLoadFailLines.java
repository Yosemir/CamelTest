package com.telefonica.camel.component;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.Util;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class CheckerLoadFailLines {

    private static final String EXTENSION = ".txt";

    @Handler
    public void existFailedLinesFile(Exchange exchange) {
	LoadSftpFileToTableProps props = getLoadFileProperties(exchange);
	String uuid = getUUID(exchange);
	String fileName = getFilename(uuid);
	Path failedLinesFile = Path.of(props.getBadDirectory(), fileName);
	if (Util.existFile(failedLinesFile)) {
	    printWarning(uuid, props.getProcessName(), failedLinesFile);
	}
    }

    private LoadSftpFileToTableProps getLoadFileProperties(Exchange exchange) {
	return exchange.getProperty(KeyProperty.LOAD_FILE_PROPERTIES, LoadSftpFileToTableProps.class);
    }

    private void printWarning(String uuid, String processName, Path failedLinesFile) {
	Logger logger = LoggerFactory.getLogger(processName);
	logger.warn("{} review the file that contain omitted failed lines: {}", uuid, failedLinesFile.toAbsolutePath());
    }

    private String getFilename(String uuid) {
	return uuid + EXTENSION;
    }

    private String getUUID(Exchange exchange) {
	return exchange.getProperty(KeyProperty.UUID, String.class);
    }

}
