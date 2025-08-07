from typing import Optional

from matilda.adb.adb_commander import AdbCommander
from matilda.adb.adb_device_shell import AdbDeviceShell


class AdbDevice:
    def __init__(self, serial: Optional[str] = None):
        commander = AdbCommander(serial)
        self.__shell = AdbDeviceShell(commander)

    @property
    def shell(self):
        return self.__shell
