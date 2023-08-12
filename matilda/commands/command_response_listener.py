from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.exceptions.command_failed_exception import CommandFailedException
from matilda.messages.message import Message
from matilda.messages.message_listener import MessageListener, MessageListeningInstance
from matilda.generated.proto.message_pb2 import MessageType
from matilda.generated.proto.command_pb2 import CommandResponse


def _parse_command_response(data: bytes) -> CommandResponse:
    command_response = CommandResponse()
    command_response.ParseFromString(data)
    return command_response


class CommandResponseListeningInstance:
    def __init__(self, message_listening_instance: MessageListeningInstance):
        self.__message_listening_instance = message_listening_instance

    def wait_for_response(self) -> bytes:
        message = self.__message_listening_instance.wait_for_message()
        command_response = _parse_command_response(message.data)
        if not command_response.success:
            raise CommandFailedException(command_response.result.decode())
        return command_response.result

    def stop(self):
        self.__message_listening_instance.stop()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.stop()


class CommandResponseListener(Dependency):
    def __init__(self, message_listener: MessageListener):
        self.__message_listener = message_listener

    def listen(self, command_id: int):
        def check_message(message: Message):
            return _parse_command_response(message.data).id == command_id

        listening_instance = self.__message_listener.listen(MessageType.COMMAND_RESPONSE, predicate=check_message)
        return CommandResponseListeningInstance(listening_instance)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandResponseListener':
        return CommandResponseListener(dependency_container.get(MessageListener))
