import importlib
import sys
from functools import cached_property
from pathlib import Path
from types import ModuleType
from typing import Dict

from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer

from matilda.commands.command_id_holder import CommandIdHolder
from matilda.commands.command_registry_manager import CommandRegistryManager
from matilda.commands.command_runner import CommandRunner
from matilda.generated.commands.plugins_service import PluginsService
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.supported_platforms import ANDROID, LINUX_X64, JVM, ANDROID_NATIVE_ARM64, ANDROID_NATIVE_ARM32
from matilda.plugins.platform_not_supported_by_plugin_exception import PlatformNotSupportedByPluginException
from matilda.plugins.plugin_entry_point import PluginEntryPoint

if sys.version_info < (3, 10):
    from importlib_metadata import entry_points
else:
    from importlib.metadata import entry_points



DEFAULT_BINARY_NAMES = {
    JVM: "plugin.jar",
    LINUX_X64: "libplugin_linuxX64.so",
    ANDROID_NATIVE_ARM64: "libplugin_androidNativeArm64.so",
    ANDROID_NATIVE_ARM32: "libplugin_androidNativeArm32.so",
    ANDROID: "android-plugin.apk",
}


class PluginsModule(Dependency):
    def __init__(self, plugins_service: PluginsService, command_runner: CommandRunner,
                 command_registry_manager: CommandRegistryManager, platform: MatildaPlatform):
        self.__plugins_service = plugins_service
        self.__command_runner = command_runner
        self.__command_registry_manager = command_registry_manager
        self.__platform = platform

    def __getattr__(self, item):
        entry_point = self.__entry_points[item]
        plugin = self.load_plugin(importlib.import_module(entry_point), item)
        setattr(self, item, plugin)
        return plugin

    @cached_property
    def __entry_points(self) -> Dict[str, str]:
        return {entry_point.name: entry_point.value for entry_point in entry_points(group='matilda.plugins')}

    def load_plugin(self, module: ModuleType, plugin_name: str):
        plugin_dependencies = self.__load_plugin_dependencies(module, plugin_name)
        return module.load_plugin(plugin_dependencies)

    def __load_plugin_dependencies(self, module: ModuleType, plugin_name: str):
        dependencies_container = DependencyContainer()
        dependencies_container.add_dependency(self.__load_plugin_id(module, plugin_name))
        dependencies_container.add_dependency(self.__command_runner)
        dependencies_container.add(CommandRegistryManager, self.__command_registry_manager)
        return dependencies_container

    def __load_plugin_id(self, module: ModuleType, plugin_name: str) -> CommandIdHolder:
        entry_point: PluginEntryPoint = self.__load_plugin_entry_point(module, plugin_name)
        plugin_id = self.__plugins_service.load_plugin(self.__load_plugin_binary(module, entry_point),
                                                       entry_point.entry_point_symbol)
        return CommandIdHolder(plugin_id)

    def __load_plugin_entry_point(self, module: ModuleType, plugin_name: str) -> PluginEntryPoint:
        plugin_entry_points: Dict[MatildaPlatform, PluginEntryPoint] = module.PLUGIN_ENTRY_POINTS
        if self.__platform not in plugin_entry_points:
            raise PlatformNotSupportedByPluginException(self.__platform, plugin_name)
        return plugin_entry_points[self.__platform]

    def __load_plugin_binary(self, module: ModuleType, entry_point: PluginEntryPoint):
        path = self.__get_plugin_binary_path(module, entry_point)
        return path.read_bytes()

    def __get_plugin_binary_path(self, module: ModuleType, entry_point: PluginEntryPoint) -> Path:
        if entry_point.binary_path:
            return Path(entry_point.binary_path)
        return Path(module.__file__).parent / "resources" / self.__default_binary_name

    @property
    def __default_binary_name(self):
        return DEFAULT_BINARY_NAMES[self.__platform]

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'PluginsModule':
        return PluginsModule(dependency_container.get(PluginsService), dependency_container.get(CommandRunner),
                             dependency_container.get(CommandRegistryManager),
                             dependency_container.get(MatildaPlatform))
