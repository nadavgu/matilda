import platform
from enum import Enum

from matilda.platform.unsupported_os_exception import UnsupportedOSException


class OperatingSystem(Enum):
    LINUX = "linux"
    ANDROID = "android"

    @staticmethod
    def of_this_machine() -> 'OperatingSystem':
        if platform.system() == 'Linux':
            return OperatingSystem.LINUX
        else:
            raise UnsupportedOSException(platform.system())
