package org.tokend.wallet

import com.google.common.io.BaseEncoding
import org.junit.Assert
import org.junit.Test

class StrKeyTest {
    val SEED_ENCODED = "SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE"
    val ACCOUNT_ID_ENCODED = "GDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQDS3"
    val PRE_AUTH_TX_ENCODED = "TDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCR6AK"
    val SHA_256_ENCODED = "XDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQ2FT"
    val BYTES = BaseEncoding.base16().decode("D278C0BC3098842800043C83E39ABFF6581567A51607C94A1F0651B3F4BDAC28")
    val CHECKSUM = BaseEncoding.base16().decode("56BE")

    @Test
    fun encodeSeed() {
        val encoded = StrKey.encodeSecretSeed(BYTES)
        Assert.assertEquals(SEED_ENCODED, encoded)
    }

    @Test
    fun decodeSeed() {
        val decoded = StrKey.decodeSecretSeed(SEED_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeAccountId() {
        val encoded = StrKey.encodeAccountId(BYTES)
        Assert.assertEquals(ACCOUNT_ID_ENCODED, encoded)
    }

    @Test
    fun decodeAccountId() {
        val decoded = StrKey.decodeAccountId(ACCOUNT_ID_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodePreAuthTx() {
        val encoded = StrKey.encodePreAuthTx(BYTES)
        Assert.assertEquals(PRE_AUTH_TX_ENCODED, encoded)
    }

    @Test
    fun decodePreAuthTx() {
        val decoded = StrKey.decodePreAuthTx(PRE_AUTH_TX_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeSha256Hash() {
        val encoded = StrKey.encodeSha256Hash(BYTES)
        Assert.assertEquals(SHA_256_ENCODED, encoded)
    }

    @Test
    fun decodeSha256Hash() {
        val decoded = StrKey.decodeSha256Hash(SHA_256_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun decodeInvalidVersionByte() {
        try {
            StrKey.decodeAccountId(SEED_ENCODED)
            Assert.fail()
        } catch (e: StrKey.FormatException) {
        }
    }

    @Test
    fun calculateChecksum() {
        val checksum = StrKey.calculateChecksum(BYTES)
        Assert.assertArrayEquals(CHECKSUM, checksum)
    }
}