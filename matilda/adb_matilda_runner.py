from subprocess import Popen, PIPE
from typing import List

from matilda.adb.adb_device import AdbDevice
from matilda.environment.adb_filesystem import AdbFilesystem
from matilda.environment.local_filesystem import LocalFilesystem
from matilda.platform.matilda_platform import MatildaPlatform

from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.popen_matilda_connection import PopenMatildaConnection


class AdbMatildaRunner:
    def __init__(self, adb_device: AdbDevice):
        self.__adb_device = adb_device

    def run(self, command: str, platform: MatildaPlatform) -> MatildaAgentEnvironment:
        popen = self.__adb_device.shell.run_async(command)
        return MatildaAgentEnvironment(
            connection=PopenMatildaConnection.create(popen),
            platform=platform,
            filesystem=AdbFilesystem(self.__adb_device),
        )
