package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.matilda.commands.CommandRegistry
import org.matilda.commands.CommandRegistryFactory
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinCommandsRegistryFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ServiceInfo) {
        fileSpecBuilder(mNameGenerator.forService(instance).commandRegistryFactoryClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).commandRegistryFactoryClassName.toKClassName())
            .addSuperinterface(CommandRegistryFactory::class.asClassName().plusParameter(service.type.typeName.toKTypeName()))
            .primaryConstructor(createInjectConstructor())
            .addProperty(createDependenciesField(service))
            .addFunction(createRegisterCommandsMethod(service))
            .addFunction(createCommandRegistryMethod(service))
            .build()

    private fun createDependenciesField(service: ServiceInfo) =
        PropertySpec.builder(JAVA_DEPENDENCIES_FIELD_NAME,
            mNameGenerator.forService(service).dependenciesClassName.toKTypeName())
            .addAnnotation(Inject::class)
            .addModifiers(KModifier.LATEINIT)
            .mutable(true)
            .build()
    private fun createRegisterCommandsMethod(service: ServiceInfo): FunSpec {
        val commandRegistryParameter =
            ParameterSpec.builder(COMMAND_REGISTRY_PARAMETER_NAME, CommandRegistry::class).build()
        val serviceParameter =
            ParameterSpec.builder(SERVICE_PARAMETER_NAME, service.type.typeName.toKTypeName()).build()
        val builder = FunSpec.builder(REGISTER_COMMANDS_METHOD_NAME)
            .addParameter(commandRegistryParameter)
            .addParameter(serviceParameter)
        service.commands.forEach { command ->
            builder.addStatement("%L.addCommand(%L, %T(%L, %L))", COMMAND_REGISTRY_PARAMETER_NAME,
                mCommandIdGenerator.generate(command),
                mNameGenerator.forCommand(command).rawCommandClassName.toKClassName(),
                SERVICE_PARAMETER_NAME, JAVA_DEPENDENCIES_FIELD_NAME)
        }
        return builder.build()
    }

    private fun createInjectConstructor() =
        FunSpec.constructorBuilder()
            .addAnnotation(Inject::class)
            .build()

    private fun createCommandRegistryMethod(service: ServiceInfo) =
        FunSpec.builder("createCommandRegistry")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder(SERVICE_PARAMETER_NAME, service.type.typeName.toKTypeName()).build())
            .addStatement("val %L = %T()", COMMAND_REGISTRY_VARIABLE_NAME, CommandRegistry::class)
            .addStatement("%L(%L, %L)", REGISTER_COMMANDS_METHOD_NAME, COMMAND_REGISTRY_VARIABLE_NAME,
                SERVICE_PARAMETER_NAME)
            .addStatement("return %L", COMMAND_REGISTRY_VARIABLE_NAME)
            .returns(CommandRegistry::class)
            .build()

    companion object {
        private const val REGISTER_COMMANDS_METHOD_NAME = "registerCommands"
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry"
        private const val SERVICE_PARAMETER_NAME = "service"
    }
}
