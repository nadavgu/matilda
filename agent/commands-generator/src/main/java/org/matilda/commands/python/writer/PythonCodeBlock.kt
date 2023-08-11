package org.matilda.commands.python.writer

import java.util.stream.Stream

open class PythonCodeBlock protected constructor(
    private val mCodeBlock: CodeBlock,
    private val mEmptyLinesBetweenStuff: Int
) {
    private enum class ElementKind {
        NONE,
        STATEMENT,
        BLOCK
    }

    private var mLastElementKind = ElementKind.NONE

    constructor(codeBlock: CodeBlock = CodeBlock()) : this(codeBlock, NORMAL_EMPTY_LINES)

    fun newFunction(function: PythonFunctionSpec): PythonCodeBlock {
        addSeparatorBeforeBlock()
        addAnnotations(function.annotations)
        mCodeBlock.addStatement("%s:", function.declaration)
        return PythonCodeBlock(createNewCodeBlockWithIndentation())
    }

    private fun addAnnotations(annotations: List<String>) {
        annotations.forEach { annotation -> addStatement("@%s", annotation) }
    }

    fun newClass(classSpec: PythonClassSpec): PythonClass {
        addSeparatorBeforeBlock()
        mCodeBlock.addStatement("%s:", classSpec.declaration)
        return PythonClass(createNewCodeBlockWithIndentation())
    }

    private fun createNewCodeBlockWithIndentation(): CodeBlock {
        mLastElementKind = ElementKind.BLOCK
        return mCodeBlock.newCodeBlockWithIndentation()
    }

    fun addStatement(statement: String): PythonCodeBlock {
        addSeparatorBeforeStatement()
        mLastElementKind = ElementKind.STATEMENT
        mCodeBlock.addStatement(statement)
        return this
    }

    fun addStatement(format: String, vararg args: Any): PythonCodeBlock {
        mCodeBlock.addStatement(format, *args)
        return this
    }

    private fun addSeparatorBeforeStatement() {
        if (mLastElementKind == ElementKind.BLOCK) {
            addSeparator()
        }
    }

    private fun addSeparatorBeforeBlock() {
        if (mLastElementKind != ElementKind.NONE) {
            addSeparator()
        }
    }

    private fun addSeparator() {
        repeat (mEmptyLinesBetweenStuff) {
            mCodeBlock.addEmptyLine()
        }
    }

    open val lines: Stream<String>
        get() = mCodeBlock.lines

    companion object {
        private const val NORMAL_EMPTY_LINES = 2
    }
}
