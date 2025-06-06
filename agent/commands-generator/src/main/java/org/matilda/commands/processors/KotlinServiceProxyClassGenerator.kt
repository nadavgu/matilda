package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
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
import org.matilda.commands.types.kotlinConverter
import org.matilda.commands.utils.addPbandkExtensionImports
import org.matilda.commands.utils.fileSpecBuilder
import pbandk.wkt.Any
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinServiceProxyClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        fileSpecBuilder(mNameGenerator.forService(instance).javaServiceProxyClassName.packageName(),
            createClassSpec(instance))
            .addPbandkExtensionImports()
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).javaServiceProxyClassName.toKClassName())
            .addSuperinterface(service.type.typeName.toKTypeName())
            .addProperty(createCommandRunnerField())
            .addProperty(createCommandRegistryIdField())
            .addProperty(createDependenciesField(service))
            .primaryConstructor(createConstructor(service))
            .apply {
                service.commands.forEach { command ->
                    addFunction(createCommandMethod(command))
                }
            }
            .build()

    private fun createCommandRunnerField() =
        PropertySpec.builder(COMMAND_RUNNER_FIELD_NAME, CommandRunner::class)
            .addModifiers(KModifier.PRIVATE)
            .build()

    private fun createCommandRegistryIdField() =
        PropertySpec.builder(COMMAND_REGISTRY_ID_FIELD_NAME, Int::class)
            .addModifiers(KModifier.PRIVATE)
            .build()

    private fun createDependenciesField(service: ServiceInfo) =
        PropertySpec.builder(JAVA_DEPENDENCIES_FIELD_NAME,
            mNameGenerator.forService(service).dependenciesClassName.toKTypeName())
            .addModifiers(KModifier.PRIVATE)
            .build()

    private fun createConstructor(service: ServiceInfo) =
        FunSpec.constructorBuilder()
            .addParameter(ParameterSpec.builder(COMMAND_RUNNER_PARAMETER_NAME, CommandRunner::class).build())
            .addParameter(ParameterSpec.builder(COMMAND_REGISTRY_ID_PARAMETER_NAME, Int::class).build())
            .addParameter(ParameterSpec.builder(DEPENDENCIES_PARAMETER_NAME,
                mNameGenerator.forService(service).dependenciesClassName.toKTypeName()).build())
            .addStatement("%L = %L", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
            .addStatement("%L = %L", COMMAND_REGISTRY_ID_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME)
            .addStatement("%L = %L", JAVA_DEPENDENCIES_FIELD_NAME, DEPENDENCIES_PARAMETER_NAME)
            .build()

    private fun createCommandMethod(command: CommandInfo) =
        FunSpec.builder(command.name)
            .addModifiers(KModifier.OVERRIDE)
            .returns(if (command.hasReturnValue()) command.returnType.typeName.toKTypeName() else UNIT)
            .apply {
                command.parameters.forEach { parameter ->
                    addParameter(ParameterSpec.builder(parameter.name, parameter.type.typeName.toKTypeName()).build())
                }
            }
            .addStatement("val %L = mutableListOf<%T>()", ANY_LIST_VARIABLE_NAME, Any::class)
            .apply {
                command.parameters.forEach {
                    addParameterConversion(it)
                }
            }
            .addStatement("val %L = %L.run(%L, %L, %T(%L).encodeToByteArray())",
                RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                COMMAND_REGISTRY_ID_FIELD_NAME, mCommandIdGenerator.generate(command),
                Some::class, ANY_LIST_VARIABLE_NAME)
            .addReturnStatement(command)
            .build()

    private fun FunSpec.Builder.addParameterConversion(parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.kotlinConverter(parameterInfo.type)
        addStatement("%L.add(%T.pack($converterFormat.convertToProtobuf(%L)))",
            ANY_LIST_VARIABLE_NAME, Any::class, *converterArgs.toTypedArray(), parameterInfo.name)
    }

    private fun FunSpec.Builder.addReturnStatement(command: CommandInfo): FunSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.kotlinConverter(command.returnType)
        if (command.hasReturnValue()) {
            addStatement("return $converterFormat.convertFromProtobuf(%T.decodeFromByteArray(%L))",
                *converterArgs.toTypedArray(), Any::class.java, RETURN_VALUE_VARIABLE_NAME)
        } else {
            addStatement("$converterFormat.convertFromProtobuf(%T.decodeFromByteArray(%L))",
                *converterArgs.toTypedArray(), Any::class.java, RETURN_VALUE_VARIABLE_NAME)
        }
        return this
    }

    companion object {
        const val COMMAND_RUNNER_FIELD_NAME = "mCommandRunner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "commandRunner"
        private const val COMMAND_REGISTRY_ID_FIELD_NAME = "mCommandRegistryId"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "commandRegistryId"
        private const val ANY_LIST_VARIABLE_NAME = "anyList"
        private const val RETURN_VALUE_VARIABLE_NAME = "returnValue"
        private const val DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
