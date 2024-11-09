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
        return JavaProperties(javaMainPackage)
    }
}
