package org.matilda.messages;

import javax.inject.Inject;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryMessageSender implements MessageSender {
    private final DataOutputStream mOutputStream;

    @Inject
    MessageSerializer mSerializer;

    @Inject
    public BinaryMessageSender(OutputStream outputStream) {
        mOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public void send(Message message) throws IOException {
        byte[] serializedMessage = mSerializer.serialize(message);
        sendLength(serializedMessage.length);
        sendData(serializedMessage);
    }

    private void sendLength(int length) throws IOException {
        byte[] array = ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(length)
                .array();
        mOutputStream.write(array);
    }

    private void sendData(byte[] bytes) throws IOException {
        mOutputStream.write(bytes);
    }
}
