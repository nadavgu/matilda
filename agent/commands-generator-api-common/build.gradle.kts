import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

android {
    namespace = "org.matilda"
    compileSdk = 36

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val protobufVersion: String by project
val pbandkVersion: String by project
val kotlinInjectVersion: String by project
val daggerVersion: String by project

kotlin {
    jvm()
    androidTarget()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation("me.tatarka.inject:kotlin-inject-runtime:$kotlinInjectVersion")
                implementation("com.google.dagger:dagger:$daggerVersion")
                api("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
                api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }

        jvmTest {
            dependencies {
                implementation(project.dependencies.platform("org.junit:junit-bom:5.9.1"))
                implementation("org.junit.jupiter:junit-jupiter")
            }
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}