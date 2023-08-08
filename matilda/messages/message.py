from dataclasses import dataclass


@dataclass
class Message:
    message_type: int
    data: bytes
