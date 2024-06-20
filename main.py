from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

if __name__ == '__main__':
    with Matilda().run_in_java_process(java_path='/home/user/Downloads/jre1.8.0_411/bin/java') as matilda_process:
        integer_class = matilda_process.java.find_class("java.lang.Integer")
        integer_object = integer_class.get_method("valueOf", JavaPrimitiveType.INT).invoke_static(12)
        print(integer_class.get_field("value").get(integer_object))
        print(matilda_process.java.find_class("java.util.ArrayList").get_fields())
        print(matilda_process.java.find_class("java.util.ArrayList").superclass)
