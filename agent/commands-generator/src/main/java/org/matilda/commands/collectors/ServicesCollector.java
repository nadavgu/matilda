package org.matilda.commands.collectors;

import org.matilda.commands.MatildaService;
import org.matilda.commands.exceptions.AnnotationProcessingException;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.info.ServiceInfo;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
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

        if (constructors.stream().anyMatch(constructor -> constructor.getAnnotation(Inject.class) != null)) {
            return true;
        }

        if (constructors.stream().anyMatch(this::isNonDefaultConstructor)) {
            throw new AnnotationProcessingException("This service has non-default constructors that aren't marked " +
                    "with @Inject. In order for you to use the sweet sweet DI, mark a constructor with " +
                    "@Inject, or, if you don't want amazing DI, delete all the non-default constructors," +
                    " so that I will be able to @Provide you",
                    constructors.get(0));
        }

        return false;
    }

    private List<Element> getConstructors(TypeElement element) {
        return element.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.CONSTRUCTOR)
                .collect(Collectors.toList());
    }

    private boolean isNonDefaultConstructor(Element constructor) {
        return ((ExecutableElement) constructor).getParameters().size() > 0;
    }
}
