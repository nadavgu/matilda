package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.messages.MessageHandlerFactory;
import org.matilda.messages.handlers.MessageHandler;

@Module(includes = {ExecutorServiceModule.class})
public class MessageHandlerModule {
    @Provides
    MessageHandler messageHandler(MessageHandlerFactory messageHandlerFactory) {
        return messageHandlerFactory.create();
    }
}
