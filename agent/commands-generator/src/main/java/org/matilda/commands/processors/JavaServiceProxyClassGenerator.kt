package org.matilda.commands.processors

import com.google.protobuf.Any
import com.squareup.javapoet.*
import org.matilda.commands.CommandRunner
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import java.io.IOException
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

class JavaServiceProxyClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).javaServiceProxyPackageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).javaServiceProxyClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(TypeName.get(service.type))
            .addField(createCommandRunnerField())
            .addField(createCommandRegistryIdField())
            .addMethod(createConstructor())
            .apply {
                service.commands.forEach { command ->
                    addMethod(createCommandMethod(command))
                }
            }
            .build()

    private fun createCommandRunnerField() =
        FieldSpec.builder(CommandRunner::class.java, COMMAND_RUNNER_FIELD_NAME)
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build()

    private fun createCommandRegistryIdField() =
        FieldSpec.builder(TypeName.INT, COMMAND_REGISTRY_ID_FIELD_NAME)
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build()

    private fun createConstructor() =
        MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(CommandRunner::class.java, COMMAND_RUNNER_PARAMETER_NAME).build())
            .addParameter(ParameterSpec.builder(TypeName.INT, COMMAND_REGISTRY_ID_PARAMETER_NAME).build())
            .addStatement("\$L = \$L", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
            .addStatement("\$L = \$L", COMMAND_REGISTRY_ID_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME)
            .build()

    private fun createCommandMethod(command: CommandInfo) =
        MethodSpec.methodBuilder(command.name)
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.get(command.returnType))
            .apply {
                command.parameters.forEach { parameter ->
                    addParameter(ParameterSpec.builder(TypeName.get(parameter.type), parameter.name).build())
                }
            }
            .beginControlFlow("try")
            .addStatement("\$T \$L = \$T.newBuilder()", Some.Builder::class.java, SOME_PARAMETER_VARIABLE_NAME,
                Some::class.java)
            .apply {
                command.parameters.forEach {
                    addParameterConversion(it)
                }
            }
            .addStatement("\$T \$L = \$L.run(\$L, \$L, \$L.build().toByteArray())",
                BYTE_ARRAY_TYPE_NAME, RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                COMMAND_REGISTRY_ID_FIELD_NAME, mCommandIdGenerator.generate(command),
                SOME_PARAMETER_VARIABLE_NAME)
            .addReturnStatement(command.returnType)
            .nextControlFlow("catch (\$T \$L)", IOException::class.java, EXCEPTION_NAME)
            .addStatement("throw new \$T(\$L)", RuntimeException::class.java, EXCEPTION_NAME)
            .endControlFlow()
            .build()

    private fun MethodSpec.Builder.addParameterConversion(parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(parameterInfo.type)
        addStatement("\$L.addAny(\$T.pack($converterFormat.convertToProtobuf(\$L)))",
            SOME_PARAMETER_VARIABLE_NAME, Any::class.java, *converterArgs.toTypedArray(), parameterInfo.name)
    }

    private fun MethodSpec.Builder.addReturnStatement(returnType: TypeMirror): MethodSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(returnType)
        addStatement("return $converterFormat.convertFromProtobuf(\$T.parseFrom(\$L))", *converterArgs.toTypedArray(),
            Any::class.java, RETURN_VALUE_VARIABLE_NAME)
        return this
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        const val COMMAND_RUNNER_FIELD_NAME = "mCommandRunner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "commandRunner"
        private const val COMMAND_REGISTRY_ID_FIELD_NAME = "mCommandRegistryId"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "commandRegistryId"
        private const val SOME_PARAMETER_VARIABLE_NAME = "someParameter"
        private const val RETURN_VALUE_VARIABLE_NAME = "returnValue"
        private const val EXCEPTION_NAME = "exception"
    }
}
