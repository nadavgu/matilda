@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



interface KspDependencies {
    fun ksp(dependencyNotation: Any)
}

fun KotlinMultiplatformExtension.commonMainKspDependencies(
    project: Project,
    block: KspDependencies.() -> Unit,
) {
    project.dependencies {
        object : KspDependencies {
            override fun ksp(dependencyNotation: Any) {
                add("kspCommonMainMetadata", dependencyNotation)
            }
        }.block()
    }

    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
        if(name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

plugins {
    id("com.android.library")
    id("com.google.protobuf") version "0.9.4"
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = "tests.generated"
val pythonResourcesDir = pythonRootDir.dir("tests/resources").asFile
val protobufVersion: String by project
val pbandkVersion: String by project
val kotlinInjectVersion: String by project

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

android {
    namespace = "org.matilda"
    compileSdk = 36
}

kotlin {
    jvm {
        mainRun {
            mainClass.set("org.matilda.template.TemplatePlugin")
        }
    }

    androidTarget()

    linuxX64 {
        binaries {
            sharedLib(buildTypes = listOf(DEBUG)) {
                outputDirectory = pythonResourcesDir
                baseName = "plugin-linuxX64.so"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commands-generator-api"))
                implementation("me.tatarka.inject:kotlin-inject-runtime:$kotlinInjectVersion")
                implementation("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(project.dependencies.platform("org.junit:junit-bom:5.9.1"))
                implementation("org.junit.jupiter:junit-jupiter")
            }
        }
    }

    commonMainKspDependencies(project) {
        ksp(project(":commands-generator"))
        ksp("me.tatarka.inject:kotlin-inject-compiler-ksp:$kotlinInjectVersion")
    }
}

dependencies {
    compileOnly(project(":commands-generator-protos"))
}

ksp {
    arg("pythonRootDir", pythonRootDir.asFile.absolutePath)
    arg("pythonGeneratedPackage", pythonGeneratedPackage)
    arg("protobufDirs",
        File(layout.buildDirectory.asFile.get(), "extracted-include-protos/debug/").absolutePath + ":"
                + File(projectDir, "src/main/proto/").absolutePath
    )
    arg("javaMainPackage", "org.matilda.template")
    arg("generateKotlin", "true")
    arg("diFramework", "kotlinInject")
}

tasks.named<Jar>("jvmJar") {
    from({
        configurations.getByName("jvmRuntimeClasspath").filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    doLast {
        outputs.files.forEach { outputFile ->
            copy {
                from(outputFile)
                into(pythonResourcesDir)
                rename {"plugin.jar"}
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}

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
        // Only generate sources for debug, to prevent non-flavored from having duplicated sources
        ofBuildType("debug").matching { !it.isTestVariant }.forEach { task ->
            task.builtins {
                create("python") {
                    task.doLast {
                        copy {
                            from(task.getOutputDir(this@create))
                            into(pythonRootDir)
                        }
                    }
                }

                findByName("java")?.also {
                    remove(it)
                }
            }
            task.plugins {
                create("pbandk") {
                    // Publish the code generated by pbandk as part of the current Kotlin Multiplatform project's
                    // `commonMain` source set. This allows other Kotlin Multiplatform sub-projects to consume the
                    // pbandk-generated Kotlin code using a regular gradle project dependency.
                    val outputDir = task.getOutputDir(this)
                    project.kotlin.sourceSets.commonMain.configure {
                        // `builtBy` ensures that gradle will automatically run the `generateProto` task before trying
                        // to compile the generated Kotlin code
                        kotlin.srcDir(project.files(outputDir).builtBy(task))
                    }
                }
            }
        }
    }
}