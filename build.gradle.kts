plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "4.1.0"
    id("org.cyclonedx.bom") version "2.3.1"
}

group = "org.xq"
version = providers.gradleProperty("version").orElse("0.1.0-SNAPSHOT").get()

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:4.1.0"))
    api(platform("io.opentelemetry:opentelemetry-bom:1.62.0"))
    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("io.opentelemetry:opentelemetry-api")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:4.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testng:testng:7.11.0")
}

val mediumTest by sourceSets.creating {
    java.srcDir("src/mediumTest/java")
    compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
    runtimeClasspath += output + compileClasspath
}

configurations[mediumTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())

tasks.register<Test>("mediumTest") {
    description = "Runs localhost-only TestNG medium tests."
    group = "verification"
    testClassesDirs = mediumTest.output.classesDirs
    classpath = mediumTest.runtimeClasspath
    useTestNG()
}

tasks.withType<Test>().configureEach {
    if (name != "mediumTest") {
        useJUnitPlatform()
    }
}

tasks.named("check") {
    dependsOn("mediumTest")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ExperienceQuality/xq-service-sdk-jvm")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "xq-service-sdk-jvm"
        }
    }
}
