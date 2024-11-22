package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import pbandk.wkt.Any
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.matilda.commands.Command
import org.matilda.commands.di.DiFrameWork
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.hasReturnValue
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.kotlinConverter
import org.matilda.commands.utils.addPbandkExtensionImports
import org.matilda.commands.utils.fileSpecBuilder
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class KotlinRawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    @Inject
    lateinit var mDiFrameWork: DiFrameWork

    override fun process(instance: CommandInfo) {
        fileSpecBuilder(mNameGenerator.forCommand(instance).rawCommandClassName.packageName(),
            createClassSpec(instance))
            .addPbandkExtensionImports()
            .build()
            .writeTo(mFiler)
    }
    private fun createClassSpec(command: CommandInfo) =
        TypeSpec.classBuilder(mNameGenerator.forCommand(command).rawCommandClassName.toKClassName())
            .addSuperinterface(Command::class)
            .addProperty(createServiceProperty(command))
            .addProperty(createDependenciesProperty(command))
            .primaryConstructor(createInjectConstructor(command))
            .addFunction(createRunMethod(command))
            .build()


    private fun createServiceProperty(command: CommandInfo) =
        PropertySpec.builder(SERVICE_FIELD_NAME, command.service.type.typeName.toKTypeName())
            .addModifiers(KModifier.PRIVATE)
            .build()

    private fun createDependenciesProperty(command: CommandInfo) =
        PropertySpec.builder(JAVA_DEPENDENCIES_FIELD_NAME,
            mNameGenerator.forService(command.service).dependenciesClassName.toKTypeName())
            .addModifiers(KModifier.PRIVATE)
            .build()

    private fun createInjectConstructor(command: CommandInfo) =
        FunSpec.constructorBuilder()
            .addAnnotation(mDiFrameWork.Inject)
            .addParameter(ParameterSpec.builder(SERVICE_PARAMETER_NAME,
                command.service.type.typeName.toKTypeName()).build())
            .addParameter(ParameterSpec.builder(DEPENDENCIES_PARAMETER_NAME,
                mNameGenerator.forService(command.service).dependenciesClassName.toKTypeName()).build())
            .addStatement("%L = %L", SERVICE_FIELD_NAME, SERVICE_PARAMETER_NAME)
            .addStatement("%L = %L", JAVA_DEPENDENCIES_FIELD_NAME, DEPENDENCIES_PARAMETER_NAME)
            .build()

    private fun createRunMethod(command: CommandInfo) =
        FunSpec.builder("run")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder(PARAMETER_NAME, ByteArray::class).build())
            .returns(ByteArray::class)
            .addStatement("val %L = %T.decodeFromByteArray(%L)",
                SOME_PARAMETER_VARIABLE_NAME, Some::class, PARAMETER_NAME)
            .apply {
                command.parameters.forEachIndexed { index, parameter ->
                    addParameterConversion(index, parameter)
                }
            }
            .addCommandInvocation(command)
            .addReturnValueConversion(command)
            .build()

    private fun FunSpec.Builder.addParameterConversion(index: Int, parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.kotlinConverter(parameterInfo.type)
        addStatement("val %L = $converterFormat.convertFromProtobuf(%L.any[%L])",
            parameterInfo.name, *converterArgs.toTypedArray(),
            SOME_PARAMETER_VARIABLE_NAME, index)
    }

    private fun FunSpec.Builder.addCommandInvocation(command: CommandInfo): FunSpec.Builder {
        if (command.hasReturnValue()) {
            addStatement("val %L = %L.%L(%L)",
                RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name,
                command.parameters.joinToString { it.name })
        } else {
            addStatement("%L.%L(%L)",
                SERVICE_FIELD_NAME, command.name, command.parameters.joinToString { it.name })
        }
        return this
    }

    private fun FunSpec.Builder.addReturnValueConversion(commandInfo: CommandInfo): FunSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.kotlinConverter(commandInfo.returnType)
        addStatement("return %T.pack($converterFormat.convertToProtobuf(%L)).encodeToByteArray()",
            Any::class.java, *converterArgs.toTypedArray(),
            if (commandInfo.hasReturnValue()) RETURN_VALUE_NAME else "null")
        return this
    }

    companion object {
        private const val SERVICE_FIELD_NAME = "mService"
        private const val SERVICE_PARAMETER_NAME = "service"
        private const val PARAMETER_NAME = "parameter"
        private const val RETURN_VALUE_NAME = "returnValue"
        private const val SOME_PARAMETER_VARIABLE_NAME = "someParameter"
        private const val DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
