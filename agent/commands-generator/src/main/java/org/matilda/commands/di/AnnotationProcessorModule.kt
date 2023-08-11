package org.matilda.commands.di;

import dagger.Module;
import dagger.Provides;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

@Module
public class AnnotationProcessorModule {
    private final Set<? extends TypeElement> mAnnotations;
    private final RoundEnvironment mRoundEnvironment;

    private final ProcessingEnvironment mProcessingEnvironment;
    private final boolean mWasRun;

    public AnnotationProcessorModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv,
                                     ProcessingEnvironment processingEnvironment, boolean wasRun) {
        mAnnotations = annotations;
        mRoundEnvironment = roundEnv;
        mProcessingEnvironment = processingEnvironment;
        mWasRun = wasRun;
    }

    @Provides
    Set<? extends TypeElement> annotations() {
        return mAnnotations;
    }

    @Provides
    RoundEnvironment roundEnvironment() {
        return mRoundEnvironment;
    }

    @Provides
    ProcessingEnvironment processingEnvironment() {
        return mProcessingEnvironment;
    }

    @Provides
    Filer filer() {
        return mProcessingEnvironment.getFiler();
    }

    @Provides
    Types types() {
        return mProcessingEnvironment.getTypeUtils();
    }

    @Provides
    Elements elements() {
        return mProcessingEnvironment.getElementUtils();
    }

    @Provides
    boolean wasRun() {
        return mWasRun;
    }
}
