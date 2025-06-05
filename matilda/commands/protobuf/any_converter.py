from typing import TypeVar, Type

from google.protobuf.any_pb2 import Any
from google.protobuf.message import Message

from matilda.commands.protobuf.message_converter import MessageConverter
from matilda.commands.protobuf.protobuf_converter import ProtobufConverter

T = TypeVar('T', bound=Message)


class AnyConverter(ProtobufConverter[T]):
    def __init__(self, message_type: Type[T]):
        self.__message_converter = MessageConverter(message_type)

    def to_protobuf(self, value: T) -> Any:
        any = Any()
        any.Pack(self.__message_converter.to_protobuf(value))
        return any

    def from_protobuf(self, any_message: Any) -> T:
        inner_any = Any()
        any_message.Unpack(inner_any)
        return self.__message_converter.from_protobuf(inner_any)
