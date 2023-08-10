package org.matilda.commands.info;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public record ServiceInfo(String fullName, TypeMirror type, List<CommandInfo> commands) {
}
