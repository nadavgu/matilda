package org.matilda.messages;

import javax.inject.Inject;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryMessageReceiver implements MessageReceiver {
    private final DataInputStream mInputStream;

    @Inject
    MessageSerializer mSerializer;

    @Inject
    public BinaryMessageReceiver(InputStream inputStream) {
        mInputStream = new DataInputStream(inputStream);
    }

    @Override
    public Message receive() throws IOException {
        int length = readLength();
        byte[] data = readData(length);
        return mSerializer.deserialize(data);
    }

    private byte[] readData(int length) throws IOException {
        byte[] bytes = new byte[length];
        mInputStream.readFully(bytes);
        return bytes;
    }

    private int readLength() throws IOException {
        byte[] lengthBytes = new byte[Integer.BYTES];
        mInputStream.readFully(lengthBytes);
        return ByteBuffer.wrap(lengthBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }
}
