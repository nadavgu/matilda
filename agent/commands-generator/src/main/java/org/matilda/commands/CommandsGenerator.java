package org.matilda.commands;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;

public class CommandsGenerator {
    @Inject
    RoundEnvironment mRoundEnvironment;

    @Inject
    CommandsGenerator() {}

    public void generate() {
    }
}
