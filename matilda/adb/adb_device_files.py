from tempfile import NamedTemporaryFile

from matilda.adb.adb_commander import AdbCommander
from matilda.adb.adb_device_shell import AdbDeviceShell


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

    def read(self, path: str):
        with NamedTemporaryFile() as temp_file:
            self.pull(path, temp_file.name)
            with open(temp_file.name, 'rb') as f:
                return f.read()
