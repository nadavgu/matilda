from abc import ABC, abstractmethod


class Filesystem(ABC):
    @abstractmethod
    def read(self, path: str) -> bytes:
        pass
