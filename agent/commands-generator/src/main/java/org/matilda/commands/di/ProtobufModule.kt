package org.matilda.commands.di

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import dagger.Module
import dagger.Provides
import org.matilda.commands.protobuf.*
import org.matilda.commands.types.*
import org.matilda.commands.utils.option
import java.io.File

@OptIn(ExperimentalProcessingApi::class)
@Module
class ProtobufModule {
    @Provides
    fun protobufLocations(processingEnvironment: XProcessingEnv) =
        ProtobufLocations(processingEnvironment.option(ProtobufLocations.PROTOBUF_DIRS_OPTION)
            .split(":")
            .map { File(it) }
        )

    @Provides
    fun protobufTypeLocator(protobufLocations: ProtobufLocations): ProtobufTypeLocator {
        return CachingTypeLocator(CompoundTypeLocator(protobufLocations.locations.map {
            DirectoryProtobufTypeLocator(it)
        }))
    }

    @Provides
    fun typeConverter(messageTypeConverter: MessageTypeConverter,
                      scalarTypeConverter: ScalarTypeConverter,
                      listTypeConverter: ListTypeConverter,
                      voidTypeConverter: VoidTypeConverter,
                      dynamicServiceTypeConverter: DynamicServiceTypeConverter,
    ): TypeConverter {
        return CompoundTypeConverter(listOf(
            messageTypeConverter,
            scalarTypeConverter,
            listTypeConverter,
            voidTypeConverter,
            dynamicServiceTypeConverter,
        ))
    }
}
