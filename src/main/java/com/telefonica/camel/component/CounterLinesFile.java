package com.telefonica.camel.component;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class CounterLinesFile {

    @Handler
    public void countLines(Exchange exchange) {
	String filePath = exchange.getIn().getHeader(Exchange.FILE_LOCAL_WORK_PATH, String.class);
	long numberLines = Util.counterLinesFile(Path.of(filePath));
	exchange.setProperty(KeyProperty.FILE_NUMBER_LINES, numberLines);
    }

}
