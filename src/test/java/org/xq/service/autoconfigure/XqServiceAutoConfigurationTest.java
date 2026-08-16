package org.xq.service.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

class XqServiceAutoConfigurationTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(XqServiceAutoConfiguration.class))
            .withPropertyValues("xq.service.name=orders");

    @Test
    void createsFoundationBeansForAConfiguredService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ServiceProperties.class);
            assertThat(context).hasSingleBean(CorrelationIdGenerator.class);
            assertThat(context).hasSingleBean(CorrelationIdFilter.class);
            assertThat(context).hasBean("xqServiceReadinessHealthIndicator");
        });
    }

    @Test
    void backsOffWhenApplicationProvidesCorrelationIdGenerator() {
        contextRunner.withUserConfiguration(ApplicationCorrelationConfiguration.class)
                .run(context -> assertThat(context).getBean(CorrelationIdGenerator.class)
                        .isSameAs(context.getBean("applicationCorrelationIdGenerator")));
    }

    static class ApplicationCorrelationConfiguration {
        @Bean
        CorrelationIdGenerator applicationCorrelationIdGenerator() {
            return () -> "application-correlation-id";
        }
    }
}
