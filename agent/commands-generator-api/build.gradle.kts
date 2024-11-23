plugins {
    `java-library`
    id("com.google.protobuf") version "0.9.4"
    kotlin("jvm")
    id("com.google.devtools.ksp")
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

val protobufVersion: String by project
val pbandkVersion: String by project

dependencies {
    implementation("me.tatarka.inject:kotlin-inject-runtime:0.7.2")
    implementation("com.google.dagger:dagger:2.52")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    ksp("com.google.dagger:dagger-compiler:2.52")
    ksp("me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    api("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
    protobuf(project(":commands-generator-protos"))
}

tasks.test {
    useJUnitPlatform()
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        create("pbandk") {
            artifact = "pro.streem.pbandk:protoc-gen-pbandk-jvm:$pbandkVersion:jvm8@jar"
        }
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

                remove(findByName("java"))
            }

            plugins {
                create("pbandk") {
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
