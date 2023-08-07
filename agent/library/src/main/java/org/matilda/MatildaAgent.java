package org.matilda;

import org.matilda.di.DaggerMatildaComponent;
import org.matilda.di.MatildaComponent;
import org.matilda.di.MatildaConnectionModule;

import java.io.IOException;

public class MatildaAgent {
    private final MatildaComponent mMatildaComponent;

    public MatildaAgent(MatildaConnection matildaConnection) {
        mMatildaComponent = DaggerMatildaComponent.builder()
                .matildaConnectionModule(new MatildaConnectionModule(matildaConnection))
                .build();
    }

    public void run() {
        try {
            mMatildaComponent.matildaConnection().outputStream.write("hello".getBytes());
            mMatildaComponent.matildaConnection().outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
