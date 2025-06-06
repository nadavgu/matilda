package test;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import test.protobuf.TestMessage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@MatildaService
public class JavaTestService {
    @MatildaCommand
    public int sum(int first, int second) {
        return first + second;
    }

    @MatildaCommand
    public String reverseString(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    @MatildaCommand
    public List<Integer> reverseList(List<Integer> values) {
        Collections.reverse(values);
        return values;
    }

    @MatildaCommand
    public void nothing() {}

    @MatildaCommand
    public TestMessage maxMessage(List<TestMessage> messages) {
        return Collections.max(messages,
                Comparator.comparing(TestMessage::getLevel).thenComparing(TestMessage::getAmount));
    }

    @MatildaCommand
    public List<Integer> mapInts(JavaTestDynamicService function, List<Integer> values) {
        return values.stream().map(function::applyInt).collect(Collectors.toList());
    }

    @MatildaCommand
    public List<String> mapStrings(JavaTestDynamicService function, List<String> values) {
        return values.stream().map(function::applyString).collect(Collectors.toList());
    }

    @MatildaCommand
    public List<List<Integer>> mapLists(JavaTestDynamicService function, List<List<Integer>> values) {
        return values.stream().map(function::applyList).collect(Collectors.toList());
    }

    @MatildaCommand
    public List<TestMessage> mapMessages(JavaTestDynamicService function, List<TestMessage> values) {
        return values.stream().map(function::applyMessage).collect(Collectors.toList());
    }

    @MatildaCommand
    public boolean isThrowing(JavaTestDynamicVoidService function) {
        try {
            function.applyVoid();
            return false;
        } catch (Throwable e) {
            return true;
        }
    }

    @MatildaCommand
    public JavaTestDynamicService createAdder(int amount) {
        return new JavaTestDynamicService()  {
            @Override
            public int applyInt(int value) {
                return value + amount;
            }

            @Override
            public String applyString(String value) {
                return value + amount;
            }

            @Override
            public List<Integer> applyList(List<Integer> value) {
                value.add(amount);
                return value;
            }

            @Override
            public TestMessage applyMessage(TestMessage value) {
                return TestMessage.newBuilder().setLevel(value.getLevel()).setAmount(value.getAmount() + amount).build();
            }
        };
    }

    @MatildaCommand
    public void fail() throws Exception {
        throw new Exception("failure");
    }
}
