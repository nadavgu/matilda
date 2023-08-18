from functools import cached_property

from matilda.generated.commands.reflection_service import ReflectionService


class JavaClass:
    def __init__(self, reflection_service: ReflectionService, object_id: int):
        self.__reflection_service = reflection_service
        self.__object_id = object_id

    @cached_property
    def name(self) -> str:
        return self.__reflection_service.get_class_name(self.__object_id)

    def __str__(self):
        return self.name

    def __repr__(self):
        return f"JavaClass({self.name})"

