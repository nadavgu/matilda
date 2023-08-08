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
        mMatildaComponent.destructionManager().addDestructor(matildaConnection::close);
    }

    public void run() {
        try {
            mMatildaComponent.messageListener().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            mMatildaComponent.destructionManager().destruct();
        }
    }
}
