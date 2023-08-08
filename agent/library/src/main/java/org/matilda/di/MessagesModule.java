package org.matilda.di;

import dagger.Binds;
import dagger.Module;
import org.matilda.messages.BinaryMessageReceiver;
import org.matilda.messages.MessageReceiver;
import org.matilda.messages.MessageSerializer;
import org.matilda.messages.ProtobufMessageSerializer;

@Module(includes = MessageHandlerFactory.class)
public interface MessagesModule {
    @Binds
    MessageReceiver bindMessageReceiver(BinaryMessageReceiver messageReceiver);

    @Binds
    MessageSerializer bindMessageSerializer(ProtobufMessageSerializer messageSerializer);
}
