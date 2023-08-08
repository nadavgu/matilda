package org.matilda;

import org.matilda.di.DaggerMatildaComponent;
import org.matilda.di.MatildaComponent;
import org.matilda.di.MatildaConnectionModule;
import org.matilda.messages.Message;

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
            Message message = mMatildaComponent.messageReceiver().receive();
            System.out.printf("%d %s", message.type, new String(message.data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
