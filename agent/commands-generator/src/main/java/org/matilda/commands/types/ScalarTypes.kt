package org.matilda.commands.types

import com.google.protobuf.ByteString
import com.google.protobuf.BytesValue
import com.google.protobuf.DoubleValue
import com.google.protobuf.FloatValue
import com.google.protobuf.Int32Value
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.sun.jdi.BooleanValue
import org.matilda.commands.python.PrimitiveTypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.utils.Package
import javax.lang.model.type.TypeMirror

data class ScalarTypeInfo(val protobufWrapperJavaType: Class<*>, val pythonType: PrimitiveTypeName)

val SCALAR_TYPE_MAP = mapOf(
    TypeName.DOUBLE to ScalarTypeInfo(DoubleValue::class.java, PrimitiveTypeName("float")),
    TypeName.FLOAT to ScalarTypeInfo(FloatValue::class.java, PrimitiveTypeName("float")),
    TypeName.INT to ScalarTypeInfo(Int32Value::class.java, PrimitiveTypeName("int")),
    TypeName.LONG to ScalarTypeInfo(Int64Value::class.java, PrimitiveTypeName("int")),
    TypeName.BOOLEAN to ScalarTypeInfo(BooleanValue::class.java, PrimitiveTypeName("bool")),
    ClassName.get(java.lang.String::class.java) to ScalarTypeInfo(StringValue::class.java, PrimitiveTypeName("str")),
    ClassName.get(ByteString::class.java) to ScalarTypeInfo(BytesValue::class.java, PrimitiveTypeName("bytes")),
)

fun TypeName.isScalarType() = this in SCALAR_TYPE_MAP
fun TypeMirror.isScalarType() = TypeName.get(this).isScalarType()
val TypeName.protobufWrapperJavaType
    get() = SCALAR_TYPE_MAP[this]!!.protobufWrapperJavaType
val TypeMirror.protobufWrapperJavaType
    get() = TypeName.get(this).protobufWrapperJavaType
val TypeName.pythonType
    get() = SCALAR_TYPE_MAP[this]!!.pythonType
val TypeName.wrapperTypeName: String
    get() = protobufWrapperJavaType.simpleName
val TypeMirror.wrapperTypeName
    get() = TypeName.get(this).wrapperTypeName
val TypeName.protobufWrapperPythonType
    get() = PythonClassName(Package("google", "protobuf", "wrappers_pb2"), wrapperTypeName)
val TypeMirror.protobufWrapperPythonType
    get() = TypeName.get(this).protobufWrapperPythonType
