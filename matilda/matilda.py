from threading import Thread
from typing import Optional

from maddie.dependency_container import DependencyContainer

from matilda.adb_executable_matilda_runner import AdbExecutableMatildaRunner
from matilda.adb_java_process_matilda_runner import AdbJavaProcessMatildaRunner
from matilda.di.dependency_providers import add_dependency_providers
from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.executable_matilda_runner import ExecutableMatildaRunner
from matilda.java_process_matilda_runner import JavaProcessMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.platform.architecture import Architecture
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.matilda_process import MatildaProcess
from matilda.matilda_runner import MatildaRunner
from matilda.messages.message_server import MessageServer


class Matilda:
    def run(self, runner: MatildaRunner) -> MatildaProcess:
        platform = runner.platform()
        connection = runner.run()
        return self.__create_matilda_process(connection, platform)

    def run_in_java_process(self, java_path='java') -> MatildaProcess:
        return self.run(JavaProcessMatildaRunner(java_path=java_path))

    def run_in_native_process(self, wait_for_debugger: bool = False) -> MatildaProcess:
        return self.run(ExecutableMatildaRunner(wait_for_debugger))

    def run_in_android_java_process(self) -> MatildaProcess:
        return self.run(AdbJavaProcessMatildaRunner())

    def run_in_android_native_process(self, architecture: Optional[Architecture] = None) -> MatildaProcess:
        return self.run(AdbExecutableMatildaRunner(architecture))

    @staticmethod
    def __create_matilda_process(connection: MatildaConnection, platform: MatildaPlatform) -> MatildaProcess:
        dependency_container = Matilda.__create_dependency_container(connection, platform)
        Matilda.__start_message_server(dependency_container.get(MessageServer),
                                       dependency_container.get(DestructionManager),
                                       connection)
        dependency_container.get(DestructionManager).add_destructor(connection.close)

        try:
            return dependency_container.get(MatildaProcess)
        except:
            dependency_container.get(DestructionManager).destruct()
            raise

    @staticmethod
    def __create_dependency_container(connection: MatildaConnection, platform: MatildaPlatform) -> DependencyContainer:
        dependency_container = DependencyContainer()
        dependency_container.add(MatildaConnection, connection)
        dependency_container.add(MatildaPlatform, platform)
        add_dependency_providers(dependency_container)
        return dependency_container

    @staticmethod
    def __start_message_server(message_server: MessageServer, destruction_manager: DestructionManager,
                               connection: MatildaConnection):
        thread = Thread(target=message_server.start)
        thread.start()
        destruction_manager.add_destructor(lambda: Matilda.__stop_message_server(thread, connection))

    @staticmethod
    def __stop_message_server(message_server_thread: Thread, connection: MatildaConnection):
        connection.close()
        message_server_thread.join()
