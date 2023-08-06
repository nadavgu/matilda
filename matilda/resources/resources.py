import os.path
from typing import IO

RESOURCES_DIR_PATH = os.path.dirname(__file__)


def get_resource_path(name: str) -> str:
    resource_path = os.path.join(RESOURCES_DIR_PATH, name)
    if not os.path.exists(resource_path):
        raise RuntimeError(f"Resource doesn't exist: {resource_path}")
    return resource_path


def open_resource(name: str) -> IO:
    return open(get_resource_path(name), "rb")


def get_resource(name, str) -> bytes:
    with open_resource(name) as f:
        return f.read()
