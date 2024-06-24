package org.matilda.di;

import dagger.Module;
import dagger.Provides;

import java.util.Random;

@Module
public class UtilsModule {
    @Provides
    Random random() {
        return new Random(System.currentTimeMillis());
    }
}
