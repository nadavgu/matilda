package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.messages.*

@Component
interface MessagesComponent : MessageHandlerComponent {
    @Provides
    fun bindMessageSender(messageSender: BinaryMessageSender): MessageSender = messageSender

    @Provides
    fun bindMessageReceiver(messageReceiver: BinaryMessageReceiver): MessageReceiver = messageReceiver

    @Provides
    fun bindMessageSerializer(messageSerializer: ProtobufMessageSerializer): MessageSerializer = messageSerializer
}
