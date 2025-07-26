from subprocess import Popen, PIPE

from matilda.exceptions.matilda_process_not_started_exception import MatildaProcessNotStartedException
from matilda.matilda_connection import MatildaConnection
from matilda.popen_matilda_connection import PopenMatildaConnection

PING_BYTE = 0


class CommandMatildaRunner:
    def __init__(self, *args: str):
        self.__args = args

    def run(self) -> MatildaConnection:
        popen = Popen(args=self.__args,
                      stdin=PIPE, stdout=PIPE, stderr=PIPE)
        self.__verify_agent_loaded(popen)
        return PopenMatildaConnection(popen)

    @staticmethod
    def __verify_agent_loaded(popen: Popen):
        ping_byte = popen.stdout.read(1)
        if len(ping_byte) != 1 or ping_byte[0] != PING_BYTE:
            exit_code = popen.wait()
            raise MatildaProcessNotStartedException(exit_code, popen.stderr.read())
