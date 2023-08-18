from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_class import JavaClass


class JavaModule(Dependency):
    def __init__(self, reflection_service: ReflectionService):
        self.__reflection_service = reflection_service

    def find_class(self, name: str):
        return JavaClass(self.__reflection_service, self.__reflection_service.find_class(name))

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'JavaModule':
        return JavaModule(dependency_container.get(ReflectionService))

