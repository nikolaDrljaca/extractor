package com.drbrosdev.extractor.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Convert bitmap to byte array using ByteBuffer.
 */
fun Bitmap.convertToByteArray(): ByteArray {
    //minimum number of bytes that can be used to store this bitmaps pixels
    val size = this.byteCount

    //allocate new instances which will hold bitmap
    val buffer = ByteBuffer.allocate(size)
    val bytes = ByteArray(size)

    //copy the bitmaps pixels into the specified buffer
    this.copyPixelsToBuffer(buffer)

    //rewinds buffer (buffer position is set to zero and the mark is discarded)
    buffer.rewind()

    //transfer bytes from buffer into the given destination array
    buffer.get(bytes)

    return bytes
}

fun Bitmap.convertToByteArrayUsingStream(): ByteArray {
    return ByteArrayOutputStream().use {
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
        it.toByteArray()
    }
}
