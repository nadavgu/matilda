package org.matilda.commands.collectors;

import org.matilda.commands.MatildaService;
import org.matilda.commands.info.ProjectServicesInfo;
import org.matilda.commands.info.ServiceInfo;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import java.util.stream.Collectors;

public class ServicesCollector {
    @Inject
    RoundEnvironment mRoundEnvironment;

    @Inject
    public ServicesCollector() {}

    public ProjectServicesInfo collect() {
        return new ProjectServicesInfo(mRoundEnvironment.getElementsAnnotatedWith(MatildaService.class)
                .stream()
                .map(this::collectService)
                .collect(Collectors.toList()));
    }

    private ServiceInfo collectService(Element element) {
        System.out.println(element);
        return new ServiceInfo();
    }
}
