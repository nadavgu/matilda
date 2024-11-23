package org.matilda.commands.types

import pbandk.wkt.Any
import pbandk.wkt.Int32Value
import org.matilda.commands.CommandRegistryFactory
import org.matilda.commands.CommandRegistryManager
import org.matilda.commands.ServiceProxyFactory

open class DynamicServiceConverter<T>(
    private val mCommandRegistryManager: CommandRegistryManager,
    private val mCommandRegistryFactory: CommandRegistryFactory<T>,
    private val mServiceProxyFactory: ServiceProxyFactory<T>
) : ProtobufConverter<T> {
    private val mIntConverter = IntConverter()

    override fun convertToProtobuf(obj: T): Int32Value {
        val registryId =
            mCommandRegistryManager.addCommandRegistry(mCommandRegistryFactory.createCommandRegistry(obj))
        return mIntConverter.convertToProtobuf(registryId)
    }

    override fun convertFromProtobuf(obj: Any): T {
        val registryId = mIntConverter.convertFromProtobuf(obj)
        return mServiceProxyFactory.createServiceProxy(registryId)
    }
}
