from dataclasses import dataclass
from queue import Queue
from typing import Optional, Callable

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.exceptions.no_more_messages_exception import NoMoreMessagesException
from matilda.messages.handler.message_handler import MessageHandler
from matilda.messages.handler.message_handler_registration import MessageHandlerRegistration
from matilda.messages.handler.message_handler_registry import MessageHandlerRegistry
from matilda.messages.message import Message


@dataclass
class MessageHolder:
    message: Optional[Message]


class MessageListeningInstance:
    def __init__(self, message_queue: Queue, message_handler_registration: MessageHandlerRegistration):
        self.__queue = message_queue
        self.__registration = message_handler_registration

    def wait_for_message(self) -> Message:
        message = self.__queue.get()
        if message is None:
            raise NoMoreMessagesException()
        return message

    def stop(self):
        self.__registration.unregister()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.stop()


class MessageListeningHandler(MessageHandler):
    def __init__(self, predicate: Optional[Callable[[Message], bool]], queue: Queue):
        self.__predicate = predicate
        self.__queue = queue

    def handle_message(self, message: Message):
        if not self.__predicate or self.__predicate(message):
            self.__queue.put(message)

    def handle_no_more_messages(self):
        self.__queue.put(None)


class MessageListener(Dependency):
    def __init__(self, message_handler_registry: MessageHandlerRegistry):
        self.__registry = message_handler_registry

    def listen(self, message_type: int, predicate: Optional[Callable[[Message], bool]] = None) -> MessageListeningInstance:
        queue = Queue()
        registration = self.__registry.register(message_type, MessageListeningHandler(predicate, queue))
        return MessageListeningInstance(queue, registration)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageListener':
        return MessageListener(dependency_container.get(MessageHandlerRegistry))
