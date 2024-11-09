package org.matilda.commands

import androidx.room.compiler.processing.*
import androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor
import org.matilda.commands.di.AnnotationProcessorModule
import org.matilda.commands.di.DaggerCommandsGeneratorComponent
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.java.JavaProperties
import org.matilda.commands.protobuf.ProtobufLocations
import org.matilda.commands.python.PythonProperties
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.tools.Diagnostic

@ExperimentalProcessingApi
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.matilda.commands.MatildaService", "org.matilda.commands.MatildaCommand",
    "org.matilda.commands.MatildaDynamicService")
@SupportedOptions(
    PythonProperties.PYTHON_ROOT_DIR_OPTION,
    PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION,
    ProtobufLocations.PROTOBUF_DIRS_OPTION,
    JavaProperties.JAVA_MAIN_PACKAGE_OPTION,
)
class CommandsGeneratingAnnotationProcessor : JavacBasicAnnotationProcessor(), XProcessingStep {
    private var mProcessingEnvironment: XProcessingEnv? = null
    private var mWasRun = false
    @Synchronized
    override fun initialize(env: XProcessingEnv) {
        mProcessingEnvironment = env
    }

    override fun processingSteps(): Iterable<XProcessingStep> {
        return listOf(this)
    }

    override fun annotations(): Set<String> = setOf("org.matilda.commands.MatildaService",
        "org.matilda.commands.MatildaCommand",
        "org.matilda.commands.MatildaDynamicService")

    override fun preRound(env: XProcessingEnv, round: XRoundEnv) {
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
