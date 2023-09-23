from google.protobuf.any_pb2 import Any
from google.protobuf.empty_pb2 import Empty

from matilda.commands.protobuf.protobuf_converter import ProtobufConverter


class EmptyConverter(ProtobufConverter[None]):
    def to_protobuf(self, value: None) -> Empty:
        return Empty()

    def from_protobuf(self, message: Any) -> None:
        return None
