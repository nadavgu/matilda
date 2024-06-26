package org.matilda.commands.processors

import com.squareup.javapoet.ClassName
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.python.*
import org.matilda.commands.python.writer.*
import org.matilda.commands.types.*
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.PYTHON_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonServiceProxyClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mProtobufTypeTranslator: ProtobufTypeTranslator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        val pythonFile = PythonFile(mNameGenerator.forService(instance).serviceProxyClassName.packageName)
            .addImports(instance)
            .addClass(instance)
        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addClass(service: ServiceInfo) = apply {
        val pythonClass = newClass(PythonClassSpec(getClassName(service),
            mNameGenerator.forService(service).serviceFullClassName.name))
            .addConstructor(service)
            .addDICreator(service)

        service.commands.forEach { command ->
            addCommandImports(command)
            pythonClass.addCommandMethod(command)
        }
    }

    private fun PythonClass.addConstructor(service: ServiceInfo) = apply {
        addInstanceMethod(
            PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name)
                .addParameter(PYTHON_DEPENDENCIES_PARAMETER_NAME, dependenciesPythonClassName(service).name)
                .addParameter(COMMAND_REGISTRY_ID_PARAMETER_NAME, pythonOptionalType(PythonTypeName.INT).name,
                    "None")
                .build()
        )
            .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
            .addStatement("self.%s = %s", PYTHON_DEPENDENCIES_FIELD_NAME, PYTHON_DEPENDENCIES_PARAMETER_NAME)
            .addStatement("self.%s = %s", COMMAND_REGISTRY_ID_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME)
    }

    private fun PythonClass.addDICreator(service: ServiceInfo) = apply {
        addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
                .returnTypeHint("'" + getClassName(service) + "'").build()
        )
            .addStatement(
                "return %s(%s.get(%s), %s.get(%s))", getClassName(service),
                DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, dependenciesPythonClassName(service).name,
            )
    }

    private fun PythonClass.addCommandMethod(command: CommandInfo) {
        addInstanceMethod(createCommandFunctionSpec(command))
            .addStatement("%s = Some()", SOME_PARAMETER_VARIABLE_NAME)
            .apply {
                command.parameters.forEach {
                    addParameterConversion(it)
                }
            }
            .addStatement("%s = %s.SerializeToString()", RAW_PARAMETER_VARIABLE_NAME, SOME_PARAMETER_VARIABLE_NAME)
            .addStatement(
                "%s = self.%s.run(%d, %s, %s=self.%s)", RAW_RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                mCommandIdGenerator.generate(command), RAW_PARAMETER_VARIABLE_NAME,
                COMMAND_REGISTRY_ID_PARAMETER_NAME, COMMAND_REGISTRY_ID_FIELD_NAME
            )
            .addReturnStatement(command.returnType)
    }

    private fun PythonCodeBlock.addParameterConversion(parameterInfo: ParameterInfo) {
        val protobufParameterName = getParameterWrapperName(parameterInfo.pythonName)
        val parameterAnyName = getParameterAnyName(parameterInfo.pythonName)
        val (converter, _) = mTypeConverter.pythonConverter(parameterInfo.type)
        addStatement("%s = %s.to_protobuf(%s)", getParameterWrapperName(parameterInfo.pythonName), converter,
            parameterInfo.pythonName)
            .addStatement("%s = Any()", parameterAnyName)
            .addStatement("%s.Pack(msg=%s)", parameterAnyName, protobufParameterName)
            .addStatement("%s.any.append(%s)", SOME_PARAMETER_VARIABLE_NAME, parameterAnyName)
    }

    private fun getParameterAnyName(name: String) = "${name}_any"
    private fun getParameterWrapperName(name: String) = "${name}_wrapper"

    private fun PythonCodeBlock.addReturnStatement(returnType: TypeMirror) {
        addStatement("%s = Any()", RETURN_VALUE_VARIABLE_NAME)
        addStatement("%s.ParseFromString(%s)", RETURN_VALUE_VARIABLE_NAME, RAW_RETURN_VALUE_VARIABLE_NAME)

        val (converter, _) = mTypeConverter.pythonConverter(returnType)
        addStatement("return %s.from_protobuf(%s)", converter, RETURN_VALUE_VARIABLE_NAME)
    }

    private fun createCommandFunctionSpec(command: CommandInfo): PythonFunctionSpec {
        val builder = PythonFunctionSpec.functionBuilder(mNameGenerator.forCommand(command).snakeCaseName)
            .returnTypeHint(getPythonType(command.returnType))
        command.parameters.forEach {
            builder.addParameter(it.pythonName, getPythonType(it.type))
        }
        return builder.build()
    }

    private val ParameterInfo.pythonName
        get() = name.toSnakeCase()

    private fun getPythonType(typeMirror: TypeMirror) = mTypeConverter.pythonType(typeMirror).name

    private fun PythonFile.addCommandImports(command: CommandInfo) = apply {
        importPythonType(command.returnType)
        command.parameters.forEach { importPythonType(it.type) }
    }

    private fun PythonFile.importPythonType(typeMirror: TypeMirror) {
        addRequiredFromImports(mTypeConverter.pythonType(typeMirror))

        val (_, converterRequiredTypes) = mTypeConverter.pythonConverter(typeMirror)
        converterRequiredTypes.forEach {
            addRequiredFromImports(it)
        }
    }
    private fun getClassName(service: ServiceInfo) = mNameGenerator.forService(service).serviceProxyClassName.name

    private fun PythonFile.addImports(service: ServiceInfo) = apply {
        addFromImport(DEPENDENCY_CONTAINER_CLASS)
            .addFromImport(COMMAND_RUNNER_CLASS)
            .addFromImport(ANY_CLASS)
            .addFromImport(mNameGenerator.forService(service).serviceFullClassName)
            .addRequiredFromImports(pythonOptionalType(PythonTypeName.INT))
            .addFromImport(mProtobufTypeTranslator.toPythonType(ClassName.get(Some::class.java)))
            .addFromImport(dependenciesPythonClassName(service))
    }

    private fun dependenciesPythonClassName(service: ServiceInfo) =
        mNameGenerator.forService(service).dependenciesPythonClassName

    companion object {
        const val COMMAND_RUNNER_FIELD_NAME = "__command_runner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "command_runner"
        private const val COMMAND_REGISTRY_ID_FIELD_NAME = "__command_registry_id"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "command_registry_id"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val RAW_PARAMETER_VARIABLE_NAME = "raw_parameter"
        private const val SOME_PARAMETER_VARIABLE_NAME = "some_parameter"
        private const val RAW_RETURN_VALUE_VARIABLE_NAME = "raw_return_value"
        private const val RETURN_VALUE_VARIABLE_NAME = "return_value"
        private const val PYTHON_DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
