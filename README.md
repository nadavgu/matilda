# matilda
Matilda is a python library that gives an infrastructure for a dynamic java-debugger (in the likes of frida). By loading the java library here and connecting the loaded library and the python library using a couple of I/O streams, the python side can debug the remote JVM


## Running matilda
Using matilda's functionality requires running it's java agent and connecting it to the python client.
Matilda's python library supplies you some basic ways to run the agent, but you can choose to run the 
agent yourself in some other way (for example - injecting the agent to a process of your choosing).

### Running the agent in a new java process
By calling `Matilda.new_java_process`, one can run the matilda agent in a new isolated process.
The function returns a `MatildaProcess` object that can be used to interact with the new process.

When you finish using the `MatildaProcess`, call its `close` function, or  simply use it
in a context manager

```python
from matilda.matilda import Matilda

with Matilda().run_in_java_process() as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
```

### Running the agent in a custom way
If you want to run the matilda agent in some other way (for example, inject it to a process, in order to debug it),
you can implement the `MatildaRunner` interface.

In your `MatildaRunner`'s `run` method, you should load the matilda java library to your chosen location, 
create a `MatildaAgent` object, and call its `run` method.

The `MatildaAgent`'s constructor receives:
- A `MatildaConnection` object, which contains an `InputStream` and an `OutputStream` to communicate with the python side
- Zero or more `Logger` instances that can be used by the agent

`MatildaAgent`'s `run` function is blocking until the connection to the python side is severed.

From your python `MatildaRunner`'s `run` function, you should return a `MatildaConnection` object, that contains input &
output streams connected to the python side.

Once you have created your matilda runner, you can pass it to `Matilda`:
```python
from matilda.matilda import Matilda
from matilda.java_process_matilda_runner import JavaProcessMatildaRunner

with Matilda().run(JavaProcessMatildaRunner()) as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
```


## Using matilda
Matilda supplies several function that can be used to debug the remote process

### Finding classes
use the `find_class` function to locate classes in the remote jvm. This function returns a `JavaClass` object
```python
from matilda.matilda import Matilda
from matilda.java_process_matilda_runner import JavaProcessMatildaRunner

with Matilda().run(JavaProcessMatildaRunner()) as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
    print(integer_class.name) # java.lang.Integer
    print(integer_class.superclass) # java.lang.Number
    print(integer_class.interfaces) # [JavaClass(java.lang.Comparable)]
```

### Using methods

`JavaClass`'s `get_method` function allows you to get a `JavaMethod` corresponding to a method of that class,
and invoke this method.

The `get_method` receives the method name and the types of the parameters, which might be `JavaClass`es,
or values of the `JavaPrimitiveType` enum (representing each of java's primitive types)

Then, one can invoke the method using `invoke_static` for static methods, or `invoke` for instance methods 
(here you also need to pass the instance)

``` python 
from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

with Matilda().run_in_java_process() as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
    integer_object = integer_class.get_method("valueOf", JavaPrimitiveType.INT).invoke_static(12)
    print(integer_object.get_class())
```

the `invoke` functions receive & return either primitives (integers, floats, booleans), or `JavaObject`s for 
non-primitives.

You can also use `JavaClass`'s `get_methods` to get all the class' methods.


### Using fields
Similarly to using java methods, one can use java fields.
The `get_field` function returns a `JavaField` object representing the field.
(and the `get_fields` function returns a list of all the fields in the class)

On a `JavaField` object, you can call `get` (or `get_static` for static fields) to get the value of the field.
These functions return primitive value or a `JavaObject`.

Similarly, you can call `set` or `set_static` to set the field's value

``` python 
from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

with Matilda().run_in_java_process() as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
    print(integer_class.get_field("value").get(integer_object))
```


### Using constructors

Likewise, you can also use java constructors.
`JavaClass`'s `get_constructor` function allows you to get a `JavaConstructor` corresponding to a constructor of that
class. The function receives the types of the parameters of the constructor.
You can also use `JavaClass`'s `get_constructors` to get all the class' constructors.

Then, one can invoke the constructor using `new_instance`, which receives the constructor's arguments
and returns a new `JavaObject`

``` python 
from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

with Matilda().run_in_java_process() as matilda_process:
    integer_class = matilda_process.java.find_class("java.lang.Integer")
    integer_object = integer_class.get_constructor(JavaPrimitiveType.INT).new_instance(12)
    print(integer_object.get_class())
```

### Creating Proxy objects

Use the `new_proxy_instance` function to create a new dynamic java object that implements a set of java interfaces,
with a custom implementation given by a callback you pass. (Similar to the `java.lang.reflect.Proxy` class in java)

``` python 
from matilda.java.java_primitive_type import JavaPrimitiveType
from matilda.matilda import Matilda

with Matilda().run_in_java_process() as matilda_process:
    runnable_class = matilda_process.java.find_class("java.lang.Runnable")
    
    def handler(method: JavaMethod, args: List[JavaValue]):
        if method.name == 'run':
            print("Running from runnable!")

    proxy = matilda_process.java.new_proxy_instance([runnable_class], handler)
    print(proxy)
    print(proxy.get_class())
    print(proxy.get_class().superclass)
    print(proxy.get_class().interfaces)
```

The callback you pass to `new_proxy_instance` will be called on every method invocation on the new object, and it
receives as arguments the `JavaMethod` corresponding to the called method, and the list of arguments to the method.
