from matilda.matilda_process import MatildaProcess
from matilda.matilda_runner import MatildaRunner


class Matilda:
    @staticmethod
    def run(runner: MatildaRunner) -> MatildaProcess:
        runner.run()
        return MatildaProcess()
