package org.matilda.commands.processors

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.types.JavaDependencyInfo
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

class CommandDependenciesClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: CommandInfo) {
        JavaFile.builder(mNameGenerator.forCommand(instance).commandDependenciesPackageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(command: CommandInfo) =
        TypeSpec.classBuilder(mNameGenerator.forCommand(command).commandDependenciesClassName)
            .addModifiers(Modifier.PUBLIC)
            .addFields(createConverterDependenciesFields(command))
            .addMethod(createInjectConstructor())
            .build()

    private fun createConverterDependenciesFields(command: CommandInfo) = collectConverterDependencies(command)
        .map { dependencyInfo ->
            FieldSpec.builder(dependencyInfo.typeName, dependencyInfo.variableName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Inject::class.java)
                .build()
        }

    private fun collectConverterDependencies(command: CommandInfo): Set<JavaDependencyInfo> =
        HashSet<JavaDependencyInfo>().apply {
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
