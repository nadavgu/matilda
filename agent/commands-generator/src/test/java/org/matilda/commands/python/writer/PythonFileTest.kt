package org.matilda.commands.python.writer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PythonFileTest  {
    private val pythonFile = PythonFile()

    @Test
    fun `test import statement works well`() {
        pythonFile.addImport("package")
        assertEquals(listOf("import package"), pythonFile.lines.toList())
    }

    @Test
    fun `test from import statement works well`() {
        pythonFile.addFromImport("package", "a", "b", "c")
        assertEquals(listOf("from package import a, b, c"), pythonFile.lines.toList())
    }

    @Test
    fun `test separator between imports and statements`() {
        pythonFile.addImport("package")
        pythonFile.addStatement("statement")
        pythonFile.addImport("package2")
        assertEquals(
            listOf("import package",
            "import package2",
            "",
            "",
            "statement"
            ), pythonFile.lines.toList())
    }

    @Test
    fun `test no separator if no imports`() {
        pythonFile.addStatement("statement")
        assertEquals(listOf("statement"), pythonFile.lines.toList())
    }
}