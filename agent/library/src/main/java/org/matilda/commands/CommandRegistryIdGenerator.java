package org.matilda.commands;

import javax.inject.Inject;
import java.util.Random;

public class CommandRegistryIdGenerator {
    @Inject
    Random mRandom;

    @Inject
    CommandRegistryIdGenerator() {
    }


    public int generate() {
        return mRandom.nextInt();
    }
}
