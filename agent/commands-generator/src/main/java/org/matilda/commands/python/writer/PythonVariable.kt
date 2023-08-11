package org.matilda.commands.python.writer;

public record PythonVariable(String name, String typeHint) {
    public PythonVariable(String name) {
        this(name, null);
    }

    public String getDeclaration() {
        if (typeHint == null) {
            return name;
        }
        return name + ": " + typeHint;
    }
}
