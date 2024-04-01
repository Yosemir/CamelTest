package com.telefonica.camel.config;

import com.telefonica.camel.common.ExecutionType;
import com.telefonica.camel.model.LoadScoreRcProps;
import com.telefonica.camel.model.TransferScoreRcProperties;
import com.telefonica.camel.route.LoadSftpFileDetailsRoute;
import com.telefonica.camel.route.LoadSftpFileHeaderRoute;
import com.telefonica.camel.route.LoadSftpFileToTableRoute;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class CamelRouteConfig extends CamelRouteConfigBase {

    private static final String	CONTEXT_SCHEDULED = "context-scheduled";
    private static final String	CONTEXT_MANUAL	  = "context-manual";

    private final LoadScoreRcProps loadScoreRcProps;

    private final TransferScoreRcProperties transferScoreRcProperties;

    @Bean(CONTEXT_SCHEDULED)
    @Primary
    public CamelContext camelContextSchedule() throws Exception {
	ExecutionType executionType = ExecutionType.SCHEDULED;
	CamelContext camelContext = getCamelContext(executionType.getLabel());
	loadRoutesInContext(camelContext, executionType);
	return camelContext;
    }

    @Bean(CONTEXT_MANUAL)
    @DependsOn(CONTEXT_SCHEDULED)
    public CamelContext camelContextManual() throws Exception {
	ExecutionType executionType = ExecutionType.MANUAL;
	CamelContext camelContext = getCamelContext(executionType.getLabel());
	loadRoutesInContext(camelContext, executionType);
	return camelContext;
    }

    private void loadRoutesInContext(CamelContext context, ExecutionType executionType) throws Exception {
	context.addRoutes(new LoadSftpFileToTableRoute(loadScoreRcProps.withExecutionType(executionType), transferScoreRcProperties.withExecutionType(executionType)));
    context.addRoutes(new LoadSftpFileHeaderRoute(loadScoreRcProps.withExecutionType(executionType), transferScoreRcProperties.withExecutionType(executionType)));
    context.addRoutes(new LoadSftpFileDetailsRoute(loadScoreRcProps.withExecutionType(executionType), transferScoreRcProperties.withExecutionType(executionType)));

    }

}
