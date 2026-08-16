package org.xq.service.autoconfigure;

import java.util.Optional;

public final class CorrelationContext {
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static Optional<String> currentId() {
        return Optional.ofNullable(CORRELATION_ID.get());
    }

    static void set(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    static void clear() {
        CORRELATION_ID.remove();
    }
}
