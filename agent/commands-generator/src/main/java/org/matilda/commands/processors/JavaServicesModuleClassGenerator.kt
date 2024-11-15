package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dagger.Module
import dagger.Provides
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import javax.inject.Inject
import javax.lang.model.element.Modifier

class JavaServicesModuleClassGenerator @Inject constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    override fun process(instance: ProjectServices) {
        JavaFile.builder(mNameGenerator.commandsGeneratedPackage.packageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(services: ProjectServices): TypeSpec {
        val builder = TypeSpec.classBuilder(mNameGenerator.servicesModuleClassName)
            .addAnnotation(Module::class.java)
            .addModifiers(Modifier.PUBLIC)
        services.forEachStaticService { service ->
            if (!service.hasInjectConstructor) {
                builder.addMethod(createServiceProvideMethod(service.serviceInfo))
            }
        }
        return builder.build()
    }

    private fun createServiceProvideMethod(service: ServiceInfo) =
        MethodSpec.methodBuilder(getProvideMethodName(service))
            .addAnnotation(Provides::class.java)
            .addModifiers(Modifier.STATIC)
            .returns(service.type.typeName)
            .addStatement("return new \$T()", service.type.typeName)
            .build()

    private fun getProvideMethodName(service: ServiceInfo) =
        StringUtils.uncapitalize(mNameGenerator.forService(service).serviceClassName)
}
