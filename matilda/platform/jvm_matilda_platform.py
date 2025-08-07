from dataclasses import dataclass

from matilda.platform.jvm_system import JvmSystem
from matilda.platform.matilda_platform import MatildaPlatform


@dataclass(eq=True, frozen=True)
class JvmMatildaPlatform(MatildaPlatform):
    system: JvmSystem

    def is_android(self) -> bool:
        return self.system == JvmSystem.ANDROID
