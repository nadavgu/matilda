from subprocess import Popen
from typing import IO, AnyStr

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
        self.agent_input.close()
        self.agent_output.close()
        self.__popen.wait()
