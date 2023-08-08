from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer

from matilda.messages.message_receiver import MessageReceiver


class MessageListener(Dependency):
    def __init__(self, message_receiver: MessageReceiver):
        self.__message_receiver = message_receiver

    def start(self):
        while True:
            try:
                message = self.__message_receiver.receive()
                print(message)
            except EOFError:
                print("Done")
                return

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageListener':
        return MessageListener(dependency_container.get(MessageReceiver))
