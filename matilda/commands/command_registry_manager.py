from abc import ABC, abstractmethod

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_registry import CommandRegistry


class CommandRegistryManager(Dependency, ABC):
    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandRegistryManager':
        from matilda.commands.command_repository import CommandRepository
        return dependency_container.get(CommandRepository)

    @abstractmethod
    def add_command_registry(self, command_registry: CommandRegistry) -> int:
        pass
