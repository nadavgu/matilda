from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer
from matilda.generated.proto.command_pb2 import CommandRequest
from matilda.generated.proto.message_pb2 import MessageType
from matilda.messages.message import Message

from matilda.messages.message_sender import MessageSender


class CommandSender(Dependency):
    def __init__(self, message_sender: MessageSender):
        self.__message_sender = message_sender

    def send(self, command_type: int, command_id: int, parameter: bytes):
        command_request = CommandRequest()
        command_request.type = command_type
        command_request.param = parameter
        command_request.id = command_id
        self.__message_sender.send(Message(MessageType.COMMAND, command_request.SerializeToString()))

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandSender':
        return CommandSender(dependency_container.get(MessageSender))
