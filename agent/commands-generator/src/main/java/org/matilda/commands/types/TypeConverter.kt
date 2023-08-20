package org.matilda.commands.types

import org.matilda.commands.python.writer.PythonClass
import javax.lang.model.type.TypeMirror

interface TypeConverter {
    fun javaConverter(type: TypeMirror): Pair<String, List<Any>>

    fun pythonConverter(type: TypeMirror): Pair<String, List<PythonClass>>

    fun isSupported(type: TypeMirror): Boolean

    val supportedTypesDescription: String
}