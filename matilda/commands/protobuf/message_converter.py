from typing import TypeVar, Type

from google.protobuf.internal.well_known_types import Any
from google.protobuf.message import Message

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter

T = TypeVar('T', bound=Message)


class MessageConverter(ProtobufConverter[T]):
    def __init__(self, message_type: Type[T]):
        self.__type = message_type

    def to_protobuf(self, value: T) -> T:
        return value

    def from_protobuf(self, any_message: Any) -> T:
        message = self.__type()
        any_message.Unpack(message)
        return message
