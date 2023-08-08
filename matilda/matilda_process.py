from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.messages.message import Message
from matilda.messages.message_sender import MessageSender


class MatildaProcess(Dependency):
    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'MatildaProcess':
        dependency_container.get(MessageSender).send(Message(10, b"hello"))
        print(dependency_container.get(IO, DependencyTags.AGENT_OUTPUT).read())
        return MatildaProcess()
