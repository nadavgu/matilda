package org.matilda.commands.types

import javax.inject.Inject
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class TypeUtilities @Inject constructor() {
    @Inject
    lateinit var mTypes: Types

    @Inject
    lateinit var mElements: Elements

    private fun toTypeMirror(type: Class<*>): TypeMirror =
        mTypes.getDeclaredType(mElements.getTypeElement(type.canonicalName))

    fun isSubtype(type: TypeMirror, parent: Class<*>) = mTypes.isSubtype(type, toTypeMirror(parent))

    fun isAnnotatedWith(type: TypeMirror, annotation: Class<out Annotation>) =
        mTypes.asElement(type) != null && mTypes.asElement(type).getAnnotation(annotation) != null
}