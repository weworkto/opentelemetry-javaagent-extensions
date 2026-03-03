plugins {
    java
}

group = "com.aikero.otel"
version = providers.gradleProperty("otelAgentVersion").get()

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

val otelAgentVersion = providers.gradleProperty("otelAgentVersion").get()
val otelSdkVersion = providers.gradleProperty("otelSdkVersion").get()
val autoServiceVersion = providers.gradleProperty("autoServiceVersion").get()

dependencies {
    // OTel Agent Extension API (provided by agent at runtime)
    compileOnly(platform("io.opentelemetry:opentelemetry-bom:$otelSdkVersion"))
    compileOnly("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:$otelAgentVersion-alpha")
    compileOnly("io.opentelemetry:opentelemetry-api")

    // AutoService annotation processor (generates META-INF/services at compile time)
    annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")
    compileOnly("com.google.auto.service:auto-service-annotations:$autoServiceVersion")

    // Test
    testImplementation(platform("io.opentelemetry:opentelemetry-bom:$otelSdkVersion"))
    testImplementation("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:$otelAgentVersion-alpha")
    testImplementation("io.opentelemetry:opentelemetry-api")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jar {
    archiveBaseName.set("opentelemetry-javaagent-extensions")
    archiveVersion.set(version.toString())
}

tasks.test {
    useJUnitPlatform()
}
