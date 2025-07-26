from pathlib import Path

from maddie.dependency_container import DependencyContainer

from matilda.platform.supported_platforms import ANDROID, JVM
from matilda.plugins.plugin_entry_point import PluginEntryPoint
from tests.generated.commands.java_test_service import JavaTestService
from tests.plugin import TestPlugin

PLUGIN_ENTRY_POINTS = {
    JVM: PluginEntryPoint("test.JavaTestPlugin",
                                          binary_path=str(Path(__file__).parent / "resources" / "java-plugin.jar")),
    ANDROID: PluginEntryPoint("test.JavaTestPlugin",
                                          binary_path=str(Path(__file__).parent / "resources" / "android-java-plugin.apk")),
}


def load_plugin(dependencies_container: DependencyContainer):
    return TestPlugin(dependencies_container.get(JavaTestService))

