package org.matilda.commands.names

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonProperties
import org.matilda.commands.utils.Package
import org.matilda.commands.utils.Package.Companion.fromString
import org.matilda.commands.utils.Package.Companion.joinPackages
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject

class NameGenerator @Inject internal constructor() {
    @Inject
    lateinit var mPythonProperties: PythonProperties
    val pythonGeneratedCommandsPackage: Package
        get() = mPythonProperties.pythonGeneratedPackage.subpackage("commands")
    val pythonGeneratedCommandsProxiesPackage: Package
        get() = pythonGeneratedCommandsPackage.subpackage("proxies")

    val pythonGeneratedServicesContainerPackage: Package
        get() = mPythonProperties.pythonGeneratedPackage.subpackage("services")

    inner class ServiceNameGenerator(private val mServiceFullName: String) {
        private val fullNamePackage: Package
            get() = fromString(mServiceFullName)
        val serviceClassName: String
            get() = fullNamePackage.lastPart
        val serviceProxyClassName: String
            get() = "${serviceClassName}Proxy"

        val serviceSnakeCaseName: String
            get() = serviceClassName.toSnakeCase()

        private val serviceProxySnakeCaseName: String
            get() = serviceProxyClassName.toSnakeCase()

        val pythonGeneratedServicePackage: Package
            get() = pythonGeneratedCommandsPackage.subpackage(serviceSnakeCaseName)

        val pythonGeneratedServiceProxyPackage: Package
            get() = pythonGeneratedCommandsProxiesPackage.subpackage(serviceProxySnakeCaseName)

        val serviceFullClassName: PythonClassName
            get() = PythonClassName(pythonGeneratedServicePackage, serviceClassName)

        val serviceProxyFullClassName: PythonClassName
            get() = PythonClassName(pythonGeneratedServiceProxyPackage, serviceProxyClassName)

        private val servicePackage: Package
            get() = fullNamePackage.withoutLastPart()
        private val serviceRelativePackage: Package
            get() = servicePackage.removeCommonPrefixFrom(ORIGINAL_PACKAGE)
        val commandRegistryFactoryPackageName: String
            get() = joinPackages(COMMAND_REGISTRY_FACTORIES_PACKAGE, serviceRelativePackage).packageName
        val commandRegistryFactoryClassName: String
            get() = serviceClassName + "CommandRegistryFactory"
        val commandRegistryFactoryTypeName: TypeName
            get() = ClassName.get(commandRegistryFactoryPackageName, commandRegistryFactoryClassName)

        inner class CommandNameGenerator(private val mCommandInfo: CommandInfo) {
            val rawCommandClassName: String
                get() = serviceClassName + StringUtils.capitalize(mCommandInfo.name) + "Command"
            val commandDependenciesClassName: String
                get() = serviceClassName + StringUtils.capitalize(mCommandInfo.name) + "CommandDependencies"
            val rawCommandPackageName: String
                get() = joinPackages(RAW_COMMAND_CLASSES_PACKAGE, serviceRelativePackage).packageName
            val commandDependenciesPackageName: String
                get() = joinPackages(COMMAND_DEPENDENCIES_CLASSES_PACKAGE, serviceRelativePackage).packageName
            val rawCommandTypeName: TypeName
                get() = ClassName.get(rawCommandPackageName, rawCommandClassName)
            val commandDependenciesTypeName: TypeName
                get() = ClassName.get(commandDependenciesPackageName, commandDependenciesClassName)
            val fullCommandName: String
                get() = serviceRelativePackage.parts.joinToString(separator = "") {
                    str -> StringUtils.capitalize(str)
                } + rawCommandClassName

            val snakeCaseName: String
                get() = mCommandInfo.name.toSnakeCase()
        }
    }

    fun forService(serviceInfo: ServiceInfo) = forService(serviceInfo.fullName)
    fun forService(fullName: String) = ServiceNameGenerator(fullName)

    fun forCommand(commandInfo: CommandInfo) = forService(commandInfo.service).CommandNameGenerator(commandInfo)

    companion object {
        private val MAIN_GENERATED_PACKAGE = fromString("org.matilda.generated")
        val COMMANDS_GENERATED_PACKAGE = MAIN_GENERATED_PACKAGE.subpackage("commands")
        val RAW_COMMAND_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("raw")
        val COMMAND_DEPENDENCIES_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("dependencies")
        val COMMAND_REGISTRY_FACTORIES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("registryFactories")
        const val COMMAND_REGISTRY_MODULE_CLASS_NAME = "CommandRegistryModule"
        val COMMANDS_MODULE_TYPE_NAME: TypeName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            COMMAND_REGISTRY_MODULE_CLASS_NAME
        )
        const val SERVICES_MODULE_CLASS_NAME = "ServicesModule"
        val SERVICES_MODULE_TYPE_NAME: TypeName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            SERVICES_MODULE_CLASS_NAME
        )
        val ORIGINAL_PACKAGE = Package("org", "matilda", "commands")
        const val SERVICES_CONTAINER_CLASS_NAME = "Services"
    }
}
