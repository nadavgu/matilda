from matilda.adb.adb_device import AdbDevice
from matilda.adb_matilda_runner import AdbMatildaRunner
from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.matilda_runner import MatildaRunner
from matilda.platform.supported_platforms import ANDROID
from matilda.resources.resources import get_resource_path


class AdbJavaProcessMatildaRunner(MatildaRunner):
    __DEVICE_PATH = "/data/local/tmp/.matilda-agent.apk"

    def __init__(self):
        self.__adb_device = AdbDevice()
        self.__adb_runner = AdbMatildaRunner(self.__adb_device)

    def run(self) -> MatildaAgentEnvironment:
        self.__adb_device.files.push(get_resource_path('android-agent.apk'), self.__DEVICE_PATH)
        return self.__adb_runner.run(self.__build_app_process_command(), ANDROID)

    @staticmethod
    def __build_app_process_command() -> str:
        return f"CLASSPATH={AdbJavaProcessMatildaRunner.__DEVICE_PATH} app_process . org.matilda.Main"
