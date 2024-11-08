package org.matilda.commands

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

import org.matilda.commands.di.DaggerCommandsGeneratorComponent

class CommandsGeneratingSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DaggerCommandsGeneratorComponent.builder()
            .build()
            .symbolProcessor()
    }
}