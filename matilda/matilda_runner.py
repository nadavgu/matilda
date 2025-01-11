from abc import ABC, abstractmethod

from matilda.matilda_connection import MatildaConnection
from matilda.platform.matilda_platform import MatildaPlatform


class MatildaRunner(ABC):
    @abstractmethod
    def run(self) -> MatildaConnection:
        pass

    @abstractmethod
    def platform(self) -> MatildaPlatform:
        pass
