package org.matilda.commands.java

import org.matilda.commands.utils.Package

data class JavaProperties(val javaMainPackage: Package) {
    companion object {
        const val JAVA_MAIN_PACKAGE_OPTION = "javaMainPackage"
    }
}
