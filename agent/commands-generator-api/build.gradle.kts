plugins {
    `java-library`
    id("com.google.protobuf") version "0.9.4"
    `maven-publish`
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

dependencies {
    api("com.google.dagger:dagger:2.47")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    api("com.google.protobuf:protobuf-java:3.23.0")
}

tasks.test {
    useJUnitPlatform()
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()

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
                            into(pythonRootDir)
                        }
                    }
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
