plugins {
    java
}

group = "org.matilda"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    doLast {
        outputs.files.forEach { outputFile ->
            copy {
                from(outputFile)
                into(rootProject.layout.projectDirectory.dir(providers.gradleProperty("RESOURCES_DIR_PATH")))
                rename {"agent.jar"}
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}