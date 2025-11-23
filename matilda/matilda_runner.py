from abc import ABC, abstractmethod

from matilda.environment.matilda_environment import MatildaAgentEnvironment


class MatildaRunner(ABC):
    @abstractmethod
    def run(self) -> MatildaAgentEnvironment:
        pass
