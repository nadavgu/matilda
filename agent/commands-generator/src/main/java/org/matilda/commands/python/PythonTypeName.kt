package org.matilda.commands.python

import org.matilda.commands.utils.Package

interface PythonTypeName {
    val name: String
    val requiredClasses: List<PythonClassName>

    companion object {
        val FLOAT = PrimitiveTypeName("float")
        val INT = PrimitiveTypeName("int")
        val BOOL = PrimitiveTypeName("bool")
        val STR = PrimitiveTypeName("str")
        val NONE = PrimitiveTypeName("None")
        val BYTES = PrimitiveTypeName("bytes")
        val LIST = PythonClassName(Package("typing"), "List")
        val OPTIONAL = PythonClassName(Package("typing"), "Optional")
    }
}

data class PythonClassName(val packageName: Package, override val name: String): PythonTypeName {
    override val requiredClasses: List<PythonClassName>
        get() = listOf(this)
}

data class PrimitiveTypeName(override val name: String): PythonTypeName {
    override val requiredClasses: List<PythonClassName>
        get() = emptyList()
}