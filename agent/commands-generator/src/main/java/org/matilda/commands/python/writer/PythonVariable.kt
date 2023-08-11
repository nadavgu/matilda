package org.matilda.commands.python.writer

data class PythonVariable(val name: String, val typeHint: String?) {
    constructor(name: String) : this(name, null)

    val declaration: String
        get() = if (typeHint == null) {
            name
        } else "$name: $typeHint"
}
