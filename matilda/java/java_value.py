from typing import Union, Optional

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_object import JavaObject
from matilda.generated.proto import reflection_pb2

JavaPrimitive = Union[int, float, bool]
JavaValue = Union[JavaObject, JavaPrimitive]
OptionalJavaValue = Optional[JavaValue]


def get_value_from_protobuf(reflection_service: ReflectionService, protobuf) -> OptionalJavaValue:
    if protobuf.object_id:
        return JavaObject(reflection_service, protobuf.object_id)
    attribute_name = protobuf.WhichOneof("value")
    if attribute_name:
        return getattr(protobuf, protobuf.WhichOneof("value"))


def convert_value_to_protobuf(value: OptionalJavaValue):
    protobuf = reflection_pb2.JavaValue()
    if value:
        if type(value) == int:
            protobuf.int = value
        elif type(value) == float:
            protobuf.float = value
        elif type(value) == bool:
            protobuf.bool = value
        else:
            protobuf.object_id = value.object_id
    return protobuf
