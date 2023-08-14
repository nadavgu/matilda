package org.matilda.commands.collectors

import com.google.protobuf.Message
import com.squareup.javapoet.TypeName
import org.matilda.commands.MatildaCommand
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.types.isScalarType
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class CommandsCollector @Inject constructor() {
    @Inject
    lateinit var mTypes: Types

    @Inject
    lateinit var mElements: Elements

    fun collect(serviceInfo: ServiceInfo, serviceElement: TypeElement) =
        serviceElement.enclosedElements
            .filter { it.getAnnotation(MatildaCommand::class.java) != null }
            .map { element -> element as ExecutableElement }
            .map { collectCommand(it, serviceInfo) }

    private fun collectCommand(element: ExecutableElement, serviceInfo: ServiceInfo) =
        CommandInfo(element.simpleName.toString(), serviceInfo, getParameters(element),
            getReturnType(element), element.thrownTypes)

    private fun getParameters(element: ExecutableElement) = element.parameters.map {
        getParameter(it)
    }

    private fun getParameter(element: VariableElement) =
        ParameterInfo(element.simpleName.toString(), element.asType()).also {
            verifyType(it.type, element)
        }

    private fun getReturnType(element: ExecutableElement) = element.returnType.also {
        verifyType(it, element)
    }

    private fun verifyType(type: TypeMirror, element: Element) {
        val messageBaseType: TypeMirror =
            mTypes.getDeclaredType(mElements.getTypeElement(Message::class.java.canonicalName))
        if (!TypeName.get(type).isScalarType() && !mTypes.isSubtype(type, messageBaseType)) {
            throw AnnotationProcessingException("Paramaters and return values of services have to be " +
                    "protobuf messages or scalar types!", element)
        }
    }
}
