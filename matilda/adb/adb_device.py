from typing import Optional

from matilda.adb.adb_commander import AdbCommander
from matilda.adb.adb_device_files import AdbDeviceFiles
from matilda.adb.adb_device_shell import AdbDeviceShell


class AdbDevice:
    def __init__(self, serial: Optional[str] = None):
        commander = AdbCommander(serial)
        self.__shell = AdbDeviceShell(commander)
        self.__files = AdbDeviceFiles(commander, self.__shell)

    @property
    def shell(self):
        return self.__shell

    @property
    def files(self):
        return self.__files
