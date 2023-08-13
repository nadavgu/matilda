package org.matilda.commands.processors

import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.DEPENDENCY_CLASS
import org.matilda.commands.python.DEPENDENCY_CONTAINER_CLASS
import org.matilda.commands.python.writer.*
import org.matilda.commands.python.writer.PythonFunctionSpec.Companion.property
import org.matilda.commands.types.TypeTranslator
import javax.inject.Inject

class PythonServicesContainerClassGenerator @Inject internal constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mPythonFileWriter: PythonFileWriter

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mTypeTranslator: TypeTranslator

    override fun process(instance: ProjectServices) {
        val pythonFile = PythonFile(mNameGenerator.pythonGeneratedServicesContainerPackage)
            .addImports(instance)
            .addClass(instance)
        mPythonFileWriter.write(pythonFile)
    }

    private fun PythonFile.addImports(services: ProjectServices) = apply {
        addFromImport(DEPENDENCY_CLASS).addFromImport(DEPENDENCY_CONTAINER_CLASS)
        services.forEachService {
            addFromImport(mNameGenerator.forService(it).serviceFullClassName)
        }
    }

    private fun PythonFile.addClass(services: ProjectServices) = apply {
        val pythonClass = newClass(PythonClassSpec(NameGenerator.SERVICES_CONTAINER_CLASS_NAME, DEPENDENCY_CLASS.name))
            .addConstructor(services)
            .addDICreator(services)

        services.forEachService {
            pythonClass.addServiceProperty(it)
        }
    }

    private fun PythonClass.addConstructor(services: ProjectServices) = apply {
        val constructor = addInstanceMethod(createConstructorSpec(services))
        if (services.services.isNotEmpty()) {
            services.forEachService { service ->
                constructor.addStatement("self.%s = %s", getServiceFieldName(service), getServiceParameterName(service))
            }
        } else {
            constructor.addStatement("pass")
        }
    }

    private fun createConstructorSpec(services: ProjectServices) =
        PythonFunctionSpec.constructorBuilder()
            .apply {
                services.forEachService { service ->
                    addParameter(getServiceParameterName(service), getClassName(service))
                }
            }.build()

    private fun getServiceParameterName(service: ServiceInfo) = mNameGenerator.forService(service).serviceSnakeCaseName

    private fun PythonClass.addDICreator(services: ProjectServices) = apply {
        val dependencyArgumentList = services.services.joinToString {
            "${DEPENDENCY_CONTAINER_PARAMETER_NAME}.get(${getClassName(it)})"
        }
        addStaticMethod(
            PythonFunctionSpec.functionBuilder("create")
                .addParameter(DEPENDENCY_CONTAINER_PARAMETER_NAME, DEPENDENCY_CONTAINER_CLASS.name)
                .returnTypeHint("'" + NameGenerator.SERVICES_CONTAINER_CLASS_NAME + "'").build()
        )
            .addStatement("return %s(%s)", NameGenerator.SERVICES_CONTAINER_CLASS_NAME, dependencyArgumentList)
    }

    private fun PythonClass.addServiceProperty(service: ServiceInfo) {
        addInstanceMethod(property(mNameGenerator.forService(service).serviceSnakeCaseName))
            .addStatement("return self.%s", getServiceFieldName(service))
    }

    private fun getServiceFieldName(service: ServiceInfo) =
        "__${mNameGenerator.forService(service).serviceSnakeCaseName}"

    private fun getClassName(service: ServiceInfo) = mNameGenerator.forService(service).serviceClassName

    companion object {
        private const val DEPENDENCY_CONTAINER_PARAMETER_NAME = "dependency_container"
    }
}
