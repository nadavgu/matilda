package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import org.matilda.commands.CommandRegistryFactory;
import org.matilda.commands.CommandRepository;
import org.matilda.commands.ServiceProxyFactory;

public class DynamicServiceConverter<T> implements ProtobufConverter<T> {
    private final CommandRepository mCommandRepository;
    private final CommandRegistryFactory<T> mCommandRegistryFactory;
    private final ServiceProxyFactory<T> mServiceProxyFactory;
    private final IntConverter mIntConverter;

    public DynamicServiceConverter(CommandRepository commandRepository,
                                   CommandRegistryFactory<T> commandRegistryFactory,
                                   ServiceProxyFactory<T> serviceProxyFactory) {
        mCommandRepository = commandRepository;
        mCommandRegistryFactory = commandRegistryFactory;
        mServiceProxyFactory = serviceProxyFactory;
        mIntConverter = new IntConverter();
    }

    @Override
    public Int32Value convertToProtobuf(T service) {
        int registryId = mCommandRepository.addCommandRegistry(mCommandRegistryFactory.createCommandRegistry(service));
        return mIntConverter.convertToProtobuf(registryId);
    }

    @Override
    public T convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        int registryId = mIntConverter.convertFromProtobuf(object);
        return mServiceProxyFactory.createServiceProxy(registryId);
    }
}
