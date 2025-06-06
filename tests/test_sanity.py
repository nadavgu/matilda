import pytest

from matilda.exceptions.command_failed_exception import CommandFailedException
from tests.plugin import TestPlugin
from tests.protos.test_pb2 import TestMessage, Level


class TestSanity:
    def test_int_function(self, plugin: TestPlugin):
        assert plugin.service.sum(3, 4) == 7

    def test_string_function(self, plugin: TestPlugin):
        assert plugin.service.reverse_string('123') == '321'

    def test_list_function(self, plugin: TestPlugin):
        assert plugin.service.reverse_list([1, 2, 3]) == [3, 2, 1]

    def test_void_function(self, plugin: TestPlugin):
        assert plugin.service.nothing() is None

    def test_message_function(self, plugin: TestPlugin):
        assert (plugin.service.max_message([TestMessage(level=Level.LOW, amount=12.5),
                                            TestMessage(level=Level.MID, amount=5),
                                            TestMessage(level=Level.HIGH, amount=6),
                                            TestMessage(level=Level.HIGH, amount=3)])
                == TestMessage(level=Level.HIGH, amount=6))

    def test_throwing_function(self, plugin: TestPlugin):
        with pytest.raises(CommandFailedException):
            plugin.service.fail()
