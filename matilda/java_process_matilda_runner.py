from subprocess import Popen, PIPE

from matilda.exceptions.matilda_java_process_not_started_exception import MatildaJavaProcessNotStartedException
from matilda.matilda_connection import MatildaConnection
from matilda.matilda_runner import MatildaRunner
from matilda.popen_matilda_connection import PopenMatildaConnection
from matilda.resources.resources import get_resource_path


PING_BYTE = 0


class JavaProcessMatildaRunner(MatildaRunner):
    def run(self) -> MatildaConnection:
        popen = Popen(args=["java", "-cp", get_resource_path("agent.jar"), "org.matilda.Main"],
                      stdin=PIPE, stdout=PIPE, stderr=PIPE)
        self.__verify_agent_loaded(popen)
        return PopenMatildaConnection(popen)

    @staticmethod
    def __verify_agent_loaded(popen: Popen):
        ping_byte = popen.stdout.read(1)
        if len(ping_byte) != 1 or ping_byte[0] != PING_BYTE:
            exit_code = popen.wait()
            raise MatildaJavaProcessNotStartedException(exit_code, popen.stderr.read())
