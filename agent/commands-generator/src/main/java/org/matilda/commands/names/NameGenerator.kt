package org.matilda.commands.names

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator.ServiceNameGenerator.CommandNameGenerator
import org.matilda.commands.python.PythonProperties
import org.matilda.commands.utils.Package
import org.matilda.commands.utils.Package.Companion.fromString
import org.matilda.commands.utils.Package.Companion.joinPackages
import javax.inject.Inject

class NameGenerator @Inject internal constructor() {
    @Inject
    lateinit var mPythonProperties: PythonProperties
    val pythonGeneratedCommandsPackage: Package
        get() = mPythonProperties.pythonGeneratedPackage.subpackage("commands")

    inner class ServiceNameGenerator(private val mServiceInfo: ServiceInfo) {
        private val fullNamePackage: Package
            get() = fromString(mServiceInfo.fullName)
        val serviceClassName: String
            get() = fullNamePackage.lastPart
        private val serviceSnakeCaseName: String
            get() {
                val regex = "([a-z])([A-Z]+)".toRegex()

                // Replacement string
                val replacement = "$1_$2"

                // Replace the given regex
                // with replacement string
                // and convert it to lower case.
                return serviceClassName.replace(regex, replacement).lowercase()
            }
        val pythonGeneratedServicePackage: Package
            get() = pythonGeneratedCommandsPackage.subpackage(serviceSnakeCaseName)
        private val servicePackage: Package
            get() = fullNamePackage.withoutLastPart()
        private val serviceRelativePackage: Package
            get() = servicePackage.removeCommonPrefixFrom(ORIGINAL_PACKAGE)

        inner class CommandNameGenerator(private val mCommandInfo: CommandInfo) {
            val rawCommandClassName: String
                get() = serviceClassName + StringUtils.capitalize(mCommandInfo.name) + "Command"
            val rawCommandPackageName: String
                get() = joinPackages(RAW_COMMAND_CLASSES_PACKAGE, serviceRelativePackage).packageName
            val rawCommandTypeName: TypeName
                get() = ClassName.get(rawCommandPackageName, rawCommandClassName)
            val fullCommandName: String
                get() = serviceRelativePackage.parts.joinToString { str -> StringUtils.capitalize(str) } +
                        rawCommandClassName
        }
    }

    fun forService(serviceInfo: ServiceInfo): ServiceNameGenerator {
        return ServiceNameGenerator(serviceInfo)
    }

    fun forCommand(commandInfo: CommandInfo): CommandNameGenerator {
        return forService(commandInfo.service).CommandNameGenerator(commandInfo)
    }

    companion object {
        private val MAIN_GENERATED_PACKAGE = fromString("org.matilda.generated")
        val COMMANDS_GENERATED_PACKAGE = MAIN_GENERATED_PACKAGE.subpackage("commands")
        val RAW_COMMAND_CLASSES_PACKAGE = COMMANDS_GENERATED_PACKAGE.subpackage("raw")
        const val COMMANDS_MODULE_CLASS_NAME = "CommandsModule"
        val COMMANDS_MODULE_TYPE_NAME: TypeName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            COMMANDS_MODULE_CLASS_NAME
        )
        const val SERVICES_MODULE_CLASS_NAME = "ServicesModule"
        val SERVICES_MODULE_TYPE_NAME: TypeName = ClassName.get(
            COMMANDS_GENERATED_PACKAGE.packageName,
            SERVICES_MODULE_CLASS_NAME
        )
        val ORIGINAL_PACKAGE = Package("org", "matilda", "commands")
    }
}
