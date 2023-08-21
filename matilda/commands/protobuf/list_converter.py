from typing import TypeVar, List

from google.protobuf.any_pb2 import Any

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter
from matilda.generated.proto.some_pb2 import Some

T = TypeVar('T')


class ListConverter(ProtobufConverter[List[T]]):
    def __init__(self, inner_converter: ProtobufConverter[T]):
        self.__inner_converter = inner_converter

    def to_protobuf(self, values: List[T]) -> Some:
        return Some(any=map(self.__to_any, values))

    def __to_any(self, value: T) -> Any:
        any_message = Any()
        any_message.Pack(msg=self.__inner_converter.to_protobuf(value))
        return any_message

    def from_protobuf(self, message: Any) -> List[T]:
        some = Some()
        message.Unpack(some)
        return list(map(self.__inner_converter.from_protobuf, some.any))

