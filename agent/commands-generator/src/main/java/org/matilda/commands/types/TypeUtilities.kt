package org.matilda.commands.types

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import javax.inject.Inject
import kotlin.reflect.KClass

@OptIn(ExperimentalProcessingApi::class)
class TypeUtilities @Inject constructor() {
    @Inject
    lateinit var mProcessingEnv: XProcessingEnv

    private fun toTypeMirror(type: Class<*>): XType? =
        mProcessingEnv.findType(type.canonicalName)

    fun isSubtype(type: XType, parent: Class<*>) = toTypeMirror(parent)?.isAssignableFrom(type) ?: false

    fun isAnnotatedWith(type: XType, annotation: KClass<out Annotation>) =
        type.typeElement?.getAnnotation(annotation) != null
}