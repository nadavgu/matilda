package org.matilda.commands.python.writer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PythonFile extends PythonCodeBlock {
    private final CodeBlock mImportsCodeBlock;


    public PythonFile() {
        mImportsCodeBlock = new CodeBlock();
    }

    public PythonFile addImport(String packageToImport) {
        mImportsCodeBlock.addStatement("import %s", packageToImport);
        return this;
    }

    public PythonFile addFromImport(String packageToImportFrom, String... stuffToImport) {
        mImportsCodeBlock.addStatement("from %s import %s", packageToImportFrom,
                String.join(", ", stuffToImport));
        return this;
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
}
