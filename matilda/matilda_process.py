from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_response_listener import CommandResponseListener
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
        with dependency_container.get(CommandResponseListener).listen(12) as listener12:
            with dependency_container.get(CommandResponseListener).listen(14) as listener14:
                dependency_container.get(CommandSender).send(CommandType.ECHO, 14, b"1235")
                dependency_container.get(CommandSender).send(CommandType.ECHO, 12, b"1234")
                print(listener14.wait_for_response())
                print(listener12.wait_for_response())

        dependency_container.get(IO, DependencyTags.AGENT_INPUT).close()
        destruction_manager = dependency_container.get(DestructionManager)
        destruction_manager.add_destructor(lambda: print("Destruct"))
        return MatildaProcess(destruction_manager)

