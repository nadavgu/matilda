import os.path
from typing import IO

from matilda.platform.architecture import Architecture
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.platform.operating_system import OperatingSystem

RESOURCES_DIR_PATH = os.path.dirname(__file__)


def get_resource_path(name: str) -> str:
    resource_path = os.path.join(RESOURCES_DIR_PATH, name)
    if not os.path.exists(resource_path):
        raise RuntimeError(f"Resource doesn't exist: {resource_path}")
    return resource_path


def open_resource(name: str) -> IO:
    return open(get_resource_path(name), "rb")


def get_resource(name: str) -> bytes:
    with open_resource(name) as f:
        return f.read()


def get_executable_path(platform: NativeMatildaPlatform):
    architecture_part = __get_executable_architecture_part(platform.architecture)
    os_part = __get_executable_os_part(platform.operating_system)
    return get_resource_path(f"agent-{os_part}{architecture_part}.kexe")

def __get_executable_architecture_part(architecture: Architecture) -> str:
    match architecture:
        case Architecture.X86_64: return "X64"
        case Architecture.ARM32: return "Arm32"
        case Architecture.ARM64: return "Arm64"
    raise ValueError(architecture)

def __get_executable_os_part(operating_system: OperatingSystem) -> str:
    match operating_system:
        case OperatingSystem.LINUX: return "linux"
        case OperatingSystem.ANDROID: return "androidNative"
    raise ValueError(operating_system)
