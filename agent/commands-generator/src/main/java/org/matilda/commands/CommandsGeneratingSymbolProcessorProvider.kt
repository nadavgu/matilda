package org.matilda.commands

import androidx.room.compiler.processing.ExperimentalProcessingApi
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider


@OptIn(ExperimentalProcessingApi::class)
class CommandsGeneratingSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment) = CommandsGeneratingKspAnnotationProcessor(environment)
}
