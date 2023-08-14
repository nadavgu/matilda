package org.matilda.services.reflection;

import javax.inject.Inject;
import java.util.Random;

public class ObjectIdGenerator {
    private final Random mRandom;

    @Inject
    ObjectIdGenerator() {
        mRandom = new Random();
    }


    public long generate(Object ignoredObject) {
        return mRandom.nextLong();
    }
}
