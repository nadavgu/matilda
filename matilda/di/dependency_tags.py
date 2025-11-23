from enum import Enum


class DependencyTags(Enum):
    AGENT_INPUT = "agent_input"
    AGENT_OUTPUT = "agent_output"
    AGENT_ENVIRONMENT_DEPENDENCY_CONTAINER = "agent_environment_dependency_container"
