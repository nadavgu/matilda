package org.matilda.commands.processors

import com.squareup.javapoet.ClassName
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.python.ANY_CLASS
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.python.writer.*
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.constructorBuilder
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.functionBuilder
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.PYTHON_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.types.ProtobufTypeTranslator
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.pythonConverter
import org.matilda.commands.types.pythonType
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonRawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    @Inject
    lateinit var mProtobufTypeTranslator: ProtobufTypeTranslator

    override fun process(instance: CommandInfo) {
        val pythonFile = PythonFile(mNameGenerator.forCommand(instance).rawCommandPythonClassName.packageName)
            .addImports(instance)
            .addClass(instance)

        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(command: CommandInfo) = apply {
        val serviceNameGenerator = mNameGenerator.forService(command.service)
        addRequiredFromImports(serviceNameGenerator.serviceFullClassName)
        addRequiredFromImports(serviceNameGenerator.dependenciesPythonClassName)
        addFromImport(ANY_CLASS)
        addFromImport(mProtobufTypeTranslator.toPythonType(ClassName.get(Some::class.java)))

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

    private fun PythonFile.addClass(command: CommandInfo) = apply {
        newClass(PythonClassSpec(mNameGenerator.forCommand(command).rawCommandPythonClassName.name))
            .addConstructor(command)
            .addRunMethod(command)
    }

    private fun PythonClass.addConstructor(command: CommandInfo) = apply {
        val serviceNameGenerator = mNameGenerator.forService(command.service)
        addInstanceMethod(constructorBuilder()
            .addParameter(SERVICE_PARAMETER_NAME, serviceNameGenerator.serviceClassName)
            .addParameter(DEPENDENCIES_PARAMETER_NAME, serviceNameGenerator.dependenciesPythonClassName.name)
            .build())
            .addStatement("self.%s = %s", SERVICE_FIELD_NAME, SERVICE_PARAMETER_NAME)
            .addStatement("self.%s = %s", PYTHON_DEPENDENCIES_FIELD_NAME, DEPENDENCIES_PARAMETER_NAME)
    }

    private fun PythonClass.addRunMethod(command: CommandInfo) = apply {
        addInstanceMethod(functionBuilder("__call__")
            .addParameter(RAW_PARAMETER_NAME, PythonTypeName.BYTES.name)
            .returnTypeHint(PythonTypeName.BYTES.name)
            .build())
            .addStatement("%s = Some()",  SOME_PARAMETER_VARIABLE_NAME)
            .addStatement("%s.ParseFromString(%s)", SOME_PARAMETER_VARIABLE_NAME, RAW_PARAMETER_NAME)
            .apply {
                command.parameters.forEachIndexed { index, parameter ->
                    addParameterConversion(index, parameter)
                }
            }
            .addCommandInvocation(command)
            .addReturnValueConversion(command)
    }

    private fun PythonCodeBlock.addParameterConversion(index: Int, parameterInfo: ParameterInfo) {
        val (converterFormat,) = mTypeConverter.pythonConverter(parameterInfo.type)
        addStatement("%s = $converterFormat.from_protobuf(%s.any[%d])",
            parameterInfo.name.toSnakeCase(), SOME_PARAMETER_VARIABLE_NAME, index)
    }

    private fun PythonCodeBlock.addCommandInvocation(command: CommandInfo): PythonCodeBlock {
        addStatement("%s = self.%s.%s(%s)",
            RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name.toSnakeCase(),
            command.parameters.joinToString { it.name.toSnakeCase() })
        return this
    }

    private fun PythonCodeBlock.addReturnValueConversion(commandInfo: CommandInfo): PythonCodeBlock {
        val (converterFormat,) = mTypeConverter.pythonConverter(commandInfo.returnType)
        addStatement("%s = Any()", ANY_RETURN_VALUE_VARIABLE_NAME)
        addStatement("%s.Pack(msg=$converterFormat.to_protobuf(%s))", ANY_RETURN_VALUE_VARIABLE_NAME, RETURN_VALUE_NAME)
        addStatement("return %s.SerializeToString()", ANY_RETURN_VALUE_VARIABLE_NAME)
        return this
    }

    companion object {
        private const val SERVICE_FIELD_NAME = "__service"
        private const val SERVICE_PARAMETER_NAME = "service"
        private const val RAW_PARAMETER_NAME = "raw_parameter"
        private const val RETURN_VALUE_NAME = "return_value"
        private const val SOME_PARAMETER_VARIABLE_NAME = "some_parameter"
        private const val ANY_RETURN_VALUE_VARIABLE_NAME = "any_return_value"
        private const val DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
