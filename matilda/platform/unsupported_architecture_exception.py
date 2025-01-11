class UnsupportedArchitectureException(Exception):
    def __init__(self, architecture: str):
        super().__init__(f"Unsupported architecture: {architecture}. Cannot run native matilda agent")
