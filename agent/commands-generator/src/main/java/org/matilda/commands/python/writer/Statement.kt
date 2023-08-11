package org.matilda.commands.python.writer;

import java.util.stream.Stream;

public record Statement(String statement) implements Token {
    @Override
    public Stream<String> getLines() {
        return Stream.of(statement);
    }
}
