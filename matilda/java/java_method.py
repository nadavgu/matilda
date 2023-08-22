from functools import cached_property
from typing import List

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_type import JavaType


class JavaMethod:
    def __init__(self, method_id: int, reflection_service: ReflectionService):
        self.__method_id = method_id
        self.__reflection_service = reflection_service

    def __str__(self):
        from matilda.java.java_type import get_type_name
        return f"{self.name}({', '.join(map(get_type_name, self.parameter_types))})"

    def __repr__(self):
        return f"JavaMethod({str(self)})"

    @cached_property
    def name(self) -> str:
        return self.__reflection_service.get_method_name(self.__method_id)

    @cached_property
    def parameter_types(self) -> List[JavaType]:
        from matilda.java.java_type import get_type_from_protobuf
        return [get_type_from_protobuf(self.__reflection_service, protobuf) for protobuf
                in self.__reflection_service.get_method_parameter_types(self.__method_id)]
