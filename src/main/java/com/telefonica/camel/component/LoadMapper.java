package com.telefonica.camel.component;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.exception.InvalidValueException;
import com.telefonica.camel.exception.NumberColumnsException;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class LoadMapper extends LoadMapperValidator {

    private static final String KEY_FILE_LINE = "file_line";

    @Handler
    public List<Map<String, Object>> getBatch(Exchange exchange, @Body String data) {
	return data.lines().map(line -> getRow(line, exchange)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Map<String, Object> getRow(String line, Exchange exchange) {
	Map<String, Object> row = null;
	try {
	    String[] cols = splitBySeparator(line, exchange);
	    row = mapper(cols);
	    row.put(KEY_FILE_LINE, line);
	} catch (InvalidValueException | NumberColumnsException e) {
	    HelperSkippedLoadFail.verifyNumberFails(exchange);
	    HelperSkippedLoadFail.printFailLine(exchange, e, line);
	}
	return row;
    }

    private String[] splitBySeparator(String line, Exchange exchange) {
	LoadSftpFileToTableProps loadFileProperties = getLoadFileProperties(exchange);
	int validNumberColumns = loadFileProperties.getNumberColumns();
	String separator = loadFileProperties.getSeparator();
	String[] cols = line.split(Pattern.quote(separator), -1);
	int numberColumns = cols.length;
	if (numberColumns != validNumberColumns) {
	    throw new NumberColumnsException(validNumberColumns, numberColumns);
	}
	return cols;
    }

    private LoadSftpFileToTableProps getLoadFileProperties(Exchange exchange) {
	return exchange.getProperty(KeyProperty.LOAD_FILE_PROPERTIES, LoadSftpFileToTableProps.class);
    }

    protected abstract Map<String, Object> mapper(String[] cols);

}
