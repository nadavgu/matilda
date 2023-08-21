package org.matilda.commands.python

import org.matilda.commands.utils.Package

interface PythonTypeName {
    val name: String
    val requiredClasses: List<PythonClassName>
}

data class PythonClassName(val packageName: Package, override val name: String): PythonTypeName {
    override val requiredClasses: List<PythonClassName>
        get() = listOf(this)
}

data class PrimitiveTypeName(override val name: String): PythonTypeName {
    override val requiredClasses: List<PythonClassName>
        get() = emptyList()
}