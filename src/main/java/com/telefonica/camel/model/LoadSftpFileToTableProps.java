package com.telefonica.camel.model;

import com.telefonica.camel.common.ExecutionType;
import lombok.Data;

@Data
public abstract class LoadSftpFileToTableProps {

    private ExecutionType executionType	= ExecutionType.SCHEDULED;
    private String	  processName;
    private String	  processDirectory;
    private String	  table;
    private String	  synonym;
    private String	  sftpPath;
    private String	  sftpUsername;
    private String	  sftpPassword;
    private Long	  reconnectDelay;
    private Integer	  maximumReconnectAttempts;
    private String	  filenamePattern;
    private String	  filenameExtension;
    private String	  lineBreak;
    private String	  charset;
    private String	  cron;
    private Integer	  maxMessageByPoll;
    private String	  mapperBean;
    private Integer	  batchSize;
    private Integer	  numberColumns;
    private String	  separator;
    private Boolean	  skipFirstLine;
    private Integer	  numberThreads;
    private String	  sqlPath;
    private String	  sqlSelectSynonym;
    private String	  sqlInsertOne;
    private String	  sqlTruncateOne;
    private String	  sqlReplaceSynonymOne;
    private String	  sqlInsertTwo;
    private String	  sqlTruncateTwo;
    private String	  sqlReplaceSynonymTwo;
    private String	  badDirectory;
    private Integer	  maxFailedLines;

    public String getCron() {
	return executionType == ExecutionType.SCHEDULED ? cron : "* * * * * *";
    }

    public int getRepeatCount() {
	return executionType == ExecutionType.SCHEDULED ? 0 : 1;
    }

    public int getMaximumReconnectAttempts() {
	return executionType == ExecutionType.SCHEDULED ? maximumReconnectAttempts : 0;
    }

    public long getReconnectDelay() {
	return executionType == ExecutionType.SCHEDULED ? reconnectDelay : 0;
    }

    public LoadSftpFileToTableProps withExecutionType(ExecutionType executionType) {
	setExecutionType(executionType);
	return this;
    }

}
