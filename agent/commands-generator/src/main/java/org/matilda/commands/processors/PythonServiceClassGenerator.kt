package org.matilda.commands.processors

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.python.*
import org.matilda.commands.python.writer.*
import org.matilda.commands.types.TypeTranslator
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
            PythonClassSpec(getClassName(service), DEPENDENCY_CLASS.name)
        )
        addConstructor(pythonClass)
        addDICreator(pythonClass, service)
        service.commands.forEach { command ->
            addCommandImports(pythonFile, command)
            addCommandMethod(pythonClass, command)
        }
    }

    private fun addConstructor(pythonClass: PythonClass) {
        pythonClass.addInstanceMethod(
            PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name)
                .build()
        )
            .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
    }

    private fun addDICreator(pythonClass: PythonClass, service: ServiceInfo) {
        pythonClass.addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
                .returnTypeHint("'" + getClassName(service) + "'").build()
        )
            .addStatement(
                "return %s(%s.get(%s))", getClassName(service),
                DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name
            )
    }

    private fun addCommandMethod(pythonClass: PythonClass, command: CommandInfo) {
        pythonClass.addInstanceMethod(createCommandFunctionSpec(command))
            .addStatement("%s = Some()", SOME_PARAMETER_VARIABLE_NAME)
            .apply {
                command.parameters.forEach {
                    addParameterConversion(it)
                }
            }
            .addStatement("%s = %s.SerializeToString()", RAW_PARAMETER_VARIABLE_NAME, SOME_PARAMETER_VARIABLE_NAME)
            .addStatement(
                "%s = self.%s.run(%d, %s)", RAW_RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                mCommandIdGenerator.generate(command), RAW_PARAMETER_VARIABLE_NAME
            )
            .addStatement("%s = %s()", RETURN_VALUE_VARIABLE_NAME, getPythonType(command.returnType))
            .addStatement("%s.ParseFromString(%s)", RETURN_VALUE_VARIABLE_NAME, RAW_RETURN_VALUE_VARIABLE_NAME)
            .addStatement("return %s", RETURN_VALUE_VARIABLE_NAME)
    }

    private fun PythonCodeBlock.addParameterConversion(parameterInfo: ParameterInfo) {
        val parameterAnyName = getParameterAnyName(parameterInfo.name)
        addStatement("%s = Any()", parameterAnyName)
            .addStatement("%s.Pack(msg=%s)", parameterAnyName, parameterInfo.name)
            .addStatement("%s.any.append(%s)", SOME_PARAMETER_VARIABLE_NAME, parameterAnyName)
    }

    private fun getParameterAnyName(name: String) = "${name}_any"

    private fun createCommandFunctionSpec(command: CommandInfo): PythonFunctionSpec {
        val builder = PythonFunctionSpec.functionBuilder(command.name)
            .returnTypeHint(getPythonType(command.returnType))
        command.parameters.forEach {
            builder.addParameter(it.name, getPythonType(it.type))
        }
        return builder.build()
    }

    private fun getPythonType(typeMirror: TypeMirror): String {
        val typeName = TypeName.get(typeMirror)
        return if (typeName is ClassName) typeName.simpleName() else typeName.toString()
    }

    private fun addCommandImports(pythonFile: PythonFile, command: CommandInfo) {
        importPythonType(pythonFile, command.returnType)
        command.parameters.forEach { importPythonType(pythonFile, it.type) }
    }

    private fun importPythonType(pythonFile: PythonFile, typeMirror: TypeMirror) {
        val typeName = TypeName.get(typeMirror)
        if (typeName is ClassName) {
            pythonFile.addFromImport(mTypeTranslator.toPythonType(typeName))
        }
    }
    private fun getClassName(service: ServiceInfo) = mNameGenerator.forService(service).serviceClassName

    private fun addImports(pythonFile: PythonFile) {
        pythonFile.addFromImport(DEPENDENCY_CLASS)
            .addFromImport(DEPENDENCY_CONTAINER_CLASS)
            .addFromImport(COMMAND_RUNNER_CLASS)
            .addFromImport(ANY_CLASS)
            .addFromImport(mTypeTranslator.toPythonType(ClassName.get(Some::class.java)))
    }

    companion object {
        private const val COMMAND_RUNNER_FIELD_NAME = "__command_runner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "command_runner"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val RAW_PARAMETER_VARIABLE_NAME = "raw_parameter"
        private const val SOME_PARAMETER_VARIABLE_NAME = "some_parameter"
        private const val RAW_RETURN_VALUE_VARIABLE_NAME = "raw_return_value"
        private const val RETURN_VALUE_VARIABLE_NAME = "return_value"
    }
}
