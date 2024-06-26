package org.matilda.commands.python

import org.matilda.commands.utils.Package
import org.matilda.commands.utils.toSnakeCase

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

data class PythonClassName(val element: PythonGlobalElement):
    PythonTypeName {
    constructor(packageName: Package, name: String) : this(PythonGlobalElement(packageName, name))
    override val name: String
        get() = element.name
    val packageName: Package
        get() = element.packageName
    override val requiredClasses: List<PythonClassName>
        get() = listOf(this)

    companion object {
        fun createFromParentPackageAndClass(parentPackage: Package, name: String) =
            PythonClassName(parentPackage.subpackage(name.toSnakeCase()), name)
    }
}

data class PrimitiveTypeName(override val name: String): PythonTypeName {
    override val requiredClasses: List<PythonClassName>
        get() = emptyList()
}