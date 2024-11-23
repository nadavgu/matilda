package org.matilda.commands

interface ServiceProxyFactory<T> {
    fun createServiceProxy(commandRegistryId: Int): T
}
