package org.matilda.commands.processors;

import com.squareup.javapoet.*;
import org.matilda.commands.Command;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.names.NameGenerator;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class RawCommandClassGenerator implements Processor<CommandInfo> {
    @Inject
    Filer mFiler;

    @Inject
    NameGenerator mNameGenerator;

    @Inject
    public RawCommandClassGenerator() {}

    private static final ArrayTypeName BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE);
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
                .addMethod(createRunMethod(command))
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

    private final static String RAW_PARAMETER_NAME = "rawParameter";
    private final static String PARSED_PARAMETER_NAME = "parameter";
    private final static String RETURN_VALUE_NAME = "returnValue";
    private final static String EXCEPTION_NAME = "exception";

    private MethodSpec createRunMethod(CommandInfo command) {
        return MethodSpec.methodBuilder("run")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(BYTE_ARRAY_TYPE_NAME, RAW_PARAMETER_NAME).build())
                .returns(ArrayTypeName.of(TypeName.BYTE))
                .beginControlFlow("try")
                .addStatement("$T $L = $T.parseFrom($L)", command.parameterType(), PARSED_PARAMETER_NAME,
                        command.parameterType(), RAW_PARAMETER_NAME)
                .addStatement("$T $L = $L.$L($L)", command.returnType(), RETURN_VALUE_NAME,
                        SERVICE_FIELD_NAME, command.name(), PARSED_PARAMETER_NAME)
                .addStatement("return $L.toByteArray()", RETURN_VALUE_NAME)
                .nextControlFlow("catch ($T $L)", IOException.class, EXCEPTION_NAME)
                .addStatement("throw new $T($L)", RuntimeException.class, EXCEPTION_NAME)
                .endControlFlow()
                .build();
    }
}
