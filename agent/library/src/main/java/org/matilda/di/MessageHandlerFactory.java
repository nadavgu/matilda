package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.messages.MessageHandlerRegistry;
import org.matilda.messages.handlers.MessageDispatcher;
import org.matilda.messages.handlers.MessageHandler;

import java.util.concurrent.ExecutorService;

@Module(includes = ExecutorServiceModule.class)
public class MessageHandlerFactory {
    @Provides
    MessageHandler create(ExecutorService executorService, MessageHandlerRegistry messageHandlerRegistry) {
        messageHandlerRegistry.registerHandler(10,
                message -> System.out.printf("Yoohoo 10! %d %s %d\n", message.type, new String(message.data), Thread.currentThread().getId()));
        messageHandlerRegistry.registerHandler(11,
                message -> System.out.printf("Yoohoo 11! %d %s %d\n", message.type, new String(message.data), Thread.currentThread().getId()));
        return new MessageDispatcher(executorService, messageHandlerRegistry);
    }
}
