from abc import ABC, abstractmethod

from matilda.messages.message import Message


class MessageReceiver(ABC):
    @abstractmethod
    def receive(self) -> Message:
        pass
