package org.matilda.commands.java

import org.matilda.commands.utils.Package

data class JavaProperties(val javaMainPackage: Package, val shouldGenerateKotlin: Boolean) {
    companion object {
        const val JAVA_MAIN_PACKAGE_OPTION = "javaMainPackage"
        const val SHOULD_GENERATE_KOTLIN = "generateKotlin"
        const val DI_FRAMEWORK = "diFramework"
    }
}
