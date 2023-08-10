package org.matilda.commands.info;

import java.util.List;

public record ServiceInfo(String fullName, List<CommandInfo> commands) {
}
