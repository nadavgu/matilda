package test

import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaService
import test.protobuf.TestMessage

@MatildaService
class MatildaTestService {
    @MatildaCommand
    fun sum(first: Int, second: Int): Int {
        return first + second
    }

    @MatildaCommand
    fun reverseString(string: String): String {
        return string.reversed()
    }

    @MatildaCommand
    fun reverseList(values: List<Int>): List<Int> {
        return values.reversed()
    }

    @MatildaCommand
    fun reverseListOfLists(values: List<List<Int>>): List<List<Int>> {
        return values.reversed()
    }

    @MatildaCommand
    fun nothing() {}

    @MatildaCommand
    fun maxMessage(messages: List<TestMessage>): TestMessage {
        val maxLevel = messages.map { it.level }.maxBy { it.value }
        return messages.filter { it.level == maxLevel }.maxBy { it.amount }
    }

    @MatildaCommand
    fun mapInts(function: MatildaTestDynamicService, values: List<Int>): List<Int> {
        return values.map { value ->
            function.applyInt(value)
        }.toList()
    }

    @MatildaCommand
    fun mapStrings(function: MatildaTestDynamicService, values: List<String>): List<String> {
        return values.map { value ->
            function.applyString(value)
        }.toList()
    }

    @MatildaCommand
    fun mapLists(function: MatildaTestDynamicService, values: List<List<Int>>): List<List<Int>> {
        return values.map { value ->
            function.applyList(value)
        }.toList()
    }

    @MatildaCommand
    fun mapMessages(function: MatildaTestDynamicService, values: List<TestMessage>): List<TestMessage> {
        return values.map { value ->
            function.applyMessage(value)
        }.toList()
    }

    @MatildaCommand
    fun isThrowing(function: MatildaTestDynamicVoidService): Boolean {
        return try {
            function.applyVoid()
            false
        } catch (e: Throwable) {
            true
        }
    }

    @MatildaCommand
    fun createAdder(amount: Int): MatildaTestDynamicService {
        return object : MatildaTestDynamicService {
            override fun applyInt(value: Int): Int {
                return value + amount
            }

            override fun applyString(value: String): String {
                return value + amount
            }

            override fun applyList(value: List<Int>): List<Int> {
                return value + amount
            }

            override fun applyMessage(value: TestMessage): TestMessage {
                return TestMessage(value.level, value.amount + amount)
            }
        }
    }

    @MatildaCommand
    fun fail() {
        throw Exception("failure")
    }
}
