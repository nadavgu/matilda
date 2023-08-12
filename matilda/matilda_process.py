from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.generated.commands.math_service import MathService, Int32Value, Some
from google.protobuf.any_pb2 import Any


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
        print(dependency_container.get(MathService).square(3))
        print(dependency_container.get(MathService).square(4))
        print(dependency_container.get(MathService).sum(3, 4))

        destruction_manager = dependency_container.get(DestructionManager)
        return MatildaProcess(destruction_manager)

