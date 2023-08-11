package org.matilda.commands.python.writer;

import java.util.stream.Stream;

interface Token {
    Stream<String> getLines();
}
