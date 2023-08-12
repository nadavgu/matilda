plugins {
    java
    id("com.google.protobuf") version "0.9.4"
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()
val generatedProtoSubpackage = providers.gradleProperty("GENERATED_PROTO_SUBPACKAGE").get()

tasks.compileJava {
    options.compilerArgs.add("-ApythonRootDir=${pythonRootDir.asFile.absolutePath}")
    options.compilerArgs.add("-ApythonGeneratedPackage=$pythonGeneratedPackage")
    options.compilerArgs.add("-AgeneratedProtoSubpackage=$generatedProtoSubpackage")
    options.compilerArgs.add("-AgoogleProtobufDir=${File(buildDir, "extracted-include-protos/main").absolutePath}")
    options.compilerArgs.add("-AprojectProtobufDir=${File(projectDir, "src/main/proto").absolutePath}")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.23.0")
    implementation("com.google.dagger:dagger:2.47")
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")
    annotationProcessor(project(":commands-generator"))
    implementation(project(":commands-generator-api"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

val generatedProtoPythonDir = pythonRootDir
    .dir(pythonGeneratedPackage.replace(".", File.separator))
    .dir(generatedProtoSubpackage.replace(".", File.separator))

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.23.0"
    }

    generateProtoTasks {
        all().configureEach {
            builtins {
                create("python") {
                    doLast {
                        copy {
                            from(getOutputDir(this@create))
                            into(generatedProtoPythonDir)
                        }
                    }
                }
            }
        }
    }
}
