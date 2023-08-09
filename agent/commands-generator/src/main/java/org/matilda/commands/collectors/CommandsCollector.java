package org.matilda.commands.collectors;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.info.CommandInfo;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsCollector {

    @Inject
    public CommandsCollector() {}

    public List<CommandInfo> collect(TypeElement serviceElement) {
        return serviceElement.getEnclosedElements().stream()
                .filter(element -> element.getAnnotation(MatildaCommand.class) != null)
                .map(this::collectCommand)
                .collect(Collectors.toList());
    }

    private CommandInfo collectCommand(Element element) {
        System.out.println(element);
        return new CommandInfo();
    }
}
