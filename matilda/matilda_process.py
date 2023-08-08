from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.matilda_connection import MatildaConnection
from matilda.messages.message import Message
from matilda.messages.message_sender import MessageSender


class MatildaProcess(Dependency):
    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        dependency_container.get(MessageSender).send(Message(10, b"hello"))
        print(dependency_container.get(MatildaConnection).agent_output.read())
        return MatildaProcess()
