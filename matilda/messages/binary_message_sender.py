from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.messages.message import Message
from matilda.messages.message_sender import MessageSender
from matilda.messages.message_serializer import MessageSerializer


class BinaryMessageSender(MessageSender, Dependency):
    def __init__(self, serializer: MessageSerializer, output_stream: IO):
        self.__serializer = serializer
        self.__output_stream = output_stream

    def send(self, message: Message):
        packet = self.__serializer.serialize(message)
        self.__output_stream.write(len(packet).to_bytes(length=4, byteorder='little'))
        self.__output_stream.write(packet)
        self.__output_stream.flush()

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'BinaryMessageSender':
        return BinaryMessageSender(dependency_container.get(MessageSerializer),
                                   dependency_container.get(IO, tag=DependencyTags.AGENT_INPUT))

