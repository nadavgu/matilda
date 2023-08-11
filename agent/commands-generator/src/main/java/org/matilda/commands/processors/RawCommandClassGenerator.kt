package org.matilda.commands.processors

import com.squareup.javapoet.*
import org.matilda.commands.Command
import org.matilda.commands.info.CommandInfo
import org.matilda.commands.names.NameGenerator
import java.io.IOException
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.Modifier

class RawCommandClassGenerator @Inject constructor() : Processor<CommandInfo> {
    @Inject
    lateinit var mFiler: Filer

    @Inject
    lateinit var mNameGenerator: NameGenerator

    override fun process(instance: CommandInfo) {
        JavaFile.builder(mNameGenerator.forCommand(instance).rawCommandPackageName, createClassSpec(instance))
            .build()
            .writeTo(mFiler)
    }

    private fun createClassSpec(command: CommandInfo): TypeSpec {
        return TypeSpec.classBuilder(mNameGenerator.forCommand(command).rawCommandClassName)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(Command::class.java)
            .addField(createServiceField(command))
            .addMethod(createInjectConstructor())
            .addMethod(createRunMethod(command))
            .build()
    }

    private fun createServiceField(command: CommandInfo): FieldSpec {
        return FieldSpec.builder(TypeName.get(command.service.type), SERVICE_FIELD_NAME)
            .addAnnotation(Inject::class.java)
            .build()
    }

    private fun createInjectConstructor(): MethodSpec {
        return MethodSpec.constructorBuilder()
            .addAnnotation(Inject::class.java)
            .build()
    }

    private fun createRunMethod(command: CommandInfo): MethodSpec {
        return MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(BYTE_ARRAY_TYPE_NAME, RAW_PARAMETER_NAME).build())
            .returns(ArrayTypeName.of(TypeName.BYTE))
            .beginControlFlow("try")
            .addStatement("\$T \$L = \$T.parseFrom(\$L)",
                command.returnType, PARSED_PARAMETER_NAME, command.parameterType, RAW_PARAMETER_NAME)
            .addStatement("\$T \$L = \$L.\$L(\$L)",
                command.returnType, RETURN_VALUE_NAME, SERVICE_FIELD_NAME, command.name, PARSED_PARAMETER_NAME)
            .addStatement("return \$L.toByteArray()", RETURN_VALUE_NAME)
            .nextControlFlow("catch (\$T \$L)", IOException::class.java, EXCEPTION_NAME)
            .addStatement("throw new \$T(\$L)", RuntimeException::class.java, EXCEPTION_NAME)
            .endControlFlow()
            .build()
    }

    companion object {
        private val BYTE_ARRAY_TYPE_NAME = ArrayTypeName.of(TypeName.BYTE)
        private const val SERVICE_FIELD_NAME = "mService"
        private const val RAW_PARAMETER_NAME = "rawParameter"
        private const val PARSED_PARAMETER_NAME = "parameter"
        private const val RETURN_VALUE_NAME = "returnValue"
        private const val EXCEPTION_NAME = "exception"
    }
}
