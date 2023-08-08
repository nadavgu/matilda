from maddie.dependency_container import DependencyContainer

from matilda.messages.binary_message_sender import BinaryMessageSender
from matilda.messages.message_sender import MessageSender
from matilda.messages.message_serializer import MessageSerializer
from matilda.messages.protobuf_message_serializer import ProtobufMessageSerializer


def add_dependency_providers(dependency_container: DependencyContainer):
    dependency_container.bind(MessageSender, BinaryMessageSender)
    dependency_container.bind(MessageSerializer, ProtobufMessageSerializer)
