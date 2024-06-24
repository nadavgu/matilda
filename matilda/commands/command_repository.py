from typing import Dict

from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer

from matilda.commands.command import Command
from matilda.commands.command_registry import CommandRegistry
from matilda.commands.command_registry_id_generator import CommandRegistryIdGenerator


class CommandRepository(Dependency):
    def __init__(self, command_registry_id_generator: CommandRegistryIdGenerator):
        self.__command_registry_id_generator = command_registry_id_generator
        self.__command_registries: Dict[int, CommandRegistry] = {}

    def add_command_registry(self, command_registry: CommandRegistry) -> int:
        registry_id = self.__command_registry_id_generator.generate()
        self.__command_registries[registry_id] = command_registry
        return registry_id

    def get_command_registry(self, registry_id: int) -> CommandRegistry:
        if registry_id not in self.__command_registries:
            raise KeyError(f"Command Registry {registry_id} not found")

        return self.__command_registries[registry_id]

    def get_command(self, registry_id: int, command_type: int) -> Command:
        return self.get_command_registry(registry_id).get_command(command_type)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandRepository':
        return CommandRepository(dependency_container.get(CommandRegistryIdGenerator))
