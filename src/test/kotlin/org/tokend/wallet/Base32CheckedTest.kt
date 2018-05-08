package org.tokend.wallet

import com.google.common.io.BaseEncoding
import org.junit.Assert
import org.junit.Test

class Base32CheckedTest {
    val SEED_ENCODED = "SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE"
    val ACCOUNT_ID_ENCODED = "GDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQDS3"
    val BALANCE_ID_ENCODED = "BDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQMUQ"
    val BYTES = BaseEncoding.base16().decode("D278C0BC3098842800043C83E39ABFF6581567A51607C94A1F0651B3F4BDAC28")
    val CHECKSUM = BaseEncoding.base16().decode("56BE")

    @Test
    fun encodeSeed() {
        val encoded = String(Base32Checked.encodeSecretSeed(BYTES))
        Assert.assertEquals(SEED_ENCODED, encoded)
    }

    @Test
    fun decodeSeed() {
        val decoded = Base32Checked.decodeSecretSeed(SEED_ENCODED.toCharArray())
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeAccountId() {
        val encoded = Base32Checked.encodeAccountId(BYTES)
        Assert.assertEquals(ACCOUNT_ID_ENCODED, encoded)
    }

    @Test
    fun decodeAccountId() {
        val decoded = Base32Checked.decodeAccountId(ACCOUNT_ID_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeBalanceId() {
        val encoded = Base32Checked.encodeBalanceId(BYTES)
        Assert.assertEquals(BALANCE_ID_ENCODED, encoded)
    }

    @Test
    fun decodeBalanceId() {
        val decoded = Base32Checked.decodeBalanceId(BALANCE_ID_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun decodeInvalidVersionByte() {
        try {
            Base32Checked.decodeAccountId(SEED_ENCODED)
            Assert.fail()
        } catch (e: Base32Checked.FormatException) {
        }
    }

    @Test
    fun calculateChecksum() {
        val checksum = Base32Checked.calculateChecksum(BYTES)
        Assert.assertArrayEquals(CHECKSUM, checksum)
    }
}