from abc import ABC, abstractmethod
from typing import IO, AnyStr


class MatildaConnection(ABC):
    @property
    @abstractmethod
    def agent_input(self) -> IO[AnyStr]:
        pass

    @property
    @abstractmethod
    def agent_output(self) -> IO[AnyStr]:
        pass

    @abstractmethod
    def close(self):
        pass

    def __enter__(self) -> 'MatildaConnection':
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

