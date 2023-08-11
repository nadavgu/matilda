package org.matilda.commands.python.writer;

import java.util.Arrays;
import java.util.stream.Collectors;

public record PythonFunction(String name, PythonVariable... parameters) {
    public String getDeclaration() {
        return "def " + name + "(" +
                Arrays.stream(parameters).map(PythonVariable::getDeclaration).collect(Collectors.joining(", "))
                + ")";
    }
}
