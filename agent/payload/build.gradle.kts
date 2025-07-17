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
    implementation(project(":library", "jvmRuntimeElements"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()

val packMergedJar = tasks.register<Jar>("packMergedJar") {
    from(tasks.jar.get().outputs.files.map { zipTree(it) })
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    destinationDirectory.set(pythonRootDir.dir(providers.gradleProperty("RESOURCES_SUBDIR")))
    archiveFileName.set("agent.jar")
}

tasks.jar {
    finalizedBy(packMergedJar)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.matilda.Main")
}