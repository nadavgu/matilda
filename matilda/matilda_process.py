from threading import Thread
from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.messages.message import Message
from matilda.messages.message_listener import MessageListener
from matilda.messages.message_sender import MessageSender


class MatildaProcess(Dependency):
    def __init__(self, destruction_manager: DestructionManager):
        self.__destruction_manager = destruction_manager

    def close(self):
        self.__destruction_manager.destruct()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        dependency_container.get(MessageSender).send(Message(11, b"jello"))
        dependency_container.get(IO, DependencyTags.AGENT_INPUT).close()
        destruction_manager = dependency_container.get(DestructionManager)
        destruction_manager.add_destructor(lambda: print("Destruct"))
        MatildaProcess.__start_message_listener(dependency_container.get(MessageListener), destruction_manager)
        return MatildaProcess(destruction_manager)

    @staticmethod
    def __start_message_listener(message_listener: MessageListener, destruction_manager: DestructionManager):
        thread = Thread(target=message_listener.start)
        thread.start()
        destruction_manager.add_destructor(thread.join)
