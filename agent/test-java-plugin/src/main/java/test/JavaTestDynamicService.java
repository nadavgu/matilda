package test;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaDynamicService;
import test.protobuf.TestMessage;

import java.util.List;

@MatildaDynamicService
public interface JavaTestDynamicService {
    @MatildaCommand
    int applyInt(int value);

    @MatildaCommand
    String applyString(String value);

    @MatildaCommand
    List<Integer> applyList(List<Integer> value);

    @MatildaCommand
    TestMessage applyMessage(TestMessage value);
}
