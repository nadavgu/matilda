package test

import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaDynamicService
import test.protobuf.TestMessage

@MatildaDynamicService
interface MatildaTestDynamicVoidService {
    @MatildaCommand
    fun applyVoid()
}
