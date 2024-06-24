from typing import Dict

from matilda.commands.command import Command


class CommandRegistry:
    def __init__(self, commands: Dict[int, Command] = None):
        self.__commands = commands if commands else {}

    def add_command(self, command_type: int, command: Command):
        if command_type in self.__commands:
            raise RuntimeError(f"Command {command_type} already handled by {self.__commands[command_type]}")

        self.__commands[command_type] = command

    def get_command(self, command_type):
        if command_type not in self.__commands:
            raise KeyError(f"command {command_type} not found")

        return self.__commands[command_type]
