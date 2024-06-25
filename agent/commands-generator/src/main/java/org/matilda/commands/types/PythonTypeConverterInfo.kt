package org.matilda.commands.types

import org.matilda.commands.python.PythonTypeName

data class PythonDependencyInfo(val typeName: PythonTypeName, val variableName: String)
data class PythonTypeConverterInfo(val converterFormat: String,
                                   val converterRequiredTypes: List<PythonTypeName>,
                                   val dependencies: List<PythonDependencyInfo> = emptyList())
