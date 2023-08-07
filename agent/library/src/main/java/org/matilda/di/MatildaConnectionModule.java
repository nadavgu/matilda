package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.MatildaConnection;

import java.io.InputStream;
import java.io.OutputStream;

@Module
public class MatildaConnectionModule {
    private final MatildaConnection mMatildaConnection;

    public MatildaConnectionModule(MatildaConnection matildaConnection) {
        mMatildaConnection = matildaConnection;
    }

    @Provides
    MatildaConnection connection() {
        return mMatildaConnection;
    }

    @Provides
    InputStream inputStream() {
        return mMatildaConnection.inputStream;
    }

    @Provides
    OutputStream outputStream() {
        return mMatildaConnection.outputStream;
    }
}
