from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.platform.operating_system import OperatingSystem
from tests.marks import android_test


class TestPlatforms:
    def test_process_has_correct_platform(self, matilda_process, matilda_platform):
        assert matilda_process.platform == matilda_platform


    @android_test
    def test_correct_architecture_is_chosen_when_not_specified_for_android_device(self, matilda, adb_device):
        with matilda.run_in_android_native_process() as process:
            assert NativeMatildaPlatform(OperatingSystem.ANDROID, adb_device.info.architecture) == process.platform
