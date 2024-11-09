package org.matilda.commands.types

import androidx.room.compiler.processing.XType
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject

class DynamicServiceTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeUtilities: TypeUtilities

    override fun javaConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("\$L.\$L",
            listOf(JAVA_DEPENDENCIES_FIELD_NAME, type.javaDynamicServiceConverterFieldName),
            listOf(JavaDependencyInfo(type.javaDynamicServiceConverterTypeName, type.javaDynamicServiceConverterFieldName))
        )
    }

    private val XType.javaDynamicServiceConverterFieldName
        get() = StringUtils.uncapitalize(javaDynamicServiceConverterTypeName.simpleName())

    private val XType.javaDynamicServiceConverterTypeName
        get() = mNameGenerator.forService(typeName.toString()).dynamicServiceConverterClassName
    override fun pythonConverter(type: XType, outerConverter: TypeConverter): PythonTypeConverterInfo {
        return PythonTypeConverterInfo(
            "self.$PYTHON_DEPENDENCIES_FIELD_NAME.${type.pythonDynamicServiceConverterFieldName}",
            emptyList(),
            listOf(PythonDependencyInfo(type.pythonDynamicServiceConverterTypeName,
                type.pythonDynamicServiceConverterFieldName)))
    }

    private val XType.pythonDynamicServiceConverterFieldName
        get() = pythonDynamicServiceConverterTypeName.name.toSnakeCase()
    private val XType.pythonDynamicServiceConverterTypeName
        get() = mNameGenerator.forService(typeName.toString()).dynamicServiceConverterPythonClassName
    override fun pythonType(type: XType, outerConverter: TypeConverter) =
        mNameGenerator.forService(type.typeName.toString()).serviceFullClassName

    override fun isSupported(type: XType, outerConverter: TypeConverter) =
        mTypeUtilities.isAnnotatedWith(type, MatildaDynamicService::class)

    override val supportedTypesDescription: String
        get() = "Dynamic Services"


    companion object {
        val DYNAMIC_CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("dynamic_service_converter"),
            "DynamicServiceConverter")
        const val JAVA_DEPENDENCIES_FIELD_NAME = "mDependencies"
        const val PYTHON_DEPENDENCIES_FIELD_NAME = "__dependencies"
    }
}