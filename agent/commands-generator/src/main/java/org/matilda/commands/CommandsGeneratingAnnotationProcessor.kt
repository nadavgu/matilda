package org.matilda.commands

import org.matilda.commands.di.AnnotationProcessorModule
import org.matilda.commands.di.DaggerCommandsGeneratorComponent
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.protobuf.ProtobufLocations
import org.matilda.commands.python.PythonProperties
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.matilda.commands.MatildaService", "org.matilda.commands.MatildaCommand",
    "org.matilda.commands.MatildaDynamicService")
@SupportedOptions(
    PythonProperties.PYTHON_ROOT_DIR_OPTION,
    PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION,
    PythonProperties.GENERATED_PROTO_SUBPACKAGE_OPTION,
    ProtobufLocations.PROJECT_PROTOBUF_DIR_OPTION,
    ProtobufLocations.API_PROTOBUF_DIR_OPTION,
    ProtobufLocations.GOOGLE_PROTOBUF_DIR_OPTION,
)
class CommandsGeneratingAnnotationProcessor : AbstractProcessor() {
    private var mProcessingEnvironment: ProcessingEnvironment? = null
    private var mWasRun = false
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        mProcessingEnvironment = processingEnv
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            val component = DaggerCommandsGeneratorComponent.builder()
                .annotationProcessorModule(AnnotationProcessorModule(annotations, roundEnv,
                    mProcessingEnvironment!!, mWasRun))
                .build()
            component.commandsGenerator().generate()
            mWasRun = true
        } catch (e: AnnotationProcessingException) {
            e.printStackTrace()
            mProcessingEnvironment!!.messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element)
        } catch (e: Throwable) {
            e.printStackTrace()
            mProcessingEnvironment!!.messager.printMessage(Diagnostic.Kind.ERROR, e.message)
        }
        return false
    }
}
