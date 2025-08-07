from typing import Set

from matilda.platform.architecture import Architecture


class ArchitectureNotSupportedByDeviceException(Exception):
    def __init__(self, architecture: Architecture, supported_architectures: Set[Architecture]):
        super().__init__(f"Architecture {architecture} is not supported on device. Supported architectures are: {supported_architectures}")
