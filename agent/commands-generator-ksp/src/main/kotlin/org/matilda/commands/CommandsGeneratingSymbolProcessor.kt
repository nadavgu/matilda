package org.matilda.commands

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import javax.inject.Inject

class CommandsGeneratingSymbolProcessor @Inject constructor() : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        TODO("Not yet implemented")
    }

}
