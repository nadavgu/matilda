from typing import Union

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.platform.supported_platforms import JVM, ANDROID, LINUX_X64
from tests.generated.commands.java_test_service import JavaTestService
from tests.generated.commands.matilda_test_service import MatildaTestService

from matilda.plugins.plugin_entry_point import PluginEntryPoint

PLUGIN_ENTRY_POINTS = {
    JVM: PluginEntryPoint("test.TestPlugin"),
    ANDROID: PluginEntryPoint("test.TestPlugin"),
    LINUX_X64: PluginEntryPoint("createCommandRegistry"),
}


def load_plugin(dependencies_container: DependencyContainer):
    return dependencies_container.get(TestPlugin)


class TestPlugin(Dependency):
    def __init__(self, test_service: Union[MatildaTestService, JavaTestService]):
        self.__test_service = test_service

    @property
    def service(self) -> MatildaTestService:
        return self.__test_service

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'TestPlugin':
        return TestPlugin(dependency_container.get(MatildaTestService))
