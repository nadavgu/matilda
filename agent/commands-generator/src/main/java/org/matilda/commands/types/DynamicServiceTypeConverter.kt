package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.CommandRepository
import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.processors.PythonServiceClassGenerator.Companion.COMMAND_RUNNER_FIELD_NAME
import org.matilda.commands.processors.RawCommandClassGenerator.Companion.COMMAND_DEPENDENCIES_FIELD_NAME
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class DynamicServiceTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeUtilities: TypeUtilities

    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("new \$T<>(\$L.\$L, \$L.\$L)",
            listOf(TypeName.get(DynamicServiceConverter::class.java),
                COMMAND_DEPENDENCIES_FIELD_NAME, COMMAND_REPOSITORY_VARIABLE_NAME,
                COMMAND_DEPENDENCIES_FIELD_NAME, getCommandRegistryFactoryFieldName(type)),
            listOf(DependencyInfo(TypeName.get(CommandRepository::class.java), COMMAND_REPOSITORY_VARIABLE_NAME),
                DependencyInfo(getCommandRegistryFactoryTypeName(type), getCommandRegistryFactoryFieldName(type)))
        )
    }

    private fun getCommandRegistryFactoryFieldName(type: TypeMirror) =
        StringUtils.uncapitalize(mNameGenerator.forService(TypeName.get(type).toString())
            .commandRegistryFactoryClassName)

    private fun getCommandRegistryFactoryTypeName(type: TypeMirror) =
        mNameGenerator.forService(TypeName.get(type).toString()).commandRegistryFactoryTypeName

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<PythonTypeName>> {
        val proxyServiceType = mNameGenerator.forService(TypeName.get(type).toString()).serviceProxyFullClassName
        return Pair("${CONVERTER_CLASS.name}(${proxyServiceType.name}, self.$COMMAND_RUNNER_FIELD_NAME)",
            listOf(CONVERTER_CLASS, proxyServiceType))
    }

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) =
        mNameGenerator.forService(TypeName.get(type).toString()).serviceFullClassName

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =
        mTypeUtilities.isAnnotatedWith(type, MatildaDynamicService::class.java)

    override val supportedTypesDescription: String
        get() = "Dynamic Services"


    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("dynamic_service_converter"),
            "DynamicServiceConverter")
        private const val COMMAND_REPOSITORY_VARIABLE_NAME = "commandRepository"
    }
}