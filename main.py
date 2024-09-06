import time
from typing import List

from template.generated.commands.function_service import FunctionService
from template.generated.commands.math_service import MathService

from matilda.java.java_method import JavaMethod
from matilda.java.java_value import JavaValue
from matilda.matilda import Matilda


class SquareFunction(FunctionService):
    def apply(self, value: int) -> int:
        return value * value


if __name__ == '__main__':
    with Matilda().run_in_java_process(java_path='/home/user/Downloads/jre1.8.0_411/bin/java') as matilda_process:
        runnable_class = matilda_process.java.find_class("java.lang.Runnable")
        thread_class = matilda_process.java.find_class("java.lang.Thread")
        thread_getid_method = thread_class.get_method("getId")

        def handler(method: JavaMethod, args: List[JavaValue]):
            if method.name == 'run':
                print(f"Running in new thread!")
                time.sleep(5)
                print("Finished running in new thread!")
            else:
                print(f"What is this: {method.name}({args})")

        proxy = matilda_process.java.new_proxy_instance([runnable_class], handler)
        print(proxy)
        print(proxy.get_class())
        print(proxy.get_class().superclass)
        print(proxy.get_class().interfaces)
        thread = thread_class.get_constructor(runnable_class).new_instance(proxy)
        print(f"Created thread! new thread id: {thread_getid_method.invoke(thread)}")
        thread_class.get_method("start").invoke(thread)
        print("started new thread")
        thread_class.get_method("join").invoke(thread)
        print("finished waiting for new thread")

        math_service: MathService = matilda_process.plugins.template.math
        print(math_service.sum(3, 4))
        print(math_service.map(SquareFunction(), [1, 2, 3, 4]))
        adder = math_service.create_adder(5)
        print(adder.apply(3))
        print(math_service.map(adder, [1, 2, 3, 4]))
