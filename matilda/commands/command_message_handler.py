from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer

from matilda.messages.message import Message
from matilda.messages.message_sender import MessageSender
from matilda.generated.proto.command_pb2 import CommandRequest


class CommandMessageHandler(Dependency):
    def __init__(self, message_sender: MessageSender):
        self.__message_sender = message_sender

    def handle(self, message: Message):
        command_request = self.__parse_command_request(message.data)
        print(command_request)

    @staticmethod
    def __parse_command_request(data: bytes) -> CommandRequest:
        command_request = CommandRequest()
        command_request.ParseFromString(data)
        return command_request

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandMessageHandler':
        return CommandMessageHandler(dependency_container.get(MessageSender))
