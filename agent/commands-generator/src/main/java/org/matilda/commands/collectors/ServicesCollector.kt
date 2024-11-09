package org.matilda.commands.collectors

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isConstructor
import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.MatildaService
import org.matilda.commands.exceptions.AnnotationProcessingException
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.info.StaticServiceInfo
import javax.inject.Inject
import javax.lang.model.element.ExecutableElement
import kotlin.reflect.KClass

class ServicesCollector @Inject constructor() {
    @Inject
    lateinit var mRoundEnvironment: XRoundEnv

    @Inject
    lateinit var mCommandsCollector: CommandsCollector
    fun collect() = ProjectServices(collectStaticServices(), collectDynamicServices())

    private fun collectStaticServices() = collectServices(MatildaService::class).map { (element, serviceInfo) ->
        StaticServiceInfo(serviceInfo, checkIfServiceHasInjectConstructor(element))
    }

    private fun collectDynamicServices() = collectServices(MatildaDynamicService::class)
        .map { (element, serviceInfo) ->
            if (!element.isInterface()) {
                throw AnnotationProcessingException("Dynamic Services must be interfaces!", element)
            }
            serviceInfo
        }

    private fun collectServices(annotation: KClass<out Annotation>) =
        mRoundEnvironment.getElementsAnnotatedWith(annotation)
            .map { obj: XElement -> XTypeElement::class.java.cast(obj) }
            .map { element: XTypeElement -> Pair(element, collectService(element)) }

    private fun collectService(element: XTypeElement): ServiceInfo {
        val serviceInfo = ServiceInfo(element.qualifiedName, element.type, ArrayList())
        serviceInfo.commands.addAll(mCommandsCollector.collect(serviceInfo, element))
        return serviceInfo
    }

    private fun checkIfServiceHasInjectConstructor(element: XTypeElement): Boolean {
        val constructors = getConstructors(element)
        if (constructors.isEmpty()) {
            return false
        }
        if (constructors.any { constructor -> constructor.getAnnotation(Inject::class) != null }) {
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

    private fun getConstructors(element: XTypeElement) =
        element.getEnclosedElements().filter { it.isConstructor() }

    private fun isNonDefaultConstructor(constructor: XElement) =
        (constructor as ExecutableElement).parameters.isNotEmpty()
}
