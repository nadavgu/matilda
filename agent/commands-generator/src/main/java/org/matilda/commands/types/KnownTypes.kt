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

data class KnownTypeInfo(val protobufWrapperType: Class<*>, val pythonType: PrimitiveTypeName)

val KNOWN_TYPE_MAP = mapOf(
    TypeName.DOUBLE to KnownTypeInfo(DoubleValue::class.java, PrimitiveTypeName("float")),
    TypeName.FLOAT to KnownTypeInfo(FloatValue::class.java, PrimitiveTypeName("float")),
    TypeName.INT to KnownTypeInfo(Int32Value::class.java, PrimitiveTypeName("int")),
    TypeName.LONG to KnownTypeInfo(Int64Value::class.java, PrimitiveTypeName("int")),
    TypeName.BOOLEAN to KnownTypeInfo(BooleanValue::class.java, PrimitiveTypeName("bool")),
    ClassName.get(java.lang.String::class.java) to KnownTypeInfo(StringValue::class.java, PrimitiveTypeName("str")),
    ClassName.get(ByteString::class.java) to KnownTypeInfo(BytesValue::class.java, PrimitiveTypeName("bytes")),
)
