# xq-service-sdk-jvm context

This Satellite ships a small, generic Spring Boot service-foundation starter. Its stable v0.1
surface is `xq.service.*` configuration, correlation context, safe service-error mapping, and
readiness. Application-provided SDK beans are authoritative; auto-configuration backs off.

This is not a product-service template. Do not add authentication, secret management, persistence,
messaging, deployment, domain models, global OpenTelemetry configuration, or resilience frameworks.

The public package coordinate is `org.xq:xq-service-sdk-jvm`. Releases are immutable SemVer
versions published only by protected-main GitHub Actions after owner approval.
