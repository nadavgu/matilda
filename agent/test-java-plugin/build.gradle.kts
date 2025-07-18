plugins {
    java
    application
    id("com.google.protobuf")
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = "tests.generated"
val pythonResourcesDir = pythonRootDir.dir("tests/resources").asFile
val protobufVersion: String by project
val daggerVersion: String by project

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":commands-generator-api"))
    compileOnly(project(":commands-generator-protos"))
    annotationProcessor(project(":commands-generator"))
    annotationProcessor("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
}

tasks.compileJava {
    options.compilerArgs.add("-ApythonRootDir=${pythonRootDir.asFile.absolutePath}")
    options.compilerArgs.add("-ApythonGeneratedPackage=$pythonGeneratedPackage")
    options.compilerArgs.add("-AprotobufDirs=${File(layout.buildDirectory.asFile.get(), "extracted-include-protos/main/").absolutePath}" +
            ":${File(projectDir, "src/main/proto/").absolutePath}")
    options.compilerArgs.add("-AjavaMainPackage=test")
}

tasks.test {
    useJUnitPlatform()
}

val packMergedJar = tasks.register<Jar>("packMergedJar") {
    from(tasks.jar.get().outputs.files.map { zipTree(it) })
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    destinationDirectory.set(pythonResourcesDir)
    archiveFileName.set("java-plugin.jar")
}

tasks.jar {
    finalizedBy(packMergedJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("org.matilda.template.TemplatePlugin")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    generateProtoTasks {
        all().configureEach {
            builtins {
                create("python") {
                    doLast {
                        copy {
                            from(getOutputDir(this@create))
                            into(pythonRootDir)
                        }
                    }
                }
            }
        }
    }
}