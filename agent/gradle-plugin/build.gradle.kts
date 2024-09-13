plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "2.0.20"
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    val matilda by plugins.creating {
        id = "org.matilda.gradle-plugin"
        implementationClass = "org.matilda.gradle.MatildaPlugin"
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
