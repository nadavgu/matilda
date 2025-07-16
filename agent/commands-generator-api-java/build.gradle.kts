import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

val protobufVersion: String by project
val pbandkVersion: String by project

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    ksp("com.google.dagger:dagger-compiler:2.51.1")
    implementation("com.google.dagger:dagger:2.51.1")
    api("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    implementation(project(":commands-generator-api-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
