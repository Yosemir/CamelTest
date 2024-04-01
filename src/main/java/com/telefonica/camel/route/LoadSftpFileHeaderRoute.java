package com.telefonica.camel.route;


import com.telefonica.camel.common.ExecutionType;
import com.telefonica.camel.common.KeyProperty;
import com.telefonica.camel.common.NameTablesSynonym;
import com.telefonica.camel.common.Util;
import com.telefonica.camel.component.ActionLogicIngestFull;
import com.telefonica.camel.component.CheckerLoadFailLines;
import com.telefonica.camel.component.CounterLinesFile;
import com.telefonica.camel.component.HandlerSqlLoadFail;
import com.telefonica.camel.exception.ExceptionHandler;
import com.telefonica.camel.model.LoadSftpFileToTableProps;
import com.telefonica.camel.model.TransferFilesProperties;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.BatchUpdateException;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class LoadSftpFileHeaderRoute extends EndpointRouteBuilder {

    private static final String	ROUTE	  = "route";
    private static final String	STOP	  = "stop";
    private static final String	SPRING	  = "spring";
    private static final String	CRON	  = "cron";
    private static final String	CHANGED	  = "changed";
    private static final String	INPROCESS = "inprocess";
    private static final String	FAILED	  = "failed";
    private static final String	PROCESSED = "processed";

    private final LoadSftpFileToTableProps props;

    private final TransferFilesProperties transferFilesProperties;

    @Override
    public void configure() throws Exception {

        Logger log = LoggerFactory.getLogger(props.getProcessName());

        Logger logTransfer = LoggerFactory.getLogger(transferFilesProperties.getProcessName());

        onException(Exception.class)
                .process(new ExceptionHandler(log));

        from(getSftpInputFile(props))
                .log("init" + props.getProcessName())
                .routeId(props.getProcessName())
                .autoStartup(false)
                .setProperty(KeyProperty.UUID).method(Util.class, "getUUID")
                .log(LoggingLevel.INFO, log, Util.log("start process"))
                .choice()
                .when(simple("${body} != null"))
                .setProperty(KeyProperty.LOAD_FILE_PROPERTIES, constant(props))
                .to(sql(props.getSqlSelectSynonym()).outputType("SelectOne").outputHeader("currentTableName"))
                .bean(ActionLogicIngestFull.class)
                .log(LoggingLevel.INFO, log, Util.log("Truncate table ${exchangeProperty.newTableName}"))
                .choice()
                .when(exchange -> exchange.getProperty("newTableName").toString().equalsIgnoreCase(props.getSynonym() + NameTablesSynonym.TABLE_1.getTableNumber()))
                .to(sql(props.getSqlTruncateOne()))
                .otherwise()
                .to(sql(props.getSqlTruncateTwo()))
                .endChoice()
                .bean(CounterLinesFile.class)
                .log(LoggingLevel.INFO, Util.log("done: get file ${header.CamelFileName}, lines=${exchangeProperty.FileNumberLines}"))
                .split(body().tokenize(props.getLineBreak(), props.getBatchSize(), props.getSkipFirstLine()))
                .streaming()
                .executorService(Executors.newFixedThreadPool(props.getNumberThreads()))
                .stopOnException()
                .bean(props.getMapperBean())
                .loopDoWhile(body().isNotNull())
                .doTry()
                .choice()
                .when(exchange -> exchange.getProperty("newTableName").toString().equalsIgnoreCase(props.getSynonym() + NameTablesSynonym.TABLE_1.getTableNumber()))
                .to(mergeDatabase(props.getSqlInsertOne()))
                .otherwise()
                .to(mergeDatabase(props.getSqlInsertTwo()))
                .endChoice()
                .end()
                .setBody(simple(null))
                .endDoTry()
                .doCatch(BatchUpdateException.class)
                .bean(HandlerSqlLoadFail.class)
                .end()
                .end()
                .end()
                .bean(CheckerLoadFailLines.class)
                .log(LoggingLevel.INFO, log, Util.log("done: upload file ${header.CamelFileName} to " + props.getTable()))
                .log(LoggingLevel.INFO, log, Util.log("Replace Synonym to ${exchangeProperty.newTableName} and truncate table ${header.currentTableName}"))
                .choice()
                .when(exchange -> exchange.getProperty("newTableName").toString().equalsIgnoreCase(props.getSynonym() + NameTablesSynonym.TABLE_1.getTableNumber()))
                .to(sql(props.getSqlReplaceSynonymOne()))
                .to(sql(props.getSqlTruncateTwo()))
                .otherwise()
                .to(sql(props.getSqlReplaceSynonymTwo()))
                .to(sql(props.getSqlTruncateOne()))
                .endChoice()
                .otherwise()
                .log(LoggingLevel.WARN, log, Util.log("not found file"))
                .endChoice()
                .end()
                .log(LoggingLevel.INFO, log, Util.log("end process"))
                .filter(constant(props.getExecutionType()).isEqualTo(ExecutionType.MANUAL))
                .to(controlbus(ROUTE)
                        .routeId(props.getProcessName())
                        .action(STOP)
                        .async(true))
                .end();

    }

    private EndpointConsumerBuilder getSftpInputFile(LoadSftpFileToTableProps props) {
        return sftp(props.getSftpPath())
                .username(props.getSftpUsername())
                .password(props.getSftpPassword())
                .include(props.getFilenamePattern())
                .includeExt(props.getFilenameExtension())
                //.fileName("ScoreRC_${date:now:ddMMyy}.txt")
                .charset(props.getCharset())
                .scheduler(SPRING)
                .schedulerProperties(CRON, props.getCron())
                .repeatCount(props.getRepeatCount())
                .binary(true)
                .preMove(INPROCESS)
                .move(PROCESSED)
                .moveFailed(FAILED)
                .readLock(CHANGED)
                .readLockCheckInterval(5000)
                .readLockMinAge(6000)
                .maxMessagesPerPoll(props.getMaxMessageByPoll())
                .preSort(true)
                .sortBy("${file:modified}")
                .sendEmptyMessageWhenIdle(true)
                .advanced()
                .bridgeErrorHandler(true)
                .throwExceptionOnConnectFailed(true)
                .localWorkDirectory(props.getProcessDirectory())
                .reconnectDelay(props.getReconnectDelay())
                .maximumReconnectAttempts(props.getMaximumReconnectAttempts())
                .stepwise(false);
    }

    private EndpointProducerBuilder mergeDatabase(String sqlPath) {
        return sql(sqlPath)
                .batch(true);
    }
}
