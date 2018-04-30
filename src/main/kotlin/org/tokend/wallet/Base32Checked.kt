package org.tokend.wallet

import com.google.common.io.BaseEncoding
import java.io.ByteArrayOutputStream
import java.io.CharArrayWriter
import java.io.IOException
import java.util.*

internal object Base32Checked {
    /**
     * Indicates that there was a problem decoding base32-checked encoded string.
     * @see Account
     */
    class FormatException(message: String) : RuntimeException(message)

    private val base32Encoding = BaseEncoding.base32().upperCase().omitPadding()

    enum class VersionByte constructor(// X
            private val value: Byte) {
        ACCOUNT_ID((6 shl 3).toByte()), // G
        SEED((18 shl 3).toByte()), // S
        BALANCE_ID((1 shl 3).toByte()); // B

        fun getValue(): Int {
            return value.toInt()
        }
    }

    fun encodeAccountId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.ACCOUNT_ID, data))
    }

    fun decodeAccountId(data: String): ByteArray {
        return decodeCheck(VersionByte.ACCOUNT_ID, data.toCharArray())
    }

    fun encodeSecretSeed(data: ByteArray): String {
        return String(encodeCheck(VersionByte.SEED, data))
    }

    fun decodeSecretSeed(data: String): ByteArray {
        return decodeCheck(VersionByte.SEED, data.toCharArray())
    }

    fun encodeBalanceId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.BALANCE_ID, data))
    }

    fun decodeBalanceId(data: String): ByteArray {
        return decodeCheck(VersionByte.BALANCE_ID, data.toCharArray())
    }

    fun encodeCheck(versionByte: VersionByte, data: ByteArray): CharArray {
        try {
            val outputStream = ByteArrayOutputStream()
            outputStream.write(versionByte.getValue())
            outputStream.write(data)
            val payload = outputStream.toByteArray()
            val checksum = Base32Checked.calculateChecksum(payload)
            outputStream.write(checksum)
            val unencoded = outputStream.toByteArray()

            // Why not use base32Encoding.encode here?
            // We don't want secret seed to be stored as String in memory because of security reasons. It's impossible
            // to erase it from memory when we want it to be erased (ASAP).
            val charArrayWriter = CharArrayWriter(unencoded.size)
            val charOutputStream = Base32Checked.base32Encoding.encodingStream(charArrayWriter)
            charOutputStream.write(unencoded)
            val charsEncoded = charArrayWriter.toCharArray()

            if (VersionByte.SEED == versionByte) {
                Arrays.fill(unencoded, 0.toByte())
                Arrays.fill(payload, 0.toByte())
                Arrays.fill(checksum, 0.toByte())

                // Clean charArrayWriter internal buffer
                val bufferSize = charArrayWriter.size()
                val zeros = CharArray(bufferSize)
                Arrays.fill(zeros, '0')
                charArrayWriter.reset()
                charArrayWriter.write(zeros)
            }

            return charsEncoded
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    fun decodeCheck(versionByte: VersionByte, encoded: CharArray): ByteArray {
        val bytes = ByteArray(encoded.size)
        for (i in encoded.indices) {
            if (encoded[i].toInt() > 127) {
                throw IllegalArgumentException("Illegal characters in encoded char array.")
            }
            bytes[i] = encoded[i].toByte()
        }

        val decoded = Base32Checked.base32Encoding.decode(java.nio.CharBuffer.wrap(encoded))
        val decodedVersionByte = decoded[0]
        val payload = Arrays.copyOfRange(decoded, 0, decoded.size - 2)
        val data = Arrays.copyOfRange(payload, 1, payload.size)
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 2, decoded.size)

        if (decodedVersionByte.toInt() != versionByte.getValue()) {
            throw FormatException("Version byte is invalid")
        }

        val expectedChecksum = Base32Checked.calculateChecksum(payload)

        if (!Arrays.equals(expectedChecksum, checksum)) {
            throw FormatException("Checksum invalid")
        }

        if (VersionByte.SEED.getValue() == decodedVersionByte.toInt()) {
            Arrays.fill(bytes, 0.toByte())
            Arrays.fill(decoded, 0.toByte())
            Arrays.fill(payload, 0.toByte())
        }

        return data
    }

    fun calculateChecksum(bytes: ByteArray): ByteArray {
        // This code calculates CRC16-XModem checksum
        // Ported from https://github.com/alexgorbatchev/node-crc
        var crc = 0x0000
        var count = bytes.size
        var i = 0
        var code: Int

        while (count > 0) {
            code = crc.ushr(8) and 0xFF
            code = code xor (bytes[i++].toInt() and 0xFF)
            code = code xor code.ushr(4)
            crc = crc shl 8 and 0xFFFF
            crc = crc xor code
            code = code shl 5 and 0xFFFF
            crc = crc xor code
            code = code shl 7 and 0xFFFF
            crc = crc xor code
            count--
        }

        // little-endian
        return byteArrayOf(crc.toByte(), crc.ushr(8).toByte())
    }
}