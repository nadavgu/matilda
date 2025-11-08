from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_runner import MatildaRunner
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.resources.resources import get_executable_path


class ExecutableMatildaRunner(MatildaRunner):
    def __init__(self, wait_for_debugger: bool = False):
        args = [get_executable_path(NativeMatildaPlatform.of_this_machine())]
        if wait_for_debugger:
            args.append("--wait-for-debugger")
        self.__command_runner = CommandMatildaRunner(args)

    def run(self) -> MatildaConnection:
        return self.__command_runner.run()

    def platform(self) -> MatildaPlatform:
        return NativeMatildaPlatform.of_this_machine()
