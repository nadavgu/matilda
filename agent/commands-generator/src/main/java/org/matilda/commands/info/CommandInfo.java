package org.matilda.commands.info;

import javax.lang.model.type.TypeMirror;

public record CommandInfo(String name, ServiceInfo service, TypeMirror parameterType, TypeMirror returnType) {
}
