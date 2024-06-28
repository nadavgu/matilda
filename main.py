import time
from typing import List

from matilda.java.java_method import JavaMethod
from matilda.java.java_value import JavaValue
from matilda.matilda import Matilda


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

        proxy = (matilda_process.java.new_proxy_instance([runnable_class], handler))
        thread = thread_class.get_constructor(runnable_class).new_instance(proxy)
        print(f"Created thread! new thread id: {thread_getid_method.invoke(thread)}")
        thread_class.get_method("start").invoke(thread)
        print("started new thread")
        thread_class.get_method("join").invoke(thread)
        print("finished waiting for new thread")
