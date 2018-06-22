package org.tokend.wallet

import org.apache.commons.codec.binary.Base32
import org.tokend.wallet.utils.toByteArray
import org.tokend.wallet.utils.toCharArray
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

/**
 * Performs encoding and decoding of specific data to Base32Check.
 * Base32Check is Base32 encoding with version byte and checksum
 * [version byte] + [data] + [CRC16 checksum of version byte and data]
 */
object Base32Check {
    /**
     * Indicates that there was a problem decoding base32-checked encoded string.
     */
    class FormatException(message: String) : RuntimeException(message)

    private val base32Encoding = Base32()

    enum class VersionByte constructor(private val value: Byte) {
        ACCOUNT_ID(48.toByte()),  // G
        SEED(144.toByte()), // S
        BALANCE_ID(8.toByte());   // B

        fun getValue(): Int {
            return value.toInt()
        }

        companion object {
            @JvmStatic
            fun valueOf(value: Byte): VersionByte? {
                return when (value) {
                    ACCOUNT_ID.value -> ACCOUNT_ID
                    SEED.value -> SEED
                    BALANCE_ID.value -> BALANCE_ID
                    else -> null
                }
            }
        }
    }

    @JvmStatic
    fun isValid(versionByte: VersionByte, data: CharArray): Boolean {
        try {
            decodeCheck(versionByte, data)
            return true
        } catch (_: Exception) {
            return false
        }
    }

    @JvmStatic
    fun encodeAccountId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.ACCOUNT_ID, data))
    }

    @JvmStatic
    fun decodeAccountId(data: String): ByteArray {
        return decodeCheck(VersionByte.ACCOUNT_ID, data.toCharArray())
    }

    @JvmStatic
    fun encodeSecretSeed(data: ByteArray): CharArray {
        return encodeCheck(VersionByte.SEED, data)
    }

    @JvmStatic
    fun decodeSecretSeed(data: CharArray): ByteArray {
        return decodeCheck(VersionByte.SEED, data)
    }

    @JvmStatic
    fun encodeBalanceId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.BALANCE_ID, data))
    }

    @JvmStatic
    fun decodeBalanceId(data: String): ByteArray {
        return decodeCheck(VersionByte.BALANCE_ID, data.toCharArray())
    }

    @JvmStatic
    fun encodeCheck(versionByte: VersionByte, data: ByteArray): CharArray {
        try {
            val outputStream = ByteArrayOutputStream()
            outputStream.write(versionByte.getValue())
            outputStream.write(data)
            val payload = outputStream.toByteArray()
            val checksum = Base32Check.calculateChecksum(payload)
            outputStream.write(checksum)
            val unencoded = outputStream.toByteArray()

//            // Why not use base32Encoding.encode here?
//            // We don't want secret seed to be stored as String in memory because of security reasons. It's impossible
//            // to erase it from memory when we want it to be erased (ASAP).
//            val charArrayWriter = CharArrayWriter(unencoded.size)
//            val charOutputStream = Base32Check.base32Encoding.encodingStream(charArrayWriter)
//            charOutputStream.write(unencoded)
//            val charsEncoded = charArrayWriter.toCharArray()
            val encoded = base32Encoding.encode(unencoded)
            val charsEncoded = encoded.toCharArray()

            if (VersionByte.SEED == versionByte) {
                Arrays.fill(unencoded, 0.toByte())
                Arrays.fill(payload, 0.toByte())
                Arrays.fill(checksum, 0.toByte())
                Arrays.fill(encoded, 0)
            }

            return charsEncoded
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    @JvmStatic
    fun decodeCheck(versionByte: VersionByte, encoded: CharArray): ByteArray {
        val decodingResult = decode(encoded)

        if (versionByte != decodingResult.first)
            throw FormatException("Version byte is invalid")

        return decodingResult.second
    }

    @JvmStatic
    fun decode(encoded: CharArray): Pair<VersionByte, ByteArray> {
        val bytes = ByteArray(encoded.size)
        for (i in encoded.indices) {
            if (encoded[i].toInt() > 127) {
                throw IllegalArgumentException("Illegal characters in encoded char array.")
            }
            bytes[i] = encoded[i].toByte()
        }

        val decoded = Base32Check.base32Encoding.decode(encoded.toByteArray())
        val decodedVersionByte = decoded[0]
        val payload = Arrays.copyOfRange(decoded, 0, decoded.size - 2)
        val data = Arrays.copyOfRange(payload, 1, payload.size)
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 2, decoded.size)

        val expectedChecksum = Base32Check.calculateChecksum(payload)

        if (!Arrays.equals(expectedChecksum, checksum)) {
            throw FormatException("Checksum invalid")
        }

        if (VersionByte.SEED.getValue() == decodedVersionByte.toInt()) {
            Arrays.fill(bytes, 0.toByte())
            Arrays.fill(decoded, 0.toByte())
            Arrays.fill(payload, 0.toByte())
        }

        val versionByte = VersionByte.valueOf(decodedVersionByte)
                ?: throw FormatException("Version byte is invalid")

        return Pair(versionByte, data)
    }

    @JvmStatic
    private fun calculateChecksum(bytes: ByteArray): ByteArray {
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