from abc import ABC, abstractmethod
from typing import Generic, TypeVar

from matilda.commands.command_registry import CommandRegistry

T = TypeVar('T')


class CommandRegistryFactory(ABC, Generic[T]):
    @abstractmethod
    def create_command_registry(self, service: T) -> CommandRegistry:
        pass
