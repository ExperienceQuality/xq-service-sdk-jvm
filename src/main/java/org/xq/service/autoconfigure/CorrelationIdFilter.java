package org.xq.service.autoconfigure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.Scope;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

public final class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";
    public static final ContextKey<String> OTEL_CONTEXT_KEY = ContextKey.named("xq.correlation_id");
    private static final Pattern VALID_ID = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]{0,127}");

    private final CorrelationIdGenerator generator;

    public CorrelationIdFilter(CorrelationIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = valid(request.getHeader(HEADER_NAME)) ? request.getHeader(HEADER_NAME) : generator.generate();
        response.setHeader(HEADER_NAME, correlationId);
        CorrelationContext.set(correlationId);
        MDC.put(MDC_KEY, correlationId);
        try (Scope ignored = Context.current().with(OTEL_CONTEXT_KEY, correlationId).makeCurrent()) {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
            CorrelationContext.clear();
        }
    }

    private boolean valid(String candidate) {
        return candidate != null && VALID_ID.matcher(candidate).matches();
    }
}
