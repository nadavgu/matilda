from dataclasses import dataclass

from matilda.platform.architecture import Architecture
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.operating_system import OperatingSystem


@dataclass(eq=True, frozen=True)
class NativeMatildaPlatform(MatildaPlatform):
    operating_system: OperatingSystem
    architecture: Architecture

    @staticmethod
    def of_this_machine() -> 'NativeMatildaPlatform':
        return NativeMatildaPlatform(OperatingSystem.of_this_machine(), Architecture.of_this_machine())
