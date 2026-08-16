package org.xq.service.autoconfigure;

@FunctionalInterface
public interface CorrelationIdGenerator {
    String generate();
}
