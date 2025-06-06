package test;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaDynamicService;
import test.protobuf.TestMessage;

import java.util.List;

@MatildaDynamicService
public interface JavaTestDynamicVoidService {
    @MatildaCommand
    void applyVoid();
}
