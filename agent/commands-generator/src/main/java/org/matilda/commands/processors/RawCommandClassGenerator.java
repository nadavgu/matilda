package org.matilda.commands.processors;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.names.NameGenerator;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import java.io.IOException;

public class RawCommandClassGenerator implements Processor<CommandInfo> {
    @Inject
    Filer mFiler;

    @Inject
    NameGenerator mNameGenerator;

    @Inject
    public RawCommandClassGenerator() {}

    @Override
    public void process(CommandInfo command) {
        try {
            JavaFile.builder(mNameGenerator.forCommand(command).getRawCommandPackageName(),
                            createClassSpec(command))
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createClassSpec(CommandInfo command) {
        return TypeSpec.classBuilder(mNameGenerator.forCommand(command).getRawCommandClassName())
                .build();
    }
}
