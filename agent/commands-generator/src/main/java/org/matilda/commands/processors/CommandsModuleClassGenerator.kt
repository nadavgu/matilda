package org.matilda.commands.processors

import com.squareup.javapoet.*
import dagger.Module
import dagger.Provides
import org.matilda.commands.CommandRegistry
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class CommandsModuleClassGenerator @Inject constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ProjectServices) {
        JavaFile.builder(NameGenerator.COMMANDS_GENERATED_PACKAGE.packageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(services: ProjectServices): TypeSpec {
        val builder = TypeSpec.classBuilder(NameGenerator.COMMANDS_MODULE_CLASS_NAME)
            .addAnnotation(createModuleAnnotation())
            .addModifiers(Modifier.PUBLIC)
        services.forEachCommand { command -> builder.addField(createCommandField(command)) }
        return builder.addMethod(createInjectConstructor())
            .addMethod(createRegisterCommandsMethod(services))
            .addMethod(createCommandRegistryProviderMethod())
            .build()
    }

    private fun createModuleAnnotation() =
        AnnotationSpec.builder(Module::class.java)
            .addMember("includes", "\$T.class", NameGenerator.SERVICES_MODULE_TYPE_NAME)
            .build()

    private fun createCommandField(command: CommandInfo) =
        FieldSpec.builder(getCommandTypeName(command), getCommandFieldName(command))
            .addAnnotation(Inject::class.java)
            .build()

    private fun createRegisterCommandsMethod(services: ProjectServices): MethodSpec {
        val commandRegistryParameter =
            ParameterSpec.builder(TypeName.get(CommandRegistry::class.java), COMMAND_REGISTRY_PARAMETER_NAME).build()
        val builder = MethodSpec.methodBuilder(REGISTER_COMMANDS_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(commandRegistryParameter)
        services.forEachCommand { command ->
            builder.addStatement("\$L.addCommand(\$L, \$L)", COMMAND_REGISTRY_PARAMETER_NAME,
                mCommandIdGenerator.generate(command), getCommandFieldName(command))
        }
        return builder.build()
    }

    private fun getCommandFieldName(command: CommandInfo) = "m" + mNameGenerator.forCommand(command).fullCommandName

    private fun createInjectConstructor() =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()

    private fun createCommandRegistryProviderMethod() =
        MethodSpec.methodBuilder("commandRegistry")
            .addModifiers(Modifier.STATIC)
            .addAnnotation(Provides::class.java)
            .addParameter(ParameterSpec.builder(NameGenerator.COMMANDS_MODULE_TYPE_NAME,
                COMMANDS_MODULE_PARAMETER_NAME).build())
            .addStatement("\$T \$L = new \$T()", CommandRegistry::class.java, COMMAND_REGISTRY_VARIABLE_NAME,
                CommandRegistry::class.java)
            .addStatement("\$L.\$L(\$L)", COMMANDS_MODULE_PARAMETER_NAME, REGISTER_COMMANDS_METHOD_NAME,
                COMMAND_REGISTRY_VARIABLE_NAME)
            .addStatement("return \$L", COMMAND_REGISTRY_VARIABLE_NAME)
            .returns(CommandRegistry::class.java)
            .build()

    private fun getCommandTypeName(command: CommandInfo) = mNameGenerator.forCommand(command).rawCommandTypeName

    companion object {
        private const val REGISTER_COMMANDS_METHOD_NAME = "registerCommands"
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry"
        private const val COMMANDS_MODULE_PARAMETER_NAME = "commandsModule"
    }
}
