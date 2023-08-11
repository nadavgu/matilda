package org.matilda.commands.python.writer;

import java.util.List;
import java.util.stream.Stream;

public class PythonCodeBlock {
    private enum ElementKind {
        NONE,
        STATEMENT,
        BLOCK,
    }
    private static final int NORMAL_EMPTY_LINES = 2;

    private final CodeBlock mCodeBlock;
    private final int mEmptyLinesBetweenStuff;
    private ElementKind mLastElementKind;

    public PythonCodeBlock() {
        this(new CodeBlock());
    }

    public PythonCodeBlock(CodeBlock codeBlock) {
        this(codeBlock, NORMAL_EMPTY_LINES);
    }
    protected PythonCodeBlock(CodeBlock codeBlock, int emptyLines) {
        mCodeBlock = codeBlock;
        mEmptyLinesBetweenStuff = emptyLines;
        mLastElementKind = ElementKind.NONE;
    }

    public PythonCodeBlock newFunction(PythonFunctionSpec function) {
        addSeparatorBeforeBlock();
        addAnnotations(function.annotations());
        mCodeBlock.addStatement("%s:", function.getDeclaration());
        return new PythonCodeBlock(createNewCodeBlockWithIndentation());
    }

    private void addAnnotations(List<String> annotations) {
        annotations.forEach(annotation -> addStatement("@%s", annotation));
    }

    public PythonClass newClass(PythonClassSpec classSpec) {
        addSeparatorBeforeBlock();
        mCodeBlock.addStatement("%s:", classSpec.getDeclaration());
        return new PythonClass(createNewCodeBlockWithIndentation());
    }
    private CodeBlock createNewCodeBlockWithIndentation() {
        mLastElementKind = ElementKind.BLOCK;
        return mCodeBlock.newCodeBlockWithIndentation();
    }

    public PythonCodeBlock addStatement(String statement) {
        addSeparatorBeforeStatement();
        mLastElementKind = ElementKind.STATEMENT;
        mCodeBlock.addStatement(statement);
        return this;
    }

    public PythonCodeBlock addStatement(String format, Object... args) {
        mCodeBlock.addStatement(format, args);
        return this;
    }

    private void addSeparatorBeforeStatement() {
        if (mLastElementKind == ElementKind.BLOCK) {
            addSeparator();
        }
    }

    private void addSeparatorBeforeBlock() {
        if (mLastElementKind != ElementKind.NONE) {
            addSeparator();
        }
    }

    public void addSeparator() {
        for (int i = 0; i < mEmptyLinesBetweenStuff; i++) {
            mCodeBlock.addEmptyLine();
        }
    }

    public Stream<String> getLines() {
        return mCodeBlock.getLines();
    }
}
