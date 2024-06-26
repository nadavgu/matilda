package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.processors.PythonServiceProxyClassGenerator.Companion.COMMAND_RUNNER_FIELD_NAME
import org.matilda.commands.python.PythonClassName
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class DynamicServiceTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeUtilities: TypeUtilities

    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("\$L.\$L",
            listOf(DEPENDENCIES_FIELD_NAME, type.dynamicServiceConverterFieldName),
            listOf(JavaDependencyInfo(type.dynamicServiceConverterTypeName, type.dynamicServiceConverterFieldName))
        )
    }

    private val TypeMirror.dynamicServiceConverterFieldName
        get() = StringUtils.uncapitalize(mNameGenerator.forService(TypeName.get(this).toString())
            .dynamicServiceConverterClassName)

    private val TypeMirror.dynamicServiceConverterTypeName
        get() = mNameGenerator.forService(TypeName.get(this).toString()).dynamicServiceConverterTypeName
    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): PythonTypeConverterInfo {
        val proxyServiceType = mNameGenerator.forService(TypeName.get(type).toString()).serviceProxyFullClassName
        return PythonTypeConverterInfo(
            "${CONVERTER_CLASS.name}(${proxyServiceType.name}, self.$COMMAND_RUNNER_FIELD_NAME)",
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
        const val DEPENDENCIES_FIELD_NAME = "mDependencies"
    }
}