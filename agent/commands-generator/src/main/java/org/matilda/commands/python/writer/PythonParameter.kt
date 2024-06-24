package org.matilda.commands.python.writer

data class PythonParameter(val variable: PythonVariable, val defaultValue: String?) {
    constructor(variable: PythonVariable) : this(variable, null)
    constructor(name: String) : this(PythonVariable(name))

    val declaration: String
        get() = if (defaultValue == null) {
            variable.declaration
        } else "${variable.declaration} = $defaultValue"
}
