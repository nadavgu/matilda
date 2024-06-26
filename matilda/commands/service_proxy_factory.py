from abc import ABC, abstractmethod
from typing import Generic, TypeVar

T = TypeVar('T')


class ServiceProxyFactory(ABC, Generic[T]):
    @abstractmethod
    def create_service_proxy(self, command_registry_id: int) -> T:
        pass
