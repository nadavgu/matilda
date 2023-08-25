class JavaObject:
    def __init__(self, object_id: int):
        self.__object_id = object_id

    @property
    def object_id(self) -> int:
        return self.__object_id
