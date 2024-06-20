from typing import Union

from matilda.generated.commands.reflection_service import ReflectionService
from matilda.java.java_object import JavaObject
from matilda.generated.proto import reflection_pb2

JavaPrimitive = Union[int, float, bool]
JavaValue = Union[JavaObject, JavaPrimitive]


def get_value_from_protobuf(reflection_service: ReflectionService, protobuf):
    if protobuf.object_id:
        return JavaObject(reflection_service, protobuf.object_id)
    return getattr(protobuf, protobuf.WhichOneof("value"))


def convert_value_to_protobuf(value: JavaValue):
    protobuf = reflection_pb2.JavaValue()
    if type(value) == int:
        protobuf.int = value
    elif type(value) == float:
        protobuf.float = value
    elif type(value) == bool:
        protobuf.bool = value
    else:
        protobuf.object_id = value.object_id
    return protobuf
