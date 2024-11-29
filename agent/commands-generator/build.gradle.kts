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
    google()
}

val kspVersion: String by project
val pbandkVersion: String by project

dependencies {
    implementation(project(":commands-generator-api"))
    implementation("com.google.dagger:dagger:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")
    implementation("me.tatarka.inject:kotlin-inject-runtime:0.7.2")
    implementation("com.squareup:javapoet:1.10.0")
    implementation("com.squareup:kotlinpoet:2.0.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
    implementation("androidx.room:room-compiler-processing:2.6.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

tasks.test {
    useJUnitPlatform()
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
