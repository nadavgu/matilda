package org.matilda.commands.python

import org.matilda.commands.utils.Package

val DEPENDENCY_CLASS = PythonClassName(Package("maddie", "dependency"), "Dependency")
val DEPENDENCY_CONTAINER_CLASS = PythonClassName(Package("maddie", "dependency_container"), "DependencyContainer")
val COMMAND_RUNNER_CLASS = PythonClassName(Package("matilda", "commands", "command_runner"), "CommandRunner")
val COMMAND_REGISTRY_CLASS = PythonClassName(Package("matilda", "commands", "command_registry"), "CommandRegistry")
val COMMAND_REGISTRY_FACTORY_CLASS = PythonClassName(Package("matilda", "commands", "command_registry_factory"), "CommandRegistryFactory")
val COMMAND_REPOSITORY_CLASS = PythonClassName(Package("matilda", "commands", "command_repository"), "CommandRepository")
val SERVICE_PROXY_FACTORY_CLASS = PythonClassName(Package("matilda", "commands", "service_proxy_factory"), "ServiceProxyFactory")
val ANY_CLASS = PythonClassName(Package("google", "protobuf", "any_pb2"), "Any")
val ABC_CLASS = PythonClassName(Package("abc"), "ABC")
val DATACLASS = PythonClassName(Package("dataclasses"), "dataclass")
val ABSTRACTMETHOD_FUNCTION = PythonGlobalElement(Package("abc"), "abstractmethod")