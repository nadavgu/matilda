from functools import cached_property
from typing import TYPE_CHECKING, List

from matilda.generated.commands.reflection_service import ReflectionService

if TYPE_CHECKING:
    from matilda.java.java_method import JavaMethod


class JavaClass:
    def __init__(self, reflection_service: ReflectionService, object_id: int):
        self.__reflection_service = reflection_service
        self.__object_id = object_id

    @cached_property
    def name(self) -> str:
        return self.__reflection_service.get_class_name(self.__object_id)

    def get_methods(self) -> List['JavaMethod']:
        from matilda.java.java_method import JavaMethod
        return [JavaMethod.from_protobuf(self.__reflection_service, protobuf) for protobuf in
                self.__reflection_service.get_class_methods(self.__object_id)]

    def __str__(self):
        return self.name

    def __repr__(self):
        return f"JavaClass({self.name})"

