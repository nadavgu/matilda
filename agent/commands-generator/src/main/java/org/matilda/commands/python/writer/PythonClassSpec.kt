package org.matilda.commands.python.writer

data class PythonClassSpec(val name: String, val superclasses: List<String>,
                           val annotations: List<String> = emptyList()) {
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

    class Builder(private val mName: String) {
        private val mSuperclasses = mutableListOf<String>()
        private val mAnnotations = mutableListOf<String>()

        fun addSuperclass(superclass: String): Builder {
            mSuperclasses.add(superclass)
            return this
        }

        fun addAnnotation(annotation: String): Builder {
            mAnnotations.add(annotation)
            return this
        }

        fun build() = PythonClassSpec(mName, mSuperclasses, mAnnotations)
    }

    companion object {
        fun builder(name: String) = Builder(name)
    }
}
