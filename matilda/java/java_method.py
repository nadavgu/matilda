from dataclasses import dataclass
from typing import List

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_type import JavaType


@dataclass
class MethodSpec:
    name: str
    parameter_types: List[JavaType]

    def __str__(self):
        from matilda.java.java_type import get_type_name
        return f"{self.name}({', '.join(map(get_type_name, self.parameter_types))})"

    def __repr__(self):
        return f"MethodSpec({str(self)})"

    @staticmethod
    def from_protobuf(reflection_service: ReflectionService, protobuf) -> 'MethodSpec':
        from matilda.java.java_type import get_type_from_protobuf
        return MethodSpec(protobuf.name, [get_type_from_protobuf(reflection_service, protobuf) for protobuf
                                          in protobuf.parameter_types])


class JavaMethod:
    def __init__(self, method_id: int, method_spec: MethodSpec):
        self.__method_id = method_id
        self.__method_spec = method_spec

    def __str__(self):
        return str(self.__method_spec)

    def __repr__(self):
        return f"JavaMethod({str(self)})"

    @staticmethod
    def from_protobuf(reflection_service: ReflectionService, protobuf) -> 'JavaMethod':
        return JavaMethod(protobuf.method_id, MethodSpec.from_protobuf(reflection_service, protobuf.spec))
