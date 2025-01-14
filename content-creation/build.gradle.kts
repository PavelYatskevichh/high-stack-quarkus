plugins {
    java
    id("io.quarkus")
}

group = "com.yatskevich"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val lombokVersion: String by project
val mapstructVersion: String by project
val lombokMapstructBindingVersion: String by project
val kafkaMessagingVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
//    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
//    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-flyway")
//    implementation("org.flywaydb:flyway-database-postgresql")
//    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("com.yatskevich:kafka-messaging:${kafkaMessagingVersion}")
    implementation("org.mapstruct:mapstruct:${mapstructVersion}")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${lombokMapstructBindingVersion}")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
