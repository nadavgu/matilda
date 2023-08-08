from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.generated.proto.message_pb2 import ProtobufMessage
from matilda.messages.message import Message
from matilda.messages.message_serializer import MessageSerializer


class ProtobufMessageSerializer(MessageSerializer, Dependency):
    def serialize(self, message: Message) -> bytes:
        protobuf_message = ProtobufMessage()
        protobuf_message.type = message.message_type
        protobuf_message.data = message.data
        return protobuf_message.SerializeToString()

    def deserialize(self, binary: bytes) -> Message:
        protobuf_message = ProtobufMessage()
        protobuf_message.ParseFromString()
        return Message(message_type=protobuf_message.type, data=protobuf_message.data)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'ProtobufMessageSerializer':
        return ProtobufMessageSerializer()
