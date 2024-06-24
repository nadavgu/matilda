import random

from maddie.dependency import Dependency, T
from maddie.dependency_container import DependencyContainer


class CommandRegistryIdGenerator(Dependency):
    @staticmethod
    def generate() -> int:
        return int.from_bytes(random.randbytes(4), byteorder='little', signed=True)

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'CommandRegistryIdGenerator':
        return CommandRegistryIdGenerator()


"""
package org.matilda.commands;

import javax.inject.Inject;
import java.util.Random;

public class CommandRegistryIdGenerator {
    @Inject
    Random mRandom;

    @Inject
    CommandRegistryIdGenerator() {
    }


    public int generate() {
        return mRandom.nextInt();
    }
}

"""