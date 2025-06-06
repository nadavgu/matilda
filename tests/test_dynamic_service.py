from typing import List

import pytest

from tests.generated.commands.matilda_test_dynamic_service import MatildaTestDynamicService
from tests.generated.commands.matilda_test_dynamic_void_service import MatildaTestDynamicVoidService
from tests.plugin import TestPlugin
from tests.protos.test_pb2 import TestMessage, Level


class Reverser(MatildaTestDynamicService):
    def apply_int(self, value: int) -> int:
        return -value

    def apply_string(self, value: str) -> str:
        return value[::-1]

    def apply_list(self, value: List[int]) -> List[int]:
        return value[::-1]

    def apply_message(self, value: TestMessage) -> TestMessage:
        if value.level is Level.HIGH:
            level = Level.LOW
        elif value.level is Level.LOW:
            level = Level.HIGH
        else:
            level = Level.MID
        return TestMessage(level=level, amount=-value.amount)


class TestDynamicService:
    @pytest.fixture
    def reverser(self) -> Reverser:
        return Reverser()

    def test_int_callback(self, plugin: TestPlugin, reverser: MatildaTestDynamicService):
        assert plugin.service.map_ints(reverser, [1, -2, 3]) == [-1, 2, -3]

    def test_string_callback(self, plugin: TestPlugin, reverser: MatildaTestDynamicService):
        assert plugin.service.map_strings(reverser, ["abc", "123"]) == ["cba", "321"]

    def test_list_callback(self, plugin: TestPlugin, reverser: MatildaTestDynamicService):
        assert plugin.service.map_lists(reverser, [[1, 2, 3], [4, 5, 6]]) == [[3, 2, 1], [6, 5, 4]]

    def test_message_callback(self, plugin: TestPlugin, reverser: MatildaTestDynamicService):
        assert (plugin.service.map_messages(reverser,
                                            [TestMessage(level=Level.LOW, amount=12.5),
                                                       TestMessage(level=Level.MID, amount=5),
                                                       TestMessage(level=Level.HIGH, amount=6)]) ==
                [TestMessage(level=Level.HIGH, amount=-12.5),
                 TestMessage(level=Level.MID, amount=-5),
                 TestMessage(level=Level.LOW, amount=-6)])

    def test_void_callback(self, plugin: TestPlugin):
        class CountingCallback(MatildaTestDynamicVoidService):
            def __init__(self):
                self.times_called = 0

            def apply_void(self) -> None:
                self.times_called += 1

        callback = CountingCallback()
        assert not plugin.service.is_throwing(callback)
        assert callback.times_called == 1

    def test_throwing_callback(self, plugin: TestPlugin):
        class ThrowingCallback(MatildaTestDynamicVoidService):
            def apply_void(self) -> None:
                raise Exception("123")

        assert plugin.service.is_throwing(ThrowingCallback())

    def test_dynamic_service_function(self, plugin: TestPlugin):
        assert plugin.service.create_adder(3).apply_int(4) == 7

    def test_pass_dynamic_service_as_callback(self, plugin: TestPlugin):
        assert plugin.service.map_ints(plugin.service.create_adder(3), [1, 2, 3]) == [4, 5, 6]