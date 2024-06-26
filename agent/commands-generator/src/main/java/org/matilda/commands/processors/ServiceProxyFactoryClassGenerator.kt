package org.matilda.commands.processors

import com.squareup.javapoet.*
import org.matilda.commands.CommandRunner
import org.matilda.commands.ServiceProxyFactory
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.DEPENDENCIES_FIELD_NAME
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class ServiceProxyFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).javaServiceProxyFactoryPackageName,
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).javaServiceProxyFactoryClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ServiceProxyFactory::class.java),
                TypeName.get(service.type)))
            .addMethod(createInjectConstructor())
            .addField(createCommandRunnerField())
            .addField(createDependenciesField(service))
            .addMethod(createServiceProxyMethod(service))
            .build()

    private fun createCommandRunnerField() =
        FieldSpec.builder(CommandRunner::class.java, COMMAND_RUNNER_FIELD_NAME)
            .addAnnotation(Inject::class.java)
            .build()


    private fun createDependenciesField(service: ServiceInfo) =
        FieldSpec.builder(mNameGenerator.forService(service).dependenciesTypeName, DEPENDENCIES_FIELD_NAME)
            .addAnnotation(Inject::class.java)
            .build()

    private fun createInjectConstructor() =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()

    private fun createServiceProxyMethod(service: ServiceInfo) =
        MethodSpec.methodBuilder("createServiceProxy")
            .addAnnotation(Override::class.java)
            .returns(TypeName.get(service.type))
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(TypeName.INT, COMMAND_REGISTRY_ID_PARAMETER_NAME).build())
            .addStatement("return new \$T(\$L, \$L, \$L)", mNameGenerator.forService(service).javaServiceProxyTypeName,
                COMMAND_RUNNER_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME, DEPENDENCIES_FIELD_NAME)
            .build()

    companion object {
        private const val COMMAND_RUNNER_FIELD_NAME = "mCommandRunner"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "commandRegistryId"
    }
}
