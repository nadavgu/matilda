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
    fun `test that function with annotations is written well`() {
        pythonCodeBlock.newFunction(PythonFunctionSpec.functionBuilder("func")
            .addAnnotation("annotation")
            .build())
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "@annotation",
            "def func():",
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
    fun `test that class instance method is written well`() {
        pythonCodeBlock.newClass(PythonClassSpec("clazz"))
            .addInstanceMethod(PythonFunctionSpec.functionBuilder("func").addParameter("param").build())
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz:",
            "\tdef func(self, param):",
            "\t\tstatement"
        ))
    }

    @Test
    fun `test that class static method is written well`() {
        pythonCodeBlock.newClass(PythonClassSpec("clazz"))
            .addStaticMethod(PythonFunctionSpec.functionBuilder("func").addParameter("param").build())
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz:",
            "\t@staticmethod",
            "\tdef func(param):",
            "\t\tstatement"
        ))
    }
    @Test
    fun `test that constructor is written well`() {
        pythonCodeBlock.newClass(PythonClassSpec("clazz"))
            .addInstanceMethod(PythonFunctionSpec.constructorBuilder().addParameter("param").build())
            .addStatement("statement")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz:",
            "\tdef __init__(self, param):",
            "\t\tstatement"
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
    @Test
    fun `test that only one newline is added between stuff in class`() {
        val classBlock = pythonCodeBlock.newClass(PythonClassSpec("clazz"))
        classBlock.newFunction(PythonFunctionSpec("func1"))
            .addStatement("inFunction1")
        classBlock.newFunction(PythonFunctionSpec("func2"))
            .addStatement("inFunction2")
        assertEquals(codeBlock.lines.toList(), listOf(
            "class clazz:",
            "\tdef func1():",
            "\t\tinFunction1",
            "",
            "\tdef func2():",
            "\t\tinFunction2"
        ))
    }
}