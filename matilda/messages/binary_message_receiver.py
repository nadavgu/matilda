from typing import IO

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.messages.message import Message
from matilda.messages.message_receiver import MessageReceiver
from matilda.messages.message_serializer import MessageSerializer


class BinaryMessageReceiver(MessageReceiver, Dependency):
    def __init__(self, serializer: MessageSerializer, input_stream: IO):
        self.__serializer = serializer
        self.__input_stream = input_stream

    def receive(self) -> Message:
        length_bytes = self.__input_stream.read(4)
        if length_bytes == b'':
            raise EOFError()
        length = int.from_bytes(length_bytes, byteorder='little')
        packet = self.__input_stream.read(length)
        return self.__serializer.deserialize(packet)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'BinaryMessageReceiver':
        return BinaryMessageReceiver(dependency_container.get(MessageSerializer),
                                     dependency_container.get(IO, tag=DependencyTags.AGENT_OUTPUT))
