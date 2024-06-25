package org.matilda.commands.python.writer

import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonGlobalElement
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package
import java.util.stream.Collectors
import java.util.stream.Stream

class PythonFile(val packageName: Package) : PythonCodeBlock() {
    private val mImportsCodeBlock = CodeBlock()
    private val mImportedTypes = mutableSetOf<PythonGlobalElement>()

    fun addImport(packageToImport: String): PythonFile {
        mImportsCodeBlock.addStatement("import %s", packageToImport)
        return this
    }

    fun addFromImport(packageToImportFrom: Package, vararg stuffToImport: String): PythonFile {
        mImportsCodeBlock.addStatement(
            "from %s import %s", packageToImportFrom.packageName,
            stuffToImport.joinToString(", ")
        )
        return this
    }

    fun addFromImport(className: PythonClassName) = addFromImport(className.element)

    fun addFromImport(element: PythonGlobalElement): PythonFile {
        if (element !in mImportedTypes) {
            mImportedTypes.add(element)
            addFromImport(element.packageName, element.name)
        }
        return this
    }

    fun addRequiredFromImports(pythonTypeName: PythonTypeName) = apply {
        pythonTypeName.requiredClasses.forEach {
            addFromImport(it)
        }
    }

    override val lines: Stream<String>
        get() {
            val importLines = mImportsCodeBlock.lines.toList()
            val mainLines = super.lines.toList()
            return if (importLines.isNotEmpty() && mainLines.isNotEmpty()) {
                Stream.concat(
                    Stream.concat(
                        importLines.stream(),
                        Stream.of("", "")
                    ), mainLines.stream()
                )
            } else Stream.concat(importLines.stream(), mainLines.stream())
        }
    val content: String
        get() = lines.collect(Collectors.joining("\n")) + "\n"
}
