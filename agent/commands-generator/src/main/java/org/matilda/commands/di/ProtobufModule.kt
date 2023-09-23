package org.matilda.commands.di

import dagger.Module
import dagger.Provides
import org.matilda.commands.protobuf.*
import org.matilda.commands.python.PythonProperties
import org.matilda.commands.types.*
import org.matilda.commands.utils.Package
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

@Module
class ProtobufModule {
    @Provides
    fun protobufLocations(processingEnvironment: ProcessingEnvironment) =
        ProtobufLocations(findFile(processingEnvironment, ProtobufLocations.PROJECT_PROTOBUF_DIR_OPTION),
            findFile(processingEnvironment, ProtobufLocations.API_PROTOBUF_DIR_OPTION),
            findFile(processingEnvironment, ProtobufLocations.GOOGLE_PROTOBUF_DIR_OPTION))

    private fun findFile(processingEnvironment: ProcessingEnvironment, option: String) =
        File(processingEnvironment.options[option]!!).also {
            if (!it.exists()) {
                throw RuntimeException("Specified file doesn't exist: $it")
            }
        }

    @Provides
    fun protobufTypeLocator(protobufLocations: ProtobufLocations,
                            pythonProperties: PythonProperties): ProtobufTypeLocator {
        return CachingTypeLocator(CompoundTypeLocator(listOf(
            pythonProperties.generatedProtobufPackage to
                    DirectoryProtobufTypeLocator(protobufLocations.projectProtobufDir),
            pythonProperties.generatedProtobufPackage to
                    DirectoryProtobufTypeLocator(protobufLocations.apiProtobufDir),
            Package("google", "protobuf") to DirectoryProtobufTypeLocator(protobufLocations.googleProtobufDir)
        )))
    }

    @Provides
    fun typeConverter(messageTypeConverter: MessageTypeConverter,
                      scalarTypeConverter: ScalarTypeConverter,
                      boxedTypeConverter: BoxedTypeConverter,
                      listTypeConverter: ListTypeConverter,
                      voidTypeConverter: VoidTypeConverter,
                      ): TypeConverter {
        return CompoundTypeConverter(listOf(
            messageTypeConverter,
            scalarTypeConverter,
            boxedTypeConverter,
            listTypeConverter,
            voidTypeConverter,
        ))
    }
}
