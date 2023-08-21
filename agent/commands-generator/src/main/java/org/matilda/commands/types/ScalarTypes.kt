package org.matilda.commands.types

import com.google.protobuf.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PrimitiveTypeName
import org.matilda.commands.python.PythonTypeName
import javax.lang.model.type.TypeMirror

data class ScalarTypeInfo(val protobufWrapperJavaType: Class<*>,
                          val javaConverterType: Class<*>,
                          val pythonType: PrimitiveTypeName)

val SCALAR_TYPE_MAP = mapOf(
    TypeName.DOUBLE to ScalarTypeInfo(DoubleValue::class.java,
        DoubleConverter::class.java,
        PythonTypeName.FLOAT),
    TypeName.FLOAT to ScalarTypeInfo(FloatValue::class.java,
        FloatConverter::class.java,
        PythonTypeName.FLOAT),
    TypeName.INT to ScalarTypeInfo(Int32Value::class.java,
        IntConverter::class.java,
        PythonTypeName.INT),
    TypeName.LONG to ScalarTypeInfo(Int64Value::class.java,
        LongConverter::class.java,
        PythonTypeName.INT),
    TypeName.BOOLEAN to ScalarTypeInfo(BoolValue::class.java,
        BooleanConverter::class.java,
        PythonTypeName.BOOL),
    ClassName.get(java.lang.String::class.java) to ScalarTypeInfo(StringValue::class.java,
        StringConverter::class.java,
        PythonTypeName.STR),
    ClassName.get(ByteString::class.java) to ScalarTypeInfo(BytesValue::class.java,
        ByteStringConverter::class.java,
        PythonTypeName.BYTES),
)

fun TypeMirror.isScalar() = TypeName.get(this) in SCALAR_TYPE_MAP

val TypeMirror.scalarProtobufWrapperJavaType
    get() = SCALAR_TYPE_MAP[TypeName.get(this)]!!.protobufWrapperJavaType


val TypeMirror.scalarJavaConverterType
    get() = SCALAR_TYPE_MAP[TypeName.get(this)]!!.javaConverterType


val TypeMirror.scalarPythonType
    get() = SCALAR_TYPE_MAP[TypeName.get(this)]!!.pythonType
