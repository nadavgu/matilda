from functools import cached_property
from typing import List

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_object import JavaObject
from matilda.java.java_type import JavaType
from matilda.java.java_value import JavaValue, convert_value_to_protobuf, get_value_from_protobuf


class JavaMethod(JavaObject):
    def __init__(self, method_id: int, reflection_service: ReflectionService):
        super().__init__(method_id)
        self.__reflection_service = reflection_service

    def __str__(self):
        from matilda.java.java_type import get_type_name
        return f"{'static ' if self.is_static else ''}{self.name}({', '.join(map(get_type_name, self.parameter_types))})"

    def __repr__(self):
        return f"JavaMethod({str(self)})"

    @cached_property
    def name(self) -> str:
        return self.__reflection_service.get_method_name(self.object_id)

    @cached_property
    def parameter_types(self) -> List[JavaType]:
        from matilda.java.java_type import get_type_from_protobuf
        return [get_type_from_protobuf(self.__reflection_service, protobuf) for protobuf
                in self.__reflection_service.get_method_parameter_types(self.object_id)]

    @cached_property
    def is_static(self) -> bool:
        return self.__reflection_service.is_method_static(self.object_id)

    def invoke(self, receiver: JavaObject, *args: JavaValue) -> JavaValue:
        result = self.__reflection_service.invoke_method(self.object_id, receiver.object_id,
                                                         [convert_value_to_protobuf(arg) for arg in args])
        return get_value_from_protobuf(result)

    def invoke_static(self, *args: JavaValue) -> JavaValue:
        result = self.__reflection_service.invoke_static_method(self.object_id,
                                                                [convert_value_to_protobuf(arg) for arg in args])
        return get_value_from_protobuf(result)

