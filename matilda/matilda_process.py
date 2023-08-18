from functools import cached_property

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.generated.services import Services
from matilda.java.java_module import JavaModule


class MatildaProcess(Dependency):
    def __init__(self, dependency_container: DependencyContainer):
        self.__dependency_container = dependency_container

    def close(self):
        self.__dependency_container.get(DestructionManager).destruct()

    @cached_property
    def services(self) -> Services:
        return self.__dependency_container.get(Services)

    @cached_property
    def java(self) -> JavaModule:
        return self.__dependency_container.get(JavaModule)

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        return MatildaProcess(dependency_container)

