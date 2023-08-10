package org.matilda.commands.processors;

import com.squareup.javapoet.*;
import dagger.Module;
import dagger.Provides;
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

    private static final String REGISTER_COMMANDS_METHOD_NAME = "registerCommands";

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
        var builder = TypeSpec.classBuilder(NameGenerator.COMMANDS_MODULE_CLASS_NAME)
                .addAnnotation(Module.class)
                .addModifiers(Modifier.PUBLIC);

        services.processEachCommand(command -> builder.addField(createCommandField(command)));

        return builder.addMethod(createInjectConstructor())
                .addMethod(createRegisterCommandsMethod(services))
                .addMethod(createCommandRegistryProviderMethod())
                .build();
    }

    private FieldSpec createCommandField(CommandInfo command) {
        return FieldSpec.builder(getCommandTypeName(command), getCommandFieldName(command))
                .addAnnotation(Inject.class)
                .build();
    }

    private static final String COMMAND_REGISTRY_PARAMETER_NAME = "commandRegistry";

    private MethodSpec createRegisterCommandsMethod(ProjectServices services) {
        ParameterSpec commandRegistryParameter =
                ParameterSpec.builder(TypeName.get(CommandRegistry.class), COMMAND_REGISTRY_PARAMETER_NAME).build();

        var builder = MethodSpec.methodBuilder(REGISTER_COMMANDS_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(commandRegistryParameter);

        services.processEachCommand(command ->
                builder.addStatement("$L.addCommand($L, $L)", COMMAND_REGISTRY_PARAMETER_NAME,
                        mCommandIdGenerator.generate(command), getCommandFieldName(command)));

        return builder.build();
    }

    private String getCommandFieldName(CommandInfo command) {
        return "m" + mNameGenerator.forCommand(command).getFullCommandName();
    }

    private MethodSpec createInjectConstructor() {
        return MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class)
                .build();
    }

    private static final String COMMAND_REGISTRY_VARIABLE_NAME = "commandRegistry";
    private static final String COMMANDS_MODULE_PARAMETER_NAME = "commandsModule";
    private MethodSpec createCommandRegistryProviderMethod() {
        return MethodSpec.methodBuilder("commandRegistry")
                .addModifiers(Modifier.STATIC)
                .addAnnotation(Provides.class)
                .addParameter(ParameterSpec.builder(NameGenerator.COMMANDS_MODULE_TYPE_NAME,
                        COMMANDS_MODULE_PARAMETER_NAME).build())
                .addStatement("$T $L = new $T()", CommandRegistry.class, COMMAND_REGISTRY_VARIABLE_NAME,
                        CommandRegistry.class)
                .addStatement("$L.$L($L)", COMMANDS_MODULE_PARAMETER_NAME, REGISTER_COMMANDS_METHOD_NAME,
                        COMMAND_REGISTRY_VARIABLE_NAME)
                .addStatement("return $L", COMMAND_REGISTRY_VARIABLE_NAME)
                .returns(CommandRegistry.class)
                .build();
    }

    private TypeName getCommandTypeName(CommandInfo command) {
        return mNameGenerator.forCommand(command).getRawCommandTypeName();
    }
}
