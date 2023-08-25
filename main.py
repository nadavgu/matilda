from matilda.matilda import Matilda

if __name__ == '__main__':
    with Matilda().run_in_java_process() as matilda_process:
        print(matilda_process.java.find_class("java.lang.Math").get_methods()[0].invoke_static(-3))
