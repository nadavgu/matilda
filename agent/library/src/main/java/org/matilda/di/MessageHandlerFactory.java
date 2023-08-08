package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.messages.handlers.MessageHandler;

@Module
public class MessageHandlerFactory {
    @Provides
    MessageHandler create() {
        return message -> System.out.printf("%d %s\n", message.type, new String(message.data));
    }
}
