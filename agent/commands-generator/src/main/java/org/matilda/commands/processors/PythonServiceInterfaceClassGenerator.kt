package org.matilda.commands.processors

import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.ABC_CLASS
import org.matilda.commands.python.ABSTRACTMETHOD_FUNCTION
import org.matilda.commands.python.DEPENDENCY_CLASS
import org.matilda.commands.python.DEPENDENCY_CONTAINER_CLASS
import org.matilda.commands.python.writer.*
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.pythonType
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonServiceInterfaceClassGenerator @Inject internal constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        val pythonFile = PythonFile(mNameGenerator.forService(instance).serviceFullClassName.packageName)
            .addImports()
            .addClass(instance)
        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addClass(service: ServiceInfo) = apply {
        val pythonClass = newClass(PythonClassSpec(getClassName(service), DEPENDENCY_CLASS.name, ABC_CLASS.name))
            .addDICreator(service)

        service.commands.forEach { command ->
            addCommandImports(command)
            pythonClass.addCommandMethod(command)
        }
    }

    private fun PythonClass.addDICreator(service: ServiceInfo) = apply {
        val proxyClassName = mNameGenerator.forService(service).serviceProxyClassName

        addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
                .returnTypeHint("'" + getClassName(service) + "'").build()
        )
            .addStatement("from %s import %s", proxyClassName.packageName.packageName, proxyClassName.name)
            .addStatement("return %s.get(%s)", DEPENDENCY_CONTAINER_PARAMETER_NAME, proxyClassName.name)
    }

    private fun PythonClass.addCommandMethod(command: CommandInfo) {
        addInstanceMethod(createCommandFunctionSpec(command))
            .addStatement("pass")
    }

    private fun createCommandFunctionSpec(command: CommandInfo): PythonFunctionSpec {
        val builder = PythonFunctionSpec.functionBuilder(mNameGenerator.forCommand(command).snakeCaseName)
            .returnTypeHint(getPythonType(command.returnType))
            .addAnnotation(ABSTRACTMETHOD_FUNCTION.name)
        command.parameters.forEach {
            builder.addParameter(it.pythonName, getPythonType(it.type))
        }
        return builder.build()
    }

    private val ParameterInfo.pythonName
        get() = name.toSnakeCase()

    private fun getPythonType(typeMirror: TypeMirror) = mTypeConverter.pythonType(typeMirror).name

    private fun PythonFile.addCommandImports(command: CommandInfo) = apply {
        importPythonType(command.returnType)
        command.parameters.forEach { importPythonType(it.type) }
    }

    private fun PythonFile.importPythonType(typeMirror: TypeMirror) {
        addRequiredFromImports(mTypeConverter.pythonType(typeMirror))
    }
    private fun getClassName(service: ServiceInfo) = mNameGenerator.forService(service).serviceClassName

    private fun PythonFile.addImports() = apply {
        addFromImport(DEPENDENCY_CLASS)
            .addFromImport(DEPENDENCY_CONTAINER_CLASS)
            .addFromImport(ABC_CLASS)
            .addFromImport(ABSTRACTMETHOD_FUNCTION)
    }

    companion object {
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
    }
}
