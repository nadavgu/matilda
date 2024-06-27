import time
from typing import List

from matilda.generated.commands.function import Function
from matilda.java.java_method import JavaMethod
from matilda.java.java_value import JavaValue
from matilda.matilda import Matilda


class MyFunction(Function):
    def apply(self, value: float) -> float:
        print(value)
        return value * 2


def handler(method: JavaMethod, args: List[JavaValue]):
    if method.name == 'run':
        print(f"Running! {args}")
        time.sleep(5)
        print("Finished running!")
    else:
        print(f"What is this: {method.name}({args})")


if __name__ == '__main__':
    with Matilda().run_in_java_process(java_path='/home/user/Downloads/jre1.8.0_411/bin/java') as matilda_process:
        runnable_class = matilda_process.java.find_class("java.lang.Runnable")
        proxy = (matilda_process.java.new_proxy_instance([matilda_process.java.find_class("java.lang.Runnable")],
                                                         handler))

