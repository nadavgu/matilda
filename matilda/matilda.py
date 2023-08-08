from threading import Thread

from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_providers import add_dependency_providers
from matilda.di.destructors.destruction_manager import DestructionManager
from matilda.java_process_matilda_runner import JavaProcessMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_process import MatildaProcess
from matilda.matilda_runner import MatildaRunner
from matilda.messages.message_listener import MessageListener


class Matilda:
    def run(self, runner: MatildaRunner) -> MatildaProcess:
        connection = runner.run()
        return self.__create_matilda_process(connection)

    def run_in_java_process(self) -> MatildaProcess:
        return self.run(JavaProcessMatildaRunner())

    @staticmethod
    def __create_matilda_process(connection: MatildaConnection) -> MatildaProcess:
        dependency_container = Matilda.__create_dependency_container(connection)
        Matilda.__start_message_listener(dependency_container.get(MessageListener),
                                         dependency_container.get(DestructionManager))
        dependency_container.get(DestructionManager).add_destructor(connection.close)

        return dependency_container.get(MatildaProcess)

    @staticmethod
    def __create_dependency_container(connection: MatildaConnection) -> DependencyContainer:
        dependency_container = DependencyContainer()
        dependency_container.add(MatildaConnection, connection)
        add_dependency_providers(dependency_container)
        return dependency_container

    @staticmethod
    def __start_message_listener(message_listener: MessageListener, destruction_manager: DestructionManager):
        thread = Thread(target=message_listener.start)
        thread.start()
        destruction_manager.add_destructor(thread.join)
