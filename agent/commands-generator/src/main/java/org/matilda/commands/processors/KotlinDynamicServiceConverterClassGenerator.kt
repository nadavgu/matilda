package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.CommandRegistryManager
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceConverter
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinDynamicServiceConverterClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    override fun process(instance: ServiceInfo) {
        fileSpecBuilder(mNameGenerator.forService(instance).dynamicServiceConverterClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).dynamicServiceConverterClassName.toKClassName())
            .superclass(DynamicServiceConverter::class.asClassName().plusParameter(service.type.typeName.toKTypeName()))
            .addSuperclassConstructorParameter(COMMAND_REGISTRY_MANAGER_VARIABLE_NAME)
            .addSuperclassConstructorParameter(service.commandRegistryFactoryParameterName)
            .addSuperclassConstructorParameter(service.serviceProxyFactoryParameterName)
            .primaryConstructor(createInjectConstructor(service))
            .build()

    private fun createInjectConstructor(service: ServiceInfo) =
        FunSpec.constructorBuilder()
            .addAnnotation(Inject::class)
            .addParameter(ParameterSpec.builder(COMMAND_REGISTRY_MANAGER_VARIABLE_NAME,
                CommandRegistryManager::class).build())
            .addParameter(ParameterSpec.builder(service.commandRegistryFactoryParameterName,
                service.commandRegistryFactoryTypeName.toKTypeName()).build())
            .addParameter(ParameterSpec.builder(service.serviceProxyFactoryParameterName,
                service.serviceProxyFactoryTypeName.toKTypeName()).build())
            .build()

    private val ServiceInfo.commandRegistryFactoryParameterName
        get() = StringUtils.uncapitalize(commandRegistryFactoryTypeName.simpleName())

    private val ServiceInfo.serviceProxyFactoryParameterName
        get() = StringUtils.uncapitalize(serviceProxyFactoryTypeName.simpleName())

    private val ServiceInfo.commandRegistryFactoryTypeName
        get() = mNameGenerator.forService(this).commandRegistryFactoryClassName

    private val ServiceInfo.serviceProxyFactoryTypeName
        get() = mNameGenerator.forService(this).javaServiceProxyFactoryClassName

    companion object {
        private const val COMMAND_REGISTRY_MANAGER_VARIABLE_NAME = "commandRegistryManager"
    }
}
