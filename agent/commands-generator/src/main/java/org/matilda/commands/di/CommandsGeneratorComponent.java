package org.matilda.commands.di;

import dagger.Component;
import org.matilda.commands.CommandsGenerator;

@Component
public interface CommandsGeneratorComponent {
    CommandsGenerator commandsGenerator();
}
