package org.matilda.commands.processors;

import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.names.NameGenerator;
import org.matilda.commands.python.writer.PythonClass;
import org.matilda.commands.python.writer.PythonClassSpec;
import org.matilda.commands.python.writer.PythonFile;
import org.matilda.commands.python.writer.PythonFunctionSpec;

import javax.inject.Inject;

import static org.matilda.commands.python.PythonClasses.*;

public class PythonServiceClassGenerator implements Processor<ServiceInfo> {
    @Inject
    NameGenerator mNameGenerator;

    @Inject
    PythonServiceClassGenerator() {}

    private static final String COMMAND_RUNNER_FIELD_NAME = "__command_runner";

    @Override
    public void process(ServiceInfo service) {
        PythonFile pythonFile = new PythonFile();
        addImports(pythonFile);
        addClass(pythonFile, service);
        System.out.println(pythonFile.getContent());
    }

    private static void addImports(PythonFile pythonFile) {
        pythonFile.addFromImport(DEPENDENCY_PACKAGE, DEPENDENCY_CLASS)
                .addFromImport(COMMAND_RUNNER_PACKAGE, COMMAND_RUNNER_CLASS);
    }

    private void addClass(PythonFile pythonFile, ServiceInfo service) {
        PythonClass pythonClass = pythonFile.newClass(new PythonClassSpec(getClassName(service), DEPENDENCY_CLASS));
        addConstructor(pythonClass);
//        addDICreator(pythonClass);
//        service.commands().forEach(command -> addCommandMethod(pythonClass, command));
    }

    private static final String COMMAND_RUNNER_PARAMETER_NAME = "command_runner";

    private void addConstructor(PythonClass pythonClass) {
        pythonClass.addInstanceMethod(PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, COMMAND_RUNNER_CLASS)
                .build())
                .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME);
    }

    private String getClassName(ServiceInfo service) {
        return mNameGenerator.forService(service).getServiceClassName();
    }
}
