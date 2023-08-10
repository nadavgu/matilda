package org.matilda.commands.collectors;

import org.matilda.commands.MatildaService;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.info.ServiceInfo;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ServicesCollector {
    @Inject
    RoundEnvironment mRoundEnvironment;

    @Inject
    CommandsCollector mCommandsCollector;

    @Inject
    public ServicesCollector() {}

    public ProjectServices collect() {
        return new ProjectServices(mRoundEnvironment.getElementsAnnotatedWith(MatildaService.class)
                .stream()
                .map(TypeElement.class::cast)
                .map(this::collectService)
                .collect(Collectors.toList()));
    }

    private ServiceInfo collectService(TypeElement element) {
        ServiceInfo serviceInfo = new ServiceInfo(element.getQualifiedName().toString(), new ArrayList<>());
        serviceInfo.commands().addAll(mCommandsCollector.collect(serviceInfo, element));
        return serviceInfo;
    }
}
