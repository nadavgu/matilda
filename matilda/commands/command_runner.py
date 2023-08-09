from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_id_generator import CommandIdGenerator
from matilda.commands.command_response_listener import CommandResponseListener
from matilda.commands.command_sender import CommandSender


class CommandRunner(Dependency):
    def __init__(self, command_sender: CommandSender,
                 command_response_listener: CommandResponseListener,
                 command_id_generator: CommandIdGenerator):
        self.__command_sender = command_sender
        self.__command_response_listener = command_response_listener
        self.__command_id_generator = command_id_generator

    def run(self, command_type: int, parameter: bytes) -> bytes:
        command_id = self.__command_id_generator.generate()
        with self.__command_response_listener.listen(command_id) as listening_instance:
            self.__command_sender.send(command_type, command_id, parameter)
            return listening_instance.wait_for_response()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandRunner':
        return CommandRunner(dependency_container.get(CommandSender),
                             dependency_container.get(CommandResponseListener),
                             dependency_container.get(CommandIdGenerator))
