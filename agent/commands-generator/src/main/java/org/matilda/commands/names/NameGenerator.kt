package org.matilda.commands.names

import com.squareup.javapoet.ClassName
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

    val pythonGeneratedServicesContainerPackage: Package
        get() = mPythonProperties.pythonGeneratedPackage.subpackage("services")

    inner class ServiceNameGenerator(private val mServiceFullName: String) {
        private val fullNamePackage: Package
            get() = fromString(mServiceFullName)
        val serviceClassName: String
            get() = fullNamePackage.lastPart

        val serviceSnakeCaseName: String
            get() = serviceClassName.toSnakeCase()

        val serviceFullClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(pythonGeneratedCommandsPackage, serviceClassName)

        val serviceProxyClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("proxies"),
                "${serviceClassName}Proxy"
            )

        private val servicePackage: Package
            get() = fullNamePackage.withoutLastPart()
        private val serviceRelativePackage: Package
            get() = servicePackage.removeCommonPrefixFrom(ORIGINAL_PACKAGE)
        val commandRegistryFactoryClassName: ClassName
            get() = ClassName.get(
                joinPackages(COMMAND_REGISTRY_FACTORIES_PACKAGE, serviceRelativePackage).packageName,
                serviceClassName + "CommandRegistryFactory"
            )
        val javaServiceProxyClassName: ClassName
            get() = ClassName.get(
                joinPackages(JAVA_SERVICE_PROXIES_PACKAGE, serviceRelativePackage).packageName,
                serviceClassName + "Proxy"
            )
        val javaServiceProxyFactoryClassName: ClassName
            get() = ClassName.get(
                joinPackages(JAVA_SERVICE_PROXIES_PACKAGE, serviceRelativePackage).packageName,
                serviceClassName + "ProxyFactory"
            )
        val dependenciesClassName: ClassName
            get() = ClassName.get(
                joinPackages(DEPENDENCIES_CLASSES_PACKAGE, serviceRelativePackage).packageName,
                serviceClassName + "Dependencies"
            )

        val dependenciesPythonClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("dependencies"),
                "${serviceClassName}Dependencies"
            )

        val dynamicServiceConverterClassName: ClassName
            get() = ClassName.get(
                joinPackages(DYNAMIC_SERVICE_CONVERTERS_CLASSES_PACKAGE, serviceRelativePackage).packageName,
                serviceClassName + "Converter"
            )

        inner class CommandNameGenerator(private val mCommandInfo: CommandInfo) {
            val rawCommandClassName: ClassName
                get() = ClassName.get(
                    joinPackages(RAW_COMMAND_CLASSES_PACKAGE, serviceRelativePackage).packageName,
                    serviceClassName + StringUtils.capitalize(mCommandInfo.name) + "Command"
                )
            val fullCommandName: String
                get() = serviceRelativePackage.parts.joinToString(separator = "") {
                    str -> StringUtils.capitalize(str)
                } + rawCommandClassName.simpleName()

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
        val DEPENDENCIES_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("dependencies")
        val DYNAMIC_SERVICE_CONVERTERS_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("converters")
        val COMMAND_REGISTRY_FACTORIES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("registryFactories")
        val JAVA_SERVICE_PROXIES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("proxies")
        val COMMANDS_MODULE_CLASS_NAME: ClassName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            "CommandRegistryModule"
        )
        val SERVICES_MODULE_CLASS_NAME: ClassName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            "ServicesModule"
        )
        val ORIGINAL_PACKAGE = Package("org", "matilda", "commands")
        const val SERVICES_CONTAINER_CLASS_NAME = "Services"
    }
}
