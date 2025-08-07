class ShellCommandFailedException(Exception):
    def __init__(self, command: str, exit_code: int, stderr: bytes):
        super().__init__(f"Shell command \"{command}\" failed. Exit code: {exit_code} ({stderr})")