package org.matilda.commands.python.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CodeBlock implements Token {
    private final String mPrefix;
    private final List<Token> mTokens;

    public CodeBlock() {
        this("");
    }

    private CodeBlock(String prefix) {
        mPrefix = prefix;
        mTokens = new ArrayList<>();
    }

    public void addStatement(String statement) {
        mTokens.add(new Statement(mPrefix + statement));
    }

    public void addStatement(String format, Object... args) {
        addStatement(String.format(format, args));
    }

    public void addEmptyLine() {
        mTokens.add(new Statement(""));
    }

    public CodeBlock newCodeBlock() {
        return newCodeBlock(mPrefix);
    }

    public CodeBlock newCodeBlockWithIndentation() {
        return newCodeBlock(mPrefix + "\t");
    }

    private CodeBlock newCodeBlock(String prefix) {
        CodeBlock codeBlock = new CodeBlock(prefix);
        mTokens.add(codeBlock);
        return codeBlock;
    }

    @Override
    public Stream<String> getLines() {
        return mTokens.stream().flatMap(Token::getLines);
    }
}
