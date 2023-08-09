from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer


class CommandIdGenerator(Dependency):
    def __init__(self):
        self.__last_id = 0

    def generate(self) -> int:
        self.__last_id += 1
        return self.__last_id

    @staticmethod
    def create(dependency_container: DependencyContainer) -> T:
        return CommandIdGenerator()
