package org.matilda.commands.processors

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.squareup.javapoet.*
import org.matilda.commands.Command
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ParameterInfo
import org.matilda.commands.info.hasReturnValue
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.protobuf.Some
import org.matilda.commands.types.DynamicServiceTypeConverter.Companion.JAVA_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.types.TypeConverter
import org.matilda.commands.types.javaConverter
import org.matilda.commands.utils.PBANDK_ANY_EXTENSIONS_EXTENSIONS_TYPE
import org.matilda.commands.utils.PBANDK_MESSAGE_EXTENSIONS_TYPE
import pbandk.wkt.Any
import javax.inject.Inject
import javax.lang.model.element.Modifier

class JavaRawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: XFiler

    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeConverter: TypeConverter

    override fun process(instance: CommandInfo) {
        JavaFile.builder(mNameGenerator.forCommand(instance).rawCommandClassName.packageName(),
            createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(command: CommandInfo) =
        TypeSpec.classBuilder(mNameGenerator.forCommand(command).rawCommandClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(Command::class.java)
            .addField(createServiceField(command))
            .addField(createDependenciesField(command))
            .addMethod(createInjectConstructor(command))
            .addMethod(createRunMethod(command))
            .build()

    private fun createServiceField(command: CommandInfo) =
        FieldSpec.builder(command.service.type.typeName, SERVICE_FIELD_NAME)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build()

    private fun createDependenciesField(command: CommandInfo) =
        FieldSpec.builder(mNameGenerator.forService(command.service).dependenciesClassName,
            JAVA_DEPENDENCIES_FIELD_NAME)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build()

    private fun createInjectConstructor(command: CommandInfo) =
        MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(command.service.type.typeName, SERVICE_PARAMETER_NAME).build())
            .addParameter(ParameterSpec.builder(mNameGenerator.forService(command.service).dependenciesClassName,
                DEPENDENCIES_PARAMETER_NAME).build())
            .addStatement("\$L = \$L", SERVICE_FIELD_NAME, SERVICE_PARAMETER_NAME)
            .addStatement("\$L = \$L", JAVA_DEPENDENCIES_FIELD_NAME, DEPENDENCIES_PARAMETER_NAME)
            .build()

    private fun createRunMethod(command: CommandInfo) =
        MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(BYTE_ARRAY_TYPE_NAME, RAW_PARAMETER_NAME).build())
            .returns(ArrayTypeName.of(TypeName.BYTE))
            .addExceptions(command.thrownTypes.map { it.typeName })
            .addStatement("\$T \$L = \$T.decodeFromByteArray(\$T, \$L)",
                Some::class.java, SOME_PARAMETER_VARIABLE_NAME,
                PBANDK_MESSAGE_EXTENSIONS_TYPE,
                Some.Companion::class.java, RAW_PARAMETER_NAME)
            .apply {
                command.parameters.forEachIndexed { index, parameter ->
                    addParameterConversion(index, parameter)
                }
            }
            .addCommandInvocation(command)
            .addReturnValueConversion(command)
            .build()

    private fun MethodSpec.Builder.addParameterConversion(index: Int, parameterInfo: ParameterInfo) {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(parameterInfo.type)
        addStatement("\$T \$L = $converterFormat.convertFromProtobuf(\$L.getAny().get(\$L))",
            parameterInfo.type.typeName, parameterInfo.name, *converterArgs.toTypedArray(),
            SOME_PARAMETER_VARIABLE_NAME, index)
    }

    private fun MethodSpec.Builder.addCommandInvocation(command: CommandInfo): MethodSpec.Builder {
        if (command.hasReturnValue()) {
            addStatement("\$T \$L = \$L.\$L(\$L)",
                command.returnType.typeName, RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name,
                command.parameters.joinToString { it.name })
        } else {
            addStatement("\$L.\$L(\$L)",
                SERVICE_FIELD_NAME, command.name, command.parameters.joinToString { it.name })
        }
        return this
    }

    private fun MethodSpec.Builder.addReturnValueConversion(commandInfo: CommandInfo): MethodSpec.Builder {
        val (converterFormat, converterArgs) = mTypeConverter.javaConverter(commandInfo.returnType)
        addStatement("return \$T.encodeToByteArray(\$T.pack(\$T, $converterFormat.convertToProtobuf(\$L), \"type.googleapis.com\"))",
            PBANDK_MESSAGE_EXTENSIONS_TYPE, PBANDK_ANY_EXTENSIONS_EXTENSIONS_TYPE,
            Any.Companion::class.java, *converterArgs.toTypedArray(),
            if (commandInfo.hasReturnValue()) RETURN_VALUE_NAME else "null")
        return this
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        private const val SERVICE_FIELD_NAME = "mService"
        private const val SERVICE_PARAMETER_NAME = "service"
        private const val RAW_PARAMETER_NAME = "rawParameter"
        private const val RETURN_VALUE_NAME = "returnValue"
        private const val SOME_PARAMETER_VARIABLE_NAME = "someParameter"
        private const val DEPENDENCIES_PARAMETER_NAME = "dependencies"
    }
}
