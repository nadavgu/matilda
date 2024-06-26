package org.matilda.commands.processors

import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.*
import org.matilda.commands.python.writer.PythonClass
import org.matilda.commands.python.writer.PythonClassSpec
import org.matilda.commands.python.writer.PythonFile
import org.matilda.commands.python.writer.PythonFileWriter
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.constructorBuilder
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.functionBuilder
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.PYTHON_DEPENDENCIES_FIELD_NAME
import javax.inject.Inject

class PythonServiceProxyFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mNameGenerator: NameGenerator

    override fun process(instance: ServiceInfo) {
        val pythonFile =
            PythonFile(mNameGenerator.forService(instance).pythonServiceProxyFactoryClassName.packageName)
                .addImports(instance)
                .addClass(instance)

        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(service: ServiceInfo) = apply {
        addRequiredFromImports(pythonTypeName(service))
        addRequiredFromImports(pythonProxyTypeName(service))
        addRequiredFromImports(superInterfaceType(service))
        addRequiredFromImports(dependenciesPythonClassName(service))
        addFromImport(COMMAND_RUNNER_CLASS)
        addFromImport(DEPENDENCY_CLASS)
        addFromImport(DEPENDENCY_CONTAINER_CLASS)
    }

    private fun PythonFile.addClass(service: ServiceInfo) = apply {
        newClass(PythonClassSpec.builder(mNameGenerator.forService(service).pythonServiceProxyFactoryClassName.name)
            .addSuperclass(superInterfaceType(service).name)
            .addSuperclass(DEPENDENCY_CLASS.name)
            .build())
            .addConstructor(service)
            .addDICreator(service)
            .addCreateServiceProxyMethod(service)
    }

    private fun superInterfaceType(service: ServiceInfo) =
        ParameterizedTypeName(SERVICE_PROXY_FACTORY_CLASS, listOf(TypeVariable(pythonTypeName(service))))

    private fun PythonClass.addConstructor(service: ServiceInfo) = apply {
        addInstanceMethod(constructorBuilder()
            .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name)
            .addParameter(PYTHON_DEPENDENCIES_PARAMETER_NAME, dependenciesPythonClassName(service).name)
            .build())
            .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
            .addStatement("self.%s = %s", PYTHON_DEPENDENCIES_FIELD_NAME, PYTHON_DEPENDENCIES_PARAMETER_NAME)
    }

    private fun PythonClass.addDICreator(service: ServiceInfo) = apply {
        val className = mNameGenerator.forService(service).pythonServiceProxyFactoryClassName.name
        addStaticMethod(functionBuilder("create")
            .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
            .returnTypeHint("'${className}'")
            .build())
            .addStatement("return %s(%s.get(%s), %s.get(%s))", className,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.name,
                DEPENDENCY_CONTAINER_PARAMETER_NAME, dependenciesPythonClassName(service).name)
    }

    private fun dependenciesPythonClassName(service: ServiceInfo) =
        mNameGenerator.forService(service).dependenciesPythonClassName

    private fun PythonClass.addCreateServiceProxyMethod(service: ServiceInfo) = apply {
        addInstanceMethod(functionBuilder("create_service_proxy")
            .addParameter(COMMAND_REGISTRY_ID_PARAMETER_NAME, PythonTypeName.INT.name)
            .returnTypeHint(pythonTypeName(service).name)
            .build())
            .addStatement("return %s(self.%s, self.%s, %s)", pythonProxyTypeName(service).name,
                COMMAND_RUNNER_FIELD_NAME, PYTHON_DEPENDENCIES_FIELD_NAME, COMMAND_REGISTRY_ID_PARAMETER_NAME)
    }

    private fun pythonTypeName(service: ServiceInfo) = mNameGenerator.forService(service).serviceFullClassName
    private fun pythonProxyTypeName(service: ServiceInfo) = mNameGenerator.forService(service).serviceProxyClassName

    companion object {
        private const val PYTHON_DEPENDENCIES_PARAMETER_NAME = "dependencies"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val COMMAND_RUNNER_FIELD_NAME = "__command_runner"
        private const val COMMAND_RUNNER_PARAMETER_NAME = "command_runner"
        private const val COMMAND_REGISTRY_ID_PARAMETER_NAME = "command_registry_id"
    }
}
