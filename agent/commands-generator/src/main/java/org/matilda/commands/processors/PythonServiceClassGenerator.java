package org.matilda.commands.processors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.names.CommandIdGenerator;
import org.matilda.commands.names.NameGenerator;
import org.matilda.commands.python.writer.*;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import java.io.IOException;

import static org.matilda.commands.python.PythonClasses.*;

public class PythonServiceClassGenerator implements Processor<ServiceInfo> {
    @Inject
    NameGenerator mNameGenerator;

    @Inject
    PythonFileWriter mPythonFileWriter;

    @Inject
    CommandIdGenerator mCommandIdGenerator;

    @Inject
    PythonServiceClassGenerator() {}

    private static final String COMMAND_RUNNER_FIELD_NAME = "__command_runner";

    @Override
    public void process(ServiceInfo service) {
        try {
            PythonFile pythonFile = new PythonFile(mNameGenerator.forService(service).getPythonGeneratedServicePackage());
            addImports(pythonFile);
            addClass(pythonFile, service);
            mPythonFileWriter.write(pythonFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addImports(PythonFile pythonFile) {
        pythonFile.addFromImport(DEPENDENCY_CLASS)
                .addFromImport(DEPENDENCY_CONTAINER_CLASS)
                .addFromImport(COMMAND_RUNNER_CLASS)
                .addFromImport(PROTO_WRAPPERS_PACKAGE, "*");
    }

    private void addClass(PythonFile pythonFile, ServiceInfo service) {
        PythonClass pythonClass = pythonFile.newClass(new PythonClassSpec(getClassName(service),
                DEPENDENCY_CLASS.className()));
        addConstructor(pythonClass);
        addDICreator(pythonClass, service);
        service.getCommands().forEach(command -> addCommandMethod(pythonClass, command));
    }

    private static final String COMMAND_RUNNER_PARAMETER_NAME = "command_runner";

    private void addConstructor(PythonClass pythonClass) {
        pythonClass.addInstanceMethod(PythonFunctionSpec.constructorBuilder()
                        .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.className())
                        .build())
                .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME);
    }

    private static final String DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container";

    private void addDICreator(PythonClass pythonClass, ServiceInfo service) {
        pythonClass.addStaticMethod(PythonFunctionSpec.functionBuilder("create")
                        .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.className())
                        .returnTypeHint("'" + getClassName(service) + "'").build())
                .addStatement("return %s(%s.get(%s))", getClassName(service),
                        DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS.className());
    }

    private static final String PARAMETER_VARIABLE_NAME = "parameter";
    private static final String RAW_PARAMETER_VARIABLE_NAME = "raw_parameter";
    private static final String RAW_RETURN_VALUE_VARIABLE_NAME = "raw_return_value";
    private static final String RETURN_VALUE_VARIABLE_NAME = "return_value";

    private void addCommandMethod(PythonClass pythonClass, CommandInfo command) {
        String parameterType = getPythonType(command.getParameterType());
        String returnType = getPythonType(command.getReturnType());
        pythonClass.addInstanceMethod(PythonFunctionSpec.functionBuilder(command.getName())
                        .addParameter(PARAMETER_VARIABLE_NAME, parameterType)
                        .returnTypeHint(returnType)
                        .build())
                .addStatement("%s = %s.SerializeToString()", RAW_PARAMETER_VARIABLE_NAME, PARAMETER_VARIABLE_NAME)
                .addStatement("%s = self.%s.run(%d, %s)", RAW_RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                        mCommandIdGenerator.generate(command), RAW_PARAMETER_VARIABLE_NAME)
                .addStatement("%s = %s()", RETURN_VALUE_VARIABLE_NAME, returnType)
                .addStatement("%s.ParseFromString(%s)", RETURN_VALUE_VARIABLE_NAME, RAW_RETURN_VALUE_VARIABLE_NAME)
                .addStatement("return %s", RETURN_VALUE_VARIABLE_NAME);
    }

    private String getPythonType(TypeMirror typeMirror) {
        TypeName typeName = TypeName.get(typeMirror);
        if (typeName instanceof ClassName) {
            return ((ClassName) typeName).simpleName();
        }

        return typeName.toString();
    }

    private String getClassName(ServiceInfo service) {
        return mNameGenerator.forService(service).getServiceClassName();
    }
}
