package org.matilda.commands.python

interface TypeArg {
    val name: String
    val requiredClasses: List<PythonClassName>
}

data class TypeVariable(val type: PythonTypeName) : TypeArg {
    override val name: String
        get() = type.name
    override val requiredClasses: List<PythonClassName>
        get() = type.requiredClasses
}

data class ParameterizedTypeName(val rawTypeName: PythonTypeName, val args: List<TypeArg>): PythonTypeName {
    override val name: String
        get() = String.format("%s[%s]", rawTypeName.name, args.joinToString(", ") { it.name })
    override val requiredClasses: List<PythonClassName>
        get() = rawTypeName.requiredClasses + args.flatMap { it.requiredClasses }
}

fun pythonListType(type: PythonTypeName) = ParameterizedTypeName(PythonTypeName.LIST, listOf(TypeVariable(type)))