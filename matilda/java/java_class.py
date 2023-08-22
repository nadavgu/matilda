from functools import cached_property
from typing import TYPE_CHECKING, List

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_object import JavaObject

if TYPE_CHECKING:
    from matilda.java.java_method import JavaMethod


class JavaClass(JavaObject):
    def __init__(self, reflection_service: ReflectionService, object_id: int):
        super().__init__(object_id)
        self.__reflection_service = reflection_service

    @cached_property
    def name(self) -> str:
        return self.__reflection_service.get_class_name(self.object_id)

    def get_methods(self) -> List['JavaMethod']:
        from matilda.java.java_method import JavaMethod
        return [JavaMethod(method_id, self.__reflection_service) for method_id in
                self.__reflection_service.get_class_methods(self.object_id)]

    def __str__(self):
        return self.name

    def __repr__(self):
        return f"JavaClass({self.name})"

