package org.matilda.commands.processors;

import com.squareup.javapoet.*;
import org.matilda.commands.Command;
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

    private static final String SERVICE_FIELD_NAME = "mService";

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
                .addSuperinterface(Command.class)
                .addField(createServiceField(command))
                .addMethod(createInjectConstructor())
                .build();
    }

    private FieldSpec createServiceField(CommandInfo command) {
        return FieldSpec.builder(TypeName.get(command.service().type()), SERVICE_FIELD_NAME)
                .addAnnotation(Inject.class)
                .build();
    }

    private MethodSpec createInjectConstructor() {
        return MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class)
                .build();
    }
}
