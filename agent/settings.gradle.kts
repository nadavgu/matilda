pluginManagement {
    plugins {
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
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
include("gradle-plugin")
include("commands-generator-ksp")
