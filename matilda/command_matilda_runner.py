from subprocess import Popen, PIPE
from typing import List

from matilda.matilda_connection import MatildaConnection
from matilda.popen_matilda_connection import PopenMatildaConnection


class CommandMatildaRunner:
    def __init__(self, args: List[str]):
        self.__args = args

    def run(self) -> MatildaConnection:
        popen = Popen(args=self.__args,
                      stdin=PIPE, stdout=PIPE, stderr=PIPE)
        return PopenMatildaConnection.create(popen)
