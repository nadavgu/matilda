package org.matilda.commands.types

import com.google.protobuf.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PrimitiveTypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.utils.Package
import javax.lang.model.type.TypeMirror

data class ScalarTypeInfo(val protobufWrapperJavaType: Class<*>,
                          val javaConverterType: Class<*>,
                          val pythonType: PrimitiveTypeName)

val SCALAR_TYPE_MAP = mapOf(
    TypeName.DOUBLE to ScalarTypeInfo(DoubleValue::class.java,
        DoubleConverter::class.java,
        PrimitiveTypeName("float")),
    TypeName.FLOAT to ScalarTypeInfo(FloatValue::class.java,
        FloatConverter::class.java,
        PrimitiveTypeName("float")),
    TypeName.INT to ScalarTypeInfo(Int32Value::class.java,
        IntConverter::class.java,
        PrimitiveTypeName("int")),
    TypeName.LONG to ScalarTypeInfo(Int64Value::class.java,
        LongConverter::class.java,
        PrimitiveTypeName("int")),
    TypeName.BOOLEAN to ScalarTypeInfo(BoolValue::class.java,
        BooleanConverter::class.java,
        PrimitiveTypeName("bool")),
    ClassName.get(java.lang.String::class.java) to ScalarTypeInfo(StringValue::class.java,
        StringConverter::class.java,
        PrimitiveTypeName("str")),
    ClassName.get(ByteString::class.java) to ScalarTypeInfo(BytesValue::class.java,
        ByteStringConverter::class.java,
        PrimitiveTypeName("bytes")),
)

fun TypeName.isScalarType() = this in SCALAR_TYPE_MAP
fun TypeMirror.isScalarType() = TypeName.get(this).isScalarType()
val TypeName.protobufWrapperJavaType
    get() = SCALAR_TYPE_MAP[this]!!.protobufWrapperJavaType
val TypeName.wrapperTypeName: String
    get() = protobufWrapperJavaType.simpleName
val TypeName.protobufWrapperPythonType
    get() = PythonClassName(Package("google", "protobuf", "wrappers_pb2"), wrapperTypeName)
val TypeMirror.protobufWrapperPythonType
    get() = TypeName.get(this).protobufWrapperPythonType
