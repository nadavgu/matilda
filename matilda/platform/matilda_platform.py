import platform
from enum import Enum

from matilda.platform.unsupported_architecture_exception import UnsupportedArchitectureException
from matilda.platform.unsupported_os_exception import UnsupportedOSException


class MatildaPlatform(Enum):
    JVM = "jvm"
    LINUX_X64 = "linuxX64"


def get_native_platform_of_this_machine():
    if platform.system() == 'Linux':
        if platform.machine() == 'x86_64':
            return MatildaPlatform.LINUX_X64
        else:
            raise UnsupportedArchitectureException(platform.machine())
    else:
        raise UnsupportedOSException(platform.system())
