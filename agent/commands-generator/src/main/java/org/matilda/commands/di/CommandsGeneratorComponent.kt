package org.matilda.commands.di

import dagger.Component
import org.matilda.commands.CommandsGenerator

@Component(modules = [AnnotationProcessorModule::class, ProcessorsModule::class,
    PythonModule::class, ProtobufModule::class])
interface CommandsGeneratorComponent {
    fun commandsGenerator(): CommandsGenerator
}
