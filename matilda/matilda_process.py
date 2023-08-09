from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_sender import CommandSender
from matilda.di.dependency_tags import DependencyTags
from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.messages.handler.message_handler_registry import MessageHandlerRegistry
from matilda.messages.message_waiter import MessageWaiter


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
        from matilda.generated.proto.command_pb2 import CommandType
        from matilda.generated.proto.message_pb2 import MessageType
        dependency_container.get(CommandSender).send(CommandType.ECHO, 12, b"1234")
        dependency_container.get(CommandSender).send(CommandType.ECHO, 14, b"1235")
        print(dependency_container.get(MessageWaiter).wait_for_message(MessageType.COMMAND_RESPONSE))
        print(dependency_container.get(MessageWaiter).wait_for_message(MessageType.COMMAND_RESPONSE))
        dependency_container.get(IO, DependencyTags.AGENT_INPUT).close()
        destruction_manager = dependency_container.get(DestructionManager)
        destruction_manager.add_destructor(lambda: print("Destruct"))
        return MatildaProcess(destruction_manager)

