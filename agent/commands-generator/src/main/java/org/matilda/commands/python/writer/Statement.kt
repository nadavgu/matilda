package org.matilda.commands.python.writer

import java.util.stream.Stream

data class Statement(val statement: String) : Token {
    override val lines: Stream<String>
        get() = Stream.of(statement)
}
