package org.matilda.commands

import androidx.room.compiler.processing.*
import androidx.room.compiler.processing.ksp.KspBasicAnnotationProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import org.matilda.commands.java.JavaProperties
import org.matilda.commands.protobuf.ProtobufLocations
import org.matilda.commands.python.PythonProperties
import javax.annotation.processing.*
import javax.lang.model.SourceVersion

@ExperimentalProcessingApi
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.matilda.commands.MatildaService", "org.matilda.commands.MatildaCommand",
    "org.matilda.commands.MatildaDynamicService")
@SupportedOptions(
    PythonProperties.PYTHON_ROOT_DIR_OPTION,
    PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION,
    ProtobufLocations.PROTOBUF_DIRS_OPTION,
    JavaProperties.JAVA_MAIN_PACKAGE_OPTION,
    JavaProperties.SHOULD_GENERATE_KOTLIN,
    JavaProperties.DI_FRAMEWORK,
)
class CommandsGeneratingKspAnnotationProcessor(symbolProcessorEnvironment: SymbolProcessorEnvironment) :
    KspBasicAnnotationProcessor(symbolProcessorEnvironment) {
    private var mDelegate = CommandsGeneratingDelegateAnnotationProcessor()

    @Synchronized
    override fun initialize(env: XProcessingEnv) {
        mDelegate.initialize(env)
    }

    override fun processingSteps() = mDelegate.processingSteps()

    override fun preRound(env: XProcessingEnv, round: XRoundEnv) {
        mDelegate.preRound(env, round)
    }
}
