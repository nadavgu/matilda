from abc import ABC, abstractmethod


class MatildaRunner(ABC):
    @abstractmethod
    def run(self):
        pass
