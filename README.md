# xq-service-sdk-jvm

Spring Boot 4.1 service-foundation starter for XQ JVM services.

## v0.1 contract

The starter supplies validated `xq.service.name` configuration, inbound `X-Correlation-ID`
propagation, MDC enrichment and cleanup, a safe RFC 9457-style `ServiceException` envelope,
and a named readiness health contribution. Application beans of the same SDK types always win.

It deliberately does not supply authentication, databases, messaging, deployment, a telemetry
exporter, resilience policies, or product-domain abstractions.

```kotlin
dependencies {
    implementation("org.xq:xq-service-sdk-jvm:0.1.0")
}
```

```yaml
xq:
  service:
    name: orders
```

Run the checks with `./gradlew check cyclonedxBom`. Publishing is performed only by protected-main
GitHub Actions; package credentials are never stored in this repository.
