import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commands-generator-api"))
    implementation("com.google.dagger:dagger-compiler:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
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
