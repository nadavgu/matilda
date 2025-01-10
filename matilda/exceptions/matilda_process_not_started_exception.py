class MatildaProcessNotStartedException(Exception):
    def __init__(self, exit_code: int, stderr: bytes):
        super().__init__(f"Matilda process could not be started. Exit code: {exit_code} ({stderr})")