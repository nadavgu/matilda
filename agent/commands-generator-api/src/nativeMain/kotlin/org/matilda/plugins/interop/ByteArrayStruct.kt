@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.*

fun CValue<ByteArrayStruct>.toByteArray() = useContents {
    ByteArray(size) { index ->
        data!![index]
    }
}

fun CValue<ByteArrayStruct>.free() = useContents {
    freeData!!(data)
    data = null
}

fun <R> CValue<ByteArrayStruct>.use(block: CValue<ByteArrayStruct>.() -> R): R = try {
    block()
} finally {
    free()
}

fun CValue<ByteArrayStruct>.moveToByteArray() = use {
    toByteArray()
}

fun ByteArray.toByteArrayStruct() = cValue<ByteArrayStruct> {
    size = this@toByteArrayStruct.size
    data = nativeHeap.allocArray(size) { index ->
        value = get(index)
    }
    freeData = staticCFunction { data ->
        data?.apply {
            nativeHeap.free(this)
        }
    }
}

fun <R> ByteArray.tempByteArrayStruct(block: (CValue<ByteArrayStruct>) -> R): R = toByteArrayStruct().use(block)