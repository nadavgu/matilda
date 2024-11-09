package org.matilda.di

import dagger.Binds
import dagger.Module
import org.matilda.messages.*

@Module(includes = [MessageHandlerModule::class])
interface MessagesModule {
    @Binds
    fun bindMessageSender(messageReceiver: BinaryMessageSender): MessageSender

    @Binds
    fun bindMessageReceiver(messageReceiver: BinaryMessageReceiver): MessageReceiver

    @Binds
    fun bindMessageSerializer(messageSerializer: ProtobufMessageSerializer): MessageSerializer
}
