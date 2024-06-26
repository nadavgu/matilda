from typing import TypeVar, Type

from google.protobuf.any_pb2 import Any

from matilda.commands.command_registry_factory import CommandRegistryFactory
from matilda.commands.command_repository import CommandRepository
from matilda.commands.command_runner import CommandRunner
from matilda.commands.protobuf.protobuf_converter import ProtobufConverter
from matilda.commands.protobuf.scalar_converter import ScalarConverter
from google.protobuf.wrappers_pb2 import Int32Value

from matilda.commands.service_proxy_factory import ServiceProxyFactory

T = TypeVar('T')


class DynamicServiceConverter(ProtobufConverter[T]):
    def __init__(self, command_repository: CommandRepository, command_registry_factory: CommandRegistryFactory[T],
                 service_proxy_factory: ServiceProxyFactory[T]):
        self.__command_repository = command_repository
        self.__command_registry_factory = command_registry_factory
        self.__service_proxy_factory = service_proxy_factory
        self.__scalar_converter = ScalarConverter(Int32Value)

    def to_protobuf(self, value: T) -> Int32Value:
        registry_id = self.__command_repository.add_command_registry(
            self.__command_registry_factory.create_command_registry(value))
        return self.__scalar_converter.to_protobuf(registry_id)

    def from_protobuf(self, message: Any) -> T:
        registry_id = self.__scalar_converter.from_protobuf(message)
        return self.__service_proxy_factory.create_service_proxy(registry_id)
