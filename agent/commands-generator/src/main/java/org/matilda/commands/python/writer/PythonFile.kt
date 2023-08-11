package org.matilda.commands.python.writer;

import org.matilda.commands.python.PythonClassName;
import org.matilda.commands.utils.Package;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PythonFile extends PythonCodeBlock {
    private final CodeBlock mImportsCodeBlock;
    private final Package mPackage;

    public PythonFile(Package filePackage) {
        mPackage = filePackage;
        mImportsCodeBlock = new CodeBlock();
    }

    public PythonFile addImport(String packageToImport) {
        mImportsCodeBlock.addStatement("import %s", packageToImport);
        return this;
    }

    public PythonFile addFromImport(Package packageToImportFrom, String... stuffToImport) {
        mImportsCodeBlock.addStatement("from %s import %s", packageToImportFrom.getPackageName(),
                String.join(", ", stuffToImport));
        return this;
    }

    public PythonFile addFromImport(PythonClassName pythonClassName) {
        return addFromImport(pythonClassName.packageName(), pythonClassName.className());
    }

    public Stream<String> getLines() {
        var importLines = mImportsCodeBlock.getLines().toList();
        var mainLines = super.getLines().toList();


        if (!importLines.isEmpty() && !mainLines.isEmpty()) {
            return Stream.concat(Stream.concat(importLines.stream(), Stream.of("", "")), mainLines.stream());
        }

        return Stream.concat(importLines.stream(), mainLines.stream());
    }

    public String getContent() {
        return getLines().collect(Collectors.joining("\n")) + "\n";
    }

    public Package getPackage() {
        return mPackage;
    }
}
