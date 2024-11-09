package org.matilda.commands.collectors

import androidx.room.compiler.processing.*
import org.matilda.commands.MatildaCommand
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.isSupported
import javax.inject.Inject

class CommandsCollector @Inject constructor() {
    @Inject
    lateinit var mTypeConverter: TypeConverter

    fun collect(serviceInfo: ServiceInfo, serviceElement: XTypeElement) =
        serviceElement.getEnclosedElements()
            .filter { it.getAnnotation(MatildaCommand::class) != null }
            .map { element -> element as XMethodElement }
            .map { collectCommand(it, serviceInfo) }

    private fun collectCommand(element: XMethodElement, serviceInfo: ServiceInfo) =
        CommandInfo(element.name, serviceInfo, getParameters(element),
            getReturnType(element), element.thrownTypes)

    private fun getParameters(element: XExecutableElement) = element.parameters.map {
        getParameter(it)
    }

    private fun getParameter(element: XVariableElement) =
        ParameterInfo(element.name, element.type).also {
            verifyType(it.type, element)
        }

    private fun getReturnType(element: XMethodElement) = element.returnType.also {
        verifyType(it, element)
    }

    private fun verifyType(type: XType, element: XElement) {
        if (!mTypeConverter.isSupported(type)) {
            throw AnnotationProcessingException("Paramaters and return values of services have to be: " +
                    mTypeConverter.supportedTypesDescription, element)
        }
    }
}
