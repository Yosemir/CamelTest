package com.telefonica.camel.config;

import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.DefaultConfigurationConfigurer;
import org.apache.camel.model.Model;
import org.apache.camel.spi.BeanRepository;
import org.apache.camel.spi.StartupStepRecorder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.boot.*;
import org.apache.camel.spring.spi.ApplicationContextBeanRepository;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.support.startup.LoggingStartupStepRecorder;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableConfigurationProperties(CamelConfigurationProperties.class)
public abstract class CamelRouteConfigBase {

    private static final Logger LOG = LoggerFactory.getLogger(CamelRouteConfigBase.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CamelConfigurationProperties config;

    protected CamelContext getCamelContext(String contextName) throws Exception {
	CamelContext camelContext = new SpringBootCamelContext(applicationContext, config.isWarnOnEarlyShutdown());
	if (contextName != null) {
	    ((SpringCamelContext) camelContext).setName(contextName);
	}
	return doConfigureCamelContext(applicationContext, camelContext, config);
    }

    /**
     * Not to be used by Camel end users
     */
    private CamelContext doConfigureCamelContext(ApplicationContext applicationContext, CamelContext camelContext,
	    CamelConfigurationProperties config) throws Exception {

	// setup startup recorder before building context
	configureStartupRecorder(camelContext, config);

	camelContext.build();

	// initialize properties component eager
	PropertiesComponent pc = applicationContext.getBeanProvider(PropertiesComponent.class).getIfAvailable();
	if (pc != null) {
	    pc.setCamelContext(camelContext);
	    camelContext.setPropertiesComponent(pc);
	}

	final Map<String, BeanRepository> repositories = applicationContext.getBeansOfType(BeanRepository.class);
	if (!repositories.isEmpty()) {
	    List<BeanRepository> reps = new ArrayList<>();
	    // include default bean repository as well
	    reps.add(new ApplicationContextBeanRepository(applicationContext));
	    // and then any custom
	    reps.addAll(repositories.values());
	    // sort by ordered
	    OrderComparator.sort(reps);
	    // and plugin as new registry
	    camelContext.adapt(ExtendedCamelContext.class).setRegistry(new DefaultRegistry(reps));
	}

	if (ObjectHelper.isNotEmpty(config.getFileConfigurations())) {
	    Environment env = applicationContext.getEnvironment();
	    if (env instanceof ConfigurableEnvironment) {
		MutablePropertySources sources = ((ConfigurableEnvironment) env).getPropertySources();
		if (!sources.contains("camel-file-configuration")) {
		    sources.addFirst(
			    new FilePropertySource("camel-file-configuration", applicationContext, config.getFileConfigurations()));
		}
	    }
	}

	camelContext.adapt(ExtendedCamelContext.class).setPackageScanClassResolver(new FatJarPackageScanClassResolver());
	camelContext.adapt(ExtendedCamelContext.class).setPackageScanResourceResolver(new FatJarPackageScanResourceResolver());

	if (config.getRouteFilterIncludePattern() != null || config.getRouteFilterExcludePattern() != null) {
	    LOG.info("Route filtering pattern: include={}, exclude={}", config.getRouteFilterIncludePattern(),
		    config.getRouteFilterExcludePattern());
	    camelContext.getExtension(Model.class).setRouteFilterPattern(config.getRouteFilterIncludePattern(),
		    config.getRouteFilterExcludePattern());
	}

	// configure the common/default options
	DefaultConfigurationConfigurer.configure(camelContext, config);
	// lookup and configure SPI beans
	DefaultConfigurationConfigurer.afterConfigure(camelContext);
	// and call after all properties are set
	DefaultConfigurationConfigurer.afterPropertiesSet(camelContext);

	return camelContext;
    }

    private void configureStartupRecorder(CamelContext camelContext, CamelConfigurationProperties config) {
	if ("false".equals(config.getStartupRecorder())) {
	    camelContext.adapt(ExtendedCamelContext.class).getStartupStepRecorder().setEnabled(false);
	} else if ("logging".equals(config.getStartupRecorder())) {
	    camelContext.adapt(ExtendedCamelContext.class).setStartupStepRecorder(new LoggingStartupStepRecorder());
	} else if ("java-flight-recorder".equals(config.getStartupRecorder()) || config.getStartupRecorder() == null) {
	    // try to auto discover camel-jfr to use
	    StartupStepRecorder fr = camelContext.adapt(ExtendedCamelContext.class).getBootstrapFactoryFinder()
		    .newInstance(StartupStepRecorder.FACTORY, StartupStepRecorder.class).orElse(null);
	    if (fr != null) {
		LOG.debug("Discovered startup recorder: {} from classpath", fr);
		fr.setRecording(config.isStartupRecorderRecording());
		fr.setStartupRecorderDuration(config.getStartupRecorderDuration());
		fr.setRecordingProfile(config.getStartupRecorderProfile());
		fr.setMaxDepth(config.getStartupRecorderMaxDepth());
		camelContext.adapt(ExtendedCamelContext.class).setStartupStepRecorder(fr);
	    }
	}
    }

}
