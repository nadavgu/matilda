package org.matilda.commands.python.writer;

public class PythonClass extends PythonCodeBlock {
    private static final int CLASS_EMPTY_LINES = 1;

    PythonClass(CodeBlock codeBlock) {
        super(codeBlock, CLASS_EMPTY_LINES);
    }

    public PythonCodeBlock addInstanceMethod(PythonFunctionSpec functionSpec) {
        PythonFunctionSpec methodSpec = functionSpec.copyBuilder().addParameterAtStart("self").build();
        return newFunction(methodSpec);
    }
}
