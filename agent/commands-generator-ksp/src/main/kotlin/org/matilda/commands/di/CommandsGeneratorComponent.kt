package org.matilda.commands.di

import org.matilda.commands.CommandsGeneratingSymbolProcessor
import dagger.Component

@Component
interface CommandsGeneratorComponent {
    fun symbolProcessor(): CommandsGeneratingSymbolProcessor
}
