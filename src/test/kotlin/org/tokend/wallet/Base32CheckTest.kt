package org.tokend.wallet

import com.google.common.io.BaseEncoding
import org.junit.Assert
import org.junit.Test

class Base32CheckTest {
    val SEED_ENCODED = "SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE"
    val ACCOUNT_ID_ENCODED = "GDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQDS3"
    val BALANCE_ID_ENCODED = "BDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCQMUQ"
    val BYTES = BaseEncoding.base16().decode("D278C0BC3098842800043C83E39ABFF6581567A51607C94A1F0651B3F4BDAC28")

    @Test
    fun encodeSeed() {
        val encoded = String(Base32Check.encodeSecretSeed(BYTES))
        Assert.assertEquals(SEED_ENCODED, encoded)
    }

    @Test
    fun decodeSeed() {
        val decoded = Base32Check.decodeSecretSeed(SEED_ENCODED.toCharArray())
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeAccountId() {
        val encoded = Base32Check.encodeAccountId(BYTES)
        Assert.assertEquals(ACCOUNT_ID_ENCODED, encoded)
    }

    @Test
    fun decodeAccountId() {
        val decoded = Base32Check.decodeAccountId(ACCOUNT_ID_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun encodeBalanceId() {
        val encoded = Base32Check.encodeBalanceId(BYTES)
        Assert.assertEquals(BALANCE_ID_ENCODED, encoded)
    }

    @Test
    fun decodeBalanceId() {
        val decoded = Base32Check.decodeBalanceId(BALANCE_ID_ENCODED)
        Assert.assertArrayEquals(BYTES, decoded)
    }

    @Test
    fun decodeInvalidVersionByte() {
        try {
            Base32Check.decodeAccountId(SEED_ENCODED)
            Assert.fail()
        } catch (e: Base32Check.FormatException) {
        }
    }

    @Test
    fun testValidation() {
        Assert.assertTrue(Base32Check.isValid(Base32Check.VersionByte.ACCOUNT_ID, ACCOUNT_ID_ENCODED.toCharArray()))
        Assert.assertFalse(Base32Check.isValid(Base32Check.VersionByte.BALANCE_ID, ACCOUNT_ID_ENCODED.toCharArray()))
    }
}