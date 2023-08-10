package org.matilda.commands.processors;

import com.squareup.javapoet.*;
import dagger.Module;
import dagger.Provides;
import org.apache.commons.lang3.StringUtils;
import org.matilda.commands.CommandRegistry;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.names.CommandIdGenerator;
import org.matilda.commands.names.NameGenerator;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class CommandsModuleClassGenerator implements Processor<ProjectServices> {
    @Inject
    Filer mFiler;

    @Inject
    NameGenerator mNameGenerator;

    @Inject
    CommandIdGenerator mCommandIdGenerator;

    @Inject
    public CommandsModuleClassGenerator() {}

    @Override
    public void process(ProjectServices services) {
        try {
            JavaFile.builder(NameGenerator.COMMANDS_GENERATED_PACKAGE,
                            createClassSpec(services))
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createClassSpec(ProjectServices services) {
        return TypeSpec.classBuilder(NameGenerator.COMMANDS_MODULE_CLASS_NAME)
                .addAnnotation(Module.class)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(createCommandRegistryProviderMethod(services))
                .build();
    }

    private static final String COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry";
    private MethodSpec createCommandRegistryProviderMethod(ProjectServices services) {
        var builder = MethodSpec.methodBuilder("commandRegistry")
                .addAnnotation(Provides.class)
                .returns(CommandRegistry.class);

        services.processEachCommand(command -> builder.addParameter(getCommandParameter(command)));

        builder.addStatement("$T $L = new $T()", CommandRegistry.class, COMMAND_REGISTRY_VARIABLE_NAME,
                CommandRegistry.class);
        services.processEachCommand(command -> builder.addStatement("$L.addCommand($L, $L)",
                COMMAND_REGISTRY_VARIABLE_NAME, mCommandIdGenerator.generate(command),
                getCommandParameterName(command)));
        builder.addStatement("return $L", COMMAND_REGISTRY_VARIABLE_NAME);

        return builder.build();
    }

    private ParameterSpec getCommandParameter(CommandInfo command) {
        return ParameterSpec.builder(getCommandTypeName(command), getCommandParameterName(command)).build();
    }

    private TypeName getCommandTypeName(CommandInfo command) {
        return mNameGenerator.forCommand(command).getRawCommandTypeName();
    }

    private String getCommandParameterName(CommandInfo command) {
        return StringUtils.uncapitalize(mNameGenerator.forCommand(command).getFullCommandName());
    }
}
