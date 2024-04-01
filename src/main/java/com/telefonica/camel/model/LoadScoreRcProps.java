package com.telefonica.camel.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "load-score-rc")
public class LoadScoreRcProps extends LoadSftpFileToTableProps{
    
}
