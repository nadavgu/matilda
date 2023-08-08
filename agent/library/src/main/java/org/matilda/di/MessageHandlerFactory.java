package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.messages.handlers.MessageDispatcher;
import org.matilda.messages.handlers.MessageHandler;

import java.util.concurrent.ExecutorService;

@Module(includes = ExecutorServiceModule.class)
public class MessageHandlerFactory {
    @Provides
    MessageHandler create(ExecutorService executorService) {
        return new MessageDispatcher(executorService,
                message -> System.out.printf("%d %s %d\n", message.type, new String(message.data), Thread.currentThread().getId()));
    }
}
