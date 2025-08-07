import traceback
from subprocess import Popen
from typing import IO, AnyStr, Callable

from matilda.exceptions.matilda_process_not_started_exception import MatildaProcessNotStartedException
from matilda.matilda_connection import MatildaConnection


PING_BYTE = 0


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

    @staticmethod
    def create(popen: Popen):
        PopenMatildaConnection.__verify_agent_loaded(popen)
        return PopenMatildaConnection(popen)

    @staticmethod
    def __verify_agent_loaded(popen: Popen):
        ping_byte = popen.stdout.read(1)
        if len(ping_byte) != 1 or ping_byte[0] != PING_BYTE:
            exit_code = popen.wait()
            raise MatildaProcessNotStartedException(exit_code, popen.stderr.read())
