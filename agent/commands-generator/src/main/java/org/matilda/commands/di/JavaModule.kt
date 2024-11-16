package org.matilda.commands.di

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import dagger.Module
import dagger.Provides
import org.matilda.commands.java.JavaProperties
import org.matilda.commands.utils.Package
import org.matilda.commands.utils.option

@OptIn(ExperimentalProcessingApi::class)
@Module
class JavaModule {
    @Provides
    fun javaProperties(processingEnvironment: XProcessingEnv): JavaProperties {
        val javaMainPackage = Package.fromString(processingEnvironment.option(JavaProperties.JAVA_MAIN_PACKAGE_OPTION))
        val shouldGenerateKotlin =
            processingEnvironment.options[JavaProperties.SHOULD_GENERATE_KOTLIN]?.toBoolean() ?: false
        return JavaProperties(javaMainPackage, shouldGenerateKotlin)
    }

    @Provides
    fun diFramework(processingEnvironment: XProcessingEnv, javaProperties: JavaProperties): DiFrameWork {
        val diFramework = processingEnvironment.options[JavaProperties.DI_FRAMEWORK] ?: return DiFrameWork.Dagger
        return when (diFramework.lowercase()) {
            "dagger" -> DiFrameWork.Dagger
            "kotlininject" -> DiFrameWork.KotlinInject.also {
                if (!javaProperties.shouldGenerateKotlin) {
                    throw IllegalArgumentException("Kotlin is required for KotlinInject DI framework")
                }
            }
            else -> throw IllegalArgumentException("Invalid DI framework: $diFramework")
        }
    }
}
