package org.matilda.commands.processors;

import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.names.NameGenerator;
import org.matilda.commands.python.writer.*;

import javax.inject.Inject;

import java.io.IOException;

import static org.matilda.commands.python.PythonClasses.*;

public class PythonServiceClassGenerator implements Processor<ServiceInfo> {
    @Inject
    NameGenerator mNameGenerator;

    @Inject
    PythonFileWriter mPythonFileWriter;

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
        pythonFile.addFromImport(DEPENDENCY_PACKAGE, DEPENDENCY_CLASS)
                .addFromImport(DEPENDENCY_CONTAINER_PACKAGE, DEPENDENCY_CONTAINER_CLASS)
                .addFromImport(COMMAND_RUNNER_PACKAGE, COMMAND_RUNNER_CLASS);
    }

    private void addClass(PythonFile pythonFile, ServiceInfo service) {
        PythonClass pythonClass = pythonFile.newClass(new PythonClassSpec(getClassName(service), DEPENDENCY_CLASS));
        addConstructor(pythonClass);
        addDICreator(pythonClass, service);
//        service.commands().forEach(command -> addCommandMethod(pythonClass, command));
    }

    private static final String COMMAND_RUNNER_PARAMETER_NAME = "command_runner";

    private void addConstructor(PythonClass pythonClass) {
        pythonClass.addInstanceMethod(PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS)
                .build())
                .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME);
    }

    private static final String DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container";
    private void addDICreator(PythonClass pythonClass, ServiceInfo service) {
        pythonClass.addStaticMethod(PythonFunctionSpec.functionBuilder("create")
                        .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS)
                        .returnTypeHint("'" + getClassName(service) + "'")
                .build())
                .addStatement("return %s(%s.get(%s))", getClassName(service),
                        DEPENDENCY_CONTAINER_PARAMETER_NAME, COMMAND_RUNNER_CLASS);
    }

    private String getClassName(ServiceInfo service) {
        return mNameGenerator.forService(service).getServiceClassName();
    }
}
