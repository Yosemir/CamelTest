package com.telefonica.camel.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "transfer-score-rc")
public class TransferScoreRcProperties extends TransferFilesProperties {
}
