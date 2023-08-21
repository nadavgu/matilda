from typing import TypeVar, Type

from google.protobuf.message import Message

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter

T = TypeVar('T')
P = TypeVar('P', bound=Message)


class ScalarConverter(ProtobufConverter[T, P]):
    def __init__(self, wrapper_type: Type[P]):
        self.__wrapper_type = wrapper_type

    def to_protobuf(self, value: T) -> P:
        return self.__wrapper_type(value=value)

    def from_protobuf(self, message: P) -> T:
        return message.value
