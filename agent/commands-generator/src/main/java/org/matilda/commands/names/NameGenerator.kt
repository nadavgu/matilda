package org.matilda.commands.names

import com.squareup.javapoet.ClassName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.java.JavaProperties
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

    @Inject
    lateinit var mJavaProperties: JavaProperties

    private val javaMainPackage
        get() = mJavaProperties.javaMainPackage

    private val javaMainGeneratedPackage
        get() = javaMainPackage.subpackage("generated")

    val commandsGeneratedPackage
        get() = javaMainGeneratedPackage.subpackage("commands")

    val rawCommandClassesPackage
        get() = commandsGeneratedPackage.subpackage("raw")
    val dependenciesClassesPackage
        get() = commandsGeneratedPackage.subpackage("dependencies")
    val dynamicServiceConvertersClassesPackage
        get() = commandsGeneratedPackage.subpackage("converters")
    val commandRegistryFactoriesPackage
        get() = commandsGeneratedPackage.subpackage("registryFactories")
    val javaServiceProxiesPackage
        get() = commandsGeneratedPackage.subpackage("proxies")

    val commandsModuleClassName: ClassName
        get() = ClassName.get(commandsGeneratedPackage.packageName, "CommandRegistryModule")

    val servicesModuleClassName: ClassName
        get() = ClassName.get(commandsGeneratedPackage.packageName, "ServicesModule")

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

        private val servicePackage: Package
            get() = fullNamePackage.withoutLastPart()
        private val serviceRelativePackage: Package
            get() = servicePackage.removeCommonPrefixFrom(javaMainPackage)
        val commandRegistryFactoryClassName: ClassName
            get() = ClassName.get(
                joinPackages(commandRegistryFactoriesPackage, serviceRelativePackage).packageName,
                serviceClassName + "CommandRegistryFactory"
            )
        val commandRegistryFactoryPythonClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("registry_factories"),
                "${serviceClassName}CommandRegistryFactory"
            )
        val javaServiceProxyClassName: ClassName
            get() = ClassName.get(
                joinPackages(javaServiceProxiesPackage, serviceRelativePackage).packageName,
                serviceClassName + "Proxy"
            )
        val serviceProxyClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("proxies"),
                "${serviceClassName}Proxy"
            )
        val javaServiceProxyFactoryClassName: ClassName
            get() = ClassName.get(
                joinPackages(javaServiceProxiesPackage, serviceRelativePackage).packageName,
                serviceClassName + "ProxyFactory"
            )
        val pythonServiceProxyFactoryClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("proxies"),
                serviceClassName + "ProxyFactory"
            )
        val dependenciesClassName: ClassName
            get() = ClassName.get(
                joinPackages(dependenciesClassesPackage, serviceRelativePackage).packageName,
                serviceClassName + "Dependencies"
            )

        val dependenciesPythonClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("dependencies"),
                "${serviceClassName}Dependencies"
            )

        val dynamicServiceConverterClassName: ClassName
            get() = ClassName.get(
                joinPackages(dynamicServiceConvertersClassesPackage, serviceRelativePackage).packageName,
                serviceClassName + "Converter"
            )

        val dynamicServiceConverterPythonClassName: PythonClassName
            get() = PythonClassName.createFromParentPackageAndClass(
                pythonGeneratedCommandsPackage.subpackage("converters"),
                serviceClassName + "Converter"
            )

        inner class CommandNameGenerator(private val mCommandInfo: CommandInfo) {
            val rawCommandClassName: ClassName
                get() = ClassName.get(
                    joinPackages(rawCommandClassesPackage, serviceRelativePackage).packageName,
                    serviceClassName + StringUtils.capitalize(mCommandInfo.name) + "Command"
                )
            val rawCommandPythonClassName: PythonClassName
                get() = PythonClassName.createFromParentPackageAndClass(
                    pythonGeneratedCommandsPackage.subpackage("raw"),
                    rawCommandClassName.simpleName()
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
        const val SERVICES_CONTAINER_CLASS_NAME = "Services"
    }
}
