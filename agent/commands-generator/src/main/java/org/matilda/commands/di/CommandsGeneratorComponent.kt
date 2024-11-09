package org.matilda.commands.di

import androidx.room.compiler.processing.ExperimentalProcessingApi
import dagger.Component
import org.matilda.commands.CommandsGenerator

@OptIn(ExperimentalProcessingApi::class)
@Component(modules = [AnnotationProcessorModule::class, ProcessorsModule::class,
    PythonModule::class, ProtobufModule::class, JavaModule::class])
interface CommandsGeneratorComponent {
    fun commandsGenerator(): CommandsGenerator
}
