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
    mavenLocal()
}

val kspVersion: String by project
val pbandkVersion: String by project
val kotlinInjectVersion: String by project
val daggerVersion: String by project

dependencies {
    implementation(project(":commands-generator-api", "jvmRuntimeElements"))
    implementation("com.google.dagger:dagger:$daggerVersion")
    ksp("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("me.tatarka.inject:kotlin-inject-runtime:$kotlinInjectVersion")
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
