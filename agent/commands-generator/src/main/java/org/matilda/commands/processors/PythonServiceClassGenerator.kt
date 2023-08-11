package org.matilda.commands.processors

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.PythonClasses
import org.matilda.commands.python.writer.*
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonServiceClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ServiceInfo) {
        val pythonFile = PythonFile(mNameGenerator.forService(instance).pythonGeneratedServicePackage)
        addImports(pythonFile)
        addClass(pythonFile, instance)
        mPythonFileWriter.write(pythonFile)
    }

    private fun addClass(pythonFile: PythonFile, service: ServiceInfo) {
        val pythonClass = pythonFile.newClass(
            PythonClassSpec(getClassName(service), PythonClasses.DEPENDENCY_CLASS.className)
        )
        addConstructor(pythonClass)
        addDICreator(pythonClass, service)
        service.commands.forEach { command -> addCommandMethod(pythonClass, command) }
    }

    private fun addConstructor(pythonClass: PythonClass) {
        pythonClass.addInstanceMethod(
            PythonFunctionSpec.constructorBuilder()
                .addParameter(COMMAND_RUNNER_PARAMETER_NAME, PythonClasses.COMMAND_RUNNER_CLASS.className)
                .build()
        )
            .addStatement("self.%s = %s", COMMAND_RUNNER_FIELD_NAME, COMMAND_RUNNER_PARAMETER_NAME)
    }

    private fun addDICreator(pythonClass: PythonClass, service: ServiceInfo) {
        pythonClass.addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, PythonClasses.DEPENDENCY_CONTAINER_CLASS.className)
                .returnTypeHint("'" + getClassName(service) + "'").build()
        )
            .addStatement(
                "return %s(%s.get(%s))", getClassName(service),
                DEPENDENCY_CONTAINER_PARAMETER_NAME, PythonClasses.COMMAND_RUNNER_CLASS.className
            )
    }

    private fun addCommandMethod(pythonClass: PythonClass, command: CommandInfo) {
        val parameterType = getPythonType(command.parameterType)
        val returnType = getPythonType(command.returnType)
        pythonClass.addInstanceMethod(
            PythonFunctionSpec.functionBuilder(command.name)
                .addParameter(PARAMETER_VARIABLE_NAME, parameterType)
                .returnTypeHint(returnType)
                .build()
        )
            .addStatement("%s = %s.SerializeToString()", RAW_PARAMETER_VARIABLE_NAME, PARAMETER_VARIABLE_NAME)
            .addStatement(
                "%s = self.%s.run(%d, %s)", RAW_RETURN_VALUE_VARIABLE_NAME, COMMAND_RUNNER_FIELD_NAME,
                mCommandIdGenerator.generate(command), RAW_PARAMETER_VARIABLE_NAME
            )
            .addStatement("%s = %s()", RETURN_VALUE_VARIABLE_NAME, returnType)
            .addStatement("%s.ParseFromString(%s)", RETURN_VALUE_VARIABLE_NAME, RAW_RETURN_VALUE_VARIABLE_NAME)
            .addStatement("return %s", RETURN_VALUE_VARIABLE_NAME)
    }

    private fun getPythonType(typeMirror: TypeMirror): String {
        val typeName = TypeName.get(typeMirror)
        return if (typeName is ClassName) {
            typeName.simpleName()
        } else typeName.toString()
    }

    private fun getClassName(service: ServiceInfo): String {
        return mNameGenerator.forService(service).serviceClassName
    }

    companion object {
        private const val COMMAND_RUNNER_FIELD_NAME = "__command_runner"
        private fun addImports(pythonFile: PythonFile) {
            pythonFile.addFromImport(PythonClasses.DEPENDENCY_CLASS)
                .addFromImport(PythonClasses.DEPENDENCY_CONTAINER_CLASS)
                .addFromImport(PythonClasses.COMMAND_RUNNER_CLASS)
                .addFromImport(PythonClasses.PROTO_WRAPPERS_PACKAGE, "*")
        }

        private const val COMMAND_RUNNER_PARAMETER_NAME = "command_runner"
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
        private const val PARAMETER_VARIABLE_NAME = "parameter"
        private const val RAW_PARAMETER_VARIABLE_NAME = "raw_parameter"
        private const val RAW_RETURN_VALUE_VARIABLE_NAME = "raw_return_value"
        private const val RETURN_VALUE_VARIABLE_NAME = "return_value"
    }
}
