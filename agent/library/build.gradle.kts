plugins {
    id("java")
}

group = "org.matilda"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    doLast {
        outputs.files.forEach { outputFile ->
            copy {
                from(outputFile)
                into(rootProject.layout.projectDirectory.dir(providers.gradleProperty("RESOURCES_DIR_PATH")))
            }
        }
    }
}