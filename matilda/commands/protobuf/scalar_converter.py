from typing import TypeVar, Type

from google.protobuf.any_pb2 import Any
from google.protobuf.message import Message

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter

T = TypeVar('T')
P = TypeVar('P', bound=Message)


class ScalarConverter(ProtobufConverter[T]):
    def __init__(self, wrapper_type: Type[P]):
        self.__wrapper_type = wrapper_type

    def to_protobuf(self, value: T) -> P:
        return self.__wrapper_type(value=value)

    def from_protobuf(self, message: Any) -> T:
        wrapper = self.__wrapper_type()
        message.Unpack(wrapper)
        return wrapper.value
