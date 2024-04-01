package com.telefonica.camel.model;

import com.telefonica.camel.common.ExecutionType;
import lombok.Data;

@Data
public abstract class TransferFilesProperties {

    private ExecutionType executionType	= ExecutionType.SCHEDULED;
    private String processName;
    private String originServerPath;
    private String originServerUsername;
    private String originServerPassword;
    private String fileName;
    private Long reconnectDelay;
    private Integer maximumReconnectAttempts;
    private String cron;

    private String destinationServerPath;
    private String destinationServerUsername;
    private String destinationServerPassword;

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

    public TransferFilesProperties withExecutionType(ExecutionType executionType) {
        setExecutionType(executionType);
        return this;
    }

}
