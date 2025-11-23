from matilda.local_matilda_runner import LocalMatildaRunner
from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.matilda_runner import MatildaRunner
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.resources.resources import get_executable_path


class ExecutableMatildaRunner(MatildaRunner):
    def __init__(self, wait_for_debugger: bool = False):
        args = [get_executable_path(NativeMatildaPlatform.of_this_machine())]
        if wait_for_debugger:
            args.append("--wait-for-debugger")
        self.__command_runner = LocalMatildaRunner(args,
                                                   NativeMatildaPlatform.of_this_machine())

    def run(self) -> MatildaAgentEnvironment:
        return self.__command_runner.run()
