package com.telefonica.camel.component;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.Util;
import com.telefonica.camel.exception.NumberFailedLinesException;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;

import java.nio.file.Path;

@UtilityClass
public class HelperSkippedLoadFail {

    private static final String	ARROW	  = " -> ";
    private static final String	EXTENSION = ".txt";

    public static void printFailLine(Exchange exchange, Exception exeption, String failLine) {
	String fileName = getFilename(exchange);
	String fullLine = failLine + ARROW + Util.replaceLineBreakBySpace(exeption.getMessage()) + System.lineSeparator();
	LoadSftpFileToTableProps props = getLoadFileProperties(exchange);
	String badDirectoryPath = props.getBadDirectory();
	Path badFile = Path.of(badDirectoryPath, fileName);
	Util.printLineFile(badFile, fullLine);
    }

    public static void verifyNumberFails(Exchange exchange) {
	long numberLines = 0;
	LoadSftpFileToTableProps props = getLoadFileProperties(exchange);
	String badDirectoryPath = props.getBadDirectory();
	String fileName = getFilename(exchange);
	Path badFile = Path.of(badDirectoryPath, fileName);

	if (Util.existFile(badFile)) {
	    numberLines = Util.counterLinesFile(badFile);
	}

	if (numberLines >= props.getMaxFailedLines()) {
	    throw new NumberFailedLinesException(badFile.toAbsolutePath().toString());
	}

    }

    private static LoadSftpFileToTableProps getLoadFileProperties(Exchange exchange) {
	return exchange.getProperty(KeyProperty.LOAD_FILE_PROPERTIES, LoadSftpFileToTableProps.class);
    }

    private static String getFilename(Exchange exchange) {
	return exchange.getProperty(KeyProperty.UUID, String.class) + EXTENSION;
    }

}
