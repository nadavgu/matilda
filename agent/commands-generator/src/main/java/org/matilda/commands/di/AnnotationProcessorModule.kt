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
    fun annotations() = mAnnotations

    @Provides
    fun roundEnvironment() = mRoundEnvironment

    @Provides
    fun processingEnvironment() = mProcessingEnvironment

    @Provides
    fun filer(): Filer = mProcessingEnvironment.filer

    @Provides
    fun types(): Types = mProcessingEnvironment.typeUtils

    @Provides
    fun elements(): Elements = mProcessingEnvironment.elementUtils

    @Provides
    fun wasRun() = mWasRun
}
