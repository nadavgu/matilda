package org.matilda.commands;

import org.matilda.commands.di.AnnotationProcessorModule;
import org.matilda.commands.di.CommandsGeneratorComponent;
import org.matilda.commands.di.DaggerCommandsGeneratorComponent;
import org.matilda.commands.exceptions.AnnotationProcessingException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

import static org.matilda.commands.python.PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION;
import static org.matilda.commands.python.PythonProperties.PYTHON_ROOT_DIR_OPTION;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.matilda.commands.MatildaService", "org.matilda.commands.MatildaCommand"})
@SupportedOptions({PYTHON_ROOT_DIR_OPTION, PYTHON_GENERATED_PACKAGE_OPTION})
public class CommandsGeneratingAnnotationProcessor extends AbstractProcessor {
    private ProcessingEnvironment mProcessingEnvironment;
    private boolean mWasRun = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        mProcessingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            CommandsGeneratorComponent component = DaggerCommandsGeneratorComponent.builder()
                    .annotationProcessorModule(new AnnotationProcessorModule(annotations, roundEnv,
                            mProcessingEnvironment, mWasRun))
                    .build();
            component.commandsGenerator().generate();
            mWasRun = true;
        } catch (AnnotationProcessingException e) {
            e.printStackTrace();
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.element);
        } catch (Throwable e) {
            e.printStackTrace();
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return false;
    }
}
