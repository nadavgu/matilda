package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.matilda.commands.CommandRunner
import org.matilda.commands.ServiceProxyFactory
import org.matilda.commands.di.DiFrameWork
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinServiceProxyFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mDiFrameWork: DiFrameWork

    override fun process(instance: ServiceInfo) {
        fileSpecBuilder(mNameGenerator.forService(instance).javaServiceProxyFactoryClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).javaServiceProxyFactoryClassName.toKClassName())
            .addSuperinterface(ServiceProxyFactory::class.asClassName().plusParameter(
                service.type.typeName.toKTypeName()))
            .primaryConstructor(createInjectConstructor(service))
            .addProperty(createCommandRunnerField())
            .addProperty(createDependenciesField(service))
            .addFunction(createServiceProxyMethod(service))
            .build()

    private fun createCommandRunnerField() =
        PropertySpec.builder(COMMAND_RUNNER_FIELD_NAME, CommandRunner::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer(COMMAND_RUNNER_FIELD_NAME)
            .build()


    private fun createDependenciesField(service: ServiceInfo) =
        PropertySpec.builder(JAVA_DEPENDENCIES_FIELD_NAME, mNameGenerator.forService(service).dependenciesClassName.toKTypeName())
            .addModifiers(KModifier.PRIVATE)
            .initializer(JAVA_DEPENDENCIES_FIELD_NAME)
            .build()

    private fun createInjectConstructor(service: ServiceInfo) =
        FunSpec.constructorBuilder()
            .addAnnotation(mDiFrameWork.Inject)
            .addParameter(COMMAND_RUNNER_FIELD_NAME, CommandRunner::class)
            .addParameter(JAVA_DEPENDENCIES_FIELD_NAME, mNameGenerator.forService(service).dependenciesClassName.toKTypeName())
            .build()

    private fun createServiceProxyMethod(service: ServiceInfo) =
        FunSpec.builder("createServiceProxy")
            .returns(service.type.typeName.toKTypeName())
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder(COMMAND_REGISTRY_ID_PARAMETER_NAME, Int::class).build())
            .addStatement("return %T(%L, %L, %L)",
                mNameGenerator.forService(service).javaServiceProxyClassName.toKClassName(),
                COMMAND_RUNNER_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME, JAVA_DEPENDENCIES_FIELD_NAME)
            .build()

    companion object {
        private const val COMMAND_RUNNER_FIELD_NAME = "mCommandRunner"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "commandRegistryId"
    }
}
