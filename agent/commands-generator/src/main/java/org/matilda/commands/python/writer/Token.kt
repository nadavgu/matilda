package org.matilda.commands.python.writer

import java.util.stream.Stream

internal interface Token {
    val lines: Stream<String>
}
