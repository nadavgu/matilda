from typing import Generator

import pytest
import tests
from _pytest.fixtures import SubRequest

from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.matilda_platform import MatildaPlatform
from tests.plugin import TestPlugin


@pytest.fixture(scope='session')
def matilda() -> Matilda:
    return Matilda()


@pytest.fixture(params = [
    MatildaPlatform.JVM,
    MatildaPlatform.LINUX_X64,
], scope='session')
def matilda_platform(request: SubRequest) -> MatildaPlatform:
    return request.param


@pytest.fixture(scope='session')
def matilda_java_process(matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    with matilda.run_in_java_process() as process:
        yield process


@pytest.fixture(scope='session')
def matilda_native_process(matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    with matilda.run_in_native_process() as process:
        yield process


@pytest.fixture(scope='session')
def matilda_process(matilda_platform: MatildaPlatform, matilda_java_process: MatildaProcess,
                    matilda_native_process: MatildaProcess) -> MatildaProcess:
    if matilda_platform == MatildaPlatform.JVM:
        return matilda_java_process
    else:
        return matilda_native_process


@pytest.fixture(scope='session')
def plugin(matilda_process: MatildaProcess) -> TestPlugin:
    return matilda_process.plugins.load_plugin(tests.plugin, "test")
