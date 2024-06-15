from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

if __name__ == '__main__':
    with Matilda().run_in_java_process(java_path='/home/user/Downloads/jre1.8.0_411/bin/java') as matilda_process:
        print(matilda_process.java.find_class("java.lang.Math").get_method("abs", JavaPrimitiveType.INT).invoke_static(-3))
