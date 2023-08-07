from matilda.java_process_matilda_runner import JavaProcessMatildaRunner
from matilda.matilda_process import MatildaProcess
from matilda.matilda_runner import MatildaRunner


class Matilda:
    @staticmethod
    def run(runner: MatildaRunner) -> MatildaProcess:
        runner.run()
        return MatildaProcess()

    def run_in_java_process(self) -> MatildaProcess:
        return self.run(JavaProcessMatildaRunner())
