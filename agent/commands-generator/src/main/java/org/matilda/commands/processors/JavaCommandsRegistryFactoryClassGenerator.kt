package org.matilda.commands.processors

import com.squareup.javapoet.*
import org.matilda.commands.CommandRegistry
import org.matilda.commands.CommandRegistryFactory
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class JavaCommandsRegistryFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).commandRegistryFactoryClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).commandRegistryFactoryClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(CommandRegistryFactory::class.java),
                TypeName.get(service.type)))
            .addMethod(createInjectConstructor())
            .addField(createDependenciesField(service))
            .addMethod(createRegisterCommandsMethod(service))
            .addMethod(createCommandRegistryMethod(service))
            .build()

    private fun createDependenciesField(service: ServiceInfo) =
        FieldSpec.builder(mNameGenerator.forService(service).dependenciesClassName, JAVA_DEPENDENCIES_FIELD_NAME)
            .addAnnotation(Inject::class.java)
            .build()
    private fun createRegisterCommandsMethod(service: ServiceInfo): MethodSpec {
        val commandRegistryParameter =
            ParameterSpec.builder(TypeName.get(CommandRegistry::class.java), COMMAND_REGISTRY_PARAMETER_NAME).build()
        val serviceParameter =
            ParameterSpec.builder(TypeName.get(service.type), SERVICE_PARAMETER_NAME).build()
        val builder = MethodSpec.methodBuilder(REGISTER_COMMANDS_METHOD_NAME)
            .addParameter(commandRegistryParameter)
            .addParameter(serviceParameter)
        service.commands.forEach { command ->
            builder.addStatement("\$L.addCommand(\$L, new \$T(\$L, \$L))", COMMAND_REGISTRY_PARAMETER_NAME,
                mCommandIdGenerator.generate(command), mNameGenerator.forCommand(command).rawCommandClassName,
                SERVICE_PARAMETER_NAME, JAVA_DEPENDENCIES_FIELD_NAME)
        }
        return builder.build()
    }

    private fun createInjectConstructor() =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()

    private fun createCommandRegistryMethod(service: ServiceInfo) =
        MethodSpec.methodBuilder("createCommandRegistry")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(TypeName.get(service.type), SERVICE_PARAMETER_NAME).build())
            .addStatement("\$T \$L = new \$T()", CommandRegistry::class.java, COMMAND_REGISTRY_VARIABLE_NAME,
                CommandRegistry::class.java)
            .addStatement("\$L(\$L, \$L)", REGISTER_COMMANDS_METHOD_NAME, COMMAND_REGISTRY_VARIABLE_NAME,
                SERVICE_PARAMETER_NAME)
            .addStatement("return \$L", COMMAND_REGISTRY_VARIABLE_NAME)
            .returns(CommandRegistry::class.java)
            .build()

    companion object {
        private const val REGISTER_COMMANDS_METHOD_NAME = "registerCommands"
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry"
        private const val SERVICE_PARAMETER_NAME = "service"
    }
}
