package org.xq.service.autoconfigure;

import java.util.Objects;

public final class CorrelationIdGenerators {
    private CorrelationIdGenerators() {
    }

    public static CorrelationIdGenerator fixed(String correlationId) {
        Objects.requireNonNull(correlationId, "correlationId");
        return () -> correlationId;
    }
}
