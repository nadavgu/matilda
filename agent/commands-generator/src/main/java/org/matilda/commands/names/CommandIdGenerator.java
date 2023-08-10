package org.matilda.commands.names;

import org.matilda.commands.info.CommandInfo;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandIdGenerator {
    @Inject
    CommandIdGenerator() {}

    public int generate(CommandInfo commandInfo) {
        try {
            var digest = MessageDigest.getInstance("SHA-1");
            digest.update(commandInfo.service().fullName().getBytes());
            digest.update(commandInfo.name().getBytes());
            byte[] hashBytes = digest.digest();

            return ByteBuffer.wrap(hashBytes).getInt();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
