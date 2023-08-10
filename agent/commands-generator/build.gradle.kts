plugins {
    id("java")
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
    implementation("com.squareup:javapoet:1.10.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.protobuf:protobuf-java:3.23.0")
}

tasks.test {
    useJUnitPlatform()
}