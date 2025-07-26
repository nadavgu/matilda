from typing import Generator, Optional

import pytest
import tests.java_plugin
import tests.plugin
from _pytest.fixtures import SubRequest
from _pytest.config import Config

from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.supported_platforms import JVM, LINUX_X64, ANDROID
from tests.plugin import TestPlugin
from tests.plugin_type import PluginType


def pytest_addoption(parser):
    parser.addoption("--test-on-connected-android-device", action="store_true", default=False)


@pytest.fixture(scope='session')
def run_on_connected_android_device(pytestconfig: Config) -> bool:
    return pytestconfig.getoption("--test-on-connected-android-device")


@pytest.fixture(scope='session')
def matilda() -> Matilda:
    return Matilda()


@pytest.fixture(params = [
    JVM,
    LINUX_X64,
    ANDROID,
], ids=[
    "JVM",
    "LINUX_X64",
    "ANDROID"
], scope='session')
def matilda_platform(request: SubRequest, run_on_connected_android_device: bool) -> MatildaPlatform:
    platform: MatildaPlatform = request.param
    if platform == ANDROID and not run_on_connected_android_device:
        pytest.skip("Not running tests on android in this run - to run pass the option --test-on-connected-android-device")
    return platform


@pytest.fixture(scope='session')
def matilda_java_process(matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    with matilda.run_in_java_process() as process:
        yield process


@pytest.fixture(scope='session')
def matilda_native_process(matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    with matilda.run_in_native_process() as process:
        yield process


@pytest.fixture(scope='session')
def matilda_android_process(matilda: Matilda, run_on_connected_android_device: bool) -> Generator[Optional[MatildaProcess], None, None]:
    if run_on_connected_android_device:
        with matilda.run_in_android_java_process() as process:
            yield process
    else:
        yield None


@pytest.fixture(scope='session')
def matilda_process(matilda_platform: MatildaPlatform, matilda_java_process: MatildaProcess,
                    matilda_native_process: MatildaProcess, matilda_android_process: MatildaProcess) -> MatildaProcess:
    if matilda_platform == JVM:
        return matilda_java_process
    elif matilda_platform == ANDROID:
        return matilda_android_process
    elif matilda_platform == LINUX_X64:
        return matilda_native_process
    else:
        raise ValueError(matilda_platform)


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
        if matilda_platform is LINUX_X64:
            pytest.skip("java plugin not supported on native platform")
        return matilda_process.plugins.load_plugin(tests.java_plugin, "test")
