from typing import TypeVar

from google.protobuf.message import Message

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter

T = TypeVar('T', bound=Message)


class MessageConverter(ProtobufConverter[T, T]):
    def to_protobuf(self, value: T) -> T:
        return value

    def from_protobuf(self, message: T) -> T:
        return message
