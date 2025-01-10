from abc import ABC, abstractmethod

from matilda.messages.message import Message


class MessageHandler(ABC):
    @abstractmethod
    def handle_message(self, message: Message):
        pass

    def handle_no_more_messages(self):
        pass
