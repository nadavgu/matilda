@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.plugins.interop.toByteArrayStruct

fun CValue<ByteArrayStruct>.toByteArray() = useContents {
    toByteArray()
}

fun ByteArrayStruct.toByteArray() = ByteArray(size) { index ->
    data!![index]
}

fun CValue<ByteArrayStruct>.free() = useContents {
    free()
}

fun ByteArrayStruct.free() {
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
    initializeFrom(this@toByteArrayStruct)
}

fun ByteArrayStruct.initializeFrom(byteArray: ByteArray) {
    size = byteArray.size
    data = nativeHeap.allocArray(size) { index ->
        value = byteArray[index]
    }
    freeData = staticCFunction { data ->
        data?.apply {
            nativeHeap.free(this)
        }
    }
}

fun <R> ByteArray.tempByteArrayStruct(block: (CValue<ByteArrayStruct>) -> R): R = toByteArrayStruct().use(block)