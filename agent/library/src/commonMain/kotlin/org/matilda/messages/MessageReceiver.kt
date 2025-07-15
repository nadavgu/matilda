package org.matilda.messages

interface MessageReceiver {
    fun receive(): Message
}
