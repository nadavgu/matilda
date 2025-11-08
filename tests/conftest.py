from typing import Generator, Optional

import pytest
import tests.java_plugin
import tests.plugin
from _pytest.fixtures import SubRequest
from _pytest.config import Config

from matilda.adb.adb_device import AdbDevice
from matilda.exceptions.architecture_not_supported_by_device_exception import ArchitectureNotSupportedByDeviceException
from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.architecture import Architecture
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.platform.supported_platforms import JVM, LINUX_X64, ANDROID, ANDROID_NATIVE_ARM64, ANDROID_NATIVE_ARM32
from tests.plugin import TestPlugin
from tests.plugin_type import PluginType


def pytest_addoption(parser):
    parser.addoption("--test-on-connected-android-device", action="store_true", default=False)


@pytest.fixture(scope='session')
def run_on_connected_android_device(pytestconfig: Config) -> bool:
    return pytestconfig.getoption("--test-on-connected-android-device")


@pytest.fixture(scope='session')
def adb_device() -> AdbDevice:
    return AdbDevice()


@pytest.fixture(scope='session')
def matilda() -> Matilda:
    return Matilda()


@pytest.fixture(params = [
    JVM,
    LINUX_X64,
    ANDROID,
    ANDROID_NATIVE_ARM64,
    ANDROID_NATIVE_ARM32,
], ids=[
    "JVM",
    "LINUX_X64",
    "ANDROID",
    "ANDROID_NATIVE_ARM64",
    "ANDROID_NATIVE_ARM32",
], scope='session')
def matilda_platform(request: SubRequest, run_on_connected_android_device: bool) -> MatildaPlatform:
    platform: MatildaPlatform = request.param
    if platform.is_android() and not run_on_connected_android_device:
        __skip_android_test()
    return platform


@pytest.fixture(scope='session')
def matilda_native_process(matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    with matilda.run_in_native_process() as process:
        yield process


@pytest.fixture(scope='session')
def matilda_process(matilda: Matilda, matilda_platform: MatildaPlatform) -> Generator[MatildaProcess, None, None]:
    if matilda_platform == JVM:
        process = matilda.run_in_java_process()
    elif matilda_platform == ANDROID:
        process = matilda.run_in_android_java_process()
    elif matilda_platform == LINUX_X64:
        process = matilda.run_in_native_process()
    elif matilda_platform == ANDROID_NATIVE_ARM64:
        process = __run_in_android_native_process(matilda, Architecture.ARM64)
    elif matilda_platform == ANDROID_NATIVE_ARM32:
        process = __run_in_android_native_process(matilda, Architecture.ARM32)
    else:
        raise ValueError(matilda_platform)

    with process:
        yield process


def __run_in_android_native_process(matilda: Matilda, architecture: Architecture) -> MatildaProcess:
    try:
        return matilda.run_in_android_native_process(architecture=architecture)
    except ArchitectureNotSupportedByDeviceException as e:
        return pytest.skip(str(e))


@pytest.fixture(params = [
    PluginType.KMP,
    PluginType.JAVA,
], scope='session')
def plugin_type(request: SubRequest) -> PluginType:
    return request.param


@pytest.fixture(scope='session')
def plugin(plugin_type: PluginType, matilda_process: MatildaProcess, matilda_platform: MatildaPlatform) -> TestPlugin:
    if plugin_type is PluginType.KMP:
        return matilda_process.plugins.load_plugin(tests.plugin, "test")
    else:
        if isinstance(matilda_platform, NativeMatildaPlatform):
            pytest.skip("java plugin not supported on native platform")
        return matilda_process.plugins.load_plugin(tests.java_plugin, "test")


@pytest.fixture(autouse=True)
def skip_android_tests_if_should(request: SubRequest, run_on_connected_android_device: bool):
    if list(request.node.iter_markers("android_test")) and not run_on_connected_android_device:
        __skip_android_test()


def __skip_android_test():
    pytest.skip("Not running tests on android in this run - to run pass the option --test-on-connected-android-device")