package org.matilda.commands.processors

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ServiceInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.JavaDependencyInfo
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

class JavaServiceDependenciesClassGenerator @Inject constructor() : Processor<ServiceInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: ServiceInfo) {
        JavaFile.builder(mNameGenerator.forService(instance).dependenciesClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(service: ServiceInfo) =
        TypeSpec.classBuilder(mNameGenerator.forService(service).dependenciesClassName)
            .addModifiers(Modifier.PUBLIC)
            .addFields(createDependenciesFields(service))
            .addMethod(createInjectConstructor())
            .build()

    private fun createDependenciesFields(service: ServiceInfo) = collectDependencies(service)
        .map { dependencyInfo ->
            FieldSpec.builder(dependencyInfo.typeName, dependencyInfo.variableName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Inject::class.java)
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

    private fun collectConverterDependencies(type: TypeMirror) = mTypeConverter.javaConverter(type).dependencies

    private fun createInjectConstructor() =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()
}
