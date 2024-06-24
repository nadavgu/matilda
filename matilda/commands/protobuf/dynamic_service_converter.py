from typing import TypeVar, Type

from google.protobuf.any_pb2 import Any

from matilda.commands.command_runner import CommandRunner
from matilda.commands.protobuf.protobuf_converter import ProtobufConverter
from matilda.commands.protobuf.scalar_converter import ScalarConverter
from google.protobuf.wrappers_pb2 import Int32Value

T = TypeVar('T')


class DynamicServiceConverter(ProtobufConverter[T]):
    def __init__(self, dynamic_service_type: Type[T], command_runner: CommandRunner):
        self.__dynamic_service_type = dynamic_service_type
        self.__scalar_converter = ScalarConverter(Int32Value)
        self.__command_runner = command_runner

    def to_protobuf(self, value: T) -> Int32Value:
        raise RuntimeError()

    def from_protobuf(self, message: Any) -> T:
        registry_id = self.__scalar_converter.from_protobuf(message)
        return self.__dynamic_service_type(self.__command_runner, command_registry_id=registry_id)
