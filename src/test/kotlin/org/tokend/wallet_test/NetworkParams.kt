package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.NetworkParams
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.util.*

class NetworkParams {
    @Test
    fun amountConversion() {
        val precision = 6
        val amounts = listOf(
                BigDecimal("1.234567"),
                BigDecimal("18446744073709.551615"),
                BigDecimal.ONE,
                BigDecimal.TEN,
                BigDecimal.ZERO,
                BigDecimal("18446744073709.551")
        )
        val networkParams = NetworkParams("Test phrase", precision)

        amounts.forEach { amount ->
            Assert.assertEquals(
                    amount,
                    networkParams.amountFromPrecised(networkParams.amountToPrecised(amount))
            )
        }
    }

    @Test
    fun amountConversionZeroPrecision() {
        val networkParams = NetworkParams("Test phrase", 0)
        val amount = BigDecimal("18446744073709551615")

        Assert.assertEquals(
                amount,
                networkParams.amountFromPrecised(networkParams.amountToPrecised(amount))
        )

        val amountToCut = BigDecimal("184467440.777")
        val cutAmount = BigDecimal("184467440")

        Assert.assertEquals(
                cutAmount,
                networkParams.amountFromPrecised(networkParams.amountToPrecised(amountToCut))
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun amountConversionNegative() {
        NetworkParams("Test phrase", 0)
                .amountToPrecised(BigDecimal("-1"))
    }

    @Test(expected = IllegalStateException::class)
    fun amountConversionOverflow() {
        NetworkParams("Test phrase", 0)
                .amountToPrecised(BigDecimal("18446744073709551619"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun tooBigPrecision() {
        NetworkParams("Test phrase", 8)
    }

    @Test
    fun timeCorrection() {
        val correction = 60
        val networkParams = NetworkParams("Test phrase",
                NetworkParams.MAX_PRECISION, correction)

        val actual = Date().time / 1000
        val calculated = networkParams.nowTimestamp

        Assert.assertTrue((calculated - actual) in (correction - 1..correction + 1))
    }

    @Test
    fun serialization() {
        val networkParams = NetworkParams("Test phrase", 4, 42)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).writeObject(networkParams)
        val inputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        val pn = ObjectInputStream(inputStream).readObject() as NetworkParams

        Assert.assertEquals(networkParams.passphrase, pn.passphrase)
        Assert.assertEquals(networkParams.precision, pn.precision)
        Assert.assertEquals(networkParams.timeOffsetSeconds, pn.timeOffsetSeconds)
        Assert.assertArrayEquals(networkParams.networkId, pn.networkId)
    }
}