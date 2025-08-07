from typing import Set

from matilda.adb.adb_device_properties import AdbDeviceProperties
from matilda.platform.architecture import Architecture
from matilda.platform.unsupported_architecture_exception import UnsupportedArchitectureException


class AdbDeviceInfo:
    def __init__(self, properties: AdbDeviceProperties):
        self.__properties = properties

    @property
    def architecture(self) -> Architecture:
        return self.__convert_to_architecture(self.__properties.get("ro.product.cpu.abi"))

    @property
    def supported_architectures(self) -> Set[Architecture]:
        arch_list = self.__properties.get("ro.product.cpu.abilist").split(',')
        return {self.__convert_to_architecture(arch_string) for arch_string in arch_list}

    @staticmethod
    def __convert_to_architecture(arch_string: str):
        match arch_string:
            case "arm64-v8a": return Architecture.ARM64
            case "armeabi-v7a": return Architecture.ARM32
            case "armeabi": return Architecture.ARM32

        raise UnsupportedArchitectureException(arch_string)
