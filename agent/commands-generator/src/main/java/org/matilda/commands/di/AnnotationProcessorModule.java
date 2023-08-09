package org.matilda.commands.di;

import dagger.Module;
import dagger.Provides;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@Module
public class AnnotationProcessorModule {
    private final Set<? extends TypeElement> mAnnotations;
    private final RoundEnvironment mRoundEnvironment;

    public AnnotationProcessorModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mAnnotations = annotations;
        mRoundEnvironment = roundEnv;
    }

    @Provides
    Set<? extends TypeElement> annotations() {
        return mAnnotations;
    }

    @Provides
    RoundEnvironment roundEnvironment() {
        return mRoundEnvironment;
    }
}
