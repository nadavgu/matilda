package org.matilda.commands.processors

import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
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

class PythonCommandsRegistryFactoryClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ServiceInfo) {
        val pythonFile =
            PythonFile(mNameGenerator.forService(instance).commandRegistryFactoryPythonClassName.packageName)
                .addImports(instance)
                .addClass(instance)

        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(service: ServiceInfo) = apply {
        addRequiredFromImports(pythonTypeName(service))
        addRequiredFromImports(superInterfaceType(service))
        addRequiredFromImports(dependenciesPythonClassName(service))
        addFromImport(COMMAND_REGISTRY_CLASS)
        addFromImport(DEPENDENCY_CLASS)
        addFromImport(DEPENDENCY_CONTAINER_CLASS)
    }

    private fun PythonFile.addClass(service: ServiceInfo) = apply {
        newClass(PythonClassSpec.builder(mNameGenerator.forService(service).commandRegistryFactoryPythonClassName.name)
            .addSuperclass(superInterfaceType(service).name)
            .addSuperclass(DEPENDENCY_CLASS.name)
            .build())
            .addConstructor(service)
            .addDICreator(service)
            .addRegisterCommandsMethod(service)
            .addCreateCommandRegistryMethod(service)
        service.commands.forEach {
            addRequiredFromImports(mNameGenerator.forCommand(it).rawCommandPythonClassName)
        }
    }

    private fun superInterfaceType(service: ServiceInfo) =
        ParameterizedTypeName(COMMAND_REGISTRY_FACTORY_CLASS, listOf(TypeVariable(pythonTypeName(service))))

    private fun PythonClass.addConstructor(service: ServiceInfo) = apply {
        addInstanceMethod(constructorBuilder()
            .addParameter(PYTHON_DEPENDENCIES_PARAMETER_NAME, dependenciesPythonClassName(service).name)
            .build())
            .addStatement("self.%s = %s", PYTHON_DEPENDENCIES_FIELD_NAME, PYTHON_DEPENDENCIES_PARAMETER_NAME)
    }

    private fun PythonClass.addDICreator(service: ServiceInfo) = apply {
        val className = mNameGenerator.forService(service).commandRegistryFactoryPythonClassName.name
        addStaticMethod(functionBuilder("create")
            .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
            .returnTypeHint("'${className}'")
            .build())
            .addStatement("return %s(%s.get(%s))", className, DEPENDENCY_CONTAINER_PARAMETER_NAME,
                dependenciesPythonClassName(service).name)
    }

    private fun dependenciesPythonClassName(service: ServiceInfo) =
        mNameGenerator.forService(service).dependenciesPythonClassName

    private fun PythonClass.addRegisterCommandsMethod(service: ServiceInfo) = apply {
        addInstanceMethod(functionBuilder(REGISTER_COMMANDS_METHOD_NAME)
            .addParameter(COMMAND_REGISTRY_PARAMETER_NAME, COMMAND_REGISTRY_CLASS.name)
            .addParameter(SERVICE_PARAMETER_NAME, pythonTypeName(service).name)
            .build())
            .apply {
                service.commands.forEach { command ->
                    addStatement("%s.add_command(%d, %s(%s, self.%s))", COMMAND_REGISTRY_PARAMETER_NAME,
                        mCommandIdGenerator.generate(command),
                        mNameGenerator.forCommand(command).rawCommandPythonClassName.name,
                        SERVICE_PARAMETER_NAME, PYTHON_DEPENDENCIES_FIELD_NAME)
                }
            }
    }

    private fun PythonClass.addCreateCommandRegistryMethod(service: ServiceInfo) = apply {
        addInstanceMethod(functionBuilder("create_command_registry")
            .addParameter(SERVICE_PARAMETER_NAME, pythonTypeName(service).name)
            .returnTypeHint(COMMAND_REGISTRY_CLASS.name)
            .build())
            .addStatement("%s = %s()", COMMAND_REGISTRY_VARIABLE_NAME, COMMAND_REGISTRY_CLASS.name)
            .addStatement("self.%s(%s, %s)", REGISTER_COMMANDS_METHOD_NAME, COMMAND_REGISTRY_VARIABLE_NAME,
                SERVICE_PARAMETER_NAME)
            .addStatement("return %s", COMMAND_REGISTRY_VARIABLE_NAME)
    }

    private fun pythonTypeName(service: ServiceInfo) = mNameGenerator.forService(service).serviceFullClassName

    companion object {
        private const val REGISTER_COMMANDS_METHOD_NAME = "__register_commands"
        private const val COMMAND_REGISTRY_PARAMETER_NAME = "command_registry"
        private const val COMMAND_REGISTRY_VARIABLE_NAME = "command_registry"
        private const val SERVICE_PARAMETER_NAME = "service"
        private const val PYTHON_DEPENDENCIES_PARAMETER_NAME = "dependencies"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
    }
}
