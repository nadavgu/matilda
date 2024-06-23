package org.matilda.commands.types

import com.squareup.javapoet.TypeName

data class DependencyInfo(val typeName: TypeName, val variableName: String)
data class JavaTypeConverterInfo(val converterFormat: String,
                                 val converterArgs: List<Any>,
                                 val dependencies: List<DependencyInfo> = emptyList())
