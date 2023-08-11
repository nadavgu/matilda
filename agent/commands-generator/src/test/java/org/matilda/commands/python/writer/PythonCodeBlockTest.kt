package org.matilda.commands.python.writer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PythonCodeBlockTest  {
    private val codeBlock = CodeBlock()
    private val pythonCodeBlock = PythonCodeBlock(codeBlock)

    @Test
    fun `test that function is written well`() {
        pythonCodeBlock.newFunction(
            PythonFunctionSpec(
                "func",
                PythonVariable("var1"),
                PythonVariable("var2", "type2")
            )
        )
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "def func(var1, var2: type2):",
            "\tstatement"
        ))
    }

    @Test
    fun `test that class is written well with superclasses`() {
        pythonCodeBlock.newClass(
            PythonClassSpec(
                "clazz",
                "super1",
                "super2"
            )
        )
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz(super1, super2):",
            "\tstatement"
        ))
    }

    @Test
    fun `test that class is written well without superclasses`() {
        pythonCodeBlock.newClass(
            PythonClassSpec(
                "clazz"
            )
        )
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz:",
            "\tstatement"
        ))
    }

    @Test
    fun `test that newlines are added before stuff`() {
        pythonCodeBlock.addStatement("beforeFunction")
        pythonCodeBlock.newFunction(PythonFunctionSpec("func"))
            .addStatement("inFunction")
        assertEquals(codeBlock.lines.toList(), listOf(
            "beforeFunction",
            "",
            "",
            "def func():",
            "\tinFunction"
        ))
    }

    @Test
    fun `test that newlines are added after stuff`() {
        pythonCodeBlock.newFunction(PythonFunctionSpec("func"))
            .addStatement("inFunction")
        pythonCodeBlock.addStatement("afterFunction")
        assertEquals(codeBlock.lines.toList(), listOf(
            "def func():",
            "\tinFunction",
            "",
            "",
            "afterFunction"
        ))
    }

    @Test
    fun `test that newlines are not added between statements`() {
        pythonCodeBlock
            .addStatement("statement%d", 1)
            .addStatement("statement%d", 2)
        assertEquals(codeBlock.lines.toList(), listOf(
            "statement1",
            "statement2"
        ))
    }

    @Test
    fun `test that newlines are added between stuff`() {
        pythonCodeBlock.newFunction(PythonFunctionSpec("func1"))
            .addStatement("inFunction1")
        pythonCodeBlock.newFunction(PythonFunctionSpec("func2"))
            .addStatement("inFunction2")
        assertEquals(codeBlock.lines.toList(), listOf(
            "def func1():",
            "\tinFunction1",
            "",
            "",
            "def func2():",
            "\tinFunction2"
        ))
    }
}