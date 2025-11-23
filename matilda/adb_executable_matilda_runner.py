from functools import cached_property
from typing import Optional

from matilda.adb.adb_device import AdbDevice
from matilda.adb_matilda_runner import AdbMatildaRunner
from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.exceptions.architecture_not_supported_by_device_exception import ArchitectureNotSupportedByDeviceException
from matilda.matilda_runner import MatildaRunner
from matilda.platform.architecture import Architecture
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.platform.operating_system import OperatingSystem
from matilda.resources.resources import get_executable_path


class AdbExecutableMatildaRunner(MatildaRunner):
    def __init__(self, architecture: Optional[Architecture] = None):
        self.__architecture = architecture
        self.__adb_device = AdbDevice()
        self.__adb_runner = AdbMatildaRunner(self.__adb_device)

    def run(self) -> MatildaAgentEnvironment:
        self.__adb_device.files.push(get_executable_path(self.__selected_platform), self.__device_path)
        self.__adb_device.files.chmod(self.__device_path, 0o777)
        return self.__adb_runner.run(self.__device_path, self.__selected_platform)

    @property
    def __device_path(self) -> str:
        return f"/data/local/tmp/.matilda-agent-{self.__selected_architecture.name}"

    @property
    def __selected_platform(self):
        return NativeMatildaPlatform(OperatingSystem.ANDROID, self.__selected_architecture)

    @cached_property
    def __selected_architecture(self) -> Architecture:
        if self.__architecture:
            if self.__architecture not in self.__adb_device.info.supported_architectures:
                raise ArchitectureNotSupportedByDeviceException(self.__architecture,
                                                                self.__adb_device.info.supported_architectures)
            return self.__architecture
        else:
            return self.__adb_device.info.architecture
