package org.matilda.commands.python.writer;

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

    public PythonCodeBlock(CodeBlock codeBlock) {
        this(codeBlock, NORMAL_EMPTY_LINES);
    }
    protected PythonCodeBlock(CodeBlock codeBlock, int emptyLines) {
        mCodeBlock = codeBlock;
        mEmptyLinesBetweenStuff = emptyLines;
        mLastElementKind = ElementKind.NONE;
    }

    public PythonCodeBlock newFunction(PythonFunctionSpec function) {
        addEmptyLinesBeforeBlock();
        mCodeBlock.addStatement("%s:", function.getDeclaration());
        return new PythonCodeBlock(createNewCodeBlockWithIndentation());
    }

    public PythonClass newClass(PythonClassSpec classSpec) {
        addEmptyLinesBeforeBlock();
        mCodeBlock.addStatement("%s:", classSpec.getDeclaration());
        return new PythonClass(createNewCodeBlockWithIndentation());
    }
    private CodeBlock createNewCodeBlockWithIndentation() {
        mLastElementKind = ElementKind.BLOCK;
        return mCodeBlock.newCodeBlockWithIndentation();
    }

    public PythonCodeBlock addStatement(String statement) {
        addEmptyLinesBeforeStatement();
        mLastElementKind = ElementKind.STATEMENT;
        mCodeBlock.addStatement(statement);
        return this;
    }

    public PythonCodeBlock addStatement(String format, Object... args) {
        mCodeBlock.addStatement(format, args);
        return this;
    }

    private void addEmptyLinesBeforeStatement() {
        if (mLastElementKind == ElementKind.BLOCK) {
            addEmptyLines();
        }
    }

    private void addEmptyLinesBeforeBlock() {
        if (mLastElementKind != ElementKind.NONE) {
            addEmptyLines();
        }
    }

    private void addEmptyLines() {
        for (int i = 0; i < mEmptyLinesBetweenStuff; i++) {
            mCodeBlock.addEmptyLine();
        }
    }
}
