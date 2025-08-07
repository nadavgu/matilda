from subprocess import Popen

from matilda.adb.adb_commander import AdbCommander


class AdbDeviceShell:
    def __init__(self, commander: AdbCommander):
        self.__commander = commander

    def run_async(self, command: str) -> Popen:
        return self.__commander.command_async("shell", command)

    def run(self, command: str) -> bytes:
        return self.__commander.command("shell", command)
