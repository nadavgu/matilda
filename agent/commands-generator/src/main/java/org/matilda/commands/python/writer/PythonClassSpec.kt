package org.matilda.commands.python.writer

data class PythonClassSpec(val name: String, val superclasses: List<String>) {
    constructor(name: String, vararg superclasses: String) : this(name, listOf(*superclasses))

    val declaration: String
        get() {
            val stringBuilder = StringBuilder("class ")
                .append(name)
            if (superclasses.isNotEmpty()) {
                stringBuilder.append("(")
                    .append(superclasses.joinToString(", "))
                    .append(")")
            }
            return stringBuilder.toString()
        }
}
