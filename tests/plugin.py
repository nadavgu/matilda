from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer
from tests.generated.commands.matilda_test_service import MatildaTestService

from matilda.platform.matilda_platform import MatildaPlatform
from matilda.plugins.plugin_entry_point import PluginEntryPoint

PLUGIN_ENTRY_POINTS = {
    MatildaPlatform.JVM: PluginEntryPoint("test.TestPlugin"),
    MatildaPlatform.LINUX_X64: PluginEntryPoint("createCommandRegistry"),
}


def load_plugin(dependencies_container: DependencyContainer):
    return dependencies_container.get(TestPlugin)


class TestPlugin(Dependency):
    def __init__(self, test_service: MatildaTestService):
        self.__test_service = test_service

    @property
    def service(self) -> MatildaTestService:
        return self.__test_service

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'TestPlugin':
        return TestPlugin(dependency_container.get(MatildaTestService))
