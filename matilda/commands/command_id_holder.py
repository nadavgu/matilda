from typing import Optional

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer


class CommandIdHolder(Dependency):
    command_id: Optional[int]

    def __init__(self, command_id: Optional[int] = None):
        self.command_id = command_id

    @staticmethod
    def create(_: DependencyContainer) -> 'CommandIdHolder':
        return CommandIdHolder()
