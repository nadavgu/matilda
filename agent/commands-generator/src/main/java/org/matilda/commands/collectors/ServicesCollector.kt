package org.matilda.commands.collectors

import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.MatildaService
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.info.StaticServiceInfo
import javax.annotation.processing.RoundEnvironment
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

class ServicesCollector @Inject constructor() {
    @Inject
    lateinit var mRoundEnvironment: RoundEnvironment

    @Inject
    lateinit var mCommandsCollector: CommandsCollector
    fun collect() = ProjectServices(collectStaticServices(), collectDynamicServices())

    private fun collectStaticServices() = collectServices(MatildaService::class.java).map { (element, serviceInfo) ->
        StaticServiceInfo(serviceInfo, checkIfServiceHasInjectConstructor(element))
    }

    private fun collectDynamicServices() = collectServices(MatildaDynamicService::class.java)
        .map { (element, serviceInfo) ->
            if (element.kind != ElementKind.INTERFACE) {
                throw AnnotationProcessingException("Dynamic Services must be interfaces!", element)
            }
            serviceInfo
        }

    private fun collectServices(annotation: Class<out Annotation>) =
        mRoundEnvironment.getElementsAnnotatedWith(annotation)
            .map { obj: Any? -> TypeElement::class.java.cast(obj) }
            .map { element: TypeElement -> Pair(element, collectService(element)) }

    private fun collectService(element: TypeElement): ServiceInfo {
        val serviceInfo = ServiceInfo(element.qualifiedName.toString(), element.asType(), ArrayList())
        serviceInfo.commands.addAll(mCommandsCollector.collect(serviceInfo, element))
        return serviceInfo
    }

    private fun checkIfServiceHasInjectConstructor(element: TypeElement): Boolean {
        val constructors = getConstructors(element)
        if (constructors.isEmpty()) {
            return false
        }
        if (constructors.any { constructor -> constructor.getAnnotation(Inject::class.java) != null }) {
            return true
        }
        if (constructors.any { constructor -> isNonDefaultConstructor(constructor) }) {
            throw AnnotationProcessingException("This service has non-default constructors that aren't marked " +
                    "with @Inject. In order for you to use the sweet sweet DI, mark a constructor with " +
                    "@Inject, or, if you don't want amazing DI, delete all the non-default constructors," +
                    " so that I will be able to @Provide you", constructors[0])
        }
        return false
    }

    private fun getConstructors(element: TypeElement) =
        element.enclosedElements.filter { enclosedElement: Element -> enclosedElement.kind == ElementKind.CONSTRUCTOR }

    private fun isNonDefaultConstructor(constructor: Element) =
        (constructor as ExecutableElement).parameters.isNotEmpty()
}
