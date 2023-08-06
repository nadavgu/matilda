package org.matilda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MatildaConnection {
    public final InputStream inputStream;
    public final OutputStream outputStream;

    public MatildaConnection(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }

        try {
            inputStream.close();
        } catch (IOException ignored) {
        }
    }
}
