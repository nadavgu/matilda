from matilda.local_matilda_runner import LocalMatildaRunner
from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.matilda_runner import MatildaRunner
from matilda.platform.supported_platforms import JVM
from matilda.resources.resources import get_resource_path


class JavaProcessMatildaRunner(MatildaRunner):
    def __init__(self, java_path: str = 'java'):
        self.__command_runner = LocalMatildaRunner([java_path, "-cp", get_resource_path("agent.jar"),
                                                     "org.matilda.Main"], JVM)

    def run(self) -> MatildaAgentEnvironment:
        return self.__command_runner.run()
