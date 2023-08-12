package org.matilda.commands.collectors

import com.google.protobuf.Message
import org.matilda.commands.MatildaCommand
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
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
        CommandInfo(element.simpleName.toString(), serviceInfo, getParameterType(element), getReturnType(element))

    private fun getParameterType(element: ExecutableElement): TypeMirror {
        val params = element.parameters
        if (params.size != 1) {
            throw AnnotationProcessingException("Can only handle commands with one parameter", element)
        }
        val type = params[0].asType()
        verifyType(type, element)
        return type
    }

    private fun getReturnType(element: ExecutableElement): TypeMirror {
        val type = element.returnType
        verifyType(type, element)
        return type
    }

    private fun verifyType(type: TypeMirror, element: Element) {
        val messageBaseType: TypeMirror =
            mTypes.getDeclaredType(mElements.getTypeElement(Message::class.java.canonicalName))
        if (!mTypes.isSubtype(type, messageBaseType)) {
            throw AnnotationProcessingException("Paramaters and return values of services have to be " +
                    "protobuf messages!", element)
        }
    }
}
