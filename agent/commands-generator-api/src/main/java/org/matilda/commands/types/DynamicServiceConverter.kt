package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import org.matilda.commands.CommandRegistryFactory;
import org.matilda.commands.CommandRegistryManager;
import org.matilda.commands.ServiceProxyFactory;

public class DynamicServiceConverter<T> implements ProtobufConverter<T> {
    private final CommandRegistryManager mCommandRegistryManager;
    private final CommandRegistryFactory<T> mCommandRegistryFactory;
    private final ServiceProxyFactory<T> mServiceProxyFactory;
    private final IntConverter mIntConverter;

    public DynamicServiceConverter(CommandRegistryManager commandRegistryManager,
                                   CommandRegistryFactory<T> commandRegistryFactory,
                                   ServiceProxyFactory<T> serviceProxyFactory) {
        mCommandRegistryManager = commandRegistryManager;
        mCommandRegistryFactory = commandRegistryFactory;
        mServiceProxyFactory = serviceProxyFactory;
        mIntConverter = new IntConverter();
    }

    @Override
    public Int32Value convertToProtobuf(T service) {
        int registryId = mCommandRegistryManager.addCommandRegistry(mCommandRegistryFactory.createCommandRegistry(service));
        return mIntConverter.convertToProtobuf(registryId);
    }

    @Override
    public T convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        int registryId = mIntConverter.convertFromProtobuf(object);
        return mServiceProxyFactory.createServiceProxy(registryId);
    }
}
