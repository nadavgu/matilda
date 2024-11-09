package org.matilda.commands

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XRoundEnv
import org.matilda.commands.di.AnnotationProcessorModule
import org.matilda.commands.di.DaggerCommandsGeneratorComponent
import org.matilda.commands.exceptions.AnnotationProcessingException
import javax.tools.Diagnostic

@OptIn(ExperimentalProcessingApi::class)
class CommandsGeneratingDelegateAnnotationProcessor {
    private var mProcessingEnvironment: XProcessingEnv? = null
    private var mWasRun = false
    @Synchronized
    fun initialize(env: XProcessingEnv) {
        mProcessingEnvironment = env
    }

    fun processingSteps(): Iterable<XProcessingStep> {
        return listOf(object: XProcessingStep {
            override fun annotations(): Set<String> = setOf("org.matilda.commands.MatildaService",
                "org.matilda.commands.MatildaCommand",
                "org.matilda.commands.MatildaDynamicService")
        })
    }

    fun preRound(env: XProcessingEnv, round: XRoundEnv) {
        try {
            val component = DaggerCommandsGeneratorComponent.builder()
                .annotationProcessorModule(AnnotationProcessorModule(env, round, mWasRun))
                .build()
            component.commandsGenerator().generate()
            mWasRun = true
        } catch (e: AnnotationProcessingException) {
            e.printStackTrace()
            env.messager.printMessage(Diagnostic.Kind.ERROR, e.message ?: "", e.element)
        } catch (e: Throwable) {
            e.printStackTrace()
            env.messager.printMessage(Diagnostic.Kind.ERROR, e.message ?: "")
        }
    }
}
