package org.matilda.commands.di

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import dagger.Module
import dagger.Provides

@ExperimentalProcessingApi
@Module
class AnnotationProcessorModule(
    private val mProcessingEnvironment: XProcessingEnv,
    private val mRoundEnvironment: XRoundEnv,
    private val mWasRun: Boolean
) {
    @Provides
    fun roundEnvironment() = mRoundEnvironment

    @Provides
    fun processingEnvironment() = mProcessingEnvironment

    @Provides
    fun filer(): XFiler = mProcessingEnvironment.filer

    @Provides
    fun wasRun() = mWasRun
}
