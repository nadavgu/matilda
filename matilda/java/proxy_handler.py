from typing import Callable, List

from matilda.java.java_method import JavaMethod
from matilda.java.java_value import JavaValue, OptionalJavaValue

ProxyHandler = Callable[[JavaMethod, List[JavaValue]], OptionalJavaValue]
