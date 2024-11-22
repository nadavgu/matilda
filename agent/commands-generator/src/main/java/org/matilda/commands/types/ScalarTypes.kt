package org.matilda.commands.types

import androidx.room.compiler.codegen.XTypeName
import androidx.room.compiler.codegen.asClassName
import androidx.room.compiler.processing.XType
import pbandk.wkt.*
import org.matilda.commands.python.PrimitiveTypeName
import org.matilda.commands.python.PythonTypeName
import pbandk.ByteArr

data class ScalarTypeInfo(val protobufWrapperJavaType: Class<*>,
                          val javaConverterType: Class<*>,
                          val pythonType: PrimitiveTypeName)

val PRIMITIVE_TYPE_MAP = mapOf(
    XTypeName.PRIMITIVE_DOUBLE to ScalarTypeInfo(DoubleValue::class.java,
        DoubleConverter::class.java,
        PythonTypeName.FLOAT),
    XTypeName.PRIMITIVE_FLOAT to ScalarTypeInfo(FloatValue::class.java,
        FloatConverter::class.java,
        PythonTypeName.FLOAT),
    XTypeName.PRIMITIVE_INT to ScalarTypeInfo(Int32Value::class.java,
        IntConverter::class.java,
        PythonTypeName.INT),
    XTypeName.PRIMITIVE_LONG to ScalarTypeInfo(Int64Value::class.java,
        LongConverter::class.java,
        PythonTypeName.INT),
    XTypeName.PRIMITIVE_BOOLEAN to ScalarTypeInfo(BoolValue::class.java,
        BooleanConverter::class.java,
        PythonTypeName.BOOL),
    String::class.asClassName() to ScalarTypeInfo(StringValue::class.java,
        StringConverter::class.java,
        PythonTypeName.STR),
    ByteArr::class.asClassName() to ScalarTypeInfo(BytesValue::class.java,
        ByteArrConverter::class.java,
        PythonTypeName.BYTES),
    XTypeName.getArrayName(XTypeName.PRIMITIVE_BYTE) to ScalarTypeInfo(BytesValue::class.java,
        ByteArrayConverter::class.java,
        PythonTypeName.BYTES),
)

val BOXED_TYPE_MAP = mapOf(
    XTypeName.BOXED_DOUBLE to PRIMITIVE_TYPE_MAP[XTypeName.PRIMITIVE_DOUBLE],
    XTypeName.BOXED_FLOAT to PRIMITIVE_TYPE_MAP[XTypeName.PRIMITIVE_FLOAT],
    XTypeName.BOXED_INT to PRIMITIVE_TYPE_MAP[XTypeName.PRIMITIVE_INT],
    XTypeName.BOXED_LONG to PRIMITIVE_TYPE_MAP[XTypeName.PRIMITIVE_LONG],
    XTypeName.BOXED_BOOLEAN to PRIMITIVE_TYPE_MAP[XTypeName.PRIMITIVE_BOOLEAN],
)

val SCALAR_TYPE_MAP = PRIMITIVE_TYPE_MAP + BOXED_TYPE_MAP

fun XType.isScalar() = asTypeName() in SCALAR_TYPE_MAP

val XType.scalarProtobufWrapperJavaType
    get() = SCALAR_TYPE_MAP[asTypeName()]!!.protobufWrapperJavaType


val XType.scalarJavaConverterType
    get() = SCALAR_TYPE_MAP[asTypeName()]!!.javaConverterType


val XType.scalarPythonType
    get() = SCALAR_TYPE_MAP[asTypeName()]!!.pythonType
