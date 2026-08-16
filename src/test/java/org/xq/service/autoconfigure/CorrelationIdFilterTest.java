package org.xq.service.autoconfigure;

import org.junit.jupiter.api.Test;
import io.opentelemetry.context.Context;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {
    private final CorrelationIdFilter filter = new CorrelationIdFilter(() -> "generated-id");

    @Test
    void propagatesValidInboundIdAndCleansThreadState() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "request-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        var chain = (jakarta.servlet.FilterChain) (req, res) -> {
            assertThat(CorrelationContext.currentId()).contains("request-123");
            assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isEqualTo("request-123");
            assertThat(Context.current().get(CorrelationIdFilter.OTEL_CONTEXT_KEY)).isEqualTo("request-123");
        };

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("request-123");
        assertThat(CorrelationContext.currentId()).isEmpty();
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
        assertThat(Context.current().get(CorrelationIdFilter.OTEL_CONTEXT_KEY)).isNull();
    }

    @Test
    void replacesInvalidInboundIdWithGeneratedId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "contains spaces");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> { });

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("generated-id");
    }
}
