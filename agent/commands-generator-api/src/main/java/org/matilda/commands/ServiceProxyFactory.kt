package org.matilda.commands;

public interface ServiceProxyFactory<T> {
    T createServiceProxy(int commandRegistryId);
}
