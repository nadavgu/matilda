from matilda.adb.adb_device import AdbDevice
from matilda.environment.filesystem import Filesystem


class AdbFilesystem(Filesystem):
    def __init__(self, adb_device: AdbDevice):
        self.__adb_device = adb_device

    def read(self, path: str) -> bytes:
        return self.__adb_device.files.read(path)
