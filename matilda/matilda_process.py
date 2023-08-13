from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.generated.commands.math_service import MathService
from matilda.generated.services import Services


class MatildaProcess(Dependency):
    def __init__(self, services: Services, destruction_manager: DestructionManager):
        self.__services = services
        self.__destruction_manager = destruction_manager

    def close(self):
        self.__destruction_manager.destruct()

    @property
    def services(self) -> Services:
        return self.__services

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        return MatildaProcess(dependency_container.get(Services), dependency_container.get(DestructionManager))

