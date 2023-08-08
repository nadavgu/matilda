package org.matilda.messages.handlers;

import org.matilda.messages.Message;

import java.util.concurrent.ExecutorService;

public class MessageDispatcher implements MessageHandler {
    private final ExecutorService mExecutorService;
    private final MessageHandler mMessageHandler;

    public MessageDispatcher(ExecutorService executorService, MessageHandler messageHandler) {
        mExecutorService = executorService;
        mMessageHandler = messageHandler;
    }

    @Override
    public void handle(Message message) {
        mExecutorService.submit(() -> mMessageHandler.handle(message));
    }
}
