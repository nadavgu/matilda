from subprocess import Popen, PIPE
from typing import List

from matilda.environment.local_filesystem import LocalFilesystem
from matilda.platform.matilda_platform import MatildaPlatform

from matilda.environment.matilda_environment import MatildaAgentEnvironment
from matilda.popen_matilda_connection import PopenMatildaConnection


class LocalMatildaRunner:
    def __init__(self, args: List[str], platform: MatildaPlatform):
        self.__args = args
        self.__platform = platform

    def run(self) -> MatildaAgentEnvironment:
        popen = Popen(args=self.__args,
                      stdin=PIPE, stdout=PIPE, stderr=PIPE)
        return MatildaAgentEnvironment(
            connection=PopenMatildaConnection.create(popen),
            platform=self.__platform,
            filesystem=LocalFilesystem(),
        )
