package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.di.DiFrameWork
import org.matilda.commands.di.staticProvidesFunctionBuilder
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.CommandIdGenerator
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinServicesModuleClassGenerator @Inject constructor() : Processor<ProjectServices> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator

    @Inject
    lateinit var mDiFrameWork: DiFrameWork

    override fun process(instance: ProjectServices) {
        fileSpecBuilder(mNameGenerator.commandsGeneratedPackage.packageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(services: ProjectServices): TypeSpec {
        val builder = TypeSpec.interfaceBuilder(mNameGenerator.servicesModuleClassName.toKClassName())
            .addAnnotation(mDiFrameWork.Module)
        builder.staticProvidesFunctionBuilder(mDiFrameWork) {
            services.forEachStaticService { service ->
                if (!service.hasInjectConstructor) {
                    addFunction(createServiceProvideMethod(service.serviceInfo))
                }
            }
        }
        return builder.build()
    }

    private fun createServiceProvideMethod(service: ServiceInfo) =
        FunSpec.builder(getProvideMethodName(service))
            .addAnnotation(mDiFrameWork.Provides)
            .returns(service.type.typeName.toKTypeName())
            .addStatement("return %T()", service.type.typeName.toKTypeName())
            .build()

    private fun getProvideMethodName(service: ServiceInfo) =
        StringUtils.uncapitalize(mNameGenerator.forService(service).serviceClassName)
}
