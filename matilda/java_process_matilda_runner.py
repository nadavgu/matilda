from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.resources.resources import get_resource_path


class JavaProcessMatildaRunner(CommandMatildaRunner):
    def __init__(self, java_path: str = 'java'):
        super(JavaProcessMatildaRunner, self).__init__(java_path, "-cp", get_resource_path("agent.jar"), "org.matilda.Main")
