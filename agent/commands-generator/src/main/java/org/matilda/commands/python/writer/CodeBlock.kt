package org.matilda.commands.python.writer

import java.util.stream.Stream

class CodeBlock private constructor(private val mPrefix: String) : Token {
    private val mTokens = mutableListOf<Token>()

    constructor() : this("")

    fun addStatement(statement: String) {
        mTokens.add(Statement(mPrefix + statement))
    }

    fun addStatement(format: String, vararg args: Any) {
        addStatement(format.format(*args))
    }

    fun addEmptyLine() {
        mTokens.add(Statement(""))
    }

    fun newCodeBlock() = newCodeBlock(mPrefix)

    fun newCodeBlockWithIndentation() = newCodeBlock(mPrefix + "\t")

    private fun newCodeBlock(prefix: String): CodeBlock {
        val codeBlock = CodeBlock(prefix)
        mTokens.add(codeBlock)
        return codeBlock
    }

    override val lines: Stream<String>
        get() = mTokens.stream().flatMap(Token::lines)
}
