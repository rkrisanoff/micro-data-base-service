import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val ktlint by configurations.creating

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("plugin.allopen") version "1.7.22"
}

group = "ifmo.dma"
version = "0.9.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

configurations {
    ktlint
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.data:spring-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
    implementation("com.networknt:json-schema-validator:1.0.79")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    // https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api
    implementation("jakarta.persistence:jakarta.persistence-api:3.0.0")

    ktlint("com.pinterest:ktlint:0.49.0") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }


    implementation("io.github.microutils:kotlin-logging:1.7.10")

}
val ktlintCheck by tasks.registering(JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}
tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
