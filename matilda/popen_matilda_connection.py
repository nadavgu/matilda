import traceback
from subprocess import Popen
from typing import IO, AnyStr, Callable

from matilda.matilda_connection import MatildaConnection


class PopenMatildaConnection(MatildaConnection):
    def __init__(self, popen: Popen):
        self.__popen = popen

    @property
    def agent_input(self) -> IO[AnyStr]:
        return self.__popen.stdin

    @property
    def agent_output(self) -> IO[AnyStr]:
        return self.__popen.stdout

    def close(self):
        self.__safe_run(self.agent_input.close)
        self.__safe_run(self.agent_output.close)
        self.__safe_run(self.__wait_for_process)

    @staticmethod
    def __safe_run(runnable: Callable[[], None]):
        try:
            runnable()
        except Exception as e:
            traceback.print_exception(e)

    def __wait_for_process(self):
        return_code = self.__popen.wait()
        if return_code != 0:
            print(self.__popen.stderr.read().decode())
