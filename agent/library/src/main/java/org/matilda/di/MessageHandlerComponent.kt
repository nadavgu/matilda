package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.messages.MessageHandlerFactory
import org.matilda.messages.handlers.MessageHandler

@Component
interface MessageHandlerComponent : ExecutorServiceComponent {
    @Provides
    fun messageHandler(messageHandlerFactory: MessageHandlerFactory): MessageHandler {
        return messageHandlerFactory.create()
    }
}
