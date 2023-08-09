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
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}