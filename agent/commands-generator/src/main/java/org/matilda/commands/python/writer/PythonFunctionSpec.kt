package org.matilda.commands.python.writer

data class PythonFunctionSpec(
    val name: String,
    val parameters: List<PythonParameter>,
    val annotations: List<String>,
    val returnTypeHint: String?
) {
    constructor(name: String, vararg parameters: PythonParameter) : this(
        name,
        listOf<PythonParameter>(*parameters),
        listOf<String>(),
        null
    )

    val declaration: String
        get() {
            val builder = StringBuilder("def ")
                .append(name)
                .append("(")
                .append(parameters.joinToString(", ") { variable -> variable.declaration })
                .append(")")
            if (returnTypeHint != null) {
                builder.append(" -> ").append(returnTypeHint)
            }
            return builder.toString()
        }

    fun copyBuilder() =
        Builder(name).addParameters(parameters).addAnnotations(annotations).returnTypeHint(returnTypeHint)

    class Builder(private val mName: String) {
        private val mParameters = mutableListOf<PythonParameter>()
        private val mAnnotations = mutableListOf<String>()
        private var mReturnTypeHint: String? = null

        fun addParameters(parameters: List<PythonParameter>): Builder {
            mParameters.addAll(parameters)
            return this
        }

        fun addParameter(parameter: String): Builder {
            mParameters.add(PythonParameter(parameter))
            return this
        }

        fun addParameter(parameter: String, typeHint: String): Builder {
            mParameters.add(PythonParameter(PythonVariable(parameter, typeHint)))
            return this
        }

        fun addParameter(parameter: String, typeHint: String, defaultValue: String): Builder {
            mParameters.add(PythonParameter(PythonVariable(parameter, typeHint), defaultValue))
            return this
        }

        fun addParameterAtStart(parameter: String): Builder {
            mParameters.add(0, PythonParameter(parameter))
            return this
        }

        fun addAnnotations(annotations: List<String>): Builder {
            mAnnotations.addAll(annotations)
            return this
        }

        fun addAnnotation(annotation: String): Builder {
            mAnnotations.add(annotation)
            return this
        }

        fun returnTypeHint(returnTypeHint: String?): Builder {
            mReturnTypeHint = returnTypeHint
            return this
        }

        fun build() = PythonFunctionSpec(mName, mParameters, mAnnotations, mReturnTypeHint)
    }

    companion object {
        fun functionBuilder(name: String) = Builder(name)

        fun constructorBuilder() = functionBuilder("__init__")

        private fun propertyBuilder(name: String) = functionBuilder(name).addAnnotation("property")

        fun property(name: String, returnTypeHint: String? = null) =
            propertyBuilder(name).returnTypeHint(returnTypeHint).build()
    }
}
