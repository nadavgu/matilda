package org.matilda.commands.di

import dagger.Module
import dagger.Provides
import org.matilda.commands.java.JavaProperties
import org.matilda.commands.utils.Package
import org.matilda.commands.utils.option
import javax.annotation.processing.ProcessingEnvironment

@Module
class JavaModule {
    @Provides
    fun javaProperties(processingEnvironment: ProcessingEnvironment): JavaProperties {
        val javaMainPackage = Package.fromString(processingEnvironment.option(JavaProperties.JAVA_MAIN_PACKAGE_OPTION))
        return JavaProperties(javaMainPackage)
    }
}
