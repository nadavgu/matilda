package org.matilda.commands;

import org.matilda.commands.di.CommandsGeneratorComponent;
import org.matilda.commands.di.DaggerCommandsGeneratorComponent;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.matilda.commands.MatildaService", "org.matilda.commands.MatildaCommand"})
public class CommandsGenerator extends AbstractProcessor {
    private final CommandsGeneratorComponent mComponent;
    public CommandsGenerator() {
        mComponent = DaggerCommandsGeneratorComponent.create();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println(annotations);
        return false;
    }
}
