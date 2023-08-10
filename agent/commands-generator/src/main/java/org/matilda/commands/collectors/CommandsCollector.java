package org.matilda.commands.collectors;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ServiceInfo;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsCollector {

    @Inject
    public CommandsCollector() {}

    public List<CommandInfo> collect(ServiceInfo serviceInfo, TypeElement serviceElement) {
        return serviceElement.getEnclosedElements().stream()
                .filter(element -> element.getAnnotation(MatildaCommand.class) != null)
                .map(element -> collectCommand(element, serviceInfo))
                .collect(Collectors.toList());
    }

    private CommandInfo collectCommand(Element element, ServiceInfo serviceInfo) {
        System.out.println(element);
        return new CommandInfo(element.getSimpleName().toString(), serviceInfo);
    }
}
