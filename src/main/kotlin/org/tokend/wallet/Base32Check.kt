package org.tokend.wallet

import org.tokend.crypto.ecdsa.erase
import org.tokend.wallet.utils.SecureBase32
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

/**
 * Performs encoding and decoding of specific data to Base32Check.
 * Base32Check is Base32 encoding with version byte and checksum:
 * &#91;version byte&#93; + &#91;data&#93; + &#91;CRC16 checksum of version byte and data&#93;
 */
object Base32Check {
    /**
     * Indicates that there was a problem decoding base32-checked encoded string.
     */
    class FormatException(message: String) : RuntimeException(message)

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

    /**
     * @return [true] if encoded data is related to the given version byte
     * and can be decoded, [false] otherwise.
     */
    @JvmStatic
    fun isValid(versionByte: VersionByte, data: CharArray): Boolean {
        try {
            decodeCheck(versionByte, data)
            return true
        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Encodes given data using [VersionByte.ACCOUNT_ID] version byte.
     */
    @JvmStatic
    fun encodeAccountId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.ACCOUNT_ID, data))
    }

    /**
     * Decodes given data using [VersionByte.ACCOUNT_ID] version byte.
     */
    @JvmStatic
    fun decodeAccountId(data: String): ByteArray {
        return decodeCheck(VersionByte.ACCOUNT_ID, data.toCharArray())
    }

    /**
     * Encodes given data using [VersionByte.SEED] version byte.
     */
    @JvmStatic
    fun encodeSecretSeed(data: ByteArray): CharArray {
        return encodeCheck(VersionByte.SEED, data)
    }

    /**
     * Decodes given data using [VersionByte.SEED] version byte.
     */
    @JvmStatic
    fun decodeSecretSeed(data: CharArray): ByteArray {
        return decodeCheck(VersionByte.SEED, data)
    }

    /**
     * Encodes given data using [VersionByte.BALANCE_ID] version byte.
     */
    @JvmStatic
    fun encodeBalanceId(data: ByteArray): String {
        return String(encodeCheck(VersionByte.BALANCE_ID, data))
    }

    /**
     * Decodes given data using [VersionByte.BALANCE_ID] version byte.
     */
    @JvmStatic
    fun decodeBalanceId(data: String): ByteArray {
        return decodeCheck(VersionByte.BALANCE_ID, data.toCharArray())
    }

    /**
     * Encodes given data using given version byte.
     */
    @JvmStatic
    fun encodeCheck(versionByte: VersionByte, data: ByteArray): CharArray {
        try {
            val outputStream = ByteArrayOutputStream()
            outputStream.write(versionByte.getValue())
            outputStream.write(data)
            val payload = outputStream.toByteArray()
            val checksum = calculateChecksum(payload)
            outputStream.write(checksum)
            val unencoded = outputStream.toByteArray()

            val charsEncoded = SecureBase32.encode(unencoded)

            if (VersionByte.SEED == versionByte) {
                unencoded.erase()
                payload.erase()
                checksum.erase()
            }

            return charsEncoded
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    /**
     * Decodes given data using given version byte.
     */
    @JvmStatic
    fun decodeCheck(versionByte: VersionByte, encoded: CharArray): ByteArray {
        val decodingResult = decode(encoded)

        if (versionByte != decodingResult.first)
            throw FormatException("Version byte is invalid")

        return decodingResult.second
    }

    /**
     * Decodes given data and obtains it's version byte.
     *
     * @return [Pair] of the version byte and decoded data
     */
    @JvmStatic
    fun decode(encoded: CharArray): Pair<VersionByte, ByteArray> {
        val bytes = ByteArray(encoded.size)
        for (i in encoded.indices) {
            if (encoded[i].toInt() > 127) {
                throw IllegalArgumentException("Illegal characters in encoded char array.")
            }
            bytes[i] = encoded[i].toByte()
        }

        val decoded = SecureBase32.decode(encoded)
        val decodedVersionByte = decoded[0]
        val payload = Arrays.copyOfRange(decoded, 0, decoded.size - 2)
        val data = Arrays.copyOfRange(payload, 1, payload.size)
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 2, decoded.size)

        val expectedChecksum = calculateChecksum(payload)

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