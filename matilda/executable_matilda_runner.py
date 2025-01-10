from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.resources.resources import get_resource_path


class ExecutableMatildaRunner(CommandMatildaRunner):
    def __init__(self):
        super().__init__(get_resource_path("agent-linuxX64.kexe"))
