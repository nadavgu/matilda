package org.matilda.commands.di;

import dagger.Component;
import org.matilda.commands.CommandsGenerator;

@Component(modules = AnnotationProcessorModule.class)
public interface CommandsGeneratorComponent {
    CommandsGenerator commandsGenerator();
}
