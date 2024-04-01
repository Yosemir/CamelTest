package com.telefonica.camel.component;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import java.sql.BatchUpdateException;
import java.util.List;
import java.util.Map;

@Component
public class HandlerSqlLoadFail {

    private static final String KEY_FILE_LINE = "file_line";

    @Handler
    public void handleSqlLoadFail(Exchange exchange, @Body List<Map<String, Object>> batch) {
	HelperSkippedLoadFail.verifyNumberFails(exchange);
	BatchUpdateException exception = getBatchException(exchange);
	int indexFailLine = getIndexFailLine(exception);
	String failLine = getFailLine(batch, indexFailLine);
	HelperSkippedLoadFail.printFailLine(exchange, exception, failLine);
	exchange.getIn().setBody(getRemainingBatch(batch, indexFailLine));
    }

    private BatchUpdateException getBatchException(Exchange exchange) {
	Throwable throwable = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
	return (BatchUpdateException) throwable.getCause();
    }

    private int getIndexFailLine(BatchUpdateException exception) {
	return exception.getUpdateCounts().length;
    }

    private String getFailLine(List<Map<String, Object>> batch, int index) {
	return batch.get(index).get(KEY_FILE_LINE).toString();
    }

    private List<Map<String, Object>> getRemainingBatch(List<Map<String, Object>> batch, int index) {
	return batch.subList(index + 1, batch.size());
    }

}
