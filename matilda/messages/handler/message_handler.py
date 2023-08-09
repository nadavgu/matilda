from typing import Callable

from matilda.messages.message import Message

MessageHandler = Callable[[Message], None]
