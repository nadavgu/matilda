from abc import ABC, abstractmethod

from matilda.messages.message import Message


class MessageSerializer(ABC):
    @abstractmethod
    def serialize(self, message: Message) -> bytes:
        pass

    @abstractmethod
    def deserialize(self, binary: bytes) -> Message:
        pass
