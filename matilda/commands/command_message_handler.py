import traceback

from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_repository import CommandRepository
from matilda.messages.message import Message
from matilda.messages.message_sender import MessageSender
from matilda.generated.proto.command_pb2 import CommandRequest
from matilda.generated.proto.command_pb2 import CommandResponse
from matilda.generated.proto.message_pb2 import MessageType


class CommandMessageHandler(Dependency):
    def __init__(self, message_sender: MessageSender, command_repository: CommandRepository):
        self.__message_sender = message_sender
        self.__command_repository = command_repository

    def handle(self, message: Message):
        command_request = self.__parse_command_request(message.data)
        command = self.__command_repository.get_command(command_request.registry_id, command_request.type)
        try:
            result = command(command_request.param)
            self.__report_command_success(command_request, result)
        except (Exception,):
            self.__report_command_failure(command_request, traceback.format_exc())

    @staticmethod
    def __parse_command_request(data: bytes) -> CommandRequest:
        command_request = CommandRequest()
        command_request.ParseFromString(data)
        return command_request

    def __report_command_success(self, command_request: CommandRequest, result: bytes):
        self.__report_command_status(command_request, result, True)

    def __report_command_failure(self, command_request: CommandRequest, error: str):
        self.__report_command_status(command_request, error.encode(), True)

    def __report_command_status(self, command_request: CommandRequest, result: bytes, success: bool):
        command_response = CommandResponse()
        command_response.id = command_request.id
        command_response.success = success
        command_response.result = result
        self.__message_sender.send(Message(MessageType.COMMAND_RESPONSE, command_response.SerializeToString()))

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandMessageHandler':
        return CommandMessageHandler(dependency_container.get(MessageSender),
                                     dependency_container.get(CommandRepository))
