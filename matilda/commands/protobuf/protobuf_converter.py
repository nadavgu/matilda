from abc import ABC
from typing import TypeVar, Generic

from google.protobuf.internal.well_known_types import Any
from google.protobuf.message import Message

T = TypeVar('T')


class ProtobufConverter(ABC, Generic[T]):
    def to_protobuf(self, value: T) -> Message:
        pass

    def from_protobuf(self, message: Any) -> T:
        pass
