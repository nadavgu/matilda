from abc import ABC, abstractmethod

from matilda.matilda_connection import MatildaConnection


class MatildaRunner(ABC):
    @abstractmethod
    def run(self) -> MatildaConnection:
        pass
