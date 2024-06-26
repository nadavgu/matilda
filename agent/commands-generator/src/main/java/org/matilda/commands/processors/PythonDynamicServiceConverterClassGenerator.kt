package org.matilda.commands.processors

import org.apache.commons.lang3.StringUtils
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.*
import org.matilda.commands.python.writer.PythonClass
import org.matilda.commands.python.writer.PythonClassSpec
import org.matilda.commands.python.writer.PythonFile
import org.matilda.commands.python.writer.PythonFileWriter
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.constructorBuilder
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.functionBuilder
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.DYNAMIC_CONVERTER_CLASS
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.PYTHON_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject

class PythonDynamicServiceConverterClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mNameGenerator: NameGenerator

    override fun process(instance: ServiceInfo) {
        val pythonFile =
            PythonFile(mNameGenerator.forService(instance).dynamicServiceConverterPythonClassName.packageName)
                .addImports(instance)
                .addClass(instance)

        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(service: ServiceInfo) = apply {
        addRequiredFromImports(pythonTypeName(service))
        addRequiredFromImports(service.commandRegistryFactoryTypeName)
        addRequiredFromImports(service.serviceProxyFactoryTypeName)
        addRequiredFromImports(superclassType(service))
        addFromImport(DEPENDENCY_CLASS)
        addFromImport(DEPENDENCY_CONTAINER_CLASS)
        addFromImport(COMMAND_REPOSITORY_CLASS)
    }

    private fun PythonFile.addClass(service: ServiceInfo) = apply {
        newClass(PythonClassSpec.builder(mNameGenerator.forService(service).dynamicServiceConverterPythonClassName.name)
            .addSuperclass(superclassType(service).name)
            .addSuperclass(DEPENDENCY_CLASS.name)
            .build())
            .addConstructor(service)
            .addDICreator(service)
    }

    private fun superclassType(service: ServiceInfo) =
        ParameterizedTypeName(DYNAMIC_CONVERTER_CLASS, listOf(TypeVariable(pythonTypeName(service))))

    private fun PythonClass.addConstructor(service: ServiceInfo) = apply {
        addInstanceMethod(constructorBuilder()
            .addParameter(COMMAND_REPOSITORY_PARAMETER_NAME, COMMAND_REPOSITORY_CLASS.name)
            .addParameter(service.commandRegistryFactoryParameterName, service.commandRegistryFactoryTypeName.name)
            .addParameter(service.serviceProxyFactoryParameterName, service.serviceProxyFactoryTypeName.name)
            .build())
            .addStatement("super(%s, self).__init__(%s, %s, %s)",
                mNameGenerator.forService(service).dynamicServiceConverterPythonClassName.name,
                COMMAND_REPOSITORY_PARAMETER_NAME, service.commandRegistryFactoryParameterName,
                service.serviceProxyFactoryParameterName)
    }

    private fun PythonClass.addDICreator(service: ServiceInfo) = apply {
        val className = mNameGenerator.forService(service).dynamicServiceConverterPythonClassName.name
        addStaticMethod(functionBuilder("create")
            .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
            .returnTypeHint("'${className}'")
            .build())
            .addStatement("return %s(%s.get(%s), %s.get(%s), %s.get(%s))", className,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_REPOSITORY_CLASS.name,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, service.commandRegistryFactoryTypeName.name,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, service.serviceProxyFactoryTypeName.name)
    }


    private val ServiceInfo.commandRegistryFactoryParameterName
        get() = commandRegistryFactoryTypeName.name.toSnakeCase()

    private val ServiceInfo.serviceProxyFactoryParameterName
        get() = serviceProxyFactoryTypeName.name.toSnakeCase()

    private val ServiceInfo.commandRegistryFactoryTypeName
        get() = mNameGenerator.forService(this).commandRegistryFactoryPythonClassName

    private val ServiceInfo.serviceProxyFactoryTypeName
        get() = mNameGenerator.forService(this).pythonServiceProxyFactoryClassName

    private fun pythonTypeName(service: ServiceInfo) = mNameGenerator.forService(service).serviceFullClassName

    companion object {
        private const val PYTHON_DEPENDENCIES_PARAMETER_NAME = "dependencies"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val COMMAND_REPOSITORY_PARAMETER_NAME = "command_repository"
    }
}
