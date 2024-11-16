plugins {
    java
    id("com.google.protobuf") version "0.9.4"
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()

ksp {
    arg("pythonRootDir", pythonRootDir.asFile.absolutePath)
    arg("pythonGeneratedPackage", pythonGeneratedPackage)
    arg("protobufDirs", File(buildDir, "extracted-include-protos/main/").absolutePath +
            ":${File(projectDir, "src/main/proto/").absolutePath}")
    arg("javaMainPackage", "org.matilda")
    arg("generateKotlin", "true")
    arg("diFramework", "kotlinInject")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.25.5")
    ksp("me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    implementation("me.tatarka.inject:kotlin-inject-runtime:0.7.2")
    ksp(project(":commands-generator"))
    implementation(project(":commands-generator-api"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.25.5"
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
