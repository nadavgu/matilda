from template.generated.commands.function_service import FunctionService
from template.generated.commands.math_service import MathService

from matilda.matilda import Matilda


class SquareFunction(FunctionService):
    def apply(self, value: int) -> int:
        return value * value


if __name__ == '__main__':
    with Matilda().run_in_java_process() as matilda_process:
        math_service: MathService = matilda_process.plugins.template.math
        print(math_service.sum(3, 4))
        print(math_service.map(SquareFunction(), [1, 2, 3, 4]))
        adder = math_service.create_adder(5)
        print(adder.apply(3))
        print(math_service.map(adder, [1, 2, 3, 4]))

        java_plugin = matilda_process.plugins.java
        runnable_class = java_plugin.find_class("java.lang.Runnable")
        thread_class = java_plugin.find_class("java.lang.Thread")
        thread_getid_method = thread_class.get_method("getId")
        print(thread_class.get_fields())
