from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer


class MatildaProcess(Dependency):
    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        return MatildaProcess()
