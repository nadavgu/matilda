package org.matilda.commands.python;

import org.matilda.commands.utils.Package;

public class PythonClasses {
    public static PythonClassName DEPENDENCY_CLASS =
            new PythonClassName(new Package("maddie", "dependency"), "Dependency");

    public static PythonClassName DEPENDENCY_CONTAINER_CLASS =
            new PythonClassName(new Package("maddie", "dependency_container"), "DependencyContainer");

    public static PythonClassName COMMAND_RUNNER_CLASS =
            new PythonClassName(new Package("matilda", "commands", "command_runner"), "CommandRunner");

    public static Package PROTO_WRAPPERS_PACKAGE = new Package("google", "protobuf", "wrappers_pb2");
}
