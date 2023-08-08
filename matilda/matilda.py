from maddie.dependency_container import DependencyContainer

from matilda.di.dependency_providers import add_dependency_providers
from matilda.java_process_matilda_runner import JavaProcessMatildaRunner
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_process import MatildaProcess
from matilda.matilda_runner import MatildaRunner


class Matilda:
    def run(self, runner: MatildaRunner) -> MatildaProcess:
        connection = runner.run()
        return self.__create_matilda_process(connection)

    def run_in_java_process(self) -> MatildaProcess:
        return self.run(JavaProcessMatildaRunner())

    @staticmethod
    def __create_matilda_process(connection: MatildaConnection) -> MatildaProcess:
        dependency_container = DependencyContainer()
        dependency_container.add(MatildaConnection, connection)
        add_dependency_providers(dependency_container)
        return dependency_container.get(MatildaProcess)

