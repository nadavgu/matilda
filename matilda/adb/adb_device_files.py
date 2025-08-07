from subprocess import Popen

from matilda.adb.adb_commander import AdbCommander
from matilda.adb.adb_device_shell import AdbDeviceShell
from matilda.exceptions.shell_command_failed_exception import ShellCommandFailedException


class AdbDeviceFiles:
    def __init__(self, commander: AdbCommander, shell: AdbDeviceShell):
        self.__commander = commander
        self.__shell = shell

    def push(self, source: str, dest: str):
        self.__commander.command("push", source, dest)

    def pull(self, source: str, dest: str):
        self.__commander.command("pull", source, dest)

    def chmod(self, path: str, mode: int):
        self.__shell.run(f"chmod {oct(mode)[2:]} {path}")
