package org.tokend.wallet.utils

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

/**
 * Converts current char sequence to raw bytes representation by UTF-8.
 */
fun CharArray.toByteArray(): ByteArray {
    val charBuffer = CharBuffer.wrap(this)
    val byteBuffer = Charset.forName("UTF-8").encode(charBuffer)
    charBuffer.clear()
    val bytes = ByteArray(byteBuffer.remaining())
    byteBuffer.get(bytes).clear()
    return bytes
}

/**
 * Converts current byte sequence to chars by UTF-8.
 */
fun ByteArray.toCharArray(): CharArray {
    val byteBuffer = ByteBuffer.wrap(this)
    val charBuffer = Charset.forName("UTF-8").decode(byteBuffer)
    byteBuffer.clear()
    val chars = CharArray(charBuffer.remaining())
    charBuffer.get(chars).clear()
    return chars
}