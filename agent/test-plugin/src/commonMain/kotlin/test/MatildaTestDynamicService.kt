package test

import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaDynamicService
import test.protobuf.TestMessage

@MatildaDynamicService
interface MatildaTestDynamicService {
    @MatildaCommand
    fun applyInt(value: Int): Int

    @MatildaCommand
    fun applyString(value: String): String

    @MatildaCommand
    fun applyList(value: List<Int>): List<Int>

    @MatildaCommand
    fun applyMessage(value: TestMessage): TestMessage
}
