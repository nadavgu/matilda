pluginManagement {
    plugins {
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        val androidPluginVersion: String by settings
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        id("com.android.application") version androidPluginVersion
        id("com.android.library") version androidPluginVersion
        id("org.jetbrains.kotlin.android") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "Matilda Agent"
include("library")
include("commands-generator")
include("commands-generator-api")
include("commands-generator-api-java")
include("commands-generator-api-common")
include("commands-generator-protos")
include("gradle-plugin")
include("test-plugin")
include("test-java-plugin")
include("test-java-plugin-android")