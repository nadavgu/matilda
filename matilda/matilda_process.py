from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_response_listener import CommandResponseListener
from matilda.commands.command_runner import CommandRunner
from matilda.commands.command_sender import CommandSender
from matilda.di.dependency_tags import DependencyTags
from matilda.di.destructors.destruction_manager import DestructionManager


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
        print(dependency_container.get(CommandRunner).run(CommandType.ECHO, b"1234"))
        print(dependency_container.get(CommandRunner).run(CommandType.ECHO, b"1235"))

        dependency_container.get(IO, DependencyTags.AGENT_INPUT).close()
        destruction_manager = dependency_container.get(DestructionManager)
        destruction_manager.add_destructor(lambda: print("Destruct"))
        return MatildaProcess(destruction_manager)

