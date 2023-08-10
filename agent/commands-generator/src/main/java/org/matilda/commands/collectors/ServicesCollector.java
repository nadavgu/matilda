package org.matilda.commands.collectors;

import org.matilda.commands.MatildaService;
import org.matilda.commands.exceptions.AnnotationProcessingException;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.info.ServiceInfo;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
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
        ServiceInfo serviceInfo = new ServiceInfo(element.getQualifiedName().toString(), element.asType(),
                new ArrayList<>(), checkIfServiceHasInjectConstructor(element));
        serviceInfo.commands().addAll(mCommandsCollector.collect(serviceInfo, element));
        return serviceInfo;
    }

    private boolean checkIfServiceHasInjectConstructor(TypeElement element) {
        List<Element> constructors = getConstructors(element);
        if (constructors.size() == 0) {
            return false;
        }

        if (constructors.stream().noneMatch(constructor -> constructor.getAnnotation(Inject.class) != null)) {
            throw new AnnotationProcessingException("This service has non-default constructors that aren't marked " +
                    "with @Inject. In order for you to use the sweet sweet DI, either mark a constructor with " +
                    "@Inject, or delete all the constructors, so that I will be able to @Provide it to you",
                    constructors.get(0));
        }

        return true;
    }

    private List<Element> getConstructors(TypeElement element) {
        return element.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.CONSTRUCTOR)
                .collect(Collectors.toList());
    }
}
