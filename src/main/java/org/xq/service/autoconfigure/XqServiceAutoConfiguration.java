package org.xq.service.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.util.UUID;

@AutoConfiguration
@EnableConfigurationProperties(ServiceProperties.class)
public class XqServiceAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    CorrelationIdGenerator correlationIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    @ConditionalOnMissingBean
    CorrelationIdFilter correlationIdFilter(CorrelationIdGenerator generator) {
        return new CorrelationIdFilter(generator);
    }

    @Bean
    @ConditionalOnMissingBean
    ServiceErrorHandler serviceErrorHandler() {
        return new ServiceErrorHandler();
    }

    @Bean("xqServiceReadinessHealthIndicator")
    @ConditionalOnMissingBean(name = "xqServiceReadinessHealthIndicator")
    HealthIndicator xqServiceReadinessHealthIndicator(ServiceProperties properties) {
        return () -> Health.up().withDetail("service", properties.getName()).build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "xqGracefulShutdownCustomizer")
    WebServerFactoryCustomizer<ConfigurableWebServerFactory> xqGracefulShutdownCustomizer() {
        return factory -> factory.setShutdown(Shutdown.GRACEFUL);
    }
}
