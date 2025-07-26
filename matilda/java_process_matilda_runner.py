from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.supported_platforms import JVM
from matilda.resources.resources import get_resource_path


class JavaProcessMatildaRunner(CommandMatildaRunner):
    def __init__(self, java_path: str = 'java'):
        super(JavaProcessMatildaRunner, self).__init__(java_path, "-cp", get_resource_path("agent.jar"), "org.matilda.Main")

    def platform(self) -> MatildaPlatform:
        return JVM
