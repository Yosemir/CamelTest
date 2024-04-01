package com.telefonica.camel.component;

import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.NameTablesSynonym;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActionLogicIngestFull {

    private static final Logger log = LoggerFactory.getLogger(ActionLogicIngestFull.class);

    @Handler
    public void countLines(Exchange exchange) {
        log.info("countLines called");

        LoadSftpFileToTableProps loadSftpFileToTableProps = exchange.getProperty(KeyProperty.LOAD_FILE_PROPERTIES, LoadSftpFileToTableProps.class);

        String currentTableName = exchange.getIn().getHeader("currentTableName", String.class);

        String newTableName = NameTablesSynonym.getNewTable(loadSftpFileToTableProps.getSynonym(), currentTableName);

        exchange.setProperty("newTableName", newTableName);

        log.info("countLines completed, newTableName: " + newTableName);
    }

}
