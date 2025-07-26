from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_runner import MatildaRunner
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.resources.resources import get_executable_path


class ExecutableMatildaRunner(MatildaRunner):
    def __init__(self):
        self.__command_runner = CommandMatildaRunner(get_executable_path(NativeMatildaPlatform.of_this_machine()))

    def run(self) -> MatildaConnection:
        return self.__command_runner.run()

    def platform(self) -> MatildaPlatform:
        return NativeMatildaPlatform.of_this_machine()
