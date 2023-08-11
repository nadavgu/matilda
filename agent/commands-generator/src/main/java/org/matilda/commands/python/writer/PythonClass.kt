package org.matilda.commands.python.writer

class PythonClass internal constructor(codeBlock: CodeBlock) : PythonCodeBlock(codeBlock, CLASS_EMPTY_LINES) {
    fun addInstanceMethod(functionSpec: PythonFunctionSpec): PythonCodeBlock {
        val methodSpec = functionSpec.copyBuilder().addParameterAtStart("self").build()
        return newFunction(methodSpec)
    }

    fun addStaticMethod(functionSpec: PythonFunctionSpec): PythonCodeBlock {
        val methodSpec = functionSpec.copyBuilder().addAnnotation("staticmethod").build()
        return newFunction(methodSpec)
    }

    companion object {
        private const val CLASS_EMPTY_LINES = 1
    }
}
