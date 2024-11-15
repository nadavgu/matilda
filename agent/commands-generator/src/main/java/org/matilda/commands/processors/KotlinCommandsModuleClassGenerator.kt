package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import dagger.Module
import dagger.Provides
import org.matilda.commands.CommandRegistry
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinCommandsModuleClassGenerator @Inject constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ProjectServices) {
        fileSpecBuilder(mNameGenerator.commandsGeneratedPackage.packageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(services: ProjectServices): TypeSpec {
        val builder = TypeSpec.classBuilder(mNameGenerator.commandsModuleClassName.toKClassName())
            .addAnnotation(createModuleAnnotation())
        services.forEachStaticCommand { command -> builder.addProperty(createCommandField(command)) }
        return builder.primaryConstructor(createInjectConstructor())
            .addFunction(createRegisterCommandsMethod(services))
            .addFunction(createCommandRegistryProviderMethod())
            .build()
    }

    private fun createModuleAnnotation() =
        AnnotationSpec.builder(Module::class)
            .addMember("includes = [%T::class]", mNameGenerator.servicesModuleClassName.toKClassName())
            .build()

    private fun createCommandField(command: CommandInfo) =
        PropertySpec.builder(getCommandFieldName(command), getCommandTypeName(command))
            .addAnnotation(Inject::class)
            .addModifiers(KModifier.LATEINIT)
            .mutable(true)
            .build()

    private fun createRegisterCommandsMethod(services: ProjectServices): FunSpec {
        val commandRegistryParameter =
            ParameterSpec.builder(COMMAND_REGISTRY_PARAMETER_NAME, CommandRegistry::class).build()
        val builder = FunSpec.builder(REGISTER_COMMANDS_METHOD_NAME)
            .addParameter(commandRegistryParameter)
        services.forEachStaticCommand { command ->
            builder.addStatement("%L.addCommand(%L, %L)", COMMAND_REGISTRY_PARAMETER_NAME,
                mCommandIdGenerator.generate(command), getCommandFieldName(command))
        }
        return builder.build()
    }

    private fun getCommandFieldName(command: CommandInfo) = "m" + mNameGenerator.forCommand(command).fullCommandName

    private fun createInjectConstructor() =
        FunSpec.constructorBuilder()
            .addAnnotation(Inject::class)
            .build()

    private fun createCommandRegistryProviderMethod() =
        FunSpec.builder("commandRegistry")
            .addAnnotation(Provides::class)
            .addAnnotation(Singleton::class)
            .addParameter(ParameterSpec.builder(COMMANDS_MODULE_PARAMETER_NAME,
                mNameGenerator.commandsModuleClassName.toKTypeName()).build())
            .addStatement("val %L = %T()", COMMAND_REGISTRY_VARIABLE_NAME, CommandRegistry::class)
            .addStatement("%L.%L(%L)", COMMANDS_MODULE_PARAMETER_NAME, REGISTER_COMMANDS_METHOD_NAME,
                COMMAND_REGISTRY_VARIABLE_NAME)
            .addStatement("return %L", COMMAND_REGISTRY_VARIABLE_NAME)
            .returns(CommandRegistry::class)
            .build()

    private fun getCommandTypeName(command: CommandInfo) =
        mNameGenerator.forCommand(command).rawCommandClassName.toKTypeName()

    companion object {
        private const val REGISTER_COMMANDS_METHOD_NAME = "registerCommands"
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry"
        private const val COMMANDS_MODULE_PARAMETER_NAME = "commandsModule"
    }
}
