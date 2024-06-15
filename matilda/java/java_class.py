from functools import cached_property
from typing import TYPE_CHECKING, List

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_object import JavaObject

if TYPE_CHECKING:
    from matilda.java.java_method import JavaMethod
    from matilda.java.java_field import JavaField
    from matilda.java.java_type import JavaType


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

    def get_method(self, name: str, *parameters: 'JavaType') -> 'JavaMethod':
        from matilda.java.java_method import JavaMethod
        from matilda.java.java_type import convert_type_to_protobuf
        return JavaMethod(
            self.__reflection_service.get_method(self.object_id, name,
                                                 [convert_type_to_protobuf(param) for param in parameters]),
            self.__reflection_service)

    def get_fields(self) -> List['JavaField']:
        from matilda.java.java_field import JavaField
        return [JavaField(field_id, self.__reflection_service) for field_id in
                self.__reflection_service.get_class_fields(self.object_id)]

    def get_field(self, name: str) -> 'JavaField':
        from matilda.java.java_field import JavaField
        return JavaField(self.__reflection_service.get_field(self.object_id, name), self.__reflection_service)

    def __str__(self):
        return self.name

    def __repr__(self):
        return f"JavaClass({self.name})"

