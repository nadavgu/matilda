package org.matilda.di;

import dagger.Binds;
import dagger.Module;
import org.matilda.messages.*;

@Module(includes = {MessageHandlerModule.class})
public interface MessagesModule {
    @Binds
    MessageSender bindMessageSender(BinaryMessageSender messageReceiver);

    @Binds
    MessageReceiver bindMessageReceiver(BinaryMessageReceiver messageReceiver);

    @Binds
    MessageSerializer bindMessageSerializer(ProtobufMessageSerializer messageSerializer);
}
