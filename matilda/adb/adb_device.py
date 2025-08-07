from typing import Optional

from matilda.adb.adb_commander import AdbCommander
from matilda.adb.adb_device_files import AdbDeviceFiles
from matilda.adb.adb_device_info import AdbDeviceInfo
from matilda.adb.adb_device_properties import AdbDeviceProperties
from matilda.adb.adb_device_shell import AdbDeviceShell


class AdbDevice:
    def __init__(self, serial: Optional[str] = None):
        commander = AdbCommander(serial)
        self.__shell = AdbDeviceShell(commander)
        self.__files = AdbDeviceFiles(commander, self.__shell)
        self.__properties = AdbDeviceProperties(self.__shell)
        self.__info = AdbDeviceInfo(self.__properties)

    @property
    def shell(self):
        return self.__shell

    @property
    def files(self):
        return self.__files

    @property
    def properties(self):
        return self.__properties

    @property
    def info(self):
        return self.__info
