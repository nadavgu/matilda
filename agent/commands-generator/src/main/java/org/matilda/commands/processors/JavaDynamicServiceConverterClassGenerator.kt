package org.matilda.commands.processors

import com.squareup.javapoet.*
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.CommandRepository
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceConverter
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class JavaDynamicServiceConverterClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).dynamicServiceConverterClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).dynamicServiceConverterClassName)
            .addModifiers(Modifier.PUBLIC)
            .superclass(ParameterizedTypeName.get(ClassName.get(DynamicServiceConverter::class.java),
                TypeName.get(service.type)))
            .addMethod(createInjectConstructor(service))
            .build()

    private fun createInjectConstructor(service: ServiceInfo) =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .addParameter(ParameterSpec.builder(CommandRepository::class.java,
                COMMAND_REPOSITORY_VARIABLE_NAME).build())
            .addParameter(ParameterSpec.builder(service.commandRegistryFactoryTypeName,
                service.commandRegistryFactoryParameterName).build())
            .addParameter(ParameterSpec.builder(service.serviceProxyFactoryTypeName,
                service.serviceProxyFactoryParameterName).build())
            .addStatement("super(\$L, \$L, \$L)", COMMAND_REPOSITORY_VARIABLE_NAME,
                service.commandRegistryFactoryParameterName, service.serviceProxyFactoryParameterName)
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
        private const val COMMAND_REPOSITORY_VARIABLE_NAME = "commandRepository"
    }
}
