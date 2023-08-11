from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.generated.commands.math_service import MathService
from google.protobuf.wrappers_pb2 import Int32Value


def build_int(value: int) -> Int32Value:
    protobuf_int = Int32Value()
    protobuf_int.value = value
    return protobuf_int


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
        print(dependency_container.get(MathService).square(build_int(3)).value)
        print(dependency_container.get(MathService).square(build_int(4)).value)

        dependency_container.get(IO, DependencyTags.AGENT_INPUT).close()
        destruction_manager = dependency_container.get(DestructionManager)
        destruction_manager.add_destructor(lambda: print("Destruct"))
        return MatildaProcess(destruction_manager)

