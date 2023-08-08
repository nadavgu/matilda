from typing import Callable, List

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer


class DestructionManager(Dependency):
    def __init__(self):
        self.__destructors: List[Callable[[], None]] = []

    def add_destructor(self, destructor: Callable[[], None]):
        self.__destructors.append(destructor)

    def destruct(self):
        while len(self.__destructors) != 0:
            self.__destructors.pop()()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'DestructionManager':
        return DestructionManager()

