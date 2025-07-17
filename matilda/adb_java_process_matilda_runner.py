import os

from matilda.command_matilda_runner import CommandMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.resources.resources import get_resource_path


class AdbJavaProcessMatildaRunner(CommandMatildaRunner):
    __DEVICE_PATH = "/data/local/tmp/.matilda-agent.apk"

    def __init__(self):
        super(AdbJavaProcessMatildaRunner, self).__init__("adb", "shell",
                                                             AdbJavaProcessMatildaRunner.__build_app_process_command())

    def platform(self) -> MatildaPlatform:
        return MatildaPlatform.ANDROID

    def run(self) -> MatildaConnection:
        os.system(f"adb push {get_resource_path('android-agent.apk')} {self.__DEVICE_PATH}")
        return super().run()

    @staticmethod
    def __build_app_process_command() -> str:
        return f"CLASSPATH={AdbJavaProcessMatildaRunner.__DEVICE_PATH} app_process . org.matilda.Main"
