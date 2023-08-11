package org.matilda.commands.python.writer;

import java.util.List;

public record PythonClassSpec(String name, List<String> superclasses) {
    public PythonClassSpec(String name, String... superclasses) {
        this(name, List.of(superclasses));
    }

    public String getDeclaration() {
        StringBuilder stringBuilder = new StringBuilder("class ")
                .append(name);
        if (!superclasses.isEmpty()) {
            stringBuilder.append("(")
                    .append(String.join(", ", superclasses))
                    .append(")");
        }

        return stringBuilder.toString();
    }
}
