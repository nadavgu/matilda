package org.matilda.commands.python.writer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CodeBlockTest  {
    private val codeBlock = CodeBlock()

    @Test
    fun `test that formatted add statement works`() {
        codeBlock.addStatement("hello%d", 1)
        assertEquals(codeBlock.lines.toList(), listOf("hello1"))
    }

    @Test
    fun `test that inner code block works well`() {
        codeBlock.addStatement("outerStatement1")
        val innerCodeBlock = codeBlock.newCodeBlock()
        codeBlock.addStatement("outerStatement2")
        innerCodeBlock.addStatement("innerStatement1")
        innerCodeBlock.addStatement("innerStatement2")
        assertEquals(codeBlock.lines.toList(), listOf("outerStatement1", "innerStatement1",
            "innerStatement2", "outerStatement2"))
    }

    @Test
    fun `test that indented code block works well`() {
        val indentedCodeBlock = codeBlock.newCodeBlockWithIndentation()
        indentedCodeBlock.addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf("\tstatement"))
    }
}