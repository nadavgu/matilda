import importlib
import sys
from functools import cached_property
from pathlib import Path
from typing import Dict

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_id_holder import CommandIdHolder
from matilda.commands.command_registry_manager import CommandRegistryManager
from matilda.commands.command_runner import CommandRunner
from matilda.generated.commands.plugins_service import PluginsService

if sys.version_info < (3, 10):
    from importlib_metadata import entry_points
else:
    from importlib.metadata import entry_points


class PluginsModule(Dependency):
    def __init__(self, plugins_service: PluginsService, command_runner: CommandRunner,
                 command_registry_manager: CommandRegistryManager):
        self.__plugins_service = plugins_service
        self.__command_runner = command_runner
        self.__command_registry_manager = command_registry_manager

    def __getattr__(self, item):
        entry_point = self.__entry_points[item]
        plugin = self.__load_plugin(entry_point)
        setattr(self, item, plugin)
        return plugin

    @cached_property
    def __entry_points(self) -> Dict[str, str]:
        return {entry_point.name: entry_point.value for entry_point in entry_points(group='matilda.plugins')}

    def __load_plugin(self, entry_point: str):
        module = importlib.import_module(entry_point)
        plugin_dependencies = self.__load_plugin_dependencies(module)
        return module.load_plugin(plugin_dependencies)

    def __load_plugin_dependencies(self, module):
        dependencies_container = DependencyContainer()
        dependencies_container.add_dependency(self.__load_plugin_id(module))
        dependencies_container.add_dependency(self.__command_runner)
        dependencies_container.add(CommandRegistryManager, self.__command_registry_manager)
        return dependencies_container

    def __load_plugin_id(self, module) -> CommandIdHolder:
        plugin_id = self.__plugins_service.load_plugin(self.__load_plugin_jar(module),
                                                       module.PLUGIN_ENTRY_POINT_CLASS_NAME)
        return CommandIdHolder(plugin_id)

    def __load_plugin_jar(self, module):
        path = self.__get_plugin_jar_path(module)
        return path.read_bytes()

    @staticmethod
    def __get_plugin_jar_path(module) -> Path:
        if hasattr(module, 'jar_path'):
            return Path(module.jar_path)
        return Path(module.__file__).parent / "resources" / "plugin.jar"

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'PluginsModule':
        return PluginsModule(dependency_container.get(PluginsService), dependency_container.get(CommandRunner),
                             dependency_container.get(CommandRegistryManager))
