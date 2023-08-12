package org.matilda.commands.processors

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.*
import org.matilda.commands.python.writer.*
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonServiceClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mTypeTranslator: TypeTranslator

    override fun process(instance: ServiceInfo) {
        val pythonFile = PythonFile(mNameGenerator.forService(instance).pythonGeneratedServicePackage)
        addImports(pythonFile)
        addClass(pythonFile, instance)
        mPythonFileWriter.write(pythonFile)
    }

    private fun addClass(pythonFile: PythonFile, service: ServiceInfo) {
        val pythonClass = pythonFile.newClass(
            PythonClassSpec(getClassName(service), DEPENDENCY_CLASS.className)
        )
        addConstructor(pythonClass)
        addDICreator(pythonClass, service)
        service.commands.forEach { command -> addCommandMethod(pythonFile, pythonClass, command) }
    }

    private fun addConstructor(pythonClass: PythonClass) {
        pythonClass.addInstanceMethod(
            PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.className)
                .build()
        )
            .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
    }

    private fun addDICreator(pythonClass: PythonClass, service: ServiceInfo) {
        pythonClass.addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.className)
                .returnTypeHint("'" + getClassName(service) + "'").build()
        )
            .addStatement(
                "return %s(%s.get(%s))", getClassName(service),
                DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.className
            )
    }

    private fun addCommandMethod(pythonFile: PythonFile, pythonClass: PythonClass, command: CommandInfo) {
        val parameterType = getPythonType(pythonFile, command.parameterType)
        val returnType = getPythonType(pythonFile, command.returnType)
        pythonClass.addInstanceMethod(
            PythonFunctionSpec.functionBuilder(command.name)
                .addParameter(PARAMETER_VARIABLE_NAME, parameterType)
                .returnTypeHint(returnType)
                .build()
        )
            .addStatement("%s = Any()", ANY_PARAMETER_VARIABLE_NAME)
            .addStatement("%s.Pack(msg=%s)", ANY_PARAMETER_VARIABLE_NAME, PARAMETER_VARIABLE_NAME)
            .addStatement("%s = %s.SerializeToString()", RAW_PARAMETER_VARIABLE_NAME, ANY_PARAMETER_VARIABLE_NAME)
            .addStatement(
                "%s = self.%s.run(%d, %s)", RAW_RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                mCommandIdGenerator.generate(command), RAW_PARAMETER_VARIABLE_NAME
            )
            .addStatement("%s = %s()", RETURN_VALUE_VARIABLE_NAME, returnType)
            .addStatement("%s.ParseFromString(%s)", RETURN_VALUE_VARIABLE_NAME, RAW_RETURN_VALUE_VARIABLE_NAME)
            .addStatement("return %s", RETURN_VALUE_VARIABLE_NAME)
    }

    private fun getPythonType(pythonFile: PythonFile, typeMirror: TypeMirror): String {
        val typeName = TypeName.get(typeMirror)
        return if (typeName is ClassName) {
            pythonFile.addFromImport(mTypeTranslator.toPythonType(typeName))
            typeName.simpleName()
        } else typeName.toString()
    }

    private fun getClassName(service: ServiceInfo) = mNameGenerator.forService(service).serviceClassName

    companion object {
        private const val COMMAND_RUNNER_FIELD_NAME = "__command_runner"
        private fun addImports(pythonFile: PythonFile) {
            pythonFile.addFromImport(DEPENDENCY_CLASS)
                .addFromImport(DEPENDENCY_CONTAINER_CLASS)
                .addFromImport(COMMAND_RUNNER_CLASS)
                .addFromImport(ANY_CLASS)
        }

        private const val COMMAND_RUNNER_PARAMETER_NAME = "command_runner"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val PARAMETER_VARIABLE_NAME = "parameter"
        private const val RAW_PARAMETER_VARIABLE_NAME = "raw_parameter"
        private const val ANY_PARAMETER_VARIABLE_NAME = "any_parameter"
        private const val RAW_RETURN_VALUE_VARIABLE_NAME = "raw_return_value"
        private const val RETURN_VALUE_VARIABLE_NAME = "return_value"
    }
}
