package org.matilda.commands.di

import dagger.Module
import dagger.Provides
import org.matilda.commands.python.PythonProperties
import org.matilda.commands.python.writer.PythonFileWriter
import org.matilda.commands.utils.Package
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

@Module
class PythonModule {
    @Provides
    fun pythonProperties(processingEnvironment: ProcessingEnvironment): PythonProperties {
        val pythonRootDir = File(processingEnvironment.options[PythonProperties.PYTHON_ROOT_DIR_OPTION]!!)
        if (!pythonRootDir.exists()) {
            throw RuntimeException("Specified python directory doesn't exist: $pythonRootDir")
        }
        val pythonGeneratedPackage =
            Package.fromString(processingEnvironment.options[PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION]!!)
        val generatedProtobufPackage = pythonGeneratedPackage.subpackage(
            processingEnvironment.options[PythonProperties.GENERATED_PROTO_SUBPACKAGE_OPTION]!!)
        return PythonProperties(pythonRootDir, pythonGeneratedPackage, generatedProtobufPackage)
    }

    @Provides
    fun pythonFileWriter(pythonProperties: PythonProperties) = PythonFileWriter(pythonProperties.pythonRootDir)
}
