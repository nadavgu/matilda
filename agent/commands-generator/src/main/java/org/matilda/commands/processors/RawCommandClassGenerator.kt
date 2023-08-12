package org.matilda.commands.processors

import com.squareup.javapoet.*
import org.matilda.commands.Command
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.isScalarType
import org.matilda.commands.types.protobufWrapperJavaType
import java.io.IOException
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

class RawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

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
            .beginControlFlow("try")
            .addStatement("\$T \$L = \$T.parseFrom(\$L)",
                Some::class.java, SOME_PARAMETER_VARIABLE_NAME, Some::class.java, RAW_PARAMETER_NAME)
            .apply {
                command.parameters.forEachIndexed { index, parameter ->
                    addParameterConversion(index, parameter)
                }
            }
            .addStatement("\$T \$L = \$L.\$L(\$L)",
                command.returnType, RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name,
                command.parameters.joinToString { it.name })
            .addReturnValueConversion(command.returnType)
            .nextControlFlow("catch (\$T \$L)", IOException::class.java, EXCEPTION_NAME)
            .addStatement("throw new \$T(\$L)", RuntimeException::class.java, EXCEPTION_NAME)
            .endControlFlow()
            .build()

    private fun MethodSpec.Builder.addParameterConversion(index: Int, parameterInfo: ParameterInfo) =
        if (parameterInfo.type.isScalarType()) {
            addStatement("\$T \$L = \$L.getAny(\$L).unpack(\$T.class).getValue()",
                parameterInfo.type, parameterInfo.name, SOME_PARAMETER_VARIABLE_NAME, index,
                parameterInfo.type.protobufWrapperJavaType)
        } else {
            addStatement("\$T \$L = \$L.getAny(\$L).unpack(\$T.class)",
                parameterInfo.type, parameterInfo.name, SOME_PARAMETER_VARIABLE_NAME, index, parameterInfo.type)
        }

    private fun MethodSpec.Builder.addReturnValueConversion(returnType: TypeMirror): MethodSpec.Builder {
        val returnValueVariable = if (returnType.isScalarType()) WRAPPER_RETURN_VALUE_NAME else RETURN_VALUE_NAME
        if (returnType.isScalarType()) {
            addStatement("\$T \$L = \$T.newBuilder().setValue(\$L).build()",
                returnType.protobufWrapperJavaType, WRAPPER_RETURN_VALUE_NAME, returnType.protobufWrapperJavaType,
                RETURN_VALUE_NAME)
        }
        addStatement("return \$L.toByteArray()", returnValueVariable)
        return this
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        private const val SERVICE_FIELD_NAME = "mService"
        private const val RAW_PARAMETER_NAME = "rawParameter"
        private const val RETURN_VALUE_NAME = "returnValue"
        private const val WRAPPER_RETURN_VALUE_NAME = "wrapperReturnValue"
        private const val EXCEPTION_NAME = "exception"
        private const val SOME_PARAMETER_VARIABLE_NAME = "someParameter"
    }
}
