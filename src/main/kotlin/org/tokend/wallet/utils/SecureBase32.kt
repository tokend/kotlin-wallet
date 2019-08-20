package org.tokend.wallet.utils

import kotlin.experimental.or

object SecureBase32 {
    private val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    private val base32Lookup = intArrayOf(0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
            0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14,
            0x15, 0x16, 0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF)

    /**
     * Encodes byte array to Base32.
     *
     * @param bytes Bytes to encode.
     */
    fun encode(bytes: ByteArray): CharArray {
        var i = 0
        var index = 0
        var digit: Int
        var currByte: Int
        var nextByte: Int
        val writer = SecureCharArrayWriter((bytes.size + 7) * 8 / 5)

        while (i < bytes.size) {
            currByte = if (bytes[i] >= 0) bytes[i].toInt() else bytes[i] + 256

            /* Is the current digit going to span a byte boundary? */
            if (index > 3) {
                if (i + 1 < bytes.size) {
                    nextByte = if (bytes[i + 1] >= 0)
                        bytes[i + 1].toInt()
                    else
                        bytes[i + 1] + 256
                } else {
                    nextByte = 0
                }

                digit = currByte and (0xFF shr index)
                index = (index + 5) % 8
                digit = digit shl index
                digit = digit or (nextByte shr 8 - index)
                i++
            } else {
                digit = currByte shr 8 - (index + 5) and 0x1F
                index = (index + 5) % 8
                if (index == 0)
                    i++
            }
            writer.append(base32Chars[digit])
        }

        val result = writer.toCharArray()
        writer.erase()

        return result
    }

    /**
     * Decodes the given Base32 chars to a raw byte array.
     *
     * @param base32 content to decode
     */
    fun decode(base32: CharArray): ByteArray {
        var i: Int
        var index: Int
        var lookup: Int
        var offset: Int
        var digit: Int
        val bytes = ByteArray(base32.size * 5 / 8)

        i = 0
        index = 0
        offset = 0
        while (i < base32.size) {
            lookup = base32[i] - '0'

            /* Skip chars outside the lookup table */
            if (lookup < 0 || lookup >= base32Lookup.size) {
                i++
                continue
            }

            digit = base32Lookup[lookup]

            /* If this digit is not in the table, ignore it */
            if (digit == 0xFF) {
                i++
                continue
            }

            if (index <= 3) {
                index = (index + 5) % 8
                if (index == 0) {
                    bytes[offset] = bytes[offset] or digit.toByte()
                    offset++
                    if (offset >= bytes.size)
                        break
                } else {
                    bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
                }
            } else {
                index = (index + 5) % 8
                bytes[offset] = bytes[offset] or digit.ushr(index).toByte()
                offset++

                if (offset >= bytes.size) {
                    break
                }
                bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
            }
            i++
        }
        return bytes
    }
}