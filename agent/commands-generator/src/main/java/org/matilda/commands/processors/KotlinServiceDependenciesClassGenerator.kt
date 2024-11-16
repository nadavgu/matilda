package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.matilda.commands.di.DiFrameWork
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.JavaDependencyInfo
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.kotlinConverter
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinServiceDependenciesClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    @Inject
    lateinit var mDiFrameWork: DiFrameWork

    override fun process(instance: ServiceInfo) {
        fileSpecBuilder(mNameGenerator.forService(instance).dependenciesClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo): TypeSpec {
        val dependencies = collectDependencies(service)
        return TypeSpec.classBuilder(mNameGenerator.forService(service).dependenciesClassName.toKClassName())
            .addProperties(createDependenciesFields(dependencies))
            .primaryConstructor(createInjectConstructor(dependencies))
            .build()
    }

    private fun createDependenciesFields(dependencies: Set<JavaDependencyInfo>) = dependencies
        .map { dependencyInfo ->
            PropertySpec.builder(dependencyInfo.variableName, dependencyInfo.typeName.toKTypeName())
                .initializer(dependencyInfo.variableName)
                .build()
        }

    private fun collectDependencies(service: ServiceInfo): Set<JavaDependencyInfo> =
        service.commands.flatMapTo(mutableSetOf()) { collectDependencies(it) }
    private fun collectDependencies(command: CommandInfo): Set<JavaDependencyInfo> =
        LinkedHashSet<JavaDependencyInfo>().apply {
            command.parameters.forEach {
                addAll(collectConverterDependencies(it.type))
            }
            addAll(collectConverterDependencies(command.returnType))
        }

    private fun collectConverterDependencies(type: XType) = mTypeConverter.kotlinConverter(type).dependencies

    private fun createInjectConstructor(dependencies: Set<JavaDependencyInfo>) =
        FunSpec.constructorBuilder()
            .addAnnotation(mDiFrameWork.Inject)
            .apply {
                dependencies.forEach {
                    addParameter(it.variableName, it.typeName.toKTypeName())
                }
            }
            .build()
}
