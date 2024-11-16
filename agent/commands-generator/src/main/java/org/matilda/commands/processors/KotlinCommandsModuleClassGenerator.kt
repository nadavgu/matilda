package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import dagger.Module
import me.tatarka.inject.annotations.Component
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.CommandRegistry
import org.matilda.commands.di.DiFrameWork
import org.matilda.commands.di.staticProvidesFunctionBuilder
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinCommandsModuleClassGenerator @Inject constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mDiFrameWork: DiFrameWork

    override fun process(instance: ProjectServices) {
        fileSpecBuilder(mNameGenerator.commandsGeneratedPackage.packageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(services: ProjectServices): TypeSpec {
        return TypeSpec.interfaceBuilder(mNameGenerator.commandsModuleClassName.toKClassName())
            .addAnnotation(createModuleAnnotation())
            .apply {
                if (mDiFrameWork == DiFrameWork.KotlinInject) {
                    addAnnotation(mDiFrameWork.Singleton)
                    addSuperinterface(mNameGenerator.servicesModuleClassName.toKClassName())
                }
            }
            .staticProvidesFunctionBuilder(mDiFrameWork) {
                addFunction(createCommandRegistryProviderMethod(services))
            }
            .build()
    }

    private fun createModuleAnnotation() =
        when (mDiFrameWork) {
            DiFrameWork.Dagger -> AnnotationSpec.builder(Module::class)
                .addMember("includes = [%T::class]", mNameGenerator.servicesModuleClassName.toKClassName())
                .build()
            DiFrameWork.KotlinInject -> AnnotationSpec.builder(Component::class).build()
        }

    private fun getCommandParameterName(command: CommandInfo) =
        StringUtils.uncapitalize(mNameGenerator.forCommand(command).fullCommandName)

    private fun createCommandRegistryProviderMethod(services: ProjectServices) =
        FunSpec.builder("commandRegistry")
            .addAnnotation(mDiFrameWork.Provides)
            .addAnnotation(mDiFrameWork.Singleton)
            .addStatement("val %L = %T()", COMMAND_REGISTRY_VARIABLE_NAME, CommandRegistry::class)
            .apply {
                services.forEachStaticCommand { command ->
                    addParameter(getCommandParameterName(command), getCommandTypeName(command))
                    addStatement("%L.addCommand(%L, %L)", COMMAND_REGISTRY_PARAMETER_NAME,
                        mCommandIdGenerator.generate(command), getCommandParameterName(command))
                }
            }
            .addStatement("return %L", COMMAND_REGISTRY_VARIABLE_NAME)
            .returns(CommandRegistry::class)
            .build()

    private fun getCommandTypeName(command: CommandInfo) =
        mNameGenerator.forCommand(command).rawCommandClassName.toKTypeName()

    companion object {
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry"
    }
}
