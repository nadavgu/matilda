plugins {
    java
    application
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

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

application {
    mainClass.set("org.matilda.Main")
}