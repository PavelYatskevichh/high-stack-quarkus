pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.flywaydb.flyway" -> {
                    val flywayPluginVersion: String by settings
                    useVersion(flywayPluginVersion)
                }
            }
        }
    }
}

rootProject.name="high-stack-quarkus"
include("content-creation")
