import platform
from enum import Enum

from matilda.platform.unsupported_architecture_exception import UnsupportedArchitectureException


class Architecture(Enum):
    X86_64 = "x86_64"
    ARM32 = "arm32"
    ARM64 = "arm64"

    @staticmethod
    def of_this_machine() -> 'Architecture':
        if platform.machine() == 'x86_64':
            return Architecture.X86_64
        else:
            raise UnsupportedArchitectureException(platform.machine())
