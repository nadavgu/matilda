package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.javapoet.*
import org.matilda.commands.CommandRunner
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.info.hasReturnValue
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import org.matilda.commands.utils.PBANDK_ANY_EXTENSIONS_EXTENSIONS_TYPE
import org.matilda.commands.utils.PBANDK_MESSAGE_EXTENSIONS_TYPE
import pbandk.wkt.Any
import java.util.Collections
import javax.inject.Inject
import javax.lang.model.element.Modifier

class JavaServiceProxyClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).javaServiceProxyClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).javaServiceProxyClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(service.type.typeName)
            .addField(createCommandRunnerField())
            .addField(createCommandRegistryIdField())
            .addField(createDependenciesField(service))
            .addMethod(createConstructor(service))
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

    private fun createDependenciesField(service: ServiceInfo) =
        FieldSpec.builder(mNameGenerator.forService(service).dependenciesClassName,
            JAVA_DEPENDENCIES_FIELD_NAME)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build()

    private fun createConstructor(service: ServiceInfo) =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(CommandRunner::class.java, COMMAND_RUNNER_PARAMETER_NAME).build())
            .addParameter(ParameterSpec.builder(TypeName.INT, COMMAND_REGISTRY_ID_PARAMETER_NAME).build())
            .addParameter(ParameterSpec.builder(mNameGenerator.forService(service).dependenciesClassName,
                DEPENDENCIES_PARAMETER_NAME).build())
            .addStatement("\$L = \$L", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
            .addStatement("\$L = \$L", COMMAND_REGISTRY_ID_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME)
            .addStatement("\$L = \$L", JAVA_DEPENDENCIES_FIELD_NAME, DEPENDENCIES_PARAMETER_NAME)
            .build()

    private fun createCommandMethod(command: CommandInfo) =
        MethodSpec.methodBuilder(command.name)
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(command.returnType.typeName)
            .apply {
                command.parameters.forEach { parameter ->
                    addParameter(ParameterSpec.builder(parameter.type.typeName, parameter.name).build())
                }
            }
            .addStatement("\$T<\$T> \$L = new \$T<>()", List::class.java, Any::class.java, ANY_LIST_VARIABLE_NAME,
                ArrayList::class.java)
            .apply {
                command.parameters.forEach {
                    addParameterConversion(it)
                }
            }
            .addStatement("\$T \$L = \$L.run(\$L, \$L, \$T.encodeToByteArray(new \$T(\$L, \$T.emptyMap())))",
                BYTE_ARRAY_TYPE_NAME, RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                COMMAND_REGISTRY_ID_FIELD_NAME, mCommandIdGenerator.generate(command), PBANDK_MESSAGE_EXTENSIONS_TYPE,
                Some::class.java, ANY_LIST_VARIABLE_NAME, Collections::class.java)
            .addReturnStatement(command)
            .build()

    private fun MethodSpec.Builder.addParameterConversion(parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(parameterInfo.type)
        addStatement("\$L.add(\$T.pack(\$T, $converterFormat.convertToProtobuf(\$L), \"type.googleapis.com\"))",
            ANY_LIST_VARIABLE_NAME, PBANDK_ANY_EXTENSIONS_EXTENSIONS_TYPE, Any.Companion::class.java,
            *converterArgs.toTypedArray(), parameterInfo.name)
    }

    private fun MethodSpec.Builder.addReturnStatement(command: CommandInfo): MethodSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(command.returnType)
        if (command.hasReturnValue()) {
            addStatement("return $converterFormat.convertFromProtobuf(\$T.decodeFromByteArray(\$T, \$L))",
                *converterArgs.toTypedArray(), PBANDK_MESSAGE_EXTENSIONS_TYPE,
                Any.Companion::class.java, RETURN_VALUE_VARIABLE_NAME)
        } else {
            addStatement("$converterFormat.convertFromProtobuf(\$T.decodeFromByteArray(\$T, \$L))",
                *converterArgs.toTypedArray(), PBANDK_MESSAGE_EXTENSIONS_TYPE,
                Any.Companion::class.java, RETURN_VALUE_VARIABLE_NAME)
        }
        return this
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        const val COMMAND_RUNNER_FIELD_NAME = "mCommandRunner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "commandRunner"
        private const val COMMAND_REGISTRY_ID_FIELD_NAME = "mCommandRegistryId"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "commandRegistryId"
        private const val ANY_LIST_VARIABLE_NAME = "anyList"
        private const val RETURN_VALUE_VARIABLE_NAME = "returnValue"
        private const val DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
