from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_runner import MatildaRunner
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.supported_platforms import JVM
from matilda.resources.resources import get_resource_path


class JavaProcessMatildaRunner(MatildaRunner):
    def __init__(self, java_path: str = 'java'):
        self.__command_runner = CommandMatildaRunner(java_path, "-cp", get_resource_path("agent.jar"),
                                                     "org.matilda.Main")

    def run(self) -> MatildaConnection:
        return self.__command_runner.run()

    def platform(self) -> MatildaPlatform:
        return JVM
