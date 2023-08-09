from matilda.messages.handler.message_handler import MessageHandler


class MessageHandlerRegistration:
    def __init__(self, message_handler_registry, message_type: int, message_handler: MessageHandler):
        self.__type = message_type
        self.__handler = message_handler
        self.__registry = message_handler_registry

    def unregister(self):
        self.__registry.unregister(self.__type, self.__handler)

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.unregister()
