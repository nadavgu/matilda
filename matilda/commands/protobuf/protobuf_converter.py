from abc import ABC
from typing import TypeVar, Generic
from google.protobuf.message import Message

T = TypeVar('T')
P = TypeVar('P', bound=Message)


class ProtobufConverter(ABC, Generic[T, P]):
    def to_protobuf(self, value: T) -> P:
        pass

    def from_protobuf(self, message: P) -> T:
        pass
