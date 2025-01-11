from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.platform.matilda_platform import MatildaPlatform, get_native_platform_of_this_machine
from matilda.resources.resources import get_resource_path


class ExecutableMatildaRunner(CommandMatildaRunner):
    def __init__(self):
        super().__init__(get_resource_path("agent-linuxX64.kexe"))

    def platform(self) -> MatildaPlatform:
        return get_native_platform_of_this_machine()
