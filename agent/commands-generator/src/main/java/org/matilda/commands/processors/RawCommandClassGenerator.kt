package org.matilda.commands.processors

import com.google.protobuf.Any
import com.squareup.javapoet.*
import org.matilda.commands.Command
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.hasReturnValue
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import java.io.IOException
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class RawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: CommandInfo) {
        JavaFile.builder(mNameGenerator.forCommand(instance).rawCommandPackageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(command: CommandInfo) =
        TypeSpec.classBuilder(mNameGenerator.forCommand(command).rawCommandClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(Command::class.java)
            .addField(createServiceField(command))
            .addMethod(createInjectConstructor())
            .addMethod(createRunMethod(command))
            .build()

    private fun createServiceField(command: CommandInfo) =
        FieldSpec.builder(TypeName.get(command.service.type), SERVICE_FIELD_NAME)
            .addAnnotation(Inject::class.java)
            .build()

    private fun createInjectConstructor() =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()

    private fun createRunMethod(command: CommandInfo) =
        MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(BYTE_ARRAY_TYPE_NAME, RAW_PARAMETER_NAME).build())
            .returns(ArrayTypeName.of(TypeName.BYTE))
            .addExceptions(command.thrownTypes.map(TypeName::get))
            .beginControlFlow("try")
            .addStatement("\$T \$L = \$T.parseFrom(\$L)",
                Some::class.java, SOME_PARAMETER_VARIABLE_NAME, Some::class.java, RAW_PARAMETER_NAME)
            .apply {
                command.parameters.forEachIndexed { index, parameter ->
                    addParameterConversion(index, parameter)
                }
            }
            .addCommandInvocation(command)
            .addReturnValueConversion(command)
            .nextControlFlow("catch (\$T \$L)", IOException::class.java, EXCEPTION_NAME)
            .addStatement("throw new \$T(\$L)", RuntimeException::class.java, EXCEPTION_NAME)
            .endControlFlow()
            .build()

    private fun MethodSpec.Builder.addParameterConversion(index: Int, parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(parameterInfo.type)
        addStatement("\$T \$L = $converterFormat.convertFromProtobuf(\$L.getAny(\$L))",
            parameterInfo.type, parameterInfo.name, *converterArgs.toTypedArray(), SOME_PARAMETER_VARIABLE_NAME, index)
    }

    private fun MethodSpec.Builder.addCommandInvocation(command: CommandInfo): MethodSpec.Builder {
        if (command.hasReturnValue()) {
            addStatement("\$T \$L = \$L.\$L(\$L)",
                command.returnType, RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name,
                command.parameters.joinToString { it.name })
        } else {
            addStatement("\$L.\$L(\$L)",
                SERVICE_FIELD_NAME, command.name, command.parameters.joinToString { it.name })
        }
        return this
    }

    private fun MethodSpec.Builder.addReturnValueConversion(commandInfo: CommandInfo): MethodSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(commandInfo.returnType)
        addStatement("return \$T.pack($converterFormat.convertToProtobuf(\$L)).toByteArray()",
            Any::class.java, *converterArgs.toTypedArray(),
            if (commandInfo.hasReturnValue()) RETURN_VALUE_NAME else "null")
        return this
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        private const val SERVICE_FIELD_NAME = "mService"
        private const val RAW_PARAMETER_NAME = "rawParameter"
        private const val RETURN_VALUE_NAME = "returnValue"
        private const val EXCEPTION_NAME = "exception"
        private const val SOME_PARAMETER_VARIABLE_NAME = "someParameter"
    }
}
