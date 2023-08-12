package org.matilda.commands.di

import dagger.Module
import dagger.Provides
import org.matilda.commands.protobuf.ProtobufLocations
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

@Module
class ProtobufModule {
    @Provides
    fun pythonProperties(processingEnvironment: ProcessingEnvironment) =
        ProtobufLocations(findFile(processingEnvironment, ProtobufLocations.PROJECT_PROTOBUF_DIR_OPTION),
            findFile(processingEnvironment, ProtobufLocations.GOOGLE_PROTOBUF_DIR_OPTION))

    private fun findFile(processingEnvironment: ProcessingEnvironment, option: String) =
        File(processingEnvironment.options[option]!!).also {
            if (!it.exists()) {
                throw RuntimeException("Specified file doesn't exist: $it")
            }
        }
}
