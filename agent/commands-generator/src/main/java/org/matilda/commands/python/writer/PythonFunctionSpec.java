package org.matilda.commands.python.writer;

import java.util.List;
import java.util.stream.Collectors;

public record PythonFunctionSpec(String name, List<PythonVariable> parameters) {
    public PythonFunctionSpec(String name, PythonVariable... parameters) {
        this(name, List.of(parameters));
    }

    public String getDeclaration() {
        return "def " + name + "(" +
                parameters.stream().map(PythonVariable::getDeclaration).collect(Collectors.joining(", "))
                + ")";
    }
}
