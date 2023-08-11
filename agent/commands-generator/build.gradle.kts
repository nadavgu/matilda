import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
}

group = "org.matilda"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commands-generator-api"))
    implementation("com.google.dagger:dagger:2.47")
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")
    kapt("com.google.dagger:dagger-compiler:2.47")
    implementation("com.squareup:javapoet:1.10.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.protobuf:protobuf-java:3.23.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}