from matilda.adb.adb_device import AdbDevice
from matilda.environment.filesystem import Filesystem


class LocalFilesystem(Filesystem):
    def read(self, path: str) -> bytes:
        with open(path, 'rb') as f:
            return f.read()
