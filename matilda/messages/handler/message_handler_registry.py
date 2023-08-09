from typing import Dict, List

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.messages.handler.message_handler import MessageHandler
from matilda.messages.handler.message_handler_registration import MessageHandlerRegistration
from matilda.messages.message import Message


class MessageHandlerRegistry(Dependency):
    def __init__(self):
        self.__handlers: Dict[int, List[MessageHandler]] = {}

    def register(self, message_type: int, message_handler: MessageHandler) -> MessageHandlerRegistration:
        if message_type not in self.__handlers:
            self.__handlers[message_type] = []
        self.__handlers[message_type].append(message_handler)
        return MessageHandlerRegistration(self, message_type, message_handler)

    def unregister(self, message_type: int, message_handler: MessageHandler):
        if message_type in self.__handlers and message_handler in self.__handlers[message_type]:
            self.__handlers[message_type].remove(message_handler)

    def handle_message(self, message: Message):
        if message.message_type in self.__handlers:
            for handler in self.__handlers[message.message_type]:
                handler(message)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageHandlerRegistry':
        return MessageHandlerRegistry()

