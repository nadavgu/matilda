from typing import List

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_class import JavaClass
from matilda.java.java_object import JavaObject
from matilda.java.java_value import get_value_from_protobuf
from matilda.java.proxy_handler import ProxyHandler
from matilda.java.proxy_handler_service_impl import ProxyHandlerServiceImpl


class JavaModule(Dependency):
    def __init__(self, reflection_service: ReflectionService):
        self.__reflection_service = reflection_service

    def find_class(self, name: str):
        return JavaClass(self.__reflection_service, self.__reflection_service.find_class(name))

    def new_proxy_instance(self, interfaces: List[JavaClass], proxy_handler: ProxyHandler) -> JavaObject:
        interface_ids = [interface.object_id for interface in interfaces]
        proxy_handler_service = ProxyHandlerServiceImpl(self.__reflection_service, proxy_handler)
        return JavaObject(self.__reflection_service,
                          self.__reflection_service.new_proxy_instance(interface_ids, proxy_handler_service))

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'JavaModule':
        return JavaModule(dependency_container.get(ReflectionService))

