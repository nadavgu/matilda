package org.matilda.commands.di

import dagger.Module
import dagger.Provides
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@Module
class AnnotationProcessorModule(
    private val mAnnotations: Set<TypeElement>, private val mRoundEnvironment: RoundEnvironment,
    private val mProcessingEnvironment: ProcessingEnvironment, private val mWasRun: Boolean
) {
    @Provides
    fun annotations(): Set<TypeElement> {
        return mAnnotations
    }

    @Provides
    fun roundEnvironment(): RoundEnvironment {
        return mRoundEnvironment
    }

    @Provides
    fun processingEnvironment(): ProcessingEnvironment {
        return mProcessingEnvironment
    }

    @Provides
    fun filer(): Filer {
        return mProcessingEnvironment.filer
    }

    @Provides
    fun types(): Types {
        return mProcessingEnvironment.typeUtils
    }

    @Provides
    fun elements(): Elements {
        return mProcessingEnvironment.elementUtils
    }

    @Provides
    fun wasRun(): Boolean {
        return mWasRun
    }
}
