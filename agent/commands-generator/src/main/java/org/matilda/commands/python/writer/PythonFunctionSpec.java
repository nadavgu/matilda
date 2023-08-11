package org.matilda.commands.python.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record PythonFunctionSpec(String name, List<PythonVariable> parameters, List<String> annotations) {
    public PythonFunctionSpec(String name, PythonVariable... parameters) {
        this(name, List.of(parameters), List.of());
    }

    public String getDeclaration() {
        return "def " + name + "(" +
                parameters.stream().map(PythonVariable::getDeclaration).collect(Collectors.joining(", "))
                + ")";
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

        private Builder(String name) {
            mName = name;
            mParameters = new ArrayList<>();
            mAnnotations = new ArrayList<>();
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

        public PythonFunctionSpec build() {
            return new PythonFunctionSpec(mName, mParameters, mAnnotations);
        }
    }
}
