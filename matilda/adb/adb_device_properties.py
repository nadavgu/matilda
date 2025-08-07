import re
from typing import Dict

from matilda.adb.adb_device_shell import AdbDeviceShell


class AdbDeviceProperties:
    def __init__(self, shell: AdbDeviceShell):
        self.__shell = shell

    def get(self, key: str) -> str:
        return self.__shell.run(f"getprop {key}").decode().strip()

    def set(self, key: str, value: str):
        self.__shell.run(f"setprop {key} {value}")

    def get_all(self) -> Dict[str, str]:
        result = self.__shell.run("getprop").decode()

        pattern = re.compile(r'\[(.*?)]: \[(.*?)](?=\n\[|\Z)', re.DOTALL)
        matches = pattern.finditer(result)
        return {match[1]: match[2] for match in matches if match}
