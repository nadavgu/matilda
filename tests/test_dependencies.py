from matilda.environment.filesystem import Filesystem
from tests.plugin import TestPlugin
from tests.protos.test_pb2 import TestMessage, Level


class TestDependencies:
    def test_filesystem_dependency(self, plugin: TestPlugin):
        filesystem = plugin.dependency_container.get(Filesystem)
        assert b'Linux' in filesystem.read("/proc/version")