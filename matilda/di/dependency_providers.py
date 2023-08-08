from typing import IO

from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_tags import DependencyTags
from matilda.matilda_connection import MatildaConnection
from matilda.messages.binary_message_receiver import BinaryMessageReceiver
from matilda.messages.binary_message_sender import BinaryMessageSender
from matilda.messages.message_receiver import MessageReceiver
from matilda.messages.message_sender import MessageSender
from matilda.messages.message_serializer import MessageSerializer
from matilda.messages.protobuf_message_serializer import ProtobufMessageSerializer


def agent_input_provider(dependency_container: DependencyContainer) -> IO:
    return dependency_container.get(MatildaConnection).agent_input


def agent_output_provider(dependency_container: DependencyContainer) -> IO:
    return dependency_container.get(MatildaConnection).agent_output


def add_dependency_providers(dependency_container: DependencyContainer):
    dependency_container.bind(MessageSender, BinaryMessageSender)
    dependency_container.bind(MessageReceiver, BinaryMessageReceiver)
    dependency_container.bind(MessageSerializer, ProtobufMessageSerializer)
    dependency_container.add_provider(IO, agent_input_provider, tag=DependencyTags.AGENT_INPUT)
    dependency_container.add_provider(IO, agent_output_provider, tag=DependencyTags.AGENT_OUTPUT)
