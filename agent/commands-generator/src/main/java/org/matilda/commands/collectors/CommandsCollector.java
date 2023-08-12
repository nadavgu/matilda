package org.matilda.commands.collectors;

import com.google.protobuf.Message;
import org.matilda.commands.MatildaCommand;
import org.matilda.commands.exceptions.AnnotationProcessingException;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ServiceInfo;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsCollector {

    @Inject
    Types mTypes;

    @Inject
    Elements mElements;

    @Inject
    public CommandsCollector() {}

    public List<CommandInfo> collect(ServiceInfo serviceInfo, TypeElement serviceElement) {
        return serviceElement.getEnclosedElements().stream()
                .filter(element -> element.getAnnotation(MatildaCommand.class) != null)
                .map(ExecutableElement.class::cast)
                .map(element -> collectCommand(element, serviceInfo))
                .collect(Collectors.toList());
    }

    private CommandInfo collectCommand(ExecutableElement element, ServiceInfo serviceInfo) {
        return new CommandInfo(element.getSimpleName().toString(), serviceInfo,
                getParameterType(element), getReturnType(element));
    }

    private TypeMirror getParameterType(ExecutableElement element) {
        List<? extends VariableElement> params = element.getParameters();
        if (params.size() != 1) {
            throw new AnnotationProcessingException("Can only handle commands with one parameter", element);
        }

        TypeMirror type = params.get(0).asType();
        verifyType(type, element);
        return type;
    }

    private TypeMirror getReturnType(ExecutableElement element) {
        TypeMirror type = element.getReturnType();
        verifyType(type, element);
        return type;
    }

    private void verifyType(TypeMirror type, Element element) {
        TypeMirror messageBaseType = mTypes.getDeclaredType(mElements.getTypeElement(Message.class.getCanonicalName()));
        if (!mTypes.isSubtype(type, messageBaseType)) {
            throw new AnnotationProcessingException("Paramaters and return values of services have to be " +
                    "protobuf messages!", element);
        }
    }
}
