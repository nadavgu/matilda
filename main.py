from matilda.matilda import Matilda

if __name__ == '__main__':
    with Matilda().run_in_java_process() as matilda_process:
        print(matilda_process.services.math_service.square(3))
        print(matilda_process.services.math_service.square(4))
        print(matilda_process.services.math_service.sum(3, 4))
        print(matilda_process.services.math_service.multi_sum([3, 4, 5]))
        print(matilda_process.services.math_service.factorize(120))
        matilda_process.services.math_service.div(3, 0)
