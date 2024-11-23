pluginManagement {
    plugins {
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "Matilda Agent"
include("library")
include("payload")
include("commands-generator")
include("commands-generator-api")
include("commands-generator-protos")
include("gradle-plugin")
