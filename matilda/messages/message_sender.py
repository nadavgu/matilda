from abc import ABC, abstractmethod

from matilda.messages.message import Message


class MessageSender(ABC):
    @abstractmethod
    def send(self, message: Message):
        pass
