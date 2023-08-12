plugins {
    java
    id("com.google.protobuf") version "0.9.4"
}

group = "org.matilda"
version = "unspecified"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.protobuf:protobuf-java:3.23.0")
}

tasks.test {
    useJUnitPlatform()
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()
val generatedProtoSubpackage = providers.gradleProperty("GENERATED_PROTO_SUBPACKAGE").get()
val generatedProtoPythonDir = pythonRootDir
    .dir(pythonGeneratedPackage.replace(".", File.separator))
    .dir(generatedProtoSubpackage.replace(".", File.separator))
val matildaProtoSubdir = providers.gradleProperty("MATILDA_PROTOS_SUBDIR").get()

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
                            from(File(getOutputDir(this@create), matildaProtoSubdir))
                            into(generatedProtoPythonDir)
                        }
                    }
                }
            }
        }
    }
}
