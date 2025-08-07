from subprocess import Popen, PIPE
from typing import Optional

from matilda.exceptions.shell_command_failed_exception import ShellCommandFailedException


class AdbCommander:
    def __init__(self, serial: Optional[str] = None):
        self.__serial = serial

    def command_async(self, *args: str) -> Popen:
        adb_command_args = ["adb"]
        if self.__serial:
            adb_command_args.append("-s")
            adb_command_args.append(self.__serial)
        adb_command_args += args
        return Popen(args=adb_command_args,
                     stdin=PIPE, stdout=PIPE, stderr=PIPE)

    def command(self, *args: str) -> bytes:
        popen = self.command_async(*args)
        return_code = popen.wait()
        if return_code != 0:
            raise ShellCommandFailedException(' '.join(args), return_code, popen.stderr.read())

        return popen.stdout.read()
