from typing import Union

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_class import JavaClass
from matilda.java.java_primitive_type import JavaPrimitiveType

JavaType = Union[JavaPrimitiveType, JavaClass]


def get_type_name(java_type: JavaType) -> str:
    if isinstance(java_type, JavaPrimitiveType):
        return java_type.value
    else:
        return java_type.name


def get_type_from_protobuf(reflection_service: ReflectionService, protobuf) -> JavaType:
    if protobuf.primitive_class_name:
        return JavaPrimitiveType(protobuf.primitive_class_name)
    else:
        return JavaClass(reflection_service, protobuf.class_id)
