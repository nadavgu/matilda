from dataclasses import dataclass

from matilda.platform.matilda_platform import MatildaPlatform

from matilda.environment.filesystem import Filesystem
from matilda.matilda_connection import MatildaConnection


@dataclass
class MatildaAgentEnvironment:
    connection: MatildaConnection
    platform: MatildaPlatform
    filesystem: Filesystem
