package org.matilda.commands.processors

import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.DATACLASS
import org.matilda.commands.python.DEPENDENCY_CLASS
import org.matilda.commands.python.DEPENDENCY_CONTAINER_CLASS
import org.matilda.commands.python.writer.*
import org.matilda.commands.types.PythonDependencyInfo
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.pythonConverter
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class PythonServiceDependenciesClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        val dependencies = collectDependencies(instance)
        val pythonFile = PythonFile(mNameGenerator.forService(instance).dependenciesPythonClassName.packageName)
            .addImports(dependencies)
            .addClass(instance, dependencies)

        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(dependencies: Set<PythonDependencyInfo>) = apply {
        addFromImport(DEPENDENCY_CLASS)
            .addFromImport(DEPENDENCY_CONTAINER_CLASS)
            .addFromImport(DATACLASS)

        dependencies.forEach {
            addRequiredFromImports(it.typeName)
        }
    }

    private fun PythonFile.addClass(service: ServiceInfo, dependencies: Set<PythonDependencyInfo>) = apply {
        newClass(PythonClassSpec.builder(getClassName(service))
            .addSuperclass(DEPENDENCY_CLASS.name)
            .addAnnotation(DATACLASS.name)
            .build())
            .addDependenciesFields(dependencies)
            .addDICreator(service, dependencies)
    }

    private fun PythonClass.addDependenciesFields(dependencies: Set<PythonDependencyInfo>) = apply {
        dependencies.forEach { addDependencyField(it) }
    }

    private fun PythonClass.addDependencyField(dependency: PythonDependencyInfo) =
        addField(PythonParameter(PythonVariable(dependency.variableName, dependency.typeName.name)))
    private fun PythonClass.addDICreator(service: ServiceInfo, dependencies: Set<PythonDependencyInfo>) = apply {
        val dependenciesInvocationList = dependencies.joinToString {
            "${DEPENDENCY_CONTAINER_PARAMETER_NAME}.get(${it.typeName.name})"
        }

        addStaticMethod(PythonFunctionSpec.functionBuilder("create")
            .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
            .returnTypeHint("'${getClassName(service)}'")
            .build())
            .addStatement("return %s(%s)",
                getClassName(service),
                dependenciesInvocationList)
    }

    private fun getClassName(service: ServiceInfo) =
        mNameGenerator.forService(service).dependenciesPythonClassName.name

    private fun collectDependencies(service: ServiceInfo): Set<PythonDependencyInfo> =
        service.commands.flatMapTo(mutableSetOf()) { collectDependencies(it) }
    private fun collectDependencies(command: CommandInfo): Set<PythonDependencyInfo> =
        LinkedHashSet<PythonDependencyInfo>().apply {
            command.parameters.forEach {
                addAll(collectConverterDependencies(it.type))
            }
            addAll(collectConverterDependencies(command.returnType))
        }

    private fun collectConverterDependencies(type: TypeMirror) = mTypeConverter.pythonConverter(type).dependencies

    companion object {
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
    }
}
