package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.matilda.commands.MatildaDynamicService
import org.matilda.commands.names.NameGenerator
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.utils.toSnakeCase
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class DynamicServiceTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mNameGenerator: NameGenerator

    @Inject
    lateinit var mTypeUtilities: TypeUtilities

    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("\$L.\$L",
            listOf(JAVA_DEPENDENCIES_FIELD_NAME, type.javaDynamicServiceConverterFieldName),
            listOf(JavaDependencyInfo(type.javaDynamicServiceConverterTypeName, type.javaDynamicServiceConverterFieldName))
        )
    }

    private val TypeMirror.javaDynamicServiceConverterFieldName
        get() = StringUtils.uncapitalize(javaDynamicServiceConverterTypeName.simpleName())

    private val TypeMirror.javaDynamicServiceConverterTypeName
        get() = mNameGenerator.forService(TypeName.get(this).toString()).dynamicServiceConverterClassName
    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): PythonTypeConverterInfo {
        return PythonTypeConverterInfo(
            "self.$PYTHON_DEPENDENCIES_FIELD_NAME.${type.pythonDynamicServiceConverterFieldName}",
            emptyList(),
            listOf(PythonDependencyInfo(type.pythonDynamicServiceConverterTypeName,
                type.pythonDynamicServiceConverterFieldName)))
    }

    private val TypeMirror.pythonDynamicServiceConverterFieldName
        get() = pythonDynamicServiceConverterTypeName.name.toSnakeCase()
    private val TypeMirror.pythonDynamicServiceConverterTypeName
        get() = mNameGenerator.forService(TypeName.get(this).toString()).dynamicServiceConverterPythonClassName
    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) =
        mNameGenerator.forService(TypeName.get(type).toString()).serviceFullClassName

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =
        mTypeUtilities.isAnnotatedWith(type, MatildaDynamicService::class.java)

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