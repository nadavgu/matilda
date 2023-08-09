from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.messages.handler.message_dispatcher import MessageDispatcher
from matilda.messages.message_receiver import MessageReceiver


class MessageListener(Dependency):
    def __init__(self, message_receiver: MessageReceiver, message_dispatcher: MessageDispatcher):
        self.__message_receiver = message_receiver
        self.__message_dispatcher = message_dispatcher

    def start(self):
        while True:
            try:
                message = self.__message_receiver.receive()
                self.__message_dispatcher.dispatch(message)
            except EOFError:
                print("Done")
                return

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MessageListener':
        return MessageListener(dependency_container.get(MessageReceiver),
                               dependency_container.get(MessageDispatcher))
