from concurrent.futures import ThreadPoolExecutor

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.messages.handler.message_handler import MessageHandler
from matilda.messages.handler.message_handler_registry import MessageHandlerRegistry
from matilda.messages.message import Message


class MessageDispatcher(MessageHandler, Dependency):
    def __init__(self, executor: ThreadPoolExecutor, message_handler: MessageHandler):
        self.__executor = executor
        self.__message_handler = message_handler

    def handle_message(self, message: Message):
        self.__executor.submit(self.__message_handler.handle_message, message)

    def handle_no_more_messages(self):
        self.__message_handler.handle_no_more_messages()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageDispatcher':
        return MessageDispatcher(dependency_container.get(ThreadPoolExecutor),
                                 dependency_container.get(MessageHandlerRegistry))
