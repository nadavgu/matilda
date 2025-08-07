from abc import ABC, abstractmethod


class MatildaPlatform(ABC):
    @abstractmethod
    def is_android(self) -> bool:
        pass
