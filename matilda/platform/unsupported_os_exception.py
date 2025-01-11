class UnsupportedOSException(Exception):
    def __init__(self, system: str):
        super().__init__(f"Unsupported operating system: {system}. Cannot run native matilda agent")
