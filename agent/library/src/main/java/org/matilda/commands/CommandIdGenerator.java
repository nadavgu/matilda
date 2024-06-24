package org.matilda.commands;

import javax.inject.Inject;
import java.util.Random;

public class CommandIdGenerator {
    @Inject
    Random mRandom;

    @Inject
    CommandIdGenerator() {
    }


    public int generate() {
        return mRandom.nextInt();
    }
}
