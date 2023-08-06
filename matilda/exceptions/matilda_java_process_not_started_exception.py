class MatildaJavaProcessNotStartedException(Exception):
    def __init__(self, exit_code: int, stderr: bytes):
        super().__init__(f"Matilda java process could not be started. Exit code: {exit_code} ({stderr})")