from dataclasses import dataclass
from queue import Queue
from typing import Optional, Callable

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

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
        return self.__queue.get()

    def stop(self):
        self.__registration.unregister()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.stop()


class MessageListener(Dependency):
    def __init__(self, message_handler_registry: MessageHandlerRegistry):
        self.__registry = message_handler_registry

    def listen(self, message_type: int, predicate: Optional[Callable[[Message], bool]] = None) -> MessageListeningInstance:
        queue = Queue()

        def check_message(message: Message):
            if not predicate or predicate(message):
                queue.put(message)

        registration = self.__registry.register(message_type, check_message)
        return MessageListeningInstance(queue, registration)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageListener':
        return MessageListener(dependency_container.get(MessageHandlerRegistry))
