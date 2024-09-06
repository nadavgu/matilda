# Matilda RPC

matilda's RPC infrastructure provides an API to call java functions (or "commands") from the python part of a plugin 
(or even matilda's core itself), and vice versa.

It does so by using java annotation processing, and the Dagger DI framework.


## Defining java services

A class marked with the `@MatildaService` annotation can provide commands from the java side to the python side. 
Each function in the service marked with the `@MatildaCommand` annotation is treated as a command that
can be called from the python side.

```java
@MatildaService
public class MathService {
    @MatildaCommand
    public int square(int number) {
        return number * number;
    }

    @MatildaCommand
    public int sum(int first, int second) {
        return first + second;
    }

    @MatildaCommand
    public int div(int first, int second) {
        return first / second;
    }

    @MatildaCommand
    public int multiSum(List<Integer> values) {
        return values.stream().reduce(0, this::sum);
    }
}
```

commands can receive & return values of several types:
- primitive types
- `String`
- `ByteString`
- `byte` arrays
- `List`s of other supported types
- `void` (for return values)
- boxed types (`Integer`, `Float`, ...)
- protobuf objects
- [Dynamic services](#dynamic-services)


Services can receive dependencies using the `Dagger` DI framework (by `@Inject`ing dependencies).


## Using services
The annotation processor generates python classes per service that can be used in the python side of a plugin 
(or matilda's core).
Each service has a corresponding generated python class that contains matching functions. To use the service, one can 
simply use the `Maddie` DI framework. Given the plugin's `DependencyContainer` object, the service can be obtained by
calling the `get` function, with the generated service class.

```python
math_service = dependency_container.get(MathService)
print(math_service.square(3))
```


## Dynamic services
Dynamic services allow matilda commands to pass callbacks between java & python.

An interface marked with `@MatildaDynamicService` can be passed to and returned from commands.
A dynamic instance of the interface can be passed to commands each time, to allow passsing callbacks.

```java
@MatildaDynamicService
public interface FunctionService {
    @MatildaCommand
    int apply(int value);
}
```

Each function in the dynamic service marked with `@MatildaCommand` can be called from both sides.

```java
@MatildaCommand
public List<Integer> map(FunctionService function, List<Integer> values) {
    return values.stream().map(function::apply).collect(Collectors.toList());
}

@MatildaCommand
public FunctionService createAdder(int amount) {
    return (value) -> value + amount;
}
```

An equivalent class for each dynamic service is generated in the python side. This class can be implemented / used.

```python
class SquareFunction(FunctionService):
    def apply(self, value: int) -> int:
        return value * value

print(math_service.map(SquareFunction(), [1, 2, 3, 4]))  # [1, 4, 9, 16]
adder = math_service.create_adder(5)
print(adder.apply(3))  # 8
print(math_service.map(adder, [1, 2, 3, 4]))  # [5, 6, 7, 8]
```