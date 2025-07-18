plugins {
    java
    id("com.google.protobuf")
    `maven-publish`
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

val protobufVersion: String by project

dependencies {
    compileOnly("com.google.protobuf:protobuf-java:$protobufVersion")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileJava {
        // The protobuf gradle plugin requires this project to apply the `java-library` plugin. But since we're only
        // generating Kotlin code, we need to disable the `compileJava` task. Otherwise gradle will complain that there
        // is no Java code available to compile.
        enabled = false
    }
}
