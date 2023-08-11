package org.matilda.commands.python.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record PythonFunctionSpec(String name, List<PythonVariable> parameters, List<String> annotations,
                                 String returnTypeHint) {
    public PythonFunctionSpec(String name, PythonVariable... parameters) {
        this(name, List.of(parameters), List.of(), null);
    }

    public String getDeclaration() {
        StringBuilder builder = new StringBuilder("def ")
                .append(name)
                .append("(")
                .append(parameters.stream()
                        .map(PythonVariable::getDeclaration)
                        .collect(Collectors.joining(", ")))
                .append(")");
        if (returnTypeHint != null) {
            builder.append(" -> ").append(returnTypeHint);
        }

        return builder.toString();
    }

    public static Builder functionBuilder(String name) {
        return new Builder(name);
    }

    public static Builder constructorBuilder() {
        return functionBuilder("__init__");
    }

    public Builder copyBuilder() {
        return new Builder(name).addParameters(parameters).addAnnotations(annotations);
    }

    public static class Builder {
        private final String mName;
        private final List<PythonVariable> mParameters;
        private final List<String> mAnnotations;
        private String mReturnTypeHint;

        private Builder(String name) {
            mName = name;
            mParameters = new ArrayList<>();
            mAnnotations = new ArrayList<>();
            mReturnTypeHint = null;
        }

        public Builder addParameters(List<PythonVariable> parameters) {
            mParameters.addAll(parameters);
            return this;
        }

        public Builder addParameter(String parameter) {
            mParameters.add(new PythonVariable(parameter));
            return this;
        }

        public Builder addParameter(String parameter, String typeHint) {
            mParameters.add(new PythonVariable(parameter, typeHint));
            return this;
        }

        public Builder addParameterAtStart(String parameter) {
            mParameters.add(0, new PythonVariable(parameter));
            return this;
        }

        public Builder addAnnotations(List<String> annotations) {
            mAnnotations.addAll(annotations);
            return this;
        }

        public Builder addAnnotation(String annotation) {
            mAnnotations.add(annotation);
            return this;
        }

        public Builder returnTypeHint(String returnTypeHint) {
            mReturnTypeHint = returnTypeHint;
            return this;
        }

        public PythonFunctionSpec build() {
            return new PythonFunctionSpec(mName, mParameters, mAnnotations, mReturnTypeHint);
        }
    }
}
