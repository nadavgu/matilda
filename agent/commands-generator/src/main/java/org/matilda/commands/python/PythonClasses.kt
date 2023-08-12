package org.matilda.commands.python

import org.matilda.commands.utils.Package

val DEPENDENCY_CLASS = PythonTypeName(Package("maddie", "dependency"), "Dependency")
val DEPENDENCY_CONTAINER_CLASS = PythonTypeName(Package("maddie", "dependency_container"), "DependencyContainer")
val COMMAND_RUNNER_CLASS = PythonTypeName(Package("matilda", "commands", "command_runner"), "CommandRunner")
val ANY_CLASS = PythonTypeName(Package("google", "protobuf", "any_pb2"), "Any")