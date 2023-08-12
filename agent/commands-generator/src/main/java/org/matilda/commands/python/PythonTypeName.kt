package org.matilda.commands.python

import org.matilda.commands.utils.Package

interface PythonTypeName {
    val name: String
}
data class PythonClassName(val packageName: Package, override val name: String): PythonTypeName
data class PrimitiveTypeName(override val name: String): PythonTypeName